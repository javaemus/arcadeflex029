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
import static vidhrdw.nibbler.*;
import static machine.vanguard.*;
import static vidhrdw.vanguard.*;
import static sndhrdw.vanguard.*;

public class nibbler
{

	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x1fff, MRA_RAM ),
		new MemoryReadAddress( 0x3000, 0xbfff, MRA_ROM ),
		new MemoryReadAddress( 0xfffa, 0xffff, MRA_ROM ),
		new MemoryReadAddress( 0x2104, 0x2104, input_port_0_r ),	/* IN0 */
		new MemoryReadAddress( 0x2105, 0x2105, input_port_1_r ),	/* IN1 */
		new MemoryReadAddress( 0x2106, 0x2106, input_port_2_r ),	/* DSW */
		new MemoryReadAddress( 0x2107, 0x2107, input_port_3_r ),	/* IN2 */
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x03ff, MWA_RAM ),
		new MemoryWriteAddress( 0x0400, 0x07ff, videoram_w, videoram,videoram_size ),
		new MemoryWriteAddress( 0x0800, 0x0bff, nibbler_videoram2_w, nibbler_videoram2 ),
		new MemoryWriteAddress( 0x0c00, 0x0fff, colorram_w, colorram ),
		new MemoryWriteAddress( 0x1000, 0x1fff, nibbler_characterram_w, nibbler_characterram ),
                new MemoryWriteAddress( 0x2100, 0x2100, vanguard_sound0_w ),
                new MemoryWriteAddress( 0x2101, 0x2101, vanguard_sound1_w ),
		new MemoryWriteAddress( 0x3000, 0xbfff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	
	
	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0x00,
			new int[] { 0, 0, 0, 0, OSD_KEY_DOWN, OSD_KEY_UP, OSD_KEY_RIGHT, OSD_KEY_LEFT }
		),
		new InputPort(	/* IN1 */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW */
			0x00,
			new int[] { 0, 0, 0, 0, OSD_KEY_F1, 0, 0, 0 }
		),
		new InputPort(	/* IN2 */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, OSD_KEY_2, OSD_KEY_1 }
		),
		new InputPort( -1 )	/* end of table */
	};
	
static TrakPort trak_ports[] =
{
         new TrakPort(-1)
};


