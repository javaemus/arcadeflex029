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
 * NOTES: romsets are from v0.36 roms
 * digdugnm is digdug in v0.36 romset
 * graphics rom loading order is based on v0.27
 */

package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static sndhrdw.pengo.*;
import static vidhrdw.generic.*;
import static vidhrdw.digdug.*;
import static machine.digdug.*;


public class digdug
{
        static  MemoryReadAddress readmem_cpu1[] =
        {
                new MemoryReadAddress( 0x8000, 0x9fff, digdug_sharedram_r, digdug_sharedram ),
                new MemoryReadAddress( 0x7100, 0x7100, digdug_customio_r ),
                new MemoryReadAddress( 0x7000, 0x700f, MRA_RAM ),
                new MemoryReadAddress( 0x0000, 0x0000, digdug_reset_r ),
                new MemoryReadAddress( 0x0001, 0x3fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static  MemoryReadAddress readmem_cpu2[] =
        {
                new MemoryReadAddress( 0x8000, 0x9fff, digdug_sharedram_r ),
                new MemoryReadAddress( 0x0000, 0x1fff, MRA_ROM ),
               new MemoryReadAddress( -1 )	/* end of table */
        };

        static  MemoryReadAddress readmem_cpu3[] =
        {
                new MemoryReadAddress( 0x8000, 0x9fff, digdug_sharedram_r ),
                new MemoryReadAddress( 0x0000, 0x0fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static  MemoryWriteAddress writemem_cpu1[] =
        {
                new MemoryWriteAddress( 0x8000, 0x9fff, digdug_sharedram_w ),
                new MemoryWriteAddress( 0x6830, 0x6830, MWA_NOP ),
                new MemoryWriteAddress( 0x7100, 0x7100, digdug_customio_w ),
                new MemoryWriteAddress( 0x7000, 0x700f, MWA_RAM ),
                new MemoryWriteAddress( 0x6820, 0x6820, digdug_interrupt_enable_1_w ),
                new MemoryWriteAddress( 0x6822, 0x6822, digdug_interrupt_enable_3_w ),
                new MemoryWriteAddress( 0x6823, 0x6823, digdug_halt_w ),
                new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
                new MemoryWriteAddress( 0x8b80, 0x8bff, MWA_RAM, spriteram, spriteram_size ), /* these three are here just to initialize */
                new MemoryWriteAddress( 0x9380, 0x93ff, MWA_RAM, spriteram_2 ),	          /* the pointers. The actual writes are */
                new MemoryWriteAddress( 0x9b80, 0x9bff, MWA_RAM, spriteram_3 ),                /* handled by digdug_sharedram_w() */
                new MemoryWriteAddress( 0x8000, 0x83ff, MWA_RAM, videoram, videoram_size ),   /* dirtybuffer[] handling is not needed because */
                new MemoryWriteAddress( 0x8400, 0x87ff, MWA_RAM ),	                          /* characters are redrawn every frame */
                new MemoryWriteAddress( 0xa000, 0xa00f, digdug_vh_latch_w, digdug_vlatches ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem_cpu2[] =
        {
                new MemoryWriteAddress( 0x8000, 0x9fff, digdug_sharedram_w ),
                new MemoryWriteAddress( 0x6821, 0x6821, digdug_interrupt_enable_2_w ),
                new MemoryWriteAddress( 0x6830, 0x6830, MWA_NOP ),
                new MemoryWriteAddress( 0x0000, 0x1fff, MWA_ROM ),
                new MemoryWriteAddress( 0xa000, 0xa00f, digdug_vh_latch_w ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };

        static  MemoryWriteAddress writemem_cpu3[] =
        {
                new MemoryWriteAddress( 0x8000, 0x9fff, digdug_sharedram_w ),
                new MemoryWriteAddress( 0x6800, 0x681f, pengo_sound_w, pengo_soundregs ),
                new MemoryWriteAddress( 0x6822, 0x6822, digdug_interrupt_enable_3_w ),
                new MemoryWriteAddress( 0x0000, 0x0fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static  InputPort input_ports[] =
        {
               new InputPort(	/* DSW1 */
                        0x98,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                        
                ),
		new InputPort(	/* DSW2 */
                        0x24,
                       new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
                        
                ),
		new InputPort(	/* IN0 */
                        0xff,
                       new int[] { OSD_KEY_UP, OSD_KEY_RIGHT, OSD_KEY_DOWN, OSD_KEY_LEFT, 0, OSD_KEY_CONTROL, 0, 0 }
                        
                ),
		new InputPort( -1 )	/* end of table */
        };

        static TrakPort[] trak_ports =
        {
           new TrakPort(-1)
        };

        static  KEYSet keys[] =
        {
                new KEYSet( 2, 3, "MOVE LEFT"  ),
                new KEYSet( 2, 1, "MOVE RIGHT" ),
                new KEYSet( 2, 0, "MOVE UP"    ),
                new KEYSet( 2, 2, "MOVE DOWN"  ),
                new KEYSet( 2, 5, "PUMP"       ),
                new KEYSet( -1 )
        };


        static DSW digdug_dsw[] =
        {
                new DSW( 0, 0xc0, "LIVES", new String[]{ "1", "2", "3", "5" } ),
                new DSW( 0, 0x38, "BONUS", new String[]{ "NONE", "20K 70K 70K", "10K 50K 50K", "20K 60K", "10K 40K 40K", "10K 40K", "20K 60K 60K", "10K" }, 1 ),
                new DSW( 1, 0x03, "RANK", new String[]{ "A", "C", "B", "D" } ),
                new DSW( 1, 0x10, "DEMO SOUND", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 1, 0x08, "CONTINUATION",new String[] { "ON", "OFF" }, 1 ),
                new DSW( -1 ),
        };


        public static GfxLayout charlayout1 = new GfxLayout
        (
                8,8,	/* 8*8 characters */
                128,	/* 128 characters */
                1,		/* 1 bit per pixel */
                new int[]{ 0 },	/* one bitplane */
                new int[]{ 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },
                8*8	/* every char takes 8 consecutive bytes */
        );

        public static GfxLayout charlayout2 = new GfxLayout
        (
                8,8,	/* 8*8 characters */
                256,	/* 256 characters */
                2,	/* 2 bits per pixel */
                 new int[]{ 0, 4 },      /* the two bitplanes for 4 pixels are packed into one byte */
                 new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },   /* characters are rotated 90 degrees */
                 new int[]{ 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3 },   /* bits are packed in groups of four */
                16*8	       /* every char takes 16 bytes */
        );

        public static GfxLayout spritelayout = new GfxLayout
        (
                16,16,	        /* 16*16 sprites */
                256,	        /* 256 sprites */
                2,	        /* 2 bits per pixel */
                new int[]{ 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
                new int[]{ 39 * 8, 38 * 8, 37 * 8, 36 * 8, 35 * 8, 34 * 8, 33 * 8, 32 * 8,
                                7 * 8, 6 * 8, 5 * 8, 4 * 8, 3 * 8, 2 * 8, 1 * 8, 0 * 8 },
                new int[]{ 0, 1, 2, 3, 8*8, 8*8+1, 8*8+2, 8*8+3, 16*8+0, 16*8+1, 16*8+2, 16*8+3,
                                24*8+0, 24*8+1, 24*8+2, 24*8+3 },
                64*8	/* every sprite takes 64 bytes */
        );


        public static GfxDecodeInfo gfxdecodeinfo[] =
	{
                new GfxDecodeInfo( 1, 0x0000, charlayout1,            0,  8 ),
                new GfxDecodeInfo( 1, 0x2000, spritelayout,         8*2, 64 ),
                new GfxDecodeInfo( 1, 0x1000, charlayout2,   64*4 + 8*2, 64 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };


        static char color_prom[] =
        {
                /* 5N - palette */
                0x00,0x2f,0xf6,0x1e,0x28,0x0d,0x36,0x04,0x80,0x5b,0x07,0xa4,0x52,0x5a,0x65,0x00,
                0x00,0x07,0x2f,0x28,0xe8,0xf6,0x36,0x1f,0x65,0x14,0x0a,0xdf,0xd8,0xd0,0x84,0x00,
                /* 1C - sprites */
                0x0f,0x01,0x05,0x0c,0x0f,0x01,0x06,0x05,0x0f,0x01,0x03,0x05,0x0f,0x00,0x06,0x05,
                0x0f,0x08,0x09,0x0a,0x0f,0x01,0x06,0x07,0x0f,0x00,0x00,0x00,0x0f,0x01,0x06,0x04,
                0x0f,0x01,0x00,0x05,0x0f,0x01,0x0f,0x05,0x0f,0x00,0x04,0x00,0x0f,0x06,0x07,0x0b,
                0x0f,0x05,0x03,0x05,0x0f,0x01,0x03,0x08,0x0f,0x01,0x03,0x08,0x0f,0x00,0x03,0x05,
                0x0f,0x05,0x03,0x08,0x0f,0x0e,0x0b,0x0d,0x0f,0x05,0x08,0x01,0x0f,0x01,0x05,0x03,
                0x0f,0x09,0x07,0x02,0x0f,0x06,0x01,0x0d,0x0f,0x06,0x03,0x09,0x0f,0x06,0x03,0x0b,
                0x0f,0x06,0x03,0x01,0x0f,0x07,0x03,0x05,0x0f,0x0d,0x05,0x01,0x0f,0x0d,0x05,0x03,
                0x0f,0x04,0x03,0x07,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                /* 2N - playfield */
                0x00,0x06,0x08,0x01,0x00,0x02,0x08,0x0a,0x06,0x01,0x01,0x03,0x01,0x03,0x03,0x05,
                0x03,0x05,0x05,0x07,0x02,0x06,0x08,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x09,0x08,0x0b,0x00,0x02,0x08,0x0a,0x09,0x0b,0x0c,0x0e,0x0c,0x0e,0x09,0x01,
                0x09,0x01,0x07,0x03,0x02,0x06,0x08,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x09,0x08,0x0b,0x00,0x02,0x08,0x0a,0x09,0x0b,0x0c,0x09,0x0c,0x09,0x00,0x0d,
                0x00,0x0d,0x09,0x0c,0x02,0x06,0x08,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x09,0x08,0x0e,0x00,0x02,0x08,0x0a,0x09,0x0e,0x05,0x0e,0x05,0x0e,0x0c,0x0e,
                0x0c,0x0e,0x07,0x0e,0x02,0x06,0x08,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00
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
                                digdug_interrupt_1,1
                        ),
			new MachineCPU(
                                CPU_Z80,
                                3125000,	/* 3.125 Mhz */
                                2,	/* memory region #2 */
                                readmem_cpu2,writemem_cpu2,null,null,
                                digdug_interrupt_2,1
                        ),
			new MachineCPU(
                                CPU_Z80,
                                3125000,	/* 3.125 Mhz */
                                3,	/* memory region #3 */
                                readmem_cpu3,writemem_cpu3,null,null,
                                digdug_interrupt_3,2
                        )
                },
                60,
                digdig_init_machine,

                /* video hardware */
                28*8, 36*8, new rectangle( 0*8, 28*8-1, 0*8, 36*8-1 ),
                gfxdecodeinfo,
                32,8*2+64*4+64*4,
                digdug_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
                null,
                digdug_vh_start,
                digdug_vh_stop,
                digdug_vh_screenrefresh,

                /* sound hardware */
                samples,
                null,
                rallyx_sh_start,
                null,
                pengo_sh_update
        );




        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr digdug_rom= new RomLoadPtr(){ public void handler()  
        {
                 ROM_REGION(0x10000);	/* 64k for code for the first CPU  */       
                 ROM_LOAD( "136007.101",   0x0000, 0x1000, 0xb9198079 );
                 ROM_LOAD( "136007.102",   0x1000, 0x1000, 0xb2acbe49 );
                 ROM_LOAD( "136007.103",   0x2000, 0x1000, 0xd6407b49 );
                 ROM_LOAD("dd1.4b",       0x3000, 0x1000, 0xf4cebc16 );

                 ROM_REGION(0x8000);	/* temporary space for graphics (disposed after conversion) */                      
                 ROM_LOAD( "dd1.9",        0x0000, 0x0800, 0xf14a6fe1 );
                 ROM_LOAD( "dd1.11",       0x1000, 0x1000, 0x7b383983 );
                 ROM_LOAD( "136007.116",   0x2000, 0x1000, 0xe22957c8 );
                 ROM_LOAD( "dd1.14",       0x3000, 0x1000, 0x2829ec99 );
                 ROM_LOAD( "136007.118",   0x4000, 0x1000, 0x458499e9 );
                 ROM_LOAD( "136007.119",   0x5000, 0x1000, 0xc58252a0 );
                
                 ROM_REGION(0x10000);	/* 64k for the second CPU */
                 ROM_LOAD("dd1.5b",       0x0000, 0x1000, 0x370ef9b4 );
                 ROM_LOAD( "dd1.6b",       0x1000, 0x1000, 0x361eeb71 );

                 ROM_REGION(0x10000);	/* 64k for the third CPU  */
                 ROM_LOAD( "136007.107",   0x0000, 0x1000, 0xa41bce72 );

                 ROM_REGION(0x01000);	/* 4k for the playfield graphics */
                 ROM_LOAD("dd1.10b",      0x0000, 0x1000, 0x2cf399c2 );
                ROM_END();
        }};

        static RomLoadPtr digdugat_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code for the first CPU  */
                ROM_LOAD( "136007.101",   0x0000, 0x1000, 0xb9198079 );
                ROM_LOAD( "136007.102",   0x1000, 0x1000, 0xb2acbe49 );
                ROM_LOAD( "136007.103",   0x2000, 0x1000, 0xd6407b49 );
                ROM_LOAD( "136007.104",   0x3000, 0x1000, 0xb3ad42c3 );

                ROM_REGION(0x8000);	/* temporary space for graphics (disposed after conversion) */         
                ROM_LOAD( "136007.108",   0x0000, 0x0800, 0x3d24a3af );    
                ROM_LOAD( "136007.115",   0x1000, 0x1000, 0x754539be );
                ROM_LOAD( "136007.116",   0x2000, 0x1000, 0xe22957c8 );
                ROM_LOAD( "136007.117",   0x3000, 0x1000, 0xa3bbfd85 );
                ROM_LOAD( "136007.118",   0x4000, 0x1000, 0x458499e9 );
                ROM_LOAD( "136007.119",   0x5000, 0x1000, 0xc58252a0 );     
                

                ROM_REGION(0x10000);	/* 64k for the second CPU */
                ROM_LOAD( "136007.105",   0x0000, 0x1000, 0x0a2aef4a );
                ROM_LOAD( "136007.106",   0x1000, 0x1000, 0xa2876d6e );

                ROM_REGION(0x10000);	/* 64k for the third CPU  */
                ROM_LOAD( "136007.107",   0x0000, 0x1000, 0xa41bce72 );

                ROM_REGION(0x01000);	/* 4k for the playfield graphics */
                ROM_LOAD( "136007.114",   0x0000, 0x1000, 0xd6822397 );
                ROM_END();
        }};
            static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name)
            {
                    /* get RAM pointer (this game is multiCPU, we can't assume the global */
                    /* RAM pointer is pointing to the right place) */
                    char []RAM = Machine.memory_region[0];


                        /* check if the hi score table has already been initialized (works for Namco & Atari) */
                       if (RAM[0x89b1] == 0x35 && RAM[0x89b4] == 0x35)
                       {
                          FILE f;
                           
                          if ((f = fopen(name, "rb")) != null)
                          {
                             fread(RAM,0x89a0,1,37,f);
                             fclose(f);
                             digdug_hiscoreloaded = 1;
                          }

                          return 1;
                       }
                       else
                          return 0; /* we can't load the hi scores yet */
            }};

            static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name)
            {
                    FILE f;
                    /* get RAM pointer (this game is multiCPU, we can't assume the global */
                    /* RAM pointer is pointing to the right place) */
                    char []RAM = Machine.memory_region[0];


                    if ((f = fopen(name, "wb")) != null)
                    {
                            fwrite(RAM,0x89a0,1,37,f);
                            fclose(f);
                    }
            }};

 

        public static GameDriver digdug_driver = new GameDriver
        (
                "Dig Dug (Namco)",
                "digdug",
                "AARON GILES\nMARTIN SCRAGG\nNICOLA SALMORIA\nMIRKO BUFFONI\nALAN J MCCORMICK",
                machine_driver,

                digdug_rom,
                null,null,
                null,

                input_ports,null, trak_ports, digdug_dsw, keys,

                color_prom, null,null,

               ORIENTATION_DEFAULT,

                hiload, hisave
        );


       public static GameDriver digdugat_driver = new GameDriver
        (
                "Dig Dug (Atari)",
                "digdugat",
                "AARON GILES\nMARTIN SCRAGG\nNICOLA SALMORIA\nMIRKO BUFFONI\nALAN J MCCORMICK",
                machine_driver,

                digdugat_rom,
                null,null,
                null,

                input_ports,null, trak_ports, digdug_dsw, keys,

                color_prom, null,null,

                ORIENTATION_DEFAULT,

                hiload, hisave
        );

}