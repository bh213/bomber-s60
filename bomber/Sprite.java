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
 *  File:       NokiaSprite.java
 *  Content:    Sprite (raster image) class with support for rotation and 
 *              flipping
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/


package bomber;

import javax.microedition.lcdui.*;
import java.io.IOException;
import java.io.DataInputStream;
import com.nokia.mid.ui.*;                      // Nokia dependant code
import com.nokia.mid.ui.DirectGraphics.*;       // Nokia dependant code


// =========================================================================;
//	Name:	NokiaSprite 
//	Desc:	Class for drawing raster images
//              WARNING: Use only one of the Sprite classes. So, if you
//              are using this class, you should not use Sprite class
//              This class DOES guarantee transparent PNG support.
// ==========================================================================;

public class Sprite implements Drawable
{
    
    protected Image[]     m_images;
    protected byte[]      m_center;
    protected boolean     m_enable_flipping;
    protected byte[]      m_user_data;

    

    
    public Sprite(DataInputStream dis, boolean flipping) throws IOException
    {
        int image_num = dis.readByte();   
        if (image_num == 0) // special mode
        {
            int user_data_size = dis.readByte();
            m_user_data = new byte[user_data_size];
            dis.read(m_user_data);
            image_num = dis.readByte();   
        }
        
        
        boolean condensed = false;
        if (image_num < 0)
        {
            condensed = true;
            image_num = -image_num;
        }
            
        m_images = new Image[image_num];
        m_center = new byte[2 * image_num];
        m_enable_flipping = flipping;
        
        if (condensed)  // combined image
        {
            byte [] tmp_x = new byte[image_num];
            byte [] tmp_y = new byte[image_num];
            for (int i = 0; i < image_num; i++)
            {
                tmp_x[i] = dis.readByte();
                tmp_y[i] = dis.readByte();
                m_center[2 * i]   =  dis.readByte();  // center x
                m_center[2 * i+1] =  dis.readByte();  // center y
            }

            int len = dis.readInt();        // image length
            byte[] data = new byte[len];    // load to buffer
            dis.read(data, 0, len);         // read buffer
            Image tmp_image = Image.createImage(data, 0, data.length);  // creates temporary image
            int c_width = 0;       // current width of the image
            for (int i = 0; i < image_num; i++)
            {
                m_images[i] = DirectUtils.createImage(tmp_x[i], tmp_y[i], 0x00000000);
                m_images[i].getGraphics().drawImage(tmp_image, -c_width, 0, 0);
                c_width += tmp_x[i];
            }
        }
        else
        {
            for (int i = 0; i < image_num; i++)
            {
                m_center[2 * i]     =  dis.readByte();    // center x
                m_center[2 * i + 1] =  dis.readByte();    // center y
                int len = dis.readInt();
                byte[] data = new byte[len];
                dis.read(data, 0, len);
                m_images[i] = Image.createImage(data, 0, data.length);
            }
        }
    }

    
    protected int getDimensionWidth(int index, boolean flip)
    {
        if (m_enable_flipping && index >= m_images.length)
        {
            if (index < 2 * m_images.length) 
            {
                flip = !flip;
                index -= m_images.length;
            }
            else if (index < 3 * m_images.length) 
            {
                index -= 2 * m_images.length;
            }
            else 
            {
                flip = !flip;
                index -= 3 * m_images.length;
            }
        }
        if (flip) return m_images[index].getHeight();
        else return m_images[index].getWidth();
    }
    

    

     public int getWidth(int index)
    {
        return getDimensionWidth(index, false);
    }
        
    public int getHeight(int index)
    {
        return getDimensionWidth(index, true);
    }
    
      
    
    public int getCenterX(int index)
    {
        int center_x = m_center[2 * index];
        int center_y = m_center[2 * index + 1];
        if (m_enable_flipping)
        {
            if (index <  m_images.length) return center_x;
            if (index < 2 * m_images.length) return getWidth(index) - center_y;
            else if (index < 3 * m_images.length) return getWidth(index) - center_x;
            else return center_y;
        }
        else return center_x;
    }

    public int getCenterY(int index)
    {
        int center_x = m_center[2 * index];
        int center_y = m_center[2 * index + 1];
        
        if (m_enable_flipping)
        {
            if (index <  m_images.length) return center_y;
            if (index < 2 * m_images.length) return center_x;
            else if (index < 3 * m_images.length) return getWidth(index) - center_y;
            else return getWidth(index) - center_x;
        }
        else return center_y;
    }

    public byte getUserData(int index)
    {
        return m_user_data[index];
    }
    
    public int getNumber()
    {
        if (m_enable_flipping) return m_images.length * 4;
        else return m_images.length;
    }

    public int getIndexFromAngle(int angle)
    {
        return (int)((getNumber() * angle + (Common.FIXED * 360) / (getNumber() * 2)) / (360 * Common.FIXED)) % getNumber();
    }
    
    
    public void drawImageWithAngle(Graphics g, int angle, int x, int y)
    {
        
        drawImage(g, getIndexFromAngle(angle) , x, y);
    }
    
    public void drawImage(Graphics g, int index, int x, int y)
    {
       
        if (m_enable_flipping && index >= m_images.length)
        {
            DirectGraphics dg = DirectUtils.getDirectGraphics(g); 
            int manipulation;
            if (index < 2 * m_images.length) 
            {
                manipulation = DirectGraphics.ROTATE_270;
                index -= m_images.length;
                
                //cy = m_images[index].getHeight() - cy;
                dg.drawImage(m_images[index], x - (m_images[index].getHeight() - m_center[index * 2 + 1]) , y - m_center[index * 2], 0, manipulation);
            }
            else if (index < 3 * m_images.length) 
            {
                //cx = m_images[index].getWidth() - cx;
                manipulation = DirectGraphics.ROTATE_180;
                index -= 2 * m_images.length;
                dg.drawImage(m_images[index], x - (m_images[index].getWidth() - m_center[index * 2]) , y - (m_images[index].getHeight() - m_center[index * 2 + 1]), 0, manipulation);
            }
            else 
            {
                manipulation = DirectGraphics.ROTATE_90;
                index -= 3 * m_images.length;
                dg.drawImage(m_images[index], x - m_center[index * 2 + 1] , y - (m_images[index].getWidth() - m_center[index * 2]), 0, manipulation);
            }
        }
        else 
        {
            g.drawImage(m_images[index], x - m_center[index * 2] , y - m_center[index * 2 + 1], 0);
        }
    }
}
