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
 *
 * this file is a test and probably needs serious rewrite
 *
 *
 *
 */

package arcadeflex;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import static mame.osdependH.*;
import static mame.common.*;
import static arcadeflex.libc.*;
import static mame.mame.*;
import static mame.driverH.*;



public class osdepend
{

    public static int play_sound;
    static boolean noscanlines;
    public static osd_bitmap bitmap;
    private static sound soundPlayer = new sound();

    static int[] palette = new int[256];
    public static int first_free_pen;

    private static software_gfx screen;
    static String[] keynames = new String[1024];
    static final int MAXPIXELS = 100000;
    static CharPtr[] pixel;
    static int p_index;

    //mouse variables;
    static int mouse_x;
    static int mouse_y;
    static boolean use_mouse=false;//todo added a command line switch also enable it once you fix it :p

    /* put here anything you need to do when the program is started. Return 0 if */
    /* initialization was successful, nonzero otherwise. */
    public static int osd_init(int argc, String[] argv)
    {
        first_free_pen = 0;

        noscanlines = false;
        play_sound = 1;
        for (int i = 1; i < argc; i++)
        {
          if (stricmp(argv[i], "-nosound") == 0)
            play_sound = 0;
          if (stricmp(argv[i], "-noscanlines") == 0) {
            noscanlines = true;
          }
        }
        return 0;
    }
    /* put here cleanup routines to be executed when the program is terminated. */
    public static void osd_exit()
    {

    }
    /* Create a bitmap. Also call clearbitmap() to appropriately initialize it to */
    /* the background color. */

    public static osd_bitmap osd_create_bitmap(int width, int height)//TODO recheck
    {
        //System.out.println("osd_create_bitmap width= "+width+ " height="+height);
        osd_bitmap localosd_bitmap;
        
        if ((Machine.orientation & ORIENTATION_SWAP_XY)!=0)
        {
                int temp;

                temp = width;
                width = height;
                height = temp;
        }
                
        if ((localosd_bitmap = new osd_bitmap()) != null) {
            localosd_bitmap.width = width;
            localosd_bitmap.height = height;

            localosd_bitmap.line = new char[height][];
            for (int i = 0; i < height; i++) {
                localosd_bitmap.line[i] = new char[width];
            }
            clearbitmap(localosd_bitmap);
        }

        return localosd_bitmap;
    }


