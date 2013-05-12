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

import static Z80.Z80H.*;
import static Z80.Z80.*;
import static mame.driverH.*;


public class invaders
{
	static int shift_data1,shift_data2,shift_amount;



	public static ReadHandlerPtr invaders_shift_data_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return ((((shift_data1 << 8) | shift_data2) << shift_amount) >> 8) & 0xff;
	} };


	public static WriteHandlerPtr invaders_shift_amount_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
		shift_amount = data;
	} };


	public static WriteHandlerPtr invaders_shift_data_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		shift_data2 = shift_data1;
		shift_data1 = data;
	} };


	static int count;


	public static InterruptPtr invaders_interrupt = new InterruptPtr() { public int handler()
	{
		count++;

		if ((count & 1) != 0) return 0x00cf;	/* RST 08h - 8080's IRQ */
		else
		{
			Z80_Regs R = new Z80_Regs();


			Z80_GetRegs(R);
			R.IFF2 = 1;	/* enable interrupts */
			Z80_SetRegs(R);

			return 0x00d7;	/* RST 10h - 8080's NMI */
		}
	} };
}