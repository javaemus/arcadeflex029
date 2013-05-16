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
 *  NOTES: romsets are from v0.36 roms 
 */



package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static machine.docastle.*;
import static vidhrdw.generic.*;
import static vidhrdw.docastle.*;
import static mame.osdependH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static sndhrdw.docastle.*;
import static mame.memoryH.*;

public class docastle
{

	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x8000, 0x97ff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
                new MemoryReadAddress( 0xb800, 0xbbff, videoram_r ), /* mirror of video ram */
                new MemoryReadAddress( 0xbc00, 0xbfff, colorram_r ), /* mirror of color ram */
                new MemoryReadAddress( 0xa000, 0xa008, docastle_shared0_r ),
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x8000, 0x97ff, MWA_RAM ),
		new MemoryWriteAddress( 0xb000, 0xb3ff, videoram_w, videoram,videoram_size ),
		new MemoryWriteAddress( 0xb400, 0xb7ff, colorram_w, colorram ),
		new MemoryWriteAddress( 0x9800, 0x99ff, MWA_RAM, spriteram,spriteram_size  ),
                new MemoryWriteAddress( 0xa000, 0xa008, docastle_shared1_w ),
                new MemoryWriteAddress( 0xe000, 0xe000, docastle_nmitrigger ),
		new MemoryWriteAddress( 0xa800, 0xa800, MWA_NOP ),
		new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};



	static MemoryReadAddress readmem2[] =
	{
		new MemoryReadAddress( 0x8000, 0x87ff, MRA_RAM ),
                new MemoryReadAddress( 0xc003, 0xc003, input_port_0_r ),
                new MemoryReadAddress( 0xc005, 0xc005, input_port_1_r ),
                new MemoryReadAddress( 0xc007, 0xc007, input_port_2_r ),
                new MemoryReadAddress( 0xc002, 0xc002, input_port_3_r ),
                new MemoryReadAddress( 0xc081, 0xc081, input_port_4_r ),
                new MemoryReadAddress( 0xc085, 0xc085, input_port_5_r ),
                new MemoryReadAddress( 0xa000, 0xa008, docastle_shared1_r ),
		new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress writemem2[] =
	{
		new MemoryWriteAddress( 0x8000, 0x87ff, MWA_RAM ),
                new MemoryWriteAddress( 0xa000, 0xa008, docastle_shared0_w ),
                new MemoryWriteAddress( 0xe000, 0xe000, docastle_sound1_w ),
                new MemoryWriteAddress( 0xe400, 0xe400, docastle_sound2_w ),
                new MemoryWriteAddress( 0xe800, 0xe800, docastle_sound3_w ),
                new MemoryWriteAddress( 0xec00, 0xec00, docastle_sound4_w ),
		new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};


        //TODO : joystick support???
	static InputPort input_ports[] =
	{
                    new InputPort(	/* IN0 */
                            0xff,
                            new int[]{ OSD_KEY_RIGHT, OSD_KEY_UP, OSD_KEY_LEFT, OSD_KEY_DOWN,
                                            OSD_KEY_Q, OSD_KEY_W, OSD_KEY_E, OSD_KEY_R }

                    ),
                    new InputPort(	/* IN1 */
                            0xff,
                            new int[]{ OSD_KEY_CONTROL, 0, 0, OSD_KEY_1,
                                            0, 0, 0, OSD_KEY_2 }
                    ),
                    new InputPort(	/* IN2 */
                            0xff,
                            new int[]{ 0, 0, 0, 0, OSD_KEY_3, 0, 0, 0 }
                    ),
                    new InputPort(	/* DSWA */
                            0xdf,
                            new int[]{ 0, 0, OSD_KEY_F1, 0, 0, 0, 0, 0 }
                    ),
                    new InputPort(	/* COIN */
                            0xff,
                            new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                    ),
                    new InputPort(	/* TEST */
                            0xff,
                            new int[]{ OSD_KEY_F2, 0, 0, 0, 0, 0, 0, 0 }
                    ),
                    new InputPort( -1 )	/* end of table */
	};

        static TrakPort[] trak_ports =
        {
           new TrakPort(-1)
        };
       static KEYSet[] keys =
       {

          new KEYSet( 0, 1, "MOVE UP" ),
	  new KEYSet( 0, 2, "MOVE LEFT"  ),
	  new KEYSet( 0, 0, "MOVE RIGHT" ),
	  new KEYSet( 0, 3, "MOVE DOWN" ),
	  new KEYSet( 1, 0, "HAMMER" ),	 
          new KEYSet(-1) 
        };
      
	static DSW dsw[] =
	{
             new DSW( 3, 0xc0, "LIVES", new String[]{ "2", "5", "4", "3" }, 1 ),
             new DSW( 3, 0x03, "DIFFICULTY", new String[]{ "HARDEST", "HARD", "MEDIUM", "EASY" }, 1 ),
	     new DSW( 3, 0x10, "EXTRA", new String[]{ "HARD", "EASY" }, 1 ),
	     new DSW( 3, 0x08, "SW4", new String[]{ "ON", "OFF" }, 1 ),
	     new DSW( -1 )

	};

        static DSW dsw_unicorn[] =
        {
                new DSW( 3, 0xc0, "LIVES", new String[]{ "2", "5", "4", "3" }, 1  ),
                new DSW( 3, 0x03, "DIFFICULTY", new String[]{ "HARDEST", "HARD", "MEDIUM", "EASY" }, 1  ),
                new DSW( 3, 0x10, "EXTRA", new String[]{ "HARD", "EASY" }, 1  ),
                new DSW( 3, 0x08, "DSW 4", new String[]{ "ON", "OFF" }, 1  ),
                new DSW( 3, 0x04, "RACK ADVANCE", new String[]{ "ON", "OFF" }, 1  ),
                new DSW( -1  )
        };

	static GfxLayout charlayout = new GfxLayout
	(
                8,8,    /* 8*8 characters */
                512,    /* 512 characters */
                4,      /* 4 bits per pixel */
                new int[]{ 0, 1, 2, 3 }, /* the bitplanes are packed in one nibble */
                new int[]{ 0, 4, 8, 12, 16, 20, 24, 28 },
                new int[]{ 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
                32*8   /* every char takes 32 consecutive bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
                16,16,  /* 16*16 sprites */
                256,    /* 256 sprites */
                4,      /* 4 bits per pixel */
                new int[]{ 0, 1, 2, 3 }, /* the bitplanes are packed in one nibble */
                new int[]{ 0, 4, 8, 12, 16, 20, 24, 28,
                                32, 36, 40, 44, 48, 52, 56, 60 },
                new int[]{ 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64,
                                8*64, 9*64, 10*64, 11*64, 12*64, 13*64, 14*64, 15*64 },
                128*8  /* every sprite takes 128 consecutive bytes */
	);



	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,    0, 64 ),
		new GfxDecodeInfo( 1, 0x4000, spritelayout,  64*16, 32  ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};



	static char color_prom[] =
	{
            0x00,0xFD,0x01,0x2B,0x92,0x17,0xDB,0xFC,0xFF,0x44,0x3A,0x4E,0x02,0x0F,0x5B,0x00,
            0xB0,0xFE,0x64,0xF9,0x6E,0x1B,0x00,0x86,0xFF,0x00,0x0F,0xE4,0x91,0xFF,0x00,0x00,
            0xFD,0x91,0x6D,0x49,0xF0,0x18,0xFF,0x00,0xFF,0x0F,0x1F,0xE4,0xF0,0x1C,0x00,0x00,
            0xFF,0x0F,0x1F,0x1C,0xF0,0xFC,0x00,0x00,0x0C,0xF0,0x2F,0xDE,0xE0,0x1F,0xFC,0x00,
            0x60,0xAC,0xD0,0x88,0xF8,0xE0,0x01,0x00,0x00,0x00,0x49,0xCC,0xA0,0xFC,0x00,0xFC,
            0xFC,0x03,0x1F,0xE0,0xFF,0xF0,0x20,0x00,0xFF,0xFC,0x00,0x03,0xE0,0xF0,0x6E,0x00,
            0x91,0x92,0x49,0x60,0x0C,0x08,0xAC,0x00,0xFF,0xFC,0x20,0x03,0x01,0xF0,0xE0,0xE0,
            0x91,0x47,0x2A,0x6F,0x08,0x1D,0xC0,0x72,0xB0,0xFE,0x64,0xF9,0x6E,0x1B,0x00,0x49,
            0xB0,0xFE,0x64,0xF9,0x6E,0x1B,0x00,0x88,0xB0,0xFE,0x64,0xF9,0x6E,0x1B,0x00,0x08,
            0xB0,0xFE,0x64,0xF9,0x6E,0x1B,0x00,0x01,0xFF,0xFC,0x00,0x03,0xE0,0xF0,0x2F,0x00,
            0xB0,0xE0,0x64,0xF9,0x6E,0x1B,0x00,0x86,0xB0,0x1C,0x64,0xF9,0x6E,0x1B,0x00,0x86,
            0xA2,0xEB,0x00,0xF2,0x08,0x1D,0xCC,0x1F,0x56,0x1C,0x62,0x75,0x08,0x1D,0xCC,0x1F,
            0x03,0x7B,0x03,0x6F,0x08,0x1D,0xCC,0x1F,0xFE,0x1F,0x2F,0x00,0xFF,0x00,0x00,0x00,
            0xFF,0xE0,0x00,0x03,0x1C,0xE4,0xFC,0x00,0xE0,0x92,0x08,0x00,0x17,0xB6,0x96,0xDA,
            0x00,0x20,0x40,0x60,0x80,0xA0,0xC0,0xE0,0x00,0x04,0x08,0x0C,0x10,0x14,0x18,0x1C,
            0x00,0x00,0x01,0x01,0x02,0x02,0x03,0x03,0x00,0x24,0x49,0x6D,0x92,0xB6,0xDB,0xFF
	};



	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				4000000,	/* 4 Mhz ? */
				0,
				readmem, writemem, null, null,
				interrupt, 1
			),
			new MachineCPU(
				CPU_Z80,
				4000000,	/* 4 Mhz ??? */
				2,	/* memory region #2 */
				readmem2, writemem2, null, null,
				docastle_interrupt2,16
			)
		},
		60,
		null,

		/* video hardware */
		32*8, 32*8, new rectangle( 1*8, 31*8-1, 4*8, 28*8-1 ),
		gfxdecodeinfo,
		256, 96*16,
	        docastle_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
		null,
		docastle_vh_start,
		docastle_vh_stop,
		docastle_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		docastle_sh_start,
                docastle_sh_stop,
                docastle_sh_update
	);



	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	static RomLoadPtr docastle_rom = new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "01p_a1.bin",  0x0000, 0x2000 , 0x17c6fc24 );
		ROM_LOAD( "01n_a2.bin",  0x2000, 0x2000 , 0x1d2fc7f4 );
		ROM_LOAD( "01l_a3.bin",  0x4000, 0x2000 , 0x71a70ba9 );
		ROM_LOAD( "01k_a4.bin",  0x6000, 0x2000 , 0x479a745e );
	
		ROM_REGION(0xc000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "03a_a5.bin",  0x0000, 0x4000 , 0x0636b8f4 );
		ROM_LOAD( "04m_a6.bin",  0x4000, 0x2000 , 0x3bbc9b26 );
		ROM_LOAD( "04l_a7.bin",  0x6000, 0x2000 , 0x3dfaa9d1 );
		ROM_LOAD( "04j_a8.bin",  0x8000, 0x2000 , 0x9afb16e9 );
		ROM_LOAD( "04h_a9.bin",  0xa000, 0x2000 , 0xaf24bce0 );
	
		ROM_REGION(0x10000);	/* 64k for the second CPU */
		ROM_LOAD( "07n_a0.bin", 0x0000, 0x4000 , 0xf23b5cdb );
                ROM_END();
        }};
        static RomLoadPtr docastl2_rom = new RomLoadPtr(){ public void handler() 
        {
            ROM_REGION(0x10000);	/* 64k for code */
            ROM_LOAD( "a1",           0x0000, 0x2000, 0x0d81fafc );
            ROM_LOAD( "a2",           0x2000, 0x2000, 0xa13dc4ac );
            ROM_LOAD( "a3",           0x4000, 0x2000, 0xa1f04ffb );
            ROM_LOAD( "a4",           0x6000, 0x2000, 0x1fb14aa6 );

            ROM_REGION(0xc000);	/* temporary space for graphics (disposed after conversion) */
	    ROM_LOAD( "03a_a5.bin",  0x0000, 0x4000 , 0x0636b8f4 );
	    ROM_LOAD( "04m_a6.bin",  0x4000, 0x2000 , 0x3bbc9b26 );
	    ROM_LOAD( "04l_a7.bin",  0x6000, 0x2000 , 0x3dfaa9d1 );
	    ROM_LOAD( "04j_a8.bin",  0x8000, 0x2000 , 0x9afb16e9 );
	    ROM_LOAD( "04h_a9.bin",  0xa000, 0x2000 , 0xaf24bce0 );

            ROM_REGION(0x10000);	/* 64k for the second CPU */
            ROM_LOAD( "a10",          0x0000, 0x4000, 0x45f7f69b );
            ROM_END();
        }};
        static RomLoadPtr dounicorn_rom = new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "DOREV1.BIN",  0x0000, 0x2000, 0x1e2cbb3c );
                ROM_LOAD( "DOREV2.BIN",  0x2000, 0x2000, 0x18418f83 );
                ROM_LOAD( "DOREV3.BIN",  0x4000, 0x2000, 0x7b9e2061 );
                ROM_LOAD( "DOREV4.BIN",  0x6000, 0x2000, 0xe013954d );

                ROM_REGION(0xc000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "03a_a5.bin",  0x0000, 0x4000, 0x0636b8f4 );
                ROM_LOAD( "DOREV6.BIN",  0x4000, 0x2000, 0x9e335bf8 );
                ROM_LOAD( "DOREV7.BIN",  0x6000, 0x2000, 0xf5d5701d );
                ROM_LOAD( "DOREV8.BIN",  0x8000, 0x2000, 0x7143ca68 );
                ROM_LOAD( "DOREV9.BIN",  0xa000, 0x2000, 0x893fc004 );

                ROM_REGION(0x10000);	/* 64k for the second CPU */
                ROM_LOAD( "DOREV10.BIN", 0x0000, 0x4000, 0x4b1925e3 );
                ROM_END();
        }};

        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
	{
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char []RAM = Machine.memory_region[0];

                /* check if the hi score table has already been initialized */
     /*TOFIX           if (memcmp(RAM,0x8020, new char[] { 0x01 , 0x00 , 0x00 },3) == 0 &&
                                memcmp(RAM,0x8068,new char[] { 0x01 , 0x00 , 0x00 },3) == 0)
                {
                       FILE f;


                        if ((f = fopen(name, "rb")) != null)
                        {
                                fread(RAM,0x8020,1,10*8,f);
                                fclose(f);
                        }

                        return 1;
                }
                else */ return 0;	/* we can't load the hi scores yet */
        }};
        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
	{
            
                FILE f;
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
        /*TOFIX          char []RAM = Machine.memory_region[0];


                if ((f = fopen(name, "wb")) != null)
                {
                        fwrite(RAM,0x8020,1,10*8,f);
                        fclose(f);
                } */
        }};
	public static GameDriver docastle_driver = new GameDriver
	(
                "Mr. Do's Castle",
		"docastle",
                "MIRKO BUFFONI\nNICOLA SALMORIA\nGARY WALTON\nSIMON WALLS",
		machine_driver,
	
		docastle_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		color_prom, null, null,
                ORIENTATION_ROTATE_270,
	
		hiload, hisave
	);
        public static GameDriver docastl2_driver = new GameDriver
        (       
                "Mr. Do's Castle (alternate version)",
                "docastl2",
                "MIRKO BUFFONI\nNICOLA SALMORIA\nGARY WALTON\nSIMON WALLS",
                machine_driver,

                docastl2_rom,
                null, null,
                null,

                input_ports, null, trak_ports, dsw, keys,

                color_prom, null, null,
                ORIENTATION_ROTATE_270,

                hiload, hisave
        );
        public static GameDriver dounicorn_driver = new GameDriver
	(
                "Mr. Do VS The Unicorns",
		"douni",
                "MIRKO BUFFONI\nNICOLA SALMORIA\nGARY WALTON\nSIMON WALLS\nLEE TAYLOR",
		machine_driver,
	
		dounicorn_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw_unicorn, keys,
	
		color_prom, null, null,
                ORIENTATION_ROTATE_270,
	
		hiload, hisave
	);
}
