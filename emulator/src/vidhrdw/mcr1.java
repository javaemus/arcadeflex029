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

public class mcr1 {
    public static CharPtr mcr1_paletteram=new CharPtr();

    static int spriteoffs;


    /***************************************************************************

      Generic MCR/I video routines

    ***************************************************************************/
        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
        
        public static VhConvertColorPromPtr mcr1_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
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

            /* no sprite offset by default */
       spriteoffs = 0;
    }};

    public static WriteHandlerPtr mcr1_palette_w = new WriteHandlerPtr() { public void handler(int offset,int data)
    {
       int color;
       int r;
       int g;
       int b;

       mcr1_paletteram.write(offset,data);

            if ((offset >= 0x00 && offset < 0x40) || (offset >= 0x400 && offset < 0x440))
            {
          color = offset & 0x1f;

          b = mcr1_paletteram.read(color) >> 4;
          g = mcr1_paletteram.read(color) & 0x0f;
          r = mcr1_paletteram.read(color+0x400) & 0x0f;

          /* up to 8 bits */
          r = (r << 4) | r;
          g = (g << 4) | g;
          b = (b << 4) | b;

          osd_modify_pen(Machine.gfx[0].colortable.read(color), r, g, b);
            }
    }};

    public static VhUpdatePtr mcr1_vh_screenrefresh= new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
    {
       int offs;
       int mx,my;

       /* for every character in the Video RAM, check if it has been modified */
       /* since last time and update it accordingly. */

       for (offs = videoram_size[0] - 1;offs >= 0;offs--)
       {
          if (dirtybuffer[offs]!=0)
          {
                            dirtybuffer[offs] = 0;

                            mx = (offs) % 32;
                            my = (offs) / 32;

                            drawgfx(tmpbitmap,Machine.gfx[0],
                                    videoram.read(offs),
                                    0,0,0,16*mx,16*my,
                                    Machine.drv.visible_area,TRANSPARENCY_NONE,0);
          }
       }

       /* copy the character mapped graphics */
       copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);

       /* Draw the sprites. */
       for (offs = 0;offs < spriteram_size[0];offs += 4)
       {
          int code,color,flipx,flipy,sx,sy,flags;

          if (spriteram.read(offs) == 0)
                            continue;

          sy = (241-spriteram.read(offs))*2;
          code = spriteram.read(offs+1) & 0x3f;
          sx = (spriteram.read(offs+2)-3+spriteoffs)*2;
          flags = spriteram.read(offs+3);
          color = 1;
          flipx = spriteram.read(offs+1) & 0x40;
          flipy = spriteram.read(offs+1) & 0x80;

          drawgfx(bitmap,Machine.gfx[1],
                  code,color,flipx,flipy,sx,sy,
                  Machine.drv.visible_area,TRANSPARENCY_PEN,0);
       }
    }};


    /***************************************************************************

      Solar Fox-specific video routines

    ***************************************************************************/
    
    public static VhConvertColorPromPtr solarfox_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
    {
            /* standard init */
            mcr1_vh_convert_color_prom.handler(palette, colortable, color_prom);

            /* except the sprites need to be offset */
            spriteoffs = 6;
    } };   
}
