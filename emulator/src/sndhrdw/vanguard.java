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
 *   this file has been rewritten from scratch from mame v0.27 source code , so
 *   it should be accurate...
 */

package sndhrdw;

/**
 *
 * @author George
 */
import static arcadeflex.osdepend.*;
import static sndhrdw.generic.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static mame.mame.*;


public class vanguard
{
static int NoSound0=1;
static int Sound0Offset;
static int Sound0Base;
static int oldSound0Base;
static int NoSound1=1;
static int Sound1Offset;
static int Sound1Base;
static int oldSound1Base;
static char LastPort1;
static char LastPort2;

public static ShStartPtr vanguard_sh_start = new ShStartPtr() { public int handler()
{
   NoSound0=1;
   Sound0Offset=0x0000;
   Sound0Base=Sound0Offset;
   oldSound0Base=Sound0Base;
   NoSound1=1;
   Sound1Offset=0x0800;
   Sound1Base=Sound1Offset;
   oldSound1Base=Sound1Base;

      osd_play_sample(0, Machine.drv.samples, 32, 1000, 0, 1);
      osd_play_sample(1, Machine.drv.samples, 32, 1000, 0, 1);

        return 0;
} };
static int count;
public static ShUpdatePtr vanguard_sh_update = new ShUpdatePtr() { public void handler()
{
	/* only update every second call (30 Hz update) */
	count++;
    if ((count & 1)!=0) return;


        /* play musical tones according to tunes stored in ROM */

     if ((NoSound0==0) && Machine.memory_region[2][Sound0Offset]!=0xff)
        osd_adjust_sample(0, (32000 / (256-Machine.memory_region[2][Sound0Offset])) * 16, 128);
     else
        osd_adjust_sample(0, 1000, 0);
     Sound0Offset++;

     if ((NoSound1==0) && Machine.memory_region[2][Sound1Offset]!=0xff)
        osd_adjust_sample(1, (32000 / (256-Machine.memory_region[2][Sound1Offset])) * 16, 128);
     else
        osd_adjust_sample(1, 1000, 0);
     Sound1Offset++;

      // check for overflow
      if (Sound0Offset > Sound0Base + 255)
         Sound0Offset = Sound0Base;

      if (Sound1Offset > Sound1Base + 255)
         Sound1Offset = Sound1Base;
} };
public static WriteHandlerPtr vanguard_sound0_w = new WriteHandlerPtr() { public void handler(int offset, int data)
{

        /* select musical tune in ROM based on sound command byte */

      Sound0Base = ((data & 0x07) << 8);
      if (Sound0Base != oldSound0Base)
         {
         oldSound0Base = Sound0Base;
         Sound0Offset = Sound0Base;
         }


      /* play noise samples requested by sound command byte */
    if ((Machine.samples!=null) && (Machine.samples.sample[0]!=null))
    {
      if (((data & 0x20)!=0) && ((LastPort1 & 0x20)==0))
         osd_play_sample(4,Machine.samples.sample[0].data,
                           Machine.samples.sample[0].length,
                           Machine.samples.sample[0].smpfreq,
                           Machine.samples.sample[0].volume,0);
      else if (((data & 0x20)==0) && ((LastPort1 & 0x20)!=0))
      {
        osd_stop_sample(4);
      }
       if (((data & 0x40)!=0) && ((LastPort1 & 0x40)==0))
         osd_play_sample(2,Machine.samples.sample[0].data,
                           Machine.samples.sample[0].length,
                           Machine.samples.sample[0].smpfreq,
                           Machine.samples.sample[0].volume,0);
      else if (((data & 0x20)==0) && ((LastPort1 & 0x20)!=0))
       {
         osd_stop_sample(2);
       }
        if (((data & 0x80)!=0) && ((LastPort1 & 0x80)==0))
         {
         osd_play_sample(3,Machine.samples.sample[1].data,
                           Machine.samples.sample[1].length,
                           Machine.samples.sample[1].smpfreq,
                           Machine.samples.sample[1].volume,0);
         }
    }

      if ((data & 0x10)!=0)
         {
         NoSound0=0;
         }

      if ((data & 0x08)!=0)
         {
         NoSound0=1;
         }

   LastPort1 = (char)data;
} };
public static WriteHandlerPtr vanguard_sound1_w = new WriteHandlerPtr() { public void handler(int offset, int data)
{

       /* select tune in ROM based on sound command byte */
      Sound1Base = 0x0800 + ((data & 0x07) << 8);
      if (Sound1Base != oldSound1Base)
         {
         oldSound1Base = Sound1Base;
         Sound1Offset = Sound1Base;
         }

      if ((data & 0x08)!=0)
         {
         NoSound1=0;
         }
      else
         {
         NoSound1=1;
         }

        LastPort2 = (char)data;
} };
}

