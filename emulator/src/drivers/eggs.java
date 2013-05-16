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
import static vidhrdw.eggs.*;
import static mame.memoryH.*;

public class eggs 
{
        static int pop(int offs)
        {
                int res;


                res = readinputport(2);

                return res;
        }

        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x2000, 0x2000, input_port_2_r ),
                new MemoryReadAddress( 0x0000, 0x07ff, MRA_RAM ),
                new MemoryReadAddress( 0x1000, 0x13ff, MRA_RAM ),
                new MemoryReadAddress( 0x3000, 0x7fff, MRA_ROM ),
                new MemoryReadAddress( 0xf000, 0xffff, MRA_ROM ),	/* reset/interrupt vectors */
                new MemoryReadAddress( 0x2002, 0x2002, input_port_0_r ),
                new MemoryReadAddress( 0x2003, 0x2003, input_port_1_r ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x07ff, MWA_RAM ),
                new MemoryWriteAddress( 0x1000, 0x13ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x1800, 0x181f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x1800, 0x1bff, eggs_mirrorvideoram_w ),
                new MemoryWriteAddress( 0x3000, 0x7fff, MWA_ROM ),
                new MemoryWriteAddress( 0xf000, 0xffff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static InputPort input_ports[] =
        {
                new InputPort(	/* IN0 */
                        0xff,
                        new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_UP, OSD_KEY_DOWN,
                                        OSD_KEY_CONTROL, 0, 0, OSD_KEY_3 }
                ),
		new InputPort(	/* IN1 */
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, OSD_KEY_1, OSD_KEY_2 }
                ),
		new InputPort(
                        0xff,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, IPB_VBLANK }
                ),
		new InputPort( -1 )	/* end of table */
        };

        static TrakPort trak_ports[] =
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



        public static GfxLayout charlayout= new GfxLayout
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
                32*8	/* every sprite takes 32 consecutive bytes */
        );



        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, charlayout,   0, 1 ),
                new GfxDecodeInfo( 1, 0x0000, spritelayout, 0, 1 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        static char palette[] =
        {
                0x00,0x00,0x00,   /* black      */
                0x94,0x00,0xd8,   /* darkpurple */
                0xd8,0x00,0x00,   /* darkred    */
                0xf8,0x64,0xd8,   /* pink       */
                0x00,0xd8,0x00,   /* darkgreen  */
                0x00,0xf8,0xd8,   /* darkcyan   */
                0xd8,0xd8,0x94,   /* darkyellow */
                0xd8,0xf8,0xd8,   /* darkwhite  */
                0xf8,0x94,0x44,   /* orange     */
                0x00,0x00,0xd8,   /* blue   */
                0xf8,0x00,0x00,   /* red    */
                0xff,0x00,0xff,   /* purple */
                0x00,0xf8,0x00,   /* green  */
                0x00,0xff,0xff,   /* cyan   */
                0xf8,0xf8,0x00,   /* yellow */
                0xff,0xff,0xff    /* white  */
        };

        
        static final int black=0;
        static final int darkpurple=1;
        static final int darkred=2;
        static final int pink=3;
        static final int darkgreen=4;
        static final int darkcyan=5;
        static final int darkyellow=6;
        static final int darkwhite=7;
        static final int orange=8;
        static final int blue=9;
        static final int red=10;
        static final int purple=11;
        static final int green=12;
        static final int cyan=13;
        static final int yellow=14;
        static final int white=15;
        

        static char colortable[] =
        {
                black, darkred,   blue,       darkyellow, yellow, green,     darkpurple, orange,
                black, darkgreen, darkred,    yellow,	   green, darkred,   darkgreen,  yellow,
                black, yellow,    darkgreen,  red,	         red, green,     orange,     yellow,
                black, darkwhite, red,        pink,        green, darkcyan,  red,        darkwhite
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
                                interrupt,1
                        )
                },
                60,
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
                eggs_vh_screenrefresh,

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
        static RomLoadPtr eggs_rom = new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);	/* 64k for code */
            
                ROM_LOAD( "e14.bin",      0x3000, 0x1000, 0x4e216f9d );
                ROM_LOAD( "d14.bin",      0x4000, 0x1000, 0x4edb267f );
                ROM_LOAD( "c14.bin",      0x5000, 0x1000, 0x15a5c48c );
                ROM_LOAD( "b14.bin",      0x6000, 0x1000, 0x5c11c00e );
                ROM_LOAD( "a14.bin",      0x7000, 0x1000, 0x953faf07 );
                ROM_RELOAD(          0xf000, 0x1000 );	/* for reset/interrupt vectors */

                ROM_REGION(0x6000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "g12.bin",  0x0000, 0x1000, 0x679f8af7 );
                ROM_LOAD( "g10.bin",  0x1000, 0x1000, 0x5b58d3b5 );
                ROM_LOAD( "h12.bin",  0x2000, 0x1000, 0x9562836d );
                ROM_LOAD( "h10.bin",  0x3000, 0x1000, 0x3cfb3a8e );
                ROM_LOAD( "j12.bin",  0x4000, 0x1000, 0xce4a2e46 );
                ROM_LOAD( "j10.bin",  0x5000, 0x1000, 0x8786e8c4 );
                             
                 ROM_END();
        }};
        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
        {
                /* check if the hi score table has already been initialized */
                if (	(memcmp(RAM,0x0400,new char[]{0x17,0x25,0x19},3)==0) &&
                        (memcmp(RAM,0x041B,new char[]{0x00,0x47,0x00},3) == 0))
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x0400,1,0x1E,f);
                                /* Fix hi score at top */
                                memcpy(RAM,0x0015,RAM,0x0403,3);
                                fclose(f);
                        }

                        return 1;
                }
                else return 0;	/* we can't load the hi scores yet */
        }};


        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
	{
                FILE f;


                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x0400,1,0x1E,f);
                        fclose(f);
                }

        }};
        public static GameDriver eggs_driver = new GameDriver
	(
                "Eggs",
                "eggs",
                "NICOLA SALMORIA\nMIKE BALFOUR",
                machine_driver,

                eggs_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                null, palette, colortable,
                ORIENTATION_DEFAULT,

                hiload, hisave
        );
   
}
