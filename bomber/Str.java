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
 *  File:       Str.java
 *  Content:    Strings for use in the game
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;

// =========================================================================;
//	Name:	Str class
// ==========================================================================;

public class Str 
{
    static final String game_over = "GAME OVER";
    static final String press_any_key = "PRESS FIRE";
    static final String fatal_error = "Fatal Error";
    
    
    static final String[] mission_2_briefing = {"You are to perform an early morning attack on enemy base. Destroy all buildings "+
    "and tanks.\nThere are some interceptors in the area, so fly carefully"};
    static final String[] mission_3_briefing = {"Enemy has taken over the island and has captured our code-books. You must destroy everything on the island, to "+
    "prevent enemy from using them.", "Please be aware that enemy is expecting our attack, so expect heavy opposition.\nGood luck."};
    static final String[] mission_1_briefing = {"Before major attack, we need to 'soften' the enemy position. Destroy the shown tanks to acheive victory."};
    static final String[] mission_zeppelin_briefing = {"We have located a number of enemy zeppelins. Attack them from above and destroy them all."};
    static final String[] mission_sub_briefing = {"Attack and sink all enemy submarines in the area. Beware of their anti-aircraft fire and keep in mind that bombs and bullets behave differently in water."};

    static final String next = "Next >>>";
    static final String highscore = "HIGH SCORE";
    
    static final String opt_menu_play = "PLAY!";
    static final String opt_menu_instructions = "INSTRUCTIONS";
    static final String opt_menu_highscores= "HIGH SCORES";
    static final String opt_menu_sound_on = "SOUND ON";
    static final String opt_menu_sound_off = "SOUND OFF";
    
    static final String opt_menu_level_easy = "EASY GAME";
    static final String opt_menu_level_normal = "NORMAL GAME";
    static final String opt_menu_level_hard = "HARD GAME";
    
    static final String opt_menu_set_player_name= "SET PLAYER NAME";
    
    static final String opt_menu_exit= "EXIT";
    
    
    // Paused screen text
    static final String exit = "EXIT";
    static final String resume = "RESUME";
    static final String paused = "PAUSED";
    
    
    static final String opt_player = "Player Name";
    static final String opt_get_player_name = "Enter Your Name";
    static final String ok = "OK";
    static final String cancel = "Cancel";
    static final String new_high_score = "NEW HIGH SCORE";
    static final String unknown = "unknown";
    static final String score = "SCORE";
    static final String level = "LEVEL ";
    //static final String intro = "Copyright 2003 While True, d.o.o.\nAll Rights Reserved.\nhttp://www.whiletrue.si";
    //static final String intro = "Copyright 2003 While True, d.o.o.\nhttp://www.whiletrue.si";
    
    static final String instructions = "Instructions";
    static final String instructions_text = 
    "Nokia 7650:\n Control plane by using UP or 2 and DOWN or 8. Drop bombs by using FIRE or 5, and use machine gun with button 1.\n"+
    "Nokia 3650:\n Control plane by using UP or 1 and DOWN or 3. Drop bombs by using FIRE or #, and use machine gun with button 4, 5, 6 or 0.\n"+
    "On top left side of the screen are three icons:\n"+
    "First one (the one with cross) represents number of targets left\n"+
    "Second one (with plane) represents number of enemy planes in reserve.\n"+
    "Last one (with clock) represents time, in which you have to complete the mission to get GOLD MEDAL bonus.\n"+
    "When mission is finished, you can be awarded additional bonuses:\n"+
    "KAMIKAZE - you haven't fired gun or dropped bombs entire mission\n"+
    "BOMBER - you haven't fired gun\n"+
    "GUNNER - you haven't dropped any bombs\n"+
    "PRECISION - you destroyed ONLY mission targets (and maybe some trees)\n"+
    "DEVASTATOR - you destroyed all buildings\n"+
    "LUMBER JACK - you chopped down all the trees\n"+
    "TANK BUSTER - all tanks have been destroyed\n"+
    "IRON MAN- you still have all five lives\n"+
    "GOLD MEDAL- you finished level, before timer run off\n"+
    "ACE- you destroyed all enemy planes\n"+
    "CON MAN- almost impossible to get. Destroy all targets without firing or dropping bombs AND without losing lives.\n"+
    
    "Keep in mind, that it is not always possibile to get bonuses. For example, if your orders are to "+
    "destroy all tanks, then you won't get TANK BUSTER bonus, as you were only following orders and not doing anything extra.\n"+
    "To get new bombs, simply fly over boundaries of the map (left or right).";
    

    
    static final String level_name_easy = " - EASY";
    static final String level_name_normal = " - NORMAL";
    static final String level_name_hard = " - HARD";
    
    static final String difficulty_bonus = "DIFFICULTY";
    static final String[] special_states = 
    {
        "KAMIKAZE",
        "BOMBER",
        "GUNNER",
        "PRECISION",
        "DEVASTATOR",
        "LUMBER JACK",
        "GOLD MEDAL",
        "TANK BUSTER",
        "CON ARTIST",
        "IRON MAN",
        "ACE"
    };
}
