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
public class mystston {
 
      public static CharPtr mystston_videoram2 = new CharPtr(); 
      public static CharPtr mystston_colorram2 = new CharPtr();
      public static int[] mystston_videoram2_size = new int[1];
      public static CharPtr mystston_scroll = new CharPtr();
      public static CharPtr mystston_paletteram = new CharPtr();

      public static VhConvertColorPromPtr mystston_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
      {

            int i;
            CharPtr color_prom = new CharPtr(color_prom_table);
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
            /* we reserve pen 0 for the background black which makes the */
            /* MS-DOS version look better */
           for (i = 0;i < Machine.drv.color_table_len;i++)
                    colortable_table[i] = (char)(i + 1);
        }};


        /***************************************************************************

          Start the video hardware emulation.

        ***************************************************************************/
       public static VhStartPtr mystston_vh_start = new VhStartPtr() { public int handler()
	{

               if ((dirtybuffer = new char[videoram_size[0]]) == null)
                        return 1;
                memset(dirtybuffer,1,videoram_size[0]);

                /* Mysterious Stones has a virtual screen twice as large as the visible screen */
                if ((tmpbitmap = osd_create_bitmap(2 * Machine.drv.screen_width,Machine.drv.screen_height)) == null)
                {
                        dirtybuffer=null;
                        return 1;
                }

                return 0;
        }};



        /***************************************************************************

          Stop the video hardware emulation.

        ***************************************************************************/
       public static VhStopPtr mystston_vh_stop = new VhStopPtr() { public void handler()
	{
                dirtybuffer=null;
                osd_free_bitmap(tmpbitmap);
                tmpbitmap=null;
        }};


        public static WriteHandlerPtr mystston_paletteram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                int bit0,bit1,bit2;
                int r,g,b;

                mystston_paletteram.write(offset,data);

                bit0 = (data >> 0) & 0x01;
                bit1 = (data >> 1) & 0x01;
                bit2 = (data >> 2) & 0x01;
                r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;

                bit0 = (data >> 3) & 0x01;
                bit1 = (data >> 4) & 0x01;
                bit2 = (data >> 5) & 0x01;
                g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;

                bit0 = 0;
                bit1 = (data >> 6) & 0x01;
                bit2 = (data >> 7) & 0x01;
                b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;

                /* we reserve pen 0 for the background black which makes the */
                /* MS-DOS version look better */
                osd_modify_pen(Machine.pens[offset + 1],r,g,b);
        }};



        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr mystston_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
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

                                sx = 16 * (offs % 32);
                                sy = 16 * (offs / 32);

                                drawgfx(tmpbitmap,Machine.gfx[4 + (colorram.read(offs) & 0x01)],
                                                videoram.read(offs),
                                                0,
                                                BOOL(sx >= 256),0,	/* flip horizontally tiles on the right half of the bitmap */
                                                sx,sy,
                                                null,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the temporary bitmap to the screen */
                {
                        int scrollx;


                        scrollx = -mystston_scroll.read();

                        copyscrollbitmap(bitmap,tmpbitmap,1,new int[] {scrollx},0,null,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }


                /* Draw the sprites */
                for (offs = 0;offs < spriteram_size[0];offs += 4)
                {
                        if ((spriteram.read(offs) & 0x01)!=0)
                        {
                /* the meaning of bit 4 of spriteram[offs] is unknown */
                                drawgfx(bitmap,Machine.gfx[(spriteram.read(offs) & 0x10)!=0 ? 3 : 2],
                                                spriteram.read(offs+1),
                                                0,
                                                spriteram.read(offs) & 0x02,0,
                                                (240 - spriteram.read(offs+2)) & 0xff,spriteram.read(offs+3),
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                        }
                }


                /* draw the frontmost playfield. They are characters, but draw them as sprites */
                for (offs = mystston_videoram2_size[0] - 1;offs >= 0;offs--)
                {
                        if ((mystston_colorram2.read(offs) & 0x07) != 0x07 ||
                                        mystston_videoram2.read(offs) != 0x40)	/* don't draw spaces */
                        {
                                int sx,sy;


                                sx = 8 * (offs % 32);
                                sy = 8 * (offs / 32);

                                drawgfx(bitmap,Machine.gfx[(mystston_colorram2.read(offs) & 0x04)!=0 ? 1 : 0],
                                                mystston_videoram2.read(offs) + 256 * (mystston_colorram2.read(offs) & 0x03),
                                                0,
                                                0,0,
                                                sx,sy,
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                        }
                }
        } };  
    
}
