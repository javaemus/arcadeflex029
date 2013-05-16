/*
 * ported to v0.29
 * ported to v0.28
 * ported to v0.27
 *
 *  Note : InputPort defination doesn't include joystick
 *
 */
package mame;
import static mame.osdependH.*;
import static mame.commonH.*;
import static arcadeflex.libc.*;
import static mame.memoryH.*;
/**
 *
 * @author shadow
 */
public class driverH
{
	public static abstract interface ReadHandlerPtr { public abstract int handler(int offset); }
	public static abstract interface WriteHandlerPtr { public abstract void handler(int offset, int data); }
	public static abstract interface InitMachinePtr { public abstract void handler(); }
	public static abstract interface InterruptPtr { public abstract int handler(); }
	public static abstract interface VhConvertColorPromPtr { public abstract void handler(char []palette, char []colortable, char []color_prom); }
	public static abstract interface VhInitPtr { public abstract int handler(String gamename); }
	public static abstract interface VhStartPtr { public abstract int handler(); }
	public static abstract interface VhStopPtr { public abstract void handler(); }
	public static abstract interface VhUpdatePtr { public abstract void handler(osd_bitmap bitmap); }
	public static abstract interface ShInitPtr { public abstract int handler(String gamename); }
	public static abstract interface ShStartPtr { public abstract int handler(); }
	public static abstract interface ShStopPtr { public abstract void handler(); }
	public static abstract interface ShUpdatePtr { public abstract void handler(); }
	public static abstract interface DecodePtr {  public abstract void handler();}
	public static abstract interface HiscoreLoadPtr { public abstract int handler(); }
	public static abstract interface HiscoreSavePtr { public abstract void handler(); }
        public static abstract interface ConversionPtr{ public abstract int handler(int data);}
        public static abstract interface RomLoadPtr { public abstract void handler();}
        public static abstract interface InputPortPtr { public abstract void handler();}


	/***************************************************************************

	Don't confuse this with the I/O ports above. This is used to handle game
	inputs (joystick, coin slots, etc). Typically, you will read them using
	input_port_[n]_r(), which you will associate to the appropriate memory
	address or I/O port.

	***************************************************************************/
	public static class InputPort
	{
		public InputPort(int dv, int k[]) { default_value = dv; keyboard = k; };
		public InputPort(int dv) { this(-1, null); };
		public int default_value;	/* default value for the input port */
		public int keyboard[];	/* keys affecting the 8 bits of the input port (0 means none) */
	  //      public int joystick[];	/* same for joystick */  //TODO??
        };
        /* Many games poll an input bit to check for vertical blanks instead of using */
        /* interrupts. This special value to put in the keyboard[] field allows you to */
        /* handle that. If you set one of the input bits to this, the bit will be */
        /* inverted while a vertical blank is happening. */
        public static int IPB_VBLANK = -1;

        public static class NewInputPort implements Cloneable
        {
            
            protected Object deepClone() throws CloneNotSupportedException
            {
                NewInputPort port = new NewInputPort();
                port.setMask(this.mask);
                port.setDefault_value(this.default_value);
                port.setType(this.type);
                port.setName(this.name);
                port.setKeyboard(this.keyboard);
                port.setJoystick(this.joystick);
                port.setArg(this.arg);
                return port;
            }
            public NewInputPort(){}; //TODO dummy for now! we will create it based on the need macros
            
            public NewInputPort(int m,int dv,int t,String n,int k,int j,int a)
            {
                mask=m;
                default_value=dv;
                type=t;
                name=n;
                keyboard=k;
                joystick=j;
                arg=a;
            }
            public int mask;	/* bits affected */
            public int default_value;	/* default value for the bits affected */
							/* you can also use one of the IP_ACTIVE defines below */
            public int type;	/* see defines below */
            public String name;	/* name to display */
            public int keyboard;	/* key affecting the input bits */
            public int joystick;	/* joystick command affecting the input bits */
            public int arg;	/* extra argument needed in some cases */

