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
 *  THIS file is not done....
 */

package sndhrdw;
import static arcadeflex.libc.*;
import static arcadeflex.osdepend.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.commonH.*;
import static sndhrdw.adpcm.*;


public class kungfum {
    static int SmpOffsetTable[] = { 128, 128, 3006, 4800, 6000, 22466, 47538, 51500, 59360, -1 };

  public static ShInitPtr kungfum_sh_init = new ShInitPtr(){public int handler(String gamename){
        int i;
	GameSamples samples;

	i = 0;
	while (SmpOffsetTable[i+1] != -1) i++;

	if ((samples = new GameSamples()) == null)
		return 0;

	samples.total = i;
        samples.sample = new GameSample[samples.total];
	for (i = 0; i < samples.total; i++)
		samples.sample[i] = null;

	for (i = 0; i < samples.total; i++)
	{
                int smplen;

		if ((smplen=(SmpOffsetTable[i+1]-SmpOffsetTable[i])) > 0)
		{
                    //System.out.println(smplen);
		   if ((samples.sample[i] = new GameSample()) != null)
		   {
                           int j;
                           samples.sample[i].data=new byte[smplen];
			   samples.sample[i].length = smplen;
			   samples.sample[i].volume = 255;
			   samples.sample[i].smpfreq = 8000;   /* standard ADPCM voice freq */
			   samples.sample[i].resolution = 8;
                           InitDecoder();
                           for (j=0; j < smplen; j++)
                           {
                             signalf += ((j%2)!=0 ? DecodeAdpcm((char)(Machine.memory_region[2][(int)((SmpOffsetTable[i]+j)) >> 2] & 0x0f))
                                               : DecodeAdpcm((char)(Machine.memory_region[2][(int)((SmpOffsetTable[i]+j)) >> 2] >> 4)));
                             if (signalf > 2047) signalf = 2047;
                             if (signalf < -2047) signalf = -2047;
                             //System.out.println(j + "="+ signalf);
                             //fprintf(errorlog,"%d =sig %d\n",j,signalf);
                             samples.sample[i].data[j] = (byte)((signalf / 16));     /* for 16-bit samples multiply by 16 */
                             //System.out.println(j + "="+(samples.sample[i].data[j]));
                             //fprintf(errorlog,"%d = %d\n",j,samples.sample[i].data[j]);
                           }
		   }
		}
	}

        Machine.samples = samples;

        return 0;
      
  }};

  public static WriteHandlerPtr kungfum_sh_port0_w = new WriteHandlerPtr()
  {
    public void handler(int offset, int data)
    {
      /*  int command;

	if (Machine.samples == null) return;

        command = data;
        data    = data & 0x7f;

        if (command == 0x80)
          osd_stop_sample(7);
        else if ((command > 0x80) && (data < Machine.samples.total) && (Machine.samples.sample[data]!=null))
        {
		osd_play_sample(7,Machine.samples.sample[data].data,
				Machine.samples.sample[data].length,
                                Machine.samples.sample[data].smpfreq,
                                Machine.samples.sample[data].volume,0);
        }*/

    }
  };

  public static ShUpdatePtr kungfum_sh_update = new ShUpdatePtr() 
  {
    public void handler() 
    {  
    } 
  } ;
}
