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

import static M6502.M6502H.*;
import static M6502.Tables.*;
import static arcadeflex.libc.*;
/**
 *
 * @author shadow
 */
public class M6502 {
  static M6502Regs R;
  static pair J = new pair(); 
  static pair K = new pair();
  static int I;

  static final void MC_Ab(pair Rg){M_LDWORD(Rg); }
  static final void MC_Zp(pair Rg) { Rg.SetL(Op6502(R.PC.W)); R.PC.AddW(1); Rg.SetH(0); }
  static final void MC_Zx(pair Rg) { Rg.SetL(Op6502(R.PC.W) + R.X & 0xFF); R.PC.AddW(1); Rg.SetH(0); }
  static final void MC_Zy(pair Rg) { Rg.SetL(Op6502(R.PC.W) + R.Y & 0xFF); R.PC.AddW(1); Rg.SetH(0); }
  static final void MC_Ax(pair Rg) { M_LDWORD(Rg); Rg.AddW(R.X); }
  static final void MC_Ay(pair Rg) { M_LDWORD(Rg); Rg.AddW(R.Y); }
  static final void MC_Ix(pair Rg) { K.SetL(Op6502(R.PC.W) + R.X & 0xFF); R.PC.AddW(1); K.SetH(0);
                                     Rg.SetL(Op6502(K.W)); K.AddW(1); Rg.SetH(Op6502(K.W)); }
  static final void MC_Iy(pair Rg) { K.SetL(Op6502(R.PC.W)); R.PC.AddW(1); K.SetH(0);
                                     Rg.SetL(Op6502(K.W)); K.AddW(1); Rg.SetH(Op6502(K.W));
                                     Rg.AddW(R.Y);}

  static final int MR_Ab() { MC_Ab(J); return Rd6502(J.W); }
  static final int MR_Im() { int i = Op6502(R.PC.W); R.PC.AddW(1); return i; }
  static final int MR_Zp() { MC_Zp(J); return Rd6502(J.W); }
  static final int MR_Zx() { MC_Zx(J); return Rd6502(J.W); }
  static final int MR_Zy() { MC_Zy(J); return Rd6502(J.W); }
  static final int MR_Ax() { MC_Ax(J); return Rd6502(J.W); }
  static final int MR_Ay() { MC_Ay(J); return Rd6502(J.W); }
  static final int MR_Ix() { MC_Ix(J); return Rd6502(J.W); }
  static final int MR_Iy() { MC_Iy(J); return Rd6502(J.W);
  }

  static final void MW_Ab(int Rg) { MC_Ab(J); Wr6502(J.W, Rg); }
  static final void MW_Zp(int Rg) { MC_Zp(J); Wr6502(J.W, Rg); }
  static final void MW_Zx(int Rg) { MC_Zx(J); Wr6502(J.W, Rg); }
  static final void MW_Zy(int Rg) { MC_Zy(J); Wr6502(J.W, Rg); }
  static final void MW_Ax(int Rg) { MC_Ax(J); Wr6502(J.W, Rg); }
  static final void MW_Ay(int Rg) { MC_Ay(J); Wr6502(J.W, Rg); }
  static final void MW_Ix(int Rg) { MC_Ix(J); Wr6502(J.W, Rg); }
  static final void MW_Iy(int Rg) { MC_Iy(J); Wr6502(J.W, Rg); }

  static final void MM_Ab() { MC_Ab(J); I = Rd6502(J.W); }
  static final void MM_Zp() { MC_Zp(J); I = Rd6502(J.W); }
  static final void MM_Zx() { MC_Zx(J); I = Rd6502(J.W); }
  static final void MM_Ax() { MC_Ax(J); I = Rd6502(J.W); }

  static final void M_FL(int Rg)
  { 
      R.P = (R.P & ~(Z_FLAG | N_FLAG) | ZNTable[Rg]); 
  }
  static final void M_LDWORD(pair Rg) 
  { 
      Rg.SetL(Op6502(R.PC.W)); 
      R.PC.AddW(1); 
      Rg.SetH(Op6502(R.PC.W)); 
      R.PC.AddW(1); 
  }
  static final void M_PUSH(int Rg) 
  {
    Wr6502(0x100 | R.S, Rg); 
    R.S = (R.S - 1 & 0xFF); 
  }
  static final int M_POP() { R.S = (R.S + 1 & 0xFF); return Op6502(0x100 | R.S); }
  static final void M_JR() { R.PC.AddW((byte)Op6502(R.PC.W) + 1); R.ICount -= 1; }

