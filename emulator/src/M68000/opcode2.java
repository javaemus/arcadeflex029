
package M68000;

import static M68000.cpudefsH.*;

public class opcode2 {
    static opcode_ptr op_2000= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.d[srcreg];
    {	VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_2008= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.a[srcreg];
    {	VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_2010= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_2018= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	regs.a[srcreg] += 4;
    {	VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_2020= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 4;
    {	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_2028= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_2030= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	LONG src = get_long(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_2038= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_2039= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            LONG src = get_long(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_203a= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	LONG src = get_long(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_203b= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	LONG src = get_long(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_203c= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = nextilong();
    {	VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_2040= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.d[srcreg];
    {	regs.a[dstreg] = (src);*/
    }};
    static opcode_ptr op_2048= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.a[srcreg];
    {	regs.a[dstreg] = (src);*/
    }};
    static opcode_ptr op_2050= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
   /* {
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	regs.a[dstreg] = (src);*/
    }};
    static opcode_ptr op_2058= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	regs.a[srcreg] += 4;
    {	regs.a[dstreg] = (src);*/
    }};
    static opcode_ptr op_2060= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 4;
    {	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	regs.a[dstreg] = (src);*/
    }};
    static opcode_ptr op_2068= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	regs.a[dstreg] = (src);*/
    }};
    static opcode_ptr op_2070= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	LONG src = get_long(srca);
    {	regs.a[dstreg] = (src);*/
    }};
    static opcode_ptr op_2078= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	regs.a[dstreg] = (src);*/
    }};
    static opcode_ptr op_2079= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            LONG src = get_long(srca);
    {	regs.a[dstreg] = (src);*/
    }};
    static opcode_ptr op_207a= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	LONG src = get_long(srca);
    {	regs.a[dstreg] = (src);*/
    }};
    static opcode_ptr op_207b= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	LONG src = get_long(srca);
    {	regs.a[dstreg] = (src);*/
    }};
    static opcode_ptr op_207c= new opcode_ptr() { public void handler(long opcode) { /* MOVEA */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = nextilong();
    {	regs.a[dstreg] = (src);*/
    }};
    static opcode_ptr op_2080= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2088= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.a[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2090= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2098= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	regs.a[srcreg] += 4;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20a0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 4;
    {	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20a8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20b0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20b8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20b9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20ba= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20bb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20bc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20c0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20c8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.a[srcreg];
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20d0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20d8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	regs.a[srcreg] += 4;
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20e0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 4;
    {	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20e8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20f0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20f8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20f9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20fa= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20fb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_20fc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += 4;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2100= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.d[srcreg];
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2108= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.a[srcreg];
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2110= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2118= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	regs.a[srcreg] += 4;
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2120= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 4;
    {	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2128= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2130= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	LONG src = get_long(srca);
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2138= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2139= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            LONG src = get_long(srca);
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_213a= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	LONG src = get_long(srca);
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_213b= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	LONG src = get_long(srca);
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_213c= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = nextilong();
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2140= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2148= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.a[srcreg];
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2150= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2158= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	regs.a[srcreg] += 4;
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2160= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 4;
    {	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2168= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2170= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2178= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2179= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_217a= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_217b= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	LONG src = get_long(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_217c= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2180= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.d[srcreg];
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2188= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = regs.a[srcreg];
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2190= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2198= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	regs.a[srcreg] += 4;
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21a0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= 4;
    {	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21a8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21b0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	LONG src = get_long(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21b8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21b9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            LONG src = get_long(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21ba= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	LONG src = get_long(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21bb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	LONG src = get_long(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21bc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21c0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	LONG src = regs.d[srcreg];
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21c8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	LONG src = regs.a[srcreg];
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21d0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21d8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	regs.a[srcreg] += 4;
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21e0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= 4;
    {	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21e8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21f0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	LONG src = get_long(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21f8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21f9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = nextilong();
            LONG src = get_long(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21fa= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	LONG src = get_long(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21fb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	LONG src = get_long(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_21fc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	LONG src = nextilong();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_23c0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	LONG src = regs.d[srcreg];
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_23c8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	LONG src = regs.a[srcreg];
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_23d0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_23d8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	regs.a[srcreg] += 4;
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_23e0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= 4;
    {	CPTR srca = regs.a[srcreg];
            LONG src = get_long(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_23e8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_23f0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	LONG src = get_long(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_23f8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
            LONG src = get_long(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_23f9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
    {{	CPTR srca = nextilong();
            LONG src = get_long(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_23fa= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	LONG src = get_long(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_23fb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	LONG src = get_long(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_23fc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	LONG src = nextilong();
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    
}
