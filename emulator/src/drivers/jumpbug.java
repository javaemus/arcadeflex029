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
 *  roms are from 0.36 romset
 *  jumpbugsega is jumpbugb in v0.36 romset
 */

package drivers;


import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static mame.inptport.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.jumpbug.*;
import static vidhrdw.generic.*;
import static vidhrdw.jumpbug.*;
import static mame.mame.*;

public class jumpbug
{



	static MemoryReadAddress readmem[] =
	{
	 new MemoryReadAddress( 0x4c00, 0x4fff, videoram_r ),	/* mirror address for Video RAM*/
	 new MemoryReadAddress( 0x5000, 0x507f, MRA_RAM ),	/* screen attributes, sprites */
	 new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
	 new MemoryReadAddress( 0x8000, 0xafff, MRA_ROM ),
	 new MemoryReadAddress( 0x6000, 0x6000, input_port_0_r ),	/* IN0 */
	 new MemoryReadAddress( 0x6800, 0x6800, input_port_1_r ),	/* IN1 */
	 new MemoryReadAddress( 0x7000, 0x7000, input_port_2_r ),	/* IN2 */
         new MemoryReadAddress( 0xeff0, 0xefff, MRA_RAM ),
         new MemoryReadAddress(-1 )	/* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
          new MemoryWriteAddress( 0x4000, 0x47ff, MWA_RAM ),
          new MemoryWriteAddress( 0x4800, 0x4bff, videoram_w, videoram, videoram_size ),
          new MemoryWriteAddress( 0x4c00, 0x4fff, videoram_w ),	/* mirror address for Video RAM */
	  new MemoryWriteAddress( 0x5000, 0x503f, jumpbug_attributes_w, jumpbug_attributesram ),
	  new MemoryWriteAddress( 0x5040, 0x505f, MWA_RAM, spriteram, spriteram_size ),
	  new MemoryWriteAddress( 0x7001, 0x7001, interrupt_enable_w ),
	  new MemoryWriteAddress( 0x7004, 0x7004, MWA_RAM, jumpbug_stars ),
	  new MemoryWriteAddress( 0x6002, 0x6006, jumpbug_gfxbank_w, jumpbug_gfxbank ),
	  new MemoryWriteAddress(0x5900, 0x5900, AY8910_control_port_0_w ),
	  new MemoryWriteAddress( 0x5800, 0x5800, AY8910_write_port_0_w ),
	  new MemoryWriteAddress( 0x7800, 0x7800, MWA_NOP ),
          new MemoryWriteAddress( 0xeff0, 0xefff, MWA_RAM ),
	  new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
	  new MemoryWriteAddress( 0x8000, 0xafff, MWA_ROM ),
	  new MemoryWriteAddress( -1 )	/* end of table */

	};


	
	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0x00,
			new int[] { 0, 0, OSD_KEY_LEFT, OSD_KEY_RIGHT,
						OSD_KEY_CONTROL, OSD_KEY_ALT, OSD_KEY_DOWN, OSD_KEY_UP }
		),
		new InputPort(	/* IN1 */
			0x40,
			new int[] { OSD_KEY_1, OSD_KEY_2, 0, 0, 0, OSD_KEY_3, 0, 0 }
		),
		new InputPort(	/* DSW */
			0x01,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};


		
	static DSW dsw[] =
	{
		new DSW( 2, 0x03, "LIVES", new String[] { "UNLIMITED", "3", "4", "5" } ),
		new DSW( 1, 0x40, "DIFFICULTY", new String[] { "HARD", "EASY" }, 1 ),
		new DSW( -1 )
	};

static  TrakPort trak_ports[] =
{
         new TrakPort(-1)
};

