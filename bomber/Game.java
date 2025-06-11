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
 *  File:       Game.java
 *  Content:    Game class
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import java.util.Vector;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	Game
//	Desc:	Class that contains game state (and all game objects)
// ==========================================================================;

public class Game implements GameState
{
    // game state types
    protected static final byte PLAYING           = 0;
    protected static final byte GAME_OVER         = 1;
    protected static final byte BRIEFING          = 2;
    protected static final byte LEVEL_COMPLETE    = 3;
    protected static final byte CUSTOM            = 4;
    // modes
    protected static final byte NORMAL            = 0;
    protected static final byte INTRO             = 1;
    
    protected  Vector     m_objects;        // all object currently flying around
    protected  Vector     m_visual;         // all visual objects (clouds, smoke, etc.)
    protected  Terrain    m_terrain;        // terrain object
    
    protected int         m_full_width;     // full width of the image
    protected int         m_full_height;    // full height of the image
    
    protected int         m_view_start_height;     // starting height of the viewport
    protected int         m_view_height;           // height of the viewport
    
    
    protected int m_scroll_x;               // current position of the viewport
    protected int m_scroll_y;               
    protected int m_scroll_offset_x;       // for addition scroll in the direction of movement
    
    protected int   m_bound_left;
    protected int   m_bound_right;
    protected int   m_bound_top;

    protected byte     m_game_state_type;      // current game state type (game, briefing, game over, etc)
    protected boolean  m_handling = true;    // is handle called for all object (eg. for pause)
    protected boolean  m_is_lookahead = true;
    protected int      m_handle_speed_factor = Common.FIXED;
    protected String   m_level_name;
    protected short[]   m_mission_data = null;
    

    
    protected ResourceManager   m_resource_manager;
    
    protected GameHandler       m_game_handler;
    protected boolean           m_quit;             // quit the game?
    
    protected byte[]            m_special_state;    // special state flags
    protected int               m_score;    // current score
    protected GameObject[]      m_targets;  // targets for current game (if any)
    protected byte              m_level;    // current level of the game
    protected byte              m_difficulty_setting;   // difficulty settings for the current game
    
    
    protected final static int MAX_OBJECT_RADIUS = 50 * Common.FIXED;
    
    protected final static int[] m_plane_info =    
    {
           // plane 1 (player's plane)
           80,      // turn rate
           250,     // fire rate
           50,      // engine
           7,      // mass
           2048,      // hitpoints
           // plane 2 (very easy enemy)
           160,      // turn rate
           750,     // fire rate
           50,      // engine
           9,       // mass
           1024,      // hitpoints
           // plane 3
           100,      // turn rate
           500,     // fire rate
           40,      // engine
           7,      // mass
           2048,      // hitpoints
           // plane 4
           100,      // turn rate
           300,      // fire rate
           50,       // engine
           7,        // mass
           2048,      // hitpoints

    };
    
    protected final static int[] c_water_table =    
    {
           0xf0f0ff,
           0xeeeeff,
           0xd0d0ff,
           0xddddff,
           0xffffff
    };

    protected final static int[] c_tree_table =    
    {
           0x6AC78E,
           0x46D875,
           0x137E3A,
           0xBC581C,
    };
    
    protected final static int[] c_ground_table =    
    {
           0x000000,
           0x400000,
           0x201010,

    };
    
    
    private final static int[] c_explosion_color_table =    
    {
         0xff0000,
         0xff4040,
         0xff6060,
         0xff8080
    };
    
    private final static int[] c_fireworks_red_color_table =    
    {
         0xff0000
    };
    
    private final static int[] c_fireworks_blue_color_table =    
    {
         0x0000ff
    };
    
    private final static int[] c_fireworks_green_color_table =    
    {
         0x00ff00
    };
    
    
    private final static int[] c_spark_color_table =    
    {
         0xFDE059,
         0xffff40,
         0xffff80,
         0xffffff
    };

    
    
    private void init(ResourceManager rm, int width, int height) 
    {
        m_objects = new Vector();
        m_visual = new Vector();
        m_resource_manager = rm;
        m_full_width = width;
        m_full_height = height;
        m_special_state = new byte[SPECIAL_STATE_SIZE];
        resetScreenOptions();
        m_score = 0;
        m_level = 0;
    }
    
