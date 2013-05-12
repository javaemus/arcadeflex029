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
 *
 *
 *  THIS FILE needs some more work to get it working...
 */


/***************************************************************************

  8910intf.c

  Another lovable case of MS-DOS 8+3 name. That stands for 8910 interface.
  Many games use the AY-3-8910 to produce sound; the functions contained in
  this file make it easier to interface with it.

***************************************************************************/

package sndhrdw;

import static arcadeflex.libc.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static sndhrdw._8910intfH.*;
import static sndhrdw.psg.*;
import static sndhrdw.psgH.*;
import static mame.osdependH.*;
import static arcadeflex.osdepend.*;

public class _8910intf
{


	static final int TARGET_EMULATION_RATE = 44100;/* will be adapted to be a multiple of buffer_len */
	static int emulation_rate;
	static int buffer_len;

	static AY8910interface intf;
        public static char buffer[][]=new char[MAX_8910][];



	private static int porthandler(int num, AY8910 chip, int port, int iswrite, int val)
	{

		if (iswrite != 0)
		{
			if (port == 0x0e)
			{
				if (intf.portAwrite[num] != null)
                                {
                                    intf.portAwrite[num].handler(0, val);
                                }
				else if (errorlog != null) fprintf(errorlog, "PC %04x: warning - write %02x to 8910 #%d Port A\n", cpu_getpc(), val, num);
			}
			else
			{
				if (intf.portBwrite[num] != null) intf.portBwrite[num].handler(1, val);
				else if (errorlog != null) fprintf(errorlog, "PC %04x: warning - write %02x to 8910 #%d Port B\n", cpu_getpc(), val, num);
			}
		}
		else
		{
			chip.Regs[port] = 0;
	
			if (port == 0x0e)
			{
				if (intf.portAread[num] != null) chip.Regs[port] = intf.portAread[num].handler(0);
				else if (errorlog != null) fprintf(errorlog, "PC %04x: warning - read 8910 #%d Port A\n", cpu_getpc(), num);
			}
			else
			{
				if (intf.portBread[num] != null) chip.Regs[port] = intf.portBread[num].handler(1);
				else if (errorlog != null) fprintf(errorlog, "PC %04x: warning - read 8910 #%d Port B\n", cpu_getpc(), num);
			}
		}
	
		return 0;
	}
	
	
	
	public static AYPortHandler porthandler0 = new AYPortHandler() { public int handler(AY8910 chip, int port, int iswrite, int val)
	{
		return porthandler(0, chip, port, iswrite, val);
	} };
	public static AYPortHandler porthandler1 = new AYPortHandler() { public int handler(AY8910 chip, int port, int iswrite, int val)
	{
		return porthandler(1, chip, port, iswrite, val);
	} };
	public static AYPortHandler porthandler2 = new AYPortHandler() { public int handler(AY8910 chip, int port, int iswrite, int val)
	{
		return porthandler(2, chip, port, iswrite, val);
	} };
	public static AYPortHandler porthandler3 = new AYPortHandler() { public int handler(AY8910 chip, int port, int iswrite, int val)
	{
		return porthandler(3, chip, port, iswrite, val);
	} };
	public static AYPortHandler porthandler4 = new AYPortHandler() { public int handler(AY8910 chip, int port, int iswrite, int val)
	{
		return porthandler(4, chip, port, iswrite, val);
	} };



