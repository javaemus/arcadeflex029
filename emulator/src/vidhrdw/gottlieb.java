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
 * using automatic conversion tool v0.01
 * converted at : 23-08-2011 23:59:03
 *
 *
 *
 */ 
package vidhrdw;

import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static arcadeflex.osdepend.*;
import static vidhrdw.generic.*;
import static mame.common.*;
import static mame.commonH.*;

public class gottlieb
{
	
	
	
	public static CharPtr gottlieb_paletteram = new CharPtr();
        public static CharPtr gottlieb_characterram = new CharPtr();
	static char[] dirtycharacter = new char[256];
	static int background_priority=0;
	static int hflip=0;
	static int vflip=0;
	static int currentbank;
	
	public static VhStartPtr qbert_vh_start = new VhStartPtr() { public int handler() 
	{
		int offs;
	
	
		for (offs = 0;offs < 256;offs++)
			dirtycharacter[offs] = 0;
	
		return generic_vh_start.handler();
	} };
	
	public static VhStartPtr mplanets_vh_start = new VhStartPtr() { public int handler() 
	{
		int offs;
	
	
		for (offs = 0;offs < 256;offs++)
			dirtycharacter[offs] = 0;
	
		return generic_vh_start.handler();
	} };
	
	public static VhStartPtr reactor_vh_start = new VhStartPtr() { public int handler() 
	{
		int offs;
	
	
		for (offs = 0;offs < 256;offs++)
			dirtycharacter[offs] = 1;
	
		return generic_vh_start.handler();
	} };
	
	public static VhStartPtr stooges_vh_start = new VhStartPtr() { public int handler() 
	{
		int offs;
	
	
		for (offs = 0;offs < 256;offs++)
			dirtycharacter[offs] = 1;
	
		return generic_vh_start.handler();
	} };
	
	public static VhStartPtr krull_vh_start = new VhStartPtr() { public int handler() 
	{
		int offs;
	
	
		for (offs = 0;offs < 256;offs++)
			dirtycharacter[offs] = 1;
	
		return generic_vh_start.handler();
	} };
	static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
	public static VhConvertColorPromPtr gottlieb_vh_init_color_palette= new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
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
	
		/* characters and sprites use the same palette */
		/* we reserve pen 0 for the background black which makes the */
		/* MS-DOS version look better */
		for (i = 0;i < TOTAL_COLORS(0);i++)
			colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char)(i + 1);
	}};
	
	static int last = 0;
	public static void gottlieb_video_outputs(int data)
	{
		background_priority = data & 1;
		hflip = data & 2;
		vflip = data & 4;
		currentbank = (data & 0x10)!=0 ? 256 : 0;
	
		if ((data & 6) != (last & 6))
			memset(dirtybuffer,1,videoram_size[0]);
		last = data;
	}
	
	
	public static WriteHandlerPtr gottlieb_paletteram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		int bit0,bit1,bit2,bit3;
		int r,g,b,val;
	
	
		gottlieb_paletteram.write(offset, data);
	
		/* red component */
		val = gottlieb_paletteram.read(offset | 1);
		bit0 = (val >> 0) & 0x01;
		bit1 = (val >> 1) & 0x01;
		bit2 = (val >> 2) & 0x01;
		bit3 = (val >> 3) & 0x01;
		r = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
	
		/* green component */
		val = gottlieb_paletteram.read(offset & ~1);
		bit0 = (val >> 4) & 0x01;
		bit1 = (val >> 5) & 0x01;
		bit2 = (val >> 6) & 0x01;
		bit3 = (val >> 7) & 0x01;
		g = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
	
		/* blue component */
		val = gottlieb_paletteram.read(offset & ~1);
		bit0 = (val >> 0) & 0x01;
		bit1 = (val >> 1) & 0x01;
		bit2 = (val >> 2) & 0x01;
		bit3 = (val >> 3) & 0x01;
		b = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
	
		osd_modify_pen(Machine.gfx[0].colortable.read(offset / 2),r,g,b);
	} };
	
	
	
	public static WriteHandlerPtr gottlieb_characterram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (gottlieb_characterram.read(offset)!=data) {
			dirtycharacter[offset/32]=1;
			gottlieb_characterram.write(offset, data);
		}
	} };
	
	
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VhUpdatePtr gottlieb_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
	    int offs;
	
	
	    /* recompute character graphics */
	    for (offs=0;offs<256;offs++)
		if (dirtycharacter[offs]!=0)
			decodechar(Machine.gfx[0],offs,gottlieb_characterram,Machine.drv.gfxdecodeinfo[0].gfxlayout);
	
	    /* for every character in the Video RAM, check if it has been modified */
	    /* since last time and update it accordingly. */
	    for (offs = videoram_size[0] - 1;offs >= 0;offs--) {
		if (dirtybuffer[offs]!=0 || dirtycharacter[videoram.read(offs)]!=0) {
		    int sx,sy;
	
		    dirtybuffer[offs] = 0;
	
			if (hflip != 0) sx=31-offs%32;
			else sx=offs%32;
			if (vflip != 0) sy=29-offs/32;
			else sy=offs/32;
	
		    drawgfx(tmpbitmap,Machine.gfx[0],
				videoram.read(offs),  /* code */
				0, /* color tuple */
				hflip, vflip,
				sx*8,sy*8,
				Machine.drv.visible_area, /* clip */
				TRANSPARENCY_NONE,
				0       /* transparent color */
			   );
		}
	    }
	
	    for (offs=0;offs<256;offs++) dirtycharacter[offs]=0;
	
		/* copy the character mapped graphics */
		copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	
		/* Draw the sprites. Note that it is important to draw them exactly in this */
		/* order, to have the correct priorities. */
	    for (offs = 0;offs < spriteram_size[0] - 8;offs += 4)     /* it seems there's something strange with sprites #62 and #63 */
		{
		    int sx,sy;
	
	
			sx = (spriteram.read(offs+1)) - 4;
			if (hflip != 0) sx = 233 - sx;
			sy = (spriteram.read(offs)) - 13;
			if (vflip != 0) sy = 228 - sy;
	
		    if (spriteram.read(offs)!=0 || spriteram.read(offs+1)!=0)
				drawgfx(bitmap,Machine.gfx[1],
						currentbank+(255^spriteram.read(offs+2)), /* object # */
						0, /* color tuple */
						hflip, vflip,
						sx,sy,
						Machine.drv.visible_area,
				/* the background pen for the game is actually 1, not 0, because we reserved */
				/* pen 0 for the background black */
						background_priority!=0 ? TRANSPARENCY_THROUGH : TRANSPARENCY_PEN,background_priority!=0 ? 1 : 0);
		}
	} };
}
