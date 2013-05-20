/***************************************************************************

Based on drivers from Juno First emulator by Chris Hardy (chrish@kcbbs.gen.nz)

***************************************************************************/

/*
 * ported to v0.29
 * using automatic conversion tool v0.04
 * converted at : 20-05-2013 17:28:21
 *
 *
 * roms are from v0.36 romset
 *
 */ 
package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static vidhrdw.generic.*;
import static sndhrdw.generic.*;
import static vidhrdw.rocnrope.*;
import static m6809.M6809H.*;
import static m6809.M6809.*;
import static mame.memoryH.*;
import static sndhrdw._8910intf.*;
import static sndhrdw._8910intfH.*;
import static arcadeflex.osdepend.*;
import static mame.osdependH.*;

public class rocnrope
{
	public static InitMachinePtr rocnrope_init_machine = new InitMachinePtr()
        {
         public void handler() {

                /* Set optimization flags for M6809 */
                m6809_Flags = M6809_FAST_S | M6809_FAST_U;
        }};
	/* Roc'n'Rope has the IRQ vectors in RAM. The rom contains $FFFF at this address! */
	public static WriteHandlerPtr rocnrope_interrupt_vector_w = new WriteHandlerPtr(){
         public void handler(int offset, int data) {  
	
		RAM[0xFFF2+offset] = (char)data;
	}};
	
	/* I am not 100% sure that this timer is correct, but */
	/* I'm using the Gyruss wired to the higher 4 bits    */
	/* instead of the lower ones, so there is a good      */
	/* chance it's the right one. */
	
	/* The timer clock which feeds the lower 4 bits of    */
	/* AY-3-8910 port A is based on the same clock        */
	/* feeding the sound CPU Z80.  It is a divide by      */
	/* 10240, formed by a standard divide by 1024,        */
	/* followed by a divide by 10 using a 4 bit           */
	/* bi-quinary count sequence. (See LS90 data sheet    */
	/* for an example).                                   */
	/* Bits 1-3 come directly from the upper three bits   */
	/* of the bi-quinary counter. Bit 0 comes from the    */
	/* output of the divide by 1024.                      */
	
	static int rocnrope_timer[] = {
	0x00, 0x01, 0x00, 0x01, 0x02, 0x03, 0x02, 0x03, 0x04, 0x05,
	0x08, 0x09, 0x08, 0x09, 0x0a, 0x0b, 0x0a, 0x0b, 0x0c, 0x0d
	};
        /* need to protect from totalcycles overflow */
	static int last_totalcycles = 0;
        /* number of Z80 clock cycles to count */
        static int clock;
	public static ReadHandlerPtr rocnrope_portB_r = new ReadHandlerPtr() {  public int handler(int offset)
	{
		int current_totalcycles;
	
		current_totalcycles = cpu_gettotalcycles();
		clock = (clock + (current_totalcycles-last_totalcycles)) % 10240;
	
		last_totalcycles = current_totalcycles;
	
		return rocnrope_timer[clock/512] << 4;
	}};
        static int last;
	public static WriteHandlerPtr rocnrope_sh_irqtrigger_w = new WriteHandlerPtr(){
         public void handler(int offset, int data) { 
		if (last == 0 && data == 1)
		{
			/* setting bit 0 low then high triggers IRQ on the sound CPU */
			cpu_cause_interrupt(1,0xff);
		}
	
		last = data;
	}};
	
	
	
	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x3080, 0x3080, input_port_0_r ), /* IO Coin */
		new MemoryReadAddress( 0x3081, 0x3081, input_port_1_r ), /* P1 IO */
		new MemoryReadAddress( 0x3082, 0x3082, input_port_2_r ), /* P2 IO */
		new MemoryReadAddress( 0x3083, 0x3083, input_port_3_r ), /* DSW 0 */
		new MemoryReadAddress( 0x3000, 0x3000, input_port_4_r ), /* DSW 1 */
		new MemoryReadAddress( 0x3100, 0x3100, input_port_5_r ), /* DSW 2 */
		new MemoryReadAddress( 0x4000, 0x5fff, MRA_RAM ),
		new MemoryReadAddress( 0x6000, 0xffff, MRA_ROM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x4000, 0x403f, MWA_RAM, spriteram_2 ),
		new MemoryWriteAddress( 0x4040, 0x43ff, MWA_RAM ),
		new MemoryWriteAddress( 0x4400, 0x443f, MWA_RAM, spriteram, spriteram_size ),
		new MemoryWriteAddress( 0x4440, 0x47ff, MWA_RAM ),
		new MemoryWriteAddress( 0x4800, 0x4bff, colorram_w, colorram ),
		new MemoryWriteAddress( 0x4c00, 0x4fff, videoram_w, videoram, videoram_size ),
		new MemoryWriteAddress( 0x5000, 0x5fff, MWA_RAM ),
