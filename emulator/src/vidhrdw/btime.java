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

public class btime
{
	static int background_image;

	public static VhConvertColorPromPtr btime_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
		int i;


		for (i = 0; i < 16; i++)
		{
			int bit0, bit1, bit2;


			bit0 = (~color_prom[i] >> 0) & 0x01;
			bit1 = (~color_prom[i] >> 1) & 0x01;
			bit2 = (~color_prom[i] >> 2) & 0x01;
			palette[3*i] = (char) (0x2e * bit0 + 0x41 * bit1 + 0x90 * bit2);
			bit0 = (~color_prom[i] >> 3) & 0x01;
			bit1 = (~color_prom[i] >> 4) & 0x01;
			bit2 = (~color_prom[i] >> 5) & 0x01;
			palette[3*i + 1] = (char) (0x2e * bit0 + 0x41 * bit1 + 0x90 * bit2);
			bit0 = 0;
			bit1 = (~color_prom[i] >> 6) & 0x01;
			bit2 = (~color_prom[i] >> 7) & 0x01;
			palette[3*i + 2] = (char) (0x2e * bit0 + 0x41 * bit1 + 0x90 * bit2);
		}

		for (i = 0;i < 2 * 8;i++)
			colortable[i] = (char) i;
	} };



	public static WriteHandlerPtr btime_background_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
            if (background_image != data)
            {
		memset(dirtybuffer,1,videoram_size[0]);

		background_image = data;

		/* kludge to make the sprites disappear when the screen is cleared */
		if (data == 0)
		{
			int i;


			for (i = 0;i < spriteram_size[0];i += 4)
				spriteram.write(i,0);
		}
	}
	} };



	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
	public static VhUpdatePtr btime_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int offs;


		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			if (dirtybuffer[offs] != 0)
			{
				int sx, sy;


				dirtybuffer[offs] = 0;

				sx = 8 * (offs % 32);
				sy = 8 * (offs / 32);

				/* draw the background (this can be handled better) */
				if ((background_image & 0x10) != 0)
				{
					rectangle clip = new rectangle();
					int bx, by;
					int base, bgoffs;
					/* kludge to get the correct background */
					int mapconvert[] = { 1,2,3,0,5,6,7,4 };


					clip.min_x = sx;
					clip.max_x = sx+7;
					clip.min_y = sy;
					clip.max_y = sy+7;

					bx = sx & 0xf0;
					by = sy & 0xf0;

					base = 0x100 * mapconvert[(background_image & 0x07)];
					bgoffs = base+16*(by/16)+bx/16;

					drawgfx(tmpbitmap, Machine.gfx[2],
							Machine.memory_region[2][bgoffs],
							0,
							0, 0,
							bx, by,
							clip, TRANSPARENCY_NONE, 0);

					drawgfx(tmpbitmap, Machine.gfx[(colorram.read(offs) & 2) != 0 ? 1 : 0],
							videoram.read(offs) + 256 * (colorram.read(offs) & 1),
							0,
							0, 0,
							sx, sy,
							Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
				}
				else
					drawgfx(tmpbitmap, Machine.gfx[(colorram.read(offs) & 2) != 0 ? 1 : 0],
							videoram.read(offs) + 256 * (colorram.read(offs) & 1),
							0,
							0, 0,
							sx, sy,
							Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
			}
		}


		/* copy the temporary bitmap to the screen */
		copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);


		/* Draw the sprites */
		for (offs = 0;offs < spriteram_size[0];offs += 4)
		{
			if ((spriteram.read(offs) & 0x01) != 0)
			{
				drawgfx(bitmap, Machine.gfx[3],
						spriteram.read(offs+1),
						0,
						spriteram.read(offs) & 0x02, 0,
						239 - spriteram.read(offs+2), spriteram.read(offs+3),
						Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
			}
		}
	} };
}
