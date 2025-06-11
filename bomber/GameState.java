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
 *  File:       GameState.java
 *  Content:    GameState interface
 *  Created:    November 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;

// =========================================================================;
//	Name:	GameState interface
//	Desc:	interface that allows GameObjects access to global game state,
//              i.e. accessing other objects data, adding new objects,
//              and possibly deleting objects
// ==========================================================================;

import java.util.Random;
import javax.microedition.lcdui.*;

public interface GameState 
{
//  --- splash types  ---
    public static final byte    GROUND  = 0;
    public static final byte    WATER   = 1;
    public static final byte    TREE    = 2;
// --- special states ---
    public static final byte    KAMIKAZE            = 0;    // no bombs/bullets fired
    public static final byte    BOMBER              = 1;    // no bullets fired
    public static final byte    GUNNER              = 2;    // no bombs dropped
    public static final byte    SURGICAL_STRIKE     = 3;    // ONLY targets destroyed
    public static final byte    DEVASTATOR          = 4;    // all building destroyed
    public static final byte    LUMBERJACK          = 5;    // all trees destroyed
    
    public static final byte    GOLD_MEDAL          = 6;    

    public static final byte    TANK_BUSTER         = 7;    // all tanks destroyed
    public static final byte    CON_MAN             = 8;    // KAMIKAZE + no lives lost
    public static final byte    IRON_MAN            = 9;    // no lives lost
    public static final byte    ACE                 = 10;    // all planes shot down
       
    public static final byte    SPECIAL_STATE_SIZE          = 11;    
    
    
    public static Random random    =  new Random();
    
    void addObject(GameObject go);          // adds object to gamestate
    void addVisual(Visual v);               // adds visual object to gamestate
    int getObjectSize();                    // gets number of objects
    GameObject getGameObject(int index);    // gets object at specific index
    
    
// system settings    
    
    void setScreenOptions(int start,        // sets screen y dimension
                          int height);
    void resetScreenOptions();
    void resetClip(Graphics g);             // sets clip as it was set according to setScreenOptions
    
    void enableHandling();                  // enables handling (calling handle(delta) )
    void disableHandling();                 // disables handling (for pause, etc).
    
    void enableLookahead();                 // enables lookahead for Rotatable objects
    void disableLookahead();                 // disables lookahead for Rotatable objects
    
    void setHandleSpeed(int factor);       // selects new speed factor for handling
    
    ResourceManager getResourceManager();   // gets resource manager object
    Storage getStorage();                   // gets storage object
    Drawable  getPlaneDrawable(int index);  // visual of the given plane
    
    int getWidth();                         // returns width of the current canvas
    int getHeight();                        // returns height of the current canvas
//  specific game functions
    
    void setMissionData(short[] data);
    short[] getMissionData();
    void setBounds(int top, int left, 
                   int right);              // sets game bounds
    void addScore(int score);               // adds score
    int getScore();                         // gets current score
    
    void setSpecialState(byte type);         // sets special state
    void clearSpecialState(byte type);       // clears special state
    void disableSpecialState(byte type);     // disables special state
    
    boolean isSpecialState(byte type);      // gets special state status
    String getSpecialStateName(byte type);  // gets display name for special state
    int getSpecialStateScore(byte type);    // gets score bonus or malus for special state score
    
    String getLevelName();                  // returns current level
    String getDifficultyBonusMultiplyerAsString();
    byte getDifficultyLevel();              // return dificulty level
    
    boolean isTarget(GameObject go);        // is object target?
    
    int getTopBound();                     // gets top bound
    int getRightBound();                   // gets right bound
    int getLeftBound();                    // gets left bound
    
    Terrain getTerrain();                   // gets terrain object
    
    void createExplosion(int x, int y);                   // creates explosion
    void createFireworks(int x, int y);                   // creates fireworks
    void createSpark(int x, int y);                       // creates spark
    void createDirectionSpark(int x, int y, 
                              int angle, int dispersion); // creates spark that goes in specific direction

    void createControlledSpark(int x, int y, 
                              int angle, int angle_dispersion,
                              byte speed, byte speed_dispersion
                              );                          // creates spark that goes in specific direction with specific speed

    
    void createBomb(int x, int y, int vx, int vy);      // creates new bomb object
    void createDebris(int x, int y, int vx, int vy, int radius);    // creates falling debris
    
    
    void createBullet(int x, int y, int vx, int vy, 
                      int existance, int blast);          // creates new bullet of flak
    void createBlast(int x, int y, 
                     int radius, int smoke);              // creates explosion blast
    void createFlakSmoke(int x, int y, int radius);       // creates flak smoke
    void createSplash(byte type, int x, int y, byte particles, 
                      byte xspeed, byte yspeed, 
                      byte x_speed_add);                 // creates splash
    
    Tree createTree(byte type, int pos_x);               // creates tree
    Tank createTank(byte type, int pos_x);               // creates tank
    Tank createSubmarine(int x, int y);                  // creates submarine
    Zeppelin createZeppelin(int x, int y, int speed_x, int hitpoint);               // creates tank
    Cloud createCloud(byte type, int x, int y, 
                     int vx);                            // creates cloud
    
    Plane createPlane(int type, int x, int y, 
                      int angle, int speed);             // creates plane 
    
    AIPlane createAIPlane(int type, int x, int y, 
                      int angle, int speed);             // creates ai controlled plane 

    
    Building createBuilding(int type, int x, 
                            byte hitpoints);             // creates building
    
}
