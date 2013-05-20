
/*
 * 
 *   TODO change the memory functions to new format once memory is done
 */
package m6809;

import static mame.cpuintrf.*;
import static mame.mame.*;

public class M6809H
{
/****************************************************************************/
/* sizeof(byte)=1, sizeof(word)=2, sizeof(dword)>=4                         */
/****************************************************************************/   
          /* 6809 Registers */
          public static class m6809_Regs
          {
            public int pc;   /*word*/    /* Program counter */
            public int u,s;  /*word*/    /* Stack pointers */
            public int x,y;  /*word*/    /* Index registers */
            public int dp;   /*byte*/    /* Direct Page register */
            public int a,b;  /*byte*/    /* Accumulator */
            public int cc;
            
            public int pending_interrupts;
          }


          public static final int M6809_INT_NONE = 0;    /* No interrupt required */
          public static final int M6809_INT_IRQ = 1;     /* Standard IRQ interrupt */
          public static final int M6809_INT_FIRQ = 2;    /* Fast IRQ */
          public static final int M6809_INT_NMI =  4;	 /* NMI */
          /****************************************************************************/
        /* Flags for optimizing memory access. Game drivers should set m6809_Flags  */
        /* to a combination of these flags depending on what can be safely          */
        /* optimized. For example, if M6809_FAST_OP is set, opcodes are fetched     */
        /* directly from the ROM array, and cpu_readmem() is not called.            */
        /* The flags affect reads and writes.                                       */
        /****************************************************************************/
          public static final int M6809_FAST_NONE = 0;/* no memory optimizations */
          /*public static final int M6809_FAST_OP = 1;/* opcode fetching */
          public static final int M6809_FAST_S = 2;/* stack */
          public static final int M6809_FAST_U = 4;/* user stack */

          /****************************************************************************/
          /* Read a byte from given memory location                                   */
          /****************************************************************************/
          public static final int M6809_RDMEM(int A)
          {
            return cpu_readmem(A);
             //  #define M6809_RDMEM(A) ((unsigned)cpu_readmem16(A))
          }
            /****************************************************************************/
            /* Write a byte to given memory location                                    */
            /****************************************************************************/
          public static final void M6809_WRMEM(int A, int V)
          {
            cpu_writemem(A, (char)V);
            //(cpu_writemem16(A,V))
          }
        /****************************************************************************/
        /* Z80_RDOP() is identical to Z80_RDMEM() except it is used for reading     */
        /* opcodes. In case of system with memory mapped I/O, this function can be  */
        /* used to greatly speed up emulation                                       */
        /****************************************************************************/
          public static final int M6809_RDOP(int A)
          {
            return ROM[A];
            //((unsigned)cpu_readop(A))
          }
        /****************************************************************************/
        /* Z80_RDOP_ARG() is identical to Z80_RDOP() except it is used for reading  */
        /* opcode arguments. This difference can be used to support systems that    */
        /* use different encoding mechanisms for opcodes and opcode arguments       */
        /****************************************************************************/
          public static final int M6809_RDOP_ARG(int A)
          {
            return RAM[A];
            //((unsigned)cpu_readop_arg(A))
          }
}



