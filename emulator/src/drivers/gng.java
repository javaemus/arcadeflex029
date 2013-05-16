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
 *
 * ported to v0.28
 * roms are from v0.36 romset
 *
 *
 */
package drivers;


import static arcadeflex.libc.*;
import static mame.mame.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.capcom.*;
import static sndhrdw.generic.*;
import static vidhrdw.generic.*;
import static machine.gng.*;
import static vidhrdw.gng.*;
import static mame.inptport.*;
import static mame.memoryH.*;
/**
 *
 * @author shadow
 */
public class gng {

static MemoryReadAddress  readmem[] =
{
	new MemoryReadAddress( 0x0000, 0x2fff, MRA_RAM ),
	new MemoryReadAddress( 0x4000, 0x5fff, gng_bankedrom_r ),
        new MemoryReadAddress( 0x6184, 0x6184, gng_catch_loop_r ),
	new MemoryReadAddress( 0x6000, 0xffff, MRA_ROM ),
	new MemoryReadAddress( 0x3c00, 0x3c00, MRA_NOP ),    /* watchdog? */
	new MemoryReadAddress( 0x3000, 0x3000, input_port_0_r ),
	new MemoryReadAddress( 0x3001, 0x3001, input_port_1_r ),
	new MemoryReadAddress( 0x3002, 0x3002, input_port_2_r ),
	new MemoryReadAddress( 0x3003, 0x3003, input_port_3_r ),
	new MemoryReadAddress( 0x3004, 0x3004, input_port_4_r ),
	new MemoryReadAddress( -1 )	/* end of table */
};
static MemoryReadAddress diamond_readmem[] =
{
	new MemoryReadAddress( 0x0000, 0x2fff, MRA_RAM ),
	new MemoryReadAddress( 0x4000, 0x5fff, gng_bankedrom_r ),
	new MemoryReadAddress( 0x6000, 0xffff, MRA_ROM ),
	new MemoryReadAddress( 0x3c00, 0x3c00, MRA_NOP ),    /* watchdog? */
	new MemoryReadAddress( 0x3000, 0x3000, input_port_0_r ),
	new MemoryReadAddress( 0x3001, 0x3001, input_port_1_r ),
	new MemoryReadAddress( 0x3002, 0x3002, input_port_2_r ),
	new MemoryReadAddress( 0x3003, 0x3003, input_port_3_r ),
	new MemoryReadAddress( 0x3004, 0x3004, input_port_4_r ),
	new MemoryReadAddress( -1 )	/* end of table */
};
static MemoryWriteAddress writemem[] =
{
	new MemoryWriteAddress( 0x0000, 0x1dff, MWA_RAM ),
	new MemoryWriteAddress( 0x2000, 0x23ff, videoram_w, videoram, videoram_size ),
	new MemoryWriteAddress( 0x2400, 0x27ff, colorram_w, colorram ),
	new MemoryWriteAddress( 0x2800, 0x2bff, gng_bgvideoram_w, gng_bgvideoram, gng_bgvideoram_size ),
	new MemoryWriteAddress( 0x2c00, 0x2fff, gng_bgcolorram_w, gng_bgcolorram ),
	new MemoryWriteAddress( 0x3c00, 0x3c00, MWA_NOP ),   /* watchdog? */
	new MemoryWriteAddress( 0x3800, 0x39ff, gng_paletteram_w, gng_paletteram ),
	new MemoryWriteAddress( 0x3e00, 0x3e00, gng_bankswitch_w ),
	new MemoryWriteAddress( 0x1e00, 0x1fff, MWA_RAM, spriteram, spriteram_size ),
	new MemoryWriteAddress( 0x3b08, 0x3b09, MWA_RAM, gng_scrollx ),
	new MemoryWriteAddress( 0x3b0a, 0x3b0b, MWA_RAM, gng_scrolly ),
	new MemoryWriteAddress( 0x3a00, 0x3a00, sound_command_w ),
	new MemoryWriteAddress( 0x4000, 0xffff, MWA_ROM ),
	new MemoryWriteAddress( -1 )	/* end of table */
};



static MemoryReadAddress sound_readmem[] =
{
	new MemoryReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
	new MemoryReadAddress( 0xc800, 0xc800, sound_command_latch_r ),
	new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
	new MemoryReadAddress( -1 )	/* end of table */
};

static MemoryWriteAddress sound_writemem[] =
{
	new MemoryWriteAddress( 0xc000, 0xc7ff, MWA_RAM ),
	new MemoryWriteAddress( 0xe000, 0xe000, AY8910_control_port_0_w ),
	new MemoryWriteAddress( 0xe001, 0xe001, AY8910_write_port_0_w ),
	new MemoryWriteAddress( 0xe002, 0xe002, AY8910_control_port_1_w ),
	new MemoryWriteAddress( 0xe003, 0xe003, AY8910_write_port_1_w ),
	new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
	new MemoryWriteAddress( -1 )	/* end of table */
};



static InputPort input_ports[] =
{
	new InputPort(	/* IN0 */
		0xff,
		new int[]{ OSD_KEY_1, OSD_KEY_2, 0, 0, 0, 0, 0, OSD_KEY_3 }

	),
	new InputPort(	/* IN1 */
		0xff,
		new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_DOWN, OSD_KEY_UP,
				OSD_KEY_CONTROL, OSD_KEY_ALT, 0, 0 }

	),
	new InputPort(	/* IN2 */
		0xff,
		new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
	),
	new InputPort(	/* DSW1 */
		0xdf,
		new int[]{ 0, 0, 0, 0, 0, 0, OSD_KEY_F2, 0 }
	),
	new InputPort(	/* DSW2 */
		0xfb,
		new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
	),
	new InputPort( -1 )	/* end of table */
};

       static TrakPort[] trak_ports =
       {
           new TrakPort(-1)
       };

      static KEYSet[] keys =
      {
          new KEYSet(1, 3, "MOVE UP"),
          new KEYSet( 1, 1, "MOVE LEFT" ),
          new KEYSet( 1, 0, "MOVE RIGHT"),
          new KEYSet( 1, 2, "MOVE DOWN"),
          new KEYSet( 1, 4, "FIRE"),
          new KEYSet(1, 5, "JUMP" ),
          new KEYSet(-1) };



