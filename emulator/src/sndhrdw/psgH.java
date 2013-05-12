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
 *  TO BE CHECKED!
 */

/*
** $Id:$
**
** File: psg/psg.h -- header file for software implementation of AY-3-8910
**		Programmable sound generator.  This module is used in my
**		GYRUSS emulator.
**
** Based on: Sound.c, (C) Ville Hallik (ville@physic.ut.ee) 1996
**
** SCC emulation removed.  Made modular and capable of multiple PSG
** emulation.
**
** Modifications (C) 1996 Michael Cuddy, Fen's Ende Software.
** http://www.fensende.com/Users/mcuddy
**
*/

package sndhrdw;

public class psgH
{
	/* register id's */
	static final int AY_AFINE	= (0);
	static final int AY_ACOARSE	= (1);
	static final int AY_BFINE	= (2);
	static final int AY_BCOARSE	= (3);
	static final int AY_CFINE	= (4);
	static final int AY_CCOARSE	= (5);
	static final int AY_NOISEPER	= (6);
	static final int AY_ENABLE	= (7);
	static final int AY_AVOL	= (8);
	static final int AY_BVOL	= (9);
	static final int AY_CVOL	= (10);
	static final int AY_EFINE	= (11);
	static final int AY_ECOARSE	= (12);
	static final int AY_ESHAPE	= (13);

	static final int AY_PORTA	= (14);
	static final int AY_PORTB	= (15);



	/* SAMPLE_16BIT is not supported, yet */
	public static final int AUDIO_CONV(int A) { return (A); }	/* use this macro for unsigned samples */

	/*
	** this is a handler for the AY8910's I/O ports -- called when
	** AYWriteReg(AY_PORTA) or AYWriteReg(AY_PORTB) is called.
	*/
	public interface AYPortHandler { public int handler(AY8910 ay, int port, int iswrite, int val); }

	/* here's the virtual AY8910 ... */
	public static class AY8910
	{
		public AY8910() { };
		public char []Buf;	/* sound buffer */
		public int UserBuffer;	/* if user provided buffers */
		public AYPortHandler Port[] = new AYPortHandler[2];	/* 'A' and 'B' port */
		public int Regs[] = new int[16];

		/* state variables */
		public int Incr0, Incr1, Incr2;
		public int Increnv, Incrnoise;
		public int StateNoise, NoiseGen;
		public int Counter0, Counter1, Counter2, Countenv, Countnoise;
		public int Vol0, Vol1, Vol2, Volnoise, Envelope;
	};
}
