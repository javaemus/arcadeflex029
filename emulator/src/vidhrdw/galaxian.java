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
import static mame.common.*;
import static mame.commonH.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static mame.cpuintrf.*;

public class galaxian {
        static final int MAX_STARS = 250;
        static final int STARS_COLOR_BASE=32;
       
        public static CharPtr galaxian_attributesram = new CharPtr();
	public static CharPtr galaxian_bulletsram = new CharPtr();
        
        public static int[] galaxian_bulletsram_size= new int[1];
        static int stars_on,stars_blink;
        static int stars_type;	/* 0 = Galaxian stars  1 = Scramble stars */
        static int stars_scroll;

        static class star
	{
		public star() {};
		public int x,y,code,col;
	};
	static star stars[] = new star[MAX_STARS];
	static { for(int k = 0; k < MAX_STARS; k++) stars[k] = new star(); }
	static int total_stars;
        

        static int gfx_bank;	/* used by Pisces and "japirem" only */
        static int gfx_extend;	/* used by Moon Cresta only */
        static int[] flipscreen=new int[2];



        /***************************************************************************

          Convert the color PROMs into a more useable format.

          Moon Cresta has one 32 bytes palette PROM, connected to the RGB output
          this way:

          bit 7 -- 220 ohm resistor  -- BLUE
                -- 470 ohm resistor  -- BLUE
                -- 220 ohm resistor  -- GREEN
                -- 470 ohm resistor  -- GREEN
                -- 1  kohm resistor  -- GREEN
                -- 220 ohm resistor  -- RED
                -- 470 ohm resistor  -- RED
          bit 0 -- 1  kohm resistor  -- RED

          The output of the background star generator is connected this way:

          bit 5 -- 100 ohm resistor  -- BLUE
                -- 150 ohm resistor  -- BLUE
                -- 100 ohm resistor  -- GREEN
                -- 150 ohm resistor  -- GREEN
                -- 100 ohm resistor  -- RED
          bit 0 -- 150 ohm resistor  -- RED

        ***************************************************************************/
        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
        public static VhConvertColorPromPtr galaxian_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
	{
                int i;
                CharPtr color_prom = new CharPtr(color_prom_table);
                CharPtr palette= new CharPtr(palette_table);

                //#define TOTAL_COLORS(gfxn) (Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity)
               // #define COLOR(gfxn,offs) (colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + offs])


                /* first, the character/sprite palette */
                for (i = 0;i < 32;i++)
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
                /* now the stars */
                for (i = 0;i < 64;i++)
                {
                        int bits;
                        int map[] = { 0x00, 0x88, 0xcc, 0xff };


                        bits = (i >> 0) & 0x03;
                        palette.writeinc(map[bits]);
                        bits = (i >> 2) & 0x03;
                        palette.writeinc(map[bits]);
                        bits = (i >> 4) & 0x03;
                        palette.writeinc(map[bits]);
                }


                /* characters and sprites use the same palette */
                for (i = 0;i < TOTAL_COLORS(0);i++)
                {
                        if ((i & 3)!=0) colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] =  (char)i;
                        else colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i]  = 0;	/* 00 is always black, regardless of the contents of the PROM */
                }

                /* bullets can be either white or yellow */
                colortable_table[Machine.drv.gfxdecodeinfo[2].color_codes_start + 0] = 0;
                colortable_table[Machine.drv.gfxdecodeinfo[2].color_codes_start + 1] = 0x0f + STARS_COLOR_BASE;	/* yellow */
                colortable_table[Machine.drv.gfxdecodeinfo[2].color_codes_start + 2] = 0;
                colortable_table[Machine.drv.gfxdecodeinfo[2].color_codes_start + 3] = 0x3f + STARS_COLOR_BASE;	/* white */
        }};



        /***************************************************************************

          Start the video hardware emulation.

        ***************************************************************************/
        static int common_vh_start()
        {
                int generator;
                int x,y;


                gfx_bank = 0;
                gfx_extend = 0;
                stars_on = 0;

                if (generic_vh_start.handler() != 0)
                        return 1;


                /* precalculate the star background */
                total_stars = 0;
                generator = 0;

                for (y = 255;y >= 0;y--)
                {
                        for (x = 511;x >= 0;x--)
                        {
                                int bit1,bit2;


                                generator <<= 1;
                                bit1 = (~generator >> 17) & 1;
                                bit2 = (generator >> 5) & 1;

                                if ((bit1 ^ bit2) != 0) generator |= 1;

                                if (y >= Machine.drv.visible_area.min_y &&
                                                y <= Machine.drv.visible_area.max_y &&
                                                (((~generator >> 16) & 1)!=0) &&
                                                (generator & 0xff) == 0xff)
                                {
                                        int color;

                                        color = (~(generator >> 8)) & 0x3f;
                                       if (color != 0 && total_stars < MAX_STARS)
                                        {
                                                stars[total_stars].x = x;
                                                stars[total_stars].y = y;
                                                stars[total_stars].code = color;
                                                stars[total_stars].col = Machine.pens[color + STARS_COLOR_BASE];

                                                total_stars++;
                                        }
                                }
                        }
                }

                return 0;
        }
        public static VhStartPtr galaxian_vh_start = new VhStartPtr() {	public int handler()
	{
                stars_type = 0;
                return common_vh_start();
        }};
        public static VhStartPtr scramble_vh_start = new VhStartPtr() {	public int handler()
	{
                stars_type = 1;
                return common_vh_start();
        }};


        public static WriteHandlerPtr galaxian_flipx_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                if (flipscreen[0] != (data & 1))
                {
                        flipscreen[0] = data & 1;
                        memset(dirtybuffer,1,videoram_size[0]);
                }
        }};
        public static WriteHandlerPtr galaxian_flipy_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                if (flipscreen[1] != (data & 1))
                {
                        flipscreen[1] = data & 1;
                        memset(dirtybuffer,1,videoram_size[0]);
                }
        }};


        public static WriteHandlerPtr galaxian_attributes_w= new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                if ((offset & 1) != 0 && galaxian_attributesram.read(offset) != data)
                {
                        int i;


                        for (i = offset / 2;i < videoram_size[0];i += 32)
                                dirtybuffer[i] = 1;
                }

                galaxian_attributesram.write(offset, data);
        }};


        public static WriteHandlerPtr galaxian_stars_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                stars_on = (data & 1);
                stars_scroll = 0;
        }};


        public static WriteHandlerPtr pisces_gfxbank_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                gfx_bank = data & 1;
        }};


        public static WriteHandlerPtr mooncrst_gfxextend_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                if (data!=0) gfx_extend |= (1 << offset);
                else gfx_extend &= ~(1 << offset);
        }};



        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr galaxian_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                int i,offs;


                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if (dirtybuffer[offs]!=0)
                        {
                                int sx,sy,charcode;


                                dirtybuffer[offs] = 0;

                                sx = offs % 32;
                                sy = offs / 32;

                                if (flipscreen[0]!=0) sx = 31 - sx;
                                if (flipscreen[1]!=0) sy = 31 - sy;


                                charcode = videoram.read(offs);

                                /* bit 5 of [2*(offs%32)+1] is used only by Moon Quasar */
                                if ((galaxian_attributesram.read(2 * (offs % 32) + 1) & 0x20)!=0)
                                        charcode += 256;

                                /* gfx_bank is used by Pisces and japirem/Uniwars only */
                                if (gfx_bank!=0)
                                        charcode += 256;

                                /* gfx_extend is used by Moon Cresta only */
                                if (((gfx_extend & 4)!=0) && (charcode & 0xc0) == 0x80)
                                        charcode = (charcode & 0x3f) | (gfx_extend << 6);

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                                charcode,
                                                galaxian_attributesram.read(2 * (offs % 32) + 1) & 0x07,
                                                flipscreen[0],flipscreen[1],
                                                8*sx,8*sy,
                                                null,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the temporary bitmap to the screen */
                {
                        int scroll[]=new int[32];


                        for (i = 0;i < 32;i++)
                                scroll[i] = -galaxian_attributesram.read(2 * i);

                        copyscrollbitmap(bitmap,tmpbitmap,0,null,32,scroll,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }


                /* Draw the bullets */  //shadow : this appear to be buggy... Use the old way to draw bullets...
                for (offs = 0;offs < galaxian_bulletsram_size[0];offs += 4)
                {
                        int x,y;
                        int color;


                        if (offs == 7*4) color = 0;	/* yellow */
                        else color = 1;	/* white */

                        x = 255 - galaxian_bulletsram.read(offs + 3) - Machine.drv.gfxdecodeinfo[2].gfxlayout.width;
                        y = 256 - galaxian_bulletsram.read(offs + 1) - Machine.drv.gfxdecodeinfo[2].gfxlayout.height;

                        drawgfx(bitmap,Machine.gfx[2],
                                        0,	/* this is just a line, generated by the hardware */
                                        color,
                                        0,0,
                                        x,y,
                                        Machine.drv.visible_area,TRANSPARENCY_NONE,0);//TODO shadow : this should be transparency PEN but it doesn't work
                }


                /* Draw the sprites */
                for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
                {
                        int flipx,flipy,sx,sy,spritecode;


                        sx = spriteram.read(offs + 3);
                        if (sx > 8)	/* ??? */
                        {
                                sy = 240 - spriteram.read(offs);
                                flipx = spriteram.read(offs + 1) & 0x40;
                                flipy = spriteram.read(offs + 1) & 0x80;

                                if (flipscreen[0]!=0)
                                {
                                        flipx = NOT(flipx);
                                        sx = 240 - sx;
                                }
                                if (flipscreen[1]!=0)
                                {
                                        flipy = NOT(flipy);
                                        sy = 240 - sy;
                                }

                                spritecode = spriteram.read(offs + 1) & 0x3f;

                                /* bit 4 of [offs+2] is used only by Crazy Kong */
                                if ((spriteram.read(offs + 2) & 0x10)!=0)
                                        spritecode += 64;

                                /* bit 5 of [offs+2] is used only by Moon Quasar */
                                if ((spriteram.read(offs + 2) & 0x20)!=0)
                                        spritecode += 64;

                                /* gfx_bank is used by Pisces and japirem/Uniwars only */
                                if (gfx_bank!=0)
                                        spritecode += 64;

                                /* gfx_extend is used by Moon Cresta only */
                                if (((gfx_extend & 4)!=0) && (spritecode & 0x30) == 0x20)
                                        spritecode = (spritecode & 0x0f) | (gfx_extend << 4);

                                drawgfx(bitmap,Machine.gfx[1],
                                                spritecode,
                                                spriteram.read(offs + 2) & 0x07,
                                                flipx,flipy,
                                                sx,sy,
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                        }
                }


                /* draw the stars */
                if (stars_on!=0)
                {
                        int bpen;


                        bpen = Machine.pens[0];

                        switch (stars_type)
                        {
                                case 0:	/* Galaxian stars */
                                        for (offs = 0;offs < total_stars;offs++)
                                        {
                                                int x,y;


                                                x = (stars[offs].x + stars_scroll/2) % 256;
                                                y = stars[offs].y;

                                                if (((y & 1)!=0) ^ (((x >> 4) & 1)!=0))
                                                {
                                                        if ((Machine.orientation & ORIENTATION_SWAP_XY)!=0)
                                                        {
                                                                int temp;


                                                                temp = x;
                                                                x = y;
                                                                y = temp;
                                                        }
                                                        if ((Machine.orientation & ORIENTATION_FLIP_X)!=0)
                                                                x = 255 - x;
                                                        if ((Machine.orientation & ORIENTATION_FLIP_Y)!=0)
                                                                y = 255 - y;

                                                        if (bitmap.line[y][x] == bpen)
                                                                bitmap.line[y][x] = (char)(stars[offs].col);
                                                }
                                        }
                                        break;

                                case 1:	/* Scramble stars */
                                        for (offs = 0;offs < total_stars;offs++)
                                        {
                                                int x,y;


                                                x = stars[offs].x / 2;
                                                y = stars[offs].y;

                                                if (((y & 1)!=0) ^ (((x >> 4) & 1)!=0))
                                                {
                                                        if ((Machine.orientation & ORIENTATION_SWAP_XY)!=0)
                                                        {
                                                                int temp;


                                                                temp = x;
                                                                x = y;
                                                                y = temp;
                                                        }
                                                        if ((Machine.orientation & ORIENTATION_FLIP_X)!=0)
                                                                x = 255 - x;
                                                        if ((Machine.orientation & ORIENTATION_FLIP_Y)!=0)
                                                                y = 255 - y;

                                                        if (bitmap.line[y][x] == bpen)
                                                        {
                                                                switch (stars_blink)
                                                                {
                                                                        case 0:
                                                                                if ((stars[offs].code & 1) != 0) bitmap.line[y][x] = (char) stars[offs].col;
                                                                                break;
                                                                        case 1:
                                                                                if ((stars[offs].code & 4) != 0) bitmap.line[y][x] = (char) stars[offs].col;
                                                                                break;
                                                                        case 2:
                                                                                if ((x & 2) != 0) bitmap.line[y][x] = (char) stars[offs].col;
                                                                                break;
                                                                        case 3:
                                                                                bitmap.line[y][x] = (char) stars[offs].col;
                                                                                break;
                                                                }
                                                        }
                                                }
                                        }
                                        break;
                        }
                }
        }};


        public static InterruptPtr galaxian_vh_interrupt = new InterruptPtr() { public int handler()
	{
                stars_scroll++;

                return nmi_interrupt.handler();
        }};

         static int blink_count;
        public static InterruptPtr scramble_vh_interrupt = new InterruptPtr() { public int handler()
	{
               
                blink_count++;
                if (blink_count >= 43)
                {
                        blink_count = 0;
                        stars_blink = (stars_blink + 1) % 4;
                }

                return nmi_interrupt.handler();
        }};
   
}
