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
import static arcadeflex.osdepend.*;
import static arcadeflex.libc.*;
import static mame.common.*;
import static mame.commonH.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;

public class popeye
{



	static final int VIDEO_RAM_SIZE = 0x1000;
	
	public static CharPtr popeye_videoram = new CharPtr();
        public static int[] popeye_videoram_size= new int[1];
        static osd_bitmap tmpbitmap2;
        static char dirtybuffer2[];
	public static CharPtr popeye_background_pos = new CharPtr();
	public static CharPtr popeye_palette_bank = new CharPtr();
	
	
	/***************************************************************************
	
	  Convert the color PROMs into a more useable format.
	
	  Popeye has four color PROMS:
	  - 32x8 char palette
	  - 32x8 background palette
	  - two 256x4 sprite palette
	  I don't know for sure how the PROMs are connected to the RGB output, but
	  it's probably the usual:
	
	  bit 7 -- 220 ohm resistor  -- BLUE
	        -- 470 ohm resistor  -- BLUE
	        -- 220 ohm resistor  -- GREEN
	        -- 470 ohm resistor  -- GREEN
	        -- 1  kohm resistor  -- GREEN
	        -- 220 ohm resistor  -- RED
	        -- 470 ohm resistor  -- RED
	  bit 0 -- 1  kohm resistor  -- RED
	
	***************************************************************************/
	public static VhConvertColorPromPtr popeye_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
		int i;


	
		for (i = 0; i < 256; i++)
		{
			int bits;
	
	
			bits = (i >> 0) & 0x07;
			palette[3*i] = (char) ((bits >> 1) | (bits << 2) | (bits << 5));
			bits = (i >> 3) & 0x07;
			palette[3*i + 1] = (char) ((bits >> 1) | (bits << 2) | (bits << 5));
			bits = (i >> 6) & 0x03;
			palette[3*i + 2] = (char) (bits | (bits >> 2) | (bits << 4) | (bits << 6));
		}
	
		for (i = 0; i < 32; i++)	/* characters */
		{
			colortable[2*i] = 0;
			colortable[2*i + 1] = (char) color_prom[i];
		}
		for (i = 0; i < 32; i++)	/* background */
		{
			colortable[i+32*2] = (char) color_prom[i+32];
		}
		for (i = 0; i < 64*4; i++)	/* sprites */
		{
			colortable[i+32*2+32] = (char) ((color_prom[i+64] & 0x0f) | ((color_prom[i+64+64*4] << 4) & 0xf0));
		}
	} };
	
