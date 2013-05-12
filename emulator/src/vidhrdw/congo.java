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

public class congo
{
	public static CharPtr congo_background_position = new CharPtr();
	public static CharPtr congo_background_enable = new CharPtr();
	static osd_bitmap backgroundbitmap;



	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	public static VhStartPtr congo_vh_start = new VhStartPtr() { public int handler()
	{
		int offs;
		int sx, sy;
		osd_bitmap prebitmap;


		if (generic_vh_start.handler() != 0)
			return 1;

		/* large bitmap for the precalculated background */
		if ((backgroundbitmap = osd_create_bitmap(512, 2303+32)) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}


		/* create a temporary bitmap to prepare the background before converting it */
		if ((prebitmap = osd_create_bitmap(4096, 256)) == null)
		{
			osd_free_bitmap(backgroundbitmap);
			backgroundbitmap = null;
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
				if (offs + sx < 0 || offs + sx >= 4096)
				{
					backgroundbitmap.line[sy][sx] = Machine.pens[0];
					backgroundbitmap.line[sy][sx+1] = Machine.pens[0];
				}
				else
				{
					backgroundbitmap.line[sy][sx] = prebitmap.line[sx/2][offs + sx];
					backgroundbitmap.line[sy][sx+1] = prebitmap.line[sx/2][offs + sx+1];
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
	public static VhStopPtr congo_vh_stop = new VhStopPtr() { public void handler()
	{
		osd_free_bitmap(backgroundbitmap);
		backgroundbitmap = null;
		generic_vh_stop.handler();
	} };



	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
    static int sprpri[] = new int[0x100]; /* this really should not be more
                                  * than 0x1e, but I did not want to check
                                  * for 0xff which is set when sprite is off
                                  * -V-
                                  */
	public static VhUpdatePtr congo_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int offs, i;


		/* copy the background */
		if (congo_background_enable.read() != 0)
		{

			int skew, scroll;
			rectangle clip = new rectangle();


			clip.min_x = Machine.drv.visible_area.min_x;
			clip.max_x = Machine.drv.visible_area.max_x;

			scroll = 1023+63 - (congo_background_position.read(0) + 256*congo_background_position.read(1));

			skew = 128;

			for (i = 0;i < 256-2*8;i++)
			{
				clip.min_y = i;
				clip.max_y = i;
				copybitmap(bitmap, backgroundbitmap, 0, 0, skew, -scroll, clip, TRANSPARENCY_NONE, 0);

				skew -= 2;
			}
		}
		else clearbitmap(bitmap);


		/* Draw the sprites. Note that it is important to draw them exactly in this */
		/* order, to have the correct priorities. */
		    /* Sprites actually start at 0xff * [0xc031], it seems to be static tho'*/
        	/* The number of active sprites is stored at 0xc032 */

		for (offs = 0x1e * 0x20 ;offs >= 0x00 ;offs -= 0x20)
			sprpri[ spriteram.read(offs+1) ] = offs;

		for (i=0x1e ; i>=0; i--)
		{
			offs = sprpri[i];

			if (spriteram.read(offs+2) != 0xff)
			{
				drawgfx(bitmap, Machine.gfx[1],
						spriteram.read(offs+2+1)& 0x7f, spriteram.read(offs+2+2),
						spriteram.read(offs+2+1) & 0x80, spriteram.read(offs+2+2) & 0x80,
						spriteram.read(offs+2) - 16, ((spriteram.read(offs+2+3) + 16) & 0xff) - 31,
						Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
			}
		}


		/* draw the frontmost playfield. They are characters, but draw them as sprites */
		for (offs = 0;offs < videoram_size[0];offs++)
		{
			int sx,sy;


			sx = 8 * (31 - offs / 32);
			sy = 8 * (offs % 32);

			if (videoram.read(offs) != 0x60)      /* don't draw spaces */
				drawgfx(bitmap, Machine.gfx[0],
						videoram.read(offs),
						colorram.read(offs),
						0, 0, sx, sy,
						Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
		}
	} };
}
