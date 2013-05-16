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
 * ported ot v0.28
 * ported to v0.27
 *
 *  roms are from v0.36 romset
 *  
 */
package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static sndhrdw.generic.*;
import static sndhrdw.pokeyintf.*;
import static machine.llander.*;
import static machine.atari_vg.*;
import static vidhrdw.atari_vg.*;
import static vidhrdw.vector.*;
import static mame.memoryH.*;
public class llander {
        static  MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x01ff, MRA_RAM ),
                new MemoryReadAddress( 0x6000, 0x7fff, MRA_ROM ),
                new MemoryReadAddress( 0x4800, 0x5fff, MRA_ROM ),
                new MemoryReadAddress( 0xf800, 0xffff, MRA_ROM ),	/* for the reset / interrupt vectors */
                new MemoryReadAddress( 0x4000, 0x47ff, MRA_RAM, vectorram ),
                new MemoryReadAddress( 0x2000, 0x2000, llander_IN0_r ),	/* IN0 */
                new MemoryReadAddress( 0x2400, 0x2407, llander_IN1_r ),	/* IN1 */
                new MemoryReadAddress( 0x2800, 0x2803, llander_DSW1_r ),	/* DSW1 */
                new MemoryReadAddress( 0x2c00, 0x2c00, llander_IN3_r ),	/* IN3 - joystick pot */
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x01ff, MWA_RAM ),
                new MemoryWriteAddress( 0x4000, 0x47ff, MWA_RAM, vectorram ),
                new MemoryWriteAddress( 0x3000, 0x3000, atari_vg_go ),
        /*	{ 0x3400, 0x3400, wdclr }, */
        /*	{ 0x3c00, 0x3c00, llander_snd }, */
        /*	{ 0x3e00, 0x3e00, llander_snd_reset }, */
                new MemoryWriteAddress( 0x6000, 0x7fff, MWA_ROM ),
                new MemoryWriteAddress( 0x4800, 0x5fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };


        static InputPort input_ports[] =
        {
                new InputPort(       /* IN0 */
                        0xff,
                        new int[]{ 0, OSD_KEY_F2, OSD_KEY_F1, 0, 0, 0, 0, 0 }
                ),
                new InputPort(        /* IN1 */
                        0x00,
                        new int[]{ OSD_KEY_1, OSD_KEY_3, 0, 0, OSD_KEY_2, OSD_KEY_ALT, OSD_KEY_RIGHT, OSD_KEY_LEFT }
                ),
                new InputPort(        /* DSW1 */
                        0x83,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(        /* IN3 - joystick pots */
                        0xff,
                        new int[]{ OSD_KEY_UP, OSD_KEY_CONTROL, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(  -1 )
        };

        static TrakPort trak_ports[] =
        {
               new TrakPort( -1 )
        };


        static KEYSet keys[] =
        {
                new KEYSet( 1, 5, "ABORT" ),
                new KEYSet( 1, 6, "RIGHT" ),
                new KEYSet( 1, 7, "LEFT" ),
                new KEYSet( 3, 0, "THRUST" ),
                new KEYSet( 3, 1, "MAX THRUST" ),
                new KEYSet( -1 )
        };

        /* These are the rev 2 dipswitch settings. MAME has trouble with the FUEL one. */
        static DSW dsw[] =
        {
                new DSW( 2, 0x0c, "LANGUAGE", new String[]{ "ENGLISH", "FRENCH", "SPANISH", "GERMAN" } ),
                new DSW( 2, 0x20, "PLAY", new String[]{ "USE COIN", "FREE PLAY" } ),
                new DSW( 2, 0xc0, "FUEL", new String[]{ "450", "600", "750", "900" } ),
                new DSW( -1 )
        };

        static GfxLayout fakelayout = new GfxLayout
     (
                1,1,
                0,
                1,
                 new int[]{ 0 },
                 new int[]{ 0 },
                 new int[]{ 0 },
                0
        );

        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 0, 0,      fakelayout,     0, 256 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };

        static char color_prom[] =
        {
                0x00,0x00,0x00,	/* BLACK */
                0x01,0x00,0x00, /* RED */
                0x00,0x01,0x00, /* GREEN */
                0x00,0x00,0x01, /* BLUE */
                0x01,0x01,0x00, /* YELLOW */
                0x00,0x01,0x01, /* CYAN */
                0x01,0x00,0x01, /* MAGENTA */
                0x01,0x01,0x01,	/* WHITE */
                0x00,0x00,0x00,	/* BLACK */
                0x01,0x00,0x00, /* RED */
                0x00,0x01,0x00, /* GREEN */
                0x00,0x00,0x01, /* BLUE */
                0x01,0x01,0x00, /* YELLOW */
                0x00,0x01,0x01, /* CYAN */
                0x01,0x00,0x01, /* MAGENTA */
                0x01,0x01,0x01	/* WHITE */};

        static MachineDriver machine_driver = new MachineDriver
       (
                /* basic machine hardware */
                new MachineCPU[] {
                  new MachineCPU(
                                CPU_M6502,
                                1500000,	/* 1.5 Mhz */
                                0,
                                readmem,writemem,null,null,
                                nmi_interrupt,4 /* 4 interrupts per frame? */
                        )
                },
                60, /* frames per second */
                null,

                /* video hardware */
                288, 224, new rectangle( -30, 1050, 0, 900 ),
                gfxdecodeinfo,
                256, 256,
                atari_vg_init_colors,
                VIDEO_TYPE_VECTOR,

                null,
                atari_vg_dvg_start,
                atari_vg_stop,
                atari_vg_screenrefresh,

                /* sound hardware */
                null,
                null,
                null,
                null,
                null
        );



        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr llander_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */           
                ROM_LOAD( "034572.02",    0x6000, 0x0800, 0xb8763eea );
                ROM_LOAD( "034571.02",    0x6800, 0x0800, 0x77da4b2f );
                ROM_LOAD( "034570.01",    0x7000, 0x0800, 0x2724e591 );
                ROM_LOAD( "034569.02",    0x7800, 0x0800, 0x72837a4e );
                ROM_RELOAD(            0xf800, 0x0800 );	/* for reset/interrupt vectors */
                /* Vector ROM */
                ROM_LOAD( "034599.01",    0x4800, 0x0800, 0x355a9371 );
                ROM_LOAD( "034598.01",    0x5000, 0x0800, 0x9c4ffa68 );
                /* This _should_ be the rom for international versions. */
                /* Unfortunately, is it not currently available. */
                ROM_LOAD( "034597.01",    0x5800, 0x0800, 0x00000000 );
                ROM_END();
        }};

        public static GameDriver llander_driver = new GameDriver
        (
                "Lunar Lander",
                "llander",
                "BRAD OLIVER\nAL KOSSOW\nHEDLEY RAINNIE\nERIC SMITH\nALLARD VAN DER BAS\nBERND WIEBELT",
                machine_driver,

                llander_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                color_prom, null, null,
                ORIENTATION_DEFAULT,    /* paused_x, paused_y */

                null, null
        );    
}
