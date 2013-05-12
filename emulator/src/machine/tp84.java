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

import static arcadeflex.libc.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.inptport.*;
import static m6809.M6809H.*;
import static m6809.M6809.*;
import static vidhrdw.tp84.*;
import static mame.mame.*;

public class tp84 {
    public static InitMachinePtr tp84_init_machine = new InitMachinePtr()
    {
    public void handler() {
            m6809_Flags = M6809_FAST_OP | M6809_FAST_S | M6809_FAST_U;
    }};

    public static ReadHandlerPtr tp84_beam_r = new ReadHandlerPtr() {
    public int handler(int offset) {
    /*	return 255 - cpu_getiloops();	* return beam position */
            return 255; /* always return beam position 255 */ /* JB 970829 */
    }};
    public static InterruptPtr  tp84_interrupt= new InterruptPtr() {
          public int handler() {
            return interrupt.handler(); /* JB 970829 */
    /*	if (cpu_getiloops() == 0) return interrupt();
            else return ignore_interrupt();*/
    }};

    /* JB 970829 - catch a busy loop for CPU 1
            E0ED: LDA   #$01
            E0EF: STA   $4000
            E0F2: BRA   $E0ED
    */
    public static WriteHandlerPtr tp84_catchloop_w = new WriteHandlerPtr()
  {
    public void handler(int offset,int data)
    {

            if( cpu_getpc()==0xe0f2 ) cpu_seticount (0);
            RAM[0x4000] = (char)data;
    }};

    /* JB 970829 - catch a busy loop for CPU 0
            83AF: CLRA
            83B0: LDX   $523A
            83B3: LDD   ,X
            83B5: ASLA
            83B6: BCC   $8398
            83B8: BRA   $83AF
    */
    public static ReadHandlerPtr tp84_catchloop_r = new ReadHandlerPtr() {

     public int handler(int offset)
     {
            int data, dataw;

            data = tp84_sharedram.read(0x23a);

            if( cpu_getpc()==0x83b3 )
            {
                    dataw = data<<8 | tp84_sharedram.read(0x23b);
                    if ((tp84_sharedram.read(dataw-0x5000) & 0x80)!=0) cpu_seticount (0);
            }
            return data;
    }};   
}
