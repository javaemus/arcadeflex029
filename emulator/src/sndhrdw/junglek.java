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
 */

package sndhrdw;

import static Z80.Z80H.*;
import static Z80.Z80.*;
import static sndhrdw._8910intfH.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.generic.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static sndhrdw.generic.*;
import static mame.inptport.*;

public class junglek
{

        static int sndnmi_disable = 1;

        public static ReadHandlerPtr taito_dsw_2_r = new ReadHandlerPtr() { public int handler(int offset)
	{
                    return readinputport(5);
        }};
        public static ReadHandlerPtr taito_dsw_3_r = new ReadHandlerPtr() { public int handler(int offset)
	{
                    return readinputport(6);
        }};
        public static WriteHandlerPtr sndnmi_msk = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		sndnmi_disable = data & 0x01;
	} };
        static AY8910interface _interface = new AY8910interface
	(
		4,	/* 4 chips */
                1,	/* 1 update per video frame (low quality) */
		1789750000,	/* 1.78975 MHZ ???? */
		new int[] { 255, 255, 255, 255 },
		new ReadHandlerPtr[] { taito_dsw_2_r },
		new ReadHandlerPtr[] { taito_dsw_3_r },
		new WriteHandlerPtr[] { null,null,null,null },
		new WriteHandlerPtr[] { null,null,null,sndnmi_msk }
	);

	public static ShStartPtr junglek_sh_start = new ShStartPtr() { public int handler()
	{
		pending_commands = 0;

		return AY8910_sh_start(_interface);
	} };

        static int count;
	public static InterruptPtr junglek_sh_interrupt = new InterruptPtr() { public int handler()
	{
               	count++;
                if (count>=8){
                    count = 0;
                    return 0xff;
                }

                else
                {
                    if ((pending_commands!=0) && !(sndnmi_disable!=0)) return Z80_NMI_INT;
                    else return Z80_IGNORE_INT;
                }
	} };
}

