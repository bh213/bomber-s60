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
 *  File:       FallingObject.java
 *  Content:    FallingObject game object
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/


package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class FallingObject
//
// ==========================================================================;

public class FallingObject extends GameObject
{
    protected OrientedBoundingBox m_bounds;     // bounds of this object 
    protected int                 m_speed_x;    // object speed x
    protected int                 m_speed_y;    // object speed y
    protected Drawable            m_drawable;   // sprite of the objects
    protected boolean             m_rotate;     // should object rotate as it falls?
    protected int                 m_gravity;    // gravity for this object
    protected int                 m_on_ground;  // how int does object stay on ground before it is destroyed
    protected boolean             m_in_water;   // is object in water;

    public static final byte ON_GROUND       = 1;

    
    
    public FallingObject(GameState gs, Drawable drawable, int vx, int vy, OrientedBoundingBox obb, int gravity, boolean rotate, int on_ground)                 
    {
        super(gs);
        m_drawable = drawable;
        m_bounds = obb;
        m_speed_x = vx;
        m_speed_y = vy;
        m_gravity = gravity;
        m_rotate = rotate;
        m_on_ground = on_ground;
    }

 
    public void draw(Graphics g, int x, int y) 
    {
        if (m_rotate)
        {
            if ((m_speed_x != 0 || m_speed_y != 0) && m_state != ON_GROUND) m_bounds.setAngle(Common.vectorToAngle(m_speed_x, m_speed_y));
            m_drawable.drawImageWithAngle(g, m_bounds.getAngle(), Common.toInt(m_bounds.getPos().x - x), Common.toInt(m_bounds.getPos().y - y));
        }
        else
        {
            m_drawable.drawImage(g, 0, Common.toInt(m_bounds.getPos().x - x), Common.toInt(m_bounds.getPos().y - y));
        }
    }
    
    public byte getCollisionType() 
    {
        return BOUNDING_BOX;
    }
    
    public int handle(int delta) 
    {
        if (m_state == DESTROYED) return DELETE;
        if (m_state == OK)
        {
            m_bounds.setPos(m_bounds.getPos().x + Common.fastMul(m_speed_x, delta), 
                            m_bounds.getPos().y + Common.fastMul(m_speed_y, delta));
            m_speed_y += Common.fastMul(m_gravity, delta);
        }
        if (m_state == ON_GROUND)
        {
            m_on_ground -= delta;
        }
        if (m_on_ground <= 0) 
        {
            onDestroyed();
            m_state = DESTROYED;
            return DELETE;
        }
        if (m_state == DESTROYED) 
        {
            onDestroyed();
            return DELETE;
        }
        else return NONE;
    }

// =========================================================================;
//	Name:	enterWater
//      Desc:   Callback called when object enters water
// ==========================================================================;

    void enterWater()
    {
    }
    
    
// =========================================================================;
//	Name:	inWater
//      Desc:   Callback called when object is in the water
// ==========================================================================;

    
    protected static final short c_pow_array[] = 
    {1019,
    1014,
    1010,
    1005,
    1001,
    996,
    992,
    987,
    983,
    978,
    974,
    970,
    965,
    961,
    957,
    952,
    948,
    944,
    940,
    935,
    931,
    927,
    923,
    919,
    915,
    911,
    906,
    902,
    898,
    894,
    890,
    886,
    882,
    878,
    874,
    870,
    867,
    863,
    859,
    855,
    851,
    847,
    843,
    840,
    836,
    832,
    828,
    825,
    821,
    817,
    814,
    810,
    806,
    803,
    799,
    796,
    792,
    788,
    785,
    781,
    778,
    774,
    771,
    767,
    764,
    761,
    757,
    754,
    750,
    747,
    744,
    740,
    737,
    734,
    730,
    727,
    724,
    721,
    717,
    714,
    711,
    708,
    705,
    701,
    698,
    695,
    692,
    689,
    686,
    683,
    680,
    677,
    673,
    670,
    667,
    664,
    661,
    659,
    656,
    653
            };
    
    void inWater(int delta)
    {
        int index;
        if (delta >= c_pow_array.length) index = c_pow_array.length - 1;
        else index = (int)(delta);
        
         m_speed_x  = Common.fastMul(m_speed_x, c_pow_array[index]);
         m_speed_y  = Common.fastMul(m_speed_y, c_pow_array[index]);
    }
    
    
    
// =========================================================================;
//	Name:	onGround
//      Desc:   Callback called when object touches the ground
// ==========================================================================;

    void onGround()
    {
    }
// =========================================================================;
//	Name:	onDestroyed
//      Desc:   Callback called when object is about to be destroyed,
//              because of destroyed state.
// ==========================================================================;
  
    void onDestroyed()
    {
    }
    
// =========================================================================;
//	Name:	onCollision
//      Desc:   Callback called when object collided with other object
// ==========================================================================;
    void onCollision(GameObject go)
    {
        m_state = OK;
    }
    
    public BoundingBox getBoundingBox() 
    {
        return m_bounds;
    }
    
    public int getRadius() 
    {
        return m_bounds.getRadius();
    }
    
    Point getPos() 
    {
        return m_bounds.getPos();
    }
    
    public byte getState()
    {
        return m_state;
    }
    
    
    void collided(GameObject go, int delta) 
    {
        if (m_state == ON_GROUND) return;
         onCollision(go);
    }
    
    short getType() 
    {
        if (m_state == OK) return COLLIDABLE | TERRAIN_DAMAGE | BOUNDS_DETECT;
        else return COLLIDABLE_INTER_OBJ;
    }
    
    byte getObjectType() 
    {
        return OBJECT;
    }
    
    void collidedTerrain(Terrain terrain, boolean water, int delta)
    {
            if (water == false && m_state != ON_GROUND)
            {
                m_state = ON_GROUND;
                onGround();
            }
            if (water == true)
            {
                if (m_in_water) inWater(delta);
                else 
                {
                    m_in_water = true;
                    enterWater();
                }
            }
    }
    
    int getDamage(int delta)
    {
        return Common.toFP(10);
    }
    
    public void destruct()
    {
        super.destruct();
        m_state = DESTROYED;
        m_bounds = null;
        m_drawable = null;   
    }
    
}
