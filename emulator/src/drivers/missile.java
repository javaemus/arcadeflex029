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
import static sndhrdw.generic.*;
import static sndhrdw.pokeyintf.*;
import static machine.missile.*;
import static vidhrdw.missile.*;
import static mame.memoryH.*;

public class missile {
        static MemoryReadAddress readmem[] =
        {
                 new MemoryReadAddress( 0x0000, 0xFFFF, missile_r ),
                 new MemoryReadAddress( -1 )	/* end of table */
        };


        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0xFFFF, missile_w ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static InputPort input_ports[] =
        {
                new InputPort(  /* IN0  - 4800, all inverted */
                /*
                 80 = right coin
                 40 = center coin
                 20 = left coin
                 10 = 1 player start
                 08 = 2 player start
                 04 = 2nd player left fire (cocktail)
                 02 = 2nd player center fire (cocktail)
                 01 = 2nd player right fire (cocktail)
                 */
                        0xFF,
                        new int[]{OSD_KEY_D, OSD_KEY_S, OSD_KEY_A, OSD_KEY_2, OSD_KEY_1, OSD_KEY_3, 0, 0}
                ),


                new InputPort(  /* IN1 - 4900, all inverted */
                /*
                 80 = vbl read
                 40 = self test
                 20 = SLAM switch
                 10 = horiz trackball input
                 08 = vertical trackball input
                 04 = 1st player left fire
                 02 = 1st player center fire
                 01 = 1st player right fire
                */

                        0x67,
                        new int[]{ OSD_KEY_D, OSD_KEY_S, OSD_KEY_A, 0, 0, OSD_KEY_F6, OSD_KEY_F5, IPB_VBLANK}
                ),


                new InputPort( 	/* IN2  - 4A00 - Pricing Option switches, all inverted */
                                0x02,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),


                new InputPort( 	/* IN3  4008 Game Option switches, all inverted */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),

                new InputPort(  -1 )	/* end of table */
        };


        static TrakPort trak_ports[] =
        {
                new TrakPort(
                  X_AXIS,
                  1,
                  1.0,
                  missile_trakball_r
                ),
                new TrakPort(
                  Y_AXIS,
                  1,
                  1.0,
                  missile_trakball_r
                ),
                new TrakPort( -1 )
        };


        static KEYSet keys[] =
        {
                new KEYSet( 1, 2, "LEFT FIRE" ),
                new KEYSet( 1, 1, "CENTER FIRE" ),
                new KEYSet( 1, 0, "RIGHT FIRE" ),
                new KEYSet( -1 )
        };





        static DSW dsw[] =
        {
                new DSW( 2, 0x60, "LANGUAGE", new String[]{ "ENGLISH", "GERMAN", "FRENCH", "SPANISH" } ),
                new DSW( 3, 0x03, "NUMBER OF CITIES", new String[]{ "6", "4", "5", "7" } ),
        /* 	{ 3, 0x08, "TRACKBALL", { "LARGE", "MINI" } }, not very useful */
                new DSW( 3, 0x70, "BONUS AT", new String[]{ "10000", "12000", "14000", "15000", "18000", "20000", "8000", "0" } ),
        /* 	{ 3, 0x80, "MODEL", { "UPRIGHT", "COCKTAIL" } }, */
                new DSW( -1 )
        };





        /* Missile Command has only minimal character mapped graphics, this definition is here */
        /* mainly for the dip switch menu */
        static GfxLayout charlayout = new GfxLayout
        (
                8,8,	/* 8*8 characters */
                50,	/* 50 characters */
                1,	/* 1 bit per pixel */
                 new int[]{ 0 },

                 new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },		/* characters are upside down */
                 new int[]{ 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
                8*8														/* every char takes 8 consecutive bytes */
        );




        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 0, 0x731A, charlayout,     0, 0x10 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };




        static char palette[] =
        {
                0, 0, 0,					/* extra black for Macs */
                0xFF,0xFF,0xFF,   /* white   */
                0xFF,0xFF,0x00,   /* yellow  */
                0xFF,0x00,0xFF,   /* magenta  */
                0xFF,0x00,0x00,   /* red    */
                0x00,0xFF,0xFF,   /* cyan    */
                0x00,0xFF,0x00,   /* green   */
                0x00,0x00,0xFF,   /* blue  */
                0x00,0x00,0x00    /* black */
        };






        static MachineDriver machine_driver = new MachineDriver
       (
                /* basic machine hardware */
                new MachineCPU[] {
                new MachineCPU(
                                CPU_M6502,
                                1000000,	/* 1 Mhz ???? */
                                0,
                                readmem,writemem,null,null,
                                interrupt, 1
                        )
                },
                60,
                missile_init_machine,

                /* video hardware */
                256, 231, new rectangle( 0, 255, 0, 230 ),
                gfxdecodeinfo,
                sizeof(palette)/3, 0,
                null,
                VIDEO_TYPE_RASTER,
                null,
                missile_vh_start,
                missile_vh_stop,
                missile_vh_update,

                /* sound hardware */
                null,
                null,
                pokey1_sh_start,
                pokey_sh_stop,
                pokey_sh_update
        );




        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr missile_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "035820.02", 0x5000, 0x0800, 0x899d091b );
                ROM_LOAD( "035821.02", 0x5800, 0x0800, 0x25543e0a );
                ROM_LOAD( "035822.02", 0x6000, 0x0800, 0x8067194f );
                ROM_LOAD( "035823.02", 0x6800, 0x0800, 0xfc0f6b13 );
                ROM_LOAD( "035824.02", 0x7000, 0x0800, 0xa3e9d74d );
                ROM_LOAD( "035825.02", 0x7800, 0x0800, 0x6050ea56 );
                ROM_RELOAD(            0xF800, 0x0800 );		/* for interrupt vectors  */
                ROM_END();
        }};


        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
        {
                FILE f;
                /* check if the hi score table has already been initialized */
        /*TOFIX        if (memcmp(RAM,0x002C,new char[] {0x47,0x4A,0x4C}, 3) == 0 &&
                                memcmp(RAM,0x0044,new char[] {0x50,0x69,0x00}, 3) == 0){

                        if ((f = fopen(name,"rb")) != null){
                                fread(RAM,0x002C,1,6*8,f);
                                fclose(f);
                        }
                        return 1;
                }else*/
                        return 0;	/* we can't load the hi scores yet */
        }};



        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
        {
                FILE f;

     /*TOFIX           if ((f = fopen(name,"wb")) != null){
                        fwrite(RAM,0x002C,1,6*8,f);
                        fclose(f);
                }*/
        }};


        public static GameDriver missile_driver= new GameDriver
        (
                "Missile Command",
                "missile",
                "RAY GIARRATANA",
                machine_driver,

                missile_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                null, palette, null,

                ORIENTATION_DEFAULT,

                hiload, hisave
        );   
}
