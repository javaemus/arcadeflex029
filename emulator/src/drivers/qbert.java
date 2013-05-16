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
 *  Notes : Roms are from v0.36 romset
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
public class qbert 
{
	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x57ff, MRA_RAM ),
		new MemoryReadAddress( 0x5800, 0x5800, input_port_0_r ),     /* DSW */
		new MemoryReadAddress( 0x5801, 0x5801, qbert_IN1_r ),     /* buttons */
		new MemoryReadAddress( 0x5802, 0x5802, input_port_2_r ),     /* trackball: not used */
		new MemoryReadAddress( 0x5803, 0x5803, input_port_3_r ),     /* trackball: not used */
		new MemoryReadAddress( 0x5804, 0x5804, input_port_4_r ),     /* joystick */
		new MemoryReadAddress( 0xA000, 0xffff, MRA_ROM ),
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
		new MemoryWriteAddress( 0xa000, 0xffff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	static MemoryReadAddress gottlieb_sound_readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x01ff, riot_ram_r ),
		new MemoryReadAddress( 0x0200, 0x03ff, gottlieb_riot_r ),
		new MemoryReadAddress( 0x4000, 0x6fff, gottlieb_sound_expansion_socket_r ),
		new MemoryReadAddress( 0x7000, 0x7fff, MRA_ROM ),
				 /* A15 not decoded except in socket expansion */
		new MemoryReadAddress( 0x8000, 0x81ff, riot_ram_r ),
		new MemoryReadAddress( 0x8200, 0x83ff, gottlieb_riot_r ),
		new MemoryReadAddress( 0xc000, 0xefff, gottlieb_sound_expansion_socket_r ),
		new MemoryReadAddress( 0xf000, 0xffff, MRA_ROM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress gottlieb_sound_writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x01ff, riot_ram_w ),
		new MemoryWriteAddress( 0x0200, 0x03ff, gottlieb_riot_w ),
		new MemoryWriteAddress( 0x1000, 0x1000, gottlieb_amplitude_DAC_w ),
		new MemoryWriteAddress( 0x2000, 0x2000, gottlieb_speech_w ),
		new MemoryWriteAddress( 0x3000, 0x3000, gottlieb_speech_clock_DAC_w ),
		new MemoryWriteAddress( 0x4000, 0x6fff, gottlieb_sound_expansion_socket_w ),
		new MemoryWriteAddress( 0x7000, 0x7fff, MWA_ROM ),
				 /* A15 not decoded except in socket expansion */
		new MemoryWriteAddress( 0x8000, 0x81ff, riot_ram_w ),
		new MemoryWriteAddress( 0x8200, 0x83ff, gottlieb_riot_w ),
		new MemoryWriteAddress( 0x9000, 0x9000, gottlieb_amplitude_DAC_w ),
		new MemoryWriteAddress( 0xa000, 0xa000, gottlieb_speech_w ),
		new MemoryWriteAddress( 0xb000, 0xb000, gottlieb_speech_clock_DAC_w ),
		new MemoryWriteAddress( 0xc000, 0xefff, gottlieb_sound_expansion_socket_w ),
		new MemoryWriteAddress( 0xf000, 0xffff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};



        static InputPort input_ports[] =
        {
                 new InputPort(        /* DSW */
                        0x0,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(       /* buttons */
                        0x40,   /* test mode off */
                        new int[]{ OSD_KEY_1, OSD_KEY_2, OSD_KEY_3, 0 /* coin 2 */,
                                        0, 0, 0, OSD_KEY_F2 }
                ),
		new InputPort(       /* trackball: not used */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(       /* trackball: not used */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(       /* 2 joysticks (cocktail mode) mapped to one */
                        0x00,
                        new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_UP, OSD_KEY_DOWN,
                                OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_UP, OSD_KEY_DOWN }
                ),
		new InputPort( -1 )  /* end of table */
        };

        static TrakPort[] trak_ports =
       {
           new TrakPort(-1)
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
                new DSW( 0, 0x08, "AUTO ROUND ADVANCE", new String[]{ "OFF","ON" } ),
                new DSW( 0, 0x01, "ATTRACT MODE SOUND", new String[]{ "ON", "OFF" } ),
                new DSW( 0, 0x10, "FREE PLAY", new String[]{ "OFF" , "ON" } ),
                new DSW( 0, 0x04, "", new String[]{ "UPRIGHT", "COCKTAIL" } ),
                new DSW( 0, 0x02, "KICKER", new String[]{ "OFF", "ON" } ),
        /* the following switch must be connected to the IP16 line */
        /*      { 1, 0x40, "TEST MODE", {"ON", "OFF"} },*/
                new DSW( -1 )
        };


        static GfxLayout charlayout = new GfxLayout
	(
		8,8,    /* 8*8 characters */
		256,    /* 256 characters */
		4,      /* 4 bits per pixel */
		new int[]{ 0, 1, 2, 3 },
		new int[]{ 0, 4, 8, 12, 16, 20, 24, 28},
		new int[]{ 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		32*8    /* every char takes 32 consecutive bytes */
        );

        static GfxLayout spritelayout = new GfxLayout
	(
		16,16,  /* 16*16 sprites */
		256,    /* 256 sprites */
		4,      /* 4 bits per pixel */
		new int[]{ 0, 0x2000*8, 0x4000*8, 0x6000*8 },
		new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
		new int[]{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		32*8    /* every sprite takes 32 consecutive bytes */
        );



        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, charlayout,   0, 1 ),
                new GfxDecodeInfo( 1, 0x2000, spritelayout, 0, 1 ),
                new GfxDecodeInfo( -1) /* end of array */
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
				3579545/4,
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


        static RomLoadPtr qbert_rom = new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);     /* 64k for code */
                ROM_LOAD( "qb-rom2.bin",  0xa000, 0x2000, 0xfe434526 );
                ROM_LOAD( "qb-rom1.bin",  0xc000, 0x2000, 0x55635447 );
                ROM_LOAD( "qb-rom0.bin",  0xe000, 0x2000, 0x8e318641 );

                ROM_REGION(0xA000);      /* temporary space for graphics */
                ROM_LOAD( "qb-bg0.bin",   0x0000, 0x1000, 0x7a9ba824 );	/* chars */
                ROM_LOAD( "qb-bg1.bin",   0x1000, 0x1000, 0x22e5b891 );
                ROM_LOAD( "qb-fg3.bin",   0x2000, 0x2000, 0xdd436d3a );	/* sprites */
                ROM_LOAD( "qb-fg2.bin",   0x4000, 0x2000, 0xf69b9483 );
                ROM_LOAD( "qb-fg1.bin",   0x6000, 0x2000, 0x224e8356 );
                ROM_LOAD( "qb-fg0.bin",   0x8000, 0x2000, 0x2f695b85 );
                
                ROM_REGION(0x10000);     /* 64k for sound cpu */
		ROM_LOAD( "qb-snd1.bin", 0xf000, 0x800, 0x469952eb );
		ROM_RELOAD(0x7000, 0x800);/* A15 is not decoded */
		ROM_LOAD( "qb-snd2.bin", 0xf800, 0x800, 0x200e1d22 );
		ROM_RELOAD(0x7800, 0x800);/* A15 is not decoded */
                ROM_END();
        }};

        static RomLoadPtr qbertjp_rom = new RomLoadPtr(){ public void handler() 
        {      
                ROM_REGION(0x10000);     /* 64k for code */
                ROM_LOAD( "qbj-rom2.bin", 0xa000, 0x2000, 0x67bb1cb2 );
                ROM_LOAD( "qbj-rom1.bin", 0xc000, 0x2000, 0xc61216e7 );
                ROM_LOAD( "qbj-rom0.bin", 0xe000, 0x2000, 0x69679d5c );

                ROM_REGION(0xA000);      /* temporary space for graphics */
                ROM_LOAD( "qb-bg0.bin",   0x0000, 0x1000, 0x7a9ba824 );	/* chars */
                ROM_LOAD( "qb-bg1.bin",   0x1000, 0x1000, 0x22e5b891 );
                ROM_LOAD( "qb-fg3.bin",   0x2000, 0x2000, 0xdd436d3a );	/* sprites */
                ROM_LOAD( "qb-fg2.bin",   0x4000, 0x2000, 0xf69b9483 );
                ROM_LOAD( "qb-fg1.bin",   0x6000, 0x2000, 0x224e8356 );
                ROM_LOAD( "qb-fg0.bin",   0x8000, 0x2000, 0x2f695b85 );
                
                ROM_REGION( 0x10000);	/* 64k for sound cpu */
                ROM_LOAD( "qb-snd1.bin",  0xf000, 0x800, 0x15787c07 );
                ROM_RELOAD(               0x7000, 0x800); /* A15 is not decoded */
                ROM_LOAD( "qb-snd2.bin",  0xf800, 0x800, 0x58437508 );
                ROM_RELOAD(               0x7800, 0x800); /* A15 is not decoded */
                
                ROM_END();
        }};


        static String gottlieb_sample_names[] =
        {
                "FX_00.SAM",
                "FX_01.SAM",
                "FX_02.SAM",
                "FX_03.SAM",
                "FX_04.SAM",
                "FX_05.SAM",
                "FX_06.SAM",
                "FX_07.SAM",
                "FX_08.SAM",
                "FX_09.SAM",
                "FX_10.SAM",
                "FX_11.SAM",
                "FX_12.SAM",
                "FX_13.SAM",
                "FX_14.SAM",
                "FX_15.SAM",
                "FX_16.SAM",
                "FX_17.SAM",
                "FX_18.SAM",
                "FX_19.SAM",
                "FX_20.SAM",
                "FX_21.SAM",
                "FX_22.SAM",
                "FX_23.SAM",
                "FX_24.SAM",
                "FX_25.SAM",
                "FX_26.SAM",
                "FX_27.SAM",
                "FX_28.SAM",
                "FX_29.SAM",
                "FX_30.SAM",
                "FX_31.SAM",
                "FX_32.SAM",
                "FX_33.SAM",
                "FX_34.SAM",
                "FX_35.SAM",
                "FX_36.SAM",
                "FX_37.SAM",
                "FX_38.SAM",
                "FX_39.SAM",
                "FX_40.SAM",
                "FX_41.SAM",
                null       /* end of array */
        };



 

      static HiscoreLoadPtr hiload = new HiscoreLoadPtr()
      {
        public int handler()
        {
       /*TOFIX         FILE f=fopen(name,"rb");
                char[] RAM = Machine.memory_region[0];

                if (f!=null) {
                        fread(RAM,0xA00,1,2,f); /* hiscore table checksum */
   /*TOFIX                     fread(RAM,0xA02,23,10,f); /* 23 hiscore ascending entries: name (3 chars) + score (7 figures) */
   /*TOFIX                     fread(RAM,0xBB0,12,1,f); /* operator parameters : coins/credits, lives, extra-lives points */
   /*TOFIX                     fclose(f);
                }*/
                return 1;
        }};
      static HiscoreSavePtr hisave = new HiscoreSavePtr()
      {
        public void handler()
        {
   /*TOFIX             FILE f=fopen(name,"wb");
                char[] RAM = Machine.memory_region[0];

                if (f!=null) {
                        fwrite(RAM,0xA00,1,2,f); /* hiscore table checksum */
   /*TOFIX                     fwrite(RAM,0xA02,23,10,f); /* 23 hiscore ascending entries: name (3 chars) + score (7 figures) */
    /*TOFIX                    fwrite(RAM,0xBB0,12,1,f); /* operator parameters : coins/credits, lives, extra-lives points */
     /*TOFIX                   fclose(f);
                }*/
        }};

      static HiscoreLoadPtr hiload_jp = new HiscoreLoadPtr()
      {
        public int handler()
        {
    /*TOFIX            FILE f=fopen(name,"rb");
                char[] RAM = Machine.memory_region[0];

                if (f!=null) {
                        fread(RAM,0xA00,1,2,f); /* hiscore table checksum */
/*TOFIX                        fread(RAM,0xA02,23,10,f); /* 23 hiscore ascending entries: name (3 chars) + score (7 figures) */
/*TOFIX                        fread(RAM,0xC0C,12,1,f); /* operator parameters : coins/credits, lives, extra-lives points */
/*TOFIX                        fclose(f);
                }*/
                return 1;
        }};
      static HiscoreSavePtr hisave_jp = new HiscoreSavePtr()
      {
        public void handler()
        {
  /*TOFIX              FILE f=fopen(name,"wb");
                char[] RAM = Machine.memory_region[0];

                if (f!=null) {
                        fwrite(RAM,0xA00,1,2,f); /* hiscore table checksum */
  /*TOFIX                      fwrite(RAM,0xA02,23,10,f); /* 23 hiscore ascending entries: name (3 chars) + score (7 figures) */
  /*TOFIX                      fwrite(RAM,0xC0C,12,1,f); /* operator parameters : coins/credits, lives, extra-lives points */
  /*TOFIX                      fclose(f);
                }*/
        }};

 	public static GameDriver qbert_driver = new GameDriver
	(
		"Q*Bert (US version)",
		"qbert",
		"FABRICE FRANCES\n\nDEDICATED TO:\nJEFF LEE\nWARREN DAVIES\nDAVID THIEL",
		machine_driver,
	
		qbert_rom,
		null, null,   /* rom decode and opcode decode functions */
		null,
	
		input_ports, null, trak_ports, dsw, keys,
	
		null, null, null,
		ORIENTATION_ROTATE_270,
	
		hiload,hisave     /* hi-score load and save */
	);
	
	public static GameDriver qbertjp_driver = new GameDriver
	(
		"Q*Bert (Japanese version)",
		"qbertjp",
		"FABRICE FRANCES\n\nDEDICATED TO:\nJEFF LEE\nWARREN DAVIES\nDAVID THIEL",
		machine_driver,
	
		qbertjp_rom,
		null, null,   /* rom decode and opcode decode functions */
		null,
	
		input_ports, null, trak_ports, dsw, keys,
	
		null, null, null,
		ORIENTATION_ROTATE_270,
	
		hiload_jp,hisave_jp     /* hi-score load and save */
	);
    
}
