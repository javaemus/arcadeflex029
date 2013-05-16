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
 *  roms are from 0.36 romset
 *  
 */

package drivers;

import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.osdependH.*;
import static mame.inptport.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.kangaroo.*;
import static sndhrdw.generic.*;
import static vidhrdw.generic.*;
import static vidhrdw.kangaroo.*;
import static machine.kangaroo.*;
import static mame.mame.*;
import static Z80.Z80.*;
import static Z80.Z80H.*;
import static arcadeflex.libc.*;
import static mame.memoryH.*;


public class kangaroo {
        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x5fff, MRA_ROM ),
                new MemoryReadAddress( 0x8000, 0xbfff, MRA_RAM ),
                new MemoryReadAddress( 0xee00, 0xee00, input_port_3_r ),
                new MemoryReadAddress( 0xe400, 0xe400, input_port_7_r ),
                new MemoryReadAddress( 0xe000, 0xe3ff, MRA_RAM ),
                new MemoryReadAddress( 0xec00, 0xec00, input_port_0_r ),
                new MemoryReadAddress( 0xed00, 0xed00, input_port_1_r ),
                new MemoryReadAddress( 0xef00, 0xef00, kangaroo_sec_chip_r ),
                new MemoryReadAddress( 0xe800, 0xe80a, MRA_RAM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };
        static MemoryReadAddress sh_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x0fff, MRA_ROM ),
                new MemoryReadAddress( 0x4000, 0x43ff, MRA_RAM ),
                new MemoryReadAddress( 0x6000, 0x6000, sound_command_latch_r ),
                new MemoryReadAddress( -1 )
        };
        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x8000, 0xbfff, kangaroo_videoramw, videoram ),
                new MemoryWriteAddress( 0xe000, 0xe3ff, MWA_RAM ),
                new MemoryWriteAddress( 0xe800, 0xe80a, kangaroo_spriteramw, spriteram ),
                new MemoryWriteAddress( 0xef00, 0xefff, kangaroo_sec_chip_w ),
                new MemoryWriteAddress( 0xec00, 0xec00, sound_command_w ),
                new MemoryWriteAddress( 0xed00, 0xed00, MWA_NOP ),
                new MemoryWriteAddress( 0x0000, 0x5fff, MWA_ROM ),
                new MemoryWriteAddress( -1 )  /* end of table */
        };
        static MemoryWriteAddress sh_writemem[] =
        {
                new MemoryWriteAddress( 0x4000, 0x43ff, MWA_RAM ),
                new MemoryWriteAddress( 0x0000, 0x0fff, MWA_ROM ),
                new MemoryWriteAddress( 0x6000, 0x6000, MWA_ROM ),
                new MemoryWriteAddress( -1 )
        };

      public static WriteHandlerPtr kanga_moja = new WriteHandlerPtr()
      {
        public void handler(int port, int val)
        {        
            /* does this really work, or is there a problem with the PSG code,
           it seems like one channel is completely missing from the output
           */
          Z80_Regs regs = new Z80_Regs();
          Z80_GetRegs(regs);

          if (regs.BC.W == 0x8000)
            AY8910_control_port_0_w.handler(port, val);
          else
            AY8910_write_port_0_w.handler(port, val);
        }
      };

      public static ReadHandlerPtr kanga_rdport = new ReadHandlerPtr()
      {
        public int handler(int port) {
            /* this is actually not used*/
          return AY8910_read_port_0_r.handler(port);
        }
      };

        static IOWritePort sh_writeport[] =
        {
                new IOWritePort( 0x00, 0x00, kanga_moja ),
                new IOWritePort( -1 )	/* end of table */
        };
        static IOReadPort sh_readport[] =
        {
                new IOReadPort( 0x00, 0x00, kanga_rdport ),
                new IOReadPort( -1 )
        };


        static InputPort k_input_ports[] =
        {
                new InputPort(	/* IN0 */
                        0x20,
                        new int[]{ OSD_KEY_4, OSD_KEY_1, OSD_KEY_2, OSD_KEY_3, 0, 0, 0, 0 }
                ),
		new InputPort(	/* IN1 */
                        0x00,
                        new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_UP, OSD_KEY_DOWN,
                                        OSD_KEY_CONTROL, 0, 0, 0 }
                ),
		new InputPort(	/* IN2 */
                        0x00,
                        new int[]{ 0 , 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* IN3 */
                        0x00,
                        new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_UP, OSD_KEY_DOWN,
                                        OSD_KEY_CONTROL, 0, 0, 0 }
                ),
		new InputPort(	/* IN4 */
                        0x00,
                        new int[]{ 0 , 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* IN5 */
                        0x0f,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* IN6 */
                        0x00,
                        new int[]{ OSD_KEY_3, OSD_KEY_4, OSD_KEY_F1, 0, 0, 0, 0, 0 }
                ),
		new InputPort(	/* DSW1 */
                        0x00,
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
		new InputPort( -1 )  /* end of table */
        };

        static  TrakPort trak_ports[] =
        {
                 new TrakPort(-1)
        };


        static KEYSet keys[] =
        {
                new KEYSet( 1, 2, "PL1 MOVE UP" ),
                new KEYSet( 1, 1, "PL1 MOVE LEFT"  ),
                new KEYSet( 1, 0, "PL1 MOVE RIGHT" ),
                new KEYSet( 1, 3, "PL1 MOVE DOWN" ),
                new KEYSet( 1, 4, "PL1 FIRE" ),
                new KEYSet( 3, 2, "PL2 MOVE UP" ),
                new KEYSet( 3, 1, "PL2 MOVE LEFT"  ),
                new KEYSet( 3, 0, "PL2 MOVE RIGHT" ),
                new KEYSet( 3, 3, "PL2 MOVE DOWN" ),
                new KEYSet( 3, 4, "PL2 FIRE" ),
                new KEYSet( -1 )
        };


        static DSW dsw[] =
        {
                new DSW( 7, 0x01, "LIVES", new String[]{ "03", "05" } ),
                new DSW( 7, 0x02, "DIFFICULTY", new String[]{ "EASY", "HARD" } ),
                new DSW( 7, 0x0c, "BONUS", new String[]{ "NO BONUS", "AT 10 000", "AT 10 AND EVERY 30 000", "AT 20 AND EVERY 40 000" } ),
                new DSW( 0, 0x20, "MUSIC", new String[]{ "ON ", "OFF" } ), /* 970617 -V- */
                new DSW( 7, 0xf0, "COIN SELECT 1", new String[]{ "1/1 1/1", "2/1 2/1", "2/1 1/3", "1/1 1/2","1/1 1/3", "1/1 1/4", "1/1 1/5", "1/1 1/6","1/2 1/2", "1/2 1/4", "1/2 1/5", "1/2 1/10","1/2 1/11", "1/2 1/12", "1/2 1/6", "FREE FREE" } ),
               /* { 7, 0xc0, "COIN SELECT 2", { "SET A", "SET B", "SET C", "SET D" } },*/
                new DSW( -1 )
        };



        static GfxLayout sprlayout = new GfxLayout
	(
                1,4,    /* 4 * 1 lines */
                8192,   /* 8192 "characters" */
                4,      /* 4 bpp        */
                new int[]{ 0x2000*8, 0x2000*8+4, 0, 4  },       /* 4 and 8192 bytes between planes */
                new int[]{ 0,1,2,3 },
                new int[]{ 0,1,2,3 },
                1*8
        );



        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 2, 0 , sprlayout, 0, 1 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };



        static char palette[] =
        {
                0x00,0x00,0x00,
                0x00,0x00,42*4,
                0x00,42*4,0x00,
                0x00,42*4,42*4,
                42*4,00,00,
                42*4,00,42*4,
                42*4,21*4,00,
                42*4,42*4,42*4,
                21*4,21*4,21*4,
                21*4,21*4,63*4,
                21*4,63*4,21*4,
                21*4,63*4,63*4,
                63*4,21*4,21*4,
                63*4,21*4,63*4,
                63*4,63*4,21*4,
                63*4,63*4,63*4
        };


        static char colortable[] =
        {
                0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15
        };

        static HiscoreLoadPtr kangaroo_hiload = new HiscoreLoadPtr()
        {
          public int handler()
          {
                /* Ok, we need to explicitly tell what RAM to read...*/
                /* realizing this was necessary took me quite a long time :( -V- */

                char []RAM = Machine.memory_region[0];

                /* just a guess really... */
   /*TOFIX             if ( RAM[0xe1da] == 0x50 )
                {
                        FILE f;
                        if (( f = fopen(name, "rb")) != null)
                        {
                                fread(RAM,0xe1a0, 1, 0x40, f);
                                /* is this enough ??? */
   /*TOFIX                             fclose(f);
                        }
                        return 1;
                 }
                 else */return 0; /* didn't load them yet, do stop by later ;-) -V- */
        }};
        static HiscoreSavePtr kangaroo_hisave = new HiscoreSavePtr()
        {
           public void handler()
           {
                FILE f;

     /*TOFIX           char []RAM = Machine.memory_region[0];

                if ((f = fopen(name , "wb")) != null)
                {
                        fwrite(RAM,0xe1a0, 1, 0x40, f);
                        fclose(f);
                }*/
        }};



        static  MachineDriver machine_driver = new MachineDriver
	(
                /* basic machine hardware */
                new MachineCPU[] {
			new MachineCPU(
                                CPU_Z80,
                                3000000,        /* 3 Mhz */
                                0,
                                readmem,writemem,null,null,
                                interrupt,1
                        ),
                        new MachineCPU(
                                CPU_Z80 | CPU_AUDIO_CPU,
                                2500000,
                                3,
                                sh_readmem,sh_writemem,sh_readport,sh_writeport,
                                kangaroo_interrupt,1
                        )
                },
                60,
                null,

                /* video hardware */
                32*8, 32*8, new rectangle( 0x0b, 0xfa, 0, 32*8-1 ),
                gfxdecodeinfo,
                sizeof(palette)/3,sizeof(colortable),
                null,
                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
                null,
                kangaroo_vh_start,
                kangaroo_vh_stop,
                kangaroo_vh_screenrefresh,

                /* sound hardware */
                null,
                null,
                kangaroo_sh_start,
                AY8910_sh_stop,
                AY8910_sh_update
        );


        /***************************************************************************

          Game driver(s)

        ***************************************************************************/
        static RomLoadPtr kangaroo_rom= new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);	/* 64k for code */             
                ROM_LOAD( "tvg_75.0",    0x0000,  0x1000, 0x0d18c581 );
                ROM_LOAD( "tvg_76.1",    0x1000,  0x1000, 0x5978d37a );
                ROM_LOAD( "tvg_77.2",    0x2000,  0x1000, 0x522d1097 );
                ROM_LOAD( "tvg_78.3",    0x3000,  0x1000, 0x063da970 );
                ROM_LOAD( "tvg_79.4",    0x4000,  0x1000, 0x9e5cf8ca );
                ROM_LOAD( "tvg_80.5",    0x5000,  0x1000, 0x2fc18049 );

                ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "tvg_83.v0", 0x0000, 0x1000, 0xc0446ca6 );	/* not needed - could be removed */

                ROM_REGION(0x10000); /* space for graphics roms */
                ROM_LOAD( "tvg_84.v1", 0x0000, 0x1000, 0xe4cb26c2);  /* because of very rare way */
                ROM_LOAD( "tvg_86.v3", 0x1000, 0x1000, 0x9e6a599f);  /* CRT controller uses these roms */
                ROM_LOAD( "tvg_83.v0", 0x2000, 0x1000, 0xc0446ca6 );  /* there's no way, but to decode */
                ROM_LOAD( "tvg_85.v2", 0x3000, 0x1000, 0xc0446ca6 );  /* it at runtime - which is SLOW */
                
                ROM_REGION(0x10000); /* sound */
                ROM_LOAD( "tvg_81.8",    0x0000,  0x1000, 0xfb449bfd );
                ROM_END();
        }};


         public static GameDriver kangaroo_driver= new GameDriver
          (

                "Kangaroo",
                "kangaroo",
                "VILLE LAITINEN",
                machine_driver,

                kangaroo_rom,
                null, null,
                null,

                k_input_ports,null, trak_ports, dsw, keys,

                null, palette, colortable,
                ORIENTATION_DEFAULT,

                kangaroo_hiload, kangaroo_hisave
        );
}
