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
 * ported to v0.27
 *
 *
 *
 */
package machine;

import static arcadeflex.libc.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static m6809.M6809H.*;
import static m6809.M6809.*;
import static mame.inptport.*;
import static mame.mame.*;

public class mappy {
    public static CharPtr mappy_sharedram=new CharPtr();
    public static CharPtr mappy_customio_1=new CharPtr();
    public static CharPtr mappy_customio_2=new CharPtr();

    static int interrupt_enable_1,interrupt_enable_2;
    static int coin, credits, fire1, fire2, start1, start2;

    static int crednum[] = { 1, 2, 3, 6, 1, 3, 1, 2 };
    static int credden[] = { 1, 1, 1, 1, 2, 2, 3, 3 };
    public static InitMachinePtr mappy_init_machine = new InitMachinePtr()
    {
      public void handler() {

            /* Reset all flags */
            credits = coin = fire1 = fire2 = start1 = start2 = 0;

            /* Set optimization flags for M6809 */
            m6809_Flags = M6809_FAST_OP | M6809_FAST_S | M6809_FAST_U;

    }};

    public static ReadHandlerPtr mappy_sharedram_r = new ReadHandlerPtr()
      {
        public int handler(int offset)
        {
            return mappy_sharedram.read(offset);
        }
       };

    public static ReadHandlerPtr mappy_sharedram_r2 = new ReadHandlerPtr()
    {
        public int handler(int offset)
        {
                /* to speed up emulation, we check for the loop the sound CPU sits in most of the time
                   and end the current iteration (things will start going again with the next IRQ) */
                if (offset == 0x010a - 0x40 && mappy_sharedram.read(offset) == 0)
                        cpu_seticount (0);
                return mappy_sharedram.read(offset);
        }
     };

    public static ReadHandlerPtr digdug2_sharedram_r2 = new ReadHandlerPtr()
      {
        public int handler(int offset)
        {
                /* to speed up emulation, we check for the loop the sound CPU sits in most of the time
                   and end the current iteration (things will start going again with the next IRQ) */
                if (offset == 0x0a1 - 0x40 && mappy_sharedram.read(offset) == 0 && cpu_getpc () == 0xe383)
                        cpu_seticount (0);
                return mappy_sharedram.read(offset);
        }
        };

    public static ReadHandlerPtr mappy_cpu1ram_r = new ReadHandlerPtr()
      {
        public int handler(int offset)
        {

            /* to speed up emulation, we check for the loop the main CPU sits in much of the time
               and end the current iteration (things will start going again with the next IRQ) */
            if (offset == 0x1382 && RAM[offset] == 0)
                    cpu_seticount (0);
            return RAM[offset];
        }
        };

        public static ReadHandlerPtr digdug2_cpu1ram_r = new ReadHandlerPtr()
          {
            public int handler(int offset)
            {
            /* to speed up emulation, we check for the loop the main CPU sits in much of the time
               and end the current iteration (things will start going again with the next IRQ) */
            if (offset == 0x1000 && RAM[offset] == 0 && cpu_getpc () == 0x80c4)
                    cpu_seticount (0);
            return RAM[offset];
            }
            };

  public static WriteHandlerPtr mappy_sharedram_w = new WriteHandlerPtr()
  {
    public void handler(int offset, int data)
    {
            mappy_sharedram.write(offset, data);
    }
  };
  public static WriteHandlerPtr mappy_customio_w_1 = new WriteHandlerPtr()
  {
    public void handler(int offset, int data)
    {
            mappy_customio_1.write(offset, data);
    }
    };

