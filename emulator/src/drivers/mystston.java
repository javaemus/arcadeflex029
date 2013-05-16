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
 * roms are from v0.36 romset
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
import static vidhrdw.mystston.*;
import static machine.mystston.*;
import static mame.memoryH.*;
public class mystston {
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x2030, 0x2030, input_port_3_r ),
                new MemoryReadAddress( 0x0000, 0x1fff, MRA_RAM ),
                new MemoryReadAddress( 0x4000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( 0x2000, 0x2000, input_port_0_r ),
                new MemoryReadAddress( 0x2010, 0x2010, input_port_1_r ),
                new MemoryReadAddress( 0x2020, 0x2020, input_port_2_r ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x0fff, MWA_RAM ),
                new MemoryWriteAddress( 0x1000, 0x13ff, MWA_RAM, mystston_videoram2, mystston_videoram2_size ),
                new MemoryWriteAddress( 0x1400, 0x17ff, MWA_RAM, mystston_colorram2 ),
                new MemoryWriteAddress( 0x1800, 0x19ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x1a00, 0x1bff, colorram_w, colorram ),
                new MemoryWriteAddress( 0x1c00, 0x1fff, MWA_RAM ),
                new MemoryWriteAddress( 0x2020, 0x2020, MWA_RAM, mystston_scroll ),
                new MemoryWriteAddress( 0x2060, 0x2077, mystston_paletteram_w, mystston_paletteram ),
                new MemoryWriteAddress( 0x4000, 0xffff, MWA_ROM ),
                new MemoryWriteAddress( 0x0780, 0x07df, MWA_RAM, spriteram, spriteram_size ),	/* handled by the MWA_RAM above, here */
                                                                                                        /* to initialize the pointer */
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static InputPort input_ports[] =
        {
                new InputPort(	/* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_UP, OSD_KEY_DOWN,
                                        OSD_KEY_ALT, OSD_KEY_CONTROL, 0, OSD_KEY_3 }
                ),
		new InputPort(	/* IN1 */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, OSD_KEY_1, OSD_KEY_2 }
                ),
		new InputPort(	/* DSW1 */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
               ),
		new InputPort(	/* DSW2 */
                        0x3f,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, IPB_VBLANK }
                ),
		new InputPort( -1 )	/* end of table */
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
                 new KEYSet( 0, 5, "FIRE" ),
                 new KEYSet( 0, 4, "KICK" ),
                 new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                new DSW( 2, 0x01, "LIVES", new String[]{ "5", "3" }, 1 ),
                new DSW( 2, 0x02, "SW2", new String[]{ "0", "1" } ),
                new DSW( 2, 0x04, "SW3", new String[]{ "0", "1" } ),
                new DSW( 2, 0x08, "SW4", new String[]{ "0", "1" } ),
                new DSW( 2, 0x10, "SW5", new String[]{ "0", "1" } ),
                new DSW( 2, 0x20, "SW6", new String[]{ "0", "1" } ),
                new DSW( 2, 0x40, "SW7", new String[]{ "0", "1" } ),
                new DSW( 2, 0x80, "SW8", new String[]{ "0", "1" } ),
                new DSW( -1 )
        };



        static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                1024,	/* 1024 characters */
                3,	/* 3 bits per pixel */
                new int[]{ 2*1024*8*8, 1024*8*8, 0 },	/* the bitplanes are separated */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },
                8*8	/* every char takes 8 consecutive bytes */
        );
        static GfxLayout spritelayout = new GfxLayout
	(
                16,16,  /* 16*16 sprites */
                256,    /* 256 sprites */
                3,	/* 3 bits per pixel */
                new int[]{ 2*256*16*16, 256*16*16, 0 },	/* the bitplanes are separated */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                                8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0,
                                16*8+7, 16*8+6, 16*8+5, 16*8+4, 16*8+3, 16*8+2, 16*8+1, 16*8+0 },
                32*8	/* every sprite takes 16 consecutive bytes */
        );
        static GfxLayout tilelayout = new GfxLayout
	(
                16,16,  /* 16*16 tiles */
                256,    /* 256 tiles */
                3,	/* 3 bits per pixel */
                new int[]{ 2*512*16*16, 512*16*16, 0 },	/* the bitplanes are separated */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                                8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0,
                                16*8+7, 16*8+6, 16*8+5, 16*8+4, 16*8+3, 16*8+2, 16*8+1, 16*8+0 },
                32*8	/* every tile takes 16 consecutive bytes */
        );



        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x00000, charlayout,   1*8, 1 ),
                new GfxDecodeInfo( 1, 0x06000, charlayout,   1*8, 1 ),
                new GfxDecodeInfo( 1, 0x00000, spritelayout,   0, 1 ),
                new GfxDecodeInfo( 1, 0x06000, spritelayout,   0, 1 ),
                new GfxDecodeInfo( 1, 0x0c000, tilelayout,   2*8, 1 ),
                new GfxDecodeInfo( 1, 0x0e000, tilelayout,   2*8, 1 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        public static MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_M6502,
                                1500000,	/* 1.5 Mhz ???? */
                                0,
                                readmem,writemem,null,null,
                                mystston_interrupt,1
                        ),
                },
                60,
                null,

                /* video hardware */
                32*8, 32*8, new rectangle( 1*8, 31*8-1, 0*8, 32*8-1 ),
                gfxdecodeinfo,
                1+24, 3*8,
                mystston_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
                null,
                mystston_vh_start,
                mystston_vh_stop,
                mystston_vh_screenrefresh,

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
        static RomLoadPtr mystston_rom= new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "ms0",          0x4000, 0x2000, 0x6dacc05f );
                ROM_LOAD( "ms1",          0x6000, 0x2000, 0xa3546df7 );
                ROM_LOAD( "ms2",          0x8000, 0x2000, 0x43bc6182 );
                ROM_LOAD( "ms3",          0xa000, 0x2000, 0x9322222b );
                ROM_LOAD( "ms4",          0xc000, 0x2000, 0x47cefe9b );
                ROM_LOAD( "ms5",          0xe000, 0x2000, 0xb37ae12b );

                ROM_REGION(0x18000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "ms6",  0x00000, 0x2000, 0x85c83806 );
                ROM_LOAD( "ms7",  0x02000, 0x2000, 0xd025f84d );
                ROM_LOAD( "ms8",  0x04000, 0x2000, 0x53765d89 );
                ROM_LOAD( "ms9",  0x06000, 0x2000, 0xb146c6ab );
                ROM_LOAD( "ms10", 0x08000, 0x2000, 0xd85015b5 );
                ROM_LOAD( "ms11", 0x0a000, 0x2000, 0x919ee527 );
                ROM_LOAD( "ms12", 0x0c000, 0x2000, 0x72d8331d );
                ROM_LOAD( "ms13", 0x0e000, 0x2000, 0x845a1f9b );
                ROM_LOAD( "ms14", 0x10000, 0x2000, 0x822874b0 );
                ROM_LOAD( "ms15", 0x12000, 0x2000, 0x4594e53c );
                ROM_LOAD( "ms16", 0x14000, 0x2000, 0x2f470b0f );
                ROM_LOAD( "ms17", 0x16000, 0x2000, 0x38966d1b );
                ROM_END();
        }};

        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
	{
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];

                /* check if the hi score table has already been initialized */
    /*TOFIX                     if ((memcmp(RAM,0x0308,new char[]{0x00,0x00,0x21},3) == 0) &&
                        (memcmp(RAM,0x033C,new char[]{0x0C,0x1D,0x0C},3) == 0))
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x0308,1,11*5,f);
                                fclose(f);
                        }

                        return 1;
                }
                else */ return 0;	/* we can't load the hi scores yet */
        } };


	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
	{
                FILE f;

                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char[] RAM = Machine.memory_region[0];


             /*TOFIX            if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x0308,1,11*5,f);
                        fclose(f);
                }*/

        }};             
        public static GameDriver  mystston_driver =new GameDriver
        (
                "Mysterious Stones",
                "mystston",
                "NICOLA SALMORIA\nMIKE BALFOUR",
                machine_driver,

                mystston_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                null, null, null,
               ORIENTATION_DEFAULT,

                null, null
        );    
}
