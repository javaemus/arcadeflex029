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
/* ported to v0.29
 * ported to v0.28
 * 
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

public class exidy 
{
        public static CharPtr exidy_characterram= new CharPtr();
        public static CharPtr exidy_color_lookup= new CharPtr();

        static  char[] exidy_dirtycharacter=new char[256];

        public static CharPtr exidy_sprite_no= new CharPtr();
        public static CharPtr exidy_sprite_enable= new CharPtr();
        public static CharPtr exidy_sprite1_xpos= new CharPtr();
        public static CharPtr exidy_sprite1_ypos= new CharPtr();
        public static CharPtr exidy_sprite2_xpos= new CharPtr();
        public static CharPtr exidy_sprite2_ypos= new CharPtr();

        static char exidy_last_state=0;

        public static WriteHandlerPtr exidy_characterram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                if (exidy_characterram.read(offset) != data)
                {
                        exidy_dirtycharacter[offset / 8% 256] = 1;

                        exidy_characterram.write(offset,data);
                }
        }};


        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

        ***************************************************************************/
        public static VhUpdatePtr exidy_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                int offs,i;
                char enable_set=0;

                if ((exidy_sprite_enable.read()&0x20)==0x20)
                        enable_set=1;

                if (exidy_last_state!=enable_set)
                {
                        memset(exidy_dirtycharacter,1,sizeof(exidy_dirtycharacter));
                        exidy_last_state=enable_set;
                }


                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        int charcode;


                        charcode = videoram.read(offs);

                        if ((dirtybuffer[offs]!=0) || (exidy_dirtycharacter[charcode]!=0))
                        {
                                int sx,sy;


                        /* decode modified characters */
                                if (exidy_dirtycharacter[charcode] == 1)
                                {
                                        decodechar(Machine.gfx[0],charcode,exidy_characterram,
                                                        Machine.drv.gfxdecodeinfo[0].gfxlayout);
                                        exidy_dirtycharacter[charcode] = 2;
                                }


                                dirtybuffer[offs] = 0;

                                sx = 8 * (offs % 32);
                                sy = 8 * (offs / 32);
                                drawgfx(tmpbitmap,Machine.gfx[0],
                                                charcode,exidy_color_lookup.read(charcode)*2+enable_set,
                                                0,0,sx,sy,
                                                Machine.drv.visible_area,TRANSPARENCY_NONE,0);
                        }
                }


                for (i = 0;i < 256;i++)
                {
                        if (exidy_dirtycharacter[i] == 2)
                                exidy_dirtycharacter[i] = 0;
                }

                /* copy the character mapped graphics */
                copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);


                /* Draw the sprites. */
                {
                        int sx,sy;

                        if (((exidy_sprite_enable.read()&0x80)==0) || ((exidy_sprite_enable.read()&0x10)!=0))
                        {
                                sx = 236-exidy_sprite1_xpos.read()-4;
                                sy = 244-exidy_sprite1_ypos.read()-4;

                                drawgfx(bitmap,Machine.gfx[1],
                                        (exidy_sprite_no.read() & 0x0F)+16*enable_set,0+enable_set,
                                        0,0,
                                        sx,sy,
                                        Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                        }
                        if ((exidy_sprite_enable.read()&0x40)==0)
                        {
                                sx = 236-exidy_sprite2_xpos.read()-4;
                                sy = 244-exidy_sprite2_ypos.read()-4;

                                drawgfx(bitmap,Machine.gfx[1],
                                                ((exidy_sprite_no.read()>>4) & 0x0F)+32,2+enable_set,
                                                0,0,
                                                sx,sy,
                                                Machine.drv.visible_area,TRANSPARENCY_PEN,0);
                        }
                }
        } };   
}
