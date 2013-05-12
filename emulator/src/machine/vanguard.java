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

import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static arcadeflex.osdepend.*;

public class vanguard
{
	static int coin;
	public static InterruptPtr vanguard_interrupt = new InterruptPtr() { public int handler()
	{

          if(cpu_getiloops()!=0)
          {
		/* user asks to insert coin: generate an interrupt. */
		if (osd_key_pressed(OSD_KEY_3))
		{
			if (coin == 0)
			{
				coin = 1;
				return nmi_interrupt.handler();
			}
		}
		else coin = 0;
                return ignore_interrupt.handler();

            }
          else return interrupt.handler(); /* one IRQ per frame */
	} };
}
