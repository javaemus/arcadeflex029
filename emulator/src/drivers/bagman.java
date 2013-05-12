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
 *  roms are from v0.36 romset
 *
 */


package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static machine.bagman.*;
import static mame.inptport.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.bagman.*;
import static vidhrdw.generic.*;
import static vidhrdw.bagman.*;

public class bagman
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x5fff, MRA_ROM ),
                new MemoryReadAddress( 0x6000, 0x67ff, MRA_RAM ),
                new MemoryReadAddress( 0x9000, 0x93ff, MRA_RAM ),	/* video RAM */
                new MemoryReadAddress( 0x9800, 0x9bff, MRA_RAM ),	/* color RAM + sprites */
                new MemoryReadAddress( 0xa000, 0xa000, bagman_rand_r ),
                new MemoryReadAddress( 0xa800, 0xa800, MRA_NOP ),
                new MemoryReadAddress( 0xb000, 0xb000, input_port_2_r ),	/* DSW */
                new MemoryReadAddress( 0xb800, 0xb800, MRA_NOP ),
                new MemoryReadAddress( 0xc000, 0xffff, MRA_ROM ),	/* Super Bagman only */
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x5fff, MWA_ROM ),
                new MemoryWriteAddress( 0x6000, 0x67ff, MWA_RAM ),
                new MemoryWriteAddress( 0x9000, 0x93ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x9800, 0x9bff, colorram_w, colorram ),
                new MemoryWriteAddress( 0xa000, 0xa000, interrupt_enable_w ),
                new MemoryWriteAddress( 0xa001, 0xa002, bagman_flipscreen_w ),
                new MemoryWriteAddress( 0xa003, 0xa003, MWA_RAM, bagman_video_enable ),
                new MemoryWriteAddress( 0xc000, 0xffff, MWA_ROM ),	/* Super Bagman only */
                new MemoryWriteAddress( 0x9800, 0x981f, MWA_RAM, spriteram, spriteram_size ),	/* hidden portion of color RAM */
                                                                                                                        /* here only to initialize the pointer, */
                                                                                                                        /* writes are handled by colorram_w */
                new MemoryWriteAddress( 0x9c00, 0x9fff, MWA_NOP ),	/* written to, but unused */
		new MemoryWriteAddress( -1 )	/* end of table */
	};

	static IOReadPort readport[] =
	{
		new IOReadPort( 0x0c, 0x0c, AY8910_read_port_0_r ),
		new IOReadPort( -1 )	/* end of table */
	};

	static IOWritePort writeport[] =
	{
		new IOWritePort( 0x08, 0x08, AY8910_control_port_0_w ),
		new IOWritePort( 0x09, 0x09, AY8910_write_port_0_w ),
                new IOWritePort( 0x56, 0x56, IOWP_NOP ),
		new IOWritePort( -1 )	/* end of table */
	};


        static InputPortPtr bagman_input_ports= new InputPortPtr(){ public void handler() 
        {
            PORT_START();	/* IN0 */
            PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
            PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
            PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
            PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
            PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
            PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
            PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
            PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );

            PORT_START();	/* IN1 */
            PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN3 );
            PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN4 );
            PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START2 );
            PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
            PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
            PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
            PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
            PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );

            PORT_START();	/* DSW */
            PORT_DIPNAME( 0x03, 0x02, "Lives", IP_KEY_NONE );
            PORT_DIPSETTING(    0x03, "2" );
            PORT_DIPSETTING(    0x02, "3" );
            PORT_DIPSETTING(    0x01, "4" );
            PORT_DIPSETTING(    0x00, "5" );
            PORT_DIPNAME( 0x04, 0x04, "Coinage", IP_KEY_NONE );
            PORT_DIPSETTING(    0x00, "2 Coins/1 Credit" );
            PORT_DIPSETTING(    0x04, "1 Coin/1 Credit" );
            PORT_DIPNAME( 0x18, 0x18, "Difficulty", IP_KEY_NONE );
            PORT_DIPSETTING(    0x18, "Easy" );
            PORT_DIPSETTING(    0x10, "Medium" );
            PORT_DIPSETTING(    0x08, "Hard" );
            PORT_DIPSETTING(    0x00, "Hardest" );
            PORT_DIPNAME( 0x20, 0x20, "Language", IP_KEY_NONE );
            PORT_DIPSETTING(    0x20, "English" );
            PORT_DIPSETTING(    0x00, "French" );
            PORT_DIPNAME( 0x40, 0x40, "Bonus Life", IP_KEY_NONE );
            PORT_DIPSETTING(    0x40, "30000" );
            PORT_DIPSETTING(    0x00, "40000" );
            PORT_DIPNAME( 0x80, 0x80, "Cabinet", IP_KEY_NONE );
            PORT_DIPSETTING(    0x80, "Upright" );
            PORT_DIPSETTING(    0x00, "Cocktail" );
            INPUT_PORTS_END();
        }};

