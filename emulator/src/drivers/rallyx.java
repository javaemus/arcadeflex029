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
 *NOTES: romsets are from v0.36 roms
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
import static sndhrdw.pengo.*;
import static vidhrdw.generic.*;
import static vidhrdw.rallyx.*;
import static arcadeflex.osdepend.*;
import static mame.memoryH.*;

public class rallyx
{
        static int counter=0;
        public static WriteHandlerPtr rallyx_play_sound_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
             /*   
                if (play_sound == 0 || Machine.samples.sample[0] == null)
                        return;

                if (data == 0 && counter > 0)
                {
                  osd_play_sample(4,Machine.samples.sample[0].data,
                                  Machine.samples.sample[0].length,
                                  Machine.samples.sample[0].smpfreq,
                                  Machine.samples.sample[0].volume,0);
                }
                counter = data;*/
        }};
	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x8000, 0x8fff, MRA_RAM ),
		new MemoryReadAddress( 0x9800, 0x9fff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new MemoryReadAddress( 0xa000, 0xa000, input_port_0_r ),	/* IN0 */
		new MemoryReadAddress( 0xa080, 0xa080, input_port_1_r ),	/* IN1 */
		new MemoryReadAddress( 0xa100, 0xa100, input_port_2_r ),	/* DSW1 */
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress writemem[] =
	{
                new MemoryWriteAddress( 0x8000, 0x83ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x8400, 0x87ff, rallyx_videoram2_w, rallyx_videoram2 ),
                new MemoryWriteAddress( 0x8800, 0x8bff, colorram_w, colorram ),
                new MemoryWriteAddress( 0x8c00, 0x8fff, rallyx_colorram2_w, rallyx_colorram2 ),
                new MemoryWriteAddress( 0x9800, 0x9fff, MWA_RAM ),
                new MemoryWriteAddress( 0xa080, 0xa080, MWA_NOP ),
                new MemoryWriteAddress( 0xa130, 0xa130, MWA_RAM, rallyx_scrollx ),
                new MemoryWriteAddress( 0xa140, 0xa140, MWA_RAM, rallyx_scrolly ),
                new MemoryWriteAddress( 0xa004, 0xa00c, MWA_RAM, rallyx_radarcarcolor ),
                new MemoryWriteAddress( 0xa100, 0xa11f, pengo_sound_w, pengo_soundregs ),
                new MemoryWriteAddress( 0xa170, 0xa170, MWA_NOP ),	/* ????? */
                new MemoryWriteAddress( 0xa180, 0xa180, rallyx_play_sound_w ),
                new MemoryWriteAddress( 0xa181, 0xa181, interrupt_enable_w ),
                new MemoryWriteAddress( 0xa182, 0xa186, MWA_NOP ),
                new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
                new MemoryWriteAddress( 0x8014, 0x801f, MWA_RAM, spriteram ),	/* these are here just to initialize */
                new MemoryWriteAddress( 0x8814, 0x881f, MWA_RAM, spriteram_2 ),	/* the pointers. */
                new MemoryWriteAddress( 0x8034, 0x803c, MWA_RAM, rallyx_radarcarx ),	/* ditto */
                new MemoryWriteAddress( 0x8834, 0x883c, MWA_RAM, rallyx_radarcary ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};

	static IOWritePort writeport[] =
	{
		new IOWritePort( 0, 0, interrupt_vector_w ),
		new IOWritePort( -1 )	/* end of table */
	};



	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { OSD_KEY_3, OSD_KEY_CONTROL, OSD_KEY_LEFT, OSD_KEY_RIGHT,
				OSD_KEY_DOWN, OSD_KEY_UP, OSD_KEY_1, 0 }
		),
		new InputPort(	/* IN1 */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, OSD_KEY_2, 0 }
		),
		new InputPort(	/* DSW1 */
			0xcb,
			new int[] { OSD_KEY_F2, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};



	static DSW dsw[] =
	{
		new DSW( 2, 0x38, "DIFFICULTY", new String[] { "2 CARS, RANK A", "3 CARS, RANK A", "1 CAR , RANK B", "2 CARS, RANK B",
			"3 CARS, RANK B", "1 CAR , RANK C", "2 CARS, RANK C", "3 CARS, RANK C" } ),
		new DSW( 2, 0x02, "BONUS", new String[] { "OFF", "ON" } ),
		new DSW( 2, 0x04, "BONUS SCORE", new String[] { "LOW", "HIGH" } ),
		new DSW( -1 )
	};

       static TrakPort[] trak_ports =
       {
           new TrakPort(-1)
       };

      static KEYSet[] keys =
      {
          new KEYSet(0, 5, "MOVE UP" ),
          new KEYSet(0, 2, "MOVE LEFT"  ),
          new KEYSet(0, 3, "MOVE RIGHT"),
          new KEYSet(0, 4, "MOVE DOWN" ),
          new KEYSet(0, 1, "SMOKE"),
          new KEYSet(-1) };

	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3 },	/* bits are packed in groups of four */
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		16*8	/* every char takes 16 bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		64,	/* 64 sprites */
		2,	/* 2 bits per pixel */
		new int[] { 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 8*8+0, 8*8+1, 8*8+2, 8*8+3, 16*8+0, 16*8+1, 16*8+2, 16*8+3,	/* bits are packed in groups of four */
				 24*8+0, 24*8+1, 24*8+2, 24*8+3, 0, 1, 2, 3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				32*8, 33*8, 34*8, 35*8, 36*8, 37*8, 38*8, 39*8 },
		64*8	/* every sprite takes 64 bytes */
	);

        static GfxLayout radardotlayout = new GfxLayout
        (
                /* there is no gfx ROM for this one, it is generated by the hardware */
                2,2,	/* 2*2 square */
                1,	/* just one */
                1,	/* 1 bit per pixel */
                new int[]{ 0 },
                new int[]{ 0, 0 },	/* I "know" that this bit is 1 */
                new int[]{ 0, 0 },	/* I "know" that this bit is 1 */
                0	/* no use */
        );

	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,   0, 32 ),
		new GfxDecodeInfo( 1, 0x0000, spritelayout, 0, 32 ),
                new GfxDecodeInfo( 1, 0x0000, radardotlayout, 0, 128 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};



	static char palette[] =
	{
		0x00,0x00,0x00,	/* BLACK */
		0xff,0xff,0xff,	/* WHITE */
		0xff,0x00,0x00,	/* RED */
		0x00,0xff,0x00,	/* GREEN */
		0x00,0x00,0xff,	/* BLUE */
		0xff,0xff,0x00,	/* YELLOW */
		0xff,0x00,0xff,	/* PURPLE */
		0xaa,0xaa,0xaa,	/* LTGRAY */
		0x88,0x88,0x88	/* DKGRAY */
	};

	static final int BLACK = 0;
	static final int WHITE = 1;
	static final int RED = 2;
	static final int GREEN = 3;
	static final int BLUE = 4;
	static final int YELLOW = 5;
	static final int PURPLE = 6;
	static final int LTGRAY = 7;
	static final int DKGRAY = 8;

	static char colortable[] =
	{
		BLACK,BLACK,BLACK,BLACK,
		BLACK,4,1,8,	/* enemy cars */
		BLACK,2,1,8,	/* player's car */
		BLACK,3,1,7,	/* radar */
		BLACK,5,7,1,	/* flag and score explanation on title screen */
		BLACK,2,7,1,	/* smoke */
		BLACK,2,7,1,	/* "HISCORE"; BANG */
		BLACK,7,7,7,	/* "MIDWAY" */
		BLACK,3,1,7,	/* left side of fuel bar */
		BLACK,7,1,3,	/* text on intro screen; fuel bar */
		BLACK,7,1,2,	/* "INSTRUCTIONS"; right side of fuel bar */
		BLACK,7,8,1,	/* "FUEL" */
		4,4,4,4,
		BLACK,3,7,8,	/* walls */
		BLACK,6,7,8,	/* 4th level walls */
		0,3,2,1,		/* blinking hi score after record */
		BLACK,7,7,7,		/* hi score */
		BLACK,8,2,3,	/* trees around the circuit */
		BLACK,1,2,3,	/* player's score; 4th level flowers */
		BLACK,3,3,3,	/* "(c) MIDWAY 1980" */
		0,4,4,4,		/* "THE HIGH SCORE OF THE DAY" */
		BLACK,5,7,6,	/* rocks */
		BLACK,5,7,2,	/* road, flags */
		7,7,7,7,
		7,7,7,7,
		1,1,1,1,
		2,2,2,2,
		3,3,3,3,
		4,4,4,4,
		5,5,5,5,
		6,6,6,6,
		7,7,7,7
	};



	/* waveforms for the audio hardware */
        /* these are NOT the correct waveforms (I'm using Galaga ones) */
	static char samples[] =
	{
	0xff,0x11,0x22,0x33,0x44,0x55,0x55,0x66,0x66,0x66,0x55,0x55,0x44,0x33,0x22,0x11,
	0xff,0xdd,0xcc,0xbb,0xaa,0x99,0x99,0x88,0x88,0x88,0x99,0x99,0xaa,0xbb,0xcc,0xdd,

	0xff,0x11,0x22,0x33,0xff,0x55,0x55,0xff,0x66,0xff,0x55,0x55,0xff,0x33,0x22,0x11,
	0xff,0xdd,0xff,0xbb,0xff,0x99,0xff,0x88,0xff,0x88,0xff,0x99,0xff,0xbb,0xff,0xdd,

	0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,0x66,
	0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,

	0x33,0x55,0x66,0x55,0x44,0x22,0x00,0x00,0x00,0x22,0x44,0x55,0x66,0x55,0x33,0x00,
	0xcc,0xaa,0x99,0xaa,0xbb,0xdd,0xff,0xff,0xff,0xdd,0xbb,0xaa,0x99,0xaa,0xcc,0xff,

	0xff,0x22,0x44,0x55,0x66,0x55,0x44,0x22,0xff,0xcc,0xaa,0x99,0x88,0x99,0xaa,0xcc,
	0xff,0x33,0x55,0x66,0x55,0x33,0xff,0xbb,0x99,0x88,0x99,0xbb,0xff,0x66,0xff,0x88,

	0xff,0x66,0x44,0x11,0x44,0x66,0x22,0xff,0x44,0x77,0x55,0x00,0x22,0x33,0xff,0xaa,
	0x00,0x55,0x11,0xcc,0xdd,0xff,0xaa,0x88,0xbb,0x00,0xdd,0x99,0xbb,0xee,0xbb,0x99,

	0xff,0x00,0x22,0x44,0x66,0x55,0x44,0x44,0x33,0x22,0x00,0xff,0xdd,0xee,0xff,0x00,
	0x00,0x11,0x22,0x33,0x11,0x00,0xee,0xdd,0xcc,0xcc,0xbb,0xaa,0xcc,0xee,0x00,0x11,

	0x22,0x44,0x44,0x22,0xff,0xff,0x00,0x33,0x55,0x66,0x55,0x22,0xee,0xdd,0xdd,0xff,
	0x11,0x11,0x00,0xcc,0x99,0x88,0x99,0xbb,0xee,0xff,0xff,0xcc,0xaa,0xaa,0xcc,0xff,
	};



	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz ? */
				0,
				readmem, writemem, null, writeport,
				interrupt, 1
			)
		},
		60,
		null,

		/* video hardware */
		36*8, 28*8, new rectangle( 0*8, 36*8-1, 0*8, 28*8-1 ),
		gfxdecodeinfo,
		sizeof(palette) / 3, sizeof(colortable),
		null,
                VIDEO_TYPE_RASTER,
		null,
		rallyx_vh_start,
		rallyx_vh_stop,
		rallyx_vh_screenrefresh,

		/* sound hardware */
		samples,
		null,
		rallyx_sh_start,
		null,
		pengo_sh_update
	);

        static String rallyx_sample_names[] =
        {
                "BANG.SAM",
                null	/* end of array */
        };
	/***************************************************************************

	  Game driver(s)

	***************************************************************************/
        static RomLoadPtr rallyx_rom= new RomLoadPtr(){ public void handler()  
        {
		 ROM_REGION(0x10000);	/* 64k for code */
		 ROM_LOAD("1b",           0x0000, 0x1000, 0x5882700d );
		 ROM_LOAD("rallyxn.1e",   0x1000, 0x1000, 0xed1eba2 );
		 ROM_LOAD("rallyxn.1h",   0x2000, 0x1000, 0x4f98dd1c );
		 ROM_LOAD("rallyxn.1k",   0x3000, 0x1000, 0x9aacccf0 );

		 ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		 ROM_LOAD("8e",           0x0000, 0x1000, 0x277c1de5 );
                ROM_END();
        }};

	public static GameDriver rallyx_driver = new GameDriver
	(
                "Rally X",
		"rallyx",
                "NICOLA SALMORIA\nMIRKO BUFFONI",
		machine_driver,

		rallyx_rom,
		null, null,
		rallyx_sample_names,

		input_ports,null, trak_ports, dsw, keys,

		null, palette, colortable,
		ORIENTATION_DEFAULT,

		null, null
	);
}