
package M68000;

import static M68000.cpudefsH.*;

public class opcode5 {

    static opcode_ptr op_5000= new opcode_ptr() { public void handler(long opcode)  { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	BYTE dst = regs.d[dstreg];
    {{ULONG newv = ((BYTE)(dst)) + ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(~dst)) < ((UBYTE)(src));
            NFLG = flgn != 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((newv) & 0xff);*/
    }};
    static opcode_ptr op_5010= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) + ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(~dst)) < ((UBYTE)(src));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5018= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {	regs.a[dstreg] += areg_byteinc[dstreg];
    {{ULONG newv = ((BYTE)(dst)) + ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(~dst)) < ((UBYTE)(src));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5020= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) + ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(~dst)) < ((UBYTE)(src));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5028= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) + ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(~dst)) < ((UBYTE)(src));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5030= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) + ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(~dst)) < ((UBYTE)(src));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5038= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
    {{	ULONG src = srcreg;
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) + ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(~dst)) < ((UBYTE)(src));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5039= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
    {{	ULONG src = srcreg;
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) + ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(~dst)) < ((UBYTE)(src));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5040= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	WORD dst = regs.d[dstreg];
    {{ULONG newv = ((WORD)(dst)) + ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(~dst)) < ((UWORD)(src));
            NFLG = flgn != 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((newv) & 0xffff);*/
    }};
    static opcode_ptr op_5048= new opcode_ptr() { public void handler(long opcode) { /* ADDA */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	LONG dst = regs.a[dstreg];
    {	ULONG newv = dst + src;
            regs.a[dstreg] = (newv);*/
    }};
    static opcode_ptr op_5050= new opcode_ptr() { public void handler(long opcode) { /* ADD */
   /* {
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) + ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(~dst)) < ((UWORD)(src));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5058= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
    {	regs.a[dstreg] += 2;
    {{ULONG newv = ((WORD)(dst)) + ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(~dst)) < ((UWORD)(src));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5060= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) + ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(~dst)) < ((UWORD)(src));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5068= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) + ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(~dst)) < ((UWORD)(src));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5070= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) + ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(~dst)) < ((UWORD)(src));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5078= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
    {{	ULONG src = srcreg;
    {	CPTR dsta = (LONG)(WORD)nextiword();
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) + ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(~dst)) < ((UWORD)(src));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5079= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
    {{	ULONG src = srcreg;
    {	CPTR dsta = nextilong();
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) + ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(~dst)) < ((UWORD)(src));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5080= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	LONG dst = regs.d[dstreg];
    {{ULONG newv = ((LONG)(dst)) + ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(~dst)) < ((ULONG)(src));
            NFLG = flgn != 0;
            regs.d[dstreg] = (newv);*/
    }};
    static opcode_ptr op_5088= new opcode_ptr() { public void handler(long opcode) { /* ADDA */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	LONG dst = regs.a[dstreg];
    {	ULONG newv = dst + src;
            regs.a[dstreg] = (newv);*/
    }};
    static opcode_ptr op_5090= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) + ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(~dst)) < ((ULONG)(src));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_5098= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
    {	regs.a[dstreg] += 4;
    {{ULONG newv = ((LONG)(dst)) + ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(~dst)) < ((ULONG)(src));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_50a0= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) + ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(~dst)) < ((ULONG)(src));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_50a8= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) + ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(~dst)) < ((ULONG)(src));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_50b0= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) + ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(~dst)) < ((ULONG)(src));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_50b8= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
    {{	ULONG src = srcreg;
    {	CPTR dsta = (LONG)(WORD)nextiword();
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) + ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(~dst)) < ((ULONG)(src));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_50b9= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
    {{	ULONG src = srcreg;
    {	CPTR dsta = nextilong();
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) + ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs == flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(~dst)) < ((ULONG)(src));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_50c0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(0) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_50c8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(0)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_50d0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(0) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_50d8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(0) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_50e0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(0) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_50e8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(0) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_50f0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(0) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_50f8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(0) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_50f9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(0) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5100= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	BYTE dst = regs.d[dstreg];
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((newv) & 0xff);*/
    }};
    static opcode_ptr op_5110= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5118= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {	regs.a[dstreg] += areg_byteinc[dstreg];
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5120= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5128= new opcode_ptr() { public void handler(long opcode) { /* SUB */
   /* {
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5130= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5138= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
    {{	ULONG src = srcreg;
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5139= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
    {{	ULONG src = srcreg;
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;
            put_byte(dsta,newv);*/
    }};
    static opcode_ptr op_5140= new opcode_ptr() { public void handler(long opcode) { /* SUB */
   /* {
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	WORD dst = regs.d[dstreg];
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((newv) & 0xffff);*/
    }};
    static opcode_ptr op_5148= new opcode_ptr() { public void handler(long opcode) { /* SUBA */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	LONG dst = regs.a[dstreg];
    {	ULONG newv = dst - src;
            regs.a[dstreg] = (newv);*/
    }};
    static opcode_ptr op_5150= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5158= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
    {	regs.a[dstreg] += 2;
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5160= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5168= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5170= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5178= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
    {{	ULONG src = srcreg;
    {	CPTR dsta = (LONG)(WORD)nextiword();
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5179= new opcode_ptr() { public void handler(long opcode) { /* SUB */
   /* {
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
    {{	ULONG src = srcreg;
    {	CPTR dsta = nextilong();
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;
            put_word(dsta,newv);*/
    }};
    static opcode_ptr op_5180= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	LONG dst = regs.d[dstreg];
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;
            regs.d[dstreg] = (newv);*/
    }};
    static opcode_ptr op_5188= new opcode_ptr() { public void handler(long opcode) { /* SUBA */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	LONG dst = regs.a[dstreg];
    {	ULONG newv = dst - src;
            regs.a[dstreg] = (newv);*/
    }};
    static opcode_ptr op_5190= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_5198= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
    {	regs.a[dstreg] += 4;
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_51a0= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_51a8= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_51b0= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
            ULONG dstreg = opcode & 7;
    {{	ULONG src = srcreg;
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_51b8= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
    {{	ULONG src = srcreg;
    {	CPTR dsta = (LONG)(WORD)nextiword();
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_51b9= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG srcreg = imm8_table[((opcode >> 9) & 7)];
    {{	ULONG src = srcreg;
    {	CPTR dsta = nextilong();
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;
            put_long(dsta,newv);*/
    }};
    static opcode_ptr op_51c0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(1) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_51c8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(1)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_51d0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(1) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_51d8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(1) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_51e0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(1) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_51e8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(1) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_51f0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(1) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_51f8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(1) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_51f9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(1) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_52c0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(2) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_52c8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(2)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_52d0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(2) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_52d8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(2) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_52e0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(2) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_52e8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(2) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_52f0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
   /* {
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(2) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_52f8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(2) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_52f9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(2) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_53c0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(3) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_53c8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(3)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_53d0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(3) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_53d8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(3) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_53e0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(3) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_53e8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(3) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_53f0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(3) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_53f8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(3) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_53f9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(3) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_54c0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(4) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_54c8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(4)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_54d0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(4) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_54d8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(4) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_54e0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(4) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_54e8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(4) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_54f0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(4) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_54f8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(4) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_54f9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(4) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_55c0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(5) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_55c8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(5)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_55d0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(5) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_55d8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(5) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_55e0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(5) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_55e8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(5) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_55f0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(5) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_55f8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
   /* {
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(5) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_55f9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(5) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_56c0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(6) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_56c8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(6)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_56d0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(6) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_56d8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(6) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_56e0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(6) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_56e8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(6) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_56f0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(6) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_56f8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
   /* {
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(6) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_56f9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(6) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_57c0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(7) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_57c8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(7)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_57d0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(7) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_57d8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(7) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_57e0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(7) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_57e8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(7) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_57f0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(7) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_57f8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(7) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_57f9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(7) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_58c0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(8) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_58c8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
   /* {
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(8)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_58d0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(8) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_58d8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(8) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_58e0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(8) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_58e8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(8) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_58f0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(8) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_58f8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
   /* {
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(8) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_58f9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(8) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_59c0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(9) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_59c8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(9)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_59d0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(9) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_59d8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(9) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_59e0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
   /* {
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(9) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_59e8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(9) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_59f0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(9) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_59f8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(9) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_59f9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(9) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ac0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(10) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_5ac8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(10)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_5ad0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(10) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ad8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(10) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ae0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(10) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ae8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(10) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5af0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(10) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5af8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(10) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5af9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(10) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5bc0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(11) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_5bc8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(11)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_5bd0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(11) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5bd8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(11) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5be0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(11) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5be8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(11) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5bf0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(11) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5bf8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(11) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5bf9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(11) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5cc0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(12) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_5cc8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(12)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_5cd0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(12) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5cd8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(12) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ce0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(12) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ce8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(12) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5cf0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(12) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5cf8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(12) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5cf9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(12) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5dc0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(13) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_5dc8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(13)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_5dd0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(13) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5dd8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(13) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5de0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(13) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5de8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(13) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5df0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(13) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5df8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(13) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5df9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(13) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ec0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(14) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_5ec8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(14)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_5ed0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(14) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ed8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(14) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ee0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(14) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ee8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(14) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ef0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(14) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ef8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(14) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ef9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(14) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5fc0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{{	int val = cctrue(15) ? 0xff : 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((val) & 0xff);*/
    }};
    static opcode_ptr op_5fc8= new opcode_ptr() { public void handler(long opcode) { /* DBcc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	WORD offs = nextiword();
            if (!cctrue(15)) {
            if (src--) regs.pc += (LONG)offs - 2;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((src) & 0xffff);
            }*/
    }};
    static opcode_ptr op_5fd0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(15) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5fd8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	int val = cctrue(15) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5fe0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
    {	int val = cctrue(15) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5fe8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {	int val = cctrue(15) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ff0= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	int val = cctrue(15) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ff8= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
    {	int val = cctrue(15) ? 0xff : 0;
            put_byte(srca,val);*/
    }};
    static opcode_ptr op_5ff9= new opcode_ptr() { public void handler(long opcode) { /* Scc */
    /*{
    {{	CPTR srca = nextilong();
    {	int val = cctrue(15) ? 0xff : 0;
            put_byte(srca,val);*/
    }};

}
