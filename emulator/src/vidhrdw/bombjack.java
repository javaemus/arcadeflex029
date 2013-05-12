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

public class bombjack
{
	public static CharPtr bombjack_paletteram = new CharPtr();
	static int background_image;

	public static VhConvertColorPromPtr bombjack_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable, char []color_prom_table)
	{
		int i;
                CharPtr palette= new CharPtr(palette_table);
                /* the palette will be initialized by the game. We just set it to some */
                /* pre-cooked values so the startup copyright notice can be displayed. */
                for (i = 0;i < Machine.drv.total_colors;i++)
                {
                        palette.writeinc(((i & 1) >> 0) * 0xff);
                        palette.writeinc(((i & 2) >> 1) * 0xff);
                        palette.writeinc(((i & 4) >> 2) * 0xff);
                }

                /* initialize the color table */
                for (i = 0;i < Machine.drv.total_colors;i++)
                        colortable[i] = (char)i;
	
	} };



	public static WriteHandlerPtr bombjack_paletteram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                int bit0,bit1,bit2,bit3;
                int r,g,b,val;


                bombjack_paletteram.write(offset,data);

                /* red component */
                val = bombjack_paletteram.read(offset & ~1);
                bit0 = (val >> 0) & 0x01;
                bit1 = (val >> 1) & 0x01;
                bit2 = (val >> 2) & 0x01;
                bit3 = (val >> 3) & 0x01;
                r = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;

                /* green component */
                val = bombjack_paletteram.read(offset & ~1);
                bit0 = (val >> 4) & 0x01;
                bit1 = (val >> 5) & 0x01;
                bit2 = (val >> 6) & 0x01;
                bit3 = (val >> 7) & 0x01;
                g = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;

                /* blue component */
                val = bombjack_paletteram.read(offset | 1);
                bit0 = (val >> 0) & 0x01;
                bit1 = (val >> 1) & 0x01;
                bit2 = (val >> 2) & 0x01;
                bit3 = (val >> 3) & 0x01;
                b = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;

                osd_modify_pen(Machine.pens[offset / 2],r,g,b);
	} };



	public static WriteHandlerPtr bombjack_background_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
		if (background_image != data)
		{
			memset(dirtybuffer,1,videoram_size[0]);

			background_image = data;
		}
	} };



	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/
	public static VhUpdatePtr bombjack_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int i, offs,base;


		base = 0x200 * (background_image & 0x07);

		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			int sx, sy;
			int bx, by;
			int tilecode, tileattribute;


			sx = 8 * (31 - offs / 32);
			sy = 8 * (offs % 32);

			if ((background_image & 0x10) != 0)
			{
				int bgoffs;


				bx = sx & 0xf0;
				by = sy & 0xf0;
	
				bgoffs = base+16*(15-bx/16)+by/16;

				tilecode = Machine.memory_region[2][bgoffs];
				tileattribute = Machine.memory_region[2][bgoffs + 0x100];
			}
			else
			{
				tilecode = 0xff;
				bx = by = tileattribute = 0;	/* avoid compiler warning */
			}

			if (dirtybuffer[offs]!=0)
                        {
			/* draw the background (this can be handled better) */
                                if (tilecode != 0xff)
                                {
                                        rectangle clip=new rectangle();


                                        clip.min_x = sx;
                                        clip.max_x = sx+7;
                                        clip.min_y = sy;
                                        clip.max_y = sy+7;

                                        drawgfx(tmpbitmap,Machine.gfx[1],
                                                        tilecode,
                                                        tileattribute & 0x0f,
                                                        tileattribute & 0x80,0,
                                                        bx,by,
                                                        clip,TRANSPARENCY_NONE,0);

                                        drawgfx(tmpbitmap,Machine.gfx[0],
                                                                videoram.read(offs) + 16 * (colorram.read(offs) & 0x10),
                                                                colorram.read(offs) & 0x0f,
                                                                0,0,
                                                                sx,sy,
                                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                                }
                                else
                                        drawgfx(tmpbitmap,Machine.gfx[0],
                                                                videoram.read(offs) + 16 * (colorram.read(offs) & 0x10),
                                                                colorram.read(offs) & 0x0f,
                                                                0,0,
                                                                sx,sy,
                                                                Machine.drv.visible_area,TRANSPARENCY_NONE,0);


                                dirtybuffer[offs] = 0;
                            }
                }
	
		/* copy the character mapped graphics */
		copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	
	
		/* Draw the sprites. */
		for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
		{
	/*
		abbbbbbb cdefgggg hhhhhhhh iiiiiiii
	
		a        ? (set when big sprites are selected)
		bbbbbbb  sprite code
		c        x flip
		d        y flip (used only in death sequence?)
		e        use big sprites (32x32 instead of 16x16)
		f        ? (set only when the bonus (B) materializes?)
		gggg     color
		hhhhhhhh x position
		iiiiiiii y position
	*/
			drawgfx(bitmap,Machine.gfx[(spriteram.read(offs+1) & 0x20) != 0 ? 3 : 2],
					spriteram.read(offs) & 0x7f, spriteram.read(offs+1) & 0x0f,
					spriteram.read(offs+1) & 0x80, spriteram.read(offs+1) & 0x40,
					spriteram.read(offs+2)-1, spriteram.read(offs+3),
					Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
		}
	} };
}