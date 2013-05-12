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
import static arcadeflex.osdepend.*;
import static vidhrdw.generic.*;

public class arabian
{

	static osd_bitmap tmpbitmap2;
        static char[] inverse_palette=new char[256];

	/***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
	public static VhStartPtr arabian_vh_start = new VhStartPtr() { public int handler()
	{
            int p1,p2,p3,p4,v1,v2,offs;
            int i;
            if ((tmpbitmap = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
		return 1;

            if ((tmpbitmap2 = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
            {
		osd_free_bitmap(tmpbitmap2);
                tmpbitmap2=null;
		return 1;
            }

             for (i = 0;i < Machine.drv.total_colors;i++)
		inverse_palette[Machine.pens[i] ] = (char)i;
            /*transform graphics data into more usable format*/
            /*which is coded like this:

              byte adr+0x4000  byte adr
              DCBA DCBA        DCBA DCBA

            D-bits of pixel 4
            C-bits of pixel 3
            B-bits of pixel 2
            A-bits of pixel 1

            after conversion :

              byte adr+0x4000  byte adr
              DDDD CCCC        BBBB AAAA
            */

              for (offs=0; offs<0x3fff; offs++)
              {
                 v1 = Machine.memory_region[2][offs];
                 v2 = Machine.memory_region[2][offs+0x4000];

                 p1 = (v1 & 0x01) | ( (v1 & 0x10) >> 3) | ( (v2 & 0x01) << 2 ) | ( (v2 & 0x10) >> 1);
                 v1 = v1 >> 1;
                 v2 = v2 >> 1;
                 p2 = (v1 & 0x01) | ( (v1 & 0x10) >> 3) | ( (v2 & 0x01) << 2 ) | ( (v2 & 0x10) >> 1);
                 v1 = v1 >> 1;
                 v2 = v2 >> 1;
                 p3 = (v1 & 0x01) | ( (v1 & 0x10) >> 3) | ( (v2 & 0x01) << 2 ) | ( (v2 & 0x10) >> 1);
                 v1 = v1 >> 1;
                 v2 = v2 >> 1;
                 p4 = (v1 & 0x01) | ( (v1 & 0x10) >> 3) | ( (v2 & 0x01) << 2 ) | ( (v2 & 0x10) >> 1);

                 Machine.memory_region[2][offs] = (char)(p1 | (p2<<4));
                 Machine.memory_region[2][offs+0x4000] = (char)(p3 | (p4<<4));

              }
              return 0;
	} };


	/***************************************************************************

	  Stop the video hardware emulation.

	***************************************************************************/
	public static VhStopPtr arabian_vh_stop = new VhStopPtr() { public void handler()
	{
		osd_free_bitmap(tmpbitmap2);
		tmpbitmap2 = null;
		osd_free_bitmap(tmpbitmap);
                tmpbitmap=null;
	} };


	/***************************************************************************

	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.

	***************************************************************************/

        static void arabian_blit_byte(int offset, int val, int val2, int plane)
        {

              int plane1, plane2, plane3, plane4;
              CharPtr bm = new CharPtr();
              int p1,p2,p3,p4;

              int	sy;
              int	sx;

              plane1 = plane & 0x01;
              plane2 = plane & 0x02;
              plane3 = plane & 0x04;
              plane4 = plane & 0x08;

              p4 = val & 0x0f;
              p3 = (val >> 4) & 0x0f;
              p2 = val2 & 0x0f;
              p1 = (val2 >> 4) & 0x0f;

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

             if (plane1!=0)
             {
                bm.set(tmpbitmap.line[sy],sx);
                if (p1!=8)
                        bm.write(p1);

                bm.set(tmpbitmap.line[sy+1],sx);
                if (p2!=8)
                        bm.write(p2);

                bm.set(tmpbitmap.line[sy+2],sx);
                if (p3!=8)
                        bm.write(p3);

                bm.set(tmpbitmap.line[sy+3],sx);
                if (p4!=8)
                        bm.write(p4);
             }


             if (plane3!=0)
             {
                bm.set(tmpbitmap2.line[sy],sx);
                if (p1!=8)
                        bm.write(16+p1);

                bm.set(tmpbitmap2.line[sy+1],sx);
                if (p2!=8)
                        bm.write(16+p2);

                bm.set(tmpbitmap2.line[sy+2],sx);
                if (p3!=8)
                        bm.write(16+p3);

                bm.set(tmpbitmap2.line[sy+3],sx);
                if (p4!=8)
                        bm.write(16+p4);

             }
                tmpbitmap.line[sy][sx] = Machine.pens[ tmpbitmap.line[sy][sx] ];
                tmpbitmap.line[sy+1][sx] = Machine.pens[ tmpbitmap.line[sy+1][sx] ];
                tmpbitmap.line[sy+2][sx] = Machine.pens[ tmpbitmap.line[sy+2][sx] ];
                tmpbitmap.line[sy+3][sx] = Machine.pens[ tmpbitmap.line[sy+3][sx] ];
                tmpbitmap2.line[sy][sx] = Machine.pens[ tmpbitmap2.line[sy][sx] ];
                tmpbitmap2.line[sy+1][sx] = Machine.pens[ tmpbitmap2.line[sy+1][sx] ];
                tmpbitmap2.line[sy+2][sx] = Machine.pens[ tmpbitmap2.line[sy+2][sx] ];
                tmpbitmap2.line[sy+3][sx] = Machine.pens[ tmpbitmap2.line[sy+3][sx] ];

        }


	static void arabian_blit_area(int plane, int src, int trg, int xb, int yb)
	{
		int x, y;
		int offs;
                int machine_scrn_x;

		machine_scrn_x = (trg-0x8000) & 0xff;

		for (y = 0; y <= yb; y++)
		{
			offs = trg-0x8000+y*0x100;
			for (x = 0; x <= xb; x++)
			{
                            if ( (offs < 0x3fff) && ( machine_scrn_x + x < 0xf8 ) )
                                arabian_blit_byte(offs,Machine.memory_region[2][src], Machine.memory_region[2][src+0x4000] , plane);
				src++;
				offs++;
			}
		}

	}


	public static WriteHandlerPtr arabian_spriteramw = new WriteHandlerPtr() { public void handler(int offset, int val)
	{

		int pl, src, trg, xb, yb;

		spriteram.write(offset, val);

		if ( (offset%8) == 6 )
		{
			pl  = spriteram.read(offset-6);
			src = spriteram.read(offset-5) + 256 * spriteram.read(offset-4);
			trg = spriteram.read(offset-3) + 256 * spriteram.read(offset-2);
			xb  = spriteram.read(offset-1);
			yb  = spriteram.read(offset);
			arabian_blit_area(pl, src, trg, xb, yb);
		}

	} };



	public static WriteHandlerPtr arabian_videoramw = new WriteHandlerPtr() { public void handler(int offset, int val)
	{
		int plane1, plane2, plane3, plane4;
		CharPtr bm = new CharPtr();
		CharPtr pom = new CharPtr();

		int sx;
		int	sy;

		plane1 = Machine.memory_region[0][0xe000] & 0x01;
		plane2 = Machine.memory_region[0][0xe000] & 0x02;
		plane3 = Machine.memory_region[0][0xe000] & 0x04;
		plane4 = Machine.memory_region[0][0xe000] & 0x08;
                  sx = offset % 256;
                  sy = (0x3f - (offset / 256)) * 4;


                tmpbitmap.line[sy][sx] = inverse_palette[ tmpbitmap.line[sy][sx] ];
                tmpbitmap.line[sy+1][sx] = inverse_palette[ tmpbitmap.line[sy+1][sx] ];
                tmpbitmap.line[sy+2][sx] = inverse_palette[ tmpbitmap.line[sy+2][sx] ];
                tmpbitmap.line[sy+3][sx] = inverse_palette[ tmpbitmap.line[sy+3][sx] ];
                tmpbitmap2.line[sy][sx] = inverse_palette[ tmpbitmap2.line[sy][sx] ];
                tmpbitmap2.line[sy+1][sx] = inverse_palette[ tmpbitmap2.line[sy+1][sx] ];
                tmpbitmap2.line[sy+2][sx] = inverse_palette[ tmpbitmap2.line[sy+2][sx] ];
                tmpbitmap2.line[sy+3][sx] = inverse_palette[ tmpbitmap2.line[sy+3][sx] ];

                pom.set(tmpbitmap.line[sy],sx);
                bm = pom;

             if (plane1!=0)
             {

                bm.and(0xf3);
                if ((val & 0x80) != 0) bm.or(8);
                if ((val & 0x08) != 0) bm.or(4);

                bm.set(tmpbitmap.line[sy+1],sx);
                bm.and(0xf3);
                if ((val & 0x40)  != 0) bm.or(8);
                if ((val & 0x04)  != 0) bm.or(4);

                bm.set(tmpbitmap.line[sy+2],sx);
                bm.and(0xf3);
                if ((val & 0x20)  != 0) bm.or(8);
                if ((val & 0x02)  != 0) bm.or(4);

                bm.set(tmpbitmap.line[sy+3],sx);
                bm.and(0xf3);
                if ((val & 0x10)  != 0) bm.or(8);
                if ((val & 0x01)  != 0) bm.or(4);

             }

             bm=pom;
             if (plane2!=0)
             {
                bm.and(0xfc);
                if ((val & 0x80) != 0) bm.or(2);
                if ((val & 0x08) != 0) bm.or(1);

                bm.set(tmpbitmap.line[sy+1],sx);
                bm.and(0xfc);
                if ((val & 0x40)  != 0) bm.or(2);
                if ((val & 0x04)  != 0) bm.or(1);

                bm.set(tmpbitmap.line[sy+2],sx);
                bm.and(0xfc);
                if ((val & 0x20)  != 0) bm.or(2);
                if ((val & 0x02)  != 0) bm.or(1);

                bm.set(tmpbitmap.line[sy+3],sx);
                bm.and(0xfc);
                if ((val & 0x10)  != 0) bm.or(2);
                if ((val & 0x01)  != 0) bm.or(1);

             }


            pom.set(tmpbitmap2.line[sy],sx);
            bm = pom;

             if (plane3!=0)
             {

                bm.and(0xf3);
                if ((val & 0x80)  != 0) bm.or(16+8);
                if ((val & 0x08)  != 0) bm.or(16+4);

                bm.set(tmpbitmap2.line[sy+1],sx);
                bm.and(0xf3);
                if ((val & 0x40)  != 0) bm.or(16+8);
                if ((val & 0x04)  != 0) bm.or(16+4);

                bm.set(tmpbitmap2.line[sy+2],sx);
                bm.and(0xf3);
                if ((val & 0x20)  != 0) bm.or(16+8);
                if ((val & 0x02)  != 0) bm.or(16+4);

                bm.set(tmpbitmap2.line[sy+3],sx);
                bm.and(0xf3);
                if ((val & 0x10)  != 0) bm.or(16+8);
                if ((val & 0x01)  != 0) bm.or(16+4);

             }

             bm=pom;
             if (plane4!=0)
             {

                bm.and(0xfc);
                if ((val & 0x80) != 0) bm.or(16+2);
                if ((val & 0x08) != 0) bm.or(16+1);

                bm.set(tmpbitmap2.line[sy+1],sx);
                bm.and(0xfc);
                if ((val & 0x40) != 0) bm.or(16+2);
                if ((val & 0x04) != 0) bm.or(16+1);

                bm.set(tmpbitmap2.line[sy+2],sx);
                bm.and(0xfc);
                if ((val & 0x20) != 0) bm.or(16+2);
                if ((val & 0x02) != 0) bm.or(16+1);

                bm.set(tmpbitmap2.line[sy+3],sx);
                bm.and(0xfc);
                if ((val & 0x10) != 0) bm.or(16+2);
                if ((val & 0x01) != 0) bm.or(16+1);

             }

                tmpbitmap.line[sy][sx] = Machine.pens[ tmpbitmap.line[sy][sx] ];
                tmpbitmap.line[sy+1][sx] = Machine.pens[ tmpbitmap.line[sy+1][sx] ];
                tmpbitmap.line[sy+2][sx] = Machine.pens[ tmpbitmap.line[sy+2][sx] ];
                tmpbitmap.line[sy+3][sx] = Machine.pens[ tmpbitmap.line[sy+3][sx] ];
                tmpbitmap2.line[sy][sx] = Machine.pens[ tmpbitmap2.line[sy][sx] ];
                tmpbitmap2.line[sy+1][sx] = Machine.pens[ tmpbitmap2.line[sy+1][sx] ];
                tmpbitmap2.line[sy+2][sx] = Machine.pens[ tmpbitmap2.line[sy+2][sx] ];
                tmpbitmap2.line[sy+3][sx] = Machine.pens[ tmpbitmap2.line[sy+3][sx] ];
	} };


	public static VhUpdatePtr arabian_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
		/* copy the character mapped graphics */
		copybitmap(bitmap, tmpbitmap2, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_NONE, 0);
		copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.drv.visible_area, TRANSPARENCY_COLOR, 0);
	} };
}