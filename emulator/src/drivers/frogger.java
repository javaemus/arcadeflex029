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
 *  roms are from v0.36 romset
 *  frogsega is frogseg2 in v0.36 romset
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
import static sndhrdw.generic.*;
import static sndhrdw.frogger.*;
import static vidhrdw.generic.*;
import static vidhrdw.frogger.*;

public class frogger
{



	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new MemoryReadAddress( 0xa800, 0xabff, MRA_RAM ),	/* video RAM */
		new MemoryReadAddress( 0xb000, 0xb05f, MRA_RAM ),	/* screen attributes, sprites */
		new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new MemoryReadAddress( 0x8800, 0x8800, MRA_NOP ),
		new MemoryReadAddress( 0xe000, 0xe000, input_port_0_r ),	/* IN0 */
		new MemoryReadAddress( 0xe002, 0xe002, input_port_1_r ),	/* IN1 */
		new MemoryReadAddress( 0xe004, 0xe004, input_port_2_r ),	/* IN2 */
		new MemoryReadAddress( -1 )	/* end of table */
	};

	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new MemoryWriteAddress( 0xa800, 0xabff, videoram_w, videoram, videoram_size ),
		new MemoryWriteAddress( 0xb000, 0xb03f, frogger_attributes_w, frogger_attributesram ),
		new MemoryWriteAddress( 0xb040, 0xb05f, MWA_RAM, spriteram, spriteram_size ),
		new MemoryWriteAddress( 0xb808, 0xb808, interrupt_enable_w ),
		new MemoryWriteAddress( 0xd000, 0xd000, sound_command_w ),
		new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};



	static MemoryReadAddress sound_readmem[] =
	{
		new MemoryReadAddress( 0x4000, 0x43ff, MRA_RAM ),
		new MemoryReadAddress( 0x0000, 0x17ff, MRA_ROM ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress sound_writemem[] =
	{
		new MemoryWriteAddress( 0x4000, 0x43ff, MWA_RAM ),
		new MemoryWriteAddress( 0x0000, 0x17ff, MWA_ROM ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	
	
	static IOReadPort sound_readport[] =
	{
		new IOReadPort( 0x40, 0x40, AY8910_read_port_0_r ),
		new IOReadPort( -1 )	/* end of table */
	};
	
	static IOWritePort sound_writeport[] =
	{
		new IOWritePort( 0x80, 0x80, AY8910_control_port_0_w ),
		new IOWritePort( 0x40, 0x40, AY8910_write_port_0_w ),
		new IOWritePort( -1 )	/* end of table */
	};

        static TrakPort[] trak_ports =
        {
           new TrakPort(-1)
        };


      static KEYSet[] keys =
      {
        new KEYSet( 2, 4, "MOVE UP" ),
        new KEYSet( 0, 5, "MOVE LEFT"   ),
        new KEYSet( 0, 4, "MOVE RIGHT" ),
        new KEYSet( 2, 6, "MOVE DOWN" ),
        new KEYSet(-1) };

	static InputPort input_ports[] =
	{
		new InputPort(	/* IN0 */
			0xff,
			new int[] { 0, 0, OSD_KEY_3, 0, OSD_KEY_RIGHT, OSD_KEY_LEFT, 0, 0 }
		),
		new InputPort(	/* IN1 */
			0xfc,
			new int[] { 0, 0, 0, 0, 0, 0, OSD_KEY_2, OSD_KEY_1 }
		),
		new InputPort(	/* IN2 */
			0xf7,
			new int[] { 0, 0, 0, 0, OSD_KEY_UP, 0, OSD_KEY_DOWN, 0 }
		),
		new InputPort( -1 )	/* end of table */
	};



	static DSW dsw[] =
	{
		new DSW( 1, 0x03, "LIVES", new String[] { "3", "5", "7", "256" } ),
		new DSW( -1 )
	};



	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		256,	/* 256 characters */
		2,	/* 2 bits per pixel */
		new int[] { 0, 256*8*8 },	/* the two bitplanes are separated */
		new int[] { 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		64,	/* 64 sprites */
		2,	/* 2 bits per pixel */
		new int[] { 0, 64*16*16 },	/* the two bitplanes are separated */
		new int[] { 23*8, 22*8, 21*8, 20*8, 19*8, 18*8, 17*8, 16*8,
				7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		32*8	/* every sprite takes 32 consecutive bytes */
	);



	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,     0, 16 ),
		new GfxDecodeInfo( 1, 0x0000, spritelayout,   0, 8 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};



	static final int BLACK = 0x00;
	static final int LTGREEN = 0x3c;
	static final int DKRED = 0x17;
	static final int DKBROWN = 0x5c;
	static final int DKPINK = 0xd7;
	static final int LTBROWN = 0x5e;
	static final int PURPLE = 0xc4;
	static final int BLUE = 0xc0;
	static final int RED = 0x07;
	static final int MAGENTA = 0xc7;
	static final int GREEN = 0x39;
	static final int CYAN = 0xf8;
	static final int YELLOW = 0x3f;
	static final int WHITE = 0xf6;

	static char color_prom[] =
	{
		/* palette */
		BLACK,BLUE,RED,WHITE,
		BLACK,DKRED,GREEN,CYAN,
		BLACK,LTBROWN,WHITE,DKBROWN,
		BLACK,MAGENTA,GREEN,YELLOW,
		BLACK,LTGREEN,CYAN,DKPINK,
		BLACK,RED,WHITE,GREEN,
		BLACK,PURPLE,BLUE,RED,
		BLACK,RED,YELLOW,PURPLE
	};



	public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				3072000,	/* 3.072 Mhz */
				0,
				readmem, writemem, null, null,
				nmi_interrupt, 1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				2000000,	/* 2 Mhz?????? */
				2,	/* memory region #2 */
				sound_readmem, sound_writemem, sound_readport, sound_writeport,
				frogger_sh_interrupt,10
			)
		},
		60,
		null,

		/* video hardware */
		32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
		gfxdecodeinfo,
		32, 64,
		frogger_vh_convert_color_prom,
                VIDEO_TYPE_RASTER,
		null,
		generic_vh_start,
		generic_vh_stop,
		frogger_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		frogger_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);
	
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	static RomLoadPtr  frogger_rom= new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "frogger.26",   0x0000, 0x1000, 0x597696d6 );
                ROM_LOAD( "frogger.27",   0x1000, 0x1000, 0xb6e6fcc3 );
                ROM_LOAD( "frsm3.7",      0x2000, 0x1000, 0xaca22ae0 );
	
		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "frogger.606",  0x0000, 0x0800, 0xf524ee30 );
                ROM_LOAD( "frogger.607",  0x0800, 0x0800, 0x05f7d883 );

		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		ROM_LOAD( "frogger.608",  0x0000, 0x0800, 0xe8ab0256 );
                ROM_LOAD( "frogger.609",  0x0800, 0x0800, 0x7380a48f );
                ROM_LOAD( "frogger.610",  0x1000, 0x0800, 0x31d7eb27 );
                     
                ROM_END();
        }};
	

	
        static RomLoadPtr frogseg2_rom= new RomLoadPtr(){ public void handler() 
        {
		 ROM_REGION(0x10000);	/* 64k for code */
		 ROM_LOAD( "frogger.ic5",  0x0000, 0x1000, 0xefab0c79 );
                 ROM_LOAD( "frogger.ic6",  0x1000, 0x1000, 0xaeca9c13 );
                 ROM_LOAD( "frogger.ic7",  0x2000, 0x1000, 0xdd251066 );
                 ROM_LOAD( "frogger.ic8",  0x3000, 0x1000, 0xbf293a02 );

		ROM_REGION(0x1000);	/* temporary space for graphics (disposed after conversion) */
		 ROM_LOAD( "frogger.606",  0x0000, 0x0800, 0xf524ee30 );
	         ROM_LOAD( "frogger.607",  0x0800, 0x0800, 0x05f7d883 );

		ROM_REGION(0x10000);	/* 64k for the audio CPU */
		 ROM_LOAD( "frogger.608",  0x0000, 0x0800, 0xe8ab0256 );
                 ROM_LOAD( "frogger.609",  0x0800, 0x0800, 0x7380a48f );
                 ROM_LOAD( "frogger.610",  0x1000, 0x0800, 0x31d7eb27 );      
                ROM_END();
        }};
	
	
        static DecodePtr frogger_decode = new DecodePtr()
        {
            public void handler()
            {
                int A;
                /* the first ROM of the second CPU has data lines D0 and D1 swapped. Decode it. */
                char[] RAM = Machine.memory_region[Machine.drv.cpu[1].memory_region];
                
                for (A = 0;A < 0x0800;A++)
		   RAM[A] = (char)((RAM[A] & 0xfc) | ((RAM[A] & 1) << 1) | ((RAM[A] & 2) >> 1));
                
                
                	/* likewise, the first gfx ROM has data lines D0 and D1 swapped. Decode it. */
                RAM = Machine.memory_region[1];
                    for (A = 0;A < 0x0800;A++)
                        RAM[A] = (char)((RAM[A] & 0xfc) | ((RAM[A] & 1) << 1) | ((RAM[A] & 2) >> 1));
            }
        };
	
        static HiscoreLoadPtr hiload = new HiscoreLoadPtr()
        {
             public int handler(String paramString)
             {
                 /* get RAM pointer (this game is multiCPU, we can't assume the global */
                /* RAM pointer is pointing to the right place) */
                  char[] RAM = Machine.memory_region[0];
                  
                /* check if the hi score table has already been initialized */
                  if (memcmp(RAM,0x83f1,new char[] {0x63, 0x04 } ,2) == 0 &&  memcmp(RAM,0x83f9,new char[] {0x27, 0x01},2) == 0)  
                 {
                       FILE localFILE;
                       if ((localFILE = fopen(paramString, "rb")) != null)
                        {
                             fread(RAM, 0x83f1, 1, 10, localFILE);
                             RAM[0x83ef] = RAM[0x83f1];
                             RAM[0x83f0] = RAM[0x83f2];
                             fclose(localFILE);
                        }

                         return 1;
                 }
                 else return 0;	/* we can't load the hi scores yet */
                }
        };
        
        static HiscoreSavePtr hisave = new HiscoreSavePtr()
         {
              public void handler(String name)
             {
                  /* get RAM pointer (this game is multiCPU, we can't assume the global */
	         /* RAM pointer is pointing to the right place) */
                 char[] RAM = Machine.memory_region[0];
                 FILE localFILE;
                 if ((localFILE = fopen(name, "wb")) != null)
                 {
                       fwrite(RAM, 0x83f1, 1, 10, localFILE);
                     fclose(localFILE);
                }
            }
        };


	public static GameDriver frogger_driver = new GameDriver
	(
                "Frogger",
		"frogger",
                "ROBERT ANSCHUETZ\nNICOLA SALMORIA\nMIRKO BUFFONI",
		machine_driver,
	
		frogger_rom,
		frogger_decode, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		color_prom, null, null,
		ORIENTATION_DEFAULT,
	
		hiload, hisave
	);



	public static GameDriver frogseg2_driver = new GameDriver
	(
                "Frogger (alternate version)",
		"frogseg2",
                "ROBERT ANSCHUETZ\nNICOLA SALMORIA\nMIRKO BUFFONI",
		machine_driver,
	
		frogseg2_rom,
		frogger_decode, null,
		null,
	
		input_ports,null, trak_ports, dsw, keys,
	
		color_prom, null, null,
		ORIENTATION_DEFAULT,
	
		hiload, hisave
	);
}
