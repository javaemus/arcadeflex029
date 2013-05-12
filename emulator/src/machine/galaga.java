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
import static vidhrdw.galaga.*;

public class galaga {
  public static CharPtr galaga_sharedram = new CharPtr();
  static int interrupt_enable_1;
  static int interrupt_enable_2;
  static int interrupt_enable_3;
  public static int galaga_hiscoreloaded;

          public static InitMachinePtr galaga_init_machine = new InitMachinePtr()
          {
            public void handler() {
                galaga_hiscoreloaded = 0;

            }
          };

  public static ReadHandlerPtr galaga_hiscore_print_r = new ReadHandlerPtr() {
    public int handler(int offset) {
      if (((cpu_getpc() == 0x031e) || (cpu_getpc() == 0xe1)) && (galaga.galaga_hiscoreloaded != 0))
      {
        if (offset == 4) {
          RAM[0x83f2] = RAM[0x8a25];  /* Adjust the 6th digit */
        }
        return RAM[0x8a20+offset];    /* return HISCORE */
      }

      return RAM[0x2b9+offset];     /* bypass ROM test */
    }
  };

          
  public static ReadHandlerPtr galaga_sharedram_r = new ReadHandlerPtr() {
    public int handler(int offset) {
      return galaga_sharedram.read(offset);
    }
  };

  public static WriteHandlerPtr galaga_sharedram_w = new WriteHandlerPtr() { public void handler(int offset, int data) { 
	if (offset < 0x800)		/* write to video RAM */
		dirtybuffer[offset & 0x3ff] = 1;

	galaga_sharedram.write(offset, data);
  
    }  
  } ;

  public static ReadHandlerPtr galaga_dsw_r = new ReadHandlerPtr()
  {
    public int handler(int offset)
    {
      	int bit0 = (input_port_0_r.handler(0) >> offset) & 1;
	int bit1 = (input_port_1_r.handler(0) >> offset) & 1;

	return bit0 | (bit1 << 1);
    }
  };
  
/***************************************************************************

 Emulate the custom IO chip.

 In the real Galaga machine, the chip would cause an NMI on CPU #1 to ask
 for data to be transferred. We don't bother causing the NMI, we just look
 into the CPU register to see where the data has to be read/written to, and
 emulate the behaviour of the NMI interrupt.

***************************************************************************/
  
  static int customio_command;
  static int mode,credits;
  static int coinpercred,credpercoin;
  static  char[] customio=new char[16];

  public static WriteHandlerPtr galaga_customio_data_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
 	customio[offset] = (char)data;

        if (errorlog!=null) fprintf(errorlog,"%04x: custom IO offset %02x data %02x\n",cpu_getpc(),offset,data);

	switch (customio_command)
	{
		case 0xa8:
			if (offset == 3 && data == 0x20)	/* total hack */
			{
		        if (Machine.samples!=null && Machine.samples.sample[0]!=null)
				{
					osd_play_sample(7,Machine.samples.sample[0].data,
							Machine.samples.sample[0].length,
							Machine.samples.sample[0].smpfreq,
							Machine.samples.sample[0].volume,0);
				}
			}
			break;

		case 0xe1:
			if (offset == 7)
			{
				coinpercred = customio[1];
				credpercoin = customio[2];
			}
			break;
	}
    }
  };
 static int coininserted;
  public static ReadHandlerPtr galaga_customio_data_r = new ReadHandlerPtr() {
    public int handler(int offset) {
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
					if (coinpercred > 0)
					{
						if ((in & 0x70) != 0x70 && credits < 99)
						{
							coininserted++;
							if (coininserted >= coinpercred)
							{
								credits += credpercoin;
								coininserted = 0;
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
				return readinputport(2);	/* player 1 input */
			else if (offset == 2)
				return readinputport(3);	/* player 2 input */

			break;
	}

	return -1;
        }
  };
  public static ReadHandlerPtr galaga_customio_r = new ReadHandlerPtr() {
    public int handler(int offset) {
        return customio_command;
    }};
  
  public static WriteHandlerPtr galaga_customio_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
        
        if (errorlog!=null && data != 0x10 && data != 0x71) fprintf(errorlog,"%04x: custom IO command %02x\n",cpu_getpc(),data);

	customio_command = data;

	switch (data)
	{
		case 0x10:
			return;	/* nop */
			//break;

		case 0xa1:	/* go into switch mode */
			mode = 1;
			break;

		case 0xe1:	/* go into credit mode */
			credits = 0;	/* this is a good time to reset the credits counter */
			mode = 0;
			break;
	}
    }};
  
  public static WriteHandlerPtr galaga_halt_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
      cpu_halt(1, data);
      cpu_halt(2, data);
    }
  };

  public static WriteHandlerPtr galaga_interrupt_enable_1_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
      interrupt_enable_1 = data & 1;
    }
  };

  public static InterruptPtr galaga_interrupt_1 = new InterruptPtr() {
    public int handler() {
      	if (cpu_getiloops() == 0)
	{
		galaga_vh_interrupt();	/* update the background stars position */

		if (interrupt_enable_1!=0) return interrupt.handler();
	}
	else if (customio_command != 0x10)
		return nmi_interrupt.handler();

	return ignore_interrupt.handler();
    }
  };

  public static WriteHandlerPtr galaga_interrupt_enable_2_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
      interrupt_enable_2 = data & 1;
    }
  };

  public static InterruptPtr galaga_interrupt_2 = new InterruptPtr() {
    public int handler() {
      if (interrupt_enable_2!=0) return interrupt.handler();
	else return ignore_interrupt.handler();
    }
  };

  public static WriteHandlerPtr galaga_interrupt_enable_3_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
      interrupt_enable_3 = NOT(data & 1);
    }
  };

  public static InterruptPtr galaga_interrupt_3 = new InterruptPtr() {
    public int handler() {
     if (interrupt_enable_3!=0) return nmi_interrupt.handler();
	else return ignore_interrupt.handler();
    }
  };   
}
