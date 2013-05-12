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
 *  HIGHLY WIP
 */
package sndhrdw;


import static sndhrdw.generic.*;
import static mame.driverH.*;
import static mame.cpuintrf.*;

public class starforc 
{
    public static WriteHandlerPtr starforce_pio_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
    {
           // z80pio_w( &pio , (offset/2)&0x01 , offset&0x01 , data );
    }};
    public static ReadHandlerPtr starforce_pio_r = new ReadHandlerPtr() { public int handler(int offset)
    {
            return 0;//z80pio_r( &pio , (offset/2)&0x01 , offset&0x01 );
    }};
    public static ReadHandlerPtr starforce_pio_p_r = new ReadHandlerPtr() { public int handler(int offset)
    {
            return 0;//z80pio_p_r( &pio , 0 );
    }};
    public static WriteHandlerPtr starforce_ctc_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
    {
            //z80ctc_w( &ctc , offset , data );
    }};
    public static ReadHandlerPtr starforce_ctc_r = new ReadHandlerPtr() { public int handler(int offset)
    {
            return 0;//z80ctc_r( &ctc , offset );
    }};
    public static WriteHandlerPtr starforce_sh_0_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
    {
           // SN76496Write(&sn[1],data);
    }};
    public static WriteHandlerPtr starforce_sh_1_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
    {
            //SN76496Write(&sn[2],data);
    }};
    public static WriteHandlerPtr starforce_sh_2_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
    {
           // SN76496Write(&sn[0],data);
    }};
    public static WriteHandlerPtr starforce_volume_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
    {
            //single_volume = ((data & 0x0f)<<4)|(data & 0x0f);
    }};
    public static InterruptPtr starforce_sh_interrupt = new InterruptPtr() { public int handler()   
    {
    /*        int irq = 0;

            /* ctc2 timer single tone generator frequency */
    /*        single_rate = CPU_CLOCK / ctc.timec[2] * ((ctc.mode[2]&0x20)? 1:16);
            /* ctc_0 cascade to ctc_1 , interval interrupt */
    /*        if( z80ctc_update(&ctc,1,UPCLK, z80ctc_update(&ctc,0,UPCLK,1) ) ){
                    /* interrupt check */
    /*                if( (irq = z80ctc_irq_r(&ctc)) != Z80_IGNORE_INT ) return irq;
    /*        }
            /* pio interrupt check */
    /*        if (pending_commands){
                    z80pio_p_w( &pio , 0 , sound_command_r(0) );
                    if( (irq = z80pio_irq_r(&pio)) != Z80_IGNORE_INT ) return irq;
            }
            return Z80_IGNORE_INT;*/
            return -1;
    }};
    public static ShStartPtr starforce_sh_start = new ShStartPtr() { public int handler()
    {
        /*    int i,j;

            pending_commands = 0;

            z80ctc_reset( &ctc , CPU_CLOCK );
            z80pio_reset( &pio );

            if ((sample = malloc(buffer_len)) == 0)
                    return 1;

            if ((single = malloc(SINGLE_LENGTH)) == 0)
            {
                    free(sample);
                    free(single);
                    return 1;
            }
            for (i = 0;i < SINGLE_LENGTH;i++)		/* freq = ctc2 zco / 8 */
     /*               single[i] = ((i/SINGLE_DIVIDER)&0x01)*(SINGLE_VOLUME/2);

            for (j = 0;j < CHIPS;j++)
            {
                    sn[j].Clock = SND_CLOCK;
                    SN76496Reset(&sn[j]);
            }
            /* CTC2 single tone generator */
    /*        osd_play_sample(CHIPS ,single,SINGLE_LENGTH,single_rate,single_volume,1);
         
     */
            return 0;
    }};
    public static ShStopPtr starforce_sh_stop = new ShStopPtr() { public void handler()
    {
           // free(sample);
           // free(single);
    }};
    public static ShUpdatePtr starforce_sh_update = new ShUpdatePtr() { public void handler()
    {
   /*         int i;

            if (play_sound == 0) return;
            for (i = 0;i < CHIPS;i++)
            {
                    SN76496UpdateB(&sn[i] , emulation_rate , sample , buffer_len );
                    osd_play_streamed_sample(i,sample,buffer_len,emulation_rate,SSG_VOLUME );
            }

            /* CTC2 single tone generator */
    /*        osd_adjust_sample(CHIPS,single_rate,single_volume );
     
     */
    }};   
}
