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
 * roms are from v0.36 romset
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
import static vidhrdw.generic.*;
import static vidhrdw.phoenix.*;
import static sndhrdw.phoenix.*;
import static sndhrdw.pleiads.*;
import static machine.phoenix.*;
import static mame.memoryH.*;

public class phoenix
{
    static MemoryReadAddress readmem[] =
    {
            new MemoryReadAddress( 0x7800, 0x7Bff, input_port_1_r ),	/* DSW */
            new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
            new MemoryReadAddress( 0x4000, 0x4fff, MRA_RAM ),	/* video RAM */
            new MemoryReadAddress( 0x5000, 0x6fff, MRA_RAM ),
            new MemoryReadAddress( 0x7000, 0x73ff, input_port_0_r ),	/* IN0 */
            new MemoryReadAddress( 0x7400, 0x77ff, MRA_RAM ),
            new MemoryReadAddress( 0x7c00, 0x7fff, MRA_RAM ),
            new MemoryReadAddress( -1 )	/* end of table */
    };

    static MemoryWriteAddress writemem[] =
    {
            new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
            new MemoryWriteAddress( 0x4000, 0x43ff, phoenix_videoram2_w, phoenix_videoram2 ),
            new MemoryWriteAddress( 0x4400, 0x47ff, MWA_RAM ),
            new MemoryWriteAddress( 0x4800, 0x4bff, videoram_w, videoram, videoram_size ),
            new MemoryWriteAddress( 0x4C00, 0x4fff, MWA_RAM ),
            new MemoryWriteAddress( 0x5000, 0x53ff, phoenix_videoreg_w ),
            new MemoryWriteAddress( 0x5400, 0x57ff, MWA_RAM ),
            new MemoryWriteAddress( 0x5800, 0x5bff, phoenix_scrollreg_w ),
            new MemoryWriteAddress( 0x5C00, 0x5fff, MWA_RAM ),
            new MemoryWriteAddress( 0x6000, 0x63ff, phoenix_sound_control_a_w ),
            new MemoryWriteAddress( 0x6400, 0x67ff, MWA_RAM ),
            new MemoryWriteAddress( 0x6800, 0x6bff, phoenix_sound_control_b_w ),
            new MemoryWriteAddress( 0x6C00, 0x6fff, MWA_RAM ),
            new MemoryWriteAddress( 0x7400, 0x77ff, MWA_RAM ),
            new MemoryWriteAddress( 0x7C00, 0x7fff, MWA_RAM ),
            new MemoryWriteAddress( -1 )	/* end of table */
    };

    static MemoryWriteAddress pl_writemem[] =
    {
            new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
            new MemoryWriteAddress( 0x4000, 0x43ff, phoenix_videoram2_w, phoenix_videoram2 ),
            new MemoryWriteAddress( 0x4400, 0x47ff, MWA_RAM ),
            new MemoryWriteAddress( 0x4800, 0x4bff, videoram_w, videoram, videoram_size ),
            new MemoryWriteAddress( 0x4C00, 0x4fff, MWA_RAM ),
            new MemoryWriteAddress( 0x5000, 0x53ff, phoenix_videoreg_w ),
            new MemoryWriteAddress( 0x5400, 0x57ff, MWA_RAM ),
            new MemoryWriteAddress( 0x5800, 0x5bff, phoenix_scrollreg_w ),
            new MemoryWriteAddress( 0x5C00, 0x5fff, MWA_RAM ),
            new MemoryWriteAddress( 0x6000, 0x63ff, pleiads_sound_control_a_w ),
            new MemoryWriteAddress( 0x6400, 0x67ff, MWA_RAM ),
            new MemoryWriteAddress( 0x6800, 0x6bff, pleiads_sound_control_b_w ),
            new MemoryWriteAddress( 0x6C00, 0x6fff, MWA_RAM ),
            new MemoryWriteAddress( 0x7400, 0x77ff, MWA_RAM ),
            new MemoryWriteAddress( 0x7C00, 0x7fff, MWA_RAM ),
            new MemoryWriteAddress( -1 )	/* end of table */
    };


