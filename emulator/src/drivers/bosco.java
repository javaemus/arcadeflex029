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
 *
 * ported to v0.27
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
import static vidhrdw.bosco.*;
import static sndhrdw.bosco.*;
import static machine.bosco.*;
import static sndhrdw.pengo.*;
import static mame.memoryH.*;

public class bosco {
        static MemoryReadAddress readmem_cpu1[] =
        {
                new MemoryReadAddress( 0x7800, 0x9fff, bosco_sharedram_r, bosco_sharedram ),
                new MemoryReadAddress( 0x6800, 0x6807, bosco_dsw_r ),
                new MemoryReadAddress( 0x7000, 0x7010, MRA_RAM ),
                new MemoryReadAddress( 0x7100, 0x7100, bosco_customio_r_1 ),
                new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),

                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryReadAddress readmem_cpu2[] =
        {
                new MemoryReadAddress( 0x9000, 0x9010, MRA_RAM ),
                new MemoryReadAddress( 0x9100, 0x9100, bosco_customio_r_2 ),
                new MemoryReadAddress( 0x7800, 0x9fff, bosco_sharedram_r ),
                new MemoryReadAddress( 0x6800, 0x6807, bosco_dsw_r ),
                new MemoryReadAddress( 0x0000, 0x1fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryReadAddress readmem_cpu3[] =
        {
                new MemoryReadAddress( 0x7800, 0x9fff, bosco_sharedram_r ),
                new MemoryReadAddress( 0x6800, 0x6807, bosco_dsw_r ),
                new MemoryReadAddress( 0x0000, 0x1fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem_cpu1[] =
        {
                new MemoryWriteAddress( 0x8000, 0x83ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x8400, 0x87ff, bosco_videoram2_w, bosco_videoram2 ),
                new MemoryWriteAddress( 0x8800, 0x8bff, colorram_w, colorram ),
                new MemoryWriteAddress( 0x8c00, 0x8fff, bosco_colorram2_w, bosco_colorram2 ),

                new MemoryWriteAddress( 0x7800, 0x9fff, bosco_sharedram_w ),

                new MemoryWriteAddress( 0x6800, 0x681f, pengo_sound_w, pengo_soundregs ),
                new MemoryWriteAddress( 0x6830, 0x6830, MWA_NOP ),
                new MemoryWriteAddress( 0x7000, 0x7010, MWA_RAM ),
                new MemoryWriteAddress( 0x7100, 0x7100, bosco_customio_w_1 ),
                new MemoryWriteAddress( 0x6820, 0x6820, bosco_interrupt_enable_1_w ),
                new MemoryWriteAddress( 0x6822, 0x6822, bosco_interrupt_enable_3_w ),
                new MemoryWriteAddress( 0x6823, 0x6823, bosco_halt_w ),
                new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),

                new MemoryWriteAddress( 0x83d4, 0x83df, MWA_RAM, spriteram, spriteram_size ),	/* these are here just to initialize */
                new MemoryWriteAddress( 0x8bd4, 0x8bdf, MWA_RAM, spriteram_2 ),	                /* the pointers. */
                new MemoryWriteAddress( 0x83f4, 0x83ff, MWA_RAM, bosco_radarx ),	                /* ditto */
                new MemoryWriteAddress( 0x8bf4, 0x8bff, MWA_RAM, bosco_radary ),
                new MemoryWriteAddress( 0x9810, 0x9810, MWA_RAM, bosco_scrollx ),
                new MemoryWriteAddress( 0x9820, 0x9820, MWA_RAM, bosco_scrolly ),
                new MemoryWriteAddress( 0x9804, 0x980f, MWA_RAM, bosco_radarattr ),

                new MemoryWriteAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem_cpu2[] =
        {
                new MemoryWriteAddress( 0x8000, 0x83ff, videoram_w ),
                new MemoryWriteAddress( 0x8400, 0x87ff, bosco_videoram2_w ),
                new MemoryWriteAddress( 0x8800, 0x8bff, colorram_w ),
                new MemoryWriteAddress( 0x8c00, 0x8fff, bosco_colorram2_w ),

                new MemoryWriteAddress( 0x9000, 0x9010, MWA_RAM ),
                new MemoryWriteAddress( 0x9100, 0x9100, bosco_customio_w_2 ),
                new MemoryWriteAddress( 0x7800, 0x9fff, bosco_sharedram_w ),
                new MemoryWriteAddress( 0x6821, 0x6821, bosco_interrupt_enable_2_w ),
                new MemoryWriteAddress( 0x0000, 0x1fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem_cpu3[] =
        {
                new MemoryWriteAddress( 0x8000, 0x83ff, videoram_w ),
                new MemoryWriteAddress( 0x8400, 0x87ff, bosco_videoram2_w ),
                new MemoryWriteAddress( 0x8800, 0x8bff, colorram_w ),
                new MemoryWriteAddress( 0x8c00, 0x8fff, bosco_colorram2_w ),

                new MemoryWriteAddress( 0x7800, 0x9fff, bosco_sharedram_w ),
                new MemoryWriteAddress( 0x6800, 0x681f, pengo_sound_w ),
                new MemoryWriteAddress( 0x6822, 0x6822, bosco_interrupt_enable_3_w ),
                new MemoryWriteAddress( 0x0000, 0x1fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static InputPort input_ports[] =
        {
                new InputPort(	/* DSW1 */
                        0xe1,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* DSW2 */
                        0xf7,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_UP, OSD_KEY_RIGHT, OSD_KEY_DOWN, OSD_KEY_LEFT, OSD_KEY_CONTROL, 0, 0, 0 }
                ),
		new InputPort( -1 )	/* end of table */
        };

        static TrakPort[] trak_ports =
       {
           new TrakPort(-1)
       };

        static KEYSet keys[] =
        {
                new KEYSet( 2, 3, "MOVE LEFT"  ),
                new KEYSet( 2, 1, "MOVE RIGHT" ),
                new KEYSet( 2, 0, "MOVE UP"    ),
                new KEYSet( 2, 2, "MOVE DOWN"  ),
                new KEYSet( 2, 4, "FIRE"       ),
                new KEYSet( -1 )
        };


        static DSW bosco_dsw[] =
        {
                new DSW( 0, 0xc0, "LIVES", new String[]{ "2", "4", "3", "5" } ),
                new DSW( 0, 0x38, "BONUS", new String[]{ "NONE", "30K 100K 100K", "20K 70K 70K", "20K 60K", "20K 60K 60K", "30K 120K 120K", "20K 80K 80K", "30K 80K" }, 1 ),
                new DSW( 1, 0x06, "DIFFICULTY", new String[]{ "MEDIUM", "HARD", "HARDEST", "EASY" }, 1 ),
                new DSW( 1, 0x08, "DEMO SOUNDS", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 1, 0x01, "2 CREDITS GAME", new String[]{ "1 PLAYER", "2 PLAYERS" }, 1 ),
                new DSW( 1, 0x40, "CONFIGURATION", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( -1 )
        };


        static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                256,	/* 256 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 4 },      /* the two bitplanes for 4 pixels are packed into one byte */
                new int[]{ 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3 },   /* bits are packed in groups of four */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },   /* characters are rotated 90 degrees */
                16*8	       /* every char takes 16 bytes */
        );

        static GfxLayout spritelayout = new GfxLayout
	(
                16,16,	        /* 16*16 sprites */
                64,	        /* 128 sprites */
                2,	        /* 2 bits per pixel */
                new int[]{ 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
                new int[]{ 8*8, 8*8+1, 8*8+2, 8*8+3, 16*8+0, 16*8+1, 16*8+2, 16*8+3,
                                24*8+0, 24*8+1, 24*8+2, 24*8+3, 0, 1, 2, 3  },
                new int[]{ 0 * 8, 1 * 8, 2 * 8, 3 * 8, 4 * 8, 5 * 8, 6 * 8, 7 * 8,
                                32 * 8, 33 * 8, 34 * 8, 35 * 8, 36 * 8, 37 * 8, 38 * 8, 39 * 8 },
                64*8	/* every sprite takes 64 bytes */
        );



        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, charlayout,       0, 32 ),
                new GfxDecodeInfo( 1, 0x0000, charlayout,    32*4, 32 ),	/* for the radar */
                new GfxDecodeInfo( 1, 0x1000, spritelayout,  0, 32 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        static char color_prom[] =
        {
                /* PROM 6B Palette */
                0xF6, 0x07, 0x1F, 0x3F, 0xC4, 0xDF, 0xF8, 0xD8,
                0x0B, 0x28, 0xC3, 0x51, 0x26, 0x0D, 0xA4, 0x00,
                0xA4, 0x0D, 0x1F, 0x3F, 0xC4, 0xDF, 0xF8, 0xD8,
                0x0B, 0x28, 0xC3, 0x51, 0x26, 0x07, 0xF6, 0x00,

                /* PROM 4M Lookup Table */
                0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x01, 0x00, 0x0E,
                0x0F, 0x02, 0x06, 0x01, 0x0F, 0x05, 0x04, 0x01,
                0x0F, 0x06, 0x07, 0x02, 0x0F, 0x02, 0x07, 0x08,
                0x0F, 0x0C, 0x01, 0x0B, 0x0F, 0x03, 0x09, 0x01,
                0x0F, 0x00, 0x0E, 0x01, 0x0F, 0x00, 0x01, 0x02,
                0x0F, 0x0E, 0x00, 0x0C, 0x0F, 0x07, 0x0E, 0x0D,
                0x0F, 0x0E, 0x03, 0x0D, 0x0F, 0x00, 0x00, 0x07,
                0x0F, 0x0D, 0x00, 0x06, 0x0F, 0x09, 0x0B, 0x04,
                0x0F, 0x09, 0x0B, 0x09, 0x0F, 0x09, 0x0B, 0x0B,
                0x0F, 0x0D, 0x05, 0x0E, 0x0F, 0x09, 0x0B, 0x01,
                0x0F, 0x09, 0x04, 0x01, 0x0F, 0x09, 0x0B, 0x05,
                0x0F, 0x09, 0x0B, 0x0D, 0x0F, 0x09, 0x09, 0x01,
                0x0F, 0x0D, 0x07, 0x0E, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x0F, 0x00, 0x00, 0x00, 0x0E,
                0x00, 0x0D, 0x0F, 0x0D, 0x0F, 0x0F, 0x0F, 0x0D,
                0x0F, 0x0F, 0x0F, 0x0E, 0x0F, 0x0D, 0x0F, 0x07,
                0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0D, 0x0E, 0x00,
                0x0F, 0x0F, 0x0F, 0x0E, 0x0F, 0x0B, 0x09, 0x04,
                0x0F, 0x09, 0x0B, 0x09, 0x0F, 0x09, 0x0B, 0x0B,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0F,
                0x00, 0x00, 0x00, 0x0E, 0x0F, 0x0D, 0x0F, 0x0D,
                0x0F, 0x0F, 0x0F, 0x0D, 0x0F, 0x0F, 0x0F, 0x0E,
                0x0F, 0x0F, 0x0F, 0x07, 0x0F, 0x0F, 0x0F, 0x02,
                0x0F, 0x0F, 0x0F, 0x07, 0x0F, 0x0F, 0x0F, 0x0F,
                0x0F, 0x0F, 0x0E, 0x0E, 0x0F, 0x01, 0x0F, 0x01,
                0x0F, 0x09, 0x03, 0x07, 0x0F, 0x02, 0x07, 0x09,
                0x0F, 0x0C, 0x05, 0x01, 0x0D, 0x0D, 0x0F, 0x0F,
                0x03, 0x03, 0x0F, 0x0F, 0x09, 0x09, 0x0F, 0x0F,
                0x0F, 0x0F, 0x0F, 0x0D, 0x0F, 0x0F, 0x0F, 0x06,
                0x0F, 0x0F, 0x0F, 0x05, 0x0F, 0x03, 0x0F, 0x03,
                0x0F, 0x03, 0x0F, 0x05, 0x0F, 0x0F, 0x0F, 0x03,
                0x0F, 0x0F, 0x03, 0x05, 0x0F, 0x03, 0x0F, 0x0F
        };




        /* waveforms for the audio hardware */
        static char samples[] =
        {
                0xff,0x11,0x22,0x33,0x44,0x55,0x55,0x66,0x66,0x66,0x55,0x55,0x44,0x33,0x22,0x11,
                0xff,0xdd,0xcc,0xbb,0xaa,0x99,0x99,0x88,0x88,0x88,0x99,0x99,0xaa,0xbb,0xcc,0xdd,

                0xff,0x11,0x22,0x33,0xff,0x55,0x55,0xff,0x66,0xff,0x55,0x55,0xff,0x33,0x22,0x11,
                0xff,0xdd,0xff,0xbb,0xff,0x99,0xff,0x88,0xff,0x88,0xff,0x99,0xff,0xbb,0xff,0xdd,

                0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,
                0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,

                0x33,0x55,0x66,0x55,0x44,0x22,0x00,0x00,0x00,0x22,0x44,0x55,0x66,0x55,0x33,0x00,
                0xcc,0xaa,0x99,0xaa,0xbb,0xdd,0xff,0xff,0xff,0xdd,0xbb,0xaa,0x99,0xaa,0xcc,0xff,

                0xff,0x22,0x44,0x55,0x66,0x55,0x44,0x22,0xff,0xcc,0xaa,0x99,0x88,0x99,0xaa,0xcc,
                0xff,0x33,0x55,0x66,0x55,0x33,0xff,0xbb,0x99,0x88,0x99,0xbb,0xff,0x66,0xff,0x88,

                0xff,0x66,0x44,0x11,0x44,0x66,0x22,0xff,0x44,0x77,0x55,0x00,0x22,0x33,0xff,0xaa,
                0x00,0x55,0x11,0xcc,0xdd,0xff,0xaa,0x88,0xbb,0x00,0xdd,0x99,0xbb,0xee,0xbb,0x99,

                0xff,0x00,0x22,0x44,0x66,0x55,0x44,0x44,0x33,0x22,0x00,0xff,0xdd,0xee,0xff,0x00,
                0x00,0x11,0x22,0x33,0x11,0x00,0xee,0xdd,0xcc,0xcc,0xbb,0xaa,0xcc,0xee,0x00,0x11,

                0x22,0x44,0x44,0x22,0xff,0xff,0x00,0x33,0x55,0x66,0x55,0x22,0xee,0xdd,0xdd,0xff,
                0x11,0x11,0x00,0xcc,0x99,0x88,0x99,0xbb,0xee,0xff,0xff,0xcc,0xaa,0xaa,0xcc,0xff,
        };



        public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                3125000,	/* 3.125 Mhz */
                                0,
                                readmem_cpu1,writemem_cpu1,null,null,
                                bosco_interrupt_1,1
                        ),
                        new MachineCPU(
                                CPU_Z80,
                                3125000,	/* 3.125 Mhz */
                                2,	/* memory region #2 */
                                readmem_cpu2,writemem_cpu2,null,null,
                                bosco_interrupt_2,1
                        ),
                        new MachineCPU(
                                CPU_Z80,
                                3125000,	/* 3.125 Mhz */
                                3,	/* memory region #3 */
                                readmem_cpu3,writemem_cpu3,null,null,
                                bosco_interrupt_3,2
                        )
                },
                60,
                bosco_init_machine,

                /* video hardware */
                36*8, 28*8, new rectangle( 0*8, 36*8-1, 0*8, 28*8-1 ),
                gfxdecodeinfo,
                32+64,64*4,	/* 32 for the characters, 64 for the stars */
                bosco_vh_convert_color_prom,
                VIDEO_TYPE_RASTER,
                null,
                bosco_vh_start,
                bosco_vh_stop,
                bosco_vh_screenrefresh,

                /* sound hardware */
                samples,
                null,
                bosco_sh_start,
                null,
                pengo_sh_update
        );

        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr bosco_rom= new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);	/* 64k for code for the first CPU  */
                ROM_LOAD( "bos3_1.bin",   0x0000, 0x1000, 0x96021267 );
                ROM_LOAD( "bos1_2.bin",   0x1000, 0x1000, 0x2d8f3ebe );
                ROM_LOAD( "bos1_3.bin",   0x2000, 0x1000, 0xc80ccfa5 );
                ROM_LOAD( "bos1_4b.bin",  0x3000, 0x1000, 0xa3f7f4ab );

                ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "5300.5d",      0x0000, 0x1000, 0xa956d3c5 );
                ROM_LOAD( "5200.5e",      0x1000, 0x1000, 0xe869219c );

                ROM_REGION(0x10000);	/* 64k for the second CPU */
                ROM_LOAD( "bos1_5c.bin",  0x0000, 0x1000, 0xa7c8e432 );
                ROM_LOAD( "bos3_6.bin",   0x1000, 0x1000, 0x4543cf82 );

                ROM_REGION(0x10000);	/* 64k for the third CPU  */
               ROM_LOAD( "2900.3e",      0x0000, 0x1000, 0xd45a4911 );

                ROM_REGION(0x3000);	/* ROMs for digitised speech */
                ROM_LOAD( "4900.5n",      0x0000, 0x1000, 0x09acc978 );
                ROM_LOAD( "5000.5m",      0x1000, 0x1000, 0xe571e959 );
                ROM_LOAD( "5100.5l",      0x2000, 0x1000, 0x17ac9511 );
                ROM_END();
        }};

