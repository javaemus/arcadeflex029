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

public class galaga {
      static class star
      {
        public int x;
        public int y;
        public int col;
      }
      static final int MAX_STARS = 250;
      static final int STARS_COLOR_BASE = 32;
      public static CharPtr galaga_starcontrol = new CharPtr();
      static int stars_scroll;
      static star[] stars = new star[MAX_STARS];
      static int total_stars;
      static int flipscreen;




        /***************************************************************************

          Convert the color PROMs into a more useable format.

          Galaga has one 32x8 palette PROM and two 256x4 color lookup table PROMs
          (one for characters, one for sprites). Only the first 128 bytes of the
          lookup tables seem to be used.
          The palette PROM is connected to the RGB output this way:

          bit 7 -- 220 ohm resistor  -- BLUE
                -- 470 ohm resistor  -- BLUE
                -- 220 ohm resistor  -- GREEN
                -- 470 ohm resistor  -- GREEN
                -- 1  kohm resistor  -- GREEN
                -- 220 ohm resistor  -- RED
                -- 470 ohm resistor  -- RED
          bit 0 -- 1  kohm resistor  -- RED

        ***************************************************************************/
      	public static VhConvertColorPromPtr galaga_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
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
                /* sprites */
                for (i = 32*4;i < 64*4;i++)
                {
                        if (i % 4 == 0) colortable[i] = 0;	/* preserve transparency */
                        else colortable[i] = (char)((15 - (color_prom[i + 32] & 0x0f)) + 0x10);
                }

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



        /***************************************************************************

          Start the video hardware emulation.

        ***************************************************************************/
        public static VhStartPtr galaga_vh_start = new VhStartPtr() { public int handler()
	{
                int generator;
                int x,y;


                if (generic_vh_start.handler() != 0)
                        return 1;


                /* precalculate the star background */
                /* this comes from the Galaxian hardware, Galaga is probably different */
                total_stars = 0;
                generator = 0;

                for (x = 255;x >= 0;x--)
                {
                        for (y = 511;y >= 0;y--)
                        {
                                int bit1,bit2;


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
                                                stars[total_stars].col = Machine.pens[color + STARS_COLOR_BASE];

                                                total_stars++;
                                        }
                                }
                        }
                }

                return 0;
        }};

        public static WriteHandlerPtr galaga_flipscreen_w = new WriteHandlerPtr() { public void handler(int offset, int data)
        {
                if (flipscreen != (data & 1))
                {
                        flipscreen = data & 1;
                        memset(dirtybuffer,1,videoram_size[0]);
                }
        }};

        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr galaga_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                int offs;


                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if (dirtybuffer[offs]!=0)
                        {
                                int sx,sy,mx,my;


                                dirtybuffer[offs] = 0;

                        /* Even if Galaga's screen is 28x36, the memory layout is 32x32. We therefore */
                        /* have to convert the memory coordinates into screen coordinates. */
                        /* Note that 32*32 = 1024, while 28*36 = 1008: therefore 16 bytes of Video RAM */
                        /* don't map to a screen position. We don't check that here, however: range */
                        /* checking is performed by drawgfx(). */

                                mx = offs / 32;
                                my = offs % 32;

                                if (mx <= 1)
                                {
                                        sx = 29 - my;
                                        sy = mx + 34;
                                }
                                else if (mx >= 30)
                                {
                                        sx = 29 - my;
                                        sy = mx - 30;
                                }
                                else
                                {
                                        sx = 29 - mx;
                                        sy = my + 2;
                                }
                                if (flipscreen!=0)
                                {
                                        sx = 27 - sx;
                                        sy = 35 - sy;
                                }

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                                videoram.read(offs),
                                                colorram.read(offs),
                                                flipscreen,flipscreen,
                                                8*sx,8*sy,
                                                Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the character mapped graphics */
                copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);


                /* Draw the sprites. */
                for (offs = 0;offs < spriteram_size[0];offs += 2)
                {
                        if ((spriteram_3.read(offs + 1) & 2) == 0)
                        {
                                int code,color,flipx,flipy,sx,sy;


                                code = spriteram.read(offs);
                                color = spriteram.read(offs + 1);
                                flipx = spriteram_3.read(offs) & 2;
                                flipy = spriteram_3.read(offs) & 1;
                                sx = spriteram_2.read(offs) - 16;
                                sy = spriteram_2.read(offs + 1) - 40 + 0x100*(spriteram_3.read(offs + 1) & 1);

                                if (flipscreen!=0)
                                {
                                        flipx = NOT(flipx);
                                        flipy = NOT(flipy);
                                }
                                
                                if ((spriteram_3.read(offs) & 8)!=0)	/* double width */
                                {
                                        drawgfx(bitmap,Machine.gfx[1],
                                                        code+2,color,flipx,flipy,sx,sy,
                                                        Machine.drv.visible_area,TRANSPARENCY_THROUGH,0);
                                        drawgfx(bitmap,Machine.gfx[1],
                                                        code,color,flipx,flipy,sx+16,sy,
                                                        Machine.drv.visible_area,TRANSPARENCY_THROUGH,0);

                                        if ((spriteram_3.read(offs) & 4)!=0)	/* double width, double height */
                                        {
                                                drawgfx(bitmap,Machine.gfx[1],
                                                                code+3,color,flipx,flipy,sx,sy+16,
                                                                Machine.drv.visible_area,TRANSPARENCY_THROUGH,0);
                                                drawgfx(bitmap,Machine.gfx[1],
                                                                code+1,color,flipx,flipy,sx+16,sy+16,
                                                                Machine.drv.visible_area,TRANSPARENCY_THROUGH,0);
                                        }
                                }
                                else if ((spriteram_3.read(offs) & 4)!=0)	/* double height */
                                {
                                        drawgfx(bitmap,Machine.gfx[1],
                                                        code,color,flipx,flipy,sx,sy,
                                                        Machine.drv.visible_area,TRANSPARENCY_THROUGH,0);
                                        drawgfx(bitmap,Machine.gfx[1],
                                                        code+1,color,flipx,flipy,sx,sy+16,
                                                        Machine.drv.visible_area,TRANSPARENCY_THROUGH,0);
                                }
                                else	/* normal */
                                        drawgfx(bitmap,Machine.gfx[1],
                                                        code,color,flipx,flipy,sx,sy,
                                                        Machine.drv.visible_area,TRANSPARENCY_THROUGH,0);
                        }
                }


                /* draw the stars */
                {
                        int bpen;
                        bpen = Machine.pens[0];
                        for (offs = 0;offs < total_stars;offs++)
                        {
                                int x,y;


                                x = stars[offs].x;
                                y = (stars[offs].y + stars_scroll/2) % 256+16;
                           
                                if (((x & 1) ^ ((y >> 4) & 1)) != 0 &&
						(bitmap.line[y][x] == bpen))
					bitmap.line[y][x] = (char) stars[offs].col;
                                
                        }                  
                }
        }};
        public static void galaga_vh_interrupt()
        {
                /* this function is called by galaga_interrupt_1() */
                int s0,s1,s2;
                int speeds[] = { 2, 3, 4, 0, -4, -3, -2, 0 };


                s0 = galaga_starcontrol.read(0) & 1;
                s1 = galaga_starcontrol.read(1) & 1;
                s2 = galaga_starcontrol.read(2) & 1;

                stars_scroll = (stars_scroll- speeds[s0 + s1*2 + s2*4])& 0xFFFF;
        }
  
}
