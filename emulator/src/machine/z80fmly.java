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

package machine;

import static arcadeflex.libc.*;
import static machine.z80fmlyH.*;
import static mame.cpuintrf.*;
import static mame.mame.*;
import static Z80.Z80H.*;

public class z80fmly {
        /* these are the bits of the incoming commands to the CTC */
        static final int INTERRUPT     =0x80;
        static final int INTERRUPT_ON  =0x80;
        static final int INTERRUPT_OFF	=0x00;

        static final int MODE				=0x40;
        static final int MODE_TIMER		=0x00;
        static final int MODE_COUNTER		=0x40;

        static final int PRESCALER			=0x20;
        static final int PRESCALER_256	=0x20;
        static final int PRESCALER_16		=0x00;

        static final int EDGE				=0x10;
        static final int EDGE_FALLING		=0x00;
        static final int EDGE_RISING		=0x10;

        static final int TRIGGER			=0x08;
        static final int TRIGGER_AUTO		=0x00;
        static final int TRIGGER_CLOCK	=0x08;

        static final int CONSTANT			=0x04;
        static final int CONSTANT_LOAD	=0x04;
        static final int CONSTANT_NONE	=0x00;

        static final int RESET				=0x02;
        static final int RESET_CONTINUE	=0x00;
        static final int RESET_ACTIVE		=0x02;

        static final int CONTROL			=0x01;
        static final int CONTROL_VECTOR	=0x00;
        static final int CONTROL_WORD		=0x01;

        /* these extra bits help us keep things accurate */
        static final int WAITING_FOR_TRIG	=0x100; 
        
        static void z80ctc_reset (Z80CTC ctc, int system_clock)
        {
                int ch;

                /* erase everything */
                
                //memset (ctc, 0, sizeof (*ctc));//is already called when constructing (shadow)

                /* insert default timers */
                for (ch = 0; ch < 4; ch++)
                {
                        ctc.mode[ch] = RESET_ACTIVE;
                        ctc.down[ch] = 0x7fffffff;
                        ctc.last[ch] = cpu_gettotalcycles();
                        ctc.tconst[ch] = 0x100;
                }

                /* set the system clock */
                ctc.sys_clk = system_clock;
        }
        static void z80ctc_w (Z80CTC ctc, int ch, int data)
        {
                int mode, i;

                /* keep channel within range, and get the current mode */
                ch &= 3;
                mode = ctc.mode[ch];

                /* if we're waiting for a time constant, this is it */
                if ((mode & CONSTANT) == CONSTANT_LOAD)
                {
                        /* set the time constant (0 . 0x100) */
                        ctc.tconst[ch] = data!=0 ? data : 0x100;

                        /* clear the internal mode -- we're no longer waiting */
                        ctc.mode[ch] &= ~CONSTANT;

                        /* also clear the reset, since the constant gets it going again */
                        ctc.mode[ch] &= ~RESET;

                        /* if we're in timer mode.... */
                        if ((mode & MODE) == MODE_TIMER)
                        {
                                /* if we're triggering on the time constant, reset the down counter now */
                                if ((mode & TRIGGER) == TRIGGER_AUTO)
                                        ctc.down[ch] = ctc.tconst[ch] << 8;

                                /* else set the bit indicating that we're waiting for the appropriate trigger */
                                else
                                {
                                        long delta = (ctc.fall[ch] - cpu_gettotalcycles ()) & 0xFFFFFFFFL;//unsigned!

                                        /* if we're waiting for the falling edge and we know when that is, set it up */
                                        if ((mode & EDGE) == EDGE_FALLING && delta < ctc.sys_clk)
                                                ctc.down[ch] = (int)((ctc.tconst[ch] << 8) + delta);

                                        /* otherwise, just indicate that we're waiting for a trigger */
                                        else
                                                ctc.mode[ch] |= WAITING_FOR_TRIG;
                                }
                        }

                        /* else just set the down counter now */
                        else
                                ctc.down[ch] = ctc.tconst[ch] << 8;

                        /* all done here */
                        return;
                }

                /* if we're writing the interrupt vector, handle it specially */
                if ((data & CONTROL) == CONTROL_VECTOR && ch == 0)
                {
                        ctc.vector = data & 0xf8;
                        if (errorlog!=null) fprintf (errorlog, "CTC Vector = %02x\n", ctc.vector);
                        return;
                }

                /* this must be a control word */
                if ((data & CONTROL) == CONTROL_WORD)
                {
                        /* set the new mode */
                        ctc.mode[ch] = data;
                        if (errorlog!=null) fprintf (errorlog,"CTC ch.%d mode = %02x\n", ch, data);

                        /* if we're being reset, clear out any pending interrupts for this channel */
                        if ((data & RESET) == RESET_ACTIVE)
                        {
                                for (i = 0; i < MAX_IRQ; i++)
                                        if (ctc.irq[i].irq == ctc.vector + (ch << 1))
                                                ctc.irq[i].irq = 0;
                        }

                        /* all done here */
                        return;
                }
        }


