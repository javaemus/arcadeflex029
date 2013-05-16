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
 * roms are from v0.36 romset
 *
 */
package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static sndhrdw.generic.*;
import static vidhrdw.yiear.*;
import static mame.memoryH.*;

public class yiear {
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x5000, 0x57FF, MRA_RAM ),
                new MemoryReadAddress( 0x5800, 0x5FFF, videoram_r, videoram, videoram_size ),
                new MemoryReadAddress( 0x8000, 0xFFFF, MRA_ROM ),

                new MemoryReadAddress( 0x4E00, 0x4E00, input_port_0_r ),	/* coin,start */
                new MemoryReadAddress( 0x4E01, 0x4E01, input_port_1_r ),	/* joy1 */
                new MemoryReadAddress( 0x4E02, 0x4E02, input_port_2_r ),	/* joy2 */
                new MemoryReadAddress( 0x4C00, 0x4C00, input_port_3_r ),	/* misc */
                new MemoryReadAddress( 0x4D00, 0x4D00, input_port_4_r ),	/* test mode */
                new MemoryReadAddress( 0x4E03, 0x4E03, input_port_5_r ),	/* coins per play */
                new MemoryReadAddress( -1 ) /* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x5030, 0x51AF, MWA_RAM, spriteram ),
                new MemoryWriteAddress( 0x5800, 0x5FFF, yiear_videoram_w, videoram ),
                new MemoryWriteAddress( 0x8000, 0xFFFF, MWA_ROM ),
                new MemoryWriteAddress( -1 ) /* end of table */
        };

        static InputPort input_ports[] =
        {
                new InputPort(	/* coin,start */
                        0xff,
                        new int[]{ 0, 0, OSD_KEY_3, OSD_KEY_1, OSD_KEY_2, 0, 0, 0 }
                ),
		new InputPort(	/* joy1 */
                        0xff,
                        new int[]{ OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_UP, OSD_KEY_DOWN,
                                        OSD_KEY_CONTROL, OSD_KEY_ALT, OSD_KEY_SPACE, 0 }
                ),
		new InputPort(	/* joy2 */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* misc */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* test mode */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
               ),
		new InputPort(	/* coins per play */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort( -1 )
        };

        static TrakPort[] trak_ports =
       {
           new TrakPort(-1)
       };

        static KEYSet keys[] =
        {
                new KEYSet( 1, 2, "MOVE UP" ),
                new KEYSet( 1, 0, "MOVE LEFT"  ),
                new KEYSet( 1, 1, "MOVE RIGHT" ),
                new KEYSet( 1, 3, "MOVE DOWN" ),
                new KEYSet( 1, 4, "PUNCH" ),
                new KEYSet( 1, 5, "KICK" ),
                new KEYSet( 1, 6, "JUMP" ),
                new KEYSet( -1 )
        };

        static DSW dsw[] =
        {
                /* DIP SWITCH 1 */
                /* input port, mask, name, values, reverse */
                 new DSW( 3, 0x03, "LIVES", new String[]{ "5", "3", "1", "1" }, 1),
                 new DSW( 3, 0x08, "BONUS", new String[]{ "40000 130000", "30000 110000" }, 1 ),
                 new DSW( 3, 0x60, "DIFFICULTY", new String[]{ "HARDEST", "HARD", "MEDIUM", "EASY" }, 1 ),
                 new DSW( 3, 0x80, "DEMO SOUNDS", new String[]{ "ON", "OFF" }, 1 ),
                 new DSW(-1)
        };

        static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8 by 8 */
                512,	/* 512 characters */
                4,		/* 4 bits per pixel */
                new int[]{ 0,4,65536, 65536+4 },		/* plane */
                new int[]{ 0, 1, 2, 3, 64, 65, 66, 67},		/* x */
                new int[]{ 0, 8, 16, 24, 32, 40, 48, 56},	/* y */
                128
        );

        static GfxLayout spritelayout = new GfxLayout
	(
                16,16,	/* 16 by 16 */
                512,	/* 512 sprites */
                4,		/* 4 bits per pixel */
                new int[]{ 0, 4, 32768*8, 32768*8+4 },	/* plane offsets */
                new int[]{ 0, 1, 2, 3, 64, 65, 66, 67,
                  128+0, 128+1, 128+2, 128+3, 128+64, 128+65, 128+66, 128+67 },
                new int[]{ 0, 8, 16, 24, 32, 40, 48, 56,
                  256+0, 256+8, 256+16, 256+24, 256+32, 256+40, 256+48, 256+56 },
                512
        );

        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                 new GfxDecodeInfo( 1, 0x0000, charlayout,    0, 1 ),
                 new GfxDecodeInfo( 1, 0x4000, spritelayout, 16, 1 ),
                 new GfxDecodeInfo(-1 ) /* end of array */
        };

        static char palette[] =
        {
                /* characters */
                0,0,0,			/* black */
                255,0,0,		/* red */
                180,242,255,		/* bluegreen */
                144,82,67,		/* reddish */
                88,148,48,		/* green */
                50,50,80,		/* dark blue */
                41,93,80,		/* green */
                20,20,20,		/* dark grey */
                128,0,100,		/* temple trim */
                224,255,214,		/* sky */
                130,212,88,		/* lgreen */
                165,159,64,		/* temple lawn */
                100,100,100,		/* grey */
                136,152,126,		/* brownish */
                130,138,236,		/* lblue */
                0xFF, 0xFF, 0xFF,	/* white */

                /* sprites */
                0x00,0x00,0x00, 	/* (transparent) */
                0xFF,0xFF,0x00, 	/* yellow */
                0xFF,0x0F,0x0F, 	/* bright red */
                0,255,0,		/* lime */
                0x5F,0x5F,0xFF, 	/* blue */
                255,255,153,    	/* light flesh */
                153,51,0,		/* brown */
                0xBF,0xBF,0xBF, 	/* dark grey */
                0,0,0,			/* black */
                204,153,51,		/* dark flesh */
                218,104,112,     	/* dark red */
                0x1F,0x7F,0, 	        /* green */
                185,255,253,     	/* light grey */
                255,204,153,	        /* flesh */
                153,102,51, 	        /* wood */
                0xFF,0xFF,0xFF         /* white */
        };

        static char colortable[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};

        static MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_M6809,
                                1250000,	/* 1.25 Mhz */
                                0,			/* memory region */
                                readmem,	/* MemoryReadAddress */
                                writemem,	/* MemoryWriteAddress */
                                null,			/* IOReadPort */
                                null,			/* IOWritePort */
                                interrupt,	/* interrupt routine */
                                1			/* interrupts per frame */
                        )
                },
                60,					/* frames per second */
                null,//yiear_init_machine,	/* init machine routine - needed?*/

                /* video hardware */
                256, 256,				/* screen_width, screen_height */
                new rectangle( 0, 255, 0, 255 ),	/* struct rectangle visible_area */
                gfxdecodeinfo,			/* GfxDecodeInfo * */

                32,						/* total colors */
                32,						/* color table length */
                null,						/* convert color prom routine */

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
                
                null,						/* vh_init routine */
                generic_vh_start,		/* vh_start routine */
                generic_vh_stop,		/* vh_stop routine */
                yiear_vh_screenrefresh,	/* vh_update routine */

                /* sound hardware */
                null,					/* pointer to samples */
                null,					/* sh_init routine */
                null,					/* sh_start routine */
                null,					/* sh_stop routine */
                null					/* sh_update routine */
        );


        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr yiear_rom = new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "i08.10d",      0x08000, 0x4000, 0xe2d7458b );
                ROM_LOAD( "i07.8d",       0x0c000, 0x4000, 0x7db7442e );

                ROM_REGION(0x14000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "g16_1.bin",    0x00000, 0x2000, 0xb68fd91d );
                ROM_LOAD( "g15_2.bin",    0x02000, 0x2000, 0xd9b167c6 );               
                ROM_LOAD( "g06_3.bin",    0x04000, 0x4000, 0xe6aa945b );
                ROM_LOAD( "g05_4.bin",    0x08000, 0x4000, 0xcc187c22 );
                ROM_LOAD( "g04_5.bin",    0x0c000, 0x4000, 0x45109b29 );
                ROM_LOAD( "g03_6.bin",    0x10000, 0x4000, 0x1d650790 );    
                ROM_END();
         }};

        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
        {   
                /* check if the hi score table has already been initialized */
       /*TOFIX                  if ((memcmp(RAM,0x5520,new char[]{0x00,0x36,0x70},3) == 0) &&
                        (memcmp(RAM,0x55A9,new char[]{0x10,0x10,0x10},3) == 0))
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                /* Read the scores */
 /*TOFIX                                        fread(RAM,0x5520,1,14*10,f);
                                /* reset score at top */
 /*TOFIX                                        memcpy(RAM,0x521C,RAM,0x5520,3);
                                fclose(f);
                        }

                        return 1;
                }
                else */return 0;	/* we can't load the hi scores yet */
        }};



        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(){
                FILE f;


 /*TOFIX                        if ((f = fopen(name,"wb")) != null)
                {
                        /* Save the scores */
  /*TOFIX                               fwrite(RAM,0x5520,1,14*10,f);
                        fclose(f);
                }*/

        }};
        public static GameDriver yiear_driver = new GameDriver
	(
                "Yie Ar Kung Fu (Konami)",
                "yiear",
                "ENRIQUE SANCHEZ\nPHILIP STROFFOLINO\nMIKE BALFOUR",
                machine_driver,

                yiear_rom,
                null, null,   /* ROM decode and opcode decode functions */
                null,      /* Sample names */

                input_ports,null, trak_ports, dsw, keys,

                null, palette, colortable,   /* colors, palette, colortable */

                ORIENTATION_DEFAULT,
               hiload, hisave			/* High score load and save */
        );    
}
