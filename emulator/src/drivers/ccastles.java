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
 * NOTES: romsets are from v0.36 roms but loading procedure is from v0.27
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
import static sndhrdw.generic.*;
import static vidhrdw.ccastles.*;
import static sndhrdw.pokeyintf.*;
import static machine.ccastles.*;
import static mame.inptport.*;

public class ccastles {
    
    static MemoryReadAddress readmem[] =
    {
            new MemoryReadAddress( 0x0000, 0x0001, MRA_RAM ),
            new MemoryReadAddress( 0x0002, 0x0002, ccastles_bitmode_r ),
            new MemoryReadAddress( 0x0003, 0x90ff, MRA_RAM ),	/* All RAM */
            new MemoryReadAddress( 0x9400, 0x9401, ccastles_trakball_r ),
            new MemoryReadAddress( 0x9500, 0x9501, ccastles_trakball_r ),	/* mirror address for the above */
            new MemoryReadAddress( 0x9600, 0x9600, input_port_0_r ),	/* IN0 */
            new MemoryReadAddress( 0x9a08, 0x9a08, input_port_1_r ),	/* OPTION SW */
            new MemoryReadAddress( 0x9800, 0x980f, pokey1_r ), /* Random # generator on a Pokey */
            new MemoryReadAddress( 0x9a00, 0x9a0f, pokey2_r ), /* Random # generator on a Pokey */
            new MemoryReadAddress( 0xA000, 0xDFFF, ccastles_rom_r ),
            new MemoryReadAddress( 0xE000, 0xFFFF, MRA_ROM ),	/* ROMs/interrupt vectors */
            new MemoryReadAddress( -1 )	/* end of table */
    };

    static MemoryWriteAddress writemem[] =
    {
            new MemoryWriteAddress( 0x0000, 0x0001, ccastles_xy_w ),
            new MemoryWriteAddress( 0x0002, 0x0002, ccastles_bitmode_w ),
            new MemoryWriteAddress( 0x0003, 0x90ff, MWA_RAM ),  /* All RAM */
            new MemoryWriteAddress( 0x9800, 0x980F, pokey1_w ),
            new MemoryWriteAddress( 0x9A00, 0x9A0F, pokey2_w ),
            new MemoryWriteAddress( 0x9C80, 0x9C80, MWA_RAM ),    /* Horizontal Scroll */
            new MemoryWriteAddress( 0x9D00, 0x9D00, MWA_RAM ),    /* Vertical Scroll */
            new MemoryWriteAddress( 0x9D80, 0x9D80, MWA_NOP ),
            new MemoryWriteAddress( 0x9E00, 0x9E00, MWA_NOP ),
            new MemoryWriteAddress( 0x9E80, 0x9E81, MWA_NOP ),
            new MemoryWriteAddress( 0x9E85, 0x9E86, MWA_NOP ),
            new MemoryWriteAddress( 0x9E87, 0x9E87, ccastles_bankswitch_w ),
            new MemoryWriteAddress( 0x9F00, 0x9F01, ccastles_axy_w ),
            new MemoryWriteAddress( 0x9F02, 0x9F07, MWA_RAM ),
            new MemoryWriteAddress( 0x9F80, 0x9FBF, ccastles_paletteram_w ),
            new MemoryWriteAddress( -1 )	/* end of table */
    };

    static InputPort ccastles_input_ports[] = {
      new InputPort(	/* IN0 */
        0xdf,
        new int[]{ OSD_KEY_4, OSD_KEY_3, OSD_KEY_5, OSD_KEY_T, 0, IPB_VBLANK, OSD_KEY_CONTROL, 0 }
      ),
        new InputPort(	/* IN1 */
        0x3f,
        new int[]{ 0, 0, 0, OSD_KEY_1, OSD_KEY_2, 0, 0, 0 }
      ),
      new InputPort( -1 )	/* end of table */
    };

    
    static TrakPort ccastles_trak_ports[] = {
      new TrakPort(
        Y_AXIS,
        0,
        1.5,
        ccastles_trakball_y
      ),
      new TrakPort(
        X_AXIS,
        0,
        1.0,
        ccastles_trakball_x
      ),
      new TrakPort( -1 )
    };
    /* End 300697 PJL */

