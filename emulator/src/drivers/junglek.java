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
 *  roms are from v0.36 romset
 *  jhunt is jungleh in v0.36 romset
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
import static sndhrdw._8910intfH.*;
import static sndhrdw.generic.*;
import static sndhrdw.junglek.*;
import static vidhrdw.generic.*;
import static vidhrdw.taito.*;
import static mame.memoryH.*;

public class junglek
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new MemoryReadAddress( 0xc400, 0xcfff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new MemoryReadAddress( 0xd408, 0xd408, input_port_0_r ),	/* IN0 */
		new MemoryReadAddress( 0xd409, 0xd409, input_port_1_r ),	/* IN1 */
		new MemoryReadAddress( 0xd40b, 0xd40b, input_port_2_r ),	/* IN2 */
		new MemoryReadAddress( 0xd40c, 0xd40c, input_port_3_r ),	/* COIN */
		new MemoryReadAddress( 0xd40a, 0xd40a, input_port_4_r ),	/* DSW1 */
		new MemoryReadAddress( 0xd40f, 0xd40f, AY8910_read_port_0_r ),	/* DSW2 and DSW3 */
	        new MemoryReadAddress( 0xd404, 0xd404, taito_gfxrom_r ),
		new MemoryReadAddress( 0xd404, 0xd404, MRA_RAM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress writemem[] =
	{
                new MemoryWriteAddress( 0x8000, 0x87ff, MWA_RAM ),
                new MemoryWriteAddress( 0xc400, 0xc7ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0xc800, 0xcbff, taito_videoram2_w, taito_videoram2 ),
                new MemoryWriteAddress( 0xcc00, 0xcfff, taito_videoram3_w, taito_videoram3 ),
                new MemoryWriteAddress( 0xd100, 0xd17f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0xd000, 0xd01f, MWA_RAM, taito_colscrolly1 ),
                new MemoryWriteAddress( 0xd020, 0xd03f, MWA_RAM, taito_colscrolly2 ),
                new MemoryWriteAddress( 0xd040, 0xd05f, MWA_RAM, taito_colscrolly3 ),
                new MemoryWriteAddress( 0xd500, 0xd500, MWA_RAM, taito_scrollx1 ),
                new MemoryWriteAddress( 0xd501, 0xd501, MWA_RAM, taito_scrolly1 ),
                new MemoryWriteAddress( 0xd502, 0xd502, MWA_RAM, taito_scrollx2 ),
                new MemoryWriteAddress( 0xd503, 0xd503, MWA_RAM, taito_scrolly2 ),
                new MemoryWriteAddress( 0xd504, 0xd504, MWA_RAM, taito_scrollx3 ),
                new MemoryWriteAddress( 0xd505, 0xd505, MWA_RAM, taito_scrolly3 ),
                new MemoryWriteAddress( 0xd506, 0xd507, taito_colorbank_w, taito_colorbank ),
                new MemoryWriteAddress( 0xd509, 0xd50a, MWA_RAM, taito_gfxpointer ),
                new MemoryWriteAddress( 0xd50b, 0xd50b, sound_command_w ),
                new MemoryWriteAddress( 0xd50d, 0xd50d, MWA_NOP ),
                new MemoryWriteAddress( 0xd200, 0xd27f, taito_paletteram_w, taito_paletteram ),
                new MemoryWriteAddress( 0x9000, 0xbfff, taito_characterram_w, taito_characterram ),
                new MemoryWriteAddress( 0xd50e, 0xd50e, MWA_NOP ),
                new MemoryWriteAddress( 0xd40e, 0xd40e, AY8910_control_port_0_w ),
                new MemoryWriteAddress( 0xd40f, 0xd40f, AY8910_write_port_0_w ),
                new MemoryWriteAddress( 0xd300, 0xd300, MWA_RAM, taito_video_priority ),
                new MemoryWriteAddress( 0xd600, 0xd600, MWA_RAM, taito_video_enable ),
                new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};



	static MemoryReadAddress sound_readmem[] =
	{
                new MemoryReadAddress( 0x4000, 0x43ff, MRA_RAM ),
                new MemoryReadAddress( 0x5000, 0x5000, sound_command_r ),
                new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress sound_writemem[] =
	{
                new MemoryWriteAddress( 0x4000, 0x43ff, MWA_RAM ),
                new MemoryWriteAddress( 0x4800, 0x4800, AY8910_control_port_1_w ),
                new MemoryWriteAddress( 0x4801, 0x4801, AY8910_write_port_1_w ),
                new MemoryWriteAddress( 0x4802, 0x4802, AY8910_control_port_2_w ),
                new MemoryWriteAddress( 0x4803, 0x4803, AY8910_write_port_2_w ),
                new MemoryWriteAddress( 0x4804, 0x4804, AY8910_control_port_3_w ),
                new MemoryWriteAddress( 0x4805, 0x4805, AY8910_write_port_3_w ),
                new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};



	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_DOWN, OSD_KEY_UP,
					OSD_KEY_CONTROL, 0 , 0, 0 }
		),
		new InputPort(	/* IN1 */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* IN2 */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, OSD_KEY_1, OSD_KEY_2 }
		),
		new InputPort(	/* COIN */
			0xff,
			new int[] { 0, 0, 0, 0, OSD_KEY_3, 0, 0, 0 }
		),
		new InputPort(	/* DSW1 */
			0x7f,
			new int[] { 0, 0, 0, 0, 0, OSD_KEY_F2, 0, 0 }
		),
                new InputPort(	/* DSW2 */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW3 */
			0x7f,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};

