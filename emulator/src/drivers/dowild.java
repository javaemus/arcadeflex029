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
 *  NOTES: romsets are from v0.36 roms 
 */



package drivers;


import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.inptport.*;
import static machine.docastle.*;
import static vidhrdw.generic.*;
import static vidhrdw.docastle.*;
import static mame.osdependH.*;
import static mame.mame.*;
import static sndhrdw.docastle.*;

public class dowild {
            static  MemoryReadAddress readmem[] =
            {
                    new MemoryReadAddress( 0x2000, 0x37ff, MRA_RAM ),
                    new MemoryReadAddress( 0x0000, 0x1fff, MRA_ROM ),
                    new MemoryReadAddress( 0x4000, 0x9fff, MRA_ROM ),
                    new MemoryReadAddress( 0xa000, 0xa008, docastle_shared0_r ),
                    new MemoryReadAddress( -1 ),	/* end of table */
            };

            static  MemoryWriteAddress writemem[] =
            {
                    new MemoryWriteAddress( 0x2000, 0x37ff, MWA_RAM ),
                    new MemoryWriteAddress( 0x3800, 0x39ff, MWA_RAM, spriteram, spriteram_size ),
                    new MemoryWriteAddress( 0xb000, 0xb3ff, videoram_w, videoram, videoram_size ),
                    new MemoryWriteAddress( 0xb400, 0xb7ff, colorram_w, colorram ),
                    new MemoryWriteAddress( 0xa000, 0xa008, docastle_shared1_w ),
                    new MemoryWriteAddress( 0xb800, 0xb800, docastle_nmitrigger ),
                    new MemoryWriteAddress( 0xa800, 0xa800, MWA_NOP ),
                    new MemoryWriteAddress( 0x0000, 0x1fff, MWA_ROM ),
                    new MemoryWriteAddress( 0x4000, 0x9fff, MWA_ROM ),
                    new MemoryWriteAddress( -1 )	/* end of table */
            };

            public static ReadHandlerPtr pup = new ReadHandlerPtr() {
                public int handler(int offs) {
                  return 0xfc;
             }
            };
            static  MemoryReadAddress readmem2[] =
            {
                     new MemoryReadAddress( 0x8000, 0x87ff, MRA_RAM ),
                     new MemoryReadAddress( 0xc003, 0xc003, input_port_0_r ),
                     new MemoryReadAddress( 0xc005, 0xc005, input_port_1_r ),
                     new MemoryReadAddress( 0xc007, 0xc007, input_port_2_r ),
                     new MemoryReadAddress( 0xc002, 0xc002, input_port_3_r ),
                     new MemoryReadAddress( 0xc081, 0xc081, input_port_4_r ),
                     new MemoryReadAddress( 0xc085, 0xc085, input_port_5_r ),
                     new MemoryReadAddress( 0xc082,0xc082,pup),
                     new MemoryReadAddress( 0xc083,0xc083,pup),
                     new MemoryReadAddress( 0xc084,0xc084,pup),
                     new MemoryReadAddress( 0xc087,0xc087,pup),
                     new MemoryReadAddress( 0xe000, 0xe008, docastle_shared1_r ),
                     new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
                     new MemoryReadAddress( -1 )	/* end of table */
            };

            static  MemoryWriteAddress writemem2[] =
            {
                    new MemoryWriteAddress( 0x8000, 0x87ff, MWA_RAM ),
                    new MemoryWriteAddress( 0xe000, 0xe008, docastle_shared0_w ),
                    new MemoryWriteAddress( 0xa000, 0xa000, docastle_sound1_w ),
                    new MemoryWriteAddress( 0xa400, 0xa400, docastle_sound2_w ),
                    new MemoryWriteAddress( 0xa800, 0xa800, docastle_sound3_w ),
                    new MemoryWriteAddress( 0xac00, 0xac00, docastle_sound4_w ),
                    new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
                    new MemoryWriteAddress( -1 ),	/* end of table */
            };



