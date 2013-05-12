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
 * using automatic conversion tool v0.02
 * converted at : 24-08-2011 22:25:36
 *
 *
 * roms are from v0.36 romset
 *
 */ 
package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static sndhrdw.generic.*;
import static sndhrdw.gottlieb.*;
import static machine.gottlieb.*;
import static vidhrdw.generic.*;
import static vidhrdw.gottlieb.*;
import static mame.inptport.*;
import static drivers.qbert.*;

public class reactor
{
	
	
	
	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x6fff, MRA_RAM ),
		new MemoryReadAddress( 0x7000, 0x7000, input_port_0_r ),     /* DSW */
		new MemoryReadAddress( 0x7001, 0x7001, reactor_IN1_r ),      /* buttons */
		new MemoryReadAddress( 0x7002, 0x7002, reactor_tb_H_r ),     /* trackball H */
		new MemoryReadAddress( 0x7003, 0x7003, reactor_tb_V_r ),     /* trackball V */
		new MemoryReadAddress( 0x7004, 0x7004, input_port_4_r ),     /* joystick */
		new MemoryReadAddress( 0x8000, 0xffff, MRA_ROM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x1fff, MWA_RAM ),
		new MemoryWriteAddress( 0x2000, 0x20ff, MWA_RAM, spriteram, spriteram_size ),
		new MemoryWriteAddress( 0x3000, 0x33ff, videoram_w, videoram, videoram_size ),
		new MemoryWriteAddress( 0x4000, 0x4fff, gottlieb_characterram_w, gottlieb_characterram ), /* bg object ram */
		new MemoryWriteAddress( 0x6000, 0x601f, gottlieb_paletteram_w, gottlieb_paletteram ),
		new MemoryWriteAddress( 0x7000, 0x7000, MWA_RAM ),    /* watchdog timer clear */
		new MemoryWriteAddress( 0x7001, 0x7001, MWA_RAM ),    /* trackball: not used */
		new MemoryWriteAddress( 0x7002, 0x7002, gottlieb_sh_w ), /* sound/speech command */
		new MemoryWriteAddress( 0x7003, 0x7003, gottlieb_output ),       /* OUT1 */
		new MemoryWriteAddress( 0x7004, 0x7004, MWA_RAM ),    /* OUT2 */
		new MemoryWriteAddress( 0x8000, 0xffff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	
	
	static InputPort input_ports[] =
	{
		new InputPort(       /* DSW */
			0x0,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(       /* buttons */
			0x02,   /* test mode off */
			new int[] { OSD_KEY_F2 /* select */, 0,0,0,0,0,0,0 }
		),
		new InputPort(       /* trackball: handled by reactor_tb_H_r() */
			0x0,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(       /* trackball: handled by reactor_tb_V_r() */
			0x0,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(       /* buttons */
			0x00,
			new int[] { OSD_KEY_1, OSD_KEY_2,         /* energy & decoy player 1. */
			  OSD_KEY_ALT, OSD_KEY_CONTROL, /* decoy & energy player 2. Also for 1 & 2 player start, long plays */
			  OSD_KEY_3, 0 /* coin 2 */, 0, 0 }
		),
		new InputPort( -1 )  /* end of table */
	};
	
	static TrakPort trak_ports[] =
{
	  new TrakPort(
	    X_AXIS,
	    1,
	    1.0,
	    gottlieb_trakball
	  ),
	  new TrakPort(
	    Y_AXIS,
	    1,
	    1.0,
	    gottlieb_trakball
	  ),
	  new TrakPort( -1 )
	};
	
	static KEYSet keys[] =
	{
		new KEYSet( 4, 1, "PLAYER 1 DECOY" ),
		new KEYSet( 4, 0, "PLAYER 1 ENERGY" ),
		new KEYSet( 4, 2, "PLAYER 2 DECOY" ),
		new KEYSet( 4, 3, "PLAYER 2 ENERGY" ),
		new KEYSet( -1 )
	};
	
	
	
	static DSW dsw[] =
	{
		new DSW( 0, 0x08, "SOUND WITH INSTRUCTIONS", new String[] { "NO","YES" } ),
		new DSW( 0, 0x01, "SOUND WITH LOGOS", new String[] { "NO", "YES" } ),
		new DSW( 0, 0x10, "", new String[] { "COCKTAIL", "UPRIGHT" } ),
		new DSW( 0, 0x04, "FREE PLAY", new String[] { "YES" , "NO" } ),
		new DSW( 0, 0x20, "COINS PER CREDIT", new String[] { "2", "1" } ),
		new DSW( 0, 0x02, "BOUNCE CHAMBERS PTS", new String[] { "10", "15" } ),
		new DSW( 0, 0xC0, "BONUS SHIP AT", new String[] { "10000", "12000", "20000", "15000" } ),
	/* the following switch must be connected to the IP16 line */
	/*      new DSW( 1, 0x2, "TEST MODE", new String[] {"ON", "OFF"} ),*/
		new DSW( -1 )
	};
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,    /* 8*8 characters */
		256,    /* 256 characters */
		4,      /* 4 bits per pixel */
		new int[] { 0, 1, 2, 3 },
		new int[] { 0, 4, 8, 12, 16, 20, 24, 28},
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		32*8    /* every char takes 32 consecutive bytes */
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,  /* 16*16 sprites */
		128,    /* 256 sprites */
		4,      /* 4 bits per pixel */
		new int[] { 0, 0x1000*8, 0x2000*8, 0x3000*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		32*8    /* every sprite takes 32 consecutive bytes */
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 0, 0x4000, charlayout, 0, 1 ),
		new GfxDecodeInfo( 1, 0, spritelayout, 0, 1 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_I86,
				5000000,        /* 5 Mhz */
				0,
				readmem,writemem,null,null,
				nmi_interrupt,1
			),
			new MachineCPU(
				CPU_M6502 | CPU_AUDIO_CPU ,
				3579545/4,        /* could it be /2 ? */
				2,             /* memory region #2 */
				gottlieb_sound_readmem,gottlieb_sound_writemem,null,null,
				gottlieb_sh_interrupt,1
			)
		},
		60,     /* frames / second */
		null,      /* init machine */
	
		/* video hardware */
		32*8, 32*8, new rectangle( 0*8, 32*8-1, 0*8, 30*8-1 ),
		gfxdecodeinfo,
		1+16, 16,
		gottlieb_vh_init_color_palette,
	
		VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
		null,      /* init vh */
		reactor_vh_start,
		generic_vh_stop,
		gottlieb_vh_screenrefresh,
	
		/* sound hardware */
		null,      /* samples */
		null,
		gottlieb_sh_start,
		gottlieb_sh_stop,
		gottlieb_sh_update
	);
	
	static RomLoadPtr reactor_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000);    /* 64k for code */
		ROM_LOAD( "ROM7", 0x8000, 0x1000, 0xd8f16275 );
		ROM_LOAD( "ROM6", 0x9000, 0x1000, 0x7aadbabf );
		ROM_LOAD( "ROM5", 0xa000, 0x1000, 0x9e2453f0 );
		ROM_LOAD( "ROM4", 0xb000, 0x1000, 0x2f1839e2 );
		ROM_LOAD( "ROM3", 0xc000, 0x1000, 0x70123534 );
		ROM_LOAD( "ROM2", 0xd000, 0x1000, 0xb50b26f3 );
		ROM_LOAD( "ROM1", 0xe000, 0x1000, 0xeaf1c223 );
		ROM_LOAD( "ROM0", 0xf000, 0x1000, 0xe126beca );
	
		ROM_REGION(0x8000);     /* temporary space for graphics */
		ROM_LOAD( "FG0", 0x0000, 0x1000, 0x80076d89 );      /* sprites */
		ROM_LOAD( "FG1", 0x1000, 0x1000, 0x0577a58b );      /* sprites */
		ROM_LOAD( "FG2", 0x2000, 0x1000, 0xe1ecaede );      /* sprites */
		ROM_LOAD( "FG3", 0x3000, 0x1000, 0x50087b04 );      /* sprites */
	
		ROM_REGION(0x10000);     /* 64k for sound cpu */
		ROM_LOAD( "SND1", 0xf000, 0x800, 0x1367334b );
			ROM_RELOAD(0x7000, 0x800);/* A15 is not decoded */
		ROM_LOAD( "SND2", 0xf800, 0x800, 0x10e64e0a );
			ROM_RELOAD(0x7800, 0x800);/* A15 is not decoded */
	ROM_END(); }}; 
	
	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name) 
	{
		FILE f=fopen(name,"rb");
                char[] RAM = Machine.memory_region[0];
                 if (RAM[0x4D8]!=0x0A) return 0;  
                if (f!=null) {
                        fread(RAM,0x4D8,0x557-0x4D8,1,f);
                        fclose(f);
                }
                return 1;
	} };
	
	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name) 
	{
		FILE f=fopen(name,"wb");
                char[] RAM = Machine.memory_region[0];

                if (f!=null) {
                        fwrite(RAM,0x4D8,0x557-0x4D8,1,f);
                        fclose(f);
                }
	} };
	
	public static GameDriver reactor_driver = new GameDriver
	(
		"Reactor",
		"reactor",
		"FABRICE FRANCES",
		machine_driver,
	
		reactor_rom,
		null, null,   /* rom decode and opcode decode functions */
		gottlieb_sample_names,
	
		input_ports, null, trak_ports, dsw, keys,
	
		null, null, null,
		ORIENTATION_DEFAULT,
	
		hiload,hisave     /* hi-score load and save */
	);
}
