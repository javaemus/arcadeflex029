
package M68000;

import static M68000.cpudefsH.*;

public class opcode3 {
    static opcode_ptr op_3000= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.d[srcreg];
    {	VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_3008= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.a[srcreg];
    {	VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_3010= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_3018= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	regs.a[srcreg] += 2;
    {	VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_3020= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 2;
    {	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_3028= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_3030= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	WORD src = get_word(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_3038= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_3039= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            WORD src = get_word(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_303a= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	WORD src = get_word(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_303b= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	WORD src = get_word(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_303c= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = nextiword();
    {	VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_3040= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.d[srcreg];
    {	regs.a[dstreg] = (LONG)(WORD)(src);*/
    }};
    static opcode_ptr op_3048= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.a[srcreg];
    {	regs.a[dstreg] = (LONG)(WORD)(src);*/
    }};
    static opcode_ptr op_3050= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	regs.a[dstreg] = (LONG)(WORD)(src);*/
    }};
    static opcode_ptr op_3058= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	regs.a[srcreg] += 2;
    {	regs.a[dstreg] = (LONG)(WORD)(src);*/
    }};
    static opcode_ptr op_3060= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 2;
    {	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	regs.a[dstreg] = (LONG)(WORD)(src);*/
    }};
    static opcode_ptr op_3068= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	regs.a[dstreg] = (LONG)(WORD)(src);*/
    }};
    static opcode_ptr op_3070= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	WORD src = get_word(srca);
    {	regs.a[dstreg] = (LONG)(WORD)(src);*/
    }};
    static opcode_ptr op_3078= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	regs.a[dstreg] = (LONG)(WORD)(src);*/
    }};
    static opcode_ptr op_3079= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            WORD src = get_word(srca);
    {	regs.a[dstreg] = (LONG)(WORD)(src);*/
    }};
    static opcode_ptr op_307a= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	WORD src = get_word(srca);
    {	regs.a[dstreg] = (LONG)(WORD)(src);*/
    }};
    static opcode_ptr op_307b= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	WORD src = get_word(srca);
    {	regs.a[dstreg] = (LONG)(WORD)(src);*/
    }};
    static opcode_ptr op_307c= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = nextiword();
    {	regs.a[dstreg] = (LONG)(WORD)(src);*/
    }};
    static opcode_ptr op_3080= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3088= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.a[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3090= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3098= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	regs.a[srcreg] += 2;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30a0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 2;
    {	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30a8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30b0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30b8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30b9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30ba= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30bb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30bc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30c0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30c8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.a[srcreg];
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30d0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30d8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	regs.a[srcreg] += 2;
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30e0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 2;
    {	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30e8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30f0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30f8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30f9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30fa= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30fb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_30fc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 2;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3100= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.d[srcreg];
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3108= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.a[srcreg];
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3110= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3118= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	regs.a[srcreg] += 2;
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3120= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 2;
    {	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3128= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3130= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	WORD src = get_word(srca);
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3138= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3139= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            WORD src = get_word(srca);
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_313a= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	WORD src = get_word(srca);
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_313b= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	WORD src = get_word(srca);
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_313c= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = nextiword();
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3140= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3148= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.a[srcreg];
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3150= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3158= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	regs.a[srcreg] += 2;
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3160= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 2;
    {	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3168= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3170= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3178= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3179= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_317a= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_317b= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	WORD src = get_word(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_317c= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3180= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.d[srcreg];
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3188= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = regs.a[srcreg];
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3190= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_3198= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	regs.a[srcreg] += 2;
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31a0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 2;
    {	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31a8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31b0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	WORD src = get_word(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31b8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31b9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            WORD src = get_word(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31ba= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	WORD src = get_word(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31bb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	WORD src = get_word(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31bc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31c0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31c8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.a[srcreg];
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31d0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31d8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	regs.a[srcreg] += 2;
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31e0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= 2;
    {	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31e8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31f0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	WORD src = get_word(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31f8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31f9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = nextilong();
            WORD src = get_word(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31fa= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	WORD src = get_word(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31fb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	WORD src = get_word(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_31fc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_33c0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.d[srcreg];
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_33c8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	WORD src = regs.a[srcreg];
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_33d0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_33d8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	regs.a[srcreg] += 2;
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_33e0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= 2;
    {	CPTR srca = regs.a[srcreg];
            WORD src = get_word(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_33e8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_33f0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	WORD src = get_word(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_33f8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
            WORD src = get_word(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_33f9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = nextilong();
            WORD src = get_word(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_33fa= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	WORD src = get_word(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_33fb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	WORD src = get_word(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_33fc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
   
}