  public static WriteHandlerPtr mappy_customio_w_2 = new WriteHandlerPtr()
  {
    public void handler(int offset, int data)
    {
            mappy_customio_2.write(offset, data);
    }
   };
    static int testvals[] = { 8, 4, 6, 14, 13, 9, 13 };
    public static ReadHandlerPtr mappy_customio_r_1 = new ReadHandlerPtr()
      {
        public int handler(int offset)
        {
            
            int val, temp;
            switch (mappy_customio_1.read(8))
            {
                    /* mode 3 is the standard, and returns actual important values */
                    case 1:
                    case 3:
                            switch (offset)
                            {
                                    case 0:
                                            val = readinputport (3) & 0x0f;

                                            /* bit 0 is a trigger for the coin slot */
                                            if ((val & 1)!=0)
                                            {
                                                    if (coin==0)
                                                    {
                                                        ++credits;
                                                        ++coin;
                                                    }

                                                    if (coin != 1) val &= ~1;
                                            }
                                            else coin = 0;
                                            break;

                                    case 1:
                                            temp = readinputport (1) & 7;
                                            val = readinputport (3) >> 4;

                                            /* bit 0 is a trigger for the 1 player start */
                                            if ((val & 1)!=0)
                                            {
                                                    if ((start1==0) && credits >= credden[temp]) credits -= credden[temp]; ++start1;
                                                    if (start1 != 1) val &= ~1;
                                            }
                                            else start1 = 0;

                                            /* bit 1 is a trigger for the 2 player start */
                                            if ((val & 2)!=0)
                                            {
                                                    if ((start2==0) && credits >= 2 * credden[temp]) credits -= 2 * credden[temp]; ++start2;
                                                    if (start2 != 1) val &= ~2;
                                            }
                                            else start2 = 0;
                                            break;

                                    case 2:
                                            temp = readinputport (1) & 7;
                                            val = (credits * crednum[temp] / credden[temp]) / 10;
                                            break;

                                    case 3:
                                            temp = readinputport (1) & 7;
                                            val = (credits * crednum[temp] / credden[temp]) % 10;
                                            break;

                                    case 4:
                                            val = readinputport (2) & 0x0f;
                                            break;

                                    case 5:
                                            val = readinputport (2) >> 4;

                                            /* bit 0 is a trigger for the fire 1 key */
                                            if ((val & 2)!=0)
                                            {
                                                    if (fire1==0) ++fire1;
                                                    if (fire1 == 1) val |= 1;
                                            }
                                            else fire1 = 0;
                                            break;

                                    case 6:
                                    case 7:
                                            val = 0;
                                            break;

                                    default:
                                            val = mappy_customio_1.read(offset);
                                            break;
                            }
                            return val;

                    case 5:
                            credits = 0;
                            if (offset >= 1 && offset <= 7) 
                                    return testvals[offset - 1];
                            break;
            }
            return mappy_customio_1.read(offset);
    }
   };
    static int testvals2[] = { 8, 4, 6, 14, 13, 9, 13 };
    public static ReadHandlerPtr mappy_customio_r_2 = new ReadHandlerPtr()
    {
        public int handler(int offset)
        {
            
            int val;

            switch (mappy_customio_2.read(8))
            {
                    /* mode 4 is the standard, and returns actual important values */
                    case 4:
                            switch (offset)
                            {
                                    case 0:
                                            val = readinputport (1) & 0x0f;
                                            break;

                                    case 1:
                                            val = readinputport (1) >> 4;
                                            break;

                                    case 2:
                                            val = readinputport (0) & 0x0f;
                                            break;

                                    case 3:
                                            val = 0;
                                            break;

                                    case 4:
                                            val = readinputport (0) >> 4;
                                            break;

                                    case 5:
                                            val = readinputport (4) >> 4;

                                            /* bit 0 is a trigger for the fire 2 key (Dig Dug 2 only) */
                                            if ((val & 2)!=0)
                                            {
                                                    if (fire2==0) ++fire2;
                                                    if (fire2 == 1) val |= 1;
                                            }
                                            else fire2 = 0;
                                            break;

                                    case 6:
                                            /* bit 3 = configuration mode (Mappy) */
                                            /* val |= 0x08; */
                                            val = readinputport (4) & 0x0f;
                                            break;

                                    case 7:
                                            /* bit 3 = configuration mode (Dig Dug 2) */
                                            /* val |= 0x08; */
                                            val = 0;
                                            break;

                                    default:
                                            val = mappy_customio_2.read(offset);
                                            break;
                            }
                            return val;

                    case 5:
                            credits = 0;
                            if (offset >= 1 && offset <= 7) 
                                    return testvals2[offset - 1];
            }
            return mappy_customio_2.read(offset);
    }
    };

    public static WriteHandlerPtr mappy_interrupt_enable_1_w = new WriteHandlerPtr()
      {
        public void handler(int offset, int data)
        {
            interrupt_enable_1 = offset;
        }
        };


    public static InterruptPtr mappy_interrupt_1 = new InterruptPtr() {
     public int handler() {
    
            /* clear all the triggered inputs */
            if (start1 == 1) ++start1;
            if (start2 == 1) ++start2;
            if (fire1 == 1) ++fire1;
            if (fire2 == 1) ++fire2;
            if (coin == 1) ++coin;

            if (interrupt_enable_1!=0) return INT_IRQ;
            else return INT_NONE;
        }
     };


    public static WriteHandlerPtr mappy_interrupt_enable_2_w = new WriteHandlerPtr()
      {
        public void handler(int offset, int data)
        {
            interrupt_enable_2 = offset;
        }
       };


    public static InterruptPtr mappy_interrupt_2 = new InterruptPtr() {
     public int handler() {
            if (interrupt_enable_2!=0) return INT_IRQ;
            else return INT_NONE;
        }
     };

    public static WriteHandlerPtr mappy_cpu_enable_w = new WriteHandlerPtr() {
     public void handler(int offset, int data) {
            cpu_halt(1, offset);
        }
     };
}
