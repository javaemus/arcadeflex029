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
import static arcadeflex.osdepend.*;
import static mame.cpuintrf.*;
import static mame.osdependH.*;
import static mame.driverH.*;
import static sndhrdw.pokeyintf.*;
import static mame.mame.*;
import static mame.inptport.*;



public class warlord 
{
    
static final int PADDLE_SPEED=1;
static final int PADDLE_MIN=0x1D;
static final int PADDLE_MAX=0xCB;


static int paddle1 = 0x80 ;
static int paddle2 = 0x80 ;
static int paddle3 = 0x80 ;
static int paddle4 = 0x80 ;
    public static ReadHandlerPtr warlord_pots = new ReadHandlerPtr() {
         public int handler(int offset) 
         {
            int trak;

      if ( osd_key_pressed(OSD_KEY_Q) && paddle1 >= PADDLE_MIN + PADDLE_SPEED )
        paddle1 -= PADDLE_SPEED ;
      if ( osd_key_pressed(OSD_KEY_W) && paddle1 <= PADDLE_MAX - PADDLE_SPEED )
        paddle1 += PADDLE_SPEED ;

            trak = readtrakport(0);
            if (trak != NO_TRAK) {
                    paddle1 += trak;
                    if (paddle1 < PADDLE_MIN)
                            paddle1 = PADDLE_MIN;
                    else if (paddle1 > PADDLE_MAX)
                            paddle1 = PADDLE_MAX;
                    }

      if ( osd_key_pressed(OSD_KEY_I) && paddle2 >= PADDLE_MIN + PADDLE_SPEED )
        paddle2 -= PADDLE_SPEED ;
      if ( osd_key_pressed(OSD_KEY_O) && paddle2 <= PADDLE_MAX - PADDLE_SPEED )
        paddle2 += PADDLE_SPEED ;

      if ( osd_key_pressed(OSD_KEY_A) && paddle3 >= PADDLE_MIN + PADDLE_SPEED )
        paddle3 -= PADDLE_SPEED ;
       if ( osd_key_pressed(OSD_KEY_S) && paddle3 <= PADDLE_MAX - PADDLE_SPEED )
       paddle3 += PADDLE_SPEED ;

      if ( osd_key_pressed(OSD_KEY_J) && paddle4 >= PADDLE_MIN + PADDLE_SPEED )
        paddle4 -= PADDLE_SPEED ;
      if ( osd_key_pressed(OSD_KEY_K) && paddle4 <= PADDLE_MAX - PADDLE_SPEED )
        paddle4 += PADDLE_SPEED ;


      switch ( offset )
      {
        case 0x00 : return paddle1 ;
        case 0x01 : return paddle2 ;
        case 0x02 : return paddle3 ;
        case 0x03 : return paddle4 ;
        case 0x0A : return pokey1_r.handler(offset);
        default: 
            if (errorlog!=null)
                      fprintf(errorlog,"%02X\n" ,new Object[] { Integer.valueOf(offset)} ) ;
                    return 0 ;
      }
    }};
    public static final int MAXMOVE=32;
    public static ConversionPtr warlord_trakball_r = new ConversionPtr() {
         public int handler(int data) 
         {
            data = data >> 1;
            if(data > MAXMOVE)
                    data = MAXMOVE;
            else if(data < -MAXMOVE)
                    data = -MAXMOVE;
            return data;
    }};
    
}
