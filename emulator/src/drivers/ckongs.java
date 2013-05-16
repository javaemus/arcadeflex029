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
 *NOTES: romsets are from v0.36 roms
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
import static machine.scramble.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.generic.*;
import static sndhrdw.scramble.*;
import static vidhrdw.generic.*;
import static vidhrdw.galaxian.*;
import static mame.memoryH.*;

public class ckongs
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x6000, 0x6bff, MRA_RAM ),	/* RAM */
		new MemoryReadAddress( 0x9000, 0x93ff, MRA_RAM ),	/* Video RAM */
		new MemoryReadAddress( 0x9800, 0x987f, MRA_RAM ),	/* screen attributes, sprites, bullets */
		new MemoryReadAddress( 0x0000, 0x5fff, MRA_ROM ),
		new MemoryReadAddress( 0xb000, 0xb000, MRA_NOP ),
		new MemoryReadAddress( 0x7000, 0x7000, input_port_0_r ),	/* IN0 */
		new MemoryReadAddress( 0x7001, 0x7001, input_port_1_r ),	/* IN1 */
		new MemoryReadAddress( 0x7002, 0x7002, input_port_2_r ),	/* IN2 */
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x6000, 0x6bff, MWA_RAM ),
		new MemoryWriteAddress( 0x9000, 0x93ff, videoram_w, videoram,videoram_size ),
		new MemoryWriteAddress( 0x9800, 0x983f, galaxian_attributes_w, galaxian_attributesram ),
		new MemoryWriteAddress( 0x9840, 0x985f, MWA_RAM, spriteram ,spriteram_size),
		new MemoryWriteAddress( 0x9860, 0x987f, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
		new MemoryWriteAddress( 0xa801, 0xa801, interrupt_enable_w ),
		new MemoryWriteAddress( 0xa804, 0xa804, galaxian_stars_w ),
		new MemoryWriteAddress( 0xa802, 0xa802, MWA_NOP ),
		new MemoryWriteAddress( 0xa806, 0xa807, MWA_NOP ),
		new MemoryWriteAddress( 0x7800, 0x7800, sound_command_w ),
		new MemoryWriteAddress( 0x0000, 0x5fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};



	static MemoryReadAddress sound_readmem[] =
	{
		new MemoryReadAddress( 0x8000, 0x83ff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress sound_writemem[] =
	{
		new MemoryWriteAddress( 0x8000, 0x83ff, MWA_RAM ),
		new MemoryWriteAddress( 0x0000, 0x1fff, MWA_ROM ),
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

        static KEYSet[] keys =
        {
                new KEYSet( 0, 4, "MOVE UP" ),
                 new KEYSet( 0, 0, "PL1 MOVE UP" ),
                 new KEYSet( 0, 5, "PL1 MOVE LEFT"  ),
                 new KEYSet( 0, 4, "PL1 MOVE RIGHT" ),
                 new KEYSet( 2, 0, "PL1 MOVE DOWN" ),
                 new KEYSet( 0, 1, "PL1 FIRE1" ),
                 new KEYSet( 0, 3, "PL1 FIRE2" ),
                 new KEYSet( 2, 4, "PL1 MOVE UP" ),
                 new KEYSet( 1, 5, "PL1 MOVE LEFT"  ),
                 new KEYSet( 1, 4, "PL1 MOVE RIGHT" ),
                 new KEYSet( 2, 6, "PL1 MOVE DOWN" ),
                 new KEYSet( 1, 2, "PL1 FIRE1" ),
                 new KEYSet( 1, 3, "PL1 FIRE2" ),
                 new KEYSet( -1 )
        };


        static TrakPort[] trak_ports =
                { new TrakPort(-1) };

	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { OSD_KEY_UP, OSD_KEY_ALT, 0, OSD_KEY_CONTROL,
						OSD_KEY_RIGHT, OSD_KEY_LEFT, 0, OSD_KEY_3 }
		),
		new InputPort(	/* IN1 */
			0xff,
			new int[] { 0, 0, OSD_KEY_ALT, OSD_KEY_CONTROL,
						OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_2, OSD_KEY_1 }
		),
		new InputPort(	/* IN2 */
			0xff,
			new int[] { OSD_KEY_DOWN, 0, 0, 0, OSD_KEY_UP, 0, OSD_KEY_DOWN, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};


		
	static DSW dsw[] =
	{
		new DSW( 2, 0x04, "LIVES", new String[] { "4", "3" }, 1 ),
		new DSW( 2, 0x02, "SW3", new String[] { "OFF", "ON" } ),
		new DSW( -1 )
	};


	
	static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                256,	/* 256 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 512*8*8 },	/* the two bitplanes are separated */
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                128,	/* 128 sprites */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 128*16*16 },	/* the two bitplanes are separated */
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7,
                                8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                                16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
                32*8	/* every sprite takes 32 consecutive bytes */
	);
	/* there's nothing here, this is just a placeholder to let the video hardware */
	/* pick the color table */
	static GfxLayout bulletlayout = new GfxLayout
	(
                /* there is no gfx ROM for this one, it is generated by the hardware */
                3,1,	/* 3*1 line */
                1,	/* just one */
                1,	/* 1 bit per pixel */
                new int[]{ 0 },
                new int[]{ 2, 2, 2 },	/* I "know" that this bit is 1 */
                new int[]{ 0 },	/* I "know" that this bit is 1 */
                0	/* no use */
	);


	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,     0, 8 ),
		new GfxDecodeInfo( 1, 0x0000, spritelayout,   0, 8 ),
		new GfxDecodeInfo( 1, 0x0000, bulletlayout, 8*4, 2 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};


	
	static char color_prom[] =
	{
		/* palette */
		0x00,0xFF,0xFF,0xFF,0x00,0x38,0xF8,0x07,0x00,0xC0,0x3F,0x38,0x00,0xF8,0xC7,0x3F,
		0x00,0x07,0x38,0xC0,0x00,0x38,0x3F,0xC7,0x00,0xC7,0xFF,0xF8,0x00,0xC0,0x07,0xFF,
	};



	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				readmem, writemem, null, null,
				scramble_vh_interrupt,1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				1789750,	/* 1.78975 Mhz?????? */
				2,	/* memory region #2 */
				sound_readmem, sound_writemem, sound_readport, sound_writeport,
				scramble_sh_interrupt, 10
			)
		},
		60,
		null,
	
		/* video hardware */
		32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
                gfxdecodeinfo,
                32+64,8*4+2*2,	/* 32 for the characters, 64 for the stars */
                galaxian_vh_convert_color_prom,

                VIDEO_TYPE_RASTER,
                null,
                scramble_vh_start,
                generic_vh_stop,
                galaxian_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		scramble_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);



	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	static RomLoadPtr ckongs_rom= new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
		    ROM_LOAD( "vid_2c.bin",   0x0000, 0x1000, 0x49a8c234 );
                    ROM_LOAD( "vid_2e.bin",   0x1000, 0x1000, 0xf1b667f1 );
                    ROM_LOAD( "vid_2f.bin",   0x2000, 0x1000, 0xb194b75d );
                    ROM_LOAD( "vid_2h.bin",   0x3000, 0x1000, 0x2052ba8a );
                    ROM_LOAD( "vid_2j.bin",   0x4000, 0x1000, 0xb377afd0 );
                    ROM_LOAD( "vid_2l.bin",   0x5000, 0x1000, 0xfe65e691 );

		ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
		  ROM_LOAD( "vid_5f.bin",   0x0000, 0x1000, 0x7866d2cb );
                  ROM_LOAD( "vid_5h.bin",   0x1000, 0x1000, 0x7311a101 );

		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		 ROM_LOAD( "turt_snd.5c",  0x0000, 0x1000, 0xf0c30f9a );
                 ROM_LOAD( "snd_5d.bin",   0x1000, 0x1000, 0x892c9547 );
                               
                ROM_END();
        }};
	
	
	
	public static GameDriver ckongs_driver = new GameDriver
	(
                "Crazy Kong (Scramble Hardware)",
		"ckongs",
                "NICOLA SALMORIA",
		machine_driver,
	
		ckongs_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		color_prom, null, null,
		ORIENTATION_ROTATE_90,
	
		null, null
	);
}

