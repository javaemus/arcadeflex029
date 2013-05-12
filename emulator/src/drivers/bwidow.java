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
import static machine.spacduel.*;
import static machine.bzone.*;
import static mame.inptport.*;
import static sndhrdw.asteroid.*;
import static machine.atari_vg.*;
import static vidhrdw.atari_vg.*;
import static vidhrdw.vector.*;

public class bwidow {
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x07ff, MRA_RAM ),
                new MemoryReadAddress( 0x9000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( 0x2800, 0x5fff, MRA_ROM ),
                new MemoryReadAddress( 0x2000, 0x27ff, MRA_RAM, vectorram ),
                new MemoryReadAddress( 0x7000, 0x7000, atari_vg_earom_r ),
                new MemoryReadAddress( 0x7800, 0x7800, bzone_IN0_r ),	/* IN0 */
                new MemoryReadAddress( 0x6008, 0x6008, input_port_1_r ),	/* DSW1 */
                new MemoryReadAddress( 0x6808, 0x6808, input_port_2_r ),	/* DSW2 */
                new MemoryReadAddress( 0x8000, 0x8000, input_port_3_r ),	/* IN1 */
                new MemoryReadAddress( 0x8800, 0x8800, input_port_4_r ),	/* IN1 */
                new MemoryReadAddress( 0x6000, 0x600f, pokey1_r ),
                new MemoryReadAddress( 0x6800, 0x680f, pokey2_r ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x07ff, MWA_RAM ),
                new MemoryWriteAddress( 0x2000, 0x27ff, MWA_RAM, vectorram ),
                new MemoryWriteAddress( 0x6000, 0x67ff, pokey1_w ),
                new MemoryWriteAddress( 0x6800, 0x6fff, pokey2_w ),
                new MemoryWriteAddress( 0x8800, 0x8800, MWA_NOP ), /* coin out */
                new MemoryWriteAddress( 0x8840, 0x8840, atari_vg_go ),
                new MemoryWriteAddress( 0x8880, 0x8880, vg_reset ),
                new MemoryWriteAddress( 0x88c0, 0x88c0, MWA_NOP ), /* interrupt acknowledge */
                new MemoryWriteAddress( 0x8900, 0x8900, atari_vg_earom_ctrl ),
                new MemoryWriteAddress( 0x8980, 0x89ed, MWA_NOP ), /* watchdog clear */
                new MemoryWriteAddress( 0x8940, 0x897f, atari_vg_earom_w ),
                new MemoryWriteAddress( 0x9000, 0xffff, MWA_ROM ),
                new MemoryWriteAddress( 0x2800, 0x5fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };

        static MemoryReadAddress spacduel_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x03ff, MRA_RAM ),
                new MemoryReadAddress( 0x4000, 0x8fff, MRA_ROM ),
                new MemoryReadAddress( 0xf000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( 0x2800, 0x3fff, MRA_ROM ),
                new MemoryReadAddress( 0x2000, 0x27ff, MRA_RAM, vectorram ),
                new MemoryReadAddress( 0x0a00, 0x0a00, atari_vg_earom_r ),
                new MemoryReadAddress( 0x0800, 0x0800, bzone_IN0_r ),	/* IN0 */
                new MemoryReadAddress( 0x1008, 0x1008, input_port_1_r ),	/* DSW1 */
                new MemoryReadAddress( 0x1408, 0x1408, input_port_2_r ),	/* DSW2 */
                new MemoryReadAddress( 0x0900, 0x0907, spacduel_IN3_r ),	/* IN1 */
                new MemoryReadAddress( 0x0900, 0x0907, input_port_3_r ),	/* IN1 */
                new MemoryReadAddress( 0x1000, 0x100f, pokey1_r ),
                new MemoryReadAddress( 0x1400, 0x140f, pokey2_r ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress spacduel_writemem[] =
        {
                 new MemoryWriteAddress( 0x0000, 0x03ff, MWA_RAM ),
                 new MemoryWriteAddress( 0x2000, 0x27ff, MWA_RAM, vectorram ),
                 new MemoryWriteAddress( 0x1000, 0x13ff, pokey1_w ),
                 new MemoryWriteAddress( 0x1400, 0x17ff, pokey2_w ),
                 new MemoryWriteAddress( 0x0905, 0x0906, MWA_NOP ), /* ignore? */
                 new MemoryWriteAddress( 0x0c00, 0x0c00, MWA_NOP ), /* coin out */
                 new MemoryWriteAddress( 0x0c80, 0x0c80, atari_vg_go ),
                 new MemoryWriteAddress( 0x0d00, 0x0d00, MWA_NOP ), /* watchdog clear */
                 new MemoryWriteAddress( 0x0d80, 0x0d80, vg_reset ),
                 new MemoryWriteAddress( 0x0e00, 0x0e00, MWA_NOP ), /* interrupt acknowledge */
                 new MemoryWriteAddress( 0x0e80, 0x0e80, atari_vg_earom_ctrl ),
                 new MemoryWriteAddress( 0x0f00, 0x0f3f, atari_vg_earom_w ),
                 new MemoryWriteAddress( 0x4000, 0x8fff, MWA_ROM ),
                 new MemoryWriteAddress( 0x2800, 0x3fff, MWA_ROM ),
                 new MemoryWriteAddress( 0xf000, 0xffff, MWA_ROM ),
                 new MemoryWriteAddress( -1 )	/* end of table */
        };



        static InputPort bwidow_input_ports[] =
        {
                 new InputPort(       /* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_3, OSD_KEY_4, 0, OSD_KEY_F1, OSD_KEY_F2, 0, 0, 0 }
                ),
                new InputPort(       /* DSW1 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(       /* DSW2 */
                        0x11,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(       /* IN1 */
                        0xff,
                        new int[]{ OSD_KEY_F, OSD_KEY_S, OSD_KEY_D, OSD_KEY_E,
                                0, OSD_KEY_1, OSD_KEY_2, 0 }
                ),
                new InputPort(       /* IN2 */
                        0xff,
                        new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_DOWN, OSD_KEY_UP,
                                0, OSD_KEY_1, OSD_KEY_2, 0 }
                ),
                new InputPort( -1 )
        };

        static TrakPort trak_ports[] =
        {
                new TrakPort( -1 )
        };


        static KEYSet bwidow_keys[] =
        {
                new KEYSet( 3, 0, "MOVE RIGHT" ),
                new KEYSet( 3, 1, "MOVE LEFT" ),
                new KEYSet( 3, 2, "MOVE DOWN" ),
                new KEYSet( 3, 3, "MOVE UP" ),
                new KEYSet( 4, 0, "FIRE RIGHT" ),
                new KEYSet( 4, 1, "FIRE LEFT" ),
                new KEYSet( 4, 2, "FIRE DOWN" ),
                new KEYSet( 4, 3, "FIRE UP" ),
                new KEYSet( -1 )
        };


        static DSW bwidow_dsw[] =
        {
                new DSW( 1, 0x03, "COINS", new String[]{ "1 COIN 1 CREDIT", "2 COINS 1 CREDIT", "FREE PLAY", "1 COIN 2 CREDITS" } ),
                new DSW( 2, 0x03, "MAX START", new String[]{ "LEVEL 13", "LEVEL 21", "LEVEL 37", "LEVEL 53" } ),
                new DSW( 2, 0x0c, "LIVES", new String[]{ "3", "4", "5", "6" } ),
                new DSW( 2, 0x30, "DIFFICULTY", new String[]{ "EASY", "MEDIUM", "HARD", "DEMO" } ),
                new DSW( 2, 0xc0, "BONUS", new String[]{ "20000", "30000", "40000", "NONE" } ),
                new DSW( -1 )
        };

        static InputPort gravitar_input_ports[] =
        {
                 new InputPort(       /* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_3, OSD_KEY_4, 0, OSD_KEY_F1, OSD_KEY_F2, 0, 0, 0 }
                ),
                new InputPort(       /* DSW1 */
                        0x13,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(       /* DSW2 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(       /* IN1 */
                        0xff,
                        new int[]{ OSD_KEY_ALT, OSD_KEY_CONTROL, OSD_KEY_RIGHT, OSD_KEY_LEFT,
                                OSD_KEY_UP, OSD_KEY_1, OSD_KEY_2, 0 }
                ),
                new InputPort(       /* IN2 */
                        0xff,
                        new int[]{ OSD_KEY_ALT, OSD_KEY_CONTROL, OSD_KEY_RIGHT, OSD_KEY_LEFT,
                                OSD_KEY_UP, OSD_KEY_1, OSD_KEY_2, 0 }
                ),
                new InputPort( -1 )
        };

        static KEYSet gravitar_keys[] =
        {
                 new KEYSet( 3, 0, "P1 SHIELD" ),
                 new KEYSet( 3, 1, "P1 FIRE" ),
                 new KEYSet( 3, 2, "P1 RIGHT" ),
                 new KEYSet( 3, 3, "P1 LEFT" ),
                 new KEYSet( 3, 4, "P1 THRUST" ),
                 new KEYSet( 4, 0, "P2 SHIELD" ),
                 new KEYSet( 4, 1, "P2 FIRE" ),
                 new KEYSet( 4, 2, "P2 RIGHT" ),
                 new KEYSet( 4, 3, "P2 LEFT" ),
                 new KEYSet( 4, 4, "P2 THRUST" ),
                 new KEYSet( -1 )
        };

        static DSW gravitar_dsw[] =
        {
                new DSW( 2, 0x03, "COINS", new String[]{ "1 COIN 1 CREDIT", "2 COINS 1 CREDIT", "FREE PLAY", "1 COIN 2 CREDITS" } ),
                new DSW( 1, 0xc0, "BONUS", new String[]{ "10000", "20000", "30000", "NONE" } ),
                new DSW( 1, 0x10, "DIFFICULTY", new String[]{ "HARD", "EASY" } ),
                new DSW( 1, 0x0c, "LIVES", new String[]{ "3", "4", "5", "6" } ),
                new DSW( -1 )
        };

        static InputPort spacduel_input_ports[] =
        {
                new InputPort(       /* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_3, OSD_KEY_4, 0, OSD_KEY_F1, OSD_KEY_F2, 0, 0, 0 }
                ),
                new InputPort(       /* DSW1 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(       /* DSW2 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(
                /* These next 2 are shared between 7 memory locations. See machine/spacduel.c for more */
                       /* IN1 - player 1 controls */
                        0x00,
                        new int[]{ OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_CONTROL, OSD_KEY_ALT,
                                OSD_KEY_UP, OSD_KEY_1, OSD_KEY_2, 0 }
                ),
                new InputPort(       /* IN2  - player 2 controls */
                        0x00,
                        new int[]{ OSD_KEY_A, OSD_KEY_D, OSD_KEY_SPACE, OSD_KEY_S,
                                OSD_KEY_W, 0, 0, 0 }
                ),
                new InputPort( -1 )
        };

        static KEYSet spacduel_keys[] =
        {
                new KEYSet( 3, 0, "P1 LEFT" ),
                new KEYSet( 3, 1, "P1 RIGHT" ),
                new KEYSet( 3, 2, "P1 FIRE" ),
                new KEYSet( 3, 3, "P1 SHIELD" ),
                new KEYSet( 3, 4, "P1 THRUST" ),
                new KEYSet( 4, 0, "P2 LEFT" ),
                new KEYSet( 4, 1, "P2 RIGHT" ),
                new KEYSet( 4, 2, "P2 FIRE" ),
                new KEYSet( 4, 3, "P2 SHIELD" ),
                new KEYSet( 4, 4, "P2 THRUST" ),
                new KEYSet( -1 )
        };

        static DSW spacduel_dsw[] =
        {
                new DSW( 1, 0x03, "COINS", new String[]{ "1 COIN 1 CREDIT", "2 COINS 1 CREDIT", "FREE PLAY", "1 COIN 2 CREDIT" } ),
                new DSW( 2, 0xc0, "BONUS", new String[]{ "10000", "15000", "8000", "NONE" } ),
                new DSW( 2, 0x30, "LANGUAGE", new String[]{ "ENGLISH", "GERMAN", "FRENCH", "SPANISH" } ),
                new DSW( 2, 0x0c, "DIFFICULTY", new String[]{ "NORMAL", "EASY", "HARD", "MEDIUM" } ),
                new DSW( 2, 0x03, "LIVES", new String[]{ "4", "3", "6", "5" } ),
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
                0x00,0x00,0x00, /* BLACK */
                0x00,0x00,0x01, /* BLUE */
                0x00,0x01,0x00, /* GREEN */
                0x00,0x01,0x01, /* CYAN */
                0x01,0x00,0x00, /* RED */
                0x01,0x00,0x01, /* MAGENTA */
                0x01,0x01,0x00, /* YELLOW */
                0x01,0x01,0x01,	/* WHITE */
                0x00,0x00,0x00, /* BLACK */
                0x00,0x00,0x01, /* BLUE */
                0x00,0x01,0x00, /* GREEN */
                0x00,0x01,0x01, /* CYAN */
                0x01,0x00,0x00, /* RED */
                0x01,0x00,0x01, /* MAGENTA */
                0x01,0x01,0x00, /* YELLOW */
                0x01,0x01,0x01	/* WHITE */};

        static MachineDriver bwidow_machine_driver = new MachineDriver
        (
                /* basic machine hardware */
                new MachineCPU[] {
                new MachineCPU(
                                CPU_M6502,
                                1500000,	/* 1.5 Mhz */
                                0,
                                readmem,writemem,null,null,
                                interrupt,4 /* 1 interrupt per frame? */
                        )
                },
                60, /* frames per second */
                null,

                /* video hardware */
                288, 224, new rectangle( 0, 480, 0, 440 ),
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
                pokey2_sh_start,
                pokey_sh_stop,
                pokey_sh_update
        );
        static MachineDriver gravitar_machine_driver = new MachineDriver
        (
                /* basic machine hardware */
                 new MachineCPU[] {
                new MachineCPU(
                                CPU_M6502,
                                1500000,	/* 1.5 Mhz */
                                0,
                                readmem,writemem,null,null,
                                interrupt,4 /* 1 interrupt per frame? */
                        )
                },
                60, /* frames per second */
                null,

                /* video hardware */
                288, 224, new rectangle( 0, 420, 0, 400 ),
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
                pokey2_sh_start,
                pokey_sh_stop,
                pokey_sh_update
        );
        static MachineDriver spacduel_machine_driver = new MachineDriver
        (

                /* basic machine hardware */
                new MachineCPU[] {
                new MachineCPU(
                                CPU_M6502,
                                1500000,	/* 1.5 Mhz */
                                0,
                                spacduel_readmem,spacduel_writemem,null,null,
                                interrupt,4 /* 1 interrupt per frame? */
                        )
                },
                60, /* frames per second */
                null,

                /* video hardware */
                288, 224, new rectangle( 0, 540, 0, 400 ),
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
                pokey2_sh_start,
                pokey_sh_stop,
                pokey_sh_update
        );



        /***************************************************************************

          Game driver(s)

        ***************************************************************************/

        /* Black Widow cannot use the the earom routines
         * She writes into some locations at $2fac-$2fd7, which is clearly
         * the vector rom. Perhaps there is some address-logic that is not yet
         * emulated
         */

        static HiscoreLoadPtr bwidow_hiload = new HiscoreLoadPtr() { public int handler(String name)
        {
                /* check if the hi score table has already been initialized */
                if (memcmp(RAM,0x0310,new char[]{0x00,0x00,0x00},3) == 0 &&
                                memcmp(RAM,0x03a0,new char[]{0x01,0x01,0x11},3) == 0)
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x0310,1,7*21,f);
                                fclose(f);
                        }

                        return 1;
                }
                else return 0;  /* we can't load the hi scores yet */
        }};
        static HiscoreSavePtr bwidow_hisave = new HiscoreSavePtr() { public void handler(String name)
        {
                FILE f;


                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x0310,1,7*21,f);
                        fclose(f);
                }
        }};

        static RomLoadPtr bwidow_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                /* Vector ROM */
                ROM_LOAD( "136017.107",  0x2800, 0x0800, 0x77a524db );
                ROM_LOAD( "136017.108",  0x3000, 0x1000, 0xf741f7f7 );
                ROM_LOAD( "136017.109",  0x4000, 0x1000, 0xa9854243 );
                ROM_LOAD( "136017.110",  0x5000, 0x1000, 0x46b9d3d1 );
                /* Program ROM */
                ROM_LOAD( "136017.101",  0x9000, 0x1000, 0xd30d0201 );
                ROM_LOAD( "136017.102",  0xa000, 0x1000, 0x68115551 );
                ROM_LOAD( "136017.103",  0xb000, 0x1000, 0x82fc6164 );
                ROM_LOAD( "136017.104",  0xc000, 0x1000, 0xcaf00204 );
                ROM_LOAD( "136017.105",  0xd000, 0x1000, 0x71d02f2a );
                ROM_LOAD( "136017.106",  0xe000, 0x1000, 0x7db99991 );
                ROM_RELOAD(              0xf000, 0x1000 );	/* for reset/interrupt vectors */
                ROM_END();
        }};

        public static GameDriver bwidow_driver = new GameDriver
        (
                "Black Widow",
                "bwidow",
                "BRAD OLIVER\nAL KOSSOW\nHEDLEY RAINNIE\nERIC SMITH\n,ALLARD VAN DER BAS\nBERND WIEBELT",
                bwidow_machine_driver,

                bwidow_rom,
                null, null,
                null,

                bwidow_input_ports,null, trak_ports, bwidow_dsw, bwidow_keys,

                color_prom,null,null,
                ORIENTATION_DEFAULT,     /* paused_x, paused_y */

                bwidow_hiload, bwidow_hisave
        );

        /***************************************************************************

          Game driver(s)

        ***************************************************************************/

        /* Gravitar now uses the earom routines
         * However, we keep the highscore location, just in case
         *		fwrite(&RAM[0x041e],1,3*16,f);
         */
        static RomLoadPtr gravitar_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                /* Vector ROM */
                ROM_LOAD( "136010.210",   0x2800, 0x0800, 0xdebcb243 );
                ROM_LOAD( "136010.207",   0x3000, 0x1000, 0x4135629a );
                ROM_LOAD( "136010.208",   0x4000, 0x1000, 0x358f25d9 );
                ROM_LOAD( "136010.309",   0x5000, 0x1000, 0x4ac78df4 );
                /* Program ROM */
                ROM_LOAD( "136010.301",   0x9000, 0x1000, 0xa2a55013 );
                ROM_LOAD( "136010.302",   0xa000, 0x1000, 0xd3700b3c );
                ROM_LOAD( "136010.303",   0xb000, 0x1000, 0x8e12e3e0 );
                ROM_LOAD( "136010.304",   0xc000, 0x1000, 0x467ad5da );
                ROM_LOAD( "136010.305",   0xd000, 0x1000, 0x840603af );
                ROM_LOAD( "136010.306",   0xe000, 0x1000, 0x3f3805ad );
                ROM_RELOAD(              0xf000, 0x1000 );
                
                ROM_END();
        }};

        public static GameDriver gravitar_driver = new GameDriver
        (
                "Gravitar",
                "gravitar",
                "BRAD OLIVER\nAL KOSSOW\nHEDLEY RAINNIE\nERIC SMITH\nALLARD VAN DER BAS\nBERND WIEBELT",
                gravitar_machine_driver,

                gravitar_rom,
                null, null,
                null,

                gravitar_input_ports,null, trak_ports, gravitar_dsw, gravitar_keys,

                color_prom, null, null,
                ORIENTATION_DEFAULT,     /* paused_x, paused_y */

                atari_vg_earom_load, atari_vg_earom_save
        );

        /***************************************************************************

          Game driver(s)

        ***************************************************************************/

        /* Space Duel now uses the earom routines
         * However, we keep the highscore location, just in case
         *	fwrite(&RAM[0x00dd],1,3*20+3*25,f);
         */
        static RomLoadPtr spacduel_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                /* Vector ROM */
                ROM_LOAD( "136006.106",  0x2800, 0x0800, 0x015b926d );
                ROM_LOAD( "136006.107",  0x3000, 0x1000, 0xfdff3939 );
                /* Program ROM */
                ROM_LOAD( "136006.201",  0x4000, 0x1000, 0x76179c9d );
                ROM_LOAD( "136006.102",  0x5000, 0x1000, 0x3e245c5e );
                ROM_LOAD( "136006.103",  0x6000, 0x1000, 0xdfc2e1e2 );
                ROM_LOAD( "136006.104",  0x7000, 0x1000, 0x1a240e0a );
                ROM_LOAD( "136006.105",  0x8000, 0x1000, 0xd3ccbbbe );
                ROM_RELOAD(              0x9000, 0x1000 );
                ROM_RELOAD(              0xa000, 0x1000 );
                ROM_RELOAD(              0xb000, 0x1000 );
                ROM_RELOAD(              0xc000, 0x1000 );
                ROM_RELOAD(              0xd000, 0x1000 );
                ROM_RELOAD(              0xe000, 0x1000 );
                ROM_RELOAD(              0xf000, 0x1000 );	/* for reset/interrupt vectors */
                ROM_END();
        }};
        public static GameDriver spacduel_driver = new GameDriver
        (
                "Space Duel",
                "spacduel",
                "BRAD OLIVER\nAL KOSSOW\nHEDLEY RAINNIE\nERIC SMITH\nALLARD VAN DER BAS\nBERND WIEBELT",
                spacduel_machine_driver,

                spacduel_rom,
                null, null,
                null,

                spacduel_input_ports,null, trak_ports, spacduel_dsw, spacduel_keys,

                color_prom, null, null,
                ORIENTATION_DEFAULT,     /* paused_x, paused_y */

                atari_vg_earom_load, atari_vg_earom_save
        );    
}
