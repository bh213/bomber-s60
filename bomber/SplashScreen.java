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
 *  File:       SplashScreen.java
 *  Content:    SplashScreen object
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;


// =========================================================================;
//	Name:	class SplashScreen
//      Desc:   Displays splash screen when the game is started
//
// ==========================================================================;

import java.util.*;
import javax.microedition.lcdui.*;
import com.nokia.mid.ui.FullCanvas;

public class SplashScreen extends FullCanvas implements GameScreen
{
    private Display     m_display;
    private BomberMIDlet 
                        m_midlet;
    
    private Drawable    m_drawable;
    //private Timer       m_timer;
    //private boolean     m_dismiss = false;
    //private boolean     m_loaded = false;

    public SplashScreen(Display display, Drawable drawable, BomberMIDlet midlet )
    {
        m_display = display;
        m_drawable = drawable;
        m_midlet = midlet;
        //m_timer = new Timer();
    }

    synchronized public void setLoaded()
    {
        //m_loaded = true;
        //if (m_dismiss) dismiss();
        dismiss();
    }
    
    protected void paint( Graphics g )
    {
        g.setColor(0xFFFFFF);  // black
        g.fillRect(0, 0, getWidth(), getHeight());
        m_drawable.drawImage(g, 0, getWidth() / 2, getHeight() / 2);
    }

    protected void showNotify()
    {
        //if (m_timer != null) m_timer.schedule( new CountDown(), 5000 );
    }

    synchronized private void dismiss()
    {
        //if (m_loaded == false) m_dismiss = true;
        //else performDismiss();
        performDismiss();
    }
    
     private void performDismiss()
    {
            /*if (m_timer != null) 
            {
                m_timer.cancel();
                m_timer = null;
            }*/
            m_midlet.screenIntro();
    }
    
     public Displayable getDisplayable()
     {
         return this;
     }
     
     public void pause()
     {
     }
     
     public void start() throws Exception
     {
     }
     
     public void stop()
     {
         //if (m_timer != null) m_timer.cancel();
         //m_timer = null;
         m_display = null;
         m_midlet = null;
         m_drawable = null;
     }
     
    /*private class CountDown extends TimerTask 
    {
        public void run()
        {
            dismiss();
        }
    }*/
}
