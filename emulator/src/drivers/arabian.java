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
 */

package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static machine.arabian.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.arabian.*;
import static vidhrdw.generic.*;
import static vidhrdw.arabian.*;

public class arabian
{

	static MemoryReadAddress readmem[] =
	{

		new MemoryReadAddress( 0x0000, 0x7fff, MRA_ROM ),
                new MemoryReadAddress( 0x8000, 0xbfff, MRA_RAM ),
                new MemoryReadAddress( 0xc000, 0xc000, input_port_0_r ),
                new MemoryReadAddress( 0xc200, 0xc200, input_port_1_r ),
                new MemoryReadAddress( 0xd000, 0xd7ef, MRA_RAM ),
                new MemoryReadAddress( 0xd7f0, 0xd7f8, arabian_input_port ),
                new MemoryReadAddress( 0xd7f9, 0xdfff, MRA_RAM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};

	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x8000, 0xbfff, arabian_videoramw, videoram ),
		new MemoryWriteAddress( 0xd000, 0xd7ff, MWA_RAM ),
		new MemoryWriteAddress( 0xe000, 0xe07f, arabian_spriteramw, spriteram ),
		new MemoryWriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};




	static IOWritePort writeport[] =
	{
		new IOWritePort( 0x00, 0x00, moja ),
		new IOWritePort( -1 )	/* end of table */
	};



	static InputPort input_ports[] =
	{
            	new InputPort(	/* IN6 */
		0x00,
		new int[]{ OSD_KEY_3, OSD_KEY_4, OSD_KEY_F1, 0, 0, 0, 0, 0 }
	),
	new InputPort(	/* DSW1 */
		0x00,
		new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
	),
		new InputPort(	/* IN0 */
			0x00,
			new int[] { 0, OSD_KEY_1, OSD_KEY_2, OSD_KEY_F2, 0, 0, 0, 0 }
		),

		new InputPort(	/* IN1 */
			0x00,
			new int[] { OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_UP, OSD_KEY_DOWN,
					0, 0, 0, 0 }
		),
		new InputPort(	/* IN2 */
			0x00,
			new int[] { OSD_KEY_CONTROL, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* IN3 */
			0x00,
			new int[] { OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_UP, OSD_KEY_DOWN,
					0, 0, 0, 0 }
		),
		new InputPort(	/* IN4 */
			0x00,
			new int[] { OSD_KEY_CONTROL, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(	/* IN5 */
			0x04,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort( -1 )  /* end of table */
	};
        static  TrakPort trak_ports[] =
        {
                new TrakPort(-1)
        };

        static KEYSet keys[] =
        {
                new KEYSet( 3, 2, "PL1 MOVE UP" ),
                new KEYSet( 3, 1, "PL1 MOVE LEFT"  ),
                new KEYSet( 3, 0, "PL1 MOVE RIGHT" ),
                new KEYSet( 3, 3, "PL1 MOVE DOWN" ),
                new KEYSet( 4, 0, "PL1 KICK" ),
                new KEYSet( 5, 2, "PL2 MOVE UP" ),
                new KEYSet( 5, 1, "PL2 MOVE LEFT"  ),
                new KEYSet( 5, 0, "PL2 MOVE RIGHT" ),
                new KEYSet( 5, 3, "PL2 MOVE DOWN" ),
                new KEYSet( 6, 0, "PL2 KICK" ),
                new KEYSet(-1)
        };

	static DSW dsw[] =
	{
	new DSW( 1, 0x01, "LIVES       ", new String[]{ "  3", "  5" } ),
	new DSW( 1, 0x08, "BOWLS       ", new String[]{ "CARRY TO NEXT LEVEL",
				     "       DO NOT CARRY" } ),
	new DSW( 1, 0xf0, "COIN SELECT ", new String[]{ "1 COIN 1 CREDIT", "SET 2", "SET 3", "SET 4",
				    "SET 5", "SET 6", "SET 7", "FREE PLAY"} ),
	new DSW( 7, 0x02, "ATTRACT SOUND", new String[]{ " ON", "OFF" } ),
	new DSW( 7, 0x0c, "BONUS        ", new String[]{ "   NO BONUS", "20000 1 MEN", "30000 1 MEN", "FORGET BONUS"} ),
	new DSW( -1 )
	};


	static char palette[] =
	{
        /*colors for plane 1*/
                0   , 0   , 0,
                0   , 37*4, 53*4,
                0   , 40*4, 63*4,
                0   , 44*4, 63*4,
                48*4, 22*4, 0,
                63*4, 39*4, 51*4,
                63*4, 56*4, 0,
                60*4, 60*4, 60*4,
                0   , 0   , 0,
                32*4, 12*4, 0,
                39*4, 18*4, 0,
                0   , 24*4, 51*4,
                45*4, 20*4, 1*4,
                63*4, 36*4, 51*4,
                57*4, 41*4, 10*4,
                63*4, 39*4, 51*4,
        /*colors for plane 2*/
                0   , 0   , 0,
                0   , 28*4, 0,
                0   , 11*4, 0,
                0   , 40*4, 0,
                48*4, 22*4, 0,
                58*4, 48*4, 0,
                44*4, 18*4, 0,
                60*4, 60*4, 60*4,
                25*4, 6*4 , 0,
                28*4, 21*4, 0,
                26*4, 18*4, 0,
                0   , 24*4, 0,
                45*4, 20*4, 1*4,
                51*4, 30*4, 5*4,
                57*4, 41*4, 10*4,
                63*4, 53*4, 16*4,
	};

	static final int BLACK=0,BLUE1=1,BLUE2=3,BLUE3=4,YELLOW=5;



	static char colortable[] =
	{
	/* characters and sprites */
            BLACK,BLUE1,BLACK,YELLOW,
	};


	static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				4000000,	/* 4 Mhz */
				0,
				readmem, writemem, null, writeport,
				arabian_interrupt, 1
			)
		},
		60,
		null,

		/* video hardware */
                32*8, 32*8, new rectangle( 0x0b, 0xf2, 0, 32*8-1 ),
		null,
		sizeof(palette)/3, sizeof(colortable),
		null,
                VIDEO_TYPE_RASTER,
		null,
		arabian_vh_start,
		arabian_vh_stop,
		arabian_vh_screenrefresh,

		/* sound hardware */
		null,
		null,
		arabian_sh_start,
		AY8910_sh_stop,
		AY8910_sh_update
	);


	/***************************************************************************

	  Game driver(s)

	***************************************************************************/
        static RomLoadPtr arabian_rom= new RomLoadPtr(){ public void handler() 
        {
		ROM_REGION(0x10000);	/* 64k for code */
		ROM_LOAD( "ic1rev2.87",       0x0000, 0x2000, 0x5e1c98b8 );
                ROM_LOAD( "ic2rev2.88",       0x2000, 0x2000, 0x092f587e );
                ROM_LOAD( "ic3rev2.89",       0x4000, 0x2000, 0x15145f23 );
                ROM_LOAD( "ic4rev2.90",       0x6000, 0x2000, 0x32b77b44 );

		ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "ic84.91",      0x0000, 0x2000, 0xc4637822 );	/*this is not used at all*/
													/* might be removed */
		ROM_REGION(0x10000); /* space for graphics roms */
		ROM_LOAD( "ic84.91",      0x0000, 0x2000, 0xc4637822 );	/* because of very rare way */
                ROM_LOAD( "ic85.92",      0x2000, 0x2000, 0xf7c6866d ); /* CRT controller uses these roms */
                ROM_LOAD( "ic86.93",      0x4000, 0x2000, 0x71acd48d );  /* there's no way, but to decode */
                ROM_LOAD( "ic87.94",      0x6000, 0x2000, 0x82160b9a );	/* it at runtime - which is SLOW */
	        ROM_END();
          }};


          static HiscoreLoadPtr arabian_hiload = new HiscoreLoadPtr()
          {
                public int handler(String name)
                {
                      char[] RAM = Machine.memory_region[0];
                       FILE localFILE;

                      /* Wait for hiscore table initialization to be done. */
                      if (memcmp(RAM,0xd384, new char[] {0x00,0x00,0x00,0x01,0x00,0x00 }, 6) != 0)
                        return 0;

                        if ((localFILE = fopen(name, "rb")) != null)
                        {
                          /* Load and set hiscore table. */
                          fread(RAM,0xd384,1,6*10,localFILE);
                          fclose(localFILE);
                        }

                      return 1;
                     }
        };
         static HiscoreSavePtr arabian_hisave = new HiscoreSavePtr()
          {
                public void handler(String name)
                {
                  char[] RAM = Machine.memory_region[0];
                  FILE localFILE;
                  if ((localFILE = fopen(name, "wb")) != null)
                  {
                       /* Write hiscore table. */
                    fwrite(RAM, 0xd384, 1, 6*10, localFILE);
                    fclose(localFILE);
                  }
                 }
          };
	public static GameDriver arabian_driver = new GameDriver
	(
                "Arabian",
		"arabian",
                "JAREK BURCZYNSKI",
		machine_driver,

		arabian_rom,
		null, null,
		null,

		input_ports,null, trak_ports, dsw, keys,

		null, palette, colortable,
		ORIENTATION_DEFAULT,

		arabian_hiload, arabian_hisave
	);
}
