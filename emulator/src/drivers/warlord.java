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
import static vidhrdw.warlord.*;
import static sndhrdw.pokeyintf.*;
import static machine.warlord.*;
import static mame.memoryH.*;
public class warlord 
{
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x03ff, MRA_RAM ),
                new MemoryReadAddress( 0x0400, 0x07ff, MRA_RAM ),
                new MemoryReadAddress( 0x5000, 0x7fff, MRA_ROM ),
                new MemoryReadAddress( 0xf800, 0xffff, MRA_ROM ),	/* for the reset / interrupt vectors */
                new MemoryReadAddress( 0x0c00, 0x0c00, input_port_0_r ),	/* IN0 */
                new MemoryReadAddress( 0x0c01, 0x0c01, input_port_1_r ),	/* IN1 */
                new MemoryReadAddress( 0x0800, 0x0800, input_port_4_r ),	/* DSW1 */
                new MemoryReadAddress( 0x0801, 0x0801, input_port_5_r ),	/* DSW2 */
                new MemoryReadAddress( 0x1000, 0x100f, warlord_pots ),   /* Read the 4 pokey pot values & the random # gen */
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x03ff, MWA_RAM ),
                new MemoryWriteAddress( 0x0400, 0x07bf, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x07c0, 0x07ff, MWA_RAM, spriteram ),
                new MemoryWriteAddress( 0x1000, 0x100f, pokey1_w ),
                new MemoryWriteAddress( 0x1010, 0x10ff, MWA_NOP ),
                new MemoryWriteAddress( 0x1800, 0x1800, MWA_NOP ),
                new MemoryWriteAddress( 0x1c00, 0x1cff, MWA_NOP ),
                new MemoryWriteAddress( 0x4000, 0x4000, MWA_NOP ),
                new MemoryWriteAddress( 0x5000, 0x7fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static InputPort input_ports[] =
        {
                new InputPort(	/* IN0 */
                        0x20,
                        new int[]{ 0, 0, 0, 0, 0, OSD_KEY_F2, IPB_VBLANK, OSD_KEY_C }
                 ),
                    new InputPort(	/* IN1 */
                        0xff,
                        new int[]{ OSD_KEY_1, OSD_KEY_2, 0, 0, 0, 0, OSD_KEY_3, 0 }
                 ),
                    new InputPort(	/* IN2 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                 ),
                    new InputPort(	/* IN3 */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                 ),
                    new InputPort(	/* DSW1 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                 ),
                    new InputPort(	/* DSW2 */
                        0x02,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                 ),
                 new InputPort( -1 )	/* end of table */
        };

        static TrakPort trak_ports[] =
        {
                new TrakPort(
                  X_AXIS,
                  1,
                  1.0,
                  warlord_trakball_r
                ),
                new TrakPort( -1 )
        };


        static KEYSet keys[] =
        {
                new KEYSet( 1, 0, "PL1 FIRE / START" ),
                new KEYSet( 1, 1, "PL2 FIRE / START" ),
                new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                 new DSW( 5, 0x03, "COINS PER CRED", new String[]{ "FREE PLAY", "1CO=2CR", "1CO=1CR", "2CO=1CR" } ),
                 new DSW( 5, 0x0C, "RIGHT COIN MULTIPLIER", new String[]{ "X1", "X4", "X5", "X6" } ),
                 new DSW( 5, 0x10, "LEFT COIN MULTIPLIER", new String[]{ "X1", "X2" } ),
                 new DSW( 4, 0x30, "CRED PER PLAY", new String[]{ "1&2UP=1", "1UP=1,2UP=2", "N/A", "1&2UP=2" } ),
                 new DSW( 4, 0x04, "END MUSIC", new String[]{ "ALWAYS", "HISCORE" } ),
                 new DSW( 4, 0x03, "LANGUAGE", new String[]{ "ENGLISH", "GERMAN", "FRENCH", "SPANISH" } ),
                 new DSW( -1 )
        };


        static GfxLayout charlayout = new GfxLayout
        (
                8,8,	/* 8*8 characters */
                128,	/* 128 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 128*8*8 , 0 },	/* bitplane separation */
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                8*8	/* every char takes 8 consecutive bytes */
        );



        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, charlayout,   0, 1 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        static MachineDriver machine_driver = new MachineDriver
           (
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_M6502,
                                1000000,	/* 1 Mhz ???? */
                                0,
                                readmem,writemem,null,null,
                                interrupt,1
                        )
                },
                60,
                null,

                /* video hardware */
                32*8, 32*8, new rectangle( 0*8, 32*8-1, 0*8, 32*8-1 ),
                gfxdecodeinfo,
                16, 2*4,
                warlord_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,

                null,
                generic_vh_start,
                generic_vh_stop,
                warlord_vh_screenrefresh,

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
        static RomLoadPtr warlord_rom= new RomLoadPtr(){ public void handler()  //using 0.27 loading ,crcs are probably wrong...
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "037154.1m", 0x5000, 0x0800, 0x69a5fadb );
                ROM_LOAD( "037153.1k", 0x5800, 0x0800, 0x13ee094a );
                ROM_LOAD( "037158.1j", 0x6000, 0x0800, 0x038996f3 );
                ROM_LOAD( "037157.1h", 0x6800, 0x0800, 0xa259de59 );
                ROM_LOAD( "037156.1e", 0x7000, 0x0800, 0x363914bd );
                ROM_LOAD( "037155.1d", 0x7800, 0x0800, 0x4880f13a );
                ROM_RELOAD(            0xf800, 0x0800 );	/* for the reset and interrupt vectors */

                ROM_REGION(0x800);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "037159.6e", 0x0000, 0x0800, 0x98aea2be );
                ROM_END();
        }};

        public static GameDriver  warlord_driver =new GameDriver
        (
                "Warlords",
                "warlord",
                "LEE TAYLOR\nJOHN CLEGG",
                machine_driver,

                warlord_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                null, null, null,
                ORIENTATION_DEFAULT,

                null, null
        );    
}
