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
 * rewrote for v0.28
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

public class cclimber {
        static final int BIGSPRITE_WIDTH= 128;
        static final int BIGSPRITE_HEIGHT= 128;
        public static CharPtr cclimber_bsvideoram = new CharPtr();
        public static int[] cclimber_bsvideoram_size=new int[1];
	public static CharPtr cclimber_bigspriteram = new CharPtr();
	public static CharPtr cclimber_column_scroll = new CharPtr();
	static char bsdirtybuffer[];
	static osd_bitmap bsbitmap;
        static int[] flipscreen=new int[2];

        public static VhConvertColorPromPtr cclimber_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
                int i;
                for (i = 0;i < 4 * 24;i++)
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

                for (i = 0;i < 4 * 24;i++)
                {
                        if ((i & 3) == 0) colortable[i] = 0;
                        else colortable[i] = (char)i;
                }
        }};



	public static VhStartPtr cclimber_vh_start = new VhStartPtr() {	public int handler()
	{
		if (generic_vh_start.handler() != 0)
			return 1;
	        if ((bsdirtybuffer = new char[cclimber_bsvideoram_size[0]]) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}
		memset(bsdirtybuffer,1,cclimber_bsvideoram_size[0]);
		if ((bsbitmap = osd_create_bitmap(BIGSPRITE_WIDTH, BIGSPRITE_HEIGHT)) == null)
		{
                        bsdirtybuffer=null;
			generic_vh_stop.handler();
			return 1;
		}

		return 0;
	} };



	public static VhStopPtr cclimber_vh_stop = new VhStopPtr() { public void handler()
	{
		osd_free_bitmap(bsbitmap);
		bsbitmap = null;
                 bsdirtybuffer=null;
		generic_vh_stop.handler();
	} };


        public static WriteHandlerPtr cclimber_flipscreen_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                if (flipscreen[offset] != (data & 1))
                {
                        flipscreen[offset] = (data & 1);
                        memset(dirtybuffer,1,videoram_size[0]);
                }
        }};



	public static WriteHandlerPtr cclimber_colorram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (colorram.read(offset) != data)
		{
			/* bit 5 of the address is not used for color memory. There is just */
			/* 512 bytes of memory; every two consecutive rows share the same memory */
			/* region. */
			offset &= 0xffdf;

			dirtybuffer[offset] = 1;
			dirtybuffer[offset + 0x20] = 1;

			colorram.write(offset, data);
			colorram.write(offset + 0x20, data);
		}
	} };



	public static WriteHandlerPtr cclimber_bigsprite_videoram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (cclimber_bsvideoram.read(offset) != data)
		{
			bsdirtybuffer[offset] = 1;

			cclimber_bsvideoram.write(offset, data);
		}
	} };



        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        static void drawbigsprite(osd_bitmap bitmap)
        {
                int sx,sy,flipx,flipy;


                sx = 136 - cclimber_bigspriteram.read(3);
                sy = 128 - cclimber_bigspriteram.read(2);
                flipx = cclimber_bigspriteram.read(1) & 0x10;
                flipy = cclimber_bigspriteram.read(1) & 0x20;
                if (flipscreen[1]!=0)	/* only the Y direction has to be flipped */
               {
                        sy = 128 - sy;
                        flipy = NOT(flipy);
                        
                }

                copybitmap(bitmap,bsbitmap,
                                flipx,flipy,
                                sx,sy,
                                Machine.drv.visible_area,TRANSPARENCY_COLOR,0);
        }

        static int lastcol;
        public static VhUpdatePtr cclimber_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                int offs;


                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if (dirtybuffer[offs]!=0)
                        {
                                int sx,sy,flipx,flipy;


                                dirtybuffer[offs] = 0;

                                sx = offs % 32;
                                sy = offs / 32;
                                flipx = colorram.read(offs) & 0x40;
                                flipy = colorram.read(offs) & 0x80;
                                if (flipscreen[0]!=0)
                                {
                                        sx = 31 - sx;
                                        flipx = NOT(flipx);
                                }
                                if (flipscreen[1]!=0)
                                {
                                        sy = 31 - sy;
                                        flipy = NOT(flipy);
                                }

                                drawgfx(tmpbitmap,Machine.gfx[(colorram.read(offs) & 0x10)!=0 ? 1 : 0],
                                                videoram.read(offs) + 8 * (colorram.read(offs) & 0x20),
                                                colorram.read(offs) & 0x0f,
                                                flipx,flipy,
                                                8*sx,8*sy,
                                                null,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the temporary bitmap to the screen */
                {
                        int scroll[]=new int[32];


                        for (offs = 0;offs < 32;offs++)
                                scroll[offs] = -cclimber_column_scroll.read(offs);

                        copyscrollbitmap(bitmap,tmpbitmap,0,null,32,scroll,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }


                /* update the "big sprite" */
                {
                        int newcol;


                        newcol = cclimber_bigspriteram.read(1) & 0x07;

                        for (offs = cclimber_bsvideoram_size[0] - 1;offs >= 0;offs--)
                        {
                                int sx,sy;


                                if ((bsdirtybuffer[offs]!=0) || (newcol != lastcol))
                                {
                                        bsdirtybuffer[offs] = 0;

                                        sx = offs % 16;
                                        sy = offs / 16;

                                        drawgfx(bsbitmap,Machine.gfx[2],
                                                        cclimber_bsvideoram.read(offs),newcol,
                                                        0,0,
                                                        8*sx,8*sy,
                                                        null,TRANSPARENCY_NONE,0);
                                }

                        }

                        lastcol = newcol;
                }


                if ((cclimber_bigspriteram.read(0) & 1)!=0)
                        /* draw the "big sprite" below sprites */
                        drawbigsprite(bitmap);


                /* draw sprites */
                for (offs = 0;offs < spriteram_size[0];offs += 4)
                {
                        int sx,sy,flipx,flipy;


                        sx = spriteram.read(offs+3);
                        sy = 240 - spriteram.read(offs+2);
                        flipx = spriteram.read(offs) & 0x40;
                        flipy = spriteram.read(offs) & 0x80;
                        if (flipscreen[0]!=0)
                        {
                                sx = 240 - sx;
                                flipx = NOT(flipx);
                        }
                        if (flipscreen[1]!=0)
                        {
                                sy = 240 - sy;
                                flipy = NOT(flipy);
                        }

                        drawgfx(bitmap,Machine.gfx[(spriteram.read(offs + 1) & 0x10)!=0 ? 4 : 3],
                                        (spriteram.read(offs) & 0x3f) + 2 * (spriteram.read(offs+1) & 0x20),
                                        spriteram.read(offs+1) & 0x0f,
                                        flipx,flipy,
                                        sx,sy,
                                        Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                }


                if ((cclimber_bigspriteram.read(0) & 1) == 0)
                        /* draw the "big sprite" over sprites */
                        drawbigsprite(bitmap);
        }};
    
}