            //needed for clone method
            public void setArg(int arg) {this.arg = arg;}
            public void setDefault_value(int default_value) {this.default_value = default_value;}
            public void setJoystick(int joystick) {this.joystick = joystick;}
            public void setKeyboard(int keyboard) { this.keyboard = keyboard;}
            public void setMask(int mask) {this.mask = mask; }
            public void setName(String name) {this.name = name;}
            public void setType(int type) {this.type = type;}

        };
        public static final int IP_ACTIVE_HIGH=0x00;
        public static final int IP_ACTIVE_LOW=0xff;
        
            public static final int IPT_END=1;
            public static final int IPT_PORT=2;
            /* use IPT_JOYSTICK for panels where the player has one single joystick */
            public static final int IPT_JOYSTICK_UP=3;
            public static final int IPT_JOYSTICK_DOWN=4;
            public static final int IPT_JOYSTICK_LEFT=5;
            public static final int IPT_JOYSTICK_RIGHT=6;
            /* use IPT_JOYSTICKLEFT and IPT_JOYSTICKRIGHT for dual joystick panels */
            public static final int IPT_JOYSTICKRIGHT_UP=7;
            public static final int IPT_JOYSTICKRIGHT_DOWN=8;
            public static final int IPT_JOYSTICKRIGHT_LEFT=9;
            public static final int IPT_JOYSTICKRIGHT_RIGHT=10;
            public static final int IPT_JOYSTICKLEFT_UP=11;
            public static final int IPT_JOYSTICKLEFT_DOWN=12;
            public static final int IPT_JOYSTICKLEFT_LEFT=13;
            public static final int IPT_JOYSTICKLEFT_RIGHT=14;
            /* action buttons */
            public static final int IPT_BUTTON1=15;
            public static final int IPT_BUTTON2=16;
            public static final int IPT_BUTTON3=17;
            public static final int IPT_BUTTON4=18;	
            public static final int IPT_BUTTON5=19;
            public static final int IPT_BUTTON6=20;
            public static final int IPT_BUTTON7=21;
            public static final int IPT_BUTTON8=22;

            /* analog inputs */
            /* the "arg" field contains the default sensitivity expressed as a percentage */
            /* (100 = default, 50 = half, 200 = twice) */
            public static final int IPT_DIAL=23;
            public static final int IPT_TRACKBALL_X=24;
            public static final int IPT_TRACKBALL_Y=25;
            public static final int IPT_AD_STICK_X=26;
            public static final int IPT_AD_STICK_Y=27;
            /* coin slots */
            public static final int IPT_COIN1=28;
            public static final int IPT_COIN2=29;
            public static final int IPT_COIN3=30;
            public static final int IPT_COIN4=31;
            /* start buttons */
            public static final int IPT_START1=32;
            public static final int IPT_START2=33;
            public static final int IPT_START3=34;
            public static final int IPT_START4=35;
            public static final int IPT_SERVICE=36;
            public static final int IPT_TILT=37;
            public static final int IPT_DIPSWITCH_NAME=38;
            public static final int IPT_DIPSWITCH_SETTING=39;
            public static final int IPT_VBLANK=40;
            public static final int IPT_UNKNOWN=41;
            
            

            public static final int IPF_MASK=       0xffff0000;
            public static final int IPF_UNUSED=     0x80000000;	/* The bit is not used by this game, but is used */
                                                                                    /* by other games running on the same hardware. */
                                                                                    /* This is different from IPT_UNUSED, which marks */
                                                                                    /* bits not connected to anything. */
            public static final int IPT_UNUSED=     IPF_UNUSED;
            
            public static final int IPF_COCKTAIL=IPF_UNUSED;	/* the bit is used in cocktail mode only */

            public static final int IPF_CHEAT=      0x40000000;	/* Indicates that the input bit is a "cheat" key */
                                                                                    /* (providing invulnerabilty, level advance, and */
                                                                                    /* so on). MAME will not recognize it when the */
                                                                                    /* -nocheat command line option is specified. */

