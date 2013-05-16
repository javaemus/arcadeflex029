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
import static mame.osdependH.*;
import static mame.inptport.*;
import static mame.mame.*;
import static machine.carnival.*;
import static vidhrdw.generic.*;
import static vidhrdw.carnival.*;
import static sndhrdw.carnival.*;
import static mame.memoryH.*;
public class carnival
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0xe000, 0xefff, MRA_RAM ),
		new MemoryReadAddress( 0x4000, 0x7fff, MRA_ROM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0xe000, 0xe3ff, videoram_w, videoram,videoram_size ),
		new MemoryWriteAddress( 0xe400, 0xe7ff, MWA_RAM ),
		new MemoryWriteAddress( 0xe800, 0xefff, carnival_characterram_w, carnival_characterram ),
		new MemoryWriteAddress( 0x4000, 0x7fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};



	static IOReadPort readport[] =
	{
		new IOReadPort( 0x00, 0x00, input_port_0_r ),
		new IOReadPort( 0x01, 0x01, input_port_1_r ),
		new IOReadPort( 0x02, 0x02, input_port_2_r ),
		new IOReadPort( 0x03, 0x03, input_port_3_r ),
		new IOReadPort( -1 )	/* end of table */
	};

	static IOWritePort writeport[] =
	{
            	new IOWritePort( 0x01, 0x01, carnival_sh_port1_w ),						/* MJC */
                new IOWritePort( 0x02, 0x02, carnival_sh_port2_w ),						/* MJC */
		new IOWritePort( -1 )	/* end of table */
	};



	static InputPort input_ports[] =
	{
		new InputPort(       /* IN0 */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(       /* IN1 */
			0xff,
			new int[] { 0, 0, 0, IPB_VBLANK, OSD_KEY_LEFT, OSD_KEY_RIGHT, 0, 0 }
		),
		new InputPort(       /* IN2 */
			0xff,
			new int[] { 0, 0, 0, 0, OSD_KEY_1, OSD_KEY_CONTROL, 0, 0 }
		),
		new InputPort(       /* IN3 */
			0xff,
			new int[] { 0, 0, 0, OSD_KEY_3, 0, OSD_KEY_2, 0, 0 }
		),
		new InputPort( -1 )
	};

static TrakPort trak_ports[] =
{
        new TrakPort(-1)
};


static KEYSet keys[] =
{
         new KEYSet( 1, 4, "MOVE LEFT"  ),
         new KEYSet( 1, 5, "MOVE RIGHT" ),
         new KEYSet( 2, 5, "JUMP" ),
         new KEYSet( -1 )
};

	static DSW dsw[] =
	{
		new DSW( 0, 0x10, "IN0 BIT 4", new String[] { "0", "1" } ),
		new DSW( -1 )
	};



	static GfxLayout charlayout = new GfxLayout
	(
			8,8,	/* 8*8 characters */
                        256,	/* 256 characters */
                        1,	/* 1 bit per pixel */
                        new int[] { 0 },
                        new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8, 8*8, 9*8 },
                        new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },	/* pretty straightforward layout */
                        8*8	/* every char takes 8 consecutive bytes */
	);




	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 0, 0xe800, charlayout, 0, 32 ),	/* the game dynamically modifies this */
		new GfxDecodeInfo( -1 ) /* end of array */
	};



