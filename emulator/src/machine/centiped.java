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

public class centiped 
{
       public static ReadHandlerPtr centiped_IN0_r = new ReadHandlerPtr(){
         public int handler(int offset) 
         {
                int res;
                int trak;

                res = readinputport(0);
                trak = readtrakport(0);

                return(res|trak);
        }};
          static char x1 = 0;
          static int res1 = 0;
       public static ConversionPtr centiped_trakball_x = new ConversionPtr() {
         public int handler(int data) 
         {
          if(data > 7) {
            data = 7;
          }

          if(data < -7) {
            data = -7;
          }

          x1 -= (char)data;

          if(x1<0x00) {
            x1 += 0x10;
          }

          if(x1>0x10) {
            x1 -= 0x10;
          }

          if(data < 0) {
            res1 = x1;
          }

          if(data > 0) {
            res1 = 0x80|x1;
          }

          return(res1);
        }};
          static char y1 = 0;
          static int res2 = 0;
        public static ConversionPtr centiped_trakball_y = new ConversionPtr() {
         public int handler(int data) 
         {
          data = -data;

          if(data > 7) {
            data = 7;
          }

          if(data < -7) {
            data = -7;
          }

          y1 -= (char)data;

          if(y1<0x00) {
            y1 += 0x10;
          }

          if(y1>0x10) {
            y1 -= 0x10;
          }

          if(data < 0) {
            res2 = y1;
          }

          if(data > 0) {
            res2 = 0x80|y1;
          }

          return(res2);
        }};
}
