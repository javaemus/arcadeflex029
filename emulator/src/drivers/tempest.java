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
import static machine.atari_vg.*;
import static vidhrdw.atari_vg.*;
import static vidhrdw.vector.*;
import static machine.tempest.*;
import static machine.mathbox.*;
import static mame.inptport.*;
import static mame.memoryH.*;
public class tempest {
        static MemoryReadAddress readmem[] =
        {
        //	{ 0x011f, 0x011f, tempest_freakout },	/* hack to avoid lockup at 150,000 points */
                new MemoryReadAddress( 0x0000, 0x07ff, MRA_RAM ),
                new MemoryReadAddress( 0x9000, 0xdfff, MRA_ROM ),
                new MemoryReadAddress( 0x3000, 0x3fff, MRA_ROM ),
                new MemoryReadAddress( 0xf000, 0xffff, MRA_ROM ),	/* for the reset / interrupt vectors */
                new MemoryReadAddress( 0x2000, 0x2fff, MRA_RAM, vectorram ),
                new MemoryReadAddress( 0x0c00, 0x0c00, tempest_IN0_r ),	/* IN0 */
                new MemoryReadAddress( 0x60c8, 0x60c8, tempest_IN1_r ),	/* IN1/DSW0 */
                new MemoryReadAddress( 0x60d8, 0x60d8, input_port_2_r ),	/* IN2 */
                new MemoryReadAddress( 0x0d00, 0x0d00, input_port_3_r ),	/* DSW1 */
                new MemoryReadAddress( 0x0e00, 0x0e00, input_port_4_r ),	/* DSW2 */
                new MemoryReadAddress( 0x6040, 0x6040, mb_status_r ),
                new MemoryReadAddress( 0x6050, 0x6050, atari_vg_earom_r ),
                new MemoryReadAddress( 0x6060, 0x6060, mb_lo_r ),
                new MemoryReadAddress( 0x6070, 0x6070, mb_hi_r ),
                new MemoryReadAddress( 0x60c0, 0x60cf, pokey1_r ),
                new MemoryReadAddress( 0x60d0, 0x60df, pokey2_r ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x07ff, MWA_RAM ),
                new MemoryWriteAddress( 0x0800, 0x080f, atari_vg_colorram_w ),
                new MemoryWriteAddress( 0x2000, 0x2fff, MWA_RAM, vectorram ),
                new MemoryWriteAddress( 0x6000, 0x603f, atari_vg_earom_w ),
                new MemoryWriteAddress( 0x6040, 0x6040, atari_vg_earom_ctrl ),
                new MemoryWriteAddress( 0x60c0, 0x60cf, pokey1_w ),
                new MemoryWriteAddress( 0x60d0, 0x60df, pokey2_w ),
                new MemoryWriteAddress( 0x6080, 0x609f, mb_go ),
                new MemoryWriteAddress( 0x4800, 0x4800, atari_vg_go ),
                new MemoryWriteAddress( 0x5000, 0x5000, MWA_RAM ),
                new MemoryWriteAddress( 0x5800, 0x5800, vg_reset ),
                new MemoryWriteAddress( 0x9000, 0xdfff, MWA_ROM ),
                new MemoryWriteAddress( 0x3000, 0x3fff, MWA_ROM ),
                new MemoryWriteAddress( 0x4000, 0x4000, MWA_NOP ),
                new MemoryWriteAddress( 0x60e0, 0x60e0, MWA_NOP ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };

        static InputPortPtr input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT ( 0x01, IP_ACTIVE_LOW, IPT_COIN3);
                PORT_BIT ( 0x02, IP_ACTIVE_LOW, IPT_COIN2);
                PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_COIN1);
                PORT_BIT ( 0x08, IP_ACTIVE_LOW, IPT_TILT);
                PORT_DIPNAME ( 0x10, 0x10, "Test", OSD_KEY_F2 );
                PORT_DIPSETTING(     0x10, "Off" );
                PORT_DIPSETTING(     0x00, "On" );
                PORT_BITX( 0x20, IP_ACTIVE_LOW, IPT_SERVICE, "Diagnostic Step", OSD_KEY_F1, IP_JOY_NONE, 0 );
                /* bit 6 is the VG HALT bit */
                /* bit 7 is tied to a 3khz (?) clock */
                /* they are both handled by tempest_IN0_r() */

