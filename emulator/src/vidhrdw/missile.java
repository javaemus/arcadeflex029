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
 *ported to v0.28
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

public class missile {
    public static char[] missile_videoram;



    /***************************************************************************

      Start the video hardware emulation.

    ***************************************************************************/
    public static VhStartPtr missile_vh_start = new VhStartPtr() { public int handler()
    {

    /* 	force video ram to be $0000-$FFFF even though only $1900-$FFFF is used */
            if ((missile_videoram = new char[256 * 256]) == null)
                    return 1;

            memset(missile_videoram, 0, 256 * 256);

            return 0;
    }};



    /***************************************************************************

      Stop the video hardware emulation.

    ***************************************************************************/
    public static VhStopPtr missile_vh_stop = new VhStopPtr() { public void handler()
    {
            missile_videoram=null;
    }};




    /********************************************************************************************/
    public static ReadHandlerPtr missile_video_r = new ReadHandlerPtr() {
         public int handler(int offset)
    {
            return(missile_videoram[offset] & 0xE0);
    }};



    /********************************************************************************************/
    public static WriteHandlerPtr missile_video_w = new WriteHandlerPtr() { public void handler(int address, int data) {
            int wbyte, wbit;


      if(address < 0xF800)
                    missile_videoram[address] = (char)data;
            else{
                    missile_videoram[address] = (char)((missile_videoram[address] & 0x20) | data);
                    wbyte = ((address - 0xF800) >> 2) & 0xFFFE;
                    wbit = (address - 0xF800) % 8;
                    if((data & 0x20)!=0)
                            RAM[0x401 + wbyte] |= (1 << wbit);
                    else
                            RAM[0x401 + wbyte] &= ((1 << wbit) ^ 0xFF);
            }
    }};






    /********************************************************************************************/
    public static WriteHandlerPtr missile_video_mult_w = new WriteHandlerPtr() { public void handler(int address, int data) {
    
            data = (data & 0x80) + ((data & 8) << 3);
            address = address << 2;
            if(address >= 0xF800)
                    data |= 0x20;
      missile_videoram[address]     = (char)data;
      missile_videoram[address + 1] = (char)data;
      missile_videoram[address + 2] = (char)data;
      missile_videoram[address + 3] = (char)data;
    }};



    /********************************************************************************************/
    public static WriteHandlerPtr missile_video_3rd_bit_w = new WriteHandlerPtr() { public void handler(int address, int data) {
            int i;

            RAM[address] = (char)data;

            address = ((address - 0x401) << 2) + 0xF800;
            for(i=0;i<8;i++){
                    if((data!=0) & ((1 << i)!=0))
                            missile_videoram[address + i] |= 0x20;
                    else
                            missile_videoram[address + i] &= 0xC0;
            }
    }};




    /********************************************************************************************/
    public static VhUpdatePtr missile_vh_update = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
    {
            int x, y;
            int address;

            for(address = 0x1900;address <= 0xFFFF;address++){
                    y = (address >> 8) - 25;
                    x = address & 0xFF;
                    if(y < 231 - 32)
                            bitmap.line[y][x] = Machine.pens[RAM[0x4B00 + ((missile_videoram[address] >> 5) & 6)] + 1];
                    else
                            bitmap.line[y][x] = Machine.pens[RAM[0x4B00 + (missile_videoram[address] >> 5)] + 1];
            }
    }};
    
}
