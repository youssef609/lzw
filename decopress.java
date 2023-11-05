/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LZW_Algorithm;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;


public class decopress {
       // Define a HashMap and other variables that will be used in the program
    public HashMap<Integer, String> dictionary = new HashMap<>();
    public String[] Array_char;
    public int dictSize = 256;
    public int currword;
    public int priorword;
    public byte[] buffer = new byte[3];
    public boolean onleft = true;

    /**
     * Decompress Method that takes in input, output as a file path Then
     * decompress the input to same file as the one passed to compress method
     * without loosing any information. In the decompression method it reads in
     * 3 bytes of information and write 2 characters corresponding to the bits
     * read.
     *
     * @param input - Name of input file path
     * @throws java.io.IOException - File input/output failure
     */
    public void LZW_Decompress(String input) throws IOException {
        // DictSize builds up to 4k, Array_Char holds these values
        Array_char = new String[4096];
        String ch="";

        for (int i = 0; i < 256; i++) {
            dictionary.put(i, Character.toString((char) i));
            Array_char[i] = Character.toString((char) i);
        }
          //System.out.println(Array_char);
        // Read input as uncompressed file & Write out compressed file
        RandomAccessFile in = new RandomAccessFile(input, "r");
        RandomAccessFile out = new RandomAccessFile(input.replace(
                ".lzw", ""), "rw");

        try {
            // Gets the first word in code and outputs its corresponding char
            buffer[0] = in.readByte();
            buffer[1] = in.readByte();
            priorword = getvalue(buffer[0], buffer[1], onleft);
            onleft = !onleft;
            out.writeBytes(Array_char[priorword]);

            // Reads every 3 bytes and generates corresponding characters
            while (true) {
                int c=0;
                
                if (onleft) {
                    buffer[0] = in.readByte();
                    buffer[1] = in.readByte();
                    currword = getvalue(buffer[0], buffer[1], onleft);
                   // System.out.println(currword);
                } else {
                    buffer[2] = in.readByte();
                    currword = getvalue(buffer[1], buffer[2], onleft);
                }
                onleft = !onleft;

                if (currword >= dictSize) {
                    if (dictSize < 4096) {
                        Array_char[dictSize] = Array_char[priorword]
                                + Array_char[priorword].charAt(0);
                    }
                    dictSize++;
                    out.writeBytes(Array_char[priorword]
                            + Array_char[priorword].charAt(0));
                } else {
                    if (dictSize < 4096) {
                        Array_char[dictSize] = Array_char[priorword]
                                + Array_char[currword].charAt(0);
                    }
                    dictSize++;
                    out.writeBytes(Array_char[currword]);
                }
                priorword = currword;
//               // System.out.println(Arrays.toString(Array_char));
//                ch+= Array_char[c];
//                c++;
//                 System.out.println(ch); 
                
            }
           
           
        } catch (EOFException e) {
            in.close();
            out.close();
        }
   
    }

    /**
     * Extract the 12 bit key from 2 bytes and gets the integer value of the key
     *
     * @param b1 - First byte
     * @param b2 - Second byte
     * @param onleft - True if on left, false if not
     * @return - An Integer which holds the value of the key
     */
    public int getvalue(byte b1, byte b2, boolean onleft) {
        String temp1 = Integer.toBinaryString(b1);
        String temp2 = Integer.toBinaryString(b2);

        while (temp1.length() < 8) {
            temp1 = "0" + temp1;
        }
        if (temp1.length() == 32) {
            temp1 = temp1.substring(24, 32);
        }
        while (temp2.length() < 8) {
            temp2 = "0" + temp2;
        }
        if (temp2.length() == 32) {
            temp2 = temp2.substring(24, 32);
        }

        if (onleft) {
            return Integer.parseInt(temp1 + temp2.substring(0, 4), 2);
        } else {
            return Integer.parseInt(temp1.substring(4, 8) + temp2, 2);
        }
    }

    
}