static DSW dsw[] =
{
	new DSW( 4, 0x03, "LIVES",new String[] { "7", "5", "4", "3" }, 1 ),
	new DSW( 4, 0x18, "BONUS",new String[] {  "30000 80000", "20000 70000", "30000 80000 80000", "20000 70000 70000" }, 1 ),
	new DSW( 4, 0x60, "DIFFICULTY",new String[] { "HARDEST", "HARD", "EASY", "NORMAL" }, 1 ),
	new DSW( 3, 0x20, "DEMO SOUNDS",new String[] { "ON", "OFF" }, 1 ),
	new DSW( -1 )
};



static GfxLayout charlayout = new GfxLayout
(
	8,8,	/* 8*8 characters */
	1024,	/* 1024 characters */
	2,	/* 2 bits per pixel */
	new int[]{ 4, 0 },
	new int[]{ 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3 },
	new int[]{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
	16*8	/* every char takes 16 consecutive bytes */
);
static GfxLayout tilelayout =new GfxLayout
(
	16,16,	/* 16*16 tiles */
	1024,	/* 1024 tiles */
	3,	/* 3 bits per pixel */
	new int[]{ 2*1024*32*8, 1024*32*8, 0 },	/* the bitplanes are separated */
        new int[]{ 0, 1, 2, 3, 4, 5, 6, 7,
            16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7 },
	new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
			8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
	32*8	/* every tile takes 32 consecutive bytes */
);
static GfxLayout spritelayout =new GfxLayout
(
	16,16,	/* 16*16 sprites */
	768,	/* 768 sprites */
	4,	/* 4 bits per pixel */
	new int[]{ 768*64*8+4, 768*64*8+0, 4, 0 },
        new int[]{ 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3,
	    32*8+0, 32*8+1, 32*8+2, 32*8+3, 33*8+0, 33*8+1, 33*8+2, 33*8+3 },
	new int[]{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
			8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
	64*8	/* every sprite takes 64 consecutive bytes */
);




static GfxDecodeInfo gfxdecodeinfo[] =
{
	new GfxDecodeInfo( 1, 0x00000, charlayout,   8*8+4*16, 16 ),
	new GfxDecodeInfo( 1, 0x04000, tilelayout,          0, 8 ),
	new GfxDecodeInfo( 1, 0x1c000, spritelayout,      8*8, 4 ),
	new GfxDecodeInfo( -1), /* end of array */
};




static MachineDriver machine_driver = new MachineDriver
(
	/* basic machine hardware */
	new MachineCPU[] {
		new MachineCPU(
			CPU_M6809,
			1500000,			/* 1 Mhz */
			0,
			readmem,writemem, null, null,
			gng_interrupt,1
		),
		new MachineCPU(
			CPU_Z80 | CPU_AUDIO_CPU,
			3072000,	/* 3 Mhz ??? */
			2,	/* memory region #2 */
			sound_readmem,sound_writemem, null, null,
			capcom_sh_interrupt,12
		),
	},
	60,
	gng_init_machine,

	/* video hardware */
	32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
	gfxdecodeinfo,
	256, 8*8+4*16+16*4,
	gng_vh_convert_color_prom,
        VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
	null,
	gng_vh_start,
	gng_vh_stop,
	gng_vh_screenrefresh,

	/* sound hardware */
	null,
	null,
	capcom_sh_start,
	AY8910_sh_stop,
	AY8910_sh_update
);
static MachineDriver diamond_machine_driver = new MachineDriver
(

	/* basic machine hardware */
	new MachineCPU[] {
		new MachineCPU(
			CPU_M6809,
			1500000,			/* 1 Mhz */
			0,
			diamond_readmem,writemem,null,null,
			gng_interrupt,1
		),
		new MachineCPU(
			CPU_Z80 | CPU_AUDIO_CPU,
			3072000,	/* 3 Mhz ??? */
			2,	/* memory region #2 */
			sound_readmem,sound_writemem,null,null,
	         	capcom_sh_interrupt,12
		)
	},
	60,
	gng_init_machine,

	/* video hardware */
	32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
	gfxdecodeinfo,
	256, 8*8+4*16+16*4,
	gng_vh_convert_color_prom,

	VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
	null,
	gng_vh_start,
	gng_vh_stop,
	gng_vh_screenrefresh,

	/* sound hardware */
	null,
	null,
	capcom_sh_start,
	AY8910_sh_stop,
	AY8910_sh_update
);

/***************************************************************************

  Game driver(s)

***************************************************************************/
        static RomLoadPtr  gng_rom= new RomLoadPtr(){ public void handler() 
        {

		ROM_REGION(0x1c000);	/* 64k for code */
                ROM_LOAD( "gg3.bin",  0x8000, 0x8000, 0x019eaa7c );
                ROM_LOAD( "gg4.bin",  0x4000, 0x4000, 0xf74cb35c );	/* 4000-5fff is page 0 */
                ROM_RELOAD(          0x18000, 0x4000 );	/* copy for my convenience */
                ROM_LOAD( "gg5.bin", 0x10000, 0x8000, 0xd39c9516 );	/* page 1, 2, 3 e 4 */

                ROM_REGION(0x34000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "gg1.bin",  0x00000, 0x4000, 0x0d95960b );	/* characters */
                ROM_LOAD( "gg11.bin", 0x04000, 0x4000, 0x8425662b );	/* tiles 0-1 Plane 1*/
                ROM_LOAD( "gg10.bin", 0x08000, 0x4000, 0x332b9d85 );	/* tiles 2-3 Plane 1*/
                ROM_LOAD( "gg9.bin",  0x0c000, 0x4000, 0xf6433b0f );	/* tiles 0-1 Plane 2*/
                ROM_LOAD( "gg8.bin",  0x10000, 0x4000, 0x24a66776 );	/* tiles 2-3 Plane 2*/
                ROM_LOAD( "gg7.bin",  0x14000, 0x4000, 0x6e6e3ec0 );	/* tiles 0-1 Plane 3*/
                ROM_LOAD( "gg6.bin",  0x18000, 0x4000, 0x248ddf8b );	/* tiles 2-3 Plane 3*/
                ROM_LOAD( "gg17.bin", 0x1c000, 0x4000, 0x1b48124e );	/* sprites 0 Plane 1-2 */
                ROM_LOAD( "gg16.bin", 0x20000, 0x4000, 0xc29702c5 );	/* sprites 1 Plane 1-2 */
                ROM_LOAD( "gg15.bin", 0x24000, 0x4000, 0x0309f6a7 );	/* sprites 2 Plane 1-2 */
                ROM_LOAD( "gg14.bin", 0x28000, 0x4000, 0x0553b263 );	/* sprites 0 Plane 3-4 */
                ROM_LOAD( "gg13.bin", 0x2c000, 0x4000, 0x3e603938 );	/* sprites 1 Plane 3-4 */
                ROM_LOAD( "gg12.bin", 0x30000, 0x4000, 0x8ec77b4f );	/* sprites 2 Plane 3-4 */

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "gg2.bin", 0x0000, 0x8000, 0x0b7edfd2 ) ;  /* Audio CPU is a Z80 */
                ROM_END();
        }};

/*static RomModule  gngcross_rom[] = {
	new RomModule( null,0x1c000, 0 ),	/* 64k for code */
/*	new RomModule( "gg3.bin",  0x8000, 0x8000, 0x019eaa7c ),
	new RomModule( "gg4.bin",  0x4000, 0x4000, 0xf74cb35c ),	/* 4000-5fff is page 0 */
/*	new RomModule("-1",          0x18000, 0x4000 ),	/* copy for my convenience */
/*	new RomModule( "gg5.bin", 0x10000, 0x8000, 0xd39c9516 ),	/* page 1, 2, 3 e 4 */

/*	new RomModule( null,0x34000, 0 ),	/* temporary space for graphics (disposed after conversion) */
/*	new RomModule( "gg1.bin",  0x00000, 0x4000, 0x0d95960b ),	/* characters */
/*	new RomModule( "gg11.bin", 0x04000, 0x4000, 0x8425662b ),	/* tiles 0-1 Plane 1*/
/*	new RomModule( "gg10.bin", 0x08000, 0x4000, 0x332b9d85 ),	/* tiles 2-3 Plane 1*/
/*	new RomModule( "gg9.bin",  0x0c000, 0x4000, 0xf6433b0f ),	/* tiles 0-1 Plane 2*/
/*	new RomModule( "gg8.bin",  0x10000, 0x4000, 0x24a66776 ),	/* tiles 2-3 Plane 2*/
/*	new RomModule( "gg7.bin",  0x14000, 0x4000, 0x6e6e3ec0 ),	/* tiles 0-1 Plane 3*/
/*	new RomModule( "gg6.bin",  0x18000, 0x4000, 0x248ddf8b ),	/* tiles 2-3 Plane 3*/
/*	new RomModule( "gg17.bin", 0x1c000, 0x4000, 0xb59f663d ),	/* sprites 0 Plane 1-2 */
/*	new RomModule( "gg16.bin", 0x20000, 0x4000, 0xc29702c5 ),	/* sprites 1 Plane 1-2 */
/*	new RomModule( "gg15.bin", 0x24000, 0x4000, 0x0309f6a7 ),	/* sprites 2 Plane 1-2 */
/*	new RomModule( "gg14.bin", 0x28000, 0x4000, 0xce3dc76f ),	/* sprites 0 Plane 3-4 */
/*	new RomModule( "gg13.bin", 0x2c000, 0x4000, 0x3e603938 ),	/* sprites 1 Plane 3-4 */
/*	new RomModule( "gg12.bin", 0x30000, 0x4000, 0x8ec77b4f ),	/* sprites 2 Plane 3-4 */

/*	new RomModule( null,0x10000, 0 ),	/* 64k for the audio CPU */
/*	new RomModule( "gg2.bin", 0x0000, 0x8000, 0x0b7edfd2 ),   /* Audio CPU is a Z80 */
/*new RomModule( null, 0, 0 )	};*/
        static RomLoadPtr  diamond_rom= new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x1c000);	/* 64k for code */
                ROM_LOAD( "d5",  0x00000, 0x8000, 0x89e5a985 );
                ROM_LOAD( "d3",  0x08000, 0x8000, 0x38d5bcc9 );
                ROM_LOAD( "d5o", 0x10000, 0x8000, 0x06f68aa8 );
                ROM_LOAD( "d3o", 0x18000, 0x4000, 0x76c09ea4 );

                ROM_REGION(0x34000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "d1",  0x00000, 0x4000, 0x7da60000 );	/* characters */
                ROM_LOAD( "d11", 0x04000, 0x4000, 0xc592534e );	/* tiles 0-1 Plane 1*/
                ROM_LOAD( "d10", 0x08000, 0x4000, 0x2c5520ed );	/* tiles 2-3 Plane 1*/
                ROM_LOAD( "d9",  0x0c000, 0x4000, 0x0e971c4b );	/* tiles 0-1 Plane 2*/
                ROM_LOAD( "d8",  0x10000, 0x4000, 0x1505a1c7 );	/* tiles 2-3 Plane 2*/
                ROM_LOAD( "d7",  0x14000, 0x4000, 0x5cfe0000 );	/* tiles 0-1 Plane 3*/
                ROM_LOAD( "d6",  0x18000, 0x4000, 0x7428a122 );	/* tiles 2-3 Plane 3*/
                ROM_LOAD( "d17", 0x1c000, 0x4000, 0x821a03ee );	/* sprites 0 Plane 1-2 */
                /* empty space for unused sprites 1 Plane 1-2 */
                /* empty space for unused sprites 2 Plane 1-2 */
                ROM_LOAD( "d14", 0x28000, 0x4000, 0x465e000e );	/* sprites 0 Plane 3-4 */
                /* empty space for unused sprites 1 Plane 3-4 */
                /* empty space for unused sprites 2 Plane 3-4 */

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "d2", 0x0000, 0x8000, 0x0b7edfd2 );   /* Audio CPU is a Z80 */
                ROM_END();
        }};



	static HiscoreLoadPtr gng_hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
	/* get RAM pointer (this game is multiCPU, we can't assume the global */
	/* RAM pointer is pointing to the right place) */
	char []RAM = Machine.memory_region[0];


	/* check if the hi score table has already been initialized */
        if (memcmp(RAM, 0x152c, new char[] { 0x00, 0x01, 0x00,0x00 }, 4) == 0 &&
				memcmp(RAM, 0x0042, new char[] { 0x00, 0x01, 0x00,0x00 }, 4) == 0)
	{

		FILE f;


		if ((f = fopen(name,"rb")) != null)
		{
			int offs;


			fread(RAM,0x1518,1,9*10,f);
			offs = RAM[0x1518] * 256 + RAM[0x1519];
			RAM[0x00d0] = RAM[offs];
			RAM[0x00d1] = RAM[offs+1];
			RAM[0x00d2] = RAM[offs+2];
			RAM[0x00d3] = RAM[offs+3];
			fclose(f);
		}

		return 1;
	}
	else 
            return 0;	/* we can't load the hi scores yet */
} };