            //TODO : joystick support???
            static InputPort input_ports[] =
            {
                    new InputPort(	/* IN0 */
                            0xff,
                           new int[] { OSD_KEY_RIGHT, OSD_KEY_UP, OSD_KEY_LEFT, OSD_KEY_DOWN,
                                            OSD_KEY_Q, OSD_KEY_W, OSD_KEY_E, OSD_KEY_R }                        
                    ),
                    new InputPort(	/* IN1 */
                            0xff,
                            new int[]{ OSD_KEY_CONTROL, 0, 0, OSD_KEY_1,
                                            0, 0, 0, OSD_KEY_2 }
                    ),
                    new InputPort(	/* IN2 */
                            0xff,
                            new int[]{ 0, 0, 0, 0, OSD_KEY_3, 0, 0, 0 }
                    ),
                    new InputPort(	/* DSWA */
                            0xdf,
                            new int[]{ 0, 0, OSD_KEY_F1, 0, 0, 0, 0, 0 }
                    ),
                    new InputPort(	/* COIN */
                            0xff,
                            new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                    ),
                    new InputPort(	/* TEST */
                            0xff,
                            new int[]{ OSD_KEY_F2, 0, 0, 0, 0, 0, 0, 0 }
                    ),
                    new InputPort( -1 )	/* end of table */
            };

            static  TrakPort trak_ports[] =
            {
                    new TrakPort(-1) 
            };


            static  KEYSet dowild_keys[] =
            {
                     new KEYSet( 0, 1, "MOVE UP" ),
                     new KEYSet( 0, 2, "MOVE LEFT"  ),
                     new KEYSet( 0, 0, "MOVE RIGHT" ),
                     new KEYSet( 0, 3, "MOVE DOWN" ),
                     new KEYSet( 1, 0, "ACCELERATE" ),
                     new KEYSet( -1 )
            };

            static  KEYSet dorunrun_keys[] =
            {
                     new KEYSet( 0, 1, "MOVE UP" ),
                     new KEYSet( 0, 2, "MOVE LEFT"  ),
                     new KEYSet( 0, 0, "MOVE RIGHT" ),
                     new KEYSet( 0, 3, "MOVE DOWN" ),
                     new KEYSet( 1, 0, "FIRE" ),
                     new KEYSet( -1 )
            };

            static  KEYSet kickridr_keys[] =
            {
                     new KEYSet( 0, 1, "MOVE UP" ),
                     new KEYSet( 0, 2, "MOVE LEFT"  ),
                     new KEYSet( 0, 0, "MOVE RIGHT" ),
                     new KEYSet( 0, 3, "MOVE DOWN" ),
                     new KEYSet( 1, 0, "KICK" ),
                     new KEYSet( -1 )
            };


            static  DSW dsw[] =
            {
                    new DSW( 3, 0x80, "LIVES", new String[]{ "5", "3" }, 1 ),
                    new DSW( 3, 0x03, "DIFFICULTY", new String[]{ "HARDEST", "HARD", "MEDIUM", "EASY" }, 1 ),
                    new DSW( 3, 0x10, "EXTRA", new String[]{ "HARD", "EASY" }, 1 ),
                    new DSW( 3, 0x40, "SPECIAL", new String[]{ "NOT GIVEN", "GIVEN" }, 1 ),
                    new DSW( 3, 0x08, "SW4", new String[]{ "ON", "OFF" }, 1 ),
                    new DSW( -1 )
            };



