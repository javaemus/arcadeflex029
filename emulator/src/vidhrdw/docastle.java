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
import static arcadeflex.osdepend.*;
public class docastle {
    static osd_bitmap tmpbitmap1;
    
        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
 
        public static void convert_color_prom(char []palette_table, char []colortable, char []color_prom_table,int priority)
        {
                int i,j;
               // #define TOTAL_COLORS(gfxn) (Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity)
               // #define COLOR(gfxn,offs) (colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + offs])

                CharPtr color_prom = new CharPtr(color_prom_table);
                CharPtr palette= new CharPtr(palette_table);
               for (i = 0;i < Machine.drv.total_colors;i++)
                {
                        int bit0,bit1,bit2;


                        /* red component */
                        bit0 = (color_prom.read() >> 5) & 0x01;
                        bit1 = (color_prom.read() >> 6) & 0x01;
                        bit2 = (color_prom.read() >> 7) & 0x01;
                        palette.writeinc(0x23 * bit0 + 0x4b * bit1 + 0x91 * bit2);
                        /* green component */
                        bit0 = (color_prom.read() >> 2) & 0x01;
                        bit1 = (color_prom.read() >> 3) & 0x01;
                        bit2 = (color_prom.read() >> 4) & 0x01;
                        palette.writeinc(0x23 * bit0 + 0x4b * bit1 + 0x91 * bit2);
                        /* blue component */
                        bit0 = 0;
                        bit1 = (color_prom.read() >> 0) & 0x01;
                        bit2 = (color_prom.read() >> 1) & 0x01;
                        palette.writeinc(0x23 * bit0 + 0x4b * bit1 + 0x91 * bit2);

                        color_prom.inc();
                }


                /* characters */
                /* characters have 4 bitplanes, but they actually have only 8 colors. The fourth */
                /* plane is used to select priority over sprites. The meaning of the high bit is */
                /* reversed in Do's Castle wrt the other games. */

                /* first create a table with all colors, used to draw the background */
                for (i = 0;i < 32;i++)
                {
                        for (j = 0;j < 8;j++)
                        {
                                colortable[16*i+j] = (char)(8*i+j);
                                colortable[16*i+j+8] = (char)(8*i+j);
                        }
                }
                /* now create a table with only the colors which have priority over sprites, used */
                /* to draw the foreground. */
                for (i = 0;i < 32;i++)
                {
                        for (j = 0;j < 8;j++)
                        {
                                if (priority == 0)	/* Do's Castle */
                                {
                                        colortable[32*16+16*i+j] = 0;	/* high bit clear means less priority than sprites */
                                        colortable[32*16+16*i+j+8] = (char)(8*i+j);
                                }
                                else	/* Do Wild Ride, Do Run Run, Kick Rider */
                                {
                                        colortable[32*16+16*i+j] = (char)(8*i+j);
                                        colortable[32*16+16*i+j+8] = 0;	/* high bit set means less priority than sprites */
                                }
                        }
                }

                /* sprites */
                /* sprites have 4 bitplanes, but they actually have only 8 colors. The fourth */
                /* plane is used for transparency. */
                for (i = 0;i < 32;i++)
                {
                        for (j = 0;j < 8;j++)
                        {
                                colortable[64*16+16*i+j] = 0;	/* high bit clear means transparent */
                                colortable[64*16+16*i+j+8] = (char)(8*i+j);
                        }
                }

                /* find a non trasparent black */
                i = 1;
                color_prom.base=0;//(shadow) hmm hack? we need to start reading from start
                while (color_prom.read(i) != 0) i++;
                colortable[64*16+8] = (char)(i);	/* replace pen 0 with a non trasparent black */
        }


        public static VhConvertColorPromPtr docastle_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
                convert_color_prom(palette,colortable,color_prom,0);
        }};
        public static VhConvertColorPromPtr dowild_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
               convert_color_prom(palette,colortable,color_prom,1);
        }};

        /***************************************************************************

          Start the video hardware emulation.

        ***************************************************************************/
        public static VhStartPtr docastle_vh_start = new VhStartPtr() { public int handler()
	{
        	if (generic_vh_start.handler() != 0)
			return 1;
		if ((tmpbitmap1 = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}
                return 0;
        }};


        /***************************************************************************

          Stop the video hardware emulation.

        ***************************************************************************/
        public static VhStopPtr docastle_vh_stop = new VhStopPtr() { public void handler()
	{
		osd_free_bitmap(tmpbitmap1);
		tmpbitmap1 = null;
		generic_vh_stop.handler();
	} };



        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr docastle_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
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
                                                videoram.read(offs) + 8*(colorram.read(offs) & 0x20),
                                                colorram.read(offs) & 0x1f,
                                                0,0,
                                                8*sx,8*sy,
                                                Machine.drv.visible_area,TRANSPARENCY_NONE,0);

                                /* also draw the part of the character which has priority over the */
                                /* sprites in another bitmap */
                                drawgfx(tmpbitmap1,Machine.gfx[0],
                                                videoram.read(offs) + 8*(colorram.read(offs) & 0x20),
                                                32 + (colorram.read(offs) & 0x1f),
                                                0,0,
                                                8*sx,8*sy,
                                                Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the character mapped graphics */
                copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);


                /* Draw the sprites. Note that it is important to draw them exactly in this */
                /* order, to have the correct priorities. */
                for (offs = 0;offs < spriteram_size[0];offs += 4)
                {
                        drawgfx(bitmap,Machine.gfx[1],
                                spriteram.read(offs+3),
                                spriteram.read(offs+2) & 0x1f,
                                spriteram.read(offs+2) & 0x40,spriteram.read(offs+2) & 0x80,
                                spriteram.read(offs+1),spriteram.read(offs),
                                Machine.drv.visible_area,TRANSPARENCY_COLOR,0);
                }


                /* now redraw the portions of the background which have priority over sprites */
                copybitmap(bitmap,tmpbitmap1,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_COLOR,0);
        }};
}
