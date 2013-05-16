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
 * ported to v0.28
 * ported to v0.27
 *
 *
 *
 */

package machine;

import static arcadeflex.libc.*;
import static mame.driverH.*;
import static mame.cpuintrf.*;
import static mame.mame.*;
import static vidhrdw.vector.*;
import static mame.memoryH.*;

public class atari_vg {
    static int earom_offset;
    static int earom_data;
    static char[] earom=new char[64];

        public static WriteHandlerPtr atari_vg_go = new WriteHandlerPtr() { public void handler(int offset, int data)
	{
            vg_go(cpu_gettotalcycles());
        }};

        public static ReadHandlerPtr  atari_vg_earom_r= new ReadHandlerPtr() { public int handler(int offset)
        {
            if (errorlog!=null) 
                    fprintf (errorlog, "read earom: %d\n",new Object[] { Integer.valueOf(offset)});
            return (earom_data);
        }};
    public static WriteHandlerPtr atari_vg_earom_w = new WriteHandlerPtr() { public void handler(int offset, int data)
    {

            if (errorlog!=null)
                    fprintf (errorlog, "write earom: %d:%d\n",new Object[] { Integer.valueOf(offset), Integer.valueOf(data)});
            earom_offset=offset;
            earom_data=data;
    }};

    /* 0,8 and 14 get written to this location, too.
     * Don't know what they do exactly 
     */
    public static WriteHandlerPtr atari_vg_earom_ctrl = new WriteHandlerPtr() { public void handler(int offset, int data)
    {
            if (errorlog!=null)
                    fprintf (errorlog, "earom ctrl: %d:%d\n",new Object[] { Integer.valueOf(offset), Integer.valueOf(data)});

            if (data == 9)
                    earom_data=earom[earom_offset];
            if (data == 12)
                    earom[earom_offset]=(char)earom_data;
    }};

    public static HiscoreLoadPtr atari_vg_earom_load = new HiscoreLoadPtr() { public int handler(String name)
    {
            /* We read the EAROM contents from disk */
            /* No check necessary */
            FILE f;

            if ((f = fopen(name,"rb")) != null)
            {
                    fread(earom,0,1,0x40,f);
                    fclose(f);
            }
            return 1;
    }};
    public static HiscoreSavePtr atari_vg_earom_save = new HiscoreSavePtr() { public void handler(String name)
    {
            FILE f;

            if ((f = fopen(name,"wb")) != null)
            {
                    fwrite(earom,0,1,0x40,f);
                    fclose(f);
            }
    } };   
}