    public static void osd_free_bitmap(osd_bitmap bitmap)
    {
        if (bitmap != null) {
            bitmap.line = ((char[][]) null);
        }
    }
    /* Create a display screen, or window, large enough to accomodate a bitmap */
    /* of the given dimensions. I don't do any test here (224x288 will just do */
    /* for now) but one could e.g. open a window of the exact dimensions */
    /* provided. Return a osd_bitmap pointer or 0 in case of error. */
    public static osd_bitmap osd_create_display(int width, int height,int video_attributes) 
    {
        //System.out.println("osd_create_display width= "+width+ " height="+height);
        mame.mame.dlprogress.setVisible(false);
        //mame.mame.dlprogress = null;
        Dimension localDimension = Toolkit.getDefaultToolkit().getScreenSize();
        screen = new software_gfx(settings.version + " (based on mame v" +mameversion + ")");
        screen.pack();
        screen.setSize(!noscanlines, width, height);
        screen.setBackground(Color.black);
        screen.start();
        screen.run();
        screen.setLocation((int) ((localDimension.getWidth() - screen.getWidth()) / 2.0D), (int) ((localDimension.getHeight() - screen.getHeight()) / 2.0D));
        screen.setVisible(true);
        screen.setResizable(noscanlines);

        screen.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent evt) {
                screen.readkey = OSD_KEY_ESC;
                screen.key[OSD_KEY_ESC] = true;
                osd_refresh();
                if (screen != null) {
                    screen.key[OSD_KEY_ESC] = false;
                }
            }
        });

        screen.addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent evt) {
                screen.resizeVideo();
            }
        });

        screen.addKeyListener(screen);
        screen.setFocusTraversalKeysEnabled(false);
        screen.requestFocus();

        bitmap = osd_create_bitmap(width, height);

        if ((Machine.orientation & ORIENTATION_SWAP_XY)!=0)
	{
		int temp;

		temp = width;
		width = height;
		height = temp;
	}
        return bitmap;
        
    }
    /* shut up the display */
    public static void osd_close_display() 
    {
        screen.stop();
        screen.setVisible(false);
        screen = null;
        osd_free_bitmap(bitmap);
        bitmap = null;

    }
    public static int osd_obtain_pen(int red, int green, int blue)
    {
        int res;


	res = first_free_pen;

	//palette_set_color(res,red>>2,green>>2,blue>>2);//TODO :hmmm not sure with mame do this but it makes things more dark...
        
        palette_set_color(res,red,green,blue);
        
	/* I could just increase first_free_pen. However, many driver writers */
	/* assume that the palette is contiguous and forget to use Machine.pens[] */
	/* to access it. This works on MS-DOS but not on the Mac. Mangling the */
	/* palette this way ensures that DOS developers immediately notice that */
	/* there's something wrong in the driver. */
	first_free_pen = (first_free_pen + 3) % 256;

	return res;
    }
    public static void palette_set_color(int c, int r, int g, int b) 
    {        
	int rgb =  r<<16 | g<<8 | b;
	palette[c] = rgb;
    }
    
    public static char[] osd_get_pen(char pen)
    {
        char[] rgb = new char[3]; //r , g ,b

        rgb[0]=(char)(palette[pen]>>16 &0xff);
        //System.out.println("r="+rgb[0]);
        rgb[1]=(char)(palette[pen]>>8 &0xff);
        //System.out.println("g="+rgb[1]);
        rgb[2]=(char)(palette[pen] &0xff);
        //System.out.println("b="+rgb[2]);
        return rgb;
    }
    public static void osd_modify_pen(int pen,int red, int green, int blue)
    {
            palette_set_color(pen,red,green,blue);
    }
    /* Update the display. */
    /* As an additional bonus, this function also saves the screen as a PCX file */
    /* when the user presses F5. This is not required for porting. */
    public static void osd_update_display()
    {
        /* copy the bitmap to screen memory */
        int offset = 0;
        if (noscanlines) {
            for (int j = 0; j < bitmap.height; j++) {
                char[] line = bitmap.line[j];
                for (int i = 0; i < bitmap.width; i++) {
                    screen._pixels[offset++] = palette[line[i]];
                }
            }
        } else {
            for (int j = 0; j < bitmap.height; j++) {
                char[] line = bitmap.line[j];
                for (int i = 0; i < bitmap.width; i++) {
                    screen._pixels[offset++] = palette[line[i]];
                }
                offset += bitmap.width;
            }
        }
        screen.blit();
    }

    /*********************
     *
     *
     *  Key functions
     *
     *
     */
    /* check if a key is pressed. The keycode is the standard PC keyboard code, as */
    /* defined in osdepend.h. Return 0 if the key is not pressed, nonzero otherwise. */
    public static synchronized boolean osd_key_pressed(int keycode)
    {
        return screen.key[keycode];
    }

    /* wait for a key press and return the keycode */
    public static int osd_read_key() {
        int i;
        do {
            for (i = 1023; (i >= 0)
                    && (!osd_key_pressed(i)); i--);
           osd_refresh();
        } while (i >= 0);
        do {
            for (i = 1023; (i >= 0)
                    && (!osd_key_pressed(i)); i--);
            osd_refresh();
        } while (i < 0);

        return i;
    }

    public static int osd_read_keyrepeat() {
        screen.readkey = 0;
        while (screen.readkey == 0) {
            osd_refresh();
        }
        return screen.readkey;
    }

    public static void osd_refresh() {
        if (screen != null) {
            screen.blit();
        }
        try {
            Thread.sleep(100L);
        } catch (InterruptedException localInterruptedException) {
        }
    }


    public static String osd_key_name(int paramInt) {
        if (paramInt < 1024) {
            return keynames[paramInt];
        }
        return " ";
    }
    static {
        for (int i = 0; i < 1024; i++) {
            keynames[i] = " ";
        }
        keynames[27] = "ESC";
        keynames[49] = "1";
        keynames[50] = "2";
        keynames[51] = "3";
        keynames[52] = "4";
        keynames[53] = "5";
        keynames[54] = "6";
        keynames[55] = "7";
        keynames[56] = "8";
        keynames[57] = "9";
        keynames[48] = "0";
        keynames[61] = "EQUAL";
        keynames[8] = "BACKSPACE";
        keynames[9] = "TAB";
        keynames[81] = "Q";
        keynames[87] = "W";
        keynames[69] = "E";
        keynames[82] = "R";
        keynames[84] = "T";
        keynames[89] = "Y";
        keynames[85] = "U";
        keynames[73] = "I";
        keynames[79] = "O";
        keynames[80] = "P";
        keynames[10] = "ENTER";
        keynames[17] = "CONTROL";
        keynames[65] = "A";
        keynames[83] = "S";
        keynames[68] = "D";
        keynames[70] = "F";
        keynames[71] = "G";
        keynames[72] = "H";
        keynames[74] = "J";
        keynames[75] = "K";
        keynames[76] = "L";
        keynames[513] = "COLON";
        keynames[90] = "Z";
        keynames[88] = "X";
        keynames[67] = "C";
        keynames[86] = "V";
        keynames[66] = "B";
        keynames[77] = "M";
        keynames[78] = "N";
        keynames[18] = "ALT";
        keynames[32] = "SPACE";
        keynames[112] = "F1";
        keynames[113] = "F2";
        keynames[114] = "F3";
        keynames[115] = "F4";
        keynames[116] = "F5";
        keynames[117] = "F6";
        keynames[118] = "F7";
        keynames[119] = "F8";
        keynames[120] = "F9";
        keynames[121] = "F10";
        keynames[38] = "UP";
        keynames[33] = "PGUP";
        keynames[109] = "MINUS PAD";
        keynames[37] = "LEFT";
        keynames[39] = "RIGHT";
        keynames[107] = "PLUS PAD";
        keynames[40] = "DOWN";
        keynames[34] = "PGDN";
        keynames[122] = "F11";
        keynames[123] = "F12";
        
        pixel = new CharPtr[100000];
        p_index = -1;
    }

