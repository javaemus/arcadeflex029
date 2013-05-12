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

public class sonson {
        public static CharPtr sonson_scrollx=new CharPtr();

        

        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
        
        public static VhConvertColorPromPtr sonson_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
	{
                int i;
                CharPtr color_prom = new CharPtr(color_prom_table);
                CharPtr palette= new CharPtr(palette_table);
                for (i = 0;i < Machine.drv.total_colors;i++)
                {
                        int bit0,bit1,bit2,bit3;


                        /* red component */
                        bit0 = (color_prom.read(Machine.drv.total_colors) >> 0) & 0x01;
                        bit1 = (color_prom.read(Machine.drv.total_colors) >> 1) & 0x01;
                        bit2 = (color_prom.read(Machine.drv.total_colors) >> 2) & 0x01;
                        bit3 = (color_prom.read(Machine.drv.total_colors) >> 3) & 0x01;
                        palette.writeinc(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                        /* green component */
                        bit0 = (color_prom.read(0) >> 4) & 0x01;
                        bit1 = (color_prom.read(0) >> 5) & 0x01;
                        bit2 = (color_prom.read(0) >> 6) & 0x01;
                        bit3 = (color_prom.read(0) >> 7) & 0x01;
                        palette.writeinc(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
                        /* blue component */
                        bit0 = (color_prom.read(0) >> 0) & 0x01;
                        bit1 = (color_prom.read(0) >> 1) & 0x01;
                        bit2 = (color_prom.read(0) >> 2) & 0x01;
                        bit3 = (color_prom.read(0) >> 3) & 0x01;
                        palette.writeinc(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);

                        color_prom.inc();
                }
                //color_prom.write(color_prom.read()+Machine.drv.total_colors);
                color_prom.inc(Machine.drv.total_colors);
                //color_prom += Machine.drv.total_colors;
                /* color_prom now points to the beginning of the lookup table */

                /* characters use colors 0-15 */
                for (i = 0;i < TOTAL_COLORS(0);i++)
                        colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char)(color_prom.readinc() & 0x0f);

                /* sprites use colors 16-31 */
                for (i = 0;i < TOTAL_COLORS(1);i++)
                       colortable_table[Machine.drv.gfxdecodeinfo[1].color_codes_start + i] = (char)((color_prom.readinc() & 0x0f) + 0x10);
        }};





        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr sonson_vh_screenrefresh= new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
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
                                                videoram.read(offs) + 256 * (colorram.read(offs) & 3),
                                                colorram.read(offs) >> 2,
                                                0,0,
                                                8*sx,8*sy,
                                                null,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the background graphics */
                {
                        int i;
                        int[] scroll=new int[32];


                        for (i = 0;i < 5;i++)
                                scroll[i] = 0;
                        for (i = 5;i < 32;i++)
                                scroll[i] = -(sonson_scrollx.read());

                        copyscrollbitmap(bitmap,tmpbitmap,32,scroll,0,null,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }


                /* draw the sprites */
                for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
                {
                        drawgfx(bitmap,Machine.gfx[1],
                                spriteram.read(offs + 2) + ((spriteram.read(offs + 1) & 0x20) << 3),
                                spriteram.read(offs + 1) & 0x1f,
                                ~spriteram.read(offs + 1) & 0x40,~spriteram.read(offs + 1) & 0x80,
                                spriteram.read(offs + 3),spriteram.read(offs + 0),
                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                }
        }};   
}
