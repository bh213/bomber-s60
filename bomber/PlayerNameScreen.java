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
 *  File:       PlayerNameScreen.java
 *  Content:    PlayerNameScreen object
 *  Created:    Januar 2003
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;

import javax.microedition.lcdui.*;


// =========================================================================;
//	Name:	class PlayerNameScreen
//      Desc:   gets player name
//
// ==========================================================================;
public class PlayerNameScreen extends TextBox implements GameScreen, CommandListener
{
    
    private Command             m_ok;
    private Command             m_cancel;
    private BomberMIDlet        m_midlet;
    public PlayerNameScreen(BomberMIDlet midlet)
    {
        super(Str.opt_get_player_name, midlet.getResourceManager().getStorage().getPlayerName(), 12, TextField.ANY);
        m_midlet = midlet;
        m_ok = new Command(Str.ok, Command.OK, 1);
        m_cancel = new Command(Str.cancel, Command.CANCEL, 2);
        addCommand(m_ok);
        addCommand(m_cancel);
        setCommandListener(this);

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
 
    public void commandAction (Command c, Displayable d) 
    {
         if (d == this) 
         {
            if (c == m_ok) 
            {
                stop();
                try
                {
                    m_midlet.getResourceManager().getStorage().setPlayerName(getString());
                }
                catch (Exception e)
                {
                    m_midlet.fatalError(e.getMessage());
                }
                m_midlet.screenOptions();
            }
            else if (c == m_cancel)
            {
                stop();
                m_midlet.screenOptions();

            }
        }
    }
    
    
}
