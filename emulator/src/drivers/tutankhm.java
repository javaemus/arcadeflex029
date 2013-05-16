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
 *  uses romset from v0.36
 */
package drivers;
import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static sndhrdw.generic.*;
import static sndhrdw._8910intf.*;
import static machine.tutankhm.*;
import static vidhrdw.tutankhm.*;
import static sndhrdw.tutankhm.*;
import static mame.memoryH.*;
public class tutankhm {
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x7fff, MRA_RAM ),
                new MemoryReadAddress( 0x8800, 0x8fff, MRA_RAM ),		/* Game RAM */
                new MemoryReadAddress( 0xa000, 0xffff, MRA_ROM ),		/* Game ROM */
                new MemoryReadAddress( 0x9000, 0x9fff, tut_bankedrom_r ),	/* Graphics ROMs ra1_1i.cpu - ra1_9i.cpu (See address $8300 for usage) */
                new MemoryReadAddress( 0x81a0, 0x81a0, input_port_2_r ),	/* Player 1 I/O */
                new MemoryReadAddress( 0x81c0, 0x81c0, input_port_3_r ),	/* Player 2 I/O */
                new MemoryReadAddress( 0x8120, 0x8120, tut_rnd_r ),
                new MemoryReadAddress( 0x8180, 0x8180, input_port_1_r ),	/* I/O: Coin slots, service, 1P/2P buttons */
                new MemoryReadAddress( 0x8160, 0x8160, input_port_0_r ),	/* DSW2 (inverted bits) */
                new MemoryReadAddress( 0x81e0, 0x81e0, input_port_4_r ),	/* DSW1 (inverted bits) */
                new MemoryReadAddress( 0x8200, 0x8200, MRA_RAM ),	        /* 1 IRQ can be fired, 0 IRQ can't be fired */
                new MemoryReadAddress( 0x8205, 0x8205, MRA_RAM ),            /* Sound amplification on/off? */

                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x7fff, tut_videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x8800, 0x8fff, MWA_RAM ),		                /* Game RAM */
                new MemoryWriteAddress( 0x8000, 0x800f, tut_palette_w, tut_paletteram ),	/* Palette RAM */
                new MemoryWriteAddress( 0x8100, 0x8100, MWA_RAM, tut_scrollx ),              /* video x pan hardware reg */
                new MemoryWriteAddress( 0x8300, 0x8300, tut_bankselect_w ),	                /* Graphics bank select */
                new MemoryWriteAddress( 0x8200, 0x8200, interrupt_enable_w ),		        /* 1 IRQ can be fired, 0 IRQ can't be fired */
                new MemoryWriteAddress( 0x8202, 0x8203, MWA_RAM ),
                new MemoryWriteAddress( 0x8205, 0x8207, MWA_RAM ),
                new MemoryWriteAddress( 0x8700, 0x8700, sound_command_w ),
                new MemoryWriteAddress( 0xa000, 0xffff, MWA_ROM ),		                /* Game ROM */
                new MemoryWriteAddress( -1 ) /* end of table */
        };


        static MemoryReadAddress sound_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x2fff, MRA_ROM ),            	/* Z80 sound ROM */
                new MemoryReadAddress( 0x3000, 0x3fff, MRA_RAM ),            	/* RAM ??? */
                new MemoryReadAddress( 0x4000, 0x4000, AY8910_read_port_0_r ),	/* Master AY3-8910 (data ???) */
                new MemoryReadAddress( 0x6000, 0x6000, AY8910_read_port_1_r ),	/* Other AY3-8910 (data ???) */
                new MemoryReadAddress( -1 )	/* end of table */

        };

        static MemoryWriteAddress sound_writemem[] =
        {
                new MemoryWriteAddress( 0x3000, 0x3fff, MWA_RAM ),            	/* RAM ??? */
                new MemoryWriteAddress( 0x4000, 0x4000, AY8910_write_port_0_w ),	/* Master AY3-8910 (data ???) */
                new MemoryWriteAddress( 0x5000, 0x5000, AY8910_control_port_0_w ),	/* Master AY3-8910 (control ???) */
                new MemoryWriteAddress( 0x6000, 0x6000, AY8910_write_port_1_w ),	/* Other AY3-8910 (data ???) */
                new MemoryWriteAddress( 0x7000, 0x7000, AY8910_control_port_1_w ),	/* Other AY3-8910 (control ???) */
                new MemoryWriteAddress( 0x8000, 0x8000, MWA_RAM ),			/* ??? */
                new MemoryWriteAddress( 0x0000, 0x2fff, MWA_ROM ),			/* Z80 sound ROM */
                new MemoryWriteAddress( -1 ) /* end of table */
        };

        static InputPort input_ports[] =
        {
                new InputPort(	/* DSW2 */
                        0xff,	/* default_value */
                         new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }	/* not affected by keyboard */
                ),
		new InputPort(	/* I/O: Coin slots, service, 1P/2P buttons */
                        0xff,	/* default_value */
                         new int[]{ OSD_KEY_3, 0, 0, OSD_KEY_1, OSD_KEY_2, 0, 0, 0 }
                ),
		new InputPort(	/* Player 1 I/O */
                        0xff,	/* default_value */
                         new int[]{ OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_CONTROL, OSD_KEY_ALT, OSD_KEY_SPACE, 0 }
                ),
		new InputPort(	/* Player 2 I/O */
                        0xff,	/* default_value */
                        new int[]{ OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_CONTROL, OSD_KEY_ALT, OSD_KEY_SPACE, 0 }
                       
                ),
		new InputPort(	/* Dip Switch 1 */
                        0xff,	/* default_value */
                         new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }	/* not affected by keyboard */
                        
                ),
		new InputPort( -1 )	/* end of table */
        };

         static TrakPort[] trak_ports =
       {
           new TrakPort(-1)
       };


        static DSW dsw[] =
        {

                new DSW( 4, 0x0f, "COIN1", new String[]{ "FREE PLAY",
                                    "4 COINS  3 CREDITS",
                                    "4 COINS  1 CREDIT",
                                    "3 COINS  4 CREDITS",
                                    "3 COINS  2 CREDITS",
                                    "3 COINS  1 CREDITS",
                                    "2 COINS  5 CREDITS",
                                    "2 COINS  3 CREDITS",
                                    "2 COINS  1 CREDIT",
                                    "1 COIN  7 CREDITS",
                                    "1 COIN  6 CREDITS",
                                    "1 COIN  5 CREDITS",
                                    "1 COIN  4 CREDITS",
                                    "1 COIN  3 CREDITS",
                                    "1 COIN  2 CREDITS",
                                    "1 COIN  1 CREDIT", }, 1 ),

                new DSW( 0, 0x03, "LIVES", new String[]{ "256", "4", "5", "3" }, 1 ),
                new DSW( 0, 0x04, "CABINET", new String[]{ "TABLE", "UPRIGHT" }, 0 ),
                new DSW( 0, 0x08, "BONUS", new String[]{ "30000", "40000" }, 0 ),
                new DSW( 0, 0x30, "DIFFICULTY", new String[]{ "HARDEST", "MEDIUM", "HARD", "EASY" }, 1 ),
                new DSW( 0, 0x40, "BOMB", new String[]{ "1 PER GAME", "3 PER GAME" }, 0 ),
                new DSW( 0, 0x80, "ATTRACT MUSIC", new String[]{ "OFF", "ON" }, 0 ),
                new DSW( -1 )
        };

        static KEYSet keys[] =
        {
                new KEYSet( 2, 2, "P1 MOVE UP" ),
                new KEYSet( 2, 0, "P1 MOVE LEFT"  ),
                new KEYSet( 2, 1, "P1 MOVE RIGHT" ),
                new KEYSet( 2, 3, "P1 MOVE DOWN" ),
                new KEYSet( 2, 4, "P1 FIRE LEFT" ),
                new KEYSet( 2, 5, "P1 FIRE RIGHT" ),
                new KEYSet( 2, 6, "P1 FLASH BOMB" ),
                new KEYSet( 3, 2, "P2 MOVE UP" ),
                new KEYSet( 3, 0, "P2 MOVE LEFT"  ),
                new KEYSet( 3, 1, "P2 MOVE RIGHT" ),
                new KEYSet( 3, 3, "P2 MOVE DOWN" ),
                new KEYSet( 3, 4, "P2 FIRE LEFT" ),
                new KEYSet( 3, 5, "P2 FIRE RIGHT" ),
                new KEYSet( 3, 6, "P2 FLASH BOMB" ),
                new KEYSet( -1 )
        };


        /* there's nothing here, this is just a placeholder to let the video hardware */
        /* pick the background color table. */
        static  GfxLayout fakelayout = new GfxLayout(
        
                1,1,
                0,
                4,	/* 4 bits per pixel */
                new int[]{ 0 },
                 new int[]{ 0 },
                 new int[]{ 0 },
                0
        );


        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 0, 0, fakelayout, 0, 1 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };
        static MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware  */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_M6809,
                                1500000,			/* 2.5 Mhz ??? */
                                0,				/* memory region # 0 */
                                readmem,			/* MemoryReadAddress */
                                writemem,			/* MemoryWriteAddress */
                                null,				/* IOReadPort */
                                null,				/* IOWritePort */
                                tutankhm_interrupt,			/* interrupt routine */
                                1				/* interrupts per frame */
                        ),
			new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2100000,			/* 2.000 Mhz ??? */
                                2,				/* memory region # 2 */
                                sound_readmem,			/* MemoryReadAddress */
                                sound_writemem,			/* MemoryWriteAddress */
                                null,				/* IOReadPort */
                                null,				/* IOWritePort */
                                tutankhm_sh_interrupt, 		/* interrupt routine */
                                10			        /* interrupts per frame */
                        )
                },
                60,						/* frames per second */
                tutankhm_init_machine,				/* init machine routine */

                /* video hardware */
                32*8, 32*8,			                /* screen_width, screen_height */
                new rectangle( 2, 8*32-3, 0*32, 8*32-1 ),			/* struct rectangle visible_area */
                gfxdecodeinfo,					/* GfxDecodeInfo * */
                1+16,                                  /* total colors */
                16,                                      /* color table length */
                tut_vh_convert_color_prom,			/* convert color prom routine */

                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
                null,						/* vh_init routine */
                generic_vh_start,					/* vh_start routine */
                generic_vh_stop,					/* vh_stop routine */
                tut_vh_screenrefresh,				/* vh_update routine */

                /* sound hardware */
                null,						/* pointer to samples */
                null,						/* sh_init routine */
                tutankhm_sh_start,				/* sh_start routine */
                AY8910_sh_stop,					/* sh_stop routine */
                AY8910_sh_update				/* sh_update routine */
        );

        static RomLoadPtr tutankhm_rom= new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION( 0x10000 + 0x1000 * 12 );      /* 64k for M6809 CPU code + ( 4k * 12 ) for ROM banks */
                ROM_LOAD( "h1.bin",       0x0a000, 0x1000, 0xda18679f ); /* program ROMs */
                ROM_LOAD( "h2.bin",       0x0b000, 0x1000, 0xa0f02c85 );
                ROM_LOAD( "h3.bin",       0x0c000, 0x1000, 0xea03a1ab );
                ROM_LOAD( "h4.bin",       0x0d000, 0x1000, 0xbd06fad0 );
                ROM_LOAD( "h5.bin",       0x0e000, 0x1000, 0xbf9fd9b0 );
                ROM_LOAD( "h6.bin",       0x0f000, 0x1000, 0xfe079c5b );
                ROM_LOAD( "j1.bin",       0x10000, 0x1000, 0x7eb59b21 ); /* graphic ROMs (banked) -- only 9 of 12 are filled */
                ROM_LOAD( "j2.bin",       0x11000, 0x1000, 0x6615eff3 );
                ROM_LOAD( "j3.bin",       0x12000, 0x1000, 0xa10d4444 );
                ROM_LOAD( "j4.bin",       0x13000, 0x1000, 0x58cd143c );
                ROM_LOAD( "j5.bin",       0x14000, 0x1000, 0xd7e7ae95 );
                ROM_LOAD( "j6.bin",       0x15000, 0x1000, 0x91f62b82 );
                ROM_LOAD( "j7.bin",       0x16000, 0x1000, 0xafd0a81f );
                ROM_LOAD( "j8.bin",       0x17000, 0x1000, 0xdabb609b );
                ROM_LOAD( "j9.bin",       0x18000, 0x1000, 0x8ea9c6a6 );

                ROM_REGION( 0x1000 ); /* ROM Region 1 -- discarded */
                ROM_LOAD( "h6.bin", 0x0000, 0x1000, 0xfe079c5b );	/* not needed - could be removed */

                ROM_REGION( 0x10000 ); /* 64k for Z80 sound CPU code */
           	ROM_LOAD( "11-7a.bin",    0x0000, 0x1000, 0xb52d01fa );
                ROM_LOAD( "10-8a.bin",    0x1000, 0x1000, 0x9db5c0ce );
                ROM_END();
        }};


        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
	{
           FILE f;

           /* get RAM pointer (this game is multiCPU, we can't assume the global */
           /* RAM pointer is pointing to the right place) */
           char []RAM = Machine.memory_region[0];

           /* check if the hi score table has already been initialized */
           if (memcmp(RAM,0x88d9,new char[]{0x01,0x00},2) == 0)
           {
              if ((f = fopen(name,"rb")) != null)
              {
                 fread(RAM,0x88a6,1,52,f);
                 fclose(f);
              }

              return 1;
           }
           else
              return 0; /* we can't load the hi scores yet */
        }};

        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
	{
                FILE f;
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char []RAM = Machine.memory_region[0];

                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x88a6,1,52,f);
                        fclose(f);
                }
        }};

        public static GameDriver tutankhm_driver = new GameDriver
        (
                "Tutankham",
                "tutankhm",
                "MIRKO BUFFONI\nDAVID DAHL\nAARON GILES",
                machine_driver,

                tutankhm_rom,
                null, null,   /* ROM decode and opcode decode functions */
                null,      /* Sample names */

                input_ports,null,trak_ports, dsw, keys,

                null, null, null,   /* colors, palette, colortable */
                ORIENTATION_DEFAULT,
                hiload, hisave		        /* High score load and save */
        );

}
