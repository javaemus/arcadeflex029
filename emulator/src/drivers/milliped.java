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
 *
 */


package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static vidhrdw.milliped.*;
import static machine.milliped.*;
import static sndhrdw.pokeyintf.*;
import static machine.centiped.*;
import static mame.inptport.*;
import static mame.memoryH.*;

public class milliped {
    
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x0200, MRA_RAM ),
                new MemoryReadAddress( 0x1000, 0x13ff, MRA_RAM ),
                new MemoryReadAddress( 0x4000, 0x7fff, MRA_ROM ),
                new MemoryReadAddress( 0xf000, 0xffff, MRA_ROM ),	/* for the reset / interrupt vectors */
        /*	{ 0x2000, 0x2000, input_port_0_r },
                { 0x2001, 0x2001, input_port_1_r }, */ /* Replaced 11-JUL-97 JB */
                new MemoryReadAddress( 0x2000, 0x2001, milliped_IN_r ), /* Added 11-JUL-97 JB */
                new MemoryReadAddress( 0x2010, 0x2010, input_port_2_r ),
                new MemoryReadAddress( 0x2011, 0x2011, input_port_3_r ),
                new MemoryReadAddress( 0x0408, 0x0408, input_port_4_r ),
                new MemoryReadAddress( 0x0808, 0x0808, input_port_5_r ),
                new MemoryReadAddress( 0x400, 0x40f, pokey1_r ),
                new MemoryReadAddress( 0x800, 0x80f, pokey2_r ),
                new MemoryReadAddress( -1 )	/* end of table */
        };



        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x0200, MWA_RAM ),
                new MemoryWriteAddress( 0x1000, 0x13ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x13c0, 0x13ff, MWA_RAM, spriteram ),
                new MemoryWriteAddress( 0x0400, 0x040f, pokey1_w ),
                new MemoryWriteAddress( 0x0800, 0x080f, pokey2_w ),
                new MemoryWriteAddress( 0x4000, 0x73ff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static InputPort input_ports[] =
        {
                new InputPort(
                        0xf8,
                        new int[]{ 0, 0, 0, 0, OSD_KEY_CONTROL, OSD_KEY_1, 0, 0}
                ),
		new InputPort(
                        0xf0,
                        new int[]{ 0, 0, 0, 0, OSD_KEY_CONTROL, OSD_KEY_2, 0, 0 }
                ),
		new InputPort(
                        0xff,
                        new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_DOWN, OSD_KEY_UP,
                                        0, 0, OSD_KEY_3, 0 }
                ),
		new InputPort(
                        0xff,
                        new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_DOWN, OSD_KEY_UP,
                                        0, 0, 0, 0 }
                ),
		new InputPort(	/* DSW1 */
                        0x14,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* DSW2 */
                        0x02,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort( -1 )	/* end of table */
        };

        /* Added 11-JUL-97 JB */
        static TrakPort trak_ports[] = {
          new TrakPort(
            X_AXIS,
            1,
            1.0,
            centiped_trakball_x
          ),
          new TrakPort(
            Y_AXIS,
            1,
            1.0,
            centiped_trakball_y
          ),
          new TrakPort( -1 )
        };


        static KEYSet keys[] =
        {
                new KEYSet( 2, 3, "PL1 MOVE UP" ),
                new KEYSet( 2, 1, "PL1 MOVE LEFT"  ),
                new KEYSet( 2, 0, "PL1 MOVE RIGHT" ),
                new KEYSet( 2, 2, "PL1 MOVE DOWN" ),
                new KEYSet( 0, 4, "PL1 FIRE" ),
                new KEYSet( 3, 3, "PL2 MOVE UP" ),
                new KEYSet( 3, 1, "PL2 MOVE LEFT"  ),
                new KEYSet( 3, 0, "PL2 MOVE RIGHT" ),
                new KEYSet( 3, 2, "PL2 MOVE DOWN" ),
                new KEYSet( 1, 4, "PL2 FIRE" ),
                new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                new DSW( 4, 0x0c, "LIVES", new String[]{ "2", "3", "4", "5" } ),
                new DSW( 4, 0x30, "BONUS", new String[]{ "12000", "15000", "20000", "NONE" } ),
                new DSW( 4, 0x80, "STARTING SCORE SELECT", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 0, 0x0c, "", new String[]{ "0", "0 1XBONUS", "0 1XBONUS 2XBONUS", "0 1XBONUS 2XBONUS 3XBONUS" } ),
                new DSW( 4, 0x01, "MILLIPEDE HEAD", new String[]{ "EASY", "HARD" } ),
                new DSW( 4, 0x02, "BEETLE", new String[]{ "EASY", "HARD" } ),
                new DSW( 4, 0x40, "SPIDER", new String[]{ "EASY", "HARD" } ),
                new DSW( 0, 0x03, "LANGUAGE", new String[]{ "ENGLISH", "GERMAN", "FRENCH", "SPANISH" } ),
                new DSW( -1 )
        };



        public static GfxLayout charlayout= new GfxLayout
	(
                8,8,	/* 8*8 characters */
                256,	/* 256 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 256*8*8 },	/* the two bitplanes are separated */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },
                8*8	/* every char takes 8 consecutive bytes */
        );
        static GfxLayout spritelayout = new GfxLayout
	(
                16,8,	/* 16*8 sprites */
                128,	/* 64 sprites */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 128*16*8 },	/* the two bitplanes are separated */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                                8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },
                16*8	/* every sprite takes 16 consecutive bytes */
        );



        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, charlayout,     0, 8 ),
                new GfxDecodeInfo( 1, 0x0000, spritelayout,   0, 8 ),
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
                black, darkred,   blue,       darkyellow,
                black, green,     darkpurple, orange,
                black, darkgreen, darkred,    yellow,
                black, darkred,   darkgreen,  yellow,
                black, yellow,    darkgreen,  red,
                black, green,     orange,     yellow,
                black, darkwhite, red,        pink,
                black, darkcyan,  red,        darkwhite
        };



        public static MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_M6502,
                                1000000,	/* 1 Mhz ???? */
                                0,
                                readmem,writemem,null,null,
                                milliped_interrupt,2		/* ASG -- 2 per frame, once for VBLANK and once not? */
                        )
                },
                60,
                null,

                /* video hardware */
                32*8, 32*8, new rectangle( 1*8, 31*8-1, 0*8, 32*8-1 ),
                gfxdecodeinfo,
                sizeof(palette)/3,sizeof(colortable),
                null,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
                null,
                generic_vh_start,
                generic_vh_stop,
                milliped_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                pokey2_sh_start,
                pokey_sh_stop,
                pokey_sh_update
        );



        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr milliped_rom= new RomLoadPtr(){ public void handler()  //using 0.27 loading ,crcs are probably wrong...
        {
        
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "milliped.104", 0x4000, 0x1000, 0xd13b2ed1 );
                ROM_LOAD( "milliped.103", 0x5000, 0x1000, 0x8d016c93 );
                ROM_LOAD( "milliped.102", 0x6000, 0x1000, 0x0a7b24db );
                ROM_LOAD( "milliped.101", 0x7000, 0x1000, 0x35374cb3 );
                ROM_RELOAD(               0xf000, 0x1000 );	/* for the reset and interrupt vectors */

                ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "milliped.106", 0x0000, 0x0800, 0x006170b5 );
                ROM_LOAD( "milliped.107", 0x0800, 0x0800, 0x7bd67d9e );
                ROM_END();
        }};



        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
	{
                /* check if the hi score table has already been initialized */
                if (memcmp(RAM,0x0064,new char[]{0x75,0x91,0x08},3) == 0 &&
                                memcmp(RAM,0x0079,new char[]{0x75,0x91,0x08},3) == 0)
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0x0064,1,6*8,f);
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
                        fwrite(RAM,0x0064,1,6*8,f);
                        fclose(f);
                }
        }};


        public static GameDriver milliped_driver = new GameDriver
	(
                "Millipede",
                "milliped",
                "IVAN MACKINTOSH\nNICOLA SALMORIA\nJOHN BUTLER\nAARON GILES\nBERND WIEBELT",
                machine_driver,

                milliped_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                null, palette, colortable,
               ORIENTATION_DEFAULT,

                hiload, hisave
        );
}
