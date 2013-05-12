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
 * converted at : 24-08-2011 23:19:25
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
import static vidhrdw.generic.*;
import static sndhrdw.generic.*;
import static machine.qix.*;
import static vidhrdw.qix.*;
import static mame.inptport.*;

public class qix
{
		
	static MemoryReadAddress readmem_cpu_data[] =
	{
		/*new MemoryReadAddress( 0x2000, 0x2001, m6821_r ),*/
		/*new MemoryReadAddress( 0x4000, 0x4003, m6821_r ),*/
		new MemoryReadAddress( 0x8000, 0x83ff, qix_sharedram_r_1, qix_sharedram ),
		new MemoryReadAddress( 0x8400, 0x87ff, MRA_RAM ),
		/*new MemoryReadAddress( 0x9000, 0x9003, m6821_r ),*/
		new MemoryReadAddress( 0x9400, 0x9400, input_port_0_r ), /* PIA 1 PORT A -- Player controls */
		new MemoryReadAddress( 0x9402, 0x9402, input_port_1_r ), /* PIA 1 PORT B -- Coin door switches */
		/*new MemoryReadAddress( 0x9900, 0x9903, m6821_r ),*/
		/*new MemoryReadAddress( 0x9c00, 0x9c03, m6821_r ),*/
		new MemoryReadAddress( 0xc000, 0xffff, MRA_ROM ),
		new MemoryReadAddress( -1 ) /* end of table */
	};
	
	static MemoryReadAddress readmem_cpu_video[] =
	{
		new MemoryReadAddress( 0x0000, 0x7fff, qix_videoram_r ),
		new MemoryReadAddress( 0x8000, 0x83ff, qix_sharedram_r_2 ),
		new MemoryReadAddress( 0x8400, 0x87ff, MRA_RAM ),
		new MemoryReadAddress( 0x9400, 0x9400, qix_addresslatch_r ),
		new MemoryReadAddress( 0x9800, 0x9800, qix_scanline_r ),
		new MemoryReadAddress( 0xc800, 0xffff, MRA_ROM ),
		new MemoryReadAddress( -1 ) /* end of table */
	};
	
	static MemoryWriteAddress writemem_cpu_data[] =
	{
		/*new MemoryWriteAddress( 0x2000, 0x2001, m6821_w ),*/
		/*new MemoryWriteAddress( 0x4000, 0x4003, m6821_w ),*/
		new MemoryWriteAddress( 0x8000, 0x83ff, qix_sharedram_w ),
		new MemoryWriteAddress( 0x8400, 0x87ff, MWA_RAM ),
		new MemoryWriteAddress( 0x8c00, 0x8c00, qix_video_firq_w ),
		/*new MemoryWriteAddress( 0x9000, 0x9003, m6821_w ),*/
		/*new MemoryWriteAddress( 0x9400, 0x9403, m6821_w ),*/
		/*new MemoryWriteAddress( 0x9900, 0x9903, m6821_w ),*/
		/*new MemoryWriteAddress( 0x9C00, 0x9C03, m6821_w ),*/
		new MemoryWriteAddress( 0xc000, 0xffff, MWA_ROM ),
		new MemoryWriteAddress( -1 ) /* end of table */
	};
	
	static MemoryWriteAddress writemem_cpu_video[] =
	{
		new MemoryWriteAddress( 0x0000, 0x7fff, qix_videoram_w ),
		new MemoryWriteAddress( 0x8000, 0x83ff, qix_sharedram_w ),
		new MemoryWriteAddress( 0x8400, 0x87ff, MWA_RAM ),
		new MemoryWriteAddress( 0x8800, 0x8800, qix_palettebank_w, qix_palettebank ),
		new MemoryWriteAddress( 0x8c00, 0x8c00, qix_data_firq_w ),
		new MemoryWriteAddress( 0x9000, 0x93ff, qix_paletteram_w, qix_paletteram ),
		new MemoryWriteAddress( 0x9400, 0x9400, qix_addresslatch_w ),
		new MemoryWriteAddress( 0x9402, 0x9403, MWA_RAM, qix_videoaddress ),
		new MemoryWriteAddress( 0xc800, 0xffff, MWA_ROM ),
		new MemoryWriteAddress( -1 ) /* end of table */
	};
	
	
	static InputPort input_ports[] =
	{
		new InputPort(	/* PORT 0 -- PIA 1 Port A -- Controls [01234567] */
			0xff,	/* default_value */
	
			/* keyboard controls */
			new int[] { OSD_KEY_UP, OSD_KEY_RIGHT, OSD_KEY_DOWN, OSD_KEY_LEFT,
			  OSD_KEY_ALT /* slow draw */, OSD_KEY_2 /* 2P start */,
			  OSD_KEY_1 /* 1P start */,  OSD_KEY_CONTROL /* fast draw */ } /* EBM 970517 */
		),
		new InputPort(	/* PORT 1 -- PIA 1 Port B -- Coin door switches */
			0xff,	/* default_value */
	
			/* keyboard controls */
			new int[] { OSD_KEY_F1 /* adv. test */, OSD_KEY_F2 /* sub. test */,/* EBM 970519 */
			  OSD_KEY_F5 /* slew up */, OSD_KEY_F6 /* slew down */,  /* EBM 970519 */
			  OSD_KEY_3 /* left coin */, OSD_KEY_4 /* right coin */, /* EBM 970517 */
			  0 /* coin sw */,  OSD_KEY_F9 /* tilt */ }
		),
		new InputPort( -1 )  /* end of table */
	};
	
	static TrakPort trak_ports[] =
	{
	        new TrakPort( -1 )
	};
	
