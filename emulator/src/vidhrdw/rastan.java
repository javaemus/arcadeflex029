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
 * using automatic conversion tool v0.03
 * converted at : 06-09-2011 13:38:45
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
import static arcadeflex.osdepend.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;

public class rastan
{
	
	
	
	//#define VIDEO_RAM_SIZE 0x400
	//static const unsigned char *colors;
	
	static osd_bitmap tmpbitmap2;
	static osd_bitmap tmpbitmap3;
	
	static char[] dirty1=new char[0x1000];	/* 16KB .L organization */
	//static unsigned char dirty2[0x2000];	/* 16KB .W organization */
	static char[] dirty3=new char[0x1000];	/* 16KB .L organization */
	//static unsigned char dirty4[0x2000];	/* 16KB .W organization */
	
	static char[] r_palram=new char[0x10000];   /*64KB for palette RAM*/
	static char[] r_sprram=new char[0x10000];   /*64KB for sprite RAM*/
	static char[] pal_dirty=new char[0x80];	  /*only 0x50 color schemes are in use*/
	
	
	//static unsigned char paltab[0x10000];	/* rip the palette */
	
	
	static char[] r_vidram1=new char[0x4000];   /*16KB for video RAM 8x8   0x1000.L */
	static char[] r_vidram2=new char[0x4000];   /*16KB for video RAM 16x16 0x2000.W */
	static char[] r_vidram3=new char[0x4000];   /*16KB for video RAM 8x8   0x1000.L */
	static char[] r_vidram4=new char[0x4000];   /*16KB for video RAM 16x16 0x2000.W */
	
	static char[] r_ram=new char[0x4000];   /*16KB for RAM*/
	
	static char scrollY[]={0,0,0,0};
	static char scrollX[]={0,0,0,0};
	
	static rectangle visiblearea = new rectangle
	(
	  0, 64*8-1,
	  0, 64*8-1
	);
	
