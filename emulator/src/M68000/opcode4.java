
package M68000;

import static M68000.cpudefsH.*;

public class opcode4 {

    static opcode_ptr op_4000= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      BYTE src = regs.d[srcreg];
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((newv) & 0xff);*/
    }};
    static opcode_ptr op_4010= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4018= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {       regs.a[srcreg] += areg_byteinc[srcreg];
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4020= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= areg_byteinc[srcreg];
    {       CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4028= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4030= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       BYTE src = get_byte(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4038= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4039= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
    {{      CPTR srca = nextilong();
            BYTE src = get_byte(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4040= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      WORD src = regs.d[srcreg];
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((WORD)(newv)) != 0) ZFLG = 0;
            NFLG = ((WORD)(newv)) < 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((newv) & 0xffff);*/
    }};
    static opcode_ptr op_4050= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((WORD)(newv)) != 0) ZFLG = 0;
            NFLG = ((WORD)(newv)) < 0;
            put_word(srca,newv);*/
    }};
    static opcode_ptr op_4058= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {       regs.a[srcreg] += 2;
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((WORD)(newv)) != 0) ZFLG = 0;
            NFLG = ((WORD)(newv)) < 0;
            put_word(srca,newv);*/
    }};
    static opcode_ptr op_4060= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= 2;
    {       CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((WORD)(newv)) != 0) ZFLG = 0;
            NFLG = ((WORD)(newv)) < 0;
            put_word(srca,newv);*/
    }};
    static opcode_ptr op_4068= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((WORD)(newv)) != 0) ZFLG = 0;
            NFLG = ((WORD)(newv)) < 0;
            put_word(srca,newv);*/
    }};
    static opcode_ptr op_4070= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       WORD src = get_word(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((WORD)(newv)) != 0) ZFLG = 0;
            NFLG = ((WORD)(newv)) < 0;
            put_word(srca,newv);*/
    }};
    static opcode_ptr op_4078= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((WORD)(newv)) != 0) ZFLG = 0;
            NFLG = ((WORD)(newv)) < 0;
            put_word(srca,newv);*/
    }};
    static opcode_ptr op_4079= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
    {{      CPTR srca = nextilong();
            WORD src = get_word(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((WORD)(newv)) != 0) ZFLG = 0;
            NFLG = ((WORD)(newv)) < 0;
            put_word(srca,newv);*/
    }};
    static opcode_ptr op_4080= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      LONG src = regs.d[srcreg];
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((LONG)(newv)) != 0) ZFLG = 0;
            NFLG = ((LONG)(newv)) < 0;
            regs.d[srcreg] = (newv);*/
    }};
    static opcode_ptr op_4090= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((LONG)(newv)) != 0) ZFLG = 0;
            NFLG = ((LONG)(newv)) < 0;
            put_long(srca,newv);*/
    }};
    static opcode_ptr op_4098= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {       regs.a[srcreg] += 4;
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((LONG)(newv)) != 0) ZFLG = 0;
            NFLG = ((LONG)(newv)) < 0;
            put_long(srca,newv);*/
    }};
    static opcode_ptr op_40a0= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= 4;
    {       CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((LONG)(newv)) != 0) ZFLG = 0;
            NFLG = ((LONG)(newv)) < 0;
            put_long(srca,newv);*/
    }};
    static opcode_ptr op_40a8= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((LONG)(newv)) != 0) ZFLG = 0;
            NFLG = ((LONG)(newv)) < 0;
            put_long(srca,newv);*/
    }};
    static opcode_ptr op_40b0= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       LONG src = get_long(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((LONG)(newv)) != 0) ZFLG = 0;
            NFLG = ((LONG)(newv)) < 0;
            put_long(srca,newv);*/
    }};
    static opcode_ptr op_40b8= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
   /* {
    {{      CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((LONG)(newv)) != 0) ZFLG = 0;
            NFLG = ((LONG)(newv)) < 0;
            put_long(srca,newv);*/
    }};
    static opcode_ptr op_40b9= new opcode_ptr() { public void handler(long opcode) { /* NEGX */
    /*{
    {{      CPTR srca = nextilong();
            LONG src = get_long(srca);
    {       ULONG newv = 0 - src - (regs.x ? 1 : 0);
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            VFLG = (!flgs && flgo && !flgn) || (flgs && !flgo && flgn);
            regs.x = CFLG = (flgs && !flgo) || (flgn && (!flgo || flgs));
            if (((LONG)(newv)) != 0) ZFLG = 0;
            NFLG = ((LONG)(newv)) < 0;
            put_long(srca,newv);*/
    }};
    static opcode_ptr op_40c0= new opcode_ptr() { public void handler(long opcode) { /* MVSR2 */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      MakeSR();
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((regs.sr) & 0xffff);*/
    }};
    static opcode_ptr op_40d0= new opcode_ptr() { public void handler(long opcode) { /* MVSR2 */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            MakeSR();
            put_word(srca,regs.sr);*/
    }};
    static opcode_ptr op_40d8= new opcode_ptr() { public void handler(long opcode) { /* MVSR2 */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
    {       regs.a[srcreg] += 2;
            MakeSR();
            put_word(srca,regs.sr);*/
    }};
    static opcode_ptr op_40e0= new opcode_ptr() { public void handler(long opcode) { /* MVSR2 */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= 2;
    {       CPTR srca = regs.a[srcreg];
            MakeSR();
            put_word(srca,regs.sr);*/
    }};
    static opcode_ptr op_40e8= new opcode_ptr() { public void handler(long opcode) { /* MVSR2 */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            MakeSR();
            put_word(srca,regs.sr);*/
    }};
    static opcode_ptr op_40f0= new opcode_ptr() { public void handler(long opcode) { /* MVSR2 */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
            MakeSR();
            put_word(srca,regs.sr);*/
    }};
    static opcode_ptr op_40f8= new opcode_ptr() { public void handler(long opcode) { /* MVSR2 */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            MakeSR();
            put_word(srca,regs.sr);*/
    }};
    static opcode_ptr op_40f9= new opcode_ptr() { public void handler(long opcode) { /* MVSR2 */
   /* {
    {{      CPTR srca = nextilong();
            MakeSR();
            put_word(srca,regs.sr);*/
    }};
    static opcode_ptr op_4100= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       LONG src = regs.d[srcreg];
    {       LONG dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_4110= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {       LONG dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_4118= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {       regs.a[srcreg] += 4;
    {       LONG dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_4120= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       regs.a[srcreg] -= 4;
    {       CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {       LONG dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_4128= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {       LONG dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_4130= new opcode_ptr() { public void handler(long opcode) { /* CHK */
   /* {
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       LONG src = get_long(srca);
    {       LONG dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_4138= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {       LONG dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_4139= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = nextilong();
            LONG src = get_long(srca);
    {       LONG dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_413a= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {       LONG src = get_long(srca);
    {       LONG dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_413b= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = get_disp_ea(m68k_getpc());
    {       LONG src = get_long(srca);
    {       LONG dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_413c= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       LONG src = nextilong();
    {       LONG dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_4180= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       WORD src = regs.d[srcreg];
    {       WORD dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_4190= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {       WORD dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_4198= new opcode_ptr() { public void handler(long opcode) { /* CHK */
   /* {
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {       regs.a[srcreg] += 2;
    {       WORD dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_41a0= new opcode_ptr() { public void handler(long opcode) { /* CHK */
   /* {
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       regs.a[srcreg] -= 2;
    {       CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {       WORD dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_41a8= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {       WORD dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_41b0= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       WORD src = get_word(srca);
    {       WORD dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_41b8= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {       WORD dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_41b9= new opcode_ptr() { public void handler(long opcode) { /* CHK */
   /* {
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = nextilong();
            WORD src = get_word(srca);
    {       WORD dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_41ba= new opcode_ptr() { public void handler(long opcode) { /* CHK */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {       WORD src = get_word(srca);
    {       WORD dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_41bb= new opcode_ptr() { public void handler(long opcode) { /* CHK */
   /* {
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       CPTR srca = get_disp_ea(m68k_getpc());
    {       WORD src = get_word(srca);
    {       WORD dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_41bc= new opcode_ptr() { public void handler(long opcode) { /* CHK */
   /* {
            ULONG dstreg = (opcode >> 9) & 7;
    {       CPTR oldpc = m68k_getpc();
    {       WORD src = nextiword();
    {       WORD dst = regs.d[dstreg];
            if ((LONG)dst < 0) { NFLG=1; Exception(6,oldpc-2); }
            else if (dst > src) { NFLG=0; Exception(6,oldpc-2); }*/
    }};
    static opcode_ptr op_41d0= new opcode_ptr() { public void handler(long opcode) { /* LEA */
   /* {
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{      CPTR srca = regs.a[srcreg];
    {       regs.a[dstreg] = (srca);*/
    }};
    static opcode_ptr op_1e8= new opcode_ptr() { public void handler(long opcode) { /* LEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {       regs.a[dstreg] = (srca);*/
    }};
    static opcode_ptr op_41f0= new opcode_ptr() { public void handler(long opcode) { /* LEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       regs.a[dstreg] = (srca);*/
    }};
    static opcode_ptr op_41f8= new opcode_ptr() { public void handler(long opcode) { /* LEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{      CPTR srca = (LONG)(WORD)nextiword();
    {       regs.a[dstreg] = (srca);*/
    }};
    static opcode_ptr op_41f9= new opcode_ptr() { public void handler(long opcode) { /* LEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{      CPTR srca = nextilong();
    {       regs.a[dstreg] = (srca);*/
    }};
    static opcode_ptr op_41fa= new opcode_ptr() { public void handler(long opcode) { /* LEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{      CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {       regs.a[dstreg] = (srca);*/
    }};
    static opcode_ptr op_41fb= new opcode_ptr() { public void handler(long opcode) { /* LEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{      CPTR srca = get_disp_ea(m68k_getpc());
    {       regs.a[dstreg] = (srca);*/
    }};
    static opcode_ptr op_4200= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      VFLG = CFLG = 0;
            ZFLG = ((BYTE)(0)) == 0;
            NFLG = ((BYTE)(0)) < 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((0) & 0xff);*/
    }};
    static opcode_ptr op_4210= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(0)) == 0;
            NFLG = ((BYTE)(0)) < 0;
            put_byte(srca,0);*/
    }};
    static opcode_ptr op_4218= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
    {       regs.a[srcreg] += areg_byteinc[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(0)) == 0;
            NFLG = ((BYTE)(0)) < 0;
            put_byte(srca,0);*/
    }};
    static opcode_ptr op_4220= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= areg_byteinc[srcreg];
    {       CPTR srca = regs.a[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(0)) == 0;
            NFLG = ((BYTE)(0)) < 0;
            put_byte(srca,0);*/
    }};
    static opcode_ptr op_4228= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(0)) == 0;
            NFLG = ((BYTE)(0)) < 0;
            put_byte(srca,0);*/
    }};
    static opcode_ptr op_4230= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(0)) == 0;
            NFLG = ((BYTE)(0)) < 0;
            put_byte(srca,0);*/
    }};
    static opcode_ptr op_4238= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(0)) == 0;
            NFLG = ((BYTE)(0)) < 0;
            put_byte(srca,0);*/
    }};
    static opcode_ptr op_4239= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
    {{      CPTR srca = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(0)) == 0;
            NFLG = ((BYTE)(0)) < 0;
            put_byte(srca,0);*/
    }};
    static opcode_ptr op_4240= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      VFLG = CFLG = 0;
            ZFLG = ((WORD)(0)) == 0;
            NFLG = ((WORD)(0)) < 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((0) & 0xffff);*/
    }};
    static opcode_ptr op_4250= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(0)) == 0;
            NFLG = ((WORD)(0)) < 0;
            put_word(srca,0);*/
    }};
    static opcode_ptr op_4258= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
    {       regs.a[srcreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(0)) == 0;
            NFLG = ((WORD)(0)) < 0;
            put_word(srca,0);*/
    }};
    static opcode_ptr op_4260= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= 2;
    {       CPTR srca = regs.a[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(0)) == 0;
            NFLG = ((WORD)(0)) < 0;
            put_word(srca,0);*/
    }};
    static opcode_ptr op_4268= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(0)) == 0;
            NFLG = ((WORD)(0)) < 0;
            put_word(srca,0);*/
    }};
    static opcode_ptr op_4270= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(0)) == 0;
            NFLG = ((WORD)(0)) < 0;
            put_word(srca,0);*/
    }};
    static opcode_ptr op_4278= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(0)) == 0;
            NFLG = ((WORD)(0)) < 0;
            put_word(srca,0);*/
    }};
    static opcode_ptr op_4279= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
    {{      CPTR srca = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(0)) == 0;
            NFLG = ((WORD)(0)) < 0;
            put_word(srca,0);*/
    }};
    static opcode_ptr op_4280= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      VFLG = CFLG = 0;
            ZFLG = ((LONG)(0)) == 0;
            NFLG = ((LONG)(0)) < 0;
            regs.d[srcreg] = (0);*/
    }};
    static opcode_ptr op_4290= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(0)) == 0;
            NFLG = ((LONG)(0)) < 0;
            put_long(srca,0);*/
    }};
    static opcode_ptr op_4298= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
    {       regs.a[srcreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(0)) == 0;
            NFLG = ((LONG)(0)) < 0;
            put_long(srca,0);*/
    }};
    static opcode_ptr op_42a0= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= 4;
    {       CPTR srca = regs.a[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(0)) == 0;
            NFLG = ((LONG)(0)) < 0;
            put_long(srca,0);*/
    }};
    static opcode_ptr op_42a8= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(0)) == 0;
            NFLG = ((LONG)(0)) < 0;
            put_long(srca,0);*/
    }};
    static opcode_ptr op_42b0= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(0)) == 0;
            NFLG = ((LONG)(0)) < 0;
            put_long(srca,0);*/
    }};
    static opcode_ptr op_42b8= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(0)) == 0;
            NFLG = ((LONG)(0)) < 0;
            put_long(srca,0);*/
    }};
    static opcode_ptr op_42b9= new opcode_ptr() { public void handler(long opcode) { /* CLR */
    /*{
    {{      CPTR srca = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(0)) == 0;
            NFLG = ((LONG)(0)) < 0;
            put_long(srca,0);*/
    }};
    static opcode_ptr op_4400= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      BYTE src = regs.d[srcreg];
    {{ULONG dst = ((BYTE)(0)) - ((BYTE)(src));
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(dst)) < 0;
            ZFLG = ((BYTE)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(0));
            NFLG = flgn != 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((dst) & 0xff);*/
    }};
    static opcode_ptr op_4410= new opcode_ptr() { public void handler(long opcode) { /* NEG */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {{ULONG dst = ((BYTE)(0)) - ((BYTE)(src));
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(dst)) < 0;
            ZFLG = ((BYTE)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(0));
            NFLG = flgn != 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4418= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {       regs.a[srcreg] += areg_byteinc[srcreg];
    {{ULONG dst = ((BYTE)(0)) - ((BYTE)(src));
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(dst)) < 0;
            ZFLG = ((BYTE)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(0));
            NFLG = flgn != 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4420= new opcode_ptr() { public void handler(long opcode) { /* NEG */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= areg_byteinc[srcreg];
    {       CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {{ULONG dst = ((BYTE)(0)) - ((BYTE)(src));
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(dst)) < 0;
            ZFLG = ((BYTE)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(0));
            NFLG = flgn != 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4428= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {{ULONG dst = ((BYTE)(0)) - ((BYTE)(src));
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(dst)) < 0;
            ZFLG = ((BYTE)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(0));
            NFLG = flgn != 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4430= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       BYTE src = get_byte(srca);
    {{ULONG dst = ((BYTE)(0)) - ((BYTE)(src));
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(dst)) < 0;
            ZFLG = ((BYTE)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(0));
            NFLG = flgn != 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4438= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {{ULONG dst = ((BYTE)(0)) - ((BYTE)(src));
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(dst)) < 0;
            ZFLG = ((BYTE)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(0));
            NFLG = flgn != 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4439= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
    {{      CPTR srca = nextilong();
            BYTE src = get_byte(srca);
    {{ULONG dst = ((BYTE)(0)) - ((BYTE)(src));
    {       int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(0)) < 0;
            int flgn = ((BYTE)(dst)) < 0;
            ZFLG = ((BYTE)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UBYTE)(src)) > ((UBYTE)(0));
            NFLG = flgn != 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4440= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      WORD src = regs.d[srcreg];
    {{ULONG dst = ((WORD)(0)) - ((WORD)(src));
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(dst)) < 0;
            ZFLG = ((WORD)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(0));
            NFLG = flgn != 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((dst) & 0xffff);*/
    }};
    static opcode_ptr op_4450= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {{ULONG dst = ((WORD)(0)) - ((WORD)(src));
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(dst)) < 0;
            ZFLG = ((WORD)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(0));
            NFLG = flgn != 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4458= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {       regs.a[srcreg] += 2;
    {{ULONG dst = ((WORD)(0)) - ((WORD)(src));
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(dst)) < 0;
            ZFLG = ((WORD)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(0));
            NFLG = flgn != 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4460= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= 2;
    {       CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {{ULONG dst = ((WORD)(0)) - ((WORD)(src));
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(dst)) < 0;
            ZFLG = ((WORD)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(0));
            NFLG = flgn != 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4468= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {{ULONG dst = ((WORD)(0)) - ((WORD)(src));
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(dst)) < 0;
            ZFLG = ((WORD)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(0));
            NFLG = flgn != 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4470= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       WORD src = get_word(srca);
    {{ULONG dst = ((WORD)(0)) - ((WORD)(src));
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(dst)) < 0;
            ZFLG = ((WORD)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(0));
            NFLG = flgn != 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4478= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {{ULONG dst = ((WORD)(0)) - ((WORD)(src));
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(dst)) < 0;
            ZFLG = ((WORD)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(0));
            NFLG = flgn != 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4479= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
    {{      CPTR srca = nextilong();
            WORD src = get_word(srca);
    {{ULONG dst = ((WORD)(0)) - ((WORD)(src));
    {       int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(0)) < 0;
            int flgn = ((WORD)(dst)) < 0;
            ZFLG = ((WORD)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((UWORD)(src)) > ((UWORD)(0));
            NFLG = flgn != 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4480= new opcode_ptr() { public void handler(long opcode) { /* NEG */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      LONG src = regs.d[srcreg];
    {{ULONG dst = ((LONG)(0)) - ((LONG)(src));
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(dst)) < 0;
            ZFLG = ((LONG)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(0));
            NFLG = flgn != 0;
            regs.d[srcreg] = (dst);*/
    }};
    static opcode_ptr op_4490= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {{ULONG dst = ((LONG)(0)) - ((LONG)(src));
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(dst)) < 0;
            ZFLG = ((LONG)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(0));
            NFLG = flgn != 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_4498= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {       regs.a[srcreg] += 4;
    {{ULONG dst = ((LONG)(0)) - ((LONG)(src));
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(dst)) < 0;
            ZFLG = ((LONG)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(0));
            NFLG = flgn != 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_44a0= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= 4;
    {       CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {{ULONG dst = ((LONG)(0)) - ((LONG)(src));
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(dst)) < 0;
            ZFLG = ((LONG)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(0));
            NFLG = flgn != 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_44a8= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {{ULONG dst = ((LONG)(0)) - ((LONG)(src));
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(dst)) < 0;
            ZFLG = ((LONG)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(0));
            NFLG = flgn != 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_44b0= new opcode_ptr() { public void handler(long opcode) { /* NEG */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       LONG src = get_long(srca);
    {{ULONG dst = ((LONG)(0)) - ((LONG)(src));
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(dst)) < 0;
            ZFLG = ((LONG)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(0));
            NFLG = flgn != 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_44b8= new opcode_ptr() { public void handler(long opcode) { /* NEG */
   /* {
    {{      CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {{ULONG dst = ((LONG)(0)) - ((LONG)(src));
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(dst)) < 0;
            ZFLG = ((LONG)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(0));
            NFLG = flgn != 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_44b9= new opcode_ptr() { public void handler(long opcode) { /* NEG */
   /* {
    {{      CPTR srca = nextilong();
            LONG src = get_long(srca);
    {{ULONG dst = ((LONG)(0)) - ((LONG)(src));
    {       int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(0)) < 0;
            int flgn = ((LONG)(dst)) < 0;
            ZFLG = ((LONG)(dst)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = regs.x = ((ULONG)(src)) > ((ULONG)(0));
            NFLG = flgn != 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_44c0= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      WORD src = regs.d[srcreg];
            MakeSR();
            regs.sr &= 0xFF00;
            regs.sr |= src & 0xFF;
            MakeFromSR();*/
    }};
    static opcode_ptr op_44d0= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
            MakeSR();
            regs.sr &= 0xFF00;
            regs.sr |= src & 0xFF;
            MakeFromSR();*/
    }};
    static opcode_ptr op_44d8= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {       regs.a[srcreg] += 2;
            MakeSR();
            regs.sr &= 0xFF00;
            regs.sr |= src & 0xFF;
            MakeFromSR();*/
    }};
    static opcode_ptr op_44e0= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= 2;
    {       CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
            MakeSR();
            regs.sr &= 0xFF00;
            regs.sr |= src & 0xFF;
            MakeFromSR();*/
    }};
    static opcode_ptr op_44e8= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
            MakeSR();
            regs.sr &= 0xFF00;
            regs.sr |= src & 0xFF;
            MakeFromSR();*/
    }};
    static opcode_ptr op_44f0= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       WORD src = get_word(srca);
            MakeSR();
            regs.sr &= 0xFF00;
            regs.sr |= src & 0xFF;
            MakeFromSR();*/
    }};
    static opcode_ptr op_44f8= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
            MakeSR();
            regs.sr &= 0xFF00;
            regs.sr |= src & 0xFF;
            MakeFromSR();*/
    }};
    static opcode_ptr op_44f9= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
    {{      CPTR srca = nextilong();
            WORD src = get_word(srca);
            MakeSR();
            regs.sr &= 0xFF00;
            regs.sr |= src & 0xFF;
            MakeFromSR();*/
    }};
    static opcode_ptr op_44fa= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
    {{      CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {       WORD src = get_word(srca);
            MakeSR();
            regs.sr &= 0xFF00;
            regs.sr |= src & 0xFF;
            MakeFromSR();*/
    }};
    static opcode_ptr op_44fb= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
    {{      CPTR srca = get_disp_ea(m68k_getpc());
    {       WORD src = get_word(srca);
            MakeSR();
            regs.sr &= 0xFF00;
            regs.sr |= src & 0xFF;
            MakeFromSR();*/
    }};
    static opcode_ptr op_44fc= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
    {{      WORD src = nextiword();
            MakeSR();
            regs.sr &= 0xFF00;
            regs.sr |= src & 0xFF;
            MakeFromSR();*/
    }};
    static opcode_ptr op_4600= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      BYTE src = regs.d[srcreg];
    /*{       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(dst)) == 0;
            NFLG = ((BYTE)(dst)) < 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((dst) & 0xff);*/
    }};
    static opcode_ptr op_4610= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(dst)) == 0;
            NFLG = ((BYTE)(dst)) < 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4618= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {       regs.a[srcreg] += areg_byteinc[srcreg];
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(dst)) == 0;
            NFLG = ((BYTE)(dst)) < 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4620= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= areg_byteinc[srcreg];
    {       CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(dst)) == 0;
            NFLG = ((BYTE)(dst)) < 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4628= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(dst)) == 0;
            NFLG = ((BYTE)(dst)) < 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4630= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       BYTE src = get_byte(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(dst)) == 0;
            NFLG = ((BYTE)(dst)) < 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4638= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(dst)) == 0;
            NFLG = ((BYTE)(dst)) < 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4639= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
    {{      CPTR srca = nextilong();
            BYTE src = get_byte(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(dst)) == 0;
            NFLG = ((BYTE)(dst)) < 0;
            put_byte(srca,dst);*/
    }};
    static opcode_ptr op_4640= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      WORD src = regs.d[srcreg];
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(dst)) == 0;
            NFLG = ((WORD)(dst)) < 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((dst) & 0xffff);*/
    }};
    static opcode_ptr op_4650= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(dst)) == 0;
            NFLG = ((WORD)(dst)) < 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4658= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {       regs.a[srcreg] += 2;
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(dst)) == 0;
            NFLG = ((WORD)(dst)) < 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4660= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= 2;
    {       CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(dst)) == 0;
            NFLG = ((WORD)(dst)) < 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4668= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(dst)) == 0;
            NFLG = ((WORD)(dst)) < 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4670= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       WORD src = get_word(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(dst)) == 0;
            NFLG = ((WORD)(dst)) < 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4678= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(dst)) == 0;
            NFLG = ((WORD)(dst)) < 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4679= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
    {{      CPTR srca = nextilong();
            WORD src = get_word(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(dst)) == 0;
            NFLG = ((WORD)(dst)) < 0;
            put_word(srca,dst);*/
    }};
    static opcode_ptr op_4680= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      LONG src = regs.d[srcreg];
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(dst)) == 0;
            NFLG = ((LONG)(dst)) < 0;
            regs.d[srcreg] = (dst);*/
    }};
    static opcode_ptr op_4690= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(dst)) == 0;
            NFLG = ((LONG)(dst)) < 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_4698= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {       regs.a[srcreg] += 4;
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(dst)) == 0;
            NFLG = ((LONG)(dst)) < 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_46a0= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= 4;
    {       CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(dst)) == 0;
            NFLG = ((LONG)(dst)) < 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_46a8= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(dst)) == 0;
            NFLG = ((LONG)(dst)) < 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_46b0= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       LONG src = get_long(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(dst)) == 0;
            NFLG = ((LONG)(dst)) < 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_46b8= new opcode_ptr() { public void handler(long opcode) { /* NOT */
   /* {
    {{      CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(dst)) == 0;
            NFLG = ((LONG)(dst)) < 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_46b9= new opcode_ptr() { public void handler(long opcode) { /* NOT */
    /*{
    {{      CPTR srca = nextilong();
            LONG src = get_long(srca);
    {       ULONG dst = ~src;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(dst)) == 0;
            NFLG = ((LONG)(dst)) < 0;
            put_long(srca,dst);*/
    }};
    static opcode_ptr op_46c0= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
            ULONG srcreg = (opcode & 7);
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      WORD src = regs.d[srcreg];
            regs.sr = src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_46d0= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
            ULONG srcreg = (opcode & 7);
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
            regs.sr = src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_46d8= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
            ULONG srcreg = (opcode & 7);
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {       regs.a[srcreg] += 2;
            regs.sr = src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_46e0= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
            ULONG srcreg = (opcode & 7);
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      regs.a[srcreg] -= 2;
    {       CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
            regs.sr = src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_46e8= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
            ULONG srcreg = (opcode & 7);
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
            regs.sr = src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_46f0= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
            ULONG srcreg = (opcode & 7);
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       WORD src = get_word(srca);
            regs.sr = src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_46f8= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
            regs.sr = src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_46f9= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      CPTR srca = nextilong();
            WORD src = get_word(srca);
            regs.sr = src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_46fa= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
   /* {
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {       WORD src = get_word(srca);
            regs.sr = src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_46fb= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      CPTR srca = get_disp_ea(m68k_getpc());
    {       WORD src = get_word(srca);
            regs.sr = src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_46fc= new opcode_ptr() { public void handler(long opcode) { /* MV2SR */
    /*{
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      WORD src = nextiword();
            regs.sr = src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_4800= new opcode_ptr() { public void handler(long opcode) { /* NBCD */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      BYTE src = regs.d[srcreg];
    {       UWORD newv_lo = - (src & 0xF) - (regs.x ? 1 : 0);
            UWORD newv_hi = - (src & 0xF0);
            UWORD newv;
            if (newv_lo > 9) { newv_lo-=6; newv_hi-=0x10; }
            newv = newv_hi + (newv_lo & 0xF);       CFLG = regs.x = (newv_hi & 0x1F0) > 0x90;
            if (CFLG) newv -= 0x60;
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((newv) & 0xff);*/
    }};
    static opcode_ptr op_4810= new opcode_ptr() { public void handler(long opcode) { /* NBCD */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {       UWORD newv_lo = - (src & 0xF) - (regs.x ? 1 : 0);
            UWORD newv_hi = - (src & 0xF0);
            UWORD newv;
            if (newv_lo > 9) { newv_lo-=6; newv_hi-=0x10; }
            newv = newv_hi + (newv_lo & 0xF);       CFLG = regs.x = (newv_hi & 0x1F0) > 0x90;
            if (CFLG) newv -= 0x60;
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4818= new opcode_ptr() { public void handler(long opcode) { /* NBCD */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {       regs.a[srcreg] += areg_byteinc[srcreg];
    {       UWORD newv_lo = - (src & 0xF) - (regs.x ? 1 : 0);
            UWORD newv_hi = - (src & 0xF0);
            UWORD newv;
            if (newv_lo > 9) { newv_lo-=6; newv_hi-=0x10; }
            newv = newv_hi + (newv_lo & 0xF);       CFLG = regs.x = (newv_hi & 0x1F0) > 0x90;
            if (CFLG) newv -= 0x60;
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4820= new opcode_ptr() { public void handler(long opcode) { /* NBCD */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= areg_byteinc[srcreg];
    {       CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {       UWORD newv_lo = - (src & 0xF) - (regs.x ? 1 : 0);
            UWORD newv_hi = - (src & 0xF0);
            UWORD newv;
            if (newv_lo > 9) { newv_lo-=6; newv_hi-=0x10; }
            newv = newv_hi + (newv_lo & 0xF);       CFLG = regs.x = (newv_hi & 0x1F0) > 0x90;
            if (CFLG) newv -= 0x60;
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4828= new opcode_ptr() { public void handler(long opcode) { /* NBCD */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {       UWORD newv_lo = - (src & 0xF) - (regs.x ? 1 : 0);
            UWORD newv_hi = - (src & 0xF0);
            UWORD newv;
            if (newv_lo > 9) { newv_lo-=6; newv_hi-=0x10; }
            newv = newv_hi + (newv_lo & 0xF);       CFLG = regs.x = (newv_hi & 0x1F0) > 0x90;
            if (CFLG) newv -= 0x60;
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4830= new opcode_ptr() { public void handler(long opcode) { /* NBCD */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       BYTE src = get_byte(srca);
    {       UWORD newv_lo = - (src & 0xF) - (regs.x ? 1 : 0);
            UWORD newv_hi = - (src & 0xF0);
            UWORD newv;
            if (newv_lo > 9) { newv_lo-=6; newv_hi-=0x10; }
            newv = newv_hi + (newv_lo & 0xF);       CFLG = regs.x = (newv_hi & 0x1F0) > 0x90;
            if (CFLG) newv -= 0x60;
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4838= new opcode_ptr() { public void handler(long opcode) { /* NBCD */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {       UWORD newv_lo = - (src & 0xF) - (regs.x ? 1 : 0);
            UWORD newv_hi = - (src & 0xF0);
            UWORD newv;
            if (newv_lo > 9) { newv_lo-=6; newv_hi-=0x10; }
            newv = newv_hi + (newv_lo & 0xF);       CFLG = regs.x = (newv_hi & 0x1F0) > 0x90;
            if (CFLG) newv -= 0x60;
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4839= new opcode_ptr() { public void handler(long opcode) { /* NBCD */
   /* {
    {{      CPTR srca = nextilong();
            BYTE src = get_byte(srca);
    {       UWORD newv_lo = - (src & 0xF) - (regs.x ? 1 : 0);
            UWORD newv_hi = - (src & 0xF0);
            UWORD newv;
            if (newv_lo > 9) { newv_lo-=6; newv_hi-=0x10; }
            newv = newv_hi + (newv_lo & 0xF);       CFLG = regs.x = (newv_hi & 0x1F0) > 0x90;
            if (CFLG) newv -= 0x60;
            if (((BYTE)(newv)) != 0) ZFLG = 0;
            NFLG = ((BYTE)(newv)) < 0;
            put_byte(srca,newv);*/
    }};
    static opcode_ptr op_4840= new opcode_ptr() { public void handler(long opcode) { /* SWAP */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      LONG src = regs.d[srcreg];
    {       ULONG dst = ((src >> 16)&0xFFFF) | ((src&0xFFFF)<<16);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(dst)) == 0;
            NFLG = ((LONG)(dst)) < 0;
            regs.d[srcreg] = (dst);*/
    }};
    static opcode_ptr op_4850= new opcode_ptr() { public void handler(long opcode) { /* PEA */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
    {       regs.a[7] -= 4;
    {       CPTR dsta = regs.a[7];
            put_long(dsta,srca);*/
    }};
    static opcode_ptr op_4868= new opcode_ptr() { public void handler(long opcode) { /* PEA */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {       regs.a[7] -= 4;
    {       CPTR dsta = regs.a[7];
            put_long(dsta,srca);*/
    }};
    static opcode_ptr op_4870= new opcode_ptr() { public void handler(long opcode) { /* PEA */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       regs.a[7] -= 4;
    {       CPTR dsta = regs.a[7];
            put_long(dsta,srca);*/
    }};
    static opcode_ptr op_4878= new opcode_ptr() { public void handler(long opcode) { /* PEA */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
    {       regs.a[7] -= 4;
    {       CPTR dsta = regs.a[7];
            put_long(dsta,srca);*/
    }};
    static opcode_ptr op_4879= new opcode_ptr() { public void handler(long opcode) { /* PEA */
    /*{
    {{      CPTR srca = nextilong();
    {       regs.a[7] -= 4;
    {       CPTR dsta = regs.a[7];
            put_long(dsta,srca);*/
    }};
    static opcode_ptr op_487a= new opcode_ptr() { public void handler(long opcode) { /* PEA */
    /*{
    {{      CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {       regs.a[7] -= 4;
    {       CPTR dsta = regs.a[7];
            put_long(dsta,srca);*/
    }};
    static opcode_ptr op_487b= new opcode_ptr() { public void handler(long opcode) { /* PEA */
    /*{
    {{      CPTR srca = get_disp_ea(m68k_getpc());
    {       regs.a[7] -= 4;
    {       CPTR dsta = regs.a[7];
            put_long(dsta,srca);*/
    }};
    static opcode_ptr op_4880= new opcode_ptr() { public void handler(long opcode) { /* EXT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      LONG src = regs.d[srcreg];
    {       UWORD dst = (WORD)(BYTE)src;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(dst)) == 0;
            NFLG = ((WORD)(dst)) < 0;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xffff) | ((dst) & 0xffff);*/
    }};
    static opcode_ptr op_4890= new opcode_ptr() { public void handler(long opcode) { /* MVMLE */
    /*{
            ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword();
    {       CPTR srca = regs.a[dstreg];
    {       UWORD dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
            while (dmask) { put_word(srca, regs.d[movem_index1[dmask]]); srca += 2; dmask = movem_next[dmask]; }
            while (amask) { put_word(srca, regs.a[movem_index1[amask]]); srca += 2; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_48a0= new opcode_ptr() { public void handler(long opcode) { /* MVMLE */
    /*{
            ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword();
    {{      CPTR srca = regs.a[dstreg];
    {       UWORD amask = mask & 0xff, dmask = (mask >> 8) & 0xff;
            while (amask) { srca -= 2; put_word(srca, regs.a[movem_index2[amask]]); amask = movem_next[amask]; }
            while (dmask) { srca -= 2; put_word(srca, regs.d[movem_index2[dmask]]); dmask = movem_next[dmask]; }
            regs.a[dstreg] = srca;*/
    }};
    static opcode_ptr op_48a8= new opcode_ptr() { public void handler(long opcode) { /* MVMLE */
    /*{
            ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword();
    {       CPTR srca = regs.a[dstreg] + (LONG)(WORD)nextiword();
    {       UWORD dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
            while (dmask) { put_word(srca, regs.d[movem_index1[dmask]]); srca += 2; dmask = movem_next[dmask]; }
            while (amask) { put_word(srca, regs.a[movem_index1[amask]]); srca += 2; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_48b0= new opcode_ptr() { public void handler(long opcode) { /* MVMLE */
   /* {
            ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword();
    {       CPTR srca = get_disp_ea(regs.a[dstreg]);
    {       UWORD dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
            while (dmask) { put_word(srca, regs.d[movem_index1[dmask]]); srca += 2; dmask = movem_next[dmask]; }
            while (amask) { put_word(srca, regs.a[movem_index1[amask]]); srca += 2; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_48b8= new opcode_ptr() { public void handler(long opcode) { /* MVMLE */
    /*{
    {       UWORD mask = nextiword();
    {       CPTR srca = (LONG)(WORD)nextiword();
    {       UWORD dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
            while (dmask) { put_word(srca, regs.d[movem_index1[dmask]]); srca += 2; dmask = movem_next[dmask]; }
            while (amask) { put_word(srca, regs.a[movem_index1[amask]]); srca += 2; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_48b9= new opcode_ptr() { public void handler(long opcode) { /* MVMLE */
    /*{
    {       UWORD mask = nextiword();
    {       CPTR srca = nextilong();
    {       UWORD dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
            while (dmask) { put_word(srca, regs.d[movem_index1[dmask]]); srca += 2; dmask = movem_next[dmask]; }
            while (amask) { put_word(srca, regs.a[movem_index1[amask]]); srca += 2; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_48c0= new opcode_ptr() { public void handler(long opcode) { /* EXT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      LONG src = regs.d[srcreg];
    {       ULONG dst = (LONG)(WORD)src;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(dst)) == 0;
            NFLG = ((LONG)(dst)) < 0;
            regs.d[srcreg] = (dst);*/
    }};
    static opcode_ptr op_48d0= new opcode_ptr() { public void handler(long opcode) { /* MVMLE */
    /*{
            ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword();
    {       CPTR srca = regs.a[dstreg];
    {       UWORD dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
            while (dmask) { put_long(srca, regs.d[movem_index1[dmask]]); srca += 4; dmask = movem_next[dmask]; }
            while (amask) { put_long(srca, regs.a[movem_index1[amask]]); srca += 4; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_48e0= new opcode_ptr() { public void handler(long opcode) { /* MVMLE */
   /* {
            ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword();
    {{      CPTR srca = regs.a[dstreg];
    {       UWORD amask = mask & 0xff, dmask = (mask >> 8) & 0xff;
            while (amask) { srca -= 4; put_long(srca, regs.a[movem_index2[amask]]); amask = movem_next[amask]; }
            while (dmask) { srca -= 4; put_long(srca, regs.d[movem_index2[dmask]]); dmask = movem_next[dmask]; }
            regs.a[dstreg] = srca;*/
    }};
    static opcode_ptr op_48e8= new opcode_ptr() { public void handler(long opcode) { /* MVMLE */
    /*{
            ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword();
    {       CPTR srca = regs.a[dstreg] + (LONG)(WORD)nextiword();
    {       UWORD dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
            while (dmask) { put_long(srca, regs.d[movem_index1[dmask]]); srca += 4; dmask = movem_next[dmask]; }
            while (amask) { put_long(srca, regs.a[movem_index1[amask]]); srca += 4; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_48f0= new opcode_ptr() { public void handler(long opcode) { /* MVMLE */
    /*{
            ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword();
    {       CPTR srca = get_disp_ea(regs.a[dstreg]);
    {       UWORD dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
            while (dmask) { put_long(srca, regs.d[movem_index1[dmask]]); srca += 4; dmask = movem_next[dmask]; }
            while (amask) { put_long(srca, regs.a[movem_index1[amask]]); srca += 4; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_48f8= new opcode_ptr() { public void handler(long opcode) { /* MVMLE */
    /*{
    {       UWORD mask = nextiword();
    {       CPTR srca = (LONG)(WORD)nextiword();
    {       UWORD dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
            while (dmask) { put_long(srca, regs.d[movem_index1[dmask]]); srca += 4; dmask = movem_next[dmask]; }
            while (amask) { put_long(srca, regs.a[movem_index1[amask]]); srca += 4; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_48f9= new opcode_ptr() { public void handler(long opcode) { /* MVMLE */
   /* {
    {       UWORD mask = nextiword();
    {       CPTR srca = nextilong();
    {       UWORD dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
            while (dmask) { put_long(srca, regs.d[movem_index1[dmask]]); srca += 4; dmask = movem_next[dmask]; }
            while (amask) { put_long(srca, regs.a[movem_index1[amask]]); srca += 4; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_49c0= new opcode_ptr() { public void handler(long opcode) { /* EXT */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      LONG src = regs.d[srcreg];
    {       ULONG dst = (LONG)(BYTE)src;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(dst)) == 0;
            NFLG = ((LONG)(dst)) < 0;
            regs.d[srcreg] = (dst);*/
    }};
    static opcode_ptr op_4a00= new opcode_ptr() { public void handler(long opcode) { /* TST */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      BYTE src = regs.d[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;*/
    }};
    static opcode_ptr op_4a10= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;*/
    }};
    static opcode_ptr op_4a18= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {       regs.a[srcreg] += areg_byteinc[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;*/
    }};
    static opcode_ptr op_4a20= new opcode_ptr() { public void handler(long opcode) { /* TST */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= areg_byteinc[srcreg];
    {       CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;*/
    }};
    static opcode_ptr op_4a28= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;*/
    }};
    static opcode_ptr op_4a30= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;*/
    }};
    static opcode_ptr op_4a38= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;*/
    }};
    static opcode_ptr op_4a39= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      CPTR srca = nextilong();
            BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;*/
    }};
    static opcode_ptr op_4a3a= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {       BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;*/
    }};
    static opcode_ptr op_4a3b= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      CPTR srca = get_disp_ea(m68k_getpc());
    {       BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;*/
    }};
    static opcode_ptr op_4a3c= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      BYTE src = nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;*/
    }};
    static opcode_ptr op_4a40= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      WORD src = regs.d[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;*/
    }};
    static opcode_ptr op_4a48= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      WORD src = regs.a[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;*/
    }};
    static opcode_ptr op_4a50= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;*/
    }};
    static opcode_ptr op_4a58= new opcode_ptr() { public void handler(long opcode) { /* TST */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {       regs.a[srcreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;*/
    }};
    static opcode_ptr op_4a60= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= 2;
    {       CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;*/
    }};
    static opcode_ptr op_4a68= new opcode_ptr() { public void handler(long opcode) { /* TST */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;*/
    }};
    static opcode_ptr op_4a70= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       WORD src = get_word(srca);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;*/
    }};
    static opcode_ptr op_4a78= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;*/
    }};
    static opcode_ptr op_4a79= new opcode_ptr() { public void handler(long opcode) { /* TST */
   /* {
    {{      CPTR srca = nextilong();
            WORD src = get_word(srca);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;*/
    }};
    static opcode_ptr op_4a7a= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {       WORD src = get_word(srca);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;*/
    }};
    static opcode_ptr op_4a7b= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      CPTR srca = get_disp_ea(m68k_getpc());
    {       WORD src = get_word(srca);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;*/
    }};
    static opcode_ptr op_4a7c= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      WORD src = nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;*/
    }};
    static opcode_ptr op_4a80= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      LONG src = regs.d[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;*/
    }};
    static opcode_ptr op_4a88= new opcode_ptr() { public void handler(long opcode) { /* TST */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      LONG src = regs.a[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;*/
    }};
    static opcode_ptr op_4a90= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;*/
    }};
    static opcode_ptr op_4a98= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {       regs.a[srcreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;*/
    }};
    static opcode_ptr op_4aa0= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= 4;
    {       CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;*/
    }};
    static opcode_ptr op_4aa8= new opcode_ptr() { public void handler(long opcode) { /* TST */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;*/
    }};
    static opcode_ptr op_4ab0= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       LONG src = get_long(srca);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;*/
    }};
    static opcode_ptr op_4ab8= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;*/
    }};
    static opcode_ptr op_4ab9= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      CPTR srca = nextilong();
            LONG src = get_long(srca);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;*/
    }};
    static opcode_ptr op_4aba= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {       LONG src = get_long(srca);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;*/
    }};
    static opcode_ptr op_4abb= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      CPTR srca = get_disp_ea(m68k_getpc());
    {       LONG src = get_long(srca);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;*/
    }};
    static opcode_ptr op_4abc= new opcode_ptr() { public void handler(long opcode) { /* TST */
    /*{
    {{      LONG src = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;*/
    }};
    static opcode_ptr op_4ac0= new opcode_ptr() { public void handler(long opcode) { /* TAS */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      BYTE src = regs.d[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            src |= 0x80;
            regs.d[srcreg] = (regs.d[srcreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_4ad0= new opcode_ptr() { public void handler(long opcode) { /* TAS */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            src |= 0x80;
            put_byte(srca,src);*/
    }};
    static opcode_ptr op_4ad8= new opcode_ptr() { public void handler(long opcode) { /* TAS */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {       regs.a[srcreg] += areg_byteinc[srcreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            src |= 0x80;
            put_byte(srca,src);*/
    }};
    static opcode_ptr op_4ae0= new opcode_ptr() { public void handler(long opcode) { /* TAS */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      regs.a[srcreg] -= areg_byteinc[srcreg];
    {       CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            src |= 0x80;
            put_byte(srca,src);*/
    }};
    static opcode_ptr op_4ae8= new opcode_ptr() { public void handler(long opcode) { /* TAS */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            src |= 0x80;
            put_byte(srca,src);*/
    }};
    static opcode_ptr op_4af0= new opcode_ptr() { public void handler(long opcode) { /* TAS */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            src |= 0x80;
            put_byte(srca,src);*/
    }};
    static opcode_ptr op_4af8= new opcode_ptr() { public void handler(long opcode) { /* TAS */
   /* {
    {{      CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            src |= 0x80;
            put_byte(srca,src);*/
    }};
    static opcode_ptr op_4af9= new opcode_ptr() { public void handler(long opcode) { /* TAS */
    /*{
    {{      CPTR srca = nextilong();
            BYTE src = get_byte(srca);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            src |= 0x80;
            put_byte(srca,src);*/
    }};
    static opcode_ptr op_4c90= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
    /*{
            ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = regs.a[dstreg];
    {       while (dmask) { regs.d[movem_index1[dmask]] = (LONG)(WORD)get_word(srca); srca += 2; dmask = movem_next[dmask]; }
            while (amask) { regs.a[movem_index1[amask]] = (LONG)(WORD)get_word(srca); srca += 2; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4c98= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
   /* {
            ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = regs.a[dstreg];
    {       while (dmask) { regs.d[movem_index1[dmask]] = (LONG)(WORD)get_word(srca); srca += 2; dmask = movem_next[dmask]; }
            while (amask) { regs.a[movem_index1[amask]] = (LONG)(WORD)get_word(srca); srca += 2; amask = movem_next[amask]; }
            regs.a[dstreg] = srca;*/
    }};
    static opcode_ptr op_4ca8= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
    /*{
            ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = regs.a[dstreg] + (LONG)(WORD)nextiword();
    {       while (dmask) { regs.d[movem_index1[dmask]] = (LONG)(WORD)get_word(srca); srca += 2; dmask = movem_next[dmask]; }
            while (amask) { regs.a[movem_index1[amask]] = (LONG)(WORD)get_word(srca); srca += 2; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4cb0= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
    /*{
            ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = get_disp_ea(regs.a[dstreg]);
    {       while (dmask) { regs.d[movem_index1[dmask]] = (LONG)(WORD)get_word(srca); srca += 2; dmask = movem_next[dmask]; }
            while (amask) { regs.a[movem_index1[amask]] = (LONG)(WORD)get_word(srca); srca += 2; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4cb8= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
    /*{
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = (LONG)(WORD)nextiword();
    {       while (dmask) { regs.d[movem_index1[dmask]] = (LONG)(WORD)get_word(srca); srca += 2; dmask = movem_next[dmask]; }
            while (amask) { regs.a[movem_index1[amask]] = (LONG)(WORD)get_word(srca); srca += 2; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4cb9= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
    /*{
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = nextilong();
    {       while (dmask) { regs.d[movem_index1[dmask]] = (LONG)(WORD)get_word(srca); srca += 2; dmask = movem_next[dmask]; }
            while (amask) { regs.a[movem_index1[amask]] = (LONG)(WORD)get_word(srca); srca += 2; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4cba= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
    /*{
                                    ULONG dstreg;
                                    dstreg = 2;
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = m68k_getpc();
                                    srca += (LONG)(WORD)nextiword();
    {       while (dmask) { regs.d[movem_index1[dmask]] = (LONG)(WORD)get_word(srca); srca += 2; dmask = movem_next[dmask]; }
                                    while (amask) { regs.a[movem_index1[amask]] = (LONG)(WORD)get_word(srca); srca += 2; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4cbb= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
    /*{
                                    ULONG dstreg;
                                    dstreg = 3;
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = get_disp_ea(m68k_getpc());
    {       while (dmask) { regs.d[movem_index1[dmask]] = (LONG)(WORD)get_word(srca); srca += 2; dmask = movem_next[dmask]; }
                                    while (amask) { regs.a[movem_index1[amask]] = (LONG)(WORD)get_word(srca); srca += 2; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4cd0= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
    /*{
                                    ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = regs.a[dstreg];
    {       while (dmask) { regs.d[movem_index1[dmask]] = get_long(srca); srca += 4; dmask = movem_next[dmask]; }
                                    while (amask) { regs.a[movem_index1[amask]] = get_long(srca); srca += 4; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4cd8= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
   /* {
                                    ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = regs.a[dstreg];
    {       while (dmask) { regs.d[movem_index1[dmask]] = get_long(srca); srca += 4; dmask = movem_next[dmask]; }
                                    while (amask) { regs.a[movem_index1[amask]] = get_long(srca); srca += 4; amask = movem_next[amask]; }
                                    regs.a[dstreg] = srca;*/
    }};
    static opcode_ptr op_4ce8= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
   /* {
                                    ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = regs.a[dstreg] + (LONG)(WORD)nextiword();
    {       while (dmask) { regs.d[movem_index1[dmask]] = get_long(srca); srca += 4; dmask = movem_next[dmask]; }
                                    while (amask) { regs.a[movem_index1[amask]] = get_long(srca); srca += 4; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4cf0= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
    /*{
                                    ULONG dstreg = opcode & 7;
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = get_disp_ea(regs.a[dstreg]);
    {       while (dmask) { regs.d[movem_index1[dmask]] = get_long(srca); srca += 4; dmask = movem_next[dmask]; }
                                    while (amask) { regs.a[movem_index1[amask]] = get_long(srca); srca += 4; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4cf8= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
   /* {
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = (LONG)(WORD)nextiword();
    {       while (dmask) { regs.d[movem_index1[dmask]] = get_long(srca); srca += 4; dmask = movem_next[dmask]; }
                                    while (amask) { regs.a[movem_index1[amask]] = get_long(srca); srca += 4; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4cf9= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
    /*{
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = nextilong();
    {       while (dmask) { regs.d[movem_index1[dmask]] = get_long(srca); srca += 4; dmask = movem_next[dmask]; }
                                    while (amask) { regs.a[movem_index1[amask]] = get_long(srca); srca += 4; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4cfa= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
    /*{
                                    ULONG dstreg;
                                    dstreg = 2;
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = m68k_getpc();
                                    srca += (LONG)(WORD)nextiword();
    {       while (dmask) { regs.d[movem_index1[dmask]] = get_long(srca); srca += 4; dmask = movem_next[dmask]; }
                                    while (amask) { regs.a[movem_index1[amask]] = get_long(srca); srca += 4; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4cfb= new opcode_ptr() { public void handler(long opcode) { /* MVMEL */
   /* {
                                    ULONG dstreg;
                                    dstreg = 3;
    {       UWORD mask = nextiword(), dmask = mask & 0xff, amask = (mask >> 8) & 0xff;
    {       CPTR srca = get_disp_ea(m68k_getpc());
    {       while (dmask) { regs.d[movem_index1[dmask]] = get_long(srca); srca += 4; dmask = movem_next[dmask]; }
                                    while (amask) { regs.a[movem_index1[amask]] = get_long(srca); srca += 4; amask = movem_next[amask]; }*/
    }};
    static opcode_ptr op_4e40= new opcode_ptr() { public void handler(long opcode) { /* TRAP */
    /*{
                                    ULONG srcreg = (opcode & 15);
    {{      ULONG src = srcreg;
                                    Exception(src+32,0);*/
    }};
    static opcode_ptr op_4e50= new opcode_ptr() { public void handler(long opcode) { /* LINK */
    /*{
                                    ULONG srcreg = (opcode & 7);
    {{      regs.a[7] -= 4;
    {       CPTR olda = regs.a[7];
    {       LONG src = regs.a[srcreg];
                                    put_long(olda,src);
                                    regs.a[srcreg] = (regs.a[7]);
    {       WORD offs = nextiword();
            regs.a[7] += offs;*/
    }};
    static opcode_ptr op_4e58= new opcode_ptr() { public void handler(long opcode) { /* UNLK */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      LONG src = regs.a[srcreg];
            regs.a[7] = src;
    {       CPTR olda = regs.a[7];
            LONG old = get_long(olda);
    {       regs.a[7] += 4;
            regs.a[srcreg] = (old);*/
    }};
    static opcode_ptr op_4e60= new opcode_ptr() { public void handler(long opcode) { /* MVR2USP */
   /* {
            ULONG srcreg = (opcode & 7);
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      LONG src = regs.a[srcreg];
            regs.usp = src;*/
    }};
    static opcode_ptr op_4e68= new opcode_ptr() { public void handler(long opcode) { /* MVUSP2R */
    /*{
            ULONG srcreg = (opcode & 7);
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      regs.a[srcreg] = (regs.usp);*/
    }};
    static opcode_ptr op_4e70= new opcode_ptr() { public void handler(long opcode) { /* RESET */
    /*{
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {       */
    }};
    static opcode_ptr op_4e71= new opcode_ptr() { public void handler(long opcode) { /* NOP */
    /*{
    {}*/}};
    static opcode_ptr op_4e72= new opcode_ptr() { public void handler(long opcode) { /* STOP */
    /*{
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      WORD src = nextiword();
            regs.sr = src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_4e73= new opcode_ptr() { public void handler(long opcode) { /* RTE */
    /*{
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {{      CPTR sra = regs.a[7];
            WORD sr = get_word(sra);
    {       regs.a[7] += 2;
    {       CPTR pca = regs.a[7];
            LONG pc = get_long(pca);
    {       regs.a[7] += 4;
            regs.sr = sr; m68k_setpc(pc);
            MakeFromSR();*/
    }};
    static opcode_ptr op_4e74= new opcode_ptr() { public void handler(long opcode) { /* RTD */
    /*{
    {{      CPTR pca = regs.a[7];
            LONG pc = get_long(pca);
    {       regs.a[7] += 4;
    {       WORD offs = nextiword();
            regs.a[7] += offs;
            m68k_setpc(pc);*/
    }};
    static opcode_ptr op_4e75= new opcode_ptr() { public void handler(long opcode) { /* RTS */
    /*{
    {{      CPTR pca = regs.a[7];
            LONG pc = get_long(pca);
    {       regs.a[7] += 4;
            m68k_setpc(pc);*/
    }};
    static opcode_ptr op_4e76= new opcode_ptr() { public void handler(long opcode) { /* TRAPV */
    /*{
    {       if(VFLG) Exception(7,m68k_getpc()-2);*/
    }};
    static opcode_ptr op_4e77= new opcode_ptr() { public void handler(long opcode) { /* RTR */
    /*{
    {       MakeSR();
    {       CPTR sra = regs.a[7];
            WORD sr = get_word(sra);
    {       regs.a[7] += 2;
    {       CPTR pca = regs.a[7];
            LONG pc = get_long(pca);
    {       regs.a[7] += 4;
            regs.sr &= 0xFF00; sr &= 0xFF;
            regs.sr |= sr; m68k_setpc(pc);
            MakeFromSR();*/
    }};
    static opcode_ptr op_4e90= new opcode_ptr() { public void handler(long opcode) { /* JSR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
    {       regs.a[7] -= 4;
    {       CPTR spa = regs.a[7];
            put_long(spa,m68k_getpc());
            m68k_setpc(srca);*/
    }};
    static opcode_ptr op_4ea8= new opcode_ptr() { public void handler(long opcode) { /* JSR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
    {       regs.a[7] -= 4;
    {       CPTR spa = regs.a[7];
            put_long(spa,m68k_getpc());
            m68k_setpc(srca);*/
    }};
    static opcode_ptr op_4eb0= new opcode_ptr() { public void handler(long opcode) { /* JSR */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
    {       regs.a[7] -= 4;
    {       CPTR spa = regs.a[7];
            put_long(spa,m68k_getpc());
            m68k_setpc(srca);*/
    }};
    static opcode_ptr op_4eb8= new opcode_ptr() { public void handler(long opcode) { /* JSR */
    /*{
    {{      CPTR srca = (LONG)(WORD)nextiword();
    {       regs.a[7] -= 4;
    {       CPTR spa = regs.a[7];
            put_long(spa,m68k_getpc());
            m68k_setpc(srca);*/
    }};
    static opcode_ptr op_4eb9= new opcode_ptr() { public void handler(long opcode) { /* JSR */
    /*{
    {{      CPTR srca = nextilong();
    {       regs.a[7] -= 4;
    {       CPTR spa = regs.a[7];
            put_long(spa,m68k_getpc());
            m68k_setpc(srca);*/
    }};
    static opcode_ptr op_4eba= new opcode_ptr() { public void handler(long opcode) { /* JSR */
    /*{
    {{      CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {       regs.a[7] -= 4;
    {       CPTR spa = regs.a[7];
            put_long(spa,m68k_getpc());
            m68k_setpc(srca);*/
    }};
    static opcode_ptr op_4ebb= new opcode_ptr() { public void handler(long opcode) { /* JSR */
    /*{
    {{      CPTR srca = get_disp_ea(m68k_getpc());
    {       regs.a[7] -= 4;
    {       CPTR spa = regs.a[7];
            put_long(spa,m68k_getpc());
            m68k_setpc(srca);*/
    }};
    static opcode_ptr op_4ed0= new opcode_ptr() { public void handler(long opcode) { /* JMP */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg];
            m68k_setpc(srca);*/
    }};
    static opcode_ptr op_4ee8= new opcode_ptr() { public void handler(long opcode) { /* JMP */
    /*{
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            m68k_setpc(srca);*/
    }};
    static opcode_ptr op_4ef0= new opcode_ptr() { public void handler(long opcode) { /* JMP */
   /* {
            ULONG srcreg = (opcode & 7);
    {{      CPTR srca = get_disp_ea(regs.a[srcreg]);
            m68k_setpc(srca);*/
    }};
    static opcode_ptr op_4ef8= new opcode_ptr() { public void handler(long opcode) { /* JMP */
   /* {
    {{      CPTR srca = (LONG)(WORD)nextiword();
            m68k_setpc(srca);*/
    }};
    static opcode_ptr op_4ef9= new opcode_ptr() { public void handler(long opcode) { /* JMP */
    /*{
    {{      CPTR srca = nextilong();
            m68k_setpc(srca);*/
    }};
    static opcode_ptr op_4efa= new opcode_ptr() { public void handler(long opcode) { /* JMP */
    /*{
    {{      CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
            m68k_setpc(srca);*/
    }};
    static opcode_ptr op_4efb= new opcode_ptr() { public void handler(long opcode) { /* JMP */
    /*{
    {{      CPTR srca = get_disp_ea(m68k_getpc());
            m68k_setpc(srca);*/
    }};

}
