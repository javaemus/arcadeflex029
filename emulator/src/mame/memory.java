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
 * ported to v0.29  - TODO STILL NOT WORKING PROPERLY AND NOT FINISHED
 *
 *
 *
 */

package mame;

import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.memoryH.*;
import static mame.mame.*;
import static mame.cpuintrf.*;

public class memory {

    public static char[] OP_RAM;
    public static char[] OP_ROM;
    public static char ophw;				/* op-code hardware number */

    public static char ramptr[][]=new char[MAX_CPU][];
    public static char romptr[][]=new char[MAX_CPU][];
    
    /* element shift bits, mask bits */
      static int[][] mhshift=new int[MAX_CPU][3];
      static int[][] mhmask=new int[MAX_CPU][3];
    /* current hardware element map */
      //static char cur_mr_element[][]=new char[MAX_CPU][];
      //static char cur_mw_element[][]=new char[MAX_CPU][];

      public static CharPtr[] cur_mr_element=new CharPtr[MAX_CPU];
      public static CharPtr[] cur_mw_element=new CharPtr[MAX_CPU];

    /* bank ram base address */
      //public static char cpu_bankbase[][]=new char[6][];
      public static CharPtr[] cpu_bankbase=new CharPtr[6];
      public static int[] cpu_bankoffset=new int[6];	

    /* sub memory/port hardware element map */
    //static char[] readhardware =new char[MH_ELEMAX<<MH_SBITS];  /* mem/port read  */
    //static char[] writehardware=new char[MH_ELEMAX<<MH_SBITS]; /* mem/port write */
      static CharPtr readhardware = new CharPtr(new char[MH_ELEMAX<<MH_SBITS]);
      static CharPtr writehardware = new CharPtr(new char[MH_ELEMAX<<MH_SBITS]);
    /* memory hardware element map */
    /* value:                      */
    public static final int HT_RAM   = 0;		/* RAM direct        */
    public static final int HT_BANK1 = 1;		/* bank memory #1    */
    public static final int HT_BANK2 = 2;		/* bank memory #2    */
    public static final int HT_BANK3 = 3;		/* bank memory #3    */
    public static final int HT_BANK4 = 4;		/* bank memory #4    */
    public static final int HT_BANK5 = 5;		/* bank memory #5    */
    public static final int HT_NON   = 6;		/* non mapped memory */
    public static final int HT_NOP   = 7;		/* NOP memory        */
    public static final int HT_RAMROM= 8;		/* RAM ROM memory    */
    public static final int HT_ROM   = 9;		/* ROM memory        */

    public static final int HT_USER=   10;	/* user functions */
    /* [MH_HARDMAX]-0xff	   link to sub memory element  */
    /*                         (value-MH_HARDMAX)<<MH_SBITS -> element bank */
    
    /* memory hardware handler */
    static ReadHandlerPtr[] memoryreadhandler = new ReadHandlerPtr[MH_HARDMAX];
    static int[] memoryreadoffset = new int[MH_HARDMAX];
    static WriteHandlerPtr[] memorywritehandler = new WriteHandlerPtr[MH_HARDMAX];
    static int[] memorywriteoffset = new int[MH_HARDMAX];
    
    /* current cpu current hardware element map point */
    public static CharPtr cur_mrhard;
    public static CharPtr cur_mwhard;
    
    /***************************************************************************

      Memory handling

    ***************************************************************************/
    public static ReadHandlerPtr mrh_bank1= new ReadHandlerPtr() { public int handler(int address) {return cpu_bankbase[1].read(address);}};
    public static ReadHandlerPtr mrh_bank2= new ReadHandlerPtr() { public int handler(int address) {return cpu_bankbase[2].read(address);}};
    public static ReadHandlerPtr mrh_bank3= new ReadHandlerPtr() { public int handler(int address) {return cpu_bankbase[3].read(address);}};
    public static ReadHandlerPtr mrh_bank4= new ReadHandlerPtr() { public int handler(int address) {return cpu_bankbase[4].read(address);}};
    public static ReadHandlerPtr mrh_bank5= new ReadHandlerPtr() { public int handler(int address) {return cpu_bankbase[5].read(address);}};

