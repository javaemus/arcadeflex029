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
 *
 *  roms are from v0.36 romset
 *
 */


package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.mame.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static mame.inptport.*;
import static vidhrdw.generic.*;
import static vidhrdw.bankp.*;
import static sndhrdw.bankp.*;

public class bankp {
    
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0xdfff, MRA_ROM ),
                new MemoryReadAddress( 0xe000, 0xe7ff, MRA_RAM ),
                new MemoryReadAddress( 0xf000, 0xffff, MRA_RAM ),
                new MemoryReadAddress( -1 )	/* end of table */
        };

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0xdfff, MWA_ROM ),
                new MemoryWriteAddress( 0xe000, 0xe7ff, MWA_RAM ),
                new MemoryWriteAddress( 0xf000, 0xf3ff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0xf400, 0xf7ff, colorram_w, colorram ),
                new MemoryWriteAddress( 0xf800, 0xfbff, bankp_videoram2_w, bankp_videoram2 ),
                new MemoryWriteAddress( 0xfc00, 0xffff, bankp_colorram2_w, bankp_colorram2 ),
                new MemoryWriteAddress( -1 )	/* end of table */
        };


        static IOReadPort readport[] =
        {
                new IOReadPort( 0x00, 0x00, input_port_0_r ),	/* IN0 */
                new IOReadPort( 0x01, 0x01, input_port_1_r ),	/* IN1 */
                new IOReadPort( 0x02, 0x02, input_port_2_r ),	/* IN2 */
                new IOReadPort( 0x04, 0x04, input_port_3_r ),	/* DSW */
                new IOReadPort( -1 )	/* end of table */
        };

        static IOWritePort writeport[] =
        {
                new IOWritePort( 0x00, 0x00, bankp_sound1_w ),
                new IOWritePort( 0x01, 0x01, bankp_sound2_w ),
                new IOWritePort( 0x02, 0x02, bankp_sound3_w ),
                new IOWritePort( 0x05, 0x05, bankp_scroll_w ),
                new IOWritePort( 0x07, 0x07, bankp_out_w ),
                new IOWritePort( -1 )	/* end of table */
        };


        static InputPortPtr input_ports= new InputPortPtr(){ public void handler() 
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* probably unused */
                PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
                PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* probably unused */
                PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY );
                PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
                PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_COIN1 );
                PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_COIN2 );
                PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON2 );

                PORT_START();	/* IN1 */
                PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_UNKNOWN );/* probably unused */
                PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY | IPF_COCKTAIL );
                PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* probably unused */
                PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY | IPF_COCKTAIL );
                PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
                PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_START1 );
                PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_START2 );
                PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_COCKTAIL );

                PORT_START();	/* IN2 */
                PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON3 );
                PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON3 | IPF_COCKTAIL );
                PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_COIN3 );
                PORT_BIT( 0xf8, IP_ACTIVE_HIGH, IPT_UNKNOWN );	/* probably unused */

                PORT_START();	/* DSW */
                PORT_DIPNAME( 0x03, 0x00, "Coin A/B", IP_KEY_NONE );
                PORT_DIPSETTING(    0x03, "3 Coins/1 Credit" );
                PORT_DIPSETTING(    0x02, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x00, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x01, "1 Coin/2 Credits" );
                PORT_DIPNAME( 0x04, 0x00, "Coin C", IP_KEY_NONE );
                PORT_DIPSETTING(    0x04, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x00, "1 Coin/1 Credit" );
                PORT_DIPNAME( 0x08, 0x00, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "3" );
                PORT_DIPSETTING(    0x08, "4" );
                PORT_DIPNAME( 0x10, 0x00, "Bonus Life", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "70K 200K 500K ..." );
                PORT_DIPSETTING(    0x10, "100K 400K 800K ..." );
                PORT_DIPNAME( 0x20, 0x00, "Difficulty", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "Easy" );
                PORT_DIPSETTING(    0x20, "Hard" );
                PORT_DIPNAME( 0x40, 0x40, "Demo Sounds", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "Off" );
                PORT_DIPSETTING(    0x40, "On" );
                PORT_DIPNAME( 0x80, 0x80, "Cabinet", IP_KEY_NONE );
                PORT_DIPSETTING(    0x80, "Upright" );
                PORT_DIPSETTING(    0x00, "Cocktail" );
                INPUT_PORTS_END();
        }};



        static GfxLayout charlayout = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                1024,	/* 1024 characters */
                2,	/* 2 bits per pixel */
                new int[]{ 0, 4 },	/* the bitplanes are packed in one byte */
                new int[]{ 8*8+3, 8*8+2, 8*8+1, 8*8+0, 3, 2, 1, 0 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                16*8	/* every char takes 8 consecutive bytes */
        );
        static GfxLayout charlayout2 = new GfxLayout
	(
                8,8,	/* 8*8 characters */
                2048,	/* 2048 characters */
                3,	/* 3 bits per pixel */
                new int[]{ 0, 2048*8*8, 2*2048*8*8 },	/* the bitplanes are separated */
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },
                new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
                8*8	/* every char takes 8 consecutive bytes */
        );

        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, charlayout,  0, 32 ),
                new GfxDecodeInfo( 1, 0x4000, charlayout2,  32*4, 16 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        static char color_prom[] =
        {
                /* pr6177 - palette */
                0x00,0x1C,0x14,0x0A,0x1F,0x9B,0x03,0x2F,0xC0,0x30,0x18,0x07,0x3F,0x9F,0x01,0xFF,
                0x00,0x1C,0x14,0x0A,0x1F,0x9B,0x03,0x40,0xC0,0x30,0x18,0x07,0x3F,0x9F,0x01,0xFF,
                /* pr6178 - charset #1 lookup table */
                0x01,0x02,0x04,0x03,0x05,0x0E,0x01,0x02,0x0E,0x05,0x02,0x04,0x03,0x02,0x05,0x0E,
                0x06,0x04,0x0E,0x02,0x00,0x03,0x05,0x0E,0x0D,0x02,0x03,0x0E,0x0D,0x0F,0x0E,0x04,
                0x0D,0x0F,0x0E,0x03,0x02,0x0E,0x0F,0x0D,0x08,0x02,0x0E,0x0F,0x04,0x03,0x02,0x0F,
                0x00,0x0D,0x0F,0x0E,0x01,0x0E,0x05,0x0F,0x0F,0x05,0x0E,0x04,0x00,0x0E,0x0F,0x05,
                0x00,0x0C,0x0B,0x0F,0x0E,0x0F,0x05,0x02,0x03,0x02,0x04,0x05,0x0B,0x04,0x03,0x02,
                0x00,0x0E,0x02,0x03,0x03,0x02,0x04,0x0E,0x01,0x0B,0x0E,0x06,0x0F,0x08,0x06,0x0E,
                0x08,0x0F,0x0E,0x01,0x05,0x0D,0x0E,0x01,0x05,0x08,0x0E,0x01,0x0E,0x0D,0x0F,0x08,
                0x05,0x0E,0x0D,0x0F,0x03,0x0F,0x0E,0x05,0x08,0x0E,0x03,0x02,0x0D,0x05,0x0E,0x03,
                0x03,0x09,0x01,0x06,0x07,0x07,0x09,0x0C,0x07,0x0A,0x02,0x03,0x03,0x0D,0x06,0x06,
                0x0B,0x0D,0x0E,0x00,0x0B,0x01,0x03,0x06,0x0A,0x0D,0x04,0x08,0x0E,0x00,0x06,0x0D,
                0x06,0x0D,0x0D,0x04,0x09,0x0D,0x05,0x06,0x05,0x0A,0x0A,0x05,0x0D,0x03,0x0D,0x0F,
                0x0F,0x0C,0x00,0x01,0x0A,0x0A,0x02,0x06,0x04,0x0E,0x06,0x0E,0x09,0x0F,0x0C,0x05,
                0x06,0x07,0x0A,0x00,0x0B,0x0A,0x09,0x07,0x05,0x08,0x09,0x0C,0x0E,0x0B,0x0D,0x03,
                0x06,0x04,0x06,0x0B,0x06,0x0F,0x0C,0x0F,0x0B,0x00,0x07,0x08,0x0E,0x06,0x01,0x00,
                0x01,0x07,0x06,0x0F,0x00,0x03,0x0F,0x05,0x0C,0x09,0x07,0x06,0x09,0x0F,0x02,0x05,
                0x05,0x01,0x03,0x0B,0x02,0x0C,0x02,0x06,0x0D,0x0C,0x0C,0x00,0x0A,0x01,0x05,0x05,
                /* pr6179 - charset #2 lookup table */
                0x0A,0x0E,0x04,0x02,0x0C,0x0F,0x08,0x03,0x00,0x04,0x0E,0x0A,0x0F,0x0B,0x0C,0x01,
                0x0A,0x0E,0x03,0x02,0x0B,0x0F,0x08,0x04,0x05,0x0E,0x04,0x02,0x0F,0x0C,0x01,0x03,
                0x09,0x04,0x0E,0x0A,0x0B,0x02,0x0F,0x01,0x04,0x01,0x03,0x02,0x0B,0x05,0x0E,0x0F,
                0x08,0x0D,0x01,0x0F,0x0E,0x05,0x02,0x0B,0x06,0x01,0x0D,0x08,0x0E,0x02,0x0F,0x05,
                0x07,0x0E,0x05,0x0F,0x0D,0x02,0x08,0x0B,0x07,0x0E,0x05,0x0F,0x0D,0x02,0x04,0x08,
                0x07,0x0E,0x05,0x0F,0x0D,0x0E,0x0B,0x04,0x07,0x0E,0x05,0x02,0x08,0x0F,0x04,0x03,
                0x07,0x0E,0x05,0x0F,0x0D,0x0E,0x08,0x09,0x00,0x0E,0x05,0x0F,0x0D,0x02,0x0B,0x08,
                0x07,0x0E,0x05,0x0F,0x0D,0x02,0x04,0x08,0x07,0x0E,0x05,0x0F,0x0D,0x0E,0x08,0x09,
                0x07,0x01,0x07,0x04,0x06,0x07,0x09,0x0C,0x06,0x02,0x00,0x04,0x03,0x0D,0x06,0x06,
                0x0B,0x0D,0x0E,0x00,0x0B,0x03,0x05,0x06,0x0A,0x0D,0x05,0x08,0x0D,0x00,0x07,0x0D,
                0x04,0x0D,0x0C,0x04,0x0B,0x0F,0x05,0x07,0x04,0x0B,0x08,0x05,0x0C,0x03,0x0D,0x0D,
                0x0F,0x0E,0x01,0x00,0x0A,0x0A,0x01,0x07,0x06,0x0E,0x06,0x0F,0x0A,0x0F,0x0D,0x05,
                0x06,0x03,0x0A,0x06,0x0B,0x0F,0x09,0x06,0x05,0x0A,0x0B,0x09,0x0E,0x0B,0x0D,0x01,
                0x04,0x00,0x04,0x0B,0x05,0x0F,0x0D,0x0E,0x0B,0x01,0x07,0x09,0x0E,0x06,0x00,0x01,
                0x03,0x06,0x06,0x0F,0x04,0x02,0x0F,0x05,0x0C,0x09,0x07,0x06,0x0F,0x0F,0x02,0x05,
                0x04,0x00,0x02,0x0A,0x03,0x0D,0x02,0x06,0x0E,0x0D,0x0C,0x01,0x0A,0x01,0x05,0x04
        };



        static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                3867120,	/* ?? the main oscillator is 15.46848 Mhz */
                                0,
                                readmem,writemem,readport,writeport,
                                nmi_interrupt,1
                        ),
                },
                60,
                null,

                /* video hardware */
                32*8, 32*8, new rectangle(3*8, 31*8-1, 2*8, 30*8-1 ),
                gfxdecodeinfo,
                32, 32*4+16*8,
                bankp_vh_convert_color_prom,

                VIDEO_TYPE_RASTER,
                null,
                bankp_vh_start,
                bankp_vh_stop,
                bankp_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                bankp_sh_start,
                bankp_sh_stop,
                bankp_sh_update
        );



        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr bankp_rom = new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "epr6175.bin", 0x0000, 0x4000, 0xb50160d9 );
                ROM_LOAD( "epr6174.bin", 0x4000, 0x4000, 0x30064be0 );
                ROM_LOAD( "epr6173.bin", 0x8000, 0x4000, 0x73d2b6a2 );
                ROM_LOAD( "epr6176.bin", 0xc000, 0x2000, 0xbef2c314 );

                ROM_REGION(0x10000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "epr6165.bin", 0x0000, 0x2000, 0xb90527b7 );	/* playfield #1 chars */
                ROM_LOAD( "epr6166.bin", 0x2000, 0x2000, 0x4868650a );
                ROM_LOAD( "epr6172.bin", 0x4000, 0x2000, 0xc951eaf9 );	/* playfield #2 chars */
                ROM_LOAD( "epr6171.bin", 0x6000, 0x2000, 0xe9d98839 );
                ROM_LOAD( "epr6170.bin", 0x8000, 0x2000, 0x1ec6c8fe );
                ROM_LOAD( "epr6169.bin", 0xa000, 0x2000, 0xb32c9638 );
                ROM_LOAD( "epr6168.bin", 0xc000, 0x2000, 0x6343f68b );
                ROM_LOAD( "epr6167.bin", 0xe000, 0x2000, 0xb5f4080e );
                ROM_END();
        }};



        static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name)
        {
                /* check if the hi score table has already been initialized */
                /* this check is really stupid and almost useless, but better than nothing */
                if (memcmp(RAM,0xe590,new char[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00},7) == 0 &&
                                memcmp(RAM,0xe620,new char[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00},7) == 0 &&
                                memcmp(RAM,0xe018,new char[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00},7) == 0)	/* high score */
                {
                        FILE f;


                        if ((f = fopen(name,"rb")) != null)
                        {
                                fread(RAM,0xe590,1,16*10,f);
                                memcpy(RAM,0xe018,RAM,0xe590,7);	/* copy high score */
                                fclose(f);
                        }

                        return 1;
                }
                else return 0;	/* we can't load the hi scores yet */
        }};



        static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name)
        {
                FILE f;


                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0xe590,1,16*10,f);
                        fclose(f);
                }
        }};



        public static GameDriver bankp_driver = new GameDriver
        (
                "Bank Panic",
                "bankp",
                "Nicola Salmoria (MAME driver)\nAlan J. McCormick (color info)",
                machine_driver,

                bankp_rom,
                null, null,
                null,

                null/*TBR*/,input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                color_prom, null, null,
                ORIENTATION_DEFAULT,

                hiload, hisave
        );
    
}
