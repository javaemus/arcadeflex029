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
 * roms are using v0.36 romset
 */
package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static sndhrdw.generic.*;
import static vidhrdw.yard.*;


public class yard {
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0xe000, 0xefff, MRA_RAM ),
                new MemoryReadAddress( 0xd000, 0xd000, input_port_0_r ),	/* IN0 */
                new MemoryReadAddress( 0xd001, 0xd001, input_port_1_r ),	/* IN1 */
                new MemoryReadAddress( 0xd002, 0xd002, input_port_2_r ),	/* IN2 */
                new MemoryReadAddress( 0xd003, 0xd003, input_port_3_r ),	/* DSW1 */
                new MemoryReadAddress( 0xd004, 0xd004, input_port_4_r ),	/* DSW2 */
                new MemoryReadAddress( 0x8000, 0x8fff, MRA_RAM ),         /* Video and Color ram */
                new MemoryReadAddress( 0x9000, 0x9fff, MRA_RAM ),
                new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0xe000, 0xefff, MWA_RAM ),
                new MemoryWriteAddress( 0x8000, 0x9fff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0xc820, 0xc87f, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0xa000, 0xa000, MWA_RAM, yard_scroll_x_low ),
                new MemoryWriteAddress( 0xa200, 0xa200, MWA_RAM, yard_scroll_x_high ),
                new MemoryWriteAddress( 0xa400, 0xa400, MWA_RAM, yard_scroll_y_low ),
                new MemoryWriteAddress( 0xa800, 0xa800, MWA_RAM ), //?
                new MemoryWriteAddress( 0xd000, 0xd000, MWA_RAM ), //?
                new MemoryWriteAddress( 0xd001, 0xd001, MWA_RAM ), //?
                new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };


        static IOReadPort readport[] =
        {
                new IOReadPort( -1 )	/* end of table */
        };

        static IOWritePort writeport[] =
        {
                new IOWritePort( -1 )	/* end of table */
        };


        static InputPort input_ports[] =
        {
                new InputPort(	/* IN0 */
                        0xff,
                         new int[]{ OSD_KEY_1, OSD_KEY_2, OSD_KEY_3, 0, 0, 0, 0, 0 }
                ),
                new InputPort(	/* IN1 */
                        0xff,
                         new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_DOWN, OSD_KEY_UP,
                                        0, OSD_KEY_CONTROL, 0, OSD_KEY_ALT }
                ),
                new InputPort(	/* IN2 */
                        0xff,
                         new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(	/* DSW1 */
                        0xff,
                         new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(	/* DSW2 */
                        0xfd,
                         new int[]{ 0, 0, 0, 0, 0, 0, 0, OSD_KEY_F1 }
                ),
                new InputPort( -1 )	/* end of table */
        };

        static TrakPort trak_ports[] =
        {
                    new TrakPort(-1)
        };


        static KEYSet keys[] =
        {
                new KEYSet( 1, 3, "MOVE UP" ),
                new KEYSet( 1, 1, "MOVE LEFT"  ),
                new KEYSet( 1, 0, "MOVE RIGHT" ),
                new KEYSet( 1, 2, "MOVE DOWN" ),
                new KEYSet( 1, 5, "THROW" ),
                new KEYSet( 1, 7, "PASS" ),
                new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                new DSW( 3, 0x01, "SW1B", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 3, 0x02, "SW2B", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 3, 0x04, "SW3B", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 4, 0x08, "SW4B", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 4, 0x10, "SW5B", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 4, 0x20, "SW6B", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 4, 0x40, "SW7B", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( 4, 0x80, "SW8B", new String[]{ "ON", "OFF" }, 1 ),
                new DSW( -1 )
        };



        static GfxLayout charlayout = new GfxLayout
            (
                8,8,	/* 8*8 characters */
                1024,	/* 1024 characters */
                3,	/* 2 bits per pixel */
                 new int[]{ 0, 0x2000*8, 0x4000*8 },
                 new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 },
                 new int[]{ 8*0, 8*1, 8*2, 8*3, 8*4, 8*5, 8*6, 8*7 },
                8*8	/* every char takes 8 consecutive bytes */
        );
        static GfxLayout spritelayout = new GfxLayout
        (
                16,16,	/* 16*16 sprites */
                256,	/* 256 sprites */
                3,	/* 3 bits per pixel */
                 new int[]{ 0,0x4000*8, 0x8000*8 },
                 new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7},
                 new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8, 8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
                32*8	/* every sprite takes 32 consecutive bytes */
        );


        static GfxDecodeInfo gfxdecodeinfo[] =
        {

                new GfxDecodeInfo( 1, 0x00000, charlayout,           0, 32 ),
                new GfxDecodeInfo( 1, 0x06000, spritelayout,      32*8, 32 ),
                new GfxDecodeInfo( 1, 0x08000, spritelayout,      32*8, 32 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };


        static char color_prom[] =
        {
                /* G-1J - character palette red component */
                0x00,0x0F,0x00,0x0F,0x00,0x0F,0x00,0x0F,0x0A,0x0E,0x0D,0x0F,0x0E,0x0B,0x0C,0x0E,
                0x0B,0x0F,0x08,0x0C,0x06,0x0D,0x0B,0x0A,0x00,0x0B,0x0F,0x0A,0x0F,0x0C,0x0A,0x0F,
                0x0A,0x0C,0x0F,0x0F,0x00,0x0E,0x0B,0x0C,0x0C,0x00,0x0D,0x0D,0x0E,0x0A,0x0C,0x0F,
                0x0A,0x0D,0x00,0x0D,0x0F,0x0C,0x07,0x0C,0x0E,0x00,0x0F,0x0F,0x00,0x0B,0x0F,0x0F,
                0x0F,0x0B,0x0F,0x0C,0x0D,0x0F,0x00,0x0D,0x0A,0x0F,0x0C,0x0B,0x0B,0x0E,0x0E,0x0F,
                0x0B,0x0F,0x0D,0x0E,0x0D,0x00,0x0C,0x07,0x0A,0x0F,0x0F,0x0F,0x0A,0x0C,0x00,0x0F,
                0x0A,0x00,0x00,0x0F,0x09,0x0C,0x0C,0x0E,0x0F,0x0B,0x0C,0x0E,0x09,0x0A,0x00,0x0B,
                0x09,0x0B,0x00,0x0F,0x09,0x0C,0x00,0x0B,0x09,0x0F,0x00,0x0F,0x0F,0x0C,0x0D,0x0B,
                0x09,0x0F,0x0B,0x0E,0x00,0x0D,0x0B,0x0B,0x0A,0x00,0x0C,0x0E,0x0B,0x0F,0x00,0x0B,
                0x0A,0x00,0x0C,0x0E,0x0C,0x0F,0x0A,0x0B,0x0A,0x0A,0x0C,0x0E,0x0C,0x0F,0x0C,0x0B,
                0x00,0x0F,0x0F,0x0A,0x0F,0x0F,0x0C,0x0E,0x00,0x00,0x0F,0x0A,0x0F,0x0C,0x0A,0x0B,
                0x0A,0x09,0x0C,0x0E,0x00,0x0E,0x0B,0x0C,0x0F,0x0F,0x0A,0x0F,0x00,0x0F,0x0F,0x0F,
                0x0F,0x0F,0x00,0x0F,0x0F,0x0F,0x0F,0x0F,0x0A,0x0F,0x0A,0x0F,0x0F,0x0F,0x00,0x0F,
                0x0A,0x0F,0x0A,0x0F,0x0F,0x0F,0x00,0x0F,0x0F,0x0A,0x00,0x0F,0x0F,0x0F,0x0D,0x0E,
                0x06,0x0F,0x0F,0x0D,0x0C,0x08,0x0F,0x0E,0x0A,0x0F,0x0F,0x0E,0x06,0x0E,0x0D,0x0B,
                0x06,0x0B,0x0F,0x0D,0x00,0x0E,0x0F,0x0F,0x0F,0x0F,0x09,0x0B,0x0A,0x08,0x0D,0x0E,
                /* B-1M - sprite palette red component */
                0x00,0x0A,0x0C,0x0F,0x0F,0x0C,0x09,0x0F,0x00,0x0F,0x0C,0x0F,0x0F,0x0A,0x0C,0x0F,
                0x00,0x0C,0x09,0x0F,0x0F,0x0A,0x00,0x0F,0x00,0x0F,0x0C,0x0F,0x00,0x0A,0x0B,0x0F,
                0x00,0x09,0x0B,0x0C,0x0D,0x0E,0x0F,0x0F,0x00,0x00,0x0B,0x0C,0x0D,0x0E,0x0F,0x0F,
                0x00,0x0C,0x0A,0x0E,0x09,0x0B,0x0F,0x0F,0x00,0x0F,0x0F,0x0E,0x0F,0x0E,0x0A,0x0B,
                0x00,0x0F,0x0F,0x0D,0x0C,0x0A,0x08,0x0B,0x00,0x0E,0x0F,0x0D,0x0C,0x0A,0x08,0x0F,
                0x00,0x0D,0x07,0x0E,0x00,0x0F,0x07,0x0F,0x00,0x0F,0x08,0x0F,0x07,0x0F,0x0C,0x0F,
                0x00,0x0F,0x07,0x0F,0x07,0x0F,0x0B,0x0F,0x00,0x0D,0x0F,0x0F,0x00,0x0E,0x08,0x0F,
                0x00,0x0F,0x0B,0x00,0x08,0x0C,0x0D,0x0F,0x00,0x0F,0x0B,0x0F,0x0F,0x0C,0x0D,0x0F,
                0x00,0x0F,0x0B,0x0F,0x08,0x0C,0x0D,0x0F,0x00,0x0D,0x09,0x0B,0x00,0x0E,0x0B,0x0F,
                0x00,0x0D,0x0B,0x0F,0x09,0x0E,0x0D,0x0F,0x00,0x0D,0x0F,0x0E,0x0A,0x0B,0x0B,0x0E,
                0x00,0x0D,0x08,0x0C,0x0A,0x0B,0x0B,0x0E,0x00,0x0F,0x07,0x0F,0x00,0x0F,0x0C,0x0F,
                0x00,0x0A,0x0A,0x0D,0x09,0x0F,0x0C,0x0E,0x00,0x0D,0x0A,0x0F,0x09,0x0F,0x0C,0x0E,
                0x00,0x0F,0x0D,0x0F,0x0B,0x0F,0x0D,0x0E,0x00,0x0F,0x0C,0x0F,0x0B,0x0E,0x0E,0x0D,
                0x00,0x0F,0x0A,0x0F,0x0B,0x0E,0x0B,0x0D,0x00,0x0F,0x0D,0x0E,0x00,0x0F,0x0A,0x0F,
                0x00,0x0F,0x0D,0x0E,0x0D,0x0F,0x0C,0x0F,0x00,0x0F,0x0C,0x0D,0x0C,0x0F,0x0F,0x0F,
                0x00,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,0x00,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,
                /* G-1F - character palette green component */
                0x00,0x00,0x0F,0x0F,0x00,0x00,0x0F,0x0F,0x08,0x0B,0x0A,0x00,0x0E,0x08,0x0A,0x0E,
                0x0B,0x0F,0x0C,0x0E,0x06,0x09,0x09,0x0A,0x00,0x08,0x0F,0x0C,0x0F,0x09,0x0A,0x0E,
                0x08,0x03,0x08,0x0B,0x00,0x0E,0x08,0x0A,0x0A,0x0C,0x0F,0x09,0x0E,0x08,0x0C,0x0C,
                0x08,0x0F,0x0E,0x09,0x0C,0x0C,0x07,0x0A,0x0E,0x0F,0x0C,0x0D,0x0F,0x08,0x0F,0x00,
                0x00,0x08,0x0F,0x0A,0x09,0x0C,0x0E,0x0F,0x0C,0x0C,0x0A,0x08,0x08,0x0D,0x0E,0x0F,
                0x08,0x0C,0x09,0x0E,0x0F,0x0E,0x0C,0x07,0x08,0x0F,0x0F,0x00,0x08,0x00,0x00,0x0F,
                0x08,0x0B,0x00,0x0C,0x09,0x0A,0x0C,0x0E,0x0F,0x08,0x0A,0x0E,0x09,0x0C,0x0D,0x0F,
                0x09,0x08,0x0E,0x0D,0x09,0x0A,0x0D,0x0F,0x09,0x0B,0x0E,0x0D,0x0F,0x0A,0x0D,0x0B,
                0x09,0x0B,0x08,0x0E,0x0D,0x0D,0x0F,0x0B,0x08,0x00,0x0A,0x0E,0x08,0x0C,0x0D,0x0A,
                0x08,0x00,0x0A,0x0E,0x09,0x0C,0x0C,0x08,0x08,0x08,0x0A,0x0E,0x09,0x0C,0x0A,0x08,
                0x00,0x0F,0x09,0x0A,0x0F,0x0F,0x0C,0x0E,0x00,0x0F,0x00,0x0A,0x0F,0x0C,0x0A,0x0B,
                0x08,0x09,0x0F,0x0E,0x00,0x0B,0x0E,0x0A,0x0F,0x0F,0x08,0x0F,0x00,0x0F,0x0F,0x0F,
                0x0F,0x0F,0x00,0x0F,0x00,0x0F,0x0F,0x0F,0x08,0x0F,0x08,0x0F,0x00,0x0F,0x00,0x0F,
                0x08,0x00,0x08,0x0F,0x00,0x09,0x00,0x0F,0x0E,0x0A,0x00,0x0F,0x00,0x0C,0x0B,0x0C,
                0x06,0x0F,0x00,0x02,0x0E,0x0C,0x0F,0x0D,0x08,0x0F,0x00,0x0E,0x06,0x0D,0x02,0x08,
                0x06,0x08,0x0F,0x02,0x0B,0x0D,0x00,0x0F,0x0E,0x00,0x08,0x08,0x0A,0x0C,0x0C,0x0E,
                /* B-1N - sprite palette green component */
                0x00,0x07,0x0C,0x0B,0x0C,0x0F,0x09,0x0F,0x00,0x00,0x0C,0x0B,0x0C,0x0D,0x0F,0x0F,
                0x00,0x0C,0x09,0x0B,0x0C,0x0D,0x00,0x0F,0x00,0x00,0x0F,0x0C,0x00,0x07,0x09,0x0B,
                0x00,0x09,0x0B,0x0D,0x00,0x0A,0x0B,0x0C,0x00,0x00,0x0B,0x0D,0x00,0x0A,0x0B,0x0F,
                0x00,0x0D,0x0B,0x0E,0x09,0x08,0x0D,0x0F,0x00,0x0F,0x0F,0x0A,0x0D,0x0C,0x08,0x0F,
                0x00,0x0B,0x0C,0x0E,0x0D,0x09,0x08,0x0B,0x00,0x0E,0x0C,0x0E,0x0D,0x09,0x08,0x0F,
                0x00,0x0C,0x0B,0x0D,0x00,0x0F,0x09,0x0F,0x00,0x00,0x0F,0x0B,0x07,0x0F,0x0A,0x0F,
                0x00,0x00,0x0C,0x0F,0x0A,0x0C,0x0E,0x0F,0x00,0x00,0x0F,0x0F,0x00,0x0A,0x08,0x0C,
                0x00,0x0C,0x09,0x00,0x08,0x0C,0x0B,0x0F,0x00,0x0C,0x09,0x0F,0x0A,0x0C,0x0B,0x0F,
                0x00,0x0C,0x09,0x0F,0x08,0x0C,0x0B,0x0F,0x00,0x00,0x0F,0x0B,0x0D,0x09,0x08,0x0C,
                0x00,0x00,0x0B,0x0F,0x09,0x09,0x0D,0x0C,0x00,0x08,0x0D,0x0E,0x0E,0x0B,0x0F,0x0E,
                0x00,0x08,0x0C,0x07,0x0E,0x0B,0x0F,0x0E,0x00,0x00,0x07,0x0F,0x00,0x0F,0x09,0x0F,
                0x00,0x09,0x0A,0x0C,0x09,0x0F,0x0C,0x0D,0x00,0x0B,0x0A,0x0F,0x09,0x0D,0x0C,0x0D,
                0x00,0x00,0x0D,0x0F,0x0A,0x0F,0x0B,0x0E,0x00,0x08,0x0B,0x0F,0x07,0x0B,0x0D,0x0D,
                0x00,0x08,0x0A,0x0F,0x07,0x0B,0x0B,0x0D,0x00,0x00,0x0C,0x0D,0x00,0x09,0x07,0x0F,
                0x00,0x00,0x0C,0x0D,0x00,0x09,0x0C,0x0F,0x00,0x0F,0x0C,0x0F,0x0C,0x0F,0x0F,0x0F,
                0x00,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,0x00,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,
                /* G-1H - character palette blue component */
                0x00,0x00,0x00,0x00,0x0F,0x0F,0x0F,0x0F,0x0D,0x08,0x07,0x03,0x0C,0x00,0x08,0x0E,
                0x0B,0x0F,0x08,0x0C,0x06,0x05,0x05,0x0A,0x00,0x00,0x00,0x0F,0x0F,0x08,0x0A,0x0C,
                0x0D,0x04,0x08,0x0B,0x00,0x0C,0x00,0x08,0x08,0x0C,0x0F,0x07,0x0C,0x0D,0x0C,0x07,
                0x0D,0x0F,0x0E,0x07,0x07,0x0C,0x07,0x08,0x0C,0x00,0x00,0x00,0x0F,0x00,0x00,0x00,
                0x03,0x00,0x00,0x08,0x07,0x07,0x0E,0x0F,0x0F,0x07,0x08,0x00,0x00,0x0A,0x0E,0x0F,
                0x00,0x07,0x07,0x0C,0x0F,0x0E,0x0C,0x07,0x0D,0x0F,0x0F,0x03,0x0D,0x08,0x00,0x0F,
                0x0D,0x0C,0x00,0x07,0x09,0x08,0x0C,0x0C,0x0F,0x00,0x08,0x0C,0x09,0x0F,0x00,0x0B,
                0x09,0x00,0x0A,0x09,0x09,0x08,0x00,0x0B,0x09,0x0B,0x0A,0x09,0x0F,0x08,0x0D,0x0B,
                0x09,0x0B,0x00,0x0C,0x00,0x0D,0x0B,0x0B,0x0D,0x00,0x08,0x0C,0x00,0x0B,0x0D,0x08,
                0x0D,0x00,0x08,0x0C,0x08,0x0B,0x0F,0x00,0x0D,0x00,0x08,0x0C,0x08,0x0B,0x08,0x00,
                0x00,0x0F,0x07,0x0A,0x0C,0x0B,0x00,0x00,0x00,0x0F,0x0F,0x0A,0x0F,0x0C,0x0A,0x0B,
                0x0D,0x09,0x0C,0x0C,0x0F,0x09,0x0E,0x08,0x0F,0x0F,0x0D,0x0F,0x00,0x0F,0x0F,0x0F,
                0x0F,0x0F,0x00,0x0F,0x00,0x0F,0x0F,0x0F,0x0D,0x00,0x0D,0x00,0x00,0x00,0x0A,0x0F,
                0x0D,0x00,0x0D,0x0F,0x00,0x09,0x00,0x0F,0x0D,0x0A,0x00,0x00,0x00,0x0B,0x09,0x0A,
                0x06,0x00,0x00,0x0C,0x0C,0x08,0x0F,0x0A,0x0D,0x00,0x00,0x0C,0x06,0x0A,0x0C,0x00,
                0x06,0x00,0x00,0x0C,0x00,0x0A,0x00,0x0F,0x0D,0x00,0x0B,0x00,0x0A,0x08,0x0B,0x0C,
                /* B-1L - sprite palette blue component */
                0x00,0x00,0x0C,0x09,0x0A,0x0F,0x09,0x0F,0x00,0x00,0x0C,0x09,0x0A,0x0E,0x0F,0x0F,
                0x00,0x0C,0x09,0x09,0x0A,0x0E,0x00,0x0F,0x00,0x00,0x0F,0x0A,0x00,0x00,0x07,0x09,
                0x00,0x0C,0x0D,0x0F,0x0D,0x0D,0x09,0x0A,0x00,0x00,0x0D,0x0F,0x0D,0x0D,0x09,0x00,
                0x00,0x0F,0x0C,0x0E,0x09,0x00,0x0B,0x0F,0x00,0x00,0x0F,0x00,0x0B,0x0A,0x00,0x0B,
                0x00,0x09,0x0A,0x0E,0x0F,0x07,0x08,0x0D,0x00,0x08,0x0A,0x0E,0x0F,0x07,0x08,0x0F,
                0x00,0x09,0x07,0x09,0x00,0x0F,0x07,0x0F,0x00,0x00,0x08,0x07,0x0B,0x0F,0x0F,0x0F,
                0x00,0x00,0x07,0x00,0x07,0x07,0x0E,0x0F,0x00,0x00,0x0F,0x00,0x0B,0x07,0x0F,0x07,
                0x00,0x00,0x08,0x00,0x0B,0x0C,0x09,0x0F,0x00,0x00,0x08,0x00,0x00,0x0C,0x09,0x0F,
                0x00,0x00,0x08,0x00,0x0B,0x0C,0x09,0x0F,0x00,0x00,0x09,0x0B,0x00,0x09,0x00,0x0A,
                0x00,0x00,0x0B,0x0F,0x09,0x09,0x0D,0x0A,0x00,0x00,0x0B,0x00,0x0A,0x0B,0x0B,0x0E,
                0x00,0x00,0x08,0x00,0x0A,0x0B,0x0B,0x0E,0x00,0x00,0x07,0x00,0x0E,0x0F,0x0C,0x0F,
                0x00,0x07,0x0A,0x09,0x09,0x0F,0x0C,0x0A,0x00,0x0C,0x0A,0x0F,0x09,0x0D,0x0C,0x0A,
                0x00,0x00,0x0D,0x00,0x08,0x0F,0x09,0x0E,0x00,0x08,0x08,0x09,0x08,0x09,0x0B,0x0D,
                0x00,0x08,0x0A,0x09,0x08,0x09,0x0B,0x0D,0x00,0x09,0x09,0x0B,0x00,0x00,0x08,0x0F,
                0x00,0x09,0x09,0x0B,0x00,0x00,0x0C,0x0F,0x00,0x0F,0x00,0x00,0x0C,0x0F,0x0F,0x0F,
                0x00,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,0x00,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F
        };



        static MachineDriver machine_driver = new MachineDriver
           (
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                4000000,	/* 4 Mhz (?) */
                                0,
                                readmem,writemem,readport,writeport,
                                interrupt,1
                        ),
                },
                60,
                null,

                /* video hardware */
                32*8, 32*8, new rectangle( 0*8, 32*8-1, 0*8, 32*8-1 ),
                gfxdecodeinfo,
                256,32*8+32*8,
                yard_vh_convert_color_prom,
                VIDEO_TYPE_RASTER,
                null,
                yard_vh_start,
                yard_vh_stop,
                yard_vh_screenrefresh,

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


        static RomLoadPtr yard_rom= new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);	/* 64k for code */                  
                ROM_LOAD( "yf-a-3p",      0x0000, 0x2000, 0x4586114f );
                ROM_LOAD( "yf-a-3n",      0x2000, 0x2000, 0x947fa760 );
                ROM_LOAD( "yf-a-3m",      0x4000, 0x2000, 0xd4975633 );
                
                ROM_REGION(0x12000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "yf-a-3c", 0x0000, 0x2000, 0xdcd73627 );	/* chars */
                ROM_LOAD( "yf-a-3e", 0x2000, 0x2000, 0xab018fe9 );
                ROM_LOAD( "yf-a-3d", 0x4000, 0x2000, 0x82a60c38 );

                ROM_LOAD( "yf-b-5b", 0x6000, 0x2000, 0x5331aec5 );	/* sprites */
                ROM_LOAD( "yf-b-5c", 0x8000, 0x2000, 0xc052c88c );
                ROM_LOAD( "yf-b-5f", 0xa000, 0x2000, 0x7fdade5c );
                ROM_LOAD( "yf-b-5e", 0xc000, 0x2000, 0x8ed559bd );
                ROM_LOAD( "yf-b-5j", 0xe000, 0x2000, 0x687142a1 );
                ROM_LOAD( "yf-b-5k", 0x10000,0x2000, 0x0143b99b );

                ROM_REGION(0x4000);	/* samples (ADPCM 4-bit) */
                ROM_LOAD( "yf-s-3a", 0x0000, 0x2000, 0xf4962ed6 );
                ROM_LOAD( "yf-s-3b", 0x2000, 0x2000, 0xbbb87550 );

                 ROM_END();
        }};


        public static GameDriver  yard_driver  =new GameDriver
        (
                "10 Yard Fight",
                "yard",
                "LEE TAYLOR\nJOHN CLEGG\nMIRKO BUFFONI\nNICOLA SALMORIA\nISHMAIR",
                machine_driver,

                yard_rom,
                null, null,
                null,

                input_ports,null, trak_ports, dsw, keys,

                color_prom, null, null,
                ORIENTATION_DEFAULT,

                null, null
        );    
}
