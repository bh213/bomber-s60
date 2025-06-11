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
 *  File:       Helper.java
 *  Content:    Helper class
 *  Created:    December 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;
import javax.microedition.lcdui.*;

// =========================================================================;
//	Name:	Helper class (static functions only)
//	Desc:	Contains helper methods, that are used in the code
// ==========================================================================;

public class Helper  
{
    private Helper(){}       // we cannot create instance of this object

    private static int m_press_any_key_counter = 0;
    final static public int PRESS_ANY_KEY_BLINK_RATE = 1000;
    static private int g_text_blink = 1000;
    static public BitmapFont font;
    static public ResourceManager resource_manager;
    
    
// =========================================================================;
//	Name:	void drawPressAnyKey(Graphics g, int y, int width, byte color)
//	Desc:	draws (blinking) "press any key" string
//              handlePressAnyKey must be called periodically for this
//              method to work.    
// ==========================================================================;
    
    public static void drawPressAnyKey(Graphics g, int y, int width, byte color)
    {
            if (g_text_blink < PRESS_ANY_KEY_BLINK_RATE / 2)
            {
                font.setSubFont(color);
                int px = -font.getStringWidth(Str.press_any_key)/2 + width/2;
                font.drawString(g, px, y, Str.press_any_key);
            }
    }
    
    public static void handlePressAnyKey(int delta)
    {
        g_text_blink = (int)(g_text_blink + delta) % PRESS_ANY_KEY_BLINK_RATE;
    }
    
// =========================================================================;
//	Name:	void resetClip(Graphics g)
//	Desc:	Resets clipping to full screen. There are some bugs with
//              clipping, for example on 7650    
// ==========================================================================;
    
    public static void resetFullScreenClip(Graphics g)
    {
                g.setClip(0, 0, ResourceManager.CANVAS_WIDTH, ResourceManager.CANVAS_HEIGHT);
    }
    
// =========================================================================;
//	Name:	void drawStringCenter(Graphics g, String s, int y, 
//              int width, byte color)
//	Desc:	Draws string in specific color. String is displayed in the
//              horizontal center of the screen    
// ==========================================================================;  
    
    public static void drawStringCenter(Graphics g, String s, int y, int width, byte color)
    {
                font.setSubFont(color);
                int px = -font.getStringWidth(s) / 2 + width/2;
                font.drawString(g, px, y, s);
    }

    
// =========================================================================;
//	Name:	void drawStringLeft(Graphics g, String s, int x, 
//              int y, byte color)
//	Desc:	Draws string in specific color. String is displayed left
//              aligned    
// ==========================================================================;  
    
    public static void drawStringLeft(Graphics g, String s, int x, int y, byte color)
    {
                font.setSubFont(color);
                int px = x - font.getStringWidth(s);
                font.drawString(g, px, y, s);
    }
    
    
// =========================================================================;
//	Name:	void drawMenu(Graphics g, String[] s, int y, 
//              int width, int selected)
//	Desc:	Draws menu
// ==========================================================================;  
    
    public static void drawMenu(Graphics g, String[] s, int x, int y, int selected)
    {
        for (int i = 0; i < s.length; i++)
        {

            //int color = 0xC0AD92;
            int color = 0x808080;
            if (i != selected)
            {
                drawString(g, s[i], x, y, ResourceManager.FONT_YELLOW);
                
            }
            else
            {
                drawString(g, s[i], x, y, ResourceManager.FONT_GREEN);
                color = ResourceManager.c_warm_colors[(i + 1) % ResourceManager.c_warm_colors.length];
            }
            drawBox(g, x - 14, y, color);

            
            y += font.getCharHeight() + 4;
        }
        
    }
    
    
// =========================================================================;
//	Name:	void drawStringListCenter(Graphics g, String[] s, 
//              int y, int width, byte color)
//	Desc:	Draws multiple lines of horizontally centered strings.
// ==========================================================================;  
    
    public static void drawStringListCenter(Graphics g, String[] s, int y, int width, byte color)
    {
        font.setSubFont(color);
        for (int i = 0; i < s.length; i++)
        {
            int px = -font.getStringWidth(s[i]) / 2 + width/2;
            font.drawString(g, px, y, s[i]);
            y += font.getCharHeight() + 2;
        }
    }

    
// =========================================================================;
//	Name:	void drawText(Graphics g, String s, 
//              int x, int y, int width, int height)
//	Desc:	draws text into given rectangle
// ==========================================================================;  
    
