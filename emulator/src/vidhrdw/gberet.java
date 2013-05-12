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

public class gberet
{
	public static CharPtr gberet_scroll = new CharPtr();
	public static CharPtr gberet_spritebank = new CharPtr();



	/***************************************************************************
	
	  Convert the color PROMs into a more useable format.
	
	  Green Beret has a 32 bytes palette PROM and two 256 bytes color lookup table
	  PROMs (one for sprites, one for characters).
	  I don't know for sure how the palette PROM is connected to the RGB output,
	  but it's probably the usual:
	
	  bit 7 -- 220 ohm resistor  -- BLUE
	        -- 470 ohm resistor  -- BLUE
	        -- 220 ohm resistor  -- GREEN
	        -- 470 ohm resistor  -- GREEN
	        -- 1  kohm resistor  -- GREEN
	        -- 220 ohm resistor  -- RED
	        -- 470 ohm resistor  -- RED
	  bit 0 -- 1  kohm resistor  -- RED
	
	***************************************************************************/
	public static VhConvertColorPromPtr gberet_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
		int i;
		
		
		for (i = 0; i < 32; i++)
		{
			int bit0, bit1, bit2;
		
		
			bit0 = (color_prom[i] >> 0) & 0x01;
			bit1 = (color_prom[i] >> 1) & 0x01;
			bit2 = (color_prom[i] >> 2) & 0x01;
			palette[3*i] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
			bit0 = (color_prom[i] >> 3) & 0x01;
			bit1 = (color_prom[i] >> 4) & 0x01;
			bit2 = (color_prom[i] >> 5) & 0x01;
			palette[3*i + 1] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
			bit0 = 0;
			bit1 = (color_prom[i] >> 6) & 0x01;
			bit2 = (color_prom[i] >> 7) & 0x01;
			palette[3*i + 2] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
		}
		
		for (i = 0;i < 16*16;i++)
			colortable[i] = (char) color_prom[i + 32];
		for (i = 16*16;i < 2*16*16;i++)
		{
			if (color_prom[i + 32] != 0) colortable[i] = (char) (color_prom[i + 32] + 0x10);
			else colortable[i] = 0;
		}
	} };
	


	/***************************************************************************

	  Start the video hardware emulation.

	***************************************************************************/
	public static VhStartPtr gberet_vh_start = new VhStartPtr() { public int handler()
	{


		if ((dirtybuffer = new char[videoram_size[0]]) == null)
			return 1;
		memset(dirtybuffer, 1, videoram_size[0]);
                /* Green Beret has a virtual screen twice as large as the visible screen */
		if ((tmpbitmap = osd_create_bitmap(2 * Machine.drv.screen_width, Machine.drv.screen_height)) == null)
		{
			dirtybuffer = null;
			return 1;
		}

		return 0;
	} };



	/***************************************************************************

	  Stop the video hardware emulation.

	***************************************************************************/
	public static VhStopPtr gberet_vh_stop = new VhStopPtr() { public void handler()
	{
		dirtybuffer = null;
		osd_free_bitmap(tmpbitmap);
		tmpbitmap = null;
	} };



	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
	public static VhUpdatePtr gberet_vh_screenrefresh = new VhUpdatePtr() {	public void handler(osd_bitmap bitmap)
	{
		int offs, i;
		
		
		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			if (dirtybuffer[offs] != 0)
			{
				int sx, sy;
		
		
				dirtybuffer[offs] = 0;
		
				sx = 8 * (offs % 64);
				sy = 8 * (offs / 64) - 8;
		
				drawgfx(tmpbitmap, Machine.gfx[0],
						videoram.read(offs) + 4 * (colorram.read(offs) & 0x40),
						colorram.read(offs) & 0x0f,
						colorram.read(offs) & 0x10, colorram.read(offs) & 0x20,
						sx, sy,
						null, TRANSPARENCY_NONE, 0);
			}
		}
		
		
		/* copy the temporary bitmap to the screen */
		{
			int scroll[] = new int[32];


			for (i = 1; i < 31; i++)
				scroll[i] = -(gberet_scroll.read(i+1) + 256 * gberet_scroll.read(i+1 + 32));

			copyscrollbitmap(bitmap, tmpbitmap, 32, scroll, 0, null, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
		}
		
		
		/* Draw the sprites.  */
		{
			CharPtr sr;


			if ((gberet_spritebank.read() & 0x08) != 0)
				sr = spriteram;
			else sr = spriteram_2;

			for (offs = 0;offs < spriteram_size[0];offs += 4)
			{
				if (sr.read(offs+3) != 0)
				{
					drawgfx(bitmap, Machine.gfx[(sr.read(offs+1) & 0x40) != 0 ? 2 : 1],
							sr.read(offs), sr.read(offs+1) & 0x0f,
							sr.read(offs+1) & 0x10, sr.read(offs+1) & 0x20,
							sr.read(offs+2) - 2*(sr.read(offs+1) & 0x80), sr.read(offs+3)-8,
							Machine.drv.visible_area, TRANSPARENCY_COLOR, 0);
				}
			}
		}
	} };
}
