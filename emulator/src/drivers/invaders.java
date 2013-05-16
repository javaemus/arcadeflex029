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
 * using v0.36 romset
 * invdelux is invdpt2m in v0.36 romset
 *
 */

package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static machine.invaders.*;
import static sndhrdw.invaders.*;
import static vidhrdw.generic.*;
import static vidhrdw.invaders.*;
import static mame.inptport.*;
import static mame.memoryH.*;

public class invaders
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x2000, 0x3fff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new MemoryReadAddress( 0x4000, 0x57ff, MRA_ROM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x2000, 0x23ff, MWA_RAM ),
		new MemoryWriteAddress( 0x2400, 0x3fff, invaders_videoram_w, invaders_videoram ),
		new MemoryWriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new MemoryWriteAddress( 0x4000, 0x57ff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
        static MemoryWriteAddress lrescue_writemem[] = /* V.V */ /* Whole function */
        {
                new MemoryWriteAddress( 0x2000, 0x23ff, MWA_RAM ),
                new MemoryWriteAddress( 0x2400, 0x3fff, lrescue_videoram_w, invaders_videoram ),
                new MemoryWriteAddress( 0x0000, 0x1fff, MWA_ROM ),
                new MemoryWriteAddress( 0x4000, 0x57ff, MWA_ROM ),
                new MemoryWriteAddress( -1 )  /* end of table */
        };


        static MemoryWriteAddress invrvnge_writemem[] = /* V.V */ /* Whole function */
        {
                new MemoryWriteAddress (0x2000, 0x23ff, MWA_RAM ),
                new MemoryWriteAddress (0x2400, 0x3fff, invrvnge_videoram_w, invaders_videoram ),
                new MemoryWriteAddress (0x0000, 0x1fff, MWA_ROM ),
                new MemoryWriteAddress (0x4000, 0x57ff, MWA_ROM ),
                new MemoryWriteAddress (-1 )  /* end of table */
        };

		
	static IOReadPort readport[] =
	{
		new IOReadPort( 0x01, 0x01, input_port_0_r ),
		new IOReadPort( 0x02, 0x02, input_port_1_r ),
		new IOReadPort( 0x03, 0x03, invaders_shift_data_r ),
		new IOReadPort( -1 )	/* end of table */
	};
        static  IOReadPort invdelux_readport[] =
        {
                new IOReadPort( 0x00, 0x00, input_port_0_r ),
                new IOReadPort( 0x01, 0x01, input_port_1_r ),
                new IOReadPort( 0x02, 0x02, input_port_2_r ),
                new IOReadPort( 0x03, 0x03, invaders_shift_data_r ),
                new IOReadPort( -1 )  /* end of table */
        };
        /* Catch the write to unmapped I/O port 6 */
        public static WriteHandlerPtr invaders_dummy_write = new WriteHandlerPtr() {	public void handler(int offset, int data)
        {

        }};

	
        static IOWritePort writeport[] =
        {
                new IOWritePort(  0x02, 0x02, invaders_shift_amount_w ),
                new IOWritePort(  0x03, 0x03, invaders_sh_port3_w ),
                new IOWritePort(  0x04, 0x04, invaders_shift_data_w ),
                new IOWritePort(  0x05, 0x05, invaders_sh_port5_w ),
                new IOWritePort(  0x06, 0x06, invaders_dummy_write ),
                new IOWritePort(  -1 ) /* end of table */
        };

        static IOWritePort invdelux_writeport[] =
        {
                new IOWritePort(  0x02, 0x02, invaders_shift_amount_w ),
                new IOWritePort(  0x03, 0x03, invaders_sh_port3_w ),
                new IOWritePort(  0x04, 0x04, invaders_shift_data_w ),
                new IOWritePort(  0x05, 0x05, invaders_sh_port5_w ),
                new IOWritePort(  0x06, 0x06, invaders_dummy_write ),
                new IOWritePort(  -1 ) /* end of table */
        };
	
	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0x81,
			new int[] { OSD_KEY_3, OSD_KEY_2, OSD_KEY_1, 0,
					OSD_KEY_CONTROL, OSD_KEY_LEFT, OSD_KEY_RIGHT, 0 }
		),
		new InputPort(	/* IN1 */
			0x00,
			new int[] { 0, 0, OSD_KEY_T, 0,
					OSD_KEY_CONTROL, OSD_KEY_LEFT, OSD_KEY_RIGHT, 0 }
		),
		new InputPort( -1 )
	};
        static InputPort invdelux_input_ports[] =
        {
                new InputPort(       /* IN0 */
                        0xf4,
                        new int[]{ 0, 0, 0, 0, 0, 0, OSD_KEY_N, 0 }

                ),
                new InputPort(       /* IN1 */
                        0x81,
                        new int[]{ OSD_KEY_3, OSD_KEY_2, OSD_KEY_1, 0,
                                        OSD_KEY_CONTROL, OSD_KEY_LEFT, OSD_KEY_RIGHT, 0 }

                ),
                new InputPort(       /* IN2 */
                        0x00,
                        new int[]{ 0, 0, OSD_KEY_T, 0,
                                    OSD_KEY_CONTROL, OSD_KEY_LEFT, OSD_KEY_RIGHT, 0 }

                ),
                new InputPort( -1 )
        };

        static TrakPort trak_ports[] =
        {
               new TrakPort (-1 )
        };

        static KEYSet keys[] =
        {
                new KEYSet( 0, 5, "PL1 MOVE LEFT"  ),
                new KEYSet( 0, 6, "PL1 MOVE RIGHT" ),
                new KEYSet( 0, 4, "PL1 FIRE"       ),
                new KEYSet( 1, 5, "PL1 MOVE LEFT"  ),
                new KEYSet( 1, 6, "PL1 MOVE RIGHT" ),
                new KEYSet( 1, 4, "PL1 FIRE"       ),
                new KEYSet( -1 )
        };

        static KEYSet invaders_keys[] =
        {
                new KEYSet( 0, 5, "PL1 MOVE LEFT"  ),
                new KEYSet( 0, 6, "PL1 MOVE RIGHT" ),
                new KEYSet( 0, 4, "PL1 FIRE"       ),
                new KEYSet( 1, 5, "PL2 MOVE LEFT"  ),
                new KEYSet( 1, 6, "PL2 MOVE RIGHT" ),
                new KEYSet( 1, 4, "PL2 FIRE"       ),
                new KEYSet( -1 )
        };

        /* Deluxe uses set two to input the name */
        static KEYSet invdelux_keys[] =
        {
                new KEYSet( 1, 5, "PL1 MOVE LEFT"  ),
                new KEYSet( 1, 6, "PL1 MOVE RIGHT" ),
                new KEYSet( 1, 4, "PL1 FIRE"       ),
                new KEYSet( 2, 5, "PL2 MOVE LEFT"  ),
                new KEYSet( 2, 6, "PL2 MOVE RIGHT" ),
                new KEYSet( 2, 4, "PL2 FIRE"       ),
                new KEYSet( -1 )
        };
	static DSW dsw[] =
	{
		new DSW( 1, 0x03, "LIVES", new String[] { "3", "4", "5", "6" } ),
		new DSW( 1, 0x08, "BONUS", new String[] { "1500", "1000" }, 1 ),
		new DSW( -1 )
	};

        static  DSW invaders_dsw[] =
        {
                new DSW( 1, 0x03, "BASES", new String[]{ "3", "4", "5", "6" } ),
                new DSW( 1, 0x08, "BONUS", new String[]{ "1500", "1000" }, 1 ),
                new DSW( 1, 0x80, "COIN INFO", new String[]{ "ON", "OFF" }, 0 ),
                new DSW( -1 )
        };

        static  DSW invdelux_dsw[] =
        {
                new DSW( 2, 0x01, "BASES", new String[]{ "3", "4" }, 0),
                new DSW( 2, 0x08, "PRESET MODE", new String[]{ "OFF", "ON" }, 0 ),
                new DSW( 2, 0x80, "COIN INFO", new String[]{ "ON", "OFF" }, 0 ),
                new DSW( -1 )
        };

        static final char palette[] = /* V.V */ /* Smoothed pure colors, overlays are not so contrasted */
        {
                0x00,0x00,0x00, /* BLACK */
                0xff,0x20,0x20, /* RED */
                0x20,0xff,0x20, /* GREEN */
                0xff,0xff,0x20, /* YELLOW */
                0xff,0xff,0xff, /* WHITE */
                0x20,0xff,0xff, /* CYAN */
                0xff,0x20,0xff  /* PURPLE */
        };

	
	static final int BLACK = 0;
	static final int RED = 1;
	static final int GREEN = 2;
	static final int YELLOW = 3;
	static final int WHITE = 4;
	static final int CYAN = 5;
        static final int PURPLE = 6;

		
	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				2000000,	/* 2 Mhz? */
				0,
				readmem, writemem, readport, writeport,
				invaders_interrupt, 2	/* two interrupts per frame */
			)
		},
		60,
		null,
	
		/* video hardware */
		32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
		null,	/* no gfxdecodeinfo - bitmapped display */
		sizeof(palette)/3,0,
		null,
                VIDEO_TYPE_RASTER,
		null,
		invaders_vh_start,
		invaders_vh_stop,
		invaders_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		null,
		null,
		invaders_sh_update
	);
      	public static MachineDriver lrescue_machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
			CPU_Z80,
			2000000,        /* 2 Mhz? */
			0,
			readmem,lrescue_writemem,readport,writeport, /* V.V */
			invaders_interrupt,2    /* two interrupts per frame */
			)
		},
                60,
                null,

                /* video hardware */
                32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
                null,	/* no gfxdecodeinfo - bitmapped display */
                sizeof(palette)/3, 0,
                null,
                VIDEO_TYPE_RASTER,
                null,
                invaders_vh_start,
                invaders_vh_stop,
                invaders_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                null,
                null,
                invaders_sh_update
	);

