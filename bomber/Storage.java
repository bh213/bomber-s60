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
 *  File:       Storage.java
 *  Content:    Storage class (highscore & settings)
 *  Created:    Januar 2002
 *  Created by: gorazd breskvar
 *
 ****************************************************************************/

package bomber;

// =========================================================================;
//	Name:	Storage class 
//	Desc:	handles and stores game highscore
// ==========================================================================;

import javax.microedition.rms.*;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;

/**
 * A class used for storing and showing game scores.
 */
public class Storage 
{
    static public int   HIGHSCORE_SIZE  =   10;
    
    private RecordStore m_record_store = null; // The RecordStore used for storing the game scores.
    private int         m_size;     // size of high score
    private int[]       m_scores;
    private String[]    m_names;
    
    private String      m_player_name;      // name of current player
    private boolean     m_sound_enabled;    // is sound enabled
    private byte        m_difficulty_level; // difficulty level settings
    

    
// =========================================================================;
//	Name:	HighScore 
//	Desc:	The constructor opens the underlying record store,
//              creating it if necessary.
// ==========================================================================;
    public Storage() throws RecordStoreException, IOException, Exception
    {
	//
	// Create a new record store for this example
	//
	m_record_store = RecordStore.openRecordStore("scores", true);
        m_scores = new int[HIGHSCORE_SIZE];
        m_names = new String[HIGHSCORE_SIZE];
        m_player_name = Str.unknown;
        m_sound_enabled = true;
        m_difficulty_level = (byte) 0;
        
        //m_record_store.deleteRecord(1);
        
        try
        {
            readRecordStore();
        }
        catch(InvalidRecordIDException rie)
        {
            m_size = 0;
            byte [] val = createByteForRecordStore();
            m_record_store.addRecord(val, 0, val.length);   // create ID 1 record
        }
    }

    
    public void save() throws Exception
    {
        byte [] val = createByteForRecordStore();
        m_record_store.setRecord(1, val, 0, val.length);   // create ID 1 record
    }
    
    protected byte[] createByteForRecordStore() throws IOException
    {
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(array);
        dos.writeShort(m_size);
        dos.writeUTF(m_player_name);
        dos.writeBoolean(m_sound_enabled);
        dos.writeByte(m_difficulty_level);
        
        for (int i = 0; i < m_size; i++)
        {
            dos.writeInt(m_scores[i]);
            dos.writeUTF(m_names[i]);
        }
        byte[] ret_val = array.toByteArray();
        dos.close();
        return ret_val;
    }
   
    protected void readRecordStore() throws Exception
    {
        byte[] data = m_record_store.getRecord(1);
        ByteArrayInputStream array = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(array);
        m_size = dis.readShort();
        m_player_name = dis.readUTF();
        m_sound_enabled = dis.readBoolean();
        m_difficulty_level = dis.readByte();
        for (int i = 0; i < m_size; i++)
        {
            m_scores[i] = dis.readInt();
            m_names[i] = dis.readUTF();
        }
        dis.close();
    }
    
    public int getSize()
    {
        return m_size;
    }
    
    public String getName(int index)
    {
        return m_names[index];
    }
    
    public int getScore(int index)
    {
        return m_scores[index];
    }
    
    public void addScore(int score) throws Exception
    {
        for (int i = 0; i < Math.min(m_size, HIGHSCORE_SIZE); i++)
        {
            if (score >= m_scores[i])
            {
                for (int j = Math.min(m_size, HIGHSCORE_SIZE - 1) - 1; j >= i; j--)
                {
                    m_scores[j + 1] = m_scores[j];
                    m_names[j + 1] = m_names[j];
                }
                m_scores[i] = score;
                m_names[i] = m_player_name;
                if (m_size < HIGHSCORE_SIZE) m_size++;
                save();
                return;
            }
        }
        if (m_size < HIGHSCORE_SIZE)
        {
                m_scores[m_size] = score;
                m_names[m_size] = m_player_name;
                m_size++;
        }
        save();
    }
    
    public int isHighScore(int score) 
    {
        for (int i = 0; i < m_size; i++)
        {
            if (score >= m_scores[i]) return i;
        }
        if (m_size < HIGHSCORE_SIZE) return m_size;
        else return -1;

    }
    
    
    public String getPlayerName()
    {
        return m_player_name;
    }
    
    public void setPlayerName(String name) throws Exception
    {
        m_player_name = name;
        save();
    }
    
    public boolean getSoundEnabled()
    {
        return m_sound_enabled;
    }
    
     public void  setSoundEnabled(boolean b) throws Exception
    {
        m_sound_enabled = b;
        save();
    }
    

    public byte getDifficultyLevel()
    {
        return m_difficulty_level;
    }
    
     public void setDifficultyLevel(byte b) throws Exception
    {
        m_difficulty_level = b;
        save();
    }

    
}
