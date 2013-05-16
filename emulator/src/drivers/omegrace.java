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
 *
 *  roms are from v0.36 romset
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
import static machine.omegrace.*;
import static vidhrdw.vector.*;
import static vidhrdw.atari_vg.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.omegrace.*;
import static mame.memoryH.*;
public class omegrace {
    
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
                new MemoryReadAddress( 0x4000, 0x4bff, MRA_RAM ),
                new MemoryReadAddress( 0x5c00, 0x5cff, MRA_RAM ), /* NVRAM */
                new MemoryReadAddress( 0x8000, 0x9fff, MRA_RAM, vectorram/*, vectorram_size */),
                new MemoryReadAddress( -1 )	/* end of table */

                /* 9000-9fff is ROM, hopefully there are no writes to it */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x4000, 0x4bff, MWA_RAM ),
                new MemoryWriteAddress( 0x5c00, 0x5cff, MWA_RAM ), /* NVRAM */
                new MemoryWriteAddress( 0x8000, 0x9fff, MWA_RAM, vectorram ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };


        static MemoryReadAddress sound_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x07ff, MRA_ROM ),
                new MemoryReadAddress( 0x1000, 0x13ff, MRA_RAM ),
                new MemoryReadAddress( -1 ) /* end of table */
        };

        static MemoryWriteAddress sound_writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x07ff, MWA_ROM ),
                new MemoryWriteAddress( 0x1000, 0x13ff, MWA_RAM ),
                new MemoryWriteAddress( -1 ) /* end of table */
        };


        static IOReadPort readport[] =
        {
                new IOReadPort( 0x08, 0x08, omegrace_vg_go ),
                new IOReadPort( 0x09, 0x09, omegrace_watchdog_r ),
                new IOReadPort( 0x0b, 0x0b, omegrace_vg_status_r ), /* vg_halt */
                new IOReadPort( 0x10, 0x10, input_port_0_r ), /* DIP SW C4 */
                new IOReadPort( 0x17, 0x17, input_port_1_r ), /* DIP SW C6 */
                new IOReadPort( 0x11, 0x11, input_port_2_r ), /* Player 1 input */
                new IOReadPort( 0x12, 0x12, input_port_3_r ), /* Player 2 input */
                new IOReadPort( 0x15, 0x15, omegrace_spinner1_r ), /* 1st controller */
                new IOReadPort( 0x16, 0x16, omegrace_spinner2_r ), /* 2nd, not supported */
                new IOReadPort( -1 )	/* end of table */
        };

        static IOWritePort writeport[] =
        {
                new IOWritePort( 0x0a, 0x0a, vg_reset ),
                new IOWritePort( 0x13, 0x13, IOWP_NOP ), /* diverse outputs */
                new IOWritePort( 0x14, 0x14, sound_command_w ), /* Sound command */
                new IOWritePort( -1 )	/* end of table */
        };

        static IOReadPort sound_readport[] =
        {
                new IOReadPort( 0x00, 0x00, sound_command_r ),
                new IOReadPort( -1 )
        };

        static IOWritePort sound_writeport[] =
        {
                new IOWritePort( 0x00, 0x00, AY8910_control_port_0_w ),
                new IOWritePort( 0x01, 0x01, AY8910_write_port_0_w ),
                new IOWritePort( 0x02, 0x02, AY8910_control_port_1_w ),
                new IOWritePort( 0x03, 0x03, AY8910_write_port_1_w ),
                new IOWritePort( -1 )  /* end of table */
        };


        static InputPort input_ports[] =
        {
                new InputPort(       /* DSW0 - port 0x10 - bonus settings */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(      /* DSW1 - port 0x17 - cocktail + various credits settings */
                        0x3f,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(      /* IN2 - port 0x11 */
                        0xff,
                        new int[]{ OSD_KEY_3, OSD_KEY_4, 0, 0, OSD_KEY_F1, OSD_KEY_ALT, OSD_KEY_CONTROL, OSD_KEY_F2 }
                ),
                new InputPort(       /* IN3 - port 0x12 */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, OSD_KEY_1, OSD_KEY_2 }
                ),
                new InputPort(      /* IN4 - port 0x15 - spinner emulation */
                        0x00,
                        new int[]{ OSD_KEY_LEFT, OSD_KEY_RIGHT, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(      /* IN5 - port 0x16 - second spinner not emulated */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort( -1 )
        };

        static TrakPort trak_ports[] =
        {
                new TrakPort(
                        X_AXIS,
                        1,
                        0.5,
                        null
                ),
                new TrakPort( -1 )
        };

        static KEYSet keys[] =
        {
                new KEYSet( 4, 0, "ROTATE LEFT" ),
                new KEYSet( 4, 1, "ROTATE RIGHT"  ),
                new KEYSet( 2, 5, "THRUST" ),
                new KEYSet( 2, 6, "FIRE" ),
                new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                new DSW( 0, 0x03, "FIRST BONUS", new String[]{ "40000", "50000", "70000", "100000" } ),
                new DSW( 0, 0x0c, "BONUS 2/3", new String[]{ "150000 250000", "250000 500000", "500000 750000", "750000 1500000" } ),
                new DSW( 0, 0x30, "CREDITS/SHIPS" , new String[]{ "1-2/2-4", "1-2/2-5", "1-3/2-6", "1-3/2-7" } ),
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
                0x01,0x01,0x01, /* WHITE */
                0x00,0x00,0x00, /* BLACK */
                0x00,0x00,0x01, /* BLUE */
                0x00,0x01,0x00, /* GREEN */
                0x00,0x01,0x02, /* CYAN */
                0x01,0x00,0x00, /* RED */
                0x01,0x00,0x01, /* MAGENTA */
                0x01,0x01,0x00, /* YELLOW */
                0x01,0x01,0x01  /* WHITE */
        };


        static MachineDriver machine_driver = new MachineDriver
       (
                /* basic machine hardware */
                new MachineCPU[] {
                new MachineCPU(
                                CPU_Z80,
                                3000000,	/* 3.0 Mhz */
                                0,
                                readmem,writemem,readport,writeport,
                                interrupt,4 	/* approx. 240Hz */
                        ),
                        new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                1500000,	/* 1.5 Mhz */
                                2, 		/* memory region 1*/
                                sound_readmem,sound_writemem,sound_readport,sound_writeport,
                                omegrace_sh_interrupt,8
                        )
                },
                60,
                null,

                /* video hardware */
                288, 224, new rectangle( 0, 1020, -10, 1010 ),
                gfxdecodeinfo,
                256,256,
                atari_vg_init_colors,

                VIDEO_TYPE_VECTOR,
                null,
                omegrace_vg_start,
                atari_vg_stop,
                atari_vg_screenrefresh,

                /* sound hardware */
                null,
                null,
                omegrace_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );


        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr omegrace_rom= new RomLoadPtr(){ public void handler()  
        { 
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "omega.m7", 0x0000, 0x1000, 0x51948d4e );
                ROM_LOAD( "omega.l7", 0x1000, 0x1000, 0xe1639841 );
                ROM_LOAD( "omega.k7", 0x2000, 0x1000, 0x4ec4afd2 );
                ROM_LOAD( "omega.j7", 0x3000, 0x1000, 0x273fa6b7 );
                ROM_LOAD( "omega.e1", 0x9000, 0x0800, 0x63c42592 );
                ROM_LOAD( "omega.f1", 0x9800, 0x0800, 0xe63e51e2 );

                ROM_REGION(0x0800);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "sound.k5", 0x0000, 0x0800, 0x7f858859 );	/* not needed - could be removed */

                ROM_REGION(0x10000);	/* 64k for audio cpu */
                ROM_LOAD( "sound.k5", 0x0000, 0x0800, 0x7f858859 );
                ROM_END();
        }};

        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
        {
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char RAM[]=Machine.memory_region[0];

                /* no reason to check hiscore table. It's an NV_RAM! */
                /* However, it does not work yet. Don't know why. BW */
         /*TOFIX       FILE f;

                if ((f = fopen(name,"rb")) != null)
                {
                        fread(RAM,0x5c00,1,0x100,f);
                        fclose(f);
                }*/
                return 1;
        }};

        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
        {
                FILE f;
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char RAM[]=Machine.memory_region[0];

               /*TOFIX if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x5c00,1,0x100,f);
                        fclose(f);
                }*/
        }};
        public static GameDriver omegrace_driver = new GameDriver
        (
                "Omega Race",
                "omegrace",
                "AL KOSSOW\nHEDLEY RAINNIE\nERIC SMITH\n BERND WIEBELT\n ",
                machine_driver,

                omegrace_rom,
                null, null,
                null,

                input_ports, null, trak_ports, dsw, keys,

                color_prom, null, null,
                ORIENTATION_DEFAULT,

                hiload, hisave
        );
    
}
