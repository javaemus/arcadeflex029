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
 * NOTES: romsets are from v0.36 roms
 *   theend romset is renamed to theends in romset v0.36
 * 
 */


package drivers;

import static arcadeflex.libc.*;
import static mame.mame.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static machine.scramble.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.generic.*;
import static sndhrdw.scramble.*;
import static vidhrdw.generic.*;
import static vidhrdw.galaxian.*;
import static mame.inptport.*;
import static sndhrdw.frogger.*;


public class scramble {




	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x4000, 0x4bff, MRA_RAM ),	/* RAM and Video RAM */
		new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new MemoryReadAddress( 0x7000, 0x7000, MRA_NOP ),
                new MemoryReadAddress( 0x7800, 0x7800, MRA_NOP ),
		new MemoryReadAddress( 0x8100, 0x8100, input_port_0_r ),	/* IN0 */
		new MemoryReadAddress( 0x8101, 0x8101, input_port_1_r ),	/* IN1 */
		new MemoryReadAddress( 0x8102, 0x8102, scramble_IN2_r ),	/* IN2 */
		new MemoryReadAddress( 0x5000, 0x507f, MRA_RAM ),	/* screen attributes, sprites, bullets */
		new MemoryReadAddress( 0x8202, 0x8202, scramble_protection_r ),
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x4000, 0x47ff, MWA_RAM ),
		new MemoryWriteAddress( 0x4800, 0x4bff, videoram_w, videoram, videoram_size ),
		new MemoryWriteAddress( 0x5000, 0x503f, galaxian_attributes_w, galaxian_attributesram ),
		new MemoryWriteAddress( 0x5040, 0x505f, MWA_RAM, spriteram,spriteram_size ),
		new MemoryWriteAddress( 0x5060, 0x507f, MWA_RAM, galaxian_bulletsram,galaxian_bulletsram_size  ),
		new MemoryWriteAddress( 0x6801, 0x6801, interrupt_enable_w ),
		new MemoryWriteAddress( 0x6804, 0x6804, galaxian_stars_w ),
		new MemoryWriteAddress( 0x6802, 0x6802, MWA_NOP ),
		new MemoryWriteAddress( 0x6806, 0x6807, MWA_NOP ),
		new MemoryWriteAddress( 0x8200, 0x8200, sound_command_w ),
		new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};



	static MemoryReadAddress sound_readmem[] =
	{
		new MemoryReadAddress( 0x8000, 0x83ff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x17ff, MRA_ROM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress sound_writemem[] =
	{
		new MemoryWriteAddress( 0x8000, 0x83ff, MWA_RAM ),
		new MemoryWriteAddress( 0x0000, 0x17ff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};



        static MemoryReadAddress froggers_sound_readmem[] =
        {
                new MemoryReadAddress( 0x4000, 0x43ff, MRA_RAM ),
                new MemoryReadAddress( 0x0000, 0x17ff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress froggers_sound_writemem[] =
        {
               new MemoryWriteAddress( 0x4000, 0x43ff, MWA_RAM ),
                new MemoryWriteAddress( 0x0000, 0x17ff, MWA_ROM ),
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

        static IOReadPort froggers_sound_readport[] =
        {
                new IOReadPort( 0x40, 0x40, AY8910_read_port_0_r  ),
                new IOReadPort( -1  )	/* end of table */
        };

        static IOWritePort froggers_sound_writeport[] =
        {
                new IOWritePort( 0x80, 0x80, AY8910_control_port_0_w ),
                new IOWritePort( 0x40, 0x40, AY8910_write_port_0_w ),
                new IOWritePort( -1 )	/* end of table */
        };

	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { OSD_KEY_UP, OSD_KEY_ALT, OSD_KEY_5, OSD_KEY_CONTROL,
						OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_4, OSD_KEY_3 }
		),
		new InputPort(	/* IN1 */
			0xfc,
			new int[] { 0, 0, OSD_KEY_ALT, OSD_KEY_CONTROL,
						OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_2, OSD_KEY_1 }
		),
		new InputPort(	/* IN2 */
			0xf1,
			new int[] { OSD_KEY_DOWN, 0, 0, 0, OSD_KEY_UP, 0, OSD_KEY_DOWN, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};

        static  TrakPort trak_ports[] =
        {
                new TrakPort( -1 )
        };


        static KEYSet keys[] =
        {
                new KEYSet( 2, 4, "PL1 MOVE UP" ),
                new KEYSet( 0, 5, "PL1 MOVE LEFT"  ),
                new KEYSet( 0, 4, "PL1 MOVE RIGHT" ),
                new KEYSet( 2, 6, "PL1 MOVE DOWN" ),
                new KEYSet( 0, 3, "PL1 FIRE FRONT" ),
                new KEYSet( 0, 1, "PL1 FIRE DOWN" ),
                new KEYSet( 0, 0, "PL2 MOVE UP" ),
                new KEYSet( 1, 5, "PL2 MOVE LEFT"  ),
                new KEYSet( 1, 4, "PL2 MOVE RIGHT" ),
                new KEYSet( 2, 0, "PL2 MOVE DOWN" ),
                new KEYSet( 1, 3, "PL2 FIRE FRONT" ),
                new KEYSet( 1, 2, "PL2 FIRE DOWN" ),
                new KEYSet( -1 )
        };

	static DSW scramble_dsw[] =
	{
		new DSW( 1, 0x03, "LIVES", new String[] { "3", "4", "5", "256" } ),
                new DSW( 2, 0x06, "COINAGE", new String[]{ "A 1/1 B 2/1 C 1/1", "A 1/2 B 1/1 C 1/2", "A 1/3 B 3/1 C 1/3", "A 1/4 B 4/1 C 1/4" } ),
		new DSW( -1 )
	};
	static DSW theend_dsw[] =
	{
		new DSW( 1, 0x03, "LIVES", new String[] { "3", "4", "5", "256" } ),
                new DSW( 2, 0x06, "COINAGE", new String[]{ "1 Coin/1 Credit", "2 Coins/1 Credit", "3 Coins/1 Credit", "1 Coin/2 Credits" } ),
		new DSW( -1 )
	};

	static DSW atlantis_dsw[] =
	{
		new DSW( 1, 0x02, "LIVES", new String[] { "5", "3" }, 1 ),
		new DSW( 1, 0x01, "SW1", new String[] { "OFF", "ON" } ),
		new DSW( 2, 0x02, "SW3", new String[] { "OFF", "ON" } ),
		new DSW( 2, 0x04, "SW4", new String[] { "OFF", "ON" } ),
		new DSW( 2, 0x08, "SW5", new String[] { "OFF", "ON" } ),
                new DSW( 2, 0x0e, "COINAGE", new String[]{ "0", "1", "2", "3", "4", "5", "6", "7" } ),
		new DSW( -1 )
	};


        static DSW froggers_dsw[] =
        {
                new DSW( 1, 0x03, "LIVES", new String[]{ "3", "4", "5", "256" } ),
                new DSW( 2, 0x06, "COINAGE", new String[]{ "A 1/1 B 1/1 C 1/1", "A 2/1 B 2/1 C 2/1", "A 2/1 B 1/3 C 2/1", "A 1/1 B 1/6 C 1/1" } ),
                new DSW( -1 )
        };
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

        static GfxLayout bulletlayout = new GfxLayout
	(
                /* there is no gfx ROM for this one, it is generated by the hardware */
                7,1,	/* it's just 1 pixel, but we use 7*1 to position it correctly */
                1,	/* just one */
                1,	/* 1 bit per pixel */
                 new int[]{ 0 },
                 new int[]{ 3, 0, 0, 0, 0, 0, 0 },	/* I "know" that this bit is 1 */
                 new int[]{ 0 },	/* I "know" that this bit is 1 */
                0	/* no use */
        );
        static GfxLayout theend_bulletlayout = new GfxLayout
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
		new GfxDecodeInfo( -1 ) /* end of array */
	};
        static GfxDecodeInfo scramble_gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, charlayout,     0, 8 ),
		new GfxDecodeInfo( 1, 0x0000, spritelayout,   0, 8 ),
                new GfxDecodeInfo( 1, 0x0000, bulletlayout, 8*4, 1 ),	/* 1 color code instead of 2, so all */
                                                                                                /* shots will be yellow */
                new GfxDecodeInfo( -1 )/* end of array */
        };

        static GfxDecodeInfo theend_gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, charlayout,     0, 8 ),
		new GfxDecodeInfo( 1, 0x0000, spritelayout,   0, 8 ),
                new GfxDecodeInfo( 1, 0x0000, theend_bulletlayout, 8*4, 2 ),
                new GfxDecodeInfo( -1 )/* end of array */
        };


	static char scramble_color_prom[] =
	{
		/* palette */
		0x00,0x17,0xC7,0xF6,0x00,0x17,0xC0,0x3F,0x00,0x07,0xC0,0x3F,0x00,0xC0,0xC4,0x07,
		0x00,0xC7,0x31,0x17,0x00,0x31,0xC7,0x3F,0x00,0xF6,0x07,0xF0,0x00,0x3F,0x07,0xC4
	};



	static char froggers_color_prom[] =
	{
		/* palette */
		0x00,0xF6,0x79,0x4F,0x00,0xC0,0x3F,0x17,0x00,0x87,0xF8,0x7F,0x00,0xC1,0x7F,0x38,
		0x00,0x7F,0xCF,0xF9,0x00,0x57,0xB7,0xC3,0x00,0xFF,0x7F,0x87,0x00,0x79,0x4F,0xFF
	};



	public static MachineDriver scramble_machine_driver = new MachineDriver
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
                scramble_gfxdecodeinfo,
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

	public static MachineDriver theend_machine_driver = new MachineDriver
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
                theend_gfxdecodeinfo,
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

	public static MachineDriver froggers_machine_driver  = new MachineDriver
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
				2000000,	/* 2 Mhz?????? */
				2,	/* memory region #2 */
				froggers_sound_readmem,froggers_sound_writemem,froggers_sound_readport,froggers_sound_writeport,
				frogger_sh_interrupt,10
			)
		},
		60,
		null,

		/* video hardware */
                32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
                scramble_gfxdecodeinfo,
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
		frogger_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);



	/***************************************************************************

	  Game driver(s)

	***************************************************************************/
        static RomLoadPtr scramble_rom= new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD("2d.k", 0x0000, 0x0800, 0xea35ccaa );
		ROM_LOAD("2e.k", 0x0800, 0x0800, 0xe7bba1b3 );
		ROM_LOAD("2f.k", 0x1000, 0x0800, 0x12d7fc3e );
		ROM_LOAD("2h.k", 0x1800, 0x0800, 0xb59360eb );
		ROM_LOAD("2j.k", 0x2000, 0x0800, 0x4919a91c );
		ROM_LOAD("2l.k", 0x2800, 0x0800, 0x26a4547b );
		ROM_LOAD("2m.k", 0x3000, 0x0800, 0x0bb49470 );
		ROM_LOAD("2p.k", 0x3800, 0x0800, 0x6a5740e5 );

		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD("5f.k", 0x0000, 0x0800, 0x4708845b );
		ROM_LOAD("5h.k", 0x0800, 0x0800, 0x11fd2887 );

		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		ROM_LOAD("5c", 0x0000, 0x0800, 0xbcd297f0 );
		ROM_LOAD("5d", 0x0800, 0x0800, 0xde7912da );
		ROM_LOAD("5e", 0x1000, 0x0800, 0xba2fa933 );
                
                ROM_END();
        }};
        static RomLoadPtr atlantis_rom= new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "2c",           0x0000, 0x0800, 0x0e485b9a );
                ROM_LOAD( "2e",           0x0800, 0x0800, 0xc1640513 );
                ROM_LOAD( "2f",           0x1000, 0x0800, 0xeec265ee );
                ROM_LOAD( "2h",           0x1800, 0x0800, 0xa5d2e442 );
                ROM_LOAD( "2j",           0x2000, 0x0800, 0x45f7cf34 );
                ROM_LOAD( "2l",           0x2800, 0x0800, 0xf335b96b );

		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "5f",           0x0000, 0x0800, 0x57f9c6b9 );
                ROM_LOAD( "5h",           0x0800, 0x0800, 0xe989f325 );

		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		ROM_LOAD( "5c",           0x0000, 0x0800, 0xbcd297f0 );
                ROM_LOAD( "5d",           0x0800, 0x0800, 0xde7912da );
                ROM_LOAD( "5e",           0x1000, 0x0800, 0xba2fa933 );
                ROM_END();
        }};
        static RomLoadPtr theends_rom= new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD("ic13",         0x0000, 0x0800, 0x90e5ab14 );
		ROM_LOAD("ic14",         0x0800, 0x0800, 0x950f0a07 );
		ROM_LOAD("ic15",         0x1000, 0x0800, 0x6786bcf5 );
		ROM_LOAD("ic16",         0x1800, 0x0800, 0x380a0017 );
		ROM_LOAD("ic17",         0x2000, 0x0800, 0xaf067b7f );
		ROM_LOAD("ic18",         0x2800, 0x0800, 0xa0411b93 );


		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD("ic30",         0x0000, 0x0800, 0x527fd384 );
		ROM_LOAD("ic31",         0x0800, 0x0800, 0xaf6d09b6 );

		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		ROM_LOAD("ic56",         0x0000, 0x0800, 0x3b2c2f70 );
		ROM_LOAD("ic55",         0x0800, 0x0800, 0xe0429e50 );
                ROM_END();
        }};
        static RomLoadPtr froggers_rom= new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD("vid_d2.bin",   0x0000, 0x0800, 0xc103066e );
		ROM_LOAD("vid_e2.bin",   0x0800, 0x0800, 0xf08bc094 );
		ROM_LOAD("vid_f2.bin",   0x1000, 0x0800, 0x637a2ff8 );
		ROM_LOAD("vid_h2.bin",   0x1800, 0x0800, 0x04c027a5 );
		ROM_LOAD("vid_j2.bin",   0x2000, 0x0800, 0xfbdfbe74 );
		ROM_LOAD("vid_l2.bin",   0x2800, 0x0800, 0x8a4389e1 );

		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD("epr-1036.1k",  0x0000, 0x0800, 0x658745f8 );
		ROM_LOAD("frogger.607",  0x0800, 0x0800, 0x05f7d883 );

		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		ROM_LOAD("frogger.608",  0x0000, 0x0800, 0xe8ab0256 );
		ROM_LOAD("frogger.609",  0x0800, 0x0800, 0x7380a48f );
		ROM_LOAD("frogger.610",  0x1000, 0x0800, 0x31d7eb27 );
                ROM_END();
        }};

	static DecodePtr froggers_decode = new DecodePtr()
        {
            public void handler()
            {
                int A;

	/* the first ROM of the second CPU has data lines D0 and D1 swapped. Decode it. */
                char[] RAM = Machine.memory_region[Machine.drv.cpu[1].memory_region];
                for (A = 0;A < 0x0800;A++)
                    RAM[A] = (char)((RAM[A] & 0xfc) | ((RAM[A] & 1) << 1) | ((RAM[A] & 2) >> 1));
                }
        };
        static HiscoreLoadPtr scramble_hiload = new HiscoreLoadPtr() { public int handler(String name)
             {
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];

                /* check if the hi score table has already been initialized */
                if ((memcmp(RAM,0x4200,new char[]{0x00,0x00,0x01},3) == 0) &&
                        (memcmp(RAM,0x421B,new char[] {0x00,0x00,0x01},3) == 0))
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x4200,1,0x1E,f);
                                /* copy high score */
                                memcpy(RAM,0x40A8,RAM,0x4200,3);
                                fclose(f);
                        }

                        return 1;
                }
                else return 0;	/* we can't load the hi scores yet */
        }};


        static HiscoreSavePtr scramble_hisave = new HiscoreSavePtr() { public void handler(String name){
                FILE f;

                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];


                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x4200,1,0x1E,f);
                        fclose(f);
                }

        }};


        static HiscoreLoadPtr atlantis_hiload = new HiscoreLoadPtr() { public int handler(String name)
             {
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];

                /* check if the hi score table has already been initialized */
                if (memcmp(RAM,0x403D,new char[]{0x00,0x00,0x00},3) == 0)
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x403D,1,4*11,f);
                                fclose(f);
                        }

                        return 1;
                }
                else return 0;	/* we can't load the hi scores yet */
        }};


        static HiscoreSavePtr atlantis_hisave = new HiscoreSavePtr() { public void handler(String name){
                FILE f;

                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];


                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x403D,1,4*11,f);
                        fclose(f);
                }

        }};

        static HiscoreLoadPtr theend_hiload = new HiscoreLoadPtr() { public int handler(String name)
             {
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];

                /* check if the hi score table has already been initialized */
                if (memcmp(RAM,0x43C0,new char[]{0x00,0x00,0x00},3) == 0)
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                /* This seems to have more than 5 scores in memory. */
                                /* If this DISPLAYS more than 5 scores, change 3*5 to 3*10 or */
                                /* however many it should be. */
                                fread(RAM,0x43C0,1,3*5,f);
                                /* copy high score */
                                memcpy(RAM,0x40A8,RAM,0x43C0,3);
                                fclose(f);
                        }

                        return 1;
                }
                else return 0;	/* we can't load the hi scores yet */
        }};


        static HiscoreSavePtr theend_hisave = new HiscoreSavePtr() { public void handler(String name){
                FILE f;

                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];


                if ((f = fopen(name,"wb")) != null)
                {
                        /* This seems to have more than 5 scores in memory. */
                        /* If this DISPLAYS more than 5 scores, change 3*5 to 3*10 or */
                        /* however many it should be. */
                        fwrite(RAM,0x43C0,1,3*5,f);
                        fclose(f);
                }

        }};


        static HiscoreLoadPtr froggers_hiload = new HiscoreLoadPtr() { public int handler(String name)
             {
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];


                /* check if the hi score table has already been initialized */
                if (memcmp(RAM,0x43f1,new char[]{0x63,0x04},2) == 0 &&
                                memcmp(RAM,0x43f8,new char[]{0x27,0x01},2) == 0)
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x43f1,1,2*5,f);
                                RAM[0x43ef] = RAM[0x43f1];
                                RAM[0x43f0] = RAM[0x43f2];
                                fclose(f);
                        }

                        return 1;
                }
                else return 0;	/* we can't load the hi scores yet */
        }};


        static HiscoreSavePtr froggers_hisave = new HiscoreSavePtr() { public void handler(String name){
                FILE f;
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];


                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x43f1,1,2*5,f);
                        fclose(f);
                }
        }};

	public static GameDriver scramble_driver = new GameDriver
	(
                "Scramble",
		"scramble",
                "NICOLA SALMORIA",
		scramble_machine_driver,

		scramble_rom,
		null, null,
		null,

		input_ports,null, trak_ports, scramble_dsw, keys,

		scramble_color_prom, null, null,
		ORIENTATION_ROTATE_90,

                scramble_hiload, scramble_hisave
	);

	public static GameDriver atlantis_driver = new GameDriver
	(
                "Battle of Atlantis",
		"atlantis",
                "NICOLA SALMORIA",
		scramble_machine_driver,

		atlantis_rom,
		null, null,
		null,

		input_ports,null, trak_ports, atlantis_dsw, keys,

		scramble_color_prom, null, null,
		ORIENTATION_ROTATE_90,

                atlantis_hiload, atlantis_hisave
	);

	public static GameDriver theends_driver = new GameDriver
	(
                "The End",
		"theends",
                "NICOLA SALMORIA\nVILLE LAITINEN",
		theend_machine_driver,

		theends_rom,
		null, null,
		null,

		input_ports,null, trak_ports, theend_dsw, keys,

		scramble_color_prom, null, null,
		ORIENTATION_ROTATE_90,

                theend_hiload, theend_hisave
	);

	public static GameDriver froggers_driver = new GameDriver
	(
                "Frog",
		"froggers",
                "NICOLA SALMORIA",
		froggers_machine_driver,

		froggers_rom,
		froggers_decode, null,
		null,

		input_ports,null, trak_ports, scramble_dsw, keys,

		froggers_color_prom, null, null,
		ORIENTATION_ROTATE_90,

                froggers_hiload, froggers_hisave
	);
}
