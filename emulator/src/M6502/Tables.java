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


package M6502;
import static M6502.M6502H.*;
/**
 *
 * @author shadow
 */
public class Tables {

       static int[] Cycles =
       {
          7,6,2,8,3,3,5,5,3,2,2,2,4,4,6,6,
          2,5,2,8,4,4,6,6,2,4,2,7,5,5,7,7,
          6,6,2,8,3,3,5,5,4,2,2,2,4,4,6,6,
          2,5,2,8,4,4,6,6,2,4,2,7,5,5,7,7,
          6,6,2,8,3,3,5,5,3,2,2,2,3,4,6,6,
          2,5,2,8,4,4,6,6,2,4,2,7,5,5,7,7,
          6,6,2,8,3,3,5,5,4,2,2,2,5,4,6,6,
          2,5,2,8,4,4,6,6,2,4,2,7,5,5,7,7,
          2,6,2,6,3,3,3,3,2,2,2,2,4,4,4,4,
          2,6,2,6,4,4,4,4,2,5,2,5,5,5,5,5,
          2,6,2,6,3,3,3,3,2,2,2,2,4,4,4,4,
          2,5,2,5,4,4,4,4,2,4,2,5,4,4,4,4,
          2,6,2,8,3,3,5,5,2,2,2,2,4,4,6,6,
          2,5,2,8,4,4,6,6,2,4,2,7,5,5,7,7,
          2,6,2,8,3,3,5,5,2,2,2,2,4,4,6,6,
          2,5,2,8,4,4,6,6,2,4,2,7,5,5,7,7
        };
      static int[] ZNTable =
      {
      Z_FLAG,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
      N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,N_FLAG,
    };
}
