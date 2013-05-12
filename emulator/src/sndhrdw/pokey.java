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


package sndhrdw;

import static arcadeflex.libc.*;
import static sndhrdw.pokeyH.*;

public class pokey 
{
    static char[] rng=new char[MAXPOKEYS];		/* Determines if the random number generator is */
                                                        /* generating new random values or returning the */
                                                        /* same value */
    
    static void Pokey_sound_init (int freq17, int playback_freq, int num_pokeys)
    {
           int chip;
           for (chip = 0; chip < MAXPOKEYS; chip++)
           {
              //AUDCTL[chip] = 0;
              //Base_mult[chip] = DIV_64;
              /* Enable the random number generator */
              rng[chip] = 1;
           }
    }
    static void Update_pokey_sound (int addr, int val, int chip, int gain)
    {
        switch (addr & 0x0f)
        {
            case SKCTL_C:
		  if ((val & 0x03)!=0)
		     rng[chip] = 1;
		  else rng[chip] = 0;
		  break;
            default:
                System.out.println(addr & 0x0f);
                break;
        }
    }
    static char[] random=new char[MAXPOKEYS];
    static int Read_pokey_regs (int addr, int chip)
    {
        

        switch (addr & 0x0f)
        {
            /* Currently only the random number generator value is emulated */
                    case  RANDOM_C:
                            /* If the random number generator is enabled, get a new random number */
                            if (rng[chip]==1) 
                            {
                                    random[chip] = (char)(rand() & 0xFF);
                                   // System.out.println("POKEY RANDOM " + random[chip]);
                            } 
                            return random[chip];
                           // break;
                    default:
                            System.out.println("POKEY UNKNOWN");
    /*#ifdef MAME_DEBUG
                            if (errorlog) fprintf (errorlog, "Pokey #%d read from register %02x\n", chip, addr);
    #endif*/
                            return 0;
                            //break;
            }
    }    
}
