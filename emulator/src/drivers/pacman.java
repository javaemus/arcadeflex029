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
 * roms are from v0.36 romset
 * 
 *  crush is probably crush2 in v0.36 romset
 *  namcopac is npacmod
 *
 */

package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static sndhrdw.pengo.*;
import static machine.pacman.*;
import static vidhrdw.generic.*;
import static vidhrdw.pengo.*;
import static mame.inptport.*;
import static mame.memoryH.*;

public class pacman
{



	public static MemoryReadAddress readmem[] =
	{
                new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
                new MemoryReadAddress( 0x4000, 0x47ff, MRA_RAM ),	/* video and color RAM */
                new MemoryReadAddress( 0x4c00, 0x4fff, MRA_RAM ),	/* including sprite codes at 4ff0-4fff */
                new MemoryReadAddress( 0x5000, 0x503f, input_port_0_r ),	/* IN0 */
                new MemoryReadAddress( 0x5040, 0x507f, input_port_1_r ),	/* IN1 */
                new MemoryReadAddress( 0x5080, 0x50bf, input_port_2_r ),	/* DSW */
                new MemoryReadAddress( 0x8000, 0x9fff, MRA_ROM ),	/* Ms. Pac Man only */
		new MemoryReadAddress( -1 )	/* end of table */
	};

	public static MemoryWriteAddress writemem[] =
	{
                new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
                new MemoryWriteAddress( 0x4000, 0x43ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0x4400, 0x47ff, colorram_w, colorram ),
                new MemoryWriteAddress( 0x4c00, 0x4fef, MWA_RAM ),
                new MemoryWriteAddress( 0x4ff0, 0x4fff, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0x5000, 0x5000, interrupt_enable_w ),
                new MemoryWriteAddress( 0x5001, 0x5001, pengo_sound_enable_w ),
                new MemoryWriteAddress( 0x5002, 0x5002, MWA_NOP ),
                new MemoryWriteAddress( 0x5003, 0x5003, pengo_flipscreen_w ),
                new MemoryWriteAddress( 0x5004, 0x5007, MWA_NOP ),
                new MemoryWriteAddress( 0x5040, 0x505f, pengo_sound_w, pengo_soundregs ),
                new MemoryWriteAddress( 0x5060, 0x506f, MWA_RAM, spriteram_2 ),
                new MemoryWriteAddress( 0x50c0, 0x50c0, MWA_NOP ),
                new MemoryWriteAddress( 0x8000, 0x9fff, MWA_ROM ),	/* Ms. Pac Man only */
                new MemoryWriteAddress( 0xc000, 0xc3ff, videoram_w ),	/* mirror address for video ram, */
                new MemoryWriteAddress( 0xc400, 0xc7ef, colorram_w ),	/* used to display HIGH SCORE and CREDITS */
		new MemoryWriteAddress( -1 )	/* end of table */
	};

