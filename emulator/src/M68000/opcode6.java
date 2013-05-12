
package M68000;

import static M68000.cpudefsH.*;

public class opcode6 {

    static opcode_ptr op_6000= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(0)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6001= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(0)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_60ff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(0)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6100= new opcode_ptr() { public void handler(long opcode){ /* BSR */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
    {	regs.a[7] -= 4;
    {	CPTR spa = regs.a[7];
            put_long(spa,m68k_getpc());
            regs.pc = oldpc + (LONG)src;*/
    }};
    static opcode_ptr op_6101= new opcode_ptr() { public void handler(long opcode){ /* BSR */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
    {	regs.a[7] -= 4;
    {	CPTR spa = regs.a[7];
            put_long(spa,m68k_getpc());
            regs.pc = oldpc + (LONG)src;*/
    }};
    static opcode_ptr op_61ff= new opcode_ptr() { public void handler(long opcode){ /* BSR */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
    {	regs.a[7] -= 4;
    {	CPTR spa = regs.a[7];
            put_long(spa,m68k_getpc());
            regs.pc = oldpc + (LONG)src;*/
    }};
    static opcode_ptr op_6200= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(2)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6201= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(2)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_62ff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(2)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6300= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(3)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6301= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(3)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_63ff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(3)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6400= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(4)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6401= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(4)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_64ff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(4)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6500= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(5)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6501= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(5)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_65ff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(5)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6600= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(6)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6601= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(6)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_66ff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(6)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6700= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(7)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6701= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(7)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_67ff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(7)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6800= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(8)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6801= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(8)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_68ff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(8)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6900= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
   /* {
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(9)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6901= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(9)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_69ff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(9)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6a00= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(10)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6a01= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(10)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6aff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(10)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6b00= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(11)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6b01= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(11)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6bff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(11)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6c00= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(12)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6c01= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(12)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6cff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(12)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6d00= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
   /* {
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(13)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6d01= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(13)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6dff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(13)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6e00= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(14)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6e01= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(14)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6eff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(14)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6f00= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	WORD src = nextiword();
            if (cctrue(15)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6f01= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
            ULONG srcreg = (LONG)(BYTE)(opcode & 255);
    {	ULONG oldpc = regs.pc;
    {	ULONG src = srcreg;
            if (cctrue(15)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    static opcode_ptr op_6fff= new opcode_ptr() { public void handler(long opcode){ /* Bcc */
    /*{
    {	ULONG oldpc = regs.pc;
    {	LONG src = nextilong();
            if (cctrue(15)) {
            regs.pc = oldpc + (LONG)src;
            }*/
    }};
    
}
