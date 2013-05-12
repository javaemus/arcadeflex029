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
import static vidhrdw.spacefb.*;

public class spacefb
{
  public static WriteHandlerPtr spacefb_port_0_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
       spacefb_vref = (data & (1<<5)) != 0 ? 128 : 0;
    }
  };

  public static WriteHandlerPtr spacefb_port_1_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {  } } ;

  public static WriteHandlerPtr spacefb_port_2_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {  } } ;

  public static WriteHandlerPtr spacefb_port_3_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {  } } ;
  
  static int count;
  public static InterruptPtr spacefb_interrupt = new InterruptPtr()
  {
    public int handler() {
      count += 1;

      if ((count & 0x1) != 0)
           return (0x00cf);		/* RST 08h */
	return (0x00d7);		/* RST 10h */
    }
  };
}
