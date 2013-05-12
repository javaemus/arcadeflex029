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
 *  ported to v0.28
 * 
 */
package vidhrdw;

import static arcadeflex.libc.*;
import static arcadeflex.osdepend.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.cpuintrf.*;

public class vector 
{
    static int dvg = 0;

    public static int x_res;	/* X-resolution of the display device */
    public static int y_res;	/* Y-resolution of the display device */

    /* This struct holds the actual X/Y coordinates the vector game uses.
     * Gets initialized in vg_init().
     */
    static class vgvideo
    {
     int width; int height;
             int x_cent; int y_cent;
	     int x_min; int x_max;
	     int y_min; int y_max;
         
    };
    static vgvideo vg_video;
    
    static int flip_word;	/* determines the endian-ness of the words read from vectorram */
    
    static final int DVG_SHIFT=10;		/* NOTE: must be even */
    static final int AVG_SHIFT=16;		/* NOTE: must be even */
    static final int RES_SHIFT=16;		/* NOTE: must be even */
    
    static int DVG_X(int x,int xs)
    {
        return ( ( ((x) >> DVG_SHIFT/2) * ((xs) >> RES_SHIFT/2) ) >> (RES_SHIFT/2 + DVG_SHIFT/2) );
    }
    static int DVG_Y(int y,int ys)
    {
        return ( ( ((y) >> DVG_SHIFT/2) * ((ys) >> RES_SHIFT/2) ) >> (RES_SHIFT/2 + DVG_SHIFT/2) );
    }
    static int AVG_X(int x,int xs)
    {
        return ( ( ((x) >> AVG_SHIFT/2) * ((xs) >> RES_SHIFT/2) ) >> (RES_SHIFT/2 + AVG_SHIFT/2) );
    }
    static int AVG_Y(int y,int ys)
    {
        return ( ( ((y) >> AVG_SHIFT/2) * ((ys) >> RES_SHIFT/2) ) >> (RES_SHIFT/2 + AVG_SHIFT/2) );
    }

    static int vg_step = 0; /* single step the vector generator */
    static int last_vgo_cyc=0;
    static int vgo_count=0;

    static int vg_busy = 0;
    static int vg_done_cyc = 0; /* cycle after which VG will be done */


    public static CharPtr vectorram = new CharPtr();
    
    static final int MAXSTACK= 8; 	/* Tempest needs more than 4     BW 210797 */

    static final int VCTR= 0;
    static final int HALT= 1;
    static final int SVEC= 2;
    static final int STAT= 3;
    static final int CNTR= 4;
    static final int JSRL= 5;
    static final int RTSL= 6;
    static final int JMPL= 7;
    static final int SCAL= 8;

    static final int DVCTR= 0x01;
    static final int DLABS= 0x0a;
    static final int DHALT= 0x0b;
    static final int DJSRL= 0x0c;
    static final int DRTSL= 0x0d;
    static final int DJMPL= 0x0e;
    static final int DSVEC= 0x0f;

    static int twos_comp_val(int num,int bits)
    {
        return (((num&(1<<(bits-1)))!=0)?(num|~((1<<bits)-1)):(num&((1<<bits)-1)));
    }
    static int map_addr(int n)
    {
        return (((n)<<1));
    }
    static int memrdwd(int offset,int PC,int cyc)
    {
        return ((vectorram.read(offset)) | (vectorram.read(offset+1)<<8));
    }
    static int memrdwd_flip(int offset,int PC,int cyc)
    {
        return ((vectorram.read(offset+1)) | (vectorram.read(offset)<<8));
    }
    static int max(int x, int y)
    {
        return (((x)>(y))?(x):(y));
    }
    static void vector_timer (int deltax, int deltay)
    {
            deltax = Math.abs (deltax);
            deltay = Math.abs (deltay);
 
            vg_done_cyc += max (deltax, deltay) >> (AVG_SHIFT+1);
    }