	/* These are only here to allow user to change the key settings */
	/* Note:  Also need keys for the coin door switches. */
	static KEYSet keys[] =
	{
		/* port, bit num., setting name */
	        new KEYSet( 0, 0, "MOVE UP" ),
	        new KEYSet( 0, 1, "MOVE RIGHT" ),
	        new KEYSet( 0, 2, "MOVE DOWN" ),
	        new KEYSet( 0, 3, "MOVE LEFT" ),
	        new KEYSet( 0, 4, "SLOW DRAW" ),
	        new KEYSet( 0, 7, "FAST DRAW" ),
	        new KEYSet( -1 )
	};
	
	
	/* Qix has no DIP switches */
	static DSW qix_dsw[] =
	{
		/* Fake a switch to configure CMOS. Pressing adv. test (F1) causes
		   machine to reset and show config screens, so we flip the adv. test
		   bit here to force it. */ /* JB 970525 */
		new DSW( 1, 0x01, "CONFIGURE CMOS", new String[] { "YES", "NO" } ),
		new DSW( -1 )
	};
	
	
	
	static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_M6809,
				1250000,		/* 1.25 Mhz */
				0,			/* memory region */
				readmem_cpu_data,	/* MemoryReadAddress */
				writemem_cpu_data,	/* MemoryWriteAddress */
				null,			/* IOReadPort */
				null,			/* IOWritePort */
				qix_data_interrupt,		/* JB 970825 - custom interrupt routine */
				1						/* JB 970825 - true interrupts per frame is only 1 */
			),
			new MachineCPU(
				CPU_M6809,
				1250000,		/* 1.25 Mhz */
				2,			/* memory region #2 */
				readmem_cpu_video, writemem_cpu_video, null, null,
				ignore_interrupt,
				1
			)
		},
		60,					/* frames per second */
		qix_init_machine,			/* init machine routine */ /* JB 970526 */
	
		/* video hardware */
		256, 256,				/* screen_width, screen_height */
		new rectangle( 0, 256-1, 0, 256-1 ),		        /* struct rectangle visible_area */
		null,				/* GfxDecodeInfo * */
		256,			/* total colors */
		0,			/* color table length */
		qix_vh_convert_color_prom,					/* convert color prom routine */
	
		VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
		null,					/* vh_init routine */
		qix_vh_start,				/* vh_start routine */ /* JB 970524 */
		qix_vh_stop,				/* vh_stop routine */ /* JB 970524 */
		qix_vh_screenrefresh,		        /* vh_update routine */	/* JB 970524 */
	
		/* sound hardware */
		null,					/* pointer to samples */
		null,					/* sh_init routine */
		null,					/* sh_start routine */
		null,					/* sh_stop routine */
		null					/* sh_update routine */
	);
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr qix_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000);/* 64k for code for the first CPU (Data) */
		ROM_LOAD( "u12", 0xC000, 0x800, 0x87bd3a11 );
		ROM_LOAD( "u13", 0xC800, 0x800, 0x85586b74 );
		ROM_LOAD( "u14", 0xD000, 0x800, 0x541d5c6f );
		ROM_LOAD( "u15", 0xD800, 0x800, 0xcbd010de );
		ROM_LOAD( "u16", 0xE000, 0x800, 0xf9da5efe );
		ROM_LOAD( "u17", 0xE800, 0x800, 0x14c09e2a );
		ROM_LOAD( "u18", 0xF000, 0x800, 0x22ae35fa );
		ROM_LOAD( "u19", 0xF800, 0x800, 0x1bf904ff );
	
		/* This is temporary space not really used but necessary because MAME
		 * always throws away memory region 1.
		 */
		ROM_REGION(0x800);
		ROM_LOAD( "u10",  0x0000, 0x0800,0 );	/* not needed - could be removed */
	
		ROM_REGION(0x10000);/* 64k for code for the second CPU (Video) */
		ROM_LOAD(  "u4", 0xC800, 0x800, 0x08bbfc51 );
		ROM_LOAD(  "u5", 0xD000, 0x800, 0xdd0f67b3 );
		ROM_LOAD(  "u6", 0xD800, 0x800, 0x37f8ce3c );
		ROM_LOAD(  "u7", 0xE000, 0x800, 0x733acfe0 );
		ROM_LOAD(  "u8", 0xE800, 0x800, 0xe1c7b84b );
		ROM_LOAD(  "u9", 0xF000, 0x800, 0xb662095a );
		ROM_LOAD( "u10", 0xF800, 0x800, 0x559ebf32 );
	ROM_END(); }}; 
	
	
	
	/* Loads high scores and all other CMOS settings */
	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name) 
	{
		/* get RAM pointer (data is in second CPU's memory region) */
		char[] RAM = Machine.memory_region[Machine.drv.cpu[1].memory_region];
                FILE f;


                if ((f = fopen(name,"rb")) != null)
                {
                        fread(RAM,0x8400,1,0x400,f);
                        fclose(f);
                }

                return 1;
	} };
	
	
	
	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name) 
	{
		/* get RAM pointer (data is in second CPU's memory region) */
            /* get RAM pointer (data is in second CPU's memory region) */
                char[] RAM = Machine.memory_region[Machine.drv.cpu[1].memory_region];
                FILE f;


                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x8400,1,0x400,f);
                        fclose(f);
                }
		
	} };
	
	
	
	public static GameDriver qix_driver = new GameDriver
	(
		"Qix",
		"qix",
		"JOHN BUTLER\nED MUELLER\nAARON GILES",
		machine_driver,
	
		qix_rom,
		null, null,   /* ROM decode and opcode decode functions */
		null,      /* Sample names */
	
		input_ports, null, trak_ports, qix_dsw, keys,
	
		null, null, null,   /* colors, palette, colortable */
		ORIENTATION_DEFAULT,
	
		hiload, hisave	       /* High score load and save */
	);
}
