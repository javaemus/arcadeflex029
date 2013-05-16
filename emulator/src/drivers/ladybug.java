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
 * ported to v0.27
 *
 *  NOTES: romsets are from v0.36 roms
 *
 */

package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.inptport.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static sndhrdw.ladybug.*;
import static vidhrdw.generic.*;
import static vidhrdw.ladybug.*;
import static mame.memoryH.*;
public class ladybug
{

	public static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress(  0x9001, 0x9001, input_port_1_r ),	/* IN1 */
                new MemoryReadAddress(  0x0000, 0x5fff, MRA_ROM ),
                new MemoryReadAddress(  0x6000, 0x6fff, MRA_RAM ),
                new MemoryReadAddress(  0x8000, 0x8fff, MRA_NOP ),
                new MemoryReadAddress(  0x9000, 0x9000, input_port_0_r ),	/* IN0 */
                new MemoryReadAddress(  0x9002, 0x9002, input_port_3_r ),	/* DSW0 */
                new MemoryReadAddress(  0x9003, 0x9003, input_port_4_r ),	/* DSW1 */
                new MemoryReadAddress(  0xd000, 0xd7ff, MRA_RAM ),	/* video and color RAM */
                new MemoryReadAddress(  0xe000, 0xe000, input_port_2_r ),	/* IN2 */
		new MemoryReadAddress( -1 )	/* end of table */
	};

	public static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x5fff, MWA_ROM ),
                new MemoryWriteAddress( 0x6000, 0x6fff, MWA_RAM ),
                new MemoryWriteAddress( 0x7000, 0x73ff, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0xa000, 0xa000, ladybug_flipscreen_w ),
                new MemoryWriteAddress( 0xb000, 0xbfff, ladybug_sound1_w ),
                new MemoryWriteAddress( 0xc000, 0xcfff, ladybug_sound2_w ),
                new MemoryWriteAddress( 0xd000, 0xd3ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0xd400, 0xd7ff, colorram_w, colorram ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};

        public static InterruptPtr ladybug_interrupt = new InterruptPtr() {	public int handler()
        {
                if ((readinputport(5) & 1)!=0)	/* Left Coin */
                        return nmi_interrupt.handler();
                else if ((readinputport(5) & 2)!=0)	/* Right Coin */
                        return interrupt.handler();
                else return ignore_interrupt.handler();
        }};
        static InputPortPtr ladybug_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_UNUSED );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_TILT );

                PORT_START();	/* IN1 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
                /* This should be connected to the 4V clock. I don't think the game uses it. */
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
                /* Note that there are TWO VBlank inputs, one is active low, the other active */
                /* high. There are probably other differencies in the hardware, but emulating */
                /* them this way is enough to get the game running. */
                PORT_BIT( 0xc0, 0x40, IPT_VBLANK );

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_UNUSED );
                PORT_BIT( 0x0e, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
                PORT_BIT( 0xe0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* DSW0 */
                PORT_DIPNAME( 0x03, 0x03, "Difficulty", IP_KEY_NONE );
                PORT_DIPSETTING(    0x03, "Easy" );
                PORT_DIPSETTING(    0x02, "Medium" );
                PORT_DIPSETTING(    0x01, "Hard" );
                PORT_DIPSETTING(    0x00, "Hardest" );
                PORT_DIPNAME( 0x04, 0x04, "High Score Names", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "3 Letters" );
                PORT_DIPSETTING(    0x04, "10 Letters" );
                PORT_BITX(    0x08, 0x08, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Rack Test", OSD_KEY_F1, IP_JOY_NONE, 0 );
                PORT_DIPSETTING(    0x08, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x10, 0x10, "Freeze", IP_KEY_NONE );
                PORT_DIPSETTING(    0x10, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x20, 0x00, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "Upright" );
                PORT_DIPSETTING(    0x20, "Cocktail" );
                PORT_DIPNAME( 0x40, 0x40, "Free Play", IP_KEY_NONE );
                PORT_DIPSETTING(    0x40, "No" );
                PORT_DIPSETTING(    0x00, "Yes" );
                PORT_DIPNAME( 0x80, 0x80, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "3" );
                PORT_DIPSETTING(    0x00, "5" );

                PORT_START();	/* DSW1 */
                PORT_DIPNAME( 0x0f, 0x0f, "Right Coin", IP_KEY_NONE );
                PORT_DIPSETTING(    0x06, "4 Coins/1 Credit" );
                PORT_DIPSETTING(    0x08, "3 Coins/1 Credit" );
                PORT_DIPSETTING(    0x0a, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x07, "3 Coins/2 Credits" );
                PORT_DIPSETTING(    0x0f, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x09, "2 Coins/3 Credits" );
                PORT_DIPSETTING(    0x0e, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0x0d, "1 Coin/3 Credits" );
                PORT_DIPSETTING(    0x0c, "1 Coin/4 Credits" );
                PORT_DIPSETTING(    0x0b, "1 Coin/5 Credits" );
                /* settings 0x00 thru 0x05 all give 1 Coin/1 Credit */
                PORT_DIPNAME( 0xf0, 0xf0, "Left Coin", IP_KEY_NONE );
                PORT_DIPSETTING(    0x60, "4 Coins/1 Credit" );
                PORT_DIPSETTING(    0x80, "3 Coins/1 Credit" );
                PORT_DIPSETTING(    0xa0, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x70, "3 Coins/2 Credits" );
                PORT_DIPSETTING(    0xf0, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x90, "2 Coins/3 Credits" );
                PORT_DIPSETTING(    0xe0, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0xd0, "1 Coin/3 Credits" );
                PORT_DIPSETTING(    0xc0, "1 Coin/4 Credits" );
                PORT_DIPSETTING(    0xb0, "1 Coin/5 Credits" );
                /* settings 0x00 thru 0x50 all give 1 Coin/1 Credit */

                PORT_START();	/* FAKE */
                /* The coin slots are not memory mapped. Coin Left causes a NMI, */
                /* Coin Right an IRQ. This fake input port is used by the interrupt */
                /* handler to be notified of coin insertions. We use IPF_IMPULSE to */
                /* trigger exactly one interrupt, without having to check when the */
                /* user releases the key. */
                PORT_BITX(0x01, IP_ACTIVE_HIGH, IPT_COIN1 | IPF_IMPULSE,
                                "Coin Left", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BITX(0x02, IP_ACTIVE_HIGH, IPT_COIN2 | IPF_IMPULSE,
                                "Coin Right", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                INPUT_PORTS_END();
        }};
        static InputPortPtr snapjack_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_UNUSED );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_TILT );

                PORT_START();	/* IN1 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
                /* This should be connected to the 4V clock. I don't think the game uses it. */
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
                /* Note that there are TWO VBlank inputs, one is active low, the other active */
                /* high. There are probably other differencies in the hardware, but emulating */
                /* them this way is enough to get the game running. */
                PORT_BIT( 0xc0, 0x40, IPT_VBLANK );

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_UNUSED  );
                PORT_BIT( 0x0e, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
                PORT_BIT( 0xe0, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* DSW0 */
                PORT_DIPNAME( 0x03, 0x03, "Difficulty", IP_KEY_NONE );
                PORT_DIPSETTING(    0x03, "Easy" );
                PORT_DIPSETTING(    0x02, "Medium" );
                PORT_DIPSETTING(    0x01, "Hard" );
                PORT_DIPSETTING(    0x00, "Hardest" );
                PORT_DIPNAME( 0x04, 0x04, "High Score Names", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "3 Letters" );
                PORT_DIPSETTING(    0x04, "10 Letters" );
                PORT_DIPNAME( 0x08, 0x00, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "Upright" );
                PORT_DIPSETTING(    0x08, "Cocktail" );
                PORT_DIPNAME( 0x10, 0x00, "unused1?", IP_KEY_NONE );
                PORT_DIPSETTING(    0x10, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0x20, 0x00, "unused2?", IP_KEY_NONE );
                PORT_DIPSETTING(    0x20, "Off" );
                PORT_DIPSETTING(    0x00, "On" );
                PORT_DIPNAME( 0xc0, 0xc0, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "2" );
                PORT_DIPSETTING(    0xc0, "3" );
                PORT_DIPSETTING(    0x80, "4" );
                PORT_DIPSETTING(    0x40, "5" );

                PORT_START();	/* DSW1 */
                /* coinage is slightly different from Lady Bug and Cosmic Avenger */
                PORT_DIPNAME( 0x0f, 0x0f, "Right Coin", IP_KEY_NONE );
                PORT_DIPSETTING(    0x05, "4 Coins/1 Credit" );
                PORT_DIPSETTING(    0x07, "3 Coins/1 Credit" );
                PORT_DIPSETTING(    0x0a, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x06, "3 Coins/2 Credits" );
                PORT_DIPSETTING(    0x09, "2 Coins/2 Credits" );
                PORT_DIPSETTING(    0x0f, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x08, "2 Coins/3 Credits" );
                PORT_DIPSETTING(    0x0e, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0x0d, "1 Coin/3 Credits" );
                PORT_DIPSETTING(    0x0c, "1 Coin/4 Credits" );
                PORT_DIPSETTING(    0x0b, "1 Coin/5 Credits" );
                /* settings 0x00 thru 0x04 all give 1 Coin/1 Credit */
                PORT_DIPNAME( 0xf0, 0xf0, "Left Coin", IP_KEY_NONE );
                PORT_DIPSETTING(    0x50, "4 Coins/1 Credit" );
                PORT_DIPSETTING(    0x70, "3 Coins/1 Credit" );
                PORT_DIPSETTING(    0xa0, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x60, "3 Coins/2 Credits" );
                PORT_DIPSETTING(    0x90, "2 Coins/2 Credits" );
                PORT_DIPSETTING(    0xf0, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x80, "2 Coins/3 Credits" );
                PORT_DIPSETTING(    0xe0, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0xd0, "1 Coin/3 Credits" );
                PORT_DIPSETTING(    0xc0, "1 Coin/4 Credits" );
                PORT_DIPSETTING(    0xb0, "1 Coin/5 Credits" );
                /* settings 0x00 thru 0x04 all give 1 Coin/1 Credit */

                PORT_START();	/* FAKE */
                /* The coin slots are not memory mapped. Coin Left causes a NMI, */
                /* Coin Right an IRQ. This fake input port is used by the interrupt */
                /* handler to be notified of coin insertions. We use IPF_IMPULSE to */
                /* trigger exactly one interrupt, without having to check when the */
                /* user releases the key. */
                PORT_BITX(0x01, IP_ACTIVE_HIGH, IPT_COIN1 | IPF_IMPULSE,
                                "Coin Left", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                PORT_BITX(0x02, IP_ACTIVE_HIGH, IPT_COIN2 | IPF_IMPULSE,
                                "Coin Right", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
                INPUT_PORTS_END();
        }};
        static InputPortPtr cavenger_input_ports= new InputPortPtr(){ public void handler()  
        {
            PORT_START();	/* IN0 */
            PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
            PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
            PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
            PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
            PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
            PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_START1 );
            PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 );
            PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_TILT );

            PORT_START();	/* IN1 */
            PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
            PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
            PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
            PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
            PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
            /* This should be connected to the 4V clock. I don't think the game uses it. */
            PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
            /* Note that there are TWO VBlank inputs, one is active low, the other active */
            /* high. There are probably other differencies in the hardware, but emulating */
            /* them this way is enough to get the game running. */
            PORT_BIT( 0xc0, 0x40, IPT_VBLANK );

            PORT_START();	/* IN2 */
            PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON2 );
            PORT_BIT( 0x0e, IP_ACTIVE_LOW, IPT_UNUSED );
            PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
            PORT_BIT( 0xe0, IP_ACTIVE_LOW, IPT_UNUSED );

            PORT_START();	/* DSW0 */
            PORT_DIPNAME( 0x03, 0x03, "Difficulty", IP_KEY_NONE );
            PORT_DIPSETTING(    0x03, "Easy" );
            PORT_DIPSETTING(    0x02, "Medium" );
            PORT_DIPSETTING(    0x01, "Hard" );
            PORT_DIPSETTING(    0x00, "Hardest" );
            PORT_DIPNAME( 0x04, 0x04, "High Score Names", IP_KEY_NONE );
            PORT_DIPSETTING(    0x00, "3 Letters" );
            PORT_DIPSETTING(    0x04, "10 Letters" );
            PORT_DIPNAME( 0x08, 0x00, "Cabinet", IP_KEY_NONE );
            PORT_DIPSETTING(    0x00, "Upright" );
            PORT_DIPSETTING(    0x08, "Cocktail" );
            PORT_DIPNAME( 0x30, 0x00, "Initial High Score", IP_KEY_NONE );
            PORT_DIPSETTING(    0x00, "0" );
            PORT_DIPSETTING(    0x30, "5000" );
            PORT_DIPSETTING(    0x20, "8000" );
            PORT_DIPSETTING(    0x10, "10000" );
            PORT_DIPNAME( 0xc0, 0xc0, "Lives", IP_KEY_NONE );
            PORT_DIPSETTING(    0x00, "2" );
            PORT_DIPSETTING(    0xc0, "3" );
            PORT_DIPSETTING(    0x80, "4" );
            PORT_DIPSETTING(    0x40, "5" );

            PORT_START();	/* DSW1 */
            PORT_DIPNAME( 0x0f, 0x0f, "Right Coin", IP_KEY_NONE );
            PORT_DIPSETTING(    0x06, "4 Coins/1 Credit" );
            PORT_DIPSETTING(    0x08, "3 Coins/1 Credit" );
            PORT_DIPSETTING(    0x0a, "2 Coins/1 Credit" );
            PORT_DIPSETTING(    0x07, "3 Coins/2 Credits" );
            PORT_DIPSETTING(    0x0f, "1 Coin/1 Credit" );
            PORT_DIPSETTING(    0x09, "2 Coins/3 Credits" );
            PORT_DIPSETTING(    0x0e, "1 Coin/2 Credits" );
            PORT_DIPSETTING(    0x0d, "1 Coin/3 Credits" );
            PORT_DIPSETTING(    0x0c, "1 Coin/4 Credits" );
            PORT_DIPSETTING(    0x0b, "1 Coin/5 Credits" );
            /* settings 0x00 thru 0x05 all give 1 Coin/1 Credit */
            PORT_DIPNAME( 0xf0, 0xf0, "Left Coin", IP_KEY_NONE );
            PORT_DIPSETTING(    0x60, "4 Coins/1 Credit" );
            PORT_DIPSETTING(    0x80, "3 Coins/1 Credit" );
            PORT_DIPSETTING(    0xa0, "2 Coins/1 Credit" );
            PORT_DIPSETTING(    0x70, "3 Coins/2 Credits" );
            PORT_DIPSETTING(    0xf0, "1 Coin/1 Credit" );
            PORT_DIPSETTING(    0x90, "2 Coins/3 Credits" );
            PORT_DIPSETTING(    0xe0, "1 Coin/2 Credits" );
            PORT_DIPSETTING(    0xd0, "1 Coin/3 Credits" );
            PORT_DIPSETTING(    0xc0, "1 Coin/4 Credits" );
            PORT_DIPSETTING(    0xb0, "1 Coin/5 Credits" );
            /* settings 0x00 thru 0x50 all give 1 Coin/1 Credit */

            PORT_START();	/* FAKE */
            /* The coin slots are not memory mapped. Coin Left causes a NMI, */
            /* Coin Right an IRQ. This fake input port is used by the interrupt */
            /* handler to be notified of coin insertions. We use IPF_IMPULSE to */
            /* trigger exactly one interrupt, without having to check when the */
            /* user releases the key. */
            PORT_BITX(0x01, IP_ACTIVE_HIGH, IPT_COIN1 | IPF_IMPULSE,
                            "Coin Left", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
            PORT_BITX(0x02, IP_ACTIVE_HIGH, IPT_COIN2 | IPF_IMPULSE,
                            "Coin Right", IP_KEY_DEFAULT, IP_JOY_DEFAULT, 1 );
            INPUT_PORTS_END(); 
        }};

	public static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
                512,	/* 512 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 512*8*8 },	/* the two bitplanes are separated */
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                8*8	/* every char takes 8 consecutive bytes */
	);
	public static GfxLayout spritelayout = new GfxLayout
	(
                16,16,	/* 16*16 sprites */
                128,	/* 128 sprites */
                2,	/* 2 bits per pixel */
                 new int[]{ 1, 0 },	/* the two bitplanes are packed in two consecutive bits */
                 new int[]{ 0, 2, 4, 6, 8, 10, 12, 14,
                                8*16+0, 8*16+2, 8*16+4, 8*16+6, 8*16+8, 8*16+10, 8*16+12, 8*16+14 },
                 new int[]{ 23*16, 22*16, 21*16, 20*16, 19*16, 18*16, 17*16, 16*16,
                                7*16, 6*16, 5*16, 4*16, 3*16, 2*16, 1*16, 0*16 },
                64*8	/* every sprite takes 64 consecutive bytes */
	);

        public static GfxLayout spritelayout2 = new GfxLayout
        (
                8,8,	/* 8*8 sprites */
                512,	/* 512 sprites */
                2,	/* 2 bits per pixel */
                 new int[]{ 1, 0 },	/* the two bitplanes are packed in two consecutive bits */
                 new int[]{ 0, 2, 4, 6, 8, 10, 12, 14 },
                 new int[]{ 7*16, 6*16, 5*16, 4*16, 3*16, 2*16, 1*16, 0*16 },
                16*8	/* every sprite takes 16 consecutive bytes */
        );

	public static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,      0,  8 ),
                new GfxDecodeInfo( 1, 0x2000, spritelayout,  4*8, 16 ),
                new GfxDecodeInfo( 1, 0x2000, spritelayout2, 4*8, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};



	static char ladybug_color_prom[] =
	{
		/* palette */
		/* palette */
                0xF5,0x90,0x41,0x54,0x94,0x11,0x80,0x65,0x05,0xD4,0x01,0x00,0xB1,0xA0,0x00,0xF5,
                0x04,0xB1,0x00,0x15,0x11,0x25,0x90,0xD0,0xA0,0x90,0x15,0x84,0xB5,0x04,0x04,0x04,
                /* sprite color lookup table */
                0x00,0x59,0x33,0xB8,0x00,0xD4,0xA3,0x8D,0x00,0x2C,0x63,0xDD,0x00,0x22,0x38,0x1D,
                0x00,0x93,0x3A,0xDD,0x00,0xE2,0x38,0xDD,0x00,0x82,0x3A,0xD8,0x00,0x22,0x68,0x1D
	};

        static char snapjack_color_prom[] =
        {
                /* palette */
                0xF5,0x05,0x54,0xC1,0xC4,0x94,0x84,0x24,0xD0,0x90,0xA1,0x00,0x31,0x50,0x25,0xF5,
                0x90,0x31,0x05,0x25,0x05,0x94,0x30,0x41,0x05,0x94,0x61,0x30,0x94,0x50,0x05,0xA5,
                /* sprite color lookup table */
                0x00,0x9D,0x11,0xB8,0x00,0x79,0x62,0x18,0x00,0x9E,0x25,0xDA,0x00,0xD7,0xA3,0x79,
                0x00,0xDE,0x29,0x74,0x00,0xD4,0x75,0x9D,0x00,0xAD,0x86,0x97,0x00,0x5A,0x4C,0x17
        };

        static char cavenger_color_prom[] =
        {
                /* palette */
                0xF5,0xC4,0xD0,0xB1,0xD4,0x90,0x45,0x44,0x00,0x54,0x91,0x94,0x25,0x21,0x65,0xF5,
                0x21,0x00,0x25,0xD0,0xB1,0x90,0xD4,0xD4,0x25,0xB1,0xC4,0x90,0x65,0xD4,0x00,0x00,
                /* sprite color lookup table */
                0x00,0x78,0xA3,0xB5,0x00,0x8C,0x79,0x64,0x00,0xC3,0xEE,0xDD,0x00,0x3C,0xA2,0x4A,
                0x00,0x87,0xBA,0xDE,0x00,0x2A,0xAE,0xBB,0x00,0x8C,0xC2,0xB7,0x00,0xAC,0xE2,0x1D
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
				ladybug_interrupt, 1
			)
		},
		60,
		null,

		/* video hardware */
		32*8, 32*8, new rectangle( 1*8, 31*8-1, 4*8, 28*8-1 ),
		gfxdecodeinfo,
		32, 4 * 24,
		ladybug_vh_convert_color_prom,
                VIDEO_TYPE_RASTER,
		null,
		generic_vh_start,
		generic_vh_stop,
		ladybug_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		ladybug_sh_start,
		ladybug_sh_stop,
		ladybug_sh_update
	);



	/***************************************************************************

	  Game driver(s)

	***************************************************************************/
        static RomLoadPtr ladybug_rom= new RomLoadPtr(){ public void handler()  
        {
                        ROM_REGION(0x10000);	/* 64k for code */
                        ROM_LOAD( "lb1.cpu",  0x0000, 0x1000, 0xd09e0adb );
                        ROM_LOAD( "lb2.cpu",  0x1000, 0x1000, 0x88bc4a0a );
                        ROM_LOAD( "lb3.cpu",  0x2000, 0x1000, 0x53e9efce );
                        ROM_LOAD( "lb4.cpu",  0x3000, 0x1000, 0xffc424d7 );
                        ROM_LOAD( "lb5.cpu",  0x4000, 0x1000, 0xad6af809 );
                        ROM_LOAD( "lb6.cpu",  0x5000, 0x1000, 0xcf1acca4 );

                        ROM_REGION(0x4000);	/* temporary space for graphics (disposed after conversion) */
                        ROM_LOAD( "lb9.vid",  0x0000, 0x1000, 0x77b1da1e );
                        ROM_LOAD( "lb10.vid", 0x1000, 0x1000, 0xaa82e00b );
                        ROM_LOAD( "lb8.cpu",  0x2000, 0x1000, 0x8b99910b );
                        ROM_LOAD( "lb7.cpu",  0x3000, 0x1000, 0x86a5b448 );
                        ROM_END();
        }};

        static RomLoadPtr snapjack_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "sj2a.bin", 0x0000, 0x1000, 0x6b30fcda );
                ROM_LOAD( "sj2b.bin", 0x1000, 0x1000,  0x1f1088d1 );
                ROM_LOAD( "sj2c.bin", 0x2000, 0x1000, 0xedd65f3a );
                ROM_LOAD( "sj2d.bin", 0x3000, 0x1000, 0xf4481192 );
                ROM_LOAD( "sj2e.bin", 0x4000, 0x1000, 0x1bff7d05 );
                ROM_LOAD( "sj2f.bin", 0x5000, 0x1000, 0x21793edf );

                ROM_REGION(0x4000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "sj2i.bin", 0x0000, 0x1000, 0xff2011c7 );
                ROM_LOAD( "sj2j.bin", 0x1000, 0x1000, 0xf097babb );
                ROM_LOAD( "sj2h.bin", 0x2000, 0x1000, 0xb7f105b6 );
                ROM_LOAD( "sj2g.bin", 0x3000, 0x1000, 0x1cdb03a8 );
                ROM_END();
        }};

        static RomLoadPtr cavenger_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "1", 0x0000, 0x1000, 0x9e0cc781 );
                ROM_LOAD( "2", 0x1000, 0x1000, 0x5ce5b950 );
                ROM_LOAD( "3", 0x2000, 0x1000, 0xbc28218d );
                ROM_LOAD( "4", 0x3000, 0x1000, 0x2b32e9f5 );
                ROM_LOAD( "5", 0x4000, 0x1000, 0xd117153e );
                ROM_LOAD( "6", 0x5000, 0x1000, 0xc7d366cb );

                ROM_REGION(0x4000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "9", 0x0000, 0x1000, 0x63357785 );
                ROM_LOAD( "0", 0x1000, 0x1000, 0x52ad1133 );
                ROM_LOAD( "8", 0x2000, 0x1000, 0xb022bf2d );
                ROM_END();
        }};

                static HiscoreLoadPtr ladybug_hiload = new HiscoreLoadPtr() { public int handler()
                {
                        /* check if the hi score table has already been initialized */
               /*TOFIX                  if (memcmp(RAM, 0x6073, new char[] { 0x01, 0x00, 0x00 }, 3) == 0 &&
                                        memcmp(RAM, 0x608b, new char[] { 0x01, 0x00, 0x00 }, 3) == 0)
                        {
                                FILE f;


                                if ((f = fopen(name, "rb")) != null)
                                {
                                        fread(RAM, 0x6073, 1, 3*9, f);
                                        fread(RAM, 0xd380, 1, 13*9, f);
                                        fclose(f);
                                }

                                return 1;
                        }
                        else */return 0;	/* we can't load the hi scores yet */
                } };
                static HiscoreSavePtr ladybug_hisave = new HiscoreSavePtr() { public void handler()
                {
                        FILE f;


                 /*TOFIX                if ((f = fopen(name, "wb")) != null)
                        {
                                fwrite(RAM, 0x6073, 1, 3*9, f);
                                fwrite(RAM, 0xd380, 1, 13*9, f);
                                fclose(f);
                        }*/
                } };
                static HiscoreLoadPtr cavenger_hiload = new HiscoreLoadPtr() { public int handler()
                {

                        /* check if the hi score table has already been initialized */
      /*TOFIX                           if ((memcmp(RAM,0x6025,new char[]{0x01,0x00,0x00},3) == 0) &&
                                (memcmp(RAM,0x6063,new char[]{0x0A,0x15,0x28},3) == 0))
                        {
                                FILE f;


           /*TOFIX                              if ((f = fopen(name,"rb")) != null)
                                {
                                        fread(RAM,0x6025,1,0x41,f);
                                        fclose(f);
                                }

                                return 1;
                        }
                        else */ return 0;	/* we can't load the hi scores yet */
                }};


                static HiscoreSavePtr cavenger_hisave = new HiscoreSavePtr() { public void handler()
                {
                        FILE f;


                      /*TOFIX           if ((f = fopen(name,"wb")) != null)
                        {
                                fwrite(RAM,0x6025,1,0x41,f);
                                fclose(f);
                        } */

                }};
                static HiscoreLoadPtr snapjack_hiload = new HiscoreLoadPtr() { public int handler()
                {
                        /* check if the hi score table has already been initialized */
               /*TOFIX                  if ((memcmp(RAM,0x6A94,new char[]{0x01,0x00,0x00},3) == 0) &&
                                (memcmp(RAM,0x6AA0,new char[]{0x01,0x00,0x00,0x1E},4) == 0) &&
                                (memcmp(RAM,0x6AD2,new char[]{0x0A,0x15,0x24},3) == 0))
                        {
                                FILE f;


                                if ((f = fopen(name,"rb")) != null)
                                {
                                        fread(RAM,0x6A94,1,0x41,f);
                                        fclose(f);
                                }

                                return 1;
                        }
                        else */ return 0;	/* we can't load the hi scores yet */
                }};


                static HiscoreSavePtr snapjack_hisave = new HiscoreSavePtr() { public void handler()
                {
                        FILE f;


                  /*TOFIX               if ((f = fopen(name,"wb")) != null)
                        {
                                fwrite(RAM,0x6A94,1,0x41,f);
                                fclose(f);
                        } */
                }};

                public static GameDriver ladybug_driver = new GameDriver
                (
                        "Lady Bug",
                        "ladybug",
                        "Nicola Salmoria",
                        machine_driver,

                        ladybug_rom,
                        null, null,
                        null,

                        null,ladybug_input_ports, null, null, null,

                        ladybug_color_prom, null, null,
                        ORIENTATION_ROTATE_270,

                        ladybug_hiload, ladybug_hisave
                );
                public static GameDriver snapjack_driver  = new GameDriver
                (
                        "Snap Jack",
                        "snapjack",
                        "Nicola Salmoria (MAME driver)\nMike Balfour (high score save)",
                        machine_driver,

                        snapjack_rom,
                        null, null,
                        null,

                        null,snapjack_input_ports, null, null, null,

                        snapjack_color_prom,null, null,
                        ORIENTATION_DEFAULT,

                        snapjack_hiload, snapjack_hisave
                );

                public static GameDriver cavenger_driver = new GameDriver
                (
                        "Cosmic Avenger",
                        "cavenger",
                        "Nicola Salmoria (MAME driver)\nMike Balfour (high score save)",
                        machine_driver,

                        cavenger_rom,
                        null, null,
                        null,

                        null,cavenger_input_ports, null, null,null,

                        cavenger_color_prom, null, null,
                       ORIENTATION_DEFAULT,

                        cavenger_hiload, cavenger_hisave
                );
}
