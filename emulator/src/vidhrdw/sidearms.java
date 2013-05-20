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
import static vidhrdw.generic.*;

public class sidearms
{
	
	
	public static CharPtr sidearms_bg_scrollx=new CharPtr();
	public static CharPtr sidearms_bg_scrolly=new CharPtr();
	public static char[] dirtybufferpal;
	public static CharPtr sidearms_paletteram=new CharPtr();
	public static int[] sidearms_paletteram_size=new int[1];
	
	
	public static VhConvertColorPromPtr sidearms_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom) 
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
	} };
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VhStartPtr sidearms_vh_start= new VhStartPtr() { public int handler()
        {

		if (generic_vh_start.handler() != 0)
			return 1;
	
	        /* Palette RAM dirty buffer */
	        if ((dirtybufferpal = new char[sidearms_paletteram_size[0]]) == null)
		{
			generic_vh_stop.handler();
			return 1;
		}
	        memset(dirtybufferpal,1,sidearms_paletteram_size[0]);
	
		return 0;
	
	}};
	
	/***************************************************************************
	
	  Stop the video hardware emulation.
	
	***************************************************************************/
	public static VhStopPtr sidearms_vh_stop = new VhStopPtr() { public void handler() 
	{
	        dirtybufferpal=null;
	        generic_vh_stop.handler();
	} };
	
	public static WriteHandlerPtr sidearms_paletteram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	        if (sidearms_paletteram.read(offset) != data)
		{
	                dirtybufferpal[offset]=1;
	                sidearms_paletteram.write(offset,data);
		}
	} };
	
	
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
		static char chTableRED[]=
	        {
	            0x00, 0x01, 0x01, 0x01, 0x02, 0x02, 0x03, 0x03,
	            0x04, 0x04, 0x05, 0x05, 0x06, 0x06, 0x07, 0x07
	        };
	
	        static char chTableGREEN[]=
	        {
	            0x00, 0x08, 0x08, 0x08, 0x10, 0x10, 0x18, 0x18,
	            0x20, 0x20, 0x28, 0x28, 0x30, 0x30, 0x38, 0x38
	        };
	
	        static char chTableBLUE[]=
	        {
	            0x00, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40,
	            0x80, 0x80, 0x80, 0x80, 0xc0, 0xc0, 0xc0, 0xc0
	        };
	public static VhUpdatePtr sidearms_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
	        int offs, sx, sy;
	        int j, i;
	        int bg_scrolly, bg_scrollx;
                CharPtr p = new CharPtr(Machine.memory_region[3]);
	        //unsigned char *p=Machine.memory_region[3];
	

	
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
	
	                        redgreen=sidearms_paletteram.read(base);
	                        red=redgreen >>4;
	                        green=redgreen & 0x0f ;
	                        blue=sidearms_paletteram.read(bluebase)&0x0f;
	
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
	
	        bg_scrollx = sidearms_bg_scrollx.read(0) + 256 * sidearms_bg_scrollx.read(1);
	        bg_scrolly = 0; //sidearms_bg_scrolly[0];
	        offs  = 16 * ((bg_scrollx>>5)+2);
	        offs += 2*(bg_scrolly>>5);
	
	        bg_scrolly&=0x1f;
	        bg_scrollx&=0x1f;
	
	        /* Draw the entire background scroll */
	        for (sx = 0; sx<14; sx++)
		{
	                offs &= 0x7fff; /* Enforce limits (for top of scroll) */
	                for (sy = 0;sy < 9;sy++)
			{                        
	                        int tile, attr, offset;
	                        offset=offs+(sy*2)%16;
	
	                        tile=p.read(offset);
	                        attr=p.read(offset+1);
	                        tile+=256*(attr&0x01); /* Maybe */
	                        drawgfx(bitmap,Machine.gfx[1],
	                                tile,
	                                (attr&0x38)>>3, /* Wrong */
	                                attr&0x02, attr&0x04,
	                                sx*32-bg_scrollx, sy*32+bg_scrolly-32,
	                                Machine.drv.visible_area,
	                                TRANSPARENCY_NONE,0);
	                }
	                offs+=0x10;
	        }
	
	
		/* Draw the sprites. */
	        for (offs = spriteram_size[0] - 32;offs >= 0;offs -= 32)
		{
	                int attr=spriteram.read(offs + 1);
	                int sprite=attr&0xe0;   /* Possibly wrong */
	                sprite<<=3;
	
	                drawgfx(bitmap,Machine.gfx[2],
	                                spriteram.read(offs)+sprite,
	                                attr & 0x0f,  /* seems OK */   
	                                0, 0,   /* No flipping ? */
	                                spriteram.read(offs + 3)+((attr&0x10)<<4)-0x040,
	                                spriteram.read(offs + 2),
	                                Machine.drv.visible_area,
	                                TRANSPARENCY_PEN,15);
		}
	
	
		/* draw the frontmost playfield. They are characters, but draw them as sprites */
	        for (i=0; i<0x1e; i++)
	        {
	                offs=i*0x40+8;
	                for (j=0; j<0x30; j++)
	                {
	                        int code=videoram.read(offs) + (((int)(colorram.read(offs) & 0xc0)<<2));
	                        if (code != 0x27)     /* don't draw spaces */
	                        {
	                                int sx1,sy1;
	
	                                sy1 = 8 * i;
	                                sx1 = 8 * j;
	
	                                drawgfx(bitmap,Machine.gfx[0],
	                                        code,
	                                        colorram.read(offs) & 0x1f,
	                                        0,0,sx1,sy1,
	                                        Machine.drv.visible_area,TRANSPARENCY_PEN,3);
	                        }
	                        offs++;
	
	                }
		}
	} };
	
		
}
