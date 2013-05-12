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

import static arcadeflex.libc.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static m6809.M6809.*;
import static m6809.M6809H.*;
import static sndhrdw.williams.*;
import static vidhrdw.williams.*;
import static mame.inptport.*;

public class williams {
        public static int defender_bank;
        public static int bank_address;


        static int  video_counter;
        static int  Index;
        static int  Stargate_IntTable[] = {0,0xFF,0x00,0xff};
        static int  IntTable[] = {0xAA,0xff,0,0x55};


        public static InterruptPtr Williams_Interrupt = new InterruptPtr() {
            public int handler() {
          video_counter = IntTable[Index&0x03];
          Index++;

          return INT_IRQ;
        }};

        public static InterruptPtr Stargate_Interrupt = new InterruptPtr() {
            public int handler() {

          video_counter = Stargate_IntTable[Index&0x03];
          Index++;

          return INT_IRQ;
        }};
        public static InterruptPtr Defender_Interrupt = new InterruptPtr() {
            public int handler() {

          video_counter = Stargate_IntTable[Index&0x03];
          Index++;
          if (RAM[0x10000+0xc07] == 0x3C)
            return INT_IRQ;

          return INT_NONE;
        }};




      public static ReadHandlerPtr video_counter_r = new ReadHandlerPtr()
      {
        public int handler(int offset)
        {
        /*
                if (errorlog)
                  fprintf(errorlog,"### Get Video counter %02X at %04X\n",video_counter,m6809_GetPC());
        */
                return video_counter;
        }};



public static InitMachinePtr williams_nofastop_init_machine= new InitMachinePtr() {
         public void handler() {
/*So we do not have to copy the roms in RAM[] when bank switching
   On my system its much faster.*/
/* used with Balster and Defender */
    m6809_Flags = M6809_FAST_NONE;
}};
public static InitMachinePtr williams_init_machine= new InitMachinePtr() {
         public void handler() {

/*The cpu will fetch its instructions directly in RAM[]*/
  m6809_Flags = M6809_FAST_OP;
 
}};



        /*
         *  For Joust
         */
        public static ReadHandlerPtr input_port_0_1 = new ReadHandlerPtr() {
         public int handler(int offset) {
                if((RAM[0xc807] & 0x1C) == 0x1C)
                   return input_port_1_r.handler(0);
                else
                   return input_port_0_r.handler(0);
        }};

        /*
         *  For Splat
         */
        public static ReadHandlerPtr input_port_2_3 = new ReadHandlerPtr() {
         public int handler(int offset) {
                if((RAM[0xc807] & 0x1C) == 0x1c)
                   return input_port_3_r.handler(0);
                else
                   return input_port_2_r.handler(0);
        }};

        /*
         *  For Blaster
         */
        public static ReadHandlerPtr blaster_input_port_0 = new ReadHandlerPtr() {
         public int handler(int offset) {
                int i;
                int keys;

                keys = input_port_0_r.handler(0);

                if((keys & 0x04)!=0)
                  i = 0x00;
                else if((keys & 0x08)!=0)
                  i = 0x80;
                else
                  i = 0x40;

                if((keys&0x02)!=0)
                  i += 0x00;
                else if((keys&0x01)!=0)
                  i += 0x08;
                else
                  i += 0x04;

                return i;
        }};


        /*
         *   Defender Read at C000-CFFF
         */
        public static ReadHandlerPtr defender_bank_r = new ReadHandlerPtr() {
                 public int handler(int offset) {
                     
                if(defender_bank == 0){         /* If bank = 0 then we are in the I/O */
                  if(offset == 0xc00)           /* Buttons IN 0  */
                    return input_port_0_r.handler(0);
                  if(offset == 0xc04)           /* Buttons IN 1  */
                    return input_port_1_r.handler(0);
                  if(offset == 0xc06)           /* Buttons IN 2  */
                    return input_port_2_r.handler(0);
                  if(offset == 0x800)           /* video counter */
                    return video_counter;
        /*  Log
         *    if (errorlog)
         *      fprintf(errorlog,"-- Read %04X at %04X\n",0xC000+offset,m6809_GetPC());
         *    else = RAM
         */
                  return RAM[0x10000+offset];
                }

                /* If not bank 0 then read RAM[] */

                return RAM[bank_address+offset];
        }};


        /*
         *  Defender Write at C000-CFFF
         */
        public static WriteHandlerPtr defender_bank_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                if (defender_bank == 0) {
                    RAM[0x10000+offset] = (char)data;
                /* WatchDog */
                    if (offset == 0x03FC)
                      return;
                /* Palette  */
                    if (offset < 0x10)
                      Williams_Palette_w.handler(offset,data);
                /* Sound    */
                    if (offset == 0x0c02)
                      williams_sh_w.handler(offset,data);
                }
        }};


        /*
         *  Defender Select a bank
         *  There is just data in bank 0
         */
        public static WriteHandlerPtr defender_bank_select_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                if (data == 7) data = 4;      /*  more convenient for us  */
                        if (defender_bank == data)
                                return;

                defender_bank = data;
                bank_address = data*0x1000 + 0x10000; /*Address of the ROM */
        }};
/* JB 970823 - speed up very busy loop in Defender */
/*    E7C3: LDA   $5D    ; dp=a0
      E7C5: BEQ   $E7C3   */
        public static ReadHandlerPtr defender_catch_loop_r = new ReadHandlerPtr() {
                 public int handler(int offset) {

                char t;

                t = RAM[0xa05d];
                if( cpu_getpc()==0xe7c5 && t==0 ) cpu_seticount(0);
            return t;
        }};

/* JB 970823 - speed up very busy loop in Stargate */
/*    0011: LDA   $39    ; dp=9c
      0013: BEQ   $0011   */
        public static ReadHandlerPtr stargate_catch_loop_r = new ReadHandlerPtr() {
                 public int handler(int offset) {

                char t;

                t = RAM[0x9c39];
                if( cpu_getpc()==0x0013 && t==0 ) cpu_seticount(0);
            return t;
        }};

/* JB 970823 - speed up very busy loop in Robotron */
/*    D19B: LDA   $10    ; dp=98
      D19D: CMPA  #$02
      D19F: BCS   $D19B  ; (BLO)   */
        public static ReadHandlerPtr robotron_catch_loop_r = new ReadHandlerPtr() {
                 public int handler(int offset) {

            char t;

            t = RAM[0x9810];
            if( cpu_getpc()==0xd19d && t<2 ) cpu_seticount(0);
            return t;
        }};



/* JB 970823 - speed up very busy loop in Splat */
/*    D04F: LDA   $4B    ; dp=98
      D051: BEQ   $D04F   */
        public static ReadHandlerPtr splat_catch_loop_r = new ReadHandlerPtr() {
                 public int handler(int offset) {

	 char t;

                t = RAM[0x984B];
                if( cpu_getpc()==0xd051 && t==0 ) cpu_seticount(0);
            return t;
        }};


        /*
         *  Blaster bank select
         */
        public static WriteHandlerPtr blaster_bank_select_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                if (defender_bank == data)
                        return;
                bank_address = data*0x4000 + 0x10000; /*Address of the ROM */
                defender_bank = data;   /* Banks are 0x4000 byte long from 0x10000 in RAM */
        }};
    
}
