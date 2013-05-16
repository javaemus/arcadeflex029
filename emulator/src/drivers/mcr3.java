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
import static vidhrdw.mcr3.*;
import static sndhrdw.mcr.*;
import static mame.inptport.*;
import static sndhrdw._8910intf.*;
import static mame.memoryH.*;
public class mcr3 {
    
         static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0xdfff, MRA_ROM ),
                new MemoryReadAddress( 0xe000, 0xe9ff, MRA_RAM ),
                new MemoryReadAddress( 0xf000, 0xf7ff, MRA_RAM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0xe000, 0xe7ff, MWA_RAM ),
                new MemoryWriteAddress( 0x0000, 0xdfff, MWA_ROM ),
                new MemoryWriteAddress( 0xe800, 0xe9ff, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0xf000, 0xf7ff, mcr3_videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0xf800, 0xf8ff, mcr3_palette_w, mcr3_paletteram ),
                new MemoryWriteAddress( -1 ) /* end of table */
        };

        static MemoryReadAddress rampage_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0xdfff, MRA_ROM ),
                new MemoryReadAddress( 0xe000, 0xebff, MRA_RAM ),
                new MemoryReadAddress( 0xf000, 0xf7ff, MRA_RAM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };

        static  MemoryWriteAddress rampage_writemem[] =
        {
                new MemoryWriteAddress( 0xe000, 0xe7ff, MWA_RAM ),
                new MemoryWriteAddress( 0x0000, 0xdfff, MWA_ROM ),
                new MemoryWriteAddress( 0xe800, 0xebff, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0xf000, 0xf7ff, mcr3_videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0xec00, 0xecff, mcr3_palette_w, mcr3_paletteram ),
                new MemoryWriteAddress( -1 )  /* end of table */
        };

        static MemoryReadAddress spyhunt_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0xdfff, MRA_ROM ),
                new MemoryReadAddress( 0xe000, 0xebff, MRA_RAM ),
                new MemoryReadAddress( 0xf000, 0xffff, MRA_RAM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };

        static MemoryWriteAddress spyhunt_writemem[] =
        {
                new MemoryWriteAddress( 0xf000, 0xf7ff, MWA_RAM ),
                new MemoryWriteAddress( 0xe800, 0xebff, MWA_RAM, spyhunt_alpharam, spyhunt_alpharam_size),
                new MemoryWriteAddress( 0xe000, 0xe7ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0xf800, 0xf9ff, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0xfa00, 0xfaff, mcr3_palette_w, mcr3_paletteram ),
                new MemoryWriteAddress( 0x0000, 0xdfff, MWA_ROM ),
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

        static MemoryReadAddress timber_sound_readmem[] =
        {
                new MemoryReadAddress( 0x3000, 0x3fff, MRA_RAM ),
                new MemoryReadAddress( 0x8000, 0x83ff, MRA_RAM ),
                new MemoryReadAddress( 0x9000, 0x9003, mcr_soundlatch_r ),
                new MemoryReadAddress( 0xa001, 0xa001, AY8910_read_port_0_r ),
                new MemoryReadAddress( 0xb001, 0xb001, AY8910_read_port_1_r ),
                new MemoryReadAddress( 0xf000, 0xf000, input_port_5_r ),
                new MemoryReadAddress( 0xe000, 0xe000, MRA_NOP ),
                new MemoryReadAddress( 0x0000, 0x2fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress timber_sound_writemem[] =
        {
                new MemoryWriteAddress( 0x3000, 0x3fff, MWA_RAM ),
                new MemoryWriteAddress( 0x8000, 0x83ff, MWA_RAM ),
                new MemoryWriteAddress( 0xa000, 0xa000, AY8910_control_port_0_w ),
                new MemoryWriteAddress( 0xa002, 0xa002, AY8910_write_port_0_w ),
                new MemoryWriteAddress( 0xb000, 0xb000, AY8910_control_port_1_w ),
                new MemoryWriteAddress( 0xb002, 0xb002, AY8910_write_port_1_w ),
                new MemoryWriteAddress( 0xc000, 0xc000, mcr_soundstatus_w ),
                new MemoryWriteAddress( 0xe000, 0xe000, MWA_NOP ),
                new MemoryWriteAddress( 0x0000, 0x2fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };


        /***************************************************************************

          Input port definitions

        ***************************************************************************/
        static InputPortPtr tapper_input_ports= new InputPortPtr(){ public void handler()  
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
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
                PORT_BIT( 0xe0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN2 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN3 -- dipswitches */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );

                PORT_START();	/* IN4 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* AIN0 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );
                INPUT_PORTS_END();
        }};

        static InputPortPtr dotron_input_ports= new InputPortPtr(){ public void handler()  
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

                PORT_START();	/* IN1 -- actually not used at all, but read as a trakport */
                PORT_BIT( 0xff, IP_ACTIVE_HIGH, IPT_UNUSED );

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
                PORT_BITX(0x10, IP_ACTIVE_LOW, 0, "Aim Down", OSD_KEY_Z, IP_KEY_NONE, 0 );
                PORT_BITX(0x20, IP_ACTIVE_LOW, 0, "Aim Up", OSD_KEY_A, IP_KEY_NONE, 0 );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 );
                PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* this needs to be low to allow 1 player games? */

                PORT_START();	/* IN3 -- dipswitches */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );

                PORT_START();	/* IN4 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* AIN0 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );
                INPUT_PORTS_END();
        }};

        static InputPortPtr destderb_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BITX(    0x20, 0x20, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x20, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_TILT );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN1 -- the high 6 bits contain the sterring wheel value */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
                PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNUSED );

                PORT_START();	/* IN2 -- the high 6 bits contain the sterring wheel value */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
                PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNUSED );

                PORT_START();	/* IN3 -- dipswitches */
                PORT_DIPNAME( 0x01, 0x01, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x01, "2P Upright" );
                PORT_DIPSETTING(    0x00, "4P Upright" );
                PORT_DIPNAME( 0x02, 0x02, "Difficulty", IP_KEY_NONE );
                PORT_DIPSETTING(    0x02, "Normal" );
                PORT_DIPSETTING(    0x00, "Harder" );
                PORT_DIPNAME( 0x04, 0x04, "Free Play", IP_KEY_NONE );
                PORT_DIPSETTING(    0x04, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x08, 0x08, "Reward Screen", IP_KEY_NONE );
                PORT_DIPSETTING(    0x08, "Expanded" );
                PORT_DIPSETTING(    0x00, "Limited" );
                PORT_DIPNAME( 0x30, 0x30, "Coinage", IP_KEY_NONE );
                PORT_DIPSETTING(    0x20, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x00, "2 Coins/2 Credits" );
                PORT_DIPSETTING(    0x30, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x10, "1 Coin/2 Credits" );
                PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNKNOWN );

                PORT_START();	/* IN4 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN3 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN4 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START3 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START4 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER3 );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER4 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER4 );

                PORT_START();	/* AIN0 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );
                INPUT_PORTS_END();
        }};

        static InputPortPtr timber_input_ports= new InputPortPtr(){ public void handler()  
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
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_PLAYER1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_PLAYER1 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_PLAYER1 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_PLAYER1 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
                PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_PLAYER2 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_PLAYER2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_PLAYER2 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_PLAYER2 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
                PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN3 -- dipswitches */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );

                PORT_START();	/* IN4 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* AIN0 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );
                INPUT_PORTS_END();
        }};

        static InputPortPtr rampage_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BITX(    0x20, 0x20, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x20, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_SERVICE );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN1 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_PLAYER1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_PLAYER1 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_PLAYER1 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_PLAYER1 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
                PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_PLAYER2 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_PLAYER2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_PLAYER2 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_PLAYER2 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
                PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN3 -- dipswitches */
                PORT_DIPNAME( 0x03, 0x03, "Difficulty", IP_KEY_NONE );
                PORT_DIPSETTING(    0x03, "1 Normal" );
                PORT_DIPSETTING(    0x02, "0 Easy" );
                PORT_DIPSETTING(    0x01, "2 Hard" );
                PORT_DIPSETTING(    0x00, "1 Free" );
                PORT_DIPNAME( 0x04, 0x04, "Score Option", IP_KEY_NONE );
                PORT_DIPSETTING(    0x04, "On" );
                PORT_DIPSETTING(    0x00, "Off" );
                PORT_BIT( 0x78, IP_ACTIVE_LOW, IPT_UNKNOWN );
                PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Rack Advance", OSD_KEY_F1, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );

                PORT_START();	/* IN4 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_PLAYER3 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_PLAYER3 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_PLAYER3 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_PLAYER3 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER3 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER3 );
                PORT_BIT( 0xc0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* AIN0 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );
                INPUT_PORTS_END();
        }};

        static InputPortPtr spyhunt_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BITX( 0x10, IP_ACTIVE_LOW, 0 | IPF_TOGGLE, "Gear Shift", OSD_KEY_ENTER, IP_JOY_DEFAULT, 0 );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_TILT );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_SERVICE );
                PORT_BITX(    0x80, 0x80, IPT_DIPSWITCH_NAME | IPF_TOGGLE, "Service Mode", OSD_KEY_F2, IP_KEY_NONE, 0 );
                PORT_DIPSETTING(    0x80, "Off" );
                PORT_DIPSETTING(    0x00, "On" );

                PORT_START();	/* IN1 -- various buttons, low 5 bits */
                PORT_BITX( 0x01, IP_ACTIVE_LOW, IPT_BUTTON4, "Oil Slick", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 0 );
                PORT_BITX( 0x02, IP_ACTIVE_LOW, IPT_BUTTON5, "Missiles", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 0 );
                PORT_BITX( 0x04, IP_ACTIVE_LOW, IPT_BUTTON3, "Weapon Truck", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 0 );
                PORT_BITX( 0x08, IP_ACTIVE_LOW, IPT_BUTTON2, "Smoke Screen", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 0 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 ); /* machine guns */
                PORT_BIT( 0xe0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN2 -- actually not used at all, but read as a trakport */
                PORT_BIT( 0xff, IP_ACTIVE_HIGH, IPT_UNUSED );

                PORT_START();	/* IN3 -- dipswitches -- low 4 bits only */
                PORT_DIPNAME( 0x01, 0x01, "Game Timer", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "1:00" );
                PORT_DIPSETTING(    0x01, "1:30" );
                PORT_DIPNAME( 0x02, 0x02, "Attract Mode Sound", IP_KEY_NONE );
                PORT_DIPSETTING(    0x02, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN );
                PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN4 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* AIN0 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );

                PORT_START();	/* fake port for additional controls */
                PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP );	/* accelerator */
                PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN );	/* accelerator */
                PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT );	/* steering */
                PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT );	/* steering */
                PORT_BIT( 0xf0, IP_ACTIVE_HIGH, IPT_UNUSED );
                INPUT_PORTS_END();
        }};



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

        static IOReadPort dotron_readport[] =
        {
           new IOReadPort( 0x00, 0x00, input_port_0_r ),
           new IOReadPort( 0x01, 0x01, input_trak_0_r ),
           new IOReadPort( 0x02, 0x02, input_port_2_r ),
           new IOReadPort( 0x03, 0x03, input_port_3_r ),
           new IOReadPort( 0x04, 0x04, input_port_4_r ),
           new IOReadPort( 0x05, 0xff, mcr_readport ),
           new IOReadPort( -1 )
        };

        static IOReadPort destderb_readport[] =
        {
           new IOReadPort( 0x00, 0x00, input_port_0_r ),
           new IOReadPort( 0x01, 0x02, destderb_port_r ),
           new IOReadPort( 0x03, 0x03, input_port_3_r ),
           new IOReadPort( 0x04, 0x04, input_port_4_r ),
           new IOReadPort( 0x05, 0xff, mcr_readport ),
           new IOReadPort( -1 )
        };

        static IOReadPort spyhunt_readport[] =
        {
           new IOReadPort( 0x00, 0x00, input_port_0_r ),
           new IOReadPort( 0x01, 0x01, input_port_1_r ),
           new IOReadPort( 0x02, 0x02, spyhunt_port_r ),
           new IOReadPort( 0x03, 0x03, input_port_3_r ),
           new IOReadPort( 0x04, 0x04, input_port_4_r ),
           new IOReadPort( 0x05, 0xff, mcr_readport ),
           new IOReadPort( -1 )
        };

        static IOWritePort writeport[] =
        {
           new IOWritePort( 0, 0xFF, mcr_writeport ),
           new IOWritePort( -1 )	/* end of table */
        };

        static IOWritePort sh_writeport[] =
        {
           new IOWritePort( 0, 0xFF, spyhunt_writeport ),
           new IOWritePort( -1 )	/* end of table */
        };

        static TrakPort mcr3_trak_ports[] =
        {
                new TrakPort(
                        X_AXIS,
                        0,
                        -0.5,
                        null
                ),
           new TrakPort( -1 )
        };


        /***************************************************************************

          Graphics layouts

        ***************************************************************************/

        /* generic character layouts */

        /* note that characters are half the resolution of sprites in each direction, so we generate
           them at double size */

        /* 1024 characters; used by tapper, timber, rampage */
        static GfxLayout mcr3_charlayout_1024 = new GfxLayout
	(
                16, 16,
                1024,
                4,
                new int[]{ 1024*16*8, 1024*16*8+1, 0, 1 },
                new int[]{ 0, 0, 2, 2, 4, 4, 6, 6, 8, 8, 10, 10, 12, 12, 14, 14 },
                new int[]{ 0, 0, 2*8, 2*8, 4*8, 4*8, 6*8, 6*8, 8*8, 8*8, 10*8, 10*8, 12*8, 12*8, 14*8, 14*8 },
                16*8
        );

        /* 512 characters; used by dotron, destderb */
        static GfxLayout mcr3_charlayout_512 = new GfxLayout
	(
                16, 16,
                512,
                4,
                new int[]{ 512*16*8, 512*16*8+1, 0, 1 },
                new int[]{ 0, 0, 2, 2, 4, 4, 6, 6, 8, 8, 10, 10, 12, 12, 14, 14 },
                new int[]{ 0, 0, 2*8, 2*8, 4*8, 4*8, 6*8, 6*8, 8*8, 8*8, 10*8, 10*8, 12*8, 12*8, 14*8, 14*8 },
                16*8
        );

        /* generic sprite layouts */

        /* 512 sprites; used by rampage */

        static GfxLayout mcr3_spritelayout_512 = new GfxLayout
	(
           32,32,
           512,
           4,
           new int[]{ 0, 1, 2, 3 },
           new int[]{  512*128*8*3+0, 512*128*8*3+4, 512*128*8*2+0, 512*128*8*2+4, 512*128*8+0, 512*128*8+4, 0, 4, 512*128*8*3+8, 512*128*8*3+12, 512*128*8*2+8, 512*128*8*2+12, 512*128*8+8, 512*128*8+12, 8, 12,
              512*128*8*3+16, 512*128*8*3+20, 512*128*8*2+16, 512*128*8*2+20, 512*128*8+16, 512*128*8+20, 16, 20, 512*128*8*3+24, 512*128*8*3+28, 512*128*8*2+24, 512*128*8*2+28,
              512*128*8+24, 512*128*8+28, 24, 28 },
          new int[] {  0, 32, 32*2, 32*3, 32*4, 32*5, 32*6, 32*7, 32*8, 32*9, 32*10, 32*11,
              32*12, 32*13, 32*14, 32*15, 32*16, 32*17, 32*18, 32*19, 32*20, 32*21,
              32*22, 32*23, 32*24, 32*25, 32*26, 32*27, 32*28, 32*29, 32*30, 32*31 },
           128*8
        );
        
        /* 256 sprites; used by tapper, timber, destderb, spyhunt */
 
        static GfxLayout mcr3_spritelayout_256 = new GfxLayout
	(
           32,32,
           256,
           4,
           new int[]{ 0, 1, 2, 3 },
           new int[]{  256*128*8*3+0, 256*128*8*3+4, 256*128*8*2+0, 256*128*8*2+4, 256*128*8+0, 256*128*8+4, 0, 4, 256*128*8*3+8, 256*128*8*3+12, 256*128*8*2+8, 256*128*8*2+12, 256*128*8+8, 256*128*8+12, 8, 12,
              256*128*8*3+16, 256*128*8*3+20, 256*128*8*2+16, 256*128*8*2+20, 256*128*8+16, 256*128*8+20, 16, 20, 256*128*8*3+24, 256*128*8*3+28, 256*128*8*2+24, 256*128*8*2+28,
              256*128*8+24, 256*128*8+28, 24, 28 },
           new int[]{  0, 32, 32*2, 32*3, 32*4, 32*5, 32*6, 32*7, 32*8, 32*9, 32*10, 32*11,
              32*12, 32*13, 32*14, 32*15, 32*16, 32*17, 32*18, 32*19, 32*20, 32*21,
              32*22, 32*23, 32*24, 32*25, 32*26, 32*27, 32*28, 32*29, 32*30, 32*31 },
           128*8
        );
 
        /* 128 sprites; used by dotron */
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
   
        /***************************** spyhunt layouts **********************************/

        /* 128 32x16 characters; used by spyhunt */
        static GfxLayout spyhunt_charlayout_128 = new GfxLayout
	(
                64, 32,
                128,
                4,
                new int[]{ 0, 1, 128*128*8, 128*128*8+1  },
                new int[]{ 0, 0, 2, 2, 4, 4, 6, 6, 8, 8, 10, 10, 12, 12, 14, 14, 16, 16, 18, 18, 20, 20, 22, 22, 24, 24, 26, 26, 28, 28, 30, 30,
                  32, 32, 34, 34, 36, 36, 38, 38, 40, 40, 42, 42, 44, 44, 46, 46, 48, 48, 50, 50, 52, 52, 54, 54, 56, 56, 58, 58, 60, 60, 62, 62 },
                new int[]{ 0, 0, 8*8, 8*8, 16*8, 16*8, 24*8, 24*8, 32*8, 32*8, 40*8, 40*8, 48*8, 48*8, 56*8, 56*8,
                  64*8, 64*8, 72*8, 72*8, 80*8, 80*8, 88*8, 88*8, 96*8, 96*8, 104*8, 104*8, 112*8, 112*8, 120*8, 120*8 },
                128*8
        );

        /* of course, Spy Hunter just *had* to be different than everyone else... */
        static GfxLayout spyhunt_alphalayout = new GfxLayout
	(
                16, 16,
                256,
                2,
                new int[]{ 0, 1 },
                new int[]{ 0, 0, 2, 2, 4, 4, 6, 6, 8, 8, 10, 10, 12, 12, 14, 14 },
                new int[]{ 0, 0, 2*8, 2*8, 4*8, 4*8, 6*8, 6*8, 8*8, 8*8, 10*8, 10*8, 12*8, 12*8, 14*8, 14*8 },
                16*8
        );



        static GfxDecodeInfo tapper_gfxdecodeinfo[] =
        {
                 new GfxDecodeInfo( 1, 0x0000, mcr3_charlayout_1024,   0, 4 ),
                 new GfxDecodeInfo( 1, 0x8000, mcr3_spritelayout_256,  0, 4 ),
                 new GfxDecodeInfo( -1 ) /* end of array */
        };

        static GfxDecodeInfo dotron_gfxdecodeinfo[] =
        {
                 new GfxDecodeInfo( 1, 0x0000, mcr3_charlayout_512,   0, 4 ),
                 new GfxDecodeInfo( 1, 0x4000, mcr3_spritelayout_128, 0, 4 ),
                 new GfxDecodeInfo( -1 ) /* end of array */
        };

        static GfxDecodeInfo destderb_gfxdecodeinfo[] =
        {
                 new GfxDecodeInfo( 1, 0x0000, mcr3_charlayout_512,   0, 4 ),
                 new GfxDecodeInfo( 1, 0x4000, mcr3_spritelayout_256, 0, 4 ),
                 new GfxDecodeInfo( -1 ) /* end of array */
        };

        static GfxDecodeInfo timber_gfxdecodeinfo[] =
        {
                 new GfxDecodeInfo( 1, 0x0000, mcr3_charlayout_1024,   0, 4 ),
                 new GfxDecodeInfo( 1, 0x8000, mcr3_spritelayout_256,  0, 4 ),
                 new GfxDecodeInfo( -1 ) /* end of array */
        };

        static GfxDecodeInfo rampage_gfxdecodeinfo[] =
        {
                 new GfxDecodeInfo( 1, 0x0000, mcr3_charlayout_1024,  0, 4 ),
                 new GfxDecodeInfo( 1, 0x8000, mcr3_spritelayout_512, 0, 4 ),
                 new GfxDecodeInfo( -1 ) /* end of array */
        };

        static GfxDecodeInfo spyhunt_gfxdecodeinfo[] =
        {
                 new GfxDecodeInfo( 1, 0x0000, spyhunt_charlayout_128,    0, 4 ),
                 new GfxDecodeInfo( 1, 0x8000, mcr3_spritelayout_256,     0, 4 ),
                 new GfxDecodeInfo( 1, 0x28000, spyhunt_alphalayout,   8*16, 4 ),
                 new GfxDecodeInfo( -1 ) /* end of array */
        };



        /***************************************************************************

          Machine drivers

        ***************************************************************************/

        public static MachineDriver tapper_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                5000000,	/* 5 Mhz */
                                0,
                                readmem,writemem,readport,writeport,
                                mcr_interrupt,32
                        ),
                        new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2000000,	/* 2 Mhz */
                                2,
                                sound_readmem,sound_writemem,null,null,
                                mcr_sh_interrupt,26
                        )
                },
                30,
                mcr_init_machine,

                /* video hardware */
                32*16, 30*16, new rectangle( 0, 32*16-1, 0, 30*16-1 ),
                tapper_gfxdecodeinfo,
                1+8*16, 8*16,
                mcr3_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
                null,
                generic_vh_start,
                generic_vh_stop,
                mcr3_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                mcr_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );

        public static MachineDriver dotron_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                5000000,	/* 5 Mhz */
                                0,
                                readmem,writemem,dotron_readport,writeport,
                                mcr_interrupt,32
                        ),
                        new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2000000,	/* 2 Mhz */
                                2,
                                sound_readmem,sound_writemem,null,null,
                                mcr_sh_interrupt,26
                        )
                },
                30,
                mcr_init_machine,

                /* video hardware */
                32*16, 30*16, new rectangle( 0, 32*16-1, 0, 30*16-1 ),
                dotron_gfxdecodeinfo,
                1+8*16, 8*16,
                mcr3_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
                null,
                generic_vh_start,
                generic_vh_stop,
                mcr3_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                mcr_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );

        public static MachineDriver destderb_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                5000000,	/* 5 Mhz */
                                0,
                                readmem,writemem,destderb_readport,writeport,
                                mcr_interrupt,32
                        ),
                        new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2000000,	/* 2 Mhz */
                                2,
                                sound_readmem,sound_writemem,null,null,
                                mcr_sh_interrupt,26
                        )
                },
                30,
                mcr_init_machine,

                /* video hardware */
                32*16, 30*16, new rectangle( 0, 32*16-1, 0, 30*16-1 ),
                destderb_gfxdecodeinfo,
                1+8*16, 8*16,
                mcr3_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
                null,
                generic_vh_start,
                generic_vh_stop,
                mcr3_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                mcr_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );

        public static MachineDriver timber_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                5000000,	/* 5 Mhz */
                                0,
                                readmem,writemem,readport,writeport,
                                mcr_interrupt,32
                        ),
                        new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2000000,	/* 2 Mhz */
                                2,
                                timber_sound_readmem,timber_sound_writemem,null,null,
                                mcr_sh_interrupt,26
                        )
                },
                30,
                mcr_init_machine,

                /* video hardware */
                32*16, 30*16, new rectangle( 0, 32*16-1, 0, 30*16-1 ),
                timber_gfxdecodeinfo,
                1+8*16, 8*16,
                mcr3_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
                null,
                generic_vh_start,
                generic_vh_stop,
                mcr3_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                mcr_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );

        public static MachineDriver rampage_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                5000000,	/* 5 Mhz */
                                0,
                                rampage_readmem,rampage_writemem,readport,writeport,
                                mcr_interrupt,32
                        ),
                },
                30,
                mcr_init_machine_no_watchdog,

                /* video hardware */
                32*16, 30*16, new rectangle( 0, 32*16-1, 0, 30*16-1 ),
                rampage_gfxdecodeinfo,
                1+8*16, 8*16,
                rampage_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
                null,
                generic_vh_start,
                generic_vh_stop,
                rampage_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                mcr_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );

        public static MachineDriver spyhunt_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                5000000,	/* 5 Mhz */
                                0,
                                spyhunt_readmem,spyhunt_writemem,spyhunt_readport,sh_writeport,
                                mcr_interrupt,32
                        ),
                        new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2000000,	/* 2 Mhz */
                                2,
                                sound_readmem,sound_writemem,null,null,
                                mcr_sh_interrupt,26
                        )
                },
                30,
                mcr_init_machine,

                /* video hardware */
                30*16, 30*16, new rectangle( 0, 30*16-1, 0, 30*16-1 ),
                spyhunt_gfxdecodeinfo,
                1+8*16+4, 8*16+4,
                spyhunt_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
                null,
                spyhunt_vh_start,
                spyhunt_vh_stop,
                spyhunt_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                mcr_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );


        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr tapper_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "TAPPG0.BIN", 0x0000, 0x4000, 0xcb048516 );
                ROM_LOAD( "TAPPG1.BIN", 0x4000, 0x4000, 0x4f5f9141 );
                ROM_LOAD( "TAPPG2.BIN", 0x8000, 0x4000, 0x88f856dc );
                ROM_LOAD( "TAPPG3.BIN", 0xc000, 0x2000, 0x2bb09d80 );

                ROM_REGION(0x28000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "TAPBG1.BIN", 0x00000, 0x4000, 0xea6a7c78 );
                ROM_LOAD( "TAPBG0.BIN", 0x04000, 0x4000, 0x5dde8902 );
                ROM_LOAD( "TAPFG7.BIN", 0x08000, 0x4000, 0x9cfc4174 );
                ROM_LOAD( "TAPFG6.BIN", 0x0c000, 0x4000, 0x54a41abe );
                ROM_LOAD( "TAPFG5.BIN", 0x10000, 0x4000, 0xbdbb4c45 );
                ROM_LOAD( "TAPFG4.BIN", 0x14000, 0x4000, 0xed4ff871 );
                ROM_LOAD( "TAPFG3.BIN", 0x18000, 0x4000, 0xcd14ce26 );
                ROM_LOAD( "TAPFG2.BIN", 0x1c000, 0x4000, 0xfaa1aaa1 );
                ROM_LOAD( "TAPFG1.BIN", 0x20000, 0x4000, 0xf2cde3f3 );
                ROM_LOAD( "TAPFG0.BIN", 0x24000, 0x4000, 0xbe24e6b8 );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "tapsnda7.bin", 0x0000, 0x1000, 0x2a3cef68 );
                ROM_LOAD( "tapsnda8.bin", 0x1000, 0x1000, 0x1b700dfa );
                ROM_LOAD( "tapsnda9.bin", 0x2000, 0x1000, 0xb4de31ba );
                ROM_LOAD( "tapsda10.bin", 0x3000, 0x1000, 0x5700e3bc );
                ROM_END();
        }};
        static RomLoadPtr dotron_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "loc-pg0.1c",   0x0000, 0x4000, 0xba0da15f );
                ROM_LOAD( "loc-pg1.2c",   0x4000, 0x4000, 0xdc300191 );
                ROM_LOAD( "loc-pg2.3c",   0x8000, 0x4000, 0xab0b3800 );
                ROM_LOAD( "loc-pg1.4c",   0xc000, 0x2000, 0xf98c9f8e );

                ROM_REGION(0x14000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "loc-bg2.6f",   0x00000, 0x2000, 0x40167124 );
                ROM_LOAD( "loc-bg1.5f",   0x02000, 0x2000, 0xbb2d7a5d );
                ROM_LOAD( "loc-a.cp0",    0x04000, 0x2000, 0xb35f5374 );
                ROM_LOAD( "loc-b.cp9",    0x06000, 0x2000, 0x565a5c48 );
                ROM_LOAD( "loc-c.cp8",    0x08000, 0x2000, 0xef45d146 );
                ROM_LOAD( "loc-d.cp7",    0x0a000, 0x2000, 0x5e8a3ef3 );
                ROM_LOAD( "loc-e.cp6",    0x0c000, 0x2000, 0xce957f1a );
                ROM_LOAD( "loc-f.cp5",    0x0e000, 0x2000, 0xd26053ce );
                ROM_LOAD( "loc-g.cp4",    0x10000, 0x2000, 0x57a2b1ff );
                ROM_LOAD( "loc-h.cp3",    0x12000, 0x2000, 0x3bb4d475 );
                
                
                /*ROM_LOAD( "loc-g.cp4",    0x04000, 0x2000, 0x57a2b1ff );
                ROM_LOAD( "loc-h.cp3",    0x06000, 0x2000, 0x3bb4d475 );
                ROM_LOAD( "loc-e.cp6",    0x08000, 0x2000, 0xce957f1a );
                ROM_LOAD( "loc-f.cp5",    0x0a000, 0x2000, 0xd26053ce );
                ROM_LOAD( "loc-c.cp8",    0x0c000, 0x2000, 0xef45d146 );
                ROM_LOAD( "loc-d.cp7",    0x0e000, 0x2000, 0x5e8a3ef3 );
                ROM_LOAD( "loc-a.cp0",    0x10000, 0x2000, 0xb35f5374 );
                ROM_LOAD( "loc-b.cp9",    0x12000, 0x2000, 0x565a5c48 );*/
             
                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "sound0.a7",    0x00000, 0x1000, 0x6d39bf19 );
                ROM_LOAD( "sound1.a8",    0x01000, 0x1000, 0xac872e1d );
                ROM_LOAD( "sound2.a9",    0x02000, 0x1000, 0xe8ef6519 );
                ROM_LOAD( "sound3.a10",   0x03000, 0x1000, 0x6b5aeb02 );
                ROM_END();
        }};
        static RomLoadPtr destderb_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "DD_PRO", 0x0000, 0x4000, 0x92df12bf );
                ROM_LOAD( "DD_PRO1", 0x4000, 0x4000, 0x87f5f32b );
                ROM_LOAD( "DD_PRO2", 0x8000, 0x4000, 0xf7d3cba3 );

                ROM_REGION(0x24000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "DD_BG0.6F", 0x00000, 0x2000, 0xaca450be );
                ROM_LOAD( "DD_BG1.5F", 0x02000, 0x2000, 0xc6416501 );
                ROM_LOAD( "DD_FG-3.A10", 0x04000, 0x4000, 0xef11eb4f );
                ROM_LOAD( "DD_FG-7.A9", 0x08000, 0x4000, 0x34d19e2f );
                ROM_LOAD( "DD_FG-2.A8", 0x0c000, 0x4000, 0x39702db0 );
                ROM_LOAD( "DD_FG-6.A7", 0x10000, 0x4000, 0x1f76977e );
                ROM_LOAD( "DD_FG-1.A6", 0x14000, 0x4000, 0x154a3f9c );
                ROM_LOAD( "DD_FG-5.A5", 0x18000, 0x4000, 0xa895e3f7 );
                ROM_LOAD( "DD_FG-0.A4", 0x1c000, 0x4000, 0x113e4f22 );
                ROM_LOAD( "DD_FG-4.A3", 0x20000, 0x4000, 0x2cbae0ce );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "tcs_u5.bin",   0x0c000, 0x2000, 0xeca33b2c );
                ROM_LOAD( "tcs_u4.bin",   0x0e000, 0x2000, 0x3490289a );
                ROM_END();
        }};

        static RomLoadPtr timber_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "timpg0.bin", 0x0000, 0x4000, 0x2a48e890 );
                ROM_LOAD( "timpg1.bin", 0x4000, 0x4000, 0xb4fa87d0 );
                ROM_LOAD( "timpg2.bin", 0x8000, 0x4000, 0x4df6b19a );
                ROM_LOAD( "timpg3.bin", 0xc000, 0x2000, 0xfb590c8f );

                ROM_REGION(0x28000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "timbg1.bin", 0x00000, 0x4000, 0x5b0ff893 );
                ROM_LOAD( "timbg0.bin", 0x04000, 0x4000, 0xcbece7a8 );
                ROM_LOAD( "timfg7.bin", 0x08000, 0x4000, 0x5ed46eae );
                ROM_LOAD( "timfg6.bin", 0x0c000, 0x4000, 0x31c7f47f );
                ROM_LOAD( "timfg5.bin", 0x10000, 0x4000, 0x01ac936c );
                ROM_LOAD( "timfg4.bin", 0x14000, 0x4000, 0x8c437a91 );
                ROM_LOAD( "timfg3.bin", 0x18000, 0x4000, 0xf4aaa2fa );
                ROM_LOAD( "timfg2.bin", 0x1c000, 0x4000, 0x4cfe5f16 );
                ROM_LOAD( "timfg1.bin", 0x20000, 0x4000, 0x08963712 );
                ROM_LOAD( "timfg0.bin", 0x24000, 0x4000, 0x567d8457 );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "tima7.bin", 0x0000, 0x1000, 0x607ed3b8 );
                ROM_LOAD( "tima8.bin", 0x1000, 0x1000, 0x85853a95 );
                ROM_LOAD( "tima9.bin", 0x2000, 0x1000, 0x49e515b1 );
                ROM_END();
        }};
        static RomLoadPtr rampage_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "pro0rev3.3b",  0x00000, 0x08000, 0x2f7ca03c );
                ROM_LOAD( "pro1rev3.5b",  0x08000, 0x08000, 0xd89bd9a4 );

                ROM_REGION(0x48000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "bg-0", 0x00000, 0x4000, 0xefa953c5 );
                ROM_LOAD( "bg-1", 0x04000, 0x4000, 0x88fe2998 );
                ROM_LOAD( "fg-3", 0x08000, 0x10000, 0x06033763 );
                ROM_LOAD( "fg-2", 0x18000, 0x10000, 0xdf6c8714 );
                ROM_LOAD( "fg-1", 0x28000, 0x10000, 0xa0449c5e );
                ROM_LOAD( "fg-0", 0x38000, 0x10000, 0xf9f7bf39 );
                ROM_END();
        }};

        static RomLoadPtr spyhunt_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "cpu_pg0.6d", 0x0000, 0x2000, 0x69818221 );
                ROM_LOAD( "cpu_pg1.7d", 0x2000, 0x2000, 0xb2695673 );
                ROM_LOAD( "cpu_pg2.8d", 0x4000, 0x2000, 0xbbf9e30f );
                ROM_LOAD( "cpu_pg3.9d", 0x6000, 0x2000, 0x256011f6 );
                ROM_LOAD( "cpu_pg4.10d",0x8000, 0x2000, 0xf5a5e14b );
                ROM_LOAD( "cpu_pg5.11d",0xA000, 0x4000, 0x8d0af17c );

                ROM_REGION(0x29000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "cpu_bg2.5a", 0x0000, 0x2000, 0x6d2296e2 );
                ROM_LOAD( "cpu_bg3.6a", 0x2000, 0x2000, 0x113ff55b );
                ROM_LOAD( "cpu_bg0.3a", 0x4000, 0x2000, 0x0d0f68b7 );
                ROM_LOAD( "cpu_bg1.4a", 0x6000, 0x2000, 0x6d113309 );
                ROM_LOAD( "vid_6fg.a2", 0x8000, 0x4000, 0x80d21978 );
                ROM_LOAD( "vid_7fg.a1", 0xc000, 0x4000, 0x1a41cab7 );
                ROM_LOAD( "vid_4fg.a4", 0x10000, 0x4000, 0x0fdc474c );
                ROM_LOAD( "vid_5fg.a3", 0x14000, 0x4000, 0x638c1f46 );
                ROM_LOAD( "vid_2fg.a6", 0x18000, 0x4000, 0xa9e6820c );
                ROM_LOAD( "vid_3fg.a5", 0x1c000, 0x4000, 0xd87b5e19 );
                ROM_LOAD( "vid_0fg.a8", 0x20000, 0x4000, 0x3a09c10f );
                ROM_LOAD( "vid_1fg.a7", 0x24000, 0x4000, 0xd6383ff6 );
                ROM_LOAD( "cpu_alph.10g",0x28000, 0x1000, 0xf22c49b2 );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "snd_0sd.a8", 0x0000, 0x1000, 0xd920c104 );
                ROM_LOAD( "snd_1sd.a7", 0x1000, 0x1000, 0x241e9f44 );
                ROM_END();
        }};


        static int mcr3_hiload (String name, int addr, int len)
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

        static void mcr3_hisave (String name, int addr, int len)
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

        static HiscoreLoadPtr tapper_hiload = new HiscoreLoadPtr() { public int handler()   { return 0;/*TOFIX mcr3_hiload (name, 0xe000, 0x9d); */}};
        static HiscoreSavePtr  tapper_hisave = new HiscoreSavePtr() { public void handler()   {        /*TOFIX mcr3_hisave (name, 0xe000, 0x9d);*/ }};

        static HiscoreLoadPtr dotron_hiload = new HiscoreLoadPtr() { public int handler()  { return 0;/*TOFIX mcr3_hiload (name, 0xe543, 0xac);*/ }};
        static HiscoreSavePtr  dotron_hisave = new HiscoreSavePtr() { public void handler()  {        /*TOFIX mcr3_hisave (name, 0xe543, 0xac);*/ }};

        static HiscoreLoadPtr destderb_hiload = new HiscoreLoadPtr() { public int handler() { return 0;/*TOFIX mcr3_hiload (name, 0xe4e6, 0x153);*/ }};
        static HiscoreSavePtr  destderb_hisave = new HiscoreSavePtr() { public void handler() {        /*TOFIX mcr3_hisave (name, 0xe4e6, 0x153);*/ }};

        static HiscoreLoadPtr timber_hiload = new HiscoreLoadPtr() { public int handler()   { return 0;/*TOFIX mcr3_hiload (name, 0xe000, 0x9f);*/ }};
        static HiscoreSavePtr  timber_hisave = new HiscoreSavePtr() { public void handler()  {        /*TOFIX mcr3_hisave (name, 0xe000, 0x9f);*/ }};

        static HiscoreLoadPtr rampage_hiload = new HiscoreLoadPtr() { public int handler()  { return 0;/*TOFIX mcr3_hiload (name, 0xe631, 0x3f);*/ }};
        static HiscoreSavePtr  rampage_hisave = new HiscoreSavePtr() { public void handler()  {        /*TOFIX mcr3_hisave (name, 0xe631, 0x3f);*/ }};

        static HiscoreLoadPtr spyhunt_hiload = new HiscoreLoadPtr() { public int handler()  { return 0;/*TOFIX mcr3_hiload (name, 0xf42b, 0xfb);*/ }};
        static HiscoreSavePtr  spyhunt_hisave = new HiscoreSavePtr() { public void handler() {        /*TOFIX mcr3_hisave (name, 0xf42b, 0xfb);*/ }};

        static DecodePtr spyhunt_decode = new DecodePtr()
        {
            public void handler()
            {
 
           char RAM[]=Machine.memory_region[0];


                /* some versions of rom 11d have the top and bottom 8k swapped; to enable us to work with either
                   a correct set or a swapped set (both of which pass the checksum!), we swap them here */
                if (RAM[0xa000] != 0x0c)
                {
                        int i;
                         char temp;

                        for (i = 0;i < 0x2000;i++)
                        {
                                temp = RAM[0xa000 + i];
                                RAM[0xa000 + i] = RAM[0xc000 + i];
                                RAM[0xc000 + i] = temp;
                        }
                }
        }};

        public static GameDriver tapper_driver = new GameDriver
	(
                "Tapper",
                "tapper",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA",
                tapper_machine_driver,

                tapper_rom,
                null, null,
                null,

                null, tapper_input_ports, null, null, null,

                null, null,null,
                ORIENTATION_DEFAULT,

                tapper_hiload, tapper_hisave
        );

        public static GameDriver dotron_driver = new GameDriver
	(
                "Discs of Tron",
                "dotron",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA",
                dotron_machine_driver,

                dotron_rom,
                null, null,
                null,

                null, dotron_input_ports, mcr3_trak_ports, null, null,

                null, null,null,
                ORIENTATION_FLIP_X,

                dotron_hiload, dotron_hisave
        );

        public static GameDriver destderb_driver = new GameDriver
	(
                "Demolition Derby",
                "destderb",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA\nBRAD OLIVER",
                destderb_machine_driver,

                destderb_rom,
                null, null,
                null,

                null, destderb_input_ports, mcr3_trak_ports, null, null,

                null, null,null,
                ORIENTATION_DEFAULT,

                destderb_hiload, destderb_hisave
        );

        public static GameDriver timber_driver = new GameDriver
	(
                "Timber",
                "timber",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA\nBRAD OLIVER",
                timber_machine_driver,

                timber_rom,
                null, null,
                null,

                null, timber_input_ports, null, null, null,

                null, null,null,
                ORIENTATION_DEFAULT,

                timber_hiload, timber_hisave
        );

        public static GameDriver rampage_driver = new GameDriver
	(
                "Rampage",
                "rampage",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA\nBRAD OLIVER",
                rampage_machine_driver,

                rampage_rom,
                null, null,
                null,

                null, rampage_input_ports, null, null, null,

                null, null,null,
                ORIENTATION_DEFAULT,

                rampage_hiload, rampage_hisave
        );

        public static GameDriver spyhunt_driver = new GameDriver
	(
                "Spy Hunter",
                "spyhunt",
                "AARON GILES\nCHRISTOPHER KIRMSE\nNICOLA SALMORIA\nBRAD OLIVER\nLAWNMOWER MAN",
                spyhunt_machine_driver,

                spyhunt_rom,
                spyhunt_decode, null,
                null,

                null, spyhunt_input_ports, null, null, null,

                null, null,null,
                ORIENTATION_ROTATE_90,

                spyhunt_hiload, spyhunt_hisave
        );

}
