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
 * using automatic conversion tool v0.01
 * converted at : 23-08-2011 23:28:19
 *
 *
 *
 */ 
package sndhrdw;

//arcadeflex libs
import static arcadeflex.libc.*;
import static arcadeflex.osdepend.*;
//mame libs
import static mame.driverH.*;
import static mame.mame.*;
import static mame.cpuintrf.*;
//sndhrdw libs
import static sndhrdw.generic.*;

public class gottlieb
{
	
	static int AUDIO_CONV(int A){ return (A+0x80); }
	
	
	static final int TARGET_EMULATION_RATE= 44100;     /* will be adapted to be a multiple of buffer_len */
	static int emulation_rate;
	static int buffer_len;
	static char buffer[];
	static int sample_pos;
	
	
	static char amplitude_DAC;
	
	
	public static WriteHandlerPtr gottlieb_sh_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	    int command= 255-data;
	    if (command != 0) sound_command_w.handler(offset,command);
	} };
	
	public static WriteHandlerPtr gottlieb_sh2_w = new WriteHandlerPtr() { public void handler(int offset, int command)
	{
	    if (command != 0) sound_command_w.handler(offset,command);
	} };
	
	
	public static ShUpdatePtr gottlieb_sh_update = new ShUpdatePtr() { public void handler() 
	{
		while (sample_pos < buffer_len)
			buffer[sample_pos++] = amplitude_DAC;
	
		osd_play_streamed_sample(0,buffer,buffer_len,emulation_rate,255);
	
		sample_pos=0;
	} };
	
	public static WriteHandlerPtr qbert_sh_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{} };
	
	public static ShStartPtr gottlieb_sh_start = new ShStartPtr() { public int handler() 
	{
	    buffer_len = TARGET_EMULATION_RATE / Machine.drv.frames_per_second;
	    emulation_rate = buffer_len * Machine.drv.frames_per_second;
	
	    if ((buffer = new char[buffer_len]) == null)
		return 1;
	    memset(buffer,0x80,buffer_len);
	
	    sample_pos = 0;
	
	    return 0;
	} };
	
	
	
	public static ShStopPtr gottlieb_sh_stop = new ShStopPtr() { public void handler() 
	{
	    buffer=null;
	} };
	
	
	
	public static WriteHandlerPtr gottlieb_amplitude_DAC_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		int totcycles,leftcycles,newpos;
	
	
		totcycles = Machine.drv.cpu[1].cpu_clock / Machine.drv.frames_per_second;
		leftcycles = cpu_getfcount();
		newpos = buffer_len * (totcycles-leftcycles) / totcycles;
	
		while (sample_pos < newpos-1)
		    buffer[sample_pos++] = amplitude_DAC;
	
	    amplitude_DAC=(char)AUDIO_CONV(data);
	
	    buffer[sample_pos++] = amplitude_DAC;
	} };
	
	
	public static InterruptPtr gottlieb_sh_interrupt = new InterruptPtr() { public int handler() 
	{
	    if (pending_commands != 0) return interrupt.handler();
	    else return ignore_interrupt.handler();
	} };
	
	public static ReadHandlerPtr gottlieb_sound_expansion_socket_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	    return 0;
	} };
	
	public static WriteHandlerPtr gottlieb_speech_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{} };
	
	public static WriteHandlerPtr gottlieb_speech_clock_DAC_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{} };
	
	public static WriteHandlerPtr gottlieb_sound_expansion_socket_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{} };
	
	    /* partial decoding takes place to minimize chip count in a 6502+6532
	       system, so both page 0 (direct page) and 1 (stack) access the same
	       128-bytes ram,
	       either with the first 128 bytes of the page or the last 128 bytes */
	
	public static ReadHandlerPtr riot_ram_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	    return RAM[offset&0x7f];
	} };
	
	public static WriteHandlerPtr riot_ram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		/* pb is that M6502.c does some memory reads directly, so we
		  repeat the writes */
	    RAM[offset&0x7F]=(char)data;
	    RAM[0x80+(offset&0x7F)]=(char)data;
	    RAM[0x100+(offset&0x7F)]=(char)data;
	    RAM[0x180+(offset&0x7F)]=(char)data;
	} };
	
	static char[] riot_regs=new char[32];
	    /* lazy handling of the 6532's I/O, and no handling of timers at all */
	
	public static ReadHandlerPtr gottlieb_riot_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	    switch (offset&0x1f) {
		case 0: /* port A */
			return sound_command_r.handler(offset);
		case 2: /* port B */
			return 0x40;    /* say that PB6 is 1 (test SW1 not pressed) */
		case 5: /* interrupt register */
			return 0x40;    /* say that edge detected on PA7 */
		default:
			return riot_regs[offset&0x1f];
	    }
	} };
	
	public static WriteHandlerPtr gottlieb_riot_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	    riot_regs[offset&0x1f]=(char)data;
	} };
}
