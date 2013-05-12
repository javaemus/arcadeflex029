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
import static mame.cpuintrf.*;
import static mame.mame.*;
import static mame.inptport.*;
import static Z80.Z80H.*;
import static Z80.Z80.*;
import static sndhrdw._8910intf.*;


public class arabian
{

	static int clock = 0;
        static int port0e=0;
        static int port0f=0;


        public static ReadHandlerPtr arabian_d7f6= new ReadHandlerPtr() { public int handler(int offset)
        {
             int pom;
             pom = ( (clock & 0xf0) >> 4) ;
             return pom;
        } };

        public static ReadHandlerPtr arabian_d7f8= new ReadHandlerPtr() { public int handler(int offset)
        {
            int pom;
            pom = clock & 0x0f ;
            return pom;
        } };

        public static InterruptPtr arabian_interrupt= new InterruptPtr() { public int handler()
        {
            clock = (clock+1) & 0xff;
            return 0;
        } };


       public static ReadHandlerPtr arabian_input_port= new ReadHandlerPtr() { public int handler(int offset)
       {
             int pom;

            if ((port0f & 0x10)!=0)  /* if 1 read the switches */
            {

             switch(offset)
             {
                case 0:
                 pom = readinputport(2);
                 break;
                case 1:
                 pom = readinputport(3);
                 break;
                case 2:
                pom = readinputport(4);
                break;
                case 3:
                pom = readinputport(5);
                break;
                case 4:
                pom = readinputport(6);
                break;
                case 5:
                 pom = readinputport(7);
                 break;
                case 6:
                pom = arabian_d7f6.handler(offset);
                break;
                case 8:
                pom = arabian_d7f8.handler(offset);
                break;
               default:
                pom = RAM[ 0xd7f0 + offset ];
                break;
            }

            }
            else  /* if bit 4 of AY port 0f==0 then read RAM memory instead of switches */
            {
                pom = RAM[ 0xd7f0 + offset ];
            }

            return pom;
       } };





        static int lastr;
	public static WriteHandlerPtr moja = new WriteHandlerPtr() { public void handler(int port, int val)
	{

		Z80_Regs regs = new Z80_Regs();
		Z80_GetRegs(regs);

		if (regs.BC.W == 0xc800)
		{
			AY8910_control_port_0_w.handler(port, val);
			lastr = val;
		}
		else
		{
			if ( (lastr == 0x0e) || (lastr == 0x0f) )
			{
                           if (lastr==0x0e)
                             port0e=val;
                           if (lastr==0x0f)
                             port0f=val;
			}
			else
			   AY8910_write_port_0_w.handler(port, val);
		}

	} };
}

