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
 *  File:       GameHandler.java
 *  Content:    GameHandler interface
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	GameHandler interface
//	Desc:	Handles different missions 
//
// ==========================================================================;

public interface GameHandler 
{
    // bounds constants
    static final byte TOP           = 0;
    static final byte LEFT          = 1;
    static final byte RIGHT         = 2;
    static final byte BOTTOM        = 3;
    // scroll type
    static final byte POINT         = 0;
    static final byte CENTER_POINT  = 1;
    static final byte OBJECT        = 2;
    
    // finished
    static final byte NOT_FINISHED  = 0;    // mode is not finished yet, it will continue running
    static final byte SUCCESS       = 1;    // mode is successfully finished (e.g, mission passed)
    static final byte FAILED        = -1;   // mode failed (e.g, game over)
    static final byte QUIT          = 66;   // quit is requested
    // events
    //static final byte LEFT          = 3;
    //static final byte RIGHT         = 4;
    static final byte UP            = 3;
    static final byte DOWN          = 4;

    static final byte FIRE_A        = 5;
    static final byte FIRE_B        = 6;
    static final byte SOFT          = 7;
    
    byte getScrollType();                   // returns current scroll tyoe
    Object   getScroll();                   // returns current scroll object (Point or GameObject)

    void draw(Graphics g, int x, int y);  // draw called after main draw has been called
    
    void planeDestroyed(Plane p);           // called when plane has been destroyed
    void buildingDestroyed(Building b);     // called when building has been destroyed
    void tankDestroyed(Tank t);             // called when tank is destroyed
    void zeppelinDestroyed(Zeppelin z);     // called when zeppelin is destroyed
    
    void objectDestroyed(GameObject go);    // called when any other object has been destroyed
    
    void objectOutsideBounds(byte bounds_type,
                             GameObject go);// called when given object is outsize bounds
    
    void objectInZone(GameObject go);       // called when given object is in zone
    
    byte isFinished();                      // is the game finished (successful or not)
    void handle(int delta);                // handles any time specific tasks
    void event(byte type, boolean pressed); // for every (key press or release)
    
}
