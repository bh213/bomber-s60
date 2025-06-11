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
 *  File:       Plane.java
 *  Content:    Plane object
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/


package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class Plane
//      Desc:	Playable object plane. User controls this objects
// ==========================================================================;

public class Plane extends GameObject implements Rotatable
{
    protected OrientedBoundingBox m_bounds;        // bounds of this object (plane)
    protected int                m_speed;          // plane speed (as scalar)
    protected int                m_energy;         // kinetic enery of the plane
    protected Drawable            m_sprite;        // sprite of the plane
    protected Drawable            m_water_sprite;  // sprite of the plane in water
    
    protected int                 m_turn_timer;     // time between turns
    protected int                 m_fire_timer;     // time between bullets
    protected int                 m_bomb_timer;     // time between bombs
    protected boolean             m_is_bombing;
    protected boolean             m_is_firing;
    protected byte                m_bombs;          // number of bombs in bomb bay
    
    protected boolean             m_is_turning_up;
    protected boolean             m_is_turning_down;
    
    protected int                 m_turn_rate;
    protected int                 m_fire_rate;
    protected int                 m_engine_power;
    protected int                 m_mass;
    protected int                m_hitpoints;
    protected int                m_max_hitpoints;
    protected Smoke               m_smoke;
    protected int                 m_smoke_timer;
    
    
    protected FallingPlane        m_falling_plane;  // object, which is created, when plane is destroyed.
    
//    protected static final int    TURN_RATE = 80;  // in ms 
    protected static final int    BOMB_RATE = 1000;  // in ms
//    protected static final int    FIRE_RATE = 250;  // in ms
//    protected static final int    ENGINE_POWER = 70;  
//    protected static final int    MASS = 10;  
    protected static final int    G = 4000;  
    //protected static final int    AIR_RESISTANCE = 1000;  
    protected static final int    AIR_RESISTANCE = 1024;  
   
    public static final int SMOKE_RATE = 450;
    
    public Plane(GameState gs, Drawable sprite, Drawable water_sprite, int turn_rate, int fire_rate, int engine_power, int mass, int hitpoints)                 // sprite must be flippable
    {
        super(gs);
        m_turn_rate = turn_rate;
        m_fire_rate = fire_rate;
        m_engine_power = engine_power;
        m_mass = mass;
        m_bombs = 5;
        m_max_hitpoints = m_hitpoints = hitpoints;
        m_sprite = sprite;
        m_water_sprite = water_sprite;
        m_bounds = new OrientedBoundingBox(Common.toFP(sprite.getWidth(0)), Common.toFP(sprite.getHeight(0)));
        m_bounds.setCenter(Common.toFP(sprite.getCenterX(0)), Common.toFP(sprite.getCenterY(0)));
     
    }

    protected void createSmoke()
    {
        m_smoke = new Smoke((byte)7, 3, Common.toFP(m_game_state.getTerrain().getWaterLevel()));
        //m_smoke.setSpeed(2000, -14000);
        m_game_state.addVisual(m_smoke);
    }
    
    
    public void setTurnUp(boolean s)
    {
        m_is_turning_up = s;
    }

    public void setTurnDown(boolean s)
    {
        m_is_turning_down = s;
    }

    
    public void setFire(boolean start)
    {
        m_is_firing = start;
    }

    public void setBomb(boolean start)
    {
        m_is_bombing = start;
    }

    
    protected void rotateRelative(int angle)
    {
        if (m_state != OK) return;
        m_bounds.setAngle(m_bounds.getAngle() + angle);
    }
    
    public int getAngle()
    {
        return m_bounds.getAngle();
    }
    
    public void setAngle(int angle)
    {
        m_bounds.setAngle(angle);
    }

    
    
    protected void bomb()
    {
        if (m_state != OK) return;
        m_game_state.getResourceManager().sound(ResourceManager.SOUND_BOMB_DROP);
        m_game_state.createBomb(
                        m_bounds.getPos().x + Common.mul(2*m_bounds.getRadius(), Common.cos(m_bounds.getAngle() + 90 * Common.FIXED)),  // x
                        m_bounds.getPos().y + Common.mul(2*m_bounds.getRadius(), Common.sin(m_bounds.getAngle() + 90 * Common.FIXED)),  // y

                        Common.mul(m_speed, Common.cos(m_bounds.getAngle())),
                        Common.mul(m_speed, Common.sin(m_bounds.getAngle())));
    }
    
    protected void fire()
    {
        if (m_state != OK) return;
        m_game_state.getResourceManager().sound(ResourceManager.SOUND_BULLET_FIRE);
        
        int x = m_bounds.getPos().x + Common.mul(m_bounds.getRadius(), Common.cos(m_bounds.getAngle()));
        int y = m_bounds.getPos().y + Common.mul(m_bounds.getRadius(), Common.sin(m_bounds.getAngle()));
        m_game_state.createControlledSpark(x, y, m_bounds.getAngle(), 5 * Common.FIXED, (byte)(Common.toByte(m_speed) + 15), (byte)15);
        m_game_state.createBullet(
                        x,
                        y,  // y
                        100 * Common.cos(m_bounds.getAngle()),
                        100 * Common.sin(m_bounds.getAngle()),
                        2000, 0);
    }
    
    
    public void setSpeed(int speed)
    {
        m_speed = speed;
    }
    