            public static final int IPF_PLAYERMASK= 0x00030000;	/* use IPF_PLAYERn if more than one person can */
            public static final int IPF_PLAYER1=    0;         	/* play at the same time. The IPT_ should be the same */
            public static final int IPF_PLAYER2=    0x00010000;	/* for all players (e.g. IPT_BUTTON1 | IPF_PLAYER2) */
            public static final int IPF_PLAYER3=    0x00020000;	/* IPF_PLAYER1 is the default and can be left out to */
            public static final int IPF_PLAYER4=    0x00030000;	/* increase readability. */

            public static final int IPF_8WAY=       0;        	/* Joystick modes of operation. 8WAY is the default, */
            public static final int IPF_4WAY=       0x00080000;	/* it prevents left/right or up/down to be pressed at */
            public static final int IPF_2WAY=       0 ;        	/* the same time. 4WAY prevents diagonal directions. */
                                                                                    /* 2WAY should be used for joysticks wich move only */
                                                    /* on one axis (e.g. Battle Zone) */

            public static final int IPF_IMPULSE=    0x00100000;	/* When this is set, when the key corrisponding to */
                                                                                    /* the input bit is pressed it will be reported as */
                                                                                    /* pressed for a certain number of video frames and */
                                                                                    /* then released, regardless of the real status of */
                                                                                    /* the key. This is useful e.g. for some coin inputs. */
                                                                                    /* The number of frames the signal should stay active */
                                                                                    /* is specified in the "arg" field. */
            public static final int IPF_TOGGLE=     0x00200000;	/* When this is set, the key acts as a toggle - press */
                                                                                    /* it once and it goes on, press it again and it goes off. */
                                                                                    /* useful e.g. for sone Test Mode dip switches. */
            public static final int IPF_REVERSE=    0x00400000;	/* By default, analog inputs like IPT_TRACKBALL increase */
                                                                                    /* when going right/up. This flag inverts them. */
            
            public static final int IPF_CENTER     =0x00800000;	/* always preload in->default, autocentering the STICK/TRACKBALL */

/* LBO - These 4 byte values are packed into the arg field and are typically used with analog ports */
/* Since the sensivity is only one byte, we want to have 100% = 2^16, */
/* 50% = 2^8, 200% = 2^32 and so on. BW	*/
        public static  int IPF_SENSITIVITY(int percent)	{ return (((percent*16)/100)&0xff);}
        public static  int IPF_CLIP(int clip)		{ return	((clip&0xff) << 8  );}
        public static  int IPF_MIN(int min)		{ return	((min&0xff)  << 16 );}
        public static  int IPF_MAX(int max)		{ return	((max&0xff)  << 24 );}
            
           public static final String IP_NAME_DEFAULT="-1";

            public static final int IP_KEY_DEFAULT= -1;
            public static final int IP_KEY_NONE= -2;
            public static final int IP_KEY_PREVIOUS= -3;	/* use the same key as the previous input bit */

            public static final int IP_JOY_DEFAULT= -1;
            public static final int IP_JOY_NONE= -2;
            public static final int IP_JOY_PREVIOUS= -3;	/* use the same joy as the previous input bit */
            
            
            
