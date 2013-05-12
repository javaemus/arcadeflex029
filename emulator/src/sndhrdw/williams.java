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
package sndhrdw;

import static mame.driverH.*;
import static mame.mame.*;

public class williams
{
  public static WriteHandlerPtr williams_sh_w = new WriteHandlerPtr()
  {
    public void handler(int offset, int data)
    {
      if ((Machine.samples == null) ) return;
   /*            if(fx == 49)  //TODO if we ever add external sample support
          if(Snd49Flag == 1)
           return;

          if(fx == 0x3F){
           if(Snd49Flag == 1)
            return;
            osd_stop_sample(0);
          }else{
                 if (Machine->samples->sample[fx] != 0){
            osd_play_sample(0,Machine->samples->sample[fx]->data,
             Machine->samples->sample[fx]->length,
             Machine->samples->sample[fx]->smpfreq,
             Machine->samples->sample[fx]->volume,0);
            if(fx == 49)
              Snd49Flag = 1;
            else
              Snd49Flag = 0;
           }
          }*/
    }
  };

  public static ShUpdatePtr williams_sh_update = new ShUpdatePtr() {
    public void handler() {  } } ;
}