    public Game(ResourceManager rm, int width, int height, byte mode) throws Exception
    {
        init(rm, width, height);
        if (mode == NORMAL)
        {
            reset();
        }
        else if (mode == INTRO)
        {
            m_game_state_type = CUSTOM;
            m_terrain = m_resource_manager.loadTerrain(this, 0, true);
            m_resource_manager.loadLevel(this, -1);
            m_game_handler = new IntroGameHandler(this);
        }
    }


    
    private void makeTargets(int flags)
    {
        int size = 0;
        for (int i = 0; i < getObjectSize(); i++)
        {
            if (getGameObject(i).getFlags() == flags)
            {
                size++;
            }
        }
        m_targets = new GameObject[size];

        size = 0;
        for (int i = 0; i < m_objects.size(); i++)
        {
            if (getGameObject(i).getFlags() == flags)
            {
                m_targets[size++] = getGameObject(i);
            }
        }
    }
    
    
     public String getDifficultyBonusMultiplyerAsString()
     {
             switch (m_difficulty_setting)
             {
                 case 0 : return "X 1";
                 case 1 : return "X 1.5";
                 default: return "X 2";
             }
     }     

    
    protected void loadLevel(int level) throws Exception
    {
        int l = level % 5;

        switch (m_difficulty_setting)
        {
                 case 0 : m_level_name = Str.level + Integer.toString(m_level + 1) + Str.level_name_easy; break;
                 case 1 : m_level_name = Str.level + Integer.toString(m_level + 1) + Str.level_name_normal; break;
                 default: m_level_name = Str.level + Integer.toString(m_level + 1) + Str.level_name_hard; break;
        }

        if (l == 2)
        {
            m_terrain = m_resource_manager.loadTerrain(this, 0, true);
            m_resource_manager.loadLevel(this, 0);
            makeTargets(level % 2 + 1);    
            briefing(Str.mission_1_briefing);
        }
        else if (l == 0)
        {
            m_terrain = m_resource_manager.loadTerrain(this, 1, true);
            m_resource_manager.loadLevel(this, 1);
            makeTargets(1);    
            briefing(Str.mission_2_briefing);
        }
        else if (l == 1)
        {
            m_terrain = m_resource_manager.loadTerrain(this, 2, false);
            m_resource_manager.loadLevel(this, 2);
            makeTargets(1);    
            briefing(Str.mission_3_briefing);
        }
        else if (l == 3)
        {
            m_terrain = m_resource_manager.loadTerrain(this, 3, false);
            m_resource_manager.loadLevel(this, 3);
            makeTargets(1);    
            briefing(Str.mission_zeppelin_briefing);
        }
        else if (l == 4)
        {
            m_terrain = m_resource_manager.loadTerrain(this, 4, false);
            m_resource_manager.loadLevel(this, 4);
            makeTargets(1);    
            briefing(Str.mission_sub_briefing);
        }
        handle(0);
        
        
    }
    
    
    protected void reset() throws Exception
    {
        for (int i = 0; i < m_objects.size(); i++) 
        {
            getGameObject(i).destruct();
        }        
        m_objects.removeAllElements();
        m_visual.removeAllElements();
        if (m_terrain != null) m_terrain.destruct();
        for (int i = 0; i < SPECIAL_STATE_SIZE; i++) m_special_state[i] = 0;
        m_difficulty_setting = m_resource_manager.getStorage().getDifficultyLevel();
        
        loadLevel(m_level);
        System.gc();
    }
    
    protected int binaryFindObject(int val)
    {
        int min = 0;
        int max = m_objects.size();
        int i = min;
        
        while (min < max)
        {
            i = (min + max) / 2;
            if (((GameObject)m_objects.elementAt(i)).getPos().x < val) min = i + 1;
            else if (((GameObject)m_objects.elementAt(i)).getPos().x > val) max = i - 1;
            else return i;
        }
        return i;
    }
        
    
    
