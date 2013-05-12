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
import static mame.mame.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static M6502.M6502H.*;

public class mystston 
{
  static int coin;
  public static InterruptPtr mystston_interrupt = new InterruptPtr()
  {
    public int handler()
    {
	if (osd_key_pressed(OSD_KEY_3))
	{
		if (coin == 0)
		{
			coin = 1;
			return INT_NMI;
		}
	}
	else coin = 0;

	return INT_IRQ;
    }
  };    
}
