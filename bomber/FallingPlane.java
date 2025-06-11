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
 *  File:       FallingPlane.java
 *  Content:    FallingPlane object
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class FallingPlane
//      Desc:   Object that represents plane that is falling down the sky
//
// ==========================================================================;

public class FallingPlane extends FallingObject 
{
    protected Smoke     m_smoke;
    protected int       m_smoke_timer;
    protected Drawable  m_water_sprite;
    protected int       m_hitpoints;
    
    public FallingPlane(GameState gs, Drawable sprite, Drawable water_sprite, int vx, int vy, OrientedBoundingBox obb, int gravity, int on_ground, Smoke smoke)                 
    {
        super(gs, sprite, vx, vy, obb, gravity, true, on_ground);
        m_smoke = smoke;
        m_smoke.setSpeed(2000, -25000);
        m_water_sprite = water_sprite;
        m_hitpoints = Common.toFP(5);
    }
    

    
    public int handle(int delta) 
    {
        
        if (m_hitpoints < 0 && m_state != DESTROYED) 
        {
            for (int i = 0; i < 5; i++)
                m_game_state.createDebris(getPos().x + Common.toFP(GameState.random.nextInt() % 10), getPos().y + Common.toFP(GameState.random.nextInt() % 20), m_speed_x + Common.toFP(GameState.random.nextInt() % 20), m_speed_y + Common.toFP(GameState.random.nextInt() % 10), 2000);
            
            onDestroyed();
            m_state = DESTROYED;
        }
        int ret_val = super.handle(delta);
        if (ret_val == DELETE) return DELETE;
        m_smoke_timer += delta;
        if (m_smoke_timer > Plane.SMOKE_RATE)
        {
            m_smoke_timer -= Plane.SMOKE_RATE;
            int y = getPos().y;
            if (y > Common.toFP(m_game_state.getTerrain().getWaterLevel()))
            {
                m_smoke_timer -= Plane.SMOKE_RATE;  // double rate
                m_smoke.addParticle(getPos().x, y, 0xFFFFFF, Smoke.BUBBLE, 2000 + Math.abs(m_game_state.random.nextInt() % 2000));
            }
            else m_smoke.addParticle(getPos().x, getPos().y, 0, Smoke.FULL, 6000);
        }
        return ret_val;
    }
    
    public void draw(Graphics g, int x, int y) 
    {
        if ((m_speed_x != 0 || m_speed_y != 0) && m_state != ON_GROUND) m_bounds.setAngle(Common.vectorToAngle(m_speed_x, m_speed_y));
        
        int index = m_drawable.getIndexFromAngle(m_bounds.getAngle());
        int clip_y = m_game_state.getTerrain().getWaterLevel() - Common.toInt(y);
        
        int ix = Common.toInt(m_bounds.getPos().x - x);
        int iy = Common.toInt(m_bounds.getPos().y - y);

        if (clip_y + iy  > Common.ceilInt(getRadius()))
        {
            g.setClip(0, 0, ResourceManager.CANVAS_WIDTH, clip_y);
            m_drawable.drawImage(g, index, ix, iy);
        }
        
        if (clip_y - iy  < Common.ceilInt(getRadius()))
        {
            g.setClip(0, clip_y + 1, ResourceManager.CANVAS_WIDTH, ResourceManager.CANVAS_HEIGHT - (clip_y + 1));
            m_water_sprite.drawImage(g, index, ix, iy);
        }
        m_game_state.resetClip(g);
    }
    
    void onDestroyed()
    {
        m_smoke.stop();
        m_game_state.createExplosion(m_bounds.getPos().x, m_bounds.getPos().y);
    }

    void onGround()
    {
        m_game_state.createSplash(GameState.GROUND, m_bounds.getPos().x, m_bounds.getPos().y, (byte)30, (byte)20, (byte)40, Common.toByte(m_speed_x / 5));
        
    }
    void enterWater()
    {
        m_game_state.createSplash(GameState.WATER, m_bounds.getPos().x, m_bounds.getPos().y, (byte)30, (byte)20, (byte)40, Common.toByte(m_speed_x / 5));
        m_game_state.getResourceManager().sound(ResourceManager.SOUND_WATER_SPLASH);
    }
    
    void collided(GameObject go, int delta) 
    {
        super.collided(go, delta);
        m_hitpoints -= go.getDamage(delta);
    }

    
    public void destruct()
    {
        super.destruct();
        m_smoke.stop();
        m_smoke = null;   
        m_water_sprite = null;
    }
}
