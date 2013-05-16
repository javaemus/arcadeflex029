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
 *  Notes : Roms are from v0.36 romset
 *   amidar = amidarjp in v0.27
 *   amidaru = amidar  in v0.27
 */

package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.generic.*;
import static sndhrdw.amidar.*;
import static vidhrdw.generic.*;
import static vidhrdw.amidar.*;
import static mame.memoryH.*;

public class amidar
{



	static MemoryReadAddress amidar_readmem[] =
	{
                new MemoryReadAddress( 0x0000, 0x4fff, MRA_ROM ),
                new MemoryReadAddress( 0x8000, 0x87ff, MRA_RAM ),
                new MemoryReadAddress( 0x9000, 0x93ff, MRA_RAM ),
                new MemoryReadAddress( 0x9800, 0x985f, MRA_RAM ),
                new MemoryReadAddress( 0xa800, 0xa800, MRA_NOP ),
                new MemoryReadAddress( 0xb000, 0xb000, input_port_0_r ),	/* IN0 */
                new MemoryReadAddress( 0xb010, 0xb010, input_port_1_r ),	/* IN1 */
                new MemoryReadAddress( 0xb020, 0xb020, input_port_2_r ),	/* IN2 */
                new MemoryReadAddress( 0xb820, 0xb820, input_port_3_r ),	/* DSW */
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress writemem[] =
	{
                new MemoryWriteAddress( 0x0000, 0x4fff, MWA_ROM ),
                new MemoryWriteAddress( 0x8000, 0x87ff, MWA_RAM ),
                new MemoryWriteAddress( 0x9000, 0x93ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x9800, 0x983f, amidar_attributes_w, amidar_attributesram ),
                new MemoryWriteAddress( 0x9840, 0x985f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x9860, 0x987f, MWA_NOP ),
                new MemoryWriteAddress( 0xa008, 0xa008, interrupt_enable_w ),
                new MemoryWriteAddress( 0xa010, 0xa010, amidar_flipx_w ),
                new MemoryWriteAddress( 0xa018, 0xa018, amidar_flipy_w ),
                new MemoryWriteAddress( 0xa030, 0xa030, MWA_NOP ),
                new MemoryWriteAddress( 0xa038, 0xa038, MWA_NOP ),
                new MemoryWriteAddress( 0xb800, 0xb800, sound_command_w ),
                new MemoryWriteAddress( 0xb810, 0xb810, MWA_NOP ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};



	static MemoryReadAddress sound_readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x1fff, MRA_ROM ),
                new MemoryReadAddress( 0x8000, 0x83ff, MRA_RAM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress sound_writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x1fff, MWA_ROM ),
                new MemoryWriteAddress( 0x8000, 0x83ff, MWA_RAM ),
                new MemoryWriteAddress( 0x9000, 0x9000, MWA_NOP ),
                new MemoryWriteAddress( 0x9080, 0x9080, MWA_NOP ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};



	static IOReadPort sound_readport[] =
	{
		new IOReadPort( 0x80, 0x80, AY8910_read_port_0_r ),
		new IOReadPort( 0x20, 0x20, AY8910_read_port_1_r ),
		new IOReadPort( -1 )	/* end of table */
	};

	static IOWritePort sound_writeport[] =
	{
		new IOWritePort( 0x40, 0x40, AY8910_control_port_0_w ),
		new IOWritePort( 0x80, 0x80, AY8910_write_port_0_w ),
		new IOWritePort( 0x10, 0x10, AY8910_control_port_1_w ),
		new IOWritePort( 0x20, 0x20, AY8910_write_port_1_w ),
		new IOWritePort( -1 )	/* end of table */
	};


        static InputPortPtr amidar_input_ports= new InputPortPtr(){ public void handler() 
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably space for button 2 */
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );

                PORT_START();	/* IN1 */
                PORT_DIPNAME( 0x03, 0x03, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x03, "3" );
                PORT_DIPSETTING(    0x02, "4" );
                PORT_DIPSETTING(    0x01, "5" );
                PORT_DIPSETTING(    0x00, "255" );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably space for player 2 button 2 */
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
                PORT_DIPNAME( 0x02, 0x00, "Demo Sounds", IP_KEY_NONE );
                PORT_DIPSETTING(    0x02, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x04, 0x00, "Bonus Life", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "30000 70000" );
                PORT_DIPSETTING(    0x04, "50000 80000" );
                PORT_DIPNAME( 0x08, 0x00, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "Upright" );
                PORT_DIPSETTING(    0x08, "Cocktail" );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY );
                PORT_DIPNAME( 0x20, 0x00, "unknown1", IP_KEY_NONE );
                PORT_DIPSETTING(    0x20, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY );
                PORT_DIPNAME( 0x80, 0x00, "unknown2", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );

