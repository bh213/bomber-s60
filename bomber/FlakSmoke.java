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
 *  File:       FlakSmoke.java
 *  Content:    Smoke that stays after flak exploded 
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/
package bomber;
import javax.microedition.lcdui.*;

public class FlakSmoke implements Visual 
{

    protected Point     m_pos;
    protected int       m_size;
    protected int       m_start_size;
    
    public FlakSmoke(int x, int y, int size) 
    {
        super();
        m_pos = new Point(x, y);
        m_start_size = m_size = size;
    }
    
    public void draw(Graphics g, int x, int y) 
    {
        int v = 128- (128 * m_size) / m_start_size;
        g.setColor(v, v, v);
        g.fillArc(Common.toInt(m_pos.x - x -  m_size), Common.toInt(m_pos.y - y - m_size), Common.toInt(2 * m_size), Common.toInt(2 * m_size), 0, 360);
    }
    
    public BoundingBox getBoundingBox() 
    {
        return null;
    }
    
    public int handle(int delta) 
    {
        m_size -= delta;
        if (m_size <= 0) 
        {
            m_size = 0;
            return DELETE;
        }
        return NONE;
    }
    
}
