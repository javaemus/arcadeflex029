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

public class mappy {
        static int mappy_scroll;
        public static VhConvertColorPromPtr  mappy_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
                int i;

                for (i = 0;i < 32;i++)
                {
                        int bit0,bit1,bit2;

                        bit0 = (color_prom[31-i] >> 0) & 0x01;
                        bit1 = (color_prom[31-i] >> 1) & 0x01;
                        bit2 = (color_prom[31-i] >> 2) & 0x01;
                        palette[3*i] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                        bit0 = (color_prom[31-i] >> 3) & 0x01;
                        bit1 = (color_prom[31-i] >> 4) & 0x01;
                        bit2 = (color_prom[31-i] >> 5) & 0x01;
                        palette[3*i + 1] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                        bit0 = 0;
                        bit1 = (color_prom[31-i] >> 6) & 0x01;
                        bit2 = (color_prom[31-i] >> 7) & 0x01;
                        palette[3*i + 2] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                }

                /* characters */
                for (i = 0*4;i < 64*4;i++)
                        colortable[i] = (char)(31 - ((color_prom[(i^3) + 32] & 0x0f) + 0x10));

                /* sprites */
                for (i = 64*4;i < 128*4;i++)
                        colortable[i] = (char)(31 - (color_prom[i + 32] & 0x0f));
        }};
        public static VhStartPtr mappy_vh_start = new VhStartPtr() { public int handler()
	{
            	if ((dirtybuffer = new char[videoram_size[0]]) == null)
		{
			return 1;
		}
                memset(dirtybuffer,1,videoram_size[0]);

                if ((tmpbitmap = osd_create_bitmap(60*8,36*8)) == null)
                {
                        dirtybuffer=null;
                        return 1;
                }

                return 0;
        }};

	public static VhStopPtr mappy_vh_stop = new VhStopPtr() { public void handler()
	{
                dirtybuffer=null;
                osd_free_bitmap(tmpbitmap);
                tmpbitmap=null;
        }};

        public static WriteHandlerPtr mappy_videoram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                if (videoram.read(offset) != data)
                {
                        dirtybuffer[offset] = 1;
                        videoram.write(offset, data);
                }
        }};

        public static WriteHandlerPtr mappy_colorram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                if (colorram.read(offset) != data)
                {
                        dirtybuffer[offset] = 1;
                        colorram.write(offset, data);
                }
        }};

        public static WriteHandlerPtr mappy_scroll_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{

                mappy_scroll = offset >> 3;
        }};
        
        

       static void mappy_draw_sprite(osd_bitmap dest, int code, int color, int flipx, int flipy, int sx, int sy)
       {
           drawgfx(dest,Machine.gfx[1],code,color,flipx,flipy,sx,sy,Machine.drv.visible_area,
                        TRANSPARENCY_COLOR,16);
       }

        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        static char overoffset[]=new char[2048];
        public static VhUpdatePtr mappy_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                //static unsigned short overoffset[2048];
               // unsigned short *save = overoffset;
                CharPtr save = new CharPtr(overoffset);
                int offs;

                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        /* characters with bit 0x40 set are higher priority than sprites; remember and redraw later */
                        if ((colorram.read(offs) & 0x40)!=0)
                        {
                            save.writeinc(offs);
                           overoffset[save.base]=(char)offs;//TODO : shadow this is probably crappy...
                                //*save++ = offs;
                        }
                            

                        if (dirtybuffer[offs]!=0)
                        {
                                int sx,sy,mx,my;


                                dirtybuffer[offs] = 0;

                                if (offs >= videoram_size[0] - 64)
                                {
                                        /* Draw the top 2 lines. */
                                        mx = offs % 32;
                                        my = (offs - (videoram_size[0] - 64)) / 32;

                                        sx = 29 - mx;
                                        sy = my;
                                }
                                else if (offs >= videoram_size[0] - 128)
                                {
                                        /* Draw the bottom 2 lines. */
                                        mx = offs % 32;
                                        my = (offs - (videoram_size[0] - 128)) / 32;

                                        sx = 29 - mx;
                                        sy = my + 34;
                                }
                                else
                                {
                                        /* draw the rest of the screen */
                                        mx = offs / 32;
                                        my = offs % 32;

                                        sx = 59 - mx;
                                        sy = my + 2;
                                }

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                                videoram.read(offs),
                                                colorram.read(offs),
                                                0,0,8*sx,8*sy,
                                                null,TRANSPARENCY_NONE,0);
                        }
                }


                /* copy the temporary bitmap to the screen */
                 {
                        int scroll[]=new int[36];


                        for (offs = 0;offs < 2;offs++)
                                scroll[offs] = 0;
                        for (offs = 2;offs < 34;offs++)
                                scroll[offs] = mappy_scroll - 255;
                        for (offs = 34;offs < 36;offs++)
                                scroll[offs] = 0;

                        copyscrollbitmap(bitmap,tmpbitmap,36,scroll,0,null,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                }


                /* Draw the sprites. */
                for (offs = 0;offs < spriteram_size[0];offs += 2)
                {
                        /* is it on? */
                        if ((spriteram_3.read(offs+1) & 2) == 0)
                        {
                                int sprite = spriteram.read(offs);
                                int color = spriteram.read(offs+1);
                                int x = spriteram_2.read(offs)-16;
                                int y = (spriteram_2.read(offs+1)-40) + 0x100*(spriteram_3.read(offs+1) & 1);
                                int flipx = spriteram_3.read(offs) & 2;
                                int flipy = spriteram_3.read(offs) & 1;

                                switch (spriteram_3.read(offs) & 0x0c)
                                {
                                        case 0:		/* normal size */
                                                mappy_draw_sprite(bitmap,sprite,color,flipx,flipy,x,y);
                                                break;

                                        case 4:		/* 2x vertical */
                                                sprite &= ~1;
                                                if (flipy==0)
                                                {
                                                        mappy_draw_sprite(bitmap,sprite,color,flipx,flipy,x,y);
                                                        mappy_draw_sprite(bitmap,1+sprite,color,flipx,flipy,x,16+y);
                                                }
                                                else
                                                {
                                                        mappy_draw_sprite(bitmap,sprite,color,flipx,flipy,x,16+y);
                                                        mappy_draw_sprite(bitmap,1+sprite,color,flipx,flipy,x,y);
                                                }
                                                break;

                                        case 8:		/* 2x horizontal */
                                                sprite &= ~2;
                                                if (flipx==0)
                                                {
                                                        mappy_draw_sprite(bitmap,2+sprite,color,flipx,flipy,x,y);
                                                        mappy_draw_sprite(bitmap,sprite,color,flipx,flipy,16+x,y);
                                                }
                                                else
                                                {
                                                        mappy_draw_sprite(bitmap,sprite,color,flipx,flipy,x,y);
                                                        mappy_draw_sprite(bitmap,2+sprite,color,flipx,flipy,16+x,y);
                                                }
                                                break;

                                        case 12:		/* 2x both ways */
                                                sprite &= ~3;
                                                if ((flipy==0) && (flipx==0))
                                                {
                                                        mappy_draw_sprite(bitmap,2+sprite,color,flipx,flipy,x,y);
                                                        mappy_draw_sprite(bitmap,3+sprite,color,flipx,flipy,x,16+y);
                                                        mappy_draw_sprite(bitmap,sprite,color,flipx,flipy,16+x,y);
                                                        mappy_draw_sprite(bitmap,1+sprite,color,flipx,flipy,16+x,16+y);
                                                }
                                                else if ((flipy!=0) && (flipx!=0))
                                                {
                                                        mappy_draw_sprite(bitmap,1+sprite,color,flipx,flipy,x,y);
                                                        mappy_draw_sprite(bitmap,sprite,color,flipx,flipy,x,16+y);
                                                        mappy_draw_sprite(bitmap,3+sprite,color,flipx,flipy,16+x,y);
                                                        mappy_draw_sprite(bitmap,2+sprite,color,flipx,flipy,16+x,16+y);
                                                }
                                                else if (flipx!=0)
                                                {
                                                        mappy_draw_sprite(bitmap,sprite,color,flipx,flipy,x,y);
                                                        mappy_draw_sprite(bitmap,1+sprite,color,flipx,flipy,x,16+y);
                                                        mappy_draw_sprite(bitmap,2+sprite,color,flipx,flipy,16+x,y);
                                                        mappy_draw_sprite(bitmap,3+sprite,color,flipx,flipy,16+x,16+y);
                                                }
                                                else /* flipy */
                                                {
                                                        mappy_draw_sprite(bitmap,3+sprite,color,flipx,flipy,x,y);
                                                        mappy_draw_sprite(bitmap,2+sprite,color,flipx,flipy,x,16+y);
                                                        mappy_draw_sprite(bitmap,1+sprite,color,flipx,flipy,16+x,y);
                                                        mappy_draw_sprite(bitmap,sprite,color,flipx,flipy,16+x,16+y);
                                                }
                                                break;
                                }
                        }
                }

                /* Draw the high priority characters */
                while (save.base>overoffset.length)//TODO: shadow this is probably crappy...
                {
                        //System.out.println(save.read());
                        int sx,sy,mx,my;
                        offs = save.readdec();

                        if (offs >= videoram_size[0] - 64)
                        {
                                /* Draw the top 2 lines. */
                                mx = offs % 32;
                                my = (offs - (videoram_size[0] - 64)) / 32;

                                sx = 29 - mx;
                                sy = my;

                                sx *= 8;
                        }
                        else if (offs >= videoram_size[0] - 128)
                        {
                                /* Draw the bottom 2 lines. */
                                mx = offs % 32;
                                my = (offs - (videoram_size[0] - 128)) / 32;

                                sx = 29 - mx;
                                sy = my + 34;

                                sx *= 8;
                        }
                        else
                        {
                                /* draw the rest of the screen */
                                mx = offs / 32;
                                my = offs % 32;

                                sx = 59 - mx;
                                sy = my + 2;

                                sx = (8*sx+mappy_scroll-255);
                        }

                        drawgfx(bitmap,Machine.gfx[0],
                                        videoram.read(offs),
                                        colorram.read(offs),
                                        0,0,sx,8*sy,
                                        null,TRANSPARENCY_COLOR,0);
               }
        } };  
}
