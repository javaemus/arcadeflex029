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

public class pooyan
{


	public static VhConvertColorPromPtr pooyan_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
		int i;


		for (i = 0;i < 256;i++)
		{
			int bits;


			bits = (i >> 5) & 0x07;
			palette[3*i] = (char) ((bits >> 1) | (bits << 2) | (bits << 5));
			bits = (i >> 2) & 0x07;
			palette[3*i + 1] = (char) ((bits >> 1) | (bits << 2) | (bits << 5));
			bits = (i >> 0) & 0x03;
			palette[3*i + 2] = (char) (bits | (bits >> 2) | (bits << 4) | (bits << 6));
		}

		for (i = 0;i < 512;i++)
			colortable[i] = (char) color_prom[i];
	} };


		
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VhUpdatePtr pooyan_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int offs;

			
		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = 0; offs < videoram_size[0]; offs++)
		{
			if (dirtybuffer[offs] != 0)
			{
				int sx, sy;

			
				dirtybuffer[offs] = 0;
	
				sx = 8 * (31 - offs / 32);
				sy = 8 * (offs % 32);
	
				drawgfx(tmpbitmap, Machine.gfx[0],
						videoram.read(offs) + 8 * (colorram.read(offs) & 0x20),
						colorram.read(offs) & 0x3f,
						colorram.read(offs) & 0x80, colorram.read(offs) & 0x40,
						sx, sy,
						Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
			}
		}

	
		/* copy the character mapped graphics */
		copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);

			
		/* Draw the sprites. Note that it is important to draw them exactly in this */
		/* order, to have the correct priorities. */
		for (offs = 0;offs < spriteram_size[0];offs += 2)
		{
			drawgfx(bitmap, Machine.gfx[1],
					spriteram.read(offs + 1),
					spriteram_2.read(offs) & 0x3f,
					spriteram_2.read(offs) & 0x80, (spriteram_2.read(offs) & 0x40) == 0 ? 1 : 0,
					spriteram_2.read(offs + 1), spriteram.read(offs),
					Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
		}
	} };
}