        static int z80ctc_r (Z80CTC ctc, int ch)
        {
                /* keep channel within range */
                ch &= 3;

                /* return the current down counter value */
                return ctc.down[ch] >> 8;
        }
        static int z80ctc_update (Z80CTC ctc, int ch, int cntclk, int cnthold)
        {
                long time = cpu_gettotalcycles ();
                int mode, zco = 0;
                int upclk, sysclk, i;

                /* keep channel within range, and get the current mode */
                ch &= 3;
                mode = ctc.mode[ch];

                /* increment the last timer update */
                sysclk = (int)((time - ctc.last[ch])&0xFFFFFFFFL);//unsigned
                ctc.last[ch] = time&0xFFFFFFFFL;//unsigned

                /* if we got an external clock, handle it */
                if (cntclk!=0)
                {
                        /* set the time of the falling edge here */
                        ctc.fall[ch] = (time + cnthold)&0xFFFFFFFFL;//unsigned

                        /* if this timer is waiting for a trigger, we can resolve it now */
                        if ((mode & WAITING_FOR_TRIG)!=0)
                        {
                                /* first clear the waiting flag */
                                ctc.mode[ch] &= ~WAITING_FOR_TRIG;
                                mode &= ~WAITING_FOR_TRIG;

                                /* rising edge? */
                                if ((mode & EDGE) == EDGE_RISING)
                                        ctc.down[ch] = ctc.tconst[ch] << 8;

                                /* falling edge? */
                                else
                                        ctc.down[ch] = (ctc.tconst[ch] << 8) + cnthold;
                        }
                }

                /* if this channel is reset, we're done */
                if ((mode & RESET) == RESET_ACTIVE)
                        return 0;

                /* if this channel is in timer mode waiting for a trigger, we're done */
                if ((mode & WAITING_FOR_TRIG)!=0)
                        return 0;

                /* select the clock to use */
                if ((mode & MODE) == MODE_TIMER)
                {
                        if ((mode & PRESCALER) == PRESCALER_16)
                                upclk = sysclk << 4;
                        else
                                upclk = sysclk;
                }
                else
                        upclk = cntclk << 8;

                /* if the clock isn't updated this time, bail */
                if (upclk==0)
                        return 0;

                /* decrement the counter and count zero crossings */
                ctc.down[ch] -= upclk;
                while (ctc.down[ch] <= 0)
                {
                        /* if we're doing interrupts, add a pending one */
                        if ((mode & INTERRUPT) == INTERRUPT_ON)
                        {
                                for (i = 0; i < MAX_IRQ; i++)
                                        if (ctc.irq[i].irq==0)
                                        {
                                                ctc.irq[i].irq = ctc.vector + (ch << 1);
                                                ctc.irq[i].time = (time + ctc.down[ch])&0xFFFFFFFFL;//unsinged
                                                break;
                                        }
                        }

                        /* update the counters */
                        zco += 1;
                        ctc.down[ch] += ctc.tconst[ch] << 8;
                }

                /* log it */
                if (errorlog!=null && zco!=0)
                {
                        if ((mode & MODE) == MODE_COUNTER)
                                fprintf (errorlog, "CTC Ch.%d trigger\n", ch);
                        else
                                fprintf (errorlog, "CTC Ch.%d rollover x%d\n", ch, zco);
                }

                /* return the number of zero crossings */
                return zco;
        }


        static int z80ctc_irq_r (Z80CTC ctc)
        {
                long basetime = (cpu_gettotalcycles () - ctc.sys_clk)&0xFFFFFFFFL;	/* no more than 1 second behind! */
                long time, earliestTime = 0xffffffffL;
                int i, earliest = -1;

                /* find the earliest IRQ */
                for (i = 0; i < MAX_IRQ; i++)
                        if (ctc.irq[i].irq!=0)
                        {
                                time = (ctc.irq[i].time - basetime)&0xFFFFFFFFL;
                                if (time < earliestTime)
                                {
                                        earliest = i;
                                        earliestTime = time;
                                }
                        }

                /* if none, bail */
                if (earliest == -1)
                        return Z80_IGNORE_INT;

                /* otherwise, return the IRQ and clear it */
                i = ctc.irq[earliest].irq;
                ctc.irq[earliest].irq = 0;
                return i;
        }




}
