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
import static vidhrdw.generic.*;
import static arcadeflex.osdepend.*;
import static mame.osdependH.*;

public class jrpacman {
  
    
      public static CharPtr jrpacman_scroll = new CharPtr(); 
      public static CharPtr jrpacman_bgpriority = new CharPtr();
      public static CharPtr jrpacman_charbank = new CharPtr(); 
      public static CharPtr jrpacman_spritebank = new CharPtr();
      public static CharPtr jrpacman_palettebank = new CharPtr(); 
      public static CharPtr jrpacman_colortablebank = new CharPtr();
    /***************************************************************************

      Convert the color PROMs into a more useable format.

      Jr. Pac Man has two 256x4 palette PROMs (the three msb of the address are
      grounded, so the effective colors are only 32) and one 256x4 color lookup
      table PROM.
      The palette PROMs are connected to the RGB output this way:

      bit 3 -- 220 ohm resistor  -- BLUE
            -- 470 ohm resistor  -- BLUE
            -- 220 ohm resistor  -- GREEN
      bit 0 -- 470 ohm resistor  -- GREEN

      bit 3 -- 1  kohm resistor  -- GREEN
            -- 220 ohm resistor  -- RED
            -- 470 ohm resistor  -- RED
      bit 0 -- 1  kohm resistor  -- RED

    ***************************************************************************/
  public static VhConvertColorPromPtr jrpacman_vh_convert_color_prom = new VhConvertColorPromPtr()
  {
    public void handler(char []palette, char []colortable, char []color_prom)
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
		bit1 = (color_prom[i+32] >> 0) & 0x01;
		bit2 = (color_prom[i+32] >> 1) & 0x01;
		palette[3*i + 1] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
		bit0 = 0;
		bit1 = (color_prom[i+32] >> 2) & 0x01;
		bit2 = (color_prom[i+32] >> 3) & 0x01;
		palette[3*i + 2] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
	}

	for (i = 0;i < 64*4;i++)
		colortable[i] = color_prom[i + 64];
	for (i = 64*4;i < 64*4+64*4;i++)
	{
		if (color_prom[i - 64*4 + 64]!=0) 
                    colortable[i] = (char)(color_prom[i - 64*4 + 64] + 0x10);
		else colortable[i] = 0;
	}
    }
  };
    /***************************************************************************

      Start the video hardware emulation.

    ***************************************************************************/
  public static VhStartPtr jrpacman_vh_start = new VhStartPtr()
  {
    public int handler()
    {
        if ((dirtybuffer = new char[videoram_size[0]]) == null)
		return 1;
	memset(dirtybuffer,1,videoram_size[0]);

	/* Jr. Pac Man has a virtual screen twice as large as the visible screen */
	if ((tmpbitmap = osd_create_bitmap(2 * Machine.drv.screen_width,Machine.drv.screen_height)) == null)
	{
		dirtybuffer=null;
		return 1;
	}

	return 0;
        
    }
  };
/***************************************************************************

  Stop the video hardware emulation.

***************************************************************************/
  public static VhStopPtr jrpacman_vh_stop = new VhStopPtr() {
    public void handler() {
      dirtybuffer = null;
      osd_free_bitmap(tmpbitmap);
      tmpbitmap = null;
    }
  };

  public static WriteHandlerPtr jrpacman_videoram_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
  	if (videoram.read(offset) != data)
	{
		dirtybuffer[offset] = 1;

		videoram.write(offset,data);

		if (offset < 32)	/* line color - mark whole line as dirty */
		{
	             for (int i = 2*32;i < 56*32;i += 32)
			dirtybuffer[i + offset] = 1;
                }
        }    
    }
  };

  public static WriteHandlerPtr jrpacman_palettebank_w = new WriteHandlerPtr() {
   public void handler(int offset, int data) {
      if (jrpacman_palettebank.read() != data)
      {
        jrpacman_palettebank.write(data);
        memset(dirtybuffer,1,videoram_size[0]);
      }
    }
  };
  
  public static WriteHandlerPtr jrpacman_colortablebank_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
      if (jrpacman_colortablebank.read() != data)
      {
        jrpacman_colortablebank.write(data);
        memset(dirtybuffer,1,videoram_size[0]);
      }
    }
  };

  public static WriteHandlerPtr jrpacman_charbank_w = new WriteHandlerPtr() {
    public void handler(int offset, int data) {
      if (jrpacman_charbank.read() != data)
      {
        jrpacman.jrpacman_charbank.write(data);
        memset(dirtybuffer,1,videoram_size[0]);
      }
    }
  };
