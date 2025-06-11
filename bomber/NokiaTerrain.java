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
 *  File:       NokiaTerrain.java
 *  Content:    2D side scrolling terrain drawing code optimized for Nokia
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import com.nokia.mid.ui.*;
import com.nokia.mid.ui.DirectGraphics.*;       // Nokia dependant code

// =========================================================================;
//	Name:	class NokiaTerrain
//	Desc:	Class for handling and drawing 2D terrain
//
// ==========================================================================;


public class NokiaTerrain implements Terrain
{
     private static final byte WATER_LEVEL = -10;
     GameState                m_game_state;
     
     protected byte[]         m_data;               // 1D height data
     protected byte[]         m_meta_data;          // type of terrain, flags, etc
     
     protected short          m_water_level;        // water level (can be changed)
     
     protected short[]        m_pixels;             // stores pixels for temporary drawing
     protected Image          m_sky_image;
     protected Image[]        m_images;             // collection of images used to draw terrain
     protected boolean[]      m_dirty_image;        // true, if image is dirty and should be redrawn
     protected short          m_image_width;        // width of one of the images used to draw terrain
     protected short          m_image_height;       // height of the image used to draw terrain
     
     protected short          m_canvas_width;        // width of the canvas on which terrain is being drawn
     protected short          m_canvas_height;       // height the canvas
     
     protected short          m_bottom;             // coordinate of the bottom of the image
     protected short          m_images_num;         // number of all images that can be allocated
     protected byte           m_allocated_images;   // number of allocated images (must be <= m_images_num)
     protected short[]        m_image_index;        
     protected Point          m_temp_point;         // used as temp storage
     protected boolean        m_is_background;      // true, if we use paralax scrolling
     
     protected short[]        m_sky_color;          // color of the sky at specific altitude
     protected ResourceManager
                              m_resource_manager;
     
     
    protected final static short[] c_terrain_table =    
    {
           (short)0xF000,
           (short)0xF010,
           (short)0xF020,
           (short)0xF030,
           (short)0xF040,           
           (short)0xF050,
           (short)0xF060,
           (short)0xF070,
           (short)0xF080,
           (short)0xF090,           
           (short)0xF0A0,           
           (short)0xF0B0,
           (short)0xF0C0,
           (short)0xF0D0,
           (short)0xF0E0,
           (short)0xF0F0
    };
     
    protected final static short[] c_grass_table =    
    {
           (short)0xF183,
           (short)0xF182,
           (short)0xF281,
           (short)0xF191,
           (short)0xF181,
           (short)0xF181,
           (short)0xF191,
           (short)0xF181,
           (short)0xF182,
           (short)0xF181,
           (short)0xF1A1, 

    };
    
    

     
     
// =========================================================================;
//	Name:	Terrain(short size, short image_width, 
//                      short image_height, short width, short height)
//	Desc:	Constructs terrain and prepares it for rendering
//
// ==========================================================================;

     public NokiaTerrain(GameState game_state, byte[] data, short image_height, short width, short height, ResourceManager resource_manager, boolean is_background)
    {
        m_game_state = game_state;
        m_resource_manager = resource_manager;
        m_is_background = is_background;
        m_data = data;
        m_meta_data = new byte[data.length];
        
        m_water_level = WATER_LEVEL;
        m_temp_point = new Point();
        
        m_image_width = ResourceManager.TERRAIN_IMAGE_WIDTH;
        m_image_height = image_height;
        
        m_canvas_width = width;
        m_canvas_height = height; 
        m_pixels = new short[m_image_width * m_image_height];
        m_bottom = image_height;
        m_images_num = (short)((5 * width / (4 * m_image_width)) + 1);
        m_images = new Image[(data.length / m_image_width) + 1];
        m_dirty_image = new boolean[(data.length / m_image_width) + 1];
        

        m_image_index = new short[m_images_num];
        for (int i = 0; i < m_images_num; i++) m_image_index[i] = -1;
        m_sky_color = new short[m_image_height];

        
        m_sky_image = m_resource_manager.getTerrainImage();
        drawSky(m_sky_image.getGraphics());
    }

     
    public int getWaterLevel()
    {
        return m_water_level;
    }
    