            static int TEMP_INPUTPORT_SIZE=100;//TODO i don't like that but how else?
            static NewInputPort[] temp_inputport = new NewInputPort[TEMP_INPUTPORT_SIZE]; 
            static int inputport_curpos=0;
            static NewInputPort[] inputport_macro=null;
            public static void PORT_START() 
            {
                //(int mask,int default_value,int type,String name,int Keyboard,int joystick,int arg)
                //{ 0, 0, IPT_PORT, 0, 0, 0, 0 }
                temp_inputport[inputport_curpos]= new NewInputPort(0,0,IPT_PORT,null,0,0,0);
                inputport_curpos++;
            }
            public static void PORT_BIT(int mask,int default_value,int type)
            {
                //mask, default, type, IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 0
                temp_inputport[inputport_curpos]= new NewInputPort(mask,default_value,type,IP_NAME_DEFAULT,IP_KEY_DEFAULT,IP_JOY_DEFAULT,0);
                inputport_curpos++;
            }
            public static void PORT_DIPNAME(int mask,int default_value,String name,int key)
            {
                //mask, default, IPT_DIPSWITCH_NAME, name, key, IP_JOY_NONE, 0
                temp_inputport[inputport_curpos]= new NewInputPort(mask,default_value,IPT_DIPSWITCH_NAME, name, key, IP_JOY_NONE, 0);
                inputport_curpos++;
            }
            public static void PORT_DIPSETTING(int default_value,String name)
            {
                //{ -1, default, IPT_DIPSWITCH_SETTING, name, IP_KEY_NONE, IP_JOY_NONE, 0 },
                temp_inputport[inputport_curpos]= new NewInputPort(-1, default_value, IPT_DIPSWITCH_SETTING, name, IP_KEY_NONE, IP_JOY_NONE, 0);
                inputport_curpos++;
            }
            public static void PORT_BITX(int mask,int default_value,int type,String name,int key,int joy,int arg)
            {
                //mask, default, type, name, key, joy, arg
                temp_inputport[inputport_curpos]= new NewInputPort(mask, default_value, type, name, key, joy, arg);
                inputport_curpos++;
            }
            /* LBO - analog input */
            public static void PORT_ANALOG(int mask,int default_value,int type,int sensitivity,int clip,int min,int max)
            {
                //mask, default, type, IP_NAME_DEFAULT, 0, 0, IPF_SENSITIVITY(sensitivity) | IPF_CLIP(clip) | IPF_MIN(min) | IPF_MAX(max)
                temp_inputport[inputport_curpos]= new NewInputPort(mask, default_value, type, IP_NAME_DEFAULT, 0, 0, IPF_SENSITIVITY(sensitivity) | IPF_CLIP(clip) | IPF_MIN(min) | IPF_MAX(max));
                inputport_curpos++;
            }
            public static void INPUT_PORTS_END()
            {
                //0, 0, IPT_END, 0, 0
                temp_inputport[inputport_curpos]= new NewInputPort(0, 0, IPT_END,null,0, 0, 0);
                inputport_curpos++;
                inputport_macro=null;
                inputport_macro=new NewInputPort[inputport_curpos];
                System.arraycopy(temp_inputport, 0, inputport_macro, 0, inputport_curpos);
                inputport_curpos=0;//reset curpos
                temp_inputport=null;
                temp_inputport=new NewInputPort[TEMP_INPUTPORT_SIZE];//reset tempmodule
            }
            
            
              /* start of table */
            //#define INPUT_PORTS_START(name) static struct NewInputPort name[] = {
            /* end of table */
            //#define INPUT_PORTS_END { 0, 0, IPT_END, 0, 0 } };
            /* start of a new input port */
            //#define PORT_START { 0, 0, IPT_PORT, 0, 0, 0, 0 },
            /* input bit definition */
            //#define PORT_BIT(mask,default,type) { mask, default, type, IP_NAME_DEFAULT, IP_KEY_DEFAULT, IP_JOY_DEFAULT, 0 },
            /* input bit definition with extended fields */
            //#define PORT_BITX(mask,default,type,name,key,joy,arg) { mask, default, type, name, key, joy, arg },
            /* analog input */
            //#define PORT_ANALOG(mask,default,type,sensitivity) { mask, default, type, IP_NAME_DEFAULT, 0, 0, sensitivity },
            /* dip switch definition */
           // #define PORT_DIPNAME(mask,default,name,key) { mask, default, IPT_DIPSWITCH_NAME, name, key, IP_JOY_NONE, 0 },
           // #define PORT_DIPSETTING(default,name) { -1, default, IPT_DIPSWITCH_SETTING, name, IP_KEY_NONE, IP_JOY_NONE, 0 },

    
     /*
     *  The idea behing the trak-ball support is to have the system dependent
     *  handler return a positive or negative number representing the distance
     *  from the mouse region origin.  Then depending on what the emulation needs,
     *  it will do a conversion on the distance from the origin into the desired
     *  format.  Why was it implemented this way?  Take Reactor and Crystal Castles
     *  as an example.  Reactor's inputs range from -127 to 127 where < 0 is
     *  left/down and >0 is right/up.  However, Crystal Castles just looks at the
     *  difference between successive unsigned char values.  So their inputs are
     *  quite different.
     */
       public static class TrakPort
      {
        public int axis;/* The axis of the trak-ball */
        public int centered;/* Does it return to the "zero" position after a read? */
        public double scale;/* How much do we scale the value by? */
        public ConversionPtr conversion;/* Function to do the conversion to what the game needs */

