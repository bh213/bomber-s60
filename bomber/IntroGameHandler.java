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
 *  File:       IntroGameHandler.java
 *  Content:    IntroGameHandler class
 *  Created:    Januar 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	IntroGameHandler class
//	Desc:	Handles intro game screen
// ==========================================================================;
public class IntroGameHandler extends BasicGameHandler 
{
    private GameState m_game_state;
    private Point     m_scroll;
    private byte      m_finished = GameHandler.NOT_FINISHED;
    private byte      m_bomb_count;
    private int       m_show_timer;
    private Plane     m_plane;
    private int       m_selected = 0;
    private BomberMIDlet
                      m_midlet;
    private String[]  m_menu;
    
    
    
    public IntroGameHandler(GameState gs) throws Exception
    {
        m_game_state = gs;
        //m_game_state.setScreenOptions(30, m_game_state.getHeight() - 60);
        m_scroll = new Point(0, Common.toFP(-200));
        m_plane = m_game_state.createPlane(0, Common.toFP(-300), Common.toFP(-200), 0, 0);
        m_game_state.setBounds(-500, -500, 200);
        m_game_state.disableLookahead();
        m_midlet = m_game_state.getResourceManager().getMidlet();
        m_menu = new String[7];
        buildMenu();
        int h = m_game_state.getResourceManager().getBomberTitle().getHeight(0);
        m_game_state.setScreenOptions(h, m_game_state.getHeight() - (h + /*m_game_state.getResourceManager().getBomberTitle().getHeight(1)*/30));
    }
    
    public Object getScroll() 
    {
        if (m_plane != null) return m_plane;
        else return m_scroll;
    }
    
    
    public byte getScrollType()
    {
        if (m_plane != null) return OBJECT;
        else return POINT;
    }

    
    public void handle(int delta) 
    {
        m_show_timer += delta;
        if (m_plane == null)
        {
            if (m_show_timer >= 1000 && m_bomb_count == 0)
            {
                m_game_state.createBomb(Common.toFP(20), Common.toFP(-240), Common.toFP(4 + GameState.random.nextInt() % 3), 0);
                m_bomb_count++;

            }
            if (m_show_timer > 1500 && m_bomb_count == 1)
            {
                m_game_state.createBomb(Common.toFP(80), Common.toFP(-240), Common.toFP(6 + GameState.random.nextInt() % 3), 0);
                m_bomb_count++;

            }
            if (m_show_timer > 2000 && m_bomb_count == 2)
            {
                m_game_state.createBomb(Common.toFP(120), Common.toFP(-240), Common.toFP(5 + GameState.random.nextInt() % 3), 0);
                m_bomb_count++;
            }
            if (m_show_timer > 5100 && m_bomb_count == 3)
            {
                m_game_state.setHandleSpeed(256);
                m_bomb_count++;
            }
            if (m_show_timer > 5800 && m_bomb_count == 4)
            {
                m_game_state.setHandleSpeed(Common.FIXED);
                m_bomb_count++;
            }
            else if (m_show_timer > 7000 && m_bomb_count == 5)
            {
                m_show_timer = 1000;
                m_bomb_count = 0;
                
            }

        }
        else if (m_show_timer > 1500)
        {
            m_plane.destruct();
            m_plane = null;
            m_show_timer = 0;
        }
    }
    
    
    public byte isFinished() 
    {
        return m_finished;
    }
    
    public void event(byte type, boolean pressed) 
    {
        if (pressed)
        {
            if (type == FIRE_A || type == FIRE_B) handleCommands((byte)0);
            else if (type == LEFT) handleCommands((byte)-1);
            else if (type == RIGHT) handleCommands((byte)1);
            if (type == UP) m_selected = (m_selected - 1 + m_menu.length) % m_menu.length;
            if (type == DOWN) m_selected = (m_selected + 1) % m_menu.length;
        }
    }
    
    
// =========================================================================;
//	Name:	handleCommands(byte cmd)
//	Desc:	handles menu commands
//              parameter cmd:
//              0 - fire
//              1 - right
//              -1 - left
// ==========================================================================;
    
   
    private void handleCommands(byte cmd)
    {
        try
        {
            switch (m_selected)
            {
                        case 0: if (cmd == 0) m_midlet.screenGame(); break;
                        case 1: if (cmd == 0) m_midlet.instructions(); break;
                        case 2: if (cmd == 0) m_midlet.highScore(); break;
                        case 3: Storage s = m_game_state.getResourceManager().getStorage();
                                s.setSoundEnabled(!s.getSoundEnabled());
                                buildMenu();
                                break;
                        case 4: s = m_game_state.getResourceManager().getStorage();
                                byte level = s.getDifficultyLevel();
                                if (cmd == -1) level = (byte)Math.max(0, level - 1);
                                else level = (byte)Math.min(2, level + 1);
                                s.setDifficultyLevel(level);
                                buildMenu();
                                break;

                        case 5: if (cmd == 0) m_midlet.screenPlayerName(); break;  
                        case 6: if (cmd == 0) m_midlet.quit(); break;
            }
        }
        catch(Exception e)
        {
            m_game_state.getResourceManager().getMidlet().fatalError(e.getMessage());
        }
    }
    
    
    private void buildMenu()
    {
        Storage s = m_game_state.getResourceManager().getStorage();
        m_menu[0] = Str.opt_menu_play;
        m_menu[1] = Str.opt_menu_instructions;
        m_menu[2] = Str.opt_menu_highscores;
        
        if (s.getSoundEnabled()) m_menu[3] = Str.opt_menu_sound_on;
        else m_menu[3] = Str.opt_menu_sound_off;
        
        if (s.getDifficultyLevel() == 0) m_menu[4] = Str.opt_menu_level_easy;
        else if (s.getDifficultyLevel() == 1) m_menu[4] = Str.opt_menu_level_normal;
        else m_menu[4] = Str.opt_menu_level_hard;



        m_menu[5] = Str.opt_menu_set_player_name;
        m_menu[6] = Str.opt_menu_exit;
        
    }
    
    
    public void draw(Graphics g, int x, int y)
    {

            m_game_state.getResourceManager().getBomberTitle().drawImage(g, 0,0, 0);
            
            int h = m_game_state.getResourceManager().getBomberTitle().getHeight(1);
            //m_game_state.getResourceManager().getBomberTitle().drawImage(g, 1, 0, ResourceManager.CANVAS_HEIGHT - h);

            //m_game_state.getResourceManager().getBomberTitle().drawImage(g, 4, 0, ResourceManager.CANVAS_HEIGHT - 30);   // draw empty banner
            m_game_state.getResourceManager().getBomberTitle().drawImage(g, 1, 0, ResourceManager.CANVAS_HEIGHT - 30);   // draw empty banner
            Helper.drawMenu(g, m_menu, 20, 70, m_selected);

            //g.setColor(0xffffff);
            //Helper.drawText(g, Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN,  Font.SIZE_SMALL), Str.intro, 10, ResourceManager.CANVAS_HEIGHT - 27 , ResourceManager.CANVAS_WIDTH - 4, 28, 0);

            
    }
}
