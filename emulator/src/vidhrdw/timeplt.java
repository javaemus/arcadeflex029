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

public class timeplt
{
	static rectangle spritevisiblearea = new rectangle
	(
		2*8, 30*8-1,
		4*8, 31*8-1
	);


        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
	public static VhConvertColorPromPtr timeplt_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
	{
            	int i;
                CharPtr color_prom = new CharPtr(color_prom_table);
                CharPtr palette= new CharPtr(palette_table);

		for (i = 0;i < Machine.drv.total_colors;i++)
                {
                        int bit0,bit1,bit2,bit3,bit4;


                        bit0 = (color_prom.read(Machine.drv.total_colors) >> 1) & 0x01;
                        bit1 = (color_prom.read(Machine.drv.total_colors) >> 2) & 0x01;
                        bit2 = (color_prom.read(Machine.drv.total_colors) >> 3) & 0x01;
                        bit3 = (color_prom.read(Machine.drv.total_colors) >> 4) & 0x01;
                        bit4 = (color_prom.read(Machine.drv.total_colors) >> 5) & 0x01;
                        palette.writeinc(0x19 * bit0 + 0x24 * bit1 + 0x35 * bit2 + 0x40 * bit3 + 0x4d * bit4);
                        bit0 = (color_prom.read(Machine.drv.total_colors) >> 6) & 0x01;
                        bit1 = (color_prom.read(Machine.drv.total_colors) >> 7) & 0x01;
                        bit2 = (color_prom.read(0) >> 0) & 0x01;
                        bit3 = (color_prom.read(0) >> 1) & 0x01;
                        bit4 = (color_prom.read(0) >> 2) & 0x01;
                        palette.writeinc(0x19 * bit0 + 0x24 * bit1 + 0x35 * bit2 + 0x40 * bit3 + 0x4d * bit4);
                        bit0 = (color_prom.read(0) >> 3) & 0x01;
                        bit1 = (color_prom.read(0) >> 4) & 0x01;
                        bit2 = (color_prom.read(0) >> 5) & 0x01;
                        bit3 = (color_prom.read(0) >> 6) & 0x01;
                        bit4 = (color_prom.read(0) >> 7) & 0x01;
                        palette.writeinc(0x19 * bit0 + 0x24 * bit1 + 0x35 * bit2 + 0x40 * bit3 + 0x4d * bit4);

                        color_prom.inc();
                }

                color_prom.inc(Machine.drv.total_colors);
                /* color_prom now points to the beginning of the lookup table */


                /* sprites */
                for (i = 0;i < TOTAL_COLORS(1);i++)
                    colortable_table[Machine.drv.gfxdecodeinfo[1].color_codes_start + i] = (char)(color_prom.readinc() & 0x0f);
                       // COLOR(1,i) = *(color_prom++) & 0x0f;

                /* characters */
                for (i = 0;i < TOTAL_COLORS(0);i++)
                    colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char)((color_prom.readinc() & 0x0f) + 0x10);
                        //COLOR(0,i) = (*(color_prom++) & 0x0f) + 0x10;

	} };



	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
	public static VhUpdatePtr timeplt_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int offs;


		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			if (dirtybuffer[offs] != 0)
			{
				int sx, sy;


				dirtybuffer[offs] = 0;

				sx = 8 * (31 - offs / 32);
				sy = 8 * (offs % 32);

				drawgfx(tmpbitmap, Machine.gfx[0],
						videoram.read(offs) + 8 * (colorram.read(offs) & 0x20),
						colorram.read(offs) & 0x1f,
						colorram.read(offs) & 0x80, colorram.read(offs) & 0x40,
						sx, sy,
						Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
			}
		}


		/* copy the character mapped graphics */
		copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);

                /* In Time Pilot, the characters can appear either behind or in front of the */
                /* sprites. The priority is selected by bit 4 of the color attribute of the */
                /* character. This feature is used to limit the sprite visibility area, and */
                /* as a sort of copyright notice protection ("KONAMI" on the title screen */
                /* alternates between characters and sprites, but they are both white so you */
                /* can't see it). To speed up video refresh, we do the sprite clipping ourselves. */

                /* Draw the sprites. Note that it is important to draw them exactly in this */
                /* order, to have the correct priorities. */
		for (offs = spriteram_size[0] - 2;offs >= 0;offs -= 2)
		{
		     /* handle double width sprites (clouds) */
		    if (offs <= 2*2 || offs >= 19*2)
				drawgfx(bitmap, Machine.gfx[2],
						spriteram.read(offs + 1),
						spriteram_2.read(offs) & 0x3f,
						spriteram_2.read(offs) & 0x80, (spriteram_2.read(offs) & 0x40) == 0 ? 1 : 0,
						2 * spriteram_2.read(offs + 1) - 16, spriteram.read(offs),
						spritevisiblearea, TRANSPARENCY_PEN, 0);
			else
				drawgfx(bitmap, Machine.gfx[1],
						spriteram.read(offs + 1),
						spriteram_2.read(offs) & 0x3f,
						spriteram_2.read(offs) & 0x80, (spriteram_2.read(offs) & 0x40) == 0 ? 1 : 0,
						spriteram_2.read(offs + 1) - 1, spriteram.read(offs),
						spritevisiblearea, TRANSPARENCY_PEN, 0);
		}
	} };
}