    public static ReadHandlerPtr mrh_error= new ReadHandlerPtr() { public int handler(int address) 
    {
            if (errorlog!=null) fprintf(errorlog,"CPU #%d PC %04x: warning - read unmapped memory address %04x\n",cpu_getactivecpu(),cpu_getpc(),address);
            return RAM[address];
    }};
    public static ReadHandlerPtr mrh_nop= new ReadHandlerPtr() { public int handler(int address) 
    {
            return 0;
    }};
   
    public static WriteHandlerPtr mwh_bank1= new WriteHandlerPtr() { public void handler(int address,int data){cpu_bankbase[1].write(address,data);}};
    public static WriteHandlerPtr mwh_bank2= new WriteHandlerPtr() { public void handler(int address,int data){cpu_bankbase[2].write(address,data);}};
    public static WriteHandlerPtr mwh_bank3= new WriteHandlerPtr() { public void handler(int address,int data){cpu_bankbase[3].write(address,data);}};
    public static WriteHandlerPtr mwh_bank4= new WriteHandlerPtr() { public void handler(int address,int data){cpu_bankbase[4].write(address,data);}};
    public static WriteHandlerPtr mwh_bank5= new WriteHandlerPtr() { public void handler(int address,int data){cpu_bankbase[5].write(address,data);}};

