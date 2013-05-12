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

import static arcadeflex.osdepend.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static Z80.Z80H.*;

public class naughtyb {
static int coin;
  public static InterruptPtr naughtyb_interrupt = new InterruptPtr()
  {
    public int handler()
    {
      int res;

      res=Z80_IGNORE_INT;
      if (osd_key_pressed(OSD_KEY_3))
      {
        if (coin == 0) res = Z80_NMI_INT;
		coin = 1;
      } else coin = 0;

	return res;
    }
  };
}
