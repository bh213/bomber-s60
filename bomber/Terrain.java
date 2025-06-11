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
 *  File:       Terrain.java
 *  Content:    2D side scrolling terrain interface
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;


import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	interface Terrain
//	Desc:	Interface for handling and drawing 2D terrain
//
// ==========================================================================;
interface Terrain
{
    
    static final byte NO_COLLIDE = 0;
    static final byte WATER      = 1;
    static final byte GROUND     = 2;
    
    void destruct();
    
// global (very fast) functions
    int getWaterLevel();            // returns water level in screen coordinates
    int getMaxHeight();             // returns max possibile height for terrain in screen coordinates
    boolean isWater(int x);         // returns true, if given position contains water
// get display bounds of this terrain
    int getTopBound();
    int getBottomBound();
    int getLeftBound();
    int getRightBound();
// local terrain functions
    int getMaxHeight(int x, int size);  // returns max height for given part of terrain
    int getMinHeight(int x, int size);  // returns min height for given part of terrain
// collsion detection functions
    byte collide(BoundingBox bb);
    byte collide(Point p);
    byte collide(Point p, int radius);
// terrain modification functions
    void crater(int x, int size, int force);    // creates crater with given size and given force (depth)
    void damage(int x, int size, int force);    // damages terrain, but does not change its height
    boolean flatten(int x, int size);           // flattens given terrain
    void setWaterLevel(short level);            // sets water level (negative value)
// drawing code
    void drawTerrain(Graphics g, int sx, int sy, int width, int height);    // draws terrain
}
