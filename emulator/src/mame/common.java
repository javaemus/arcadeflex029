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
 * ported to v0.28
 * ported to v0.27
 *
 *   NOTES: readroms doesn't support crc check
 *          readsamples and free samples are not implemented
 *          
 *
 */
package mame;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.mameH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static arcadeflex.osdepend.*;
import static mame.driverH.*;

/*********************************************************************

common.c

Generic functions, mostly ROM and graphics related.

 *********************************************************************/
public class common {

    public static void showdisclaimer() {
        printf("MAME is an emulator: it reproduces, more or less faithfully, the behaviour of\n"
                + "several arcade machines. But hardware is useless without software, so an image\n"
                + "of the ROMs which run on that hardware is required. Such ROMs, like any other\n"
                + "commercial software, are copyrighted material and it is therefore illegal to\n"
                + "use them if you don't own the original arcade machine. Needless to say, ROMs\n"
                + "are not distributed together with MAME. Distribution of MAME together with ROM\n"
                + "images is a violation of copyright law and should be promptly reported to the\n"
                + "authors so that appropriate legal action can be taken.\n\n", new Object[0]);
    }

    /***************************************************************************
    
    Read ROMs into memory.
    
    Arguments:
    const struct RomModule *romp - pointer to an array of Rommodule structures,
    as defined in common.h.
    
    const char *basename - Name of the directory where the files are
    stored. The function also supports the
    control sequence %s in file names: for example,
    if the RomModule gives the name "%s.bar", and
    the basename is "foo", the file "foo/foo.bar"
    will be loaded.
    
     ***************************************************************************/
    public static int readroms(RomModule[] rommodule, String basename) {
        int region;
        RomModule[] romp;


        romp = rommodule;

        for (region = 0; region < MAX_MEMORY_REGIONS; region++) {
            Machine.memory_region[region] = null;
        }

        region = 0;
        int _ptr = 0;

        while (romp[_ptr].name != null || romp[_ptr].offset != 0 || romp[_ptr].length != 0) {
            int region_size;


            if (romp[_ptr].name != null || romp[_ptr].length != 0) {
                printf("Error in RomModule definition: expecting ROM_REGION\n");
                return getout();
            }

            region_size = romp[_ptr].offset;
            if ((Machine.memory_region[region] = new char[region_size]) == null) {
                printf("Unable to allocate %d bytes of RAM\n", region_size);
                return getout();
            }

            /* some games (i.e. Pleiades) want the memory clear on startup */
            memset(Machine.memory_region[region], 0, region_size);

            _ptr++;

            while (romp[_ptr].length != 0) {
                FILE f;
                String buf;
                String name;


                if (romp[_ptr].name == null) {
                    printf("Error in RomModule definition: ROM_CONTINUE not preceded by ROM_LOAD\n");
                    return getout();
                }
                if (romp[_ptr].name.compareTo("-1") == 0) {
                    printf("Error in RomModule definition: ROM_RELOAD not preceded by ROM_LOAD\n", new Object[0]);
                    return getout();
                }
                buf = sprintf(romp[_ptr].name, basename);
                name = sprintf("%s/%s", basename, buf);

                if ((f = fopen(name, "rb")) == null) {
                    printf("Unable to open file %s\n", name);
                    return printromlist(rommodule, basename);
                }

                do {
                    if ((romp[_ptr].name != null) && (romp[_ptr].name.compareTo("-1") == 0)) {
                        System.out.println("ROM RELOAD");
                        fseek(f, 0);
                    }
                    if (romp[_ptr].offset + romp[_ptr].length > region_size) {
                        printf("Error in RomModule definition: %s out of memory region space\n", name);
                        fclose(f);
                        return getout();
                    }

                    if (fread(Machine.memory_region[region], romp[_ptr].offset, 1, romp[_ptr].length, f) != romp[_ptr].length) {
                        printf("Unable to read ROM %s\n", name);
                        fclose(f);
                        return printromlist(rommodule, basename);
                    }

                    _ptr++;
                } while (romp[_ptr].length != 0 && (romp[_ptr].name == null || romp[_ptr].name.compareTo("-1") == 0));

                fclose(f);
            }

            region++;
        }

        return 0;
    }

    static int printromlist(RomModule[] rommodule, String basename) {
        RomModule[] romp = rommodule;
        printf("\nMAME is an emulator: it reproduces, more or less faithfully, the behaviour of\n"
                + "several arcade machines. But hardware is useless without software, so an image\n"
                + "of the ROMs which run on that hardware is required. Such ROMs, like any other\n"
                + "commercial software, are copyrighted material and it is therefore illegal to\n"
                + "use them if you don't own the original arcade machine. Needless to say, ROMs\n"
                + "are not distributed together with MAME. Distribution of MAME together with ROM\n"
                + "images is a violation of copyright law and should be promptly reported to the\n"
                + "author so that appropriate legal action can be taken.\n\nPress return to continue\n");
        getchar();
        printf("This is the list of the ROMs required.\n"
                + "All the ROMs must reside in a subdirectory called \"%s\".\n"
                + "Name             Size\n", basename);
        int _ptr = 0;
        while (romp[_ptr].name != null || romp[_ptr].offset != 0 || romp[_ptr].length != 0) {
            _ptr++;	/* skip memory region definition */

            while (romp[_ptr].length != 0) {
                String name;
                int length;


                name = sprintf(romp[_ptr].name, basename);

                length = 0;

                do {
                    /* ROM_RELOAD */
                    if ((romp[_ptr].name != null) && (romp[_ptr].name.compareTo("-1") == 0)) {
                        length = 0;
                    }
                    length += romp[_ptr].length;

                    _ptr++;
                } while (romp[_ptr].length != 0 && (romp[_ptr].name == null || romp[_ptr].name.compareTo("-1") == 0));

                printf("%-12s %5d bytes\n", name, length);
            }
        }
        return getout();
    }