	public static int AY8910_sh_start(AY8910interface _interface)
	{
                int i;
                intf = _interface;
            	buffer_len = TARGET_EMULATION_RATE / Machine.drv.frames_per_second / intf.updates_per_frame;
                emulation_rate = buffer_len * Machine.drv.frames_per_second * intf.updates_per_frame;

                for (i = 0;i < MAX_8910;i++)  buffer[i]=null;
                for (i = 0;i < intf.num;i++)
                {
                    if ((buffer[i] = new char[buffer_len * intf.updates_per_frame]) == null)
                    {
			while (--i >= 0) buffer[i]=null;
			return 1;
                    }
                    memset(buffer[i],0x80,buffer_len * intf.updates_per_frame);
                }
	        
		if (AYInit(intf.num, intf.clock, emulation_rate, buffer_len) == 0)
		{
			AYSetPortHandler(0, AY_PORTA, porthandler0);
			AYSetPortHandler(0, AY_PORTB, porthandler0);
			if (intf.num > 1)
			{
				AYSetPortHandler(1, AY_PORTA, porthandler1);
				AYSetPortHandler(1, AY_PORTB, porthandler1);
			}
			if (intf.num > 2)
			{
				AYSetPortHandler(2, AY_PORTA, porthandler2);
				AYSetPortHandler(2, AY_PORTB, porthandler2);
			}
			if (intf.num > 3)
			{
				AYSetPortHandler(3, AY_PORTA, porthandler3);
				AYSetPortHandler(3, AY_PORTB, porthandler3);
			}
			if (intf.num > 4)
			{
				AYSetPortHandler(4, AY_PORTA, porthandler4);
				AYSetPortHandler(4, AY_PORTB, porthandler4);
			}
	
			return 0;
		}
		else return 1;
	}
	
	
	
	public static ShStopPtr AY8910_sh_stop = new ShStopPtr() { public void handler()
	{
	    int i;
            AYShutdown();
            for (i = 0;i < intf.num;i++) buffer[i]=null;
	} };
	
	
	
	static int lastreg0, lastreg1, lastreg2, lastreg3, lastreg4;	/* AY-3-8910 register currently selected */
	
	
	public static ReadHandlerPtr AY8910_read_port_0_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return AYReadReg(0, lastreg0);
	} };
	public static ReadHandlerPtr AY8910_read_port_1_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return AYReadReg(1, lastreg1);
	} };
	public static ReadHandlerPtr AY8910_read_port_2_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return AYReadReg(2, lastreg2);
	} };
	public static ReadHandlerPtr AY8910_read_port_3_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return AYReadReg(3, lastreg3);
	} };
	public static ReadHandlerPtr AY8910_read_port_4_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return AYReadReg(4, lastreg4);
	} };
	
	
	
	public static WriteHandlerPtr AY8910_control_port_0_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
		lastreg0 = data;
	} };
	public static WriteHandlerPtr AY8910_control_port_1_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		lastreg1 = data;
	} };
	public static WriteHandlerPtr AY8910_control_port_2_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
		lastreg2 = data;
	} };
	public static WriteHandlerPtr AY8910_control_port_3_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
		lastreg3 = data;
	} };
	public static WriteHandlerPtr AY8910_control_port_4_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		lastreg4 = data;
	} };
	
	
	
	public static WriteHandlerPtr AY8910_write_port_0_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		AYWriteReg(0, lastreg0, data);
	} };
	public static WriteHandlerPtr AY8910_write_port_1_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		AYWriteReg(1, lastreg1, data);
	} };
	public static WriteHandlerPtr AY8910_write_port_2_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		AYWriteReg(2, lastreg2, data);
	} };
	public static WriteHandlerPtr AY8910_write_port_3_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		AYWriteReg(3, lastreg3, data);
	} };
	public static WriteHandlerPtr AY8910_write_port_4_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		AYWriteReg(4, lastreg4, data);
	} };

	static int updatecount;
        public static void AY8910_update()
        {
                	int i;


            if (updatecount >= intf.updates_per_frame) return;

            for (i = 0;i < intf.num;i++)
            {
                   // AYSetBuffer(i,buffer[i][updatecount * buffer_len]);
            }
            AYUpdate();

            updatecount++;
        }
	
	public static ShUpdatePtr AY8910_sh_update = new ShUpdatePtr() { public void handler()
	{
            int i;


            if (play_sound == 0) return;

            if (intf.updates_per_frame == 1) AY8910_update();

        if ((errorlog!=null) && updatecount != intf.updates_per_frame)
            fprintf(errorlog,"Error: AY8910_update() has not been called %d times in a frame\n",intf.updates_per_frame);

	updatecount = 0;	/* must be zeroed here to keep in sync in case of a reset */

	for (i = 0;i < intf.num;i++)
        {
		//AYSetBuffer(i,buffer[i][0]);
        }

	/* update FM music */
         //osd_ym2203_update();

	for (i = 0;i < intf.num;i++)
        {
		//osd_play_streamed_sample(i,buffer[i],buffer_len * intf->updates_per_frame,emulation_rate,intf->volume[i]);
        }
	} };
}