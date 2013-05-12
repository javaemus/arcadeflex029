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
package sndhrdw;

import static arcadeflex.libc.*;
import static mame.driverH.*;
import static arcadeflex.osdepend.*;
import static mame.mame.*;
import static sndhrdw.pengo.*;

public class bosco {
    
    static char speech[]=new char[0x6000];	/* 24k for speech */

    public static ShStartPtr bosco_sh_start = new ShStartPtr() { public int handler()
    {

	int i;
	char bits;

	/* decode the rom samples */
	for (i = 0;i < 0x3000;i++)
	{
		bits = (char)(Machine.memory_region[4][i] & 0x0f);
		speech[2 * i] = (char)(((bits << 4) | bits) + 0x80);

		bits = (char)(Machine.memory_region[4][i] & 0xf0);
		speech[2 * i + 1] = (char)((bits | (bits >> 4)) + 0x80);
	}

	return rallyx_sh_start.handler();
    }};

    public static void bosco_sample_play(int offset, int length)
    {
            if (play_sound == 0)
                    return;

            osd_play_sample(4,speech,offset,length,4000,0xff,0);
    }   
}
