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
 *  File:       FallingDebris.java
 *  Content:    FallingDebris game object
 *  Created:    February 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/


package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class FallingDebris
//
// ==========================================================================;

public class FallingDebris extends GameObject
{
    protected Point               m_pos;        // bounds of this object 
    protected int                 m_speed_x;    // object speed x
    protected int                 m_speed_y;    // object speed y
    protected int                 m_radius;     // size of this object
    protected int                 m_gravity;    // gravity for this object
    protected Smoke               m_smoke;
    protected int                 m_smoke_timer;
   
    public static final int SMOKE_RATE = 100;

    
    
    public FallingDebris(GameState gs, int x, int y, int vx, int vy, int radius, int gravity)
    {
        super(gs);
        m_pos = new Point(x, y);
        m_speed_x = vx;
        m_speed_y = vy;
        m_gravity = gravity;
        m_radius = radius;
        
        m_smoke = new Smoke((byte)10, 2, Common.toFP(m_game_state.getTerrain().getWaterLevel()));
        m_game_state.addVisual(m_smoke);
    }

 
    public void draw(Graphics g, int x, int y) 
    {
        g.setColor(0);
        g.fillArc(Common.toInt(getPos().x - x -  getRadius()), Common.toInt(getPos().y - y -  getRadius()), Common.toInt(2*getRadius()), Common.toInt(2*getRadius()), 0, 360);
    }
    
    byte getCollisionType() 
    {
        return CIRCLE;
    }
    
    public int handle(int delta) 
    {
        if (m_state == DESTROYED) 
        {
            m_smoke.stop();
            return DELETE;
        }
        if (m_state == OK)
        {
            
            m_smoke_timer += delta;
            if (m_smoke_timer > SMOKE_RATE)
            {
                m_smoke_timer -= SMOKE_RATE;
                m_smoke.addParticle(getPos().x, getPos().y, 0xff0000, Smoke.FULL, 2000);
            }

            
            
            m_radius = m_radius - delta;
            if (m_radius < 0)
            {
                m_radius = 0;
                m_state = DESTROYED;
            }
            m_pos.set(m_pos.x + Common.fastMul(m_speed_x, delta), 
                            m_pos.y + Common.fastMul(m_speed_y, delta));
            m_speed_y += Common.fastMul(m_gravity, delta);
        }
        return NONE;
    }

  
    
    BoundingBox getBoundingBox() 
    {
        return null;
    }
    
    int getRadius() 
    {
        return m_radius;
    }
    
    Point getPos() 
    {
        return m_pos;
    }
    
    
    void collided(GameObject go, int delta) 
    {
        if (go.getObjectType() != FALLING_DEBRIS && go.getObjectType() != BLAST ) m_state = DESTROYED;
    }
    
    short getType() 
    {
        return COLLIDABLE_TERRAIN;
    }
    
    byte getObjectType() 
    {
        return FALLING_DEBRIS;
    }
    
    void collidedTerrain(Terrain terrain, boolean water, int delta)
    {
            if (water == false)
            {
                m_state = DESTROYED;
            }
            if (water == true)
            {
                m_state = DESTROYED;            
            }
    }
    
    int getDamage(int delta)
    {
        return 100;
    }
    
    public void destruct()
    {
        super.destruct();
        if (m_smoke != null)
        {
            m_smoke.stop();
            m_smoke = null;
        }
        m_state = DESTROYED;
        m_pos = null;
    }
    
}