    void insertionSort() 
    {
        int n = m_objects.size();
        int pos_place_to_insert;  // index of possible location to insert the
                                  // first unsorted object
        GameObject to_insert;     // value to be inserted
 
        for (int i = 1; i < n; i++) 
        {
                // objects 0.. i are sorted
		 
            if ( ((GameObject)m_objects.elementAt(i)).getPos().x < ((GameObject)m_objects.elementAt(i - 1)).getPos().x )
            { 
                to_insert = (GameObject)m_objects.elementAt(i);
                pos_place_to_insert = i; 
            do
	    {
	      pos_place_to_insert--;  // move the possible insertion place down by 
              m_objects.setElementAt(m_objects.elementAt(pos_place_to_insert), pos_place_to_insert + 1);
	    }  
            while ((pos_place_to_insert > 0) && (to_insert.getPos().x < ((GameObject)m_objects.elementAt(pos_place_to_insert - 1)).getPos().x));
            m_objects.setElementAt(to_insert,pos_place_to_insert);
	} 
      
    } 
  }

    
    
    public void addObject(GameObject go)
    {
        //m_objects.insertElementAt(go, binaryFindObject(go.getPos().x));
        m_objects.addElement(go);
    }
    

    public void setBounds(int top, int left, int right)
    {
        m_bound_left = left;
        m_bound_right = right;
        m_bound_top = top;
    }
    
    void handleScroll(int x, int y, int delta)
    {
            if (m_is_lookahead)
            {
                int max = Common.mul(Common.toFP(m_full_width), 200);
                if (m_scroll_offset_x > max) m_scroll_offset_x = max;
                else if (m_scroll_offset_x < -max) m_scroll_offset_x = -max;
                
            }
            else m_scroll_offset_x = 0;
            
            m_scroll_x = Common.toInt(x) - m_full_width / 2 + Common.toInt(m_scroll_offset_x);
            m_scroll_y = Common.toInt(y) - m_view_height / 2 + 20 - m_view_start_height;
            
            limitScroll();
    }
    
