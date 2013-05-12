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


import static mame.driverH.*;

public class panic
{
	static int count=0;
	public static InterruptPtr panic_interrupt = new InterruptPtr() { public int handler()
	{
		int ans=0;

		count++;

   		/* Apparently, only the first 2 ever get called on the real thing! */

		switch(count)
		{
		case 1: ans = 0x00cf;		/* RST 08h */
	    	break;

                 case 2: ans = 0x00d7;		/* RST 10h */

                 count=0;
                 break;
	    }
    	return ans;
	} };
}