static TrakPort trak_ports[] =
{
        new TrakPort(-1)
};


static KEYSet keys[] =
{
        new KEYSet(0, 3, "MOVE UP" ),
        new KEYSet( 0, 0, "MOVE LEFT"  ),
        new KEYSet( 0, 1, "MOVE RIGHT" ),
        new KEYSet( 0, 2, "MOVE DOWN" ),
        new KEYSet( 0, 4, "FIRE" ),
        new KEYSet( -1 )
};

	static DSW dsw[] =
	{
		new DSW( 4, 0x18, "LIVES", new String[] { "6", "5", "4", "3" }, 1 ),
		new DSW( 6, 0x03, "BONUS", new String[] { "30000", "20000", "10000", "NONE" }, 1 ),
		new DSW( 4, 0x03, "FINISH BONUS", new String[] { "TIMER X3", "TIMER X2", "TIMER X1", "NONE" }, 1 ),
		new DSW( 6, 0x40, "DEMO MODE", new String[] { "ON", "OFF" }, 1 ),
		new DSW( 6, 0x20, "YEAR DISPLAY", new String[] { "NO", "YES" }, 1 ),
		new DSW( 4, 0x04, "SW A 3", new String[] { "ON", "OFF" }, 1 ),
		new DSW( 6, 0x04, "SW C 3", new String[] { "ON", "OFF" }, 1 ),
		new DSW( 6, 0x08, "SW C 4", new String[] { "ON", "OFF" }, 1 ),
		new DSW( 6, 0x10, "SW C 5", new String[] { "ON", "OFF" }, 1 ),
		new DSW( -1 )
	};


        static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                256,	/* 256 characters */
                3,	/* 3 bits per pixel */
                new int[]{ 512*8*8, 256*8*8, 0 },	/* the bitplanes are separated */
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                8*8	/* every char takes 8 consecutive bytes */
        );
        static GfxLayout spritelayout = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                64,	/* 64 sprites */
                3,	/* 3 bits per pixel */
               new int[] { 128*16*16, 64*16*16, 0 },	/* the bitplanes are separated */
               new int[] { 7, 6, 5, 4, 3, 2, 1, 0,
                        8*8+7, 8*8+6, 8*8+5, 8*8+4, 8*8+3, 8*8+2, 8*8+1, 8*8+0 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                                16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
                32*8	/* every sprite takes 32 consecutive bytes */
        );


	static GfxDecodeInfo gfxdecodeinfo[] =
	{
                new GfxDecodeInfo(0, 0x9000, charlayout,   0, 16 ),	/* the game dynamically modifies this */
                new GfxDecodeInfo(0, 0x9000, spritelayout, 0,  8 ),	/* the game dynamically modifies this */
                new GfxDecodeInfo(0, 0xa800, charlayout,   0,  8 ),	/* the game dynamically modifies this */
                new GfxDecodeInfo(0, 0xa800, spritelayout, 0,  8 ),	/* the game dynamically modifies this */
		new GfxDecodeInfo( -1 ) /* end of array */
	};





        /* Jungle King doesn't have a color PROM, it uses a RAM to generate colors */
        /* and change them during the game. Here is the list of all the colors is uses. */
        static char color_prom[] =
        {
                /* total: 168 colors (2 bytes per color) */
                0x01,0xFF,	/* transparent black */
                0x01,0xFF,0x01,0xFE,0x01,0xFB,0x01,0xF8,0x01,0xF7,0x01,0xF4,0x01,0xF3,0x01,0xEF,
                0x01,0xEC,0x01,0xEB,0x01,0xE5,0x01,0xE4,0x01,0xE3,0x01,0xDF,0x01,0xD9,0x01,0xD7,
                0x01,0xD6,0x01,0xD1,0x01,0xD0,0x01,0xCF,0x01,0xCE,0x01,0xC8,0x01,0xC7,0x01,0xC6,
                0x01,0xC4,0x01,0xC3,0x01,0xC0,0x01,0xBF,0x01,0xBC,0x01,0xB8,0x01,0xAF,0x01,0x9F,
                0x01,0x9C,0x01,0x98,0x01,0x86,0x01,0x85,0x01,0x81,0x01,0x7F,0x01,0x7C,0x01,0x6F,
                0x01,0x59,0x01,0x52,0x01,0x51,0x01,0x4C,0x01,0x4B,0x01,0x45,0x01,0x44,0x01,0x42,
                0x01,0x39,0x01,0x37,0x01,0x2C,0x01,0x25,0x01,0x22,0x01,0x20,0x01,0x1C,0x01,0x17,
                0x01,0x0F,0x01,0x0B,0x01,0x04,0x00,0xFF,0x00,0xFC,0x00,0xFB,0x00,0xF9,0x00,0xF6,
                0x00,0xF2,0x00,0xF1,0x00,0xF0,0x00,0xEE,0x00,0xE6,0x00,0xE4,0x00,0xE1,0x00,0xD8,
                0x00,0xD7,0x00,0xD5,0x00,0xD2,0x00,0xD1,0x00,0xCF,0x00,0xCB,0x00,0xC7,0x00,0xC3,
                0x00,0xBF,0x00,0xBC,0x00,0xBB,0x00,0xAF,0x00,0xAD,0x00,0xA6,0x00,0xA2,0x00,0xA1,
                0x00,0x9D,0x00,0x9C,0x00,0x9B,0x00,0x98,0x00,0x88,0x00,0x87,0x00,0x81,0x00,0x80,
                0x00,0x7F,0x00,0x7E,0x00,0x7D,0x00,0x7B,0x00,0x79,0x00,0x78,0x00,0x77,0x00,0x75,
                0x00,0x6E,0x00,0x6A,0x00,0x67,0x00,0x64,0x00,0x63,0x00,0x5F,0x00,0x5E,0x00,0x5C,
                0x00,0x5B,0x00,0x5A,0x00,0x57,0x00,0x55,0x00,0x54,0x00,0x53,0x00,0x52,0x00,0x51,
                0x00,0x4F,0x00,0x4D,0x00,0x4C,0x00,0x49,0x00,0x48,0x00,0x47,0x00,0x44,0x00,0x40,
                0x00,0x3F,0x00,0x3D,0x00,0x3A,0x00,0x39,0x00,0x38,0x00,0x37,0x00,0x32,0x00,0x2F,
                0x00,0x2D,0x00,0x2B,0x00,0x2A,0x00,0x27,0x00,0x24,0x00,0x23,0x00,0x1F,0x00,0x1E,
                0x00,0x1D,0x00,0x1C,0x00,0x1B,0x00,0x17,0x00,0x16,0x00,0x15,0x00,0x14,0x00,0x13,
                0x00,0x12,0x00,0x11,0x00,0x10,0x00,0x0F,0x00,0x0D,0x00,0x0B,0x00,0x0A,0x00,0x09,
                0x00,0x07,0x00,0x06,0x00,0x05,0x00,0x04,0x00,0x03,0x00,0x02,0x00,0x00
        };



	static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz? */
				0,
				readmem, writemem, null, null,
				interrupt, 1
			),
                        new MachineCPU(
			CPU_Z80 | CPU_AUDIO_CPU,
			3000000,	/* 3 Mhz ??? */
			3,	/* memory region #3 */
			sound_readmem,sound_writemem,null, null,
			junglek_sh_interrupt,5
                        )

		},
		60,
		null,

		/* video hardware */
		32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
		gfxdecodeinfo,
		168, 16*8,
		taito_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
		null,
		junglek_vh_start,
		taito_vh_stop,
		taito_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		junglek_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);



	/***************************************************************************

	  Game driver(s)

	***************************************************************************/
        static RomLoadPtr junglek_rom= new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x12000);	/* 64k for code */
                ROM_LOAD( "kn21-1.bin",   0x00000, 0x1000, 0x45f55d30 );
                ROM_LOAD( "kn22-1.bin",   0x01000, 0x1000, 0x07cc9a21 );
                ROM_LOAD( "kn43.bin",     0x02000, 0x1000, 0xa20e5a48 );
                ROM_LOAD( "kn24.bin",     0x03000, 0x1000, 0x19ea7f83 );
                ROM_LOAD( "kn25.bin",     0x04000, 0x1000, 0x844365ea );
                ROM_LOAD( "kn46.bin",     0x05000, 0x1000, 0x27a95fd5 );
                ROM_LOAD( "kn47.bin",     0x06000, 0x1000, 0x5c3199e0 );
                ROM_LOAD( "kn28.bin",     0x07000, 0x1000, 0x194a2d09 );
                /* 10000-10fff space for another banked ROM (not used) */
                ROM_LOAD( "kn60.bin",     0x11000, 0x1000, 0x1a9c0a26 ); /* banked at 7000 */
            
                ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "kn55.bin",     0x0000, 0x1000, 0x70aef58f );	/* not needed - could be removed */

                ROM_REGION(0x8000);	/* graphic ROMs */
                ROM_LOAD( "kn29.bin",     0x0000, 0x1000, 0x8f83c290 );
                ROM_LOAD( "kn30.bin",     0x1000, 0x1000, 0x89fd19f1 );
                ROM_LOAD( "kn51.bin",     0x2000, 0x1000, 0x70e8fc12 );
                ROM_LOAD( "kn52.bin",     0x3000, 0x1000, 0xbcbac1a3 );
                ROM_LOAD( "kn53.bin",     0x4000, 0x1000, 0xb946c87d );
                ROM_LOAD( "kn34.bin",     0x5000, 0x1000, 0x320db2e1 );
                ROM_LOAD( "kn55.bin",     0x6000, 0x1000, 0x70aef58f );
                ROM_LOAD( "kn56.bin",     0x7000, 0x1000, 0x932eb667 );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "kn37.bin",     0x0000, 0x1000, 0xdee7f5d4 );
                ROM_LOAD( "kn38.bin",     0x1000, 0x1000, 0xbffd3d21 );
                ROM_LOAD( "kn59-1.bin",   0x2000, 0x1000, 0xcee485fc );
                ROM_END();
        }};

        static RomLoadPtr jungleh_rom= new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x12000);	/* 64k for code */
                ROM_LOAD( "kn41a",        0x00000, 0x1000, 0x6bf118d8 );
                ROM_LOAD( "kn42.bin",     0x01000, 0x1000, 0xbade53af );
                ROM_LOAD( "kn43.bin",     0x02000, 0x1000, 0xa20e5a48 );
                ROM_LOAD( "kn44.bin",     0x03000, 0x1000, 0x44c770d3 );
                ROM_LOAD( "kn45.bin",     0x04000, 0x1000, 0xf60a3d06 );
                ROM_LOAD( "kn46a",        0x05000, 0x1000, 0xac89c155 );
                ROM_LOAD( "kn47.bin",     0x06000, 0x1000, 0x5c3199e0 );
                ROM_LOAD( "kn48a",        0x07000, 0x1000, 0xef80e931 );
                /* 10000-10fff space for another banked ROM (not used) */
                ROM_LOAD( "kn60.bin",     0x11000, 0x1000, 0x1a9c0a26 );	/* banked at 7000 */

                ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "kn55.bin",   0x0000, 0x1000, 0x70aef58f );	/* not needed - could be removed */

               ROM_REGION(0x8000);	/* graphic ROMs */
                    ROM_LOAD( "kn49a",        0x0000, 0x1000, 0xb139e792 );
                    ROM_LOAD( "kn50a",        0x1000, 0x1000, 0x1046019f );
                    ROM_LOAD( "kn51a",        0x2000, 0x1000, 0xda50c8a4 );
                    ROM_LOAD( "kn52a",        0x3000, 0x1000, 0x0444f06c );
                    ROM_LOAD( "kn53a",        0x4000, 0x1000, 0x6a17803e );
                    ROM_LOAD( "kn54a",        0x5000, 0x1000, 0xd41428c7 );
                    ROM_LOAD( "kn55.bin",     0x6000, 0x1000, 0x70aef58f );
                    ROM_LOAD( "kn56a",        0x7000, 0x1000, 0x679c1101 );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "kn57-1.bin",   0x0000, 0x1000, 0x62f6763a );
                ROM_LOAD( "kn58-1.bin",   0x1000, 0x1000, 0x9ef46c7f );
                ROM_LOAD( "kn59-1.bin",   0x2000, 0x1000, 0xcee485fc );
                ROM_END();
        }};

        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
        {
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char RAM[]=Machine.memory_region[0];

                /* check if the hi score table has already been initialized */
    /*TOFIX                     if (memcmp(RAM,0x816B,new char[]{0x00,0x50,0x00},3) == 0)
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x816B,1,3,f);
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


      /*TOFIX                   if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x816B,1,3,f);
                        fclose(f);
                }*/

        }};

	public static GameDriver junglek_driver = new GameDriver
	(
                "Jungle King",
		"junglek",
                "NICOLA SALMORIA\nMIKE BALFOUR",
		machine_driver,

		junglek_rom,
		null, null,
		null,

		input_ports,null, trak_ports, dsw, keys,

		color_prom,null,null,
		ORIENTATION_DEFAULT,

		hiload, hisave
	);



	public static GameDriver jungleh_driver = new GameDriver
	(
                "Jungle Hunt",
		"jungleh",
                "NICOLA SALMORIA\nMIKE BALFOUR",
		machine_driver,

		jungleh_rom,
		null, null,
		null,

		input_ports,null, trak_ports, dsw, keys,

		color_prom, null, null,
		ORIENTATION_DEFAULT,

		hiload, hisave
	);
}
