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

public class frogger
{



	

	public static CharPtr frogger_attributesram = new CharPtr();


		
	/***************************************************************************

	  Convert the color PROMs into a more useable format.

	  Frogger has one 32 bytes palette PROM, connected to the RGB output
	  this way:

	  bit 7 -- 220 ohm resistor  -- BLUE
			-- 470 ohm resistor  -- BLUE
			-- 220 ohm resistor  -- GREEN
			-- 470 ohm resistor  -- GREEN
			-- 1  kohm resistor  -- GREEN
			-- 220 ohm resistor  -- RED
			-- 470 ohm resistor  -- RED
	  bit 0 -- 1  kohm resistor  -- RED

	  Additionally, there is a bit which is 1 in the upper half of the display
	  (136 lines? I'm not sure of the exact value); it is connected to blue
	  through a 470 ohm resistor. It is used to make the river blue instead of
	  black.

	***************************************************************************/
	public static VhConvertColorPromPtr frogger_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
		int i;


		for (i = 0; i < 32; i++)
		{
			int bit0, bit1, bit2;


			bit0 = (color_prom[i] >> 0) & 0x01;
			bit1 = (color_prom[i] >> 1) & 0x01;
			bit2 = (color_prom[i] >> 2) & 0x01;
			palette[3*i] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
			bit0 = (color_prom[i] >> 3) & 0x01;
			bit1 = (color_prom[i] >> 4) & 0x01;
			bit2 = (color_prom[i] >> 5) & 0x01;
			palette[3*i + 1] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
			bit0 = 0;
			bit1 = (color_prom[i] >> 6) & 0x01;
			bit2 = (color_prom[i] >> 7) & 0x01;
			palette[3*i + 2] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
		}

		/* use an otherwise unused pen for the river background */
		palette[3*4] = 0;
		palette[3*4 + 1] = 0;
		palette[3*4 + 2] = 0x47;

		/* normal */
		for (i = 0;i < 4 * 8;i++)
		{
			if ((i & 3) != 0) colortable[i] = (char) i;
			else colortable[i] = 0;
		}
		/* blue background (river) */
		for (i = 4 * 8; i < 4 * 16; i++)
		{
			if ((i & 3) != 0) colortable[i] = (char) (i - 4 * 8);
			else colortable[i] = 4;
		}
	} };



	public static WriteHandlerPtr frogger_attributes_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if ((offset & 1) != 0 && frogger_attributesram.read(offset) != data)
		{
			int i;

	
			for (i = offset / 2;i < videoram_size[0];i += 32)
				dirtybuffer[i] = 1;
		}
	
		frogger_attributesram.write(offset, data);
	} };



	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
	public static VhUpdatePtr frogger_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int i, offs;


		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			if (dirtybuffer[offs] != 0)
			{
				int sx, sy;


				dirtybuffer[offs] = 0;

				sx = (31 - offs / 32);
				sy = (offs % 32);

				drawgfx(tmpbitmap, Machine.gfx[0],
						videoram.read(offs),
						frogger_attributesram.read(2 * sy + 1) + (sy < 17 ? 8 : 0),
						0, 0, 8 * sx, 8 * sy,
						null, TRANSPARENCY_NONE, 0);
			}
		}


		/* copy the temporary bitmap to the screen */
		{
			int scroll[] = new int[32];
			int s;


			for (i = 0; i < 32; i++)
			{
				s = frogger_attributesram.read(2 * i);
				scroll[i] = ((s << 4) & 0xf0) | ((s >> 4) & 0x0f);
			}

			copyscrollbitmap(bitmap, tmpbitmap, 32, scroll, 0, null, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
		}


		/* Draw the sprites. Note that it is important to draw them exactly in this */
	        /* order, to have the correct priorities. */
	        for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
		{
			if (spriteram.read(offs + 3) != 0)
			{
				int x;


				x = spriteram.read(offs);
				x = ((x << 4) & 0xf0) | ((x >> 4) & 0x0f);

				drawgfx(bitmap, Machine.gfx[1],
						spriteram.read(offs + 1) & 0x3f,
						spriteram.read(offs + 2),
						spriteram.read(offs + 1) & 0x80, spriteram.read(offs + 1) & 0x40,
						x, spriteram.read(offs + 3),
						Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
			}
		}
	} };
}
