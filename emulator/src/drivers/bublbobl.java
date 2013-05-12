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
 * using v0.36 romsets but v0.27 loading 
 */
package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static sndhrdw.generic.*;
import static vidhrdw.bublbobl.*;
import static machine.bublbobl.*;
import static mame.inptport.*;
/**
 *
 * @author shadow
 */
public class bublbobl {

            static MemoryReadAddress readmem[] =
            {
            //    { 0xf66e, 0xf66e, bublbobl_read_f66e },
                new MemoryReadAddress( 0xfc1f, 0xfc1f, input_port_0_r ),
                new MemoryReadAddress( 0xfc20, 0xfc20, input_port_1_r ),
                new MemoryReadAddress( 0xfc21, 0xfc21, input_port_2_r ),
                new MemoryReadAddress( 0xfc22, 0xfc22, input_port_3_r ),
                new MemoryReadAddress( 0xfc23, 0xfc23, input_port_4_r ),
                new MemoryReadAddress( 0xc000, 0xffff, bublbobl_sharedram_r ),
                new MemoryReadAddress( 0x0000, 0xbfff, MRA_ROM ),
                new MemoryReadAddress( -1 )  /* end of table */
            };

            static MemoryWriteAddress writemem[] =
            {
                    new MemoryWriteAddress( 0xc000, 0xdcff, bublbobl_videoram_w, videoram, videoram_size ),
                    new MemoryWriteAddress( 0xdd00, 0xdfff, bublbobl_objectram_w, bublbobl_objectram, bublbobl_objectram_size ),	/* handled by bublbobl_sharedram_w() */
            //	{ 0xf7fe, 0xf7ff, bublbobl_write_f7fe },
            //	{ 0xfa00, 0xfa00, bublbobl_play_sound },
                    new MemoryWriteAddress( 0xfb40, 0xfb40, bublbobl_bankswitch_w ),
                    new MemoryWriteAddress( 0xf800, 0xf9ff, bublbobl_paletteram_w, bublbobl_paletteram ),
                    new MemoryWriteAddress( 0xc000, 0xffff, bublbobl_sharedram_w, bublbobl_sharedram ),
                    new MemoryWriteAddress( 0x0000, 0xbfff, MWA_ROM ),
                    new MemoryWriteAddress( -1 )  /* end of table */
            };

            static MemoryReadAddress readmem_lvl[] =
            {
                     new MemoryReadAddress( 0xc000, 0xffff, bublbobl_sharedram_r ),
                     new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
                     new MemoryReadAddress( -1 )  /* end of table */
            };

            static MemoryWriteAddress writemem_lvl[] =
            {
                    new MemoryWriteAddress( 0xcd00, 0xd4ff, bublbobl_videoram_w ),
                    new MemoryWriteAddress( 0xc000, 0xffff, bublbobl_sharedram_w ),
                    new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
                    new MemoryWriteAddress( -1 )  /* end of table */
            };



