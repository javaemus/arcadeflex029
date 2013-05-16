/*
This file is part of Arcadeflex.

Arcadeflex is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Arcadeflex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Arcadeflex.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * ported to v0.28
 * ported to v0.27
 *
 *   readtrakport is partially implemented
 *
 */

package mame;

import M6502.M6502H;
import static M6502.M6502H.*;
import static M6502.M6502.*;
import static I86.I86.*;
import static m6809.M6809H.*;
import static m6809.M6809.*;
import static Z80.Z80H.*;
import static Z80.Z80.*;
import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.mame.*;
import static arcadeflex.osdepend.*;
import m6809.M6809H;
import static mame.inptport.*;
import static mame.memoryH.*;
/***************************************************************************

  cpuintrf.c

  Don't you love MS-DOS 8+3 names? That stands for CPU interface.
  Functions needed to interface the CPU emulator with the other parts of
  the emulation.

***************************************************************************/
public class cpuintrf
{
     
      static int activecpu;
      static int totalcpu;
      static int iloops,iperiod;
      static int[] cpurunning = new int[MAX_CPU];
      static int[] totalcycles = new int[MAX_CPU];
      static int have_to_reset;
      
      static int have_24bit_address_space; 
      static int lookup_shift; 
      static int lookup_entries; 
      
      static MemoryReadAddress[] memoryread;
      static MemoryWriteAddress[] memorywrite;
      
      /* Lookup constants for CPUs using a 16-bit address space */
      static final int MH_SHIFT_16=    8;
      static final int MH_ENTRIES_16= (1<<(16-MH_SHIFT_16));

        /* Lookup constants for CPUs using a 24-bit address space */
      static final int MH_SHIFT_24= 10;
      static final int MH_ENTRIES_24=	(1<<(24-MH_SHIFT_24));


      static ReadHandlerPtr[] memoryreadhandler = new ReadHandlerPtr[MH_ENTRIES_24];
      static int[] memoryreadoffset = new int[MH_ENTRIES_24];
      static WriteHandlerPtr[] memorywritehandler = new WriteHandlerPtr[MH_ENTRIES_24];
      static int[] memorywriteoffset = new int[MH_ENTRIES_24];

      public static Object[] cpucontext = new Object[MAX_CPU];/* enough to accomodate the cpu status */
      public static char ramptr[][]=new char[MAX_CPU][];
      public static char romptr[][]=new char[MAX_CPU][];
      
      /* JB 970825 - Qix driver is a pain in the neck. Needs these new globals. */
       public static int yield_cpu;
       public static int saved_icount;

      public static class m6809context
      {
        public m6809_Regs regs = new m6809_Regs();
        public int icount;
        public int iperiod;
        public int irq;
      }

      public static class z80context
      {
        public Z80_Regs regs = new Z80_Regs();
        public int icount;
        public int iperiod;  
      }
      /***************************************************************************

          Input ports handling

        ***************************************************************************/
        





 
          /***************************************************************************

           Memory handling

         ***************************************************************************/
          public static ReadHandlerPtr mrh_error = new ReadHandlerPtr() {
            public int handler(int address) {
              if (errorlog != null) fprintf(errorlog, "CPU #%d PC %04x: warning - read unmapped memory address %04x\n", new Object[] { Integer.valueOf(activecpu), Integer.valueOf(cpu_getpc()), Integer.valueOf(address) });
              return RAM[address];
            }
          };
          public static ReadHandlerPtr mrh_ram = new ReadHandlerPtr() {
            public int handler(int address) {
              return RAM[address];
            }
          };

          public static ReadHandlerPtr mrh_nop = new ReadHandlerPtr() {
            public int handler(int address) {
              return 0;
            }
          };
            public static ReadHandlerPtr mrh_readmem = new ReadHandlerPtr()
          {
            public int handler(int address)
            {
              MemoryReadAddress[] mra = memoryread;
              int i = 0;
              while (mra[i].start != -1)
              {
                if ((address >= mra[i].start) && (address <= mra[i].end))
                {
                  int handler = mra[i].handler;

                  if (handler == MRA_NOP) return 0;
                     else if (handler == MRA_RAM || handler == MRA_ROM) return RAM[address];
                  return mra[i]._handler.handler(address - mra[i].start);
                }

                i++;
              }

              if (errorlog != null) fprintf(errorlog, "CPU #%d PC %04x: warning - read unmapped memory address %04x\n", new Object[] { Integer.valueOf(activecpu), Integer.valueOf(cpu_getpc()), Integer.valueOf(address) });
              return RAM[address];
            }
          };