    static InputPort input_ports[] =
    {
            new InputPort(	/* IN0 */
                    0xff,
                    new int[]{ OSD_KEY_3, OSD_KEY_1, OSD_KEY_2, 0,
                            OSD_KEY_CONTROL, OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_ALT }
            ),
            new InputPort(	/* DSW */
                    0x60,
                    new int[]{ 0, 0, 0, 0, 0, 0, 0, IPB_VBLANK }
            ),
            new InputPort( -1 )	/* end of table */
    };

    static TrakPort[] trak_ports =
    {
           new TrakPort(-1)
    };

    static KEYSet keys[] =
    {
            new KEYSet( 0, 6, "MOVE LEFT"  ),
            new KEYSet( 0, 5, "MOVE RIGHT" ),
            new KEYSet( 0, 7, "BARRIER"    ),
            new KEYSet( 0, 4, "FIRE" ),
            new KEYSet( -1 )
    };


    static DSW dsw[] =
    {
            new DSW( 1, 0x03, "LIVES", new String[]{ "3", "4", "5", "6" } ),
            new DSW( 1, 0x0c, "BONUS", new String[]{ "3000", "4000", "5000", "6000" } ),
            new DSW( 1, 0x20, "DEMO SOUNDS", new String[]{ "OFF", "ON" } ),
            new DSW( -1 )
    };



    public static GfxLayout charlayout = new GfxLayout
    (
            8,8,	/* 8*8 characters */
            256,	/* 256 characters */
            2,	/* 2 bits per pixel */
            new int[]{ 0, 256*8*8 },	/* the two bitplanes are separated */
            new int[]{ 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
            new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },	/* pretty straightforward layout */
            8*8	/* every char takes 8 consecutive bytes */
    );

    static GfxDecodeInfo gfxdecodeinfo[] =
    {
            new GfxDecodeInfo( 1, 0x0000, charlayout,  0, 16 ),
            new GfxDecodeInfo( 1, 0x1000, charlayout, 64, 16 ),
            new GfxDecodeInfo( -1 ) /* end of array */
    };



    static char palette[] =
    {
            0x00,0x00,0x00,	/* BLACK */
            0xff,0xff,0xff,	/* WHITE */
            0xff,0x00,0x00,	/* RED */
            0x49,0xdb,0x00,	/* GREEN */
            0x24,0x24,0xdb,	/* BLUE */
            0x00,0xdb,0x95,	/* CYAN, */
            0xff,0xff,0x00,	/* YELLOW, */
            0xff,0xb6,0xdb,	/* PINK */
            0xff,0xb6,0x49,	/* ORANGE */
            0xff,0x24,0xb6, /* LTPURPLE */
            0xff,0xb6,0x00,	/* DKORANGE */
            0xb6,0x24,0xff, /* DKPURPLE */
            0x95,0x95,0x95, /* GREY */
            0xdb,0xdb,0x00,	/* DKYELLOW */
            0x00,0x95,0xff, /* BLUISH */
            0xff,0x00,0xff, /* PURPLE */
    };

    static final int pBLACK=0;
    static final int pWHITE=1;
    static final int pRED=2;
    static final int pGREEN=3;
    static final int pBLUE=4;
    static final int pCYAN=5;
    static final int pYELLOW=6;
    static final int pPINK=7;
    static final int pORANGE=8;
    static final int pLTPURPLE=9;
    static final int pDKORANGE=10;
    static final int pDKPURPLE=11;
    static final int pGREY=12;
    static final int pDKYELLOW=13;
    static final int pBLUISH=14;
    static final int pPURPLE=15;

