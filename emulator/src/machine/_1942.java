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

/* unchanged in v0.29
 * ported to v0.28
 * ported to v0.27
 *
 *
 *
 */

package machine;

import static mame.driverH.*;
import static mame.mame.*;

public class _1942
{


	static int bankaddress;
        
	public static WriteHandlerPtr c1942_bankswitch_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
		bankaddress = 0x10000 + (data & 0x03) * 0x4000;
	} };



	public static ReadHandlerPtr c1942_bankedrom_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return RAM[bankaddress + offset];
	} };



	static int count;
	public static InterruptPtr c1942_interrupt = new InterruptPtr() { public int handler()
	{

		count = (count + 1) % 2;

		if (count != 0) return 0x00cf;	/* RST 08h */
		else return 0x00d7;	/* RST 10h */
	} };
}