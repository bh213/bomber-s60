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
 *  File:       Blast.java
 *  Content:    Blast object (GameObject for explosion)
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class Bomb
//      Desc:   Object that represents falling bomb
//
// ==========================================================================;

public class Blast extends GameObject
{
    //protected Explosion m_explosion;
    protected int      m_radius;           // blast radius
    protected int      m_start_radius;     // original radius of the blast
    protected int      m_radius_decay;     // shrinking of the radius
    protected Point     m_pos;              // center of the blast
    protected int       m_smoke;            // when (if ever) will smoke be created
    protected boolean   m_in_water;         // is blast in water?
    
    public Blast(GameState gs, int x, int y, int radius, int smoke)                 
    {
        super(gs);
        m_pos = new Point(x, y);
        m_start_radius = m_radius = radius;
        m_radius_decay = Common.toFP(20);
        m_smoke = smoke;
        m_in_water = m_game_state.getTerrain().collide(m_pos) == Terrain.WATER;
        
    }
    
    
   
    
    public void draw(Graphics g, int x, int y) 
    {
        int v = (int)((50 * (m_radius - m_smoke)) / (m_start_radius - m_smoke));
        if (m_in_water) g.setColor(5 * v, 5 * v, 200 + v);
        else g.setColor(200 + v, 5 * v, 5 * v);
        g.fillArc(Common.toInt(getPos().x - x -  getRadius()), Common.toInt(getPos().y - y -  getRadius()), Common.toInt(2*getRadius()), Common.toInt(2*getRadius()), 0, 360);
    }
    
    BoundingBox getBoundingBox() 
    {
        return null;
    }
    
    byte getCollisionType() 
    {
        return CIRCLE;
    }
    
    Point getPos() 
    {
        return m_pos;
    }
    
    int getRadius() 
    {
        return m_radius;
    }
    
    short getType() 
    {
        return COLLIDABLE;
    }
    
    public int handle(int delta) 
    {
        
        m_radius -= Common.fastMul(m_radius_decay, delta);
        
        if (m_radius <= 0) 
        {
            m_radius = 0;
            m_state = DESTROYED;
            return DELETE;
        }
        else if (m_radius < m_smoke)
        {
            m_radius = 0;
            m_state = DESTROYED;
            m_game_state.createFlakSmoke(m_pos.x, m_pos.y, m_smoke);
            return DELETE;
        }
        else 
        {
            m_radius_decay +=  Common.fastMul(delta, 5 * m_radius_decay);
            return NONE;
        }
    }
    
    byte getObjectType() 
    {
        return BLAST;
    }
    
    
    void collided(GameObject go, int delta) 
    {
    }
    
    void collidedTerrain(Terrain terrain, boolean water, int delta)
    {
    }
    
    int getDamage(int delta)
    {
        return 10 * delta;
    }
    
    public void destruct()
    {
        super.destruct();
        m_pos = null;
    }
    
}
