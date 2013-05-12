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
 *
 * ported to v0.28
 *
 *
 *
 */
package machine;

import static mame.driverH.*;
import static mame.mame.*;
import static m6809.M6809.*;
import static m6809.M6809H.*;
import static mame.cpuintrf.*;

public class gng {
  static int bankaddress;

  public static InitMachinePtr gng_init_machine = new InitMachinePtr()
  {
    public void handler() {
	/* Set optimization flags for M6809 */
	m6809_Flags = M6809_FAST_NONE;
    }
  };

  public static WriteHandlerPtr gng_bankswitch_w = new WriteHandlerPtr() {
    public void handler(int offset,int data)
    {
        bankaddress = 0x10000 + data * 0x2000;

    }
  };

  public static ReadHandlerPtr gng_bankedrom_r = new ReadHandlerPtr() {
    public int handler(int offset) {
      return RAM[bankaddress + offset];
    }
  };
    public static ReadHandlerPtr gng_catch_loop_r = new ReadHandlerPtr() {
    public int handler(int offset) {
	m6809_Regs	r=new m6809_Regs();

	/* check to see if the branch will be taken */
	m6809_GetRegs (r);
	if ((r.cc & 0x01)!=0) cpu_seticount (0);
	return ROM[0x6184];
        
    }};
}
