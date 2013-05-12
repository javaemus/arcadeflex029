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
 *
 *   
 */
package sndhrdw;

import static mame.driverH.*;

public class _8910intfH
{
	public static final int MAX_8910 = 5;


	public static class AY8910interface
	{
                public AY8910interface(int nu,int ups, int cl, int []vo, ReadHandlerPtr []pAr, ReadHandlerPtr []pBr, WriteHandlerPtr []pAw, WriteHandlerPtr []pBw)
		{
                    num = nu;
                    updates_per_frame=ups;
                    clock = cl;
                    volume = vo;
                    portAread = pAr;
                    portBread = pBr;
                    portAwrite = pAw;
                    portBwrite = pBw;
                };
		public int num;	/* total number of 8910 in the machine */
                public int updates_per_frame; /* you have to call AY8910_update() this number of */
							/* times each video frame. This is usually done from */
							/* inside the interrupt handler. If you set this to */
							/* 1, you don't HAVE /you may if you want to) to call */
							/* AY8910_update(), it will be called automatically by */
							/* AY8910_sh_update() */
		public int clock;
		public int volume[];
		public ReadHandlerPtr portAread[];
		public ReadHandlerPtr portBread[];
		public WriteHandlerPtr portAwrite[];
		public WriteHandlerPtr portBwrite[];
	};
}