    public int getMaxHeight()
    {
        return -m_image_height;
    }
    
    
// =========================================================================;
//	Name:	boolean collide(BoundingBox bb)
//	Desc:	check collision between BoundingBox and terrain
//
// ==========================================================================;
    
public byte collide(BoundingBox bb)
{
    int c= NO_COLLIDE;    // This is used, because NO_COLLIDE, WATER and GROUND are ordered.
    c = Math.max(c, collide(bb.getBound(0)));
    c = Math.max(c, collide(bb.getBound(1)));
    c = Math.max(c, collide(bb.getBound(2)));
    c = Math.max(c, collide(bb.getBound(3)));
    return (byte)c;
}

// =========================================================================;
//	Name:	boolean collide(Point p)
//	Desc:	check collision between Point and terrain
//
// ==========================================================================;

public byte collide(Point p)
{
    int ix = Common.toInt(p.x);
    int iy = Common.toInt(p.y);
    if (ix < 0 || ix >= m_data.length) 
    {
        if (iy  >= m_water_level) return WATER;
        else return NO_COLLIDE;
    }
    else if (iy >= -m_data[ix]) return GROUND;
    else if (iy  >= m_water_level) return WATER;
    else return NO_COLLIDE;
}

// =========================================================================;
//	Name:	boolean collide(Point p, int radius)
//	Desc:	check collision between circle and terrain
//
// ==========================================================================;

public byte collide(Point p, int radius)
{
    int ix = Common.toInt(p.x);
    
    if (ix < Common.toFP(radius) || ix >= m_data.length + Common.toFP(radius)) 
    {
        if (Common.toFP(p.y - radius)  <= m_water_level) return WATER;
        else return NO_COLLIDE;
    }
    else 
    {
        for (int i = p.y - radius; i < p.y + radius; i+= Common.FIXED)
        {
            if (p.distance(i, Common.toFP(-m_data[Common.toInt(i)])) <= radius) return GROUND;
        }
    }
    
    if (Common.toFP(p.y - radius)  <= m_water_level) return WATER;

    return NO_COLLIDE;
}

        
protected void calculateSkyColor(Graphics g)
{
    DirectGraphics dg = DirectUtils.getDirectGraphics(g); 
    
    for (int i = 0; i < m_sky_color.length + m_water_level; i++)
    {
        if (m_is_background)
        {
            int b =192*15/255;
            m_sky_color[i] = (short)((b << 8) + (b << 4) + b + 0xF000);
        }
        else 
        {
            m_sky_color[i] = (short)0xF0BE;
        }
    }
    for (int i = m_sky_color.length + m_water_level; i < m_sky_color.length; i++)
    {
        m_sky_color[i] = (short)0xF08F;
    }
}

// =========================================================================;
//	Name:	void drawSky(Graphics g, int y)
//	Desc:	Draws background sky
//
// ==========================================================================;
    
    private void drawSky(Graphics g)
    {
        calculateSkyColor(g);
        DirectGraphics dg = DirectUtils.getDirectGraphics(g); 
        for (int i = 0; i < m_image_width; i++)
        {
            dg.drawPixels(m_sky_color, false, 0, 1, i, 0, 1, m_image_height, 0, DirectGraphics.TYPE_USHORT_4444_ARGB);
        }
        
        
        g.setColor(0, 128, 255);
        g.fillRect(g.getClipX(), g.getClipHeight() + m_water_level, g.getClipWidth(), g.getClipHeight());
    }


// =========================================================================;
//	Name:	void drawTerrainImage(Graphics g, int sx, int sy, 
//                                    int width, int height)
//	Desc:	Draws ground (mostly rendered into image buffer)
//
// ==========================================================================;

