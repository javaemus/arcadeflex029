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
 *
 * rewrote for v0.28
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
public class xevious {
    
        public static CharPtr xevious_videoram2=new CharPtr();
        public static CharPtr xevious_colorram2=new CharPtr();

        static osd_bitmap tmpbitmap1;
        static osd_bitmap tmpbitmap2;

        public static char[] dirtybuffer2;

        /* scroll position controller write (CUSTOM 13-1J on seet 7B) */
        static int bg_y_pos , bg_x_pos;
        static int fo_y_pos , fo_x_pos;
        static int flip;

        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
        public static VhConvertColorPromPtr xevious_vh_convert_color_prom =new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
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
                        palette.writeinc(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                        /* green component */
                        bit0 = (color_prom.read(Machine.drv.total_colors) >> 0) & 0x01;
                        bit1 = (color_prom.read(Machine.drv.total_colors) >> 1) & 0x01;
                        bit2 = (color_prom.read(Machine.drv.total_colors) >> 2) & 0x01;
                        bit3 = (color_prom.read(Machine.drv.total_colors) >> 3) & 0x01;
                        palette.writeinc(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                        /* blue component */
                        bit0 = (color_prom.read(2*Machine.drv.total_colors) >> 0) & 0x01;
                        bit1 = (color_prom.read(2*Machine.drv.total_colors)  >> 1) & 0x01;
                        bit2 = (color_prom.read(2*Machine.drv.total_colors)  >> 2) & 0x01;
                        bit3 = (color_prom.read(2*Machine.drv.total_colors)  >> 3) & 0x01;
                        palette.writeinc(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);

                        color_prom.inc();
                }
                
                color_prom.inc(2*Machine.drv.total_colors);
                /* color_prom now points to the beginning of the lookup table */

                /* background tiles */
                for (i = 0;i < TOTAL_COLORS(1);i++)
                    colortable_table[Machine.drv.gfxdecodeinfo[1].color_codes_start + i] = color_prom.readinc();
                        //COLOR(1,i) = *(color_prom++);

                /* sprites */
                for (i = 0;i < TOTAL_COLORS(2);i++)
                {
                        if (i % 8 == 0) 
                        {
                            //COLOR(2,i) = 0x80; 	/* transparent */
                            colortable_table[Machine.drv.gfxdecodeinfo[2].color_codes_start + i] = 0x80;
                        }
                        else 
                        {
                            //COLOR(2,i) = *color_prom;
                            colortable_table[Machine.drv.gfxdecodeinfo[2].color_codes_start + i] = color_prom.read();
                        }

                        color_prom.inc();
                }

                /* foreground characters */
                for (i = 0;i < TOTAL_COLORS(0);i++)
                {
                        if (i % 2 == 0) 
                        {
                            //COLOR(0,i) = 0;
                            colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] =0;
                        }     
                        else 
                        {
                            //COLOR(0,i) = i / 2;
                            colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] =(char)(i / 2);
                        }
                            
                }
        }};



        public static WriteHandlerPtr xevious_vh_latch_w = new WriteHandlerPtr() { public void handler(int offset,int data)
        {   
                int reg;

                data = data + ((offset&0x01)<<8);	/* A0 . D8 */
                reg = (offset&0xf0)>>4;

                switch (reg)
                {
                case 0:		/* BG Y scroll position */
                        bg_y_pos = data;
                        break;
                case 2:		/* BG X scroll position ?? */
                        bg_x_pos = data;
                        break;
                case 1:		/* FONT Y scroll position ??*/
                        fo_y_pos = data;
                        break;
                case 3:		/* FONT X scroll position ?? */
                        fo_x_pos = data;
                        break;
                case 7:		/* DISPLAY XY FLIP ?? */
                        flip = data&1;
                        break;
           default:
                           if (errorlog!=null)
                                   fprintf(errorlog,"CRTC WRITE REG: %x  Data: %03x\n",reg, data);
                           break;
                }
        }};



        public static VhStartPtr xevious_vh_start = new VhStartPtr() { public int handler()
         {
                if (generic_vh_start.handler() != 0)
                        return 1;

                if ((dirtybuffer2 =new char[videoram_size[0]]) == null)
                {
                        generic_vh_stop.handler();
                        return 1;
                }
                memset(dirtybuffer2,1,videoram_size[0]);

                if ((tmpbitmap1 = osd_create_bitmap(32*8,64*8)) == null)
                {
                        dirtybuffer2=null;
                        generic_vh_stop.handler();
                        return 1;
                }

                if ((tmpbitmap2 = osd_create_bitmap(32*8,64*8)) == null)
                {
                        osd_free_bitmap(tmpbitmap1);
                        tmpbitmap1=null;
                        dirtybuffer2=null;
                        generic_vh_stop.handler();
                        return 1;
                }

                return 0;
        }};
        public static VhStopPtr xevious_vh_stop = new VhStopPtr() { public void handler()
        {
                osd_free_bitmap(tmpbitmap2);
                osd_free_bitmap(tmpbitmap1);
                tmpbitmap1=null;
                tmpbitmap2=null;
                 dirtybuffer2=null;
                generic_vh_stop.handler();
        }};

        public static WriteHandlerPtr xevious_videoram2_w = new WriteHandlerPtr() { public void handler(int offset,int data)
        {
                if (xevious_videoram2.read(offset) != data)
                {
                        dirtybuffer2[offset] = 1;

                        xevious_videoram2.write(offset,data);
                }
        }};


        public static WriteHandlerPtr xevious_colorram2_w = new WriteHandlerPtr() { public void handler(int offset,int data)
            {
                if (xevious_colorram2.read(offset) != data)
                {
                        dirtybuffer2[offset] = 1;

                        xevious_colorram2.write(offset,data);
                }
        }};


        public static VhUpdatePtr xevious_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
        {
                int offs, sx,sy;

                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] ;offs >= 0;offs--)
                {
                        /* foreground */
                        if (dirtybuffer[offs]!=0)
                        {
                                dirtybuffer[offs] = 0;

                                sx = 31 - (offs / 64);
                                sy = offs % 64;

                                drawgfx(tmpbitmap1,Machine.gfx[0],
                                                videoram.read(offs),
                                                ((colorram.read(offs) & 0x03) << 4) + ((colorram.read(offs) >> 2) & 0x0f),
                                                colorram.read(offs) & 0x80,colorram.read(offs) & 0x40,
                                                8*sx,8*sy,
                                                null,TRANSPARENCY_NONE,0);
                        }

                        /* background */
                        if (dirtybuffer2[offs]!=0)
                        {
                                dirtybuffer2[offs] = 0;

                                sx = 31 - (offs / 64);
                                sy = offs % 64;

                                drawgfx(tmpbitmap2,Machine.gfx[1],
                                                xevious_videoram2.read(offs) + 256*(xevious_colorram2.read(offs) & 1),
                                                ((xevious_colorram2.read(offs) >> 2) & 0xf) +
                                                                ((xevious_colorram2.read(offs)& 0x3) << 5) +
                                                                ((xevious_videoram2.read(offs) & 0x80) >> 3),
                                                xevious_colorram2.read(offs) & 0x80,xevious_colorram2.read(offs) & 0x40,
                                                8*sx,8*sy,
                                                null,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the background */
                {
                        int scrollx,scrolly;


                        scrollx = bg_x_pos - 16;
                        scrolly = -bg_y_pos - 20;

                        copyscrollbitmap(bitmap,tmpbitmap2,1,new int[]{scrollx},1,new int[]{scrolly},Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }


                /* draw sprites */
                for (offs = 0;offs < spriteram_size[0];offs += 2)
                {
                        if ((spriteram.read(offs + 1) & 0x40) == 0)	/* I'm not sure about this one */
                        {
                                int bank,code,color,flipx,flipy,sx1,sy1;


                                bank = 2 + ((spriteram.read(offs) & 0x80) >> 7) + ((spriteram_3.read(offs) & 0x80) >> 6);
                                code = spriteram.read(offs) & 0x7f;
                                color = spriteram.read(offs+1) & 0x7f;
                                flipx = spriteram_3.read(offs) & 4;
                                flipy = spriteram_3.read(offs+1) & 4;
                                sx1 = spriteram_2.read(offs) - 15;
                                sy1 = spriteram_2.read(offs+1) - 40 + 0x100*(spriteram_3.read(offs+1)& 1);
                                if ((spriteram_3.read(offs) & 2)!=0)	/* double width (?) */
                                {
                                        if ((spriteram_3.read(offs) & 1)!=0)	/* double width, double height */
                                        {
                                                code &= 0x7c;
                                                drawgfx(bitmap,Machine.gfx[bank],
                                                                code+3,color,flipx,flipy,
                                                                flipx!=0 ? sx1+16 : sx1,flipy!=0 ? sy1 : sy1+16,
                                                                Machine.drv.visible_area,TRANSPARENCY_COLOR,0x80);
                                                drawgfx(bitmap,Machine.gfx[bank],
                                                                code+1,color,flipx,flipy,
                                                                flipx!=0 ? sx1 : sx1+16,flipy!=0 ? sy1 : sy1+16,
                                                                Machine.drv.visible_area,TRANSPARENCY_COLOR,0x80);
                                        }
                                        code &= 0x7d;
                                        drawgfx(bitmap,Machine.gfx[bank],
                                                        code+2,color,flipx,flipy,
                                                        flipx!=0 ? sx1+16 : sx1,flipy!=0 ? sy1+16 : sy1,
                                                        Machine.drv.visible_area,TRANSPARENCY_COLOR,0x80);
                                        drawgfx(bitmap,Machine.gfx[bank],
                                                        code,color,flipx,flipy,
                                                        flipx!=0 ? sx1 : sx1+16,flipy!=0 ? sy1+16 : sy1,
                                                        Machine.drv.visible_area,TRANSPARENCY_COLOR,0x80);
                                }
                                else if ((spriteram_3.read(offs) & 1)!=0)	/* double height */
                                {
                                        code &= 0x7e;
                                        drawgfx(bitmap,Machine.gfx[bank],
                                                        code,color,flipx,flipy,
                                                        flipx!=0 ? sx1+16 : sx1,flipy!=0 ? sy1+16 : sy1,
                                                        Machine.drv.visible_area,TRANSPARENCY_COLOR,0x80);
                                        drawgfx(bitmap,Machine.gfx[bank],
                                                        code+1,color,flipx,flipy,
                                                        flipx!=0 ? sx1+16 : sx1,flipy!=0 ? sy1 : sy1+16,
                                                        Machine.drv.visible_area,TRANSPARENCY_COLOR,0x80);
                                }
                                else	/* normal */
                                {
                                        drawgfx(bitmap,Machine.gfx[bank],
                                                        code,color,flipx,flipy,sx1,sy1,
                                                        Machine.drv.visible_area,TRANSPARENCY_COLOR,0x80);
                                }
                        }
                }


                /* copy the foreground  */
                {
                        int scrollx,scrolly;


                        scrollx = fo_x_pos - 14;
                        scrolly = -fo_y_pos - 32;

                        copyscrollbitmap(bitmap,tmpbitmap1,1,new int[]{scrollx},1,new int[]{scrolly},Machine.drv.visible_area,TRANSPARENCY_COLOR,0);
                }
        }};
    
}
