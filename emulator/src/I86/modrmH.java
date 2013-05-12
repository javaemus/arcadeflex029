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

import static I86.eaH.*;
import static I86.I86.*;
import static I86.I86H.*;
/**
 *
 * @author George
 */
public class modrmH {
  public static _Mod_RM Mod_RM = new _Mod_RM();

  public static final int RegWord(int ModRM) { return regs.w[Mod_RM.reg.w[ModRM]]; } 
  public static final int RegByte(int ModRM) { return regs.b[Mod_RM.reg.b[ModRM]]; } 
  public static final void SetRegWord(int ModRM, int val) { regs.SetW(Mod_RM.reg.w[ModRM], val); } 
  public static final void SetRegByte(int ModRM, int val) { regs.SetB(Mod_RM.reg.b[ModRM], val); }

  public static final int GetRMWord(int ModRM) {
    return ModRM >= 0xc0 ? regs.w[Mod_RM.RM.w[ModRM]] : ReadWord(GetEA[ModRM].handler());
  }

  public static final void PutbackRMWord(int ModRM, int val) {
    if (ModRM >= 0xc0) regs.SetW(Mod_RM.RM.w[ModRM], val); else
      WriteWord(EA, val); 
  }

  public static final int GetnextRMWord() {
    return ReadWord(EA + 2);
  }

  public static final void PutRMWord(int ModRM, int val) {
    if (ModRM >= 0xc0) {
      regs.SetW(Mod_RM.RM.w[ModRM], val);
    }
    else {
      GetEA[ModRM].handler();
      WriteWord(EA, val);
    }
  }

  public static final void PutImmRMWord(int ModRM)
  {
    if (ModRM >= 0xc0) {
      regs.SetW(Mod_RM.RM.w[ModRM], FETCHWORD());
    }
    else {
      GetEA[ModRM].handler();
      int i = FETCHWORD();
      WriteWord(EA, i);
    }
  }

  public static final int GetRMByte(int ModRM) {
    return ModRM >= 0xc0 ? regs.b[Mod_RM.RM.b[ModRM]] : ReadByte(GetEA[ModRM].handler());
  }

  public static final void PutRMByte(int ModRM, int val) {
    if (ModRM >= 0xc0)
      regs.SetB(Mod_RM.RM.b[ModRM], val);
    else
      WriteByte(GetEA[ModRM].handler(), val);
  }

  public static final void PutImmRMByte(int ModRM)
  {
    if (ModRM >= 0xc0) {
      regs.SetB(Mod_RM.RM.b[ModRM], FETCH());
    }
    else {
      GetEA[ModRM].handler();
      WriteByte(EA, FETCH());
    }
  }

  public static final void PutbackRMByte(int ModRM, int val)
  {
    if (ModRM >= 0xc0) regs.SetB(Mod_RM.RM.b[ModRM], val); 
    else
     WriteByte(EA, val);
  }

  static class _Mod_RM
  {
    public _reg reg = new _reg();

    public _RM RM = new _RM();

    static class _RM
    {
      public int[] w = new int[256];
      public int[] b = new int[256];
    }

    static class _reg
    {
      public int[] w = new int[256];
      public int[] b = new int[256];
    }
  }
}
