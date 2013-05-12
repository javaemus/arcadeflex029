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
import static mame.mame.*;
import static mame.osdependH.*;
import static mame.inptport.*;
public class warpwarp
{


	public static ReadHandlerPtr warpwarp_input_c000_7_r = new ReadHandlerPtr() { public int handler(int offset)
	{
            	int res;
                res = readinputport(1);
                return(((res & (1<<offset))!=0) ? 1 : 0);
		/*switch (offset&7)
		{
		case 0x0 : if (osd_key_pressed(OSD_KEY_3)) return(0); else return(1);
		case 0x1 : return(1);
		case 0x2 : if (osd_key_pressed(OSD_KEY_1)) return(0); else return(1);
		case 0x3 : if (osd_key_pressed(OSD_KEY_2)) return(0); else return(1);
		case 0x4 : if (osd_key_pressed(OSD_KEY_ALT)) return(0); else return(1);
		case 0x5 : if (osd_key_pressed(OSD_KEY_F1)) return(0); else return(1);
		case 0x6 : return(1);
		case 0x7 : return(1);
		default:
			break;
		}
		return(0);*/
	} };
/* Read the Dipswitches */
	public static ReadHandlerPtr warpwarp_input_c020_27_r = new ReadHandlerPtr() { public int handler(int offset)
	{
            	int res;

                res = readinputport(0);
                 return(((res & (1<<offset& 7))!=0) ? 1 : 0);

	} };
	public static ReadHandlerPtr warpwarp_input_controller_r = new ReadHandlerPtr() { public int handler(int offset)
	{

                int res;
                res = readinputport(2);
                if ((res & 1)!=0) return(111);
                if ((res & 2)!=0) return(167);
                if ((res & 4)!=0) return(63);
                if ((res & 8)!=0) return(23);
                return(255);
	} };

}	