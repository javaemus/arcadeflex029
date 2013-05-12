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
import static arcadeflex.osdepend.*;
import static mame.common.*;
import static mame.commonH.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;

public class tp84 {
    public static CharPtr tp84_videoram2=new CharPtr();
    public static CharPtr tp84_colorram2=new CharPtr();
    static osd_bitmap tmpbitmap2;
    static char[] dirtybuffer2;

    public static CharPtr tp84_scrollx=new CharPtr();
    public static CharPtr tp84_scrolly=new CharPtr();

    static int col0;


    public static CharPtr tp84_sharedram=new CharPtr();


    static rectangle topvisiblearea = new rectangle
    (
            0*8, 2*8-1,
            2*8, 30*8-1
    );
    static rectangle bottomvisiblearea = new rectangle
    (
            30*8, 32*8-1,
            2*8, 30*8-1
    );


    public static ReadHandlerPtr tp84_sharedram_r = new ReadHandlerPtr() { public int handler(int offset)
    {
            return tp84_sharedram.read(offset);
    }};
    public static WriteHandlerPtr tp84_sharedram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
    {
            tp84_sharedram.write(offset,data);
    }};


        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
    public static VhConvertColorPromPtr tp84_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
    {
                int i;
                CharPtr color_prom = new CharPtr(color_prom_table);
                CharPtr palette= new CharPtr(palette_table);
            for (i = 0;i < Machine.drv.total_colors;i++)
            {
                    int bit0,bit1,bit2,bit3;

                    /* red component */
                    bit0 = (color_prom.read(0) >> 0) & 0x01;
                    bit1 = (color_prom.read(0) >> 1) & 0x01;
                    bit2 = (color_prom.read(0) >> 2) & 0x01;
                    bit3 = (color_prom.read(0) >> 3) & 0x01;
                    palette.write(0x0e * bit0 + 0x1f * bit1 + 0x42 * bit2 + 0x90 * bit3);
                    /* green component */
                    bit0 = (color_prom.read(Machine.drv.total_colors) >> 0) & 0x01;
                    bit1 = (color_prom.read(Machine.drv.total_colors) >> 1) & 0x01;
                    bit2 = (color_prom.read(Machine.drv.total_colors) >> 2) & 0x01;
                    bit3 = (color_prom.read(Machine.drv.total_colors) >> 3) & 0x01;
                    palette.write(0x0e * bit0 + 0x1f * bit1 + 0x42 * bit2 + 0x90 * bit3);
                    /* blue component */
                    bit0 = (color_prom.read(2*Machine.drv.total_colors) >> 0) & 0x01;
                    bit1 = (color_prom.read(2*Machine.drv.total_colors) >> 1) & 0x01;
                    bit2 = (color_prom.read(2*Machine.drv.total_colors) >> 2) & 0x01;
                    bit3 = (color_prom.read(2*Machine.drv.total_colors) >> 3) & 0x01;
                    palette.write(0x0e * bit0 + 0x1f * bit1 + 0x42 * bit2 + 0x90 * bit3);

                    color_prom.inc();
            }

            color_prom.inc(2*Machine.drv.total_colors);
            /* color_prom now points to the beginning of the lookup table */


            /* characters use colors 128-255 */
            for (i = 0;i < TOTAL_COLORS(0)/8;i++)
            {
                    int j;


                    for (j = 0;j < 8;j++)
                        colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i+256*j]=(char)(color_prom.read() + 128 + 16*j);
                            //COLOR(0,i+256*j) = *color_prom + 128 + 16*j;

                    color_prom.inc();
            }
            /* sprites use colors 0-127 */
            for (i = 0;i < TOTAL_COLORS(1)/8;i++)
            {
                    int j;


                    for (j = 0;j < 8;j++)
                    {
                        if(color_prom.read()!=0) colortable_table[Machine.drv.gfxdecodeinfo[1].color_codes_start + i+256*j]=(char)(color_prom.read() + 16*j);
                        else colortable_table[Machine.drv.gfxdecodeinfo[1].color_codes_start + i+256*j]=0;
                            //if (*color_prom) COLOR(1,i+256*j) = *color_prom + 16*j;
                            //else COLOR(1,i+256*j) = 0;	/* preserve transparency */
                    }

                    color_prom.inc();
            }
    }};



    /***************************************************************************

      Start the video hardware emulation.

    ***************************************************************************/
    public static VhStartPtr tp84_vh_start = new VhStartPtr() { public int handler()
    {
   
            if (generic_vh_start.handler() != 0)
			return 1;

                if ((dirtybuffer2 = new char[videoram_size[0]]) == null)
               {
                       generic_vh_stop.handler();
                        return 1;
                }
                memset(dirtybuffer2,1,videoram_size[0]);

                if ((tmpbitmap2 = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
                {
                        dirtybuffer2=null;
                        generic_vh_stop.handler();
                        return 1;
                }

                return 0;
    }};



    /***************************************************************************

      Stop the video hardware emulation.

    ***************************************************************************/
    public static VhStopPtr tp84_vh_stop = new VhStopPtr() { public void handler()
    {
                dirtybuffer2=null;
                osd_free_bitmap(tmpbitmap2);
                tmpbitmap2=null;
                generic_vh_stop.handler();
     }};


        public static WriteHandlerPtr tp84_videoram2_w = new WriteHandlerPtr() { public void handler(int offset,int data)
        {

            if (tp84_videoram2.read(offset) != data)
            {
                    dirtybuffer2[offset] = 1;

                    tp84_videoram2.write(offset,data);
            }
    }};


    public static WriteHandlerPtr tp84_colorram2_w = new WriteHandlerPtr() { public void handler(int offset,int data)
    {
            if (tp84_colorram2.read(offset) != data)
            {
                    dirtybuffer2[offset] = 1;

                    tp84_colorram2.write(offset,data);
            }
    }};



    /*****
      col0 is a register to index the color Proms
    *****/
    public static WriteHandlerPtr tp84_col0_w = new WriteHandlerPtr() { public void handler(int offset,int data)
    {
            if(col0 != data)
            {
                    col0 = data;

                    memset(dirtybuffer,1,videoram_size[0]);
            }
    }};



    /***************************************************************************

            Draw the game screen in the given osd_bitmap.
            Do NOT call osd_update_display() from this function, it will be called by
            the main emulation engine.

    ***************************************************************************/
    public static VhUpdatePtr tp84_vh_screenrefresh= new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
    {
            int i;
            int offs;
            int coloffset;


            coloffset = ((col0&0x18) << 1) + ((col0&0x07) << 6);

            for (offs = videoram_size[0] - 1;offs >= 0;offs--)
            {
                    if (dirtybuffer[offs]!=0)
                    {
                            int sx,sy;


                            dirtybuffer[offs] = 0;

                            sx = offs % 32;
                            sy = offs / 32;

                            drawgfx(tmpbitmap,Machine.gfx[0],
                                            videoram.read(offs) + ((colorram.read(offs)  & 0x30) << 4),
                                            (colorram.read(offs)  & 0x0f) + coloffset,
                                            0, /*colorram[offs] & 0x40,*/ /* Not sure, not used */
                                            0, /*colorram[offs] & 0x80,*/ /* Not sure, not used */
                                            8*sx,8*sy,
                                            null,TRANSPARENCY_NONE,0);
                    }

                    if (dirtybuffer2[offs]!=0)
                    {
                            int sx,sy;


                            dirtybuffer2[offs] = 0;

                            sx = offs % 32;
                            sy = offs / 32;

                    /* Skip the middle of the screen, this ram seem to be used as normal ram. */
                            if (sx < 2 || sx >= 30)
                                    drawgfx(tmpbitmap2,Machine.gfx[0],
                                                    tp84_videoram2.read(offs)  + ((tp84_colorram2.read(offs)  & 0x30) << 4),
                                                    (tp84_colorram2.read(offs)  & 0x0f) + coloffset,
                                                    tp84_colorram2.read(offs)  & 0x40,tp84_colorram2.read(offs)  & 0x80,
                                                    8*sx,8*sy,
                                                    Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                    }
            }


            /* copy the temporary bitmap to the screen */
            {
                    int scrollx,scrolly;


                    scrollx = tp84_scrollx.read();
                    scrolly = tp84_scrolly.read();

                    copyscrollbitmap(bitmap,tmpbitmap,1,new int[]{scrollx},1,new int[]{scrolly},Machine.drv.visible_area,TRANSPARENCY_NONE,0);
            }


            /* Draw the sprites. */
            coloffset = ((col0&0x07) << 4);
            for (i = 95;i >= 0; i--)
            {
    /*		if (tp84_sharedram[4*i+1])  Is there not a flag to tell us to display or not? */

    /*I think there is a special case for sprite 94, this one is supossed to
            be black because it is the shadow of the player */
    /* How can I do that? */

                    drawgfx(bitmap,Machine.gfx[1],
                                    tp84_sharedram.read(4*i + 1),
                                    (tp84_sharedram.read(4*i + 2)&0x0F)+coloffset,
                                    NOT(tp84_sharedram.read(4*i + 2) & 0x40),   /*hflip*/
                                    tp84_sharedram.read(4*i + 2) & 0x80,      /*vflip*/
                                    tp84_sharedram.read(4*i + 0),             /*xpos*/
                                    240-tp84_sharedram.read(4*i + 3),         /*ypos*/
                                    Machine.drv.visible_area,TRANSPARENCY_COLOR,0);
            }


            /* Copy the frontmost playfield. */
            copybitmap(bitmap,tmpbitmap2,0,0,0,0,topvisiblearea,TRANSPARENCY_NONE,0);
            copybitmap(bitmap,tmpbitmap2,0,0,0,0,bottomvisiblearea,TRANSPARENCY_NONE,0);
    } };   
}
