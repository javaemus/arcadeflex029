/*
 * 
 * ported to v0.31
 */
package machine;


public class system8H {
    public static final int SPR_Y_TOP=		0;
    public static final int SPR_Y_BOTTOM=	1;
    public static final int SPR_X_LO=		2;
    public static final int SPR_X_HI=		3;
    public static final int SPR_BANK=		3;
    public static final int SPR_WIDTH_LO=	4;
    public static final int SPR_WIDTH_HI=	5;
    public static final int SPR_GFXOFS_LO=	6;
    public static final int SPR_GFXOFS_HI=	7;
    public static final int SPR_FLIP_X	=	7;

    public static final int SYSTEM8_NO_SPRITEBANKS=            0;
    public static final int SYSTEM8_SUPPORTS_SPRITEBANKS=	1;  
}
