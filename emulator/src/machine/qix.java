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

/*
 * ported to v0.28
 * using automatic conversion tool v0.02
 * converted at : 24-08-2011 23:12:28
 *
 *
 *
 */ 
package machine;

import static arcadeflex.libc.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static m6809.M6809H.*;
import static m6809.M6809.*;
import static mame.mame.*;

public class qix
{
	
	public static CharPtr qix_sharedram = new CharPtr();	
	static int lastcheck2;

	
	
	/* JB 970824 - don't interrupt when we're just yielding */
	public static InterruptPtr qix_data_interrupt = new InterruptPtr() { public int handler() 
	{
		if (yield_cpu != 0)
			return INT_NONE;
		else
			return INT_IRQ;
	} };
	
	public static ReadHandlerPtr qix_sharedram_r_1 = new ReadHandlerPtr() { public int handler(int offset)
	{
		int pc;
	
		pc = cpu_getpc();
		/* JB 970824 - trap all occurrences where the main cpu is waiting for the video */
		/*             cpu and yield immediately                                        */
		if(pc==0xdd54 || pc==0xcf16 || pc==0xdd5a)
		{
			yield_cpu = 1;
			saved_icount = cpu_geticount();
			cpu_seticount (0);
		}
		/* JB 970824 - waiting for an IRQ */
		else if(pc==0xdd4b && RAM[0x8520]!=0)
			cpu_seticount (0);
	
		/* JB 970825 - these loop optimizations work, but incorrectly affect the gameplay */
		/*               (even with speed throttling, play is too fast)                   */
		/*#if 0
		if(pc==0xdd6b || pc==0xdd73 || pc==0xdd62)
		{
			yield_cpu = TRUE;
			saved_icount = cpu_geticount();
			cpu_seticount (0);
		}
		#endif*/
	
		return qix_sharedram.read(offset);
	} };
	
	public static ReadHandlerPtr qix_sharedram_r_2 = new ReadHandlerPtr() { public int handler(int offset)
	{
		/* ASG - 970726 - CPU 2 sits and spins on offset 37D */
		if (offset == 0x37d)
		{
			int count = cpu_geticount ();
	
			/* this is a less tight loop; stop if less than 60 cycles apart */
			if (lastcheck2 < count + 60 && cpu_getpc () == 0xc894)
			{
				cpu_seticount (0);
				lastcheck2 = 0x7fffffff;
			}
			else
				lastcheck2 = count;
		}
		return qix_sharedram.read(offset);
	} };
	
	
	
	public static WriteHandlerPtr qix_sharedram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		qix_sharedram.write(offset,data);
	} };
	
	
	
	public static WriteHandlerPtr qix_video_firq_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		/* generate firq for video cpu */
		m6809context ctxt = (m6809context)cpucontext[1];
                ctxt.irq = INT_FIRQ;
	} };
	
	
	
	public static WriteHandlerPtr qix_data_firq_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		/* generate firq for data cpu */
		m6809context ctxt = (m6809context)cpucontext[0];
                ctxt.irq = INT_FIRQ;
	} };
	
	
	
	/* Because all the scanlines are drawn at once, this has no real
	   meaning. But the game sometimes waits for the scanline to be $0
	   and other times for it to be $ff, so we fake it. Ugly, huh?
	*/
        static  char hack = 0x00;
	public static ReadHandlerPtr qix_scanline_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		
	
		/* scanline is always $0 or $ff */
		if( hack==0x00 ) hack = 0xff; else hack = 0x00;
		return hack;
	} };
	
	
	
	public static InitMachinePtr qix_init_machine = new InitMachinePtr() { public void handler() 
	{
		/* Set OPTIMIZATION FLAGS FOR M6809 */
		m6809_Flags = M6809_FAST_OP | M6809_FAST_S;/* | M6809_FAST_U;*/
	
		/* Reset the checking flags */
		lastcheck2 = 0x7fffffff;
	} };
}
