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
 * using automatic conversion tool v0.03
 * converted at : 06-09-2011 13:38:45
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
import static sndhrdw.rastan.*;
import static vidhrdw.generic.*;
import static vidhrdw.rastan.*;
import static mame.inptport.*;

public class rastan
{
	

	
	static MemoryReadAddress rastan_readmem[] =
	{
		new MemoryReadAddress( 0x200000, 0x20ffff, rastan_paletteram_r ),
		new MemoryReadAddress( 0xc00000, 0xc03fff, rastan_videoram1_r),
		new MemoryReadAddress( 0xc04000, 0xc07fff, rastan_videoram2_r),
		new MemoryReadAddress( 0xc08000, 0xc0bfff, rastan_videoram3_r),
		new MemoryReadAddress( 0xc0c000, 0xc0ffff, rastan_videoram4_r),
		new MemoryReadAddress( 0xd00000, 0xd0ffff, rastan_spriteram_r),
	
	//	new MemoryReadAddress( 0x3e0001, 0x3e0001, rastan_sound_port_r ),
		new MemoryReadAddress( 0x3e0003, 0x3e0003, rastan_sound_comm_r ),
	
		new MemoryReadAddress( 0x390001, 0x390001, input_port_0_r ),
		new MemoryReadAddress( 0x390003, 0x390003, input_port_0_r ),
		new MemoryReadAddress( 0x390005, 0x390005, input_port_1_r ),
		new MemoryReadAddress( 0x390007, 0x390007, input_port_2_r ),
	
		new MemoryReadAddress( 0x390009, 0x390009, input_port_3_r ),	/* DSW 1 */
		new MemoryReadAddress( 0x39000b, 0x39000b, input_port_4_r ),	/* DSW 2 */
		new MemoryReadAddress( 0x10c000, 0x10ffff, rastan_ram_r ),	/* RAM */
		new MemoryReadAddress( 0x000000, 0x05ffff, MRA_ROM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress rastan_writemem[] =
	{
		new MemoryWriteAddress( 0x200000, 0x20ffff, rastan_paletteram_w ),
		new MemoryWriteAddress( 0xc00000, 0xc03fff, rastan_videoram1_w, videoram, videoram_size ), /*this is just a fake */
		new MemoryWriteAddress( 0xc04000, 0xc07fff, rastan_videoram2_w ),
		new MemoryWriteAddress( 0xc08000, 0xc0bfff, rastan_videoram3_w ),
		new MemoryWriteAddress( 0xc0c000, 0xc0ffff, rastan_videoram4_w ),
	
		new MemoryWriteAddress( 0xc20000, 0xc20003, rastan_scrollY_w ),  /* scroll Y  1st.w plane1  2nd.w plane2 */
		new MemoryWriteAddress( 0xc40000, 0xc40003, rastan_scrollX_w ),  /* scroll X  1st.w plane1  2nd.w plane2 */
		new MemoryWriteAddress( 0xd00000, 0xd0ffff, rastan_spriteram_w ),
	
		new MemoryWriteAddress( 0x3e0001, 0x3e0001, rastan_sound_port_w ),
		new MemoryWriteAddress( 0x3e0003, 0x3e0003, rastan_sound_comm_w ),
	
		new MemoryWriteAddress( 0xc50000, 0xc50001, MWA_NOP ),     /* 0 only (rarely)*/
		new MemoryWriteAddress( 0x350008, 0x350009, MWA_NOP ),     /* 0 only (often) ? */
		new MemoryWriteAddress( 0x380000, 0x380001, MWA_NOP ),     /*0000,0060,0063,0063b   ? */
		new MemoryWriteAddress( 0x3c0000, 0x3c0001, MWA_NOP ),     /*0000,0020,0063,0992,1753 (very often) watchdog? */
		new MemoryWriteAddress( 0x10c000, 0x10ffff, rastan_ram_w ),
		new MemoryWriteAddress( 0x000000, 0x05ffff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	
	
	
	static MemoryReadAddress rastan_s_readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new MemoryReadAddress( 0x8000, 0x8fff, MRA_RAM ),
	        new MemoryReadAddress( 0x9000, 0x9000, rYMport ),
	        new MemoryReadAddress( 0x9001, 0x9001, rYMdata ),
	        new MemoryReadAddress( 0x9002, 0x9100, MRA_RAM ),
	        new MemoryReadAddress( 0xa000, 0xa000, r_rd_a000 ),
	        new MemoryReadAddress( 0xa001, 0xa001, r_rd_a001 ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress rastan_s_writemem[] =
	{
		new MemoryWriteAddress( 0x8000, 0x8fff, MWA_RAM ),
	        new MemoryWriteAddress( 0xa000, 0xa000, r_wr_a000 ),
	        new MemoryWriteAddress( 0xa001, 0xa001, r_wr_a001 ),
		new MemoryWriteAddress( 0x9000, 0x9000, wYMport ),
		new MemoryWriteAddress( 0x9001, 0x9001, wYMdata ),
	        new MemoryWriteAddress( 0xb000, 0xb000, r_wr_b000 ),
	        new MemoryWriteAddress( 0xc000, 0xc000, r_wr_c000 ),
	        new MemoryWriteAddress( 0xd000, 0xd000, r_wr_d000 ),
		new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	
	
	
	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_LEFT, OSD_KEY_RIGHT,
					OSD_KEY_CONTROL, OSD_KEY_ALT, 0, 0 }
		),
		new InputPort(	/* IN1 */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* IN2 */
			0xff-0x40-0x20 -0x80,
			new int[] { OSD_KEY_7, 0, OSD_KEY_6, OSD_KEY_1, OSD_KEY_2, OSD_KEY_3, OSD_KEY_4, OSD_KEY_8 }
		),
		new InputPort(	/* DSW1 */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW2 */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )  /* end of table */
	};
	
	static TrakPort trak_ports[] =
	{
	        new TrakPort( -1 )
	};
	
	static KEYSet keys[] =
	{
	        new KEYSet( 0, 0, "MOVE UP" ),
	        new KEYSet( 0, 2, "MOVE LEFT"  ),
	        new KEYSet( 0, 3, "MOVE RIGHT" ),
	        new KEYSet( 0, 1, "MOVE DOWN" ),
	        new KEYSet( 0, 4, "FIRE" ),
	        new KEYSet( 0, 5, "WARP" ),
	        new KEYSet( -1 )
	};
	
	
	static DSW dsw[] =
	{
		new DSW( 3, 0x04, "TEST MODE", new String[] { "ON", "OFF" } ),
		new DSW( 4, 0x03, "DIFFICULTY", new String[] { "HARDEST", "DIFFICULT", "EASIEST", "EASY" } ),
			/* not a mistake, EASIEST and EASY are swapped */
		new DSW( 4, 0x0c, "BONUS PLAYER", new String[] { "250 000 PTS", "200 000 PTS", "150 000 PTS", "100 000 PTS" } ),
		new DSW( 4, 0x30, "LIVES", new String[] { "6", "5", "4", "3" } ),
		new DSW( 4, 0x40, "CONTINUE MODE", new String[] { "OFF", "ON" } ),
		new DSW( -1 )
	};
	
	
	
	static GfxLayout spritelayout1 = new GfxLayout
	(
		8,8,	/* 8*8 sprites */
		4096,	/* 4096 sprites */
		4,	/* 4 bits per pixel */
		new int[] { 0, 1, 2, 3 },
	        new int[] { 0, 4, 0x10000*8+0 ,0x10000*8+4, 8+0, 8+4, 0x10000*8+8+0, 0x10000*8+8+4 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		16*8	/* every sprite takes 16 consecutive bytes */
	);
	
	static GfxLayout spritelayout2 = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		1024,	/* 1024 sprites */
		4,	/* 4 bits per pixel */
		new int[] { 0, 1, 2, 3 },
	new int[] {
	0, 4, 0x10000*8+0 ,0x10000*8+4,
	8+0, 8+4, 0x10000*8+8+0, 0x10000*8+8+4,
	16+0, 16+4, 0x10000*8+16+0, 0x10000*8+16+4,
	24+0, 24+4, 0x10000*8+24+0, 0x10000*8+24+4
	},
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,
				8*32, 9*32, 10*32, 11*32, 12*32, 13*32, 14*32, 15*32 },
		64*8	/* every sprite takes 64 consecutive bytes */
	);
	
	/* there's nothing here, this is just a placeholder to let the video hardware */
	/* pick the remapped color table and dynamically build the real one. */
	
	static GfxLayout fakelayout = new GfxLayout
	(
		1,1,
		0,
		1,
		new int[] { 0 },
		new int[] { 0 },
		new int[] { 0 },
		0
	);
	
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x00000, spritelayout1,  0, 0x50 ),	/* sprites 8x8*/
		new GfxDecodeInfo( 1, 0x20000, spritelayout1,  0, 0x50 ),	/* sprites 8x8*/
		new GfxDecodeInfo( 1, 0x40000, spritelayout2,  0, 0x50 ),	/* sprites 16x16*/
		new GfxDecodeInfo( 1, 0x60000, spritelayout2,  0, 0x50 ),	/* sprites 16x16*/
		new GfxDecodeInfo( 0, 0,      fakelayout,    0x50*16, 0xff ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	/* RASTAN doesn't have a color PROM, it uses a RAM to generate colors */
	/* and change them during the game. Here is the list of all the colors is uses. */
	/* We cannot do it this time :( I've checked : RASTAN uses more than 512 colors*/
	
	
	static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_M68000,
				8000000,	/* 8 Mhz */
				0,
				rastan_readmem,rastan_writemem,null,null,
				rastan_interrupt,1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				4000000,	/* 4 Mhz */
				2,
				rastan_s_readmem,rastan_s_writemem,null,null,
				rastan_s_interrupt,4
			)
		},
		60,
		rastan_machine_init,
	