    private void drawTerrainImage(Graphics g, int sx)
    {
          int current_ground;
          int index;
	  for (int x = 0; x < m_image_width; x++)
          {
                if (sx + x >= m_data.length)
                {
                    index = x;
                    for (int y = 0; y < m_image_height + m_water_level; y++)
                    {
                        m_pixels[index] = m_sky_color[y];
                        index += m_image_width;
                    }
                    for (int y = 0; y < - m_water_level; y++)
                    {
                        m_pixels[index] = (short)0xF08F;
                        index += m_image_width;
                    }
                    
                    continue;

                }
                else 
                {
                    current_ground = m_image_height - (m_data[sx + x] );
                }
                
                index = x;
                for (int y = 0; y < current_ground; y++)
                {
                    m_pixels[index] = m_sky_color[y];
                    index += m_image_width;
                }
                
                if (-m_data[sx + x] > m_water_level)   // Draw water level
                {
                            for (int i =  m_image_height + m_water_level; i < Math.min(current_ground, m_image_height); i++) 
                            {
                                m_pixels[x + m_image_width * i] = (short)0xF08F;
                            }
                }
                
                int c_type = Math.min(m_meta_data[sx + x], m_data[sx + x] - 2) + 2;
                if (c_type > 15) c_type = 15;
                
                index = x + m_image_width * current_ground;
                for (int i = 0; i < c_type; i++)
                {
                    //m_pixels[x + m_image_width * (current_ground + i)] = c_terrain_table[i / 2];
                    m_pixels[index] = c_terrain_table[i / 2];
                    index += m_image_width;
                }
                
                index = x + m_image_width * (current_ground + c_type);
                
                for (int i = current_ground + c_type; i < m_image_height; i++)
                {
                    //m_pixels[x + m_image_width * i] = c_grass_table[(x * i) % c_grass_table.length];
                    m_pixels[index] = c_grass_table[(x * i) % c_grass_table.length];
                    index += m_image_width;
                }
          }
          DirectGraphics dg = DirectUtils.getDirectGraphics(g); 
          dg.drawPixels(m_pixels, false, 0, m_image_width, 0, 0, m_image_width, m_image_height, 0, DirectGraphics.TYPE_USHORT_4444_ARGB);
    }
    
// =========================================================================;
//	Name:	void createImage(Graphics g, short index, 
//                               short start_index, short end_index)
//	Desc:	Creates image that contains background (sky + terrain). This
//              image is then blited to main screen. This is used to greatly
//              speed up rendering.
//
// ==========================================================================;

    private void createImage(Graphics g, short index, short start_index, short end_index)
    {
        if (m_allocated_images < m_images_num)    // no need to find unused image
        {
            //m_images[index] = Image.createImage((int)m_image_width, (int)m_image_height); 
            m_images[index] = m_resource_manager.getTerrainImage();
            m_image_index[m_allocated_images] = index;
            m_allocated_images++;
        }
        else    // find image that is being unused
        {
            for (int i = 0; i < m_image_index.length; i++)
            {
               if (m_image_index[i] < start_index || m_image_index[i] > end_index) 
               {
                   m_images[index] = m_images[m_image_index[i]];
                   m_images[m_image_index[i]] = null;
                   m_image_index[i] = index;
                   break;
               }
            }
            
        }
        recreateImage(g, index);    
    }
    
    
    public void recreateImage(Graphics g, short index)
    {
        m_dirty_image[index] = false;
        drawTerrainImage(m_images[index].getGraphics(), index * m_image_width);
    }
        
   
    
// =========================================================================;
//	Name:	public void crater(int x, int size, int force)
//	Desc:	Creates dent in terrain. It's center is at x, it has
//              radius of 2*size and creates inverted sinus hole with
//              amplitude force    
//
// ==========================================================================;
    public void crater(int x, int size, int force)
    {
        // WARN ME: Optimize this code
        int start = x - size;
        int end = x + size;
        if (start < 0) start = 0;
        if (end >= m_meta_data.length) end = m_meta_data.length - 1;
        
        for (int i = start; i < end; i++)
        {
            byte frc = Common.toByte(force * Common.sin((i - (x - size)) * Common.FIXED * 180 / (size * 2)));
            m_meta_data[i] = (byte) Math.min(m_meta_data[i] + frc/2 + 3, 20);
            m_data[i] = (byte) Math.max(0, m_data[i] - frc);
            m_dirty_image[i / m_image_width] = true;
        }
    }
      
// =========================================================================;
//	Name:	public void damage(int x, int size, int force)
//	Desc:	Damages the terrain. It's center is at x, it has
//              radius of 2*size and creates inverted sinus hole with
//              amplitude force    
//
// ==========================================================================;  
    
