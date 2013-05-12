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
 *
 * ported to v0.28
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
import static m6809.M6809H.*;
public class gng {


  static final int GFX_CHAR = 0;
  static final int GFX_TILE = 1;
  static final int GFX_SPRITE = 2;
  public static CharPtr gng_paletteram = new CharPtr();

  public static CharPtr gng_bgvideoram = new CharPtr();
  public static CharPtr gng_bgcolorram = new CharPtr();
  public static CharPtr gng_scrollx = new CharPtr();
  public static CharPtr gng_scrolly = new CharPtr();
  public static int[] gng_bgvideoram_size=new int[1];
  static char[] dirtybuffer2;
  static char[] spritebuffer1;
  static char[] spritebuffer2;
  static osd_bitmap tmpbitmap2;

/***************************************************************************

  Convert the color PROMs into a more useable format.

  Ghosts 'n Goblins doesn't have color PROMs, it uses RAM instead.

  I don't know the exact values of the resistors between the RAM and the
  RGB output. I assumed these values (the same as Commando)
  bit 7 -- 220 ohm resistor  -- RED
        -- 470 ohm resistor  -- RED
        -- 1  kohm resistor  -- RED
        -- 2.2kohm resistor  -- RED
        -- 220 ohm resistor  -- GREEN
        -- 470 ohm resistor  -- GREEN
        -- 1  kohm resistor  -- GREEN
  bit 0 -- 2.2kohm resistor  -- GREEN

  bit 7 -- 220 ohm resistor  -- BLUE
        -- 470 ohm resistor  -- BLUE
        -- 1  kohm resistor  -- BLUE
        -- 2.2kohm resistor  -- BLUE
        -- unused
        -- unused
        -- unused
  bit 0 -- unused

***************************************************************************/

public static VhConvertColorPromPtr gng_vh_convert_color_prom= new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
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
	for (i = 0;i < Machine.drv.color_table_len;i++)
		colortable_table[i] = (char)i;
} };



/***************************************************************************

  Start the video hardware emulation.

***************************************************************************/
public static VhStartPtr gng_vh_start = new VhStartPtr() { public int handler()
{
	if (generic_vh_start.handler() != 0)
			return 1;

	if ((dirtybuffer2 = new char[gng_bgvideoram_size[0]]) == null)
	{
		generic_vh_stop.handler();
			return 1;
	}
	memset(dirtybuffer2,1,gng_bgvideoram_size[0]);

	if ((spritebuffer1 = new char[spriteram_size[0]]) == null)
	{
		dirtybuffer2 = null;
		generic_vh_stop.handler();
		return 1;
	}

	if ((spritebuffer2 = new char[spriteram_size[0]]) == null)
	{
		spritebuffer1= null;
		dirtybuffer2= null;
		generic_vh_stop.handler();
		return 1;
	}

	/* the background area is twice as tall and twice as large as the screen */
	if ((tmpbitmap2 = osd_create_bitmap(2*Machine.drv.screen_width,2*Machine.drv.screen_height)) == null)
	{
		spritebuffer2= null;
		spritebuffer1= null;
		dirtybuffer2= null;
		generic_vh_stop.handler();
		return 1;
	}

	return 0;
} };



/***************************************************************************

  Stop the video hardware emulation.

***************************************************************************/
public static VhStopPtr gng_vh_stop= new VhStopPtr() { public void handler()
{
	osd_free_bitmap(tmpbitmap2);
	spritebuffer2= null;
	spritebuffer1= null;
	dirtybuffer2= null;
	generic_vh_stop.handler();
} };



public static WriteHandlerPtr gng_bgvideoram_w= new WriteHandlerPtr() { public void handler(int offset,int data)
{
	if (gng_bgvideoram.read(offset) != data)
	{
		dirtybuffer2[offset] = 1;

		gng_bgvideoram.write(offset, data);
	}
} };



public static WriteHandlerPtr gng_bgcolorram_w= new WriteHandlerPtr() { public void handler(int offset,int data)
{
	if (gng_bgcolorram.read(offset) != data)
	{
		dirtybuffer2[offset] = 1;

		gng_bgcolorram.write(offset, data);
	}
} };



public static WriteHandlerPtr gng_paletteram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
{
	int bit0,bit1,bit2,bit3;
	int r,g,b,val;

	gng_paletteram.write(offset,data);

	val = gng_paletteram.read(offset & ~0x100);
	bit0 = (val >> 4) & 0x01;
	bit1 = (val >> 5) & 0x01;
	bit2 = (val >> 6) & 0x01;
	bit3 = (val >> 7) & 0x01;
	r = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;

	bit0 = (val >> 0) & 0x01;
	bit1 = (val >> 1) & 0x01;
	bit2 = (val >> 2) & 0x01;
	bit3 = (val >> 3) & 0x01;
	g = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;

	val = gng_paletteram.read(offset | 0x100);
	bit0 = (val >> 4) & 0x01;
	bit1 = (val >> 5) & 0x01;
	bit2 = (val >> 6) & 0x01;
	bit3 = (val >> 7) & 0x01;
	b = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;

	osd_modify_pen(Machine.pens[(offset & ~0x100)],r,g,b);
} };



