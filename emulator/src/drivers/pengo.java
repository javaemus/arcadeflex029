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
 *  games uses v0.36 romset
 *  pengoa isn't supported since we can't find the romset
 */


package drivers;
import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static sndhrdw.pengo.*;
import static vidhrdw.generic.*;
import static vidhrdw.pengo.*;
import static mame.memoryH.*;
public class pengo
{



	public static MemoryReadAddress readmem[] =
	{
            new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
            new MemoryReadAddress( 0x8000, 0x8fff, MRA_RAM ),	/* video and color RAM, scratchpad RAM, sprite codes */
            new MemoryReadAddress( 0x9000, 0x903f, input_port_3_r ),	/* DSW1 */
            new MemoryReadAddress( 0x9040, 0x907f, input_port_2_r ),	/* DSW0 */
            new MemoryReadAddress( 0x9080, 0x90bf, input_port_1_r ),	/* IN1 */
            new MemoryReadAddress( 0x90c0, 0x90ff, input_port_0_r ),	/* IN0 */
            new MemoryReadAddress( -1 )	/* end of table */
	};

	public static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
                new MemoryWriteAddress( 0x8000, 0x83ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x8400, 0x87ff, colorram_w, colorram ),
                new MemoryWriteAddress( 0x8800, 0x8fef, MWA_RAMROM ),
                new MemoryWriteAddress( 0x8ff0, 0x8fff, MWA_RAM, spriteram, spriteram_size),
                new MemoryWriteAddress( 0x9000, 0x901f, pengo_sound_w, pengo_soundregs ),
                new MemoryWriteAddress( 0x9020, 0x902f, MWA_RAM, spriteram_2 ),
                new MemoryWriteAddress( 0x9040, 0x9040, interrupt_enable_w ),
                new MemoryWriteAddress( 0x9041, 0x9041, pengo_sound_enable_w ),
                new MemoryWriteAddress( 0x9042, 0x9042, MWA_NOP ),
                new MemoryWriteAddress( 0x9043, 0x9043, pengo_flipscreen_w ),
                new MemoryWriteAddress( 0x9044, 0x9046, MWA_NOP ),
                new MemoryWriteAddress( 0x9047, 0x9047, pengo_gfxbank_w ),
                new MemoryWriteAddress( 0x9070, 0x9070, MWA_NOP ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};

