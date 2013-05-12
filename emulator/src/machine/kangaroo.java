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
 *
 * ported to v0.27
 *
 *
 */


package machine;

import static mame.driverH.*;

public class kangaroo
{
  static int clock = 0;

  public static ReadHandlerPtr kangaroo_sec_chip_r = new ReadHandlerPtr()
  {
    public int handler(int offset) {
          clock++;
          return (clock & 0xff);
    }
  };

  public static WriteHandlerPtr kangaroo_sec_chip_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {  } } ;

  public static InterruptPtr kangaroo_interrupt = new InterruptPtr()
  {
    public int handler() {
      return 0xffff;
    }
  };
}