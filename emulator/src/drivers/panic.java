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
 *  roms are from v0.36 romset
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
import static machine.panic.*;
import static vidhrdw.generic.*;
import static vidhrdw.panic.*;

public class panic
{

	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x4000, 0x5FFF, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new MemoryReadAddress( 0x6803, 0x6803, input_port_0_r ),
		new MemoryReadAddress( 0x6800, 0x6800, input_port_1_r ),
		new MemoryReadAddress( 0x6802, 0x6802, input_port_2_r ), /* DSW */
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x5C00, 0x5FFF, MWA_RAM ),
		new MemoryWriteAddress( 0x4000, 0x5BFF, panic_videoram_w, panic_videoram ),
		new MemoryWriteAddress( 0x6000, 0x601F, MWA_RAM, spriteram ,spriteram_size),
		new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};



	static IOReadPort readport[] =
	{
		new IOReadPort( -1 )	/* end of table */
	};

	static IOWritePort writeport[] =
	{
		new IOWritePort( -1 )	/* end of table */
	};


		
	static InputPort input_ports[] =
	{
		new InputPort(       /* IN0 */
			0xFF,
			new int[] { OSD_KEY_1, OSD_KEY_2, 0, 0, 0, 0, OSD_KEY_3, OSD_KEY_4 }
		),
		new InputPort(       /* IN1 */
			0xFF,
			new int[] { OSD_KEY_CONTROL, OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_DOWN, OSD_KEY_UP, 0, 0, OSD_KEY_ALT }
		),
		new InputPort(	/* DSW */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )
	};

        static  TrakPort trak_ports[] =
        {
                new TrakPort( -1 )
        };

        static  KEYSet keys[] =
        {
                 new KEYSet( 1, 4, "MOVE UP" ),
                 new KEYSet( 1, 2, "MOVE LEFT"  ),
                 new KEYSet( 1, 1, "MOVE RIGHT" ),
                 new KEYSet( 1, 3, "MOVE DOWN" ),
                 new KEYSet( 1, 7, "FILL" ),
                 new KEYSet( 1, 0, "DIG" ),
                 new KEYSet( -1 )
        };
	static DSW dsw[] =
	{
		new DSW( 2, 0x20, "LIVES", new String[] { "3", "4" } ),
		new DSW( 2, 0x10, "BONUS", new String[] { "3000", "5000" } ),
		new DSW( 2, 0x08, "CABINET", new String[] { "UPRIGHT", "COCKTAIL" } ),
		new DSW( 2, 0x07, "LEFT SLOT", new String[] { "1 1", "1 2", "1 3", "1 4", "1 5", "2 3", "? ?", "? ?" } ),
		new DSW( 2, 0xC0, "RIGHT SLOT", new String[] { "2 1", "1 1", "1 2", "1 3" } ),
		new DSW( -1 )
	};



	/* Main Sprites */
	
	static GfxLayout spritelayout0 = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		48 ,	/* 64 sprites */
		2,	    /* 2 bits per pixel */
		new int[] { 0, 4096*8 },	/* the two bitplanes are separated */
		new int[] { 15*8, 14*8, 13*8, 12*8, 11*8, 10*8, 9*8, 8*8, 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
  		new int[] { 16*8+7, 16*8+6, 16*8+5, 16*8+4, 16*8+3, 16*8+2, 16*8+1, 16*8+0, 7, 6, 5, 4, 3, 2, 1, 0 },
		32*8	/* every sprite takes 32 consecutive bytes */
	);

	/* Man Top */
	
	static GfxLayout spritelayout1 = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		16 ,	/* 16 sprites */
		2,	    /* 2 bits per pixel */
		new int[] { 0, 4096*8 },	/* the two bitplanes are separated */
		new int[] { 15*8, 14*8, 13*8, 12*8, 11*8, 10*8, 9*8, 8*8, 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
  		new int[] { 16*8+7, 16*8+6, 16*8+5, 16*8+4, 16*8+3, 16*8+2, 16*8+1, 16*8+0, 7, 6, 5, 4, 3, 2, 1, 0 },
		32*8	/* every sprite takes 32 consecutive bytes */
	);
		
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0A00, spritelayout0, 0, 8 ),    /* Monsters             */
		new GfxDecodeInfo( 1, 0x0200, spritelayout0, 0, 8 ),    /* Monsters eating Man  */
		new GfxDecodeInfo( 1, 0x0800, spritelayout1, 0, 8 ),    /* Man                  */
		new GfxDecodeInfo( -1 ) /* end of array */
	};

	static char palette[] =
	{
		0x00,0x00,0x00,   /* black  */
		0x00,0x00,0xd8,   /* blue   */
		0xf8,0x00,0x00,   /* red    */
		0xff,0x00,0xff,   /* purple */
		0x00,0xf8,0x00,   /* green  */
		0x00,0xff,0xff,   /* cyan   */
		0xf8,0xf8,0x00,   /* yellow */
		0xff,0xff,0xff,   /* white  */
		0xf8,0x94,0x44,   /* orange */
	};

	static final int black = 0, blue = 1, red = 2, purple = 3, green = 4, cyan = 5, yellow = 6, white = 7, orange = 8;
	
	static char colortable[] =
	{
		black, yellow,    red,        white,        /* 1 Drop Monster */
		black, green,     purple,     white,        /* 3 Drop Monster */
		black, red,       white,      yellow,       /* Man */
		black, white,     white,      white,        /* Not Used? */
		black, blue,      green,      white,        /* 2 Drop Monster */

		black, blue,         0,          0,         /* Screen Colours */
		0,     purple,       orange,     0,
		0,     0,            green,      cyan,
    	0,     0,            0,          white
	};

	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				2000000,	/* 2 Mhz? */
				0,
				readmem, writemem, readport, writeport,
				panic_interrupt, 2
			)
		},
		60,
		null,

		/* video hardware */
  		32*8, 32*8, new rectangle( 6*8, 30*8-1, 0*8, 32*8-1 ),
		gfxdecodeinfo,
		sizeof(palette)/3, sizeof(colortable),
		null,
                VIDEO_TYPE_RASTER,  
		null,
		panic_vh_start,
		panic_vh_stop,
		panic_vh_screenrefresh,

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
	
	static HiscoreLoadPtr panic_hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
		/* wait for default to be copied */
		if (RAM[0x40c1] == 0x00 && RAM[0x40c2] == 0x03 && RAM[0x40c3] == 0x04)
		{
			FILE f;
	
			if ((f = fopen(name, "rb")) != null)
			{
	        	RAM[0x4004] = 0x01;	/* Prevent program resetting high score */
	
				fread(RAM, 0x40C1, 1, 5, f);
	            fread(RAM, 0x5C00, 1, 12, f);
				fclose(f);
			}
	
			return 1;
		}
		else return 0;	/* we can't load the hi scores yet */
	} };
	
	
	
	static HiscoreSavePtr panic_hisave = new HiscoreSavePtr()	{ public void handler(String name)
	{
		FILE f;
	
	
		if ((f = fopen(name, "wb")) != null)
		{
			fwrite(RAM, 0x40C1, 1, 5, f);
	        fwrite(RAM, 0x5C00, 1, 12, f);
			fclose(f);
		}
	} };
        static RomLoadPtr panic_rom= new RomLoadPtr(){ public void handler()  
        {
		 ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "spcpanic.1",   0x0000, 0x0800, 0x405ae6f9 );
                ROM_LOAD( "spcpanic.2",   0x0800, 0x0800, 0xb6a286c5 );
                ROM_LOAD( "spcpanic.3",   0x1000, 0x0800, 0x85ae8b2e );
                ROM_LOAD( "spcpanic.4",   0x1800, 0x0800, 0xb6d4f52f );
                ROM_LOAD( "spcpanic.5",   0x2000, 0x0800, 0x5b80f277 );
                ROM_LOAD( "spcpanic.6",   0x2800, 0x0800, 0xb73babf0 );
                ROM_LOAD( "spcpanic.7",   0x3000, 0x0800, 0xfc27f4e5 );
                ROM_LOAD( "spcpanic.8",   0x3800, 0x0800, 0x7da0b321 );         /* Colour Table */

		ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "spcpanic.9",   0x0000, 0x0800, 0xeec78b4c );
                ROM_LOAD( "spcpanic.10",  0x0800, 0x0800, 0xc9631c2d );
                ROM_LOAD( "spcpanic.12",  0x1000, 0x0800, 0xe83423d0 );
                ROM_LOAD( "spcpanic.11",  0x1800, 0x0800, 0xacea9df4 );
                ROM_END();
        }};
        
	static RomLoadPtr  panica_rom= new RomLoadPtr(){ public void handler()  
        {                 
                     ROM_REGION(0x10000);	/* 64k for code */
                    ROM_LOAD( "panica.1",     0x0000, 0x0800, 0x289720ce );
                    ROM_LOAD( "spcpanic.2",   0x0800, 0x0800, 0xb6a286c5 );
                    ROM_LOAD( "spcpanic.3",   0x1000, 0x0800, 0x85ae8b2e );
                    ROM_LOAD( "spcpanic.4",   0x1800, 0x0800, 0xb6d4f52f );
                    ROM_LOAD( "spcpanic.5",   0x2000, 0x0800, 0x5b80f277 );
                    ROM_LOAD( "spcpanic.6",   0x2800, 0x0800, 0xb73babf0 );
                    ROM_LOAD( "panica.7",     0x3000, 0x0800, 0x3641cb7f );
                    ROM_LOAD( "spcpanic.8",   0x3800, 0x0800, 0x7da0b321 );         /* Colour Table */

                   ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
                    ROM_LOAD( "spcpanic.9",   0x0000, 0x0800, 0xeec78b4c );
                    ROM_LOAD( "spcpanic.10",  0x0800, 0x0800, 0xc9631c2d );
                    ROM_LOAD( "spcpanic.12",  0x1000, 0x0800, 0xe83423d0 );
                    ROM_LOAD( "spcpanic.11",  0x1800, 0x0800, 0xacea9df4 );
                    ROM_END();
        }};

	public static GameDriver panic_driver = new GameDriver
	(
                "Space Panic",
		"panic",
                "MIKE COATES",
		machine_driver,
	
		panic_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		null, palette, colortable,
		ORIENTATION_DEFAULT,
	
		panic_hiload, panic_hisave
	);
        public static GameDriver panica_driver = new GameDriver
	(
                "Space Panic (alternate version)",
		"panica",
                "MIKE COATES",
		machine_driver,

		panica_rom,
		null, null,
		null,

		input_ports,null, trak_ports, dsw, keys,

		null, palette, colortable,
		ORIENTATION_DEFAULT,

		panic_hiload, panic_hisave
	);
}