    public int getSpeed()
    {
        return m_speed;
    }
    
    public void draw(Graphics g, int x, int y) 
    {
        m_sprite.drawImageWithAngle(g, m_bounds.getAngle(), Common.toInt(m_bounds.getPos().x - x), Common.toInt(m_bounds.getPos().y - y));
    }
    
    byte getCollisionType() 
    {
        return BOUNDING_BOX;
    }
    
    public int handle(int delta) 
    {
       if (m_state == DESTROYED) return DELETE;

       if (m_hitpoints < m_max_hitpoints)
       {
            if (m_smoke == null) createSmoke();
           
            m_smoke_timer += delta;
            if (m_smoke_timer > SMOKE_RATE)
            {
                m_smoke_timer -= SMOKE_RATE;
                
                
                int b = (255 * m_hitpoints) / m_max_hitpoints;
                int color = (b << 16) + (b << 8) + b;
                m_smoke.addParticle(getPos().x, getPos().y, color, Smoke.FULL, 6000);
            }
       }
        
       
       if (m_is_turning_down && m_turn_timer <= 0)
       {
           rotateRelative(23040);
           m_turn_timer += m_turn_rate;
       }
       if (m_is_turning_up && m_turn_timer <= 0)
       {
           rotateRelative(-23040);
           m_turn_timer += m_turn_rate;
       }
       
       if (m_is_firing && m_fire_timer <= 0)
       {
           fire();
           m_fire_timer += m_fire_rate;
       }
       
       if (m_is_bombing && m_bomb_timer <= 0 && m_bombs > 0)
       {
           m_bombs--;
           bomb();
           m_bomb_timer += BOMB_RATE;
       }

       
       if (m_turn_timer > 0) m_turn_timer -= delta;
       if (m_bomb_timer > 0) m_bomb_timer -= delta;
       if (m_fire_timer > 0) m_fire_timer -= delta;
       
       m_energy += Common.mul(delta * m_mass, G *  Common.sin(m_bounds.getAngle())) + Common.toFP(m_engine_power) * delta;
       m_speed = Common.sqrt((m_energy / (2 * m_mass)));

       m_energy -= ((long)m_speed * (long)m_speed) / AIR_RESISTANCE;
       
       if (m_energy < 0) m_energy = 0;
       

       m_bounds.setPos(m_bounds.getPos().x + Common.mul(Common.mul(m_speed, Common.cos(m_bounds.getAngle())), delta), 
                       m_bounds.getPos().y + Common.mul(Common.mul(m_speed, Common.sin(m_bounds.getAngle())), delta));

        
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

   
    void collided(GameObject go, int delta) 
    {
        if (m_state == DESTROYED) return;
        
        int damage = go.getDamage(delta);
        m_hitpoints -= damage;
        
        if (m_hitpoints > 0) 
        {
            if (damage > 0) m_game_state.createSpark(m_bounds.getPos().x, m_bounds.getPos().y);
            return;
        }
        m_state = DESTROYED;
         
        m_game_state.createExplosion(m_bounds.getPos().x, m_bounds.getPos().y);
        if (m_smoke == null) createSmoke();
        m_falling_plane = new FallingPlane(m_game_state, 
        m_sprite, m_water_sprite,
        Common.mul(m_speed, Common.cos(m_bounds.getAngle())),
        Common.mul(m_speed, Common.sin(m_bounds.getAngle())),
        m_bounds,
        20000,
//        true, 
        3000,
        m_smoke);
        m_game_state.addObject(m_falling_plane);


    }
    
    short getType() 
    {
        return CONTROLABLE | COLLIDABLE | BOUNDS_DETECT | ZONES_DETECT;
    }
    
    byte getObjectType() 
    {
        return PLANE;
    }
    
    byte getBombs()
    {
        return m_bombs;
    }
    
    void setBombs(byte b)
    {
        m_bombs = b;
    }
    
    boolean addBomb()
    {
        if (m_bombs < 5) 
        {
            m_bombs++;
            return true;
        }
        else return false;
    }
    
    boolean repair()
    {
        if (m_hitpoints != m_max_hitpoints)
        {
            m_hitpoints = m_max_hitpoints;
            return true;
        }
        else return false;
    }
    
    public FallingPlane getFallingPlane()
    {
        return m_falling_plane;
    }
    
    void collidedTerrain(Terrain terrain, boolean water, int delta)
    {
        m_state = DESTROYED;
        //m_game_state.createExplosion(m_bounds.getPos().x, m_bounds.getPos().y);
        if (m_smoke == null) createSmoke();
        m_falling_plane = new FallingPlane(m_game_state, m_sprite,
        m_water_sprite,
        Common.mul(m_speed, Common.cos(m_bounds.getAngle())),
        Common.mul(m_speed, Common.sin(m_bounds.getAngle())),
        m_bounds,
        20000,
//        true, 
        3000,
        m_smoke);
        m_game_state.addObject(m_falling_plane);
    }
    
    int getDamage(int delta)
    {
        return Common.toFP(1);
    }
    
    public void destruct()
    {
        super.destruct();
        m_state = DESTROYED;
        m_bounds = null;         
        m_sprite = null;         
        m_water_sprite = null;
        m_falling_plane = null;  
        m_smoke = null;
    }
    
}
