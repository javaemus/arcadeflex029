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


package M6502;

import static mame.cpuintrf.*;
import static mame.mame.*;
import static mame.memory.*;

/**
 *
 * @author shadow
 */
public class M6502H {
 public static final int INT_NONE = 0;
  public static final int INT_IRQ = 1;
  public static final int INT_NMI = 2;
  public static final int INT_QUIT = 3;
  static final int C_FLAG=0x01;         /* 1: Carry occured           */
  static final int Z_FLAG=0x02;        /* 1: Result is zero          */
  static final int I_FLAG=0x04;         /* 1: Interrupts disabled     */
  static final int D_FLAG=0x08;         /* 1: Decimal mode            */
  static final int B_FLAG=0x10;         /* Break [0 on stk after int] */
  static final int R_FLAG=0x20;         /* Always 1                   */
  static final int V_FLAG=0x40;         /* 1: Overflow occured        */
  static final int N_FLAG=0x80;         /* 1: Result is negative      */


  public static final void Wr6502(int paramInt1, int paramInt2)
  {
    cpu_writemem16(paramInt1, (char)paramInt2); }
  public static final int Rd6502(int paramInt) { return cpu_readmem16(paramInt); }
  public static final int Op6502(int paramInt) { return RAM[paramInt]; }
  public static final int Op6502_1(int paramInt) { return ROM[paramInt];
  }

  public static final int Loop6502(M6502Regs paramM6502Regs)
  {
    return cpu_interrupt();
  }

  public static class M6502Regs
  {
    public int A;
    public int P;
    public int X;
    public int Y;
    public int S;
    public M6502H.pair PC = new M6502H.pair(); public M6502H.pair previousPC = new M6502H.pair();
    public int IPeriod;
    public int ICount;
    //public int IRequest;
    public int pending_irq;	/* NS 970904 */
    public int pending_nmi;	/* NS 970904 */
    public int AfterCLI;
    public int IBackup;
    public char[] User;
    public int TrapBadOps;
    public int Trap;
    public int Trace;
  }

  public static class pair
  {
    int H;
    int L;
    public int W;

    public void SetH(int paramInt)
    {
      this.H = paramInt; this.W = (this.H << 8 | this.L); }
    public void SetL(int paramInt) { this.L = paramInt; this.W = (this.H << 8 | this.L); }
    public void SetW(int paramInt) { this.W = paramInt; this.H = (this.W >> 8); this.L = (this.W & 0xFF); }
    public void AddH(int paramInt) { this.H = (this.H + paramInt & 0xFF); this.W = (this.H << 8 | this.L); }
    public void AddL(int paramInt) { this.L = (this.L + paramInt & 0xFF); this.W = (this.H << 8 | this.L); }
    public void AddW(int paramInt) { this.W = (this.W + paramInt & 0xFFFF); this.H = (this.W >> 8); this.L = (this.W & 0xFF);
    }
  }
}
