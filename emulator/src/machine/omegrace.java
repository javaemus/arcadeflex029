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
 * 
 *
 *
 *
 */

package machine;
import static vidhrdw.vectorH.*;
import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.cpuintrf.*;
import static mame.mame.*;
import static vidhrdw.vector.*;
import static mame.inptport.*;
import static mame.osdependH.*;


public class omegrace {
        public static VhStartPtr omegrace_vg_start = new VhStartPtr() { public int handler()
        {
                if ((vg_init(0x2000, USE_DVG,0))!=0)
                        return 1;
                return 0;
        }};
        
        public static ReadHandlerPtr  omegrace_vg_go= new ReadHandlerPtr() { public int handler(int offset)
        {
                vg_go(cpu_gettotalcycles()>>1);
                return 0;
        }};
        public static ReadHandlerPtr  omegrace_watchdog_r= new ReadHandlerPtr() { public int handler(int offset)
        {
                return 0;
        }};
        public static ReadHandlerPtr  omegrace_vg_status_r= new ReadHandlerPtr() { public int handler(int offset)
        {
        /*	if (errorlog) fprintf(errorlog,"reading vg_halt\n");*/
                if ((vg_done(cpu_gettotalcycles()>>1))!=0)
                        return 0;
                else 
                        return 0x80;
        }};

        /*
         * Encoder bit mappings
         * The encoder is a 64 way switch, with the inputs scrambled
         * on the input port (and shifted 2 bits to the left for the
         * 1 player encoder
         *
         * 3 6 5 4 7 2 for encoder 1 (shifted two bits left..)
         *
         *
         * 5 4 3 2 1 0 for encoder 2 (not shifted..)
         */

        static char spinnerTable[] = {
                0x00, 0x04, 0x14, 0x10, 0x18, 0x1c, 0x5c, 0x58,
                0x50, 0x54, 0x44, 0x40, 0x48, 0x4c, 0x6c, 0x68,
                0x60, 0x64, 0x74, 0x70, 0x78, 0x7c, 0xfc, 0xf8,
                0xf0, 0xf4, 0xe4, 0xe0, 0xe8, 0xec, 0xcc, 0xc8,
                0xc0, 0xc4, 0xd4, 0xd0, 0xd8, 0xdc, 0x9c, 0x98,
                0x90, 0x94, 0x84, 0x80, 0x88, 0x8c, 0xac, 0xa8,
                0xa0, 0xa4, 0xb4, 0xb0, 0xb8, 0xbc, 0x3c, 0x38,
                0x30, 0x34, 0x24, 0x20, 0x28, 0x2c, 0x0c, 0x08 };

        static final int SPINNER_SENSITY= 5; 
        static int spinner=0;
                static int spintime=SPINNER_SENSITY;
        public static ReadHandlerPtr  omegrace_spinner1_r= new ReadHandlerPtr() { public int handler(int offset)
        {
                
                int res;

                res=readtrakport(0);
                if (res != NO_TRAK) spinner+=res;

                if ((spintime--) == 0) {
                        res=readinputport(4);
                        if ((res & 0x01)!=0)
                                spinner--;
                        if ((res & 0x02)!=0)
                                spinner++;
                        spintime=SPINNER_SENSITY;
                }
                return (spinnerTable[spinner & 0x3f]);
        }};

        public static ReadHandlerPtr  omegrace_spinner2_r= new ReadHandlerPtr() { public int handler(int offset)
        {
                if (errorlog!=null) fprintf(errorlog,"reading spinner 2\n");
                return (0);
        }};
    
}
