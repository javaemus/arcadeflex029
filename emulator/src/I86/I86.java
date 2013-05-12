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


package I86;

import static arcadeflex.libc.*;
import static I86.I86H.*;

public class I86 {
  static _regs regs = new _regs();
  public static int ip;
  static int[] sregs = new int[4];
  static int[] base = new int[4];
  static int TF;
  static int IF;
  static int DF;
  static int AuxVal;
  static int OverVal;
  static int SignVal;
  static int ZeroVal;
  static int CarryVal;
  static int ParityVal;
  static int int86_pending;
  static char[] Memory;
  static int cycle_count;
  static int cycles_per_run;
  static int[] parity_table = new int[256];

  static instr.InstructionPtr i_add_br8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegByte(i);
      int k = modrmH.GetRMByte(i);
      int m = k;

      I86.cycle_count -= 3;
      k += j;

      I86H.SetCFB(k);
      I86H.SetOFB_Add(k, j, m);
      I86H.SetAF(k, j, m);
      I86H.SetSZPF_Byte(k);

      modrmH.PutbackRMByte(i, k & 0xFF);
    }
  };

  static instr.InstructionPtr i_add_wr16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegWord(i);
      int k = modrmH.GetRMWord(i);

      I86.cycle_count -= 3;
      int m = k + j;

      I86H.SetCFW(m);
      I86H.SetOFW_Add(m, j, k);
      I86H.SetAF(m, j, k);
      I86H.SetSZPF_Word(m);

      modrmH.PutbackRMWord(i, m & 0xFFFF);
    }
  };

  static instr.InstructionPtr i_add_r8b = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegByte(i);
      int k = modrmH.GetRMByte(i);
      int m = j;

      I86.cycle_count -= 3;
      j += k;

      I86H.SetCFB(j);
      I86H.SetOFB_Add(j, k, m);
      I86H.SetAF(j, k, m);
      I86H.SetSZPF_Byte(j);

      modrmH.SetRegByte(i, j & 0xFF);
    }
  };

  static instr.InstructionPtr i_add_r16w = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegWord(i);
      int k = modrmH.GetRMWord(i);

      I86.cycle_count -= 3;
      int m = j + k;

      I86H.SetCFW(m);
      I86H.SetOFW_Add(m, k, j);
      I86H.SetAF(m, k, j);
      I86H.SetSZPF_Word(m);

      modrmH.SetRegWord(i, m & 0xFFFF);
    }
  };

  static instr.InstructionPtr i_add_ald8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = I86.regs.b[0];
      int k = j;

      I86.cycle_count -= 4;
      k += i;

      I86H.SetCFB(k);
      I86H.SetOFB_Add(k, i, j);
      I86H.SetAF(k, i, j);
      I86H.SetSZPF_Byte(k);

      I86.regs.SetB(0, k & 0xFF);
    }
  };

  static instr.InstructionPtr i_add_axd16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int j = I86.regs.w[0];
      int k = j;

      int i = I86H.FETCH();
      i += (I86H.FETCH() << 8);

      I86.cycle_count -= 4;
      k += i;

      I86H.SetCFW(k);
      I86H.SetOFW_Add(k, j, i);
      I86H.SetAF(k, j, i);
      I86H.SetSZPF_Word(k);

      I86.regs.SetW(0, k & 0xFFFF);
    }
  };

  static instr.InstructionPtr i_push_es = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 3;
      I86H.PUSH(I86.sregs[0]);
    }
  };

  static instr.InstructionPtr i_pop_es = new instr.InstructionPtr()
  {
    public void handler() {
      I86.sregs[0] = I86H.POP();
      I86.base[0] = I86H.SegBase(0);
      I86.cycle_count -= 2;
    }
  };

  static instr.InstructionPtr i_or_br8 = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86H.FETCH();
      int j = modrmH.RegByte(i);
      int k = modrmH.GetRMByte(i);
      I86.cycle_count -= 3;
      k |= j;
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Byte(k);
      modrmH.PutbackRMByte(i, k & 0xFF);
    }
  };

  static instr.InstructionPtr i_or_r8b = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86H.FETCH();
      int j = modrmH.GetRMByte(i);
      int k = modrmH.RegByte(i);
      I86.cycle_count -= 3;
      k |= j;
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Byte(k);
      modrmH.SetRegByte(i, k & 0xFF);
    }
  };

  static instr.InstructionPtr i_or_r16w = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86H.FETCH();
      int j = modrmH.GetRMWord(i);
      int k = modrmH.RegWord(i);
      I86.cycle_count -= 3;
      k |= j;
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Word(k);
      modrmH.SetRegWord(i, k);
    }
  };

  static instr.InstructionPtr i_or_ald8 = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86.regs.b[0];
      I86.cycle_count -= 4;
      i |= I86H.FETCH();
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Byte(i);
      I86.regs.SetB(0, i & 0xFF);
    }
  };

  static instr.InstructionPtr i_or_axd16 = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86H.FETCH();
      int j = I86.regs.w[0];
      i += (I86H.FETCH() << 8);
      I86.cycle_count -= 4;
      j |= i;
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Word(j);
      I86.regs.SetW(0, j);
    }
  };

  static instr.InstructionPtr i_push_cs = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 3;
      I86H.PUSH(I86.sregs[1]);
    }
  };

  static instr.InstructionPtr i_adc_wr16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegWord(i) + I86H.CF();
      int k = modrmH.GetRMWord(i);

      I86.cycle_count -= 3;
      int m = k + j;

      I86H.SetCFW(m);
      I86H.SetOFW_Add(m, j, k);
      I86H.SetAF(m, j, k);
      I86H.SetSZPF_Word(m);

      modrmH.PutbackRMWord(i, m & 0xFFFF);
    }
  };

  static instr.InstructionPtr i_adc_r8b = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegByte(i);
      int k = modrmH.GetRMByte(i) + I86H.CF();
      int m = j;

      I86.cycle_count -= 3;
      j += k;

      I86H.SetCFB(j);
      I86H.SetOFB_Add(j, k, m);
      I86H.SetAF(j, k, m);
      I86H.SetSZPF_Byte(j);

      modrmH.SetRegByte(i, j & 0xFF);
    }
  };

  static instr.InstructionPtr i_adc_r16w = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegWord(i);
      int k = modrmH.GetRMWord(i) + I86H.CF();

      I86.cycle_count -= 3;
      int m = j + k;

      I86H.SetCFW(m);
      I86H.SetOFW_Add(m, k, j);
      I86H.SetAF(m, k, j);
      I86H.SetSZPF_Word(m);

      modrmH.SetRegWord(i, m & 0xFFFF);
    }
  };

  static instr.InstructionPtr i_push_ds = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 3;
      I86H.PUSH(I86.sregs[3]);
    }
  };

  static instr.InstructionPtr i_pop_ds = new instr.InstructionPtr()
  {
    public void handler() {
      I86.sregs[3] = I86H.POP();
      I86.base[3] = I86H.SegBase(3);
      I86.cycle_count -= 2;
    }
  };

  static instr.InstructionPtr i_and_r8b = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86H.FETCH();
      int j = modrmH.GetRMByte(i);
      int k = modrmH.RegByte(i);
      I86.cycle_count -= 3;
      k &= j;
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Byte(k);
      modrmH.SetRegByte(i, k & 0xFF);
    }
  };

  static instr.InstructionPtr i_and_r16w = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86H.FETCH();
      int j = modrmH.GetRMWord(i);
      int k = modrmH.RegWord(i);
      I86.cycle_count -= 3;
      k &= j;
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Word(k);
      modrmH.SetRegWord(i, k);
    }
  };

  static instr.InstructionPtr i_and_ald8 = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86.regs.b[0];
      I86.cycle_count -= 4;
      i &= I86H.FETCH();
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Byte(i);
      I86.regs.SetB(0, i & 0xFF);
    }
  };

  static instr.InstructionPtr i_and_axd16 = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86H.FETCH();
      int j = I86.regs.w[0];
      i += (I86H.FETCH() << 8);
      I86.cycle_count -= 4;
      j &= i;
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Word(j);
      I86.regs.SetW(0, j);
    }
  };

  static instr.InstructionPtr i_daa = new instr.InstructionPtr()
  {
    public void handler() {
      if ((I86H.AF() != 0) || ((I86.regs.b[0] & 0xF) > 9))
      {
        I86.regs.AddB(0, 6);
        I86.AuxVal = 1;
      }
      else {
        I86.AuxVal = 0;
      }
      if ((I86H.CF() != 0) || (I86.regs.b[0] > 159))
      {
        I86.regs.AddB(0, 96);
        I86.CarryVal = 1;
      }
      else {
        I86.CarryVal = 0;
      }
      I86H.SetSZPF_Byte(I86.regs.b[0]);
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_sub_br8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegByte(i);
      int k = modrmH.GetRMByte(i);
      int m = k;

      I86.cycle_count -= 3;
      k -= j;

      I86H.SetCFB(k);
      I86H.SetOFB_Sub(k, j, m);
      I86H.SetAF(k, j, m);
      I86H.SetSZPF_Byte(k);

      modrmH.PutbackRMByte(i, k & 0xFF);
    }
  };

  static instr.InstructionPtr i_sub_wr16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegWord(i);
      int k = modrmH.GetRMWord(i);

      I86.cycle_count -= 3;
      int m = k - j;

      I86H.SetCFW(m);
      I86H.SetOFW_Sub(m, j, k);
      I86H.SetAF(m, j, k);
      I86H.SetSZPF_Word(m);

      modrmH.PutbackRMWord(i, m & 0xFFFF);
    }
  };

  static instr.InstructionPtr i_sub_r8b = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegByte(i);
      int k = modrmH.GetRMByte(i);
      int m = j;

      I86.cycle_count -= 3;
      j -= k;

      I86H.SetCFB(j);
      I86H.SetOFB_Sub(j, k, m);
      I86H.SetAF(j, k, m);
      I86H.SetSZPF_Byte(j);

      modrmH.SetRegByte(i, j & 0xFF);
    }
  };

  static instr.InstructionPtr i_sub_r16w = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegWord(i);
      int k = modrmH.GetRMWord(i);

      I86.cycle_count -= 3;
      int m = j - k;

      I86H.SetCFW(m);
      I86H.SetOFW_Sub(m, k, j);
      I86H.SetAF(m, k, j);
      I86H.SetSZPF_Word(m);

      modrmH.SetRegWord(i, m & 0xFFFF);
    }
  };

  static instr.InstructionPtr i_sub_ald8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = I86.regs.b[0];
      int k = j;

      I86.cycle_count -= 4;
      k -= i;

      I86H.SetCFB(k);
      I86H.SetOFB_Sub(k, i, j);
      I86H.SetAF(k, i, j);
      I86H.SetSZPF_Byte(k);

      I86.regs.SetB(0, k & 0xFF);
    }
  };

  static instr.InstructionPtr i_sub_axd16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int j = I86.regs.w[0];
      int k = j;

      int i = I86H.FETCH();
      i += (I86H.FETCH() << 8);

      I86.cycle_count -= 4;
      k -= i;

      I86H.SetCFW(k);
      I86H.SetOFW_Sub(k, i, j);
      I86H.SetAF(k, j, i);
      I86H.SetSZPF_Word(k);

      I86.regs.SetW(0, k & 0xFFFF);
    }
  };

  static instr.InstructionPtr i_das = new instr.InstructionPtr()
  {
    public void handler() {
      if ((I86H.AF() != 0) || ((I86.regs.b[0] & 0xF) > 9))
      {
        I86.regs.AddB(0, -6);
        I86.AuxVal = 1;
      }
      else {
        I86.AuxVal = 0;
      }
      if ((I86H.CF() != 0) || (I86.regs.b[0] > 159))
      {
        I86.regs.AddB(0, -96);
        I86.CarryVal = 1;
      }
      else {
        I86.CarryVal = 0;
      }
      I86H.SetSZPF_Byte(I86.regs.b[0]);
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_xor_r8b = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86H.FETCH();
      int j = modrmH.GetRMByte(i);
      int k = modrmH.RegByte(i);
      I86.cycle_count -= 3;
      k ^= j;
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Byte(k);
      modrmH.SetRegByte(i, k & 0xFF);
    }
  };

  static instr.InstructionPtr i_xor_r16w = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86H.FETCH();
      int j = modrmH.GetRMWord(i);
      int k = modrmH.RegWord(i);
      I86.cycle_count -= 3;
      k ^= j;
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Word(k);
      modrmH.SetRegWord(i, k);
    }
  };

  static instr.InstructionPtr i_ss = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.base[3] = I86.base[2];

      I86.cycle_count -= 2;
      instr.instruction[I86H.FETCH()].handler();

      I86.base[3] = I86H.SegBase(3);
    }
  };

  static instr.InstructionPtr i_aaa = new instr.InstructionPtr()
  {
    public void handler() {
      if ((I86H.AF() != 0) || ((I86.regs.b[0] & 0xF) > 9))
      {
        I86.regs.AddB(0, 6);
        I86.regs.AddB(1, 1);
        I86.AuxVal = 1;
        I86.CarryVal = 1;
      }
      else
      {
        I86.AuxVal = 0;
        I86.CarryVal = 0;
      }
      I86.regs.SetB(0, I86.regs.b[0] & 0xF);
      I86.cycle_count -= 8;
    }
  };

  static instr.InstructionPtr i_cmp_br8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegByte(i);
      int k = modrmH.GetRMByte(i);
      int m = k;

      I86.cycle_count -= 3;
      k -= j;

      I86H.SetCFB(k);
      I86H.SetOFB_Sub(k, j, m);
      I86H.SetAF(k, j, m);
      I86H.SetSZPF_Byte(k);
    }
  };

  static instr.InstructionPtr i_cmp_wr16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegWord(i);
      int k = modrmH.GetRMWord(i);

      I86.cycle_count -= 3;
      int m = k - j;

      I86H.SetCFW(m);
      I86H.SetOFW_Sub(m, j, k);
      I86H.SetAF(m, j, k);
      I86H.SetSZPF_Word(m);
    }
  };

  static instr.InstructionPtr i_cmp_r8b = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegByte(i);
      int k = modrmH.GetRMByte(i);
      int m = j;

      I86.cycle_count -= 3;
      j -= k;

      I86H.SetCFB(j);
      I86H.SetOFB_Sub(j, k, m);
      I86H.SetAF(j, k, m);
      I86H.SetSZPF_Byte(j);
    }
  };

  static instr.InstructionPtr i_cmp_r16w = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegWord(i);
      int k = modrmH.GetRMWord(i);

      I86.cycle_count -= 3;
      int m = j - k;

      I86H.SetCFW(m);
      I86H.SetOFW_Sub(m, k, j);
      I86H.SetAF(m, k, j);
      I86H.SetSZPF_Word(m);
    }
  };

  static instr.InstructionPtr i_cmp_ald8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = I86.regs.b[0];
      int k = j;

      I86.cycle_count -= 4;
      k -= i;

      I86H.SetCFB(k);
      I86H.SetOFB_Sub(k, i, j);
      I86H.SetAF(k, i, j);
      I86H.SetSZPF_Byte(k);
    }
  };

  static instr.InstructionPtr i_cmp_axd16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int j = I86.regs.w[0];
      int k = j;

      int i = I86H.FETCH();
      i += (I86H.FETCH() << 8);

      I86.cycle_count -= 4;
      k -= i;

      I86H.SetCFW(k);
      I86H.SetOFW_Sub(k, i, j);
      I86H.SetAF(k, j, i);
      I86H.SetSZPF_Word(k);
    }
  };

  static instr.InstructionPtr i_inc_ax = new instr.InstructionPtr()
  {
    public void handler() {
      I86.IncWordReg(0);
    }
  };

  static instr.InstructionPtr i_inc_cx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.IncWordReg(1);
    }
  };

  static instr.InstructionPtr i_inc_dx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.IncWordReg(2);
    }
  };

  static instr.InstructionPtr i_inc_bx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.IncWordReg(3);
    }
  };

  static instr.InstructionPtr i_inc_bp = new instr.InstructionPtr()
  {
    public void handler() {
      I86.IncWordReg(5);
    }
  };

  static instr.InstructionPtr i_inc_si = new instr.InstructionPtr()
  {
    public void handler() {
      I86.IncWordReg(6);
    }
  };

  static instr.InstructionPtr i_inc_di = new instr.InstructionPtr()
  {
    public void handler() {
      I86.IncWordReg(7);
    }
  };

  static instr.InstructionPtr i_dec_ax = new instr.InstructionPtr()
  {
    public void handler() {
      I86.DecWordReg(0);
    }
  };

  static instr.InstructionPtr i_dec_cx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.DecWordReg(1);
    }
  };

  static instr.InstructionPtr i_dec_dx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.DecWordReg(2);
    }
  };

  static instr.InstructionPtr i_dec_bx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.DecWordReg(3);
    }
  };

  static instr.InstructionPtr i_dec_bp = new instr.InstructionPtr()
  {
    public void handler() {
      I86.DecWordReg(5);
    }
  };

  static instr.InstructionPtr i_dec_si = new instr.InstructionPtr()
  {
    public void handler() {
      I86.DecWordReg(6);
    }
  };

  static instr.InstructionPtr i_dec_di = new instr.InstructionPtr()
  {
    public void handler() {
      I86.DecWordReg(7);
    }
  };

  static instr.InstructionPtr i_push_ax = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 4;
      I86H.PUSH(I86.regs.w[0]);
    }
  };

  static instr.InstructionPtr i_push_cx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 4;
      I86H.PUSH(I86.regs.w[1]);
    }
  };

  static instr.InstructionPtr i_push_dx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 4;
      I86H.PUSH(I86.regs.w[2]);
    }
  };

  static instr.InstructionPtr i_push_bx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 4;
      I86H.PUSH(I86.regs.w[3]);
    }
  };

  static instr.InstructionPtr i_push_bp = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 4;
      I86H.PUSH(I86.regs.w[5]);
    }
  };

  static instr.InstructionPtr i_push_si = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 4;
      I86H.PUSH(I86.regs.w[6]);
    }
  };

  static instr.InstructionPtr i_push_di = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 4;
      I86H.PUSH(I86.regs.w[7]);
    }
  };

  static instr.InstructionPtr i_pop_ax = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 2;
      I86.regs.SetW(0, I86H.POP());
    }
  };

  static instr.InstructionPtr i_pop_cx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 2;
      I86.regs.SetW(1, I86H.POP());
    }
  };

  static instr.InstructionPtr i_pop_dx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 2;
      I86.regs.SetW(2, I86H.POP());
    }
  };

  static instr.InstructionPtr i_pop_bx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 2;
      I86.regs.SetW(3, I86H.POP());
    }
  };

  static instr.InstructionPtr i_pop_bp = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 2;
      I86.regs.SetW(5, I86H.POP());
    }
  };

  static instr.InstructionPtr i_pop_si = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 2;
      I86.regs.SetW(6, I86H.POP());
    }
  };

  static instr.InstructionPtr i_pop_di = new instr.InstructionPtr()
  {
    public void handler() {
      I86.cycle_count -= 2;
      I86.regs.SetW(7, I86H.POP());
    }
  };

  static instr.InstructionPtr i_jo = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if (I86H.OF() != 0)
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jno = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if (I86H.OF() == 0)
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jb = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if (I86H.CF() != 0)
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jnb = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if (I86H.CF() == 0)
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jz = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if (I86H.ZF() != 0)
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jnz = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if (I86H.ZF() == 0)
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jbe = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if ((I86H.CF() != 0) || (I86H.ZF() != 0))
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jnbe = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if ((I86H.CF() == 0) && (I86H.ZF() == 0))
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_js = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if (I86H.SF() != 0)
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jns = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if (I86H.SF() == 0)
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jp = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if (I86H.PF() != 0)
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jnp = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if (I86H.PF() == 0)
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jl = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if ((I86H.SF() != I86H.OF()) && (I86H.ZF() == 0))
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jnl = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if ((I86H.ZF() != 0) || (I86H.SF() == I86H.OF()))
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jle = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if ((I86H.ZF() != 0) || (I86H.SF() != I86H.OF()))
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_jnle = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      if ((I86H.SF() == I86H.OF()) && (I86H.ZF() == 0))
      {
        I86.ip = I86.ip + i & 0xFFFF;
        I86.cycle_count -= 16; } else {
        I86.cycle_count -= 4;
      }
    }
  };

  static instr.InstructionPtr i_80pre = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86H.FETCH();
      int j = modrmH.GetRMByte(i);
      int k = I86H.FETCH();
      int m;
      switch (i & 0x38)
      {
      case 0:
        I86.cycle_count -= 4;
        m = k + j;

        I86H.SetCFB(m);
        I86H.SetOFB_Add(m, k, j);
        I86H.SetAF(m, k, j);
        I86H.SetSZPF_Byte(m);

        modrmH.PutbackRMByte(i, m & 0xFF);
        break;
      case 8:
        I86.cycle_count -= 4;
        j |= k;

        I86.CarryVal = I86.OverVal = I86.AuxVal = 0;

        I86H.SetSZPF_Byte(j);

        modrmH.PutbackRMByte(i, j & 0xFF);
        break;
      case 16:
        I86.cycle_count -= 4;
        k += I86H.CF();
        m = k + j;

        I86H.SetCFB(m);
        I86H.SetOFB_Add(m, k, j);
        I86H.SetAF(m, k, j);
        I86H.SetSZPF_Byte(m);

        modrmH.PutbackRMByte(i, m & 0xFF);
        break;
      case 24:
        I86.cycle_count -= 4;
        k += I86H.CF();
        m = j;
        j -= k;

        I86H.SetCFB(j);
        I86H.SetOFB_Sub(j, k, m);
        I86H.SetAF(j, k, m);
        I86H.SetSZPF_Byte(j);

        modrmH.PutbackRMByte(i, j & 0xFF);
        break;
      case 32:
        I86.cycle_count -= 4;
        j &= k;

        I86.CarryVal = I86.OverVal = I86.AuxVal = 0;

        I86H.SetSZPF_Byte(j);

        modrmH.PutbackRMByte(i, j & 0xFF);
        break;
      case 40:
        I86.cycle_count -= 4;
        m = j;
        j -= k;

        I86H.SetCFB(j);
        I86H.SetOFB_Sub(j, k, m);
        I86H.SetAF(j, k, m);
        I86H.SetSZPF_Byte(j);

        modrmH.PutbackRMByte(i, j & 0xFF);
        break;
      case 48:
        I86.cycle_count -= 4;
        j ^= k;

        I86.CarryVal = I86.OverVal = I86.AuxVal = 0;

        I86H.SetSZPF_Byte(j);

        modrmH.PutbackRMByte(i, j & 0xFF);
        break;
      case 56:
        I86.cycle_count -= 4;
        m = j;
        j -= k;

        I86H.SetCFB(j);
        I86H.SetOFB_Sub(j, k, m);
        I86H.SetAF(j, k, m);
        I86H.SetSZPF_Byte(j);
      }
    }
  };

  static instr.InstructionPtr i_81pre = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.GetRMWord(i);
      int k = I86H.FETCH();

      k += (I86H.FETCH() << 8);
      int m;
      switch (i & 0x38)
      {
      case 0:
        I86.cycle_count -= 2;
        m = k + j;

        I86H.SetCFW(m);
        I86H.SetOFW_Add(m, k, j);
        I86H.SetAF(m, k, j);
        I86H.SetSZPF_Word(m);

        modrmH.PutbackRMWord(i, m & 0xFFFF);
        break;
      case 8:
        I86.cycle_count -= 2;
        j |= k;

        I86.CarryVal = I86.OverVal = I86.AuxVal = 0;

        I86H.SetSZPF_Word(j);

        modrmH.PutbackRMWord(i, j);
        break;
      case 16:
        I86.cycle_count -= 2;
        k += I86H.CF();
        m = k + j;

        I86H.SetCFW(m);
        I86H.SetOFW_Add(m, k, j);
        I86H.SetAF(m, k, j);
        I86H.SetSZPF_Word(m);

        modrmH.PutbackRMWord(i, m & 0xFFFF);
        break;
      case 24:
        I86.cycle_count -= 2;
        k += I86H.CF();
        m = j;
        j -= k;

        I86H.SetCFW(j);
        I86H.SetOFW_Sub(j, k, m);
        I86H.SetAF(j, k, m);
        I86H.SetSZPF_Word(j);

        modrmH.PutbackRMWord(i, j & 0xFFFF);
        break;
      case 32:
        I86.cycle_count -= 2;
        j &= k;

        I86.CarryVal = I86.OverVal = I86.AuxVal = 0;

        I86H.SetSZPF_Word(j);

        modrmH.PutbackRMWord(i, j);
        break;
      case 40:
        I86.cycle_count -= 2;
        m = j;
        j -= k;

        I86H.SetCFW(j);
        I86H.SetOFW_Sub(j, k, m);
        I86H.SetAF(j, k, m);
        I86H.SetSZPF_Word(j);

        modrmH.PutbackRMWord(i, j & 0xFFFF);
        break;
      case 48:
        I86.cycle_count -= 2;
        j ^= k;

        I86.CarryVal = I86.OverVal = I86.AuxVal = 0;

        I86H.SetSZPF_Word(j);

        modrmH.PutbackRMWord(i, j);
        break;
      case 56:
        I86.cycle_count -= 2;
        m = j;
        j -= k;

        I86H.SetCFW(j);
        I86H.SetOFW_Sub(j, k, m);
        I86H.SetAF(j, k, m);
        I86H.SetSZPF_Word(j);
      }
    }
  };

  static instr.InstructionPtr i_83pre = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.GetRMWord(i);
      int k = (byte)I86H.FETCH() & 0xFFFF;
      int m;
      switch (i & 0x38)
      {
      case 0:
        I86.cycle_count -= 2;
        m = k + j;

        I86H.SetCFW(m);
        I86H.SetOFW_Add(m, k, j);
        I86H.SetAF(m, k, j);
        I86H.SetSZPF_Word(m);

        modrmH.PutbackRMWord(i, m & 0xFFFF);
        break;
      case 8:
        I86.cycle_count -= 2;
        j |= k;

        I86.CarryVal = I86.OverVal = I86.AuxVal = 0;

        I86H.SetSZPF_Word(j);

        modrmH.PutbackRMWord(i, j);
        break;
      case 16:
        I86.cycle_count -= 2;
        k += I86H.CF();
        m = k + j;

        I86H.SetCFW(m);
        I86H.SetOFW_Add(m, k, j);
        I86H.SetAF(m, k, j);
        I86H.SetSZPF_Word(m);

        modrmH.PutbackRMWord(i, m & 0xFFFF);
        break;
      case 24:
        I86.cycle_count -= 2;
        k += I86H.CF();
        m = j;
        j -= k;

        I86H.SetCFW(j);
        I86H.SetOFW_Sub(j, k, m);
        I86H.SetAF(j, k, m);
        I86H.SetSZPF_Word(j);

        modrmH.PutbackRMWord(i, j & 0xFFFF);
        break;
      case 32:
        I86.cycle_count -= 2;
        j &= k;

        I86.CarryVal = I86.OverVal = I86.AuxVal = 0;

        I86H.SetSZPF_Word(j);

        modrmH.PutbackRMWord(i, j);
        break;
      case 40:
        I86.cycle_count -= 2;
        m = j;
        j -= k;

        I86H.SetCFW(j);
        I86H.SetOFW_Sub(j, k, m);
        I86H.SetAF(j, k, m);
        I86H.SetSZPF_Word(j);

        modrmH.PutbackRMWord(i, j & 0xFFFF);
        break;
      case 48:
        I86.cycle_count -= 2;
        j ^= k;

        I86.CarryVal = I86.OverVal = I86.AuxVal = 0;

        I86H.SetSZPF_Word(j);

        modrmH.PutbackRMWord(i, j);
        break;
      case 56:
        I86.cycle_count -= 2;
        m = j;
        j -= k;

        I86H.SetCFW(j);
        I86H.SetOFW_Sub(j, k, m);
        I86H.SetAF(j, k, m);
        I86H.SetSZPF_Word(j);
      }
    }
  };

  static instr.InstructionPtr i_test_br8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegByte(i);
      int k = modrmH.GetRMByte(i);
      I86.cycle_count -= 3;
      k &= j;
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Byte(k);
    }
  };

  static instr.InstructionPtr i_xchg_br8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegByte(i);
      int k = modrmH.GetRMByte(i);
      modrmH.SetRegByte(i, k);
      I86.cycle_count -= 4;
      modrmH.PutbackRMByte(i, j);
    }
  };

  static instr.InstructionPtr i_xchg_wr16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegWord(i);
      int k = modrmH.GetRMWord(i);
      modrmH.SetRegWord(i, k);
      I86.cycle_count -= 4;
      modrmH.PutbackRMWord(i, j);
    }
  };

  static instr.InstructionPtr i_mov_br8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegByte(i);
      I86.cycle_count -= 2;
      modrmH.PutRMByte(i, j);
    }
  };

  static instr.InstructionPtr i_mov_wr16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.RegWord(i);
      I86.cycle_count -= 2;
      modrmH.PutRMWord(i, j);
    }
  };

  static instr.InstructionPtr i_mov_r8b = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.GetRMByte(i);
      I86.cycle_count -= 2;
      modrmH.SetRegByte(i, j);
    }
  };

  static instr.InstructionPtr i_mov_r16w = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.GetRMWord(i);
      I86.cycle_count -= 2;
      modrmH.SetRegWord(i, j);
    }
  };

  static instr.InstructionPtr i_mov_wsreg = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      I86.cycle_count -= 2;
      modrmH.PutRMWord(i, I86.sregs[((i & 0x38) >> 3)]);
    }
  };

  static instr.InstructionPtr i_lea = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      I86.cycle_count -= 2;
      modrmH.SetRegWord(i, eaH.GetEA[i].handler());
    }
  };

  static int multiple = 0;
  static instr.InstructionPtr i_mov_sregw = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.GetRMWord(i);

      I86.cycle_count -= 2;
      switch (i & 0x38)
      {
      case 0:
        I86.sregs[0] = j;
        I86.base[0] = I86H.SegBase(0);
        break;
      case 24:
        I86.sregs[3] = j;
        I86.base[3] = I86H.SegBase(3);
        break;
      case 16:
        I86.sregs[2] = j;
        I86.base[2] = I86H.SegBase(2);
        int k = I86H.FETCH();
        if (instr.instruction[k] == null)
        {
          System.err.println("OPCODE " + Integer.toHexString(k));
          //System.exit(0);
        } else {
          instr.instruction[k].handler();
        }break;
      case 8:
      }
    }
  };

  static instr.InstructionPtr i_popw = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();

      int j = I86H.POP();
      I86.cycle_count -= 4;
      modrmH.PutRMWord(i, j);
    }
  };

  static instr.InstructionPtr i_nop = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.cycle_count -= 3;
    }
  };

  static instr.InstructionPtr i_xchg_axdx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.XchgAXReg(2);
    }
  };

  static instr.InstructionPtr i_xchg_axbx = new instr.InstructionPtr()
  {
    public void handler() {
      I86.XchgAXReg(3);
    }
  };

  static instr.InstructionPtr i_cbw = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.cycle_count -= 2;
      I86.regs.SetB(1, (I86.regs.b[0] & 0x80) != 0 ? 255 : 0);
    }
  };

  static instr.InstructionPtr i_call_far = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      i += (I86H.FETCH() << 8);

      int j = I86H.FETCH();
      j += (I86H.FETCH() << 8);

      I86H.PUSH(I86.sregs[1]);
      I86H.PUSH(I86.ip);

      I86.ip = i & 0xFFFF;
      I86.sregs[1] = (j & 0xFFFF);
      I86.base[1] = I86H.SegBase(1);
      I86.cycle_count -= 14;
    }
  };

  static instr.InstructionPtr i_pushf = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.cycle_count -= 3;
      I86H.PUSH(I86H.CompressFlags() | 0xF000);
    }
  };

  static instr.InstructionPtr i_popf = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.POP();
      I86.cycle_count -= 2;
      I86H.ExpandFlags(i);

      if (I86.TF != 0) I86.trap();
    }
  };

  static instr.InstructionPtr i_mov_aldisp = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      i += (I86H.FETCH() << 8);

      I86.cycle_count -= 4;
      I86.regs.SetB(0, I86H.GetMemB(I86.base[3], i));
    }
  };

  static instr.InstructionPtr i_mov_axdisp = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      i += (I86H.FETCH() << 8);

      I86.cycle_count -= 4;
      I86.regs.SetB(0, I86H.GetMemB(I86.base[3], i));
      I86.regs.SetB(1, I86H.GetMemB(I86.base[3], i + 1));
    }
  };

  static instr.InstructionPtr i_mov_dispal = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      i += (I86H.FETCH() << 8);

      I86.cycle_count -= 3;
      I86H.PutMemB(I86.base[3], i, I86.regs.b[0]);
    }
  };

  static instr.InstructionPtr i_mov_dispax = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      i += (I86H.FETCH() << 8);

      I86.cycle_count -= 3;
      I86H.PutMemB(I86.base[3], i, I86.regs.b[0]);
      I86H.PutMemB(I86.base[3], i + 1, I86.regs.b[1]);
    }
  };

  static instr.InstructionPtr i_movsb = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86.regs.w[7];
      int j = I86.regs.w[6];

      int k = I86H.GetMemB(I86.base[3], j);

      I86H.PutMemB(I86.base[0], i, k);

      i += -2 * I86.DF + 1;
      j += -2 * I86.DF + 1;

      I86.regs.SetW(7, i & 0xFFFF);
      I86.regs.SetW(6, j & 0xFFFF);
      I86.cycle_count -= 5;
    }
  };

  static instr.InstructionPtr i_movsw = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86.regs.w[7];
      int j = I86.regs.w[6];

      int k = I86H.GetMemW(I86.base[3], j);

      I86H.PutMemW(I86.base[0], i, k);

      i += -4 * I86.DF + 2;
      j += -4 * I86.DF + 2;

      I86.regs.SetW(7, i & 0xFFFF);
      I86.regs.SetW(6, j & 0xFFFF);
      I86.cycle_count -= 5;
    }
  };

  static instr.InstructionPtr i_cmpsb = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86.regs.w[7];
      int j = I86.regs.w[6];
      int k = I86H.GetMemB(I86.base[0], i);
      int m = I86H.GetMemB(I86.base[3], j);
      int n = m;

      m -= k;

      I86H.SetCFB(m);
      I86H.SetOFB_Sub(m, k, n);
      I86H.SetAF(m, k, n);
      I86H.SetSZPF_Byte(m);

      i += -2 * I86.DF + 1;
      j += -2 * I86.DF + 1;

      I86.regs.SetW(7, i & 0xFFFF);
      I86.regs.SetW(6, j & 0xFFFF);
      I86.cycle_count -= 10;
    }
  };

  static instr.InstructionPtr i_cmpsw = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86.regs.w[7];
      int j = I86.regs.w[6];
      int k = I86H.GetMemW(I86.base[0], i);
      int m = I86H.GetMemW(I86.base[3], j);
      int n = m;

      m -= k;

      I86H.SetCFW(m);
      I86H.SetOFW_Sub(m, k, n);
      I86H.SetAF(m, k, n);
      I86H.SetSZPF_Word(m);

      i += -4 * I86.DF + 2;
      j += -4 * I86.DF + 2;

      I86.regs.SetW(7, i & 0xFFFF);
      I86.regs.SetW(6, j & 0xFFFF);
      I86.cycle_count -= 10;
    }
  };

  static instr.InstructionPtr i_test_ald8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86.regs.b[0];
      int j = I86H.FETCH();

      I86.cycle_count -= 4;
      i &= j;
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Byte(i);
    }
  };

  static instr.InstructionPtr i_test_axd16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86.regs.w[0];

      int j = I86H.FETCH();
      j += (I86H.FETCH() << 8);

      I86.cycle_count -= 4;
      i &= j;
      I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
      I86H.SetSZPF_Word(i);
    }
  };

  static instr.InstructionPtr i_stosb = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86.regs.w[7];

      I86H.PutMemB(I86.base[0], i, I86.regs.b[0]);
      i += -2 * I86.DF + 1;
      I86.regs.SetW(7, i & 0xFFFF);
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_stosw = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86.regs.w[7];

      I86H.PutMemB(I86.base[0], i, I86.regs.b[0]);
      I86H.PutMemB(I86.base[0], i + 1, I86.regs.b[1]);
      i += -4 * I86.DF + 2;
      I86.regs.SetW(7, i & 0xFFFF);
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_lodsb = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86.regs.w[6];

      I86.regs.SetB(0, I86H.GetMemB(I86.base[3], i));
      i += -2 * I86.DF + 1;
      I86.regs.SetW(6, i & 0xFFFF);
      I86.cycle_count -= 6;
    }
  };

  static instr.InstructionPtr i_lodsw = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86.regs.w[6];
      int j = I86H.GetMemW(I86.base[3], i);

      i += -4 * I86.DF + 2;
      I86.regs.SetW(6, i & 0xFFFF);
      I86.regs.SetW(0, j);
      I86.cycle_count -= 6;
    }
  };

  static instr.InstructionPtr i_scasb = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86.regs.w[7];
      int j = I86H.GetMemB(I86.base[0], i);
      int k = I86.regs.b[0];
      int m = k;

      k -= j;

      I86H.SetCFB(k);
      I86H.SetOFB_Sub(k, j, m);
      I86H.SetAF(k, j, m);
      I86H.SetSZPF_Byte(k);

      i += -2 * I86.DF + 1;

      I86.regs.SetW(7, i & 0xFFFF);
      I86.cycle_count -= 9;
    }
  };

  static instr.InstructionPtr i_scasw = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86.regs.w[7];
      int j = I86H.GetMemW(I86.base[0], i);
      int k = I86.regs.w[0];
      int m = k;

      k -= j;

      I86H.SetCFW(k);
      I86H.SetOFW_Sub(k, j, m);
      I86H.SetAF(k, j, m);
      I86H.SetSZPF_Word(k);

      i += -4 * I86.DF + 2;

      I86.regs.SetW(7, i & 0xFFFF);
      I86.cycle_count -= 9;
    }
  };

  static instr.InstructionPtr i_mov_ald8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(0, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_cld8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(2, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_dld8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(4, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_bld8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(6, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_ahd8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(1, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_chd8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(3, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_dhd8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(5, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_bhd8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(7, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_axd16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(0, I86H.FETCH());
      I86.regs.SetB(1, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_cxd16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(2, I86H.FETCH());
      I86.regs.SetB(3, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_dxd16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(4, I86H.FETCH());
      I86.regs.SetB(5, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_bxd16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(6, I86H.FETCH());
      I86.regs.SetB(7, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_spd16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(8, I86H.FETCH());
      I86.regs.SetB(9, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_bpd16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(10, I86H.FETCH());
      I86.regs.SetB(11, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_sid16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(12, I86H.FETCH());
      I86.regs.SetB(13, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_mov_did16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.regs.SetB(14, I86H.FETCH());
      I86.regs.SetB(15, I86H.FETCH());
      I86.cycle_count -= 4;
    }
  };

  static instr.InstructionPtr i_ret = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.ip = I86H.POP();
      I86.cycle_count -= 10;
    }
  };

  static instr.InstructionPtr i_mov_bd8 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      I86.cycle_count -= 4;
      modrmH.PutImmRMByte(i);
    }
  };

  static instr.InstructionPtr i_mov_wd16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      I86.cycle_count -= 4;
      modrmH.PutImmRMWord(i);
    }
  };

  static instr.InstructionPtr i_iret = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.cycle_count -= 12;
      I86.ip = I86H.POP();
      I86.sregs[1] = I86H.POP();
      I86.base[1] = I86H.SegBase(1);
      I86.i_popf.handler();
    }
  };

  static instr.InstructionPtr i_d0pre = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.GetRMByte(i);
      int k = j;

      I86.cycle_count -= 2;
      switch (i & 0x38)
      {
      case 0:
        I86.CarryVal = j & 0x80;
        modrmH.PutbackRMByte(i, (j << 1) + I86H.CF() & 0xFF);
        I86.OverVal = BOOL(BOOL(j & 0x40) != I86H.CF());
        break;
      case 8:
        I86.CarryVal = j & 0x1;
        modrmH.PutbackRMByte(i, (j >> 1) + (I86H.CF() << 7) & 0xFF);
        I86.OverVal = BOOL(BOOL(j & 0x80) != I86H.CF());
        break;
      case 16:
        I86.OverVal = (j ^ j << 1) & 0x80;
        modrmH.PutbackRMByte(i, (j << 1) + I86H.CF() & 0xFF);
        I86.CarryVal = j & 0x80;
        break;
      case 24:
        modrmH.PutbackRMByte(i, (j >> 1) + (I86H.CF() << 7) & 0xFF);
        I86.OverVal = BOOL(BOOL(j & 0x80) != I86H.CF());
        I86.CarryVal = j & 0x1;
        break;
      case 32:
      case 48:
        j <<= 1;

        I86H.SetCFB(j);
        I86H.SetOFB_Add(j, k, k);
        I86.AuxVal = 1;
        I86H.SetSZPF_Byte(j);

        modrmH.PutbackRMByte(i, j & 0xFF);
        break;
      case 40:
        I86.CarryVal = j & 0x1;
        I86.OverVal = j & 0x80;

        k = j >> 1;

        I86H.SetSZPF_Byte(k);
        I86.AuxVal = 1;
        modrmH.PutbackRMByte(i, k & 0xFF);
        break;
      case 56:
        I86.CarryVal = j & 0x1;
        I86.OverVal = 0;

        k = j >> 1 | j & 0x80;

        I86H.SetSZPF_Byte(k);
        I86.AuxVal = 1;
        modrmH.PutbackRMByte(i, k & 0xFF);
      }
    }
  };

  static instr.InstructionPtr i_d1pre = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.GetRMWord(i);
      int k = j;

      I86.cycle_count -= 2;
      switch (i & 0x38)
      {
      case 0:
        k = (j << 1) + I86H.CF();
        I86H.SetCFW(k);
        I86.OverVal = BOOL(BOOL(j & 0x4000) != I86H.CF());
        modrmH.PutbackRMWord(i, k & 0xFFFF);
        break;
      case 8:
        I86.CarryVal = j & 0x1;
        k = (j >> 1) + (I86H.CF() << 15);
        I86.OverVal = BOOL(BOOL(j & 0x8000) != I86H.CF());
        modrmH.PutbackRMWord(i, k & 0xFFFF);
        break;
      case 16:
        k = (j << 1) + I86H.CF();
        I86H.SetCFW(k);
        I86.OverVal = (j ^ j << 1) & 0x8000;
        modrmH.PutbackRMWord(i, k & 0xFFFF);
        break;
      case 24:
        k = (j >> 1) + (I86H.CF() << 15);
        I86.OverVal = BOOL(BOOL(j & 0x8000) != I86H.CF());
        I86.CarryVal = j & 0x1;
        modrmH.PutbackRMWord(i, k & 0xFFFF);
        break;
      case 32:
      case 48:
        j <<= 1;

        I86H.SetCFW(j);
        I86H.SetOFW_Add(j, k, k);
        I86.AuxVal = 1;
        I86H.SetSZPF_Word(j);

        modrmH.PutbackRMWord(i, j & 0xFFFF);
        break;
      case 40:
        I86.CarryVal = j & 0x1;
        I86.OverVal = j & 0x8000;

        k = j >> 1;

        I86H.SetSZPF_Word(k);
        I86.AuxVal = 1;
        modrmH.PutbackRMWord(i, k & 0xFFFF);
        break;
      case 56:
        I86.CarryVal = j & 0x1;
        I86.OverVal = 0;

        k = j >> 1 | j & 0x8000;

        I86H.SetSZPF_Word(k);
        I86.AuxVal = 1;
        modrmH.PutbackRMWord(i, k & 0xFFFF);
      }
    }
  };

  static instr.InstructionPtr i_d2pre = new instr.InstructionPtr()
  {
    public void handler()
    {
      int m = I86.regs.b[2];

      if (m == 0)
      {
        I86.CarryVal = 0;
        I86.cycle_count -= 8;
        return;
      }

      if (m == 1)
      {
        I86.i_d0pre.handler();
        return;
      }

      I86.cycle_count -= 8 + 4 * m;
      int i = I86H.FETCH();
      int j = modrmH.GetRMByte(i);
      int k;
      switch (i & 0x38)
      {
      case 0:
        for (; m > 0; m--)
        {
          I86.CarryVal = j & 0x80;
          j = (j << 1) + I86H.CF();
        }
        modrmH.PutbackRMByte(i, j & 0xFF);
        break;
      case 8:
        for (; m > 0; m--)
        {
          I86.CarryVal = j & 0x1;
          j = (j >> 1) + (I86H.CF() << 7);
        }
        modrmH.PutbackRMByte(i, j & 0xFF);
        break;
      case 16:
        for (; m > 0; m--)
        {
          j = (j << 1) + I86H.CF();
          I86H.SetCFB(j);
        }
        modrmH.PutbackRMByte(i, j & 0xFF);
        break;
      case 24:
        for (; m > 0; m--)
        {
          k = (j >> 1) + (I86H.CF() << 7);
          I86.CarryVal = j & 0x1;
          j = k;
        }
        modrmH.PutbackRMByte(i, j & 0xFF);
        break;
      case 32:
      case 48:
        if (m >= 9)
        {
          I86.CarryVal = 0;
          j = 0;
        }
        else
        {
          j <<= m;
          I86H.SetCFB(j);
        }

        I86.AuxVal = 1;
        I86H.SetSZPF_Byte(j);

        modrmH.PutbackRMByte(i, j & 0xFF);
        break;
      case 40:
        if (m >= 9)
        {
          I86.CarryVal = 0;
          j = 0;
        }
        else
        {
          I86.CarryVal = j >> m - 1 & 0x1;
          j >>= m;
        }

        I86H.SetSZPF_Byte(j);
        I86.AuxVal = 1;
        modrmH.PutbackRMByte(i, j & 0xFF);
        break;
      case 56:
        k = j & 0x80;
        I86.CarryVal = (byte)j >> m - 1 & 0x1;
        for (; m > 0; m--) {
          j = j >> 1 | k;
        }
        I86H.SetSZPF_Byte(j);
        I86.AuxVal = 1;
        modrmH.PutbackRMByte(i, j & 0xFF);
      }
    }
  };

  static instr.InstructionPtr i_d3pre = new instr.InstructionPtr()
  {
    public void handler()
    {
      int m = I86.regs.b[2];

      if (m == 0)
      {
        I86.CarryVal = 0;
        I86.cycle_count -= 8;
        return;
      }

      if (m == 1)
      {
        I86.i_d1pre.handler();
        return;
      }

      I86.cycle_count -= 8 + 4 * m;
      int i = I86H.FETCH();
      int j = modrmH.GetRMWord(i);
      int k;
      switch (i & 0x38)
      {
      case 0:
        for (; m > 0; m--)
        {
          I86.CarryVal = j & 0x8000;
          j = (j << 1) + I86H.CF();
        }
        modrmH.PutbackRMWord(i, j & 0xFFFF);
        break;
      case 8:
        for (; m > 0; m--)
        {
          I86.CarryVal = j & 0x1;
          j = (j >> 1) + (I86H.CF() << 15);
        }
        modrmH.PutbackRMWord(i, j & 0xFFFF);
        break;
      case 16:
        for (; m > 0; m--)
        {
          j = (j << 1) + I86H.CF();
          I86H.SetCFW(j);
        }
        modrmH.PutbackRMWord(i, j & 0xFFFF);
        break;
      case 24:
        for (; m > 0; m--)
        {
          k = (j >> 1) + (I86H.CF() << 15);
          I86.CarryVal = j & 0x1;
          j = k;
        }
        modrmH.PutbackRMWord(i, j & 0xFFFF);
        break;
      case 32:
      case 48:
        if (m >= 17)
        {
          I86.CarryVal = 0;
          j = 0;
        }
        else
        {
          j <<= m;
          I86H.SetCFW(j);
        }

        I86.AuxVal = 1;
        I86H.SetSZPF_Word(j);

        modrmH.PutbackRMWord(i, j & 0xFFFF);
        break;
      case 40:
        if (m >= 17)
        {
          I86.CarryVal = 0;
          j = 0;
        }
        else
        {
          I86.CarryVal = j >> m - 1 & 0x1;
          j >>= m;
        }

        I86H.SetSZPF_Word(j);
        I86.AuxVal = 1;
        modrmH.PutbackRMWord(i, j & 0xFFFF);
        break;
      case 56:
        k = j & 0x8000;
        I86.CarryVal = (short)j >> m - 1 & 0x1;
        for (; m > 0; m--) {
          j = j >> 1 | k;
        }
        I86H.SetSZPF_Word(j);
        I86.AuxVal = 1;
        modrmH.PutbackRMWord(i, j & 0xFFFF);
      }
    }
  };

  static instr.InstructionPtr i_xlat = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86.regs.w[3] + I86.regs.b[0];

      I86.cycle_count -= 5;
      I86.regs.SetB(0, I86H.GetMemB(I86.base[3], i & 0xFFFF));
    }
  };

  static instr.InstructionPtr i_loopne = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = (byte)I86H.FETCH();
      int j = I86.regs.w[1] - 1;

      I86.regs.SetW(1, j & 0xFFFF);

      if ((I86H.ZF() == 0) && (j != 0))
      {
        I86.cycle_count -= 19;
        I86.ip = I86.ip + i & 0xFFFF; } else {
        I86.cycle_count -= 5;
      }
    }
  };

  static instr.InstructionPtr i_loop = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = (byte)I86H.FETCH();
      int j = I86.regs.w[1] - 1;

      I86.regs.SetW(1, j & 0xFFFF);

      if (j != 0)
      {
        I86.cycle_count -= 17;
        I86.ip = I86.ip + i & 0xFFFF; } else {
        I86.cycle_count -= 5;
      }
    }
  };

  static instr.InstructionPtr i_jcxz = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = (byte)I86H.FETCH();

      if (I86.regs.w[1] == 0)
      {
        I86.cycle_count -= 18;
        I86.ip = I86.ip + i & 0xFFFF; } else {
        I86.cycle_count -= 6;
      }
    }
  };

  static instr.InstructionPtr i_call_d16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      i += (I86H.FETCH() << 8);

      I86H.PUSH(I86.ip);
      I86.ip = I86.ip + (short)i & 0xFFFF;
      I86.cycle_count -= 12;
    }
  };

  static instr.InstructionPtr i_jmp_d16 = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      i += (I86H.FETCH() << 8);

      I86.ip = I86.ip + (short)i & 0xFFFF;
      I86.cycle_count -= 15;
    }
  };

  static instr.InstructionPtr i_jmp_far = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      i += (I86H.FETCH() << 8);

      int j = I86H.FETCH();
      j += (I86H.FETCH() << 8);

      I86.sregs[1] = j;
      I86.base[1] = I86H.SegBase(1);
      I86.ip = i;
      I86.cycle_count -= 15;
    }
  };

  static instr.InstructionPtr i_jmp_d8 = new instr.InstructionPtr()
  {
    public void handler() {
      int i = (byte)I86H.FETCH();
      I86.ip = I86.ip + i & 0xFFFF;
      I86.cycle_count -= 15;
    }
  };

  static instr.InstructionPtr i_repne = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.rep(0);
    }
  };

  static instr.InstructionPtr i_repe = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.rep(1);
    }
  };

  static instr.InstructionPtr i_f6pre = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86H.FETCH();
      int j = modrmH.GetRMByte(i);
      int k;
      int m;
      switch (i & 0x38)
      {
      case 0:
      case 8:
        I86.cycle_count -= 5;
        j &= I86H.FETCH();

        I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
        I86H.SetSZPF_Byte(j);
        break;
      case 16:
        I86.cycle_count -= 3;
        modrmH.PutbackRMByte(i, (j ^ 0xFFFFFFFF) & 0xFF);
        break;
      case 24:
        I86.cycle_count -= 3;
        k = j;
        j = -j;

        I86.CarryVal = BOOL(k > 0);

        I86H.SetAF(j, 0, k);
        I86H.SetSZPF_Byte(j);

        modrmH.PutbackRMByte(i, j & 0xFF);
        break;
      case 32:
        I86.cycle_count -= 77;

        k = I86.regs.b[0];

        I86H.SetSF((byte)k);
        I86H.SetPF(k);

        m = k * j;
        I86.regs.SetW(0, m & 0xFFFF);

        I86H.SetZF(I86.regs.w[0]);
        I86.CarryVal = I86.OverVal = BOOL(I86.regs.b[1] != 0);

        break;
      case 40:
        I86.cycle_count -= 80;

        k = I86.regs.b[0];

        I86H.SetSF((byte)k);
        I86H.SetPF(k);

        m = (byte)k * (byte)j;
        I86.regs.SetW(0, m & 0xFFFF);

        I86H.SetZF(I86.regs.w[0]);

        I86.CarryVal = I86.OverVal = BOOL((m >> 7 != 0) && (m >> 7 != -1));

        break;
      case 48:
        I86.cycle_count -= 90;

        m = I86.regs.w[0];

        if (j != 0)
        {
          if (m / j > 255)
          {
            I86.I86_interrupt(0);
            break;
          }

          I86.regs.SetB(1, m % j);
          I86.regs.SetB(0, m / j);
        }
        else
        {
          I86.I86_interrupt(0);
          break;
        }

        break;
      case 56:
        I86.cycle_count -= 106;

        m = I86.regs.w[0];

        if (j != 0)
        {
          k = m % (byte)j;

          if ((m /= (byte)j) > 0xff)
          {
            I86.I86_interrupt(0);
          }
          else
          {
            I86.regs.SetB(0, m);
            I86.regs.SetB(1, k);
          }
        }
        else
        {
          I86.I86_interrupt(0);
        }break;
      }
    }
  };

  static instr.InstructionPtr i_f7pre = new instr.InstructionPtr()
  {
    public void handler() {
      int i = I86H.FETCH();
      int j = modrmH.GetRMWord(i);
      int k;
      int m;
      switch (i & 0x38)
      {
      case 0:
      case 8:
        I86.cycle_count -= 3;
        k = I86H.FETCH();
        k += (I86H.FETCH() << 8);

        j &= k;

        I86.CarryVal = I86.OverVal = I86.AuxVal = 0;
        I86H.SetSZPF_Word(j);
        break;
      case 16:
        I86.cycle_count -= 3;
        j ^= -1;
        modrmH.PutbackRMWord(i, j & 0xFFFF);
        break;
      case 24:
        I86.cycle_count -= 3;
        k = j;
        j = -j;

        I86.CarryVal = BOOL(k > 0);

        I86H.SetAF(j, 0, k);
        I86H.SetSZPF_Word(j);

        modrmH.PutbackRMWord(i, j & 0xFFFF);
        break;
      case 32:
        I86.cycle_count -= 129;

        k = I86.regs.w[0];

        I86H.SetSF((short)k);
        I86H.SetPF(k);

        m = k * j;
        I86.regs.SetW(0, m & 0xFFFF);
        m >>= 16;
        I86.regs.SetW(2, m);

        I86H.SetZF(I86.regs.w[0] | I86.regs.w[2]);
        I86.CarryVal = I86.OverVal = BOOL(I86.regs.w[2] != 0);

        break;
      case 40:
        I86.cycle_count -= 150;

        k = I86.regs.w[0];

        I86H.SetSF((short)k);
        I86H.SetPF(k);

        m = (short)k * (short)j;
        I86.CarryVal = I86.OverVal = BOOL((m >> 15 != 0) && (m >> 15 != -1));

        I86.regs.SetW(0, m & 0xFFFF);
        m = m >> 16 & 0xFFFF;
        I86.regs.SetW(2, m);

        I86H.SetZF(I86.regs.w[0] | I86.regs.w[2]);

        break;
      case 48:
        I86.cycle_count -= 158;

        m = (I86.regs.w[2] << 16) + I86.regs.w[0];

        if (j != 0)
        {
          k = m % j;
          if (m / j > 65535)
          {
            I86.I86_interrupt(0);
            break;
          }

          I86.regs.SetW(2, k);
          m /= j;
          I86.regs.SetW(0, m);
        }
        else
        {
          I86.I86_interrupt(0);
          break;
        }

        break;
      case 56:
        I86.cycle_count -= 180;

        m = (I86.regs.w[2] << 16) + I86.regs.w[0];

        if (j != 0)
        {
          k = m % (short)j;

          if ((m /= (short)j) > 65535)
          {
            I86.I86_interrupt(0);
          }
          else
          {
            I86.regs.SetW(0, m);
            I86.regs.SetW(2, k);
          }
        }
        else
        {
          I86.I86_interrupt(0);
        }break;
      }
    }
  };

  static instr.InstructionPtr i_clc = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.cycle_count -= 2;
      I86.CarryVal = 0;
    }
  };

  static instr.InstructionPtr i_stc = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.cycle_count -= 2;
      I86.CarryVal = 1;
    }
  };

  static instr.InstructionPtr i_cli = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.cycle_count -= 2;
      I86.IF = 0;
    }
  };

  static instr.InstructionPtr i_cld = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.cycle_count -= 2;
      I86.DF = 0;
    }
  };

  static instr.InstructionPtr i_std = new instr.InstructionPtr()
  {
    public void handler()
    {
      I86.cycle_count -= 2;
      I86.DF = 1;
    }
  };

  static instr.InstructionPtr i_fepre = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j = modrmH.GetRMByte(i);

      I86.cycle_count -= 3;
      int k;
      if ((i & 0x38) == 0)
      {
        k = j + 1;
        I86H.SetOFB_Add(k, j, 1);
      }
      else
      {
        k = j - 1;
        I86H.SetOFB_Sub(k, 1, j);
      }

      I86H.SetAF(k, j, 1);
      I86H.SetSZPF_Byte(k);

      modrmH.PutbackRMByte(i, k & 0xFF);
    }
  };

  static instr.InstructionPtr i_ffpre = new instr.InstructionPtr()
  {
    public void handler()
    {
      int i = I86H.FETCH();
      int j;
      int k;
      switch (i & 0x38)
      {
      case 0:
        I86.cycle_count -= 3;
        j = modrmH.GetRMWord(i);
        k = j + 1;

        I86H.SetOFW_Add(k, j, 1);
        I86H.SetAF(k, j, 1);
        I86H.SetSZPF_Word(k);

        modrmH.PutbackRMWord(i, k & 0xFFFF);
        break;
      case 8:
        I86.cycle_count -= 3;
        j = modrmH.GetRMWord(i);
        k = j - 1;

        I86H.SetOFW_Sub(k, 1, j);
        I86H.SetAF(k, j, 1);
        I86H.SetSZPF_Word(k);

        modrmH.PutbackRMWord(i, k & 0xFFFF);
        break;
      case 16:
        I86.cycle_count -= 9;
        j = modrmH.GetRMWord(i);
        I86H.PUSH(I86.ip);
        I86.ip = j;
        break;
      case 24:
        I86.cycle_count -= 11;
        I86H.PUSH(I86.sregs[1]);
        I86H.PUSH(I86.ip);
        I86.ip = modrmH.GetRMWord(i);
        I86.sregs[1] = modrmH.GetnextRMWord();
        I86.base[1] = I86H.SegBase(1);
        break;
      case 32:
        I86.cycle_count -= 11;
        I86.ip = modrmH.GetRMWord(i);
        break;
      case 40:
        I86.cycle_count -= 4;
        I86.ip = modrmH.GetRMWord(i);
        I86.sregs[1] = modrmH.GetnextRMWord();
        I86.base[1] = I86H.SegBase(1);
        break;
      case 48:
        I86.cycle_count -= 3;
        j = modrmH.GetRMWord(i);
        I86H.PUSH(j);
      }
    }
  };

  public static void I86_Reset(char[] mem, int cycles)
  {
    int[] reg_name = { AL, CL, DL, BL, AH, CH, DH, BH };

    cycles_per_run = cycles;
    int86_pending = 0;
    for (int i = 0; i < 4; i++) sregs[i] = 0;
    sregs[1] = 0xFFFF;

    base[CS] = SegBase(CS);
    base[DS] = SegBase(DS);
    base[ES] = SegBase(ES);
    base[SS] = SegBase(SS);

    for (int i = 0; i < 8; i++) regs.SetW(i, 0);
    ip = 0;

    Memory = mem;

    for (int i = 0; i < 256; i++)
    {
      int k;
      int j = i; for (k = 0; j > 0; j >>= 1) {
        if ((j & 0x1) == 0) continue; k++;
      }
      parity_table[i] = NOT(k & 0x1);
    }

    TF = I86.IF = I86.DF = 0;
    SignVal = I86.CarryVal = I86.AuxVal = I86.OverVal = 0;
    ZeroVal = I86.ParityVal = 1;

    for (int i = 0; i < 256; i++)
    {
      modrmH.Mod_RM.reg.b[i] = reg_name[((i & 0x38) >> 3)];
      modrmH.Mod_RM.reg.w[i] = ((i & 0x38) >> 3);
    }

    for (int i = 192; i < 256; i++)
    {
      modrmH.Mod_RM.RM.w[i] = (i & 0x7);
      modrmH.Mod_RM.RM.b[i] = reg_name[(i & 0x7)];
    }
  }

  static void I86_interrupt(int int_num)
  {
    i_pushf.handler();
    int j = GetMemW(0, int_num * 4);
    int i = GetMemW(0, int_num * 4 + 2);

    PUSH(sregs[CS]);
    PUSH(ip);
    ip = j;
    sregs[1] = i;
    base[1] = I86H.SegBase(CS);

    TF = I86.IF = 0;
  }

  static void trap()
  {
    int i = I86H.FETCH();
    if (instr.instruction[i] == null)
    {
      System.err.println("TRAP OPCODE " + Integer.toHexString(i));
     // System.exit(0);
    }
    instr.instruction[i].handler();
    I86_interrupt(1);
  }

  static void external_int()
  {
    I86_interrupt(int86_pending);
    int86_pending = 0;
  }

  static final void IncWordReg(int reg)
  {
    int i = regs.w[reg];
    int j = i + 1;
    SetOFW_Add(j, i, 1);
    SetAF(j, i, 1);
    SetSZPF_Word(j);
    regs.SetW(reg, j & 0xFFFF);
    cycle_count -= 3;
  }

  static final void DecWordReg(int reg)
  {
    int i = regs.w[reg];
    int j = i - 1;
    SetOFW_Sub(j, 1, i);
    SetAF(j, i, 1);
    SetSZPF_Word(j);
    regs.SetW(reg, j & 0xFFFF);
    cycle_count -= 3;
  }

  static final void XchgAXReg(int reg)
  {
    int i = regs.w[reg];
    regs.SetW(reg, regs.w[AX]);
    regs.SetW(AX, i);
    cycle_count -= 3;
  }

  static void rep(int flagval)
  {
      /* Handles rep- and repnz- prefixes. flagval is the value of ZF for the
       loop  to continue for CMPS and SCAS instructions. */
      
    int i = FETCH();
    int j = regs.w[CX];

    switch (i)
    {
    case 38:
      int tmp149_148 = base[0]; base[3] = tmp149_148; base[2] = tmp149_148;
      cycle_count -= 2;
      rep(flagval);
      base[3] = SegBase(3);
      base[2] = SegBase(2);
      break;
    case 46:
      int tmp198_197 = base[1]; base[3] = tmp198_197; base[2] = tmp198_197;
      cycle_count -= 2;
      rep(flagval);
      base[3] = SegBase(3);
      base[2] = SegBase(2);
      break;
    case 54:
      base[3] = base[2];
      cycle_count -= 2;
      rep(flagval);
      base[3] = SegBase(3);
      break;
    case 62:
      base[2] = base[3];
      cycle_count -= 2;
      rep(flagval);
      base[2] = SegBase(2);
      break;
    case 164:
      cycle_count -= 9 - j;
      for (; j > 0; j--)
        i_movsb.handler();
      regs.SetW(1, j);
      break;
    case 165:
      cycle_count -= 9 - j;
      for (; j > 0; j--)
        i_movsw.handler();
      regs.SetW(1, j);
      break;
    case 166:
      cycle_count -= 9;
      for (ZeroVal = NOT(flagval); (I86H.ZF() == flagval) && (j > 0); j--)
        i_cmpsb.handler();
      regs.SetW(1, j);
      break;
    case 167:
      cycle_count -= 9;
      for (ZeroVal = NOT(flagval); (I86H.ZF() == flagval) && (j > 0); j--)
        i_cmpsw.handler();
      regs.SetW(1, j);
      break;
    case 170:
      cycle_count -= 9 - j;
      for (; j > 0; j--)
        i_stosb.handler();
      regs.SetW(1, j);
      break;
    case 171:
      cycle_count -= 9 - j;
      for (; j > 0; j--)
        i_stosw.handler();
      regs.SetW(1, j);
      break;
    case 172:
      cycle_count -= 9;
      for (; j > 0; j--)
        i_lodsb.handler();
      regs.SetW(1, j);
      break;
    case 173:
      cycle_count -= 9;
      for (; j > 0; j--)
        i_lodsw.handler();
      regs.SetW(1, j);
      break;
    case 174:
      cycle_count -= 9;
      for (ZeroVal = NOT(flagval); (I86H.ZF() == flagval) && (j > 0); j--)
        i_scasb.handler();
      regs.SetW(1, j);
      break;
    case 175:
      cycle_count -= 9;
      for (ZeroVal = NOT(flagval); (I86H.ZF() == flagval) && (j > 0); j--)
        i_scasw.handler();
      regs.SetW(1, j);
      break;
    default:
      instr.instruction[i].handler();
    }
  }

  public static void I86_Execute()
  {
    if (int86_pending != 0) external_int();

    cycle_count = cycles_per_run;
    while (cycle_count > 0)
    {
      int i = base[1] + ip;

      int j = I86H.FETCH();

      if (instr.instruction[j] == null)
      {
        System.err.println("OPCODE " + Integer.toHexString(j));
        //System.exit(0);
      } else {
        instr.instruction[j].handler();
      }
    }
    int86_pending = 2;
  }

  static class _regs
  {
    public int[] w = new int[8];
    public int[] b = new int[16];

    public void SetB(int index, int val)
    {
      this.b[index] = val; 
      this.w[(index >> 1)] = (this.b[((index & 0xFFFFFFFE) + 1)] << 8 | this.b[(index & 0xFFFFFFFE)]); 
    } 
    public void AddB(int index, int val) 
    { 
        this.b[index] = (this.b[index] + val & 0xFF); 
        this.w[(index >> 1)] = (this.b[((index & 0xFFFFFFFE) + 1)] << 8 | this.b[(index & 0xFFFFFFFE)]); 
    } 
    public void SetW(int index, int val) 
    { 
        this.w[index] = val; index <<= 1; 
        this.b[index] = (val & 0xFF); 
        this.b[(index + 1)] = (val >> 8);
    }
   }
}
