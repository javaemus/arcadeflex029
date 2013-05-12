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

public class kangaroo
{
  static osd_bitmap tmpbitmap2;
  static char inverse_palette[]=new char[256];
  
  public static VhStartPtr kangaroo_vh_start = new VhStartPtr() {
    public int handler() {
      	int i;	
	if ((tmpbitmap = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
		return 1;

	if ((tmpbitmap2 = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
	{
		osd_free_bitmap(tmpbitmap);
		return 1;
	}

	/* JB 970727 */
	for (i = 0;i < Machine.drv.total_colors;i++)
		inverse_palette[ Machine.pens[i] ] = (char)i;

	return 0;
    }
  };

  public static VhStopPtr kangaroo_vh_stop = new VhStopPtr() {
    public void handler() {
      osd_free_bitmap(tmpbitmap2);
      osd_free_bitmap(tmpbitmap);
      tmpbitmap = null;
      tmpbitmap2 = null;
      generic_vh_stop.handler();
    }
  };

  public static WriteHandlerPtr kangaroo_spriteramw = new WriteHandlerPtr()
  {
    public void handler(int offset, int val)
    {
          int ofsx, ofsy,x, y, pl, src, xb,yb;


          spriteram.write(offset,val);

          if ( (offset) ==5 )
          {
             pl  = spriteram.read(8);
             src = spriteram.read(0) + 256 * spriteram.read(1) - 0xc000;
        /*     trg = spriteram[2] + 256 * spriteram[3] - 0x8000;*/
        /*     xb  = spriteram[4] ; */
        /*     yb  = spriteram[5] * 4; */
        /*     pl |= ( pl << 1 ); */

             ofsx = spriteram.read(2);
             ofsy = ( 0xbf - spriteram.read(3))*4;
             yb = ofsy - ( spriteram.read(5) *4 );
             xb = ofsx + spriteram.read(4) ;

             for (y=ofsy; y>=yb; y-=4)
               for (x=ofsx; x<=xb; x++, src++)
               {
                 if((pl & 0x0c)!=0)
                   drawgfx(tmpbitmap2,Machine.gfx[0],src,0,0,0,x , y ,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                 if((pl & 0x03)!=0)
                   drawgfx(tmpbitmap,Machine.gfx[0],src,0,0,0,x , y ,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }

          }   
    }
  };
  static void kangaroo_color_shadew(int val)
  {
  }
  public static WriteHandlerPtr kangaroo_videoramw = new WriteHandlerPtr()
  {
    public void handler(int offset, int val) 
    {
        
 	int plane1,plane2,plane3,plane4;
        CharPtr bm = new CharPtr();
	int sx, sy;


	plane1 = Machine.memory_region[0][0xe808] & 0x01;
	plane2 = Machine.memory_region[0][0xe808] & 0x02;
	plane3 = Machine.memory_region[0][0xe808] & 0x04;
	plane4 = Machine.memory_region[0][0xe808] & 0x08;


	sx = offset % 256;
	sy = (0x3f - (offset / 256)) * 4;

	/* JB 970727 */
	tmpbitmap.line[sy][sx] = inverse_palette[ tmpbitmap.line[sy][sx] ];
	tmpbitmap.line[sy+1][sx] = inverse_palette[ tmpbitmap.line[sy+1][sx] ];
	tmpbitmap.line[sy+2][sx] = inverse_palette[ tmpbitmap.line[sy+2][sx] ];
	tmpbitmap.line[sy+3][sx] = inverse_palette[ tmpbitmap.line[sy+3][sx] ];
	tmpbitmap2.line[sy][sx] = inverse_palette[ tmpbitmap2.line[sy][sx] ];
	tmpbitmap2.line[sy+1][sx] = inverse_palette[ tmpbitmap2.line[sy+1][sx] ];
	tmpbitmap2.line[sy+2][sx] = inverse_palette[ tmpbitmap2.line[sy+2][sx] ];
	tmpbitmap2.line[sy+3][sx] = inverse_palette[ tmpbitmap2.line[sy+3][sx] ];


	if (plane2!=0)
	{       
                bm.set(tmpbitmap.line[sy], sx);
                bm.and(0xfc);
		if ((val & 0x80)!=0) bm.or(2);
		if ((val & 0x08)!=0) bm.or(1);

		bm.set(tmpbitmap.line[sy+1], sx);
		bm.and(0xfc);
		if ((val & 0x40)!=0) bm.or(2);
		if ((val & 0x04)!=0) bm.or(1);

		bm.set(tmpbitmap.line[sy+2], sx);
		bm.and(0xfc);
		if ((val & 0x20)!=0) bm.or(2);
		if ((val & 0x02)!=0) bm.or(1);

		bm.set(tmpbitmap.line[sy+3], sx);
		bm.and(0xfc);
		if ((val & 0x10)!=0) bm.or(2);
		if ((val & 0x01)!=0) bm.or(1);
	}

	if (plane1!=0)
	{
		bm.set(tmpbitmap.line[sy], sx);
		bm.and(0xf3);
		if ((val & 0x80)!=0) bm.or(8);
		if ((val & 0x08)!=0) bm.or(4);

		bm.set(tmpbitmap.line[sy+1], sx);
		bm.and(0xf3);
		if ((val & 0x40)!=0) bm.or(8);
		if ((val & 0x04)!=0) bm.or(4);

		bm.set(tmpbitmap.line[sy+2], sx);
		bm.and(0xf3);
		if ((val & 0x20)!=0) bm.or(8);
		if ((val & 0x02)!=0) bm.or(4);

		bm.set(tmpbitmap.line[sy+3], sx);
		bm.and(0xf3);
		if ((val & 0x10)!=0) bm.or(8);
		if ((val & 0x01)!=0) bm.or(4);
	}

	if (plane4!=0)
	{
		bm.set(tmpbitmap.line[sy], sx);
		bm.and(0xfc);
		if ((val & 0x80)!=0) bm.or(2);
		if ((val & 0x08)!=0) bm.or(1);

		bm.set(tmpbitmap.line[sy+1], sx);
		bm.and(0xfc);
		if ((val & 0x40)!=0) bm.or(2);
		if ((val & 0x04)!=0) bm.or(1);

		bm.set(tmpbitmap.line[sy+2], sx);
		bm.and(0xfc);
		if ((val & 0x20)!=0) bm.or(2);
		if ((val & 0x02)!=0) bm.or(1);

		bm.set(tmpbitmap.line[sy+3], sx);
		bm.and(0xfc);
		if ((val & 0x10)!=0) bm.or(2);
		if ((val & 0x01)!=0) bm.or(1);
	}

	if (plane3!=0)
	{
		bm.set(tmpbitmap.line[sy], sx);
		bm.and(0xf3);
		if ((val & 0x80)!=0) bm.or(8);
		if ((val & 0x08)!=0) bm.or(4);

		bm.set(tmpbitmap.line[sy+1], sx);
		bm.and(0xf3);
		if ((val & 0x40)!=0) bm.or(8);
		if ((val & 0x04)!=0) bm.or(4);

		bm.set(tmpbitmap.line[sy+2], sx);
		bm.and(0xf3);
		if ((val & 0x20)!=0) bm.or(8);
		if ((val & 0x02)!=0) bm.or(4);

		bm.set(tmpbitmap.line[sy+3], sx);
		bm.and(0xf3);
		if ((val & 0x10)!=0) bm.or(8);
		if ((val & 0x01)!=0) bm.or(4);
	}

	/* JB 970727 */
	tmpbitmap.line[sy][sx] = Machine.pens[ tmpbitmap.line[sy][sx] ];
	tmpbitmap.line[sy+1][sx] = Machine.pens[ tmpbitmap.line[sy+1][sx] ];
	tmpbitmap.line[sy+2][sx] = Machine.pens[ tmpbitmap.line[sy+2][sx] ];
	tmpbitmap.line[sy+3][sx] = Machine.pens[ tmpbitmap.line[sy+3][sx] ];
	tmpbitmap2.line[sy][sx] = Machine.pens[ tmpbitmap2.line[sy][sx] ];
	tmpbitmap2.line[sy+1][sx] = Machine.pens[ tmpbitmap2.line[sy+1][sx] ];
	tmpbitmap2.line[sy+2][sx] = Machine.pens[ tmpbitmap2.line[sy+2][sx] ];
	tmpbitmap2.line[sy+3][sx] = Machine.pens[ tmpbitmap2.line[sy+3][sx] ];       
    }
  };

  public static VhUpdatePtr kangaroo_vh_screenrefresh = new VhUpdatePtr()
  {
    public void handler(osd_bitmap bitmap) {
        copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
        copybitmap(bitmap,tmpbitmap2,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_COLOR,0);
    }
  };


}