    void limitScroll()
    {
            int h = m_view_height + m_view_start_height;
            
            //if (m_game_handler.isScoreBoard()) h -=  m_resource_manager.getScoreBoard().getHeight(0) ;

            if (m_scroll_y > m_terrain.getBottomBound() - h) m_scroll_y = m_terrain.getBottomBound() - h;
            else if (m_scroll_y < m_bound_top + m_view_start_height) m_scroll_y = m_bound_top + m_view_start_height;
            if (m_scroll_x < m_bound_left) m_scroll_x = m_bound_left;
            else if (m_scroll_x > m_bound_right - m_full_width) m_scroll_x = m_bound_right - m_full_width;

    }
    
    
    public void handle(int delta) throws Exception
    {
        if (m_handling)
        {
            if (m_handle_speed_factor != Common.FIXED)
            {
                int new_delta = Common.mul(delta, m_handle_speed_factor);
                if (delta > 0 && new_delta == 0) new_delta = 1;
                delta = new_delta;
            }
            
            collision(delta);
            for (int i = 0; i < m_objects.size(); i++) 
            {
                GameObject go = (GameObject)m_objects.elementAt(i);
                if (go.handle(delta) == GameObject.DELETE)
                {
                    if (go.getObjectType() == GameObject.PLANE) m_game_handler.planeDestroyed((Plane)go);
                    if (go.getObjectType() == GameObject.TANK) m_game_handler.tankDestroyed((Tank)go);
                    if (go.getObjectType() == GameObject.ZEPPELIN) m_game_handler.zeppelinDestroyed((Zeppelin)go);
                    else if (go.getObjectType() == GameObject.BUILDING) m_game_handler.buildingDestroyed((Building)go);
                    else m_game_handler.objectDestroyed(go);
                    m_objects.removeElementAt(i);
                    i--;
                    go.destruct();
                }
            }
            // sort X
           insertionSort();

        }        
        
        // handle visual objects
        for (int i = 0; i < m_visual.size(); i++) 
        {
            if (((Visual)m_visual.elementAt(i)).handle(delta) == Visual.DELETE)
            {
                m_visual.removeElementAt(i);
                i--;
            }
        }
       
       // update targets
        if (m_targets != null)
        {
            for (int i = 0; i < m_targets.length; i++)
            {
                if (m_targets[i] != null && m_targets[i].isAlive() == false) m_targets[i] = null;
            }
        }
       
       
        m_game_handler.handle(delta);
        if (m_game_state_type == PLAYING && m_game_handler.isFinished() == GameHandler.FAILED)
        {
            gameOver();
        }
        else if (m_game_state_type == PLAYING && m_game_handler.isFinished() == GameHandler.SUCCESS)
        {
            levelComplete();
        }
        else if (m_game_state_type == GAME_OVER && m_game_handler.isFinished() != GameHandler.NOT_FINISHED)
        {
            m_quit = true;
        }
        else if (m_game_state_type == BRIEFING && m_game_handler.isFinished() != GameHandler.NOT_FINISHED)
        {
            gamePlay();
        }
        else if (m_game_state_type == LEVEL_COMPLETE && m_game_handler.isFinished() != GameHandler.NOT_FINISHED)
        {
            reset();
        }
        else if (m_game_state_type == CUSTOM && m_game_handler.isFinished() != GameHandler.NOT_FINISHED)
        {
            m_quit = true;
        }

        switch(m_game_handler.getScrollType())
        {

            case GameHandler.POINT:
            {
                m_scroll_offset_x = 0;
                Point p = (Point)m_game_handler.getScroll();
                m_scroll_x = Common.toInt(p.x);
                m_scroll_y = Common.toInt(p.y);
                limitScroll();
                break;
            }
            case GameHandler.CENTER_POINT:
            {
                m_scroll_offset_x = 0;
                Point p = (Point)m_game_handler.getScroll();
                handleScroll(p.x, p.y, delta); 
                break;
            }   
            
            case GameHandler.OBJECT: 
            {
                GameObject go = (GameObject)m_game_handler.getScroll();
                 if (go == null || go.isAlive() == false) break;
                 
                Point p =  go.getPos();

                if (go instanceof Rotatable)
                {
                    int angle = ((Rotatable)go).getAngle();
                    m_scroll_offset_x += Common.mul(Common.mul(Common.toFP(m_full_width)/2, Common.cos(angle)), delta) ;
                    m_scroll_offset_x =  Common.mul(m_scroll_offset_x, 950);
                }
                handleScroll(p.x, p.y, delta); 
                break;
                }
        }
    }

     public void draw(Graphics g)
     {
        resetClip(g);
        int x = Common.toFP(m_scroll_x);
        int y = Common.toFP(m_scroll_y);
        
        
        m_terrain.drawTerrain(g, m_scroll_x, m_scroll_y, m_full_width, m_view_height);
        
        int clip_width = MAX_OBJECT_RADIUS + Common.toFP(g.getClipWidth());
        for (int i = 0; i < m_objects.size(); i++) // WARN ME: optimize this
        {
            
            GameObject go = (GameObject)m_objects.elementAt(i);
            if (go.isAlive() == false) continue;
            if (Math.abs(go.getPos().x - x) > clip_width) continue;
            go.draw(g, x, y);
        }
        for (int i = 0; i < m_visual.size(); i++) 
        {
            Visual vis = (Visual)m_visual.elementAt(i);
            vis.draw(g, x, y);
        }
        
        Helper.resetFullScreenClip(g);
        m_game_handler.draw(g, x, y);
    }
      
