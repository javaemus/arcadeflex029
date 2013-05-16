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
 *  asteroi2 is asteroi1
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
import static sndhrdw.pokeyintf.*;
import static machine.asteroid.*;
import static sndhrdw.asteroid.*;
import static machine.atari_vg.*;
import static vidhrdw.atari_vg.*;
import static vidhrdw.vector.*;
import static mame.memoryH.*;

public class asteroid 
{
         static  MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x03ff, MRA_RAM ),
                new MemoryReadAddress( 0x6800, 0x7fff, MRA_ROM ),
                new MemoryReadAddress( 0x5000, 0x57ff, MRA_ROM ),
                new MemoryReadAddress( 0xf800, 0xffff, MRA_ROM ),	/* for the reset / interrupt vectors */
                new MemoryReadAddress( 0x4000, 0x47ff, MRA_RAM, vectorram ),
                new MemoryReadAddress( 0x2000, 0x2007, asteroid_IN0_r ),	/* IN0 */
                new MemoryReadAddress( 0x2400, 0x2407, asteroid_IN1_r ),	/* IN1 */
                new MemoryReadAddress( 0x2800, 0x2803, asteroid_DSW1_r ),	/* DSW1 */
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x03ff, MWA_RAM ),
                new MemoryWriteAddress( 0x4000, 0x47ff, MWA_RAM, vectorram ),
                new MemoryWriteAddress( 0x3000, 0x3000, atari_vg_go ),
                new MemoryWriteAddress( 0x3200, 0x3200, asteroid_bank_switch_w ),
                new MemoryWriteAddress( 0x3400, 0x3400, MWA_NOP ), /* watchdog clear */
                new MemoryWriteAddress( 0x3600, 0x3600, asteroid_explode_w ),
                new MemoryWriteAddress( 0x3a00, 0x3a00, asteroid_thump_w ),
                new MemoryWriteAddress( 0x3c00, 0x3c05, asteroid_sounds_w ),
                new MemoryWriteAddress( 0x6800, 0x7fff, MWA_ROM ),
                new MemoryWriteAddress( 0x5000, 0x57ff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };

        static MemoryReadAddress astdelux_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x03ff, MRA_RAM ),
                new MemoryReadAddress( 0x6000, 0x7fff, MRA_ROM ),
                new MemoryReadAddress( 0x4800, 0x57ff, MRA_ROM ),
                new MemoryReadAddress( 0xf800, 0xffff, MRA_ROM ),	/* for the reset / interrupt vectors */
                new MemoryReadAddress( 0x4000, 0x47ff, MRA_RAM, vectorram ),
                new MemoryReadAddress( 0x2000, 0x2007, asteroid_IN0_r ),	/* IN0 */
                new MemoryReadAddress( 0x2400, 0x2407, asteroid_IN1_r ),	/* IN1 */
                new MemoryReadAddress( 0x2800, 0x2803, asteroid_DSW1_r ),	/* DSW1 */
                new MemoryReadAddress( 0x2c08, 0x2c08, input_port_3_r ),		/* DSW2 */
                new MemoryReadAddress( 0x2c00, 0x2c0f, pokey1_r ),
                new MemoryReadAddress( 0x2c40, 0x2c7f, atari_vg_earom_r ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress astdelux_writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x03ff, MWA_RAM ),
                new MemoryWriteAddress( 0x4000, 0x47ff, MWA_RAM, vectorram ),
                new MemoryWriteAddress( 0x3000, 0x3000, atari_vg_go ),
                new MemoryWriteAddress( 0x2c00, 0x2c0f, pokey1_w),
                new MemoryWriteAddress( 0x3200, 0x323f, atari_vg_earom_w ),
        /*	{ 0x3400, 0x3400, wdclr }, */
                new MemoryWriteAddress( 0x3600, 0x3600, asteroid_explode_w ),
                new MemoryWriteAddress( 0x3800, 0x3800, vg_reset ),
                new MemoryWriteAddress( 0x3a00, 0x3a00, atari_vg_earom_ctrl ),
                new MemoryWriteAddress( 0x3c00, 0x3c07, astdelux_bank_switch_w ),
                new MemoryWriteAddress( 0x6000, 0x7fff, MWA_ROM ),
                new MemoryWriteAddress( 0x4800, 0x57ff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static InputPort input_ports[] =
        {
                new InputPort(       /* IN0 */
                        0x00,
                        new int[]{ 0, 0, 0, OSD_KEY_ALT, OSD_KEY_CONTROL, 0, OSD_KEY_F2, OSD_KEY_F1 }
                ),
                new InputPort(       /* IN1 */
                        0x07,
                        new int[]{ OSD_KEY_3, 0, 0, OSD_KEY_1, OSD_KEY_2, OSD_KEY_UP, OSD_KEY_RIGHT, OSD_KEY_LEFT }
                ),
                new InputPort(       /* DSW1 */
                        0x80,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort( -1 )
        };

        static InputPort astdelux_input_ports[] =
        {
                new InputPort(       /* IN0 */
                        0x00,
                        new int[]{ 0, 0, 0, OSD_KEY_ALT, OSD_KEY_CONTROL, 0, OSD_KEY_F2, OSD_KEY_F1 }
                ),
                new InputPort(       /* IN1 */
                        0x07,
                        new int[]{ OSD_KEY_3, 0, 0, OSD_KEY_1, OSD_KEY_2, OSD_KEY_UP, OSD_KEY_RIGHT, OSD_KEY_LEFT }
                ),
                new InputPort(       /* DSW1 */
                        0x04,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(       /* DSW2 - Asteroids Deluxe only */
                        0x9d,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort( -1 )
        };

        static TrakPort trak_ports[] =
        {
                new TrakPort( -1 )
        };

        static KEYSet keys[] =
        {
                new KEYSet( 0, 3, "HYPERSPACE OR SHIELD" ),
                new KEYSet( 0, 4, "FIRE" ),
                new KEYSet( 1, 5, "THRUST" ),
                new KEYSet( 1, 6, "RIGHT" ),
                new KEYSet( 1, 7, "LEFT" ),
                new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                 new DSW( 2, 0x03, "LANGUAGE", new String[]{ "ENGLISH", "GERMAN", "FRENCH", "SPANISH" } ),
                 new DSW( 2, 0x04, "SHIPS", new String[]{ "4", "3" } ),
                 new DSW( 2, 0xc0, "COIN", new String[]{ "FREE", "1 COIN 2 PLAY", "1 COIN 1 PLAY", "2 COIN 1 PLAY" } ),
                 new DSW( -1 )
        };

        static DSW astdelux_dsw[] =
        {
                 new DSW( 2, 0x03, "LANGUAGE", new String[]{ "ENGLISH", "GERMAN", "FRENCH", "SPANISH" } ),
                 new DSW( 2, 0x0c, "SHIPS",new String[] { "2 TO 4", "3 TO 5", "4 TO 6", "5 TO 7" } ),
                 new DSW( 2, 0x10, "PLAY", new String[]{ "MINIMUM 1", "MINIMUM 2" } ),
                 new DSW( 2, 0xc0, "BONUS SHIP", new String[]{ "10K", "12K", "15K", "NONE" } ),
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
            0x00,0x00,0x01, /* BLUE */
            0x00,0x01,0x00, /* GREEN */
            0x00,0x01,0x02, /* CYAN */
            0x01,0x00,0x00, /* RED */
            0x01,0x00,0x01, /* MAGENTA */
            0x01,0x01,0x00, /* YELLOW */
            0x01,0x01,0x01,	/* WHITE */
            0x00,0x00,0x00,	/* BLACK */
            0x00,0x00,0x01, /* BLUE */
            0x00,0x01,0x00, /* GREEN */
            0x00,0x01,0x02, /* CYAN */
            0x01,0x00,0x00, /* RED */
            0x01,0x00,0x01, /* MAGENTA */
            0x01,0x01,0x00, /* YELLOW */
            0x01,0x01,0x01	/* WHITE */
        };
        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name)
        {
                /* check if the hi score table has already been initialized */
                if (memcmp(RAM,0x001d,new char[]{0x00,0x00},2) == 0 &&
                                memcmp(RAM,0x0050,new char[]{0x00,0x00},2) == 0 &&
                                memcmp(RAM,0x0032,new char[]{0xff,0xff},2) == 0)
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x001d,1,2*10+3*11,f);
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
                        fwrite(RAM,0x001d,1,2*10+3*11,f);
                        fclose(f);
                }
         }};

        /* Asteroids Deluxe now uses the earom routines
         * However, we keep the highscore location, just in case
         *		fwrite(&RAM[0x0023],1,3*10+3*11,f);
         */
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
                asteroid_init_machine,

                /* video hardware */
                288, 224, new rectangle( -20, 1020, 70, 950 ),
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
                asteroid_sh_update
        );
        static MachineDriver astdelux_machine_driver = new MachineDriver
        (

                /* basic machine hardware */
                new MachineCPU[] {
	            new MachineCPU(
                                CPU_M6502,
                                1500000,	/* 1.5 Mhz */
                                0,
                                astdelux_readmem,astdelux_writemem,null,null,
                                nmi_interrupt,4 /* 4 interrupts per frame? */
                        )
                },
                60, /* frames per second */
                asteroid_init_machine,

                /* video hardware */
                288, 224, new rectangle( -20, 1020, 70, 950 ),
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
                pokey1_sh_start,
                pokey_sh_stop,
                pokey_sh_update
        );

        /***************************************************************************

          Game driver(s)

        ***************************************************************************/

        static String asteroid_sample_names[] =
        {
                "thumphi.sam",
                "thumplo.sam",
                "fire.sam",
                "lsaucer.sam",
                "ssaucer.sam",
                "thrust.sam",
                "sfire.sam",
                "life.sam",
                "explode1.sam",
                "explode2.sam",
                "explode3.sam",
                null	/* end of array */
        };
        static RomLoadPtr asteroid_rom= new RomLoadPtr(){ public void handler()  
        {                
                ROM_REGION( 0x10000);	/* 64k for code */
                ROM_LOAD( "035145.02",    0x6800, 0x0800, 0x0cc75459 );
                ROM_LOAD( "035144.02",    0x7000, 0x0800, 0x096ed35c );
                ROM_LOAD( "035143.02",    0x7800, 0x0800, 0x312caa02 );
                ROM_RELOAD(            0xf800, 0x0800 );	/* for reset/interrupt vectors */
                /* Vector ROM */
                ROM_LOAD( "035127.02",    0x5000, 0x0800, 0x8b71fd9e );
                ROM_END();
        }};
        static RomLoadPtr asteroi1_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION( 0x10000);	/* 64k for code */
                ROM_LOAD( "035145.01",    0x6800, 0x0800, 0xe9bfda64 );
                ROM_LOAD( "035144.01",    0x7000, 0x0800, 0xe53c28a9 );
                ROM_LOAD( "035143.01",    0x7800, 0x0800, 0x7d4e3d05 );
                ROM_RELOAD(            0xf800, 0x0800 );	/* for reset/interrupt vectors */
                /* Vector ROM */
                ROM_LOAD( "035127.01",    0x5000, 0x0800, 0x99699366 );
                ROM_END();
        }};


        public static GameDriver asteroid_driver = new GameDriver
        (
                "Asteroids (rev 2)",
                "asteroid",
                "BRAD OLIVER\nAL KOSSOW\nHEDLEY RAINNIE\nERIC SMITH\nALLARD VAN DER BAS\nBERND WIEBELT",
                machine_driver,

                asteroid_rom,
                null, null,
                asteroid_sample_names,

                input_ports,null, trak_ports, dsw, keys,

                color_prom, null, null,
                ORIENTATION_DEFAULT,     /* paused_x, paused_y */

                hiload, hisave
        );
        public static GameDriver asteroi1_driver = new GameDriver
        (
                "Asteroids (rev 1)",
                "asteroi1",
                "BRAD OLIVER\nAL KOSSOW\nHEDLEY RAINNIE\nERIC SMITH\nALLARD VAN DER BAS\nBERND WIEBELT",
                machine_driver,

                asteroi1_rom,
                null, null,
                asteroid_sample_names,

                input_ports,null, trak_ports, dsw, keys,

                color_prom, null, null,
                ORIENTATION_DEFAULT,     /* paused_x, paused_y */

                hiload, hisave
        );

        static RomLoadPtr astdelux_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION( 0x10000 );	/* 64k for code */
                ROM_LOAD( "036430.02",    0x6000, 0x0800, 0xa4d7a525 );
                ROM_LOAD( "036431.02",    0x6800, 0x0800, 0xd4004aae );
                ROM_LOAD( "036432.02",    0x7000, 0x0800, 0x6d720c41 );
                ROM_LOAD( "036433.03",    0x7800, 0x0800, 0x0dcc0be6 );
                ROM_RELOAD(               0xf800, 0x0800 );	/* for reset/interrupt vectors */
                /* Vector ROM */
                ROM_LOAD( "036800.02",    0x4800, 0x0800, 0xbb8cabe1 );
                ROM_LOAD( "036799.01",    0x5000, 0x0800, 0x7d511572 );
                ROM_END();
        }};
        static RomLoadPtr astdelu1_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "036430.01",    0x6000, 0x0800, 0x8f5dabc6 );
                ROM_LOAD( "036431.01",    0x6800, 0x0800, 0x157a8516 );
                ROM_LOAD( "036432.01",    0x7000, 0x0800, 0xfdea913c );
                ROM_LOAD( "036433.02",    0x7800, 0x0800, 0xd8db74e3 );
                ROM_RELOAD(               0xf800, 0x0800 );	/* for reset/interrupt vectors */
                /* Vector ROM */
                ROM_LOAD( "036800.01",    0x4800, 0x0800, 0x3b597407 );
                ROM_LOAD( "036799.01",    0x5000, 0x0800, 0x7d511572 );
                ROM_END();
              
        }};


        public static GameDriver astdelux_driver= new GameDriver
        (
                "Asteroids Deluxe (rev 2)",
                "astdelux",
                "BRAD OLIVER\nAL KOSSOW\nHEDLEY RAINNIE\nERIC SMITH\nALLARD VAN DER BAS\nBERND WIEBELT",
                astdelux_machine_driver,

                astdelux_rom,
                null, null,
                asteroid_sample_names,

                astdelux_input_ports,null, trak_ports, astdelux_dsw, keys,

                color_prom, null, null,
                ORIENTATION_DEFAULT,      /* paused_x, paused_y */

                atari_vg_earom_load, atari_vg_earom_save
        );
        public static GameDriver astdelu1_driver= new GameDriver
        (
                "Asteroids Deluxe (rev 1)",
                "astdelu1",
                "BRAD OLIVER\nAL KOSSOW\nHEDLEY RAINNIE\nERIC SMITH\nALLARD VAN DER BAS\nBERND WIEBELT",
                astdelux_machine_driver,

                astdelu1_rom,
                null, null,
                asteroid_sample_names,

                astdelux_input_ports,null, trak_ports, astdelux_dsw, keys,

                color_prom, null, null,
                ORIENTATION_DEFAULT,     /* paused_x, paused_y */

                atari_vg_earom_load, atari_vg_earom_save
        );
   
}
