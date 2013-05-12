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

public class Z80H
{
	/****************************************************************************/
	/* Define a Z80 word. Upper bytes are always zero                           */
	/****************************************************************************/
	public static class z80_pair
	{
                public int H,L,W;
		public void SetH(int val) 
                {
                    H = val;
                    W = (H << 8) | L;
                }
		public void SetL(int val) 
                {
                    L = val;
                    W = (H << 8) | L;
                }
		public void SetW(int val)
                {
                    W = val;
                    H = W >> 8;
                    L = W & 0xFF;
                }
		public void AddH(int val) 
                {
                    H = (H + val) & 0xFF;
                    W = (H << 8) | L;
                }

		public void AddW(int val)
                {
                    W = (W + val) & 0xFFFF;
                    H = W >> 8;
                    L = W & 0xFF;
                }
              public void AddL(int val)
              {
                  L = (L + val) & 0xFF;
                  W = (H << 8) | L;
              }

	};

	/****************************************************************************/
	/* The Z80 registers. HALT is set to 1 when the CPU is halted, the refresh  */
	/* register is calculated as follows: refresh=(Regs.R&127)|(Regs.R2&128)    */
	/****************************************************************************/
	public static class Z80_Regs
	{
	  	public int AF2, BC2, DE2, HL2;
	  	public int IFF1, IFF2, HALT, IM, I, R, R2;
                public int AF, PC, SP;
                public int A, F;
                public z80_pair BC = new Z80H.z80_pair();
                public z80_pair DE = new Z80H.z80_pair();
                public z80_pair HL = new Z80H.z80_pair();
                public z80_pair IX = new Z80H.z80_pair();
                public z80_pair IY = new Z80H.z80_pair();
                int pending_irq,pending_nmi;
	};

	public static final int Z80_IGNORE_INT = -1;   /* Ignore interrupt                            */
	public static final int Z80_NMI_INT = -2;   	/* Execute NMI                                 */
	
	/*int Z80_Interrupt(void);*/           /* This is called after IPeriod T-States */
	                                   /* have been executed. It should return  */
	                                   /* Z80_IGNORE_INT, Z80_NMI_INT or a byte */
	                                   /* identifying the device (most often    */
	                                   /* 0xFF)                                 */
	//public static final int Z80_Interrupt() { return cpu_interrupt(); }
}
