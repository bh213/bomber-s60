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
 *  File:       ResourceManager.java
 *  Content:    ResourceManager object
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import com.nokia.mid.sound.*;     // nokia dependant
// =========================================================================;
//	Name:	class ResourceManager
//      Desc:   class that loads and handles all resources
//
// ==========================================================================;

public class ResourceManager
{
    private Drawable[]          m_resources;
    private BitmapFont          m_bitmap_font;
    private Storage             m_storage;
    private Stack               m_terrain_images;
    private Sound[]             m_sounds;
    private BomberMIDlet        m_midlet;
  
    public static final short CANVAS_WIDTH  = 176;
    public static final short CANVAS_HEIGHT = 208;
    
    public static final short TERRAIN_IMAGE_WIDTH = 30;
    public static final short TERRAIN_IMAGE_HEIGHT = 128;
    
    
    public static final byte SOUND_CLICK              = 0;
    public static final byte SOUND_OK                 = 1;
    public static final byte SOUND_FAIL               = 2;
    public static final byte SOUND_BOMB               = 3;
    public static final byte SOUND_BULLET_FIRE        = 4;
    public static final byte SOUND_BOMB_DROP          = 5;
    public static final byte SOUND_BUILDING_EXPLODE   = 6;
    public static final byte SOUND_WATER_SPLASH       = 7;
    public static final byte SOUND_EXPLODE            = 8;

    
    public static final byte FONT_RED       = 0;
    public static final byte FONT_GREEN     = 1;
    public static final byte FONT_BLUE      = 2;
    public static final byte FONT_YELLOW    = 3;
    
    public static final int c_warm_colors[] = 
    {
        0xA31D82,
        0xFF0000,
        0xF9822B,
        0xFAA500,
        0xFFC800,
        0xFFFF00
    };
    
    
    
    public ResourceManager(BomberMIDlet midlet)
    {
        m_terrain_images = new Stack();
        m_midlet = midlet;
    }
    public Drawable loadTitleImage() throws IOException
    {
           DataInputStream dis = new DataInputStream(this.getClass().getResourceAsStream("bin/init.b"));
           Drawable ret_val = new Sprite(dis,false); 
           dis.close();
           return ret_val;
    }
    
    private Sound loadSound(DataInputStream dis) throws IOException
    {
            int len = dis.readInt();        // sound length
            byte[] data = new byte[len];    // load to buffer
            dis.read(data, 0, len);         // read buffer
            Sound ret_val = new Sound(data, Sound.FORMAT_WAV);
            ret_val.init(data, Sound.FORMAT_WAV);
            return ret_val;
    }
    
    private void loadSounds() throws Exception
    {
        DataInputStream dis = new DataInputStream(this.getClass().getResourceAsStream("bin/sound.b"));
        m_sounds = new Sound[3];
        m_sounds[0] = loadSound(dis);   // explosion big
        m_sounds[1] = loadSound(dis);   // explosion
        m_sounds[2] = loadSound(dis);   // bomb drop
        dis.close();
    }
    