/*TEMPHACK TODO*/		//new MemoryWriteAddress( 0x8000, 0x8000, watchdog_reset_w ),
		new MemoryWriteAddress( 0x8080, 0x8080, rocnrope_flipscreen_w ),
		new MemoryWriteAddress( 0x8081, 0x8081, rocnrope_sh_irqtrigger_w ),  /* cause interrupt on audio CPU */
		new MemoryWriteAddress( 0x8082, 0x8082, MWA_NOP ),	/* interrupt acknowledge??? */
		new MemoryWriteAddress( 0x8083, 0x8083, MWA_NOP ),	/* Coin counter 1 */
		new MemoryWriteAddress( 0x8084, 0x8084, MWA_NOP ),	/* Coin counter 2 */
		new MemoryWriteAddress( 0x8087, 0x8087, interrupt_enable_w ),
		new MemoryWriteAddress( 0x8100, 0x8100, soundlatch_w ),
		new MemoryWriteAddress( 0x8182, 0x818d, rocnrope_interrupt_vector_w ),
		new MemoryWriteAddress( 0x6000, 0xffff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	static MemoryReadAddress sound_readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x1fff, MRA_ROM ),
		new MemoryReadAddress( 0x3000, 0x33ff, MRA_RAM ),
		new MemoryReadAddress( 0x4000, 0x4000, AY8910_read_port_0_r ),
		new MemoryReadAddress( 0x6000, 0x6000, AY8910_read_port_1_r ),
		new MemoryReadAddress( -1 )	/* end of table */
	};
	
	static MemoryWriteAddress sound_writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x1fff, MWA_ROM ),
		new MemoryWriteAddress( 0x3000, 0x33ff, MWA_RAM ),
		new MemoryWriteAddress( 0x4000, 0x4000, AY8910_write_port_0_w ),
		new MemoryWriteAddress( 0x5000, 0x5000, AY8910_control_port_0_w ),
		new MemoryWriteAddress( 0x6000, 0x6000, AY8910_write_port_1_w ),
		new MemoryWriteAddress( 0x7000, 0x7000, AY8910_control_port_1_w ),
		new MemoryWriteAddress( -1 )	/* end of table */
	};
	
	
	
	static InputPortPtr input_ports = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* DSW0 */
		PORT_DIPNAME( 0x0f, 0x0f, "Coin A", IP_KEY_NONE );
		PORT_DIPSETTING(    0x02, "4 Coins/1 Credit" );
		PORT_DIPSETTING(    0x05, "3 Coins/1 Credit" );
		PORT_DIPSETTING(    0x08, "2 Coins/1 Credit" );
		PORT_DIPSETTING(    0x04, "3 Coins/2 Credits" );
		PORT_DIPSETTING(    0x01, "4 Coins/3 Credits" );
		PORT_DIPSETTING(    0x0f, "1 Coin/1 Credit" );
		PORT_DIPSETTING(    0x03, "3 Coins/4 Credits" );
		PORT_DIPSETTING(    0x07, "2 Coins/3 Credits" );
		PORT_DIPSETTING(    0x0e, "1 Coin/2 Credits" );
		PORT_DIPSETTING(    0x06, "2 Coins/5 Credits" );
		PORT_DIPSETTING(    0x0d, "1 Coin/3 Credits" );
		PORT_DIPSETTING(    0x0c, "1 Coin/4 Credits" );
		PORT_DIPSETTING(    0x0b, "1 Coin/5 Credits" );
		PORT_DIPSETTING(    0x0a, "1 Coin/6 Credits" );
		PORT_DIPSETTING(    0x09, "1 Coin/7 Credits" );
		PORT_DIPSETTING(    0x00, "Free Play" );
		PORT_DIPNAME( 0xf0, 0xf0, "Coin B", IP_KEY_NONE );
		PORT_DIPSETTING(    0x20, "4 Coins/1 Credit" );
		PORT_DIPSETTING(    0x50, "3 Coins/1 Credit" );
		PORT_DIPSETTING(    0x80, "2 Coins/1 Credit" );
		PORT_DIPSETTING(    0x40, "3 Coins/2 Credits" );
		PORT_DIPSETTING(    0x10, "4 Coins/3 Credits" );
		PORT_DIPSETTING(    0xf0, "1 Coin/1 Credit" );
		PORT_DIPSETTING(    0x30, "3 Coins/4 Credits" );
		PORT_DIPSETTING(    0x70, "2 Coins/3 Credits" );
		PORT_DIPSETTING(    0xe0, "1 Coin/2 Credits" );
		PORT_DIPSETTING(    0x60, "2 Coins/5 Credits" );
		PORT_DIPSETTING(    0xd0, "1 Coin/3 Credits" );
		PORT_DIPSETTING(    0xc0, "1 Coin/4 Credits" );
		PORT_DIPSETTING(    0xb0, "1 Coin/5 Credits" );
		PORT_DIPSETTING(    0xa0, "1 Coin/6 Credits" );
		PORT_DIPSETTING(    0x90, "1 Coin/7 Credits" );
		PORT_DIPSETTING(    0x00, "Disabled" );
	/* 0x00 disables Coin 2. It still accepts coins and makes the sound, but
	   it doesn't give you any credit */
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, "Lives", IP_KEY_NONE );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPSETTING(    0x01, "5" );
		PORT_BITX( 0,       0x00, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "255", IP_KEY_NONE, IP_JOY_NONE, 0 );
		PORT_DIPNAME( 0x04, 0x00, "Cabinet", IP_KEY_NONE );
		PORT_DIPSETTING(    0x00, "Upright" );
		PORT_DIPSETTING(    0x04, "Cocktail" );
		PORT_DIPNAME( 0x78, 0x78, "Difficulty", IP_KEY_NONE );
		PORT_DIPSETTING(    0x78, "Easy 1" );
		PORT_DIPSETTING(    0x70, "Easy 2" );
		PORT_DIPSETTING(    0x68, "Easy 3" );
		PORT_DIPSETTING(    0x60, "Easy 4" );
		PORT_DIPSETTING(    0x58, "Normal 1" );
		PORT_DIPSETTING(    0x50, "Normal 2" );
		PORT_DIPSETTING(    0x48, "Normal 3" );
		PORT_DIPSETTING(    0x40, "Normal 4" );
		PORT_DIPSETTING(    0x38, "Normal 5" );
		PORT_DIPSETTING(    0x30, "Normal 6" );
		PORT_DIPSETTING(    0x28, "Normal 7" );
		PORT_DIPSETTING(    0x20, "Normal 8" );
		PORT_DIPSETTING(    0x18, "Difficult 1" );
		PORT_DIPSETTING(    0x10, "Difficult 2" );
		PORT_DIPSETTING(    0x08, "Difficult 3" );
		PORT_DIPSETTING(    0x00, "Difficult 4" );
		PORT_DIPNAME( 0x80, 0x00, "Demo Sounds", IP_KEY_NONE );
		PORT_DIPSETTING(    0x80, "Off" );
		PORT_DIPSETTING(    0x00, "On" );
	
		PORT_START();       /* DSW2 */
		PORT_DIPNAME( 0x07, 0x07, "First Bonus", IP_KEY_NONE );
		PORT_DIPSETTING(    0x07, "20000" );
		PORT_DIPSETTING(    0x05, "30000" );
		PORT_DIPSETTING(    0x04, "40000" );
		PORT_DIPSETTING(    0x03, "50000" );
		PORT_DIPSETTING(    0x02, "60000" );
		PORT_DIPSETTING(    0x01, "70000" );
		PORT_DIPSETTING(    0x00, "80000" );
		/* 0x06 gives 20000 */
		PORT_DIPNAME( 0x38, 0x38, "Repeated Bonus", IP_KEY_NONE );
		PORT_DIPSETTING(    0x38, "40000" );
		PORT_DIPSETTING(    0x18, "50000" );
		PORT_DIPSETTING(    0x10, "60000" );
		PORT_DIPSETTING(    0x08, "70000" );
		PORT_DIPSETTING(    0x00, "80000" );
		/* 0x20, 0x28 and 0x30 all gives 40000 */
		PORT_DIPNAME( 0x40, 0x00, "Grant Repeated Bonus", IP_KEY_NONE );
		PORT_DIPSETTING(    0x40, "No" );
		PORT_DIPSETTING(    0x00, "Yes" );
		PORT_DIPNAME( 0x80, 0x00, "Unknown DSW 8", IP_KEY_NONE );
		PORT_DIPSETTING(    0x80, "Off" );
		PORT_DIPSETTING(    0x00, "On" );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 sprites */
		512,	/* 512 characters */
		4,	/* 4 bits per pixel */
		new int[] { 0x2000*8+4, 0x2000*8+0, 4, 0 },
		new int[] { 0, 1, 2, 3, 8*8+0, 8*8+1, 8*8+2, 8*8+3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		16*8	/* every sprite takes 64 consecutive bytes */
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		256,	/* 256 sprites */
		4,	/* 4 bits per pixel */
		new int[] { 256*64*8+4, 256*64*8+0, 4, 0 },
		new int[] { 0, 1, 2, 3, 8*8+0, 8*8+1, 8*8+2, 8*8+3,
				16*8+0, 16*8+1, 16*8+2, 16*8+3, 24*8+0, 24*8+1, 24*8+2, 24*8+3 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				32*8, 33*8, 34*8, 35*8, 36*8, 37*8, 38*8, 39*8 },
		64*8	/* every sprite takes 64 consecutive bytes */
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 1, 0x0000, charlayout,       0, 16 ),
		new GfxDecodeInfo( 1, 0x4000, spritelayout, 16*16, 16 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static char color_prom[] =
	{
		/* palette */
		0x00,0x07,0x38,0x3F,0xC0,0x77,0x1D,0x88,0xE0,0xF8,0x14,0xAD,0x13,0x48,0x26,0xFF,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		/* sprite lookup table */
		0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,
		0x00,0x03,0x05,0x04,0x02,0x05,0x0A,0x07,0x08,0x01,0x0A,0x0B,0x0C,0x0D,0x0E,0x02,
		0x00,0x0F,0x02,0x03,0x04,0x05,0x06,0x03,0x08,0x01,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,
		0x00,0x05,0x02,0x03,0x0F,0x05,0x06,0x09,0x08,0x03,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,
		0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x09,0x08,0x01,0x0A,0x0B,0x0C,0x0D,0x0E,0x03,
		0x00,0x01,0x02,0x03,0x04,0x01,0x06,0x07,0x08,0x03,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,
		0x00,0x05,0x04,0x0B,0x05,0x09,0x0A,0x0B,0x0C,0x04,0x0E,0x0F,0x0C,0x02,0x03,0x0F,
		0x00,0x03,0x05,0x01,0x06,0x05,0x0E,0x0A,0x0D,0x08,0x04,0x03,0x0C,0x07,0x04,0x02,
		0x00,0x05,0x03,0x04,0x05,0x09,0x0A,0x0B,0x0C,0x04,0x0E,0x0F,0x0C,0x02,0x03,0x0F,
		0x00,0x05,0x01,0x0F,0x05,0x05,0x0A,0x0B,0x0C,0x04,0x0E,0x0F,0x0C,0x02,0x03,0x0F,
		0x00,0x05,0x04,0x02,0x05,0x05,0x0A,0x0B,0x0C,0x04,0x0E,0x0F,0x0C,0x02,0x03,0x01,
		0x00,0x05,0x03,0x0B,0x05,0x05,0x0A,0x0B,0x0C,0x04,0x0E,0x0F,0x0C,0x02,0x03,0x0F,
		0x00,0x02,0x05,0x03,0x05,0x05,0x02,0x01,0x0E,0x0A,0x0B,0x07,0x0C,0x02,0x03,0x0F,
		0x00,0x03,0x06,0x0F,0x06,0x02,0x0D,0x0C,0x0B,0x05,0x07,0x09,0x0C,0x01,0x0F,0x03,
		0x00,0x02,0x04,0x06,0x03,0x05,0x08,0x09,0x07,0x0F,0x0E,0x0A,0x0C,0x07,0x07,0x0F,
		0x00,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,0x0E,0x0F,0x0F,0x0F,0x0C,0x08,0x08,0x0F,
		/* character lookup table */
		0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,
		0x00,0x02,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0C,0x0B,0x0D,0x0D,0x0E,0x0F,
		0x00,0x03,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x06,0x0B,0x0A,0x0D,0x0E,0x0F,
		0x00,0x04,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,
		0x00,0x05,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x03,
		0x00,0x06,0x02,0x03,0x01,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,
		0x00,0x07,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,
		0x00,0x08,0x02,0x03,0x04,0x05,0x0C,0x07,0x08,0x09,0x0A,0x0B,0x00,0x0D,0x0E,0x0F,
		0x00,0x09,0x02,0x03,0x04,0x05,0x0C,0x07,0x08,0x09,0x0A,0x0B,0x00,0x0D,0x0E,0x03,
		0x00,0x0F,0x02,0x03,0x09,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x00,0x0D,0x0E,0x0F,
		0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x00,0x0D,0x0E,0x0F,
		0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0D,0x0D,0x0E,0x0F,
		0x00,0x0D,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0D,0x0D,0x0E,0x0F,
		0x00,0x0B,0x02,0x03,0x03,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0D,0x0D,0x0E,0x03,
		0x00,0x0F,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0D,0x0D,0x0E,0x0F,
		0x00,0x01,0x02,0x01,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0D,0x0D,0x0E,0x0F
	};
	
	
//TODO i hacked the interface with the old one (shadow)	
	/*static struct AY8910interface ay8910_interface =
	{
		2,	
		1789750,	
		{ 0x38ff, 0x38ff },
		{ soundlatch_r },
		{ rocnrope_portB_r },
		{ 0 },
		{ 0 }
	};*/
        
/*temp hack*/	
        static AY8910interface ay8910_interface = new AY8910interface
	(
		2,	/* 2 chips */
                12, /*not used  anymore*/
		1789750,	/* 1.78975 MHz ? (same as other Konami games) */
		new int[] { 0x38ff, 0x38ff },
		new ReadHandlerPtr[] { soundlatch_r },
		new ReadHandlerPtr[] { rocnrope_portB_r },
		new WriteHandlerPtr[] { null,null },
		new WriteHandlerPtr[] { null,null}
	);
/*temp hack to work with old interface */
	public static ShStartPtr rocnrop_sh_start = new ShStartPtr() { public int handler()
	{
		pending_commands = 0;

		return AY8910_sh_start(ay8910_interface);
	} };
	
	static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_M6809,
				2048000,        /* 2 Mhz */
				0,
				readmem,writemem,null,null,
				interrupt,1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				14318180/4,	/* ???? same as other Konami games */
				2,	/* memory region #2 */
				sound_readmem,sound_writemem,null,null,
				ignore_interrupt,1	/* interrupts are triggered by the main CPU */
			)
		},
		60, 
