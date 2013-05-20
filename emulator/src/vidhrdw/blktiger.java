/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

  Video output ports:
    ????? (3)
        0x03 ?????

    Control (4)
        0x80 ???
        0x40 Table / Cocktail mode

    ???? (0x0c)
        0x04 ???
        0x02 ???

    Scroll page (0x0d)
        Read/write scroll page number register

    Screen layout (0x0e)
        Screen layout (8x4 or 4x8)

***************************************************************************/

/*
 * ported to v0.29
 * using automatic conversion tool v0.04
 * converted at : 30-05-2012 23:15:36
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

public class blktiger
{
	
	public static CharPtr blktiger_paletteram=new CharPtr();
	public static CharPtr blktiger_backgroundram=new CharPtr();
	public static char blktiger_video_control;
	public static CharPtr blktiger_screen_layout=new CharPtr();
	public static int[] blktiger_paletteram_size=new int[1];
	public static int[] blktiger_backgroundram_size=new int[1];
	
	public static int blktiger_scroll_bank;
	public static int scroll_page_count=4;
	public static char[] blktiger_scrolly=new char[2];
	public static char[]  blktiger_scrollx=new char[2];
	public static char[] scroll_ram;
	public static char[]  dirtybuffer2;
	public static char[]  dirtybufferpal;
	public static osd_bitmap tmpbitmap2;
	public static osd_bitmap tmpbitmap3;
	public static int screen_layout;
	
	public static char[] scrollpalette_dirty=new char[0x40];
	
	public static int displayfreeze=0;
	
	static int testoutput;
        

	public static WriteHandlerPtr blktiger_test_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	    testoutput=data;
	} };
	
	public static VhConvertColorPromPtr blktiger_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom) 
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
	
	public static VhStartPtr blktiger_vh_start= new VhStartPtr() { public int handler()
        {
		if (generic_vh_start.handler() != 0)
			return 1;
	
	        if ((dirtybuffer2 =new char[blktiger_backgroundram_size[0]*scroll_page_count]) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}
	        memset(dirtybuffer2,1,blktiger_backgroundram_size[0]*scroll_page_count);
	
	        if ((scroll_ram = new char[blktiger_backgroundram_size[0]*scroll_page_count]) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}
	        memset(scroll_ram,0,blktiger_backgroundram_size[0]*scroll_page_count);
	
	
	        /* Palette RAM dirty buffer */
	        if ((dirtybufferpal = new char[blktiger_paletteram_size[0]]) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}
	        memset(dirtybufferpal,1,blktiger_paletteram_size[0]);
	
	
	        /* the background area is 8 x 4 */
	        if ((tmpbitmap2 = osd_create_bitmap(8*Machine.drv.screen_width,
	                scroll_page_count*Machine.drv.screen_height)) == null)
		{
			dirtybuffer2=null;
			generic_vh_stop.handler();
			return 1;
		}
	
	        /* the alternative background area is 4 x 8 */
	        if ((tmpbitmap3 = osd_create_bitmap(4*Machine.drv.screen_width,
	                2*scroll_page_count*Machine.drv.screen_height)) == null)
		{
			dirtybuffer2=null;
	                osd_free_bitmap(tmpbitmap2);
                        tmpbitmap2=null;
			generic_vh_stop.handler();
			return 1;
		}
	
	
	
		return 0;
	
	}};
	
	/***************************************************************************
	
	  Stop the video hardware emulation.
	
	***************************************************************************/
	public static VhStopPtr blktiger_vh_stop = new VhStopPtr() { public void handler() 
	{
		osd_free_bitmap(tmpbitmap2);
                tmpbitmap2=null;
		dirtybuffer2=null;
	        dirtybufferpal=null;
	        scroll_ram=null;
		generic_vh_stop.handler();
	} };
	
	public static WriteHandlerPtr blktiger_background_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	        offset+=blktiger_scroll_bank;
	
	        if (scroll_ram[offset] != data)
		{
			dirtybuffer2[offset] = 1;
	                scroll_ram[offset] = (char)data;
		}
	} };
	
	
	public static WriteHandlerPtr blktiger_scrollbank_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	         blktiger_scroll_bank=(data&0x03)*blktiger_backgroundram_size[0];
	} };
	
	public static ReadHandlerPtr blktiger_background_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	        offset+=blktiger_scroll_bank;
	        return scroll_ram[offset];
	} };
	
	public static WriteHandlerPtr blktiger_scrolly_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	        blktiger_scrolly[offset]=(char)data;
	} };
	
	public static WriteHandlerPtr blktiger_scrollx_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	        blktiger_scrollx[offset]=(char)data;
	} };
	
	public static WriteHandlerPtr blktiger_paletteram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	        if (blktiger_paletteram.read(offset) != data)
		{
	                dirtybufferpal[offset]=1;
	                blktiger_paletteram.write(offset,data);
	                /*
	                   Mark palettes as dirty.
	                   We are only interested in whether tiles have changed.
	                   This is done by colour code.
	
	                   Sprites are always drawn. I don't think that characters
	                   change. If I am wrong then the code below needs to
	                   be changed.
	                */
	                scrollpalette_dirty[(offset>>4)&0x3f]=1;
		}
	} };
	
	public static WriteHandlerPtr blktiger_video_control_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	        blktiger_video_control=(char)data;
	} };
	
	public static WriteHandlerPtr blktiger_freeze_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	        displayfreeze=data;
	} };
	
	public static WriteHandlerPtr blktiger_screen_layout_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	        screen_layout=data;
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
	            0x00, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40,
	            0x80, 0x80, 0x80, 0x80, 0xc0, 0xc0, 0xc0, 0xc0
	        };
	public static VhUpdatePtr blktiger_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
	
	        int offs, sx, sy;
	        int j, i;
	

	
	        /* rebuild the colour lookup table from RAM palette */
	        for (j=0; j<3; j++)
		{
	                /*
	                     0000-0100:  background palettes. (16x16 colours)
	                     0200-0280:  sprites palettes.    ( 8x16 colours)
	                     0300-0340:  characters palettes  (32x4 colours)
	                */
	                            /* CHARS  TILES   SPRITES */
	                int start[]={0x0300, 0x0000, 0x0200};
	                int count[]={0x0080, 0x0100, 0x0100};
	                int base=start[j];
	                int bluebase=base+0x0400;
	                int max=count[j];
	
	
	                for (i=0; i<max; i++)
	                {
	                    if (dirtybufferpal[base]!=0 || dirtybufferpal[bluebase]!=0)
	                    {
	                        int red, green, blue, redgreen;
	
	                        redgreen=blktiger_paletteram.read(base);
	                        red=redgreen >>4;
	                        green=redgreen & 0x0f ;
	                        blue=blktiger_paletteram.read(bluebase)&0x0f;
	
	                        dirtybufferpal[base] = dirtybufferpal[bluebase] = 0;
	
	                        offs     = chTableGREEN[green];
	                        offs    |= chTableRED[red];
	                        offs    |= chTableBLUE[blue];
	
	                        Machine.gfx[j].colortable.write(i,Machine.pens[offs]);
	                    }
	                    base++;
	                    bluebase++;
	                }
	        }
	
	        /* Dirty all touched colours */
	        for (j=blktiger_backgroundram_size[0]*scroll_page_count-1; j>=0; j-=2)
	        {
	                int colour=(scroll_ram[j]&0x78)>>3;
	                if (scrollpalette_dirty[colour]!=0)
	                        dirtybuffer2[j]=1;
	        }
	
	        /*
	        Draw the tiles.
	
	        This method may look unnecessarily complex. Only tiles that are
	        likely to be visible are drawn. The rest are kept dirty until they
	        become visible.
	
	        The reason for this is that on level 3, the palette changes a lot
	        if the whole virtual screen is checked and redrawn then the
	        game will slow down to a crawl.
	        */
	
	        if (screen_layout != 0)
	        {
	                /* 8x4 screen */
	                int offsetbase;
	                int scrollx,scrolly, y;
	                scrollx = ((blktiger_scrollx[0]>>4) + 16 * blktiger_scrollx[1]);
	                scrolly = ((blktiger_scrolly[0]>>4) + 16 * blktiger_scrolly[1]);
	
	                for (sy=0; sy<18; sy++)
	                {
	                    y=(scrolly+sy)&(16*4-1);
	                    offsetbase=((y&0xf0)<<8)+32*(y&0x0f);
	                    for (sx=0; sx<18; sx++)
	                    {
	                        int colour, attr, code, x;
	                        x=(scrollx+sx)&(16*8-1);
	                        offs=offsetbase + ((x&0xf0)<<5)+2*(x&0x0f);
	
	                        if (dirtybuffer2[offs]!=0 || dirtybuffer2[offs+1]!=0 )
	                        {
	
	                                attr=scroll_ram[offs+1];
	                                colour=(attr&0x78)>>3;
	                                code=scroll_ram[offs];
	                                code+=256*(attr&0x07);
	
	                                dirtybuffer2[offs] =dirtybuffer2[offs+1] = 0 ;
	
	                                drawgfx(tmpbitmap2,Machine.gfx[1],
	                                           code,
	                                           colour,
	                                           attr & 0x80,
	                                           0,
	                                           x*16, y*16,
	                                           null,TRANSPARENCY_NONE,16);
	                       }
	                    }
	                }
	
	                /* copy the background graphics */
	                {
	                        int scrollx1,scrolly1;
	
	                        scrollx1 = -(blktiger_scrollx[0] + 256 * blktiger_scrollx[1]);
	                        scrolly1 = -(blktiger_scrolly[0] + 256 * blktiger_scrolly[1]);
	                        copyscrollbitmap(bitmap,tmpbitmap2,1,new int[] {scrollx1 },1,new int[] {scrolly1},Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	                }
	        }
	        else
	        {
	                /* 4x8 screen */
	                int offsetbase;
	                int scrollx,scrolly, y;
	                scrollx = ((blktiger_scrollx[0]>>4) + 16 * blktiger_scrollx[1]);
	                scrolly = ((blktiger_scrolly[0]>>4) + 16 * blktiger_scrolly[1]);
	
	                for (sy=0; sy<18; sy++)
	                {
	                    y=(scrolly+sy)&(16*8-1);
	                    offsetbase=((y&0xf0)<<7)+32*(y&0x0f);
	                    for (sx=0; sx<18; sx++)
	                    {
	                        int colour, attr, code, x;
	                        x=(scrollx+sx)&(16*4-1);
	                        offs=offsetbase + ((x&0xf0)<<5)+2*(x&0x0f);
	
	                        if (dirtybuffer2[offs]!=0 || dirtybuffer2[offs+1]!=0 )
	                        {
	                                attr=scroll_ram[offs+1];
	                                colour=(attr&0x78)>>3;
	
	                                code=scroll_ram[offs];
	                                code+=256*(attr&0x07);
	
	                                dirtybuffer2[offs] =dirtybuffer2[offs+1] = 0 ;
	
	                                drawgfx(tmpbitmap3,Machine.gfx[1],
	                                           code,
	                                           colour,
	                                           attr & 0x80,
	                                           0,
	                                           x*16, y*16,
	                                           null,TRANSPARENCY_NONE,16);
	                         }
	                     }
	                }
	
	                /* copy the background graphics */
	                {
	                        int scrollx2,scrolly2;
	
	                        scrollx2 = -(blktiger_scrollx[0] + 256 * blktiger_scrollx[1]);
	                        scrolly2 = -(blktiger_scrolly[0] + 256 * blktiger_scrolly[1]);
	                        copyscrollbitmap(bitmap,tmpbitmap3,1,new int[] {scrollx2},1,new int[] {scrolly2},Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	                }
	        }
	
		/* Draw the sprites. */
		for (offs = spriteram_size[0] - 4;offs >= 0;offs -= 4)
		{
	                /* SPRITES
	                   =====
	                   Attribute
	                   0x80 Code MSB
	                   0x40 Code MSB
	                   0x20 Code MSB
	                   0x10 X MSB
	                   0x08 X flip
	                   0x04 Colour
	                   0x02 Colour
	                   0x01 Colour
	                */
	
	
	                int code2,colour2,sx2,sy2;
	
	                code2 = spriteram.read(offs);
	                code2 += ( ((int)(spriteram.read(offs+1)&0xe0)) << 3 );
	                colour2 = spriteram.read(offs+1) & 0x07;
	
	                sy2 = spriteram.read(offs + 2);
	                sx2 = spriteram.read(offs + 3)-0x10 * ( spriteram.read(offs + 1) & 0x10);
	
	                drawgfx(bitmap,Machine.gfx[2],
	                                        code2,
	                                        colour2,
	                                        spriteram.read(offs+1)&0x08,
	                                        0,
	                                        sx2, sy2,
	                                        Machine.drv.visible_area,TRANSPARENCY_PEN,15);
	        }
	
		/* draw the frontmost playfield. They are characters, but draw them as sprites */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
	                int code=videoram.read(offs) + (((int)(colorram.read(offs) & 0xe0)<<3));
	                if (code != 0x20)     /* don't draw spaces */
	                {
				int sx2,sy2;
	
	                        sy2 = 8 * (offs / 32);
	                        sx2 = 8 * (offs % 32);
	
				drawgfx(bitmap,Machine.gfx[0],
	                                        code,
	                                        colorram.read(offs) & 0x0f,
	                                        0,0,sx2,sy2,
	                                        Machine.drv.visible_area,TRANSPARENCY_PEN,3);
			}
	        }
	        memset(scrollpalette_dirty, 0, sizeof(scrollpalette_dirty));
	} };
}
