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

package machine;

import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.cpuintrf.*;
import static mame.mameH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static Z80.Z80H.*;
import static Z80.Z80.*;
import static mame.osdependH.*;
import static machine.z80fmly.*;
import static machine.z80fmlyH.*;
import static vidhrdw.mcr3.*;

public class mcr {
    
        public static int mcr_loadnvram;
        static int spyhunt_mux;

        static char[] soundlatch=new char[4];
        static char soundstatus;

        static int vblholdcycles;

        static int watchdogcount = 0;
        static int watchdogmax;
        static int watchdogon;

        /* z80 ctc */
        static Z80CTC ctc=new Z80CTC();


        /***************************************************************************

          Generic MCR handlers

        ***************************************************************************/
        public static InitMachinePtr mcr_init_machine = new InitMachinePtr() {	public void handler()
        {
           int i;

                /* reset the sound */
           for (i = 0; i < 4; i++)
              soundlatch[i] = 0;
           soundstatus = 0;

           /* compute the duration of the VBL for the CTC -- assume it's is about 1/12th of the frame time */
           vblholdcycles = Machine.drv.cpu[0].cpu_clock / Machine.drv.frames_per_second / 12;
           z80ctc_reset (ctc, Machine.drv.cpu[0].cpu_clock);

           /* can't load NVRAM right away */
           mcr_loadnvram = 0;

           /* set up the watchdog */
           watchdogon = 1;
           watchdogcount = 0;
           watchdogmax = Machine.drv.frames_per_second * Machine.drv.cpu[0].interrupts_per_frame;
        }};
        public static InitMachinePtr mcr_init_machine_no_watchdog = new InitMachinePtr() {	public void handler()
        {
                mcr_init_machine.handler();
                watchdogon = 0;
                mcr_loadnvram = 1;
        }};

        public static InterruptPtr mcr_interrupt = new InterruptPtr() {
            public int handler() {
           int irq;

                /* watchdog time? */
                if (watchdogon!=0 && ++watchdogcount > watchdogmax)
                {
                        machine_reset ();
                        watchdogcount = 0;
                        return ignore_interrupt.handler();
                }

           /* clock the external clock at 30Hz, but update more frequently */
           z80ctc_update (ctc, 0, 0, 0);
           z80ctc_update (ctc, 1, 0, 0);
           z80ctc_update (ctc, 2, 0, 0);
           z80ctc_update (ctc, 3, (cpu_getiloops () == 0) ? 1 : 0, vblholdcycles);

           /* handle any pending interrupts */
           irq = z80ctc_irq_r (ctc);
           if (irq != Z80_IGNORE_INT)
              if (errorlog!=null)
                                fprintf (errorlog, "  (Interrupt from ch. %d)\n", (irq - ctc.vector) / 2);

           return irq;
        }};

