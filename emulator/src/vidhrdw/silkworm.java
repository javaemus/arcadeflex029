/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.29
 * using automatic conversion tool v0.04
 * converted at : 31-05-2012 00:00:07
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
import static arcadeflex.osdepend.*;

public class silkworm
{
	
	
	public static CharPtr silkworm_videoram=new CharPtr();
        public static CharPtr silkworm_colorram=new CharPtr();
	public static CharPtr silkworm_videoram2=new CharPtr();
        public static CharPtr silkworm_colorram2=new CharPtr();
	public static CharPtr silkworm_scroll=new CharPtr();
	public static CharPtr  silkworm_paletteram=new CharPtr();
	public static int[] silkworm_videoram2_size=new int[1];
	
	static char[] dirtybuffer2;
	static osd_bitmap tmpbitmap2,tmpbitmap3;
	static char[] dirtycolor=new char[64];
	
	
	
	/***************************************************************************
	
	  Convert the color PROMs into a more useable format.
	
	  Silkworm doesn't have a color PROM. It uses 1024 bytes of RAM to
	  dynamically create the palette. Each couple of bytes defines one
	  color (4 bits per pixel; the high 4 bits of the first byte are unused).
	  Since the graphics use 4 bitplanes, hence 16 colors, this makes for 64
	  different color codes, divided in four groups of 16 (sprites, characters,
	  bg #1, bg #2).
	
	  I don't know the exact values of the resistors between the RAM and the
	  RGB output. I assumed these values (the same as Commando)
	  bit 7 -- unused
	        -- unused
	        -- unused
	        -- unused
	        -- 220 ohm resistor  -- BLUE
	        -- 470 ohm resistor  -- BLUE
	        -- 1  kohm resistor  -- BLUE
	  bit 0 -- 2.2kohm resistor  -- BLUE
	
	  bit 7 -- 220 ohm resistor  -- RED
	        -- 470 ohm resistor  -- RED
	        -- 1  kohm resistor  -- RED
	        -- 2.2kohm resistor  -- RED
	        -- 220 ohm resistor  -- GREEN
	        -- 470 ohm resistor  -- GREEN
	        -- 1  kohm resistor  -- GREEN
	  bit 0 -- 2.2kohm resistor  -- GREEN
	
	***************************************************************************/
	public static VhConvertColorPromPtr silkworm_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
	{
		int i;
	
                CharPtr color_prom = new CharPtr(color_prom_table);
                CharPtr palette= new CharPtr(palette_table);
		/* we can use only 256 colors, so we'll have to degrade the */
		/* 4x4x4 color space to 3x3x2 */
		for (i = 0;i < Machine.drv.total_colors;i++)
		{
			int bit0,bit1,bit2;
	
	
			/* red component */
			bit0 = (i >> 3) & 0x01;
			bit1 = (i >> 4) & 0x01;
			bit2 = (i >> 5) & 0x01;
			palette.writeinc(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
			/* green component */
			bit0 = (i >> 0) & 0x01;
			bit1 = (i >> 1) & 0x01;
			bit2 = (i >> 2) & 0x01;
			palette.writeinc(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
			/* blue component */
			bit0 = 0;
			bit1 = (i >> 6) & 0x01;
			bit2 = (i >> 7) & 0x01;
			palette.writeinc(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
		}
	} };
	
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	public static VhStartPtr silkworm_vh_start = new VhStartPtr() { public int handler() 
	{
		if (generic_vh_start.handler() != 0)
			return 1;
	
		if ((dirtybuffer2 = new char[videoram_size[0]]) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}
		memset(dirtybuffer2,1,videoram_size[0]);
	
		/* the background area is twice as wide as the screen */
		if ((tmpbitmap2 = osd_create_bitmap(2*Machine.drv.screen_width,Machine.drv.screen_height)) == null)
		{
			dirtybuffer2=null;
			generic_vh_stop.handler();
			return 1;
		}
		if ((tmpbitmap3 = osd_create_bitmap(2*Machine.drv.screen_width,Machine.drv.screen_height)) == null)
		{
			osd_free_bitmap(tmpbitmap2);
                        tmpbitmap2=null;
			dirtybuffer2=null;
			generic_vh_stop.handler();
			return 1;
		}
	
		return 0;
	} };
	
	
	
	/***************************************************************************
	
	  Stop the video hardware emulation.
	
	***************************************************************************/
	public static VhStopPtr silkworm_vh_stop = new VhStopPtr() { public void handler() 
	{
		osd_free_bitmap(tmpbitmap3);
		osd_free_bitmap(tmpbitmap2);
		tmpbitmap2=null;
                tmpbitmap3=null;
		dirtybuffer2=null;
		generic_vh_stop.handler();
	} };
	
	
	
	public static WriteHandlerPtr silkworm_videoram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (silkworm_videoram.read(offset) != data)
		{
			dirtybuffer2[offset] = 1;
	
			silkworm_videoram.write(offset,data);
		}
	} };
	
	
	