        static InputPortPtr input_ports= new InputPortPtr(){ public void handler() 
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
                /* the coin input must stay low for no less than 2 frames and no more */
                /* than 9 frames to pass the self test check. */
                /* Moreover, this way we avoid the game freezing until the user releases */
                /* the "coin" key. */
                PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_COIN1 | IPF_IMPULSE,
                                "Coin A", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 2 );
                PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_COIN2 | IPF_IMPULSE,
                                "Coin B", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 2 );
                /* Coin Aux doesn't need IMPULSE to pass the test, but it still needs it */
                /* to avoid the freeze. */
                PORT_BITX(0x40, IP_ACTIVE_LOW, IPT_COIN3 | IPF_IMPULSE,
                                "Coin Aux", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 2 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 );

                PORT_START();	/* IN1 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_SERVICE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );

                PORT_START();	/* DSW0 */
                PORT_DIPNAME( 0x01, 0x00, "Bonus Life", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "30000" );
                PORT_DIPSETTING(    0x01, "50000" );
                PORT_DIPNAME( 0x02, 0x00, "Demo Sounds", IP_KEY_NONE );
                PORT_DIPSETTING(    0x02, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x04, 0x00, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "Upright" );
                PORT_DIPSETTING(    0x04, "Cocktail" );
                PORT_DIPNAME( 0x18, 0x10, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x18, "2" );
                PORT_DIPSETTING(    0x10, "3" );
                PORT_DIPSETTING(    0x08, "4" );
                PORT_DIPSETTING(    0x00, "5" );
                PORT_BITX(    0x20, 0x20, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Rack Test", OSD_KEY_F1, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x20, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0xc0, 0x80, "Difficulty", IP_KEY_NONE );
                PORT_DIPSETTING(    0xc0, "Easy" );
                PORT_DIPSETTING(    0x80, "Medium" );
                PORT_DIPSETTING(    0x40, "Hard" );
                PORT_DIPSETTING(    0x00, "Hardest" );

                PORT_START();	/* DSW1 */
                PORT_DIPNAME( 0x0f, 0x0c, "A Coin/Cred", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "4/1" );
                PORT_DIPSETTING(    0x08, "3/1" );
                PORT_DIPSETTING(    0x04, "2/1" );
                PORT_DIPSETTING(    0x09, "2/1+Bonus each 5" );
                PORT_DIPSETTING(    0x05, "2/1+Bonus each 4" );
                PORT_DIPSETTING(    0x0c, "1/1" );
                PORT_DIPSETTING(    0x0d, "1/1+Bonus each 5" );
                PORT_DIPSETTING(    0x03, "1/1+Bonus each 4" );
                PORT_DIPSETTING(    0x0b, "1/1+Bonus each 2" );
                PORT_DIPSETTING(    0x02, "1/2" );
                PORT_DIPSETTING(    0x07, "1/2+Bonus each 5" );
                PORT_DIPSETTING(    0x0f, "1/2+Bonus each 4" );
                PORT_DIPSETTING(    0x0a, "1/3" );
                PORT_DIPSETTING(    0x06, "1/4" );
                PORT_DIPSETTING(    0x0e, "1/5" );
                PORT_DIPSETTING(    0x01, "1/6" );
                PORT_DIPNAME( 0xf0, 0xc0, "B Coin/Cred", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "4/1" );
                PORT_DIPSETTING(    0x80, "3/1" );
                PORT_DIPSETTING(    0x40, "2/1" );
                PORT_DIPSETTING(    0x90, "2/1+Bonus each 5" );
                PORT_DIPSETTING(    0x50, "2/1+Bonus each 4" );
                PORT_DIPSETTING(    0xc0, "1/1" );
                PORT_DIPSETTING(    0xd0, "1/1+Bonus each 5" );
                PORT_DIPSETTING(    0x30, "1/1+Bonus each 4" );
                PORT_DIPSETTING(    0xb0, "1/1+Bonus each 2" );
                PORT_DIPSETTING(    0x20, "1/2" );
                PORT_DIPSETTING(    0x70, "1/2+Bonus each 5" );
                PORT_DIPSETTING(    0xf0, "1/2+Bonus each 4" );
                PORT_DIPSETTING(    0xa0, "1/3" );
                PORT_DIPSETTING(    0x60, "1/4" );
                PORT_DIPSETTING(    0xe0, "1/5" );
                PORT_DIPSETTING(    0x10, "1/6" );
                INPUT_PORTS_END();
        }};



	public static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 }, /* characters are rotated 90 degrees */
		new int[] { 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3 },	/* bits are packed in groups of four */
		16*8	/* every char takes 16 bytes */
	);
	public static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		64,	/* 64 sprites */
		2,	/* 2 bits per pixel */
		new int[] { 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 39 * 8, 38 * 8, 37 * 8, 36 * 8, 35 * 8, 34 * 8, 33 * 8, 32 * 8,
				7 * 8, 6 * 8, 5 * 8, 4 * 8, 3 * 8, 2 * 8, 1 * 8, 0 * 8 },
		new int[] { 8*8, 8*8+1, 8*8+2, 8*8+3, 16*8+0, 16*8+1, 16*8+2, 16*8+3,
				24*8+0, 24*8+1, 24*8+2, 24*8+3, 0, 1, 2, 3 },
		64*8	/* every sprite takes 64 bytes */
	);



	public static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,      0, 32 ),	/* first bank */
		new GfxDecodeInfo( 1, 0x1000, spritelayout,    0, 32 ),
		new GfxDecodeInfo( 1, 0x2000, charlayout,   4*32, 32 ),	/* second bank */
		new GfxDecodeInfo( 1, 0x3000, spritelayout, 4*32, 32 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};



	static char color_prom[] =
	{
                /* palette */
                0x00,0xF6,0x07,0x38,0xC9,0xF8,0x3F,0xEF,0x6F,0x16,0x2F,0x7F,0xF0,0x36,0xDB,0xC6,
                0x00,0xF6,0xD8,0xF0,0xF8,0x16,0x07,0x2F,0x36,0x3F,0x7F,0x28,0x32,0x38,0xEF,0xC6,
                /* color lookup table (512x8, but only the first 256 bytes are used) */
                0x00,0x00,0x00,0x00,0x00,0x05,0x03,0x01,0x00,0x05,0x02,0x01,0x00,0x05,0x06,0x01,
                0x00,0x05,0x07,0x01,0x00,0x05,0x0A,0x01,0x00,0x05,0x0B,0x01,0x00,0x05,0x0C,0x01,
                0x00,0x05,0x0D,0x01,0x00,0x05,0x04,0x01,0x00,0x03,0x06,0x01,0x00,0x03,0x02,0x01,
                0x00,0x03,0x07,0x01,0x00,0x03,0x05,0x01,0x00,0x02,0x03,0x01,0x00,0x00,0x00,0x00,
                0x00,0x08,0x03,0x01,0x00,0x09,0x02,0x05,0x00,0x08,0x05,0x0D,0x04,0x04,0x04,0x04,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02,0x02,0x02,0x00,0x03,0x03,0x03,
                0x00,0x06,0x06,0x06,0x00,0x07,0x07,0x07,0x00,0x0A,0x0A,0x0A,0x00,0x0B,0x0B,0x0B,
                0x00,0x01,0x01,0x01,0x00,0x05,0x05,0x05,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,
                0x00,0x00,0x00,0x00,0x00,0x03,0x07,0x0D,0x00,0x0C,0x0F,0x0B,0x00,0x0C,0x0E,0x0B,
                0x00,0x0C,0x06,0x0B,0x00,0x0C,0x07,0x0B,0x00,0x0C,0x03,0x0B,0x00,0x0C,0x08,0x0B,
                0x00,0x0C,0x0D,0x0B,0x00,0x0C,0x04,0x0B,0x00,0x0C,0x09,0x0B,0x00,0x0C,0x05,0x0B,
                0x00,0x0C,0x02,0x0B,0x00,0x0C,0x0B,0x02,0x00,0x08,0x0C,0x02,0x00,0x08,0x0F,0x02,
                0x00,0x03,0x02,0x01,0x00,0x02,0x0F,0x03,0x00,0x0F,0x0E,0x02,0x00,0x0E,0x07,0x0F,
                0x00,0x07,0x06,0x0E,0x00,0x06,0x05,0x07,0x00,0x05,0x00,0x06,0x00,0x00,0x0B,0x05,
                0x00,0x0B,0x0C,0x00,0x00,0x0C,0x0D,0x0B,0x00,0x0D,0x08,0x0C,0x00,0x08,0x09,0x0D,
                0x00,0x09,0x0A,0x08,0x00,0x0A,0x01,0x09,0x00,0x01,0x04,0x0A,0x00,0x04,0x03,0x01
	};



	/* waveforms for the audio hardware */
	static char samples[] =
	{
		0x00,0x00,0x00,0x00,0x00,0x77,0x77,0x00,0x00,0x88,0x88,0x88,0x00,0x00,0x00,0x00,
		0x77,0x77,0x77,0x00,0x88,0x88,0x00,0x00,0x00,0x00,0x77,0x77,0x00,0x00,0x88,0x88,

		0xff,0x11,0x22,0x33,0xff,0x55,0x55,0xff,0x66,0xff,0x55,0x55,0xff,0x33,0x22,0x11,
		0xff,0xdd,0xff,0xbb,0xff,0x99,0xff,0x88,0xff,0x88,0xff,0x99,0xff,0xbb,0xff,0xdd,

		0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,
		0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,

		0x00,0x00,0x00,0x88,0x00,0x00,0x77,0x77,0x88,0x88,0x00,0x00,0x00,0x77,0x77,0x77,
		0x88,0x00,0x00,0x88,0x77,0x77,0x00,0x00,0x00,0x00,0x77,0x00,0x88,0x88,0x88,0x00,

		0xff,0x22,0x44,0x55,0x66,0x55,0x44,0x22,0xff,0xcc,0xaa,0x99,0x88,0x99,0xaa,0xcc,
		0xff,0x33,0x55,0x66,0x55,0x33,0xff,0xbb,0x99,0x88,0x99,0xbb,0xff,0x66,0xff,0x88,

		0xff,0x66,0x44,0x11,0x44,0x66,0x22,0xff,0x44,0x77,0x55,0x00,0x22,0x33,0xff,0xaa,
		0x00,0x55,0x11,0xcc,0xdd,0xff,0xaa,0x88,0xbb,0x00,0xdd,0x99,0xbb,0xee,0xbb,0x99,

		0xff,0x00,0x22,0x44,0x66,0x55,0x44,0x44,0x33,0x22,0x00,0xff,0xdd,0xee,0xff,0x00,
		0x00,0x11,0x22,0x33,0x11,0x00,0xee,0xdd,0xcc,0xcc,0xbb,0xaa,0xcc,0xee,0x00,0x11,

		0x22,0x44,0x44,0x22,0xff,0xff,0x00,0x33,0x55,0x66,0x55,0x22,0xee,0xdd,0xdd,0xff,
		0x11,0x11,0x00,0xcc,0x99,0x88,0x99,0xbb,0xee,0xff,0xff,0xcc,0xaa,0xaa,0xcc,0xff
	};



	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3020000,	/* The correct speed is 3.072 Mhz, but 3.020 gives a more */
						/* accurate emulation speed (time for two attract mode */
						/* cycles after power up, until the high score list appears */
						/* for the second time: 3'39") */
				0,
				readmem, writemem, null, null,
				interrupt, 1
			)
		},
		60,
		null,

		/* video hardware */
		28*8, 36*8, new rectangle( 0*8, 28*8-1, 0*8, 36*8-1 ),
		gfxdecodeinfo,
		32, 4 * 64,
		pengo_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,

		null,
		pengo_vh_start,
		generic_vh_stop,
		pengo_vh_screenrefresh,

		/* sound hardware */
		samples,
		null,
		null,
		null,
		pengo_sh_update
	);



	/***************************************************************************

	  Game driver(s)

	***************************************************************************/
        static RomLoadPtr pengo_rom = new RomLoadPtr(){ public void handler()  //using 0.27 loading ,crcs are probably wrong...
        {
		 ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "ic8",          0x0000, 0x1000, 0xf37066a8 );
                ROM_LOAD( "ic7",          0x1000, 0x1000, 0xbaf48143 );
                ROM_LOAD( "ic15",         0x2000, 0x1000, 0xadf0eba0 );
                ROM_LOAD( "ic14",         0x3000, 0x1000, 0xa086d60f );
                ROM_LOAD( "ic21",         0x4000, 0x1000, 0xb72084ec );
                ROM_LOAD( "ic20",         0x5000, 0x1000, 0x94194a89 );
                ROM_LOAD( "ic32",         0x6000, 0x1000, 0xaf7b12c4 );
                ROM_LOAD( "ic31",         0x7000, 0x1000, 0x933950fe );

		ROM_REGION(0x4000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "ic92",         0x0000, 0x2000, 0xd7eec6cd );
		ROM_LOAD( "ic105",        0x2000, 0x2000, 0x5bfd26e9 );
                ROM_END();
        }};

        static RomLoadPtr penta_rom = new RomLoadPtr(){ public void handler()  //using 0.27 loading ,crcs are probably wrong...
        {
		 ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "008_pn01.bin", 0x0000, 0x1000, 0x22f328df );
                ROM_LOAD( "007_pn05.bin", 0x1000, 0x1000, 0x15bbc7d3 );
                ROM_LOAD( "015_pn02.bin", 0x2000, 0x1000, 0xde82b74a );
                ROM_LOAD( "014_pn06.bin", 0x3000, 0x1000, 0x160f3836 );
                ROM_LOAD( "021_pn03.bin", 0x4000, 0x1000, 0x7824e3ef );
                ROM_LOAD( "020_pn07.bin", 0x5000, 0x1000, 0x377b9663 );
                ROM_LOAD( "032_pn04.bin", 0x6000, 0x1000, 0xbfde44c1 );
                ROM_LOAD( "031_pn08.bin", 0x7000, 0x1000, 0x64e8c30d );

		ROM_REGION(0x4000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "092_pn09.bin", 0x0000, 0x2000, 0x6afeba9d );
		ROM_LOAD( "ic105",        0x2000, 0x2000, 0x5bfd26e9 );
                ROM_END();
        }};
	/*pengoa rom doesn't exist in v0.36 romset so we don't support it
        static RomModule pengoa_rom[] = {
                ROM_LOAD( null, 0x10000, 0 ),	/* 64k for code */
   /*             ROM_LOAD( "pengo.u8",  0x0000, 0x1000, 0x63680136 ),
                ROM_LOAD( "pengo.u7",  0x1000, 0x1000, 0xe9ee4c30 ),
                ROM_LOAD( "pengo.u15", 0x2000, 0x1000, 0x13baf5dc ),
                ROM_LOAD( "pengo.u14", 0x3000, 0x1000, 0x5d563bbc ),
                ROM_LOAD( "pengo.u21", 0x4000, 0x1000, 0x6e1ee25e ),
                ROM_LOAD( "pengo.u20", 0x5000, 0x1000, 0x67866864 ),
                ROM_LOAD( "pengo.u32", 0x6000, 0x1000, 0x9938161a ),
                ROM_LOAD( "pengo.u31", 0x7000, 0x1000, 0x4f0eeb9e ),

                ROM_LOAD( null, 0x4000, 0 ),	/* temporary space for graphics (disposed after conversion) */
     /*           ROM_LOAD( "pengo.u92", 0x0000, 0x2000, 0x6865b315 ),
                ROM_LOAD( "pengo.105", 0x2000, 0x2000, 0xbb009a64 ),
        ROM_LOAD( null, 0, 0 )	};*/
	static char pengo_data_xortable[][] =
	{
		{ 0x28,0xa0,0x28,0xa0 },	/* ...0...0...0...0 */
		{ 0xa0,0x88,0x88,0xa0 },	/* ...0...0...0...1 */
		{ 0xa0,0x88,0x00,0x28 },	/* ...0...0...1...0 */
		{ 0xa0,0x88,0x88,0xa0 },	/* ...0...0...1...1 */
		{ 0x28,0xa0,0x28,0xa0 },	/* ...0...1...0...0 */
		{ 0x08,0x08,0xa8,0xa8 },	/* ...0...1...0...1 */
		{ 0xa0,0x88,0x00,0x28 },	/* ...0...1...1...0 */
		{ 0x00,0x00,0x00,0x00 },	/* ...0...1...1...1 */
		{ 0xa0,0x88,0x00,0x28 },	/* ...1...0...0...0 */
		{ 0x00,0x00,0x00,0x00 },	/* ...1...0...0...1 */
		{ 0x08,0x20,0xa8,0x80 },	/* ...1...0...1...0 */
		{ 0xa0,0x88,0x00,0x28 },	/* ...1...0...1...1 */
		{ 0x88,0x88,0x28,0x28 },	/* ...1...1...0...0 */
		{ 0x88,0x88,0x28,0x28 },	/* ...1...1...0...1 */
		{ 0x08,0x20,0xa8,0x80 },	/* ...1...1...1...0 */
		{ 0xa0,0x88,0x00,0x28 }		/* ...1...1...1...1 */
	};
	static char pengo_opcode_xortable[][] =
	{
		{ 0xa0,0x88,0x88,0xa0 },	/* ...0...0...0...0 */
		{ 0x28,0xa0,0x28,0xa0 },	/* ...0...0...0...1 */
		{ 0xa0,0x88,0x00,0x28 },	/* ...0...0...1...0 */
		{ 0x08,0x20,0xa8,0x80 },	/* ...0...0...1...1 */
		{ 0x08,0x08,0xa8,0xa8 },	/* ...0...1...0...0 */
		{ 0xa0,0x88,0x00,0x28 },	/* ...0...1...0...1 */
		{ 0xa0,0x88,0x00,0x28 },	/* ...0...1...1...0 */
		{ 0xa0,0x88,0x00,0x28 },	/* ...0...1...1...1 */
		{ 0x88,0x88,0x28,0x28 },	/* ...1...0...0...0 */
		{ 0x88,0x88,0x28,0x28 },	/* ...1...0...0...1 */
		{ 0x08,0x20,0xa8,0x80 },	/* ...1...0...1...0 */
		{ 0xa0,0x88,0x88,0xa0 },	/* ...1...0...1...1 */
		{ 0x08,0x08,0xa8,0xa8 },	/* ...1...1...0...0 */
		{ 0x00,0x00,0x00,0x00 },	/* ...1...1...0...1 */
		{ 0x08,0x20,0xa8,0x80 },	/* ...1...1...1...0 */
		{ 0x08,0x08,0xa8,0xa8 }		/* ...1...1...1...1 */
	};
         static DecodePtr pengo_decode  = new DecodePtr()
         {
             public void handler()
             {
                int A;


                for (A = 0x0000;A < 0x8000;A++)
                {
                        int i,j;
                        char src;


                        src = RAM[A];

                        /* pick the translation table from bits 0, 4, 8 and 12 of the address */
                        i = (A & 1) + (((A >> 4) & 1) << 1) + (((A >> 8) & 1) << 2) + (((A >> 12) & 1) << 3);

                        /* pick the offset in the table from bits 3 and 5 of the source data */
                        j = ((src >> 3) & 1) + (((src >> 5) & 1) << 1);
                        /* the bottom half of the translation table is the mirror image of the top */
                        if ((src & 0x80)!=0) j = 3 - j;

                        /* decode the ROM data */
                        RAM[A] = (char)(src ^ pengo_data_xortable[i][j]);

                        /* now decode the opcodes */
                        ROM[A] = (char)(src ^ pengo_opcode_xortable[i][j]);
                }
             }
        };

	static char penta_data_xortable[][] =
	{
		{ 0xa0,0x82,0x28,0x0a,0x82,0xa0,0x0a,0x28 },	/* ...............0 */
		{ 0x88,0x0a,0x82,0x00,0x88,0x0a,0x82,0x00 }		/* ...............1 */
	};
	static char penta_opcode_xortable[][] =
	{
		{ 0x02,0x08,0x2a,0x20,0x20,0x2a,0x08,0x02 },	/* ...0...0...0.... */
		{ 0x88,0x88,0x00,0x00,0x88,0x88,0x00,0x00 },	/* ...0...0...1.... */
		{ 0x88,0x0a,0x82,0x00,0xa0,0x22,0xaa,0x28 },	/* ...0...1...0.... */
		{ 0x88,0x0a,0x82,0x00,0xa0,0x22,0xaa,0x28 },	/* ...0...1...1.... */
		{ 0x2a,0x08,0x2a,0x08,0x8a,0xa8,0x8a,0xa8 },	/* ...1...0...0.... */
		{ 0x2a,0x08,0x2a,0x08,0x8a,0xa8,0x8a,0xa8 },	/* ...1...0...1.... */
		{ 0x88,0x0a,0x82,0x00,0xa0,0x22,0xaa,0x28 },	/* ...1...1...0.... */
		{ 0x88,0x0a,0x82,0x00,0xa0,0x22,0xaa,0x28 }		/* ...1...1...1.... */
	};
        static DecodePtr penta_decode  = new DecodePtr()
        {
             public void handler()
             {
                int A;


                for (A = 0x0000;A < 0x8000;A++)
                {
                        int i,j;
                        char src;


                        src = RAM[A];

                        /* pick the translation table from bit 0 of the address */
                        i = A & 1;

                        /* pick the offset in the table from bits 1, 3 and 5 of the source data */
                        j = ((src >> 1) & 1) + (((src >> 3) & 1) << 1) + (((src >> 5) & 1) << 2);
                        /* the bottom half of the translation table is the mirror image of the top */
                        if ((src & 0x80)!=0) j = 7 - j;

                        /* decode the ROM data */
                        RAM[A] = (char)(src ^ penta_data_xortable[i][j]);

                        /* now decode the opcodes */
                        /* pick the translation table from bits 4, 8 and 12 of the address */
                        i = ((A >> 4) & 1) + (((A >> 8) & 1) << 1) + (((A >> 12) & 1) << 2);
                        ROM[A] = (char)(src ^ penta_opcode_xortable[i][j]);
                }
             }
        };


	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
	{
		/* check if the hi score table has already been initialized */
	 /*TOFIX        	if (memcmp(RAM,0x8840,new char[] {0xd0,0x07},2) == 0 &&
			memcmp(RAM,0x8858,new char[]{0xd0,0x07},2) == 0 &&
			memcmp(RAM,0x880c,new char[]{0xd0,0x07},2) == 0)	/* high score */
 /*TOFIX        		{
			FILE f;


			if ((f = fopen(name, "rb")) != null)
			{
				fread(RAM, 0x8840, 1, 6*5, f);
				RAM[0x880c] = RAM[0x8858];
				RAM[0x880d] = RAM[0x8859];
				fclose(f);
			}

			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };


	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
	{
		FILE f;


		 /*TOFIX        if ((f = fopen(name, "wb")) != null)
		{
			fwrite(RAM, 0x8840, 1, 6*5, f);
			fclose(f);
		}*/
	} };



	public static GameDriver pengo_driver = new GameDriver
	(
                "Pengo",
		"pengo",
                "Allard van der Bas (original code)\nNicola Salmoria (MAME driver)\nSergio Munoz (color and sound info)",
		machine_driver,

		pengo_rom,
		null, pengo_decode,
		null,

		null/*TBR*/,input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

		color_prom, null, null,
		ORIENTATION_DEFAULT,

		hiload, hisave
	);

	public static GameDriver penta_driver = new GameDriver
	(
                "Penta",
		"penta",
                "Allard van der Bas (original code)\nNicola Salmoria (MAME driver)\nSergio Munoz (color and sound info)",
		machine_driver,

		penta_rom,
		null, penta_decode,
		null,

		null/*TBR*/,input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

		color_prom, null, null,
		ORIENTATION_DEFAULT,

		hiload, hisave
	);
        //we don't support pengoa since we don't have the romset
        /*public static GameDriver pengoa_driver = new GameDriver
	(
                "Pengo (alternate)",
                "pengoa",
                "ALLARD VAN DER BAS\nNICOLA SALMORIA\nSERGIO MUNOZ",

		machine_driver,

		penta_rom,
		null, null,
		null,

		input_ports, trak_ports, dsw, keys,

		color_prom, null, null,
		8*11, 8*20,

		hiload, hisave
	);*/

}

