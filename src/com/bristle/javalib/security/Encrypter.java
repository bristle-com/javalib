// Copyright (C) 2005-2012 Bristle Software, Inc.
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 1, or (at your option)
// any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc.

package com.bristle.javalib.security;

import java.io.UnsupportedEncodingException;

import com.bristle.javalib.util.Base64;
import com.bristle.javalib.util.Base64.InvalidBase64DigitException;
import com.bristle.javalib.util.Base64.TruncatedBase64EncodingException;

// Encrypter
/******************************************************************************
* This class contains support encrypting and decrypting data.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*     - To encrypt a string:
*               String strEncrypted = Encrypter.encrypt(strDecrypted);
*     - To decrypt an encrypted string:
*               String strDecrypted = Encrypter.decrypt(strEncrypted);
*
*   - See the source code of the inner Tester class for more examples.
*  
*<b>Assumptions:</b>
*<b>Effects:</b>
*       - None.
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class Encrypter
{
    //--
    //-- Class variables
    //--

    //--
    //-- Instance variables to support public properties
    //--

    //--
    //-- Internal instance variables
    //--

    /**************************************************************************
    * Return the encrypted string value of the specified string.  The 
    * specified string is assumed to use the ISO-8859-1 (Latin-1) encoding.
    *@param  strIn          String to be encrypted.
    *@return                Encrypted string.
    *@throws UnsupportedEncodingException
    *                       When the incoming string does not use the 
    *                       ISO-8859-1 (Latin-1) encoding.
    **************************************************************************/
    public static String encrypt(String strIn)
                throws UnsupportedEncodingException
    {
        strIn = Base64.encode(strIn);

        // Note:  Strip off trailing equals signs and rotate the last
        //        char to the front of the string after Base64 encoding, 
        //        as an extra level of obfuscation.
        int intIndexOfEqualSign = strIn.indexOf('=');
        if (intIndexOfEqualSign != -1)
        {
            strIn = strIn.substring(0,intIndexOfEqualSign);
        }
        int intLastIndex = strIn.length() - 1;
        if (intLastIndex > 0)
        {
            strIn = strIn.substring(intLastIndex)
                  + strIn.substring(0,intLastIndex);
        }
        return strIn;
    }

    /**************************************************************************
    * Return the decrypted string value of the specified string.
    * The specified string is assumed to use the ISO-8859-1 (Latin-1) encoding.
    *@param  strIn          String to be decrypted.
    *@return                Decrypted string.
    *@throws UnsupportedEncodingException
    *                       When the incoming string does not use the 
    *                       ISO-8859-1 (Latin-1) encoding.
    *@throws InvalidBase64DigitException
    *                       When the string to be decrypted contains a character
    *                       that is not a valid Base64 digit char.
    *@throws TruncatedBase64EncodingException
    *                       When the string to be decrypted has an invalid 
    *                       length.
    **************************************************************************/
    public static String decrypt(String strIn)
                throws UnsupportedEncodingException
                      ,InvalidBase64DigitException
                      ,TruncatedBase64EncodingException
    {
        // Rotate the first char of the Base64 encoding to the end before
        // decoding, as an extra level of obfuscation.
        if (strIn.length() > 1)
        {
            strIn = strIn.substring(1) + strIn.substring(0,1);
        }
        return Base64.decode(strIn);
    }

    /**************************************************************************
    * Each class contains a Tester inner class with a main() for easier
    * unit testing.  To call main from the command line, use:
    * <pre>
    *   java class$Tester
    *</pre>
    * where "class" is the name of the outer class.
    **************************************************************************/
    public static class Tester
    {
        /**********************************************************************
        * Main testing method.
        *@param  args       Array of command line argument strings
        **********************************************************************/
        public static void main(String[] args)
        {
            try
            {
                if (args.length > 0)
                {
                    if (args[0].equals("-decrypt"))
                    {
                        System.out.println(decrypt(args[1]));
                        return;
                    }
                    if (args[0].equals("-encrypt"))
                    {
                        System.out.println(encrypt(args[1]));
                        return;
                    }
                }

                System.out.println("Begin tests...");
                System.out.println("...End tests.");
            }
            catch (Throwable e)
            {
                System.out.println("Error in main(): ");
                e.printStackTrace();
            }
        }
    }
}
