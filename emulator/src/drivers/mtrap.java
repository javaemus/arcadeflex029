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

public class mtrap {
        public static CharPtr mtrap_i2r= new CharPtr();

       public static ReadHandlerPtr mtrap_input_port_2_r = new ReadHandlerPtr() {
        public int handler(int offset) {
                int value;

                /* Get 2 coin keys */
                value=input_port_2_r.handler(offset) & 0x60;

                /* Combine with what's in memory */
                value=value | (mtrap_i2r.read() & 0x9F);

                /* Character collision */
                if (exidy_sprite1_xpos.read() == 0x78 && exidy_sprite1_ypos.read() == 0x38)
                        value=value & 0xFB;
                else
                        value=value | 0x04;

                /* Sprite collision */
                if (	(exidy_sprite1_xpos.read() + 0x10 < exidy_sprite2_xpos.read()) ||
                        (exidy_sprite1_xpos.read() > exidy_sprite2_xpos.read() + 0x10) ||
                        (exidy_sprite1_ypos.read() + 0x10 < exidy_sprite2_ypos.read()) ||
                        (exidy_sprite1_ypos.read() > exidy_sprite2_ypos.read() + 0x10) ||
                        ((exidy_sprite_enable.read()&0x40)!=0))
                {
                        value=value & 0xEF;
                }
                else
                        value=value | 0x10;

                return value;
        }};

        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x3fff, MRA_RAM ),
                new MemoryReadAddress( 0x4000, 0x43ff, MRA_RAM ),
                new MemoryReadAddress( 0x4800, 0x4fff, MRA_RAM ),
                new MemoryReadAddress( 0x5100, 0x5100, input_port_0_r ),	/* DSW */
                new MemoryReadAddress( 0x5101, 0x5101, input_port_1_r ),	/* IN0 */
                new MemoryReadAddress( 0x5103, 0x5103, mtrap_input_port_2_r, mtrap_i2r ),	/* IN1 */
                new MemoryReadAddress( 0x5213, 0x5213, input_port_3_r ),	/* IN2 */
                new MemoryReadAddress( 0x8000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x3fff, MWA_RAM ),
                new MemoryWriteAddress( 0x4000, 0x43ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x4800, 0x4fff, exidy_characterram_w, exidy_characterram ),
                new MemoryWriteAddress( 0x5000, 0x5000, MWA_RAM, exidy_sprite1_xpos ),
                new MemoryWriteAddress( 0x5040, 0x5040, MWA_RAM, exidy_sprite1_ypos ),
                new MemoryWriteAddress( 0x5080, 0x5080, MWA_RAM, exidy_sprite2_xpos ),
                new MemoryWriteAddress( 0x50C0, 0x50C0, MWA_RAM, exidy_sprite2_ypos ),
                new MemoryWriteAddress( 0x5100, 0x5100, MWA_RAM, exidy_sprite_no ),
                new MemoryWriteAddress( 0x5101, 0x5101, MWA_RAM, exidy_sprite_enable ),
                new MemoryWriteAddress( 0x8000, 0xffff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };


        static MemoryReadAddress sound_readmem[] =
        {
                 new MemoryReadAddress( 0x0000, 0x57ff, MRA_RAM ),
                 new MemoryReadAddress( 0x5800, 0x7fff, MRA_ROM ),
                 new MemoryReadAddress( 0x8000, 0xf7ff, MRA_RAM ),
                 new MemoryReadAddress( 0xf800, 0xffff, MRA_ROM ),
                 new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress sound_writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x57ff, MWA_RAM ),
                new MemoryWriteAddress( 0x5800, 0x7fff, MWA_ROM ),
                new MemoryWriteAddress( 0x8000, 0xf7ff, MWA_RAM ),
                new MemoryWriteAddress( 0xf800, 0xffff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };


        static InputPort input_ports[] =
        {
                new InputPort(	/* DSW */
                        0xb0,
                        new int[]{ OSD_KEY_4, 0, 0, 0, 0, 0, 0, 0 }
                 ),
		new InputPort(	/* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_1, OSD_KEY_2, OSD_KEY_RIGHT, OSD_KEY_LEFT,
                                        OSD_KEY_CONTROL, OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_3 }
                 ),
		new InputPort(	/* IN1 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, OSD_KEY_4, OSD_KEY_3, 0 }
                 ),
		new InputPort(	/* IN2 */
                        0xff,
                        new int[]{ OSD_KEY_X, OSD_KEY_Z, 0, OSD_KEY_C, 0, 0, 0, 0 }
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
                new KEYSet( 1, 4, "DOG BUTTON" ),
                new KEYSet( 3, 1, "RED DOOR BUTTON" ),
                new KEYSet( 3, 0, "YELLOW DOOR BUTTON" ),
                new KEYSet( 3, 3, "BLUE DOOR BUTTON" ),
                new KEYSet( -1 )
        };

        static DSW dsw[] =
        {
                 new DSW( 0, 0x60, "LIVES",  new String[]{ "2", "3", "4", "5" } ),
                 new DSW( 0, 0x06, "BONUS",  new String[]{ "20000", "30000", "40000", "50000" } ),
                 new DSW( 0, 0x18, "COIN SELECT",  new String[]{ "1 COIN 4 CREDITS", "1 COIN 2 CREDITS", "2 COINS 1 CREDIT", "1 COIN 1 CREDIT" } ),
                 new DSW( -1 )
        };



        static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                256,	/* 256 characters */
                1,	/* 1 bits per pixel */
                new int[]{ 0 }, /* No info needed for bit offsets */
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
                new GfxDecodeInfo( 0, 0x4800, charlayout,   0,       13*2 ),	/* the game dynamically modifies this */
                new GfxDecodeInfo( 1, 0x0000, spritelayout, (13*2)*2, 2*2 ),  /* Sprites */
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        static char palette[] =
        {
                0x00,0x00,0x00,   /* black      */
                0x80,0x00,0x80,   /* darkpurple */
                0x80,0x00,0x00,   /* darkred    */
                0xf8,0x64,0xd8,   /* pink       */
                0x00,0x80,0x00,   /* darkgreen  */
                0x00,0x80,0x80,   /* darkcyan   */
                0x80,0x80,0x00,   /* darkyellow */
                0x80,0x80,0x80,   /* darkwhite  */
                0xf8,0x94,0x44,   /* orange     */
                0x00,0x00,0xff,   /* blue   */
                0xff,0x00,0x00,   /* red    */
                0xff,0x00,0xff,   /* purple */
                0x00,0xff,0x00,   /* green  */
                0x00,0xff,0xff,   /* cyan   */
                0xff,0xff,0x00,   /* yellow */
                0xff,0xff,0xff    /* white  */
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
                black, green,
                black, green,

                /* maze path colors */
                black, green,
                black, green,

                /* dot colors */
                black, yellow,
                black, yellow,

                /* cat colors */
                black, yellow,
                black, yellow,

                /* "IN" box */
                purple, white,
                purple, white,

                /* Red door */
                black, red,
                black, red,

                /* blue door */
                black, blue,
                black, blue,

                /* yellow door */
                black, yellow,
                black, yellow,

                /* bonus objects */
                black, red,
                black, red,

                /* dog biscuit */
                black, red,
                black, red,

                /* extra mouse */
                black, cyan,
                black, cyan,

                /* hawk (for instructions) */
                black, purple,
                black, purple,

                /* dog (for instructions) */
                black, red,
                black, red,

                /* Mouse/Dog */
                black, cyan,
                black, red,

                /* Hawk */
                black, purple,
                black, purple,

        };

        static final int c_text=0; 
        static final int c_path=1;
        static final int c_dots=2; 
        static final int c_cats=3; 
        static final int c_ibox=4; 
        static final int c_rddr=5;
        static final int c_bldr=6;
        static final int c_yldr=7;
        static final int c_bnus=8;
        static final int c_bsct=9;
        static final int c_xmse=10;
        static final int c_hawk=11;
        static final int c_dogs=12;
        
        static char mtrap_color_lookup[] =
        {
                /* 0x00-0x0F */
                c_bldr, c_bldr, c_bldr, c_bldr, c_ibox, c_ibox, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0x10-0x1F */
                c_text, c_text, c_text, c_text, c_hawk, c_hawk, c_hawk, c_hawk,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0x20-0x2F */
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0x30-0x3F */
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0x40-0x4F */
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0x50-0x5F */
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_path, c_path, c_path,
                /* 0x60-0x6F */
                c_path, c_path, c_path, c_path, c_path, c_path, c_path, c_path,
                c_path, c_path, c_path, c_path, c_path, c_path, c_path, c_path,
                /* 0x70-0x7F */
                c_path, c_path, c_path, c_path, c_path, c_path, c_path, c_path,
                c_path, c_path, c_path, c_path, c_path, c_path, c_path, c_path,
                /* 0x80-0x8F */
                c_rddr, c_rddr, c_rddr, c_rddr, c_bsct, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0x90-0x9F */
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0xA0-0xAF */
                c_text, c_text, c_bnus, c_bnus, c_bnus, c_bnus, c_dogs, c_dogs,
                c_dogs, c_dogs, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0xB0-0xBF */
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0xC0-0xCF */
                c_yldr, c_yldr, c_dots, c_text, c_text, c_text, c_text, c_text,
                c_text, c_text, c_text, c_text, c_text, c_text, c_text, c_text,
                /* 0xD0-0xDF */
                c_text, c_text, c_yldr, c_yldr, c_xmse, c_xmse, c_xmse, c_xmse,
                c_cats, c_cats, c_cats, c_cats, c_cats, c_cats, c_cats, c_cats,
                /* 0xE0-0xEF */
                c_cats, c_cats, c_cats, c_cats, c_cats, c_cats, c_cats, c_cats,
                c_cats, c_cats, c_cats, c_cats, c_cats, c_cats, c_cats, c_cats,
                /* 0xF0-0xFF */
                c_cats, c_cats, c_cats, c_cats, c_cats, c_cats, c_cats, c_cats,
                c_cats, c_cats, c_cats, c_cats, c_cats, c_cats, c_cats, c_cats,
        };


        public static InitMachinePtr mtrap_init_machine = new InitMachinePtr() { public void handler()
	{
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                 char RAM[]=Machine.memory_region[0];

                /* Disable ROM Check for quicker startup */
                RAM[0xF439]=0xEA;
                RAM[0xF43A]=0xEA;
                RAM[0xF43B]=0xEA;

                /* Set color lookup table to point to us */
                exidy_color_lookup.set(mtrap_color_lookup,0);
        }};



        public static MachineDriver mtrap_machine_driver = new MachineDriver
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
                                CPU_M6502 | CPU_AUDIO_CPU,
                                1000000,	/* 1 Mhz ???? */
                                2,	/* memory region #2 */
                                sound_readmem,sound_writemem,null,null,
                                interrupt,1
                        )
                },
                60,
                mtrap_init_machine,

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
        static RomLoadPtr mtrap_rom= new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "mtl11a.bin", 0xA000, 0x1000, 0xb4e109f7 );
                ROM_LOAD( "mtl10a.bin", 0xB000, 0x1000, 0xe890bac6 );
                ROM_LOAD( "mtl9a.bin",  0xC000, 0x1000, 0x06628e86 );
                ROM_LOAD( "mtl8a.bin",  0xD000, 0x1000, 0xa12b0c55 );
                ROM_LOAD( "mtl7a.bin",  0xE000, 0x1000, 0xb5c75a2f );
                ROM_LOAD( "mtl6a.bin",  0xF000, 0x1000, 0x2e7f499b );

                ROM_REGION(0x0800);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "mtl11D.bin", 0x0000, 0x0800, 0x389ef2ec );

                ROM_REGION(0x10000);	/* 64k for audio */
                ROM_LOAD( "mta5a.bin", 0x6800, 0x0800, 0xdc40685a );
                ROM_LOAD( "mta6a.bin", 0x7000, 0x0800, 0x250b2f5f );
                ROM_LOAD( "mta7a.bin", 0x7800, 0x0800, 0x3ba2700a );
                ROM_LOAD( "mta7a.bin", 0xF800, 0x0800, 0x3ba2700a );
                ROM_END();
        }};



        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
        {
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char RAM[]=Machine.memory_region[0];

                /* check if the hi score table has already been initialized */
       /*TOFIX         if ((memcmp(RAM,0x0380,new char[]{0x00,0x06,0x0C,0x12,0x18},5) == 0) &&
                        (memcmp(RAM,0x03A0,new char[]{'L','W','H'},3) == 0))
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x0380,1,5+6*5,f);
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


          /*TOFIX      if ((f = fopen(name,"wb")) != null)
                {
                        /* 5 bytes for score order, 6 bytes per score/initials */
         /*TOFIX               fwrite(RAM,0x0380,1,5+6*5,f);
                        fclose(f);
                }*/

        }};


        public static GameDriver mtrap_driver = new GameDriver
	(
                "Mouse Trap",
                "mtrap",
                "MARC LAFONTAINE\nBRIAN LEVINE\nMIKE BALFOUR",
                mtrap_machine_driver,

                mtrap_rom,
                null, null,
                null,

                input_ports, null, trak_ports, dsw, keys,

                null, palette, colortable,
                ORIENTATION_DEFAULT,

                hiload,hisave
        );    
}
