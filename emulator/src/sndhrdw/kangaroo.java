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
import static sndhrdw._8910intfH.*;
import static sndhrdw._8910intf.*;
import static mame.driverH.*;


public class kangaroo {
	static AY8910interface _interface = new AY8910interface
	(
		1,	/* 1 chip */
                1,	/* 1 update per video frame (low quality) */
                1250000000,     /* 1.250000000 MHZ?????? */
		new int[] { 255 },
		new ReadHandlerPtr[] { null },
		new ReadHandlerPtr[] { null },
		new WriteHandlerPtr[] { null },
		new WriteHandlerPtr[] { null }
	);



	public static ShStartPtr kangaroo_sh_start = new ShStartPtr() { public int handler()
	{
		return AY8910_sh_start(_interface);
	} };
}
