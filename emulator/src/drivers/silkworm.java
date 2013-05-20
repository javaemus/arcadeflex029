/***************************************************************************

Silkworm memory map (preliminary)

0000-bfff ROM
c000-c1ff Background video RAM #2
c200-c3ff Background color RAM #2
c400-c5ff Background video RAM #1
c600-c7ff Background color RAM #1
c800-cbff Video RAM
cc00-cfff Color RAM
d000-dfff RAM
e000-e7ff Sprites
e800-efff Palette RAM, groups of 2 bytes, 4 bits per gun: xB RG
          e800-e9ff sprites
          ea00-ebff characters
          ec00-edff bg #1
          ee00-efff bg #2
f000-f7ff window for banked ROM

read:
f800      IN0 (heli) bit null-3
f801      IN0 bit 4-7
f802      IN1 (jeep) bit null-3
f803      IN1 bit 4-7
f806      DSWA bit null-3
f807      DSWA bit 4-7
f808      DSWB bit null-3
f809      DSWB bit 4-7
f80f      COIN

write:
f800-f801 bg #1 x scroll
f802      bg #1 y scroll
f803-f804 bg #2 x scroll
f805      bg #2 y scroll
f806      ????
f808      ROM bank selector
f809      ????
f80b      ????

***************************************************************************/

/*
 * ported to v0.29
 * using automatic conversion tool v0.04
 * converted at : 30-05-2012 23:04:54
 *
 *
 * roms are from v0.36 romset
 *
 */ 
package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static machine.silkworm.*;
import static vidhrdw.generic.*;
import static vidhrdw.silkworm.*;
import static mame.inptport.*;
import static mame.memoryH.*;

public class silkworm
{
	
	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new MemoryReadAddress( 0xc000, 0xefff, MRA_RAM ),
		new MemoryReadAddress( 0xf000, 0xf7ff, silkworm_bankedrom_r ),
		new MemoryReadAddress( 0xf800, 0xf800, input_port_0_r ),
		new MemoryReadAddress( 0xf801, 0xf801, input_port_1_r ),
		new MemoryReadAddress( 0xf802, 0xf802, input_port_2_r ),
		new MemoryReadAddress( 0xf803, 0xf803, input_port_3_r ),
		new MemoryReadAddress( 0xf806, 0xf806, input_port_4_r ),
		new MemoryReadAddress( 0xf807, 0xf807, input_port_5_r ),
		new MemoryReadAddress( 0xf808, 0xf808, input_port_6_r ),
		new MemoryReadAddress( 0xf809, 0xf809, input_port_7_r ),
		new MemoryReadAddress( 0xf80f, 0xf80f, input_port_8_r ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new MemoryWriteAddress( 0xc000, 0xc1ff, videoram_w, videoram, videoram_size ),
		new MemoryWriteAddress( 0xc200, 0xc3ff, colorram_w, colorram ),
		new MemoryWriteAddress( 0xc400, 0xc5ff, silkworm_videoram_w, silkworm_videoram ),
		new MemoryWriteAddress( 0xc600, 0xc7ff, silkworm_colorram_w, silkworm_colorram ),
		new MemoryWriteAddress( 0xc800, 0xcbff, MWA_RAM, silkworm_videoram2, silkworm_videoram2_size ),
		new MemoryWriteAddress( 0xcc00, 0xcfff, MWA_RAM, silkworm_colorram2 ),
		new MemoryWriteAddress( 0xd000, 0xdfff, MWA_RAM ),
		new MemoryWriteAddress( 0xe000, 0xe7ff, MWA_RAM, spriteram, spriteram_size ),
		new MemoryWriteAddress( 0xe800, 0xefff, silkworm_paletteram_w, silkworm_paletteram ),
		new MemoryWriteAddress( 0xf000, 0xf7ff, MWA_ROM ),
		new MemoryWriteAddress( 0xf800, 0xf805, MWA_RAM, silkworm_scroll ),
                new MemoryWriteAddress( 0xf806, 0xf806, MWA_NOP ),	/* ???? */
		new MemoryWriteAddress( 0xf808, 0xf808, silkworm_bankswitch_w ),
                new MemoryWriteAddress( 0xf809, 0xf809, MWA_NOP ),	/* ???? */
                new MemoryWriteAddress( 0xf80b, 0xf80b, MWA_NOP ),	/* ???? */
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	
	
	static InputPortPtr input_ports = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 bit 0-3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY );
	
		PORT_START(); 	/* IN0 bit 4-7 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON3 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* unused? */
	
		PORT_START(); 	/* IN1 bit 0-3 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_PLAYER2 );
	
		PORT_START(); 	/* IN1 bit 4-7 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* unused? */
	