          public static WriteHandlerPtr mwh_error = new WriteHandlerPtr() {
            public void handler(int address,int data) {
              if (errorlog != null) fprintf(errorlog, "CPU #%d PC %04x: warning - write %02x to unmapped memory address %04x\n", new Object[] { Integer.valueOf(activecpu), Integer.valueOf(cpu_getpc()), Integer.valueOf(data), Integer.valueOf(address) });
              RAM[address] = (char)data;
            }
          };
           public static WriteHandlerPtr mwh_ram = new WriteHandlerPtr() {
	       public void handler(int address,int data) {
	              RAM[address] = (char)data;
	       }
	  };
          public static WriteHandlerPtr mwh_ramrom = new WriteHandlerPtr()
          {
            public void handler(int address,int data)
            {
              char temp = (char)data;
              ROM[address] = temp;
              RAM[address] = temp;
            }
          };

          public static WriteHandlerPtr mwh_nop = new WriteHandlerPtr() {
            public void handler(int address, int data) {  } } ;


          public static WriteHandlerPtr mwh_writemem = new WriteHandlerPtr()
          {
            public void handler(int address, int data)
            {
              MemoryWriteAddress[] mwa = memorywrite;
              int i = 0;
              while (mwa[i].start != -1)
              {
                if ((address >= mwa[i].start) && (address <= mwa[i].end))
                {
                  int handler = mwa[i].handler;

                    if (handler == MWA_NOP) return;
                      else if (handler == MWA_RAM) RAM[address] = (char)data;
                      else if (handler == MWA_ROM)
                      {
                         if (errorlog != null) fprintf(errorlog, "CPU #%d PC %04x: warning - write %02x to ROM address %04x\n", new Object[] { Integer.valueOf(activecpu), Integer.valueOf(cpu_getpc()), Integer.valueOf(data), Integer.valueOf(address) });
                      }
                      else if (handler == MWA_RAMROM)
                      {
                             char temp = (char)data;
                             ROM[address] = temp;
                             RAM[address] = temp;
                      }
                      else {
                             mwa[i]._handler.handler(address - mwa[i].start, data);
                      }
                      return;
                 }

                i++;
              }

              if (errorlog != null) fprintf(errorlog, "CPU #%d PC %04x: warning - write %02x to unmapped memory address %04x\n", new Object[] { Integer.valueOf(activecpu), Integer.valueOf(cpu_getpc()), Integer.valueOf(data), Integer.valueOf(address) });
              RAM[address] = (char)data;
            }
          };

