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
import static arcadeflex.osdepend.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;

public class rallyx
{

        public static CharPtr rallyx_videoram2= new CharPtr();
        public static CharPtr rallyx_colorram2= new CharPtr();
        public static CharPtr rallyx_radarcarx= new CharPtr();
        public static CharPtr rallyx_radarcary= new CharPtr();
        public static CharPtr rallyx_radarcarcolor= new CharPtr();
        public static CharPtr rallyx_scrollx= new CharPtr();
        public static CharPtr rallyx_scrolly= new CharPtr();
        static char dirtybuffer2[];	/* keep track of modified portions of the screen */
											/* to speed up video refresh */

	static osd_bitmap tmpbitmap1;



	static rectangle visiblearea = new rectangle
	(
		0*8, 28*8-1,
		0*8, 28*8-1
	);

	static rectangle radarvisiblearea = new rectangle
	(
		28*8, 36*8-1,
		0*8, 28*8-1
	);



	public static VhStartPtr rallyx_vh_start = new VhStartPtr() { public int handler()
	{
		if (generic_vh_start.handler() != 0)
			return 1;

                if ((dirtybuffer2 = new char[videoram_size[0]]) == null)
			return 1;
		memset(dirtybuffer2, 1, videoram_size[0]);

		if ((tmpbitmap1 = osd_create_bitmap(32*8, 32*8)) == null)
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
	public static VhStopPtr rallyx_vh_stop = new VhStopPtr() { public void handler()
	{
		osd_free_bitmap(tmpbitmap1);
                dirtybuffer2 = null;
		tmpbitmap1 = null;
		generic_vh_stop.handler();
	} };







	public static WriteHandlerPtr rallyx_videoram2_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (rallyx_videoram2.read(offset) != data)
		{
			dirtybuffer2[offset] = 1;

			rallyx_videoram2.write(offset, data);
		}
	} };



	public static WriteHandlerPtr rallyx_colorram2_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (rallyx_colorram2.read(offset) != data)
		{
			dirtybuffer2[offset] = 1;

			rallyx_colorram2.write(offset, data);
		}
	} };



	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
	public static VhUpdatePtr rallyx_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int offs, sx, sy;


		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			if (dirtybuffer2[offs] != 0)
			{
				dirtybuffer2[offs] = 0;

				sx = offs % 32;
				sy = offs / 32;

				drawgfx(tmpbitmap1, Machine.gfx[0],
						rallyx_videoram2.read(offs),
						rallyx_colorram2.read(offs) & 0x1f,
						(rallyx_colorram2.read(offs) & 0x40) == 0 ? 1 : 0, rallyx_colorram2.read(offs) & 0x80,
						8*sx, 8*sy,
						null, TRANSPARENCY_NONE, 0);
			}
		}

		/* update radar */
		for (sy = 0; sy < 32; sy++)
		{
			for (sx = 0; sx < 8; sx++)
			{
				offs = sy * 32 + sx;

				if (dirtybuffer[offs] != 0)
				{
					dirtybuffer[offs] = 0;

					drawgfx(tmpbitmap, Machine.gfx[0],
							videoram.read(offs),
							colorram.read(offs) & 0x1f,
							(colorram.read(offs) & 0x40) == 0 ? 1 : 0, colorram.read(offs) & 0x80,
							8 * ((sx ^ 4) + 28), 8*(sy-2),
							radarvisiblearea, TRANSPARENCY_NONE, 0);
				}
			}
		}


		/* copy the temporary bitmap to the screen */
		{
			int scrollx, scrolly;


			scrollx = -(rallyx_scrollx.read() - 3);
			scrolly = -(rallyx_scrolly.read() + 16);

			copyscrollbitmap(bitmap, tmpbitmap1, 1, new int[] { scrollx }, 1, new int[] { scrolly }, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
		}


		/* radar */
		copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, radarvisiblearea, TRANSPARENCY_NONE, 0);


		/* draw the sprites */
		for (offs = 0; offs < 6*2; offs += 2)
		{
			drawgfx(bitmap, Machine.gfx[1],
					spriteram.read(offs) >> 2, spriteram_2.read(offs + 1),
					spriteram.read(offs) & 1, spriteram.read(offs) & 2,
					spriteram.read(offs + 1) - 1, 224 - spriteram_2.read(offs),
					visiblearea, TRANSPARENCY_PEN, 0);
		}
                	/* draw the cars on the radar */
                for (offs = 0; offs < 9; offs++)
                {
                    int x,y;
                    int color;


                    /* TODO: map to the correct color */
                    color = rallyx_radarcarcolor.read(offs) & 0xfe;

                    x = rallyx_radarcarx.read(offs) + 256 * (1 - (rallyx_radarcarcolor.read(offs) & 1));
                    y = 237 - rallyx_radarcary.read(offs);

                    drawgfx(bitmap,Machine.gfx[2],
                                    0,	/* this is just a square, generated by the hardware */
                                    color,
                                    0,0,
                                    x,y,
                                    Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	}
	} };
}