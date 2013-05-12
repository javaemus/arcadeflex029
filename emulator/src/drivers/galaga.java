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
 *  Notes : Roms are from v0.36 romset
 *
 *  galaganm is galaga 
 *  galaga is galagamw
 *  galagabl is galagab2
 */

package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static sndhrdw.pengo.*;
import static vidhrdw.generic.*;
import static vidhrdw.galaga.*;
import static machine.galaga.*;

public class galaga {
        static MemoryReadAddress readmem_cpu1[] =
        {
                new MemoryReadAddress( 0x8000, 0x9fff, galaga_sharedram_r, galaga_sharedram ),
                new MemoryReadAddress( 0x6800, 0x6807, galaga_dsw_r ),
                new MemoryReadAddress( 0x7000, 0x700f, galaga_customio_data_r ),
                new MemoryReadAddress( 0x7100, 0x7100, galaga_customio_r ),
                new MemoryReadAddress( 0x02b9, 0x02bd, galaga_hiscore_print_r ),
                new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryReadAddress readmem_cpu2[] =
        {
                new MemoryReadAddress( 0x8000, 0x9fff, galaga_sharedram_r ),
                new MemoryReadAddress( 0x6800, 0x6807, galaga_dsw_r ),
                new MemoryReadAddress( 0x0000, 0x1fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryReadAddress readmem_cpu3[] =
        {
                new MemoryReadAddress( 0x8000, 0x9fff, galaga_sharedram_r ),
                new MemoryReadAddress( 0x6800, 0x6807, galaga_dsw_r ),
                new MemoryReadAddress( 0x0000, 0x1fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem_cpu1[] =
        {
                new MemoryWriteAddress( 0x8000, 0x9fff, galaga_sharedram_w ),
                new MemoryWriteAddress( 0x6830, 0x6830, MWA_NOP ),
                new MemoryWriteAddress( 0x7000, 0x700f, galaga_customio_data_w ),
                new MemoryWriteAddress( 0x7100, 0x7100, galaga_customio_w ),
                new MemoryWriteAddress( 0xa000, 0xa005, MWA_RAM, galaga_starcontrol ),
                new MemoryWriteAddress( 0x6820, 0x6820, galaga_interrupt_enable_1_w ),
                new MemoryWriteAddress( 0x6822, 0x6822, galaga_interrupt_enable_3_w ),
                new MemoryWriteAddress( 0x6823, 0x6823, galaga_halt_w ),
                new MemoryWriteAddress( 0xa007, 0xa007, galaga_flipscreen_w ),
                new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
                new MemoryWriteAddress( 0x8b80, 0x8bff, MWA_RAM, spriteram, spriteram_size ),	/* these three are here just to initialize */
                new MemoryWriteAddress( 0x9380, 0x93ff, MWA_RAM, spriteram_2 ),	/* the pointers. The actual writes are */
                new MemoryWriteAddress( 0x9b80, 0x9bff, MWA_RAM, spriteram_3 ),	/* handled by galaga_sharedram_w() */
                new MemoryWriteAddress( 0x8000, 0x83ff, MWA_RAM, videoram, videoram_size ),	/* dirtybuffer[] handling is not needed because */
                new MemoryWriteAddress( 0x8400, 0x87ff, MWA_RAM, colorram ),	/* characters are redrawn every frame */
                new MemoryWriteAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem_cpu2[] =
        {
                new MemoryWriteAddress( 0x8000, 0x9fff, galaga_sharedram_w ),
                new MemoryWriteAddress( 0x6821, 0x6821, galaga_interrupt_enable_2_w ),
                new MemoryWriteAddress( 0x0000, 0x1fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem_cpu3[] =
        {
                new MemoryWriteAddress( 0x8000, 0x9fff, galaga_sharedram_w ),
                new MemoryWriteAddress( 0x6800, 0x681f, pengo_sound_w, pengo_soundregs ),
                new MemoryWriteAddress( 0x6822, 0x6822, galaga_interrupt_enable_3_w ),
                new MemoryWriteAddress( 0x0000, 0x1fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };

        static InputPortPtr galaga_input_ports= new InputPortPtr(){ public void handler() 
        {

                PORT_START();	/* DSW0 */
                PORT_DIPNAME( 0x07, 0x07, "Coinage", IP_KEY_NONE );
                PORT_DIPSETTING(    0x04, "4 Coins/1 Credit" );
                PORT_DIPSETTING(    0x02, "3 Coins/1 Credit" );
                PORT_DIPSETTING(    0x06, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x07, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x01, "2 Coins/3 Credits" );
                PORT_DIPSETTING(    0x03, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0x05, "1 Coin/3 Credits" );
                PORT_DIPSETTING(    0x00, "Free Play" );
                /* TODO: bonus scores are different for 5 lives */
                PORT_DIPNAME( 0x38, 0x38, "Bonus Life", IP_KEY_NONE );
                PORT_DIPSETTING(    0x20, "20K 60K 60K" );
                PORT_DIPSETTING(    0x18, "20K 60K" );
                PORT_DIPSETTING(    0x10, "20K 70K 70K" );
                PORT_DIPSETTING(    0x30, "20K 80K 80K" );
                PORT_DIPSETTING(    0x38, "30K 80K" );
                PORT_DIPSETTING(    0x08, "30K 100K 100K" );
                PORT_DIPSETTING(    0x28, "30K 120K 120K" );
                PORT_DIPSETTING(    0x00, "None" );
                PORT_DIPNAME( 0xc0, 0x80, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "2" );
                PORT_DIPSETTING(    0x80, "3" );
                PORT_DIPSETTING(    0x40, "4" );
                PORT_DIPSETTING(    0xc0, "5" );

                PORT_START();	/* DSW1 */
                PORT_DIPNAME( 0x01, 0x01, "2 Credits Game", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "1 Player" );
                PORT_DIPSETTING(    0x01, "2 Players" );
                PORT_DIPNAME( 0x06, 0x06, "Difficulty", IP_KEY_NONE );
                PORT_DIPSETTING(    0x06, "Easy" );
                PORT_DIPSETTING(    0x00, "Medium" );
                PORT_DIPSETTING(    0x02, "Hard" );
                PORT_DIPSETTING(    0x04, "Hardest" );
                PORT_DIPNAME( 0x08, 0x00, "Demo Sounds", IP_KEY_NONE );
                PORT_DIPSETTING(    0x08, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x10, 0x10, "Freeze", IP_KEY_NONE );
                PORT_DIPSETTING(    0x10, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_BITX(    0x20, 0x20, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Rack Test", OSD_KEY_F1, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x20, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x40, 0x40, "Unknown", IP_KEY_NONE );
                PORT_DIPSETTING(    0x40, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x80, 0x80, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Upright" );
                PORT_DIPSETTING(    0x00, "Cocktail" );

                PORT_START();	/* FAKE */
                /* The player inputs are not memory mapped, they are handled by an I/O chip. */
                /* These fake input ports are read by galaga_customio_data_r() */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY );
                PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_IMPULSE,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BIT( 0xe0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* FAKE */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL );
                PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_IMPULSE | IPF_COCKTAIL,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BIT( 0xe0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* FAKE */
                /* the button here is used to trigger the sound in the test screen */
                PORT_BITX(0x03, IP_ACTIVE_LOW, IPT_BUTTON1,	null, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 0 );
                PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_START1 | IPF_IMPULSE,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_START2 | IPF_IMPULSE,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_COIN1 | IPF_IMPULSE,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_COIN2 | IPF_IMPULSE,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BITX(0x40, IP_ACTIVE_LOW, IPT_COIN3 | IPF_IMPULSE,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                INPUT_PORTS_END();
        }};

/* same as galaga, dip switches are slightly different */
        static InputPortPtr galaganm_input_ports= new InputPortPtr(){ public void handler() 
        {
                PORT_START();	/* DSW0 */
                PORT_DIPNAME( 0x07, 0x07, "Coinage", IP_KEY_NONE );
                PORT_DIPSETTING(    0x04, "4 Coins/1 Credit" );
                PORT_DIPSETTING(    0x02, "3 Coins/1 Credit" );
                PORT_DIPSETTING(    0x06, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x07, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x01, "2 Coins/3 Credits" );
                PORT_DIPSETTING(    0x03, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0x05, "1 Coin/3 Credits" );
                PORT_DIPSETTING(    0x00, "Free Play" );
                /* TODO: bonus scores are different for 5 lives */
                PORT_DIPNAME( 0x38, 0x38, "Bonus Life", IP_KEY_NONE );
                PORT_DIPSETTING(    0x20, "20K 60K 60K" );
                PORT_DIPSETTING(    0x18, "20K 60K" );
                PORT_DIPSETTING(    0x10, "20K 70K 70K" );
                PORT_DIPSETTING(    0x30, "20K 80K 80K" );
                PORT_DIPSETTING(    0x38, "30K 80K" );
                PORT_DIPSETTING(    0x08, "30K 100K 100K" );
                PORT_DIPSETTING(    0x28, "30K 120K 120K" );
                PORT_DIPSETTING(    0x00, "None" );
                PORT_DIPNAME( 0xc0, 0x80, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "2" );
                PORT_DIPSETTING(    0x80, "3" );
                PORT_DIPSETTING(    0x40, "4" );
                PORT_DIPSETTING(    0xc0, "5" );

                PORT_START();	/* DSW1 */
                PORT_DIPNAME( 0x03, 0x03, "Difficulty", IP_KEY_NONE );
                PORT_DIPSETTING(    0x03, "Easy" );
                PORT_DIPSETTING(    0x00, "Medium" );
                PORT_DIPSETTING(    0x01, "Hard" );
                PORT_DIPSETTING(    0x02, "Hardest" );
                PORT_DIPNAME( 0x04, 0x04, "Unknown 1", IP_KEY_NONE );
                PORT_DIPSETTING(    0x04, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x08, 0x00, "Demo Sounds", IP_KEY_NONE );
                PORT_DIPSETTING(    0x08, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x10, 0x10, "Freeze", IP_KEY_NONE );
                PORT_DIPSETTING(    0x10, "Off" );
                PORT_DIPSETTING(    0x00, "On") ;
                PORT_BITX(    0x20, 0x20, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Rack Test", OSD_KEY_F1, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x20, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x40, 0x40, "Unknown 2", IP_KEY_NONE );
                PORT_DIPSETTING(    0x40, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x80, 0x80, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Upright" );
                PORT_DIPSETTING(    0x00, "Cocktail" );

                PORT_START();	/* FAKE */
                /* The player inputs are not memory mapped, they are handled by an I/O chip. */
                /* These fake input ports are read by galaga_customio_data_r() */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY );
                PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_IMPULSE,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BIT( 0xe0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* FAKE */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL );
                PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_IMPULSE | IPF_COCKTAIL,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BIT( 0xe0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* FAKE */
                /* the button here is used to trigger the sound in the test screen */
                PORT_BITX(0x03, IP_ACTIVE_LOW, IPT_BUTTON1,	null, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 0 );
                PORT_BITX(0x04, IP_ACTIVE_LOW, IPT_START1 | IPF_IMPULSE,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BITX(0x08, IP_ACTIVE_LOW, IPT_START2 | IPF_IMPULSE,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_COIN1 | IPF_IMPULSE,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BITX(0x20, IP_ACTIVE_LOW, IPT_COIN2 | IPF_IMPULSE,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BITX(0x40, IP_ACTIVE_LOW, IPT_COIN3 | IPF_IMPULSE,
                                IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                INPUT_PORTS_END();
        }};



        static GfxLayout charlayout = new GfxLayout
	(
                8,8,	       /* 8*8 characters */
                128,	       /* 128 characters */
                2,             /* 2 bits per pixel */
                new int[]{ 0, 4 },       /* the two bitplanes for 4 pixels are packed into one byte */
                new int[]{ 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },   /* characters are rotated 90 degrees */
                new int[]{ 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3 },   /* bits are packed in groups of four */
                16*8	       /* every char takes 16 bytes */
        );

        static GfxLayout spritelayout = new GfxLayout
	(
                16,16,	        /* 16*16 sprites */
                128,	        /* 128 sprites */
                2,	        /* 2 bits per pixel */
                 new int[]{ 0, 4 },	/* the two bitplanes for 4 pixels are packed into one byte */
                 new int[]{ 39 * 8, 38 * 8, 37 * 8, 36 * 8, 35 * 8, 34 * 8, 33 * 8, 32 * 8,
                                7 * 8, 6 * 8, 5 * 8, 4 * 8, 3 * 8, 2 * 8, 1 * 8, 0 * 8 },
                 new int[]{ 0, 1, 2, 3, 8*8, 8*8+1, 8*8+2, 8*8+3, 16*8+0, 16*8+1, 16*8+2, 16*8+3,
                                24*8+0, 24*8+1, 24*8+2, 24*8+3 },
                64*8	/* every sprite takes 64 bytes */
        );



        static GfxDecodeInfo gfxdecodeinfo[] =
	{
                new GfxDecodeInfo( 1, 0x0000, charlayout,       0, 32 ),
                new GfxDecodeInfo( 1, 0x1000, spritelayout,  32*4, 32 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        static char color_prom[] =
        {
                /* 5N - palette */
                0xF6,0x07,0x3F,0x27,0x2F,0xC7,0xF8,0xED,0x16,0x38,0x21,0xD8,0xC4,0xC0,0xA0,0x00,
                0xF6,0x07,0x3F,0x27,0x00,0xC7,0xF8,0xE8,0x00,0x38,0x00,0xD8,0xC5,0xC0,0x00,0x00,
                /* 2N - chars */
                0x0F,0x00,0x00,0x06,0x0F,0x0D,0x01,0x00,0x0F,0x02,0x0C,0x0D,0x0F,0x0B,0x01,0x00,
                0x0F,0x01,0x00,0x01,0x0F,0x00,0x00,0x02,0x0F,0x00,0x00,0x03,0x0F,0x00,0x00,0x05,
                0x0F,0x00,0x00,0x09,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x0F,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x0F,0x0B,0x07,0x06,0x0F,0x06,0x0B,0x07,0x0F,0x07,0x06,0x0B,0x0F,0x0F,0x0F,0x01,
                0x0F,0x0F,0x0B,0x0F,0x0F,0x02,0x0F,0x0F,0x0F,0x06,0x06,0x0B,0x0F,0x06,0x0B,0x0B,
                /* 1C - sprites */
                0x0F,0x08,0x0E,0x02,0x0F,0x05,0x0B,0x0C,0x0F,0x00,0x0B,0x01,0x0F,0x01,0x0B,0x02,
                0x0F,0x08,0x0D,0x02,0x0F,0x06,0x01,0x04,0x0F,0x09,0x01,0x05,0x0F,0x07,0x0B,0x01,
                0x0F,0x01,0x06,0x0B,0x0F,0x01,0x0B,0x00,0x0F,0x01,0x02,0x00,0x0F,0x00,0x01,0x06,
                0x0F,0x00,0x00,0x06,0x0F,0x03,0x0B,0x09,0x0F,0x06,0x02,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00
        };




        /* waveforms for the audio hardware */
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



        static MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                3125000,	/* 3.125 Mhz */
                                0,
                                readmem_cpu1,writemem_cpu1,null,null,
                                galaga_interrupt_1,100
                        ),
			new MachineCPU(
                                CPU_Z80,
                                3125000,	/* 3.125 Mhz */
                                2,	/* memory region #2 */
                                readmem_cpu2,writemem_cpu2,null,null,
                                galaga_interrupt_2,1
                        ),
			new MachineCPU(
                                CPU_Z80,
                                3125000,	/* 3.125 Mhz */
                                3,	/* memory region #3 */
                                readmem_cpu3,writemem_cpu3,null,null,
                                galaga_interrupt_3,2
                        )
                },
                60,
                galaga_init_machine,

                /* video hardware */
                28*8, 36*8, new rectangle( 0*8, 28*8-1, 0*8, 36*8-1 ),
                gfxdecodeinfo,
                32+64,64*4,	/* 32 for the characters, 64 for the stars */
                galaga_vh_convert_color_prom,
                VIDEO_TYPE_RASTER,
                null,
                galaga_vh_start,
                generic_vh_stop,
                galaga_vh_screenrefresh,

                /* sound hardware */
                samples,
                null,
                rallyx_sh_start,
                null,
                pengo_sh_update
        );



        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr galagamw_rom = new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code for the first CPU  */
                ROM_LOAD( "3200a.bin",    0x0000, 0x1000, 0x3ef0b053 );
                ROM_LOAD( "3300b.bin",    0x1000, 0x1000, 0x1b280831 );
                ROM_LOAD( "3400c.bin",    0x2000, 0x1000, 0x16233d33 );
                ROM_LOAD( "3500d.bin",    0x3000, 0x1000, 0x0aaf5c23 );

                ROM_REGION(0x3000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "07m_g08.bin",  0x0000, 0x1000, 0x58b2f47c );	
                ROM_LOAD( "07e_g10.bin",  0x1000, 0x1000, 0xad447c80 );
                ROM_LOAD( "07h_g09.bin",  0x2000, 0x1000, 0xdd6f1afc );

                ROM_REGION(0x10000);	/* 64k for the second CPU */
                ROM_LOAD( "3600e.bin",    0x0000, 0x1000, 0xbc556e76 );

                ROM_REGION(0x10000);	/* 64k for the third CPU  */
                ROM_LOAD( "3700g.bin",    0x0000, 0x1000, 0xb07f0aa4 );
                ROM_END();
        }};

        static RomLoadPtr galaga_rom = new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code for the first CPU  */
                ROM_LOAD( "04m_g01.bin",  0x0000, 0x1000, 0xa3a0f743 );
                ROM_LOAD( "04k_g02.bin",  0x1000, 0x1000, 0x43bb0d5c );
                ROM_LOAD( "04j_g03.bin",  0x2000, 0x1000, 0x753ce503 );
                ROM_LOAD( "04h_g04.bin",  0x3000, 0x1000, 0x83874442 );

                ROM_REGION(0x3000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "07m_g08.bin",  0x0000, 0x1000, 0x58b2f47c );
                ROM_LOAD( "07e_g10.bin",  0x1000, 0x1000, 0xad447c80 );
                ROM_LOAD( "07h_g09.bin",  0x2000, 0x1000, 0xdd6f1afc );

                ROM_REGION(0x10000);	/* 64k for the second CPU */
                ROM_LOAD( "04e_g05.bin",  0x0000, 0x1000, 0x3102fccd );

                ROM_REGION(0x10000);	/* 64k for the third CPU  */
                ROM_LOAD( "04d_g06.bin",  0x0000, 0x1000, 0x8995088d );
                ROM_END();
        }};

        static RomLoadPtr galagab2_rom = new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code for the first CPU  */
                ROM_LOAD( "g1",           0x0000, 0x1000, 0xab036c9f );
                ROM_LOAD( "g2",           0x1000, 0x1000, 0xd9232240 );
                ROM_LOAD( "04j_g03.bin",  0x2000, 0x1000, 0x753ce503 );
                ROM_LOAD( "g4",           0x3000, 0x1000, 0x499fcc76 );

                ROM_REGION(0x3000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "gallag.8",     0x0000, 0x1000, 0x169a98a4 );
                ROM_LOAD( "07e_g10.bin",  0x1000, 0x1000, 0xad447c80 );
                ROM_LOAD( "07h_g09.bin",  0x2000, 0x1000, 0xdd6f1afc );

                ROM_REGION(0x10000);	/* 64k for the second CPU */
                ROM_LOAD( "04e_g05.bin",  0x0000, 0x1000, 0x3102fccd );

                ROM_REGION(0x10000);	/* 64k for the third CPU  */
                ROM_LOAD( "04d_g06.bin",  0x0000, 0x1000, 0x8995088d );
                ROM_END();
        }};
        static RomLoadPtr gallag_rom = new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code for the first CPU  */
                ROM_LOAD( "04m_g01.bin",  0x0000, 0x1000, 0xa3a0f743 );
                ROM_LOAD( "gallag.2",     0x1000, 0x1000, 0x5eda60a7 );
                ROM_LOAD( "04j_g03.bin",  0x2000, 0x1000, 0x753ce503 );
                ROM_LOAD( "04h_g04.bin",  0x3000, 0x1000, 0x83874442 );

                ROM_REGION(0x3000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "gallag.8",     0x0000, 0x1000, 0x169a98a4 );
                ROM_LOAD( "07e_g10.bin",  0x1000, 0x1000, 0xad447c80 );
                ROM_LOAD( "07h_g09.bin",  0x2000, 0x1000, 0xdd6f1afc );

                ROM_REGION(0x10000);	/* 64k for the second CPU */
                ROM_LOAD( "04e_g05.bin",  0x0000, 0x1000, 0x3102fccd );

                ROM_REGION(0x10000);	/* 64k for the third CPU  */
                ROM_LOAD( "04d_g06.bin",  0x0000, 0x1000, 0x8995088d );
                ROM_END();
        }};



        static String galaga_sample_names[] =
        {
                "BANG.SAM",
                null	/* end of array */
        };


        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
           FILE f;

           /* get RAM pointer (this game is multiCPU, we can't assume the global */
           /* RAM pointer is pointing to the right place) */
            char[] RAM = Machine.memory_region[0];

           /* check if the hi score table has already been initialized */
           if (memcmp(RAM,0x8a4c,new char[] {0x18,0x6e},2) == 0)
           {
              if ((f = fopen(name,"rb")) != null)
              {
                 fread(RAM,0x8A20,1,45,f);
                 fclose(f);
                 galaga_hiscoreloaded = 1;
              }

              return 1;
           }
           else
              return 0; /* we can't load the hi scores yet */
        }};


        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name)
	{
           FILE f;

           /* get RAM pointer (this game is multiCPU, we can't assume the global */
           /* RAM pointer is pointing to the right place) */
           char[] RAM = Machine.memory_region[0];

           if ((f = fopen(name,"wb")) != null)
           {
              fwrite(RAM,0x8A20,1,45,f);
              fclose(f);
           }
        }};


        public static GameDriver galagamw_driver = new GameDriver
	(
                "Galaga (Midway)",
                "galagamw",
                "MARTIN SCRAGG\nNICOLA SALMORIA\nMIRKO BUFFONI",
                machine_driver,

                galagamw_rom,
                null, null,
                galaga_sample_names,

                null/*TBR*/,galaga_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                color_prom, null, null,

                ORIENTATION_DEFAULT,

                hiload, hisave
        );

        public static GameDriver galaga_driver = new GameDriver
	(
                "Galaga (Namco)",
                "galaga",
                "MARTIN SCRAGG\nNICOLA SALMORIA\nMIRKO BUFFONI",
                machine_driver,

                galaga_rom,
                 null, null,
                galaga_sample_names,

                null/*TBR*/,galaganm_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                color_prom, null, null,

                ORIENTATION_DEFAULT,

                hiload, hisave
        );

        public static GameDriver galagab2_driver = new GameDriver
	(
                "Galaga (bootleg)",
                "galagab2",
                "MARTIN SCRAGG\nNICOLA SALMORIA\nMIRKO BUFFONI",
                machine_driver,

                galagab2_rom,
                 null, null,
                galaga_sample_names,

                null/*TBR*/,galaganm_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                color_prom, null, null,

                ORIENTATION_DEFAULT,

                hiload, hisave
        );

        public static GameDriver gallag_driver = new GameDriver
	(
                "Gallag (bootleg Galaga)",
                "gallag",
                "MARTIN SCRAGG\nNICOLA SALMORIA\nMIRKO BUFFONI",
                machine_driver,

                gallag_rom,
                 null, null,
                galaga_sample_names,

                null/*TBR*/,galaganm_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                color_prom, null, null,

                ORIENTATION_DEFAULT,

                hiload, hisave
        );
    
}