     public void collision(int delta)
     {
         int max_h = Common.toFP(m_terrain.getMaxHeight());

         for (int i = 0; i < m_objects.size(); i++) 
         {
             GameObject go = (GameObject)m_objects.elementAt(i);
             if (go.isAlive() == false) continue;
             
             // --- handler over the bounds handling ---
             int top = Common.toFP(m_bound_top);
             int left = Common.toFP(m_bound_left);
             int right = Common.toFP(m_bound_right);
             
             if ((go.getType() & GameObject.BOUNDS_DETECT) != 0) 
             {
                int radius = go.getRadius();
                
                if (go.getPos().y + radius < top) m_game_handler.objectOutsideBounds(GameHandler.TOP, go);
                else if (go.getPos().y - radius > 0) m_game_handler.objectOutsideBounds(GameHandler.BOTTOM, go);
                if (go.getPos().x + radius < left) m_game_handler.objectOutsideBounds(GameHandler.LEFT, go);
                else if (go.getPos().x - radius > right) m_game_handler.objectOutsideBounds(GameHandler.RIGHT, go);

                if (go.isAlive() == false) continue;
                 
             }
             boolean immobile = (go.getType() & GameObject.IMMOBILE) != 0;
             if ((go.getType() & GameObject.COLLIDABLE_INTER_OBJ) != 0) 
             {
                 // --- object object collision ---
                 int check_start = Math.max(binaryFindObject(go.getPos().x - MAX_OBJECT_RADIUS), i + 1);
                 int check_end = Math.min(binaryFindObject(go.getPos().x + MAX_OBJECT_RADIUS) + 1, m_objects.size() - 1);

                 for (int j = check_start; j <= check_end; j++) 
                 {
                     GameObject go2 =  (GameObject)m_objects.elementAt(j);
                     if (go2.isAlive() == false) continue;
                     if (Math.abs(go.getPos().y  - go2.getPos().y) > MAX_OBJECT_RADIUS) continue;   // only y needs to be checked 
                                                                                                    // array is x sorted        
                      if (immobile && ((go2.getType() & GameObject.IMMOBILE) != 0)) continue;       // immobile-immobile check
                     
                     if (go.checkCollision(go2))
                     {
                         go2.collided(go, delta);
                         go.collided(go2, delta);
                     }
                 }
             }


             if ((go.getType() & GameObject.COLLIDABLE_TERRAIN) != 0) 
             {
                // --- object ground collision ---             
                 if (go.getPos().y + go.getRadius() < max_h) continue;

                 byte collide_type;
                 BoundingBox bb = go.getBoundingBox();
                 if (bb != null)
                 {
                     collide_type = m_terrain.collide(bb);
                 }
                 else if (go.getRadius() > 0)
                 {
                    collide_type = m_terrain.collide(go.getPos(), go.getRadius());

                 }
                 else 
                 {
                    collide_type = m_terrain.collide(go.getPos());
                 }

                 if (collide_type != Terrain.NO_COLLIDE)
                 {
                    if ((go.getType() & GameObject.TERRAIN_DAMAGE) != 0 && (collide_type != Terrain.WATER))
                        m_terrain.crater(Common.toInt(go.getPos().x), Common.toInt(go.getRadius()), 5);
                    go.collidedTerrain(m_terrain, (collide_type == Terrain.WATER), delta);   
                 }
             }
         }
     }

// =========================================================================;
//	protected void gameOver()
//	Desc:	handles end of the game
// ==========================================================================;
     
     protected void gameOver() throws Exception
     {
         resetScreenOptions();
         enableHandling();
         m_game_handler = new GameOverHandler(this, Common.toFP(m_scroll_x), Common.toFP(m_scroll_y));
         //m_game_handler = new StaticGameHandler(this, Str.game_over, m_control);
         m_game_state_type = GAME_OVER;
     }
 
     protected void levelComplete()
     {
         resetScreenOptions();
         enableHandling();
         checkSpecialStateEnd();        // awards special handling flags (ie, awards tank buster, if available)
         m_level++;
         m_game_handler = new LevelCompleteHandler(this, Integer.toString(m_level), Common.toFP(m_scroll_x), Common.toFP(m_scroll_y));
         //m_game_handler = new StaticGameHandler(this, Str.game_over, m_control);
         m_game_state_type = LEVEL_COMPLETE;
     }
     
     
     protected void briefing(String[] message)
     {
         resetScreenOptions();
         enableHandling();
         m_game_state_type = BRIEFING;
         m_game_handler = new BriefingGameHandler(this, message, m_targets);
     }
     
     protected void gamePlay()
     {
         resetScreenOptions();
         enableHandling();
         checkSpecialStateStart();  // sets special handling flags (ie, check, for example, if tank buster bonus is available)
         m_game_handler = new MissionGameHandler(this, m_targets);
         m_game_state_type = PLAYING;
     }
     
     
     public int getObjectSize()
     {
         return m_objects.size();
     }
     
