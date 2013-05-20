
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
import static arcadeflex.osdepend.*;
import static mame.common.*;
import static mame.commonH.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;

public class lwings
{
	
	
	public static CharPtr lwings_paletteram=new CharPtr();
	public static CharPtr lwings_backgroundram=new CharPtr();
	public static CharPtr lwings_backgroundattribram=new CharPtr();
	public static int[] lwings_paletteram_size=new int[1];
	public static int[] lwings_backgroundram_size=new int[1];
	public static CharPtr lwings_scrolly=new CharPtr();
	public static CharPtr lwings_scrollx=new CharPtr();
	
	public static char[] dirtybuffer2;
	public static char[] dirtybuffer3;
	public static char[] dirtybuffer4;
	public static osd_bitmap tmpbitmap2;
	public static char[] lwings_paletteram_dirty=new char[0x40];
	
	
	public static VhConvertColorPromPtr lwings_vh_convert_color_prom= new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
	     int i;
	
	     for (i=0; i<Machine.drv.total_colors; i++)
	     {
	         /*
	         Convert 12 bit RGB to 8 bit RGB. Some blue will be lost.
	         */
	         int red, green, blue;
	         red = (i & 0x07)<<5;
	         green=(i & 0x38)<<2;
	         blue= (i & 0xc0);
	
	         palette[3*i]   =  (char)red;    
	         palette[3*i+1] =  (char)green;  
	         palette[3*i+2] =  (char)blue;   
	     }
	}};
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VhStartPtr lwings_vh_start = new VhStartPtr() { public int handler() 
	{
		if (generic_vh_start.handler() != 0)
			return 1;
	
	        if ((dirtybuffer2 = new char[lwings_backgroundram_size[0]]) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}
	        memset(dirtybuffer2,1,lwings_backgroundram_size[0]);
	
	        /* Palette RAM dirty buffer */
	        if ((dirtybuffer3 = new char[lwings_paletteram_size[0]]) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}
	        memset(dirtybuffer3,1,lwings_paletteram_size[0]);
	        if ((dirtybuffer4 = new char[lwings_paletteram_size[0]]) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}
	        memset(dirtybuffer4,1,lwings_paletteram_size[0]);
	
	
		/* the background area is twice as tall as the screen */
	        if ((tmpbitmap2 = osd_create_bitmap(2*Machine.drv.screen_width,
	                2*Machine.drv.screen_height)) == null)
		{
			dirtybuffer2=null;
			generic_vh_stop.handler();
			return 1;
		}
	
		return 0;
	
	} };
	
	/***************************************************************************
	
	  Stop the video hardware emulation.
	
	***************************************************************************/
	public static VhStopPtr lwings_vh_stop = new VhStopPtr() { public void handler() 
	{
		osd_free_bitmap(tmpbitmap2);
		dirtybuffer2=null;
	        dirtybuffer3=null;
	        dirtybuffer4=null;
		generic_vh_stop.handler();
	} };
	
	public static WriteHandlerPtr lwings_background_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	        if (lwings_backgroundram.read(offset) != data)
		{
			dirtybuffer2[offset] = 1;
	
	                lwings_backgroundram.write(offset, data);
		}
	} };
	
	public static WriteHandlerPtr lwings_backgroundattrib_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	        if (lwings_backgroundattribram.read(offset) != data)
		{
	                dirtybuffer4[offset] = 1;
	
	                lwings_backgroundattribram.write(offset, data);
		}
	} };
	
	
	public static WriteHandlerPtr lwings_paletteram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	        if (lwings_paletteram.read(offset) != data)
		{
	                dirtybuffer3[offset]=1;
	                lwings_paletteram.write(offset, data);
	                /* Mark entire colour schemes dirty if touched */
	                lwings_paletteram_dirty[(offset>>4)&0x3f]=1;
		}
	} };
	
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
		static  char chTableRED[]=
	        {
	            0x00, 0x01, 0x01, 0x01, 0x02, 0x02, 0x03, 0x03,
	            0x04, 0x04, 0x05, 0x05, 0x06, 0x06, 0x07, 0x07
	        };
	
	        static  char chTableGREEN[]=
	        {
	            0x00, 0x08, 0x08, 0x08, 0x10, 0x10, 0x18, 0x18,
	            0x20, 0x20, 0x28, 0x28, 0x30, 0x30, 0x38, 0x38
	        };
	
	
	        static  char chTableBLUE[]=
	        {
	            0x00, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x80,
	            0x80, 0x80, 0x80, 0x80, 0xc0, 0xc0, 0xc0, 0xc0
	        };
	public static VhUpdatePtr lwings_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
	        int j, i, offs;
	

	
	        /* rebuild the colour lookup table */
	        for (j=0; j<3; j++)
		{
	                /*
	                    0000-007f:  background palette. (8x16 colours)
	                    0180-01ff:  sprite palette.     (8x16 colours)
	                    0200-0240:  character palette   (16x4 colours)
	                    0300-0340:  character palette   (16x4 colours)
	                */
	                             /* CHARS TILES SPRITES */
	                int start[]={0x0200, 0x0000, 0x0180};
	                int count[]={  0x40,   0x80,   0x80};
	                int base=start[j];
	                int max=count[j];
	
	                for (i=0; i<max; i++)
			{
	                        if (dirtybuffer3[base]!=0 || dirtybuffer3[base+0x0400]!=0)
	                        {                           
	                           int redgreen=lwings_paletteram.read(base);
	                           int blue=lwings_paletteram.read(base+0x0400)>>4;
	                           int red=redgreen >> 4;
	                           int green=redgreen & 0x0f;
	
	                           dirtybuffer3[base] = dirtybuffer3[base+0x0400] = 0;
	
	                           offs     = chTableGREEN[green];
	                           offs    |= chTableRED[red];
	                           offs    |= chTableBLUE[blue];
	                           Machine.gfx[j].colortable.write(i,Machine.pens[offs]);
	                        }
	                        base++;
			}
	        }
	
	        for (offs = lwings_backgroundram_size[0] - 1;offs >= 0;offs--)
	        {
	                int sx,sy, colour;
	                /*
	                        Tiles
	                        =====
	                        0x80 Tile code MSB
	                        0x40 Tile code MSB
	                        0x20 Tile code MSB
	                        0x10 X flip
	                        0x08 Y flip
	                        0x04 Colour
	                        0x02 Colour
	                        0x01 Colour
	                */
	
	                colour=(lwings_backgroundattribram.read(offs) & 0x07);
	                if (dirtybuffer2[offs] != 0 || dirtybuffer4[offs] != 0 ||
	                      lwings_paletteram_dirty[colour]!=0)
	                {
	                      int code;
	                      dirtybuffer2[offs] = dirtybuffer4[offs] = 0;
	
	                      sx = offs / 32;
	                      sy = offs % 32;
	                      code=lwings_backgroundram.read(offs);
	                      code+=((((int)lwings_backgroundattribram.read(offs)) &0xe0) <<3);
	
	                      drawgfx(tmpbitmap2,Machine.gfx[1],
	                                   code,
	                                   colour,
	                                   (lwings_backgroundattribram.read(offs) & 0x08),
	                                   (lwings_backgroundattribram.read(offs) & 0x10),
	                                   16 * sx,16 * sy,
	                                   null,TRANSPARENCY_NONE,0);
			}
		}
	
	
		/* copy the background graphics */
		{
			int scrollx,scrolly;
	
	                scrolly = -(lwings_scrollx.read(0) + 256 * lwings_scrollx.read(1));
	                scrollx = -(lwings_scrolly.read(0) + 256 * lwings_scrolly.read(1));
	
			copyscrollbitmap(bitmap,tmpbitmap2,1,new int[] {scrollx},1,new int[] {scrolly},Machine.drv.visible_area,TRANSPARENCY_NONE,0);
		}
	
	
		/* Draw the sprites. */
		for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
		{
	                int code,colour,sx,sy;
	
	                /*
	                        Sprites
	                        =======
	                        0x80 Sprite code MSB
	                        0x40 Sprite code MSB
	                        0x20 Colour
	                        0x10 Colour
	                        0x08 Colour
	                        0x04 Y flip
	                        0x02 X flip
	                        0x01 X MSB
	                */
	                sy = spriteram.read(offs + 2);
	                if (sy != 0xf8) /* Small optimization */
	                {                                
	                        code = spriteram.read(offs);
	                        code += ( ((int)(spriteram.read(offs + 1)&0xc0)) << 2 );                        
	                        colour = (spriteram.read(offs + 1) & 0x38)>>3;
	                        sx = spriteram.read(offs + 3)-0x100 * ( spriteram.read(offs + 1) & 0x01);
	
	                        drawgfx(bitmap,Machine.gfx[2],
	                                        code,
	                                        colour,
	                                        spriteram.read(offs + 1)&0x02,
	                                        spriteram.read(offs + 1)&0x04,
	                                        sx, sy,
						Machine.drv.visible_area,TRANSPARENCY_PEN,15);
	                }
	        }
	
		/* draw the frontmost playfield. They are characters, but draw them as sprites */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
	                int code=videoram.read(offs) + (((int)(colorram.read(offs) & 0xc0)<<2));
	                if (code != ' ')     /* don't draw spaces */
			{
				int sx,sy;
	
	                        sy = 8 * (offs / 32);
	                        sx = 8 * (offs % 32);
	
				drawgfx(bitmap,Machine.gfx[0],
	                                        code,
	                                        colorram.read(offs) & 0x0f,
						0,0,sx,sy,
	                                        Machine.drv.visible_area,TRANSPARENCY_PEN,3);
			}
		}
	       // memset(lwings_paletteram_dirty, 0, sizeof(lwings_paletteram_dirty));
                for(int k=0; k<lwings_paletteram_dirty.length; k++)
                {
                    lwings_paletteram_dirty[k]=0;
                }
	} };
	
}