    static KEYSet ccastles_keys[] = {
       new KEYSet( -1 )
    };

    static DSW ccastles_dsw[] = {
      new DSW( 0, 0x10, "SELF TEST", new String[]{ "ON", "OFF" }, 1 ),
      new DSW( -1 )
    };

    static GfxLayout ccastles_spritelayout = new GfxLayout
    (
            8,16,	/* 8*16 sprites */
            256,	/* 256 sprites */
            4,	/* 4 bits per pixel (the most significant bit is always 0) */
            new int[]{ 0x2000*8+0, 0x2000*8+4, 0, 4 },	/* the three bitplanes are separated */
            new int[]{ 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3 },
            new int[]{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
                            8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
            32*8	/* every sprite takes 32 consecutive bytes */
    );
    /* there's nothing here, this is just a placeholder to let the video hardware */
    /* pick the background color table. */
    static GfxLayout fakelayout = new GfxLayout
    (
            1,1,
            0,
            4,	/* 4 bits per pixel */
            new int[]{ 0 },
            new int[]{ 0 },
            new int[]{ 0 },
            0
    );


    static GfxDecodeInfo gfxdecodeinfo[] =
    {
            new GfxDecodeInfo( 1, 0x0000, ccastles_spritelayout,  0, 1 ),
            new GfxDecodeInfo( 0, 0,      fakelayout,            16, 1 ),
            new GfxDecodeInfo( -1 ) /* end of array */
    };



    static MachineDriver ccastles_machine = new MachineDriver
    (
      /* basic machine hardware */
      new MachineCPU[] {
	new MachineCPU(
          CPU_M6502,
          1500000,	/* 1.5 Mhz */
          0,
          readmem,writemem,null,null,
          interrupt,4
        )
      },
      60,
      null,
      256, 232, new rectangle( 0, 255, 0, 231 ),
      gfxdecodeinfo,
      32, 16+16,
      ccastles_vh_convert_color_prom,
      VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
      null,
      ccastles_vh_start,
      ccastles_vh_stop,
      ccastles_vh_screenrefresh,

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
    static RomLoadPtr ccastles_rom= new RomLoadPtr(){ public void handler()  //using 0.27 loading ,crcs are probably wrong...
    {
         ROM_REGION(0x14000);	/* 64k for code */
         ROM_LOAD( "ccastles.303",  0xA000, 0x2000, 0xe3d3d32d );
         ROM_LOAD( "ccastles.304",  0xC000, 0x2000, 0x31eab944 );
         ROM_LOAD( "ccastles.305",  0xE000, 0x2000, 0xd765a559 );
         ROM_LOAD( "ccastles.102", 0x10000, 0x2000, 0x5bbb3ac1 );	/* Bank switched ROMs */
         ROM_LOAD( "ccastles.101", 0x12000, 0x2000, 0xe2aa8e74 );	/* containing level data. */

         ROM_REGION(0x4000);	/* temporary space for graphics */
         ROM_LOAD( "ccastles.107", 0x0000, 0x2000, 0x399cc984 );
         ROM_LOAD( "ccastles.106", 0x2000, 0x2000, 0x8b4c0208 );
        ROM_END();
    }};


    static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name)
    {
            /* Read the NVRAM contents from disk */
            /* No check necessary */
            FILE f;


            if ((f = fopen(name,"rb")) != null)
            {
                    fread(RAM,0x9000,1,0x100,f);
                    fclose(f);
            }
            return 1;
    }};


    static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name)
    {
            FILE f;


            if ((f = fopen(name,"wb")) != null)
            {
                    fwrite(RAM,0x9000,1,0x100,f);
                    fclose(f);
            }
    }};


    public static GameDriver ccastles_driver = new GameDriver
    (
        "Crystal Castles",
	"ccastles",
	"PAT LAWRENCE\nCHRIS HARDY\nSTEVE CLYNES\nNICOLA SALMORIA",
	ccastles_machine,

      ccastles_rom,
      null, null,
      null,

      ccastles_input_ports,null, ccastles_trak_ports,
      ccastles_dsw, ccastles_keys,

      null, null, null,

      ORIENTATION_DEFAULT,

      /* Not sure where the high scores are saved in RAM... */
      hiload, hisave
    );

}
