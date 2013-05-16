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
 *
 * romset are from 0.36 romset
 *
 */


package drivers;
import static machine.mcr.*;
import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static vidhrdw.mcr2.*;
import static sndhrdw.mcr.*;
import static mame.inptport.*;
import static sndhrdw._8910intf.*;
import static mame.memoryH.*;
public class mcr2 {
    
        static MemoryReadAddress mcr2_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0xbfff, MRA_ROM ),
                new MemoryReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
                new MemoryReadAddress( 0xf000, 0xffff, MRA_RAM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };

        static MemoryWriteAddress mcr2_writemem[] =
        {
                new MemoryWriteAddress( 0xc000, 0xc7ff, MWA_RAM ),
                new MemoryWriteAddress( 0x0000, 0xbfff, MWA_ROM ),
                new MemoryWriteAddress( 0xf000, 0xf7ff, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0xf800, 0xff7f, mcr2_videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0xff80, 0xffff, mcr2_palette_w, mcr2_paletteram ),
                new MemoryWriteAddress( -1 )  /* end of table */
        };

        static MemoryReadAddress journey_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0xbfff, MRA_ROM ),
                new MemoryReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
                new MemoryReadAddress( 0xf000, 0xf1ff, MRA_RAM ),
                new MemoryReadAddress( 0xf800, 0xffff, MRA_RAM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };

        static MemoryWriteAddress journey_writemem[] =
        {
                new MemoryWriteAddress( 0xc000, 0xc7ff, MWA_RAM ),
                new MemoryWriteAddress( 0x0000, 0xbfff, MWA_ROM ),
                new MemoryWriteAddress( 0xf000, 0xf1ff, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0xf800, 0xff7f, mcr2_videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0xff80, 0xffff, mcr2_palette_w, mcr2_paletteram ),
                new MemoryWriteAddress( -1 )  /* end of table */
        };


        static MemoryReadAddress sound_readmem[] =
        {
                new MemoryReadAddress( 0x8000, 0x83ff, MRA_RAM ),
                new MemoryReadAddress( 0x9000, 0x9003, mcr_soundlatch_r ),
                new MemoryReadAddress( 0xa001, 0xa001, AY8910_read_port_0_r ),
                new MemoryReadAddress( 0xb001, 0xb001, AY8910_read_port_1_r ),
                new MemoryReadAddress( 0xf000, 0xf000, input_port_5_r ),
                new MemoryReadAddress( 0xe000, 0xe000, MRA_NOP ),
                new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress sound_writemem[] =
        {
                new MemoryWriteAddress( 0x8000, 0x83ff, MWA_RAM ),
                new MemoryWriteAddress( 0xa000, 0xa000, AY8910_control_port_0_w ),
                new MemoryWriteAddress( 0xa002, 0xa002, AY8910_write_port_0_w ),
                new MemoryWriteAddress( 0xb000, 0xb000, AY8910_control_port_1_w ),
                new MemoryWriteAddress( 0xb002, 0xb002, AY8910_write_port_1_w ),
                new MemoryWriteAddress( 0xc000, 0xc000, mcr_soundstatus_w ),
                new MemoryWriteAddress( 0xe000, 0xe000, MWA_NOP ),
                new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };


        static IOReadPort readport[] =
        {
           new IOReadPort( 0x00, 0x00, input_port_0_r ),
           new IOReadPort( 0x01, 0x01, input_port_1_r ),
           new IOReadPort( 0x02, 0x02, input_port_2_r ),
           new IOReadPort( 0x03, 0x03, input_port_3_r ),
           new IOReadPort( 0x04, 0x04, input_port_4_r ),
           new IOReadPort( 0x05, 0xff, mcr_readport ),
           new IOReadPort( -1 )
        };

        static IOReadPort wacko_readport[] =
        {
           new IOReadPort( 0x00, 0x00, input_port_0_r ),
           new IOReadPort( 0x01, 0x01, wacko_trakball_x_r ), /* trackball x */
           new IOReadPort( 0x02, 0x02, wacko_trakball_y_r ), /* trackball y */
           new IOReadPort( 0x03, 0x03, input_port_3_r ),
           new IOReadPort( 0x04, 0x04, input_port_4_r ),
           new IOReadPort( 0x05, 0xff, mcr_readport ),
           new IOReadPort( -1 )
        };

        static IOReadPort kroozr_readport[] =
        {
           new IOReadPort( 0x00, 0x00, input_port_0_r ),
           new IOReadPort( 0x01, 0x01, kroozr_dial_r ), /* firing spinner */
           new IOReadPort( 0x02, 0x02, kroozr_trakball_x_r ), /* x-axis */
           new IOReadPort( 0x03, 0x03, input_port_3_r ),
           new IOReadPort( 0x04, 0x04, kroozr_trakball_y_r ), /* y-axis */
           new IOReadPort( 0x05, 0xff, mcr_readport ),
           new IOReadPort( -1 )
        };

        static IOWritePort writeport[] =
        {
           new IOWritePort( 0, 0xFF, mcr_writeport ),
           new IOWritePort( -1 )	/* end of table */
        };
        static InputPortPtr tron_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_TILT );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN3 );
                PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );

                PORT_START();	/* IN1 -- controls spinner */
                PORT_ANALOG( 0xff, 0x00, IPT_DIAL | IPF_REVERSE, 50 );

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT | IPF_8WAY | IPF_COCKTAIL );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT | IPF_8WAY | IPF_COCKTAIL );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP | IPF_8WAY | IPF_COCKTAIL );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN | IPF_8WAY | IPF_COCKTAIL );

                PORT_START();	/* IN3 -- dipswitches */
                PORT_DIPNAME( 0x01, 0x00, "Coin Meters", IP_KEY_NONE );
                PORT_DIPSETTING(    0x01, "1" );
                PORT_DIPSETTING(    0x00, "2" );
                PORT_DIPNAME( 0x02, 0x00, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "Upright" );
                PORT_DIPSETTING(    0x02, "Cocktail" );
                PORT_DIPNAME( 0x04, 0x00, "Allow Buy In", IP_KEY_NONE );
                PORT_DIPSETTING(    0x04, "No" );
                PORT_DIPSETTING(    0x00, "Yes" );
                PORT_BIT( 0xf8, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN4 */
                PORT_ANALOG( 0xff, 0x00, IPT_DIAL | IPF_REVERSE | IPF_COCKTAIL, 50 );

                PORT_START();	/* AIN0 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );
                INPUT_PORTS_END();
        }};
        static InputPortPtr twotiger_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BITX(0x10, IP_ACTIVE_LOW, IPT_START1, "Dogfight Start", OSD_KEY_6, IP_JOY_NONE, 0 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_TILT );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN3 );
                PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );

                PORT_START();	/* IN1 -- player 1 spinner */
                PORT_ANALOG( 0xff, 0x00, IPT_DIAL | IPF_REVERSE, 50 );

                PORT_START();;	/* IN2 -- buttons for player 1 & player 2 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
                PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN3 -- dipswitches */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN4 -- player 2 spinner */
                PORT_ANALOG( 0xff, 0x00, IPT_DIAL | IPF_REVERSE | IPF_PLAYER2, 50 );

                PORT_START();	/* AIN0 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );
                INPUT_PORTS_END();
        }};

        static InputPortPtr domino_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_TILT );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );

                PORT_START();	/* IN1 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );

                PORT_START();	/* IN2 unused */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN3 -- dipswitches */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );

                PORT_START();	/* IN4 unused */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* AIN0 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );
                INPUT_PORTS_END();
        }};

        static InputPortPtr shollow_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_TILT );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );

                PORT_START();	/* IN1 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_2WAY );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_2WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );

                PORT_START();	/* IN2 unused */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN3 -- dipswitches */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );

                PORT_START();	/* IN4 unused */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* AIN0 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );
                INPUT_PORTS_END();
        }};
        
        static InputPortPtr wacko_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_TILT );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );

                PORT_START();	/* IN1 -- controls joystick x-axis */
                PORT_BIT( 0xff, IP_ACTIVE_HIGH, IPT_UNUSED );

                PORT_START();	/* IN2 -- controls joystick y-axis */
                PORT_BIT( 0xff, IP_ACTIVE_HIGH, IPT_UNUSED );

                PORT_START();	/* IN3 -- dipswitches */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );

                PORT_START();	/* IN4 -- 4-way firing joystick */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY );
                PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* AIN0 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );
                INPUT_PORTS_END();
        }};
        static InputPortPtr kroozr_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_TILT );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );

                PORT_START();	/* IN1 -- controls firing spinner */
                PORT_BIT( 0x07, IP_ACTIVE_HIGH, IPT_UNUSED );
                PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
                PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNUSED );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 );

                PORT_START();	/* IN2 -- controls joystick x-axis */
                PORT_BIT( 0xff, IP_ACTIVE_HIGH, IPT_UNUSED );

                PORT_START();	/* IN3 -- dipswitches */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );

                PORT_START();	/* IN4 -- controls joystick y-axis */
                PORT_BIT( 0xff, IP_ACTIVE_HIGH, IPT_UNUSED );

                PORT_START();	/* AIN0 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );

                PORT_START();	/* dummy extra port for keyboard movement */
                PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
                PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY );
                PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY );
                PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY );
                PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );

                PORT_START();	/* dummy extra port for dial control */
                PORT_ANALOG( 0xff, 0x00, IPT_DIAL | IPF_REVERSE, 40 );
                INPUT_PORTS_END();
        }};



        static TrakPort wacko_trak_ports[] =
        {
                new TrakPort(
                        X_AXIS,
                        1,
                        -0.5,
                        wacko_trakball_xy
                ),
                new TrakPort(
                        Y_AXIS,
                        1,
                        0.5,
                        wacko_trakball_xy
                ),
           new TrakPort( -1 )
        };


        /* 512 characters; used by all the mcr2 games */
        static GfxLayout mcr2_charlayout_512 = new GfxLayout
	(
                16, 16,
                512,
                4,
                new int[]{ 512*16*8, 512*16*8+1, 0, 1 },
                new int[]{ 0, 0, 2, 2, 4, 4, 6, 6, 8, 8, 10, 10, 12, 12, 14, 14 },
                new int[]{ 0, 0, 2*8, 2*8, 4*8, 4*8, 6*8, 6*8, 8*8, 8*8, 10*8, 10*8, 12*8, 12*8, 14*8, 14*8 },
                16*8
        );

        /* 64 sprites; used by all mcr2 games but Journey */

        static GfxLayout mcr2_spritelayout_64 = new GfxLayout
	(
           32,32,
           64,
           4,
           new int[]{ 0, 1, 2, 3 },
           new int[]{  64*128*8*3+0, 64*128*8*3+4, 64*128*8*2+0, 64*128*8*2+4, 64*128*8+0, 64*128*8+4, 0, 4, 64*128*8*3+8, 64*128*8*3+12, 64*128*8*2+8, 64*128*8*2+12, 64*128*8+8, 64*128*8+12, 8, 12,
              64*128*8*3+16, 64*128*8*3+20, 64*128*8*2+16, 64*128*8*2+20, 64*128*8+16, 64*128*8+20, 16, 20, 64*128*8*3+24, 64*128*8*3+28, 64*128*8*2+24, 64*128*8*2+28,
              64*128*8+24, 64*128*8+28, 24, 28 },
           new int[]{  0, 32, 32*2, 32*3, 32*4, 32*5, 32*6, 32*7, 32*8, 32*9, 32*10, 32*11,
              32*12, 32*13, 32*14, 32*15, 32*16, 32*17, 32*18, 32*19, 32*20, 32*21,
              32*22, 32*23, 32*24, 32*25, 32*26, 32*27, 32*28, 32*29, 32*30, 32*31 },
           128*8
        );


        /* 128 sprites; used by Journey - it features an mcr3 spriteboard */
        static GfxLayout mcr3_spritelayout_128 = new GfxLayout
	(
           32,32,
           128,
           4,
           new int[]{ 0, 1, 2, 3 },
           new int[]{  128*128*8*3+0, 128*128*8*3+4, 128*128*8*2+0, 128*128*8*2+4, 128*128*8+0, 128*128*8+4, 0, 4, 128*128*8*3+8, 128*128*8*3+12, 128*128*8*2+8, 128*128*8*2+12, 128*128*8+8, 128*128*8+12, 8, 12,
              128*128*8*3+16, 128*128*8*3+20, 128*128*8*2+16, 128*128*8*2+20, 128*128*8+16, 128*128*8+20, 16, 20, 128*128*8*3+24, 128*128*8*3+28, 128*128*8*2+24, 128*128*8*2+28,
              128*128*8+24, 128*128*8+28, 24, 28 },
           new int[]{  0, 32, 32*2, 32*3, 32*4, 32*5, 32*6, 32*7, 32*8, 32*9, 32*10, 32*11,
              32*12, 32*13, 32*14, 32*15, 32*16, 32*17, 32*18, 32*19, 32*20, 32*21,
              32*22, 32*23, 32*24, 32*25, 32*26, 32*27, 32*28, 32*29, 32*30, 32*31 },
           128*8
        );


        static GfxDecodeInfo mcr2_gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, mcr2_charlayout_512,  0, 8 ),
                new GfxDecodeInfo( 1, 0x4000, mcr2_spritelayout_64, 0, 8 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };

        static GfxDecodeInfo journey_gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, mcr2_charlayout_512,   0, 8 ),
                new GfxDecodeInfo( 1, 0x4000, mcr3_spritelayout_128, 0, 8 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };

        public static MachineDriver  tron_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                2500000,	/* 2.5 Mhz */
                                0,
                                mcr2_readmem,mcr2_writemem,readport,writeport,
                                mcr_interrupt,32
                        ),
                        new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2000000,	/* 2 Mhz */
                                2,	/* memory region #2 */
                                sound_readmem,sound_writemem,null,null,
                                mcr_sh_interrupt,26
                        )
                },
                30,
                mcr_init_machine,

                /* video hardware */
                32*16, 30*16, new rectangle( 0, 32*16-1, 0, 30*16-1 ),
                mcr2_gfxdecodeinfo,
                1+8*16, 8*16,
                mcr2_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
                null,
                generic_vh_start,
                generic_vh_stop,
                mcr2_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                mcr_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );

        public static MachineDriver  domino_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                2500000,	/* 2.5 Mhz */
                                0,
                                mcr2_readmem,mcr2_writemem,readport,writeport,
                                mcr_interrupt,32
                        ),
                        new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2000000,	/* 2 Mhz */
                                2,	/* memory region #2 */
                                sound_readmem,sound_writemem,null,null,
                                mcr_sh_interrupt,26
                        )
                },
                30,
                mcr_init_machine,

                /* video hardware */
                32*16, 30*16, new rectangle( 0, 32*16-1, 0, 30*16-1 ),
                mcr2_gfxdecodeinfo,
                1+8*16, 8*16,
                mcr2_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
                null,
                generic_vh_start,
                generic_vh_stop,
                mcr2_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                mcr_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );

        public static MachineDriver  wacko_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                2500000,	/* 2.5 Mhz */
                                0,
                                mcr2_readmem,mcr2_writemem,wacko_readport,writeport,
                                mcr_interrupt,32
                        ),
                        new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2000000,	/* 2 Mhz */
                                2,	/* memory region #2 */
                                sound_readmem,sound_writemem,null,null,
                                mcr_sh_interrupt,26
                        )
                },
                30,
                mcr_init_machine,

                /* video hardware */
                32*16, 30*16, new rectangle( 0, 32*16-1, 0, 30*16-1 ),
                mcr2_gfxdecodeinfo,
                1+8*16, 8*16,
                wacko_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
                null,
                generic_vh_start,
                generic_vh_stop,
                mcr2_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                mcr_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );

        public static MachineDriver  kroozr_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                2500000,	/* 2.5 Mhz */
                                0,
                                mcr2_readmem,mcr2_writemem,kroozr_readport,writeport,
                                mcr_interrupt,32
                        ),
                        new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2000000,	/* 2 Mhz */
                                2,	/* memory region #2 */
                                sound_readmem,sound_writemem,null,null,
                                mcr_sh_interrupt,26
                        )
                },
                30,
                mcr_init_machine,

                /* video hardware */
                32*16, 30*16, new rectangle( 0, 32*16-1, 0, 30*16-1 ),
                mcr2_gfxdecodeinfo,
                1+8*16, 8*16,
                mcr2_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
                null,
                generic_vh_start,
                generic_vh_stop,
                mcr2_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                mcr_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );

        public static MachineDriver  journey_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                7500000,	/* Looks like it runs at 7.5 Mhz rather than 5 or 2.5 */
                                0,
                                journey_readmem,journey_writemem,readport,writeport,
                                mcr_interrupt,32
                        ),
                        new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2000000,	/* 2 Mhz */
                                2,	/* memory region #2 */
                                sound_readmem,sound_writemem,null,null,
                                mcr_sh_interrupt,26
                        )
                },
                30,
                mcr_init_machine,

                /* video hardware */
                32*16, 30*16, new rectangle( 0, 32*16-1, 0, 30*16-1 ),
                journey_gfxdecodeinfo,
                1+8*16, 8*16,
                journey_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
                null,
                generic_vh_start,
                generic_vh_stop,
                journey_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                mcr_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );


        /***************************************************************************

          High score save/load

        ***************************************************************************/

        static int mcr2_hiload (String name, int addr, int len)
        {
           char RAM[]=Machine.memory_region[0];

           /* see if it's okay to load */
           if (mcr_loadnvram!=0)
           {
              FILE f;

                        f = fopen (name, "rb");
              if (f!=null)
              {
                                fread (RAM,addr, 1, len, f);
                                fclose (f);
              }
              return 1;
           }
           else return 0;	/* we can't load the hi scores yet */
        }

        static void mcr2_hisave (String name, int addr, int len)
        {
           char RAM[]=Machine.memory_region[0];
           FILE f;

                f = fopen (name, "wb");
           if (f!=null)
           {
              fwrite (RAM,addr, 1, len, f);
              fclose (f);
           }
        }
        static HiscoreLoadPtr domino_hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
             return mcr2_hiload (name, 0xc000, 0x92); 
        }};
        static HiscoreSavePtr domino_hisave = new HiscoreSavePtr() { public void handler(String name)
	{
               mcr2_hisave (name, 0xc000, 0x92); 
        }};
        static HiscoreLoadPtr journey_hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
            return mcr2_hiload (name, 0xc000, 0x9e);
        }};
        static HiscoreSavePtr journey_hisave = new HiscoreSavePtr() { public void handler(String name)
	{
            mcr2_hisave (name, 0xc000, 0x9e); 
        }};
        static HiscoreLoadPtr tron_hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
            return mcr2_hiload (name, 0xc4f0, 0x97); 
        }};
        static HiscoreSavePtr tron_hisave = new HiscoreSavePtr() { public void handler(String name)
	{
             mcr2_hisave (name, 0xc4f0, 0x97);
        }};
        static HiscoreLoadPtr kroozr_hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
            return mcr2_hiload (name, 0xc466, 0x95); 
        }};
        static HiscoreSavePtr kroozr_hisave = new HiscoreSavePtr() { public void handler(String name)
	{
                 mcr2_hisave (name, 0xc466, 0x95); 
        }};
        static HiscoreLoadPtr shollow_hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
            return mcr2_hiload (name, 0xc600, 0x8a); 
        }};
        static HiscoreSavePtr shollow_hisave = new HiscoreSavePtr() { public void handler(String name)
	{
               mcr2_hisave (name, 0xc600, 0x8a); 
        }};
        static HiscoreLoadPtr twotiger_hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
                return mcr2_hiload (name, 0xc000, 0xa0); 
        }};
        static HiscoreSavePtr twotiger_hisave = new HiscoreSavePtr() { public void handler(String name)
	{
           mcr2_hisave (name, 0xc000, 0xa0); 
        }};   
        static HiscoreLoadPtr wacko_hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
            return mcr2_hiload (name, 0xc000, 0x91); 
        }};
        static HiscoreSavePtr wacko_hisave = new HiscoreSavePtr() { public void handler(String name)
	{
               mcr2_hisave (name, 0xc000, 0x91); 
        }};


        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr tron_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "SCPU_PGA.BIN", 0x0000, 0x2000, 0x495012e0 );
                ROM_LOAD( "SCPU_PGB.BIN", 0x2000, 0x2000, 0xc8ef81e1 );
                ROM_LOAD( "SCPU_PGC.BIN", 0x4000, 0x2000, 0x9879b4a1 );
                ROM_LOAD( "SCPU_PGD.BIN", 0x6000, 0x2000, 0x3b1704c1 );
                ROM_LOAD( "SCPU_PGE.BIN", 0x8000, 0x2000, 0x0dffee6f );
                ROM_LOAD( "SCPU_PGF.BIN", 0xA000, 0x2000, 0xd16843d6 );

                ROM_REGION(0x0c000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "SCPU_BGG.BIN", 0x00000, 0x2000, 0x944e9e08 );
                ROM_LOAD( "SCPU_BGH.BIN", 0x02000, 0x2000, 0x223a0a26 );
                ROM_LOAD( "VG_0.BIN", 0x04000, 0x2000, 0x63f820fc );
                ROM_LOAD( "VG_1.BIN", 0x06000, 0x2000, 0xa9a4acba );
                ROM_LOAD( "VG_2.BIN", 0x08000, 0x2000, 0xcd13759f );
                ROM_LOAD( "VG_3.BIN", 0x0a000, 0x2000, 0x7d9cdf2c );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "SSI_0A.BIN", 0x0000, 0x1000, 0xadad608b );
                ROM_LOAD( "SSI_0B.BIN", 0x1000, 0x1000, 0xef829006 );
                ROM_LOAD( "SSI_0C.BIN", 0x2000, 0x1000, 0x5a16d2ca );
                ROM_END();
        }};

        public static GameDriver tron_driver = new GameDriver
	(
                "Tron",
                "tron",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA\nBRAD OLIVER",
                tron_machine_driver,

                tron_rom,
                null, null,
                null,

                null/*TBR*/,tron_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                null, null,null,
                ORIENTATION_ROTATE_90,

                tron_hiload,tron_hisave
        );
        static RomLoadPtr twotiger_rom= new RomLoadPtr(){ public void handler()  
        {

                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "2tgrpg0.BIN", 0x0000, 0x2000, 0xa39d47cf );
                ROM_LOAD( "2tgrpg1.BIN", 0x2000, 0x2000, 0x2a9eb640 );
                ROM_LOAD( "2tgrpg2.BIN", 0x4000, 0x2000, 0x7af2eae6 );
                ROM_LOAD( "2tgrpg3.BIN", 0x6000, 0x2000, 0xec723040 );

                ROM_REGION(0x0c000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "2tgrbg0.bin", 0x00000, 0x2000, 0x0eb09cc8 );
                ROM_LOAD( "2tgrbg1.bin", 0x02000, 0x2000, 0x3cc85334 );
                ROM_LOAD( "2tgrfg3.bin", 0x04000, 0x2000, 0xcd170edb );
                ROM_LOAD( "2tgrfg2.bin", 0x06000, 0x2000, 0x9c072ca7 );
                ROM_LOAD( "2tgrfg1.bin", 0x08000, 0x2000, 0x3415a753 );
                ROM_LOAD( "2tgrfg0.bin", 0x0a000, 0x2000, 0x3d25f267 );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "2tgra7.bin", 0x0000, 0x1000, 0x5918facc );
                ROM_LOAD( "2tgra8.bin", 0x1000, 0x1000, 0xa85a90a6 );
                ROM_LOAD( "2tgra9.bin", 0x2000, 0x1000, 0x1be1374b );
                ROM_END();
        }};

        public static GameDriver twotiger_driver = new GameDriver
	(
                "Two Tigers",
                "twotiger",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA\nBRAD OLIVER",
                tron_machine_driver,

                twotiger_rom,
                null, null,
                null,

                null/*TBR*/,twotiger_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                null, null,null,
                ORIENTATION_DEFAULT,

                twotiger_hiload,twotiger_hisave
        );
        static RomLoadPtr domino_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "dmanpg0.BIN", 0x0000, 0x2000, 0x88d22534 );
                ROM_LOAD( "dmanpg1.BIN", 0x2000, 0x2000, 0x92428d46 );
                ROM_LOAD( "dmanpg2.BIN", 0x4000, 0x2000, 0x98d70acf );
                ROM_LOAD( "dmanpg3.BIN", 0x6000, 0x2000, 0xac447472 );

                ROM_REGION(0x0c000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "dmanbg0.bin", 0x00000, 0x2000, 0x9d160492 );
                ROM_LOAD( "dmanbg1.bin", 0x02000, 0x2000, 0x4e768e40 );
                ROM_LOAD( "dmanfg3.bin", 0x04000, 0x2000, 0x6dd92095 );
                ROM_LOAD( "dmanfg2.bin", 0x06000, 0x2000, 0x05a42c70 );
                ROM_LOAD( "dmanfg1.bin", 0x08000, 0x2000, 0x198791a1 );
                ROM_LOAD( "dmanfg0.bin", 0x0a000, 0x2000, 0x5e270dc5 );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "dm-a7.snd", 0x0000, 0x1000, 0x6045bddf );
                ROM_LOAD( "dm-a8.snd", 0x1000, 0x1000, 0x858d9a77 );
                ROM_LOAD( "dm-a9.snd", 0x2000, 0x1000, 0x4270a548 );
                ROM_LOAD( "dm-a10.snd", 0x3000, 0x1000, 0x4259a5b5 );
                ROM_END();
        }};

        public static GameDriver domino_driver = new GameDriver
	(
                "Domino Man",
                "domino",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA\nBRAD OLIVER",
                domino_machine_driver,

                domino_rom,
                null, null,
                null,

                null/*TBR*/,domino_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                null, null,null,
                ORIENTATION_DEFAULT,

                domino_hiload,domino_hisave
        );
        static RomLoadPtr shollow_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "sh-pro.00", 0x0000, 0x2000, 0xadb10b65 );
                ROM_LOAD( "sh-pro.01", 0x2000, 0x2000, 0x6e5b6ce5 );
                ROM_LOAD( "sh-pro.02", 0x4000, 0x2000, 0x4033cf35 );
                ROM_LOAD( "sh-pro.03", 0x6000, 0x2000, 0xe9f23dfe );
                ROM_LOAD( "sh-pro.04", 0x8000, 0x2000, 0xf7a1db59 );
                ROM_LOAD( "sh-pro.05", 0xa000, 0x2000, 0xd3222934 );

                ROM_REGION(0x0c000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "sh-bg.00", 0x00000, 0x2000, 0x922498d4 );
                ROM_LOAD( "sh-bg.01", 0x02000, 0x2000, 0x281c1676 );
                ROM_LOAD( "sh-fg.03", 0x04000, 0x2000, 0xda515dab );
                ROM_LOAD( "sh-fg.02", 0x06000, 0x2000, 0x3300aba6 );
                ROM_LOAD( "sh-fg.01", 0x08000, 0x2000, 0x8b7a26b2 );
                ROM_LOAD( "sh-fg.00", 0x0a000, 0x2000, 0xc62c3d2e );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "sh-snd.01", 0x0000, 0x1000, 0x130dcc4b );
                ROM_LOAD( "sh-snd.02", 0x1000, 0x1000, 0xc1a01dda );
                ROM_LOAD( "sh-snd.03", 0x2000, 0x1000, 0xb6a0455e );
                ROM_END();
        }};

        public static GameDriver shollow_driver = new GameDriver
	(
                "Satan's Hollow",
                "shollow",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA\nBRAD OLIVER",
                domino_machine_driver,

                shollow_rom,
                null, null,
                null,

                null/*TBR*/,shollow_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                null, null,null,
                ORIENTATION_ROTATE_90,

                shollow_hiload,shollow_hisave
        );
        static RomLoadPtr wacko_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "WACKOCPU.2D", 0x0000, 0x2000, 0xb3b6ac28 );
                ROM_LOAD( "WACKOCPU.3D", 0x2000, 0x2000, 0xc0c3cc3d );
                ROM_LOAD( "WACKOCPU.4D", 0x4000, 0x2000, 0x3fa1f397 );
                ROM_LOAD( "WACKOCPU.5D", 0x6000, 0x2000, 0x60d7328d );

                ROM_REGION(0x0c000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "WACKOCPU.3G", 0x00000, 0x2000, 0x5fd6f5e2 );
                ROM_LOAD( "WACKOCPU.4G", 0x02000, 0x2000, 0x5571ad01 );
                ROM_LOAD( "WACKOVID.1A", 0x04000, 0x2000, 0x141a3ae2 );
                ROM_LOAD( "WACKOVID.1B", 0x06000, 0x2000, 0xb4f25874 );
                ROM_LOAD( "WACKOVID.1D", 0x08000, 0x2000, 0xf3e5fb89 );
                ROM_LOAD( "WACKOVID.1E", 0x0a000, 0x2000, 0xe427b1ed );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "WACKOSND.7A", 0x0000, 0x1000, 0x5a5f1e6d );
                ROM_LOAD( "WACKOSND.8A", 0x1000, 0x1000, 0x746fe5a1 );
                ROM_LOAD( "WACKOSND.9A", 0x2000, 0x1000, 0xd0467392 );
                ROM_END();
        }};

        public static GameDriver wacko_driver = new GameDriver
	(
                "Wacko",
                "wacko",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA\nBRAD OLIVER\nJOHN BUTLER",
                wacko_machine_driver,

                wacko_rom,
                null, null,
                null,

                null/*TBR*/,wacko_input_ports, wacko_trak_ports,null/*TBR*/,null/*TBR*/,

                null,null,null,
                ORIENTATION_DEFAULT,

                wacko_hiload,wacko_hisave
        );
        static RomLoadPtr kroozr_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "KOZMKCPU.2D", 0x0000, 0x2000, 0x8318ed7e );
                ROM_LOAD( "KOZMKCPU.3D", 0x2000, 0x2000, 0x60ce4368 );
                ROM_LOAD( "KOZMKCPU.4D", 0x4000, 0x2000, 0x1f73ee75 );
                ROM_LOAD( "KOZMKCPU.5D", 0x6000, 0x2000, 0x76a12467 );
                ROM_LOAD( "KOZMKCPU.6D", 0x8000, 0x2000, 0x6ca9c89b );

                ROM_REGION(0x0c000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "KOZMKCPU.3G", 0x00000, 0x2000, 0x166aecc0 );
                ROM_LOAD( "KOZMKCPU.4G", 0x02000, 0x2000, 0x7df926cb );
                ROM_LOAD( "KOZMKVID.1A", 0x04000, 0x2000, 0x46e0fe3e );
                ROM_LOAD( "KOZMKVID.1B", 0x06000, 0x2000, 0xc5692813 );
                ROM_LOAD( "KOZMKVID.1D", 0x08000, 0x2000, 0xa1ed0189 );
                ROM_LOAD( "KOZMKVID.1E", 0x0a000, 0x2000, 0x4fb1786f );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "KOZMKSND.7A", 0x0000, 0x1000, 0x6dd2a82e );
                ROM_LOAD( "KOZMKSND.8A", 0x1000, 0x1000, 0xe3c2f448 );
                ROM_LOAD( "KOZMKSND.9A", 0x2000, 0x1000, 0x42598ef5 );
                ROM_END();
        }};

        public static GameDriver kroozr_driver = new GameDriver
	(
                "Kozmik Kroozr",
                "kroozr",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA\nBRAD OLIVER",
                kroozr_machine_driver,

                kroozr_rom,
                null, null,
                null,

                null/*TBR*/,kroozr_input_ports, null/*TBR*/,null/*TBR*/,null/*TBR*/,

                null,null,null,
                ORIENTATION_DEFAULT,

                kroozr_hiload,kroozr_hisave
        );
        static RomLoadPtr journey_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "D2", 0x0000, 0x2000, 0x76406a54 );
                ROM_LOAD( "D3", 0x2000, 0x2000, 0xfa24bbf6 );
                ROM_LOAD( "D4", 0x4000, 0x2000, 0x6dd86bae );
                ROM_LOAD( "D5", 0x6000, 0x2000, 0xa62e7f50 );
                ROM_LOAD( "D6", 0x8000, 0x2000, 0xdb5958a9 );

                ROM_REGION(0x14000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "G3", 0x00000, 0x2000, 0x335de8f1 );
                ROM_LOAD( "G4", 0x02000, 0x2000, 0x5c526be4 );
                ROM_LOAD( "A1", 0x04000, 0x2000, 0x49f93f0f );
                ROM_LOAD( "A2", 0x06000, 0x2000, 0x85a6f51e );
                ROM_LOAD( "A3", 0x08000, 0x2000, 0xfc8edbae );
                ROM_LOAD( "A4", 0x0a000, 0x2000, 0xf54c7628 );
                ROM_LOAD( "A5", 0x0c000, 0x2000, 0x0427d9b3 );
                ROM_LOAD( "A6", 0x0e000, 0x2000, 0xe00c515e );
                ROM_LOAD( "A7", 0x10000, 0x2000, 0xe2535a87 );
                ROM_LOAD( "A8", 0x12000, 0x2000, 0x816df0db );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "A", 0x0000, 0x1000, 0xa600a476 );
                ROM_LOAD( "B", 0x1000, 0x1000, 0x300df06f );
                ROM_LOAD( "C", 0x2000, 0x1000, 0xd665caf5 );
                ROM_LOAD( "D", 0x3000, 0x1000, 0x43a19b55 );
                ROM_END();
        }};

        public static GameDriver journey_driver = new GameDriver
	(
                "Journey",
                "journey",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA\nBRAD OLIVER",
                journey_machine_driver,

                journey_rom,
                null, null,
                null,

                null/*TBR*/,domino_input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                null,null,null,
                ORIENTATION_ROTATE_90,

                journey_hiload,journey_hisave
        );    
}
