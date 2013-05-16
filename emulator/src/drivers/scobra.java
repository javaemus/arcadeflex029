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
 *  NOTES: romsets are from v0.36 roms 
 * 
 *  scobra = scobras
 *  scobrak = scobra
 *  rom replaced since this is how they exist in mame 0.36
 * 
 *   hunchy_driver has been moved to dkong.c at v0.35 so we don't support it here
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
public class scobra
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x8000, 0x8bff, MRA_RAM ),	/* RAM and Video RAM */
		new MemoryReadAddress( 0x0000, 0x6fff, MRA_ROM ),
		new MemoryReadAddress( 0xb000, 0xb000, MRA_NOP ),
		new MemoryReadAddress( 0x9800, 0x9800, input_port_0_r ),	/* IN0 */
		new MemoryReadAddress( 0x9801, 0x9801, input_port_1_r ),	/* IN1 */
		new MemoryReadAddress( 0x9802, 0x9802, scramble_IN2_r ),	/* IN2 */
		new MemoryReadAddress( 0x9000, 0x907f, MRA_RAM ),	/* screen attributes, sprites, bullets */
		new MemoryReadAddress( 0xa002, 0xa002, scramble_protection_r ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new MemoryWriteAddress( 0x8800, 0x8bff, videoram_w, videoram,videoram_size ),
		new MemoryWriteAddress( 0x9000, 0x903f, galaxian_attributes_w, galaxian_attributesram ),
		new MemoryWriteAddress( 0x9040, 0x905f, MWA_RAM, spriteram,spriteram_size ),
		new MemoryWriteAddress( 0x9060, 0x907f, MWA_RAM, galaxian_bulletsram,galaxian_bulletsram_size ),
		new MemoryWriteAddress( 0xa801, 0xa801, interrupt_enable_w ),
		new MemoryWriteAddress( 0xa804, 0xa804, galaxian_stars_w ),
		new MemoryWriteAddress( 0xa000, 0xa000, sound_command_w ),
		new MemoryWriteAddress( 0x0000, 0x6fff, MWA_ROM ),
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

		

	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { 0, OSD_KEY_ALT, OSD_KEY_3, OSD_KEY_CONTROL, OSD_KEY_RIGHT, OSD_KEY_LEFT, 0, 0 }
		),
		new InputPort(	/* IN1 */
			0xfd,
			new int[] { 0, 0, 0, 0, 0, 0, OSD_KEY_2, OSD_KEY_1 }
		),
		new InputPort(	/* IN2 */
			0xf3,
			new int[] { 0, 0, 0, 0, OSD_KEY_UP, 0, OSD_KEY_DOWN, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};


        static  TrakPort trak_ports[] =
        {
                new TrakPort( -1 )
        };

        static  KEYSet keys[] =
        {
                new KEYSet( 2, 4, "MOVE UP" ),
                new KEYSet( 0, 5, "MOVE LEFT"  ),
                new KEYSet( 0, 4, "MOVE RIGHT" ),
                new KEYSet( 2, 6, "MOVE DOWN" ),
                new KEYSet( 0, 3, "FIRE1" ),
                new KEYSet( 0, 1, "FIRE2" ),
                new KEYSet( -1 )
        };		
	static DSW dsw[] =
	{
		new DSW( 1, 0x02, "LIVES", new String[] { "3", "5" } ),
		new DSW( 1, 0x01, "ALLOW CONTINUE", new String[] { "NO", "YES" } ),
		new DSW( -1 )
	};

        /*
                JRT: Order is 0.7
                        IN0                                     IN1:                            IN2:
                        0: Start2                       0: (DIP) Free Play      0: DIP?
                        1: Start1                       1: (DIP) Demo Mode      1: DIP?
                        2: Up                           2: Fire U                       2: DIP?
                        3: Down                         3: Fire D                       3: DIP?
                        4: Left                         4: Fire R                       4: DIP?
                        5: Right                        5: Fire L                       5: DIP?
                        6: Coin (2?)            6: Whip                         6: DIP?
                        7: Coin (1?)            7: DIP?                         7: DIP?

        */
        static InputPort LTinput_ports[] =
        {
                new InputPort(	/* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_2, OSD_KEY_1, OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_3, OSD_KEY_4 }
                        
                ),
		new InputPort(	/* IN1 */
                        0xfd,
                        new int[]{ 0, 0, OSD_KEY_E, OSD_KEY_D, OSD_KEY_F, OSD_KEY_S, OSD_KEY_CONTROL, OSD_KEY_1 }
                       
                ),
		new InputPort(	/* IN2 */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                       
                ),
		new InputPort( -1 )	/* end of table */
        };


        static DSW LTdsw[] =
        {
                new DSW( 1, 0x02, "FREE PLAY", new String[]{ "NO", "YES" } ),
                new DSW( 1, 0x01, "PLAYER CAN DIE", new String[]{ "NO", "YES" } ),
        /*   JRT: In Development
                { 1, 0x01, "DEMO MODE", { "ON", "OFF" } },

                { 1, 0x02, "DIP1 1", { "ON", "OFF" } },

                { 2, 0x80, "DIP1 7", { "ON", "OFF" } },
                { 2, 0x01, "DIP2 0", { "ON", "OFF" } },
                { 2, 0x02, "DIP2 1", { "ON", "OFF" } },
                { 2, 0x04, "DIP2 2", { "ON", "OFF" } },
                { 2, 0x08, "DIP2 3", { "ON", "OFF" } },
                { 2, 0x10, "DIP2 4", { "ON", "OFF" } },
                { 2, 0x20, "DIP2 5", { "ON", "OFF" } },
                { 2, 0x40, "DIP2 6", { "ON", "OFF" } },
                { 2, 0x80, "DIP2 7", { "ON", "OFF" } },
        */
                new DSW( -1 )
        };


        static KEYSet LTkeys[] =
        {
                new KEYSet( 0, 2, "MOVE UP" ),
                new KEYSet( 0, 3, "MOVE DOWN" ),
                new KEYSet( 0, 5, "MOVE RIGHT"  ),
                new KEYSet( 0, 4, "MOVE LEFT" ),
                new KEYSet( 1, 2, "FIRE UP" ),
                new KEYSet( 1, 3, "FIRE DOWN" ),
                new KEYSet( 1, 4, "FIRE RIGHT"  ),
                new KEYSet( 1, 5, "FIRE LEFT" ),
                new KEYSet( 1, 6, "WHIP" ),
                new KEYSet( -1 )
        };


        /*
                RESCUE  (Thanx To Chris Hardy)
                JRT: Order is 0.7
                        IN0                                     IN1:                            IN2:
                        0: Bomb                         0: (DIP) Free Play      0: Start1
                        1: ???                          1: (DIP) Demo Mode      1:
                        2: Up                           2: Fire U                       2:
                        3: Down                         3: Fire D                       3:
                        4: Right                        4: Fire R                       4:
                        5: Left                         5: Fire L                       5:
                        6: Coin (2?)            6:                                      6: Start2
                        7: Coin (1?)            7:                                      7:

        */
        static InputPort Rescueinput_ports[] =
        {
                new InputPort(       /* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_CONTROL, OSD_KEY_ALT, OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_3, OSD_KEY_4 }
                        
                ),
		new InputPort(       /* IN1 */
                        0xff,
                        new int[]{ 0, 0, OSD_KEY_E, OSD_KEY_D, OSD_KEY_F, OSD_KEY_S, OSD_KEY_CONTROL, OSD_KEY_1 }
                        
                ),
		new InputPort(       /* IN2 */
                        0xff,
                        new int[]{ OSD_KEY_1, 0, 0, 0, 0, 0, OSD_KEY_2, 0 }
                        
                ),
		new InputPort( -1 )  /* end of table */
        };


        static DSW Rescuedsw[] =
        {
                new DSW( 1, 0x02, "FREE PLAY", new String[]{ "NO", "YES" } ),
                new DSW( 1, 0x01, "PLAYER CAN DIE", new String[]{ "NO", "YES" } ),
                new DSW( -1 )
        };


        static KEYSet Rescuekeys[] =
        {
                 new KEYSet(  0, 2, "MOVE UP" ),
                 new KEYSet(  0, 3, "MOVE DOWN" ),
                 new KEYSet(  0, 5, "MOVE RIGHT"  ),
                 new KEYSet(  0, 4, "MOVE LEFT" ),
                 new KEYSet(  1, 2, "FIRE UP" ),
                 new KEYSet(  1, 3, "FIRE DOWN" ),
                 new KEYSet(  1, 5, "FIRE RIGHT"  ),
                 new KEYSet(  1, 4, "FIRE LEFT" ),
                 new KEYSet(  2, 0, "BOMB" ),
                 new KEYSet(  2, 6, "FIRE2?" ),
                 new KEYSet(  -1 )
        };


        /*
                ANTEATER
                JRT: Order is 0.7
                        IN0                                     IN1:                            IN2:
                        0: Retract                      0: (DIP) Free Play      0: Start1
                        1: Fire(?)1                     1: (DIP) Demo Mode      1:
                        2: Up                           2:                                      2:
                        3: Down                         3:                                      3:
                        4: Right                        4:                                      4:
                        5: Left                         5:                                      5:
                        6: Coin (2?)            6:                                      6: Start2
                        7: Coin (1?)            7:                                      7:

        */
        static InputPort AntEaterinput_ports[] =
        {
                new InputPort(       /* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_CONTROL, OSD_KEY_ALT, OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_3, OSD_KEY_4 }
                        
                ),
		new InputPort(       /* IN1 */
                        0xfd,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                        
                ),
		new InputPort(       /* IN2 */
                        0xff,
                        new int[]{ OSD_KEY_1, 0, 0, 0, 0, 0, OSD_KEY_2, 0 }
                        
                ),
		new InputPort( -1 )  /* end of table */
        };


        static DSW AntEaterdsw[] =
        {
                 new DSW( 1, 0x02, "FREE PLAY", new String[]{ "NO", "YES" } ),
                 new DSW( 1, 0x01, "LIVES", new String[]{ "5", "3" } ),
                 new DSW( -1 )
        };


        static KEYSet AntEaterkeys[] =
        {
                new KEYSet( 0, 2, "MOVE UP" ),
                new KEYSet( 0, 3, "MOVE DOWN" ),
                new KEYSet( 0, 5, "MOVE RIGHT"  ),
                new KEYSet( 0, 4, "MOVE LEFT" ),
                new KEYSet( 0, 0, "RETRACT" ),
                new KEYSet( -1 )
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


		
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,     0, 8 ),
		new GfxDecodeInfo( 1, 0x0000, spritelayout,   0, 8 ),
                new GfxDecodeInfo( 1, 0x0000, bulletlayout, 8*4, 1 ),	/* 1 color code instead of 2, so all */
											/* shots will be yellow */
		new GfxDecodeInfo( -1 ) /* end of array */
	};


		
	static char color_prom[] =
	{
		/* palette */
		0x00,0x17,0xC7,0xF6,0x00,0x17,0xC0,0x3F,0x00,0x07,0xC0,0x3F,0x00,0xC0,0xC4,0x07,
		0x00,0xC7,0x31,0x17,0x00,0x31,0xC7,0x3F,0x00,0xF6,0x07,0xF0,0x00,0x3F,0x07,0xC4
	};

        static char rescue_color_prom[] =
        {
                /* palette */
                0x00,0xA4,0x18,0x5B,0x00,0xB6,0x07,0x36,0x00,0xDB,0xA3,0x9B,0x00,0xDC,0x27,0xAD,
                0x00,0xC0,0x2B,0x5F,0x00,0xAD,0x2F,0x85,0x00,0xE4,0x3F,0x28,0x00,0x9A,0xC0,0x27
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
		32*8, 32*8, new rectangle(  0*8, 32*8-1, 2*8, 30*8-1),
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
	static RomLoadPtr scobra_rom = new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD("2c", 0x0000, 0x1000, 0xa0744b3f );
		ROM_LOAD("2e", 0x1000, 0x1000, 0x8e7245cd );
		ROM_LOAD("2f", 0x2000, 0x1000, 0x47a4e6fb );
		ROM_LOAD("2h", 0x3000, 0x1000, 0x7244f21c );
		ROM_LOAD("2j", 0x4000, 0x1000, 0xe1f8a801 );
		ROM_LOAD("2l", 0x5000, 0x1000, 0xd52affde );
	
		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD("5f", 0x0000, 0x0800, 0x64d113b4 );
		ROM_LOAD("5h", 0x0800, 0x0800, 0xa96316d3 );

		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		ROM_LOAD("5c", 0x0000, 0x0800, 0xd4346959 );
		ROM_LOAD("5d", 0x0800, 0x0800, 0xcc025d95 );
		ROM_LOAD("5e", 0x1000, 0x0800, 0x1628c53f );
                ROM_END();
        }};
        static RomLoadPtr scobras_rom = new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD("scobra2c.bin", 0x0000, 0x1000, 0xe15ade38 );
		ROM_LOAD("scobra2e.bin", 0x1000, 0x1000, 0xa270e44d );
		ROM_LOAD("scobra2f.bin", 0x2000, 0x1000, 0xbdd70346 );
		ROM_LOAD("scobra2h.bin", 0x3000, 0x1000, 0xdca5ec31 );
		ROM_LOAD("scobra2j.bin", 0x4000, 0x1000, 0x0d8f6b6e );
		ROM_LOAD("scobra2l.bin", 0x5000, 0x1000, 0x6f80f3a9 );
	
		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD("5f", 0x0000, 0x0800, 0x64d113b4 );
		ROM_LOAD("5h", 0x0800, 0x0800, 0xa96316d3 );

		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		ROM_LOAD("snd_5c.bin",      0x0000, 0x0800, 0xdeeb0dd3 );
		ROM_LOAD("snd_5d.bin",      0x0800, 0x0800, 0x872c1a74 );
		ROM_LOAD("snd_5e.bin",      0x1000, 0x0800, 0xccd7a110 );
                ROM_END();
        }};
        static RomLoadPtr scobrab_rom = new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "vid_2c.bin",   0x0000, 0x0800, 0xaeddf391 );
                ROM_LOAD( "vid_2e.bin",   0x0800, 0x0800, 0x72b57eb7 );
                ROM_LOAD( "scobra2e.bin", 0x1000, 0x1000, 0xa270e44d );
                ROM_LOAD( "scobra2f.bin", 0x2000, 0x1000, 0xbdd70346 );
                ROM_LOAD( "scobra2h.bin", 0x3000, 0x1000, 0xdca5ec31 );
                ROM_LOAD( "scobra2j.bin", 0x4000, 0x1000, 0x0d8f6b6e );
                ROM_LOAD( "scobra2l.bin", 0x5000, 0x1000, 0x6f80f3a9 );
                
		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD("5f",   0x0000, 0x0800, 0x64d113b4 );
		ROM_LOAD("5h",   0x0800, 0x0800, 0xa96316d3 );

		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		ROM_LOAD("snd_5c.bin",   0x0000, 0x0800, 0xdeeb0dd3 );
		ROM_LOAD("snd_5d.bin",   0x0800, 0x0800, 0x872c1a74 );
		ROM_LOAD("snd_5e.bin",   0x1000, 0x0800, 0xccd7a110 );
                ROM_END();
        }};
        static RomLoadPtr losttomb_rom = new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD("2c",      0x0000, 0x1000, 0xd6176d2c );
		ROM_LOAD("2e",      0x1000, 0x1000, 0xa5f55f4a );
		ROM_LOAD("2f",      0x2000, 0x1000, 0x0169fa3c );
		ROM_LOAD("2h-easy", 0x3000, 0x1000, 0x054481b6 );
		ROM_LOAD("2j",      0x4000, 0x1000, 0x249ee040 );
		ROM_LOAD("2l",      0x5000, 0x1000, 0xc7d2e608 );
		ROM_LOAD("2m",      0x6000, 0x1000, 0xbc4bc5b1 );
	
		ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD("5f",      0x1000, 0x0800, 0x61f137e7 );/* we load the roms at 0x1000-0x1fff, they */
		ROM_LOAD("5h",      0x1800, 0x0800, 0x5581de5f );/* will be decrypted at 0x0000-0x0fff */

		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		ROM_LOAD("5c",      0x0000, 0x0800, 0xb899be2a );
		ROM_LOAD("5d",      0x0800, 0x0800, 0x6907af31 );
                ROM_END();
        }};
        static RomLoadPtr anteater_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "ra1-2c", 0x0000, 0x1000, 0x58bc9393 );
                ROM_LOAD( "ra1-2e", 0x1000, 0x1000, 0x574fc6f6 );
                ROM_LOAD( "ra1-2f", 0x2000, 0x1000, 0x2f7c1fe5 );
                ROM_LOAD( "ra1-2h", 0x3000, 0x1000, 0xae8a5da3 );

                ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "ra6-5f", 0x1000, 0x0800, 0x87300b4f );	/* we load the roms at 0x1000-0x1fff, they */
                ROM_LOAD( "ra6-5h", 0x1800, 0x0800, 0xaf4e5ffe );	/* will be decrypted at 0x0000-0x0fff */

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "ra4-5c", 0x0000, 0x0800, 0x4c3f8a08 );
                ROM_LOAD( "ra4-5d", 0x0800, 0x0800, 0xb30c7c9f );
                ROM_END();
        }};
        static RomLoadPtr rescue_rom = new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "rb15acpu.bin", 0x0000, 0x1000, 0xd7e654ba );
                ROM_LOAD( "rb15bcpu.bin", 0x1000, 0x1000, 0xa93ea158 );
                ROM_LOAD( "rb15ccpu.bin", 0x2000, 0x1000, 0x058cd3d0 );
                ROM_LOAD( "rb15dcpu.bin", 0x3000, 0x1000, 0xd6505742 );
                ROM_LOAD( "rb15ecpu.bin", 0x4000, 0x1000, 0x604df3a4 );

                ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "rb15fcpu.bin", 0x1000, 0x0800, 0x4489d20c );	/* we load the roms at 0x1000-0x1fff, they */
                ROM_LOAD( "rb15hcpu.bin", 0x1800, 0x0800, 0x5512c547 );	/* will be decrypted at 0x0000-0x0fff */

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "rb15csnd.bin", 0x0000, 0x0800, 0x8b24bf17 );
                ROM_LOAD( "rb15dsnd.bin", 0x0800, 0x0800, 0xd96e4fb3 );
                ROM_END();
        }};
        
         //game doesn't exist in v0.36 in this driver. There is hunchbkd in dkong.c driver though
        /*static RomModule hunchy_rom[] = {
                ROM_REGION(0x10000)     /* 64k for code */
       /*         ROM_LOAD( "1b.bin", 0x0000, 0x1000, 0xedc7328b ),
                ROM_LOAD( "2a.bin", 0x1000, 0x1000, 0x07a951d3 ),
                ROM_LOAD( "3a.bin", 0x2000, 0x1000, 0x84d6fc0e ),
                ROM_LOAD( "4c.bin", 0x3000, 0x1000, 0xe2a39fbd ),
                ROM_LOAD( "5a.bin", 0x4000, 0x1000, 0x2f46d302 ),
                ROM_LOAD( "6c.bin", 0x5000, 0x1000, 0x8d6b2637 ),

                ROM_REGION(0x2000)     /* temporary space for graphics (disposed after conversion) */
          /*      ROM_LOAD( "8a.bin",  0x0000, 0x0800, 0x7afe3b86 ),
                ROM_LOAD( "9b.bin",  0x0800, 0x0800, 0x015ee94c ),
                ROM_LOAD( "10b.bin", 0x1000, 0x0800, 0x72d82f86 ),
                ROM_LOAD( "11a.bin", 0x1800, 0x0800, 0xf5280414 ),

                ROM_REGION(0x10000)     /* 64k for the audio CPU */
        /*        ROM_LOAD( "5b_snd.bin", 0x0000, 0x0800, 0xd71b1c53 ),
        ROM_REGION(0, 0 )	};	*/
	
        static int bit(int i,int n)
        {
                return ((i >> n) & 1);
        }
        static DecodePtr losttomb_decode = new DecodePtr()
        {
             public void handler()
             {
                /*
                *   Code To Decode Lost Tomb by Mirko Buffoni
                *   Optimizations done by Fabio Buffoni
                */
                int i,j;
                

                /* The gfx ROMs are scrambled. Decode them. They have been loaded at 0x1000, */
                /* we write them at 0x0000. */
                RAM = Machine.memory_region[1];

                for (i = 0;i < 0x1000;i++)
                {
                        j = i & 0xa7f;
                        j |= ( bit(i,7) ^ (bit(i,1) & ( bit(i,7) ^ bit(i,10) ))) << 8;
                        j |= ( (bit(i,1) & bit(i,7)) | ((1 ^ bit(i,1)) & (bit(i,8)))) << 10;
                        j |= ( (bit(i,1) & bit(i,8)) | ((1 ^ bit(i,1)) & (bit(i,10)))) << 7;
                        RAM[i] = RAM[j + 0x1000];
                }
        }};

        static DecodePtr anteater_decode = new DecodePtr()
        {
         public void handler()
         {
                /*
                *   Code To Decode Lost Tomb by Mirko Buffoni
                *   Optimizations done by Fabio Buffoni
                */
                int i,j;
                


                /*
                //      Patch To Bypass The Self Test
                //
                //      Thanx To Chris Hardy For The Patch Code
                //  To Remove The Self Test For Rescue.
                //      (Also Works For Ant Eater, and Lost Tomb)
                */
                Machine.memory_region[ 0 ][ 0x008A ] = 0;
                Machine.memory_region[ 0 ][ 0x008B ] = 0;
                Machine.memory_region[ 0 ][ 0x008C ] = 0;

                Machine.memory_region[ 0 ][ 0x0091 ] = 0;
                Machine.memory_region[ 0 ][ 0x0092 ] = 0;
                Machine.memory_region[ 0 ][ 0x0093 ] = 0;

                Machine.memory_region[ 0 ][ 0x0097 ] = 0;
                Machine.memory_region[ 0 ][ 0x0098 ] = 0;
                Machine.memory_region[ 0 ][ 0x0099 ] = 0;

                        /* The gfx ROMs are scrambled. Decode them. They have been loaded at 0x1000, */
                        /* we write them at 0x0000. */
                        RAM = Machine.memory_region[1];

                        for (i = 0;i < 0x1000;i++)
                        {
                                j = i & 0x9bf;
                                j |= ( bit(i,0) ^ bit(i,6) ^ 1) << 10;
                                j |= ( bit(i,2) ^ bit(i,10) ) << 9;
                                j |= ( bit(i,4) ^ bit(i,9) ^ ( bit(i,2) & bit (i,10) )) << 6;
                                RAM[i] = RAM[j + 0x1000];
                        }
        }};

        static DecodePtr rescue_decode = new DecodePtr()
        {
            public void handler()
            {
                /*
                *   Code To Decode Lost Tomb by Mirko Buffoni
                *   Optimizations done by Fabio Buffoni
                */
                int i,j;
                

                /*
                //      Patch To Bypass The Self Test
                //
                //      Thanx To Chris Hardy For The Patch Code
                //  To Remove The Self Test For Rescue.
                //      (Also Works For Ant Eater, and Lost Tomb)
                */
                Machine.memory_region[ 0 ][ 0x008A ] = 0;
                Machine.memory_region[ 0 ][ 0x008B ] = 0;
                Machine.memory_region[ 0 ][ 0x008C ] = 0;

                Machine.memory_region[ 0 ][ 0x0091 ] = 0;
                Machine.memory_region[ 0 ][ 0x0092 ] = 0;
                Machine.memory_region[ 0 ][ 0x0093 ] = 0;

                Machine.memory_region[ 0 ][ 0x0097 ] = 0;
                Machine.memory_region[ 0 ][ 0x0098 ] = 0;
                Machine.memory_region[ 0 ][ 0x0099 ] = 0;

                /* The gfx ROMs are scrambled. Decode them. They have been loaded at 0x1000, */
                /* we write them at 0x0000. */
                RAM = Machine.memory_region[1];

                for (i = 0;i < 0x1000;i++)
                {
                        j = i & 0xa7f;
                        j |= ( bit(i,3) ^ bit(i,10) ) << 7;
                        j |= ( bit(i,1) ^ bit(i,7) ) << 8;
                        j |= ( bit(i,0) ^ bit(i,8) ) << 10;
                        RAM[i] = RAM[j + 0x1000];
                }
        }};
	public static GameDriver scobra_driver = new GameDriver
	(
                "Super Cobra (Konami)",               
		"scobra",
                "NICOLA SALMORIA",
		machine_driver,
	
		scobra_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		color_prom, null, null,
		ORIENTATION_ROTATE_90,
	
		null, null
	);
	
	public static GameDriver scobras_driver = new GameDriver
	(
                "Super Cobra (Stern)",
		"scobras",
                "NICOLA SALMORIA",
		machine_driver,
	
		scobras_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
                
	
		color_prom, null, null,
		ORIENTATION_ROTATE_90,
	
		null, null
	);
	
	public static GameDriver scobrab_driver = new GameDriver
	(
                "Super Cobra (bootleg)",
		"scobrab",
                "NICOLA SALMORIA",
		machine_driver,
	
		scobrab_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		color_prom, null, null,
		ORIENTATION_ROTATE_90,
	
		null, null
	);
	
	public static GameDriver losttomb_driver = new GameDriver
	(
                "Lost Tomb",
		"losttomb",
                "NICOLA SALMORIA\nJAMES R. TWINE\nMIRKO BUFFONI\nFABIO BUFFONI",
		machine_driver,
	
		losttomb_rom,
		losttomb_decode, null,
		null,
	
		LTinput_ports,null, trak_ports, LTdsw, LTkeys,
	
		color_prom, null, null,
		ORIENTATION_ROTATE_90,
	
		null, null
	);
        static HiscoreLoadPtr anteater_hiload = new HiscoreLoadPtr() { public int handler()
        {
          RAM = Machine.memory_region[0];
          FILE f;

          /* Wait for machine initialization to be done. */
 /*TOFIX                 if (memcmp(RAM,0x146a, new char[] { 0x01, 0x04, 0x80 }, 3) != 0) return 0;

          if ((f = fopen(name, "rb")) != null)
            {
              /* Load and set hiscore table. */
 /*TOFIX                     fread(RAM,0x146a,1,6*10,f);
              fclose(f);
            } */

          return 1;
        }};

        static HiscoreSavePtr anteater_hisave = new HiscoreSavePtr() { public void handler()
	{
          RAM = Machine.memory_region[0];
          FILE f;

   /*TOFIX               if ((f = fopen(name, "wb")) != null)
            {
              /* Write hiscore table. */
      /*TOFIX               fwrite(RAM,0x80ef,1,6*10,f);
              fclose(f);
            }*/
        }};


        public static GameDriver anteater_driver = new GameDriver
        (
                "Ant Eater",
                "anteater",
                "JAMES R. TWINE\nCHRIS HARDY\nMIRKO BUFFONI\nFABIO BUFFONI",
                machine_driver,

                anteater_rom,
                anteater_decode, null,
                null,

                AntEaterinput_ports,null, trak_ports, AntEaterdsw, AntEaterkeys,

                color_prom, null, null,
                ORIENTATION_ROTATE_90,
                anteater_hiload, anteater_hisave
        );


        public static GameDriver rescue_driver  = new GameDriver
        (
                "Rescue",
                "rescue",
                "JAMES R. TWINE\nCHRIS HARDY\nMIRKO BUFFONI\nFABIO BUFFONI\nALAN J MCCORMICK",
                machine_driver,

                rescue_rom,
                rescue_decode, null,
                null,

                Rescueinput_ports,null, trak_ports, Rescuedsw, Rescuekeys,

                rescue_color_prom, null, null,
                ORIENTATION_ROTATE_90,

                null, null
        );

         //game doesn't exist in v0.36 in this driver. There is hunchbkd in dkong.c driver though
       /* public static GameDriver hunchy_driver  = new GameDriver
        (
                "Hunchback",
                "hunchy",
                "JAMES R. TWINE\nCHRIS HARDY",
                machine_driver,

                hunchy_rom,
                null, null,
                null,

                LTinput_ports, trak_ports, LTdsw, LTkeys,

                color_prom, null, null,
                8*13, 8*16,

                null,null
        );*/
}
