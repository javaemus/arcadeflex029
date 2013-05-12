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

public class tutankhm {
    
     public static CharPtr tut_paletteram = new CharPtr();
     public static CharPtr tut_scrollx = new CharPtr();
        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
        public static VhConvertColorPromPtr tut_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
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

            /* initialize the color table */
            /* we reserve pen 0 for the background black which makes the */
            /* MS-DOS version look better */
            for (i = 0;i < TOTAL_COLORS(0);i++)
                    colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char)(i + 1);
        }};

        public static WriteHandlerPtr tut_videoram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                if (videoram.read(offset) != data)
                {
                        CharPtr lookup = new CharPtr(Machine.gfx[0].colortable.memory);
                        int x, y;

                        /* bitmap is rotated -90 deg. */
                        x = ( offset >> 7 );
                        y = ( ( ~offset ) & 0x7f ) << 1;

                        tmpbitmap.line[ y ][ x ] = lookup.read(data >> 4);
                        tmpbitmap.line[ y + 1 ][ x ] = lookup.read(data & 0x0f);
        
                        videoram.write(offset, data);
                }
        }};

        public static WriteHandlerPtr tut_palette_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
            	int r, g, b;
                int bit0,bit1,bit2;


                tut_paletteram.write(offset, data);

                /* red component */
                bit0 = (data >> 0) & 0x01;
                bit1 = (data >> 1) & 0x01;
                bit2 = (data >> 2) & 0x01;
                r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
                /* green component */
                bit0 = (data >> 3) & 0x01;
                bit1 = (data >> 4) & 0x01;
                bit2 = (data >> 5) & 0x01;
                g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
                /* blue component */
                bit0 = 0;
                bit1 = (data >> 6) & 0x01;
                bit2 = (data >> 7) & 0x01;
                b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;

                osd_modify_pen (Machine.gfx[0].colortable.read(offset), r, g, b);
        }};


        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr tut_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                /* copy the temporary bitmap to the screen */
                {
                        int i;
                        int[] scroll = new int[32];


                        for (i = 0;i < 8;i++)
                                scroll[i] = 0;
                        for (i = 8;i < 32;i++)
                                scroll[i] = -(tut_scrollx.read());

                        copyscrollbitmap(bitmap,tmpbitmap,32,scroll,0,null,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }
        }};
     
}
