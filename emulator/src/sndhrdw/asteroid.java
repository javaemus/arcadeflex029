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
 *   since we don't load samples implementing this is useless atm...
 */

package sndhrdw;

import static mame.driverH.*;
import static mame.mame.*;

public class asteroid 
{
    /* Constants for the sound names in the sample array */
/*#define kHighThump		0
#define kLowThump		1
#define kFire			2
#define kLargeSaucer	3
#define kSmallSaucer	4
#define kThrust			5
#define kSaucerFire		6
#define kLife			7
#define kExplode1		8
#define kExplode2		9
#define kExplode3		10*/
    
        public static WriteHandlerPtr asteroid_explode_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{

              /*  static int explosion = -1;
                int explosion2;
                int sound = -1;*/

                if (Machine.samples == null) return;

               /* if (data & 0x3c) {
                        if (errorlog) fprintf (errorlog, "data: %02x, old explosion %02x\n",data, explosion);
                        explosion2 = data >> 6;
                        if (explosion2 != explosion) {
                                osd_stop_sample (6);
                                switch (explosion2) {
                                        case 0:
                                                sound = kExplode1;
                                                break;
                                        case 1:
                                                sound = kExplode1;
                                                break;
                                        case 2:
                                                sound = kExplode2;
                                                break;
                                        case 3:
                                                sound = kExplode3;
                                                break;
                                        }
                                osd_play_sample (6,Machine->samples->sample[sound]->data,
                                        Machine->samples->sample[sound]->length,
                                        Machine->samples->sample[sound]->smpfreq,
                                        Machine->samples->sample[sound]->volume,0);
                                }
                        explosion = explosion2;
                        }
                else explosion = -1;*/
                }};
        public static WriteHandlerPtr asteroid_thump_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{

                if (Machine.samples == null) return;

                /* is the volume bit on? */
            /*    if (data & 0x10) {
                        int sound;

                        if (data & 0x0f)
                                sound = kHighThump;
                        else
                                sound = kLowThump;
                        osd_play_sample (0,Machine->samples->sample[sound]->data,
                                Machine->samples->sample[sound]->length,
                                Machine->samples->sample[sound]->smpfreq,
                                Machine->samples->sample[sound]->volume,0);
                        }*/
                }};
        public static WriteHandlerPtr asteroid_sounds_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
               /* static int fire = 0;
                static int saucer = 0;
                int sound;
                int fire2;*/

                if (Machine.samples == null) return;

      /*          switch (offset) {
                        case 0: /* Saucer sounds */
        /*                        if (data & 0x80) {
                                        if (saucer)
                                                sound = kLargeSaucer;
                                        else
                                                sound = kSmallSaucer;
                                        osd_play_sample (1,Machine->samples->sample[sound]->data,
                                                Machine->samples->sample[sound]->length,
                                                Machine->samples->sample[sound]->smpfreq,
                                                Machine->samples->sample[sound]->volume,0);
                                        }
                                break;
                        case 1: /* Saucer fire */
                /*                if (data & 0x80) {
                                        osd_play_sample (3,Machine->samples->sample[kSaucerFire]->data,
                                                Machine->samples->sample[kSaucerFire]->length,
                                                Machine->samples->sample[kSaucerFire]->smpfreq,
                                                Machine->samples->sample[kSaucerFire]->volume,0);
                                        }
                                break;
                        case 2: /* Saucer sound select */
               /*                 saucer = data & 0x80;
                                break;
                        case 3: /* Player thrust */
                /*                if (data & 0x80) {
                                        osd_play_sample (4,Machine->samples->sample[kThrust]->data,
                                                Machine->samples->sample[kThrust]->length,
                                                Machine->samples->sample[kThrust]->smpfreq,
                                                Machine->samples->sample[kThrust]->volume,0);
                                        }
                                break;
                        case 4: /* Player fire */
                  /*              if (errorlog) fprintf (errorlog, "fire: %02x, old fire %02x\n",data & 0x80, fire);
                                fire2 = data & 0x80;
                                if (fire2 != fire) {
                                        osd_stop_sample (2);
                                        if (fire2)
                                                osd_play_sample (2,Machine->samples->sample[kFire]->data,
                                                        Machine->samples->sample[kFire]->length,
                                                        Machine->samples->sample[kFire]->smpfreq,
                                                        Machine->samples->sample[kFire]->volume,0);
                                        }
                                fire = fire2;
                                break;
                        case 5: /* life sound */
                 /*               if (data & 0x80) {
                                        osd_play_sample (5,Machine->samples->sample[kLife]->data,
                                                Machine->samples->sample[kLife]->length,
                                                Machine->samples->sample[kLife]->smpfreq,
                                                Machine->samples->sample[kLife]->volume,0);
                                        }
                                break;
                        }*/
                }};
	public static ShUpdatePtr asteroid_sh_update = new ShUpdatePtr() { public void handler()
	{
	} };
  
}
