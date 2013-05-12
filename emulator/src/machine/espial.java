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
 *
 * rewrote for v0.28
 *
 *
 *
 */
package machine;

import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.cpuintrf.*;


public class espial {
    static int interrupt_enable;


    public static InitMachinePtr espial_init_machine = new InitMachinePtr() {	public void handler()
    {
            /* we must start with NMI interrupts disabled */
            interrupt_enable = 0;
    }};


public static WriteHandlerPtr espial_interrupt_enable_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {

            interrupt_enable = NOT(data & 1);
    }};


public static InterruptPtr espial_interrupt = new InterruptPtr() {
            public int handler() {

            if (interrupt_enable!=0) return nmi_interrupt.handler();
            else return ignore_interrupt.handler();
    }};   
}
