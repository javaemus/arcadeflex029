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
import static arcadeflex.osdepend.*;
import static mame.common.*;
import static mame.commonH.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;

public class ccastles {
    static osd_bitmap sprite_bm;
    static osd_bitmap maskbitmap;
    static int dirtypalette;

    static int ax;
    static int ay;
    static char xcoor;
    static char ycoor;


        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
        public static VhConvertColorPromPtr ccastles_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
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


                /* sprites */
                /* we should use colors 0-15, but we swap them with 16-31 to have a black color 0 */
                for (i = 0;i < TOTAL_COLORS(0);i++)
                        colortable_table[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] = (char)(i + 16);
                /* background */
                for (i = 0;i < TOTAL_COLORS(1);i++)
                        colortable_table[Machine.drv.gfxdecodeinfo[1].color_codes_start + i] = (char)i;
    }};


    public static VhStartPtr ccastles_vh_start = new VhStartPtr() { public int handler()
    {
            if ((tmpbitmap = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
                    return 1;

            if ((maskbitmap = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
            {
                    osd_free_bitmap(tmpbitmap);
                    tmpbitmap=null;
                    return 1;
            }

            if ((sprite_bm = osd_create_bitmap(8,16)) == null)
            {
                    osd_free_bitmap(maskbitmap);
                    osd_free_bitmap(tmpbitmap);
                    maskbitmap=null;
                    tmpbitmap=null;
                    return 1;
            }

            return 0;
    }};


    public static VhStopPtr ccastles_vh_stop = new VhStopPtr() { public void handler()
    {
            osd_free_bitmap(sprite_bm);
            osd_free_bitmap(maskbitmap);
            osd_free_bitmap(tmpbitmap);
            sprite_bm=null;
            maskbitmap=null;
            tmpbitmap=null;
    }};
    public static WriteHandlerPtr ccastles_paletteram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
    {
	int r,g,b;
	int bit0,bit1,bit2;


	/* we swap colors 0-15 with colors 16-31 because color 0 is red and color */
	/* 16 is black. The MS-DOS version looks better with a black color 0. */
	offset ^= 0x10;

	r = (data & 0xC0) >> 6;
	b = (data & 0x38) >> 3;
	g = (data & 0x07);
	/* a write to offset 32-63 means to set the msb of the red component */
	if ((offset & 0x20)!=0) r += 4;

	/* bits are inverted */
	r = 7-r;
	g = 7-g;
	b = 7-b;

	bit0 = (r >> 0) & 0x01;
	bit1 = (r >> 1) & 0x01;
	bit2 = (r >> 2) & 0x01;
	r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	bit0 = (g >> 0) & 0x01;
	bit1 = (g >> 1) & 0x01;
	bit2 = (g >> 2) & 0x01;
	g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	bit0 = (b >> 0) & 0x01;
	bit1 = (b >> 1) & 0x01;
	bit2 = (b >> 2) & 0x01;
	b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;

	osd_modify_pen(Machine.pens[offset & 0x1f],r,g,b);
    }};


   public static ReadHandlerPtr ccastles_bitmode_r = new ReadHandlerPtr() {
     public int handler(int offset) 
     {
    
      int addr;

      addr = (ycoor<<7) | (xcoor>>1);

      if(ax==0) {
        if(RAM[0x9F02]==0) {
          xcoor++;
        } else {
          xcoor--;
        }
      }

      if(ay==0) {
        if(RAM[0x9F03]==0) {
          ycoor++;
        } else {
          ycoor--;
        }
      }

      if((xcoor & 0x01)!=0) {
        return((RAM[addr] & 0x0F) << 4);
      } else {
        return(RAM[addr] & 0xF0);
      }
    }};
    public static WriteHandlerPtr ccastles_xy_w = new WriteHandlerPtr() { public void handler(int offset,int data)
    {
      RAM[offset] = (char)data;

      if(offset == 0x0000) {
        xcoor = (char)data;
      } else {
        ycoor = (char)data;
      }
    }};
    public static WriteHandlerPtr ccastles_axy_w = new WriteHandlerPtr() { public void handler(int offset,int data)
    {
      RAM[0x9F00+offset] = (char)data;

      if(offset == 0x0000) {
        if(data!=0) {
          ax = data;
        } else {
          ax = 0x00;
        }
      } else {
        if(data!=0) {
          ay = data;
        } else {
          ay = 0x00;
        }
      }
    }};
    public static WriteHandlerPtr ccastles_bitmode_w = new WriteHandlerPtr() { public void handler(int offset,int data)
    {
      int addr;
      int mode;

      RAM[0x0002] = (char)data;
      if((xcoor & 0x01)!=0) {
        mode = (data >> 4) & 0x0F;
      } else {
        mode = (data & 0xF0);
      }

      addr = (ycoor<<7) | (xcoor>>1);

      if((addr>0x0BFF) && (addr<0x8000))
      {
        if((xcoor & 0x01)!=0) {
          RAM[addr] = (char)((RAM[addr] & 0xF0) | mode);
        } else {
          RAM[addr] = (char)((RAM[addr] & 0x0F) | mode);
        }


            {
                    int x,y,j;


                    j = 2*(addr - 0xc00);
                    x = j%256;
                    y = j/256;
                    tmpbitmap.line[y][x] = Machine.gfx[1].colortable.read((RAM[addr] & 0xF0) >> 4);
                    tmpbitmap.line[y][x+1] = Machine.gfx[1].colortable.read(RAM[addr] & 0x0F);

                    /* if bit 3 of the pixel is set, background has priority over sprites when */
                    /* the sprite has the priority bit set. We use a second bitmap to remember */
                    /* which pixels have priority. */
                    maskbitmap.line[y][x] = (char)(RAM[addr] & 0x80);
                    maskbitmap.line[y][x+1] = (char)(RAM[addr] & 0x08);
            }
      }


      if(ax==0) {
        if(RAM[0x9F02]==0) {
          xcoor++;
        } else {
          xcoor--;
        }
      }

      if(ay==0) {
        if(RAM[0x9F03]==0) {
          ycoor++;
        } else {
          ycoor--;
        }
      }
    }};


    public static VhUpdatePtr ccastles_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
    {
            int offs;
            int i,j;
            int x,y;
            int spriteaddr;
            int scrollx,scrolly;


            /* if colors have changed, redraw the whole screen. This doesn't happen often, */
            /* so we can do it without affecting performance */
            if (dirtypalette!=0)
            {
                    j = 0;

                    for(i = 0x0C00;i < 0x8000;i += 2)
                    {
                            tmpbitmap.line[j/256][j%256] = Machine.gfx[1].colortable.read((RAM[i] & 0xF0) >> 4);
                            tmpbitmap.line[j/256][j%256+1] = Machine.gfx[1].colortable.read(RAM[i] & 0x0F);
                            tmpbitmap.line[j/256][j%256+2] = Machine.gfx[1].colortable.read((RAM[i+1] & 0xF0) >> 4);
                            tmpbitmap.line[j/256][j%256+3] = Machine.gfx[1].colortable.read(RAM[i+1] & 0x0F);

                            j+=4;
                    }

                    dirtypalette = 0;
            }

            scrollx = 256 - RAM[0x9c80];
            scrolly = 256 - RAM[0x9d00];

            copyscrollbitmap(bitmap,tmpbitmap,1,new int[] {scrollx},1,new int[] {scrolly},
                       Machine.drv.visible_area,
                       TRANSPARENCY_NONE,0);


            if(RAM[0x9F07]!=0) {
            spriteaddr = 0x8F00;
            } else {
            spriteaddr = 0x8E00;
            }


            /* Draw the sprites */
            for (offs = 0x00;offs < 0x100;offs += 0x04)
            {
                    /* Get the X and Y coordinates from the MOB RAM */
                    x = RAM[spriteaddr+offs+3];
                    y = 216 - RAM[spriteaddr+offs+1];

                    if ((RAM[spriteaddr+offs+2] & 0x80)!=0)	/* background can have priority over the sprite */
                    {
                            clearbitmap(sprite_bm);
                            drawgfx(sprite_bm,Machine.gfx[0],
                                            RAM[spriteaddr+offs],1,
                                            0,0,
                                            0,0,
                                            null,TRANSPARENCY_PEN,7);

                            for (j = 0;j < 16;j++)
                            {
                                    if (y + j >= 0)	/* avoid accesses out of the bitmap boundaries */
                                    {
                                            for (i = 0;i < 8;i++)
                                            {
                                                    char pixa,pixb;


                                                    pixa = sprite_bm.line[j][i];
                                                    pixb = maskbitmap.line[(y+scrolly+j)%232][(x+scrollx+i)%256];

                                                    /* if background has priority over sprite, make the */
                                                    /* temporary bitmap transparent */
                                                    if (pixb != 0 && (pixa != Machine.gfx[0].colortable.read(0)))
                                                            sprite_bm.line[j][i] = Machine.pens[0];
                                            }
                                    }
                            }

                            copybitmap(bitmap,sprite_bm,0,0,x,y,Machine.drv.visible_area,TRANSPARENCY_COLOR,0);
                    }
                    else
                    {
                            drawgfx(bitmap,Machine.gfx[0],
                                            RAM[spriteaddr+offs],1,
                                            0,0,
                                            x,y,
                                            Machine.drv.visible_area,TRANSPARENCY_PEN,7);
                    }
            }
    }};
}
