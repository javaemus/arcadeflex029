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

import static Z80.Z80.*;
import static arcadeflex.libc.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;

public class scramble
{
	public static ReadHandlerPtr scramble_IN2_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		int res;


		res = readinputport(2);

	/*if (errorlog != null) fprintf(errorlog, "%04x: read IN2\n", cpu_getpc());*/

		/* avoid protection */
		if (cpu_getpc() == 0x00e4) res &= 0x7f;

		return res;
	} };



	public static ReadHandlerPtr scramble_protection_r = new ReadHandlerPtr() {	public int handler(int offset)
	{
		if (errorlog != null) fprintf(errorlog, "%04x: read protection\n", cpu_getpc());

		return 0x6f;

		/* codes for the Konami version (not working yet) */
		/*if (cpu_getpc() == 0x00a8) return 0xf0;
		if (cpu_getpc() == 0x00be) return 0xb0;
		if (cpu_getpc() == 0x0c1d) return 0xf0;
		if (cpu_getpc() == 0x0c6a) return 0xb0;
		if (cpu_getpc() == 0x0ceb) return 0x40;
		if (cpu_getpc() == 0x0d37) return 0x60;*/
	} };
}
