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
/**
 *
 * @author shadow
 */
public class taito
{
	public static CharPtr taito_videoram2 = new CharPtr(), taito_videoram3 = new CharPtr();
        public static CharPtr taito_characterram= new CharPtr();
        public static CharPtr taito_scrollx1= new CharPtr(),taito_scrollx2= new CharPtr(),taito_scrollx3= new CharPtr();
        public static CharPtr taito_scrolly1= new CharPtr(),taito_scrolly2= new CharPtr(),taito_scrolly3= new CharPtr();
        public static CharPtr taito_colscrolly1= new CharPtr(),taito_colscrolly2= new CharPtr(),taito_colscrolly3= new CharPtr();
        public static CharPtr taito_gfxpointer= new CharPtr(),taito_paletteram= new CharPtr();
        public static CharPtr taito_colorbank= new CharPtr(),taito_video_priority= new CharPtr(),taito_video_enable= new CharPtr();
        static char []dirtybuffer2;
        static char []dirtybuffer3;
	static osd_bitmap tmpbitmap2, tmpbitmap3;
        public static char[] colors;
        static int dirtypalette;
        static int frontcharset,spacechar;
        static char[] dirtycharacter1=new char[256];
        static char[] dirtycharacter2=new char[256];
        static char[] dirtysprite1=new char[64];
        static char[] dirtysprite2=new char[64];

        public static VhConvertColorPromPtr taito_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
            	int i;


	colors = color_prom;	/* we'll need the colors later to dynamically remap the characters */

