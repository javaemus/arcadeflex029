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
 *  roms are from v0.36 romset (graphics roms are in v0.27 order)
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
import static sndhrdw._8910intf.*;
import static sndhrdw.generic.*;
import static sndhrdw.gyruss.*;
import static vidhrdw.generic.*;
import static vidhrdw.gyruss.*;
import static mame.memoryH.*;

public class gyruss
{
	
	
	
	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x9000, 0x9fff, MRA_RAM ),
		new MemoryReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x5fff, MRA_ROM ),
		new MemoryReadAddress( 0xa700, 0xa700, MRA_RAM ),
		new MemoryReadAddress( 0xa7fc, 0xa7fd, MRA_RAM ),
		new MemoryReadAddress( 0xc080, 0xc080, input_port_0_r ),	/* IN0 */
		new MemoryReadAddress( 0xc0a0, 0xc0a0, input_port_1_r ),	/* IN1 */
		new MemoryReadAddress( 0xc0c0, 0xc0c0, input_port_2_r ),	/* IN2 */
		new MemoryReadAddress( 0xc0e0, 0xc0e0, input_port_3_r ),	/* DSW1 */
		new MemoryReadAddress( 0xc000, 0xc000, input_port_4_r ),	/* DSW2 */
		new MemoryReadAddress( 0xc100, 0xc100, input_port_5_r ),	/* DSW3 */
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x9000, 0x9fff, MWA_RAM ),
		new MemoryWriteAddress( 0x8000, 0x83ff, colorram_w, colorram ),
		new MemoryWriteAddress( 0x8400, 0x87ff, videoram_w, videoram,videoram_size ),
		new MemoryWriteAddress( 0xa000, 0xa17f, MWA_RAM, spriteram,spriteram_size ),     /* odd frame spriteram */
		new MemoryWriteAddress( 0xa200, 0xa37f, MWA_RAM, spriteram_2 ),   /* even frame spriteram */
		new MemoryWriteAddress( 0xa700, 0xa700, MWA_RAM, gyruss_spritebank ),
		new MemoryWriteAddress( 0xa701, 0xa701, MWA_NOP ),        /* semaphore system   */
		new MemoryWriteAddress( 0xa702, 0xa702, gyruss_queuereg_w ),       /* semaphore system   */
		new MemoryWriteAddress( 0xa7fc, 0xa7fc, MWA_RAM, gyruss_6809_drawplanet ),
		new MemoryWriteAddress( 0xa7fd, 0xa7fd, MWA_RAM, gyruss_6809_drawship ),
		new MemoryWriteAddress( 0xc180, 0xc180, interrupt_enable_w ),      /* NMI enable         */
		new MemoryWriteAddress( 0xc000, 0xc000, MWA_NOP ),
		new MemoryWriteAddress( 0xc100, 0xc100, sound_command_w ),         /* command to soundb  */
		new MemoryWriteAddress( 0xc080, 0xc080, MWA_NOP ),
		new MemoryWriteAddress( 0xc185, 0xc185, MWA_RAM ),	/* ??? */
		new MemoryWriteAddress( 0x0000, 0x5fff, MWA_ROM ),                 /* rom space+1        */
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	
	
	static MemoryReadAddress sound_readmem[] =
	{
		new MemoryReadAddress( 0x6000, 0x63ff, MRA_RAM ),                 /* ram soundboard     */
		new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),                 /* rom soundboard     */
		new MemoryReadAddress( 0x8000, 0x8000, sound_command_r ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress sound_writemem[] =
	{
		new MemoryWriteAddress( 0x6000, 0x63ff, MWA_RAM ),                 /* ram soundboard     */
		new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),                 /* rom soundboard     */
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	static IOReadPort sound_readport[] =
	{
		new IOReadPort( 0x01, 0x01, AY8910_read_port_0_r ),
	  	new IOReadPort( 0x05, 0x05, AY8910_read_port_1_r ),
		new IOReadPort( 0x09, 0x09, AY8910_read_port_2_r ),
	  	new IOReadPort( 0x0d, 0x0d, AY8910_read_port_3_r ),
	  	new IOReadPort( 0x11, 0x11, AY8910_read_port_4_r ),
		new IOReadPort( -1 )
	};
	
	static IOWritePort sound_writeport[] =
	{
		new IOWritePort( 0x00, 0x00, AY8910_control_port_0_w ),
		new IOWritePort( 0x02, 0x02, AY8910_write_port_0_w ),
		new IOWritePort( 0x04, 0x04, AY8910_control_port_1_w ),
		new IOWritePort( 0x06, 0x06, AY8910_write_port_1_w ),
		new IOWritePort( 0x08, 0x08, AY8910_control_port_2_w ),
		new IOWritePort( 0x0a, 0x0a, AY8910_write_port_2_w ),
		new IOWritePort( 0x0c, 0x0c, AY8910_control_port_3_w ),
		new IOWritePort( 0x0e, 0x0e, AY8910_write_port_3_w ),
		new IOWritePort( 0x10, 0x10, AY8910_control_port_4_w ),
		new IOWritePort( 0x12, 0x12, AY8910_write_port_4_w ),
		new IOWritePort( 0x14, 0x14, gyruss_sh_soundfx_on_w ),
		new IOWritePort( 0x18, 0x18, gyruss_sh_soundfx_data_w ),
		new IOWritePort( -1 )	/* end of table */
	};
	
	
	
	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { 0, 0, OSD_KEY_3, OSD_KEY_1, OSD_KEY_2, 0, 0, 0 }
		),
		new InputPort(	/* IN1 */
			0xff,
			new int[] { OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_CONTROL, 0, 0, 0 }
		),
		new InputPort(	/* IN2 */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW1 */
			0xaf,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW2 */
			0x3b,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW3 */
			0xfe,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};

