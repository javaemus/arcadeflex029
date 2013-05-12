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

package machine;

import static arcadeflex.osdepend.*;
import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static mame.cpuintrf.*;
import static mame.mame.*;
import static mame.inptport.*;

public class ccastles {
        static int bankaddress;

        public static ConversionPtr ccastles_trakball_x = new ConversionPtr() {
         public int handler(int data) 
         {
          return data;
         }};
        public static ConversionPtr ccastles_trakball_y = new ConversionPtr() {
         public int handler(int data) 
         {
          return 0xFF-data;
         }};
        static int x;
        static int y;
        public static ReadHandlerPtr ccastles_trakball_r = new ReadHandlerPtr(){
         public int handler(int offset) 
         {
          int trak_x, trak_y;

          switch(offset) {
          case 0x00:
            if(osd_key_pressed(OSD_KEY_DOWN) /*|| osd_joy_pressed(OSD_JOY_DOWN)*/) {
              x-=2;
              y-=4;
            }
            if(osd_key_pressed(OSD_KEY_UP) /*|| osd_joy_pressed(OSD_JOY_UP)*/) {
              x+=2;
              y+=4;
            }

            trak_y = input_trak_0_r.handler(0);

            if(trak_y == NO_TRAK) {
              RAM[0x9500] = (char)(y & 0xFF);
              return(y & 0xFF);
            } else {
              RAM[0x9500] = (char)trak_y;
              return(trak_y);
            }
            //break;
          case 0x01:
            if(osd_key_pressed(OSD_KEY_LEFT) /*|| osd_joy_pressed(OSD_JOY_LEFT)*/) {
              x-=4;
              y+=2;
            }
            if(osd_key_pressed(OSD_KEY_RIGHT) /*|| osd_joy_pressed(OSD_JOY_RIGHT)*/) {
              x+=4;
              y-=2;
            }

            trak_x = input_trak_1_r.handler(0);

            if(trak_x == NO_TRAK) {
              RAM[0x9501] = (char)(x & 0xFF);
              return(x & 0xFF);
            } else {
              RAM[0x9501] = (char)trak_x;
              return(trak_x);
            }

          //  break;
          }

          return(0);
        }};
        public static ReadHandlerPtr ccastles_rom_r = new ReadHandlerPtr() {
         public int handler(int offset) 
         {
          return(RAM[bankaddress+offset]);
        }};

        public static ReadHandlerPtr ccastles_random_r = new ReadHandlerPtr() {
         public int handler(int offset) 
         {       
          return rand() & 0xFF;
        }};

        public static WriteHandlerPtr ccastles_bankswitch_w = new WriteHandlerPtr() { public void handler(int offset, int data) { 
          if(data!=0) {
            bankaddress = 0x10000;
          } else {
            bankaddress = 0xA000;
          }
        }};

}