static char color_prom[] =
{
	/* U49: palette */
	0xE0,0xA0,0x80,0xA0,0x60,0xA0,0x20,0x60,0xE0,0xA0,0x80,0xA0,0x60,0xA0,0x20,0x60,
	0xE0,0xA0,0x80,0xA0,0x60,0xA0,0x20,0x60,0xE0,0xA0,0x80,0xA0,0x60,0xA0,0x20,0x60
};




	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz (?) */
				0,
				readmem, writemem, readport, writeport,
				carnival_interrupt, 1
			)
		},
		60,
		null,

		/* video hardware */
		32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
		gfxdecodeinfo,
		64,32*2,
		carnival_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
		null,
		generic_vh_start,
		generic_vh_stop,
		carnival_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		null,
		null,
		carnival_sh_update
	);
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	static RomLoadPtr carnival_rom= new RomLoadPtr(){ public void handler()  
        {
		 ROM_REGION(0x10000);	/* 64k for code */
                 ROM_LOAD( "651u33.cpu", 0x0000, 0x0400, 0x9f2736e6 );
                 ROM_RELOAD(0x4000, 0x0400 );
                 ROM_LOAD( "652u32.cpu", 0x4400, 0x0400, 0xa1f58beb );
                 ROM_LOAD( "653u31.cpu", 0x4800, 0x0400, 0x67b17922 );
                 ROM_LOAD( "654u30.cpu", 0x4c00, 0x0400, 0xbefb09a5 );
                 ROM_LOAD( "655u29.cpu", 0x5000, 0x0400, 0x623fcdad );
                 ROM_LOAD( "656u28.cpu", 0x5400, 0x0400, 0x53040332 );
                 ROM_LOAD( "657u27.cpu", 0x5800, 0x0400, 0xf2537467 );
                 ROM_LOAD( "658u26.cpu", 0x5c00, 0x0400, 0xfcc3854e );
                 ROM_LOAD( "659u8.cpu",  0x6000, 0x0400, 0x28be8d69 );
                 ROM_LOAD( "660u7.cpu",  0x6400, 0x0400, 0x3873ccdb );
                 ROM_LOAD( "661u6.cpu",  0x6800, 0x0400, 0xd9a96dff );
                 ROM_LOAD( "662u5.cpu",  0x6c00, 0x0400, 0xd893ca72 );
                 ROM_LOAD( "663u4.cpu",  0x7000, 0x0400, 0xdf8c63c5 );
                 ROM_LOAD( "664u3.cpu",  0x7400, 0x0400, 0x689a73e8 );
                 ROM_LOAD( "665u2.cpu",  0x7800, 0x0400, 0x28e7b2b6 );
                 ROM_LOAD( "666u1.cpu",  0x7c00, 0x0400, 0x4eec7fae );
                 ROM_END();
        }};

                
        static String carnival_sample_names[] =					/* MJC */
        {
                "C1.SAM",	/* Fire Gun 1 */
                "C2.SAM",	/* Hit Target */
                "C3.SAM",	/* Duck 1 */
                "C4.SAM",	/* Duck 2 */
                "C5.SAM",	/* Duck 3 */
                "C6.SAM",	/* Hit Pipe */
                "C7.SAM",	/* BONUS */
                "C8.SAM",	/* Hit bonus box */
                "C9.SAM",	/* Fire Gun 2 */
                "C10.SAM",	/* Hit Bear */
                null     	/* end of array */
        };
	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
	{
                /* check if the hi score table has already been initialized */
          /*TOFIX               if ((memcmp(RAM,0xE397,new char[]{0x00,0x00,0x00},3) == 0) &&
                        (memcmp(RAM,0xE5A2,"   ",3) == 0))
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                /* Read the scores */
   /*TOFIX                                      fread(RAM,0xE397,1,2*30,f);
                                /* Read the initials */
   /*TOFIX                                      fread(RAM,0xE5A2,1,9,f);
                                fclose(f);
                        }

                        return 1;
                }
                else */return 0;	/* we can't load the hi scores yet */
        }};



        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
	{
                FILE f;


    /*TOFIX                     if ((f = fopen(name,"wb")) != null)
                {
                        /* Save the scores */
    /*TOFIX                             fwrite(RAM,0xE397,1,2*30,f);
                        /* Save the initials */
    /*TOFIX                             fwrite(RAM,0xE5A2,1,9,f);
                        fclose(f);
                }*/

        }};
	public static GameDriver carnival_driver = new GameDriver
	(
                "Carnival",
		"carnival",
                "MIKE COATES\nRICHARD DAVIES\nNICOLA SALMORIA\nMIKE BALFOUR",
		machine_driver,
	
		carnival_rom,
		null, null,
		carnival_sample_names,
	
		input_ports,null, trak_ports, dsw, keys,
	
		color_prom,null, null,
		ORIENTATION_DEFAULT,
	
		hiload, hisave
	);
}
