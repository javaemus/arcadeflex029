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


public class bublbobl {
    public static CharPtr bublbobl_objectram = new CharPtr();
    public static CharPtr bublbobl_paletteram = new CharPtr();
    public static int[] bublbobl_objectram_size=new int[1];
    
        static int TOTAL_COLORS(int gfxn) 
        {
            return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
        }
    	public static VhConvertColorPromPtr bublbobl_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
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

                    /* initialize the color table */
                    for (i = 0;i < Machine.drv.total_colors;i++)
                            colortable_table[i] = (char)i;
        }};



        /***************************************************************************

          Start the video hardware emulation.

        ***************************************************************************/
        public static VhStartPtr bublbobl_vh_start = new VhStartPtr() { public int handler()
	{
                /* In Bubble Bobble the video RAM and the color RAM and interleaved, */
                /* forming a contiguous memory region 0x800 bytes long. We only need half */
                /* that size for the dirtybuffer */
                if ((dirtybuffer = new char[videoram_size[0] / 2]) == null)
                        return 1;
                memset(dirtybuffer,1,videoram_size[0] / 2);

                if ((tmpbitmap = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
                {
                        dirtybuffer=null;
                        return 1;
                }

                return 0;
        }};



        /***************************************************************************

          Stop the video hardware emulation.

        ***************************************************************************/
        public static VhStopPtr bublbobl_vh_stop = new VhStopPtr() { public void handler()
	{
                osd_free_bitmap(tmpbitmap);
                tmpbitmap=null;
                dirtybuffer=null;
        }};


        public static WriteHandlerPtr bublbobl_videoram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                if (videoram.read(offset) != data)
                {
                        dirtybuffer[offset / 2] = 1;
                        videoram.write(offset, data);
                }
        }};


        public static WriteHandlerPtr bublbobl_objectram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                if (bublbobl_objectram.read(offset) != data)
                {
                        /* gfx bank selector for the background playfield */
                        if ((offset < 0x40) && (offset % 4 == 3)) memset(dirtybuffer,1,videoram_size[0] / 2);
                        bublbobl_objectram.write(offset, data);
                }
        }};


        public static WriteHandlerPtr bublbobl_paletteram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                int bit0,bit1,bit2,bit3;
                int r,g,b,val;


                bublbobl_paletteram.write(offset,data);

                /* red component */
                val = bublbobl_paletteram.read(offset & ~1);
                bit0 = (val >> 4) & 0x01;
                bit1 = (val >> 5) & 0x01;
                bit2 = (val >> 6) & 0x01;
                bit3 = (val >> 7) & 0x01;
                r = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;

                /* green component */
                val = bublbobl_paletteram.read(offset & ~1);
                bit0 = (val >> 0) & 0x01;
                bit1 = (val >> 1) & 0x01;
                bit2 = (val >> 2) & 0x01;
                bit3 = (val >> 3) & 0x01;
                g = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;

                /* blue component */
                val = bublbobl_paletteram.read(offset | 1);
                bit0 = (val >> 4) & 0x01;
                bit1 = (val >> 5) & 0x01;
                bit2 = (val >> 6) & 0x01;
                bit3 = (val >> 7) & 0x01;
                b = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;

                osd_modify_pen(Machine.pens[(offset / 2) ^ 0x0f],r,g,b);
        }};



        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr bublbobl_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
	int offs;
	int sx,sy,xc,yc;
	int gfx_num,gfx_code,gfx_offs;




	/* Bubble Bobble doesn't have a real video RAM. All graphics (characters */
	/* and sprites) are stored in the same memory region, and information on */
	/* the background character columns is stored inthe area dd00-dd3f */
	for (offs = 0;offs < 16;offs++)
	{
		gfx_num = bublbobl_objectram.read(4*offs + 1);

		gfx_code = bublbobl_objectram.read(4*offs + 3);

		sx = offs * 16;
		gfx_offs = ((gfx_num & 0x3f) * 0x80);

		for (xc = 0;xc < 2;xc++)
		{
			for (yc = 0;yc < 32;yc++)
			{
				int goffs,color;


				goffs = gfx_offs + xc * 0x40 + yc * 0x02;
				color = (videoram.read(goffs + 1) & 0x3c) >> 2;
				if ((dirtybuffer[goffs / 2]!=0))
				{
					dirtybuffer[goffs / 2] = 0;

					drawgfx(tmpbitmap,Machine.gfx[0],
							videoram.read(goffs) + 256 * (videoram.read(goffs + 1) & 0x03) + 1024 * (gfx_code & 0x0f),
							color,
							videoram.read(goffs + 1) & 0x40,videoram.read(goffs + 1) & 0x80,
							sx + xc * 8,yc * 8,
							null,TRANSPARENCY_NONE,0);
				}
			}
		}
	}



	/* copy the background graphics to the screen */
	{
		int[] scroll=new int[16];


		for (offs = 0;offs < 16;offs++)
			scroll[offs] = -bublbobl_objectram.read(4*offs);

		copyscrollbitmap(bitmap,tmpbitmap,0,null,16,scroll,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	}


	/* draw the sprites */
	for (offs = 0x40;offs < bublbobl_objectram_size[0];offs += 4)
    {
		int height;


		gfx_num = bublbobl_objectram.read(offs + 1);

		gfx_code = bublbobl_objectram.read(offs + 3);

		sx = bublbobl_objectram.read(offs + 2);
		if ((gfx_num & 0x80) == 0)	/* sprites */
		{
			sy = 256 - 8*8 - (bublbobl_objectram.read(offs + 0));
			gfx_offs = ((gfx_num & 0x1f) * 0x80) + ((gfx_num & 0x60) >> 1);
			height = 8;
		}
		else	/* foreground chars (each "sprite" is one 16x256 column) */
		{
			sy = -(bublbobl_objectram.read(offs + 0));
			gfx_offs = ((gfx_num & 0x3f) * 0x80);
			height = 32;
		}

		for (xc = 0;xc < 2;xc++)
		{
			for (yc = 0;yc < height;yc++)
			{
				int goffs;


				goffs = gfx_offs + xc * 0x40 + yc * 0x02;
				if (videoram.read(goffs)!=0)
				{
					drawgfx(bitmap,Machine.gfx[0],
							videoram.read(goffs) + 256 * (videoram.read(goffs + 1) & 0x03) + 1024 * (gfx_code & 0x0f),
							(videoram.read(goffs + 1) & 0x3c) >> 2,
							videoram.read(goffs + 1) & 0x40,videoram.read(goffs + 1) & 0x80,
							-4*(gfx_code & 0x40) + sx + xc * 8,sy + yc * 8,
							Machine.drv.visible_area,TRANSPARENCY_PEN,0);
				}
			}
		}
	}
            }};
}
