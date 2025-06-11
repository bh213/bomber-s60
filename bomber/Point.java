/*  Bomber for Nokia Series 60 Phones
    Copyright (C) 2003, 2004  While True, d.o.o.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
	
    For any info contact gorazd@whiletrue.com.
*/

/*==========================================================================;
 *
 *  While True, d.o.o.
 *	
 *  File:       point.java
 *  Content:    Point class
 *  Created:    October 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/


package bomber;


// =========================================================================;
//	Name:	class Point
//
// ==========================================================================;

public class Point
{
    public int  x;
    public int  y;
    public Point(int nx, int ny)
    {
        set(nx, ny);
    }

    public Point()
    {
        // x and y are set to 0 automatically
    }

    public void add(int nx, int ny)
    {
        x += nx;
        y += ny;
    }

    public void sub(int nx, int ny)
    {
        x -= nx;
        y -= ny;
    }
    
    public void mul(int alp)
    {
        x = Common.mul(x, alp);
        y = Common.mul(y, alp);
    }
    
    public void set(int nx, int ny)
    {
        x = nx;
        y = ny;
    }
    
    public void set(Point p)
    {
        x = p.x;
        y = p.y;
    }
    
    public int distance(int px, int py)
    {
        return Common.sqrt(Common.sqr(x - px) + Common.sqr(y - py));
    }
    
    public int distance(Point p)
    {
        return distance(p.x, p.y);
    }
    
    public void swap()       // swaps x and y coordinates
    {
        int t = x;
        x = y;
        y = t;
    }
}
