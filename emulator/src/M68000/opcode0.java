
package M68000;

/**
 *
 * @author george
 */
import static M68000.cpudefsH.*;

public class opcode0 {

    static opcode_ptr op_0= new opcode_ptr() { public void handler(long opcode) {/* OR */

        /*ULONG dstreg = opcode & 7;
    	BYTE src = nextiword();
    	BYTE dst = regs.d[dstreg];
        src |= dst;
        VFLG = CFLG = 0;
        ZFLG = ((BYTE)(src)) == 0;
        NFLG = ((BYTE)(src)) < 0;
        regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_10= new opcode_ptr() { public void handler(long opcode) { /* OR */
    
      /*  ULONG dstreg = opcode & 7;
    	BYTE src = nextiword();
    	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_18= new opcode_ptr() { public void handler(long opcode) { /* OR */
    
      /*      ULONG dstreg = opcode & 7;
    	BYTE src = nextiword();
    	regs.a[dstreg] += areg_byteinc[dstreg];
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_20= new opcode_ptr() { public void handler(long opcode) { /* OR */
    
       /*     ULONG dstreg = opcode & 7;
    	BYTE src = nextiword();
    	regs.a[dstreg] -= areg_byteinc[dstreg];
    	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_28= new opcode_ptr() { public void handler(long opcode) { /* OR */
    
       /*     ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_30= new opcode_ptr() { public void handler(long opcode) { /* OR */
    
      /*      ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_38= new opcode_ptr() { public void handler(long opcode) { /* OR */
    
    /*{{	BYTE src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_39= new opcode_ptr() { public void handler(long opcode) { /* OR */
    /*{{	BYTE src = nextiword();
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_3c= new opcode_ptr() { public void handler(long opcode) { /* ORSR */
    
    /*{	MakeSR();
    {	WORD src = nextiword();
            src &= 0xFF;
            regs.sr |= src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_40= new opcode_ptr() { public void handler(long opcode) { /* OR */
    
     /*       ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	WORD dst = regs.d[dstreg];
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_50= new opcode_ptr() { public void handler(long opcode) { /* OR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_58= new opcode_ptr() { public void handler(long opcode) { /* OR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
    {	regs.a[dstreg] += 2;
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_60= new opcode_ptr() { public void handler(long opcode) { /* OR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_68= new opcode_ptr() { public void handler(long opcode) { /* OR */
    
     /*       ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            WORD dst = get_word(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_70= new opcode_ptr() { public void handler(long opcode) { /* OR */
    
      /*      ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	WORD dst = get_word(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_78= new opcode_ptr() { public void handler(long opcode) { /* OR */
    
    /*{{	WORD src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            WORD dst = get_word(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_79= new opcode_ptr() { public void handler(long opcode) { /* OR */
    
    /*{{	WORD src = nextiword();
    {	CPTR dsta = nextilong();
            WORD dst = get_word(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_7c= new opcode_ptr() { public void handler(long opcode) { /* ORSR */
    /*{
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {	MakeSR();
    {	WORD src = nextiword();
            regs.sr |= src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_80= new opcode_ptr() { public void handler(long opcode) { /* OR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	LONG dst = regs.d[dstreg];
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_90= new opcode_ptr() { public void handler(long opcode) { /* OR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_98= new opcode_ptr() { public void handler(long opcode) { /* OR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
    {	regs.a[dstreg] += 4;
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_a0= new opcode_ptr() { public void handler(long opcode) { /* OR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_a8= new opcode_ptr() { public void handler(long opcode) { /* OR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            LONG dst = get_long(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_b0= new opcode_ptr() { public void handler(long opcode) { /* OR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	LONG dst = get_long(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_b8= new opcode_ptr() { public void handler(long opcode) { /* OR */
    /*{
    {{	LONG src = nextilong();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            LONG dst = get_long(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_b9= new opcode_ptr() { public void handler(long opcode) { /* OR */
    /*{
    {{	LONG src = nextilong();
    {	CPTR dsta = nextilong();
            LONG dst = get_long(dsta);
            src |= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_100= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	LONG src = regs.d[srcreg];
    {	LONG dst = regs.d[dstreg];
            src &= 31;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_108= new opcode_ptr() { public void handler(long opcode) { /* MVPMR */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {	CPTR memp = regs.a[srcreg] + nextiword();
    {	UWORD val = (get_byte(memp) << 8) + get_byte(memp + 2);
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((val) & 0xffff);*/
    }};
    static opcode_ptr op_110= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_118= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_120= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_128= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_130= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_138= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_139= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_13a= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg;
            dstreg = 2;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = m68k_getpc();
            dsta += (LONG)(WORD)nextiword();
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_13b= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg;
            dstreg = 3;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = get_disp_ea(m68k_getpc());
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_13c= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
    {{	BYTE src = regs.d[srcreg];
    {	BYTE dst = nextiword();
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_140= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	LONG src = regs.d[srcreg];
    {	LONG dst = regs.d[dstreg];
            src &= 31;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            regs.d[dstreg] = (dst);*/
    }};
    static opcode_ptr op_148= new opcode_ptr() { public void handler(long opcode) { /* MVPMR */
    /*{
            ULONG srcreg = (opcode & 7);
            ULONG dstreg = (opcode >> 9) & 7;
    {	CPTR memp = regs.a[srcreg] + nextiword();
    {	ULONG val = (get_byte(memp) << 24) + (get_byte(memp + 2) << 16)
                                                            + (get_byte(memp + 4) << 8) + get_byte(memp + 6);
            regs.d[dstreg] = (val);*/
    }};
    static opcode_ptr op_150= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_158= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_160= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_168= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_170= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_178= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_179= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_17a= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg;
            dstreg = 2;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = m68k_getpc();
            dsta += (LONG)(WORD)nextiword();
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_17b= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg;
            dstreg = 3;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = get_disp_ea(m68k_getpc());
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_180= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	LONG src = regs.d[srcreg];
    {	LONG dst = regs.d[dstreg];
            src &= 31;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            regs.d[dstreg] = (dst);*/
    }};
    static opcode_ptr op_188= new opcode_ptr() { public void handler(long opcode) { /* MVPRM */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	WORD src = regs.d[srcreg];
            CPTR memp = regs.a[dstreg] + nextiword();
            put_byte(memp, src >> 8); put_byte(memp + 2, src);*/
    }};
    static opcode_ptr op_190= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_198= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1a0= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1a8= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1b0= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1b8= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1b9= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1ba= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg;
            dstreg = 2;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = m68k_getpc();
            dsta += (LONG)(WORD)nextiword();
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1bb= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg;
            dstreg = 3;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = get_disp_ea(m68k_getpc());
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1c0= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	LONG src = regs.d[srcreg];
    {	LONG dst = regs.d[dstreg];
            src &= 31;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            regs.d[dstreg] = (dst);*/
    }};
    static opcode_ptr op_1c8= new opcode_ptr() { public void handler(long opcode) { /* MVPRM */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	LONG src = regs.d[srcreg];
            CPTR memp = regs.a[dstreg] + nextiword();
            put_byte(memp, src >> 24); put_byte(memp + 2, src >> 16);
            put_byte(memp + 4, src >> 8); put_byte(memp + 6, src);*/
    }};
    static opcode_ptr op_1d0= new opcode_ptr() { public void handler(long opcode) { /* BSET */
   /* {
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1d8= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1e0= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1e8= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1f0= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg = opcode & 7;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1f8= new opcode_ptr() { public void handler(long opcode) { /* BSET */
   /* {
            ULONG srcreg = ((opcode >> 9) & 7);
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1f9= new opcode_ptr() { public void handler(long opcode) { /* BSET */
   /* {
            ULONG srcreg = ((opcode >> 9) & 7);
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1fa= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg;
            dstreg = 2;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = m68k_getpc();
            dsta += (LONG)(WORD)nextiword();
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_1fb= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
            ULONG srcreg = ((opcode >> 9) & 7);
            ULONG dstreg;
            dstreg = 3;
    {{	BYTE src = regs.d[srcreg];
    {	CPTR dsta = get_disp_ea(m68k_getpc());
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_200= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	BYTE dst = regs.d[dstreg];
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_210= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_218= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_220= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_228= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_230= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_238= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
    {{	BYTE src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_239= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
    {{	BYTE src = nextiword();
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_23c= new opcode_ptr() { public void handler(long opcode) { /* ANDSR */
    /*{
    {	MakeSR();
    {	WORD src = nextiword();
            src |= 0xFF00;
            regs.sr &= src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_240= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	WORD dst = regs.d[dstreg];
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_250= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_258= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
    {	regs.a[dstreg] += 2;
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_260= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_268= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            WORD dst = get_word(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_270= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	WORD dst = get_word(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_278= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            WORD dst = get_word(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_279= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = nextilong();
            WORD dst = get_word(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_27c= new opcode_ptr() { public void handler(long opcode) { /* ANDSR */
    /*{
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {	MakeSR();
    {	WORD src = nextiword();
            regs.sr &= src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_280= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	LONG dst = regs.d[dstreg];
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_290= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_298= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
    {	regs.a[dstreg] += 4;
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2a0= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2a8= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            LONG dst = get_long(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2b0= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	LONG dst = get_long(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2b8= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
    {{	LONG src = nextilong();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            LONG dst = get_long(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_2b9= new opcode_ptr() { public void handler(long opcode) { /* AND */
    /*{
    {{	LONG src = nextilong();
    {	CPTR dsta = nextilong();
            LONG dst = get_long(dsta);
            src &= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_400= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_410= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_418= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_420= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_428= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_430= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_438= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_439= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_440= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
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
    static opcode_ptr op_450= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
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
    static opcode_ptr op_458= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
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
    static opcode_ptr op_460= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
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
    static opcode_ptr op_468= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
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
    static opcode_ptr op_470= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
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
    static opcode_ptr op_478= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
    {{	WORD src = nextiword();
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
    static opcode_ptr op_479= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
    {{	WORD src = nextiword();
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
    static opcode_ptr op_480= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
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
    static opcode_ptr op_490= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
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
    static opcode_ptr op_498= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
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
    static opcode_ptr op_4a0= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
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
    static opcode_ptr op_4a8= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
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
    static opcode_ptr op_4b0= new opcode_ptr() { public void handler(long opcode) { /* SUB */
   /* {
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
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
    static opcode_ptr op_4b8= new opcode_ptr() { public void handler(long opcode) { /* SUB */
   /* {
    {{	LONG src = nextilong();
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
    static opcode_ptr op_4b9= new opcode_ptr() { public void handler(long opcode) { /* SUB */
    /*{
    {{	LONG src = nextilong();
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
    static opcode_ptr op_600= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_610= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_618= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_620= new opcode_ptr() { public void handler(long opcode) { /* ADD */
   /* {
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_628= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_630= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_638= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_639= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
    {{	BYTE src = nextiword();
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
    static opcode_ptr op_640= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
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
    static opcode_ptr op_650= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
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
    static opcode_ptr op_658= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
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
    static opcode_ptr op_660= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
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
    static opcode_ptr op_668= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
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
    static opcode_ptr op_670= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
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
    static opcode_ptr op_678= new opcode_ptr() { public void handler(long opcode) { /* ADD */
   /* {
    {{	WORD src = nextiword();
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
    static opcode_ptr op_679= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
    {{	WORD src = nextiword();
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
    static opcode_ptr op_680= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
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
    static opcode_ptr op_690= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
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
    static opcode_ptr op_698= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
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
    static opcode_ptr op_6a0= new opcode_ptr() { public void handler(long opcode) { /* ADD */
   /* {
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
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
    static opcode_ptr op_6a8= new opcode_ptr() { public void handler(long opcode) { /* ADD */
   /* {
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
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
    static opcode_ptr op_6b0= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
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
    static opcode_ptr op_6b8= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
    {{	LONG src = nextilong();
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
    static opcode_ptr op_6b9= new opcode_ptr() { public void handler(long opcode) { /* ADD */
    /*{
    {{	LONG src = nextilong();
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
    static opcode_ptr op_800= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	LONG dst = regs.d[dstreg];
            src &= 31;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_810= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_818= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_820= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_828= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_830= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_838= new opcode_ptr() { public void handler(long opcode) { /* BTST */
   /* {
    {{	WORD src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_839= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_83a= new opcode_ptr() { public void handler(long opcode) { /* BTST */
   /* {
            ULONG dstreg;
            dstreg = 2;
    {{	WORD src = nextiword();
    {	CPTR dsta = m68k_getpc();
            dsta += (LONG)(WORD)nextiword();
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_83b= new opcode_ptr() { public void handler(long opcode) { /* BTST */
   /* {
            ULONG dstreg;
            dstreg = 3;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(m68k_getpc());
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_83c= new opcode_ptr() { public void handler(long opcode) { /* BTST */
    /*{
    {{	WORD src = nextiword();
    {	BYTE dst = nextiword();
            src &= 7;
            ZFLG = !(dst & (1 << src));*/
    }};
    static opcode_ptr op_840= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	LONG dst = regs.d[dstreg];
            src &= 31;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            regs.d[dstreg] = (dst);*/
    }};
    static opcode_ptr op_850= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_858= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_860= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_868= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_870= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_878= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_879= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_87a= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG dstreg;
            dstreg = 2;
    {{	WORD src = nextiword();
    {	CPTR dsta = m68k_getpc();
            dsta += (LONG)(WORD)nextiword();
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_87b= new opcode_ptr() { public void handler(long opcode) { /* BCHG */
    /*{
            ULONG dstreg;
            dstreg = 3;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(m68k_getpc());
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst ^= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_880= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	LONG dst = regs.d[dstreg];
            src &= 31;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            regs.d[dstreg] = (dst);*/
    }};
    static opcode_ptr op_890= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_898= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8a0= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8a8= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8b0= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8b8= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8b9= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8ba= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
    /*{
            ULONG dstreg;
            dstreg = 2;
    {{	WORD src = nextiword();
    {	CPTR dsta = m68k_getpc();
            dsta += (LONG)(WORD)nextiword();
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8bb= new opcode_ptr() { public void handler(long opcode) { /* BCLR */
   /* {
            ULONG dstreg;
            dstreg = 3;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(m68k_getpc());
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst &= ~(1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8c0= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	LONG dst = regs.d[dstreg];
            src &= 31;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            regs.d[dstreg] = (dst);*/
    }};
    static opcode_ptr op_8d0= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8d8= new opcode_ptr() { public void handler(long opcode) { /* BSET */
   /* {
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8e0= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8e8= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8f0= new opcode_ptr() { public void handler(long opcode) { /* BSET */
   /* {
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8f8= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8f9= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8fa= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
            ULONG dstreg;
            dstreg = 2;
    {{	WORD src = nextiword();
    {	CPTR dsta = m68k_getpc();
            dsta += (LONG)(WORD)nextiword();
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_8fb= new opcode_ptr() { public void handler(long opcode) { /* BSET */
    /*{
            ULONG dstreg;
            dstreg = 3;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(m68k_getpc());
    {	BYTE dst = get_byte(dsta);
            src &= 7;
            ZFLG = !(dst & (1 << src));
            dst |= (1 << src);
            put_byte(dsta,dst);*/
    }};
    static opcode_ptr op_a00= new opcode_ptr() { public void handler(long opcode) { /* EOR */
   /* {
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	BYTE dst = regs.d[dstreg];
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xff) | ((src) & 0xff);*/
    }};
    static opcode_ptr op_a10= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_a18= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {	regs.a[dstreg] += areg_byteinc[dstreg];
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_a20= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_a28= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_a30= new opcode_ptr() { public void handler(long opcode) { /* EOR */
   /* {
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_a38= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
    {{	BYTE src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_a39= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
    {{	BYTE src = nextiword();
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((BYTE)(src)) == 0;
            NFLG = ((BYTE)(src)) < 0;
            put_byte(dsta,src);*/
    }};
    static opcode_ptr op_a3c= new opcode_ptr() { public void handler(long opcode) { /* EORSR */
    /*{
    {	MakeSR();
    {	WORD src = nextiword();
            src &= 0xFF;
            regs.sr ^= src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_a40= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	WORD dst = regs.d[dstreg];
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            regs.d[dstreg] = (regs.d[dstreg] & ~0xffff) | ((src) & 0xffff);*/
    }};
    static opcode_ptr op_a50= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_a58= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
    {	regs.a[dstreg] += 2;
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_a60= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_a68= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            WORD dst = get_word(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_a70= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	WORD dst = get_word(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_a78= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            WORD dst = get_word(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_a79= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = nextilong();
            WORD dst = get_word(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((WORD)(src)) == 0;
            NFLG = ((WORD)(src)) < 0;
            put_word(dsta,src);*/
    }};
    static opcode_ptr op_a7c= new opcode_ptr() { public void handler(long opcode) { /* EORSR */
    /*{
    {if (!regs.s) { regs.pc -= 2; Exception(8,0); } else
    {	MakeSR();
    {	WORD src = nextiword();
            regs.sr ^= src;
            MakeFromSR();*/
    }};
    static opcode_ptr op_a80= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	LONG dst = regs.d[dstreg];
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            regs.d[dstreg] = (src);*/
    }};
    static opcode_ptr op_a90= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_a98= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
    {	regs.a[dstreg] += 4;
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_aa0= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_aa8= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            LONG dst = get_long(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_ab0= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	LONG dst = get_long(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_ab8= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
    {{	LONG src = nextilong();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            LONG dst = get_long(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_ab9= new opcode_ptr() { public void handler(long opcode) { /* EOR */
    /*{
    {{	LONG src = nextilong();
    {	CPTR dsta = nextilong();
            LONG dst = get_long(dsta);
            src ^= dst;
            VFLG = CFLG = 0;
            ZFLG = ((LONG)(src)) == 0;
            NFLG = ((LONG)(src)) < 0;
            put_long(dsta,src);*/
    }};
    static opcode_ptr op_c00= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	BYTE dst = regs.d[dstreg];
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c10= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c18= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {	regs.a[dstreg] += areg_byteinc[dstreg];
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c20= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	regs.a[dstreg] -= areg_byteinc[dstreg];
    {	CPTR dsta = regs.a[dstreg];
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c28= new opcode_ptr() { public void handler(long opcode) { /* CMP */
   /* {
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c30= new opcode_ptr() { public void handler(long opcode) { /* CMP */
   /* {
            ULONG dstreg = opcode & 7;
    {{	BYTE src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c38= new opcode_ptr() { public void handler(long opcode) { /* CMP */
   /* {
    {{	BYTE src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c39= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
    {{	BYTE src = nextiword();
    {	CPTR dsta = nextilong();
            BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c3a= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg;
            dstreg = 2;
    {{	BYTE src = nextiword();
    {	CPTR dsta = m68k_getpc();
            dsta += (LONG)(WORD)nextiword();
    {	BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c3b= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg;
            dstreg = 3;
    {{	BYTE src = nextiword();
    {	CPTR dsta = get_disp_ea(m68k_getpc());
    {	BYTE dst = get_byte(dsta);
    {{ULONG newv = ((BYTE)(dst)) - ((BYTE)(src));
    {	int flgs = ((BYTE)(src)) < 0;
            int flgo = ((BYTE)(dst)) < 0;
            int flgn = ((BYTE)(newv)) < 0;
            ZFLG = ((BYTE)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UBYTE)(src)) > ((UBYTE)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c40= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	WORD dst = regs.d[dstreg];
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c50= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c58= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
    {	regs.a[dstreg] += 2;
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c60= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	regs.a[dstreg] -= 2;
    {	CPTR dsta = regs.a[dstreg];
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c68= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c70= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c78= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c79= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
    {{	WORD src = nextiword();
    {	CPTR dsta = nextilong();
            WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c7a= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg;
            dstreg = 2;
    {{	WORD src = nextiword();
    {	CPTR dsta = m68k_getpc();
            dsta += (LONG)(WORD)nextiword();
    {	WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c7b= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg;
            dstreg = 3;
    {{	WORD src = nextiword();
    {	CPTR dsta = get_disp_ea(m68k_getpc());
    {	WORD dst = get_word(dsta);
    {{ULONG newv = ((WORD)(dst)) - ((WORD)(src));
    {	int flgs = ((WORD)(src)) < 0;
            int flgo = ((WORD)(dst)) < 0;
            int flgn = ((WORD)(newv)) < 0;
            ZFLG = ((WORD)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((UWORD)(src)) > ((UWORD)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c80= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	LONG dst = regs.d[dstreg];
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c90= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_c98= new opcode_ptr() { public void handler(long opcode) { /* CMP */
   /* {
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
    {	regs.a[dstreg] += 4;
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_ca0= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	regs.a[dstreg] -= 4;
    {	CPTR dsta = regs.a[dstreg];
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_ca8= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = regs.a[dstreg] + (LONG)(WORD)nextiword();
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_cb0= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg = opcode & 7;
    {{	LONG src = nextilong();
    {	CPTR dsta = get_disp_ea(regs.a[dstreg]);
    {	LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_cb8= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
    {{	LONG src = nextilong();
    {	CPTR dsta = (LONG)(WORD)nextiword();
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_cb9= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
    {{	LONG src = nextilong();
    {	CPTR dsta = nextilong();
            LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_cba= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg;
            dstreg = 2;
    {{	LONG src = nextilong();
    {	CPTR dsta = m68k_getpc();
            dsta += (LONG)(WORD)nextiword();
    {	LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;*/
    }};
    static opcode_ptr op_cbb= new opcode_ptr() { public void handler(long opcode) { /* CMP */
    /*{
            ULONG dstreg;
            dstreg = 3;
    {{	LONG src = nextilong();
    {	CPTR dsta = get_disp_ea(m68k_getpc());
    {	LONG dst = get_long(dsta);
    {{ULONG newv = ((LONG)(dst)) - ((LONG)(src));
    {	int flgs = ((LONG)(src)) < 0;
            int flgo = ((LONG)(dst)) < 0;
            int flgn = ((LONG)(newv)) < 0;
            ZFLG = ((LONG)(newv)) == 0;
            VFLG = (flgs != flgo) && (flgn != flgo);
            CFLG = ((ULONG)(src)) > ((ULONG)(dst));
            NFLG = flgn != 0;*/
    }};
    
}
