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
 */

package machine;

import static arcadeflex.libc.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static m6809.M6809H.*;
import static m6809.M6809.*;
import static mame.mame.*;

public class tutankhm
{
  static int tutbankaddress = 0x10000;
  static int tut_bank = 0;
  static int count;
  public static InterruptPtr tutankhm_interrupt = new InterruptPtr()
  {
    public int handler() {
     count = (count + 1) % 2;

      if (count!=0) return INT_IRQ;
	else return INT_NONE;
    }
  };

  public static InitMachinePtr tutankhm_init_machine = new InitMachinePtr()
  {
    public void handler() {
      	/* Set optimization flags for M6809 */
	m6809_Flags = M6809_FAST_OP | M6809_FAST_S;    /* thanks to Dave Dahl for suggestion */

  
    }
  };

  public static ReadHandlerPtr tut_rnd_r = new ReadHandlerPtr() {
    public int handler(int offest) {
      return rand() & 0xFF;
    }
  };

  public static WriteHandlerPtr tut_bankselect_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
        if (tut_bank == data) return;

	tutbankaddress = 0x10000 + ( data * 0x1000 );
	tut_bank = data;
    }
  };

  public static ReadHandlerPtr tut_bankedrom_r = new ReadHandlerPtr() {
    public int handler(int offset) {
      return RAM[tutbankaddress + offset];
    }
  };
}