    public static WriteHandlerPtr mwh_error= new WriteHandlerPtr() { public void handler(int address,int data)
    {
           if (errorlog!=null) fprintf(errorlog,"CPU #%d PC %04x: warning - write %02x to unmapped memory address %04x\n",cpu_getactivecpu(),cpu_getpc(),data,address);
            RAM[address] = (char)data;
    }};
    public static WriteHandlerPtr mwh_rom= new WriteHandlerPtr() { public void handler(int address,int data)
    {
            if (errorlog!=null) fprintf(errorlog,"CPU #%d PC %04x: warning - write %02x to ROM address %04x\n",cpu_getactivecpu(),cpu_getpc(),data,address);
    }};
    public static WriteHandlerPtr mwh_ramrom= new WriteHandlerPtr() { public void handler(int address,int data)
    {
            RAM[address] = ROM[address] = (char)data;
    }};
    public static WriteHandlerPtr mwh_nop= new WriteHandlerPtr() { public void handler(int address,int data)
    {
    }};
    static void mem_dump()
    {
            int cpu;
            int naddr,addr;
            char nhw,hw;

            if (errorlog==null) return;

            for( cpu = 0 ; cpu < totalcpu ; cpu++ )
            {
                    fprintf(errorlog,"cpu %d read memory \n",cpu);
                    addr = 0;
                    naddr = 0;
                    nhw = 0xff;
                    while( (addr >> mhshift[cpu][0]) <= mhmask[cpu][0] ){
                            hw = cur_mr_element[cpu].read(addr >> mhshift[cpu][0]);
                            if( hw >= MH_HARDMAX )
                            {	/* 2nd element link */
                                    hw = readhardware.read(((hw-MH_HARDMAX)<<MH_SBITS) + ((addr>>mhshift[cpu][1]) & mhmask[cpu][1]));
                                    if( hw >= MH_HARDMAX )
                                            hw = readhardware.read(((hw-MH_HARDMAX)<<MH_SBITS) + (addr & mhmask[cpu][2]));
                            }
                            if( nhw != hw )
                            {
                                    if( addr!=0 )
                                        fprintf(errorlog,"  %06x(%06x) - %06x = %02x\n",new Object[] {naddr,memoryreadoffset[nhw],addr-1,Integer.valueOf(nhw)});
                                    nhw = hw;
                                    naddr = addr;
                            }
                            addr++;
                    }
                    fprintf(errorlog,"  %06x(%06x) - %06x = %02x\n",naddr,memoryreadoffset[nhw],addr-1,Integer.valueOf(nhw));

                    fprintf(errorlog,"cpu %d write memory \n",cpu);
                    naddr = 0;
                    addr = 0;
                    nhw = 0xff;
                    while( (addr >> mhshift[cpu][0]) <= mhmask[cpu][0] ){
                            hw = cur_mw_element[cpu].read(addr >> mhshift[cpu][0]);
                            if( hw >= MH_HARDMAX )
                            {	/* 2nd element link */
                                    hw = writehardware.read(((hw-MH_HARDMAX)<<MH_SBITS) + ((addr>>mhshift[cpu][1]) & mhmask[cpu][1]));
                                    if( hw >= MH_HARDMAX )
                                            hw = writehardware.read(((hw-MH_HARDMAX)<<MH_SBITS) + (addr & mhmask[cpu][2]));
                            }
                            if( nhw != hw )
                            {
                                    if( addr!=0 )
                                     fprintf(errorlog,"  %06x(%06x) - %06x = %02x\n",naddr,memorywriteoffset[nhw],addr-1,Integer.valueOf(nhw));
                                    nhw = hw;
                                    naddr = addr;
                            }
                            addr++;
                    }
                fprintf(errorlog,"  %06x(%06x) - %06x = %02x\n",naddr,memorywriteoffset[nhw],addr-1,Integer.valueOf(nhw));
            }
    }
    /* return element offset */
    static char[] get_element( char []element , int ad , int elemask ,char[] subelement , int ele_max )
    {
            char hw = element[ad];
            int i,ele;
            int banks = ( elemask / (1<<MH_SBITS) ) + 1;

            int temp_elemax=0;
            if( hw >= MH_HARDMAX ) 
            {
                //return &subelement[(hw-MH_HARDMAX)<<MH_SBITS];
                char[] temp = new char[subelement.length];
                int offset1 = (hw-MH_HARDMAX)<<MH_SBITS;
                int sizez = subelement.length - ((hw-MH_HARDMAX)<<MH_SBITS);
                //System.out.println(offset1);
                //System.out.println(sizez);
                System.arraycopy(subelement, offset1, temp, 0, sizez);
                System.arraycopy(temp, 0, subelement, 0, temp.length);//pfff subelement=temp not working here
                return subelement;
                
                
            }
            //shadow code :find ele_max variable
            if(ele_max==1)//read
            {
                temp_elemax=rdelement_max;
            }
            if(ele_max==2)//write
            {
                temp_elemax=wrelement_max;
            }
            /* create new element block */
            if(temp_elemax+banks > MH_ELEMAX)
            //if( (*ele_max)+banks > MH_ELEMAX )
            {
                    if (errorlog!=null) fprintf(errorlog,"memory element size over \n");
                    return null;
            }
            /* get new element nunber */
  /*          ele = *ele_max;
            (*ele_max)+=banks;*/
            ele= temp_elemax;
            if(ele_max==1)//read
            {
                rdelement_max+=banks;
            }
            if(ele_max==2)//write
            {
                wrelement_max+=banks;
            }
            
           
            if (errorlog!=null) fprintf(errorlog,"create element %2d(%2d)\n",ele,banks);

            /* set link mark to current element */
            element[ad] = (char)(ele + MH_HARDMAX);
        
            /* get next subelement top */
           // subelement  = &subelement[ele<<MH_SBITS];*/
            char[] temp = new char[subelement.length];
            int offset1 = ele<<MH_SBITS;
            int sizez = subelement.length - (ele<<MH_SBITS);
            System.arraycopy(subelement, offset1, temp, 0, sizez);
            System.arraycopy(temp, 0, subelement, 0, temp.length);//pfff subelement=temp not working here

             
            /* initialize new block */
            for( i = 0 ; i < (1<<MH_SBITS) ; i++ )
                    subelement[i] = hw;

            return subelement;
    }
    /* return element offset */
    static CharPtr get_element( CharPtr element , int ad , int elemask ,CharPtr subelement , int ele_max )
    {
            char hw = element.read(ad);
            int i,ele;
            int banks = ( elemask / (1<<MH_SBITS) ) + 1;

            int temp_elemax=0;
            if( hw >= MH_HARDMAX ) 
            {
                subelement.set(subelement.getSubArray((hw-MH_HARDMAX)<<MH_SBITS,subelement.memory.length-1), 0);
                return subelement;
                //return &subelement[(hw-MH_HARDMAX)<<MH_SBITS];
                //return new CharPtr(subelement.getSubArray((hw-MH_HARDMAX)<<MH_SBITS, subelement.memory.length-1),0);
                
            }
            //shadow code :find ele_max variable
            if(ele_max==1)//read
            {
                temp_elemax=rdelement_max;
            }
            if(ele_max==2)//write
            {
                temp_elemax=wrelement_max;
            }
            /* create new element block */
            if(temp_elemax+banks > MH_ELEMAX)
            //if( (*ele_max)+banks > MH_ELEMAX )
            {
                    if (errorlog!=null) fprintf(errorlog,"memory element size over \n");
                    return null;
            }
            /* get new element nunber */
  /*          ele = *ele_max;
            (*ele_max)+=banks;*/
            ele= temp_elemax;
            if(ele_max==1)//read
            {
                rdelement_max+=banks;
            }
            if(ele_max==2)//write
            {
                wrelement_max+=banks;
            }
            
           
            if (errorlog!=null) fprintf(errorlog,"create element %2d(%2d)\n",ele,banks);

            /* set link mark to current element */
            element.write(ad,ele + MH_HARDMAX);
        
            /* get next subelement top */
           // subelement  = &subelement[ele<<MH_SBITS];*/
            subelement.set(subelement.getSubArray(ele<<MH_SBITS,subelement.memory.length-1), 0);
             
            /* initialize new block */
            for( i = 0 ; i < (1<<MH_SBITS) ; i++ )
                    subelement.write(i,hw);

            return subelement;
    }
    static void set_element( int cpu , CharPtr celement , int sp , int ep , int type , CharPtr subelement , int ele_max )
    {
	int i;
	int edepth = 0;
	int shift,mask;
	CharPtr eele = celement;//new CharPtr(celement,0);
	CharPtr sele = celement;//new CharPtr(celement,0);
	CharPtr ele= new CharPtr();
	int ss,sb,eb,ee;
	if (errorlog!=null) fprintf(errorlog,"set_element %6X-%6X = %2X\n",sp,ep,type);

	if( sp > ep ) return;
	do{
		mask  = mhmask[cpu][edepth];
		shift = mhshift[cpu][edepth];
                //System.out.println(mask);
                //System.out.println(shift);
		/* center element */
		ss = sp >> shift;
		sb = sp!=0 ? ((sp-1) >> shift) + 1 : 0;
		eb = ((ep+1) >> shift) - 1;
		ee = ep >> shift;

		if( sb <= eb )
		{
			if( (sb|mask)==(eb|mask) )
			{
				/* same reasion */
				ele = (sele!=null ? sele : eele);
				for( i = sb ; i <= eb ; i++ ){
				 	ele.write(i & mask,type);
				}
			}
			else
			{
				if( sele!=null ) for( i = sb ; i <= (sb|mask) ; i++ )
				 	sele.write(i & mask,type);
				if( eele!=null ) for( i = eb&(~mask) ; i <= eb ; i++ )
				 	eele.write(i & mask,type);
			}
		}

		edepth++;

		if( ss == sb ) sele = null;
		else sele = get_element( sele , ss & mask, mhmask[cpu][edepth] ,
									subelement , ele_max );
		if( ee == eb ) eele = null;
		else eele = get_element( eele , ee & mask , mhmask[cpu][edepth] ,
									subelement , ele_max );

	}while( sele!=null || eele!=null );
    }
    /* ASG 971007 added to allocate cur_m*_element() dynamic */
    /* return = FALSE:can't allocate element memory */
    