    /* 4 colors per pixel * 8 groups of characters * 2 charsets * 2 pallettes */
    static char colortable[] =
    {
            /* charset A pallette A */
            pBLACK,pBLACK,pCYAN,pCYAN,          /* Background, Unused, Letters, asterisks */
            pBLACK,pYELLOW,pRED,pWHITE,         /* Background, Ship middle, Numbers/Ship, Ship edge */
            pBLACK,pYELLOW,pRED,pWHITE,         /* Background, Ship middle, Ship, Ship edge/bullets */
            pBLACK,pPINK,pPURPLE,pYELLOW,       /* Background, Bird eyes, Bird middle, Bird Wings */
            pBLACK,pPINK,pPURPLE,pYELLOW,       /* Background, Bird eyes, Bird middle, Bird Wings */
            pBLACK,pPINK,pPURPLE,pYELLOW,       /* Background, Bird eyes, Bird middle, Bird Wings */
            pBLACK,pWHITE,pPURPLE,pYELLOW,      /* Background, Explosions */
            pBLACK,pPURPLE,pGREEN,pWHITE,       /* Background, Barrier */
            /* charset A pallette B */
            pBLACK,pBLUE,pCYAN,pCYAN,           /* Background, Unused, Letters, asterisks */
            pBLACK,pYELLOW,pRED,pWHITE,         /* Background, Ship middle, Numbers/Ship, Ship edge */
            pBLACK,pYELLOW,pRED,pWHITE,         /* Background, Ship middle, Ship, Ship edge/bullets */
            pBLACK,pYELLOW,pGREEN,pPURPLE,      /* Background, Bird eyes, Bird middle, Bird Wings */
            pBLACK,pYELLOW,pGREEN,pPURPLE,      /* Background, Bird eyes, Bird middle, Bird Wings */
            pBLACK,pYELLOW,pGREEN,pPURPLE,      /* Background, Bird eyes, Bird middle, Bird Wings */
            pBLACK,pWHITE,pRED,pPURPLE,         /* Background, Explosions */
            pBLACK,pPURPLE,pGREEN,pWHITE,       /* Background, Barrier */
            /* charset B pallette A */
            pBLACK,pRED,pBLUE,pGREY,            /* Background, Starfield */
            pBLACK,pPURPLE,pBLUISH,pDKORANGE,   /* Background, Planets */
            pBLACK,pDKPURPLE,pGREEN,pDKORANGE,  /* Background, Mothership: turrets, u-body, l-body */
            pBLACK,pBLUISH,pDKPURPLE,pLTPURPLE, /* Background, Motheralien: face, body, feet */
            pBLACK,pPURPLE,pBLUISH,pGREEN,      /* Background, Eagles: face, body, shell */
            pBLACK,pPURPLE,pBLUISH,pGREEN,      /* Background, Eagles: face, body, feet */
            pBLACK,pPURPLE,pBLUISH,pGREEN,      /* Background, Eagles: face, body, feet */
            pBLACK,pPURPLE,pBLUISH,pGREEN,      /* Background, Eagles: face, body, feet */
            /* charset B pallette B */
            pBLACK,pRED,pBLUE,pGREY,            /* Background, Starfield */
            pBLACK,pPURPLE,pBLUISH,pDKORANGE,   /* Background, Planets */
            pBLACK,pDKPURPLE,pGREEN,pDKORANGE,  /* Background, Mothership: turrets, upper body, lower body */
            pBLACK,pBLUISH,pDKPURPLE,pLTPURPLE, /* Background, Motheralien: face, body, feet */
            pBLACK,pBLUISH,pLTPURPLE,pGREEN,    /* Background, Eagles: face, body, shell */
            pBLACK,pBLUISH,pLTPURPLE,pGREEN,    /* Background, Eagles: face, body, feet */
            pBLACK,pBLUISH,pLTPURPLE,pGREEN,    /* Background, Eagles: face, body, feet */
            pBLACK,pBLUISH,pLTPURPLE,pGREEN     /* Background, Eagles: face, body, feet */
    };

