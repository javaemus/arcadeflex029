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

public class exedexes 
{
	public static int[] exedexes_backgroundram_size = new int[1];
	public static CharPtr exedexes_backgroundram = new CharPtr();
        public static CharPtr exedexes_bg_scroll = new CharPtr();
        public static CharPtr exedexes_nbg_yscroll= new CharPtr();
        public static CharPtr exedexes_nbg_xscroll= new CharPtr();
        static char []dirtybuffer2;
	static osd_bitmap tmpbitmap2;

        static int TileMap(int offs) {
            return Machine.memory_region[3][offs];
        }
        public static VhConvertColorPromPtr exedexes_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
                int i;


                for (i = 0;i < 256;i++)
                {
                        int bit0,bit1,bit2,bit3;


                        bit0 = (color_prom[i] >> 0) & 0x01;
                        bit1 = (color_prom[i] >> 1) & 0x01;
                        bit2 = (color_prom[i] >> 2) & 0x01;
                        bit3 = (color_prom[i] >> 3) & 0x01;
                        palette[3*i] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                        bit0 = (color_prom[i+256] >> 0) & 0x01;
                        bit1 = (color_prom[i+256] >> 1) & 0x01;
                        bit2 = (color_prom[i+256] >> 2) & 0x01;
                        bit3 = (color_prom[i+256] >> 3) & 0x01;
                        palette[3*i + 1] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                        bit0 = (color_prom[i+256*2] >> 0) & 0x01;
                        bit1 = (color_prom[i+256*2] >> 1) & 0x01;
                        bit2 = (color_prom[i+256*2] >> 2) & 0x01;
                        bit3 = (color_prom[i+256*2] >> 3) & 0x01;
                        palette[3*i + 2] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                }

                /* characters use colors 192-207 */
                for (i = 0;i < 64*4;i++)
                        colortable[i] = (char) (color_prom[i + 256*3] + 192);

                /* 8x? tiles use colors 0-15 */
                for (i = 64*4;i < 2*64*4;i++)
                        colortable[i] = (char) (color_prom[i + 256*3]);

                /* 16x16 tiles use colors 64-79 */
                for (i = 2*64*4;i < 2*64*4+16*16;i++)
                        colortable[i] = (char) (color_prom[i + 256*3] + 64);

                /* sprites use colors 128-192 in four banks */
                for (i = 2*64*4+16*16;i < 2*64*4+16*16+16*16;i++)
                        colortable[i] = (char) (color_prom[i + 256*3] + 128 + 16 * color_prom[i + 256*3 + 256]);
        }};



        /***************************************************************************

          Start the video hardware emulation.

        ***************************************************************************/
        public static VhInitPtr exedexes_vh_init = new VhInitPtr() { public int handler(String name)
        {
                if(Machine.memory_region[1]==null)
                {
                        printf("Region Erased!\n");
                        return 1;
                }

                return 0;
        }};
        public static VhStartPtr exedexes_vh_start = new VhStartPtr() { public int handler()
	{
                if (generic_vh_start.handler() != 0)
			return 1;
                if ((dirtybuffer2 = new char[exedexes_backgroundram_size[0]]) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}
                
                memset(dirtybuffer2,1,exedexes_backgroundram_size[0]);

                /* the background area is twice as tall as the screen */
                if ((tmpbitmap2 = osd_create_bitmap(Machine.drv.screen_width,2*Machine.drv.screen_height)) == null)
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
        public static VhStopPtr exedexes_vh_stop = new VhStopPtr() { public void handler()
	{
                osd_free_bitmap(tmpbitmap2);
		tmpbitmap2 = null;
		dirtybuffer2 = null;
		generic_vh_stop.handler();
        }};


        public static WriteHandlerPtr exedexes_background_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                if (exedexes_backgroundram.read(offset) != data)
                {
                        dirtybuffer2[offset] = 1;

                        exedexes_backgroundram.write(offset, data);
                }
        }};



        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr exedexes_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                int offs,sx,sy,x,y;


                for (sy = 0;sy < 32;sy++)
                {
                        for (sx = 0;sx < 16;sx++)
                        {
                                offs = 32 * (31 - sy) + sx;

                                if (dirtybuffer2[offs] != 0 || dirtybuffer2[offs + 16] != 0)
                                {
                                        dirtybuffer2[offs] = dirtybuffer2[offs + 16] = 0;

                                        drawgfx(tmpbitmap2,Machine.gfx[(exedexes_backgroundram.read(offs + 16) & 0x80)!=0 ? 2 : 1],
                                                        exedexes_backgroundram.read(offs),
                                                        (exedexes_backgroundram.read(offs + 16) & 0x1f),
                                                        0,0,
                                                        16 * sx,16 * sy,
                                                        null,TRANSPARENCY_NONE,0);
                                        drawgfx(tmpbitmap2,Machine.gfx[(exedexes_backgroundram.read(offs + 16) & 0x80)!=0 ? 2 : 1],
                                                        exedexes_backgroundram.read(offs),
                                                        (exedexes_backgroundram.read(offs + 16) & 0x1f),
                                                        0,1,
                                                        16 * sx,16 * sy+8,
                                                        null,TRANSPARENCY_NONE,0);
                                }
                        }
                }

                /* copy the background graphics */
                {
                        int bg_scroll;


                        bg_scroll = exedexes_bg_scroll.read(0) + 256 * exedexes_bg_scroll.read(1) - 256;

                        copyscrollbitmap(bitmap,tmpbitmap2,0,null,1,new int[] {bg_scroll},Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }

                for (y=0;y<=16;y++)
                {
                        for (x=0;x<16;x++)
                        {
                                int xo,yo,tile,Chr;
                                xo=((exedexes_nbg_xscroll.read(1))<<8)+exedexes_nbg_xscroll.read(0)+x*16;
                                yo=((exedexes_nbg_yscroll.read(1))<<8)+exedexes_nbg_yscroll.read(0)+y*16;

                                tile = ((yo & 0xf0) >> 4) + (xo & 0xF0) +
                                       (yo & 0x700) + ((xo & 0xe00) << 3);

                                Chr=TileMap(tile);
                                if(Chr!=0)
                                        drawgfx(bitmap,Machine.gfx[2],
                                                Chr,
                                                0,
                                                0,0,
                                                x*16-(xo&0xF),240-y*16+(yo&0xF),
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                        }
                }
                /* Draw the sprites. */
                for (offs = spriteram_size[0] - 32;offs >= 0;offs -= 32)
                {
                        drawgfx(bitmap,Machine.gfx[3],
                                        spriteram.read(offs),
                                        spriteram.read(offs + 1) & 0x0f,
                                        spriteram.read(offs + 1) & 0x20, spriteram.read(offs + 1) & 0x10,
                                        spriteram.read(offs + 2),240 - spriteram.read(offs + 3) + 0x10 * (spriteram.read(offs + 1) & 0x80),
                                        Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                }


                /* draw the frontmost playfield. They are characters, but draw them as sprites */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if (videoram.read(offs)!= 0x24 || colorram.read(offs)!= 0)		/* don't draw spaces */
                        {
                                //int sx,sy;


                                sx = 8 * (offs / 32);
                                sy = 8 * (31 - offs % 32);

                                /* Should only use trancparency when pen 0 is black */
                                drawgfx(bitmap,Machine.gfx[0],
                                                videoram.read(offs) + 2 * (colorram.read(offs) & 0x80),
                                                colorram.read(offs) & 0x3f,
                                                0,0,
                                                sx,sy,
                                                Machine.drv.visible_area,TRANSPARENCY_COLOR,207);
                        }
                }
        }};
}
