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

public class warlord 
{
    public static VhConvertColorPromPtr warlord_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
    {
            int i;


            for (i = 0;i < 16; i++)
            {
                    if ((i & 0x08) == 0) /* luminance = 0 */
                    {
                            palette[3*i] = (char)(0xc0 * (i & 1));
                            palette[3*i + 1] = (char)(0xc0 * ((i >> 1) & 1));
                            palette[3*i + 2] = (char)(0xc0 * ((i >> 2) & 1));
                    }
                    else	/* luminance = 1 */
                    {
                            palette[3*i] = (char)(0xff * (i & 1));
                            palette[3*i + 1] = (char)(0xff * ((i >> 1) & 1));
                            palette[3*i + 2] = (char)(0xff * ((i >> 2) & 1));
                    }
            }

            colortable[0]=0;
            colortable[1]=1;
            colortable[2]=2;
            colortable[3]=3;
    }};



    public static VhUpdatePtr warlord_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
    {
            int offs;


            for (offs = videoram_size[0] - 1;offs >= 0;offs--)
            {
                    if (dirtybuffer[offs]!=0)
                    {
                            int sx,sy;

                            dirtybuffer[offs] = 0;

                            sy = (offs / 32);
                            sx = (offs % 32);

                            drawgfx(tmpbitmap,Machine.gfx[0],
                                            videoram.read(offs) & 0x3f , 0 ,
                                            videoram.read(offs) & 0x40 , videoram.read(offs) & 0x80 ,
                                            8*sx,8*sy,
                                            Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                    }
            }


            /* For standup mode use the following line :-
            copybitmap(bitmap,tmpbitmap,1,0,0,0,&Machine.drv.visible_area,TRANSPARENCY_NONE,0);
            */

            /* copy the temporary bitmap to the screen */
            copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);


            /* Draw the sprites */
            for (offs = 0;offs < 0x10;offs++)
            {
                    int spritenum  /*,color*/ ;

                    spritenum = ( spriteram.read(offs) & 0x3f ) + 0x40 ;

                    /*
                    color = spriteram[offs+0x30];
                    Machine.gfx[0].colortable[3] =
                                    Machine.pens[15 - centiped_spritepalette[(color >> 4) & 3]];
                    Machine.gfx[0].colortable[2] =
                                    Machine.pens[15 - centiped_spritepalette[(color >> 2) & 3]];
                    Machine.gfx[0].colortable[1] =
                                    Machine.pens[15 - centiped_spritepalette[(color >> 0) & 3]];
                    */
                    drawgfx(bitmap,Machine.gfx[0],
                                    spritenum,0,
                                    spriteram.read(offs) & 0x40 , spriteram.read(offs) & 0x80 ,
                                    spriteram.read(offs + 0x20),248 - spriteram.read(offs + 0x10),
                                    Machine.drv.visible_area,TRANSPARENCY_PEN,0);
            }
    }};
   
}