    static char pl_colortable[] =
    {
            /* charset A pallette A */
            pBLACK,pBLACK,pCYAN,pCYAN,          /* Background, Unused, Letters, asterisks */
            pBLACK,pYELLOW,pRED,pWHITE,         /* Background, Ship sides, Numbers/Ship, Ship gun */
            pBLACK,pYELLOW,pRED,pWHITE,         /* Background, Ship sides, Ship body, Ship gun */
            pBLACK,pYELLOW,pBLACK,pYELLOW,      /* Background, Martian1 eyes, Martian1 body, Martian1 mouth */
            pBLACK,pYELLOW,pBLACK,pYELLOW,      /* Background, Martian1 eyes, Martian1 body, Martian1 mouth */
            pBLACK,pPINK,pPURPLE,pYELLOW,       /* Background, UFO edge, UFO middle, UFO eyes */
            pBLACK,pWHITE,pPURPLE,pYELLOW,      /* Background, Martian2 legs, Martian2 head, Martian2 eyes */
            pBLACK,pPURPLE,pGREEN,pWHITE,       /* Background, Explosions */
            /* charset A pallette B */
            pBLACK,pBLUE,pCYAN,pCYAN,           /* Background, Unused, Letters, asterisks */
            pBLACK,pYELLOW,pRED,pWHITE,         /* Background, Ship sides, Numbers/Ship, Ship gun */
            pBLACK,pYELLOW,pRED,pWHITE,         /* Background, Ship sides, Ship body, Ship gun */
            pBLACK,pYELLOW,pBLUISH,pWHITE,      /* Background, Martian1 eyes, Martian1 body, Martian1 mouth */
            pBLACK,pYELLOW,pBLUISH,pWHITE,      /* Background, Martian1 eyes, Martian1 body, Martian1 mouth */
            pBLACK,pYELLOW,pGREEN,pPURPLE,      /* Background, UFO edge, UFO middle, UFO eyes */
            pBLACK,pWHITE,pRED,pPURPLE,         /* Background, Martian2 legs, Martian2 head, Martian2 eyes */
            pBLACK,pPURPLE,pGREEN,pWHITE,       /* Background, Explosions */
            /* charset B pallette A */
            pBLACK,pRED,pBLUE,pGREY,            /* Background, Stars & planets */
            pBLACK,pPURPLE,pBLUISH,pDKORANGE,   /* Background, Battleship: body, eyes, edge */
            pBLACK,pDKPURPLE,pGREEN,pDKORANGE,  /* Background, Radar dish inside, Radar base/parked ship, Radar dish outside */
            pBLACK,pBLUISH,pDKPURPLE,pLTPURPLE, /* Background, Building/Landing light(off?), parked ship, Landing light(on?) */
            pBLACK,pPURPLE,pBLUISH,pGREEN,      /* Background, Space Monsters: face, body, horns/claws */
            pBLACK,pPURPLE,pBLUISH,pGREEN,      /* Background, Space Monsters: face, body, horns/claws */
            pBLACK,pPURPLE,pBLUISH,pGREEN,      /* Background, Space Monsters: face, body, horns/claws */
            pBLACK,pPURPLE,pBLUISH,pGREEN,      /* Background, Space Monsters: body, horns/claws */
            /* charset B pallette B */
            pBLACK,pRED,pBLUE,pGREY,            /* Background, Stars & planets */
            pBLACK,pPURPLE,pBLUISH,pDKORANGE,   /* Background, Battleship: body, eyes, edge */
            pBLACK,pDKPURPLE,pGREEN,pDKORANGE,  /* Background, Radar dish inside, Radar base/parked ship, Radar dish outside */
            pBLACK,pBLUISH,pDKPURPLE,pLTPURPLE, /* Background, Building/Landing light(off?), parked ship, Landing light(on?) */
            pBLACK,pBLUISH,pLTPURPLE,pGREEN,    /* Background, Space Monsters: face, body, horns/claws */
            pBLACK,pBLUISH,pLTPURPLE,pGREEN,    /* Background, Space Monsters: face, body, horns/claws */
            pBLACK,pBLUISH,pLTPURPLE,pGREEN,    /* Background, Space Monsters: face, body, horns/claws */
            pBLACK,pBLUISH,pLTPURPLE,pGREEN,    /* Background, Space Monsters: body, horns/claws */
    };