        static void initmemoryhandlers()
          {
            int i,s,e,a,b;

            memoryread = Machine.drv.cpu[activecpu].memory_read;
            memorywrite = Machine.drv.cpu[activecpu].memory_write;

            if (have_24bit_address_space!=0) {
		lookup_entries = MH_ENTRIES_24;
		lookup_shift = MH_SHIFT_24;
		}
            else {
                    lookup_entries = MH_ENTRIES_16;
                    lookup_shift = MH_SHIFT_16;
                    }

            for (int x = 0;x < lookup_entries;x++)
            {
              memoryreadhandler[x] = mrh_error;
              memoryreadoffset[x] = 0;

              memorywritehandler[x] = mwh_error;
              memorywriteoffset[x] = 0;
            }

            MemoryReadAddress[] mra = memoryread;
            int r = 0;
            while (mra[r].start != -1) r++;
            r--;

                /* go backwards because entries up in the memory array have greater priority than */
                /* the following ones. If an entry is duplicated, going backwards we overwrite */
                /* the handler set by the lower priority one. */
            while (r >= 0)//while (mra >= memoryread)
            {
              s = mra[r].start >> lookup_shift;
              a = mra[r].start != 0 ? (mra[r].start - 1 >> lookup_shift) + 1 : 0;
              b = (mra[r].end + 1 >> lookup_shift) - 1;
              e = mra[r].end >> lookup_shift;
                         
              /* first of all make all the entries point to the general purpose handler... */
              for (i = s; i <= e; i++)
              {
                memoryreadhandler[i] = mrh_readmem;
                memoryreadoffset[i] = 0;
              }
              /* ... and now make the ones containing only one handler point directly to the handler */
              for (i = a; i <= b; i++)
              {
                int handler = mra[r].handler;

                if (handler == MRA_NOP)
                {
                  memoryreadhandler[i] = mrh_nop;
                  memoryreadoffset[i] = 0;
                }
                else if (handler == MRA_RAM || handler == MRA_ROM)
                {
                  memoryreadhandler[i] =mrh_ram;
                  memoryreadoffset[i] = 0;
                }
                else
                {
                  memoryreadhandler[i] = mra[r]._handler;
                  memoryreadoffset[i] = mra[r].start;
                }
              }

              r--;
            }

            MemoryWriteAddress[] wra = memorywrite;
            int w = 0;
            while (wra[w].start != -1) w++;
            w--;
                /* go backwards because entries up in the memory array have greater priority than */
                /* the following ones. If an entry is duplicated, going backwards we overwrite */
                /* the handler set by the lower priority one. */
            while (w >= 0)//while (mwa >= memorywrite)
            {
              s = wra[w].start >> lookup_shift;
              a = wra[w].start != 0 ? (wra[w].start - 1 >> lookup_shift) + 1 : 0;
              b = (wra[w].end + 1 >> lookup_shift) - 1;
              e = wra[w].end >> lookup_shift;
              /* first of all make all the entries point to the general purpose handler... */
              for (i = s; i <= e; i++)
              {
                memorywritehandler[i] = mwh_writemem;
                memorywriteoffset[i] = 0;
              }
              /* ... and now make the ones containing only one handler point directly to the handler */
              for (i = a; i <= b; i++)
              {
               int  handler = wra[w].handler;

                if (handler == MWA_NOP)
                {
                  memorywritehandler[i] = mwh_nop;
                  memorywriteoffset[i] = 0;
                }
                else if (handler == MWA_RAM)
                {
                  memorywritehandler[i] = mwh_ram;
                  memorywriteoffset[i] = 0;
                }
                else if (handler == MWA_RAMROM)
                {
                  memorywritehandler[i] = mwh_ramrom;
                  memorywriteoffset[i] = 0;
                }
                else if (handler != MWA_ROM)
                {
                  memorywritehandler[i] = wra[w]._handler;
                  memorywriteoffset[i] = wra[w].start;
                }
              }

              w--;
            }
          }
        static void cpu_init()
        {
                /* count how many CPUs we have to emulate */
                totalcpu=0;
                have_24bit_address_space = 0; /* LBO 090597 */
                while(totalcpu<MAX_CPU)
                {
                  MemoryReadAddress[]  mra = Machine.drv.cpu[totalcpu].memory_read;
                  MemoryWriteAddress[] mwa = Machine.drv.cpu[totalcpu].memory_write;
                  if (Machine.drv.cpu[totalcpu].cpu_type == 0) break;
                  
                /* If we are using a 24-bit address space, we must use the larger mem lookup tables */
		if (Machine.drv.cpu[totalcpu].cpu_type == CPU_M68000)
			have_24bit_address_space = 1; /* LBO 090597 */

                  ramptr[totalcpu]=Machine.memory_region[Machine.drv.cpu[totalcpu].memory_region];

                /* opcode decryption is currently supported only for the first memory region */
                        if (totalcpu == 0) romptr[totalcpu] = ROM;
                        else romptr[totalcpu] = ramptr[totalcpu];

                    int i = 0;
                    while (mra[i].start != -1)
                    {
                             if (mra[i].base != null)
                                mra[i].base.set(ramptr[totalcpu], mra[i].start);

                             if (mra[i].size != null)
                                  mra[i].size[0] = ( mra[i].end -  mra[i].start + 1);
                       i++;
                    }

                    i = 0;
                    while (mwa[i].start != -1)
                    {
                         if (mwa[i].base != null)
                             mwa[i].base.set(ramptr[totalcpu], mwa[i].start);

                        if (mwa[i].size != null)
                            mwa[i].size[0] = (mwa[i].end - mwa[i].start + 1);
                      i++;
                    }

                    totalcpu += 1;

                }
         }
        public static void cpu_run()
        {
              int usres;

         reset:
        for(;;)
        {
            have_to_reset=0;
            for (activecpu = 0;activecpu < totalcpu;activecpu++)
            {
              if ((play_sound == 0) && ((Machine.drv.cpu[activecpu].cpu_type & CPU_AUDIO_CPU) != 0))
                cpurunning[activecpu] = 0;
              else {
                cpurunning[activecpu] = 1;
              }
              totalcycles[activecpu] = 0;
            }
            	/* do this AFTER the above so init_machine() can use cpu_halt() to hold the */
                /* execution of some CPUs */
                if (Machine.drv.init_machine!=null) Machine.drv.init_machine.handler();
                        
            for (activecpu = 0; activecpu < totalcpu; activecpu += 1)
            {
              int cycles;
              cycles = Machine.drv.cpu[activecpu].cpu_clock / (Machine.drv.frames_per_second * Machine.drv.cpu[activecpu].interrupts_per_frame);
              RAM = ramptr[activecpu];
              ROM = romptr[activecpu];
              initmemoryhandlers();

              switch (Machine.drv.cpu[activecpu].cpu_type & ~CPU_FLAGS_MASK)
              {
              case CPU_Z80:
              {
                Object ctxt;
                cpucontext[activecpu] = new z80context();
                ctxt = (z80context)cpucontext[activecpu];
                Z80_Reset();
                Z80_GetRegs(((z80context)ctxt).regs);
                ((z80context)ctxt).icount = cycles;
                ((z80context)ctxt).iperiod = cycles;
              }
                break;
              case CPU_M6502:
              {
                 Object ctxt;
                cpucontext[activecpu] = new M6502Regs();
                ctxt = (M6502Regs)cpucontext[activecpu];
                ((M6502Regs)ctxt).IPeriod = cycles;/* must be done before Reset6502() */
                 Reset6502((M6502Regs)ctxt);
              }
                break;
              case CPU_I86:
                I86_Reset(RAM, cycles);
                break;
              case CPU_M6809:
                 {
                     Object ctxt;
                     cpucontext[activecpu] = new m6809context();
                     ctxt = (m6809context)cpucontext[activecpu];
                     m6809_IPeriod = cycles;
                     m6809_reset();
                     m6809_GetRegs(((m6809context)ctxt).regs);
                    ((m6809context)ctxt).icount = cycles;
                    ((m6809context)ctxt).iperiod = cycles;
                    ((m6809context)ctxt).irq = M6809H.INT_NONE;
                }
                break;
             case CPU_M68000:
		{
		    //MC68000_reset(cycles);/TODO!
		}
		break;
             }

            }
            do
            {
               update_input_ports();	/* read keyboard & update the status of the input ports */
              for (activecpu = 0; activecpu < totalcpu; activecpu += 1)
              {
                  if (have_to_reset!=0) continue reset;	/* machine_reset() was called, have to reset */
                  if (cpurunning[activecpu] !=0)
                  {

                    RAM = ramptr[activecpu];
                    ROM = romptr[activecpu];
                    initmemoryhandlers();

                    switch (Machine.drv.cpu[activecpu].cpu_type & ~CPU_FLAGS_MASK)
                    {
                      case CPU_Z80:
                      {
                         Object ctxt;
                         ctxt = (z80context)cpucontext[activecpu];

                         Z80_SetRegs(((z80context)ctxt).regs);
                         Z80_ICount = ((z80context)ctxt).icount;
                         iperiod = ((z80context)ctxt).iperiod;
                         
                         for (iloops = Machine.drv.cpu[activecpu].interrupts_per_frame - 1;iloops >= 0;iloops--){
                             	/* TODO: keep track of differences between the requested number */
				/* of cycles and the actual cycles executed. */
				totalcycles[activecpu] += Z80_Execute(iperiod);
				cpu_cause_interrupt(activecpu,cpu_interrupt());
                         }
                         Z80_GetRegs(((z80context)ctxt).regs);
                        ((z80context)ctxt).icount = Z80_ICount; 
                       }
                       break;
                       case CPU_M6502:
                       {
                           Object ctxt;
                           ctxt = (M6502Regs)cpucontext[activecpu];
                           iperiod = ((M6502Regs)ctxt).IPeriod;
                             for (iloops = Machine.drv.cpu[activecpu].interrupts_per_frame - 1; iloops >= 0;iloops--)
                             {
                                 /* TODO: keep track of differences between the requested number */
				/* of cycles and the actual cycles executed. */
				totalcycles[activecpu] += Run6502((M6502Regs)cpucontext[activecpu],iperiod);
				cpu_cause_interrupt(activecpu,cpu_interrupt());
                             }
                                 
                       }
                       break;
                       case CPU_I86:
                       {
        /* TODO: retrieve iperiod from the CPU context (needed by cpu_getfcount()) */
                        for (iloops = Machine.drv.cpu[activecpu].interrupts_per_frame - 1;iloops >= 0;iloops--)
                                          I86_Execute();
                                           totalcycles[activecpu] += iperiod;
                       }
                       break;
                       case CPU_M6809:
                       {
                           Object ctxt;
                           ctxt = (m6809context)cpucontext[activecpu];
                           m6809_SetRegs(((m6809context)ctxt).regs);
                           m6809_ICount = ((m6809context)ctxt).icount;
                           m6809_IPeriod = ((m6809context)ctxt).iperiod;
                           m6809_IRequest = ((m6809context)ctxt).irq;
                                for (iloops = Machine.drv.cpu[activecpu].interrupts_per_frame - 1; iloops >= 0;iloops--)
                                {
                                    m6809_execute();
                                    totalcycles[activecpu] += iperiod;
                                }
                                    

                            m6809_GetRegs(((m6809context)ctxt).regs);
                            ((m6809context)ctxt).icount = m6809_ICount;
                            ((m6809context)ctxt).iperiod = m6809_IPeriod;
                            ((m6809context)ctxt).irq = m6809_IRequest;
                       }
                       break;
                       case CPU_M68000:
		           {
				for (iloops = Machine.drv.cpu[activecpu].interrupts_per_frame - 1; iloops >= 0;iloops--)
				{
					//MC68000_Execute();//TODO
					totalcycles[activecpu] += iperiod;
				}
			  }
			  break;
                    }//end of switch
                    /* keep track of changes to RAM and ROM pointers (bank switching) */
                    ramptr[activecpu] = RAM;
                    romptr[activecpu] = ROM;
                    /* increase the total cycles counter */
                    totalcycles[activecpu] += Machine.drv.cpu[activecpu].cpu_clock / Machine.drv.frames_per_second;
                  }//end of if
                }//end of for
                /* JB 970825 - qix driver needs to yield a cpu without causing an update */
		if (yield_cpu!=0)
		{
			usres = 0;
			yield_cpu = 0;
		}
		else
		{
			usres = updatescreen();
		}
                } while (usres == 0);
                break;
            }
         }
        /***************************************************************************

          This function resets the machine (the reset will not take place
          immediately, it will be performed at the end of the active CPU's time
          slice)

        ***************************************************************************/
         public static void machine_reset()
        {
          
            /* write hi scores to disk */
            if ((hiscoreloaded != 0) && (Machine.gamedrv.hiscore_save != null)) {
                Machine.gamedrv.hiscore_save.handler();
            }
            hiscoreloaded = 0;
            have_to_reset = 1;
            
        }
        /***************************************************************************

          Use this function to stop and restart CPUs

        ***************************************************************************/
        public static void cpu_halt(int cpunum,int running)
        {
                if (cpunum >= MAX_CPU) return;

                cpurunning[cpunum] = running;
        }



