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
import static arcadeflex.osdepend.*;
import static vidhrdw.generic.*;

public class starforc {
          public static CharPtr starforc_scrollx2 = new CharPtr(); 
          public static CharPtr starforc_scrolly2 = new CharPtr();
          public static CharPtr starforc_scrollx3 = new CharPtr();
          public static CharPtr starforc_scrolly3 = new CharPtr();
          public static CharPtr starforc_paletteram = new CharPtr();
          public static CharPtr starforc_tilebackground2 = new CharPtr();
          public static CharPtr starforc_tilebackground3 = new CharPtr();
          public static CharPtr starforc_tilebackground4 = new CharPtr();
          public static int[] starforc_bgvideoram_size = new int[1];
          static char[] dirtybuffer2;
          static char[] dirtybuffer3;
          static osd_bitmap tmpbitmap2;
          static osd_bitmap tmpbitmap3;
          static char[] dirtycolor = new char[48];/* keep track of modified colors */


        /***************************************************************************

          Convert the color PROMs into a more useable format.

          Star Force doesn't have colors PROMs, it uses RAM. The meaning of the bits are
          bit 7 -- Intensity
                -- Intensity
                -- Blue
                -- Blue
                -- Green
                -- Green
                -- Red
          bit 0 -- Red

        ***************************************************************************/
       public static VhConvertColorPromPtr starforc_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
       {
                int i;


                for (i = 0;i < 256;i++)
                {
                        int bits,intensity;


                        intensity = (i >> 6) & 0x03;
                        bits = (i >> 0) & 0x03;
                        palette[3*i] = (char)(0x44 * bits + 0x11 * intensity);
                        bits = (i >> 2) & 0x03;
                        palette[3*i + 1] = (char)(0x44 * bits + 0x11 * intensity);
                        bits = (i >> 4) & 0x03;
                        palette[3*i + 2] = (char)(0x44 * bits + 0x11 * intensity);
                }
        }};



        /***************************************************************************

          Start the video hardware emulation.

        ***************************************************************************/
       public static VhStartPtr starforc_vh_start = new VhStartPtr() { public int handler()
	{
                if (generic_vh_start.handler() != 0)
                        return 1;

                if ((dirtybuffer2 = new char[starforc_bgvideoram_size[0]]) == null)
                {
                        generic_vh_stop.handler();
                        return 1;
                }
                memset(dirtybuffer2,1,starforc_bgvideoram_size[0]);

                if ((dirtybuffer3 = new char[starforc_bgvideoram_size[0]]) == null)
                {
                        dirtybuffer2=null;
                        generic_vh_stop.handler();
                        return 1;
                }
                memset(dirtybuffer3,1,starforc_bgvideoram_size[0]);

                /* the background area is twice as large as the screen */
                if ((tmpbitmap2 = osd_create_bitmap(2*Machine.drv.screen_width,Machine.drv.screen_height)) == null)
                {
                        dirtybuffer3=null;
                        dirtybuffer2=null;
                        generic_vh_stop.handler();
                        return 1;
                }

                if ((tmpbitmap3 = osd_create_bitmap(2*Machine.drv.screen_width,Machine.drv.screen_height)) == null)
                {
                        osd_free_bitmap(tmpbitmap2);
                        tmpbitmap2=null;
                        dirtybuffer3=null;
                        dirtybuffer2=null;
                        generic_vh_stop.handler();
                        return 1;
                }

                return 0;
        }};


        /***************************************************************************

          Stop the video hardware emulation.

        ***************************************************************************/
       public static VhStopPtr starforc_vh_stop = new VhStopPtr() { public void handler()
	{
                osd_free_bitmap(tmpbitmap3);
                osd_free_bitmap(tmpbitmap2);
                tmpbitmap3=null;
                tmpbitmap2=null;
                dirtybuffer3=null;
                dirtybuffer2=null;
                generic_vh_stop.handler();
        }};


        public static WriteHandlerPtr starforc_tiles2_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                if (starforc_tilebackground2.read(offset) != data)
                {
                        dirtybuffer2[offset] = 1;

                        starforc_tilebackground2.write(offset, data);
                }
        }};
        public static WriteHandlerPtr starforc_tiles3_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{

                if (starforc_tilebackground3.read(offset) != data)
                {
                        dirtybuffer3[offset] = 1;

                        starforc_tilebackground3.write(offset, data);
                }
        }};
        public static WriteHandlerPtr starforc_tiles4_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{

                if (starforc_tilebackground4.read(offset) != data)
                {
                        dirtybuffer3[offset] = 1;

                        starforc_tilebackground4.write(offset, data);
                }
        }};



        public static WriteHandlerPtr starforc_paletteram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{

                if (starforc_paletteram.read(offset) != data)
                {
                        dirtycolor[offset / 8] = 1;

                        starforc_paletteram.write(offset, data);
                }
        }};



        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        static int[] colormap = { 0,2,4,6,1,3,5,7 };
        public static VhUpdatePtr starforc_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                int i,offs;


                /* rebuild the color lookup table */
                for (i = 0;i < 48*8;i++)
                {
                        int col;


                        col = starforc_paletteram.read(i);
                        /* avoid undesired transparency using dark gray instead of black */
                        if (col == 0x00 && i % 8 != 0) col = 0x40;
                        Machine.gfx[0].colortable.write(i,Machine.pens[col]);
                }

                for (offs = starforc_bgvideoram_size[0] - 1;offs >= 0;offs--)
                {
                        int col,code;
                        
                        if (dirtybuffer2[offs]!=0)
                        {
                                int sx,sy;

                                dirtybuffer2[offs] = 0;

                                sx = 31 - offs / 16;
                                sy = offs % 16;

                                drawgfx(tmpbitmap2,Machine.gfx[3],
                                                starforc_tilebackground2.read(offs),
                                                starforc_tilebackground2.read(offs) >> 5,
                                                0,0,
                                                16*sx,16*sy,
                                                null,TRANSPARENCY_NONE,0);
                        }


                        code = starforc_tilebackground4.read(offs);
                        col = colormap[code >> 5];

                        if (dirtybuffer3[offs]!=0)
                        {
                                int sx,sy;


                                dirtybuffer3[offs] = 0;

                                sx = 16 * (31 - offs / 16);
                                sy = 16 * (offs % 16);

                                drawgfx(tmpbitmap3,Machine.gfx[1],
                                                starforc_tilebackground3.read(offs),
                                                starforc_tilebackground3.read(offs) >> 5,
                                                0,0,
                                                sx,sy,
                                                null,TRANSPARENCY_NONE,0);

                                drawgfx(tmpbitmap3,Machine.gfx[2],
                                                code,
                                                col,
                                                0,0,
                                                sx,sy,
                                                null,TRANSPARENCY_PEN,0);
                        }
                        else if ((code!=0) && (dirtycolor[8+col]!=0))
                        {
                                int sx,sy;


                                sx = 16 * (31 - offs / 16);
                                sy = 16 * (offs % 16);

                                drawgfx(tmpbitmap3,Machine.gfx[2],
                                                code,
                                                col,
                                                0,0,
                                                sx,sy,
                                                null,TRANSPARENCY_PEN,0);
                        }
                }


                for (i = 0;i < 48;i++)
                        dirtycolor[i] = 0;


                {
                        int scrollx,scrolly;


                        scrollx = starforc_scrollx2.read(0) + 256 * starforc_scrollx2.read(1) + 256;
                        scrolly = -starforc_scrolly2.read(0);
                        copyscrollbitmap(bitmap,tmpbitmap2,1,new int[] {scrollx},1,new int[] {scrolly},Machine.drv.visible_area,TRANSPARENCY_NONE,0);

                        scrollx = starforc_scrollx3.read(0) + 256 * starforc_scrollx3.read(1) + 256;
                        scrolly = -starforc_scrolly3.read(0);
                        copyscrollbitmap(bitmap,tmpbitmap3,1,new int[] {scrollx},1,new int[] {scrolly},Machine.drv.visible_area,TRANSPARENCY_COLOR,0);
                }


                /* Draw the sprites. Note that it is important to draw them exactly in this */
                /* order, to have the correct priorities. */
                for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
                {
                        drawgfx(bitmap,Machine.gfx[((spriteram.read(offs) & 0xc0) == 0xc0) ? 5 : 4],
                                        spriteram.read(offs),
                                        spriteram.read(offs + 1) & 0x07,
                                        spriteram.read(offs + 1) & 0x80,spriteram.read(offs + 1) & 0x40,
                                        spriteram.read(offs + 2),spriteram.read(offs + 3),
                                        Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                }


                /* draw the frontmost playfield. They are characters, but draw them as sprites */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        int sx,sy;
                        int charcode;


                        charcode = videoram.read(offs) + 0x10 * (colorram.read(offs) & 0x10);

                        if (charcode!=0)	/* don't draw spaces */
                        {
                                sx = 8 * (31 - offs / 32);
                                sy = 8 * (offs % 32);

                                drawgfx(bitmap,Machine.gfx[0],
                                                charcode,
                                                colorram.read(offs) & 0x07,
                                                0,0,
                                                sx,sy,
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                        }
                }
        }};    
}
