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
 */


package sndhrdw;

import static mame.driverH.*;


public class carnival {

static int SoundFX;
static char Sound = 0;
	public static WriteHandlerPtr carnival_sh_port1_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
               data = ~data;//TODO support external samples
               //no need to implement this since we don't support samples

               /* if (errorlog) fprintf(errorlog,"port 0 : %02x and %02x\n",data,SoundFX);

                if (data & 0x01 && ~Sound & 0x01 && Machine->samples->sample[0])
                        osd_play_sample(0,Machine->samples->sample[0]->data,
                                                  Machine->samples->sample[0]->length,
                                  Machine->samples->sample[0]->smpfreq,
                                  Machine->samples->sample[0]->volume,0);

                if (data & 0x02 && ~Sound & 0x02)
                {
                if (SoundFX & 0x04 && Machine->samples->sample[1])
                            osd_play_sample(1,Machine->samples->sample[1]->data,
                                                      Machine->samples->sample[1]->length,
                                      Machine->samples->sample[1]->smpfreq,
                                      Machine->samples->sample[1]->volume,0);
                else
                    if (Machine->samples->sample[9])
                                osd_play_sample(1,Machine->samples->sample[9]->data,
                                                          Machine->samples->sample[9]->length,
                                          Machine->samples->sample[9]->smpfreq,
                                          Machine->samples->sample[9]->volume,0);
            }

                if (data & 0x04 && ~Sound & 0x04 && Machine->samples->sample[2])
                        osd_play_sample(2,Machine->samples->sample[2]->data,
                                                  Machine->samples->sample[2]->length,
                                  Machine->samples->sample[2]->smpfreq,
                                  Machine->samples->sample[2]->volume,1);

                if (~data & 0x04 && Sound & 0x04)
                        osd_stop_sample(2);


                if (data & 0x08 && ~Sound & 0x08 && Machine->samples->sample[3])
                        osd_play_sample(3,Machine->samples->sample[3]->data,
                                                  Machine->samples->sample[3]->length,
                                  Machine->samples->sample[3]->smpfreq,
                                  Machine->samples->sample[3]->volume,1);

                if (~data & 0x08 && Sound & 0x08)
                        osd_stop_sample(3);


                if (data & 0x10 && ~Sound & 0x10 && Machine->samples->sample[4])
                        osd_play_sample(4,Machine->samples->sample[4]->data,
                                                  Machine->samples->sample[4]->length,
                                  Machine->samples->sample[4]->smpfreq,
                                  Machine->samples->sample[4]->volume,1);

                if (~data & 0x10 && Sound & 0x10)
                        osd_stop_sample(4);


                if (data & 0x20 && ~Sound & 0x20 && Machine->samples->sample[5])
                        osd_play_sample(5,Machine->samples->sample[5]->data,
                                                  Machine->samples->sample[5]->length,
                                  Machine->samples->sample[5]->smpfreq,
                                  Machine->samples->sample[5]->volume,0);

                if (data & 0x40 && ~Sound & 0x40 && Machine->samples->sample[6])
                        osd_play_sample(6,Machine->samples->sample[6]->data,
                                                  Machine->samples->sample[6]->length,
                                  Machine->samples->sample[6]->smpfreq,
                                  Machine->samples->sample[6]->volume,0);

                if (data & 0x80 && ~Sound & 0x80 && Machine->samples->sample[7])
                        osd_play_sample(7,Machine->samples->sample[7]->data,
                                                          Machine->samples->sample[7]->length,
                                          Machine->samples->sample[7]->smpfreq,
                                          Machine->samples->sample[7]->volume,0);
                */
                Sound = (char)data;
        }};

	public static WriteHandlerPtr carnival_sh_port2_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
            SoundFX = (char)data;
        }};

	public static ShUpdatePtr carnival_sh_update = new ShUpdatePtr() { public void handler()
	{
	} };

}