public static MachineDriver invrvnge_machine_driver = new MachineDriver /* V.V */ /* Whole function */
(
	/* basic machine hardware */
	new MachineCPU[] {
			new MachineCPU(
			CPU_Z80,
			2000000,        /* 2 Mhz? */
			0,
			readmem,invrvnge_writemem,readport,writeport,
			invaders_interrupt,2    /* two interrupts per frame */
		)
        },
	60,
	null,

	/* video hardware */
	32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
	null,	/* no gfxdecodeinfo - bitmapped display */
	sizeof(palette)/3, 0,
	null,
        VIDEO_TYPE_RASTER,
	null,
	invaders_vh_start,
	invaders_vh_stop,
	invaders_vh_screenrefresh,

	/* sound hardware */
	null,
	null,
	null,
	null,
	invaders_sh_update
);



public static MachineDriver invdelux_machine_driver = new MachineDriver
(
	/* basic machine hardware */
	new MachineCPU[] {
			new MachineCPU(
			CPU_Z80,
			2000000,        /* 2 Mhz? */
			0,
			readmem,writemem,invdelux_readport, invdelux_writeport,
			invaders_interrupt,2    /* two interrupts per frame */
		)
        },
	60,
        null,

	/* video hardware */
	32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
	null,	/* no gfxdecodeinfo - bitmapped display */
	sizeof(palette)/3, 0,
	null,
        VIDEO_TYPE_RASTER,
	null,
	invaders_vh_start,
	invaders_vh_stop,
	invaders_vh_screenrefresh,

	/* sound hardware */
	null,
	null,
	null,
	null,
	invaders_sh_update
);


	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	static RomLoadPtr invaders_rom= new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "invaders.h",  0x0000, 0x0800, 0x734f5ad8);
		ROM_LOAD( "invaders.g",   0x0800, 0x0800, 0x6bfaca4a);
		ROM_LOAD( "invaders.f",   0x1000, 0x0800, 0x0ccead96);
		ROM_LOAD( "invaders.e",   0x1800, 0x0800, 0x14e538b0);
                ROM_END();
        }};
        static RomLoadPtr spaceatt_rom= new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "spaceatt.h", 0x0000, 0x0800, 0xa31d0756);
		ROM_LOAD( "spaceatt.g", 0x0800, 0x0800, 0xf41241f7);
		ROM_LOAD( "spaceatt.f", 0x1000, 0x0800, 0x4c060223);
		ROM_LOAD( "spaceatt.e", 0x1800, 0x0800, 0x7cf6f604);                             
                ROM_END();
        }};
        static RomLoadPtr invrvnge_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);     /* 64k for code */
                ROM_LOAD( "invrvnge.h", 0x0000, 0x0800, 0xaca41bbb);
                ROM_LOAD( "invrvnge.g", 0x0800, 0x0800, 0xcfe89dad);
                ROM_LOAD( "invrvnge.f", 0x1000, 0x0800, 0xe350de2c);
                ROM_LOAD( "invrvnge.e", 0x1800, 0x0800, 0x1ec8dfc8);                              
                ROM_END();
        }};
        static RomLoadPtr invdpt2m_rom= new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "invdelux.h", 0x0000, 0x0800, 0xe690818f);
		ROM_LOAD( "invdelux.g", 0x0800, 0x0800, 0x4268c12d);
		ROM_LOAD( "invdelux.f", 0x1000, 0x0800, 0xf4aa1880);
		ROM_LOAD( "invdelux.e", 0x1800, 0x0800, 0x408849c1);
		ROM_LOAD( "invdelux.d", 0x4000, 0x0800, 0xe8d5afcd);              
                ROM_END();
        }};
	static RomLoadPtr galxwars_rom= new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "galxwars.0", 0x0000, 0x0400, 0x608bfe7f);
		ROM_LOAD( "galxwars.1", 0x0400, 0x0400, 0xa810b258);
		ROM_LOAD( "galxwars.2", 0x0800, 0x0400, 0x74f31781);
		ROM_LOAD( "galxwars.3", 0x0c00, 0x0400, 0xc88f886c);
		ROM_LOAD( "galxwars.4", 0x4000, 0x0400, 0xae4fe8fb);
		ROM_LOAD( "galxwars.5", 0x4400, 0x0400, 0x37708a35);              
                ROM_END();
        }};
	static RomLoadPtr lrescue_rom= new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "lrescue.1", 0x0000, 0x0800, 0x2bbc4778);
		ROM_LOAD( "lrescue.2", 0x0800, 0x0800, 0x49e79706);
		ROM_LOAD( "lrescue.3", 0x1000, 0x0800, 0x1ac969be);
		ROM_LOAD( "lrescue.4", 0x1800, 0x0800, 0x782fee3c);
		ROM_LOAD( "lrescue.5", 0x4000, 0x0800, 0x58fde8bc);
		ROM_LOAD( "lrescue.6", 0x4800, 0x0800, 0xbfb0f65d);
                ROM_END();
        }};
	static RomLoadPtr desterth_rom= new RomLoadPtr(){ public void handler()  
        {            
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "36_h.bin", 0x0000, 0x0800, 0xf86923e5);
		ROM_LOAD( "35_g.bin", 0x0800, 0x0800, 0x797f440d);
		ROM_LOAD( "34_f.bin", 0x1000, 0x0800, 0x993d0846);
		ROM_LOAD( "33_e.bin", 0x1800, 0x0800, 0x8d155fc5);
		ROM_LOAD( "32_d.bin", 0x4000, 0x0800, 0x3f531b6f);
		ROM_LOAD( "31_c.bin", 0x4800, 0x0800, 0xab019c30);
		ROM_LOAD( "42_b.bin", 0x5000, 0x0800, 0xed9dbac6);
                ROM_END();
        }};
	
	static String invaders_sample_names[] =
	{
		"0.raw",
		"1.raw",
		"2.raw",
		"3.raw",
		"4.raw",
		"5.raw",
		"6.raw",
		"7.raw",
		"8.raw",
		null	/* end of array */
	};



	static HiscoreLoadPtr invaders_hiload = new HiscoreLoadPtr() { public int handler()
	{
		/* check if the hi score table has already been initialized */
	/*TOFIX	if (memcmp(RAM, 0x20f4, new char[] { 0x00, 0x00 }, 2) == 0)
		{
			FILE f;
	
	
			if ((f = fopen(name, "rb")) != null)
			{
				fread(RAM, 0x20f4, 1, 2, f);
				fclose(f);
			}
	
			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };



	static HiscoreSavePtr invaders_hisave = new HiscoreSavePtr() { public void handler()
	{
		FILE f;
	
	
	/*TOFIX	if ((f = fopen(name, "wb")) != null)
		{
			fwrite(RAM, 0x20f4, 1, 2, f);
			fclose(f);
		}*/
	} };
        static HiscoreLoadPtr invdelux_hiload = new HiscoreLoadPtr() { public int handler()
        {
                /* check if the hi score table has already been initialized */
       /*TOFIX         if (memcmp(RAM, 0x2340, new char[] { 0x1b, 0x1b }, 2) == 0)
                {
                        FILE f;

                        if ((f = fopen(name, "rb")) != null)
                        {
                 /* Load the actual score */
     /*TOFIX                      fread(RAM,0x20f4,1, 0x2,f);
                 /* Load the name */
    /*TOFIX                       fread(RAM,0x2340,1, 0xa,f);
                           fclose(f);
                        }

                        return 1;
                }
                else */return 0;  /* we can't load the hi scores yet */
        }};
        static HiscoreSavePtr invdelux_hisave = new HiscoreSavePtr() { public void handler()
        {

     /*TOFIX           FILE f;

                if ((f = fopen(name,"wb")) != null)
                {
              /* Save the actual score */
    /*TOFIX                    fwrite(RAM,0x20f4,1, 0x2,f);
              /* Save the name */
  /*TOFIX                      fwrite(RAM,0x2340,1, 0xa,f);
                        fclose(f);
                }*/
        }};
        static HiscoreLoadPtr lrescue_hiload = new HiscoreLoadPtr() { public int handler()
        {
                /* check if the hi score table has already been initialized */
      /*TOFIX          if (memcmp(RAM, 0x20CF, new char[] { 0x1b, 0x1b }, 2) == 0)
                {
                        FILE f;

                        if ((f = fopen(name,"rb")) != null)
                       {
                 /* Load the actual score */
    /*TOFIX                       fread(RAM,0x20F4,1, 0x2,f);
                 /* Load the name */
  /*TOFIX                         fread(RAM,0x20CF,1, 0xa,f);
                 /* Load the high score length */
  /*TOFIX                         fread(RAM,0x20DB,1, 0x1,f);
                           fclose(f);
                        }

                        return 1;
                }
                else */return 0;  /* we can't load the hi scores yet */

        }};
        static HiscoreLoadPtr desterth_hiload = new HiscoreLoadPtr() { public int handler()
        {
        /* check if the hi score table has already been initialized */
     /*TOFIX       if (memcmp(RAM, 0x20CF, new char[] { 0x1b, 0x07 }, 2) == 0)
            {
                        FILE f;

                        if ((f = fopen(name,"rb")) != null)
                       {
                 /* Load the actual score */
     /*TOFIX                      fread(RAM,0x20F4,1, 0x2,f);
                 /* Load the name */
     /*TOFIX                      fread(RAM,0x20CF,1, 0xa,f);
                 /* Load the high score length */
     /*TOFIX                      fread(RAM,0x20DB,1, 0x1,f);
    /*TOFIX                            fclose(f);
                        }

                        return 1;
                }
                else */return 0;   /* we can't load the hi scores yet */

        }};


        static HiscoreSavePtr lrescue_hisave = new HiscoreSavePtr() { public void handler()
        {
                FILE f;

    /*TOFIX            if ((f = fopen(name,"wb")) != null)
                {
              /* Save the actual score */
   /*TOFIX                    fwrite(RAM,0x20F4,1,0x02,f);
              /* Save the name */
    /*TOFIX                    fwrite(RAM,0x20CF,1,0xa,f);
              /* Save the high score length */
    /*TOFIX                    fwrite(RAM,0x20DB,1,0x1,f);
                        fclose(f);
                }*/
        }};
	
	public static GameDriver invaders_driver = new GameDriver
	(
                "Space Invaders",
		"invaders",
                "MICHAEL STRUTTS\nNICOLA SALMORIA\nTORMOD TJABERG\nMIRKO BUFFONI\nVALERIO VERRANDO",    /* V.V */
		machine_driver,
	
		invaders_rom,
		null, null,
		invaders_sample_names,
	
		input_ports,null, trak_ports, invaders_dsw, invaders_keys,
	
		null, palette, null,
		ORIENTATION_DEFAULT,
	
		invaders_hiload, invaders_hisave
	);
	
	public static GameDriver earthinv_driver = new GameDriver
	(
                "Super Earth Invasion",
		"earthinv",
                "MICHAEL STRUTTS\nNICOLA SALMORIA\nTORMOD TJABERG\nMIRKO BUFFONI\nVALERIO VERRANDO",    /* V.V */
		machine_driver,
	
		invaders_rom,
		null, null,
		invaders_sample_names,
	
		input_ports,null, trak_ports, dsw, keys,
	
		null, palette, null,
		ORIENTATION_DEFAULT,
	
		null, null
	);
	
	public static GameDriver spaceatt_driver = new GameDriver
	(
                "Space Attack II",
		"spaceatt",
                "MICHAEL STRUTTS\nNICOLA SALMORIA\nTORMOD TJABERG\nMIRKO BUFFONI\nVALERIO VERRANDO",    /* V.V */
		machine_driver,
	
		spaceatt_rom,
		null, null,
		invaders_sample_names,
	
		input_ports,null, trak_ports, dsw, keys,
	
		null, palette, null,
		ORIENTATION_DEFAULT,
	
		null, null
	);
        public static GameDriver invrvnge_driver = new GameDriver
        (
                "Invaders Revenge",
                "invrvnge",
                "MICHAEL STRUTTS\nNICOLA SALMORIA\nTORMOD TJABERG\nMIRKO BUFFONI",
                invrvnge_machine_driver,

                invrvnge_rom,
                null, null,
                invaders_sample_names,

                input_ports,null, trak_ports, dsw, keys,

                null, palette, null,
                ORIENTATION_DEFAULT,

                null, null
        );
	public static GameDriver invdpt2m_driver = new GameDriver
	(
                "Invaders Deluxe",
		"invdpt2m",
                "MICHAEL STRUTTS\nNICOLA SALMORIA\nTORMOD TJABERG\nMIRKO BUFFONI\nVALERIO VERRANDO",    /* V.V */
		invdelux_machine_driver,
	
		invdpt2m_rom,
		null, null,
		invaders_sample_names,
	
		invdelux_input_ports,null, trak_ports, invdelux_dsw, invdelux_keys,
	
		null, palette, null,
		ORIENTATION_DEFAULT,
	
		invdelux_hiload, invdelux_hisave
	);
	
	public static GameDriver galxwars_driver = new GameDriver
	(
                "Galaxy Wars",
		"galxwars",
                "MICHAEL STRUTTS\nNICOLA SALMORIA\nTORMOD TJABERG\nMIRKO BUFFONI\nVALERIO VERRANDO",    /* V.V */
		machine_driver,
	
		galxwars_rom,
		null, null,
		invaders_sample_names,
	
		input_ports,null, trak_ports, dsw, keys,
	
		null, palette, null,
		ORIENTATION_DEFAULT,
	
		null, null
	);
	
	public static GameDriver lrescue_driver = new GameDriver
	(
                "Lunar Rescue",
		"lrescue",
                "MICHAEL STRUTTS\nNICOLA SALMORIA\nTORMOD TJABERG\nMIRKO BUFFONI\nVALERIO VERRANDO",    /* V.V */
		lrescue_machine_driver,
	
		lrescue_rom,
		null, null,
		invaders_sample_names,
	
		input_ports,null, trak_ports, dsw, keys,
	
		null, palette, null,
		ORIENTATION_DEFAULT,
	
		lrescue_hiload, lrescue_hisave
	);
	
	public static GameDriver desterth_driver = new GameDriver
	(
                "Destination Earth",
		"desterth",
                "MICHAEL STRUTTS\nNICOLA SALMORIA\nTORMOD TJABERG\nMIRKO BUFFONI\nVALERIO VERRANDO",    /* V.V */
		machine_driver,
	
		desterth_rom,
		null, null,
		invaders_sample_names,
	
		input_ports,null, trak_ports, dsw, keys,
	
		null, palette, null,
		ORIENTATION_DEFAULT,
	
		desterth_hiload, lrescue_hisave
	);
}
