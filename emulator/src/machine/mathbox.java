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

/*
 *  ported to v0.28
 *  ported to v0.27
 */
package machine;

import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.mame.*;


public class mathbox
{

 
    static short mb_reg[] = new short[16];/* math box scratch registers */
    static short mb_result = 0; /* math box result */
    
    public static WriteHandlerPtr mb_go = new WriteHandlerPtr() {

        public void handler(int addr, int data)
        {
              int mb_temp;  /* temp 32-bit multiply results */
              short mb_q=0;     /* temp used in division */
              int msb;
             // if (errorlog!=null) fprintf (errorlog, "math box command %02x data %02x  ", addr, data);
            switch(addr)
            {
                    case 0x00: mb_result = mb_reg[0] = (short)((mb_reg[0] & 0xff00) | data);        break;
                    case 0x01: mb_result = mb_reg[0] = (short)((mb_reg[0] & 0x00ff) | (data << 8)); break;
                    case 0x02: mb_result = mb_reg[1] = (short)((mb_reg[1] & 0xff00) | data);        break;
                    case 0x03: mb_result = mb_reg[1] = (short)((mb_reg[1] & 0x00ff) | (data << 8)); break;
                    case 0x04: mb_result = mb_reg[2] = (short)((mb_reg[2] & 0xff00) | data);        break;
                    case 0x05: mb_result = mb_reg[2] = (short)((mb_reg[2] & 0x00ff) | (data << 8)); break;
                    case 0x06: mb_result = mb_reg[3] = (short)((mb_reg[3] & 0xff00) | data);        break;
                    case 0x07: mb_result = mb_reg[3] = (short)((mb_reg[3] & 0x00ff) | (data << 8)); break;
                    case 0x08: mb_result = mb_reg[4] = (short)((mb_reg[4] & 0xff00) | data);        break;
                    case 0x09: mb_result = mb_reg[4] = (short)((mb_reg[4] & 0x00ff) | (data << 8)); break;
                    case 0x0a: mb_result = mb_reg[5] = (short)((mb_reg[5] & 0xff00) | data);        break;
                    /* note: no function loads low part of REG5 without performing a computation */
                    case 0x0c: mb_result = mb_reg[6] = (short)data; break;
                      /* note: no function loads high part of REG6 */

                    case 0x15: mb_result = mb_reg[7] = (short)((mb_reg[7] & 0xff00) | data);        break;
                    case 0x16: mb_result = mb_reg[7] = (short)((mb_reg[7] & 0x00ff) | (data << 8)); break;

                    case 0x1a: mb_result = mb_reg[8] = (short)((mb_reg[8] & 0xff00) | data);        break;
                    case 0x1b: mb_result = mb_reg[8] = (short)((mb_reg[8] & 0x00ff) | (data << 8)); break;

                    case 0x0d: mb_result = mb_reg[10] = (short)((mb_reg[10]& 0xff00) | data);         break;
                    case 0x0e: mb_result = mb_reg[10] = (short)((mb_reg[10] & 0x00ff) | (data << 8)); break;
                    case 0x0f: mb_result = mb_reg[11] = (short)((mb_reg[11]& 0xff00) | data);         break;
                    case 0x10: mb_result = mb_reg[11] = (short)((mb_reg[11] & 0x00ff) | (data << 8)); break;

                    case 0x17: mb_result = mb_reg[7]; break;
                    case 0x19: mb_result = mb_reg[8]; break;
                    case 0x18: mb_result = mb_reg[9]; break;


            case 0x11:
              mb_reg[5] = (short)((mb_reg[5] & 0x00ff) | (data << 8)); 
              mb_reg[15] = 0x0000;  /* do everything in one step */
              //goto step_048;
              //break; //remove break so it can execute step_048 as well
            case 0x0b:
              if(addr==0x0b)//execute this only if it's 0x0b not when coming from 0x11 case
              {
                  mb_reg[5] = (short)((mb_reg[5] & 0x00ff) | (data << 8)); 

                  mb_reg[15] = (short)(0xffff);
                  mb_reg[4] -= mb_reg[2];
                  mb_reg[5] -= mb_reg[3];
              }

            //step_048:

              mb_temp = mb_reg[0] * mb_reg[4];
              mb_reg[12] = (short)(mb_temp >> 16);
              mb_reg[14] = (short)(mb_temp & 0xffff);

              mb_temp = -mb_reg[1] * mb_reg[5];
              mb_reg[7] = (short)(mb_temp >> 16);
              mb_q = (short)(mb_temp & 0xffff);

              mb_reg[7] += mb_reg[12];

              /* rounding */
              mb_reg[14] = (short)((mb_reg[14] >> 1) & 0x7fff);
              mb_reg[12] = (short)((mb_q >> 1) & 0x7fff);
              mb_q = (short)(mb_reg[12] + mb_reg[14]);
              if (mb_q < 0)
                mb_reg[7]++;

              mb_result = mb_reg[7];

              if (mb_reg[15] < 0)
              {
                break;
              }
              mb_reg[7] += mb_reg[2];

               /* fall into command 12 */
           
                case 0x12:

                  mb_temp = mb_reg[1] * mb_reg[4];
                  mb_reg[12] = (short)(mb_temp >> 16);
                  mb_reg[9] = (short)(mb_temp & 0xffff);

                  mb_temp = mb_reg[0] * mb_reg[5];
                  mb_reg[8] = (short)(mb_temp >> 16);
                  mb_q = (short)(mb_temp & 0xffff);

                  mb_reg[8] += mb_reg[12];

                  /* rounding */
                  mb_reg[9] = (short)((mb_reg[9] >> 1) & 0x7fff);
                  mb_reg[12] = (short)((mb_q >> 1) & 0x7fff);
                  mb_reg[9] += mb_reg[12];
                  if (mb_reg[9] < 0)
                  {
                      mb_reg[8]++;
                  }
                    
                  mb_reg[9] <<= 1;  /* why? only to get the desired load address? */

                  mb_result = mb_reg[8];

                  if (mb_reg[15] < 0)
                  {
                      break;
                  }
                  mb_reg[8] += mb_reg[3];

                  mb_reg[9] &= 0xff00;

                  /* fall into command 13 */         

            case 0x13: 
                 //if (errorlog!=null) fprintf (errorlog, "\nR7: %04x  R8: %04x  R9: %04x\n", REG7, REG8, REG9);
                  mb_reg[12] = mb_reg[9];
                  mb_q = mb_reg[8];
                  // goto step_0bf;
            case 0x14: 
                if(addr==0x14)//execute only if addr=0x14 not 0x13
                {
                    mb_reg[12] = mb_reg[10];
                    mb_q = mb_reg[11];
                }        
                //step_0bf:
                  mb_reg[14] = (short)(mb_reg[7] ^ mb_q);  /* save sign of result */
                  mb_reg[13] = mb_q;
                  if (mb_q >= 0)
                    mb_q = mb_reg[12];
                  else
                    {
                      mb_reg[13] = (short)(- mb_q - 1);
                      mb_q = (short)(- mb_reg[12] - 1);
                      if ((mb_q < 0) && ((mb_q + 1) < 0))
                        mb_reg[13]++;
                      mb_q++;
                    }

                /* step 0c9: */
                  /* REGc = abs (REG7) */
                  if (mb_reg[7] >= 0)
                    mb_reg[12] = mb_reg[7];
                  else
                    mb_reg[12] = (short)(-mb_reg[7]);

                  mb_reg[15] = mb_reg[6];  /* step counter */

                  do
                    {
                      mb_reg[13] -= mb_reg[12];
                      msb = BOOL((mb_q & 0x8000) != 0);
                      mb_q <<= 1;
                      if (mb_reg[13] >= 0)
                        mb_q++;
                      else
                        mb_reg[13] += mb_reg[12];
                      mb_reg[13]<<= 1;
                      mb_reg[13] += msb;
                    }
                  while (--mb_reg[15] >= 0);

                  if (mb_reg[14]>= 0)
                    mb_result = mb_q;
                  else
                    mb_result = (short)(- mb_q);
                  break;

            case 0x1C: 
                  /* window test? */
                  mb_reg[5] = (short)((mb_reg[5] & 0x00ff) | (data << 8)); 
                  do
                    {
                      mb_reg[14] = (short)((mb_reg[4]+ mb_reg[7]) >> 1);
                      mb_reg[15] = (short)((mb_reg[5] + mb_reg[8]) >> 1);
                      if ((mb_reg[11]  < mb_reg[14] ) && (mb_reg[15]  < mb_reg[14] ) && ((mb_reg[14]  + mb_reg[15] ) >= 0))
                      { 
                          mb_reg[7] = mb_reg[14]; 
                        mb_reg[8] = mb_reg[15]; 
                      }
                      else 
                      { 
                          mb_reg[4] = mb_reg[14];
                          mb_reg[5] = mb_reg[15]; 
                      
                      }
                    }
                  while (--mb_reg[6] >= 0);

                  mb_result = mb_reg[8];
                  break;
            case 0x1D: 
                  mb_reg[3] = (short)((mb_reg[3] & 0x00ff) | (data << 8));

                  mb_reg[2] -= mb_reg[0];
                  if (mb_reg[2]< 0)
                    mb_reg[2] = (short)(-mb_reg[2]);

                  mb_reg[3] -= mb_reg[1];
                  if (mb_reg[3] < 0)
                    mb_reg[3] = (short)(-mb_reg[3]);

                  /* fall into command 1e */
            case 0x1E: 
                      /* result = max (REG2, REG3) + 3/8 * min (REG2, REG3) */
                  if (mb_reg[3]  >= mb_reg[2] )
                    { 
                        mb_reg[12] = mb_reg[2];
                        mb_reg[13] = mb_reg[3];
                    }
                  else
                    { 
                        mb_reg[13] = mb_reg[2];
                        mb_reg[12] = mb_reg[3];
                    }
                  mb_reg[12] >>= 2;
                  mb_reg[13] += mb_reg[12];
                  mb_reg[12] >>= 1;
                  mb_result = mb_reg[13] = (short)((mb_reg[12]  + mb_reg[13] ));
                  break;
   

            case 0x1F: 
                if(errorlog != null)
                {
                    fprintf(errorlog, "math box function 0x1f\n", new Object[0]);
                    /* $$$ do some computation here (selftest? signature analysis? */
                }
                break;
            }
        }

    };
    public static ReadHandlerPtr mb_status_r = new ReadHandlerPtr() {

        public int handler(int offset)
        {
            return 0x00; /* always done! */
        }

    };
    public static ReadHandlerPtr mb_lo_r = new ReadHandlerPtr() {

        public int handler(int offset)
        {
            return mb_result & 0xff;
        }

    };
    public static ReadHandlerPtr mb_hi_r = new ReadHandlerPtr() {

        public int handler(int offset)
        {
            return (mb_result >> 8) & 0xff;
        }

    };

}

