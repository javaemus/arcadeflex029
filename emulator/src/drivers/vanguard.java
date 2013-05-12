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
 * roms are from 0.36 romset
 *
 */

package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.inptport.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static machine.vanguard.*;
import static vidhrdw.generic.*;
import static vidhrdw.vanguard.*;
import static sndhrdw.vanguard.*;

public class vanguard
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x1fff, MRA_RAM ),
		new MemoryReadAddress( 0x4000, 0xbfff, MRA_ROM ),
		new MemoryReadAddress( 0xf000, 0xffff, MRA_ROM ),	/* for the reset / interrupt vectors */
		new MemoryReadAddress( 0x3104, 0x3104, input_port_0_r ),	/* IN0 */
		new MemoryReadAddress( 0x3105, 0x3105, input_port_1_r ),	/* IN1 */
		new MemoryReadAddress( 0x3106, 0x3106, input_port_2_r ),	/* DSW ?? */
		new MemoryReadAddress( 0x3107, 0x3107, input_port_3_r ),	/* IN2 */
                new MemoryReadAddress( 0xf000, 0xffff, MRA_ROM ),	/* for the reset / interrupt vectors */
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress writemem[] =
	{
            new MemoryWriteAddress( 0x0000, 0x03ff, MWA_RAM ),
            new MemoryWriteAddress( 0x0400, 0x07ff, MWA_RAM, vanguard_videoram2 ),
            new MemoryWriteAddress( 0x0800, 0x0bff, videoram_w, videoram, videoram_size ),
            new MemoryWriteAddress( 0x0c00, 0x0fff, colorram_w, colorram ),
            new MemoryWriteAddress( 0x1000, 0x1fff, vanguard_characterram_w, vanguard_characterram ),
            new MemoryWriteAddress( 0x3300, 0x3300, MWA_RAM, vanguard_scrollx ),
            new MemoryWriteAddress( 0x3200, 0x3200, MWA_RAM, vanguard_scrolly ),
            new MemoryWriteAddress( 0x3100, 0x3100, vanguard_sound0_w ),
            new MemoryWriteAddress( 0x3101, 0x3101, vanguard_sound1_w ),
            new MemoryWriteAddress( 0x4000, 0xbfff, MWA_ROM ),
            new MemoryWriteAddress( -1 )	/* end of table */

	};



	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0x00,
			new int[] { OSD_KEY_D, OSD_KEY_E, OSD_KEY_F, OSD_KEY_S,
					OSD_KEY_DOWN, OSD_KEY_UP, OSD_KEY_RIGHT, OSD_KEY_LEFT }
		),
		new InputPort(	/* IN1 */
			0x00,
			new int[] { OSD_KEY_D, OSD_KEY_E, OSD_KEY_F, OSD_KEY_S,
					OSD_KEY_DOWN, OSD_KEY_UP, OSD_KEY_RIGHT, OSD_KEY_LEFT }
		),
		new InputPort(	/* DSW ?? */
			0x00,
			new int[] { 0, 0, 0, 0, OSD_KEY_F1, 0, 0, 0 }
		),
		new InputPort(	/* IN2 */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, OSD_KEY_2, OSD_KEY_1 }
		),
		new InputPort( -1 )	/* end of table */
	};



	static DSW dsw[] =
	{
		new DSW( 2, 0x30, "LIVES", new String[] { "3", "4", "5", "3" } ),
		new DSW( 2, 0x40, "COINS PER CREDIT", new String[] { "1", "2" } ),
		new DSW( -1 )
	};

static TrakPort trak_ports[] =
{
        new TrakPort(-1)
};


static KEYSet keys[] =
{
        new KEYSet( 0, 5, "MOVE UP" ),
         new KEYSet( 0, 7, "MOVE LEFT"  ),
         new KEYSet(  0, 6, "MOVE RIGHT" ),
         new KEYSet(  0, 4, "MOVE DOWN" ),
         new KEYSet(  0, 1, "FIRE UP" ),
         new KEYSet(  0, 3, "FIRE LEFT"  ),
         new KEYSet(  0, 2, "FIRE RIGHT" ),
         new KEYSet(  0, 0, "FIRE DOWN" ),
         new KEYSet(  -1 )
};


	public static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 256*8*8 }, /* the two bitplanes are separated */
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		8*8	/* every char takes 8 consecutive bytes */
	);

	static GfxLayout charlayout2 = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 256*8*8 },	/* the two bitplanes are separated */
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		8*8	/* every char takes 8 consecutive bytes */
	);



	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 0, 0xf000, charlayout,    0, 8 ),	/* the game dynamically modifies this */
		new GfxDecodeInfo( 1, 0x0000, charlayout2, 8*4, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};