        /***************************************************************************

          This function returns CPUNUM current status  (running or halted)

        ***************************************************************************/
        public static int cpu_getstatus(int cpunum)
        {
                if (cpunum >= MAX_CPU) return 0;

                return cpurunning[cpunum];
        }


         public static int cpu_getpc()
         {
                        switch(Machine.drv.cpu[activecpu].cpu_type & ~CPU_FLAGS_MASK)
                        {
                        case CPU_Z80:
                                return Z80_GetPC();

                        case CPU_M6502:
                                return ((M6502Regs) cpucontext[activecpu]).PC.W;
                        case CPU_M6809:
                                return m6809_GetPC();

                        default:
                                if (errorlog != null) fprintf(errorlog, "cpu_getpc: unsupported CPU type %02x\n", Machine.drv.cpu[activecpu].cpu_type);
                                return -1;
                        }
         }



        /***************************************************************************

          This is similar to cpu_getpc(), but instead of returning the current PC,
          it returns the address of the opcode that is doing the read/write. The PC
          has already been incremented by some unknown amount by the time the actual
          read or write is being executed. This helps to figure out what opcode is
          actually doing the reading or writing, and therefore the amount of cycles
          it's taking. The Missile Command driver needs to know this.

        ***************************************************************************/
        public static int cpu_getpreviouspc()
        {
                switch(Machine.drv.cpu[activecpu].cpu_type & ~CPU_FLAGS_MASK)
                {
                        case CPU_M6502:
                                return ((M6502Regs)cpucontext[activecpu]).previousPC.W;
                        default:
                            if (errorlog != null) fprintf(errorlog, "cpu_getpreviouspc: unsupported CPU type %02x\n", new Object[] { Integer.valueOf(Machine.drv.cpu[activecpu].cpu_type) });
                                return -1;
                }
            
        }


