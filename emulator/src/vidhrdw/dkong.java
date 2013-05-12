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
 * rewrote to v0.28
 * 
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

public class dkong {
        static int gfx_bank,palette_bank;
        public static CharPtr color_codes= new CharPtr();


        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
        public static VhConvertColorPromPtr dkong_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
	{
                int i;
                CharPtr color_prom = new CharPtr(color_prom_table);
                CharPtr palette= new CharPtr(palette_table);
                float r0,r1,r2,p1,p2,max,min;
                r0 = 1000;
                r1 = 470;
                r2 = 220;
                p1 = 470;
                p2 = 680;


                max = 1/r0 + 1/r1 + 1/r2 + 1/p2;
                min = 1/p2;

                for (i = 0;i < Machine.drv.total_colors;i++)
                {
                        int bit0,bit1,bit2,val;


                        /* red component */
                        bit0 = (color_prom.read(0) >> 1) & 1;
                        bit1 = (color_prom.read(0) >> 2) & 1;
                        bit2 = (color_prom.read(0) >> 3) & 1;
                        val = (int)(255 - (bit0/r0 + bit1/r1 + bit2/r2 + 1/p1 - min) * 255 / (max-min));
                        if (val < 0) val = 0;
                        palette.writeinc(val);
                        /* green component */
                        bit0 = (color_prom.read(Machine.drv.total_colors) >> 2) & 1;
                        bit1 = (color_prom.read(Machine.drv.total_colors) >> 3) & 1;
                        bit2 = (color_prom.read(0) >> 0) & 1;
                        val = (int)(255 - (bit0/r0 + bit1/r1 + bit2/r2 + 1/p1 - min) * 255 / (max-min));
                        if (val < 0) val = 0;
                        palette.writeinc(val);
                        /* blue component */
                        bit0 = 0;
                        bit1 = (color_prom.read(Machine.drv.total_colors) >> 0) & 1;
                        bit2 = (color_prom.read(Machine.drv.total_colors) >> 1) & 1;
                        val = (int)(255 - (bit0/r0 + bit1/r1 + bit2/r2 + 1/p2 - min) * 255 / (max-min));
                        if (val < 0) val = 0;
                        palette.writeinc(val);

                        color_prom.inc();
                }

                color_prom.inc(Machine.drv.total_colors);
                /* color_prom now points to the beginning of the character color codes */
                color_codes = color_prom;	/* we'll need it later */

                /* sprites use the same palette as characters */
                for (i = 0;i < TOTAL_COLORS(0);i++)
                        colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char)i;
        }};

        public static VhStartPtr dkong_vh_start = new VhStartPtr() { public int handler()
	{
		gfx_bank = 0;
		palette_bank = 0;
		return generic_vh_start.handler();
	} };



        public static WriteHandlerPtr dkongjr_gfxbank_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (gfx_bank != (data & 1))
                {
                        gfx_bank = data & 1;
                        memset(dirtybuffer,1,videoram_size[0]);
                }
	} };


        public static WriteHandlerPtr dkong3_gfxbank_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                if (gfx_bank != (~data & 1))
                {
                        gfx_bank = ~data & 1;
                        memset(dirtybuffer,1,videoram_size[0]);
                }
        }};


        public static WriteHandlerPtr dkong_palettebank_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                int newbank;


                newbank = palette_bank;
                if ((data & 1)!=0)
                        newbank |= 1 << offset;
                else
                        newbank &= ~(1 << offset);

                if (palette_bank != newbank)
                {
                        palette_bank = newbank;
                        memset(dirtybuffer,1,videoram_size[0]);
                }
        }};



        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr dkong_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                int offs;


                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if (dirtybuffer[offs]!=0)
                        {
                                int sx,sy;
                                int charcode,color;


                                dirtybuffer[offs] = 0;

                                sx = offs % 32;
                                sy = offs / 32;

                                charcode = videoram.read(offs) + 256 * gfx_bank;
                                /* retrieve the character color from the PROM */
                                color = (color_codes.read(offs % 32) & 0x0f) + 0x10 * palette_bank;

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                                charcode,color,
                                                0,0,
                                                8*sx,8*sy,
                                                Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the character mapped graphics */
                copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);


                /* Draw the sprites. */
                for (offs = 0;offs < spriteram_size[0];offs += 4)
                {
                        if (spriteram.read(offs)!=0)
                        {
                                /* spriteram[offs + 2] & 0x40 is used by Donkey Kong 3 only */
                                /* spriteram[offs + 2] & 0x30 don't seem to be used (they are */
                                /* probably not part of the color code, since Mario Bros, which */
                                /* has similar hardware, uses a memory mapped port to change */
                                /* palette bank, so it's limited to 16 color codes) */
                                drawgfx(bitmap,Machine.gfx[1],
                                                (spriteram.read(offs + 1) & 0x7f) + 2 * (spriteram.read(offs + 2) & 0x40),
                                                (spriteram.read(offs + 2) & 0x0f) + 16 * palette_bank,
                                                spriteram.read(offs + 2) & 0x80,spriteram.read(offs + 1) & 0x80,
                                                spriteram.read(offs + 3) - 8,240 - spriteram.read(offs) + 7,
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                        }
                }
        }};



        /* this would be the same as dkong, but since we don't have the color PROMs for */
        /* dkongjr and dkong3, we have to pick the character colors in a different way */
        public static VhUpdatePtr dkongjr_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                int offs;


                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if (dirtybuffer[offs]!=0)
                        {
                                int sx,sy;
                                int charcode,color;


                                dirtybuffer[offs] = 0;

                                sx = offs % 32;
                                sy = offs / 32;

                                charcode = videoram.read(offs) + 256 * gfx_bank;
                                color = charcode >> 2;	/* this is wrong, but gives a reasonable approximation */

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                                charcode,color,
                                                0,0,
                                                8*sx,8*sy,
                                                Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the character mapped graphics */
                copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);


                /* Draw the sprites. */
                for (offs = 0;offs < spriteram_size[0];offs += 4)
                {
                        if (spriteram.read(offs)!=0)
                        {
                                /* spriteram[offs + 2] & 0x40 is used by Donkey Kong 3 only */
                                /* spriteram[offs + 2] & 0x30 don't seem to be used (they are */
                                /* probably not part of the color code, since Mario Bros, which */
                                /* has similar hardware, uses a memory mapped port to change */
                                /* palette bank, so it's limited to 16 color codes) */
                                drawgfx(bitmap,Machine.gfx[1],
                                                (spriteram.read(offs + 1) & 0x7f) + 2 * (spriteram.read(offs + 2) & 0x40),
                                                (spriteram.read(offs + 2) & 0x0f) + 16 * palette_bank,
                                                spriteram.read(offs + 2) & 0x80,spriteram.read(offs + 1) & 0x80,
                                                spriteram.read(offs + 3) - 8,240 - spriteram.read(offs) + 7,
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                        }
                }
        }};  
}