  static final void M_ADC(int Rg) {
    if ((R.P & D_FLAG) != 0)
    {
      K.SetL((R.A & 0xF) + (Rg & 0xF) + (R.P & 0x1) & 0xFF);
      if (K.L > 9) K.AddL(6);
      K.SetH((R.A >> 4) + (Rg >> 4) + (K.L > 15 ? 1 : 0) & 0xFF);
      if (K.H > 9) K.AddH(6);
      R.A = ((K.L & 0xF | K.H << 4) & 0xFF);
      R.P = (R.P & ~C_FLAG | (K.H > 15 ? 1 : 0));
    }
    else
    {
      K.SetW(R.A + Rg + (R.P & 0x1) & 0xFFFF);
      R.P &= ~(N_FLAG | V_FLAG | Z_FLAG | C_FLAG);
      R.P |= (((R.A ^ Rg ^ 0xFFFFFFFF) & (R.A ^ K.L) & 0x80) != 0 ? 64 : 0) | (K.H != 0 ? 1 : 0) | ZNTable[K.L];

      R.A = K.L;
    }
  }

  static final void M_SBC(int Rg)
  {
    if ((R.P & D_FLAG) != 0)
    {
      K.SetL((R.A & 0xF) - (Rg & 0xF) - ((R.P ^ 0xFFFFFFFF) & 0x1) & 0xFF);
      if ((K.L & 0x10) != 0) K.AddL(-6);
      K.SetH((R.A >> 4) - (Rg >> 4) - ((K.L & 0x10) >> 4) & 0xFF);
      if ((K.H & 0x10) != 0) K.AddH(-6);
      R.A = ((K.L & 0xF | K.H << 4) & 0xFF);
      R.P = (R.P & 0xFFFFFFFE | ((K.H & 0x10) != 0 ? 0 : 1));
    }
    else
    {
      K.SetW(R.A - Rg - ((R.P ^ 0xFFFFFFFF) & 0x1) & 0xFFFF);
      R.P &= -196;
      R.P |= (((R.A ^ Rg) & (R.A ^ K.L) & 0x80) != 0 ? 64 : 0) | (K.H != 0 ? 0 : 1) | ZNTable[K.L];

      R.A = K.L;
    }
  }

  static final void M_CMP(int Rg1, int Rg2) {
    K.SetW(Rg1 - Rg2 & 0xFFFF);
    R.P &= ~(N_FLAG | Z_FLAG | C_FLAG);
    R.P |= ZNTable[K.L] | (K.H != 0 ? 0 : 1);
  }
  static final void M_BIT(int Rg) {
    R.P &= -195;
    R.P |= Rg & 0xC0 | ((Rg & R.A) != 0 ? 0 : 2);
  }
  static final void M_AND(int Rg) {
    R.A &= Rg; M_FL(R.A); }
  static final void M_ORA(int Rg) { R.A |= Rg; M_FL(R.A); }
  static final void M_EOR(int Rg) { R.A ^= Rg; M_FL(R.A); }
  static final int M_INC(int Rg) { Rg = Rg + 1 & 0xFF; M_FL(Rg); return Rg; }
  static final int M_DEC(int Rg) { Rg = Rg - 1 & 0xFF; M_FL(Rg); return Rg; }
  static final int M_ASL(int Rg) {
    R.P &= ~C_FLAG; R.P |= Rg >> 7; Rg = Rg << 1 & 0xFF; M_FL(Rg); return Rg; }
  static final int M_LSR(int Rg) { R.P &= ~C_FLAG; R.P |= Rg & 0x1; Rg = Rg >> 1 & 0xFF; M_FL(Rg); return Rg; }
  static final int M_ROL(int Rg) { K.SetL((Rg << 1 | R.P & 0x1) & 0xFF);
    R.P &= ~C_FLAG; R.P |= Rg >> 7; Rg = K.L;
    M_FL(Rg); return Rg; }
  static final int M_ROR(int Rg) { K.SetL((Rg >> 1 | R.P << 7) & 0xFF);
    R.P &= ~C_FLAG; R.P |= Rg & 0x1; Rg = K.L;
    M_FL(Rg); return Rg;
  }

