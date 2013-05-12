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
 *   since we don't load samples implementing this is useless atm...
 */


package sndhrdw;

import static mame.driverH.*;
import static mame.mame.*;

public class invaders
{
	
	static final int emulation_rate = 11025;
	
	
	static char Sound = 0;
	public static WriteHandlerPtr invaders_sh_port3_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{



	if (Machine.samples == null) return;

	/*if (data & 0x01 && ~Sound & 0x01 && Machine->samples->sample[0])
		osd_play_sample(0,Machine->samples->sample[0]->data,
				Machine->samples->sample[0]->length,
                                Machine->samples->sample[0]->smpfreq,
                                Machine->samples->sample[0]->volume,1);

	if (~data & 0x01 && Sound & 0x01)
		osd_stop_sample(0);

	if (data & 0x02 && ~Sound & 0x02 && Machine->samples->sample[1])
		osd_play_sample(1,Machine->samples->sample[1]->data,
				Machine->samples->sample[1]->length,
                                Machine->samples->sample[1]->smpfreq,
                                Machine->samples->sample[1]->volume,0);

	if (~data & 0x02 && Sound & 0x02)
		osd_stop_sample(1);

	if (data & 0x04 && ~Sound & 0x04 && Machine->samples->sample[2])
		osd_play_sample(2,Machine->samples->sample[2]->data,
				Machine->samples->sample[2]->length,
                                Machine->samples->sample[2]->smpfreq,
                                Machine->samples->sample[2]->volume,0);

	if (~data & 0x04 && Sound & 0x04)
		osd_stop_sample(2);

	if (data & 0x08 && ~Sound & 0x08 && Machine->samples->sample[3])
		osd_play_sample(3,Machine->samples->sample[3]->data,
				Machine->samples->sample[3]->length,
                                Machine->samples->sample[3]->smpfreq,
                                Machine->samples->sample[3]->volume,0);

	if (~data & 0x08 && Sound & 0x08)
		osd_stop_sample(3);

	Sound = data;*/
	
	
	} };
	
	
	public static WriteHandlerPtr invaders_sh_port5_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	
	
		if (Machine.samples == null) return;
	/*if (data & 0x01 && ~Sound & 0x01 && Machine->samples->sample[4])
		osd_play_sample(4,Machine->samples->sample[4]->data,
				Machine->samples->sample[4]->length,
                                Machine->samples->sample[4]->smpfreq,
                                Machine->samples->sample[4]->volume,0);

	if (data & 0x02 && ~Sound & 0x02 && Machine->samples->sample[5])
		osd_play_sample(4,Machine->samples->sample[5]->data,
				Machine->samples->sample[5]->length,
                                Machine->samples->sample[5]->smpfreq,
                                Machine->samples->sample[5]->volume,0);

	if (data & 0x04 && ~Sound & 0x04 && Machine->samples->sample[6])
		osd_play_sample(4,Machine->samples->sample[6]->data,
				Machine->samples->sample[6]->length,
                                Machine->samples->sample[6]->smpfreq,
                                Machine->samples->sample[6]->volume,0);

	if (data & 0x08 && ~Sound & 0x08 && Machine->samples->sample[7])
		osd_play_sample(4,Machine->samples->sample[7]->data,
				Machine->samples->sample[7]->length,
                                Machine->samples->sample[7]->smpfreq,
                                Machine->samples->sample[7]->volume,0);

	if (data & 0x10 && ~Sound & 0x10 && Machine->samples->sample[8])
		osd_play_sample(5,Machine->samples->sample[8]->data,
				Machine->samples->sample[8]->length,
                                Machine->samples->sample[8]->smpfreq,
                                Machine->samples->sample[8]->volume,0);

	if (~data & 0x10 && Sound & 0x10)
		osd_stop_sample(5);

	Sound = data;*/
	
	} };
	
	
	
	public static ShUpdatePtr invaders_sh_update = new ShUpdatePtr() { public void handler()
	{
	} };
}