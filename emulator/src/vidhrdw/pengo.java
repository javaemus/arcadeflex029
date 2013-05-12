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

public class pengo
{

	static int gfx_bank;
        static int flipscreen;
        static int xoffsethack;


	static rectangle spritevisiblearea = new rectangle
	(
		0*8, 28*8-1,
                2*8, 34*8-1
	);



	//#define TOTAL_COLORS(gfxn) (Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity)
	//#define COLOR(gfxn,offs) (colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + offs])
	static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
        static int COLOR(char []colortable,int gfxn,int offs) 
        {
            return colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + offs];
        }
                
        
                
        public static VhConvertColorPromPtr pengo_vh_convert_color_prom = new VhConvertColorPromPtr() {	public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
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
                        bit0 = (color_prom.read()>> 3) & 0x01;
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
                /* color_prom now points to the beginning of the lookup table */

                /* character lookup table */
                /* sprites use the same color lookup table as characters */
                for (i = 0;i < TOTAL_COLORS(0);i++)
                        colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i]=color_prom.readinc();
                        //COLOR(colortable_table,0,i) = color_prom.readinc();

                if (Machine.gfx[2]!=null)	/* only Pengo has the second gfx bank */
                {
                        /* second bank character lookup table */
                        /* sprites use the same color lookup table as characters */
                        for (i = 0;i < TOTAL_COLORS(2);i++)
                        {
                            
                                if (color_prom.read()!=0) 
                                {
                                    colortable_table[Machine.drv.gfxdecodeinfo[2].color_codes_start + i]=(char)(color_prom.read() +0x10);/* second palette bank */
                                }
                                else
                                {
                                    colortable_table[Machine.drv.gfxdecodeinfo[2].color_codes_start + i]=0;/* preserve transparency */
                                }	

                                color_prom.inc();
                        }
                }
                
	} };



	public static VhStartPtr pengo_vh_start = new VhStartPtr() { public int handler()
	{
		gfx_bank = 0;
                flipscreen = 0;
                xoffsethack = 0;

		return generic_vh_start.handler();
	} };

	public static VhStartPtr pacman_vh_start = new VhStartPtr() { public int handler()
	{
                gfx_bank = 0;
                flipscreen = 0;
                /* In the Pac Man based games (NOT Pengo) the first two sprites must be offset */
                /* one pixel to the left to get a more correct placement */
                xoffsethack = 1;

		return generic_vh_start.handler();
	} };
        public static WriteHandlerPtr pengo_flipscreen_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
        {
                if (flipscreen != (data & 1))
                {
                        flipscreen = data & 1;
                        memset(dirtybuffer,1,videoram_size[0]);
                }
        }};
	public static WriteHandlerPtr pengo_gfxbank_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
		/* the Pengo hardware can set independently the palette bank, color lookup */
		/* table, and chars/sprites. However the game always set them together (and */
		/* the only place where this is used is the intro screen) so I don't bother */
		/* emulating the whole thing. */
		gfx_bank = data & 1;
	} };



	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
	public static VhUpdatePtr pengo_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int offs;


		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			if (dirtybuffer[offs] != 0)
			{
				int sx, sy, mx, my;


				dirtybuffer[offs] = 0;

		/* Even if Pengo's screen is 28x36, the memory layout is 32x32. We therefore */
		/* have to convert the memory coordinates into screen coordinates. */
		/* Note that 32*32 = 1024, while 28*36 = 1008: therefore 16 bytes of Video RAM */
		/* don't map to a screen position. We don't check that here, however: range */
		/* checking is performed by drawgfx(). */

				mx = offs / 32;
				my = offs % 32;

				if (mx <= 1)
				{
					sx = 29 - my;
					sy = mx + 34;
				}
				else if (mx >= 30)
				{
					sx = 29 - my;
					sy = mx - 30;
				}
				else
				{
					sx = 29 - mx;
					sy = my + 2;
				}
                                if (flipscreen!=0)
                                {
                                        sx = 27 - sx;
                                        sy = 35 - sy;
                                }
                                drawgfx(tmpbitmap,Machine.gfx[2 * gfx_bank],
					videoram.read(offs),
					colorram.read(offs),
					flipscreen,flipscreen,
					8*sx,8*sy,
					Machine.drv.visible_area,TRANSPARENCY_NONE,0);
			}
		}

		/* copy the character mapped graphics */
		copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);

		/* Draw the sprites. Note that it is important to draw them exactly in this */
		/* order, to have the correct priorities. */
		/* sprites #0 and #7 are not used */
                for (offs = spriteram_size[0] - 2;offs > 2*2;offs -= 2)
                {
                        drawgfx(bitmap,Machine.gfx[1 + 2 * gfx_bank],
                                        spriteram.read(offs) >> 2,spriteram.read(offs + 1),
                                        spriteram.read(offs) & 2,spriteram.read(offs) & 1,
                                        239 - spriteram_2.read(offs),272 - spriteram_2.read(offs + 1),
                                        spritevisiblearea,TRANSPARENCY_COLOR,0);

                        /* also plot the sprite 256 pixels higher for vertical wraparound (tunnel in */
                        /* Crush Roller) */
                        drawgfx(bitmap,Machine.gfx[1 + 2 * gfx_bank],
                                        spriteram.read(offs) >> 2,spriteram.read(offs + 1),
                                        spriteram.read(offs) & 2,spriteram.read(offs) & 1,
                                        239 - spriteram_2.read(offs),272-256 - spriteram_2.read(offs + 1),
                                        spritevisiblearea,TRANSPARENCY_COLOR,0);
                }
                /* In the Pac Man based games (NOT Pengo) the first two sprites must be offset */
                /* one pixel to the left to get a more correct placement */
                for (offs = 2*2;offs >= 0;offs -= 2)
                {
                        drawgfx(bitmap,Machine.gfx[1 + 2 * gfx_bank],
                                        spriteram.read(offs) >> 2,spriteram.read(offs + 1),
                                        spriteram.read(offs) & 2,spriteram.read(offs) & 1,
                                        239 - xoffsethack - spriteram_2.read(offs),272 - spriteram_2.read(offs + 1),
                                        spritevisiblearea,TRANSPARENCY_COLOR,0);

                        /* also plot the sprite 256 pixels higher for vertical wraparound (tunnel in */
                        /* Crush Roller) */
                        drawgfx(bitmap,Machine.gfx[1 + 2 * gfx_bank],
                                        spriteram.read(offs) >> 2,spriteram.read(offs + 1),
                                        spriteram.read(offs) & 2,spriteram.read(offs) & 1,
                                        239 - xoffsethack - spriteram_2.read(offs),272-256 - spriteram_2.read(offs + 1),
                                        spritevisiblearea,TRANSPARENCY_COLOR,0);
                }
	} };
}
