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

import static Z80.Z80.*;
import static Z80.Z80IO.*;

public class Z80Codes
{
	public static final int M_POP()
	{	int i = M_RDSTACK(R.SP) + (M_RDSTACK((R.SP + 1) & 65535) << 8);
		R.SP = (R.SP + 2) & 0xFFFF; return i;
        }
	public static final void M_PUSH(int Rg)
	{	R.SP = (R.SP - 2) & 0xFFFF;
		M_WRSTACK(R.SP, Rg & 0xFF);
		M_WRSTACK((R.SP + 1) & 65535, Rg >> 8);
        }
	public static final void M_CALL()
	{
		int q = M_RDMEM_OPCODE_WORD();
		M_PUSH(R.PC);
		R.PC = q;
		Z80_ICount -= 7;
	}
	public static final void M_JP()
	{	R.PC = M_RDOP_ARG(R.PC) + ((M_RDOP_ARG((R.PC + 1) & 65535)) << 8);

        }
	public static final void M_JR()
	{	R.PC = (R.PC + (byte) M_RDOP_ARG(R.PC) + 1) & 0xFFFF; Z80_ICount-=5;

        }
	public static final void M_RET()
        {
            R.PC = M_POP(); Z80_ICount -= 6;
        }
	public static final void M_RST(int Addr)
        {
            M_PUSH(R.PC); R.PC = Addr;
        }
	public static final int M_SET(int Bit, int Reg)
        {
            return Reg | (1 << Bit);
        }
	public static final int M_RES(int Bit, int Reg) //NEW!
        {
           // return Reg & ~(1 << Bit);
            return Reg & (1 << Bit ^ 0xFFFFFFFF);
        }
	public static final void M_BIT(int Bit, int Reg)
	{	R.F = (R.F & C_FLAG) | H_FLAG |
		(((Reg & (1 << Bit)) != 0) ? ((Bit == 7) ? S_FLAG : 0) : Z_FLAG);
          //TODO buggy check it..
         //R.F = (R.F & C_FLAG | H_FLAG | ((Reg & 1 << Bit) != 0 ? 0 : Bit == 7 ? S_FLAG : Z_FLAG));
        }
	public static final void M_AND(int Reg)
        {
            R.A &= Reg;
            R.F = ZSPTable[R.A] | H_FLAG;
        }
	public static final void M_OR(int Reg) 
        {
            R.A |= Reg;
            R.F = ZSPTable[R.A];
        }
	public static final void M_XOR(int Reg) 
        {
            R.A ^= Reg;
            R.F = ZSPTable[R.A];
        }
	public static final int M_IN()
	{	int Reg = Z80_In(R.BC.L); 
                R.F = (R.F & C_FLAG) | ZSPTable[Reg]; return Reg;
        }
	public static final void M_RLCA()
	{	R.A = ((R.A << 1) | ((R.A & 0x80) >> 7)) & 0xFF;
		R.F = (R.F & 0xEC) | (R.A & C_FLAG);
        }
	public static final void M_RRCA()
	{	R.F = (R.F & 0xEC) | (R.A & 0x01);
		R.A = ((R.A >> 1) | (R.A << 7)) & 0xFF;	}

