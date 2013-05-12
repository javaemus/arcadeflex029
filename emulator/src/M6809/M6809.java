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

/**
 *
 * @author shadow
 */
import static mame.cpuintrf.*;
import static mame.mame.*;
import static mame.driverH.*;
import static arcadeflex.libc.*;

public class M6809 {
  static int iccreg;
  static int idpreg;
  static int ixreg;
  static int iyreg;
  static int iureg;
  static int isreg;
  static int ipcreg;
  static int iareg;
  static int ibreg;
  static int eaddr;
  static int ireg;
  static int iflag;
  public static int m6809_IPeriod = 50000;
  public static int m6809_ICount = 50000;
  public static int m6809_IRequest = 0;
  public static int m6809_Flags;
  static int fastopcodes;
  static ReadHandlerPtr rd_op_handler;
  static ReadHandlerPtr rd_op_handler_wd;
  static ReadHandlerPtr rd_u_handler;
  static ReadHandlerPtr rd_u_handler_wd;
  static ReadHandlerPtr rd_s_handler;
  static ReadHandlerPtr rd_s_handler_wd;
  static WriteHandlerPtr wr_u_handler;
  static WriteHandlerPtr wr_u_handler_wd;
  static WriteHandlerPtr wr_s_handler;
  static WriteHandlerPtr wr_s_handler_wd;
  static int[] haspostbyte = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

  static int[] cycles = { 7, 0, 0, 7, 7, 0, 7, 7, 7, 7, 7, 0, 7, 7, 4, 7, 0, 0, 2, 2, 0, 0, 5, 9, 0, 2, 3, 0, 3, 2, 8, 6, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 2, 2, 2, 2, 0, 5, 3, 6, 0, 11, 0, 0, 2, 0, 0, 2, 2, 0, 2, 2, 2, 2, 2, 0, 2, 2, 0, 2, 2, 0, 0, 2, 2, 0, 2, 2, 2, 2, 2, 0, 2, 2, 0, 2, 7, 0, 0, 7, 7, 0, 7, 7, 7, 7, 7, 0, 7, 7, 4, 7, 7, 0, 0, 7, 7, 0, 7, 7, 7, 7, 7, 0, 7, 7, 4, 7, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 6, 6, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 6, 6, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 6, 6, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 6, 6, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 6, 6, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 6, 6, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 6, 6, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 6, 6 };

  public static ReadHandlerPtr rd_slow = new ReadHandlerPtr() {
    public int handler(int paramInt) {
      return M_RDMEM(paramInt);
    }
  };

  public static ReadHandlerPtr rd_slow_wd = new ReadHandlerPtr() {
    public int handler(int paramInt) {
      return M_RDMEM(paramInt) << 8 | M_RDMEM(paramInt + 1 & 0xFFFF);
    }
  };

  public static ReadHandlerPtr rd_fast = new ReadHandlerPtr() {
    public int handler(int paramInt) {
      return RAM[paramInt];
    }
  };

  public static ReadHandlerPtr rd_fast_wd = new ReadHandlerPtr() {
    public int handler(int paramInt) {
      return RAM[paramInt] << 8 | RAM[(paramInt + 1 & 0xFFFF)];
    }
  };

  public static WriteHandlerPtr wr_slow = new WriteHandlerPtr() {
    public void handler(int paramInt1, int paramInt2) {
      M_WRMEM(paramInt1, paramInt2);
    }
  };

  public static WriteHandlerPtr wr_slow_wd = new WriteHandlerPtr() {
    public void handler(int paramInt1, int paramInt2) {
      M_WRMEM(paramInt1, paramInt2 >> 8);
      M_WRMEM(paramInt1 + 1 & 0xFFFF, paramInt2 & 0xFF);
    }
  };

  public static WriteHandlerPtr wr_fast = new WriteHandlerPtr() {
    public void handler(int paramInt1, int paramInt2) {
      RAM[paramInt1] = (char)paramInt2;
    }
  };

  public static WriteHandlerPtr wr_fast_wd = new WriteHandlerPtr() {
    public void handler(int paramInt1, int paramInt2) {
      RAM[paramInt1] = (char)(paramInt2 >> 8);
      RAM[(paramInt1 + 1 & 0xFFFF)] = (char)(paramInt2 & 0xFF);
    }
  };

  static final int M_RDMEM(int paramInt)
  {
    return M6809H.M6809_RDMEM(paramInt); }
  static final void M_WRMEM(int paramInt1, int paramInt2) { M6809H.M6809_WRMEM(paramInt1, paramInt2); }
  static final int M_RDOP(int paramInt) { return M6809H.M6809_RDOP(paramInt); }
  static final int M_RDOP_ARG(int paramInt) { return M6809H.M6809_RDOP_ARG(paramInt); }

  static final int GETWORD(int paramInt) {
    return M_RDMEM_WORD(paramInt); }
  static final void SETBYTE(int paramInt1, int paramInt2) { M_WRMEM(paramInt1, paramInt2 & 0xFF); }

  static final void SETWORD(int paramInt1, int paramInt2)
  {
    M_WRMEM_WORD(paramInt1, paramInt2);
  }
  static final int IMMBYTE() {
    int i = rd_op_handler.handler(ipcreg); ipcreg = ipcreg + 1 & 0xFFFF; return i; }
  static final int IMMWORD() { int i = rd_op_handler_wd.handler(ipcreg); ipcreg = ipcreg + 2 & 0xFFFF; return i; }
  static final void PUSHBYTE(int paramInt) { isreg = isreg - 1 & 0xFFFF; wr_s_handler.handler(isreg, paramInt); }
  static final void PUSHWORD(int paramInt) { isreg = isreg - 2 & 0xFFFF; wr_s_handler_wd.handler(isreg, paramInt); }
  static final int PULLBYTE() { int i = rd_s_handler.handler(isreg); isreg = isreg + 1 & 0xFFFF; return i; }
  static final int PULLWORD() { int i = rd_s_handler_wd.handler(isreg); isreg = isreg + 2 & 0xFFFF; return i; }
  static final void PSHUBYTE(int paramInt) { iureg = iureg - 1 & 0xFFFF; wr_u_handler.handler(iureg, paramInt); }
  static final void PSHUWORD(int paramInt) { iureg = iureg - 2 & 0xFFFF; wr_u_handler_wd.handler(iureg, paramInt); }
  static final int PULUBYTE() { int i = rd_u_handler.handler(iureg); iureg = iureg + 1 & 0xFFFF; return i; }
  static final int PULUWORD() { int i = rd_u_handler_wd.handler(iureg); iureg = iureg + 2 & 0xFFFF; return i;
  }

  static final int GETDREG()
  {
    return iareg << 8 | ibreg; }
  static final void SETDREG(int paramInt) { iareg = paramInt >> 8 & 0xFF; ibreg = paramInt & 0xFF; }

  static final void DIRECT() {
    eaddr = IMMBYTE(); eaddr |= idpreg << 8; }
  static final void IMM8() { eaddr = ipcreg; ipcreg = ipcreg + 1 & 0xFFFF; }
  static final void IMM16() { eaddr = ipcreg; ipcreg = ipcreg + 2 & 0xFFFF; }
  static final void EXTENDED() { eaddr = IMMWORD(); }

  static final void SEC() {
    iccreg |= 1; }
  static final void CLC() { iccreg &= 254; }
  static final void SEZ() { iccreg |= 4; }
  static final void CLZ() { iccreg &= 251; }
  static final void SEN() { iccreg |= 8; }
  static final void CLN() { iccreg &= 247; }
  static final void SEV() { iccreg |= 2; }
  static final void CLV() { iccreg &= 253; }
  static final void SEH() { iccreg |= 32; }
  static final void CLH() { iccreg &= 223; }

  static final void SETNZ8(int paramInt) {
    if (paramInt != 0) CLZ(); else SEZ(); if ((paramInt & 0x80) != 0) SEN(); else CLN();
  }
  static final void SETNZ16(int paramInt) { if (paramInt != 0) CLZ(); else SEZ(); if ((paramInt & 0x8000) != 0) SEN(); else CLN();
  }

