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
import static vidhrdw.mcr1.*;
import static sndhrdw.mcr.*;
import static mame.inptport.*;
import static sndhrdw._8910intf.*;
import static mame.memoryH.*;

public class mcr1 {
    
        static MemoryReadAddress mcr1_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x6fff, MRA_ROM ),
                new MemoryReadAddress( 0x7000, 0x7fff, MRA_RAM ),
                new MemoryReadAddress( 0xf000, 0xffff, MRA_RAM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };

        static MemoryWriteAddress mcr1_writemem[] =
        {
                new MemoryWriteAddress( 0x7000, 0x77ff, MWA_RAM ),
                new MemoryWriteAddress( 0x0000, 0x6fff, MWA_ROM ),
                new MemoryWriteAddress( 0xf000, 0xf1ff, MWA_RAM, spriteram, spriteram_size ),
                new MemoryWriteAddress( 0xfc00, 0xffff, videoram_w, videoram, videoram_size ),
                new MemoryWriteAddress( 0xf400, 0xf81f, mcr1_palette_w, mcr1_paletteram ),
                new MemoryWriteAddress( -1 ) /* end of table */
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


        static IOWritePort writeport[] =
        {
           new IOWritePort( 0, 0xFF, mcr_writeport ),
           new IOWritePort( -1 )	/* end of table */
        };



        static IOReadPort readport[] =
        {
           new IOReadPort( 0x00, 0x00, input_port_0_r ),
           new IOReadPort(  0x01, 0x01, input_port_1_r ),
           new IOReadPort(  0x02, 0x02, input_port_2_r ),
           new IOReadPort(  0x03, 0x03, input_port_3_r ),
           new IOReadPort(  0x04, 0x04, input_port_4_r ),
           new IOReadPort(  0x05, 0xff, mcr_readport ),
           new IOReadPort(  -1 )
        };

        static IOReadPort kick_readport[] =
        {
           new IOReadPort(  0x00, 0x00, input_port_0_r ),
           new IOReadPort(  0x01, 0x01, kick_trakball_r ),
           new IOReadPort(  0x02, 0x02, input_port_2_r ),
           new IOReadPort(  0x03, 0x03, input_port_3_r ),
           new IOReadPort(  0x04, 0x04, input_port_4_r ),
           new IOReadPort(  0x05, 0xff, mcr_readport ),
           new IOReadPort(  -1 )
        };


        static TrakPort kick_trak_ports[] =
        {
                new TrakPort(
                        X_AXIS,
                        1,
                        1.0,
                        kick_trakball_x
                ),
           new TrakPort( -1 )
        };

        static InputPortPtr kick_input_ports= new InputPortPtr(){ public void handler()  
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
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* IN3 -- dipswitches */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );

                PORT_START();	/* IN4 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );

                PORT_START();	/* AIN0 */
                PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNKNOWN );

                PORT_START();	/* dummy extra port for keyboard movement */
                PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_2WAY );
                PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_2WAY );
                PORT_BIT( 0xfc, IP_ACTIVE_HIGH, IPT_UNUSED );
                INPUT_PORTS_END();
        }};

        static InputPortPtr solarfox_input_ports= new InputPortPtr(){ public void handler()  
        {
                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
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
                PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNUSED );

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


        static  GfxLayout kick_charlayout = new GfxLayout
	(
                16, 16,
                256,	/* 256 characters */
                4,
                new int[]{ 256*16*8, 256*16*8+1, 0, 1 },	/* bit planes */
                new int[]{ 0, 0, 2, 2, 4, 4, 6, 6, 8, 8, 10, 10, 12, 12, 14, 14 },
                new int[]{ 0, 0, 2*8, 2*8, 4*8, 4*8, 6*8, 6*8, 8*8, 8*8, 10*8, 10*8, 12*8, 12*8, 14*8, 14*8 },
                16*8	/* every char takes 16 bytes */
        );

        static final int X1= (64*128*8);
        static final int Y1= (2*X1);
        static final int Z1= (3*X1);
        
        static GfxLayout kick_spritelayout = new GfxLayout
	(
           32,32,	/* 32x32 sprites */
           64,	        /* 128 sprites */
           4,	        /* 4 bit planes */
           new int[]{ 0, 1, 2, 3 },
           new int[]{  Z1+0, Z1+4, Y1+0, Y1+4, X1+0, X1+4, 0, 4, Z1+8, Z1+12, Y1+8, Y1+12, X1+8, X1+12, 8, 12,
              Z1+16, Z1+20, Y1+16, Y1+20, X1+16, X1+20, 16, 20, Z1+24, Z1+28, Y1+24, Y1+28,
              X1+24, X1+28, 24, 28 },
           new int[]{  0, 32, 32*2, 32*3, 32*4, 32*5, 32*6, 32*7, 32*8, 32*9, 32*10, 32*11,
              32*12, 32*13, 32*14, 32*15, 32*16, 32*17, 32*18, 32*19, 32*20, 32*21,
              32*22, 32*23, 32*24, 32*25, 32*26, 32*27, 32*28, 32*29, 32*30, 32*31 },
           128*8	/* bits per sprite per plane */
        );

        static GfxDecodeInfo kick_gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, kick_charlayout,   0, 2 ),
                new GfxDecodeInfo( 1, 0x2000, kick_spritelayout, 0, 2 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };

        static GfxLayout solarfox_charlayout= new GfxLayout
	(
                16, 16,
                256,	/* 256 characters */
                4,
                new int[]{ 256*16*8, 256*16*8+1, 0, 1 },	/* bit planes */
                new int[]{ 0, 0, 2, 2, 4, 4, 6, 6, 8, 8, 10, 10, 12, 12, 14, 14 },
                new int[]{ 0, 0, 2*8, 2*8, 4*8, 4*8, 6*8, 6*8, 8*8, 8*8, 10*8, 10*8, 12*8, 12*8, 14*8, 14*8 },
                16*8	/* every char takes 16 bytes */
        );

        static final int X2=(64*128*8);
        static final int Y2= (2*X2);
        static final int Z2=(3*X2);
        
        static GfxLayout solarfox_spritelayout = new GfxLayout
	(
           32,32,	/* 32x32 sprites */
           64,	        /* 128 sprites */
           4,	        /* 4 bit planes */
           new int[]{ 0, 1, 2, 3 },
           new int[]{  Z2+0, Z2+4, Y2+0, Y2+4, X2+0, X2+4, 0, 4, Z2+8, Z2+12, Y2+8, Y2+12, X2+8, X2+12, 8, 12,
              Z2+16, Z2+20, Y2+16, Y2+20, X2+16, X2+20, 16, 20, Z2+24, Z2+28, Y2+24, Y2+28,
              X2+24, X2+28, 24, 28 },
           new int[]{  0, 32, 32*2, 32*3, 32*4, 32*5, 32*6, 32*7, 32*8, 32*9, 32*10, 32*11,
              32*12, 32*13, 32*14, 32*15, 32*16, 32*17, 32*18, 32*19, 32*20, 32*21,
              32*22, 32*23, 32*24, 32*25, 32*26, 32*27, 32*28, 32*29, 32*30, 32*31 },
           128*8	/* bits per sprite per plane */
        );


        static GfxDecodeInfo solarfox_gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 1, 0x0000, solarfox_charlayout,   0, 2 ),
                new GfxDecodeInfo( 1, 0x2000, solarfox_spritelayout, 0, 2 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };

        public static MachineDriver kick_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                2500000,	/* 2.5 Mhz */
                                0,
                                mcr1_readmem,mcr1_writemem,kick_readport,writeport,
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
                32*16, 29*16, new rectangle( 0, 32*16-1, 0, 29*16-1 ),
                kick_gfxdecodeinfo,
                1+8*16, 8*16,
                mcr1_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
                null,
                generic_vh_start,
                generic_vh_stop,
                mcr1_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                mcr_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );


        public static MachineDriver solarfox_machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                2500000,	/* 2.5 Mhz */
                                0,
                                mcr1_readmem,mcr1_writemem,readport,writeport,
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
                32*16, 29*16, new rectangle( 0, 32*16-1, 0, 29*16-1 ),
                solarfox_gfxdecodeinfo,
                1+8*16, 8*16,
                solarfox_vh_convert_color_prom,

                VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
                null,
                generic_vh_start,
                generic_vh_stop,
                mcr1_vh_screenrefresh,

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

        static int mcr1_hiload (String name, int addr, int len)
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

        static void mcr1_hisave (String name, int addr, int len)
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
        static HiscoreLoadPtr kick_hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
            return mcr1_hiload (name, 0x7000, 0x91);
        }};
        static HiscoreSavePtr kick_hisave = new HiscoreSavePtr() { public void handler(String name)
	{
            mcr1_hisave (name, 0x7000, 0x91);
        }};
        static HiscoreLoadPtr solarfox_hiload = new HiscoreLoadPtr() { public int handler(String name)
	{
           char RAM[]=Machine.memory_region[0];

           /* see if it's okay to load */
           if (mcr_loadnvram !=0)
           {
              FILE f;

             f = fopen (name, "rb");
              if (f!=null)
              {
                                fread (RAM,0x7000, 1, 0x86, f);
                                fclose (f);
              }
              else
              {
                /* leaving RAM all-zero is not a happy thing for solarfox */
                char[] init = { 0,0,1,1,1,1,1,3,3,3,7,0,0,0,0,0 };
                memcpy (RAM,0x7000, init,0, sizeof (init)); //TODO : probably correct but who knows :P
              }
              return 1;
           }
           else return 0;	/* we can't load the hi scores yet */
        }};
        static HiscoreSavePtr solarfox_hisave= new HiscoreSavePtr() { public void handler(String name)
	{
               mcr1_hisave (name, 0x7000, 0x86); 
        }};


        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr kick_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "1200a-v2.b3",  0x0000, 0x1000, 0x65924917 );
                ROM_LOAD( "1300b-v2.b4",  0x1000, 0x1000, 0x27929f52 );
                ROM_LOAD( "1400c-v2.b5",  0x2000, 0x1000, 0x69107ce6 );
                ROM_LOAD( "1500d-v2.d4",  0x3000, 0x1000, 0x04a23aa1 );
                ROM_LOAD( "1600e-v2.d5",  0x4000, 0x1000, 0x1d2834c0 );
                ROM_LOAD( "1700f-v2.d6",  0x5000, 0x1000, 0xddf84ce1 );

                ROM_REGION(0x0a000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "1800g-v2.g4",  0x0000, 0x1000, 0xb4d120f3 );
                ROM_LOAD( "1900h-v2.g5",  0x1000, 0x1000, 0xc3ba4893 );
                ROM_LOAD( "2600a-v2.1e",  0x2000, 0x2000, 0x2c5d6b55 );
                ROM_LOAD( "2700b-v2.1d",  0x4000, 0x2000, 0x565ea97d );
                ROM_LOAD( "2800c-v2.1b",  0x6000, 0x2000, 0xf3be56a1 );
                ROM_LOAD( "2900d-v2.1a",  0x8000, 0x2000, 0x77da795e );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "4200-a.a7",    0x0000, 0x1000, 0x9e35c02e );
                ROM_LOAD( "4300-b.a8",    0x1000, 0x1000, 0xca2b7c28 );
                ROM_LOAD( "4400-c.a9",    0x2000, 0x1000, 0xd1901551 );
                ROM_LOAD( "4500-d.a10",   0x3000, 0x1000, 0xd36ddcdc );
                ROM_END();
        }};
        static RomLoadPtr solarfox_rom= new RomLoadPtr(){ public void handler()  
        {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "sfcpu.3b", 0x0000, 0x1000, 0x1b63bbb9 );
                ROM_LOAD( "sfcpu.4b", 0x1000, 0x1000, 0x9f1f7dc7 );
                ROM_LOAD( "sfcpu.5b", 0x2000, 0x1000, 0x4b7d8bc1 );
                ROM_LOAD( "sfcpu.4d", 0x3000, 0x1000, 0xae2ca80e );
                ROM_LOAD( "sfcpu.5d", 0x4000, 0x1000, 0x426dcdab );
                ROM_LOAD( "sfcpu.6d", 0x5000, 0x1000, 0x508903cd );
                ROM_LOAD( "sfcpu.7d", 0x6000, 0x1000, 0x51468c4e );

                ROM_REGION(0x0a000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "sfcpu.4g", 0x0000, 0x1000, 0x44e0e0c0 );
                ROM_LOAD( "sfcpu.5g", 0x1000, 0x1000, 0xb69f2685 );
                ROM_LOAD( "sfvid.1a", 0x8000, 0x2000, 0x1e42fb6a );
                ROM_LOAD( "sfvid.1b", 0x6000, 0x2000, 0xb92428f4 );
                ROM_LOAD( "sfvid.1d", 0x4000, 0x2000, 0x6eeff5c5 );
                ROM_LOAD( "sfvid.1e", 0x2000, 0x2000, 0x803cf0ae );

                ROM_REGION(0x10000);	/* 64k for the audio CPU */
                ROM_LOAD( "sfsnd.7a", 0x0000, 0x1000, 0xc3945494 );
                ROM_LOAD( "sfsnd.8a", 0x1000, 0x1000, 0xd43589ef );
                ROM_LOAD( "sfsnd.9a", 0x2000, 0x1000, 0x9f5bd101 );

                ROM_END();
        }};

        public static GameDriver kick_driver = new GameDriver
	(
                "Kick",
                "kick",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA\nBRAD OLIVER\nJOHN BUTLER",
                kick_machine_driver,

                kick_rom,
                null, null,
                null,

                null/*TBR*/, kick_input_ports, kick_trak_ports, null/*TBR*/, null/*TBR*/,

                null, null,null,
                ORIENTATION_SWAP_XY,

                kick_hiload,kick_hisave
        );

        public static GameDriver solarfox_driver = new GameDriver
	(
                "Solar Fox",
                "solarfox",
                "CHRISTOPHER KIRMSE\nAARON GILES\nNICOLA SALMORIA\nBRAD OLIVER",
                solarfox_machine_driver,

                solarfox_rom,
                null, null,
                null,

                null/*TBR*/, solarfox_input_ports, null/*TBR*/, null/*TBR*/, null/*TBR*/,

                null, null,null,
                ORIENTATION_SWAP_XY,

                solarfox_hiload,solarfox_hisave
        );    
}
