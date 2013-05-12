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

public class gyruss
{
	
	
	
	
	public static CharPtr gyruss_spritebank = new CharPtr();
	public static CharPtr gyruss_6809_drawplanet = new CharPtr();
	public static CharPtr gyruss_6809_drawship = new CharPtr();
	
	
	static class Sprites
	{
		public Sprites() {};
		public void Set(CharPtr cp, int b) { mem = cp; base = b; y = mem.read(base); shape = mem.read(base + 1); attr = mem.read(base + 2); x = mem.read(base + 3); };
		public void SetY(int value) { y = value; mem.write(base, y); };
		public void NegY() { y = (-y) & 0xFF; mem.write(base, y); };
		public void AddY(int value) { y = (y + value) & 0xFF; mem.write(base, y); };
		public void SetShape(int value) { shape = value; mem.write(base + 1, shape); };
		public void SetAttr(int value) { attr = value; mem.write(base + 2, attr); };
		public void SetX(int value) { x = value; mem.write(base + 3, x); };
		public void NegX() { x = (-x) & 0xFF; mem.write(base + 3, x); };
		public void AddX(int value) { x = (x + value) & 0xFF; mem.write(base + 3, x); };
		public int y;
		public int shape;
		public int attr;
		public int x;
		CharPtr mem;
		int base;
	};
	static Sprites _spr = new Sprites();	
	
	
	public static VhConvertColorPromPtr gyruss_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
		int i;
	
	
		for (i = 0; i < 256; i++)
		{
			int bits;
	
	
			bits = (i >> 5) & 0x07;
			palette[3*i] = (char) ((bits >> 1) | (bits << 2) | (bits << 5));
			bits = (i >> 2) & 0x07;
			palette[3*i + 1] = (char) ((bits >> 1) | (bits << 2) | (bits << 5));
			bits = (i >> 0) & 0x03;
			palette[3*i + 2] = (char) (bits | (bits >> 2) | (bits << 4) | (bits << 6));
		}
	