	for (i = 0;i < Machine.drv.total_colors;i++)
	{
		int bit0,bit1,bit2;


		bit0 = (~color_prom[2*i+1] >> 6) & 0x01;
		bit1 = (~color_prom[2*i+1] >> 7) & 0x01;
		bit2 = (~color_prom[2*i] >> 0) & 0x01;
		palette[3*i] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
		bit0 = (~color_prom[2*i+1] >> 3) & 0x01;
		bit1 = (~color_prom[2*i+1] >> 4) & 0x01;
		bit2 = (~color_prom[2*i+1] >> 5) & 0x01;
		palette[3*i + 1] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
		bit0 = (~color_prom[2*i+1] >> 0) & 0x01;
		bit1 = (~color_prom[2*i+1] >> 1) & 0x01;
		bit2 = (~color_prom[2*i+1] >> 2) & 0x01;
		palette[3*i + 2] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
	}
        } };
 /***************************************************************************

  Start the video hardware emulation.

***************************************************************************/

 public static VhStartPtr taito_vh_start = new VhStartPtr() { public int handler()
{
        if (generic_vh_start.handler() != 0)
			return 1;

	 if ((dirtybuffer2 = new char[videoram_size[0]]) == null)
	{
		generic_vh_stop.handler();
			return 1;
	}
	memset(dirtybuffer2,1,videoram_size[0]);

	if ((dirtybuffer3 = new char[videoram_size[0]]) == null)
	{
		dirtybuffer2=null;
		generic_vh_stop.handler();
		return 1;
	}
	memset(dirtybuffer3,1,videoram_size[0]);

	if ((tmpbitmap2 = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
	{
		dirtybuffer3=null;
		dirtybuffer2=null;
		generic_vh_stop.handler();
		return 1;
	}

	if ((tmpbitmap3 = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
	{
		osd_free_bitmap(tmpbitmap2);
                tmpbitmap2=null;
		dirtybuffer3=null;
		dirtybuffer2=null;
		generic_vh_stop.handler();
		return 1;
	}

	return 0;
} };
public static VhStartPtr elevator_vh_start = new VhStartPtr() { public int handler()
{
	frontcharset = 0;
	spacechar = 0;
	return taito_vh_start.handler();
} };

public static VhStartPtr junglek_vh_start = new VhStartPtr() { public int handler()
{
	frontcharset = 2;
	spacechar = 0xff;
	return taito_vh_start.handler();
} };

public static VhStartPtr wwestern_vh_start = new VhStartPtr() { public int handler()
{
	frontcharset = 0;
	spacechar = 0xbb;
	return taito_vh_start.handler();
} };
/***************************************************************************

  Stop the video hardware emulation.

***************************************************************************/
public static VhStopPtr taito_vh_stop = new VhStopPtr() { public void handler()
{
	osd_free_bitmap(tmpbitmap3);
	osd_free_bitmap(tmpbitmap2);
        tmpbitmap2=null;
	dirtybuffer3=null;
	dirtybuffer2=null;
        tmpbitmap3=null;
	generic_vh_stop.handler();
} };



public static ReadHandlerPtr taito_gfxrom_r = new ReadHandlerPtr() { public int handler(int offset)
{
       int offs;

       offs= taito_gfxpointer.read(0) + taito_gfxpointer.read(1)*256;
      taito_gfxpointer.write(0, taito_gfxpointer.read(0) + 1 & 0xFF); //taito_gfxpointer[0]++;
      if (taito_gfxpointer.read(0)==0) taito_gfxpointer.write(1, taito_gfxpointer.read(1)+1 & 0xFF); //taito_gfxpointer[1]++;

      if (offs < 0x8000)
	    return Machine.memory_region[2][offs];
      return 0;
}};
 public static WriteHandlerPtr taito_videoram2_w = new WriteHandlerPtr() { public void handler(int offset,int data)
{
		if (taito_videoram2.read(offset) != data)
		{
			dirtybuffer2[offset] = 1;

			taito_videoram2.write(offset, data);
		}

}};
public static WriteHandlerPtr taito_videoram3_w = new WriteHandlerPtr() { public void handler(int offset,int data)
{
		if (taito_videoram3.read(offset) != data)
		{
			dirtybuffer3[offset] = 1;

			taito_videoram3.write(offset, data);
		}
}};
public static WriteHandlerPtr taito_colorbank_w = new WriteHandlerPtr() { public void handler(int offset,int data)
{
	if (taito_colorbank.read(offset) != data)
	{
		memset(dirtybuffer,1,videoram_size[0]);
		memset(dirtybuffer2,1,videoram_size[0]);
		memset(dirtybuffer3,1,videoram_size[0]);

		taito_colorbank.write(offset, data);
	}
}};
 public static WriteHandlerPtr taito_paletteram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
{
	if (taito_paletteram.read(offset) != data)
	{
		dirtypalette = 1;

		taito_paletteram.write(offset, data);
	}
}};
 public static WriteHandlerPtr taito_characterram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
{
	if (taito_characterram.read(offset) != data)
	{
		if (offset < 0x1800)
		{
			dirtycharacter1[(offset / 8) & 0xff] = 1;
			dirtysprite1[(offset / 32) & 0x3f] = 1;
		}
		else
		{
			dirtycharacter2[(offset / 8) & 0xff] = 1;
			dirtysprite2[(offset / 32) & 0x3f] = 1;
		}

		taito_characterram.write(offset, data);
	}
}};
 public static VhUpdatePtr taito_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
{
     	int offs,i;


	/* decode modified characters */
	for (offs = 0;offs < 256;offs++)
	{
		if (dirtycharacter1[offs] == 1)
		{
			decodechar(Machine.gfx[0],offs,taito_characterram,Machine.drv.gfxdecodeinfo[0].gfxlayout);
			dirtycharacter1[offs] = 0;
		}
		if (dirtycharacter2[offs] == 1)
		{
			decodechar(Machine.gfx[2],offs,new CharPtr(taito_characterram,0x1800),Machine.drv.gfxdecodeinfo[2].gfxlayout);
			dirtycharacter2[offs] = 0;
		}
	}
	/* decode modified sprites */
	for (offs = 0;offs < 64;offs++)
	{
		if (dirtysprite1[offs] == 1)
		{
			decodechar(Machine.gfx[1],offs,taito_characterram,Machine.drv.gfxdecodeinfo[1].gfxlayout);
			dirtysprite1[offs] = 0;
		}
		if (dirtysprite2[offs] == 1)
		{
			decodechar(Machine.gfx[3],offs,new CharPtr(taito_characterram,0x1800),Machine.drv.gfxdecodeinfo[3].gfxlayout);
			dirtysprite2[offs] = 0;
		}
	}


	/* if the palette has changed, rebuild the color lookup table */
	if (dirtypalette!=0)
	{
		dirtypalette = 0;

		for (i = 0;i < 8*8;i++)
		{
			int col;


			offs = 0;
			while (offs < Machine.drv.total_colors)
			{
                            if (((taito_paletteram.read(2 * i) & 0x1) == colors[(2 * offs)]) && (taito_paletteram.read(2 * i + 1) == colors[(2 * offs + 1)]))
					break;

				offs++;
			}

			/* avoid undesired transparency */
			if (offs == 0 && i % 8 != 0) offs = 1;
			col = Machine.pens[offs];
			Machine.gfx[0].colortable.write(i,col);
			if (i % 8 == 0) col = Machine.pens[0];	/* create also an alternate color code with transparent pen 0 */
			Machine.gfx[0].colortable.write(i+8*8,col);
		}

		/* redraw everything */
		memset(dirtybuffer,1,videoram_size[0]);
		memset(dirtybuffer2,1,videoram_size[0]);
		memset(dirtybuffer3,1,videoram_size[0]);
	}


	/* for every character in the Video RAM, check if it has been modified */
	/* since last time and update it accordingly. */
	for (offs = videoram_size[0] - 1;offs >= 0;offs--)
	{
		if (dirtybuffer[offs]!=0)
		{
			int sx,sy;


			dirtybuffer[offs] = 0;

			sx = offs % 32;
			sy = offs / 32;

			drawgfx(tmpbitmap,Machine.gfx[0],
					videoram.read(offs),
					taito_colorbank.read(0) & 0x0f,
					0,0,8*sx,8*sy,
					Machine.drv.visible_area,TRANSPARENCY_NONE,0);
		}

		if (dirtybuffer2[offs]!=0)
		{
			int sx,sy;


			dirtybuffer2[offs] = 0;

			sx = 8 * (offs % 32);
			sy = 8 * (offs / 32);

			drawgfx(tmpbitmap2,Machine.gfx[0],
					taito_videoram2.read(offs),
					((taito_colorbank.read(0) >> 4) & 0x0f) + 8,	/* use transparent pen 0 */
					0,0,sx,sy,
					null,TRANSPARENCY_NONE,0);
		}

		if (dirtybuffer3[offs]!=0)
		{
			int sx,sy;


			dirtybuffer3[offs] = 0;

			sx = 8 * (offs % 32);
			sy = 8 * (offs / 32);

			drawgfx(tmpbitmap3,Machine.gfx[0],
					taito_videoram3.read(offs),
					taito_colorbank.read(1) & 0x0f,
					0,0,sx,sy,
					null,TRANSPARENCY_NONE,0);
		}
	}


	/* copy the first playfield */
	{
		int scrollx;
                int scrolly[] =  new int[32];


		scrollx = taito_scrollx3.read();
		scrollx = -((scrollx & 0xf8) | (7 - ((scrollx-1) & 7))) + 18;
		for (i = 0;i < 32;i++)
			scrolly[i] = -taito_colscrolly3.read(i) - taito_scrolly3.read();

		if ((taito_video_enable.read() & 0x40)!=0)
			copyscrollbitmap(bitmap,tmpbitmap3,1,new int[] {scrollx},32,scrolly,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
		else
			clearbitmap(bitmap);
	}
        /* copy the second playfield if it has not priority over sprites */
       // boolean video_enable = ((taito_video_enable.read() & 0x20)!=0) ? true :false;
        // boolean video_priority=((taito_video_priority.read() & 0x08)!=0) ? true :false;
        // if(!(video_enable && video_priority))
//	if ((*taito_video_enable & 0x20) && (*taito_video_priority & 0x08) == 0)
        if (((taito_video_enable.read() & 0x20) != 0) && ((taito_video_priority.read() & 0x8) == 0))
	{
		int scrollx;
                int scrolly[] =  new int[32];


		scrollx = taito_scrollx2.read();
		scrollx = -((scrollx & 0xf8) | (7 - ((scrollx+1) & 7))) + 16;
		for (i = 0;i < 32;i++)
			scrolly[i] = -taito_colscrolly2.read(i) - taito_scrolly2.read();

		copyscrollbitmap(bitmap,tmpbitmap2,1,new int[] {scrollx},32,scrolly,Machine.drv.visible_area,TRANSPARENCY_COLOR,0);
	}


	/* Draw the sprites. Note that it is important to draw them exactly in this */
	/* order, to have the correct priorities. */
	if ((taito_video_enable.read() & 0x80)!=0)
	{
		for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
		{
			drawgfx(bitmap,Machine.gfx[((spriteram.read(offs + 3) & 0x40)!=0) ? 3 : 1],
					spriteram.read(offs + 3) & 0x3f,
					2 * ((taito_colorbank.read(1) >> 4) & 0x0f) + ((spriteram.read(offs+2) >> 2) & 1),
					spriteram.read(offs+2) & 1,0,
					((spriteram.read(offs)+13)&0xff)-15,240-spriteram.read(offs+1),
					Machine.drv.visible_area,TRANSPARENCY_PEN,0);
		}
	}


	/* copy the second playfield if it has priority over sprites */
        if (((taito_video_enable.read() & 0x20) != 0) && ((taito_video_priority.read() & 0x8) != 0))
//	if ((*taito_video_enable & 0x20) && (*taito_video_priority & 0x08) != 0)
	{
		int scrollx;
                int scrolly[] =  new int[32];


		scrollx = taito_scrollx2.read();
		scrollx = -((scrollx & 0xf8) | (7 - ((scrollx+1) & 7))) + 16;
		for (i = 0;i < 32;i++)
			scrolly[i] = -taito_colscrolly2.read(i) - taito_scrolly2.read();

		copyscrollbitmap(bitmap,tmpbitmap2,1,new int[] {scrollx},32,scrolly,Machine.drv.visible_area,TRANSPARENCY_COLOR,0);
	}


	/* draw the frontmost playfield. They are characters, but draw them as sprites */
	if ((taito_video_enable.read() & 0x10)!=0)
	{
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			if (videoram.read(offs) != spacechar)	/* don't draw spaces */
			{
				int sx,sy;


				sx = offs % 32;
				sy = (8*(offs / 32) - taito_colscrolly1.read(sx) - taito_scrolly1.read()) & 0xff;
				/* horizontal scrolling of the frontmost playfield is not implemented */
				sx = 8*sx;

				drawgfx(bitmap,Machine.gfx[frontcharset],
						videoram.read(offs),
						taito_colorbank.read(0) & 0x0f,
						0,0,sx,sy,
						Machine.drv.visible_area,TRANSPARENCY_PEN,0);
			}
		}
	}


}};

}