static HiscoreSavePtr gng_hisave = new HiscoreSavePtr() { public void handler(String name)
	{
	/* get RAM pointer (this game is multiCPU, we can't assume the global */
	/* RAM pointer is pointing to the right place) */
	char []RAM = Machine.memory_region[0];
	FILE f;
	if ((f = fopen(name,"wb")) != null)
	{
		fwrite(RAM,0x1518,1,9*10,f);
		fclose(f);
	}
} };
static HiscoreLoadPtr diamond_hiload = new HiscoreLoadPtr() { public int handler(String name)
{

	/* get RAM pointer (this game is multiCPU, we can't assume the global */
	/* RAM pointer is pointing to the right place) */
	char []RAM = Machine.memory_region[0];

	/* We're just going to blast the hi score table into ROM and be done with it */
        if (memcmp(RAM,0xC10E,"KLE",3) == 0)
	{
		FILE f;


		if ((f = fopen(name,"rb")) != null)
		{
                        fread(RAM,0xC10E,1,0x80,f);
			fclose(f);
		}

		return 1;
	}
	else return 0;	/* we can't load the hi scores yet */
}};


static HiscoreSavePtr diamond_hisave = new HiscoreSavePtr() { public void handler(String name)
{
	FILE f;

	/* get RAM pointer (this game is multiCPU, we can't assume the global */
	/* RAM pointer is pointing to the right place) */
	char []RAM = Machine.memory_region[0];


	if ((f = fopen(name,"wb")) != null)
	{
		/* The RAM location of the hi score table */
                fwrite(RAM,0x105F,1,0x80,f);
		fclose(f);
	}
}};

