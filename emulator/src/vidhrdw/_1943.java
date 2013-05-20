//TODO there are some hacks here due to the fact that 0.29 core features haven't complete. FIX THEM


/*
 * ported to v0.29
 * using automatic conversion tool v0.04
 * converted at : 29-08-2011 17:41:38
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

public class _1943
{
	
	
	
	public static CharPtr c1943_scrollx=new CharPtr();
	public static CharPtr c1943_scrolly=new CharPtr();
	public static CharPtr c1943_bgscrolly=new CharPtr();
	static int chon,objon,sc1on,sc2on;
	static int flipscreen;
	
	static osd_bitmap sc2bitmap;
	static osd_bitmap sc1bitmap;
	static char[][][] sc2map=new char[9][8][2];
	static char[][][] sc1map=new char[9][9][2];
	
	
	
        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
	public static VhConvertColorPromPtr c1943_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
	{
                int i;
                CharPtr color_prom = new CharPtr(color_prom_table);
                CharPtr palette= new CharPtr(palette_table);
		
	
		for (i = 0;i < Machine.drv.total_colors;i++)
		{
			int bit0,bit1,bit2,bit3;
	
	
			bit0 = (color_prom.read(0) >> 0) & 0x01;
			bit1 = (color_prom.read(0) >> 1) & 0x01;
			bit2 = (color_prom.read(0) >> 2) & 0x01;
			bit3 = (color_prom.read(0) >> 3) & 0x01;
			palette.writeinc(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
			bit0 = (color_prom.read(Machine.drv.total_colors) >> 0) & 0x01;
			bit1 = (color_prom.read(Machine.drv.total_colors) >> 1) & 0x01;
			bit2 = (color_prom.read(Machine.drv.total_colors) >> 2) & 0x01;
			bit3 = (color_prom.read(Machine.drv.total_colors) >> 3) & 0x01;
			palette.writeinc(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
			bit0 = (color_prom.read(2*Machine.drv.total_colors) >> 0) & 0x01;
			bit1 = (color_prom.read(2*Machine.drv.total_colors) >> 1) & 0x01;
			bit2 = (color_prom.read(2*Machine.drv.total_colors) >> 2) & 0x01;
			bit3 = (color_prom.read(2*Machine.drv.total_colors) >> 3) & 0x01;
			palette.writeinc(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
	
			color_prom.inc();
		}
	
		color_prom.inc(2*Machine.drv.total_colors);
		/* color_prom now points to the beginning of the lookup table */
	
		/* characters use colors 64-79 */
		for (i = 0;i < TOTAL_COLORS(0);i++)
                        colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char)(color_prom.readinc() + 64);
			//COLOR(0,i) = *(color_prom++) + 64;
		color_prom.inc(128);	/* skip the bottom half of the PROM - not used */
	
		/* foreground tiles use colors 0-63 */
		for (i = 0;i < TOTAL_COLORS(1);i++)
		{
			/* color 0 MUST map to pen 0 in order for transparency to work */
			if (i % Machine.gfx[1].color_granularity == 0)
                        {
				//COLOR(1,i) = 0;
                                 colortable_table[Machine.drv.gfxdecodeinfo[1].color_codes_start + i] =0;
                        }
			else
                        {
				//COLOR(1,i) = color_prom[0] + 16 * (color_prom[256] & 0x03);
                                colortable_table[Machine.drv.gfxdecodeinfo[1].color_codes_start + i]= (char)(color_prom.read(0)+ 16 * (color_prom.read(256) & 0x03));
                        }
			color_prom.inc();
		}
		color_prom.inc(TOTAL_COLORS(1));
	
		/* background tiles use colors 0-63 */
		for (i = 0;i < TOTAL_COLORS(2);i++)
		{
			//COLOR(2,i) = color_prom[0] + 16 * (color_prom[256] & 0x03);
                        colortable_table[Machine.drv.gfxdecodeinfo[2].color_codes_start + i]=(char)(color_prom.read(0) + 16 * (color_prom.read(256) & 0x03));
			color_prom.inc();
		}
		color_prom.inc(TOTAL_COLORS(2));
	
		/* sprites use colors 128-255 */
		/* bit 3 of BMPROM.07 selects priority over the background, but we handle */
		/* it differently for speed reasons */
		for (i = 0;i < TOTAL_COLORS(3);i++)
		{
			//COLOR(3,i) = color_prom[0] + 16 * (color_prom[256] & 0x07) + 128;
                        colortable_table[Machine.drv.gfxdecodeinfo[3].color_codes_start + i]= (char)(color_prom.read(0)+ 16 * (color_prom.read(256) & 0x07) + 128);
			color_prom.inc();
		}
		color_prom.inc(TOTAL_COLORS(3));
	} };
	
	
	
	public static WriteHandlerPtr c1943_c804_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		int bankaddress;
	
	
		/* bits 0 and 1 are for coin counters - we ignore them */
	
		/* bits 2, 3 and 4 select the ROM bank */
		bankaddress = 0x10000 + (data & 0x1c) * 0x1000;
	//	cpu_setbank(1,&RAM[bankaddress]); //TODO fix me when i support bank switching properly
