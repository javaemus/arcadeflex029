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

import static mame.driverH.*;
import static mame.cpuintrf.*;
import static mame.inptport.*;
import static mame.mame.*;

public class asteroid 
{
    static final int IN0_HYPER=(1 << 3);
    static final int IN0_FIRE=(1 << 4);
    static final int IN0_SLAM=(1 << 6);
    static final int IN0_TEST=(1 << 7);
    static final int IN1_COIN=(1 << 0);
    static final int IN1_COIN2=(1 << 1);
    static final int IN1_COIN3=(1 << 2);
    static final int IN1_P1=(1 << 3);
    static final int IN1_P2=(1 << 4);
    static final int IN1_THRUST=(1 << 5);
    static final int IN1_RIGHT=(1 << 6);
    static final int IN1_LEFT=(1 << 7);

    static int bank = 0;
    public static ReadHandlerPtr asteroid_IN0_r = new ReadHandlerPtr() { public int handler(int offset)
    {
	int res;
	int res1;

	res1 = readinputport(0);
	res = 0x00;

	switch (offset & 0x07) {
		case 0: /* nothing */
			break;
		case 1:  /* clock toggles at 3 KHz*/
			if ((cpu_gettotalcycles() & 0x100)!=0)
				res = 0x80;
		case 2: /* vector generator halt */
			break;
		case 3: /* hyperspace/shield */
			if ((res1 & IN0_HYPER)!=0) res |= 0x80;
			break;
		case 4: /* fire */
			if ((res1 & IN0_FIRE)!=0) res |= 0x80;
			break;
		case 5: /* diagnostics */
			break;
		case 6: /* slam */
			if ((res1 & IN0_SLAM)!=0) res |= 0x80;
			break;
		case 7: /* self test */
			if ((res1 & IN0_TEST)!=0) res |= 0x80;
			break;
		}
	      return res;
	}};

/*

These 7 memory locations are used to read the player's controls.
Typically, only the high bit is used. Note that the coin inputs are active-low.

*/
        public static ReadHandlerPtr asteroid_IN1_r = new ReadHandlerPtr() { public int handler(int offset)
	{


	int res;
	int res1;

	res1 = readinputport(1);
	res = 0x00;

	switch (offset & 0x07) {
		case 0: /* left coin slot */
			res = 0x80;
			if ((res1 & IN1_COIN)!=0) res &= ~0x80;
			break;
		case 1:  /* center coin slot */
			res = 0x80;
/*			if (res1 & IN1_COIN2) res &= ~0x80; */
			break;
		case 2: /* right coin slot */
			res = 0x80;
/*			if (res1 & IN1_COIN3) res &= ~0x80; */
			break;
		case 3: /* 1P start */
			if ((res1 & IN1_P1)!=0) res |= 0x80;
			break;
		case 4: /* 2P start */
			if ((res1 & IN1_P2)!=0) res |= 0x80;
			break;
		case 5: /* thrust */
			if ((res1 & IN1_THRUST)!=0) res |= 0x80;
			break;
		case 6: /* rotate right */
			if ((res1 & IN1_RIGHT)!=0) res |= 0x80;
			break;
		case 7: /* rotate left */
			if ((res1 & IN1_LEFT)!=0) res |= 0x80;
			break;
		}
	  return res;
	}};
        public static ReadHandlerPtr asteroid_DSW1_r = new ReadHandlerPtr() { public int handler(int offset)
	{
            int res;
            int res1;

            res1 = readinputport(2);

            res = 0xfc | ((res1 >> (2 * (3 - (offset & 0x3)))) & 0x3);
            return res;
	}};
        public static WriteHandlerPtr asteroid_bank_switch_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	int newbank;
	
	newbank = (data >> 2) & 1;
	if (bank != newbank) {
		/* Perform bankswitching on page 1 and page 2 */
		int temp;
		int i;
		
		bank = newbank;
		for (i = 0; i < 0x100; i++) {
			temp = RAM[0x100 + i];
			RAM[0x100 + i] = RAM[0x200 + i];
			RAM[0x200 + i] = (char)temp;
			}
		}
	}};
        public static WriteHandlerPtr astdelux_bank_switch_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{	
	int newbank;
	
	switch (offset & 0x07) {
		case 0: /* Player 1 start LED */
			break;
		case 1:  /* Player 2 start LED */
			break;
		case 2:
			break;
		case 3: /* Thrust sound */
			break;
		case 4: /* bank switch */
			newbank = (data >> 2) & 1;
			if (bank != newbank) {
				/* Perform bankswitching on page 1 and page 2 */
				int temp;
				int i;
		
				bank = newbank;
				for (i = 0; i < 0x100; i++) {
					temp = RAM[0x100 + i];
					RAM[0x100 + i] = RAM[0x200 + i];
				RAM[0x200 + i] = (char)temp;
					}
				}
			break;
		case 5: /* left coin counter */
			break;
		case 6: /* center coin counter */
			break;
		case 7: /* right coin counter */
			break;
		}
	}};
        public static InitMachinePtr asteroid_init_machine = new InitMachinePtr() {	public void handler()
	{	

            bank = 0;
	}};
	   
}
