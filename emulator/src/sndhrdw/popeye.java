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
import static mame.inptport.*;

public class popeye
{

        public static InterruptPtr popeye_sh_interrupt = new InterruptPtr() { public int handler()
	{

	/* the CPU needs 2 interrupts per frame, the handler is called 20 times */
	  if (cpu_getiloops() % 10 == 0 ) return nmi_interrupt.handler();
	  else return ignore_interrupt.handler();
	} };

	static int dswbit;


	public static WriteHandlerPtr popeye_portB_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
		dswbit = data / 2;
	} };



	public static ReadHandlerPtr popeye_portA_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		int res;


		res = input_port_3_r.handler(offset);
		res |= (input_port_4_r.handler(offset) << (7-dswbit)) & 0x80;

		return res;
	} };



	static AY8910interface _interface = new AY8910interface
	(
		1,	/* 1 chip */
                20,	/* 20 updates per video frame (good quality) (the frame rate is 30fps) */
                1832727040,	/* 1.832727040 MHZ ????? */
		new int[] { 255 },
		new ReadHandlerPtr[] { popeye_portA_r },
		new ReadHandlerPtr[] { null },
		new WriteHandlerPtr[] { null },
		new WriteHandlerPtr[] { popeye_portB_w }
	);
	public static ShStartPtr popeye_sh_start = new ShStartPtr() { public int handler()
	{
		return AY8910_sh_start(_interface);
	} };

}