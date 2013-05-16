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
 *        
 *
 */

package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.generic.*;
import static vidhrdw.generic.*;
import static vidhrdw.starforc.*;
import static sndhrdw.starforc.*;
import static mame.inptport.*;
import static mame.memoryH.*;
public class starforc {
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x8000, 0x97ff, MRA_RAM ),
                new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
                new MemoryReadAddress( 0xa000, 0xa1ff, MRA_RAM ),
                new MemoryReadAddress( 0xa800, 0xa9ff, MRA_RAM ),
                new MemoryReadAddress( 0xb000, 0xb1ff, MRA_RAM ),
                new MemoryReadAddress( 0xd000, 0xd000, input_port_0_r ),	/* player 1 input */
                new MemoryReadAddress( 0xd001, 0xd001, input_port_0_r ),	/* player 2 input (use player 1's) */
                new MemoryReadAddress( 0xd002, 0xd002, input_port_1_r ),	/* coin */
                new MemoryReadAddress( 0xd004, 0xd004, input_port_2_r ),	/* DSW1 */
                new MemoryReadAddress( 0xd005, 0xd005, input_port_3_r ),	/* DSW2 */
                new MemoryReadAddress( -1 )  /* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x8000, 0x8fff, MWA_RAM ),
                new MemoryWriteAddress( 0x9000, 0x93ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x9400, 0x97ff, colorram_w, colorram ),
                new MemoryWriteAddress( 0xa000, 0xa1ff, starforc_tiles2_w, starforc_tilebackground2, starforc_bgvideoram_size ),
                new MemoryWriteAddress( 0xa800, 0xa9ff, starforc_tiles3_w, starforc_tilebackground3 ),
                new MemoryWriteAddress( 0xb000, 0xb1ff, starforc_tiles4_w, starforc_tilebackground4 ),
                new MemoryWriteAddress( 0xd004, 0xd004, sound_command_w ),
                new MemoryWriteAddress( 0x9e20, 0x9e21, MWA_RAM, starforc_scrollx2 ),

                new MemoryWriteAddress( 0x9e30, 0x9e31, MWA_RAM, starforc_scrollx3 ),
                new MemoryWriteAddress( 0x9e25, 0x9e25, MWA_RAM, starforc_scrolly2 ),
                new MemoryWriteAddress( 0x9e35, 0x9e35, MWA_RAM, starforc_scrolly3 ),
                new MemoryWriteAddress( 0x9800, 0x987f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x9c00, 0x9d7f, starforc_paletteram_w, starforc_paletteram ),
                new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )  /* end of table */
        };


        static MemoryReadAddress sound_readmem[] =
        {
                new MemoryReadAddress( 0x4000, 0x43ff, MRA_RAM ),
                new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };

        static MemoryWriteAddress sound_writemem[] =
        {
                new MemoryWriteAddress( 0x4000, 0x43ff, MWA_RAM ),
                new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
                new MemoryWriteAddress( 0x8000, 0x8000, starforce_sh_0_w ),
                new MemoryWriteAddress( 0x9000, 0x9000, starforce_sh_1_w ),
                new MemoryWriteAddress( 0xa000, 0xa000, starforce_sh_2_w ),
                new MemoryWriteAddress( 0xd000, 0xd000, starforce_volume_w ),
                new MemoryWriteAddress( -1 )  /* end of table */
        };

        static IOReadPort sound_readport[] =
        {
                new IOReadPort( 0x00, 0x03, starforce_pio_r ),
                new IOReadPort( 0x08, 0x0b, starforce_ctc_r ),
                new IOReadPort( -1 )	/* end of table */
        };

        static IOWritePort sound_writeport[] =
        {
                new IOWritePort( 0x00, 0x03, starforce_pio_w ),
                new IOWritePort( 0x08, 0x0b, starforce_ctc_w ),
                new IOWritePort( -1 )	/* end of table */
        };

        static InputPort input_ports[] =
        {
                new InputPort(	/* IN0 / IN1 */
                        0x00,
                        new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_UP, OSD_KEY_DOWN,
                                        OSD_KEY_CONTROL, 0, 0, 0 }
                ),
		new InputPort(	/* IN2 */
                        0x00,
                        new int[]{ 0, OSD_KEY_3, OSD_KEY_1, OSD_KEY_2, 0, 0, 0, 0 }
                ),
		new InputPort(	/* DSW1 */
                        0x40,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* DSW2 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, OSD_KEY_F1,OSD_KEY_F2 }
                ),
		new InputPort( -1 )  /* end of table */
        };

       static TrakPort[] trak_ports =
       {
           new TrakPort(-1)
       };


        static KEYSet keys[] =
        {
                new KEYSet( 0, 2, "MOVE UP" ),
                new KEYSet( 0, 1, "MOVE LEFT"  ),
                new KEYSet( 0, 0, "MOVE RIGHT" ),
                new KEYSet( 0, 3, "MOVE DOWN" ),
                new KEYSet( 0, 4, "FIRE" ),
                new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                new DSW( 2, 0x30, "LIVES", new String[]{ "3", "4", "5", "2" } ),
                new DSW( 3, 0x07, "BONUS", new String[]{ "50K 200K 500K", "100K 300K 800K", "50K 200K", "100K 300K", "50K", "100K", "200K", "NONE" } ),
                new DSW( 3, 0x38, "DIFFICULTY", new String[]{ "DEFAULT", "DIFFICULT 1", "DIFFICULT 2", "DIFFICULT 3", "DIFFICULT 4", "DIFFICULT 5", "UNUSED", "UNUSED" } ),
                new DSW( 2, 0x80, "DEMO SOUNDS", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( -1 )
        };



        static GfxLayout charlayout1 = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                512,	/* 512 characters */
                3,	/* 3 bits per pixel */
                new int[]{ 0, 512*8*8, 2*512*8*8 },	/* the bitplanes are separated */
                new int[]{ 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },	/* pretty straightforward layout */
                8*8	/* every char takes 8 consecutive bytes */
        );
        static GfxLayout charlayout2 = new GfxLayout
	(
                16,16,	/* 16*16 characters */
                256,	/* 256 characters */
                3,	/* 3 bits per pixel */
                new int[]{ 2*256*16*16, 256*16*16, 0 },	/* the bitplanes are separated */
                new int[]{ 23*8, 22*8, 21*8, 20*8, 19*8, 18*8, 17*8, 16*8,
                                7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7,	/* pretty straightforward layout */
                                8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
                32*8	/* every character takes 32 consecutive bytes */
        );
        static GfxLayout charlayout3 = new GfxLayout
	(
                16,16,	/* 16*16 characters */
                128,	/* 128 characters */
                3,	/* 3 bits per pixel */
                new int[]{ 2*128*16*16, 128*16*16, 0 },	/* the bitplanes are separated */
                new int[]{ 23*8, 22*8, 21*8, 20*8, 19*8, 18*8, 17*8, 16*8,
                                7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7,	/* pretty straightforward layout */
                                8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
                32*8	/* every character takes 32 consecutive bytes */
        );
        static GfxLayout spritelayout1 = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                256,	/* 256 sprites */
                3,	/* 3 bits per pixel */
                new int[]{ 2*512*16*16, 512*16*16, 0 },	/* the bitplanes are separated */
                new int[]{ 23*8, 22*8, 21*8, 20*8, 19*8, 18*8, 17*8, 16*8,
                                7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7,	/* pretty straightforward layout */
                                8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
                32*8	/* every sprite takes 32 consecutive bytes */
        );

        static GfxLayout spritelayout2 = new GfxLayout
	(
                32,32,	/* 32*32 sprites */
                64,	/* 64 sprites */
                3,	/* 3 bits per pixel */
                new int[]{ 2*128*32*32, 128*32*32, 0 },	/* the bitplanes are separated */
                new int[]{ 87*8, 86*8, 85*8, 84*8, 83*8, 82*8, 81*8, 80*8,
                                71*8, 70*8, 69*8, 68*8, 67*8, 66*8, 65*8, 64*8,
                                23*8, 22*8, 21*8, 20*8, 19*8, 18*8, 17*8, 16*8,
                                7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7,	/* pretty straightforward layout */
                                8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7,
                                32*8+0, 32*8+1, 32*8+2, 32*8+3, 32*8+4, 32*8+5, 32*8+6, 32*8+7,
                                40*8+0, 40*8+1, 40*8+2, 40*8+3, 40*8+4, 40*8+5, 40*8+6, 40*8+7 },
                128*8	/* every sprite takes 128 consecutive bytes */
        );



        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x00000, charlayout1,      0, 8 ),	/* characters */
                new GfxDecodeInfo( 1, 0x03000, charlayout2,   16*8, 8 ),	/* background #1 */
                new GfxDecodeInfo( 1, 0x09000, charlayout2,    8*8, 8 ),	/* background #2 */
                new GfxDecodeInfo( 1, 0x0f000, charlayout3,   24*8, 8 ),	/* star background */
                new GfxDecodeInfo( 1, 0x12000, spritelayout1, 40*8, 8 ),	/* normal sprites */
                new GfxDecodeInfo( 1, 0x14000, spritelayout2, 40*8, 8 ),	/* large sprites */
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        static MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                4000000,	/* 4 Mhz */
                                0,
                                readmem,writemem,null,null,
                                interrupt,1
                        ),
			new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2000000,        /* 2 Mhz */
                                2,
                                sound_readmem,sound_writemem,
                                sound_readport,sound_writeport,
                                starforce_sh_interrupt,10
                        )
                },
                60,
                null,

                /* video hardware */
                32*8, 32*8, new rectangle( 2*8, 30*8-1, 0, 32*8-1 ),
                gfxdecodeinfo,
                256, 48*8,
                starforc_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
                null,
                starforc_vh_start,
                starforc_vh_stop,
                starforc_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                starforce_sh_start,
                starforce_sh_stop,
                starforce_sh_update
        );



        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr starforc_rom= new RomLoadPtr(){ public void handler()  
        {
            
               //rom loading is taken from v0.30
                ROM_REGION(0x10000);    /* 64k for code */
                ROM_LOAD( "starforc.3", 0x0000, 0x4000, 0x782481e8 );
                ROM_LOAD( "starforc.2", 0x4000, 0x4000, 0x63eb0cc1 );

                ROM_REGION(0x1e000);     /* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "starforc.7",  0x00000, 0x1000, 0xc2a35075 );
                ROM_LOAD( "starforc.8",  0x01000, 0x1000, 0xc10380f7 );
                ROM_LOAD( "starforc.9",  0x02000, 0x1000, 0xf907fc49 );
                ROM_LOAD( "starforc.10", 0x03000, 0x2000, 0x8ff8c55c );
                ROM_LOAD( "starforc.11", 0x05000, 0x2000, 0x647d74eb );
                ROM_LOAD( "starforc.12", 0x07000, 0x2000, 0x451c5ffc );
                ROM_LOAD( "starforc.13", 0x09000, 0x2000, 0x0be64664 );
                ROM_LOAD( "starforc.14", 0x0b000, 0x2000, 0xff8c2118 );
                ROM_LOAD( "starforc.15", 0x0d000, 0x2000, 0x44f6e3f8 );
                ROM_LOAD( "starforc.16", 0x0f000, 0x1000, 0x111fb9ed );
                ROM_LOAD( "starforc.17", 0x10000, 0x1000, 0xb62c8e7a );
                ROM_LOAD( "starforc.18", 0x11000, 0x1000, 0x4185c335 );
                ROM_LOAD( "starforc.4",  0x12000, 0x4000, 0xbe304630 );
                ROM_LOAD( "starforc.5",  0x16000, 0x4000, 0x178f15e9 );
                ROM_LOAD( "starforc.6",  0x1a000, 0x4000, 0x1cd03e28 );

                ROM_REGION(0x10000);     /* 64k for sound board */
                ROM_LOAD( "starforc.1", 0x0000, 0x2000, 0xfb4a6b5a );
                ROM_END();
        }};



        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
	{
            //this doesn't seem to work anyway so we leave it for later...
            
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
      /*          unsigned char *RAM = Machine->memory_region[0];


                /* check if the hi score table has already been initialized */
     /*           if (memcmp(&RAM[0x803a],"\x00\x05\x08\x00",4) == 0 &&
                                memcmp(&RAM[0x809d],"\x00\x01\x80\x00",4) == 0)
                {
                        FILE *f;


                        if ((f = fopen(name,"rb")) != 0)
                        {
                                fread(&RAM[0x803a],1,11*10,f);
                                RAM[0x8348] = RAM[0x803d];
                                RAM[0x8349] = RAM[0x803c];
                                RAM[0x834a] = RAM[0x803b];
                                RAM[0x834b] = RAM[0x803a];
                                fclose(f);
                        }

                        return 1;
                }
                else*/ return 0;	/* we can't load the hi scores yet */
        }};



        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
	{
           /*     FILE *f;
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
          /*      unsigned char *RAM = Machine->memory_region[0];


                if ((f = fopen(name,"wb")) != 0)
                {
                        fwrite(&RAM[0x803a],1,11*10,f);
                        fclose(f);
                }*/
        }};


        public static GameDriver starforc_driver = new GameDriver
        (
                "Star Force",
                "starforc",
                "MIRKO BUFFONI\nNICOLA SALMORIA\nTATSUYUKI SATOH",
                machine_driver,

                starforc_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                null, null, null,
                ORIENTATION_DEFAULT,

                /* TODO: high score load doesn't work */
                hiload, hisave
        );  
}
