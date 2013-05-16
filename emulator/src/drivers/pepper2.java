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
 * ported to v0.28
 *
 *
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
import static vidhrdw.generic.*;
import static vidhrdw.exidy.*;
import static mame.memoryH.*;

public class pepper2 {
    
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x03ff, MRA_RAM ),
                new MemoryReadAddress( 0x4000, 0x43ff, MRA_RAM ),
                new MemoryReadAddress( 0x5100, 0x5100, input_port_0_r ),	/* DSW */
                new MemoryReadAddress( 0x5101, 0x5101, input_port_1_r ),	/* IN0 */
                new MemoryReadAddress( 0x5103, 0x5103, input_port_2_r ),	/* IN1 */
                new MemoryReadAddress( 0x6000, 0x6fff, MRA_RAM ),
                new MemoryReadAddress( 0x7000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                 new MemoryWriteAddress( 0x0000, 0x3fff, MWA_RAM ),
                 new MemoryWriteAddress( 0x4000, 0x43ff, videoram_w, videoram, videoram_size ),
                 new MemoryWriteAddress( 0x5000, 0x5000, MWA_RAM, exidy_sprite1_xpos ),
                 new MemoryWriteAddress( 0x5040, 0x5040, MWA_RAM, exidy_sprite1_ypos ),
                 new MemoryWriteAddress( 0x5080, 0x5080, MWA_RAM, exidy_sprite2_xpos ),
                 new MemoryWriteAddress( 0x50C0, 0x50C0, MWA_RAM, exidy_sprite2_ypos ),
                 new MemoryWriteAddress( 0x5100, 0x5100, MWA_RAM, exidy_sprite_no ),
                 new MemoryWriteAddress( 0x5101, 0x5101, MWA_RAM, exidy_sprite_enable ),
                 new MemoryWriteAddress( 0x6000, 0x6fff, exidy_characterram_w, exidy_characterram ),
                 new MemoryWriteAddress( 0x7000, 0xffff, MWA_ROM ),
                 new MemoryWriteAddress( -1 )	/* end of table */
        };


        static MemoryReadAddress sound_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x67ff, MRA_RAM ),
                new MemoryReadAddress( 0x6800, 0x7fff, MRA_ROM ),
                new MemoryReadAddress( 0x8000, 0xf7ff, MRA_RAM ),
                new MemoryReadAddress( 0xf800, 0xffff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress sound_writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x67ff, MWA_RAM ),
                new MemoryWriteAddress( 0x6800, 0x7fff, MWA_ROM ),
                new MemoryWriteAddress( 0x8000, 0xf7ff, MWA_RAM ),
                new MemoryWriteAddress( 0xf800, 0xffff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };


        static InputPort input_ports[] =
        {
                new InputPort(	/* DSW */
                        0xb0,
                        new int[]{ OSD_KEY_4, OSD_KEY_5, OSD_KEY_5, OSD_KEY_5, OSD_KEY_5, OSD_KEY_5, OSD_KEY_5, OSD_KEY_5 }
                ),
		new InputPort(	/* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_1, OSD_KEY_2, OSD_KEY_RIGHT, OSD_KEY_LEFT,
                                        0, OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_3 }
                ),
		new InputPort(	/* IN1 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, OSD_KEY_4, OSD_KEY_3, 0}
                ),
		new InputPort( -1 )	/* end of table */
        };

        static TrakPort[] trak_ports =
        {
           new TrakPort(-1)
        };


        static KEYSet keys[] =
        {
                new KEYSet( 1, 5, "MOVE UP" ),
                new KEYSet( 1, 3, "MOVE LEFT"  ),
                new KEYSet( 1, 2, "MOVE RIGHT" ),
                new KEYSet( 1, 6, "MOVE DOWN" ),
                new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                new DSW( 0, 0x60, "LIVES",  new String[]{ "5", "4", "3", "2" } ),
                new DSW( 0, 0x06, "BONUS",  new String[]{ "50000", "40000", "30000", "20000" } ),
                new DSW( 0, 0x18, "COIN SELECT",  new String[]{ "1 COIN 4 CREDITS", "1 COIN 2 CREDITS", "2 COINS 1 CREDIT", "1 COIN 1 CREDIT" } ),
                new DSW( -1 )
        };



        static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                256,	/* 256 characters */
                2,	/* 2 bits per pixel */
                 new int[]{ 0, 256*8*8 }, /* 2 bits separated by 0x0800 bytes */
                 new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },
                 new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                8*8	/* every char takes 8 consecutive bytes */
        );
        static GfxLayout spritelayout = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                16*4,	/* 8 characters */
                1,	/* 1 bit per pixel */
                 new int[]{ 0 },
                 new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7},
                 new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8, 8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8},
                8*32	/* every char takes 8 consecutive bytes */
        );


        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                 new GfxDecodeInfo( 0, 0x6000, charlayout,   0,       10*2 ),	/* the game dynamically modifies this */
                 new GfxDecodeInfo( 1, 0x0000, spritelayout, (10*2)*4, 2*2 ),  /* Angel/Devil/Zipper Ripper */
                 new GfxDecodeInfo(-1 ) /* end of array */
        };



        static char palette[] =
        {
                0x00,0x00,0x00,   /* black      */
                0x80,0x00,0x80,   /* darkpurple */
                0x80,0x00,0x00,   /* darkred    */
                0xf8,0x64,0xd8,   /* pink       */	/* bad guys */
                0x00,0x80,0x00,   /* darkgreen  */	/* "Player 1" */
                0x00,0x80,0x80,   /* darkcyan   */
                0x80,0x80,0x00,   /* darkyellow */
                0x80,0x80,0x80,   /* darkwhite  */
                0xf8,0x94,0x44,   /* orange     */
                0x00,0x00,0xff,   /* blue   */		/* maze primary, bonus objects */
                0xff,0x00,0x00,   /* red    */		/* maze secondary, bonus objects */
                0xff,0x00,0xff,   /* purple */		/* changed maze secondary */
                0x00,0xff,0x00,   /* green  */		/* pitchfork, changed maze primary */
                0x00,0xff,0xff,   /* cyan   */		/* changed bad guys */
                0xff,0xff,0x00,   /* yellow */		/* angel, bonus objects */
                0xff,0xff,0xff    /* white  */		/* text, devil */
        };
	static final int black = 0;
	static final int darkpurple = 1;
	static final int darkred = 2;
	static final int pink = 3;
	static final int darkgreen = 4;
	static final int darkcyan = 5;
	static final int darkyellow = 6;
	static final int darkwhite = 7;
	static final int orange = 8;
	static final int blue = 9;
	static final int red = 10;
	static final int purple = 11;
	static final int green = 12;
	static final int cyan = 13;
	static final int yellow = 14;
	static final int white = 15;


        static char colortable[] =
        {
                /* text colors */
                black, black, black, white,
                black, black, black, white,

                /* maze path colors */
                black, black, blue, red,
                black, black, green, purple,

                /* Roaming Eyes */
                black, black, blue, red,
                black, black, green, purple,

                /* unused Angels at top of screen */
                black, black, yellow, yellow,
                black, black, yellow, yellow,

                /* box fill */
                black, black, blue, white,
                black, black, white, blue,

                /* pitchfork */
                black, black, green, yellow,
                black, black, green, yellow,

                /* "Pepper II" */
                black, black, yellow, yellow,
                black, black, yellow, yellow,

                /* Maze number boxes */
                black, red, yellow, blue,
                black, red, yellow, blue,

                /* Exit signs */
                black, red, blue, white,
                black, red, blue, white,

                /* Bonus objects */
                black, blue, red, yellow,
                black, blue, red, yellow,

                /* Angel/Devil */
                black, yellow, black, white,

                /* Zipper Ripper */
                black, green, black, green,

        };
        static final int c_text=0; 
        static final int c_path=1;
        static final int c_eyes=2; 
        static final int c_angl=3; 
        static final int c_fill=4; 
        static final int c_fork=5;
        static final int c_ppr2=6;
        static final int c_mbox=7;
        static final int c_exit=8;
        static final int c_bnus=9;


        static char pepper2_color_lookup[] =
        {
                /* 0x00-0x0F */
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0x10-0x1F */
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0x20-0x2F */
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0x30-0x3F */
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0x40-0x4F */
                c_path, c_path, c_path, c_path, c_path, c_path, c_path, c_path,
                c_path, c_path, c_path, c_path, c_path, c_path, c_path, c_path,
                /* 0x50-0x5F */
                c_path, c_path, c_path, c_path, c_path, c_path, c_eyes, c_eyes,
                c_eyes, c_eyes, c_eyes, c_eyes, c_eyes, c_eyes, c_eyes, c_eyes,
                /* 0x60-0x6F */
                c_eyes, c_eyes, c_eyes, c_eyes, c_eyes, c_eyes, c_eyes, c_eyes,
                c_eyes, c_angl, c_angl, c_angl, c_angl, c_angl, c_angl, c_angl,
                /* 0x70-0x7F */
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0x80-0x8F */
                c_fill, c_fork, c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2,
                c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2,
                /* 0x90-0x9F */
                c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2,
                c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2, c_ppr2,
                /* 0xA0-0xAF */
                c_ppr2, c_fork, c_text, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0xB0-0xBF */
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0xC0-0xCF */
                c_mbox, c_mbox, c_mbox, c_mbox, c_mbox, c_mbox, c_mbox, c_mbox,
                c_mbox, c_mbox, c_mbox, c_mbox, c_mbox, c_mbox, c_mbox, c_mbox,
                /* 0xD0-0xDF */
                c_mbox, c_mbox, c_mbox, c_mbox, c_mbox, c_mbox, c_mbox, c_mbox,
                c_exit, c_exit, c_exit, c_exit, c_exit, c_exit, c_bnus, c_bnus,
                /* 0xE0-0xEF */
                c_bnus, c_bnus, c_bnus, c_bnus, c_bnus, c_bnus, c_bnus, c_bnus,
                c_bnus, c_bnus, c_bnus, c_bnus, c_bnus, c_bnus, c_bnus, c_bnus,
                /* 0xF0-0xFF */
                c_bnus, c_bnus, c_bnus, c_bnus, c_bnus, c_bnus, c_bnus, c_bnus,
                c_bnus, c_bnus, c_bnus, c_bnus, c_bnus, c_bnus, c_bnus, c_bnus,
        };

        public static InitMachinePtr pepper2_init_machine = new InitMachinePtr() { public void handler()
	{

                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char RAM[]=Machine.memory_region[0];

                /* Disable ROM Check for quicker startup */
                RAM[0xF52D]=0xEA;
                RAM[0xF52E]=0xEA;
                RAM[0xF52F]=0xEA;

                /* Set color lookup table to point to us */
                exidy_color_lookup.set(pepper2_color_lookup,0);
        }};


        public static MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_M6502,
                                1000000,	/* 1 Mhz ???? */
                                0,
                                readmem,writemem,null,null,
                                interrupt,1
                        ),
                        new MachineCPU(
                                CPU_M6502,
                                1000000,	/* 1 Mhz ???? */
                                2,
                                sound_readmem,sound_writemem,null,null,
                                interrupt,1
                        )
                },
                60,
                pepper2_init_machine,

                /* video hardware */
                32*8, 32*8, new rectangle( 0*8, 32*8-1, 0*8, 32*8-1 ),
                gfxdecodeinfo,
                sizeof(palette)/3,sizeof(colortable),
                null,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
                null,
                generic_vh_start,
                generic_vh_stop,
                exidy_vh_screenrefresh,

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
        static RomLoadPtr pepper2_rom =new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "main_12a", 0x9000, 0x1000, 0x0a47e1e3 );
                ROM_LOAD( "main_11a", 0xA000, 0x1000, 0x120e0da0 );
                ROM_LOAD( "main_10a", 0xB000, 0x1000, 0x4ab11dc7 );
                ROM_LOAD( "main_9a",  0xC000, 0x1000, 0xa6f0e57e );
                ROM_LOAD( "main_8a",  0xD000, 0x1000, 0x04b6fd2a );
                ROM_LOAD( "main_7a",  0xE000, 0x1000, 0x9e350147 );
                ROM_LOAD( "main_6a",  0xF000, 0x1000, 0xda172fe9 );

                ROM_REGION(0x0800);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "main_11d", 0x0000, 0x0800, 0x8ae4f8ba );

                ROM_REGION(0x10000);	/* 64k for audio */
                ROM_LOAD( "audio_5a", 0x6800, 0x0800, 0x96de524a );
                ROM_LOAD( "audio_6a", 0x7000, 0x0800, 0xf2685a2c );
                ROM_LOAD( "audio_7a", 0x7800, 0x0800, 0xe5a6f8ec );
                ROM_LOAD( "audio_7a", 0xF800, 0x0800, 0xe5a6f8ec );
                ROM_END();
        }};


        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
        {
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char RAM[]=Machine.memory_region[0];

                /* check if the hi score table has already been initialized */
     /*TOFIX           if ((memcmp(RAM,0x0360,new char[]{0x00,0x06,0x0C,0x12,0x18},5) == 0) &&
                        (memcmp(RAM,0x0380,new char[]{0x15,0x20,0x11},3) == 0))
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x0360,1,5+6*5,f);
                                fclose(f);
                        }

                        return 1;
                }
                else */return 0;	/* we can't load the hi scores yet */
        }};



        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
	{
                FILE f;

                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char RAM[]=Machine.memory_region[0];

       /*TOFIX         if ((f = fopen(name,"wb")) != null)
                {
                        /* 5 bytes for score order, 6 bytes per score/initials */
        /*TOFIX                fwrite(RAM,0x0360,1,5+6*5,f);
                        fclose(f);
                }*/

        }};


        public static GameDriver pepper2_driver = new GameDriver
	(
                "Pepper II",
                "pepper2",
                "MARC LAFONTAINE\nBRIAN LEVINE\nMIKE BALFOUR",
                machine_driver,

                pepper2_rom,
                null, null,
                null,

                input_ports, null, trak_ports, dsw, keys,

                null, palette, colortable,
                ORIENTATION_DEFAULT,

                hiload,hisave
        );    
}
