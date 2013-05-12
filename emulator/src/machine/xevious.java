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
 *
 * rewrote for v0.28
 *
 *
 *
 */
package machine;

import static arcadeflex.libc.*;
import static arcadeflex.osdepend.*;
import static mame.driverH.*;
import static mame.common.*;
import static mame.commonH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static mame.cpuintrf.*;
import static vidhrdw.generic.*;
import static Z80.Z80H.*;
import static Z80.Z80.*;

public class xevious {
    public static CharPtr xevious_sharedram = new CharPtr();
    public static CharPtr xevious_vlatches = new CharPtr();
    static int interrupt_enable_1;
    static int interrupt_enable_2;
    static int interrupt_enable_3;
    /* static int    HiScore; */

    public static CharPtr rom2a= new CharPtr();
    public static CharPtr rom2b= new CharPtr();
    public static CharPtr rom2c= new CharPtr();
    static int[] xevious_bs= new int[2];


    static char namco_key[] =
    /*  LDRU,LDR,LDU,LD ,LRU,LR ,LU , L ,DRU,DR ,DU , D ,RU , R , U ,NON  */
      {   5 , 5 , 5 , 5 , 7 , 6 , 7 , 6 , 3 , 3 , 4 , 4 , 1 , 2 , 0 , 8 };

      public static InitMachinePtr xevious_init_machine = new InitMachinePtr() {	public void handler()
	{
            /* halt the slave CPUs until they're reset */
            cpu_halt(1,0);
            cpu_halt(2,0);

            Machine.memory_region[0][0x8c00] = 1;
            Machine.memory_region[0][0x8c01] = 1;

            rom2a.set(Machine.memory_region[4],0);
            rom2b.set(Machine.memory_region[4],0x1000);
            rom2c.set(Machine.memory_region[4],0x3000);


    }};

    /* emulation for schematic 9B */
    public static WriteHandlerPtr xevious_bs_w = new WriteHandlerPtr() { public void handler(int offset, int data)
   {
            xevious_bs[offset & 0x01] = data;
    }};

    public static ReadHandlerPtr  xevious_bb_r= new ReadHandlerPtr() { public int handler(int offset)
    {
            int adr_2b,adr_2c;
            int dat1,dat2;


            /* get BS to 12 bit data from 2A,2B */
            adr_2b = ((xevious_bs[1]&0x7e)<<6)|((xevious_bs[0]&0xfe)>>1);
            if(( adr_2b & 1 )!=0){
                    /* high bits select */
                    dat1 = ((rom2a.read(adr_2b>>1)&0xf0)<<4)|rom2b.read(adr_2b);
            }else{
                /* low bits select */
                dat1 = ((rom2a.read(adr_2b>>1)&0x0f)<<8)|rom2b.read(adr_2b);
            }
            adr_2c = (dat1 & 0x1ff)<<2;
            if(( offset & 0x01 )!=0)
                    adr_2c += (1<<11);	/* signal 4H to A11 */
            if( ((xevious_bs[0]&1)!=0) ^ (((dat1>>10)&1)!=0) )
                    adr_2c |= 1;
            if( ((xevious_bs[1]&1)!=0) ^ (((dat1>>9)&1)!=0) )
                    adr_2c |= 2;
            if(( offset & 0x01 )!=0){
                    /* return BB1 */
                    dat2 = rom2c.read(adr_2c);
            }else{
                    /* return BB0 */
                    dat2 =rom2c.read(adr_2c);
                    /* swap bit 6 & 7 */
                    dat2 = (dat2 & 0x3f) | ((dat2 & 0x80) >> 1) | ((dat2 & 0x40) << 1);
                    /* flip x & y */
                    dat2 ^= (dat1 >> 4) & 0x40;
                    dat2 ^= (dat1 >> 2) & 0x80;
            }
            return dat2;
    }};

    public static ReadHandlerPtr  xevious_sharedram_r= new ReadHandlerPtr() { public int handler(int offset)
        {
            return xevious_sharedram.read(offset);
    }};

