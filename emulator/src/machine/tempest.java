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

import static mame.driverH.*;
import static mame.cpuintrf.*;
import static mame.mame.*;
import static vidhrdw.vector.*;
import static mame.osdependH.*;
import static mame.inptport.*;

public class tempest {
        static final int IN0_3KHZ= (1<<7);
        static final int IN0_VG_HALT= (1<<6);

        /* hack to avoid lockup at 150,000 points */
        public static ReadHandlerPtr tempest_freakout = new ReadHandlerPtr() { public int handler(int offset)
        {
                return (0);
        }};

        public static ReadHandlerPtr tempest_IN0_r = new ReadHandlerPtr() { public int handler(int offset)
        {
                int res;

                res = readinputport(0);

                /* Emulate the 3Khz source on bit 7 (divide 1.5Mhz by 512) */
                if ((cpu_geticount() & 0x100)!=0) {
                        res &=~IN0_3KHZ;
                } else {
                        res|=IN0_3KHZ;
                }

                if ((vg_done(cpu_gettotalcycles()))!=0)
                        res |=IN0_VG_HALT;
                else
                        res &=~IN0_VG_HALT;
                return (res);
        }};
         static int spinner = 0;
        public static ReadHandlerPtr tempest_IN1_r = new ReadHandlerPtr() { public int handler(int offset)
        {
               
                int res, trak_in;

                res = readinputport(1);
                if ((res & 1)!=0) 
                        spinner--;
                if ((res & 2)!=0)
                        spinner++;
                trak_in = readtrakport(0);
                if (trak_in != NO_TRAK)
                        spinner += trak_in;
                spinner &= 0x0f;	
                return ((res & 0xf0) | spinner);
        }};
        public static ConversionPtr tempest_spinner = new ConversionPtr() {
         public int handler(int data) 
         {
                if (data>7)
                        data=7;
                if (data<-7)
                        data=-7;
                return (data);
        }};	
}
