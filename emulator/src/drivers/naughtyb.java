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
 *  naughtyb is naughtyc in v0.36 romset
 *  roms are using v0.27 loading order in graphics rom
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
import static machine.naughtyb.*;
import static vidhrdw.naughtyb.*;
import static mame.memoryH.*;

/**
 *
 * @author shadow
 */
public class naughtyb {
    static  MemoryReadAddress readmem[] =
    {
            new MemoryReadAddress( 0xb800, 0xbfff, input_port_1_r ),     /* DSW */
            new MemoryReadAddress( 0x4000, 0x7fff, MRA_RAM ),    /* work RAM */
            new MemoryReadAddress( 0x8000, 0x87ff, MRA_RAM ),    /* Scrollable video RAM */
            new MemoryReadAddress( 0x8800, 0x8fff, MRA_RAM ),    /* Scores RAM and so on */
            new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
            new MemoryReadAddress( 0xb000, 0xb7ff, input_port_0_r ),     /* IN0 */
            new MemoryReadAddress( -1 )  /* end of table */
    };

    static  MemoryWriteAddress writemem[] =
    {
            new MemoryWriteAddress( 0x4000, 0x7fff, MWA_RAM ),
            new MemoryWriteAddress( 0x8800, 0x8fff, naughtyb_videoram2_w, naughtyb_videoram2 ),
            new MemoryWriteAddress( 0x8000, 0x87ff, videoram_w, videoram, videoram_size ),
            new MemoryWriteAddress( 0x9000, 0x97ff, naughtyb_videoreg_w ),
            new MemoryWriteAddress( 0x9800, 0x9fff, MWA_RAM, naughtyb_scrollreg ),
    //        { 0xa000, 0xa7ff, naughtyb_sound_control_a_w },
    //        { 0xa800, 0xafff, naughtyb_sound_control_b_w },
            new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
            new MemoryWriteAddress( -1 )  /* end of table */
    };

        static InputPortPtr input_ports= new InputPortPtr(){ public void handler()  
        {

                PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY );
                PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY );

