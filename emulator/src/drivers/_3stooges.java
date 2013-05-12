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

public class _3stooges
{
		
	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x1fff, MRA_RAM ),
		new MemoryReadAddress( 0x2000, 0x2fff, MRA_ROM ),
		new MemoryReadAddress( 0x3000, 0x57ff, MRA_RAM ),
		new MemoryReadAddress( 0x5800, 0x5800, input_port_0_r ),     /* DSW */
		new MemoryReadAddress( 0x5801, 0x5801, stooges_IN1_r ),     /* buttons */
		new MemoryReadAddress( 0x5802, 0x5802, input_port_2_r ),     /* trackball H: not used */
		new MemoryReadAddress( 0x5803, 0x5803, input_port_3_r ),     /* trackball V: not used */
		new MemoryReadAddress( 0x5804, 0x5804, stooges_joysticks ),  /* joysticks demultiplexer */
		new MemoryReadAddress( 0x6000, 0xffff, MRA_ROM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x1fff, MWA_RAM ),
		new MemoryWriteAddress( 0x2000, 0x2fff, MWA_ROM ),
		new MemoryWriteAddress( 0x3000, 0x30ff, MWA_RAM, spriteram, spriteram_size ),
		new MemoryWriteAddress( 0x3800, 0x3bff, videoram_w, videoram, videoram_size ),
		new MemoryWriteAddress( 0x4000, 0x4fff, gottlieb_characterram_w, gottlieb_characterram ),
		new MemoryWriteAddress( 0x5000, 0x501f, gottlieb_paletteram_w, gottlieb_paletteram ),
		new MemoryWriteAddress( 0x5800, 0x5800, MWA_RAM ),    /* watchdog timer clear */
		new MemoryWriteAddress( 0x5801, 0x5801, MWA_RAM ),    /* trackball output not used */
		new MemoryWriteAddress( 0x5802, 0x5802, gottlieb_sh2_w ), /* sound/speech command */
		new MemoryWriteAddress( 0x5803, 0x5803, gottlieb_output ),       /* OUT1 */
		new MemoryWriteAddress( 0x5804, 0x5804, MWA_RAM ),    /* OUT2 */
		new MemoryWriteAddress( 0x6000, 0xffff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	

		
	static InputPort input_ports[] =
        {
                new InputPort(       /* DSW */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(       /* buttons */
                        0x11, /* tilt off, test mode off */
                        new int[]{ 0, OSD_KEY_F2, /* test mode, select */
                          OSD_KEY_4,OSD_KEY_3, /* coin 1 & 2 */
                          OSD_KEY_T, /* tilt : does someone really want that ??? */
                          0,0,0 }
                ),
		new InputPort(       /* trackball H: not used */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(       /* trackball V: not used */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(       /* joystick 2 (Moe) */
                        0x00,
                        new int[]{ OSD_KEY_I, OSD_KEY_L, OSD_KEY_K, OSD_KEY_J,
                        OSD_KEY_ALT,0,0,0 }
                ),
		new InputPort(      /* joystick 1 (Curly) */
                        0x00,
                        new int[]{ OSD_KEY_E, OSD_KEY_F, OSD_KEY_D, OSD_KEY_S,
                        0,OSD_KEY_CONTROL,0,0 }	/* Larry fire */
                       
                ),
		new InputPort(      /* joystick 3 (Larry) */
                        0x00,
                        new int[]{ OSD_KEY_UP, OSD_KEY_RIGHT, OSD_KEY_DOWN, OSD_KEY_LEFT,
                        0,0,OSD_KEY_ENTER,0 }	/* Curly fire */
                       
                ),
		new InputPort( -1 )  /* end of table */
        };
	
	static TrakPort trak_ports[] =
	{
		new TrakPort( -1 )
	};
	
	static KEYSet keys[] =
	{
		new KEYSet( 4, 0, "MOE UP" ),
		new KEYSet( 4, 3, "MOE LEFT"  ),
		new KEYSet( 4, 1, "MOE RIGHT" ),
		new KEYSet( 4, 2, "MOE DOWN" ),
		new KEYSet( 4, 4, "MOE FIRE"     ),
		new KEYSet( 5, 0, "CURLY UP" ),
		new KEYSet( 5, 3, "CURLY LEFT"  ),
		new KEYSet( 5, 1, "CURLY RIGHT" ),
		new KEYSet( 5, 2, "CURLY DOWN" ),
		new KEYSet( 6, 6, "CURLY FIRE"     ),
		new KEYSet( 6, 0, "LARRY UP" ),
		new KEYSet( 6, 3, "LARRY LEFT"  ),
		new KEYSet( 6, 1, "LARRY RIGHT" ),
		new KEYSet( 6, 2, "LARRY DOWN" ),
		new KEYSet( 5, 5, "LARRY FIRE" ),
		new KEYSet( -1 )
	};
	
	static DSW dsw[] =
	{
		new DSW( 0, 0x01, "ATTRACT MODE SOUND", new String[] { "ON", "OFF" } ),
		new DSW( 0, 0x02, "DIFFICULTY", new String[] { "NORMAL", "HARD" } ),
		new DSW( 0, 0x08, "LIVES PER GAME", new String[] { "3", "5" } ),
		new DSW( 0, 0x1C, "", new String[] {
			"1 PLAY FOR 1 COIN" , "2 PLAYS FOR 1 COIN",
			"1 PLAY FOR 1 COIN" , "2 PLAYS FOR 1 COIN",
			"1 PLAY FOR 2 COINS", "FREE PLAY",
			"1 PLAY FOR 2 COINS", "FREE PLAY"
			} ),
		new DSW( 0, 0x40, "FIRST EXTRA LIVE AT", new String[] { "20000","10000" } ),
		new DSW( 0, 0x80, "ADD. EXTRA LIVE EVERY", new String[] { "20000", "10000" } ),
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
		256,    /* 256 sprites */
		4,      /* 4 bits per pixel */
		new int[] { 0, 0x2000*8, 0x4000*8, 0x6000*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		32*8    /* every sprite takes 32 consecutive bytes */
	);
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,   0, 1 ), /* 1 palette for the game */
		new GfxDecodeInfo( 1, 0x0000, spritelayout, 0, 1 ),
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
	},
		60,     /* frames / second */
		null,      /* init machine */
	
		/* video hardware */
		32*8, 32*8, new rectangle( 0*8, 32*8-1, 0*8, 30*8-1 ),
		gfxdecodeinfo,
		1+16,256,
		gottlieb_vh_init_color_palette,
	
		VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
		null,      /* init vh */
		stooges_vh_start,
		generic_vh_stop,
		gottlieb_vh_screenrefresh,
	
		/* sound hardware */
		null,
		null,
		gottlieb_sh_start,
		gottlieb_sh_stop,
		gottlieb_sh_update
	);
	
	static RomLoadPtr stooges_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000);    /* 64k for code */
		ROM_LOAD( "GV113RAM.4", 0x2000, 0x1000, 0x533bff2a );
                ROM_LOAD( "GV113ROM.4", 0x6000, 0x2000, 0x8b6e52b8 );
                ROM_LOAD( "GV113ROM.3", 0x8000, 0x2000, 0xb816d8c4 );
                ROM_LOAD( "GV113ROM.2", 0xa000, 0x2000, 0xb45b2a79 );
                ROM_LOAD( "GV113ROM.1", 0xc000, 0x2000, 0x34ab051e );
                ROM_LOAD( "GV113ROM.0", 0xe000, 0x2000, 0xab124329 );
	
		ROM_REGION(0x8000);     /* temporary space for graphics */
		ROM_LOAD( "GV113FG3", 0x0000, 0x2000, 0x28071212 );       /* sprites */
                ROM_LOAD( "GV113FG2", 0x2000, 0x2000, 0x9fa3dfde  );       /* sprites */
                ROM_LOAD( "GV113FG1", 0x4000, 0x2000, 0xfb223854 );       /* sprites */
                ROM_LOAD( "GV113FG0", 0x6000, 0x2000, 0x95762c53 );       /* sprites */
	
		ROM_REGION(0x10000);     /* 64k for sound cpu */
	//      ROM_LOAD( "DROM", 0xe000, 0x2000, 0x3aa5d107 );
	//      ROM_RELOAD(0x6000, 0x2000);/* A15 not decoded ?? */
	ROM_END(); }}; 
	
	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name) 
	{
		FILE f=fopen(name,"rb");
                char[] RAM = Machine.memory_region[0];

                if (f!=null) {
                        fread(RAM,0x485,22,7,f); /* 21 hiscore entries + 1 (?) */
                        fclose(f);
                }
                return 1;
	} };
	
	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name) 
	{
		FILE f=fopen(name,"wb");
                char[] RAM = Machine.memory_region[0];

                if (f!=null) {
                        fwrite(RAM,0x485,22,7,f); /* hiscore entries */
                        fclose(f);
                }
	} };
	
	
	public static GameDriver stooges_driver = new GameDriver
	(
		"Three Stooges",
		"3stooges",
		"FABRICE FRANCES",
		machine_driver,
	
		stooges_rom,
		null, null,   /* rom decode and opcode decode functions */
		gottlieb_sample_names,
	
		input_ports, null, trak_ports, dsw, keys,
	
		null, null, null,
		ORIENTATION_DEFAULT,
	
		hiload, hisave
	);
}
