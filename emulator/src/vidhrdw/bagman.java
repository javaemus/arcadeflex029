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

public class bagman
{





	public static CharPtr bagman_video_enable = new CharPtr();
        static int[] flipscreen=new int[2];

	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
         public static WriteHandlerPtr bagman_flipscreen_w = new WriteHandlerPtr() { public void handler(int offset, int data)
        {
                if ((data & 1) != flipscreen[offset])
                {
                        flipscreen[offset] = data & 1;
                        memset(dirtybuffer,1,videoram_size[0]);
                }
        }};
	public static VhUpdatePtr bagman_vh_screenrefresh = new VhUpdatePtr() {	public void handler(osd_bitmap bitmap)
	{
		int  offs;


		if (bagman_video_enable.read() == 0)
		{
			clearbitmap(bitmap);

			return;
		}


		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if (dirtybuffer[offs]!=0)
                        {
                                int sx,sy;


                                dirtybuffer[offs] = 0;

                                sx = offs % 32;
                                if (flipscreen[0]!=0) sx = 31 - sx;
                                sy = offs / 32;
                                if (flipscreen[1]!=0) sy = 31 - sy;

                                drawgfx(tmpbitmap,Machine.gfx[(colorram.read(offs) & 0x10)!=0 ? 1 : 0],
                                                videoram.read(offs) + 8 * (colorram.read(offs) & 0x20),
                                                colorram.read(offs) & 0x0f,
                                                flipscreen[0],flipscreen[1],
                                                8*sx,8*sy,
                                                Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the character mapped graphics */
                copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);


                /* Draw the sprites. */
                for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
                {
                        int sx,sy,flipx,flipy;


                        sx = spriteram.read(offs+3);
                        sy = 240 - spriteram.read(offs+2);
                        flipx = spriteram.read(offs) & 0x40;
                        flipy = spriteram.read(offs) & 0x80;
                        if (flipscreen[0]!=0)
                        {
                                sx = 240 - sx +1;	/* compensate misplacement */
                                flipx = NOT(flipx);
                        }
                        if (flipscreen[1]!=0)
                        {
                                sy = 240 - sy;
                                flipy = NOT(flipy);
                        }

                        if ((spriteram.read(offs+2)!=0) && (spriteram.read(offs+3)!=0))
                                drawgfx(bitmap,Machine.gfx[2],
                                                (spriteram.read(offs) & 0x3f) + 2 * (spriteram.read(offs+1) & 0x20),
                                                spriteram.read(offs+1) & 0x1f,
                                                flipx,flipy,
                                                sx,sy+1,	/* compensate misplacement */
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                }
	} };
}