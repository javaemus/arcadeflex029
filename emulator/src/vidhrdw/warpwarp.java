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

public class warpwarp
{

	public static CharPtr warpwarp_bulletsram = new CharPtr();

	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/


	public static VhUpdatePtr warpwarp_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int offs;


		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			if (dirtybuffer[offs] != 0)
			{
				int mx, my, sx, sy;

		/* Even if Pengo's screen is 28x36, the memory layout is 32x32. We therefore */
		/* have to convert the memory coordinates into screen coordinates. */
		/* Note that 32*32 = 1024, while 28*36 = 1008: therefore 16 bytes of Video RAM */
		/* don't map to a screen position. We don't check that here, however: range */
		/* checking is performed by drawgfx(). */

				mx = (offs / 32);
				my = (offs % 32);

				if (mx == 0)
				{
					sx = my;
					sy = 33;
				}
				else if (mx == 1)
				{
					sx = my;
					sy = 0;
				}
				else
				{
					sx = mx;
					sy = my+1;
				}
				sx = 32-sx;

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                                videoram.read(offs),
                                                colorram.read(offs)&0xF,
                                                0,0,8*sx ,8*sy,
                                                Machine.drv.visible_area,TRANSPARENCY_NONE,0);
				dirtybuffer[offs] = 0;
			}
		}

		/* copy the character mapped graphics */
		copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);

		{
			int x,y;
			int colour;
			colour = Machine.gfx[0].colortable.read(0x09);

			x = warpwarp_bulletsram.read(1);
			x += 8;
			if (x >= Machine.drv.visible_area.min_x && x <= Machine.drv.visible_area.max_x)
			{
				y = 256 - warpwarp_bulletsram.read();
                                if (y<256-16) {
				y += 5;
				if (y >= 0)
				{
					int j;

					for (j = 0; j < 2; j++)
					{
						bitmap.line[y+j][x+0] = (char)colour;
						bitmap.line[y+j][x+1] = (char)colour;
	/*					bitmap->line[y+j][x+2] = colour; */
					}
				}

                            }
                         }
		}
	} };
}