/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static int L = 512;       // number of codewords = 2^W
    private static int W = 9;         // codeword width
    private static double oldRatio;
    private static double newRatio;
    public static void compress(char argument) { 
        //oldRatio = (original.length() - st.size())/st.size();
        double compressed = 0;
        double uncompressed = 0;
        boolean oldRatioCheck = false;
       BinaryStdOut.write(argument);
        String input = BinaryStdIn.readString();
       String original = input;
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
            
           
        int code = R+1;  // R is codeword for EOF
        while (input.length() > 0) {
            String s = st.longestPrefixOf(input); 
            uncompressed += s.length() * 8.0;
            BinaryStdOut.write(st.get(s), W);   
           compressed += W;// Print s's encoding.
            int t = s.length();
       
            if (t < input.length() && code < L)    // Add s to symbol table.
            {
                st.put(input.substring(0, t + 1), code++);
            }
              
           if(code == L && W < 16)
            {
                W++;
                L = 2* L;
                st.put(input.substring(0, t + 1), code++);
            }    
                    
              if((argument == 'r') && code == 65536)
              {
                 st = new TST<Integer>();
                 for (int i = 0; i < R; i++)
                  st.put("" + (char) i, i);
                  code = R+1;
                 L = 512;       
                 W = 9; 
             }
             else if((argument == 'm')&& code == 65536)
             {
                 newRatio = uncompressed/compressed;
                 System.err.println("Old ratio " + oldRatio);
                 System.err.println("New ratio " + newRatio);
                  
                 if(oldRatio/newRatio > 1.1)//Reset
                 {
                   System.err.println("Monitor");
                   st = new TST<Integer>();
                   for (int i = 0; i < R; i++)
                   st.put("" + (char) i, i);
                   code = R+1;
                   L = 512;       
                   W = 9;
                   oldRatioCheck = false;

                 }
               
             } 
              if(oldRatioCheck == false && code ==65536)
                 {
                 oldRatio = newRatio;
                 oldRatioCheck = true;
                }
                
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
        char argument = BinaryStdIn.readChar(8);
        String[] st = new String[L];
        int i; // next available codeword value
        oldRatio = 0;
        double compressed = 0;
        double uncompressed = 0;
        boolean oldRatioCheck = false;
        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
        {
            st[i] = "" + (char) i;
            
        }
        st[i++] = ""; // (unused) lookahead for EOF
        
        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];
        while (true) {
            BinaryStdOut.write(val);
              
            uncompressed += val.length() * 8.0;
            codeword = BinaryStdIn.readInt(W);
             compressed += W;
           
            if (codeword == R) break;//empty string
            String s = st[codeword];
            
            if (i == codeword)s = val + val.charAt(0);   // special case hack
           
             if (i < L-1) 
            {
               st[i++] = val + s.charAt(0);
            }
           
            if(i == L-1 && W < 16) //(i&(i-1))==0
            {
              W++;
              L = 2*L; 
              st = resize(st);
              st[i++] = val + s.charAt(0);
            }
            
            if(argument == 'r' &&  i == 65536-1)//subtract 1?
            {
               L = 512;       
               W = 9;
               st = new String[L];
               for (i = 0; i < R; i++)
               st[i] = "" + (char) i;

            }
            else if((argument == 'm') && i == 65536-1 )
            {
                newRatio = uncompressed/compressed;
                if(oldRatio/newRatio > 1.1)//Reset
                {
                     L = 512;       
                     W = 9;
                     st = new String[L];
                     for (i = 0; i < R; i++)
                     st[i] = "" + (char) i;
                     oldRatioCheck = false;
                }
                  if(oldRatioCheck == false && i == 65536-1)
                  {
                      oldRatio = newRatio;
                      oldRatioCheck = true;
                }   
            
            }
         
            val = s;
        }
        BinaryStdOut.close();
    }

    public static String[] resize(String[]st)
    {
        String[] array = new String[L];
        for(int i = 0; i < st.length; i++)
        {
            array[i] = st[i];
        }
        return array;
    }

    public static void main(String[] args) {
        char argument;
        if (args[0].equals("-"))
        {
            
             if(args[1].equals("n")||args[1].equals("r")||args[1].equals("m"))
             {
                argument = args[1].charAt(0);
                compress(argument);
             }
             else throw new IllegalArgumentException("Illegal command line argument");
            
        }
        else if (args[0].equals("+"))expand();
    
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