    //shadow static ints so we can store/save values on it
    static int rdelement_max = 0;
    static int wrelement_max = 0;
    public static int initmemoryhandlers()
    {
            int i, cpu;
            MemoryReadAddress[] memoryread;
            MemoryWriteAddress[] memorywrite;
            MemoryReadAddress[] mra;
            MemoryWriteAddress[] mwa;
            //int rdelement_max = 0;
           // int wrelement_max = 0;
            int rdhard_max = HT_USER;
            int wrhard_max = HT_USER;
            int hardware;
            int abits1,abits2,abits3,abitsmin;

            //shadow code intialaze ptrs
            for(int k=0; k<6; k++)
                cpu_bankbase[k]=new CharPtr();
            
     
            //endof shadow code
            for( cpu = 0 ; cpu < MAX_CPU ; cpu++ )
            {
                    cur_mr_element[cpu] = null;
                    cur_mw_element[cpu] = null;
            }

            for( cpu = 0 ; cpu < cpu_gettotalcpu() ; cpu++ )
            {
                    MemoryReadAddress[] mra1=Machine.drv.cpu[cpu].memory_read;
                    MemoryWriteAddress[] mwa1=Machine.drv.cpu[cpu].memory_write;


                    ramptr[cpu] = Machine.memory_region[Machine.drv.cpu[cpu].memory_region];

                    /* opcode decryption is currently supported only for the first memory region */
                    if (cpu == 0) romptr[cpu] = ROM;
                    else romptr[cpu] = ramptr[cpu];


                    /* initialize the memory base pointers for memory hooks */
                    //mra1 = Machine.drv.cpu[cpu].memory_read;
                    int mra1_ptr=0;
                    while (mra1[mra1_ptr].start != -1)
                    {
                            if (mra1[mra1_ptr].base!=null)
                                mra1[mra1_ptr].base.set(ramptr[cpu], mra1[mra1_ptr].start);
                            
                            if (mra1[mra1_ptr].size!=null) 
                                mra1[mra1_ptr].size[0] = mra1[mra1_ptr].end - mra1[mra1_ptr].start + 1;
                            mra1_ptr++;
                    }
                   // mwa1 = Machine.drv.cpu[cpu].memory_write;
                    int mwa1_ptr=0;
                    while (mwa1[mwa1_ptr].start != -1)
                    {
                            if (mwa1[mwa1_ptr].base!=null) 
                                mwa1[mwa1_ptr].base.set(ramptr[cpu],mwa1[mwa1_ptr].start);
                            if (mwa1[mwa1_ptr].size!=null) 
                                mwa1[mwa1_ptr].size[0] =mwa1[mwa1_ptr].end - mwa1[mwa1_ptr].start + 1;
                             mwa1_ptr++;
                    }
            }
            /* initialize grobal handler */
            for( i = 0 ; i < MH_HARDMAX ; i++ ){
                    memoryreadoffset[i] = 0;
                    memorywriteoffset[i] = 0;
            }
            /* bank1 memory */
            memoryreadhandler[HT_BANK1] = mrh_bank1;
            memorywritehandler[HT_BANK1] = mwh_bank1;
            /* bank2 memory */
            memoryreadhandler[HT_BANK2] = mrh_bank2;
            memorywritehandler[HT_BANK2] = mwh_bank2;
            /* bank3 memory */
            memoryreadhandler[HT_BANK3] = mrh_bank3;
            memorywritehandler[HT_BANK3] = mwh_bank3;
            /* bank4 memory */
            memoryreadhandler[HT_BANK4] = mrh_bank4;
            memorywritehandler[HT_BANK4] = mwh_bank4;
            /* bank5 memory */
            memoryreadhandler[HT_BANK5] = mrh_bank5;
            memorywritehandler[HT_BANK5] = mwh_bank5;
            /* non map mamery */
            memoryreadhandler[HT_NON] = mrh_error;
            memorywritehandler[HT_NON] = mwh_error;
            /* NOP memory */
            memoryreadhandler[HT_NOP] = mrh_nop;
            memorywritehandler[HT_NOP] = mwh_nop;
            /* RAMROM memory */
            memorywritehandler[HT_RAMROM] = mwh_ramrom;
            /* ROM memory */
            memorywritehandler[HT_ROM] = mwh_rom;

            for( cpu = 0 ; cpu < cpu_gettotalcpu() ; cpu++ )
            {
                    /* cpu selection */
                    switch(Machine.drv.cpu[cpu].cpu_type & ~CPU_FLAGS_MASK){
                    case CPU_I86:
                            abits1 = ABITS1_20;
                            abits2 = ABITS2_20;
                            abits3 = ABITS3_20;
                            abitsmin = ABITS_MIN_20;
                            break;
                    case CPU_M68000:
                            abits1 = ABITS1_24;
                            abits2 = ABITS2_24;
                            abits3 = ABITS3_24;
                            abitsmin = ABITS_MIN_24;
                            break;
                    default:
                            abits1 = ABITS1_16;
                            abits2 = ABITS2_16;
                            abits3 = ABITS3_16;
                            abitsmin = ABITS_MIN_16;
                            break;
                    }
                    /* ASG 971005 -- changed to use the macros in memory.h */
                    /* element shifter , mask set */
                    mhshift[cpu][0] = (abits2+abits3);
                    mhshift[cpu][1] = abits3;			/* 2nd */
                    mhshift[cpu][2] = 0;				/* 3rd (used by set_element)*/
                   // System.out.println( mhshift[cpu][0]);
                    //System.out.println( mhshift[cpu][1]);
                    //System.out.println( mhshift[cpu][2]);
                    mhmask[cpu][0]  = MHMASK(abits1);		/*1st(used by set_element)*/
                    mhmask[cpu][1]  = MHMASK(abits2);		/*2nd*/
                    mhmask[cpu][2]  = MHMASK(abits3)==-1? 0 : MHMASK(abits3);/*3rd*/         //TODO : ugly hack but why it returns -1 ??
                   // System.out.println(mhmask[cpu][0]);
                    //System.out.println(mhmask[cpu][1]);
                    //System.out.println(mhmask[cpu][2]);
                    /* allocate current element */
                    //if( (cur_mr_element[cpu] = new char[1<<abits1]) == null ) //sizeof(unsigned char)=1 (correct?)
                    if( (cur_mr_element[cpu] = new CharPtr(new char[1<<abits1])) == null )
                    {
                          //  shutdownmemoryhandler();//TODO
                            return 0;
                    }
                    //if( (cur_mw_element[cpu] = new char[1<<abits1]) == null )
                    if( (cur_mw_element[cpu] = new CharPtr(new char[1<<abits1])) == null )
                    {
                          //  shutdownmemoryhandler();//TODO
                            return 0;
                    }

                    /* initialize curent element table */
                    for( i = 0 ; i < (1<<abits1) ; i++ )
                    {
                            cur_mr_element[cpu].write(i,HT_NON);	/* no map memory */
                            cur_mw_element[cpu].write(i,HT_NON);	/* no map memory */
                    }

                    memoryread = Machine.drv.cpu[cpu].memory_read;
                    memorywrite = Machine.drv.cpu[cpu].memory_write;

                    /* memory read handler build */
                    mra = memoryread;
                    int mra_ptr=0;
                    while (mra[mra_ptr].start != -1) mra_ptr++;
                    mra_ptr--;

                    while (mra_ptr>=0) //(mra >= memoryread)
                    {
                           // int (*handler)() = mra->handler;
                            int handler = mra[mra_ptr].handler;
                            switch( (int)handler )
                            {
                            case (int)MRA_RAM:
                            case (int)MRA_ROM:
                                    hardware = HT_RAM;	/* sprcial case ram read */
                                    break;
                            case (int)MRA_BANK1:
                                    hardware = HT_BANK1;
                                    cpu_bankoffset[1] = mra[mra_ptr].start; 
                                    cpu_bankbase[1].set(RAM,mra[mra_ptr].start); //TODO check if it is correct...	 
                                    break;
                            case (int)MRA_BANK2:
                                    hardware = HT_BANK2;
                                    cpu_bankoffset[2] = mra[mra_ptr].start; 
                                    cpu_bankbase[2].set(RAM,mra[mra_ptr].start); //TODO check if it is correct...	 
                                    break;
                            case (int)MRA_BANK3:
                                    hardware = HT_BANK3;
                                    cpu_bankoffset[3] = mra[mra_ptr].start; 
                                    cpu_bankbase[3].set(RAM,mra[mra_ptr].start); //TODO check if it is correct...		 
                                    break;
                            case (int)MRA_BANK4:
                                    hardware = HT_BANK4;
                                    cpu_bankoffset[4] = mra[mra_ptr].start; 
                                    cpu_bankbase[4].set(RAM,mra[mra_ptr].start); //TODO check if it is correct...	
                                    break;
                            case (int)MRA_BANK5:
                                    hardware = HT_BANK5;
                                    cpu_bankoffset[5] = mra[mra_ptr].start; 
                                    cpu_bankbase[5].set(RAM,mra[mra_ptr].start); //TODO check if it is correct...	
                                    break;
                            case (int)MRA_NOP:
                                    hardware = HT_NOP;
                                    break;
                            default:
                                    /* create newer haraware handler */
                                    if( rdhard_max == MH_HARDMAX )
                                    {
                                            if (errorlog!=null)
                                             fprintf(errorlog,"read memory hardware pattern over !\n");
                                            hardware = 0;
                                    }
                                    else
                                    {
                                            /* regist hardware function */
                                            hardware = rdhard_max++;
                                            memoryreadhandler[hardware] = mra[mra_ptr]._handler;//handler;
                                            memoryreadoffset[hardware] = mra[mra_ptr].start;
                                    }
                            }
                            //fprintf(errorlog,"start_element %6X-%6X \n",mra[mra_ptr].start,mra[mra_ptr].end);
                            
                            /* hardware element table make */
                            set_element( cpu , cur_mr_element[cpu] ,
                                    mra[mra_ptr].start >> abitsmin , mra[mra_ptr].end >> abitsmin ,
                                    hardware , readhardware , 1 /*1 for read 2 for write*/ /*rdelement_max*/ );
                            mra_ptr--;
                    }

                    /* memory write handler build */
                    mwa = memorywrite;
                    int mwa_ptr=0;
                    while (mwa[mwa_ptr].start != -1) mwa_ptr++;
                    mwa_ptr--;

                    while (mwa_ptr>=0)//(mwa >= memorywrite)
                    {
                            int handler = mwa[mwa_ptr].handler;
                            switch( (int)handler )
                            {
                            case (int)MWA_RAM:
                                    hardware = HT_RAM;	/* sprcial case ram write */
                                    break;
                            case (int)MWA_BANK1:
                                    hardware = HT_BANK1;
                                    cpu_bankoffset[1] = mwa[mwa_ptr].start; 
                                    cpu_bankbase[1].set(RAM,mwa[mwa_ptr].start); //TODO check if it is correct...
                                    break;
                            case (int)MWA_BANK2:
                                    hardware = HT_BANK2;
                                    cpu_bankoffset[2] = mwa[mwa_ptr].start; 
                                    cpu_bankbase[2].set(RAM,mwa[mwa_ptr].start); //TODO check if it is correct...
                                    break;
                            case (int)MWA_BANK3:
                                    hardware = HT_BANK3;
                                    cpu_bankoffset[3] = mwa[mwa_ptr].start; 
                                    cpu_bankbase[3].set(RAM,mwa[mwa_ptr].start); //TODO check if it is correct...
                                    break;
                            case (int)MWA_BANK4:
                                    hardware = HT_BANK4;
                                    cpu_bankoffset[4] = mwa[mwa_ptr].start; 
                                    cpu_bankbase[4].set(RAM,mwa[mwa_ptr].start); //TODO check if it is correct...
                                    break;
                            case (int)MWA_BANK5:
                                    hardware = HT_BANK5;
                                    cpu_bankoffset[5] = mwa[mwa_ptr].start; 
                                    cpu_bankbase[5].set(RAM,mwa[mwa_ptr].start); //TODO check if it is correct...
                                    break;
                            case (int)MWA_NOP:
                                    hardware = HT_NOP;
                                    break;
                            case (int)MWA_RAMROM:
                                    hardware = HT_RAMROM;
                                    break;
                            case (int)MWA_ROM:
                                    hardware = HT_ROM;
                                    break;
                            default:
                                    /* create newer haraware handler */
                                    if( wrhard_max == MH_HARDMAX ){
                                            if (errorlog!=null)
                                             fprintf(errorlog,"write memory hardware pattern over !\n");
                                            hardware = 0;
                                    }else{
                                            /* regist hardware function */
                                            hardware = wrhard_max++;
                                            memorywritehandler[hardware] = mwa[mwa_ptr]._handler;
                                            memorywriteoffset[hardware]  = mwa[mwa_ptr].start;
                                    }
                            }
                            //fprintf(errorlog,"startw_element %6X-%6X \n",mwa[mwa_ptr].start,mwa[mwa_ptr].end);
                            
                            /* hardware element table make */
                            set_element( cpu , cur_mw_element[cpu] ,
                                    mwa[mwa_ptr].start >> abitsmin , mwa[mwa_ptr].end >> abitsmin ,
                                    hardware , writehardware , 2 /*1 for read 2 for write*/ /*wrelement_max*/ );//(MHELE *)writehardware , &wrelement_max );
                            mwa_ptr--;
                    }
            }

            if (errorlog!=null){
                    fprintf(errorlog,"used read  elements %d/%d , functions %d/%d\n"
                        ,rdelement_max,MH_ELEMAX , rdhard_max,MH_HARDMAX );
                    fprintf(errorlog,"used write elements %d/%d , functions %d/%d\n"
                        ,wrelement_max,MH_ELEMAX , wrhard_max,MH_HARDMAX );
            }
            mem_dump();
            return 1;	/* ok */
    }
    public static void memorycontextswap(int activecpu)
    {
            RAM = ramptr[activecpu];
            ROM = romptr[activecpu];

            cur_mrhard = cur_mr_element[activecpu];
            cur_mwhard = cur_mw_element[activecpu];

            /* op code memory pointer */
            ophw = HT_RAM;
            OP_RAM = RAM;
            OP_ROM = ROM;
    }

