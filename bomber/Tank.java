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
 *  File:       Tank.java
 *  Content:    Tank GameObject
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/


package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class Tank
//
// ==========================================================================;

public class Tank extends GameObject implements CanTarget
{
    protected BoundingBox         m_bounds;         // bounds of tank(cannot be rotated)
    protected Drawable            m_sprite;         // sprite of the tank
    protected byte                m_state;          // state of the tank
    protected int                 m_gun_angle;      // angle of the gun
    protected int                 m_gun_timer;
    protected Point               m_projection_x;   // projection of this object to x
    protected GameObject          m_target;
    protected int                 m_hitpoints;
    protected boolean             m_is_crater;
    
    public static final int FIRE_RATE         = 5000;
    public static final int FIRE_RANGE        = 10 * Common.FIXED;
    public static final int ROTATE_SPEED      = 30;
    public static final int MAX_X_RANGE       = 80 * Common.FIXED;
    
    public Tank(GameState gs, Drawable sprite, int x)
    {
        super(gs);
        init(gs, sprite);
        m_bounds.setPos(x, 0);
        m_projection_x = new Point();
        m_bounds.xProjection(m_projection_x);
        m_bounds.setPos(x, Common.toFP(m_game_state.getTerrain().getMinHeight(Common.toInt(m_projection_x.x), Common.ceilInt(m_projection_x.y - m_projection_x.x))));
        m_game_state.getTerrain().flatten(Common.toInt(m_projection_x.x), Common.ceilInt(m_projection_x.y - m_projection_x.x) + 1);
        m_is_crater = true;
    }
    
    public Tank(GameState gs, Drawable sprite, int x, int y)
    {
        super(gs);
        init(gs, sprite);
        m_bounds.setPos(x, y);
        m_is_crater = false;
    }

    
    
    private void init(GameState gs, Drawable sprite)
    {
        m_sprite = sprite;
        m_bounds = new BoundingBox(Common.toFP(sprite.getWidth(0)), Common.toFP(sprite.getHeight(0)));
        m_bounds.setCenter(Common.toFP(sprite.getCenterX(0)), Common.toFP(sprite.getCenterY(0)));
        m_hitpoints = Common.toFP(m_game_state.getDifficultyLevel() + 2);
        m_state = OK;
        m_gun_angle = 270 * Common.FIXED;
    }
    
    public void draw(Graphics g, int x, int y) 
    {
        g.setColor(0);
        
        int sx = Common.toInt(m_bounds.getPos().x - x) - m_sprite.getCenterX(0) + m_sprite.getUserData(0);
        int sy = Common.toInt(m_bounds.getPos().y - y) - m_sprite.getCenterY(0) + m_sprite.getUserData(1);
        
        int gun_ext = Common.FIXED;
        if (m_gun_timer < FIRE_RATE) 
        {
            gun_ext = ((Common.FIXED/2) * m_gun_timer) / FIRE_RATE + Common.FIXED / 2;
            gun_ext = Common.sqrt(gun_ext);
        }
            
        
        int gx = sx + Common.toInt(Common.mul(Common.cos(m_gun_angle) * m_sprite.getUserData(2), gun_ext));
        int gy = sy + Common.toInt(Common.mul(Common.sin(m_gun_angle) * m_sprite.getUserData(2), gun_ext));
        
        g.drawLine(sx, sy, gx, gy);
        m_sprite.drawImage(g, 0, Common.toInt(m_bounds.getPos().x - x), Common.toInt(m_bounds.getPos().y - y));

    }
    
