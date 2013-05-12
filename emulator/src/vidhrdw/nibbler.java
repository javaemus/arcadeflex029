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

public class nibbler
{
	public static CharPtr nibbler_videoram2 = new CharPtr();
	public static CharPtr nibbler_characterram = new CharPtr();
	static char dirtycharacter[] = new char[256];



	public static WriteHandlerPtr nibbler_videoram2_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
		if (nibbler_videoram2.read(offset) != data)
		{
			dirtybuffer[offset] = 1;
	
			nibbler_videoram2.write(offset, data);
		}
	} };	



	public static WriteHandlerPtr nibbler_characterram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (nibbler_characterram.read(offset) != data)
		{
			dirtycharacter[(offset / 8) & 0xff] = 1;
	
			nibbler_characterram.write(offset, data);
		}
	} };


	
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VhUpdatePtr nibbler_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int offs, i;

			
		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			int charcode;


			charcode = videoram.read(offs);

			if (dirtybuffer[offs] != 0 || dirtycharacter[charcode] != 0)
			{
				int sx, sy;


			/* decode modified characters */
				if (dirtycharacter[charcode] == 1)
				{
					decodechar(Machine.gfx[0],charcode,nibbler_characterram,Machine.drv.gfxdecodeinfo[0].gfxlayout);
					dirtycharacter[charcode] = 2;
				}


				dirtybuffer[offs] = 0;

				sx = (31 - offs / 32) - 2;
				sy = (offs % 32);

				drawgfx(tmpbitmap, Machine.gfx[1],
						nibbler_videoram2.read(offs), colorram.read(offs) >> 3,
						0, 0, 8*sx, 8*sy,
						Machine.drv.visible_area, TRANSPARENCY_NONE, 0);

				drawgfx(tmpbitmap, Machine.gfx[0],
						charcode, colorram.read(offs) & 0x07,
						0, 0, 8*sx, 8*sy,
						Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
			}
		}


		for (i = 0; i < 256; i++)
		{
			if (dirtycharacter[i] == 2) dirtycharacter[i] = 0;
		}


		/* copy the temporary bitmap to the screen */
		copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
	} };
};
