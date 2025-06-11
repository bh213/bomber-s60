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
 *  File:       BomberMIDlet.java
 *  Content:    BomberMIDlet class
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/


package bomber;


import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;


public class BomberMIDlet extends MIDlet 
{
    private Display             m_display;    // The display for this MIDlet
    private ResourceManager     m_resource_manager;
    private boolean             m_started = false;
    
    
    public BomberMIDlet()
    {
            m_display = Display.getDisplay(this);
            m_resource_manager = new ResourceManager(this);

    }
// =========================================================================;
//	Name:	void fatalError(String s)
//	Desc:	Displays fatal error and exits the app
//
// ==========================================================================;
    
    public void fatalError(String s)
    {
          m_display.setCurrent(new Alert(Str.fatal_error, s, null, AlertType.ERROR));
          destroyApp(false);
          notifyDestroyed();
    }
    
    ResourceManager getResourceManager()
    {
        return m_resource_manager;
    }
    
    
    protected synchronized GameCanvas stopCurrent()
    {
        
            if (m_display.getCurrent() != null)
            {
                ((GameScreen)m_display.getCurrent()).stop();
                if (m_display.getCurrent() instanceof GameCanvas) return (GameCanvas)m_display.getCurrent();
            }
            return null;
         
    }
    
// =========================================================================;
//	Name:	void screenGame()
//	Desc:	Displays game screen and starts the game
//
// ==========================================================================;
    public synchronized void screenGame() 
    {
        GameCanvas gc = stopCurrent();
        try
        {
            if (gc != null) 
            {
                gc.init(this, Game.NORMAL);
                gc.start();
            }
            else 
            {
                gc = new GameCanvas(this, Game.NORMAL);
                gc.start();
                m_display.setCurrent(gc);
            }
        }
        catch (Exception e)
        {
            fatalError(e.getMessage());
        }
    }
    
    
// =========================================================================;
//	Name:	void screenIntro()
//	Desc:	Displays intro for the game
//
// ==========================================================================;
    public synchronized void screenIntro() 
    {
        GameCanvas gc = stopCurrent();
        try
        {
            if (gc != null) 
            {
                
                gc.init(this, Game.INTRO);
                gc.start();
            }
            else 
            {
                gc = new GameCanvas(this, Game.INTRO);
                gc.start();
                m_display.setCurrent(gc);
            }
            
        }
        catch (Exception e)
        {
            fatalError(e.getMessage());
        }
        
    }
    
    
     public synchronized void screenOptionsNoCleanup() 
    {
        GameCanvas gc = null;
        try
        {
            if (m_display.getCurrent() != null)
            {
                if (m_display.getCurrent() instanceof GameCanvas) gc = (GameCanvas)m_display.getCurrent();
            }

            
        /*    if (gc != null) 
            {
                gc.init(this, Game.INTRO);
                gc.start();
            }
            else 
            {*/
                gc = new GameCanvas(this, Game.INTRO);
                gc.start();
                m_display.setCurrent(gc);
            //}
            
        }
        catch (Exception e)
        {
            fatalError(e.getMessage());
        }
        
    }
    
    
// =========================================================================;
//	Name:	void screenOptions()
//	Desc:	Displays main options screen
//
// ==========================================================================;
    
    public synchronized void screenOptions()
    {
        screenIntro();
    }
    
    
    
    
    
// =========================================================================;
//	Name:	void screenPlayerName()
//	Desc:	Gets player name
//
// ==========================================================================;

    public synchronized void screenPlayerName()
    {
        stopCurrent();
        m_display.setCurrent(new PlayerNameScreen(this));
    }
    
    
// =========================================================================;
//	Name:	void highScore()
//	Desc:	Displays high score
//
// ==========================================================================;

    public synchronized void highScore()
    {
        stopCurrent();
        HighScoreScreen hss = new HighScoreScreen(this);
        m_display.setCurrent(hss);
        hss.repaint();
    }
// =========================================================================;
//	Name:	void instructions()
//	Desc:	Displays instructions for playing the game
//
// ==========================================================================; 
    public synchronized void instructions()
    {
        stopCurrent();
        m_display.setCurrent(new InstructionsScreen(this));
    }

// =========================================================================;
//	Name:	void quit()
//	Desc:	quits the applications
//
// ==========================================================================; 
    void quit()
    {
            destroyApp(false);
            notifyDestroyed();
    }
    
    
    public synchronized void startApp() 
    {
        try
        {
            if (m_started)
            {
                ((GameScreen)m_display.getCurrent()).start();
            }
            else
            {

                m_started = true;
                SplashScreen splash = new SplashScreen(m_display, m_resource_manager.loadTitleImage(), this);

                m_display.setCurrent(splash);

                m_resource_manager.load();

                splash.setLoaded();
                    //highScore();
                    //screenOptions();
                    //screenGame();
                    //
                
            }
        }
        catch(Exception e)
        {
            fatalError(e.getMessage());
        }
    }
    
    public synchronized void pauseApp() 
    {
        ((GameScreen)m_display.getCurrent()).pause();
        System.gc();
    }
    
    public synchronized void destroyApp(boolean unconditional) 
    {
        ((GameScreen)m_display.getCurrent()).stop();
        m_display = null;
        m_resource_manager = null;
    }
    

  
}