        /***************************************************************************

          This is similar to cpu_getpc(), but instead of returning the current PC,
          it returns the address stored on the top of the stack, which usually is
          the address where execution will resume after the current subroutine.
          Note that the returned value will be wrong if the program has PUSHed
          registers on the stack.

        ***************************************************************************/
        public static int cpu_getreturnpc()
        {
                switch(Machine.drv.cpu[activecpu].cpu_type & ~CPU_FLAGS_MASK)
                {
                        case CPU_Z80:
                                {
                                    Z80_Regs regs = new Z80_Regs();
                                    Z80_GetRegs(regs);
                                    return RAM[regs.SP] + (RAM[(regs.SP + 1)] << 8);

                                }
                        default:
                if (errorlog != null) arcadeflex.libc.fprintf(errorlog, "cpu_getreturnpc: unsupported CPU type %02x\n", new Object[] { Integer.valueOf(Machine.drv.cpu[activecpu].cpu_type) });
                        return -1;

                }
        }



        /***************************************************************************

          Returns the number of CPU cycles since the last reset of the CPU

          IMPORTANT: this value wraps around in a relatively short time.
          For example, for a 6Mhz CPU, it will wrap around in
          2^32/6000000 = 716 seconds = 12 minutes.
          Make sure you don't do comparisons between values returned by this
          function, but only use the difference (which will be correct regardless
          of wraparound).

        ***************************************************************************/
        public static int cpu_gettotalcycles()
        {
                return totalcycles[activecpu] + iperiod - cpu_geticount();
        }



