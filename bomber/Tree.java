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
 *  File:       Tree.java
 *  Content:    Tree GameObject
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/


package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class Tree
//
// ==========================================================================;

public class Tree extends GameObject
{
    protected BoundingBox m_bounds;                 // bounds of tree (cannot be rotated)
    protected Drawable            m_sprite;         // sprite of the tree
    protected byte                m_state;          // state of the building
    

    public Tree(GameState gs, Drawable sprite, int x)
    {
        
        super(gs);
        m_sprite = sprite;
        m_bounds = new BoundingBox(Common.toFP(sprite.getWidth(0)), Common.toFP(sprite.getHeight(0)));
        m_bounds.setCenter(Common.toFP(sprite.getCenterX(0)), Common.toFP(sprite.getCenterY(0)));
        m_bounds.setPos(x, Common.toFP(m_game_state.getTerrain().getMinHeight(Common.toInt(x) - 3, 6)));
        m_state = OK;
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
    
    public int getRadius() 
    {
        return m_bounds.getRadius();
    }
    
    public void setPoint(int x, int y)
    {
        m_bounds.setPos(x, y);
    }
    
    public Point getPos() 
    {
        return m_bounds.getPos();
    }
    
    
    void collided(GameObject go, int delta) 
    {
        if (m_state == DESTROYED) return;
        m_game_state.createSplash(GameState.TREE, m_bounds.getPos().x, m_bounds.getPos().y, (byte)8, (byte)5, (byte)30, (byte)0);

        m_state = DESTROYED;
    }
    
    short getType() 
    {
        return COLLIDABLE_INTER_OBJ | IMMOBILE;
    }
    
    byte getObjectType() 
    {
        return TREE;
    }
    
    public boolean isAlive()
    {
        return m_state != DESTROYED;
    }
    
    void collidedTerrain(Terrain terrain, boolean water, int delta)
    {
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
        m_sprite = null;
    }
    
}