     public GameObject getGameObject(int index)
     {
         return (GameObject)m_objects.elementAt(index);
     }
    
     public void addVisual(Visual visual)
     {
         m_visual.addElement(visual);
     }
     
     public void createBomb(int x, int y, int vx, int vy) 
     {
         addObject(new Bomb(this, m_resource_manager.getBomb(0), m_resource_manager.getBomb(1), vx, vy, 20000, x, y));
     }
     
     public void createDebris(int x, int y, int vx, int vy, int radius)
     {
         addObject(new FallingDebris(this, x, y, vx, vy, radius, 20000));
     }     

     
     public void createBlast(int x, int y, int radius, int smoke) 
     {
          addObject(new Blast(this, x, y, radius, smoke));
     }
     
     public void addScore(int score) 
     {
         if (score > 0)
         {
             switch (m_difficulty_setting)
             {
                 case 1: score = (score * 3) / 2; break;
                 case 2: score *= 2; break;
             }
         }
         m_score += score;
         if (m_score < 0) m_score = 0;
         if (m_score > 99999) m_score = 99999;
     }
     
     public Terrain getTerrain() 
     {
        return m_terrain;
     }
     
     public void createExplosion(int x, int y) 
     {
         addVisual(new Explosion(c_explosion_color_table, x, y, (byte)20, 6000, 6000));
     }
     
     public void createFireworks(int x, int y)
     {
         switch(Math.abs(random.nextInt() % 3))
         {
             case 0: addVisual(new Explosion(c_fireworks_green_color_table, x, y, (byte)20, 6000, 6000));break;
             case 1: addVisual(new Explosion(c_fireworks_red_color_table, x, y, (byte)20, 6000, 6000));break;
             case 2: addVisual(new Explosion(c_fireworks_blue_color_table, x, y, (byte)20, 6000, 6000));break;
         }
     }     
     
     public void createSpark(int x, int y) 
     {
         addVisual(new Explosion(c_spark_color_table, x, y, (byte)6, 4000, 6000));
     }
     
     public void createDirectionSpark(int x, int y, int angle, int dispersion) 
     {
         addVisual(new Explosion(c_spark_color_table, x, y, (byte)6, 4000, 6000, angle, dispersion, (byte)-1, (byte)-1));
     }

     public void createControlledSpark(int x, int y, int angle, int angle_dispersion, byte speed, byte speed_dispersion)
     {
         addVisual(new Explosion(c_spark_color_table, x, y, (byte)6, 4000, 6000, angle, angle_dispersion, speed, speed_dispersion));
     }

     
     
     public void createSplash(byte type, int x, int y, byte particles, byte xspeed, byte yspeed, byte x_speed_add) 
     {

           switch(type)
           {
               case WATER: addVisual(new Splash(x, y, (byte)particles, (byte)3, 40000, xspeed, yspeed, x_speed_add, c_water_table)); break;
               case GROUND: addVisual(new Splash(x, y, (byte)particles, (byte)3, 40000, xspeed, yspeed, x_speed_add, c_ground_table)); break;
               case TREE: addVisual(new Splash(x, y, (byte)particles, (byte)3, 40000, xspeed, yspeed, x_speed_add, c_ground_table)); break;
           }
     }
     
     public int getScore()
     {
         return m_score;
     }
     
     public Plane createPlane(int type, int x, int y, int angle, int speed)
     {
         // int turn_rate, int fire_rate, int engine_power, int mass
         int idx = type * 5;
         Plane p = new Plane(this, m_resource_manager.getPlane(0), m_resource_manager.getPlane(1),
                                   m_plane_info[idx + 0], m_plane_info[idx + 1], 
                                   m_plane_info[idx + 2], m_plane_info[idx + 3], m_plane_info[idx + 4]);
         p.setPoint(x, y);
         p.setSpeed(speed);
         p.setAngle(angle);
         addObject(p);
         return p;
     }

