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
 *
 * NOTES: romsets are from v0.36 roms
 *
 */

package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static sndhrdw.dkong.*;
import static vidhrdw.generic.*;
import static vidhrdw.dkong.*;
import static mame.inptport.*;
import static mame.memoryH.*;
public class dkong {
    
        static MemoryReadAddress readmem[] =
	{
                new MemoryReadAddress( 0x0000, 0x5fff, MRA_ROM ),	/* DK: 0000-3fff */
                new MemoryReadAddress( 0x6000, 0x6fff, MRA_RAM ),	/* including sprites RAM */
                new MemoryReadAddress( 0x7400, 0x77ff, MRA_RAM ),	/* video RAM */
                new MemoryReadAddress( 0x7c00, 0x7c00, input_port_0_r ),	/* IN0 */
                new MemoryReadAddress( 0x7c80, 0x7c80, input_port_1_r ),	/* IN1 */
                new MemoryReadAddress( 0x7d00, 0x7d00, input_port_2_r ),	/* IN2/DSW2 */
                new MemoryReadAddress( 0x7d80, 0x7d80, input_port_3_r ),	/* DSW1 */
                new MemoryReadAddress( 0x8000, 0x9fff, MRA_ROM ),	/* DK3 only */
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress dkong_writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x5fff, MWA_ROM ),
                new MemoryWriteAddress( 0x6000, 0x68ff, MWA_RAM ),
                new MemoryWriteAddress( 0x6900, 0x6a7f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x6a80, 0x6fff, MWA_RAM ),
                new MemoryWriteAddress( 0x7400, 0x77ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x7800, 0x7803, MWA_RAM ),	/* ???? */
                new MemoryWriteAddress( 0x7808, 0x7808, MWA_RAM ),	/* ???? */
                new MemoryWriteAddress( 0x7c00, 0x7c00, dkong_sh2_w ),    	/* ???? */
                new MemoryWriteAddress( 0x7c80, 0x7c80, dkongjr_gfxbank_w ),
                new MemoryWriteAddress( 0x7d00, 0x7d07, dkong_sh1_w ),    /* ???? */
                new MemoryWriteAddress( 0x7d80, 0x7d80, dkong_sh3_w ),
                new MemoryWriteAddress( 0x7d81, 0x7d83, MWA_RAM ),	/* ???? */
                new MemoryWriteAddress( 0x7d84, 0x7d84, interrupt_enable_w ),
                new MemoryWriteAddress( 0x7d85, 0x7d85, MWA_RAM ),
                new MemoryWriteAddress( 0x7d86, 0x7d87, dkong_palettebank_w ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };
        static MemoryWriteAddress dkongjr_writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x5fff, MWA_ROM ),
                new MemoryWriteAddress( 0x6000, 0x68ff, MWA_RAM ),
                new MemoryWriteAddress( 0x6900, 0x6a7f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x6a80, 0x6fff, MWA_RAM ),
                new MemoryWriteAddress( 0x7400, 0x77ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x7800, 0x7803, MWA_RAM ),	/* ???? */
                new MemoryWriteAddress( 0x7808, 0x7808, MWA_RAM ),	/* ???? */
                new MemoryWriteAddress( 0x7c00, 0x7c00, MWA_RAM ),	/* ???? */
                new MemoryWriteAddress( 0x7c80, 0x7c80, dkongjr_gfxbank_w ),
                new MemoryWriteAddress( 0x7d00, 0x7d07, MWA_RAM ),	/* ???? */
                new MemoryWriteAddress( 0x7d80, 0x7d83, MWA_RAM ),	/* ???? */
                new MemoryWriteAddress( 0x7d84, 0x7d84, interrupt_enable_w ),
                new MemoryWriteAddress( 0x7d85, 0x7d87, MWA_RAM ),	/* ???? */
                new MemoryWriteAddress( -1 )	/* end of table */
        };
        static MemoryWriteAddress dkong3_writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x5fff, MWA_ROM ),
                new MemoryWriteAddress( 0x6000, 0x68ff, MWA_RAM ),
                new MemoryWriteAddress( 0x6900, 0x6a7f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x6a80, 0x6fff, MWA_RAM ),
                new MemoryWriteAddress( 0x7400, 0x77ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x7e81, 0x7e81, dkong3_gfxbank_w ),
                new MemoryWriteAddress( 0x7e84, 0x7e84, interrupt_enable_w ),
                new MemoryWriteAddress( 0x8000, 0x9fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



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
			new int[] { 0, 0, OSD_KEY_1, OSD_KEY_2, 0, 0, 0, OSD_KEY_3 }
		),
		new InputPort(	/* DSW1 */
			0x84,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};

        static InputPort dkong3_input_ports[] =
        {
                new InputPort(	/* IN0 */
                        0x00,
                        new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_UP, OSD_KEY_DOWN,
                                        OSD_KEY_CONTROL, OSD_KEY_1, OSD_KEY_2, OSD_KEY_F1 }
                ),
		new InputPort(	/* IN1 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, OSD_KEY_3, 0, 0 }
                ),
		new InputPort(	/* DSW2 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* DSW1 */
                        0x00,
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
          new KEYSet(0, 2, "MOVE UP"),
          new KEYSet(0, 1, "MOVE LEFT"  ),
          new KEYSet( 0, 0, "MOVE RIGHT"),
          new KEYSet( 0, 3, "MOVE DOWN" ),
          new KEYSet(0, 4, "JUMP"),
          new KEYSet(-1) 
      };
        static DSW dsw[] =
	{
		new DSW( 3, 0x03, "LIVES", new String[] { "3", "4", "5", "6" } ),
		new DSW( 3, 0x0c, "BONUS", new String[] { "7000", "10000", "15000", "20000" } ),
		new DSW( -1 )
	};

        static DSW dkong3_dsw[] =
	{
		new DSW( 3, 0x03, "LIVES", new String[] { "3", "4", "5", "6" } ),
		new DSW( 3, 0x0c, "BONUS", new String[] { "30000", "40000", "50000", "NONE" } ),
		new DSW( 3, 0x30, "ADDITIONAL BONUS", new String[] { "30000", "40000", "50000", "NONE" } ),
		new DSW( 3, 0xc0, "DIFFICULTY", new String[] { "EASY", "MEDIUM", "HARD", "HARDEST" } ),
		new DSW( -1 )
	};


        static  GfxLayout dkong_charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                256,	/* 256 characters */
                2,	/* 2 bits per pixel */
                new int[] { 256*8*8, 0 },	/* the two bitplanes are separated */
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },	/* pretty straightforward layout */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                8*8	/* every char takes 8 consecutive bytes */
        );
        static GfxLayout dkongjr_charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                512,	/* 512 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 512*8*8 },	/* the two bitplanes are separated */
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },	/* pretty straightforward layout */
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                8*8	/* every char takes 8 consecutive bytes */
        );
        static GfxLayout dkong_spritelayout = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                128,	/* 128 sprites */
                2,	/* 2 bits per pixel */
                new int[]{ 128*16*16, 0 },	/* the two bitplanes are separated */
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7,	/* the two halves of the sprite are separated */
                                64*16*16+0, 64*16*16+1, 64*16*16+2, 64*16*16+3, 64*16*16+4, 64*16*16+5, 64*16*16+6, 64*16*16+7 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                                8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
                16*8	/* every sprite takes 16 consecutive bytes */
        );
        static GfxLayout dkong3_spritelayout = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                256,	/* 256 sprites */
                2,	/* 2 bits per pixel */
                new int[]{ 256*16*16, 0 },	/* the two bitplanes are separated */
                new int[]{ 0, 1, 2, 3, 4, 5, 6, 7,	/* the two halves of the sprite are separated */
                                128*16*16+0, 128*16*16+1, 128*16*16+2, 128*16*16+3, 128*16*16+4, 128*16*16+5, 128*16*16+6, 128*16*16+7 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                                8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
                16*8	/* every sprite takes 16 consecutive bytes */
        );



        static GfxDecodeInfo dkong_gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, dkong_charlayout,      0, 64 ),
                new GfxDecodeInfo( 1, 0x1000, dkong_spritelayout,    0, 64 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };
        static GfxDecodeInfo dkongjr_gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, dkongjr_charlayout,     0, 128 ),
                new GfxDecodeInfo( 1, 0x2000, dkong_spritelayout, 128*4,  16 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };
        static GfxDecodeInfo dkong3_gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, dkongjr_charlayout,     0, 64 ),
                new GfxDecodeInfo( 1, 0x2000, dkong3_spritelayout, 64*4, 16 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        static char dkong_color_prom[] =
        {
                /* 2j - palette high 4 bits (inverted) */
                0xFF,0xF6,0xFE,0xF1,0xFF,0xF0,0xF1,0xF0,0xFF,0xF0,0xF1,0xFF,0xFF,0xFF,0xF1,0xFE,
                0xFF,0xF1,0xF0,0xF1,0xFF,0xF0,0xF1,0xF0,0xFF,0xF1,0xFF,0xFE,0xFF,0xF5,0xF0,0xF0,
                0xFF,0xF5,0xF0,0xF1,0xFF,0xF0,0xF1,0xF1,0xFF,0xFF,0xF0,0xF1,0xFF,0xFF,0xF1,0xF0,
                0xFF,0xF0,0xFE,0xFF,0xFF,0xF1,0xF0,0xF1,0xFF,0xF2,0xF0,0xFF,0xFF,0xF0,0xF1,0xFF,
                0xFF,0xF6,0xFE,0xF1,0xFF,0xF0,0xF1,0xF0,0xFF,0xF0,0xF1,0xFF,0xFF,0xF1,0xF0,0xF0,
                0xFF,0xF1,0xF0,0xF0,0xFF,0xF1,0xF0,0xF0,0xFF,0xF1,0xF0,0xF0,0xFF,0xF5,0xF0,0xF0,
                0xFF,0xF5,0xF0,0xF1,0xFF,0xF0,0xF1,0xF1,0xFF,0xFF,0xF0,0xF1,0xFF,0xFF,0xF1,0xF0,
                0xFF,0xF0,0xFE,0xFF,0xFF,0xF1,0xF0,0xF1,0xFF,0xF2,0xF0,0xFF,0xFF,0xF0,0xF1,0xFF,
                0xFF,0xF6,0xFE,0xF1,0xFF,0xF0,0xF1,0xF0,0xFF,0xF0,0xF1,0xFF,0xFF,0xF1,0xF7,0xFE,
                0xFF,0xF1,0xF7,0xFE,0xFF,0xF1,0xF7,0xFE,0xFF,0xF1,0xF7,0xFE,0xFF,0xF5,0xF0,0xF0,
                0xFF,0xF5,0xF0,0xF1,0xFF,0xF0,0xF1,0xF1,0xFF,0xFF,0xF0,0xF1,0xFF,0xFF,0xF1,0xF0,
                0xFF,0xF0,0xFE,0xFF,0xFF,0xF1,0xF0,0xF1,0xFF,0xF2,0xF0,0xFF,0xFF,0xF0,0xF1,0xFF,
                0xFF,0xF6,0xFE,0xF1,0xFF,0xF0,0xF1,0xF0,0xFF,0xF0,0xF1,0xFF,0xFF,0xFF,0xFE,0xF0,
                0xFF,0xFF,0xFE,0xF0,0xFF,0xFF,0xFE,0xF0,0xFF,0xFF,0xFE,0xF0,0xFF,0xF5,0xF0,0xF0,
                0xFF,0xF5,0xF0,0xF1,0xFF,0xF0,0xF1,0xF1,0xFF,0xFF,0xF0,0xF1,0xFF,0xFF,0xF1,0xF0,
                0xFF,0xF0,0xFE,0xFF,0xFF,0xF1,0xF0,0xF1,0xFF,0xF2,0xF0,0xFF,0xFF,0xF0,0xF1,0xFF,
                /* 2k - palette low 4 bits (inverted) */
                0xFF,0xFC,0xF0,0xFF,0xFF,0xFB,0xFF,0xF0,0xFF,0xFA,0xFF,0xFD,0xFF,0xFC,0xFF,0xF3,
                0xFF,0xF3,0xFB,0xFF,0xFF,0xF0,0xFD,0xF3,0xFF,0xFF,0xFC,0xF0,0xFF,0xFF,0xFA,0xF0,
                0xFF,0xFF,0xFA,0xF3,0xFF,0xF0,0xF3,0xF5,0xFF,0xFD,0xF0,0xF5,0xFF,0xFC,0xF3,0xFA,
                0xFF,0xF0,0xF0,0xFC,0xFF,0xFF,0xFB,0xF3,0xFF,0xFE,0xFA,0xFC,0xFF,0xFB,0xFF,0xFF,
                0xFF,0xFC,0xF0,0xFF,0xFF,0xFB,0xFF,0xF0,0xFF,0xFA,0xFF,0xFD,0xFF,0xF3,0xFA,0xF0,
                0xFF,0xF3,0xFA,0xF0,0xFF,0xF3,0xFA,0xF0,0xFF,0xF3,0xFA,0xF0,0xFF,0xFF,0xFA,0xF0,
                0xFF,0xFF,0xFA,0xF3,0xFF,0xF0,0xF3,0xF5,0xFF,0xFD,0xF0,0xF5,0xFF,0xFC,0xF3,0xFA,
                0xFF,0xF0,0xF0,0xFC,0xFF,0xFF,0xFB,0xF3,0xFF,0xFE,0xFA,0xFC,0xFF,0xFB,0xFF,0xFF,
                0xFF,0xFC,0xF0,0xFF,0xFF,0xFB,0xFF,0xF0,0xFF,0xFA,0xFF,0xFD,0xFF,0xFA,0xFF,0xF0,
                0xFF,0xFA,0xFF,0xF0,0xFF,0xFA,0xFF,0xF0,0xFF,0xFA,0xFF,0xF0,0xFF,0xFF,0xFA,0xF0,
                0xFF,0xFF,0xFA,0xF3,0xFF,0xF0,0xF3,0xF5,0xFF,0xFD,0xF0,0xF5,0xFF,0xFC,0xF3,0xFA,
                0xFF,0xF0,0xF0,0xFC,0xFF,0xFF,0xFB,0xF3,0xFF,0xFE,0xFA,0xFC,0xFF,0xFB,0xFF,0xFF,
                0xFF,0xFC,0xF0,0xFF,0xFF,0xFB,0xFF,0xF0,0xFF,0xFA,0xFF,0xFD,0xFF,0xFC,0xF0,0xFB,
                0xFF,0xFC,0xF0,0xFB,0xFF,0xFC,0xF0,0xFB,0xFF,0xFC,0xF0,0xFB,0xFF,0xFF,0xFA,0xF0,
                0xFF,0xFF,0xFA,0xF3,0xFF,0xF0,0xF3,0xF5,0xFF,0xFD,0xF0,0xF5,0xFF,0xFC,0xF3,0xFA,
                0xFF,0xF0,0xF0,0xFC,0xFF,0xFF,0xFB,0xF3,0xFF,0xFE,0xFA,0xFC,0xFF,0xFB,0xFF,0xFF,
                /* 5f - character color codes on a per-column basis */
                0xF0,0xF1,0xF1,0xF2,0xF6,0xF6,0xF4,0xF6,0xF6,0xF6,0xF6,0xF3,0xF6,0xF3,0xF4,0xF3,
                0xF6,0xF6,0xF6,0xF6,0xF4,0xF3,0xF4,0xF5,0xF4,0xF6,0xF5,0xF3,0xF5,0xF3,0xF6,0xF3,
                0xF0,0xF1,0xF1,0xF2,0xF6,0xF6,0xF4,0xF6,0xF6,0xF6,0xF6,0xF3,0xF6,0xF3,0xF4,0xF3,
                0xF6,0xF6,0xF6,0xF6,0xF4,0xF3,0xF4,0xF5,0xF4,0xF6,0xF5,0xF3,0xF5,0xF3,0xF6,0xF3,
                0xF0,0xF1,0xF1,0xF2,0xF6,0xF6,0xF4,0xF6,0xF6,0xF6,0xF6,0xF3,0xF6,0xF3,0xF4,0xF3,
                0xF6,0xF6,0xF6,0xF6,0xF4,0xF3,0xF4,0xF5,0xF4,0xF6,0xF5,0xF3,0xF5,0xF3,0xF6,0xF3,
                0xF0,0xF1,0xF1,0xF2,0xF6,0xF6,0xF4,0xF6,0xF6,0xF6,0xF6,0xF3,0xF6,0xF3,0xF4,0xF3,
                0xF6,0xF6,0xF6,0xF6,0xF4,0xF3,0xF4,0xF5,0xF4,0xF6,0xF5,0xF3,0xF5,0xF3,0xF6,0xF3,
                0xF0,0xF1,0xF1,0xF2,0xF6,0xF6,0xF4,0xF6,0xF6,0xF6,0xF6,0xF3,0xF6,0xF3,0xF4,0xF3,
                0xF6,0xF6,0xF6,0xF6,0xF4,0xF3,0xF4,0xF5,0xF4,0xF6,0xF5,0xF3,0xF5,0xF3,0xF6,0xF3,
                0xF0,0xF1,0xF1,0xF2,0xF6,0xF6,0xF4,0xF6,0xF6,0xF6,0xF6,0xF3,0xF6,0xF3,0xF4,0xF3,
                0xF6,0xF6,0xF6,0xF6,0xF4,0xF3,0xF4,0xF5,0xF4,0xF6,0xF5,0xF3,0xF5,0xF3,0xF6,0xF3,
                0xF0,0xF1,0xF1,0xF2,0xF6,0xF6,0xF4,0xF6,0xF6,0xF6,0xF6,0xF3,0xF6,0xF3,0xF4,0xF3,
                0xF6,0xF6,0xF6,0xF6,0xF4,0xF3,0xF4,0xF5,0xF4,0xF6,0xF5,0xF3,0xF5,0xF3,0xF6,0xF3,
                0xF0,0xF1,0xF1,0xF2,0xF6,0xF6,0xF4,0xF6,0xF6,0xF6,0xF6,0xF3,0xF6,0xF3,0xF4,0xF3,
                0xF6,0xF6,0xF6,0xF6,0xF4,0xF3,0xF4,0xF5,0xF4,0xF6,0xF5,0xF3,0xF5,0xF3,0xF6,0xF3
        };



        static char dkongjr_palette[] =
        {
                0x00,0x00,0x00, /* BLACK */
                0xff,0xff,0xff, /* WHITE */
                0xff,0x00,0x00, /* RED */
                0xff,0x00,0xff, /* PURPLE */
                168,0xff,0xff,  /* CYAN */
                255,200,184,    /* LTORANGE */
                184,0x00,0x00,  /* DKRED */
                0x00,0x00,0xff, /* BLUE */
                0xff,0xff,0x00, /* YELLOW */
                255,128,255,    /* PINK */
                120,192,255,    /* LTBLUE */
                0xff,96,0x00,   /* ORANGE */
                0x00,0xff,0x00, /* GREEN */
                136,0x00,0x00,  /* DKBROWN */
                255,176,112,    /* LTBROWN */
                0x00,96,0x00,   /* DKGREEN */
        };
        
        static final int BLACK=0;
        static final int WHITE=1;
        static final int RED=2;
        static final int PURPLE=3;
        static final int CYAN=4;
        static final int LTORANGE=5;
        static final int DKRED=6;
        static final int BLUE=7;
        static final int YELLOW=8;
        static final int PINK=9;
        static final int LTBLUE=10;
        static final int ORANGE=11;
        static final int GREEN=12;
        static final int DKBROWN=13;
        static final int LTBROWN=14;
        static final int DKGREEN=15; 

        static char dkongjr_colortable[] =
        {
                /* chars (first bank) */
                BLACK,BLACK,BLACK,WHITE,       /* 0-9 */
                BLACK,BLACK,BLACK,WHITE,       /* 0-9 */
                BLACK,BLACK,BLACK,WHITE,       /* 0-9 */
                BLACK,BLACK,BLACK,WHITE,       /* 0-9 */
                BLACK,BLACK,BLACK,RED,         /* A-Z */
                BLACK,BLACK,BLACK,RED,         /* A-Z */
                BLACK,BLACK,BLACK,RED,         /* A-Z */
                BLACK,BLACK,BLACK,RED,         /* A-Z */
                BLACK,BLACK,BLACK,RED,         /* A-Z */
                BLACK,BLACK,BLACK,RED,         /* A-Z */
                BLACK,BLACK,BLACK,RED,         /* A-Z */
                BLACK,BLACK,BLACK,RED,         /* A-Z */
                BLACK,BLACK,BLACK,RED,         /* A-Z */
                BLACK,BLACK,BLACK,RED,         /* A-Z */
                BLACK,BLACK,BLACK,RED,         /* A-Z */
                BLACK,BLACK,BLACK,RED,         /* A-Z */
                BLACK,BLACK,BLACK,WHITE,       /* RUB END */
                BLACK,BLACK,BLACK,WHITE,       /* RUB END */
                BLACK,BLACK,BLACK,WHITE,       /* RUB END */
                BLACK,BLACK,BLACK,WHITE,       /* RUB END */
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                BLACK,BLACK,RED,BLUE,          /* Bonus */
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                BLACK,BLACK,RED,BLUE,          /* Bonus Box */
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                BLACK,BLACK,RED,BLUE,          /* Bonus Box */
                BLACK,BLACK,RED,BLUE,          /* Bonus Box */
                BLACK,BLACK,RED,BLUE,          /* Bonus Box */
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                BLACK,BLACK,ORANGE,BLACK,      /* Screen1: Islands Bottom */
                BLACK,LTBLUE,BLUE,RED,         /* Screen4: Locks */
                BLACK,LTBLUE,BLUE,WHITE,       /* Screen1: Water */
                BLACK,BLACK,BLUE,BLACK,        /* Screen2: Rope Cage-Lock */
                BLACK,BLACK,BLUE,BLACK,        /* Screen2: Rope Cage-Lock */
                BLACK,GREEN,RED,LTBROWN,       /* Screen1: Vines */
                BLACK,GREEN,BLUE,LTBLUE,       /* Rope & Chains */
                BLACK,CYAN,BLUE,LTBLUE,        /* Screen2: Wall */
                BLACK,LTBLUE,BLUE,BLUE,        /* Screen4: Mario's Platform */
                BLACK,LTBLUE,BLUE,BLUE,        /* Screen4: Mario's Platform */
                BLACK,LTBLUE,BLUE,BLUE,        /* Screen4: Mario's Platform */
                0,0,0,0,                       /* Unused */
                0,0,0,0,                       /* Unused */
                BLACK,GREEN,DKGREEN,WHITE,     /* Screen1: Islands Top */
                BLACK,LTBLUE,BLUE,WHITE,       /* Screen1&2: Top Floor Bottom Part */
                BLACK,LTBLUE,BLUE,BLUE,        /* Screen4: Bottom Platform */
                BLACK,LTBLUE,BLUE,BLUE,        /* Screen4: Bottom Platform */
                BLACK,LTBLUE,ORANGE,YELLOW,    /* Screen1,2,4: Platform */
                BLACK,LTBLUE,BLUE,BLUE,        /* Screen4: Bottom Platform */
                BLACK,LTBLUE,BLUE,BLUE,        /* Screen4: Bottom Platform */
                BLACK,DKGREEN,DKBROWN,LTBROWN, /* Lives */

                /* chars (second bank) */
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLACK,        /* Logo: Donkey Kong */
                BLACK,YELLOW,RED,BLUE,         /* Logo: Donkey Kong, TM, Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                BLACK,BLACK,RED,BLUE,          /* Logo: Junior */
                0,0,0,0,
                BLACK,RED,BLACK,BLACK,         /* Logo: (c) 198 */
                BLACK,RED,BLACK,BLACK,         /*       2 Nin   */
                BLACK,RED,BLACK,BLACK,         /*       tendo   */
                BLACK,RED,BLACK,BLACK,         /*       of Am   */
                BLACK,RED,BLACK,BLACK,         /*       erica   */
                BLACK,RED,BLACK,BLACK,         /*       Inc.    */
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,

                /* sprites */
                BLACK,BLUE,LTORANGE,RED,       /* Mario */
                BLACK,BLACK,BLACK,WHITE,       /* Bonus Score */
                BLACK,ORANGE,GREEN,WHITE,      /* Screen2: Moving Platform */
                BLACK,1,2,3,                   /* ?? */
                BLACK,BLUE,YELLOW,WHITE,       /* Cursor */
                BLACK,4,5,6,                   /* ?? */
                BLACK,7,8,9,                   /* ?? */
                BLACK,DKBROWN,WHITE,LTBROWN,   /* Kong Jr */
                BLACK,BLUE,BLUE,LTBLUE,        /* Cage */
                BLACK,DKBROWN,ORANGE,LTBROWN,  /* Kong */
                BLACK,1,7,9,                   /* ?? */
                BLACK,ORANGE,GREEN,YELLOW,     /* Banana & Pear */
                BLACK,RED,GREEN,WHITE,         /* Apple */
                BLACK,BLUE,CYAN,WHITE,         /* Blue Creature */
                BLACK,RED,YELLOW,WHITE,        /* Red Creature */
                BLACK,PURPLE,YELLOW,WHITE      /* Key */
        };


        static char dkong3_palette[] =
        {
                0x00,0x00,0x00,	/* BLACK */
                0xdb,0x00,0x00,	/* RED */
                0xdb,0x92,0x49,	/* BROWN */
                0xff,0xb6,0xdb,	/* PINK */
                0x00,0xdb,0x00,	/* UNUSED */
                0x00,0xdb,0xdb,	/* CYAN */
                0x49,0xb6,0xdb,	/* DKCYAN */
                0xff,0xb6,0x49,	/* DKORANGE */
                0x88,0x88,0x88,	/* UNUSED */
                0xdb,0xdb,0x00,	/* YELLOW */
                0xff,0x00,0xdb,	/* UNUSED */
                0x24,0x24,0xdb,	/* BLUE */
                0x00,0xdb,0x00,	/* GREEN */
                0x49,0xb6,0x92,	/* DKGREEN */
                0xff,0xb6,0x92,	/* LTORANGE */
                0xdb,0xdb,0xdb	/* GREY */
        };

        static final int DK3_BLACK=0;
        static final int DK3_RED=1;
        static final int DK3_BROWN=2;
        static final int DK3_PINK=3;
        static final int DK3_UNUSED1=4;
        static final int DK3_CYAN=5;
        static final int DK3_DKCYAN=6;
        static final int DK3_DKORANGE=7;
        static final int DK3_UNUSED2=8;
        static final int DK3_YELLOW=9;
        static final int DK3_UNUSED3=10;
        static final int DK3_BLUE=11;
        static final int DK3_GREEN=12;
        static final int DK3_DKGREEN=13;
        static final int DK3_LTORANGE=14;
        static final int DK3_GREY=15;

        /* Used for common colors (much easier to change them here!) */
        static final int DK_LEAVE_MIDDLE =    11;
        static final int DK_VINES_EDGE  =     13;
        static final int LEAVE_EDGE    =      12;
        static final int LEAVE_MIDDLE_LEV2 =  6;
        static final int LEAVE_EDGE_LEV2  =   11;
        static final int TRUNK_LEV2      =    13;

        static char dkong3_colortable[] =
        {
            /* chars */
            /* (#0-3) NUMBERS, 1UP/2UP, TREE TRUNK on Level 2. */
            DK3_BLACK,DK3_UNUSED1,DK3_UNUSED2,DK3_GREY, /* #0. ?,?,0123 */
            DK3_BLACK,DK3_UNUSED1,DK3_UNUSED2,DK3_GREY, /* #1. ?,?,4567 */
            DK3_BLACK,DK3_UNUSED1,DK3_RED,DK3_GREY,     /* #2. ?,color of part of "1UP",89 */

            /* 2=color of tree trunk on level 2, 3=color of "UP 2UP",
               4=tree trunk on level 2  */
            DK3_BLACK,LEAVE_EDGE_LEV2,DK3_RED,TRUNK_LEV2,

            /* (#4-12) A-Z, "TOP", & top vines on Level 1. */
            DK3_BLACK,DK3_RED,DK3_RED,DK3_RED,
            DK3_BLACK,DK3_RED,DK3_RED,DK3_RED,
            DK3_BLACK,DK3_RED,DK3_RED,DK3_RED,
            DK3_BLACK,DK3_RED,DK3_RED,DK3_RED,
            DK3_BLACK,DK3_RED,DK3_RED,DK3_RED,
            DK3_BLACK,DK3_RED,DK3_RED,DK3_RED,
            DK3_BLACK,DK_VINES_EDGE,DK3_RED,DK3_RED, /* #10. "T" in TOP, XYZ */

            /* 2="OP" & top vines in level 1, 3="OP" & top vines in level 1
               4=top vines in level 1.  */
            DK3_BLACK,DK_VINES_EDGE,DK_VINES_EDGE,DK_VINES_EDGE,

            /* 2=top-vines, 3=???, 4=top-vines */
            DK3_BLACK,DK_VINES_EDGE,DK3_UNUSED3,DK_VINES_EDGE,

            /* (#13-15) TIME display and the 2-color box around it. */
            DK3_BLACK,DK3_RED,DK3_BLUE,DK3_GREY,        /* #13. outerborder, innerborder, ??? */
            DK3_BLACK,DK3_BLUE,DK3_BLUE,DK3_GREY,       /* #14. "TIME" display, line under time, ??? */
            DK3_BLACK,DK3_RED,DK3_BLUE,DK3_GREY,        /* #15. outerborder, innerborder, ??? */

            /* (#16-22) 2=shadow of "3", 3=outline of "3" & vines, 4=middle of "3" */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,  /* 3=vertical vines  */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,  /* 3=horizontal vines */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,  /* 2=box on bottom, 3=vines in box. */

            /* 2=shadow of "3" & middle of DK-logo & middle of box on bottom
               3=outline of "3" & outline of DK-logo & vines in box on bottom
               4=middle of "3"  */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#23-47) DK-LOGO
               2=middle of DK-logo & line on top of boxes on side of levels,
               3=boxes on side of level & outline of DK-logo, 4=boxes on side of levels  */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#28) 2=middle of DK-logo & leaves, 3=outline of DK-logo, 4=edge of leaves */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#32) 2=middle of leaves & shadow of "3", 3=outline of "3",
               4=edge of leaves & middle of "3"  */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#33) 2=middle of leaves & shadow of "3", 4=edge of leaves */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#34-36) 2=middle of DK-logo & middle of leaves, 3=outline of DK-logo,
               4=edge of leaves */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#37-38) 2=middle of DK-logo, 3=outline of DK-logo */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#39) 2=middle of DK-logo & color of DK's hanging vines, 3=outline of
               DK-logo, 4=color of DK's hanging vines (mixed w/color 2).  */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#40-41) 2=middle of DK-logo & boxes near bottom, 3=outline of DK-logo &
               lines in boxes near bottom. */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#42) 2=middle of DK-logo & boxes near bottom, 3=outline of DK-logo &
               lines in boxes near bottom, 4=boxes on side of levels  */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#43-46) DK-Logo, ANGLED pieces on side of levels.
               2=middle of DK-logo, 3=outline of DK-logo, 4=boxes on side of levels */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#47) 2=leave middle, 4=leave edge & boxes on side of levels */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#48-53) LEAVES on Level 1, LETTERS on title screen.
               2=leave middle, 4=leave edges & some letters  */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#54-57) LEAVES on Level 1, (56-57) LETTERS on title screen
               2=leave middle, 4=leave edges  */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            /* 2=some letters on title screen  */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#58) 2=tree trunk & some letters on title screen & angled pieces
               on Level 2, 4=tree trunk & tops of angulars on Level 2.  */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#59) 2=some letters on title screen */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#60) 2=some letters on title screen & vines on level 2, 3=side bar,
               4=vines on level 2.  */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#61) 2=some letters on title screen & angulars on level 2,
             4=top of angled pieces on level 2.  */
            DK3_BLACK,DK_LEAVE_MIDDLE,DK_VINES_EDGE,LEAVE_EDGE,

            /* (#62-63) LEAVES ON LEVEL 2.
               2=middle of leaves on level 2, 4=edges of leaves on level 2.  */
            DK3_BLACK,LEAVE_MIDDLE_LEV2,DK3_UNUSED3,LEAVE_EDGE_LEV2,
            DK3_BLACK,LEAVE_MIDDLE_LEV2,DK3_UNUSED3,LEAVE_EDGE_LEV2,

            /* sprites */
            /* #0. Donkey Kong's head. */
            DK3_BLACK,DK3_BROWN,DK3_RED,DK3_GREY,

            DK3_BLACK,DK3_UNUSED1,DK3_UNUSED2,DK3_UNUSED3,       /* ???? */
            DK3_BLACK,DK3_UNUSED2,DK3_UNUSED1,DK3_UNUSED3,       /* ???? */

            /* #3. Middle-vertical-vines on level 2. */
            DK3_BLACK,DK_VINES_EDGE,DK_LEAVE_MIDDLE,LEAVE_EDGE,

            DK3_BLACK,DK3_UNUSED3,DK3_UNUSED2,DK3_UNUSED1,       /* ???? */

            /* #5. Box on the bottom-middle of level #1, 2=box color, 3=lines in box, 4=???  */
            DK3_BLACK,DK_VINES_EDGE,DK_LEAVE_MIDDLE,DK3_GREY,

            DK3_BLACK,DK3_UNUSED1,DK3_UNUSED3,DK3_UNUSED2,       /* ???? */

            /* #7. Mario's body? */
            DK3_BLACK,DK3_RED,DK3_DKGREEN,DK3_BLUE,
            /* #8. Mario's head */
            DK3_BLACK,DK3_BROWN,DK3_LTORANGE,DK3_BLUE,

            /* #9. Mario's BULLETS (weak gun) */
            DK3_BLACK,DK3_GREEN,DK3_RED,DK3_BLUE,

            /* #10. Bee hives & 2-hit bugs (level 3) (EYES,WINGS/FEET,BODY/ANTENNA)
                    also color of SHOTS & PLAYER when player gets spray bottle. */
            DK3_BLACK,DK3_BROWN,DK3_RED,DK3_YELLOW,

            /* #11. Bugs-common ones (EYES,WINGS/FEET,BODY/ANTENNA) */
            DK3_BLACK,DK3_GREEN,DK3_DKGREEN,DK3_BLUE,

            /* #12. Bugs */
            DK3_BLACK,DK3_BLUE,DK3_DKGREEN,DK3_GREEN,

            /* #13. Worm (BODY,EYES/STRIPES,STRIPES) */
            DK3_BLACK,DK3_RED,DK3_DKCYAN,DK3_YELLOW,

            /* #14. Spray Bottle & flowers? (near DK on level 1) */
            DK3_BLACK,DK3_GREEN,DK3_RED,DK3_GREY,

            /* #15. Donkey Kong's body. (BODY,CHEST,EDGES), also BALL on level 2. */
            DK3_BLACK,DK3_BROWN,DK3_RED,DK3_RED
        };



        static MachineDriver dkong_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                3072000,	/* 3.072 Mhz (?) */
                                0,
                                readmem,dkong_writemem, null, null,
                                nmi_interrupt,1
                        )
                },
                60,
                null,

                /* video hardware */
                32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
                dkong_gfxdecodeinfo,
                256, 64*4,
                dkong_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
                null,
                dkong_vh_start,
                generic_vh_stop,
                dkong_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                null,
                null,
                dkong_sh_update
        );

        static MachineDriver dkongjr_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                3072000,	/* 3.072 Mhz (?) */
                                0,
                                readmem,dkongjr_writemem,null, null,
                                nmi_interrupt,1
                        )
                },
                60,
                null,

                /* video hardware */
                32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
                dkongjr_gfxdecodeinfo,
                sizeof(dkongjr_palette)/3,sizeof(dkongjr_colortable),
                null,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
                 null,
                dkong_vh_start,
                generic_vh_stop,
                dkongjr_vh_screenrefresh,

                /* sound hardware */
                 null,
                 null,
                 null,
                 null,
                 null
        );

        static MachineDriver dkong3_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                4000000,	/* 4 Mhz ? */
                                0,
                                readmem,dkong3_writemem,null, null,
                                nmi_interrupt,1
                        )
                },
                60,
                null, 

                /* video hardware */
                32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
                dkong3_gfxdecodeinfo,
                sizeof(dkong3_palette)/3,sizeof(dkong3_colortable),
                null,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
                null,
                dkong_vh_start,
                generic_vh_stop,
                dkongjr_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                null,
                null,
                null
        );



        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr dkong_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "dk.5e",  0x0000, 0x1000, 0xa0bfe0f7 );
                ROM_LOAD( "dk.5c",  0x1000, 0x1000, 0x36320606 );
                ROM_LOAD( "dk.5b",  0x2000, 0x1000, 0x57b81038 );
                ROM_LOAD( "dk.5a",  0x3000, 0x1000, 0xe2f03e46 );
                /* space for diagnostic ROM */

                ROM_REGION(0x3000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "dk.3n",  0x0000, 0x0800, 0x5947fc8f );
                ROM_LOAD( "dk.3p",  0x0800, 0x0800, 0xcf971207 );
                ROM_LOAD( "dk.7c",  0x1000, 0x0800, 0xeca7e655 );
                ROM_LOAD( "dk.7d",  0x1800, 0x0800, 0xd8700f2a );
                ROM_LOAD( "dk.7e",  0x2000, 0x0800, 0x3dd5410b );
                ROM_LOAD( "dk.7f",  0x2800, 0x0800, 0xcc1d7c97 );

                ROM_REGION(0x1000);	/* sound */
                ROM_LOAD( "dk.3h",  0x0000, 0x0800, 0x52574a61 );
                ROM_LOAD( "dk.3f",  0x0800, 0x0800, 0x2a6cd3fa );
                ROM_END();
        }};
        static RomLoadPtr dkongjp_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "5f.cpu",  0x0000, 0x1000, 0x949b12d3 );
                ROM_LOAD( "5g.cpu",  0x1000, 0x1000, 0xf81386e7 );
                ROM_LOAD( "5h.cpu",  0x2000, 0x1000, 0x45b7e62b );
                ROM_LOAD( "5k.cpu",  0x3000, 0x1000, 0x97dd25c5 );

                ROM_REGION(0x3000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "dk.3n",        0x0000, 0x0800, 0x12c8c95d );
                ROM_LOAD( "5k.vid",       0x0800, 0x0800, 0x3684f914 );
                ROM_LOAD( "dk.7c",        0x1000, 0x0800, 0x59f8054d );
                ROM_LOAD( "dk.7d",        0x1800, 0x0800, 0x672e4714 );
                ROM_LOAD( "dk.7e",        0x2000, 0x0800, 0xfeaa59ee );
                ROM_LOAD( "dk.7f",        0x2800, 0x0800, 0x20f2ef7e );

                ROM_REGION(0x1000);	/* sound */
                ROM_LOAD( "dk.3h",        0x0000, 0x0800, 0x45a4ed06 );
                ROM_LOAD( "dk.3f",        0x0800, 0x0800, 0x4743fe92 );
                ROM_END();
        }};
        static RomLoadPtr dkongjr_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "dkj.5b",  0x0000, 0x1000, 0x831d73dd );
                ROM_CONTINUE(        0x3000, 0x1000 );
                ROM_LOAD( "dkj.5c",  0x2000, 0x0800, 0xe0078007 );
                ROM_CONTINUE(        0x4800, 0x0800 );
                ROM_CONTINUE(        0x1000, 0x0800 );
                ROM_CONTINUE(        0x5800, 0x0800 );
                ROM_LOAD( "dkj.5e",  0x4000, 0x0800, 0xb31be9f1 );
                ROM_CONTINUE(        0x2800, 0x0800 );
                ROM_CONTINUE(        0x5000, 0x0800 );
                ROM_CONTINUE(        0x1800, 0x0800 );

                ROM_REGION(0x4000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "dkj.3n",  0x0000, 0x1000, 0x4c96d5b0 );
                ROM_LOAD( "dkj.3p",  0x1000, 0x1000, 0xfbaafe4a );
                ROM_LOAD( "dkj.7c",  0x2000, 0x0800, 0x9ecd901b );
                ROM_LOAD( "dkj.7d",  0x2800, 0x0800, 0xac9378bb );
                ROM_LOAD( "dkj.7e",  0x3000, 0x0800, 0x9785a0b9 );
                ROM_LOAD( "dkj.7f",  0x3800, 0x0800, 0xecc39067 );

                ROM_REGION(0x1000);	/* sound? */
                ROM_LOAD( "dkj.3h",  0x0000, 0x1000, 0x65e71f9f );
                ROM_END();
        }};
       static RomLoadPtr dkong3_rom= new RomLoadPtr(){ public void handler()  
        { 

                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "dk3c.7b", 0x0000, 0x2000, 0x1f48b2d8 );
                ROM_LOAD( "dk3c.7c", 0x2000, 0x2000, 0x02129a26 );
                ROM_LOAD( "dk3c.7d", 0x4000, 0x2000, 0xf6ac38f8 );
                ROM_LOAD( "dk3c.7e", 0x8000, 0x2000, 0x9aa51d95 );

                ROM_REGION(0x6000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "dk3v.3n", 0x0000, 0x1000, 0xce64f6d0 );
                ROM_LOAD( "dk3v.3p", 0x1000, 0x1000, 0x06537143 );
                ROM_LOAD( "dk3v.7c", 0x2000, 0x1000, 0xfdc2c044 );
                ROM_LOAD( "dk3v.7d", 0x3000, 0x1000, 0x79ed16db );
                ROM_LOAD( "dk3v.7e", 0x4000, 0x1000, 0x233a64e8 );
                ROM_LOAD( "dk3v.7f", 0x5000, 0x1000, 0x4381b33d );

                ROM_REGION(0x4000);	/* sound */
                ROM_LOAD( "dk3c.5l", 0x0000, 0x2000, 0x34813d8d );
                ROM_LOAD( "dk3c.6h", 0x2000, 0x2000, 0xe2c9caa7 );
                ROM_END();
        }};



        static String sample_names[] =
        {
                "effect00.sam",
                "effect01.sam",
                "effect02.sam",
                "effect03.sam",
                "effect04.sam",
                "effect05.sam",
                "effect06.sam",
                "effect07.sam",
                "tune00.sam",
                "tune01.sam",
                "tune02.sam",
                "tune03.sam",
                "tune04.sam",
                "tune05.sam",
                "tune06.sam",
                "tune07.sam",
                "tune08.sam",
                "tune09.sam",
                "tune0a.sam",
                "tune0b.sam",
                "tune0c.sam",
                "tune0d.sam",
                "tune0e.sam",
                "tune0f.sam",
                "interupt.sam",
            null	/* end of array */
        };



	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
	{
		/* check if the hi score table has already been initialized */
            	if (memcmp(RAM,0x611d, new char[] {0x50,0x76,0x00},3) == 0 &&
			memcmp(RAM,0x61a5,new char[] {0x00 ,0x43,0x00},3) == 0 &&
			memcmp(RAM,0x60b8,new char[] {0x50,0x76,0x00},3) == 0)	/* high score */
		{
			FILE f;
	
	
			if ((f = fopen(name, "rb")) != null)
			{
				fread(RAM, 0x6100, 1, 34*5, f);
				RAM[0x60b8] = RAM[0x611d];
				RAM[0x60b9] = RAM[0x611e];
				RAM[0x60ba] = RAM[0x611f];
			/* also copy the high score to the screen, otherwise it won't be */
			/* updated until a new game is started */
                                videoram_w.handler(0x0221,RAM[0x6108]);
                                videoram_w.handler(0x0201,RAM[0x6109]);
                                videoram_w.handler(0x01e1,RAM[0x610a]);
                                videoram_w.handler(0x01c1,RAM[0x610b]);
                                videoram_w.handler(0x01a1,RAM[0x610c]);
				fclose(f);
			}
	
			return 1;
		}
		else return 0;	/* we can't load the hi scores yet */
	} };
	
		
		
	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
	{
		FILE f;
	
	
		if ((f = fopen(name, "wb")) != null)
		{
			fwrite(RAM, 0x6100, 1, 34*5, f);
			fclose(f);
		}
	} };


        static HiscoreLoadPtr dkong3_hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
		/* check if the hi score table has already been initialized */
		if (memcmp(RAM, 0x6b1d, new char[] { 0x00, 0x20, 0x01 }, 3) == 0 &&
				memcmp(RAM, 0x6ba5, new char[] { 0x00, 0x32, 0x00 }, 3) == 0)
		{
			FILE f;
	
	
			if ((f = fopen(name, "rb")) != null)
			{
				fread(RAM, 0x6b00, 1, 34*5, f);	/* hi scores */
				RAM[0x68f3] = RAM[0x6b1f];
				RAM[0x68f4] = RAM[0x6b1e];
				RAM[0x68f5] = RAM[0x6b1d];
				fread(RAM, 0x6c20, 1, 0x40, f);	/* distributions */
				fread(RAM, 0x6c16, 1, 4, f);
				fclose(f);
			}
	
			return 1;
		}
		else return 0;	/* we can't load the hi scores yet */
	} };
  
        static HiscoreSavePtr dkong3_hisave = new HiscoreSavePtr() { public void handler(String name)
	{
		FILE f;
	
	
		if ((f = fopen(name, "wb")) != null)
		{
			fwrite(RAM, 0x6b00, 1, 34*5, f);	/* hi scores */
			fwrite(RAM, 0x6c20, 1, 0x40, f);	/* distribution */
			fwrite(RAM, 0x6c16, 1, 4, f);
			fclose(f);
		}
	} };



        public static GameDriver dkong_driver = new GameDriver
	(
                "Donkey Kong (US version)",
                "dkong",
                "Gary Shepherdson (Kong emulator)\nBrad Thomas (hardware info)\nEdward Massey (MageX emulator)\nNicola Salmoria (MAME driver)\nRon Fries (sound)\nGary Walton (color info)\nSimon Walls (color info)",
                dkong_machine_driver,

                dkong_rom,
                null, null,
                sample_names,

                input_ports, null, trak_ports, dsw, keys,

                dkong_color_prom, null, null,
                ORIENTATION_ROTATE_90,

                hiload, hisave
        );
        public static GameDriver dkongjp_driver = new GameDriver
	(
                "Donkey Kong (Japanese version)",
                "dkongjp",
                "Gary Shepherdson (Kong emulator)\nBrad Thomas (hardware info)\nEdward Massey (MageX emulator)\nNicola Salmoria (MAME driver)\nRon Fries (sound)\nGary Walton (color info)\nSimon Walls (color info)",
                dkong_machine_driver,

                dkongjp_rom,
                null, null,
                sample_names,

                input_ports, null, trak_ports, dsw, keys,

                dkong_color_prom, null, null,
                ORIENTATION_ROTATE_90,

                hiload, hisave
        );
        public static GameDriver dkongjr_driver = new GameDriver
	(
                "Donkey Kong Jr.",
                "dkongjr",
                "Gary Shepherdson (Kong emulator)\nBrad Thomas (hardware info)\nNicola Salmoria (MAME driver)\nPaul Berberich (colors)\nMarc Vergoossen (colors)",
                dkongjr_machine_driver,

                dkongjr_rom,
                null, null,
                null,

                input_ports, null, trak_ports, dsw, keys,

                null, dkongjr_palette, dkongjr_colortable,
                ORIENTATION_ROTATE_90,

                hiload, hisave
        );
        public static GameDriver dkong3_driver = new GameDriver
	(
                "Donkey Kong 3",
                "dkong3",
                "Mirko Buffoni (MAME driver)\nNicola Salmoria (additional code)\nMatthew Hillmer (colors)",
                dkong3_machine_driver,

                dkong3_rom,
                 null, null,
                null,

                dkong3_input_ports,  null, trak_ports, dkong3_dsw, keys,

                 null, dkong3_palette, dkong3_colortable,
                ORIENTATION_ROTATE_90,

                hiload, hisave
        );
    
}
