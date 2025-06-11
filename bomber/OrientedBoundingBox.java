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
 *  File:       OrientedBoundingBox.java
 *  Content:    Oriented Bounding Box class
 *  Created:    October 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;

// =========================================================================;
//	Name:	OrientedBoundingBox
//	Desc:	Class that handles collision detection using
//              oriented bounding boxes
// ==========================================================================;
    
public class OrientedBoundingBox extends BoundingBox
{
    protected int m_angle;     // angle of this box
    protected int m_cos;       // cos of the angle (speed optimization)
    protected int m_sin;       // sin of the angle (speed optimization)
    
    public OrientedBoundingBox(int sx, int sy) 
    {
        super(sx, sy);
    }
    
    private void rotatePoint(int x, int y, Point r)
    {
        r.set(Common.fastMul(m_cos, x) - Common.fastMul(m_sin, y) + m_pos.x ,
              Common.fastMul(m_sin, x) + Common.fastMul(m_cos, y) + m_pos.y) ;
    }
    
    private void rotatePoint(int x, int y, int angle, Point r)
    {
        int cos = Common.cos(angle);
        int sin = Common.sin(angle);
        r.set(Common.fastMul(cos, x) - Common.fastMul(sin, y) + m_pos.x ,
              Common.fastMul(sin, x) + Common.fastMul(cos, y) + m_pos.y) ;
    }
    
    protected void updateBounds()  // WARN ME???
    {
        m_cos = Common.cos(m_angle);
        m_sin = Common.sin(m_angle);
        rotatePoint( - m_center.x,  - m_center.y, m_bounds[0]);
        rotatePoint(m_size.x - m_center.x, - m_center.y, m_bounds[1]);
        rotatePoint(- m_center.x, m_size.y - m_center.y, m_bounds[2]);
        rotatePoint(m_size.x - m_center.x, m_size.y - m_center.y, m_bounds[3]);
    }
    
    public int getAngle()
    {
        return m_angle;
    }

    public void setAngle(int angle)
    {
        m_angle = angle;
        m_angle %= 360 * Common.FIXED;
        if (m_angle < 0) m_angle+= 360 * Common.FIXED;
        updateBounds();
    }
// =========================================================================;
//	Name:	Point inversePointTransform(Point p)
//	Desc:	Transforms point into local coordinate system. This means
//              that the point returned can be used as if the bounding box
//              was not rotated.    
// ==========================================================================;

    protected Point inversePointTransform(Point p)
    {
        Point ret_val = new Point();
        rotatePoint( - m_pos.x + p.x, -m_pos.y + p.y, 360 * Common.FIXED - m_angle, ret_val);
        return ret_val;
    }
    
    public boolean collision(Point p)   // is point in bounding box
    {
        return super.collision(inversePointTransform(p));
    }
    
    public boolean collision(Point p, int r)   // is circle in bounding box
    {
        return super.collision(inversePointTransform(p), r);
    }   
}