     public AIPlane createAIPlane(int type, int x, int y, int angle, int speed)
    {
        int idx = type * 5;
        AIPlane p = new AIPlane(this, m_resource_manager.getPlane(2), m_resource_manager.getPlane(3),
                   m_plane_info[idx + 0], m_plane_info[idx + 1], 
                   m_plane_info[idx + 2], m_plane_info[idx + 3], m_plane_info[idx + 4]);

         p.setPoint(x, y);
         p.setSpeed(speed);
         p.setAngle(angle);
         addObject(p);
         return p;

    }     

     
     
     public Drawable getPlaneDrawable(int index)
    {
        return m_resource_manager.getPlane(index);
    }     

     
     public void createFlakSmoke(int x, int y, int radius) 
     {
        addVisual(new FlakSmoke(x, y, radius));
     }     
     
     public void createBullet(int x, int y, int vx, int vy, int existance, int blast) 
     {
                addObject(new Bullet(this, x, y, vx, vy, existance, blast));
     }
     
     public Building createBuilding(int type, int x, byte hitpoints) 
     {
            Building b = new Building(this, m_resource_manager.getBuilding(type), Common.toFP(x), hitpoints);
            addObject(b);
            return b;
     }
     
     
     public void event(byte type, boolean pressed)
     {
         m_game_handler.event(type, pressed);
     }

     public boolean isQuit()
     {
         return m_quit;
     }
     
     public ResourceManager getResourceManager()
     {
         return m_resource_manager;
     }
     
     public int getHeight()
     {
         return m_full_height;
     }
     
     public int getWidth()
     {
         return m_full_width;
     }
     
     public void resetScreenOptions()
     {
            m_view_start_height = 0;
            m_view_height = m_full_height; 
     }
     
     public void setScreenOptions(int start, int height)
     {
        m_view_start_height = start;
        m_view_height = height;
     }
     
     public void resetClip(Graphics g)
     {
         g.setClip(0, m_view_start_height, m_full_width, m_view_height);
     }     

     
     
     public void enableHandling()
     {
         m_handling = true;
     }
     
     public void disableHandling()
     {
         m_handling = false;
     }
     
     public void destruct()
     {
        m_objects.removeAllElements();
        m_visual.removeAllElements();
        m_objects = null;
        m_visual = null; 
        if (m_terrain != null) m_terrain.destruct();
        m_terrain = null;
        m_targets = null;  
        m_resource_manager = null;
        m_game_handler = null;
     }
     
     public Storage getStorage()
     {
         return m_resource_manager.getStorage();
     }
     
     public Tree createTree(byte index, int pos_x)
     {
        Tree ret_val = new Tree(this, m_resource_manager.getTree(index), pos_x);
        addObject(ret_val);
        return ret_val;
     }

     public Tank createTank(byte index, int pos_x)
     {
        Tank ret_val = new Tank(this, m_resource_manager.getTank(index), pos_x);
        addObject(ret_val);
        return ret_val;
     }     
     
     public Tank createSubmarine(int x, int y)
     {
        Tank ret_val = new Tank(this, m_resource_manager.getSubmarine(), x, y);
        addObject(ret_val);
        return ret_val;
     }    
     
     
     public Zeppelin createZeppelin(int x, int y, int speed_x, int hitpoints)
     {
        Zeppelin ret_val = new Zeppelin(this, m_resource_manager.getZeppelin(), x, y, speed_x, hitpoints);
        addObject(ret_val);
        return ret_val;
     }
     
     public Cloud createCloud(byte type, int x, int y, int vx)
     {
         Cloud ret_val = new Cloud(this, m_resource_manager.getCloud(type), x, y, vx);
         addVisual(ret_val);
         return ret_val;
     }     
     
     public boolean isSpecialState(byte type)
     {
        return m_special_state[type] > 0;
     }     

     public void setSpecialState(byte type)
     {
         if (m_special_state[type] == 0) m_special_state[type] = 1;
     }     

    public void clearSpecialState(byte type)
     {
         if (m_special_state[type] == 1) m_special_state[type] = 0;
     }     

     
     public void disableSpecialState(byte type)
     {
          m_special_state[type] = -1;
     }
     
