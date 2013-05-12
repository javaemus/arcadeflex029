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

public class vulgus {
          public static int[] vulgus_bgvideoram_size = new int[1];
          public static CharPtr vulgus_bgvideoram = new CharPtr();
          public static CharPtr vulgus_bgcolorram = new CharPtr();
          public static CharPtr vulgus_scrolllow = new CharPtr();
          public static CharPtr vulgus_scrollhigh = new CharPtr();
          static char[] dirtybuffer2;
          static osd_bitmap tmpbitmap2;
          /***************************************************************************

          Convert the color PROMs into a more useable format.

          This function comes from 1942. It is yet unknown how Vulgus works.

        ***************************************************************************/
          public static VhConvertColorPromPtr vulgus_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	 {
                int i;


                for (i = 0;i < 256;i++)
                {
                        int bit0,bit1,bit2,bit3;


                        bit0 = (color_prom[i] >> 0) & 0x01;
                        bit1 = (color_prom[i] >> 1) & 0x01;
                        bit2 = (color_prom[i] >> 2) & 0x01;
                        bit3 = (color_prom[i] >> 3) & 0x01;
                        palette[3*i] = (char)(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                        bit0 = (color_prom[i+256] >> 0) & 0x01;
                        bit1 = (color_prom[i+256] >> 1) & 0x01;
                        bit2 = (color_prom[i+256] >> 2) & 0x01;
                        bit3 = (color_prom[i+256] >> 3) & 0x01;
                        palette[3*i + 1] = (char)(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                        bit0 = (color_prom[i+256*2] >> 0) & 0x01;
                        bit1 = (color_prom[i+256*2] >> 1) & 0x01;
                        bit2 = (color_prom[i+256*2] >> 2) & 0x01;
                        bit3 = (color_prom[i+256*2] >> 3) & 0x01;
                        palette[3*i + 2] = (char)(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                }

                /* characters use colors 128-143 */
                for (i = 0;i < 64*4;i++)
                        colortable[i] = (char)(color_prom[i + 256*3] + 128);

                /* sprites use colors 64-79 */
                for (i = 64*4;i < 64*4+16*16;i++)
                        colortable[i] = (char)(color_prom[i + 256*3] + 64);

                /* background tiles use colors 0-63 in four banks */
                for (i = 64*4+16*16;i < 64*4+16*16+32*8;i++)
                {
                        colortable[i] = color_prom[i + 256*3];
                        colortable[i+32*8] = (char)(color_prom[i + 256*3] + 16);
                        colortable[i+2*32*8] = (char)(color_prom[i + 256*3] + 32);
                        colortable[i+3*32*8] = (char)(color_prom[i + 256*3] + 48);
                }
        }};



        /***************************************************************************

          Start the video hardware emulation.

        ***************************************************************************/
        public static VhStartPtr vulgus_vh_start = new VhStartPtr() { public int handler()
	{
                if (generic_vh_start.handler() != 0)
                        return 1;
                
                if ((dirtybuffer2 = new char[vulgus_bgvideoram_size[0]]) == null)
		{
                        generic_vh_stop.handler();
                        return 1;
                }
                memset(dirtybuffer2,1,vulgus_bgvideoram_size[0]);

                /* the background area is twice as tall and twice as large as the screen */
                if ((tmpbitmap2 = osd_create_bitmap(2*Machine.drv.screen_width,2*Machine.drv.screen_height)) == null)
                {
                        dirtybuffer2 = null;
			generic_vh_stop.handler();
                        return 1;
                }

                return 0;
        }};



        /***************************************************************************

          Stop the video hardware emulation.

        ***************************************************************************/
	public static VhStopPtr vulgus_vh_stop = new VhStopPtr() { public void handler()
	{
		osd_free_bitmap(tmpbitmap2);
		tmpbitmap2 = null;
		dirtybuffer2 = null;
		generic_vh_stop.handler();
	} };

        public static WriteHandlerPtr vulgus_bgvideoram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                if (vulgus_bgvideoram.read(offset) != data)
                {
                        dirtybuffer2[offset] = 1;

                        vulgus_bgvideoram.write(offset,data);
                }
        }};


        public static WriteHandlerPtr vulgus_bgcolorram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                if (vulgus_bgcolorram.read(offset) != data)
                {
                        dirtybuffer2[offset] = 1;

                        vulgus_bgcolorram.write(offset,data);
                }
        }};



        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr vulgus_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                int offs;


                for (offs = vulgus_bgvideoram_size[0] - 1;offs >= 0;offs--)
                {
                        int sx,sy;


                        if (dirtybuffer2[offs]!=0)
                        {
                                dirtybuffer2[offs] = 0;

                                sx = offs % 32;
                                sy = 31 - offs / 32;

                                drawgfx(tmpbitmap2,Machine.gfx[((vulgus_bgcolorram.read(offs) & 0x80)!=0) ? 2 : 1],
                                                vulgus_bgvideoram.read(offs),
                                                vulgus_bgcolorram.read(offs) & 0x1f,
                                                vulgus_bgcolorram.read(offs) & 0x40,vulgus_bgcolorram.read(offs) & 0x20,
                                                16 * sx,16 * sy,
                                                null,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the background graphics */
                {
                        int scrollx,scrolly;


                        scrollx = -(vulgus_scrolllow.read(0) + 256 * vulgus_scrollhigh.read(0));
                        scrolly = vulgus_scrolllow.read(1) + 256 * vulgus_scrollhigh.read(1) - 256;

                        copyscrollbitmap(bitmap,tmpbitmap2,1,new int[] {scrollx},1,new int[] {scrolly},Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }


                /* Draw the sprites. */
                for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
                {
                        int bank,i,code,col,sx,sy;


                        bank = 3;
                        if ((spriteram.read(offs) & 0x80)!=0) bank++;

                        code = spriteram.read(offs) & 0x7f;
                        col = spriteram.read(offs + 1) & 0x0f;
                        sx = spriteram.read(offs + 2);
                        sy = 240 - spriteram.read(offs + 3) + 0x10 * (spriteram.read(offs + 1) & 0x10);

                        i = (spriteram.read(offs + 1) & 0xc0) >> 6;
                        if (i == 2) i = 3;

                        do
                        {
                                drawgfx(bitmap,Machine.gfx[bank],
                                                code + i,col,
                                                0, 0,
                                                sx + 16 * i,sy,
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,15);

                                i--;
                        } while (i >= 0);
                }


                /* draw the frontmost playfield. They are characters, but draw them as sprites */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if (videoram.read(offs) != 0x20)	/* don't draw spaces */
                        {
                                int sx,sy;


                                sx = 8 * (offs / 32);
                                sy = 8 * (31 - offs % 32);

                                drawgfx(bitmap,Machine.gfx[0],
                                                videoram.read(offs) + 2 * (colorram.read(offs) & 0x80),
                                                colorram.read(offs) & 0x0f,
                                                0,0,sx,sy,
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                        }
                }
        }};  
}
