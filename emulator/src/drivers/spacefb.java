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
 *  NOTES: romsets are from v0.36 roms 
 */
package drivers;

import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static sndhrdw._8910intf.*;
import static vidhrdw.generic.*;
import static machine.spacefb.*;
import static vidhrdw.spacefb.*;
import static mame.inptport.*;
import static mame.memoryH.*;

public class spacefb {
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0xC000, 0xC7FF, MRA_RAM ),
                new MemoryReadAddress( 0x8000, 0x83FF, MRA_RAM ),
                new MemoryReadAddress( 0x0000, 0x3FFF, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0xC000, 0xC7FF, MWA_RAM ),
                new MemoryWriteAddress( 0x8000, 0x83FF, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x0000, 0x3FFF, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };

        static InputPort input_ports[] =
        {
                new InputPort(	/* DSW1 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort( /* P1 and P2 IO */
                        0x00,
                        new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, 0,0,OSD_KEY_ALT, 0,0, OSD_KEY_CONTROL}
                ),
		new InputPort(
                        0x00,
                        new int[]{ 0,0,OSD_KEY_1, OSD_KEY_2, 0,0,0,OSD_KEY_3}
                ),
		new InputPort( -1 )	/* end of table */
        };

        static IOReadPort readport[] =
        {
                 new IOReadPort( 0x00, 0x00, input_port_1_r ),
                 new IOReadPort( 0x02, 0x02, input_port_2_r ),
                 new IOReadPort( 0x03, 0x03, input_port_0_r ),
                 new IOReadPort( -1 )	/* end of table */
        };

        static IOWritePort writeport[] =
        {
                 new IOWritePort( 0x00, 0x00, spacefb_port_0_w ),
                 new IOWritePort( 0x01, 0x01, spacefb_port_1_w ),
                 new IOWritePort( 0x02, 0x02, spacefb_port_2_w ),
                 new IOWritePort( 0x03, 0x03, spacefb_port_3_w ),
                 new IOWritePort( -1 )	/* end of table */
        };

        static  TrakPort trak_ports[] =
        {
             new TrakPort(-1) 
        };


        static KEYSet keys[] =
        {
                new KEYSet( 1, 1, "MOVE LEFT"  ),
                new KEYSet( 1, 0, "MOVE RIGHT" ),
                new KEYSet( 1, 7, "FIRE"       ),
                new KEYSet( 1, 4, "ESCAPE"     ),
                new KEYSet( -1 )
        };

        static DSW dsw[] =
        {
                new DSW( 0, 0x03, "SHIPS", new String[]{ "3", "4", "5", "6" } ),
                new DSW( 0, 0x0C, "CREDITS", new String[]{ "1 COIN 1 CREDIT","2 COIN 1 CREDIT","1 COIN 3 CREDITS","1 COIN 2 CREDITS" } ),
                new DSW( 0, 0x10, "EXTRA SHIP", new String[]{ "AT 5000 PTS","AT 8000 PTS" } ),
                new DSW( 0, 0x20, "CABINET", new String[]{ "COCKTAIL" ,"UPRIGHT", } ),
                new DSW( -1 )
        };

        static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                256,	/* 256 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 256*8*8 },	/* the two bitplanes are separated */
                new int[]{ 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },
                8*8	/* every char takes 8 consecutive bytes */
        );
        /*
         * The bullests are stored in a 256x4bit PROM but the .bin file is
         * 256*8bit
         */
        static GfxLayout bulletlayout = new GfxLayout
	(
            4,8,	/* 4*4 characters */
            256/4,	/* 64 characters */
            1,	/* 1 bits per pixel */
            new int[]{ 0 },	/* the two bitplanes are separated */
            new int[]{ 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
            new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },
            4*8	/* every char takes 4 consecutive bytes */
        );

        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, charlayout,   0, 8 ),
                new GfxDecodeInfo( 1, 0x0000, bulletlayout, 0, 8 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };


        static char colorprom[] =
        {
                0x00,0x17,0x27,0xD0,0x00,0xA4,0xC0,0x16,0x00,0x07,0xC7,0x37,0x00,0x3F,0xD8,0x07,
                0x00,0x3B,0xC0,0x16,0x00,0xDB,0xC0,0xC7,0x00,0x07,0xC7,0x37,0x00,0x3F,0xD8,0x07
        };

        public static MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                4000000,	/* 4 Mhz? */
                                0,
                                readmem,writemem,readport,writeport,
                                spacefb_interrupt,2 /* two int's per frame */
                        )
                },
                60,
                null,

                /* video hardware */
                32*8, 32*8, new rectangle( 2*8, 32*8-1, 0*8, 32*8-1 ),
                gfxdecodeinfo,

                32,32,
                spacefb_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
                null,
                generic_vh_start,
                generic_vh_stop,
                spacefb_vh_screenrefresh,

                /* sound hardware */
                 null,
                 null,
                 null,
                 null,
                 null
        );



        static RomLoadPtr spacefb_rom= new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "5e.cpu",       0x0000, 0x0800, 0x2d406678 );         /* Code */
                ROM_LOAD( "5f.cpu",       0x0800, 0x0800, 0x89f0c34a );
                ROM_LOAD( "5h.cpu",       0x1000, 0x0800, 0xc4bcac3e );
                ROM_LOAD( "5i.cpu",       0x1800, 0x0800, 0x61c00a65 );
                ROM_LOAD( "5j.cpu",       0x2000, 0x0800, 0x598420b9 );
                ROM_LOAD( "5k.cpu",       0x2800, 0x0800, 0x1713300c );
                ROM_LOAD( "5m.cpu",       0x3000, 0x0800, 0x6286f534 );
                ROM_LOAD( "5n.cpu",       0x3800, 0x0800, 0x1c9f91ee );

                ROM_REGION(0x1100);	/* temporary space for graphics (disposed after conversion) */
                //ROM_LOAD( "6k.vid", 0x0000, 0x0800, 0x5076d18e );
                //ROM_LOAD( "5k.vid", 0x0800, 0x0800, 0xe945e879 );
                //ROM_LOAD( "4i.vid", 0x1000, 0x0100, 0x75c90c07 );
                ROM_LOAD( "5k.vid",       0x0000, 0x0800, 0x236e1ff7 );
                ROM_LOAD( "6k.vid",       0x0800, 0x0800, 0xbf901a4e );
                ROM_LOAD( "4i.vid",       0x1000, 0x0100, 0x528e8533 );
                ROM_END();             
        }};



        public static GameDriver spacefb_driver  = new GameDriver
	(
                "Space Firebird",
                "spacefb",
                "CHRIS HARDY\nANDY CLARK\nPAUL JOHNSON",
                machine_driver,

                spacefb_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                colorprom, null, null,
                ORIENTATION_DEFAULT,

                null, null
        );
   
}
