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
import static mame.mame.*;
import static mame.inptport.*;
import static vidhrdw.vector.*;
import static vidhrdw.atari_vg.*;
public class bzone {
    static final int IN0_3KHZ= (1<<7);
    static final int IN0_VG_HALT= (1<<6);

    public static ReadHandlerPtr bzone_IN0_r = new ReadHandlerPtr() { public int handler(int offset)
    {
            int res;

            res = readinputport(0);

            if ((cpu_geticount() & 0x100)!=0)
                    res|=IN0_3KHZ;
            else
                    res&=~IN0_3KHZ;

            if (vg_done(cpu_gettotalcycles())!=0)
                    res|=IN0_VG_HALT;
            else
                    res&=~IN0_VG_HALT;

            return res;
    }};

    /* Translation table for one-joystick emulation */

    static int one_joy_trans[]={
            0x00,0x0A,0x05,0x00,0x06,0x02,0x01,0x00,
            0x09,0x08,0x04,0x00,0x00,0x00,0x00,0x00,
            0x10,0x1A,0x15,0x10,0x16,0x12,0x11,0x10,
            0x19,0x18,0x14,0x10,0x10,0x10,0x10,0x10 };
    
    static int directors_cut=0x80; /* JB 970901 */
    public static ReadHandlerPtr bzone_IN3_r = new ReadHandlerPtr() { public int handler(int offset)
    {
    
	int res,res1;

	res1=readinputport(4);

	/* Decide between red/green or red only */
	if ((res1 & 0x80) != directors_cut) {
		directors_cut=res1&0x80;
		if (directors_cut!=0) {
			atari_vg_colorram_w.handler(3,3);
			atari_vg_colorram_w.handler(11,11);
		} else {
			atari_vg_colorram_w.handler(3,0);
			atari_vg_colorram_w.handler(11,8);
		}
	}

	res=readinputport(3);
	res|=one_joy_trans[res1&0x1f];

	return (res);
    } };  
}
