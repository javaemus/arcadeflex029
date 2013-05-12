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
import static sndhrdw.generic.*;
import static mame.driverH.*;
import static mame.cpuintrf.*;

public class vulgus {
    
  	public static InterruptPtr vulgus_sh_interrupt = new InterruptPtr() { public int handler()
	{
            AY8910_update();

            return interrupt.handler();
	  
	} };

	static AY8910interface _interface = new AY8910interface
	(
		2,	/* 2 chips */
                8,	/* 8 updates per video frame (good quality) */
                1500000000,	/* 1.5 MHZ ? */
                new int[]{ 255, 255 },
		new ReadHandlerPtr[] { null,null },
		new ReadHandlerPtr[] { null,null },
		new WriteHandlerPtr[] { null,null },
		new WriteHandlerPtr[] { null,null}
	);



	public static ShStartPtr vulgus_sh_start = new ShStartPtr() { public int handler()
	{
		pending_commands = 0;

		return AY8910_sh_start(_interface);
	} };  
}
