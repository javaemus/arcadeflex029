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

public class mario
{



	

	static int gfx_bank;
	public static CharPtr mario_sprite_palette = new CharPtr();



	public static VhStartPtr mario_vh_start = new VhStartPtr() { public int handler()
	{
		gfx_bank = 0;

		return generic_vh_start.handler();
	} };



	public static WriteHandlerPtr mario_gfxbank_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
		gfx_bank = data;
	} };



	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
	public static VhUpdatePtr mario_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int  offs;


		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			if (dirtybuffer[offs] != 0)
			{
				int sx,sy;
				int charcode;


				dirtybuffer[offs] = 0;

				sx = 8 * (31 - offs % 32);
				sy = 8 * (31 - offs / 32);

				charcode = videoram.read(offs) + 256 * gfx_bank;

				drawgfx(tmpbitmap, Machine.gfx[0],
						charcode, charcode >> 4,
						0, 0,
						sx, sy,
						Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
			}
		}


		/* copy the character mapped graphics */
		copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);


		/* Draw the sprites. */
		for (offs = 0;offs < spriteram_size[0];offs += 4)
		{
			if (spriteram.read(offs) != 0)
			{
				drawgfx(bitmap, Machine.gfx[1],
						spriteram.read(offs+2),
						(spriteram.read(offs+1) & 0x0f) + 0x10 * mario_sprite_palette.read(),
						spriteram.read(offs+1) & 0x80, spriteram.read(offs+1) & 0x40,
						248 - spriteram.read(offs+3), spriteram.read(offs) - 8,
						Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
			}
		}
	} };
}
