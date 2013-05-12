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
import static mame.osdependH.*;
import static arcadeflex.osdepend.*;
import static vidhrdw.generic.*;


public class espial {
    public static CharPtr espial_attributeram= new CharPtr();
    public static CharPtr espial_column_scroll= new CharPtr();


    public static VhConvertColorPromPtr espial_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
    {
            int i;


            for (i = 0;i < 32;i++)
            {
                    int bit0,bit1,bit2;


                    bit0 = (color_prom[i] >> 0) & 0x01;
                    bit1 = (color_prom[i] >> 1) & 0x01;
                    bit2 = (color_prom[i] >> 2) & 0x01;
                    palette[3*i + 0] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                    bit0 = (color_prom[i] >> 3) & 0x01;
                    bit1 = (color_prom[i] >> 4) & 0x01;
                    bit2 = (color_prom[i] >> 5) & 0x01;
                    palette[3*i + 1] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                    bit0 = 0;
                    bit1 = (color_prom[i] >> 6) & 0x01;
                    bit2 = (color_prom[i] >> 7) & 0x01;
                    palette[3*i + 2] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
            }


            for (i = 0;i < 4 * 8;i++)
            {
                    if ((i & 3)!=0) colortable[i] = (char)i;
                    else colortable[i] = 0;
            }
    }};


    public static WriteHandlerPtr espial_attributeram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
    {
            if (espial_attributeram.read(offset) != data)
            {
                    espial_attributeram.write(offset,data);
                    memset(dirtybuffer,1,videoram_size[0]);
            }
    }};



    /***************************************************************************

      Draw the game screen in the given osd_bitmap.
      Do NOT call osd_update_display() from this function, it will be called by
      the main emulation engine.

    ***************************************************************************/
    public static VhUpdatePtr espial_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
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

                            sx = offs % 32;
                            sy = offs / 32;

                            drawgfx(tmpbitmap,Machine.gfx[0],
                                            videoram.read(offs) + 256*(espial_attributeram.read(offs) & 0x03),
                                            colorram.read(offs),
                                            espial_attributeram.read(offs) & 0x04,espial_attributeram.read(offs) & 0x08,
                                            8*sx,8*sy,
                                            null,TRANSPARENCY_NONE,0);
                    }
            }


            /* copy the temporary bitmap to the screen */
            {
                    int[] scroll=new int[32];


                    for (offs = 0;offs < 32;offs++)
                            scroll[offs] = -espial_column_scroll.read(offs);

                    copyscrollbitmap(bitmap,tmpbitmap,0,null,32,scroll,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
            }


            /* Draw the sprites. Note that it is important to draw them exactly in this */
            /* order, to have the correct priorities. */
            for (offs = 0;offs < spriteram_size[0]/2;offs++)
            {
                    int sx,sy,code,color,flipx,flipy;


                    sx = spriteram.read(offs + 16);
                    sy = 240 - spriteram_2.read(offs);
                    code = spriteram.read(offs) >> 1;
                    color = spriteram_2.read(offs + 16);
                    flipx = spriteram_3.read(offs) & 0x04;
                    flipy = spriteram_3.read(offs) & 0x08;

                    if ((spriteram.read(offs) & 1)!=0)	/* double height */
                    {
                            drawgfx(bitmap,Machine.gfx[1],
                                            code,
                                            color,
                                            flipx,flipy,
                                            sx,sy - 16,
                                            Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                            drawgfx(bitmap,Machine.gfx[1],
                                            code + 1,
                                            color,
                                            flipx,flipy,
                                            sx,sy,
                                            Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                    }
                    else
                            drawgfx(bitmap,Machine.gfx[1],
                                            code,
                                            color,
                                            flipx,flipy,
                                            sx,sy,
                                            Machine.drv.visible_area,TRANSPARENCY_PEN,0);
            }
    }};    
}
