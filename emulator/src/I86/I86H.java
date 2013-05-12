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
import static I86.I86.*;
import static mame.cpuintrf.*;

/**
 *
 * @author George
 */
public class I86H {
  static final int ES = 0;
  static final int CS = 1;
  static final int SS = 2;
  static final int DS = 3;
  static final int AX = 0;
  static final int CX = 1;
  static final int DX = 2;
  static final int BX = 3;
  static final int SP = 4;
  static final int BP = 5;
  static final int SI = 6;
  static final int DI = 7;
  static final int AL = 0;
  static final int AH = 1;
  static final int CL = 2;
  static final int CH = 3;
  static final int DL = 4;
  static final int DH = 5;
  static final int BL = 6;
  static final int BH = 7;
  static final int SPL = 8;
  static final int SPH = 9;
  static final int BPL = 10;
  static final int BPH = 11;
  static final int SIL = 12;
  static final int SIH = 13;
  static final int DIL = 14;
  static final int DIH = 15;

  static final void SetTF(int paramInt)
  {
    TF = paramInt; } 
  static final void SetIF(int paramInt) { IF = paramInt; } 
  static final void SetDF(int paramInt) { DF = paramInt; } 
  static final void SetOFW_Add(int paramInt1, int paramInt2, int paramInt3) {
    OverVal = (paramInt1 ^ paramInt2) & (paramInt1 ^ paramInt3) & 0x8000; } 
  static final void SetOFB_Add(int paramInt1, int paramInt2, int paramInt3) { OverVal = (paramInt1 ^ paramInt2) & (paramInt1 ^ paramInt3) & 0x80; } 
  static final void SetOFW_Sub(int paramInt1, int paramInt2, int paramInt3) { OverVal = (paramInt3 ^ paramInt2) & (paramInt3 ^ paramInt1) & 0x8000; } 
  static final void SetOFB_Sub(int paramInt1, int paramInt2, int paramInt3) { OverVal = (paramInt3 ^ paramInt2) & (paramInt3 ^ paramInt1) & 0x80; } 
  static final void SetCFB(int paramInt) {
    CarryVal = paramInt & 0x100; } 
  static final void SetCFW(int paramInt) { CarryVal = paramInt & 0x10000; } 
  static final void SetAF(int paramInt1, int paramInt2, int paramInt3) { AuxVal = (paramInt1 ^ paramInt2 ^ paramInt3) & 0x10; } 
  static final void SetSF(int paramInt) { SignVal = paramInt; } 
  static final void SetZF(int paramInt) { ZeroVal = paramInt; } 
  static final void SetPF(int paramInt) { ParityVal = paramInt; } 
  static final void SetSZPF_Byte(int paramInt) {
    SetSF((byte)paramInt); SetZF((byte)paramInt); SetPF(paramInt); } 
  static final void SetSZPF_Word(int paramInt) { SetSF((short)paramInt); SetZF((short)paramInt); SetPF(paramInt); } 
  static final int CF() {
    return BOOL(CarryVal != 0); } 
  static final int SF() { return BOOL(SignVal < 0); } 
  static final int ZF() { return BOOL(ZeroVal == 0); } 
  static final int PF() { return BOOL(parity_table[(ParityVal & 0xFF)]); } 
  static final int AF() { return BOOL(AuxVal != 0); } 
  static final int OF() { return BOOL(OverVal != 0); }

  static final int SegBase(int paramInt)
  {
    return sregs[paramInt] << 4 & 0xFFFF;
  }
  static final int GetMemB(int paramInt1, int paramInt2) { cycle_count -= 6; return cpu_readmem(paramInt1 + paramInt2); } 
  static final int GetMemW(int paramInt1, int paramInt2) { cycle_count -= 10; return GetMemB(paramInt1, paramInt2) + (GetMemB(paramInt1, paramInt2 + 1) << 8); } 
  static final void PutMemB(int paramInt1, int paramInt2, int paramInt3) { cycle_count -= 7; cpu_writemem(paramInt1 + paramInt2, (char)paramInt3); } 
  static final void PutMemW(int paramInt1, int paramInt2, int paramInt3) { cycle_count -= 11; PutMemB(paramInt1, paramInt2, paramInt3 & 0xFF); PutMemB(paramInt1, paramInt2 + 1, paramInt3 >> 8 & 0xFF); } 
  static final int ReadByte(int paramInt) {
    cycle_count -= 6; return cpu_readmem(paramInt) & 0xFF; } 
  static final int ReadWord(int paramInt) { cycle_count -= 10; return cpu_readmem(paramInt) + (cpu_readmem(paramInt + 1) << 8); } 
  static final void WriteByte(int paramInt1, int paramInt2) { cycle_count -= 7; cpu_writemem(paramInt1, paramInt2); } 
  static final void WriteWord(int paramInt1, int paramInt2) { cycle_count -= 11; cpu_writemem(paramInt1, paramInt2 & 0xFF); cpu_writemem(paramInt1 + 1, paramInt2 >> 8);
  }

  static final int FETCH()
  {
    int i = Memory[(base[1] + ip)] & 0xFF; ip = ip + 1 & 0xFFFF; return i; } 
  static final int FETCHWORD() { int i = Memory[(base[1] + ip)] + (Memory[(base[1] + ip + 1)] << '\b'); ip = ip + 2 & 0xFFFF; return i; } 
  static final void PUSH(int paramInt) { int i = paramInt; cycle_count -= 11; regs.w[4] = (regs.w[4] - 2 & 0xFFFF);
    Memory[(base[2] + regs.w[4])] = (char)(i & 0xFF);
    Memory[(base[2] + regs.w[4] + 1)] = (char)(i >> 8); } 
  static final int POP() { int i = regs.w[4]; cycle_count -= 10; regs.w[4] = (regs.w[4] + 2 & 0xFFFF);
    return Memory[(base[2] + i)] + (Memory[(base[2] + i + 1)] << '\b'); } 
  static final int CompressFlags() {
    return CF() | PF() << 2 | AF() << 4 | ZF() << 6 | SF() << 7 | TF << 8 | IF << 9 | DF << 10 | OF() << 11;
  }

  static final void ExpandFlags(int paramInt)
  {
    CarryVal = paramInt & 0x1;
    ParityVal = NOT(paramInt & 0x4);
    AuxVal = paramInt & 0x10;
    ZeroVal = NOT(paramInt & 0x40);
    SignVal = (paramInt & 0x80) != 0 ? -1 : 0;
    TF = BOOL((paramInt & 0x100) == 256);
    IF = BOOL((paramInt & 0x200) == 512);
    DF = BOOL((paramInt & 0x400) == 1024);
    OverVal = paramInt & 0x800;
  }
}
