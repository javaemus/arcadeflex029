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
import static mame.inptport.*;
import static machine.atari_vg.*;
import static vidhrdw.atari_vg.*;
import static vidhrdw.vector.*;
import static machine.redbaron.*;
import static machine.mathbox.*;
import static mame.memoryH.*;
public class redbaron {
 
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
                new MemoryReadAddress( 0x1818, 0x1818, redbaron_joy_r ),	/* IN3 */
                new MemoryReadAddress( 0x1802, 0x1802, input_port_4_r ),	/* IN4 */
                new MemoryReadAddress( 0x1800, 0x1800, mb_status_r ),
                new MemoryReadAddress( 0x1804, 0x1804, mb_lo_r ),
                new MemoryReadAddress( 0x1806, 0x1806, mb_hi_r ),
                new MemoryReadAddress( 0x1810, 0x181f, pokey1_r ),
                new MemoryReadAddress( 0x1820, 0x185f, atari_vg_earom_r ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x03ff, MWA_RAM ),
                new MemoryWriteAddress( 0x2000, 0x2fff, MWA_RAM, vectorram ),
                new MemoryWriteAddress( 0x1808, 0x1808, redbaron_joyselect ), /* used for sound, too */
                new MemoryWriteAddress( 0x1000, 0x1000, MWA_NOP ), /* coin out */
                new MemoryWriteAddress( 0x180a, 0x180a, MWA_NOP ), /* sound reset, yet todo */
                new MemoryWriteAddress( 0x180c, 0x180c, atari_vg_earom_ctrl ),
                new MemoryWriteAddress( 0x1810, 0x181f, pokey1_w ),
                new MemoryWriteAddress( 0x1820, 0x185f, atari_vg_earom_w ),
                new MemoryWriteAddress( 0x1860, 0x187f, mb_go ),
                new MemoryWriteAddress( 0x1200, 0x1200, atari_vg_go ),
                new MemoryWriteAddress( 0x1400, 0x1400, MWA_NOP ), /* watchdog clear */
                new MemoryWriteAddress( 0x1600, 0x1600, vg_reset ),
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
                new InputPort(       /* DSW1  - coin values */
                        0xfd,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(      /* DSW2 - gameplay settings */
                        0xeb,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(       /* IN3 - RedBaron uses an analog joystick, so this is
                         *       partially a fake. */
                        0x00,
                        new int[]{ OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_UP, OSD_KEY_DOWN, 0, 0, 0, 0 }
                ),
                new InputPort(       /* IN4 */
                        0x00,
                       new int[] { 0, 0, 0, 0, 0, 0, OSD_KEY_1, OSD_KEY_CONTROL }
                ),
                new InputPort( -1 )
        };

        static TrakPort trak_ports[] =
        {
                new TrakPort(
                        X_AXIS,
                        1,
                        1.0,
                        null
                ),
                new TrakPort(
                        Y_AXIS,
                        1,
                        1.0,
                        null
                ),
                new TrakPort( -1 )
        };


        static KEYSet keys[] =
        {
                new KEYSet( 3, 0, "BANK LEFT" ),
                new KEYSet( 3, 1, "BANK RIGHT" ),
                new KEYSet( 3, 2, "CLIMB" ),
                new KEYSet( 3, 3, "DIVE" ),
                new KEYSet( 4, 7, "FIRE" ),
                new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                 new DSW( 2, 0x03, "LANGUAGE", new String[]{ "GERMAN", "FRENCH", "SPANISH", "ENGLISH" } ),
                 new DSW( 2, 0x0c, "BONUS PLANE", new String[]{ "NONE", "6K 20K 50K", "4K 15K 40K", "2K 10K 30K" } ),
                 new DSW( 2, 0x30, "LIVES", new String[]{ "5", "4", "3", "2" } ),
                 new DSW( 2, 0x80, "SELF ADJUST DIFF", new String[]{ "ON", "OFF" } ),
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
                0x00,0x01,0x01, /* CYAN */
                0x00,0x00,0x01, /* BLUE */
                0x00,0x01,0x00, /* GREEN */
                0x01,0x00,0x00, /* RED */
                0x01,0x00,0x01, /* MAGENTA */
                0x01,0x01,0x00, /* YELLOW */
                0x01,0x01,0x01,	/* WHITE */
                0x00,0x00,0x00,	/* BLACK */
                0x00,0x01,0x01, /* CYAN */
                0x00,0x00,0x01, /* BLUE */
                0x00,0x01,0x00, /* GREEN */
                0x01,0x00,0x00, /* RED */
                0x01,0x00,0x01, /* MAGENTA */
                0x01,0x01,0x00, /* YELLOW */
                0x01,0x01,0x01,	/* WHITE */
                0x00,0x00,0x00	/* BLACK */
        };

        static MachineDriver machine_driver = new MachineDriver
        (
                /* basic machine hardware */
                new MachineCPU[] {
                new MachineCPU(
                                CPU_M6502,
                                1500000,	/* 1.5 Mhz */
                                0,
                                readmem,writemem,null,null,
                                nmi_interrupt,4 /* 1 interrupt per frame? */
                        )
                },
                60, /* frames per second */
                null,

                /* video hardware */
                288, 224,  new rectangle( 0, 520, 0, 400 ),
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
        static RomLoadPtr redbaron_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "037587.01",  0x4800, 0x0800, 0x8e42eeee );
                ROM_CONTINUE(           0x5800, 0x0800 );
                ROM_LOAD( "037000.01E", 0x5000, 0x0800, 0x6dea3bc4 );
                ROM_LOAD( "036998.01E", 0x6000, 0x0800, 0x0c59f20d );
                ROM_LOAD( "036997.01E", 0x6800, 0x0800, 0x31e948b7 );
                ROM_LOAD( "036996.01E", 0x7000, 0x0800, 0xd2c126d9 );
                ROM_LOAD( "036995.01E", 0x7800, 0x0800, 0x2d259e61 );
                ROM_RELOAD(             0xf800, 0x0800 );	/* for reset/interrupt vectors */
                /* Mathbox ROMs */
                ROM_LOAD( "037006.01E", 0x3000, 0x0800, 0xbd8f807f );
                ROM_LOAD( "037007.01E", 0x3800, 0x0800, 0xd59dec13 );
                ROM_END();
        }};

        public static GameDriver redbaron_driver = new GameDriver
        (
                "Red Baron",
                "redbaron",
                "BRAD OLIVER\nAL KOSSOW\nHEDLEY RAINNIE\nERIC SMITH\nALLARD VAN DER BAS\nBERND WIEBELT\nBALOO",
                machine_driver,

                redbaron_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                color_prom, null, null,
                ORIENTATION_DEFAULT,     /* paused_x, paused_y */

                atari_vg_earom_load, atari_vg_earom_save
        );   
}
