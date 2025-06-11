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
 *  File:       Smoke.java
 *  Content:    Smoke object
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	class Smoke
//      Desc:   Handle smoke
//
// ==========================================================================;

public class Smoke implements Visual
{
    protected Point[]             m_trail;              // trail (or smoke) for object
    protected int[]              m_trail_size;         // size of trail (0 for no trail yet)
    protected int[]               m_trail_meta;        // color of the trail
    protected byte[]              m_trail_type;         // type of the trail particle
    protected byte                m_trail_length;       // length of trail (how many objects)

    protected int                m_smoke_vx;           // movement of smoke
    protected int                m_smoke_vy;           // movement of smoke
    protected int                 m_decay_rate;         // decay rate for smoke particles
    protected boolean             m_stopping;           // is smoke stoping to smoke
    
    protected int                m_water_level;        // water level (for bubbles)
    
    public static final byte    FULL = 0;
    public static final byte    BUBBLE = 1;

    
    
    public Smoke(byte trail_length,  int decay_rate, int water_level)                 
    {
        m_trail = new Point[trail_length];
        m_trail_size = new int[trail_length];
        m_trail_meta = new int[trail_length];
        m_trail_type = new byte[trail_length];
        
        for (int i = 0; i < m_trail.length; i++) 
        {
            m_trail[i] = new Point();
            m_trail_meta[i] = 0;
        }
        
        m_decay_rate = decay_rate;
        m_stopping = false;
        m_water_level = water_level;


    }
    
    
    public void draw(Graphics g, int x, int y) 
    {
        
        for (int i = 0; i < m_trail.length; i++)
        {
            byte trail_size = Common.toByte(m_trail_size[i]);
            if (trail_size > 0)
            {
                
                if (m_trail_type[i] == FULL)
                {
                    g.setColor(m_trail_meta[i]);
                    g.fillArc(Common.toInt(m_trail[i].x - x) - trail_size, Common.toInt(m_trail[i].y - y) - trail_size, trail_size, trail_size, 0, 360);
                }
                else if (m_trail_type[i] == BUBBLE)
                {
                    g.setColor(0xFFFFFF);
                    g.drawArc(Common.toInt(m_trail[i].x - x + trail_size/2) - trail_size, Common.toInt(m_trail[i].y - y + trail_size/2) - trail_size, trail_size, trail_size, 0, 360);
                }
            }
        }
    
    }
    
    protected void newTrail(int index, int x, int y, int color, byte type, int size)
    {
            m_trail[index].set(x, y);
            m_trail_size[index] = size;
            if (type == FULL)   m_trail_meta[index] = color;
            else m_trail_meta[index] = 0;
            m_trail_type[index] = type;
    }
    
    
    public void addParticle(int x, int y, int color, byte type, int size)
    {
            for (int i = 0; i < m_trail.length - 1; i++)
            {
                if (m_trail_size[i] <= 0)
                {
                    newTrail(i, x, y, color, type, size);
                    return;
                }
            }
    }
    
    
    public int handle(int delta) 
    {
        int alive = 0;
        for (int i = 0; i < m_trail.length - 1; i++)
        {
            if (m_trail_size[i] > 0)
            {
                alive++;
                if (m_trail_type[i] == BUBBLE)
                {
                    m_trail[i].x += Common.fastMul(m_smoke_vx, delta) + Common.cos(m_trail_meta[i] * 360 / 5) / 4;
                    //m_trail[i].y += Common.mul(m_smoke_vy, delta);
                    m_trail[i].y -= Common.fastMul(m_trail_meta[i], delta);
                    
                    
                    if (m_trail[i].y < m_water_level)
                    {
                        //m_trail[i].y = m_water_level;
                        //m_trail_meta[i] -= 10 * delta;
                        //m_trail_size[i] -= delta;
                        m_trail_size[i] = 0;

                    }
                    else m_trail_meta[i] += 5 * delta;
                    
                }
                else
                {
                    m_trail_size[i] -= m_decay_rate * delta;
                    m_trail[i].x += Common.fastMul(m_smoke_vx, delta);
                    m_trail[i].y += Common.fastMul(m_smoke_vy, delta);

                }
            }
        }
        if (m_stopping && alive == 0) return DELETE;
        return NONE;
    }

    void setSpeed(int vx, int vy)
    {
        m_smoke_vx = vx;
        m_smoke_vy = vy;
    }

    void stop()
    {
        m_stopping = true;
    }
    
}