/***************************************************************************

  Start the video hardware emulation.

***************************************************************************/
public static VhStartPtr popeye_vh_start= new VhStartPtr() { public int handler()
{
	if (generic_vh_start.handler() != 0)
			return 1;

	if ((dirtybuffer2 = new char[popeye_videoram_size[0]]) == null)
	{
		generic_vh_stop.handler();
		return 1;
	}
	memset(dirtybuffer2,1,popeye_videoram_size[0]);

	if ((tmpbitmap2 = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
	{
			dirtybuffer2 = null;
			generic_vh_stop.handler();
			return 1;
	}

	return 0;
} };


/***************************************************************************

  Stop the video hardware emulation.

***************************************************************************/
public static VhStopPtr popeye_vh_stop = new VhStopPtr() { public void handler()
{
		osd_free_bitmap(tmpbitmap2);
		tmpbitmap2 = null;
		dirtybuffer2 = null;
		generic_vh_stop.handler();
} };


	
	public static WriteHandlerPtr popeye_videoram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
		if ((data & 0x80) != 0)	/* write to the upper nibble */
		{
			if ((popeye_videoram.read(offset) & 0xf0) != ((data << 4)&0xf0))
			{
				dirtybuffer2[offset] = 1;
	
				popeye_videoram.write(offset, (popeye_videoram.read(offset) & 0x0f) | ((data << 4)&0xf0));
			}
		}
		else	/* write to the lower nibble */
		{
			if ((popeye_videoram.read(offset) & 0x0f) != (data & 0x0f))
			{
				dirtybuffer2[offset] = 1;
	
				popeye_videoram.write(offset, (popeye_videoram.read(offset) & 0xf0) | (data & 0x0f));
			}
		}
	} };
	
	
	
	public static WriteHandlerPtr popeye_palettebank_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
		if ((data & 0x08) != (popeye_palette_bank.read() & 0x08))
		{
		   memset(dirtybuffer,1,videoram_size[0]);
		   memset(dirtybuffer2,1,popeye_videoram_size[0]);
		}
	
		popeye_palette_bank.write(data);
	} };
	
	
	
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VhUpdatePtr popeye_vh_screenrefresh = new VhUpdatePtr() {	public void handler(osd_bitmap bitmap)
	{

            	int offs;


	/* for every character in the Video RAM, check if it has been modified */
	/* since last time and update it accordingly. */
	for (offs = popeye_videoram_size[0] - 1 - 128;offs >= 128;offs--)
	{
		if (dirtybuffer2[offs] != 0)
		{
			int sx,sy,y,colour;


			dirtybuffer2[offs] = 0;

			sx = 8 * (offs % 64);
			sy = 8 * (offs / 64) - 16;

			if (sx >= Machine.drv.visible_area.min_x &&
					sx+7 <= Machine.drv.visible_area.max_x &&
					sy >= Machine.drv.visible_area.min_y &&
					sy+7 <= Machine.drv.visible_area.max_y)
			{
				colour = Machine.gfx[3].colortable.read((popeye_videoram.read(offs) & 0x0f) + 2*(popeye_palette_bank.read() & 0x08));
				for (y = 0;y < 4;y++)
                                {
					memset(tmpbitmap2.line[sy+y],sx,colour,8);
                                }

				colour = Machine.gfx[3].colortable.read((popeye_videoram.read(offs) >> 4) + 2*(popeye_palette_bank.read() & 0x08));
				for (y = 4;y < 8;y++)
                                {
					memset(tmpbitmap2.line[sy+y],sx,colour,8);
                               }
			}
		}
	}

	if (popeye_background_pos.read(0) == 0)	/* no background */
	{
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			if (dirtybuffer[offs]!=0)
			{
				int sx,sy;


				dirtybuffer[offs] = 0;

				sx = 16 * (offs % 32);
				sy = 16 * (offs / 32) - 16;

				drawgfx(tmpbitmap,Machine.gfx[0],
						videoram.read(offs),colorram.read(offs),
						0,0,sx,sy,
						Machine.drv.visible_area,TRANSPARENCY_NONE,0);
			}
		}


		/* copy the frontmost playfield (should be in front of sprites, but never mind) */
		copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	}
	else
	{
		/* copy the background graphics */
		copybitmap(bitmap,tmpbitmap2,0,0,
			400 - 2 * popeye_background_pos.read(0),
			2 * (256 - popeye_background_pos.read(1)),
			Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	}


	/* Draw the sprites. Note that it is important to draw them exactly in this */
	/* order, to have the correct priorities. */
	for (offs = 0;offs < spriteram_size[0];offs += 4)
	{
		/*
		 * offs+3:
		 * bit 7 ?
		 * bit 6 ?
		 * bit 5 ?
		 * bit 4 MSB of sprite code
		 * bit 3 vertical flip
		 * bit 2 sprite bank
		 * bit 1 \ color (with bit 2 as well)
		 * bit 0 /
		 */
		if (spriteram.read(offs) != 0)
			drawgfx(bitmap,Machine.gfx[(spriteram.read(offs + 3) & 0x04) != 0 ? 1 : 2],
					0xff - (spriteram.read(offs + 2) & 0x7f) - 8*(spriteram.read(offs + 3) & 0x10),
					(spriteram.read(offs + 3) & 0x07) + 8*(popeye_palette_bank.read() & 0x07),
					spriteram.read(offs + 2) & 0x80,spriteram.read(offs + 3) & 0x08,
					2*(spriteram.read(offs))-7,2*(256-spriteram.read(offs + 1)) - 16,
					Machine.drv.visible_area,TRANSPARENCY_PEN,0);
	}


	if (popeye_background_pos.read(offs) != 0)	/* background is present */
	{
		/* draw the frontmost playfield. They are characters, but draw them as sprites */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			int sx,sy;


			if (videoram.read(offs) != 0xff)	/* don't draw spaces */
			{
				sx = 16 * (offs % 32);
				sy = 16 * (offs / 32) - 16;

				drawgfx(bitmap,Machine.gfx[0],
						videoram.read(offs),colorram.read(offs),
						0,0,sx,sy,
						Machine.drv.visible_area,TRANSPARENCY_PEN,0);
			}
		}
	}





    
	} };
}