    /* ASG 971007 */
    public static void shutdownmemoryhandler()
    {
            int cpu;

            for( cpu = 0 ; cpu < MAX_CPU ; cpu++ )
            {
                    if( cur_mr_element[cpu] != null )
                    {
                            //free( cur_mr_element[cpu] );
                            cur_mr_element[cpu] = null;
                    }
                    if( cur_mw_element[cpu] != null )
                    {
                            //free( cur_mw_element[cpu] );
                            cur_mw_element[cpu] = null;
                    }
            }
    }

    public static void updatememorybase(int activecpu)
    {
            /* keep track of changes to RAM and ROM pointers (bank switching) */
            ramptr[activecpu] = RAM;
            romptr[activecpu] = ROM;
    }
    /***************************************************************************

      Perform a memory read. This function is called by the CPU emulation.

    ***************************************************************************/

    /* ASG 971005 -- broke into three versions, using constant shift values */
     public static int cpu_readmem16(int address)
    {
            int hw = cur_mrhard.read(address >> (ABITS2_16+ABITS_MIN_16));

            if( NOT(hw)!=0 ) return RAM[address];
            if( hw >= MH_HARDMAX )
            {	/* 2nd element link */
                    hw = readhardware.read(((hw-MH_HARDMAX)<<MH_SBITS) + ((address>>ABITS_MIN_16) & MHMASK(ABITS2_16)));
                    if( hw==0 ) return RAM[address];
            }
            return memoryreadhandler[hw].handler(address - memoryreadoffset[hw]);
    }
    
