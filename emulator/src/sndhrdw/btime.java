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
package sndhrdw;

import static M6502.M6502H.*;
import static sndhrdw._8910intfH.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.generic.*;
import static mame.driverH.*;

public class btime
{



	static AY8910interface _interface = new AY8910interface
	(
		2,	/* 2 chips */
                1,	/* 1 update per video frame (low quality) */
                1536000000,	/* 1 MHZ ? */
		new int[] { 255, 255 },
		new ReadHandlerPtr[] { null,null },
		new ReadHandlerPtr[] { null,null },
		new WriteHandlerPtr[] { null,null },
		new WriteHandlerPtr[] { null,null }
	);



	static int interrupt_enable;


	public static WriteHandlerPtr btime_sh_interrupt_enable_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
		interrupt_enable = data;
	} };



	public static InterruptPtr btime_sh_interrupt = new InterruptPtr() { public int handler()
	{
		if (pending_commands != 0) return INT_IRQ;
		else if (interrupt_enable != 0) return INT_NMI;
		else return INT_NONE;
	} };



	public static ShStartPtr btime_sh_start = new ShStartPtr() { public int handler()
	{
		pending_commands = 0;

		return AY8910_sh_start(_interface);
	} };
}
