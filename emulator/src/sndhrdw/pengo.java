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

import static arcadeflex.libc.*;
import static mame.driverH.*;
import static arcadeflex.osdepend.*;
import static mame.mame.*;

public class pengo
{


	static final int SND_CLOCK = 3072000;	/* 3.072 Mhz */


	public static CharPtr pengo_soundregs = new CharPtr();
	static int sound_enable;
	static int sound_changed;



	public static ShStartPtr rallyx_sh_start = new ShStartPtr() { public int handler()
	{
		sound_enable = 1;	/* start with sound enabled, Rally X doesn't have a sound enable register */
		return 0;
	} };



	public static WriteHandlerPtr pengo_sound_enable_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		sound_enable = data;
                if (sound_enable == 0)
                {
                    osd_adjust_sample(0,1000,0);
                    osd_adjust_sample(1,1000,0);
                    osd_adjust_sample(2,1000,0);
                }
	} };



	public static WriteHandlerPtr pengo_sound_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		data &= 0x0f;

		if (pengo_soundregs.read(offset) != data)
		{
			sound_changed = 1;
			pengo_soundregs.write(offset, data);
		}
	} };



	public static ShUpdatePtr pengo_sh_update = new ShUpdatePtr() { public void handler()
	{
	if (play_sound == 0) return;

            if ((sound_enable != 0) && (sound_changed != 0))
            {
		int voice;

                int[] currwave = { -1, -1, -1 };


		sound_changed = 0;

		for (voice = 0;voice < 3;voice++)
		{
			int freq,volume,wave;


			freq = pengo_soundregs.read(0x14 + 5 * voice);	/* always 0 */
			freq = freq * 16 + pengo_soundregs.read(0x13 + 5 * voice);
			freq = freq * 16 + pengo_soundregs.read(0x12 + 5 * voice);
			freq = freq * 16 + pengo_soundregs.read(0x11 + 5 * voice);
			if (voice == 0)
				freq = freq * 16 + pengo_soundregs.read(0x10 + 5 * voice);
			else freq = freq * 16;

			freq = (SND_CLOCK / 2048) * freq / 512;

			volume = pengo_soundregs.read(0x15 + 5 * voice);
			volume = (volume << 4) | volume;

			if (freq == 0)
			{
				freq = 1000;
				volume = 0;
			}

			wave = pengo_soundregs.read(0x05 + 5 * voice) & 7;
			if (wave != currwave[voice])
			{
				osd_play_sample(voice,Machine.drv.samples,wave * 32,32,freq,volume,1);
				currwave[voice] = wave;
			}
			else osd_adjust_sample(voice,freq,volume);
		}
            }
	} };
}