/*************************************
 *
 *
 *   Sound functions
 *
 */
    public static void osd_update_audio() {
        if (play_sound == 0) {
            return;
        }

        soundPlayer.update();
    }

    public static void osd_play_sample(int channel, char[] data, int offset, int datalength, int freq, int volume, int loop) {
        if (play_sound == 0) {
            return;
        }
        soundPlayer.playSample(channel, data, offset, datalength, freq, volume,loop);
    }
    public static void osd_play_sample(int channel, char[] data, int datalength, int freq, int volume, int loop) {
        if (play_sound == 0) {
            return;
        }
        soundPlayer.playSample(channel, data, datalength, freq, volume, loop);
    }
    public static void osd_play_streamed_sample(int channel, char[] data, int datalength, int freq, int volume)
    {
       if (play_sound == 0) {
            return;
        }
        soundPlayer.playSample(channel, data, datalength, freq, volume, 0);//hackish but partially works
    }
    public static void osd_adjust_sample(int channel, int freq, int volume) {
        soundPlayer.adjustSample(channel, freq, volume);
    }
    public static void osd_stop_sample(int samplenumber)
    {
        soundPlayer.stopSample(samplenumber);
    }
    public static void osd_set_mastervolume(int volume) {
        soundPlayer.setMasterVolume(volume);
    }
     
    public static int[] open_page (int step)
    {
            int i;
            int[] res=new int[2];//holds x_res and y_res

            res[0]/*x_res*/=bitmap.width;
            res[1]/*y_res*/=bitmap.height;
           int bg=Machine.pens[0];
            for (i=p_index; i>=0; i--)
            {
                     pixel[i].write(bg);
            }
            p_index=-1;
            return res;
    }

    public static void close_page ()
    {
    }
    public static void draw_pixel (int x, int y, int col)
    {
            CharPtr address=new CharPtr();
            if (x<0 || x >= bitmap.width)
                    return;
            if (y<0 || y >= bitmap.height)
                    return;
            //address=&(bitmap.line[y][x]);
            address.set(bitmap.line[y], x);             
            address.write(col);
            if (p_index<MAXPIXELS-1) {
                    p_index++;
                    pixel[p_index]=address;
                    
         }
    }
    static int x1=0;
    static int y1=0;
    public static void draw_to (int x2, int y2, int col)
    {

            int temp_x, temp_y;
            int dx,dy,cx,cy,sx,sy;

            /*if (errorlog!=null)
                    fprintf(errorlog,
                    "line:%d,%d nach %d,%d color %d\n",
                     x1,y1,x2,y2,col);*/
 

            if (col<0)
            {
                    x1=x2;
                    y1=y2;
                    return;
            } else
                    col=Machine.gfx[0].colortable.read(col);

            temp_x = x2; temp_y = y2;


            dx=Math.abs(x1-x2);
            dy=Math.abs(y1-y2);

            if ((dx>=dy && x1>x2) || (dy>dx && y1>y2))
            {
                    int t;
                    t = x1; x1 = x2; x2 = t;
                    t = y1; y1 = y2; y2 = t;
            }
            sx = ((x1 <= x2) ? 1 : -1);
            sy = ((y1 <= y2) ? 1 : -1);
            cx=dx/2;
            cy=dy/2;

            if (dx>=dy)
            {
                    while (x1 <= x2)
                    {
                            draw_pixel(x1,y1,col);
                            x1+=sx;
                            cx-=dy;
                            if (cx < 0)
                            {
                                    y1+=sy;
                                    cx+=dx;
                            }
                    }
            }
            else
            {
                    while (y1 <= y2)
                    {
                            draw_pixel(x1,y1,col);
                            y1+=sy;
                            cy-=dx;
                            if (cy < 0)
                            {
                                    x1+=sx;
                                    cy+=dy;
                            }
                    }
            }

            y1=temp_y;
            x1=temp_x;
    }

    /* this is used from the windows port of mame (which we use to debug) in dos mame 0.27 is a bit different so we use leave that for debugging reasons
    public static void draw_to (int x2, int y2, int col)
    {

	int temp_x, temp_y;
	int dx,dy,cx,cy,sx,sy;
	

	/*	if (errorlog!=null)
			fprintf(errorlog,
			"line:%d,%d nach %d,%d color %d\n",
			 x1,y1,x2,y2,col);*/