/* EXACTLY the same as bagman, the only difference is that the START1 button */
/* also acts as the shoot button. */
        static InputPortPtr sbagman_input_ports= new InputPortPtr(){ public void handler() 
        {
            PORT_START();	/* IN0 */
            PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
            PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
            PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
            PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 );	/* double-function button, start and shoot */
            PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
            PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
            PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
            PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
            PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );

            PORT_START();	/* IN1 */
            PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN3 );
            PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN4 );
            PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START2 );
            PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );	/* double-function button, start and shoot */
            PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
            PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
            PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
            PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
            PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );

            PORT_START();	/* DSW */
            PORT_DIPNAME( 0x03, 0x02, "Lives", IP_KEY_NONE );
            PORT_DIPSETTING(    0x03, "2" );
            PORT_DIPSETTING(    0x02, "3" );
            PORT_DIPSETTING(    0x01, "4" );
            PORT_DIPSETTING(    0x00, "5" );
            PORT_DIPNAME( 0x04, 0x04, "Coinage", IP_KEY_NONE );
            PORT_DIPSETTING(    0x00, "2 Coins/1 Credit" );
            PORT_DIPSETTING(    0x04, "1 Coin/1 Credit" );
            PORT_DIPNAME( 0x18, 0x18, "Difficulty", IP_KEY_NONE );
            PORT_DIPSETTING(    0x18, "Easy" );
            PORT_DIPSETTING(    0x10, "Medium" );
            PORT_DIPSETTING(    0x08, "Hard" );
            PORT_DIPSETTING(    0x00, "Hardest" );
            PORT_DIPNAME( 0x20, 0x20, "Language", IP_KEY_NONE );
            PORT_DIPSETTING(    0x20, "English" );
            PORT_DIPSETTING(    0x00, "French" );
            PORT_DIPNAME( 0x40, 0x40, "Bonus Life", IP_KEY_NONE );
            PORT_DIPSETTING(    0x40, "30000" );
            PORT_DIPSETTING(    0x00, "40000" );
            PORT_DIPNAME( 0x80, 0x80, "Cabinet", IP_KEY_NONE );
            PORT_DIPSETTING(    0x80, "Upright" );
            PORT_DIPSETTING(    0x00, "Cocktail" );
            INPUT_PORTS_END();
        }};


	static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                512,	/* 512 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 512*8*8 },	/* the two bitplanes are separated */
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },	/* pretty straightforward layout */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                128,	/* 128 sprites */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 128*16*16 },	/* the two bitplanes are separated */
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7,	/* pretty straightforward layout */
                                8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                                16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
                32*8	/* every sprite takes 32 consecutive bytes */
	);



	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,      0, 16 ),	/* char set #1 */
		new GfxDecodeInfo( 1, 0x2000, charlayout,      0, 16 ),	/* char set #2 */
		new GfxDecodeInfo( 1, 0x0000, spritelayout,    0, 16 ),	/* sprites */
		new GfxDecodeInfo( -1 ) /* end of array */
	};



	static char palette[] =
	{
		0x00,0x00,0x00,	/* BLACK */
		0x49,0x00,0x00,	/* DKRED1 */
		0x92,0x00,0x00,	/* DKRED2 */
		0xff,0x00,0x00,	/* RED */
		0x00,0x24,0x00,	/* DKGRN1 */
		0x92,0x24,0x00,	/* DKBRN1 */
		0xb6,0x24,0x00,	/* DKBRN2 */
		0xff,0x24,0x00,	/* LTRED1 */
		0xdb,0x49,0x00,	/* BROWN */
		0x00,0x6c,0x00,	/* DKGRN2 */
		0xff,0x6c,0x00,	/* LTORG1 */
		0x00,0x92,0x00,	/* DKGRN3 */
		0x92,0x92,0x00,	/* DKYEL */
		0xdb,0x92,0x00,	/* DKORG */
		0xff,0x92,0x00,	/* ORANGE */
		0x00,0xdb,0x00,	/* GREEN1 */
		0x6d,0xdb,0x00,	/* LTGRN1 */
		0x00,0xff,0x00,	/* GREEN2 */
		0x49,0xff,0x00,	/* LTGRN2 */
		0xff,0xff,0x00,	/* YELLOW */
		0x00,0x00,0x55,	/* DKBLU1 */
		0xff,0x00,0x55,	/* DKPNK1 */
		0xff,0x24,0x55,	/* DKPNK2 */
		0xff,0x6d,0x55,	/* LTRED2 */
		0xdb,0x92,0x55,	/* LTBRN */
		0xff,0x92,0x55,	/* LTORG2 */
		0x24,0xff,0x55,	/* LTGRN3 */
		0x49,0xff,0x55,	/* LTGRN4 */
		0xff,0xff,0x55,	/* LTYEL */
		0x00,0x00,0xaa,	/* DKBLU2 */
		0xff,0x00,0xaa,	/* PINK1 */
		0x00,0x24,0xaa,	/* DKBLU3 */
		0xff,0x24,0xaa,	/* PINK2 */
		0xdb,0xdb,0xaa,	/* CREAM */
		0xff,0xdb,0xaa,	/* LTORG3 */
		0x00,0x00,0xff,	/* BLUE */
		0xdb,0x00,0xff,	/* PURPLE */
		0x00,0xb6,0xff,	/* LTBLU1 */
		0x92,0xdb,0xff,	/* LTBLU2 */
		0xdb,0xdb,0xff,	/* WHITE1 */
		0xff,0xff,0xff	/* WHITE2 */
	};

	static final int BLACK = 0;
	static final int DKRED1 = 1;
	static final int DKRED2 = 2;
	static final int RED = 3;
	static final int DKGRN1 = 4;
	static final int DKBRN1 = 5;
	static final int DKBRN2 = 6;
	static final int LTRED1 = 7;
	static final int BROWN = 8;
	static final int DKGRN2 = 9;
	static final int LTORG1 = 10;
	static final int DKGRN3 = 11;
	static final int DKYEL = 12;
	static final int DKORG = 13;
	static final int ORANGE = 14;
	static final int GREEN1 = 15;
	static final int LTGRN1 = 16;
	static final int GREEN2 = 17;
	static final int LTGRN2 = 18;
	static final int YELLOW = 19;
	static final int DKBLU1 = 20;
	static final int DKPNK1 = 21;
	static final int DKPNK2 = 22;
	static final int LTRED2 = 23;
	static final int LTBRN = 24;
	static final int LTORG2 = 25;
	static final int LTGRN3 = 26;
	static final int LTGRN4 = 27;
	static final int LTYEL = 28;
	static final int DKBLU2 = 29;
	static final int PINK1 = 30;
	static final int DKBLU3 = 31;
	static final int PINK2 = 32;
	static final int CREAM = 33;
	static final int LTORG3 = 34;
	static final int BLUE = 35;
	static final int PURPLE = 36;
	static final int LTBLU1 = 37;
	static final int LTBLU2 = 38;
	static final int WHITE1 = 39;
	static final int WHITE2 = 40;

	static char colortable[] =
	{
	/* characters and sprites */
	BLACK,BLUE,LTYEL,LTBLU1,	/* an axe, picked bag */
	BLACK,BLUE,LTYEL,LTBLU1,	/* a bag */
	BLACK,WHITE1,DKGRN3,BLUE,	/* cactus */
	BLACK,RED,BLUE,LTBLU1,		/*  */
	BLACK,RED,BLUE,WHITE1,		/* a bomb, the train */
	BLACK,BLUE,WHITE1,LTBLU1,	/* picked gun */
	BLACK,BLUE,WHITE1,LTBLU1,	/* logo, gun */
	BLACK,LTYEL,BROWN,WHITE1,	/*  */
	BLACK,LTBRN,BROWN,CREAM,	/*  */
	BLACK,RED,LTRED1,YELLOW,	/*  */
	BLACK,LTBRN,CREAM,LTRED1,	/*  */
	BLACK,LTYEL,BLUE,BROWN,		/*  */
	BLACK,LTBRN,BLUE,CREAM,	/* policeman, big bagman (game front) */
	BLACK,YELLOW,BLUE,RED,		/*  */
	BLACK,DKGRN3,LTBLU1,BROWN,	/*  */
	BLACK,BROWN,DKBRN1,LTBLU1,	/* ground, the stairs, the drabina */
	};



	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz (?) */
				0,
				readmem, writemem, readport, writeport,
				interrupt, 1
			)
		},
		60,
		null,

		/* video hardware */
		32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
		gfxdecodeinfo,
		sizeof(palette) / 3, sizeof(colortable),
		null,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
		null,
		generic_vh_start,
		generic_vh_stop,
		bagman_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		bagman_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);



	/***************************************************************************

	  Game driver(s)

	***************************************************************************/
        static RomLoadPtr bagman_rom = new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "e9_b05.bin",   0x0000, 0x1000, 0xe0156191 );
                ROM_LOAD( "f9_b06.bin",   0x1000, 0x1000, 0x7b758982 );
                ROM_LOAD( "f9_b07.bin",   0x2000, 0x1000, 0x302a077b );
                ROM_LOAD( "k9_b08.bin",   0x3000, 0x1000, 0xf04293cb );
                ROM_LOAD( "m9_b09s.bin",  0x4000, 0x1000, 0x68e83e4f );
                ROM_LOAD( "n9_b10.bin",   0x5000, 0x1000, 0x1d6579f7 );

		 ROM_REGION(0x4000);	/* temporary space for graphics (disposed after conversion) */
                 ROM_LOAD( "e1_b02.bin",   0x0000, 0x1000, 0x4a0a6b55 );
	         ROM_LOAD( "j1_b04.bin",   0x1000, 0x1000, 0xc680ef04 );
                 ROM_LOAD( "c1_b01.bin",   0x2000, 0x1000, 0x705193b2 );
	         ROM_LOAD( "f1_b03s.bin",  0x3000, 0x1000, 0xdba1eda7 );

		 ROM_REGION(0x2000);	/* ??? */
		 ROM_LOAD("r9_b11.bin",   0x0000, 0x1000, 0x2e0057ff );
		 ROM_LOAD("t9_b12.bin",   0x1000, 0x1000, 0xb2120edd );
                 ROM_END();
        }};

        static RomLoadPtr sbagman_rom = new RomLoadPtr(){ public void handler()  
        {
            ROM_REGION(0x10000);	/* 64k for code */
            ROM_LOAD( "5.9e",         0x0000, 0x1000, 0x1b1d6b0a );
            ROM_LOAD( "6.9f",         0x1000, 0x1000, 0xac49cb82 );
            ROM_LOAD( "7.9j",         0x2000, 0x1000, 0x9a1c778d );
            ROM_LOAD( "8.9k",         0x3000, 0x1000, 0xb94fbb73 );
            ROM_LOAD( "9.9m",         0x4000, 0x1000, 0x601f34ba );
            ROM_LOAD( "10.9n",        0x5000, 0x1000, 0x5f750918 );
            ROM_LOAD("13.8d",        0xc000, 0x0e00, 0x944a4453 );
            ROM_CONTINUE( 0xfe00, 0x0200 );
            ROM_LOAD( "14.8f",        0xd000, 0x0400, 0x83b10139 );
            ROM_CONTINUE( 0xe400, 0x0200 );
            ROM_CONTINUE( 0xd600, 0x0a00 );
            ROM_LOAD( "15.8j",        0xe000, 0x0400, 0xfe924879 );
            ROM_CONTINUE( 0xd400, 0x0200 );
            ROM_CONTINUE( 0xe600, 0x0a00 );
            ROM_LOAD( "16.8k",        0xf000, 0x0e00, 0xb77eb1f5 );
            ROM_CONTINUE( 0xce00, 0x0200 );

            ROM_REGION(0x4000);	/* temporary space for graphics (disposed after conversion) */
            ROM_LOAD( "2.1e",         0x0000, 0x1000, 0xf4d3d4e6  );
            ROM_LOAD( "4.1j",         0x1000, 0x1000, 0x2c6a510d );
            ROM_LOAD( "1.1c",         0x2000, 0x1000, 0xa046ff44 );
            ROM_LOAD( "3.1f",         0x3000, 0x1000, 0xa4422da4 );

            ROM_REGION(0x2000);	/* ??? */
            ROM_LOAD( "11.9r",        0x0000, 0x1000, 0x2e0057ff );
            ROM_LOAD( "12.9t",        0x1000, 0x1000, 0xb2120edd );
            ROM_END();
        }};

	public static GameDriver bagman_driver = new GameDriver
	(
                "Bagman",
		"bagman",
               "Robert Anschuetz (Arcade emulator)\nNicola Salmoria (MAME driver)\nJarek Burczynski (colors)",
		machine_driver,

		bagman_rom,
		null, null,
		null,

		null,bagman_input_ports, null, null, null,

		null, palette, colortable,
		ORIENTATION_ROTATE_270,

		null, null
	);
        public static GameDriver sbagman_driver = new GameDriver
        (
                "Super Bagman",
                "sbagman",
                "Jarek Burczynski (enhancement of the Bagman driver)",
                machine_driver,

                sbagman_rom,
                null, null,
                        null,

                null,sbagman_input_ports, null, null, null,

                null, palette, colortable,
                ORIENTATION_ROTATE_270,

                null, null
        );
}

