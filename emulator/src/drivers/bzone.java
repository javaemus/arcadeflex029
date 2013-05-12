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
import static machine.bzone.*;
import static sndhrdw.asteroid.*;
import static machine.atari_vg.*;
import static mame.inptport.*;
import static vidhrdw.atari_vg.*;
import static vidhrdw.vector.*;
import static machine.mathbox.*;

public class bzone {
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x03ff, MRA_RAM ),
                new MemoryReadAddress( 0x5000, 0x7fff, MRA_ROM ),
                new MemoryReadAddress( 0x3000, 0x3fff, MRA_ROM ),
                new MemoryReadAddress( 0xf800, 0xffff, MRA_ROM ),	/* for the reset / interrupt vectors */
                new MemoryReadAddress( 0x2000, 0x2fff, MRA_RAM, vectorram ),
                new MemoryReadAddress( 0x0800, 0x0800, bzone_IN0_r ),	/* IN0 */
                new MemoryReadAddress( 0x0a00, 0x0a00, input_port_1_r ),	/* DSW1 */
                new MemoryReadAddress( 0x0c00, 0x0c00, input_port_2_r ),	/* DSW2 */
                new MemoryReadAddress( 0x1828, 0x1828, bzone_IN3_r ),	/* IN3 */
                new MemoryReadAddress( 0x1800, 0x1800, mb_status_r ),
                new MemoryReadAddress( 0x1810, 0x1810, mb_lo_r ),
                new MemoryReadAddress( 0x1818, 0x1818, mb_hi_r ),
                new MemoryReadAddress( 0x1820, 0x182f, pokey1_r ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x03ff, MWA_RAM ),
                new MemoryWriteAddress( 0x2000, 0x2fff, MWA_RAM, vectorram ),
                new MemoryWriteAddress( 0x1820, 0x182f, pokey1_w ),
                new MemoryWriteAddress( 0x1860, 0x187f, mb_go ),
                new MemoryWriteAddress( 0x1200, 0x1200, atari_vg_go ),
                new MemoryWriteAddress( 0x1000, 0x1000, MWA_NOP ), /* coin out */
                new MemoryWriteAddress( 0x1400, 0x1400, MWA_NOP ), /* watchdog clear */
                new MemoryWriteAddress( 0x1600, 0x1600, vg_reset ),
                new MemoryWriteAddress( 0x1840, 0x1840, MWA_NOP ), /* bzone sound, yet todo */
                new MemoryWriteAddress( 0x5000, 0x7fff, MWA_ROM ),
                new MemoryWriteAddress( 0x3000, 0x3fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static InputPort input_ports[] =
        {
                new InputPort(       /* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_3, OSD_KEY_4, 0, OSD_KEY_F1, OSD_KEY_F2, 0, 0, 0 }
                ),
                new InputPort(       /* DSW1 */
                        0x11,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(       /* DSW2 */
                        0x02,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(       /* IN3 */
                        0x00,
                        new int[]{ OSD_KEY_K, OSD_KEY_I, OSD_KEY_S, OSD_KEY_W, OSD_KEY_SPACE, OSD_KEY_1, OSD_KEY_2, 0 }
                ),
                new InputPort(	/* This is just a fake to play BZONE with only one joystick */
                                /* And to get the "directors cut" switch */
                                /* Director's cut (red/green) is activated! */
                        0x80,
                        new int[]{ OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_CONTROL, 0, 0, 0 }
                ),
                new InputPort( -1 )
        };

        static TrakPort trak_ports[] =
        {
                new TrakPort( -1 )
        };


        static KEYSet keys[] =
        {
                new KEYSet( 3, 0, "RIGHT BACKWARD" ),
                new KEYSet( 3, 1, "RIGHT FORWARD" ),
                new KEYSet( 3, 2, "LEFT BACKWARD" ),
                new KEYSet( 3, 3, "LEFT FORWARD" ),
                new KEYSet( 3, 4, "FIRE" ),
                new KEYSet( 4, 0, "UP" ),
                new KEYSet( 4, 1, "DOWN" ),
                new KEYSet( 4, 2, "TURN LEFT" ),
                new KEYSet( 4, 3, "TURN RIGHT" ),
                new KEYSet( 4, 4, "FIRE" ),
                new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                new DSW( 1, 0x03, "LIVES", new String[]{ "2", "3", "4", "5" } ),
                new DSW( 1, 0x0c, "MISSILE APPEARS AT", new String[]{ "5000", "10000", "20000", "30000" } ),
                new DSW( 1, 0x30, "BONUS TANK", new String[]{ "NONE", "15K AND 100K", "20K AND 100K", "50K AND 100K" } ),
                new DSW( 1, 0xc0, "LANGUAGE", new String[]{ "ENGLISH", "GERMAN", "FRENCH", "SPANISH" } ),
                new DSW( 4, 0x80, "COLORS", new String[]{ "GREENISH", "DIRECTOR'S CUT" } ),
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
            0x00,0x01,0x00, /* GREEN */
            0x00,0x01,0x01, /* CYAN */
            0x00,0x00,0x01, /* BLUE */
            0x01,0x00,0x00, /* RED */
            0x01,0x00,0x01, /* MAGENTA */
            0x01,0x01,0x00, /* YELLOW */
            0x01,0x01,0x01,	/* WHITE */
            0x00,0x00,0x00,	/* BLACK */
            0x00,0x01,0x00, /* GREEN */
            0x00,0x01,0x01, /* CYAN */
            0x00,0x00,0x01, /* BLUE */
            0x01,0x00,0x00, /* RED */
            0x01,0x00,0x01, /* MAGENTA */
            0x01,0x01,0x00, /* YELLOW */
            0x01,0x01,0x01,	/* WHITE */
            0x00,0x00,0x00	/* BLACK */
        };



        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name)
        {
                /* check if the hi score table has already been initialized */
                if (memcmp(RAM,0x0300,new char[]{0x05,0x00,0x00},3) == 0 &&
                                memcmp(RAM,0x0339,new char[]{0x22,0x28,0x38},3) == 0)
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x0300,1,6*10,f);
                                fclose(f);
                        }

                        return 1;
                }
                else return 0;	/* we can't load the hi scores yet */
        }};


        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name)
        {
                FILE f;


                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x0300,1,6*10,f);
                        fclose(f);
                }
        }};

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
                288, 224, new rectangle( 0, 480, 0, 400 ),
                gfxdecodeinfo,
                256, 256,
                atari_vg_init_colors,
                VIDEO_TYPE_VECTOR,

                null,
                atari_vg_avg_start,
                atari_vg_stop,
                atari_vg_screenrefresh,

                /* sound hardware */
                null,
                null,
                pokey1_sh_start,
                pokey_sh_stop,
                pokey_sh_update
        );


        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr bzone_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "036414.01", 0x5000, 0x0800, 0xc40b04fb );
                ROM_LOAD( "036413.01", 0x5800, 0x0800, 0x9f3aa956 );
                ROM_LOAD( "036412.01", 0x6000, 0x0800, 0x5c3bda25 );
                ROM_LOAD( "036411.01", 0x6800, 0x0800, 0xa40bfa05 );
                ROM_LOAD( "036410.01", 0x7000, 0x0800, 0x364b14eb );
                ROM_LOAD( "036409.01", 0x7800, 0x0800, 0x7a21b649 );
                ROM_RELOAD(            0xf800, 0x0800 );	/* for reset/interrupt vectors */
                /* Mathbox ROMs */
                ROM_LOAD( "036422.01", 0x3000, 0x0800, 0x5c8342bd );
                ROM_LOAD( "036421.01", 0x3800, 0x0800, 0x16a742bd );
                ROM_END();
        }};
        static RomLoadPtr bzone2_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "036414a.01", 0x5000, 0x0800, 0x1fe91ce3 );
                ROM_LOAD( "036413.01", 0x5800, 0x0800, 0x9f3aa956 );
                ROM_LOAD( "036412.01", 0x6000, 0x0800, 0x5c3bda25 );
                ROM_LOAD( "036411.01", 0x6800, 0x0800, 0xa40bfa05 );
                ROM_LOAD( "036410.01", 0x7000, 0x0800, 0x364b14eb );
                ROM_LOAD( "036409.01", 0x7800, 0x0800, 0x7a21b649 );
                ROM_RELOAD(            0xf800, 0x0800 );	/* for reset/interrupt vectors */
                /* Mathbox ROMs */
                ROM_LOAD( "036422.01", 0x3000, 0x0800, 0x5c8342bd );
                ROM_LOAD( "036421.01", 0x3800, 0x0800, 0x16a742bd );
                ROM_END();
        }};

        public static GameDriver bzone_driver = new GameDriver
        (
                "Battle Zone",
                "bzone",
                "BRAD OLIVER\nAL KOSSOW\nHEDLEY RAINNIE\nERIC SMITH\nALLARD VAN DER BAS\nBERND WIEBELT\nMAURO MINENNA\n",
                machine_driver,

                bzone_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                color_prom, null, null,
                ORIENTATION_DEFAULT,      /* paused_x, paused_y */

                hiload, hisave
        );

        public static GameDriver bzone2_driver = new GameDriver
        (
                "Battle Zone (alternate version)",
                "bzone2",
                "BRAD OLIVER\nAL KOSSOW\nHEDLEY RAINNIE\nERIC SMITH\nALLARD VAN DER BAS\nBERND WIEBELT",
                machine_driver,

                bzone2_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                color_prom, null, null,
                ORIENTATION_DEFAULT,    /* paused_x, paused_y */

                hiload, hisave
        ); 
}
