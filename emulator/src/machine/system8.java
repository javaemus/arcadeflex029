//HACKED system 8 driver from mame 0.31

/*
 * ported to v0.29 manually to 0.31
 * using automatic conversion tool v0.04
 * converted at : 20-09-2011 00:26:26
 *
 *
 *
 */ 
package machine;

import static arcadeflex.libc.*;
import static arcadeflex.osdepend.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static machine.system8H.*;
import static vidhrdw.generic.*;
import static sndhrdw.generic.*;
import static mame.common.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static Z80.Z80H.*;

public class system8
{
	
	public static CharPtr system8_bg_pagesel=new CharPtr();
	public static CharPtr system8_scroll_y=new CharPtr();
	public static CharPtr system8_scroll_x=new CharPtr();
	public static CharPtr system8_paletteram=new CharPtr();
	public static CharPtr system8_spritepaletteram=new CharPtr();
	public static CharPtr system8_backgroundpaletteram=new CharPtr();
	public static CharPtr system8_videoram=new CharPtr();
	public static CharPtr system8_spriteram=new CharPtr();
	public static CharPtr system8_backgroundram=new CharPtr();
	public static CharPtr system8_sprites_collisionram=new CharPtr();
	public static CharPtr system8_background_collisionram=new CharPtr();
	public static CharPtr system8_scrollx_ram=new CharPtr();
	public static int[] 	system8_videoram_size=new int[1];
	public static int[] 	system8_paletteram_size=new int[1];
	public static int[] 	system8_spritepaletteram_size=new int[1];
	public static int[] 	system8_backgroundram_size=new int[1];
	public static int[] 	system8_backgroundpaletteram_size=new int[1];
	
	public static char[] bg_ram;
	public static char[] bg_dirtybuffer;
	public static char[] bg_palette_dirtybuffer;
	public static char[] tx_dirtybuffer;
	public static char[] tx_palette_dirtybuffer;
	public static char[] SpritesData;// = NULL;
	public static char[] SpritesCollisionTable;
	static int 	scrollx=0,scrolly=0,system8_supports_banks=0,bg_bank=0,bg_bank_latch=0,sprite_offset_y=16;
	
	static int[] scrollx_row=new int[32];
	static  osd_bitmap bitmap1;
	static  osd_bitmap bitmap2;
	static  rectangle system8_clip=new rectangle();
	
	static int  system8_bank = 0x0f;
//	static void (*Check_SpriteRAM_for_Clear)(void) = NULL;//TODO
	public interface callback {
            void proc();
        }
        public static callback Check_SpriteRAM_for_Clear;

	
	