            static  GfxLayout charlayout = new GfxLayout
            (
                    8,8,    /* 8*8 characters */
                    512,    /* 512 characters */
                    4,      /* 4 bits per pixel */
                    new int[]{ 0, 1, 2, 3 }, /* the bitplanes are packed in one nibble */
                    new int[]{ 0, 4, 8, 12, 16, 20, 24, 28 },
                    new int[]{ 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
                    32*8   /* every char takes 32 consecutive bytes */
            );
            static  GfxLayout spritelayout  = new GfxLayout
            (
                    16,16,  /* 16*16 sprites */
                    256,    /* 256 sprites */
                    4,      /* 4 bits per pixel */
                    new int[]{ 0, 1, 2, 3 }, /* the bitplanes are packed in one nibble */
                    new int[]{ 0, 4, 8, 12, 16, 20, 24, 28,
                                    32, 36, 40, 44, 48, 52, 56, 60 },
                    new int[]{ 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64,
                                    8*64, 9*64, 10*64, 11*64, 12*64, 13*64, 14*64, 15*64 },
                    128*8  /* every sprite takes 128 consecutive bytes */
            );



            static  GfxDecodeInfo gfxdecodeinfo[] =
            {
                    new GfxDecodeInfo( 1, 0x0000, charlayout,       0, 64 ),
                    new GfxDecodeInfo( 1, 0x4000, spritelayout, 64*16, 32 ),
                    new GfxDecodeInfo( -1 ) /* end of array */
            };



            static char dowild_color_prom[] =
            {
                    0x00,0xB6,0x92,0x6F,0x4A,0x42,0x00,0x00,0x80,0xD8,0x90,0x48,0x4C,0xFF,0xE1,0x77,
                    0x80,0xF9,0xF5,0xED,0x4A,0xFF,0x81,0xD3,0x80,0x1C,0x14,0x0C,0x4A,0xFF,0x81,0x77,
                    0x80,0xD9,0xB5,0x91,0x4A,0xFF,0x81,0xD3,0x80,0x1B,0x13,0x0B,0x4A,0xFF,0x81,0x77,
                    0xC1,0x1E,0x16,0x0E,0x4A,0xFF,0x49,0xD3,0x00,0xFC,0xE0,0x1C,0xB4,0x0F,0xF2,0x00,
                    0x00,0xA4,0x8C,0x60,0xFF,0x1C,0xF0,0x00,0x00,0x1C,0xF0,0xF8,0x82,0xA2,0x65,0x00,
                    0xFF,0xFC,0xF4,0xE0,0x17,0x60,0x1C,0x00,0xFF,0xFC,0xF4,0xE0,0x17,0x60,0x1C,0x77,
                    0xFF,0xFC,0xF4,0xE0,0x17,0x60,0x1C,0x00,0xE0,0x14,0x00,0x92,0xFC,0x13,0x8C,0x00,
                    0x00,0xFC,0xE0,0x1B,0xF4,0x0F,0xFF,0xFC,0x90,0x18,0xE0,0xFC,0x6E,0xFF,0x00,0x00,
                    0x00,0x92,0xF4,0x60,0x10,0xFC,0xEC,0x00,0x00,0xE0,0x80,0xB6,0x17,0xEC,0xFF,0xB6,
                    0x00,0xA0,0x6E,0x14,0xFF,0xD3,0x13,0xE0,0x00,0xA0,0x6E,0x14,0xFF,0x77,0x13,0xE0,
                    0x00,0xE4,0xC0,0xF8,0x60,0x0F,0xFF,0x00,0x4A,0x92,0xB6,0xFF,0xFC,0xF0,0xE0,0x00,
                    0x1F,0xFC,0xF0,0xE2,0xE0,0x6E,0xFD,0x00,0x00,0xE0,0x80,0xB6,0x17,0xEC,0x6E,0x1F,
                    0x00,0xE8,0xE0,0xF8,0xFC,0x0F,0xFF,0x88,0x61,0x40,0x4A,0x6E,0xB6,0xFF,0xF4,0xF0,
                    0x00,0x46,0x92,0x6E,0x49,0xFC,0xB4,0x1B,0x00,0xE0,0x00,0xFC,0x0F,0x00,0x00,0xE2,
                    0x00,0x20,0x40,0x60,0x80,0xA0,0xC0,0xE0,0x00,0x04,0x08,0x0C,0x10,0x14,0x18,0x1C,
                    0x00,0x00,0x01,0x01,0x02,0x02,0x03,0x03,0x00,0x24,0x49,0x6D,0x92,0xB6,0xDB,0xFF
            };

            static char dorunrun_color_prom[] =
            {
                    0x00,0xFF,0xFC,0xE8,0xE0,0xC0,0xA0,0xFC,0x00,0x14,0x10,0x0C,0xFC,0xC0,0xFF,0x00,
                    0x00,0x26,0x27,0x4B,0xF9,0xFF,0xE0,0xEA,0x00,0x26,0x27,0x4B,0xE0,0xFC,0x9C,0xFC,
                    0x00,0xEB,0xFC,0x13,0x0F,0x0B,0xFF,0x6E,0x00,0x42,0xFF,0xFC,0xF4,0x1A,0x17,0x0F,
                    0x00,0x42,0xFF,0xFC,0xF4,0xF8,0xD4,0xB0,0x00,0x42,0xFF,0xFC,0xF4,0xF6,0xD2,0xAE,
                    0x00,0x42,0xFF,0xFC,0xF4,0x9C,0x78,0x54,0x00,0x42,0xFF,0xFC,0xF4,0x9E,0x7A,0x56,
                    0x00,0x42,0xFF,0xFC,0xF4,0xD8,0xB4,0x90,0x00,0x42,0xFF,0xFC,0xF4,0xF7,0xD3,0xAF,
                    0x00,0x42,0xFF,0xFC,0xF4,0x59,0x35,0x11,0x00,0x42,0xFF,0xFC,0xF4,0xF2,0xCE,0xAA,
                    0x00,0x42,0xFF,0xFC,0xF4,0xD9,0xB5,0x91,0x00,0x42,0xFF,0xFC,0xF4,0xA1,0x81,0x61,
                    0x00,0x42,0x18,0xF8,0xE4,0xB6,0x92,0x6E,0x00,0x42,0xFC,0xDC,0x9C,0xB6,0x92,0x6E,
                    0x00,0x42,0xFC,0xE0,0x14,0xB6,0x92,0x6E,0x00,0x42,0xDE,0x19,0x15,0xB6,0x92,0x6E,
                    0x00,0xA0,0x0F,0xFF,0xE0,0xEC,0xFC,0x0F,0x1C,0x14,0x0C,0xF4,0xD0,0xAC,0x88,0x00,
                    0xE4,0xF8,0x88,0xB4,0x0B,0x7F,0x6E,0x17,0xFD,0xF8,0xB0,0xE4,0xC0,0x80,0x40,0x00,
                    0x40,0x60,0x80,0xA0,0xF7,0xEF,0xFF,0x00,0x00,0x13,0x52,0xE0,0xFF,0x63,0x1C,0x9C,
                    0x00,0xC1,0x52,0xFC,0xF7,0xEF,0xFB,0x17,0xE4,0xF2,0xF0,0xFC,0x7F,0x1F,0x13,0x00,
                    0x00,0x20,0x40,0x60,0x80,0xA0,0xC0,0xE0,0x00,0x04,0x08,0x0C,0x10,0x14,0x18,0x1C,
                    0x00,0x00,0x01,0x01,0x02,0x02,0x03,0x03,0x00,0x24,0x49,0x6D,0x92,0xB6,0xDB,0xFF
            };

            static char kickridr_color_prom[] =
            {
                    0x00,0x80,0x60,0x40,0xE0,0xC0,0xA0,0xFC,0xE0,0xC0,0xA0,0xF0,0x6E,0x00,0xFF,0x00,
                    0x96,0x14,0xFF,0x08,0x40,0xAC,0x1B,0x00,0x0F,0x07,0x03,0xF0,0x02,0x00,0xFF,0x00,
                    0xD0,0x84,0x64,0x44,0x00,0x00,0xA4,0x00,0xF8,0xF0,0x0A,0xF4,0x00,0xFF,0x00,0x00,
                    0x92,0x14,0xFF,0x08,0x40,0xAC,0x0F,0xEA,0x72,0x14,0xFF,0x08,0x40,0xAC,0x1B,0x00,
                    0x0F,0x07,0xDB,0x4A,0x00,0xF4,0xFF,0x00,0x89,0x81,0xDA,0x4A,0x00,0xF4,0xFF,0x00,
                    0xE0,0xFC,0xA0,0x00,0xFF,0x6E,0x00,0x00,0x09,0x05,0x01,0x00,0xFF,0x6E,0xF0,0x00,
                    0x04,0x14,0x10,0x08,0x04,0x40,0x00,0x00,0xE0,0xFF,0x92,0x80,0x01,0xB6,0x40,0x20,
                    0xFF,0xE0,0x92,0x80,0xF0,0xB6,0x40,0x20,0xF0,0xD0,0x00,0x00,0x00,0x00,0x00,0x00,
                    0x0F,0xE2,0xFF,0xFC,0x1B,0x1F,0xFF,0x00,0xFF,0xF0,0xA0,0xE8,0xFC,0x00,0x6E,0x00,
                    0xDA,0x6E,0x00,0xFC,0x4A,0xFF,0xFF,0x00,0x16,0xB6,0x92,0x1C,0xFF,0xE0,0xFC,0x00,
                    0x0F,0x4A,0xE0,0xA0,0xF0,0x00,0xFF,0x00,0xC0,0xA0,0x80,0x60,0x00,0xDA,0xFF,0x00,
                    0xE0,0xA0,0xDB,0x4A,0x00,0xF0,0xFF,0x00,0xB6,0x92,0x6E,0x00,0x00,0xF4,0xFF,0x00,
                    0xF0,0xD0,0xDB,0xFF,0xFF,0x0C,0x92,0x6E,0xFC,0xFF,0x00,0x02,0x01,0xB6,0x92,0x6E,
                    0xE0,0xFF,0xAC,0x80,0x1B,0x00,0x80,0x40,0xFF,0xE0,0xAC,0x80,0xF4,0x00,0x80,0x40,
                    0x00,0x20,0x40,0x60,0x80,0xA0,0xC0,0xE0,0x00,0x04,0x08,0x0C,0x10,0x14,0x18,0x1C,
                    0x00,0x00,0x01,0x01,0x02,0x02,0x03,0x03,0x00,0x24,0x49,0x6D,0x92,0xB6,0xDB,0xFF
            };



            public static MachineDriver machine_driver = new MachineDriver
            (
 		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
                                    CPU_Z80,
                                    4000000,	/* 4 Mhz ? */
                                    0,
                                    readmem,writemem,null,null,
                                    interrupt,1
                            ),
			new MachineCPU(
                                    CPU_Z80,
                                    4000000,	/* 4 Mhz ??? */
                                    2,	/* memory region #2 */
                                    readmem2, writemem2,null,null,
                                    docastle_interrupt2,16
                            )
                    },
                    60,
                    null,

