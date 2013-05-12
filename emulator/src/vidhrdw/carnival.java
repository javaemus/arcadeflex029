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

package vidhrdw;

import static arcadeflex.libc.*;
import static mame.common.*;
import static mame.commonH.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;

public class carnival
{



	public static CharPtr carnival_characterram = new CharPtr();
	static char dirtycharacter[] = new char[256];


        /***************************************************************************

          Convert the color PROMs into a more useable format.

          Carnival has one 32x8 palette PROM. The color code is taken from the three
          most significant bits of the character code, plus two additional palette
          bank bits which however are not used by Carnival.
          The palette PROM is connected to the RGB output this way:

          bit 7 -- 22 ohm resistor  -- RED   \
                -- 22 ohm resistor  -- BLUE  |  foreground
                -- 22 ohm resistor  -- GREEN /
                --
                -- 22 ohm resistor  -- RED   \
                -- 22 ohm resistor  -- BLUE  |  background
                -- 22 ohm resistor  -- GREEN /
          bit 0 --

        ***************************************************************************/
	public static VhConvertColorPromPtr carnival_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
            	int i;


	for (i = 0;i < 32;i++)
	{
		int bit;


		bit = (color_prom[i] >> 3) & 0x01;
		palette[3*2*i] = (char)(0xff * bit);
		bit = (color_prom[i] >> 1) & 0x01;
		palette[3*2*i + 1] = (char)(0xff * bit);
		bit = (color_prom[i] >> 2) & 0x01;
		palette[3*2*i + 2] = (char)(0xff * bit);

		bit = (color_prom[i] >> 7) & 0x01;
		palette[3*(2*i+1)] = (char)(0xff * bit);
		bit = (color_prom[i] >> 5) & 0x01;
		palette[3*(2*i+1) + 1] = (char)(0xff * bit);
		bit = (color_prom[i] >> 6) & 0x01;
		palette[3*(2*i+1) + 2] = (char)(0xff * bit);
	}

	/* characters use colors 128-143 */
	for (i = 0;i < 32*2;i++)
		colortable[i] = (char)i;
        }};



	public static WriteHandlerPtr carnival_characterram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (carnival_characterram.read(offset) != data)
		{
			dirtycharacter[(offset / 8) & 0xff] = 1;

			carnival_characterram.write(offset, data);
		}
	} };



	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
	public static VhUpdatePtr carnival_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int offs;


		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			int charcode;


			charcode = videoram.read(offs);

			if (dirtybuffer[offs] != 0 || dirtycharacter[charcode] != 0)
			{
				int sx, sy;


			/* decode modified characters */
				if (dirtycharacter[charcode] == 1)
				{

                                        decodechar(Machine.gfx[0],charcode,carnival_characterram,Machine.drv.gfxdecodeinfo[0].gfxlayout);
					dirtycharacter[charcode] = 2;
				}


				dirtybuffer[offs] = 0;

				sx = 8 * (offs / 32);
				sy = 8 * (31 - offs % 32);

                                drawgfx(tmpbitmap,Machine.gfx[0],
					charcode,
					charcode >> 5,
					0,0,
					sx + 16,sy,
					Machine.drv.visible_area,TRANSPARENCY_NONE,0);
			}
		}


		for (offs = 0;offs < 256;offs++)
		{
			if (dirtycharacter[offs] == 2) dirtycharacter[offs] = 0;
		}


		/* copy the character mapped graphics */
		copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
	} };
}
