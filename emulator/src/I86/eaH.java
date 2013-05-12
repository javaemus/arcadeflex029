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

import static I86.I86.*;
import static I86.I86H.*;
/**
 *
 * @author George
 */
public class eaH {

  public static int EA;
  static GetEAPtr EA_000 = new GetEAPtr() { public int handler() { cycle_count -= 7; EA = base[3] + (regs.w[3] + regs.w[6]) & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_001 = new GetEAPtr() { public int handler() { cycle_count -= 8; EA = base[3] + (regs.w[3] + regs.w[7]) & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_002 = new GetEAPtr() { public int handler() { cycle_count -= 8; EA = base[2] + (regs.w[5] + regs.w[6]) & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_003 = new GetEAPtr() { public int handler() { cycle_count -= 7; EA = base[2] + (regs.w[5] + regs.w[7]) & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_004 = new GetEAPtr() { public int handler() { cycle_count -= 5; EA = base[3] + regs.w[6]; return EA; } } ;
  static GetEAPtr EA_005 = new GetEAPtr() { public int handler() { cycle_count -= 5; EA = base[3] + regs.w[7]; return EA; } } ;
  static GetEAPtr EA_006 = new GetEAPtr() { public int handler() { cycle_count -= 6; EA = FETCH(); EA += (FETCH() << 8); EA += base[3]; return EA; } } ;
  static GetEAPtr EA_007 = new GetEAPtr() { public int handler() { cycle_count -= 5; EA = base[3] + regs.w[3]; return EA; } } ;

  static GetEAPtr EA_100 = new GetEAPtr() { public int handler() { cycle_count -= 11; EA = base[3] + (regs.w[3] + regs.w[6] + (byte)FETCH()) & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_101 = new GetEAPtr() { public int handler() { cycle_count -= 12; EA = base[3] + (regs.w[3] + regs.w[7] + (byte)FETCH()) & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_102 = new GetEAPtr() { public int handler() { cycle_count -= 12; EA = base[2] + (regs.w[5] + regs.w[6] + (byte)FETCH()) & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_103 = new GetEAPtr() { public int handler() { cycle_count -= 11; EA = base[2] + (regs.w[5] + regs.w[7] + (byte)FETCH()) & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_104 = new GetEAPtr() { public int handler() { cycle_count -= 9; EA = base[3] + (regs.w[6] + (byte)FETCH()) & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_105 = new GetEAPtr() { public int handler() { cycle_count -= 9; EA = base[3] + (regs.w[7] + (byte)FETCH()) & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_106 = new GetEAPtr() { public int handler() { cycle_count -= 9; EA = base[2] + (regs.w[5] + (byte)FETCH()) & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_107 = new GetEAPtr() { public int handler() { cycle_count -= 9; EA = base[3] + (regs.w[3] + (byte)FETCH()) & 0xFFFF; return EA; } } ;

  static GetEAPtr EA_200 = new GetEAPtr() { public int handler() { cycle_count -= 11; EA = FETCH(); EA += (FETCH() << 8); EA += regs.w[3] + regs.w[6]; EA = base[3] + EA & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_201 = new GetEAPtr() { public int handler() { cycle_count -= 12; EA = FETCH(); EA += (FETCH() << 8); EA += regs.w[3] + regs.w[7]; EA = base[3] + EA & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_202 = new GetEAPtr() { public int handler() { cycle_count -= 12; EA = FETCH(); EA += (FETCH() << 8); EA += regs.w[5] + regs.w[6]; EA = base[2] + EA & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_203 = new GetEAPtr() { public int handler() { cycle_count -= 11; EA = FETCH(); EA += (FETCH() << 8); EA += regs.w[5] + regs.w[7]; EA = base[2] + EA & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_204 = new GetEAPtr() { public int handler() { cycle_count -= 9; EA = FETCH(); EA += (FETCH() << 8); EA += regs.w[6]; EA = base[3] + EA & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_205 = new GetEAPtr() { public int handler() { cycle_count -= 9; EA = FETCH(); EA += (FETCH() << 8); EA += regs.w[7]; EA = base[3] + EA & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_206 = new GetEAPtr() { public int handler() { cycle_count -= 9; EA = FETCH(); EA += (FETCH() << 8); EA += regs.w[5]; EA = base[2] + EA & 0xFFFF; return EA; } } ;
  static GetEAPtr EA_207 = new GetEAPtr() { public int handler() { cycle_count -= 9; EA = FETCH(); EA += (FETCH() << 8); EA += regs.w[3]; EA = base[3] + EA & 0xFFFF; return EA; } } ;

  public static GetEAPtr[] GetEA = 
  { 
      	EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
	EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
	EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
	EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
	EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
	EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
	EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,
	EA_000, EA_001, EA_002, EA_003, EA_004, EA_005, EA_006, EA_007,

	EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
	EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
	EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
	EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
	EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
	EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
	EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,
	EA_100, EA_101, EA_102, EA_103, EA_104, EA_105, EA_106, EA_107,

	EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
	EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
	EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
	EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
	EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
	EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
	EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207,
	EA_200, EA_201, EA_202, EA_203, EA_204, EA_205, EA_206, EA_207
  };

  public static abstract interface GetEAPtr
  {
    public abstract int handler();
  }
}