	public static final void M_RLA()
	{
		int i;
		i = R.F & C_FLAG;
		R.F = (R.F & 0xEC) | ((R.A & 0x80) >> 7);
		R.A = ((R.A << 1) | i) & 0xFF;
	};
	public static final void M_RRA()
	{
		int i;
		i = R.F & C_FLAG;
		R.F = (R.F & 0xEC) | (R.A & 0x01);
		R.A = ((R.A >> 1) | (i << 7)) & 0xFF;
	};
	public static final int M_RLC(int Reg)
	{
		int q = Reg >> 7;
		Reg = ((Reg << 1) | q) & 0xFF;
		R.F = ZSPTable[Reg] | q;
		return Reg;
	}
	public static final int M_RRC(int Reg)
	{
		int q= Reg & 1;
		Reg = ((Reg >> 1) | (q << 7)) & 0xFF;
		R.F = ZSPTable[Reg] | q;
		return Reg;
	}
	public static final int M_RL(int Reg)
	{
		int q = Reg >> 7;
		Reg = ((Reg << 1) | (R.F & 1)) & 0xFF;
		R.F = ZSPTable[Reg] | q;
		return Reg;
	}
	public static final int M_RR(int Reg)
	{
		int q = Reg & 1;
		Reg = ((Reg >> 1) | (R.F << 7)) & 0xFF;
		R.F = ZSPTable[Reg] | q;
		return Reg;
	}
	public static final int M_SLL(int Reg)
	{
		int q = Reg >> 7;
		Reg = ((Reg << 1) | 1) & 0xFF;
		R.F = ZSPTable[Reg] | q;
		return Reg;
	}
	public static final int M_SLA(int Reg)
	{
		int q = Reg >> 7;
		Reg = (Reg << 1) & 0xFF;
		R.F = ZSPTable[Reg] | q;
		return Reg;
	}
	public static final int M_SRL(int Reg)
	{
		int q = Reg & 1;
		Reg = (Reg >> 1) & 0xFF;
		R.F = ZSPTable[Reg] | q;
		return Reg;
	}
	public static final int M_SRA(int Reg)
	{
		int q = Reg & 1;
		Reg = ((Reg >> 1) | (Reg & 0x80)) & 0xFF;
		R.F = ZSPTable[Reg] | q;
		return Reg;
	}
	public static final int M_INC(int Reg)
	{
		Reg = (Reg + 1) & 0xFF;
		R.F = (R.F & C_FLAG) | ZSTable[Reg] |
			((Reg == 0x80) ? V_FLAG : 0) | ((Reg &0x0F) != 0 ? 0 : H_FLAG);
		return Reg;
	}
	public static final int M_DEC(int Reg)
	{
		R.F = (R.F & C_FLAG) | N_FLAG |
			((Reg == 0x80) ? V_FLAG : 0) | ((Reg & 0x0F) != 0 ? 0 : H_FLAG);
		Reg = (Reg - 1) & 0xFF;
 		R.F |= ZSTable[Reg];
		return Reg;
	}
	public static final void M_ADD(int Reg)
	{
		int q = R.A + Reg;
		R.F = ZSTable[q & 255] | ((q & 256) >> 8) |
		      ((R.A ^ q ^ Reg) & H_FLAG) |
		      (((Reg ^ R.A ^ 0x80) & (Reg ^ q) & 0x80) >> 5);
		R.A = q & 0xFF;
	}

	public static final void M_ADC(int Reg)
	{
		int q = R.A + Reg + (R.F & 1);
		R.F = ZSTable[q & 255] | ((q & 256) >> 8) |
		      ((R.A ^ q ^ Reg) & H_FLAG) |
		      (((Reg ^ R.A ^ 0x80) & (Reg ^ q) & 0x80) >> 5);
		R.A = q & 0xFF;
	}
	public static final void M_SUB(int Reg)
	{
		int q = R.A - Reg;
		R.F = ZSTable[q & 255] | ((q & 256) >> 8) | N_FLAG |
			((R.A ^ q ^ Reg) & H_FLAG) |
			(((Reg ^ R.A) & (Reg ^ q) & 0x80) >> 5);
		R.A = q & 0xFF;
	}
	public static final void M_SBC(int Reg)
	{
		int q;
		q = R.A - Reg - (R.F & 1);
		R.F = ZSTable[q & 255] | ((q & 256) >> 8) | N_FLAG |
			((R.A ^ q ^ Reg) & H_FLAG) |
			(((Reg ^ R.A) & (Reg ^ q) & 0x80) >> 5);
		R.A = q & 0xFF;
	}
	public static final void M_CP(int Reg)
	{
		int q = R.A - Reg;
	 	R.F = ZSTable[q & 255] | ((q & 256) >> 8) | N_FLAG |
	          ((R.A ^ q ^ Reg) & H_FLAG) |
	          (((Reg ^ R.A) & (Reg ^ q) & 0x80) >> 5);
	}
	public static final int M_ADDW(int Reg1, int Reg2)
	{
		int q = Reg1 + Reg2;
		R.F = (R.F & (S_FLAG | Z_FLAG | V_FLAG)) |
				(((Reg1 ^ q ^ Reg2) & 0x1000) >> 8) |
				((q >> 16) & 1);
		return q & 0xFFFF;
	}
	public static final void M_ADCW(int Reg)
	{
		int q = R.HL.W + Reg + (R.F & 1);
		R.F = (((R.HL.W ^ q ^ Reg) & 0x1000) >> 8) |
			((q >> 16) & 1) |
			((q & 0x8000) >> 8) |
			(((q & 65535) != 0) ? 0 : Z_FLAG) |
			(((Reg ^ R.HL.W ^ 0x8000) & (Reg ^ q) & 0x8000) >> 13);
		R.HL.SetW(q & 0xFFFF);
	}
	public static final void M_SBCW(int Reg)
	{
		int q = R.HL.W - Reg - (R.F & 1);
		R.F = (((R.HL.W ^ q ^ Reg) & 0x1000) >> 8) |
			  ((q >> 16) & 1) |
			  ((q & 0x8000) >> 8) |
			  (((q & 65535) != 0) ? 0 : Z_FLAG) |
			  (((Reg ^ R.HL.W) & (Reg ^ q) & 0x8000) >> 13) |
			  N_FLAG;
		R.HL.SetW(q & 0xFFFF);
	}
}