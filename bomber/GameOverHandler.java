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
 *  File:       GameOverHandler.java
 *  Content:    GameOverHandler class
 *  Created:    Januar 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	GameOverHandler class
//	Desc:	handler end of game
// ==========================================================================;
public class GameOverHandler extends BasicGameHandler 
{
    private GameState m_game_state;
    private Point     m_scroll;
    private byte      m_finished = GameHandler.NOT_FINISHED;
    private int       m_high_score_place;
    
    
    public GameOverHandler(GameState gs, int x, int y) throws Exception
    {
        init(gs);
        m_scroll = new Point(x, y);
        m_high_score_place = m_game_state.getResourceManager().getStorage().isHighScore(m_game_state.getScore());
        m_game_state.getResourceManager().getStorage().addScore(m_game_state.getScore());
    }
    
    public void init(GameState gs) 
    {
        m_game_state = gs;
        m_game_state.setScreenOptions(30, m_game_state.getHeight() - 60);
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
        Helper.handlePressAnyKey(delta);
    }
    
    
    public byte isFinished() 
    {
        return m_finished;
    }
    
    

    
    public void objectOutsideBounds(byte bounds_type, GameObject go) 
    {
        if (bounds_type == BOTTOM) go.destruct();
        else if (go instanceof Rotatable)
        {
            ((Rotatable)go).setAngle(((Rotatable)go).getAngle() + 180 * Common.FIXED);
        }
    }
    

    
    public void event(byte type, boolean pressed)
    {
        if ((type == FIRE_A || type == FIRE_B) && pressed) m_finished = GameHandler.SUCCESS;
    }
    
    public void draw(Graphics g, int x, int y)
    {
            g.setColor(0x000000);
            g.fillRect(0, 0, m_game_state.getWidth(), 30);
            g.fillRect(0, m_game_state.getHeight() - 30, m_game_state.getWidth(), 30);
//            Helper.font.setSubFont((byte)0);
//            int px = -Helper.font.getStringWidth(m_message)/2 + m_game_state.getWidth()/2;
            Helper.drawStringCenter(g, Str.game_over, m_game_state.getHeight()/2, m_game_state.getWidth(), ResourceManager.FONT_YELLOW);
            
            if (m_high_score_place != -1)
            {
                Helper.drawStringCenter(g, Str.new_high_score, m_game_state.getHeight()/2 - 20, m_game_state.getWidth(), ResourceManager.FONT_GREEN);
            }
            
            //Helper.font.drawString(g, px, m_game_state.getHeight()/ 2, m_message);
            Helper.drawPressAnyKey(g, m_game_state.getHeight() - 20, m_game_state.getWidth(), ResourceManager.FONT_YELLOW);
            
            

    }
    
    
}
