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
import static mame.inptport.*;
import static vidhrdw.vector.*;
public class llander {
        static final int IN0_3KHZ=	(1 << 6);
        static final int IN0_SLAM=	(1 << 2);
        static final int IN0_TEST=	(1 << 1);
        static final int IN0_VG_HALT=	(1 << 0);

        static final int IN1_P1=	(1 << 0);
        static final int IN1_COIN=	(1 << 1);
        static final int IN1_COIN2=	(1 << 2);
        static final int IN1_COIN3=	(1 << 3);
        static final int IN1_P2=	(1 << 4);
        static final int IN1_ABORT=	(1 << 5);
        static final int IN1_RIGHT=	(1 << 6);
        static final int IN1_LEFT=	(1 << 7);

        static final int IN3_THRUST=	(1 << 0);
        static final int IN3_MAXTHRUST=	(1 << 1);

        public static ReadHandlerPtr llander_IN0_r = new ReadHandlerPtr() { public int handler(int offset)
        {

                int res;

                res = readinputport(0);

                if ((cpu_geticount() & 0x100)!=0)
                        res|=IN0_3KHZ;
                else
                        res&=~IN0_3KHZ;

        /*	res &= (vg_done (cpu_geticount())); */

                if ((vg_done(cpu_gettotalcycles()))!=0)
                        res|=IN0_VG_HALT;
                else
                        res&=~IN0_VG_HALT;

                return res;
                }
        };


        /*

        These 7 memory locations are used to read the player's controls.
        Typically, only the high bit is used. Note that the coin inputs are active-low.

        */
        public static ReadHandlerPtr llander_IN1_r = new ReadHandlerPtr() { public int handler(int offset)
        {

                int res;
                int res1;

                res1 = readinputport(1);
                res = 0x00;

                switch (offset & 0x07) {
                        case 0: /* start */
                                if ((res1 & IN1_P1)!=0) res |= 0x80;
                                break;
                        case 1:  /* left coin slot */
                                res = 0x80;
                                if ((res1 & IN1_COIN)!=0) res &= ~0x80;
                                break;
                        case 2: /* center coin slot */
                                res = 0x80;
        /*			if (res1 & IN1_COIN2) res &= ~0x80; */
                                break;
                        case 3: /* right coin slot */
                                res = 0x80;
        /*			if (res1 & IN1_COIN3) res &= ~0x80; */
                                break;
                        case 4: /* 2P start/select */
                                res = 0x80;
                                if ((res1 & IN1_P2)!=0) res &= ~0x80;
                                break;
                        case 5: /* abort */
                                if ((res1 & IN1_ABORT)!=0) res |= 0x80;
                                break;
                        case 6: /* rotate right */
                                if ((res1 & IN1_RIGHT)!=0) res |= 0x80;
                                break;
                        case 7: /* rotate left */
                                if ((res1 & IN1_LEFT)!=0) res |= 0x80;
                                break;
                        }
                return res;
                }
        };
        public static ReadHandlerPtr llander_IN3_r = new ReadHandlerPtr() { public int handler(int offset)
        {

                int res;

                res = readinputport(3);

                if ((res & IN3_THRUST)!=0)
                        return 0x80;
                if ((res & IN3_MAXTHRUST)!=0)
                        return 0xff;
                return 0x00;
                }};
        public static ReadHandlerPtr llander_DSW1_r = new ReadHandlerPtr() { public int handler(int offset)
        {

                int res;

                res = readinputport(2);

        /*	res = 0xfc | ((res1 >> (2 * (3 - (offset & 0x3)))) & 0x3);
        */
                return res;
                }  
        };
}