                    /* video hardware */
                    32*8, 32*8, new rectangle( 1*8, 31*8-1, 4*8, 28*8-1 ),
                    gfxdecodeinfo,
                    256, 96*16,
                    dowild_vh_convert_color_prom,
                    VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
                    null,
                    docastle_vh_start,
                    docastle_vh_stop,
                    docastle_vh_screenrefresh,

                    /* sound hardware */
                    null,
                    null,
                    docastle_sh_start,
                    docastle_sh_stop,
                    docastle_sh_update
            );



            /***************************************************************************

              Game driver(s)

            ***************************************************************************/
            static RomLoadPtr dowild_rom = new RomLoadPtr(){ public void handler()
            {
                    ROM_REGION(0x10000);	/* 64k for code */
                    ROM_LOAD( "W1",  0x0000, 0x2000, 0x097de78b );
                    ROM_LOAD( "W3",  0x4000, 0x2000, 0xfc6a1cbb );
                    ROM_LOAD( "W4",  0x6000, 0x2000, 0x8aac1d30 );
                    ROM_LOAD( "W2",  0x8000, 0x2000, 0x0914ab69 );

                    ROM_REGION(0xc000);	/* temporary space for graphics (disposed after conversion) */
                    ROM_LOAD( "W5",  0x0000, 0x4000, 0xb294b151 );
                    ROM_LOAD( "W6",  0x4000, 0x2000, 0x57e0208b );
                    ROM_LOAD( "W7",  0x6000, 0x2000, 0x5001a6f7 );
                    ROM_LOAD( "W8",  0x8000, 0x2000, 0xec503251 );
                    ROM_LOAD( "W9",  0xa000, 0x2000, 0xaf7bd7eb );

                    ROM_REGION(0x10000);	/* 64k for the second CPU */
                    ROM_LOAD( "W10", 0x0000, 0x4000, 0xd1f37fba );
                    ROM_END();
            }};
            static RomLoadPtr dorunrun_rom = new RomLoadPtr(){ public void handler()
            {
                    ROM_REGION(0x10000);	/* 64k for code */
                    ROM_LOAD( "2764.P1",  0x0000, 0x2000, 0x95c86f8e );
                    ROM_LOAD( "2764.L1",  0x4000, 0x2000, 0xe9a65ba7 );
                    ROM_LOAD( "2764.K1",  0x6000, 0x2000, 0xb1195d3d );
                    ROM_LOAD( "2764.N1",  0x8000, 0x2000, 0x6a8160d1 );

                    ROM_REGION(0xc000);	/* temporary space for graphics (disposed after conversion) */
                    ROM_LOAD( "27128.A3", 0x0000, 0x4000, 0x4be96dcf );
                    ROM_LOAD( "2764.M4",  0x4000, 0x2000, 0x4bb231a0 );
                    ROM_LOAD( "2764.L4",  0x6000, 0x2000, 0x0c08508a );
                    ROM_LOAD( "2764.J4",  0x8000, 0x2000, 0x79287039 );
                    ROM_LOAD( "2764.H4",  0xa000, 0x2000, 0x523aa999 );

                    ROM_REGION(0x10000);	/* 64k for the second CPU */
                    ROM_LOAD( "27128.P7", 0x0000, 0x4000, 0x8b06d461 );
                    ROM_END();
            }};
            static RomLoadPtr kickridr_rom = new RomLoadPtr(){ public void handler()
            {
                    ROM_REGION(0x10000);	/* 64k for code */
                    ROM_LOAD( "K1",  0x0000, 0x2000, 0xdfdd1ab4 );
                    ROM_LOAD( "K3",  0x4000, 0x2000, 0x412244da );
                    ROM_LOAD( "K4",  0x6000, 0x2000, 0xa67dd2ec );
                    ROM_LOAD( "K2",  0x8000, 0x2000, 0xe193fb5c );

                    ROM_REGION(0xc000);	/* temporary space for graphics (disposed after conversion) */
                    ROM_LOAD( "K5",  0x0000, 0x4000, 0x3f7d7e49 );
                    ROM_LOAD( "K6",  0x4000, 0x2000, 0x94252ed3 );
                    ROM_LOAD( "K7",  0x6000, 0x2000, 0x7ef2420e );
                    ROM_LOAD( "K8",  0x8000, 0x2000, 0x29bed201 );
                    ROM_LOAD( "K9",  0xa000, 0x2000, 0x847584d3 );

                    ROM_REGION(0x10000);	/* 64k for the second CPU */
                    ROM_LOAD( "K10", 0x0000, 0x4000, 0xd77bd167 );
                    ROM_END();
            }};

            static HiscoreLoadPtr dowild_hiload = new HiscoreLoadPtr() { public int handler(String name)
            {
                    /* get RAM pointer (this game is multiCPU, we can't assume the global */
                    /* RAM pointer is pointing to the right place) */
                    char []RAM = Machine.memory_region[0];


                    /* check if the hi score table has already been initialized */
                    if (memcmp(RAM,0x8020, new char[] { 0x01 , 0x00 , 0x00 },3) == 0 &&
                                memcmp(RAM,0x8068,new char[] { 0x01 , 0x00 , 0x00 },3) == 0)
                    {
                    
                            FILE f;


                            if ((f = fopen(name, "rb")) != null)
                            {
                                    fread(RAM,0x2020,1,10*8,f);
                                    fclose(f);
                            }

                            return 1;
                    }
                    else return 0;	/* we can't load the hi scores yet */
            }};


            static HiscoreSavePtr dowild_hisave = new HiscoreSavePtr() { public void handler(String name)
            {
                    FILE f;
                    /* get RAM pointer (this game is multiCPU, we can't assume the global */
                    /* RAM pointer is pointing to the right place) */
                    char []RAM = Machine.memory_region[0];


                    if ((f = fopen(name, "wb")) != null)
                    {
                            fwrite(RAM,0x2020,1,10*8,f);
                            fclose(f);
                    }
            }};


            static HiscoreLoadPtr dorunrun_hiload = new HiscoreLoadPtr() { public int handler(String name)
            {

                    /* get RAM pointer (this game is multiCPU, we can't assume the global */
                    /* RAM pointer is pointing to the right place) */
                    char []RAM = Machine.memory_region[0];


                    /* check if the hi score table has already been initialized */
                    if (memcmp(RAM,0x8020, new char[] { 0x00 , 0x10 , 0x00 },3) == 0 &&
                                memcmp(RAM,0x8068,new char[] { 0x00 , 0x10 , 0x00 },3) == 0)
                    {
                    
                            FILE f;


                             if ((f = fopen(name, "rb")) != null)
                            {
                                    fread(RAM,0x2010,1,50*8,f);
                                    fclose(f);
                            }

                            return 1;
                    }
                    else return 0;	/* we can't load the hi scores yet */
            }};

            static HiscoreSavePtr dorunrun_hisave = new HiscoreSavePtr() { public void handler(String name)
            {
                    FILE f;
                    /* get RAM pointer (this game is multiCPU, we can't assume the global */
                    /* RAM pointer is pointing to the right place) */
                    char []RAM = Machine.memory_region[0];


                   if ((f = fopen(name, "wb")) != null)
                    {
                            fwrite(RAM,0x2010,1,50*8,f);
                            fclose(f);
                    }
            }};



            public static GameDriver dowild_driver  = new GameDriver
            (
                    "Mr. Do! Wild Ride",
                    "dowild",
                    "MIRKO BUFFONI\nNICOLA SALMORIA\nGARY WALTON\nSIMON WALLS",
                    machine_driver,

                    dowild_rom,
                    null, null,
                    null,

                    input_ports,null, trak_ports, dsw, dowild_keys,

                    dowild_color_prom, null, null,
                    ORIENTATION_DEFAULT,

                    dowild_hiload, dowild_hisave
            );

            public static GameDriver dorunrun_driver  = new GameDriver
            (
                    "Mr. Do! Run Run",
                    "dorunrun",
                    "MIRKO BUFFONI\nNICOLA SALMORIA\nGARY WALTON\nSIMON WALLS",
                    machine_driver,

                    dorunrun_rom,
                    null, null,
                    null,

                    input_ports,null, trak_ports, dsw, dorunrun_keys,

                    dorunrun_color_prom, null, null,
                    ORIENTATION_DEFAULT,

                    dorunrun_hiload, dorunrun_hisave
            );

             public static GameDriver kickridr_driver  = new GameDriver
            (
                    "Kick Rider",
                    "kickridr",
                    "MIRKO BUFFONI\nNICOLA SALMORIA\nGARY WALTON\nSIMON WALLS",
                    machine_driver,

                    kickridr_rom,
                    null, null,
                    null,

                    input_ports,null, trak_ports, dsw, kickridr_keys,

                    kickridr_color_prom, null, null,
                    ORIENTATION_DEFAULT,

                    dowild_hiload, dowild_hisave
            );
}
