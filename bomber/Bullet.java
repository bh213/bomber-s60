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
 *  File:       Bullet.java
 *  Content:    Bullet object
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class Bullet
//
// ==========================================================================;

public class Bullet extends GameObject
{
    protected Point               m_pos;          // position of the bullet
    protected int                m_speed_x;      // bullet speed (x direction)
    protected int                m_speed_y;      // bullet speed (y direction)
    protected int                 m_life;         // how int this object has been alive
    protected int                 m_existance;    // how int will this object BE alive
    protected int                 m_blast;        // does bullet explode?
    protected Smoke               m_smoke;        // smoke trail (or better bubbles)
    protected int                 m_bubble_timer; // time to next bubble
    protected boolean             m_in_water;
    
    public static final int BUBBLE_RATE = 250;
    
    public Bullet(GameState gs, int x, int y, int speed_x, int speed_y, int existance, int blast)                 
    {
        super(gs);
        m_pos = new Point(x, y);
        m_speed_x = speed_x;
        m_speed_y = speed_y;
        m_existance = existance;
        m_blast = blast;
        m_in_water = false;
    }

    
    public void draw(Graphics g, int x, int y) 
    {
        if (m_state == DESTROYED) return;
        g.setColor(255,255,255);
        g.fillRect(Common.toInt(m_pos.x - x) - 1, Common.toInt(m_pos.y - y) - 1, 2, 2);
    }
    
    byte getCollisionType() 
    {
        return POINT;
    }
    
    public int handle(int delta) 
    {
        m_pos.set(m_pos.x + Common.fastMul(m_speed_x, delta), m_pos.y + Common.fastMul(m_speed_y, delta));
        m_life += delta;
        if (m_life > m_existance) 
        {
            if (m_blast > 0)
            {
                m_game_state.createBlast(m_pos.x, m_pos.y, 8 * Common.FIXED, 3 * Common.FIXED);
            }
            return DELETE;
        }
        if (m_state == DESTROYED) return DELETE;
        else return NONE;
    }
    

    BoundingBox getBoundingBox() 
    {
        return null;
    }
    
    int getRadius() 
    {
        return 0;
    }
    
    
    Point getPos() 
    {
        return m_pos;
    }
    
    
    short getType() 
    {
        return COLLIDABLE;
    }
    
    byte getObjectType() 
    {
        return BULLET;
    }

    void collidedTerrain(Terrain terrain, boolean water, int delta)
    {
        if (m_in_water != water)
        {
            m_game_state.createSplash(GameState.WATER, m_pos.x, m_pos.y, (byte)8, (byte)5, (byte)30, Common.toByte(m_speed_x / 10));
        }
            
        m_in_water = water;
        if (water) 
        {
            m_life += delta;
            if (m_smoke == null)
            {
                    m_smoke = new Smoke((byte)7, 1, Common.toFP(m_game_state.getTerrain().getWaterLevel()));
                    m_smoke.setSpeed(0, -14000);
                    m_game_state.addVisual(m_smoke);
            }
            
            m_bubble_timer += delta;
            if (m_bubble_timer > BUBBLE_RATE)
            {
                m_bubble_timer -= BUBBLE_RATE;
                m_smoke.addParticle(getPos().x, getPos().y, 0xFFFFFFFF, Smoke.BUBBLE, (GameState.random.nextInt() % 5 + 3) * 1000);
            }
            
            //m_speed_x  = Common.mul(m_speed_x, delta/2);
            //m_speed_y  = Common.mul(m_speed_y, delta/2);

            
            //m_game_state.createSplash(GameState.WATER, m_pos.x, m_pos.y, (byte)8, (byte)5, (byte)30, Common.toByte(m_speed_x / 10));
        }
        else 
        {
            m_state = DESTROYED;
            if (m_smoke != null) m_smoke.stop();
            m_game_state.createSplash(GameState.GROUND, m_pos.x, m_pos.y, (byte)8, (byte)5, (byte)30, Common.toByte(m_speed_x / 10));
        }
    }
    
    void collided(GameObject go, int delta) 
    {
        m_state = DESTROYED;
        if (m_smoke != null)  m_smoke.stop();
    }
    
    int getDamage(int delta)
    {
        return Common.FIXED;
    }
    
    public void destruct()
    {
        super.destruct();
        m_state = DESTROYED;
        if (m_smoke != null) m_smoke.stop();
        m_smoke = null;
        m_pos = null;          
    }
    
}