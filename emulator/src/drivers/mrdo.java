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
 *  NOTES: romsets are from v0.36 roms
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
import static sndhrdw.ladybug.*;
import static machine.mrdo.*;
import static vidhrdw.generic.*;
import static vidhrdw.mrdo.*;
import static mame.memoryH.*;
public class mrdo
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0xe000, 0xefff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new MemoryReadAddress( 0x8000, 0x8fff, MRA_RAM ),	/* video and color RAM */
		new MemoryReadAddress( 0xa000, 0xa000, input_port_0_r ),	/* IN0 */
		new MemoryReadAddress( 0xa001, 0xa001, input_port_1_r ),	/* IN1 */
		new MemoryReadAddress( 0xa002, 0xa002, input_port_2_r ),	/* DSW1 */
		new MemoryReadAddress( 0xa003, 0xa003, input_port_3_r ),	/* DSW2 */
		new MemoryReadAddress( 0x9803, 0x9803, mrdo_SECRE_r ),
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0xe000, 0xefff, MWA_RAM ),
		new MemoryWriteAddress( 0x8000, 0x83ff, colorram_w, colorram ),
		new MemoryWriteAddress( 0x8400, 0x87ff, videoram_w, videoram,videoram_size ),
		new MemoryWriteAddress( 0x8800, 0x8bff, mrdo_colorram2_w, mrdo_colorram2 ),
		new MemoryWriteAddress( 0x8c00, 0x8fff, mrdo_videoram2_w, mrdo_videoram2 ),
		new MemoryWriteAddress( 0x9000, 0x90ff, MWA_RAM, spriteram,spriteram_size ),
		new MemoryWriteAddress( 0x9801, 0x9801, ladybug_sound1_w ),
		new MemoryWriteAddress( 0x9802, 0x9802, ladybug_sound2_w ),
		new MemoryWriteAddress( 0xf800, 0xffff, MWA_RAM, mrdo_scroll_x ),
		new MemoryWriteAddress( 0x9800, 0x9800, MWA_NOP ),
		new MemoryWriteAddress( 0xf000, 0xf7ff, MWA_NOP ),
		new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};



	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { OSD_KEY_LEFT, OSD_KEY_DOWN, OSD_KEY_RIGHT, OSD_KEY_UP,
				OSD_KEY_CONTROL, OSD_KEY_1, OSD_KEY_2, 0 }
		),
		new InputPort(	/* IN1 */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, OSD_KEY_3, 0 }
		),
		new InputPort(	/* DSW1 */
			0xdf,
			new int[] { 0, 0, OSD_KEY_F1, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW2 */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};



	static DSW dsw[] =
	{
		new DSW( 2, 0xc0, "LIVES", new String[] { "2", "5", "4", "3" }, 1 ),
		new DSW( 2, 0x03, "DIFFICULTY", new String[] { "HARDEST", "HARD", "MEDIUM", "EASY" }, 1 ),
		new DSW( 2, 0x10, "EXTRA", new String[] { "HARD", "EASY" }, 1 ),
		new DSW( 2, 0x08, "SPECIAL", new String[] { "HARD", "EASY" }, 1 ),
		new DSW( -1 )
	};

static TrakPort trak_ports[] =
{
        new TrakPort( -1 )
};


static KEYSet keys[] =
{
        new KEYSet( 0, 3, "MOVE UP" ),
        new KEYSet( 0, 0, "MOVE LEFT"  ),
        new KEYSet( 0, 2, "MOVE RIGHT" ),
        new KEYSet( 0, 1, "MOVE DOWN" ),
        new KEYSet( 0, 4, "FIRE"      ),
        new KEYSet( -1 )
};


	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		512,	/* 512 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 512*8*8 },	/* the two bitplanes are separated */
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		128,	/* 128 sprites */
		2,	/* 2 bits per pixel */
		new int[] { 4, 0 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 0*16, 2*16, 4*16, 6*16, 8*16, 10*16, 12*16, 14*16,
				16*16, 18*16, 20*16, 22*16, 24*16, 26*16, 28*16, 30*16 },
		new int[] { 24+0, 24+1, 24+2, 24+3, 16+0, 16+1, 16+2, 16+3,
				8+0, 8+1, 8+2, 8+3, 0, 1, 2, 3 },
		64*8	/* every sprite takes 64 consecutive bytes */
	);



	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,       0, 128 ),
		new GfxDecodeInfo( 1, 0x2000, charlayout,       0, 128 ),
		new GfxDecodeInfo( 1, 0x4000, spritelayout, 4*128,  16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};



	static char color_prom[] =
	{
		/* palette (high bits) */
		0x00,0x0C,0x03,0x00,0x0F,0x0B,0x0C,0x3F,0x0D,0x0F,0x0F,0x0C,0x0C,0x3C,0x0C,0x30,
		0x0C,0x03,0x30,0x03,0x0C,0x0F,0x00,0x3F,0x03,0x1E,0x00,0x0F,0x37,0x36,0x0D,0x33,
		/* palette (low bits) */
		0x00,0x0C,0x03,0x00,0x0C,0x03,0x00,0x3F,0x0F,0x03,0x0F,0x3F,0x0C,0x0F,0x0F,0x3A,
		0x03,0x0F,0x00,0x0C,0x00,0x0F,0x3F,0x03,0x2A,0x0C,0x00,0x0A,0x0C,0x0E,0x3F,0x0F,
		/* sprite color lookup table */
		0x00,0x97,0x71,0xF9,0x00,0x27,0xA5,0x13,0x00,0x32,0x77,0x3F,0x00,0xA7,0x72,0xF9,
		0x00,0x1F,0x9A,0x77,0x00,0x15,0x27,0x38,0x00,0xC2,0x55,0x69,0x00,0x7F,0x76,0x7A
	};



	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				4000000,	/* 4 Mhz */
				0,
				readmem, writemem, null, null,
				interrupt, 1
			)
		},
		60,
		null,

		/* video hardware */
		32*8, 32*8, new rectangle( 4*8, 28*8-1, 1*8, 31*8-1 ),
		gfxdecodeinfo,
		256, 4 * 144,
		mrdo_vh_convert_color_prom,
                VIDEO_TYPE_RASTER,
		null,
		mrdo_vh_start,
		mrdo_vh_stop,
		mrdo_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		ladybug_sh_start,
		ladybug_sh_stop,
		ladybug_sh_update
	);



	/***************************************************************************

	  Game driver(s)

	***************************************************************************/
        static RomLoadPtr mrdo_rom= new RomLoadPtr(){ public void handler()  
        {

		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD("a4-01.bin", 0x0000, 0x2000, 0x03dcfba2 );
		ROM_LOAD("c4-02.bin", 0x2000, 0x2000, 0x0ecdd39c );
		ROM_LOAD("e4-03.bin", 0x4000, 0x2000, 0x358f5dc2 );
		ROM_LOAD("f4-04.bin", 0x6000, 0x2000, 0xf4190cfc );

		ROM_REGION(0x6000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD("s8-09.bin", 0x0000, 0x1000, 0xaa80c5b6 );
		ROM_LOAD("u8-10.bin", 0x1000, 0x1000, 0xd20ec85b );
		ROM_LOAD("r8-08.bin", 0x2000, 0x1000, 0xdbdc9ffa );
		ROM_LOAD("n8-07.bin", 0x3000, 0x1000, 0x4b9973db );
		ROM_LOAD("h5-05.bin", 0x4000, 0x1000, 0xe1218cc5 );
		ROM_LOAD("k5-06.bin", 0x5000, 0x1000, 0xb1f68b04 );
                ROM_END();
        }};
        static RomLoadPtr mrdot_rom= new RomLoadPtr(){ public void handler()  
        {

		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD("D1",  0x0000, 0x2000, 0x3dcd9359 );
		ROM_LOAD("D2",  0x2000, 0x2000, 0x710058d8 );
		ROM_LOAD("D3",  0x4000, 0x2000, 0x467d12d8 );
		ROM_LOAD("D4",  0x6000, 0x2000, 0xfce9afeb );

		ROM_REGION(0x6000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD("D9",  0x0000, 0x1000, 0xde4cfe66 );
		ROM_LOAD("D10", 0x1000, 0x1000, 0xa6c2f38b );
		ROM_LOAD("r8-08.bin",  0x2000, 0x1000, 0xdbdc9ffa );
		ROM_LOAD("n8-07.bin",  0x3000, 0x1000, 0x4b9973db );
		ROM_LOAD("h5-05.bin",  0x4000, 0x1000, 0xe1218cc5 );
		ROM_LOAD("k5-06.bin",  0x5000, 0x1000, 0xb1f68b04 );
                ROM_END();
        }};
        static RomLoadPtr mrlo_rom= new RomLoadPtr(){ public void handler()  
        { 

		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD("mrlo01.bin",   0x0000, 0x2000, 0x6f455e7d );
		ROM_LOAD("d2",           0x2000, 0x2000, 0x710058d8 );
		ROM_LOAD("dofix.d3",     0x4000, 0x2000, 0x3a7d039b );
		ROM_LOAD("mrlo04.bin",   0x6000, 0x2000, 0x49c10274 );

		ROM_REGION(0x6000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD("mrlo09.bin",   0x0000, 0x1000, 0xfdb60d0d );
		ROM_LOAD("mrlo10.bin",   0x1000, 0x1000, 0x0492c10e );
		ROM_LOAD("r8-08.bin", 0x2000, 0x1000, 0xdbdc9ffa );
		ROM_LOAD("n8-07.bin", 0x3000, 0x1000, 0x4b9973db  );
		ROM_LOAD("h5-05.bin", 0x4000, 0x1000, 0xe1218cc5 );
		ROM_LOAD("k5-06.bin", 0x5000, 0x1000, 0xb1f68b04 );
                ROM_END();
        }};

	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
	{
		/* check if the hi score table has already been initialized */
	 /*TOFIX        	if (memcmp(RAM, 0xe017, new char[] { 0x01, 0x00, 0x00 }, 3) == 0 &&
				memcmp(RAM, 0xe071, new char[] { 0x01, 0x00, 0x00 }, 3) == 0)
		{
			FILE f;


			if ((f = fopen(name, "rb")) != null)
			{
				fread(RAM, 0xe017, 1, 10*10+2, f);
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
			fwrite(RAM, 0xe017, 1, 10*10+2, f);
			fclose(f);
		}*/
	} };



	public static GameDriver mrdo_driver = new GameDriver
	(
                "Mr. Do! (Universal)",
		"mrdo",
                "NICOLA SALMORIA\nPAUL SWAN",
		machine_driver,

		mrdo_rom,
		null, null,
		null,

		input_ports,null, trak_ports, dsw, keys,

		color_prom, null, null,
		ORIENTATION_DEFAULT,

		hiload, hisave
	);

	public static GameDriver mrdot_driver = new GameDriver
	(
                "Mr. Do! (Taito)",
		"mrdot",
                "NICOLA SALMORIA\nPAUL SWAN",
		machine_driver,

		mrdot_rom,
		null, null,
		null,

		input_ports,null, trak_ports, dsw, keys,

		color_prom, null, null,
		ORIENTATION_DEFAULT,

		hiload, hisave
	);

	public static GameDriver mrlo_driver = new GameDriver
	(
                "Mr. Lo!",
		"mrlo",
                "NICOLA SALMORIA\nPAUL SWAN",
		machine_driver,

		mrlo_rom,
		null, null,
		null,

		input_ports,null, trak_ports, dsw, keys,

		color_prom, null, null,
		ORIENTATION_DEFAULT,

		hiload, hisave
	);
}