		for (i = 0; i < 320; i++)
			colortable[i] = color_prom[i];
	} };
	
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	public static VhStartPtr gyruss_vh_start = new VhStartPtr() { public int handler()
	{
		if (generic_vh_start.handler() != 0)
			return 1;
	
		return 0;
	} };
	
	
	
	/* convert sprite coordinates from polar to cartesian */
	static int SprTrans(Sprites u)
	{
		final int YTABLE_START = (0x0000);
		final int SINTABLE_START = (0x0400);
		final int COSTABLE_START = (0x0600);
		int ro;
		int theta2;
		CharPtr table = new CharPtr();
	
	
		ro = Machine.memory_region[3][YTABLE_START + u.y];
		theta2 = 2 * u.x;
	
		/* cosine table */
		table.set(Machine.memory_region[3], COSTABLE_START);
	
		u.SetY((table.read(theta2+1) * ro) >> 8);
		if (u.y >= 0x80)
		{
			u.SetY(0);
			return 0;
		}
		if (table.read(theta2) != 0)	/* negative */
		{
			if (u.y >= 0x78)	/* avoid wraparound from top to bottom of screen */
			{
				u.SetY(0);
				return 0;
			}
			u.NegY();
		}
	
		/* sine table */
		table.set(Machine.memory_region[3], SINTABLE_START);
	
		u.SetX((table.read(theta2+1) * ro) >> 8);
		if (u.x >= 0x80)
		{
			u.SetY(0);
			return 0;
		}
		if (table.read(theta2) != 0)	/* negative */
			u.NegX();
	
	
		/* convert from logical coordinates to screen coordinates */
		if ((u.attr & 0x10) != 0)
			u.AddY(0x78);
		else
			u.AddY(0x7C);
	
		u.AddX(0x78);
	
	
	    return 1;	/* queue this sprite */
	}
	
	
	
	/* this macro queues 'nq' sprites at address 'u', into queue at 'q' */
	static final Sprites SPR(CharPtr sr, int n) { _spr.Set(sr, 4*(n)); return _spr; }
	
	
	
	/* Gyruss uses a semaphore system to queue sprites, and when critic
	   region is released, the 6809 processor writes queued sprites onto
	   screen visible area.
	   When a701 = 0 and a702 = 1 gyruss hardware queue sprites.
	   When a701 = 1 and a702 = 0 gyruss hardware draw sprites.

           both Z80 e 6809 are interrupted at the same time by the
           VBLANK interrupt.  If there is some work to do (example
           A7FF is not 0 or FF), 6809 waits for Z80 to store a 1 in
           A701 and draw currently queued sprites
	*/
	public static WriteHandlerPtr gyruss_queuereg_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
		if (data == 1)
		{
	        int n;
			CharPtr sr;
	
	
	        /* Gyruss hardware stores alternatively sprites at position
	           0xa000 and 0xa200.  0xA700 tells which one is used.
	        */
	
			if (gyruss_spritebank.read() == 0)
				sr = spriteram;
			else sr = spriteram_2;
	
	
			/* #0-#3 - ship */
	
			/* #4-#23 */
	        if (gyruss_6809_drawplanet.read() != 0)	/* planet is on screen */
			{
				SprTrans(SPR(sr, 4));	/* #4 - polar coordinates - ship */
	
				SPR(sr, 5).SetY(0);	/* #5 - unused */
	
				/* #6-#23 - planet */
	        }
			else
			{
				for (n = 4; n < 24; n += 2)	/* 10 double height sprites in polar coordinates - enemies flying */
				{
					SprTrans(SPR(sr, n));
	
					SPR(sr, n+1).SetY(0);
				}
			}
	
	
			/* #24-#59 */
			for (n = 24; n < 60; n++)	/* 36 sprites in polar coordinates - enemies at center of screen */
				SprTrans(SPR(sr, n));
	
	
			/* #60-#63 - unused */
	
	
			/* #64-#77 */
	        if (gyruss_6809_drawship.read() == 0)
			{
				for (n = 64; n < 78; n++)	/* 14 sprites in polar coordinates - bullets */
					SprTrans(SPR(sr, n));
			}
			/* else 14 sprites - player ship being formed */
	
	
			/* #78-#93 - stars */
		    for (n = 78; n < 86; n++)
			{
				if (SprTrans(SPR(sr, n)) != 0)
				{
					/* make a mirror copy */
					SPR(sr, n+8).SetX(SPR(sr, n).y - 4);
					SPR(sr, n+8).SetY(SPR(sr, n).x + 4);
				}
				else
					SPR(sr, n+8).SetY(0);
			}
		}
	} };
	
	
	
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VhUpdatePtr gyruss_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		int offs;
	
	
		clearbitmap(bitmap);
	
	
		/*
		   offs+0 :  Ypos
		   offs+1 :  Sprite number
		   offs+2 :  Attribute in the form HF-VF-BK-DH-p3-p2-p1-p0
					 where  HF is horizontal flip
							VF is vertical flip
							BK is for bank select
							DH is for double height (if set sprite is 16*16, else is 16*8)
							px is palette weight
		   offs+3 :  Xpos
		*/
	
		/* Draw the sprites. Note that it is important to draw them exactly in this */
		/* order, to have the correct priorities. */
		{
			CharPtr sr;
	
	
			if (gyruss_spritebank.read() == 0)
				sr = spriteram;
			else sr = spriteram_2;
	
	
			for (offs = spriteram_size[0] - 8;offs >= 0;offs -= 8)
			{
				if ((sr.read(2 + offs) & 0x10) != 0)	/* double height */
				{
					if (sr.read(offs + 0) > 2)
					{
						drawgfx(bitmap, Machine.gfx[(sr.read(offs + 2) & 0x20) != 0 ? 5 : 6],
								sr.read(offs + 1)/2,
								sr.read(offs + 2) & 0x0f,
								sr.read(offs + 2) & 0x80, (sr.read(offs + 2) & 0x40) == 0 ? 1 : 0,
								sr.read(offs + 3), sr.read(offs + 0),
								Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
					}
				}
				else	/* single height */
				{
					if (sr.read(offs + 0) > 2)
					{
						drawgfx(bitmap, Machine.gfx[((sr.read(offs + 2) & 0x20) != 0 ? 1 : 3) + (sr.read(offs + 1) & 1)],
								sr.read(offs + 1)/2,
								sr.read(offs + 2) & 0x0f,
								sr.read(offs + 2) & 0x80, (sr.read(offs + 2) & 0x40) == 0 ? 1 : 0,
								sr.read(offs + 3), sr.read(offs + 0),
								Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
					}
	
					if (sr.read(offs + 4) > 2)
					{
						drawgfx(bitmap, Machine.gfx[((sr.read(offs + 6) & 0x20) != 0 ? 1 : 3) + (sr.read(offs + 5) & 1)],
								sr.read(offs + 5)/2,
								sr.read(offs + 6) & 0x0f,
								sr.read(offs + 6) & 0x80, (sr.read(offs + 6) & 0x40) == 0 ? 1 : 0,
								sr.read(offs + 7), sr.read(offs + 4),
								Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
					}
				}
			}
		}
	
	
		/* draw the frontmost playfield. They are characters, but draw them as sprites */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			int sx, sy, charcode;
	
	
			sx = 8 * (31 - offs / 32);
			sy = 8 * (offs % 32);
	
			charcode = videoram.read(offs) + 8 * (colorram.read(offs) & 0x20);
	
			if (charcode != 0x83) /* don't draw spaces */
				drawgfx(bitmap, Machine.gfx[0],
						charcode,
						colorram.read(offs) & 0x3f,
						colorram.read(offs) & 0x80, colorram.read(offs) & 0x40,
						sx, sy,
						Machine.drv.visible_area, TRANSPARENCY_PEN, 0);
		}
	} };
}