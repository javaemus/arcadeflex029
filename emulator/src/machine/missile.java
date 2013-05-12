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

import static arcadeflex.osdepend.*;
import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static mame.cpuintrf.*;
import static mame.mame.*;
import static sndhrdw.pokeyintf.*;
import static vidhrdw.missile.*;

public class missile {
     static int ctrld;
     static int h_pos, v_pos;  
     
     static final int KEYMOVE=2;
     public static ReadHandlerPtr missile_read_trackball = new ReadHandlerPtr(){
         public int handler(int offset) 
         {

            int xdelta, ydelta;


    /* 	mouse support */
            xdelta = readtrakport(0);
            ydelta = readtrakport(1);

            h_pos += xdelta;
            v_pos -= ydelta;

    /* 	keyboard & joystick support	 */
            if(osd_key_pressed(OSD_KEY_UP) /*|| osd_joy_pressed(OSD_JOY_UP)*/)
                    v_pos += KEYMOVE;
            if(osd_key_pressed(OSD_KEY_DOWN)/* || osd_joy_pressed(OSD_JOY_DOWN)*/)
                    v_pos -= KEYMOVE;
            if(osd_key_pressed(OSD_KEY_LEFT) /*|| osd_joy_pressed(OSD_JOY_LEFT)*/)
                    h_pos -= KEYMOVE;
            if(osd_key_pressed(OSD_KEY_RIGHT) /*|| osd_joy_pressed(OSD_JOY_RIGHT)*/)
                    h_pos += KEYMOVE;


            return( ((v_pos << 4) & 0xF0)  |  (h_pos & 0x0F));
    }};

        static final int MAXMOVE=7; 
    public static ConversionPtr missile_trakball_r = new ConversionPtr() {
         public int handler(int data) 
         {
            data = data >> 1;
            if(data > MAXMOVE)
                    data = MAXMOVE;
            else if(data < -MAXMOVE)
                    data = -MAXMOVE;
            return data;
    }};



    /********************************************************************************************/
    public static WriteHandlerPtr missile_4800_w = new WriteHandlerPtr() { public void handler(int offset, int data) {
            ctrld = data & 1;
    }};



    /********************************************************************************************/
    public static ReadHandlerPtr missile_4008_r = new ReadHandlerPtr() {
         public int handler(int offset) 
    {
            return(readinputport(3));
    }};



    /********************************************************************************************/
    public static ReadHandlerPtr missile_4800_r = new ReadHandlerPtr() {
         public int handler(int offset){
            if(ctrld!=0)
                    return(missile_read_trackball.handler(offset));
            else
                    return (readinputport(0));
    }};


    /********************************************************************************************/
    public static ReadHandlerPtr missile_4900_r = new ReadHandlerPtr() {
         public int handler(int offset)
    {
            return (readinputport(1));
    }};

    /********************************************************************************************/
    public static ReadHandlerPtr missile_4A00_r = new ReadHandlerPtr() {
         public int handler(int offset)
    {
            return (readinputport(2));
    }};



    /********************************************************************************************/
    public static InitMachinePtr missile_init_machine = new InitMachinePtr() {	public void handler()
    {
            h_pos = v_pos = 0;
    }};


    /********************************************************************************************/
    public static WriteHandlerPtr missile_w = new WriteHandlerPtr() { public void handler(int address, int data) {

            int pc, opcode;


            pc = cpu_getpreviouspc();
            opcode = RAM[pc];

    /* 	3 different ways to write to video ram... 		 */

            if((opcode == 0x81) && address >= 0x640){
                    /* 	STA ($00,X) */
                    missile_video_w.handler(address, data);
                    return;
            }

            if(address >= 0x401 && address <= 0x5FF){
                    missile_video_3rd_bit_w.handler(address, data);
                    return;
            }

            if(address >= 0x640 && address <= 0x3FFF){
                            missile_video_mult_w.handler(address, data);
                            return;
            }




            if(address == 0x4800){
                    ctrld = data & 1;
                    return;
            }

            if(address >= 0x4000 && address <= 0x400F){
                    pokey1_w.handler(address, data);
                    return;
            }

            if(address >= 0x4B00 && address <= 0x4B07){
                    RAM[address] = (char)((data & 0x0E) >> 1);
                    return;
            }

            RAM[address] = (char)data;
    }};



    /********************************************************************************************/
    public static ReadHandlerPtr missile_r = new ReadHandlerPtr() {
         public int handler(int address)
    {
            int pc, opcode;


            pc = cpu_getpreviouspc();
            opcode = RAM[pc];


            if((opcode == 0xA1) && (address >=0x1900 && address <= 0xFFF9)){
            /* 		LDA ($00,X)  */
                    return(missile_video_r.handler(address));
            }

            if(address == 0x4008)
                    return(missile_4008_r.handler(0));
            if ((address >= 0x4000) && (address <= 0x400f))
                    return(pokey1_r.handler(address & 0x0f));
            if(address == 0x4800)
                    return(missile_4800_r.handler(0));
            if(address == 0x4900)
                    return(missile_4900_r.handler(0));
            if(address == 0x4A00)
                    return(missile_4A00_r.handler(0));

    /* 	if(address >= 0xFFF9 && address <= 0xFFFF) */
    /* 		return(ROM[address]); */

            return(RAM[address]);
    }};
}
