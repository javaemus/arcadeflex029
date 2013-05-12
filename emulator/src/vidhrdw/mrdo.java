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
import static arcadeflex.osdepend.*;
import static vidhrdw.generic.*;

public class mrdo
{

	public static CharPtr mrdo_videoram2 = new CharPtr();
	public static CharPtr mrdo_colorram2 = new CharPtr();
	public static CharPtr mrdo_scroll_x = new CharPtr();
	static osd_bitmap tmpbitmap1, tmpbitmap2;



	/***************************************************************************

	  Convert the color PROMs into a more useable format.

	  Mr. Do! has two 32 bytes palette PROM and a 32 bytes sprite color lookup
	  table PROM.
	  The palette PROMs are connected to the RGB output this way:

	  U2:
	  bit 7 -- unused
			-- unused
			-- 100 ohm resistor  -- BLUE
			--  75 ohm resistor  -- BLUE
			-- 100 ohm resistor  -- GREEN
			--  75 ohm resistor  -- GREEN
			-- 100 ohm resistor  -- RED
	  bit 0 --  75 ohm resistor  -- RED

	  T2:
	  bit 7 -- unused
			-- unused
			-- 150 ohm resistor  -- BLUE
			-- 120 ohm resistor  -- BLUE
			-- 150 ohm resistor  -- GREEN
			-- 120 ohm resistor  -- GREEN
			-- 150 ohm resistor  -- RED
	  bit 0 -- 120 ohm resistor  -- RED

	***************************************************************************/
	public static VhConvertColorPromPtr mrdo_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
		int i, j;


		for (i = 0; i < 64; i++)
		{
			for (j = 0; j < 4; j++)
			{
				int bit0, bit1, bit2, bit3;


				bit0 = (color_prom[4 * (i / 8) + j + 32] >> 1) & 0x01;
				bit1 = (color_prom[4 * (i / 8) + j + 32] >> 0) & 0x01;
				bit2 = (color_prom[4 * (i % 8) + j] >> 1) & 0x01;
				bit3 = (color_prom[4 * (i % 8) + j] >> 0) & 0x01;
				palette[3*(4*i + j)] = (char) (0x2c * bit0 + 0x37 * bit1 + 0x43 * bit2 + 0x59 * bit3);
				bit0 = (color_prom[4 * (i / 8) + j + 32] >> 3) & 0x01;
				bit1 = (color_prom[4 * (i / 8) + j + 32] >> 2) & 0x01;
				bit2 = (color_prom[4 * (i % 8) + j] >> 3) & 0x01;
				bit3 = (color_prom[4 * (i % 8) + j] >> 2) & 0x01;
				palette[3*(4*i + j) + 1] = (char) (0x2c * bit0 + 0x37 * bit1 + 0x43 * bit2 + 0x59 * bit3);
				bit0 = (color_prom[4 * (i / 8) + j + 32] >> 5) & 0x01;
				bit1 = (color_prom[4 * (i / 8) + j + 32] >> 4) & 0x01;
				bit2 = (color_prom[4 * (i % 8) + j] >> 5) & 0x01;
				bit3 = (color_prom[4 * (i % 8) + j] >> 4) & 0x01;
				palette[3*(4*i + j) + 2] = (char) (0x2c * bit0 + 0x37 * bit1 + 0x43 * bit2 + 0x59 * bit3);
			}
		}

		/* characters with pen 0 = black */
		for (i = 0; i < 4 * 64; i++)
		{
			if (i % 4 == 0) colortable[i] = 0;
			else colortable[i] = (char) i;
		}
		/* characters with colored pen 0 */
		colortable[0 + 4 * 64] = 3;	/* black, but avoid avoid transparency */
		for (i = 1; i < 4 * 64; i++)
			colortable[i + 4 * 64] = (char) i;

