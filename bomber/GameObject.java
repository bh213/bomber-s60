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
 *  File:       GameObject.java
 *  Content:    Game object interface
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class GameObject
//      Desc:   base class for objects that represent all game objects
//
// ==========================================================================;

public abstract class GameObject implements Visual
{
    // bounds constants

    public static final byte POINT         = 1;
    public static final byte CIRCLE        = 2;
    public static final byte BOUNDING_BOX  = 4;
    
    public static final short COLLIDABLE                 = 6;         // this object can be collided with everything
    public static final short CONTROLABLE                = 1;         // this object can be controlled
    
    public static final short COLLIDABLE_TERRAIN         = 2;         // this object can collide with terrain
    public static final short COLLIDABLE_INTER_OBJ       = 4;         // this object can collide with other objects
    
    public static final short TERRAIN_DAMAGE             = 8;         // this object can damage the terrain
    public static final short BOUNDS_DETECT              = 16;        // calls GameHandler outofbounds hanler for this objects
    public static final short ZONES_DETECT               = 32;        // calls GameHandler zone hanler for this objects
    public static final short CAN_TARGET                 = 64;        // this object can have his target set
    public static final short IMMOBILE                   = 128;        // object does not move (collision speed optimization)
    
    // object types
    public static final byte BULLET                     = 1;
    public static final byte OBJECT                     = 2;
    public static final byte BUILDING                   = 3;
    public static final byte BLAST                      = 4;
    public static final byte PLANE                      = 5;
    public static final byte TANK                       = 6;
    public static final byte TREE                       = 7;
    public static final byte DEBRIS                     = 8;
    public static final byte ZEPPELIN                   = 9;
    public static final byte FALLING_DEBRIS             = 10;
    
    
    // states
    public static final byte OK                         = 0;
    public static final byte DESTROYED                  = 2;

    
    protected GameState         m_game_state;           // state of the game interface
    protected byte              m_state;
    protected int               m_flags;                // generic flags
    
    GameObject(GameState gs)
    {
        m_game_state = gs;
        m_state = OK;
    }
    
    abstract void collided(GameObject go, int delta);  // reports that object has collided with specific object
    abstract void collidedTerrain(Terrain terrain, 
                                  boolean water, 
                                  int delta);          // reports that object has collided with ground (or water)
    //
    abstract int getDamage(int delta);                // returns amount of damage done this turn
    
    abstract short getType();                           // gets type of this object (CONTROLABLE, COLLIDABLE)
    abstract byte getObjectType();                      // gets id of the object type
    public boolean isAlive()                            // returns true, if object is alive, or false if it is dead and ready to be GC'ed
    {
        return m_state != DESTROYED;
    }                          
    
    abstract BoundingBox getBoundingBox();              // gets bounding box of this object (or null, if it does not exist)
    abstract Point getPos();                            // gets center point for this object
    abstract int getRadius();                          // gets circle radius for this object
    
    abstract byte getCollisionType();                   // returns collision type of this object

    public void destruct()                             // destructs the object (releases all references)
    {
        m_game_state = null;
    }

    public void destroy()                             // destroys the object (isAlive == false)
    {
        m_state = DESTROYED;
    }
    
    public int getFlags() 
    {
        return m_flags;
    }
    
    public void setFlags(int flags) 
    {
        m_flags = flags;
    }
    
    
    public boolean checkCollision(GameObject go)
    {
        switch(go.getCollisionType())
        {
            case NONE: return false;
            case POINT: return collide(go.getPos()); 
            case CIRCLE: return collide(go.getPos(), go.getRadius()); 
            case BOUNDING_BOX: return collide(go.getBoundingBox()); 
        }
        return false;
    }
    
    public boolean collide(BoundingBox bb)
    {
        if (getBoundingBox() != null) return bb.collision(getBoundingBox());
        else if (getRadius() != 0) return bb.collision(getPos(), getRadius());
        else return bb.collision(getPos());
    }
    
    public boolean collide(Point p)
    {
        if (getBoundingBox() != null) return getBoundingBox().collision(p);
        else if (getRadius() != 0) 
        {
            if (p.distance(getPos()) < getRadius()) return true;
            else return false;
        }
        else return false;      // WARN ME:points do not collide

    }
    public boolean collide(Point p, int radius)
    {
        if (getBoundingBox() != null) return getBoundingBox().collision(p, radius);
        else    // this handles circle-circle and circle-point detection
        {
            if (p.distance(getPos()) < getRadius() + radius) return true;
            else return false;
        }
    }

    
    
    
}
