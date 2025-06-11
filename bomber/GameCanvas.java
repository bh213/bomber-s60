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
 *  File:       GameCanvas.java
 *  Content:    GameCanvas class
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;

import javax.microedition.lcdui.*;
import java.io.DataInputStream;
import com.nokia.mid.ui.FullCanvas;             // Nokia dependant code
import com.nokia.mid.ui.DirectGraphics.*;       // Nokia dependant code
import com.nokia.mid.ui.*;
import java.util.*;
import java.lang.Thread;

public class GameCanvas extends FullCanvas implements GameScreen, Runnable
{
    private Thread  m_thread;
    private int m_frame_rate = 0;
    private final int MS_PER_FRAME = 20;
    private BomberMIDlet        m_midlet;           // reference to bomber
    private ResourceManager     m_resource_manager; // reference to resource manager
    private boolean             m_paused;           // is game currently paused
    private Game                m_game;             // reference to game object, that contains whole game
    private byte                m_game_mode;        // game mode (INTRO, NORMAL, ...)
    private boolean             m_quit;             // quit request
    
    public GameCanvas(BomberMIDlet bm, byte mode) throws Exception
    {
       init(bm, mode);
    }
    
    public void init(BomberMIDlet bm, byte mode) throws Exception
    {
        m_midlet = bm;
        m_resource_manager = m_midlet.getResourceManager();
        m_game_mode = mode;
        m_quit = false;
        m_paused = false;
        System.gc();    // collect all garage before start

    }
    
    
     
    public void paint(Graphics g) 
    {
        synchronized (this)
        {
            if (m_game != null) m_game.draw(g);
        }
        //g.setColor(255,255,255);
        //g.drawString(Integer.toString(m_frame_rate), 150, 20, g.LEFT | g.BASELINE);
        //g.drawString(Integer.toString(xxx), 150, 40, g.LEFT | g.BASELINE);
        //g.drawString(Long.toString(Runtime.getRuntime().freeMemory()), 100, 40, g.LEFT | g.BASELINE);

            
        //g.drawString(Long.toString(Runtime.getRuntime().freeMemory()), 10, 40, g.LEFT | g.BASELINE);
        
        if (m_paused)
        {
            g.setColor(80, 80, 80);
            for (int i = 0; i < ResourceManager.CANVAS_HEIGHT; i+=2)
            {
                g.drawLine(0, i, ResourceManager.CANVAS_WIDTH - 1, i);
            }
            
            for (int i = 0; i < ResourceManager.CANVAS_WIDTH; i+=2)
            {
                g.drawLine(i, 0, i, ResourceManager.CANVAS_HEIGHT - 1);
            }

            Helper.drawStringCenter(g, Str.paused, ResourceManager.CANVAS_HEIGHT / 2, ResourceManager.CANVAS_WIDTH, ResourceManager.FONT_YELLOW);
            if (m_game_mode != Game.INTRO)
            {
                
                m_resource_manager.getBomberTitle().drawImage(g, 4, 0, ResourceManager.CANVAS_HEIGHT - 30);   // draw empty banner
                
                Helper.drawString(g, Str.exit, 2, ResourceManager.CANVAS_HEIGHT - 12,  ResourceManager.FONT_RED);
                Helper.drawStringLeft(g, Str.resume, ResourceManager.CANVAS_WIDTH, ResourceManager.CANVAS_HEIGHT - 12,  ResourceManager.FONT_GREEN);
            }
        }
        
    }
    protected synchronized void keyPressed(int keyCode) 
    {
        if (keyCode == KEY_SOFTKEY1 || keyCode == KEY_SOFTKEY2)
        {
            if (m_game_mode == Game.INTRO)
            {
                m_paused = false;
            }
            else if (m_paused == true)
            {    
                if (keyCode == KEY_SOFTKEY1) 
                {
                    m_quit = true;
                    return;
                }
                else m_paused = false;
            }
            else m_paused = true;
        }
        
        

        if (m_game == null) return;
        switch(getGameAction(keyCode))
        {
            case UP     : m_game.event(GameHandler.UP, true); break;
            case DOWN   : m_game.event(GameHandler.DOWN, true); break;
            case LEFT   : m_game.event(GameHandler.LEFT, true); break;
            case RIGHT  : m_game.event(GameHandler.RIGHT, true); break;
            case FIRE  : m_game.event(GameHandler.FIRE_A, true); break;
            default:
            {
//                if (keyCode == KEY_NUM4) m_game.event(GameHandler.FIRE_A, true); 
  //              else if (keyCode == Canvas.KEY_NUM1 || keyCode == Canvas.KEY_POUND || keyCode == Canvas.KEY_STAR) m_game.event(GameHandler.FIRE_B, true); 
                  if ((keyCode >= Canvas.KEY_NUM0 && keyCode <= Canvas.KEY_NUM9) || keyCode == Canvas.KEY_POUND || keyCode == Canvas.KEY_STAR) m_game.event(GameHandler.FIRE_B, true); 
            }
        }
    }
    
