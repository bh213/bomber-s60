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
 *  File:       Drawable.java
 *  Content:    Drawable interface (for Drawing and Sprite classes)
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	Drawable interface
//	Desc:	Interface for drawing vector and raster images

// ==========================================================================;

public interface Drawable 
{
    public int getNumber();
    public void drawImageWithAngle(Graphics g, int angle, int x, int y);
    public void drawImage(Graphics g, int index, int x, int y);
    public int getWidth(int index);
    public int getHeight(int index);
    public int getCenterX(int index);
    public int getCenterY(int index);
    public int getIndexFromAngle(int angle);
    public byte getUserData(int index);
};