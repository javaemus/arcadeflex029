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
 * using automatic conversion tool v0.03
 * converted at : 25-08-2011 13:01:12
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
import static vidhrdw.wow.*;
import static sndhrdw.wow.*;
import static machine.wow.*;
import static mame.inptport.*;
import static mame.memoryH.*;
public class wow
{
		
	/*
	 * Default Settings
	 */
	
	static MemoryReadAddress readmem[] =
	{
	    new MemoryReadAddress( 0xD000, 0xDfff, MRA_RAM ),
		new MemoryReadAddress( 0x4000, 0x7fff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new MemoryReadAddress( 0x8000, 0xcfff, MRA_ROM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
	    new MemoryWriteAddress( 0xD000, 0xDfff, MWA_RAM ),
		new MemoryWriteAddress( 0x4000, 0x7fff, wow_videoram_w, wow_videoram, videoram_size ),	/* ASG */
		new MemoryWriteAddress( 0x0000, 0x3fff, wow_magicram_w ),
		new MemoryWriteAddress( 0x8000, 0xcfff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	static IOReadPort readport[] =
	{
		new IOReadPort( 0x08, 0x08, wow_intercept_r ),
	    new IOReadPort( 0x0E, 0x0E, wow_video_retrace_r ),
		new IOReadPort( 0x10, 0x10, input_port_0_r ),
		new IOReadPort( 0x11, 0x11, input_port_1_r ),
	  	new IOReadPort( 0x12, 0x12, wow_port_2_r ),
		new IOReadPort( 0x13, 0x13, input_port_3_r ),
	    new IOReadPort( 0x17, 0x17, wow_speech_r ),				/* Actually a Write! */
		new IOReadPort( -1 )	/* end of table */
	};
	
	static IOWritePort writeport[] =
	{
		new IOWritePort( 0x0d, 0x0d, interrupt_vector_w ),
		new IOWritePort( 0x19, 0x19, wow_magic_expand_color_w ),
		new IOWritePort( 0x0c, 0x0c, wow_magic_control_w ),
		new IOWritePort( 0x78, 0x7e, wow_pattern_board_w ),
	    new IOWritePort( 0x0F, 0x0F, wow_interrupt_w ),
	    new IOWritePort( 0x0E, 0x0E, wow_interrupt_enable_w ),
	    new IOWritePort( 0x00, 0x07, colour_register_w ),
	    new IOWritePort( 0x09, 0x09, colour_split_w ),
		new IOWritePort( -1 )	/* end of table */
	};
	
	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { OSD_KEY_3, OSD_KEY_4, OSD_KEY_F1, OSD_KEY_F2,
					OSD_KEY_5, OSD_KEY_1, OSD_KEY_2, OSD_KEY_6 }
		),
		new InputPort(	/* IN1 */
			0xff,
			new int[] { OSD_KEY_E, OSD_KEY_D, OSD_KEY_S, OSD_KEY_F, OSD_KEY_X, OSD_KEY_G, 0, 0 }
		),
		new InputPort(	/* IN2 */
			0xff,
			new int[] { OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_ALT, OSD_KEY_CONTROL, 0, OSD_KEY_O  }
		),
		new InputPort(	/* DSW */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};
	
	static TrakPort trak_ports[] =
	{
	        new TrakPort( -1 )
	};
	
	static KEYSet keys[] =
	{
	        new KEYSet( 2, 0, "MOVE UP" ),
	        new KEYSet( 2, 2, "MOVE LEFT"  ),
	        new KEYSet( 2, 3, "MOVE RIGHT" ),
	        new KEYSet( 2, 1, "MOVE DOWN" ),
	        new KEYSet( 2, 5, "FIRE1" ),
	        new KEYSet( 2, 4, "FIRE2" ),
	        new KEYSet( -1 )
	};
	
	static DSW dsw[] =
	{
		new DSW( 3, 0x10, "LIVES", new String[] { "3 7", "2 5" }, 1 ),
		new DSW( 3, 0x20, "BONUS", new String[] { "4TH LEVEL", "3RD LEVEL" }, 1 ),
	    new DSW( 3, 0x40, "PAYMENT", new String[] { "FREE", "COIN" }, 1 ),
		new DSW( 3, 0x80, "DEMO SOUNDS", new String[] { "OFF", "ON" }, 1 ),
		new DSW( -1 )
	};
	
	static char palette[] =
	{
	     0x00,0x00,0x00,     /* 0 */
	     0x08,0x08,0x08,	/* Gorf back */
	     0x10,0x10,0x10,	/* Gorf back */
	     0x18,0x18,0x18,	/* Gorf back */
	     0x20,0x20,0x20,	/* Gorf back & Stars */
	     0x50,0x50,0x50,    /* Gorf Stars */
	     0x80,0x80,0x80,    /* Gorf Stars */
	     0xA0,0xA0,0xA0,    /* Gorf Stars */
	     0x00,0x00,0x28,
	     0x00,0x00,0x59,
	     0x00,0x00,0x8A,
	     0x00,0x00,0xBB,
	     0x00,0x00,0xEC,
	     0x00,0x00,0xFF,
	     0x00,0x00,0xFF,
	     0x00,0x00,0xFF,
	     0x28,0x00,0x28,     /* 10 */
	     0x2F,0x00,0x52,
	     0x36,0x00,0x7C,
	     0x3D,0x00,0xA6,
	     0x44,0x00,0xD0,
	     0x4B,0x00,0xFA,
	     0x52,0x00,0xFF,
	     0x59,0x00,0xFF,
	     0x28,0x00,0x28,
	     0x36,0x00,0x4B,
	     0x44,0x00,0x6E,
	     0x52,0x00,0x91,
	     0x60,0x00,0xB4,
	     0x6E,0x00,0xD7,
	     0x7C,0x00,0xFA,
	     0x8A,0x00,0xFF,
	     0x28,0x00,0x28,     /* 20 */
	     0x3D,0x00,0x44,
	     0x52,0x00,0x60,
	     0x67,0x00,0x7C,
	     0x7C,0x00,0x98,
	     0x91,0x00,0xB4,
	     0xA6,0x00,0xD0,
	     0xBB,0x00,0xEC,
	     0x28,0x00,0x28,
	     0x4B,0x00,0x3D,
	     0x6E,0x00,0x52,
	     0x91,0x00,0x67,
	     0xB4,0x00,0x7C,
	     0xD7,0x00,0x91,
	     0xFA,0x00,0xA6,
	     0xFF,0x00,0xBB,
	     0x28,0x00,0x28,     /* 30 */
	     0x4B,0x00,0x36,
	     0x6E,0x00,0x44,
	     0x91,0x00,0x52,
	     0xB4,0x00,0x60,
	     0xD7,0x00,0x6E,
	     0xFA,0x00,0x7C,
	     0xFF,0x00,0x8A,
	     0x28,0x00,0x28,
	     0x52,0x00,0x2F,
	     0x7C,0x00,0x36,
	     0xA6,0x00,0x3D,
	     0xD0,0x00,0x44,
	     0xFA,0x00,0x4B,
	     0xFF,0x00,0x52,
	     0xFF,0x00,0x59,
	     0x28,0x00,0x00,     /* 40 */
	     0x59,0x00,0x00,
	     0x8A,0x00,0x00,
	     0xBB,0x00,0x00,
	     0xEC,0x00,0x00,
	     0xFF,0x00,0x00,
	     0xFF,0x00,0x00,
	     0xFF,0x00,0x00,
	     0x28,0x00,0x00,
	     0x59,0x00,0x00,
	     0xE0,0x00,0x00,	/* Gorf (8a,00,00) */
	     0xBB,0x00,0x00,
	     0xEC,0x00,0x00,
	     0xFF,0x00,0x00,
	     0xFF,0x00,0x00,
	     0xFF,0x00,0x00,
	     0x28,0x00,0x00,     /* 50 */
	     0x80,0x00,0x00,	/* WOW (59,00,00) */
	     0xC0,0x00,0x00,	/* WOW (8a,00,00) */
	     0xF0,0x00,0x00,	/* WOW (bb,00,00) */
	     0xEC,0x00,0x00,
	     0xFF,0x00,0x00,
	     0xFF,0x00,0x00,
	     0xFF,0x00,0x00,
	     0xC0,0x00,0x00,	/* Seawolf 2 */
	     0x59,0x00,0x00,
	     0x8A,0x00,0x00,
	     0xBB,0x00,0x00,
	     0xEC,0x00,0x00,
	     0xFF,0x00,0x00,
	     0xFF,0x00,0x00,
	     0xFF,0x00,0x00,
	     0x28,0x28,0x00,     /* 60 */
	     0x59,0x2F,0x00,
	     0x8A,0x36,0x00,
	     0xBB,0x3D,0x00,
	     0xEC,0x44,0x00,
	     0xFF,0xC0,0x00,	/* Gorf (ff,4b,00) */
	     0xFF,0x52,0x00,
	     0xFF,0x59,0x00,
	     0x28,0x28,0x00,
	     0x59,0x3D,0x00,
	     0x8A,0x52,0x00,
	     0xBB,0x67,0x00,
	     0xEC,0x7C,0x00,
	     0xFF,0x91,0x00,
	     0xFF,0xA6,0x00,
	     0xFF,0xBB,0x00,
	     0x28,0x28,0x00,     /* 70 */
	     0x59,0x3D,0x00,
	     0x8A,0x52,0x00,
	     0xBB,0x67,0x00,
	     0xEC,0x7C,0x00,
	     0xFF,0xE0,0x00,	/* Gorf (ff,91,00) */
	     0xFF,0xA6,0x00,
	     0xFF,0xBB,0x00,
	     0x28,0x28,0x00,
	     0x52,0x44,0x00,
	     0x7C,0x60,0x00,
	     0xA6,0x7C,0x00,
	     0xD0,0x98,0x00,
	     0xFA,0xB4,0x00,
	     0xFF,0xD0,0x00,
	     0xFF,0xEC,0x00,
	     0x28,0x28,0x00,     /* 80 */
	     0x4B,0x52,0x00,
	     0x6E,0x7C,0x00,
	     0x91,0xA6,0x00,
	     0xB4,0xD0,0x00,
	     0xD7,0xFA,0x00,
	     0xFA,0xFF,0x00,
	     0xFF,0xFF,0x00,
	     0x28,0x28,0x00,
	     0x4B,0x59,0x00,
	     0x6E,0x8A,0x00,
	     0x91,0xBB,0x00,
	     0xB4,0xEC,0x00,
	     0xD7,0xFF,0x00,
	     0xFA,0xFF,0x00,
	     0xFF,0xFF,0x00,
	     0x28,0x28,0x00,     /* 90 */
	     0x4B,0x59,0x00,
	     0x6E,0x8A,0x00,
	     0x91,0xBB,0x00,
	     0xB4,0xEC,0x00,
	     0xD7,0xFF,0x00,
	     0xFA,0xFF,0x00,
	     0xFF,0xFF,0x00,
	     0x28,0x28,0x00,
	     0x52,0x59,0x00,
	     0x7C,0x8A,0x00,
	     0xA6,0xBB,0x00,
	     0xD0,0xEC,0x00,
	     0xFA,0xFF,0x00,
	     0xFF,0xFF,0x00,
	     0xFF,0xFF,0x00,
	     0x28,0x28,0x00,     /* A0 */
	     0x4B,0x59,0x00,
	     0x6E,0x8A,0x00,
	     0x91,0xBB,0x00,
	     0x00,0x00,0xFF,	/* Gorf (b4,ec,00) */
	     0xD7,0xFF,0x00,
	     0xFA,0xFF,0x00,
	     0xFF,0xFF,0x00,
	     0x00,0x28,0x28,
	     0x00,0x59,0x2F,
	     0x00,0x8A,0x36,
	     0x00,0xBB,0x3D,
	     0x00,0xEC,0x44,
	     0x00,0xFF,0x4B,
	     0x00,0xFF,0x52,
	     0x00,0xFF,0x59,
	     0x00,0x28,0x28,     /* B0 */
	     0x00,0x59,0x36,
	     0x00,0x8A,0x44,
	     0x00,0xBB,0x52,
	     0x00,0xEC,0x60,
	     0x00,0xFF,0x6E,
	     0x00,0xFF,0x7C,
	     0x00,0xFF,0x8A,
	     0x00,0x28,0x28,
	     0x00,0x52,0x3D,
	     0x00,0x7C,0x52,
	     0x00,0xA6,0x67,
	     0x00,0xD0,0x7C,
	     0x00,0xFA,0x91,
	     0x00,0xFF,0xA6,
	     0x00,0xFF,0xBB,
	     0x00,0x28,0x28,     /* C0 */
	     0x00,0x4B,0x44,
	     0x00,0x6E,0x60,
	     0x00,0x91,0x7C,
	     0x00,0xB4,0x98,
	     0x00,0xD7,0xB4,
	     0x00,0xFA,0xD0,
	     0x00,0x00,0x00,	/* WOW Background */
	     0x00,0x28,0x28,
	     0x00,0x4B,0x4B,
	     0x00,0x6E,0x6E,
	     0x00,0x91,0x91,
	     0x00,0xB4,0xB4,
	     0x00,0xD7,0xD7,
	     0x00,0xFA,0xFA,
	     0x00,0xFF,0xFF,
	     0x00,0x28,0x28,     /* D0 */
	     0x00,0x4B,0x52,
	     0x00,0x6E,0x7C,
	     0x00,0x91,0xA6,
	     0x00,0xB4,0xD0,
	     0x00,0xD7,0xFA,
	     0x00,0xFA,0xFF,
	     0x00,0xFF,0xFF,
	     0x00,0x00,0x30,	/* Gorf (00,00,28)   also   */
	     0x00,0x00,0x48,    /* Gorf (00,00,59)   used   */
	     0x00,0x00,0x60,	/* Gorf (00,00,8a)    by    */
	     0x00,0x00,0x78,	/* Gorf (00,00,bb) seawolf2 */
	     0x00,0x00,0x90, 	/* seawolf2 */
	     0x00,0x00,0xFF,
	     0x00,0x00,0xFF,
	     0x00,0x00,0xFF,
	     0x00,0x00,0x28,     /* E0 */
	     0x00,0x00,0x52,
	     0x00,0x00,0x7C,
	     0x00,0x00,0xA6,
	     0x00,0x00,0xD0,
	     0x00,0x00,0xFA,
	     0x00,0x00,0xFF,
	     0x00,0x00,0xFF,
	     0x00,0x00,0x28,
	     0x00,0x00,0x4B,
	     0x00,0x00,0x6E,
	     0x00,0x00,0x91,
	     0x00,0x00,0xB4,
	     0x00,0x00,0xD7,
	     0x00,0x00,0xFA,
	     0x00,0x00,0xFF,
	     0x00,0x00,0x28,     /* F0 */
	     0x00,0x00,0x44,
	     0x00,0x00,0x60,
	     0x00,0x00,0x7C,
	     0x00,0x00,0x98,
	     0x00,0x00,0xB4,
	     0x00,0x00,0xD0,
	     0x00,0x00,0xEC,
	     0x00,0x00,0x28,
	     0x00,0x00,0x3D,
	     0x00,0x00,0x52,
	     0x00,0x00,0x67,
	     0x00,0x00,0x7C,
	     0x00,0x00,0x91,
	     0x00,0x00,0xA6,
	     0x00,0x00,0xBB,
	};
	
	static final int BLACK = 0, YELLOW = 1, BLUE = 2, RED = 3, WHITE = 4;
	
	static char colortable[] =
	{
		BLACK,YELLOW,BLUE,RED,
		BLACK,WHITE,BLACK,RED	/* not used by the game, here only for the dip switch menu */
	};
	
	
	/****************************************************************************
	 * Wizard of Wor
	 ****************************************************************************/
	
	static RomLoadPtr wow_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000);/* 64k for code */
		ROM_LOAD( "wow.x1", 0x0000, 0x1000, 0x222baf9b );
		ROM_LOAD( "wow.x2", 0x1000, 0x1000, 0xccb3fafb );
		ROM_LOAD( "wow.x3", 0x2000, 0x1000, 0xe4a6665e );
		ROM_LOAD( "wow.x4", 0x3000, 0x1000, 0xf3659d69 );
		ROM_LOAD( "wow.x5", 0x8000, 0x1000, 0xda26e8d8 );
		ROM_LOAD( "wow.x6", 0x9000, 0x1000, 0x550a3720 );
		ROM_LOAD( "wow.x7", 0xa000, 0x1000, 0x0ef6502c );
	/*	ROM_LOAD( "wow.x8", 0xc000, 0x1000, ? );here would go the foreign language ROM */
	ROM_END(); }}; 
	
