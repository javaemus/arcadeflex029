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
 *
 */
package vidhrdw;

import static arcadeflex.libc.*;
import static mame.common.*;
import static mame.commonH.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static arcadeflex.osdepend.*;
import static vidhrdw.generic.*;
import static vidhrdw.vectorH.*;
import static vidhrdw.vector.*;
public class atari_vg 
{
    public static VhConvertColorPromPtr atari_vg_init_colors = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
    {
            int c,i,e;   	/* (c)olor, (i)ntensity, palette (e)ntry */
            int r,g,b;
            for (c=0; c<16; c++)
            {
                    for (i=0; i<16; i++)
                    {
                            e=c+i*16;
                            colortable[e]=(char)e;
                            r=color_prom[3*c  ]*i*0x18;
                            g=color_prom[3*c+1]*i*0x18;
                            b=color_prom[3*c+2]*i*0x18;
                            palette[3*e  ]=(char)((r < 256) ? r : 0xff);
                            palette[3*e+1]=(char)((g < 256) ? g : 0xff);
                            palette[3*e+2]=(char)((b < 256) ? b : 0xff);
                    }
            }
    }};
    public static VhConvertColorPromPtr sw_vg_init_colors = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom)
    {

	int c,i,e;   	/* (c)olor, (i)ntensity, palette (e)ntry */
	int r,g,b;
	for (c=0; c<8; c++)
	{
		for (i=0; i<32; i++)
		{
			e=c+i*8;
			colortable[e]=(char)e;
			r=(int)(color_prom[3*c  ]*i*8.25);
			g=(int)(color_prom[3*c+1]*i*8.25);
			b=(int)(color_prom[3*c+2]*i*8.25);
			palette[3*e  ]=(char)((r < 256) ? r : 0xff);
			palette[3*e+1]=(char)((g < 256) ? g : 0xff);
			palette[3*e+2]=(char)((b < 256) ? b : 0xff);
		}
	}
    }};
    /* If you want to use this, please make sure that you have
     * a fake GfxLayout, otherwise you'll crash */
    public static WriteHandlerPtr atari_vg_colorram_w = new WriteHandlerPtr() { public void handler(int offset,int data)
    {
            int i;

            data&=0x0f;
            for (i=0; i<16; i++)
                    Machine.gfx[0].colortable.write(offset+i*16,Machine.pens[data+i*16]);
    }};



    public static VhStopPtr atari_vg_stop = new VhStopPtr() { public void handler()
    {
            vg_stop ();
    }};


    public static VhUpdatePtr atari_vg_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap)
    {
    /* This routine does nothing - writes to vg_go start the drawing process */
    }};


    /*
     * AVG-games start like this
     */
    public static VhStartPtr atari_vg_avg_start = new VhStartPtr() { public int handler()
    {
            if (vg_init (0x1000, USE_AVG,0)!=0)
                    return 1;
            return 0;
    }};

    /*
     * DVG-games start like this
     */
    public static VhStartPtr atari_vg_dvg_start = new VhStartPtr() { public int handler()
    {
            if (vg_init (0x800, USE_DVG,0)!=0)
                    return 1;
            return 0;
    }};

    /*
     * Starwars starts like this
     */
    public static VhStartPtr atari_vg_avg_flip_start = new VhStartPtr() { public int handler()
    {
            if (vg_init(0x4000,USE_AVG,1)!=0)
                    return 1;
            return 0;
    }};

   
}