    /* waveforms for the audio hardware */
    static  char samples[] =
    {
            /* sine-wave */
            0x0F, 0x0F, 0x0F, 0x06, 0x06, 0x09, 0x09, 0x06, 0x06, 0x09, 0x06, 0x0D, 0x0F, 0x0F, 0x0D, 0x00,
            0xE6, 0xDE, 0xE1, 0xE6, 0xEC, 0xE6, 0xE7, 0xE7, 0xE7, 0xEC, 0xEC, 0xEC, 0xE7, 0xE1, 0xE1, 0xE7,
    /*
            0x00,0x00,0x00,0x00,0x22,0x22,0x22,0x22,0x44,0x44,0x44,0x44,0x22,0x22,0x22,0x22,
            0x00,0x00,0x00,0x00,0xdd,0xdd,0xdd,0xdd,0xbb,0xbb,0xbb,0xbb,0xdd,0xdd,0xdd,0xdd,
    */
            /* white-noise ? */
            0x79, 0x75, 0x71, 0x72, 0x72, 0x6F, 0x70, 0x71, 0x71, 0x73, 0x75, 0x76, 0x74, 0x74, 0x78, 0x7A,
            0x79, 0x7A, 0x7B, 0x7C, 0x7C, 0x7C, 0x7C, 0x7C, 0x7C, 0x7C, 0x7D, 0x80, 0x85, 0x88, 0x88, 0x87,
            0x8B, 0x8B, 0x8A, 0x8A, 0x89, 0x87, 0x85, 0x87, 0x89, 0x86, 0x83, 0x84, 0x84, 0x85, 0x84, 0x84,
            0x85, 0x86, 0x87, 0x87, 0x88, 0x88, 0x86, 0x81, 0x7E, 0x7D, 0x7F, 0x7D, 0x7C, 0x7D, 0x7D, 0x7C,
            0x7E, 0x81, 0x7F, 0x7C, 0x7E, 0x82, 0x82, 0x82, 0x82, 0x83, 0x83, 0x84, 0x83, 0x82, 0x82, 0x83,
            0x82, 0x84, 0x88, 0x8C, 0x8E, 0x8B, 0x8B, 0x8C, 0x8A, 0x8A, 0x8A, 0x89, 0x85, 0x86, 0x89, 0x89,
            0x86, 0x85, 0x85, 0x85, 0x84, 0x83, 0x82, 0x83, 0x83, 0x83, 0x82, 0x83, 0x83

    };