        public TrakPort(int axis, int centered, double scale, ConversionPtr conversion) { this.axis = axis; this.centered = centered; this.scale = scale; this.conversion = conversion; }
        public TrakPort(int paramInt) { this(-1, 0, 0.0D, null); }
      }
      /* Key setting definition */
      public static class KEYSet
      {
        public int num;/* input port affected */
                                  /* -1 terminates the array */
        public int mask; /* bit affected */
        public String name;/* name of the setting */

        public KEYSet(int num, int mask, String name){this.num = num; this.mask = mask; this.name = name; }
        public KEYSet(int paramInt) { this(-1, 0, null); }
      }


     /* dipswitch setting definition */
      public static class DSW
      {
		public DSW(int n, int m, String na, String v[], int r) { num = n; mask = m; name = na; CopyArray(values, v); reverse = r; };
		public DSW(int n, int m, String na, String v[]) { this(n, m, na, v, 0); };
		public DSW(int n) { this(-1, 0, null, null, 0); };
		public int num;	/* input port affected */
						/* -1 terminates the array */
		public int mask;	/* bits affected */
		public String name;	/* name of the setting */
		public String values[] = new String[17];/* null terminated array of names for the values */
									/* the setting can have */
		public int reverse; 	/* set to 1 to display values in reverse order */
     };

     public static class GfxDecodeInfo
     {
		public GfxDecodeInfo(int mr, int s, GfxLayout g, int ccs, int tcc) { memory_region = mr; start = s; gfxlayout = g; color_codes_start = ccs; total_color_codes = tcc; };
		public GfxDecodeInfo(int s, GfxLayout g, int ccs, int tcc) { start = s; gfxlayout = g; color_codes_start = ccs; total_color_codes = tcc; };
		public GfxDecodeInfo(int s) { this(s, s, null, 0, 0); }
		public int memory_region;	/* memory region where the data resides (usually 1) */
						/* -1 marks the end of the array */
		public int start;	/* beginning of data data to decode (offset in RAM[]) */
		public GfxLayout gfxlayout;
		public int color_codes_start;	/* offset in the color lookup table where color codes start */
		public int total_color_codes;	/* total number of color codes */
     };

     public static class MachineCPU
     {
		public MachineCPU(int ct, int cc, int mrg, MemoryReadAddress []mr, MemoryWriteAddress []mw, IOReadPort []pr, IOWritePort []pw, InterruptPtr in, int ipf)
		{
			cpu_type = ct; cpu_clock = cc; memory_region = mrg;
			memory_read = mr; memory_write = mw; port_read = pr; port_write = pw; interrupt = in; interrupts_per_frame = ipf;
		};

		public MachineCPU()
		{ this(0, 0, 0, null, null, null, null, null, 0); }

		public static MachineCPU[] create(int n)
		{ MachineCPU []a = new MachineCPU[n]; for(int k = 0; k < n; k++) a[k] = new MachineCPU(); return a; }


		public int cpu_type;	/* see #defines below. */
		public int cpu_clock;	/* in Hertz */
		public int memory_region;	/* number of the memory region (allocated by loadroms()) where */
							/* this CPU resides */
		public MemoryReadAddress []memory_read;
		public MemoryWriteAddress []memory_write;
		public IOReadPort []port_read;
		public IOWritePort []port_write;
		public InterruptPtr interrupt;
		public int interrupts_per_frame;	/* usually 1 */
      };
        public static final int CPU_Z80 = 1;
        public static final int CPU_M6502 = 2;
        public static final int CPU_I86 = 3;
        public static final int CPU_M6803=  4;
        public static final int CPU_M6808=  CPU_M6803;
        public static final int CPU_M6809=  5;
        public static final int CPU_M68000= 6;

	/* set this if the CPU is used as a slave for audio. It will not be emulated if */
	/* play_sound == 0, therefore speeding up a lot the emulation. */
	public static final int CPU_AUDIO_CPU = 0x8000;

