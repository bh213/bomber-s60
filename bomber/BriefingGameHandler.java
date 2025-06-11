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
 *  File:       BriefingGameHandler.java
 *  Content:    BriefingGameHandler class
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	BriefingGameHandler class
//	Desc:	shows briefing for the mission
// ==========================================================================;

public class BriefingGameHandler extends BasicGameHandler 
{
    protected GameState m_game_state;
    protected String[]  m_message;

    protected int       m_object_index;
    protected int      m_timer;
    
    protected GameObject[] m_objects;
    protected byte      m_finished = GameHandler.NOT_FINISHED;
    protected boolean   m_is_transition;
    protected Point     m_scroll_point;
    protected byte      m_message_index;
    
    
    protected static final int    SHOW_TIME  = 2000;
    protected static final int    TRANSITION_TIME  = 200;
    
    
    public BriefingGameHandler(GameState gs, String[] message, GameObject[] objects) 
    {
        init(gs);
        m_message = message;
        m_objects = objects;
        m_object_index = 0;
        //m_game_state.setScreenOptions(0, m_game_state.getHeight() - 30);
        m_game_state.setScreenOptions(30, m_game_state.getHeight() - 60);
        m_game_state.disableHandling();
        m_is_transition = false;
        m_scroll_point = new Point();
        m_scroll_point.set(m_objects[0].getPos());
    }
    
    public void init(GameState gs) 
    {
        m_game_state = gs;
    }
    
    public Object getScroll() 
    {
        return m_scroll_point;
    }
    
    
    public byte getScrollType()
    {
        return CENTER_POINT;
    }


    public void handle(int delta) 
    {
                
        m_timer += delta;
        //m_scroll_timer += delta;
        Helper.handlePressAnyKey(delta);

        if (m_is_transition)
        {
            GameObject go = m_objects[m_object_index];
            GameObject go2 = m_objects[(m_object_index + 1) % m_objects.length];
            
            if (m_timer > TRANSITION_TIME) 
            {
                m_timer = TRANSITION_TIME;
                m_is_transition = false;
                m_object_index = (m_object_index + 1) % m_objects.length;
            }

            m_scroll_point.set(go.getPos().x + Common.div(Common.mul((go2.getPos().x - go.getPos().x), m_timer), TRANSITION_TIME),
                               go.getPos().y + Common.div(Common.mul((go2.getPos().y - go.getPos().y), m_timer), TRANSITION_TIME));
            
        }
        else
        {
            if (m_timer > SHOW_TIME)
            {
                m_timer = 0;
                m_is_transition = true;
            }
            m_scroll_point.set(m_objects[m_object_index].getPos());
        }
        
        
    }
    
    
    public byte isFinished() 
    {
        return m_finished;
    }
    
   
    private boolean isLastMessage()
    {
        if (m_message_index == m_message.length - 1) return true;
        else return false;
    }
    
    public void event(byte type, boolean pressed)
    {
        if ((type == FIRE_A || type == FIRE_B) && pressed) 
        {
            if (isLastMessage()) m_finished = GameHandler.SUCCESS;
            else m_message_index++;
            
        }
    }
    
    public void draw(Graphics g, int x, int y)
    {
            Drawable icon = m_game_state.getResourceManager().getIcons();
            for (int i = 0; i < m_objects.length; i++)
            {
                if (m_objects[i] == null) continue;
                Helper.drawIconAboveObject(g, m_objects[i], icon, 0, x, y);
            }
        
            //g.setColor(0x000000);
            //g.fillRect(0, 0, m_game_state.getWidth(), 30);
            m_game_state.getResourceManager().getBomberTitle().drawImage(g, 2, 0, 0);   // draw 'briefing' banner
            m_game_state.getResourceManager().getBomberTitle().drawImage(g, 4, 0, m_game_state.getHeight() - 30);   // draw empty banner
              
            Helper.drawString(g, m_game_state.getLevelName(), 10, 35, ResourceManager.FONT_GREEN);
            
  
            //g.setColor(255, 255, 51);
            g.setColor(0);
            Helper.drawText(g, Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD,  Font.SIZE_SMALL), m_message[m_message_index], 10, 58 , m_game_state.getWidth() - 10, 150, 0);

            if (isLastMessage() == false)
            {
                g.drawString(Str.next, m_game_state.getHeight() - 30, m_game_state.getWidth() - 3, Graphics.BASELINE | Graphics.RIGHT);
            }
            Helper.drawPressAnyKey(g, m_game_state.getHeight() - 20, m_game_state.getWidth(), ResourceManager.FONT_YELLOW);
    }
    
}
