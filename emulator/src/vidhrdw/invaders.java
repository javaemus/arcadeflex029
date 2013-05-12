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

public class invaders
{

	static final int BLACK = 0;
	static final int RED = 1;
	static final int GREEN = 2;
	static final int YELLOW = 3;
	static final int WHITE = 4;
	static final int CYAN = 5;
        static final int PURPLE = 6;

	public static CharPtr invaders_videoram = new CharPtr();

        public static VhStartPtr invaders_vh_start = new VhStartPtr() { public int handler()
	{
	 if ((tmpbitmap = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
		return 1;

	   return 0;

	} };
        public static VhStopPtr invaders_vh_stop = new VhStopPtr() { public void handler()
	{
		osd_free_bitmap(tmpbitmap);
		tmpbitmap = null;
	} };


	public static WriteHandlerPtr invaders_videoram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	if (invaders_videoram.read(offset) != data)
	{
		int i,x,y;


		invaders_videoram.write(offset, data);

		x = offset / 32 + 16;
		y = 256-8 - 8 * (offset % 32);

		for (i = 0;i < 8;i++)
		{
			int col;


			col = Machine.pens[WHITE];
			if (y >= 184 && y < 240) col = Machine.pens[GREEN];
			if (y >= 240 && x > 32 && x < 150) col = Machine.pens[GREEN];
			if (y >= 32 && y < 64) col = Machine.pens[RED];

			if ((data & 0x80)!=0) tmpbitmap.line[y][x] = (char)col;
			else tmpbitmap.line[y][x] = Machine.pens[BLACK];

			y++;
			data <<= 1;
		}
	}
	} };	
public static WriteHandlerPtr invrvnge_videoram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
{
	if (invaders_videoram.read(offset) != data)
	{
		int i,x,y;


		invaders_videoram.write(offset, data);

		x = offset / 32 + 16;
		y = 256-8 - 8 * (offset % 32);

		for (i = 0;i < 8;i++)
		{
			int col;


			col = Machine.pens[WHITE];
			if (y >= 184) col = Machine.pens[GREEN];
			if (y > 32 && y < 64) col = Machine.pens[RED];

			if ((data & 0x80)!=0) tmpbitmap.line[y][x] = (char)col;
			else tmpbitmap.line[y][x] = Machine.pens[BLACK];

			y++;
			data <<= 1;
		}
	}
} };



public static WriteHandlerPtr lrescue_videoram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
{
	if (invaders_videoram.read(offset) != data)
	{
		int i,x,y;


		invaders_videoram.write(offset, data);

		x = offset / 32 + 16;
		y = 256-8 - 8 * (offset % 32);

		for (i = 0;i < 8;i++)
		{
			int col;


			col = Machine.pens[WHITE];

			if (y >= 8 && y < 16) {
				if (x < 88) col = Machine.pens[CYAN];
				if (x >= 88 && x < 176) col = Machine.pens[RED];
				if (x >= 176) col = Machine.pens[YELLOW];
				}


			if (y >= 16 && y < 24) {
				if (x < 88) col = Machine.pens[CYAN];
				if (x >= 88 && x < 176) col = Machine.pens[GREEN];
				if (x >= 176) col = Machine.pens[YELLOW];
				}


			if (y >= 24 && y < 32) {
				if (x >= 88 && x < 176) col = Machine.pens[GREEN];	/* or 168? */
				if (x >= 176) col = Machine.pens[YELLOW];
				}

			if (y >= 32 && y < 40) col = Machine.pens[RED];
			if (y >= 40 && y < 64) col = Machine.pens[PURPLE];
			if (y >= 64 && y < 96) col = Machine.pens[GREEN];
			if (y >= 96 && y < 128) col = Machine.pens[CYAN];
			if (y >= 128 && y < 160) col = Machine.pens[PURPLE];
			if (y >= 160 && y < 192) col = Machine.pens[YELLOW];
			if (y >= 192 && y < 216) col = Machine.pens[RED];
			if (y >= 216 && y < 232) col = Machine.pens[CYAN];
			if (y >= 232 && y < 240) col = Machine.pens[RED];
			if (y >= 240) {
				if (x < 152) col = Machine.pens[CYAN];
				if (x >= 152 && x < 200) col = Machine.pens[PURPLE];
				if (x >= 200) col = Machine.pens[CYAN];
				}
			if (x == 239) col = Machine.pens[BLACK];

			if ((data & 0x80)!=0) tmpbitmap.line[y][x] = (char)col;
			else tmpbitmap.line[y][x] = Machine.pens[BLACK];

			y++;
			data <<= 1;
		}
	}
} };

		
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VhUpdatePtr invaders_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		/* copy the character mapped graphics */
		copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
	} };
}
