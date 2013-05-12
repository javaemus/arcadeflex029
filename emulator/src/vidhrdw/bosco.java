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

public class bosco {
        
        public static CharPtr bosco_videoram2= new CharPtr();
        public static CharPtr bosco_colorram2= new CharPtr();
        public static CharPtr bosco_radarx= new CharPtr();
        public static CharPtr bosco_radary= new CharPtr();
        public static CharPtr bosco_radarattr= new CharPtr();
        public static CharPtr bosco_scrollx= new CharPtr();
        public static CharPtr bosco_scrolly= new CharPtr();
                                                                                                      /* to speed up video refresh */
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

        public static VhConvertColorPromPtr bosco_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
                int i;


                for (i = 0;i < 32;i++)
                {
                        int bit0,bit1,bit2;


                        bit0 = (color_prom[31-i] >> 0) & 0x01;
                        bit1 = (color_prom[31-i] >> 1) & 0x01;
                        bit2 = (color_prom[31-i] >> 2) & 0x01;
                        palette[3*i] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                        bit0 = (color_prom[31-i] >> 3) & 0x01;
                        bit1 = (color_prom[31-i] >> 4) & 0x01;
                        bit2 = (color_prom[31-i] >> 5) & 0x01;
                        palette[3*i + 1] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                        bit0 = 0;
                        bit1 = (color_prom[31-i] >> 6) & 0x01;
                        bit2 = (color_prom[31-i] >> 7) & 0x01;
                        palette[3*i + 2] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                }

                /* characters */
                for (i = 0;i < 32*4;i++)
                        colortable[i] = (char)(15 - (color_prom[i + 32] & 0x0f));
                /* sprites / radar */
                for (i = 32*4;i < 64*4;i++)
                        colortable[i] = (char)((15 - (color_prom[i + 32] & 0x0f)) + 0x10);