    public synchronized void sound(byte type)
    {
        if (m_storage.getSoundEnabled() == false) return;
        int index = -1;
        if (type == SOUND_BUILDING_EXPLODE)
        {
            index = 0;
        }
        else if (type == SOUND_BOMB)
        {
            index = 1;
        }
        else if (type == SOUND_BOMB_DROP)
        {
            index = 2;
        }
        
        if (index != -1)
        {
            /*for (int i = 0; i < 3; i++) 
            {
                if (m_sounds[i].getState() == SOUND_PLAYING) m_sounds[i].stop();
            }*/
            if (m_sounds[index].getState() != Sound.SOUND_PLAYING) m_sounds[index].play(1);
        }
    }
    
    
    public void load() throws Exception
    {
        loadSounds();
        m_storage = new Storage();
        DataInputStream dis = new DataInputStream(this.getClass().getResourceAsStream("bin/b.b"));
        m_resources = new Drawable[27];
        m_resources[0] = new Sprite(dis, true); // plane sprite
        m_resources[1] = new Sprite(dis, true); // water plane sprite
        m_resources[2] = new Sprite(dis, true); // plane sprite 2
        m_resources[3] = new Sprite(dis, true); // water plane sprite 2

        
        
        m_resources[4] = new Sprite(dis, false); // debris
        //m_resources[1] = new Sprite(dis, true); // enemy plane sprite
        m_resources[5] = new Sprite(dis, false); // cloud sprite
        m_resources[6] = new Sprite(dis, true); // bomb sprite
        m_resources[7] = new Sprite(dis, true); // water bomb sprite
        m_resources[8] = new Sprite(dis, false); // barn
        m_resources[9] = new Sprite(dis, false); // church
        m_resources[10] = new Sprite(dis, false); // house
        m_resources[11] = new Sprite(dis, false); // factory
        m_resources[12] = new Sprite(dis, false); // hangar
        
        m_resources[13] = new Sprite(dis, false); // terrain back
        m_resources[14] = new Sprite(dis, false); // score board
        m_resources[15] = new Sprite(dis, false); // score board numbers
        
        m_resources[16] = new Sprite(dis, false); // tank
        
        
        //m_bitmap_font = new BitmapFont[2];
        //loadBitmapFont(dis, 0);
        loadBitmapFont(dis);
        m_resources[17] = new Sprite(dis, false); // tree 1
        m_resources[18] = new Sprite(dis, false); // tree 2
        m_resources[19] = new Sprite(dis, false); // tree 2
        m_resources[20] = new Sprite(dis, false); // sun
        m_resources[21] = new Sprite(dis, false); // icons
        m_resources[22] = new Sprite(dis, false); // wood tile
        m_resources[23] = new Sprite(dis, true);  // indicator
        m_resources[24] = new Sprite(dis, false); // bomber title
        m_resources[25] = new Sprite(dis, false); // zeppelin
        m_resources[26] = new Sprite(dis, false); // sub
        dis.close();

        Helper.font = getFont();
        Helper.resource_manager = this;
    }
    
    private void loadBitmapFont(DataInputStream dis) throws IOException
    {
        
            short c_start = dis.readShort();
            short c_end = dis.readShort();
            byte sub_fonts = dis.readByte();
            byte sub_fonts_height = dis.readByte();
            short[] start_pos = new short[c_end - c_start + 2];   // +1 for len calculation and +1 for guard
            for (int i = 0; i < start_pos.length; i++)
            {
                start_pos[i] = dis.readShort();
            }
            
                        
            int len = dis.readInt();        // image length
            byte[] data = new byte[len];    // load to buffer
            dis.read(data, 0, len);         // read buffer
            Image bitmap_image = Image.createImage(data, 0, data.length);  // creates bitmap font image
            //start_pos[start_pos.length - 2] = (short)(bitmap_image.getWidth() - 1);
            //
            m_bitmap_font= new BitmapFont(bitmap_image, c_start, c_end, start_pos, sub_fonts_height, CANVAS_WIDTH, CANVAS_HEIGHT);       // WARN ME: 
    }
    
    
    public Terrain loadTerrain(GameState gs, int index, boolean background) throws IOException
    {
        DataInputStream dis;
        switch (index)
        {
            case 0: dis= new DataInputStream(this.getClass().getResourceAsStream("bin/terrain.0")); break;
            case 1: dis= new DataInputStream(this.getClass().getResourceAsStream("bin/terrain.1")); break;
            case 2: dis= new DataInputStream(this.getClass().getResourceAsStream("bin/terrain.2")); break;
            case 3: dis= new DataInputStream(this.getClass().getResourceAsStream("bin/terrain.3")); break;
            default: dis= new DataInputStream(this.getClass().getResourceAsStream("bin/terrain.4")); break;
        }

        byte height = dis.readByte();
        byte water_level = dis.readByte();

        short top = dis.readShort();
        short left = dis.readShort();
        short right = dis.readShort();

        gs.setBounds(top, left, right);

        short len = dis.readShort();

        byte[] data = new byte[len];    // terrain data
        dis.read(data, 0, len);         
        dis.close();
        Terrain ret_val = new NokiaTerrain(gs, data, height, (short)gs.getWidth(), (short)(gs.getHeight() + 1), this, background);
        ret_val.setWaterLevel(water_level);
        return ret_val;
    }
    
