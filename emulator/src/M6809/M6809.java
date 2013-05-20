

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
  static int xreg;
  static int yreg;
  static int ureg;
  static int sreg;
  static int pcreg;
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
    int i = rd_op_handler.handler(pcreg); pcreg = pcreg + 1 & 0xFFFF; return i; }
  static final int IMMWORD() { int i = rd_op_handler_wd.handler(pcreg); pcreg = pcreg + 2 & 0xFFFF; return i; }
  static final void PUSHBYTE(int paramInt) { sreg = sreg - 1 & 0xFFFF; wr_s_handler.handler(sreg, paramInt); }
  static final void PUSHWORD(int paramInt) { sreg = sreg - 2 & 0xFFFF; wr_s_handler_wd.handler(sreg, paramInt); }
  static final int PULLBYTE() { int i = rd_s_handler.handler(sreg); sreg = sreg + 1 & 0xFFFF; return i; }
  static final int PULLWORD() { int i = rd_s_handler_wd.handler(sreg); sreg = sreg + 2 & 0xFFFF; return i; }
  static final void PSHUBYTE(int paramInt) { ureg = ureg - 1 & 0xFFFF; wr_u_handler.handler(ureg, paramInt); }
  static final void PSHUWORD(int paramInt) { ureg = ureg - 2 & 0xFFFF; wr_u_handler_wd.handler(ureg, paramInt); }
  static final int PULUBYTE() { int i = rd_u_handler.handler(ureg); ureg = ureg + 1 & 0xFFFF; return i; }
  static final int PULUWORD() { int i = rd_u_handler_wd.handler(ureg); ureg = ureg + 2 & 0xFFFF; return i;
  }

  static final int GETDREG()
  {
    return iareg << 8 | ibreg; }
  static final void SETDREG(int paramInt) { iareg = paramInt >> 8 & 0xFF; ibreg = paramInt & 0xFF; }

  static final void DIRECT() {
    eaddr = IMMBYTE(); eaddr |= idpreg << 8; }
  static final void IMM8() { eaddr = pcreg; pcreg = pcreg + 1 & 0xFFFF; }
  static final void IMM16() { eaddr = pcreg; pcreg = pcreg + 2 & 0xFFFF; }
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
    if (iflag == 0) { i = IMMBYTE(); if (paramInt != 0) pcreg = pcreg + (byte)i & 0xFFFF;  } else {
      i = IMMWORD(); if (paramInt != 0) pcreg = pcreg + i & 0xFFFF; m6809_ICount -= 2; }
  }
  static final int NXORV() { return iccreg & 0x8 ^ (iccreg & 0x2) << 2;
  }

  static final int GETREG(int paramInt)
  {
    switch (paramInt) {
    case 0:
      return GETDREG();
    case 1:
      return xreg;
    case 2:
      return yreg;
    case 3:
      return ureg;
    case 4:
      return sreg;
    case 5:
      return pcreg;
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
      xreg = paramInt1; break;
    case 2:
      yreg = paramInt1; break;
    case 3:
      ureg = paramInt1; break;
    case 4:
      sreg = paramInt1; break;
    case 5:
      pcreg = paramInt1; break;
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
    pcreg = paramm6809_Regs.pc;
    ureg = paramm6809_Regs.u;
    sreg = paramm6809_Regs.s;
    xreg = paramm6809_Regs.x;
    yreg = paramm6809_Regs.y;
    idpreg = paramm6809_Regs.dp;
    iareg = paramm6809_Regs.a;
    ibreg = paramm6809_Regs.b;
    iccreg = paramm6809_Regs.cc;
  }

  public static void m6809_GetRegs(M6809H.m6809_Regs paramm6809_Regs)
  {
    paramm6809_Regs.pc = pcreg;
    paramm6809_Regs.u = ureg;
    paramm6809_Regs.s = sreg;
    paramm6809_Regs.x = xreg;
    paramm6809_Regs.y = yreg;
    paramm6809_Regs.dp = idpreg;
    paramm6809_Regs.a = iareg;
    paramm6809_Regs.b = ibreg;
    paramm6809_Regs.cc = iccreg;
  }

  public static int m6809_GetPC()
  {
    return pcreg;
  }

  static void m6809_Interrupt()
  {
    if ((iccreg & 0x10) == 0)
    {
      PUSHWORD(pcreg);
      PUSHWORD(ureg);
      PUSHWORD(yreg);
      PUSHWORD(xreg);
      PUSHBYTE(idpreg);
      PUSHBYTE(ibreg);
      PUSHBYTE(iareg);
      PUSHBYTE(iccreg);
      iccreg |= 144;
      pcreg = GETWORD(65528);
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
      PUSHWORD(pcreg);
      PUSHBYTE(iccreg);
      iccreg &= 127;
      iccreg |= 80;
      pcreg = GETWORD(65526);
      m6809_ICount -= 10;
    }
  }

  public static void m6809_reset()
  {
    pcreg = M_RDMEM_WORD(65534);

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
        if (fastopcodes != 0) ireg = M_RDOP(pcreg); else
          ireg = M_RDMEM(pcreg);
        pcreg = pcreg + 1 & 0xFFFF;

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
    int postbyte;
    postbyte = M_RDOP(pcreg); 
    pcreg = pcreg + 1 & 0xFFFF; /*convert result to word */

    switch (postbyte) 
    {
            case 0x00: eaddr=xreg;break;
	    case 0x01: eaddr=xreg+1 & 0xFFFF;break;
	    case 0x02: eaddr=xreg+2 & 0xFFFF;break;
	    case 0x03: eaddr=xreg+3 & 0xFFFF;break;
	    case 0x04: eaddr=xreg+4 & 0xFFFF;break;
	    case 0x05: eaddr=xreg+5 & 0xFFFF;break;
	    case 0x06: eaddr=xreg+6 & 0xFFFF;break;
	    case 0x07: eaddr=xreg+7 & 0xFFFF;break;
	    case 0x08: eaddr=xreg+8 & 0xFFFF;break;
	    case 0x09: eaddr=xreg+9 & 0xFFFF;break;
	    case 0x0A: eaddr=xreg+10 & 0xFFFF;break;
	    case 0x0B: eaddr=xreg+11 & 0xFFFF;break;
	    case 0x0C: eaddr=xreg+12 & 0xFFFF;break;
	    case 0x0D: eaddr=xreg+13 & 0xFFFF;break;
	    case 0x0E: eaddr=xreg+14 & 0xFFFF;break;
	    case 0x0F: eaddr=xreg+15 & 0xFFFF;break;
	    case 0x10: eaddr=xreg-16 & 0xFFFF;break;
	    case 0x11: eaddr=xreg-15 & 0xFFFF;break;
	    case 0x12: eaddr=xreg-14 & 0xFFFF;break;
	    case 0x13: eaddr=xreg-13 & 0xFFFF;break;
	    case 0x14: eaddr=xreg-12 & 0xFFFF;break;
	    case 0x15: eaddr=xreg-11 & 0xFFFF;break;
	    case 0x16: eaddr=xreg-10 & 0xFFFF;break;
	    case 0x17: eaddr=xreg-9 & 0xFFFF;break;
	    case 0x18: eaddr=xreg-8 & 0xFFFF;break;
	    case 0x19: eaddr=xreg-7 & 0xFFFF;break;
	    case 0x1A: eaddr=xreg-6 & 0xFFFF;break;
	    case 0x1B: eaddr=xreg-5 & 0xFFFF;break;
	    case 0x1C: eaddr=xreg-4 & 0xFFFF;break;
	    case 0x1D: eaddr=xreg-3 & 0xFFFF;break;
	    case 0x1E: eaddr=xreg-2 & 0xFFFF;break;
	    case 0x1F: eaddr=xreg-1 & 0xFFFF;break;
 	    case 0x20: eaddr=yreg;break;
	    case 0x21: eaddr=yreg+1 & 0xFFFF; break;
	    case 0x22: eaddr=yreg+2 & 0xFFFF;break;
	    case 0x23: eaddr=yreg+3 & 0xFFFF;break;
	    case 0x24: eaddr=yreg+4 & 0xFFFF;break;
	    case 0x25: eaddr=yreg+5 & 0xFFFF;break;
	    case 0x26: eaddr=yreg+6 & 0xFFFF;break;
	    case 0x27: eaddr=yreg+7 & 0xFFFF;break;
	    case 0x28: eaddr=yreg+8 & 0xFFFF;break;
	    case 0x29: eaddr=yreg+9 & 0xFFFF;break;
	    case 0x2A: eaddr=yreg+10 & 0xFFFF;break;
	    case 0x2B: eaddr=yreg+11 & 0xFFFF;break;
	    case 0x2C: eaddr=yreg+12 & 0xFFFF;break;
	    case 0x2D: eaddr=yreg+13 & 0xFFFF;break;
	    case 0x2E: eaddr=yreg+14 & 0xFFFF;break;
	    case 0x2F: eaddr=yreg+15 & 0xFFFF;break;
	    case 0x30: eaddr=yreg-16 & 0xFFFF;break;
	    case 0x31: eaddr=yreg-15 & 0xFFFF;break;
	    case 0x32: eaddr=yreg-14 & 0xFFFF;break;
	    case 0x33: eaddr=yreg-13 & 0xFFFF;break;
	    case 0x34: eaddr=yreg-12 & 0xFFFF;break;
	    case 0x35: eaddr=yreg-11 & 0xFFFF;break;
	    case 0x36: eaddr=yreg-10 & 0xFFFF;break;
	    case 0x37: eaddr=yreg-9 & 0xFFFF;break;
	    case 0x38: eaddr=yreg-8 & 0xFFFF;break;
	    case 0x39: eaddr=yreg-7 & 0xFFFF;break;
	    case 0x3A: eaddr=yreg-6 & 0xFFFF;break;
	    case 0x3B: eaddr=yreg-5 & 0xFFFF;break;
	    case 0x3C: eaddr=yreg-4 & 0xFFFF;break;
	    case 0x3D: eaddr=yreg-3 & 0xFFFF;break;
	    case 0x3E: eaddr=yreg-2 & 0xFFFF;break;
	    case 0x3F: eaddr=yreg-1 & 0xFFFF;break;         
	    case 0x40: eaddr=ureg;break;
	    case 0x41: eaddr=ureg+1 & 0xFFFF;break;
	    case 0x42: eaddr=ureg+2 & 0xFFFF;break;
	    case 0x43: eaddr=ureg+3 & 0xFFFF;break;
	    case 0x44: eaddr=ureg+4 & 0xFFFF;break;
	    case 0x45: eaddr=ureg+5 & 0xFFFF;break;
	    case 0x46: eaddr=ureg+6 & 0xFFFF;break;
	    case 0x47: eaddr=ureg+7 & 0xFFFF;break;
	    case 0x48: eaddr=ureg+8 & 0xFFFF;break;
	    case 0x49: eaddr=ureg+9 & 0xFFFF;break;
	    case 0x4A: eaddr=ureg+10 & 0xFFFF;break;
	    case 0x4B: eaddr=ureg+11 & 0xFFFF;break;
	    case 0x4C: eaddr=ureg+12 & 0xFFFF;break;
	    case 0x4D: eaddr=ureg+13 & 0xFFFF;break;
	    case 0x4E: eaddr=ureg+14 & 0xFFFF;break;
	    case 0x4F: eaddr=ureg+15 & 0xFFFF;break;
	    case 0x50: eaddr=ureg-16 & 0xFFFF;break;
	    case 0x51: eaddr=ureg-15 & 0xFFFF;break;
	    case 0x52: eaddr=ureg-14 & 0xFFFF;break;
	    case 0x53: eaddr=ureg-13 & 0xFFFF;break;
	    case 0x54: eaddr=ureg-12 & 0xFFFF;break;
	    case 0x55: eaddr=ureg-11 & 0xFFFF;break;
	    case 0x56: eaddr=ureg-10 & 0xFFFF;break;
	    case 0x57: eaddr=ureg-9 & 0xFFFF;break;
	    case 0x58: eaddr=ureg-8 & 0xFFFF;break;
	    case 0x59: eaddr=ureg-7 & 0xFFFF;break;
	    case 0x5A: eaddr=ureg-6 & 0xFFFF;break;
	    case 0x5B: eaddr=ureg-5 & 0xFFFF;break;
	    case 0x5C: eaddr=ureg-4 & 0xFFFF;break;
	    case 0x5D: eaddr=ureg-3 & 0xFFFF;break;
	    case 0x5E: eaddr=ureg-2 & 0xFFFF;break;
	    case 0x5F: eaddr=ureg-1 & 0xFFFF;break;
	    case 0x60: eaddr=sreg;break;
	    case 0x61: eaddr=sreg+1 & 0xFFFF;break;
	    case 0x62: eaddr=sreg+2 & 0xFFFF;break;
	    case 0x63: eaddr=sreg+3 & 0xFFFF;break;
	    case 0x64: eaddr=sreg+4 & 0xFFFF;break;
	    case 0x65: eaddr=sreg+5 & 0xFFFF;break;
	    case 0x66: eaddr=sreg+6 & 0xFFFF;break;
	    case 0x67: eaddr=sreg+7 & 0xFFFF;break;
	    case 0x68: eaddr=sreg+8 & 0xFFFF;break;
	    case 0x69: eaddr=sreg+9 & 0xFFFF;break;
	    case 0x6A: eaddr=sreg+10 & 0xFFFF;break;
	    case 0x6B: eaddr=sreg+11 & 0xFFFF;break;
	    case 0x6C: eaddr=sreg+12 & 0xFFFF;break;
	    case 0x6D: eaddr=sreg+13 & 0xFFFF;break;
	    case 0x6E: eaddr=sreg+14 & 0xFFFF;break;
	    case 0x6F: eaddr=sreg+15 & 0xFFFF;break;
	    case 0x70: eaddr=sreg-16 & 0xFFFF;break;
	    case 0x71: eaddr=sreg-15 & 0xFFFF;break;
	    case 0x72: eaddr=sreg-14 & 0xFFFF;break;
	    case 0x73: eaddr=sreg-13 & 0xFFFF;break;
	    case 0x74: eaddr=sreg-12 & 0xFFFF;break;
	    case 0x75: eaddr=sreg-11 & 0xFFFF;break;
	    case 0x76: eaddr=sreg-10 & 0xFFFF;break;
	    case 0x77: eaddr=sreg-9 & 0xFFFF;break;
	    case 0x78: eaddr=sreg-8 & 0xFFFF;break;
	    case 0x79: eaddr=sreg-7 & 0xFFFF;break;
	    case 0x7A: eaddr=sreg-6 & 0xFFFF;break;
	    case 0x7B: eaddr=sreg-5 & 0xFFFF;break;
	    case 0x7C: eaddr=sreg-4 & 0xFFFF;break;
	    case 0x7D: eaddr=sreg-3 & 0xFFFF;break;
	    case 0x7E: eaddr=sreg-2 & 0xFFFF;break;
	    case 0x7F: eaddr=sreg-1 & 0xFFFF;break;
   	    case 0x80: eaddr=xreg; xreg = xreg + 1 & 0xFFFF;  break;
	    case 0x81: eaddr=xreg; xreg = xreg + 2 & 0xFFFF;break;
	    case 0x82: xreg = xreg - 1 & 0xFFFF; eaddr=xreg;break;
	    case 0x83: xreg = xreg - 2 & 0xFFFF; eaddr=xreg;break;
            case 0x84: eaddr=xreg;break;

         
    case 133:
      eaddr = xreg + (byte)ibreg & 0xFFFF; break;
    case 134:
      eaddr = xreg + (byte)iareg & 0xFFFF; break;
    case 136:
      eaddr = IMMBYTE(); eaddr = xreg + (byte)eaddr & 0xFFFF; break;
    case 137:
      eaddr = IMMWORD(); eaddr = eaddr + xreg & 0xFFFF; break;
    case 139:
      eaddr = xreg + GETDREG() & 0xFFFF; break;
    case 145:
      eaddr = xreg; xreg = xreg + 2 & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 148:
      eaddr = xreg; eaddr = GETWORD(eaddr); break;
    case 149:
      eaddr = xreg + (byte)ibreg & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 150:
      eaddr = xreg + (byte)iareg & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 152:
      eaddr = IMMBYTE(); eaddr = xreg + (byte)eaddr & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 155:
      eaddr = xreg + GETDREG() & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 159:
      eaddr = IMMWORD(); eaddr = GETWORD(eaddr); break;
    case 160:
      eaddr = yreg; yreg = yreg + 1 & 0xFFFF; break;
    case 161:
      eaddr = yreg; yreg = yreg + 2 & 0xFFFF; break;
    case 162:
      yreg = yreg - 1 & 0xFFFF; eaddr = yreg; break;
    case 163:
      yreg = yreg - 2 & 0xFFFF; eaddr = yreg; break;
    case 164:
      eaddr = yreg; break;
    case 165:
      eaddr = yreg + (byte)ibreg & 0xFFFF; break;
    case 166:
      eaddr = yreg + (byte)iareg & 0xFFFF; break;
    case 168:
      eaddr = IMMBYTE(); eaddr = yreg + (byte)eaddr & 0xFFFF; break;
    case 169:
      eaddr = IMMWORD(); eaddr = eaddr + yreg & 0xFFFF; break;
    case 170:
      eaddr = 0; break;
    case 171:
      eaddr = yreg + GETDREG() & 0xFFFF; break;
    case 177:
      eaddr = yreg; yreg = yreg + 2 & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 180:
      eaddr = yreg; eaddr = GETWORD(eaddr); break;
    case 181:
      eaddr = yreg + (byte)ibreg & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 182:
      eaddr = yreg + (byte)iareg & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 184:
      eaddr = IMMBYTE(); eaddr = yreg + (byte)eaddr & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 192:
      eaddr = ureg; ureg = ureg + 1 & 0xFFFF; break;
    case 193:
      eaddr = ureg; ureg = ureg + 2 & 0xFFFF; break;
    case 194:
      ureg = ureg - 1 & 0xFFFF; eaddr = ureg; break;
    case 195:
      ureg = ureg - 2 & 0xFFFF; eaddr = ureg; break;
    case 196:
      eaddr = ureg; break;
    case 197:
      eaddr = ureg + (byte)ibreg & 0xFFFF; break;
    case 198:
      eaddr = ureg + (byte)iareg & 0xFFFF; break;
    case 200:
      eaddr = IMMBYTE(); eaddr = ureg + (byte)eaddr & 0xFFFF; break;
    case 201:
      eaddr = IMMWORD(); eaddr = eaddr + ureg & 0xFFFF; break;
    case 203:
      eaddr = ureg + GETDREG() & 0xFFFF; break;
    case 209:
      eaddr = ureg; ureg = ureg + 2 & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 212:
      eaddr = ureg; eaddr = GETWORD(eaddr); break;
    case 213:
      eaddr = ureg + (byte)ibreg & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 214:
      eaddr = ureg + (byte)iareg & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 216:
      eaddr = IMMBYTE(); eaddr = ureg + (byte)eaddr & 0xFFFF; eaddr = GETWORD(eaddr); break;
    case 224:
      eaddr = sreg; sreg = sreg + 1 & 0xFFFF; break;
    case 225:
      eaddr = sreg; sreg = sreg + 2 & 0xFFFF; break;
    case 226:
      sreg = sreg - 1 & 0xFFFF; eaddr = sreg; break;
    case 227:
      sreg = sreg - 2 & 0xFFFF; eaddr = sreg; break;
    case 228:
      eaddr = sreg; break;
    case 232:
      eaddr = IMMBYTE(); eaddr = sreg + (byte)eaddr & 0xFFFF; break;
    case 233:
      eaddr = IMMWORD(); eaddr = eaddr + sreg & 0xFFFF; break;
    case 238:
      eaddr = 0; break;
    case 244:
      eaddr = sreg; eaddr = GETWORD(eaddr); break;
    case 248:
      eaddr = IMMBYTE(); eaddr = sreg + (byte)eaddr & 0xFFFF; eaddr = GETWORD(eaddr); break;
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
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = FEA " + Integer.toHexString(i));
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
      DIRECT(); pcreg = eaddr; break;
    case 15:
      DIRECT(); SETBYTE(eaddr, 0); CLN(); CLV(); SEZ(); CLC(); break;
    case 5:
    case 7:
    case 11:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = 0X " + Integer.toHexString(ireg));
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
      eaddr = IMMWORD(); pcreg = pcreg + eaddr & 0xFFFF; break;
    case 23:
      eaddr = IMMWORD(); PUSHWORD(pcreg); pcreg = pcreg + eaddr & 0xFFFF; break;
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
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = 1X " + Integer.toHexString(ireg));
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
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = 2X " + Integer.toHexString(ireg));
      break;
    }
  }

  static final void codes_3X()
  {
    int j;
    switch (ireg) {
    case 48:
      xreg = eaddr; if (xreg != 0) CLZ(); else SEZ(); break;
    case 49:
      yreg = eaddr; if (yreg != 0) CLZ(); else SEZ(); break;
    case 50:
      sreg = eaddr; break;
    case 51:
      ureg = eaddr; break;
    case 52:
      j = IMMBYTE();
      if ((j & 0x80) != 0) PUSHWORD(pcreg);
      if ((j & 0x40) != 0) PUSHWORD(ureg);
      if ((j & 0x20) != 0) PUSHWORD(yreg);
      if ((j & 0x10) != 0) PUSHWORD(xreg);
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
      if ((j & 0x10) != 0) xreg = PULLWORD();
      if ((j & 0x20) != 0) yreg = PULLWORD();
      if ((j & 0x40) != 0) ureg = PULLWORD();
      if ((j & 0x80) == 0) break; pcreg = PULLWORD(); break;
    case 54:
      j = IMMBYTE();
      if ((j & 0x80) != 0) PSHUWORD(pcreg);
      if ((j & 0x40) != 0) PSHUWORD(sreg);
      if ((j & 0x20) != 0) PSHUWORD(yreg);
      if ((j & 0x10) != 0) PSHUWORD(xreg);
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
      if ((j & 0x10) != 0) xreg = PULUWORD();
      if ((j & 0x20) != 0) yreg = PULUWORD();
      if ((j & 0x40) != 0) sreg = PULUWORD();
      if ((j & 0x80) == 0) break; pcreg = PULUWORD(); break;
    case 57:
      pcreg = PULLWORD(); break;
    case 58:
      xreg = xreg + ibreg & 0xFFFF; break;
    case 59:
      j = iccreg & 0x80;
      iccreg = PULLBYTE();
      if (j != 0)
      {
        m6809_ICount -= 9;
        iareg = PULLBYTE();
        ibreg = PULLBYTE();
        idpreg = PULLBYTE();
        xreg = PULLWORD();
        yreg = PULLWORD();
        ureg = PULLWORD();
      }
      pcreg = PULLWORD();
      break;
    case 61:
      int i = iareg * ibreg & 0xFFFF; if (i != 0) CLZ(); else SEZ(); if ((i & 0x80) != 0) SEC(); else CLC(); SETDREG(i); break;
    case 56:
    case 60:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = 3X " + Integer.toHexString(ireg));
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
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = 4X " + Integer.toHexString(ireg));
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
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = 5X " + Integer.toHexString(ireg));
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
      pcreg = eaddr; break;
    case 111:
      SETBYTE(eaddr, 0); CLN(); CLV(); SEZ(); CLC(); break;
    case 97:
    case 98:
    case 101:
    case 107:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = 6X " + Integer.toHexString(ireg));
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
      EXTENDED(); pcreg = eaddr; break;
    case 127:
      EXTENDED(); SETBYTE(eaddr, 0); CLN(); CLV(); SEZ(); CLC(); break;
    case 117:
    case 119:
    case 123:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = 7X " + Integer.toHexString(ireg));
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

      if (iflag == 2) n = ureg; else n = GETDREG();
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

      if (iflag == 0) m = xreg;
      else if (iflag == 1) m = yreg; else
        m = sreg;
      n = GETWORD(eaddr);
      i1 = m - n;
      SETSTATUSD(m, n, i1);

      break;
    case 141:
      k = IMMBYTE(); PUSHWORD(pcreg); pcreg = pcreg + (byte)k & 0xFFFF; break;
    case 142:
      IMM16(); j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) xreg = j; else yreg = j; break;
    case 135:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = 8X " + Integer.toHexString(ireg));
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

      if (iflag == 2) n = ureg; else
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

      if (iflag == 0) m = xreg;
      else if (iflag == 1) m = yreg; else
        m = sreg;
      n = GETWORD(eaddr);
      i1 = m - n;
      SETSTATUSD(m, n, i1);

      break;
    case 158:
      DIRECT(); j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) xreg = j; else yreg = j; break;
    case 159:
      DIRECT(); if (iflag == 0) j = xreg; else j = yreg; CLV(); SETNZ16(j); SETWORD(eaddr, j); break;
    case 146:
    case 157:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = 9X " + Integer.toHexString(ireg));
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
      if (iflag == 2) m = ureg; else
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
      if (iflag == 0) k = xreg;
      else if (iflag == 1) k = yreg; else
        k = sreg;
      m = GETWORD(eaddr);
      n = k - m;
      SETSTATUSD(k, m, n);

      break;
    case 173:
      PUSHWORD(pcreg); pcreg = eaddr; break;
    case 174:
      j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) xreg = j; else yreg = j; break;
    case 175:
      if (iflag == 0) j = xreg; else j = yreg; CLV(); SETNZ16(j); SETWORD(eaddr, j); break;
    case 165:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = AX " + Integer.toHexString(ireg));
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

      if (iflag == 2) n = ureg; else
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
      EXTENDED(); PUSHWORD(pcreg); pcreg = eaddr; break;
    case 188:
      EXTENDED();

      if (iflag == 0) m = xreg;
      else if (iflag == 1) m = yreg; else
        m = sreg;
      n = GETWORD(eaddr);
      i1 = m - n;
      SETSTATUSD(m, n, i1);

      break;
    case 190:
      EXTENDED(); j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) xreg = j; else yreg = j; break;
    case 191:
      EXTENDED(); if (iflag == 0) j = xreg; else j = yreg; CLV(); SETNZ16(j); SETWORD(eaddr, j); break;
    case 178:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = BX " + Integer.toHexString(ireg));
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
      IMM16(); j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) ureg = j; else sreg = j; break;
    case 199:
    case 205:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = CX " + Integer.toHexString(ireg));
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
      DIRECT(); j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) ureg = j; else sreg = j; break;
    case 223:
      DIRECT(); if (iflag == 0) j = ureg; else j = sreg; CLV(); SETNZ16(j); SETWORD(eaddr, j); break;
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = DX " + Integer.toHexString(ireg));
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
      j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) ureg = j; else sreg = j; break;
    case 239:
      if (iflag == 0) j = ureg; else j = sreg; CLV(); SETNZ16(j); SETWORD(eaddr, j); break;
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = EX " + Integer.toHexString(ireg));
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
      EXTENDED(); j = GETWORD(eaddr); CLV(); SETNZ16(j); if (iflag == 0) ureg = j; else sreg = j; break;
    case 255:
      EXTENDED(); if (iflag == 0) j = ureg; else j = sreg; CLV(); SETNZ16(j); SETWORD(eaddr, j); break;
    case 248:
    default:
      System.out.println("MAIN PC = " + Integer.toHexString(pcreg) + " OPCODE = FX " + Integer.toHexString(ireg));
      break;
    }
  }
}
