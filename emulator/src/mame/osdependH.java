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

//TODO finish porting to 0.29

/*
 *
 * ported to v0.27
 *
 *
 *
 */

package mame;

import java.awt.event.KeyEvent;

/**
 *
 * @author shadow
 */
public class osdependH
{
        public static class osd_bitmap {

            public osd_bitmap() {}
            public int width, height;	/* width and hegiht of the bitmap */
            public char[][] line;		/* pointers to the start of each line */
       };

        public static final int OSD_KEY_ESC           = KeyEvent.VK_ESCAPE;
        public static final int OSD_KEY_1             = KeyEvent.VK_1;
        public static final int OSD_KEY_2             = KeyEvent.VK_2;
        public static final int OSD_KEY_3             = KeyEvent.VK_3;
        public static final int OSD_KEY_4             = KeyEvent.VK_4;
        public static final int OSD_KEY_5             = KeyEvent.VK_5;
        public static final int OSD_KEY_6             = KeyEvent.VK_6;
        public static final int OSD_KEY_7             = KeyEvent.VK_7;
        public static final int OSD_KEY_8             = KeyEvent.VK_8;
        public static final int OSD_KEY_9             = KeyEvent.VK_9;
        public static final int OSD_KEY_0             = KeyEvent.VK_0;
       // public static final int OSD_KEY_MINUS       12
        public static final int OSD_KEY_EQUALS        = KeyEvent.VK_EQUALS;
       // public static final int OSD_KEY_BACKSPACE   14
        public static final int OSD_KEY_TAB           = KeyEvent.VK_TAB;
        public static final int OSD_KEY_Q             = KeyEvent.VK_Q;
        public static final int OSD_KEY_W             = KeyEvent.VK_W;
        public static final int OSD_KEY_E             = KeyEvent.VK_E;
        public static final int OSD_KEY_R             = KeyEvent.VK_R;
        public static final int OSD_KEY_T             = KeyEvent.VK_T;
        public static final int OSD_KEY_Y             = KeyEvent.VK_Y;
        public static final int OSD_KEY_U             = KeyEvent.VK_U;
        public static final int OSD_KEY_I             = KeyEvent.VK_I;
        public static final int OSD_KEY_O             = KeyEvent.VK_O;
        public static final int OSD_KEY_P             = KeyEvent.VK_P;
       // public static final int OSD_KEY_OPENBRACE   26
      //  public static final int OSD_KEY_CLOSEBRACE  27
        public static final int OSD_KEY_ENTER       = KeyEvent.VK_ENTER;
        public static final int OSD_KEY_CONTROL     = KeyEvent.VK_CONTROL;
        public static final int OSD_KEY_A           = KeyEvent.VK_A;
        public static final int OSD_KEY_S           = KeyEvent.VK_S;
        public static final int OSD_KEY_D           = KeyEvent.VK_D;
        public static final int OSD_KEY_F           = KeyEvent.VK_F;
        public static final int OSD_KEY_G           = KeyEvent.VK_G;
        public static final int OSD_KEY_H           = KeyEvent.VK_H;
        public static final int OSD_KEY_J           = KeyEvent.VK_J;
        public static final int OSD_KEY_K           = KeyEvent.VK_K;
        public static final int OSD_KEY_L           = KeyEvent.VK_L;
        //public static final int OSD_KEY_COLON       39
        //public static final int OSD_KEY_QUOTE       40
        //public static final int OSD_KEY_TILDE       41
        public static final int OSD_KEY_LSHIFT      = KeyEvent.VK_SHIFT; //hmm java uses that for both left and right shift...
        
        public static final int OSD_KEY_Z           = KeyEvent.VK_Z;
        public static final int OSD_KEY_X           = KeyEvent.VK_X;
        public static final int OSD_KEY_C           = KeyEvent.VK_C;
        public static final int OSD_KEY_V           = KeyEvent.VK_V;
        public static final int OSD_KEY_B           = KeyEvent.VK_B;
        public static final int OSD_KEY_N           = KeyEvent.VK_N;
        public static final int OSD_KEY_M           = KeyEvent.VK_M;
        //public static final int OSD_KEY_COMMA       51
        //public static final int OSD_KEY_STOP        52
        //public static final int OSD_KEY_SLASH       53
        //public static final int OSD_KEY_RSHIFT      54
        //public static final int OSD_KEY_ASTERISK    55
        public static final int OSD_KEY_ALT         = KeyEvent.VK_ALT;
        public static final int OSD_KEY_SPACE       = KeyEvent.VK_SPACE;
        //public static final int OSD_KEY_CAPSLOCK    58
        public static final int OSD_KEY_F1          = KeyEvent.VK_F1;
        public static final int OSD_KEY_F2          = KeyEvent.VK_F2;
        public static final int OSD_KEY_F3          = KeyEvent.VK_F3;
        public static final int OSD_KEY_F4          = KeyEvent.VK_F4;
        public static final int OSD_KEY_F5          = KeyEvent.VK_F5;
        public static final int OSD_KEY_F6          = KeyEvent.VK_F6;
        public static final int OSD_KEY_F7          = KeyEvent.VK_F7;
        public static final int OSD_KEY_F8          = KeyEvent.VK_F8;
        public static final int OSD_KEY_F9          = KeyEvent.VK_F9;
        public static final int OSD_KEY_F10         = KeyEvent.VK_F10;
        //public static final int OSD_KEY_NUMLOCK     69
        //public static final int OSD_KEY_SCRLOCK     70
        //public static final int OSD_KEY_HOME        71
        public static final int OSD_KEY_UP          = KeyEvent.VK_UP;
        public static final int OSD_KEY_PGUP        = KeyEvent.VK_PAGE_UP;
        public static final int OSD_KEY_MINUS_PAD   = KeyEvent.VK_MINUS;
        public static final int OSD_KEY_LEFT        = KeyEvent.VK_LEFT;
        //public static final int OSD_KEY_5_PAD       76
        public static final int OSD_KEY_RIGHT       = KeyEvent.VK_RIGHT;
        public static final int OSD_KEY_PLUS_PAD    = KeyEvent.VK_PLUS;
       // public static final int OSD_KEY_END         79
        public static final int OSD_KEY_DOWN        = KeyEvent.VK_DOWN;
        public static final int OSD_KEY_PGDN        = KeyEvent.VK_PAGE_DOWN;
       // public static final int OSD_KEY_INSERT      82
        //public static final int OSD_KEY_DEL         83
        public static final int OSD_KEY_F11         = KeyEvent.VK_F11;
        public static final int OSD_KEY_F12         = KeyEvent.VK_F12;
       // public static final int OSD_MAX_KEY

        //joystic definations TODO
        /*#define OSD_JOY_LEFT	1
        #define OSD_JOY_RIGHT	2
        #define OSD_JOY_UP		3
        #define OSD_JOY_DOWN	4
        #define OSD_JOY_FIRE1	5
        #define OSD_JOY_FIRE2	6
        #define OSD_JOY_FIRE3	7
        #define OSD_JOY_FIRE4	8
        #define OSD_JOY_FIRE	9	/* any of the fire buttons */
       /* #define OSD_MAX_JOY     9*/

       public static final int X_AXIS = 1;
       public static final int Y_AXIS = 2;
       public static final int NO_TRAK = 1000000;
       
       /* file handling routines */
       public static final int OSD_FILETYPE_ROM= 1;
       public static final int OSD_FILETYPE_SAMPLE= 2;
       public static final int OSD_FILETYPE_HIGHSCORE= 3;
       public static final int OSD_FILETYPE_CONFIG= 4;
}
