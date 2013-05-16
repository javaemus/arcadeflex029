/* 
 * ported to v0.29
 * ported to v0.28
 * ported to v0.27
 *
 *
 *
 */

package sndhrdw;

import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.mame.*;
/***************************************************************************

  Many games use a master-slave CPU setup. Typically, the main CPU writes
  a command to some register, and then writes to another register to trigger
  an interrupt on the slave CPU (the interrupt might also be triggered by
  the first write). The slave CPU, notified by the interrupt, goes and reads
  the command.

  Currently, MAME doesn't interleave the execution of the two CPUs: they run
  alternatively for one complete video frame (~50000 clock cycles for a 3Mhz
  CPU). This could lead to data loss if the main CPU sends a quick sequence
  of commands. To avoid that, we use a buffer.

***************************************************************************/
public class generic {
	static final int QUEUE_LENGTH = 10;	/* hopefully enough! */

	static int command_queue[] = new int[QUEUE_LENGTH];
	public static int pending_commands;


        public static WriteHandlerPtr sound_command_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
		if (pending_commands < QUEUE_LENGTH)
		{
			command_queue[pending_commands] = data;
			pending_commands++;
		}
		else
		{
			if (errorlog != null) fprintf(errorlog, "error: sound command queue overflow!\n");
		}
	} };
        
	public static ReadHandlerPtr sound_command_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		int i, res;


		if (pending_commands > 0)
		{
			res = command_queue[0];

			pending_commands--;

			for (i = 0; i < pending_commands; i++)
				command_queue[i] = command_queue[i+1];
		}
		else
		{
			if (errorlog != null) fprintf(errorlog, "warning: read sound command, but queue empty\n");
			res = 0;
		}

		return res;
	} };

	public static ReadHandlerPtr sound_command_latch_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		int i, res;


		res = command_queue[0];

		if (pending_commands > 0)
		{
			pending_commands--;

			for (i = 0; i < pending_commands; i++)
				command_queue[i] = command_queue[i+1];
		}

		return res;
	} };
    public static ReadHandlerPtr sound_pending_commands_r = new ReadHandlerPtr() { public int handler(int offset)
    {
            if (pending_commands > 0) return 0xff;
            else return 0;
    }};
    
    static int latch,read_debug;
    
    public static WriteHandlerPtr soundlatch_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
    {
    if (errorlog!=null && read_debug == 0)
            fprintf(errorlog,"Warning: sound latch written before being read. Previous: %02x, new: %02x\n",latch,data);
            latch = data;
            read_debug = 0;
    }};
    public static ReadHandlerPtr soundlatch_r = new ReadHandlerPtr() { public int handler(int offset)
    {
            read_debug = 1;
            return latch;
    }};

}