        /***************************************************************************

          Returns the number of CPU cycles before the next interrupt handler call

        ***************************************************************************/
        public static int cpu_geticount()
        {
                switch(Machine.drv.cpu[activecpu].cpu_type & ~CPU_FLAGS_MASK)
                {
                        case CPU_Z80:
                                return Z80_ICount;
                        case CPU_M6502:
                                return ((M6502Regs)cpucontext[activecpu]).ICount;
                        case CPU_M6809:
                                return m6809_ICount;
                        default:
                if (errorlog != null) arcadeflex.libc.fprintf(errorlog, "cpu_geticount: unsupported CPU type %02x\n", new Object[] { Integer.valueOf(Machine.drv.cpu[activecpu].cpu_type) });
                                return -1;
                }
        }



        /***************************************************************************

          Returns the number of CPU cycles before the end of the current video frame

        ***************************************************************************/
        public static int cpu_getfcount()
        {
                switch(Machine.drv.cpu[activecpu].cpu_type & ~CPU_FLAGS_MASK)
                {
                        case CPU_Z80:
                                return Z80_ICount + iloops * iperiod;
                        case CPU_M6502:
                                return ((M6502Regs)cpucontext[activecpu]).ICount + iloops * iperiod;
                        case CPU_M6809:
                                return m6809_ICount + iloops * iperiod;
                        default:
                if (errorlog != null) fprintf(errorlog, "cpu_getfcount: unsupported CPU type %02x\n", new Object[] { Integer.valueOf(Machine.drv.cpu[activecpu].cpu_type) });
                                return -1;

                }
        }
        /***************************************************************************

          Returns the number of CPU cycles in one video frame

        ***************************************************************************/
        public static int cpu_getfperiod()
        { 
                return Machine.drv.cpu[activecpu].cpu_clock / Machine.drv.frames_per_second;
        }
        public static void cpu_seticount(int cycles)
        {
                switch(Machine.drv.cpu[activecpu].cpu_type & ~CPU_FLAGS_MASK)
                {
                        case CPU_Z80:
                                Z80_ICount = cycles;
                                break;
                       case CPU_M6502:
                                ((M6502Regs)cpucontext[activecpu]).ICount = cycles;
                                break;
                        case CPU_M6809:
                                m6809_ICount = cycles;
                                break;
                        default:
                         if (errorlog == null) break; arcadeflex.libc.fprintf(errorlog, "cpu_seticount: unsupported CPU type %02x\n", new Object[] { Integer.valueOf(Machine.drv.cpu[activecpu].cpu_type) });
                        break;
                }
        }

