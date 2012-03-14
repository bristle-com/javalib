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

package com.bristle.javalib.util;

import java.io.UnsupportedEncodingException;

// Base64
/******************************************************************************
* This class contains support for Base64 encoding and decoding.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*     - To encode a string as Base64:
*               String strEncoded = Base64.encode(strDecoded);
*     - To decode a Base64 string:
*               String strDecoded = Base64.decode(strEncoded);
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
public class Base64
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

    //-- Table of the Base64 digit chars used for the 6-bit numbers 0-63 
    //-- in a Base64 encoded string.
    private static final char[] arrchBASE_64_DIGITS = 
                      { 
                        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
                        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
                        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
                        'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 
                        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
                        'w', 'x', 'y', 'z', '0', '1', '2', '3', 
                        '4', '5', '6', '7', '8', '9', '+', '/'
                      };

    /**************************************************************************
    * This exception is thrown when an invalid character is found as a digit
    * in a Base64 encoded string that is being decoded.
    **************************************************************************/
    public static class InvalidBase64DigitException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public InvalidBase64DigitException(String msg) { super(msg); }
    }

    /**************************************************************************
    * This exception is thrown when a Base64 encoded string that is being 
    * decoded has an invalid length, or has a trailing Base64 digit that
    * implies that there should be more digits.
    **************************************************************************/
    public static class TruncatedBase64EncodingException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public TruncatedBase64EncodingException(String msg) { super(msg); }
    }

    /**************************************************************************
    * Assemble an 8-bit byte from 2 8-bit bytes, using the specified shifts
    * and bitmasks.  Each byte is masked, then shifted.  Then the bytes are
    * OR'ed together and the low order 8-bits are returned.  
    * Deals with all the issues like sign bit extension during implicit 
    * promotion to int before shifting.
    *@param  byte1      First byte
    *@param  intMask1   Bitmask to apply before shifting
    *@param  intShift1  Number of bits to shift left (negative for right)
    *@param  byte2      Second byte
    *@param  intMask2   Bitmask to apply before shifting
    *@param  intShift2  Number of bits to shift left (negative for right)
    *@return            Assembled byte.
    **************************************************************************/
    private static int assembleByte
                        (byte byte1,
                         int  intMask1,
                         int  intShift1,
                         byte byte2,
                         int  intMask2,
                         int  intShift2)
    {
        //-- Force the masks to 8 bits or less.
        intMask1 &= 0x000000FF;
        intMask2 &= 0x000000FF;

        //-- Explicitly convert to int to avoid implicit promotion to int 
        //-- during shift operations.
        //-- Note:  This is a big part of the reason we need this routine
        //--        at all.  Simply shifting via:
        //--            ">>>" (the logical right shift operator) 
        //--        instead of:
        //--            ">>"  (the arithmetic right shift operator)
        //--        is not sufficient.  A byte value greater than or equal 
        //--        to 128 (0x80) has the sign bit set.  When you shift it, 
        //--        even with ">>>", it first gets promoted to int, and thus
        //--        sign-extended producing 0xFFFFFF80, not 0x00000080.  
        //--        Then it gets shifted.  Therefore:
        //--            byte b = (byte)0x7F;
        //--            int  i = b >> 1;        /* Arithmetic shift */
        //--        and:
        //--            byte b = (byte)0x7F;
        //--            int  i = b >>> 1;       /* Logical shift */
        //--        both produce i == 0x0000003F, as expected, and:
        //--            byte b = (byte)0x80;
        //--            int  i = b >> 1;        /* Arithmetic shift */
        //--        produces i == 0xFFFFFFC0, as expected, but:
        //--            byte b = (byte)0x80;
        //--            int  i = b >>> 1
        //--        unexpectedly produces i == 0x7FFFFFC0, not the desired
        //--        value of 0x00000040.
        int int1 = (int)byte1;
        int int2 = (int)byte2;

        //-- Apply the specified masks.
        int1 &= intMask1;
        int2 &= intMask2;

        //-- Apply the specified shifts.
        if (intShift1 > 0)
        {
            int1 <<= intShift1;
        }
        else if (intShift1 < 0)
        {
            int1 >>= -intShift1;
        }
        else
        {
            //-- Nothing to do.  Shift by 0 bits is a no-op.
        }
        if (intShift2 > 0)
        {
            int2 <<= intShift2;
        }
        else if (intShift2 < 0)
        {
            int2 >>= -intShift2;
        }
        else
        {
            //-- Nothing to do.  Shift by 0 bits is a no-op.
        }

        //-- Combine and strip off any bits that got shifted outside of 
        //-- the 8-bit range.
        return (int1 | int2) & 0x0000000FF;
    }

    /**************************************************************************
    * Return the Base64 encoded string value of the specified string.  The 
    * specified string is assumed to use the ISO-8859-1 (Latin-1) encoding.
    *@param  strIn          String to be encoded.
    *@return                Encoded string.
    *@throws UnsupportedEncodingException
    *                       When the incoming string does not use the 
    *                       ISO-8859-1 (Latin-1) encoding.
    **************************************************************************/
    public static String encode(String strIn)
                throws UnsupportedEncodingException
    {
        //-- Convert the incoming string to a byte array, using the 
        //-- ISO-8859-1 (Latin-1) encoding.
        final byte[] arrbyteIn = strIn.getBytes("8859_1");

        //-- Grab 24 bits at a time, from 3 bytes, writing 4 Base64 digits.
        StringBuffer sbOut = new StringBuffer();
        for (int i = 0;  i + 2 < arrbyteIn.length;  i = i + 3)
        {
            //-- First 6 bits of first byte
            sbOut.append(arrchBASE_64_DIGITS
                    [assembleByte((byte)0,        0,     0,
                                  arrbyteIn[i],   0xFC, -2)]);
    
            //-- Last 2 bits of first byte and first 4 bits of second byte.
            sbOut.append(arrchBASE_64_DIGITS
                    [assembleByte(arrbyteIn[i],   0x03,  4,
                                  arrbyteIn[i+1], 0xF0, -4)]);
    
            //-- Last 4 bits of second byte and first 2 bits of third byte.
            sbOut.append(arrchBASE_64_DIGITS
                    [assembleByte(arrbyteIn[i+1], 0x0F,  2,
                                  arrbyteIn[i+2], 0xC0, -6)]);
    
            //-- Last 6 bits of third byte.
            sbOut.append(arrchBASE_64_DIGITS
                    [assembleByte(arrbyteIn[i+2], 0x3F,  0,
                                  (byte)0,        0,     0)]);
        }
    
        //-- Handle leftover one or two bytes
        if (arrbyteIn.length % 3 == 2)
        {
            //-- Start 2 chars from the end.
            int i = arrbyteIn.length - 2;

            //-- First 6 bits of first byte
            sbOut.append(arrchBASE_64_DIGITS
                    [assembleByte((byte)0,        0,     0,
                                  arrbyteIn[i],   0xFC, -2)]);
    
            //-- Last 2 bits of first byte and first 4 bits of second byte.
            sbOut.append(arrchBASE_64_DIGITS
                    [assembleByte(arrbyteIn[i],   0x03,  4,
                                  arrbyteIn[i+1], 0xF0, -4)]);
    
            //-- Last 4 bits of second byte, followed by 2 bits of 0.
            sbOut.append(arrchBASE_64_DIGITS
                    [assembleByte(arrbyteIn[i+1], 0x0F,  2,
                                  (byte)0,        0,     0)]);
    
            //-- Pad output string with 1 equals sign.
            sbOut.append("=");
        }
        else if (arrbyteIn.length % 3 == 1)
        {
            //-- Start 1 char from the end.
            int i = arrbyteIn.length - 1;

            //-- First 6 bits of first byte
            sbOut.append(arrchBASE_64_DIGITS
                    [assembleByte((byte)0,        0,     0,
                                  arrbyteIn[i],   0xFC, -2)]);
    
            //-- Last 2 bits of first byte, followed by 4 bits of 0.
            sbOut.append(arrchBASE_64_DIGITS
                    [assembleByte(arrbyteIn[i],   0x03,  4,
                                  (byte)0,        0,     0)]);
    
            //-- Pad output string with 2 equals signs.
            sbOut.append("==");
        }
        return sbOut.toString();
    }

    /**************************************************************************
    * Return the decoded string value of the specified Base64 encoded string.
    * The specified string is assumed to use the ISO-8859-1 (Latin-1) encoding.
    *@param  strIn          String to be decoded.
    *@return                Decoded string.
    *@throws UnsupportedEncodingException
    *                       When the incoming string does not use the 
    *                       ISO-8859-1 (Latin-1) encoding.
    *@throws InvalidBase64DigitException
    *                       When the string to be decoded contains a character
    *                       that is not a valid Base64 digit char.
    *@throws TruncatedBase64EncodingException
    *                       When the string to be decoded has an invalid 
    *                       length.
    **************************************************************************/
    public static String decode(String strIn)
                throws UnsupportedEncodingException
                      ,InvalidBase64DigitException
                      ,TruncatedBase64EncodingException
    {
        //-- Strip off the trailing equals signs, if any.
        int intIndexOfEqualsSign = strIn.indexOf('=');
        if (intIndexOfEqualsSign != -1)
        {
            strIn = strIn.substring(0,intIndexOfEqualsSign);
        }

        //-- Convert the incoming string to a byte array, using the 
        //-- ISO-8859-1 (Latin-1) encoding.
        final byte[] arrbyteIn = strIn.getBytes("8859_1");

        //-- Sparsely populated table of the 6-bit numbers 0-63 represented 
        //-- by the Base64 digit chars, indexed by the chars.  All invalid 
        //-- values in the table contain -1.
        final byte[] arrbyteBase64Values = new byte[128];
        for (int i = 0; i < arrbyteBase64Values.length; i++)
        {
            arrbyteBase64Values[i] = -1;
        }
        for (byte b = 0; b < 64; b++)
        {
            arrbyteBase64Values[arrchBASE_64_DIGITS[b]] = b;
        }

        /**********************************************************************
        * This class contains the method doIt().
        * It is a class only so that it can be nested inside a method.
        **********************************************************************/
        class GetBase64DigitValue
        {
            /******************************************************************
            * Get the 6-bit integer that corresponds to the Base64 digit 
            * specified.
            *@param  intBase64Digit Base64 digit
            *@return                6-bit integer value.
            *@throws InvalidBase64DigitException
            *                       When chBase64Digit is not a valid Base64
            *                       digit char.
            ******************************************************************/
            public byte doIt(int intBase64Digit)
                         throws InvalidBase64DigitException
            {
                try
                {
                    //-- Look up the 6-bit integer value.
                    //-- Note:  If outside of the array, throws an exception
                    //--        which is caught and translated to a new 
                    //--        exception below.
                    byte byteRC = arrbyteBase64Values[intBase64Digit];

                    //-- Detect use of unexpected array slot.
                    //-- Note:  This exception is caught and rethrown with a 
                    //--        better error message below.
                    if (byteRC < 0)
                    {
                        throw new InvalidBase64DigitException
                                ("Dummy message caught and discarded below.");
                    }
                    return byteRC;
                }
                catch (Exception e)
                {
                    throw new InvalidBase64DigitException
                                ("The character \"" 
                                + (char)intBase64Digit 
                                + "\" is not a valid digit of a Base64"
                                + " encoded string."
                                );
                }
            }
        }
        GetBase64DigitValue getBase64DigitValue = new GetBase64DigitValue();

        //-- Grab 24 bits at a time, from 4 Base64 digits, writing 3 chars.
        StringBuffer sbOut = new StringBuffer();
        for (int i = 0;  i + 3 < arrbyteIn.length;  i = i + 4)
        {
            //-- Get the 6-bit integer values of the next 4 Base64 digits.
            byte byteDigit1 = getBase64DigitValue.doIt(arrbyteIn[i]);
            byte byteDigit2 = getBase64DigitValue.doIt(arrbyteIn[i+1]);
            byte byteDigit3 = getBase64DigitValue.doIt(arrbyteIn[i+2]);
            byte byteDigit4 = getBase64DigitValue.doIt(arrbyteIn[i+3]);

            //-- All 6 bits of first digit and first 2 of 6 bits of second 
            //-- digit.
            sbOut.append((char)assembleByte
                                (byteDigit1, 0x3F,  2,
                                 byteDigit2, 0x30, -4));

            //-- Last 4 bits of second digit and first 4 of 6 bits of third 
            //-- digit.
            sbOut.append((char)assembleByte
                                (byteDigit2, 0x0F,  4,
                                 byteDigit3, 0x3C, -2));
    
            //-- Last 2 bits of third digit and all 6 bits of fourth digit.
            sbOut.append((char)assembleByte
                                (byteDigit3, 0x03,  6,
                                 byteDigit4, 0x3F,  0));
        }
    
        //-- Handle leftover one, two or three digits
        if (arrbyteIn.length % 4 == 3)
        {
            //-- Start 3 digits from the end.
            int i = arrbyteIn.length - 3;

            //-- Get the 6-bit integer values of the next 3 Base64 digits.
            byte byteDigit1 = getBase64DigitValue.doIt(arrbyteIn[i]);
            byte byteDigit2 = getBase64DigitValue.doIt(arrbyteIn[i+1]);
            byte byteDigit3 = getBase64DigitValue.doIt(arrbyteIn[i+2]);

            //-- All 6 bits of first digit and first 2 of 6 bits of second 
            //-- digit.
            sbOut.append((char)assembleByte
                                (byteDigit1, 0x3F,  2,
                                 byteDigit2, 0x30, -4));

            //-- Last 4 bits of second digit and first 4 of 6 bits of third 
            //-- digit.
            sbOut.append((char)assembleByte
                                (byteDigit2, 0x0F,  4,
                                 byteDigit3, 0x3C, -2));
    
            //-- Last 2 bits of third digit must be zeroes.
            if ((byteDigit3 & 0x03) != 0)
            {
                throw new TruncatedBase64EncodingException
                                ("The character \"" 
                                + (char)arrbyteIn[i+2]
                                + "\" is not valid as the last character of a "
                                + arrbyteIn.length
                                + "-character Base64 encoded string."
                                );
            }
        }
        else if (arrbyteIn.length % 4 == 2)
        {
            //-- Start 2 digits from the end.
            int i = arrbyteIn.length - 2;

            //-- Get the 6-bit integer values of the next 2 Base64 digits.
            byte byteDigit1 = getBase64DigitValue.doIt(arrbyteIn[i]);
            byte byteDigit2 = getBase64DigitValue.doIt(arrbyteIn[i+1]);

            //-- All 6 bits of first digit and first 2 of 6 bits of second 
            //-- digit.
            sbOut.append((char)assembleByte
                                (byteDigit1, 0x3F,  2,
                                 byteDigit2, 0x30, -4));

            //-- Last 4 bits of second digit must be zeroes.
            if ((byteDigit2 & 0x0F) != 0)
            {
                throw new TruncatedBase64EncodingException
                                ("The character \"" 
                                + (char)arrbyteIn[i+1]
                                + "\" is not valid as the last character of a "
                                + arrbyteIn.length
                                + "-character Base64 encoded string."
                                );
            }
        }
        else if (arrbyteIn.length % 4 == 1)
        {
            //-- A single leftover digit should never occur.  6 bits is not
            //-- enough to form any 8-bit chars.
            throw new TruncatedBase64EncodingException
                                (arrbyteIn.length 
                                + " is not a valid length for a Base64"
                                + " encoded string."
                                );
        }
        return sbOut.toString();
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
        private static void doTest(String strIn, String strOut, String strValid)
        {
            System.out.println("Input: " + strIn);
            System.out.println("Output should be: " + strValid);
            System.out.println("Output is:        " + strOut);
            System.out.println(ObjUtil.equalsOrBothNull(strOut, strValid) 
                               ? "Success!" 
                               : "Failure!");
            System.out.println();
        }
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
                    if (args[0].equals("-decode"))
                    {
                        System.out.println(decode(args[1]));
                        return;
                    }
                    if (args[0].equals("-encode"))
                    {
                        System.out.println(encode(args[1]));
                        return;
                    }
                }

                System.out.println("Begin tests...");

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- assembleByte");
                System.out.println("--");
                System.out.println();
                //-------------------------------------------------------------
                byte b;
                int  i;

                b = (byte)0x7F;
                i =        b >> 1;
                doTest("0x7F >> 1 native", Integer.toHexString(i), "3f");
                i =        b >>> 1;
                doTest("0x7F >>> 1 native", Integer.toHexString(i), "3f");
                i = assembleByte(b,0xFF,-1,(byte)0,0,0);
                doTest("assembleByte", Integer.toHexString(i), "3f");

                b = (byte)0x80;
                i =        b >> 1;
                doTest("0x80 >> 1 native", Integer.toHexString(i), "ffffffc0");
                i =        b >>> 1;
                doTest("0x80 >>> 1 native", Integer.toHexString(i), "7fffffc0");
                i = assembleByte(b,0xFF,-1,(byte)0,0,0);
                doTest("assembleByte", Integer.toHexString(i), "40");


                String strIn        = "";

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- encode");
                System.out.println("--");
                System.out.println();
                //-------------------------------------------------------------

                strIn = "AAA";
                doTest(strIn,
                       Base64.encode(strIn),
                       "QUFB");
                strIn = "AAAA";
                doTest(strIn,
                       Base64.encode(strIn),
                       "QUFBQQ==");
                strIn = "AAAAA";
                doTest(strIn,
                       Base64.encode(strIn),
                       "QUFBQUE=");
                strIn = "AAAAAA";
                doTest(strIn,
                       Base64.encode(strIn),
                       "QUFBQUFB");

                char ch65  = (char)65;
                char ch0   = (char)0;
                char ch127 = (char)127;
                char ch128 = (char)128;
                char ch255 = (char)255;
                char ch256 = (char)256;

                System.out.println("Encoding string with char 65");
                strIn = "A" + ch65 + "A";
                doTest(strIn,
                       Base64.encode(strIn),
                       "QUFB");

                System.out.println("Encoding string with char 0");
                strIn = "A" + ch0 + "A";
                doTest(strIn,
                       Base64.encode(strIn),
                       "QQBB");

                System.out.println("Encoding string with char 127");
                strIn = "A" + ch127 + "A";
                doTest(strIn,
                       Base64.encode(strIn),
                       "QX9B");

                System.out.println("Encoding string with char 128");
                strIn = "A" + ch128 + "A";
                doTest(strIn,
                       Base64.encode(strIn),
                       "QYBB");

                System.out.println("Encoding string with char 255");
                strIn = "A" + ch255 + "A";
                doTest(strIn,
                       Base64.encode(strIn),
                       "Qf9B");

                System.out.println("Encoding string with char 256");
                System.out.println("(Java converts char 256 to '?'.)");
                strIn = "A" + ch256 + "A";
                doTest(strIn,
                       Base64.encode(strIn),
                       "QT9B");

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- decode");
                System.out.println("--");
                System.out.println();
                //-------------------------------------------------------------

                strIn = "QUFB";
                doTest(strIn,
                       Base64.decode(strIn),
                       "AAA");
                strIn = "QUFBQQ==";
                doTest(strIn,
                       Base64.decode(strIn),
                       "AAAA");
                strIn = "QUFBQUE=";
                doTest(strIn,
                       Base64.decode(strIn),
                       "AAAAA");
                strIn = "QUFBQUFB";
                doTest(strIn,
                       Base64.decode(strIn),
                       "AAAAAA");

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- decode(encode)");
                System.out.println("--");
                System.out.println();
                //-------------------------------------------------------------

                strIn = "abcdefghijklmnopqrstuvwxyz";
                doTest(strIn + " (" + Base64.encode(strIn) + ")",
                        Base64.decode(Base64.encode(strIn)),
                        strIn);
                
                strIn = "abcdefghijklmnopqrstuvwxy";
                doTest(strIn + " (" + Base64.encode(strIn) + ")",
                        Base64.decode(Base64.encode(strIn)),
                        strIn);
                
                strIn = "abcdefghijklmnopqrstuvwx";
                doTest(strIn + " (" + Base64.encode(strIn) + ")",
                        Base64.decode(Base64.encode(strIn)),
                        strIn);
                
                strIn = "abcdefghijklmnopqrstuvw";
                doTest(strIn + " (" + Base64.encode(strIn) + ")",
                        Base64.decode(Base64.encode(strIn)),
                        strIn);
                
                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- decode exceptions");
                System.out.println("--");
                System.out.println();
                //-------------------------------------------------------------

                System.out.println();
                System.out.println("Encoded string w/non-7-bit ASCII char...");
                String strNot7BitAscii = new Character((char)128).toString();
                try
                {
                    Base64.decode(strNot7BitAscii + "AAA");
                    System.out.println("Failure!  Expected error not detected.");
                }
                catch (InvalidBase64DigitException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                System.out.println();
                System.out.println("Encoded string w/invalid Base64 digit...");
                try
                {
                    Base64.decode("#AAA");
                    System.out.println("Failure!  Expected error not detected.");
                }
                catch (InvalidBase64DigitException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                System.out.println();
                System.out.println
                        ( "Encoded string w/invalid 3rd and last digit before" 
                        + " equals..."
                        );
                try
                {
                    Base64.decode("QUF=");
                    System.out.println("Failure!  Expected error not detected.");
                }
                catch (TruncatedBase64EncodingException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                System.out.println();
                System.out.println
                        ("Encoded string w/invalid 3rd and last digit...");
                try
                {
                    Base64.decode("QUF");
                    System.out.println("Failure!  Expected error not detected.");
                }
                catch (TruncatedBase64EncodingException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                System.out.println();
                System.out.println
                        ( "Encoded string w/invalid 2nd and last digit before"
                        + " two equals..."
                        );
                try
                {
                    Base64.decode("QF==");
                    System.out.println("Failure!  Expected error not detected.");
                }
                catch (TruncatedBase64EncodingException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                System.out.println();
                System.out.println
                        ( "Encoded string w/invalid 2nd and last digit before"
                        + " equals..."
                        );
                try
                {
                    Base64.decode("QF=");
                    System.out.println("Failure!  Expected error not detected.");
                }
                catch (TruncatedBase64EncodingException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                System.out.println();
                System.out.println
                        ("Encoded string w/invalid 2rd and last digit...");
                try
                {
                    Base64.decode("QF");
                    System.out.println("Failure!  Expected error not detected.");
                }
                catch (TruncatedBase64EncodingException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                System.out.println();
                System.out.println
                        ( "Encoded string w/invalid length of 1 before three"
                        + " equals..."
                        );
                try
                {
                    Base64.decode("A===");
                    System.out.println("Failure!  Expected error not detected.");
                }
                catch (TruncatedBase64EncodingException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                System.out.println();
                System.out.println
                        ( "Encoded string w/invalid length of 1 before two"
                        + " equals..."
                        );
                try
                {
                    Base64.decode("A==");
                    System.out.println("Failure!  Expected error not detected.");
                }
                catch (TruncatedBase64EncodingException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                System.out.println();
                System.out.println
                        ( "Encoded string w/invalid length of 1 before"
                        + " equals..."
                        );
                try
                {
                    Base64.decode("A=");
                    System.out.println("Failure!  Expected error not detected.");
                }
                catch (TruncatedBase64EncodingException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                System.out.println();
                System.out.println("Encoded string w/invalid length of 1...");
                try
                {
                    Base64.decode("A");
                    System.out.println("Failure!  Expected error not detected.");
                }
                catch (TruncatedBase64EncodingException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                System.out.println();
                System.out.println("Encoded string w/invalid length of 5...");
                try
                {
                    Base64.decode("AAAAA");
                    System.out.println("Failure!  Expected error not detected.");
                }
                catch (TruncatedBase64EncodingException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                System.out.println();
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
