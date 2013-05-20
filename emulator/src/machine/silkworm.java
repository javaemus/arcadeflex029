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

public class silkworm
{
	
	
	static int bankaddress;
	
	public static WriteHandlerPtr silkworm_bankswitch_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		bankaddress = 0x10000 + ((data & 0xf8) << 8);
	} };
	
	
	
	public static ReadHandlerPtr silkworm_bankedrom_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return RAM[bankaddress + offset];
	} };
}
