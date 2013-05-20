/***************************************************************************

  machine.c

  Functions to emulate general aspects of the machine (RAM, ROM, interrupts,
  I/O ports)

***************************************************************************/

/*
 * ported to v0.29
 * using automatic conversion tool v0.04
 * converted at : 30-05-2012 23:04:54
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

public class sidearms
{
	static int olddata=0xff;
	public static WriteHandlerPtr blktiger_bankswitch_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	        
	        int bankaddress;
	        if (data != olddata)
	        {
	                bankaddress = 0x10000 + (data & 0x0f) * 0x4000 ;
	                memcpy(RAM,0x8000, ROM,bankaddress, 0x4000);
	                olddata=data;
	        }
	} };
	
	public static InterruptPtr blktiger_interrupt = new InterruptPtr() { public int handler() 
	{
	        if (osd_key_pressed(OSD_KEY_F))
	        {
	                FILE fp=fopen("RAM.DMP", "w+b");
	                if (fp != null)
	                {
	                        fwrite(RAM, 0x10000, 1, fp);
	                        fclose(fp);
	                }
	        }
	
	        return 0x38;
	} };
}
