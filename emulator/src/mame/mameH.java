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

package mame;

import static mame.commonH.*;
import static mame.driverH.*;
import static mame.osdependH.*;

public class mameH
{
	public static final int MAX_GFX_ELEMENTS = 10;
	public static final int MAX_MEMORY_REGIONS = 10;
        public static final int MAX_PENS=256;	/* can't handle more than 256 colors on screen */

	public static class RunningMachine
	{
		public RunningMachine() {};

		public char memory_region[][] = new char[MAX_MEMORY_REGIONS][];
		public osd_bitmap scrbitmap;	/* bitmap to draw into */
                public GfxElement gfx[] = new GfxElement[MAX_GFX_ELEMENTS];	/* graphic sets (chars, sprites) */
		public GfxElement uifont;   /* font used by DisplayText() */
                public char[] pens = new char[MAX_PENS];/* remapped palette pen numbers. When you write */
								/* directly to a bitmap never use absolute values, */
								/* use this array to get the pen number. For example, */
								/* if you want to use color #6 in the palette, use */
								/* pens[6] instead of just 6. */

		public GameDriver gamedrv;	/* contains the definition of the game machine */
		public MachineDriver drv;	/* same as gamedrv->drv */
		public GameSamples samples;	/* samples loaded from disk */
                public NewInputPort[] input_ports;	/* the input ports definition from the driver */
								/* is copied here and modified (load settings from disk, */
								/* remove cheat commands, and so on) */
                public int orientation;	/* see #defines in driver.h */
                

	};
}
