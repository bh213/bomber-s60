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
 *  File:       MissionGameHandler.java
 *  Content:    MissionGameHandler class
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;
// =========================================================================;
//	Name:	MissionGameHandler class
//	Desc:	handles basic plane game
// ==========================================================================;
public class MissionGameHandler extends BasicGameHandler 
{
    protected GameState    m_game_state;        // reference to game state
    protected byte         m_lives;             // lives left
    protected GameObject[] m_targets;           // targets to destroy
    protected int          m_live_targets;      // number of targets left to kill
    protected Plane        m_control;           // plane under player's control
    protected GameObject   m_falling_plane;     // falling plane of the player
    protected byte         m_state;             // state of the game 
    protected Point        m_start_point;       // starting point of the game (coordinates)
    protected Point        m_enemy_start_point; // starting point for enemy planes
    protected int          m_start_angle;       // starting angle
    protected int          m_enemy_start_angle; // starting angle of enemy
    
    protected boolean      m_gun_state = false; // special states handling
    protected boolean      m_bomb_state = false;// special states handling
    protected boolean      m_lives_lost = false;// special states handling
    protected int          m_score_board_height;// height at which scoreboard is drawn
    protected int          m_enemy_planes;      // number of all enemy planes
    
    protected AIPlane      m_enemy_plane;   // WARN ME?
    protected int          m_medal_timer;
    
    protected int          m_starting_plane_blink;
    protected int          m_plane_destroyed_timer;
    
    protected int          m_indicator_blink;
    protected boolean      m_indicator_on;
    
    static final short     PLANE_DESTROYED_SHOW_TIME = 1000;
    
    static final byte      PLAYING = 0;
    static final byte      PLANE_FALLING = 1;
    static final byte      PLANE_DESTROYED = 2;
    static final byte      WAITING_FOR_START = 3;
    static final int       START_PLANE_BLINK = 400;
    
    

    public MissionGameHandler(GameState gs, GameObject[] targets) 
    {
        init(gs);
        m_score_board_height = m_game_state.getHeight() - m_game_state.getResourceManager().getScoreBoard().getHeight(0);
        m_game_state.setScreenOptions(0, m_score_board_height);
        m_targets = targets;
        m_state = WAITING_FOR_START;
        
    }
    
    public void init(GameState gs) 
    {
        m_game_state = gs;
        m_lives = 5;
        m_enemy_planes = 1 + m_game_state.getDifficultyLevel();
        if (m_enemy_planes == 0) m_game_state.disableSpecialState(GameState.ACE);
        
        m_medal_timer = (int)Common.toFP(gs.getMissionData()[0]);
        m_start_point = new Point(Common.toFP(gs.getMissionData()[1]), Common.toFP(gs.getMissionData()[2]));
        m_start_angle = Common.toFP(gs.getMissionData()[3]);
        m_enemy_start_point = new Point(Common.toFP(gs.getMissionData()[4]), Common.toFP(gs.getMissionData()[5]));
        m_enemy_start_angle = Common.toFP(gs.getMissionData()[6]);
    }
    
    
    public void buildingDestroyed(Building b) 
    {
        if (m_game_state.isTarget(b))
        {
            m_game_state.addScore(1);
        }
        else 
        {
            m_game_state.addScore(-2);
            m_game_state.clearSpecialState(GameState.SURGICAL_STRIKE);
        }
    }
    
    public void tankDestroyed(Tank t)
    {
        if (m_game_state.isTarget(t) == false) 
        {
            m_game_state.clearSpecialState(GameState.SURGICAL_STRIKE);
            m_game_state.addScore(2);
        }
        else m_game_state.addScore(5);

    }

    public void zeppelinDestroyed(Zeppelin z)
    {
        m_game_state.addScore(50);
    }

    
    
