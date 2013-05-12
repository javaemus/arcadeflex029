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
 * 
 * 
 *  Dummy implementation...
 * 
 */
package sndhrdw;

import static mame.cpuintrf.*;
import static mame.driverH.*;
import static sndhrdw.generic.*;
import static sndhrdw.pokey.*;
import static sndhrdw.pokeyH.*;
import static mame.mame.*;
public class pokeyintf 
{
    public static final int  emulation_rate= (FREQ_17_APPROX / (28*4));
    //public static int  buffer_len= (emulation_rate/Machine.drv.frames_per_second);

    static int numpokeys; /* Number of pokeys used by the driver, also used for clipping */
                                              /* For best results, should be power of 2 */

        public static ShStartPtr pokey1_sh_start = new ShStartPtr() { public int handler()
	{
            numpokeys = 1;

            Pokey_sound_init (FREQ_17_APPROX,emulation_rate,numpokeys);
            return 0;
        }};

        public static ShStartPtr pokey2_sh_start = new ShStartPtr() { public int handler()
	{
            numpokeys = 2;

            Pokey_sound_init (FREQ_17_APPROX,emulation_rate,numpokeys);
            return 0;
        }};

        public static ShStartPtr pokey4_sh_start = new ShStartPtr() { public int handler()
	{
            numpokeys = 4;

            Pokey_sound_init (FREQ_17_APPROX,emulation_rate,numpokeys);
            return 0;
        }};

        public static ShStopPtr pokey_sh_stop = new ShStopPtr() { public void handler()
	{
        }};

        public static ReadHandlerPtr pokey1_r = new ReadHandlerPtr() { public int handler(int offset)
	{
                return Read_pokey_regs (offset,0);
        }};

        public static ReadHandlerPtr pokey2_r = new ReadHandlerPtr() { public int handler(int offset)
	{
                return Read_pokey_regs (offset,1);
        }};

        public static ReadHandlerPtr pokey3_r = new ReadHandlerPtr() { public int handler(int offset)
	{
                return Read_pokey_regs (offset,2);
        }};

        public static ReadHandlerPtr pokey4_r = new ReadHandlerPtr() { public int handler(int offset)
	{
                return Read_pokey_regs (offset,3);
        }};

        public static WriteHandlerPtr pokey1_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
            Update_pokey_sound (offset,data,0,16/numpokeys);
        }};

        public static WriteHandlerPtr pokey2_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
            Update_pokey_sound (offset,data,1,16/numpokeys);
        }};

        public static WriteHandlerPtr pokey3_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
            Update_pokey_sound (offset,data,2,16/numpokeys);
        }};

        public static WriteHandlerPtr pokey4_w = new WriteHandlerPtr() {	public void handler(int offset, int data)
	{
            Update_pokey_sound (offset,data,3,16/numpokeys);
        }};


        public static ShUpdatePtr pokey_sh_update = new ShUpdatePtr() { public void handler()
	{
        }};   
}
