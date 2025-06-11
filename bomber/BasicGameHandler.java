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
 *  File:       BasicGameHandler.java
 *  Content:    BasicGameHandler class
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;
// =========================================================================;
//	Name:	BasicGameHandler class
//	Desc:	provides common handler functions
// ==========================================================================;
public abstract class BasicGameHandler implements GameHandler 
{
    public void objectOutsideBounds(byte bounds_type, GameObject go)
    {
        if (go.getObjectType() == OBJECT)
        {
            go.destroy();
        }
        else if (bounds_type == BOTTOM) 
        {
            go.destroy();
        }
        else if (go instanceof Rotatable)
        {
            int angle = ((Rotatable)go).getAngle();
            if (bounds_type == LEFT && (angle > 90 * Common.FIXED  && angle < 270 * Common.FIXED))
            {
                ((Rotatable)go).setAngle(angle + 180 * Common.FIXED);
            }
            else if (bounds_type == RIGHT && (angle > 270 * Common.FIXED || angle < 90 * Common.FIXED))
            {
                ((Rotatable)go).setAngle(angle + 180 * Common.FIXED);
            }
            else if (bounds_type == TOP && angle > 180 * Common.FIXED)
            {
                ((Rotatable)go).setAngle(angle + 180 * Common.FIXED);
            }
        }

    }
    
    public void tankDestroyed(Tank t)
    {
    }

    public void zeppelinDestroyed(Zeppelin z)
    {
    }
    
    public void objectDestroyed(GameObject go) 
    {
    }
    
    public void planeDestroyed(Plane p) 
    {
    }

    public void buildingDestroyed(Building b) 
    {
    }
    
    
    public void objectInZone(GameObject go) 
    {
    }


    
}