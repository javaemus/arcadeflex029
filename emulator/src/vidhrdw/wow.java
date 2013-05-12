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
 * using automatic conversion tool v0.03
 * converted at : 25-08-2011 11:18:05
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
import static machine.wow.*;
import static mame.cpuintrf.*;
import static Z80.Z80.*;
import static Z80.Z80H.*;

public class wow
{
	
	public static CharPtr wow_videoram = new CharPtr();
	
	
	static int ColourSplit=0;
	static int magic_expand_color, magic_control, collision;
	
	/* Gorf Star Screen */
	
	static final int MAX_STARS =750;
	
	static int StarColour[] =
	{
		0,1,2,3,3,2,1,0
	};
	
	static int total_stars;
	static class star
	{
		public star() {};
		public int x,y,colour;
	};
	static star stars[] = new star[MAX_STARS];
	static { for(int k = 0; k < MAX_STARS; k++) stars[k] = new star(); }
	
	/*
	 * Default colours for WOW
	 *
	 * It doesn't set them up for initial screen
	 */
	
	static int Colour[] =
	{
		0,0,0,0,0xC7,0xF3,0x7C,0x51
	};
	
	public static ReadHandlerPtr wow_intercept_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		int res;
	
		res = collision;
		collision = 0;
	
		return res;
	} };
	
	public static ReadHandlerPtr wow_video_retrace_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	    return CurrentScan;
	} };
	
	/* Doesn't seem to work (i.e. I don;t fully understand it!) */
	
	public static WriteHandlerPtr colour_split_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	/*
		if(ColourSplit != data)
	    {
	        ColourSplit = data;
	        memset(dirtybuffer,1,videoram_size);
	    }
	*/
	    if (errorlog != null) fprintf(errorlog,"Colour split set to %02x\n",data);
	} };
	
	public static WriteHandlerPtr colour_register_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if(Colour[offset] != data)
	    {
			Colour[offset] = data;
	        memset(dirtybuffer,1,videoram_size[0]);
	    }
	
	    if (errorlog != null) fprintf(errorlog,"Colour %01x set to %02x\n",offset,data);
	} };
	
	public static WriteHandlerPtr wow_videoram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if ((offset < 0x4000) && (wow_videoram.read(offset) != data))
		{
			wow_videoram.write(offset,data);
                        dirtybuffer[offset] = 1;
	    }
	} };
	
	public static WriteHandlerPtr wow_magic_expand_color_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                if (errorlog != null) fprintf(errorlog,"%04x: magic_expand_color = %02x\n",cpu_getpc(),data);
		magic_expand_color = data;
	} };
	
	
	public static WriteHandlerPtr wow_magic_control_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                if (errorlog != null) fprintf(errorlog,"%04x: magic_control = %02x\n",cpu_getpc(),data);
		magic_control = data;
	} };
	
	
	public static WriteHandlerPtr copywithflip = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
               if ((magic_control & 0x40)!=0)	/* copy backwards */
                {
                        int bits,stib,k;

                        bits = data;
                        stib = 0;
                        for (k = 0;k < 4;k++)
                        {
                                stib >>= 2;
                                stib |= (bits & 0xc0);
                                bits <<= 2;
                        }

                        data = stib;
                }

                if ((magic_control & 0x40)!=0)	/* copy backwards */
                {
                        int shift,data1,mask;


                        shift = magic_control & 3;
                        data1 = 0;
                        mask = 0xff;
                        while (shift > 0)
                        {
                                data1 <<= 2;
                                data1 |= (data & 0xc0) >> 6;
                                data <<= 2;
                                mask <<= 2;
                                shift--;
                        }

                        if ((magic_control & 0x30)!=0)
                        {
                                /* TODO: the collision detection should be made independently for */
                                /* each of the four pixels */
                                if (((mask & wow_videoram.read(offset))!=0) || ((~mask & wow_videoram.read(offset-1))!=0))
                                        collision |= 0xff;
                                else collision &= 0x0f;
                        }

                        if ((magic_control & 0x20)!=0) data ^= wow_videoram.read(offset);	/* draw in XOR mode */
                        else if ((magic_control & 0x10)!=0) data |= wow_videoram.read(offset);	/* draw in OR mode */
                        else data |= ~mask & wow_videoram.read(offset);	/* draw in copy mode */
                        wow_videoram_w.handler(offset,data);
                        if ((magic_control & 0x20)!=0) data1 ^= wow_videoram.read(offset-1);	/* draw in XOR mode */
                        else if ((magic_control & 0x10)!=0) data1 |= wow_videoram.read(offset-1);	/* draw in OR mode */
                        else data1 |= mask & wow_videoram.read(offset-1);	/* draw in copy mode */
                        wow_videoram_w.handler(offset-1,data1);
                }
                else
                {
                        int shift,data1,mask;


                        shift = magic_control & 3;
                        data1 = 0;
                        mask = 0xff;
                        while (shift > 0)
                        {
                                data1 >>= 2;
                                data1 |= (data & 0x03) << 6;
                                data >>= 2;
                                mask >>= 2;
                                shift--;
                        }

                        if ((magic_control & 0x30)!=0)
                        {
                                /* TODO: the collision detection should be made independently for */
                                /* each of the four pixels */
                                if (((mask & wow_videoram.read(offset))!=0) || ((~mask & wow_videoram.read(offset+1))!=0))
                                        collision |= 0xff;
                                else collision &= 0x0f;
                        }

                        if ((magic_control & 0x20)!=0)
                                data ^= wow_videoram.read(offset);	/* draw in XOR mode */
                        else if ((magic_control & 0x10)!=0)
                                data |= wow_videoram.read(offset);	/* draw in OR mode */
                        else
                                data |= ~mask & wow_videoram.read(offset);	/* draw in copy mode */
                        wow_videoram_w.handler(offset,data);
                        if ((magic_control & 0x20)!=0)
                                data1 ^= wow_videoram.read(offset+1);	/* draw in XOR mode */
                        else if ((magic_control & 0x10)!=0)
                                data1 |= wow_videoram.read(offset+1);	/* draw in OR mode */
                        else
                                data1 |= mask & wow_videoram.read(offset+1);	/* draw in copy mode */
                        wow_videoram_w.handler(offset+1,data1);
                }
	} };
	static int count;
	public static WriteHandlerPtr wow_magicram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                if ((magic_control & 0x08)!=0)	/* expand mode */
                {
                        int bits,bibits,k;

                        bits = data;
                        if (count!=0) bits <<= 4;
                        bibits = 0;
                        for (k = 0;k < 4;k++)
                        {
                                bibits <<= 2;
                                if ((bits & 0x80)!=0) bibits |= (magic_expand_color >> 2) & 0x03;
                                else bibits |= magic_expand_color & 0x03;
                                bits <<= 1;
                        }

                        copywithflip.handler(offset,bibits);

                        count ^= 1;
                }
                else copywithflip.handler(offset,data);
	} };
	
	static int src;
        static int mode;	/*  bit 0 = direction
                                                                bit 1 = expand mode
                                                                bit 2 = constant
                                                                bit 3 = flush
                                                                bit 4 = flip
                                                                bit 5 = flop */
        static int skip;	/* bytes to skip after row copy */
        static int dest;
        static int length;	/* row length */
        static int loops;	/* rows to copy - 1 */
	public static WriteHandlerPtr wow_pattern_board_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
                switch (offset)
                {
                        case 0:
                                src = data;
                                break;
                        case 1:
                                src = src + data * 256;
                                break;
                        case 2:
                                mode = data & 0x3f;			/* register is 6 bit wide */
                                break;
                        case 3:
                                skip = data;
                                break;
                        case 4:
                                dest = skip + data * 256;	/* register 3 is shared between skip and dest */
                                break;
                        case 5:
                                length = data;
                                break;
                        case 6:
                                loops = data;
                                break;
                }

                if (offset == 6)	/* trigger blit */
                {
                        int i,j;

                        if (errorlog!=null) fprintf(errorlog,"%04x: blit src %04x mode %02x skip %d dest %04x length %d loops %d\n",
                         new Object[] { Integer.valueOf(cpu_getpc()), Integer.valueOf(src),Integer.valueOf(mode),Integer.valueOf(skip),Integer.valueOf(dest),Integer.valueOf(length),Integer.valueOf(loops) });
                        
                /* Special scroll screen for Gorf */

                if (src==(dest+0x4000))
                {
                        if(dest==0)
                    {
                        for (i=0x3FFF;i>0;i--) cpu_writemem(i,RAM[i+0x4000]);
                        /* Cycle Steal (slow scroll down!) */
                        Z80_ICount -= 65336;
                    }
                }
                else
                {

                
                
                            for (i = 0; i <= loops;i++)
                            {
                                    for (j = 0;j <= length;j++)
                                    {
                                           if (((mode & 0x08)==0) || j < length)
                                              if ((mode & 0x01)!=0)			/* Direction */
                                                  RAM[src]=RAM[dest];
                                               else
                                                  if (dest >= 0) cpu_writemem(dest,RAM[src]);

                                           if (((j & 1)!=0) || ((mode & 0x02)==0))  /* Expand Mode - don't increment source on odd loops */
                                               if ((mode & 0x04)!=0) src++;		/* Constant mode - don't increment at all! */
                                           if ((mode & 0x20)!=0) dest++;		/* copy forwards */
                                            else dest--;					/* backwards */
                                     }

                                     if (((j & 1)!=0) && ((mode & 0x02)!=0))	    /* always increment source at end of line */
                                           if ((mode & 0x04)!=0) src++;			/* Constant mode - don't increment at all! */

                                            if (((mode & 0x08)!=0) && ((mode & 0x04)!=0)) /* Correct src if in flush mode */
                                                    src--;                          /* and NOT in Constant mode */

                                            if ((mode & 0x20)!=0) dest--;			/* copy forwards */
                                            else dest++;						/* backwards */

                                            dest += (byte)skip;	/* extend the sign of the skip register */

                            /* Note: actually the hardware doesn't handle the sign of the skip register, */
                            /* when incrementing the destination address the carry bit is taken from the */
                            /* mode register. To faithfully emulate the hardware I should do: */
        /*#if 0
                                    {
                                            int lo,hi;

                                            lo = dest & 0x00ff;
                                            hi = dest & 0xff00;
                                            lo += skip;
                                            if (mode & 0x10)
                                            {
                                                    if (lo < 0x100) hi -= 0x100;
                                            }
                                            else
                                            {
                                                    if (lo > 0xff) hi += 0x100;
                                            }
                                            dest = hi | (lo & 0xff);
                                    }
        #endif*/
                           }
                }
                }
	} };
	
	
	/* GORF Special Registers
	 *
	 * These are data writes, done by IN commands
	 *
	 * The data is placed on the upper 8 bits of the address bus (B)
	 *
	 * These probably control :-
	 *
	 * ?? : Ranking Lights
	 * ?? : Sparkle Circuit
	 * ?? : Star Field
	 */
	
	public static ReadHandlerPtr Gorf_IO_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		Z80_Regs regs = new Z80_Regs();
		int data;
	
		Z80_GetRegs(regs);
		data = regs.BC.H;//regs.BC.B.h;
	
	    if (errorlog != null) fprintf(errorlog,"Gorf IO %02x set to %04x\n",0x15+offset,data);
	
	    return data;			/* Probably not used */
	} };
	
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	
	/****************************************************************************
	 * Gorf specific routines
	 ****************************************************************************/
	
	public static VhStartPtr gorf_vh_start = new VhStartPtr() { public int handler() 
	{
		int generator;
		int x,y;
	
		if (generic_vh_start.handler() != 0)
			return 1;
	
	
		/* precalculate the star background */
	
		total_stars = 0;
		generator = 0;
	
		for (y = 319;y >= 0;y--)
		{
			for (x = 204;x >= 0;x--)
			{
				int bit1,bit2;
	
				generator <<= 1;
				bit1 = (~generator >> 17) & 1;
				bit2 = (generator >> 5) & 1;
	
				if ((bit1 ^ bit2)!=0) generator |= 1;
	
				if (y >= Machine.drv.visible_area.min_y &&
					y <= Machine.drv.visible_area.max_y &&
					(((~generator >> 16) & 1)!=0) &&
					(generator & 0x3f) == 0x3f)
				{
					int color;
	
					color = (~(generator >> 8)) & 0x07;
					if (color!=0 && (total_stars < MAX_STARS))
					{
						stars[total_stars].x      = x;
						stars[total_stars].y      = y;
						stars[total_stars].colour = color-1;
	
						total_stars++;
					}
				}
			}
		}
	
		return 0;
	} };
	
	public static void Gorf_CopyLine(int Line)
	{
		/* Copy one line to bitmap, using current colour register settings */
	
	    int memloc;
	    int i,x;
	    int data,color;
	
	    memloc = Line * 80;
	
	    for(i=0;i<80;i++,memloc++)
	    {
	    	if(dirtybuffer[memloc]!=0)
	        {
				data = wow_videoram.read(memloc);
	
	            for(x=i*4+3;x>=i*4;x--)
	            {
	            	color = data & 03;
					tmpbitmap.line[319-x][Line] = Machine.pens[Colour[color]];
	
	                data >>= 2;
	            }
	
	          	dirtybuffer[memloc] = 0;
	        }
	    }
	}
	static int Speed=0;
	public static VhUpdatePtr gorf_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
	    
		int offs;
	
		/* copy the character mapped graphics */
	
		copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	
	    /* Plot the stars (on colour 0 only) */
	
	    if (Colour[0] != 0)
	    {
		    Speed = (Speed + 1) & 3;
	
		    for (offs = 0;offs < total_stars;offs++)
		    {
			    int x,y;
	
			    x = stars[offs].x;
			    y = stars[offs].y;
	
	            if(Speed==0) stars[offs].colour = (stars[offs].colour + 1) & 7;
	
			    if ((Machine.orientation & ORIENTATION_SWAP_XY)!=0)
			    {
				    int temp;
	
				    temp = x;
				    x = y;
				    y = temp;
			    }
	
			    if ((Machine.orientation & ORIENTATION_FLIP_X)!=0)
				    x = 255 - x;
	
			    if ((Machine.orientation & ORIENTATION_FLIP_Y)!=0)
				    y = 255 - y;
	
			    if (bitmap.line[y][x] == Machine.pens[Colour[0]])
				    bitmap.line[y][x] = Machine.pens[Colour[0]+StarColour[stars[offs].colour]];
		    }
	    }
	} };
	
	/****************************************************************************
	 * Seawolf specific routines
	 ****************************************************************************/
	
	public static VhUpdatePtr seawolf2_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
	
	    int x,y,centre;
	
		/* copy the character mapped graphics */
	
		copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	
	    /* Draw a sight */
	
	    if(RAM[0xc1fb] != 0)	/* Number of Players */
	    {
	    	/* Yellow sight for Player 1 */
	
	        centre = 317 - (Controller1-18) * 10;
	
	        if (centre<2)   centre=2;
	        if (centre>317) centre=317;
	
	        for(y=25;y<46;y++) bitmap.line[y][centre] = Machine.pens[0x77];
	
	        for(x=centre-20;x<centre+21;x++)
	            if((x>0) && (x<=319)) bitmap.line[35][x] = Machine.pens[0x77];
	
	        /* Red sight for Player 2 */
	
	        if(RAM[0xc1fb] == 2)
			{
	            centre = 316 - (Controller2-18) * 10;
	
	            if (centre<1)   centre=1;
	            if (centre>316) centre=316;
	
	            for(y=25;y<46;y++) bitmap.line[y][centre] = Machine.pens[0x58];
	
	            for(x=centre-20;x<centre+21;x++)
	                if((x>0) && (x<=319)) bitmap.line[33][x] = Machine.pens[0x58];
	        }
	    }
	} };
	
	/****************************************************************************
	 * Standard WOW routines
	 ****************************************************************************/
	
	public static void CopyLine(int Line)
	{
		/* Copy one line to bitmap, using current colour register settings */
	
	    int memloc;
	    int i,x;
	    int data,color;
	
	    memloc = Line * 80;
	
	    for(i=0;i<80;i++,memloc++)
	    {
	      	if(dirtybuffer[memloc]!=0)
	        {
	        	dirtybuffer[memloc] = 0;
				data = wow_videoram.read(memloc);
	
	            for(x=i*4+3;x>=i*4;x--)
	            {
	            	color = (data & 03);
					if(x < ColourSplit) tmpbitmap.line[Line][x] = Machine.pens[Colour[color]];
	                else tmpbitmap.line[Line][x] = Machine.pens[Colour[color+4]];
	
	                data >>= 2;
	            }
	        }
	    }
	}
	
	public static VhUpdatePtr wow_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
		/* copy the character mapped graphics */
		copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	} };
	
}
