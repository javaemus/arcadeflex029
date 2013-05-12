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

package m6809;
import static mame.cpuintrf.*;
import static mame.mame.*;
/**
 *
 * @author shadow
 */
public class M6809H
{
          public static class m6809_Regs
          {
            public int pc;
            public int u;
            public int s;
            public int x;
            public int y;
            public int dp;
            public int a;
            public int b;
            public int cc;
          }

          public static final int INT_NONE = 0; /* No interrupt required */
          public static final int INT_IRQ = 1; /* Standard IRQ interrupt */
          public static final int INT_FIRQ = 2; /* Fast IRQ */

          /****************************************************************************/
        /* Flags for optimizing memory access. Game drivers should set m6809_Flags  */
        /* to a combination of these flags depending on what can be safely          */
        /* optimized. For example, if M6809_FAST_OP is set, opcodes are fetched     */
        /* directly from the ROM array, and cpu_readmem() is not called.            */
        /* The flags affect reads and writes.                                       */
        /****************************************************************************/
          public static final int M6809_FAST_NONE = 0;/* no memory optimizations */
          public static final int M6809_FAST_OP = 1;/* opcode fetching */
          public static final int M6809_FAST_S = 2;/* stack */
          public static final int M6809_FAST_U = 4;/* user stack */

          /****************************************************************************/
          /* Read a byte from given memory location                                   */
          /****************************************************************************/
          public static final int M6809_RDMEM(int A)
          {
            return cpu_readmem(A);
          }
            /****************************************************************************/
            /* Write a byte to given memory location                                    */
            /****************************************************************************/
          public static final void M6809_WRMEM(int A, int V)
          {
            cpu_writemem(A, (char)V);
          }
        /****************************************************************************/
        /* Z80_RDOP() is identical to Z80_RDMEM() except it is used for reading     */
        /* opcodes. In case of system with memory mapped I/O, this function can be  */
        /* used to greatly speed up emulation                                       */
        /****************************************************************************/
          public static final int M6809_RDOP(int A)
          {
            return ROM[A];
          }
        /****************************************************************************/
        /* Z80_RDOP_ARG() is identical to Z80_RDOP() except it is used for reading  */
        /* opcode arguments. This difference can be used to support systems that    */
        /* use different encoding mechanisms for opcodes and opcode arguments       */
        /****************************************************************************/
          public static final int M6809_RDOP_ARG(int A)
          {
            return RAM[A];
          }
}



