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
 *  File:       Bomb.java
 *  Content:    Bomb object
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

public class Bomb extends FallingObject 
{
    protected Drawable m_water_drawable;
    public Bomb(GameState gs, Drawable drawable, Drawable water_drawable, int vx, int vy, int gravity, int x, int y)                 
    {
        super(gs, drawable, 
        vx, vy, 
        new OrientedBoundingBox(Common.toFP(drawable.getWidth(0)), Common.toFP(drawable.getHeight(0))),
        gravity, true, 1);
        m_bounds.setCenter(Common.toFP(drawable.getCenterX(0)), Common.toFP(drawable.getCenterY(0)));
        m_bounds.setPos(x, y);
        m_water_drawable = water_drawable;
    }
    
    void onDestroyed()
    {
           //m_game_state.addVisual(new Explosion(m_bounds.getPos().x, m_bounds.getPos().y, (byte)20, 1000, 6000));
    }

    public void draw(Graphics g, int x, int y) 
    {
        if (m_speed_x != 0 || m_speed_y != 0) m_bounds.setAngle(Common.vectorToAngle(m_speed_x, m_speed_y));
        int ix = Common.toInt(m_bounds.getPos().x - x);
        int iy = Common.toInt(m_bounds.getPos().y - y);
        int index = m_drawable.getIndexFromAngle(m_bounds.getAngle());
        int clip_y = m_game_state.getTerrain().getWaterLevel() - Common.toInt(y);
        
        if (clip_y + iy  > Common.ceilInt(getRadius()))
        {
            g.setClip(0, 0, ResourceManager.CANVAS_WIDTH, clip_y);
            m_drawable.drawImage(g, index, ix, iy);

        }
        
        if (clip_y - iy  < Common.ceilInt(getRadius()))
        {
            g.setClip(0, clip_y + 1, ResourceManager.CANVAS_WIDTH, ResourceManager.CANVAS_HEIGHT - (clip_y + 1));
            m_water_drawable.drawImage(g, index, ix, iy);
        }
        
        
        m_game_state.resetClip(g);
    }
    
    
    void onCollision(GameObject go)
    {
        if (m_state != DESTROYED) 
        {
            m_game_state.createBlast(m_bounds.getPos().x, m_bounds.getPos().y, 25000, 0);
            m_game_state.getResourceManager().sound(ResourceManager.SOUND_BOMB);
        }
        m_state = DESTROYED;
    }
    
    void onGround()
    {
        if (m_state != DESTROYED) 
        {
            m_game_state.createBlast(m_bounds.getPos().x, m_bounds.getPos().y, 25000, 0);
            m_game_state.getTerrain().crater(Common.toInt(m_bounds.getPos().x), 15, 5);
            m_game_state.getResourceManager().sound(ResourceManager.SOUND_BOMB);
            m_state = DESTROYED;
        }
    }
    
    void enterWater()
    {
        m_game_state.createSplash(GameState.WATER, m_bounds.getPos().x, Common.toFP(m_game_state.getTerrain().getWaterLevel()), (byte)30, (byte)5, (byte)50, Common.toByte(m_speed_x / 5));
        m_game_state.getResourceManager().sound(ResourceManager.SOUND_WATER_SPLASH);
    }
    
    public void destruct()
    {
        super.destruct();
        m_state = DESTROYED;
        m_water_drawable = null;
    }
    
}