		/* video hardware */
		40*8, 30*8, new rectangle( 0*8, 40*8-1, 0*8, 30*8-1 ),
		gfxdecodeinfo,
		256,0x50*16+256, /* looking on palette it seems that RASTAN uses 0x0-0x4f color schemes 16 colors each*/
		rastan_vh_convert_color_prom,
	
		VIDEO_TYPE_RASTER,
		null,
		rastan_vh_start,
		rastan_vh_stop,
		rastan_vh_screenrefresh,
	
		/* sound hardware */
		null,
		rastan_sh_init,
		null, //rastan_sh_start,
		null, //AY8910_sh_stop,
		null  //osd_ym2151_update  //AY8910_sh_update
	);
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	static RomLoadPtr rastan_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x60000);/* 6*64k for 68000 code */
		ROM_LOAD( "IC19_38.bin", 0x00000, 0x10000, 0 );
		ROM_LOAD( "IC07_37.bin", 0x10000, 0x10000, 0 );
		ROM_LOAD( "IC20_40.bin", 0x20000, 0x10000, 0 );
		ROM_LOAD( "IC08_39.bin", 0x30000, 0x10000, 0 );
		ROM_LOAD( "IC21_42.bin", 0x40000, 0x10000, 0 );
		ROM_LOAD( "IC09_43.bin", 0x50000, 0x10000,0  );
	
		ROM_REGION(0x80000);/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "IC40_01.bin",  0x00000, 0x10000, 0 );/* 8x8 0 */
		ROM_LOAD( "IC67_02.bin",  0x10000, 0x10000, 0 );/* 8x8 1 */
		ROM_LOAD( "IC39_03.bin",  0x20000, 0x10000, 0 );/* 8x8 0 */
		ROM_LOAD( "IC66_04.bin",  0x30000, 0x10000, 0 );/* 8x8 1 */
		ROM_LOAD( "IC15_05.bin",  0x40000, 0x10000, 0 );/* sprites 1a */
		ROM_LOAD( "IC28_06.bin",  0x50000, 0x10000, 0 );/* sprites 1b */
		ROM_LOAD( "IC14_07.bin",  0x60000, 0x10000, 0 );/* sprites 2a */
		ROM_LOAD( "IC27_08.bin",  0x70000, 0x10000, 0 );/* sprites 2b */
	
		ROM_REGION(0x10000);/* 64k for the audio CPU */
		ROM_LOAD( "IC49_19.bin", 0x0000, 0x10000, 0 );  /* Audio CPU is a Z80  */
	                                                     /* sound chip is YM2151*/
		ROM_REGION(0x10000);/* 64k for the samples */
		ROM_LOAD( "IC76_20.bin", 0x0000, 0x10000, 0 );/* samples are 4bit ADPCM */
	ROM_END(); }}; 
	
	
	
	public static GameDriver rastan_driver = new GameDriver
	(
		"RASTAN",
		"rastan",
		"JAREK BURCZYNSKI",
		machine_driver,
	
		rastan_rom,
		null, null,
		null,
	
		input_ports, null, trak_ports, dsw, keys,
	
		null, null, null,   /* colors, palette, colortable */
		ORIENTATION_DEFAULT,
		null, null
	);
	
}