    public void damage(int x, int size, int force)
    {
        // WARN ME: Optimize this code
        int start = x - size;
        int end = x + size;
        if (start < 0) start = 0;
        if (end >= m_meta_data.length) end = m_meta_data.length - 1;
        
        for (int i = start; i < end; i++)
        {
            byte frc = Common.toByte((force + Math.abs(GameState.random.nextInt() % 6)) * Common.sin((i - (x - size)) * Common.FIXED * 180 / (size * 2)));
            m_meta_data[i] = (byte) Math.min(m_meta_data[i] + frc/2 + 3, 20);
            m_dirty_image[i / m_image_width] = true;
        }
    }
 
    
    
    public boolean isWater(int x)
    {
        if (x < 0) 
        {
            return true;
        }
        else if (x >= m_data.length)
        {
            return true;
        }
        else if (-m_data[x] > m_water_level)
        {
            return true;
        }
        else return false;
    }
    
// =========================================================================;
//	Name:	 public int getMaxHeight(int x, int size)
//	Desc:	returns max height of interval (x, x + size)
//              Result is negative.
//
// ==========================================================================;
    
    public int getMaxHeight(int x, int size)
    {
        // WARN ME: Optimize this code
        int end = x + size;
        if (x < 0) x = 0;
        if (end >= m_meta_data.length) end = m_meta_data.length - 1;
        int max = m_water_level;
        
        for (int i = x; i < end; i++)
        {
            if (-m_data[i] < max) max = -m_data[i];
        }
        return max;
    }

// =========================================================================;
//	Name:	 public int getMinHeight(int x, int size)
//	Desc:	returns min height of interval (x, x + size)
//
// ==========================================================================;  
    
