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

import static arcadeflex.libc.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static Z80.Z80H.*;

public class docastle
{
        static char[] buffer0 = new char[9]; 
        static char[] buffer1 = new char[9];
        static int nmi;


        public static ReadHandlerPtr docastle_shared0_r = new ReadHandlerPtr() {
            public int handler(int offset) {
             if ((errorlog != null) && (offset == 8)) fprintf(errorlog, "shared0r\n", new Object[0]);
                return docastle.buffer0[offset];
            }
        };

        public static ReadHandlerPtr docastle_shared1_r = new ReadHandlerPtr() {
            public int handler(int offset) {
             if ((errorlog != null) && (offset == 8)) fprintf(errorlog, "shared1r\n", new Object[0]);
                return docastle.buffer1[offset];
            }
        };

         public static WriteHandlerPtr docastle_shared0_w = new WriteHandlerPtr() {
             public void handler(int offset,int data) {
                 if ((errorlog != null) && (offset == 8)) fprintf(errorlog, "shared0w %02x %02x %02x %02x %02x %02x %02x %02x %02x\n", 
                           new Object[] { Character.valueOf(docastle.buffer0[0]), Character.valueOf(docastle.buffer0[1]), Character.valueOf(docastle.buffer0[2]), Character.valueOf(docastle.buffer0[3]), Character.valueOf(docastle.buffer0[4]), Character.valueOf(docastle.buffer0[5]), Character.valueOf(docastle.buffer0[6]), Character.valueOf(docastle.buffer0[7]), Integer.valueOf(data) });

                docastle.buffer0[offset] = (char)data;
            }
        };

          public static WriteHandlerPtr docastle_shared1_w = new WriteHandlerPtr() {
            public void handler(int offset,int data) {
              if ((errorlog != null) && (offset == 8)) fprintf(errorlog, "shared1w %02x %02x %02x %02x %02x %02x %02x %02x %02x\n", new Object[] { Character.valueOf(docastle.buffer1[0]), Character.valueOf(docastle.buffer1[1]), Character.valueOf(docastle.buffer1[2]), Character.valueOf(docastle.buffer1[3]), Character.valueOf(docastle.buffer1[4]), Character.valueOf(docastle.buffer1[5]), Character.valueOf(docastle.buffer1[6]), Character.valueOf(docastle.buffer1[7]), Integer.valueOf(data) });

              docastle.buffer1[offset] = (char)data;
                /* To prevent a "bad communication" error with Mr. Do's Wild Ride, prepare a valid */
                /* response. This hack is necessary because MAME currently doesn't interleave the */
                /* execution of the two CPUs, so the first CPU tries to read the reply from the second */
                /* before the second has had a chance to run. */
              if (offset == 8) if (memcmp(docastle.buffer1, new char[] { 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04 }, 9) == 0) {
                  memcpy(docastle.buffer0, new char[] { 0x20, 0x11, 0x11, 0x00, 0x00, 0x00, 0x00, 0x00, 0x42 }, 9);
                }

                /* Horrible kludge to make Do's Castle read the dip switch settings during boot. */
                /* Again, this is necessary because of the limited control MAME has on multiple CPUs. */
              if (offset == 8) if (memcmp(docastle.buffer1, new char[] { 0x44, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x44 }, 9) == 0)
                  cpu_seticount(3000);
            }
          };



        public static WriteHandlerPtr docastle_nmitrigger = new WriteHandlerPtr() {
            public void handler(int offset,int data) {
              nmi++;
          }
        };
        
        static int count;
          
        public static InterruptPtr docastle_interrupt2 = new InterruptPtr()
          {
            public int handler()
            {
              count++;
              if ((count & 0x1) != 0)
              {
                if (nmi != 0)
                {
                  nmi -= 1;
                  return Z80_NMI_INT;
                }
                else return Z80_IGNORE_INT;
              }
              else return 0xff;
            }
          };

}	