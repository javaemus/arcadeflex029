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

public class milliped 
{
    static int IN0_VBLANK= (1<<6);

    static int vblank = 0;

    /* JB 23-JUL-97 modified again */
    static int milliped_val;
    public static ReadHandlerPtr milliped_IN_r = new ReadHandlerPtr(){
        
     public int handler(int offset) 
     {
        int res;

        /* ASG -- rewrote to incorporate fire controls and to
           toggle the IN0_VBLANK bit every other read after a VBLANK */
        res = readinputport(offset) & 0xbf;

        if (offset==0)
        if (vblank!=0)
        {
             milliped_val= IN0_VBLANK;
            milliped_val ^= IN0_VBLANK;
            res |= milliped_val;
            vblank = 0;
        }

        if ( cpu_geticount() > 4000 )
            return (res & 0x70) | (readtrakport(offset) & 0x8f);
        else
            return res;
    }};



    /* ASG added to set the vblank flag once per interrupt (a read to the in port above happens next */
    public static InterruptPtr milliped_interrupt = new InterruptPtr() { public int handler()
    {
            vblank = 1;

            return interrupt.handler();
    }};
}
