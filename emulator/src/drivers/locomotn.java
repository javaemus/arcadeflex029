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
 *  NOTES: romsets are from v0.36 roms
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
import static sndhrdw._8910intf.*;
import static sndhrdw.generic.*;
import static sndhrdw.pooyan.*;
import static vidhrdw.generic.*;
import static vidhrdw.locomotn.*;
import static mame.memoryH.*;

public class locomotn {
        static  MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x9800, 0x9fff, MRA_RAM ),
                new MemoryReadAddress( 0x0000, 0x5fff, MRA_ROM ),
                new MemoryReadAddress( 0xa000, 0xa000, input_port_0_r ),	/* IN0 */
                new MemoryReadAddress( 0xa080, 0xa080, input_port_1_r ),	/* IN1 */
                new MemoryReadAddress( 0xa100, 0xa100, input_port_2_r ),	/* IN2 */
                new MemoryReadAddress( 0xa180, 0xa180, input_port_3_r ),	/* DSW */
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x9800, 0x9fff, MWA_RAM ),
                new MemoryWriteAddress( 0x8040, 0x87ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x8840, 0x8fff, colorram_w, colorram ),
                new MemoryWriteAddress( 0x8800, 0x883f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x8000, 0x803f, MWA_RAM, spriteram_2 ),
                new MemoryWriteAddress( 0xa080, 0xa080, MWA_NOP ),
                new MemoryWriteAddress( 0xa181, 0xa181, interrupt_enable_w ),
                new MemoryWriteAddress( 0x0000, 0x5fff, MWA_ROM ),
                new MemoryWriteAddress( 0xa100, 0xa100, sound_command_w ),
                new MemoryWriteAddress( 0xa180, 0xa180, MWA_NOP ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static MemoryReadAddress sound_readmem[] =
        {
                new MemoryReadAddress( 0x2000, 0x23ff, MRA_RAM ),
                new MemoryReadAddress( 0x4000, 0x4000, AY8910_read_port_0_r ),
                new MemoryReadAddress( 0x6000, 0x6000, AY8910_read_port_1_r ),
                new MemoryReadAddress( 0x0000, 0x0fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress sound_writemem[] =
        {
                 new MemoryWriteAddress( 0x2000, 0x23ff, MWA_RAM ),
                 new MemoryWriteAddress( 0x5000, 0x5000, AY8910_control_port_0_w ),
                 new MemoryWriteAddress( 0x4000, 0x4000, AY8910_write_port_0_w ),
                 new MemoryWriteAddress( 0x7000, 0x7000, AY8910_control_port_1_w ),
                 new MemoryWriteAddress( 0x6000, 0x6000, AY8910_write_port_1_w ),
                 new MemoryWriteAddress( 0x0000, 0x0fff, MWA_ROM ),
                 new MemoryWriteAddress( -1 )	/* end of table */
        };



        static InputPort input_ports[] =
        {
                new InputPort(	/* IN0 */
                        0xff,
                        new int[]{ 0, 0, OSD_KEY_3, OSD_KEY_CONTROL, OSD_KEY_LEFT, OSD_KEY_RIGHT, 0, 0 }
                ),
		new InputPort(	/* IN1 */
                        0xff,
                        new int[]{ OSD_KEY_UP, 0, 0, 0, 0, 0, OSD_KEY_2, OSD_KEY_1 }
                ),
		new InputPort(	/* IN2 */
                        0xf6,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, OSD_KEY_DOWN }
                        
                ),
		new InputPort(	/* DSW */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                        
                ),
		new InputPort( -1 )	/* end of table */
        };

        static TrakPort[] trak_ports =
        {
           new TrakPort(-1)
        };


        static KEYSet keys[] =
        {
                new KEYSet( 1, 0, "MOVE UP" ),
                new KEYSet( 0, 4, "MOVE LEFT" ),
                new KEYSet( 0, 5, "MOVE RIGHT" ),
                new KEYSet( 2, 7, "MOVE DOWN" ),
                new KEYSet( 0, 3, "ACCELERATE" ),
                new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                new DSW( 2, 0x30, "LIVES", new String[]{ "255?", "5", "4", "3" }, 1 ),
                new DSW( 2, 0x01, "DEMO SOUNDS", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 2, 0x02, "SWA1", new String[]{ "0", "1" } ),
                new DSW( 2, 0x04, "SWA2", new String[]{ "0", "1" } ),
                new DSW( 2, 0x40, "SWA6", new String[]{ "0", "1" } ),
                new DSW( 3, 0x10, "SWB4", new String[]{ "0", "1" } ),
                new DSW( 3, 0x20, "SWB5", new String[]{ "0", "1" } ),
                new DSW( 3, 0x40, "SWB6", new String[]{ "0", "1" } ),
                new DSW( 3, 0x80, "SWB7",new String[] { "0", "1" } ),
                new DSW( -1 )
        };



        public static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                512,	/* 512 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 4, 0 },
                new int[]{ 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
                new int[]{ 8*8+0,8*8+1,8*8+2,8*8+3, 0, 1, 2, 3 },
                16*8	/* every char takes 16 consecutive bytes */
        );
        public static GfxLayout spritelayout = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                64,	/* 64 sprites */
                2,	/* 2 bits per pixel */
                new int[]{ 4, 0 },
                new int[]{ 39*8, 38*8, 37*8, 36*8, 35*8, 34*8, 33*8, 32*8,
                                7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
                new int[]{ 8*8, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3,
                                24*8+0, 24*8+1, 24*8+2, 24*8+3, 16*8+0, 16*8+1, 16*8+2, 16*8+3 },
                64*8	/* every sprite takes 64 consecutive bytes */
        );



        public static GfxDecodeInfo gfxdecodeinfo[] =
	{
                new GfxDecodeInfo( 1, 0x0000, charlayout,   0, 8 ),
                new GfxDecodeInfo( 1, 0x1000, spritelayout, 0, 8 ),
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
                0,13,15,17
        };



        public static MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                3072000,	/* 3.072 Mhz (?) */
                                0,
                                readmem,writemem,null,null,
                                nmi_interrupt,1
                        ),
                        new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                3072000,	/* 3.072 Mhz (?) */
                                2,	/* memory region #2 */
                                sound_readmem,sound_writemem,null,null,
                                pooyan_sh_interrupt,10
                        )
                },
                60,
                null,

                /* video hardware */
                28*8, 36*8, new rectangle( 0*8, 28*8-1, 0*8, 36*8-1 ),
                gfxdecodeinfo,
                sizeof(palette)/3,sizeof(colortable),
                null,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
                null,
                generic_vh_start,
                generic_vh_stop,
                locomotn_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                pooyan_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );



        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr locomotn_rom = new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "1a.cpu",       0x0000, 0x1000, 0xb43e689a );
                ROM_LOAD( "2a.cpu",       0x1000, 0x1000, 0x529c823d );
                ROM_LOAD( "3.cpu",        0x2000, 0x1000, 0xc9dbfbd1 );
                ROM_LOAD( "4.cpu",        0x3000, 0x1000, 0xcaf6431c );
                ROM_LOAD( "5.cpu",        0x4000, 0x1000, 0x64cf8dd6 );

                ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "5l_c1.bin",    0x0000, 0x1000, 0x5732eda9 );
	        ROM_LOAD( "c2.cpu",       0x1000, 0x1000, 0xc3035300 );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD("1b_s1.bin",    0x0000, 0x1000, 0xa1105714 );                       
                ROM_END();
        }};

        static int ROMloaded=0;
        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
        {       
	/* get RAM pointer (this game is multiCPU, we can't assume the global */
	/* RAM pointer is pointing to the right place) */
	char []RAM = Machine.memory_region[0];
	

	/* read the top score into ROM to fix display at top of screen */
	/* (Does anybody have a cleaner way to do this?) */

	if (ROMloaded==0)
	{
		FILE f;

		if ((f = fopen(name,"rb")) != null)
		{
                        fread(RAM,0x0931,1,3,f);
			fclose(f);
		}
		ROMloaded=1;
	}

	/* check if the hi score table has already been initialized */
        if ((memcmp(RAM,0x9F00,RAM,0x0931,3) == 0) &&
		(memcmp(RAM,0x9F75,new char[]{0x3E,0x3E,0x3E},3) == 0))
	{
		FILE f;


		if ((f = fopen(name,"rb")) != null)
		{
                        fread(RAM,0x9F00,1,12*10,f);
			fclose(f);
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
            char []RAM = Machine.memory_region[0];


            if ((f = fopen(name,"wb")) != null)
            {
                    fwrite(RAM,0x9F00,1,12*10,f);
                    fclose(f);
            }

        }};
        public static GameDriver locomotn_driver = new GameDriver
       (
                "Loco-Motion",
                "locomotn",
                "NICOLA SALMORIA\nLAWNMOWER MAN\nMIKE BALFOUR",
                machine_driver,

                locomotn_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                null, palette, colortable,
                ORIENTATION_DEFAULT,

                hiload, hisave
        );    
}