    static int getout() {
        for (int region = 0; region < MAX_MEMORY_REGIONS; region++) {
            Machine.memory_region[region] = null;
        }
        return 1;
    }

    /***************************************************************************
    
    Read samples into memory.
    This function is different from readroms() because it doesn't fail if
    it doesn't find a file: it will load as many samples as it can find.
    
     ***************************************************************************/
    /*struct GameSamples *readsamples(const char **samplenames,const char *basename)
    {
    int i;
    struct GameSamples *samples;
    
    if (samplenames == 0 || samplenames[0] == 0) return 0;
    
    i = 0;
    while (samplenames[i] != 0) i++;
    
    if ((samples = malloc(sizeof(struct GameSamples) + (i-1)*sizeof(struct GameSample))) == 0)
    return 0;
    
    samples.total = i;
    for (i = 0;i < samples.total;i++)
    samples.sample[i] = 0;
    
    for (i = 0;i < samples.total;i++)
    {
    FILE *f;
    char buf[100];
    char name[100];
    
    
    if (samplenames[i][0])
    {
    sprintf(buf,samplenames[i],basename);
    sprintf(name,"%s/%s",basename,buf);
    
    if ((f = fopen(name,"rb")) != 0)
    {
    if (fseek(f,0,SEEK_END) == 0)
    {
    int dummy;
    unsigned char smpvol=0, smpres=0;
    unsigned smplen=0, smpfrq=0;
    
    fseek(f,0,SEEK_SET);
    fread(buf,1,4,f);
    if (memcmp(buf, "MAME", 4) == 0) {
    fread(&smplen,1,4,f);         /* all datas are LITTLE ENDIAN */
    /*                          fread(&smpfrq,1,4,f);
    smplen = intelLong (smplen);  /* so convert them in the right endian-ness */
    /*                           smpfrq = intelLong (smpfrq);
    fread(&smpres,1,1,f);
    fread(&smpvol,1,1,f);
    fread(&dummy,1,2,f);
    if ((smplen != 0) && (samples.sample[i] = malloc(sizeof(struct GameSample) + (smplen)*sizeof(char))) != 0)
    {
    samples.sample[i].length = smplen;
    samples.sample[i].volume = smpvol;
    samples.sample[i].smpfreq = smpfrq;
    samples.sample[i].resolution = smpres;
    fread(samples.sample[i].data,1,smplen,f);
    }
    }
    }
    
    fclose(f);
    }
    }
    }
    
    return samples;
    }*/
    /*void freesamples(struct GameSamples *samples)
    {
    int i;
    
    
    if (samples == 0) return;
    
    for (i = 0;i < samples.total;i++)
    free(samples.sample[i]);
    
    free(samples);
    }*/
    /***************************************************************************
    
    Function to convert the information stored in the graphic roms into a
    more usable format.
    
    Back in the early '80s, arcade machines didn't have the memory or the
    speed to handle bitmaps like we do today. They used "character maps",
    instead: they had one or more sets of characters (usually 8x8 pixels),
    which would be placed on the screen in order to form a picture. This was
    very fast: updating a character mapped display is, rougly speaking, 64
    times faster than updating an equivalent bitmap display, since you only
    modify groups of 8x8 pixels and not the single pixels. However, it was
    also much less versatile than a bitmap screen, since with only 256
    characters you had to do all of your graphics. To overcome this
    limitation, some special hardware graphics were used: "sprites". A sprite
    is essentially a bitmap, usually larger than a character, which can be
    placed anywhere on the screen (not limited to character boundaries) by
    just telling the hardware the coordinates. Moreover, sprites can be
    flipped along the major axis just by setting the appropriate bit (some
    machines can flip characters as well). This saves precious memory, since
    you need only one copy of the image instead of four.
    
    What about colors? Well, the early machines had a limited palette (let's
    say 16-32 colors) and each character or sprite could not use all of them
    at the same time. Characters and sprites data would use a limited amount
    of bits per pixel (typically 2, which allowed them to address only four
    different colors). You then needed some way to tell to the hardware which,
    among the available colors, were the four colors. This was done using a
    "color attribute", which typically was an index for a lookup table.
    
    OK, after this brief and incomplete introduction, let's come to the
    purpose of this section: how to interpret the data which is stored in
    the graphic roms. Unfortunately, there is no easy answer: it depends on
    the hardware. The easiest way to find how data is encoded, is to start by
    making a bit by bit dump of the rom. You will usually be able to
    immediately recognize some pattern: if you are lucky, you will see
    letters and numbers right away, otherwise you will see something which
    looks like letters and numbers, but with halves switched, dilated, or
    something like that. You'll then have to find a way to put it all
    together to obtain our standard one byte per pixel representation. Two
    things to remember:
    - keep in mind that every pixel has typically two (or more) bits
    associated with it, and they are not necessarily near to each other.
    - characters might be rotated 90 degrees. That's because many games used a
    tube rotated 90 degrees. Think how your monitor would look like if you
    put it on its side ;-)
    
    After you have successfully decoded the characters, you have to decode
    the sprites. This is usually more difficult, because sprites are larger,
    maybe have more colors, and are more difficult to recognize when they are
    messed up, since they are pure graphics without letters and numbers.
    However, with some work you'll hopefully be able to work them out as
    well. As a rule of thumb, the sprites should be encoded in a way not too
    dissimilar from the characters.
    
     ***************************************************************************/
    private static int readbit(CharPtr src, int bitnum) {
        int bit;


        bit = src.read(bitnum / 8) << (bitnum % 8);

        if ((bit & 0x80) != 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public static void decodechar(GfxElement gfx, int num, CharPtr src, GfxLayout gl) {
        int plane;

	for (plane = 0;plane < gl.planes;plane++)
	{
		int offs,y;


		offs = num * gl.charincrement + gl.planeoffset[plane];

		for (y = 0;y < gfx.height;y++)
		{
			int x;
			char[] dp;


			dp = gfx.gfxdata.line[num * gfx.height + y];

			for (x = 0;x < gfx.width;x++)
			{
				int xoffs,yoffs;


				if (plane == 0) dp[x] = 0;
				else dp[x] <<= 1;

				xoffs = x;
				yoffs = y;

				if ((Machine.orientation & ORIENTATION_FLIP_X)!=0)
					xoffs = gfx.width-1 - xoffs;

				if ((Machine.orientation & ORIENTATION_FLIP_Y)!=0)
					yoffs = gfx.height-1 - yoffs;

				if ((Machine.orientation & ORIENTATION_SWAP_XY)!=0)
				{
					int temp;


					temp = xoffs;
					xoffs = yoffs;
					yoffs = temp;
				}

				dp[x] += readbit(src,offs + gl.yoffset[yoffs] + gl.xoffset[xoffs]);
			}
		}
	}
    }
    public static GfxElement decodegfx(CharPtr src, GfxLayout gl) {

	int c;
	osd_bitmap bm;
	GfxElement gfx;


	if ((gfx = new GfxElement()) == null)
		return null;

	if ((Machine.orientation & ORIENTATION_SWAP_XY)!=0)
	{
		gfx.width = gl.height;
		gfx.height = gl.width;

		if ((bm = osd_create_bitmap(gl.total * gfx.height,gfx.width)) == null)
		{
			gfx=null;
			return null;
		}
	}
	else
	{
		gfx.width = gl.width;
		gfx.height = gl.height;

		if ((bm = osd_create_bitmap(gfx.width,gl.total * gfx.height)) == null)
		{
			gfx=null;
			return null;
		}
	}

	gfx.total_elements = gl.total;
	gfx.color_granularity = 1 << gl.planes;
	gfx.gfxdata = bm;

	for (c = 0;c < gl.total;c++)
		decodechar(gfx,c,src,gl);

	return gfx;
    }
    public static void freegfx(GfxElement gfx) {
        if (gfx != null) {
            osd_free_bitmap(gfx.gfxdata);
            gfx.gfxdata = null;
            gfx = null;
        }
    }

    /***************************************************************************
    
    Draw graphic elements in the specified bitmap.
    
    transparency == TRANSPARENCY_NONE - no transparency.
    transparency == TRANSPARENCY_PEN - bits whose _original_ value is == transparent_color
    are transparent. This is the most common kind of
    transparency.
    transparency == TRANSPARENCY_COLOR - bits whose _remapped_ value is == Machine.pens[transparent_color]
    are transparent. This is used by e.g. Pac Man.
    transparency == TRANSPARENCY_THROUGH - if the _destination_ pixel is == Machine.pens[transparent_color],
    the source pixel is drawn over it. This is used by
    e.g. Jr. Pac Man to draw the sprites when the background
    has priority over them.
     ***************************************************************************/
    public static void drawgfx(osd_bitmap dest, GfxElement gfx,
            int code, int color, int flipx, int flipy, int sx, int sy,
            rectangle clip, int transparency, int transparent_color) {
        int ox, oy, ex, ey, x, y, start, dy;
        CharPtr sd = new CharPtr();
        CharPtr bm = new CharPtr();
        int col;
        rectangle myclip=new rectangle();

        if (gfx == null) {
            return;
        }
	if ((Machine.orientation & ORIENTATION_SWAP_XY)!=0)
	{
		int temp;

		temp = sx;
		sx = sy;
		sy = temp;

		temp = flipx;
		flipx = flipy;
		flipy = temp;

		if (clip!=null)
		{
			myclip.min_x = clip.min_y;
			myclip.max_x = clip.max_y;
			myclip.min_y = clip.min_x;
			myclip.max_y = clip.max_x;
			clip = myclip;
		}
	}
	if ((Machine.orientation & ORIENTATION_FLIP_X)!=0)
	{
		sx = dest.width - gfx.width - sx;
		if (clip!=null)
		{
			int temp;


			temp = clip.min_x;
			myclip.min_x = dest.width-1 - clip.max_x;
			myclip.max_x = dest.width-1 - temp;
			myclip.min_y = clip.min_y;
			myclip.max_y = clip.max_y;
			clip = myclip;
		}
	}
	if ((Machine.orientation & ORIENTATION_FLIP_Y)!=0)
	{
		int temp;


		sy = dest.height - gfx.height - sy;
		if (clip!=null)
		{
			myclip.min_x = clip.min_x;
			myclip.max_x = clip.max_x;
			temp = clip.min_y;
			myclip.min_y = dest.height-1 - clip.max_y;
			myclip.max_y = dest.height-1 - temp;
			clip = myclip;
		}
	}
        /* check bounds */
        ox = sx;
        oy = sy;
        ex = sx + gfx.width - 1;
        if (sx < 0) {
            sx = 0;
        }
        if (clip != null && sx < clip.min_x) {
            sx = clip.min_x;
        }
        if (ex >= dest.width) {
            ex = dest.width - 1;
        }
        if (clip != null && ex > clip.max_x) {
            ex = clip.max_x;
        }
        if (sx > ex) {
            return;
        }
        ey = sy + gfx.height - 1;
        if (sy < 0) {
            sy = 0;
        }
        if (clip != null && sy < clip.min_y) {
            sy = clip.min_y;
        }
        if (ey >= dest.height) {
            ey = dest.height - 1;
        }
        if (clip != null && ey > clip.max_y) {
            ey = clip.max_y;
        }
        if (sy > ey) {
            return;
        }

        /* start = (code % gfx.total_elements) * gfx.height; */
        if (flipy != 0) /* Y flop */ {
            start = (code % gfx.total_elements) * gfx.height + gfx.height - 1 - (sy - oy);
            dy = -1;
        } else /* normal */ {
            start = (code % gfx.total_elements) * gfx.height + (sy - oy);
            dy = 1;
        }


        /* if necessary, remap the transparent color */
        if (transparency == TRANSPARENCY_COLOR || transparency == TRANSPARENCY_THROUGH) {
            transparent_color = Machine.pens[transparent_color];
        }


        if (gfx.colortable != null) /* remap colors */ {
            CharPtr paldata;
            paldata = new CharPtr(gfx.colortable, gfx.color_granularity * (color % gfx.total_colors));

            switch (transparency) {
                case TRANSPARENCY_NONE:
                    if (flipx != 0) /* X flip */ {
                        //System.out.println("A)TRANSPARENCY_NONE FLIPX");
                        for (y = sy; y <= ey; y++) {
                            bm.set(dest.line[y], sx);
                            sd.set(gfx.gfxdata.line[start], gfx.width - 1 - (sx - ox));
                            for (x = sx; x <= ex - 7; x += 8) {
                                bm.writeinc(paldata.read(sd.readdec()));
                                bm.writeinc(paldata.read(sd.readdec()));
                                bm.writeinc(paldata.read(sd.readdec()));
                                bm.writeinc(paldata.read(sd.readdec()));
                                bm.writeinc(paldata.read(sd.readdec()));
                                bm.writeinc(paldata.read(sd.readdec()));
                                bm.writeinc(paldata.read(sd.readdec()));
                                bm.writeinc(paldata.read(sd.readdec()));
                                /* bm+=7; */
                            }
                            for (; x <= ex; x++) {
                                bm.writeinc(paldata.read(sd.readdec()));
                            }
                            start += dy;
                        }
                    } else /* normal */ {
                        //System.out.println("A)TRANSPARENCY_NONE NORMAL");
                        for (y = sy; y <= ey; y++) {
                            bm.set(dest.line[y], sx);
                            sd.set(gfx.gfxdata.line[start], (sx - ox));
                            for (x = sx; x <= ex - 7; x += 8) {
                                bm.writeinc(paldata.read(sd.readinc()));
                                bm.writeinc(paldata.read(sd.readinc()));
                                bm.writeinc(paldata.read(sd.readinc()));
                                bm.writeinc(paldata.read(sd.readinc()));
                                bm.writeinc(paldata.read(sd.readinc()));
                                bm.writeinc(paldata.read(sd.readinc()));
                                bm.writeinc(paldata.read(sd.readinc()));
                                bm.writeinc(paldata.read(sd.readinc()));
                            }
                            for (; x <= ex; x++) {
                                bm.writeinc(paldata.read(sd.readinc()));
                            }
                            start += dy;
                        }
                    }
                    break;

                case TRANSPARENCY_PEN:
                    if (flipx != 0) /* X flip */ {
                        //System.out.println("A)TRANSPARENCY_PEN FLIPX");
                        IntPtr sd4 = new IntPtr();
                        int trans4, col4;

                        trans4 = transparent_color * 0x01010101;
                        for (y = sy; y <= ey; y++) {
                            bm.set(dest.line[y], sx);
                            sd4.set(gfx.gfxdata.line[start], (gfx.width - 1 - (sx - ox) - 3));
                            //sd4 = (int *)(gfx.gfxdata.line[start] + gfx.width -1 - (sx-ox) -3);
                            for (x = sx; x <= ex - 3; x += 4) {
                                if ((col4 = sd4.read()) != trans4) {
                                    //col4 = intelLong(col4); /* LBO */
                                    col = (col4 >> 24) & 0xff;
                                    if (col != transparent_color) {
                                        bm.write(0, paldata.read(col));
                                    }
                                    col = (col4 >> 16) & 0xff;
                                    if (col != transparent_color) {
                                        bm.write(1, paldata.read(col));
                                    }
                                    col = (col4 >> 8) & 0xff;
                                    if (col != transparent_color) {
                                        bm.write(2, paldata.read(col));
                                    }
                                    col = col4 & 0xff;
                                    if (col != transparent_color) {
                                        bm.write(3, paldata.read(col));
                                    }
                                }
                                bm.inc();
                                bm.inc();
                                bm.inc();
                                bm.inc();
                                sd4.dec();
                            }
                            //sd = (unsigned char *)sd4 + 3;
                            sd.set(sd4.readCA(), sd4.getBase() + 3);

                            for (; x <= ex; x++) {
                                //col =  * (sd--);
                                col = sd.readdec();
                                if (col != transparent_color) {
                                    bm.write(col);
                                }
                                bm.inc();
                            }
                            start += dy;

                        }
                    } else /* normal */ {
                        //System.out.println("A)TRANSPARENCY_PEN NORMAL");
                        IntPtr sd4 = new IntPtr();
                        int trans4, col4;

                        trans4 = transparent_color * 0x01010101;
                        for (y = sy; y <= ey; y++) {
                            bm.set(dest.line[y], sx);
                            //sd4 = (int *)(gfx.gfxdata.line[start] + (sx-ox));
                            sd4.set(gfx.gfxdata.line[start], (sx - ox));
                            for (x = sx; x <= ex - 3; x += 4) {
                                if ((col4 = sd4.read()) != trans4) {
                                    //col4 = intelLong (col4); /* LBO */
                                    col = col4 & 0xff;
                                    if (col != transparent_color) {
                                        bm.write(0, paldata.read(col));
                                    }
                                    col = (col4 >> 8) & 0xff;
                                    if (col != transparent_color) {
                                        bm.write(1, paldata.read(col));
                                    }
                                    col = (col4 >> 16) & 0xff;
                                    if (col != transparent_color) {
                                        bm.write(2, paldata.read(col));
                                    }
                                    col = (col4 >> 24) & 0xff;
                                    if (col != transparent_color) {
                                        bm.write(3, paldata.read(col));
                                    }
                                }
                                //bm+=4;
                                bm.inc();
                                bm.inc();
                                bm.inc();
                                bm.inc();
                                sd4.inc();
                            }
                            //sd = (unsigned char *)sd4;
                            sd.set(sd4.readCA(), sd4.getBase());
                            for (; x <= ex; x++) {
                                col = sd.readinc();
                                if (col != transparent_color) {
                                    bm.write(col);
                                }
                                bm.inc();
                            }
                            start += dy;
                        }
                    }
                    break;

                case TRANSPARENCY_COLOR:
                    if (flipx != 0) /* X flip */ {
                        //System.out.println("A)TRANSPARENCY_COLOR FLIPX");
                        for (y = sy; y <= ey; y++) {
                            bm.set(dest.line[y], sx);
                            sd.set(gfx.gfxdata.line[start], (gfx.width - 1 - (sx - ox)));
                            for (x = sx; x <= ex; x++) {
                                col = paldata.read(sd.readdec());
                                if (col != transparent_color) {
                                    bm.write(col);
                                }
                                bm.inc();
                            }
                            start += dy;
                        }
                    } else /* normal */ {
                        //System.out.println("A)TRANSPARENCY_COLOR NORMAL");
                        for (y = sy; y <= ey; y++) {
                            bm.set(dest.line[y], sx);
                            sd.set(gfx.gfxdata.line[start], (sx - ox));

                            for (x = sx; x <= ex; x++) {
                                col = paldata.read(sd.readinc());
                                if (col != transparent_color) {
                                    bm.write(col);
                                }
                                bm.inc();
                            }
                            start += dy;
                        }
                    }
                    break;

                case TRANSPARENCY_THROUGH:
                    if (flipx != 0) /* X flip */ {
                        //System.out.println("A)TRANSPARENCY_THROUGH FLIPX");
                        for (y = sy; y <= ey; y++) {
                            //bm = dest.line[y] + sx;
                            bm.set(dest.line[y], sx);
                            //sd = gfx.gfxdata.line[start] + gfx.width-1 - (sx-ox);
                            sd.set(gfx.gfxdata.line[start], gfx.width - 1 - (sx - ox));

                            for (x = sx; x <= ex; x++) {
                                if (bm.read() == transparent_color) {
                                    bm.write(paldata.read(sd.read()));
                                }
                                bm.inc();
                                sd.dec();
                            }
                            start += dy;
                        }
                    } else /* normal */ {
                        //System.out.println("A)TRANSPARENCY_THROUGH NORMAL");
                        for (y = sy; y <= ey; y++) {
                            bm.set(dest.line[y], sx);
                            sd.set(gfx.gfxdata.line[start], (sx - ox));
                            for (x = sx; x <= ex; x++) {
                                if (bm.read() == transparent_color) {
                                    bm.write(paldata.read(sd.read()));
                                }

                                bm.inc();
                                sd.inc();
                            }
                            start += dy;
                        }
                    }
                    break;
            }
        } else {
            switch (transparency) {
                case TRANSPARENCY_NONE:		/* do a verbatim copy (faster) */
                    if (flipx != 0) /* X flip */ {
                        System.out.println("B)TRANSPARENCY_NONE FLIPX *untested!");
                        for (y = sy; y <= ey; y++) {
                            bm.set(dest.line[y], sx);
                            sd.set(gfx.gfxdata.line[start], gfx.width - 1 - (sx - ox));

                            for (x = sx; x <= ex - 7; x += 8) {
                                //*(bm++) = *(sd--);
                                bm.writeinc(sd.readdec());
                                bm.writeinc(sd.readdec());
                                bm.writeinc(sd.readdec());
                                bm.writeinc(sd.readdec());
                                bm.writeinc(sd.readdec());
                                bm.writeinc(sd.readdec());
                                bm.writeinc(sd.readdec());
                                bm.writeinc(sd.readdec());                            
                            }
                            for (; x <= ex; x++) {
                                //* (bm++) =  * (sd--);
                                bm.writeinc(sd.readdec());
                            }
                            start += dy;
                        }
                    } else /* normal */ {
                        //System.out.println("B)TRANSPARENCY_NONE NORMAL");
                        for (y = sy; y <= ey; y++) {
                            bm.set(dest.line[y], sx);
                            sd.set(gfx.gfxdata.line[start], (sx - ox));
                            memcpy(bm, sd, ex - sx + 1);
                            start += dy;
                        }
                    }
                    break;

                case TRANSPARENCY_PEN:
                case TRANSPARENCY_COLOR:
                    if (flipx != 0) /* X flip */ {
                       // System.out.println("B)TRANS PEN-COLOR FLIPX *untested!");
                        IntPtr sd4 = new IntPtr();
                        int trans4, col4;

                        trans4 = transparent_color * 0x01010101;

                        for (y = sy; y <= ey; y++) {
                            bm.set(dest.line[y], sx);


                            sd4.set(gfx.gfxdata.line[start], gfx.width - 1 - (sx - ox) - 3);

                            for (x = sx; x <= ex - 3; x += 4) {
                                if ((col4 = sd4.read()) != trans4) {
                                    //col4 = intelLong (col4); /* LBO */
                                    col = col4 >> 24;
                                    if (col != transparent_color) {
                                        bm.write(0, col);
                                    }
                                    col = (col4 >> 16) & 0xff;
                                    if (col != transparent_color) {
                                        bm.write(1, col);
                                    };
                                    col = (col4 >> 8) & 0xff;
                                    if (col != transparent_color) {
                                        bm.write(2, col);
                                    };
                                    col = col4 & 0xff;
                                    if (col != transparent_color) {
                                        bm.write(3, col);
                                    };
                                }
                                bm.inc();
                                bm.inc();
                                bm.inc();
                                bm.inc();
                                sd4.dec();
                            }
                            //sd = (unsigned char *)sd4+3;
                            sd.set(sd4.readCA(), sd4.getBase() + 3);

                            for (; x <= ex; x++) {
                                //col = *(sd--);
                                col = sd.readdec();
                                if (col != transparent_color) {
                                    bm.write(col);
                                }

                                bm.inc();
                            }
                            start += dy;
                        }
                    } else /* normal */ {
                        // System.out.println("B)TRANS PEN-COLOR NORMAL");
                        IntPtr sd4 = new IntPtr();
                        int trans4, col4;
                        int xod4;

                        trans4 = transparent_color * 0x01010101;

                        for (y = sy; y <= ey; y++) {
                            bm.set(dest.line[y], sx);
                            sd4.set(gfx.gfxdata.line[start], (sx - ox));

                            for (x = sx; x <= ex - 3; x += 4) {
                                if ((col4 = sd4.read()) != trans4) {
                                    xod4 = col4 ^ trans4;
                                    if (((xod4 & 0x000000ff) != 0) && ((xod4 & 0x0000ff00) != 0) && ((xod4 & 0x00ff0000) != 0) && ((xod4 & 0xff000000) != 0)) {
                                        // A
                                        //*(int *)bm = col4; //copy col4 to a 32bit memory of bm
                                        bm.write(0, col4 & 0xff);
                                        bm.write(1, (col4 >> 8) & 0xff);
                                        bm.write(2, (col4 >> 16) & 0xff);
                                        bm.write(3, (col4 >> 24) & 0xff);
                                    } else {
                                        // auta ta dio asta etsi
                                        //col4 = intelLong (col4); /* LBO */
                                        //xod4 = intelLong (xod4); /* LBO */

                                        if ((xod4 & 0x000000ff) != 0) {
                                            // B
                                            bm.write(0, col4 & 0xff);
                                        }
                                        if ((xod4 & 0x0000ff00) != 0) {
                                            //C
                                            bm.write(1, (col4 >> 8) & 0xff);
                                        }
                                        if ((xod4 & 0x00ff0000) != 0) {
                                            //D
                                            bm.write(2, (col4 >> 16) & 0xff);
                                        }
                                        if ((xod4 & 0xff000000) != 0) {
                                            //E
                                            bm.write(3, (col4 >> 24) & 0xff);//doesn't sign extended in mame but it crashes otherwise
                                        }
                                    }
                                }
                                bm.inc();
                                bm.inc();
                                bm.inc();
                                bm.inc();
                                sd4.inc();
                            }
                            //sd = (unsigned char *)sd4;
                            sd.set(sd4.readCA(), sd4.getBase());

                            for (; x <= ex; x++) {
                                col = sd.readinc();
                                if (col != transparent_color) {
                                    bm.write(col);

                                }
                                bm.inc();
                            }
                            start += dy;
                        }
                    }
                    break;

                case TRANSPARENCY_THROUGH:
                    if (flipx != 0) /* X flip */ {
                        //System.out.println("B)TRANS THROUGH FLIPX *untested!");
                        for (y = sy; y <= ey; y++) {
                            bm.set(dest.line[y], sx);
                            sd.set(gfx.gfxdata.line[start], gfx.width - 1 - (sx - ox));


                            for (x = sx; x <= ex; x++) {
                                if (bm.read() == transparent_color) //*bm = *sd;
                                {
                                    bm.write(sd.read());
                                }

                                bm.inc();
                                sd.dec();

                            }
                            start += dy;
                        }
                    } else /* normal */ {
                        //System.out.println("B)TRANS THROUGH NORMAL *untested!");
                        for (y = sy; y <= ey; y++) {
                            bm.set(dest.line[y], sx);
                            sd.set(gfx.gfxdata.line[start], (sx - ox));

                            for (x = sx; x <= ex; x++) {
                                if (bm.read() == transparent_color) //*bm = *sd;
                                {
                                    bm.write(sd.read());
                                }



                                bm.inc();
                                sd.inc();
                            }
                            start += dy;
                        }
                    }
                    break;
            }
        }
    }
    /***************************************************************************
    
    Use drawgfx() to copy a bitmap onto another at the given position.
    This function will very likely change in the future.
    
     ***************************************************************************/
    static GfxElement mygfx = new GfxElement(
            0, 0, 0, /* filled in later */
            1, 1, 0, 1);

    public static void copybitmap(osd_bitmap dest, osd_bitmap src, int flipx, int flipy, int sx, int sy,
            rectangle clip, int transparency, int transparent_color) {

        mygfx.width = src.width;
        mygfx.height = src.height;
        mygfx.gfxdata = src;
        drawgfx(dest, mygfx, 0, 0, flipx, flipy, sx, sy, clip, transparency, transparent_color);
    }

    /***************************************************************************
    
    Copy a bitmap onto another with scroll and wraparound.
    This function supports multiple independently scrolling rows/columns.
    "rows" is the number of indepentently scrolling rows. "rowscroll" is an
    array of integers telling how much to scroll each row. Same thing for
    "cols" and "colscroll".
    If the bitmap cannot scroll in one direction, set rows or columns to 0.
    If the bitmap scrolls as a whole, set rows and/or cols to 1.
    Bidirectional scrolling is supported only if the bitmap scrolls as a whole.
    
     ***************************************************************************/
    public static void copyscrollbitmap(osd_bitmap dest, osd_bitmap src,
            int rows, int[] rowscroll, int cols, int[] colscroll,
            rectangle clip, int transparency, int transparent_color) {
        
        int srcwidth,srcheight;


	if ((Machine.orientation & ORIENTATION_SWAP_XY)!=0)
	{
		srcwidth = src.height;
		srcheight = src.width;
	}
	else
	{
		srcwidth = src.width;
		srcheight = src.height;
	}
        if (rows == 0) {
            /* scrolling columns */
            int col, colwidth;
            rectangle myclip = new rectangle();


            colwidth = srcwidth / cols;

            myclip.min_y = clip.min_y;
            myclip.max_y = clip.max_y;

            col = 0;
            while (col < cols) {
                int cons, scroll;


                /* count consecutive columns scrolled by the same amount */
                scroll = colscroll[col];
                cons = 1;
                while (col + cons < cols && colscroll[col + cons] == scroll) {
                    cons++;
                }

                if (scroll < 0) {
                    scroll = srcheight - (-scroll) % srcheight;
                } else {
                    scroll %= srcheight;
                }

                myclip.min_x = col * colwidth;
                if (myclip.min_x < clip.min_x) {
                    myclip.min_x = clip.min_x;
                }
                myclip.max_x = (col + cons) * colwidth - 1;
                if (myclip.max_x > clip.max_x) {
                    myclip.max_x = clip.max_x;
                }

                copybitmap(dest, src, 0, 0, 0, scroll, myclip, transparency, transparent_color);
                copybitmap(dest,src,0,0,0,scroll - srcheight,myclip,transparency,transparent_color);

                col += cons;
            }
        } else if (cols == 0) {
            /* scrolling rows */
            int row, rowheight;
            rectangle myclip = new rectangle();


            rowheight = srcheight / rows;

            myclip.min_x = clip.min_x;
            myclip.max_x = clip.max_x;

            row = 0;
            while (row < rows) {
                int cons, scroll;


                /* count consecutive rows scrolled by the same amount */
                scroll = rowscroll[row];
                cons = 1;
                while (row + cons < rows && rowscroll[row + cons] == scroll) {
                    cons++;
                }

                if (scroll < 0) {
                    scroll = srcwidth - (-scroll) % srcwidth;
                } else {
                   scroll %= srcwidth;
                }

                myclip.min_y = row * rowheight;
                if (myclip.min_y < clip.min_y) {
                    myclip.min_y = clip.min_y;
                }
                myclip.max_y = (row + cons) * rowheight - 1;
                if (myclip.max_y > clip.max_y) {
                    myclip.max_y = clip.max_y;
                }

                copybitmap(dest, src, 0, 0, scroll, 0, myclip, transparency, transparent_color);
                copybitmap(dest,src,0,0,scroll - srcwidth,0,myclip,transparency,transparent_color);

                row += cons;
            }
        } else if (rows == 1 && cols == 1) {
            /* XY scrolling playfield */
            int scrollx, scrolly;


            if (rowscroll[0] < 0) {
                scrollx = srcwidth - (-rowscroll[0]) % srcwidth;
            } else {
                scrollx = rowscroll[0] % srcwidth;
            }

            if (colscroll[0] < 0) {
                scrolly = srcheight - (-colscroll[0]) % srcheight;
            } else {
                scrolly = colscroll[0] % srcheight;
            }

            copybitmap(dest, src, 0, 0, scrollx, scrolly, clip, transparency, transparent_color);
  	    copybitmap(dest,src,0,0,scrollx,scrolly - srcheight,clip,transparency,transparent_color);
	    copybitmap(dest,src,0,0,scrollx - srcwidth,scrolly,clip,transparency,transparent_color);
	    copybitmap(dest,src,0,0,scrollx - srcwidth,scrolly - srcheight,clip,transparency,transparent_color);
        } else if (rows == 1) {
            /* scrolling columns + horizontal scroll */
            int col, colwidth;
            int scrollx;
            rectangle myclip = new rectangle();


            if (rowscroll[0] < 0) {
                scrollx = srcwidth - (-rowscroll[0]) % srcwidth;
            } else {
                scrollx = rowscroll[0] % srcwidth;
            }

            colwidth = srcwidth / cols;

            myclip.min_y = clip.min_y;
            myclip.max_y = clip.max_y;

            col = 0;
            while (col < cols) {
                int cons, scroll;


                /* count consecutive columns scrolled by the same amount */
                scroll = colscroll[col];
                cons = 1;
                while (col + cons < cols && colscroll[col + cons] == scroll) {
                    cons++;
                }

                if (scroll < 0) {
                    scroll = srcheight - (-scroll) % srcheight;
                } else {
                    scroll %= srcheight;
                }

                myclip.min_x = col * colwidth + scrollx;
                if (myclip.min_x < clip.min_x) {
                    myclip.min_x = clip.min_x;
                }
                myclip.max_x = (col + cons) * colwidth - 1 + scrollx;
                if (myclip.max_x > clip.max_x) {
                    myclip.max_x = clip.max_x;
                }

                copybitmap(dest, src, 0, 0, scrollx, scroll, myclip, transparency, transparent_color);
               copybitmap(dest,src,0,0,scrollx,scroll - srcheight,myclip,transparency,transparent_color);

                myclip.min_x = col * colwidth + scrollx - srcwidth;
		if (myclip.min_x < clip.min_x) myclip.min_x = clip.min_x;
		myclip.max_x = (col + cons) * colwidth - 1 + scrollx - srcwidth;
		if (myclip.max_x > clip.max_x) myclip.max_x = clip.max_x;

		copybitmap(dest,src,0,0,scrollx - srcwidth,scroll,myclip,transparency,transparent_color);
		copybitmap(dest,src,0,0,scrollx - srcwidth,scroll - srcheight,myclip,transparency,transparent_color);

                col += cons;
            }
        } else if (cols == 1) {
            /* scrolling rows + vertical scroll */
            int row, rowheight;
            int scrolly;
            rectangle myclip = new rectangle();


  		if (colscroll[0] < 0) scrolly = srcheight - (-colscroll[0]) % srcheight;
		else scrolly = colscroll[0] % srcheight;

		rowheight = srcheight / rows;

		myclip.min_x = clip.min_x;
		myclip.max_x = clip.max_x;

		row = 0;
		while (row < rows)
		{
			int cons,scroll;


			/* count consecutive rows scrolled by the same amount */
			scroll = rowscroll[row];
			cons = 1;
			while (row + cons < rows &&	rowscroll[row + cons] == scroll)
				cons++;

			if (scroll < 0) scroll = srcwidth - (-scroll) % srcwidth;
			else scroll %= srcwidth;

			myclip.min_y = row * rowheight + scrolly;
			if (myclip.min_y < clip.min_y) myclip.min_y = clip.min_y;
			myclip.max_y = (row + cons) * rowheight - 1 + scrolly;
			if (myclip.max_y > clip.max_y) myclip.max_y = clip.max_y;

			copybitmap(dest,src,0,0,scroll,scrolly,myclip,transparency,transparent_color);
			copybitmap(dest,src,0,0,scroll - srcwidth,scrolly,myclip,transparency,transparent_color);

			myclip.min_y = row * rowheight + scrolly - srcheight;
			if (myclip.min_y < clip.min_y) myclip.min_y = clip.min_y;
			myclip.max_y = (row + cons) * rowheight - 1 + scrolly - srcheight;
			if (myclip.max_y > clip.max_y) myclip.max_y = clip.max_y;

			copybitmap(dest,src,0,0,scroll,scrolly - srcheight,myclip,transparency,transparent_color);
			copybitmap(dest,src,0,0,scroll - srcwidth,scrolly - srcheight,myclip,transparency,transparent_color);

			row += cons;
		}
        }
    }

    public static void clearbitmap(osd_bitmap bitmap) {
        int i;
        for (i = 0; i < bitmap.height; i++) {
            memset(bitmap.line[i], Machine.pens[0], bitmap.width);
        }
    }
}