    public int getMinHeight(int x, int size)
    {
        // WARN ME: Optimize this code
        int end = x + size;
        if (x < 0) x = 0;
        if (end >= m_meta_data.length) end = m_meta_data.length - 1;
        int min = -10000;
        
        for (int i = x; i < end; i++)
        {
            if (-m_data[i] > min) min = -m_data[i];
        }
        if (min == -10000) return m_water_level;
        else return min;
    }
    
// =========================================================================;
//	Name:	public boolean flatten(int x, int size)
//	Desc:	returns flattens interval to lowest height in interval
//     (x, x + size). Returns true, if nothing was changed and false if 
//      ground was flattened.    
//
// ==========================================================================;   
    public boolean flatten(int x, int size)
    {
        boolean flat = true;
        int min = getMinHeight(x, size);
        int end = x + size;
        if (x < 0) x = 0;
        if (end >= m_meta_data.length) end = m_meta_data.length - 1;
        
        
        for (int i = x; i < end; i++)
        {
            if (-m_data[i] < min) 
            {
                m_data[i] = (byte)(-min);
                m_dirty_image[i/ m_image_width] = true;
                flat = false;
            }
        }
        return flat;
    }
    
    
// =========================================================================;
//	Name:	void drawTerrain(Graphics g, int sx, int sy, 
//                               int width, int height)
//	Desc:	Draws terrain and sky to main screen. Uses number of
//              image buffers to speed up the process.    
//
// ==========================================================================;  

    
    public void drawTerrain(Graphics g, int sx, int sy, int width, int height)
    {
          int back_width = m_resource_manager.getTerrainBackground(0).getWidth(0);
          int back_s_x = (-sx/3) % back_width;

          int back_y = - m_image_height - sy - 1 ;

          DirectGraphics dg = DirectUtils.getDirectGraphics(g); 

          g.setColor(0, 176, 224);  // top sky color
          if (m_is_background)
          {
                m_resource_manager.getTerrainBackground(0).drawImage(g, 0, back_s_x, back_y);
                if (back_s_x + back_width < g.getClipWidth()) m_resource_manager.getTerrainBackground(0).drawImage(g, 0, back_s_x + back_width, back_y);
                if (back_s_x - back_width < g.getClipWidth()) m_resource_manager.getTerrainBackground(0).drawImage(g, 0, back_s_x - back_width, back_y);

                g.fillRect(g.getClipX(), 0, g.getClipWidth(), back_y - m_resource_manager.getTerrainBackground(0).getHeight(0) + 1);
          }
          else g.fillRect(g.getClipX(), 0, g.getClipWidth(), back_y + 1);
          
          
          m_resource_manager.getSun().drawImage(g, 0, 40, back_y - m_resource_manager.getTerrainBackground(0).getHeight(0) + 1 - 30);
            //g.fillRect(g.getClipX(), 0, g.getClipWidth(), g.getClipHeight());
          
          //if (true) return;
          
          //sy = 0;//- sy;// + ;
          sy = - m_image_height - sy;
/*          if (sy > 0)
          {
              g.setColor(80, 80, 160);  // top sky color
              g.fillRect(g.getClipX(), 0, g.getClipWidth(), sy);
          }*/

          int index_x   = sx + g.getClipX();
          
          int x = g.getClipX();
          
          
          short start_index = (short)(index_x / m_image_width);    // index of image to draw
          short end_index = (short)((index_x + g.getClipWidth()) / m_image_width + 1);
          if (index_x < 0 && sx % m_image_width != 0)
          {
              start_index--;
          }
          
          for (short index = start_index; index < end_index; index++)
          {
              if (index >= 0 && index < m_images.length) 
              {

                if (m_images[index] == null) createImage(g, index, start_index, end_index);
                else if (m_dirty_image[index]) recreateImage(g, index);
                //dg.drawImage(m_images[index], x - index_x % m_image_width, sy, 0, 0); 
                g.drawImage(m_images[index], x - index_x % m_image_width, sy, 0); 
              }     
              else if (index < 0)
              {
                  int tx = (index_x % m_image_width);       // its 2am and I have no idea why this code works. *sigh*
                  if (index_x % m_image_width != 0) tx+= m_image_width;
                  g.drawImage(m_sky_image, x - tx, sy, 0); 
                  
                  g.setColor(0, 128, 255);
                  g.fillRect(x - tx, m_image_height + m_water_level + sy, m_image_width, -m_water_level);
              }
              else 
              {
                  g.drawImage(m_sky_image, x - index_x % m_image_width, sy, 0); 
                  g.setColor(0, 128, 255);
                  g.fillRect(x - index_x % m_image_width, m_image_height + m_water_level + sy, m_image_width, -m_water_level);

              }

              index_x += m_image_width;
              x += m_image_width;
          }
    }

    public int getBottomBound() 
    {
        return 0;
    }
    
    public int getLeftBound() 
    {
        return 0;
    }
    
    public int getRightBound() 
    {
        return m_data.length;
    }
    
    public int getTopBound() 
    {
        return -(m_image_height - 1);
    }
    
    public void setWaterLevel(short level)
    {
        m_water_level = level;
    }
 
    public void destruct()
    {
        for(int i = 0; i < m_images.length; i++) 
        {
            if (m_images[i] != null) 
            {
                m_resource_manager.releaseTerrainImage(m_images[i]);
            }
            m_images[i] = null;
        }
        m_images = null;
        
        m_game_state = null;
        m_data = null;
        m_meta_data = null;
        m_pixels = null;
        if (m_sky_image != null) m_resource_manager.releaseTerrainImage(m_sky_image);
        m_sky_image = null;
        m_dirty_image = null;
        m_image_index = null;
        m_temp_point = null;
        m_sky_color = null;
        m_resource_manager = null;
    }
    
    
}
 