	public static VhStartPtr system8_vh_start= new VhStartPtr() { public int handler() 
	{
		int i;
	
		if ((SpritesCollisionTable = new char[0x10000]) == null)
			return 1;
	
		if ((bg_dirtybuffer = new char[1024]) == null)
		{
			SpritesCollisionTable=null;
			return 1;
		}
		if ((bg_palette_dirtybuffer = new char[64]) == null)
		{
			bg_dirtybuffer=null;
			SpritesCollisionTable=null;
			return 1;
		}
		if ((tx_dirtybuffer = new char[1024]) == null)
		{
			bg_dirtybuffer=null;
			bg_palette_dirtybuffer=null;
			SpritesCollisionTable=null;
			return 1;
		}
		if ((tx_palette_dirtybuffer = new char[64]) == null)
		{
			tx_dirtybuffer=null;
			bg_dirtybuffer=null;
			bg_palette_dirtybuffer=null;
			SpritesCollisionTable=null;
			return 1;
		}
		if ((bg_ram =new char[0x4000]) == null)			/* Allocate 16k for background banked ram */
		{
			bg_palette_dirtybuffer=null;
			bg_dirtybuffer=null;
			tx_palette_dirtybuffer=null;
			tx_dirtybuffer=null;
			SpritesCollisionTable=null;
			return 1;
		}
		if ((SpritesData = new char[0x40000]) == null)	/* Allocate a maximum for 512k of 16 colors sprites */
		{
	                bg_ram=null;
			bg_palette_dirtybuffer=null;
			bg_dirtybuffer=null;
		        tx_palette_dirtybuffer=null;
			tx_dirtybuffer=null;
			SpritesCollisionTable=null;
			return 1;
		}
		if ((bitmap1 = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
		{
			bg_ram=null;
			bg_palette_dirtybuffer=null;
			bg_dirtybuffer=null;
			tx_palette_dirtybuffer=null;
			tx_dirtybuffer=null;
			SpritesData=null;
			SpritesCollisionTable=null;
			return 1;
		}
		if ((bitmap2 = osd_create_bitmap(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
		{
			osd_free_bitmap(bitmap1);
                        bitmap1=null;
			bg_ram=null;
			bg_palette_dirtybuffer=null;
			bg_dirtybuffer=null;
			tx_palette_dirtybuffer=null;
			tx_dirtybuffer=null;
			SpritesData=null;
			SpritesCollisionTable=null;
			return 1;
		}
	
		memset(bg_dirtybuffer,0,1024);
		memset(bg_palette_dirtybuffer,0,64);
		memset(tx_dirtybuffer,0,1024);
		memset(tx_palette_dirtybuffer,0,64);
	//TODO is that neccesary??	memset(system8_backgroundram,0,system8_backgroundram_size[0]);
	//TODO is that neccesary??	memset(system8_videoram,0,system8_videoram_size[0]);
		memset(SpritesCollisionTable,255,0x10000);
	
		for (i=0; i < 512; i++)
		{
			Machine.gfx[0].colortable.write(i,Machine.pens[0]);
			Machine.gfx[1].colortable.write(i,Machine.pens[0]);
		}
		return 0;
	}};
	
	public static VhStopPtr system8_vh_stop= new VhStopPtr() { public void handler() 
	{
		osd_free_bitmap(bitmap2);
		osd_free_bitmap(bitmap1);
                bitmap1=null;
                bitmap2=null;
		bitmap1=null;
		bg_ram=null;
		bg_palette_dirtybuffer=null;
		bg_dirtybuffer=null;
		tx_palette_dirtybuffer=null;
		tx_dirtybuffer=null;
		SpritesData=null;
		SpritesCollisionTable=null;
	}};
	
	
	public static void system8_define_checkspriteram(callback call)
	{
		Check_SpriteRAM_for_Clear = call;
	}
	
	public static void system8_define_spritememsize(int region, int size)
	{
		char []SpritesPackedData = Machine.memory_region[region];
	
		if (SpritesData==null)
			SpritesData = new char[size*2];
	
		if (SpritesData != null)
		{
			int a;
			for (a=0; a < size; a++)
			{
				SpritesData[a*2  ] = (char)(SpritesPackedData[a] >> 4);
				SpritesData[a*2+1] = (char)(SpritesPackedData[a] & 0xF);
			}
		}
	} 
	
	public static void system8_define_banksupport(int support)
	{
		system8_supports_banks = support;
	}
	
	public static void system8_define_sprite_offset_y(int offset)
	{
		sprite_offset_y = offset;
	}
	
	public static void system8_define_cliparea(int x1, int x2, int y1, int y2)
	{
		system8_clip.min_x = x1;
		system8_clip.max_x = x2;
		system8_clip.min_y = y1;
		system8_clip.max_y = y2;
	}
	
	public static int GetSpriteBottomY(int spr_number)
	{
		return  system8_spriteram.read((spr_number<<4) + SPR_Y_BOTTOM);
	} 
	
	
	static void Pixel(osd_bitmap bitmap,int x,int y,int spr_number,int color)
	{
		int xr,yr,spr_y1,spr_y2;
		int SprOnScreen;
	
		if (SpritesCollisionTable[256*y+x] == 255)
		{
			SpritesCollisionTable[256*y+x] = (char)spr_number;
			bitmap.line[y][x] = Machine.pens[color];
		}
		else
		{
			SprOnScreen=SpritesCollisionTable[256*y+x];
			system8_sprites_collisionram.write(SprOnScreen + (spr_number<<5),0xff);
			spr_y1 = GetSpriteBottomY(spr_number);
			spr_y2 = GetSpriteBottomY(SprOnScreen);
			if (spr_y1 >= spr_y2)
			{
				bitmap.line[y][x]=Machine.pens[color];
				SpritesCollisionTable[256*y+x]=(char)spr_number;
			}
		}
		xr = (x>>3);
		yr = (y>>3);
		if ((system8_backgroundram.read(((yr%32)<<6) + ((xr%32)<<1) + 1) & 0x10)!=0)
		  system8_background_collisionram.write(spr_number,0xff);
	}
	
	
	static void MarkBackgroundDirtyBufferBySprite(int x,int y,int width,int height)
	{
		int xr,yr,wr,hr,row,col;
	
		x  = (char)((x - scrollx) % 256);
		y  = (char)((scrolly + y + sprite_offset_y) % 256);
		xr = (x>>3);
		yr = (y>>3);
		wr = (width>>3)  + (((x & 7)!=0) ? 1:0) +  (((width & 7)!=0) ? 1:0);
		hr = (height>>3) + (((y & 7)!=0) ? 1:0) + (((height & 7)!=0) ? 1:0);
	
		for (row=0;row<hr;row++)
		{
			int sumrow = (((row+yr)%32)<<5);
			for (col=0;col<wr;col++)
			{
	//			tx_dirtybuffer[sumrow+((col+xr)%32)] = 1;
				bg_dirtybuffer[sumrow+((col+xr)%32)] = 1;
			}
		}
	}
	
	
	static void RenderSprite(osd_bitmap bitmap, rectangle clip, int spr_number)
	{
		int SprX,SprY,Col,Row,Height,DataOffset,FlipX;
		int Color,scrx,scry,Bank;
		//unsigned char *SprPalette,*SprReg;
		//unsigned short NextLine,Width,Offset16;
                CharPtr SprPalette = new CharPtr();
                CharPtr SprReg = new CharPtr();
                int NextLine,Width,Offset16;
                
	
		SprReg.set(system8_spriteram,(spr_number<<4));
		Bank		= ((((SprReg.read(SPR_BANK) & 0x80)>>7)+((SprReg.read(SPR_BANK) & 0x40)>>5))<<16) * system8_supports_banks;
		Width 		= SprReg.read(SPR_WIDTH_LO) + (SprReg.read(SPR_WIDTH_HI)<<8);
		Height		= SprReg.read(SPR_Y_BOTTOM) - SprReg.read(SPR_Y_TOP);
		FlipX 		= SprReg.read(SPR_FLIP_X) & 0x80;
		DataOffset 	= SprReg.read(SPR_GFXOFS_LO)+((SprReg.read(SPR_GFXOFS_HI) & 0x7F)<<8)+Width;
		SprPalette.set(system8_spritepaletteram,(spr_number<<4));
		SprX 		= (SprReg.read(SPR_X_LO) >> 1) + ((SprReg.read(SPR_X_HI) & 1) << 7);
		SprY 		= SprReg.read(SPR_Y_TOP);
		NextLine	= Width;
	
		if ((DataOffset & 0x8000) != 0) FlipX^=0x80;
		if ((Width & 0x8000) != 0) Width = (~Width)+1;		// width isn't positive
								// and this means that sprite will has fliped Y
		Width<<=1;
	
		MarkBackgroundDirtyBufferBySprite(SprX,SprY,Width,Height);
	
		for(Row=0; Row<Height; Row++)
		{
			//Offset16 = (DataOffset+(Row*NextLine)) << 1;
                        Offset16 = (DataOffset+(Row*NextLine)) << 1;
			scry = ((char)(((SprY+Row) + scrolly + sprite_offset_y) % 256)&0xFF);//shadow add 0xFF since it is unsigned char in original
			if ((clip.min_y <= SprY+Row) && (clip.max_y >= SprY+Row))
			{
				for(Col=0; Col<Width; Col++)
				{
					Color = (FlipX!=0) ? SpritesData[Bank+Offset16-Col+1]
							: SpritesData[Bank+Offset16+Col];
					if (Color == 15) break;
					if (Color!=0 && (clip.min_x <= SprX+Col) && (clip.max_x >= SprX+Col))
					{
						scrx = ((char)(((SprX+Col) - scrollx) % 256)&0xFF);//shadow add 0xFF since it is unsigned char in original
						Pixel(bitmap,scrx,scry,spr_number,SprPalette.read(Color));
					}
				}
			}
		}
	}
	
	
	static void ClearSpritesCollisionTable()
	{
		int col,row,i,sx,sy;
	
		for (i = 0; i<1024; i++)
		{
			if (bg_dirtybuffer[i]!=0)
			{
				sx = (i % 32)<<3;
				sy = (i >> 5)<<3;
				for(row=sy; row<sy+8; row++)
					for(col=sx; col<sx+8; col++)
						SpritesCollisionTable[256*row+col] = 255;
			}
		}
	}
	
	public static callback pitfall2_clear_spriteram = new callback() { public void proc(){
            if (((NOT(system8_backgroundram.read()))!=0) && system8_videoram.read(212) != 'M' &&
				system8_spriteram.read(SPR_GFXOFS_LO) != 0xDD &&
					system8_spriteram.read(SPR_GFXOFS_HI) != 0x2C)
            {
                //memset(system8_spriteram,0,0x200);		/* Workaround for Pitfall!  TODO BETTER! */
                for(int k=0; k<0x200; k++)
                {
                    system8_spriteram.write(k, 0);
                }
            }
            System.out.println("here");
			
        }};

	
	static void DrawSprites(osd_bitmap bitmap,rectangle clip)
	{
		int spr_number,SprBottom,SprTop;
		//unsigned char *SprReg;
                CharPtr SprReg=new CharPtr();
	
		if (clip==null) clip = Machine.drv.visible_area;
	
		if (Check_SpriteRAM_for_Clear != null)
			Check_SpriteRAM_for_Clear.proc();
		
                for(int k=0; k<0x400; k++)
                {
                    system8_sprites_collisionram.write(k, 0);
                }
	
		for(spr_number=0; spr_number<32; spr_number++)
		{
			//SprReg 		= system8_spriteram + (spr_number<<4);
			//SprTop		= SprReg[SPR_Y_TOP];
			//SprBottom	= SprReg[SPR_Y_BOTTOM];
                        SprReg.set(system8_spriteram, (spr_number<<4));
			SprTop		= SprReg.read(SPR_Y_TOP);
			SprBottom	= SprReg.read(SPR_Y_BOTTOM);
			if (SprBottom!=0 && (SprBottom-SprTop > 0))
                        {
				RenderSprite(bitmap,clip,spr_number);
                        }
		}
	
		ClearSpritesCollisionTable();
	}
	
	
	
	public static WriteHandlerPtr system8_soundport_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		soundlatch_w.handler(0,data);
		cpu_cause_interrupt(1, Z80_NMI_INT);
	} };
	
	public static ReadHandlerPtr system8_bg_bankselect_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return bg_bank_latch;
	} };
	
	public static WriteHandlerPtr system8_bg_bankselect_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (data != bg_bank_latch)
		{
			bg_bank_latch = data;
			bg_bank = (data >> 1) & 0x03;	/* Select 4 banks of 4k, bit 2,1 */
		}
	} };
	
	public static WriteHandlerPtr system8_videoram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		system8_videoram.write(offset,data);
		bg_ram[0x1000*bg_bank + offset] = (char)data;
		tx_dirtybuffer[offset>>1] = 1;
	} };
	
	public static WriteHandlerPtr system8_paletteram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		system8_paletteram.write(offset,data);
		tx_palette_dirtybuffer[offset>>3] = 1;
		Machine.gfx[1].colortable.write(offset,Machine.pens[data]);
	} };
	
	public static WriteHandlerPtr system8_backgroundram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		system8_backgroundram.write(offset,data);
		bg_ram[0x1000*bg_bank + offset + 0x800] = (char)data;
		bg_dirtybuffer[offset>>1] = 1;
	} };
	
	public static WriteHandlerPtr system8_backgroundpaletteram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		system8_backgroundpaletteram.write(offset,data);
		bg_palette_dirtybuffer[offset>>3] = 1;
		Machine.gfx[0].colortable.write(offset,Machine.pens[data]);
	} };
	
	public static VhUpdatePtr system8_vh_screenrefresh= new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
		int scroll_x,scroll_y, sx,sy, i;
		int pitfall2_trans_pen;
	
		scrollx = (system8_scroll_x.read(0) >> 1) + ((system8_scroll_x.read(1) & 1) << 7) + 14;
		scrolly = system8_scroll_y.read() - 16;
	
		/* for every character in the background video RAM, check if it has
		 * been modified since last time and update it accordingly.
		 */
	
		for (i = 0; i<system8_backgroundram_size[0]; i+=2)
		{
			int code = (system8_backgroundram.read(i) + (system8_backgroundram.read(i+1) << 8)) & 0x7FF;
			int palette = code >> 5;
	
			if (bg_dirtybuffer[i>>1]!=0 || bg_palette_dirtybuffer[palette]!=0)
			{
				bg_dirtybuffer[i>>1] = 0;
				sx = (i % 64) << 2;
				sy = (i >> 6) << 3;
				drawgfx(bitmap1,Machine.gfx[0],
						code,palette,
						0,0,
						sx,sy,
						null,TRANSPARENCY_NONE,0);
			}
		}
		memset(bg_palette_dirtybuffer,0,64);
	
		DrawSprites(bitmap1, system8_clip);
	
	
		/* for every character in the background video RAM,
		 * check if it has clip attribute and update it accordingly.
		 */
	
		for (i = 0; i<system8_backgroundram_size[0]; i+=2)
		{
			if ((system8_backgroundram.read(i+1) & 8)!=0)
			{
				int code = (system8_backgroundram.read(i) + (system8_backgroundram.read(i+1) << 8)) & 0x7FF;
				int palette = code >> 5;
				sx = (i % 64) << 2;
				sy = (i >> 6) << 3;
				pitfall2_trans_pen=0;
	
				if (strcmp(Machine.gamedrv.name,"pitfall")==0)
				{
				   if (code==1658) pitfall2_trans_pen=5;
				   if (code==1660) pitfall2_trans_pen=6;
				   if (code==227  || code==976 || (code>977 && code<983)) pitfall2_trans_pen=3;
				   if (code==1645 || code==1046 || code==1039 || code==1805
							  || code==1820 || code==1641) pitfall2_trans_pen=1;
				}
				drawgfx(bitmap1,Machine.gfx[0],
						code,palette,
						0,0,
						sx,sy,
						null,TRANSPARENCY_PEN,pitfall2_trans_pen);
			}
		}
	
	
		/* copy the temporary bitmap to the screen */
	
		scroll_x = scrollx;
		scroll_y = -scrolly;
		copyscrollbitmap(bitmap,bitmap1,1,new int[]{scroll_x},1,new int[] {scroll_y},
				Machine.drv.visible_area,TRANSPARENCY_NONE,0);
	
	
		/* for every character in the text video RAM,
		 * check if it different than 0 and update it accordingly.
		 */
	
		for (i = 0; i<system8_videoram_size[0]; i+=2)
		{
			int code = (system8_videoram.read(i) + (system8_videoram.read(i+1) << 8)) & 0x7FF;
	
			if (code != 0)
			{
				int palette = code>>5;
				sx = (i % 64)<<2;
				sy = ((i >> 6)<<3)+16;
				drawgfx(bitmap,Machine.gfx[1],
						code,palette,
						0,0,
						sx,sy,
						null,TRANSPARENCY_PEN,0);
			}
		}
	
	}};
	
	
	public static VhConvertColorPromPtr system8_vh_convert_color_prom= new VhConvertColorPromPtr() { public void handler(char []palette_table, char []colortable_table, char []color_prom_table)
	{
                int i;
                CharPtr color_prom = new CharPtr(color_prom_table);
                CharPtr palette= new CharPtr(palette_table);
		
		for (i = 0;i < Machine.drv.total_colors;i++)
		{
			int bit0,bit1,bit2,bit3;
	
			bit0 = (color_prom.read(0*Machine.drv.total_colors) >> 0) & 0x01;
			bit1 = (color_prom.read(0*Machine.drv.total_colors) >> 1) & 0x01;
			bit2 = (color_prom.read(0*Machine.drv.total_colors) >> 2) & 0x01;
			bit3 = (color_prom.read(0*Machine.drv.total_colors) >> 3) & 0x01;
			palette.writeinc(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
			bit0 = (color_prom.read(1*Machine.drv.total_colors) >> 0) & 0x01;
			bit1 = (color_prom.read(1*Machine.drv.total_colors) >> 1) & 0x01;
			bit2 = (color_prom.read(1*Machine.drv.total_colors) >> 2) & 0x01;
			bit3 = (color_prom.read(1*Machine.drv.total_colors) >> 3) & 0x01;
			palette.writeinc(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
			bit0 = (color_prom.read(2*Machine.drv.total_colors) >> 0) & 0x01;
			bit1 = (color_prom.read(2*Machine.drv.total_colors) >> 1) & 0x01;
			bit2 = (color_prom.read(2*Machine.drv.total_colors) >> 2) & 0x01;
			bit3 = (color_prom.read(2*Machine.drv.total_colors) >> 3) & 0x01;
			palette.writeinc(0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
			color_prom.inc();
		}
	}};
	
	
	
	public static WriteHandlerPtr system8_bankswitch_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		int bankaddress;
	
		bankaddress = 0x10000 + (((data & 0x0c)>>2) * 0x4000);
/*TODO*///		cpu_setbank(1,&RAM[bankaddress]);
                memcpy(RAM,0x8000,RAM,bankaddress,0x4000);//TODO remove temp fix
		system8_bank=data;
	} };
	
	public static ReadHandlerPtr system8_bankswitch_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return(system8_bank);
	} };
	
	
	
	public static WriteHandlerPtr choplifter_scroll_x_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		system8_scrollx_ram.write(offset,data);
	
		scrollx_row[offset/2] = (system8_scrollx_ram.read(offset & ~1) >> 1) + ((system8_scrollx_ram.read(offset | 1) & 1) << 7);
	} };
	
	
	static void choplifter_backgroundrefresh(osd_bitmap bitmap, int layer)
	{
		int code,palette,priority,sx,sy,i;
		int choplifter_scroll_x_on = (system8_scrollx_ram.read(4) == 0xE5 && system8_scrollx_ram.read(5) == 0xFF) ? 0:1;
	
		/*	for every character in the background & text video RAM,
		 *	check if it has been modified since last time and update it accordingly.
		 */
	
		for (i = 0; i < system8_backgroundram_size[0]>>1; i++)
		{
			code = system8_backgroundram.read(i*2) + (system8_backgroundram.read(i*2+1) << 8);
			priority = (code >> 11) & 0x0f;
			palette = (code>>5) & 0x3f;
	
			if (bg_dirtybuffer[i]!=0 || bg_palette_dirtybuffer[palette]!=0)
			{
				if (NOT(priority)!=0)
					bg_dirtybuffer[i]=0;
				sx = (i % 32)<<3;
				sy = ((i >> 5)<<3)+16;
				code = ((code >> 4) & 0x800) | (code & 0x7ff);
	
	//			code = 48+priority;
				if (NOT(layer)!=0)
					drawgfx(bitmap1,Machine.gfx[0],
						code,palette,0,0,sx,sy,null,TRANSPARENCY_NONE,0);
				else
				if ((priority & layer) != 0)
				{
					if (choplifter_scroll_x_on != 0)
					{
						int row = (i >> 5)+2;
						sx = (((i % 32)<<3)+scrollx_row[row]) % 256;
						sy = row<<3;
						drawgfx(bitmap,Machine.gfx[0],
								code,palette,0,0,sx,sy,system8_clip,TRANSPARENCY_PEN,0);
					}
					else
					{
						sx = ((i % 32)<<3);
						sy = ((i >> 5)<<3);
						drawgfx(bitmap,Machine.gfx[0],
								code,palette,0,0,sx,sy+16,system8_clip,TRANSPARENCY_PEN,0);
					}
				}
			}
		}
		memset(bg_palette_dirtybuffer,0,64);
	
		if (NOT(layer)!=0)
		{
			if (choplifter_scroll_x_on != 0)
				copyscrollbitmap(bitmap,bitmap1,32,scrollx_row,0,null,system8_clip,TRANSPARENCY_NONE,0);
			else
				copybitmap(bitmap,bitmap1,0,0,0,0,system8_clip,TRANSPARENCY_NONE,0);
		}
	}
	
	static void choplifter_textrefresh(osd_bitmap bitmap, int layer)
	{
		int code,palette,priority,sx,sy,i;
	
		/*	for every character in the background & text video RAM,
		 *	check if it has been modified since last time and update it accordingly.
		 */
	
		for (i = 0; i < system8_backgroundram_size[0]>>1; i++)
		{
			code = system8_videoram.read(i*2) + (system8_videoram.read(i*2+1) << 8);
			priority = (code >> 11) & 0x01;
			palette = (code>>5) & 0x3f;
	
			if (tx_dirtybuffer[i]!=0 || tx_palette_dirtybuffer[palette]!=0)
			{
				if (NOT(priority)!=0)
					tx_dirtybuffer[i]=0;
				sx = (i % 32)<<3;
				sy = ((i >> 5)<<3);
				code = ((code >> 4) & 0x800) | (code & 0x7ff);
	
	//			code = 48+priority;
				if (layer != 0)
					drawgfx(bitmap,Machine.gfx[1],
						code,palette,0,0,sx,sy+16,system8_clip,TRANSPARENCY_PEN,0);
				else
					drawgfx(bitmap2,Machine.gfx[1],
						code,palette,0,0,sx,sy,null,TRANSPARENCY_NONE,0);
			}
		}
		memset(tx_palette_dirtybuffer,0,64);
	
		if (NOT(layer)!=0)
		/*  Copy background & text bitmap to the screen */
		copybitmap(bitmap,bitmap2,0,0,0,16,system8_clip,TRANSPARENCY_COLOR,131);
	//	else
	//	copybitmap(bitmap,bitmap2,0,0,0,16,&system8_clip,TRANSPARENCY_NONE,0);
	
	}
	
	public static VhUpdatePtr choplifter_vh_screenrefresh= new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
		choplifter_backgroundrefresh(bitmap,0);
		choplifter_textrefresh(bitmap,0);
		choplifter_backgroundrefresh(bitmap,2);
		DrawSprites(bitmap,system8_clip);
		choplifter_backgroundrefresh(bitmap,1);
		choplifter_textrefresh(bitmap,1);
	
	}};
	
	
	
	static void system8_backgroundrefresh(osd_bitmap bitmap, int trasp)
	{
		GfxElement gfx = Machine.gfx[0];
	
		int page;
	        
		//unsigned char * scrx = ((system8_bank&0x80)?(system8_videoram + 0x7F6):(system8_videoram + 0x7c0));
		//unsigned char * scry = ((system8_bank&0x80)?(system8_videoram + 0x784):(system8_videoram + 0x7ba));
	        CharPtr scrx = new CharPtr();
                CharPtr scry = new CharPtr();
                if((system8_bank&0x80)!=0)
                {
                    scrx.set(system8_videoram, 0x7F6);
                    scry.set(system8_videoram, 0x784);
                }
                else
                {
                    scrx.set(system8_videoram, 0x7c0);
                    scry.set(system8_videoram, 0x7ba);
                }
		int scrollx = (scrx.read(0) >> 1) + ((scrx.read(1) & 1) << 7)-256-2;
		int scrolly = -scry.read();
	
		if (scrollx < 0) scrollx = 512 - (-scrollx) % 512; else scrollx = scrollx % 512;
		if (scrolly < 0) scrolly = 512 - (-scrolly) % 512; else scrolly = scrolly % 512;
	
		for (page=0; page < 4; page++)
		{
			//const unsigned char *source = bg_ram + (system8_bg_pagesel[ page*2 ] & 0x07)*0x800;
                        CharPtr source = new CharPtr(bg_ram,(system8_bg_pagesel.read( page*2 ) & 0x07)*0x800);
	
			int startx = (page&1)*256+scrollx;
			int starty = (page>>1)*256+scrolly;
	
			int row,col;
	
			for( row=0; row<28*8; row+=8 )
			{
				for( col=0; col<32*8; col+=8 )
				{
					int x = startx+col;
					int y = starty+row;
					if( x>256 ) x-=512;
					if( y>224 ) y-=512;
					if( x>-8 && x<256 && y>-8 && y<224 )
					{
						int tile = source.read(0) + source.read(1)*256;
						int priority = tile & 0x800;
						tile = ((tile >> 4) & 0x800)|(tile & 0x7ff);
						if (NOT(trasp)!=0)
							drawgfx(bitmap, gfx,
									tile,
									(tile >> 5),
									0, 0, x, y,
									system8_clip, TRANSPARENCY_NONE, 0);
						else if (priority != 0)
							drawgfx(bitmap, gfx,
									tile,
									(tile >> 5),
									0, 0, x, y,
									system8_clip, TRANSPARENCY_COLOR, 0);
					}
					//source+=2;
                                        source.inc(2);
				}
			}
		} /* next page */
	}
	
	static void system8_textrefresh(osd_bitmap bitmap)
	{
		int i;
	
		for (i = 0; i < system8_videoram_size[0]>>1; i++)
		{
			int code = bg_ram[i*2] | (bg_ram[i*2+1] << 8);
			int palette = (code>>5) & 0x3f;
	
			if (tx_dirtybuffer[i]!=0 || tx_palette_dirtybuffer[palette]!=0)
			{
				int sx,sy;
	
				tx_dirtybuffer[i]=0;
				sx = (i % 32)<<3;
				sy = ((i >> 5)<<3);
				code = ((code >> 4) & 0x800) + (code & 0x7ff);
	
				if (sx < 7*8 || sy < 3*8 || sy >= 216)
					drawgfx(bitmap,Machine.gfx[1],
						code,palette,0,0,sx,sy,null,TRANSPARENCY_NONE,0);
				else
					drawgfx(bitmap,Machine.gfx[1],
						code,palette,0,0,sx,sy,null,TRANSPARENCY_COLOR,0);
			}
		}
	}

        public static VhUpdatePtr wbml_vh_screenrefresh= new VhUpdatePtr() { public void handler(osd_bitmap bitmap) 
	{
		system8_backgroundrefresh(bitmap,0);
		DrawSprites(bitmap,system8_clip);
		system8_backgroundrefresh(bitmap,1);
		system8_textrefresh(bitmap);
	}};
}