	static InputPort wow_input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { OSD_KEY_3, OSD_KEY_4, 0, OSD_KEY_F2,
					0, OSD_KEY_1, OSD_KEY_2, 0}
		),
		new InputPort(	/* IN1 */
			0xef,
			new int[] { OSD_KEY_E, OSD_KEY_D, OSD_KEY_S, OSD_KEY_F, OSD_KEY_X, OSD_KEY_G, 0, 0 }
		),
		new InputPort(	/* IN2 */
			0xef,
			new int[] { OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_ALT, OSD_KEY_CONTROL, 0, 0}
		),
		new InputPort(	/* DSW */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};
	
	static DSW wow_dsw[] =
	{
		new DSW( 3, 0x10, "LIVES", new String[] { "3 7", "2 5" }, 1 ),
		new DSW( 3, 0x20, "BONUS", new String[] { "4TH LEVEL", "3RD LEVEL" }, 1 ),
	    new DSW( 3, 0x40, "PAYMENT", new String[] { "FREE", "COIN" }, 1 ),
		new DSW( 3, 0x80, "DEMO SOUNDS", new String[] { "OFF", "ON" }, 1 ),
	    new DSW( 0, 0x80, "FLIP SCREEN", new String[] { "YES", "NO" }, 1 ),
	/* 	new DSW( 3, 0x08, "LANGUAGE", new String[] { "FOREIGN", "ENGLISH" }, 1 ), */
		new DSW( -1 )
	};
	
	static MachineDriver wow_machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				readmem,writemem,readport,writeport,
				wow_interrupt,224
			)
		},
		60,
		null,
	
		/* video hardware */
		320, 204, new rectangle( 0, 320-1, 0, 204-1 ),
		null,	/* no gfxdecodeinfo - bitmapped display */
		sizeof(palette)/3,sizeof(colortable),
		null,
	
		VIDEO_TYPE_RASTER,
		null,
		generic_vh_start,
		generic_vh_stop,
		wow_vh_screenrefresh,
	
		/* sound hardware */
		null,
	    null,             				/* Initialise audio hardware */
	    wow_sh_start,   			/* Start audio  */
	    wow_sh_stop,     			/* Stop audio   */
	    wow_sh_update               /* Update audio */
	);
	
	static HiscoreLoadPtr wow_hiload = new HiscoreLoadPtr() { public int handler() 
	{
		/* check if the hi score table has already been initialized */
	/*TOFIX        if (memcmp(RAM,0xD004,new char[] {0x00,0x00},2) == 0)
		{
			FILE f;
	
	
			if ((f = fopen(name,"rb")) != null)
			{
	            fread(RAM,0xD004,1,20,f);
				/* stored twice in memory??? */
	/*TOFIX			memcpy(RAM,0xD304,RAM,0xD004,20);
				fclose(f);
			}
	
			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };
	
	
	
	static HiscoreSavePtr wow_hisave = new HiscoreSavePtr() { public void handler() 
	{
		FILE f;
	
	/*TOFIX	if ((f = fopen(name,"wb")) != null)
		{
	        fwrite(RAM,0xD004,1,20,f);
			fclose(f);
		}*/
	
	} };
	
	public static GameDriver wow_driver = new GameDriver
	(
	    "Wizard of Wor",
		"wow",
	    "NICOLA SALMORIA (Mame Driver)\nSTEVE SCAVONE (Info and Code)\nMIKE COATES (Extra Code)\nMIKE BALFOUR (High Scores)",
		wow_machine_driver,
	
		wow_rom,
		null, null,
		null,
	
		wow_input_ports, null, trak_ports, wow_dsw, keys,
	
		null, palette, colortable,
		ORIENTATION_DEFAULT,
	
		wow_hiload, wow_hisave
	);
	
	/****************************************************************************
	 * Robby Roto
	 ****************************************************************************/
	
	static RomLoadPtr robby_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000);/* 64k for code */
		ROM_LOAD( "rotox1.bin",  0x0000, 0x1000, 0x90debdc2 );
		ROM_LOAD( "rotox2.bin",  0x1000, 0x1000, 0xfa125fb2 );
		ROM_LOAD( "rotox3.bin",  0x2000, 0x1000, 0x88e6e5bc );
		ROM_LOAD( "rotox4.bin",  0x3000, 0x1000, 0x111241ea );
		ROM_LOAD( "rotox5.bin",  0x8000, 0x1000, 0x3b7e01f0 );
		ROM_LOAD( "rotox6.bin",  0x9000, 0x1000, 0x478421ee );
		ROM_LOAD( "rotox7.bin",  0xa000, 0x1000, 0xf9ac77a0 );
		ROM_LOAD( "rotox8.bin",  0xb000, 0x1000, 0x799f8a3d );
	  	ROM_LOAD( "rotox9.bin",  0xc000, 0x1000, 0xaa9f62f5 );
		ROM_LOAD( "rotox10.bin", 0xd000, 0x1000, 0x55a239fa );
	ROM_END(); }}; 
	
	static MemoryReadAddress robby_readmem[] =
	{
		new MemoryReadAddress( 0xe000, 0xffff, MRA_RAM ),
		new MemoryReadAddress( 0x4000, 0x7fff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new MemoryReadAddress( 0x8000, 0xdfff, MRA_ROM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress robby_writemem[] =
	{
		new MemoryWriteAddress( 0xe000, 0xffff, MWA_RAM ),
		new MemoryWriteAddress( 0x4000, 0x7fff, wow_videoram_w, wow_videoram, videoram_size ),
		new MemoryWriteAddress( 0x0000, 0x3fff, wow_magicram_w ),
		new MemoryWriteAddress( 0x8000, 0xdfff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	
	static MachineDriver robby_machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				robby_readmem,robby_writemem,readport,writeport,
				wow_interrupt,224
			)
		},
		60,
		null,
	
		/* video hardware */
		320, 204, new rectangle( 1, 320-1, 0, 204-1 ),
		null,	/* no gfxdecodeinfo - bitmapped display */
		sizeof(palette)/3,sizeof(colortable),
		null,
	
		VIDEO_TYPE_RASTER,
		null,
		generic_vh_start,
		generic_vh_stop,
		wow_vh_screenrefresh,
	
		/* sound hardware */
		null,
		null,
		null,
		null,
		null
	);
	
	static HiscoreLoadPtr robby_hiload = new HiscoreLoadPtr() { public int handler() 
	{
		/* check if the hi score table has already been initialized */
	  /*TOFIX      if ((memcmp(RAM,0xE13B,new char[] {0x10,0x27},2) == 0) &&
			(memcmp(RAM,0xE1E4,"COCK",4) == 0))
		{
			FILE f;
	
	
			if ((f = fopen(name,"rb")) != null)
			{
                                fread(RAM,0xE13B,1,0xAD,f);
				/* appears twice in memory??? */
	/*TOFIX			memcpy(RAM,0xE33B,RAM,0xE13B,0xAD);
				fclose(f);
			}
	
			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };
	
	
	
	static HiscoreSavePtr robby_hisave = new HiscoreSavePtr() { public void handler() 
	{
		FILE f;
	
		/*TOFIXif ((f = fopen(name,"wb")) != null)
		{
	        fwrite(RAM,0xE13B,1,0xAD,f);
			fclose(f);
		}*/
	
	} };
	
	public static GameDriver robby_driver = new GameDriver
	(
	    "Robby Roto",
		"robby",
	    "NICOLA SALMORIA (Mame Driver)\nSTEVE SCAVONE (Info and Code)\nMIKE COATES (Extra Code)\nMIKE BALFOUR (High Scores)",
		robby_machine_driver,
	
		robby_rom,
		null, null,
		null,
	
		input_ports, null, trak_ports, dsw, keys,
	
		null, palette, colortable,
		ORIENTATION_DEFAULT,
	
		robby_hiload, robby_hisave
	);
	
	/****************************************************************************
	 * Gorf
	 ****************************************************************************/
	
	static RomLoadPtr gorf_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000);/* 64k for code */
		ROM_LOAD( "gorf-a.bin", 0x0000, 0x1000, 0x4dbb1c41 );
		ROM_LOAD( "gorf-b.bin", 0x1000, 0x1000, 0xed7b0427 );
		ROM_LOAD( "gorf-c.bin", 0x2000, 0x1000, 0xe101f49b );
		ROM_LOAD( "gorf-d.bin", 0x3000, 0x1000, 0xd12d3e47 );
		ROM_LOAD( "gorf-e.bin", 0x8000, 0x1000, 0x07ab966d );
		ROM_LOAD( "gorf-f.bin", 0x9000, 0x1000, 0xf7d14e9b );
		ROM_LOAD( "gorf-g.bin", 0xa000, 0x1000, 0x0ddd505f );
		ROM_LOAD( "gorf-h.bin", 0xb000, 0x1000, 0x5e488f10 );
	ROM_END(); }}; 
	
	static IOReadPort Gorf_readport[] =
	{
		new IOReadPort( 0x08, 0x08, wow_intercept_r ),
	    new IOReadPort( 0x0E, 0x0E, wow_video_retrace_r ),
		new IOReadPort( 0x10, 0x10, input_port_0_r ),
		new IOReadPort( 0x11, 0x11, input_port_1_r ),
		new IOReadPort( 0x12, 0x12, wow_port_2_r ),
		new IOReadPort( 0x13, 0x13, input_port_3_r ),
	    new IOReadPort( 0x15, 0x16, Gorf_IO_r ),					/* Actually a Write! */
	    new IOReadPort( 0x17, 0x17, wow_speech_r ),				/* Actually a Write! */
		new IOReadPort( -1 )	/* end of table */
	};
	
	static MemoryReadAddress Gorf_readmem[] =
	{
	    new MemoryReadAddress( 0xd000, 0xd0a4, MRA_RAM ),
	    new MemoryReadAddress( 0xd0a5, 0xd0a5, gorf_timer_r ),
	    new MemoryReadAddress( 0xd0a6, 0xdfff, MRA_RAM ),
	    new MemoryReadAddress( 0xd9d5, 0xdfff, MRA_RAM ),
		new MemoryReadAddress( 0x4000, 0x7fff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new MemoryReadAddress( 0x8000, 0xcfff, MRA_ROM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static InputPort Gorf_input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { OSD_KEY_3, OSD_KEY_4, OSD_KEY_F1, OSD_KEY_F2,
					OSD_KEY_1, OSD_KEY_2, 0, 0 }
		),
		new InputPort(	/* IN1 */
			0x1f,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* IN2 */
			0x9f,
			new int[] { OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_CONTROL, 0, 0, 0 }
		),
		new InputPort(	/* DSW */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};
	
	static DSW Gorf_dsw[] =
	{
		new DSW( 3, 0x10, "LIVES", new String[] { "3 6", "2 4" }, 1 ),
		new DSW( 3, 0x20, "BONUS", new String[] { "MISSION 5", "NONE" }, 1 ),
	    new DSW( 3, 0x40, "PAYMENT", new String[] { "FREE", "COIN" }, 1 ),
		new DSW( 3, 0x80, "DEMO SOUNDS", new String[] { "OFF", "ON" }, 1 ),
		new DSW( 0, 0x40, "JU1", new String[] { "ON", "OFF" }, 1 ),
	    new DSW( 0, 0x80, "FLIP SCREEN", new String[] { "YES", "NO" }, 1 ),
		new DSW( 0, 0x08, "SLAM TILT", new String[] { "ON", "OFF" }, 1 ),
		new DSW( -1 )
	};
	
	static KEYSet Gorf_keys[] =
	{
	        new KEYSet( 2, 0, "MOVE UP" ),
	        new KEYSet( 2, 2, "MOVE LEFT"  ),
	        new KEYSet( 2, 3, "MOVE RIGHT" ),
	        new KEYSet( 2, 1, "MOVE DOWN" ),
	        new KEYSet( 2, 4, "FIRE" ),
	        new KEYSet( -1 )
	};
	
	static MachineDriver gorf_machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				Gorf_readmem,writemem,Gorf_readport,writeport,		/* ASG */
				gorf_interrupt,256
			)
		},
		60,
		null,
	
		/* video hardware */
		204, 320, new rectangle( 0, 204-1, 0, 320-1 ),	/* ASG */
		null,	/* no gfxdecodeinfo - bitmapped display */
		sizeof(palette)/3,sizeof(colortable),
		null,
	
		VIDEO_TYPE_RASTER,
		null,
		gorf_vh_start,
		generic_vh_stop,
		gorf_vh_screenrefresh,
	
		/* sound hardware */
		null,
	    null,            				/* Initialise audio hardware */
	    wow_sh_start,    			/* Start audio  */
	    wow_sh_stop,     			/* Stop audio   */
	    wow_sh_update               /* Update audio */
	);
	
	static HiscoreLoadPtr gorf_hiload = new HiscoreLoadPtr() { public int handler() 
	{
		/* check if the hi score table has already been initialized */
 /*TOFIX        	        if ((RAM[0xD00B]==0xFF) && (RAM[0xD03D]==0x33))
		{
			FILE f;
	
	
			if ((f = fopen(name,"rb")) != null)
			{
	            fread(RAM,0xD010,1,0x22,f);
				fclose(f);
			}
	
			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };
	
	
	
	static HiscoreSavePtr gorf_hisave = new HiscoreSavePtr() { public void handler() 
	{
		FILE f;
	
 /*TOFIX        		if ((f = fopen(name,"wb")) != null)
		{
	        fwrite(RAM,0xD010,1,0x22,f);
			fclose(f);
		}*/
	
	} };
	
	public static GameDriver gorf_driver = new GameDriver
	(
	    "Gorf",
		"gorf",
	    "NICOLA SALMORIA (Mame Driver)\nSTEVE SCAVONE (Info and Code)\nMIKE COATES (Game Support)\nMIKE BALFOUR (High Scores)",
		gorf_machine_driver,
	
		gorf_rom,
		null, null,
		null,
	
		Gorf_input_ports, null, trak_ports, Gorf_dsw, Gorf_keys,
	
		null, palette, colortable,
		ORIENTATION_DEFAULT,
	
		gorf_hiload, gorf_hisave
	);
	
	/****************************************************************************
	 * Space Zap
	 ****************************************************************************/
	
	static RomLoadPtr spacezap_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000);/* 64k for code */
		ROM_LOAD( "0662.01", 0x0000, 0x1000, 0xaf642ac8 );
		ROM_LOAD( "0663.xx", 0x1000, 0x1000, 0x0cb6228e );
		ROM_LOAD( "0664.xx", 0x2000, 0x1000, 0x2986a6d6 );
		ROM_LOAD( "0665.xx", 0x3000, 0x1000, 0xb3037b39 );
	ROM_END(); }}; 
	
	static InputPort spacezap_input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { OSD_KEY_3, OSD_KEY_4, OSD_KEY_F1, OSD_KEY_F2,
					0, OSD_KEY_1, OSD_KEY_2, 0 }
		),
		new InputPort(	/* IN1 */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* IN2 */
			0xff,
			new int[] { OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_CONTROL, 0, 0, 0 }
		),
		new InputPort(	/* DSW */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};
	
	static KEYSet spacezap_keys[] =
	{
	        new KEYSet( 2, 0, "MOVE UP" ),
	        new KEYSet( 2, 2, "MOVE LEFT"  ),
	        new KEYSet( 2, 3, "MOVE RIGHT" ),
	        new KEYSet( 2, 1, "MOVE DOWN" ),
	        new KEYSet( 2, 4, "FIRE" ),
	        new KEYSet( -1 )
	};
	
	static DSW spacezap_dsw[] =
	{
		new DSW( 3, 0x20, "BONUS", new String[] { "4TH LEVEL", "3RD LEVEL" }, 1 ),
		new DSW( 3, 0x80, "DEMO SOUNDS", new String[] { "OFF", "ON" }, 1 ),
		new DSW( -1 )
	};
	
	static MachineDriver spacezap_machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				readmem,writemem,readport,writeport,
				wow_interrupt,204
			)
		},
		60,
		null,
	
		/* video hardware */
		320, 204, new rectangle( 0, 320-1, 0, 204-1 ),
		null,	/* no gfxdecodeinfo - bitmapped display */
		sizeof(palette)/3,sizeof(colortable),
		null,
	
		VIDEO_TYPE_RASTER,
		null,
		generic_vh_start,
		generic_vh_stop,
		wow_vh_screenrefresh,
	
		/* sound hardware */
		null,
		null,
		null,
		null,
		null
	);
	
	static HiscoreLoadPtr spacezap_hiload = new HiscoreLoadPtr() { public int handler() 
	{
		/* check if memory has already been initialized */
 /*TOFIX        	        if (memcmp(RAM,0xD024,new char[] {0x01,0x01},2) == 0)
		{
			FILE f;
	
	
			if ((f = fopen(name,"rb")) != null)
			{
	            fread(RAM,0xD01D,1,6,f);
				/* Appears twice in memory??? */
 /*TOFIX        				memcpy(RAM,0xD041,RAM,0xD01D,6);
				fclose(f);
			}
	
			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };
	
	
	
	static HiscoreSavePtr spacezap_hisave = new HiscoreSavePtr() { public void handler() 
	{
		FILE f;
	
 /*TOFIX        		if ((f = fopen(name,"wb")) != null)
		{
	        fwrite(RAM,0xD01D,1,6,f);
			fclose(f);
		}*/
	
	} };
	
	public static GameDriver spacezap_driver = new GameDriver
	(
	    "Space Zap",
		"spacezap",
	    "NICOLA SALMORIA (Mame Driver)\nSTEVE SCAVONE (Info and Code)\nMIKE COATES (Game Support)\nMIKE BALFOUR (High Scores)",
		spacezap_machine_driver,
	
		spacezap_rom,
		null, null,
		null,
	
		spacezap_input_ports, null, trak_ports, spacezap_dsw, spacezap_keys,
	
		null, palette, colortable,
		ORIENTATION_DEFAULT,
	
		spacezap_hiload, spacezap_hisave
	);
	
	/****************************************************************************
	 * Seawolf II
	 ****************************************************************************/
	
	static RomLoadPtr seawolf2_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000);/* 64k for code */
		ROM_LOAD( "sw2x1.bin", 0x0000, 0x0800, 0x8a11d667 );
		ROM_LOAD( "sw2x2.bin", 0x0800, 0x0800, 0x1d7d50a1 );
		ROM_LOAD( "sw2x3.bin", 0x1000, 0x0800, 0x60364550 );
		ROM_LOAD( "sw2x4.bin", 0x1800, 0x0800, 0xd28a15b2 );
	ROM_END(); }}; 
	
	static MemoryReadAddress seawolf2_readmem[] =
	{
		new MemoryReadAddress( 0xc000, 0xcfff, MRA_RAM ),
		new MemoryReadAddress( 0x4000, 0x7fff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress seawolf2_writemem[] =
	{
		new MemoryWriteAddress( 0xc000, 0xcfff, MWA_RAM ),
		new MemoryWriteAddress( 0x4000, 0x7fff, wow_videoram_w, wow_videoram, videoram_size ),
		new MemoryWriteAddress( 0x0000, 0x3fff, wow_magicram_w ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	static InputPort seawolf2_input_ports[] =
	{
		new InputPort(	/* IN0 */
			0x0,
			new int[] { 0, 0, 0, 0, 0, 0, 0, OSD_KEY_CONTROL }
		),
		new InputPort(	/* IN1 */
			0x0,
			new int[] { OSD_KEY_Q, OSD_KEY_W, OSD_KEY_E, OSD_KEY_R, OSD_KEY_T, OSD_KEY_Y, OSD_KEY_U, OSD_KEY_I }
		),
		new InputPort(	/* IN2 */
			0x0,
			new int[] { OSD_KEY_3, OSD_KEY_1, OSD_KEY_2, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW */
			0xFF,
			new int[] { OSD_KEY_Z, OSD_KEY_X, OSD_KEY_C, OSD_KEY_V, OSD_KEY_B, OSD_KEY_N, OSD_KEY_M, OSD_KEY_L }
		),
		new InputPort( -1 )	/* end of table */
	};
	
	static DSW seawolf2_dsw[] =
	{
		new DSW( 0, 0x40, "LANGUAGE 1", new String[] { "LANGUAGE 2", "FRENCH" }, 1 ),
		new DSW( 2, 0x08, "LANGUAGE 2", new String[] { "ENGLISH", "GERMAN" }, 1 ),
	    new DSW( 3, 0x06, "PLAY TIME", new String[] { "70", "60", "50", "40" } ),
		new DSW( 3, 0x80, "TEST MODE", new String[] { "ON", "OFF" }, 1 ),
		new DSW( -1 )
	};
	
	static IOReadPort seawolf2_readport[] =
	{
		new IOReadPort( 0x08, 0x08, wow_intercept_r ),
	    new IOReadPort( 0x0E, 0x0E, wow_video_retrace_r ),
		new IOReadPort( 0x10, 0x10, seawolf2_controller2_r ),
		new IOReadPort( 0x11, 0x11, seawolf2_controller1_r ),
		new IOReadPort( 0x12, 0x12, input_port_2_r ),
		new IOReadPort( 0x13, 0x13, input_port_3_r ),
		new IOReadPort( -1 )	/* end of table */
	};
	
	static KEYSet seawolf2_keys[] =
	{
	        new KEYSet( 0, 0, "PLAYER 1 LEFT"  ),
	        new KEYSet( 0, 1, "PLAYER 1 RIGHT" ),
	        new KEYSet( 0, 7, "PLAYER 1 FIRE" ),
	        new KEYSet( 1, 0, "PLAYER 2 LEFT"  ),
	        new KEYSet( 1, 1, "PLAYER 2 RIGHT" ),
	        new KEYSet( 1, 7, "PLAYER 2 FIRE" ),
	        new KEYSet( -1 )
	};
	
	static MachineDriver seawolf_machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				seawolf2_readmem,seawolf2_writemem,seawolf2_readport,writeport,
				wow_interrupt,230
			)
		},
		60,
		null,
	
		/* video hardware */
		320, 204, new rectangle( 1, 320-1, 0, 204-1 ),
		null,	/* no gfxdecodeinfo - bitmapped display */
		sizeof(palette)/3,sizeof(colortable),
		null,
	
		VIDEO_TYPE_RASTER,
		null,
		generic_vh_start,
		generic_vh_stop,
		seawolf2_vh_screenrefresh,
	
		/* sound hardware */
		null,
		null,
		null,
		null,
		null
	);
	
	static HiscoreLoadPtr seawolf_hiload = new HiscoreLoadPtr() { public int handler() 
	{
		/* check if the hi score table has already been initialized */
	 /*TOFIX                if (memcmp(RAM,0xC20D,new char[] {0xD8,0x19},2) == 0)
		{
			FILE f;
	
	
			if ((f = fopen(name,"rb")) != null)
			{
                            fread(RAM,0xC208,1,2,f);
				fclose(f);
			}
	
			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };
	
	
	
	static HiscoreSavePtr seawolf_hisave = new HiscoreSavePtr() { public void handler() 
	{
		FILE f;
	
	 /*TOFIX        	if ((f = fopen(name,"wb")) != null)
		{
	        fwrite(RAM,0xC208,1,2,f);
			fclose(f);
		}*/
	
	} };
	
	public static GameDriver seawolf2_driver = new GameDriver
	(
	    "Sea Wolf II",
		"seawolf2",
	    "NICOLA SALMORIA (Mame Driver)\nSTEVE SCAVONE (Info and Code)\nMIKE COATES (Game Support)\nMIKE BALFOUR (High Scores)",
		seawolf_machine_driver,
	
		seawolf2_rom,
		null, null,
		null,
	
		seawolf2_input_ports, null, trak_ports, seawolf2_dsw, seawolf2_keys,
	
		null, palette, colortable,
		ORIENTATION_DEFAULT,
	
		seawolf_hiload, seawolf_hisave
	);
	
}