        /***************************************************************************

          Returns the number of times the interrupt handler will be called before
          the end of the current video frame. This is can be useful to interrupt
          handlers to synchronize their operation. If you call this from outside
          an interrupt handler, add 1 to the result, i.e. if it returns 0, it means
          that the interrupt handler will be called once.

        ***************************************************************************/
        public static int cpu_getiloops()
        {
                return iloops;
        }
        /***************************************************************************

          Interrupt handling

        ***************************************************************************/
        /***************************************************************************

          Use this function to cause an interrupt immediately (don't have to wait
          until the next call to the interrupt handler)

        ***************************************************************************/
  
        public static void cpu_cause_interrupt(int cpu,int type)
        {
                switch(Machine.drv.cpu[cpu].cpu_type & ~CPU_FLAGS_MASK)
                {
                        case CPU_Z80:
                                if (cpu == activecpu)
                                        Z80_Cause_Interrupt(type);
                                else
                                {
                                        Object ctxt;
                                        Z80_Regs regs = new Z80_Regs();


                                        cpucontext[cpu] = new z80context();
                                        ctxt = (z80context)cpucontext[cpu];

                                        Z80_GetRegs(regs);
                                        Z80_SetRegs(((z80context)ctxt).regs);
                                        Z80_Cause_Interrupt(type);
                                        Z80_GetRegs(((z80context)ctxt).regs);
                                        Z80_SetRegs(regs);
                                }
                                break;

                        case CPU_M6502:
                                {
                                    Object ctxt;
                                    ctxt = (M6502Regs)cpucontext[cpu];
                                     M6502_Cause_Interrupt((M6502Regs)ctxt,type);
                                }
                                break;

                        default:
                            if (errorlog!=null) fprintf(errorlog,"cpu_cause_interrupt: unsupported CPU type %02x\n",Machine.drv.cpu[activecpu].cpu_type);
                                break;
                }
        }


                         
        public static void cpu_clear_pending_interrupts(int cpu)
        {
                switch(Machine.drv.cpu[activecpu].cpu_type & ~CPU_FLAGS_MASK)
                {
                        case CPU_Z80:
                                if (cpu == activecpu)
                                        Z80_Clear_Pending_Interrupts();
                                else
                                {
                                       Object ctxt;
                                        Z80_Regs regs = new Z80_Regs();
                                        cpucontext[cpu] = new z80context();
                                        ctxt = (z80context)cpucontext[cpu];
                                        Z80_GetRegs(regs);
                                        Z80_SetRegs(((z80context)ctxt).regs);
                                        Z80_Clear_Pending_Interrupts();
                                        Z80_GetRegs(((z80context)ctxt).regs);
                                        Z80_SetRegs(regs);
                                }
                                break;

                        case CPU_M6502:
                                {
                                      Object ctxt;
                                      ctxt = (M6502Regs)cpucontext[cpu];
                                      M6502_Clear_Pending_Interrupts((M6502Regs)ctxt);
                                }
                                break;

                        default:
                             if (errorlog!=null) fprintf(errorlog,"clear_pending_interrupts: unsupported CPU type %02x\n",Machine.drv.cpu[activecpu].cpu_type);
                                break;
                }
        }
        public static int Z80_IRQ;	/* needed by the CPU emulation */


        /* start with interrupts enabled, so the generic routine will work even if */
        /* the machine doesn't have an interrupt enable port */
        static int interrupt_enable = 1;
        static int interrupt_vector = 0xff;

                public static WriteHandlerPtr interrupt_enable_w = new WriteHandlerPtr() { public void handler(int offset, int data)
                {
                        interrupt_enable = data;

                        /* make sure there are no queued interrupts */
                        if (data == 0) cpu_clear_pending_interrupts(activecpu);
                } };



                public static WriteHandlerPtr interrupt_vector_w = new WriteHandlerPtr() { public void handler(int offset, int data)
                {
                    if (interrupt_vector != data)
                    {
                            interrupt_vector = data;

                            /* make sure there are no queued interrupts */
                            cpu_clear_pending_interrupts(activecpu);
                    }
                } };

                public static InterruptPtr interrupt = new InterruptPtr() { public int handler()
                {
                        switch(Machine.drv.cpu[activecpu].cpu_type & ~CPU_FLAGS_MASK)
                        {
                        case CPU_Z80:
                                if (interrupt_enable == 0) return Z80_IGNORE_INT;
                                else return interrupt_vector;

                        case CPU_M6502:
                                if (interrupt_enable == 0) return M6502H.INT_NONE;
                                else return M6502H.INT_IRQ;
                       case CPU_M6809:
                                if (interrupt_enable == 0) return M6809H.INT_NONE;
                                else return M6809H.INT_IRQ;
                        default:
                                if (errorlog != null) fprintf(errorlog, "interrupt: unsupported CPU type %02x\n", Machine.drv.cpu[activecpu].cpu_type);
                                return -1;
                        }
                } };