/***************************************************************************

  Draw the game screen in the given osd_bitmap.
  Do NOT call osd_update_display() from this function, it will be called by
  the main emulation engine.

***************************************************************************/
  public static VhUpdatePtr jrpacman_vh_screenrefresh = new VhUpdatePtr()
  {
    public void handler(osd_bitmap bitmap)
    {
	int i,offs;


	/* for every character in the Video RAM, check if it has been modified */
	/* since last time and update it accordingly. */
	for (offs = videoram_size[0] - 1;offs >= 0;offs--)
	{
		if (dirtybuffer[offs]!=0)
		{
			int sx,sy,mx,my;


			dirtybuffer[offs] = 0;

			/* Jr. Pac Man's screen layout is quite awkward */
			mx = offs / 32;
			my = offs % 32;

			if (mx >= 2 && mx < 60)
			{
				if (mx < 56)
				{
					sx = 57 - mx;
					sy = my + 2;

					drawgfx(tmpbitmap,Machine.gfx[0],
							videoram.read(offs) + 256 * jrpacman_charbank.read(),
						/* color is set line by line */
							(videoram.read(my) & 0x1f) + 0x20 * (jrpacman_colortablebank.read() & 1)
									+ 0x40 * (jrpacman_palettebank.read() & 1),
							0,0,8*sx,8*sy,
							null,TRANSPARENCY_NONE,0);
				}
				else
				{
					if (mx >= 58)
					{
						sx = 29 - my;
						sy = mx - 58;

						drawgfx(tmpbitmap,Machine.gfx[0],
								videoram.read(offs),
								(videoram.read(offs + 4*32) & 0x1f) + 0x20 * (jrpacman_colortablebank.read() & 1)
										+ 0x40 * (jrpacman_palettebank.read() & 1),
								0,0,8*sx,8*sy,
								null,TRANSPARENCY_NONE,0);
					}
					else
					{
						sx = 29 - my;
						sy = mx - 22;

						drawgfx(tmpbitmap,Machine.gfx[0],
								videoram.read(offs) + 0x100 * (jrpacman_charbank.read() & 1),
								(videoram.read(offs + 4*32) & 0x1f) + 0x20 * (jrpacman_colortablebank.read() & 1)
										+ 0x40 * (jrpacman_palettebank.read() & 1),
								0,0,8*sx,8*sy,
								null,TRANSPARENCY_NONE,0);
					}
				}
			}
		}
	}


	/* copy the temporary bitmap to the screen */
	{
		int scroll[]=new int[36];


		for (i = 0;i < 2;i++)
			scroll[i] = 0;
		for (i = 2;i < 34;i++)
			scroll[i] = jrpacman_scroll.read() - 224;
		for (i = 34;i < 36;i++)
			scroll[i] = 0;
                
		copyscrollbitmap(bitmap,tmpbitmap,36,scroll,0,null,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	}


	/* Draw the sprites. Note that it is important to draw them exactly in this */
	/* order, to have the correct priorities. */
	for (offs = spriteram_size[0] - 2;offs > 2*2;offs -= 2)
	{
		drawgfx(bitmap,Machine.gfx[1],
				(spriteram.read(offs) >> 2) + 0x40 * (jrpacman_spritebank.read() & 1),
				(spriteram.read(offs + 1) & 0x1f) + 0x20 * (jrpacman_colortablebank.read() & 1)
						+ 0x40 * (jrpacman_palettebank.read() & 1),
				spriteram.read(offs) & 2,spriteram.read(offs) & 1,
				239 - spriteram_2.read(offs),272 - spriteram_2.read(offs + 1),
				Machine.drv.visible_area,
				(jrpacman_bgpriority.read() & 1) !=0 ? TRANSPARENCY_THROUGH : TRANSPARENCY_COLOR,0);
	}
	/* the first two sprites must be offset one pixel to the left */
	for (offs = 2*2;offs > 0;offs -= 2)
	{
		drawgfx(bitmap,Machine.gfx[1],
				(spriteram.read(offs) >> 2) + 0x40 * (jrpacman_spritebank.read() & 1),
				(spriteram.read(offs + 1) & 0x1f) + 0x20 * (jrpacman_colortablebank.read() & 1)
						+ 0x40 * (jrpacman_palettebank.read() & 1),
				spriteram.read(offs) & 2,spriteram.read(offs) & 1,
				238 - spriteram_2.read(offs),272 - spriteram_2.read(offs + 1),
				Machine.drv.visible_area,
				(jrpacman_bgpriority.read() & 1) !=0 ? TRANSPARENCY_THROUGH : TRANSPARENCY_COLOR,0);
	}        
    }
  };    
}
