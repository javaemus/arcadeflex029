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
 */

package vidhrdw;

import static arcadeflex.libc.*;
import static mame.common.*;
import static mame.commonH.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;

public class jumpbug {
  static class star
  {
    public int x;
    public int y;
    public int code;
    public int col;
  }
  static final int MAX_STARS = 250;
  static final int STARS_COLOR_BASE = 32;
  public static CharPtr jumpbug_attributesram = new CharPtr();
  public static CharPtr jumpbug_gfxbank = new CharPtr();
  public static CharPtr jumpbug_stars = new CharPtr();

  static star[] stars = new star[MAX_STARS];
  static int total_stars;



  public static VhConvertColorPromPtr jumpbug_vh_convert_color_prom = new VhConvertColorPromPtr()
  {
    public void handler(char[] palette, char[] colortable, char[] color_prom)
    {
	int i;


	for (i = 0;i < 32;i++)
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


	for (i = 0;i < 4 * 8;i++)
	{
		if ((i & 3)!=0) colortable[i] = (char)i;
		else colortable[i] = 0;
	}


	/* now the stars */
	for (i = 32;i < 32 + 64;i++)
	{
		int bits;
		int[] map = { 0x00, 0x88, 0xcc, 0xff };

		bits = ((i-32) >> 0) & 0x03;
		palette[3*i] = (char)map[bits];
		bits = ((i-32) >> 2) & 0x03;
		palette[3*i + 1] = (char)map[bits];
		bits = ((i-32) >> 4) & 0x03;
		palette[3*i + 2] = (char)map[bits];
	}
    }
  };

  public static VhStartPtr jumpbug_vh_start = new VhStartPtr()
  {
    public int handler()
    {
        int generator;
	int x,y;


	if (generic_vh_start.handler() != 0)
		return 1;


	/* precalculate the star background */
	total_stars = 0;
	generator = 0;

	for (x = 255;x >= 0;x--)
	{
		for (y = 511;y >= 0;y--)
		{
			int bit1,bit2;

                       //TODO (shadow) : check for precision issues...
			generator <<= 1;
			bit1 = (~generator >> 17) & 1;
			bit2 = (generator >> 5) & 1;

			if ((bit1 ^ bit2)!=0) generator |= 1;

			if (x >= Machine.drv.visible_area.min_x &&
					x <= Machine.drv.visible_area.max_x &&
					(((~generator >> 16) & 1)!=0) &&
					(generator & 0xff) == 0xff)
			{
				int color;

				color = (~(generator >> 8)) & 0x3f;
				if ((color!=0) && total_stars < MAX_STARS)
				{
                                        stars[total_stars] = new star();
					stars[total_stars].x = x;
					stars[total_stars].y = y;
					stars[total_stars].code = color;
					stars[total_stars].col = Machine.pens[color + STARS_COLOR_BASE];

					total_stars++;
				}
			}
		}
	}

	return 0;
    }
  };

  public static WriteHandlerPtr jumpbug_attributes_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
      if (((offset & 0x1) != 0) && (jumpbug_attributesram.read(offset) != data))
      {
        for (int i = offset / 2; i < generic.videoram_size[0]; i += 32) {
          dirtybuffer[i] = 1;
        }
      }
      jumpbug_attributesram.write(offset, data);
    }
  };

  public static WriteHandlerPtr jumpbug_gfxbank_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
      if (jumpbug_gfxbank.read(offset) != data)
      {
        jumpbug_gfxbank.write(offset, data);
        memset(dirtybuffer, 1, videoram_size[0]);
      }
    }


  };
  static int stars_blink;
  static int blink_count;
  public static VhUpdatePtr jumpbug_vh_screenrefresh = new VhUpdatePtr()
  {
    public void handler(osd_bitmap bitmap)
    {
        int offs;


	/* for every character in the Video RAM, check if it has been modified */
	/* since last time and update it accordingly. */
	for (offs = videoram_size[0] - 1;offs >= 0;offs--)
	{
		if (dirtybuffer[offs]!=0)
		{
			int sx,sy,charcode;


			dirtybuffer[offs] = 0;

			sx = (31 - offs / 32);
			sy = (offs % 32);

			charcode = videoram.read(offs);
			if (((charcode & 0xc0) == 0x80) && ((jumpbug_gfxbank.read(2) & 1) != 0))
			{
				charcode += 128 + 64 * (jumpbug_gfxbank.read(0) & 1)
						+ 128 * (jumpbug_gfxbank.read(1) & 1) + 256 * (~jumpbug_gfxbank.read(4) & 1);
			}

			drawgfx(tmpbitmap,Machine.gfx[0],
					charcode,
					jumpbug_attributesram.read(2 * sy + 1),
					0,0,8*sx,8*sy,
					null,TRANSPARENCY_NONE,0);
		}
	}


	/* copy the temporary bitmap to the screen */
	{
		int[] scroll=new int[32];


		for (offs = 0;offs < 32;offs++)
			scroll[offs] = jumpbug_attributesram.read(2 * offs);

		copyscrollbitmap(bitmap,tmpbitmap,32,scroll,0,null,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	}


	/* Draw the sprites */
	for (offs = 0;offs < spriteram_size[0];offs += 4)
	{
		int spritecode;


		spritecode = spriteram.read(offs + 1) & 0x3f;
		if ((spritecode & 0x30) == 0x20 && (jumpbug_gfxbank.read(2) & 1) != 0)
		{
			spritecode += 32 + 16 * (jumpbug_gfxbank.read(0) & 1)
					+ 32 * (jumpbug_gfxbank.read(1) & 1) + 64 * (~jumpbug_gfxbank.read(4) & 1);
		}

		drawgfx(bitmap,Machine.gfx[1],
				spritecode,
				spriteram.read(offs + 2),
				spriteram.read(offs + 1) & 0x80,spriteram.read(offs + 1) & 0x40,
				spriteram.read(offs),spriteram.read(offs + 3),
				Machine.drv.visible_area,TRANSPARENCY_PEN,0);
	}


	/* draw the stars */
	if ((jumpbug_stars.read() & 0x1) != 0)
	{
		int bpen;
		
		blink_count++;
		if (blink_count >= 43)
		{
			blink_count = 0;
			stars_blink = (stars_blink + 1) % 4;
		}

		bpen = Machine.pens[0];
		for (offs = 0;offs < total_stars;offs++)
		{
			int x,y;


			x = stars[offs].x;
			y = stars[offs].y / 2;

			if ((((x & 1) ^ ((y >> 4) & 1))!=0) &&
					(bitmap.line[y][x] == bpen))
			{
				switch (stars_blink)
				{
					case 0:
						if ((stars[offs].code & 1)!=0) bitmap.line[y][x] = (char)stars[offs].col;
						break;
					case 1:
						if ((stars[offs].code & 4)!=0) bitmap.line[y][x] = (char)stars[offs].col;
						break;
					case 2:
						if ((x & 2)!=0) bitmap.line[y][x] = (char)stars[offs].col;
						break;
					case 3:
						bitmap.line[y][x] = (char)stars[offs].col;
						break;
				}
			}
		}
	}
    }
  };


}
