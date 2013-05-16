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
 * roms are from 0.36romset
 *
 *   ccjap is cclimbrj in v0.36 romset
 */

package drivers;


import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.cclimber.*;
import static vidhrdw.generic.*;
import static vidhrdw.cclimber.*;
import static mame.memoryH.*;

public class cclimber
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x5fff, MRA_ROM ),	/* 5000-5fff: Crazy Kong only */
                new MemoryReadAddress( 0x6000, 0x6bff, MRA_RAM ),	/* Crazy Kong only */
                new MemoryReadAddress( 0x8000, 0x83ff, MRA_RAM ),	/* Crazy Climber only */
                new MemoryReadAddress( 0x9000, 0x93ff, MRA_RAM ),	/* video RAM */
                new MemoryReadAddress( 0x9800, 0x981f, MRA_RAM ),	/* column scroll registers */
                new MemoryReadAddress( 0x9880, 0x989f, MRA_RAM ),	/* sprite registers */
                new MemoryReadAddress( 0x98dc, 0x98df, MRA_RAM ),	/* bigsprite registers */
                new MemoryReadAddress( 0x9c00, 0x9fff, MRA_RAM ),	/* color RAM */
                new MemoryReadAddress( 0xa000, 0xa000, input_port_0_r ),	/* IN0 */
                new MemoryReadAddress( 0xa800, 0xa800, input_port_1_r ),	/* IN1 */
                new MemoryReadAddress( 0xb000, 0xb000, input_port_3_r ),	/* DSW */
                new MemoryReadAddress( 0xb800, 0xb800, input_port_2_r ),	/* IN2 */
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress writemem[] =
	{
                new MemoryWriteAddress( 0x0000, 0x5fff, MWA_ROM ),	/* 5000-5fff: Crazy Kong only */
                new MemoryWriteAddress( 0x6000, 0x6bff, MWA_RAM ),	/* Crazy Kong only */
                new MemoryWriteAddress( 0x8000, 0x83ff, MWA_RAM ),	/* Crazy Climber only */
                new MemoryWriteAddress( 0x8800, 0x88ff, cclimber_bigsprite_videoram_w, cclimber_bsvideoram, cclimber_bsvideoram_size ),
                new MemoryWriteAddress( 0x9000, 0x93ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x9400, 0x97ff, videoram_w ),	/* mirror address, used by Crazy Climber to draw windows */
                new MemoryWriteAddress( 0x9800, 0x981f, MWA_RAM, cclimber_column_scroll ),
                new MemoryWriteAddress( 0x9880, 0x989f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x98dc, 0x98df, MWA_RAM, cclimber_bigspriteram ),
                new MemoryWriteAddress( 0x9c00, 0x9fff, cclimber_colorram_w, colorram ),
                new MemoryWriteAddress( 0xa000, 0xa000, interrupt_enable_w ),
                new MemoryWriteAddress( 0xa001, 0xa002, cclimber_flipscreen_w ),
                new MemoryWriteAddress( 0xa004, 0xa004, cclimber_sample_trigger_w ),
                new MemoryWriteAddress( 0xa800, 0xa800, cclimber_sample_rate_w ),
                new MemoryWriteAddress( 0xb000, 0xb000, cclimber_sample_volume_w ),
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
		new IOWritePort( -1 )	/* end of table */
	};


        static InputPortPtr cclimber_input_ports= new InputPortPtr(){ public void handler()  
        {

                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_UP | IPF_4WAY );
                PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_DOWN | IPF_4WAY );
                PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_LEFT | IPF_4WAY );
                PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_RIGHT | IPF_4WAY );
                PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_UP | IPF_4WAY );
                PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_DOWN | IPF_4WAY );
                PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_LEFT | IPF_4WAY );
                PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_RIGHT | IPF_4WAY );

                PORT_START();	/* IN1 */
                PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_UP | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_DOWN | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_LEFT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICKLEFT_RIGHT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_UP | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_DOWN | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_LEFT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICKRIGHT_RIGHT | IPF_4WAY | IPF_COCKTAIL );

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_COIN1 );
                PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_COIN2 );
                PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
                PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
                PORT_DIPNAME( 0x10, 0x10, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x10, "Upright" );
                PORT_DIPSETTING(    0x00, "Cocktail" );
                PORT_BIT( 0xe0, IP_ACTIVE_HIGH, IPT_UNUSED );

                PORT_START();	/* DSW */
                PORT_DIPNAME( 0x03, 0x00, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "3" );
                PORT_DIPSETTING(    0x01, "4" );
                PORT_DIPSETTING(    0x02, "5" );
                PORT_DIPSETTING(    0x03, "6" );
                PORT_DIPNAME( 0x04, 0x00, "Bonus Life", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "30000" );
                PORT_DIPSETTING(    0x04, "50000" );
                PORT_BITX(    0x08, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Rack Test", OSD_KEY_F1, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x00, "Off" );
                PORT_DIPSETTING(    0x08, "On" );
                PORT_DIPNAME( 0x30, 0x00, "Coin A", IP_KEY_NONE );
                PORT_DIPSETTING(    0x30, "4 Coins/1 Credit" );
                PORT_DIPSETTING(    0x20, "3 Coins/1 Credit" );
                PORT_DIPSETTING(    0x10, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x00, "1 Coin/1 Credit" );
                PORT_DIPNAME( 0xc0, 0x00, "Coin B", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x40, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0x80, "1 Coin/3 Credits" );
                PORT_DIPSETTING(    0xc0, "Free Play" );
                INPUT_PORTS_END();
        }};