    protected  void keyReleased(int keyCode) 
    {
            if (m_game == null) return;
            switch(getGameAction(keyCode))
            {
                case UP     : m_game.event(GameHandler.UP, false); break;
                case DOWN   : m_game.event(GameHandler.DOWN, false); break;
                case LEFT   : m_game.event(GameHandler.LEFT, false); break;
                case RIGHT  : m_game.event(GameHandler.RIGHT, false); break;
                case FIRE  : m_game.event(GameHandler.FIRE_A, false); break;
                default:
                {
                    
                    if ((keyCode >= Canvas.KEY_NUM0 && keyCode <= Canvas.KEY_NUM9) || keyCode == Canvas.KEY_POUND || keyCode == Canvas.KEY_STAR) m_game.event(GameHandler.FIRE_B, false); 
                }
            }
    }

    
    public synchronized void start() throws Exception
    {
        if (m_thread == null)
        {
            if (m_game != null) m_game.destruct();
            m_game = new Game(m_resource_manager, getWidth(), getHeight(), m_game_mode);
            m_thread = new Thread(this);
            m_thread.start();
        }
    }

    public synchronized void stop()
    {
        m_thread = null;
    }

    public synchronized void pause()
    {
        m_paused = true;
    }
    
    public Displayable getDisplayable()
    {
        return this;
    }
    
    private void handle(int delta)
    {
        try
        {
            m_game.handle(delta);
        }
        catch(Exception e)
        {
            m_midlet.fatalError(e.getMessage());
        }
    }
    
    
    public void run() 
    {
        Thread current_thread = Thread.currentThread();
        try
        {
            long mid_time = System.currentTimeMillis();
            while (true)
            {
                if (current_thread != m_thread)
                {
                    break;
                }
                    
                if (m_paused)
                {
                    Thread.sleep(200);
                    if (m_quit)
                    {
                        m_midlet.screenOptionsNoCleanup();
                        break;    // quit
                    }
                    continue;
                }

                long delta = (int)(System.currentTimeMillis() - mid_time);
                if (delta > 200) delta = 200;       
                if (delta < MS_PER_FRAME)
                {
                    Thread.sleep(MS_PER_FRAME - delta);
                    delta = System.currentTimeMillis() - mid_time;
                }
                mid_time = System.currentTimeMillis();
                switch(m_resource_manager.getStorage().getDifficultyLevel())
                {
                    case 0: delta = (delta * 60) / 100; break;
                    case 1: delta = (delta * 80) / 100; break;
                }
                synchronized(this)
                {
                    handle((int)delta);
                    if (m_game.isQuit()) 
                    {
                        m_quit = true;
                    }
                    m_frame_rate = (int)(1000 / delta);
                    repaint();
                    serviceRepaints();
                }
                
                if (m_quit)
                {
                    m_midlet.screenOptionsNoCleanup();
                    break;    // quit
                }

                
            }
        }
        catch (InterruptedException e)
        {
        }
        catch(Exception e)
        {
            //System.out.println(e.toString());
            e.printStackTrace();
            m_midlet.fatalError(e.getMessage());
        }
        
        /*synchronized (this)
        {
            if (m_game != null) m_game.destruct();
            m_game = null;
            //m_midlet = null;
        }*/
    }
 
    protected  void hideNotify() 
    {
        pause();
    }
 

    
    
    
}