    public static WriteHandlerPtr xevious_sharedram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
    {
            xevious_sharedram.write(offset, data);
    }};



    public static ReadHandlerPtr  xevious_dsw_r= new ReadHandlerPtr() { public int handler(int offset)
        {
            int bit0,bit1;

            bit0 = (input_port_0_r.handler(0) >> offset) & 1;
            bit1 = (input_port_1_r.handler(0) >> offset) & 1;

            return bit0 | (bit1 << 1);
    }};

    /***************************************************************************

     Emulate the custom IO chip.

     In the real Xevious machine, the chip would cause an NMI on CPU #1 to ask
     for data to be transferred. We don't bother causing the NMI, we just look
     into the CPU register to see where the data has to be read/written to, and
     emulate the behaviour of the NMI interrupt.

    ***************************************************************************/
    static int customio_command;
    static int mode,credits;
    static int auxcoinpercred,auxcredpercoin;
    static int leftcoinpercred,leftcredpercoin;
    static int rightcoinpercred,rightcredpercoin;
    static char[] customio=new char[16];

    public static WriteHandlerPtr xevious_customio_data_w= new WriteHandlerPtr() { public void handler(int offset, int data)
    {

            customio[offset] = (char)data;

    if (errorlog!=null) fprintf(errorlog,"%04x: custom IO offset %02x data %02x\n",cpu_getpc(),offset,data);

            switch (customio_command)
            {
                    case 0xa1:
                            if (offset == 0)
                            {
                                    if (data == 0x05)
                                            mode = 1;	/* go into switch mode */
                                    else	/* go into credit mode */
                                    {
                                            credits = 0;	/* this is a good time to reset the credits counter */
                                            mode = 0;
                                    }
                            }
                            else if (offset == 7)
                            {
                                    auxcoinpercred = customio[1];
                                    auxcredpercoin = customio[2];
                                    leftcoinpercred = customio[3];
                                    leftcredpercoin = customio[4];
                                    rightcoinpercred = customio[5];
                                    rightcredpercoin = customio[6];
                            }
                            break;

                    case 0x68:
                            if (offset == 6)
                            {
                                //TODO samples code but we don't support samples
                                    /* it is not known how the parameters control the explosion. */
                                    /* We just use samples. */
                             /*       if (memcmp(customio,"\x40\x40\x40\x01\xff\x00\x20",7) == 0)
                                    {
                                            /* ground target explosion */
                            /*                if (Machine.samples && Machine.samples.sample[0])
                                            {
                                                    osd_play_sample(7,Machine.samples.sample[0].data,
                                                                    Machine.samples.sample[0].length,
                                                                    Machine.samples.sample[0].smpfreq,
                                                                    Machine.samples.sample[0].volume,0);
                                            }
                                    }
                                    else if (memcmp(customio,"\x30\x40\x00\x02\xdf\x00\x10",7) == 0)
                                    {
                                            /* Solvalou explosion */
                             /*               if (Machine.samples && Machine.samples.sample[0])
                                            {
                                                    osd_play_sample(7,Machine.samples.sample[1].data,
                                                                    Machine.samples.sample[1].length,
                                                                    Machine.samples.sample[1].smpfreq,
                                                                    Machine.samples.sample[1].volume,0);
                                            }
                                    }*/
                            }
                            break;
            }
    }};

    static int leftcoininserted;
    static int rightcoininserted;
    static int auxcoininserted;
     static int fire;
     static int fire2;
    public static ReadHandlerPtr  xevious_customio_data_r= new ReadHandlerPtr() { public int handler(int offset)
    {
    if (errorlog!=null && customio_command != 0x71) fprintf(errorlog,"%04x: custom IO read offset %02x\n",cpu_getpc(),offset);

            switch (customio_command)
            {
                    case 0x71:	/* read input */
                    case 0xb1:	/* only issued after 0xe1 (go into credit mode) */
                            if (offset == 0)
                            {
                                    if (mode!=0)	/* switch mode */
                                    {
                                            /* bit 7 is the service switch */
                                            return readinputport(4);
                                    }
                                    else	/* credits mode: return number of credits in BCD format */
                                    {
                                            int in;



                                            in = readinputport(4);

                                            /* check if the user inserted a coin */
                                            if (leftcoinpercred > 0)
                                            {
                                                    if ((in & 0x10) == 0 && credits < 99)
                                                    {
                                                            leftcoininserted++;
                                                            if (leftcoininserted >= leftcoinpercred)
                                                            {
                                                                    credits += leftcredpercoin;
                                                                    leftcoininserted = 0;
                                                            }
                                                    }
                                                    if ((in & 0x20) == 0 && credits < 99)
                                                    {
                                                            rightcoininserted++;
                                                            if (rightcoininserted >= rightcoinpercred)
                                                            {
                                                                    credits += rightcredpercoin;
                                                                    rightcoininserted = 0;
                                                            }
                                                    }
                                                    if ((in & 0x40) == 0 && credits < 99)
                                                    {
                                                            auxcoininserted++;
                                                            if (auxcoininserted >= auxcoinpercred)
                                                            {
                                                                    credits += auxcredpercoin;
                                                                    auxcoininserted = 0;
                                                            }
                                                    }
                                            }
                                            else credits = 2;


                                            /* check for 1 player start button */
                                            if ((in & 0x04) == 0)
                                                    if (credits >= 1) credits--;

                                            /* check for 2 players start button */
                                            if ((in & 0x08) == 0)
                                                    if (credits >= 2) credits -= 2;

                                            return (credits / 10) * 16 + credits % 10;
                                    }
                            }
                            else if (offset == 1)
                            {
                                    int in;
                                   
                                    in = readinputport(2);	/* player 1 input */
                                    in = namco_key[in & 0x0f] | (in & 0xf0);
                                    if ((in & 0x20) == 0)
                                    {
                                            if (fire == 0) in &= ~0x10;	/* fire button impulse */
                                            fire = 1;
                                    }
                                    else fire = 0;
                                    return in;
                            }
                            else if (offset == 2)
                            {
                                    int in;
                                    

                                    in = readinputport(3);	/* player 2 input */
                                    in = namco_key[in & 0x0f] | (in & 0xf0);
                                    if ((in & 0x20) == 0)
                                    {
                                            if (fire2 == 0) in &= ~0x10;	/* fire button impulse */
                                            fire2 = 1;
                                    }
                                    else fire2 = 0;
                                    return in;
                            }

                            break;

                    case 0x74:		/* protect data read ? */
                            if (offset == 3)
                            {
                                    if (customio[0] == 0x80 || customio[0] == 0x10)
                                            return 0x05;	/* 1st check */
                                    else
                                            return 0x95;  /* 2nd check */
                            }
                            else return 0x00;
                           // break;
            }

            return -1;
    }};

    public static ReadHandlerPtr  xevious_customio_r= new ReadHandlerPtr() { public int handler(int offset)
    {

            return customio_command;
    }};

    public static WriteHandlerPtr xevious_customio_w= new WriteHandlerPtr() { public void handler(int offset, int data)
    {

    if (errorlog!=null && data != 0x10 && data != 0x71) fprintf(errorlog,"%04x: custom IO command %02x\n",cpu_getpc(),data);

            customio_command = data;

            switch (data)
            {
                    case 0x10:
                            return;	/* nop */
                            //break;
            }
    }};



    public static WriteHandlerPtr xevious_halt_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                cpu_halt(1,data&1);
                cpu_halt(2,data&1);
        }};


    public static WriteHandlerPtr xevious_interrupt_enable_1_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
              interrupt_enable_1 = (data&1);
            }
          };

    public static InterruptPtr xevious_interrupt_1 = new InterruptPtr() {
            public int handler() {
                if (cpu_getiloops() == 0)
                {
                        if (interrupt_enable_1!=0) return interrupt.handler();
                }
                else if (customio_command != 0x10)
                        return nmi_interrupt.handler();

                return ignore_interrupt.handler();
                }
          };



    public static WriteHandlerPtr xevious_interrupt_enable_2_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
              interrupt_enable_2 = (data&1);
            }
          };


    public static InterruptPtr xevious_interrupt_2 = new InterruptPtr() {
            public int handler() {
              if (interrupt_enable_2!=0) return interrupt.handler();
                    else return ignore_interrupt.handler();
            }
          };



    public static WriteHandlerPtr xevious_interrupt_enable_3_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
              interrupt_enable_3 = NOT(data&1);
            }
          };


    public static InterruptPtr xevious_interrupt_3 = new InterruptPtr() {
            public int handler() {
              if (interrupt_enable_3!=0) return nmi_interrupt.handler();
                else return ignore_interrupt.handler();
            }
          };   
}