        public static WriteHandlerPtr mcr_writeport = new WriteHandlerPtr() {
            public void handler(int port, int value) {

                switch (port)
                {
                        case 0:	/* OP0  Write latch OP0 (coin meters, 2 led's and cocktail 'flip') */
                           if (errorlog!=null)
                              fprintf (errorlog, "mcr write to OP0 = %02d\n", value);
                                return;

                        case 4:	/* Write latch OP4 */
                           if (errorlog!=null)
                              fprintf (errorlog, "mcr write to OP4 = %02d\n", value);
                                return;

                        case 0x1c:	/* WIRAM0 - write audio latch 0 */
                        case 0x1d:	/* WIRAM0 - write audio latch 1 */
                        case 0x1e:	/* WIRAM0 - write audio latch 2 */
                        case 0x1f:	/* WIRAM0 - write audio latch 3 */
                                soundlatch[port - 0x1c] = (char)value;
                                return;

                        case 0xe0:	/* clear watchdog timer */
                                watchdogcount = 0;
                                return;

                        case 0xe8:
                                /* A sequence of values gets written here at startup; we don't know why;
                                   However, it does give us the opportunity to tweak the IX register before
                                   it's checked in Tapper and Timber, thus eliminating the need for patches
                                   The value 5 is written last; we key on that, and only modify IX if it is
                                   currently 0; hopefully this is 99.9999% safe :-) */
                                if (value == 5)
                                {
                                    Z80_Regs temp=new Z80_Regs();
                                    Z80_GetRegs(temp);
                                    if(temp.IX.W==0)
                                        temp.IX.W+=1;
                                    Z80_SetRegs(temp);
                                      /*  Z80_Regs temp;
                                        Z80_GetRegs (&temp);
                                        if (temp.IX.D == 0)
                                                temp.IX.D += 1;
                                        Z80_SetRegs (&temp);*/
                                }
                                return;

                        case 0xf0:	/* These are the ports of a Z80-CTC; it generates interrupts in mode 2 */
                        case 0xf1:
                        case 0xf2:
                        case 0xf3:
                          z80ctc_w (ctc, port - 0xf0, value);
                      return;
                }

                /* log all writes that end up here */
           if (errorlog!=null)
              fprintf (errorlog, "mcr unknown write port %02x %02d\n", port, value);
        }};

        public static ReadHandlerPtr mcr_readport = new ReadHandlerPtr() { public int handler(int port)
	{

                /* ports 0-4 are read directly via the input ports structure */
                port += 5;

           /* only a few ports here */
           switch (port)
           {
                        case 0x07:	/* Read audio status register */

                                /* once the system starts checking the sound, memory tests are done; load the NVRAM */
                           mcr_loadnvram = 1;
                                return soundstatus;

                        case 0x10:	/* Tron reads this as an alias to port 0 -- does this apply to all ports 10-14? */
                                return cpu_readport (port & 0x0f);
           }

                /* log all reads that end up here */
           if (errorlog!=null)
              fprintf (errorlog, "reading port %i (PC=%04X)\n", port, cpu_getpc ());
                return 0;
        }};

