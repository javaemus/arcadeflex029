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
 */



package drivers;


import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static mame.inptport.*;
import static machine.seicross.*;
import static sndhrdw._8910intf.*;
import static vidhrdw.generic.*;
import static vidhrdw.cclimber.*;
import static vidhrdw.seicross.*;
import static sndhrdw.seicross.*;
import static mame.memoryH.*;
public class seicross
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x7814, 0x7815, seicross_protection_r ),	/* ??? */
                new MemoryReadAddress( 0x7800, 0x7fff, MRA_RAM ),
                new MemoryReadAddress( 0x0000, 0x6fff, MRA_ROM ),
                new MemoryReadAddress( 0x9000, 0x93ff, MRA_RAM ),	/* video RAM */
                new MemoryReadAddress( 0x9800, 0x981f, MRA_RAM ),
                new MemoryReadAddress( 0x9c00, 0x9fff, MRA_RAM ),	/* color RAM */
                new MemoryReadAddress( 0xa000, 0xa000, input_port_0_r ),	/* IN0 */
                new MemoryReadAddress( 0xa800, 0xa800, input_port_1_r ),	/* IN1 */
                new MemoryReadAddress( 0xb000, 0xb000, input_port_2_r ),	/* test */
                new MemoryReadAddress( 0xb800, 0xb800, MRA_NOP ),
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x6fff, MWA_ROM ),
                new MemoryWriteAddress( 0x7800, 0x7fff, MWA_RAM ),
                new MemoryWriteAddress( 0x9000, 0x93ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x9800, 0x981f, MWA_RAM, seicross_row_scroll ),
                new MemoryWriteAddress( 0x8820, 0x887f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x9c00, 0x9fff, seicross_colorram_w, colorram ),
                new MemoryWriteAddress( 0x9880, 0x989f, MWA_NOP ),	/* ? */
		new MemoryWriteAddress( -1 )	/* end of table */
	};

	static IOReadPort readport[] =
	{
		new IOReadPort( 0x04, 0x04, AY8910_read_port_0_r ),
		new IOReadPort( -1 )	/* end of table */
	};

	static IOWritePort writeport[] =
	{
		new IOWritePort( 0x00, 0x00, AY8910_control_port_0_w ),
		new IOWritePort( 0x01, 0x01, AY8910_write_port_0_w ),
		new IOWritePort( -1 )	/* end of table */
	};



	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0x00,
			new int[] { OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_LEFT, OSD_KEY_RIGHT,
				0, 0, OSD_KEY_1, OSD_KEY_2 }
		),
		new InputPort(	/* IN1 */
			0x00,/* standup cabinet */
			new int[] { 0, 0, 0, 0, OSD_KEY_3, 0, OSD_KEY_CONTROL, 0 }
		),
		new InputPort(	/* test */
			0x00,
			new int[]{ OSD_KEY_F1, OSD_KEY_F2, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* ? */
			0x60,	
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};

        static  TrakPort trak_ports[] =
        {
             new TrakPort(-1) 
        };


        static  KEYSet keys[] =
        {
                new KEYSet( 0, 0, "MOVE UP" ),
                new KEYSet( 0, 2, "MOVE LEFT" ),
                new KEYSet( 0, 3, "MOVE RIGHT" ),
                new KEYSet( 0, 1, "MOVE DOWN" ),
                new KEYSet( -1 )
        };

	static DSW dsw[] =
	{
		new DSW( 3, 0x40, "DEMO MODE", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 3, 0x20, "DEBUG MODE", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 3, 0x80, "SW7B", new String[]{ "0", "1" } ),
		new DSW( -1 )
	};



	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 4 },	/* the two bitplanes are packed in one byte */
		new int[] { 7*16, 6*16, 5*16, 4*16, 3*16, 2*16, 1*16, 0*16 },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3 },
		16*8	/* every char takes 16 consecutive bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		64,	/* 64 sprites */
		2,	/* 2 bits per pixel */
		new int[] { 0, 4 },	/* the two bitplanes are packed in one byte */
		new int[] { 23*16, 22*16, 21*16, 20*16, 19*16, 18*16, 17*16, 16*16,
			7*16, 6*16, 5*16, 4*16, 3*16, 2*16, 1*16, 0*16 },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3,
			16*8+0, 16*8+1, 16*8+2, 16*8+3, 17*8+0, 17*8+1, 17*8+2, 17*8+3 },
		64*8	/* every sprite takes 64 consecutive bytes */
	);



	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,   0, 16 ),
		new GfxDecodeInfo( 1, 0x1000, charlayout,   0, 16 ),
		new GfxDecodeInfo( 1, 0x1000, charlayout,   0, 16 ),
		new GfxDecodeInfo( 1, 0x2000, spritelayout, 0, 16 ),
		new GfxDecodeInfo( 1, 0x3000, spritelayout, 0, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};



	static char color_prom[] =
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



	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				readmem, writemem, readport, writeport,
				interrupt, 1
			)
		},
		60,
		null,

		/* video hardware */
		32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
		gfxdecodeinfo,
		96, 4*24,
		cclimber_vh_convert_color_prom,
                VIDEO_TYPE_RASTER,
		null,
		generic_vh_start,
		generic_vh_stop,
		seicross_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		seicross_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);



	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	static RomLoadPtr seicross_rom= new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD("smc1", 0x0000, 0x1000, 0xf6c3aeca );
		ROM_LOAD("smc2", 0x1000, 0x1000, 0x0ec6c218 );
		ROM_LOAD("smc3", 0x2000, 0x1000, 0xceb3c8f4 );
		ROM_LOAD("smc4", 0x3000, 0x1000, 0x3112af59 );
		ROM_LOAD("smc5", 0x4000, 0x1000, 0xb494a993 );
		ROM_LOAD("smc6", 0x5000, 0x1000, 0x09d5b9da );
		ROM_LOAD("smc7", 0x6000, 0x1000, 0x13052b03 );
                ROM_LOAD("smc8", 0x7000, 0x0800, 0x2093461d );

		ROM_REGION(0x4000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD("sz11.7k", 0x0000, 0x1000, 0xfbd9b91d );
		ROM_LOAD("smcd", 0x1000, 0x1000, 0xc3c953c4 );
		ROM_LOAD("sz9.7j", 0x2000, 0x1000, 0x4819f0cd );
		ROM_LOAD("sz10.7h", 0x3000, 0x1000, 0x4c268778 );
                ROM_END();
        }};

	public static GameDriver seicross_driver = new GameDriver
	(
                "Seicross",
		"seicross",
                "MIRKO BUFFONI\nNICOLA SALMORIA",
		machine_driver,
	
		seicross_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		color_prom, null, null,
		ORIENTATION_DEFAULT,
	
		null, null
	);
}
