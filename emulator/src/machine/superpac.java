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
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.inptport.*;
import static m6809.M6809H.*;
import static m6809.M6809.*;

public class superpac {
  public static CharPtr superpac_sharedram = new CharPtr();
  public static CharPtr superpac_customio_1 = new CharPtr();
  public static CharPtr superpac_customio_2 = new CharPtr();
  public static CharPtr pacnpal_loop_val_1 = new CharPtr();
  static int interrupt_enable_1;
  static int interrupt_enable_2;
  static int coin;
  static int credits;
  static int fire;
  static int start;
  static int[] crednum = { 1, 2, 3, 6, 7, 1, 3, 1 };
  static int[] credden = { 1, 1, 1, 1, 1, 2, 2, 3 };



  public static InitMachinePtr superpac_init_machine = new InitMachinePtr()
  {
    public void handler() {
      /* Reset all flags */
	coin = credits = fire = start = 0;

	/* Disable interrupts */
	interrupt_enable_1 = interrupt_enable_2 = 0;

	/* Set optimization flags for M6809 */
	m6809_Flags = M6809_FAST_OP | M6809_FAST_S | M6809_FAST_U;
	
    }
  };



  public static ReadHandlerPtr superpac_sharedram_r = new ReadHandlerPtr() {
    public int handler(int offset) {
      return superpac_sharedram.read(offset);
    }
  };

  public static ReadHandlerPtr superpac_sharedram_r2 = new ReadHandlerPtr()
  {
    public int handler(int offset)
    {
       /* to speed up emulation, we check for the loop the sound CPU sits in most of the time
	   and end the current iteration (things will start going again with the next IRQ) */
        if ((offset == 0xfb - 0x40) && (superpac_sharedram.read(offset) == 0))
		cpu_seticount (0);
	return superpac_sharedram.read(offset);

    }
  };

public static ReadHandlerPtr pacnpal_sharedram_r2 = new ReadHandlerPtr() {

     public int handler(int offset)
     {

	return superpac_sharedram.read(offset);
    }
};


public static WriteHandlerPtr pacnpal_sharedram_w2 = new WriteHandlerPtr()
  {
    public void handler(int offset,int data)
    {
	superpac_sharedram.write(offset, data);

	/* this hack allows proper synchronization between CPU 1 & 2 at startup */
	if ((offset == 0x41 - 0x40) && (data == 2))
		cpu_seticount (0);
      }
};

  public static WriteHandlerPtr superpac_sharedram_w = new WriteHandlerPtr() {
    public void handler(int offset,int data) {
      superpac_sharedram.write(offset,data);
    }
  };

  public static WriteHandlerPtr superpac_customio_w_1 = new WriteHandlerPtr() {
    public void handler(int offset,int data) {
      superpac_customio_1.write(offset,data);
    }
  };

  public static WriteHandlerPtr superpac_customio_w_2 = new WriteHandlerPtr() {
    public void handler(int offset,int data) {
      superpac_customio_2.write(offset,data);
    }
  };
  static void superpac_update_credits()
  {
    int val = readinputport(3) & 0xF;
    if ((val & 0x1) != 0)
    {
      if (coin != 0) { val &=  ~1; } else {
        coin = 1; credits += 1;
      }
    } else coin = 0;

    int temp = readinputport(1) & 0x7;
    val = readinputport(3) >> 4;
    if ((val & 0x1) != 0)
    {
      if ((start != 0) || (credits < credden[temp])) { val &= ~1; } else {
        credits -= credden[temp]; start = 1;
      }
    } else start = 0;
    if ((val & 0x2) != 0)
    {
      if ((start != 0) || (credits < 2 * credden[temp])) { val &= ~2; } else {
        credits -= 2 * credden[temp]; start = 1;
      }
    } else start = 0;
  }
  static int cointrig;
  public static ReadHandlerPtr superpac_customio_r_1 = new ReadHandlerPtr()
  {

    public int handler(int offset)
    {
        int val, temp;

	switch (superpac_customio_1.read(8))
	{
		/* mode 1 & 3 are used by Pac & Pals, and returns actual important values */
		case 1:
		case 3:
			switch (offset)
			{

				case 0:
					val = readinputport (3) & 0x0f;
					if ((val & 1)!=0)
					{
						if (coin!=0)
						{
							if (cointrig==0) val &= ~1;
							else cointrig -= 1;
						}
						else coin = cointrig = 1; credits += 1;
					}
					else coin = 0;
					break;

				case 1:
					val = readinputport (2) & 0x0f;
					break;

				case 2:
					val = 0;
					break;

				case 3:
					temp = readinputport (1) & 7;
					val = readinputport (3) >> 4;
					val |= val << 2;
					if ((val & 1)!=0)
					{
						if ((start!=0) || credits < credden[temp]) val &= ~1;
						else credits -= credden[temp]; start = 1;
					}
					else start = 0;
					if ((val & 2)!=0)
					{
						if ((start!=0) || credits < 2 * credden[temp]) val &= ~2;
						else credits -= 2 * credden[temp]; start = 1;
					}
					else start = 0;

					/* I don't know the exact mix, but the low bit is used both for
					   the fire button and for player 1 start; I'm just ORing for now */
					val |= readinputport (2) >> 4;
					break;

				case 4:
				case 5:
				case 6:
				case 7:
					val = 0xf;
					break;

				default:
					val = superpac_customio_1.read(offset);
					break;
			}
			return val;

		/* mode 4 is the standard, and returns actual important values */
		case 4:
			switch (offset)
			{
				case 0:
					superpac_update_credits ();
					temp = readinputport (1) & 7;
					val = (credits * crednum[temp] / credden[temp]) / 10;
					break;

				case 1:
					superpac_update_credits ();
					temp = readinputport (1) & 7;
					val = (credits * crednum[temp] / credden[temp]) % 10;
					break;

				case 4:
					val = readinputport (2) & 0x0f;
					break;

				case 5:
					val = readinputport (2) >> 4;
					if ((val & 2)!=0)
					{
						if (fire==0) val |= 1; fire = 1;
					}
					else fire = 0;
					break;

				case 6:
				case 7:
					val = 0xf;
					break;

				default:
					val = superpac_customio_1.read(offset);
					break;
			}
			return val;

		/* mode 8 is the test mode: always return 0 for these locations */
		case 8:
			credits = 0;
			if (offset >= 9 && offset <= 15)
				return 0;
			break;
	}
	return superpac_customio_1.read(offset);

    }
  };

