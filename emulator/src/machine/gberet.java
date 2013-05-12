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
import static Z80.Z80H.*;
import static mame.driverH.*;

public class gberet
{
	public static CharPtr gberet_interrupt_enable = new CharPtr();

	static int nmi;
	public static InterruptPtr gberet_interrupt = new InterruptPtr() { public int handler()
	{
		nmi = (nmi + 1) % 32;

		if (nmi == 0) return 0xff;
		else if ((nmi % 2) != 0)
		{
			if ((gberet_interrupt_enable.read() & 1) != 0) return Z80_NMI_INT;
		}
		return Z80_IGNORE_INT;
	} };
}