static KEYSet keys[] =
{
         new KEYSet( 0, 5, "MOVE UP" ),
         new KEYSet( 0, 7, "MOVE LEFT"  ),
         new KEYSet( 0, 6, "MOVE RIGHT" ),
         new KEYSet( 0, 4, "MOVE DOWN" ),
         new KEYSet( -1 )
};
	
	static DSW dsw[] =
	{
		new DSW( 2, 0x03, "LIVES", new String[] { "3", "4", "5", "6" } ),
		new DSW( 2, 0x04, "DIFFICULTY", new String[] { "EASY", "HARD" } ),
		new DSW( -1 )
	};
	
	
	
	public static GfxLayout charlayout= new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 256*8*8 },	/* the two bitplanes are separated */
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout charlayout2 = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 512*8*8 },	/* the two bitplanes are separated */
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 0, 0xf000, charlayout, 0, 8 ),	/* the game dynamically modifies this */
		new GfxDecodeInfo( 1, 0x0800, charlayout2,   8*4, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static char color_prom[] =
        {
            /* foreground colors */
            0x00,0x07,0xff,0xC5,0x00,0x38,0xad,0xA8,0x00,0xad,0x3f,0xC0,0x00,0xff,0x07,0xFF,
            0x00,0x3f,0xc0,0xAD,0x00,0xff,0xc5,0x3F,0x00,0xff,0x3f,0x07,0x00,0x07,0xc5,0x3F,
            /* background colors */
            0x00,0x3f,0xff,0xC0,0x00,0xc7,0x38,0x05,0x00,0x07,0xc0,0x3F,0x00,0x3f,0xe0,0x05,
            0x00,0x07,0xac,0xC0,0x00,0xff,0xc5,0x2F,0x00,0xc0,0x05,0x2F,0x00,0x3f,0x05,0xC7
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
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
		null,
		generic_vh_start,
		generic_vh_stop,
		nibbler_vh_screenrefresh,

		/* sound hardware */
		samples,
		null,
		vanguard_sh_start,
		null,
		vanguard_sh_update
	);
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	static RomLoadPtr nibbler_rom = new RomLoadPtr(){ public void handler()  
        {
            
            ROM_REGION(0x10000);	/* 64k for code */

            ROM_LOAD( "g960-52.12",   0x3000, 0x1000, 0xac6a802b );
            ROM_LOAD( "g960-48.07",   0x4000, 0x1000, 0x35971364 );
            ROM_LOAD( "g960-49.08",   0x5000, 0x1000, 0x6b33b806 );
            ROM_LOAD( "g960-50.09",   0x6000, 0x1000, 0x91a4f98d );
            ROM_LOAD( "g960-51.10",   0x7000, 0x1000, 0xa151d934 );
            ROM_LOAD( "g960-53.14",   0x8000, 0x1000, 0x063f05cc );
            ROM_RELOAD(0xf000, 0x1000 );	/* for the reset and interrupt vectors */
            ROM_LOAD( "g960-54.15",   0x9000, 0x1000, 0x7205fb8d );
            ROM_LOAD( "g960-55.16",   0xa000, 0x1000, 0x4bb39815 );
            ROM_LOAD( "g960-56.17",   0xb000, 0x1000, 0xed680f19 );

            ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
            ROM_LOAD( "g960-57.50",   0x0000, 0x1000, 0x01d4d0c2 );
            ROM_LOAD( "g960-58.51",   0x1000, 0x1000, 0xfeff7faf );

            ROM_REGION( 0x1800);	/* sound ROMs */
            ROM_LOAD( "g959-43.51",   0x0000, 0x0800, 0x0345f8b7 );
            ROM_LOAD( "g959-44.52",   0x0800, 0x0800, 0x87d67dee );
            ROM_LOAD( "g959-45.53",   0x1000, 0x0800, 0x33189917 );
            ROM_END();
        }};
	static RomLoadPtr fantasy_rom = new RomLoadPtr(){ public void handler()  
        {

             ROM_REGION(0x10000);	/* 64k for code */
            ROM_LOAD( "ic12.cpu",        0x3000, 0x1000, 0x22cb2249 );
            ROM_LOAD( "ic07.cpu",        0x4000, 0x1000, 0x0e2880b6 );
            ROM_LOAD( "ic08.cpu",        0x5000, 0x1000, 0x4c331317 );
            ROM_LOAD( "ic09.cpu",        0x6000, 0x1000, 0x6ac1dbfc );
            ROM_LOAD( "ic10.cpu",        0x7000, 0x1000, 0xc796a406 );
            ROM_LOAD( "ic14.cpu",        0x8000, 0x1000, 0x6f1f0698 );
            ROM_RELOAD(0xf000, 0x1000 );	/* for the reset and interrupt vectors */
            ROM_LOAD( "ic15.cpu",        0x9000, 0x1000, 0x5534d57e );
            ROM_LOAD( "ic16.cpu",        0xa000, 0x1000, 0x6c2aeb6e );
            ROM_LOAD( "ic17.cpu",        0xb000, 0x1000, 0xf6aa5de1 );

             ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
             ROM_LOAD( "fs10ic50.bin",    0x0000, 0x1000, 0x86a801c3 );
             ROM_LOAD( "fs11ic51.bin",    0x1000, 0x1000, 0x9dfff71c );

             ROM_REGION( 0x1800);	/* sound ROMs */
             ROM_LOAD( "fs_b_51.bin",     0x0000, 0x0800, 0x48094ec5 );
             ROM_LOAD( "fs_a_52.bin",     0x0800, 0x0800, 0x1d0316e8 );
             ROM_LOAD( "fs_c_53.bin",     0x1000, 0x0800, 0x49fd4ae8 );

            ROM_END();
        }};



	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
		/* check if the hi score table has already been initialized */
		if (memcmp(RAM, 0x0290, new char[] { 0x00, 0x50, 0x00, 0x00 }, 4) == 0 &&
				memcmp(RAM, 0x02b4, new char[] { 0x00, 0x05, 0x00, 0x00 }, 4) == 0)
		{
			FILE f;
	
	
			if ((f = fopen(name, "rb")) != null)
			{
				fread(RAM, 0x0290, 1, 4*10, f);
				fread(RAM, 0x02d0, 1, 3*10, f);
				fclose(f);
			}
	
			return 1;
		}
		else return 0;	/* we can't load the hi scores yet */
	} };
	


	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name)
	{
		FILE f;
	
	
		if ((f = fopen(name, "wb")) != null)
		{
			fwrite(RAM, 0x0290, 1, 4*10, f);
			fwrite(RAM, 0x02d0, 1, 3*10, f);
			fclose(f);
		}
	} };



	public static GameDriver nibbler_driver = new GameDriver
	(
                "Nibbler",
		"nibbler",
                "NICOLA SALMORIA\nBRIAN LEVINE\nMIRKO BUFFONI",
		machine_driver,
	
		nibbler_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		color_prom, null,null,
		ORIENTATION_DEFAULT,
	
		hiload, hisave
	);

	public static GameDriver fantasy_driver = new GameDriver
	(
                "Fantasy",
		"fantasy",
                "NICOLA SALMORIA\nBRIAN LEVINE\nMIRKO BUFFONI",
		machine_driver,
	
		fantasy_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		color_prom, null,null,
		ORIENTATION_DEFAULT,
	
		null, null
	);
}
