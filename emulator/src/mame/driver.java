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

//TODO finish porting to 0.29

/*
 * ported to v0.28
 * ported to v0.27
 *
 *
 *   commented drivers are the TODO drivers
 */
package mame;
import static mame.driverH.*;
import static drivers.pepper2.*;
import static drivers.mtrap.*;
import static drivers.venture.*;
import static drivers.dkong.*;



public class driver {
	public static GameDriver drivers[] =
	{
            pepper2_driver,
            mtrap_driver,
            venture_driver,
            dkong_driver,
            dkongjp_driver,
            dkongjr_driver,
            dkong3_driver,  
            dkjrjp_driver,
            dkjrbl_driver,
            radarscp_driver, //freezes (not working in 0.29 either)
            null	/* end of array */
        };
}
