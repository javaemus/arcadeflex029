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
import static mame.osdependH.*;
import static mame.inptport.*;

public class redbaron {
        /* starwars uses A-to-D converter to get joystick-thrust as 8-bit data */
        /* trying the same, redbaron doesn't seem to like the full range 0-255? */
        /* dead response occurs at limits if min and max range is increased */
        static final int RBADCMAX= 160;                           /* +5v */
        static final int RBADCMIN= 96;                             /* gnd */
        static final int RBADCCTR= 128;
        static final int RESPONSE= 1;                            /* just a guess */


        static int roll=RBADCCTR;	/* start centered */
        static int pitch=RBADCCTR;
        static int input_select; 	/* 0 is roll_data, 1 is pitch_data */
        public static WriteHandlerPtr redbaron_joyselect = new WriteHandlerPtr() { public void handler(int offset, int data)
        {
                input_select=(data & 0x01);
        }};
        public static ReadHandlerPtr redbaron_joy_r = new ReadHandlerPtr() { public int handler(int offset)
        {
                int res;
                int trak;

                res=readinputport(3);
                trak=readtrakport(0);
                if (trak != NO_TRAK) 
                        roll+=trak;
                if ((res & 0x01)!=0)
                        roll-=RESPONSE;
                if ((res & 0x02)!=0)
                        roll+=RESPONSE;
                if (roll<RBADCMIN)
                        roll=RBADCMIN;
                if (roll>RBADCMAX)
                        roll=RBADCMAX;

                trak=readtrakport(1);
                if (trak != NO_TRAK) 
                        pitch+=trak;
                if ((res & 0x04)!=0)
                        pitch-=RESPONSE;
                if ((res & 0x08)!=0)
                        pitch+=RESPONSE;
                if (pitch<RBADCMIN)
                        pitch=RBADCMIN;
                if (pitch>RBADCMAX)
                        pitch=RBADCMAX;

                if (input_select!=0)
                        return (roll);
                else
                        return (pitch);
        }};    
}