        static IOWritePort writeport[] =
        {
                new IOWritePort( 0, 0, interrupt_vector_w ),	/* Pac Man only */
                new IOWritePort( -1 )	/* end of table */
        };
        static InputPortPtr pacman_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY );
                PORT_BITX(    0x10, 0x10, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Rack Test", OSD_KEY_F1, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x10, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN3 );

                PORT_START();	/* IN1 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
                PORT_BITX(    0x10, 0x10, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x10, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
                PORT_DIPNAME( 0x80, 0x80, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Upright" );
                PORT_DIPSETTING(    0x00, "Cocktail" );

                PORT_START();	/* DSW */
                PORT_DIPNAME( 0x03, 0x01, "Coinage", IP_KEY_NONE );
                PORT_DIPSETTING(    0x03, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x01, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x02, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0x00, "Free Play" );
                PORT_DIPNAME( 0x0c, 0x08, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "1" );
                PORT_DIPSETTING(    0x04, "2" );
                PORT_DIPSETTING(    0x08, "3" );
                PORT_DIPSETTING(    0x0c, "5" );
                PORT_DIPNAME( 0x30, 0x00, "Bonus Life", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "10000" );
                PORT_DIPSETTING(    0x10, "15000" );
                PORT_DIPSETTING(    0x20, "20000" );
                PORT_DIPSETTING(    0x30, "None" );
                PORT_DIPNAME( 0x40, 0x40, "Difficulty", IP_KEY_NONE );
                PORT_DIPSETTING(    0x40, "Normal" );
                PORT_DIPSETTING(    0x00, "Hard" );
                PORT_DIPNAME( 0x80, 0x80, "Ghost Names", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Normal" );
                PORT_DIPSETTING(    0x00, "Alternate" );

                PORT_START();	/* FAKE */
                /* This fake input port is used to get the status of the fire button */
                /* and activate the speedup cheat if it is. */
                PORT_BITX(    0x01, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Speedup Cheat", OSD_KEY_CONTROL, 0/*OSD_JOY_FIRE1*/, 0 );
                PORT_DIPSETTING(    0x00, "Off" );
                PORT_DIPSETTING(    0x01, "On" );
                INPUT_PORTS_END();
        }};
        static InputPortPtr mspacman_input_ports= new InputPortPtr(){ public void handler()  
        {
        /* Ms. Pac Man input ports are identical to Pac Man, the only difference is */
        /* the missing Ghost Names dip switch. */
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY );
                PORT_BITX(    0x10, 0x10, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Rack Test", OSD_KEY_F1, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x10, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN3 );

                PORT_START();	/* IN1 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
                PORT_BITX(    0x10, 0x10, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x10, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
                PORT_DIPNAME( 0x80, 0x80, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Upright" );
                PORT_DIPSETTING(    0x00, "Cocktail" );

                PORT_START();	/* DSW */
                PORT_DIPNAME( 0x03, 0x01, "Coinage", IP_KEY_NONE );
                PORT_DIPSETTING(    0x03, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x01, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x02, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0x00, "Free Play" );
                PORT_DIPNAME( 0x0c, 0x08, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "1" );
                PORT_DIPSETTING(    0x04, "2" );
                PORT_DIPSETTING(    0x08, "3" );
                PORT_DIPSETTING(    0x0c, "5" );
                PORT_DIPNAME( 0x30, 0x00, "Bonus Life", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "10000" );
                PORT_DIPSETTING(    0x10, "15000" );
                PORT_DIPSETTING(    0x20, "20000" );
                PORT_DIPSETTING(    0x30, "None" );
                PORT_DIPNAME( 0x40, 0x40, "Difficulty", IP_KEY_NONE );
                PORT_DIPSETTING(    0x40, "Normal" );
                PORT_DIPSETTING(    0x00, "Hard" );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* FAKE */
                /* This fake input port is used to get the status of the fire button */
                /* and activate the speedup cheat if it is. */
                PORT_BITX(    0x01, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Speedup Cheat", OSD_KEY_CONTROL, 0/*OSD_JOY_FIRE1*/, 0 );
                PORT_DIPSETTING(    0x00, "Off" );
                PORT_DIPSETTING(    0x01, "On" );
                INPUT_PORTS_END();
        }};
        static InputPortPtr crush_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY );
                PORT_DIPNAME( 0x10, 0x00, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "Upright" );
                PORT_DIPSETTING(    0x10, "Cocktail" );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN3 );

                PORT_START();	/* IN1 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
                PORT_DIPNAME( 0x10, 0x10, "Unknown 1", IP_KEY_NONE );
                PORT_DIPSETTING(    0x10, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
                PORT_DIPNAME( 0x80, 0x80, "Unknown 2", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );

                PORT_START();	/* DSW */
                PORT_DIPNAME( 0x03, 0x01, "Coinage", IP_KEY_NONE );
                PORT_DIPSETTING(    0x03, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x01, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x02, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0x00, "Free Play" );
                PORT_DIPNAME( 0x0c, 0x00, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "3" );
                PORT_DIPSETTING(    0x04, "4" );
                PORT_DIPSETTING(    0x08, "5" );
                PORT_DIPSETTING(    0x0c, "6" );
                PORT_DIPNAME( 0x10, 0x10, "First Pattern", IP_KEY_NONE );
                PORT_DIPSETTING(    0x10, "Easy" );
                PORT_DIPSETTING(    0x00, "Hard" );
                PORT_DIPNAME( 0x20, 0x20, "Teleport holes", IP_KEY_NONE );
                PORT_DIPSETTING(    0x20, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x40, 0x40, "Unknown 3", IP_KEY_NONE );
                PORT_DIPSETTING(    0x40, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x80, 0x80, "Unknown 4", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                INPUT_PORTS_END();
        }};

	

	public static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 4},	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 }, /* characters are rotated 90 degrees */
		new int[] { 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3 },	/* bits are packed in groups of four */
		16*8	/* every char takes 16 bytes */
	);
	public static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		64,	/* 64 sprites */
		2,	/* 2 bits per pixel */
		new int[] { 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
		new int[] { 39 * 8, 38 * 8, 37 * 8, 36 * 8, 35 * 8, 34 * 8, 33 * 8, 32 * 8,
					7 * 8, 6 * 8, 5 * 8, 4 * 8, 3 * 8, 2 * 8, 1 * 8, 0 * 8 },
		new int[] { 8*8, 8*8+1, 8*8+2, 8*8+3, 16*8+0, 16*8+1, 16*8+2, 16*8+3,
					24*8+0, 24*8+1, 24*8+2, 24*8+3, 0, 1, 2, 3 },
		64*8	/* every sprite takes 64 bytes */
	);


	
	public static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,   0, 32 ),
		new GfxDecodeInfo( 1, 0x1000, spritelayout, 0, 32 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};

        static char pacplus_color_prom[] =
        {
                /* palette */
                0x00,0x3F,0x07,0xEF,0xF8,0x6F,0x38,0xC9,0xAF,0xAA,0x20,0xD5,0xBF,0x5D,0xED,0xF6,
                /* color lookup table */
                0x00,0x00,0x00,0x00,0x00,0x0F,0x07,0x02,0x00,0x00,0x00,0x00,0x00,0x0F,0x07,0x03,
                0x00,0x00,0x00,0x00,0x00,0x0F,0x07,0x04,0x00,0x00,0x00,0x00,0x00,0x0F,0x07,0x05,
                0x00,0x00,0x00,0x00,0x00,0x07,0x02,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x0F,0x00,0x08,0x00,0x01,0x0B,0x0F,
                0x00,0x08,0x00,0x09,0x00,0x06,0x08,0x07,0x00,0x0F,0x08,0x0F,0x00,0x00,0x00,0x00,
                0x00,0x0F,0x02,0x0D,0x00,0x0F,0x0A,0x06,0x00,0x01,0x0D,0x0C,0x00,0x0B,0x0F,0x0D,
                0x00,0x04,0x03,0x01,0x00,0x0F,0x07,0x00,0x00,0x08,0x00,0x09,0x00,0x08,0x00,0x09,
                0x00,0x00,0x00,0x00,0x00,0x0F,0x08,0x02,0x00,0x0F,0x07,0x08,0x00,0x08,0x00,0x0F
        };
	
	static char pacman_color_prom[] =
	{
		/* palette */
		0x00,0x07,0x66,0xEF,0x00,0xF8,0xEA,0x6F,0x00,0x3F,0x00,0xC9,0x38,0xAA,0xAF,0xF6,
		/* color lookup table */
		0x00,0x00,0x00,0x00,0x00,0x0F,0x0B,0x01,0x00,0x00,0x00,0x00,0x00,0x0F,0x0B,0x03,
		0x00,0x00,0x00,0x00,0x00,0x0F,0x0B,0x05,0x00,0x00,0x00,0x00,0x00,0x0F,0x0B,0x07,
		0x00,0x00,0x00,0x00,0x00,0x0B,0x01,0x09,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x0F,0x00,0x0E,0x00,0x01,0x0C,0x0F,
		0x00,0x0E,0x00,0x0B,0x00,0x0C,0x0B,0x0E,0x00,0x0C,0x0F,0x01,0x00,0x00,0x00,0x00,
		0x00,0x01,0x02,0x0F,0x00,0x07,0x0C,0x02,0x00,0x09,0x06,0x0F,0x00,0x0D,0x0C,0x0F,
		0x00,0x05,0x03,0x09,0x00,0x0F,0x0B,0x00,0x00,0x0E,0x00,0x0B,0x00,0x0E,0x00,0x0B,
		0x00,0x00,0x00,0x00,0x00,0x0F,0x0E,0x01,0x00,0x0F,0x0B,0x0E,0x00,0x0E,0x00,0x0F
	};

	static char crush_color_prom[] =
	{
		/* palette */
		0x00,0x07,0x66,0xEF,0x00,0xF8,0xEA,0x6F,0x00,0x3F,0x00,0xC9,0x38,0xAA,0xAF,0xF6,
		/* color lookup table */
		0x00,0x00,0x00,0x00,0x00,0x0f,0x0b,0x01,0x00,0x0f,0x0b,0x03,0x00,0x0f,0x0b,0x0f,
		0x00,0x0f,0x0b,0x07,0x00,0x0f,0x0b,0x05,0x00,0x0f,0x0b,0x0c,0x00,0x0f,0x0b,0x09,
		0x00,0x05,0x0b,0x07,0x00,0x0b,0x01,0x09,0x00,0x05,0x0b,0x01,0x00,0x02,0x05,0x01,
		0x00,0x02,0x0b,0x01,0x00,0x05,0x0b,0x09,0x00,0x0c,0x01,0x07,0x00,0x01,0x0c,0x0f,
		0x00,0x0f,0x00,0x0b,0x00,0x0c,0x05,0x0f,0x00,0x0f,0x0b,0x0e,0x00,0x0f,0x0b,0x0d,
		0x00,0x01,0x09,0x0f,0x00,0x09,0x0c,0x09,0x00,0x09,0x05,0x0f,0x00,0x05,0x0c,0x0f,
		0x00,0x01,0x07,0x0b,0x00,0x0f,0x0b,0x00,0x00,0x0f,0x00,0x0b,0x00,0x0b,0x05,0x09,
		0x00,0x0b,0x0c,0x0f,0x00,0x0b,0x07,0x09,0x00,0x02,0x0b,0x00,0x00,0x02,0x0b,0x07
	};



	/* waveforms for the audio hardware */
	static char samples[] =
	{
		0xff,0x11,0x22,0x33,0x44,0x55,0x55,0x66,0x66,0x66,0x55,0x55,0x44,0x33,0x22,0x11,
		0xff,0xdd,0xcc,0xbb,0xaa,0x99,0x99,0x88,0x88,0x88,0x99,0x99,0xaa,0xbb,0xcc,0xdd,

		0xff,0x44,0x66,0x66,0x55,0x33,0x11,0x22,0x33,0x33,0x22,0x11,0xee,0xcc,0xbb,0xdd,
		0xff,0x11,0x33,0x22,0x00,0xdd,0xcc,0xbb,0xbb,0xcc,0xdd,0xbb,0x99,0x88,0x88,0xaa,

		0xff,0x22,0x44,0x55,0x66,0x55,0x44,0x22,0xff,0xcc,0xaa,0x99,0x88,0x99,0xaa,0xcc,
		0xff,0x33,0x55,0x66,0x55,0x33,0xff,0xbb,0x99,0x88,0x99,0xbb,0xff,0x66,0xff,0x88,

		0xff,0x55,0x33,0x00,0x33,0x55,0x11,0xee,0x33,0x66,0x44,0xff,0x11,0x22,0xee,0xaa,
		0xff,0x44,0x00,0xcc,0xdd,0xff,0xaa,0x88,0xbb,0x00,0xdd,0x99,0xbb,0xee,0xbb,0x99,

		0x88,0x00,0x77,0xff,0x99,0x00,0x66,0xff,0xaa,0x00,0x55,0xff,0xbb,0x00,0x44,0xff,
		0xcc,0x00,0x33,0xff,0xdd,0x00,0x22,0xff,0xee,0x00,0x11,0xff,0xff,0x00,0x00,0xff,

		0xff,0x00,0xee,0x11,0xdd,0x22,0xcc,0x33,0xbb,0x44,0xaa,0x55,0x99,0x66,0x88,0x77,
		0x88,0x77,0x99,0x66,0xaa,0x55,0xbb,0x44,0xcc,0x33,0xdd,0x22,0xee,0x11,0xff,0x00,

		0x88,0x99,0xaa,0xbb,0xcc,0xdd,0xee,0xff,0x00,0x11,0x22,0x33,0x44,0x55,0x66,0x77,
		0x77,0x66,0x55,0x44,0x33,0x22,0x11,0x00,0xff,0xee,0xdd,0xcc,0xbb,0xaa,0x99,0x88,

		0x88,0x99,0xaa,0xbb,0xcc,0xdd,0xee,0xff,0x00,0x11,0x22,0x33,0x44,0x55,0x66,0x77,
		0x88,0x99,0xaa,0xbb,0xcc,0xdd,0xee,0xff,0x00,0x11,0x22,0x33,0x44,0x55,0x66,0x77
	};


	
	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				readmem, writemem, null, writeport,
				pacman_interrupt, 1
			)
		},
		60,
		pacman_init_machine,
		
		/* video hardware */
		28*8, 36*8, new rectangle( 0*8, 28*8-1, 0*8, 36*8-1 ),
		gfxdecodeinfo,
		16, 4 * 32,
		pengo_vh_convert_color_prom,
                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY,
		null,
		pacman_vh_start,
                generic_vh_stop,
                pengo_vh_screenrefresh,
		
		/* sound hardware */
		samples,
		null,
		null,
		null,
		pengo_sh_update
	);



	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	static RomLoadPtr pacman_rom= new RomLoadPtr(){ public void handler()  
        {
                    ROM_REGION(0x10000);	/* 64k for code */
		    ROM_LOAD( "namcopac.6e",  0x0000, 0x1000, 0xfee263b3 );
                    ROM_LOAD( "namcopac.6f",  0x1000, 0x1000, 0x39d1fc83 );
                    ROM_LOAD( "namcopac.6h",  0x2000, 0x1000, 0x02083b03 );
                    ROM_LOAD( "namcopac.6j",  0x3000, 0x1000, 0x7a36fe55 );
	
                     ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
		     ROM_LOAD( "pacman.5e",    0x0000, 0x1000, 0x0c944964 );
                     ROM_LOAD( "pacman.5f",    0x1000, 0x1000, 0x958fedf9 );
	             ROM_END();
        }};
        static RomLoadPtr npacmod_rom = new RomLoadPtr(){ public void handler()  
        {
        
                    ROM_REGION(0x10000);	/* 64k for code */
                    ROM_LOAD( "namcopac.6e",  0x0000, 0x1000, 0xfee263b3 );
                    ROM_LOAD( "namcopac.6f",  0x1000, 0x1000, 0x39d1fc83 );
                    ROM_LOAD( "namcopac.6h",  0x2000, 0x1000, 0x02083b03 );
                    ROM_LOAD( "npacmod.6j",   0x3000, 0x1000, 0x7d98d5f5 );

                    ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
                    ROM_LOAD( "pacman.5e",    0x0000, 0x1000, 0x0c944964 );
                    ROM_LOAD( "pacman.5f",    0x1000, 0x1000, 0x958fedf9 );
                    ROM_END();
        }};
        static RomLoadPtr pacmanjp_rom = new RomLoadPtr(){ public void handler()  
        {
                    ROM_REGION(0x10000);	/* 64k for code */
                    ROM_LOAD( "pacman.6e",    0x0000, 0x1000, 0xc1e6ab10 );
                    ROM_LOAD( "pacman.6f",    0x1000, 0x1000, 0x1a6fb2d4 );
                    ROM_LOAD( "pacman.6h",    0x2000, 0x1000, 0xbcdd1beb );
                    ROM_LOAD( "prg7",         0x3000, 0x0800, 0xb6289b26 );
                    ROM_LOAD( "prg8",         0x3800, 0x0800, 0x17a88c13 );

                    ROM_REGION(0x3000);	/* temporary space for graphics (disposed after conversion) */
                    ROM_LOAD( "chg1",         0x0000, 0x0800, 0x2066a0b7 );
                    ROM_LOAD( "chg2",         0x0800, 0x0800, 0x3591b89d );
                    ROM_LOAD( "pacman.5f",    0x1000, 0x1000, 0x958fedf9 );  
                    ROM_END();
        }};
        static RomLoadPtr pacmod_rom = new RomLoadPtr(){ public void handler()  
        {
		    ROM_REGION(0x10000);	/* 64k for code */
	            ROM_LOAD( "pacmanh.6e",   0x0000, 0x1000, 0x3b2ec270 );
                    ROM_LOAD( "pacman.6f",    0x1000, 0x1000, 0x1a6fb2d4 );
                    ROM_LOAD( "pacmanh.6h",   0x2000, 0x1000, 0x18811780 );
                    ROM_LOAD( "pacmanh.6j",   0x3000, 0x1000, 0x5c96a733 );
	
                    ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
                    ROM_LOAD( "pacmanh.5e",   0x0000, 0x1000, 0x299fb17a );
                    ROM_LOAD( "pacman.5f",    0x1000, 0x1000, 0x958fedf9 );
                    ROM_END();
        }};
        static RomLoadPtr pacplus_rom = new RomLoadPtr(){ public void handler()  
        {
                    ROM_REGION(0x10000);	/* 64k for code */
                    ROM_LOAD( "pacplus.6e",   0x0000, 0x1000, 0xd611ef68 );
                    ROM_LOAD( "pacplus.6f",   0x1000, 0x1000, 0xc7207556 );
                    ROM_LOAD( "pacplus.6h",   0x2000, 0x1000, 0xae379430 );
                    ROM_LOAD( "pacplus.6j",   0x3000, 0x1000, 0x5a6dff7b );

                    ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
                    ROM_LOAD( "pacplus.5e",   0x0000, 0x1000, 0x022c35da );
                    ROM_LOAD( "pacplus.5f",   0x1000, 0x1000, 0x4de65cdd );
                    ROM_END();
        }};
        static RomLoadPtr hangly_rom = new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "hangly.6e",    0x0000, 0x1000, 0x5fe8610a );
                ROM_LOAD( "hangly.6f",    0x1000, 0x1000, 0x73726586 );
                ROM_LOAD( "hangly.6h",    0x2000, 0x1000, 0x4e7ef99f );
                ROM_LOAD( "hangly.6j",    0x3000, 0x1000, 0x7f4147e6 );

                ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "pacman.5e",    0x0000, 0x1000, 0x0c944964 );
                ROM_LOAD( "pacman.5f",    0x1000, 0x1000, 0x958fedf9 );
                ROM_END();
        }};
        static RomLoadPtr puckman_rom = new RomLoadPtr(){ public void handler()  
        {
                     ROM_REGION(0x10000);	/* 64k for code */
                    ROM_LOAD( "puckman.6e",   0x0000, 0x1000, 0xa8ae23c5 );
                    ROM_LOAD( "pacman.6f",    0x1000, 0x1000, 0x1a6fb2d4 );
                    ROM_LOAD( "puckman.6h",   0x2000, 0x1000, 0x197443f8 );
                    ROM_LOAD( "puckman.6j",   0x3000, 0x1000, 0x2e64a3ba );

                     ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
                    ROM_LOAD( "pacman.5e",    0x0000, 0x1000, 0x0c944964 );
                    ROM_LOAD( "pacman.5f",    0x1000, 0x1000, 0x958fedf9 );
                    ROM_END();
        }};
        static RomLoadPtr piranha_rom = new RomLoadPtr(){ public void handler()  
        {
		 ROM_REGION(0x10000);	/* 64k for code */	
                ROM_LOAD( "pr1.cpu",      0x0000, 0x1000, 0xbc5ad024 );
                ROM_LOAD( "pacman.6f",    0x1000, 0x1000, 0x1a6fb2d4 );
                ROM_LOAD( "pr3.cpu",      0x2000, 0x1000, 0x473c379d );
                ROM_LOAD( "pr4.cpu",      0x3000, 0x1000, 0x63fbf895 );
                
		 ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "pr5.cpu",      0x0000, 0x0800, 0x3fc4030c );
                ROM_LOAD( "pr7.cpu",      0x0800, 0x0800, 0x30b9a010 );
		ROM_LOAD( "pr6.cpu",      0x1000, 0x0800, 0xf3e9c9d5 );
                ROM_LOAD( "pr8.cpu",      0x1800, 0x0800, 0x133d720d );
                ROM_END();
        }};
		
        static RomLoadPtr mspacman_rom = new RomLoadPtr(){ public void handler()  
        {      
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "boot1",        0x0000, 0x1000, 0xd16b31b7 );
                ROM_LOAD( "boot2",        0x1000, 0x1000, 0x0d32de5e );
                ROM_LOAD( "boot3",        0x2000, 0x1000, 0x1821ee0b );
                ROM_LOAD( "boot4",        0x3000, 0x1000, 0x165a9dd8 );
                ROM_LOAD( "boot5",        0x8000, 0x1000, 0x8c3e6de6 );
                ROM_LOAD( "boot6",        0x9000, 0x1000, 0x368cb165 );
	
		ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "5e",           0x0000, 0x1000, 0x5c281d01 );
                ROM_LOAD( "5f",           0x1000, 0x1000, 0x615af909 );
                ROM_END();
        }};
        static RomLoadPtr mspacatk_rom = new RomLoadPtr(){ public void handler()  
        {
                 ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "boot1",        0x0000, 0x1000, 0xd16b31b7 );
                ROM_LOAD( "mspacatk.2",   0x1000, 0x1000, 0x0af09d31 );
                ROM_LOAD( "boot3",        0x2000, 0x1000, 0x1821ee0b );
                ROM_LOAD( "boot4",        0x3000, 0x1000, 0x165a9dd8 );
                ROM_LOAD( "mspacatk.5",   0x8000, 0x1000, 0xe6e06954 );
                ROM_LOAD( "mspacatk.6",   0x9000, 0x1000, 0x3b5db308 );

                 ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "5e",           0x0000, 0x1000, 0x5c281d01 );
                ROM_LOAD( "5f",           0x1000, 0x1000, 0x615af909 );
                ROM_END();
        }};
        static RomLoadPtr crush2_rom = new RomLoadPtr(){ public void handler()  
        {           
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "tp1",          0x0000, 0x0800, 0xf276592e );
                ROM_LOAD( "tp5a",         0x0800, 0x0800, 0x3d302abe );
                ROM_LOAD( "tp2",          0x1000, 0x0800, 0x25f42e70 );
                ROM_LOAD( "tp6",          0x1800, 0x0800, 0x98279cbe );
                ROM_LOAD( "tp3",          0x2000, 0x0800, 0x8377b4cb );
                ROM_LOAD( "tp7",          0x2800, 0x0800, 0xd8e76c8c );
                ROM_LOAD( "tp4",          0x3000, 0x0800, 0x90b28fa3 );
                ROM_LOAD( "tp8",          0x3800, 0x0800, 0x10854e1b );
                	
		ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "tpa",          0x0000, 0x0800, 0xc7617198 );
                ROM_LOAD( "tpc",          0x0800, 0x0800, 0xe129d76a );
                ROM_LOAD( "tpb",          0x1000, 0x0800, 0xd1899f05 );
                ROM_LOAD( "tpd",          0x1800, 0x0800, 0xd35d1caf );        
                ROM_END();
        }};



	static int resetcount;
	static HiscoreLoadPtr pacman_hiload = new HiscoreLoadPtr() { public int handler()
	{


		/* during a reset, leave time to the game to clear the screen */
		if (++resetcount < 60) return 0;

		/* wait for "HIGH SCORE" to be on screen */
/*TOFIX		if (memcmp(RAM, 0x43d1, new char[] { 0x48, 0x47, 0x49, 0x48 }, 2) == 0)
		{
			FILE f;


			resetcount = 0;

			if ((f = fopen(name, "rb")) != null)
			{
				String buf;
				int hi;


				fread(RAM, 0x4e88, 1, 4, f);
				/* also copy the high score to the screen, otherwise it won't be */
				/* updated */
	/*TOFIX			hi = (RAM[0x4e88] & 0x0f) +
						(RAM[0x4e88] >> 4) * 10 +
						(RAM[0x4e89] & 0x0f) * 100 +
						(RAM[0x4e89] >> 4) * 1000 +
						(RAM[0x4e8a] & 0x0f) * 10000 +
						(RAM[0x4e8a] >> 4) * 100000 +
						(RAM[0x4e8b] & 0x0f) * 1000000 +
						(RAM[0x4e8b] >> 4) * 10000000;
				if (hi != 0)
				{
					buf = sprintf("%8d", hi);
					if (buf.charAt(2) != ' ') videoram_w.handler(0x03f2, (char) (buf.charAt(2)-'0'));
					if (buf.charAt(3) != ' ') videoram_w.handler(0x03f1, (char) (buf.charAt(3)-'0'));
					if (buf.charAt(4) != ' ') videoram_w.handler(0x03f0, (char) (buf.charAt(4)-'0'));
					if (buf.charAt(5) != ' ') videoram_w.handler(0x03ef, (char) (buf.charAt(5)-'0'));
					if (buf.charAt(6) != ' ') videoram_w.handler(0x03ee, (char) (buf.charAt(6)-'0'));
					cpu_writemem(0x43ed, (char) (buf.charAt(7)-'0'));
				}
				fclose(f);
			}

			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };



	static HiscoreSavePtr pacman_hisave = new HiscoreSavePtr() { public void handler()
	{
		FILE f;


/*TOFIX		if ((f = fopen(name, "wb")) != null)
		{
			fwrite(RAM, 0x4e88, 1, 4, f);
			fclose(f);
		}*/
	} };



	static HiscoreLoadPtr crush_hiload = new HiscoreLoadPtr() { public int handler()
	{


		/* during a reset, leave time to the game to clear the screen */
/*TOFIX		if (++resetcount < 60) return 0;

		/* wait for "HI SCORE" to be on screen */
/*TOFIX		if (memcmp(RAM, 0x43d0, new char[] { 0x53, 0x40, 0x49, 0x48 }, 2) == 0)
		{
			FILE f;


			resetcount = 0;

			if ((f = fopen(name, "rb")) != null)
			{
				String buf;
				int hi;


				fread(RAM, 0x4c80, 1, 3, f);
				/* also copy the high score to the screen, otherwise it won't be */
				/* updated */
	/*TOFIX			hi = (RAM[0x4c82] & 0x0f) +
						(RAM[0x4c82] >> 4) * 10 +
						(RAM[0x4c81] & 0x0f) * 100 +
						(RAM[0x4c81] >> 4) * 1000 +
						(RAM[0x4c80] & 0x0f) * 10000 +
						(RAM[0x4c80] >> 4) * 100000;
				if (hi != 0)
				{
					buf = sprintf("%8d", hi);
					if (buf.charAt(2) != ' ') videoram_w.handler(0x03f3, (char) (buf.charAt(2)-'0'));
					if (buf.charAt(3) != ' ') videoram_w.handler(0x03f2, (char) (buf.charAt(3)-'0'));
					if (buf.charAt(4) != ' ') videoram_w.handler(0x03f1, (char) (buf.charAt(4)-'0'));
					if (buf.charAt(5) != ' ') videoram_w.handler(0x03f0, (char) (buf.charAt(5)-'0'));
					if (buf.charAt(6) != ' ') videoram_w.handler(0x03ef, (char) (buf.charAt(6)-'0'));
					cpu_writemem(0x43ee, (char) (buf.charAt(7)-'0'));
				}
				fclose(f);
			}

			return 1;
		}
		else */return 0;	/* we can't load the hi scores yet */
	} };



	static HiscoreSavePtr crush_hisave = new HiscoreSavePtr() { public void handler()
	{
		FILE f;


	/*TOFIX	if ((f = fopen(name, "wb")) != null)
		{
			fwrite(RAM, 0x4c80, 1, 3, f);
			fclose(f);
		}*/
	} };



	public static GameDriver pacman_driver = new GameDriver
	(
                "Pac Man (Midway)",
		"pacman",
                "Allard van der Bas (original code)\nNicola Salmoria (MAME driver)",
		machine_driver,
	
		pacman_rom,
		null, null,
		null,
	
		null/*TBR*/,pacman_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,
	
		pacman_color_prom, null, null,

		ORIENTATION_DEFAULT,
	
		pacman_hiload, pacman_hisave
	);
        public static GameDriver npacmod_driver = new GameDriver
        (
                "Pac Man (Namco)",
                "npacmod",
                "Allard van der Bas (original code)\nNicola Salmoria (MAME driver)",
                machine_driver,

                npacmod_rom,
                        null, null,
                        null,

                null/*TBR*/,pacman_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                pacman_color_prom,  null, null,
                ORIENTATION_DEFAULT,

                pacman_hiload, pacman_hisave
        );

        public static GameDriver pacmanjp_driver = new GameDriver
        (
                "Pac Man (Namco, alternate)",
                "pacmanjp",
               "Allard van der Bas (original code)\nNicola Salmoria (MAME driver)",
                machine_driver,

                pacmanjp_rom,
                        null, null,
                        null,

                null/*TBR*/,pacman_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                pacman_color_prom, null, null,
               ORIENTATION_DEFAULT,

                pacman_hiload, pacman_hisave
        );
	public static GameDriver pacmod_driver = new GameDriver
	(
                "Pac Man (modified)",
		"pacmod",
                "Allard van der Bas (original code)\nNicola Salmoria (MAME driver)",
		machine_driver,
	
		pacmod_rom,
		null, null,
		null,
	
		null/*TBR*/,pacman_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,
	
		pacman_color_prom, null, null,

		ORIENTATION_DEFAULT,
	
		pacman_hiload, pacman_hisave
	);
	
	public static GameDriver pacplus_driver = new GameDriver
	(
                "Pac Man with Pac Man Plus graphics",
		"pacplus",
               "Allard van der Bas (original code)\nNicola Salmoria (MAME driver)",
		machine_driver,
	
		pacplus_rom,
		null, null,
		null,
	
		null/*TBR*/,pacman_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,
	
		pacplus_color_prom, null, null,

		ORIENTATION_DEFAULT,
	
		pacman_hiload, pacman_hisave
	);
	
	public static GameDriver hangly_driver = new GameDriver
	(
                "Hangly Man",
		"hangly",
                "Allard van der Bas (original code)\nNicola Salmoria (MAME driver)",
		machine_driver,
	
		hangly_rom,
		null, null,
		null,
	
		null/*TBR*/,pacman_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,
	
		pacman_color_prom, null, null,

		ORIENTATION_DEFAULT,
	
		pacman_hiload, pacman_hisave
	);
	
	public static GameDriver puckman_driver = new GameDriver
	(
                "Puck Man",
		"puckman",
                "Allard van der Bas (original code)\nNicola Salmoria (MAME driver)",
		machine_driver,
	
		puckman_rom,
		null, null,
		null,
	
		null/*TBR*/,pacman_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,
	
		pacman_color_prom, null, null,
		ORIENTATION_DEFAULT,
	
		pacman_hiload, pacman_hisave
	);
	
	public static GameDriver piranha_driver = new GameDriver
	(
                "Piranha",
		"piranha",
               "Allard van der Bas (original code)\nNicola Salmoria (MAME driver)",
		machine_driver,
	
		piranha_rom,
		null, null,
		null,
	
		null/*TBR*/,mspacman_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,
	
		pacman_color_prom, null, null,
		ORIENTATION_DEFAULT, 
	
		pacman_hiload, pacman_hisave
	);
	
	public static GameDriver mspacman_driver = new GameDriver
	(
                "Ms. Pac Man",
		"mspacman",
               "Allard van der Bas (original code)\nNicola Salmoria (MAME driver)",
		machine_driver,
	
		mspacman_rom,
		null, null,
		null,
	
		null/*TBR*/,mspacman_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,
	
		pacman_color_prom, null, null,
		ORIENTATION_DEFAULT,
	
		pacman_hiload, pacman_hisave
	);
        public static GameDriver mspacatk_driver = new GameDriver
        (
            "Ms. Pac Man Attacks",
            "mspacatk",
            "Allard van der Bas (original code)\nNicola Salmoria (MAME driver)",
            machine_driver,

            mspacatk_rom,
                    null, null,
                    null,

            null/*TBR*/,mspacman_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

            pacman_color_prom, null, null,
            ORIENTATION_DEFAULT,

            pacman_hiload, pacman_hisave
        );

	public static GameDriver crush2_driver = new GameDriver
	(
                "Crush Roller (bootleg)",
		"crush2",
                "Allard van der Bas (original code)\nNicola Salmoria (MAME driver)\nGary Walton (color info)\nSimon Walls (color info)",
		machine_driver,
	
		crush2_rom,
		null, null,
		null,
	
		null/*TBR*/,crush_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,
	
		crush_color_prom, null, null,

		ORIENTATION_DEFAULT,
	
		crush_hiload, crush_hisave
	);
}

