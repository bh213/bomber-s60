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
 *  File:       LevelCompleteHandler.java
 *  Content:    LevelCompleteHandler class
 *  Created:    Januar 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	LevelCompleteHandler class
//	Desc:	handles completition of the level
// ==========================================================================;
public class LevelCompleteHandler extends BasicGameHandler 
{
    protected GameState m_game_state;
    protected String    m_level_name;
    protected Point     m_scroll;
    protected int       m_fireworks_timer;
    protected int       m_ignore_keypress_timer;
    protected byte      m_finished = GameHandler.NOT_FINISHED;
    
    static final int    FIREWORKS_RATE = 900;
    static final int    IGNORE_KEYPRESS = 1000;
    
    public LevelCompleteHandler(GameState gs, String level_name, int x, int y) 
    {
        init(gs);
        m_level_name = level_name;
        m_scroll = new Point(x, y);
    }
    
    public void init(GameState gs) 
    {
        m_game_state = gs;
        m_game_state.setScreenOptions(30, m_game_state.getHeight() - 60);
        
        for (byte i = 0; i < m_game_state.SPECIAL_STATE_SIZE; i++)
        {
            if (m_game_state.isSpecialState(i)) 
                m_game_state.addScore(m_game_state.getSpecialStateScore(i));
        }
    }
    
    public Object getScroll() 
    {
        return m_scroll;
    }
    
    
    public byte getScrollType()
    {
        return POINT;
    }
    
    public void handle(int delta) 
    {
        m_ignore_keypress_timer += delta;
        m_fireworks_timer += delta;
        if (m_fireworks_timer > FIREWORKS_RATE)
        {
            m_fireworks_timer -= FIREWORKS_RATE;
            m_game_state.createFireworks(m_scroll.x + Math.abs(Common.toFP(GameState.random.nextInt() % 150 )), m_scroll.y + Math.abs(Common.toFP(GameState.random.nextInt() % 150)));
        }
        Helper.handlePressAnyKey(delta);
    }
    
    
    public byte isFinished() 
    {
        return m_finished;
    }
    
    public void event(byte type, boolean pressed)
    {
        if ((type == FIRE_A || type == FIRE_B) && pressed && m_ignore_keypress_timer > IGNORE_KEYPRESS) m_finished = GameHandler.SUCCESS;
    }
    
    public void draw(Graphics g, int x, int y)
    {
            Drawable icon = m_game_state.getResourceManager().getIcons();

            m_game_state.getResourceManager().getBomberTitle().drawImage(g, 3, 0, 0);   // draw 'victory' banner
            m_game_state.getResourceManager().getBomberTitle().drawImage(g, 4, 0, m_game_state.getHeight() - 30);   // draw empty banner

            int c_y = 40;
            
            for (byte i = 0; i < m_game_state.SPECIAL_STATE_SIZE; i++)
            {
                if (m_game_state.isSpecialState(i) )
                {
                    //icon.drawImage(g, 1, 1, c_y);
                    int color = ResourceManager.c_warm_colors[(i + 1) % ResourceManager.c_warm_colors.length];
                    Helper.drawBox(g, 1, c_y, color);

                    
                    
                    Helper.drawString(g, m_game_state.getSpecialStateName(i), 14, c_y, ResourceManager.FONT_YELLOW);
                    Helper.drawString(g, "+"+Integer.toString(m_game_state.getSpecialStateScore(i)), 130, c_y, ResourceManager.FONT_GREEN);
                    c_y += 10;
                }
            }


            Helper.drawString(g, Str.difficulty_bonus, 4, m_game_state.getHeight() - 42, ResourceManager.FONT_YELLOW);
            Helper.drawString(g, m_game_state.getDifficultyBonusMultiplyerAsString(), 130, m_game_state.getHeight() - 42, ResourceManager.FONT_GREEN);

            
            Helper.drawString(g, Str.score, 4, m_game_state.getHeight() - 26, ResourceManager.FONT_YELLOW);
            Helper.drawString(g, Integer.toString(m_game_state.getScore()), 130, m_game_state.getHeight() - 26, ResourceManager.FONT_GREEN);

            if (m_ignore_keypress_timer > IGNORE_KEYPRESS)
            {
                Helper.drawPressAnyKey(g, m_game_state.getHeight() - 20, m_game_state.getWidth(), ResourceManager.FONT_YELLOW);
            }
    }
}
