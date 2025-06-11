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
 *  File:       Building.java
 *  Content:    Building GameObject
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/


package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class Building
//
// ==========================================================================;

public class Building extends GameObject
{
    protected BoundingBox m_bounds;                 // bounds of bouilding (cannot be rotated)
    protected Drawable            m_sprite;         // sprite of the building
    protected Point               m_projection_x;   // projection of this object to x
    protected int                m_hitpoints;      // hitpoint of this building
    protected int                m_total_hitpoints;// total hitpoints at beginning
    
    public static final byte OK              = 0;
    public static final byte DESTROYED       = 2;
    
    
    public Building(GameState gs, Drawable sprite, int x, byte hitpoints)
    {
        super(gs);
        m_sprite = sprite;
        m_bounds = new BoundingBox(Common.toFP(sprite.getWidth(0)), Common.toFP(sprite.getHeight(0)));
       
        m_bounds.setCenter(Common.toFP(sprite.getCenterX(0)), Common.toFP(sprite.getCenterY(0)));
        
        m_bounds.setPos(x, 0);
        m_projection_x = new Point();
        m_bounds.xProjection(m_projection_x);

        m_bounds.setPos(x, Common.toFP(m_game_state.getTerrain().getMinHeight(Common.toInt(m_projection_x.x), Common.ceilInt(m_projection_x.y - m_projection_x.x))));

        m_game_state.getTerrain().flatten(Common.toInt(m_projection_x.x), Common.ceilInt(m_projection_x.y - m_projection_x.x) + 1);
        
        m_total_hitpoints = m_hitpoints = Common.toFP(hitpoints);
    }

    public void draw(Graphics g, int x, int y) 
    {
        m_sprite.drawImage(g, 0, Common.toInt(m_bounds.getPos().x - x), Common.toInt(m_bounds.getPos().y - y));
    }
    
    byte getCollisionType() 
    {
        return BOUNDING_BOX;
    }
    
    public int handle(int delta) 
    {
        if (m_state == DESTROYED) return DELETE;
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
   
    void collided(GameObject go, int delta) 
    {
        if (m_state == DESTROYED) return;
        
        m_hitpoints -= go.getDamage(delta);
        
        if (m_hitpoints <= 0)
        {
           m_state = DESTROYED;
           m_game_state.createExplosion(m_bounds.getPos().x, m_bounds.getPos().y);
           m_game_state.getResourceManager().sound(ResourceManager.SOUND_BUILDING_EXPLODE);
           m_game_state.getTerrain().damage(Common.toInt(m_bounds.getPos().x), 15, 1);
           m_game_state.addObject(new Debris(m_game_state, m_bounds.getPos().x, (byte)120));
        }
    }
    
    
    void collidedTerrain(Terrain terrain, boolean water, int delta)
    {
    }

    
    short getType() 
    {
        return COLLIDABLE_INTER_OBJ | IMMOBILE;
    }
    
    byte getObjectType() 
    {
        return BUILDING;
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
    }
    
}
