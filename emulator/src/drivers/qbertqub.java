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
import static mame.memoryH.*;
public class qbertqub
{
		
	
	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x57ff, MRA_RAM ),
		new MemoryReadAddress( 0x5800, 0x5800, input_port_0_r ),     /* DSW */
		new MemoryReadAddress( 0x5801, 0x5801, qbert_IN1_r ),     /* buttons */
		new MemoryReadAddress( 0x5802, 0x5802, input_port_2_r ),     /* trackball: not used */
		new MemoryReadAddress( 0x5803, 0x5803, input_port_3_r ),     /* trackball: not used */
		new MemoryReadAddress( 0x5804, 0x5804, input_port_4_r ),     /* joystick */
		new MemoryReadAddress( 0x8000, 0xffff, MRA_ROM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x2fff, MWA_RAM ),
		new MemoryWriteAddress( 0x3000, 0x30ff, MWA_RAM, spriteram, spriteram_size ),
		new MemoryWriteAddress( 0x3800, 0x3bff, videoram_w, videoram, videoram_size ),
		new MemoryWriteAddress( 0x4000, 0x4fff, MWA_RAM ), /* bg object ram... ? not used ? */
		new MemoryWriteAddress( 0x5000, 0x501f, gottlieb_paletteram_w, gottlieb_paletteram ),
		new MemoryWriteAddress( 0x5800, 0x5800, MWA_RAM ),    /* watchdog timer clear */
		new MemoryWriteAddress( 0x5801, 0x5801, MWA_RAM ),    /* trackball: not used */
		new MemoryWriteAddress( 0x5802, 0x5802, gottlieb_sh_w ), /* sound/speech command */
		new MemoryWriteAddress( 0x5803, 0x5803, gottlieb_output ),       /* OUT1 */
		new MemoryWriteAddress( 0x5804, 0x5804, MWA_RAM ),    /* OUT2 */
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
			0x40,   /* test mode off */
			new int[] { OSD_KEY_1, OSD_KEY_2, OSD_KEY_3, 0 /* coin 2 */,
					0, 0, 0, OSD_KEY_F2 }
		),
		new InputPort(       /* trackball: not used */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(       /* trackball: not used */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(       /* 2 joysticks (cocktail mode) mapped to one */
			0x00,
			new int[] { OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_UP, OSD_KEY_DOWN,
				OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_UP, OSD_KEY_DOWN }
		),
		new InputPort( -1 )  /* end of table */
	};
	
	static TrakPort trak_ports[] =
	{
	        new TrakPort( -1 )
	};
	
	
	static KEYSet keys[] =
	{
		new KEYSet( 4, 0, "MOVE DOWN RIGHT" ),
		new KEYSet( 4, 1, "MOVE UP LEFT"  ),
		new KEYSet( 4, 2, "MOVE UP RIGHT" ),
		new KEYSet( 4, 3, "MOVE DOWN LEFT" ),
		new KEYSet( -1 )
	};
	
	
	static DSW dsw[] =
	{
		new DSW( 0, 0x08, "ATTRACT MODE SOUND", new String[] { "ON", "OFF" } ),
	/*
	   Too lazy to enter such a big table of coins/credit until non-consecutive
	   dip-switches are handled... 8-)
	   Dip-switches 2-3-4-5 are at locations 0x01, 0x04, 0x10, 0x20
	   Two remarkable values:
		0x01 0x04 0x10 0x20
		-------------------
		 0    0    0    0     1-1 1-1
		 0    1    1    1     Free play
	*/
		new DSW( 0, 0x02, "1ST EXTRA LIFE AT", new String[] { "10000", "15000" } ),
		new DSW( 0, 0x40, "ADDITIONAL LIFE EVERY", new String[] { "20K", "25K" } ),
		new DSW( 0, 0x80, "DIFFICULTY LEVEL", new String[] { "NORMAL", "HARD" } ),
	/* the following switch must be connected to the IP16 line */
	/*      new DSW( 1, 0x40, "TEST MODE", new String[] {"ON", "OFF"} ),*/
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
		512,    /* 512 sprites */
		4,      /* 4 bits per pixel */
		new int[] { 0, 0x4000*8, 0x8000*8, 0xC000*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		32*8    /* every sprite takes 32 consecutive bytes */
	);
	
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,   0, 1 ),
		new GfxDecodeInfo( 1, 0x2000, spritelayout, 0, 1 ),
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
		qbert_vh_start,
		generic_vh_stop,
		gottlieb_vh_screenrefresh,
	
		/* sound hardware */
		null,      /* samples */
		null,
		gottlieb_sh_start,
		gottlieb_sh_stop,
		gottlieb_sh_update
	);
	
	
	
	static RomLoadPtr qbertqub_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000);    /* 64k for code */
		ROM_LOAD( "qq-rom3.bin", 0x8000, 0x2000, 0xac3cb8e2 );
		ROM_LOAD( "qq-rom2.bin", 0xa000, 0x2000, 0x64167070 );
		ROM_LOAD( "qq-rom1.bin", 0xc000, 0x2000, 0xdc7d6dc1 );
		ROM_LOAD( "qq-rom0.bin", 0xe000, 0x2000, 0xf2bad75a );
	
		ROM_REGION(0x12000);     /* temporary space for graphics */
		ROM_LOAD( "qq-bg0.bin", 0x0000, 0x1000, 0x13c600e6 );
		ROM_LOAD( "qq-bg1.bin", 0x1000, 0x1000, 0x542c9488 );
		ROM_LOAD( "qq-fg3.bin", 0x2000, 0x4000, 0xacd201f8 );      /* sprites */
		ROM_LOAD( "qq-fg2.bin", 0x6000, 0x4000, 0xa6a4660c );      /* sprites */
		ROM_LOAD( "qq-fg1.bin", 0xA000, 0x4000, 0x038fc633 );      /* sprites */
		ROM_LOAD( "qq-fg0.bin", 0xE000, 0x4000, 0x65b1f0f1 );      /* sprites */
	ROM_END(); }}; 
	
	
	
	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler() 
	{
/*TOFIX		FILE f=fopen(name,"rb");
                char[] RAM = Machine.memory_region[0];

 /*TOFIX               if (f!=null) {
                        fread(RAM,0x200,2*20,15,f); /* hi-score entries */
     /*TOFIX                   fread(RAM,0x458,8,1,f); /* checksum */
 /*TOFIX                       fclose(f);
                }*/
                return 1;
	} };
	
	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler() 
	{
/*TOFIX		FILE f=fopen(name,"wb");
                char[] RAM = Machine.memory_region[0];

                if (f!=null) {
                        fwrite(RAM,0x200,2*20,15,f); /* hi-score entries */
  /*TOFIX                      fwrite(RAM,0x458,8,1,f); /* checksum */
    /*TOFIX                    fclose(f);
                }*/
	} };
	
	
	public static GameDriver qbertqub_driver = new GameDriver
	(
	        "Q*Bert Qubes",
		"qbertqub",
	        "FABRICE FRANCES\nRODIMUS PRIME",
		machine_driver,
	
		qbertqub_rom,
		null, null,   /* rom decode and opcode decode functions */
		null,
	
		input_ports, null, trak_ports, dsw, keys,
	
		null, null, null,
		ORIENTATION_ROTATE_270,
	
		hiload,hisave     /* hi-score load and save */
	);
	
}
