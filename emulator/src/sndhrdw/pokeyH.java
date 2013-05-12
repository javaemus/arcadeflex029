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
public class pokeyH {
    
    /* CONSTANT DEFINITIONS */

    /* POKEY WRITE LOGICALS */
    /* Note: only 0x00 - 0x09, part of 0x0f are emulated by POKEYSND */
    public static final int AUDF1_C=     0x00;
    public static final int AUDC1_C=     0x01;
    public static final int AUDF2_C=     0x02;
    public static final int AUDC2_C=     0x03;
    public static final int AUDF3_C=     0x04;
    public static final int AUDC3_C=     0x05;
    public static final int AUDF4_C=     0x06;
    public static final int AUDC4_C=     0x07;
    public static final int AUDCTL_C=    0x08;
    public static final int STIMER_C=    0x09;
    public static final int SKREST_C=    0x0A;
    public static final int POTGO_C=     0x0B;
    public static final int SEROUT_C=    0x0D;
    public static final int IRQEN_C=     0x0E;
    public static final int SKCTL_C=     0x0F;

    /* POKEY READ LOGICALS */
    public static final int POT0_C=      0x00;
    public static final int POT1_C=      0x01;
    public static final int POT2_C=      0x02;
    public static final int POT3_C=      0x03;
    public static final int POT4_C=      0x04;
    public static final int POT5_C=      0x05;
    public static final int POT6_C=      0x06;
    public static final int POT7_C=      0x07;
    public static final int ALLPOT_C=    0x08;
    public static final int KBCODE_C=    0x09;
    public static final int RANDOM_C=    0x0A;
    public static final int SERIN_C=     0x0D;
    public static final int IRQST_C=     0x0E;
    public static final int SKSTAT_C=    0x0F;


/* As an alternative to using the exact frequencies, selecting a playback
   frequency that is an exact division of the main clock provides a higher
   quality output due to less aliasing.  For best results, a value of
   1787520 MHz is used for the main clock.  With this value, both the
   64 kHz and 15 kHz clocks are evenly divisible.  Selecting a playback
   frequency that is also a division of the clock provides the best
   results.  The best options are FREQ_64 divided by either 2, 3, or 4.
   The best selection is based on a trade off between performance and
   sound quality.

   Of course, using a main clock frequency that is not exact will affect
   the pitch of the output.  With these numbers, the pitch will be low
   by 0.127%.  (More than likely, an actual unit will vary by this much!) */

    public static final int FREQ_17_EXACT=     1789790;  /* exact 1.79 MHz clock freq */
    public static final int FREQ_17_APPROX=    1787520;  /* approximate 1.79 MHz clock freq */

    public static final int MAXPOKEYS=         4;        /* max number of emulated chips */
}
