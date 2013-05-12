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
 * unchanged for 0.29
 * ported to v0.27
 *
 *
 *
 */
package mame;

/**
 *
 * @author shadow
 */
public class usrintrfH {
  public static final int DT_COLOR_WHITE = 0;
  public static final int DT_COLOR_YELLOW = 1;
  public static final int DT_COLOR_RED = 2;

	public static class DisplayText
	{
		public DisplayText() {};
		public DisplayText(String t, int c, int _x, int _y) { text = t; color = c; x = _x; y = _y; };
		public DisplayText(int z) { this(null, 0, 0, 0); };
		public static DisplayText[] create(int n)
		{ DisplayText []a = new DisplayText[n];	for(int k = 0; k < n; k++) a[k] = new DisplayText(); return a; }
		public String text;	/* 0 marks the end of the array */
		public int color;
		public int x;
		public int y;
	};
}