	public static WriteHandlerPtr silkworm_colorram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (silkworm_colorram.read(offset) != data)
		{
			dirtybuffer2[offset] = 1;
	
			silkworm_colorram.write(offset,data);
		}
	} };
	
	
	
	public static WriteHandlerPtr silkworm_paletteram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (silkworm_paletteram.read(offset) != data)
		{
			dirtycolor[offset/2/16] = 1;
			silkworm_paletteram.write(offset,data);
	
			if ((offset & ~1) == 0x200)
			{
				int i;
	
	
				/* special case: color 0 of the character set is used for the */
				/* background. To speed up rendering, we set it in color 0 of bg #2 */
				for (i = 0;i < 16;i++)
					dirtycolor[48 + i] = 1;
			}
		}
	} };
	
	
	
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VhUpdatePtr silkworm_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
		int offs;
	
	
		/* rebuild the color lookup table */
		{
			int i,j;
			int gf[] = { 1, 0, 3, 4 };
			int off[] = { 0x000, 0x200, 0x400, 0x600 };
	
	
			for (j = 0;j < 4;j++)
			{
				for (i = 0;i < 16*16;i++)
				{
					int col;
	
	
					col = ((silkworm_paletteram.read(2*i+off[j]) & 0x0c) << 4) |
							((silkworm_paletteram.read(2*i+1+off[j]) & 0xe0) >> 2) |
							((silkworm_paletteram.read(2*i+1+off[j]) & 0x0e) >> 1);
	
					/* avoid undesired transparency using dark red instead of black */
					if (j == 2 && col == 0x00 && i % 16 != 0) col = 0x01;
	
					/* special case: color 0 of the character set is used for the */
					/* background. To speed up rendering, we set it in color 0 of bg #2 */
					if (j == 3 && i % 16 == 0)
						col = ((silkworm_paletteram.read(0x200) & 0x0c) << 4) |
								((silkworm_paletteram.read(0x201) & 0xe0) >> 2) |
								((silkworm_paletteram.read(0x201) & 0x0e) >> 1);
	
					Machine.gfx[gf[j]].colortable.write(i,Machine.pens[col]);
				}
			}
		}
	
	
	
		/* draw the background. */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			int color;
	
	
			color = colorram.read(offs) >> 4;
	
			if (dirtybuffer[offs]!=0 || dirtycolor[color + 48]!=0)
			{
				int sx,sy;
	
	
				dirtybuffer[offs] = 0;
	
				sx = offs % 32;
				sy = offs / 32;
	
				drawgfx(tmpbitmap2,Machine.gfx[4],
						videoram.read(offs) + 256 * (colorram.read(offs) & 0x07),
						color,
						0,0,
						16*sx,16*sy,
						null,TRANSPARENCY_NONE,0);
			}
	
	
			color = silkworm_colorram.read(offs) >> 4;
	
			if (dirtybuffer2[offs]!=0 || dirtycolor[color + 32]!=0)
			{
				int sx,sy;
	
	
				dirtybuffer2[offs] = 0;
	
				sx = offs % 32;
				sy = offs / 32;
	
				drawgfx(tmpbitmap3,Machine.gfx[3],
						silkworm_videoram.read(offs) + 256 * (silkworm_colorram.read(offs) & 0x07),
						color,
						0,0,
						16*sx,16*sy,
						null,TRANSPARENCY_NONE,0);
			}
		}
	
	
		for (offs = 0;offs < 64;offs++)
			dirtycolor[offs] = 0;
	
	
		/* copy the temporary bitmap to the screen */
		{
			int scrollx,scrolly;
	
	
			scrollx = -silkworm_scroll.read(3) - 256 * (silkworm_scroll.read(4) & 1) - 48;
			scrolly = -silkworm_scroll.read(5);
			copyscrollbitmap(bitmap,tmpbitmap2,1,new int[] {scrollx},1,new int[] {scrolly},Machine.drv.visible_area,TRANSPARENCY_NONE,0);
			scrollx = -silkworm_scroll.read(0) - 256 * (silkworm_scroll.read(1) & 1) - 48;
			scrolly = -silkworm_scroll.read(2);
			copyscrollbitmap(bitmap,tmpbitmap3,1,new int[] {scrollx},1,new int[] {scrolly},Machine.drv.visible_area,TRANSPARENCY_COLOR,0);
		}
	
	
		/* Draw the sprites. */
		for (offs = 0;offs < spriteram_size[0];offs += 8)
		{
			if ((spriteram.read(offs + 0) & 4)!=0)
			{
				int size,code,color,flipx,flipy,sx,sy;
	
	
				size = (spriteram.read(offs + 2) & 3);	/* 1 = single 2 = double */
				code = (spriteram.read(offs + 1) >> 2) + ((spriteram.read(offs + 0) & 0xf8) << 3);
				if (size == 2) code >>= 2;
				color = spriteram.read(offs + 3) & 0x0f;
				flipx = spriteram.read(offs + 0) & 1;
				flipy = spriteram.read(offs + 0) & 2;
				sx = spriteram.read(offs + 5) - ((spriteram.read(offs + 3) & 0x10) << 4);
				sy = spriteram.read(offs + 4) - ((spriteram.read(offs + 3) & 0x20) << 3);
	
				drawgfx(bitmap,Machine.gfx[size],
						code,
						color,
						flipx,flipy,
						sx,sy,
						Machine.drv.visible_area,TRANSPARENCY_PEN,0);
			}
		}
	
	
		/* draw the frontmost playfield. They are characters, but draw them as sprites */
		for (offs = silkworm_videoram2_size[0] - 1;offs >= 0;offs--)
		{
			if (silkworm_videoram2.read(offs)!=0)	/* don't draw spaces */
			{
				int sx,sy;
	
	
				sx = offs % 32;
				sy = offs / 32;
	
				drawgfx(bitmap,Machine.gfx[0],
						silkworm_videoram2.read(offs) + ((silkworm_colorram2.read(offs) & 0x03) << 8),
						silkworm_colorram2.read(offs) >> 4,
						0,0,
						8*sx,8*sy,
						Machine.drv.visible_area,TRANSPARENCY_PEN,0);
			}
		}
	} };
}
