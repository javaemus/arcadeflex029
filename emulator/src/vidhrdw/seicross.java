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

public class seicross
{
    public static CharPtr seicross_row_scroll = new CharPtr();
    
        public static WriteHandlerPtr seicross_colorram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                if (colorram.read(offset) != data)
                {
                        /* bit 5 of the address is not used for color memory. There is just */
                        /* 512k of memory; every two consecutive rows share the same memory */
                        /* region. */
                        offset &= 0xffdf;

                        dirtybuffer[offset] = 1;
                        dirtybuffer[offset + 0x20] = 1;

                        colorram.write(offset, data);
                        colorram.write(offset + 0x20,data);
                }
        }};
        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr seicross_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
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

                                sx = 8 * (31 - offs / 32);
                                sy = 8 * (offs % 32);

                                drawgfx(tmpbitmap,Machine.gfx[(colorram.read(offs) & 0x10) != 0 ? 1 : 0],
                                                videoram.read(offs) + 8 * (colorram.read(offs) & 0x20),
                                                colorram.read(offs) & 0x0f,
                                                colorram.read(offs) & 0x40,colorram.read(offs) & 0x80,
                                                sx,sy,
                                                null,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the temporary bitmap to the screen */
                {
                        int scroll[]=new int[32];


                        for (offs = 0;offs < 32;offs++)
                                scroll[offs] = seicross_row_scroll.read(offs);

                        copyscrollbitmap(bitmap,tmpbitmap,32,scroll,0,null,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }

                /* draw sprites */
                for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
                {
                        drawgfx(bitmap,Machine.gfx[(spriteram.read(offs + 1) & 0x10) !=0 ? 4 : 3],
                                        (spriteram.read(offs) & 0x3f),
                                        spriteram.read(offs + 1) & 0x0f,
                                        spriteram.read(offs) & 0x80,spriteram.read(offs) & 0x40,
                                        spriteram.read(offs + 2) + 1,spriteram.read(offs + 3),
                                        Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                }

        }};
}
