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
 * ported to v0.29
 * ported to v0.28
 * ported to v0.27
 *
 *NOTES: romsets are from v0.36 roms
 *
 */

package drivers;


import static arcadeflex.libc.*;
import static arcadeflex.osdepend.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.generic.*;
import static sndhrdw.bombjack.*;
import static vidhrdw.generic.*;
import static vidhrdw.bombjack.*;
import static mame.memoryH.*;

public class bombjack
{

	static MemoryReadAddress readmem[] =
	{
		
		new MemoryReadAddress( 0xb003, 0xb003, MRA_NOP ),
		new MemoryReadAddress( 0xb000, 0xb000, input_port_0_r ),	/* player 1 input */
		new MemoryReadAddress( 0xb001, 0xb001, input_port_1_r ),	/* player 2 input */
		new MemoryReadAddress( 0xb002, 0xb002, input_port_2_r ),	/* coin */
		new MemoryReadAddress( 0xb004, 0xb004, input_port_3_r ),	/* DSW1 */
		new MemoryReadAddress( 0xb005, 0xb005, input_port_4_r ),	/* DSW2 */
                new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new MemoryReadAddress( 0x8000, 0x97ff, MRA_RAM ),	/* including video and color RAM */
		new MemoryReadAddress( 0xc000, 0xdfff, MRA_ROM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x9820, 0x987f, MWA_RAM, spriteram ,spriteram_size  ),
		new MemoryWriteAddress( 0x9c00, 0x9cff, bombjack_paletteram_w, bombjack_paletteram ),
		new MemoryWriteAddress( 0xb000, 0xb000, interrupt_enable_w ),
		new MemoryWriteAddress( 0x9e00, 0x9e00, bombjack_background_w ),
		new MemoryWriteAddress( 0xb800, 0xb800, sound_command_w ),
                new MemoryWriteAddress( 0x9a00, 0x9a00, MWA_NOP ),
                new MemoryWriteAddress( 0x8000, 0x8fff, MWA_RAM ),
                new MemoryWriteAddress( 0x9000, 0x93ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x9400, 0x97ff, colorram_w, colorram ),
		new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new MemoryWriteAddress( 0xc000, 0xdfff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	static MemoryReadAddress bombjack_sound_readmem[] =
	{
		//new MemoryReadAddress( 0x4390, 0x4390, bombjack_sh_intflag_r ),	/* kludge to speed up the emulation */
		new MemoryReadAddress( 0x2000, 0x5fff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new MemoryReadAddress( 0x6000, 0x6000, sound_command_r ),
		new MemoryReadAddress( -1 )  /* end of table */
	};

	static MemoryWriteAddress bombjack_sound_writemem[] =
	{
		new MemoryWriteAddress( 0x2000, 0x5fff, MWA_RAM ),
		new MemoryWriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};


	static IOWritePort bombjack_sound_writeport[] =
	{
		new IOWritePort( 0x00, 0x00, AY8910_control_port_0_w ),
		new IOWritePort( 0x01, 0x01, AY8910_write_port_0_w ),
		new IOWritePort( 0x10, 0x10, AY8910_control_port_1_w ),
		new IOWritePort( 0x11, 0x11, AY8910_write_port_1_w ),
		new IOWritePort( 0x80, 0x80, AY8910_control_port_2_w ),
		new IOWritePort( 0x81, 0x81, AY8910_write_port_2_w ),
		new IOWritePort( -1 )	/* end of table */
	};
	
        static TrakPort[] trak_ports =
        {
           new TrakPort(-1)
        };


      static KEYSet[] keys =
      {
        new KEYSet( 0, 2, "MOVE UP" ),
        new KEYSet( 0, 1, "MOVE LEFT"  ),
        new KEYSet( 0, 0, "MOVE RIGHT" ),
        new KEYSet( 0, 3, "MOVE DOWN" ),
        new KEYSet( 0, 4, "JUMP" ),
        new KEYSet(-1) };

	
	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0x00,
			new int[] { OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_UP, OSD_KEY_DOWN,
					OSD_KEY_CONTROL, 0, 0, 0 }
		),
		new InputPort(	/* IN1 */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* IN2 */
			0x00,
			new int[] { OSD_KEY_4, OSD_KEY_3, OSD_KEY_1, OSD_KEY_2, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW1 */
			0xc0,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* DSW2 */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )  /* end of table */
	};
	
	
	
	static DSW dsw[] =
	{
                new DSW( 3, 0x30, "LIVES", new String[] { "3", "4", "5", "2" } ),
                new DSW( 4, 0x18, "DIFFICULTY 1 (BIRD)", new String[] { "EASY", "MEDIUM", "HARD", "HARDEST" } ),
                new DSW( 4, 0x60, "DIFFICULTY 2", new String[] { "MEDIUM", "EASY", "HARD", "HARDEST" } ),
                        /* not a mistake, MEDIUM and EASY are swapped */
                new DSW( 4, 0x80, "SPECIAL", new String[] { "EASY", "HARD" } ),
                new DSW( 3, 0x40, "CABINET", new String[] { "COCKTAIL", "UPRIGHT" } ),
                new DSW( 3, 0x80, "DEMO SOUNDS", new String[] { "OFF", "ON" } ),
                new DSW( 4, 0x07, "INITIAL HIGH SCORE", new String[] { "10000", "100000", "30000", "50000", "100000", "50000", "100000", "50000" } ),
                new DSW( 3, 0x03, "COIN 1", new String[] { "1/1", "1/2", "1/3", "1/6" } ),
                new DSW( 3, 0x0c, "COIN 2", new String[] { "1/1", "2/1", "1/2", "1/3" } ),

                new DSW( -1 )
	};
	
	
	
	static GfxLayout charlayout1 = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		512,	/* 512 characters */
		3,	/* 3 bits per pixel */
		new int[] { 0, 512*8*8, 2*512*8*8 },	/* the bitplanes are separated */
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },	/* pretty straightforward layout */
		8*8	/* every char takes 8 consecutive bytes */
	);
	