	public static final int CPU_FLAGS_MASK = 0xff00;


	static final int MAX_CPU = 4;	/* MAX_CPU is the maximum number of CPUs which cpuintrf.c */
					/* can run at the same time. Currently, 4 is enough. */

        /* ASG 081897 -- added these flags for the video hardware */

        /* bit 0 of the video attributes indicates raster or vector video hardware */
        public static final int VIDEO_TYPE_RASTER=0x0000;
        public static final int VIDEO_TYPE_VECTOR=0x0001;

        /* bit 1 of the video attributes indicates whether or not dirty rectangles will work */
        public static final int VIDEO_SUPPORTS_DIRTY=0x0002;

        /* bit 2 of the video attributes indicates whether or not the driver modifies the palette */
        public static final int VIDEO_MODIFIES_PALETTE=	0x0004;
 	public static class MachineDriver
	{
		public MachineDriver(MachineCPU []mcp, int fps, InitMachinePtr im, int sw, int sh, rectangle va, GfxDecodeInfo []gdi, int tc, int ctl, VhConvertColorPromPtr vccp,int vattr, VhInitPtr vi, VhStartPtr vsta, VhStopPtr vsto, VhUpdatePtr vup, char []sa, ShInitPtr si, ShStartPtr ssta, ShStopPtr ssto, ShUpdatePtr sup)
		{
			CopyArray(cpu, mcp); frames_per_second = fps;	init_machine = im;
			screen_width = sw; screen_height = sh; visible_area = va; gfxdecodeinfo = gdi; total_colors = tc; color_table_len = ctl; vh_convert_color_prom = vccp;
			video_attributes=vattr; vh_init = vi; vh_start = vsta; vh_stop = vsto; vh_update = vup;
			samples = sa; sh_init = si; sh_start = ssta; sh_stop = ssto; sh_update = sup;
		}
                
                //new defination for 0.29 , all other drivers should be moved here...
		public MachineDriver(MachineCPU []mcp, int fps,int slices, InitMachinePtr im, int sw, int sh, rectangle va, GfxDecodeInfo []gdi, int tc, int ctl, VhConvertColorPromPtr vccp,int vattr, VhInitPtr vi, VhStartPtr vsta, VhStopPtr vsto, VhUpdatePtr vup, char []sa, ShInitPtr si, ShStartPtr ssta, ShStopPtr ssto, ShUpdatePtr sup)
		{
			CopyArray(cpu, mcp); 
                        frames_per_second = fps;
                        cpu_slices_per_frame=slices;
                        init_machine = im;
			screen_width = sw; screen_height = sh; visible_area = va; gfxdecodeinfo = gdi; total_colors = tc; color_table_len = ctl; vh_convert_color_prom = vccp;
			video_attributes=vattr; vh_init = vi; vh_start = vsta; vh_stop = vsto; vh_update = vup;
			samples = sa; sh_init = si; sh_start = ssta; sh_stop = ssto; sh_update = sup;
		}
		/* basic machine hardware */
		public MachineCPU cpu[] = MachineCPU.create(MAX_CPU);
		public int frames_per_second;
                public int cpu_slices_per_frame;	/* for multicpu games. 1 is the minimum, meaning */
								/* that each CPU runs for the whole video frame */
								/* before giving control to the others. The higher */
								/* this setting, the more closely CPUs are interleaved */
								/* and therefore the more accurate the emulation is. */
								/* However, an higher setting also means slower */
								/* performance. */
		public InitMachinePtr init_machine;

		/* video hardware */
		public int screen_width, screen_height;
		public rectangle visible_area;
		public GfxDecodeInfo []gfxdecodeinfo;
		public int total_colors;	/* palette is 3*total_colors bytes long */
		public int color_table_len;	/* length in bytes of the color lookup table */
		public VhConvertColorPromPtr vh_convert_color_prom;
                int video_attributes;

		public VhInitPtr vh_init;
		public VhStartPtr vh_start;
		public VhStopPtr vh_stop;
		public VhUpdatePtr vh_update;

