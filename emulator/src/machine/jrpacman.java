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
import static mame.cpuintrf.*;
import static mame.mame.*;
import static arcadeflex.osdepend.*;
import static mame.osdependH.*;
/**
 *
 * @author shadow
 */
public class jrpacman {
      static int speedcheat = 0;/* a well known hack allows to make JrPac Man run at four times */
				/* his usual speed. When we start the emulation, we check if the */
				/* hack can be applied, and set this flag accordingly. */

  public static InitMachinePtr jrpacman_init_machine = new InitMachinePtr()
  {
    public void handler() {
        /* check if the loaded set of ROMs allows the Pac Man speed hack */
      if (RAM[0x180b] == 0xbe || RAM[0x180b] == 0x01)
        jrpacman.speedcheat = 1;
      else 
         jrpacman.speedcheat = 0;

    }
  };

  public static InterruptPtr jrpacman_interrupt = new InterruptPtr()
  {
    public int handler() {
        /* speed up cheat */
	if (jrpacman.speedcheat != 0)
	{
		if (osd_key_pressed(OSD_KEY_CONTROL))
			RAM[0x180b] = 0x01;
		else
			RAM[0x180b] = 0xbe;
	}

	return interrupt.handler();       
    }
  };
}
