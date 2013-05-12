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

public class qix {

    public static CharPtr qix_paletteram = new CharPtr();
    public static CharPtr qix_palettebank = new CharPtr();
    public static CharPtr qix_videoaddress = new CharPtr();
    static int dirtypalette;       
    static char[] qixpal = new char[256];
    static char[] screen;
    
                        /* this conversion table is probably quite wrong, but at least it gives */
                        /* a reasonable gray scale in the test screen. However, in the very same */
                        /* test screen the red, green and blue squares are almost invisible since */
                        /* they are very dark (value = 1, intensity = 0) */
                        static char table[] =
                        {
                                0x00,	/* value = 0, intensity = 0 */
                                0x12,	/* value = 0, intensity = 1 */
                                0x24,	/* value = 0, intensity = 2 */
                                0x49,	/* value = 0, intensity = 3 */
                                0x12,	/* value = 1, intensity = 0 */
                                0x24,	/* value = 1, intensity = 1 */
                                0x49,	/* value = 1, intensity = 2 */
                                0x92,	/* value = 1, intensity = 3 */
                                0x5b,	/* value = 2, intensity = 0 */
                                0x6d,	/* value = 2, intensity = 1 */
                                0x92,	/* value = 2, intensity = 2 */
                                0xdb,	/* value = 2, intensity = 3 */
                                0x7f,	/* value = 3, intensity = 0 */
                                0x91,	/* value = 3, intensity = 1 */
                                0xb6,	/* value = 3, intensity = 2 */
                                0xff	/* value = 3, intensity = 3 */
                        };
        public static VhConvertColorPromPtr qix_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
	{
                int i;


                for (i = 0;i < 256;i++)
                {

                        int bits,intensity;
                        intensity = (i >> 0) & 0x03;
                        bits = (i >> 6) & 0x03;
                        palette[3*i] = table[(bits << 2) | intensity];
                        bits = (i >> 4) & 0x03;
                        palette[3*i + 1] = table[(bits << 2) | intensity];
                        bits = (i >> 2) & 0x03;
                        palette[3*i + 2] = table[(bits << 2) | intensity];
                }
        }};
        



        /***************************************************************************

          Start the video hardware emulation.

        ***************************************************************************/
        public static VhStartPtr qix_vh_start = new VhStartPtr() { public int handler()
	{
                dirtypalette = 1;

                if ((screen = new char[256*256]) == null)
                        return 1;

                if ((tmpbitmap = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
                {
                        screen=null;
                        return 1;
                }

                return 0;
        }};



        /***************************************************************************

          Stop the video hardware emulation.

        ***************************************************************************/
        public static VhStopPtr qix_vh_stop = new VhStopPtr() { public void handler()
	{
                osd_free_bitmap(tmpbitmap);
                tmpbitmap=null;
                screen=null;
        }};



        /* The screen is 256x256 with eight bit pixels (64K).  The screen is divided
        into two halves each half mapped by the video CPU at $0000-$7FFF.  The
        high order bit of the address latch at $9402 specifies which half of the
        screen is being accessed.

        The address latch works as follows.  When the video CPU accesses $9400,
        the screen address is computed by using the values at $9402 (high byte)
        and $9403 (low byte) to get a value between $0000-$FFFF.  The value at
        that location is either returned or written. */
    public static ReadHandlerPtr qix_videoram_r = new ReadHandlerPtr() {
        public int handler(int offset) 
        {
                offset += (qix_videoaddress.read(0) & 0x80) * 0x100;
                return screen[offset];
        }};
        public static WriteHandlerPtr qix_videoram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                int x, y;


                offset += (qix_videoaddress.read(0) & 0x80) * 0x100;

                /* bitmap is rotated -90 deg. */
                x = offset >> 8;
                y = ~offset & 0xff;
                tmpbitmap.line[y][x] = qixpal[data];

                screen[offset] = (char)data;
        }};


    public static ReadHandlerPtr qix_addresslatch_r = new ReadHandlerPtr() {
        public int handler(int offset) 
        {
                offset = qix_videoaddress.read(0) * 0x100 + qix_videoaddress.read(1);
                return screen[offset];
        }};


        public static WriteHandlerPtr qix_addresslatch_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                int x, y;


                offset = qix_videoaddress.read(0) * 0x100 + qix_videoaddress.read(1);

                /* bitmap is rotated -90 deg. */
                x = offset >> 8;
                y = ~offset & 0xff;
                tmpbitmap.line[y][x] = qixpal[data];

                screen[offset] = (char)data;
        }};



        /* The color RAM works as follows.  The color RAM contains palette values for
        four pages (0-3).  When a write to $8800 on the video CPU occurs, the color
        RAM page is taken from the lowest 2 bits of the value.  This selects one of
        the color RAM pages as follows:

             colorRAMAddr = 0x9000 + ((data & 0x03) * 0x100);

        Qix uses a palette of 64 colors (2 each RGB) and four intensities (RRGGBBII).
        */
        public static WriteHandlerPtr qix_paletteram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                if (qix_paletteram.read(offset) != data)
                {
                        dirtypalette = 1;
                        qix_paletteram.write(offset, data);
                }
        }};


        public static WriteHandlerPtr qix_palettebank_w = new WriteHandlerPtr() { public void handler(int offset,int data)
	{
                if ((qix_palettebank.read() & 0x03) != (data & 0x03))
                        dirtypalette = 1;

                qix_palettebank.write(data);
        }};


        public static VhUpdatePtr qix_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
	{
                if (dirtypalette!=0)
                {
                        int i,x;
                        CharPtr bm=new CharPtr();
                        CharPtr pram;//=new CharPtr();
                        
                        dirtypalette = 0;

                        //pram = &qix_paletteram[256 * (*qix_palettebank & 0x03)];
                        //pram.set(qix_paletteram.read(256 * (qix_palettebank.read() & 0x03)),0);
                        pram = new CharPtr(qix_paletteram,256 * (qix_palettebank.read() & 0x03));

                        for (i = 0;i < 256;i++)
                                qixpal[i] = Machine.pens[pram.readinc()];


                        /* refresh the bitmap with new colors */
                        for (i = 0;i < 256;i++)
                        {
                                bm.set(tmpbitmap.line[i],0);
                                for (x = 0;x < 256;x++)
                                        bm.writeinc(qixpal[screen[(x << 8) + (255 - i)]]);      
                                       // *bm++ = qixpal[screen[(x << 8) + (255 - i)]];
                        }
                }


                /* copy the screen */
                copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.drv.visible_area,TRANSPARENCY_NONE,0);
        } };  
}