		/* sound hardware */
		public char []samples;
		public ShInitPtr sh_init;
		public ShStartPtr sh_start;
		public ShStopPtr sh_stop;
		public ShUpdatePtr sh_update;
	};

        public static class GameDriver
	{
                public GameDriver(String description, String name, String credits, MachineDriver drv, RomLoadPtr load, DecodePtr rom_decode, DecodePtr opcode_decode, String[] samplenames, InputPort[] input_ports,InputPortPtr load_input_ports, TrakPort[] trak_ports, DSW[] dswsettings, KEYSet[] keysettings, char[] color_prom, char[] palette, char[] colortable, int orientation, HiscoreLoadPtr hiscore_load, HiscoreSavePtr hiscore_save)
                {
                     this.description = description; this.name = name; this.credits = credits; this.drv = drv;
                     load.handler();//load the rom
                     this.rom = rommodule_macro; //copy rommodule_macro to rom
                     this.rom_decode = rom_decode; this.opcode_decode = opcode_decode; this.samplenames = samplenames;
                     this.input_ports = input_ports;
                     if(load_input_ports!=null)
                     {
                        load_input_ports.handler();//load new input ports
                        this.new_input_ports=inputport_macro; //copy result from macro here
                     }
                     this.trak_ports = trak_ports; this.dswsettings = dswsettings; this.keysettings = keysettings;
                     this.color_prom = color_prom; this.palette = palette; this.colortable = colortable; this.orientation=orientation;
                     this.hiscore_load = hiscore_load; this.hiscore_save = hiscore_save;
                 }
		public String description;
		public String name;
                public String credits;
		public MachineDriver drv;

		public RomModule []rom;
		public DecodePtr rom_decode;	/* used to decrypt the ROMs after loading them */
		public DecodePtr opcode_decode;	/* used to decrypt the ROMs when the CPU fetches an opcode */
		public String []samplenames;	/* optional array of names of samples to load. */
							/* drivers can retrieve them in Machine->samples */

		public InputPort []input_ports;
                public NewInputPort[] new_input_ports;
                public TrakPort[] trak_ports;
		public DSW []dswsettings;
                public KEYSet[] keysettings;

				/* if they are available, provide a dump of the color proms (there is no */
				/* copyright infringement in that, since you can't copyright a color scheme) */
				/* and a function to convert them to a usable palette and colortable (the */
				/* function pointer is in the MachineDriver, not here) */
				/* Otherwise, leave this field null and provide palette and colortable. */

                public char []color_prom;
		public char []palette;
		public char []colortable;

		public int orientation;	/* orientation of the monitor; see defines below */
		public HiscoreLoadPtr hiscore_load;	/* will be called every vblank until it */
											/* returns nonzero */
		public HiscoreSavePtr hiscore_save;	/* will not be loaded if hiscore_load() hasn't yet */
										/* returned nonzero, to avoid saving an invalid table */
	};
        public static final int ORIENTATION_DEFAULT=		0x00;
        public static final int ORIENTATION_FLIP_X=		0x01;	/* mirror everything in the X direction */
        public static final int ORIENTATION_FLIP_Y=		0x02;	/* mirror everything in the Y direction */
        public static final int ORIENTATION_SWAP_XY=		0x04;	/* mirror along the top-left/bottom-rigth diagonal */
        public static final int ORIENTATION_ROTATE_90=	(ORIENTATION_SWAP_XY|ORIENTATION_FLIP_X);	/* rotate clockwise 90 degrees */
        public static final int ORIENTATION_ROTATE_180=	(ORIENTATION_FLIP_X|ORIENTATION_FLIP_Y);	/* rotate 180 degrees */
        public static final int ORIENTATION_ROTATE_270=	(ORIENTATION_SWAP_XY|ORIENTATION_FLIP_Y);	/* rotate counter-clockwise 90 degrees */
        /* IMPORTANT: to perform more than one transformation, DO NOT USE |, use ^ instead. */
        /* For example, to rotate 90 degrees counterclockwise and flip horizontally, use: */
        /* ORIENTATION_ROTATE_270 ^ ORIENTATION_FLIP_X */
        /* FLIP is performed *after* SWAP_XY. */

}
