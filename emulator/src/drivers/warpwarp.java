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
import static mame.osdependH.*;
import static machine.warpwarp.*;
import static vidhrdw.generic.*;
import static vidhrdw.warpwarp.*;
import static mame.memoryH.*;
public class warpwarp
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x8000, 0x83FF, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x37ff, MRA_ROM ),
		new MemoryReadAddress( 0x4000, 0x47FF, MRA_RAM ),
		new MemoryReadAddress( 0x4800, 0x4FFF, MRA_ROM ),
        	new MemoryReadAddress( 0xc000, 0xc007, warpwarp_input_c000_7_r ),
        	new MemoryReadAddress( 0xc010, 0xc010, warpwarp_input_controller_r ),
        	new MemoryReadAddress( 0xc020, 0xc027, warpwarp_input_c020_27_r ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x8000, 0x83FF, MWA_RAM ),
		new MemoryWriteAddress( 0x4000, 0x43ff, videoram_w, videoram , videoram_size ),
		new MemoryWriteAddress( 0x4400, 0x47ff, colorram_w, colorram ),
		new MemoryWriteAddress( 0xC000, 0xC001, MWA_RAM, warpwarp_bulletsram ),

		new MemoryWriteAddress( 0x0000, 0x37ff, MWA_ROM ),
		new MemoryWriteAddress( 0x4800, 0x4FFF, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};


        static InputPort input_ports[] =
        {
                new InputPort(	/* DSW1 */
                        0x0D,
                       new int[]  { 0, 0, 0, 0, 0, 0, 0, 0 }

                ),
                new InputPort( /* Controls get mapped to correct read location in Machine.c code */
                        0xFF,
                       new int[]  { OSD_KEY_3, 0, OSD_KEY_1, OSD_KEY_2, OSD_KEY_CONTROL,0,0,0 }

                ),
                new InputPort(
                        0x00,
                       new int[]  { OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_UP, OSD_KEY_DOWN,0,0,0,0 }

                ),
               new InputPort(	/* Test setting */
                        0x1,
                      new int[]   { 0, 0, 0, 0, 0, 0, 0, 0 }

                ),
                new InputPort( -1 )	/* end of table */
        };

        static TrakPort trak_ports[] =
        {
                new TrakPort( -1 )
        };


        static KEYSet keys[] =
        {
                new KEYSet( -1 )
        };
	
	static DSW dsw[] =
	{

            new DSW( 0, 0x03, "CREDITS", new String[]{ "FREEPLAY", "1 COIN 1 CREDIT","1 COIN 2 CREDITS","2 COINS 1 CREDIT" } ),
            new DSW( 0, 0x0C, "NO FIGHTERS", new String[]{ "2", "3", "4", "5" } ),
    /* The bonus setting changes depending on the no of lives */
            new DSW( 0, 0x30, "BONUS",new String[] {	"SETTING 1","SETTING 2","SETTING 3","SETTING 4", } ),
            new DSW( 0, 0x40, "DEMO SOUND",new String[] { "ON", "OFF" } ),
            new DSW( 1, 1<<5, "TEST MODE",new String[] { "ON", "OFF" } ),
            new DSW( -1 )
	};


	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		1,	/* 2 bits per pixel */
		new int[] { 0 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 }, /* characters are rotated 90 degrees */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },	/* bits are packed in groups of four */
		8*8	/* every char takes 16 bytes */
	);

	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		64,	/* 64 sprites */
		1,	/* 1 bits per pixel */
		new int[] { 0 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 23 * 8, 22 * 8, 21 * 8, 20 * 8, 19 * 8, 18 * 8, 17 * 8, 16 * 8 ,
	           7 * 8, 6 * 8, 5 * 8, 4 * 8, 3 * 8, 2 * 8, 1 * 8, 0 * 8 },
		new int[] {  0, 1, 2, 3, 4, 5, 6, 7 ,
        	 8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		32*8	/* every sprite takes 32 bytes */
	);


		
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,   0, 16 ),
		new GfxDecodeInfo( 1, 0x0000, spritelayout, 0, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};


	static char palette[] =
	{
            0, 0, 0,
            0, 165, 255,
            231, 231, 0,
            165, 0, 0,
            181, 181, 181,
            239, 0, 239,
            247, 0, 156,
            255, 0, 0,
            255, 132, 0,
            255, 181, 115,
            255, 255, 255,
            255, 0, 255,
            0, 255, 255,
            0, 0, 255,
            255, 0, 0
	};
	
	
	
	static char colortable[] =
	{
	            0,0,
                    0,1,
                    0,2,
                    0,3,
                    0,4,
                    0,5,
                    0,6,
                    0,7,
                    0,8,
                    0,9,
                    0,9,
                    0,10,
                    0,11,
                    0,12,
                    0,13,
                    0,14,
                    0,15
	};


		
	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				2048000,	/* 3 Mhz? */
				0,
				readmem, writemem, null, null,
				interrupt, 1
			)
		},
		60,
		null,
	
		/* video hardware */
		32*8, 34*8, new rectangle( 0*8, 31*8-1, 0*8, 34*8-1 ),
		gfxdecodeinfo,
		sizeof(palette)/3 ,sizeof(colortable),
		null,
                VIDEO_TYPE_RASTER,
		null,
		generic_vh_start,
		generic_vh_stop,
		warpwarp_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		null,
		null,
		null
	);



	static RomLoadPtr warpwarp_rom= new RomLoadPtr(){ public void handler()  
        {
		ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "g-n9601n.2r",  0x0000, 0x1000, 0xf5262f38 );
                ROM_LOAD( "g-09602n.2m",  0x1000, 0x1000, 0xde8355dd );
                ROM_LOAD( "g-09603n.1p",  0x2000, 0x1000, 0xbdd1dec5 );
                ROM_LOAD( "g-09613n.1t",  0x3000, 0x0800, 0xaf3d77ef );
                ROM_LOAD( "g-9611n.4c",   0x4800, 0x0800, 0x380994c8 );

		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		 ROM_LOAD( "g-9611n.4c", 0x0000, 0x0800,0 );
                ROM_END();
        }};
              
	public static GameDriver warpwarp_driver = new GameDriver
	(
                "Warp Warp",
		"warpwarp",
                "CHRIS HARDY",
		machine_driver,
	
		warpwarp_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		null, palette, colortable,
		ORIENTATION_DEFAULT,
	
		null, null
	);
}

