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
 * ported to v0.27
 *
 */
package sndhrdw;

public class sn76496H
{

        public static class SN76496
        {
          public SN76496()
          {
            Clock=0;
            Volume = new int[4];
            Frequency=new int[3];
            NoiseShiftRate=0;
            Register=new int[8];
            LastRegister=0;
            Counter=new int[4];
            NoiseGen=0;
          }

    	  /* set this before calling SN76496Reset() */
	  int Clock;	/* chip clock in Hz */

	  /* read these after calling SN76496Update(), and produce the required sounds */
	  int[] Volume;	/* volume of voice 0-2 and noise. Range is 0-255 */
	  int[] Frequency;	/* tone frequency in Hz */
	  int NoiseShiftRate;	/* sample rate for the noise sample */

	  /* private */
 	  int[] Register;
	  int LastRegister;
	  /* buffer mode */
	  int[] Counter;
	  int NoiseGen;
        };
}
