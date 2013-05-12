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


import static sndhrdw._8910intfH.*;
import static sndhrdw._8910intf.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static sndhrdw.generic.*;

public class scramble
{


	public static ReadHandlerPtr scramble_portB_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		int clock;

		final int TIMER_RATE = (512*2);

		clock = cpu_gettotalcycles() / TIMER_RATE;

		clock = ((clock & 0x01) << 4) | ((clock & 0x02) << 6) |
			((clock & 0x08) << 2) | ((clock & 0x10) << 2);

		return clock;
	} };



	public static InterruptPtr scramble_sh_interrupt = new InterruptPtr() { public int handler()
	{
		AY8910_update();
                if (pending_commands!=0) return interrupt.handler();
                else return ignore_interrupt.handler();
	} };



	static AY8910interface _interface = new AY8910interface
	(
		2,	/* 2 chips */
                10,	/* 10 updates per video frame (good quality) */
                1789750000,	/* 1.78975 Mhz (? the crystal is 14.318 MHz) */
		new int[] { 255, 255 },
		new ReadHandlerPtr[] { sound_command_r },
		new ReadHandlerPtr[] { scramble_portB_r },
		new WriteHandlerPtr[] { null,null },
		new WriteHandlerPtr[] { null,null }
	);



	public static ShStartPtr scramble_sh_start = new ShStartPtr() { public int handler()
	{
		pending_commands = 0;

		return AY8910_sh_start(_interface);
	} };
}