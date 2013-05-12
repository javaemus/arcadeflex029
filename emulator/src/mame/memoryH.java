/*
 *
 * ported to v0.29  
 *
 *
 *
 */
package mame;
import static mame.osdependH.*;
import static mame.commonH.*;
import static arcadeflex.libc.*;
import static mame.inptport.*;
import static mame.driverH.*;
import static mame.memory.*;
/**
 *
 * @author shadow
 */
public class memoryH {
        /***************************************************************************
	Note that the memory hooks are not passed the actual memory address where
	the operation takes place, but the offset from the beginning of the block
	they are assigned to. This makes handling of mirror addresses easier, and
	makes the handlers a bit more "object oriented". If you handler needs to
	read/write the main memory area, provide a "base" pointer: it will be
	initialized by the main engine to point to the beginning of the memory block
	assigned to the handler. You may also provided a pointer to "size": it
        will be set to the length of the memory area processed by the handler.

	***************************************************************************/
        public static class MemoryReadAddress
	{
                public MemoryReadAddress(int s, int e, int h, CharPtr b, int[] size){ this.start = s; this.end = e; this.handler = h; this.base = b; this.size = size; }
                public MemoryReadAddress(int s, int e, ReadHandlerPtr rhp, CharPtr b, int[] size) { this.start = s; this.end = e; this.handler = 1; this._handler = rhp; this.base = b; this.size = size; }
		public MemoryReadAddress(int s, int e, int h, CharPtr b) { start = s; end = e; handler = h; base = b; };
		public MemoryReadAddress(int s, int e, int h) { this(s, e, h, null); };
		public MemoryReadAddress(int s, int e, ReadHandlerPtr rhp, CharPtr b) { start = s; end = e; handler = 1; _handler = rhp; base = b; };
		public MemoryReadAddress(int s, int e, ReadHandlerPtr rhp) { this(s, e, rhp, null); };
		public MemoryReadAddress(int s) { this(s, -1, null); };
		public int start,end;
		public int handler;
		public ReadHandlerPtr _handler;	/* see special values below */
		public CharPtr base;
                public int[] size;
	};

	public static final int MRA_NOP = 0;	/* don't care, return 0 */
	public static final int MRA_RAM = -1;	/* plain RAM location (return its contents) */
	public static final int MRA_ROM = -2;	/* plain ROM location (return its contents) */
        public static final int MRA_BANK1 =-10;  /* bank memory */
        public static final int MRA_BANK2 =-11;  /* bank memory */
        public static final int MRA_BANK3 =-12;  /* bank memory */
        public static final int MRA_BANK4 =-13;  /* bank memory */
        public static final int MRA_BANK5 =-14;  /* bank memory */

	public static class MemoryWriteAddress
	{
                public MemoryWriteAddress(int s, int e, int h, CharPtr b, int[] size){this.start = s; this.end = e; this.handler = h; this.base = b; this.size = size; }
		public MemoryWriteAddress(int s, int e, WriteHandlerPtr whp, CharPtr b, int[] size) { this.start = s; this.end = e; this.handler = 1; this._handler = whp; this.base = b; this.size = size; }
                public MemoryWriteAddress(int s, int e, int h, CharPtr b) { start = s; end = e; handler = h; base = b; };
		public MemoryWriteAddress(int s, int e, int h) { this(s, e, h, null); };
		public MemoryWriteAddress(int s, int e, WriteHandlerPtr whp, CharPtr b) { start = s; end = e; handler = 1; _handler = whp; base = b; };
		public MemoryWriteAddress(int s, int e, WriteHandlerPtr whp) { this(s, e, whp, null); };
		public MemoryWriteAddress(int s) { this(s, -1, null); };
		public int start,end;
		public int handler;
		public WriteHandlerPtr _handler;	/* see special values below */
		public CharPtr base;
                public int[] size;
	};

