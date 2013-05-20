/***************************************************************************

  Sidearms
  ========

  Driver provided by Paul Leaman (paull@vortexcomputing.demon.co.uk)

  Please do not send anything large to this address without asking me
  first.

  This is obviously not complete. I might not get the time to finish this
  which is why it is released as it is.

  The game appears to use 3 Z80s. Some code for one of those Z80s is missing.


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
import static sndhrdw._8910intf.*;
import static sndhrdw.capcom.*;
import static sndhrdw.generic.*;
import static machine.sidearms.*;
import static vidhrdw.generic.*;
import static vidhrdw.sidearms.*;
import static mame.inptport.*;
import static mame.memoryH.*;

public class sidearms
{
		
	static MemoryReadAddress sidearms_readmem[] =
	{
	        new MemoryReadAddress( 0x0000, 0xbfff, MRA_ROM ),   /* CODE */
	        new MemoryReadAddress( 0xc800, 0xc800, input_port_0_r ),
	        new MemoryReadAddress( 0xc801, 0xc801, input_port_1_r ),
	        new MemoryReadAddress( 0xc802, 0xc802, input_port_2_r ),
	        new MemoryReadAddress( 0xc803, 0xc803, input_port_3_r ),
	        new MemoryReadAddress( 0xc804, 0xc804, input_port_4_r ),
	        new MemoryReadAddress( 0xc805, 0xc805, input_port_5_r ),
	        new MemoryReadAddress( 0xc000, 0xffff, MRA_RAM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	
	
	static MemoryWriteAddress sidearms_writemem[] =
	{
	        new MemoryWriteAddress( 0xd000, 0xd7ff, videoram_w, videoram, videoram_size ),
	        new MemoryWriteAddress( 0xd800, 0xdfff, colorram_w, colorram, null ),
	        new MemoryWriteAddress( 0xc800, 0xc800, sound_command_w),
	        new MemoryWriteAddress( 0xc801, 0xc801, blktiger_bankswitch_w ),
	        new MemoryWriteAddress( 0xc808, 0xc809, MWA_RAM, sidearms_bg_scrollx ),
	        new MemoryWriteAddress( 0xf000, 0xffff, MWA_RAM, spriteram, spriteram_size ),
	        new MemoryWriteAddress( 0xe000, 0xefff, MWA_RAM ),
	        new MemoryWriteAddress( 0xc000, 0xc7ff, sidearms_paletteram_w, sidearms_paletteram, sidearms_paletteram_size),
		new MemoryWriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	/*
	  The sound board reads both sound chip control registers (or a value supplied
	  by the main CPU. It will not output any sound unless I return 3. Must be
	  some "is device ready?" code.
	*/
        public static ReadHandlerPtr ControlPort_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	        return 3;
	}};
	
	static MemoryReadAddress sound_readmem[] =
	{
	        new MemoryReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
	        new MemoryReadAddress( 0xd000, 0xd000, sound_command_latch_r ),
	        new MemoryReadAddress( 0xf000, 0xf000, ControlPort_r ),
	        new MemoryReadAddress( 0xf001, 0xf001, MRA_RAM ),
	        new MemoryReadAddress( 0xf002, 0xf002, ControlPort_r ),
	        new MemoryReadAddress( 0xf003, 0xf003, MRA_RAM ),
	        new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress sound_writemem[] =
	{
	        new MemoryWriteAddress( 0xc000, 0xc7ff, MWA_RAM ),
	        new MemoryWriteAddress( 0xf000, 0xf000, AY8910_control_port_0_w ),
	        new MemoryWriteAddress( 0xf001, 0xf001, AY8910_write_port_0_w ),
	        new MemoryWriteAddress( 0xf002, 0xf002, AY8910_control_port_1_w ),
	        new MemoryWriteAddress( 0xf003, 0xf003, AY8910_write_port_1_w ),
	        new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	
	static InputPortPtr input_ports = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
	
		PORT_START(); 	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );/* probably unused */
	
		PORT_START(); 	/* DSW0 */
	        PORT_DIPNAME( 0x80, 0x80, "Service Mode", IP_KEY_NONE );
	        PORT_DIPSETTING(    0x00, "On" );
	        PORT_DIPSETTING(    0x80, "Off");
	
	        /* These aren't right either */
	        PORT_DIPNAME( 0x07, 0x07, "Coin 1", IP_KEY_NONE );
	        PORT_DIPSETTING(    0x00, "1");
	        PORT_DIPSETTING(    0x01, "2");
	        PORT_DIPSETTING(    0x02, "3");
	        PORT_DIPSETTING(    0x03, "4");
	        PORT_DIPSETTING(    0x04, "5");
	        PORT_DIPSETTING(    0x05, "6");
	        PORT_DIPSETTING(    0x06, "7");
	        PORT_DIPSETTING(    0x07, "1 coin 1 credit");
	
	        PORT_DIPNAME( 0x38, 0x38, "Coin 2", IP_KEY_NONE );
	        PORT_DIPSETTING(    0x00, "1");
	        PORT_DIPSETTING(    0x08, "2");
	        PORT_DIPSETTING(    0x10, "3");
	        PORT_DIPSETTING(    0x18, "4");
	        PORT_DIPSETTING(    0x20, "5");
	        PORT_DIPSETTING(    0x28, "6");
	        PORT_DIPSETTING(    0x30, "7");
	        PORT_DIPSETTING(    0x38, "1 coin 1 credit");
	
	
	        PORT_START();       /* DSW1 */
	        /* Nor these ... */
	        PORT_DIPNAME( 0x80, 0x00, "Type", IP_KEY_NONE );
	        PORT_DIPSETTING(    0x00, "Upright" );
	        PORT_DIPSETTING(    0x80, "Table");
	        PORT_DIPNAME( 0x40, 0x40, "Continue", IP_KEY_NONE );
	        PORT_DIPSETTING(    0x00, "Off");
	        PORT_DIPSETTING(    0x40, "On" );
	        PORT_DIPNAME( 0x20, 0x20, "Demo sound", IP_KEY_NONE );
	        PORT_DIPSETTING(    0x00, "Off");
	        PORT_DIPSETTING(    0x20, "On" );
	        PORT_DIPNAME( 0x1c, 0x0c, "Difficulty", IP_KEY_NONE );
	        PORT_DIPSETTING(    0x00, "Very difficult");
	        PORT_DIPSETTING(    0x04, "Diff 2" );
	        PORT_DIPSETTING(    0x08, "Diff 1" );
	        PORT_DIPSETTING(    0x0c, "Normal" );
	        PORT_DIPSETTING(    0x10, "Easy 1" );
	        PORT_DIPSETTING(    0x14, "Easy 2" );
	        PORT_DIPSETTING(    0x18, "Easy 3" );
	        PORT_DIPSETTING(    0x1c, "Very easy");
	        PORT_DIPNAME( 0x03, 0x03, "Players", IP_KEY_NONE );
	        PORT_DIPSETTING(    0x00, "7");
	        PORT_DIPSETTING(    0x01, "5" );
	        PORT_DIPSETTING(    0x02, "2" );
	        PORT_DIPSETTING(    0x03, "3" );
	
	        PORT_START();       /* DSW2 */
	        PORT_DIPNAME( 0x81, 0x81, "Screen stop", IP_KEY_NONE );
	        PORT_DIPSETTING(    0x81, "Off");
	        PORT_DIPSETTING(    0x00, "On" );
	INPUT_PORTS_END(); }}; 
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
	        2048,   /* 1024 characters */
		2,	/* 2 bits per pixel */
	        new int[] { 0, 4 },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		16*8	/* every char takes 16 consecutive bytes */
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
	        2048,   /* 2048 sprites */
		4,	/* 4 bits per pixel */
	        new int[] { 0x20000*8+4, 0x20000*8+0, 4, 0 },
	        new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3,
		    32*8+0, 32*8+1, 32*8+2, 32*8+3, 33*8+0, 33*8+1, 33*8+2, 33*8+3 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		64*8	/* every sprite takes 64 consecutive bytes */
	);
	
	static GfxLayout tilelayout = new GfxLayout
	(
		32,32,  /* 32*32 tiles */
		512,    /* 512 tiles */
		4,      /* 4 bits per pixel */
		new int[] { 0x20000*8+4, 0x20000*8, 4, 0 },
		new int[] {
				0,       1,       2,       3,       8+0,       8+1,       8+2,       8+3,
			32*16+0, 32*16+1, 32*16+2, 32*16+3, 32*16+8+0, 32*16+8+1, 32*16+8+2, 32*16+8+3,
			64*16+0, 64*16+1, 64*16+2, 64*16+3, 64*16+8+0, 64*16+8+1, 64*16+8+2, 64*16+8+3,
			96*16+0, 96*16+1, 96*16+2, 96*16+3, 96*16+8+0, 96*16+8+1, 96*16+8+2, 96*16+8+3,
		},
		new int[] {
			0*16,  1*16,  2*16,  3*16,  4*16,  5*16,  6*16,  7*16,
			8*16,  9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16,
			16*16, 17*16, 18*16, 19*16, 20*16, 21*16, 22*16, 23*16,
			24*16, 25*16, 26*16, 27*16, 28*16, 29*16, 30*16, 31*16
		},
		2*32*32    /* every sprite takes this number of consecutive bytes */
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
	        /*   start    pointer       colour start   number of colours */
	        new GfxDecodeInfo( 1, 0x00000, charlayout,      0,  32 ),
	        new GfxDecodeInfo( 1, 0x10000, tilelayout,   32*4, 16 ),
	        new GfxDecodeInfo( 1, 0x50000, spritelayout,  32*4+16*16,  16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static MachineDriver sidearms_machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				4000000,	/* 4 Mhz (?) */
				0,
	                        sidearms_readmem,sidearms_writemem,null,null,
	                        blktiger_interrupt,1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				3000000,	/* 3 Mhz ??? */
				2,	/* memory region #2 */
				sound_readmem,sound_writemem,null,null,
	                        capcom_sh_interrupt,12
			)
		},
		60,
		10,	/* 10 CPU slices per frame - enough for the sound CPU to read all commands */
		null,
	
		/* video hardware */
	        48*8, 30*8, new rectangle( 0*8, 48*8-1, 2*8, 32*8-1 ),
		gfxdecodeinfo,
	        256,               /* 256 colours */
	        32*4+16*16+8*16,   /* Colour table length */
	        sidearms_vh_convert_color_prom,
	
	        VIDEO_TYPE_RASTER,
	        null,
	        sidearms_vh_start,
	        sidearms_vh_stop,
	        sidearms_vh_screenrefresh,
	
		/* sound hardware */
		null,
		null,
	        capcom_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);
	
	
	static RomLoadPtr sidearms_rom = new RomLoadPtr(){ public void handler(){   //NOTE most of the loading code is changed to work with 0.36 romset
	        ROM_REGION(0x50000);    /* 64k for code + banked ROMs images */
                ROM_LOAD( "sa03.bin",     0x00000, 0x08000, 0xe10fe6a0 );        /* CODE */
                ROM_LOAD( "a_14e.rom",    0x10000, 0x08000, 0x4925ed03 );        /* 0+1 */
                ROM_LOAD( "a_12e.rom",    0x18000, 0x08000, 0x81d0ece7 );        /* 2+3 */
	
                ROM_REGION(0x90000);     /* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "a_10j.rom",    0x00000, 0x4000, 0x651fef75 );  /* characters */

                ROM_LOAD( "b_13d.rom", 0x10000, 0x8000, 0xa0101f3e ); /* tiles */
                ROM_LOAD( "b_13e.rom", 0x18000, 0x8000, 0x89f8efec ); /* tiles */
                ROM_LOAD( "b_13f.rom", 0x20000, 0x8000, 0x184ae81c ); /* tiles */
                ROM_LOAD( "b_13g.rom", 0x28000, 0x8000, 0xceb77765 ); /* tiles */

                ROM_LOAD( "b_14d.rom", 0x30000, 0x8000, 0xd2d2105e ); /* tiles */
                ROM_LOAD( "b_14e.rom", 0x38000, 0x8000, 0x184ae81c ); /* tiles */
                ROM_LOAD( "b_14f.rom", 0x40000, 0x8000, 0xf8089804 ); /* tiles */
                ROM_LOAD( "b_14g.rom", 0x48000, 0x8000, 0xecfb6047 ); /* tiles */


                ROM_LOAD( "b_13b.rom", 0x50000, 0x8000, 0xe2f99cc3 ); /* sprites */
                ROM_LOAD( "b_11a.rom", 0x58000, 0x8000, 0xc805167b ); /* sprites */
                ROM_LOAD( "b_13a.rom", 0x60000, 0x8000, 0x778f5043 ); /* sprites */
                ROM_LOAD( "b_11b.rom", 0x68000, 0x8000, 0xd21e2362 ); /* sprites */

                ROM_LOAD( "b_14b.rom", 0x70000, 0x8000, 0x863bc3f1 ); /* sprites */
                ROM_LOAD( "b_12a.rom", 0x78000, 0x8000, 0x770e34ca ); /* sprites */
                ROM_LOAD( "b_14a.rom", 0x80000, 0x8000, 0x70aa3e6c ); /* sprites */
                ROM_LOAD( "b_12b.rom", 0x88000, 0x8000, 0x63102ab8 ); /* sprites */
	               
	        ROM_REGION(0x10000);/* 64k for the audio CPU */
	        ROM_LOAD( "a_04k.rom",    0x0000, 0x8000, 0x34efe2d2 );
	
	        ROM_REGION(0x08000);/* 64k tile map */
	        ROM_LOAD( "b_03d.rom",    0x0000, 0x8000, 0x6f348008 );
	
	        ROM_REGION(0x10000);/* 64k for CPU (incomplete ROM set????) */
	        ROM_LOAD( "b_11j.rom",    0x0000, 0x8000, 0x134dc35b );
	ROM_END(); }}; 
	
	
	public static GameDriver sidearms_driver = new GameDriver
	(
	        "Sidearms",
	        "sidearms",
	        "Paul Leaman",
	        sidearms_machine_driver,
	
	        sidearms_rom,
	        null,
	        null,null,
	
	        null,input_ports,null,null,null,
	        null, null, null,
	
	        ORIENTATION_DEFAULT,
	        null, null
	);
	
}