                public static InterruptPtr nmi_interrupt = new InterruptPtr() { public int handler()
                {
                        switch(Machine.drv.cpu[activecpu].cpu_type & ~CPU_FLAGS_MASK)
                        {
                        case CPU_Z80:
                                if (interrupt_enable == 0) return Z80_IGNORE_INT;
                                else return Z80_NMI_INT;

                        case CPU_M6502:
                                if (interrupt_enable == 0) return M6502H.INT_NONE;
                                else return M6502H.INT_NMI;

                        default:
                                if (errorlog != null) fprintf(errorlog, "nmi_interrupt: unsupported CPU type %02x\n", Machine.drv.cpu[activecpu].cpu_type);
                                return -1;
                        }
                } };



        public static InterruptPtr ignore_interrupt = new InterruptPtr() {
            public int handler() {
                switch(Machine.drv.cpu[activecpu].cpu_type & ~CPU_FLAGS_MASK)
                {
                        case CPU_Z80:
                                return Z80_IGNORE_INT;

                        case CPU_M6502:
                                return M6502H.INT_NONE;
                        case CPU_M6809:
                                return M6809H.INT_NONE;
                        default:
                        if (errorlog != null) fprintf(errorlog, "interrupt: unsupported CPU type %02x\n", Machine.drv.cpu[activecpu].cpu_type);
                                return -1;
                }
        }};
        /***************************************************************************

          Perform a memory read. This function is called by the CPU emulation.

        ***************************************************************************/
        public static int cpu_readmem(int address)
        {
            int i = address>> lookup_shift;
            return memoryreadhandler[i].handler(address - memoryreadoffset[i]);
        }



        /***************************************************************************

          Perform a memory write. This function is called by the CPU emulation.

        ***************************************************************************/
        public static void cpu_writemem(int address,int data)
        {
            int i = address >> lookup_shift;
            memorywritehandler[i].handler(address - memorywriteoffset[i], data);
        }



        /***************************************************************************

          Perform an I/O port read. This function is called by the CPU emulation.

        ***************************************************************************/
        public static int cpu_readport(int Port)
        {
            IOReadPort[] iorp = Machine.drv.cpu[activecpu].port_read;
            if (iorp != null)
            {
              int i = 0;
              while (iorp[i].start != -1)
              {
                if ((Port >= iorp[i].start) && (Port <= iorp[i].end))
                {
                  int j = iorp[i].handler;

                  if (j == IOWP_NOP) return 0;
                  return iorp[i]._handler.handler(Port - iorp[i].start);
                }

                i++;
              }
            }

            if (errorlog != null) fprintf(errorlog, "CPU #%d PC %04x: warning - read unmapped I/O port %02x\n", new Object[] { Integer.valueOf(activecpu), Integer.valueOf(cpu_getpc()), Integer.valueOf(Port) });
            return 0;
        }



        /***************************************************************************

          Perform an I/O port write. This function is called by the CPU emulation.

        ***************************************************************************/
        public static void cpu_writeport(int Port,int Value)
        {
            IOWritePort[] iowp = Machine.drv.cpu[activecpu].port_write;
            int i=0;
            if (iowp != null)
            {
              while (iowp[i].start != -1)
              {
                if ((Port >= iowp[i].start) && (Port <= iowp[i].end))
                {
                  int j = iowp[i].handler;

                  if (j == IOWP_NOP) return;
                  iowp[i]._handler.handler(Port - iowp[i].start, Value);

                  return;
                }

                i++;
              }
            }
            if (errorlog != null) arcadeflex.libc.fprintf(errorlog, "CPU #%d PC %04x: warning - write %02x to unmapped I/O port %02x\n", new Object[] { Integer.valueOf(activecpu), Integer.valueOf(cpu_getpc()), Integer.valueOf(Value), Integer.valueOf(Port) });
         }



	/***************************************************************************

  	Interrupt handler. This function is called at regular intervals
  	(determined by IPeriod) by the CPU emulation.

	***************************************************************************/

	public static int cpu_interrupt()
	{
		return Machine.drv.cpu[activecpu].interrupt.handler();
	}


	public static void Z80_Patch (Z80_Regs Regs)
	{
	}



	public static void Z80_Reti ()
	{
	}



	public static void Z80_Retn ()
	{
	}
}