/*
	if (col<0)
	{
		x1=x2;
		y1=y2;
		return;
	} else 
		col=Machine.gfx[0].colortable.read(col);

	temp_x = x2; temp_y = y2;
	
	dx=Math.abs(x1-x2);
	dy=Math.abs(y1-y2);

	if ((dx>=dy && x1>x2) || (dy>dx && y1>y2))
	{
		int t;
		t = x1; x1 = x2; x2 = t;
		t = y1; y1 = y2; y2 = t;
	}
	sx = ((x1 <= x2) ? 1 : -1);
	sy = ((y1 <= y2) ? 1 : -1);
	cx=dx/2;
	cy=dy/2;

	if (dx == dy)
	{
		while (x1 <= x2)
		{
			draw_pixel(x1,y1,col);
			//fprintf(errorlog,"draw:%d,%d col %d\n",x1,y1,col);
			x1+=sx;
			y1+=sy;
		}
	}
	else if (dx>dy)
	{
		while (x1 <= x2)
		{
			draw_pixel(x1,y1,col);
			//fprintf(errorlog,"draw:%d,%d col %d\n",x1,y1,col);
			x1+=sx;
			cx-=dy;
			if (cx < 0)
			{
				y1+=sy;
				cx+=dx;
			}
		}
	}
	else
	{
		while (y1 <= y2)
		{
			draw_pixel(x1,y1,col);
			//fprintf(errorlog,"draw:%d,%d col %d\n",x1,y1,col);
			y1+=sy;
			cy-=dx;
			if (cy < 0)
			{
				x1+=sx;
				cy+=dy;
			}
		}
	}	
	x1 = temp_x;
	y1 = temp_y;
	
    }*/
    
    //new stuff for v0.29
    static int oldx,oldy;
    //Tried to emulate get_mouse_mickeys from allegro lib but doesn't seem to work....
    public static int[] get_mouse_mickeys()
    {
        int[] mickeys=new int[2];
        mickeys[0]=mouse_x-oldx;
        mickeys[1]=mouse_y-oldy;
        System.out.println("m1 " +mickeys[0]);
        System.out.println("m2 " +mickeys[1]);
        oldx=mouse_x;
        oldy=mouse_y;
        return mickeys;
    }
    static int deltax;
    static int deltay;
    public static int osd_trak_read(int axis) {

        
	int mickeyx=0, mickeyy=0;
	int ret;

	if (!use_mouse)
		return(0);

	int[] mick =get_mouse_mickeys();
        mick[0]=mickeyx;
        mick[1]=mickeyy;

	deltax+=mickeyx;
	deltay+=mickeyy;

	switch(axis) {
		case X_AXIS:
			ret=deltax;
			deltax=0;
			break;
		case Y_AXIS:
			ret=deltay;
			deltay=0;
			break;
		default:
			ret=0;
			if (errorlog!=null)
				fprintf (errorlog, "Error: no axis in osd_track_read\n");
	}

	return ret;

    } 
    
    /* file handling routines */

        /* gamename holds the driver name, filename is only used for ROMs and samples. */
        /* if 'write' is not 0, the file is opened for write. Otherwise it is opened */
        /* for read. */
        public static FILE osd_fopen(String gamename,String filename,int filetype,int write)
        {
                char[] name=new char[100];
                FILE f;


                switch (filetype)
                {
                        case OSD_FILETYPE_ROM:
                        case OSD_FILETYPE_SAMPLE:
                                sprintf(name,"%s/%s",new Object[] {gamename,filename});
                                f = fopen(name,write!=0 ? "wb" : "rb");
                                if (f == null)
                                {
                                        /* try with a .zip directory (if ZipMagic is installed) */
                                        sprintf(name,"%s.zip/%s",new Object[] {gamename,filename});
                                        f = fopen(name,write!=0 ? "wb" : "rb");
                                }
                                if (f == null)
                                {
                                        /* try with a .zif directory (if ZipFolders is installed) */
                                        sprintf(name,"%s.zif/%s",new Object[] {gamename,filename});
                                        f = fopen(name,write!=0 ? "wb" : "rb");
                                }
                                return f;
                                //break;
                        case OSD_FILETYPE_HIGHSCORE:
                                sprintf(name,"hi/%s.hi",new Object[] {gamename});
                                return fopen(name,write!=0 ? "wb" : "rb");
                                //break;
                        case OSD_FILETYPE_CONFIG:
                                sprintf(name,"cfg/%s.cfg",new Object[] {gamename});
                                return fopen(name,write!=0 ? "wb" : "rb");
                                //break;
                        default:
                                return null;
                                //break;
                }
        }



        public static int osd_fread(FILE file,char[] buffer,int offset,int length)
        {
                return fread(buffer,offset,1,length,file);
        }



        public static void osd_fwrite(FILE file,char[] buffer,int offset,int length)
        {
                fwrite(buffer,offset,1,length,file);
        }



        /*public static int osd_fseek(FILE file,int offset,int whence) //TODO
        {
                return fseek(file,offset,whence);
        }*/



        public static void osd_fclose(FILE file)
        {
                fclose(file);
        }
}