    byte getCollisionType() 
    {
        return BOUNDING_BOX;
    }
    
    
    public int handle(int delta) 
    {
        if (m_state == DESTROYED) return DELETE;
        if (m_gun_timer < FIRE_RATE) m_gun_timer += m_game_state.getDifficultyLevel() * delta;
        if (m_target != null)
        {
        
            if (m_target.isAlive() == false) 
            {
                m_target = null;
            }
            else
            {

                int dist = Common.distancePointToLine(m_target.getPos().x - m_bounds.getPos().x, m_target.getPos().y - m_bounds.getPos().y, Common.cos(m_gun_angle), Common.sin(m_gun_angle));
                
                if (Math.abs(dist) < FIRE_RANGE && m_gun_timer >= FIRE_RATE && Math.abs(m_target.getPos().x - m_bounds.getPos().x) < MAX_X_RANGE)
                {
                        m_gun_timer -=FIRE_RATE;
                        fire();
                }
                else
                {
                    if (dist < 0) m_gun_angle += ROTATE_SPEED * delta;
                    else m_gun_angle -= ROTATE_SPEED * delta;
                }



                m_gun_angle = Common.clampAngle(m_gun_angle);

                if (m_gun_angle < Common.FIXED * 180 && m_gun_angle > Common.FIXED * 90) m_gun_angle = Common.FIXED * 180;
                else if (m_gun_angle > 0 && m_gun_angle < Common.FIXED * 90) m_gun_angle = 0;
            }
        }
        
        return NONE;
    }
    
    
    BoundingBox getBoundingBox() 
    {
        return m_bounds;
    }
    
    int getRadius() 
    {
        return m_bounds.getRadius();
    }
    
    void setPoint(int x, int y)
    {
        m_bounds.setPos(x, y);
    }
    
    Point getPos() 
    {
        return m_bounds.getPos();
    }
    
    public byte getState()
    {
        return m_state;
    }
   
    short getType() 
    {
        return COLLIDABLE_INTER_OBJ | CAN_TARGET | IMMOBILE;
    }
    
    byte getObjectType() 
    {
        return TANK;
    }
    
    protected void fire()
    {
        if (m_state != OK) return;
        
        int sx = m_bounds.getPos().x - Common.toFP(m_sprite.getCenterX(0) - m_sprite.getUserData(0));
        int sy = m_bounds.getPos().y - Common.toFP(m_sprite.getCenterY(0) - m_sprite.getUserData(1));
        
        int gx = sx + Common.cos(m_gun_angle) * m_sprite.getUserData(2);
        int gy = sy + Common.sin(m_gun_angle) * m_sprite.getUserData(2);

        
        
        
        m_game_state.createDirectionSpark(gx, gy, m_gun_angle, 30 * Common.FIXED);
        m_game_state.createBullet(
                        //m_bounds.getPos().x + Common.mul(m_bounds.getRadius(), Common.cos(m_gun_angle)),  // x
                        //m_bounds.getPos().y + Common.mul(m_bounds.getRadius(), Common.sin(m_gun_angle)),  // y
                        gx, 
                        gy,
                        40 * Common.cos(m_gun_angle),
                        40 * Common.sin(m_gun_angle),
                        3000 + m_game_state.getDifficultyLevel() * 500, 5000 + m_game_state.getDifficultyLevel() * 250);
    }
    
    public boolean isAlive()
    {
        return m_state != DESTROYED;
    }

    public void setTarget(GameObject go)
    {
        m_target = go;
    }

    
    public GameObject getTarget()
    {
        return m_target;
    }

    void collided(GameObject go, int delta) 
    {
        if (m_state == DESTROYED) return;

        m_hitpoints -= go.getDamage(delta);
        
        if (m_hitpoints <= 0)
        {
            m_game_state.createExplosion(m_bounds.getPos().x, m_bounds.getPos().y);
        if (m_is_crater)    m_game_state.getTerrain().crater(Common.toInt(m_bounds.getPos().x), 15, 3);
            m_game_state.getResourceManager().sound(ResourceManager.SOUND_BUILDING_EXPLODE);
            m_state = DESTROYED;
        }
        

    }
    
    void collidedTerrain(Terrain terrain, boolean water, int delta)
    {
    }
    
    int getDamage(int delta)
    {
        return Common.toFP(100);
    }
    
    public void destruct()
    {
        super.destruct();
        m_state = DESTROYED;
        m_bounds = null;
        m_sprite = null;         
        m_projection_x = null;   
        m_target = null;
    }
    
}