    static void dvg_vector_timer (int scale)
    {
            vg_done_cyc += (DVG_SHIFT-8) << scale;
    }
    static void dvg_draw_vector_list()
    {
        	int pc;
	int sp;
	int stack[]=new int[MAXSTACK];

	int scale;
	int statz;

	int xscale, yscale;
	int currentx, currenty;

	int done = 0;

	int firstwd;
	int secondwd = 0; /* Initialize to tease the compiler */
	int opcode;

	int x, y;
	int z, temp;
	int a;

	int deltax, deltay;
	
	pc = 0;
	sp = 0;
	scale = 0;
	statz = 0;

        int[] tempres=new int[2];
	tempres=open_page (0);//open_page (&x_res,&y_res,0);
        x_res=tempres[0];
        y_res=tempres[1];
	
	xscale = (x_res << RES_SHIFT)/vg_video.width;		/* ASG 080497 */
	yscale = (y_res << RES_SHIFT)/vg_video.height;		/* ASG 080497 */
	
	currentx = 0;
	currenty = 0;
	
  	while (done==0)
	{  
		vg_done_cyc += 8;
		if (flip_word!=0)
			firstwd = memrdwd_flip (map_addr (pc), 0, 0);
		else
			firstwd = memrdwd (map_addr (pc), 0, 0);
		opcode = firstwd >> 12;
		pc++;
		if ((opcode >= 0 /* DVCTR */) && (opcode <= DLABS))
		{
			if (flip_word!=0)
				secondwd = memrdwd_flip (map_addr (pc), 0, 0);
			else
				secondwd = memrdwd (map_addr (pc), 0, 0);
			pc++;
		}

		switch (opcode)
		{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
	  			y = firstwd & 0x03ff;
				if ((firstwd & 0x400)!=0)
					y=-y;
				x = secondwd & 0x3ff;
				if ((secondwd & 0x400)!=0)
					x=-x;
				z = secondwd >> 12;
	  			temp = ((scale + opcode) & 0x0f);
	  			if (temp > 9)
					temp = -1;
	  			deltax = (x << DVG_SHIFT) >> (9-temp);		/* ASG 080497 */
				deltay = (y << DVG_SHIFT) >> (9-temp);		/* ASG 080497 */
	  			currentx += deltax;
				currenty -= deltay;
				dvg_vector_timer (temp);
				/* ASG 080497 */
				draw_to (DVG_X (currentx, xscale), DVG_Y (currenty, yscale), z!=0 ? 7+(z<<4) : -1);
				break;

			case DLABS:
				x = twos_comp_val (secondwd, 12);
				y = twos_comp_val (firstwd, 12);
	  			scale = (secondwd >> 12);
                                currentx = ((x-vg_video.x_min) << DVG_SHIFT);		
				currenty = ((vg_video.y_max-y) << DVG_SHIFT);		
				break;

			case DHALT:
				done = 1;
				break;

			case DJSRL:
				a = firstwd & 0x0fff;
				stack [sp] = pc;
				if (sp == (MAXSTACK - 1))
	    			{
					if (errorlog!=null) fprintf (errorlog,"\n*** Vector generator stack overflow! ***\n");
					done = 1;
					sp = 0;
				}
				else
					sp++;
				pc = a;
				break;
	
			case DRTSL:
				if (sp == 0)
	    			{
					if (errorlog!=null) fprintf (errorlog,"\n*** Vector generator stack underflow! ***\n");
					done = 1;
					sp = MAXSTACK - 1;
				}
				else
					sp--;
				pc = stack [sp];
				break;

			case DJMPL:
				a = firstwd & 0x0fff;
				pc = a;
				break;
	
			case DSVEC:
				y = firstwd & 0x0300;
				if ((firstwd & 0x0400)!=0)
					y = -y;
				x = (firstwd & 0x03) << 8;
				if ((firstwd & 0x04)!=0)
					x = -x;
				z = (firstwd >> 4) & 0x0f;
				temp = 2 + ((firstwd >> 2) & 0x02) + ((firstwd >>11) & 0x01);
	  			temp = ((scale + temp) & 0x0f);
				if (temp > 9)
					temp = -1;

				deltax = (x << DVG_SHIFT) >> (9-temp);	/* ASG 080497 */
				deltay = (y << DVG_SHIFT) >> (9-temp);	/* ASG 080497 */
	  			currentx += deltax;
				currenty -= deltay;
				dvg_vector_timer (temp);
				/* ASG 080497 */
				draw_to (DVG_X (currentx, xscale), DVG_Y (currenty, yscale), z!=0 ? 7+(z<<4) : -1);
				break;

			default:
				if (errorlog!=null)
					fprintf (errorlog,"internal error\n");
				done = 1;
		}
	}
	close_page ();
    }
    static void avg_draw_vector_list()
    {
    	int pc;
	int sp;
	int stack[]=new  int[MAXSTACK];

	int xscale, yscale;

	int scale;
	int statz;
        int z_inline;
	int color;

	int currentx, currenty;
	int done = 0;

	int firstwd, secondwd;
	int opcode;

	int x, y, z, b, l, d, a;

	int deltax, deltay;

	pc = 0;
	sp = 0;
	statz = 0;
	color = 0;

	if (flip_word!=0) {
		firstwd = memrdwd_flip (map_addr (pc), 0, 0);
		secondwd = memrdwd_flip (map_addr (pc+1), 0, 0);
	} else {
		firstwd = memrdwd (map_addr (pc), 0, 0);
		secondwd = memrdwd (map_addr (pc+1), 0, 0);
	}
	if ((firstwd == 0) && (secondwd == 0)) {
		if (errorlog!=null) fprintf (errorlog,"VGO with zeroed vector memory\n");
		return;
	}

	int[] tempres=new int[2];
	tempres=open_page (0);//open_page (&x_res,&y_res,0);
        x_res=tempres[0];
        y_res=tempres[1];


	xscale = (x_res << RES_SHIFT)/vg_video.width;		/* ASG 080497 */
	yscale = (y_res << RES_SHIFT)/vg_video.height;		/* ASG 080497 */

	scale = 0;		/* ASG 080497 */
	currentx = vg_video.x_cent << AVG_SHIFT;		/* ASG 080497 */
	currenty = vg_video.y_cent << AVG_SHIFT;		/* ASG 080497 */

	while (done==0) {
	
		if (flip_word!=0)
			firstwd = memrdwd_flip (map_addr (pc), 0, 0);
		else
			firstwd = memrdwd (map_addr (pc), 0, 0);
		opcode = firstwd >> 13;
		pc++;
		if (opcode == VCTR) {
			if (flip_word!=0)
				secondwd = memrdwd_flip (map_addr (pc), 0, 0);
			else
				secondwd = memrdwd (map_addr (pc), 0, 0);
			pc++;
		}

		if ((opcode == STAT) && ((firstwd & 0x1000) != 0))
			opcode = SCAL;


		switch (opcode) {
		case VCTR:
			x = twos_comp_val (secondwd,13);
			y = twos_comp_val (firstwd,13);
			z_inline = (secondwd >>13); // SJB 17/8/97
			z = z_inline+z_inline;  // SJB 17/8/97
			if (z == 0) {
			} else if ((z == 2 && flip_word == 0) || flip_word == 1)  {
				z = statz;
			}
			deltax = x * scale;
			deltay = y * scale;
			currentx += deltax;
			currenty -= deltay;
			vector_timer (deltax, deltay);
			/* ASG 080497 */
                        if(flip_word!=0)
				draw_to (AVG_X (currentx, xscale), AVG_Y (currenty, yscale), z!=0 ? color+( ((z_inline*z)>>3) & 0xf8 ) : -1); // SJB 17/8/97
			else
				draw_to (AVG_X (currentx, xscale), AVG_Y (currenty, yscale), z!=0 ? color+(z<<4) : -1);
			break;
	
		case SVEC:
			x = twos_comp_val (firstwd, 5) << 1;
			y = twos_comp_val (firstwd >> 8, 5) << 1;
			z_inline = ((firstwd >> 5) & 7);
			z = z_inline+z_inline; // SJBNEW
			if (z == 0) {
			} else if ((z == 2 && flip_word == 0) || flip_word == 1)  {
				z = statz;
			}
			deltax = x * scale;
			deltay = y * scale;
			currentx += deltax;
			currenty -= deltay;
			vector_timer (deltax,deltay);
			if(flip_word!=0)
				draw_to (AVG_X (currentx, xscale), AVG_Y (currenty, yscale), z!=0 ? color+( ((z_inline*z)>>3) & 0xf8 ) : -1); // SJB 17/8/97
			else
				draw_to (AVG_X (currentx, xscale), AVG_Y (currenty, yscale), z!=0 ? color+(z<<4) : -1);
			break;
	
		case STAT:
			if (flip_word!=0) {
				color=(char)((firstwd & 0x0700)>>8); /* Colour code 0-7 stored in top 3 bits of `colour'*/
				statz = (firstwd &0xff);
			} else {
				color = firstwd & 0x0f;
				statz = (firstwd >> 4) & 0x0f;
        		}
			/* should do e, h, i flags here! */
			break;
      
		case SCAL:
			b = ((firstwd >> 8) & 0x07)+8;
			l = (~firstwd) & 0xff;
			scale = (l << AVG_SHIFT) >> b;		/* ASG 080497 */
			break;
	
		case CNTR:
			d = firstwd & 0xff;
			currentx = vg_video.x_cent << AVG_SHIFT;		/* ASG 080497 */
			currenty = vg_video.y_cent << AVG_SHIFT;		/* ASG 080497 */

			/* ASG 080497 */
			draw_to (AVG_X (currentx, xscale), AVG_Y (currenty, yscale), -1);
			break;
	
		case RTSL:
			if (sp == 0) {
				if (errorlog!=null)
					fprintf (errorlog,"\n*** Vector generator stack underflow! ***\n");
				done = 1;
				sp = MAXSTACK - 1;
			} else
				sp--;
			pc = stack [sp];
			break;

		case HALT:
			done = 1;
			break;

		case JMPL:
			a = firstwd & 0x1fff;
			/* if a = 0x0000, treat as HALT */
			if (a == 0x0000)
				done = 1;
			else
				pc = a;
			break;
	
		case JSRL:
			a = firstwd & 0x1fff;
			/* if a = 0x0000, treat as HALT */
			if (a == 0x0000)
				done = 1;
			else {
				stack [sp] = pc;
				if (sp == (MAXSTACK - 1)) {
					if (errorlog!=null)
						fprintf (errorlog,"\n*** Vector generator stack overflow! ***\n");
					done = 1;
					sp = 0;
				}
				else
					sp++;
				pc = a;
			}
			break;
	
		default:
			if (errorlog!=null)
				fprintf (errorlog,"internal error\n");
		}
	}

	close_page ();    
    }
    public static int vg_done (int cyc)
    {
	if (cyc-last_vgo_cyc>vg_done_cyc)
		vg_busy = 0;
	return NOT(vg_busy);
        
    }
    public static void vg_go (int cyc)
    {
  	if (errorlog!=null) {
		vgo_count++;
		fprintf (errorlog,"VGO #%d cpu-cycle %d vector cycles %d\n",
			vgo_count, cpu_gettotalcycles(), vg_done_cyc);
	}
	last_vgo_cyc=cyc;
	vg_busy=1;	
	vg_done_cyc = 8;
	if (dvg!=0)
		dvg_draw_vector_list ();
	else
		avg_draw_vector_list ();     
    }
    public static WriteHandlerPtr vg_reset = new WriteHandlerPtr() { public void handler(int offset, int data)
    {
       	vg_busy = 0;
	if (errorlog!=null)
		fprintf (errorlog,"vector generator reset @%04x\n",cpu_getpc()); 
    }};
    public static int vg_init (int len, int usingDvg, int flip)
    {
       	if (usingDvg!=0)
		dvg = 1;
	else
		dvg = 0;
	flip_word = flip;
        vg_step = 0;
	last_vgo_cyc = 0;
	vgo_count = 0;
	vg_busy = 0;
	vg_done_cyc = 0;
        vg_video=new vgvideo();
	vg_video.width =Machine.drv.visible_area.max_x-Machine.drv.visible_area.min_x;
	vg_video.height=Machine.drv.visible_area.max_y-Machine.drv.visible_area.min_y;
	vg_video.x_cent=(Machine.drv.visible_area.max_x+Machine.drv.visible_area.min_x)/2;
	vg_video.y_cent=(Machine.drv.visible_area.max_y+Machine.drv.visible_area.min_y)/2;
	vg_video.x_min=Machine.drv.visible_area.min_x;
	vg_video.y_min=Machine.drv.visible_area.min_y;
        vg_video.x_max=Machine.drv.visible_area.max_x;
	vg_video.y_max=Machine.drv.visible_area.max_y;

	return 0;
    }
    public static void vg_stop ()
    {
    }
    
}