    public static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
                            CPU_Z80,
                            3072000,	/* 3 Mhz ? */
                            0,
                            readmem,writemem,null,null,
                            phoenix_interrupt,1
                    )
            },
            60,
            null,

            /* video hardware */
            32*8, 32*8, new rectangle( 3*8, 29*8-1, 0*8, 31*8-1 ),
            gfxdecodeinfo,
            sizeof(palette)/3,sizeof(colortable),
            null,
            VIDEO_TYPE_RASTER, 
            null,
            phoenix_vh_start,
            phoenix_vh_stop,
            phoenix_vh_screenrefresh,

            /* sound hardware */
            samples,
            phoenix_sh_init,
            phoenix_sh_start,
            null,
            phoenix_sh_update
    );

    public static MachineDriver pleiads_machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
                            CPU_Z80,
                            3072000,	/* 3 Mhz ? */
                            0,
                            readmem,pl_writemem,null,null,
                            phoenix_interrupt,1
                    )
            },
            60,
            null,

            /* video hardware */
            32*8, 32*8, new rectangle( 3*8, 29*8-1, 0*8, 31*8-1 ),
            gfxdecodeinfo,
            sizeof(palette)/3,sizeof(colortable),
            null,
            VIDEO_TYPE_RASTER,
            null,
            phoenix_vh_start,
            phoenix_vh_stop,
            phoenix_vh_screenrefresh,

            /* sound hardware */
            samples,
            pleiads_sh_init,
            pleiads_sh_start,
            null,
            pleiads_sh_update
    );

    static String phoenix_sample_names[] =
    {
            "shot8.sam",
            "death8.sam",
            null	/* end of array */
    };

    /***************************************************************************

      Game driver(s)

    ***************************************************************************/
    static RomLoadPtr phoenix_rom = new RomLoadPtr(){ public void handler() 
    {
            ROM_REGION(0x10000);	/* 64k for code */
            ROM_LOAD( "ic45",         0x0000, 0x0800, 0x9f68086b );
            ROM_LOAD( "ic46",         0x0800, 0x0800, 0x273a4a82 );
            ROM_LOAD( "ic47",         0x1000, 0x0800, 0x3d4284b9 );
            ROM_LOAD( "ic48",         0x1800, 0x0800, 0xcb5d9915 );
            ROM_LOAD( "ic49",         0x2000, 0x0800, 0xa105e4e7 );
            ROM_LOAD( "ic50",         0x2800, 0x0800, 0xac5e9ec1 );
            ROM_LOAD( "ic51",         0x3000, 0x0800, 0x2eab35b4 );
            ROM_LOAD( "ic52",         0x3800, 0x0800, 0xaff8e9c5 );

            ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
            ROM_LOAD( "ic39",         0x0000, 0x0800, 0x53413e8f );
            ROM_LOAD( "ic40",         0x0800, 0x0800, 0x0be2ba91 );           
            ROM_LOAD( "ic23",         0x1000, 0x0800, 0x3c7e623f );
            ROM_LOAD( "ic24",         0x1800, 0x0800, 0x59916d3b );
            ROM_END();
    }};
    static RomLoadPtr phoenixt_rom = new RomLoadPtr(){ public void handler() 
    {
            ROM_REGION(0x10000);	/* 64k for code */
            ROM_LOAD( "phoenix.45",   0x0000, 0x0800, 0x5b8c55a8 );
            ROM_LOAD( "phoenix.46",   0x0800, 0x0800, 0xdbc942fa );
            ROM_LOAD( "phoenix.47",   0x1000, 0x0800, 0xcbbb8839 );
            ROM_LOAD( "phoenix.48",   0x1800, 0x0800, 0xcb65eff8 );
            ROM_LOAD( "phoenix.49",   0x2000, 0x0800, 0xc8a5d6d6 );
            ROM_LOAD( "ic50",         0x2800, 0x0800, 0xac5e9ec1 );
            ROM_LOAD( "ic51",         0x3000, 0x0800, 0x2eab35b4 );
            ROM_LOAD( "phoenix.52",   0x3800, 0x0800, 0xb9915263 );

            ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
            ROM_LOAD( "ic39",         0x0000, 0x0800, 0x53413e8f );
            ROM_LOAD( "ic40",         0x0800, 0x0800, 0x0be2ba91 );           
            ROM_LOAD( "ic23",         0x1000, 0x0800, 0x3c7e623f );
            ROM_LOAD( "ic24",         0x1800, 0x0800, 0x59916d3b );
            ROM_END();
    }};
    static RomLoadPtr phoenix3_rom = new RomLoadPtr(){ public void handler() 
    {
            ROM_REGION(0x10000);	/* 64k for code */
            ROM_LOAD( "phoenix3.45",  0x0000, 0x0800, 0xa362cda0 );
            ROM_LOAD( "phoenix3.46",  0x0800, 0x0800, 0x5748f486 );
            ROM_LOAD( "phoenix.47",   0x1000, 0x0800, 0xcbbb8839 );
            ROM_LOAD( "phoenix3.48",  0x1800, 0x0800, 0xb5d97a4d );
            ROM_LOAD( "ic49",         0x2000, 0x0800, 0xa105e4e7 );
            ROM_LOAD( "ic50",         0x2800, 0x0800, 0xac5e9ec1 );
            ROM_LOAD( "ic51",         0x3000, 0x0800, 0x2eab35b4 );
            ROM_LOAD( "phoenix3.52",  0x3800, 0x0800, 0xd2c5c984 );

            ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
            ROM_LOAD( "ic39",         0x0000, 0x0800, 0x53413e8f );
            ROM_LOAD( "ic40",         0x0800, 0x0800, 0x0be2ba91 );           
            ROM_LOAD( "ic23",         0x1000, 0x0800, 0x3c7e623f );
            ROM_LOAD( "ic24",         0x1800, 0x0800, 0x59916d3b );
            ROM_END();
    }};
    static RomLoadPtr pleiads_rom = new RomLoadPtr(){ public void handler() 
    {
            ROM_REGION(0x10000);	/* 64k for code */
            ROM_LOAD( "ic47.r1",      0x0000, 0x0800, 0x960212c8 );
            ROM_LOAD( "ic48.r2",      0x0800, 0x0800, 0xb254217c );
            ROM_LOAD( "ic47.bin",     0x1000, 0x0800, 0x87e700bb ); /* IC 49 on real board */
            ROM_LOAD( "ic48.bin",     0x1800, 0x0800, 0x2d5198d0 ); /* IC 50 on real board */
            ROM_LOAD( "ic51.r5",      0x2000, 0x0800, 0x49c629bc );
            ROM_LOAD( "ic50.bin",     0x2800, 0x0800, 0xf1a8a00d ); /* IC 52 on real board */
            ROM_LOAD( "ic53.r7",      0x3000, 0x0800, 0xb5f07fbc );
            ROM_LOAD( "ic52.bin",     0x3800, 0x0800, 0xb1b5a8a6 ); /* IC 54 on real board */
            
            ROM_REGION(0x2000);	/* temporary space for graphics (disposed after conversion) */
            ROM_LOAD( "ic39.bin",     0x0000, 0x0800, 0x85866607 ); /* IC 27 on real board */
	    ROM_LOAD( "ic40.bin",     0x0800, 0x0800, 0xa841d511 ); /* IC 26 on real board */       
            ROM_LOAD( "ic23.bin",     0x1000, 0x0800, 0x4e30f9e7 ); /* IC 45 on real board */
	    ROM_LOAD( "ic24.bin",     0x1800, 0x0800, 0x5188fc29 ); /* IC 44 on real board */
            ROM_END();
    }};



    static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler()
    {
           FILE f;

       /* get RAM pointer (this game is multiCPU, we can't assume the global */
       /* RAM pointer is pointing to the right place) */
       char[] RAM = Machine.memory_region[0];

       /* check if the hi score table has already been initialized */
  /*TOFIX     if (memcmp(RAM,0x438a,new char[] {0x00,0x00,0x0f},3) == 0)
       {
          if ((f = fopen(name,"rb")) != null)
          {
             fread(RAM,0x4388,1,4,f);

    /*       // I suppose noone can do such an HISCORE!!! ;)
             phoenix_videoram2_w(0x0221, (RAM[0x4388] >> 4)+0x20);
             phoenix_videoram2_w(0x0201, (RAM[0x4388] & 0xf)+0x20);
    */
/*TOFIX             phoenix_videoram2_w.handler(0x01e1, (RAM[0x4389] >> 4)+0x20);
             phoenix_videoram2_w.handler(0x01c1, (RAM[0x4389] & 0xf)+0x20);
             phoenix_videoram2_w.handler(0x01a1, (RAM[0x438a] >> 4)+0x20);
             phoenix_videoram2_w.handler(0x0181, (RAM[0x438a] & 0xf)+0x20);
             phoenix_videoram2_w.handler(0x0161, (RAM[0x438b] >> 4)+0x20);
             phoenix_videoram2_w.handler(0x0141, (RAM[0x438b] & 0xf)+0x20);
             fclose(f);
          }

          return 1;
       }
       else */ return 0; /* we can't load the hi scores yet */
    }};
    static long get_score(CharPtr score)
    {
      return score.read(3) + 256 * score.read(2) + 65536L * score.read(1) + 16777216L * score.read(0);
    }



    static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler()
    {
       FILE f;

           /* get RAM pointer (this game is multiCPU, we can't assume the global */
           /* RAM pointer is pointing to the right place) */
       char[] RAM = Machine.memory_region[0];
       int offset=0;
       long score1 = get_score(new CharPtr(RAM,0x4380));
       long score2 = get_score(new CharPtr(RAM,0x4384));
       long hiscore = get_score(new CharPtr(RAM,0x4388));

       if (score1 > hiscore) offset += 0x4380;         /* check consistency */
       else if (score2 > hiscore) offset += 0x4384;
       else offset += 0x4388;

  /*TOFIX     if ((f = fopen(name,"wb")) != null)
       {
          fwrite(RAM,offset,1,4,f);
          fclose(f);
       }*/
    }};
        public static GameDriver phoenix_driver = new GameDriver
	(
                "Phoenix (Amstar)",
                "phoenix",
                "RICHARD DAVIES\nBRAD OLIVER\nMIRKO BUFFONI\nNICOLA SALMORIA\nSHAUN STEPHENSON\nANDREW SCOTT",
                machine_driver,

                phoenix_rom,
                null, null,
                phoenix_sample_names,

                input_ports,null, trak_ports, dsw, keys,

                null, palette, colortable,
                ORIENTATION_DEFAULT,

                hiload, hisave
        );

        public static GameDriver  phoenixt_driver = new GameDriver
	(
                "Phoenix (Taito)",
                "phoenixt",
                "RICHARD DAVIES\nBRAD OLIVER\nMIRKO BUFFONI\nNICOLA SALMORIA\nSHAUN STEPHENSON\nANDREW SCOTT",
                machine_driver,

                phoenixt_rom,
                null, null,
                phoenix_sample_names,

                input_ports,null, trak_ports, dsw, keys,

                null, palette, colortable,
                ORIENTATION_DEFAULT,

                hiload, hisave
        );

        public static GameDriver  phoenix3_driver = new GameDriver
	(
                "Phoenix (T.P.N.)",
                "phoenix3",
                "RICHARD DAVIES\nBRAD OLIVER\nMIRKO BUFFONI\nNICOLA SALMORIA\nSHAUN STEPHENSON\nANDREW SCOTT",
                machine_driver,

                phoenix3_rom,
                null, null,
                phoenix_sample_names,

                input_ports,null, trak_ports, dsw, keys,

                null, palette, colortable,
                ORIENTATION_DEFAULT,

                hiload, hisave
        );

        public static GameDriver  pleiads_driver = new GameDriver
	(
                "Pleiads",
                "pleiads",
                "RICHARD DAVIES\nBRAD OLIVER\nMIRKO BUFFONI\nNICOLA SALMORIA\nSHAUN STEPHENSON\nANDREW SCOTT",
                pleiads_machine_driver,

                pleiads_rom,
                null, null,
                phoenix_sample_names,

                input_ports,null, trak_ports, dsw, keys,

                null, palette, pl_colortable,
                ORIENTATION_DEFAULT,

                hiload, hisave
        );
}
