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
 *  File:       AIPlane.java
 *  Content:    AIPlane object
 *  Created:    Januar 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/


package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class AIPlane
//      Desc:	AI controlled plane
// ==========================================================================;
public class AIPlane extends Plane implements CanTarget
{
    protected GameObject          m_target;     // who are we trying to shot?
    protected int                 m_ai_timer;   // when next ai update should be done
    

    public static final int FIRE_RANGE        = 80 * Common.FIXED;
    
    public static final int TO_CLOSE          = 60 * Common.FIXED;
    
    
    public static final int IGNORE_TERRAIN    = 150 * Common.FIXED;
    
    public static final int AI_RATE           = 400;
    


    
    public AIPlane(GameState gs, Drawable sprite, Drawable water_sprite, int turn_rate, int fire_rate, int engine_power, int mass, int hitpoints)
    {
        super(gs, sprite, water_sprite, turn_rate, fire_rate, engine_power, mass, hitpoints);
    }
    
    
   public int handle(int delta) 
   {
        int ret_val = super.handle(delta);
        m_ai_timer += delta;
        if (m_target != null && m_ai_timer > AI_RATE)
        {
            m_ai_timer -= AI_RATE;

            if (m_target.isAlive() == false) 
            {
                m_target = null;
            }
            else
            {

                int dist = getPos().distance(m_target.getPos());
                boolean is_to_close = dist < TO_CLOSE;
                if (dist < FIRE_RANGE) setFire(true);
                else setFire(false);
                

/*                if (Math.abs(dist) < FIRE_RANGE &&  Math.abs(m_target.getPos().x - m_bounds.getPos().x) < MAX_X_RANGE)
                {
                        fire();
                }
                else
                {
                    //if (dist < 0) m_gun_angle += ROTATE_SPEED * delta;
                    //else m_gun_angle -= ROTATE_SPEED * delta;
                }
 */
 
                    int line_dist = Common.distancePointToLine(m_target.getPos().x - m_bounds.getPos().x, m_target.getPos().y - m_bounds.getPos().y, Common.cos(getAngle()), Common.sin(getAngle()));
                    if (line_dist > 0) 
                    {
                        this.setTurnUp(!is_to_close);
                        this.setTurnDown(is_to_close);
                    }
                    else 
                    {
                        this.setTurnUp(is_to_close);
                        this.setTurnDown(!is_to_close);

                    }

                    if (dist > IGNORE_TERRAIN)
                    {
                        if (getPos().y > Common.toFP(m_game_state.getTerrain().getMaxHeight() - 20))
                        {
                            int angle = getAngle();
                            if (angle < Common.FIXED * (180 + 30) && angle > Common.FIXED * 90) 
                            {
                                    this.setTurnUp(false);
                                    this.setTurnDown(true);
                            }
                            else if (angle > 330 * Common.FIXED || angle < Common.FIXED * 90) 
                            {
                                    this.setTurnUp(true);
                                    this.setTurnDown(false);
                            }

                        }
                    }
            }
        }
        if (m_target == null)
        {
            setFire(false);
        }
        
        return ret_val;
    }
   
    public void setTarget(GameObject go)
    {
        m_target = go;
    }

    public GameObject getTarget()
    {
        return m_target;
    }

    short getType() 
    {
        return (byte)(super.getType() | CAN_TARGET);
    }
}

