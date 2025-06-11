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
 *  File:       Debris.java
 *  Content:    Debris GameObject
 *  Created:    Januar 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/


package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class Debris
//
// ==========================================================================;

public class Debris extends Building
{

   public Debris(GameState gs, int x, byte hitpoints)
    {
        super(gs, gs.getResourceManager().getDebris(), x, hitpoints);
    }
    
    void collided(GameObject go, int delta) 
    {
        if (m_state == DESTROYED) return;
        
        m_hitpoints -= go.getDamage(delta);
        
        if (m_hitpoints <= 0)
        {
           m_state = DESTROYED;
           m_game_state.createExplosion(m_bounds.getPos().x, m_bounds.getPos().y);
           m_game_state.getTerrain().crater(Common.toInt(m_bounds.getPos().x), 19, 3);
        }
    }
    
    void collidedTerrain(Terrain terrain, boolean water, int delta)
    {
    }

    byte getObjectType() 
    {
        return DEBRIS;
    }
    
}