  public static void Reset6502(M6502Regs R)
  {
    R.A = (R.X = R.Y = 0);
    R.P = 34;
    R.S = 255;
    R.PC.SetL(Rd6502(0xFFFC));
    R.PC.SetH(Rd6502(0xFFFD));
    R.ICount = R.IPeriod;
    //R.IRequest = 0;
    R.pending_irq = 0;	/* NS 970904 */
    R.pending_nmi = 0;	/* NS 970904 */
    R.AfterCLI = 0;
  }

  static void Int6502(M6502Regs R)
  {
    /*  if((Type==INT_NMI)||((Type==INT_IRQ)&&!(R.P&I_FLAG)))*/
      if((R.pending_nmi != 0)||((R.pending_irq != 0)&& ((R.P&I_FLAG)==0)))	/* NS 970904 */
      {
        R.ICount-=7;
        M_PUSH(R.PC.H);
        M_PUSH(R.PC.L);
        M_PUSH(R.P&~B_FLAG);
        R.P&=~D_FLAG;
    /*    if(Type==INT_NMI)*/ /* NS 970904 */
            if (R.pending_nmi != 0)	/* NS 970904 */
            {
                    R.pending_nmi = 0;	/* NS 970904 */
                    J.W=0xFFFA;
            }
            else
            {
                    R.pending_irq = 0;	/* NS 970904 */
                    R.P|=I_FLAG;J.W=0xFFFE;
            }
        R.PC.SetL(Rd6502(J.W)); J.AddW(1);
        R.PC.SetH(Rd6502(J.W));
      }  
  }
    public static void M6502_Cause_Interrupt(M6502Regs R,int type)	/* NS 970904 */
    {
            if (type == INT_NMI)
                    R.pending_nmi = 1;
            else if (type == INT_IRQ)
                    R.pending_irq = 1;
    }
    public static void M6502_Clear_Pending_Interrupts(M6502Regs R)	/* NS 970904 */
    {
            R.pending_irq = 0;
            R.pending_nmi = 0;
    }
  public static int Run6502(M6502Regs Rarg,int cycles)
  {
    R = Rarg;
    R.ICount=cycles;
    do
    {
      R.previousPC.SetW(R.PC.W);

      I = Op6502_1(R.PC.W); R.PC.AddW(1);
      R.ICount -= Cycles[I];
      switch (I) {
      case 16:
        if ((R.P & N_FLAG) != 0) { R.PC.AddW(1); continue; } M_JR(); break;
      case 48:
        if ((R.P & N_FLAG) != 0) { M_JR(); continue; } R.PC.AddW(1); break;
      case 208:
        if ((R.P & Z_FLAG) != 0) { R.PC.AddW(1); continue; } M_JR(); break;
      case 240:
        if ((R.P & Z_FLAG) != 0) { M_JR(); continue; } R.PC.AddW(1); break;
      case 144:
        if ((R.P & C_FLAG) != 0) { R.PC.AddW(1); continue; } M_JR(); break;
      case 176:
        if ((R.P & C_FLAG) != 0) { M_JR(); continue; } R.PC.AddW(1); break;
      case 80:
        if ((R.P & V_FLAG) != 0) { R.PC.AddW(1); continue; } M_JR(); break;
      case 112:
        if ((R.P & V_FLAG) != 0) { M_JR(); continue; } R.PC.AddW(1); break;
      case 64://0x40 RTI
        R.P = M_POP(); R.P |= 32; R.PC.SetL(M_POP()); R.PC.SetH(M_POP());
        /* NS 970904 */
          if((R.pending_irq!=0)&&(R.P&I_FLAG)==0)	/* NS 970904 */
            R.AfterCLI=1;
        break;
      case 96:
        R.PC.SetL(M_POP()); R.PC.SetH(M_POP()); R.PC.AddW(1); break;
      case 32:
        K.SetL(Op6502(R.PC.W)); R.PC.AddW(1);
        K.SetH(Op6502(R.PC.W));
        M_PUSH(R.PC.H);
        M_PUSH(R.PC.L);
        R.PC.SetW(K.W); break;
      case 76:
        M_LDWORD(K); R.PC.SetW(K.W); break;
      case 108:
        M_LDWORD(K);
        R.PC.SetL(Rd6502(K.W)); K.AddW(1);
        R.PC.SetH(Rd6502(K.W));
        break;
      case 0:
        R.PC.AddW(1);
        M_PUSH(R.PC.H); M_PUSH(R.PC.L);
        M_PUSH(R.P | 0x10);
        R.P = ((R.P | 0x4) & 0xFFFFFFF7);
        R.PC.SetL(Rd6502(65534));
        R.PC.SetH(Rd6502(65535));
        break;
      case 88://0x58 CLI
         
          if((R.pending_irq!=0)&&((R.P&I_FLAG)!=0))	/* NS 970904 */
          {
            R.AfterCLI=1;
          }
          R.P&=~I_FLAG;
        break;
      case 40:
        I = M_POP();
        /*  if((R->IRequest!=INT_NONE)&&((I^R->P)&~I&I_FLAG))*/
          if((R.pending_irq!=0)&&(((I^R.P)&~I&I_FLAG)!=0))	/* NS 970904 */
          {
            R.AfterCLI=1;
        /*    R->IBackup=R->ICount;
            R->ICount=1;*/ /* NS 970904 */
          }
          R.P=I|R_FLAG;
        break;
      case 8:
        M_PUSH(R.P); break;
      case 24:
        R.P &= -2; break;
      case 184:
        R.P &= -65; break;
      case 216:
        R.P &= -9; break;
      case 56:
        R.P |= 1; break;
      case 248:
        R.P |= 8; break;
      case 120:
        R.P |= 4; break;
      case 72:
        M_PUSH(R.A); break;
      case 104:
        R.A = M_POP(); M_FL(R.A); break;
      case 152:
        R.A = R.Y; M_FL(R.A); break;
      case 168:
        R.Y = R.A; M_FL(R.Y); break;
      case 200:
        R.Y = (R.Y + 1 & 0xFF); M_FL(R.Y); break;
      case 136:
        R.Y = (R.Y - 1 & 0xFF); M_FL(R.Y); break;
      case 138:
        R.A = R.X; M_FL(R.A); break;
      case 170:
        R.X = R.A; M_FL(R.X); break;
      case 232:
        R.X = (R.X + 1 & 0xFF); M_FL(R.X); break;
      case 202:
        R.X = (R.X - 1 & 0xFF); M_FL(R.X); break;
      case 234:
        break;
      case 154:
        R.S = R.X; break;
      case 186:
        R.X = R.S; break;
      case 36:
        I = MR_Zp(); M_BIT(I); break;
      case 44:
        I = MR_Ab(); M_BIT(I); break;
      case 5:
        I = MR_Zp(); M_ORA(I); break;
      case 6:
        MM_Zp(); I = M_ASL(I); Wr6502(J.W, I); break;
      case 37:
        I = MR_Zp(); M_AND(I); break;
      case 38:
        MM_Zp(); I = M_ROL(I); Wr6502(J.W, I); break;
      case 69:
        I = MR_Zp(); M_EOR(I); break;
      case 70:
        MM_Zp(); I = M_LSR(I); Wr6502(J.W, I); break;
      case 101:
        I = MR_Zp(); M_ADC(I); break;
      case 102:
        MM_Zp(); I = M_ROR(I); Wr6502(J.W, I); break;
      case 132:
        MW_Zp(R.Y); break;
      case 133:
        MW_Zp(R.A); break;
      case 134:
        MW_Zp(R.X); break;
      case 164:
        R.Y = MR_Zp(); M_FL(R.Y); break;
      case 165:
        R.A = MR_Zp(); M_FL(R.A); break;
      case 166:
        R.X = MR_Zp(); M_FL(R.X); break;
      case 196:
        I = MR_Zp(); M_CMP(R.Y, I); break;
      case 197:
        I = MR_Zp(); M_CMP(R.A, I); break;
      case 198:
        MM_Zp(); I = M_DEC(I); Wr6502(J.W, I); break;
      case 228:
        I = MR_Zp(); M_CMP(R.X, I); break;
      case 229:
        I = MR_Zp(); M_SBC(I); break;
      case 230:
        MM_Zp(); I = M_INC(I); Wr6502(J.W, I); break;
      case 13:
        I = MR_Ab(); M_ORA(I); break;
      case 14:
        MM_Ab(); I = M_ASL(I); Wr6502(J.W, I); break;
      case 45:
        I = MR_Ab(); M_AND(I); break;
      case 46:
        MM_Ab(); I = M_ROL(I); Wr6502(J.W, I); break;
      case 77:
        I = MR_Ab(); M_EOR(I); break;
      case 78:
        MM_Ab(); I = M_LSR(I); Wr6502(J.W, I); break;
      case 109:
        I = MR_Ab(); M_ADC(I); break;
      case 110:
        MM_Ab(); I = M_ROR(I); Wr6502(J.W, I); break;
      case 140:
        MW_Ab(R.Y); break;
      case 141:
        MW_Ab(R.A); break;
      case 142:
        MW_Ab(R.X); break;
      case 172:
        R.Y = MR_Ab(); M_FL(R.Y); break;
      case 173:
        R.A = MR_Ab(); M_FL(R.A); break;
      case 174:
        R.X = MR_Ab(); M_FL(R.X); break;
      case 205:
        I = MR_Ab(); M_CMP(R.A, I); break;
      case 206:
        MM_Ab(); I = M_DEC(I); Wr6502(J.W, I); break;
      case 236:
        I = MR_Ab(); M_CMP(R.X, I); break;
      case 237:
        I = MR_Ab(); M_SBC(I); break;
      case 238:
        MM_Ab(); I = M_INC(I); Wr6502(J.W, I); break;
      case 9:
        I = MR_Im(); M_ORA(I); break;
      case 41:
        I = MR_Im(); M_AND(I); break;
      case 73:
        I = MR_Im(); M_EOR(I); break;
      case 105:
        I = MR_Im(); M_ADC(I); break;
      case 160:
        R.Y = MR_Im(); M_FL(R.Y); break;
      case 162:
        R.X = MR_Im(); M_FL(R.X); break;
      case 169:
        R.A = MR_Im(); M_FL(R.A); break;
      case 192:
        I = MR_Im(); M_CMP(R.Y, I); break;
      case 201:
        I = MR_Im(); M_CMP(R.A, I); break;
      case 224:
        I = MR_Im(); M_CMP(R.X, I); break;
      case 233:
        I = MR_Im(); M_SBC(I); break;
      case 21:
        I = MR_Zx(); M_ORA(I); break;
      case 22:
        MM_Zx(); I = M_ASL(I); Wr6502(J.W, I); break;
      case 53:
        I = MR_Zx(); M_AND(I); break;
      case 54:
        MM_Zx(); I = M_ROL(I); Wr6502(J.W, I); break;
      case 85:
        I = MR_Zx(); M_EOR(I); break;
      case 86:
        MM_Zx(); I = M_LSR(I); Wr6502(J.W, I); break;
      case 117:
        I = MR_Zx(); M_ADC(I); break;
      case 118:
        MM_Zx(); I = M_ROR(I); Wr6502(J.W, I); break;
      case 148:
        MW_Zx(R.Y); break;
      case 149:
        MW_Zx(R.A); break;
      case 150:
        MW_Zy(R.X); break;
      case 180:
        R.Y = MR_Zx(); M_FL(R.Y); break;
      case 181:
        R.A = MR_Zx(); M_FL(R.A); break;
      case 182:
        R.X = MR_Zy(); M_FL(R.X); break;
      case 213:
        I = MR_Zx(); M_CMP(R.A, I); break;
      case 214:
        MM_Zx(); I = M_DEC(I); Wr6502(J.W, I); break;
      case 245:
        I = MR_Zx(); M_SBC(I); break;
      case 246:
        MM_Zx(); I = M_INC(I); Wr6502(J.W, I); break;
      case 25:
        I = MR_Ay(); M_ORA(I); break;
      case 29:
        I = MR_Ax(); M_ORA(I); break;
      case 30:
        MM_Ax(); I = M_ASL(I); Wr6502(J.W, I); break;
      case 57:
        I = MR_Ay(); M_AND(I); break;
      case 61:
        I = MR_Ax(); M_AND(I); break;
      case 62:
        MM_Ax(); I = M_ROL(I); Wr6502(J.W, I); break;
      case 89:
        I = MR_Ay(); M_EOR(I); break;
      case 93:
        I = MR_Ax(); M_EOR(I); break;
      case 94:
        MM_Ax(); I = M_LSR(I); Wr6502(J.W, I); break;
      case 121:
        I = MR_Ay(); M_ADC(I); break;
      case 125:
        I = MR_Ax(); M_ADC(I); break;
      case 126:
        MM_Ax(); I = M_ROR(I); Wr6502(J.W, I); break;
      case 153:
        MW_Ay(R.A); break;
      case 157:
        MW_Ax(R.A); break;
      case 185:
        R.A = MR_Ay(); M_FL(R.A); break;
      case 188:
        R.Y = MR_Ax(); M_FL(R.Y); break;
      case 189:
        R.A = MR_Ax(); M_FL(R.A); break;
      case 190:
        R.X = MR_Ay(); M_FL(R.X); break;
      case 217:
        I = MR_Ay(); M_CMP(R.A, I); break;
      case 221:
        I = MR_Ax(); M_CMP(R.A, I); break;
      case 222:
        MM_Ax(); I = M_DEC(I); Wr6502(J.W, I); break;
      case 249:
        I = MR_Ay(); M_SBC(I); break;
      case 253:
        I = MR_Ax(); M_SBC(I); break;
      case 254:
        MM_Ax(); I = M_INC(I); Wr6502(J.W, I); break;
      case 17:
        I = MR_Iy(); M_ORA(I); break;
      case 33:
        I = MR_Ix(); M_AND(I); break;
      case 49:
        I = MR_Iy(); M_AND(I); break;
      case 81:
        I = MR_Iy(); M_EOR(I); break;
      case 113:
        I = MR_Iy(); M_ADC(I); break;
      case 129:
        MW_Ix(R.A); break;
      case 145:
        MW_Iy(R.A); break;
      case 161:
        R.A = MR_Ix(); M_FL(R.A); break;
      case 177:
        R.A = MR_Iy(); M_FL(R.A); break;
      case 209:
        I = MR_Iy(); M_CMP(R.A, I); break;
      case 225:
        I = MR_Ix(); M_SBC(I); break;
      case 241:
        I = MR_Iy(); M_SBC(I); break;
      case 10:
        R.A = M_ASL(R.A); break;
      case 42:
        R.A = M_ROL(R.A); break;
      case 74:
        R.A = M_LSR(R.A); break;
      case 106:
        R.A = M_ROR(R.A); break;
      case 1:
      case 2:
      case 3:
      case 4:
      case 7:
      case 11:
      case 12:
      case 15:
      case 18:
      case 19:
      case 20:
      case 23:
      case 26:
      case 27:
      case 28:
      case 31:
      case 34:
      case 35:
      case 39:
      case 43:
      case 47:
      case 50:
      case 51:
      case 52:
      case 55:
      case 58:
      case 59:
      case 60:
      case 63:
      case 65:
      case 66:
      case 67:
      case 68:
      case 71:
      case 75:
      case 79:
      case 82:
      case 83:
      case 84:
      case 87:
      case 90:
      case 91:
      case 92:
      case 95:
      case 97:
      case 98:
      case 99:
      case 100:
      case 103:
      case 107:
      case 111:
      case 114:
      case 115:
      case 116:
      case 119:
      case 122:
      case 123:
      case 124:
      case 127:
      case 128:
      case 130:
      case 131:
      case 135:
      case 137:
      case 139:
      case 143:
      case 146:
      case 147:
      case 151:
      case 155:
      case 156:
      case 158:
      case 159:
      case 163:
      case 167:
      case 171:
      case 175:
      case 178:
      case 179:
      case 183:
      case 187:
      case 191:
      case 193:
      case 194:
      case 195:
      case 199:
      case 203:
      case 204:
      case 207:
      case 210:
      case 211:
      case 212:
      case 215:
      case 218:
      case 219:
      case 220:
      case 223:
      case 226:
      case 227:
      case 231:
      case 235:
      case 239:
      case 242:
      case 243:
      case 244:
      case 247:
      case 250:
      case 251:
      case 252:
      default:
        printf("[M6502 %X] Unrecognized instruction: $%02X at PC=$%04X\n", new Object[] { R.User, Integer.valueOf(Op6502(R.PC.W - 1)), Integer.valueOf(R.PC.W - 1) });
      }
      /* NS 970904 - all new */
	if (R.AfterCLI!=0) R.AfterCLI = 0;
	else
	{
		if (R.pending_irq != 0 || R.pending_nmi != 0)
			Int6502(R);
	}
    }while (R.ICount > 0);

    return cycles - R.ICount;	/* NS 970904 */
  }
}
