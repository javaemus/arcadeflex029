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
 *  NOTES: Not sure if AY8910interface is correct
 */
package sndhrdw;


import static sndhrdw._8910intfH.*;
import static sndhrdw._8910intf.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static sndhrdw.generic.*;
/**
 *
 * @author shadow
 */
public class blueprnt {
        public static InterruptPtr blueprnt_sh_interrupt = new InterruptPtr() { public int handler()
	{
                
            if (cpu_getiloops() % 2 == 0) return interrupt.handler();
            else
            {
                    AY8910_update();

                    if (pending_commands!=0) return nmi_interrupt.handler();
                    else return ignore_interrupt.handler();
            }
	} };
        
        static AY8910interface _interface = new AY8910interface
	(
            2,	/* 2 chips */
            8,	/* 8 updates per video frame (good quality) */
            1789750000,	/* 1.78975 MHZ ????? */
            new int[]{ 255, 255 },
            new ReadHandlerPtr[]{ null,null },
            new ReadHandlerPtr[]{ sound_command_r,null},
            new WriteHandlerPtr[]{ null,null },
            new WriteHandlerPtr[]{ null,null }
        );


        public static ShStartPtr blueprnt_sh_start = new ShStartPtr() { public int handler()
	{
            pending_commands = 0;

            return AY8910_sh_start(_interface);
        }};

}
