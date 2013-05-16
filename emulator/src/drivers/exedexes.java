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
 * NOTES: romsets are from v0.36 roms
 *        
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
import static sndhrdw._8910intf.*;
import static sndhrdw.capcom.*;
import static sndhrdw.generic.*;
import static machine._1942.*;
import static vidhrdw.generic.*;
import static vidhrdw.exedexes.*;

public class exedexes {
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0xe000, 0xefff, MRA_RAM ), /* Work RAM */
                new MemoryReadAddress( 0xf000, 0xffff, MRA_RAM ), /* Sprite RAM */
                new MemoryReadAddress( 0xc000, 0xc000, input_port_0_r ),
                new MemoryReadAddress( 0xc001, 0xc001, input_port_1_r ),
                new MemoryReadAddress( 0xc002, 0xc002, input_port_2_r ),
                new MemoryReadAddress( 0xc003, 0xc003, input_port_3_r ),
                new MemoryReadAddress( 0xc004, 0xc004, input_port_4_r ),
                new MemoryReadAddress( 0x0000, 0xbfff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0xe000, 0xefff, MWA_RAM ),
                new MemoryWriteAddress( 0xd000, 0xd3ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0xd400, 0xd7ff, colorram_w, colorram ),
                new MemoryWriteAddress( 0xf000, 0xfbff, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0xfc00, 0xffff, exedexes_background_w, exedexes_backgroundram, exedexes_backgroundram_size ), /* Dummy Tile RAM */
                new MemoryWriteAddress( 0xc800, 0xc800, sound_command_w ),
        //        { 0xc804, 0xc804, exedexes_palette_bank_w, &exedexes_palette_bank },
        //        { 0xc806, 0xc806, MWA_RAM },  /* Bank switch... usually zero! */
                new MemoryWriteAddress( 0xd800, 0xd801, MWA_RAM, exedexes_nbg_yscroll ),
                new MemoryWriteAddress( 0xd802, 0xd803, MWA_RAM, exedexes_nbg_xscroll ),
                new MemoryWriteAddress( 0xd804, 0xd805, MWA_RAM, exedexes_bg_scroll ),
        /*        { 0xd806, 0xd807, MWA_RAM, ... }  unknown */
                new MemoryWriteAddress( 0xd808, 0xd83f, MWA_RAM ),  /* Unused Write Ports */
                new MemoryWriteAddress( 0x0000, 0xbfff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static MemoryReadAddress sound_readmem[] =
        {
                new MemoryReadAddress( 0x4000, 0x47ff, MRA_RAM ),
                new MemoryReadAddress( 0x6000, 0x6000, sound_command_latch_r ),
                new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress sound_writemem[] =
        {
                new MemoryWriteAddress( 0x4000, 0x47ff, MWA_RAM ),
                new MemoryWriteAddress( 0x8000, 0x8000, AY8910_control_port_0_w ),
                new MemoryWriteAddress( 0x8001, 0x8001, AY8910_write_port_0_w ),
                new MemoryWriteAddress( 0x8002, 0x8002, AY8910_control_port_1_w ),
                new MemoryWriteAddress( 0x8003, 0x8003, AY8910_write_port_1_w ),
                new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };



        static InputPort input_ports[] =
        {
                new InputPort(	/* IN0 */
                        0xff,
                        new int[] { OSD_KEY_1, OSD_KEY_2, 0, 0, 0, 0, OSD_KEY_4, OSD_KEY_3 }
                ),
		new InputPort(	/* IN1 */
                        0xff,
                        new int[] { OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_DOWN, OSD_KEY_UP,
                                        OSD_KEY_CONTROL, OSD_KEY_ALT, 0, 0 }
                ),
		new InputPort(	/* IN2 */
                        0xff,
                        new int[] { OSD_KEY_D, OSD_KEY_A, OSD_KEY_S, OSD_KEY_W,
                                        OSD_KEY_J, OSD_KEY_K, 0, 0 }
                ),
		new InputPort(	/* DSW1 */
                        0xdf,
                        new int[] { 0, 0, 0, 0, 0, 0, 0, OSD_KEY_F2 }
                ),
		new InputPort(	/* DSW2 */
                        0xff,
                        new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort( -1 )	/* end of table */
        };


        static TrakPort[] trak_ports =
       {
           new TrakPort(-1)
       };

        static KEYSet keys[] =
        {
                new KEYSet( 1, 3, "PLAYER 1 MOVE UP" ),
                new KEYSet( 1, 1, "PLAYER 1 MOVE LEFT"  ),
                new KEYSet( 1, 0, "PLAYER 1 MOVE RIGHT" ),
                new KEYSet( 1, 2, "PLAYER 1 MOVE DOWN" ),
                new KEYSet( 1, 4, "PLAYER 1 FIRE" ),
                new KEYSet( 1, 5, "PLAYER 1 INVULNERABILITY" ),
                new KEYSet( 2, 3, "PLAYER 2 MOVE UP" ),
                new KEYSet( 2, 1, "PLAYER 2 MOVE LEFT"  ),
                new KEYSet( 2, 0, "PLAYER 2 MOVE RIGHT" ),
                new KEYSet( 2, 2, "PLAYER 2 MOVE DOWN" ),
                new KEYSet( 2, 4, "PLAYER 2 FIRE" ),
                new KEYSet( 2, 5, "PLAYER 2 INVULNERABILITY" ),
                new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                new DSW( 3, 0x0c, "LIVES", new String[]{ "5", "2", "1", "3" }, 1 ),
                new DSW( 3, 0x03, "DIFFICULTY", new String[]{ "HARDEST", "HARD", "NORMAL", "EASY" }, 1 ),
                new DSW( 3, 0x20, "LANGUAGE", new String[]{ "ENGLISH", "JAPANESE", }, 1 ),
                new DSW( 3, 0x40, "PICTURE", new String[]{ "FROZEN", "NORMAL", }, 1 ),
                new DSW( 4, 0x80, "ATTRACT SOUND", new String[]{ "OFF", "ON", }, 1 ),
                new DSW( 4, 0x40, "CONTINUE", new String[]{ "OFF", "ON", }, 1 ),
                new DSW( 3, 0x10, "1 CREDIT", new String[]{ "1 OR 2 PLAYERS", "1 PLAYER", }, 1 ),
                new DSW( 4, 0x38, "COIN A", new String[]{ "4 COINS 1 CREDIT", "3 COINS 1 CREDIT", "2 COIN 1 CREDIT", "1 COIN 5 CREDITS",
                                       "1 COIN 4 CREDITS", "1 COIN 3 CREDITS", "1 COIN 2 CREDITS", "1 COIN 1 CREDIT" }, 1 ),
                new DSW( 4, 0x07, "COIN B", new String[]{ "4 COINS 1 CREDIT", "3 COINS 1 CREDIT", "2 COIN 1 CREDIT", "1 COIN 5 CREDITS",
                                       "1 COIN 4 CREDITS", "1 COIN 3 CREDITS", "1 COIN 2 CREDITS", "1 COIN 1 CREDIT" }, 1 ),
                new DSW( -1 )
        };



        static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                512,	/* 512 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 4, 0 },
                new int[]{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
                new int[]{ 8+3, 8+2, 8+1, 8+0, 3, 2, 1, 0 },
                16*8	/* every char takes 16 consecutive bytes */
        );
        static GfxLayout spritelayout = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                256,    /* 256 sprites */
                4,      /* 4 bits per pixel */
                new int[]{ 0x4000*8+4, 0x4000*8+0, 4, 0 },
                new int[]{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
                                8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
                new int[]{ 33*8+3, 33*8+2, 33*8+1, 33*8+0, 32*8+3, 32*8+2, 32*8+1, 32*8+0,
                                8+3, 8+2, 8+1, 8+0, 3, 2, 1, 0 },
                64*8	/* every sprite takes 64 consecutive bytes */
        );
        static GfxLayout tilelayout = new GfxLayout
	(
                16,8,  /* 16*16 tiles - wrong */
                512,    /* 256 tiles - wrong */
                2,      /* 2 bits per pixel */
                new int[]{ 4, 0 },
                new int[]{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
                                8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
                new int[]{ 8+3, 8+2, 8+1, 8+0, 3, 2, 1, 0 },
                32*8	/* every sprite takes 64 consecutive bytes */
        );



        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, charlayout,              0, 64 ),
                new GfxDecodeInfo( 1, 0x2000, tilelayout,           64*4, 64 ), /* 8x? Tiles */
                new GfxDecodeInfo( 1, 0x6000, spritelayout,       2*64*4, 16 ), /* 16x16 Tiles */
                new GfxDecodeInfo( 1, 0xe000, spritelayout, 2*64*4+16*16, 16 ), /* Sprites */
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        static char color_prom[] =
        {
                /* 02D_E-02.BIN: red component */
                0x00,0x05,0x06,0x07,0x08,0x04,0x06,0x07,0x05,0x06,0x07,0x08,0x02,0x03,0x05,0x07,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x04,0x05,0x06,0x08,0x03,0x04,0x05,0x07,0x03,0x04,0x05,0x06,0x07,0x08,0x00,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x01,0x05,0x07,0x09,0x0b,0x0d,0x04,0x06,0x08,0x0a,0x0c,0x06,0x09,0x0c,0x0e,0x00,
                0x06,0x08,0x0a,0x0c,0x0e,0x00,0x02,0x04,0x06,0x08,0x05,0x07,0x09,0x0b,0x0d,0x00,
                0x07,0x09,0x0b,0x0d,0x0f,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x00,
                0x0e,0x0e,0x04,0x05,0x09,0x0b,0x0c,0x0c,0x09,0x0e,0x0d,0x09,0x06,0x04,0x07,0x00,
                0x00,0x07,0x00,0x0f,0x09,0x06,0x09,0x0c,0x04,0x00,0x0f,0x0f,0x09,0x0f,0x0f,0x00,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                /* 03D_E-03.BIN: green component */
                0x00,0x00,0x00,0x00,0x00,0x04,0x06,0x07,0x03,0x04,0x05,0x06,0x04,0x05,0x07,0x08,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x02,0x03,0x04,0x06,0x05,0x06,0x07,0x09,0x03,0x04,0x05,0x06,0x07,0x08,0x00,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x01,0x05,0x07,0x09,0x0b,0x0d,0x06,0x08,0x0a,0x0c,0x0e,0x00,0x00,0x00,0x00,0x00,
                0x03,0x05,0x07,0x09,0x0b,0x06,0x08,0x0a,0x0c,0x0e,0x05,0x07,0x09,0x0b,0x0d,0x00,
                0x04,0x06,0x08,0x0a,0x0c,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x00,
                0x06,0x0a,0x06,0x09,0x0a,0x0a,0x08,0x06,0x05,0x05,0x03,0x00,0x02,0x04,0x07,0x00,
                0x00,0x00,0x0b,0x0f,0x09,0x05,0x08,0x0b,0x04,0x07,0x0a,0x00,0x0f,0x0f,0x00,0x00,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                /* 04D_E-04.BIN: blue component */
                0x00,0x00,0x00,0x00,0x00,0x02,0x04,0x05,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x04,0x05,0x06,0x07,0x08,0x09,0x00,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x01,0x06,0x08,0x0a,0x0c,0x0e,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x03,0x05,0x07,0x09,0x0b,0x00,0x02,0x04,0x06,0x08,0x05,0x07,0x09,0x0b,0x0d,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x00,
                0x00,0x00,0x00,0x00,0x05,0x00,0x00,0x00,0x03,0x00,0x00,0x00,0x00,0x04,0x06,0x00,
                0x01,0x0c,0x0c,0x0f,0x09,0x00,0x00,0x00,0x04,0x0f,0x00,0x07,0x00,0x00,0x00,0x00,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                /* 06F_E-05.BIN: char lookup table */
                0x0f,0x03,0x0e,0x09,0x0f,0x01,0x01,0x01,0x0f,0x02,0x02,0x02,0x0f,0x03,0x03,0x03,
                0x0f,0x04,0x04,0x04,0x0f,0x05,0x05,0x05,0x0f,0x06,0x06,0x06,0x0f,0x07,0x07,0x07,
                0x0f,0x08,0x08,0x08,0x0f,0x09,0x09,0x09,0x0f,0x0a,0x0a,0x0a,0x0f,0x0b,0x0b,0x0b,
                0x0f,0x0c,0x0c,0x0c,0x0f,0x0d,0x0d,0x0d,0x0f,0x0e,0x0e,0x0e,0x0f,0x00,0x00,0x00,
                0x0f,0x01,0x02,0x03,0x0f,0x08,0x02,0x03,0x08,0x01,0x02,0x03,0x0f,0x0d,0x0e,0x0f,
                0x0f,0x01,0x02,0x03,0x0f,0x05,0x06,0x07,0x0f,0x09,0x0a,0x0b,0x0f,0x0d,0x0e,0x0f,
                0x00,0x03,0x03,0x03,0x00,0x00,0x00,0x00,0x0f,0x02,0x0e,0x00,0x0f,0x0c,0x0e,0x00,
                0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                /* L04_E-10.BIN: 8x? tile lookup table */
                0x00,0x05,0x06,0x07,0x00,0x0C,0x0D,0x0E,0x0C,0x0D,0x0E,0x0F,0x00,0x08,0x09,0x0A,
                0x08,0x09,0x0A,0x0B,0x01,0x02,0x03,0x04,0x01,0x02,0x03,0x0A,0x09,0x0A,0x0C,0x0E,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                /* C04_E-07.BIN: 16x16 tile lookup table */
                0x0f,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x0f,0x05,0x06,0x07,0x08,0x01,0x02,0x03,0x04,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x0f,0x09,0x0a,0x0b,0x0c,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x0d,0x0e,0x00,
                0x0f,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x0f,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                /* L09_E-11.BIN: sprite lookup table */
                0x0f,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x06,0x07,0x08,0x09,0x0a,0x01,0x02,0x03,0x04,0x05,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x06,0x06,0x07,0x07,0x08,0x08,0x09,0x09,0x0a,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x00,0x00,0x01,0x01,0x02,0x02,0x03,0x03,0x04,0x04,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,0x01,0x02,0x03,0x04,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,0x01,0x02,0x03,0x04,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x05,0x06,0x07,0x08,0x09,0x00,0x01,0x02,0x03,0x04,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x00,0x01,0x02,0x03,0x04,0x0a,0x0b,0x0c,0x0d,0x0e,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x06,0x08,0x0a,0x0c,0x0e,0x00,0x00,0x00,0x01,0x00,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x00,0x00,0x00,0x0b,0x00,
                0x0f,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x00,0x00,0x0b,0x0c,0x00,
                0x0f,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x00,0x0b,0x0c,0x0d,0x00,
                0x0f,0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0b,0x0c,0x0d,0x0e,0x00,
                0x0f,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0e,0x00,
                /* L10_E-12.BIN: sprite palette bank */
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x02,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
                0x00,0x01,0x01,0x01,0x01,0x01,0x02,0x02,0x02,0x02,0x02,0x00,0x00,0x00,0x00,0x00,
                0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
                0x00,0x01,0x01,0x01,0x01,0x01,0x02,0x02,0x02,0x02,0x02,0x00,0x00,0x00,0x00,0x00,
                0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
                0x00,0x02,0x02,0x02,0x02,0x02,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
                0x00,0x02,0x02,0x02,0x02,0x02,0x00,0x00,0x03,0x03,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
                0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
                0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
                0x00,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,
                0x00,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x02,0x00
        };



        static MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                4000000,	/* 4 Mhz (?) */
                                0,
                                readmem,writemem,null,null,
                                c1942_interrupt,2
                        ),
			new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                3000000,	/* 3 Mhz ??? */
                                2,	/* memory region #2 */
                                sound_readmem,sound_writemem,null,null,
                                capcom_sh_interrupt,12
                        )
                },
                60,
                null,

                /* video hardware */
                32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
                gfxdecodeinfo,
                256,64*4+64*4+16*16+16*16,
                exedexes_vh_convert_color_prom,

                VIDEO_TYPE_RASTER,
                exedexes_vh_init,
                exedexes_vh_start,
                exedexes_vh_stop,
                exedexes_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                capcom_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );


        static RomLoadPtr exedexes_rom= new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);     /* 64k for code */
                        
                ROM_LOAD( "11m_ee04.bin", 0x0000, 0x4000, 0x44140dbd );
                ROM_LOAD( "10m_ee03.bin", 0x4000, 0x4000, 0xbf72cfba );
                ROM_LOAD( "09m_ee02.bin", 0x8000, 0x4000, 0x7ad95e2f );

                /*ROM_LOAD( "c01_ee07.bin", 0x10000, 0x4000, 0x00000000 )
                ROM_LOAD( "h04_ee09.bin", 0x14000, 0x2000, 0x00000000 )
                ROM_LOAD( "09m_ee02.bin", 0x18000, 0x4000, 0x00000000 )*/
                /* might use bank switching, not sure about e7 and e9 */

                ROM_REGION(0x16000);     /* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "05c_ee00.bin", 0x00000, 0x2000, 0xcadb75bd ); /* Characters */            	
                ROM_LOAD( "h01_ee08.bin", 0x02000, 0x4000, 0x96a65c1d ); /* 32x32 tiles planes 0-1 */
                ROM_LOAD( "a03_ee06.bin", 0x06000, 0x4000, 0x6039bdd1 ); /* 16x16 tiles planes 0-1 */
                ROM_LOAD( "a02_ee05.bin", 0x0a000, 0x4000, 0xb32d8252 ); /* 16x16 tiles planes 2-3 */
                ROM_LOAD( "j11_ee10.bin", 0x0e000, 0x4000, 0xbc83e265 ); /* Sprites planes 0-1 */
                ROM_LOAD( "j12_ee11.bin", 0x12000, 0x4000, 0x0e0f300d ); /* Sprites planes 2-3 */
                       
                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "11e_ee01.bin", 0x00000, 0x4000, 0x73cdf3b2 );
                
                ROM_REGION(0x4000);      /* For Tile background */
                ROM_LOAD( "c01_ee07.bin", 0x00000, 0x4000, 0x1ffca036 );/* Tile Map */
                
                ROM_END();
        }};
        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name)
        {
                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char RAM[]=Machine.memory_region[0];

                /* check if the hi score table has already been initialized */
                if ((memcmp(RAM,0xE680,new char[]{0x00,0x00,0x00},3) == 0) &&
                        (memcmp(RAM,0xE6CD,new char[]{0x24,0x1E,0x19},3) == 0))
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0xE680,1,0x50,f);
                                /* fix the score at the top */
                                memcpy(RAM,0xE600,RAM,0xE680,8);
                                fclose(f);
                        }

                        return 1;
                }
                else return 0;	/* we can't load the hi scores yet */
        }};



        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name)
	{
                FILE f;

                /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                char RAM[]=Machine.memory_region[0];


                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0xE680,1,0x50,f);
                        fclose(f);
                }

        }};

        public static GameDriver exedexes_driver = new GameDriver
        (
                "Exed Exes",
                "exedexes",
                "RICHARD DAVIES\nPAUL LEAMAN\nNICOLA SALMORIA\nMIRKO BUFFONI\nPAUL SWAN",
                machine_driver,

                exedexes_rom,
                null, null,
                null,

                input_ports,null,trak_ports, dsw, keys,

                color_prom, null, null,
                ORIENTATION_DEFAULT,

                hiload, hisave
        );
}