        static  String bosco_sample_names[] =
        {
                "MIDBANG.SAM",
                "BIGBANG.SAM",
                "SHOT.SAM",
                null	/* end of array */
        };

        static HiscoreLoadPtr hiload = new HiscoreLoadPtr()
       {
            public int handler(String name)
            {
                FILE f;
                int		i;

                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];

                /* check if the hi score table has already been initialized */
                if (memcmp(RAM,0x8bd3,new char[] {0x18},1) == 0)
                {
                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x8BC5, 15, 1, f);
                                fread(RAM,0x8BE4, 16, 1, f);
                                fread(RAM,0x885C, 4, 1, f);
                                fread(RAM,0x8060, 8, 1, f);
                                fclose(f);
                        }
                        HiScore = 0;
                        for (i = 0; i < 3; i++)
                        {
                                HiScore = HiScore * 10 + RAM[0x8065 + i];
                        }
                        for (i = 0; i < 4; i++)
                        {
                                HiScore = HiScore * 10 + RAM[0x8060 + i];
                        }
                        if (HiScore == 0)
                                HiScore = 20000;
                        bosco_hiscoreloaded = 1;
                        return 1;
                }
                return 0; /* we can't load the hi scores yet */
        }};


      static HiscoreSavePtr hisave = new HiscoreSavePtr()
      {
        public void handler(String name)
        {
                FILE f;

                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];

                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x8BC5, 15, 1, f);
                        fwrite(RAM,0x8BE4, 16, 1, f);
                        fwrite(RAM,0x885C, 4, 1, f);
                        fwrite(RAM,0x8060, 8, 1, f);
                        fclose(f);
                }
        }};

        public static GameDriver bosco_driver = new GameDriver
	(
                "Bosconian",
                "bosco",
                "MARTIN SCRAGG\nAARON GILES\nNICOLA SALMORIA\nMIRKO BUFFONI",
                machine_driver,

                bosco_rom,
                null, null,
                bosco_sample_names,

                input_ports,null, trak_ports, bosco_dsw, keys,

                color_prom, null, null,

               ORIENTATION_DEFAULT,

                hiload, hisave
        );   
}
