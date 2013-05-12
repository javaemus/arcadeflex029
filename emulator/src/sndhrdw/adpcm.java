/*
This file is part of Arcadeflex.

Arcadeflex is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Arcadeflex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Arcadeflex.  If not, see <http://www.gnu.org/licenses/>.
 */

package sndhrdw;

/**
 *
 * @author shadow
 */
public class adpcm {
     /* step size index shift table */
    static int indsft[]={-1, -1, -1, -1, 2, 4, 6, 8};

    /* step size table:  calculated as   stpsz[i]=floor[16*(11/10)^i] */
    static int stpsz[]= {16,17,19,21,23,25,28,31,34,37,41,45,50,55,60,66,
                    73,80,88,97,107,118,130,143,157,173,190,209,230,
                    253,279,307,337,371,408,449,494,544,598,658,724,
                    796,876,963,1060,1166,1282,1411,1552};

    /* nibble to bit map */
    static int nbl2bit[][]={{1,0,0,0},{1,0,0,1},{1,0,1,0},{1,0,1,1},
                        {1,1,0,0},{1,1,0,1},{1,1,1,0},{1,1,1,1},
                        {-1,0,0,0},{-1,0,0,1},{-1,0,1,0},{-1,0,1,1},
                        {-1,1,0,0},{-1,1,0,1},{-1,1,1,0},{-1,1,1,1}};

    /* step size index */
    static int ssindex=0;

    /* the current adpcm signalf */
    static int signalf=-2;



    static void InitDecoder()
    {
        ssindex = 0;
        signalf = -2;
    }



    /*
     *   This function perform ADPCM decode of a given Input block
     *   It reads in input an ADPCM nibble and return decoded difference
     */

    static int DecodeAdpcm(char encoded)
    {
        int diff,step;

        step = stpsz[ssindex];
        diff = nbl2bit[encoded][0]*(step*nbl2bit[encoded][1]+
                                   (step/2)*nbl2bit[encoded][2]+
                                   (step/4)*nbl2bit[encoded][3]+
                                   (step/8));

        ssindex += indsft[(encoded%8)];
        if(ssindex<0) ssindex=0;      /* clip step index */
        if(ssindex>48) ssindex=48;
        return(diff);
    }
   
}
