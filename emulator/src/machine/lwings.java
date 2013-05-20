//TODO fix it when adding the new memory system

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
import static arcadeflex.osdepend.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;

public class lwings
{
	static int nOldData=0xff;
	public static WriteHandlerPtr lwings_bankswitch_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	        int bankaddress;
	        
	        if (data != nOldData)
	        {
	                bankaddress = 0x10000 + (data & 0x06) * 0x1000 * 2;
//cpu_setbank(1,&ROM[bankaddress]);//TODO fix it when fixing banking
/*TEMPHACK*/            memcpy(ROM,0x8000,ROM,bankaddress,0x4000);//TODO remove temp fix
	                nOldData=data;
	        }
	} };
	static int count;
	public static InterruptPtr lwings_interrupt = new InterruptPtr() { public int handler() 
	{
			
		count = (count + 1) % 2;
	
		if (count != 0) return 0x00cf;	/* RST 08h */
		else return 0x00d7;	/* RST 10h */
	} };
}