/* several differences with cclimber: note that IN2 bits are ACTIVE_LOW, while in */
/* cclimber they are ACTIVE_HIGH. */
        static InputPortPtr ckong_input_ports= new InputPortPtr(){ public void handler()  
        {

                PORT_START();	/* IN0 */
                PORT_BIT( 0x07, IP_ACTIVE_HIGH, IPT_UNUSED );
                PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON1 );
                PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_4WAY );
                PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_4WAY );
                PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_4WAY );
                PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY );

                PORT_START();	/* IN1 */
                PORT_BIT( 0x07, IP_ACTIVE_HIGH, IPT_UNUSED );
                PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
                PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* DSW */
                PORT_DIPNAME( 0x03, 0x00, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "3" );
                PORT_DIPSETTING(    0x01, "4" );
                PORT_DIPSETTING(    0x02, "5" );
                PORT_DIPSETTING(    0x03, "6" );
                PORT_DIPNAME( 0x0c, 0x00, "Bonus Life", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "7000" );
                PORT_DIPSETTING(    0x04, "10000" );
                PORT_DIPSETTING(    0x08, "15000" );
                PORT_DIPSETTING(    0x0c, "20000" );
                PORT_DIPNAME( 0x70, 0x00, "Coinage", IP_KEY_NONE );
                PORT_DIPSETTING(    0x70, "5 Coins/1 Credit" );
                PORT_DIPSETTING(    0x50, "4 Coins/1 Credit" );
                PORT_DIPSETTING(    0x30, "3 Coins/1 Credit" );
                PORT_DIPSETTING(    0x10, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x00, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x20, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0x40, "1 Coin/3 Credits" );
                PORT_DIPSETTING(    0x60, "1 Coin/4 Credits" );
                PORT_DIPNAME( 0x80, 0x80, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Upright" );
                PORT_DIPSETTING(    0x00, "Cocktail" );
                INPUT_PORTS_END();
        }};

	static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                512,	/* 512 characters (256 in Crazy Climber) */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 512*8*8 },	/* the two bitplanes are separated */
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },	/* pretty straightforward layout */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                8*8	/* every char takes 8 consecutive bytes */
	);
        static GfxLayout bscharlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                256,	/* 256 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 256*8*8 },	/* the two bitplanes are separated */
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },	/* pretty straightforward layout */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                128,	/* 128 sprites (64 in Crazy Climber) */
                2,	/* 2 bits per pixel */
                new int[] { 0, 128*16*16 },	/* the two bitplanes are separated */
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
                new GfxDecodeInfo( 1, 0x4000, bscharlayout, 4*16,  8 ),	/* big sprite char set */
                new GfxDecodeInfo( 1, 0x0000, spritelayout,    0, 16 ),	/* sprite set #1 */
                new GfxDecodeInfo( 1, 0x2000, spritelayout,    0, 16 ),	/* sprite set #2 */
		new GfxDecodeInfo( -1 ) /* end of array */
	};



        static char cclimber_color_prom[] =
        {
                /* char palette */
                0x00,0x79,0x04,0x87,0x00,0xb7,0xff,0x5f,0x00,0xc0,0xe8,0xf4,0x00,0x3f,0x04,0x38,
                0x00,0x0d,0x7a,0xb7,0x00,0x07,0x26,0x02,0x00,0x27,0x16,0x30,0x00,0xb7,0xf4,0x0c,
                0x00,0x4f,0xf6,0x24,0x00,0xb6,0xff,0x5f,0x00,0x33,0x00,0xb7,0x00,0x66,0x00,0x3a,
                0x00,0xc0,0x3f,0xb7,0x00,0x20,0xf4,0x16,0x00,0xff,0x7f,0x87,0x00,0xb6,0xf4,0xc0,
                /* bigsprite palette */
                0x00,0xff,0x18,0xc0,0x00,0xff,0xc6,0x8f,0x00,0x0f,0xff,0x1e,0x00,0xff,0xc0,0x67,
                0x00,0x47,0x7f,0x80,0x00,0x88,0x47,0x7f,0x00,0x7f,0x88,0x47,0x00,0x40,0x08,0xff
        };

        static char ckong_color_prom[] =
        {
                /* char palette */
                0x00,0x3F,0x16,0xFF,0x00,0xC0,0xFF,0xA7,0x00,0xC8,0xE8,0x3F,0x00,0x27,0x16,0x2F,
                0x00,0x1F,0x37,0xFF,0x00,0xD0,0xC0,0xE8,0x00,0x07,0x27,0xF6,0x00,0x2F,0xF7,0xA7,
                0x00,0x2F,0xC0,0x16,0x00,0x07,0x27,0xD0,0x00,0x17,0x27,0xE8,0x00,0x07,0x1F,0xFF,
                0x00,0xE8,0xD8,0x07,0x00,0x3D,0xFF,0xE8,0x00,0x07,0x3F,0xD2,0x00,0xFF,0xD0,0xE0,
                /* bigsprite palette */
                0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0x00,0x16,0x27,0x2F,0xff,0xff,0xff,0xff,
                0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0x00,0x40,0x08,0xFF
        /*	0x00,0xFF,0xC6,0x8F,0x00,0xFF,0xC6,0x8F,0x00,0xFF,0xC0,0x67,0x00,0xFF,0xC0,0x67, */
        /*	0x00,0x88,0x47,0x7F,0x00,0x88,0x47,0x7F,0x00,0x40,0x08,0xFF,0x00,0x40,0x08,0xFF, */
        };



	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				readmem, writemem, readport, writeport,
				nmi_interrupt, 1
			)
		},
		60,
		null,

		/* video hardware */
		32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
		gfxdecodeinfo,
		96,4*24,
		cclimber_vh_convert_color_prom,
                VIDEO_TYPE_RASTER,
		null,
		cclimber_vh_start,
		cclimber_vh_stop,
		cclimber_vh_screenrefresh,
 
		/* sound hardware */
		null,
		null,
		cclimber_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);



	/***************************************************************************

	  Game driver(s)

	***************************************************************************/
        static RomLoadPtr cclimber_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "cc11", 0x0000, 0x1000, 0xda9892bc );
                ROM_LOAD( "cc10", 0x1000, 0x1000, 0x154b349b );
                ROM_LOAD( "cc09", 0x2000, 0x1000, 0x6b5227fc );
                ROM_LOAD( "cc08", 0x3000, 0x1000, 0x4a92862c );
                ROM_LOAD( "cc07", 0x4000, 0x1000, 0xc6a5d53b );

                ROM_REGION(0x5000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "cc06", 0x0000, 0x0800, 0x8ceda6c7 );
                /* empy hole - Crazy Kong has an additional ROM here */
                ROM_LOAD( "cc04", 0x1000, 0x0800, 0xda323f5a );
                /* empy hole - Crazy Kong has an additional ROM here */
                ROM_LOAD( "cc05", 0x2000, 0x0800, 0xa26db1cf );
                /* empy hole - Crazy Kong has an additional ROM here */
                ROM_LOAD( "cc03", 0x3000, 0x0800, 0x8eb7e34d );
                /* empy hole - Crazy Kong has an additional ROM here */
                ROM_LOAD( "cc02", 0x4000, 0x0800, 0x25f097b6 );
                ROM_LOAD( "cc01", 0x4800, 0x0800, 0xb90b75dd );

                ROM_REGION(0x2000);	/* samples */
                ROM_LOAD( "cc13", 0x0000, 0x1000, 0xf33cfa4a );
                ROM_LOAD( "cc12", 0x1000, 0x1000, 0xe3e5f257 );
                ROM_END();
        }};

        static  char xortable[][] =
	{
		/* -1 marks spots which are unused and therefore unknown */
		{
			0x44,0x15,0x45,0x11,0x50,0x15,0x15,0x41,
			0x01,0x50,0x15,0x41,0x11,0x45,0x45,0x11,
			0x11,0x41,0x01,0x55,0x04,0x10,0x51,0x05,
			0x15,0x55,0x51,0x05,0x55,0x40,0x01,0x55,
			0x54,0x50,0x51,0x05,0x11,0x40,0x14,   (char)-1,
			0x54,0x10,0x40,0x51,0x05,0x54,0x14,   (char)-1,
			0x44,0x14,0x01,0x40,0x14,   (char)-1,0x41,0x50,
			0x50,0x41,0x41,0x45,0x14,   (char)-1,0x10,0x01
		},
		{
			0x44,0x11,0x04,0x50,0x11,0x50,0x41,0x05,
			0x10,0x50,0x54,0x01,0x54,0x44,   (char)-1,0x40,
			0x54,0x04,0x51,0x15,0x55,0x15,0x14,0x05,
			0x51,0x05,0x55,   (char)-1,0x50,0x50,0x40,0x54,
			   (char)-1,0x55,   (char)-1,   (char)-1,0x10,0x55,0x50,0x04,
			0x41,0x10,0x05,0x51,  (char) -1,0x55,0x51,0x54,
			0x01,0x51,0x11,0x45,0x44,0x10,0x14,0x40,
			0x55,0x15,0x41,0x15,0x45,0x10,0x44,0x41
		}
	};
	static DecodePtr cclimber_decode = new DecodePtr()
        {
            public void handler()
            {
                int A;


                for (A = 0x0000;A < 0x10000;A++)
                {
                        int i,j;
                        char src;


                        src = RAM[A];

                        /* pick the translation table from bit 0 of the address */
                        i = A & 1;

                        /* pick the offset in the table from bits 012467 of the source data */
                        j = (src & 0x07) + ((src & 0x10) >> 1) + ((src & 0xc0) >> 2);

                        /* decode the opcodes */
                        ROM[A] = (char)(src ^ xortable[i][j]);
                }

            }

        };
        static RomLoadPtr cclimbrj_rom= new RomLoadPtr(){ public void handler()  
        {
        	ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "cc11j.bin", 0x0000, 0x1000, 0x9ac39aa9 );
                ROM_LOAD( "cc10j.bin", 0x1000, 0x1000, 0x878c61ca );
                ROM_LOAD( "cc09j.bin", 0x2000, 0x1000, 0x32fdd4f5 );
                ROM_LOAD( "cc08j.bin", 0x3000, 0x1000, 0x398cbfc0 );
                ROM_LOAD( "cc07j.bin", 0x4000, 0x1000, 0xbe3cc484 );

                ROM_REGION(0x5000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "cc06", 0x0000, 0x0800, 0x8ceda6c7 );
                /* empy hole - Crazy Kong has an additional ROM here */
                ROM_LOAD( "cc04", 0x1000, 0x0800, 0xda323f5a );
                /* empy hole - Crazy Kong has an additional ROM here */
                ROM_LOAD( "cc05", 0x2000, 0x0800, 0xa26db1cf );
                /* empy hole - Crazy Kong has an additional ROM here */
                ROM_LOAD( "cc03", 0x3000, 0x0800, 0x8eb7e34d );
                /* empy hole - Crazy Kong has an additional ROM here */
                ROM_LOAD( "cc02", 0x4000, 0x0800, 0x25f097b6 );
                ROM_LOAD( "cc01", 0x4800, 0x0800, 0xb90b75dd );

                ROM_REGION(0x2000);	/* samples */
                ROM_LOAD( "cc13j.bin", 0x0000, 0x1000, 0x9f4339e5 );
                ROM_LOAD( "cc12j.bin", 0x1000, 0x1000, 0xe921f6f5 );
                ROM_END(); 
        }};
        static RomLoadPtr ccboot_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "m11.bin", 0x0000, 0x1000, 0xdd73b251 );
                ROM_LOAD( "m10.bin", 0x1000, 0x1000, 0x890ef772 );
                ROM_LOAD( "cc09j.bin", 0x2000, 0x1000, 0x32fdd4f5 );
                ROM_LOAD( "m08.bin", 0x3000, 0x1000, 0x068011fe );
                ROM_LOAD( "cc07j.bin", 0x4000, 0x1000, 0xbe3cc484 );

                ROM_REGION(0x5000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "cc06", 0x0000, 0x0800, 0x8ceda6c7 );
                /* empy hole - Crazy Kong has an additional ROM here */
                ROM_LOAD( "m04.bin", 0x1000, 0x0800, 0x91305aa8 );
                /* empy hole - Crazy Kong has an additional ROM here */
                ROM_LOAD( "m05.bin", 0x2000, 0x0800, 0xe883dff7 );
                /* empy hole - Crazy Kong has an additional ROM here */
                ROM_LOAD( "m03.bin", 0x3000, 0x0800, 0xd4cd8d75 );
                /* empy hole - Crazy Kong has an additional ROM here */
                ROM_LOAD( "m02.bin", 0x4000, 0x0800, 0x2bed2d1d );
                ROM_LOAD( "m01.bin", 0x4800, 0x0800, 0x82637d0f );

                ROM_REGION(0x2000);	/* samples */
                ROM_LOAD( "cc13j.bin", 0x0000, 0x1000, 0x9f4339e5 );
                ROM_LOAD( "cc12j.bin", 0x1000, 0x1000, 0xe921f6f5 );
                ROM_END();
        }};

	static  char xortable2[][] =
	{
		{
			0x41,0x55,0x44,0x10,0x55,0x11,0x04,0x55,
			0x15,0x01,0x51,0x45,0x15,0x40,0x10,0x01,
			0x04,0x50,0x55,0x01,0x44,0x15,0x15,0x10,
			0x45,0x11,0x55,0x41,0x50,0x10,0x55,0x10,
			0x14,0x40,0x05,0x54,0x05,0x41,0x04,0x55,
			0x14,0x41,0x01,0x51,0x45,0x50,0x40,0x01,
			0x51,0x01,0x05,0x10,0x10,0x50,0x54,0x41,
			0x40,0x51,0x14,0x50,0x01,0x50,0x15,0x40
		},
		{
			0x50,0x10,0x10,0x51,0x44,0x50,0x50,0x50,
			0x41,0x05,0x11,0x55,0x51,0x11,0x54,0x11,
			0x14,0x54,0x54,0x50,0x54,0x40,0x44,0x04,
			0x14,0x50,0x15,0x44,0x54,0x14,0x05,0x50,
			0x01,0x04,0x55,0x51,0x45,0x40,0x11,0x15,
			0x44,0x41,0x11,0x15,0x41,0x05,0x55,0x51,
			0x51,0x54,0x05,0x01,0x15,0x51,0x41,0x45,
			0x14,0x11,0x41,0x45,0x50,0x55,0x05,0x01
		}
	};

	static DecodePtr cclimbrj_decode = new DecodePtr()
        {
            public void handler()
            {
                int A;


                for (A = 0x0000;A < 0x10000;A++)
                {
                        int i,j;
                        char src;


                        src = RAM[A];

                        /* pick the translation table from bit 0 of the address */
                        i = A & 1;

                        /* pick the offset in the table from bits 012467 of the source data */
                        j = (src & 0x07) + ((src & 0x10) >> 1) + ((src & 0xc0) >> 2);

                        /* decode the opcodes */
                        ROM[A] = (char)(src ^ xortable2[i][j]);
                }
            }

        };
        static RomLoadPtr ckong_rom= new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "d05-07.bin",   0x0000, 0x1000, 0xb27df032 );
                ROM_LOAD( "f05-08.bin",   0x1000, 0x1000, 0x5dc1aaba );
                ROM_LOAD( "h05-09.bin",   0x2000, 0x1000, 0xc9054c94 );
                ROM_LOAD( "k05-10.bin",   0x3000, 0x1000, 0x069c4797 );
                ROM_LOAD( "l05-11.bin",   0x4000, 0x1000, 0xae159192 );
                ROM_LOAD( "n05-12.bin",   0x5000, 0x1000, 0x966bc9ab );

		ROM_REGION(0x5000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "n11-06.bin",   0x0000, 0x1000, 0x2dcedd12 );
                ROM_LOAD( "k11-04.bin",   0x1000, 0x1000, 0x3375b3bd );
                ROM_LOAD( "l11-05.bin",   0x2000, 0x1000, 0xfa7cbd91 );
                ROM_LOAD( "h11-03.bin",   0x3000, 0x1000, 0x5655cc11 );
                ROM_LOAD( "c11-02.bin",   0x4000, 0x0800, 0xd1352c31 );
                ROM_LOAD( "a11-01.bin",   0x4800, 0x0800, 0xa7a2fdbd );

		ROM_REGION(0x2000);	/* samples */
		ROM_LOAD( "cc13j.bin",    0x0000, 0x1000, 0x5f0bcdfb );
                ROM_LOAD( "cc12j.bin",    0x1000, 0x1000, 0x9003ffbd );
                ROM_END();
        }};
        static RomLoadPtr ckonga_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "d05-07.bin",   0x0000, 0x1000, 0xb27df032 );
                ROM_LOAD( "f05-08.bin",   0x1000, 0x1000, 0x5dc1aaba );
                ROM_LOAD( "h05-09.bin",   0x2000, 0x1000, 0xc9054c94 );
                ROM_LOAD( "10.dat",       0x3000, 0x1000, 0xc3beb501 );
                ROM_LOAD( "l05-11.bin",   0x4000, 0x1000, 0xae159192 );
                ROM_LOAD( "n05-12.bin",   0x5000, 0x1000, 0x966bc9ab );

                ROM_REGION(0x5000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "k11-04.bin",   0x1000, 0x1000, 0x3375b3bd );
                ROM_LOAD( "l11-05.bin",   0x2000, 0x1000, 0xfa7cbd91 );
                ROM_LOAD( "h11-03.bin",   0x3000, 0x1000, 0x5655cc11 );
                ROM_LOAD( "c11-02.bin",   0x4000, 0x0800, 0xd1352c31 );
                ROM_LOAD("a11-01.bin",   0x4800, 0x0800, 0xa7a2fdbd );

                ROM_REGION(0x2000);	/* samples */
                ROM_LOAD( "cc13j.bin",    0x0000, 0x1000, 0x5f0bcdfb );
                ROM_LOAD( "cc12j.bin",    0x1000, 0x1000, 0x9003ffbd );
                ROM_END();
        }};
        static RomLoadPtr ckongjeu_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */

                ROM_LOAD( "d05-07.bin",   0x0000, 0x1000, 0xb27df032 );
                ROM_LOAD( "f05-08.bin",   0x1000, 0x1000, 0x5dc1aaba );
                ROM_LOAD( "h05-09.bin",   0x2000, 0x1000, 0xc9054c94 );
                ROM_LOAD( "ckjeu10.dat",  0x3000, 0x1000, 0x7e6eeec4 );
                ROM_LOAD( "l05-11.bin",   0x4000, 0x1000, 0xae159192 );
                ROM_LOAD( "ckjeu12.dat",  0x5000, 0x1000, 0x0532f270 );


                ROM_REGION(0x5000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "n11-06.bin",   0x0000, 0x1000, 0x2dcedd12 );
                ROM_LOAD( "k11-04.bin",   0x1000, 0x1000, 0x3375b3bd );
                ROM_LOAD( "l11-05.bin",   0x2000, 0x1000, 0xfa7cbd91 );
                ROM_LOAD( "h11-03.bin",   0x3000, 0x1000, 0x5655cc11 );
                ROM_LOAD( "c11-02.bin",   0x4000, 0x0800, 0xd1352c31 );
                ROM_LOAD( "a11-01.bin",   0x4800, 0x0800, 0xa7a2fdbd );

                ROM_REGION(0x2000);	/* samples */
                ROM_LOAD( "cc13j.bin",    0x0000, 0x1000, 0x5f0bcdfb );
                ROM_LOAD( "cc12j.bin",    0x1000, 0x1000, 0x9003ffbd );
                ROM_END();
        }};




	static HiscoreLoadPtr cclimber_hiload = new HiscoreLoadPtr() { public int handler()
	{
		/* check if the hi score table has already been initialized */
	 /*TOFIX        	if (memcmp(RAM, 0x8083, new char[] { 0x02, 0x00, 0x00 }, 3) == 0 &&
				memcmp(RAM, 0x808f, new char[] { 0x02, 0x00, 0x00 }, 3) == 0)
		{
			FILE f;


			if ((f = fopen(name, "rb")) != null)
			{
				fread(RAM, 0x8083, 1, 17*5, f);
				fclose(f);
			}

			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };



	static HiscoreSavePtr cclimber_hisave = new HiscoreSavePtr() { public void handler()
	{
		FILE f;


	 /*TOFIX        	if ((f = fopen(name, "wb")) != null)
		{
			fwrite(RAM, 0x8083, 1, 17*5, f);
			fclose(f);
		}*/
	} };

	static HiscoreLoadPtr ckong_hiload = new HiscoreLoadPtr() { public int handler()
	{
		/* check if the hi score table has already been initialized */
 /*TOFIX        		if (memcmp(RAM, 0x611d, new char[] { 0x50, 0x76, 0x00 }, 3) == 0 &&
				memcmp(RAM, 0x61a5, new char[] { 0x00, 0x43, 0x00 }, 3) == 0)
		{
			FILE f;


			if ((f = fopen(name, "rb")) != null)
			{
				fread(RAM, 0x6100, 1, 34*5, f);
				RAM[0x60b8] = RAM[0x611d];
				RAM[0x60b9] = RAM[0x611e];
				RAM[0x60ba] = RAM[0x611f];
				fclose(f);
			}

			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };


	static HiscoreSavePtr ckong_hisave = new HiscoreSavePtr() { public void handler()
	{
		FILE f;


 /*TOFIX        		if ((f = fopen(name, "wb")) != null)
		{
			fwrite(RAM, 0x6100, 1, 34*5, f);
			fclose(f);
		}*/
	} };


        public static GameDriver cclimber_driver = new GameDriver
        (
                "Crazy Climber (US version)",
                "cclimber",
                "Lionel Theunissen (hardware info and ROM decryption)\nNicola Salmoria (MAME driver)",
                machine_driver,

                cclimber_rom,
                null, cclimber_decode,
                null,

                null/*TBR*/,cclimber_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                cclimber_color_prom, null, null,
                ORIENTATION_DEFAULT,

                cclimber_hiload, cclimber_hisave
        );

        public static GameDriver cclimbrj_driver = new GameDriver
        (
                "Crazy Climber (Japanese version)",
                "cclimbrj",
                "Lionel Theunissen (hardware info and ROM decryption)\nNicola Salmoria (MAME driver)",
                machine_driver,

                cclimbrj_rom,
                null, cclimbrj_decode,
                null,

                null/*TBR*/,cclimber_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                cclimber_color_prom, null, null,
                ORIENTATION_DEFAULT,

                cclimber_hiload, cclimber_hisave
        );

        public static GameDriver ccboot_driver = new GameDriver
        (
                "Crazy Climber (bootleg)",
                "ccboot",
                "Lionel Theunissen (hardware info and ROM decryption)\nNicola Salmoria (MAME driver)",
                machine_driver,

                ccboot_rom,
                null, cclimbrj_decode,
                null,

                null/*TBR*/,cclimber_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                cclimber_color_prom, null, null,
                ORIENTATION_DEFAULT,

                cclimber_hiload, cclimber_hisave
        );



        public static GameDriver ckong_driver = new GameDriver
        (
                "Crazy Kong (Crazy Climber hardware)",
                "ckong",
                "Nicola Salmoria (MAME driver)\nVille Laitinen (adaptation from Crazy Climber)\nDoug Jefferys (colors)",
                machine_driver,

                ckong_rom,
                null, null,
                null,

                null/*TBR*/,ckong_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                ckong_color_prom, null, null,
                ORIENTATION_ROTATE_270,

                ckong_hiload, ckong_hisave
        );

        public static GameDriver ckonga_driver = new GameDriver
        (
                "Crazy Kong (alternate version)",
                "ckonga",
                "Nicola Salmoria (MAME driver)\nVille Laitinen (adaptation from Crazy Climber)\nDoug Jefferys (colors)",
                machine_driver,

                ckonga_rom,
                null, null,
                null,

                null/*TBR*/,ckong_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                ckong_color_prom, null, null,
                ORIENTATION_ROTATE_270,

                ckong_hiload, ckong_hisave
        );

        public static GameDriver ckongjeu_driver = new GameDriver
        (
                "Crazy Kong (Jeutel bootleg)",
                "ckongjeu",
                "Nicola Salmoria (MAME driver)\nVille Laitinen (adaptation from Crazy Climber)\nDoug Jefferys (colors)",
                machine_driver,

                ckongjeu_rom,
                null, null,
                null,

                null/*TBR*/,ckong_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                ckong_color_prom, null, null,
                ORIENTATION_ROTATE_270,

                ckong_hiload, ckong_hisave
        );
}