	/***************************************************************************
	
	  Convert the color PROMs into a more useable format.
	
	  RASTAN doesn't have a color PROM. It uses 64KB bytes of RAM to
	  dynamically create the palette. Each couple of bytes defines one
	  color (5 bits per R,G,B; the highest 1 bit of the first byte is unused).
	  Graphics use 4 bitplanes. It seems only first 0x50 colors are in use.
	  Game uses 512+ colors at run time so there's no chance for optimized
	  palette.
	
	***************************************************************************/
	public static VhConvertColorPromPtr rastan_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, char []color_prom) 
	{
		int i;
	
		for (i = 0;i < 256;i++)
		{
			int bits;
	
			bits = (i >> 0) & 0x07;
			palette[3*i] = (char)((bits >> 1) | (bits << 2) | (bits << 5));
			bits = (i >> 3) & 0x07;
			palette[3*i + 1] = (char)((bits >> 1) | (bits << 2) | (bits << 5));
			bits = (i >> 6) & 0x03;
			palette[3*i + 2] = (char)(bits | (bits >> 2) | (bits << 4) | (bits << 6));
		}
	
		for (i = 0;i < 256;i++)
			colortable[0x50*16+i] = (char)i;
	
	} };
	
	
	public static VhStartPtr rastan_vh_start = new VhStartPtr() { public int handler() 
	{
		if (generic_vh_start.handler() != 0)
			return 1;
	
		/* the background area is twice as tall and twice as large as the screen */
		if ((tmpbitmap2 = osd_create_bitmap(512,512)) == null)
	        {
			generic_vh_stop.handler();
			return 1;
		}
		if ((tmpbitmap3 = osd_create_bitmap(512,512)) == null)
	        {
			generic_vh_stop.handler();
			osd_free_bitmap(tmpbitmap2);
			return 1;
		}
		return 0;
	} };
	
	
	public static ReadHandlerPtr getb = new ReadHandlerPtr() { public int handler(int i)
	{
		return 2*((r_palram[i]>>2)&0x1f);
	} };
	public static ReadHandlerPtr getg = new ReadHandlerPtr() { public int handler(int i)
	{
		return 2*( ((r_palram[i]&0x03)<<3)|((r_palram[i+1]>>5)&0x07) );
	} };
	public static ReadHandlerPtr getr = new ReadHandlerPtr() { public int handler(int i)
	{
		return 2*(r_palram[i+1]&0x1f);
	} };
	
	public static VhStopPtr rastan_vh_stop = new VhStopPtr() { public void handler() {
	
	FILE  tab;
	int i,pom;
	
	
	tab=fopen("vidram1","wa");
	for (i=0; i<0x4000; i+=4)
	{
	  for (pom=0; pom<4; pom++)
	    fprintf(tab,"%02x ",r_vidram1[i+pom]);
	  fprintf(tab,"\n");
	}
	fclose(tab);
	
	tab=fopen("vidram3","wa");
	for (i=0; i<0x4000; i+=4)
	{
	  for (pom=0; pom<4; pom++)
	    fprintf(tab,"%02x ",r_vidram3[i+pom]);
	  fprintf(tab,"\n");
	}
	fclose(tab);
	
	
	
	
	
	tab=fopen("vidram2","wa");
	for (i=0; i<0x4000; i+=4)
	{
	  for (pom=0; pom<4; pom++)
	    fprintf(tab,"%02x ",r_vidram2[i+pom]);
	  fprintf(tab,"\n");
	}
	fclose(tab);
	tab=fopen("vidram4","wa");
	for (i=0; i<0x4000; i+=4)
	{
	  for (pom=0; pom<4; pom++)
	    fprintf(tab,"%02x ",r_vidram4[i+pom]);
	  fprintf(tab,"\n");
	}
	fclose(tab);
	
	
	
	/*tab=fopen("paltab","wa");
	pom=0;
	for (i=0; i<0x10000; i++)
	{
	  if (paltab[i]!=0)
	  {
	    fprintf(tab,"%04x ",i);
	    fprintf(tab,"\n");
	    pom++;
	  }
	}
	  fprintf(tab,"\ntotal colors %04x\n",pom);
	fclose(tab);*/
	
	
	tab=fopen("sprram","wa");
	for (i=0; i<0x800; i+=8)
	{
	  fprintf(tab,"%04x  ",i/8);
	  for (pom=0; pom<8; pom+=2)
	    fprintf(tab,"%02x%02x ",r_sprram[i+pom],r_sprram[i+pom+1]);
	  fprintf(tab,"\n");
	}
	fclose(tab);
	
	
	
	tab=fopen("p0-200","wa");
	fprintf(tab,"NeoPaint Palette File\n(C)1992-95 NeoSoft Corp.\n256\n");
	for (i=0x000; i<0x200; i+=2)
	{
	//  fprintf(tab,"%04x %04x ",i, i/32);
	//  fprintf(tab,"%02x%02x ",r_palram[i],r_palram[i+1]);
	  fprintf(tab,"%3i %3i %3i",getr.handler(i),getg.handler(i),getb.handler(i) );
	  fprintf(tab,"\n");
	}
	fclose(tab);
	tab=fopen("p200-400","wa");
	fprintf(tab,"NeoPaint Palette File\n(C)1992-95 NeoSoft Corp.\n256\n");
	for (i=0x200; i<0x400; i+=2)
	{
	//  fprintf(tab,"%04x %04x ",i, i/32);
	//  fprintf(tab,"%02x%02x ",r_palram[i],r_palram[i+1]);
	  fprintf(tab,"%3i %3i %3i",getr.handler(i),getg.handler(i),getb.handler(i) );
	  fprintf(tab,"\n");
	}
	fclose(tab);
	
	tab=fopen("p400-600","wa");
	fprintf(tab,"NeoPaint Palette File\n(C)1992-95 NeoSoft Corp.\n256\n");
	for (i=0x400; i<0x600; i+=2)
	{
	//  fprintf(tab,"%04x %04x ",i, i/32);
	//  fprintf(tab,"%02x%02x ",r_palram[i],r_palram[i+1]);
	  fprintf(tab,"%3i %3i %3i",getr.handler(i),getg.handler(i),getb.handler(i) );
	  fprintf(tab,"\n");
	}
	fclose(tab);
	tab=fopen("p600-800","wa");
	fprintf(tab,"NeoPaint Palette File\n(C)1992-95 NeoSoft Corp.\n256\n");
	for (i=0x600; i<0x800; i+=2)
	{
	//  fprintf(tab,"%04x %04x ",i, i/32);
	//  fprintf(tab,"%02x%02x ",r_palram[i],r_palram[i+1]);
	  fprintf(tab,"%3i %3i %3i",getr.handler(i),getg.handler(i),getb.handler(i) );
	  fprintf(tab,"\n");
	}
	fclose(tab);
	
	tab=fopen("p800-a00","wa");
	fprintf(tab,"NeoPaint Palette File\n(C)1992-95 NeoSoft Corp.\n256\n");
	for (i=0x800; i<0xa00; i+=2)
	{
	//  fprintf(tab,"%04x %04x ",i, i/32);
	//  fprintf(tab,"%02x%02x ",r_palram[i],r_palram[i+1]);
	  fprintf(tab,"%3i %3i %3i",getr.handler(i),getg.handler(i),getb.handler(i) );
	  fprintf(tab,"\n");
	}
	fclose(tab);
	
	tab=fopen("pa-2000","wa");
	fprintf(tab,"NeoPaint Palette File\n(C)1992-95 NeoSoft Corp.\n256\n");
	for (i=0xa00; i<0x2000; i+=2)
	{
	  fprintf(tab,"%3i %3i %3i",getr.handler(i),getg.handler(i),getb.handler(i) );
	  fprintf(tab,"\n");
	}
	fclose(tab);
	
	
		osd_free_bitmap(tmpbitmap2);
		osd_free_bitmap(tmpbitmap3);
                tmpbitmap2=null;
                tmpbitmap3=null;
		generic_vh_stop.handler();
	} };
	
	
	
	
	public static WriteHandlerPtr rastan_scrollY_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	   scrollY[offset]=(char)data;
	} };
	
	public static WriteHandlerPtr rastan_scrollX_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	   scrollX[offset]=(char)data;
	} };
	
	public static WriteHandlerPtr rastan_paletteram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	   r_palram[offset]=(char)data;
	   if ((offset/32) < 0x80)
		   pal_dirty[offset/32] = 1;
	} };
	
	public static ReadHandlerPtr rastan_paletteram_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	   return r_palram[offset];
	} };
	
	
	public static WriteHandlerPtr rastan_spriteram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	   r_sprram[offset]=(char)data;
	} };
	
	public static ReadHandlerPtr rastan_spriteram_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	   return r_sprram[offset];
	} };
	
	public static WriteHandlerPtr rastan_videoram1_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (r_vidram1[offset] != data)
		{
			dirty1[offset >> 2] = 1;
			r_vidram1[offset]=(char)data;
		}
	} };
	
	public static ReadHandlerPtr rastan_videoram1_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	   return r_vidram1[offset];
	} };
	
	
	public static WriteHandlerPtr rastan_videoram2_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	   r_vidram2[offset]=(char)data;
	   if (errorlog != null)
	     if (data>0)
		fprintf(errorlog,"Writing data>0 %02x to vidram2 !!!\n",data);
	} };
	
	public static ReadHandlerPtr rastan_videoram2_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	   return r_vidram2[offset];
	} };
	
	public static WriteHandlerPtr rastan_videoram3_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (r_vidram3[offset] != data)
		{
			dirty3[offset >> 2] = 1;
			r_vidram3[offset]=(char)data;
		}
	} };
	
	public static ReadHandlerPtr rastan_videoram3_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	   return r_vidram3[offset];
	} };
	
	public static WriteHandlerPtr rastan_videoram4_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	   r_vidram4[offset]=(char)data;
	   if (errorlog != null)
	     if (data>0)
		fprintf(errorlog,"Writing data>0 %02x to vidram4 !!!\n",data);
	} };
	
	public static ReadHandlerPtr rastan_videoram4_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	   return r_vidram4[offset];
	} };
	
	public static WriteHandlerPtr rastan_ram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
	   r_ram[offset]=(char)data;
	} };
	
	public static ReadHandlerPtr rastan_ram_r = new ReadHandlerPtr() { public int handler(int offset)
	{
	   return r_ram[offset];
	} };
	
	
	
	public static InterruptPtr rastan_interrupt= new InterruptPtr() { public int handler()
	{
	//static int inter=0;
	//	inter = (inter+1)&0x01;
	//	if (inter != 0)
			return 5;  /*Interrupt vector 5*/
	//	else
	//		return 4;
	}};

 //TODO check if that works (really i am not sure...)
	static int mix_it(CharPtr address, int offset, int len)
	{
	/*
	 * this procedure mixes bytes in RAM - this is needed by Rastan
	 * where there are odd bytes stored in one ROM file, and even bytes in another
	 */
	/*
	 * address - address of first byte (odd)
	 * offset  - offset to even byte from address (usually length of rom file)
	 * len     - length of data to process (usually length of rom file)
	*/
	
	char[] buf;
	CharPtr storeaddress=new CharPtr();
	CharPtr pom=new CharPtr();
	int i;
	
		if( (buf = new char[2*len]) == null )
			return 1;
		storeaddress = address;
	        pom.set(buf, 0);
		for( i = 0; i < len; i++ )  /* mix within buffer */
	        {
			pom.write(address.read());
			pom.inc();
                        pom.write(address.read()+offset);     
			//*pom = *(address+offset);
			pom.inc();
			address.inc();
		}
	        pom.set(buf,0);
		for( i = 0; i < 2*len; i++ )  /* copy back to RAM */
	        {
			//*(storeaddress++) = *(pom++);
                        storeaddress.writeinc(pom.readinc());
		}
		buf=null;
		return 0;
	}
	
	public static InitMachinePtr rastan_machine_init= new InitMachinePtr() {	public void handler()
	{
		if ( mix_it(new CharPtr(Machine.memory_region[0],0x00000), 0x10000, 0x10000)!=0 )
			{
				printf ("Mixing unsuccessful\n");
				return;
			}
		if ( mix_it(new CharPtr(Machine.memory_region[0],0x20000), 0x10000, 0x10000)!=0 )
			{
				printf ("Mixing unsuccessful\n");
				return;
			}
		if ( mix_it(new CharPtr(Machine.memory_region[0],0x40000), 0x10000, 0x10000)!=0 )
			{
				printf ("Mixing unsuccessful\n");
				return;
			}
	//	Machine.memory_region[0][0x3a204]=0x60;  /* Rastan infinite lives */
	//	Machine.memory_region[0][0x517ec]=0x60;  /* Rastan infinite energy */
	//	Machine.memory_region[0][0x527b2]=0x60;  /* Rastan infinite energy */
	
	}};
	
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VhUpdatePtr rastan_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
		int offs,pom;
		int scrollx,scrolly;
		int i,col;
	
		for (pom=0; pom<0x50; pom++){
			if (pal_dirty[pom]!=0)
			{
				Machine.gfx[0].colortable.write(pom*16,0);
	//				Machine.gfx[4].colortable[0];
				for (i = 1;i <16 ;i++) {
					col = (r_palram[pom*32+i*2+1] >> 2) & 0x07;   /* red component */
					col |= (r_palram[pom*32+i*2+1] >> 4) & 0x08;   /* green component LSB */
					col |= (r_palram[pom*32+i*2] << 4) & 0x30;   /* green component MSB */
					col |= (r_palram[pom*32+i*2] << 1) & 0xc0;   /* blue component */
					if (col==0)
						col=1;
					Machine.gfx[0].colortable.write(pom*16+i,Machine.gfx[4].colortable.read(col));
				}
			}
		}
	
		for (pom = 0x0;pom < 0x4000;pom+=4)
		{
			offs=pom>>2;
			if ( dirty1[offs]!=0 || pal_dirty[ r_vidram1[pom+1] & 0x7f ]!=0 )
	
			{
				int sx,sy;
				int num,bank;
	
				num = ((r_vidram1[pom+2]<<8)+r_vidram1[pom+3]) & 0x1fff;
				bank= (r_vidram1[pom+2] & 0x20)>>5;
	
				dirty1[offs] = 0;
	
				sx = 8 * (offs % 64);
				sy = 8 * (offs / 64);
	
				drawgfx(tmpbitmap2, Machine.gfx[bank]   ,
						num,
						r_vidram1[pom+1]&0x7f,
						r_vidram1[pom]&0x40,r_vidram1[pom]&0x80,
						sx,sy,
						visiblearea,TRANSPARENCY_NONE,0);
	
			}
		}
	
		for (pom = 0x0;pom < 0x4000;pom+=4)
		{
			offs=pom>>2;
			if ( dirty3[offs]!=0 || pal_dirty[ r_vidram3[pom+1] & 0x7f ]!=0 )
			{
				int sx,sy;
				int num,bank;
	
				num = ((r_vidram3[pom+2]<<8)+r_vidram3[pom+3]) & 0x1fff;
				bank= (r_vidram3[pom+2] & 0x20)>>5;
	
				dirty3[offs] = 0;
	
				sx = 8 * (offs % 64);
				sy = 8 * (offs / 64);
	
				drawgfx(tmpbitmap3, Machine.gfx[bank]   ,
						num,
						r_vidram3[pom+1]&0x7f,
						r_vidram3[pom]&0x40,r_vidram3[pom]&0x80,
						sx,sy,
						visiblearea,TRANSPARENCY_NONE,0);
	
			}
		}
	
		for (pom=0; pom<0x80; pom++)
			pal_dirty[pom]=0;
	
		/* copy the character mapped graphics */
	
		scrollx = (scrollX[0]<<8) + scrollX[1] - 16;
		scrolly = (scrollY[0]<<8) + scrollY[1] - 8;
		copyscrollbitmap(bitmap,tmpbitmap2, 1,new int[] {scrollx},1,new int[] {scrolly},Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	
		scrollx = (scrollX[2]<<8) + scrollX[3] - 16;
		scrolly = (scrollY[2]<<8) + scrollY[3] - 8;
		copyscrollbitmap(bitmap,tmpbitmap3,1,new int[] {scrollx},1,new int[] {scrolly},Machine.drv.visible_area,TRANSPARENCY_PEN,0);
	
	
		/* Draw the sprites. 256 sprites in total */
		for (offs = 0x800-8;offs >=0;offs -= 8)
		{
			int sx1,sy1,col1;
			int num1,bank1,pom1;
	
			sx1 = (r_sprram[offs+6]<<8) | r_sprram[offs+7];
			sy1 = (r_sprram[offs+2]<<8) | r_sprram[offs+3];
	
			sx1 = sx1<32768 ? sx1&0x1ff : -( (65536-sx1) & 0x1ff );
			sy1 = sy1<32768 ? sy1&0x1ff : -( (65536-sy1) & 0x1ff );
	
			num1=(r_sprram[offs+4]<<8) | r_sprram[offs+5];
			if  ( num1!=0 && (sx1<320) && (sy1<240) )
			{
				num1 &= 0x3ff;
				bank1 =(r_sprram[offs+4]>>3)&0x01;
				pom1 = (r_sprram[offs+4]>>2)&0x01;
				if (pom1 != 0)
				{
					num1=0x40;  /* this is because this ROM set is partial*/
					bank1=0;
				}
				col1 = r_sprram[offs+1];
				if (col1<0x40)
					col1= col1|0x30;
				if (offs/8>=0xbe) /*found experimentally*/
					col1= ((col1&0x3f)|0x30);
				drawgfx(bitmap,Machine.gfx[2+bank1],
					num1,
					col1 ,
					r_sprram[offs]&0x40,0,
					sx1,sy1-8,
					Machine.drv.visible_area,TRANSPARENCY_PEN,0);
			}
		}
	
	} };
	
}