     public String getLevelName()
     {
         
         return m_level_name;
     }
     
     public byte getDifficultyLevel()
     {
         return (byte)Math.min(m_level + 1 + m_difficulty_setting * 2, (byte)10);
     }
     
     public String getSpecialStateName(byte type)
     {
         return Str.special_states[type];
     }     
     
     
     public boolean isTarget(GameObject go)
     {
         if (m_targets == null) return false;
        
        for (int i = 0; i < m_targets.length; i++)
        {
            if (m_targets[i] == go) return true;
        }
        return false;
     }
     
// =========================================================================;
//	Name:	void checkSpecialEnd()
//	Desc:	checks which special states can actually be won on this 
//              level     
// ==========================================================================;
     protected void checkSpecialStateStart()
     {
        if (m_targets == null || m_targets.length == 0) disableSpecialState(SURGICAL_STRIKE);
        boolean devastator = false;
        boolean lumberjack = false;
        boolean tankbuster = false; 
        boolean surgical_strike = false; 
        
        for (int i = 0; i < getObjectSize(); i++)
        {
            GameObject go = getGameObject(i);
            if (go.getObjectType() == GameObject.BUILDING && isTarget(go) == false) 
            {
                devastator = true;
                surgical_strike = true;
            }
            if (go.getObjectType() == GameObject.TREE && isTarget(go) == false) lumberjack = true;
            if (go.getObjectType() == GameObject.TANK && isTarget(go) == false) 
            {
                tankbuster = true;
                surgical_strike = true;
            }
        }
        
        if (devastator == false) disableSpecialState(DEVASTATOR);
        if (lumberjack == false) disableSpecialState(LUMBERJACK);
        if (tankbuster == false) disableSpecialState(TANK_BUSTER);
        if (surgical_strike == false) disableSpecialState(SURGICAL_STRIKE);
        else setSpecialState(SURGICAL_STRIKE);
     }
    
// =========================================================================;
//	Name:	void checkSpecialStateEnd()
//	Desc:	checks which states have been acheived
// ==========================================================================;
     protected void checkSpecialStateEnd()
     {
        boolean devastator = true;
        boolean lumberjack = true;
        boolean tankbuster = true; 
        
        for (int i = 0; i < getObjectSize(); i++)
        {
            GameObject go = getGameObject(i);
            if (go.isAlive() == false) continue;
            if (go.getObjectType() == GameObject.BUILDING) devastator = false;
            else if (go.getObjectType() == GameObject.TANK) tankbuster = false;
            else if (go.getObjectType() == GameObject.TREE) lumberjack = false;
        }
        
        if (devastator) setSpecialState(DEVASTATOR);
        if (lumberjack) setSpecialState(LUMBERJACK);
        if (tankbuster) setSpecialState(TANK_BUSTER);
     }
     
     
     public int getSpecialStateScore(byte type)
     {
         switch(type)
         {
             case KAMIKAZE:             return 100;
             case BOMBER:               return 10;
             case GUNNER:               return 50;
             case SURGICAL_STRIKE:      return 100;
             case DEVASTATOR:           return 50;
             case LUMBERJACK:           return 10;
             case GOLD_MEDAL:           return 100;
             case TANK_BUSTER:          return 50;
             case CON_MAN:              return 200;
             case IRON_MAN:             return 25;
             case ACE:                  return 20;
             default:                   return 0;
         }
     }
     
     
     public int getLeftBound()
     {
         return Common.toFP(m_bound_left);
     }     

     public int getRightBound()
     {
         return Common.toFP(m_bound_right);
     }     
     
     public int getTopBound()
     {
         return Common.toFP(m_bound_top);
     }
     
     public void disableLookahead()
     {
         m_is_lookahead = false;
     }
     
     public void enableLookahead()
     {
         m_is_lookahead = true;
     }
     
     public void setHandleSpeed(int factor)
     {
         m_handle_speed_factor = factor;
     }
     
     public short[] getMissionData()
     {
         return m_mission_data;
     }
     
     public void setMissionData(short[] data)
     {
         m_mission_data = data;
     }
     
 

     
}
