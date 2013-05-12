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
import static vidhrdw.centiped.*;
import static sndhrdw.pokeyintf.*;
import static machine.centiped.*;
import static mame.inptport.*;

public class centiped {
    
    static MemoryReadAddress readmem[] =
    {
            new MemoryReadAddress( 0x0000, 0x03ff, MRA_RAM ),
            new MemoryReadAddress( 0x0400, 0x07ff, MRA_RAM ),
            new MemoryReadAddress( 0x2000, 0x3fff, MRA_ROM ),
            new MemoryReadAddress( 0xf800, 0xffff, MRA_ROM ),	/* for the reset / interrupt vectors */
            new MemoryReadAddress( 0x0c00, 0x0c00, centiped_IN0_r ),	/* IN0 */
            new MemoryReadAddress( 0x0c01, 0x0c01, input_port_1_r ),	/* IN1 */
            new MemoryReadAddress( 0x0c02, 0x0c02, input_trak_1_r ),	/* IN2 */
            new MemoryReadAddress( 0x0c03, 0x0c03, input_port_3_r ),	/* IN3 */
            new MemoryReadAddress( 0x0800, 0x0800, input_port_4_r ),	/* DSW1 */
            new MemoryReadAddress( 0x0801, 0x0801, input_port_5_r ),	/* DSW2 */
            new MemoryReadAddress( 0x1000, 0x100f, pokey1_r ),
            new MemoryReadAddress( -1 )	/* end of table */
    };

    static MemoryWriteAddress writemem[] =
    {
            new MemoryWriteAddress( 0x0000, 0x03ff, MWA_RAM ),
            new MemoryWriteAddress( 0x0400, 0x07bf, videoram_w, videoram, videoram_size ),
            new MemoryWriteAddress( 0x07c0, 0x07ff, MWA_RAM, spriteram ),
            new MemoryWriteAddress( 0x1000, 0x100f, pokey1_w ),
            new MemoryWriteAddress( 0x1404, 0x1407, centiped_vh_charpalette_w, centiped_charpalette ),
            new MemoryWriteAddress( 0x140c, 0x140f, MWA_RAM, centiped_spritepalette ),
            new MemoryWriteAddress( 0x1800, 0x1800, MWA_NOP ),
            new MemoryWriteAddress( 0x1c00, 0x1c07, MWA_NOP ),
            new MemoryWriteAddress( 0x1680, 0x1680, MWA_NOP ),
            new MemoryWriteAddress( 0x2000, 0x2000, MWA_NOP ),
            new MemoryWriteAddress( 0x2000, 0x3fff, MWA_ROM ),
            new MemoryWriteAddress( -1 )	/* end of table */
    };