static TrakPort trak_ports[] =
{
       new TrakPort(-1)
};


static KEYSet keys[] =
{
        new KEYSet( 1, 2, "MOVE UP" ),
        new KEYSet( 1, 0, "MOVE LEFT"  ),
        new KEYSet( 1, 1, "MOVE RIGHT" ),
        new KEYSet( 1, 3, "MOVE DOWN" ),
        new KEYSet( 1, 4, "FIRE" ),
        new KEYSet( -1 )
};

	
	
	static DSW dsw[] =
	{
		new DSW( 4, 0x03, "LIVES", new String[] { "255", "5", "4", "3" }, 1 ),
	 	new DSW( 4, 0x08, "BONUS", new String[] { "60000 80000", "50000 70000" }, 1 ),
		new DSW( 4, 0x70, "DIFFICULTY", new String[] { "MOST DIFFICULT", "VERY DIFFICULT", "DIFFICULT", "AVERAGE", "EASY 3", "EASY 2", "EASY 1" , "VERY EASY" }, 1 ),
		new DSW( 4, 0x80, "DEMO SOUNDS", new String[] { "ON", "OFF" }, 1 ),
		new DSW( 5, 0x01, "DEMO MUSIC", new String[] { "ON", "OFF" }, 1 ),
		new DSW( -1 )
	};
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		512,	/* 512 characters */
		2,	/* 2 bits per pixel */
		new int[] { 4, 0 },
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 8*8+0,8*8+1,8*8+2,8*8+3 },
		16*8	/* every char takes 16 consecutive bytes */
	);
	
	static GfxLayout spritelayout1 = new GfxLayout
	(
		16,8,	/* 16*8 sprites */
		128,	/* 128 sprites */
		4,	/* 4 bits per pixel */
		new int[] {  4, 0, 0x2000*8+4, 0x2000*8 },
		new int[] { 39*8, 38*8, 37*8, 36*8, 35*8, 34*8, 33*8, 32*8,
				7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3,  8*8, 8*8+1, 8*8+2, 8*8+3 },
		64*8	/* every sprite takes 64 consecutive bytes */
	);
	
	static GfxLayout spritelayout2 = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		128,	/* 128 sprites */
		4,	/* 4 bits per pixel */
		new int[] {  4, 0, 0x2000*8+4, 0x2000*8 },
		new int[] { 39*8, 38*8, 37*8, 36*8, 35*8, 34*8, 33*8, 32*8,
				7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3,  8*8, 8*8+1, 8*8+2, 8*8+3,
			16*8+0, 16*8+1, 16*8+2, 16*8+3,  24*8, 24*8+1, 24*8+2, 24*8+3 },
		64*8	/* every sprite takes 64 consecutive bytes */
	);
	
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,       0, 16 ),
		new GfxDecodeInfo( 1, 0x2000, spritelayout1, 16*4, 16 ),	/* upper half */
		new GfxDecodeInfo( 1, 0x2010, spritelayout1, 16*4, 16 ),	/* lower half */
		new GfxDecodeInfo( 1, 0x6000, spritelayout1, 16*4, 16 ),	/* upper half */
		new GfxDecodeInfo( 1, 0x6010, spritelayout1, 16*4, 16 ),	/* lower half */
		new GfxDecodeInfo( 1, 0x2000, spritelayout2, 16*4, 16 ),
		new GfxDecodeInfo( 1, 0x6000, spritelayout2, 16*4, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	/* these are NOT the original color PROMs */
	static char color_prom[] =
	{
		/* char palette */
		0x00, 0x90, 0xB4, 0xD8, 0x00, 0xB4, 0xD8, 0xFC, 0x00, 0xA4, 0x92, 0xFF, 0x00, 0x48, 0x6C, 0x90,
		0x00, 0xD0, 0x88, 0xFF, 0x00, 0x49, 0x92, 0xFF, 0x00, 0x0D, 0x11, 0x1E, 0x90, 0xFC, 0xB4, 0xD8,
		0xD8, 0xB4, 0x90, 0xB4, 0x00, 0x6C, 0x48, 0xB4, 0x00, 0x49, 0x92, 0xFF, 0x00, 0x55, 0x9E, 0x1A,
		0x00, 0x48, 0x6C, 0x90, 0x00, 0xC4, 0xC8, 0xF0, 0x00, 0x40, 0x80, 0xE0, 0x00, 0x6C, 0x90, 0xB4,
		
		/* Sprite palette */
		0x00, 0x01, 0x02, 0x03, 0xDF, 0xDF, 0x1C, 0xFF, 0x12, 0x13, 0x00, 0xB6, 0x00, 0x01, 0x92, 0xE0,
		0x00, 0x01, 0x02, 0x03, 0xFF, 0xDB, 0x1C, 0xFF, 0x12, 0x1F, 0x01, 0xB6, 0x03, 0x01, 0x92, 0xE0,
		0x00, 0x01, 0x02, 0x03, 0xDF, 0xDB, 0xDC, 0xFF, 0x12, 0x1F, 0x1C, 0xB6, 0x03, 0x01, 0x92, 0xE0,
		0x00, 0x01, 0x02, 0x03, 0xDF, 0xDB, 0x1C, 0xFF, 0x12, 0x1F, 0x1C, 0xB6, 0x00, 0x01, 0x92, 0xE0,
		0x00, 0x13, 0x02, 0x23, 0xFF, 0xBA, 0xBC, 0x0E, 0x92, 0x37, 0x00, 0x90, 0xB6, 0x49, 0x92, 0x0A,
		0x48, 0x6D, 0x02, 0x03, 0x49, 0xB6, 0x1C, 0x09, 0xB6, 0x1F, 0x00, 0x90, 0x92, 0x49, 0x92, 0x06,
		0x00, 0x0B, 0x02, 0x03, 0x7B, 0xB6, 0x95, 0x09, 0x17, 0x1F, 0x00, 0x94, 0x0E, 0x91, 0x96, 0x13,
		0x00, 0xA8, 0x02, 0x03, 0x49, 0xB6, 0x95, 0x09, 0xED, 0x1F, 0x00, 0x94, 0xE8, 0x09, 0x51, 0x1F,
		0x00, 0x1B, 0xD4, 0x96, 0xE8, 0x1B, 0xF4, 0xB6, 0xFC, 0x17, 0xE4, 0x92, 0xFC, 0x13, 0xE8, 0xFF,
		0x00, 0xFF, 0x0B, 0x03, 0x92, 0xE0, 0x17, 0x19, 0x12, 0x1F, 0x0B, 0xBA, 0x96, 0x5C, 0x92, 0xE0,
		0x00, 0x01, 0x02, 0x03, 0x08, 0x10, 0x1C, 0x09, 0x12, 0x1F, 0x00, 0x90, 0xBC, 0x49, 0x92, 0xFF,
		0x00, 0x01, 0x02, 0x03, 0x08, 0x10, 0x1C, 0x09, 0x12, 0x1F, 0x00, 0x90, 0x7C, 0x49, 0x92, 0xFF,
		0x00, 0x01, 0x02, 0x03, 0x08, 0x10, 0x1C, 0x09, 0x12, 0x1F, 0x00, 0x90, 0xFC, 0x49, 0x92, 0xFF,
		0x01, 0x01, 0x01, 0x01, 0x15, 0x01, 0x01, 0x01, 0x1D, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
		0x48, 0x01, 0x02, 0x03, 0x08, 0x10, 0x1C, 0x09, 0x12, 0x1F, 0x00, 0x90, 0xFC, 0x49, 0x92, 0xFF,
		0x00, 0x01, 0x03, 0x03, 0x09, 0x9F, 0x17, 0x57, 0x12, 0xE0, 0x9B, 0x0E, 0xFC, 0xF0, 0x53, 0xC0
	};
	
	
	
	static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz (?) */
				0,
				readmem, writemem, null, null,
				nmi_interrupt, 1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				3579500,	/* 3.5795 Mhz */
				2,	/* memory region #2 */
				sound_readmem, sound_writemem, sound_readport, sound_writeport,
				gyruss_sh_interrupt, 10
			)
		},
		60,
		null,
	
		/* video hardware */
		32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
		gfxdecodeinfo,
		256, 32*16,
		gyruss_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
		null,
		gyruss_vh_start,
		generic_vh_stop,
		gyruss_vh_screenrefresh,
	
		/* sound hardware */
		null,
		null,
		gyruss_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	static RomLoadPtr  gyruss_rom= new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "gyrussk.1",    0x0000, 0x2000, 0xc673b43d );
                ROM_LOAD( "gyrussk.2",    0x2000, 0x2000, 0xa4ec03e4 );
                ROM_LOAD("gyrussk.3",    0x4000, 0x2000, 0x27454a98 );
		/* the diagnostics ROM would go here */
	
		ROM_REGION(0xa000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "gyrussk.4",    0x0000, 0x2000, 0x27d8329b );//rom ( name gy-6.bin size 8192 crc 27d8329b )
                ROM_LOAD( "gyrussk.5",    0x2000, 0x2000, 0x4f22411a );//rom ( name gy-9.bin size 8192 crc 4f22411a )
                ROM_LOAD( "gyrussk.7",    0x4000, 0x2000, 0x8e8d388c );//rom ( name gy-7.bin size 8192 crc 8e8d388c )
                ROM_LOAD( "gyrussk.6",    0x6000, 0x2000, 0xc949db10 );//rom ( name gy-10.bin size 8192 crc c949db10 )             
                ROM_LOAD( "gyrussk.8",    0x8000, 0x2000, 0x47cd1fbc );//rom ( name gy-8.bin size 8192 crc 47cd1fbc )
                
	
		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		ROM_LOAD( "gyrussk.1a",   0x0000, 0x2000, 0xf4ae1c17 );
                ROM_LOAD( "gyrussk.2a",   0x2000, 0x2000, 0xba498115 );
	
		ROM_REGION(0x2000);	/* Gyruss also contains a 6809, we don't need to emulate it */
							/* but need the data tables contained in its ROM */
		ROM_LOAD( "gyrussk.9",  0x0000, 0x2000, 0x822bf27e );//rom ( name gy-5.bin size 8192 crc 822bf27e )
                               
	        ROM_END();
        }};
		
	static String gyruss_sample_names[] =
	{
                "AUDIO01.SAM",
                "AUDIO02.SAM",
                "AUDIO03.SAM",
                "AUDIO04.SAM",
                "AUDIO05.SAM",
                "AUDIO06.SAM",
                "AUDIO07.SAM",
		null	/* end of array */
	};
	
	
	
	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
	{
		/* get RAM pointer (this game is multiCPU, we can't assume the global */
		/* RAM pointer is pointing to the right place) */
		char []RAM = Machine.memory_region[0];
	
		/* check if the hi score table has already been initialized */
	 /*TOFIX        	if (memcmp(RAM, 0x9489, new char[] { 0x00, 0x00, 0x01 }, 3) == 0 &&
				memcmp(RAM, 0x94a9, new char[] { 0x00, 0x43, 0x00 }, 3) == 0)
		{
			FILE f;
	
	
			if ((f = fopen(name, "rb")) != null)
			{
				fread(RAM, 0x9488, 1, 8*5, f);
				RAM[0x940b] = RAM[0x9489];
				RAM[0x940c] = RAM[0x948a];
				RAM[0x940d] = RAM[0x948b];
				fclose(f);
			}
	
			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };
	
	
	
	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
	{
		FILE f;
		/* get RAM pointer (this game is multiCPU, we can't assume the global */
		/* RAM pointer is pointing to the right place) */
		char []RAM = Machine.memory_region[0];
	
	
	 /*TOFIX        	if ((f = fopen(name, "wb")) != null)
		{
			fwrite(RAM, 0x9488, 1, 8*5, f);
			fclose(f);
		}*/
	} };
	
	
	
	public static GameDriver gyruss_driver = new GameDriver
	(
                "Gyruss",
		"gyruss",
                "MIKE CUDDY\nMIRKO BUFFONI\nNICOLA SALMORIA",
		machine_driver,
	
		gyruss_rom,
		null, null,
		gyruss_sample_names,
	
		input_ports,null, trak_ports, dsw, keys,
	
		color_prom, null, null,
	
		ORIENTATION_DEFAULT,
	
		hiload, hisave
	);
}