                PORT_START();	/* DSW */
                PORT_DIPNAME( 0x0f, 0x0f, "Coin A", IP_KEY_NONE );
                PORT_DIPSETTING(    0x04, "4 Coins/1 Credit" );
                PORT_DIPSETTING(    0x0a, "3 Coins/1 Credit" );
                PORT_DIPSETTING(    0x01, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x02, "3 Coins/2 Credits" );
                PORT_DIPSETTING(    0x08, "4 Coins/3 Credits" );
                PORT_DIPSETTING(    0x0f, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x0c, "3 Coins/4 Credits" );
                PORT_DIPSETTING(    0x0e, "2 Coins/3 Credits" );
                PORT_DIPSETTING(    0x07, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0x06, "2 Coins/5 Credits" );
                PORT_DIPSETTING(    0x0b, "1 Coin/3 Credits" );
                PORT_DIPSETTING(    0x03, "1 Coin/4 Credits" );
                PORT_DIPSETTING(    0x0d, "1 Coin/5 Credits" );
                PORT_DIPSETTING(    0x05, "1 Coin/6 Credits" );
                PORT_DIPSETTING(    0x09, "1 Coin/7 Credits" );
                PORT_DIPSETTING(    0x00, "Free Play" );
                PORT_DIPNAME( 0xf0, 0xf0, "Coin B", IP_KEY_NONE );
                PORT_DIPSETTING(    0x40, "4 Coins/1 Credit" );
                PORT_DIPSETTING(    0xa0, "3 Coins/1 Credit" );
                PORT_DIPSETTING(    0x10, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x20, "3 Coins/2 Credits" );
                PORT_DIPSETTING(    0x80, "4 Coins/3 Credits" );
                PORT_DIPSETTING(    0xf0, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0xc0, "3 Coins/4 Credits" );
                PORT_DIPSETTING(    0xe0, "2 Coins/3 Credits" );
                PORT_DIPSETTING(    0x70, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0x60, "2 Coins/5 Credits" );
                PORT_DIPSETTING(    0xb0, "1 Coin/3 Credits" );
                PORT_DIPSETTING(    0x30, "1 Coin/4 Credits" );
                PORT_DIPSETTING(    0xd0, "1 Coin/5 Credits" );
                PORT_DIPSETTING(    0x50, "1 Coin/6 Credits" );
                PORT_DIPSETTING(    0x90, "1 Coin/7 Credits" );
                PORT_DIPSETTING(    0x00, "Disable All Coins" );
                INPUT_PORTS_END();
        }};

        /* similar to Amidar, dip swtiches are different and port 3, which in Amidar */
        /* selects coins per credit, is not used. */
        static InputPortPtr turtles_input_ports= new InputPortPtr(){ public void handler() 
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably space for button 2 */
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );

                PORT_START();	/* IN1 */
                PORT_DIPNAME( 0x03, 0x01, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "2" );
                PORT_DIPSETTING(    0x01, "3" );
                PORT_DIPSETTING(    0x02, "4" );
                PORT_DIPSETTING(    0x03, "126" );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably space for player 2 button 2 */
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
                PORT_DIPNAME( 0x06, 0x00, "Coinage", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "A 1/1 B 2/1 C 1/1" );
                PORT_DIPSETTING(    0x02, "A 1/2 B 1/1 C 1/2" );
                PORT_DIPSETTING(    0x04, "A 1/3 B 3/1 C 1/3" );
                PORT_DIPSETTING(    0x06, "A 1/4 B 4/1 C 1/4" );
                PORT_DIPNAME( 0x08, 0x00, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "Upright" );
                PORT_DIPSETTING(    0x08, "Cocktail" );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY );
                PORT_DIPNAME( 0x20, 0x00, "unknown1", IP_KEY_NONE );
                PORT_DIPSETTING(    0x20, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY );
                PORT_DIPNAME( 0x80, 0x00, "unknown2", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                INPUT_PORTS_END();
        }};

        /* same as Turtles, but dip switches are different. */
        static InputPortPtr turpin_input_ports= new InputPortPtr(){ public void handler() 
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably space for button 2 */
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );

                PORT_START();	/* IN1 */
                PORT_DIPNAME( 0x03, 0x01, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "2" );
                PORT_DIPSETTING(    0x01, "4" );
                PORT_DIPSETTING(    0x02, "6" );
                PORT_DIPSETTING(    0x03, "126" );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );	/* probably space for player 2 button 2 */
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
                PORT_DIPNAME( 0x06, 0x00, "Coinage", IP_KEY_NONE );
                PORT_DIPSETTING(    0x06, "4 Coins/1 Credit" );
                PORT_DIPSETTING(    0x02, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x00, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x04, "1 Coin/2 Credits" );
                PORT_DIPNAME( 0x08, 0x00, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "Upright" );
                PORT_DIPSETTING(    0x08, "Cocktail" );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY );
                PORT_DIPNAME( 0x20, 0x00, "unknown1", IP_KEY_NONE );
                PORT_DIPSETTING(    0x20, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY );
                PORT_DIPNAME( 0x80, 0x00, "unknown2", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                INPUT_PORTS_END();
        }};
        
	static GfxLayout charlayout = new GfxLayout
	(
            8,8,	/* 8*8 characters */
            256,	/* 256 characters */
            2,	/* 2 bits per pixel */
            new int[]{ 0, 256*8*8 },	/* the two bitplanes are separated */
            new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },
            new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
            8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
            16,16,	/* 16*16 sprites */
            64,	/* 64 sprites */
            2,	/* 2 bits per pixel */
            new int[]{ 0, 64*16*16 },	/* the two bitplanes are separated */
            new int[]{ 0, 1, 2, 3, 4, 5, 6, 7,
                            8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
            new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                            16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
            32*8	/* every sprite takes 32 consecutive bytes */
	);



	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,     0, 8 ),
		new GfxDecodeInfo( 1, 0x0000, spritelayout,   0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};


        static  char amidar_color_prom[] =
        {
                /* palette */
                0x00,0x07,0xC0,0xB6,0x00,0x38,0xC5,0x67,0x00,0x30,0x07,0x3F,0x00,0x07,0x30,0x3F,
                0x00,0x3F,0x30,0x07,0x00,0x38,0x67,0x3F,0x00,0xFF,0x07,0xDF,0x00,0xF8,0x07,0xFF
        };



        static  char turtles_color_prom[] =
        {
                /* palette */
                0x00,0xC0,0x57,0xFF,0x00,0x66,0xF2,0xFE,0x00,0x2D,0x12,0xBF,0x00,0x2F,0x7D,0xB8,
                0x00,0x72,0xD2,0x06,0x00,0x94,0xFF,0xE8,0x00,0x54,0x2F,0xF6,0x00,0x24,0xBF,0xC6
        };


	static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				amidar_readmem,writemem, null, null,
				nmi_interrupt, 1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				2100000,	/* 2 Mhz?????? */
				2,	/* memory region #2 */
				sound_readmem, sound_writemem, sound_readport, sound_writeport,
				amidar_sh_interrupt, 10
			)
		},
		60,
		null,

		/* video hardware */
		32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1  ),
		gfxdecodeinfo,
		32,32,
		amidar_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
		null,
		generic_vh_start,
		generic_vh_stop,
		amidar_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		amidar_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);

	/***************************************************************************

	  Game driver(s)

	***************************************************************************/
        static RomLoadPtr amidar_rom = new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD(  "amidar.2c", 0x0000, 0x1000, 0xc294bf27 );
		ROM_LOAD(  "amidar.2e", 0x1000, 0x1000, 0xe6e96826 );
		ROM_LOAD(  "amidar.2f", 0x2000, 0x1000, 0x3656be6f );
		ROM_LOAD(  "amidar.2h", 0x3000, 0x1000, 0x1be170bd );

		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD(  "amidar.5f", 0x0000, 0x0800, 0x5e51e84d );
		ROM_LOAD(  "amidar.5h", 0x0800, 0x0800, 0x2f7f1c30 );

		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		ROM_LOAD(  "amidar.5c", 0x0000, 0x1000, 0xc4b66ae4 );
		ROM_LOAD(  "amidar.5d", 0x1000, 0x1000, 0x806785af );
	        ROM_END();
        }};
        static RomLoadPtr amidaru_rom = new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "amidarus.2c", 0x0000, 0x1000, 0x951e0792 );
		ROM_LOAD( "amidarus.2e", 0x1000, 0x1000, 0xa1a3a136 );
		ROM_LOAD( "amidarus.2f", 0x2000, 0x1000, 0xa5121bf5 );
		ROM_LOAD( "amidarus.2h", 0x3000, 0x1000, 0x051d1c7f );
		ROM_LOAD( "amidarus.2j", 0x4000, 0x1000, 0x351f00d5 );

		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "amidarus.5f", 0x0000, 0x0800, 0x2cfe5ede );
		ROM_LOAD( "amidarus.5h", 0x0800, 0x0800, 0x57c4fd0d );

		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		ROM_LOAD( "amidarus.5c", 0x0000, 0x1000, 0x8ca7b750 );
		ROM_LOAD( "amidarus.5d", 0x1000, 0x1000, 0x9b5bdc0a );
                ROM_END();
        }};
        static RomLoadPtr amigo_rom = new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "2732.a1",      0x0000, 0x1000, 0x930dc856 );
                ROM_LOAD( "2732.a2",      0x1000, 0x1000, 0x66282ff5 );
                ROM_LOAD( "2732.a3",      0x2000, 0x1000, 0xe9d3dc76 );
                ROM_LOAD( "2732.a4",      0x3000, 0x1000, 0x4a4086c9 );

                ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "2716.a6",      0x0000, 0x0800, 0x2082ad0a );
                ROM_LOAD( "2716.a5",      0x0800, 0x0800, 0x3029f94f );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "amidarus.5c",  0x0000, 0x1000, 0x8ca7b750 );
                ROM_LOAD( "amidarus.5d",  0x1000, 0x1000, 0x9b5bdc0a );
                ROM_END();
        }};
        static RomLoadPtr turtles_rom = new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD(  "turt_vid.2c", 0x0000, 0x1000, 0xec5e61fb );
		ROM_LOAD(  "turt_vid.2e", 0x1000, 0x1000, 0xfd10821e );
		ROM_LOAD(  "turt_vid.2f", 0x2000, 0x1000, 0xddcfc5fa );
		ROM_LOAD(  "turt_vid.2h", 0x3000, 0x1000, 0x9e71696c );
		ROM_LOAD(  "turt_vid.2j", 0x4000, 0x1000, 0xfcd49fef );

		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */

		ROM_LOAD(  "turt_vid.5f", 0x0000, 0x0800, 0xe5999d52 );
		ROM_LOAD(  "turt_vid.5h", 0x0800, 0x0800, 0xc3ffd655  );

		ROM_REGION(0x10000);	/* 64k for the audio CPU */

		ROM_LOAD(  "turt_snd.5c", 0x0000, 0x1000, 0xf0c30f9a );
		ROM_LOAD(  "turt_snd.5d", 0x1000, 0x1000, 0xaf5fc43c );
                ROM_END();
        }};



        static RomLoadPtr turpin_rom = new RomLoadPtr(){ public void handler()  
        {
                 ROM_REGION(0x10000);	/* 64k for code */
                 ROM_LOAD( "m1",           0x0000, 0x1000, 0x89177473 );
                 ROM_LOAD( "m2",           0x1000, 0x1000, 0x4c6ca5c6 );
                 ROM_LOAD( "m3",           0x2000, 0x1000, 0x62291652 );
                 ROM_LOAD( "turt_vid.2h",  0x3000, 0x1000, 0x9e71696c );
                 ROM_LOAD( "m5",           0x4000, 0x1000, 0x7d2600f2 );

                ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "turt_vid.5h",  0x0000, 0x0800, 0xe5999d52 );
                ROM_LOAD( "turt_vid.5f",  0x0800, 0x0800, 0xc3ffd655 );

                 ROM_REGION(0x10000);	/* 64k for the audio CPU */

                 ROM_LOAD( "turt_snd.5c",  0x0000, 0x1000, 0xf0c30f9a );
                 ROM_LOAD( "turt_snd.5d",  0x1000, 0x1000, 0xaf5fc43c );
                ROM_END();
        }};


      static HiscoreLoadPtr amidar_hiload = new HiscoreLoadPtr()
      {
        public int handler()
        {
          char[] RAM = Machine.memory_region[0];

         /*TOFIX          if (memcmp(RAM, 0x8200, new char[] { 0x00, 0x00, 0x01 }, 3) == 0 && memcmp(RAM, 0x821b, new char[] { 0x00, 0x00, 0x01 }, 3) == 0)
            {
              FILE localFILE;
              if ((localFILE = fopen(name, "rb")) != null)
              {
                fread(RAM, 0x8200, 1, 3*10, localFILE);
                RAM[0x80a8] = RAM[0x8200];
                RAM[0x80a9] = RAM[0x8201];
                RAM[0x80aa] = RAM[0x8202];
                fclose(localFILE);
              }

              return 1;
            }*/
          return 0;
        }
      };

      static HiscoreSavePtr amidar_hisave = new HiscoreSavePtr()
      {
        public void handler()
        {
       /*TOFIX            char[] RAM = Machine.memory_region[0];
          FILE localFILE;
          if ((localFILE = fopen(name, "wb")) != null)
          {
            fwrite(RAM, 0x8200, 1, 3*10, localFILE);
            fclose(localFILE);
          }*/
        }
      };


	public static GameDriver amidaru_driver = new GameDriver
	(
                "Amidar (US version)",
		"amidaru",
                "Robert Anschuetz (Arcade emulator)\nNicola Salmoria (MAME driver)\nAlan J. McCormick (color info)",
		machine_driver,

		amidaru_rom,
		null, null,
		null,

		null/*TBR*/,amidar_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

		amidar_color_prom,null,null,
		ORIENTATION_ROTATE_90,

		amidar_hiload, amidar_hisave
	);

	public static GameDriver amidar_driver = new GameDriver
	(
                "Amidar (Japanese version)",
		"amidar",
                "Robert Anschuetz (Arcade emulator)\nNicola Salmoria (MAME driver)\nAlan J. McCormick (color info)",
		machine_driver,

		amidar_rom,
		null, null,
		null,

		null/*TBR*/,amidar_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

		amidar_color_prom, null, null,
		ORIENTATION_ROTATE_90,

		amidar_hiload, amidar_hisave
	);
	public static GameDriver amigo_driver = new GameDriver
	(
                "Amigo (Amidar US bootleg)",
		"amigo",
                "Robert Anschuetz (Arcade emulator)\nNicola Salmoria (MAME driver)\nAlan J. McCormick (color info)",
		machine_driver,

		amigo_rom,
		null, null,
		null,

		null/*TBR*/,amidar_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

		amidar_color_prom, null, null,
		ORIENTATION_ROTATE_90,

		amidar_hiload, amidar_hisave
	);
	public static GameDriver turtles_driver = new GameDriver
	(
                "Turtles",
		"turtles",
                "Robert Anschuetz (Arcade emulator)\nNicola Salmoria (MAME driver)\nAlan J. McCormick (color info)",
		machine_driver,

		turtles_rom,
		null, null,
		null,

		null/*TBR*/,turtles_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

		turtles_color_prom, null, null,
		ORIENTATION_ROTATE_90,

		null, null
	);
        public static GameDriver turpin_driver =new GameDriver
        (
                "Turpin",
                "turpin",
                "Robert Anschuetz (Arcade emulator)\nNicola Salmoria (MAME driver)\nAlan J. McCormick (color info)",
                machine_driver,

                turpin_rom,
                null, null,
                null,

                null/*TBR*/,turpin_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                turtles_color_prom, null, null,
                ORIENTATION_ROTATE_90,

                null, null
        );
}

