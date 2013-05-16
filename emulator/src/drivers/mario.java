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
 * romset are from 0.36 romset
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
import static vidhrdw.mario.*;
import static mame.inptport.*;
import static mame.memoryH.*;

public class mario
{

	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x6000, 0x6fff, MRA_RAM ),
		new MemoryReadAddress( 0x7400, 0x77ff, MRA_RAM ),	/* video RAM */
		new MemoryReadAddress( 0x0000, 0x5fff, MRA_ROM ),
		new MemoryReadAddress( 0xf000, 0xffff, MRA_ROM ),
		new MemoryReadAddress( 0x7c00, 0x7c00, input_port_0_r ),	/* IN0 */
		new MemoryReadAddress( 0x7c80, 0x7c80, input_port_1_r ),	/* IN1 */
		new MemoryReadAddress( 0x7f80, 0x7f80, input_port_2_r ),	/* DSW */
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x6000, 0x68ff, MWA_RAM ),
		new MemoryWriteAddress( 0x6a80, 0x6fff, MWA_RAM ),
		new MemoryWriteAddress( 0x6900, 0x6a7f, MWA_RAM, spriteram,spriteram_size ),
		new MemoryWriteAddress( 0x7400, 0x77ff, videoram_w, videoram,videoram_size ),
		new MemoryWriteAddress( 0x7e80, 0x7e80, mario_gfxbank_w ),
		new MemoryWriteAddress( 0x7e84, 0x7e84, interrupt_enable_w ),
		new MemoryWriteAddress( 0x7e83, 0x7e83, MWA_RAM, mario_sprite_palette ),
		new MemoryWriteAddress( 0x0000, 0x5fff, MWA_ROM ),
		new MemoryWriteAddress( 0xf000, 0xffff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};

		
	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0x00,
			new int[] { OSD_KEY_RIGHT, OSD_KEY_LEFT, 0, 0, OSD_KEY_CONTROL, OSD_KEY_1, OSD_KEY_2, OSD_KEY_F1 }
		),
		new InputPort(	/* IN1 */
			0x00,
			new int[] { OSD_KEY_X, OSD_KEY_Z, 0, 0, OSD_KEY_SPACE, OSD_KEY_3, 0, 0 }
		),
		new InputPort(	/* DSW */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};


	
	static DSW dsw[] =
	{
		new DSW( 2, 0x03, "LIVES", new String[] { "3", "4", "5", "6" } ),
		new DSW( 2, 0x30, "BONUS", new String[] { "20000", "30000", "40000", "NONE" } ),
		new DSW( 2, 0xc0, "DIFFICULTY", new String[] { "EASY", "MEDIUM", "HARD", "HARDEST" } ),
		new DSW( -1 )
	};

       static TrakPort[] trak_ports =
       {
           new TrakPort(-1)
       };

      static KEYSet[] keys =
      {
          new KEYSet( 0, 1, "PL1 MOVE LEFT"),
          new KEYSet(0, 0, "PL1 MOVE RIGHT"  ),
          new KEYSet(0, 4, "PL1 JUMP"),
          new KEYSet(1, 1, "PL2 MOVE LEFT" ),
          new KEYSet(1, 0, "PL2 MOVE RIGHT" ),
          new KEYSet(1, 4, "PL2 JUMP"),
          new KEYSet(-1) };


	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		512,	/* 512 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 512*8*8 },	/* the two bitplanes are separated */
		new int[] { 7, 6, 5, 4, 3, 2, 1, 0 },	/* pretty straightforward layout */
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);

	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		256,	/* 256 sprites */
		3,	/* 3 bits per pixel */
		new int[] { 0, 256*16*16, 2*256*16*16 },	/* the bitplanes are separated */
		new int[] { 256*16*8+7, 256*16*8+6, 256*16*8+5, 256*16*8+4, 256*16*8+3, 256*16*8+2, 256*16*8+1, 256*16*8+0,
				7, 6, 5, 4, 3, 2, 1, 0 },	/* the two halves of the sprite are separated */
		new int[] { 15*8, 14*8, 13*8, 12*8, 11*8, 10*8, 9*8, 8*8,
				7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		16*8	/* every sprite takes 16 consecutive bytes */
	);


		
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,      0, 32 ),
		new GfxDecodeInfo( 1, 0x2000, spritelayout, 32*4, 32 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};


	
	static char palette[] =
	{
		0x00,0x00,0x00,	/* BLACK */
        	0xff,0xff,0xff, /* WHITE */
        	0xff,0x00,0x00, /* RED */
        	0xff,0x00,0xff, /* PURPLE */
        	0,0,127,        /* INBLUE */
        	0,180,0,        /* GRUN */
        	255,131,3,      /* ORANGE */
        	0x00,0x00,0xff, /* BLUE */
        	0xff,0xff,0x00, /* YELLOW */
        	120,120,255,      /* GREY */
        	3,180,239,      /* LTBLUE */
        	0,80,0,         /* DKGRUN */
        	0x00,0xff,0x00, /* GREEN */
        	167,3,3,        /* DKBROWN */
        	255,183,115,    /* LTBROWN */
        	0x00,127,0x00,  /* DKGREEN */
	};

	static final int BLACK = 0, WHITE = 1, RED = 2, PURPLE = 3, INBLUE = 4, GRUN = 5, ORANGE = 6, BLUE = 7, YELLOW = 8, GREY = 9,
					LTBLUE = 10, DKGRUN = 11, GREEN = 12, DKBROWN = 13, LTBROWN = 14, DKGREEN = 15;
	
	static char colortable[] =
	{
		/* chars (first bank) */
		BLACK,BLUE,WHITE,BLACK,         /* 0-9 */
		BLACK,BLUE,WHITE,BLACK,         /* A-Z */
		BLACK,BLUE,WHITE,BLACK,         /* A-Z */
		BLACK,BLUE,WHITE,BLACK,         /* A-Z */
		BLACK,DKBROWN,BLUE,LTBROWN,     /* MARIO */
		BLACK,DKBROWN,GREEN,BLACK,      /* A-Z */
		BLACK,DKBROWN,GREEN,LTBROWN,    /* LUIGI */
		BLACK,DKBROWN,GREEN,BLACK,        /* A-Z */
		BLACK,DKBROWN,RED,WHITE,                /* A-Z */
		BLACK,DKBROWN,RED,WHITE,                /* A-Z */
		BLACK,DKBROWN,RED,WHITE,                /* A-Z */
		BLACK,DKBROWN,RED,WHITE,                /* A-Z */
		BLACK,BLUE,LTBLUE,WHITE,        /* Mattonella / Acqua */
		BLACK,ORANGE,YELLOW,WHITE,      /* A-Z */
		BLACK,ORANGE,YELLOW,WHITE,      /* Mattonelle A-Z */
		BLACK,ORANGE,YELLOW,WHITE,
	
		BLACK,BLUE,WHITE,BLACK,	/* char set #1 */
		BLACK,BLUE,WHITE,BLACK,
		BLACK,BLUE,WHITE,BLACK,
		BLACK,BLUE,WHITE,BLACK,
		BLACK,DKBROWN,GREEN,BLACK,	/* char set #2 */
		BLACK,DKBROWN,GREEN,BLACK,
		BLACK,DKBROWN,GREEN,BLACK,
		BLACK,DKBROWN,GREEN,BLACK,
		BLACK,DKBROWN,RED,WHITE,	/* char set #3 */
		BLACK,DKBROWN,RED,WHITE,
		BLACK,DKBROWN,RED,WHITE,
		BLACK,DKBROWN,RED,WHITE,
		BLACK,ORANGE,YELLOW,WHITE,	/* char set #4 */
		BLACK,ORANGE,YELLOW,WHITE,
		BLACK,ORANGE,YELLOW,WHITE,
		BLACK,ORANGE,YELLOW,WHITE,
	
		/* Sprites bank #1 */
		BLACK,RED,GREEN,DKGRUN,DKGREEN,GRUN,WHITE,BLACK,  /* Tubatura */
		BLACK,WHITE,WHITE,DKBROWN,GREEN,BLUE,LTBROWN,DKGRUN, /* Tartarughe */
		BLACK,WHITE,WHITE,DKBROWN,LTBLUE,BLUE,LTBROWN,DKGRUN, /* Tartarughe Incazzose */
		BLACK,WHITE,WHITE,DKBROWN,RED,DKBROWN,LTBROWN,DKGRUN, /* Tartarughe Violente */
		BLACK,DKBROWN,YELLOW,GREY,WHITE,ORANGE,RED,BLUE, /* Granchio + Bonus */
		BLACK,INBLUE,YELLOW,GREY,WHITE,LTBLUE,BLUE,RED, /* Granchio Incazzoso */
		BLACK,INBLUE,YELLOW,GREY,WHITE,LTBROWN,PURPLE,BLUE, /* Granchio Violento */
		BLACK,DKGRUN,GREEN,GREY,WHITE,GRUN,DKGREEN,BLUE, /* Fuoco Verde */
		BLACK,DKBROWN,WHITE,ORANGE,BLUE,RED,RED,ORANGE, /* Moscone */
		BLACK,DKBROWN,YELLOW,ORANGE,BLUE,RED,RED,ORANGE, /* Moscone Ribaltone */
		BLACK,WHITE,DKBROWN,DKBROWN,BLUE,RED,LTBROWN,BLACK, /* Mario */
		BLACK,WHITE,DKBROWN,BLUE,GREEN,BLUE,LTBROWN,BLACK, /* Luigi */
		BLACK,WHITE, RED ,GRUN,WHITE,GREEN, ORANGE ,DKGRUN, /*  */
		BLACK,BLACK,DKBROWN,RED,RED,DKBROWN,WHITE,DKBROWN,
		BLACK,BLACK,BLUE,3,LTBLUE,GRUN,WHITE,3, /* Mattonelle Schiacciate + Pow */
		BLACK,BLACK,ORANGE,3,YELLOW,3,WHITE,3, /* Pow */
	
		/* Sprites bank #2 */
		BLACK,WHITE,DKBROWN,DKBROWN,BLUE,RED,LTBROWN,BLACK,  /* Mario titolo */
		BLACK,WHITE,DKBROWN,BLUE,GREEN,BLUE,LTBROWN,BLACK, /* Luigi titolo */
		BLACK,WHITE,WHITE,DKBROWN,LTBLUE,BLUE,LTBROWN,DKGRUN,
		BLACK,WHITE,WHITE,DKBROWN,RED,DKBROWN,LTBROWN,DKGRUN,
		BLACK,DKBROWN,YELLOW,GREY,WHITE,ORANGE,RED,BLUE,
		BLACK,INBLUE,YELLOW,GREY,WHITE,LTBLUE,BLUE,RED,
		BLACK,INBLUE,YELLOW,GREY,WHITE,LTBROWN,PURPLE,BLUE,
		BLACK,DKGRUN,GREEN,GREY,WHITE,GRUN,DKGREEN,BLUE,
		BLACK,WHITE,0,BLUE,0,LTBLUE,0,INBLUE, /* TitoloSU #1 */
		BLACK,DKGRUN,0,LTBLUE,0,WHITE,0,BLUE, /* Titolo #2 */
		BLACK,GRUN,0,WHITE,0,DKGRUN,0,LTBLUE, /* Titolo #3 */
		BLACK,GREEN,0,DKGRUN,0,GRUN,0,WHITE, /* Titolo #4 */
		BLACK,WHITE,0,GRUN,0,GREEN,0,DKGRUN, /* TitoloGIU #5 */
		BLACK,INBLUE,0,GREEN,0,WHITE,0,GRUN, /* Titolo #6 */
		BLACK,BLUE,0,WHITE,0,INBLUE,0,GREEN, /* Titolo #7 */
		BLACK,LTBLUE,0,INBLUE,0,BLUE,0,WHITE /* Titolo #8 */
	};


		
	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz (?) */
				0,
				readmem, writemem, null, null,
				nmi_interrupt, 1
			)
		},
		60,
		null,

		/* video hardware */
		32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
		gfxdecodeinfo,
		sizeof(palette)/3, sizeof(colortable),
		null,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
		null,
		mario_vh_start,
		generic_vh_stop,
		mario_vh_screenrefresh,

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
	static RomLoadPtr mario_rom= new RomLoadPtr(){ public void handler()  //using 0.27 loading ,crcs are probably wrong...
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "mario.7f",     0x0000, 0x2000, 0xc0c6e014 );
		ROM_LOAD( "mario.7e",     0x2000, 0x2000, 0x116b3856 );
		ROM_LOAD( "mario.7d",     0x4000, 0x2000, 0xdcceb6c1 );
		ROM_LOAD( "mario.7c",     0xf000, 0x1000, 0x4a63d96b );
	
		ROM_REGION(0x8000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "mario.3f", 0x0000, 0x1000, 0x28b0c42c );
		ROM_LOAD( "mario.3j", 0x1000, 0x1000, 0x0c8cc04d );
		ROM_LOAD( "mario.7m", 0x2000, 0x1000, 0x22b7372e );
		ROM_LOAD( "mario.7n", 0x3000, 0x1000, 0x4f3a1f47 );
		ROM_LOAD( "mario.7p", 0x4000, 0x1000, 0x56be6ccd );
		ROM_LOAD( "mario.7s", 0x5000, 0x1000, 0x56f1d613 );
		ROM_LOAD( "mario.7t", 0x6000, 0x1000, 0x641f0008 );
		ROM_LOAD( "mario.7u", 0x7000, 0x1000, 0x7baf5309 );
	
		ROM_REGION(0x1000);	/* sound? */
		ROM_LOAD( "tma1c-a.6k",   0x0000, 0x1000, 0x06b9ff85 );
                ROM_END();
         }};
                               

	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
	{
		/* check if the hi score table has already been initialized */
 /*TOFIX        		if (memcmp(RAM, 0x6b1d, new char[] { 0x00, 0x20, 0x01 }, 3) == 0 &&
				memcmp(RAM, 0x6ba5, new char[] { 0x00, 0x32, 0x00 }, 3) == 0)
		{
			FILE f;
	
	
			if ((f = fopen(name, "rb")) != null)
			{
				fread(RAM, 0x6b00, 1, 34*5, f);	/* hi scores */
 /*TOFIX        				RAM[0x6823] = RAM[0x6b1f];
				RAM[0x6824] = RAM[0x6b1e];
				RAM[0x6825] = RAM[0x6b1d];
				fread(RAM, 0x6c00, 1, 0x3c, f);	/* distributions */
 /*TOFIX        				fclose(f);
			}
	
			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };
		
		
		
	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
	{
		FILE f;
	
	
 /*TOFIX        		if ((f = fopen(name, "wb")) != null)
		{
			fwrite(RAM, 0x6b00, 1, 34*5, f);	/* hi scores */
 /*TOFIX        			fwrite(RAM, 0x6c00, 1, 0x3c, f);	/* distribution */
 /*TOFIX        			fclose(f);
		}*/
	} };



	public static GameDriver mario_driver = new GameDriver
	(
                "Mario Bros.",
		"mario",
                "MIRKO BUFFONI\nNICOLA SALMORIA\nSTEFANO MOZZI",
		machine_driver,
	
		mario_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		null, palette, colortable,
		ORIENTATION_DEFAULT,
	
		hiload, hisave
	);
}
