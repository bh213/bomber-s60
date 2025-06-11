using System;
using System.IO;
using System.Text;
using System.Text.RegularExpressions;
using System.Globalization;
namespace tobin
{

	class ToBin
	{
        public enum MODE_TYPE
        {
            BYTE,
            WORD,
            DWORD
        };

	    
        static private short shortToNO(short i)
        {    
            byte[] temp = BitConverter.GetBytes(i);
            Array.Reverse(temp);
            short returnVal = BitConverter.ToInt16(temp, 0);
            return(returnVal);
        }


        static private int intToNO(int i)
        {    
            byte[] temp = BitConverter.GetBytes(i);
            Array.Reverse(temp);
            int returnVal = BitConverter.ToInt32(temp, 0);
            return(returnVal);
        }
	    
	    
	    static MODE_TYPE m_mode;
	    static int m_written;
	    static void write(BinaryWriter bw, int number)
	    {

	        System.Console.WriteLine("num {0}, mode {1}", number, m_mode);
            switch(m_mode)
	        {
	            case ToBin.MODE_TYPE.BYTE : bw.Write((byte) number);m_written++;break;
	            case ToBin.MODE_TYPE.WORD : bw.Write(shortToNO((short) number));m_written+=2;break;
	            case ToBin.MODE_TYPE.DWORD : bw.Write(intToNO((int) number));m_written+=4;break;
	        }
	    }
	
	
	    static void includeBinary(BinaryWriter bw, string filename)
	    {
	        FileStream file;

            file = new FileStream(filename, FileMode.Open);
            System.Console.WriteLine("Including file {0}, length {1}", filename, file.Length);
            
            byte[] buff = new Byte[file.Length];
            bw.Write(intToNO((int) file.Length));
            m_written+=2;
            file.Read(buff, 0, (int)file.Length);
            bw.Write(buff);
            m_written+=buff.Length;

	    }
	
        static void insertBinary(BinaryWriter bw, string filename)
        {
            FileStream file;

            file = new FileStream(filename, FileMode.Open);
            System.Console.WriteLine("Including file {0}, length {1}", filename, file.Length);
            
            byte[] buff = new Byte[file.Length];
            file.Read(buff, 0, (int)file.Length);
            bw.Write(buff);
            m_written+=buff.Length;

        }

		[STAThread]
		static void Main(string[] args)
		{
		    m_mode = ToBin.MODE_TYPE.DWORD;
            if (args.Length != 2)
            {
                System.Console.WriteLine("Usage: input_file output_file");
                return;
            }
            
		    string in_filename = args[0];
		    string out_filename = args[1];
            
            FileStream in_file;
		    try
		    {
                in_file = new FileStream(in_filename, FileMode.Open);
            
            
		    System.Console.WriteLine("Creating output file {0}", out_filename);
		    FileStream out_file = new FileStream(out_filename, FileMode.Create);
		    BinaryWriter bw = new BinaryWriter( out_file/*, Encoding.BigEndianUnicode*/);
		
		    
		    StreamReader sr = new StreamReader(in_file);
		    
            while (sr.Peek() > -1) 
            {
                string text = sr.ReadLine();
                
                
                Regex r;

                 r = new Regex("(\\s+)");
                 string[] res = r.Split(text);
                bool include = false;
                bool insert = false;
                
                
                for (int i = 0; i < res.Length; i++) 
                {
                    string val = res[i].ToLower();
                    val = val.Trim();
                    if (val.Length == 0) continue;


                    if (include)
                    {
                        includeBinary(bw, val);
                        include = false;
                    }
                    else if (insert)
                    {
                        insertBinary(bw, val);
                        insert = false;
                    }
                    else if (val.CompareTo("byte") == 0) m_mode = ToBin.MODE_TYPE.BYTE;
                    else if (val.CompareTo("word") == 0) m_mode = ToBin.MODE_TYPE.WORD;
                    else if (val.CompareTo("short") == 0) m_mode = ToBin.MODE_TYPE.WORD;
                    else if (val.CompareTo("int") == 0) m_mode = ToBin.MODE_TYPE.DWORD;
                    else if (val.CompareTo("include") == 0) include = true;
                    else if (val.CompareTo("insert") == 0) insert= true;
                    else if (val.StartsWith("#")) break;
                    else
                    {
                        int number;
                        if (val.StartsWith("0x"))
                            number = Int32.Parse(val.Remove(0,2), NumberStyles.AllowHexSpecifier);
                        else
                            number = Int32.Parse(val, NumberStyles.Any);
                        write(bw, number);
                    }
                    
                    
                }

                
                
                
            }
            System.Console.WriteLine("Written {0} bytes.", m_written);
            sr.Close();
            bw.Close();
		    
		   
		    
            }
            catch(Exception e)
            {
                System.Console.WriteLine("Error: {0}", e.Message);
                return;
            }
		
		
		
		}
	}
}
