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


///PARTIALLY!!!!

/*
 * ported to v0.28
 * using automatic conversion tool v0.02
 * converted at : 25-08-2011 00:04:10
 *
 *
 *
 */ 
package sndhrdw;

import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static Z80.Z80.*;
import static Z80.Z80H.*;

public class wow
{
	
	public static ShStartPtr wow_sh_start = new ShStartPtr() { public int handler() 
	{
		/* Start votrax emulation (load samples) */
	
		//sh_votrax_start(0);
	
	    return 0;
	} };
	
	public static ShStopPtr wow_sh_stop = new ShStopPtr() { public void handler() 
	{
		/* Free the samples */
	
		//sh_votrax_stop();
	} };
	
	/* Writes to speech chip with an IN command */
	
	public static ReadHandlerPtr wow_speech_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		 Z80_Regs regs = new Z80_Regs();
                 int data;
	
		Z80_GetRegs(regs);
                data = regs.BC.H;//regs.BC.B.h; //TODO correct????
	
		//votrax_w(data);
	
	    return data;				/* Probably not used */
	} };
	
	/* Read from port 2 (0x12) returns speech status as 0x80 */
	
	public static ReadHandlerPtr wow_port_2_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		int Ans;
	
	    Ans = (input_port_2_r.handler(0) & 0x7F);
	
	    /*if (votrax_status_r() != 0)*/ Ans += 128;
	
	    return Ans;
	} };
	
	public static ShUpdatePtr wow_sh_update = new ShUpdatePtr() { public void handler() 
	{
	} };
}
