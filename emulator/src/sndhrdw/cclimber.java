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
package sndhrdw;

import static sndhrdw._8910intfH.*;
import static sndhrdw._8910intf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static arcadeflex.osdepend.*;

public class cclimber
{



	static final int SND_CLOCK = 3072000;	/* 3.072 Mhz */


	static char samples[] = new char[0x4000];	/* 16k for samples */
	static int sample_freq,sample_volume;
	static int porta;



	public static WriteHandlerPtr cclimber_portA_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		porta = data;
	} };



	static AY8910interface _interface = new AY8910interface
	(
		1,	/* 1 chip */
                1,	/* 1 update per video frame (low quality) */
                1536000000,	/* 1.536000000 MHZ */
		new int[] { 255 },
		new ReadHandlerPtr[] { null },
		new ReadHandlerPtr[] { null },
		new WriteHandlerPtr[] { cclimber_portA_w },
		new WriteHandlerPtr[] { null }
	);



	public static ShStartPtr cclimber_sh_start = new ShStartPtr() { public int handler()
	{
		int i;
		int bits;


		/* decode the rom samples */
		for (i = 0; i < 0x2000; i++)
		{
			bits = Machine.memory_region[2][i] & 0xf0;
			samples[2 * i] = (char) ((bits | (bits >> 4)) + 0x80);

			bits = Machine.memory_region[2][i] & 0x0f;
			samples[2 * i + 1] = (char) (((bits << 4) | bits) + 0x80);
		}

		return AY8910_sh_start(_interface);
	} };



	public static WriteHandlerPtr cclimber_sample_rate_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		/* calculate the sampling frequency */
		sample_freq = SND_CLOCK / 4 / (256 - data);
	} };



	public static WriteHandlerPtr cclimber_sample_volume_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		sample_volume = data & 0x1f;
		sample_volume = (sample_volume << 3) | (sample_volume >> 2);
	} };



	public static WriteHandlerPtr cclimber_sample_trigger_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		int start, end;


		if (data == 0 || play_sound == 0)
			return;

		start = 64 * porta;
		end = start;

		/* find end of sample */
		while (end < 0x4000 && (samples[end] != 0xf7 || samples[end + 1] != 0x80))
			end += 2;

		osd_play_sample(1,samples,start,end - start,sample_freq,sample_volume,0);
	} };
}