public static GameDriver gng_driver =new GameDriver
(
	"Ghosts'n Goblins",
	"gng",
	"ROBERTO VENTURA\nMIRKO BUFFONI\nNICOLA SALMORIA\nGABRIO SECCO",
	machine_driver,

	gng_rom,
	null, null,
	null,

	input_ports,null, trak_ports, dsw, keys,

	 null, null, null,
	ORIENTATION_DEFAULT,

	gng_hiload, gng_hisave
);

/*public static GameDriver gngcross_driver =new GameDriver
(
	"Ghosts'n Goblins (Cross)",
	"gngcross",
	"ROBERTO VENTURA\nMIRKO BUFFONI\nNICOLA SALMORIA\nGABRIO SECCO",
	machine_driver,

	gngcross_rom,
	null, null,
		null,

	input_ports, trak_ports, dsw, keys,

	gng_color_prom, null, null,
	8*13, 8*16,

	gng_hiload, gng_hisave
);*/

public static GameDriver diamond_driver =new GameDriver
(
	"Diamond Run",
	"diamond",
	"ROBERTO VENTURA\nMIRKO BUFFONI\nNICOLA SALMORIA",
	diamond_machine_driver,

	diamond_rom,
	null, null,
	null,

	input_ports,null, trak_ports, dsw, keys,

	null, null, null,
	ORIENTATION_DEFAULT,

	diamond_hiload, diamond_hisave
);

}
