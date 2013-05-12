/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package M68000;

/**
 *
 * @author shadow
 */
public class mc68kmem {
    static int[] movem_index1=new int[256];
    static int[] movem_index2=new int[256];
    static int[] movem_next=new int[256];
    
   // UBYTE *actadr;

    //regstruct regs, lastint_regs;

    //union flagu intel_flag_lookup[256];
    //union flagu regflags;


    //#define MC68000_interrupt() (cpu_interrupt())
    //#define ReadMEM(A) (cpu_readmem(A))
    //#define WriteMEM(A,V) (cpu_writemem(A,V))

    static int icount=0;
    static int MC68000_IPeriod=0;

    static int InitStatus=0;

}
