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
import static mame.commonH.*;
import static mame.driverH.*;
import static vidhrdw.generic.*;
import static mame.osdependH.*;
import static mame.mame.*;
import static mame.common.*;

public class naughtyb {
    public static CharPtr naughtyb_videoram2 = new CharPtr();

    /* use these to draw charset B */
    public static CharPtr naughtyb_scrollreg = new CharPtr();

    /* video/control register 1  */
    static char videoctlreg;

    /* use this to select palette */
    static char palreg;

    /* used in Naughty Boy to select video bank */
    static int bankreg;

    static rectangle scrollvisiblearea = new rectangle(0*8, 28*8-1,2*8, 34*8-1);
    static rectangle topvisiblearea = new rectangle(0*8, 28*8-1,0*8, 2*8-1);
    static rectangle bottomvisiblearea = new rectangle(0*8, 28*8-1,34*8, 36*8-1);

        /***************************************************************************

          Start the video hardware emulation.

        ***************************************************************************/
    public static VhStartPtr naughtyb_vh_start = new VhStartPtr() {
      public int handler() {
                palreg    = 0;
                bankreg   = 0;
                videoctlreg = 0;

                /* Naughty Boy has a virtual screen twice as large as the visible screen */
               if ((dirtybuffer = new char[videoram_size[0]]) == null)
                        return 1;
                memset(dirtybuffer,1,videoram_size[0]);

                if ((tmpbitmap = osd_create_bitmap(28*8,68*8)) == null)
                {
                        dirtybuffer=null;
                        return 1;
                }

                return 0;
        }};



        /***************************************************************************

          Stop the video hardware emulation.

        ***************************************************************************/
      public static VhStopPtr naughtyb_vh_stop = new VhStopPtr() {
        public void handler() {
                osd_free_bitmap(tmpbitmap);
                tmpbitmap = null;
                dirtybuffer = null;
        }};



      public static WriteHandlerPtr naughtyb_videoram2_w = new WriteHandlerPtr() {
         public void handler(int offset, int data) {
                if (naughtyb_videoram2.read(offset) != data)
                {
                        dirtybuffer[offset] = 1;

                        naughtyb_videoram2.write(offset,data);
                }
        }};



      public static WriteHandlerPtr naughtyb_videoreg_w = new WriteHandlerPtr()
      {
        public void handler(int offset, int data)
        {
        //        if (videoctlreg != data) {

                   videoctlreg = (char)data;

                   /*   REMEMBER - both bits 1&2 are used to set the pallette
                    *   Don't forget to add in bit 2, which doubles as the bank
                    *   select
                    */

                   palreg  = (char)((videoctlreg >> 1) & 0x01);       /* pallette sel is bit 1 */
                   bankreg = ((videoctlreg >> 2) & 0x01); /* banksel is bit 2      */

        //        }
        }};



        /***************************************************************************

          Draw the game screen in the given osd_bitmap.
          Do NOT call osd_update_display() from this function, it will be called by
          the main emulation engine.

          The Naughty Boy screen is split into two sections by the hardware

          NonScrolled = 28x4 - (rows 0,1,34,35, as shown below)
          this area is split between the top and bottom of the screen,
          and the address mapping is really funky.

          Scrolled = 28x64, with a 28x32 viewport, as shown below
          Each column in the virtual screen is 64 (40h) characters high.
          Thus, column 27 is stored in VRAm at address 0-3fh,
          column 26 is stored at 40-7f, and so on.
          This illustration shows the horizonal scroll register set to zero,
          so the topmost 32 rows of the virtual screen are shown.

          The following screen-to-memory mapping. This is shown from player's viewpoint,
          which with the CRT rotated 90 degrees CCW. This example shows the horizonal
          scroll register set to zero.


                                  COLUMN
                        0   1   2    -    25  26  27
                      -------------------------------
                    0| 76E 76A 762   -   70A 706 702 |
                     |                               |  Nonscrolled display
                    1| 76F 76B 762   -   70B 707 703 |
                     |-------------------------------| -----
                    2| 6C0 680 640   -    80  40  00 |
                     |                               |
                R   3| 6C1 681 641   -    81  41  01 |
                O    |                               |  28 x 32 viewport
                W   ||      |                 |      |  into 28x64 virtual,
                     |                               |  scrollable screen
                   32| 6DE 69E 65E        9E  5E  1E |
                     |                               |
                   33| 6DF 69F 65F   -    9F  5F  1F |
                     |-------------------------------| -----
                   34| 76C 768 764       708 704 700 |
                     |                               |  Nonscrolled display
                   35| 76D 769 765       709 705 701 |
                      -------------------------------


        ***************************************************************************/
        public static VhUpdatePtr naughtyb_vh_screenrefresh = new VhUpdatePtr()
        {
            public void handler(osd_bitmap bitmap)
            {
                int offs;


                /* for every character in the Video RAM, check if it has been modified */
                /* since last time and update it accordingly. */
                for (offs = videoram_size[0] - 1;offs >= 0;offs--)
                {
                        if (dirtybuffer[offs]!=0)
                        {
                                int sx,sy;


                                dirtybuffer[offs] = 0;

                                if (offs < 0x700)
                                {
                                        sx = 27 - offs / 64;
                                        sy = offs % 64;
                                }
                                else
                                {
                                        sx = 27 - (offs - 0x700) / 4;
                                        sy = 64 + (offs - 0x700) % 4;
                                }

                                drawgfx(tmpbitmap,Machine.gfx[1],
                                                naughtyb_videoram2.read(offs) + 256*bankreg,
                                                (naughtyb_videoram2.read(offs) >> 5) + 8*palreg,
                                                0,0,
                                                8*sx,8*sy,
                                                null,TRANSPARENCY_NONE,0);

                                drawgfx(tmpbitmap,Machine.gfx[0],
                                                videoram.read(offs) + 256*bankreg,
                                                (videoram.read(offs) >> 5) + 8*palreg,
                                                0,0,
                                                8*sx,8*sy,
                                                null,TRANSPARENCY_PEN,0);
                        }
                }


                /* copy the temporary bitmap to the screen */
                {
                        int scrolly;


                        copybitmap(bitmap,tmpbitmap,0,0,0,-66*8,topvisiblearea,TRANSPARENCY_NONE,0);
                        copybitmap(bitmap,tmpbitmap,0,0,0,-30*8,bottomvisiblearea,TRANSPARENCY_NONE,0);

                        scrolly = -naughtyb_scrollreg.read() + 16;
                        copyscrollbitmap(bitmap,tmpbitmap,0,null,1,new int[] {scrolly},scrollvisiblearea,TRANSPARENCY_NONE,0);
                }
        }};

}