    static InputPort input_ports[] =
    {
            new InputPort(	/* IN0 */
                    0x20,
                    new int[]{ 0, 0, 0, 0, 0, OSD_KEY_F2, IPB_VBLANK, 0 }
            ),
		new InputPort(	/* IN1 */
                    0xff,
                    new int[]{ OSD_KEY_1, OSD_KEY_2, OSD_KEY_CONTROL, OSD_KEY_CONTROL, 0, 0, OSD_KEY_3, 0 }
            ),
		new InputPort(	/* IN2 */
                    0x00,
                    new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
            ),
	    new InputPort(	/* IN3 */
                    0xff,
                    new int[]{ OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_LEFT, OSD_KEY_RIGHT,
                                    OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_LEFT, OSD_KEY_RIGHT }
            ),
	    new InputPort(	/* DSW1 */
                    0x54,
                    new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
            ),
            new InputPort(	/* DSW2 */
                    0x02,
                    new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
            ),
	   new InputPort( -1 )	/* end of table */
    };

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
            new KEYSet( 3, 0, "MOVE UP" ),
            new KEYSet( 3, 2, "MOVE LEFT"  ),
            new KEYSet( 3, 3, "MOVE RIGHT" ),
            new KEYSet( 3, 1, "MOVE DOWN" ),
            new KEYSet( 1, 2, "PL1 FIRE" ),
            new KEYSet( 1, 3, "PL2 FIRE" ),
            new KEYSet( -1 )
    };


    static DSW dsw[] =
    {
            new DSW( 4, 0x0c, "LIVES", new String[]{ "2", "3", "4", "5" } ),
            new DSW( 4, 0x30, "BONUS", new String[]{ "10000", "12000", "15000", "20000" } ),
            new DSW( 4, 0x40, "DIFFICULTY", new String[]{ "HARD", "EASY" }, 1 ),
            new DSW( 4, 0x03, "LANGUAGE", new String[]{ "ENGLISH", "GERMAN", "FRENCH", "SPANISH" } ),
            new DSW( -1 )
    };



    public static GfxLayout charlayout= new GfxLayout
    (
            8,8,	/* 8*8 characters */
            256,	/* 256 characters */
            2,	/* 2 bits per pixel */
            new int[]{ 256*8*8, 0 },	/* the two bitplanes are separated */
            new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
            new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },
            8*8	/* every char takes 8 consecutive bytes */
    );
    static GfxLayout spritelayout = new GfxLayout
    (
            16,8,	/* 16*8 sprites */
            128,	/* 64 sprites */
            2,	/* 2 bits per pixel */
            new int[]{ 128*16*8, 0 },	/* the two bitplanes are separated */
            new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                            8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
            new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },
            16*8	/* every sprite takes 16 consecutive bytes */
    );



    static GfxDecodeInfo gfxdecodeinfo[] =
    {
            new GfxDecodeInfo( 1, 0x0000, charlayout,   4, 1 ),
            new GfxDecodeInfo( 1, 0x0000, spritelayout, 0, 1 ),
            new GfxDecodeInfo( -1 ) /* end of array */
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
                            interrupt,1
                    )
            },
            60,
            null,

            /* video hardware */
            32*8, 32*8, new rectangle( 1*8, 31*8-1, 0*8, 32*8-1 ),
            gfxdecodeinfo,
            16, 2*4,
            centiped_vh_convert_color_prom,
            VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
            null,
            generic_vh_start,
            generic_vh_stop,
            centiped_vh_screenrefresh,

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
    static RomLoadPtr centiped_rom= new RomLoadPtr(){ public void handler()  //using 0.27 loading ,crcs are probably wrong...
    {
            ROM_REGION(0x10000);	/* 64k for code */
            ROM_LOAD( "centiped.307", 0x2000, 0x0800, 0x2409fc03 );
            ROM_LOAD( "centiped.308", 0x2800, 0x0800, 0x209922dd );
            ROM_LOAD( "centiped.309", 0x3000, 0x0800, 0x57cee11e );
            ROM_LOAD( "centiped.310", 0x3800, 0x0800, 0xb959c639 );
            ROM_RELOAD(         0xf800, 0x0800 );	/* for the reset and interrupt vectors */

            ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
            ROM_LOAD( "centiped.211", 0x0000, 0x0800, 0x704a2608 );
            ROM_LOAD( "centiped.212", 0x0800, 0x0800, 0xc9016e3f );
            ROM_END();
    }};



    static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name)
    {
            /* check if the hi score table has already been initialized */
            if (memcmp(RAM,0x0002,new char[]{0x43,0x65,0x01},3) == 0 &&
                            memcmp(RAM,0x0017,new char[]{0x02,0x21,0x01},3) == 0)
            {
                    FILE f;


                    if ((f = fopen(name,"rb")) != null)
                    {
                            fread(RAM,0x0002,1,6*8,f);
                            fclose(f);
                    }

                    return 1;
            }
            else return 0;	/* we can't load the hi scores yet */
    }};



    static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name)
    {
            FILE f;


            if ((f = fopen(name,"wb")) != null)
            {
                    fwrite(RAM,0x0002,1,6*8,f);
                    fclose(f);
            }
    }};


    public static GameDriver centiped_driver = new GameDriver
    (
            "Centipede",
            "centiped",
            "IVAN MACKINTOSH\nEDWARD MASSEY\nPETE RITTWAGE\nNICOLA SALMORIA\nMIRKO BUFFONI",
            machine_driver,

            centiped_rom,
            null, null,
            null,

            input_ports,null, trak_ports, dsw, keys,

            null, null, null,
            ORIENTATION_DEFAULT,

            hiload, hisave
    );
}