	static GfxLayout charlayout2 = new GfxLayout
	(
		16,16,	/* 16*16 characters */
		256,	/* 256 characters */
		3,	/* 3 bits per pixel */
		new int[] { 0, 1024*8*8, 2*1024*8*8 },	/* the bitplanes are separated */
		new int[] { 23*8, 22*8, 21*8, 20*8, 19*8, 18*8, 17*8, 16*8,
				7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,	/* pretty straightforward layout */
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		32*8	/* every character takes 32 consecutive bytes */
	);
	
	static GfxLayout spritelayout1 = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		128,	/* 128 sprites */
		3,	/* 3 bits per pixel */
		new int[] { 0, 1024*8*8, 2*1024*8*8 },	/* the bitplanes are separated */
		new int[] { 23*8, 22*8, 21*8, 20*8, 19*8, 18*8, 17*8, 16*8,
				7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,	/* pretty straightforward layout */
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		32*8	/* every sprite takes 32 consecutive bytes */
	);
	
	static GfxLayout spritelayout2 = new GfxLayout
	(
		32,32,	/* 32*32 sprites */
		32,	/* 32 sprites */
		3,	/* 3 bits per pixel */
		new int[] { 0, 1024*8*8, 2*1024*8*8 },	/* the bitplanes are separated */
		new int[] { 87*8, 86*8, 85*8, 84*8, 83*8, 82*8, 81*8, 80*8,
				71*8, 70*8, 69*8, 68*8, 67*8, 66*8, 65*8, 64*8,
				23*8, 22*8, 21*8, 20*8, 19*8, 18*8, 17*8, 16*8,
				7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,	/* pretty straightforward layout */
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7,
				32*8+0, 32*8+1, 32*8+2, 32*8+3, 32*8+4, 32*8+5, 32*8+6, 32*8+7,
				40*8+0, 40*8+1, 40*8+2, 40*8+3, 40*8+4, 40*8+5, 40*8+6, 40*8+7 },
		128*8	/* every sprite takes 128 consecutive bytes */
	);

	
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout1,   0, 16 ),	/* characters */
		new GfxDecodeInfo( 1, 0x3000, charlayout2,   0, 16 ),	/* background tiles */
		new GfxDecodeInfo( 1, 0x9000, spritelayout1, 0, 16 ),	/* normal sprites */
		new GfxDecodeInfo( 1, 0xa000, spritelayout2, 0, 16 ),	/* large sprites */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				4000000,	/* 4 Mhz */
				0,
				readmem, writemem, null, null,
				nmi_interrupt, 1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				3072000,	/* 3.072 Mhz????? */
				3,	/* memory region #3 */
				bombjack_sound_readmem, bombjack_sound_writemem, null, bombjack_sound_writeport,
				bombjack_sh_interrupt,10
			)
		},
		60,
                10,	/* 10 CPU slices per frame - enough for the sound CPU to read all commands */
		null,
	
		/* video hardware */
		32*8, 32*8, new rectangle( 2*8, 30*8-1, 0, 32*8-1 ),
		gfxdecodeinfo,
		128, 16*8,
		bombjack_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE|VIDEO_SUPPORTS_DIRTY,
                
		null,
		generic_vh_start,
		generic_vh_stop,
		bombjack_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		bombjack_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);



	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
        static RomLoadPtr bombjack_rom= new RomLoadPtr(){ public void handler() {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "09_j01b.bin",  0x0000, 0x2000, 0xc668dc30 );
		ROM_LOAD( "10_l01b.bin",  0x2000, 0x2000, 0x52a1e5fb );
		ROM_LOAD( "11_m01b.bin",  0x4000, 0x2000, 0xb68a062a );
		ROM_LOAD( "12_n01b.bin",  0x6000, 0x2000, 0x1d3ecee5 );
		ROM_LOAD( "13.1r",        0xc000, 0x2000, 0x70e0244d  );

		ROM_REGION(0xf000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "03_e08t.bin",  0x0000, 0x1000 , 0x9f0470d5 );	/* chars */
		ROM_LOAD( "04_h08t.bin",  0x1000, 0x1000 , 0x81ec12e6 );
		ROM_LOAD( "05_k08t.bin",  0x2000, 0x1000 , 0xe87ec8b1 );
		ROM_LOAD( "06_l08t.bin",  0x3000, 0x2000 , 0x51eebd89 );	/* background tiles */
		ROM_LOAD( "07_n08t.bin",  0x5000, 0x2000 , 0x9dd98e9d  );
		ROM_LOAD( "08_r08t.bin",  0x7000, 0x2000 , 0x3155ee7d );
		ROM_LOAD( "16_m07b.bin",  0x9000, 0x2000 , 0x94694097 );	/* sprites */
		ROM_LOAD( "15_l07b.bin",  0xb000, 0x2000 , 0x013f58f2 );
		ROM_LOAD( "14_j07b.bin",  0xd000, 0x2000 , 0x101c858d );

		ROM_REGION(0x1000);	/* background graphics */
		ROM_LOAD( "02_p04t.bin",  0x0000, 0x1000 , 0x398d4a02 );

		ROM_REGION(0x10000);	/* 64k for sound board */
		ROM_LOAD( "01_h03t.bin",  0x0000, 0x2000, 0x8407917d );
                               
                ROM_END();
        }};



	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
	{
		/* get RAM pointer (this game is multiCPU, we can't assume the global */
		/* RAM pointer is pointing to the right place) */
		char []RAM = Machine.memory_region[0];


		/* check if the hi score table has already been initialized */
                      if (memcmp(RAM,0x8100,RAM,0x8124,4) == 0 &&
			memcmp(RAM,0x8100,RAM,0x80e2,4) == 0 &&	/* high score */
			(memcmp(RAM,0x8100,new char[] { 0x00,0x00,0x01,0x00 },4) == 0 ||
			memcmp(RAM,0x8100,new char[] {0x00,0x00,0x03,0x00 },4) == 0 ||
			memcmp(RAM,0x8100,new char[] {0x00,0x00,0x05,0x00 },4) == 0 ||
			memcmp(RAM,0x8100,new char[] {0x00,0x00,0x10,0x00 },4) == 0))
                      {
			FILE f;
			if ((f = osd_fopen(Machine.gamedrv.name,null,OSD_FILETYPE_HIGHSCORE,0)) != null)
			{
				String buf;
				int hi;
	
	
				osd_fread(f,RAM, 0x8100,15*10);
				RAM[0x80e2] = RAM[0x8100];
				RAM[0x80e3] = RAM[0x8101];
				RAM[0x80e4] = RAM[0x8102];
				RAM[0x80e5] = RAM[0x8103];
				/* also copy the high score to the screen, otherwise it won't be */
				/* updated until a new game is started */
				hi = (RAM[0x8100] & 0x0f) +
						(RAM[0x8100] >> 4) * 10 +
						(RAM[0x8101] & 0x0f) * 100 +
						(RAM[0x8101] >> 4) * 1000 +
						(RAM[0x8102] & 0x0f) * 10000 +
						(RAM[0x8102] >> 4) * 100000 +
						(RAM[0x8103] & 0x0f) * 1000000 +
						(RAM[0x8103] >> 4) * 10000000;
				String str = sprintf("%8d", new Object[] { Integer.valueOf(hi) });
                                videoram_w.handler(0x013f,str.charAt(0));
                                videoram_w.handler(0x011f,str.charAt(1));
                                videoram_w.handler(0x00ff,str.charAt(2));
                                videoram_w.handler(0x00df,str.charAt(3));
                                videoram_w.handler(0x00bf,str.charAt(4));
                                videoram_w.handler(0x009f,str.charAt(5));
                                videoram_w.handler(0x007f,str.charAt(6));
                                videoram_w.handler(0x005f,str.charAt(7));
				osd_fclose(f);
			}
	
			return 1;
		}
		else return 0;	/* we can't load the hi scores yet */
	} };



	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
	{
		FILE f;
		/* get RAM pointer (this game is multiCPU, we can't assume the global */
		/* RAM pointer is pointing to the right place) */
		char []RAM = Machine.memory_region[0];
	
	
		if ((f = osd_fopen(Machine.gamedrv.name,null,OSD_FILETYPE_HIGHSCORE,1)) != null)
		{
			osd_fwrite(f,RAM, 0x8100,15*10);
			osd_fclose(f);
		}
	} };



	public static GameDriver bombjack_driver = new GameDriver(
                "Bomb Jack",
		"bombjack",
                "BRAD THOMAS\nJAKOB FRENDSEN\nCONNY MELIN\nMIRKO BUFFONI\nNICOLA SALMORIA\nJAREK BURCZYNSKI",
		machine_driver,
		bombjack_rom,
		null, null,
		null,
		input_ports,null, trak_ports, dsw, keys,
                null,null,null,
	        ORIENTATION_DEFAULT,
		hiload, hisave
	);
}
