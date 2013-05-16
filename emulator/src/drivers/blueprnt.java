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
 * ported to v0.29
 * ported to v0.28
 * ported to v0.27
 *
 *NOTES: romsets are from v0.36 roms
 *
 */

package drivers;

import static arcadeflex.libc.*;
import static arcadeflex.osdepend.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.blueprnt.*;
import static sndhrdw.generic.*;
import static vidhrdw.generic.*;
import static vidhrdw.blueprnt.*;
import static mame.memoryH.*;

public class blueprnt {
        public static ReadHandlerPtr pip = new ReadHandlerPtr() { public int handler(int offset)
	{
            if (RAM[0xd000] == 0x11) return readinputport(2);
            else return readinputport(3);
        }};


        static  MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x8000, 0x87ff, MRA_RAM ),
                new MemoryReadAddress( 0x9000, 0x93ff, MRA_RAM ),
                new MemoryReadAddress( 0xf000, 0xf3ff, MRA_RAM ),
                new MemoryReadAddress( 0x0000, 0x4fff, MRA_ROM ),
                new MemoryReadAddress( 0xa000, 0xa01f, MRA_RAM ),
                new MemoryReadAddress( 0xb000, 0xb0ff, MRA_RAM ),
                new MemoryReadAddress( 0xc000, 0xc000, input_port_0_r ),
                new MemoryReadAddress( 0xc001, 0xc001, input_port_1_r ),
                new MemoryReadAddress( 0xc003, 0xc003, pip ),
                new MemoryReadAddress( 0xe000, 0xe000, MRA_NOP ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static  MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x8000, 0x87ff, MWA_RAM ),
                new MemoryWriteAddress( 0x9000, 0x93ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0xf000, 0xf3ff, colorram_w, colorram ),
                new MemoryWriteAddress( 0xa000, 0xa01f, MWA_RAM ),
                new MemoryWriteAddress( 0xb000, 0xb0ff, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0xd000, 0xd000, sound_command_w ),
                new MemoryWriteAddress( 0x0000, 0x4fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static  MemoryReadAddress sound_readmem[] =
        {
                new MemoryReadAddress( 0x4000, 0x43ff, MRA_RAM ),
                new MemoryReadAddress( 0x6002, 0x6002, AY8910_read_port_0_r ),
                new MemoryReadAddress( 0x8002, 0x8002, AY8910_read_port_1_r ),
                new MemoryReadAddress( 0x0000, 0x0fff, MRA_ROM ),
                new MemoryReadAddress( 0x2000, 0x2fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static  MemoryWriteAddress sound_writemem[] =
        {
                new MemoryWriteAddress( 0x4000, 0x43ff, MWA_RAM ),
                new MemoryWriteAddress( 0x6000, 0x6000, AY8910_control_port_0_w ),
                new MemoryWriteAddress( 0x6001, 0x6001, AY8910_write_port_0_w ),
                new MemoryWriteAddress( 0x8000, 0x8000, AY8910_control_port_1_w ),
                new MemoryWriteAddress( 0x8001, 0x8001, AY8910_write_port_1_w ),
                new MemoryWriteAddress( 0x0000, 0x0fff, MWA_ROM ),
                new MemoryWriteAddress( 0x2000, 0x2fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static  InputPort input_ports[] =
        {
                new InputPort(	/* IN0 */
                        0x00,
                        new int[]{ OSD_KEY_3, OSD_KEY_1, OSD_KEY_T, OSD_KEY_CONTROL, OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_UP, OSD_KEY_DOWN }
                ),
		new InputPort(	/* IN1 */
                        0x00,
                        new int[]{ OSD_KEY_4, OSD_KEY_2, OSD_KEY_F2, OSD_KEY_Q, OSD_KEY_W, OSD_KEY_E, OSD_KEY_R, OSD_KEY_T }
                ),
		new InputPort(	/* DSW1 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* DSW2 */
                        0x01,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort( -1 )	/* end of table */
        };

       static TrakPort[] trak_ports =
       {
           new TrakPort(-1)
       };


        static  KEYSet keys[] =
        {
                new KEYSet( 0, 6, "MOVE UP" ),
                new KEYSet( 0, 4, "MOVE LEFT"  ),
                new KEYSet( 0, 5, "MOVE RIGHT" ),
                new KEYSet( 0, 7, "MOVE DOWN" ),
                new KEYSet( 0, 3, "ACCELERATE" ),
                new KEYSet( -1 )
        };


        static  DSW dsw[] =
        {
                new DSW( 3, 0x03, "LIVES", new String[]{ "2", "3", "4", "5" } ),
                new DSW( 2, 0x06, "BONUS", new String[]{ "20000", "30000", "40000", "50000" } ),
                new DSW( 3, 0x30, "DIFFICULTY", new String[]{ "EASY", "MEDIUM", "HARD", "HARDEST" } ),
                new DSW( 2, 0x10, "MAZE MONSTER", new String[]{ "2ND MAZE", "3RD MAZE" } ),
                new DSW( -1 )
        };



        static  GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                512,	/* 512 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 512*8*8 },	/* the bitplanes are separated */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },
                8*8	/* every char takes 8 consecutive bytes */
        );
        static  GfxLayout spritelayout = new GfxLayout
	(
                16,8,	/* 16*16 sprites */
                256,	/* 256 sprites */
                3,	/* 3 bits per pixel */
                new int[]{ 0, 128*16*16, 2*128*16*16 },	/* the bitplanes are separated */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                        8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },
                16*8	/* every sprite takes 32 consecutive bytes */
        );


        static  GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, charlayout,  0, 8 ),
                new GfxDecodeInfo( 1, 0x2000, spritelayout, 8*4, 1 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        static char palette[] =
        {
                0x00,0x00,0x00,	/* BLACK */
                0xff,0x00,0x00, /* RED */
                0x00,0xff,0x00, /* GREEN */
                0x00,0x00,0xff, /* BLUE */
                0xff,0xff,0x00, /* YELLOW */
                0xff,0x00,0xff, /* MAGENTA */
                0x00,0xff,0xff, /* CYAN */
                0xff,0xff,0xff, /* WHITE */
                0xE0,0xE0,0xE0, /* LTGRAY */
                0xC0,0xC0,0xC0, /* DKGRAY */
                0xe0,0xb0,0x70,	/* BROWN */
                0xd0,0xa0,0x60,	/* BROWN0 */
                0xc0,0x90,0x50,	/* BROWN1 */
                0xa3,0x78,0x3a,	/* BROWN2 */
                0x80,0x60,0x20,	/* BROWN3 */
                0x54,0x40,0x14,	/* BROWN4 */
                0x54,0xa8,0xff, /* LTBLUE */
                0x00,0xa0,0x00, /* DKGREEN */
                0x00,0xe0,0x00, /* GRASSGREEN */
                0xff,0xb6,0xdb,	/* PINK */
                0x49,0xb6,0xdb,	/* DKCYAN */
                0xff,96,0x49,	/* DKORANGE */
                0xff,128,0x00,	/* ORANGE */
                0xdb,0xdb,0xdb	/* GREY */
        };


        static char colortable[] =
        {
                0,1,2,3,
                0,4,5,6,
                0,7,8,9,
                0,10,11,12,
                0,13,14,15,
                0,1,3,5,
                0,7,9,11,
                0,13,15,17,

                0,1,2,3,7,4,5,6
        };



        static  MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                3072000,	/* 3.072 Mhz (?) */
                                0,
                                readmem,writemem, null, null,
                                interrupt,1
                        ),
			new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                3072000,	/* 3.072 Mhz ? */
                                2,	/* memory region #2 */
                                sound_readmem,sound_writemem, null, null,
                                blueprnt_sh_interrupt,16
                        )
                },
                60,
                10,	/* 10 CPU slices per frame - enough for the sound CPU to read all commands */
                null,

                /* video hardware */
                32*8, 32*8, new rectangle( 0*8, 32*8-1, 0*8, 32*8-1 ),
                gfxdecodeinfo,
                sizeof(palette)/3,sizeof(colortable),
                null,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
                null,
                generic_vh_start,
                generic_vh_stop,
                blueprnt_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                blueprnt_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );



        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr blueprnt_rom= new RomLoadPtr(){ public void handler()  
        {
               ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "1m",           0x0000, 0x1000, 0xb20069a6 );
                ROM_LOAD( "1n",           0x1000, 0x1000, 0x4a30302e );
                ROM_LOAD( "1p",           0x2000, 0x1000, 0x6866ca07 );
                ROM_LOAD( "1r",           0x3000, 0x1000, 0x5d3cfac3 );
                ROM_LOAD( "1s",           0x4000, 0x1000, 0xa556cac4 );

                ROM_REGION(0x5000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "c3",           0x0000, 0x1000, 0xac2a61bc );
                ROM_LOAD( "d3",           0x1000, 0x1000, 0x81fe85d7 );
                ROM_LOAD( "d17",          0x2000, 0x1000, 0xa73b6483 );
                ROM_LOAD( "d18",          0x3000, 0x1000, 0x7d622550 );
                ROM_LOAD( "d20",          0x4000, 0x1000, 0x2fcb4f26 );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD("3u",           0x0000, 0x1000, 0xfd38777a );
                ROM_LOAD("3v",           0x2000, 0x1000, 0x33d5bf5b );
                ROM_END();
        }};

        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
        {
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char RAM[]=Machine.memory_region[0];

                /* check if the hi score table has already been initialized */
                if ((memcmp(RAM,0x8100,new char[]{0x00,0x00,0x00},3) == 0) &&
                        (memcmp(RAM,0x813B,new char[]{0x90,0x90,0x90},3) == 0))
                {
                        FILE f;


                        if ((f = osd_fopen(Machine.gamedrv.name,null,OSD_FILETYPE_HIGHSCORE,0)) != null)
                        {
                                osd_fread(f,RAM,0x8100,0x3E);
                                osd_fclose(f);
                        }

                        return 1;
                }
                else return 0;	/* we can't load the hi scores yet */
        }};



        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
	{
                FILE f;

                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char RAM[]=Machine.memory_region[0];


                if ((f = osd_fopen(Machine.gamedrv.name,null,OSD_FILETYPE_HIGHSCORE,1)) != null)
                {
                        osd_fwrite(f,RAM,0x8100,0x3E);
                        osd_fclose(f);
                }

        }};

         public static GameDriver blueprnt_driver = new GameDriver
	(
                "Blue Print",
                "blueprnt",
                "NICOLA SALMORIA\nMIKE BALFOUR",
                machine_driver,

                blueprnt_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                null, palette, colortable,
                ORIENTATION_DEFAULT,

                hiload, hisave
        );
}
