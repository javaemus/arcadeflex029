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

package machine;
import static arcadeflex.libc.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;

public class bublbobl {
       public static CharPtr bublbobl_sharedram = new CharPtr();

       public static ReadHandlerPtr  bublbobl_sharedram_r= new ReadHandlerPtr() { public int handler(int offset)
        {
            return bublbobl_sharedram.read(offset);
        }};
       	public static WriteHandlerPtr bublbobl_sharedram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
            bublbobl_sharedram.write(offset, data);
        }};
       	public static WriteHandlerPtr bublbobl_bankswitch_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
            //ROM = RAM = Machine.memory_region[Machine.drv.cpu[0].memory_region][0x10000*(data & 3)];
            char[] temp = new char[RAM.length];
            int offset1 = 0x10000*(data & 3);
            int sizez = RAM.length - 0x10000*(data & 3);
           // for(int i=0; i<sizez; i++)
            //    temp[i]=Machine.memory_region[Machine.drv.cpu[0].memory_region][i+offset1];
                
            //ROM=RAM=temp;
             
            System.arraycopy(Machine.memory_region[Machine.drv.cpu[0].memory_region], offset1, temp, 0, sizez);
            ROM=RAM=temp;
        }};
        public static InterruptPtr bublbobl_interrupt = new InterruptPtr() { public int handler()
	{
            bublbobl_sharedram.write(0x3c85,0x37);
            return 0x2e;
        }};
    	public static InitMachinePtr boblbobl_init = new InitMachinePtr() {	public void handler()
	{
                MOD_PAGE(3,0x9a71,0x00); MOD_PAGE(3,0x9a72,0x00); MOD_PAGE(3,0x9a73,0x00);
                MOD_PAGE(3,0xa4af,0x00); MOD_PAGE(3,0xa4b0,0x00); MOD_PAGE(3,0xa4b1,0x00);
                MOD_PAGE(3,0xa55d,0x00); MOD_PAGE(3,0xa55e,0x00); MOD_PAGE(3,0xa55f,0x00);
                MOD_PAGE(3,0xb561,0x00); MOD_PAGE(3,0xb562,0x00); MOD_PAGE(3,0xb563,0x00);

               // for(int j=0; j<0x4000; j++)
		//	ROM[j+0x08000] = ROM[j+0x10000];
                
                /* ease up on the RAM check */
                MODIFY_ROM(0x00c5, 1); MODIFY_ROM(0x00c6, 0);
                MODIFY_ROM(0x00f5, 2); MODIFY_ROM(0x00f6, 0);
                MODIFY_ROM(0x00fb, 0); MODIFY_ROM(0x00fc, 0); MODIFY_ROM(0x00fc, 0);
                MODIFY_ROM(0x0102, 0); MODIFY_ROM(0x0103, 0); MODIFY_ROM(0x0104, 0);
                MODIFY_ROM(0x0108, 0); MODIFY_ROM(0x0109, 0); MODIFY_ROM(0x010a, 0);
    
        }};
        public static void MODIFY_ROM(int addr,int val)
        {
                Machine.memory_region[0][addr] = (char)val;
                Machine.memory_region[0][addr+0x10000] = (char)val;
                Machine.memory_region[0][addr+0x20000] = (char)val;
                Machine.memory_region[0][addr+0x30000] = (char)val;
        }
        public static void MOD_PAGE(int page,int addr,int data)
        {
            Machine.memory_region[0][addr+0x10000*page] = (char)data;
            //Machine.memory_region[0][addr-0x8000+0x10000+0x4000*page] = (char)data;
        }
        public static void HALT_AT(int addr)
        {
            MODIFY_ROM(addr, 0x76);
        }
        public static InitMachinePtr bublbobl_init = new InitMachinePtr() {	public void handler()
	{
                /* speed up the 'wonderful journey' message */
               // MODIFY_ROM_WORD(0x25F6, WONDERFUL_JOURNEY_TIME);

                /* remove protection */
                MODIFY_ROM(0x0533,0xc9);
                MODIFY_ROM(0x0b20,0xc9);
                MODIFY_ROM(0x0b48,0x18);
                MODIFY_ROM(0x0b8b,0x18);
                MODIFY_ROM(0x0d4e,0xc9);
                MODIFY_ROM(0x13c2,0xc9);
                MODIFY_ROM(0x1b07,0x18);
                MODIFY_ROM(0x1d7e,0x18);
                MODIFY_ROM(0x1db1,0x18);
                MODIFY_ROM(0x1ecf,0x18);
                MODIFY_ROM(0x24eb,0xc9);
                MODIFY_ROM(0x313f,0xc9);
                MODIFY_ROM(0x3349,0xc9);
                MODIFY_ROM(0x3447,0xc9);
                MODIFY_ROM(0x37A9,0x18);
                MODIFY_ROM(0x39D5,0x18);
                MODIFY_ROM(0x3E58,0x18);
                MODIFY_ROM(0x40E9,0x18);
                MODIFY_ROM(0x4245,0xc9);
                MODIFY_ROM(0x4866,0xc9);
                MODIFY_ROM(0x4B93,0x18);
                MODIFY_ROM(0x4F50,0xc9);
                MODIFY_ROM(0x4F84,0x18);
                MODIFY_ROM(0x54A1,0x00);
                MODIFY_ROM(0x54A2,0x00);
                MODIFY_ROM(0x58ED,0x18);
                MODIFY_ROM(0x5955,0x18);
                MODIFY_ROM(0x5C26,0x18);
                MODIFY_ROM(0x5D0E,0x18);
                MODIFY_ROM(0x60A0,0xc9);
                MODIFY_ROM(0x660A,0xc9);
                MODIFY_ROM(0x6612,0x18);
                MODIFY_ROM(0x6802,0x18);
                MODIFY_ROM(0x7A31,0x18);
                MODIFY_ROM(0x7C07,0x18);
                MOD_PAGE(0,0x8291,0x18);
                MOD_PAGE(0,0x8498,0x18);
                MOD_PAGE(0,0x85C4,0x18);
                MOD_PAGE(0,0x8F58,0x18);
                MOD_PAGE(0,0x927B,0xc9);
                MOD_PAGE(0,0x92FB,0x18);
                MOD_PAGE(0,0x969F,0x18);
                MOD_PAGE(0,0x98EF,0x18);
                MOD_PAGE(0,0x9DD0,0xc9);
                MOD_PAGE(0,0x9E3D,0x18);
                MOD_PAGE(0,0xA15A,0x18);
                MOD_PAGE(0,0xA609,0x18);
                MOD_PAGE(0,0xB154,0x18);
                MOD_PAGE(0,0xB937,0xc9);
                MOD_PAGE(0,0xBC1F,0x18);
                MOD_PAGE(2,0x84F1,0x18);
                MOD_PAGE(2,0x85E9,0xc9);
                MOD_PAGE(2,0x87A0,0x18);
                MOD_PAGE(2,0x89D4,0x18);
                MOD_PAGE(2,0x8BA5,0xc9);
                MOD_PAGE(2,0x8DA1,0xc9);
                MOD_PAGE(2,0x8F51,0xc9);
                MOD_PAGE(2,0x90DB,0xc9);
                MOD_PAGE(2,0x9367,0xc9);
                MOD_PAGE(2,0x96E4,0xc9);
                MOD_PAGE(2,0xA2FA,0xc9);
                MOD_PAGE(2,0xA81D,0xc9);
                MOD_PAGE(2,0xB4EF,0xc9);
                MOD_PAGE(2,0xBB0D,0xc9);

                /* ease up on the RAM check */
               MODIFY_ROM(0x00c5, 1); MODIFY_ROM(0x00c6, 0);
                MODIFY_ROM(0x00f5, 2); MODIFY_ROM(0x00f6, 0);
                MODIFY_ROM(0x00fb, 0); MODIFY_ROM(0x00fc, 0); MODIFY_ROM(0x00fc, 0);
                MODIFY_ROM(0x0102, 0); MODIFY_ROM(0x0103, 0); MODIFY_ROM(0x0104, 0);
                MODIFY_ROM(0x0109, 0); MODIFY_ROM(0x010a, 0); MODIFY_ROM(0x010b, 0);
                MODIFY_ROM(0x010f, 0); MODIFY_ROM(0x0110, 0); MODIFY_ROM(0x0111, 0);

                HALT_AT(0x0534); HALT_AT(0x0B21); HALT_AT(0x0B4A); HALT_AT(0x0B8D);
                HALT_AT(0x0D4F); HALT_AT(0x13C3); HALT_AT(0x1B09); HALT_AT(0x1D80);
                HALT_AT(0x1DB3); HALT_AT(0x1ED1); HALT_AT(0x24EC); HALT_AT(0x3140);
                HALT_AT(0x334A); HALT_AT(0x3448); HALT_AT(0x37AB); HALT_AT(0x39D7);
                HALT_AT(0x3E5A); HALT_AT(0x40EB); HALT_AT(0x4246); HALT_AT(0x4867);
                HALT_AT(0x4B95); HALT_AT(0x4F51); HALT_AT(0x4F89); HALT_AT(0x54A1);
                HALT_AT(0x58EF); HALT_AT(0x5958); HALT_AT(0x5C28); HALT_AT(0x5D10);
                HALT_AT(0x60A1); HALT_AT(0x660A); HALT_AT(0x6614); HALT_AT(0x6804);
                HALT_AT(0x7A33); HALT_AT(0x7C09);
                MOD_PAGE(0, 0x8293, 0x76); MOD_PAGE(0, 0x849A, 0x76);
                MOD_PAGE(0, 0x85C6, 0x76); MOD_PAGE(0, 0x8F5A, 0x76);
                MOD_PAGE(0, 0x927C, 0x76); MOD_PAGE(0, 0x92FD, 0x76);
                MOD_PAGE(0, 0x96A1, 0x76); MOD_PAGE(0, 0x98F1, 0x76);
                MOD_PAGE(0, 0x9DD1, 0x76); MOD_PAGE(0, 0x9E3F, 0x76);
                MOD_PAGE(0, 0xA15C, 0x76); MOD_PAGE(0, 0xA60B, 0x76);
                MOD_PAGE(0, 0xB156, 0x76); MOD_PAGE(0, 0xB938, 0x76);
                MOD_PAGE(0, 0xBC21, 0x76);
                MOD_PAGE(2, 0x84F3, 0x76); MOD_PAGE(2, 0x85EA, 0x76);
                MOD_PAGE(2, 0x87A4, 0x76); MOD_PAGE(2, 0x89D6, 0x76);
                MOD_PAGE(2, 0x8BA6, 0x76); MOD_PAGE(2, 0x8DA2, 0x76);
                MOD_PAGE(2, 0x8F52, 0x76); MOD_PAGE(2, 0x90DC, 0x76);
                MOD_PAGE(2, 0x9368, 0x76); MOD_PAGE(2, 0x96E5, 0x76);
                MOD_PAGE(2, 0xA2FB, 0x76); MOD_PAGE(2, 0xA81E, 0x76);
                MOD_PAGE(2, 0xB4F0, 0x76); MOD_PAGE(2, 0xBB0E, 0x76);
        }};
}