            static InputPort bublbobl_input_ports[] =
            {
                new InputPort( /* 0: IN0 - FC1F */
                    0xff,
                    new int[]{ 0, 0, OSD_KEY_3, OSD_KEY_4, 0, 0, 0, 0 }

                ),
                new InputPort(	/* 1: DSWA - FC20 */
                    0xff,
                    new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 } /* test mode */

                ),
                new InputPort(	/* 2: DSWB - FC21 */
                    0xff,
                    new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }

                ),
                new InputPort(	/* 3: IN1 - FC22 */
                    0xff,
                    new int[]{ OSD_KEY_LEFT, OSD_KEY_RIGHT, 0, 0,
                      OSD_KEY_ALT, OSD_KEY_CONTROL, OSD_KEY_1, 0 }

                ),
                new InputPort(	/* 4: IN2 - FC23 */
                    0xff,
                    new int[]{ OSD_KEY_Q, OSD_KEY_W, 0, 0,
                      OSD_KEY_I, OSD_KEY_O, OSD_KEY_2, 0 }

                ),
                new InputPort( -1 )  /* end of table */
            };

            static TrakPort trak_ports[] =
            {
                 new TrakPort(-1)
            };

            static KEYSet keys[] =
            {
                new KEYSet( 3, 0, "P1 LEFT" ),    new KEYSet( 3, 1, "P1 RIGHT" ),
                new KEYSet( 3, 4, "P1 JUMP" ),    new KEYSet( 3, 5, "P1 BUBBLE" ),
                new KEYSet( 4, 0, "P2 LEFT" ),    new KEYSet( 4, 1, "P2 RIGHT" ),
                new KEYSet( 4, 4, "P2 JUMP" ),    new KEYSet( 4, 5, "P2 BUBBLE" ),
                new KEYSet( 3, 6, "P1 START" ),   new KEYSet( 4, 6, "P2 START" ),
                new KEYSet( -1 )
            };

            static DSW dsw[] =
            {
                new DSW( 2, 0x30, "LIVES", new String[]{ "2", "1", "5", "3" } ),
                new DSW( 2, 0x0c, "BONUS", new String[]{ "50/250", "40/200", "20/80", "30/100" } ),
                new DSW( 2, 0x03, "DIFFICULTY", new String[]{ "VERY HARD", "HARD", "EASY", "NORMAL" } ),
                new DSW( 1, 0x01, "LANGUAGE", new String[]{ "ENGLISH", "JAPANESE" } ),
                new DSW( 1, 0x30, "SLOT A", new String[]{ "2C/3P", "2C/1P", "1C/2P", "1C/1P" } ),
                new DSW( 1, 0xc0, "SLOT B", new String[]{ "2C/3P", "2C/1P", "1C/2P", "1C/1P" } ),
                new DSW( 1, 0x08, "ATTRACT SND",new String[] { "OFF", "ON" } ),
                new DSW( 1, 0x04, "TEST MODE", new String[]{ "ON", "OFF" } ),
                new DSW( 1, 0x02, "REV SCREEN", new String[]{ "ON", "OFF" } ),
                new DSW( 2, 0xc0, "SPARE", new String[]{ "A", "B", "C", "D" } ),
                new DSW( -1 )
            };



            static GfxLayout charlayout = new GfxLayout
            (
                    8,8,	/* the characters are 8x8 pixels */
                    256*8*6,			/* 256 characters per bank,
                                                            * 8 banks per ROM pair,
                                                            * 6 ROM pairs */
                    4,	/* 4 bits per pixel */
                    new int[]{ 0, 4, 6*0x8000*8, 6*0x8000*8+4 },
                    new int[]{ 3, 2, 1, 0, 11, 10, 9, 8 },
                    new int[]{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
                    16*8	/* every char takes 16 bytes in two ROMs */
            );

           static GfxDecodeInfo gfxdecodeinfo[] =
            {
                    /* read all graphics into one big graphics region */
                    new GfxDecodeInfo( 1, 0x00000, charlayout, 0, 16 ),
                    new GfxDecodeInfo( -1 )	/* end of array */
            };



            static MachineDriver boblbobl_machine_driver = new MachineDriver
            (
                /* basic machine hardware */
                new MachineCPU[] {
                    new MachineCPU(
                        CPU_Z80,
                        6000000,		/* 6 Mhz??? */
                        0,			/* memory_region */
                        readmem,writemem,null,null,
                        bublbobl_interrupt, 1
                    ),
                    new MachineCPU(
                        CPU_Z80,
                        4000000,		/* 4 Mhz??? - if this is too low then collision
                                             * detection gets lazy - it becomes hard to pop
                                             * or bounce on bubbles - if it gets too high
                                             * then the game slows down. */
                        2,			/* memory_region */
                        readmem_lvl,writemem_lvl,null,null,
                        interrupt, 1
                   	)
                },
                60,				/* frames_per_second */
                boblbobl_init,		/* init_machine() */

                /* video hardware */
                32*8, 32*8,			/* screen_width, height */
                new rectangle( 0, 32*8-1, 2*8, 30*8-1 ), /* visible_area */
                gfxdecodeinfo,
                256, /* total_colours */
                16*16,		/* color_table_len */
                bublbobl_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
                null,
                bublbobl_vh_start,
                bublbobl_vh_stop,
                bublbobl_vh_screenrefresh,

                null, null, null, null, null		/* sound hardware */
            );

            static MachineDriver  bublbobl_machine_driver = new MachineDriver
            (
                /* basic machine hardware */
                new MachineCPU[] {
                    new MachineCPU(
                        CPU_Z80,
                        6000000,		/* 6 Mhz??? */
                        0,			/* memory_region */
                        readmem,writemem,null,null,
                        bublbobl_interrupt, 1
                    ),
		new MachineCPU(
                        CPU_Z80,
                        4000000,		/* 4 Mhz??? - if this is too low then collision
                                             * detection gets lazy - it becomes hard to pop
                                             * or bounce on bubbles - if it gets too high
                                             * then the game slows down. */
                        2,			/* memory_region */
                        readmem_lvl,writemem_lvl,null,null,
                        interrupt, 1
                   )
                },
                60,				/* frames_per_second */
                bublbobl_init,		/* init_machine() */

                /* video hardware */
                32*8, 32*8,			/* screen_width, height */
                new rectangle( 0, 32*8-1, 2*8, 30*8-1 ), /* visible_area */
                gfxdecodeinfo,
                256, /* total_colours */
                16*16,		/* color_table_len */
                bublbobl_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,   
                null,
                bublbobl_vh_start,
                bublbobl_vh_stop,
                bublbobl_vh_screenrefresh,

                null, null, null, null, null		/* sound hardware */
            );



            /***************************************************************************

              Game driver(s)

            ***************************************************************************/
            static RomLoadPtr boblbobl_rom = new RomLoadPtr(){ public void handler(){ 
            
               ROM_REGION(0x40000);	/* 4 * 64k for the first CPU (4 banks) */
              ROM_LOAD( "bb3", 0x00000, 0x8000, 0x9202336e );
                ROM_RELOAD(      0x10000, 0x8000 );
               ROM_RELOAD(      0x20000, 0x8000 );
                ROM_RELOAD(      0x30000, 0x8000 );
              ROM_LOAD( "bb5", 0x08000, 0x4000, 0x0d5aa1e0 );	/* first bank */
                ROM_CONTINUE(    0x18000, 0x4000 );				/* second bank */
              ROM_LOAD( "bb4", 0x28000, 0x4000, 0xc6fc6192 );	/* third bank */
                ROM_CONTINUE(    0x38000, 0x4000 );				/* fourth bank */

               ROM_REGION(0x60000);	/* temporary space for graphics (disposed after conversion) */
              ROM_LOAD( "a78_09.bin", 0x00000, 0x8000, 0x4c2d6dc7 );
              ROM_LOAD( "a78_10.bin", 0x08000, 0x8000, 0x58e234c8 );
              ROM_LOAD( "a78_11.bin", 0x10000, 0x8000, 0x12787a3c );
              ROM_LOAD( "a78_12.bin", 0x18000, 0x8000, 0xcb0e4600 );
              ROM_LOAD( "a78_13.bin", 0x20000, 0x8000, 0x08e59a77 );
              ROM_LOAD( "a78_14.bin", 0x28000, 0x8000, 0xda100c26 );
              ROM_LOAD( "a78_15.bin", 0x30000, 0x8000, 0xe24b0f79 );
              ROM_LOAD( "a78_16.bin", 0x38000, 0x8000, 0x82f9f47b );
              ROM_LOAD( "a78_17.bin", 0x40000, 0x8000, 0x02935fdb );
              ROM_LOAD( "a78_18.bin", 0x48000, 0x8000, 0x41e639be );
              ROM_LOAD( "a78_19.bin", 0x50000, 0x8000, 0xd7b322af );
              ROM_LOAD( "a78_20.bin", 0x58000, 0x8000, 0xd2704b1e );

                ROM_REGION(0x10000);	/* 64k for the second CPU */
              ROM_LOAD( "a78_08.bin", 0x0000, 0x08000, 0xa11cdcf4 );
               ROM_END();
            }};
            static RomLoadPtr bublbobl_rom = new RomLoadPtr(){ public void handler(){ 
                ROM_REGION(0x40000);	/* 4 * 64k for the first CPU (4 banks) */
              ROM_LOAD( "a78_06.bin", 0x00000, 0x8000, 0x54bbd2ad );
                ROM_RELOAD(             0x10000, 0x8000 );
                ROM_RELOAD(             0x20000, 0x8000 );
                ROM_RELOAD(             0x30000, 0x8000 );
              ROM_LOAD( "a78_05.bin", 0x08000, 0x4000, 0x366354e5 );	/* first bank */
                ROM_CONTINUE(           0x18000, 0x4000 );			/* second bank */
                ROM_CONTINUE(           0x28000, 0x4000 );				/* third bank */
                ROM_CONTINUE(           0x38000, 0x4000 );				/* fourth bank */

               ROM_REGION(0x60000);/* temporary space for graphics (disposed after conversion) */
              ROM_LOAD( "a78_09.bin", 0x00000, 0x8000, 0x4c2d6dc7 );
              ROM_LOAD( "a78_10.bin", 0x08000, 0x8000, 0x58e234c8 );
              ROM_LOAD( "a78_11.bin", 0x10000, 0x8000, 0x12787a3c );
              ROM_LOAD( "a78_12.bin", 0x18000, 0x8000, 0xcb0e4600 );
              ROM_LOAD( "a78_13.bin", 0x20000, 0x8000, 0x08e59a77 );
              ROM_LOAD( "a78_14.bin", 0x28000, 0x8000, 0xda100c26 );
              ROM_LOAD( "a78_15.bin", 0x30000, 0x8000, 0xe24b0f79 );
              ROM_LOAD( "a78_16.bin", 0x38000, 0x8000, 0x82f9f47b );
              ROM_LOAD( "a78_17.bin", 0x40000, 0x8000, 0x02935fdb );
              ROM_LOAD( "a78_18.bin", 0x48000, 0x8000, 0x41e639be );
              ROM_LOAD( "a78_19.bin", 0x50000, 0x8000, 0xd7b322af );
              ROM_LOAD( "a78_20.bin", 0x58000, 0x8000, 0xd2704b1e );

               ROM_REGION(0x10000);	/* 64k for the second CPU */
              ROM_LOAD( "a78_08.bin", 0x0000, 0x08000, 0xa11cdcf4 );
               ROM_END();
            }};



            /* High Score memory, etc
             *
             *  E64C-E64E - the high score - initialised with points for first extra life
             *
             *  [ E64F-E653 - memory for copy protection routine ]
             *
             *  E654/5/6 score 1     E657 level     E658/9/A initials
             *  E65B/C/D score 2     E65E level     E65f/0/1 initials
             *  E662/3/4 score 3     E665 level     E666/7/8 initials
             *  E669/A/B score 4     E66C level     E66d/E/F initials
             *  E670/1/2 score 5     E673 level     E674/5/6 initials
             *
             *  E677 - position we got to in the high scores
             *  E67B - initialised to 1F - record level number reached
             *  E67C - initialised to 1F - p1's best level reached
             *  E67D - initialised to 13 - p2's best level reached
             */
             static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name)
             {
                 return 0; //not yet
             }};

            /*static int hiload(const char *name)
            {
                    /* get RAM pointer (this game is multiCPU, we can't assume the global */
                    /* RAM pointer is pointing to the right place) */
            /*        unsigned char *RAM = Machine->memory_region[0];


                    /* check if the hi score table has already been initialized */
              /*      if (memcmp(&RAM[0xe654],&RAM[0xe670],3) == 0 &&
                                    memcmp(&RAM[0xe654],&RAM[0xe64c],3) == 0 &&	/* high score */
             /*                       memcmp(&RAM[0xe67b],"\x1f\x1f\x13",3) == 0 &&	/* level number */
             /*                       (memcmp(&RAM[0xe654],"\x00\x20\x00",3) == 0 ||
                                    memcmp(&RAM[0xe654],"\x00\x30\x00",3) == 0 ||
                                    memcmp(&RAM[0xe654],"\x00\x40\x00",3) == 0 ||
                                    memcmp(&RAM[0xe654],"\x00\x50\x00",3) == 0))
                    {
                            FILE *f;


                            if ((f = fopen(name,"rb")) != 0)
                            {
                                    fread(&RAM[0xe654],1,7*5,f);
                                    fread(&RAM[0xe67b],1,3,f);
                                    RAM[0xe64c] = RAM[0xe654];
                                    RAM[0xe64d] = RAM[0xe655];
                                    RAM[0xe64e] = RAM[0xe656];
                                    fclose(f);
                            }

                            return 1;
                    }
                    else return 0;	/* we can't load the hi scores yet */
         /*   }*/

        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name){

        }};

       /*     static void hisave(const char *name)
            {
                    FILE *f;
                    /* get RAM pointer (this game is multiCPU, we can't assume the global */
                    /* RAM pointer is pointing to the right place) */
    /*                unsigned char *RAM = Machine->memory_region[0];


                    if ((f = fopen(name,"wb")) != 0)
                    {
                            fwrite(&RAM[0xe654],1,7*5,f);
                            fwrite(&RAM[0xe67b],1,3,f);
                            fclose(f);
                    }
            }*/



            public static GameDriver boblbobl_driver =new GameDriver
            (
                    "Bobble Bobble",
                    "boblbobl",
                    "CHRIS MOORE\nOLIVER WHITE\nNICOLA SALMORIA",
                    boblbobl_machine_driver, boblbobl_rom, null, null, null,
                    bublbobl_input_ports,null, trak_ports, /* input_ports, trak_ports */
                    dsw, keys,

                    null, null, null,
                    ORIENTATION_DEFAULT,			/* paused_x, paused_y */
                    hiload, hisave
            );

            public static GameDriver bublbobl_driver =new GameDriver
            (
                    "Bubble Bobble",
                    "bublbobl",
                    "CHRIS MOORE\nOLIVER WHITE\nNICOLA SALMORIA",
                    bublbobl_machine_driver, bublbobl_rom, null, null, null,
                    bublbobl_input_ports,null, trak_ports, /* input_ports, trak_ports */
                    dsw, keys,

                    null, null, null,
                    ORIENTATION_DEFAULT,			/* paused_x, paused_y */
                    hiload, hisave
            );

}