                /* now the stars */
                for (i = 32;i < 32 + 64;i++)
                {
                        int bits;
                        int map[] = { 0x00, 0x88, 0xcc, 0xff };

                        bits = ((i-32) >> 0) & 0x03;
                        palette[3*i] = (char)(map[bits]);
                        bits = ((i-32) >> 2) & 0x03;
                        palette[3*i + 1] = (char)(map[bits]);
                        bits = ((i-32) >> 4) & 0x03;
                        palette[3*i + 2] = (char)(map[bits]);
                }
        }};
	public static VhStartPtr bosco_vh_start = new VhStartPtr() { public int handler()
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
        public static VhStopPtr bosco_vh_stop = new VhStopPtr() { public void handler()
	{
		osd_free_bitmap(tmpbitmap1);
                dirtybuffer2 = null;
		tmpbitmap1 = null;
		generic_vh_stop.handler();
	} };

	public static WriteHandlerPtr bosco_videoram2_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (bosco_videoram2.read(offset) != data)
		{
			dirtybuffer2[offset] = 1;

			bosco_videoram2.write(offset, data);
		}
	} };

	public static WriteHandlerPtr bosco_colorram2_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (bosco_colorram2.read(offset) != data)
		{
			dirtybuffer2[offset] = 1;

			bosco_colorram2.write(offset, data);
		}
	} };


        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr bosco_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                int offs,sx,sy;


                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0]-1; offs >= 0;offs--)
                {
                        if (dirtybuffer2[offs]!=0)
                        {
                                dirtybuffer2[offs] = 0;

                                sx = offs % 32;
                                sy = offs / 32;

                                drawgfx(tmpbitmap1, Machine.gfx[0],
						bosco_videoram2.read(offs),
						bosco_colorram2.read(offs) & 0x1f,
						(bosco_colorram2.read(offs) & 0x40) == 0 ? 1 : 0, bosco_colorram2.read(offs) & 0x80,
						8*sx, 8*sy,
						null, TRANSPARENCY_NONE, 0);
                        }
                }

                /* update radar */
                for (sy = 0;sy < 32;sy++)
                {
                        for (sx = 0;sx < 8;sx++)
                        {
                                offs = sy * 32 + sx;

                                if (dirtybuffer[offs]!=0)
                                {
                                        dirtybuffer[offs] = 0;

                                        drawgfx(tmpbitmap, Machine.gfx[1],
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
                        int scrollx,scrolly;

                        scrollx = -(bosco_scrollx.read() - 3);
			scrolly = -(bosco_scrolly.read() + 16);
                        copyscrollbitmap(bitmap, tmpbitmap1, 1, new int[] { scrollx }, 1, new int[] { scrolly }, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
       
                }


                /* radar */
                copybitmap(bitmap,tmpbitmap,0,0,0,0,radarvisiblearea,TRANSPARENCY_NONE,0);


                /* draw the sprites */
                for (offs = 0;offs < spriteram_size[0];offs += 2)
                {
                        drawgfx(bitmap, Machine.gfx[2],
					spriteram.read(offs) >> 2, spriteram_2.read(offs + 1),
					spriteram.read(offs) & 1, spriteram.read(offs) & 2,
					spriteram.read(offs + 1) - 1, 224 - spriteram_2.read(offs),
					visiblearea, TRANSPARENCY_THROUGH, 0);
                }

                /* draw the dots on the radar and the bullets */
                for (offs = 0; offs < 12; offs++)
                {
                        int x,y;
                        int color;
                        int	attr;


                        attr = bosco_radarattr.read(offs);

                        x = bosco_radarx.read(offs) + 256 * (1 - (bosco_radarattr.read(offs) & 1));
                        y = 238 - bosco_radary.read(offs);

                        if ((attr & 8)!=0)	/* Long bullets */
                        {
                                color = Machine.pens[1];
                                switch ((attr & 6) >> 1)
                                {
                                        case 0:		/* Diagonal Left to Right */
                                                if (x >= Machine.drv.visible_area.min_x &&
                                                        x < (Machine.drv.visible_area.max_x - 3) &&
                                                        y > (Machine.drv.visible_area.min_y + 3) &&
                                                        y < Machine.drv.visible_area.max_y)
                                                {
                                                        bitmap.line[y][x+3] = (char)(color);
                                                        bitmap.line[y-1][x+3] = (char)(color);
                                                        bitmap.line[y-1][x+2] = (char)(color);
                                                        bitmap.line[y-2][x+2] = (char)(color);
                                                        bitmap.line[y-2][x+1] = (char)(color);
                                                        bitmap.line[y-3][x+1] = (char)(color);
                                                }
                                                break;
                                        case 1:		/* Diagonal Right to Left */
                                                if (x >= Machine.drv.visible_area.min_x &&
                                                        x < (Machine.drv.visible_area.max_x - 3) &&
                                                        y > (Machine.drv.visible_area.min_y + 3) &&
                                                        y < Machine.drv.visible_area.max_y)
                                                {
                                                        bitmap.line[y][x+1] = (char)(color);
                                                        bitmap.line[y-1][x+1] = (char)(color);
                                                        bitmap.line[y-1][x+2] = (char)(color);
                                                        bitmap.line[y-2][x+2] = (char)(color);
                                                        bitmap.line[y-2][x+3] = (char)(color);
                                                        bitmap.line[y-3][x+3] = (char)(color);
                                                }
                                                break;
                                        case 2:		/* Up and Down */
                                                if (x >= Machine.drv.visible_area.min_x &&
                                                        x < Machine.drv.visible_area.max_x &&
                                                        y > (Machine.drv.visible_area.min_y + 3) &&
                                                        y < Machine.drv.visible_area.max_y)
                                                {
                                                        bitmap.line[y][x] = (char)(color);
                                                        bitmap.line[y][x+1] = (char)(color);
                                                        bitmap.line[y-1][x] = (char)(color);
                                                        bitmap.line[y-1][x+1] = (char)(color);
                                                        bitmap.line[y-2][x] = (char)(color);
                                                        bitmap.line[y-2][x+1] = (char)(color);
                                                        bitmap.line[y-3][x] = (char)(color);
                                                        bitmap.line[y-3][x+1] = (char)(color);
                                                }
                                                break;
                                        case 3:		/* Left and Right */
                                                if (x >= Machine.drv.visible_area.min_x &&
                                                        x < (Machine.drv.visible_area.max_x - 2) &&
                                                        y > Machine.drv.visible_area.min_y &&
                                                        y < Machine.drv.visible_area.max_y)
                                                {
                                                        bitmap.line[y-1][x] = (char)(color);
                                                        bitmap.line[y-1][x+1] = (char)(color);
                                                        bitmap.line[y][x] = (char)(color);
                                                        bitmap.line[y][x+1] = (char)(color);
                                                        bitmap.line[y-1][x+2] = (char)(color);
                                                        bitmap.line[y-1][x+3] = (char)(color);
                                                        bitmap.line[y][x+2] = (char)(color);
                                                        bitmap.line[y][x+3] = (char)(color);
                                                }
                                                break;
                                }
                        }
                        else
                        {
                                color = Machine.pens[(bosco_radarattr.read(offs) >> 1) & 3];

                                /* normal size dots */
                                if (x >= Machine.drv.visible_area.min_x &&
                                        x < Machine.drv.visible_area.max_x &&
                                        y > Machine.drv.visible_area.min_y &&
                                        y <= Machine.drv.visible_area.max_y)
                                {
                                        bitmap.line[y-1][x] = (char)(color);
                                        bitmap.line[y-1][x+1] = (char)(color);
                                        bitmap.line[y][x] = (char)(color);
                                        bitmap.line[y][x+1] = (char)(color);
                                }
                        }
                }
        }};    
}