public static InterruptPtr gng_interrupt = new InterruptPtr() { public int handler()
{
	/* we must store previous sprite data in a buffer and draw that instead of */
	/* the latest one, otherwise sprites will not be synchronized with */
	/* background scrolling */
	memcpy(spritebuffer2,spritebuffer1,spriteram_size[0]);
	memcpy(spritebuffer1,spriteram,spriteram_size[0]);

	return INT_IRQ;
} };



/***************************************************************************

  Draw the game screen in the given osd_bitmap.
  Do NOT call osd_update_display() from this function, it will be called by
  the main emulation engine.

***************************************************************************/
public static VhUpdatePtr gng_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
{
	int offs;



	for (offs = gng_bgvideoram_size[0] - 1;offs >= 0;offs--)
	{
		int sx,sy;


		if ((dirtybuffer2[offs] !=0) )
		{
			dirtybuffer2[offs] = 0;

			sx = offs / 32;
			sy = offs % 32;

			drawgfx(tmpbitmap2,Machine.gfx[GFX_TILE],
					gng_bgvideoram.read(offs) + 256*((gng_bgcolorram.read(offs) >> 6) & 0x03),
					gng_bgcolorram.read(offs) & 0x07,
					gng_bgcolorram.read(offs) & 0x10,gng_bgcolorram.read(offs) & 0x20,
					16 * sx,16 * sy,
					null,TRANSPARENCY_NONE,0);
		}
	}


	/* copy the background graphics */
	{
		int scrollx,scrolly;


		scrollx = -(gng_scrollx.read(0) + 256 * gng_scrollx.read(1));
		scrolly = -(gng_scrolly.read(0) + 256 * gng_scrolly.read(1));

		copyscrollbitmap(bitmap,tmpbitmap2,1,new int[] {scrollx },1,new int[] {scrolly},Machine.drv.visible_area,TRANSPARENCY_NONE,0);

	}


	/* Draw the sprites. Note that it is important to draw them exactly in this */
	/* order, to have the correct priorities. */
	for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
	{
		int bank;


		/* the meaning of bit 1 of [offs+1] is unknown */

		bank = ((spritebuffer2[offs + 1] >> 6) & 3);

		if (bank < 3)
			drawgfx(bitmap,Machine.gfx[GFX_SPRITE],
					spritebuffer2[offs] + 256*bank,
					(spritebuffer2[offs + 1] >> 4) & 3,
					spritebuffer2[offs + 1] & 0x04,spritebuffer2[offs + 1] & 0x08,
					spritebuffer2[offs + 3] - 0x100 * (spritebuffer2[offs + 1] & 0x01),spritebuffer2[offs + 2],
					Machine.drv.visible_area,TRANSPARENCY_PEN,15);
	}


	/* redraw the background tiles which have priority over sprites */
	{
		int scrollx,scrolly;


		scrollx = -(gng_scrollx.read(0) + 256 * gng_scrollx.read(1));
		scrolly = -(gng_scrolly.read(0) + 256 * gng_scrolly.read(1));

		for (offs = gng_bgvideoram_size[0] - 1;offs >= 0;offs--)
		{
			int sx,sy;


			if ((gng_bgcolorram.read(offs)& 0x08)!=0)
			{
				sx = ((16 * (offs / 32) + scrollx + 16) & 0x1ff) - 16;
				sy = ((16 * (offs % 32) + scrolly + 16) & 0x1ff) - 16;

				drawgfx(bitmap,Machine.gfx[GFX_TILE],
						gng_bgvideoram.read(offs) + 256*((gng_bgcolorram.read(offs) >> 6) & 0x03),
						gng_bgcolorram.read(offs) & 0x07,
						gng_bgcolorram.read(offs) & 0x10,gng_bgcolorram.read(offs) & 0x20,
						sx,sy,
						Machine.drv.visible_area,TRANSPARENCY_PEN,0);
			}
		}
	}


	/* draw the frontmost playfield. They are characters, but draw them as sprites */
	for (offs = videoram_size[0] - 1;offs >= 0;offs--)
	{
		int charcode;


		charcode = videoram.read(offs) + 4 * (colorram.read(offs) & 0x40);

		if (charcode != 0x20)	/* don't draw spaces */
		{
			int sx,sy;


			sx = 8 * (offs % 32);
			sy = 8 * (offs / 32);

			drawgfx(bitmap,Machine.gfx[GFX_CHAR],
					charcode,
					colorram.read(offs) & 0x0f,
					0,0,
					sx,sy,
					Machine.drv.visible_area,TRANSPARENCY_PEN,3);
		}
	}
}
 };
}

