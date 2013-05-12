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

public class vanguard
{
	public static CharPtr vanguard_videoram2 = new CharPtr();


	public static CharPtr vanguard_characterram = new CharPtr();
	static char dirtycharacter[] = new char[256];

	public static CharPtr vanguard_scrollx = new CharPtr();
        public static CharPtr vanguard_scrolly = new CharPtr();

        public static VhConvertColorPromPtr vanguard_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
        {
                int i;


                for (i = 0;i < 16*4;i++)
                {
                        int bit0,bit1,bit2;


                        bit0 = (color_prom[i] >> 0) & 0x01;
                        bit1 = (color_prom[i] >> 1) & 0x01;
                        bit2 = (color_prom[i] >> 2) & 0x01;
                        palette[3*i] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                        bit0 = (color_prom[i] >> 3) & 0x01;
                        bit1 = (color_prom[i] >> 4) & 0x01;
                        bit2 = (color_prom[i] >> 5) & 0x01;
                        palette[3*i + 1] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                        bit0 = 0;
                        bit1 = (color_prom[i] >> 6) & 0x01;
                        bit2 = (color_prom[i] >> 7) & 0x01;
                        palette[3*i + 2] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                }

                for (i = 0;i < 16*4;i++)
                {
                        if (i % 4 == 0) colortable[i] = 0;
                        else colortable[i] = (char)i;
                }
        }};
	public static WriteHandlerPtr vanguard_characterram_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
		if (vanguard_characterram.read(offset) != data)
		{
			dirtycharacter[(offset / 8) & 0xff] = 1;

			vanguard_characterram.write(offset, data);
		}
	} };




	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
public static VhUpdatePtr vanguard_vh_screenrefresh = new VhUpdatePtr()	{ public void handler(osd_bitmap bitmap)
{

            	int offs;


	/* for every character in the Video RAM, check if it has been modified */
	/* since last time and update it accordingly. */
	for (offs = videoram_size[0] - 1;offs >= 0;offs--)
	{
		if (dirtybuffer[offs]!=0)
		{
			int sx,sy;


			dirtybuffer[offs] = 0;

			sx = 31 - offs / 32;
			sy = offs % 32;

			drawgfx(tmpbitmap,Machine.gfx[1],
					videoram.read(offs),
					(colorram.read(offs) >> 3) & 0x07,
					0,0,
					8*sx,8*sy,
					null,TRANSPARENCY_NONE,0);
		}
	}


	/* copy the background graphics */
	{
		int scrollx,scrolly;


		scrollx = vanguard_scrollx.read() - 2;
		scrolly = -(vanguard_scrolly.read());
		copyscrollbitmap(bitmap,tmpbitmap,1,new int[] {scrollx},1,new int[] {scrolly},Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	}


	/* draw the frontmost playfield. They are characters, but draw them as sprites */
	for (offs = videoram_size[0] - 1;offs >= 0;offs--)
	{
		int charcode;
		int sx,sy;


		charcode = vanguard_videoram2.read(offs);

		/* decode modified characters */
		if (dirtycharacter[charcode] == 1)
		{
			decodechar(Machine.gfx[0],charcode,vanguard_characterram,Machine.drv.gfxdecodeinfo[0].gfxlayout);
			dirtycharacter[charcode] = 2;
		}

		if (charcode != 0x30)	/* don't draw spaces */
		{
			sx = (31 - offs / 32) - 2;
			sy = (offs % 32);

			drawgfx(bitmap,Machine.gfx[0],
					charcode,
					colorram.read(offs) & 0x07,
					0,0,8*sx,8*sy,
					Machine.drv.visible_area,TRANSPARENCY_PEN,0);
		}
	}

	for (offs = 0;offs < 256;offs++)
	{
		if (dirtycharacter[offs] == 2) dirtycharacter[offs] = 0;
	}


} };

}

