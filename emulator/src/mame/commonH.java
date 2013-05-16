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
 * ported to v0.29
 * ported to v0.28
 * ported to v0.27
 * 
 *
 *
 */
package mame;

import static arcadeflex.libc.*;
import static mame.osdependH.*;
public class commonH
{
	public static class RomModule
	{
		public RomModule(String n, int o, int s) { name = n; offset = o; length = s; };//function without CRC
		public String name;	/* name of the file to load */
		public int offset;			/* offset to load it to */
		public int length;			/* length of the file */
                public int checksum;                   /* our custom checksum */
                public RomModule(String n, int o, int s,int c) { name = n; offset = o; length = s; checksum=c;};//new definition for when we support checksum...
	};
        /* there are some special cases for the above. name, offset and size all set to 0 */
        /* mark the end of the aray. If name is 0 and the others aren't, that means "continue */
        /* reading the previous from from this address". If length is 0 and offset is not 0, */
        /* that marks the start of a new memory region. Confused? Well, don't worry, just use */
        /* the macros below. */
        
        static int TEMP_MODULE_SIZE=50;//TODO i don't like that but how else?
        static RomModule[] tempmodule = new RomModule[TEMP_MODULE_SIZE]; 
        static int curpos=0;
        static RomModule[] rommodule_macro=null;
        /* start of memory region */
        public static void ROM_REGION(int offset)
        {
           tempmodule[curpos]=new RomModule( null, offset, 0 );
           curpos++;
        }
        /* ROM to load */
        public static void ROM_LOAD(String name,int offset,int size,int crc)
        {
            tempmodule[curpos]=new RomModule( name,offset,size,crc);
            curpos++;
        }

        /* continue loading the previous ROM to a new address */
        public static void ROM_CONTINUE(int offset,int length)
        {
             tempmodule[curpos]=new RomModule( null,offset,length);
             curpos++;
        }
        /* restart loading the previous ROM to a new address */
        public static void ROM_RELOAD(int offset,int length)
        {
            tempmodule[curpos]=new RomModule( "-1",offset,length);
            curpos++;
        }
        /* load the ROM at even/odd addresses. Useful with 16 bit games */
        
        public static void ROM_LOAD_EVEN(String name,int offset,int length,int checksum) 
        { 
            tempmodule[curpos]=new RomModule(name, offset & ~1, length | 0x80000000, checksum);
            curpos++;
        }
        public static void ROM_LOAD_ODD(String name,int offset,int length,int checksum) 
        { 
            tempmodule[curpos]=new RomModule(name, offset |  1, length | 0x80000000, checksum);
            curpos++;
        }
        /* end of table */
        public static void ROM_END()
        {
            tempmodule[curpos]=new RomModule( null, 0, 0 );
            curpos++;
            rommodule_macro=null;
            rommodule_macro=new RomModule[curpos];
            System.arraycopy(tempmodule, 0, rommodule_macro, 0, curpos);
            curpos=0;//reset curpos
            tempmodule=null;
            tempmodule=new RomModule[TEMP_MODULE_SIZE];//reset tempmodule
        }
        public static class GameSample
	{
		public GameSample() {};
		public int length;
                public int smpfreq;
                public int resolution;
                public int volume;
		public byte data[];	/* extendable */
	};

	public static class GameSamples
	{
		public GameSamples() {};
		public int total;	/* total number of samples */
		public GameSample sample[];	/* extendable */
	};


	public static class GfxLayout
	{
		public GfxLayout(int w, int h, int t, int p, int po[], int x[], int y[], int ci) { width = w; height = h; total = t; planes = p; planeoffset = po; xoffset = x; yoffset = y; charincrement = ci; };
		public int width,height;	/* width and height of chars/sprites */
		public int total;	/* total numer of chars/sprites in the rom */
		public int planes;	/* number of bitplanes */
		public int planeoffset[];	/* start of every bitplane */
		public int xoffset[];	/* coordinates of the bit corresponding to the pixel */
		public int yoffset[];	/* of the given coordinates */
		public int charincrement;	/* distance between two consecutive characters/sprites */
	};



	public static class GfxElement
	{
		public GfxElement(int w, int h, osd_bitmap g, int te, int cg, CharPtr c, int tc) { width = w; height = h; gfxdata = g; total_elements = te; color_granularity = cg; colortable = c; total_colors = tc; }
		public GfxElement(int w, int h, int g, int te, int cg, int c, int tc) { this(w, h, null, te, cg, null, tc); }
		public GfxElement() {};
		public int width,height;

		public osd_bitmap gfxdata;	/* graphic data */
		public int total_elements;	/* total number of characters/sprites */

		public int color_granularity;	/* number of colors for each color code */
								/* (for example, 4 for 2 bitplanes gfx) */
		public CharPtr colortable;	/* map color codes to screen pens */
										/* if this is 0, the function does a verbatim copy */
		public int total_colors;
	};



	public static class rectangle
	{
		public rectangle() {};
		public rectangle(int minx, int maxx, int miny, int maxy) { min_x = minx; max_x = maxx; min_y = miny; max_y = maxy; };
		public int min_x,max_x;
		public int min_y,max_y;
	};

	public static final int TRANSPARENCY_NONE = 0;
	public static final int TRANSPARENCY_PEN = 1;
	public static final int TRANSPARENCY_COLOR = 2;
        public static final int TRANSPARENCY_THROUGH = 3;
}