    public static void drawText(Graphics g, Font f, String s, int x, int y, int width, int height, int scroll_y)
    {
        g.setFont(f);
        g.setClip(x, y, width, height);
        y -= scroll_y;
        int orig_x = x;
        int orig_y = y;
        int pos = 0;
        int next_pos;
        int word_end = 0;
        boolean flag = true;
        boolean eol = false;
        while (pos < s.length())
        {
            if (y > orig_y + height) 
            {
                resetFullScreenClip(g);
                return;
            }
            int tmp = s.indexOf(" ", pos);
            if (tmp == -1) 
            {
                word_end = s.length() - 1;
                flag = true;
            }
            else word_end = tmp;
            next_pos = word_end + 1;
            
            tmp = s.indexOf("\n", pos);
            if (tmp != -1 && tmp < word_end)
            {
                eol = true;
                word_end = tmp - 1;
                next_pos = tmp + 1;
            }
            
            tmp = f.substringWidth(s, pos, word_end - pos + 1);

            if (x + tmp > orig_x + width)
            {
                x = orig_x;
                y += f.getHeight() + 2;
                eol = false;
            }
            
            g.drawSubstring(s, pos, word_end - pos + 1, x, y, Graphics.TOP | Graphics.LEFT);
            x += tmp;
            if (eol)
            {
                x = orig_x;
                y += f.getHeight() + 2;
                eol = false;
            }
            pos = next_pos;
        }
        resetFullScreenClip(g);
    }
    
    
// =========================================================================;
//	Name:	void drawString(Graphics g, String s, 
//              int x, int y, byte color)
//	Desc:	Draws string on specific position
// ==========================================================================;      
    
    public static void drawString(Graphics g, String s, int x, int y, byte color)
    {
                font.setSubFont(color);
                font.drawString(g, x, y, s);

    }
 
// =========================================================================;
//	Name:	void drawString(Graphics g, String s, 
//              int x, int y, byte color)
//	Desc:	Draws string on specific position
// ==========================================================================;      
    
     public static void drawScoreBoard(Graphics g, int y, int score, int lives, int bombs)
     {
         resource_manager.getScoreBoard().drawImage(g, 0, 0, y);
         y += 3;
         drawScore(g, score, 3, y);
         
         for (int i = 0; i < 5; i++)
         {
             resource_manager.getScoreBoardSymbols().drawImage(g, i >= bombs ? 11 : 10, 60 + i * 10, y);
         }

         for (int i = 0; i < 5; i++)
         {
             resource_manager.getScoreBoardSymbols().drawImage(g, i >= lives ? 13 : 12, 126 + i * 10, y);
         }
     }
    
// =========================================================================;
//	Name:	void drawScore(Graphics g, int score, 
//              int x, int y)
//	Desc:	Displays score (max 5 digits) with specific score fonts.
// ==========================================================================;  
     
    public static void drawScore(Graphics g, int score, int x, int y)
    {
         for (int i = 0; i < 5; i++)
         {
             int num = score % 10;
             score /= 10;
             resource_manager.getScoreBoardSymbols().drawImage(g, num, x + (4 - i) * 7, y);
         } 
    }
    
    public static void drawIconAboveObject(Graphics g, GameObject go, Drawable icon, int index, int x, int y)
    {
            icon.drawImage(g, index, Common.toInt(go.getPos().x - x), Common.toInt(go.getPos().y - y - go.getRadius()));
    }
    
    
    public static void drawIndicator(Graphics g, int x, int y, GameObject go, int s_x, int s_y, int s_x1, int s_y1)
    {
            int r = Common.ceilInt(go.getRadius());
            int c_x = Common.toInt(go.getPos().x - x);
            int c_y = Common.toInt(go.getPos().y - y);
            
            int d_x, d_y;
            
            byte x_index = 0;
            byte y_index = 0;
            
            
            if (c_x < s_x - r) 
            {
                d_x = s_x;
                x_index = (byte)-1;
            }
            else if (c_x > s_x1 + r) 
            {
                d_x = s_x1;
                x_index = (byte)1;
            }
            else d_x = c_x;

            
            if (c_y < s_y - r) 
            {
                d_y = s_y;
                y_index = (byte)-1;
            }
            else if (c_y > s_y1 + r) 
            {
                d_y = s_y1;
                y_index = (byte)1;
            }
            else d_y = c_y;
            
            int index;  // icon direction index
            if (x_index == 0 && y_index == 0) return;   // object on screen
            else if (x_index == 0 && y_index == 1) index = 2;
            else if (x_index == 1 && y_index == 0) index = 0;
            else if (x_index == 1 && y_index == 1) index = 1;
            else if (x_index == 0 && y_index == -1) index = 6;
            else if (x_index == -1 && y_index == 0) index = 4;
            else if (x_index == -1 && y_index == -1) index = 5;
            else if (x_index == 1 && y_index == -1) index = 7;
            //else if (x_index == -1 && y_index == 1) index = 3;
            else index = 3;
            
            
            
            resource_manager.getIndicator().drawImage(g, index, d_x, d_y);
    }
    
     
    public static void drawNumber(Graphics g, int number, int x, int y, int fixed_places)
    {
        String s = Integer.toString(number);
        fixed_places = Math.max(fixed_places - s.length(), 0);
        Drawable symbols = resource_manager.getScoreBoardSymbols();
        for (int i = 0; i < fixed_places; i++)
         {
             symbols.drawImage(g, 0, x, y);
             x += symbols.getWidth(0);
         } 
        
         for (int i = 0; i < s.length(); i++)
         {
             int num = s.charAt(i) - '0';
             symbols.drawImage(g, num, x, y);
             x += symbols.getWidth(num);
             
         } 
    }
    
        
    public static void drawBox(Graphics g, int x, int y, int color)
    {
        g.setColor(0);
        g.drawRect(x,y, 10, 10);
        g.setColor(color);
        g.fillRect(x + 1, y + 1, 9, 9);
    }
    
    
    
}
    