                PORT_START();	/* IN1/DSW0 */
                /* The four lower bits are for the spinner. We cheat here
                 * by asking for joystick/keyboard input. It might be a good
                 * idea to have generic IPT-definitions for trackball support
                 */
                PORT_BIT ( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY );
                PORT_BIT ( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
                PORT_BIT ( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN ); /* spinner bit 2 */
                PORT_BIT ( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN ); /* spinner bit 3 */
                /* The next one is reponsible for cocktail mode.
                 * According to the documentation, this is not a switch, although
                 * it may have been planned to put it on the Math Box PCB, D/E2 )
                 */
                PORT_DIPNAME( 0x10, 0x10, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x10, "Upright" );
                PORT_DIPSETTING(    0x00, "Cocktail" );
                PORT_BIT ( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT ( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT ( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );

                PORT_START();	/* IN2 */
                PORT_DIPNAME ( 0x03, 0x00, "Difficulty", IP_KEY_NONE );
                PORT_DIPSETTING(     0x01, "Easy" );
                PORT_DIPSETTING(     0x00, "Medium1" );
                PORT_DIPSETTING(     0x03, "Medium2" );
                PORT_DIPSETTING(     0x02, "Hard" );
                PORT_DIPNAME ( 0x04, 0x00, "Rating", IP_KEY_NONE );
                PORT_DIPSETTING(     0x00, "1, 3, 5, 7, 9" );
                PORT_DIPSETTING(     0x04, "tied to high score" );
                PORT_BIT (0x08, IP_ACTIVE_HIGH, IPT_BUTTON2 );
                PORT_BIT (0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
                PORT_BIT (0x20, IP_ACTIVE_HIGH, IPT_START1 );
                PORT_BIT (0x40, IP_ACTIVE_HIGH, IPT_START2 );
                PORT_BIT (0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );

                PORT_START();	/* DSW1 - (N13 on analog vector generator PCB */
                PORT_DIPNAME (0x03, 0x00, "Coinage", IP_KEY_NONE );
                PORT_DIPSETTING (   0x01, "2 Coins/1 Credit" );
                PORT_DIPSETTING (   0x00, "1 Coin/1 Credit" );
                PORT_DIPSETTING (   0x03, "1 Coin/2 Credits" );
                PORT_DIPSETTING (   0x02, "Free Play" );
                PORT_DIPNAME (0x0c, 0x00, "Right Coin", IP_KEY_NONE );
                PORT_DIPSETTING (   0x00, "*1" );
                PORT_DIPSETTING (   0x04, "*4" );
                PORT_DIPSETTING (   0x08, "*5" );
                PORT_DIPSETTING (   0x0c, "*6" );
                PORT_DIPNAME (0x10, 0x00, "Left Coin", IP_KEY_NONE );
                PORT_DIPSETTING (   0x00, "*1" );
                PORT_DIPSETTING (   0x10, "*2" );
                PORT_DIPNAME (0xe0, 0x00, "Bonus Coins", IP_KEY_NONE );
                PORT_DIPSETTING (   0x00, "None" );
                PORT_DIPSETTING (   0x80, "1 each 5" );
                PORT_DIPSETTING (   0x40, "1 each 4 (+Demo)" );
                PORT_DIPSETTING (   0xa0, "1 each 3" );
                PORT_DIPSETTING (   0x60, "2 each 4 (+Demo)" );
                PORT_DIPSETTING (   0x20, "1 each 2" );
                PORT_DIPSETTING (   0xc0, "Freeze Mode" );
                PORT_DIPSETTING (   0xe0, "Freeze Mode" );

                PORT_START();	/* DSW2 - (L12 on analog vector generator PCB */
                PORT_DIPNAME (0x01, 0x00, "Minimum", IP_KEY_NONE );
                PORT_DIPSETTING (   0x00, "1 Credit" );
                PORT_DIPSETTING (   0x01, "2 Credit" );
                PORT_DIPNAME (0x06, 0x00, "Language", IP_KEY_NONE);
                PORT_DIPSETTING (   0x00, "English" );
                PORT_DIPSETTING (   0x02, "French" );
                PORT_DIPSETTING (   0x04, "German" );
                PORT_DIPSETTING (   0x06, "Spanish" );
                PORT_DIPNAME (0x38, 0x00, "Bonus Life", IP_KEY_NONE );
                PORT_DIPSETTING (   0x08, "10000" );
                PORT_DIPSETTING (   0x00, "20000" );
                PORT_DIPSETTING (   0x10, "30000" );
                PORT_DIPSETTING (   0x18, "40000" );
                PORT_DIPSETTING (   0x20, "50000" );
                PORT_DIPSETTING (   0x28, "60000" );
                PORT_DIPSETTING (   0x30, "70000" );
                PORT_DIPSETTING (   0x38, "None" );
                PORT_DIPNAME (0xc0, 0x00, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING (   0xc0, "2" );
                PORT_DIPSETTING (   0x00, "3" );
                PORT_DIPSETTING (   0x40, "4" );
                PORT_DIPSETTING (   0x80, "5" );
                INPUT_PORTS_END();
        }};

        static TrakPort trak_ports[] =
        {
                new TrakPort(
                        X_AXIS,
                        1,
                        1.0,
                        tempest_spinner
                ),
                new TrakPort( -1 )
        };

        /*
         * Tempest does not really have a colorprom, nor any graphics to
         * decode. It has a 16 byte long colorram, but only the lower nibble
         * of each byte is important.
         * The (inverted) meaning of the bits is:
         * 3-green 2-blue 1-red 0-intensity.
         * To dynamically alter the colors, we need access to the colortable.
         * This is what this fakelayout is for.
         */
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
                0x02,0x02,0x02,	/* WHITE */
                0x01,0x01,0x01,	/* WHITE */
                0x00,0x02,0x02, /* CYAN */
                0x00,0x01,0x01, /* CYAN */
                0x02,0x02,0x00, /* YELLOW */
                0x01,0x01,0x00, /* YELLOW */
                0x00,0x02,0x00, /* GREEN */
                0x00,0x01,0x00, /* GREEN */
                0x02,0x00,0x02, /* MAGENTA */
                0x01,0x00,0x01, /* MAGENTA */
                0x00,0x00,0x02,	/* BLUE */
                0x00,0x00,0x01,	/* BLUE */
                0x02,0x00,0x00, /* RED */
                0x01,0x00,0x00, /* RED */
                0x00,0x00,0x00, /* BLACK */
                0x00,0x00,0x00  /* BLACK */
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
                                interrupt,4 /* 4 interrupts per frame? */
                        )
                },
                60, /* frames per second */
                null,

                /* video hardware */
                224, 288, new rectangle( 0, 580, 0, 540 ),
                gfxdecodeinfo,
                256,256,
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
        static RomLoadPtr tempest_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "136002.113",   0x9000, 0x0800, 0x65d61fe7 );
                ROM_LOAD( "136002.114",   0x9800, 0x0800, 0x11077375 );
                ROM_LOAD( "136002.115",   0xa000, 0x0800, 0xf3e2827a );
                ROM_LOAD( "136002.316",   0xa800, 0x0800, 0xaeb0f7e9 );
                ROM_LOAD( "136002.217",   0xb000, 0x0800, 0xef2eb645 );
                ROM_LOAD( "136002.118",   0xb800, 0x0800, 0xbeb352ab );
                ROM_LOAD( "136002.119",   0xc000, 0x0800, 0xa4de050f );
                ROM_LOAD( "136002.120",   0xc800, 0x0800, 0x35619648 );
                ROM_LOAD( "136002.121",   0xd000, 0x0800, 0x73d38e47 );
                ROM_LOAD( "136002.222",   0xd800, 0x0800, 0x707bd5c3 );
                ROM_RELOAD(             0xf800, 0x0800 ); /* for reset/interrupt vectors */
                /* Mathbox ROMs */
                ROM_LOAD( "136002.123",   0x3000, 0x0800, 0x29f7e937 );
                ROM_LOAD( "136002.124",   0x3800, 0x0800, 0xc16ec351 );
                ROM_END();
        }};

        public static GameDriver tempest_driver = new GameDriver
        (
                "Tempest",
                "tempest",
                "BRAD OLIVER\nBERND WIEBELT\nALLARD VAN DER BAS\nNEIL BRADLEY\nAL KOSSOW\nHEDLEY RAINNIE\nERIC SMITH",
                machine_driver,

                tempest_rom,
                null, null,
                null,

                null/*TBR*/,input_ports, trak_ports,null/*TBR*/,null/*TBR*/,

                color_prom, null,null,
                ORIENTATION_DEFAULT,      /* paused_x, paused_y */

                atari_vg_earom_load, atari_vg_earom_save
        );
}
