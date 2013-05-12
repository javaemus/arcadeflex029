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
import static arcadeflex.osdepend.*;
import static mame.common.*;
import static mame.commonH.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;

public class milliped {
    public static VhUpdatePtr milliped_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
    {
            int offs;


            /* for every character in the Video RAM, check if it has been modified */
            /* since last time and update it accordingly. */
            for (offs = videoram_size[0] - 1;offs >= 0;offs--)
            {
                    if (dirtybuffer[offs]!=0)
                    {
                            int sx,sy;
             int Add = 0x40;

                            dirtybuffer[offs] = 0;

                            sx = (offs / 32);
                            sy = (31 - offs % 32);

             if ((videoram.read(offs) & 0x40)!=0)	/* not used by Centipede */
                Add = 0xC0;

             drawgfx(tmpbitmap,Machine.gfx[0],
                                            (videoram.read(offs) & 0x3f) + Add,
                                            0,
                                            0,0,8*(sx+1),8*sy,
                                            Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                    }
            }


            /* copy the temporary bitmap to the screen */
            copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);


            /* Draw the sprites */
            for (offs = 0;offs < 0x10;offs++)
            {
                    if (spriteram.read(offs + 0x20) < 0xf8)
                    {
                            int spritenum;


                            spritenum = spriteram.read(offs) & 0x3f;
                            if ((spritenum & 1)!=0) spritenum = spritenum / 2 + 64;
                            else spritenum = spritenum / 2;

                            drawgfx(bitmap,Machine.gfx[1],
                                            spritenum,
    0,/*					spriteram[offs + 0x30],*/
                                            spriteram.read(offs) & 0x80,0,
                                            248 - spriteram.read(offs + 0x10),248 - spriteram.read(offs + 0x20),
                                            Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                    }
            }
    }};
}