/*TEMPHACK*/    memcpy(RAM,0x8000,RAM,bankaddress,0x4000);
	
		/* bit 5 resets the sound CPU - we ignore it */
	
		/* bit 6 flips screen */
		if (flipscreen != (data & 0x40))
		{
			flipscreen = data & 0x40;
	//		memset(dirtybuffer,1,c1942_backgroundram_size);
		}
	
		/* bit 7 enables characters */
		chon = data & 0x80;
	} };
	
	
	
	public static WriteHandlerPtr c1943_d806_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		/* bit 4 enables bg 1 */
		sc1on = data & 0x10;
	
		/* bit 5 enables bg 2 */
		sc2on = data & 0x20;
	
		/* bit 6 enables sprites */
		objon = data & 0x40;
	} };
	
	
	public static VhStartPtr c1943_vh_start = new VhStartPtr() { public int handler() 
	{
		if ((sc2bitmap = osd_create_bitmap(8*32,9*32)) == null)
			return 1;
		
		if ((sc1bitmap = osd_create_bitmap(9*32,9*32)) == null)
		{
			osd_free_bitmap(sc2bitmap);
                        sc2bitmap=null;
			return 1;
		}
		
		if (generic_vh_start.handler() == 1)
		{
			osd_free_bitmap(sc2bitmap);
			osd_free_bitmap(sc1bitmap);
			return 1;
		}
		
		//memset (sc2map, 0xff, sizeof (sc2map));
		//memset (sc1map, 0xff, sizeof (sc1map));
                for(int i=0; i<sc2map.length; i++)
                {
                    for(int k=0; k<sc2map[i].length; k++)
                    {
                        for(int z=0; z<sc2map[i][k].length; z++)
                        {
                            sc2map[i][k][z]=0xff;
                        }
                    }
                }
                for(int i=0; i<sc1map.length; i++)
                {
                    for(int k=0; k<sc1map[i].length; k++)
                    {
                        for(int z=0; z<sc1map[i][k].length; z++)
                        {
                            sc1map[i][k][z]=0xff;
                        }
                    }
                }
	
		return 0;
	} };
	
	
	public static VhStopPtr c1943_vh_stop = new VhStopPtr() { public void handler() 
	{
		osd_free_bitmap(sc2bitmap);
		osd_free_bitmap(sc1bitmap);
                sc2bitmap=null;
                sc1bitmap=null;
	} };
	
	
	
	
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VhUpdatePtr c1943_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
		int offs,sx,sy;
		int bg_scrolly, bg_scrollx;
		CharPtr p=new CharPtr();
		int top,left,xscroll,yscroll;
	
	/* TODO: support flipscreen */
		if (sc2on != 0)
		{
			p.set(Machine.memory_region[3],0x8000);
			bg_scrolly = c1943_bgscrolly.read(0) + 256 * c1943_bgscrolly.read(1);
			offs = 16 * ((bg_scrolly>>5)+8);
	
			top = 8 - (bg_scrolly>>5) % 9;
	
			bg_scrolly&=0x1f;
	
			for (sy = 0;sy <9;sy++)
			{
				int ty = (sy + top) % 9;
				//unsigned char *map = &sc2map[ty][0][0];
                                CharPtr map = new CharPtr(sc2map[ty][0],0);
				offs &= 0x7fff; /* Enforce limits (for top of scroll) */
	
				for (sx = 0;sx < 8;sx++)
				{
					int tile, attr, offset;
					offset=offs+2*sx;
	
					tile=p.read(offset);
					attr=p.read(offset+1);
					if (tile != map.read(0) || attr != map.read(1))
					{
						map.write(0,tile);
                                                map.write(1,attr);
						drawgfx(sc2bitmap,Machine.gfx[2],
								tile,
								(attr & 0x3c) >> 2,
								attr&0x80, attr&0x40,
								sx*32, ty*32,
								null,
								TRANSPARENCY_NONE,0);
					}
					//map += 2;//NOT NEEEDED??

				}
				offs-=0x10;
			}
	
			xscroll = 0;
			yscroll = -(top*32+32-bg_scrolly);
			copyscrollbitmap(bitmap,sc2bitmap,
				1,new int[] {xscroll},
				1,new int[] {yscroll},
				Machine.drv.visible_area,
				TRANSPARENCY_NONE,0);
		}
		else 
                {
                    fillbitmap(bitmap,Machine.pens[0],Machine.drv.visible_area);
                }
	

		if (objon != 0)
		{
			/* Draw the sprites which don't have priority over the foreground. */
			for (offs = spriteram_size[0] - 32;offs >= 0;offs -= 32)
			{
				int color;
	
	
				color = spriteram.read(offs + 1) & 0x0f;
				if (color == 0x0a || color == 0x0b)	/* the priority is actually selected by */
													/* bit 3 of BMPROM.07 */
				{
					sx = spriteram.read(offs + 2);
					sy = 240 - spriteram.read(offs + 3) + ((spriteram.read(offs + 1) & 0x10) << 4);
					if (flipscreen != 0)
					{
						sx = 240 - sx;
						sy = 240 - sy;
					}
	
					drawgfx(bitmap,Machine.gfx[3],
							spriteram.read(offs) + ((spriteram.read(offs + 1) & 0xe0) << 3),
							color,
							flipscreen,flipscreen,
							sx,sy,
							Machine.drv.visible_area,TRANSPARENCY_PEN,0);
				}
			}
		}
	
	
	/* TODO: support flipscreen */
		if (sc1on != 0)
		{
			p.set(Machine.memory_region[3],0);
	
			bg_scrolly = c1943_scrolly.read(0) + 256 * c1943_scrolly.read(1);
			bg_scrollx = c1943_scrollx.read(0);
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
					//unsigned char *map = &sc1map[ty][tx][0];
                                        CharPtr map = new CharPtr(sc1map[ty][tx],0);
					offset=offs+(sx*2);
	
					tile=p.read(offset);
                                        attr=p.read(offset+1);
					
					if (tile != map.read(0) || attr != map.read(1))
					{
						map.write(0,tile);
                                                map.write(1,attr);
						tile+=256*(attr&0x01);
						drawgfx(sc1bitmap,Machine.gfx[1],
								tile,
								(attr & 0x3c) >> 2,
								attr & 0x80,attr & 0x40,
								tx*32, ty*32,
								null,
								TRANSPARENCY_NONE,0);
					}
				}
				offs-=0x10;
			}
	
			xscroll = -(left*32+bg_scrollx);
			yscroll = -(top*32+32-bg_scrolly);
			copyscrollbitmap(bitmap,sc1bitmap,
				1,new int[] {xscroll},
				1,new int[] {yscroll},
				Machine.drv.visible_area,
				TRANSPARENCY_COLOR,0);
		}
	
	
		if (objon != 0)
		{
			/* Draw the sprites which have priority over the foreground. */
			for (offs = spriteram_size[0] - 32;offs >= 0;offs -= 32)
			{
				int color;
	
	
				color = spriteram.read(offs + 1) & 0x0f;
				if (color != 0x0a && color != 0x0b)	/* the priority is actually selected by */
													/* bit 3 of BMPROM.07 */
				{
					sx = spriteram.read(offs + 2);
					sy = 240 - spriteram.read(offs + 3) + ((spriteram.read(offs + 1) & 0x10) << 4);
					if (flipscreen != 0)
					{
						sx = 240 - sx;
						sy = 240 - sy;
					}
	
					drawgfx(bitmap,Machine.gfx[3],
							spriteram.read(offs) + ((spriteram.read(offs + 1) & 0xe0) << 3),
							color,
							flipscreen,flipscreen,
							sx,sy,
							Machine.drv.visible_area,TRANSPARENCY_PEN,0);
				}
			}
		}
	
	
		if (chon != 0)
		{
			/* draw the frontmost playfield. They are characters, but draw them as sprites */
			for (offs = videoram_size[0] - 1;offs >= 0;offs--)
			{
				int code;
	
	
				code = videoram.read(offs) + ((colorram.read(offs) & 0xe0) << 3);
	
				if (code != 0x24 || colorram.read(offs) != 0)	/* don't draw spaces */
				{
					int sx1,sy1;
	
	
					sx1 = offs / 32;
					sy1 = 31 - offs % 32;
					if (flipscreen != 0)
					{
						sx1 = 31 - sx1;
						sy1 = 31 - sy1;
					}
	
					drawgfx(bitmap,Machine.gfx[0],
							code,
							colorram.read(offs) & 0x1f,
							flipscreen,flipscreen,
							8*sx1,8*sy1,
							Machine.drv.visible_area,TRANSPARENCY_COLOR,79);
				}
			}
		}
	} };
}