/*TEMPHACK*/                /*DEFAULT_60HZ_VBLANK_DURATION,*/	/* frames per second, vblank duration */
		1,	/* 1 CPU slice per frame - interleaving is forced when a sound command is written */
		rocnrope_init_machine,
	
		/* video hardware */
		32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
		gfxdecodeinfo,
		32,16*16+16*16,
		rocnrope_vh_convert_color_prom,
	
		VIDEO_TYPE_RASTER,
		null,
		generic_vh_start,
		generic_vh_stop,
		rocnrope_vh_screenrefresh,
	
		/* sound hardware */
/*TEMPHACK*/		null,null,rocnrop_sh_start,
                AY8910_sh_stop,
		AY8910_sh_update
                
                
			/* sound hardware */
     /*TOFIX*/       /*    null,null,null,null,
                {
                        {
                                SOUND_AY8910,
                                &ay8910_interface
                        }
                }*/
	);
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/

	static RomLoadPtr rocnrope_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000);    /* 64k for code */
		ROM_LOAD( "rr1.1h", 0x6000, 0x2000, 0x431c1ca0 );
		ROM_LOAD( "rr2.2h", 0x8000, 0x2000, 0x2e1a952e );
		ROM_LOAD( "rr3.3h", 0xA000, 0x2000, 0x6fae29fa );
		ROM_LOAD( "rr4.4h", 0xC000, 0x2000, 0x1a2852cc );
		ROM_LOAD( "rnr_h5.vid", 0xE000, 0x2000, 0x474255f6 );
	
		ROM_REGION(0xc000);   /* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "rnr_h12.vid", 0x0000, 0x2000, 0xc1895bf3 );
		ROM_LOAD( "rnr_h11.vid", 0x2000, 0x2000, 0x854f7d6d );
		ROM_LOAD( "rnr_a11.vid", 0x4000, 0x2000, 0xccd353a1 );
		ROM_LOAD( "rnr_a12.vid", 0x6000, 0x2000, 0x7918ecd6 );
		ROM_LOAD( "rnr_a9.vid",  0x8000, 0x2000, 0xbbdb0eef );
		ROM_LOAD( "rnr_a10.vid", 0xa000, 0x2000, 0xa087b117 );
	
		ROM_REGION(0x10000);/* 64k for the audio CPU */
		ROM_LOAD( "rnr_7a.snd", 0x0000, 0x1000, 0x2c7ea8d8 );
		ROM_LOAD( "rnr_8a.snd", 0x1000, 0x1000, 0x172f0eab );
	ROM_END(); }}; 
	
	static RomLoadPtr ropeman_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000);    /* 64k for code */
		ROM_LOAD( "j01_rm01.bin", 0x6000, 0x2000, 0x64a5efa5 );
		ROM_LOAD( "j02_rm02.bin", 0x8000, 0x2000, 0xfebda671 );
		ROM_LOAD( "j03_rm03.bin", 0xA000, 0x2000, 0xbb34d17a );
		ROM_LOAD( "j04_rm04.bin", 0xC000, 0x2000, 0x0d68c368 );
		ROM_LOAD( "j05_rm05.bin", 0xE000, 0x2000, 0x474255f6 );
	
		ROM_REGION(0xc000);   /* temporary space for graphics (disposed after conversion) */
		ROM_LOAD( "j12_rm07.bin", 0x0000, 0x2000, 0x9dd6694a );
		ROM_LOAD( "j11_rm06.bin", 0x2000, 0x2000, 0x60afcded );
		ROM_LOAD( "a11_rm10.bin", 0x4000, 0x2000, 0xccd353a1 );
		ROM_LOAD( "a12_rm11.bin", 0x6000, 0x2000, 0x7918ecd6 );
		ROM_LOAD( "a09_rm08.bin", 0x8000, 0x2000, 0xbbdb0eef );
		ROM_LOAD( "a10_rm09.bin", 0xa000, 0x2000, 0xa087b117 );
	
		ROM_REGION(0x10000);/* 64k for the audio CPU */
		ROM_LOAD( "a07_rm12.bin", 0x0000, 0x1000, 0x2c7ea8d8 );
		ROM_LOAD( "a08_rm13.bin", 0x1000, 0x1000, 0x172f0eab );
	ROM_END(); }}; 
        
        
	/*temp here to see if it works*/
        static char KonamiDecode(char opcode,int address)
        {
            char xormask = 0;
            if ((address & 0x02)!=0) xormask |= 0x80;
            else xormask |= 0x20;
            if ((address & 0x08)!=0) xormask |= 0x08;
            else xormask |= 0x02;

            return (char)(opcode ^ xormask);
        }
        static DecodePtr rocnrope_decode = new DecodePtr(){ public void handler()
        {
		int A;
	
	
		for (A = 0x6000;A < 0x10000;A++)
		{
			ROM[A] = KonamiDecode(RAM[A],A);
		}
	}};
	
	
	
	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler() 
	{
		/* get RAM pointer (this game is multiCPU, we can't assume the global */
		/* RAM pointer is pointing to the right place) */
		char[] RAM= Machine.memory_region[0];
	
		if (memcmp(RAM, 0x5160, new char[] { 0x01,0x00,0x00 }, 3) == 0 &&
			memcmp(RAM, 0x50A6, new char[] { 0x01,0x00,0x00 }, 3) == 0)
		{
			FILE f;
	
	
			if ((f = osd_fopen(Machine.gamedrv.name,null,OSD_FILETYPE_HIGHSCORE,0)) != null)
			{
				osd_fread(f,RAM, 0x5160,0x40);
				RAM[0x50A6] = RAM[0x5160];
				RAM[0x50A7] = RAM[0x5161];
				RAM[0x50A8] = RAM[0x5162];
				osd_fclose(f);
			}
	
			return 1;
		}
		else return 0;  /* we can't load the hi scores yet */
	} };
	
	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler() 
	{
		/* get RAM pointer (this game is multiCPU, we can't assume the global */
		/* RAM pointer is pointing to the right place) */
		char[] RAM= Machine.memory_region[0];
		FILE f;
	
		if ((f = osd_fopen(Machine.gamedrv.name,null,OSD_FILETYPE_HIGHSCORE,1)) != null)
		{
			osd_fwrite(f,RAM, 0x5160,0x40);
			osd_fclose(f);
		}
	} };
	
	
	
	public static GameDriver rocnrope_driver = new GameDriver
	(
		"Rock'n'Rope",
		"rocnrope",
		"Chris Hardy (MAME driver)\nPaul Swan (color info)\nValerio Verrando (high score save)",
		machine_driver,
	
		rocnrope_rom,
		null, rocnrope_decode,
		null,
/*TEMPHACK*/		//null,	/* sound_prom */
	
/*TEMPHACK*/		null,input_ports,null, null, null,/*TODO remove nulls*/
	
		color_prom, null, null,
		ORIENTATION_ROTATE_270,
	
		hiload, hisave
	);
	
	public static GameDriver ropeman_driver = new GameDriver
	(
		"Rope Man",
		"ropeman",
		"Chris Hardy (MAME driver)\nPaul Swan (color info)\nValerio Verrando (high score save)",
		machine_driver,
	
		ropeman_rom,
		null, rocnrope_decode,
		null,
/*TEMPHACK*/		//null,	/* sound_prom */
	
/*TEMPHACK*/		null,input_ports,null, null, null,/*TODO remove nulls*/
	
		color_prom, null, null,
		ORIENTATION_ROTATE_270,
	
		hiload, hisave
	);
}