        public static WriteHandlerPtr mcr_soundstatus_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {

           soundstatus = (char)data;
        }};

        public static ReadHandlerPtr mcr_soundlatch_r = new ReadHandlerPtr() { public int handler(int offset)
	{
           return soundlatch[offset];
        }};


        /***************************************************************************

          Game-specific port handlers

        ***************************************************************************/
        public static WriteHandlerPtr spyhunt_writeport = new WriteHandlerPtr() {
            public void handler(int port, int value) {

                switch (port)
                {
                        case 0x04:
                           spyhunt_mux = value;
                           break;

                        case 0x84:
                                spyhunt_scrollx = (spyhunt_scrollx & ~0xff) | value;
                                break;

                        case 0x85:
                                spyhunt_scrollx = (spyhunt_scrollx & 0xff) | ((value & 0x07) << 8);
                                spyhunt_scrolly = (spyhunt_scrolly & 0xff) | ((value & 0x80) << 1);
                                break;

                        case 0x86:
                                spyhunt_scrolly = (spyhunt_scrolly & ~0xff) | value;
                                break;

                        default:
                                mcr_writeport.handler(port,value);
                                break;
                }
        }};


        /***************************************************************************

          Game-specific input handlers

        ***************************************************************************/
        /* Spy Hunter -- multiplexed steering wheel/gas pedal */
        static int spy_val = 0x30;
        public static ReadHandlerPtr spyhunt_port_r= new ReadHandlerPtr() { public int handler(int offset)
	{
                int port = readinputport (6);

                /* mux high bit on means return steering wheel */
                if ((spyhunt_mux & 0x80)!=0)
                {
                        if ((port & 8)!=0)
                                return 0x94;
                        else if ((port & 4)!=0)
                                return 0x54;
                        else
                                return 0x74;
                }

                /* mux high bit off means return gas pedal */
                else
                {
                        
                        if ((port & 1)!=0)
                        {
                                spy_val += 4;
                                if (spy_val > 0xff) spy_val = 0xff;
                        }
                        else if ((port & 2)!=0)
                        {
                                spy_val -= 4;
                                if (spy_val < 0x30) spy_val = 0x30;
                        }
                        return spy_val;
                }
        }};


        /* Destruction Derby -- 6 bits of steering plus 2 bits of normal inputs */
        public static ReadHandlerPtr destderb_port_r= new ReadHandlerPtr() { public int handler(int offset)
	{
                return readinputport (1 + offset) | (readtrakport (offset) & 0xfc);
        }};


        /* Kick -- trackball reader */
        static char kick_x;
        public static ReadHandlerPtr kick_trakball_r = new ReadHandlerPtr() { public int handler(int offset)
	{
                

                int trak_x = readtrakport (0);
                int val = readinputport (6);

                if ((val & 0x02)!=0)		/* left */
                        kick_x += 2;
                if ((val & 0x01)!=0)		/* right */
                        kick_x -= 2;

                /* JB 970828 */
                if (trak_x != NO_TRAK)
                        kick_x -= trak_x;

                return kick_x;
        }};

        /* Kick -- trackball handler */
        public static ConversionPtr kick_trakball_x = new ConversionPtr() { public int handler(int data)
	{
                if (data < -5)
                        data = -5;
                else if (data > 5)
                        data = 5;
                return data;
        }};


        /* Wacko -- trackball readers */
        static char wacko_x;
        public static ReadHandlerPtr wacko_trakball_x_r = new ReadHandlerPtr() { public int handler(int offset)
	{

                int trak_x = readtrakport (0);
                int val = readinputport (6);

                if ((val & 0x02)!=0)		/* left */
                        wacko_x += 2;
                if ((val & 0x01)!=0)		/* right */
                        wacko_x -= 2;

                /* JB 970828 */
               if (trak_x != NO_TRAK)
                        wacko_x -= trak_x;

                return wacko_x;
        }};
        static char wacko_y;
        public static ReadHandlerPtr wacko_trakball_y_r = new ReadHandlerPtr() { public int handler(int offset)
	{

                int trak_y = readtrakport (1);
                int val = readinputport (6);

                if ((val & 0x08)!=0)		/* left */
                        wacko_y += 2;
                if ((val & 0x04)!=0)		/* right */
                        wacko_y -= 2;

                /* JB 970828 */
                if (trak_y != NO_TRAK)
                        wacko_y -= trak_y;

                return wacko_y;
        }};


        /* Wacko -- trackball handler */
        public static ConversionPtr wacko_trakball_xy = new ConversionPtr() { public int handler(int data)
	{
                if (data < -8)
                        data = -8;
                else if (data > 8)
                        data = 8;
                return data;
        }};


        /* Kozmik Krooz'r -- dial reader */
        public static ReadHandlerPtr kroozr_dial_r= new ReadHandlerPtr() { public int handler(int offset)
	{
                int dial = readinputport (7);
                int val = readinputport (1);

                val |= (dial & 0x80) >> 1;
                val |= (dial & 0x70) >> 4;

                return val;
        }};


        /* Kozmik Krooz'r -- joystick readers */
        public static ReadHandlerPtr kroozr_trakball_x_r= new ReadHandlerPtr() { public int handler(int data)
	{
                int val = readinputport (6);

                if ((val & 0x02)!=0)		/* left */
                        return 0x64 - 0x34;
                if ((val & 0x01)!=0)		/* right */
                        return 0x64 + 0x34;
                return 0x64;
        }};
        public static ReadHandlerPtr kroozr_trakball_y_r= new ReadHandlerPtr() { public int handler(int data)
	{
                int val = readinputport (6);

                if ((val & 0x08)!=0)		/* up */
                       return 0x64 - 0x34;
                if ((val & 0x04)!=0)		/* down */
                        return 0x64 + 0x34;
                return 0x64;
        }};
    
}