  public static ReadHandlerPtr superpac_customio_r_2 = new ReadHandlerPtr()
  {
    public int handler(int offset)
    {
        	int val;

	switch (superpac_customio_2.read(8))
	{
		/* mode 3 is the standard for Pac & Pals, and returns actual important values */
		case 3:
			switch (offset)
			{
				case 0:
				case 1:
				case 2:
				case 3:
					val = 0;
					break;

				case 4:
					val = readinputport (0) & 0xf;
					break;

				case 5:
					val = readinputport (1) >> 4;
					break;

				case 6:
					val = readinputport (1) & 0xf;
					break;

				case 7:
					val = 0;
					/* bit 3 = configuration mode */
					/* val |= 0x08; */
					break;

				default:
					val = superpac_customio_2.read(offset);
					break;
			}
			return val;

		/* mode 9 is the standard, and returns actual important values */
		case 9:{
			switch (offset)
			{
				case 0:
					val = readinputport (1) & 0x0f;
					break;

				case 1:
					val = readinputport (1) >> 4;
					break;

				case 2:
					val = 0;
					break;

				case 3:
					val = readinputport (0) & 0x0f;
					break;

				case 4:
					val = readinputport (0) >> 4;
					break;

				case 5:
					val = 0;
					break;

				case 6:
					val = 0;
					/* bit 3 = configuration mode? */
					/* val |= 0x08; */
					break;

				case 7:
					val = 0;
					break;

				default:
					val = superpac_customio_2.read(offset);
					break;
			}
			return val;}

		/* mode 8 is the test mode: always return 0 for these locations */
		case 8:
			credits = 0;
			if (offset >= 9 && offset <= 15)
				return 0;
			break;
	}
	return superpac_customio_2.read(offset);
      }
  };

  public static WriteHandlerPtr superpac_interrupt_enable_1_w = new WriteHandlerPtr() {
    public void handler(int offset,int data) {
      interrupt_enable_1 = offset;
    }
  };

  public static InterruptPtr superpac_interrupt_1 = new InterruptPtr() {
    public int handler() {
      if (interrupt_enable_1!=0) return INT_IRQ;
	else return INT_NONE;

    }
  };

  public static WriteHandlerPtr pacnpal_interrupt_enable_2_w = new WriteHandlerPtr()
  {
    public void handler(int offset,int data) {
      	/* note: only used by Pac & Pal */
	if (offset == 1 && cpu_getpc () == 0xf2cd && cpu_geticount () > 100)
		cpu_seticount (100);

	interrupt_enable_2 = offset;
    }
  };

  public static WriteHandlerPtr superpac_cpu_enable_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
      cpu_halt(1, offset);
    }
  };
  public static InterruptPtr  pacnpal_interrupt_2= new InterruptPtr() {
          public int handler() {
              if (interrupt_enable_2!=0) return INT_IRQ;
	      else return INT_NONE;
     }
  };






}

