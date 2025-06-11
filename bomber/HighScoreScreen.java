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
 *  File:       HighScoreScreen.java
 *  Content:    HighScoreScreen object
 *  Created:    Januar 2003
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;


// =========================================================================;
//	Name:	class HighScoreScreen
//      Desc:   Displays high score
//
// ==========================================================================;

import java.util.*;
import javax.microedition.lcdui.*;
import com.nokia.mid.ui.FullCanvas;

public class HighScoreScreen extends FullCanvas implements GameScreen
{
    private BomberMIDlet m_midlet;
    private Drawable    m_background;
    private Storage     m_storage;

    public HighScoreScreen(BomberMIDlet midlet)
    {
        m_midlet = midlet;
        m_background = midlet.getResourceManager().getWoodTile(0);
        m_storage = m_midlet.getResourceManager().getStorage();
    }

    protected void keyPressed( int keyCode )
    {
        dismiss();
    }


    
    protected void paint( Graphics g )
    {
        int x = 0;
        int y = 0;
        while (y < ResourceManager.CANVAS_HEIGHT)
        {
            while (x < ResourceManager.CANVAS_WIDTH)
            {
                m_background.drawImage(g, 0, x, y);
                x += m_background.getWidth(0);
            }
            x = 0;
            y += m_background.getHeight(0);
        }
        Helper.drawStringCenter(g, Str.highscore, 10, ResourceManager.CANVAS_WIDTH, (byte)0);
        
        for (int i = 0; i < 10; i++)
        {
            int c_y = 40 + i * 14;
            String name;
            int score;
            
            if (i >= m_storage.getSize())
            {
                name = "------";
                score = 0;
            }
            else
            {
                name = m_storage.getName(i);
                score = m_storage.getScore(i);
            }
            
            Helper.drawScore(g, score, 10, c_y);
            g.setColor(0xffffff);
            //g.setFont(Font.getDefaultFont());
            g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.drawString(name.toUpperCase(), 60, c_y, g.TOP | g.LEFT);
            
            //Helper.drawString(g, name.toUpperCase(), 60, c_y, (byte)1);
            
        }
    }

    protected void pointerPressed( int x, int y )
    {
        dismiss();
    }

 

    private void dismiss()
    {
        m_midlet.screenOptions();    
    }
    
    public Displayable getDisplayable()
    {
        return this;
    }    
    
    public void pause()
    {
    }
    
    public void start()
    {
    }
    
    public void stop()
    {
    }
    
}