		PORT_START(); 	/* DSWA bit 0-3 */
		PORT_DIPNAME( 0x03, 0x00, "Coinage", IP_KEY_NONE );
		PORT_DIPSETTING(    0x01, "2 Coins/1 Credit" );
		PORT_DIPSETTING(    0x00, "1 Coin/1 Credit" );
		PORT_DIPSETTING(    0x02, "1 Coin/2 Credits" );
		PORT_DIPSETTING(    0x03, "1 Coin/3 Credits" );
		PORT_DIPNAME( 0x04, 0x00, "A 3/4", IP_KEY_NONE );
		PORT_DIPSETTING(    0x00, "0" );
		PORT_DIPSETTING(    0x04, "1" );
		PORT_DIPSETTING(    0x08, "2" );
		PORT_DIPSETTING(    0x0c, "3" );
	
		PORT_START(); 	/* DSWA bit 4-7 */
		PORT_DIPNAME( 0x03, 0x00, "Lives", IP_KEY_NONE );
		PORT_DIPSETTING(    0x03, "2" );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x01, "4" );
		PORT_DIPSETTING(    0x02, "5" );
		PORT_DIPNAME( 0x04, 0x00, "A 7", IP_KEY_NONE );/* unused? */
		PORT_DIPSETTING(    0x00, "Off" );
		PORT_DIPSETTING(    0x04, "On" );
		PORT_DIPNAME( 0x08, 0x08, "Demo Sounds", IP_KEY_NONE );
		PORT_DIPSETTING(    0x00, "Off" );
		PORT_DIPSETTING(    0x08, "On" );
	
		PORT_START(); 	/* DSWB bit 0-3 */
		PORT_DIPNAME( 0x07, 0x00, "Bonus Life", IP_KEY_NONE );
		PORT_DIPSETTING(    0x00, "50000 200000 500000" );
		PORT_DIPSETTING(    0x01, "100000 300000 800000" );
		PORT_DIPSETTING(    0x02, "50000 200000" );
		PORT_DIPSETTING(    0x03, "100000 300000" );
		PORT_DIPSETTING(    0x04, "50000" );
		PORT_DIPSETTING(    0x05, "100000" );
		PORT_DIPSETTING(    0x06, "200000" );
		PORT_DIPSETTING(    0x07, "None" );
		PORT_DIPNAME( 0x08, 0x00, "B 4", IP_KEY_NONE );/* unused? */
		PORT_DIPSETTING(    0x00, "Off" );
		PORT_DIPSETTING(    0x08, "On" );
	
		PORT_START(); 	/* DSWB bit 4-7 */
		PORT_DIPNAME( 0x07, 0x00, "Difficulty", IP_KEY_NONE );
		PORT_DIPSETTING(    0x00, "0" );
		PORT_DIPSETTING(    0x01, "1" );
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x04, "4" );
		PORT_DIPSETTING(    0x05, "5" );
		/* 0x06 and 0x07 are the same as 0x00 */
		PORT_DIPNAME( 0x08, 0x00, "Allow Continue", IP_KEY_NONE );
		PORT_DIPSETTING(    0x00, "Yes" );
		PORT_DIPSETTING(    0x08, "No" );
	
		PORT_START(); 	/* COIN */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_COIN2 );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		1024,	/* 1024 characters */
		4,	/* 4 bits per pixel */
		new int[] { 0, 1, 2, 3 },	/* the bitplanes are packed in one nibble */
		new int[] { 0*4, 1*4, 2*4, 3*4, 4*4, 5*4, 6*4, 7*4 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		32*8	/* every char takes 32 consecutive bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		2048,	/* 2048 sprites */
		4,	/* 4 bits per pixel */
		new int[] { 0, 1, 2, 3 },	/* the bitplanes are packed in one nibble */
		new int[] { 0*4, 1*4, 2*4, 3*4, 4*4, 5*4, 6*4, 7*4,
				32*8+0*4, 32*8+1*4, 32*8+2*4, 32*8+3*4, 32*8+4*4, 32*8+5*4, 32*8+6*4, 32*8+7*4 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,
				16*32, 17*32, 18*32, 19*32, 20*32, 21*32, 22*32, 23*32 },
		128*8	/* every sprite takes 128 consecutive bytes */
	);
	static GfxLayout spritelayout2x = new GfxLayout
	(
		32,32,	/* 32*32 sprites */
		512,	/* 512 sprites */
		4,	/* 4 bits per pixel */
		new int[] { 0, 1, 2, 3 },	/* the bitplanes are packed in one nibble */
		new int[] { 0*4, 1*4, 2*4, 3*4, 4*4, 5*4, 6*4, 7*4,
				32*8+0*4, 32*8+1*4, 32*8+2*4, 32*8+3*4, 32*8+4*4, 32*8+5*4, 32*8+6*4, 32*8+7*4,
				128*8+0*4, 128*8+1*4, 128*8+2*4, 128*8+3*4, 128*8+4*4, 128*8+5*4, 128*8+6*4, 128*8+7*4,
				160*8+0*4, 160*8+1*4, 160*8+2*4, 160*8+3*4, 160*8+4*4, 160*8+5*4, 160*8+6*4, 160*8+7*4 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,
				16*32, 17*32, 18*32, 19*32, 20*32, 21*32, 22*32, 23*32,
				64*32, 65*32, 66*32, 67*32, 68*32, 69*32, 70*32, 71*32,
				80*32, 81*32, 82*32, 83*32, 84*32, 85*32, 86*32, 87*32 },
		512*8	/* every sprite takes 512 consecutive bytes */
	);
	static GfxLayout tilelayout = new GfxLayout
	(
		16,16,	/* 16*16 tiles */
		2048,	/* 2048 tiles */
		4,	/* 4 bits per pixel */
		new int[] { 0, 1, 2, 3 },	/* the bitplanes are packed in one nibble */
		new int[] { 0*4, 1*4, 2*4, 3*4, 4*4, 5*4, 6*4, 7*4,
				32*8+0*4, 32*8+1*4, 32*8+2*4, 32*8+3*4, 32*8+4*4, 32*8+5*4, 32*8+6*4, 32*8+7*4 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,
				16*32, 17*32, 18*32, 19*32, 20*32, 21*32, 22*32, 23*32 },
		128*8	/* every tile takes 128 consecutive bytes */
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x00000, charlayout,       16*16, 16 ),
		new GfxDecodeInfo( 1, 0x08000, spritelayout,         0, 16 ),
		new GfxDecodeInfo( 1, 0x08000, spritelayout2x,       0, 16 ),	/* double size */
		new GfxDecodeInfo( 1, 0x48000, tilelayout,     2*16*16, 16 ),	/* bg #1 */
		new GfxDecodeInfo( 1, 0x88000, tilelayout,     3*16*16, 16 ),	/* bg #2 */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				6000000,	/* 6 Mhz (?????) */
				0,
				readmem,writemem,null,null,
				interrupt,1
			),
		},
		60,
		10,	/* 10 CPU slices per frame - enough for the sound CPU to read all commands */
		null,
	
		/* video hardware */
		32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
		gfxdecodeinfo,
		256,4*16*16,
		silkworm_vh_convert_color_prom,
	
		VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
		null,
		silkworm_vh_start,
		silkworm_vh_stop,
		silkworm_vh_screenrefresh,
	
		/* sound hardware */
		null,
		null,
		null,
		null,
		null
	);
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr silkworm_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x20000);/* 64k for code */
		ROM_LOAD( "silkworm.4",  0x00000, 0x10000, 0x8242f71e );/* c000-ffff is not used */
		ROM_LOAD( "silkworm.5",  0x10000, 0x10000, 0xdc2e3a8c );/* banked at f000-f7ff */
	
		ROM_REGION(0xc8000);/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "silkworm.2",  0x00000, 0x08000, 0xcc03f6a3 );/* characters */
		ROM_LOAD( "silkworm.6",  0x08000, 0x10000, 0x9fa0b862 );/* sprites */
		ROM_LOAD( "silkworm.7",  0x18000, 0x10000, 0x61dfce63 );/* sprites */
		ROM_LOAD( "silkworm.8",  0x28000, 0x10000, 0xc7a80a5c );/* sprites */
		ROM_LOAD( "silkworm.9",  0x38000, 0x10000, 0x4b6e4340 );/* sprites */
		ROM_LOAD( "silkworm.10", 0x48000, 0x10000, 0xfad1bcad );/* tiles #1 */
		ROM_LOAD( "silkworm.11", 0x58000, 0x10000, 0x35f18a5b );/* tiles #1 */
		ROM_LOAD( "silkworm.12", 0x68000, 0x10000, 0xc4faff70 );/* tiles #1 */
		ROM_LOAD( "silkworm.13", 0x78000, 0x10000, 0x98692fdd );/* tiles #1 */
		ROM_LOAD( "silkworm.14", 0x88000, 0x10000, 0x23e5846f );/* tiles #2 */
		ROM_LOAD( "silkworm.15", 0x98000, 0x10000, 0xb389f5f5 );/* tiles #2 */
		ROM_LOAD( "silkworm.16", 0xa8000, 0x10000, 0x783c76d8 );/* tiles #2 */
		ROM_LOAD( "silkworm.17", 0xb8000, 0x10000, 0xf292cf5e );/* tiles #2 */
	
		ROM_REGION(0x20000);/* 64k for the audio CPU */
		ROM_LOAD( "silkworm.3",  0x00000, 0x08000, 0x0867f097 );
		ROM_LOAD( "silkworm.1",  0x08000, 0x08000, 0x83601ea4 );/* don't know what this is */
																/* nor where it should be mapped */
	ROM_END(); }}; 
	
	
	
	public static GameDriver silkworm_driver = new GameDriver
	(
		"Silkworm",
		"silkworm",
		"Nicola Salmoria",
		machine_driver,
	
		silkworm_rom,
		null, null,
		null,
	
		null/*TBR*/,input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,
	
		null, null, null,
		ORIENTATION_DEFAULT,
	
		null, null
	);
}
