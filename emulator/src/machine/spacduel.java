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

public class spacduel {
         static final int IN_LEFT=	(1 << 0);
         static final int IN_RIGHT= (1 << 1);
         static final int IN_FIRE= (1 << 2);
         static final int IN_SHIELD= (1 << 3);
         static final int IN_THRUST= (1 << 4);
         static final int IN_P1= (1 << 5);
         static final int IN_P2= (1 << 6);

        /*

        These 7 memory locations are used to read the 2 players' controls as well
        as sharing some dipswitch info in the lower 4 bits pertaining to coins/credits
        Typically, only the high 2 bits are read.

        */
        public static ReadHandlerPtr spacduel_IN3_r = new ReadHandlerPtr() { public int handler(int offset)
        {

                int res;
                int res1;
                int res2;

                res1 = readinputport(3);
                res2 = readinputport(4);
                res = 0x00;

                switch (offset & 0x07) {
                        case 0:
                                if ((res1 & IN_SHIELD)!=0) res |= 0x80;
                                if ((res1 & IN_FIRE)!=0) res |= 0x40;
                                break;
                        case 1: /* Player 2 */
                                if ((res2 & IN_SHIELD)!=0) res |= 0x80;
                                if ((res2 & IN_FIRE)!=0) res |= 0x40;
                                break;
                        case 2:
                                if ((res1 & IN_LEFT)!=0) res |= 0x80;
                                if ((res1 & IN_RIGHT)!=0) res |= 0x40;
                                break;
                        case 3: /* Player 2 */
                                if ((res2 & IN_LEFT)!=0) res |= 0x80;
                                if ((res2 & IN_RIGHT)!=0) res |= 0x40;
                                break;
                        case 4:
                                if ((res1 & IN_THRUST)!=0) res |= 0x80;
                                if ((res1 & IN_P1)!=0) res |= 0x40;
                                break;
                        case 5:  /* Player 2 */
                                if ((res2 & IN_THRUST)!=0) res |= 0x80;
                                break;
                        case 6:
                                if ((res1 & IN_P2)!=0) res |= 0x80;
                                break;
                        case 7:
                                res = (0x00 /* upright */ | (0 & 0x40));
                                break;
                        }
                return res;
                }};
   
}
