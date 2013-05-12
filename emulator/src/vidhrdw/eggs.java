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

import static mame.common.*;
import static mame.commonH.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static arcadeflex.osdepend.*;

public class eggs 
{       
        public static WriteHandlerPtr eggs_mirrorvideoram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                int x,y;


                /* swap x and y coordinates */
                x = offset / 32;
                y = offset % 32;
                offset = 32 * y + x;

                videoram_w.handler(offset,data);
        }};



        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        static int base;
        public static VhUpdatePtr eggs_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{

                int offs;
        
                if (osd_key_pressed(OSD_KEY_Z))
                {
                        while (osd_key_pressed(OSD_KEY_Z));
                        base -= 0x400;
                }
                if (osd_key_pressed(OSD_KEY_X))
                {
                        while (osd_key_pressed(OSD_KEY_X));
                        base += 0x400;
                }


                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if ((1!=0)||(dirtybuffer[offs]!=0))
                        {
                                int sx,sy;


                                dirtybuffer[offs] = 0;

                                sx = 8 * (offs % 32);
                                sy = 8 * (offs / 32);

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                                videoram.read(offs+base),
        0,//					colorram[offs],
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
                        if ((spriteram.read(offs + 0) & 0x01)!=0)
                                drawgfx(bitmap,Machine.gfx[1],
                                                spriteram.read(offs + 1),
                                                0,
                                                spriteram.read(offs + 0) & 0x02,spriteram.read(offs + 0) & 0x04,
                                                240 - spriteram.read(offs + 2),spriteram.read(offs + 3),
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                }
        }};
    
}
