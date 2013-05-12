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

import static arcadeflex.libc.*;
import static Z80.DAAH.*;
import static Z80.Z80Codes.*;
import static Z80.Z80H.*;
import static Z80.Z80IO.*;
import static mame.cpuintrf.*;

public class Z80
{

	static final int S_FLAG = 0x80;
	static final int Z_FLAG = 0x40;
	static final int H_FLAG = 0x10;
	static final int V_FLAG = 0x04;
	static final int N_FLAG = 0x02;
	static final int C_FLAG = 0x01;
		


	public static Z80_Regs R = new Z80_Regs();
	public static int Z80_Running = 1;
	//public static int Z80_IPeriod = 50000;
	public static int Z80_ICount = 50000;
	static int PTable[] = new int[512];
	static int ZSTable[] = new int[512];
	static int ZSPTable[] = new int[512];

	static opcode_fn adc_a_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_ADC(i); } };
	static opcode_fn adc_a_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_ADC(i); } };
	static opcode_fn adc_a_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_ADC(i); } };
	static opcode_fn adc_a_a = new opcode_fn() { public void handler() { M_ADC(R.A); } };
	static opcode_fn adc_a_b = new opcode_fn() { public void handler() { M_ADC(R.BC.H); } };
	static opcode_fn adc_a_c = new opcode_fn() { public void handler() { M_ADC(R.BC.L); } };
	static opcode_fn adc_a_d = new opcode_fn() { public void handler() { M_ADC(R.DE.H); } };
	static opcode_fn adc_a_e = new opcode_fn() { public void handler() { M_ADC(R.DE.L); } };
	static opcode_fn adc_a_h = new opcode_fn() { public void handler() { M_ADC(R.HL.H); } };
	static opcode_fn adc_a_l = new opcode_fn() { public void handler() { M_ADC(R.HL.L); } };
	static opcode_fn adc_a_byte = new opcode_fn() { public void handler() { int i = M_RDMEM_OPCODE(); M_ADC(i); } };

	static opcode_fn adc_hl_bc = new opcode_fn() { public void handler() { M_ADCW(R.BC.W); } };
	static opcode_fn adc_hl_de = new opcode_fn() { public void handler() { M_ADCW(R.DE.W); } };
	static opcode_fn adc_hl_hl = new opcode_fn() { public void handler() { M_ADCW(R.HL.W); } };
	static opcode_fn adc_hl_sp = new opcode_fn() { public void handler() { M_ADCW(R.SP); } };

	static opcode_fn add_a_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_ADD(i); } };
	static opcode_fn add_a_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_ADD(i); } };
	static opcode_fn add_a_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_ADD(i); } };
	static opcode_fn add_a_a = new opcode_fn() { public void handler() { M_ADD(R.A); } };
	static opcode_fn add_a_b = new opcode_fn() { public void handler() { M_ADD(R.BC.H); } };
	static opcode_fn add_a_c = new opcode_fn() { public void handler() { M_ADD(R.BC.L); } };
	static opcode_fn add_a_d = new opcode_fn() { public void handler() { M_ADD(R.DE.H); } };
	static opcode_fn add_a_e = new opcode_fn() { public void handler() { M_ADD(R.DE.L); } };
	static opcode_fn add_a_h = new opcode_fn() { public void handler() { M_ADD(R.HL.H); } };
	static opcode_fn add_a_l = new opcode_fn() { public void handler() { M_ADD(R.HL.L); } };
	static opcode_fn add_a_ixh = new opcode_fn() { public void handler() { M_ADD(R.IX.H); } };
        static opcode_fn add_a_ixl = new opcode_fn() { public void handler() { M_ADD(R.IX.L); } } ;
	static opcode_fn add_a_iyh = new opcode_fn() { public void handler() { M_ADD(R.IY.H); } };
	static opcode_fn add_a_iyl = new opcode_fn() { public void handler() { M_ADD(R.IY.L); } };
	static opcode_fn add_a_byte = new opcode_fn() { public void handler() { int i = M_RDMEM_OPCODE(); M_ADD(i); } };

	static opcode_fn add_hl_bc = new opcode_fn() { public void handler() { R.HL.SetW(M_ADDW(R.HL.W, R.BC.W)); } };
	static opcode_fn add_hl_de = new opcode_fn() { public void handler() { R.HL.SetW(M_ADDW(R.HL.W, R.DE.W)); } };
	static opcode_fn add_hl_hl = new opcode_fn() { public void handler() { R.HL.SetW(M_ADDW(R.HL.W, R.HL.W)); } };
	static opcode_fn add_hl_sp = new opcode_fn() { public void handler() { R.HL.SetW(M_ADDW(R.HL.W, R.SP)); } };
	static opcode_fn add_ix_bc = new opcode_fn() { public void handler() { R.IX.SetW(M_ADDW(R.IX.W, R.BC.W)); } };
	static opcode_fn add_ix_de = new opcode_fn() { public void handler() { R.IX.SetW(M_ADDW(R.IX.W, R.DE.W)); } };
	static opcode_fn add_ix_ix = new opcode_fn() { public void handler() { R.IX.SetW(M_ADDW(R.IX.W, R.IX.W)); } };
	static opcode_fn add_ix_sp = new opcode_fn() { public void handler() { R.IX.SetW(M_ADDW(R.IX.W, R.SP)); } };
	static opcode_fn add_iy_bc = new opcode_fn() { public void handler() { R.IY.SetW(M_ADDW(R.IY.W, R.BC.W)); } };
	static opcode_fn add_iy_de = new opcode_fn() { public void handler() { R.IY.SetW(M_ADDW(R.IY.W, R.DE.W)); } };
	static opcode_fn add_iy_iy = new opcode_fn() { public void handler() { R.IY.SetW(M_ADDW(R.IY.W, R.IY.W)); } };
	static opcode_fn add_iy_sp = new opcode_fn() { public void handler() { R.IY.SetW(M_ADDW(R.IY.W, R.SP)); } };

	static opcode_fn and_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_AND(i); } };
	static opcode_fn and_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_AND(i); } };
	static opcode_fn and_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_AND(i); } };
	static opcode_fn and_a = new opcode_fn() { public void handler() { R.F = ZSPTable[R.A] | H_FLAG; } };
	static opcode_fn and_b = new opcode_fn() { public void handler() { M_AND(R.BC.H); } };
	static opcode_fn and_c = new opcode_fn() { public void handler() { M_AND(R.BC.L); } };
	static opcode_fn and_d = new opcode_fn() { public void handler() { M_AND(R.DE.H); } };
	static opcode_fn and_e = new opcode_fn() { public void handler() { M_AND(R.DE.L); } };
	static opcode_fn and_h = new opcode_fn() { public void handler() { M_AND(R.HL.H); } };
	static opcode_fn and_l = new opcode_fn() { public void handler() { M_AND(R.HL.L); } };
	static opcode_fn and_byte = new opcode_fn() { public void handler() { int i = M_RDMEM_OPCODE(); M_AND(i); } };

	static opcode_fn bit_0_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_BIT(0, i); } };
	static opcode_fn bit_0_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_BIT(0, i); } };
	static opcode_fn bit_0_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_BIT(0, i); } };
	static opcode_fn bit_0_a = new opcode_fn() { public void handler() { M_BIT(0, R.A); } };
	static opcode_fn bit_0_b = new opcode_fn() { public void handler() { M_BIT(0, R.BC.H); } };
	static opcode_fn bit_0_c = new opcode_fn() { public void handler() { M_BIT(0, R.BC.L); } };
	static opcode_fn bit_0_d = new opcode_fn() { public void handler() { M_BIT(0, R.DE.H); } };
	static opcode_fn bit_0_e = new opcode_fn() { public void handler() { M_BIT(0, R.DE.L); } };
	static opcode_fn bit_0_h = new opcode_fn() { public void handler() { M_BIT(0, R.HL.H); } };
	static opcode_fn bit_0_l = new opcode_fn() { public void handler() { M_BIT(0, R.HL.L); } };

	static opcode_fn bit_1_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_BIT(1, i); } };
	static opcode_fn bit_1_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_BIT(1, i); } };
	static opcode_fn bit_1_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_BIT(1, i); } };
	static opcode_fn bit_1_a = new opcode_fn() { public void handler() { M_BIT(1, R.A); } };
	static opcode_fn bit_1_b = new opcode_fn() { public void handler() { M_BIT(1, R.BC.H); } };
	static opcode_fn bit_1_c = new opcode_fn() { public void handler() { M_BIT(1, R.BC.L); } };
	static opcode_fn bit_1_d = new opcode_fn() { public void handler() { M_BIT(1, R.DE.H); } };
	static opcode_fn bit_1_e = new opcode_fn() { public void handler() { M_BIT(1, R.DE.L); } };
	static opcode_fn bit_1_h = new opcode_fn() { public void handler() { M_BIT(1, R.HL.H); } };
	static opcode_fn bit_1_l = new opcode_fn() { public void handler() { M_BIT(1, R.HL.L); } };

	static opcode_fn bit_2_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_BIT(2, i); } };
	static opcode_fn bit_2_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_BIT(2, i); } };
	static opcode_fn bit_2_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_BIT(2, i); } };
	static opcode_fn bit_2_a = new opcode_fn() { public void handler() { M_BIT(2, R.A); } };
	static opcode_fn bit_2_b = new opcode_fn() { public void handler() { M_BIT(2, R.BC.H); } };
	static opcode_fn bit_2_c = new opcode_fn() { public void handler() { M_BIT(2, R.BC.L); } };
	static opcode_fn bit_2_d = new opcode_fn() { public void handler() { M_BIT(2, R.DE.H); } };
	static opcode_fn bit_2_e = new opcode_fn() { public void handler() { M_BIT(2, R.DE.L); } };
	static opcode_fn bit_2_h = new opcode_fn() { public void handler() { M_BIT(2, R.HL.H); } };
	static opcode_fn bit_2_l = new opcode_fn() { public void handler() { M_BIT(2, R.HL.L); } };

	static opcode_fn bit_3_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_BIT(3, i); } };
	static opcode_fn bit_3_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_BIT(3, i); } };
	static opcode_fn bit_3_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_BIT(3, i); } };
	static opcode_fn bit_3_a = new opcode_fn() { public void handler() { M_BIT(3, R.A); } };
	static opcode_fn bit_3_b = new opcode_fn() { public void handler() { M_BIT(3, R.BC.H); } };
	static opcode_fn bit_3_c = new opcode_fn() { public void handler() { M_BIT(3, R.BC.L); } };
	static opcode_fn bit_3_d = new opcode_fn() { public void handler() { M_BIT(3, R.DE.H); } };
	static opcode_fn bit_3_e = new opcode_fn() { public void handler() { M_BIT(3, R.DE.L); } };
	static opcode_fn bit_3_h = new opcode_fn() { public void handler() { M_BIT(3, R.HL.H); } };
	static opcode_fn bit_3_l = new opcode_fn() { public void handler() { M_BIT(3, R.HL.L); } };

	static opcode_fn bit_4_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_BIT(4, i); } };
	static opcode_fn bit_4_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_BIT(4, i); } };
	static opcode_fn bit_4_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_BIT(4, i); } };
	static opcode_fn bit_4_a = new opcode_fn() { public void handler() { M_BIT(4, R.A); } };
	static opcode_fn bit_4_b = new opcode_fn() { public void handler() { M_BIT(4, R.BC.H); } };
	static opcode_fn bit_4_c = new opcode_fn() { public void handler() { M_BIT(4, R.BC.L); } };
	static opcode_fn bit_4_d = new opcode_fn() { public void handler() { M_BIT(4, R.DE.H); } };
	static opcode_fn bit_4_e = new opcode_fn() { public void handler() { M_BIT(4, R.DE.L); } };
	static opcode_fn bit_4_h = new opcode_fn() { public void handler() { M_BIT(4, R.HL.H); } };
	static opcode_fn bit_4_l = new opcode_fn() { public void handler() { M_BIT(4, R.HL.L); } };

	static opcode_fn bit_5_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_BIT(5, i); } };
	static opcode_fn bit_5_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_BIT(5, i); } };
	static opcode_fn bit_5_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_BIT(5, i); } };
	static opcode_fn bit_5_a = new opcode_fn() { public void handler() { M_BIT(5, R.A); } };
	static opcode_fn bit_5_b = new opcode_fn() { public void handler() { M_BIT(5, R.BC.H); } };
	static opcode_fn bit_5_c = new opcode_fn() { public void handler() { M_BIT(5, R.BC.L); } };
	static opcode_fn bit_5_d = new opcode_fn() { public void handler() { M_BIT(5, R.DE.H); } };
	static opcode_fn bit_5_e = new opcode_fn() { public void handler() { M_BIT(5, R.DE.L); } };
	static opcode_fn bit_5_h = new opcode_fn() { public void handler() { M_BIT(5, R.HL.H); } };
	static opcode_fn bit_5_l = new opcode_fn() { public void handler() { M_BIT(5, R.HL.L); } };

	static opcode_fn bit_6_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_BIT(6, i); } };
	static opcode_fn bit_6_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_BIT(6, i); } };
	static opcode_fn bit_6_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_BIT(6, i); } };
	static opcode_fn bit_6_a = new opcode_fn() { public void handler() { M_BIT(6, R.A); } };
	static opcode_fn bit_6_b = new opcode_fn() { public void handler() { M_BIT(6, R.BC.H); } };
	static opcode_fn bit_6_c = new opcode_fn() { public void handler() { M_BIT(6, R.BC.L); } };
	static opcode_fn bit_6_d = new opcode_fn() { public void handler() { M_BIT(6, R.DE.H); } };
	static opcode_fn bit_6_e = new opcode_fn() { public void handler() { M_BIT(6, R.DE.L); } };
	static opcode_fn bit_6_h = new opcode_fn() { public void handler() { M_BIT(6, R.HL.H); } };
	static opcode_fn bit_6_l = new opcode_fn() { public void handler() { M_BIT(6, R.HL.L); } };

	static opcode_fn bit_7_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_BIT(7, i); } };
	static opcode_fn bit_7_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_BIT(7, i); } };
	static opcode_fn bit_7_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_BIT(7, i); } };
	static opcode_fn bit_7_a = new opcode_fn() { public void handler() { M_BIT(7, R.A); } };
	static opcode_fn bit_7_b = new opcode_fn() { public void handler() { M_BIT(7, R.BC.H); } };
	static opcode_fn bit_7_c = new opcode_fn() { public void handler() { M_BIT(7, R.BC.L); } };
	static opcode_fn bit_7_d = new opcode_fn() { public void handler() { M_BIT(7, R.DE.H); } };
	static opcode_fn bit_7_e = new opcode_fn() { public void handler() { M_BIT(7, R.DE.L); } };
	static opcode_fn bit_7_h = new opcode_fn() { public void handler() { M_BIT(7, R.HL.H); } };
	static opcode_fn bit_7_l = new opcode_fn() { public void handler() { M_BIT(7, R.HL.L); } };

	static opcode_fn call_c = new opcode_fn() { public void handler() { if (M_C()) { M_CALL(); } else { M_SKIP_CALL(); } } };
	static opcode_fn call_m = new opcode_fn() { public void handler() { if (M_M()) { M_CALL(); } else { M_SKIP_CALL(); } } };
	static opcode_fn call_nc = new opcode_fn() { public void handler() { if (M_NC()) { M_CALL(); } else { M_SKIP_CALL(); } } };
	static opcode_fn call_nz = new opcode_fn() { public void handler() { if (M_NZ()) { M_CALL(); } else { M_SKIP_CALL(); } } };
	static opcode_fn call_p = new opcode_fn() { public void handler() { if (M_P()) { M_CALL(); } else { M_SKIP_CALL(); } } };
	static opcode_fn call_z = new opcode_fn() { public void handler() { if (M_Z()) { M_CALL(); } else { M_SKIP_CALL(); } } };
	static opcode_fn call = new opcode_fn() { public void handler() { M_CALL(); } };

	static opcode_fn ccf = new opcode_fn() { public void handler() { R.F = ((R.F & 0xED) | ((R.F & 1) << 4)) ^ 1; } };

	static opcode_fn cp_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_CP(i); } };
	static opcode_fn cp_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_CP(i); } };
	static opcode_fn cp_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_CP(i); } };
	static opcode_fn cp_a = new opcode_fn() { public void handler() { M_CP(R.A); } };
	static opcode_fn cp_b = new opcode_fn() { public void handler() { M_CP(R.BC.H); } };
	static opcode_fn cp_c = new opcode_fn() { public void handler() { M_CP(R.BC.L); } };
	static opcode_fn cp_d = new opcode_fn() { public void handler() { M_CP(R.DE.H); } };
	static opcode_fn cp_e = new opcode_fn() { public void handler() { M_CP(R.DE.L); } };
	static opcode_fn cp_h = new opcode_fn() { public void handler() { M_CP(R.HL.H); } };
	static opcode_fn cp_l = new opcode_fn() { public void handler() { M_CP(R.HL.L); } };
	static opcode_fn cp_byte = new opcode_fn() { public void handler() { int i = M_RDMEM_OPCODE(); M_CP(i); } };

	static opcode_fn cpdr = new opcode_fn() { public void handler()
	{
		int i, j;
	 	R.R -= 2;
	 	do
	 	{
	  		R.R += 2;
	  		i = M_RDMEM(R.HL.W);
	  		j = (R.A - i) & 0xFF;
			R.HL.AddW(-1);
			R.BC.AddW(-1);
	 		Z80_ICount -= 21;
	 	}
	 	while (R.BC.W != 0 && j != 0 && Z80_ICount > 0);
	 	R.F = (R.F & C_FLAG) | ZSTable[j] |
	          ((R.A ^ i ^ j) & H_FLAG) | (R.BC.W != 0 ? V_FLAG : 0) | N_FLAG;
	 	if (R.BC.W != 0 && j != 0) R.PC = (R.PC - 2) & 0xFFFF;
		else Z80_ICount += 5;
	} };

	static opcode_fn cpi = new opcode_fn() { public void handler()
	{
		int i, j;
		i = M_RDMEM(R.HL.W);
		j = (R.A - i) & 0xFF;
		R.HL.AddW(1);
		R.BC.AddW(-1);
		R.F = (R.F & C_FLAG) | ZSTable[j] |
			  ((R.A ^ i ^ j) & H_FLAG) | (R.BC.W != 0 ? V_FLAG : 0) | N_FLAG;
	} };

	static opcode_fn cpir = new opcode_fn() { public void handler()
	{
		int i, j;
		R.R -= 2;
		do
		{
			R.R += 2;
			i = M_RDMEM(R.HL.W);
			j = (R.A - i) & 0xFF;
			R.HL.AddW(1);
			R.BC.AddW(-1);
			Z80_ICount -= 21;
		}
		while (R.BC.W != 0 && j != 0 && Z80_ICount > 0);
		R.F = (R.F & C_FLAG) | ZSTable[j] |
			  ((R.A ^ i ^ j) & H_FLAG) | (R.BC.W != 0 ? V_FLAG : 0) | N_FLAG;
		if (R.BC.W != 0 && j != 0) R.PC = (R.PC - 2) & 0xFFFF;
		else Z80_ICount += 5;
	} };

	static opcode_fn cpl = new opcode_fn() { public void handler() { R.A ^= 0xFF; R.F |= (H_FLAG | N_FLAG); } };

	static opcode_fn daa = new opcode_fn() { public void handler()
	{
		int i;
		i = R.A;
		if ((R.F & C_FLAG) != 0) i |= 256;
		if ((R.F & H_FLAG) != 0) i |= 512;
		if ((R.F & N_FLAG) != 0) i |= 1024;
		R.A = ((char) DAATable[i]) >> 8;
		R.F = ((char) DAATable[i]) & 0xFF;
	} };

	static opcode_fn dec_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_DEC(i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn dec_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_DEC(i);
		M_WRMEM(j, i);
	} };
	static opcode_fn dec_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_DEC(i);
		M_WRMEM(j, i);
	} };
	static opcode_fn dec_a = new opcode_fn() { public void handler() { R.A = M_DEC(R.A); } };
	static opcode_fn dec_b = new opcode_fn() { public void handler() { R.BC.SetH(M_DEC(R.BC.H)); } };
	static opcode_fn dec_c = new opcode_fn() { public void handler() { R.BC.SetL(M_DEC(R.BC.L)); } };
	static opcode_fn dec_d = new opcode_fn() { public void handler() { R.DE.SetH(M_DEC(R.DE.H)); } };
	static opcode_fn dec_e = new opcode_fn() { public void handler() { R.DE.SetL(M_DEC(R.DE.L)); } };
	static opcode_fn dec_h = new opcode_fn() { public void handler() { R.HL.SetH(M_DEC(R.HL.H)); } };
	static opcode_fn dec_l = new opcode_fn() { public void handler() { R.HL.SetL(M_DEC(R.HL.L)); } };
	static opcode_fn dec_ixl = new opcode_fn() { public void handler() { R.IX.SetL(M_DEC(R.IX.L)); } } ;
        static opcode_fn dec_iyh = new opcode_fn() { public void handler() { R.IY.SetH(M_DEC(R.IY.H)); } };
	static opcode_fn dec_iyl = new opcode_fn() { public void handler() { R.IY.SetL(M_DEC(R.IY.L)); } };

	static opcode_fn dec_bc = new opcode_fn() { public void handler() { R.BC.AddW(-1); } };
	static opcode_fn dec_de = new opcode_fn() { public void handler() { R.DE.AddW(-1); } };
	static opcode_fn dec_hl = new opcode_fn() { public void handler() { R.HL.AddW(-1); } };
	static opcode_fn dec_ix = new opcode_fn() { public void handler() { R.IX.AddW(-1); } };
	static opcode_fn dec_iy = new opcode_fn() { public void handler() { R.IY.AddW(-1); } };
	static opcode_fn dec_sp = new opcode_fn() { public void handler() { R.SP = (R.SP - 1) & 0xFFFF; } };

	static opcode_fn di = new opcode_fn() {	public void handler() { R.IFF1 = R.IFF2 = 0; } };

	static opcode_fn djnz = new opcode_fn() { public void handler() { R.BC.AddH(-1); if (R.BC.H != 0) { M_JR(); } else { M_SKIP_JR(); } } };

	static opcode_fn ex_xsp_hl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM_WORD(R.SP);
		M_WRMEM_WORD(R.SP, R.HL.W);
		R.HL.SetW(i);
	} };

	static opcode_fn ex_xsp_ix = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM_WORD(R.SP);
		M_WRMEM_WORD(R.SP, R.IX.W);
		R.IX.SetW(i);
	} };

	static opcode_fn ex_xsp_iy = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM_WORD(R.SP);
		M_WRMEM_WORD(R.SP, R.IY.W);
		R.IY.SetW(i);
	} };

	static opcode_fn ex_af_af = new opcode_fn() { public void handler()
	{
		int i;
		i = (R.A << 8) | R.F;
		R.A = (R.AF2 >> 8);
		R.F = (R.AF2 & 0xFF);
		R.AF2 = i;
	} };

	static opcode_fn ex_de_hl = new opcode_fn() { public void handler()
	{
		int i;
		i = R.DE.W;
		R.DE.SetW(R.HL.W);
		R.HL.SetW(i);
	} };

	static opcode_fn exx = new opcode_fn() { public void handler()
	{
		int i;
		i = R.BC.W;
		R.BC.SetW(R.BC2);
		R.BC2 = i;
		i = R.DE.W;
		R.DE.SetW(R.DE2);
		R.DE2 = i;
		i = R.HL.W;
		R.HL.SetW(R.HL2);
		R.HL2 = i;
	} };

	static opcode_fn halt = new opcode_fn() { public void handler()
	{
		R.PC = (R.PC - 1) & 0xFFFF;
		R.HALT = 1;
		if (Z80_ICount > 0) Z80_ICount = 0;
	} };

	static opcode_fn im_0 = new opcode_fn() { public void handler() { R.IM = 0; } };
	static opcode_fn im_1 = new opcode_fn() { public void handler() { R.IM = 1; } };
	static opcode_fn im_2 = new opcode_fn() { public void handler() { R.IM = 2; } };

	static opcode_fn in_a_c = new opcode_fn() { public void handler() { R.A = M_IN(); } };
	static opcode_fn in_c_c = new opcode_fn() { public void handler() { R.BC.SetL(M_IN()); } };
        static opcode_fn in_b_c = new opcode_fn() { public void handler() { R.BC.SetH(M_IN()); } } ;
        static opcode_fn in_e_c = new opcode_fn() { public void handler() { R.DE.SetL(M_IN()); } } ;
        static opcode_fn in_l_c = new opcode_fn() { public void handler() { R.HL.SetL(M_IN()); } } ;

        static opcode_fn in_a_byte = new opcode_fn() { public void handler() { int i = M_RDMEM_OPCODE(); R.A = Z80_In(i); } };

	static opcode_fn inc_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_INC(i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn inc_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_INC(i);
		M_WRMEM(j, i);
	} };
	static opcode_fn inc_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_INC(i);
		M_WRMEM(j, i);
	} };
	static opcode_fn inc_a = new opcode_fn() { public void handler() { R.A = M_INC(R.A); } };
	static opcode_fn inc_b = new opcode_fn() { public void handler() { R.BC.SetH(M_INC(R.BC.H)); } };
	static opcode_fn inc_c = new opcode_fn() { public void handler() { R.BC.SetL(M_INC(R.BC.L)); } };
	static opcode_fn inc_d = new opcode_fn() { public void handler() { R.DE.SetH(M_INC(R.DE.H)); } };
	static opcode_fn inc_e = new opcode_fn() { public void handler() { R.DE.SetL(M_INC(R.DE.L)); } };
	static opcode_fn inc_h = new opcode_fn() { public void handler() { R.HL.SetH(M_INC(R.HL.H)); } };
	static opcode_fn inc_l = new opcode_fn() { public void handler() { R.HL.SetL(M_INC(R.HL.L)); } };
	static opcode_fn inc_ixl = new opcode_fn() { public void handler() { R.IX.SetL(M_INC(R.IX.L)); } };

	static opcode_fn inc_bc = new opcode_fn() {	public void handler() { R.BC.AddW(1); } };
	static opcode_fn inc_de = new opcode_fn() {	public void handler() { R.DE.AddW(1); } };
	static opcode_fn inc_hl = new opcode_fn() {	public void handler() { R.HL.AddW(1); } };
	static opcode_fn inc_ix = new opcode_fn() {	public void handler() { R.IX.AddW(1); } };
	static opcode_fn inc_iy = new opcode_fn() {	public void handler() { R.IY.AddW(1); } };
	static opcode_fn inc_sp = new opcode_fn() {	public void handler() { R.SP = (R.SP + 1) & 0xFFFF; } };

	static opcode_fn jp = new opcode_fn() //TODO changed for v0.27 to be checked
        {
            public void handler()
            {
               // M_JP();
                 int i = R.PC - 1;
                 M_JP();
                 int j = R.PC;
                if (j == i)
                {
                    if (Z80_ICount > 0) Z80_ICount = 0;/* speed up busy loop */
                }
                else if ((j == i - 3) && (M_RDOP(j) == 0x31))/* LD SP,#xxxx - Galaga */
                {
                    if (Z80_ICount > 10) Z80_ICount = 10;
                 }

            }
        };
	static opcode_fn jp_hl = new opcode_fn() { public void handler() { R.PC = R.HL.W; } };
	static opcode_fn jp_ix = new opcode_fn() { public void handler() { R.PC = R.IX.W; } };
	static opcode_fn jp_iy = new opcode_fn() { public void handler() { R.PC = R.IY.W; } };
	static opcode_fn jp_c = new opcode_fn() { public void handler() { if (M_C()) { M_JP(); } else { M_SKIP_JP(); } } };
	static opcode_fn jp_m = new opcode_fn() { public void handler() { if (M_M()) { M_JP(); } else { M_SKIP_JP(); } } };
	static opcode_fn jp_nc = new opcode_fn() { public void handler() { if (M_NC()) { M_JP(); } else { M_SKIP_JP(); } } };
	static opcode_fn jp_nz = new opcode_fn() { public void handler() { if (M_NZ()) { M_JP(); } else { M_SKIP_JP(); } } };
	static opcode_fn jp_p = new opcode_fn() { public void handler() { if (M_P()) { M_JP(); } else { M_SKIP_JP(); } } };
	static opcode_fn jp_pe = new opcode_fn() { public void handler() { if (M_PE()) { M_JP(); } else { M_SKIP_JP(); } } };
	static opcode_fn jp_po = new opcode_fn() { public void handler() { if (M_PO()) { M_JP(); } else { M_SKIP_JP(); } } };
	static opcode_fn jp_z = new opcode_fn() { public void handler() { if (M_Z()) { M_JP(); } else { M_SKIP_JP(); } } };

	static opcode_fn jr = new opcode_fn() //TODO changed for v0.27 to be checked
        {
            public void handler()
            {
               // M_JR();
               int i = Z80.R.PC - 1;
               M_JR();
               int j = R.PC;
               if (j == i)
               {
                  if (Z80_ICount > 0) Z80_ICount = 0;/* speed up busy loop */
               }
               else if ((j == i - 1) && (M_RDOP(j) == 0xfb))/* EI - 1942 */
               {
                   if (Z80_ICount > 4) Z80_ICount = 4;
               }
            }
        };
	static opcode_fn jr_c = new opcode_fn() { public void handler() { if (M_C()) { M_JR(); } else { M_SKIP_JR(); } } };
	static opcode_fn jr_nc = new opcode_fn() { public void handler() { if (M_NC()) { M_JR(); } else { M_SKIP_JR(); } } };
	static opcode_fn jr_nz = new opcode_fn() { public void handler() { if (M_NZ()) { M_JR(); } else { M_SKIP_JR(); } } };
	static opcode_fn jr_z = new opcode_fn() { public void handler() { if (M_Z()) { M_JR(); } else { M_SKIP_JR(); } } };

	static opcode_fn ld_xbc_a = new opcode_fn() { public void handler() { M_WRMEM(R.BC.W, R.A); } };
	static opcode_fn ld_xde_a = new opcode_fn() { public void handler() { M_WRMEM(R.DE.W, R.A); } };
	static opcode_fn ld_xhl_a = new opcode_fn() { public void handler() { M_WRMEM(R.HL.W, R.A); } };
	static opcode_fn ld_xhl_b = new opcode_fn() { public void handler() { M_WRMEM(R.HL.W, R.BC.H); } };
	static opcode_fn ld_xhl_c = new opcode_fn() { public void handler() { M_WRMEM(R.HL.W, R.BC.L); } };
	static opcode_fn ld_xhl_d = new opcode_fn() { public void handler() { M_WRMEM(R.HL.W, R.DE.H); } };
	static opcode_fn ld_xhl_e = new opcode_fn() { public void handler() { M_WRMEM(R.HL.W, R.DE.L); } };
	static opcode_fn ld_xhl_h = new opcode_fn() { public void handler() { M_WRMEM(R.HL.W, R.HL.H); } };
	static opcode_fn ld_xhl_l = new opcode_fn() { public void handler() { M_WRMEM(R.HL.W, R.HL.L); } };
	static opcode_fn ld_xhl_byte = new opcode_fn() { public void handler() { int i = M_RDMEM_OPCODE(); M_WRMEM(R.HL.W, i); } };
	static opcode_fn ld_xix_a = new opcode_fn() { public void handler() { M_WR_XIX(R.A); } };
	static opcode_fn ld_xix_b = new opcode_fn() { public void handler() { M_WR_XIX(R.BC.H); } };
	static opcode_fn ld_xix_c = new opcode_fn() { public void handler() { M_WR_XIX(R.BC.L); } };
	static opcode_fn ld_xix_d = new opcode_fn() { public void handler() { M_WR_XIX(R.DE.H); } };
	static opcode_fn ld_xix_e = new opcode_fn() { public void handler() { M_WR_XIX(R.DE.L); } };
	static opcode_fn ld_xix_h = new opcode_fn() { public void handler() { M_WR_XIX(R.HL.H); } };
	static opcode_fn ld_xix_l = new opcode_fn() { public void handler() { M_WR_XIX(R.HL.L); } };
	static opcode_fn ld_xix_byte = new opcode_fn() { public void handler()
	{
		int i, j;
		i = M_XIX();
		j = M_RDMEM_OPCODE();
		M_WRMEM(i, j);
	} };
	static opcode_fn ld_xiy_a = new opcode_fn() { public void handler() { M_WR_XIY(R.A); } };
	static opcode_fn ld_xiy_b = new opcode_fn() { public void handler() { M_WR_XIY(R.BC.H); } };
	static opcode_fn ld_xiy_c = new opcode_fn() { public void handler() { M_WR_XIY(R.BC.L); } };
	static opcode_fn ld_xiy_d = new opcode_fn() { public void handler() { M_WR_XIY(R.DE.H); } };
	static opcode_fn ld_xiy_e = new opcode_fn() { public void handler() { M_WR_XIY(R.DE.L); } };
	static opcode_fn ld_xiy_h = new opcode_fn() { public void handler() { M_WR_XIY(R.HL.H); } };
	static opcode_fn ld_xiy_l = new opcode_fn() { public void handler() { M_WR_XIY(R.HL.L); } };
	static opcode_fn ld_xiy_byte = new opcode_fn() { public void handler()
	{
		int i, j;
		i = M_XIY();
		j = M_RDMEM_OPCODE();
		M_WRMEM(i, j);
	} };
	static opcode_fn ld_xbyte_a = new opcode_fn() { public void handler()
	{ int i = M_RDMEM_OPCODE_WORD(); M_WRMEM(i, R.A); } };
	static opcode_fn ld_xword_bc = new opcode_fn() { public void handler() { M_WRMEM_WORD(M_RDMEM_OPCODE_WORD(), R.BC.W); } };
	static opcode_fn ld_xword_de = new opcode_fn() { public void handler() { M_WRMEM_WORD(M_RDMEM_OPCODE_WORD(), R.DE.W); } };
	static opcode_fn ld_xword_hl = new opcode_fn() { public void handler() { M_WRMEM_WORD(M_RDMEM_OPCODE_WORD(), R.HL.W); } };
	static opcode_fn ld_xword_ix = new opcode_fn() { public void handler() { M_WRMEM_WORD(M_RDMEM_OPCODE_WORD(), R.IX.W); } };
	static opcode_fn ld_xword_iy = new opcode_fn() { public void handler() { M_WRMEM_WORD(M_RDMEM_OPCODE_WORD(), R.IY.W); } };
	static opcode_fn ld_xword_sp = new opcode_fn() { public void handler() { M_WRMEM_WORD(M_RDMEM_OPCODE_WORD(), R.SP); } };
	static opcode_fn ld_a_xbc = new opcode_fn() { public void handler() { R.A = M_RDMEM(R.BC.W); } };
	static opcode_fn ld_a_xde = new opcode_fn() { public void handler() { R.A = M_RDMEM(R.DE.W); } };
	static opcode_fn ld_a_xhl = new opcode_fn() { public void handler() { R.A = M_RD_XHL(); } };
	static opcode_fn ld_a_xix = new opcode_fn() { public void handler() { R.A = M_RD_XIX(); } };
	static opcode_fn ld_a_xiy = new opcode_fn() { public void handler() { R.A = M_RD_XIY(); } };
	static opcode_fn ld_a_xbyte = new opcode_fn() { public void handler()
	{ int i = M_RDMEM_OPCODE_WORD(); R.A = M_RDMEM(i); } };

	static opcode_fn ld_a_byte = new opcode_fn() { public void handler() { R.A = M_RDMEM_OPCODE(); } };
	static opcode_fn ld_b_byte = new opcode_fn() { public void handler() { R.BC.SetH(M_RDMEM_OPCODE()); } };
	static opcode_fn ld_c_byte = new opcode_fn() { public void handler() { R.BC.SetL(M_RDMEM_OPCODE()); } };
	static opcode_fn ld_d_byte = new opcode_fn() { public void handler() { R.DE.SetH(M_RDMEM_OPCODE()); } };
	static opcode_fn ld_e_byte = new opcode_fn() { public void handler() { R.DE.SetL(M_RDMEM_OPCODE()); } };
	static opcode_fn ld_h_byte = new opcode_fn() { public void handler() { R.HL.SetH(M_RDMEM_OPCODE()); } };
	static opcode_fn ld_l_byte = new opcode_fn() { public void handler() { R.HL.SetL(M_RDMEM_OPCODE()); } };
	static opcode_fn ld_ixh_byte = new opcode_fn() { public void handler() { R.IX.SetH(M_RDMEM_OPCODE()); } } ;
        static opcode_fn ld_ixl_byte = new opcode_fn() { public void handler() { R.IX.SetL(M_RDMEM_OPCODE()); } } ;
        static opcode_fn ld_iyh_byte = new opcode_fn() { public void handler() { R.IY.SetH(M_RDMEM_OPCODE()); } };
	static opcode_fn ld_iyl_byte = new opcode_fn() { public void handler() { R.IY.SetL(M_RDMEM_OPCODE()); } };

	static opcode_fn ld_b_xhl = new opcode_fn() { public void handler() { R.BC.SetH(M_RD_XHL()); } };
	static opcode_fn ld_c_xhl = new opcode_fn() { public void handler() { R.BC.SetL(M_RD_XHL()); } };
	static opcode_fn ld_d_xhl = new opcode_fn() { public void handler() { R.DE.SetH(M_RD_XHL()); } };
	static opcode_fn ld_e_xhl = new opcode_fn() { public void handler() { R.DE.SetL(M_RD_XHL()); } };
	static opcode_fn ld_h_xhl = new opcode_fn() { public void handler() { R.HL.SetH(M_RD_XHL()); } };
	static opcode_fn ld_l_xhl = new opcode_fn() { public void handler() { R.HL.SetL(M_RD_XHL()); } };
	static opcode_fn ld_b_xix = new opcode_fn() { public void handler() { R.BC.SetH(M_RD_XIX()); } };
	static opcode_fn ld_c_xix = new opcode_fn() { public void handler() { R.BC.SetL(M_RD_XIX()); } };
	static opcode_fn ld_d_xix = new opcode_fn() { public void handler() { R.DE.SetH(M_RD_XIX()); } };
	static opcode_fn ld_e_xix = new opcode_fn() { public void handler() { R.DE.SetL(M_RD_XIX()); } };
	static opcode_fn ld_h_xix = new opcode_fn() { public void handler() { R.HL.SetH(M_RD_XIX()); } };
	static opcode_fn ld_l_xix = new opcode_fn() { public void handler() { R.HL.SetL(M_RD_XIX()); } };
	static opcode_fn ld_b_xiy = new opcode_fn() { public void handler() { R.BC.SetH(M_RD_XIY()); } };
	static opcode_fn ld_c_xiy = new opcode_fn() { public void handler() { R.BC.SetL(M_RD_XIY()); } };
	static opcode_fn ld_d_xiy = new opcode_fn() { public void handler() { R.DE.SetH(M_RD_XIY()); } };
	static opcode_fn ld_e_xiy = new opcode_fn() { public void handler() { R.DE.SetL(M_RD_XIY()); } };
	static opcode_fn ld_h_xiy = new opcode_fn() { public void handler() { R.HL.SetH(M_RD_XIY()); } };
	static opcode_fn ld_l_xiy = new opcode_fn() { public void handler() { R.HL.SetL(M_RD_XIY()); } };
	static opcode_fn ld_a_a = new opcode_fn() { public void handler() 
        {
           System.out.println("Z80: Unsupported ld_a_a instruction!");
        } };
	static opcode_fn ld_a_b = new opcode_fn() { public void handler() { R.A = R.BC.H; } };
	static opcode_fn ld_a_c = new opcode_fn() { public void handler() { R.A = R.BC.L; } };
	static opcode_fn ld_a_d = new opcode_fn() { public void handler() { R.A = R.DE.H; } };
	static opcode_fn ld_a_e = new opcode_fn() { public void handler() { R.A = R.DE.L; } };
	static opcode_fn ld_a_h = new opcode_fn() { public void handler() { R.A = R.HL.H; } };
	static opcode_fn ld_a_l = new opcode_fn() { public void handler() { R.A = R.HL.L; } };
        static opcode_fn ld_a_ixh = new opcode_fn() { public void handler() { R.A = R.IX.H; } } ;
	static opcode_fn ld_a_ixl = new opcode_fn() { public void handler() { R.A = R.IX.L; } };
	static opcode_fn ld_a_iyh = new opcode_fn() { public void handler() { R.A = R.IY.H; } };
	static opcode_fn ld_a_iyl = new opcode_fn() { public void handler() { R.A = R.IY.L; } };
	static opcode_fn ld_b_b = new opcode_fn() {	public void handler()
        {
            System.out.println("Z80: Unsupported ld_b_b instruction!");
        } };

	static opcode_fn ld_b_a = new opcode_fn() {	public void handler() { R.BC.SetH(R.A); } };
	static opcode_fn ld_b_c = new opcode_fn() {	public void handler() { R.BC.SetH(R.BC.L); } };
	static opcode_fn ld_b_d = new opcode_fn() {	public void handler() { R.BC.SetH(R.DE.H); } };
	static opcode_fn ld_b_e = new opcode_fn() {	public void handler() { R.BC.SetH(R.DE.L); } };
	static opcode_fn ld_b_h = new opcode_fn() {	public void handler() { R.BC.SetH(R.HL.H); } };
	static opcode_fn ld_b_l = new opcode_fn() {	public void handler() { R.BC.SetH(R.HL.L); } };
	static opcode_fn ld_c_c = new opcode_fn() {	public void handler() 
        {
           System.out.println("Z80: Unsupported ld_c_c instruction!");
        } };

	static opcode_fn ld_c_a = new opcode_fn() {	public void handler() { R.BC.SetL(R.A); } };
	static opcode_fn ld_c_b = new opcode_fn() {	public void handler() { R.BC.SetL(R.BC.H); } };
	static opcode_fn ld_c_d = new opcode_fn() {	public void handler() { R.BC.SetL(R.DE.H); } };
	static opcode_fn ld_c_e = new opcode_fn() {	public void handler() { R.BC.SetL(R.DE.L); } };
	static opcode_fn ld_c_h = new opcode_fn() {	public void handler() { R.BC.SetL(R.HL.H); } };
        static opcode_fn ld_c_l = new opcode_fn() {	public void handler() { R.BC.SetL(R.HL.L); } };
	static opcode_fn ld_c_ixh = new opcode_fn() { public void handler() { R.BC.SetL(R.IX.H); } } ;
        static opcode_fn ld_d_d = new opcode_fn() {	public void handler()
        {
           System.out.println("Z80: Unsupported ld_d_d instruction!");
        } };

	static opcode_fn ld_d_a = new opcode_fn() {	public void handler() { R.DE.SetH(R.A); } };
	static opcode_fn ld_d_b = new opcode_fn() {	public void handler() { R.DE.SetH(R.BC.H);} };
	static opcode_fn ld_d_c = new opcode_fn() {	public void handler() { R.DE.SetH(R.BC.L); } };
	static opcode_fn ld_d_e = new opcode_fn() {	public void handler() { R.DE.SetH(R.DE.L); } };
	static opcode_fn ld_d_h = new opcode_fn() {	public void handler() { R.DE.SetH(R.HL.H); } };
	static opcode_fn ld_d_l = new opcode_fn() {	public void handler() { R.DE.SetH(R.HL.L); } };
	static opcode_fn ld_d_iyh = new opcode_fn() { public void handler() { R.DE.SetH(R.IY.H); } };
	static opcode_fn ld_d_iyl = new opcode_fn() { public void handler() { R.DE.SetH(R.IY.L); } };
	static opcode_fn ld_e_e = new opcode_fn() {	public void handler() 
        {
          System.out.println("Z80: Unsupported ld_e_e instruction!");
        } };
	static opcode_fn ld_e_a = new opcode_fn() {	public void handler() { R.DE.SetL(R.A); } };
	static opcode_fn ld_e_b = new opcode_fn() {	public void handler() { R.DE.SetL(R.BC.H); } };
	static opcode_fn ld_e_c = new opcode_fn() {	public void handler() { R.DE.SetL(R.BC.L); } };
	static opcode_fn ld_e_d = new opcode_fn() {	public void handler() { R.DE.SetL(R.DE.H); } };
	static opcode_fn ld_e_h = new opcode_fn() {	public void handler() { R.DE.SetL(R.HL.H); } };
	static opcode_fn ld_e_l = new opcode_fn() {	public void handler() { R.DE.SetL(R.HL.L); } };
	static opcode_fn ld_e_ixl = new opcode_fn() { public void handler() { R.DE.SetL(R.IX.L); } } ;
        static opcode_fn ld_e_iyh = new opcode_fn() { public void handler() { R.DE.SetL(R.IY.H); } };
	static opcode_fn ld_e_iyl = new opcode_fn() { public void handler() { R.DE.SetL(R.IY.L); } };
	static opcode_fn ld_h_h = new opcode_fn() {	public void handler() 
        {
           System.out.println("Z80: Unsupported ld_h_h instruction!");
        } };

	static opcode_fn ld_h_a = new opcode_fn() {	public void handler() { R.HL.SetH(R.A); } };
	static opcode_fn ld_h_b = new opcode_fn() {	public void handler() { R.HL.SetH(R.BC.H); } };
	static opcode_fn ld_h_c = new opcode_fn() {	public void handler() { R.HL.SetH(R.BC.L); } };
	static opcode_fn ld_h_d = new opcode_fn() {	public void handler() { R.HL.SetH(R.DE.H); } };
	static opcode_fn ld_h_e = new opcode_fn() {	public void handler() { R.HL.SetH(R.DE.L); } };
	static opcode_fn ld_h_l = new opcode_fn() {	public void handler() { R.HL.SetH(R.HL.L); } };
	static opcode_fn ld_l_l = new opcode_fn() {	public void handler() 
        {
            System.out.println("Z80: Unsupported ld_l_l instruction!");
        } };

        static opcode_fn ld_l_a = new opcode_fn() {	public void handler() { R.HL.SetL(R.A); } };
	static opcode_fn ld_l_b = new opcode_fn() {	public void handler() { R.HL.SetL(R.BC.H); } };
	static opcode_fn ld_l_c = new opcode_fn() {	public void handler() { R.HL.SetL(R.BC.L); } };
	static opcode_fn ld_l_d = new opcode_fn() {	public void handler() { R.HL.SetL(R.DE.H); } };
	static opcode_fn ld_l_e = new opcode_fn() {	public void handler() { R.HL.SetL(R.DE.L); } };
	static opcode_fn ld_l_h = new opcode_fn() {	public void handler() { R.HL.SetL(R.HL.H); } };
	static opcode_fn ld_ixl_a = new opcode_fn() { public void handler() { R.IX.SetL(R.A); } };
	static opcode_fn ld_iyh_a = new opcode_fn() { public void handler() { R.IY.SetH(R.A); } };
	static opcode_fn ld_iyl_a = new opcode_fn() { public void handler() { R.IY.SetL(R.A); } };
        static opcode_fn ld_ixh_a = new opcode_fn() { public void handler() { R.IX.SetH(R.A); } } ;
        static opcode_fn ld_ixh_b = new opcode_fn() { public void handler() { R.IX.SetH(R.BC.H); } } ;
        static opcode_fn ld_ixh_c = new opcode_fn() { public void handler() { R.IX.SetH(R.BC.L); } } ;
        static opcode_fn ld_ixh_d = new opcode_fn() { public void handler() { R.IX.SetH(R.DE.H); } } ;
        static opcode_fn ld_ixh_e = new opcode_fn() { public void handler() { R.IX.SetH(R.DE.L); } } ;

	static opcode_fn ld_bc_xword = new opcode_fn() { public void handler() { R.BC.SetW(M_RDMEM_WORD(M_RDMEM_OPCODE_WORD())); } };
	static opcode_fn ld_bc_word = new opcode_fn() { public void handler() { R.BC.SetW(M_RDMEM_OPCODE_WORD()); } };
	static opcode_fn ld_de_xword = new opcode_fn() { public void handler() { R.DE.SetW(M_RDMEM_WORD(M_RDMEM_OPCODE_WORD())); } };
	static opcode_fn ld_de_word = new opcode_fn() { public void handler() { R.DE.SetW(M_RDMEM_OPCODE_WORD()); } };
	static opcode_fn ld_hl_xword = new opcode_fn() { public void handler() { R.HL.SetW(M_RDMEM_WORD(M_RDMEM_OPCODE_WORD())); } };
	static opcode_fn ld_hl_word = new opcode_fn() { public void handler() { R.HL.SetW(M_RDMEM_OPCODE_WORD()); } };
	static opcode_fn ld_ix_xword = new opcode_fn() { public void handler() { R.IX.SetW(M_RDMEM_WORD(M_RDMEM_OPCODE_WORD())); } };
	static opcode_fn ld_ix_word = new opcode_fn() { public void handler() { R.IX.SetW(M_RDMEM_OPCODE_WORD()); } };
	static opcode_fn ld_iy_xword = new opcode_fn() { public void handler() { R.IY.SetW(M_RDMEM_WORD(M_RDMEM_OPCODE_WORD())); } };
	static opcode_fn ld_iy_word = new opcode_fn() { public void handler() { R.IY.SetW(M_RDMEM_OPCODE_WORD()); } };
	static opcode_fn ld_sp_xword = new opcode_fn() { public void handler() { R.SP = M_RDMEM_WORD(M_RDMEM_OPCODE_WORD()); } };
	static opcode_fn ld_sp_word = new opcode_fn() { public void handler() { R.SP = M_RDMEM_OPCODE_WORD(); } };
	static opcode_fn ld_sp_hl = new opcode_fn() {	public void handler() { R.SP = R.HL.W; } };
	static opcode_fn ld_sp_ix = new opcode_fn() {	public void handler() { R.SP = R.IX.W; } };
	static opcode_fn ld_sp_iy = new opcode_fn() {	public void handler() { R.SP = R.IY.W; } };
	static opcode_fn ld_a_i = new opcode_fn() {	public void handler()
	{
		R.A = R.I;
		R.F = (R.F & C_FLAG) | ZSTable[R.I] | (R.IFF2 << 2);
	} };
	static opcode_fn ld_i_a = new opcode_fn() {	public void handler() { R.I = R.A; } };
	static opcode_fn ld_a_r = new opcode_fn() {	public void handler()
	{
		 R.A = (R.R & 127) | (R.R2 & 128);
		 R.F = (R.F & C_FLAG) | ZSTable[R.A] | (R.IFF2 << 2);
	} };
	static opcode_fn ld_r_a = new opcode_fn() {	public void handler() { R.R = R.R2 = R.A; } };

	static opcode_fn ldd = new opcode_fn() { public void handler()
	{
		M_WRMEM(R.DE.W, M_RDMEM(R.HL.W));
		R.DE.AddW(-1);
		R.HL.AddW(-1);
		R.BC.AddW(-1);
		R.F = (R.F & 0xE9) | (R.BC.W != 0 ? V_FLAG : 0);
	} };

	static opcode_fn lddr = new opcode_fn() { public void handler()
	{
		R.R -= 2;
		do
		{
			R.R += 2;
			M_WRMEM(R.DE.W, M_RDMEM(R.HL.W));
			R.DE.AddW(-1);
			R.HL.AddW(-1);
			R.BC.AddW(-1);
			Z80_ICount -= 21;
		}
		while (R.BC.W != 0 && Z80_ICount > 0);
		R.F = (R.F & 0xE9) | (R.BC.W != 0 ? V_FLAG : 0);
		if (R.BC.W != 0) R.PC = (R.PC - 2) & 0xFFFF;
		else Z80_ICount += 5;
	 
	} };
	static opcode_fn ldi = new opcode_fn() { public void handler()
	{
		M_WRMEM(R.DE.W, M_RDMEM(R.HL.W));
		R.DE.AddW(1);
		R.HL.AddW(1);
		R.BC.AddW(-1);
		R.F = (R.F & 0xE9) | (R.BC.W != 0 ? V_FLAG : 0);
	} };
	static opcode_fn ldir = new opcode_fn() { public void handler()
	{
		R.R -= 2;
		do
		{
			R.R += 2;
			M_WRMEM(R.DE.W, M_RDMEM(R.HL.W));
			R.DE.AddW(1);
			R.HL.AddW(1);
			R.BC.AddW(-1);
			Z80_ICount -= 21;
		}
		while (R.BC.W != 0 && Z80_ICount > 0);
		R.F = (R.F & 0xE9) | (R.BC.W != 0 ? V_FLAG : 0);
		if (R.BC.W != 0) R.PC = (R.PC - 2) & 0xFFFF;
		else Z80_ICount += 5;
	} };
	static opcode_fn neg = new opcode_fn() { public void handler()
	{
		int i;
		i = R.A;
		R.A = 0;
		M_SUB(i);
	} };

	static opcode_fn nop = new opcode_fn() { public void handler() { } };

	static opcode_fn or_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_OR(i); } };
	static opcode_fn or_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_OR(i); } };
	static opcode_fn or_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_OR(i); } };
	static opcode_fn or_a = new opcode_fn() { public void handler() { R.F = ZSPTable[R.A]; } };
	static opcode_fn or_b = new opcode_fn() { public void handler() { M_OR(R.BC.H); } };
	static opcode_fn or_c = new opcode_fn() { public void handler() { M_OR(R.BC.L); } };
	static opcode_fn or_d = new opcode_fn() { public void handler() { M_OR(R.DE.H); } };
	static opcode_fn or_e = new opcode_fn() { public void handler() { M_OR(R.DE.L); } };
	static opcode_fn or_h = new opcode_fn() { public void handler() { M_OR(R.HL.H); } };
	static opcode_fn or_l = new opcode_fn() { public void handler() { M_OR(R.HL.L); } };
	static opcode_fn or_byte = new opcode_fn() { public void handler() { int i = M_RDMEM_OPCODE(); M_OR(i); } };

	static opcode_fn outi = new opcode_fn() { public void handler()
	{
		Z80_Out (R.BC.L, M_RDMEM(R.HL.W));
		R.HL.AddW(1);
		R.BC.AddH(-1);
		R.F = (R.BC.H != 0) ? N_FLAG : (Z_FLAG | N_FLAG);
	} };
	static opcode_fn otir = new opcode_fn() { public void handler()
	{
		R.R -= 2;
		do
		{
			R.R += 2;
			Z80_Out(R.BC.L, M_RDMEM(R.HL.W));
			R.HL.AddW(1);
			R.BC.AddH(-1);
			Z80_ICount -= 21;
		}
		while (R.BC.H != 0 && Z80_ICount > 0);
		R.F = (R.BC.H != 0) ? N_FLAG : (Z_FLAG | N_FLAG);
		if (R.BC.H != 0) R.PC = (R.PC - 2) & 0xFFFF;
		else Z80_ICount += 5;
	} };

	static opcode_fn out_c_a = new opcode_fn() { public void handler() { Z80_Out(R.BC.L, R.A); } };
	static opcode_fn out_c_b = new opcode_fn() { public void handler() { Z80_Out(R.BC.L, R.BC.H); } };
	static opcode_fn out_c_d = new opcode_fn() { public void handler() { Z80_Out(R.BC.L, R.DE.H); } };
	static opcode_fn out_c_e = new opcode_fn() { public void handler() { Z80_Out(R.BC.L, R.DE.L); } };
	static opcode_fn out_c_h = new opcode_fn() { public void handler() { Z80_Out(R.BC.L, R.HL.H); } };
	static opcode_fn out_c_l = new opcode_fn() { public void handler() { Z80_Out(R.BC.L, R.HL.L); } };
	static opcode_fn out_byte_a = new opcode_fn() { public void handler() { int i = M_RDMEM_OPCODE(); Z80_Out(i, R.A); } };

	static opcode_fn pop_af = new opcode_fn() { public void handler() { R.AF = M_POP(); R.A = R.AF >> 8; R.F = R.AF & 0xFF; } };
	static opcode_fn pop_bc = new opcode_fn() { public void handler() { R.BC.SetW(M_POP()); } };
	static opcode_fn pop_de = new opcode_fn() { public void handler() { R.DE.SetW(M_POP()); } };
	static opcode_fn pop_hl = new opcode_fn() { public void handler() { R.HL.SetW(M_POP()); } };
	static opcode_fn pop_ix = new opcode_fn() { public void handler() { R.IX.SetW(M_POP()); } };
	static opcode_fn pop_iy = new opcode_fn() { public void handler() { R.IY.SetW(M_POP()); } };

	static opcode_fn push_af = new opcode_fn() { public void handler() { M_PUSH((R.A << 8) | R.F); } };
	static opcode_fn push_bc = new opcode_fn() { public void handler() { M_PUSH(R.BC.W); } };
	static opcode_fn push_de = new opcode_fn() { public void handler() { M_PUSH(R.DE.W); } };
	static opcode_fn push_hl = new opcode_fn() { public void handler() { M_PUSH(R.HL.W); } };
	static opcode_fn push_ix = new opcode_fn() { public void handler() { M_PUSH(R.IX.W); } };
	static opcode_fn push_iy = new opcode_fn() { public void handler() { M_PUSH(R.IY.W); } };

	static opcode_fn res_0_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_RES(0, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn res_0_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_RES(0, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn res_0_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_RES(0, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn res_0_a = new opcode_fn() { public void handler() { R.A = M_RES(0, R.A); } };
	static opcode_fn res_0_b = new opcode_fn() { public void handler() { R.BC.SetH(M_RES(0, R.BC.H)); } };
	static opcode_fn res_0_c = new opcode_fn() { public void handler() { R.BC.SetL(M_RES(0, R.BC.L)); } };
	static opcode_fn res_0_d = new opcode_fn() { public void handler() { R.DE.SetH(M_RES(0, R.DE.H)); } };
	static opcode_fn res_0_e = new opcode_fn() { public void handler() { R.DE.SetL(M_RES(0, R.DE.L)); } };
	static opcode_fn res_0_h = new opcode_fn() { public void handler() { R.HL.SetH(M_RES(0, R.HL.H)); } };
	static opcode_fn res_0_l = new opcode_fn() { public void handler() { R.HL.SetL(M_RES(0, R.HL.L)); } };

	static opcode_fn res_1_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_RES(1, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn res_1_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_RES(1, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn res_1_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_RES(1, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn res_1_a = new opcode_fn() { public void handler() { R.A = M_RES(1, R.A); } };
	static opcode_fn res_1_b = new opcode_fn() { public void handler() { R.BC.SetH(M_RES(1, R.BC.H)); } };
	static opcode_fn res_1_c = new opcode_fn() { public void handler() { R.BC.SetL(M_RES(1, R.BC.L)); } };
	static opcode_fn res_1_d = new opcode_fn() { public void handler() { R.DE.SetH(M_RES(1, R.DE.H)); } };
	static opcode_fn res_1_e = new opcode_fn() { public void handler() { R.DE.SetL(M_RES(1, R.DE.L)); } };
	static opcode_fn res_1_h = new opcode_fn() { public void handler() { R.HL.SetH(M_RES(1, R.HL.H)); } };
	static opcode_fn res_1_l = new opcode_fn() { public void handler() { R.HL.SetL(M_RES(1, R.HL.L)); } };

	static opcode_fn res_2_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_RES(2, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn res_2_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_RES(2, i);
		M_WRMEM(j, i);
	} };
        static opcode_fn res_2_xiy = new opcode_fn()
        {
            public void handler()
            {
                int j = M_XIY();
                int i = M_RDMEM(j);
                i = M_RES(2, i);
                M_WRMEM(j, i);
            }
        };

	static opcode_fn res_2_a = new opcode_fn() { public void handler() { R.A = M_RES(2, R.A); } };
	static opcode_fn res_2_b = new opcode_fn() { public void handler() { R.BC.SetH(M_RES(2, R.BC.H)); } };
	static opcode_fn res_2_c = new opcode_fn() { public void handler() { R.BC.SetL(M_RES(2, R.BC.L)); } };
	static opcode_fn res_2_d = new opcode_fn() { public void handler() { R.DE.SetH(M_RES(2, R.DE.H)); } };
	static opcode_fn res_2_e = new opcode_fn() { public void handler() { R.DE.SetL(M_RES(2, R.DE.L)); } };
	static opcode_fn res_2_h = new opcode_fn() { public void handler() { R.HL.SetH(M_RES(2, R.HL.H)); } };
	static opcode_fn res_2_l = new opcode_fn() { public void handler() { R.HL.SetL(M_RES(2, R.HL.L)); } };

	static opcode_fn res_3_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_RES(3, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn res_3_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_RES(3, i);
		M_WRMEM(j, i);
	} };
        static opcode_fn res_3_xiy = new opcode_fn()
        {
            public void handler()
            {
                int j = M_XIY();
                int i = M_RDMEM(j);
                i = M_RES(3, i);
                M_WRMEM(j, i);
            }
        };
	static opcode_fn res_3_a = new opcode_fn() { public void handler() { R.A = M_RES(3, R.A); } };
	static opcode_fn res_3_b = new opcode_fn() { public void handler() { R.BC.SetH(M_RES(3, R.BC.H)); } };
	static opcode_fn res_3_c = new opcode_fn() { public void handler() { R.BC.SetL(M_RES(3, R.BC.L)); } };
	static opcode_fn res_3_d = new opcode_fn() { public void handler() { R.DE.SetH(M_RES(3, R.DE.H)); } };
	static opcode_fn res_3_e = new opcode_fn() { public void handler() { R.DE.SetL(M_RES(3, R.DE.L)); } };
	static opcode_fn res_3_h = new opcode_fn() { public void handler() { R.HL.SetH(M_RES(3, R.HL.H)); } };
	static opcode_fn res_3_l = new opcode_fn() { public void handler() { R.HL.SetL(M_RES(3, R.HL.L)); } };

	static opcode_fn res_4_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_RES(4, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn res_4_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_RES(4, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn res_4_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_RES(4, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn res_4_a = new opcode_fn() { public void handler() { R.A = M_RES(4, R.A); } };
	static opcode_fn res_4_b = new opcode_fn() { public void handler() { R.BC.SetH(M_RES(4, R.BC.H)); } };
	static opcode_fn res_4_c = new opcode_fn() { public void handler() { R.BC.SetL(M_RES(4, R.BC.L)); } };
	static opcode_fn res_4_d = new opcode_fn() { public void handler() { R.DE.SetH(M_RES(4, R.DE.H)); } };
	static opcode_fn res_4_e = new opcode_fn() { public void handler() { R.DE.SetL(M_RES(4, R.DE.L)); } };
	static opcode_fn res_4_h = new opcode_fn() { public void handler() { R.HL.SetH(M_RES(4, R.HL.H)); } };
	static opcode_fn res_4_l = new opcode_fn() { public void handler() { R.HL.SetL(M_RES(4, R.HL.L)); } };

	static opcode_fn res_5_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_RES(5, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn res_5_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_RES(5, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn res_5_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_RES(5, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn res_5_a = new opcode_fn() { public void handler() { R.A = M_RES(5, R.A); } };
	static opcode_fn res_5_b = new opcode_fn() { public void handler() { R.BC.SetH(M_RES(5, R.BC.H)); } };
	static opcode_fn res_5_c = new opcode_fn() { public void handler() { R.BC.SetL(M_RES(5, R.BC.L)); } };
	static opcode_fn res_5_d = new opcode_fn() { public void handler() { R.DE.SetH(M_RES(5, R.DE.H)); } };
	static opcode_fn res_5_e = new opcode_fn() { public void handler() { R.DE.SetL(M_RES(5, R.DE.L)); } };
	static opcode_fn res_5_h = new opcode_fn() { public void handler() { R.HL.SetH(M_RES(5, R.HL.H)); } };
	static opcode_fn res_5_l = new opcode_fn() { public void handler() { R.HL.SetL(M_RES(5, R.HL.L)); } };

	static opcode_fn res_6_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_RES(6, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn res_6_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_RES(6, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn res_6_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_RES(6, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn res_6_a = new opcode_fn() { public void handler() { R.A = M_RES(6, R.A); } };
	static opcode_fn res_6_b = new opcode_fn() { public void handler() { R.BC.SetH(M_RES(6, R.BC.H)); } };
	static opcode_fn res_6_c = new opcode_fn() { public void handler() { R.BC.SetL(M_RES(6, R.BC.L)); } };
	static opcode_fn res_6_d = new opcode_fn() { public void handler() { R.DE.SetH(M_RES(6, R.DE.H)); } };
	static opcode_fn res_6_e = new opcode_fn() { public void handler() { R.DE.SetL(M_RES(6, R.DE.L)); } };
	static opcode_fn res_6_h = new opcode_fn() { public void handler() { R.HL.SetH(M_RES(6, R.HL.H)); } };
	static opcode_fn res_6_l = new opcode_fn() { public void handler() { R.HL.SetL(M_RES(6, R.HL.L)); } };

	static opcode_fn res_7_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_RES(7, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn res_7_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_RES(7, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn res_7_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_RES(7, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn res_7_a = new opcode_fn() { public void handler() { R.A = M_RES(7, R.A); } };
	static opcode_fn res_7_b = new opcode_fn() { public void handler() { R.BC.SetH(M_RES(7, R.BC.H)); } };
	static opcode_fn res_7_c = new opcode_fn() { public void handler() { R.BC.SetL(M_RES(7, R.BC.L)); } };
	static opcode_fn res_7_d = new opcode_fn() { public void handler() { R.DE.SetH(M_RES(7, R.DE.H)); } };
	static opcode_fn res_7_e = new opcode_fn() { public void handler() { R.DE.SetL(M_RES(7, R.DE.L)); } };
	static opcode_fn res_7_h = new opcode_fn() { public void handler() { R.HL.SetH(M_RES(7, R.HL.H)); } };
	static opcode_fn res_7_l = new opcode_fn() { public void handler() { R.HL.SetL(M_RES(7, R.HL.L)); } };

	static opcode_fn ret = new opcode_fn() { public void handler() { M_RET(); } };
	static opcode_fn ret_c = new opcode_fn() { public void handler() { if (M_C()) { M_RET(); } else { M_SKIP_RET(); } } };
	static opcode_fn ret_m = new opcode_fn() { public void handler() { if (M_M()) { M_RET(); } else { M_SKIP_RET(); } } };
	static opcode_fn ret_nc = new opcode_fn() { public void handler() { if (M_NC()) { M_RET(); } else { M_SKIP_RET(); } } };
	static opcode_fn ret_nz = new opcode_fn() { public void handler() { if (M_NZ()) { M_RET(); } else { M_SKIP_RET(); } } };
	static opcode_fn ret_p = new opcode_fn() { public void handler() { if (M_P()) { M_RET(); } else { M_SKIP_RET(); } } };
	static opcode_fn ret_pe = new opcode_fn() { public void handler() { if (M_PE()) { M_RET(); } else { M_SKIP_RET(); } } };
	static opcode_fn ret_po = new opcode_fn() { public void handler() { if (M_PO()) { M_RET(); } else { M_SKIP_RET(); } } };
	static opcode_fn ret_z = new opcode_fn() { public void handler() { if (M_Z()) { M_RET(); } else { M_SKIP_RET(); } } };

	static opcode_fn reti = new opcode_fn() { public void handler() { Z80_Reti(); M_RET(); } };
	static opcode_fn retn = new opcode_fn() { public void handler() { R.IFF1 = R.IFF2; Z80_Retn(); M_RET(); } };

	static opcode_fn rl_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_RL(i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn rl_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_RL(i);
		M_WRMEM(j, i);
	} };
	static opcode_fn rl_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_RL(i);
		M_WRMEM(j, i);
	} };
	static opcode_fn rl_a = new opcode_fn() { public void handler() { R.A = M_RL(R.A); } };
	static opcode_fn rl_b = new opcode_fn() { public void handler() { R.BC.SetH(M_RL(R.BC.H)); } };
	static opcode_fn rl_c = new opcode_fn() { public void handler() { R.BC.SetL(M_RL(R.BC.L)); } };
	static opcode_fn rl_d = new opcode_fn() { public void handler() { R.DE.SetH(M_RL(R.DE.H)); } };
	static opcode_fn rl_e = new opcode_fn() { public void handler() { R.DE.SetL(M_RL(R.DE.L)); } };
	static opcode_fn rl_h = new opcode_fn() { public void handler() { R.HL.SetH(M_RL(R.HL.H)); } };
	static opcode_fn rl_l = new opcode_fn() { public void handler() { R.HL.SetL(M_RL(R.HL.L)); } };
	static opcode_fn rla = new opcode_fn() { public void handler() { M_RLA(); } };

	static opcode_fn rlc_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_RLC(i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn rlc_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_RLC(i);
		M_WRMEM(j, i);
	} };
	static opcode_fn rlc_a = new opcode_fn() { public void handler() { R.A = M_RLC(R.A); } };
	static opcode_fn rlc_b = new opcode_fn() { public void handler() { R.BC.SetH(M_RLC(R.BC.H)); } };
	static opcode_fn rlc_c = new opcode_fn() { public void handler() { R.BC.SetL(M_RLC(R.BC.L)); } };
	static opcode_fn rlc_d = new opcode_fn() { public void handler() { R.DE.SetH(M_RLC(R.DE.H)); } };
	static opcode_fn rlc_e = new opcode_fn() { public void handler() { R.DE.SetL(M_RLC(R.DE.L)); } };
	static opcode_fn rlc_h = new opcode_fn() { public void handler() { R.HL.SetH(M_RLC(R.HL.H)); } };
	static opcode_fn rlc_l = new opcode_fn() { public void handler() { R.HL.SetL(M_RLC(R.HL.L)); } };
	static opcode_fn rlca = new opcode_fn() { public void handler() { M_RLCA(); } };

	static opcode_fn rld = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		M_WRMEM(R.HL.W, ((i << 4) | (R.A & 0x0F)) & 0xFF);
		R.A = ((R.A & 0xF0) | (i >> 4)) & 0xFF;
		R.F = (R.F & C_FLAG) | ZSPTable[R.A];
	} };

	static opcode_fn rr_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_RR(i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn rr_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_RR(i);
		M_WRMEM(j, i);
	} };
	static opcode_fn rr_a = new opcode_fn() { public void handler() { R.A = M_RR(R.A); } };
	static opcode_fn rr_b = new opcode_fn() { public void handler() { R.BC.SetH(M_RR(R.BC.H)); } };
	static opcode_fn rr_c = new opcode_fn() { public void handler() { R.BC.SetL(M_RR(R.BC.L)); } };
	static opcode_fn rr_d = new opcode_fn() { public void handler() { R.DE.SetH(M_RR(R.DE.H)); } };
	static opcode_fn rr_e = new opcode_fn() { public void handler() { R.DE.SetL(M_RR(R.DE.L)); } };
	static opcode_fn rr_h = new opcode_fn() { public void handler() { R.HL.SetH(M_RR(R.HL.H)); } };
	static opcode_fn rr_l = new opcode_fn() { public void handler() { R.HL.SetL(M_RR(R.HL.L)); } };
	static opcode_fn rra = new opcode_fn() { public void handler() { M_RRA(); } };

	static opcode_fn rrc_xhl = new opcode_fn() { public void handler()
	{
		int i;
	 	i = M_RDMEM(R.HL.W);
	 	i = M_RRC(i);
	 	M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn rrc_xix = new opcode_fn() { public void handler()
	{
		int i;
	 	int j;
	 	j = M_XIX();
	 	i = M_RDMEM(j);
	 	i = M_RRC(i);
	 	M_WRMEM(j, i);
	} };
	static opcode_fn rrc_a = new opcode_fn() { public void handler() { R.A = M_RRC(R.A); } };
	static opcode_fn rrc_b = new opcode_fn() { public void handler() { R.BC.SetH(M_RRC(R.BC.H)); } };
	static opcode_fn rrc_c = new opcode_fn() { public void handler() { R.BC.SetL(M_RRC(R.BC.L)); } };
	static opcode_fn rrc_d = new opcode_fn() { public void handler() { R.DE.SetH(M_RRC(R.DE.H)); } };
	static opcode_fn rrc_e = new opcode_fn() { public void handler() { R.DE.SetL(M_RRC(R.DE.L)); } };
	static opcode_fn rrc_h = new opcode_fn() { public void handler() { R.HL.SetH(M_RRC(R.HL.H)); } };
	static opcode_fn rrc_l = new opcode_fn() { public void handler() { R.HL.SetL(M_RRC(R.HL.L)); } };
	static opcode_fn rrca = new opcode_fn() { public void handler() { M_RRCA(); } };

	static opcode_fn rrd = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		M_WRMEM(R.HL.W, ((i >> 4) | (R.A << 4)) & 0xFF); 
		R.A = ((R.A & 0xF0) | (i & 0x0F)) & 0xFF;
		R.F = (R.F & C_FLAG) | ZSPTable[R.A];
	} };
	static opcode_fn rst_00 = new opcode_fn() { public void handler() { M_RST(0x00); } };
	static opcode_fn rst_08 = new opcode_fn() { public void handler() { M_RST(0x08); } };
	static opcode_fn rst_10 = new opcode_fn() { public void handler() { M_RST(0x10); } };
	static opcode_fn rst_18 = new opcode_fn() { public void handler() { M_RST(0x18); } };
	static opcode_fn rst_20 = new opcode_fn() { public void handler() { M_RST(0x20); } };
	static opcode_fn rst_28 = new opcode_fn() { public void handler() { M_RST(0x28); } };
	static opcode_fn rst_30 = new opcode_fn() { public void handler() { M_RST(0x30); } };
	static opcode_fn rst_38 = new opcode_fn() { public void handler() { M_RST(0x38); } };

	static opcode_fn sbc_a_byte = new opcode_fn() { public void handler() { int i = M_RDMEM_OPCODE(); M_SBC(i); } };
	static opcode_fn sbc_a_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_SBC(i); } };
	static opcode_fn sbc_a_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_SBC(i); } };
	static opcode_fn sbc_a_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_SBC(i); } };
	
	static opcode_fn sbc_a_a = new opcode_fn() { public void handler() { M_SBC(R.A); } };
	static opcode_fn sbc_a_b = new opcode_fn() { public void handler() { M_SBC(R.BC.H); } };
	static opcode_fn sbc_a_c = new opcode_fn() { public void handler() { M_SBC(R.BC.L); } };
	static opcode_fn sbc_a_d = new opcode_fn() { public void handler() { M_SBC(R.DE.H); } };
	static opcode_fn sbc_a_e = new opcode_fn() { public void handler() { M_SBC(R.DE.L); } };
	static opcode_fn sbc_a_h = new opcode_fn() { public void handler() { M_SBC(R.HL.H); } };
	static opcode_fn sbc_a_l = new opcode_fn() { public void handler() { M_SBC(R.HL.L); } };

	static opcode_fn sbc_hl_bc = new opcode_fn() { public void handler() { M_SBCW(R.BC.W); } };
	static opcode_fn sbc_hl_de = new opcode_fn() { public void handler() { M_SBCW(R.DE.W); } };
	static opcode_fn sbc_hl_hl = new opcode_fn() { public void handler() { M_SBCW(R.HL.W); } };
	static opcode_fn sbc_hl_sp = new opcode_fn() { public void handler() { M_SBCW(R.SP); } };

	static opcode_fn scf = new opcode_fn() { public void handler() { R.F = (R.F & 0xEC) | C_FLAG; } };

	static opcode_fn set_0_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_SET(0, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn set_0_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_SET(0, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_0_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_SET(0, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_0_a = new opcode_fn() { public void handler() { R.A = M_SET(0, R.A); } };
	static opcode_fn set_0_b = new opcode_fn() { public void handler() { R.BC.SetH(M_SET(0, R.BC.H)); } };
	static opcode_fn set_0_c = new opcode_fn() { public void handler() { R.BC.SetL(M_SET(0, R.BC.L)); } };
	static opcode_fn set_0_d = new opcode_fn() { public void handler() { R.DE.SetH(M_SET(0, R.DE.H)); } };
	static opcode_fn set_0_e = new opcode_fn() { public void handler() { R.DE.SetL(M_SET(0, R.DE.L)); } };
	static opcode_fn set_0_h = new opcode_fn() { public void handler() { R.HL.SetH(M_SET(0, R.HL.H)); } };
	static opcode_fn set_0_l = new opcode_fn() { public void handler() { R.HL.SetL(M_SET(0, R.HL.L)); } };

	static opcode_fn set_1_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_SET(1, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn set_1_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_SET(1, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_1_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_SET(1, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_1_a = new opcode_fn() { public void handler() { R.A = M_SET(1, R.A); } };
	static opcode_fn set_1_b = new opcode_fn() { public void handler() { R.BC.SetH(M_SET(1, R.BC.H)); } };
	static opcode_fn set_1_c = new opcode_fn() { public void handler() { R.BC.SetL(M_SET(1, R.BC.L)); } };
	static opcode_fn set_1_d = new opcode_fn() { public void handler() { R.DE.SetH(M_SET(1, R.DE.H)); } };
	static opcode_fn set_1_e = new opcode_fn() { public void handler() { R.DE.SetL(M_SET(1, R.DE.L)); } };
	static opcode_fn set_1_h = new opcode_fn() { public void handler() { R.HL.SetH(M_SET(1, R.HL.H)); } };
	static opcode_fn set_1_l = new opcode_fn() { public void handler() { R.HL.SetL(M_SET(1, R.HL.L)); } };

	static opcode_fn set_2_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_SET(2, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn set_2_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_SET(2, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_2_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_SET(2, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_2_a = new opcode_fn() { public void handler() { R.A = M_SET(2, R.A); } };
	static opcode_fn set_2_b = new opcode_fn() { public void handler() { R.BC.SetH(M_SET(2, R.BC.H)); } };
	static opcode_fn set_2_c = new opcode_fn() { public void handler() { R.BC.SetL(M_SET(2, R.BC.L)); } };
	static opcode_fn set_2_d = new opcode_fn() { public void handler() { R.DE.SetH(M_SET(2, R.DE.H)); } };
	static opcode_fn set_2_e = new opcode_fn() { public void handler() { R.DE.SetL(M_SET(2, R.DE.L)); } };
	static opcode_fn set_2_h = new opcode_fn() { public void handler() { R.HL.SetH(M_SET(2, R.HL.H)); } };
	static opcode_fn set_2_l = new opcode_fn() { public void handler() { R.HL.SetL(M_SET(2, R.HL.L)); } };

	static opcode_fn set_3_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_SET(3, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn set_3_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_SET(3, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_3_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_SET(3, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_3_a = new opcode_fn() { public void handler() { R.A = M_SET(3, R.A); } };
	static opcode_fn set_3_b = new opcode_fn() { public void handler() { R.BC.SetH(M_SET(3, R.BC.H)); } };
	static opcode_fn set_3_c = new opcode_fn() { public void handler() { R.BC.SetL(M_SET(3, R.BC.L)); } };
	static opcode_fn set_3_d = new opcode_fn() { public void handler() { R.DE.SetH(M_SET(3, R.DE.H)); } };
	static opcode_fn set_3_e = new opcode_fn() { public void handler() { R.DE.SetL(M_SET(3, R.DE.L)); } };
	static opcode_fn set_3_h = new opcode_fn() { public void handler() { R.HL.SetH(M_SET(3, R.HL.H)); } };
	static opcode_fn set_3_l = new opcode_fn() { public void handler() { R.HL.SetL(M_SET(3, R.HL.L)); } };

	static opcode_fn set_4_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_SET(4, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn set_4_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_SET(4, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_4_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_SET(4, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_4_a = new opcode_fn() { public void handler() { R.A = M_SET(4, R.A); } };
	static opcode_fn set_4_b = new opcode_fn() { public void handler() { R.BC.SetH(M_SET(4, R.BC.H)); } };
	static opcode_fn set_4_c = new opcode_fn() { public void handler() { R.BC.SetL(M_SET(4, R.BC.L)); } };
	static opcode_fn set_4_d = new opcode_fn() { public void handler() { R.DE.SetH(M_SET(4, R.DE.H)); } };
	static opcode_fn set_4_e = new opcode_fn() { public void handler() { R.DE.SetL(M_SET(4, R.DE.L)); } };
	static opcode_fn set_4_h = new opcode_fn() { public void handler() { R.HL.SetH(M_SET(4, R.HL.H)); } };
	static opcode_fn set_4_l = new opcode_fn() { public void handler() { R.HL.SetL(M_SET(4, R.HL.L)); } };

	static opcode_fn set_5_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_SET(5, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn set_5_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_SET(5, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_5_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_SET(5, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_5_a = new opcode_fn() { public void handler() { R.A = M_SET(5, R.A); } };
	static opcode_fn set_5_b = new opcode_fn() { public void handler() { R.BC.SetH(M_SET(5, R.BC.H)); } };
	static opcode_fn set_5_c = new opcode_fn() { public void handler() { R.BC.SetL(M_SET(5, R.BC.L)); } };
	static opcode_fn set_5_d = new opcode_fn() { public void handler() { R.DE.SetH(M_SET(5, R.DE.H)); } };
	static opcode_fn set_5_e = new opcode_fn() { public void handler() { R.DE.SetL(M_SET(5, R.DE.L)); } };
	static opcode_fn set_5_h = new opcode_fn() { public void handler() { R.HL.SetH(M_SET(5, R.HL.H)); } };
	static opcode_fn set_5_l = new opcode_fn() { public void handler() { R.HL.SetL(M_SET(5, R.HL.L)); } };

	static opcode_fn set_6_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_SET(6, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn set_6_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_SET(6, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_6_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_SET(6, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_6_a = new opcode_fn() { public void handler() { R.A = M_SET(6, R.A); } };
	static opcode_fn set_6_b = new opcode_fn() { public void handler() { R.BC.SetH(M_SET(6, R.BC.H)); } };
	static opcode_fn set_6_c = new opcode_fn() { public void handler() { R.BC.SetL(M_SET(6, R.BC.L)); } };
	static opcode_fn set_6_d = new opcode_fn() { public void handler() { R.DE.SetH(M_SET(6, R.DE.H)); } };
	static opcode_fn set_6_e = new opcode_fn() { public void handler() { R.DE.SetL(M_SET(6, R.DE.L)); } };
	static opcode_fn set_6_h = new opcode_fn() { public void handler() { R.HL.SetH(M_SET(6, R.HL.H)); } };
	static opcode_fn set_6_l = new opcode_fn() { public void handler() { R.HL.SetL(M_SET(6, R.HL.L)); } };

	static opcode_fn set_7_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_SET(7, i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn set_7_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_SET(7, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_7_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_SET(7, i);
		M_WRMEM(j, i);
	} };
	static opcode_fn set_7_a = new opcode_fn() { public void handler() { R.A = M_SET(7, R.A); } };
	static opcode_fn set_7_b = new opcode_fn() { public void handler() { R.BC.SetH(M_SET(7, R.BC.H)); } };
	static opcode_fn set_7_c = new opcode_fn() { public void handler() { R.BC.SetL(M_SET(7, R.BC.L)); } };
	static opcode_fn set_7_d = new opcode_fn() { public void handler() { R.DE.SetH(M_SET(7, R.DE.H)); } };
	static opcode_fn set_7_e = new opcode_fn() { public void handler() { R.DE.SetL(M_SET(7, R.DE.L)); } };
	static opcode_fn set_7_h = new opcode_fn() { public void handler() { R.HL.SetH(M_SET(7, R.HL.H)); } };
	static opcode_fn set_7_l = new opcode_fn() { public void handler() { R.HL.SetL(M_SET(7, R.HL.L)); } };

	static opcode_fn sla_xhl = new opcode_fn() { public void handler()
	{
		int i;
		i = M_RDMEM(R.HL.W);
		i = M_SLA(i);
		M_WRMEM(R.HL.W, i);
	} };
	static opcode_fn sla_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_SLA(i);
		M_WRMEM(j, i);
	} };
	static opcode_fn sla_xiy = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIY();
		i = M_RDMEM(j);
		i = M_SLA(i);
		M_WRMEM(j, i);
	} };

	static opcode_fn sla_a = new opcode_fn() { public void handler() { R.A = M_SLA(R.A); } };
	static opcode_fn sla_b = new opcode_fn() { public void handler() { R.BC.SetH(M_SLA(R.BC.H)); } };
	static opcode_fn sla_c = new opcode_fn() { public void handler() { R.BC.SetL(M_SLA(R.BC.L)); } };
	static opcode_fn sla_d = new opcode_fn() { public void handler() { R.DE.SetH(M_SLA(R.DE.H)); } };
	static opcode_fn sla_e = new opcode_fn() { public void handler() { R.DE.SetL(M_SLA(R.DE.L)); } };
	static opcode_fn sla_h = new opcode_fn() { public void handler() { R.HL.SetH(M_SLA(R.HL.H)); } };
	static opcode_fn sla_l = new opcode_fn() { public void handler() { R.HL.SetL(M_SLA(R.HL.L)); } };

	static opcode_fn sra_a = new opcode_fn() { public void handler() { R.A = M_SRA(R.A); } };
	static opcode_fn sra_b = new opcode_fn() { public void handler() { R.BC.SetH(M_SRA(R.BC.H)); } };
	static opcode_fn sra_c = new opcode_fn() { public void handler() { R.BC.SetL(M_SRA(R.BC.L)); } };
	static opcode_fn sra_d = new opcode_fn() { public void handler() { R.DE.SetH(M_SRA(R.DE.H)); } };
	static opcode_fn sra_e = new opcode_fn() { public void handler() { R.DE.SetL(M_SRA(R.DE.L)); } };
	static opcode_fn sra_h = new opcode_fn() { public void handler() { R.HL.SetH(M_SRA(R.HL.H)); } };
	static opcode_fn sra_l = new opcode_fn() { public void handler() { R.HL.SetL(M_SRA(R.HL.L)); } };

	static opcode_fn srl_xix = new opcode_fn() { public void handler()
	{
		int i;
		int j;
		j = M_XIX();
		i = M_RDMEM(j);
		i = M_SRL(i);
		M_WRMEM(j, i);
	} };
	static opcode_fn srl_a = new opcode_fn() { public void handler() { R.A = M_SRL(R.A); } };
	static opcode_fn srl_b = new opcode_fn() { public void handler() { R.BC.SetH(M_SRL(R.BC.H)); } };
	static opcode_fn srl_c = new opcode_fn() { public void handler() { R.BC.SetL(M_SRL(R.BC.L)); } };
	static opcode_fn srl_d = new opcode_fn() { public void handler() { R.DE.SetH(M_SRL(R.DE.H)); } };
	static opcode_fn srl_e = new opcode_fn() { public void handler() { R.DE.SetL(M_SRL(R.DE.L)); } };
	static opcode_fn srl_h = new opcode_fn() { public void handler() { R.HL.SetH(M_SRL(R.HL.H)); } };
	static opcode_fn srl_l = new opcode_fn() { public void handler() { R.HL.SetL(M_SRL(R.HL.L)); } };

	static opcode_fn sub_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_SUB(i); } };
	static opcode_fn sub_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_SUB(i); } };
	static opcode_fn sub_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_SUB(i); } };
	static opcode_fn sub_a = new opcode_fn() { public void handler() { R.A = 0; R.F = Z_FLAG | N_FLAG; } };
	static opcode_fn sub_b = new opcode_fn() { public void handler() { M_SUB(R.BC.H); } };
	static opcode_fn sub_c = new opcode_fn() { public void handler() { M_SUB(R.BC.L); } };
	static opcode_fn sub_d = new opcode_fn() { public void handler() { M_SUB(R.DE.H); } };
	static opcode_fn sub_e = new opcode_fn() { public void handler() { M_SUB(R.DE.L); } };
	static opcode_fn sub_h = new opcode_fn() { public void handler() { M_SUB(R.HL.H); } };
	static opcode_fn sub_l = new opcode_fn() { public void handler() { M_SUB(R.HL.L); } };
        static opcode_fn sub_ixh = new opcode_fn() { public void handler() { M_SUB(Z80.R.IX.H); } } ;
        static opcode_fn sub_ixl = new opcode_fn() { public void handler() { M_SUB(Z80.R.IX.L); } } ;
	static opcode_fn sub_byte = new opcode_fn() { public void handler() { int i = M_RDMEM_OPCODE(); M_SUB(i); } };

	static opcode_fn xor_xhl = new opcode_fn() { public void handler() { int i = M_RD_XHL(); M_XOR(i); } };
	static opcode_fn xor_xix = new opcode_fn() { public void handler() { int i = M_RD_XIX(); M_XOR(i); } };
	static opcode_fn xor_xiy = new opcode_fn() { public void handler() { int i = M_RD_XIY(); M_XOR(i); } } ;
        static opcode_fn xor_a = new opcode_fn() { public void handler() { R.A = 0; R.F = Z_FLAG | V_FLAG; } };
	static opcode_fn xor_b = new opcode_fn() { public void handler() { M_XOR(R.BC.H); } };
	static opcode_fn xor_c = new opcode_fn() { public void handler() { M_XOR(R.BC.L); } };
	static opcode_fn xor_d = new opcode_fn() { public void handler() { M_XOR(R.DE.H); } };
	static opcode_fn xor_e = new opcode_fn() { public void handler() { M_XOR(R.DE.L); } };
	static opcode_fn xor_h = new opcode_fn() { public void handler() { M_XOR(R.HL.H); } };
	static opcode_fn xor_l = new opcode_fn() { public void handler() { M_XOR(R.HL.L); } };
	static opcode_fn xor_byte = new opcode_fn() { public void handler() { int i = M_RDMEM_OPCODE(); M_XOR(i); } };
        static opcode_fn xor_ixh = new opcode_fn() { public void handler() { M_XOR(R.IX.H); } } ;
	static opcode_fn no_op = new opcode_fn() { public void handler()
	{
		R.PC = (R.PC - 1) & 0xFFFF;
	} };

  static int[] cycles_main = { 4, 10, 7, 6, 4, 4, 7, 4, 4, 11, 7, 6, 4, 4, 7, 4, 8, 10, 7, 6, 4, 4, 7, 4, 7, 11, 7, 6, 4, 4, 7, 4, 7, 10, 16, 6, 4, 4, 7, 4, 7, 11, 16, 6, 4, 4, 7, 4, 7, 10, 13, 6, 11, 11, 10, 4, 7, 11, 13, 6, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 7, 7, 7, 7, 7, 7, 4, 7, 4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, 5, 10, 10, 10, 10, 11, 7, 11, 5, 4, 10, 0, 10, 10, 7, 11, 5, 10, 10, 11, 10, 11, 7, 11, 5, 4, 10, 11, 10, 0, 7, 11, 5, 10, 10, 19, 10, 11, 7, 11, 5, 4, 10, 4, 10, 0, 7, 11, 5, 10, 10, 4, 10, 11, 7, 11, 5, 6, 10, 4, 10, 0, 7, 11 };

  static int[] cycles_cb = { 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 12, 8, 8, 8, 8, 8, 8, 8, 12, 8, 8, 8, 8, 8, 8, 8, 12, 8, 8, 8, 8, 8, 8, 8, 12, 8, 8, 8, 8, 8, 8, 8, 12, 8, 8, 8, 8, 8, 8, 8, 12, 8, 8, 8, 8, 8, 8, 8, 12, 8, 8, 8, 8, 8, 8, 8, 12, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8, 8, 8, 8, 8, 8, 8, 15, 8 };

  static int[] cycles_xx_cb = { 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 0, 0, 23, 0 };

  static int[] cycles_xx = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0, 0, 0, 14, 20, 10, 9, 9, 9, 0, 0, 15, 20, 10, 9, 9, 9, 0, 0, 0, 0, 0, 23, 23, 19, 0, 0, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 19, 0, 0, 0, 0, 0, 9, 9, 19, 0, 0, 0, 0, 0, 9, 9, 19, 0, 0, 0, 0, 0, 9, 9, 19, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 19, 19, 19, 19, 19, 19, 19, 19, 0, 0, 0, 0, 9, 9, 19, 0, 0, 0, 0, 0, 9, 9, 19, 0, 0, 0, 0, 0, 9, 9, 19, 0, 0, 0, 0, 0, 9, 9, 19, 0, 0, 0, 0, 0, 9, 9, 19, 0, 0, 0, 0, 0, 9, 9, 19, 0, 0, 0, 0, 0, 9, 9, 19, 0, 0, 0, 0, 0, 9, 9, 19, 0, 0, 0, 0, 0, 9, 9, 19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 0, 23, 0, 15, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0 };

  static int[] cycles_ed = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 12, 15, 20, 8, 8, 8, 9, 12, 12, 15, 20, 8, 8, 8, 9, 12, 12, 15, 20, 8, 8, 8, 9, 12, 12, 15, 20, 8, 8, 8, 9, 12, 12, 15, 20, 8, 8, 8, 18, 12, 12, 15, 20, 8, 8, 8, 18, 12, 12, 15, 20, 8, 8, 8, 0, 12, 12, 15, 20, 8, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 16, 16, 16, 0, 0, 0, 0, 16, 16, 16, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };


	static opcode_fn no_op_xx = new opcode_fn() { public void handler() {
		R.PC = (R.PC + 1) & 0xFFFF;
	} };
        static opcode_fn[] opcode_dd_cb = { no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, rlc_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, rrc_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, rl_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, rr_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, sla_xix, no_op_xx, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, srl_xix, no_op_xx, bit_0_xix, bit_0_xix, bit_0_xix, bit_0_xix, bit_0_xix, bit_0_xix, bit_0_xix, bit_0_xix, bit_1_xix, bit_1_xix, bit_1_xix, bit_1_xix, bit_1_xix, bit_1_xix, bit_1_xix, bit_1_xix, bit_2_xix, bit_2_xix, bit_2_xix, bit_2_xix, bit_2_xix, bit_2_xix, bit_2_xix, bit_2_xix, bit_3_xix, bit_3_xix, bit_3_xix, bit_3_xix, bit_3_xix, bit_3_xix, bit_3_xix, bit_3_xix, bit_4_xix, bit_4_xix, bit_4_xix, bit_4_xix, bit_4_xix, bit_4_xix, bit_4_xix, bit_4_xix, bit_5_xix, bit_5_xix, bit_5_xix, bit_5_xix, bit_5_xix, bit_5_xix, bit_5_xix, bit_5_xix, bit_6_xix, bit_6_xix, bit_6_xix, bit_6_xix, bit_6_xix, bit_6_xix, bit_6_xix, bit_6_xix, bit_7_xix, bit_7_xix, bit_7_xix, bit_7_xix, bit_7_xix, bit_7_xix, bit_7_xix, bit_7_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_0_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_1_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_2_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_3_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_4_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_5_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_6_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_7_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_0_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_1_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_2_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_3_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_4_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_5_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_6_xix, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_7_xix, no_op_xx };

        static opcode_fn[] opcode_fd_cb = { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, rl_xiy, no_op_xx, null, null, null, null, null, null, null, null, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, sla_xiy, no_op_xx, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, bit_0_xiy, bit_0_xiy, bit_0_xiy, bit_0_xiy, bit_0_xiy, bit_0_xiy, bit_0_xiy, bit_0_xiy, bit_1_xiy, bit_1_xiy, bit_1_xiy, bit_1_xiy, bit_1_xiy, bit_1_xiy, bit_1_xiy, bit_1_xiy, bit_2_xiy, bit_2_xiy, bit_2_xiy, bit_2_xiy, bit_2_xiy, bit_2_xiy, bit_2_xiy, bit_2_xiy, bit_3_xiy, bit_3_xiy, bit_3_xiy, bit_3_xiy, bit_3_xiy, bit_3_xiy, bit_3_xiy, bit_3_xiy, bit_4_xiy, bit_4_xiy, bit_4_xiy, bit_4_xiy, bit_4_xiy, bit_4_xiy, bit_4_xiy, bit_4_xiy, bit_5_xiy, bit_5_xiy, bit_5_xiy, bit_5_xiy, bit_5_xiy, bit_5_xiy, bit_5_xiy, bit_5_xiy, bit_6_xiy, bit_6_xiy, bit_6_xiy, bit_6_xiy, bit_6_xiy, bit_6_xiy, bit_6_xiy, bit_6_xiy, bit_7_xiy, bit_7_xiy, bit_7_xiy, bit_7_xiy, bit_7_xiy, bit_7_xiy, bit_7_xiy, bit_7_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_0_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_1_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_2_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_3_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_4_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_5_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_6_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, res_7_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_0_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_1_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_2_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_3_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_4_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_5_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_6_xiy, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, no_op_xx, set_7_xiy, no_op_xx };


	static opcode_fn dd_cb = new opcode_fn() { public void handler()
	{
	 	int opcode=M_RDOP_ARG((R.PC+1)&0xFFFF);
	 	Z80_ICount -= cycles_xx_cb[opcode];
  		if (opcode_dd_cb[opcode] != null)
  			opcode_dd_cb[opcode].handler();
  		else
  		{
	  		System.out.println("DD CB PC = " + Integer.toHexString(R.PC) + " OPCODE = " + Integer.toHexString(opcode));
	  	}
		R.PC = (R.PC + 1) & 0xFFFF;
	} };
	static opcode_fn fd_cb = new opcode_fn() { public void handler()
	{

		int opcode=M_RDOP_ARG((R.PC+1)&0xFFFF);
	 	Z80_ICount -= cycles_xx_cb[opcode];
  		if (opcode_fd_cb[opcode] != null)
  			opcode_fd_cb[opcode].handler();
  		else
  		{
	  		System.out.println("FD CB PC = " + Integer.toHexString(R.PC) + " OPCODE = " + Integer.toHexString(opcode));
	  	}
		R.PC = (R.PC + 1) & 0xFFFF;
	} };

        static opcode_fn[] opcode_cb = { rlc_b, rlc_c, rlc_d, rlc_e, rlc_h, rlc_l, rlc_xhl, rlc_a, rrc_b, rrc_c, rrc_d, rrc_e, rrc_h, rrc_l, rrc_xhl, rrc_a, rl_b, rl_c, rl_d, rl_e, rl_h, rl_l, rl_xhl, rl_a, rr_b, rr_c, rr_d, rr_e, rr_h, rr_l, rr_xhl, rr_a, sla_b, sla_c, sla_d, sla_e, sla_h, sla_l, sla_xhl, sla_a, sra_b, sra_c, sra_d, sra_e, sra_h, sra_l, null, sra_a, null, null, null, null, null, null, null, null, srl_b, srl_c, srl_d, srl_e, srl_h, srl_l, null, srl_a, bit_0_b, bit_0_c, bit_0_d, bit_0_e, bit_0_h, bit_0_l, bit_0_xhl, bit_0_a, bit_1_b, bit_1_c, bit_1_d, bit_1_e, bit_1_h, bit_1_l, bit_1_xhl, bit_1_a, bit_2_b, bit_2_c, bit_2_d, bit_2_e, bit_2_h, bit_2_l, bit_2_xhl, bit_2_a, bit_3_b, bit_3_c, bit_3_d, bit_3_e, bit_3_h, bit_3_l, bit_3_xhl, bit_3_a, bit_4_b, bit_4_c, bit_4_d, bit_4_e, bit_4_h, bit_4_l, bit_4_xhl, bit_4_a, bit_5_b, bit_5_c, bit_5_d, bit_5_e, bit_5_h, bit_5_l, bit_5_xhl, bit_5_a, bit_6_b, bit_6_c, bit_6_d, bit_6_e, bit_6_h, bit_6_l, bit_6_xhl, bit_6_a, bit_7_b, bit_7_c, bit_7_d, bit_7_e, bit_7_h, bit_7_l, bit_7_xhl, bit_7_a, res_0_b, res_0_c, res_0_d, res_0_e, res_0_h, res_0_l, res_0_xhl, res_0_a, res_1_b, res_1_c, res_1_d, res_1_e, res_1_h, res_1_l, res_1_xhl, res_1_a, res_2_b, res_2_c, res_2_d, res_2_e, res_2_h, res_2_l, res_2_xhl, res_2_a, res_3_b, res_3_c, res_3_d, res_3_e, res_3_h, res_3_l, res_3_xhl, res_3_a, res_4_b, res_4_c, res_4_d, res_4_e, res_4_h, res_4_l, res_4_xhl, res_4_a, res_5_b, res_5_c, res_5_d, res_5_e, res_5_h, res_5_l, res_5_xhl, res_5_a, res_6_b, res_6_c, res_6_d, res_6_e, res_6_h, res_6_l, res_6_xhl, res_6_a, res_7_b, res_7_c, res_7_d, res_7_e, res_7_h, res_7_l, res_7_xhl, res_7_a, set_0_b, set_0_c, set_0_d, set_0_e, set_0_h, set_0_l, set_0_xhl, set_0_a, set_1_b, set_1_c, set_1_d, set_1_e, set_1_h, set_1_l, set_1_xhl, set_1_a, set_2_b, set_2_c, set_2_d, set_2_e, set_2_h, set_2_l, set_2_xhl, set_2_a, set_3_b, set_3_c, set_3_d, set_3_e, set_3_h, set_3_l, set_3_xhl, set_3_a, set_4_b, set_4_c, set_4_d, set_4_e, set_4_h, set_4_l, set_4_xhl, set_4_a, set_5_b, set_5_c, set_5_d, set_5_e, set_5_h, set_5_l, set_5_xhl, set_5_a, set_6_b, set_6_c, set_6_d, set_6_e, set_6_h, set_6_l, set_6_xhl, set_6_a, set_7_b, set_7_c, set_7_d, set_7_e, set_7_h, set_7_l, set_7_xhl, set_7_a };

        static opcode_fn[] opcode_dd = { no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, add_ix_bc, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, add_ix_de, no_op, no_op, no_op, no_op, no_op, no_op, null, ld_ix_word, ld_xword_ix, inc_ix, null, null, ld_ixh_byte, null, no_op, add_ix_ix, ld_ix_xword, dec_ix, inc_ixl, dec_ixl, ld_ixl_byte, no_op, no_op, no_op, no_op, no_op, inc_xix, dec_xix, ld_xix_byte, no_op, no_op, add_ix_sp, no_op, no_op, no_op, no_op, no_op, no_op, null, null, null, null, null, null, ld_b_xix, null, null, null, null, null, ld_c_ixh, null, ld_c_xix, null, null, null, null, null, null, null, ld_d_xix, null, null, null, null, null, null, ld_e_ixl, ld_e_xix, null, ld_ixh_b, ld_ixh_c, ld_ixh_d, ld_ixh_e, null, null, ld_h_xix, ld_ixh_a, null, null, null, null, null, null, ld_l_xix, ld_ixl_a, ld_xix_b, ld_xix_c, ld_xix_d, ld_xix_e, ld_xix_h, ld_xix_l, no_op, ld_xix_a, no_op, no_op, no_op, no_op, ld_a_ixh, ld_a_ixl, ld_a_xix, no_op, no_op, no_op, no_op, no_op, add_a_ixh, add_a_ixl, add_a_xix, no_op, null, null, null, null, null, null, adc_a_xix, null, no_op, no_op, no_op, no_op, sub_ixh, sub_ixl, sub_xix, no_op, null, null, null, null, null, null, sbc_a_xix, null, null, null, null, null, null, null, and_xix, null, null, null, null, null, xor_ixh, null, xor_xix, null, null, null, null, null, null, null, or_xix, null, null, null, null, null, null, null, cp_xix, null, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, dd_cb, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, pop_ix, no_op, ex_xsp_ix, no_op, push_ix, no_op, no_op, no_op, jp_ix, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, null, null, null, null, null, no_op, no_op, null };

        static opcode_fn[] opcode_ed = { nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, in_b_c, out_c_b, sbc_hl_bc, ld_xword_bc, neg, retn, im_0, ld_i_a, in_c_c, null, adc_hl_bc, ld_bc_xword, neg, reti, im_0, ld_r_a, null, out_c_d, sbc_hl_de, ld_xword_de, neg, retn, im_1, ld_a_i, in_e_c, out_c_e, adc_hl_de, ld_de_xword, neg, reti, im_2, ld_a_r, null, out_c_h, null, null, neg, retn, im_0, rrd, in_l_c, out_c_l, adc_hl_hl, null, neg, reti, im_0, rld, null, null, null, ld_xword_sp, neg, retn, im_1, null, in_a_c, out_c_a, null, ld_sp_xword, neg, reti, im_2, null, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, ldi, cpi, null, outi, null, null, null, null, ldd, null, null, null, null, null, null, null, ldir, cpir, null, otir, null, null, null, null, lddr, cpdr, null, null, null, null, null, null, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, nop, null, null, null, null, null, null, null, null };

        static opcode_fn[] opcode_fd = { no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, add_iy_bc, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, add_iy_de, no_op, no_op, no_op, no_op, no_op, no_op, null, ld_iy_word, ld_xword_iy, inc_iy, null, dec_iyh, ld_iyh_byte, null, null, null, ld_iy_xword, dec_iy, null, dec_iyl, ld_iyl_byte, null, no_op, no_op, no_op, no_op, inc_xiy, dec_xiy, ld_xiy_byte, no_op, no_op, add_iy_sp, no_op, no_op, no_op, no_op, no_op, no_op, null, null, null, null, null, null, ld_b_xiy, null, null, null, null, null, null, null, ld_c_xiy, null, null, null, null, null, ld_d_iyh, null, ld_d_xiy, null, null, null, no_op, null, null, ld_e_iyl, ld_e_xiy, null, null, null, null, null, null, null, ld_h_xiy, ld_iyh_a, null, null, null, null, null, null, ld_l_xiy, ld_iyl_a, ld_xiy_b, ld_xiy_c, ld_xiy_d, ld_xiy_e, ld_xiy_h, ld_xiy_l, no_op, ld_xiy_a, null, null, null, null, ld_a_iyh, ld_a_iyl, ld_a_xiy, null, null, null, null, null, add_a_iyh, null, add_a_xiy, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, sub_xiy, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, and_xiy, null, null, null, null, null, null, null, xor_xiy, null, null, null, null, null, null, null, or_xiy, null, null, null, null, null, null, null, cp_xiy, null, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, fd_cb, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, pop_iy, no_op, ex_xsp_iy, no_op, push_iy, no_op, no_op, no_op, jp_iy, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, no_op, ld_sp_iy, no_op, no_op, no_op, no_op, no_op, no_op };


	static opcode_fn cb = new opcode_fn() //TODO checked since it changed!
        {
            public void handler()
	    {
	 	
	 	R.R += 1;
		int opcode = M_RDOP(R.PC);
		R.PC = (R.PC + 1) & 0xFFFF;
	 	Z80_ICount -= cycles_cb[opcode];
  		if (opcode_cb[opcode] != null)
  			opcode_cb[opcode].handler();
  		else
  		{
	  		System.out.println("CB PC = " + Integer.toHexString(R.PC) + " OPCODE = " + Integer.toHexString(opcode));
	  	}
	    }
        };

	static opcode_fn dd = new opcode_fn()//TODO checked since it changed!
        {
            public void handler()
	    {
	 	
	 	R.R += 1;
		int opcode = M_RDOP(R.PC);
		R.PC = (R.PC + 1) & 0xFFFF;
	 	Z80_ICount -= cycles_xx[opcode];
  		if (opcode_dd[opcode] != null)
  			opcode_dd[opcode].handler();
  		else
  		{
	  		System.out.println("DD PC = " + Integer.toHexString(R.PC) + " OPCODE = " + Integer.toHexString(opcode));
	  	}
	    }
        };
	static opcode_fn ed = new opcode_fn()//TODO checked since it changed!
        {
            public void handler()
	    {
	 	
	 	R.R += 1;
		int opcode = M_RDOP(R.PC);
		R.PC = (R.PC + 1) & 0xFFFF;
	 	Z80_ICount -= cycles_ed[opcode];
  		if (opcode_ed[opcode] != null)
  			opcode_ed[opcode].handler();
  		else
  		{
	  		System.out.println("ED PC = " + Integer.toHexString(R.PC) + " OPCODE = " + Integer.toHexString(opcode));	
	  	}
	    }
        };
	static opcode_fn fd = new opcode_fn() //TODO checked since it changed!
        {
            public void handler()
	    {
	 	
	 	Z80.R.R += 1;
		int opcode = M_RDOP(R.PC);
		R.PC = (R.PC + 1) & 0xFFFF;
	 	Z80_ICount -= cycles_xx[opcode];
  		if (opcode_fd[opcode] != null)
  			opcode_fd[opcode].handler();
  		else
  		{
	  		System.out.println("FD PC = " + Integer.toHexString(R.PC) + " OPCODE = " + Integer.toHexString(opcode));
	        }
	    }
        };
	static opcode_fn ei = new opcode_fn()//TODO checked since it changed!
        {
            public void handler()
	    {
		int opcode;
		/* If interrupts were disabled, execute one more instruction and check the */
		/* IRQ line. If not, simply set interrupt flip/flop 2                      */
		if (R.IFF1 == 0)
		{
			R.IFF1 = R.IFF2 = 1;
			R.R += 1;
			opcode=M_RDOP(R.PC);
			R.PC = (R.PC + 1) & 0xFFFF;
			Z80_ICount -= cycles_main[opcode];
			//System.out.println(Integer.toHexString(opcode));
			opcode_main[opcode].handler();
			Interrupt();
		}
		else
			R.IFF2 = 1;
	   }
        };

        static opcode_fn[] opcode_main =
        {
            nop, ld_bc_word, ld_xbc_a, inc_bc, inc_b, dec_b, ld_b_byte, rlca,
            ex_af_af, add_hl_bc, ld_a_xbc, dec_bc, inc_c, dec_c, ld_c_byte, rrca,
            djnz, ld_de_word, ld_xde_a, inc_de, inc_d, dec_d, ld_d_byte, rla, jr,
            add_hl_de, ld_a_xde, dec_de, inc_e, dec_e, ld_e_byte, rra, jr_nz,
            ld_hl_word, ld_xword_hl, inc_hl, inc_h, dec_h, ld_h_byte, daa, jr_z,
            add_hl_hl, ld_hl_xword, dec_hl, inc_l, dec_l, ld_l_byte, cpl, jr_nc,
            ld_sp_word, ld_xbyte_a, inc_sp, inc_xhl, dec_xhl, ld_xhl_byte, scf,
            jr_c, add_hl_sp, ld_a_xbyte, dec_sp, inc_a, dec_a, ld_a_byte, ccf,
            ld_b_b, ld_b_c, ld_b_d, ld_b_e, ld_b_h, ld_b_l, ld_b_xhl, ld_b_a,
            ld_c_b, ld_c_c, ld_c_d, ld_c_e, ld_c_h, ld_c_l, ld_c_xhl, ld_c_a,
            ld_d_b, ld_d_c, ld_d_d, ld_d_e, ld_d_h, ld_d_l, ld_d_xhl, ld_d_a,
            ld_e_b, ld_e_c, ld_e_d, ld_e_e, ld_e_h, ld_e_l, ld_e_xhl, ld_e_a,
            ld_h_b, ld_h_c, ld_h_d, ld_h_e, ld_h_h, ld_h_l, ld_h_xhl, ld_h_a,
            ld_l_b, ld_l_c, ld_l_d, ld_l_e, ld_l_h, ld_l_l, ld_l_xhl, ld_l_a,
            ld_xhl_b, ld_xhl_c, ld_xhl_d, ld_xhl_e, ld_xhl_h, ld_xhl_l, halt,
            ld_xhl_a, ld_a_b, ld_a_c, ld_a_d, ld_a_e, ld_a_h, ld_a_l, ld_a_xhl,
            ld_a_a, add_a_b, add_a_c, add_a_d, add_a_e, add_a_h, add_a_l, add_a_xhl,
            add_a_a, adc_a_b, adc_a_c, adc_a_d, adc_a_e, adc_a_h, adc_a_l, adc_a_xhl,
            adc_a_a, sub_b, sub_c, sub_d, sub_e, sub_h, sub_l, sub_xhl, sub_a, sbc_a_b,
            sbc_a_c, sbc_a_d, sbc_a_e, sbc_a_h, sbc_a_l, sbc_a_xhl, sbc_a_a, and_b,
            and_c, and_d, and_e, and_h, and_l, and_xhl, and_a, xor_b, xor_c, xor_d,
            xor_e, xor_h, xor_l, xor_xhl, xor_a, or_b, or_c, or_d, or_e, or_h, or_l,
            or_xhl, or_a, cp_b, cp_c, cp_d, cp_e, cp_h, cp_l, cp_xhl, cp_a, ret_nz,
            pop_bc, jp_nz, jp, call_nz, push_bc, add_a_byte, rst_00, ret_z, ret, jp_z,
            cb, call_z, call, adc_a_byte, rst_08, ret_nc, pop_de, jp_nc, out_byte_a,
            call_nc, push_de, sub_byte, rst_10, ret_c, exx, jp_c, in_a_byte, call_c, dd,
            sbc_a_byte, rst_18, ret_po, pop_hl, jp_po, ex_xsp_hl, null, push_hl, and_byte,
            rst_20, ret_pe, jp_hl, jp_pe, ex_de_hl, null, ed, xor_byte, rst_28, ret_p,
            pop_af, jp_p, di, call_p, push_af, or_byte, rst_30, ret_m, ld_sp_hl, jp_m,
            ei, call_m, fd, cp_byte, rst_38 };

        static int InitTables_virgin = 1;
        static boolean addresses[] = new boolean[0x10000];
        static boolean debug = false;
	static int oldPC;

        public static final int M_RDMEM(int A) { return Z80_RDMEM(A); }
	public static final void M_WRMEM(int A, int V) { Z80_WRMEM(A, V); }
	public static final char M_RDOP(int A) { return Z80_RDOP(A); }
	public static final char M_RDOP_ARG(int A) { return Z80_RDOP_ARG(A); }
	public static final char M_RDSTACK(int A) { return Z80_RDSTACK(A); }
	public static final void M_WRSTACK(int A, int V) { Z80_WRSTACK(A, V); }

       public static final void M_SKIP_CALL()
       {
           R.PC = (R.PC + 2) & 0xFFFF;
       }
	public static final void M_SKIP_JP()
        {
            R.PC = (R.PC + 2) & 0xFFFF;
        }
	public static final void M_SKIP_JR()
        {
            R.PC = (R.PC + 1) & 0xFFFF;
        }
	public static final void M_SKIP_RET()////TODO check if i need to do something
        {
          //  System.out.println("Z80 M_SKIP_RET CALLED! (dunno if i must support that)");
        }



        static final boolean M_C() { return (R.F & C_FLAG) != 0; }
	static final boolean M_NC() { return (!M_C()); }
	static final boolean M_Z() { return (R.F & Z_FLAG) != 0; }
	static final boolean M_NZ() { return (!M_Z()); }
	static final boolean M_M() { return (R.F & S_FLAG) != 0; }
	static final boolean M_P() { return (!M_M()); }
	static final boolean M_PE() { return (R.F & V_FLAG) != 0; }
	static final boolean M_PO() { return (!M_PE()); }

	/* Get next opcode and increment program counter */
	static char M_RDMEM_OPCODE() //TODO check it i modified for 0.27
	{
		int retval = M_RDOP_ARG(R.PC);
	 	R.PC = (R.PC + 1) & 0xFFFF;
	 	return (char)retval;
	}
	static char M_RDMEM_WORD(int A)
	{
		int i=M_RDMEM(A);
		i+=M_RDMEM((A+1)&0xFFFF)<<8;
		return (char) i;
	}
	static void M_WRMEM_WORD(int A, int V)
	{
		M_WRMEM(A, V & 255);
		M_WRMEM((A+1)&0xFFFF,V>>8);
	}
	static char M_RDMEM_OPCODE_WORD()
	{
		int i=M_RDMEM_OPCODE();
		i+=M_RDMEM_OPCODE()<<8;
		return (char) i;
	}
	static final int M_XIX()
        {
            return (R.IX.W + (byte) M_RDMEM_OPCODE()) & 0xFFFF;
        }
	static final int M_XIY()
        {
            return (R.IY.W + (byte) M_RDMEM_OPCODE()) & 0xFFFF;
        }
	static final int M_RD_XHL()
        {
            return M_RDMEM(R.HL.W);
        }
	static final int M_RD_XIX()
	{
		int i = M_XIX();
	 	return M_RDMEM(i);
	}
	static final int M_RD_XIY()
	{
		int i = M_XIY();
	 	return M_RDMEM(i);
	}
	static final void M_WR_XIX(int a)
	{
		int i = M_XIX();
	 	M_WRMEM(i, a);
	}
	static final void M_WR_XIY(int a)
	{
		int i = M_XIY();
	 	M_WRMEM(i, a);
	}
	/****************************************************************************/
	/* Reset registers to their initial values                                  */
	/****************************************************************************/
	public static void Z80_Reset()
	{
		R.AF = R.PC = R.SP = 0;
		R.A = R.F = 0;
		R.BC.SetW(0); R.DE.SetW(0); R.HL.SetW(0); R.IX.SetW(0); R.IY.SetW(0);
		R.AF2 = R.BC2 = R.DE2 = R.HL2 = 0;
		R.IFF1 = R.IFF2 = R.HALT = R.IM = R.I = R.R = R.R2 = 0;
		R.SP = 0xF000;
 		R.R = rand();
		Z80_Clear_Pending_Interrupts();
	}
	

	/****************************************************************************/
	/* Initialise the various lookup tables used by the emulation code          */
	/****************************************************************************/
	static void InitTables()//TODO checked it since i modified it for 0.27
	{
		int zs;
		int i, p;
		if (InitTables_virgin == 0) return;
		InitTables_virgin = 0;
		for (i = 0; i < 256; i++)
		{
			zs = 0;
			if (i == 0)
				zs |= Z_FLAG;
			if ((i&0x80) != 0)
				zs |= S_FLAG;
			p = 0;
			if ((i&1) != 0) p++;
			if ((i&2) != 0) p++;
			if ((i&4) != 0) p++;
			if ((i&8) != 0) p++;
			if ((i&16) != 0) p++;
			if ((i&32) != 0) p++;
			if ((i&64) != 0) p++;
			if ((i&128) != 0) p++;
			PTable[i] = ((p&1) != 0) ? 0:V_FLAG;
			ZSTable[i] = zs;
			ZSPTable[i] = zs | PTable[i];
		}
		for (i = 0; i < 256; i++)
		{
			ZSTable[i + 256] = ZSTable[i] | C_FLAG;
			ZSPTable[i + 256] = ZSPTable[i] | C_FLAG;
			PTable[i + 256] = PTable[i] | C_FLAG;
		}
	}

	/****************************************************************************/
	/* Issue an interrupt if necessary                                          */
	/****************************************************************************/
        static void Interrupt ()//rewrote for v0.29
        {
        /* Z80_IRQ = j;	* -NS- sticky interrupts *	* NS 970901 */

        /* if (j==Z80_IGNORE_INT) return; */ /* NS 970904*/
        /* if (j==Z80_NMI_INT || R.IFF1) */

                if (R.pending_irq == Z80_IGNORE_INT && R.pending_nmi == 0) return;	/* NS 970904 */
                if (R.pending_nmi != 0 || R.IFF1!=0)	/* NS 970904 */
         {
        /*Z80_IRQ = Z80_IGNORE_INT;*/	/* NS 970904 */
          /* Clear interrupt flip-flop 1 */
          R.IFF1=0;
          /* Check if processor was halted */
          if (R.HALT!=0)
          {
           R.PC = (R.PC + 1) & 0xFFFF;// ++R.PC.W.l;
           R.HALT=0;
          }
        /*  if (j==Z80_NMI_INT)*/
                if (R.pending_nmi != 0)	/* NS 970904 */
          {
                R.pending_nmi = 0;	/* NS 970904 */
           M_PUSH (R.PC);
           R.PC=0x0066;
          }
          else
          {
                  int j;

                  j = R.pending_irq;	/* NS 970904 */
                R.pending_irq = Z80_IGNORE_INT;	/* NS 970904 */

           /* Interrupt mode 2. Call [R.I:databyte] */
           if (R.IM==2)
           {
            M_PUSH (R.PC);
            R.PC=M_RDMEM_WORD((j&255)|(R.I<<8));
           }
           else
            /* Interrupt mode 1. RST 38h */
            if (R.IM==1)
            {
             Z80_ICount-=cycles_main[0xFF];
             opcode_main[0xFF].handler();
            }
            else
            /* Interrupt mode 0. We check for CALL and JP instructions, if neither  */
            /* of these were found we assume a 1 byte opcode was placed on the      */
            /* databus                                                              */
            {
             switch (j&0xFF0000)
             {
              case 0xCD0000:	/* bugfix NS 970904 */
               M_PUSH(R.PC);
              case 0xC30000:	/* bugfix NS 970904 */
               R.PC=j&0xFFFF;
               break;
              default:
               j&=255;
               Z80_ICount-=cycles_main[j];
               opcode_main[j].handler();
               break;
             }
            }
          }
         }
        }
	
	public static void Debug(String toto)
	{
		System.out.println(toto + " PC " + Integer.toHexString(oldPC) + " SP " + Integer.toHexString(R.SP) + " AF " + Integer.toHexString((R.A << 8) | R.F) + " BC " + Integer.toHexString(R.BC.W) + " DE " + Integer.toHexString(R.DE.W) + " HL " + Integer.toHexString(R.HL.W) + " IX " + Integer.toHexString(R.IX.W));

        }

	/****************************************************************************/
	/* Set all registers to given values                                        */
	/****************************************************************************/
	public static void Z80_SetRegs(Z80_Regs Regs)
	{
		R.AF = Regs.AF; R.PC = Regs.PC; R.SP = Regs.SP;
		R.A = Regs.A; R.F = Regs.F;
		R.BC.SetW(Regs.BC.W); R.DE.SetW(Regs.DE.W); R.HL.SetW(Regs.HL.W); R.IX.SetW(Regs.IX.W); R.IY.SetW(Regs.IY.W);
		R.AF2 = Regs.AF2; R.BC2 = Regs.BC2; R.DE2 = Regs.DE2; R.HL2 = Regs.HL2;
		R.IFF1 = Regs.IFF1; R.IFF2 = Regs.IFF2; R.HALT = Regs.HALT; R.IM = Regs.IM; R.I = Regs.I; R.R = Regs.R; R.R2 = Regs.R2;
                R.pending_irq=Regs.pending_irq;
                R.pending_nmi=Regs.pending_nmi;
	}	
	/****************************************************************************/
	/* Get all registers in given buffer                                        */
	/****************************************************************************/
	public static void Z80_GetRegs(Z80_Regs Regs)
	{
		Regs.AF = R.AF; Regs.PC = R.PC; Regs.SP = R.SP;
		Regs.A = R.A; Regs.F = R.F;
		Regs.BC.SetW(R.BC.W); Regs.DE.SetW(R.DE.W); Regs.HL.SetW(R.HL.W); Regs.IX.SetW(R.IX.W); Regs.IY.SetW(R.IY.W);
		Regs.AF2 = R.AF2; Regs.BC2 = R.BC2; Regs.DE2 = R.DE2; Regs.HL2 = R.HL2;
		Regs.IFF1 = R.IFF1; Regs.IFF2 = R.IFF2; Regs.HALT = R.HALT; Regs.IM = R.IM; Regs.I = R.I; Regs.R = R.R; Regs.R2 = R.R2;
                Regs.pending_irq=R.pending_irq;
                Regs.pending_nmi=R.pending_nmi;
	}


	/****************************************************************************/
	/* Return program counter                                                   */
	/****************************************************************************/
	public static int Z80_GetPC()
	{
		return R.PC;
	}

	public static void Z80_Cause_Interrupt(int type)	/* NS 970904 */
        {
                if (type == Z80_NMI_INT)
                        R.pending_nmi = 1;
                else if (type != Z80_IGNORE_INT)
                        R.pending_irq = type;
        }
        public static void Z80_Clear_Pending_Interrupts()	/* NS 970904 */
        {
                R.pending_irq = Z80_IGNORE_INT;
                R.pending_nmi = 0;
        }

	/****************************************************************************/
	/* Execute IPeriod T-States. Return 0 if emulation should be stopped        */
	/****************************************************************************/
    public static void Z80_RegisterDump ()
    {
     int i;
     printf
     (
       "AF:%04X HL:%04X DE:%04X BC:%04X PC:%04X SP:%04X IX:%04X IY:%04X\n",
       R.AF,R.HL.W,R.DE.W,R.BC.W,R.PC,R.SP,R.IX.W,R.IY.W
     );
    /* printf ("STACK: ");
     for (i=0;i<10;++i) 
         printf ("%04X ",M_RDMEM_WORD((R.SP+i*2)&0xFFFF));*/

    }
	public static int Z80_Execute(int cycles)//TODO changed for v0.27 - recheck if neccesary
	{
 		//char opcode;
 		Z80_Running=1;
 		InitTables();
                Z80_ICount=cycles;	/* NS 970904 */
 		do
 		{
                       if (R.pending_nmi != 0 || R.pending_irq != Z80_IGNORE_INT) Interrupt();	/* NS 970901 */
  			//++R.R;
                        R.R += 1;
			oldPC=R.PC;
  			//opcode=M_RDOP(R.PC);
                         int i = M_RDOP(R.PC);
			R.PC = (R.PC + 1) & 0xFFFF;
			Z80_ICount -= cycles_main[i];
  			if (opcode_main[i] != null)
                        {
  				opcode_main[i].handler();
                              //Z80_RegisterDump();
                        }
  			else
  			{
	  			System.out.println("MAIN PC = " + Integer.toHexString(oldPC) + " OPCODE = " + Integer.toHexString(i));
	  			
	  		}
 		}
 		while (Z80_ICount > 0);
 		return cycles - Z80_ICount;	/* NS 970904 */
	}
	
	/****************************************************************************/
	/* Interpret Z80 code                                                       */
	/****************************************************************************/
	/*public static char Z80()
	{
		while (Z80_Execute() != 0);
		return (char) (R.PC & 0xFFFF);
	}*/


  public static abstract interface opcode_fn
  {
    public abstract void handler();
  }
}
