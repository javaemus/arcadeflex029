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
 *  roms are from v0.36 romset
 */

package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.inptport.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static machine.mpatrol.*;
import static vidhrdw.generic.*;
import static vidhrdw.mpatrol.*;

public class mpatrol
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0xe000, 0xe7ff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new MemoryReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new MemoryReadAddress( 0xd000, 0xd000, input_port_0_r ),	/* IN0 */
		new MemoryReadAddress( 0xd001, 0xd001, input_port_1_r ),	/* IN1 */
		new MemoryReadAddress( 0xd002, 0xd002, input_port_2_r ),	/* IN2 */
		new MemoryReadAddress( 0xd003, 0xd003, input_port_3_r ),	/* DSW1 */
		new MemoryReadAddress( 0xd004, 0xd004, input_port_4_r ),	/* DSW2 */
		new MemoryReadAddress( 0x8800, 0x8800, mpatrol_protection_r ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0xe000, 0xe7ff, MWA_RAM ),
		new MemoryWriteAddress( 0x8000, 0x83ff, videoram_w, videoram,videoram_size ),
		new MemoryWriteAddress( 0x8400, 0x87ff, colorram_w, colorram ),
		new MemoryWriteAddress( 0xc820, 0xc87f, MWA_RAM, spriteram,spriteram_size),
		new MemoryWriteAddress( 0xc8a0, 0xc8ff, MWA_RAM, spriteram_2 ),
		new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	


