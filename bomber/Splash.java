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
 *  File:       Splash.java
 *  Content:    Splash object (for water and machine gun)
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class Splash
//      Desc:   Handle Splashes
//
// ==========================================================================;

public class Splash implements Visual
{
    protected Point[]             m_splash;                  // splash 
    protected byte[]              m_splash_size;             // size of the splash particle (0 for empty)
    protected int[]               m_splash_color;            // color of the splash
    protected int[]               m_splash_velocity_x;        
    protected int[]               m_splash_velocity_y;        
    protected byte                m_splash_initial_size;     // inital size for smoke
    protected short               m_size;                    // number of alive particles
    protected int                m_gravity;
    protected int                m_start_y;
    protected byte                m_start_speed_x;           // range of starting x speeds
    protected byte                m_start_speed_y;           // range of starting y speeds 
    protected int                m_speed_x_add;             // speed in x direction added to start_x_speed (for wind, original velocity, etc)
    protected int[]               m_color_table;
        
    
    public Splash(int x, int y, byte particles, byte initial_size, int gravity, byte speed_x, byte speed_y, byte speed_x_add, int[] color_table) 
    {
        m_start_speed_x = speed_x;
        m_start_speed_y = speed_y;
        m_speed_x_add = Common.toFP(speed_x_add);
        
        m_color_table = color_table;
                
        m_splash = new Point[particles];
        m_splash_size = new byte[particles];
        m_splash_color = new int[particles];
        m_splash_velocity_x = new int[particles];
        m_splash_velocity_y = new int[particles];

        m_start_y = y;
        m_size = particles;
        m_splash_initial_size = initial_size;
        m_gravity = gravity;

        for (int i = 0; i < m_splash.length; i++) 
        {
            m_splash[i] = new Point(x, y);
            newParticle(i);
        }
    }
    
    
    public void draw(Graphics g, int x, int y) 
    {

        m_size = 0;
        for (int i = 0; i < m_splash.length; i++)
        {

            byte splash_size = m_splash_size[i];
            if (splash_size > 0)
            {
                m_size++;
                g.setColor(m_splash_color[i]);
                g.fillArc(Common.toInt(m_splash[i].x - x) - splash_size, Common.toInt(m_splash[i].y - y) - splash_size, splash_size, splash_size, 0, 360);
                //g.fillRect(Common.toInt(m_splash[i].x - x),  Common.toInt(m_splash[i].y - y), splash_size, Common.toInt(m_start_y - m_splash[i].y));
            }
        }
    
    }
    
    protected void newParticle(int index)
    {
            int color  = m_color_table[Math.abs(GameState.random.nextInt()) % m_color_table.length];

            m_splash_velocity_x[index] = (GameState.random.nextInt() % 1024)  * m_start_speed_x + (int)m_speed_x_add;
            m_splash_velocity_y[index] = -(Math.abs(GameState.random.nextInt()) % 1024) * m_start_speed_y;
            
            m_splash_size[index] = m_splash_initial_size;
            m_splash_color[index] = color;
    }
    
    public int handle(int delta) 
    {
        for (int i = 0; i < m_splash.length; i++)
        {
            if (m_splash_size[i] > 0)
            {
                m_splash[i].x += Common.fastMul(m_splash_velocity_x[i], delta); 
                m_splash[i].y += Common.fastMul(m_splash_velocity_y[i], delta); 
                m_splash_velocity_y[i] += Common.fastMul(m_gravity, delta);
                if (m_splash[i].y > m_start_y)
                {
                    m_splash_size[i] = 0;
                }
            }
        }
        return m_size == 0 ? DELETE : NONE;
    }



}