    public Object getScroll() 
    {
        switch (m_state)
        {
            case PLAYING : return m_control;
            case WAITING_FOR_START: return m_start_point;
            case PLANE_DESTROYED: return null;
            case PLANE_FALLING: return m_falling_plane;
        }
        return null;
    }
    
    
    public byte getScrollType()
    {
        switch (m_state)
        {
            case PLAYING : return OBJECT;
            case WAITING_FOR_START: return CENTER_POINT;
            case PLANE_DESTROYED: return OBJECT;
            case PLANE_FALLING: return OBJECT;
        }
        return OBJECT;
    }

    
    public void handle(int delta) 
    {
            if (m_enemy_plane != null && m_enemy_plane.isAlive() == false) m_enemy_plane = null;
            
            if (m_enemy_plane == null && m_enemy_planes > 0 )
            {
                m_enemy_planes--;
                m_enemy_plane = m_game_state.createAIPlane(1, m_enemy_start_point.x , m_enemy_start_point.y, m_enemy_start_angle, 0);                              
                m_enemy_plane.setTarget(m_control);
            }
                            
            if (m_enemy_plane != null)
            {
                m_indicator_blink += (int)delta;
                int blink;
                if (m_state == PLAYING)
                {
                    int distance = m_control.getPos().distance(m_enemy_plane.getPos());
                    if (distance < 400 * Common.FIXED) 
                    {
                        blink = (Common.toInt(distance)) * 2;
                    
                        if (m_indicator_blink > blink)
                        {
                            m_indicator_blink = 0;
                            m_indicator_on = !m_indicator_on;
                        }
                    }
                    else
                    {
                        m_indicator_on = true;
                    }
                }
                else m_indicator_on = true;
        }
            
            m_starting_plane_blink = (int)(m_starting_plane_blink + delta) % START_PLANE_BLINK;
            if (m_state == PLANE_FALLING)
            {
                if (m_falling_plane.isAlive() == false)
                {
                    m_state = PLANE_DESTROYED;
                    m_plane_destroyed_timer = PLANE_DESTROYED_SHOW_TIME;
                    m_falling_plane = null;
                    
                }
            }
            else if (m_state == PLANE_DESTROYED)
            {
                m_plane_destroyed_timer -= delta;
                if (m_plane_destroyed_timer <= 0) m_state = WAITING_FOR_START;
            }
            
            m_medal_timer -= delta;
            Helper.handlePressAnyKey(delta);
    }
    
    protected void checkStates()
    {
        if (m_gun_state == false && m_bomb_state == false && m_lives_lost == false) m_game_state.setSpecialState(GameState.CON_MAN);
        else 
        {
            if (m_gun_state == false && m_bomb_state == false) m_game_state.setSpecialState(GameState.KAMIKAZE);
            else if (m_gun_state == false) m_game_state.setSpecialState(GameState.BOMBER);
            else if (m_bomb_state == false) m_game_state.setSpecialState(GameState.GUNNER);
            if (m_lives_lost == false) m_game_state.setSpecialState(GameState.IRON_MAN);
        }
        if (m_enemy_planes == 0 && (m_enemy_plane == null || (m_enemy_plane != null && m_enemy_plane.isAlive() == false))) m_game_state.setSpecialState(GameState.ACE);
        if (m_medal_timer > 0) m_game_state.setSpecialState(GameState.GOLD_MEDAL);
    }
    
    
    public byte isFinished() 
    {
        if (m_lives <= 0) return FAILED;
        m_live_targets = 0;
        for (int i = 0; i < m_targets.length; i++)
        {
            if (m_targets[i] != null) m_live_targets++;
        }
        if (m_live_targets > 0) return NOT_FINISHED;
        checkStates();
        return SUCCESS;
        
        
    }
    
    
    public void objectDestroyed(GameObject go) 
    {
    }
    
    public void objectInZone(GameObject go) 
    {
    }
    
    public void objectOutsideBounds(byte bounds_type, GameObject go) 
    {
        super.objectOutsideBounds(bounds_type, go);
        if ((go == m_control && go instanceof Plane) && (bounds_type == LEFT || bounds_type == RIGHT))
        {
            ((Plane)go).setBombs((byte)5);
        }
    }
    
    public void planeDestroyed(Plane p) 
    {
        if (p == m_control) ControlableDestroyed(p);
        else m_game_state.addScore(20);
    }
    