	public static final int MWA_NOP = 0;	/* do nothing */
	public static final int MWA_RAM = -1;	/* plain RAM location (store the value) */
	public static final int MWA_ROM = -2;	/* plain ROM location (do nothing) */
        /* RAM[] and ROM[] are usually the same, but they aren't if the CPU opcodes are */
        /* encrypted. In such a case, opcodes are fetched from ROM[], and arguments from */
        /* RAM[]. If the program dynamically creates code in RAM and executes it, it */
        /* won't work unless writes to RAM affects both RAM[] and ROM[]. */
         public static final int MWA_RAMROM = -3;
         public static final int  MWA_BANK1 =-10;  /* bank memory */
         public static final int  MWA_BANK2 =-11;  /* bank memory */
         public static final int  MWA_BANK3 =-12;  /* bank memory */
         public static final int  MWA_BANK4 =-13;  /* bank memory */
         public static final int  MWA_BANK5 =-14;  /* bank memory */

         	/***************************************************************************

	IN and OUT ports are handled like memory accesses, the hook template is the
	same so you can interchange them. Of course there is no 'base' pointer for
	IO ports.

	***************************************************************************/
	public static class IOReadPort
	{
		public IOReadPort(int s, int e, int h) { start = s; end = e; handler = h; };
		public IOReadPort(int s, int e, ReadHandlerPtr rhp) {  start = s; end = e; handler = 1; _handler = rhp; };
		public IOReadPort(int s) { this(s, -1, null); };
		public int start,end;
		public int handler;
		public ReadHandlerPtr _handler;	/* see special values below */
	};

	public static final int IORP_NOP = 0;	/* don't care, return 0 */


	public static class IOWritePort
	{
		public IOWritePort(int s, int e, int h) { start = s; end = e; handler = h; };
		public IOWritePort(int s, int e, WriteHandlerPtr whp) {  start = s; end = e; handler = 1; _handler = whp; };
		public IOWritePort(int s) { this(s, -1, null); };
		public int start,end;
		public int handler;
		public WriteHandlerPtr _handler;	/* see special values below */
	};

	public static final int IOWP_NOP = 0;	/* do nothing */
    
        /* ASG 971005 -- moved into the header file */
        /* memory element block size */
        public static final int MH_SBITS=    8;			/* sub element bank size */
        public static final int MH_PBITS=   8;			/* port   current element size */
        public static final int MH_ELEMAX=  64;			/* sub elements       limit */
        public static final int MH_HARDMAX= 64;			/* hardware functions limit */

        /* ASG 971007 customize to elemet size period */
        /* 24 bits address */
        public static final int ABITS1_24=    12;
        public static final int ABITS2_24=    10;
        public static final int ABITS3_24=     0;
        public static final int ABITS_MIN_24=  2;      /* minimum memory block is 4 bytes */
        /* 20 bits address */
        public static final int ABITS1_20=    12;
        public static final int ABITS2_20=     8;
        public static final int ABITS3_20=     0;
        public static final int ABITS_MIN_20=  0;      /* minimum memory block is 1 byte */
        /* 16 bits address */
        public static final int ABITS1_16=    12;
        public static final int ABITS2_16=     4;
        public static final int ABITS3_16=     0;
        public static final int ABITS_MIN_16=  0;     /* minimum memory block is 1 byte */
        /* mask bits */
        public static int MHMASK(int abits) { return  (0xffffffff>>>(32-abits));}
        
        //TODO FUNCTIONS!!! 
        
        /* -----  bank memory function ----- */
        //#define cpu_setbank(B,A)  {cpu_bankbase[B]=(unsigned char *)(A)-cpu_bankoffset[B];if(ophw==B){ \
                               //ophw=0xff;cpu_setOPbase16(cpu_getpc());}}
        /* -----  op-code rompage select ----- */
        //#define cpu_setrombase(A) ROM=OP_ROM=(A)	/* ASG 971005 -- renamed to avoid confusion */

        /* ----- op-code reasion set function ----- */
        //#define change_pc(pc) {if(cur_mrhard[pc>>ABITS2_16]!=ophw)cpu_setOPbase16(pc);}


        public static int cpu_readop(int A) { return  OP_ROM[A];}
        public static int cpu_readop_arg(int A)	{ return OP_RAM[A];}
}
