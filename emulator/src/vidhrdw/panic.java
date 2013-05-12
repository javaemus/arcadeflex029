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

/***************************************************************************

  panic.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

package vidhrdw;

import static arcadeflex.libc.*;
import static arcadeflex.osdepend.*;
import static mame.common.*;
import static mame.commonH.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;

public class panic
{

	/* Sprite number - Bank conversion */

	static char Remap[][] =
	{
            {0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0}, /* 00 */
            {0x00,0},{0x26,0},{0x25,0},{0x24,0},{0x23,0},{0x22,0},{0x21,0},{0x20,0}, /* 08 */
            {0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0}, /* 10 */
            {0x00,0},{0x16,0},{0x15,0},{0x14,0},{0x13,0},{0x12,0},{0x11,0},{0x10,0}, /* 18 */
            {0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0}, /* 20 */
            {0x00,0},{0x06,0},{0x05,0},{0x04,0},{0x03,0},{0x02,0},{0x01,0},{0x00,0}, /* 28 */
            {0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0},{0x00,0}, /* 30 */
            {0x07,2},{0x06,2},{0x05,2},{0x04,2},{0x03,2},{0x02,2},{0x01,2},{0x00,2}, /* 38 */
	};

	/*
	   Colour is via a ROM colour table, 8 screens selected by
	   location's 42FC,42FD & 42FE. 16 colour bands are used for
	   32 lines of screen. Banding is defined in array Band.
	   Memory address of each lookup table is in CLT, along
	   with a flag to indicate whether high or low nibble is
	   used for that layout.

	   The score screen however, uses a different system, which
	   I have'nt been able to identify, so I detect that the
	   screen is displayed using location 700C and colourmap 0
	*/

	static int Band[] =
	{
    		15*32,
		15*32,
		15*32,
		14*32,
		13*32,
		13*32,
		12*32,
		12*32,
		11*32,
		11*32,
		10*32,
		10*32,
		9*32,
		9*32,
		8*32,
		8*32,
		7*32,
		7*32,
		6*32,
		6*32,
		5*32,
		5*32,
		4*32,
		4*32,
		3*32,
		3*32,
		2*32,
		2*32,
		1*32,
		1*32,
		0*32,
		0*32
	};

	static int CLT[][] = {
    		{0x3800,0},{0x3a00,0},{0x3800,1},{0x3a00,1},
    		{0x3c00,0},{0x3e00,0},{0x3c00,1},{0x3e00,1}
	};

	public static CharPtr panic_videoram = new CharPtr();

	static int ColourMap;

	public static WriteHandlerPtr panic_videoram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (panic_videoram.read(offset) != data)
		{
			panic_videoram.write(offset, data);

			/* Restrict to visible area only*/

			if (offset > 0x3ff)
			{
				int i, x, y;
				int col;
				int ColTab;

				x = offset / 32 + 16;
				y = 256-8 - 8 * (offset % 32);

				/* Scoring Screen override - only on colour map 0 */

				if(RAM[0x700C] == 0x80 && ColourMap == 0)
				{
			    	col = Machine.gfx[0].colortable.read(30);   /* Green */
            	}
            	else
            	{
	            	ColTab = RAM[CLT[ColourMap][0] + Band[y / 8] + x / 8 - 2];

    	        	if (CLT[ColourMap][1] == 1) col = Machine.gfx[0].colortable.read(20+(ColTab >> 4));
        	    	else col = Machine.gfx[0].colortable.read(20+(ColTab & 0x0F));
            	}

				for (i = 0;i < 8;i++)
				{

					if ((data & 0x01) != 0) tmpbitmap.line[y][x] = (char) col;
					else tmpbitmap.line[y][x] = Machine.gfx[0].colortable.read(0); /* black */

					y++;
					data >>= 1;
				}
			}
			else
			{
            	if (offset >= 0x2FC && offset <= 0x2FE) /* Colour Map Registers */
    	    	{
                	int x, y, col, ColTab;

                	ColourMap = (RAM[0x42fc]>>7) + (RAM[0x42fd]>>6) + (RAM[0x42fe]>>5);

                	for(x = 0; x < 256; x++) /* Need to re-colour existing screen! */
					{
                		for(y = 0; y < 256; y++)
                		{
                			if(tmpbitmap.line[y][x] != Machine.gfx[0].colortable.read(0))
                			{
                				ColTab = RAM[CLT[ColourMap][0] + Band[y / 8] + x / 8 - 2];

								if (CLT[ColourMap][1] == 1) col = Machine.gfx[0].colortable.read(20+(ColTab >> 4));
								else col = Machine.gfx[0].colortable.read(20+(ColTab & 0x0F));

								tmpbitmap.line[y][x] = (char) col;
                            }
                    	}
                	}
            	}
			}
		}
	} };
                /***************************************************************************

          Start the video hardware emulation.

        ***************************************************************************/
        public static VhStartPtr panic_vh_start= new VhStartPtr() { public int handler()
        {
                if ((tmpbitmap = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
                        return 1;

                return 0;
        }};


        /***************************************************************************

          Stop the video hardware emulation.

        ***************************************************************************/
        public static VhStopPtr panic_vh_stop = new VhStopPtr() { public void handler()
        {
                osd_free_bitmap(tmpbitmap);
                tmpbitmap=null;
        }};
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/

	public static VhUpdatePtr panic_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int offs, Sprite, Bank, Rotate;
		
		/* copy the character mapped graphics */
		
		copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
		
		/* Draw the sprites. Note that it is important to draw them exactly in this */
		/* order, to have the correct priorities. */
		
		for (offs = 0;offs < spriteram_size[0];offs += 4)
		{
			if (spriteram.read(offs) != 0)
		        {
		        	/* Remap sprite number to my layout */
		
				Sprite = Remap[(spriteram.read(offs) & 0x3F)][0];
				Bank   = Remap[(spriteram.read(offs) & 0x3F)][1];
				Rotate = spriteram.read(offs) & 0x40;
		
				/* Switch Bank */
		
				if((spriteram.read(offs+3) & 0x08) != 0) Bank=1;
		
				drawgfx(bitmap, Machine.gfx[Bank],
						    Sprite,
						    spriteram.read(offs+3) & 0x07,
						    Rotate, 0,
						    spriteram.read(offs+1)+16, spriteram.read(offs+2)-16,
						    Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
		        }
		}
	} };
}