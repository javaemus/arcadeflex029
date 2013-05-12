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
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static arcadeflex.osdepend.*;
import static mame.common.*;
/**
 *
 * @author George
 */
public class commando
{

public static CharPtr commando_bgvideoram = new CharPtr();
public static CharPtr commando_bgcolorram = new CharPtr();
public static int[] commando_bgvideoram_size= new int[1];
public static CharPtr commando_scrollx = new CharPtr();
public static CharPtr commando_scrolly = new CharPtr();
static char [] dirtybuffer2;
static char [] spritebuffer1;
static char [] spritebuffer2;
static osd_bitmap tmpbitmap2;


/***************************************************************************

  Convert the color PROMs into a more useable format.

  Commando has three 256x4 palette PROMs (one per gun), connected to the
  RGB output this way:

  bit 3 -- 220 ohm resistor  -- RED/GREEN/BLUE
        -- 470 ohm resistor  -- RED/GREEN/BLUE
        -- 1  kohm resistor  -- RED/GREEN/BLUE
  bit 0 -- 2.2kohm resistor  -- RED/GREEN/BLUE

***************************************************************************/
public static VhConvertColorPromPtr commando_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
{
	int i;


	for (i = 0;i < 256;i++)
	{
		int bit0,bit1,bit2,bit3;


		bit0 = (color_prom[i] >> 0) & 0x01;
		bit1 = (color_prom[i] >> 1) & 0x01;
		bit2 = (color_prom[i] >> 2) & 0x01;
		bit3 = (color_prom[i] >> 3) & 0x01;
		palette[3*i] = (char)(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
		bit0 = (color_prom[i+256] >> 0) & 0x01;
		bit1 = (color_prom[i+256] >> 1) & 0x01;
		bit2 = (color_prom[i+256] >> 2) & 0x01;
		bit3 = (color_prom[i+256] >> 3) & 0x01;
		palette[3*i + 1] = (char)(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
		bit0 = (color_prom[i+256*2] >> 0) & 0x01;
		bit1 = (color_prom[i+256*2] >> 1) & 0x01;
		bit2 = (color_prom[i+256*2] >> 2) & 0x01;
		bit3 = (color_prom[i+256*2] >> 3) & 0x01;
		palette[3*i + 2] = (char)(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
	}

	/* characters use colors 192-255 */
	for (i = 0;i < 16*4;i++)
		colortable[i] = (char)(i + 192);

	/* sprites use colors 128-191 */
	for (i = 16*4;i < 16*4+4*16;i++)
		colortable[i] = (char)(i - 16*4 + 128);

	/* background tiles use colors 0-127 */
	for (i = 16*4+4*16;i < 16*4+4*16+16*8;i++)
		colortable[i] = (char)(i - (16*4+4*16));
}};



/***************************************************************************

  Start the video hardware emulation.

***************************************************************************/
public static VhStartPtr commando_vh_start = new VhStartPtr() { public int handler()
{
	if (generic_vh_start.handler() != 0)
			return 1;
       if ((dirtybuffer2 = new char[commando_bgvideoram_size[0]]) == null)
       {
               generic_vh_stop.handler();
                return 1;
        }

	memset(dirtybuffer2,1,commando_bgvideoram_size[0]);

	if ((spritebuffer1 = new char[spriteram_size[0]]) == null)
	{
		dirtybuffer=null;
		generic_vh_stop.handler();
		return 1;
	}

	if ((spritebuffer2 = new char[spriteram_size[0]]) == null)
	{
		spritebuffer1=null;
		dirtybuffer2=null;
		generic_vh_stop.handler();
		return 1;
	}

	if (generic_vh_start.handler() != 0)
			return 1;

	/* the background area is twice as tall and twice as large as the screen */
	if ((tmpbitmap2 = osd_create_bitmap(2*Machine.drv.screen_width,2*Machine.drv.screen_height)) == null)
	{
		spritebuffer2=null;
		spritebuffer1=null;
		dirtybuffer2=null;
		generic_vh_stop.handler();
		return 1;
	}

	return 0;
}};



/***************************************************************************

  Stop the video hardware emulation.

***************************************************************************/
public static VhStopPtr commando_vh_stop = new VhStopPtr() { public void handler()
{
	osd_free_bitmap(tmpbitmap2);
	spritebuffer2=null;
	spritebuffer1=null;
	dirtybuffer2=null;
	generic_vh_stop.handler();
}};


public static WriteHandlerPtr commando_bgvideoram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
{
	if (commando_bgvideoram.read(offset) != data)
	{
		dirtybuffer2[offset] = 1;

		commando_bgvideoram.write(offset, data);
	}
}};

public static WriteHandlerPtr commando_bgcolorram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
{

	if (commando_bgcolorram.read(offset) != data)
	{
		dirtybuffer2[offset] = 1;

		commando_bgcolorram.write(offset, data);
	}
}};


public static InterruptPtr commando_interrupt = new InterruptPtr() { public int handler()
{
	if (cpu_getiloops() == 1) return 0x00cf;	/* RST 08h */
	else
	{
		/* we must store previous sprite data in a buffer and draw that instead of */
		/* the latest one, otherwise sprites will not be synchronized with */
		/* background scrolling */
		memcpy(spritebuffer2,spritebuffer1,spriteram_size[0]);
		memcpy(spritebuffer1,spriteram,spriteram_size[0]);

		return 0x00d7;	/* RST 10h - VBLANK */
	}
}};



/***************************************************************************

  Draw the game screen in the given osd_bitmap.
  Do NOT call osd_update_display() from this function, it will be called by
  the main emulation engine.

***************************************************************************/
public static VhUpdatePtr commando_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
{
	int offs;


	for (offs = commando_bgvideoram_size[0] - 1;offs >= 0;offs--)
	{
		int sx,sy;


		if (dirtybuffer2[offs]!=0)
		{
			dirtybuffer2[offs] = 0;

			sx = offs % 32;
			sy = 31 - offs / 32;

			drawgfx(tmpbitmap2,Machine.gfx[1],
					commando_bgvideoram.read(offs) + 4*(commando_bgcolorram.read(offs) & 0xc0),
					commando_bgcolorram.read(offs) & 0x0f,
					commando_bgcolorram.read(offs) & 0x20,commando_bgcolorram.read(offs) & 0x10,
					16 * sx,16 * sy,
					null,TRANSPARENCY_NONE,0);
		}
	}


	/* copy the background graphics */
	{
		int scrollx,scrolly;


		scrollx = -(commando_scrollx.read(0) + 256 * commando_scrollx.read(1));
		scrolly = commando_scrolly.read(0) + 256 * commando_scrolly.read(1) - 256;

		copyscrollbitmap(bitmap,tmpbitmap2,1,new int[] {scrollx},1,new int[] {scrolly},Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	}


	/* Draw the sprites. Note that it is important to draw them exactly in this */
	/* order, to have the correct priorities. */
	for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
	{
		int bank;


		/* the meaning of bit 1 of [offs+1] is unknown */

		bank = ((spritebuffer2[offs + 1] >> 6) & 3);

		if (bank < 3)
			drawgfx(bitmap,Machine.gfx[2],
					spritebuffer2[offs] + 256* bank,
					(spritebuffer2[offs + 1] >> 4) & 3,
					spritebuffer2[offs + 1] & 0x08,spritebuffer2[offs + 1] & 0x04,
					spritebuffer2[offs + 2],240 - spritebuffer2[offs + 3] + 0x100 * (spritebuffer2[offs + 1] & 0x01),
					Machine.drv.visible_area,TRANSPARENCY_PEN,15);
	}


	/* draw the frontmost playfield. They are characters, but draw them as sprites */
	for (offs = videoram_size[0] - 1;offs >= 0;offs--)
	{
		int charcode;


		charcode = videoram.read(offs) + 4 * (colorram.read(offs) & 0xc0);

		if (charcode != 0x20)	/* don't draw spaces */
		{
			int sx,sy;


			sx = 8 * (offs / 32);
			sy = 8 * (31 - offs % 32);

			drawgfx(bitmap,Machine.gfx[0],
					charcode,
					colorram.read(offs) & 0x0f,
					colorram.read(offs) & 0x20,0,
					sx,sy,
					Machine.drv.visible_area,TRANSPARENCY_PEN,3);
		}
	}
}};
}

