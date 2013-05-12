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

package Z80;

import static mame.cpuintrf.*;
import static mame.mame.*;
import static mame.memory.*;
import static mame.memoryH.*;

public class Z80IO
{

	/****************************************************************************/
	/* Input a byte from given I/O port                                         */
	/****************************************************************************/
	static final char Z80_In(int Port) { return (char) cpu_readport(Port); }

	/****************************************************************************/
	/* Output a byte to given I/O port                                          */
	/****************************************************************************/
	static final void Z80_Out(int Port, int Value) { cpu_writeport(Port, Value); }

	/****************************************************************************/
	/* Read a byte from given memory location                                   */
	/****************************************************************************/
	public static final char Z80_RDMEM(int A) { return (char) cpu_readmem16(A); }
	
	/****************************************************************************/
	/* Write a byte to given memory location                                    */
	/****************************************************************************/
	public static final void Z80_WRMEM(int A, int V) { cpu_writemem16(A, (char) V); }
	
	/****************************************************************************/
	/* Z80_RDOP() is identical to Z80_RDMEM() except it is used for reading     */
	/* opcodes. In case of system with memory mapped I/O, this function can be  */
	/* used to greatly speed up emulation                                       */
	/****************************************************************************/
	public static final char Z80_RDOP(int A) { return (char)cpu_readop(A); }
	
	/****************************************************************************/
	/* Z80_RDOP_ARG() is identical to Z80_RDOP() except it is used for reading  */
	/* opcode arguments. This difference can be used to support systems that    */
	/* use different encoding mechanisms for opcodes and opcode arguments       */
	/****************************************************************************/
	public static final char Z80_RDOP_ARG(int A) { return (char)cpu_readop_arg(A); }
	
	/****************************************************************************/
	/* Z80_RDSTACK() is identical to Z80_RDMEM() except it is used for reading  */
	/* stack variables. In case of system with memory mapped I/O, this function */
	/* can be used to slightly speed up emulation                               */
	/****************************************************************************/
	public static final char Z80_RDSTACK(int A) 
        {
            //return RAM[A];/* Galaga doesn't work with this */
            return (char)cpu_readmem16(A);
        }
	
	/****************************************************************************/
	/* Z80_WRSTACK() is identical to Z80_WRMEM() except it is used for writing  */
	/* stack variables. In case of system with memory mapped I/O, this function */
	/* can be used to slightly speed up emulation                               */
	/****************************************************************************/
	public static final void Z80_WRSTACK(int A, int V) 
        {
            //RAM[A] = (char) V; /* Galaga doesn't work with this */
            cpu_writemem16(A, (char)V);
        }
}