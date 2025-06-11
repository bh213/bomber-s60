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
 *  File:       InstructionsScreen.java
 *  Content:    InstructionsScreen object
 *  Created:    Januar 2003
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;

import javax.microedition.lcdui.*;


// =========================================================================;
//	Name:	class InstructionsScreen
//      Desc:   Displays instructions
//
// ==========================================================================;
public class InstructionsScreen extends Form implements GameScreen, CommandListener
{
    
    private Command             m_ok;
    private BomberMIDlet        m_midlet;
    
    public InstructionsScreen(BomberMIDlet midlet)
    {
        super(Str.instructions);
        m_midlet = midlet;
        m_ok = new Command(Str.ok, Command.EXIT, 1);
        addCommand(m_ok);
        setCommandListener(this);
        append(new StringItem(null, Str.instructions_text));
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
                m_midlet.screenOptions();
            }
        }
    }
    
    
}