static char color_prom[] =
{
 	/* foreground colors */
	0x00,0x2F,0xF4,0xFF,0xEF,0xF8,0xFF,0x07,0xFE,0xC0,0x07,0x3F,0xFF,0x3F,0xC6,0xC0,
	0x00,0x38,0xE7,0x07,0xEF,0xC0,0xF4,0xFF,0xFE,0xFF,0xF8,0xC0,0xFF,0xC6,0xE7,0xC0,
	/* background colors */
	0x00,0x80,0x3F,0xC6,0xEF,0xC6,0x2F,0xF8,0xFE,0xC6,0xE7,0xC0,0xFF,0x2F,0x38,0xC6,
	0x00,0x07,0x80,0x2F,0xEF,0x07,0xF8,0xFF,0xFE,0xFF,0xF8,0xC0,0xFF,0xE7,0xC6,0xF4
};

static  char samples[] =
{
   0x88, 0x88, 0x88, 0x88, 0xaa, 0xaa, 0xaa, 0xaa,
   0xcc, 0xcc, 0xcc, 0xcc, 0xee, 0xee, 0xee, 0xee,
   0x11, 0x11, 0x11, 0x11, 0x22, 0x22, 0x22, 0x22,
   0x44, 0x44, 0x44, 0x44, 0x66, 0x66, 0x66, 0x66
};


	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_M6502,
				1000000,	/* 1 Mhz ???? */
				0,
				readmem, writemem, null, null,
				vanguard_interrupt,2
			)
		},
		60,
		null,

		/* video hardware */
		32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
		gfxdecodeinfo,
		16*4,16*4,
	        vanguard_vh_convert_color_prom,
                VIDEO_TYPE_RASTER,
		null,
		generic_vh_start,
		generic_vh_stop,
		vanguard_vh_screenrefresh,

		/* sound hardware */
		samples,
		null,
		vanguard_sh_start,
		null,
		vanguard_sh_update
	);

static String vanguard_sample_names[]=
{
        "fire.sam",
        "explsion.sam",
        null	/* end of array */
};

	/***************************************************************************

	  Game driver(s)

	***************************************************************************/
        static RomLoadPtr vanguard_rom= new RomLoadPtr(){ public void handler()  
        {
            ROM_REGION(0x10000);	/* 64k for code */

            ROM_LOAD( "sk4_ic07.bin", 0x4000, 0x1000, 0x8dd6cf9a );
            ROM_LOAD( "sk4_ic08.bin", 0x5000, 0x1000, 0xa3740a46 );
            ROM_LOAD( "sk4_ic09.bin", 0x6000, 0x1000, 0x27c429cc );
            ROM_LOAD( "sk4_ic10.bin", 0x7000, 0x1000, 0x1e153237 );
            ROM_LOAD( "sk4_ic13.bin", 0x8000, 0x1000, 0x32820c48 );
            ROM_RELOAD(0xf000, 0x1000 );	/* for the reset and interrupt vectors */
            ROM_LOAD( "sk4_ic14.bin", 0x9000, 0x1000, 0x4b34aaea );
            ROM_LOAD( "sk4_ic15.bin", 0xa000, 0x1000, 0x64432d1d );
            ROM_LOAD( "sk4_ic16.bin", 0xb000, 0x1000, 0xb4d9810f );

            ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
            ROM_LOAD( "sk5_ic50.bin", 0x0000, 0x0800, 0x6a6bc3f7 );
            ROM_LOAD( "sk5_ic51.bin", 0x0800, 0x0800, 0x7eb71bd1 );

            ROM_REGION(0x1000);	/* space for the sound ROMs */
            ROM_LOAD( "sk4_ic51.bin", 0x0000, 0x0800, 0x7f305f4c );  /* sound ROM 1 */
            ROM_LOAD( "sk4_ic52.bin", 0x0800, 0x0800, 0xe6fb89fb );  /* sound ROM 2 */
            ROM_END();
        }};



	public static GameDriver vanguard_driver = new GameDriver
	(
                "Vanguard",
		"vanguard",
                 "BRIAN LEVINE\nBRAD OLIVER\nMIRKO BUFFONI\nANDREW SCOTT",
		machine_driver,

		vanguard_rom,
		null, null,
		vanguard_sample_names,

		input_ports,null, trak_ports, dsw, keys,

		color_prom, null, null,
		ORIENTATION_DEFAULT,

		null, null
	);
}

