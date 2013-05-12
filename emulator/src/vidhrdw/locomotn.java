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


public class locomotn {
          /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr locomotn_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                int offs;


                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if (dirtybuffer[offs]!=0)
                        {
                                int sx,sy;


                                dirtybuffer[offs] = 0;

                                if (offs < 0x400)
                                {
                                                        sx = 8 * (offs / 32);
                                                        sy = 8 * (7 - offs % 32);
                                }
                                else
                                {
                                                        sx = 8 * ((offs - 0x400) / 32);
                                                        sy = 8 * (35 - offs % 32);
                                }

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                                (videoram.read(offs)&0x7f) + 2*(colorram.read(offs)&0x40) + 2*(videoram.read(offs)&0x80),
                                                colorram.read(offs) & 0x3f,
                                        /* not a mistake, one bit selects both  flips */
                                                ~colorram.read(offs) & 0x80,~colorram.read(offs) & 0x80,
                                                sx,sy,
                                                Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the character mapped graphics */
                copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);


                /* Draw the sprites. Note that it is important to draw them exactly in this */
                /* order, to have the correct priorities. */
                for (offs = 0;offs < spriteram_size[0];offs += 2)
                {
                        if (spriteram_2.read(offs + 1) > 16)
                                drawgfx(bitmap,Machine.gfx[1],
                                                ((spriteram_2.read(offs) >> 2) & 0x1f) + 0x20*(spriteram_2.read(offs) & 0x01),
                                                spriteram.read(offs + 1),
                                                0,0,
                                                spriteram.read(offs) - 16,spriteram_2.read(offs + 1) + 32,
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                }
        }};
}
