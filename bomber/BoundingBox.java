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
 *  File:       BoundingBox.java
 *  Content:    Bounding Box class
 *  Created:    October 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;

import javax.microedition.lcdui.*;      // WARN ME: for draw only. Comment for release builds

// =========================================================================;
//	Name:	BoundingBox
//	Desc:	Class that handles collision detection using
//              axis aligned bounding boxes
// ==========================================================================;
    
public class BoundingBox
{
    protected Point m_pos;          // position of this bounding box
    protected Point m_size;         // size of this bb.
    protected Point m_center;       // center of this bb.
    protected Point[] m_bounds;     // calculated values of this bb. These are calculated by virtual function.
    protected int    m_radius;     // max radius of this object
    
    public BoundingBox(int sx, int sy) 
    {
        m_pos = new Point();
        m_size = new Point(sx, sy);
        m_center = new Point(sx / 2, sy /2); // default center is in the center of bounding box
        m_bounds = new Point[4];
        for (int i = 0; i < 4; i++) m_bounds[i] = new Point();
        updateBounds();
        updateRadius();
    }

    protected void updateRadius()
    {
        m_radius = Math.max(m_center.distance(0,0), m_center.distance(m_size.x,0));
        m_radius = Math.max(m_radius, m_center.distance(0,m_size.y));
        m_radius = Math.max(m_radius, m_center.distance(m_size.x, m_size.y));
    }
    
    public Point getPos()
    {
        return m_pos;
    }
    
    public Point getSize()
    {
        return m_size;
    }

    public Point getBound(int index)
    {
        return m_bounds[index];
    }
    
    public Point getCenter()
    {
        return m_center;
    }
    
    public int getRadius() // WARN ME: use center in this calculation
    {
        return m_radius;
    }
    
    public void setCenter(int x, int y)
    {
        m_center.set(x, y);
        updateBounds();
        updateRadius();
    }
    
    protected void updateBounds()  // WARN ME???
    {
        m_bounds[0].set(m_pos.x - m_center.x, m_pos.y - m_center.y);
        m_bounds[1].set(m_pos.x + m_size.x - m_center.x, m_pos.y - m_center.y);
        m_bounds[2].set(m_pos.x - m_center.x, m_pos.y  + m_size.y - m_center.y);
        m_bounds[3].set(m_pos.x + m_size.x - m_center.x, m_pos.y + m_size.y - m_center.y);

    }
    

    public void setPos(int x, int y)
    {
        m_pos.x = x;
        m_pos.y = y;
        updateBounds();
    }
    

    
    public boolean fastCollision(BoundingBox bb)
    {
        if (m_pos.distance(bb.getPos()) > getRadius() + bb.getRadius()) return false;
        else return true;
    }
    
    protected boolean performCollision(BoundingBox bb) 
    {
        if (collision(bb.m_bounds[0])) return true;
        if (collision(bb.m_bounds[1])) return true;
        if (collision(bb.m_bounds[2])) return true;
        if (collision(bb.m_bounds[3])) return true;
        return false;
    }
    
    public boolean collision(BoundingBox bb) 
    {
        if (fastCollision(bb) == false) return false; // optimization
        if (performCollision(bb)) return true;
        return bb.performCollision(this);
    }
 
    public boolean collision(Point p)   // is point in bounding box
    {
        if (m_pos.x - m_center.x <= p.x && m_pos.x - m_center.x + m_size.x >= p.x &&
            m_pos.y - m_center.y <= p.y && m_pos.y - m_center.y + m_size.y >= p.y) return true;
        else return false;
    }
    
    public boolean collision(Point p, int r)   // is circle in bounding box
    {
        if (m_pos.x - m_center.x - r <= p.x && m_pos.x - m_center.x + m_size.x + r>= p.x &&
            m_pos.y - m_center.y - r <= p.y && m_pos.y - m_center.y + m_size.y + r>= p.y) return true;
        else return false;
    }
    
    public void xProjection(Point p)
    {
        p.x = Integer.MAX_VALUE;
        p.y = Integer.MIN_VALUE;


        for (int i = 0; i < 4; i++)
        {
            if (m_bounds[i].x < p.x) p.x = m_bounds[i].x;
            if (m_bounds[i].x > p.y) p.y = m_bounds[i].x;
        }
        if (p.x > p.y) p.swap();
    }
    
    public void yProjection(Point p)
    {
        p.x = Integer.MAX_VALUE;
        p.y = Integer.MIN_VALUE;


        for (int i = 0; i < 4; i++)
        {
            if (m_bounds[i].x < p.x) p.x = m_bounds[i].y;
            if (m_bounds[i].x > p.y) p.y = m_bounds[i].y;
        }
        if (p.x > p.y) p.swap();
    } 
    // WARN ME: This code should NOT be included in release build
    public void draw(Graphics g, int x, int y)
    {
        g.drawLine(Common.toInt(m_bounds[0].x - x),Common.toInt(m_bounds[0].y - y), Common.toInt(m_bounds[1].x - x),Common.toInt(m_bounds[1].y - y));
        g.drawLine(Common.toInt(m_bounds[0].x - x),Common.toInt(m_bounds[0].y - y), Common.toInt(m_bounds[2].x - x),Common.toInt(m_bounds[2].y - y));

        g.drawLine(Common.toInt(m_bounds[1].x - x),Common.toInt(m_bounds[1].y - y), Common.toInt(m_bounds[3].x - x),Common.toInt(m_bounds[3].y - y));
        g.drawLine(Common.toInt(m_bounds[2].x - x),Common.toInt(m_bounds[2].y - y), Common.toInt(m_bounds[3].x - x),Common.toInt(m_bounds[3].y - y));
    }
}
