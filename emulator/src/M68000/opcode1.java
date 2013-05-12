
package M68000;

/**
 *
 * @author george
 */
import static M68000.cpudefsH.*;

public class opcode1 {
    static opcode_ptr op_1000= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	BYTE src = regs.d[srcreg];
    {	VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_1010= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_1018= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_1020= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_1028= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_1030= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	BYTE src = get_byte(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_1038= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_1039= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            BYTE src = get_byte(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_103a= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	BYTE src = get_byte(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_103b= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	BYTE src = get_byte(srca);
    {	VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_103c= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	BYTE src = nextiword();
    {	VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_1080= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1090= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1098= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10a0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10a8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10b0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10b8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10b9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10ba= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10bb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10bc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10c0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10d0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10d8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10e0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10e8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10f0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10f8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10f9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10fa= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10fb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_10fc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1100= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	BYTE src = regs.d[srcreg];
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1110= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1118= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1120= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1128= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1130= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	BYTE src = get_byte(srca);
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1138= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1139= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            BYTE src = get_byte(srca);
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_113a= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	BYTE src = get_byte(srca);
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_113b= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	BYTE src = get_byte(srca);
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_113c= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	BYTE src = nextiword();
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1140= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1150= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1158= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1160= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1168= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1170= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1178= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1179= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_117a= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_117b= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_117c= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1180= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1190= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_1198= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11a0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11a8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11b0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11b8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11b9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = nextilong();
            BYTE src = get_byte(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11ba= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11bb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11bc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG dstreg = (opcode >> 9) & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11c0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11d0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11d8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11e0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11e8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11f0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11f8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11f9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = nextilong();
            BYTE src = get_byte(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11fa= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11fb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_11fc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	BYTE src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_13c0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_13d0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_13d8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	regs.a[srcreg] += areg_byteinc[srcreg];
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_13e0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	regs.a[srcreg] -= areg_byteinc[srcreg];
    {	CPTR srca = regs.a[srcreg];
            BYTE src = get_byte(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_13e8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = regs.a[srcreg] + (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_13f0= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
   /* {
            ULONG srcreg = (opcode & 7);
    {{	CPTR srca = get_disp_ea(regs.a[srcreg]);
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_13f8= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = (LONG)(WORD)nextiword();
            BYTE src = get_byte(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_13f9= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = nextilong();
            BYTE src = get_byte(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_13fa= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = m68k_getpc();
            srca += (LONG)(WORD)nextiword();
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_13fb= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	CPTR srca = get_disp_ea(m68k_getpc());
    {	BYTE src = get_byte(srca);
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_13fc= new opcode_ptr() { public void handler(long opcode) { /* MOVE */
    /*{
    {{	BYTE src = nextiword();
    {	CPTR dsta = nextilong();
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    
}
