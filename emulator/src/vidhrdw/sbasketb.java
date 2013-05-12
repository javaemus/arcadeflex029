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
 * 
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
import static arcadeflex.osdepend.*;
import static mame.osdependH.*;

import static vidhrdw.generic.*;
public class sbasketb {
        public static CharPtr sbasketb_scroll= new CharPtr();
        static rectangle scroll_area=new rectangle(0*8, 32*8-1, 0*8, 32*8-1);



        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr sbasketb_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                int offs;
                int sx,sy,attribute;


                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if (dirtybuffer[offs]!=0)
                        {
                                dirtybuffer[offs] = 0;

                                attribute = colorram.read(offs);

                                sx = 8 * (31 - offs / 32);
                                sy = 8 * (offs % 32);

                                drawgfx(tmpbitmap,Machine.gfx[(attribute & 0x20) >> 5],
                                                videoram.read(offs),
                                                0,
                                                attribute & 0x80,
                                                attribute & 0x40,
                                                sx,sy,
                                                scroll_area,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the temporary bitmap to the screen */
                {
                        int[] scroll=new int[32];

                        for (int i = 0;i < 6;i++)
                                scroll[i] = 0;

                        for (int i = 6;i < 32;i++)
                                scroll[i] = sbasketb_scroll.read();

                        copyscrollbitmap(bitmap,tmpbitmap,32,scroll,0,null,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }

                /* Draw the sprites. */
                for (offs = spriteram_size[0] - 16;offs >= 0;offs -= 16)
                {
                        if ((spriteram.read(offs+4)!=0) || (spriteram.read(offs+6)!=0))
                                drawgfx(bitmap,Machine.gfx[2],
                                                spriteram.read(offs + 0xe) | ((spriteram.read(offs + 0xf) & 0x20) << 3),
                                                0,
                                                spriteram.read(offs + 0xf) & 0x80,
                                                spriteram.read(offs + 0xf) & 0x40,
                                                spriteram.read(offs + 6),spriteram.read(offs + 4),
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                }
        }};
    
}
