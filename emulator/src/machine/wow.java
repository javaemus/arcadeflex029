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
 * using automatic conversion tool v0.03
 * converted at : 25-08-2011 11:42:03
 *
 *
 *
 */ 
package machine;

import static arcadeflex.libc.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static Z80.Z80H.*;
import static mame.inptport.*;
import static vidhrdw.wow.*;

public class wow
{
	

	/****************************************************************************
	 * Scanline Interrupt System
	 ****************************************************************************/
	
	static int NextScanInt=0;			/* Normal */
	public static int CurrentScan=0;
	static int InterruptFlag=0;
	
	public static int Controller1=32;			/* Seawolf II */
	public static int Controller2=32;
	
	static int GorfDelay;				/* Gorf */
	static int Countdown=0;
	
	public static WriteHandlerPtr wow_interrupt_enable_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	    InterruptFlag = data;
	
	    if ((data & 0x01) != 0)					/* Disable Interrupts? */
	  	    interrupt_enable_w.handler(0,0);
	    else
	  		interrupt_enable_w.handler(0,1);
	
	    /* Gorf Special interrupt */
	
	    if ((data & 0x10) != 0)
	 	{
	  		GorfDelay =(CurrentScan + 10) & 0xFF;
	
	        /* Gorf Special *MUST* occur before next scanline interrupt */
	
	        if ((NextScanInt > CurrentScan) && (NextScanInt < GorfDelay))
	        {
	          	GorfDelay = NextScanInt - 1;
	        }
	
	        if (errorlog != null) fprintf(errorlog,"Gorf Delay set to %02x\n",GorfDelay);
	    }
	
	    if (errorlog != null) fprintf(errorlog,"Interrupt Flag set to %02x\n",InterruptFlag);
	} };
	
	public static WriteHandlerPtr wow_interrupt_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		/* A write to 0F triggers an interrupt at that scanline */
	
		if (errorlog != null) fprintf(errorlog,"Scanline interrupt set to %02x\n",data);
	
	    NextScanInt = data;
	} };
	
	public static InterruptPtr wow_interrupt = new InterruptPtr() { public int handler() 
	{
		int res=Z80_IGNORE_INT;
	    int Direction;
	
	    CurrentScan++;
	
	    if (CurrentScan == Machine.drv.cpu[0].interrupts_per_frame)
		{
			CurrentScan = 0;
	
	    	/*
			 * Seawolf2 needs to emulate rotary ports
	         *
	         * Checked each flyback, takes 1 second to traverse screen
	         */
	
	        Direction = input_port_0_r.handler(0);
	
	        if (((Direction & 2)!=0) && (Controller1 > 0))
				Controller1--;
	
			if (((Direction & 1)!=0) && (Controller1 < 63))
				Controller1++;
	
	        Direction = input_port_1_r.handler(0);
	
	        if (((Direction & 2)!=0) && (Controller2 > 0))
				Controller2--;
	
			if (((Direction & 1)!=0) && (Controller2 < 63))
				Controller2++;
	    }
	
	    if (CurrentScan < 204) CopyLine(CurrentScan);
	
	    /* Scanline interrupt enabled ? */
	
	    if (((InterruptFlag & 0x08)!=0) && (CurrentScan == NextScanInt))
			res = interrupt.handler();
	
	    return res;
	} };
	
	/****************************************************************************
	 * Gorf - Interrupt routine and Timer hack
	 ****************************************************************************/
	
	public static InterruptPtr gorf_interrupt = new InterruptPtr() { public int handler() 
	{
		int res=Z80_IGNORE_INT;
	
	    CurrentScan++;
	
	    if (CurrentScan == 256)
		{
			CurrentScan=0;
	    }
	
	    if (CurrentScan < 204) Gorf_CopyLine(CurrentScan);
	
	    /* Scanline interrupt enabled ? */
	
	    if (((InterruptFlag & 0x08)!=0) && (CurrentScan == NextScanInt))
			res = interrupt.handler();
	
	
	    /* Gorf Special Bits */
	
	    if (Countdown>0) Countdown--;
	
	    if (((InterruptFlag & 0x10)!=0) && (CurrentScan==GorfDelay))
			res = interrupt.handler() & 0xF0;
	
		cpu_clear_pending_interrupts(0);
	
	    return res;
	} };
	static int Skip=0;
	public static ReadHandlerPtr gorf_timer_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		
	
		if ((RAM[0x5A93]==160) || (RAM[0x5A93]==4)) 	/* INVADERS AND    */
		{												/* GALAXIAN SCREEN */
	        if (cpu_getpc()==0x3086)
	        {
	    	    if(--Skip==-1)
	            {
	                Skip=2;
	            }
	        }
	
		   	return Skip;
	    }
	    else
	    {
	    	return RAM[0xD0A5];
	    }
	
	} };
	
	/****************************************************************************
	 * Seawolf Controllers
	 ****************************************************************************/
	
	/*
	 * Seawolf2 uses rotary controllers on input ports 10 + 11
	 * each controller responds 0-63 for reading, with bit 7 as
	 * fire button. Controller values are calculated in the
	 * interrupt routine, and just formatted & returned here.
	 *
	 * The controllers look like they returns Grays binary,
	 * so I use a table to translate my simple counter into it!
	 */
	
	static int ControllerTable[] = {
	    0  , 1  , 3  , 2  , 6  , 7  , 5  , 4  ,
	    12 , 13 , 15 , 14 , 10 , 11 , 9  , 8  ,
	    24 , 25 , 27 , 26 , 30 , 31 , 29 , 28 ,
	    20 , 21 , 23 , 22 , 18 , 19 , 17 , 16 ,
	    48 , 49 , 51 , 50 , 54 , 55 , 53 , 52 ,
	    60 , 61 , 63 , 62 , 58 , 59 , 57 , 56 ,
	    40 , 41 , 43 , 42 , 46 , 47 , 45 , 44 ,
	    36 , 37 , 39 , 38 , 34 , 35 , 33 , 32
	};
	
	public static ReadHandlerPtr seawolf2_controller1_r = new ReadHandlerPtr() { public int handler(int offset)
	{
            return (input_port_0_r.handler(0) & 0x80) + ControllerTable[Controller1];
	} };
	
	public static ReadHandlerPtr seawolf2_controller2_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	    return (input_port_1_r.handler(0) & 0x80) + ControllerTable[Controller2];
	} };
	
}