static KEYSet keys[] =
{
        new KEYSet( 0, 7, "MOVE UP" ),
        new KEYSet( 0, 2, "MOVE LEFT"  ),
        new KEYSet( 0, 3, "MOVE RIGHT" ),
        new KEYSet( 0, 6, "MOVE DOWN" ),
        new KEYSet( 0, 4, "FIRE" ),
        new KEYSet( 0, 5, "JUMP" ),
        new KEYSet( -1 )
};
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		768,	/* 768 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 768*8*8 },	/* the two bitplanes are separated */
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		192,	/* 192 sprites */
		2,	/* 2 bits per pixel */
		new int[] { 0, 192*16*16 },	/* the two bitplanes are separated */
		new int[] { 23*8, 22*8, 21*8, 20*8, 19*8, 18*8, 17*8, 16*8,
				7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		32*8	/* every sprite takes 32 consecutive bytes */
	);


	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,     0, 8 ),
		new GfxDecodeInfo( 1, 0x0000, spritelayout,   0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};


	
	static char color_prom[] =
	{
		/* palette */
		0x00,0x17,0xC7,0xF6,0x00,0x17,0xC0,0x3F,0x00,0x07,0xC0,0x3F,0x00,0xC0,0xC4,0x07,
		0x00,0xC7,0x31,0x17,0x00,0x31,0xC7,0x3F,0x00,0xF6,0x07,0xF0,0x00,0x3F,0x07,0xC4
	};



	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				readmem, writemem, null, null,
				nmi_interrupt, 1
			)
		},
		60,
		null,
	
		/* video hardware */
		32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
		gfxdecodeinfo,
                32+64, 32,	/* 32 for the characters, 64 for the stars */
                jumpbug_vh_convert_color_prom,
                VIDEO_TYPE_RASTER,
		null,
		jumpbug_vh_start,
		generic_vh_stop,
		jumpbug_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		jumpbug_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	static RomLoadPtr jumpbug_rom= new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "jb1",          0x0000, 0x1000, 0x415aa1b7 );
                ROM_LOAD( "jb2",          0x1000, 0x1000, 0xb1c27510 );
                ROM_LOAD( "jb3",          0x2000, 0x1000, 0x97c24be2 );
                ROM_LOAD( "jb4",          0x3000, 0x1000, 0x66751d12 );
                ROM_LOAD( "jb5",          0x8000, 0x1000, 0xe2d66faf );
                ROM_LOAD( "jb6",          0x9000, 0x1000, 0x49e0bdfd );
                ROM_LOAD( "jb7",          0xa000, 0x0800, 0x83d71302 );
	
		ROM_REGION(0x3000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "jbl",          0x0000, 0x0800, 0x9a091b0a );
                ROM_LOAD( "jbm",          0x0800, 0x0800, 0x8a0fc082 );
                ROM_LOAD( "jbn",          0x1000, 0x0800, 0x155186e0 );
                ROM_LOAD( "jbi",          0x1800, 0x0800, 0x7749b111 );
                ROM_LOAD( "jbj",          0x2000, 0x0800, 0x06e8d7df );
                ROM_LOAD( "jbk",          0x2800, 0x0800, 0xb8dbddf3 );         
	        ROM_END();
        }};
	static RomLoadPtr jumpbugb_rom= new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x10000);	/* 64k for code */
                    ROM_LOAD( "jb1",          0x0000, 0x1000, 0x415aa1b7 );
                    ROM_LOAD( "jb2",          0x1000, 0x1000, 0xb1c27510 );
                    ROM_LOAD( "jb3b",         0x2000, 0x1000, 0xcb8b8a0f );
                    ROM_LOAD( "jb4",          0x3000, 0x1000, 0x66751d12 );
                    ROM_LOAD( "jb5b",         0x8000, 0x1000, 0x7553b5e2 );
                    ROM_LOAD( "jb6b",         0x9000, 0x1000, 0x47be9843 );
                    ROM_LOAD( "jb7b",         0xa000, 0x0800, 0x460aed61 );

		ROM_REGION(0x3000);	/* temporary space for graphics (disposed after conversion) */
	            ROM_LOAD( "jbl",          0x0000, 0x0800, 0x9a091b0a );
                    ROM_LOAD( "jbm",          0x0800, 0x0800, 0x8a0fc082 );
                    ROM_LOAD( "jbn",          0x1000, 0x0800, 0x155186e0 );
                    ROM_LOAD( "jbi",          0x1800, 0x0800, 0x7749b111 );
                    ROM_LOAD( "jbj",          0x2000, 0x0800, 0x06e8d7df );
                    ROM_LOAD( "jbk",          0x2800, 0x0800, 0xb8dbddf3 );
	        ROM_END();
        }};


	static DecodePtr jumpbug_decode = new DecodePtr()
        {
            public void handler()
            {
                /* this is not a "decryption", it is just a protection removal */
                RAM[0x265a] = 0xc9;
                RAM[0x8a16] = 0xc9;
                RAM[0x8dae] = 0xc9;
                RAM[0x8dbe] = 0xc3;
                RAM[0x8dd7] = 0x18;
                RAM[0x9f3d] = 0x18;
                RAM[0x9f53] = 0xc3;
            }
         
        };




	public static GameDriver jumpbug_driver = new GameDriver
	(
                "Jump Bug",
		"jumpbug",
                "RICHARD DAVIES\nBRAD OLIVER\nNICOLA SALMORIA",
		machine_driver,
	
		jumpbug_rom,
		jumpbug_decode, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		color_prom, null, null,
		ORIENTATION_DEFAULT,
	
		null, null
	);

	public static GameDriver jumpbugb_driver = new GameDriver
	(
                "Jump Bug (bootleg)",
		"jumpbugb",
                "RICHARD DAVIES\nBRAD OLIVER\nNICOLA SALMORIA",
		machine_driver,
	
		jumpbugb_rom,
		null, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		color_prom, null, null,
		ORIENTATION_DEFAULT,
	
		null, null
	);
}

