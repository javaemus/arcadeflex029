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
 * NOTES: romsets are from v0.36 roms
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
import static vidhrdw.generic.*;
import static vidhrdw.congo.*;

public class congo
{



	static MemoryReadAddress readmem[] =
	{
                new MemoryReadAddress( 0x8000, 0x8fff, MRA_RAM ),
                new MemoryReadAddress( 0xa000, 0x83ff, MRA_RAM ),
                new MemoryReadAddress( 0xa000, 0xa7ff, MRA_RAM ),
                new MemoryReadAddress( 0xc000, 0xc000, input_port_0_r ),
                new MemoryReadAddress( 0xc001, 0xc001, input_port_1_r ),
                new MemoryReadAddress( 0xc008, 0xc008, input_port_2_r ),
                new MemoryReadAddress( 0xc002, 0xc002, input_port_3_r ),
                new MemoryReadAddress( 0xc003, 0xc003, input_port_4_r ),
                new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x8000, 0x83ff, MWA_RAM ),
		new MemoryWriteAddress( 0xa000, 0xa3ff, videoram_w, videoram,videoram_size ),
		new MemoryWriteAddress( 0xa400, 0xa7ff, colorram_w, colorram ),
		new MemoryWriteAddress( 0x8400, 0x8fff, MWA_RAM, spriteram ),
		new MemoryWriteAddress( 0xc01f, 0xc01f, interrupt_enable_w ),
		new MemoryWriteAddress( 0xc028, 0xc029, MWA_RAM, congo_background_position ),
		new MemoryWriteAddress( 0xc01d, 0xc01d, MWA_RAM, congo_background_enable ),
		new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};


	public static InterruptPtr congo_interrupt = new InterruptPtr() {
        public int handler() {
                if ((readinputport(5) & 1)!=0)	/* get status of the F2 key */
                        return nmi_interrupt.handler();	/* trigger self test */
                else return interrupt.handler();
        }
      };
      static InputPortPtr input_ports= new InputPortPtr(){ public void handler()  
      {
	PORT_START();	/* IN0 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_4WAY );
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_4WAY );
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_4WAY );
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* probably unused (the self test doesn't mention it) */
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* probably unused (the self test doesn't mention it) */
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* probably unused (the self test doesn't mention it) */

	PORT_START();	/* IN1 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* probably unused (the self test doesn't mention it) */
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* probably unused (the self test doesn't mention it) */
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* probably unused (the self test doesn't mention it) */

	PORT_START();	/* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* probably unused (the self test doesn't mention it) */
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* probably unused (the self test doesn't mention it) */
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* probably unused (the self test doesn't mention it) */
	/* the coin inputs must stay active for exactly one frame, otherwise */
	/* the game will keep inserting coins. */
	PORT_BITX(0x20, IP_ACTIVE_HIGH, IPT_COIN1 | IPF_IMPULSE,
			"Coin A", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
	PORT_BITX(0x40, IP_ACTIVE_HIGH, IPT_COIN2 | IPF_IMPULSE,
			"Coin B", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
	/* Coin Aux doesn't need IMPULSE to pass the test, but it still needs it */
	/* to avoid the freeze. */
	PORT_BITX(0x80, IP_ACTIVE_HIGH, IPT_COIN3 | IPF_IMPULSE,
			"Coin Aux", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );

	PORT_START();	/* DSW0 */
	PORT_DIPNAME( 0x03, 0x03, "Bonus Life", IP_KEY_NONE );
	PORT_DIPSETTING(    0x03, "10000" );
	PORT_DIPSETTING(    0x01, "20000" );
	PORT_DIPSETTING(    0x02, "30000" );
	PORT_DIPSETTING(    0x00, "40000" );
	PORT_DIPNAME( 0x0c, 0x0c, "Difficulty???", IP_KEY_NONE );
	PORT_DIPSETTING(    0x0c, "Easy?" );
	PORT_DIPSETTING(    0x04, "Medium?" );
	PORT_DIPSETTING(    0x08, "Hard?" );
	PORT_DIPSETTING(    0x00, "Hardest?" );
	PORT_DIPNAME( 0x30, 0x30, "Lives", IP_KEY_NONE );
	PORT_DIPSETTING(    0x30, "3" );
	PORT_DIPSETTING(    0x10, "4" );
	PORT_DIPSETTING(    0x20, "5" );
	PORT_DIPSETTING(    0x00, "Infinite" );
	PORT_DIPNAME( 0x40, 0x40, "Demo Sounds", IP_KEY_NONE );
	PORT_DIPSETTING(    0x00, "Off" );
	PORT_DIPSETTING(    0x40, "On" );
	PORT_DIPNAME( 0x80, 0x00, "Cabinet", IP_KEY_NONE );
	PORT_DIPSETTING(    0x00, "Upright" );
	PORT_DIPSETTING(    0x80, "Cocktail" );

	PORT_START();	/* DSW1 */
 	PORT_DIPNAME( 0x0f, 0x03, "B Coin/Cred", IP_KEY_NONE );
	PORT_DIPSETTING(    0x0f, "4/1" );
	PORT_DIPSETTING(    0x07, "3/1" );
	PORT_DIPSETTING(    0x0b, "2/1" );
	PORT_DIPSETTING(    0x06, "2/1+Bonus each 5" );
	PORT_DIPSETTING(    0x0a, "2/1+Bonus each 2" );
	PORT_DIPSETTING(    0x03, "1/1" );
	PORT_DIPSETTING(    0x02, "1/1+Bonus each 5" );
	PORT_DIPSETTING(    0x0c, "1/1+Bonus each 4" );
	PORT_DIPSETTING(    0x04, "1/1+Bonus each 2" );
	PORT_DIPSETTING(    0x0d, "1/2" );
	PORT_DIPSETTING(    0x08, "1/2+Bonus each 5" );
	PORT_DIPSETTING(    0x00, "1/2+Bonus each 4" );
	PORT_DIPSETTING(    0x05, "1/3" );
	PORT_DIPSETTING(    0x09, "1/4" );
	PORT_DIPSETTING(    0x01, "1/5" );
	PORT_DIPSETTING(    0x0e, "1/6" );
	PORT_DIPNAME( 0xf0, 0x30, "A Coin/Cred", IP_KEY_NONE );
	PORT_DIPSETTING(    0xf0, "4/1" );
	PORT_DIPSETTING(    0x70, "3/1" );
	PORT_DIPSETTING(    0xb0, "2/1" );
	PORT_DIPSETTING(    0x60, "2/1+Bonus each 5" );
	PORT_DIPSETTING(    0xa0, "2/1+Bonus each 2" );
	PORT_DIPSETTING(    0x30, "1/1" );
	PORT_DIPSETTING(    0x20, "1/1+Bonus each 5" );
	PORT_DIPSETTING(    0xc0, "1/1+Bonus each 4" );
	PORT_DIPSETTING(    0x40, "1/1+Bonus each 2" );
	PORT_DIPSETTING(    0xd0, "1/2" );
	PORT_DIPSETTING(    0x80, "1/2+Bonus each 5" );
	PORT_DIPSETTING(    0x00, "1/2+Bonus each 4" );
	PORT_DIPSETTING(    0x50, "1/3" );
	PORT_DIPSETTING(    0x90, "1/4" );
	PORT_DIPSETTING(    0x10, "1/5" );
	PORT_DIPSETTING(    0xe0, "1/6" );

	PORT_START();	/* FAKE */
	/* This fake input port is used to get the status of the F2 key, */
	/* and activate the test mode, which is triggered by a NMI */
	PORT_BITX(0x01, IP_ACTIVE_HIGH, IPT_SERVICE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
        INPUT_PORTS_END();
      }};


	
	static GfxLayout charlayout1 = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 256*8*8 },	/* the two bitplanes are separated */
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		8*8	/* every char takes 8 consecutive bytes */
	);

	static GfxLayout spritelayout = new GfxLayout
	(
		32,32,	/* 32*32 sprites */
		128,	/* 64 sprites */
		3,	/* 3 bits per pixel */
		new int[] { 0, 2*1024*8*8, 2*2*1024*8*8 },    /* the bitplanes are separated */
		new int[] { 103*8, 102*8, 101*8, 100*8, 99*8, 98*8, 97*8, 96*8,
				71*8, 70*8, 69*8, 68*8, 67*8, 66*8, 65*8, 64*8,
				39*8, 38*8, 37*8, 36*8, 35*8, 34*8, 33*8, 32*8,
				7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7,
				16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7,
				24*8+0, 24*8+1, 24*8+2, 24*8+3, 24*8+4, 24*8+5, 24*8+6, 24*8+7 },
		128*8	/* every sprite takes 128 consecutive bytes */
	);

	static GfxLayout charlayout2 = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		1024,	/* 1024 characters */
		3,	/* 3 bits per pixel */
		new int[] { 0, 1024*8*8, 2*1024*8*8 },	/* the bitplanes are separated */
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		8*8	/* every char takes 8 consecutive bytes */
	);


		
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout1,          0, 16 ),	/* characters */
		new GfxDecodeInfo( 1, 0x1000, spritelayout,      16*4, 32 ),	/* sprites */
		new GfxDecodeInfo( 1, 0xd000, charlayout2,  16*4+32*8, 16 ),	/* background tiles */
		new GfxDecodeInfo( -1 ) /* end of array */
	};


	
	static char palette[] =
	{
                        0x00,0x00,0x00,	/* BLACK */

                  0xff,0x00,0x00, /* RED */
                  0x00,0xff,0x00, /* GREEN */
                  0x00,0x00,0xff, /* BLUE */
                  0xff,0xff,0x00, /* YELLOW */
                  0xff,0x00,0xff, /* MAGENTA */
                  0x00,0xff,0xff, /* CYAN */
                  0xff,0xff,0xff, /* WHITE */
                  0xE0,0xE0,0xE0, /* LTGRAY */
                  0xC0,0xC0,0xC0, /* DKGRAY */

                        0xe0,0xb0,0x70,	/* BROWN */
                        0xd0,0xa0,0x60,	/* BROWN0 */
                        0xc0,0x90,0x50,	/* BROWN1 */
                        0xa3,0x78,0x3a,	/* BROWN2 */
                        0x80,0x60,0x20,	/* BROWN3 */
                        0x54,0x40,0x14,	/* BROWN4 */

                  0x54,0xa8,0xff, /* LTBLUE */
                  0x00,0xa0,0x00, /* DKGREEN */
                  0x00,0xe0,0x00, /* GRASSGREEN */



                        0xff,0xb6,0xdb,	/* PINK */
                        0x49,0xb6,0xdb,	/* DKCYAN */
                        0xff,96,0x49,	/* DKORANGE */
                        0xff,128,0x00,	/* ORANGE */
                        0xdb,0xdb,0xdb	/* GREY */
	};

	static final int BLACK = 0, RED = 1, GREEN = 2, BLUE = 3, YELLOW = 4, MAGENTA = 5, CYAN = 6, WHITE = 7, LTGRAY = 8, DKGRAY = 9,
       BROWN = 10, BROWN0 = 11, BROWN1 = 12, BROWN2 = 13, BROWN3 = 14, BROWN4 = 15,
                LTBLUE=16,DKGREEN=17,GRASSGREEN=18,PINK=19,DKCYAN=20,DKORANGE=21,ORANGE=22,GREY=23;
			 
	
	static char colortable[] =
	{
                /* chars (16) */
                0,0,BLACK,CYAN,   /* Top line */
                0,0,BLACK,WHITE,  /* Second line, HIGH SCORES, SEGA */
                0,0,BLACK,YELLOW, /* CREDITS, INSERT COIN */
                0,CYAN,BLACK,RED, /* scores, BONUS */
                0,0,BLACK,GREEN,  /* 1 PLAYER... */
                BLACK,WHITE,BLACK,BROWN,   /* Life */
                BROWN3,BROWN2,BLACK,BLACK, /* Life */
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,


                /* sprites (32) */
                0,BROWN1,BLACK,BROWN,BROWN4,BROWN3,BROWN2,WHITE,   /* monkey1 */
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,BROWN4,BLACK,BROWN2,BLACK,BROWN3,BROWN4,WHITE,  /* monkey2 */
                0,WHITE,BLACK,BROWN,BROWN0,BROWN3,BROWN1,BROWN2,  /* hero */
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,LTGRAY,LTGRAY,LTGRAY,LTGRAY,LTGRAY,LTGRAY,LTGRAY, /* stage 3: beside holes */
                0,0,0,0,0,0,0,0,
                0,BROWN1,BROWN3,BROWN2,BROWN2,BROWN3,BROWN3,BROWN2, /* stage 1: falling bridge */
                0,0,0,0,0,0,0,0,
                0,DKGRAY,LTGRAY,LTBLUE,BLUE,WHITE,BLACK,DKCYAN,     /* hippo */
                0,RED,BROWN4,ORANGE,YELLOW,WHITE,BROWN1,BLACK,      /* scorpion */
                0,GREEN,ORANGE,GRASSGREEN,DKGREEN,BLACK,WHITE,BROWN4, /* snake & rino1 */
                0,RED,DKORANGE,YELLOW,ORANGE,BROWN2,BROWN3,BROWN,   /* barrels */
                0,BROWN4,BLACK,BROWN,WHITE,BROWN1,BROWN,WHITE,      /* hero asleep in tent */
                0,BROWN2,YELLOW,YELLOW,BLACK,WHITE,BROWN3,BROWN,    /* guy in hole */
                0,GREEN,DKGREEN,BLACK,GRASSGREEN,WHITE,CYAN,WHITE,  /* stage 4: moving leaf */
                0,DKGRAY,CYAN,LTBLUE,BLUE,BLACK,WHITE,BROWN4,       /* rino1 */
                0,BROWN0,BROWN0,BROWN2,BROWN4,BROWN3,WHITE,BLACK,   /* kong */
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,DKGRAY,LTBLUE,LTGRAY,BLUE,WHITE,WHITE,BLACK,      /* whale */
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,ORANGE,DKGREEN,BLUE,YELLOW,GREEN,LTBLUE,BLACK,    /* snoring zzzzz */

                /* background tiles (16) */
                0,0,0,0,0,0,0,0,  /* Score Back, all Black */
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,
         BROWN4,BROWN1,BROWN2,BROWN3,DKCYAN,LTBLUE,WHITE,DKGREEN, /* Brown with trees */
         BROWN4,BROWN1,BROWN2,BROWN3,DKCYAN,BROWN0,WHITE,BROWN3, /* Brown wood:stage 1 bridge, stage 2 */
         BROWN4,BROWN1,BROWN2,BROWN3,DKCYAN,GRASSGREEN,LTGRAY,DKGREEN, /* stage 2 green */
         BLACK,BROWN1,BROWN2,BROWN3,DKCYAN,BROWN3,WHITE,LTGRAY, /* stage 1 under the skeleton, stage 3 */
         0,DKGRAY,LTBLUE,LTGRAY,BLUE,WHITE,WHITE,BLACK,      /* bird on the mountain at the end of level 2 */
         0,BROWN1,BROWN3,BROWN2,BROWN2,BROWN3,BROWN3,BROWN2, /* two tiles on top right of level 3 */
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
				congo_interrupt, 1
			)
		},
		60,
		null,

		/* video hardware */
		32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
		gfxdecodeinfo,
		sizeof(palette)/3, sizeof(colortable),
		null,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
		null,
		congo_vh_start,
		congo_vh_stop,
		congo_vh_screenrefresh,

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
	static RomLoadPtr congo_rom= new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
	
                ROM_LOAD( "congo1.bin",   0x0000, 0x2000, 0x09355b5b );
                ROM_LOAD( "congo2.bin",   0x2000, 0x2000, 0x1c5e30ae );
                ROM_LOAD( "congo3.bin",   0x4000, 0x2000, 0x5ee1132c );
                ROM_LOAD( "congo4.bin",   0x6000, 0x2000, 0x5332b9bf );
                
	        ROM_REGION(0x13000);      /* temporary space for graphics (disposed after conversion) */
	        ROM_LOAD( "congo5.bin", 0x0000, 0x1000, 0x7bf6ba2b );	/* characters */
	        ROM_LOAD( "congo16.bin", 0x1000, 0x2000, 0xcb6d5775 );
	        ROM_LOAD( "congo15.bin", 0x3000, 0x2000, 0x7b15a7a4 );
	        ROM_LOAD( "congo12.bin", 0x5000, 0x2000, 0x15e3377a );
	        ROM_LOAD( "congo14.bin", 0x7000, 0x2000, 0xbf9169fe );
	        ROM_LOAD( "congo11.bin", 0x9000, 0x2000, 0x73e2709f );
	        ROM_LOAD( "congo13.bin", 0xb000, 0x2000, 0x1d1321c8 );
	        ROM_LOAD( "congo10.bin", 0xd000, 0x2000, 0xf27a9407 );
	        ROM_LOAD( "congo9.bin",  0xf000, 0x2000, 0x93e2309e );
	        ROM_LOAD( "congo8.bin",  0x11000, 0x2000, 0xdb99a619 );
                             
	/*
	*        5 is characters, 3 bitplanes in one rom
	*        16 and 15 are 1 plane of the sprites
	*        12 and 14 the second
	*        11 and 13 third one
	*        10 - 8 are the background graphics
	*/
	        ROM_REGION(0x8000);      /* background data */
	        ROM_LOAD( "congo6.bin",  0x0000, 0x2000, 0xd637f02b );
	        ROM_LOAD( "congo6.bin",  0x2000, 0x2000, 0xd637f02b );
	        ROM_LOAD( "congo7.bin",  0x4000, 0x2000, 0x80927943 );
	        ROM_LOAD( "congo7.bin", 0x6000, 0x2000, 0x80927943 );
	       /* I cheated a little with the background graphics - this
	        * approach involved least writing ;)
	        */
                ROM_END();
        }};
	

	
	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
		/* check if the hi score table has already been initialized */
		if (memcmp(RAM, 0x8030, new char[] { 0x00, 0x89, 0x00 }, 3) == 0 &&
				memcmp(RAM, 0x8099, new char[] { 0x00, 0x37, 0x00 }, 3) == 0)
		{
			FILE f;


			if ((f = fopen(name, "rb")) != null)
			{
				fread(RAM, 0x8020, 1, 21*6, f);
				RAM[0x80bd] = RAM[0x8030];
				RAM[0x80be] = RAM[0x8031];
				RAM[0x80bf] = RAM[0x8032];
				fclose(f);
			}

			return 1;
		}
		else return 0;	/* we can't load the hi scores yet */
	} };



	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name)
	{
                /* make sure that the high score table is still valid (entering the */
                /* test mode corrupts it) */
                if (memcmp(RAM,0x8030,new char[]{0x00,0x00,0x00},3) != 0)
                {
                        FILE f;


                        if ((f = fopen(name,"wb")) != null)
                        {
                                fwrite(RAM,0x8020,1,21*6,f);
                                fclose(f);
                        }
                }
	} };



	public static GameDriver congo_driver = new GameDriver
	(
                "Congo Bongo",
		"congo",
                "Ville Laitinen (MAME driver)\nNicola Salmoria (Zaxxon driver)\nMarc Lafontaine (colors)\nPaul Berberich (colors)",
		machine_driver,
	
		congo_rom,
		null, null,
		null,
	
		null/*TBR*/,input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,
	
		null, palette, colortable,
		ORIENTATION_DEFAULT,
	
		hiload, hisave
	);
}
