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
import static arcadeflex.osdepend.*;
import static mame.driverH.*;
import static mame.mame.*;
import static arcadeflex.libc.*;


public class mooncrst
{

	static final int SOUND_CLOCK = 1536000; /* 1.536 Mhz */
	
	static final int TONE_LENGTH = 2000;
	static final int TONE_PERIOD = 4;
	static final int NOISE_LENGTH = 8000;
	static final int NOISE_RATE = 1000;
	static final int WAVE_AMPLITUDE = 70;
	static final int MAXFREQ  = 220;
        static final int MINFREQ  = 110;
        static final int STEP  = 1;
        static char[] tone;
        static char[] noise;

        static int shootsampleloaded = 0;
        static int deathsampleloaded = 0;
        static int backgroundsampleloaded = 0;
        static int F=2;
        static int t=0;
        static int LastPort1=0;
        static int LastPort2=0;
        static int lfo_rate=0;
        static int freq=MAXFREQ;
        static int lforate0=0;
        static int lforate1=0;
        static int lforate2=0;
        static int lforate3=0;
	
	
public static ShInitPtr mooncrst_sh_init = new ShInitPtr() { public int handler(String gamename)
{
        if (Machine.samples != null && Machine.samples.sample[0] !=null)    /* We should check also that Samplename[0] = 0 */
          shootsampleloaded = 1;
        else
          shootsampleloaded = 0;

        if (Machine.samples != null && Machine.samples.sample[1] != null)    /* We should check also that Samplename[0] = 0 */
          deathsampleloaded = 1;
        else
          deathsampleloaded = 0;

        if (Machine.samples != null && Machine.samples.sample[2] != null)    /* We should check also that Samplename[0] = 0 */
          backgroundsampleloaded = 1;
        else
          backgroundsampleloaded = 0;

        return 0;

}};
	
public static WriteHandlerPtr mooncrst_sound_freq_w = new WriteHandlerPtr() { public void handler(int offset, int data)
{


	if ((data!=0) && data != 0xff) osd_adjust_sample(0,(SOUND_CLOCK/16)/(256-data)*32*F,128);
	else osd_adjust_sample(0,1000,0);
} };
	
	
public static WriteHandlerPtr mooncrst_noise_w = new WriteHandlerPtr() { public void handler(int offset, int data)
{
        if (deathsampleloaded!=0)
        {
           if (((data & 1)!=0) && ((LastPort1 & 1)==0))
              osd_play_sample(1,Machine.samples.sample[1].data,
                           Machine.samples.sample[1].length,
                           Machine.samples.sample[1].smpfreq,
                           Machine.samples.sample[1].volume,0);
           LastPort1=data;
        }
        else
        {
  	  if ((data & 1)!=0) osd_adjust_sample(1,NOISE_RATE,255);
	  else osd_adjust_sample(1,NOISE_RATE,0);
        }
} };
static int playBackground = 0;
public static WriteHandlerPtr mooncrst_background_w = new WriteHandlerPtr() { public void handler(int offset, int data)
{

        if (backgroundsampleloaded!=0)
        {
          if ((data & 1)!=0)
          {
             if (playBackground==0)
             {
                osd_play_sample(3,Machine.samples.sample[2].data,
                               Machine.samples.sample[2].length,
                               Machine.samples.sample[2].smpfreq,
                               Machine.samples.sample[2].volume,1);
                playBackground = 1;
             }
          }
          else
          {
             playBackground = 0;
             osd_stop_sample(3);
          }

        }

} };
public static WriteHandlerPtr mooncrst_shoot_w = new WriteHandlerPtr() { public void handler(int offset, int data)
{


      if (((data & 1)!=0) && ((LastPort2 & 1)==0) && (shootsampleloaded!=0))
         osd_play_sample(2,Machine.samples.sample[0].data,
                           Machine.samples.sample[0].length,
                           Machine.samples.sample[0].smpfreq,
                           Machine.samples.sample[0].volume,0);
      LastPort2=data;
} };

public static ShStartPtr mooncrst_sh_start = new ShStartPtr() { public int handler()
{
	int i;


	if ((tone = new char[TONE_LENGTH]) == null)
		return 1;
	if ((noise = new char[NOISE_LENGTH]) == null)
	{
		tone=null;
		return 1;
	}

	for (i = 0;i < NOISE_LENGTH;i++)
		noise[i] = (char)((rand() % (2*WAVE_AMPLITUDE)) - WAVE_AMPLITUDE);
	for (i = 0;i < TONE_LENGTH;i++)
		tone[i] = (char)(WAVE_AMPLITUDE * Math.sin(2*Math.PI*i/TONE_PERIOD));

        osd_play_sample(0, Machine.drv.samples,32,1000,0,1);
        if (deathsampleloaded==0)
   	    osd_play_sample(1,noise,NOISE_LENGTH,NOISE_RATE,0,1);
        if (backgroundsampleloaded==0)
            osd_play_sample(3, Machine.drv.samples,32,32,1000,0,1);
	return 0;
} };



public static ShStopPtr mooncrst_sh_stop = new ShStopPtr() { public void handler()
{
    tone=null;
    noise=null;
    osd_stop_sample(0);
    osd_stop_sample(1);
    osd_stop_sample(2);
    osd_stop_sample(3);

} };
public static WriteHandlerPtr mooncrst_sound_freq_sel_w = new WriteHandlerPtr() { public void handler(int offset, int data)
{
        if (offset==1 && ((data & 1)!=0))
            F=1;
        else
            F=2;
 } };
public static WriteHandlerPtr mooncrst_lfo_freq_w = new WriteHandlerPtr() { public void handler(int offset, int data)
{
        if (offset==3) lforate3=(data & 1);
        if (offset==2) lforate2=(data & 1);
        if (offset==1) lforate1=(data & 1);
        if (offset==0) lforate0=(data & 1);
        lfo_rate=lforate3*8+lforate2*4+lforate1*2+lforate0;
        lfo_rate=16-lfo_rate;
} };
	
public static ShUpdatePtr mooncrst_sh_update = new ShUpdatePtr() { public void handler()
{
  if (backgroundsampleloaded==0)
  {
    if (lfo_rate!=1)
    {
      osd_adjust_sample(3,freq*32,64);
      if (t==0)
         freq-=lfo_rate;
      if (freq<=MINFREQ)
         freq=MAXFREQ;
    }
    else
    {
      osd_adjust_sample(3,1000,0);
    }
    t++;
    if (t==3) t=0;
   }
} };


}








