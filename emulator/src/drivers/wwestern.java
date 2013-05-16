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
 *   Notes : Roms are from v0.36 romset
 */

package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static machine.elevator.*;
import static vidhrdw.generic.*;
import static vidhrdw.taito.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.generic.*;
import static sndhrdw.elevator.*;
import static mame.memoryH.*;

public class wwestern {
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x8000, 0x87ff, MRA_RAM ),
                new MemoryReadAddress( 0xc400, 0xcfff, MRA_RAM ),
                new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
                new MemoryReadAddress( 0xd408, 0xd408, input_port_0_r ),	/* IN0 */
                new MemoryReadAddress( 0xd409, 0xd409, input_port_1_r ),	/* IN1 */
                new MemoryReadAddress( 0xd40b, 0xd40b, input_port_2_r ),	/* IN2 */
                new MemoryReadAddress( 0xd40c, 0xd40c, input_port_3_r ),	/* COIN */
                new MemoryReadAddress( 0xd40a, 0xd40a, input_port_4_r ),	/* DSW1 */
                new MemoryReadAddress( 0xd40f, 0xd40f, AY8910_read_port_0_r ),	/* DSW2 and DSW3 */
                new MemoryReadAddress( 0xd404, 0xd404, taito_gfxrom_r ),
                new MemoryReadAddress( 0x8801, 0x8801, elevator_protection_t_r ),
                new MemoryReadAddress( 0x8800, 0x8800, elevator_protection_r ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x8000, 0x87ff, MWA_RAM ),
                new MemoryWriteAddress( 0xc400, 0xc7ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0xc800, 0xcbff, taito_videoram2_w, taito_videoram2 ),
                new MemoryWriteAddress( 0xcc00, 0xcfff, taito_videoram3_w, taito_videoram3 ),
                new MemoryWriteAddress( 0xd100, 0xd17f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0xd000, 0xd01f, MWA_RAM, taito_colscrolly1 ),
                new MemoryWriteAddress( 0xd020, 0xd03f, MWA_RAM, taito_colscrolly2 ),
                new MemoryWriteAddress( 0xd040, 0xd05f, MWA_RAM, taito_colscrolly3 ),
                new MemoryWriteAddress( 0xd500, 0xd500, MWA_RAM, taito_scrollx1 ),
                new MemoryWriteAddress( 0xd501, 0xd501, MWA_RAM, taito_scrolly1 ),
                new MemoryWriteAddress( 0xd502, 0xd502, MWA_RAM, taito_scrollx2 ),
                new MemoryWriteAddress( 0xd503, 0xd503, MWA_RAM, taito_scrolly2 ),
                new MemoryWriteAddress( 0xd504, 0xd504, MWA_RAM, taito_scrollx3 ),
                new MemoryWriteAddress( 0xd505, 0xd505, MWA_RAM, taito_scrolly3 ),
                new MemoryWriteAddress( 0xd506, 0xd507, taito_colorbank_w, taito_colorbank ),
                new MemoryWriteAddress( 0xd509, 0xd50a, MWA_RAM, taito_gfxpointer ),
                new MemoryWriteAddress( 0xd50b, 0xd50b, sound_command_w ),
                new MemoryWriteAddress( 0xd50d, 0xd50d, MWA_NOP ),
                new MemoryWriteAddress( 0xd200, 0xd27f, taito_paletteram_w, taito_paletteram ),
                new MemoryWriteAddress( 0x9000, 0xbfff, taito_characterram_w, taito_characterram ),
                new MemoryWriteAddress( 0xd40e, 0xd40e, AY8910_control_port_0_w ),
                new MemoryWriteAddress( 0xd40f, 0xd40f, AY8910_write_port_0_w ),
                new MemoryWriteAddress( 0xd300, 0xd300, MWA_RAM, taito_video_priority ),
                new MemoryWriteAddress( 0xd600, 0xd600, MWA_RAM, taito_video_enable ),
                new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static MemoryReadAddress sound_readmem[] =
        {
                new MemoryReadAddress( 0x4000, 0x43ff, MRA_RAM ),
                new MemoryReadAddress( 0x5000, 0x5000, sound_command_r ),
                new MemoryReadAddress( 0x0000, 0x0fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress sound_writemem[] =
        {
                new MemoryWriteAddress( 0x4000, 0x43ff, MWA_RAM ),
                new MemoryWriteAddress(  0x4800, 0x4800, AY8910_control_port_1_w ),
                new MemoryWriteAddress(  0x4801, 0x4801, AY8910_write_port_1_w ),
                new MemoryWriteAddress(  0x4802, 0x4802, AY8910_control_port_2_w ),
                new MemoryWriteAddress(  0x4803, 0x4803, AY8910_write_port_2_w ),
                new MemoryWriteAddress(  0x4804, 0x4804, AY8910_control_port_3_w ),
                new MemoryWriteAddress(  0x4805, 0x4805, AY8910_write_port_3_w ),
                new MemoryWriteAddress(  0x0000, 0x0fff, MWA_ROM ),
                new MemoryWriteAddress(  -1 ) /* end of table */
        };



        static InputPort input_ports[] =
        {
                new InputPort(	/* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_DOWN, OSD_KEY_UP,
                                        OSD_KEY_CONTROL, 0 , 0, 0 }
                ),
		new InputPort(	/* IN1 */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* IN2 */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, OSD_KEY_1, OSD_KEY_2 }
                ),
		new InputPort(	/* COIN */
                        0xff,
                        new int[]{ 0, 0, 0, 0, OSD_KEY_3, 0, 0, 0 }
                ),
		new InputPort(	/* DSW1 */
                        0x7b,
                        new int[]{ 0, 0, 0, 0, 0, OSD_KEY_F2, 0, 0 }
                ),
		new InputPort(	/* DSW2 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* DSW3 */
                        0x7f,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort( -1 )	/* end of table */
        };

       static TrakPort[] trak_ports =
       {
           new TrakPort(-1)
       };



        static KEYSet keys[] =
        {
                new KEYSet( 0, 3, "MOVE UP" ),
                new KEYSet( 0, 0, "MOVE LEFT"  ),
                new KEYSet( 0, 1, "MOVE RIGHT" ),
                new KEYSet( 0, 2, "MOVE DOWN" ),
                new KEYSet( 0, 4, "FIRE" ),
                new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                new DSW( 4, 0x18, "LIVES", new String[]{ "6", "5", "4", "3" }, 1 ),
                new DSW( 4, 0x03, "BONUS", new String[]{ "70000", "50000", "30000", "10000" }, 1 ),
                new DSW( 6, 0x40, "DEMO MODE", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 6, 0x20, "YEAR DISPLAY", new String[]{ "NO", "YES" }, 1 ),
                new DSW( 4, 0x04, "HIGH SCORE NAME",new String[] { "YES", "NO" } ),
                new DSW( 6, 0x01, "SW C 1",new String[] { "ON", "OFF" }, 1 ),
                new DSW( 6, 0x02, "SW C 2", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 6, 0x04, "SW C 3", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 6, 0x08, "SW C 4",new String[] { "ON", "OFF" }, 1 ),
                new DSW( 6, 0x10, "SW C 5",new String[] { "ON", "OFF" }, 1 ),
                new DSW( -1 )
        };



        static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                256,	/* 256 characters */
                3,	/* 3 bits per pixel */
                new int[]{ 512*8*8, 256*8*8, 0 },	/* the bitplanes are separated */
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                8*8	/* every char takes 8 consecutive bytes */
        );
        static GfxLayout spritelayout = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                64,	/* 64 sprites */
                3,	/* 3 bits per pixel */
                new int[]{ 128*16*16, 64*16*16, 0 },	/* the bitplanes are separated */
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0,
                        8*8+7, 8*8+6, 8*8+5, 8*8+4, 8*8+3, 8*8+2, 8*8+1, 8*8+0 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                                16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
                32*8	/* every sprite takes 32 consecutive bytes */
        );



        static GfxDecodeInfo gfxdecodeinfo[] =
	{
                new GfxDecodeInfo( 0, 0x9000, charlayout,   0, 16 ),	/* the game dynamically modifies this */
                new GfxDecodeInfo( 0, 0x9000, spritelayout, 0,  8 ),	/* the game dynamically modifies this */
                new GfxDecodeInfo( 0, 0xa800, charlayout,   0,  8 ),	/* the game dynamically modifies this */
                new GfxDecodeInfo( 0, 0xa800, spritelayout, 0,  8 ),	/* the game dynamically modifies this */
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        /* Wild Western doesn't have a color PROM, it uses a RAM to generate colors */
        /* and change them during the game. Here is the list of all the colors is uses. */
        static char color_prom[] =
        {
                /* total: 33 colors (2 bytes per color) */
                0x01,0xFF,	/* transparent black */
                0x01,0xFF,0x01,0xFE,0x01,0xFD,0x01,0xFC,0x01,0xFB,0x01,0xFA,0x01,0xF9,0x01,0xF8,
                0x01,0xF0,0x01,0xE8,0x01,0xE0,0x01,0xD8,0x01,0xD0,0x01,0xC8,0x01,0xC7,0x01,0xC3,
                0x01,0xC0,0x01,0x80,0x01,0x40,0x01,0x00,0x00,0xC0,0x00,0xBF,0x00,0x93,0x00,0x8F,
                0x00,0x80,0x00,0x57,0x00,0x40,0x00,0x3F,0x00,0x38,0x00,0x24,0x00,0x07,0x00,0x00
        };



        public static MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                3072000,	/* 3.072 Mhz? */
                                0,
                                readmem,writemem,null,null,
                                interrupt,1
                        ),
			new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                3000000,	/* 3 Mhz ??? */
                                3,	/* memory region #3 */
                                sound_readmem,sound_writemem,null,null,
                                elevator_sh_interrupt,5
                        )
                },
                60,
                null,

                /* video hardware */
                32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
                gfxdecodeinfo,
                33, 16*8,
                taito_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
                
                null,
                wwestern_vh_start,
                taito_vh_stop,
                taito_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                elevator_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );



        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr wwestern_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "ww01.bin",     0x0000, 0x1000, 0xbfe10753 );
                ROM_LOAD( "ww02d.bin",    0x1000, 0x1000, 0x20579e90 );
                ROM_LOAD( "ww03d.bin",    0x2000, 0x1000, 0x0e65be37 );
                ROM_LOAD( "ww04d.bin",    0x3000, 0x1000, 0xb3565a31 );
                ROM_LOAD( "ww05d.bin",    0x4000, 0x1000, 0x089f3d89 );
                ROM_LOAD( "ww06d.bin",    0x5000, 0x1000, 0xc81c9736 );
                ROM_LOAD( "ww07.bin",     0x6000, 0x1000, 0x1937cc17 );

                ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD("ww08.bin",  0x0000, 0x1000,0 );	/* not needed - could be removed */

                ROM_REGION(0x8000);	/* graphic ROMs */
                ROM_LOAD( "ww08.bin",     0x0000, 0x1000, 0x041a5a1c );
                ROM_LOAD( "ww09.bin",     0x1000, 0x1000, 0x07982ac5 );
                ROM_LOAD( "ww10.bin",     0x2000, 0x1000, 0xf32ae203 );
                ROM_LOAD( "ww11.bin",     0x3000, 0x1000, 0x7ff1431f );
                ROM_LOAD( "ww12.bin",     0x4000, 0x1000, 0xbe1b563a );
                ROM_LOAD( "ww13.bin",     0x5000, 0x1000, 0x092cd9e5 );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "ww14.bin",     0x0000, 0x1000, 0x23776870 );
                ROM_END();
        }};

        static RomLoadPtr frontlin_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x12000);	/* 64k for code */
                ROM_LOAD( "fl69.u69",     0x00000, 0x1000, 0x93b64599 );
                ROM_LOAD( "fl68.u68",     0x01000, 0x1000, 0x82dccdfb );
                ROM_LOAD( "fl67.u67",     0x02000, 0x1000, 0x3fa1ba12 );
                ROM_LOAD( "fl66.u66",     0x03000, 0x1000, 0x4a3db285 );
                ROM_LOAD( "fl65.u65",     0x04000, 0x1000, 0xda00ec70 );
                ROM_LOAD( "fl64.u64",     0x05000, 0x1000, 0x9fc90a20 );
                ROM_LOAD( "fl55.u55",     0x06000, 0x1000, 0x359242c2 );
                ROM_LOAD( "fl54.u54",     0x07000, 0x1000, 0xd234c60f );
                ROM_LOAD( "aa1_10.8",     0x0e000, 0x1000, 0x2704aa4c );
                ROM_LOAD( "fl53.u53",     0x10000, 0x1000, 0x67429975 );	/* banked at 6000 */
                ROM_LOAD( "fl52.u52",     0x11000, 0x1000, 0xcb223d34 );	/* banked at 7000 */

             /*   ROM_REGION(0x2000)	/* temporary space for graphics (disposed after conversion) */
         /*       ROM_OBSOLETELOAD( "aa1.09",  0x0000, 0x2000 )	/* not needed - could be removed */

                ROM_REGION(0x8000);	/* graphic ROMs */
                        
                ROM_LOAD( "fl1.u1",       0x0000, 0x1000, 0xe82c9f46 );
                ROM_LOAD( "fl2.u2",       0x1000, 0x1000, 0x123055d3 );
                ROM_LOAD( "fl3.u3",       0x2000, 0x1000, 0x7ea46347 );
                ROM_LOAD( "fl4.u4",       0x3000, 0x1000, 0x9e2cff10 );
                ROM_LOAD( "fl5.u5",       0x4000, 0x1000, 0x630b4be1 );
                ROM_LOAD( "fl6.u6",       0x5000, 0x1000, 0x9e092d58 );
                ROM_LOAD( "fl7.u7",       0x6000, 0x1000, 0x613682a3 );
                ROM_LOAD( "fl8.u8",       0x7000, 0x1000, 0xf73b0d5e );
                        
                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "fl70.u70",     0x0000, 0x1000, 0x15f4ed8c );
                ROM_LOAD( "fl71.u71",     0x1000, 0x1000, 0xc3eb38e7 );
                ROM_END();
        }};



        public static GameDriver wwestern_driver = new GameDriver
	(
                "Wild Western",
                "wwestern",
                "NICOLA SALMORIA",
                machine_driver,

                wwestern_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                color_prom, null, null,
                ORIENTATION_ROTATE_270,

                null, null
        );

        public static GameDriver frontlin_driver = new GameDriver
	(
                "Front Line",
                "frontlin",
                "NICOLA SALMORIA",
                machine_driver,

                frontlin_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                color_prom, null, null,
                ORIENTATION_ROTATE_270,

                null, null
        );
}