		/* sprites */
		for (i = 0; i < 4 * 8; i++)
		{
			int bits1, bits2;


			/* low 4 bits are for sprite n */
			bits1 = (color_prom[i + 64] >> 0) & 3;
			bits2 = (color_prom[i + 64] >> 2) & 3;
			colortable[i + 4 * 128] = (char) (bits1 + (bits2 << 2) + (bits2 << 5));

			/* high 4 bits are for sprite n + 8 */
			bits1 = (color_prom[i + 64] >> 4) & 3;
			bits2 = (color_prom[i + 64] >> 6) & 3;
			colortable[i + 4 * 136] = (char) (bits1 + (bits2 << 2) + (bits2 << 5));
		}
	} };



	/***************************************************************************

	  Start the video hardware emulation.

	***************************************************************************/
	public static VhStartPtr mrdo_vh_start = new VhStartPtr() {	public int handler()
	{
		if (generic_vh_start.handler() != 0)
			return 1;

		if ((tmpbitmap1 = osd_create_bitmap(Machine.drv.screen_width, Machine.drv.screen_height)) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}

		if ((tmpbitmap2 = osd_create_bitmap(Machine.drv.screen_width, Machine.drv.screen_height)) == null)
		{
			osd_free_bitmap(tmpbitmap1);
			tmpbitmap1 = null;
			generic_vh_stop.handler();
			return 1;
		}

		return 0;
	} };



	/***************************************************************************

	  Stop the video hardware emulation.

	***************************************************************************/
	public static VhStopPtr mrdo_vh_stop = new VhStopPtr() { public void handler()
	{
		osd_free_bitmap(tmpbitmap1);
		tmpbitmap1 = null;
		osd_free_bitmap(tmpbitmap2);
		tmpbitmap2 = null;
		generic_vh_stop.handler();
	} };



	public static WriteHandlerPtr mrdo_videoram2_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (mrdo_videoram2.read(offset) != data)
		{
			dirtybuffer[offset] = 1;

			mrdo_videoram2.write(offset, data);
		}
	} };



	public static WriteHandlerPtr mrdo_colorram2_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (mrdo_colorram2.read(offset) != data)
		{
			dirtybuffer[offset] = 1;

			mrdo_colorram2.write(offset, data);
		}
	} };



	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
	public static VhUpdatePtr mrdo_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
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

				sx = 8 * (offs / 32);
				sy = 8 * (31 - offs % 32);

				/* Mr. Do! has two playfields, one of which can be scrolled. However, */
				/* during gameplay this feature is not used, so I keep the composition */
				/* of the two playfields in a third temporary bitmap, to speed up rendering. */
				drawgfx(tmpbitmap1, Machine.gfx[1],
						videoram.read(offs) + 2 * (colorram.read(offs) & 0x80),
						colorram.read(offs) & 0x7f,
						0, 0, sx, sy,
						null, TRANSPARENCY_NONE, 0);
				drawgfx(tmpbitmap2, Machine.gfx[0],
						mrdo_videoram2.read(offs) + 2 * (mrdo_colorram2.read(offs) & 0x80),
						mrdo_colorram2.read(offs) & 0x7f,
						0, 0, sx, sy,
						null, TRANSPARENCY_NONE, 0);

				drawgfx(tmpbitmap, Machine.gfx[1],
						videoram.read(offs) + 2 * (colorram.read(offs) & 0x80),
						colorram.read(offs) & 0x7f,
						0, 0, sx, sy,
						Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
				drawgfx(tmpbitmap, Machine.gfx[0],
						mrdo_videoram2.read(offs) + 2 * (mrdo_colorram2.read(offs) & 0x80),
						mrdo_colorram2.read(offs) & 0x7f,
						0, 0, sx, sy,
						Machine.drv.visible_area, (mrdo_colorram2.read(offs) & 0x40) != 0 ? TRANSPARENCY_NONE : TRANSPARENCY_PEN,0);
			}
		}


		/* copy the character mapped graphics */
		if (mrdo_scroll_x.read() != 0)
		{
			int scroll;


			scroll = -mrdo_scroll_x.read();

			copyscrollbitmap(bitmap, tmpbitmap1, 1, new int[] { scroll }, 0, null, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);

			copybitmap(bitmap, tmpbitmap2, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_COLOR, 0);
		}
		else
			copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);


		/* Draw the sprites. Note that it is important to draw them exactly in this */
		/* order, to have the correct priorities. */
		for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
		{
			if (spriteram.read(offs + 1) != 0)
			{
				drawgfx(bitmap, Machine.gfx[2],
						spriteram.read(offs), spriteram.read(offs + 2) & 0x0f,
						spriteram.read(offs + 2) & 0x20, spriteram.read(offs + 2) & 0x10,
						256 - spriteram.read(offs + 1), 240 - spriteram.read(offs + 3),
						Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
			}
		}
	} };
}