    public void loadLevel(GameState gs, int index) throws IOException
    {
        DataInputStream dis;
        switch (index)
        {
            case -1: dis= new DataInputStream(this.getClass().getResourceAsStream("bin/intro.0")); break;
            case 0: dis= new DataInputStream(this.getClass().getResourceAsStream("bin/level.0")); break;
            case 1: dis= new DataInputStream(this.getClass().getResourceAsStream("bin/level.1")); break;
            case 2: dis= new DataInputStream(this.getClass().getResourceAsStream("bin/level.2")); break;
            case 3: dis= new DataInputStream(this.getClass().getResourceAsStream("bin/level.3")); break;
            default: dis= new DataInputStream(this.getClass().getResourceAsStream("bin/level.4")); break;
        }

        
        short len = dis.readShort();
        if (len > 0)
        {
            short[] data = new short[len];    // terrain data
            for (int i = 0; i < len; i++) data[i] = dis.readShort();
            gs.setMissionData(data);
        }
        while (true)
        {
            byte type = dis.readByte();
            if (type == -1) 
            {
                dis.close();
                return;
            }
            
            GameObject go = null;
            Visual vis = null;
            switch(type)
            {
                case 0:     // Tree
                {
                    go = gs.createTree(dis.readByte(), Common.toFP(dis.readShort()));
                    break;
                }
                case 1:     // Tank
                {
                    go = gs.createTank(dis.readByte(), Common.toFP(dis.readShort()));
                    break;
                }
                case 2:     // Building
                {
                    go = gs.createBuilding(dis.readByte(), dis.readShort(), dis.readByte());
                    break;
                }
                
                case 3:     // Cloud
                {
                    vis = gs.createCloud(dis.readByte(), Common.toFP(dis.readShort()), Common.toFP(dis.readShort()), Common.toFP(dis.readShort()));
                    break;
                }
                case 4:     // Zeppelin
                {
                    go = gs.createZeppelin(dis.readShort(), dis.readShort(), dis.readShort(), dis.readShort());
                    break;
                }
                case 5:     // Submarine
                {
                    go = gs.createSubmarine(Common.toFP(dis.readShort()), Common.toFP(dis.readShort()));
                    break;
                }

                
                
            }
            
            if (go != null)
            {
                go.setFlags(dis.readInt());
            }
        }
    }
    
    
    
    public void release()
    {
        m_resources = null;
    }
    
    Drawable getPlane(int index)
    {
        return m_resources[index];
    }

    Drawable getDebris()
    {
        return m_resources[4];
    }

    
    Drawable getCloud(int index)
    {
        return m_resources[5 + index];
    }

    Drawable getBomb(int index)
    {
        return m_resources[6 + index];
    }
    
    Drawable getBuilding(int index)
    {
        return m_resources[8 + index];
    }
    
    Drawable getTerrainBackground(int index)
    {
        return m_resources[13 + index];
    }
    
    Drawable getScoreBoard()
    {
        return m_resources[14];
    }
    
    Drawable getScoreBoardSymbols()
    {
        return m_resources[15];
    }
    
    BitmapFont getFont()
    {
        return m_bitmap_font;
    }
    
    Drawable getTank(int index)
    {
        return m_resources[16 + index];
    }

   Drawable getTree(int index)
   {
        return m_resources[17 + index];
   }
    
   Drawable getSun()
   {
        return m_resources[20];
   }
   
   Drawable getIcons()
   {
        return m_resources[21];
   }

   Drawable getWoodTile(int index)
   {
        return m_resources[22 + index];
   }
   
   Drawable getIndicator()
   {
        return m_resources[23];
   }

   Drawable getBomberTitle()
   {
        return m_resources[24];
   }

   Drawable getZeppelin()
   {
        return m_resources[25];
   }
   
   Drawable getSubmarine()
   {
        return m_resources[26];
   }

    public Storage getStorage()
    {
        return m_storage;
    }
    
    public Image getTerrainImage()
    {
        if (m_terrain_images.empty() == false)
        {
            return (Image)m_terrain_images.pop();
        }
        else 
        {
            return Image.createImage((int)TERRAIN_IMAGE_WIDTH, (int)TERRAIN_IMAGE_HEIGHT); 
        }
    }
    
    public void releaseTerrainImage(Image i)
    {
        if (i != null && m_terrain_images.search(i) == -1) 
        {
            m_terrain_images.push(i);
        }
    }
     
    public BomberMIDlet getMidlet()
    {
        return m_midlet;
    }
}