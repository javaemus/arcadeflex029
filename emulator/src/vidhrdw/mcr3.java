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
public class mcr3 {
        public static CharPtr mcr3_paletteram=new CharPtr();

        static int[] sprite_transparency=new int[512]; /* no mcr3 game has more than this many sprites */


        /***************************************************************************

          Generic MCR/III video routines

        ***************************************************************************/
        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
        public static VhConvertColorPromPtr mcr3_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
	{
                int i;
                CharPtr color_prom = new CharPtr(color_prom_table);
                CharPtr palette= new CharPtr(palette_table);
 
                /* the palette will be initialized by the game. We just set it to some */
                /* pre-cooked values so the startup copyright notice can be displayed. */
                for (i = 0;i < Machine.drv.total_colors;i++)
                {
                        palette.writeinc(((i & 1) >> 0) * 0xff);
                        palette.writeinc(((i & 2) >> 1) * 0xff);
                        palette.writeinc(((i & 4) >> 2) * 0xff);
                }

                /* characters and sprites use the same palette */
                /* we reserve pen 0 for the background black which makes the */
                /* MS-DOS version look better */
                for (i = 0;i < TOTAL_COLORS(0);i++)
                        colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char)(i + 1);

           /* now check our sprites and mark which ones have color 8 ('draw under') */
           {
              GfxElement gfx=new GfxElement();
              int i1,x,y;
              char[] dp;

              gfx = Machine.gfx[1];
              for (i1=0;i1<gfx.total_elements;i1++)
              {
                                sprite_transparency[i1] = 0;

                                for (y=0;y<gfx.height;y++)
                                {
                                        dp=gfx.gfxdata.line[i1 * gfx.height + y];
                                        for (x=0;x<gfx.width;x++)
                                                if (dp[x] == 8)
                                                        sprite_transparency[i1] = 1;
                                }

                                if (sprite_transparency[i1]!=0)
                                        if (errorlog!=null)
                                                fprintf(errorlog,"sprite %d has transparency.\n",i1);
             }
           }
        }};



        public static WriteHandlerPtr mcr3_palette_w = new WriteHandlerPtr() { public void handler(int offset,int data)
        {
           int r;
           int g;
           int b;

           mcr3_paletteram.write(offset,data);
                offset &= 0x7f;

           r = ((offset & 1) << 2) + (data >> 6);
           g = (data >> 0) & 7;
           b = (data >> 3) & 7;

           /* up to 8 bits */
           r = (r << 5) | (r << 2) | (r >> 1);
           g = (g << 5) | (g << 2) | (g >> 1);
           b = (b << 5) | (b << 2) | (b >> 1);

           osd_modify_pen(Machine.gfx[0].colortable.read(offset/2), r, g, b);
        }};

        public static WriteHandlerPtr mcr3_videoram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
        {
                if (videoram.read(offset) != data)
                {
                        dirtybuffer[offset & ~1] = 1;
                        videoram.write(offset,data);
                }
        }};

        public static VhUpdatePtr mcr3_vh_screenrefresh= new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
        {
           int offs;
           int mx,my;
           int attr;
           int color;

           /* for every character in the Video RAM, check if it has been modified */
           /* since last time and update it accordingly. */
           for (offs = videoram_size[0] - 2;offs >= 0;offs -= 2)
           {
              if (dirtybuffer[offs]!=0)
              {
                                dirtybuffer[offs] = 0;

                                mx = (offs/2) % 32;
                                my = (offs/2) / 32;
                      attr = videoram.read(offs+1);
                      color = (attr & 0x30) >> 4;

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                        videoram.read(offs)+256*(attr & 0x03),
                                        color,attr & 0x04,attr&0x08,16*mx,16*my,
                                        Machine.drv.visible_area,TRANSPARENCY_NONE,0);
              }
           }

           /* copy the character mapped graphics */
           copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);

           /* Draw the sprites. */
           for (offs = 0;offs < spriteram_size[0];offs += 4)
           {
              int code,color1,flipx,flipy,sx,sy,flags;

              if (spriteram.read(offs) == 0)
                                continue;

              code = spriteram.read(offs+2);
              flags = spriteram.read(offs+1);
              color1 = 3 - (flags & 0x03);
              flipx = flags & 0x10;
              flipy = flags & 0x20;
              sx = (spriteram.read(offs+3)-3)*2;
              sy = (241-spriteram.read(offs))*2;

              drawgfx(bitmap,Machine.gfx[1],
                      code,color1,flipx,flipy,sx,sy,
                      Machine.drv.visible_area,TRANSPARENCY_PEN,0);

              /* sprites use color 0 for background pen and 8 for the 'under tile' pen.
                                The color 8 is used to cover over other sprites.
                At the beginning we scanned all sprites and marked the ones that contained
                                at least one pixel of color 8, so we only need to worry about these few. */
              if (sprite_transparency[code]!=0)
              {
                                rectangle clip=new rectangle();

                                clip.min_x = sx;
                                clip.max_x = sx+31;
                                clip.min_y = sy;
                                clip.max_y = sy+31;

                                copybitmap(bitmap,tmpbitmap,0,0,0,0,clip,TRANSPARENCY_THROUGH,8+(color1*16)+1);
              }
           }
        }};


        /***************************************************************************

          Rampage-specific video routines

        ***************************************************************************/
        public static VhConvertColorPromPtr rampage_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
	{
                osd_bitmap bitmap = Machine.gfx[0].gfxdata;
                int y, x;

                /* standard init */
                mcr3_vh_convert_color_prom.handler(palette_table, colortable_table, color_prom_table);

                /* the tile graphics for Rampage are reverse colors; we preswap them here */
                for (y = bitmap.height - 1; y >= 0; y--)
                        for (x = bitmap.width - 1; x >= 0; x--)
                                bitmap.line[y][x] ^= 15;
        }};

        public static VhUpdatePtr rampage_vh_screenrefresh= new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
        {
           int offs;
           int mx,my;
           int attr;
           int color;

           /* for every character in the Video RAM, check if it has been modified */
           /* since last time and update it accordingly. */
           for (offs = videoram_size[0] - 2;offs >= 0;offs -= 2)
           {
              if (dirtybuffer[offs]!=0)
              {
                                dirtybuffer[offs] = 0;

                                mx = (offs/2) % 32;
                                my = (offs/2) / 32;
                      attr = videoram.read(offs+1);
                      color = (attr & 0x30) >> 4;

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                        videoram.read(offs)+256*(attr & 0x03),
                                        3-color,attr & 0x04,attr&0x08,16*mx,16*my,
                                        Machine.drv.visible_area,TRANSPARENCY_NONE,0);
              }
           }

           /* copy the character mapped graphics */
           copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);

           /* Draw the sprites. */
           for (offs = 0;offs < spriteram_size[0];offs += 4)
           {
              int code,color1,flipx,flipy,sx,sy,flags;

              if (spriteram.read(offs) == 0)
                                 continue;

              code = spriteram.read(offs+2);
              flags = spriteram.read(offs+1);
              code += 256 * ((flags >> 3) & 1);
              color1 = 3 - (flags & 0x03);
              flipx = flags & 0x10;
              flipy = flags & 0x20;
              sx = (spriteram.read(offs+3)-3)*2;
              sy = (241-spriteram.read(offs))*2;

              drawgfx(bitmap,Machine.gfx[1],
                      code,color1,flipx,flipy,sx,sy,
                      Machine.drv.visible_area,TRANSPARENCY_PEN,0);

              /* sprites use color 0 for background pen and 8 for the 'under tile' pen.
                                The color 8 is used to cover over other sprites.
                At the beginning we scanned all sprites and marked the ones that contained
                                at least one pixel of color 8, so we only need to worry about these few. */
              if (sprite_transparency[code]!=0)
              {
                                rectangle clip=new rectangle();

                                clip.min_x = sx;
                                clip.max_x = sx+31;
                                clip.min_y = sy;
                                clip.max_y = sy+31;

                                copybitmap(bitmap,tmpbitmap,0,0,0,0,clip,TRANSPARENCY_THROUGH,8+(color1*16)+1);
              }
           }
        }};


        /***************************************************************************

          Spy Hunter-specific video routines

        ***************************************************************************/

        public static CharPtr spyhunt_alpharam=new CharPtr();
        public static int[] spyhunt_alpharam_size=new int[1];
        public static int spyhunt_scrollx,spyhunt_scrolly;

        public static osd_bitmap backbitmap;	/* spy hunter only for scrolling background */
        public static rectangle backclip=new rectangle();

        public static VhConvertColorPromPtr spyhunt_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
	{
                /* standard init */
           mcr3_vh_convert_color_prom.handler(palette_table,colortable_table,color_prom_table);

           /* plus some colors for the alpha RAM */
           palette_table[(8*16+1)*3+0] = 0;
           palette_table[(8*16+1)*3+1] = 0;
           palette_table[(8*16+1)*3+2] = 0;
           palette_table[(8*16+2)*3+0] = 0;
           palette_table[(8*16+2)*3+1] = 255;
           palette_table[(8*16+2)*3+2] = 0;
           palette_table[(8*16+3)*3+0] = 0;
           palette_table[(8*16+3)*3+1] = 0;
           palette_table[(8*16+3)*3+2] = 255;
           palette_table[(8*16+4)*3+0] = 255;
           palette_table[(8*16+4)*3+1] = 255;
           palette_table[(8*16+4)*3+2] = 255;

           /* put them into the color table */
           colortable_table[8*16+0] = 8*16+1;
           colortable_table[8*16+1] = 8*16+2;
           colortable_table[8*16+2] = 8*16+3;
           colortable_table[8*16+3] = 8*16+4;
        }};

        public static VhStartPtr spyhunt_vh_start = new VhStartPtr() { public int handler()
	{
                dirtybuffer = new char[videoram_size[0]];
                if (dirtybuffer==null)
                        return 1;
                memset(dirtybuffer,1,videoram_size[0]);

                backclip.min_x = backclip.min_y = 0;
                backclip.max_x = 64*64 - 1;
                backclip.max_y = 32*32 - 1;

                backbitmap = osd_create_bitmap(64*64,32*32);
                if (backbitmap==null)
                {
                        dirtybuffer=null;
                        return 1;
                }

                spyhunt_scrollx = spyhunt_scrolly = 0;

                return 0;
        }};

        public static VhStopPtr spyhunt_vh_stop = new VhStopPtr() { public void handler()
	{
                osd_free_bitmap(backbitmap);
                backbitmap=null;
                dirtybuffer=null;
        }};
        public static VhUpdatePtr spyhunt_vh_screenrefresh = new VhUpdatePtr() {	public void handler(osd_bitmap bitmap)
	{
           int offs;
           int mx,my;


           /* for every character in the Video RAM, check if it has been modified */
           /* since last time and update it accordingly. */
           for (offs = videoram_size[0] - 1; offs >= 0; offs--)
           {
                        if (dirtybuffer[offs]!=0)
                        {
                                int code = videoram.read(offs);

                                dirtybuffer[offs] = 0;

                                mx = (offs >> 4) & 0x3f;
                                my = (offs & 0x0f) | ((offs >> 6) & 0x10);

                                drawgfx(backbitmap,Machine.gfx[0],
                                        (code & 0x3f) | ((code & 0x80) >> 1),
                                        1,0,(code & 0x40),64*mx,32*my,
                                        backclip,TRANSPARENCY_NONE,0);
                        }
           }

           /* copy the character mapped graphics */
           {
                        int scrollx,scrolly;

                        scrollx = -spyhunt_scrollx * 2 - 16;
                        scrolly = -spyhunt_scrolly * 2;

                        copyscrollbitmap(bitmap,backbitmap,1,new int[] {scrollx},1,new int[] {scrolly},Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }

           /* Draw the sprites. */
           for (offs = 0;offs < spriteram_size[0];offs += 4)
           {
              int code,flipx,flipy,sx,sy,flags;

              if (spriteram.read(offs) == 0)
                                 continue;

              code = spriteram.read(offs+2) ^ 0x80;
              flags = spriteram.read(offs+1);
              flipx = flags & 0x10;
              flipy = flags & 0x20;
              sx = (spriteram.read(offs+3)-3)*2;
              sy = (241-spriteram.read(offs))*2;

              drawgfx(bitmap,Machine.gfx[1],
                      code,0,flipx,flipy,sx-16,sy,
                      Machine.drv.visible_area,TRANSPARENCY_PEN,0);

              /* sprites use color 0 for background pen and 8 for the 'under tile' pen.
                                The color 8 is used to cover over other sprites.
                At the beginning we scanned all sprites and marked the ones that contained
                                at least one pixel of color 8, so we only need to worry about these few. */
              if (sprite_transparency[code]!=0)
              {
                                rectangle clip=new rectangle();

                                clip.min_x = sx;
                                clip.max_x = sx+31;
                                clip.min_y = sy;
                                clip.max_y = sy+31;

                                copybitmap(bitmap,tmpbitmap,0,0,0,0,clip,TRANSPARENCY_THROUGH,8+16+1);
              }
           }

                /* render any characters on top */
           for (offs = spyhunt_alpharam_size[0] - 1; offs >= 0; offs--)
           {
                int ch = spyhunt_alpharam.read(offs);
                if (ch!=0)
                {
                      mx = (offs) / 32;
                      my = (offs) % 32;

                      drawgfx(bitmap,Machine.gfx[2],
                              ch,
                              0,0,0,16*mx-16,16*my,
                              Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                        }
                }
        }};    
}
