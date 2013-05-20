/*
 * ported to v0.29
 * using automatic conversion tool v0.04
 * converted at : 29-08-2011 17:40:58
 *
 *
 *
 */ 
package machine;

import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.mame.*;
import static Z80.Z80H.*;
import static Z80.Z80.*;
import static mame.cpuintrf.*;

public class _1943
{
	
	
	
	/* this is a protection check. The game crashes (thru a jump to 0x8000) */
	/* if a read from this address doesn't return the value it expects. */
	public static ReadHandlerPtr c1943_protection_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		Z80_Regs regs=new Z80_Regs();
	
	
		Z80_GetRegs(regs);
		if (errorlog != null) fprintf(errorlog,"protection read, PC: %04x Result:%02x\n",cpu_getpc(),regs.BC.H);
		return regs.BC.H; /*regs.BC.B.h;*/
	} };
}
