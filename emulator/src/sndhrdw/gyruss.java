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

import static Z80.Z80H.*;
import static Z80.Z80.*;
import static sndhrdw._8910intfH.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.generic.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;

public class gyruss
{
	
	static char samplenumber=0;
	static final int emulation_rate = 22050;
	
	
	
	public static WriteHandlerPtr gyruss_sh_soundfx_on_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	
		if (Machine.samples == null) return;
               /*                 static unsigned char soundon = 0; //TODO if we support external samples

                if (Machine->samples == 0) return;

                if (data) {
                  if (Machine->samples->sample[data-1])
                    osd_play_sample(7,Machine->samples->sample[data-1]->data,
                                  Machine->samples->sample[data-1]->length,
                                  Machine->samples->sample[data-1]->smpfreq,
                                  Machine->samples->sample[data-1]->volume,0);
                }
                else osd_stop_sample(7);

                soundon = data;*/
	} };
	
	public static WriteHandlerPtr gyruss_sh_soundfx_data_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
            samplenumber = (char)data;
	} };
	
	
	
	public static ReadHandlerPtr gyruss_portA_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return cpu_gettotalcycles() / 570;
	} };
	
	
	
	public static InterruptPtr gyruss_sh_interrupt = new InterruptPtr() { public int handler()
	{
                AY8910_update();
		if (pending_commands != 0) return 0xff;
		else return Z80_IGNORE_INT;
	} };
	
	
	
	static AY8910interface _interface = new AY8910interface
	(
		5,	/* 5 chips */
                10,	/* 10 updates per video frame (good quality) */
		1789772727,	/* 1.789772727 MHZ */
		new int[] { 255, 255, 255, 255, 255 },
		new ReadHandlerPtr[] { null, null, gyruss_portA_r },
		new ReadHandlerPtr[] { null,null,null,null,null },
		new WriteHandlerPtr[] { null,null,null,null,null },
		new WriteHandlerPtr[] { null,null,null,null,null }
	);
	
	
	
	public static ShStartPtr gyruss_sh_start = new ShStartPtr() { public int handler()
	{
		pending_commands = 0;
	
		return AY8910_sh_start(_interface);
	} };
}
