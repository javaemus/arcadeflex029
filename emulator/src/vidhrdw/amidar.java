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

public class amidar
{

	public static CharPtr amidar_attributesram = new CharPtr();
        public static int[] flipscreen=new int[2];
        
        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
        
        public static VhConvertColorPromPtr amidar_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
	{
            	int i;
                CharPtr color_prom = new CharPtr(color_prom_table);
                CharPtr palette= new CharPtr(palette_table);
                for (i = 0;i < Machine.drv.total_colors;i++)
                {
                        int bit0,bit1,bit2;                    
                        /* red component */
                        bit0 = (color_prom.read() >> 0) & 0x01;
                        bit1 = (color_prom.read() >> 1) & 0x01;
                        bit2 = (color_prom.read() >> 2) & 0x01;
                        palette.writeinc(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                        /* green component */
                        bit0 = (color_prom.read() >> 3) & 0x01;
                        bit1 = (color_prom.read() >> 4) & 0x01;
                        bit2 = (color_prom.read() >> 5) & 0x01;
                        palette.writeinc(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                        /* blue component */
                        bit0 = 0;
                        bit1 = (color_prom.read() >> 6) & 0x01;
                        bit2 = (color_prom.read() >> 7) & 0x01;
                        palette.writeinc(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);

                        color_prom.inc();
                }


                /* characters and sprites use the same palette */
                for (i = 0;i < TOTAL_COLORS(0);i++)
                {
                        if ((i & 3)!=0)  colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char)i;
                        else  colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = 0;	/* 00 is always black, regardless of the contents of the PROM */
                }
        } };
        public static WriteHandlerPtr amidar_flipx_w = new WriteHandlerPtr() { public void handler(int offset, int data)
        {
                if (flipscreen[0] != (data & 1))
                {
                        flipscreen[0] = data & 1;
                        memset(dirtybuffer,1,videoram_size[0]);
                }
        }};
        public static WriteHandlerPtr amidar_flipy_w = new WriteHandlerPtr() { public void handler(int offset, int data)
        {
                if (flipscreen[1] != (data & 1))
                {
                        flipscreen[1] = data & 1;
                        memset(dirtybuffer,1,videoram_size[0]);
                }
        }};

	public static WriteHandlerPtr amidar_attributes_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if ((offset & 1) != 0 && amidar_attributesram.read(offset) != data)
		{
			int i;


			for (i = offset / 2;i < videoram_size[0];i += 32)
				dirtybuffer[i] = 1;
		}

		amidar_attributesram.write(offset, data);
	} };



	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
	public static VhUpdatePtr amidar_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
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

                                if (flipscreen[0]!=0) sx = 31 - sx;
                                if (flipscreen[1]!=0) sy = 31 - sy;

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                                videoram.read(offs),
                                                amidar_attributesram.read(2 * (offs % 32) + 1) & 0x07,
                                                flipscreen[0],flipscreen[1],
                                                8*sx,8*sy,
                                                Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the temporary bitmap to the screen */
                copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);


                /* Draw the sprites. Note that it is important to draw them exactly in this */
                /* order, to have the correct priorities. */
                for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
                {
                        int flipx,flipy,sx,sy;


                        flipx = spriteram.read(offs + 1) & 0x40;
                        flipy = spriteram.read(offs + 1) & 0x80;
                        sx = (spriteram.read(offs + 3) + 1) & 0xff;	/* ??? */
                        sy = 240 - spriteram.read(offs);

                        if (flipscreen[0]!=0)
                        {
                                flipx = NOT(flipx);
                                sx = 241 - sx;	/* note: 241, not 240 */
                        }
                        if (flipscreen[1]!=0)
                        {
                                flipy = NOT(flipy);
                                sy = 240 - sy;
                        }

                        /* Sprites #0, #1 and #2 need to be offset one pixel to be correctly */
                        /* centered on the ladders in Turtles (we move them down, but since this */
                        /* is a rotated game, we actually move them left). */
                        /* Note that the adjustement must be done AFTER handling flipscreen, thus */
                        /* proving that this is a hardware related "feature" */
                        if (offs <= 2*4) sy++;

                        drawgfx(bitmap,Machine.gfx[1],
                                        spriteram.read(offs + 1) & 0x3f,
                                        spriteram.read(offs + 2) & 0x07,
                                        flipx,flipy,
                                        sx,sy,
                                        Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                }
	} };
};