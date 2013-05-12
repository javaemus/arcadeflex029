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
 * ported to v0.28
 * 
 */
package machine;


public class z80fmlyH {
    static final int MAX_IRQ=20;
    public static class PendIRQ
    {
        long time;//unsigned
        int irq;
    }
    public static class Z80CTC
    {
        public Z80CTC()
        {
            vector=0;
            sys_clk=0;
            mode = new int[4];
            tconst=new int[4];
            down=new int[4];
            fall=new long[4];//unsigned
            last=new long[4];//unsigned
            irq=new PendIRQ[MAX_IRQ];
            for(int i=0; i<MAX_IRQ; i++)
            {
                irq[i]=new PendIRQ();
            }
            
        }
        int vector;					/* interrupt vector */
	int sys_clk;				/* system clock */
	int[] mode;				/* current mode */
	int[] tconst;				/* time constant * 256 */
	int[] down;				/* down counter * 256 */
        long[] fall;	/* time of the next falling edge */
	long[] last;	/* time of the last update */
	PendIRQ[] irq;	/* pending IRQ's */
    }
    
}
