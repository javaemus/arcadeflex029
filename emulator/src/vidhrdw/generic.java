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
/* ported to v0.29
 * ported to v0.28
 * ported to v0.27
 *
 *
 *
 */

package vidhrdw;

import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static arcadeflex.osdepend.*;

public class generic
{


	public static CharPtr videoram = new CharPtr();
        public static int[] videoram_size = new int[1];
	public static CharPtr colorram = new CharPtr();
	public static CharPtr spriteram = new CharPtr();	/* not used in this module... */
	public static CharPtr spriteram_2 = new CharPtr();
        public static CharPtr spriteram_3 = new CharPtr();
        public static int[] spriteram_size = new int[1];/* ... here just for convenience */
	public static char dirtybuffer[];
	static osd_bitmap tmpbitmap;


	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/

        public static VhStartPtr generic_vh_start = new VhStartPtr() {
            public int handler() 
            {
             
                if (videoram_size[0] == 0)
                {
                    if (errorlog!=null) fprintf(errorlog,"Error: generic_vh_start() called but videoram_size not initialized\n");
                    return 1;
                }
                        
                if ((dirtybuffer = new char[videoram_size[0]]) == null)
                return 1;
                
                memset(dirtybuffer, 1, videoram_size[0]);

              if ((tmpbitmap = osd_create_bitmap(Machine.drv.screen_width, Machine.drv.screen_height)) == null)
              {
                dirtybuffer = null;
                return 1;
              }

              return 0;
            }
          };

		
	/***************************************************************************
	
	  Stop the video hardware emulation.
	
	***************************************************************************/
	public static VhStopPtr generic_vh_stop = new VhStopPtr() { public void handler()
	{
		dirtybuffer = null;
		osd_free_bitmap(tmpbitmap);
		tmpbitmap = null;
	} };	



	public static ReadHandlerPtr videoram_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return videoram.read(offset);
	} };



	public static ReadHandlerPtr colorram_r = new ReadHandlerPtr() { public int handler(int offset)
	{
		return colorram.read(offset);
	} };


	
	public static WriteHandlerPtr videoram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (videoram.read(offset) != data)
		{
			dirtybuffer[offset] = 1;
		
			videoram.write(offset, data);
		}
	} };	



	public static WriteHandlerPtr colorram_w = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
		if (colorram.read(offset) != data)
		{
			dirtybuffer[offset] = 1;
		
			colorram.write(offset, data);
		}
	} };	
}