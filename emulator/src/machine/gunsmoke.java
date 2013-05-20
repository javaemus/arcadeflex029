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

public class gunsmoke
{
	
	static int bankaddress;
	
	public static WriteHandlerPtr gunsmoke_bankswitch_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	if (bankaddress != 0x10000 + ((data & 0x0c) * 0x1000))
	{
		bankaddress = 0x10000 + ((data & 0x0c) * 0x1000);
		memcpy(RAM,0x8000,RAM,bankaddress,0x4000);
	}
	} };
	
	public static ReadHandlerPtr gunsmoke_bankedrom_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return RAM[bankaddress + offset];
	} };
}
