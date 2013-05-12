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
/**
 *
 * @author shadow
 */
public class kungfum {
  public static CharPtr kungfum_scroll_low = new CharPtr();
  public static CharPtr kungfum_scroll_high = new CharPtr();

  static rectangle spritevisiblearea = new rectangle(0, 255, 80, 255);

        public static VhConvertColorPromPtr kungfum_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
                int i,j,used;
                char[] allocated = new char[3*256];

                /* The game has 512 colors, but we are limited to a maximum of 256. */
                /* Luckily, many of the colors are duplicated, so the total number of */
                /* different colors is less than 256. We select the unique colors and */
                /* put them in our palette. */

                memset(palette,0,3 * Machine.drv.total_colors);

                used = 0;
                for (i = 0;i < 512;i++)
                {
                        for (j = 0;j < used;j++)
                        {
                                if (allocated[j] == color_prom[i] &&
                                                allocated[j+256] == color_prom[i+512] &&
                                                allocated[j+2*256] == color_prom[i+2*512])
                                        break;
                        }
                        if (j == used)
                        {
                                int bit0,bit1,bit2,bit3;


                                used++;

                                allocated[j] = color_prom[i];
                                allocated[j+256] = color_prom[i+512];
                                allocated[j+2*256] = color_prom[i+2*512];

                                bit0 = (color_prom[i] >> 0) & 0x01;
                                bit1 = (color_prom[i] >> 1) & 0x01;
                                bit2 = (color_prom[i] >> 2) & 0x01;
                                bit3 = (color_prom[i] >> 3) & 0x01;
                                palette[3*j] = (char)(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                                bit0 = (color_prom[i+512] >> 0) & 0x01;
                                bit1 = (color_prom[i+512] >> 1) & 0x01;
                                bit2 = (color_prom[i+512] >> 2) & 0x01;
                                bit3 = (color_prom[i+512] >> 3) & 0x01;
                                palette[3*j + 1] = (char)(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                                bit0 = (color_prom[i+512*2] >> 0) & 0x01;
                                bit1 = (color_prom[i+512*2] >> 1) & 0x01;
                                bit2 = (color_prom[i+512*2] >> 2) & 0x01;
                                bit3 = (color_prom[i+512*2] >> 3) & 0x01;
                                palette[3*j + 2] = (char)(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                        }

                        colortable[i] = (char)j;
                }
        } };
       public static VhStartPtr kungfum_vh_start = new VhStartPtr() { public int handler()
        {
                if ((dirtybuffer = new char[videoram_size[0]]) == null)
                        return 1;
                memset(dirtybuffer,1,videoram_size[0]);

                /* Kung Fu Master has a virtual screen twice as large as the visible screen */
                if ((tmpbitmap = osd_create_bitmap(2 * Machine.drv.screen_width,Machine.drv.screen_height)) == null)
                {
                        dirtybuffer=null;
                        return 1;
                }

                return 0;
         } };
        public static VhStopPtr kungfum_vh_stop = new VhStopPtr() { public void handler()
        {
                dirtybuffer=null;
                osd_free_bitmap(tmpbitmap);
                tmpbitmap=null;
        } };
        public static VhUpdatePtr kungfum_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
        {
                int offs,i;


                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if (dirtybuffer[offs]!=0)
                        {
                                int sx,sy;


                                dirtybuffer[offs] = 0;

                                sx = 8 * (offs % 64);
                                sy = 8 * (offs / 64);

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                                videoram.read(offs) + 4 * (colorram.read(offs) & 0xc0),
                                                colorram.read(offs) & 0x1f,
                                                colorram.read(offs) & 0x20,0,
                                                sx,sy,
                                                null,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the temporary bitmap to the screen */
                {
                        int[] scroll=new int[32];


                        for (i = 0;i < 6;i++)
                                scroll[i] = -128;
                        for (i = 6;i < 32;i++)
                                scroll[i] = -(kungfum_scroll_low.read() + 256 * kungfum_scroll_high.read()) - 128;

                        copyscrollbitmap(bitmap,tmpbitmap,32,scroll,0,null,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }


                /* Draw the sprites. */
                for (offs = 0;offs < spriteram_size[0];offs += 8)
                {
                        int bank,k,code,col,flipx,sx,sy;
                        char sprite_height_prom[] =
                        {
                                /* B-5F - sprite height, one entry per 32 sprites */
                                0x00,0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x01,0x01,0x02,0x02,0x02,0x02,
                                0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x01,0x01,0x00,0x01,0x01,0x01,0x01,
                        };


                        bank = spriteram.read(offs+5) & 0x03;
                        code = spriteram.read(offs+4);

                        if (code != 0 || bank != 0)
                        {
                                col = spriteram.read(offs+0) & 0x1f;
                                flipx = spriteram.read(offs+5) & 0x40;
                                sx = (256 * spriteram.read(offs+7) + spriteram.read(offs+6)) - 128;
                                sy = 256+128-15 - (256 * spriteram.read(offs+3) + spriteram.read(offs+2));

                                k = sprite_height_prom[(256 * bank + code) / 32];
                                if (k == 1)	/* double height */
                                {
                                        code &= 0xfe;
                                        sy -= 16;
                                }
                                else if (k == 2)	/* quadruple height */
                                {
                                        k = 3;
                                        code &= 0xfc;
                                        sy -= 3*16;
                                }

                                do
                                {
                                        drawgfx(bitmap,Machine.gfx[1 + bank],
                                                        code + k,col,
                                                        flipx, 0,
                                                        sx,sy + 16 * k,
                                                        spritevisiblearea,TRANSPARENCY_PEN,0);

                                        k--;
                                } while (k >= 0);
                        }
                }
        }};
}

