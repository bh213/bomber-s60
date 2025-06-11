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
 *  File:       Explosion.java
 *  Content:    Explosion object
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;


// =========================================================================;
//	Name:	class Explosion
//      Desc:   Handle explosion
//
// ==========================================================================;

public class Explosion implements Visual
{
    protected Point[]            m_explosion;                  // explosion (or smoke) for object
    protected int[]              m_explosion_size;             // size of the explosion particle (0 for empty)
    protected int[]              m_explosion_color;            // color of the explosion
    protected short[]            m_explosion_direction;        // directon if which particle is flying
    protected byte[]             m_explosion_speed;            // speed with which each particle is flying
    protected int                m_explosion_initial_size;     // inital size for smoke
    protected int                m_decay;                      // how fast does explosion decay
    protected int                m_x, m_y;                     // center of explosion
    protected short              m_size;                       // number of alive particles
    protected int[]              m_color_table;                // color fo the current particle
    protected int                m_direction;                  // direction, in which explosion is going
    protected int                m_direction_dispersion;       // dispersion of direction
    protected byte               m_speed;                      // speed of explosion particles
    protected byte               m_speed_dispersion;           // dispersion of speed of explosion particles
    
    protected static final byte PARTICLE_SPEED = 30;
    
    public Explosion(int [] color_table, int x, int y, byte particles, int explosion_initial_size, int decay, int direction, int direction_dispersion, byte speed, byte speed_dispersion)
    {
        m_direction_dispersion = direction_dispersion;
        m_direction = direction;
        m_speed = speed;
        m_speed_dispersion = speed_dispersion;
        init(color_table, x, y, particles, explosion_initial_size, decay);
    }
    
    public Explosion(int [] color_table, int x, int y, byte particles, int explosion_initial_size, int decay) 
    {
        m_direction_dispersion = -1;        // no direction explosion
        m_speed_dispersion = -1;
        init(color_table, x, y, particles, explosion_initial_size, decay);
    }
    
    public void init(int [] color_table, int x, int y, byte particles, int explosion_initial_size, int decay) 
    {
        m_explosion = new Point[particles];
        m_explosion_size = new int[particles];
        m_explosion_color = new int[particles];
        m_explosion_direction = new short[particles];
        m_explosion_speed = new byte[particles];
        m_size = particles;
        m_explosion_initial_size = explosion_initial_size;
        m_decay = decay;
        m_x = x;
        m_y = y;
        m_color_table = color_table;
        for (int i = 0; i < m_explosion.length; i++) 
        {
            m_explosion[i] = new Point();
            newParticle(i);
        }
    }
    
    
    
    public void draw(Graphics g, int x, int y) 
    {
        m_size = 0;
        for (int i = 0; i < m_explosion.length; i++)
        {
            byte explosion_size = Common.toByte(m_explosion_size[i]);
            if (explosion_size > 0)
            {
                m_size++;
                g.setColor(m_explosion_color[i]);
                g.fillArc(Common.toInt(m_explosion[i].x - x) - explosion_size, Common.toInt(m_explosion[i].y - y) - explosion_size, explosion_size, explosion_size, 0, 360);
            }
        }
    
    }
    
    protected void newParticle(int index)
    {
            int color  = m_color_table[Math.abs(GameState.random.nextInt()) % m_color_table.length];
        
            if (m_direction_dispersion < 0)
            {
                m_explosion_direction[index] = (short)(GameState.random.nextInt() % 360);
            }
            else
            {
                int dir = m_direction + (GameState.random.nextInt() % m_direction_dispersion);
                dir = (Common.toInt(dir) + 360) % 360;
                m_explosion_direction[index] = (short)dir;
            }
            m_explosion[index].set(m_x, m_y);
            m_explosion_size[index] = m_explosion_initial_size;
            m_explosion_color[index] = color;
            
            if (m_speed_dispersion < 0)
            {
                m_explosion_speed[index] = (byte)(GameState.random.nextInt() % PARTICLE_SPEED);
            }
            else
            {
                int spd = m_speed + (GameState.random.nextInt() % m_speed_dispersion);
                m_explosion_speed[index] = (byte)spd;
            }

            

    }
    
    public int handle(int delta) 
    {
        int decay = Common.fastMul(m_decay, delta);
        for (int i = 0; i < m_explosion.length; i++)
        {
            m_explosion_size[i] -= decay;
            m_explosion[i].x += Common.fastMul(m_explosion_speed[i] * Common.cos(m_explosion_direction[i] * Common.FIXED), delta); 
            m_explosion[i].y += Common.fastMul(m_explosion_speed[i] * Common.sin(m_explosion_direction[i] * Common.FIXED), delta); 
        }
        return m_size == 0 ? DELETE : NONE;
    }



}