static IOWritePort writeport[] =
{
	new IOWritePort( 0x1c, 0x1f, mpatrol_scroll_w ),
	new IOWritePort( 0x40, 0x40, mpatrol_bg1xpos_w ),
	new IOWritePort( 0x60, 0x60, mpatrol_bg1ypos_w ),
	new IOWritePort( 0x80, 0x80, mpatrol_bg2xpos_w ),
	new IOWritePort( 0xa0, 0xa0, mpatrol_bg2ypos_w ),
	new IOWritePort( 0xc0, 0xc0, mpatrol_bgcontrol_w ),
	new IOWritePort( -1 )	/* end of table */
};

       static TrakPort[] trak_ports =
        { new TrakPort(-1) };

        static KEYSet[] keys =
        {
            new KEYSet( 1, 1, "MOVE LEFT"),
            new KEYSet(1, 0, "MOVE RIGHT"),
            new KEYSet(1, 5, "JUMP"),
            new KEYSet( 1, 7, "FIRE"),
            new KEYSet(-1) };


	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { OSD_KEY_1, OSD_KEY_2, 0, OSD_KEY_3, 0, 0, 0, 0 }
		),
		new InputPort(	/* IN1 */
			0xff,
			new int[] { OSD_KEY_RIGHT, OSD_KEY_LEFT, 0, 0, 0, OSD_KEY_ALT, 0, OSD_KEY_CONTROL }
		),
		new InputPort(	/* IN2 */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW1 */
			0xfd,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW2 */
			0xfd,
			new int[] { 0, 0, 0, 0, 0, 0, 0, OSD_KEY_F2 }
		),
		new InputPort( -1 )	/* end of table */
	};
	


	static DSW dsw[] =
	{
		new DSW( 3, 0x03, "LIVES", new String[] { "2", "3", "4", "5" } ),
		new DSW( 3, 0x0c, "BONUS", new String[] { "NONE", "10000", "20 40 60000", "10 30 50000" } ),
		new DSW( 4, 0x20, "SECTOR SELECTION", new String[] { "YES", "NO" }, 1 ),
		new DSW( 4, 0x40, "DEMO MODE", new String[] { "YES", "NO" }, 1  ),
		new DSW( -1 )
	};


	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		512,	/* 512 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 512*8*8 },	/* the two bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		128,	/* 128 sprites */
		2,	/* 2 bits per pixel */
		new int[] { 0, 128*16*16 },	/* the two bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
					16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
					8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		32*8	/* every sprite takes 32 consecutive bytes */
	);
	static GfxLayout bgcharlayout = new GfxLayout
	(
		64,64,	/* 64*64 characters */
		4,	/* 4 characters (actually, it is just 1 big 256x64 image) */
		2,	/* 2 bits per pixel */
		new int[] { 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3, 2*8+0, 2*8+1, 2*8+2, 2*8+3, 3*8+0, 3*8+1, 3*8+2, 3*8+3,
					4*8+0, 4*8+1, 4*8+2, 4*8+3, 5*8+0, 5*8+1, 5*8+2, 5*8+3, 6*8+0, 6*8+1, 6*8+2, 6*8+3, 7*8+0, 7*8+1, 7*8+2, 7*8+3,
					8*8+0, 8*8+1, 8*8+2, 8*8+3, 9*8+0, 9*8+1, 9*8+2, 9*8+3, 10*8+0, 10*8+1, 10*8+2, 10*8+3, 11*8+0, 11*8+1, 11*8+2, 11*8+3,
					12*8+0, 12*8+1, 12*8+2, 12*8+3, 13*8+0, 13*8+1, 13*8+2, 13*8+3, 14*8+0, 14*8+1, 14*8+2, 14*8+3, 15*8+0, 15*8+1, 15*8+2, 15*8+3 },
		new int[] { 0*512, 1*512, 2*512, 3*512, 4*512, 5*512, 6*512, 7*512, 8*512, 9*512, 10*512, 11*512, 12*512, 13*512, 14*512, 15*512,
					16*512, 17*512, 18*512, 19*512, 20*512, 21*512, 22*512, 23*512, 24*512, 25*512, 26*512, 27*512, 28*512, 29*512, 30*512, 31*512,
					32*512, 33*512, 34*512, 35*512, 36*512, 37*512, 38*512, 39*512, 40*512, 41*512, 42*512, 43*512, 44*512, 45*512, 46*512, 47*512,
					48*512, 49*512, 50*512, 51*512, 52*512, 53*512, 54*512, 55*512, 56*512, 57*512, 58*512, 59*512, 60*512, 61*512, 62*512, 63*512 },
		128
	);


	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,      0, 32 ),
		new GfxDecodeInfo( 1, 0x2000, spritelayout, 32*4, 16 ),
		new GfxDecodeInfo( 1, 0x4000, bgcharlayout, 48*4,  1 ),
		new GfxDecodeInfo( 1, 0x5000, bgcharlayout, 49*4,  1 ),
		new GfxDecodeInfo( 1, 0x6000, bgcharlayout, 50*4,  1 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};


	
	static char palette[] =
	{
        0x00,0x00,0x00,   /* black      */
        0x94,0x00,0xd8,   /* darkpurple */
        0xd8,0x00,0x00,   /* darkred    */
        0xf8,0x64,0xd8,   /* pink       */
        0x00,0xd8,0x00,   /* darkgreen  */
        0x00,0x00,0x80,   /* darkblue   */
        0xd8,0xd8,0x94,   /* darkyellow */
        0xd8,0xf8,0xd8,   /* darkwhite  */
        0xf8,0x94,0x44,   /* orange     */
        0x00,0x00,0xd8,   /* blue   */
        0xf8,0x00,0x00,   /* red    */
        0xff,0x00,0xff,   /* purple */
        0x00,0xf8,0x00,   /* green  */
        0x00,0xff,0xff,   /* cyan   */
        0xf8,0xf8,0x00,   /* yellow */
        0xff,0xff,0xff,    /* white  */
        255,183,115,    /* LTBROWN */
        167,3,3,        /* DKBROWN */
	};
	
	static final int black = 0;
	static final int darkpurple = 1;
	static final int darkred = 2;
	static final int pink = 3;
	static final int darkgreen = 4;
	static final int darkblue = 5;
	static final int darkyellow = 6;
	static final int darkwhite = 7;
	static final int orange = 8;
	static final int blue = 9;
	static final int red = 10;
	static final int purple = 11;
	static final int green = 12;
	static final int cyan = 13;
	static final int yellow = 14;
	static final int white = 15;
	static final int ltbrown = 16;
	static final int dkbrown = 17;


	static char colortable[] =
	{
        /* chars */
        0,cyan,white,3, /* MOON PATROL on title screen */
        blue,red,white,1,       /* score beginner course */
        cyan,black,red,blue,    /* point / time beginner course */
        cyan,3,red,2,   /* lit caution led on champion course */
        green,orange,15,red,        /* ground */
        0,1,3,5,
        blue,black,1,2, /* high score point letter on beginner course */
        cyan,pink,1,2,  /* point letter on champion course */
        0,4,6,8,
        blue,black,red,2,       /* high score beginner course */
        0,9,12,15,
        pink,black,1,2, /* high score point letter on champion course */
        pink,red,yellow,1,      /* score champion course */
        cyan,black,red,pink,    /* point / time champion course */
        pink,black,red,2,       /* high score champion course */
        0,7,10,13,
        0,1,2,3,
        0,4,5,6,
        0,orange,orange,9,       /* starting ramp */
        0,10,11,12,
        0,13,14,15,
        0,1,3,5,
        0,7,9,11,
        0,13,15,2,
        0,4,6,8,
        0,10,12,14,
        0,1,4,7,
        0,10,13,2,
        0,5,8,11,
        0,14,3,6,
        0,9,12,15,
        0,7,10,13,

        /* sprites */
        0,black,pink,cyan,      /* moon patrol on beginner course */
        0,4,5,6,                /* buggy-shot, droping bomb, explosion 1 */
        0,7,8,9,                /* grabbing plant */
        0,10,11,12,             /* explosion 2 (on ground) */
        0,ltbrown,14,dkbrown,   /* boulders */
        0,1,3,5,
        0,7,9,11,
        0,13,15,2,              /* space crafts */
        0,4,6,8,                /* bottom part of plant */
        0,10,12,14,             /* any enemy  cars */

        0,1,4,7,                /* tri star bomb 1 */

        0,10,13,2,              /* tri star bomb 1 */

        0,black,red,cyan,       /* moon patrol on champion course */

        0,14,3,6,
        0,9,12,15,
        0,7,10,13,

        /* backgrounds */
        0,yellow,blue,green,
        0,1,darkgreen,green,
/*        0,white,darkblue,blue,*/
        0,0,darkblue,blue,
	};
	


	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz ? */
				0,
				readmem, writemem, null, writeport,
				interrupt, 1
			)
		},
		60,
		null,
	
		/* video hardware */
		32*8, 32*8, new rectangle( 1*8, 31*8-1, 0*8, 32*8-1 ),
		gfxdecodeinfo,
		sizeof(palette) / 3, sizeof(colortable),
		null,
                VIDEO_TYPE_RASTER,
		null,
		mpatrol_vh_start,
		mpatrol_vh_stop,
		mpatrol_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		null,
		null,
		null
	);



	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	static RomLoadPtr mpatrol_rom =new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "mp-a.3m",     0x0000, 0x1000, 0xbaa1a1d4 );
                ROM_LOAD( "mp-a.3l",     0x1000, 0x1000, 0x52459e51 );
                ROM_LOAD( "mp-a.3k",     0x2000, 0x1000, 0x9b249fe5 );
                ROM_LOAD( "mp-a.3j",     0x3000, 0x1000, 0xfee76972 );
	
		ROM_REGION(0x7000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "mp-e.3e",     0x0000, 0x1000, 0xf56e01fe );       /* chars */
                ROM_LOAD( "mp-e.3f",     0x1000, 0x1000, 0xcaaba2d9 );	
                ROM_LOAD( "mp-b.3m",      0x2000, 0x1000, 0x707ace5e );       /* sprites */
                ROM_LOAD( "mp-b.3n",      0x3000, 0x1000, 0x9b72133a );	
                ROM_LOAD( "mp-e.3l",      0x4000, 0x1000, 0xc46a7f72 );       /* background graphics */
                ROM_LOAD( "mp-e.3k",      0x5000, 0x1000, 0xc7aa1fb0 );
                ROM_LOAD( "mp-e.3h",      0x6000, 0x1000, 0xa0919392 );               
                ROM_END();
        }};
        
	static RomLoadPtr mranger_rom =new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x10000);	/* 64k for code */                
                ROM_LOAD( "mp-a.3m",      0x0000, 0x1000, 0x5873a860 );
                ROM_LOAD( "mr-a.3l",      0x1000, 0x1000, 0x217dd431 );
                ROM_LOAD( "mr-a.3k",      0x2000, 0x1000, 0x9f0af7b2 );
                ROM_LOAD( "mr-a.3j",      0x3000, 0x1000, 0x7fe8e2cd );
	
		ROM_REGION(0x7000);	/* temporary space for graphics (disposed after conversion) */              
                ROM_LOAD( "mp-e.3e",      0x0000, 0x1000, 0xe3ee7f75 );       /* chars */
                ROM_LOAD( "mp-e.3f",      0x1000, 0x1000, 0xcca6d023 );
                ROM_LOAD( "mp-b.3m",      0x2000, 0x1000, 0x707ace5e );       /* sprites */
                ROM_LOAD( "mp-b.3n",      0x3000, 0x1000, 0x9b72133a );
                ROM_LOAD( "mp-e.3l",      0x4000, 0x1000, 0xc46a7f72 );       /* background graphics */
                ROM_LOAD( "mp-e.3k",      0x5000, 0x1000, 0xc7aa1fb0 );
                ROM_LOAD( "mp-e.3h",      0x6000, 0x1000, 0xa0919392 );
                ROM_END();
        }};
	
	
	
	public static GameDriver mpatrol_driver = new GameDriver
	(
                "Moon Patrol",
		"mpatrol",
                "NICOLA SALMORIA\nCHRIS HARDY",
		machine_driver,
	
		mpatrol_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		null, palette, colortable,
		ORIENTATION_DEFAULT,
	
		null, null
	);

	public static GameDriver mranger_driver = new GameDriver
	(
                "Moon Ranger (bootleg Moon Patrol)",
		"mranger",
                "NICOLA SALMORIA\nCHRIS HARDY",
		machine_driver,

		mranger_rom,
		null, null,
		null,

		input_ports,null, trak_ports, dsw, keys,

		null, palette, colortable,
		ORIENTATION_DEFAULT,

		null, null
	);
}

