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
import static arcadeflex.osdepend.*;
import static mame.driverH.*;
import static mame.mame.*;

/**
 *
 * @author shadow
 */
public class mappy {
  
  public static CharPtr mappy_soundregs = new CharPtr();
  static int sound_enable;
  static int sound_changed;

  public static WriteHandlerPtr mappy_sound_enable_w = new WriteHandlerPtr() { public void handler(int offset, int data)
  {
      	sound_enable = offset;
	if (sound_enable == 0)
	{
		osd_adjust_sample(0,1000,0);
		osd_adjust_sample(1,1000,0);
		osd_adjust_sample(2,1000,0);
		osd_adjust_sample(3,1000,0);
		osd_adjust_sample(4,1000,0);
		osd_adjust_sample(5,1000,0);
		osd_adjust_sample(6,1000,0);
		osd_adjust_sample(7,1000,0);
	}
  }  } ;

  public static WriteHandlerPtr mappy_sound_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
	if (mappy_soundregs.read(offset) != data)
	{
		sound_changed = 1;
		mappy_soundregs.write(offset, data);
	}
    }
  };
static int currwave[] = { -1,-1,-1,-1,-1,-1,-1,-1 };

  public static ShUpdatePtr mappy_sh_update = new ShUpdatePtr()
  {
    public void handler()
    {
      if (play_sound == 0) return;

         if ((mappy.sound_enable != 0) && (mappy.sound_changed != 0))
            {
                    int voice;


                    sound_changed = 0;

                    for (voice = 0;voice < 8;voice++)
                    {
                            int freq,volume,wave;

                            freq = mappy_soundregs.read(0x06 + 8 * voice) & 15;	/* high bits are from here */
                            freq = freq * 256 + mappy_soundregs.read(0x05 + 8 * voice);
                            freq = freq * 256 + mappy_soundregs.read(0x04 + 8 * voice);
                            freq = freq * 730 / 1000;	/* thanks to jrok for this */

                            volume = mappy_soundregs.read(0x03 + 8 * voice);
                            volume = (volume << 4) | volume;

                            if (freq == 0)
                            {
                                    freq = 1000;
                                    volume = 0;
                            }

                            wave = (mappy_soundregs.read(0x06 + 8 * voice) >> 4) & 7;
                            if (wave != currwave[voice])
                            {
                                    osd_play_sample(voice,Machine.drv.samples,wave * 32,32,freq,volume,1);
                                    currwave[voice] = wave;
                            }
                            else
                                    osd_adjust_sample(voice,freq,volume);
                    }
            }
      
    }
  };
}

