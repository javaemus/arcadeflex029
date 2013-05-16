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

public class spacefb {
    public static int spacefb_vref = 0;
    
        /***************************************************************************

          Convert the color PROMs into a more useable format.

          Space FB has one 32 bytes palette PROM, connected to the RGB output this
          way:

          bit 7 -- 220 ohm resistor  -- BLUE
                -- 470 ohm resistor  -- BLUE
                -- 220 ohm resistor  -- GREEN
                -- 470 ohm resistor  -- GREEN
                -- 1  kohm resistor  -- GREEN
                -- 220 ohm resistor  -- RED
                -- 470 ohm resistor  -- RED
          bit 0 -- 1  kohm resistor  -- RED

        ***************************************************************************/
        public static VhConvertColorPromPtr spacefb_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
                int i;

               for (i = 0;i < 32;i++)
                {
                        int bit0,bit1,bit2;

                        bit0 = (color_prom[i] >> 0) & 0x01;
                        bit1 = (color_prom[i] >> 1) & 0x01;
                        bit2 = (color_prom[i] >> 2) & 0x01;
                        palette[3*i + 0] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                        bit0 = (color_prom[i] >> 3) & 0x01;
                        bit1 = (color_prom[i] >> 4) & 0x01;
                        bit2 = (color_prom[i] >> 5) & 0x01;
                        palette[3*i + 1] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                        bit0 = 0;
                        bit1 = (color_prom[i] >> 6) & 0x01;
                        bit2 = (color_prom[i] >> 7) & 0x01;
                        if ((bit1 | bit2)!=0)
                                bit0 = 1;
                        palette[3*i + 2] = (char)(0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
                }

                for (i = 0;i < 4 * 8;i++)
                {
                        if ((i & 3)!=0) colortable[i] = (char)(i);
                        else colortable[i] = 0;
                }
        }};



        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr spacefb_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                int offs;
                int spriteno;



                /* Clear the bitmap */
                osd_clearbitmap(bitmap);

                /* Draw the sprite/chars */
                for (offs = 0,spriteno = spacefb_vref;offs < 128;offs++,spriteno++)
                {
                        int h,v,chr,cnt;
                        h = videoram.read(spriteno);
                        v = videoram.read(spriteno+0x100);
                        
                        h = ((255-h)*256)/260;
                        v = 255-v;
                        
                        chr = videoram.read(spriteno+0x200);
                        cnt = videoram.read(spriteno+0x300);

                        if (cnt!=0) {
                                if ((cnt & 0x20)!=0) {
                                /* Draw bullets */
                                        int charnum = chr & 63;
                                        drawgfx(bitmap,Machine.gfx[1],
                                                        charnum,
                                                        2,
                                                        0,0,v,h,
                                                        null,TRANSPARENCY_PEN,0);

                                } else if ((cnt & 0x40)!=0) {
                                        int charnum = 255-chr;
                                        int pal = 7-(cnt & 0x7);

                                        drawgfx(bitmap,Machine.gfx[0],
                                                        charnum,
                                                        pal,
                                                        0,0,v,h,
                                                        null,TRANSPARENCY_NONE,0);
                                }
                        }
                }

        }};

}
