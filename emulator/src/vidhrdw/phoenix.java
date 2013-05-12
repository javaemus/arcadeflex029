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
import static arcadeflex.osdepend.*;

public class phoenix
{


	public static CharPtr phoenix_videoram2 = new CharPtr();
	static char []dirtybuffer2;
	static osd_bitmap tmpbitmap2;

	static int scrollreg;
	static int palette_bank;



	static rectangle backvisiblearea = new rectangle
	(
		3*8, 29*8-1,
		3*8, 31*8-1
	);

	static rectangle backtmparea = new rectangle
	(
		3*8, 29*8-1,
		0*8, 32*8-1
	);



	/***************************************************************************
	
	  Stop the video hardware emulation.
	
	***************************************************************************/
	public static VhStartPtr phoenix_vh_start = new VhStartPtr() { public int handler()
	{
		scrollreg = 0;
		palette_bank = 0;


		if (generic_vh_start.handler() != 0)
			return 1;
                if ((dirtybuffer2 = new char[videoram_size[0]]) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}
                memset(dirtybuffer2,1,videoram_size[0]);
		/* small temp bitmap for the score display */
		if ((tmpbitmap2 = osd_create_bitmap(Machine.drv.screen_width, 3*8)) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}

		return 0;
	} };




	/***************************************************************************

	  Stop the video hardware emulation.

	***************************************************************************/
	public static VhStopPtr phoenix_vh_stop = new VhStopPtr() { public void handler()
	{
		osd_free_bitmap(tmpbitmap2);
		tmpbitmap2 = null;
                dirtybuffer2=null;
		generic_vh_stop.handler();
	} };



	public static WriteHandlerPtr phoenix_videoram2_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
		if (phoenix_videoram2.read(offset) != data)
		{
			dirtybuffer2[offset] = 1;

			phoenix_videoram2.write(offset, data);
		}
	} };



	public static WriteHandlerPtr phoenix_scrollreg_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		scrollreg = data;
	} };



	public static WriteHandlerPtr phoenix_videoreg_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                if (palette_bank != ((data >> 1) & 1))
                {
                        palette_bank = (data >> 1) & 1;

                        memset(dirtybuffer,1,videoram_size[0]);
                        memset(dirtybuffer2,1,videoram_size[0]);
                }
	} };



	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
	public static VhUpdatePtr phoenix_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
            	int offs;


                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        /* background */
                        if (dirtybuffer[offs]!=0)
                        {
                                int sx,sy;


                                dirtybuffer[offs] = 0;

                                sx = 8 * (31 - offs / 32) - 3 * 8;
                                sy = 8 * (offs % 32);

                               drawgfx(tmpbitmap, Machine.gfx[1],
						videoram.read(offs),
						(videoram.read(offs) >> 5) + 8 * palette_bank,
						0, 0, sx, sy,
						backtmparea, TRANSPARENCY_NONE, 0);
                                                                
                        }
                }

                /* score */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if (dirtybuffer2[offs]!=0)
                        {
                                int sx,sy;


                                dirtybuffer2[offs] = 0;

                                sx = 8 * (31 - offs / 32) - 3 * 8;
                                sy = 8 * (offs % 32);

                               drawgfx(tmpbitmap2, Machine.gfx[0],
						phoenix_videoram2.read(offs),
						phoenix_videoram2.read(offs) >> 5,
						0, 0, sx, sy,
						null, TRANSPARENCY_NONE, 0);

                        }
                }


                /* copy the character mapped graphics */
                {
                        int scroll;


                        scroll = -scrollreg;

                        copyscrollbitmap(bitmap, tmpbitmap, 0, null, 1, new int[] { scroll }, backvisiblearea, TRANSPARENCY_NONE, 0);
                }
                copybitmap(bitmap, tmpbitmap2, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);


                /* draw the frontmost playfield. They are characters, but draw them as sprites */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        int sx,sy;


                        sx = 8 * (31 - offs / 32) - 3 * 8;
                        sy = 8 * (offs % 32);

                        if (sy >= 3 * 8 && phoenix_videoram2.read(offs) != 0)	/* don't draw score and spaces */
                                drawgfx(bitmap, Machine.gfx[0],
						phoenix_videoram2.read(offs),
						(phoenix_videoram2.read(offs) >> 5) + 8 * palette_bank,
						0, 0, sx, sy,
						Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
                }
	} };
}