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
import static mame.memoryH.*;

public class mplanets
{
	
	
	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x57ff, MRA_RAM ),
		new MemoryReadAddress( 0x5800, 0x5800, input_port_0_r ),     /* DSW */
		new MemoryReadAddress( 0x5801, 0x5801, mplanets_IN1_r ),     /* buttons */
		new MemoryReadAddress( 0x5802, 0x5802, input_port_2_r ),     /* trackball H: not used */
		new MemoryReadAddress( 0x5803, 0x5803, mplanets_dial_r ),     /* trackball V: dialer */
		new MemoryReadAddress( 0x5804, 0x5804, input_port_4_r ),     /* joystick */
		new MemoryReadAddress( 0x6000, 0xffff, MRA_ROM ),
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
		new MemoryWriteAddress( 0x6000, 0xffff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	static InputPort input_ports[] =
	{
		new InputPort(       /* DSW */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(       /* buttons */
			0x80,
			new int[] { OSD_KEY_3, OSD_KEY_4, /* coin 1 and 2 */
			  0,0,                  /* not connected ? */
			  0,0,                  /* not connected ? */
			  OSD_KEY_F2,           /* select */
			  0 }
		),
		new InputPort(       /* trackball H: not used */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(       /* trackball V: dialer */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(       /* joystick */
			0x00,
			new int[] { OSD_KEY_UP, OSD_KEY_RIGHT, OSD_KEY_DOWN, OSD_KEY_LEFT,
			OSD_KEY_CONTROL,OSD_KEY_1,OSD_KEY_2,OSD_KEY_ALT}
		),
		new InputPort( -1 )  /* end of table */
	};
	
	static TrakPort trak_ports[] =
	{
		new TrakPort( -1 )
	};
	
	
	static KEYSet keys[] =
	{
		new KEYSet( 4, 0, "MOVE UP" ),
		new KEYSet( 4, 3, "MOVE LEFT"  ),
		new KEYSet( 4, 1, "MOVE RIGHT" ),
		new KEYSet( 4, 2, "MOVE DOWN" ),
		new KEYSet( 4, 4, "FIRE1"     ),
		new KEYSet( 4, 7, "FIRE2"     ),
		new KEYSet( -1 )
	};
	
	
	static DSW dsw[] =
	{
		new DSW( 0, 0x08, "ROUND SELECT", new String[] { "OFF","ON" } ),
		new DSW( 0, 0x01, "ATTRACT MODE SOUND", new String[] { "ON", "OFF" } ),
		new DSW( 0, 0x1C, "", new String[] {
			"1 PLAY FOR 1 COIN" , "1 PLAY FOR 2 COINS",
			"1 PLAY FOR 1 COIN" , "1 PLAY FOR 2 COINS",
			"2 PLAYS FOR 1 COIN", "FREE PLAY",
			"2 PLAYS FOR 1 COIN", "FREE PLAY"
			} ),
		new DSW( 0, 0x20, "SHIPS PER GAME", new String[] { "3", "5" } ),
		new DSW( 0, 0x02, "EXTRA SHIP EVERY", new String[] { "10000", "12000" } ),
		new DSW( 0, 0xC0, "DIFFICULTY", new String[] { "STANDARD", "EASY", "HARD", "VERY HARD" } ),
		/*new DSW( 1, 0x80, "TEST MODE", new String[] {"ON", "OFF"} ),*/
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
		mplanets_vh_start,
		generic_vh_stop,
		gottlieb_vh_screenrefresh,
	
		/* sound hardware */
		null,      /* samples */
		null,
		gottlieb_sh_start,
		gottlieb_sh_stop,
		gottlieb_sh_update
	);
	
	static RomLoadPtr mplanets_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000);    /* 64k for code */
		ROM_LOAD( "ROM4", 0x6000, 0x2000, 0xf09b30bb );
		ROM_LOAD( "ROM3", 0x8000, 0x2000, 0x52223738 );
		ROM_LOAD( "ROM2", 0xa000, 0x2000, 0xe406bbb6 );
		ROM_LOAD( "ROM1", 0xc000, 0x2000, 0x385a7fa6 );
		ROM_LOAD( "ROM0", 0xe000, 0x2000, 0x29df430b );
	
		ROM_REGION(0xA000);     /* temporary space for graphics */
		ROM_LOAD( "BG0", 0x0000, 0x1000, 0xb85b00c3 );
		ROM_LOAD( "BG1", 0x1000, 0x1000, 0x175bc547 );
		ROM_LOAD( "FG3", 0x2000, 0x2000, 0x7c6a72bc );      /* sprites */
		ROM_LOAD( "FG2", 0x4000, 0x2000, 0x6ab56cc7 );      /* sprites */
		ROM_LOAD( "FG1", 0x6000, 0x2000, 0x16c596b7 );      /* sprites */
		ROM_LOAD( "FG0", 0x8000, 0x2000, 0x96727f86 );      /* sprites */
	
		ROM_REGION(0x10000);     /* 64k for sound cpu */
		ROM_LOAD( "SND1", 0xf000, 0x800, 0xca36c072 );
			ROM_RELOAD(0x7000, 0x800);/* A15 is not decoded */
		ROM_LOAD( "SND2", 0xf800, 0x800, 0x66461044 );
			ROM_RELOAD(0x7800, 0x800);/* A15 is not decoded */
	
	ROM_END(); }}; 
	
	
	
	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler() 
	{
/*TOFIX		FILE f=fopen(name,"rb");
    /*TOFIX            char[] RAM = Machine.memory_region[0];

                if (f!=null) {
                        fread(RAM,0x536,1,2,f); /* hiscore table checksum */
    /*TOFIX                    fread(RAM,0x538,41,7,f); /* 20+20+1 hiscore entries */
    /*TOFIX                    fclose(f);
                }
		return 1;*/
	} };
	
	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler() 
	{
/*TOFIX		 FILE f=fopen(name,"wb");
                char[] RAM = Machine.memory_region[0];

                if (f!=null) {
                        /* not saving distributions tables : does anyone really want them ? */
   /*TOFIX                     fwrite(RAM,0x536,1,2,f); /* hiscore table checksum */
       /*TOFIX                 fwrite(RAM,0x538,41,7,f); /* 20+20+1 hiscore entries */
        /*TOFIX                fclose(f);
                }*/
	} };
	
	
	public static GameDriver mplanets_driver = new GameDriver
	(
		"Mad Planets",
		"mplanets",
		"FABRICE FRANCES",
		machine_driver,
	
		mplanets_rom,
		null, null,   /* rom decode and opcode decode functions */
		gottlieb_sample_names,
	
		input_ports, null, trak_ports, dsw, keys,
	
		null, null, null,
		ORIENTATION_ROTATE_270,
	
		hiload,hisave     /* hi-score load and save */
	);
}