                PORT_START();	/* DSW0 & VBLANK */
                PORT_DIPNAME( 0x03, 0x01, "Lives", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "2" );
                PORT_DIPSETTING(    0x01, "3" );
                PORT_DIPSETTING(    0x02, "4" );
                PORT_DIPSETTING(    0x03, "5" );
                PORT_DIPNAME( 0x0c, 0x04, "Bonus Life", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "10000" );
                PORT_DIPSETTING(    0x04, "30000" );
                PORT_DIPSETTING(    0x08, "50000" );
                PORT_DIPSETTING(    0x0c, "70000" );
                PORT_DIPNAME( 0x30, 0x10, "Coinage", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "2 Coins/1 Credit" );
                PORT_DIPSETTING(    0x10, "1 Coin/1 Credit" );
                PORT_DIPSETTING(    0x20, "1 Coin/2 Credits" );
                PORT_DIPSETTING(    0x30, "1 Coin/3 Credits" );
                PORT_DIPNAME( 0x40, 0x00, "Difficulty", IP_KEY_NONE );
                PORT_DIPSETTING(    0x00, "Easy" );
                PORT_DIPSETTING(    0x40, "Hard" );
                /* This is a bit of a mystery. Bit 0x80 is read as the vblank, but
                   it apparently also controls cocktail/table mode. */
                PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_VBLANK );
                INPUT_PORTS_END();
        }};



    static GfxLayout charlayout = new GfxLayout
    (
            8,8,    /* 8*8 characters */
            512,    /* 512 characters */
            2,      /* 2 bits per pixel */
            new int[]{ 0, 512*8*8 }, /* the two bitplanes are separated */
            new int[]{ 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },
            new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },     /* pretty straightforward layout */
            8*8     /* every char takes 8 consecutive bytes */
    );



    static GfxDecodeInfo gfxdecodeinfo[] =
    {
            new GfxDecodeInfo( 1, 0x0000, charlayout,  0, 16 ),
            new GfxDecodeInfo( 1, 0x2000, charlayout,  0, 16 ),
            new GfxDecodeInfo( -1 ) /* end of array */
    };




    static char palette[] =
    {
            0x00,0x00,0x00,	/* BLACK */
            0xdb,0xdb,0xdb,	/* WHITE */
            0xff,0x00,0x00,	/* RED */
            0x00,0xff,0x00, /* GREEN */
            0x24,0x24,0xdb,	/* BLUE */
            0x00,0xff,0xdb,	/* CYAN, */
            0xff,0xff,0x00,	/* YELLOW, */
            239,3,239,      /* PINK */
            0xff,0xb6,0x49,	/* ORANGE */
            0xff,0x24,0xb6, /* LTPURPLE */
            0xb6,0x24,0xff, /* DKPURPLE */
            0x00,0xdb,0xdb,	/* DKCYAN */
            0xdb,0xdb,0x00,	/* DKYELLOW */
            0x95,0x95,0xff, /* BLUISH */
            0xff,0x00,0xff, /* PURPLE */
            0xdb,0x49,0x00, /* BROWN */
    };

      static final int BLACK = 0;
      static final int WHITE = 1;
      static final int RED = 2;
      static final int GREEN = 3;
      static final int BLUE = 4;
      static final int CYAN = 5;
      static final int YELLOW = 6;
      static final int PINK = 7;
      static final int ORANGE = 8;
      static final int LTPURPLE = 9;
      static final int DKPURPLE = 10;
      static final int DKCYAN = 11;
      static final int DKYELLOW = 12;
      static final int BLUISH = 13;
      static final int PURPLE = 14;
      static final int BROWN = 15;
    //define UNUSED BLACK

    /* COLORS ARE **COMPLETELY** GUESSED. */
    /* 4 colors per pixel * 8 groups of characters * 2 charsets * 2 pallettes */
    static char colortable[] =
    {
            /* charset A */
            BLACK,WHITE,BLUE,RED,          // bg, title outline, title main, hiscore
            BLACK,WHITE,BROWN,GREEN,        // bg,cinemat,bridge edge,trees
            BLACK,YELLOW,RED,PURPLE,      //   /* bg, rock explosion */
            BLACK,YELLOW,RED,PURPLE,           /* bg, monster,mouth,flash */
            BLACK,YELLOW,RED,PURPLE,           /* bg, monster,mouth,flash */
            BLACK,BLUISH,YELLOW,WHITE,         /* bg, arms/legs,rock/face,body/feet */
            BLACK,BLUISH,YELLOW,WHITE,         /* bg, arms/legs,rock/face,body/feet */
            BLACK,BLUISH,YELLOW,WHITE,         /* bg, during throw */
            /* charset B */
            BLACK,WHITE,RED,RED,          // bg, title outline, title main, hiscore
            BLACK,WHITE,BROWN,GREEN,        // bg,cinemat,bridge edge,trees
            BLACK,YELLOW,RED,PURPLE,      //   /* bg, rock explosion */
            BLACK,YELLOW,RED,PURPLE,           /* bg, monster,mouth,flash */
            BLACK,YELLOW,RED,PURPLE,           /* bg, monster,mouth,flash */
            BLACK,BLUISH,YELLOW,WHITE,         /* bg, arms/legs,rock/face,body/feet */
            BLACK,BLUISH,YELLOW,WHITE,         /* bg, arms/legs,rock/face,body/feet */
            BLACK,BLUISH,YELLOW,WHITE,         /* bg, during throw */
    };



    static MachineDriver machine_driver = new MachineDriver
    (
            /* basic machine hardware */
            new MachineCPU[] {
			new MachineCPU(
                            CPU_Z80,
                            1500000,	/* 3 Mhz ? */
                            0,
                            readmem,writemem,null,null,
                            naughtyb_interrupt,1
                    )
            },
            60,
            null,

            /* video hardware */
            28*8, 36*8, new rectangle( 0*8, 28*8-1, 0*8, 36*8-1 ),
            gfxdecodeinfo,
            sizeof(palette)/3,sizeof(colortable),
            null,
            VIDEO_TYPE_RASTER,
            null,
            naughtyb_vh_start,
            naughtyb_vh_stop,
            naughtyb_vh_screenrefresh,

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
    static RomLoadPtr naughtyc_rom= new RomLoadPtr(){ public void handler()  
    {

            ROM_REGION(0x10000);   /* 64k for code */
            ROM_LOAD( "nb1ic30",   0x0000, 0x0800, 0x3f482fa3 );
            ROM_LOAD( "nb2ic29",   0x0800, 0x0800, 0x7ddea141 );
            ROM_LOAD( "nb3ic28",   0x1000, 0x0800, 0x8c72a069 );
            ROM_LOAD( "nb4ic27",   0x1800, 0x0800, 0x30feae51 );
            ROM_LOAD( "nb5ic26",   0x2000, 0x0800, 0x05242fd0 );
            ROM_LOAD( "nb6ic25",   0x2800, 0x0800, 0x7a12ffea );
            ROM_LOAD( "nb7ic24",   0x3000, 0x0800, 0x9cc287df );
            ROM_LOAD( "nb8ic23",   0x3800, 0x0800, 0x4d84ff2c );

            ROM_REGION(0x4000);    /* temporary space for graphics (disposed after conversion) */
            ROM_LOAD( "nb11ic48",  0x0000, 0x0800, 0x23271a13 );
            ROM_LOAD( "12.47",     0x0800, 0x0800, 0xef0706c3 );
            ROM_LOAD( "nb9ic50",   0x1000, 0x0800, 0xd6949c27 );
            ROM_LOAD( "10.49",     0x1800, 0x0800, 0xc97c97b9 );
            ROM_LOAD( "15.44",     0x2000, 0x0800, 0xd692f9c7 );
            ROM_LOAD( "16.43",     0x2800, 0x0800, 0xd3ba8b27 );
            ROM_LOAD( "13.46",     0x3000, 0x0800, 0xc1669cd5 );
            ROM_LOAD( "14.45",     0x3800, 0x0800, 0xeef2c8e5 );           
            ROM_END();
        }};


      static HiscoreLoadPtr hiload = new HiscoreLoadPtr()
      {
        public int handler()
        {
           /* get RAM pointer (this game is multiCPU, we can't assume the global */
           /* RAM pointer is pointing to the right place) */
          char []RAM = Machine.memory_region[0];
           /* check if the hi score has already been written to screen */
 /*TOFIX          if((RAM[0x874a] == 8) && (RAM[0x8746] == 9) && (RAM[0x8742] == 7) && (RAM[0x873e] == 8) &&  /* HIGH */
 /*TOFIX             (RAM[0x8743] == 0x20) && (RAM[0x873f] == 0x20) && (RAM[0x873b] == 0x20) && (RAM[0x8737] == 0x20))
           {

            FILE f;
            if ((f = fopen(name, "rb")) != null)
            {
              char[] buf = new char[10];

              fread(RAM, 0x4003, 1, 4, f);
             /* also copy the high score to the screen, otherwise it won't be */
             /* updated */
     /*TOFIX       int hi = (RAM[0x4006] & 0x0f) +
                  (RAM[0x4006] >> 4) * 10 +
                  (RAM[0x4005] & 0x0f) * 100 +
                  (RAM[0x4005] >> 4) * 1000 +
                  (RAM[0x4004] & 0x0f) * 10000 +
                  (RAM[0x4004] >> 4) * 100000 +
                  (RAM[0x4003] & 0x0f) * 1000000 +
                  (RAM[0x4003] >> 4) * 10000000;
   /*TOFIX           if (hi != 0)
              {
                     sprintf(buf, "%8d", new Object[] { Integer.valueOf(hi) });
                     if (buf[2] != ' ') videoram_w.handler(0x0743,buf[2]-'0'+0x20);
                     if (buf[3] != ' ') videoram_w.handler(0x073f,buf[3]-'0'+0x20);
                     if (buf[4] != ' ') videoram_w.handler(0x073b,buf[4]-'0'+0x20);
                     if (buf[5] != ' ') videoram_w.handler(0x0737,buf[5]-'0'+0x20);
                     if (buf[6] != ' ') videoram_w.handler(0x0733,buf[6]-'0'+0x20);
                     if (buf[7] != ' ') videoram_w.handler(0x072f,buf[7]-'0'+0x20);
              }
              fclose(f);
            }

            return 1;
          }*/
          return 0;
        }
      };

    static long get_score(CharPtr score)
    {
    return score.read(3) + (154 * score.read(2)) + (39322L * score.read(1)) + (39322L *154 * score.read(0));
    }
    static HiscoreSavePtr hisave = new HiscoreSavePtr()
      {
        public void handler()
        {
          char []RAM = Machine.memory_region[0];
          int offset = 0;//we can't write directly to RAM so we store the offset (shadow)

   /*TOFIX       long score1 = naughtyb.get_score(new CharPtr(RAM, 0x4020));
          long score2 = naughtyb.get_score(new CharPtr(RAM, 0x4030));
          long hiscore = naughtyb.get_score(new CharPtr(RAM, 0x4003));

          if (score1 > hiscore) offset += 0x4020;
            else if (score2 > hiscore) offset += 0x4030;
          else offset += 0x4003;

          FILE f;
          if ((f = fopen(name, "wb")) != null)
          {
            fwrite(RAM, offset, 1, 4, f);
            fclose(f);
          }*/
        }
      };

    public static GameDriver naughtyc_driver = new GameDriver
    (
            "Naughty Boy",
            "naughtyc",
            "Sal and John Bugliarisi (MAME driver)\nMirko Buffoni (additional code)\nNicola Salmoria (additional code)",
            machine_driver,

            naughtyc_rom,
            null, null,
            null,

            null,input_ports,null,null,null, 

            null, palette, colortable,
            ORIENTATION_DEFAULT,

            hiload, hisave
    );
    static RomLoadPtr popflame_rom= new RomLoadPtr(){ public void handler()  
    {
                ROM_REGION(0x10000);	/* 64k for code */
                ROM_LOAD( "ic86.pop", 0x0000, 0x1000, 0x3874201e );
                ROM_LOAD( "ic80.pop", 0x1000, 0x1000, 0x62c9e875 );
                ROM_LOAD( "ic94.pop", 0x2000, 0x1000, 0xef0139a5 );
                ROM_LOAD( "ic100.pop", 0x3000, 0x1000, 0x782f7f4d );

                ROM_REGION(0x4000);	/* temporary space for graphics (disposed after conversion) */
                ROM_LOAD( "ic29.pop", 0x0000, 0x1000, 0x8244c820 );
                ROM_LOAD( "ic38.pop", 0x1000, 0x1000, 0x19ed84eb );
                ROM_LOAD( "ic3.pop", 0x2000, 0x1000, 0xde7c7614 );
                ROM_LOAD( "ic13.pop", 0x3000, 0x1000, 0xb40c561a );
                ROM_END();
     }};


        public static GameDriver popflame_driver = new GameDriver
        (
                "Pop Flamer",
                "popflame",
                "Brad Oliver (MAME driver)\nSal and John Bugliarisi (Naughty Boy driver)\nMirko Buffoni (additional code)\nNicola Salmoria (additional code)",
                machine_driver,

                popflame_rom,
                null, null,
                null,

                null/*TBR*/,input_ports,null/*TBR*/,null/*TBR*/,null/*TBR*/,

                null, palette, colortable,
                ORIENTATION_DEFAULT,

                null, null
        );
}
