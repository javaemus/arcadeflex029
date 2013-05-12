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

public class zaxxon
{





	public static CharPtr zaxxon_background_position = new CharPtr();
	public static CharPtr zaxxon_background_color = new CharPtr();
	public static CharPtr zaxxon_background_enable = new CharPtr();
	static osd_bitmap backgroundbitmap1, backgroundbitmap2;



	/***************************************************************************

	  Start the video hardware emulation.

	***************************************************************************/
	public static VhStartPtr zaxxon_vh_start = new VhStartPtr() { public int handler()
	{
		int offs;
		int sx, sy;
		osd_bitmap prebitmap;


		if (generic_vh_start.handler() != 0)
			return 1;

		/* large bitmap for the precalculated background */
		if ((backgroundbitmap1 = osd_create_bitmap(512, 2303+32)) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}

		if ((backgroundbitmap2 = osd_create_bitmap(512, 2303+32)) == null)
		{
			osd_free_bitmap(backgroundbitmap1);
			backgroundbitmap1 = null;
			generic_vh_stop.handler();
			return 1;
		}

		/* create a temporary bitmap to prepare the background before converting it */
		if ((prebitmap = osd_create_bitmap(4096, 256)) == null)
		{
			osd_free_bitmap(backgroundbitmap2);
			backgroundbitmap2 = null;
			osd_free_bitmap(backgroundbitmap1);
			backgroundbitmap1 = null;
			generic_vh_stop.handler();
			return 1;
		}

		/* prepare the background */
		for (offs = 0;offs < 0x4000;offs++)
		{
			sx = 8 * (511 - offs / 32);
			sy = 8 * (offs % 32);

			drawgfx(prebitmap, Machine.gfx[2],
					Machine.memory_region[2][offs] + 256 * (Machine.memory_region[2][0x4000 + offs] & 3),
					Machine.memory_region[2][0x4000 + offs] >> 4,
					0, 0,
					sx, sy,
					null, TRANSPARENCY_NONE, 0);
		}

		/* the background is stored as a rectangle, but is drawn by the hardware skewed: */
		/* go right two pixels, then up one pixel. Doing the conversion at run time would */
		/* be extremely expensive, so we do it now. To save memory, we squash the image */
		/* horizontally (doing line shifts at run time is much less expensive than doing */
		/* column shifts) */
		for (offs = -510;offs < 4096;offs += 2)
		{
			sy = (2302-510/2) - offs/2;

			for (sx = 0;sx < 512;sx += 2)
			{
				if (offs + sx >= 0 && offs + sx < 4096)
				{
					backgroundbitmap1.line[sy][sx] = prebitmap.line[sx/2][offs + sx];
					backgroundbitmap1.line[sy][sx+1] = prebitmap.line[sx/2][offs + sx+1];
				}
			}
		}


		/* prepare a second background with different colors, used in the death sequence */
		for (offs = 0;offs < 0x4000;offs++)
		{
			sx = 8 * (511 - offs / 32);
			sy = 8 * (offs % 32);

			drawgfx(prebitmap, Machine.gfx[2],
					Machine.memory_region[2][offs] + 256 * (Machine.memory_region[2][0x4000 + offs] & 3),
					Machine.memory_region[2][0x4000 + offs] >> 4,
					0, 0,
					sx, sy,
					null, TRANSPARENCY_NONE, 0);
		}

		/* the background is stored as a rectangle, but is drawn by the hardware skewed: */
		/* go right two pixels, then up one pixel. Doing the conversion at run time would */
		/* be extremely expensive, so we do it now. To save memory, we squash the image */
		/* horizontally (doing line shifts at run time is much less expensive than doing */
		/* column shifts) */
		for (offs = -510;offs < 4096;offs += 2)
		{
			sy = (2302-510/2) - offs/2;

			for (sx = 0;sx < 512;sx += 2)
			{
				if (offs + sx >= 0 && offs + sx < 4096)
				{
					backgroundbitmap2.line[sy][sx] = prebitmap.line[sx/2][offs + sx];
					backgroundbitmap2.line[sy][sx+1] = prebitmap.line[sx/2][offs + sx+1];
				}
			}
		}

		osd_free_bitmap(prebitmap);
		prebitmap = null;


		/* free the graphics ROMs, they are no longer needed */
		Machine.memory_region[2] = null;


		return 0;
	} };



	/***************************************************************************

	  Stop the video hardware emulation.

	***************************************************************************/
	public static VhStopPtr zaxxon_vh_stop = new VhStopPtr() { public void handler()
	{
		osd_free_bitmap(backgroundbitmap2);
		backgroundbitmap2 = null;
		osd_free_bitmap(backgroundbitmap1);
		backgroundbitmap1 = null;
		generic_vh_stop.handler();
	} };



	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
	public static VhUpdatePtr zaxxon_vh_screenrefresh = new VhUpdatePtr() {	public void handler(osd_bitmap bitmap)
	{
		int offs;

		/* copy the background */
		if (zaxxon_background_enable.read() != 0)
		{
			int i, skew, scroll;
			rectangle clip = new rectangle();


			clip.min_x = Machine.drv.visible_area.min_x;
			clip.max_x = Machine.drv.visible_area.max_x;

			scroll = 2048+63 - (zaxxon_background_position.read(0) + 256*zaxxon_background_position.read(1));

			skew = 128;

			for (i = 0;i < 256-4*8;i++)
			{
				clip.min_y = i;
				clip.max_y = i;

				if (zaxxon_background_color.read() != 0)
					copybitmap(bitmap, backgroundbitmap2, 0, 0, skew, -scroll, clip, TRANSPARENCY_NONE, 0);
				else copybitmap(bitmap, backgroundbitmap1, 0, 0, skew, -scroll, clip, TRANSPARENCY_NONE, 0);

				skew -= 2;
			}
		}
		else clearbitmap(bitmap);


		/* Draw the sprites. Note that it is important to draw them exactly in this */
		/* order, to have the correct priorities. */
		for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
		{
			if (spriteram.read(offs) != 0xff)
			{
				drawgfx(bitmap, Machine.gfx[1],
						spriteram.read(offs+1), spriteram.read(offs+2),
						spriteram.read(offs+1) & 0x80,spriteram.read(offs+1) & 0x40,
						spriteram.read(offs) - 15, ((spriteram.read(offs+3) + 16) & 0xff) - 32,
						Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
			}
		}



		/* draw the frontmost playfield. They are characters, but draw them as sprites */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			int sx,sy;


			sx = 8 * (31 - offs / 32);
			sy = 8 * (offs % 32);

			if (videoram.read(offs) != 0x60)	/* don't draw spaces */
				drawgfx(bitmap, Machine.gfx[0],
						videoram.read(offs),
						videoram.read(offs) >> 4,
						0, 0, sx, sy,
						Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
		}
	} };
}