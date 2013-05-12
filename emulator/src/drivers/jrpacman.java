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
 *  Notes : Roms are from v0.36 romset
 *
 */



package drivers;


import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static machine.jrpacman.*;
import static vidhrdw.generic.*;
import static sndhrdw.pengo.*;
import static vidhrdw.jrpacman.*;
import static arcadeflex.libc.*;

public class jrpacman 
{
        static MemoryReadAddress  readmem[] =
        {
                new MemoryReadAddress( 0x4000, 0x4fff, MRA_RAM ),	/* including video and color RAM */
                new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
                new MemoryReadAddress( 0x8000, 0xdfff, MRA_ROM ),
                new MemoryReadAddress( 0x5000, 0x503f, input_port_0_r ),	/* IN0 */
                new MemoryReadAddress( 0x5040, 0x507f, input_port_1_r ),	/* IN1 */
                new MemoryReadAddress( 0x5080, 0x50bf, input_port_2_r ),	/* DSW1 */
                new MemoryReadAddress( -1 )	/* end of table */
        };
        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x4800, 0x4fef, MWA_RAM ),
                new MemoryWriteAddress( 0x4000, 0x47ff, jrpacman_videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x5040, 0x505f, pengo_sound_w, pengo_soundregs ),
                new MemoryWriteAddress( 0x4ff0, 0x4fff, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x5060, 0x506f, MWA_RAM, spriteram_2 ),
                new MemoryWriteAddress( 0x5080, 0x5080, MWA_RAM, jrpacman_scroll ),
                new MemoryWriteAddress( 0x5070, 0x5070, jrpacman_palettebank_w, jrpacman_palettebank ),
                new MemoryWriteAddress( 0x5071, 0x5071, jrpacman_colortablebank_w, jrpacman_colortablebank ),
                new MemoryWriteAddress( 0x5073, 0x5073, MWA_RAM, jrpacman_bgpriority ),
                new MemoryWriteAddress( 0x5074, 0x5074, jrpacman_charbank_w, jrpacman_charbank ),
                new MemoryWriteAddress( 0x5075, 0x5075, MWA_RAM, jrpacman_spritebank ),
                new MemoryWriteAddress( 0x5000, 0x5000, interrupt_enable_w ),
                new MemoryWriteAddress( 0x50c0, 0x50c0, MWA_NOP ),
                new MemoryWriteAddress( 0x5001, 0x5001, pengo_sound_enable_w ),
                new MemoryWriteAddress( 0x5002, 0x5007, MWA_NOP ),
                new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
                new MemoryWriteAddress( 0x8000, 0xdfff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };   
 
        static IOWritePort writeport[] =
        {
                new IOWritePort( 0, 0, interrupt_vector_w ),
                new IOWritePort( -1 )	/* end of table */
        };



        static InputPort input_ports[] =
        {
                new InputPort(	/* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_UP, OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_DOWN,
                                        OSD_KEY_F1, 0, 0, OSD_KEY_3 }
                       
                ),
                new InputPort(	/* IN1 */
                        0xff,
                        new int[]{ 0, 0, 0, 0, OSD_KEY_F2, OSD_KEY_1, OSD_KEY_2, 0 }
                        
                ),
                new InputPort(	/* DSW1 */
                        0xe9,
                        new int[]{ 0, 0, 0, 0, 0, OSD_KEY_F1, 0, 0 }
                       
                ),
                new InputPort( -1 )	/* end of table */
        };
        static  TrakPort trak_ports[] =
        {
                new TrakPort(-1)
        };

        static KEYSet keys[] =
        {
                new KEYSet( 0, 0, "MOVE UP" ),
                new KEYSet( 0, 1, "MOVE LEFT"  ),
                new KEYSet( 0, 2, "MOVE RIGHT" ),
                new KEYSet( 0, 3, "MOVE DOWN" ),
                new KEYSet(-1)
        };

        static DSW dsw[] =
        {
                new DSW( 2, 0x0c, "LIVES", new String[]{ "1", "2", "3", "5" } ),
                new DSW( 2, 0x30, "BONUS", new String[]{ "10000", "15000", "20000", "30000" } ),
                new DSW( 2, 0x40, "DIFFICULTY", new String[]{ "HARD", "NORMAL" }, 1 ),
                new DSW( 2, 0x80, "SW7", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( -1 )
        };

        static  GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                512,	/* 512 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
                new int[]{ 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 }, /* characters are rotated 90 degrees */
                new int[]{ 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3 },	/* bits are packed in groups of four */
                16*8	/* every char takes 16 bytes */
        );
        static  GfxLayout spritelayout = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                128,	/* 128 sprites */
                2,	/* 2 bits per pixel */
                 new int[]{ 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
                 new int[]{ 39 * 8, 38 * 8, 37 * 8, 36 * 8, 35 * 8, 34 * 8, 33 * 8, 32 * 8,
                                7 * 8, 6 * 8, 5 * 8, 4 * 8, 3 * 8, 2 * 8, 1 * 8, 0 * 8 },
                 new int[]{ 8*8, 8*8+1, 8*8+2, 8*8+3, 16*8+0, 16*8+1, 16*8+2, 16*8+3,
                                24*8+0, 24*8+1, 24*8+2, 24*8+3, 0, 1, 2, 3 },
                64*8	/* every sprite takes 64 bytes */
        );
        
        static  GfxDecodeInfo gfxdecodeinfo[] =
        {
                 new GfxDecodeInfo( 1, 0x0000, charlayout,      0, 128 ),
                 new GfxDecodeInfo( 1, 0x2000, spritelayout,    0, 128 ),
                 new GfxDecodeInfo( -1 ) /* end of array */
        };

        static char color_prom[] =
        {
                /* 9E - palette low bits */
                0x00,0x07,0x0D,0x0F,0x08,0x08,0x0A,0x0F,0x02,0x0F,0x0D,0x09,0x08,0x0A,0x0F,0x0F,
                0x00,0x05,0x07,0x09,0x00,0x03,0x06,0x0D,0x00,0x0F,0x0B,0x0D,0x00,0x0F,0x0F,0x08,
                /* 9F - palette high bits */
                0x00,0x00,0x01,0x0E,0x01,0x0F,0x0E,0x02,0x01,0x03,0x02,0x0C,0x03,0x0A,0x0A,0x0F,
                0x00,0x00,0x00,0x00,0x01,0x01,0x02,0x02,0x03,0x03,0x05,0x0A,0x0C,0x0F,0x0E,0x0F,
                /* 9P - color lookup table */
                0x00,0x00,0x00,0x00,0x00,0x0F,0x0B,0x01,0x00,0x00,0x00,0x00,0x00,0x0F,0x0B,0x03,
                0x00,0x00,0x00,0x00,0x00,0x0F,0x0B,0x05,0x00,0x00,0x00,0x00,0x00,0x0F,0x0B,0x07,
                0x00,0x08,0x0A,0x09,0x00,0x0B,0x01,0x09,0x00,0x0E,0x0B,0x07,0x00,0x09,0x06,0x07,
                0x00,0x09,0x02,0x06,0x00,0x0F,0x04,0x09,0x00,0x0F,0x00,0x0E,0x00,0x01,0x0C,0x0F,
                0x00,0x0E,0x00,0x0B,0x00,0x0C,0x0B,0x0E,0x00,0x0C,0x0F,0x01,0x00,0x04,0x0C,0x0F,
                0x00,0x01,0x02,0x0F,0x00,0x07,0x0C,0x02,0x00,0x09,0x06,0x0F,0x00,0x0D,0x0C,0x0F,
                0x00,0x05,0x03,0x09,0x00,0x0F,0x0B,0x00,0x00,0x0E,0x00,0x0B,0x00,0x0E,0x00,0x0B,
                0x00,0x0F,0x02,0x07,0x00,0x0F,0x0E,0x01,0x00,0x0F,0x0B,0x0E,0x00,0x0E,0x00,0x0F,
                0x00,0x00,0x00,0x00,0x00,0x0D,0x0C,0x02,0x00,0x04,0x08,0x0D,0x00,0x05,0x06,0x07,
                0x00,0x0B,0x0D,0x0A,0x00,0x0A,0x0D,0x01,0x00,0x05,0x0A,0x03,0x00,0x06,0x0C,0x0D,
                0x00,0x05,0x07,0x09,0x00,0x0C,0x02,0x09,0x00,0x0D,0x0C,0x0E,0x00,0x0D,0x0C,0x0F,
                0x00,0x0D,0x0C,0x06,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        };



        /* waveforms for the audio hardware */
        /* I don't know if these are correct. I'm using the Pac Man ones. */
        static char samples[] =
        {
                0xff,0x11,0x22,0x33,0x44,0x55,0x55,0x66,0x66,0x66,0x55,0x55,0x44,0x33,0x22,0x11,
                0xff,0xdd,0xcc,0xbb,0xaa,0x99,0x99,0x88,0x88,0x88,0x99,0x99,0xaa,0xbb,0xcc,0xdd,

                0xff,0x44,0x66,0x66,0x55,0x33,0x11,0x22,0x33,0x33,0x22,0x11,0xee,0xcc,0xbb,0xdd,
                0xff,0x11,0x33,0x22,0x00,0xdd,0xcc,0xbb,0xbb,0xcc,0xdd,0xbb,0x99,0x88,0x88,0xaa,

                0xff,0x22,0x44,0x55,0x66,0x55,0x44,0x22,0xff,0xcc,0xaa,0x99,0x88,0x99,0xaa,0xcc,
                0xff,0x33,0x55,0x66,0x55,0x33,0xff,0xbb,0x99,0x88,0x99,0xbb,0xff,0x66,0xff,0x88,

                0xff,0x55,0x33,0x00,0x33,0x55,0x11,0xee,0x33,0x66,0x44,0xff,0x11,0x22,0xee,0xaa,
                0xff,0x44,0x00,0xcc,0xdd,0xff,0xaa,0x88,0xbb,0x00,0xdd,0x99,0xbb,0xee,0xbb,0x99,

                0x88,0x00,0x77,0xff,0x99,0x00,0x66,0xff,0xaa,0x00,0x55,0xff,0xbb,0x00,0x44,0xff,
                0xcc,0x00,0x33,0xff,0xdd,0x00,0x22,0xff,0xee,0x00,0x11,0xff,0xff,0x00,0x00,0xff,

                0xff,0x00,0xee,0x11,0xdd,0x22,0xcc,0x33,0xbb,0x44,0xaa,0x55,0x99,0x66,0x88,0x77,
                0x88,0x77,0x99,0x66,0xaa,0x55,0xbb,0x44,0xcc,0x33,0xdd,0x22,0xee,0x11,0xff,0x00,

                0x88,0x99,0xaa,0xbb,0xcc,0xdd,0xee,0xff,0x00,0x11,0x22,0x33,0x44,0x55,0x66,0x77,
                0x77,0x66,0x55,0x44,0x33,0x22,0x11,0x00,0xff,0xee,0xdd,0xcc,0xbb,0xaa,0x99,0x88,

                0x88,0x99,0xaa,0xbb,0xcc,0xdd,0xee,0xff,0x00,0x11,0x22,0x33,0x44,0x55,0x66,0x77,
                0x88,0x99,0xaa,0xbb,0xcc,0xdd,0xee,0xff,0x00,0x11,0x22,0x33,0x44,0x55,0x66,0x77
        };
        
        static  MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                3072000,	/* 3.072 Mhz */
                                0,
                                readmem,writemem,null,writeport,
                                jrpacman_interrupt,1
                        )
                },
                60,
                jrpacman_init_machine,

                /* video hardware */
                28*8, 36*8, new rectangle( 0*8, 28*8-1, 0*8, 36*8-1 ),
                gfxdecodeinfo,
                32,128*4,
                jrpacman_vh_convert_color_prom,
                VIDEO_TYPE_RASTER,
                null,
                jrpacman_vh_start,
                jrpacman_vh_stop,
                jrpacman_vh_screenrefresh,

                /* sound hardware */
                samples,
                null,
                null,
                null,
                pengo_sh_update
        );
        
        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr jrpacman_rom= new RomLoadPtr(){ public void handler() 
        {
                 ROM_REGION(0x10000);	/* 64k for code */
                 ROM_LOAD( "jrp8d.bin",    0x0000, 0x2000, 0xe3fa972e );
                 ROM_LOAD( "jrp8e.bin",    0x2000, 0x2000, 0xec889e94 );
                 ROM_LOAD( "jrp8h.bin",    0x8000, 0x2000, 0x35f1fc6e );
                 ROM_LOAD( "jrp8j.bin",    0xa000, 0x2000, 0x9737099e );
                 ROM_LOAD( "jrp8k.bin",    0xc000, 0x2000, 0x5252dd97 );

                ROM_REGION(0x4000);/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "jrp2c.bin",    0x0000, 0x2000, 0x0527ff9b );
	        ROM_LOAD( "jrp2e.bin",    0x2000, 0x2000, 0x73477193 );
                ROM_END();
         }};
        

          static DecodePtr jrpacman_decode = new DecodePtr()
          {
                /* The encryption PALs garble bits 0, 2 and 7 of the ROMs. The encryption */
                /* scheme is complex (basically it's a state machine) and can only be */
                /* faithfully emulated at run time. To avoid the performance hit that would */
                /* cause, here we have a table of the values which must be XORed with */
                /* each memory region to obtain the decrypted bytes. */
                /* Decryption table provided by David Caldwell (david@indigita.com) */
                /* For an accurate reproduction of the encryption, see jrcrypt.c */
            public void handler()
            {
              int[][] table = { 
                  { 0x00C1, 0x00 },{ 0x0002, 0x80 },{ 0x0004, 0x00 },{ 0x0006, 0x80 },
                        { 0x0003, 0x00 },{ 0x0002, 0x80 },{ 0x0009, 0x00 },{ 0x0004, 0x80 },
                        { 0x9968, 0x00 },{ 0x0001, 0x80 },{ 0x0002, 0x00 },{ 0x0001, 0x80 },
                        { 0x0009, 0x00 },{ 0x0002, 0x80 },{ 0x0009, 0x00 },{ 0x0001, 0x80 },
                        { 0x00AF, 0x00 },{ 0x000E, 0x04 },{ 0x0002, 0x00 },{ 0x0004, 0x04 },
                        { 0x001E, 0x00 },{ 0x0001, 0x80 },{ 0x0002, 0x00 },{ 0x0001, 0x80 },
                        { 0x0002, 0x00 },{ 0x0002, 0x80 },{ 0x0009, 0x00 },{ 0x0002, 0x80 },
                        { 0x0009, 0x00 },{ 0x0002, 0x80 },{ 0x0083, 0x00 },{ 0x0001, 0x04 },
                        { 0x0001, 0x01 },{ 0x0001, 0x00 },{ 0x0002, 0x05 },{ 0x0001, 0x00 },
                        { 0x0003, 0x04 },{ 0x0003, 0x01 },{ 0x0002, 0x00 },{ 0x0001, 0x04 },
                        { 0x0003, 0x01 },{ 0x0003, 0x00 },{ 0x0003, 0x04 },{ 0x0001, 0x01 },
                        { 0x002E, 0x00 },{ 0x0078, 0x01 },{ 0x0001, 0x04 },{ 0x0001, 0x05 },
                        { 0x0001, 0x00 },{ 0x0001, 0x01 },{ 0x0001, 0x04 },{ 0x0002, 0x00 },
                        { 0x0001, 0x01 },{ 0x0001, 0x04 },{ 0x0002, 0x00 },{ 0x0001, 0x01 },
                        { 0x0001, 0x04 },{ 0x0002, 0x00 },{ 0x0001, 0x01 },{ 0x0001, 0x04 },
                        { 0x0001, 0x05 },{ 0x0001, 0x00 },{ 0x0001, 0x01 },{ 0x0001, 0x04 },
                        { 0x0002, 0x00 },{ 0x0001, 0x01 },{ 0x0001, 0x04 },{ 0x0002, 0x00 },
                        { 0x0001, 0x01 },{ 0x0001, 0x04 },{ 0x0001, 0x05 },{ 0x0001, 0x00 },
                        { 0x01B0, 0x01 },{ 0x0001, 0x00 },{ 0x0002, 0x01 },{ 0x00AD, 0x00 },
                        { 0x0031, 0x01 },{ 0x005C, 0x00 },{ 0x0005, 0x01 },{ 0x604E, 0x00 },
                        { 0,0 }
                 };

              int A = 0;
              int i = 0;
              while (table[i][0] != 0)
              {
                for (int j = 0; j < table[i][0]; j++)
                {
                  int tmp1 = A;
                  char[] tmp = RAM; tmp[tmp1] = (char)(tmp[tmp1] ^ (char)table[i][1]);
                  A++;
                }
                i++;
              }
            }
          };
          static int resetcount;
          static HiscoreLoadPtr hiload = new HiscoreLoadPtr()
          {
            public int handler(String name)
            {
                /* during a reset, leave time to the game to clear the screen */
                if (++resetcount < 60) return 0;

                /* wait for "HIGH SCORE" to be on screen */
                if (memcmp(RAM, 0x476d, new char[] { 0x40, 0x40, 0x40, 0x40 }, 4) == 0 &&
				memcmp(RAM, 0x4751, new char[] { 0x48, 0x47, 0x49, 0x48 }, 4) == 0)
                    
                {
                        FILE f;


                        resetcount = 0;

                        if ((f = fopen(name,"rb")) != null)
                        {
                                char buf[] =new char[10];
                                int hi;


                                fread(RAM,0x4e88,1,4,f);
                                /* also copy the high score to the screen, otherwise it won't be */
                                /* updated */
                                hi = (RAM[0x4e88] & 0x0f) +
                                     (RAM[0x4e88] >> 4) * 10 +
                                     (RAM[0x4e89] & 0x0f) * 100 +
                                     (RAM[0x4e89] >> 4) * 1000 +
                                     (RAM[0x4e8a] & 0x0f) * 10000 +
                                     (RAM[0x4e8a] >> 4) * 100000 +
                                     (RAM[0x4e8b] & 0x0f) * 1000000 +
                                     (RAM[0x4e8b] >> 4) * 10000000;
                                if (hi!=0)
                                {
                                        
                                        sprintf(buf, "%8d", new Object[] { Integer.valueOf(hi) });
                                        
                                        if (buf[2] != ' ') jrpacman_videoram_w.handler(0x0772,buf[2]-'0');
                                        if (buf[3] != ' ') jrpacman_videoram_w.handler(0x0771,buf[3]-'0');
                                        if (buf[4] != ' ') jrpacman_videoram_w.handler(0x0770,buf[4]-'0');
                                        if (buf[5] != ' ') jrpacman_videoram_w.handler(0x076f,buf[5]-'0');
                                        if (buf[6] != ' ') jrpacman_videoram_w.handler(0x076e,buf[6]-'0');
                                        jrpacman_videoram_w.handler(0x076d,buf[7]-'0');
                                                     
                                }
                                fclose(f);
                        }

                        return 1;
                }
                else return 0;	/* we can't load the hi scores yet */
            }
          };
          static HiscoreSavePtr hisave = new HiscoreSavePtr()
          {
            public void handler(String name)
            {
                FILE f;
                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x4e88,1,4,f);
                        fclose(f);
                }
            }
          };
          public static GameDriver jrpacman_driver = new GameDriver
          (
                    "Jr. Pac Man",
                    "jrpacman",
                    "DAVID CALDWELL\nNICOLA SALMORIA",
                    machine_driver,

                    jrpacman_rom,
                    jrpacman_decode, null,
                    null,

                    input_ports,null, trak_ports, dsw, keys,

                    color_prom, null, null,
                    ORIENTATION_DEFAULT,

                    hiload, hisave
            ); 
        
}