  static final void SETSTATUS(int paramInt1, int paramInt2, int paramInt3)
  {
    if (((paramInt1 ^ paramInt2 ^ paramInt3) & 0x10) != 0) SEH(); else CLH();
    if (((paramInt1 ^ paramInt2 ^ paramInt3 ^ paramInt3 >> 1) & 0x80) != 0) SEV(); else CLV();
    if ((paramInt3 & 0x100) != 0) SEC(); else CLC(); SETNZ8(paramInt3 & 0xFF);
  }

  static final void SETSTATUSD(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt3 & 0x10000) != 0) SEC(); else CLC();
    if (((paramInt3 >> 1 ^ paramInt1 ^ paramInt2 ^ paramInt3) & 0x8000) != 0) SEV(); else CLV();
    SETNZ16(paramInt3 & 0xFFFF);
  }

  static final void BRANCH(int paramInt)
  {
    int i;
    if (iflag == 0) { i = IMMBYTE(); if (paramInt != 0) ipcreg = ipcreg + (byte)i & 0xFFFF;  } else {
      i = IMMWORD(); if (paramInt != 0) ipcreg = ipcreg + i & 0xFFFF; m6809_ICount -= 2; }
  }
  static final int NXORV() { return iccreg & 0x8 ^ (iccreg & 0x2) << 2;
  }

  static final int GETREG(int paramInt)
  {
    switch (paramInt) {
    case 0:
      return GETDREG();
    case 1:
      return ixreg;
    case 2:
      return iyreg;
    case 3:
      return iureg;
    case 4:
      return isreg;
    case 5:
      return ipcreg;
    case 8:
      return iareg;
    case 9:
      return ibreg;
    case 10:
      return iccreg;
    case 11:
      return idpreg;
    case 6:
    case 7: } return -1;
  }

  static final void SETREG(int paramInt1, int paramInt2)
  {
    switch (paramInt2) {
    case 0:
      SETDREG(paramInt1); break;
    case 1:
      ixreg = paramInt1; break;
    case 2:
      iyreg = paramInt1; break;
    case 3:
      iureg = paramInt1; break;
    case 4:
      isreg = paramInt1; break;
    case 5:
      ipcreg = paramInt1; break;
    case 8:
      iareg = paramInt1; break;
    case 9:
      ibreg = paramInt1; break;
    case 10:
      iccreg = paramInt1; break;
    case 11:
      idpreg = paramInt1;
    case 6:
    case 7:
    }
  }

  static final int LOADAC() {
    return M_RDMEM(eaddr); }
  static final void STOREAC(int paramInt) { M_WRMEM(eaddr, paramInt);
  }

  static final int M_RDMEM_WORD(int paramInt)
  {
    int i = M_RDMEM(paramInt + 1 & 0xFFFF);
    i |= M_RDMEM(paramInt) << 8;
    return i;
  }

  static final void M_WRMEM_WORD(int paramInt1, int paramInt2)
  {
    M_WRMEM(paramInt1 + 1 & 0xFFFF, paramInt2 & 0xFF);
    M_WRMEM(paramInt1, paramInt2 >> 8);
  }

  public static void m6809_SetRegs(M6809H.m6809_Regs paramm6809_Regs)
  {
    ipcreg = paramm6809_Regs.pc;
    iureg = paramm6809_Regs.u;
    isreg = paramm6809_Regs.s;
    ixreg = paramm6809_Regs.x;
    iyreg = paramm6809_Regs.y;
    idpreg = paramm6809_Regs.dp;
    iareg = paramm6809_Regs.a;
    ibreg = paramm6809_Regs.b;
    iccreg = paramm6809_Regs.cc;
  }

  public static void m6809_GetRegs(M6809H.m6809_Regs paramm6809_Regs)
  {
    paramm6809_Regs.pc = ipcreg;
    paramm6809_Regs.u = iureg;
    paramm6809_Regs.s = isreg;
    paramm6809_Regs.x = ixreg;
    paramm6809_Regs.y = iyreg;
    paramm6809_Regs.dp = idpreg;
    paramm6809_Regs.a = iareg;
    paramm6809_Regs.b = ibreg;
    paramm6809_Regs.cc = iccreg;
  }

  public static int m6809_GetPC()
  {
    return ipcreg;
  }

  static void m6809_Interrupt()
  {
    if ((iccreg & 0x10) == 0)
    {
      PUSHWORD(ipcreg);
      PUSHWORD(iureg);
      PUSHWORD(iyreg);
      PUSHWORD(ixreg);
      PUSHBYTE(idpreg);
      PUSHBYTE(ibreg);
      PUSHBYTE(iareg);
      PUSHBYTE(iccreg);
      iccreg |= 144;
      ipcreg = GETWORD(65528);
      m6809_ICount -= 19;
    }
    else {
      m6809_IRequest = 1;
    }
  }

  static void m6809_FIRQ()
  {
    if ((iccreg & 0x40) == 0)
    {
      PUSHWORD(ipcreg);
      PUSHBYTE(iccreg);
      iccreg &= 127;
      iccreg |= 80;
      ipcreg = GETWORD(65526);
      m6809_ICount -= 10;
    }
  }

  public static void m6809_reset()
  {
    ipcreg = M_RDMEM_WORD(65534);

    idpreg = 0;
    iccreg = 0;
    iccreg |= 16;
    iccreg |= 64;
    iareg = 0;
    ibreg = 0;
    m6809_ICount = m6809_IPeriod;
    m6809_IRequest = 0;

    fastopcodes = 0;
    rd_op_handler = rd_slow;
    rd_op_handler_wd = rd_slow_wd;
    rd_u_handler = rd_slow;
    rd_u_handler_wd = rd_slow_wd;
    rd_s_handler = rd_slow;
    rd_s_handler_wd = rd_slow_wd;
    wr_u_handler = wr_slow;
    wr_u_handler_wd = wr_slow_wd;
    wr_s_handler = wr_slow;
    wr_s_handler_wd = wr_slow_wd;

    if ((m6809_Flags & 0x1) != 0)
    {
      fastopcodes = 1;
      rd_op_handler = rd_fast; rd_op_handler_wd = rd_fast_wd;
    }
    if ((m6809_Flags & 0x4) != 0)
    {
      rd_u_handler = rd_fast; rd_u_handler_wd = rd_fast_wd;
      wr_u_handler = wr_fast; wr_u_handler_wd = wr_fast_wd;
    }
    if ((m6809_Flags & 0x2) != 0)
    {
      rd_s_handler = rd_fast; rd_s_handler_wd = rd_fast_wd;
      wr_s_handler = wr_fast; wr_s_handler_wd = wr_fast_wd;
    }
  }

  public static void m6809_execute()
  {
    do
    {
      iflag = 0;
      while (true)
      {
        if (fastopcodes != 0) ireg = M_RDOP(ipcreg); else
          ireg = M_RDMEM(ipcreg);
        ipcreg = ipcreg + 1 & 0xFFFF;

        if (haspostbyte[ireg] != 0) fetch_effective_address();
        if (ireg == 16)
        {
          iflag = 1; continue;
        }
        if (ireg != 17)
          break;
        iflag = 2;
      }

      switch (ireg & 0xF0) {
      case 0:
        codes_0X(); break;
      case 16:
        codes_1X(); break;
      case 32:
        codes_2X(); break;
      case 48:
        codes_3X(); break;
      case 64:
        codes_4X(); break;
      case 80:
        codes_5X(); break;
      case 96:
        codes_6X(); break;
      case 112:
        codes_7X(); break;
      case 128:
        codes_8X(); break;
      case 144:
        codes_9X(); break;
      case 160:
        codes_AX(); break;
      case 176:
        codes_BX(); break;
      case 192:
        codes_CX(); break;
      case 208:
        codes_DX(); break;
      case 224:
        codes_EX(); break;
      case 240:
        codes_FX();
      }

      if (m6809_IRequest == 2)
      {
        m6809_IRequest = 0;
        m6809_FIRQ();
      }

      m6809_ICount -= cycles[(ireg & 0xFF)];
    }
    while (m6809_ICount > 0);

    m6809_IRequest = 0;
    int i = cpu_interrupt();
    m6809_ICount = m6809_IPeriod;

    if (i == 1) m6809_Interrupt();
  }

  static final void fetch_effective_address()
  {
    int i;
    if (fastopcodes != 0) i = M_RDOP(ipcreg); else
      i = M_RDMEM(ipcreg);
    ipcreg = ipcreg + 1 & 0xFFFF;

    switch (i) {
    case 0:
      eaddr = ixreg; break;
    case 1:
      eaddr = ixreg + 1 & 0xFFFF; break;
    case 2:
      eaddr = ixreg + 2 & 0xFFFF; break;
    case 3:
      eaddr = ixreg + 3 & 0xFFFF; break;
    case 4:
      eaddr = ixreg + 4 & 0xFFFF; break;
    case 5:
      eaddr = ixreg + 5 & 0xFFFF; break;
    case 6:
      eaddr = ixreg + 6 & 0xFFFF; break;
    case 7:
      eaddr = ixreg + 7 & 0xFFFF; break;
    case 8:
      eaddr = ixreg + 8 & 0xFFFF; break;
    case 9:
      eaddr = ixreg + 9 & 0xFFFF; break;
    case 10:
      eaddr = ixreg + 10 & 0xFFFF; break;
    case 11:
      eaddr = ixreg + 11 & 0xFFFF; break;
    case 12:
      eaddr = ixreg + 12 & 0xFFFF; break;
    case 13:
      eaddr = ixreg + 13 & 0xFFFF; break;
    case 14:
      eaddr = ixreg + 14 & 0xFFFF; break;
    case 15:
      eaddr = ixreg + 15 & 0xFFFF; break;
    case 16:
      eaddr = ixreg - 16 & 0xFFFF; break;
    case 17:
      eaddr = ixreg - 15 & 0xFFFF; break;
    case 18:
      eaddr = ixreg - 14 & 0xFFFF; break;
    case 19:
      eaddr = ixreg - 13 & 0xFFFF; break;
    case 20:
      eaddr = ixreg - 12 & 0xFFFF; break;
    case 21:
      eaddr = ixreg - 11 & 0xFFFF; break;
    case 22:
      eaddr = ixreg - 10 & 0xFFFF; break;
    case 23:
      eaddr = ixreg - 9 & 0xFFFF; break;
    case 24:
      eaddr = ixreg - 8 & 0xFFFF; break;
    case 25:
      eaddr = ixreg - 7 & 0xFFFF; break;
    case 26:
      eaddr = ixreg - 6 & 0xFFFF; break;
    case 27:
      eaddr = ixreg - 5 & 0xFFFF; break;
    case 28:
      eaddr = ixreg - 4 & 0xFFFF; break;
    case 29:
      eaddr = ixreg - 3 & 0xFFFF; break;
    case 30:
      eaddr = ixreg - 2 & 0xFFFF; break;
    case 31:
      eaddr = ixreg - 1 & 0xFFFF; break;
    case 33:
      eaddr = iyreg + 1 & 0xFFFF; break;
    case 34:
      eaddr = iyreg + 2 & 0xFFFF; break;
    case 35:
      eaddr = iyreg + 3 & 0xFFFF; break;
    case 36:
      eaddr = iyreg + 4 & 0xFFFF; break;
    case 37:
      eaddr = iyreg + 5 & 0xFFFF; break;
    case 38:
      eaddr = iyreg + 6 & 0xFFFF; break;
    case 39:
      eaddr = iyreg + 7 & 0xFFFF; break;
    case 40:
      eaddr = iyreg + 8 & 0xFFFF; break;
    case 41:
      eaddr = iyreg + 9 & 0xFFFF; break;
    case 42:
      eaddr = iyreg + 10 & 0xFFFF; break;
    case 43:
      eaddr = iyreg + 11 & 0xFFFF; break;
    case 44:
      eaddr = iyreg + 12 & 0xFFFF; break;
    case 45:
      eaddr = iyreg + 13 & 0xFFFF; break;
    case 46:
      eaddr = iyreg + 14 & 0xFFFF; break;
    case 47:
      eaddr = iyreg + 15 & 0xFFFF; break;
    case 48:
      eaddr = iyreg - 16 & 0xFFFF; break;
    case 49:
      eaddr = iyreg - 15 & 0xFFFF; break;
    case 50:
      eaddr = iyreg - 14 & 0xFFFF; break;
    case 51:
      eaddr = iyreg - 13 & 0xFFFF; break;
    case 52:
      eaddr = iyreg - 12 & 0xFFFF; break;
    case 53:
      eaddr = iyreg - 11 & 0xFFFF; break;
    case 54:
      eaddr = iyreg - 10 & 0xFFFF; break;
    case 55:
      eaddr = iyreg - 9 & 0xFFFF; break;
    case 56:
      eaddr = iyreg - 8 & 0xFFFF; break;
    case 57:
      eaddr = iyreg - 7 & 0xFFFF; break;
    case 58:
      eaddr = iyreg - 6 & 0xFFFF; break;
    case 59:
      eaddr = iyreg - 5 & 0xFFFF; break;
    case 60:
      eaddr = iyreg - 4 & 0xFFFF; break;
    case 61:
      eaddr = iyreg - 3 & 0xFFFF; break;
    case 62:
      eaddr = iyreg - 2 & 0xFFFF; break;
    case 63:
      eaddr = iyreg - 1 & 0xFFFF; break;
    case 65:
      eaddr = iureg + 1 & 0xFFFF; break;
    case 66:
      eaddr = iureg + 2 & 0xFFFF; break;
    case 67:
      eaddr = iureg + 3 & 0xFFFF; break;
    case 68:
      eaddr = iureg + 4 & 0xFFFF; break;
    case 69:
      eaddr = iureg + 5 & 0xFFFF; break;
    case 70:
      eaddr = iureg + 6 & 0xFFFF; break;
    case 71:
      eaddr = iureg + 7 & 0xFFFF; break;
    case 72:
      eaddr = iureg + 8 & 0xFFFF; break;
    case 73:
      eaddr = iureg + 9 & 0xFFFF; break;
    case 74:
      eaddr = iureg + 10 & 0xFFFF; break;
    case 75:
      eaddr = iureg + 11 & 0xFFFF; break;
    case 76:
      eaddr = iureg + 12 & 0xFFFF; break;
    case 77:
      eaddr = iureg + 13 & 0xFFFF; break;
    case 78:
      eaddr = iureg + 14 & 0xFFFF; break;
    case 79:
      eaddr = iureg + 15 & 0xFFFF; break;
    case 80:
      eaddr = iureg - 16 & 0xFFFF; break;
    case 81:
      eaddr = iureg - 15 & 0xFFFF; break;
    case 82:
      eaddr = iureg - 14 & 0xFFFF; break;
    case 83:
      eaddr = iureg - 13 & 0xFFFF; break;
    case 84:
      eaddr = iureg - 12 & 0xFFFF; break;
    case 85:
      eaddr = iureg - 11 & 0xFFFF; break;
    case 86:
      eaddr = iureg - 10 & 0xFFFF; break;
    case 87:
      eaddr = iureg - 9 & 0xFFFF; break;
    case 88:
      eaddr = iureg - 8 & 0xFFFF; break;
    case 89:
      eaddr = iureg - 7 & 0xFFFF; break;
    case 90:
      eaddr = iureg - 6 & 0xFFFF; break;
    case 91:
      eaddr = iureg - 5 & 0xFFFF; break;
    case 92:
      eaddr = iureg - 4 & 0xFFFF; break;
    case 93:
      eaddr = iureg - 3 & 0xFFFF; break;
    case 94:
      eaddr = iureg - 2 & 0xFFFF; break;
    case 95:
      eaddr = iureg - 1 & 0xFFFF; break;
    case 97:
      eaddr = isreg + 1 & 0xFFFF; break;
    case 98:
      eaddr = isreg + 2 & 0xFFFF; break;
    case 99:
      eaddr = isreg + 3 & 0xFFFF; break;
    case 100:
      eaddr = isreg + 4 & 0xFFFF; break;
    case 101:
      eaddr = isreg + 5 & 0xFFFF; break;
    case 102:
      eaddr = isreg + 6 & 0xFFFF; break;
    case 104:
      eaddr = isreg + 8 & 0xFFFF; break;
    case 105:
      eaddr = isreg + 9 & 0xFFFF; break;
    case 107:
      eaddr = isreg + 11 & 0xFFFF; break;
    case 108:
      eaddr = isreg + 12 & 0xFFFF; break;
    case 110:
      eaddr = isreg + 14 & 0xFFFF; break;
    case 120:
      eaddr = isreg - 8 & 0xFFFF; break;
    case 122:
      eaddr = isreg - 6 & 0xFFFF; break;
    case 123:
      eaddr = isreg - 5 & 0xFFFF; break;
    case 125:
      eaddr = isreg - 3 & 0xFFFF; break;
    case 126:
      eaddr = isreg - 2 & 0xFFFF; break;
    case 127:
      eaddr = isreg - 1 & 0xFFFF; break;
    case 128:
      eaddr = ixreg; ixreg = ixreg + 1 & 0xFFFF; break;
    case 129:
      eaddr = ixreg; ixreg = ixreg + 2 & 0xFFFF; break;
    case 130:
      ixreg = ixreg - 1 & 0xFFFF; eaddr = ixreg; break;
    case 131:
      ixreg = ixreg - 2 & 0xFFFF; eaddr = ixreg; break;
    case 132:
      eaddr = ixreg; break;
    case 133:
      eaddr = ixreg + (byte)ibreg & 0xFFFF; break;
    case 134:
      eaddr = ixreg + (byte)iareg & 0xFFFF; break;
    case 136:
      eaddr = IMMBYTE(); eaddr = ixreg + (byte)eaddr & 0xFFFF; break;
    case 137:
      eaddr = IMMWORD(); eaddr = eaddr + ixreg & 0xFFFF; break;
    case 139:
      eaddr = ixreg + GETDREG() & 0xFFFF; break;
    case 145:
      eaddr = ixreg; ixreg = ixreg + 2 & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 148:
      eaddr = ixreg; eaddr = GETWORD(eaddr); break;
    case 149:
      eaddr = ixreg + (byte)ibreg & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 150:
      eaddr = ixreg + (byte)iareg & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 152:
      eaddr = IMMBYTE(); eaddr = ixreg + (byte)eaddr & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 155:
      eaddr = ixreg + GETDREG() & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 159:
      eaddr = IMMWORD(); eaddr = GETWORD(eaddr); break;
    case 160:
      eaddr = iyreg; iyreg = iyreg + 1 & 0xFFFF; break;
    case 161:
      eaddr = iyreg; iyreg = iyreg + 2 & 0xFFFF; break;
    case 162:
      iyreg = iyreg - 1 & 0xFFFF; eaddr = iyreg; break;
    case 163:
      iyreg = iyreg - 2 & 0xFFFF; eaddr = iyreg; break;
    case 164:
      eaddr = iyreg; break;
    case 165:
      eaddr = iyreg + (byte)ibreg & 0xFFFF; break;
    case 166:
      eaddr = iyreg + (byte)iareg & 0xFFFF; break;
    case 168:
      eaddr = IMMBYTE(); eaddr = iyreg + (byte)eaddr & 0xFFFF; break;
    case 169:
      eaddr = IMMWORD(); eaddr = eaddr + iyreg & 0xFFFF; break;
    case 170:
      eaddr = 0; break;
    case 171:
      eaddr = iyreg + GETDREG() & 0xFFFF; break;
    case 177:
      eaddr = iyreg; iyreg = iyreg + 2 & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 180:
      eaddr = iyreg; eaddr = GETWORD(eaddr); break;
    case 181:
      eaddr = iyreg + (byte)ibreg & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 182:
      eaddr = iyreg + (byte)iareg & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 184:
      eaddr = IMMBYTE(); eaddr = iyreg + (byte)eaddr & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 192:
      eaddr = iureg; iureg = iureg + 1 & 0xFFFF; break;
    case 193:
      eaddr = iureg; iureg = iureg + 2 & 0xFFFF; break;
    case 194:
      iureg = iureg - 1 & 0xFFFF; eaddr = iureg; break;
    case 195:
      iureg = iureg - 2 & 0xFFFF; eaddr = iureg; break;
    case 196:
      eaddr = iureg; break;
    case 197:
      eaddr = iureg + (byte)ibreg & 0xFFFF; break;
    case 198:
      eaddr = iureg + (byte)iareg & 0xFFFF; break;
    case 200:
      eaddr = IMMBYTE(); eaddr = iureg + (byte)eaddr & 0xFFFF; break;
    case 201:
      eaddr = IMMWORD(); eaddr = eaddr + iureg & 0xFFFF; break;
    case 203:
      eaddr = iureg + GETDREG() & 0xFFFF; break;
    case 209:
      eaddr = iureg; iureg = iureg + 2 & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 212:
      eaddr = iureg; eaddr = GETWORD(eaddr); break;
    case 213:
      eaddr = iureg + (byte)ibreg & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 214:
      eaddr = iureg + (byte)iareg & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 216:
      eaddr = IMMBYTE(); eaddr = iureg + (byte)eaddr & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 224:
      eaddr = isreg; isreg = isreg + 1 & 0xFFFF; break;
    case 225:
      eaddr = isreg; isreg = isreg + 2 & 0xFFFF; break;
    case 226:
      isreg = isreg - 1 & 0xFFFF; eaddr = isreg; break;
    case 227:
      isreg = isreg - 2 & 0xFFFF; eaddr = isreg; break;
    case 228:
      eaddr = isreg; break;
    case 232:
      eaddr = IMMBYTE(); eaddr = isreg + (byte)eaddr & 0xFFFF; break;
    case 233:
      eaddr = IMMWORD(); eaddr = eaddr + isreg & 0xFFFF; break;
    case 238:
      eaddr = 0; break;
    case 244:
      eaddr = isreg; eaddr = GETWORD(eaddr); break;
    case 248:
      eaddr = IMMBYTE(); eaddr = isreg + (byte)eaddr & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 32:
    case 64:
    case 96:
    case 103:
    case 106:
    case 109:
    case 111:
    case 112:
    case 113:
    case 114:
    case 115:
    case 116:
    case 117:
    case 118:
    case 119:
    case 121:
    case 124:
    case 135:
    case 138:
    case 140:
    case 141:
    case 142:
    case 143:
    case 144:
    case 146:
    case 147:
    case 151:
    case 153:
    case 154:
    case 156:
    case 157:
    case 158:
    case 167:
    case 172:
    case 173:
    case 174:
    case 175:
    case 176:
    case 178:
    case 179:
    case 183:
    case 185:
    case 186:
    case 187:
    case 188:
    case 189:
    case 190:
    case 191:
    case 199:
    case 202:
    case 204:
    case 205:
    case 206:
    case 207:
    case 208:
    case 210:
    case 211:
    case 215:
    case 217:
    case 218:
    case 219:
    case 220:
    case 221:
    case 222:
    case 223:
    case 229:
    case 230:
    case 231:
    case 234:
    case 235:
    case 236:
    case 237:
    case 239:
    case 240:
    case 241:
    case 242:
    case 243:
    case 245:
    case 246:
    case 247:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = FEA " + Integer.toHexString(i));
      break;
    }
  }

  static final void codes_0X()
  {
    int j;
    int i;
    int k;
    switch (ireg) {
    case 0:
      DIRECT(); j = M_RDMEM(eaddr); i = -j & 0xFFFF; SETSTATUS(0, j, i); SETBYTE(eaddr, i); break;
    case 1:
      break;
    case 2:
      break;
    case 3:
      DIRECT(); k = (M_RDMEM(eaddr) ^ 0xFFFFFFFF) & 0xFF; SETNZ8(k); SEC(); CLV(); SETBYTE(eaddr, k); break;
    case 4:
      DIRECT(); k = M_RDMEM(eaddr); if ((k & 0x1) != 0) SEC(); else CLC(); if ((k & 0x10) != 0) SEH(); else CLH(); k = k >> 1 & 0xFF; SETNZ8(k); SETBYTE(eaddr, k); break;
    case 6:
      DIRECT();
      k = (iccreg & 0x1) << 7 & 0xFF;
      int m = M_RDMEM(eaddr);
      if ((m & 0x1) != 0) SEC(); else CLC();
      i = (m >> 1) + k & 0xFFFF;
      SETNZ8(i);
      SETBYTE(eaddr, i);
      break;
    case 8:
      DIRECT(); j = M_RDMEM(eaddr); i = j << 1 & 0xFFFF; SETSTATUS(j, j, i); SETBYTE(eaddr, i); break;
    case 9:
      DIRECT();
      k = M_RDMEM(eaddr);
      i = iccreg & 0x1;
      if ((k & 0x80) != 0) SEC(); else CLC();
      if ((k & 0x80 ^ k << 1 & 0x80) != 0) SEV(); else CLV();
      k = (k << 1) + i & 0xFF;
      SETNZ8(k);
      SETBYTE(eaddr, k);
      break;
    case 10:
      DIRECT(); k = M_RDMEM(eaddr) - 1 & 0xFF; if (k == 127) SEV(); else CLV(); SETNZ8(k); SETBYTE(eaddr, k); break;
    case 12:
      DIRECT(); k = M_RDMEM(eaddr) + 1 & 0xFF; if (k == 128) SEV(); else CLV(); SETNZ8(k); SETBYTE(eaddr, k); break;
    case 13:
      DIRECT(); k = M_RDMEM(eaddr); SETNZ8(k); break;
    case 14:
      DIRECT(); ipcreg = eaddr; break;
    case 15:
      DIRECT(); SETBYTE(eaddr, 0); CLN(); CLV(); SEZ(); CLC(); break;
    case 5:
    case 7:
    case 11:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = 0X " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_1X()
  {
    int i;
    int j;
    switch (ireg) {
    case 18:
      break;
    case 19:
      break;
    case 21:
      break;
    case 22:
      eaddr = IMMWORD(); ipcreg = ipcreg + eaddr & 0xFFFF; break;
    case 23:
      eaddr = IMMWORD(); PUSHWORD(ipcreg); ipcreg = ipcreg + eaddr & 0xFFFF; break;
    case 25:
      i = iareg;
      if ((iccreg & 0x20) != 0) i += 6;
      if ((i & 0xF) > 9) i += 6;
      if ((iccreg & 0x1) != 0) i += 96;
      if ((i & 0xF0) > 144) i += 96;
      if ((i & 0x100) != 0) SEC();
      iareg = i & 0xFF;
      break;
    case 26:
      j = IMMBYTE(); iccreg |= j; break;
    case 28:
      j = IMMBYTE(); iccreg &= j; break;
    case 29:
      i = (byte)ibreg & 0xFFFF; SETNZ16(i); SETDREG(i); break;
    case 30:
      j = IMMBYTE();

      i = GETREG(j >> 4);
      int k = GETREG(j & 0xF);
      SETREG(k, j >> 4);
      SETREG(i, j & 0xF);

      break;
    case 31:
      j = IMMBYTE(); i = GETREG(j >> 4); SETREG(i, j & 0xF); break;
    case 20:
    case 24:
    case 27:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = 1X " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_2X()
  {
    switch (ireg) {
    case 32:
      BRANCH(1); break;
    case 34:
      BRANCH(NOT(iccreg & 0x5)); break;
    case 35:
      BRANCH(iccreg & 0x5); break;
    case 36:
      BRANCH(NOT(iccreg & 0x1)); break;
    case 37:
      BRANCH(iccreg & 0x1); break;
    case 38:
      BRANCH(NOT(iccreg & 0x4)); break;
    case 39:
      BRANCH(iccreg & 0x4); break;
    case 40:
      BRANCH(NOT(iccreg & 0x2)); break;
    case 41:
      BRANCH(iccreg & 0x2); break;
    case 42:
      BRANCH(NOT(iccreg & 0x8)); break;
    case 43:
      BRANCH(iccreg & 0x8); break;
    case 44:
      BRANCH(NOT(NXORV())); break;
    case 45:
      BRANCH(NXORV()); break;
    case 46:
      BRANCH(NOT((NXORV() != 0) || ((iccreg & 0x4) != 0))); break;
    case 47:
      BRANCH(BOOL((NXORV() != 0) || ((iccreg & 0x4) != 0))); break;
    case 33:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = 2X " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_3X()
  {
    int j;
    switch (ireg) {
    case 48:
      ixreg = eaddr; if (ixreg != 0) CLZ(); else SEZ(); break;
    case 49:
      iyreg = eaddr; if (iyreg != 0) CLZ(); else SEZ(); break;
    case 50:
      isreg = eaddr; break;
    case 51:
      iureg = eaddr; break;
    case 52:
      j = IMMBYTE();
      if ((j & 0x80) != 0) PUSHWORD(ipcreg);
      if ((j & 0x40) != 0) PUSHWORD(iureg);
      if ((j & 0x20) != 0) PUSHWORD(iyreg);
      if ((j & 0x10) != 0) PUSHWORD(ixreg);
      if ((j & 0x8) != 0) PUSHBYTE(idpreg);
      if ((j & 0x4) != 0) PUSHBYTE(ibreg);
      if ((j & 0x2) != 0) PUSHBYTE(iareg);
      if ((j & 0x1) == 0) break; PUSHBYTE(iccreg); break;
    case 53:
      j = IMMBYTE();
      if ((j & 0x1) != 0) iccreg = PULLBYTE();
      if ((j & 0x2) != 0) iareg = PULLBYTE();
      if ((j & 0x4) != 0) ibreg = PULLBYTE();
      if ((j & 0x8) != 0) idpreg = PULLBYTE();
      if ((j & 0x10) != 0) ixreg = PULLWORD();
      if ((j & 0x20) != 0) iyreg = PULLWORD();
      if ((j & 0x40) != 0) iureg = PULLWORD();
      if ((j & 0x80) == 0) break; ipcreg = PULLWORD(); break;
    case 54:
      j = IMMBYTE();
      if ((j & 0x80) != 0) PSHUWORD(ipcreg);
      if ((j & 0x40) != 0) PSHUWORD(isreg);
      if ((j & 0x20) != 0) PSHUWORD(iyreg);
      if ((j & 0x10) != 0) PSHUWORD(ixreg);
      if ((j & 0x8) != 0) PSHUBYTE(idpreg);
      if ((j & 0x4) != 0) PSHUBYTE(ibreg);
      if ((j & 0x2) != 0) PSHUBYTE(iareg);
      if ((j & 0x1) == 0) break; PSHUBYTE(iccreg); break;
    case 55:
      j = IMMBYTE();
      if ((j & 0x1) != 0) iccreg = PULUBYTE();
      if ((j & 0x2) != 0) iareg = PULUBYTE();
      if ((j & 0x4) != 0) ibreg = PULUBYTE();
      if ((j & 0x8) != 0) idpreg = PULUBYTE();
      if ((j & 0x10) != 0) ixreg = PULUWORD();
      if ((j & 0x20) != 0) iyreg = PULUWORD();
      if ((j & 0x40) != 0) isreg = PULUWORD();
      if ((j & 0x80) == 0) break; ipcreg = PULUWORD(); break;
    case 57:
      ipcreg = PULLWORD(); break;
    case 58:
      ixreg = ixreg + ibreg & 0xFFFF; break;
    case 59:
      j = iccreg & 0x80;
      iccreg = PULLBYTE();
      if (j != 0)
      {
        m6809_ICount -= 9;
        iareg = PULLBYTE();
        ibreg = PULLBYTE();
        idpreg = PULLBYTE();
        ixreg = PULLWORD();
        iyreg = PULLWORD();
        iureg = PULLWORD();
      }
      ipcreg = PULLWORD();
      break;
    case 61:
      int i = iareg * ibreg & 0xFFFF; if (i != 0) CLZ(); else SEZ(); if ((i & 0x80) != 0) SEC(); else CLC(); SETDREG(i); break;
    case 56:
    case 60:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = 3X " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_4X()
  {
    int i;
    int j;
    switch (ireg) {
    case 64:
      i = -iareg & 0xFFFF; SETSTATUS(0, iareg, i); iareg = i & 0xFF; break;
    case 67:
      j = (iareg ^ 0xFFFFFFFF) & 0xFF; SETNZ8(j); SEC(); CLV(); iareg = j; break;
    case 68:
      j = iareg; if ((j & 0x1) != 0) SEC(); else CLC(); if ((j & 0x10) != 0) SEH(); else CLH(); j = j >> 1 & 0xFF; SETNZ8(j); iareg = j; break;
    case 69:
      break;
    case 70:
      j = (iccreg & 0x1) << 7 & 0xFF; if ((iareg & 0x1) != 0) SEC(); else CLC(); iareg = (iareg >> 1) + j & 0xFF; SETNZ8(iareg); break;
    case 71:
      j = iareg; if ((j & 0x1) != 0) SEC(); else CLC(); if ((j & 0x10) != 0) SEH(); else CLH(); j = j >> 1 & 0xFF; if ((j & 0x40) != 0) j |= 128; iareg = j; SETNZ8(j); break;
    case 72:
      i = iareg << 1 & 0xFFFF; SETSTATUS(iareg, iareg, i); iareg = i & 0xFF; break;
    case 73:
      j = iareg; i = iccreg & 0x1; if ((j & 0x80) != 0) SEC(); else CLC(); if ((j & 0x80 ^ j << 1 & 0x80) != 0) SEV(); else CLV(); j = (j << 1) + i & 0xFF; SETNZ8(j); iareg = j; break;
    case 74:
      j = iareg - 1 & 0xFF; if (j == 127) SEV(); else CLV(); SETNZ8(j); iareg = j; break;
    case 76:
      j = iareg + 1 & 0xFF; if (j == 128) SEV(); else CLV(); SETNZ8(j); iareg = j; break;
    case 77:
      SETNZ8(iareg); break;
    case 79:
      iareg = 0; CLN(); CLV(); SEZ(); CLC(); break;
    case 65:
    case 66:
    case 75:
    case 78:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = 4X " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_5X()
  {
    int i;
    int j;
    switch (ireg) {
    case 80:
      i = -ibreg & 0xFFFF; SETSTATUS(0, ibreg, i); ibreg = i & 0xFF; break;
    case 83:
      j = (ibreg ^ 0xFFFFFFFF) & 0xFF; SETNZ8(j); SEC(); CLV(); ibreg = j; break;
    case 84:
      j = ibreg; if ((j & 0x1) != 0) SEC(); else CLC(); if ((j & 0x10) != 0) SEH(); else CLH(); j = j >> 1 & 0xFF; SETNZ8(j); ibreg = j; break;
    case 86:
      j = (iccreg & 0x1) << 7 & 0xFF; if ((ibreg & 0x1) != 0) SEC(); else CLC(); ibreg = (ibreg >> 1) + j & 0xFF; SETNZ8(ibreg); break;
    case 87:
      j = ibreg; if ((j & 0x1) != 0) SEC(); else CLC(); if ((j & 0x10) != 0) SEH(); else CLH(); j = j >> 1 & 0xFF; if ((j & 0x40) != 0) j |= 128; ibreg = j; SETNZ8(j); break;
    case 88:
      i = ibreg << 1 & 0xFFFF; SETSTATUS(ibreg, ibreg, i); ibreg = i & 0xFF; break;
    case 89:
      j = ibreg;
      i = iccreg & 0x1;
      if ((j & 0x80) != 0) SEC(); else CLC();
      if ((j & 0x80 ^ j << 1 & 0x80) != 0) SEV(); else CLV();
      j = (j << 1) + i & 0xFF;
      SETNZ8(j);
      ibreg = j;
      break;
    case 90:
      j = ibreg - 1 & 0xFF; if (j == 127) SEV(); else CLV(); SETNZ8(j); ibreg = j; break;
    case 92:
      j = ibreg + 1 & 0xFF; if (j == 128) SEV(); else CLV(); SETNZ8(j); ibreg = j; break;
    case 93:
      SETNZ8(ibreg); break;
    case 95:
      ibreg = 0; CLN(); CLV(); SEZ(); CLC(); break;
    case 81:
    case 82:
    case 85:
    case 91:
    case 94:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = 5X " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_6X()
  {
    int i;
    int j;
    int m;
    switch (ireg) {
    case 96:
      i = M_RDMEM(eaddr); j = -i & 0xFFFF; SETSTATUS(0, i, j); SETBYTE(eaddr, j); break;
    case 99:
      m = (M_RDMEM(eaddr) ^ 0xFFFFFFFF) & 0xFF; SETNZ8(m); SEC(); CLV(); SETBYTE(eaddr, m); break;
    case 100:
      m = M_RDMEM(eaddr); if ((m & 0x1) != 0) SEC(); else CLC(); if ((m & 0x10) != 0) SEH(); else CLH(); m = m >> 1 & 0xFF; SETNZ8(m); SETBYTE(eaddr, m); break;
    case 102:
      m = (iccreg & 0x1) << 7 & 0xFF;
      int k = M_RDMEM(eaddr);
      if ((k & 0x1) != 0) SEC(); else CLC();
      j = (k >> 1) + m & 0xFFFF;
      SETNZ8(j);
      SETBYTE(eaddr, j);
      break;
    case 103:
      m = M_RDMEM(eaddr); if ((m & 0x1) != 0) SEC(); else CLC(); if ((m & 0x10) != 0) SEH(); else CLH(); m = m >> 1 & 0xFF; if ((m & 0x40) != 0) m |= 128; SETBYTE(eaddr, m); SETNZ8(m); break;
    case 104:
      i = M_RDMEM(eaddr); j = i << 1 & 0xFFFF; SETSTATUS(i, i, j); SETBYTE(eaddr, j); break;
    case 105:
      m = M_RDMEM(eaddr);
      j = iccreg & 0x1;
      if ((m & 0x80) != 0) SEC(); else CLC();
      if ((m & 0x80 ^ m << 1 & 0x80) != 0) SEV(); else CLV();
      m = (m << 1) + j & 0xFF;
      SETNZ8(m);
      SETBYTE(eaddr, m);
      break;
    case 106:
      m = M_RDMEM(eaddr) - 1 & 0xFF; if (m == 127) SEV(); else CLV(); SETNZ8(m); SETBYTE(eaddr, m); break;
    case 108:
      m = M_RDMEM(eaddr) + 1 & 0xFF; if (m == 128) SEV(); else CLV(); SETNZ8(m); SETBYTE(eaddr, m); break;
    case 109:
      m = M_RDMEM(eaddr); SETNZ8(m); break;
    case 110:
      ipcreg = eaddr; break;
    case 111:
      SETBYTE(eaddr, 0); CLN(); CLV(); SEZ(); CLC(); break;
    case 97:
    case 98:
    case 101:
    case 107:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = 6X " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_7X()
  {
    int m;
    int j;
    switch (ireg) {
    case 115:
      EXTENDED(); m = (M_RDMEM(eaddr) ^ 0xFFFFFFFF) & 0xFF; SETNZ8(m); SEC(); CLV(); SETBYTE(eaddr, m); break;
    case 116:
      EXTENDED(); m = M_RDMEM(eaddr); if ((m & 0x1) != 0) SEC(); else CLC(); if ((m & 0x10) != 0) SEH(); else CLH(); m = m >> 1 & 0xFF; SETNZ8(m); SETBYTE(eaddr, m); break;
    case 118:
      EXTENDED(); m = (iccreg & 0x1) << 7 & 0xFF; int k = M_RDMEM(eaddr); if ((k & 0x1) != 0) SEC(); else CLC(); j = (k >> 1) + m & 0xFFFF; SETNZ8(j); SETBYTE(eaddr, j); break;
    case 120:
      EXTENDED(); int i = M_RDMEM(eaddr); j = i << 1 & 0xFFFF; SETSTATUS(i, i, j); SETBYTE(eaddr, j); break;
    case 121:
      EXTENDED(); m = M_RDMEM(eaddr); j = iccreg & 0x1; if ((m & 0x80) != 0) SEC(); else CLC(); if ((m & 0x80 ^ m << 1 & 0x80) != 0) SEV(); else CLV(); m = (m << 1) + j & 0xFF; SETNZ8(m); SETBYTE(eaddr, m); break;
    case 122:
      EXTENDED(); m = M_RDMEM(eaddr) - 1 & 0xFF; if (m == 127) SEV(); else CLV(); SETNZ8(m); SETBYTE(eaddr, m); break;
    case 124:
      EXTENDED(); m = M_RDMEM(eaddr) + 1 & 0xFF; if (m == 128) SEV(); else CLV(); SETNZ8(m); SETBYTE(eaddr, m); break;
    case 125:
      EXTENDED(); m = M_RDMEM(eaddr); SETNZ8(m); break;
    case 126:
      EXTENDED(); ipcreg = eaddr; break;
    case 127:
      EXTENDED(); SETBYTE(eaddr, 0); CLN(); CLV(); SEZ(); CLC(); break;
    case 117:
    case 119:
    case 123:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = 7X " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_8X()
  {
    int i;
    int j;
    int n;
    int i1;
    int m;
    int k;
    switch (ireg) {
    case 128:
      IMM8(); i = M_RDMEM(eaddr); j = iareg - i & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 129:
      IMM8(); i = M_RDMEM(eaddr); j = iareg - i & 0xFFFF; SETSTATUS(iareg, i, j); break;
    case 130:
      IMM8(); i = M_RDMEM(eaddr); j = iareg - i - (iccreg & 0x1) & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 131:
      IMM16();

      if (iflag == 2) n = iureg; else n = GETDREG();
      i1 = GETWORD(eaddr);
      m = n - i1;
      SETSTATUSD(n, i1, m);
      if (iflag == 0) SETDREG(m);

      break;
    case 132:
      IMM8(); iareg &= M_RDMEM(eaddr); SETNZ8(iareg); CLV(); break;
    case 133:
      IMM8(); k = iareg & M_RDMEM(eaddr); SETNZ8(k); CLV(); break;
    case 134:
      IMM8(); iareg = LOADAC(); CLV(); SETNZ8(iareg); break;
    case 136:
      IMM8(); iareg ^= M_RDMEM(eaddr); SETNZ8(iareg); CLV(); break;
    case 137:
      IMM8(); i = M_RDMEM(eaddr); j = iareg + i + (iccreg & 0x1) & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 138:
      IMM8(); iareg |= M_RDMEM(eaddr); SETNZ8(iareg); CLV(); break;
    case 139:
      IMM8(); i = M_RDMEM(eaddr); j = iareg + i & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 140:
      IMM16();

      if (iflag == 0) m = ixreg;
      else if (iflag == 1) m = iyreg; else
        m = isreg;
      n = GETWORD(eaddr);
      i1 = m - n;
      SETSTATUSD(m, n, i1);

      break;
    case 141:
      k = IMMBYTE(); PUSHWORD(ipcreg); ipcreg = ipcreg + (byte)k & 0xFFFF; break;
    case 142:
      IMM16(); j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) ixreg = j; else iyreg = j; break;
    case 135:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = 8X " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_9X()
  {
    int i;
    int j;
    int n;
    int i1;
    int m;
    switch (ireg) {
    case 144:
      DIRECT(); i = M_RDMEM(eaddr); j = iareg - i & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 145:
      DIRECT(); i = M_RDMEM(eaddr); j = iareg - i & 0xFFFF; SETSTATUS(iareg, i, j); break;
    case 147:
      DIRECT();

      if (iflag == 2) n = iureg; else
        n = GETDREG();
      i1 = GETWORD(eaddr);
      m = n - i1;
      SETSTATUSD(n, i1, m);
      if (iflag == 0) SETDREG(m);

      break;
    case 148:
      DIRECT(); iareg &= M_RDMEM(eaddr); SETNZ8(iareg); CLV(); break;
    case 149:
      DIRECT(); int k = iareg & M_RDMEM(eaddr); SETNZ8(k); CLV(); break;
    case 150:
      DIRECT(); iareg = LOADAC(); CLV(); SETNZ8(iareg); break;
    case 151:
      DIRECT(); SETNZ8(iareg); CLV(); STOREAC(iareg); break;
    case 152:
      DIRECT(); iareg ^= M_RDMEM(eaddr); SETNZ8(iareg); CLV(); break;
    case 153:
      DIRECT(); i = M_RDMEM(eaddr); j = iareg + i + (iccreg & 0x1) & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 154:
      DIRECT(); iareg |= M_RDMEM(eaddr); SETNZ8(iareg); CLV(); break;
    case 155:
      DIRECT(); i = M_RDMEM(eaddr); j = iareg + i & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 156:
      DIRECT();

      if (iflag == 0) m = ixreg;
      else if (iflag == 1) m = iyreg; else
        m = isreg;
      n = GETWORD(eaddr);
      i1 = m - n;
      SETSTATUSD(m, n, i1);

      break;
    case 158:
      DIRECT(); j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) ixreg = j; else iyreg = j; break;
    case 159:
      DIRECT(); if (iflag == 0) j = ixreg; else j = iyreg; CLV(); SETNZ16(j); SETWORD(eaddr, j); break;
    case 146:
    case 157:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = 9X " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_AX()
  {
    int i;
    int j;
    int m;
    int n;
    int k;
    switch (ireg) {
    case 160:
      i = M_RDMEM(eaddr); j = iareg - i & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 161:
      i = M_RDMEM(eaddr); j = iareg - i & 0xFFFF; SETSTATUS(iareg, i, j); break;
    case 162:
      i = M_RDMEM(eaddr); j = iareg - i - (iccreg & 0x1) & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 163:
      if (iflag == 2) m = iureg; else
        m = GETDREG();
      n = GETWORD(eaddr);
      k = m - n;
      SETSTATUSD(m, n, k);
      if (iflag == 0) SETDREG(k);

      break;
    case 164:
      iareg &= M_RDMEM(eaddr); SETNZ8(iareg); CLV(); break;
    case 166:
      iareg = LOADAC(); CLV(); SETNZ8(iareg); break;
    case 167:
      SETNZ8(iareg); CLV(); STOREAC(iareg); break;
    case 168:
      iareg ^= M_RDMEM(eaddr); SETNZ8(iareg); CLV(); break;
    case 169:
      i = M_RDMEM(eaddr); j = iareg + i + (iccreg & 0x1) & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 170:
      iareg |= M_RDMEM(eaddr); SETNZ8(iareg); CLV(); break;
    case 171:
      i = M_RDMEM(eaddr); j = iareg + i & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 172:
      if (iflag == 0) k = ixreg;
      else if (iflag == 1) k = iyreg; else
        k = isreg;
      m = GETWORD(eaddr);
      n = k - m;
      SETSTATUSD(k, m, n);

      break;
    case 173:
      PUSHWORD(ipcreg); ipcreg = eaddr; break;
    case 174:
      j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) ixreg = j; else iyreg = j; break;
    case 175:
      if (iflag == 0) j = ixreg; else j = iyreg; CLV(); SETNZ16(j); SETWORD(eaddr, j); break;
    case 165:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = AX " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_BX()
  {
    int i;
    int j;
    int n;
    int i1;
    int m;
    switch (ireg) {
    case 176:
      EXTENDED(); i = M_RDMEM(eaddr); j = iareg - i & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 177:
      EXTENDED(); i = M_RDMEM(eaddr); j = iareg - i & 0xFFFF; SETSTATUS(iareg, i, j); break;
    case 179:
      EXTENDED();

      if (iflag == 2) n = iureg; else
        n = GETDREG();
      i1 = GETWORD(eaddr);
      m = n - i1;
      SETSTATUSD(n, i1, m);
      if (iflag == 0) SETDREG(m);

      break;
    case 180:
      EXTENDED(); iareg &= M_RDMEM(eaddr); SETNZ8(iareg); CLV(); break;
    case 181:
      EXTENDED(); int k = iareg & M_RDMEM(eaddr); SETNZ8(k); CLV(); break;
    case 182:
      EXTENDED(); iareg = LOADAC(); CLV(); SETNZ8(iareg); break;
    case 183:
      EXTENDED(); SETNZ8(iareg); CLV(); STOREAC(iareg); break;
    case 184:
      EXTENDED(); iareg ^= M_RDMEM(eaddr); SETNZ8(iareg); CLV(); break;
    case 185:
      EXTENDED(); i = M_RDMEM(eaddr); j = iareg + i + (iccreg & 0x1) & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 186:
      EXTENDED(); iareg |= M_RDMEM(eaddr); SETNZ8(iareg); CLV(); break;
    case 187:
      EXTENDED(); i = M_RDMEM(eaddr); j = iareg + i & 0xFFFF; SETSTATUS(iareg, i, j); iareg = j & 0xFF; break;
    case 189:
      EXTENDED(); PUSHWORD(ipcreg); ipcreg = eaddr; break;
    case 188:
      EXTENDED();

      if (iflag == 0) m = ixreg;
      else if (iflag == 1) m = iyreg; else
        m = isreg;
      n = GETWORD(eaddr);
      i1 = m - n;
      SETSTATUSD(m, n, i1);

      break;
    case 190:
      EXTENDED(); j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) ixreg = j; else iyreg = j; break;
    case 191:
      EXTENDED(); if (iflag == 0) j = ixreg; else j = iyreg; CLV(); SETNZ16(j); SETWORD(eaddr, j); break;
    case 178:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = BX " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_CX()
  {
    int i;
    int j;
    switch (ireg) {
    case 192:
      IMM8(); i = M_RDMEM(eaddr); j = ibreg - i & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 193:
      IMM8(); i = M_RDMEM(eaddr); j = ibreg - i & 0xFFFF; SETSTATUS(ibreg, i, j); break;
    case 194:
      IMM8(); i = M_RDMEM(eaddr); j = ibreg - i - (iccreg & 0x1) & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 195:
      IMM16();

      int n = GETDREG();
      int i1 = GETWORD(eaddr);
      int m = n + i1;
      SETSTATUSD(n, i1, m);
      SETDREG(m);

      break;
    case 196:
      IMM8(); ibreg &= M_RDMEM(eaddr); SETNZ8(ibreg); CLV(); break;
    case 197:
      IMM8(); int k = ibreg & M_RDMEM(eaddr); SETNZ8(k); CLV(); break;
    case 198:
      IMM8(); ibreg = LOADAC(); CLV(); SETNZ8(ibreg); break;
    case 200:
      IMM8(); ibreg ^= M_RDMEM(eaddr); SETNZ8(ibreg); CLV(); break;
    case 201:
      IMM8(); i = M_RDMEM(eaddr); j = ibreg + i + (iccreg & 0x1) & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 202:
      IMM8(); ibreg |= M_RDMEM(eaddr); SETNZ8(ibreg); CLV(); break;
    case 203:
      IMM8(); i = M_RDMEM(eaddr); j = ibreg + i & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 204:
      IMM16(); j = GETWORD(eaddr); SETNZ16(j); CLV(); SETDREG(j); break;
    case 206:
      IMM16(); j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) iureg = j; else isreg = j; break;
    case 199:
    case 205:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = CX " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_DX()
  {
    int i;
    int j;
    switch (ireg) {
    case 208:
      DIRECT(); i = M_RDMEM(eaddr); j = ibreg - i & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 209:
      DIRECT(); i = M_RDMEM(eaddr); j = ibreg - i & 0xFFFF; SETSTATUS(ibreg, i, j); break;
    case 210:
      DIRECT(); i = M_RDMEM(eaddr); j = ibreg - i - (iccreg & 0x1) & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 211:
      DIRECT();

      int n = GETDREG();
      int i1 = GETWORD(eaddr);
      int m = n + i1;
      SETSTATUSD(n, i1, m);
      SETDREG(m);

      break;
    case 212:
      DIRECT(); ibreg &= M_RDMEM(eaddr); SETNZ8(ibreg); CLV(); break;
    case 213:
      DIRECT(); int k = ibreg & M_RDMEM(eaddr); SETNZ8(k); CLV(); break;
    case 214:
      DIRECT(); ibreg = LOADAC(); CLV(); SETNZ8(ibreg); break;
    case 215:
      DIRECT(); SETNZ8(ibreg); CLV(); STOREAC(ibreg); break;
    case 216:
      DIRECT(); ibreg ^= M_RDMEM(eaddr); SETNZ8(ibreg); CLV(); break;
    case 217:
      DIRECT(); i = M_RDMEM(eaddr); j = ibreg + i + (iccreg & 0x1) & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 218:
      DIRECT(); ibreg |= M_RDMEM(eaddr); SETNZ8(ibreg); CLV(); break;
    case 219:
      DIRECT(); i = M_RDMEM(eaddr); j = ibreg + i & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 220:
      DIRECT(); j = GETWORD(eaddr); SETNZ16(j); CLV(); SETDREG(j); break;
    case 221:
      DIRECT(); j = GETDREG(); SETNZ16(j); CLV(); SETWORD(eaddr, j); break;
    case 222:
      DIRECT(); j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) iureg = j; else isreg = j; break;
    case 223:
      DIRECT(); if (iflag == 0) j = iureg; else j = isreg; CLV(); SETNZ16(j); SETWORD(eaddr, j); break;
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = DX " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_EX()
  {
    int i;
    int j;
    switch (ireg) {
    case 224:
      i = M_RDMEM(eaddr); j = ibreg - i & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 225:
      i = M_RDMEM(eaddr); j = ibreg - i & 0xFFFF; SETSTATUS(ibreg, i, j); break;
    case 226:
      i = M_RDMEM(eaddr); j = ibreg - i - (iccreg & 0x1) & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 227:
      int n = GETDREG();
      int i1 = GETWORD(eaddr);
      int m = n + i1;
      SETSTATUSD(n, i1, m);
      SETDREG(m);

      break;
    case 228:
      ibreg &= M_RDMEM(eaddr); SETNZ8(ibreg); CLV(); break;
    case 229:
      int k = ibreg & M_RDMEM(eaddr); SETNZ8(k); CLV(); break;
    case 230:
      ibreg = LOADAC(); CLV(); SETNZ8(ibreg); break;
    case 231:
      SETNZ8(ibreg); CLV(); STOREAC(ibreg); break;
    case 232:
      ibreg ^= M_RDMEM(eaddr); SETNZ8(ibreg); CLV(); break;
    case 233:
      i = M_RDMEM(eaddr); j = ibreg + i + (iccreg & 0x1) & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 234:
      ibreg |= M_RDMEM(eaddr); SETNZ8(ibreg); CLV(); break;
    case 235:
      i = M_RDMEM(eaddr); j = ibreg + i & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 236:
      j = GETWORD(eaddr); SETNZ16(j); CLV(); SETDREG(j); break;
    case 237:
      j = GETDREG(); SETNZ16(j); CLV(); SETWORD(eaddr, j); break;
    case 238:
      j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) iureg = j; else isreg = j; break;
    case 239:
      if (iflag == 0) j = iureg; else j = isreg; CLV(); SETNZ16(j); SETWORD(eaddr, j); break;
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = EX " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_FX()
  {
    int i;
    int j;
    switch (ireg) {
    case 240:
      EXTENDED(); i = M_RDMEM(eaddr); j = ibreg - i & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 241:
      EXTENDED(); i = M_RDMEM(eaddr); j = ibreg - i & 0xFFFF; SETSTATUS(ibreg, i, j); break;
    case 242:
      EXTENDED(); i = M_RDMEM(eaddr); j = ibreg - i - (iccreg & 0x1) & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 243:
      EXTENDED();

      int n = GETDREG();
      int i1 = GETWORD(eaddr);
      int m = n + i1;
      SETSTATUSD(n, i1, m);
      SETDREG(m);

      break;
    case 244:
      EXTENDED(); ibreg &= M_RDMEM(eaddr); SETNZ8(ibreg); CLV(); break;
    case 245:
      EXTENDED(); int k = ibreg & M_RDMEM(eaddr); SETNZ8(k); CLV(); break;
    case 246:
      EXTENDED(); ibreg = LOADAC(); CLV(); SETNZ8(ibreg); break;
    case 247:
      EXTENDED(); SETNZ8(ibreg); CLV(); STOREAC(ibreg); break;
    case 249:
      EXTENDED(); i = M_RDMEM(eaddr); j = ibreg + i + (iccreg & 0x1) & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 250:
      EXTENDED(); ibreg |= M_RDMEM(eaddr); SETNZ8(ibreg); CLV(); break;
    case 251:
      EXTENDED(); i = M_RDMEM(eaddr); j = ibreg + i & 0xFFFF; SETSTATUS(ibreg, i, j); ibreg = j & 0xFF; break;
    case 252:
      EXTENDED(); j = GETWORD(eaddr); SETNZ16(j); CLV(); SETDREG(j); break;
    case 253:
      EXTENDED(); j = GETDREG(); SETNZ16(j); CLV(); SETWORD(eaddr, j); break;
    case 254:
      EXTENDED(); j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) iureg = j; else isreg = j; break;
    case 255:
      EXTENDED(); if (iflag == 0) j = iureg; else j = isreg; CLV(); SETNZ16(j); SETWORD(eaddr, j); break;
    case 248:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(ipcreg) + " OPCODE = FX " + Integer.toHexString(ireg));
      break;
    }
  }
}
