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
 *
 *  using 0.36 romset
 */
package drivers;

import static arcadeflex.libc.*;
import static arcadeflex.osdepend.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static sndhrdw.generic.*;
import static machine.williams.*;
import static vidhrdw.williams.*;
import static sndhrdw.williams.*;
import static mame.usrintrfH.*;
import static mame.usrintrf.*;
import static mame.inptport.*;

public class williams {
            /*	JB 970823 - separate Stargate for busy loop optimization
         *   Read mem for Stargate
         */

        static MemoryReadAddress stargate_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x97ff, williams_videoram_r ),
                new MemoryReadAddress( 0x9c39, 0x9c39, stargate_catch_loop_r ), /* JB 970823 */
                new MemoryReadAddress( 0x9800, 0xbfff, MRA_RAM ),
                new MemoryReadAddress( 0xc804, 0xc804, input_port_0_r ),     /* IN0 */
                new MemoryReadAddress( 0xc806, 0xc806, input_port_1_r ),     /* IN1 */
                new MemoryReadAddress( 0xc80c, 0xc80c, input_port_2_r ),     /* IN2 */
                new MemoryReadAddress( 0xc80e, 0xc80e, MRA_RAM ),            /* not used? */
                new MemoryReadAddress( 0xcb00, 0xcb00, video_counter_r ),
                new MemoryReadAddress( 0xCC00, 0xCFFF, MRA_RAM ),            /* CMOS */
                new MemoryReadAddress( 0xd000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };


        /*	JB 970823 - separate Robotron for busy loop optimization
         *   Read mem for Robotron
         */

        static MemoryReadAddress robotron_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x97ff, williams_videoram_r ),
                new MemoryReadAddress( 0x9810, 0x9810, robotron_catch_loop_r ), /* JB 970823 */
                new MemoryReadAddress( 0x9800, 0xbfff, MRA_RAM ),
                new MemoryReadAddress( 0xc804, 0xc804, input_port_0_r ),     /* IN0 */
                new MemoryReadAddress( 0xc806, 0xc806, input_port_1_r ),     /* IN1 */
                new MemoryReadAddress( 0xc80c, 0xc80c, input_port_2_r ),     /* IN2 */
                new MemoryReadAddress( 0xc80e, 0xc80e, MRA_RAM ),            /* not used? */
                new MemoryReadAddress( 0xcb00, 0xcb00, video_counter_r ),
                new MemoryReadAddress( 0xCC00, 0xCFFF, MRA_RAM ),            /* CMOS */
                new MemoryReadAddress( 0xd000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };

        /*
         *   Read mem for Robotron Stargate Bubbles Splat
         */

        static MemoryReadAddress readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x97ff, williams_videoram_r ),
                new MemoryReadAddress( 0x9800, 0xbfff, MRA_RAM ),
                new MemoryReadAddress( 0xc804, 0xc804, input_port_0_r ),     /* IN0 */
                new MemoryReadAddress( 0xc806, 0xc806, input_port_1_r ),     /* IN1 */
                new MemoryReadAddress( 0xc80c, 0xc80c, input_port_2_r ),     /* IN2 */
                new MemoryReadAddress( 0xc80e, 0xc80e, MRA_RAM ),            /* not used? */
                new MemoryReadAddress( 0xcb00, 0xcb00, video_counter_r ),
                new MemoryReadAddress( 0xCC00, 0xCFFF, MRA_RAM ),            /* CMOS */
                new MemoryReadAddress( 0xd000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };


        /*
         *   Write mem for Robotron Joust Stargate Bubbles
         */

        static MemoryWriteAddress writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x97ff, williams_videoram_w ),
                new MemoryWriteAddress( 0x9800, 0xbfff, MWA_RAM ),
                new MemoryWriteAddress( 0xc000, 0xc00f, Williams_Palette_w ),
                new MemoryWriteAddress( 0xc80e, 0xc80e, williams_sh_w),            /* Sound */
                new MemoryWriteAddress( 0xc900, 0xc900, MWA_RAM ),                 /* bank  */
                new MemoryWriteAddress( 0xca00, 0xca00, williams_StartBlitter ),   /* StartBlitter */
                new MemoryWriteAddress( 0xca01, 0xca07, MWA_RAM ),                 /* Blitter      */
                new MemoryWriteAddress( 0xcbff, 0xcbff, MWA_NOP ),                 /* WatchDog (have to be $39) */
                new MemoryWriteAddress( 0xCC00, 0xCFFF, MWA_RAM ),                 /* CMOS                      */
                new MemoryWriteAddress( 0xd000, 0xffff, MWA_ROM ),
                new MemoryWriteAddress( -1 )  /* end of table */
        };


        /*
         *   Read mem for Joust
         */

        static MemoryReadAddress joust_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x97ff, williams_videoram_r ),
                new MemoryReadAddress( 0x9800, 0xbfff, MRA_RAM ),
                new MemoryReadAddress( 0xc804, 0xc804, input_port_0_1 ),          /* IN0-1 */
                new MemoryReadAddress( 0xc806, 0xc806, input_port_2_r ),          /* IN2 */
                new MemoryReadAddress( 0xc80c, 0xc80c, input_port_3_r ),          /* IN3 */
                new MemoryReadAddress( 0xc80e, 0xc80e, MRA_RAM ),                 /* not used? */
                new MemoryReadAddress( 0xcb00, 0xcb00, video_counter_r ),
                new MemoryReadAddress( 0xCC00, 0xCFFF, MRA_RAM ),                 /* CMOS */
               // new MemoryReadAddress( 0xe0f2, 0xe0f2, joust_catch_loop_r ),		/* JB 970823 */
                new MemoryReadAddress( 0xd000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };


        /*
         *   Read mem for Splat
         */

        static MemoryReadAddress splat_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x97ff, williams_videoram_r ),
                new MemoryReadAddress( 0x984b, 0x984b, splat_catch_loop_r ),		/* JB 970823 */
                new MemoryReadAddress( 0x9800, 0xbfff, MRA_RAM ),
                new MemoryReadAddress( 0xc804, 0xc804, input_port_0_1 ),          /* IN0-1 */
                new MemoryReadAddress( 0xc806, 0xc806, input_port_2_3 ),          /* IN2-3*/
                new MemoryReadAddress( 0xc80c, 0xc80c, input_port_4_r ),          /* IN4 */
                new MemoryReadAddress( 0xc80e, 0xc80e, MRA_RAM ),                 /* not used? */
                new MemoryReadAddress( 0xcb00, 0xcb00, video_counter_r ),
                new MemoryReadAddress( 0xCC00, 0xCFFF, MRA_RAM ),                 /* CMOS */
                new MemoryReadAddress( 0xd000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( -1 ) /* end of table */
        };


        /*
         *   Write mem for Splat
         */

        static MemoryWriteAddress splat_writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x97ff, williams_videoram_w ),
                new MemoryWriteAddress( 0x9800, 0xbfff, MWA_RAM ),
                new MemoryWriteAddress( 0xc000, 0xc00f, Williams_Palette_w ),
                new MemoryWriteAddress( 0xc80e, 0xc80e, williams_sh_w),            /* Sound */
                new MemoryWriteAddress( 0xc900, 0xc900, MWA_RAM ),                 /* bank  */
                new MemoryWriteAddress( 0xca00, 0xca00, williams_StartBlitter2 ),  /* StartBlitter */
                new MemoryWriteAddress( 0xca01, 0xca07, MWA_RAM ),                 /* Blitter      */
                new MemoryWriteAddress( 0xcbff, 0xcbff, MWA_NOP ),                 /* WatchDog     */
                new MemoryWriteAddress( 0xCC00, 0xCFFF, MWA_RAM ),                 /* CMOS         */
                new MemoryWriteAddress( 0xd000, 0xffff, MWA_ROM ),
                new MemoryWriteAddress( -1 )  /* end of table */
        };


        /*
         *   Read mem for Sinistar
         */

        static MemoryReadAddress sinistar_readmem[] =
        {
                new MemoryReadAddress( 0x0000, 0x97ff, williams_videoram_r ),
                new MemoryReadAddress( 0x9800, 0xbfff, MRA_RAM ),
                new MemoryReadAddress( 0xc804, 0xc804, input_port_0_r ),          /* IN0 */
                new MemoryReadAddress( 0xc806, 0xc806, input_port_1_r ),          /* IN1 */
                new MemoryReadAddress( 0xc80c, 0xc80c, input_port_2_r ),          /* IN2 */
                new MemoryReadAddress( 0xc80e, 0xc80e, MRA_RAM ),                 /* not used? */
                new MemoryReadAddress( 0xcb00, 0xcb00, video_counter_r ),
                new MemoryReadAddress( 0xCC00, 0xCFFF, MRA_RAM ),                 /* CMOS */
                new MemoryReadAddress( 0xd000, 0xdfff, MRA_RAM ),                 /* for Sinistar */
                new MemoryReadAddress( 0xe000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };


        /*
         *   Write mem for Sinistar
         */

        static MemoryWriteAddress sinistar_writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x97ff, williams_videoram_w ),
                new MemoryWriteAddress( 0x9800, 0xbfff, MWA_RAM ),
                new MemoryWriteAddress( 0xc000, 0xc00f, Williams_Palette_w ),
                new MemoryWriteAddress( 0xc900, 0xc900, MWA_RAM ),                 /* bank         */
                new MemoryWriteAddress( 0xca00, 0xca00, williams_StartBlitter ),   /* StartBlitter */
                new MemoryWriteAddress( 0xca01, 0xca07, MWA_RAM ),                 /* Blitter      */
                new MemoryWriteAddress( 0xcbff, 0xcbff, MWA_NOP ),                 /* WatchDog     */
                new MemoryWriteAddress( 0xCC00, 0xCFFF, MWA_RAM ),                 /* CMOS         */
                new MemoryWriteAddress( 0xd000, 0xdfff, MWA_RAM ),                 /* for Sinistar */
                new MemoryWriteAddress( 0xe000, 0xffff, MWA_ROM ),
                new MemoryWriteAddress( -1 )  /* end of table */
        };


        /*
         *   Read mem for Blaster
         */

        static MemoryReadAddress blaster_readmem[] =
        {
                 new MemoryReadAddress( 0x0000, 0x96ff, blaster_videoram_r ),
                 new MemoryReadAddress( 0x9700, 0xbfff, MRA_RAM ),
                 new MemoryReadAddress( 0xc804, 0xc804, blaster_input_port_0 ),    /* IN0 */
                 new MemoryReadAddress( 0xc806, 0xc806, input_port_1_r ),          /* IN1 */
                 new MemoryReadAddress( 0xc80c, 0xc80c, input_port_2_r ),          /* IN2 */
                 new MemoryReadAddress( 0xc80e, 0xc80e, MRA_RAM ),                 /* not used? */
                 new MemoryReadAddress( 0xcb00, 0xcb00, video_counter_r ),
                 new MemoryReadAddress( 0xCC00, 0xCFFF, MRA_RAM ),                 /* CMOS      */
                 new MemoryReadAddress( 0xd000, 0xffff, MRA_ROM ),
                 new MemoryReadAddress( -1 )  /* end of table */
        };


        /*
         *   Write mem for Blaster
         */

        static MemoryWriteAddress blaster_writemem[] =
        {
                new MemoryWriteAddress( 0x0000, 0x96ff, blaster_videoram_w ),
                new MemoryWriteAddress( 0x9700, 0xbfff, MWA_RAM ),
                new MemoryWriteAddress( 0xc000, 0xc00f, Williams_Palette_w ),
                new MemoryWriteAddress( 0xc80e, 0xc80e, williams_sh_w),                 /* Sound */
                new MemoryWriteAddress( 0xc900, 0xc900, MWA_RAM ),                      /* bank  */
                new MemoryWriteAddress( 0xc940, 0xc940, MWA_RAM ),                      /* select remap colors in Remap prom */
                new MemoryWriteAddress( 0xc980, 0xc980, blaster_bank_select_w ),        /* Bank Select */
        /*        { 0xc9c0, 0xc9c0, MWA_RAM }, */
                new MemoryWriteAddress( 0xca00, 0xca00, williams_BlasterStartBlitter ), /* StartBlitter */
                new MemoryWriteAddress( 0xca01, 0xca07, MWA_RAM ),                      /* Blitter      */
                new MemoryWriteAddress( 0xcbff, 0xcbff, MWA_NOP ),                      /* WatchDog     */
                new MemoryWriteAddress( 0xCC00, 0xCFFF, MWA_RAM ),                      /* CMOS         */
                new MemoryWriteAddress( 0xd000, 0xffff, MWA_ROM ),
                new MemoryWriteAddress( -1 )  /* end of table */
        };


        /*
         *   Read mem for Defender
         */

        static MemoryReadAddress defender_readmem[] =
        {
                new MemoryReadAddress( 0xa05d, 0xa05d, defender_catch_loop_r ), /* JB 970823 */
                new MemoryReadAddress( 0x0000, 0xbfff, MRA_RAM ),
                new MemoryReadAddress( 0xc000, 0xcfff, defender_bank_r ),
                new MemoryReadAddress( 0xd000, 0xffff, MRA_ROM ),
                new MemoryReadAddress( -1 )  /* end of table */
        };


        /*
         *   Write mem for Defender
         */

        static MemoryWriteAddress defender_writemem[] =
        {
                 new MemoryWriteAddress( 0x0000, 0x97ff, defender_videoram_w ),
                 new MemoryWriteAddress( 0x9800, 0xbfff, MWA_RAM ),
                 new MemoryWriteAddress( 0xc000, 0xcfff, defender_bank_w ),
                 new MemoryWriteAddress( 0xd000, 0xd000, defender_bank_select_w ),       /* Bank Select */
                 new MemoryWriteAddress( 0xd001, 0xffff, MWA_ROM ),
                 new MemoryWriteAddress( -1 ) /* end of table */
        };




        /* Dip switch for all games
            -There is no Dip switches on the Williams games
              but this has to be here because
              the program will bug when TAB is pressed */

        static DSW williams_dsw[] =
        {
                new DSW( 2, 0x80, "NO DIP SWITCHES", new String[]{ "" } ),
                new DSW( -1 )
        };



        /*
         *   Robotron buttons
         */

        static InputPort robotron_input_ports[] =
        {
                 new InputPort(       /* IN0 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_E, OSD_KEY_D, OSD_KEY_S, OSD_KEY_F, OSD_KEY_1, OSD_KEY_2, OSD_KEY_UP, OSD_KEY_DOWN }
                ),
                new InputPort(      /* IN1 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_LEFT, OSD_KEY_RIGHT, 0,0,0,0,0,0 }
                ),
                new InputPort(       /* IN2 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_5, OSD_KEY_6, OSD_KEY_3, OSD_KEY_7, 0,0,0,0 }
                ),
                new InputPort( -1 )  /* end of table */
        };


        static TrakPort trak_ports[] =
        {
                 new TrakPort(-1)
         };


        static KEYSet robotron_keys[] =
        {
          new KEYSet( 0, 0, "MOVE UP" ),
          new KEYSet( 0, 1, "MOVE DOWN" ),
          new KEYSet( 0, 2, "MOVE LEFT" ),
          new KEYSet( 0, 3, "MOVE RIGHT" ),
          new KEYSet( 0, 6, "FIRE UP" ),
          new KEYSet( 0, 7, "FIRE DOWN" ),
          new KEYSet( 1, 0, "FIRE LEFT" ),
          new KEYSet( 1, 1, "FIRE RIGHT" ),
        /*
          { 0, 4, "1 PLAYER" },
          { 0, 5, "2 PLAYERS" },
          { 2, 2, "COIN IN" },
        */
          new KEYSet( 2, 0, "AUTO UP" ),
          new KEYSet( 2, 1, "ADVANCE" ),
          new KEYSet( 2, 3, "HIGHSCORE RESET" ),
          new KEYSet( -1 )
        };



        /*
         *   Joust buttons
         */

        static InputPort joust_input_ports[] =
        {
                 new InputPort(       /* IN0 Player 2 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_F, OSD_KEY_G, OSD_KEY_S, 0, OSD_KEY_2, OSD_KEY_1, 0, 0 }
                ),
                new InputPort(       /* IN0 Player 1 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_LEFT, OSD_KEY_RIGHT, OSD_KEY_CONTROL, 0, 0, 0, 0, 0 }
                ),
                new InputPort(       /* IN2 */
                        0x00,   /* default_value */
                        new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(       /* IN3 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_5, OSD_KEY_6, OSD_KEY_3, OSD_KEY_7, 0,0,0,0 }
                ),
                new InputPort( -1 )  /* end of table */
        };

        static KEYSet joust_keys[] =
        {
          new KEYSet( 1, 0, "PLAYER 1 MOVE LEFT" ),
          new KEYSet( 1, 1, "PLAYER 1 MOVE RIGHT" ),
          new KEYSet( 1, 2, "PLAYER 1 FLAP" ),
          new KEYSet( 0, 0, "PLAYER 2 MOVE LEFT" ),
          new KEYSet( 0, 1, "PLAYER 2 MOVE RIGHT" ),
          new KEYSet( 0, 2, "PLAYER 2 FLAP" ),
        /*
          { 0, 5, "1 PLAYER" },
          { 0, 4, "2 PLAYERS" },
          { 3, 2, "COIN IN" },
        */
          new KEYSet( 3, 0, "AUTO UP" ),
          new KEYSet( 3, 1, "ADVANCE" ),
          new KEYSet( 3, 3, "HIGHSCORE RESET" ),
          new KEYSet( -1 )
        };


        /*
         *   Stargate buttons
         */

        static InputPort stargate_input_ports[] =
        {
                 new InputPort(       /* IN0 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_CONTROL, OSD_KEY_ALT, OSD_KEY_X, OSD_KEY_C, OSD_KEY_2, OSD_KEY_1, OSD_KEY_LEFT, OSD_KEY_DOWN } 
                ),
                new InputPort(       /* IN1 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_UP, OSD_KEY_V, 0, 0, 0, 0, 0, 0 }
                ),
                new InputPort(       /* IN2 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_5, OSD_KEY_6, OSD_KEY_3, OSD_KEY_7, 0,0,0,0 }
                ),
                new InputPort( -1 )  /* end of table */
        };

        static KEYSet stargate_keys[] =
        {
          new KEYSet( 1, 0, "MOVE UP" ),
          new KEYSet( 0, 7, "MOVE DOWN" ),
          new KEYSet( 0, 6, "REVERSE" ),
          new KEYSet( 0, 0, "FIRE" ),
          new KEYSet( 0, 1, "THRUST" ),
          new KEYSet( 0, 2, "SMART BOMB" ),
          new KEYSet( 1, 1, "INVISO" ),
          new KEYSet( 0, 3, "HYPERSPACE" ),
        /*
          { 0, 5, "1 PLAYER" },
          { 0, 4, "2 PLAYERS" },
          { 2, 2, "COIN IN" },
        */
          new KEYSet( 2, 0, "AUTO UP" ),
          new KEYSet( 2, 1, "ADVANCE" ),
          new KEYSet( 2, 3, "HIGHSCORE RESET" ),
          new KEYSet( -1 )
        };



        /*
         *   Defender buttons
         */

        static InputPort defender_input_ports[] =
        {
                 new InputPort(       /* IN0 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_5, OSD_KEY_6, OSD_KEY_3, OSD_KEY_7, 0, 0, 0, 0 }
                ),
                new InputPort(       /* IN1 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_CONTROL, OSD_KEY_ALT, OSD_KEY_X, OSD_KEY_C, OSD_KEY_2, OSD_KEY_1, OSD_KEY_LEFT, OSD_KEY_DOWN }
                ),
                new InputPort(       /* IN2 */
                        0x00,   /* default_value */
                       new int[] { OSD_KEY_UP, 0,0,0,0,0,0,0 }
                ),
                new InputPort( -1 )  /* end of table */
        };

        static KEYSet defender_keys[] =
        {
          new KEYSet( 2, 0, "MOVE UP" ),
          new KEYSet( 1, 7, "MOVE DOWN" ),
          new KEYSet( 1, 6, "REVERSE" ),
          new KEYSet( 1, 0, "FIRE" ),
          new KEYSet( 1, 1, "THRUST" ),
          new KEYSet( 1, 2, "SMART BOMB" ),
          new KEYSet( 1, 3, "HYPERSPACE" ),
        /*
          { 1, 5, "1 PLAYER" },
          { 1, 4, "2 PLAYERS" },
          { 0, 2, "COIN IN" },
        */
          new KEYSet( 0, 0, "AUTO UP" ),
          new KEYSet( 0, 1, "ADVANCE" ),
          new KEYSet( 0, 3, "HIGHSCORE RESET" ),
          new KEYSet( -1 )
        };



        /*
         *   Sinistar buttons
         */

        static InputPort sinistar_input_ports[] =
        {
                 new InputPort(       /* IN0 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_1, OSD_KEY_2, OSD_KEY_3, OSD_KEY_4, OSD_KEY_5, OSD_KEY_6, OSD_KEY_7, OSD_KEY_8 }
                ),
                new InputPort(       /* IN1 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_Q, OSD_KEY_W, OSD_KEY_E, OSD_KEY_R, OSD_KEY_T, OSD_KEY_Y, OSD_KEY_U, OSD_KEY_I }
                ),
                new InputPort(       /* IN2 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_A, OSD_KEY_S, OSD_KEY_D, OSD_KEY_F, OSD_KEY_G, OSD_KEY_H, OSD_KEY_J, OSD_KEY_K }
                ),
                new InputPort( -1 )  /* end of table */
        };



        /*
         *   Bubbles buttons
         */

        static InputPort bubbles_input_ports[] =
        {
                new InputPort(       /* IN0 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_1, OSD_KEY_2, OSD_KEY_3, OSD_KEY_4, OSD_KEY_5, OSD_KEY_6, OSD_KEY_7, OSD_KEY_8 }
                ),
                new InputPort(       /* IN1 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_Q, OSD_KEY_W, OSD_KEY_E, OSD_KEY_R, OSD_KEY_T, OSD_KEY_Y, OSD_KEY_U, OSD_KEY_I }
                ),
                new InputPort(       /* IN2 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_A, OSD_KEY_S, OSD_KEY_D, OSD_KEY_F, OSD_KEY_G, OSD_KEY_H, OSD_KEY_J, OSD_KEY_K }
                ),
                new InputPort( -1 )  /* end of table */
        };



        /*
         *   Splat buttons
         */

        static InputPort splat_input_ports[] =
        {
                 new InputPort(       /* IN0 player 2*/
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_I, OSD_KEY_K, OSD_KEY_J, OSD_KEY_L, OSD_KEY_1, OSD_KEY_2, OSD_KEY_UP, OSD_KEY_DOWN }
                ),
                new InputPort(       /* IN0 player 1*/
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_W, OSD_KEY_S, OSD_KEY_A, OSD_KEY_D, 0, 0, OSD_KEY_T, OSD_KEY_G }
                ),
                new InputPort(       /* IN1 player 2*/
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_LEFT, OSD_KEY_RIGHT, 0,0,0,0,0,0 }
                ),
                new InputPort(       /*  IN1 player1*/
                        0x00,  /*  default_value */
                        new int[]{ OSD_KEY_F, OSD_KEY_H, 0 ,0, 0, 0, 0, 0 }
                ),
                new InputPort(       /* IN2 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_5, OSD_KEY_6, OSD_KEY_3, OSD_KEY_7, 0,0,0,0 }
                ),
                new InputPort( -1 )  /* end of table */
        };

        static KEYSet splat_keys[] =
        {
          new KEYSet( 1, 0, "PL1 WALK UP" ),
          new KEYSet( 1, 1, "PL1 WALK DOWN" ),
          new KEYSet( 1, 2, "PL1 WALK LEFT" ),
          new KEYSet( 1, 3, "PL1 WALK RIGHT" ),
          new KEYSet( 1, 6, "PL1 THROW UP" ),
          new KEYSet( 1, 7, "PL1 THROW DOWN" ),
          new KEYSet( 3, 0, "PL1 THROW LEFT" ),
          new KEYSet( 3, 1, "PL1 THROW RIGHT" ),
          new KEYSet( 0, 0, "PL2 WALK UP" ),
          new KEYSet( 0, 1, "PL2 WALK DOWN" ),
          new KEYSet( 0, 2, "PL2 WALK LEFT" ),
          new KEYSet( 0, 3, "PL2 WALK RIGHT" ),
          new KEYSet( 0, 6, "PL2 THROW UP" ),
          new KEYSet( 0, 7, "PL2 THROW DOWN" ),
          new KEYSet( 2, 0, "PL2 THROW LEFT" ),
          new KEYSet( 2, 1, "PL2 THROW RIGHT" ),
        /*
          { 1, 4, "1 PLAYER" },
          { 1, 5, "2 PLAYERS" },
          { 4, 2, "COIN IN" },
        */
          new KEYSet( 4, 0, "AUTO UP" ),
          new KEYSet( 4, 1, "ADVANCE" ),
          new KEYSet( 4, 3, "HIGHSCORE RESET" ),
          new KEYSet( -1 )
        };


        /*
         *   Blaster buttons
         */

        static InputPort blaster_input_ports[] =
        {
                 new InputPort(       /* IN0 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_UP, OSD_KEY_DOWN, OSD_KEY_LEFT, OSD_KEY_RIGHT, 0,0,0,0 }
                ),
                new InputPort(       /* IN1 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_ALT, OSD_KEY_CONTROL, OSD_KEY_Z, 0, OSD_KEY_1, OSD_KEY_2, 0,0 }
                ),
                new InputPort(       /* IN2 */
                        0x00,   /* default_value */
                        new int[]{ OSD_KEY_5, OSD_KEY_6, OSD_KEY_3, OSD_KEY_7, 0,0,0,0 }
                ),
                new InputPort( -1 )  /* end of table */
        };

        static KEYSet blaster_keys[] =
        {
          new KEYSet( 0, 0, "MOVE UP" ),
          new KEYSet( 0, 1, "MOVE DOWN" ),
          new KEYSet( 0, 2, "MOVE LEFT" ),
          new KEYSet( 0, 3, "MOVE RIGHT" ),
          new KEYSet( 1, 1, "BLAST" ),
          new KEYSet( 1, 0, "THRUST PANEL" ),
          new KEYSet( 1, 2, "THRUST JOYSTICK" ),
        /*
          { 1, 4, "1 PLAYER" },
          { 1, 5, "2 PLAYERS" },
          { 2, 2, "COIN IN" },
        */
          new KEYSet( 2, 0, "AUTO UP" ),
          new KEYSet( 2, 1, "ADVANCE" ),
          new KEYSet( 2, 3, "HIGHSCORE RESET" ),
          new KEYSet( -1 )
        };


        /* To do... */
        static KEYSet keys[] =
        {
         new KEYSet( 4, 0, "MOVE UP" ),
         new KEYSet( -1 )
        };

/* there's nothing here, this is just a placeholder to let the video hardware */
/* pick the background color table. */
        static GfxLayout fakelayout = new GfxLayout(
        
                1,1,
                0,
                4,	/* 4 bits per pixel */
                new int[]{ 0 },
                new int[]{ 0 },
                new int[]{ 0 },
                0
        );


        static GfxDecodeInfo gfxdecodeinfo[] =
        {
                new GfxDecodeInfo( 0, 0, fakelayout, 0, 1 ),
                new GfxDecodeInfo( -1 ) /* end of array */
        };
        static MachineDriver robotron_machine_driver = new MachineDriver
        (
                /* basic machine hardware  */
                new MachineCPU[]
                {
                    new MachineCPU(
                                CPU_M6809,
                                1000000,                /* ? Mhz */
                                0,                      /* memory region */
                                robotron_readmem,                /* MemoryReadAddress */
                                writemem,               /* MemoryWriteAddress */
                                null,                      /* IOReadPort */
                                null,                      /* IOWritePort */
                                Williams_Interrupt,     /* interrupt routine */
                                4                       /* interrupts per frame (should be 4ms) */
                        )
                },
                60,                                     /* frames per second */
                williams_init_machine,          /* init machine routine */

                /* video hardware */
                304, 240,                               /* screen_width, screen_height */
                new rectangle( 0, 304-1, 0, 256-1 ),                 /* struct rectangle visible_area */
                gfxdecodeinfo,                         /* GfxDecodeInfo * */
                1+16,                                  /* total colors */
                16,                                      /* color table length */
                williams_vh_convert_color_prom,         /* convert color prom routine */
                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE, 
                null,                                     /* vh_init routine */
                williams_vh_start,                      /* vh_start routine */
                williams_vh_stop,                       /* vh_stop routine */
                williams_vh_screenrefresh,              /* vh_update routine */

                /* sound hardware */
                null,                                      /* pointer to samples */
                null,                                      /* sh_init routine */
                null,                                      /* sh_start routine */
                null,                                      /* sh_stop routine */
                williams_sh_update                      /* sh_update routine */
        );



        static MachineDriver joust_machine_driver = new MachineDriver
        (

                /* basic machine hardware  */
                new MachineCPU[]
                {
                    new MachineCPU(
                                CPU_M6809,
                                1000000,                /* ? Mhz */
                                0,                      /* memory region */
                                joust_readmem,          /* MemoryReadAddress */
                                writemem,               /* MemoryWriteAddress */
                                null,                      /* IOReadPort */
                                null,                      /* IOWritePort */
                                Williams_Interrupt,     /* interrupt routine */
                                4                       /* interrupts per frame (should be 4ms) */
                        )
                },
                60,                                     /* frames per second */
                williams_init_machine,          /* init machine routine */

                /* video hardware */
                304, 240,                               /* screen_width, screen_height */
                new rectangle( 0, 304-1, 0, 256-1 ),                 /* struct rectangle visible_area */
                 gfxdecodeinfo,                          /* GfxDecodeInfo * */
                    1+16,                                  /* total colors */
                    16,                                      /* color table length */
                    williams_vh_convert_color_prom,         /* convert color prom routine */

                    VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
                null,                                      /* vh_init routine */
                williams_vh_start,                      /* vh_start routine */
                williams_vh_stop,                       /* vh_stop routine */
                williams_vh_screenrefresh,              /* vh_update routine */

                /* sound hardware */
                null,                                      /* pointer to samples */
                null,                                      /* sh_init routine */
                null,                                      /* sh_start routine */
                null,                                      /* sh_stop routine */
                williams_sh_update                      /* sh_update routine */
        );



        static MachineDriver stargate_machine_driver = new MachineDriver
        (
                /* basic machine hardware  */
                new MachineCPU[]
                {
                    new MachineCPU(
                                CPU_M6809,
                                1000000,                /* ? Mhz */ /*Stargate do not like 1 mhz*/
                                0,                      /* memory region */
                                stargate_readmem,                /* MemoryReadAddress */
                                writemem,               /* MemoryWriteAddress */
                                null,                      /* IOReadPort */
                                null,                      /* IOWritePort */
                                Stargate_Interrupt,     /* interrupt routine */
                                2                       /* interrupts per frame (should be 4ms) */
                        )
                },
                60,                                     /* frames per second */
                williams_init_machine,          /* init machine routine */

                /* video hardware */
                304, 240,                               /* screen_width, screen_height */
                new rectangle( 0, 304-1, 0, 256-1 ),                 /* struct rectangle visible_area */
                gfxdecodeinfo,                          /* GfxDecodeInfo * */
                1+16,                                  /* total colors */
                16,                                      /* color table length */
                williams_vh_convert_color_prom,         /* convert color prom routine */

                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,

                null,                                      /* vh_init routine */
                williams_vh_start,                      /* vh_start routine */
                williams_vh_stop,                       /* vh_stop routine */
                williams_vh_screenrefresh,              /* vh_update routine */

                /* sound hardware */
                null,                                      /* pointer to samples */
                null,                                      /* sh_init routine */
                null,                                      /* sh_start routine */
                null,                                      /* sh_stop routine */
                williams_sh_update                      /* sh_update routine */
        );



        static MachineDriver sinistar_machine_driver = new MachineDriver
        (

                /* basic machine hardware  */
                new MachineCPU[]
                {
                    new MachineCPU(
                                CPU_M6809,
                                1000000,                /* ? Mhz */ /*Sinistar do not like 1 mhz*/
                                0,                      /* memory region */
                                sinistar_readmem,       /* MemoryReadAddress */
                                sinistar_writemem,      /* MemoryWriteAddress */
                                null,                      /* IOReadPort */
                                null,                      /* IOWritePort */
                                Williams_Interrupt,     /* interrupt routine */
                                4                       /* interrupts per frame (should be 4ms) */
                        )
                },
                60,                                     /* frames per second */
                williams_init_machine,          /* init machine routine */

                /* video hardware */
                304, 240,                               /* screen_width, screen_height */
                new rectangle( 0, 304-1, 0, 256-1 ),                 /* struct rectangle visible_area */
                gfxdecodeinfo,                          /* GfxDecodeInfo * */
                1+16,                                  /* total colors */
                16,                                      /* color table length */
                williams_vh_convert_color_prom,         /* convert color prom routine */

                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,

                null,                                      /* vh_init routine */
                williams_vh_start,                      /* vh_start routine */
                williams_vh_stop,                       /* vh_stop routine */
                williams_vh_screenrefresh,              /* vh_update routine */

                /* sound hardware */
                null,                                      /* pointer to samples */
                null,                                      /* sh_init routine */
                null,                                      /* sh_start routine */
                null,                                      /* sh_stop routine */
                null                                       /* sh_update routine */
        );



        static MachineDriver defender_machine_driver = new MachineDriver
        (
                /* basic machine hardware  */
                new MachineCPU[]
                {
                    new MachineCPU(
                                CPU_M6809,
                                1200000,                /* ? Mhz */ /*Defender do not like 1 mhz */
                                0,                      /* memory region */
                                defender_readmem,       /* MemoryReadAddress */
                                defender_writemem,      /* MemoryWriteAddress */
                                null,                      /* IOReadPort */
                                null,                      /* IOWritePort */
                                Defender_Interrupt,     /* interrupt routine */
                                2                       /* interrupts per frame (should be 4ms) */
                        )
                },
                60,                                     /* frames per second */
                williams_nofastop_init_machine,         /* init machine routine */

                /* video hardware */
                304, 240,                               /* screen_width, screen_height */
                new rectangle( 0, 304-1, 0, 256-1 ),                 /* struct rectangle visible_area */
                gfxdecodeinfo,                          /* GfxDecodeInfo * */
                1+16,                                  /* total colors */
                16,                                      /* color table length */
                williams_vh_convert_color_prom,         /* convert color prom routine */

                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,
                null,                                      /* vh_init routine */
                williams_vh_start,                      /* vh_start routine */
                williams_vh_stop,                       /* vh_stop routine */
                 williams_vh_screenrefresh,              /* vh_update routine */

                /* sound hardware */
                null,                                      /* pointer to samples */
                null,                                      /* sh_init routine */
                null,                                      /* sh_start routine */
                null,                                      /* sh_stop routine */
                williams_sh_update                      /* sh_update routine */
        );



        static MachineDriver splat_machine_driver = new MachineDriver
        (
                /* basic machine hardware  */
                new MachineCPU[]
                {
                    new MachineCPU(
                                CPU_M6809,
                                1000000,                /* ? Mhz */
                                0,                      /* memory region */
                                splat_readmem,          /* MemoryReadAddress */
                                splat_writemem,         /* MemoryWriteAddress */
                                null,                      /* IOReadPort */
                                null,                      /* IOWritePort */
                                Williams_Interrupt,     /* interrupt routine */
                                4                       /* interrupts per frame (should be 4ms) */
                        )
                },
                60,                                     /* frames per second */
                williams_init_machine,          /* init machine routine */

                /* video hardware */
                304, 240,                               /* screen_width, screen_height */
                new rectangle( 0, 304-1, 0, 256-1 ),                 /* struct rectangle visible_area */
                gfxdecodeinfo,                          /* GfxDecodeInfo * */
                1+16,                                  /* total colors */
                16,                                      /* color table length */
                williams_vh_convert_color_prom,         /* convert color prom routine */

                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,

                null,                                      /* vh_init routine */
                williams_vh_start,                      /* vh_start routine */
                williams_vh_stop,                       /* vh_stop routine */
                williams_vh_screenrefresh,              /* vh_update routine */

                /* sound hardware */
                null,                                      /* pointer to samples */
                null,                                      /* sh_init routine */
                null,                                      /* sh_start routine */
                null,                                      /* sh_stop routine */
                williams_sh_update                      /* sh_update routine */
       );



        static MachineDriver bubbles_machine_driver = new MachineDriver
        (
                /* basic machine hardware  */
                new MachineCPU[]
                {
                    new MachineCPU(
                                CPU_M6809,
                                1000000,                /* ? Mhz */
                                0,                      /* memory region */
                                readmem,                /* MemoryReadAddress */
                                writemem,               /* MemoryWriteAddress */
                                null,                      /* IOReadPort */
                                null,                      /* IOWritePort */
                                Williams_Interrupt,     /* interrupt routine */
                                4                       /* interrupts per frame (should be 4ms) */
                        )
                },
                60,                                     /* frames per second */
                williams_init_machine,          /* init machine routine */

                /* video hardware */
                304, 240,                                     /* screen_width, screen_height */
                new rectangle( 0, 304-1, 0, 256-1 ),                 /* struct rectangle visible_area */
                 gfxdecodeinfo,                          /* GfxDecodeInfo * */
                1+16,                                  /* total colors */
                16,                                      /* color table length */
                williams_vh_convert_color_prom,         /* convert color prom routine */

                VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,

                null,                                      /* vh_init routine */
                williams_vh_start,                      /* vh_start routine */
                williams_vh_stop,                       /* vh_stop routine */
                williams_vh_screenrefresh,              /* vh_update routine */

                /* sound hardware */
                null,                                      /* pointer to samples */
                null,                                      /* sh_init routine */
                null,                                      /* sh_start routine */
                null,                                      /* sh_stop routine */
                williams_sh_update                      /* sh_update routine */
        );



        static MachineDriver blaster_machine_driver = new MachineDriver
        (
                /* basic machine hardware  */
               new MachineCPU[]
                {
                    new MachineCPU(
                                CPU_M6809,
                                1100000,                /* ? Mhz */
                                0,                      /* memory region */
                                blaster_readmem,        /* MemoryReadAddress */
                                blaster_writemem,       /* MemoryWriteAddress */
                                null,                      /* IOReadPort */
                                null,                      /* IOWritePort */
                                Williams_Interrupt,     /* interrupt routine */
                                4                       /* interrupts per frame (should be 4ms) */
                        )
                },
                60,                                     /* frames per second */
                williams_nofastop_init_machine,          /* init machine routine */

                /* video hardware */
                304, 240,                               /* screen_width, screen_height */
                new rectangle( 0, 304-1, 0, 256-1 ),                 /* struct rectangle visible_area */
                 gfxdecodeinfo,                          /* GfxDecodeInfo * */
                    1+16,                                  /* total colors */
                    16,                                      /* color table length */
                    williams_vh_convert_color_prom,         /* convert color prom routine */

                    VIDEO_TYPE_RASTER|VIDEO_MODIFIES_PALETTE,

                null,                                      /* vh_init routine */
                williams_vh_start,                      /* vh_start routine */
                williams_vh_stop,                       /* vh_stop routine */
                blaster_vh_screenrefresh,               /* vh_update routine */

                /* sound hardware */
                null,                                      /* pointer to samples */
                null,                                      /* sh_init routine */
                null,                                      /* sh_start routine */
                null,                                      /* sh_stop routine */
                williams_sh_update                      /* sh_update routine */
        );

    static HiscoreLoadPtr cmos_load = new HiscoreLoadPtr()
      {
        public int handler(String name)
        {
                FILE f;

                if ((f = fopen(name,"rb")) != null)
                {
                        fread(RAM,0xCC00,1,0x400,f);
                        fclose(f);
                }else{
                        DisplayText[] dt = DisplayText.create(2);
                        dt[0].text ="THIS IS THE FIRST TIME YOU START THIS GAME\n"+
                          "\n"+
                          "AFTER THE RAM TEST THE GAME WILL\n"+
                          "DISPLAY  FACTORY SETTINGS RESTORED\n"+
                          "IT WILL WAIT FOR YOU TO PRESS\n"+
                          "THE ADVANCE BUTTON THAT IS KEY 6\n"+
                          "\n"+
                          "PRESS THIS BUTTON LATER TO GO TO\n"+
                          "THE TESTS AND OPERATOR MENU\n"+
                          "\n"+
                          "TO SKIP THE TESTS HOLD THE AUTO UP\n"+
                          "BUTTON BEFORE PRESSING ADVANCE\n"+
                          "AUTO UP BUTTON IS KEY 5\n"+
                          "\n"+
                          "\n"+
                                "PRESS ADVANCE TO CONTINUE\n";

                        dt[0].color = DT_COLOR_RED;
                        dt[0].x = 10;
                        dt[0].y = 20;
                        dt[1].text = null;
                        displaytext(dt,0);

                        while (!osd_key_pressed(OSD_KEY_6));    /* wait for key press */
                        while (osd_key_pressed(OSD_KEY_6));     /* wait for key release */
                }

                return 1;
        }};
         static HiscoreSavePtr cmos_save = new HiscoreSavePtr()
          {
            public void handler(String name)
            {
                FILE f;

                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0xCC00,1,0x400,f);
                        fclose(f);
                }
        }};

    static HiscoreLoadPtr defender_cmos_load = new HiscoreLoadPtr()
      {
        public int handler(String name)
        {
                FILE f;

                if ((f = fopen(name,"rb")) != null)
                {
                        fread(RAM,0x10400,1,0x100,f);
                        fclose(f);
                }else{
                        DisplayText[] dt = DisplayText.create(2);
                        dt[0].text =
              "THIS IS THE FIRST TIME YOU START DEFENDER\n"+
              "\n"+
              "AFTER THE RAM TEST THE GAME WILL\n"+
              "GO IN THE OPERATOR MENU\n"+
              "THIS IS NORMAL AND WILL OCCUR\n"+
              "ONLY THIS TIME\n"+
              "WHILE IN THE MENU IF AUTO UP\n"+
              "BUTTON IS RELEASED YOU WILL\n"+
              "DECREASE VALUES HOLD IT PRESSED\n"+
              "TO INCREASE VALUES\n"+
              "TO EXIT THE MENU YOU HAVE TO\n"+
              "PASS FROM LINE 28 TO LINE 0\n"+
              "SO HOLD AUTO UP AND PRESS ADVANCE\n"+
              "MANY TIMES\n"+
              "THE AUTO UP BUTTON IS KEY 5\n"+
              "THE ADVANCE BUTTON IS KEY 6\n"+
              "\n"+
              "PRESS ADVANCE BUTTON LATER TO GO\n"+
              "TO THE TESTS AND OPERATOR MENU\n"+
              "\n"+
              "TO SKIP THE TESTS HOLD THE AUTO UP\n"+
              "BUTTON BEFORE PRESSING ADVANCE\n"+
              "\n"+
              "\n"+
                                "   PRESS ADVANCE TO CONTINUE\n";

                        dt[0].color = DT_COLOR_RED;
                        dt[0].x = 10;
                        dt[0].y = 0;
                        dt[1].text = null;
                        displaytext(dt,0);

                        while (!osd_key_pressed(OSD_KEY_6));    /* wait for key press */
                        while (osd_key_pressed(OSD_KEY_6));     /* wait for key release */
                }

                return 1;
        }};
         static HiscoreSavePtr defender_cmos_save = new HiscoreSavePtr()
          {
            public void handler(String name)
            {
                FILE f;

                if ((f = fopen(name,"wb")) != null)
                {
                        fwrite(RAM,0x10400,1,0x100,f);
                        fclose(f);
                }
        }};


        static String williams_sample_names[] =
        {
                "sound00.sam",
                "sound01.sam",
                "sound02.sam",
                "sound03.sam",
                "sound04.sam",
                "sound05.sam",
                "sound06.sam",
                "sound07.sam",
                "sound08.sam",
                "sound09.sam",
                "sound10.sam",
                "sound11.sam",
                "sound12.sam",
                "sound13.sam",
                "sound14.sam",
                "sound15.sam",
                "sound16.sam",
                "sound17.sam",
                "sound18.sam",
                "sound19.sam",
                "sound20.sam",
                "sound21.sam",
                "sound22.sam",
                "sound23.sam",
                "sound24.sam",
                "sound25.sam",
                "sound26.sam",
                "sound27.sam",
                "sound28.sam",
                "sound29.sam",
                "sound30.sam",
                "sound31.sam",
                "sound32.sam",
                "sound33.sam",
                "sound34.sam",
                "sound35.sam",
                "sound36.sam",
                "sound37.sam",
                "sound38.sam",
                "sound39.sam",
                "sound40.sam",
                "sound41.sam",
                "sound42.sam",
                "sound43.sam",
                "sound44.sam",
                "sound45.sam",
                "sound46.sam",
                "sound47.sam",
                "sound48.sam",
                "sound49.sam",
                "sound50.sam",
                "sound51.sam",
                "sound52.sam",
                "sound53.sam",
                "sound54.sam",
                "sound55.sam",
                "sound56.sam",
                "sound57.sam",
                "sound58.sam",
                "sound59.sam",
                "sound60.sam",
                "sound61.sam",
                "sound62.sam",
                "sound63.sam",
                null       /* end of array */
        };


        static RomLoadPtr robotron_rom = new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);     /* 64k for code */
                ROM_LOAD( "robotron.sb1", 0x0000, 0x1000, 0x66c7d3ef );
                ROM_LOAD( "robotron.sb2", 0x1000, 0x1000, 0x5bc6c614 );
                ROM_LOAD( "robotron.sb3", 0x2000, 0x1000, 0xe99a82be );
                ROM_LOAD( "robotron.sb4", 0x3000, 0x1000, 0xafb1c561 );
                ROM_LOAD( "robotron.sb5", 0x4000, 0x1000, 0x62691e77 );
                ROM_LOAD( "robotron.sb6", 0x5000, 0x1000, 0xbd2c853d );
                ROM_LOAD( "robotron.sb7", 0x6000, 0x1000, 0x49ac400c );
                ROM_LOAD( "robotron.sb8", 0x7000, 0x1000, 0x3a96e88c );
                ROM_LOAD( "robotron.sb9", 0x8000, 0x1000, 0xb124367b );
                ROM_LOAD( "robotron.sba", 0xd000, 0x1000, 0x13797024 );
                ROM_LOAD( "robotron.sbb", 0xe000, 0x1000, 0x7e3c1b87 );
                ROM_LOAD( "robotron.sbc", 0xf000, 0x1000, 0x645d543e );
                ROM_END();
        }};

        public static GameDriver robotron_driver = new GameDriver
        (
                "robotron",
                "robotron",
                "MARC LAFONTAINE\nSTEVEN HUGG\nMIRKO BUFFONI",
                robotron_machine_driver,       /* MachineDriver * */

                robotron_rom,                   /* RomModule * */
                null, null,                           /* ROM decrypt routines */
                williams_sample_names,          /* samplenames */

                robotron_input_ports,           /* InputPort  */
                null,
                trak_ports,                     /* TrackBall  */
                williams_dsw,                   /* DSW        */
                robotron_keys,                  /* KEY def    */

                null, null,null,
                ORIENTATION_DEFAULT,

                cmos_load, cmos_save
        );


        static RomLoadPtr joust_rom = new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);     /* 64k for code */
                ROM_LOAD( "joust.wg1",    0x0000, 0x1000, 0xfe41b2af );
                ROM_LOAD( "joust.wg2",    0x1000, 0x1000, 0x501c143c );
                ROM_LOAD( "joust.wg3",    0x2000, 0x1000, 0x43f7161d );
                ROM_LOAD( "joust.wg4",    0x3000, 0x1000, 0xdb5571b6 );
                ROM_LOAD( "joust.wg5",    0x4000, 0x1000, 0xc686bb6b );
                ROM_LOAD( "joust.wg6",    0x5000, 0x1000, 0xfac5f2cf );
                ROM_LOAD( "joust.wg7",    0x6000, 0x1000, 0x81418240 );
                ROM_LOAD( "joust.wg8",    0x7000, 0x1000, 0xba5359ba );
                ROM_LOAD( "joust.wg9",    0x8000, 0x1000, 0x39643147 );
                ROM_LOAD( "joust.wga",    0xd000, 0x1000, 0x3f1c4f89 );
                ROM_LOAD( "joust.wgb",    0xe000, 0x1000, 0xea48b359 );
                ROM_LOAD( "joust.wgc",    0xf000, 0x1000, 0xc710717b );
                ROM_END();
        }};
        public static GameDriver joust_driver = new GameDriver
        (
                "joust",
                "joust",
                "MARC LAFONTAINE\nSTEVEN HUGG\nMIRKO BUFFONI",
                joust_machine_driver,          /* MachineDriver * */

                joust_rom,                      /* RomModule * */
                null, null,                           /* ROM decrypt routines */
                williams_sample_names,          /* samplenames */

                joust_input_ports,              /* InputPort  */
                null,
                trak_ports,                     /* TrackBall  */
                williams_dsw,                   /* DSW        */
                joust_keys,                     /* KEY def    */

                null, null, null,
                ORIENTATION_DEFAULT,

                cmos_load, cmos_save
        );


        static RomLoadPtr sinistar_rom = new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);     /* 64k for code */
                ROM_LOAD( "sinistar.01",  0x0000, 0x1000, 0xf6f3a22c );
                ROM_LOAD( "sinistar.02",  0x1000, 0x1000, 0xcab3185c );
                ROM_LOAD( "sinistar.03",  0x2000, 0x1000, 0x1ce1b3cc );
                ROM_LOAD( "sinistar.04",  0x3000, 0x1000, 0x6da632ba );
                ROM_LOAD( "sinistar.05",  0x4000, 0x1000, 0xb662e8fc );
                ROM_LOAD( "sinistar.06",  0x5000, 0x1000, 0x2306183d );
                ROM_LOAD( "sinistar.07",  0x6000, 0x1000, 0xe5dd918e );
                ROM_LOAD( "sinistar.08",  0x7000, 0x1000, 0x4785a787 );
                ROM_LOAD( "sinistar.09",  0x8000, 0x1000, 0x50cb63ad );
                ROM_LOAD( "sinistar.10",  0xe000, 0x1000, 0x3d670417 );
                ROM_LOAD( "sinistar.11",  0xf000, 0x1000, 0x3162bc50 );
                ROM_END();
        }};
        public static GameDriver sinistar_driver = new GameDriver
        (
                "sinistar",
                "sinistar",
                "MARC LAFONTAINE\nSTEVEN HUGG\nMIRKO BUFFONI",
                sinistar_machine_driver,       /* MachineDriver * */

                sinistar_rom,                   /* RomModule * */
                null, null,                           /* ROM decrypt routines */
                williams_sample_names,          /* samplenames */

                sinistar_input_ports,           /* InputPort  */
                null,
                trak_ports,                     /* TrackBall  */
                williams_dsw,                   /* DSW        */
                keys,                           /* KEY def    */

                null, null, null,
                ORIENTATION_DEFAULT,

                cmos_load, cmos_save
        );


        static RomLoadPtr bubbles_rom = new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);     /* 64k for code */                    
                ROM_LOAD( "bubbles.1b",   0x0000, 0x1000, 0x8234f55c );
                ROM_LOAD( "bubbles.2b",   0x1000, 0x1000, 0x4a188d6a );
                ROM_LOAD( "bubbles.3b",   0x2000, 0x1000, 0x7728f07f );
                ROM_LOAD( "bubbles.4b",   0x3000, 0x1000, 0x040be7f9 );
                ROM_LOAD( "bubbles.5b",   0x4000, 0x1000, 0x0b5f29e0 );
                ROM_LOAD( "bubbles.6b",   0x5000, 0x1000, 0x4dd0450d );
                ROM_LOAD( "bubbles.7b",   0x6000, 0x1000, 0xe0a26ec0 );
                ROM_LOAD( "bubbles.8b",   0x7000, 0x1000, 0x4fd23d8d );
                ROM_LOAD( "bubbles.9b",   0x8000, 0x1000, 0xb48559fb );
                ROM_LOAD( "bubbles.10b",  0xd000, 0x1000, 0x26e7869b );
                ROM_LOAD( "bubbles.11b",  0xe000, 0x1000, 0x5a5b572f );
                ROM_LOAD( "bubbles.12b",  0xf000, 0x1000, 0xce22d2e2 );
                ROM_END();
        }};
                
        public static GameDriver bubbles_driver = new GameDriver
        (
                "bubbles",
                "bubbles",
                "MARC LAFONTAINE\nSTEVEN HUGG\nMIRKO BUFFONI",
                bubbles_machine_driver,        /* MachineDriver * */

                bubbles_rom,                    /* RomModule * */
                null, null,                           /* ROM decrypt routines */
                williams_sample_names,          /* samplenames */

                bubbles_input_ports,            /* InputPort  */
                null,
                trak_ports,                     /* TrackBall  */
                williams_dsw,                   /* DSW        */
                keys,                           /* KEY def    */

                null, null, null,
                ORIENTATION_DEFAULT,

                cmos_load, cmos_save
        );


        static RomLoadPtr stargate_rom = new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);     /* 64k for code */
                ROM_LOAD( "01",           0x0000, 0x1000, 0x88824d18 );
                ROM_LOAD( "02",           0x1000, 0x1000, 0xafc614c5 );
                ROM_LOAD( "03",           0x2000, 0x1000, 0x15077a9d );
                ROM_LOAD( "04",           0x3000, 0x1000, 0xa8b4bf0f );
                ROM_LOAD( "05",           0x4000, 0x1000, 0x2d306074 );
                ROM_LOAD( "06",           0x5000, 0x1000, 0x53598dde );
                ROM_LOAD( "07",           0x6000, 0x1000, 0x23606060 );
                ROM_LOAD( "08",           0x7000, 0x1000, 0x4ec490c7 );
                ROM_LOAD( "09",           0x8000, 0x1000, 0x88187b64 );
                ROM_LOAD( "10",           0xd000, 0x1000, 0x60b07ff7 );
                ROM_LOAD( "11",           0xe000, 0x1000, 0x7d2c5daf );
                ROM_LOAD( "12",           0xf000, 0x1000, 0xa0396670 );
                ROM_END();
        }};
        
        public static GameDriver stargate_driver = new GameDriver
        (
                "stargate",
                "stargate",
                "MARC LAFONTAINE\nSTEVEN HUGG\nMIRKO BUFFONI",
                stargate_machine_driver,       /* MachineDriver * */

                stargate_rom,                   /* RomModule * */
                null, null,                           /* ROM decrypt routines */
                williams_sample_names,          /* samplenames */

                stargate_input_ports,           /* InputPort  */
                null,
                trak_ports,                     /* TrackBall  */
                williams_dsw,                   /* DSW        */
                stargate_keys,                  /* KEY def    */

                null, null, null,
                ORIENTATION_DEFAULT,

                cmos_load, cmos_save
        );


        static RomLoadPtr defender_rom = new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x15000);     /* 64k for code + 5 banks of 4K */
             /*   ROM_LOAD( "defend.1",     0x0d000, 0x0800, 0xc3e52d7e );
                ROM_LOAD( "defend.4",     0x0d800, 0x0800, 0x9a72348b );
                ROM_LOAD( "defend.2",     0x0e000, 0x1000, 0x89b75984 );
                ROM_LOAD( "defend.3",     0x0f000, 0x1000, 0x94f51e9b );
                /* bank 0 is the place for CMOS ram */
            /*    ROM_LOAD( "defend.9",     0x10000, 0x0800, 0x6870e8a5 );
                ROM_LOAD( "defend.12",    0x10800, 0x0800, 0xf1f88938 );
                ROM_LOAD( "defend.8",     0x11000, 0x0800, 0xb649e306 );
                ROM_LOAD( "defend.11",    0x11800, 0x0800, 0x9deaf6d9 );
                ROM_LOAD( "defend.7",     0x12000, 0x0800, 0x339e092e );
                ROM_LOAD( "defend.10",    0x12800, 0x0800, 0xa543b167 );
                ROM_RELOAD(               0x13800, 0x0800 );
                ROM_LOAD( "defend.6",     0x13000, 0x0800, 0x65f4efd1 );
                ROM_END();*/
                ROM_LOAD( "defend.1", 0xd000, 0x0800, 0x4aa8c614 ); //using v0.27 loading since 0.36 not working well
                ROM_LOAD( "defend.4", 0xd800, 0x0800, 0x99e9bb31 );
                ROM_LOAD( "defend.2", 0xe000, 0x1000, 0x8991dceb );
                ROM_LOAD( "defend.3", 0xf000, 0x1000, 0x3f6e9fe2 );
          /* 10000 to 10fff is the place for CMOS ram (BANK 0) */
                ROM_LOAD( "defend.9", 0x11000, 0x0800, 0x3e2646ae );
                ROM_LOAD( "defend.12",0x11800, 0x0800, 0xd13eeb4a );
                ROM_LOAD( "defend.8", 0x12000, 0x0800, 0x67afa299 );
                ROM_LOAD( "defend.11",0x12800, 0x0800, 0x287572ed );
                ROM_LOAD( "defend.7", 0x13000, 0x0800, 0x344c9bd0 );
                ROM_LOAD( "defend.10",0x13800, 0x0800, 0xee30b06e );
                ROM_LOAD( "defend.6", 0x14000, 0x0800, 0x8c7b2da3 );
                ROM_LOAD( "defend.10",0x14800, 0x0800, 0xee30b06e );
                ROM_END();
        }};
        
        public static GameDriver defender_driver = new GameDriver
        (
                "defender",
                "defender",
                "MARC LAFONTAINE\nSTEVEN HUGG\nMIRKO BUFFONI",
                defender_machine_driver,       /* MachineDriver * */

                defender_rom,                   /* RomModule * */
                null, null,                           /* ROM decrypt routines */
                williams_sample_names,          /* samplenames */

                defender_input_ports,           /* InputPort  */
                null,
                trak_ports,                     /* TrackBall  */
                williams_dsw,                   /* DSW        */
                defender_keys,                  /* KEY def    */

                null, null, null,
                ORIENTATION_DEFAULT,

                defender_cmos_load, defender_cmos_save
        );




        static RomLoadPtr splat_rom = new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x10000);     /* 64k for code */
                ROM_LOAD( "splat.01",     0x0000, 0x1000, 0x1cf26e48 );
                ROM_LOAD( "splat.02",     0x1000, 0x1000, 0xac0d4276 );
                ROM_LOAD( "splat.03",     0x2000, 0x1000, 0x74873e59 );
                ROM_LOAD( "splat.04",     0x3000, 0x1000, 0x70a7064e );
                ROM_LOAD( "splat.05",     0x4000, 0x1000, 0xc6895221 );
                ROM_LOAD( "splat.06",     0x5000, 0x1000, 0xea4ab7fd );
                ROM_LOAD( "splat.07",     0x6000, 0x1000, 0x82fd8713 );
                ROM_LOAD( "splat.08",     0x7000, 0x1000, 0x7dded1b4 );
                ROM_LOAD( "splat.09",     0x8000, 0x1000, 0x71cbfe5a );
                ROM_LOAD( "splat.10",     0xd000, 0x1000, 0xd1a1f632 );
                ROM_LOAD( "splat.11",     0xe000, 0x1000, 0xca8cde95 );
                ROM_LOAD( "splat.12",     0xf000, 0x1000, 0x5bee3e60 );
                ROM_END();
        }};

        public static GameDriver splat_driver = new GameDriver
        (
                "splat",
                "splat",
                "MARC LAFONTAINE\nSTEVEN HUGG\nMIRKO BUFFONI",
                splat_machine_driver,          /* MachineDriver * */

                splat_rom,                      /* RomModule * */
                null, null,                           /* ROM decrypt routines */
                williams_sample_names,          /* samplenames */

                splat_input_ports,              /* InputPort  */
                null,
                trak_ports,                     /* TrackBall  */
                williams_dsw,                   /* DSW        */
                splat_keys,                     /* KEY def    */

                null, null, null,
                ORIENTATION_DEFAULT,

          cmos_load, cmos_save
        );
        static RomLoadPtr blaster_rom = new RomLoadPtr(){ public void handler() 
        {
                ROM_REGION(0x49000) ;    /* 256k for code */
                ROM_LOAD( "blaster.16", 0x0d000, 0x1000, 0xd6e04ee2 );
                ROM_LOAD( "blaster.13", 0x0e000, 0x2000, 0x376fc541 );
                ROM_LOAD( "blaster.15", 0x10000, 0x4000, 0x1c345c06 );
                ROM_LOAD( "blaster.8",  0x14000, 0x4000, 0xc297d5ab );
                ROM_LOAD( "blaster.9",  0x18000, 0x4000, 0xe88478a0 );
                ROM_LOAD( "blaster.10", 0x1c000, 0x4000, 0xc68a1386 );
                ROM_LOAD( "blaster.6",  0x20000, 0x4000, 0x3142941e );
                ROM_LOAD( "blaster.5",  0x24000, 0x4000, 0x2ebd56e5 );
                ROM_LOAD( "blaster.14", 0x28000, 0x4000, 0xe0267262 );
                ROM_LOAD( "blaster.7",  0x2c000, 0x4000, 0x17bff9a5 );
                ROM_LOAD( "blaster.1",  0x30000, 0x4000, 0x37d9abe5 );
                ROM_LOAD( "blaster.2",  0x34000, 0x4000, 0xd99ff133 );
                ROM_LOAD( "blaster.4",  0x38000, 0x4000, 0x8d86011c );
                ROM_LOAD( "blaster.3",  0x3c000, 0x4000, 0x86ddd013 );
                ROM_LOAD( "blaster.1",  0x40000, 0x4000, 0x37d9abe5 );
                ROM_LOAD( "blaster.11", 0x44000, 0x2000, 0xdc7831b2 );
                ROM_LOAD( "blaster.12", 0x46000, 0x2000, 0x68244eb6 );
                ROM_LOAD( "blaster.17", 0x48000, 0x1000, 0xecbf6a51 );
                        
                /*ROM_LOAD( "blaster.11",   0x04000, 0x2000, 0x6371e62f );
                ROM_LOAD( "blaster.12",   0x06000, 0x2000, 0x9804faac );
                ROM_LOAD( "blaster.17",   0x08000, 0x1000, 0xbf96182f );
                ROM_LOAD( "blaster.16",   0x0d000, 0x1000, 0x54a40b21 );
                ROM_LOAD( "blaster.13",   0x0e000, 0x2000, 0xf4dae4c8 );

                ROM_LOAD( "blaster.15",   0x00000, 0x4000, 0x1ad146a4 );
                ROM_LOAD( "blaster.8",    0x10000, 0x4000, 0xf110bbb0 );
                ROM_LOAD( "blaster.9",    0x14000, 0x4000, 0x5c5b0f8a );
                ROM_LOAD( "blaster.10",   0x18000, 0x4000, 0xd47eb67f );
                ROM_LOAD( "blaster.6",    0x1c000, 0x4000, 0x47fc007e );
                ROM_LOAD( "blaster.5",    0x20000, 0x4000, 0x15c1b94d );
                ROM_LOAD( "blaster.14",   0x24000, 0x4000, 0xaea6b846 );
                ROM_LOAD( "blaster.7",    0x28000, 0x4000, 0x7a101181 );
                ROM_LOAD( "blaster.1",    0x2c000, 0x4000, 0x8d0ea9e7 );
                ROM_LOAD( "blaster.2",    0x30000, 0x4000, 0x03c4012c );
                ROM_LOAD( "blaster.4",    0x34000, 0x4000, 0xfc9d39fb );
                ROM_LOAD( "blaster.3",    0x38000, 0x4000, 0x253690fb );*/
                ROM_END();
        }};

        public static GameDriver blaster_driver = new GameDriver
        (
                "blaster",
                "blaster",
                "MARC LAFONTAINE\nSTEVEN HUGG\nMIRKO BUFFONI",
                blaster_machine_driver,        /* MachineDriver * */

                blaster_rom,                    /* RomModule * */
                null, null,                           /* ROM decrypt routines */
                williams_sample_names,          /* samplenames */

                blaster_input_ports,            /* InputPort  */
                null,
                trak_ports,                     /* TrackBall  */
                williams_dsw,                   /* DSW        */
                blaster_keys,                   /* KEY def    */

                null, null, null,
                ORIENTATION_DEFAULT,

                cmos_load, cmos_save
        );
  
}
