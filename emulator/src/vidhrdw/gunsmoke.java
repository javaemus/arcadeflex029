/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.29
 * using automatic conversion tool v0.04
 * converted at : 31-05-2012 00:00:07
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
import static arcadeflex.osdepend.*;
import static vidhrdw.generic.*;

public class gunsmoke
{
	
	
	public static CharPtr gunsmoke_bg_scrolly=new CharPtr();
	public static CharPtr gunsmoke_bg_scrollx=new CharPtr();
	
	static osd_bitmap bgbitmap;
	static char[][][] bgmap=new char[9][9][2];
        
	
	/***************************************************************************
	
	  Convert the color PROMs into a more useable format.
	
	  Exed Exes has three 256x4 palette PROMs (one per gun) and three 256x4 lookup
	  table PROMs (one for characters, one for sprites, one for background tiles).
	  The palette PROMs are connected to the RGB output this way:
	
	  bit 3 -- 220 ohm resistor  -- RED/GREEN/BLUE
	        -- 470 ohm resistor  -- RED/GREEN/BLUE
	        -- 1  kohm resistor  -- RED/GREEN/BLUE
	  bit 0 -- 2.2kohm resistor  -- RED/GREEN/BLUE
	
	  However, seing as this isn't Exed Exes the format of the colour lookup
	  table may be different. In particular, there are no 16*16 upper layer
	  tiles. This needs to be rewritten when the PROMS become available.
	
	***************************************************************************/
	public static VhConvertColorPromPtr gunsmoke_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom) 
	{
		int i;
	
		for (i = 0;i < 256;i++)
		{
			int bit0,bit1,bit2,bit3;
	
			bit0 = (color_prom[i] >> 0) & 0x01;
			bit1 = (color_prom[i] >> 1) & 0x01;
			bit2 = (color_prom[i] >> 2) & 0x01;
			bit3 = (color_prom[i] >> 3) & 0x01;
			palette[3*i] = (char)(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
			bit0 = (color_prom[i+256] >> 0) & 0x01;
			bit1 = (color_prom[i+256] >> 1) & 0x01;
			bit2 = (color_prom[i+256] >> 2) & 0x01;
			bit3 = (color_prom[i+256] >> 3) & 0x01;
			palette[3*i + 1] = (char)(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
			bit0 = (color_prom[i+256*2] >> 0) & 0x01;
			bit1 = (color_prom[i+256*2] >> 1) & 0x01;
			bit2 = (color_prom[i+256*2] >> 2) & 0x01;
			bit3 = (color_prom[i+256*2] >> 3) & 0x01;
			palette[3*i + 2] = (char)(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
		}
	
		/* characters use colors 192-207 */
		for (i = 0;i < 64*4;i++)
			colortable[i] = (char)(color_prom[i + 256*3] + 192);
	
	        /* 32x32 tiles use colors 0-15 */
		for (i = 64*4;i < 2*64*4;i++)
			colortable[i] = color_prom[i + 256*3];
	
	        /* 16x16 tiles use colors 64-79 . There are no 16*16 tiles in gunsmoke */
		for (i = 2*64*4;i < 2*64*4+16*16;i++)
			colortable[i] = (char)(color_prom[i + 256*3] + 64);
	
		/* sprites use colors 128-192 in four banks */
		for (i = 2*64*4+16*16;i < 2*64*4+16*16+16*16;i++)
			colortable[i] = (char)(color_prom[i + 256*3] + 128 + 16 * color_prom[i + 256*3 + 256]);
	} };
	
	
	
	public static VhStartPtr gunsmoke_vh_start = new VhStartPtr() { public int handler() 
	{
            	if ((bgbitmap = osd_create_bitmap(9*32,9*32)) == null)
		        return 1;
		
		if (generic_vh_start.handler() == 1)
		{
			osd_free_bitmap(bgbitmap);
                        bgbitmap=null;
			return 1;
		}
		
		//memset (bgmap, 0xff, sizeof (bgmap)); //TODO the following implementation is probably ok but check it on future anyway
                for(int i=0; i<bgmap.length; i++) 
                {
                    for(int k=0; k<bgmap[i].length; k++)
                    {
                        for(int z=0; z<bgmap[i][k].length; z++)
                        {
                            bgmap[i][k][z]=0xff;
                        }
                    }
                }
	
		return 0;
	} };
	
	
	public static VhStopPtr gunsmoke_vh_stop = new VhStopPtr() { public void handler() 
	{
		osd_free_bitmap(bgbitmap);
                bgbitmap=null;
	} };
	
	
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VhUpdatePtr gunsmoke_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
            	int offs,sx,sy;
		int bg_scrolly, bg_scrollx;
		CharPtr p=new CharPtr(Machine.memory_region[3]);
		int top,left,xscroll,yscroll;
	
	
		bg_scrolly = gunsmoke_bg_scrolly.read(0) + 256 * gunsmoke_bg_scrolly.read(1);
		bg_scrollx = gunsmoke_bg_scrollx.read(0);
		offs = 16 * ((bg_scrolly>>5)+8)+2*(bg_scrollx>>5) ;
		if ((bg_scrollx & 0x80) != 0) offs -= 0x10;
	
		top = 8 - (bg_scrolly>>5) % 9;
		left = (bg_scrollx>>5) % 9;
                	
		bg_scrolly&=0x1f;
		bg_scrollx&=0x1f;
	
		for (sy = 0;sy <9;sy++)
		{
			int ty = (sy + top) % 9;
			offs &= 0x7fff; /* Enforce limits (for top of scroll) */
			
			for (sx = 0;sx < 9;sx++)
			{
				int tile, attr, offset;
				int tx = (sx + left) % 9;
				//unsigned char *map = &bgmap[ty][tx][0];
                                CharPtr map = new CharPtr(bgmap[ty][tx],0);
				offset=offs+(sx*2);
                                
				tile=p.read(offset);
				attr=p.read(offset+1);
                                
				if (tile != map.read(0) || attr != map.read(1))
				{
					map.write(0,tile);
					map.write(1,attr);
					tile+=256*(attr&0x01);
					drawgfx(bgbitmap,Machine.gfx[1],
							tile,
							(attr & 0x3c) >> 2,
							attr & 0x80,attr & 0x40,
							tx*32, ty*32,
							null,
							TRANSPARENCY_NONE,0);
				}
				//map += 2;
                                map.inc(2);
			}
			offs-=0x10;
		}
	
		xscroll = -(left*32+bg_scrollx);
		yscroll = -(top*32+32-bg_scrolly);
		copyscrollbitmap(bitmap,bgbitmap,
			1,new int[] {xscroll},
			1,new int[] {yscroll},
			Machine.drv.visible_area,
			TRANSPARENCY_NONE,0);
	
		/* Draw the entire background scroll */
	/*#if 0
	/* TODO: this is very slow, have to optimize it using a temporary bitmap */
	/*	for (sy = 0;sy <9;sy++)
		{
			offs &= 0x7fff; /* Enforce limits (for top of scroll) */
	/*		for (sx = 0;sx < 9;sx++)
			{
				int tile, attr, offset;
				offset=offs+(sx*2);
	
				tile=p[offset];
				attr=p[offset+1];
				tile+=256*(attr&0x01);
				drawgfx(bitmap,Machine.gfx[1],
						tile,
						(attr & 0x03c) >> 2,
						attr & 0x80,attr & 0x40,
						sx*32-bg_scrollx, sy*32+bg_scrolly-32,
						&Machine.drv.visible_area,
						TRANSPARENCY_NONE,0);
			}
			offs-=0x10;
		}
	#endif*/
	
	
/* Draw the sprites. */
		for (offs = spriteram_size[0] - 32;offs >= 0;offs -= 32)
		{
			int sprite=spriteram.read(offs + 1)&0xc0;
			sprite<<=2;
			drawgfx(bitmap,Machine.gfx[2],
					spriteram.read(offs)+sprite,
					spriteram.read(offs + 1) & 0x0f,
					spriteram.read(offs + 1) & 0x10, spriteram.read(offs + 1) & 0x20,
					spriteram.read(offs + 2),240 - spriteram.read(offs + 3),
					Machine.drv.visible_area,TRANSPARENCY_PEN,0);
		}
	
	
		/* draw the frontmost playfield. They are characters, but draw them as sprites */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			int code;
	
	
			code = videoram.read(offs) + ((colorram.read(offs) & 0xc0) << 2);
	
			if (code != 0x24 || colorram.read(offs) != 0)		/* don't draw spaces */
			{
				int sx2,sy2;
	
	
				sx2 = offs / 32;
				sy2 = 31 - offs % 32;
	
				drawgfx(bitmap,Machine.gfx[0],
						code,
						colorram.read(offs) & 0x0f,
						0,0,
						8*sx2,8*sy2,
						Machine.drv.visible_area,TRANSPARENCY_COLOR,207);
			}
		}
	} };
}
