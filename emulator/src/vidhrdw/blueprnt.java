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

public class blueprnt 
{
        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr blueprnt_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
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

                                sx = 8 * (offs % 32);
                                sy = 8 * (offs / 32);

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                                videoram.read(offs),
                                                colorram.read(offs) & 0x7f,
                                                0,0,
                                                sx,sy,
                                                Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the character mapped graphics */
                copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);


                /* Draw the sprites */
                for (offs = 0;offs < spriteram_size[0];offs += 4)
                {
                        drawgfx(bitmap,Machine.gfx[1],
                                        spriteram.read(offs + 1),
                                        0,
                                        spriteram.read(offs + 2 - 4) & 0x80,0,	/* -4? Awkward, isn't it? */
                                        239-spriteram.read(offs + 0),246-spriteram.read(offs + 3),
                                        Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                }


                /* redraw the characters which have priority over sprites */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if ((colorram.read(offs) & 0x80)!=0)
                        {
                                int sx,sy;


                                sx = 8 * (offs % 32);
                                sy = 8 * (offs / 32);

                                drawgfx(bitmap,Machine.gfx[0],
                                                videoram.read(offs),
                                                colorram.read(offs) & 0x7f,
                                                0,0,
                                                sx,sy,
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                        }
                }
        }};
}
