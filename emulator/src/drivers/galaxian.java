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
 *  rom are from v0.36 romset
 */


package drivers;
import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static sndhrdw.mooncrst.*;
import static vidhrdw.generic.*;
import static vidhrdw.galaxian.*;
import static mame.memoryH.*;

public class galaxian
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x5000, 0x5fff, MRA_RAM ),	/* video RAM, screen attributes, sprites, bullets */
		new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),	/* not all games use all the space */
		new MemoryReadAddress( 0x6000, 0x6000, input_port_0_r ),	/* IN0 */
		new MemoryReadAddress( 0x6800, 0x6800, input_port_1_r ),	/* IN1 */
		new MemoryReadAddress( 0x7000, 0x7000, input_port_2_r ),	/* DSW */
		new MemoryReadAddress( 0x7800, 0x7800, MRA_NOP ),
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress galaxian_writemem[] =
	{
		
                 new MemoryWriteAddress( 0x5000, 0x53ff, videoram_w, videoram, videoram_size ),
                 new MemoryWriteAddress( 0x5800, 0x583f, galaxian_attributes_w, galaxian_attributesram ),
                 new MemoryWriteAddress( 0x5840, 0x585f, MWA_RAM, spriteram, spriteram_size ),
                 new MemoryWriteAddress( 0x5860, 0x5880, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
                 new MemoryWriteAddress( 0x7001, 0x7001, interrupt_enable_w ),
                 new MemoryWriteAddress( 0x7800, 0x7800, mooncrst_sound_freq_w ),
                 new MemoryWriteAddress( 0x6800, 0x6800, mooncrst_background_w ),
                 new MemoryWriteAddress( 0x6803, 0x6803, mooncrst_noise_w ),
                 new MemoryWriteAddress( 0x6805, 0x6805, mooncrst_shoot_w ),
                 new MemoryWriteAddress( 0x6806, 0x6807, mooncrst_sound_freq_sel_w ),
                 new MemoryWriteAddress( 0x6004, 0x6007, mooncrst_lfo_freq_w ),
                 new MemoryWriteAddress( 0x7004, 0x7004, galaxian_stars_w ),
                 new MemoryWriteAddress( 0x0000, 0x27ff, MWA_ROM ),
		 new MemoryWriteAddress( -1 )	/* end of table */
	};
	static MemoryWriteAddress pisces_writemem[] =
	{
                new MemoryWriteAddress( 0x5000, 0x53ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x5800, 0x583f, galaxian_attributes_w, galaxian_attributesram ),
                new MemoryWriteAddress( 0x5840, 0x585f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x5860, 0x5880, MWA_RAM, galaxian_bulletsram, galaxian_bulletsram_size ),
                new MemoryWriteAddress( 0x7001, 0x7001, interrupt_enable_w ),
                new MemoryWriteAddress( 0x7800, 0x7800, mooncrst_sound_freq_w ),
                new MemoryWriteAddress( 0x6800, 0x6800, mooncrst_background_w ),
                new MemoryWriteAddress( 0x6803, 0x6803, mooncrst_noise_w ),
                new MemoryWriteAddress( 0x6805, 0x6805, mooncrst_shoot_w ),
                new MemoryWriteAddress( 0x6806, 0x6807, mooncrst_sound_freq_sel_w ),
                new MemoryWriteAddress( 0x6004, 0x6007, mooncrst_lfo_freq_w ),
                new MemoryWriteAddress( 0x6002, 0x6002, pisces_gfxbank_w ),
                new MemoryWriteAddress( 0x7004, 0x7004, galaxian_stars_w ),
                new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),	/* not all games use all the space */
		new MemoryWriteAddress( -1 )	/* end of table */
	};


		
	static InputPort galaxian_input_ports[] =
	{
		new InputPort(	/* IN0 */
			0x00,
			new int[] { 0, OSD_KEY_3, OSD_KEY_LEFT, OSD_KEY_RIGHT,
				OSD_KEY_CONTROL, 0, OSD_KEY_F2, 0 }
		),
		new InputPort(	/* IN1 */
			0x00,
			new int[] { OSD_KEY_1, OSD_KEY_2, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};

        static TrakPort trak_ports[] =
        {
                new TrakPort( -1 )
        };


        static KEYSet keys[] =
        {
                new KEYSet( 0, 2, "MOVE LEFT"  ),
                new KEYSet( 0, 3, "MOVE RIGHT" ),
                new KEYSet( 0, 4, "FIRE"       ),
                new KEYSet( -1 )
        };

		
	static InputPort warofbug_input_ports[] =
	{
		new InputPort(	/* IN0 */
			0x00,
			new int[] { OSD_KEY_3, 0, OSD_KEY_LEFT, OSD_KEY_RIGHT,
				OSD_KEY_CONTROL, 0, OSD_KEY_DOWN, OSD_KEY_UP }
		),
		new InputPort(	/* IN1 */
			0x00,
			new int[] { OSD_KEY_1, OSD_KEY_2, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW */
			0x02,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};

        static KEYSet wbug_keys[] =
        {
                 new KEYSet( 0, 6, "MOVE UP" ),
                 new KEYSet( 0, 2, "MOVE LEFT"  ),
                 new KEYSet( 0, 3, "MOVE RIGHT" ),
                 new KEYSet( 0, 7, "MOVE DOWN" ),
                 new KEYSet( 0, 4, "FIRE"      ),
                 new KEYSet( -1 )
        };
			
	static DSW galaxian_dsw[] =
	{
		new DSW( 2, 0x04, "LIVES", new String[] { "2", "3" } ),
		new DSW( 2, 0x03, "BONUS", new String[] { "7000", "10000", "12000", "20000" } ),
		new DSW( -1 )
	};



	static DSW galboot_dsw[] =
	{
		new DSW( 2, 0x04, "LIVES", new String[] { "3", "5" } ),
		new DSW( 2, 0x03, "BONUS", new String[] { "7000", "10000", "12000", "20000" } ),
		new DSW( -1 )
	};


		
	static DSW pisces_dsw[] =
	{
		new DSW( 1, 0x40, "LIVES", new String[] { "3", "4" } ),
		new DSW( 1, 0x80, "SW2", new String[] { "OFF", "ON" } ),
		new DSW( 2, 0x01, "SW3", new String[] { "OFF", "ON" } ),
		new DSW( 2, 0x04, "SW5", new String[] { "OFF", "ON" } ),
		new DSW( 2, 0x08, "SW6", new String[] { "OFF", "ON" } ),
		new DSW( -1 )
	};


		
	static DSW japirem_dsw[] =
	{
		new DSW( 2, 0x04, "LIVES", new String[] { "3", "5" } ),
		new DSW( 2, 0x03, "BONUS", new String[] { "NONE", "4000", "5000", "7000" } ),
		new DSW( 2, 0x08, "SW6", new String[] { "OFF", "ON" } ),
		new DSW( -1 )
	};


		
	static DSW warofbug_dsw[] =
	{
		new DSW( 2, 0x03, "LIVES", new String[] { "1", "2", "3", "4" } ),
		new DSW( 2, 0x04, "SW5", new String[] { "OFF", "ON" } ),
		new DSW( 2, 0x08, "SW6", new String[] { "OFF", "ON" } ),
		new DSW( -1 )
	};


		
	static GfxLayout galaxian_charlayout = new GfxLayout
	(
		    8,8,	/* 8*8 characters */
                    256,	/* 256 characters */
                    2,	/* 2 bits per pixel */
                    new int[]{ 0, 256*8*8 },	/* the two bitplanes are separated */
                    new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },
                    new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                    8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout galaxian_spritelayout = new GfxLayout
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
	static GfxLayout pisces_charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
                512,	/* 512 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 512*8*8 },	/* the two bitplanes are separated */
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout pisces_spritelayout = new GfxLayout
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

		
	static GfxDecodeInfo galaxian_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, galaxian_charlayout,    0,  8 ),
		new GfxDecodeInfo( 1, 0x0000, galaxian_spritelayout,  0,  8 ),
                new GfxDecodeInfo( 1, 0x0000, bulletlayout, 8*4, 2 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	static GfxDecodeInfo pisces_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, pisces_charlayout,    0,  8 ),
		new GfxDecodeInfo( 1, 0x0000, pisces_spritelayout,  0,  8 ),
                new GfxDecodeInfo( 1, 0x0000, bulletlayout, 8*4, 2 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};


		
	static char galaxian_color_prom[] =
	{
		/* palette */
		0x00,0x00,0x00,0xF6,0x00,0x16,0xC0,0x3F,0x00,0xD8,0x07,0x3F,0x00,0xC0,0xC4,0x07,
		0x00,0xC0,0xA0,0x0C,0x00,0x00,0x00,0x07,0x00,0xF6,0x07,0xF0,0x00,0x76,0x07,0xC6
	};


		
	static char japirem_color_prom[] =
	{
		/* palette */
		0x00,0x7A,0x36,0x07,0x00,0xF0,0x38,0x1F,0x00,0xC7,0xF0,0x3F,0x00,0xDB,0xC6,0x38,
		0x00,0x36,0x07,0xF0,0x00,0x33,0x3F,0xDB,0x00,0x3F,0x57,0xC6,0x00,0xC6,0x3F,0xFF
	};



	static char uniwars_color_prom[] =
	{
		/* palette */
		0x00,0xe8,0x17,0x3f,0x00,0x2f,0x87,0x20,0x00,0xff,0x3f,0x38,0x00,0x83,0x3f,0x06,
		0x00,0xdc,0x1f,0xd0,0x00,0xef,0x20,0x96,0x00,0x3f,0x17,0xf0,0x00,0x3f,0x17,0x14
	};

        static char samples[] =
        {
           0x88, 0x88, 0x88, 0x88, 0xaa, 0xaa, 0xaa, 0xaa,
           0xcc, 0xcc, 0xcc, 0xcc, 0xee, 0xee, 0xee, 0xee,
           0x11, 0x11, 0x11, 0x11, 0x22, 0x22, 0x22, 0x22,
           0x44, 0x44, 0x44, 0x44, 0x66, 0x66, 0x66, 0x66,
           0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44,
           0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44,
           0xcc, 0xcc, 0xcc, 0xcc, 0xcc, 0xcc, 0xcc, 0xcc,
           0xcc, 0xcc, 0xcc, 0xcc, 0xcc, 0xcc, 0xcc, 0xcc
        };
		
	public static MachineDriver galaxian_machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				readmem, galaxian_writemem, null, null,
				galaxian_vh_interrupt,1
			)
		},
		60,
		null,
			
		/* video hardware */
		/* video hardware */
                32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
                galaxian_gfxdecodeinfo,
                32+64,8*4+2*2,	/* 32 for the characters, 64 for the stars */
                galaxian_vh_convert_color_prom,

                VIDEO_TYPE_RASTER,
                null,
                galaxian_vh_start,
                generic_vh_stop,
                galaxian_vh_screenrefresh,

		/* sound hardware */
		samples,
		mooncrst_sh_init,
		mooncrst_sh_start,
		mooncrst_sh_stop,
		mooncrst_sh_update
	);


		
	public static MachineDriver pisces_machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				readmem, pisces_writemem, null, null,
				galaxian_vh_interrupt,1
			)
		},
		60,
		null,
	
		/* video hardware */
		/* video hardware */
                32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
                pisces_gfxdecodeinfo,
                32+64,8*4+2*2,	/* 32 for the characters, 64 for the stars */
                galaxian_vh_convert_color_prom,

                VIDEO_TYPE_RASTER,
                null,
                galaxian_vh_start,
                generic_vh_stop,
                galaxian_vh_screenrefresh,

		/* sound hardware */
		samples,
		mooncrst_sh_init,
		mooncrst_sh_start,
		mooncrst_sh_stop,
		mooncrst_sh_update
	);

        static String mooncrst_sample_names[] =
        {
                "shot.sam",
                "death.sam",
                "backgrnd.sam",
                null	/* end of array */
        };

	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr galaxian_rom= new RomLoadPtr(){ public void handler() {
		ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "galmidw.u",    0x0000, 0x0800, 0x745e2d61 );  /*	\ 7f previously */
                ROM_LOAD( "galmidw.v",    0x0800, 0x0800, 0x9c999a40 );	/*  /				*/
                ROM_LOAD( "galmidw.w",    0x1000, 0x0800, 0xb5894925 );  /*  \ 7j previously */
                ROM_LOAD( "galmidw.y",    0x1800, 0x0800, 0x6b3ca10b );  /*  /               */
                ROM_LOAD( "7l",           0x2000, 0x0800, 0x1b933207 );

		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "1h", 0x0000, 0x0800,0 );
		ROM_LOAD( "1k", 0x0800, 0x0800,0 );
	ROM_END(); }};

	static RomLoadPtr galmidw_rom= new RomLoadPtr(){ public void handler() {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "galmidw.u",    0x0000, 0x0800, 0x745e2d61 );
                ROM_LOAD( "galmidw.v",    0x0800, 0x0800, 0x9c999a40 );
                ROM_LOAD( "galmidw.w",    0x1000, 0x0800, 0xb5894925 );
                ROM_LOAD( "galmidw.y",    0x1800, 0x0800, 0x6b3ca10b );
                ROM_LOAD( "galmidw.z",    0x2000, 0x0800, 0xcb24f797 );
	
		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "galmidw.1j",   0x0000, 0x0800, 0x84decf98 );
                ROM_LOAD( "galmidw.1k",   0x0800, 0x0800, 0xc31ada9e );
                             
	ROM_END(); }};
	
	static RomLoadPtr galturbo_rom= new RomLoadPtr(){ public void handler() {  
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "superg.u",     0x0000, 0x0800, 0xe8f3aa67 );
                ROM_LOAD( "galx.v",       0x0800, 0x0800, 0xbc16064e );
                ROM_LOAD( "superg.w",     0x1000, 0x0800, 0xddeabdae );
                ROM_LOAD( "galturbo.y",   0x1800, 0x0800, 0xa44f450f );
                ROM_LOAD( "galturbo.z",   0x2000, 0x0800, 0x3247f3d4 );
	
		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "galturbo.1h",  0x0000, 0x0800, 0xa713fd1a );
                ROM_LOAD( "galturbo.1k",  0x0800, 0x0800, 0x28511790 );             
	ROM_END(); }};
	
	static RomLoadPtr galapx_rom= new RomLoadPtr(){ public void handler() {
		ROM_REGION(0x10000);	/* 64k for code */
		  ROM_LOAD( "galx.u",       0x0000, 0x0800, 0x79e4007d );
                  ROM_LOAD( "galx.v",       0x0800, 0x0800, 0xbc16064e );
                  ROM_LOAD( "galx.w",       0x1000, 0x0800, 0x72d2d3ee );
                  ROM_LOAD( "galx.y",       0x1800, 0x0800, 0xafe397f3 );
	          ROM_LOAD( "galx.z",       0x2000, 0x0800, 0x778c0d3c );
	
		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		 ROM_LOAD( "galx.1h",      0x0000, 0x0800, 0xe8810654 );
                 ROM_LOAD( "galx.1k",      0x0800, 0x0800, 0xcbe84a76 );
                                
	ROM_END(); }};
	
	static RomLoadPtr galap1_rom= new RomLoadPtr(){ public void handler() {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "superg.u",     0x0000, 0x0800, 0xe8f3aa67 );
                ROM_LOAD( "superg.v",     0x0800, 0x0800, 0xf58283e3 );
                ROM_LOAD( "cp3",          0x1000, 0x0800, 0x4c7031c0 );
                ROM_LOAD( "galx_1_4.rom", 0x1800, 0x0800, 0xe71e1d9e );
                ROM_LOAD( "galx_1_5.rom", 0x2000, 0x0800, 0x6e65a3b2 );
	
		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "galmidw.1j",   0x0000, 0x0800, 0x84decf98 );
	        ROM_LOAD( "galmidw.1k",   0x0800, 0x0800, 0xc31ada9e );                             
	ROM_END(); }};
	
	static RomLoadPtr galap4_rom= new RomLoadPtr(){ public void handler() {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "galnamco.u",   0x0000, 0x0800, 0xacfde501 );
                ROM_LOAD( "galnamco.v",   0x0800, 0x0800, 0x65cf3c77 );
                ROM_LOAD( "galnamco.w",   0x1000, 0x0800, 0x9eef9ae6 );
                ROM_LOAD( "galnamco.y",   0x1800, 0x0800, 0x56a5ddd1 );
                ROM_LOAD( "galnamco.z",   0x2000, 0x0800, 0xf4bc7262 );
	
		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "galx_4c1.rom", 0x0000, 0x0800, 0xd5e88ab4 );
                ROM_LOAD( "galx_4c2.rom", 0x0800, 0x0800, 0xa57b83e4 );                                         
	ROM_END(); }};
        
	static RomLoadPtr superg_rom= new RomLoadPtr(){ public void handler() {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "superg.u",     0x0000, 0x0800, 0xe8f3aa67 );
                ROM_LOAD( "superg.v",     0x0800, 0x0800, 0xf58283e3 );
                ROM_LOAD( "superg.w",     0x1000, 0x0800, 0xddeabdae );
                ROM_LOAD( "superg.y",     0x1800, 0x0800, 0x9463f753 );
                ROM_LOAD( "superg.z",     0x2000, 0x0800, 0xe6312e35 );
	
		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "galmidw.1j",   0x0000, 0x0800, 0x84decf98 );
                ROM_LOAD( "galmidw.1k",   0x0800, 0x0800, 0xc31ada9e );                                       
	ROM_END(); }};
                                      
	static RomLoadPtr pisces_rom= new RomLoadPtr(){ public void handler() {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "pisces.a1",    0x0000, 0x0800, 0x856b8e1f );
                ROM_LOAD( "pisces.a2",    0x0800, 0x0800, 0x055f9762 );
                ROM_LOAD( "pisces.b2",    0x1000, 0x0800, 0x5540f2e4 );
                ROM_LOAD( "pisces.c1",    0x1800, 0x0800, 0x44aaf525 );
                ROM_LOAD( "pisces.d1",    0x2000, 0x0800, 0xfade512b );
                ROM_LOAD( "pisces.e2",    0x2800, 0x0800, 0x5ab2822f );
	
		ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "pisces.1j",    0x0000, 0x1000, 0x2dba9e0e );
                ROM_LOAD( "pisces.1k",    0x1000, 0x1000, 0xcdc5aa26 );             
	ROM_END(); }};
	
	/*static RomLoadPtr japirem_rom= new RomLoadPtr(){ public void handler() {
		ROM_REGION(0x10000);	/* 64k for code */
	/*	ROM_LOAD( "f07_1a.bin",  0x0000, 0x0800 );
		ROM_LOAD( "h07_2a.bin",  0x0800, 0x0800 );
		ROM_LOAD( "k07_3a.bin",  0x1000, 0x0800 );
		ROM_LOAD( "m07_4a.bin",  0x1800, 0x0800 );
		ROM_LOAD( "d08p_5a.bin", 0x2000, 0x0800 );
		ROM_LOAD( "e08p_6a.bin", 0x2800, 0x0800 );
		ROM_LOAD( "m08p_7a.bin", 0x3000, 0x0800 );
		ROM_LOAD( "n08p_8a.bin", 0x3800, 0x0800 );
	
		ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
	/*	ROM_LOAD( "h01_1.bin",   0x0000, 0x0800 );
		ROM_LOAD( "h01_2.bin",   0x0800, 0x0800 );
		ROM_LOAD( "k01_1.bin",   0x1000, 0x0800 );
		ROM_LOAD( "k01_2.bin",   0x1800, 0x0800 );
	ROM_END(); }};*/
	
	static RomLoadPtr uniwars_rom= new RomLoadPtr(){ public void handler() {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "f07_1a.bin",   0x0000, 0x0800, 0xd975af10 );
                ROM_LOAD( "h07_2a.bin",   0x0800, 0x0800, 0xb2ed14c3 );
                ROM_LOAD( "k07_3a.bin",   0x1000, 0x0800, 0x945f4160 );
                ROM_LOAD( "m07_4a.bin",   0x1800, 0x0800, 0xddc80bc5 );
                ROM_LOAD( "d08p_5a.bin",  0x2000, 0x0800, 0x62354351 );
                ROM_LOAD( "gg6",          0x2800, 0x0800, 0x270a3f4d );
                ROM_LOAD( "m08p_7a.bin",  0x3000, 0x0800, 0xc9245346 );
                ROM_LOAD( "n08p_8a.bin",  0x3800, 0x0800, 0x797d45c7 );
	
		ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "egg10",        0x0000, 0x0800, 0x012941e0 );
                ROM_LOAD( "h01_2.bin",    0x0800, 0x0800, 0xc26132af );
                ROM_LOAD( "egg9",         0x1000, 0x0800, 0xfc8b58fd );
                ROM_LOAD( "k01_2.bin",    0x1800, 0x0800, 0xdcc2b33b );
                                
	ROM_END(); }};

	static RomLoadPtr warofbug_rom= new RomLoadPtr(){ public void handler() {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "warofbug.u",   0x0000, 0x0800, 0xb8dfb7e3 );
                ROM_LOAD( "warofbug.v",   0x0800, 0x0800, 0xfd8854e0 );
                ROM_LOAD( "warofbug.w",   0x1000, 0x0800, 0x4495aa14 );
                ROM_LOAD( "warofbug.y",   0x1800, 0x0800, 0xc14a541f );
                ROM_LOAD( "warofbug.z",   0x2000, 0x0800, 0xc167fe55 );

		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "warofbug.1k",  0x0000, 0x0800, 0x8100fa85 );
                ROM_LOAD( "warofbug.1j",  0x0800, 0x0800, 0xd1220ae9 );
                              
	ROM_END(); }};



	static HiscoreLoadPtr galaxian_hiload = new HiscoreLoadPtr() { public int handler()
	{
		/* wait for the checkerboard pattern to be on screen */
	/*TOFIX	if (memcmp(RAM, 0x5000, new char[] { 0x30, 0x32 }, 2) == 0)
		{
			FILE f;


			if ((f = fopen(name, "rb")) != null)
			{
				fread(RAM, 0x40a8, 1, 3, f);
				fclose(f);
			}

			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };



	static HiscoreSavePtr galaxian_hisave = new HiscoreSavePtr() { public void handler()
	{
		FILE f;


/*TOFIX		if ((f = fopen(name, "wb")) != null)
		{
			fwrite(RAM, 0x40a8, 1, 3, f);
			fclose(f);
		}*/
	} };
        static HiscoreLoadPtr pisces_hiload = new HiscoreLoadPtr() { public int handler()
	{
                /* wait for the screen to initialize */
      /*TOFIX          if (memcmp(RAM,0x5000,new char[]{0x10,0x10,0x10},3) == 0)
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x4021,1,3,f);
                                fclose(f);
                        }

                        return 1;
                }
                else */return 0;	/* we can't load the hi scores yet */
        }};



        static HiscoreSavePtr pisces_hisave = new HiscoreSavePtr() { public void handler()
	{
                FILE f;


      /*TOFIX          if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x4021,1,3,f);
                        fclose(f);
                }*/
        }};

        static HiscoreLoadPtr warofbug_hiload = new HiscoreLoadPtr() { public int handler()
	{
                /* wait for memory to be set */
   /*TOFIX            if (memcmp(RAM,0x4045,new char[]{0x1F,0x1F},2) == 0)
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x4034,1,3,f);
                                fclose(f);
                        }

                        return 1;
                }
                else */return 0;	/* we can't load the hi scores yet */
        }};



        static HiscoreSavePtr warofbug_hisave = new HiscoreSavePtr() { public void handler()
	{
     /*TOFIX           FILE f;


                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x4034,1,3,f);
                        fclose(f);
                }*/
        }};

	
	public static GameDriver galaxian_driver = new GameDriver
	(
                "Galaxian (Namco)",
		"galaxian",
                "ROBERT ANSCHUETZ\nNICOLA SALMORIA\nANDREW SCOTT",
		galaxian_machine_driver,
	
		galaxian_rom,
		null, null,
		mooncrst_sample_names,
	
		galaxian_input_ports,null, trak_ports, galaxian_dsw, keys,
	
		galaxian_color_prom, null, null,
		ORIENTATION_ROTATE_90,
	
		galaxian_hiload, galaxian_hisave
	);

	public static GameDriver galmidw_driver = new GameDriver
	(
                "Galaxian (Midway)",
		"galmidw",
                "ROBERT ANSCHUETZ\nNICOLA SALMORIA\nANDREW SCOTT",
		galaxian_machine_driver,
	
		galmidw_rom,
		null, null,
		mooncrst_sample_names,
	
		galaxian_input_ports,null, trak_ports, galaxian_dsw, keys,
	
		galaxian_color_prom, null, null,
		ORIENTATION_ROTATE_90,
	
		galaxian_hiload, galaxian_hisave
	);
	
	/*public static GameDriver galnamco_driver = new GameDriver  //duno which game is that...
	(
                "Galaxian (Namco, modified)",
		"galnamco",
                "ROBERT ANSCHUETZ\nNICOLA SALMORIA\nANDREW SCOTT",
		galaxian_machine_driver,
	
		galnamco_rom,
		null, null,
		mooncrst_sample_names,
	
		galaxian_input_ports, trak_ports, galboot_dsw, keys,
	
		galaxian_color_prom, null, null,
		8*13, 8*16,
	
		galaxian_hiload, galaxian_hisave
	);*/
	
	public static GameDriver superg_driver = new GameDriver
	(
                "Super Galaxian",
		"superg",
                "ROBERT ANSCHUETZ\nNICOLA SALMORIA\nANDREW SCOTT",
		galaxian_machine_driver,
	
		superg_rom,
		null, null,
		mooncrst_sample_names,
	
		galaxian_input_ports,null, trak_ports, galboot_dsw, keys,
	
		galaxian_color_prom, null, null,
		ORIENTATION_ROTATE_90,
	
		galaxian_hiload, galaxian_hisave
	);
	
	public static GameDriver galapx_driver = new GameDriver
	(
                "Galaxian Part X",
		"galapx",
                "ROBERT ANSCHUETZ\nNICOLA SALMORIA\nANDREW SCOTT",
		galaxian_machine_driver,
	
		galapx_rom,
		null, null,
		mooncrst_sample_names,
	
		galaxian_input_ports,null, trak_ports, galboot_dsw, keys,
	
		galaxian_color_prom, null, null,
		ORIENTATION_ROTATE_90,
	
		galaxian_hiload, galaxian_hisave
	);
	
	public static GameDriver galap1_driver = new GameDriver
	(
                "Galaxian Part 1",
		"galap1",
                "ROBERT ANSCHUETZ\nNICOLA SALMORIA\nANDREW SCOTT",
		galaxian_machine_driver,
	
		galap1_rom,
		null, null,
		mooncrst_sample_names,
	
		galaxian_input_ports,null, trak_ports, galboot_dsw, keys,
	
		galaxian_color_prom, null, null,
		ORIENTATION_ROTATE_90,
	
		galaxian_hiload, galaxian_hisave
	);
	
	public static GameDriver galap4_driver = new GameDriver
	(
                "Galaxian Part 4",
		"galap4",
                "ROBERT ANSCHUETZ\nNICOLA SALMORIA\nANDREW SCOTT",
		galaxian_machine_driver,
	
		galap4_rom,
		null, null,
		mooncrst_sample_names,
	
		galaxian_input_ports,null, trak_ports, galboot_dsw, keys,
	
		galaxian_color_prom, null, null,
		ORIENTATION_ROTATE_90,
	
		galaxian_hiload, galaxian_hisave
	);
	
	public static GameDriver galturbo_driver = new GameDriver
	(
                "Galaxian Turbo",
		"galturbo",
                "ROBERT ANSCHUETZ\nNICOLA SALMORIA\nANDREW SCOTT",
		galaxian_machine_driver,
	
		galturbo_rom,
		null, null,
		mooncrst_sample_names,
	
		galaxian_input_ports,null, trak_ports, galboot_dsw, keys,
	
		galaxian_color_prom, null, null,
		ORIENTATION_ROTATE_90,
	
		galaxian_hiload, galaxian_hisave
	);
	
	public static GameDriver pisces_driver = new GameDriver
	(
                "Pisces",
		"pisces",
                "ROBERT ANSCHUETZ\nNICOLA SALMORIA\nANDREW SCOTT",
		pisces_machine_driver,
	
		pisces_rom,
		null, null,
		mooncrst_sample_names,
	
		galaxian_input_ports,null, trak_ports, pisces_dsw, keys,
	
		galaxian_color_prom, null, null,
		ORIENTATION_ROTATE_90,

                pisces_hiload, pisces_hisave
	);
	
	/*public static GameDriver japirem_driver = new GameDriver //doesn't exist in v0.36 romset
	(
                "Gingateikoku No Gyakushu",
		"japirem",
                "NICOLA SALMORIA\nLIONEL THEUNISSEN\nROBERT ANSCHUETZ\nANDREW SCOTT",
		pisces_machine_driver,
	
		japirem_rom,
		null, null,
		mooncrst_sample_names,
	
		galaxian_input_ports, trak_ports, japirem_dsw, keys,
	
		japirem_color_prom, null, null,
		8*13, 8*16,
	
		null, null
	);*/
	
	public static GameDriver uniwars_driver = new GameDriver
	(
                "Uniwars",
		"uniwars",
                "NICOLA SALMORIA\nGARY WALTON\nROBERT ANSCHUETZ\nANDREW SCOTT",
		pisces_machine_driver,
	
		uniwars_rom,
		null, null,
		mooncrst_sample_names,
	
		galaxian_input_ports,null, trak_ports, japirem_dsw, keys,
	
		uniwars_color_prom, null, null,
		ORIENTATION_ROTATE_90,
	
		null, null
	);
	
	public static GameDriver warofbug_driver = new GameDriver
	(
                "War of the Bugs",
		"warofbug",
                "ROBERT ANSCHUETZ\nNICOLA SALMORIA\nANDREW SCOTT",
		galaxian_machine_driver,
	
		warofbug_rom,
		null, null,
		mooncrst_sample_names,
	
		warofbug_input_ports,null, trak_ports, warofbug_dsw, wbug_keys,
	
		japirem_color_prom, null, null,
		ORIENTATION_ROTATE_90,

                warofbug_hiload, warofbug_hisave
	);
}

