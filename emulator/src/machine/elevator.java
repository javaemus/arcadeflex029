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
import static mame.driverH.*;
import static mame.mame.*;

public class elevator
{

	static int bank;

	public static InitMachinePtr elevator_init_machine = new InitMachinePtr() { public void handler()
	{
		bank = 0x00;

	} };


        public static CharPtr elevator_protection = new CharPtr();

	public static ReadHandlerPtr elevator_protection_r = new ReadHandlerPtr() { public int handler(int offset)
	{
      	    int data;

            data = (int)(elevator_protection.read());
	/*********************************************************************/
	/*  elevator action , fast entry 52h , must be return 17h            */
	/*  (preliminary version)                                            */
            data = data - 0x3b;
	/*********************************************************************/

            if (errorlog != null) fprintf(errorlog,"Protection entry:%02x , return:%02x\n" , (int)elevator_protection.read() , data );
            return data;
	} };



	public static ReadHandlerPtr elevator_protection_t_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return 0xff;
	} };



	public static WriteHandlerPtr elevatob_bankswitch_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if ((data & 0x80) != bank)
		{
                    if ((data & 0x80) != 0) memcpy(RAM, 0x7000, RAM, 0xf000, 0x1000);
                    else memcpy(RAM, 0x7000, RAM, 0xe000,0x1000);

                    bank = data & 0x80;
	  	}
	} };
}