    private void ControlableDestroyed(GameObject ctrl) 
    {
        if (m_lives > 0)
        {
            m_lives_lost = true;
            m_lives--;
            m_state = PLANE_FALLING;
            m_falling_plane = m_control.getFallingPlane();
        }
        else m_control = null;
        
    }
    
    protected int getBombs()
    {
        if (m_control == null) return 0;
        else return m_control.getBombs();
    }    
    
    
    private void handleTargets()
    {
        for (int i = 0; i < m_game_state.getObjectSize(); i++)
        {
            if ((m_game_state.getGameObject(i).getType() & GameObject.CAN_TARGET) != 0)
            {
                ((CanTarget)m_game_state.getGameObject(i)).setTarget(m_control);
            }
        }
    }
    
    
    public void event(byte type, boolean pressed)
    {
        
          if ((m_state == WAITING_FOR_START || m_state == PLANE_FALLING || m_state == PLANE_DESTROYED) && (type == FIRE_A || type == FIRE_B) && pressed )
          {
                m_control = m_game_state.createPlane(0, m_start_point.x, m_start_point.y, m_start_angle, 0);              
                m_state = PLAYING;
                handleTargets();
          }
          
          else if (m_state == PLAYING)
          {
              switch(type)
              {
                  case UP: m_control.setTurnUp(pressed); break;
                  case DOWN: m_control.setTurnDown(pressed); break;
                  case FIRE_A: m_control.setBomb(pressed); if (pressed) m_bomb_state = true; break;
                  case FIRE_B: m_control.setFire(pressed); if (pressed) m_gun_state = true; break;
              }
          }
    }
    
    public void draw(Graphics g, int x, int y)
    {
        Drawable icon = m_game_state.getResourceManager().getIcons();
        for (int i = 0; i < m_targets.length; i++)
        {
            if (m_targets[i] == null) continue;
            Helper.drawIconAboveObject(g, m_targets[i], icon, 0, x, y);
        }
        
        if (m_state == WAITING_FOR_START || m_state == PLANE_FALLING || m_state == PLANE_DESTROYED)
        {
            Helper.drawPressAnyKey(g, m_game_state.getHeight() - 50, m_game_state.getWidth(), ResourceManager.FONT_YELLOW);
            
            if (m_state == WAITING_FOR_START)
            {
                //Helper.drawStringCenter(g, m_game_state.getLevelName(), 14, m_game_state.getWidth(), ResourceManager.FONT_GREEN);
                Helper.drawString(g, m_game_state.getLevelName(), 40, 14, ResourceManager.FONT_GREEN);
                if (m_starting_plane_blink < START_PLANE_BLINK / 2)
                m_game_state.getPlaneDrawable(0).drawImageWithAngle(g, m_start_angle, Common.toInt(m_start_point.x - x), Common.toInt(m_start_point.y - y ));
            }
        }



        
            if (m_enemy_plane != null && m_indicator_on)
            {
                Helper.drawIndicator(g, x, y, m_enemy_plane, 0, 0, m_game_state.getWidth(), m_score_board_height);
            }

        

        int icon_y = 10;
        icon.drawImage(g, 2, 2, icon_y);
        Helper.drawNumber(g, m_live_targets, 16, icon_y + 2, 2);
        icon_y += 15;
        
        if (m_enemy_planes > 0 || (m_enemy_plane != null && m_enemy_plane.isAlive() == true))
        {
            icon.drawImage(g, 3, 2, icon_y);
            Helper.drawNumber(g, m_enemy_planes + 1, 16, icon_y + 2, 2);
            icon_y += 15;
        }
        
        if (m_medal_timer > 0)
        {
            icon.drawImage(g, 4, 2, icon_y);
            Helper.drawNumber(g, Common.toInt(m_medal_timer), 16, icon_y + 2, 2);
        }
        
        Helper.drawScoreBoard(g, m_score_board_height, m_game_state.getScore(), m_lives, getBombs());        
        

    }
}
