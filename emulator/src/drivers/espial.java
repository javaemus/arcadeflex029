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
 *
 *  roms are from 0.36 romset
 *
 */
package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static vidhrdw.espial.*;
import static sndhrdw.generic.*;
import static machine.espial.*;
import static mame.memoryH.*;

public class espial {
        public static ReadHandlerPtr pip = new ReadHandlerPtr() { public int handler(int offset)
	{
                if (errorlog!=null) fprintf(errorlog,"%04x: 6090\n",cpu_getpc());
                return 0xff;
        }};


        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x4fff, MRA_ROM ),
                new MemoryReadAddress( 0x5800, 0x5fff, MRA_RAM ),
                new MemoryReadAddress( 0x7000, 0x7000, MRA_RAM ),	/* ?? */
                new MemoryReadAddress( 0x8000, 0x803f, MRA_RAM ),
                new MemoryReadAddress( 0x8400, 0x87ff, MRA_RAM ),
                new MemoryReadAddress( 0x8c00, 0x8fff, MRA_RAM ),
                new MemoryReadAddress( 0x9000, 0x903f, MRA_RAM ),
                new MemoryReadAddress( 0x9400, 0x97ff, MRA_RAM ),
                new MemoryReadAddress( 0xc000, 0xcfff, MRA_ROM ),
                new MemoryReadAddress( 0x6081, 0x6081, input_port_0_r ),	/* IN0 */
                new MemoryReadAddress( 0x6082, 0x6082, input_port_1_r ),	/* IN1 */
                new MemoryReadAddress( 0x6083, 0x6083, input_port_2_r ),	/* IN2 */
                new MemoryReadAddress( 0x6084, 0x6084, input_port_3_r ),	/* IN3 */
                new MemoryReadAddress( 0x6090, 0x6090, pip ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x4fff, MWA_ROM ),
                new MemoryWriteAddress( 0x5800, 0x5fff, MWA_RAM ),
                new MemoryWriteAddress( 0x7000, 0x7000, MWA_RAM ), /* watchdog reset */
                new MemoryWriteAddress( 0x7100, 0x7100, espial_interrupt_enable_w ),
                new MemoryWriteAddress( 0x8000, 0x801f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x8400, 0x87ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x8800, 0x880f, MWA_RAM, spriteram_3 ),
                new MemoryWriteAddress( 0x8c00, 0x8fff, espial_attributeram_w, espial_attributeram ),
                new MemoryWriteAddress( 0x9000, 0x901f, MWA_RAM, spriteram_2 ),
                new MemoryWriteAddress( 0x9020, 0x903f, MWA_RAM, espial_column_scroll ),
                new MemoryWriteAddress( 0x9400, 0x97ff, colorram_w, colorram ),
                new MemoryWriteAddress( 0xc000, 0xcfff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };


       /* #ifdef TRYSOUND
        static struct MemoryReadAddress sound_readmem[] =
        {
                { 0x0000, 0x1fff, MRA_ROM },
                { 0x2000, 0x23ff, MRA_RAM },
                { -1 }	/* end of table */
   /*     };

        static struct MemoryWriteAddress sound_writemem[] =
        {
                { 0x0000, 0x1fff, MWA_ROM },
                { 0x2000, 0x23ff, MWA_RAM },
                { -1 }	/* end of table */
 /*       };

        static struct IOWritePort sound_writeport[] =
        {
                { 0x00, 0x00, AY8910_control_port_0_w },
                { 0x01, 0x01, AY8910_write_port_0_w },
                { -1 }	/* end of table */
     /*   };
        #endif*/

        static InputPortPtr espial_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_BUTTON1 );
                PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );

                PORT_START();	/* IN1 */
                PORT_DIPNAME( 0x03, 0x00, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "3" );
                PORT_DIPSETTING(    0x01, "4" );
                PORT_DIPSETTING(    0x02, "5" );
                PORT_DIPSETTING(    0x03, "6" );
                PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNKNOWN );

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
                PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
                PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY );
                PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );

                PORT_START();	/* IN3 */
                PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN1 );
                PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
                PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY );
                PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_BUTTON2 );
                PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY );
                INPUT_PORTS_END();
        }};



        static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                768,	/* 768 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
                new int[]{ 0, 1, 2, 3, 8*8+0, 8*8+1, 8*8+2, 8*8+3 },	/* bits are packed in groups of four */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                16*8	/* every char takes 16 bytes */
        );

        static GfxLayout spritelayout = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                128,	/* 128 sprites */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 128*32*8 },	/* the two bitplanes are separated */
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7,
                                8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                                16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
                32*8	/* every sprite takes 32 consecutive bytes */
        );


        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, charlayout,    0, 8 ),
                new GfxDecodeInfo( 1, 0x3000, spritelayout,  0, 8 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        /* this is NOT the original color PROM */
        static char espial_color_prom[] =
        {
                /* palette */
                0x00,0x07,0xC0,0xB6,0x00,0x38,0xC5,0x67,0x00,0x30,0x07,0x3F,0x00,0x07,0x30,0x3F,
                0x00,0x3F,0x30,0x07,0x00,0x38,0x67,0x3F,0x00,0xFF,0x07,0xDF,0x00,0xF8,0x07,0xFF
        };



        static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                3072000,	/* 3.072 Mhz */
                                0,
                                readmem,writemem,null,null,
                                espial_interrupt,1
                        ),
        /*#ifdef TRYSOUND
                        {
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2100000,	/* 2 Mhz?????? */
       /*                         2,	/* memory region #2 */
       /*                         sound_readmem,sound_writemem,0,sound_writeport,
                                interrupt,1
                        }
        #endif*/
                },
                60,
                espial_init_machine,

                /* video hardware */
                32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
                gfxdecodeinfo,
                32,32,
                espial_vh_convert_color_prom,

                VIDEO_TYPE_RASTER,
                null,
                generic_vh_start,
                generic_vh_stop,
                espial_vh_screenrefresh,

                /* sound hardware */
                 null,
                 null,
                 null/*espial_sh_start*/,
                 null/*AY8910_sh_stop*/,
                 null/*AY8910_sh_update*/
        );



        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr espial_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "espial.3", 0x0000, 0x2000, 0x0317ae6d );
                ROM_LOAD( "espial.4", 0x2000, 0x2000, 0xd76ccd4e );
                ROM_LOAD( "espial.6", 0x4000, 0x1000, 0x472e9fd4 );
                ROM_LOAD( "espial.5", 0xc000, 0x1000, 0xa295f697 );

                ROM_REGION(0x5000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "espial.8",  0x0000, 0x2000, 0xc2dfd8e7 );
                ROM_LOAD( "espial.7",  0x2000, 0x1000, 0x868e222e );
                ROM_LOAD( "espial.9",  0x3000, 0x1000, 0x04efcefb );
                ROM_LOAD( "espial.10", 0x4000, 0x1000, 0x43808b36 );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "espial.1", 0x0000, 0x1000, 0xb3acb1b0 );
                ROM_LOAD( "espial.2", 0x1000, 0x1000, 0xeec42d68 );
                ROM_END();
        }};



        public static GameDriver espial_driver = new GameDriver
        (
                "Espial",
                "espial",
                "Brad Oliver\nNicola Salmoria",
                machine_driver,

                espial_rom,
                null, null,
                null,

                null/*TBR*/,espial_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                espial_color_prom, null, null,
                ORIENTATION_DEFAULT,

                null, null
        );
    
}