    /***************************************************************************

      Perform a memory write. This function is called by the CPU emulation.

    ***************************************************************************/

    /* ASG 971005 -- broke into three versions, using constant shift values */
    public static void cpu_writemem16(int address,int data)
    {
            int hw = cur_mwhard.read(address >> (ABITS2_16+ABITS_MIN_16));

            if( NOT(hw)!=0 )
            {
                    RAM[address] = (char)data;
                    return;
            }
            if( hw >= MH_HARDMAX )
            {	/* 2nd element link */
                    hw = writehardware.read(((hw-MH_HARDMAX)<<MH_SBITS) + ((address>>ABITS_MIN_16) & MHMASK(ABITS2_16)));
                    if( hw==0 )
                    {
                            RAM[address] = (char)data;
                            return;
                    }
            }
            memorywritehandler[hw].handler(address - memorywriteoffset[hw],data);
    }
    /***************************************************************************

      Perform an I/O port read. This function is called by the CPU emulation.

    ***************************************************************************/
    public static int cpu_readport(int Port)
    {
            Port &= 0xff;	/* we can't handle 16 bit adresses yet */
            IOReadPort[] iorp = Machine.drv.cpu[cpu_getactivecpu()].port_read;
            if (iorp != null)
            {
                    int i = 0;
                    while (iorp[i].start != -1)
                    {
                            if ((Port >= iorp[i].start) && (Port <= iorp[i].end))     
                            {
                                     int j = iorp[i].handler;

                                    if (j == IOWP_NOP) return 0;
                                    return iorp[i]._handler.handler(Port - iorp[i].start);

                            }
                            i++;
                    }
            }

            if (errorlog!=null ) fprintf(errorlog,"CPU #%d PC %04x: warning - read unmapped I/O port %02x\n",cpu_getactivecpu(),cpu_getpc(),Port);
            return 0;
    }



    /***************************************************************************

      Perform an I/O port write. This function is called by the CPU emulation.

    ***************************************************************************/
    public static void cpu_writeport(int Port,int Value)
    {

            Port &= 0xff;	/* we can't handle 16 bit adresses yet */
            IOWritePort[] iowp = Machine.drv.cpu[cpu_getactivecpu()].port_write;
            int i=0;
            if (iowp != null)
            {
              while (iowp[i].start != -1)
              {
                if ((Port >= iowp[i].start) && (Port <= iowp[i].end))
                {
                  int j = iowp[i].handler;

                  if (j == IOWP_NOP) return;
                  iowp[i]._handler.handler(Port - iowp[i].start, Value);

                  return;
                }

                i++;
              }
            }
            if (errorlog!=null) fprintf(errorlog,"CPU #%d PC %04x: warning - write %02x to unmapped I/O port %02x\n",cpu_getactivecpu(),cpu_getpc(),Value,Port);
    }
    
}
