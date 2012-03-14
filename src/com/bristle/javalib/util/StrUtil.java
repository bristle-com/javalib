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

import java.util.StringTokenizer;

// StrUtil
/******************************************************************************
* This class contains utility routines for manipulating Java Strings.
*<pre>
*<b>Usage:</b>
*   - Typical scenarios for using this class are:
*     - Put quotes around comma-separated substrings.
*       String strOut = StrUtil.quoteDelimitedSubstrings(strIn, ',', "'");
*     - Put spaces after commas.
*       String strOut = StrUtil.insertAfterDelimiters(strIn, ',', " ");
*     - Create a string of 5 stars (*):
*       String strOut = StrUtil.makeStringOfChars('*', 5);
*     - Pad string on left with zeros to a total length of 10 chars.
*       String strOut = StrUtil.lpad(strIn, '0', 10);
*     - Pad string on right with blanks to a total length of 7 chars.
*       String strOut = StrUtil.rpad(strIn, ' ', 7);
*     - Get the leftmost 7 chars:
*       String strOut = StrUtil.left(strIn, 7);
*     - Get the rightmost 7 chars:
*       String strOut = StrUtil.right(strIn, 7);
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
public class StrUtil
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
    * Return true if the specified String is null or empty; false otherwise. 
    *@param  strIn      String to compare.
    *@return            true if equal or both null or empty; false otherwise.
    **************************************************************************/
    public static boolean isNullOrEmpty(String strIn)
    {
        return (strIn == null || strIn.equals(""));
    }

    /**************************************************************************
    * Return true if the specified Strings are equal or if both of them are 
    * null or empty; false otherwise. 
    *@param  str1       String to compare.
    *@param  str2       String to compare.
    *@return            true if equal or both null or empty; false otherwise.
    **************************************************************************/
    public static boolean equalsOrBothNullOrEmpty(String str1, String str2)
    {
        if (isNullOrEmpty(str1) != isNullOrEmpty(str2))
        {
            // One is null or empty and the other is not.
            return false;
        }

        // Both are null or empty, or both are non-null and non-empty.
        return (isNullOrEmpty(str1) || str1.equals(str2));
    }

    /**************************************************************************
    * Return true if the specified Strings are equalsIgnoreCase() or if both 
    * of them are null or empty; false otherwise. 
    *@param  str1       String to compare.
    *@param  str2       String to compare.
    *@return            true if equal or both null or empty; false otherwise.
    **************************************************************************/
    public static boolean equalsIgnoreCaseOrBothNullOrEmpty
                                                    (String str1, String str2)
    {
        if (isNullOrEmpty(str1) != isNullOrEmpty(str2))
        {
            // One is null or empty and the other is not.
            return false;
        }

        // Both are null or empty, or both are non-null and non-empty.
        return (isNullOrEmpty(str1) || str1.equalsIgnoreCase(str2));
    }

    /**************************************************************************
    * Return true if the specified Strings are equal or both null; false 
    * otherwise. 
    *@param  str1       String to compare.
    *@param  str2       String to compare.
    *@return            true if equal or both null; false otherwise.
    **************************************************************************/
    public static boolean equalsIgnoreCaseOrBothNull(String str1, String str2)
    {
        if ((str1 == null) != (str2 == null))
        {
            // One is null and the other is not.
            return false;
        }
        // Both are null, or both are non-null.
        return (str1 == null || str1.equalsIgnoreCase(str2));
    }

    /**************************************************************************
    * Returns null if the specified string, when trimmed, is null or empty.
    * Otherwise, returns the specified string.
    *@param  strIn      The input string.
    *@return            Null or the value of strIn.
    **************************************************************************/
    public static String mapEmptyToNull(String strIn)
    {
        if (strIn == null || strIn.trim().equals(""))
        {
            return null;
        }
        return strIn;
    }

    /**************************************************************************
    * Return a String that is a copy of strIn with all occurrences of strFrom 
    * replaced with strTo.  If strFrom is the empty string, return strIn.
    * Note:  The advantage of this over the native Java String.replaceAll() is
    *        that it does not try to interpret strFrom as a regular expression.
    *@param  strIn       String to start with.
    *@param  strFrom     String to change from.
    *@param  strTo       String to change to.
    *@return             Copy of strIn with updates.
    **************************************************************************/
    public static String replaceAll
                                (String strIn, String strFrom, String strTo)
    {
        //-- Note:  Beware an empty strFrom.  Without this test, the loop
        //--        below would be infinite.
        int intFromLength = strFrom.length();
        if (intFromLength <= 0)
        {
            return strIn;
        }

        StringBuffer sbRC = new StringBuffer();
        int intIndex = 0;
        int intLength = strIn.length();
        while (intIndex < intLength)
        {
            //-- Find the next occurrence of strFrom in strIn.
            int intMatchPos = strIn.indexOf(strFrom, intIndex);
            if (intMatchPos > -1)
            {
                //-- Copy the chars we just scanned past.
                sbRC.append(strIn.substring(intIndex, intMatchPos));

                //-- Copy strTo instead of the matched strFrom.
                sbRC.append(strTo);

                //-- Advance past the matched strFrom.
                intIndex = intMatchPos + intFromLength;
            }
            else
            {
                //-- No more matches.  Copy the remaining chars, and 
                //-- advance to the end of the string.
                sbRC.append(strIn.substring(intIndex, intLength));
                intIndex = intLength;
            }
        }

        return sbRC.toString();
    }

    /**************************************************************************
    * Return a String that is a copy of strIn with the first occurrences, if 
    * any, of strFrom replaced with strTo.  If strFrom is the empty string, 
    * return strIn.
    * Note:  The advantage of this over the native Java String.replaceFirst() is
    *        that it does not try to interpret strFrom as a regular expression.
    *@param  strIn       String to start with.
    *@param  strFrom     String to change from.
    *@param  strTo       String to change to.
    *@return             Copy of strIn with updates.
    **************************************************************************/
    public static String replaceFirst
                                (String strIn, String strFrom, String strTo)
    {
        int intFromLength = strFrom.length();
        if (intFromLength <= 0)
        {
            return strIn;
        }

        StringBuffer sbRC = new StringBuffer();

        //-- Find the next occurrence of strFrom in strIn.
        int intMatchPos = strIn.indexOf(strFrom);
        if (intMatchPos > -1)
        {
            //-- Copy the chars we just scanned past.
            sbRC.append(strIn.substring(0, intMatchPos));

            //-- Copy strTo instead of the matched strFrom.
            sbRC.append(strTo);

            //-- Advance past the matched strFrom.
            sbRC.append(strIn.substring(intMatchPos + intFromLength));
            return sbRC.toString();
        }
        else
        {
            return strIn;
        }
    }

    /**************************************************************************
    * Return a string that is a copy of strString with strQuote inserted
    * before and after each substring that is delimited by chDelim.
    *@param  strString  String to search for delimited substrings.
    *@param  chDelim    Delimiter character.
    *@param  strQuote   String to insert before and after each substring.
    *@return            String with quotes inserted.
    **************************************************************************/
    public static String quoteDelimitedSubstrings
                        (String strString, char chDelim, String strQuote)
    {
        String strDelim = String.valueOf(chDelim);
        boolean blnFirstTime = true;
        String strRC = "";
        StringTokenizer st = new StringTokenizer(strString, strDelim);
        while (st.hasMoreTokens())
        {
            strRC += (blnFirstTime ? "" : strDelim)
                  + strQuote + st.nextToken() + strQuote;
            blnFirstTime = false;
        }
        return strRC;
    }

    /**************************************************************************
    * Return a string that is a copy of strString with strInsert inserted
    * after each occurrence of chDelim.
    *@param  strString  String to search for delimited substrings.
    *@param  chDelim    Delimiter character.
    *@param  strInsert  String to insert after each delimiter.
    *@return            String with insertions.
    **************************************************************************/
    public static String insertAfterDelimiters
                        (String strString, char chDelim, String strInsert)
    {
        String strDelim = String.valueOf(chDelim);
        return replaceAll
                        (strString,
                         strDelim,
                         strDelim + strInsert);
    }

    /**************************************************************************
    * Return a string containing intLength occurrences of the character chChar.
    * If intLength is zero or negative, return the empty string ("");
    *@param  chChar     Character to put in the string.
    *@param  intLength  Desired length of the result string.
    *@return            Generated string.
    **************************************************************************/
    public static String makeStringOfChars(char chChar, int intLength)
    {
        if (intLength <= 0) 
        {
            return "";
        }
        char ach[] = new char[intLength];
        for (int i=0; i<intLength; i++)
        {
            ach[i] = chChar;
        }
        return new String(ach);
    }

    /**************************************************************************
    * Left pad a string.  
    * Return a string that is a copy of strString preceded by enough copies of 
    * chPad to make the length of the resulting string equal intLength.  If
    * strString is already too long, return it unchanged.
    *@param  strString  String to pad.
    *@param  chPad      Character to pad with.
    *@param  intLength  Desired length of the result string.
    *@return            Padded string.
    **************************************************************************/
    public static String lpad(String strString, char chPad, int intLength)
    {
        return StrUtil.makeStringOfChars(chPad, intLength - strString.length()) 
             + strString;
    }

    /**************************************************************************
    * Right pad a string.  
    * Return a string that is a copy of strString followed by enough copies of 
    * chPad to make the length of the resulting string equal intLength.  If
    * strString is already too long, return it unchanged.
    *@param  strString  String to pad.
    *@param  chPad      Character to pad with.
    *@param  intLength  Desired length of the result string.
    *@return            Padded string.
    **************************************************************************/
    public static String rpad(String strString, char chPad, int intLength)
    {
        return strString
             + StrUtil.makeStringOfChars(chPad, intLength - strString.length());
    }

    /**************************************************************************
    * Left trim a string.  
    * Return a string that is a copy of strString with all consecutive leading  
    * occurrences of the specified substring removed.
    *@param  strString  String to trim.
    *@param  strMatch   Substring to remove.
    *@return            Padded string.
    **************************************************************************/
    public static String ltrim(String strString, String strMatch)
    {
        int intStringLen = strString.length();
        int intMatchLen  = strMatch.length();
        if (intStringLen == 0 || intMatchLen == 0 || intMatchLen > intStringLen)
        {
            return strString;
        }
        int intTrimAt    = 0;
        int intBegin     = 0;
        int intEnd       = intMatchLen;
        while (   intEnd <= intStringLen 
               && strString.substring(intBegin, intEnd).equals(strMatch))
        {
            intTrimAt = intEnd;
            intBegin  += intMatchLen;
            intEnd    += intMatchLen;
        }
        if (intTrimAt > 0)
        {
            strString = strString.substring(intTrimAt);
        }
        return strString;
    }

    /**************************************************************************
    * Right trim a string.  
    * Return a string that is a copy of strString with all consecutive trailing 
    * occurrences of the specified substring removed.
    *@param  strString  String to trim.
    *@param  strMatch   Substring to remove.
    *@return            Padded string.
    **************************************************************************/
    public static String rtrim(String strString, String strMatch)
    {
        int intStringLen = strString.length();
        int intMatchLen  = strMatch.length();
        if (intStringLen == 0 || intMatchLen == 0 || intMatchLen > intStringLen)
        {
            return strString;
        }
        int intTrimAt    = intStringLen;
        int intBegin     = intStringLen - intMatchLen;
        int intEnd       = intStringLen;
        while (   intBegin >= 0 
               && strString.substring(intBegin, intEnd).equals(strMatch))
        {
            intTrimAt = intBegin;
            intBegin  -= intMatchLen;
            intEnd    -= intMatchLen;
        }
        if (intTrimAt < intStringLen)
        {
            strString = strString.substring(0, intTrimAt);
        }
        return strString;
    }

    /**************************************************************************
    * Returns a string containing the specified number of chars copied from 
    * the beginning of a string, or less if the specified string is too short.
    *@param  strString   String to get substring from.
    *@param  intLength   Max length of the substring to return.  Negative 
    *                    values rounded up to zero.
    *@return             Substring.
    **************************************************************************/
    public static String left(String strString, int intLength)
    {
        int intEndPos = Math.min (strString.length(), 
                                  Math.max (0, intLength));
        return strString.substring(0, intEndPos);
    }

    /**************************************************************************
    * Returns a string containing the specified number of chars copied from 
    * the end of a string, or less if the specified string is too short.
    *@param  strString   String to get substring from.
    *@param  intLength   Max length of the substring to return.  Negative 
    *                    values rounded up to zero.
    *@return             Substring.
    **************************************************************************/
    public static String right(String strString, int intLength)
    {
        int intAdjustedLength = Math.min (strString.length(), 
                                          Math.max (0, intLength));
        int intStartPos = strString.length() - intAdjustedLength;
        return strString.substring(intStartPos);
    }

    /**************************************************************************
    * Returns the specified string with commas inserted every 3 chars counting 
    * from the right -- the traditional formatting of a large number, broken 
    * into ones, thousands, millions, etc.
    *@param  strString   String to insert commas into.
    *@return             String with commas.
    **************************************************************************/
    public static String insertCommas(String strString)
    {
        StringBuffer sb = new StringBuffer(strString);
        for (int i = sb.length() - 3; i > 0; i -= 3)
        {
             sb.insert(i, ',');
        }
        return sb.toString();
    }

    /**************************************************************************
    * Return true if strString contains any of the chars in strChars; false 
    * otherwise.
    *@param  strString   String to search.
    *@param  strChars    String of chars to search for.
    *@return             true if strIn contains any of the chars in strChars; 
    *                    false otherwise.
    **************************************************************************/
    public static boolean containsAnyChar(String strString, String strChars)
    {
        // Loop through the specified chars, searching strString for each char.
        // Stop at the first match.
        if (strString == null || strChars == null) return false;
        for (int i = 0; i < strChars.length(); i++)
        {
            if (strString.indexOf(strChars.charAt(i)) >= 0)
            {
                return true;
            }
        }
        return false;
    }
    
    /**************************************************************************
    * Return true if strString contains any char other than those in strChars; 
    * false otherwise.
    *@param  strString   String to search.
    *@param  strChars    String of chars to search for.
    *@return             true if strString contains any char other than those 
    *                    in strChars; false otherwise.
    **************************************************************************/
    public static boolean containsAnyOtherChar(String strString, String strChars)
    {
        // Loop through strString, searching strChars for each char.
        // Stop at the first non-match.
        if (strString == null || strChars == null) return false;
        for (int i = 0; i < strString.length(); i++)
        {
            if (strChars.indexOf(strString.charAt(i)) < 0)
            {
                return true;
            }
        }
        return false;
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
        private static void doTest
                (String strIn, boolean blnOut, boolean blnValid)
        {
            System.out.println ("Input: " + strIn);
            System.out.println ("Output should be: " + blnValid);
            System.out.println ("Output is:        " + blnOut);
            System.out.println (blnOut == blnValid 
                                ? "Success!" 
                                : "Failure!");
        }

        private static void doTest
                (String str1, String str2, boolean blnOut, boolean blnValid)
        {
            System.out.println ("Input 1: " + str1);
            System.out.println ("Input 2: " + str2);
            System.out.println ("Output should be: " + blnValid);
            System.out.println ("Output is:        " + blnOut);
            System.out.println (blnOut == blnValid 
                                ? "Success!" 
                                : "Failure!");
        }

        private static void doTest
                (String strIn, String strOut, String strValid)
        {
            System.out.println ("Input: " + strIn);
            System.out.println ("Output should be: " + strValid);
            System.out.println ("Output is:        " + strOut);
            System.out.println (ObjUtil.equalsOrBothNull(strOut, strValid)
                                ? "Success!" 
                                : "Failure!");
        }

        private static void doTest
                (String str1, String str2, String strOut, String strValid)
        {
            System.out.println ("Input 1: " + str1);
            System.out.println ("Input 2: " + str2);
            System.out.println ("Output should be: " + strValid);
            System.out.println ("Output is:        " + strOut);
            System.out.println (ObjUtil.equalsOrBothNull(strOut, strValid)
                                ? "Success!" 
                                : "Failure!");
        }

        /**********************************************************************
        * Main testing method.
        *@param  args       Array of command line argument strings
        **********************************************************************/
        public static void main(String[] args)
        {
            try
            {
                System.out.println ("Begin tests...");

                String strIn = "";
                String str1 = "";
                String str2 = "";

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- isNullorEmpty");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Null
                strIn = null;
                doTest(strIn, StrUtil.isNullOrEmpty(strIn), true);

                //-- Empty
                strIn = "";
                doTest(strIn, StrUtil.isNullOrEmpty(strIn), true);

                //-- Non-empty
                strIn = "abc";
                doTest(strIn, StrUtil.isNullOrEmpty(strIn), false);

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- equalsOrBothNullOrEmpty");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- First null, second null
                str1 = null;
                str2 = null;
                doTest(str1, 
                       str2, 
                       StrUtil.equalsOrBothNullOrEmpty(str1, str2), 
                       true);

                //-- First null, second empty
                str1 = null;
                str2 = new String("");
                doTest(str1, 
                       str2, 
                       StrUtil.equalsOrBothNullOrEmpty(str1, str2), 
                       true);

                //-- First null, second non-empty
                str1 = null;
                str2 = new String("abc");
                doTest(str1, 
                       str2, 
                       StrUtil.equalsOrBothNullOrEmpty(str1, str2), 
                       false);

                //-- First empty, second null
                str1 = new String("");
                str2 = null; 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsOrBothNullOrEmpty(str1, str2), 
                       true);

                //-- First empty, second empty
                str1 = new String("");
                str2 = new String("");
                doTest(str1, 
                       str2, 
                       StrUtil.equalsOrBothNullOrEmpty(str1, str2), 
                       true);

                //-- First empty, second non-empty
                str1 = new String("");
                str2 = new String("abc");
                doTest(str1, 
                       str2, 
                       StrUtil.equalsOrBothNullOrEmpty(str1, str2), 
                       false);

                //-- First non-empty, second null
                str1 = new String("abc");
                str2 = null; 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsOrBothNullOrEmpty(str1, str2), 
                       false);

                //-- First non-empty, second empty
                str1 = new String("abc");
                str2 = new String(""); 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsOrBothNullOrEmpty(str1, str2), 
                       false);

                //-- Same object
                str1 = new String("abc");
                str2 = str1; 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsOrBothNullOrEmpty(str1, str2), 
                       true);

                //-- Different object, but same value
                str1 = new String("abc");
                str2 = new String("abc"); 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsOrBothNullOrEmpty(str1, str2), 
                       true);

                //-- Different values
                str1 = new String("abc");
                str2 = new String("def"); 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsOrBothNullOrEmpty(str1, str2), 
                       false);

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- equalsIgnoreCaseOrBothNullOrEmpty");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- First null, second null
                str1 = null;
                str2 = null;
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNullOrEmpty(str1, str2), 
                       true);

                //-- First null, second empty
                str1 = null;
                str2 = new String("");
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNullOrEmpty(str1, str2), 
                       true);

                //-- First null, second non-empty
                str1 = null;
                str2 = new String("abc");
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNullOrEmpty(str1, str2), 
                       false);

                //-- First empty, second null
                str1 = new String("");
                str2 = null; 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNullOrEmpty(str1, str2), 
                       true);

                //-- First empty, second empty
                str1 = new String("");
                str2 = new String("");
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNullOrEmpty(str1, str2), 
                       true);

                //-- First empty, second non-empty
                str1 = new String("");
                str2 = new String("abc");
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNullOrEmpty(str1, str2), 
                       false);

                //-- First non-empty, second null
                str1 = new String("abc");
                str2 = null; 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNullOrEmpty(str1, str2), 
                       false);

                //-- First non-empty, second empty
                str1 = new String("abc");
                str2 = new String(""); 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNullOrEmpty(str1, str2), 
                       false);

                //-- Same object
                str1 = new String("abc");
                str2 = str1; 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNullOrEmpty(str1, str2), 
                       true);

                //-- Different object, but same value
                str1 = new String("abc");
                str2 = new String("abc"); 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNullOrEmpty(str1, str2), 
                       true);

                //-- Values differ only in case
                str1 = new String("abc");
                str2 = new String("ABC"); 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNullOrEmpty(str1, str2), 
                       true);

                //-- Different values
                str1 = new String("abc");
                str2 = new String("def"); 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNullOrEmpty(str1, str2), 
                       false);

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- equalsIgnoreCaseOrBothNull");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- First null, second null
                str1 = null;
                str2 = null;
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNull(str1, str2), 
                       true);

                //-- First null, second empty
                str1 = null;
                str2 = new String("");
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNull(str1, str2), 
                       false);

                //-- First null, second non-empty
                str1 = null;
                str2 = new String("abc");
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNull(str1, str2), 
                       false);

                //-- First empty, second null
                str1 = new String("");
                str2 = null; 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNull(str1, str2), 
                       false);

                //-- First empty, second empty
                str1 = new String("");
                str2 = new String("");
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNull(str1, str2), 
                       true);

                //-- First empty, second non-empty
                str1 = new String("");
                str2 = new String("abc");
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNull(str1, str2), 
                       false);

                //-- First non-empty, second null
                str1 = new String("abc");
                str2 = null; 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNull(str1, str2), 
                       false);

                //-- First non-empty, second empty
                str1 = new String("abc");
                str2 = new String(""); 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNull(str1, str2), 
                       false);

                //-- Same object
                str1 = new String("abc");
                str2 = str1; 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNull(str1, str2), 
                       true);

                //-- Different object, but same value
                str1 = new String("abc");
                str2 = new String("abc"); 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNull(str1, str2), 
                       true);

                //-- Values differ only in case
                str1 = new String("abc");
                str2 = new String("ABC"); 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNull(str1, str2), 
                       true);

                //-- Different values
                str1 = new String("abc");
                str2 = new String("def"); 
                doTest(str1, 
                       str2, 
                       StrUtil.equalsIgnoreCaseOrBothNull(str1, str2), 
                       false);

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- mapEmptyToNull");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Basic test.
                strIn = "abc";
                doTest(strIn,
                       StrUtil.mapEmptyToNull(strIn),
                       "abc");

                //-- Empty string.
                strIn = "";
                doTest(strIn,
                       StrUtil.mapEmptyToNull(strIn),
                       null);

                //-- Null string
                strIn = null;
                doTest(strIn,
                       StrUtil.mapEmptyToNull(strIn),
                       null);

                //-- Spaces only.
                strIn = "  ";
                doTest(strIn,
                       StrUtil.mapEmptyToNull(strIn),
                       null);

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- replaceAll");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Basic test.
                strIn = "axbxc";
                doTest(strIn,
                       StrUtil.replaceAll(strIn, "x", "_"),
                       "a_b_c");

                //-- Empty string.
                strIn = "";
                doTest(strIn,
                       StrUtil.replaceAll(strIn, "x", "_"),
                       "");

                //-- No matches.
                strIn = "abc";
                doTest(strIn,
                       StrUtil.replaceAll(strIn, "x", "_"),
                       "abc");

                //-- Leading match.
                strIn = "xabc";
                doTest(strIn,
                       StrUtil.replaceAll(strIn, "x", "_"),
                       "_abc");

                //-- Trailing match.
                strIn = "abcx";
                doTest(strIn,
                       StrUtil.replaceAll(strIn, "x", "_"),
                       "abc_");

                //-- Single char, which is the match.
                strIn = "x";
                doTest(strIn,
                       StrUtil.replaceAll(strIn, "x", "_"),
                       "_");

                //-- Replace with same pattern but longer.
                strIn = "axbxc";
                doTest(strIn,
                       StrUtil.replaceAll(strIn, "x", "xx"),
                       "axxbxxc");

                //-- Replace with shorter pattern.
                strIn = "axxbxxc";
                doTest(strIn,
                       StrUtil.replaceAll(strIn, "xx", "_"),
                       "a_b_c");

                //-- Replace with same pattern but shorter.
                strIn = "axxbxxc";
                doTest(strIn,
                       StrUtil.replaceAll(strIn, "xx", "x"),
                       "axbxc");

                //-- Replace with same pattern but even shorter.
                strIn = "axxxbxxxc";
                doTest(strIn,
                       StrUtil.replaceAll(strIn, "xxx", "x"),
                       "axbxc");

                //-- Replace apostrophes with double apostrophes.
                strIn = "a'b'c";
                doTest(strIn,
                       StrUtil.replaceAll(strIn, "'", "''"),
                       "a''b''c");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- replaceFirst");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Basic test.
                strIn = "axbxc";
                doTest(strIn,
                       StrUtil.replaceFirst(strIn, "x", "_"),
                       "a_bxc");

                //-- Empty string.
                strIn = "";
                doTest(strIn,
                       StrUtil.replaceFirst(strIn, "x", "_"),
                       "");

                //-- No matches.
                strIn = "abc";
                doTest(strIn,
                       StrUtil.replaceFirst(strIn, "x", "_"),
                       "abc");

                //-- Leading match.
                strIn = "xabc";
                doTest(strIn,
                       StrUtil.replaceFirst(strIn, "x", "_"),
                       "_abc");

                //-- Trailing match.
                strIn = "abcx";
                doTest(strIn,
                       StrUtil.replaceFirst(strIn, "x", "_"),
                       "abc_");

                //-- Single char, which is the match.
                strIn = "x";
                doTest(strIn,
                       StrUtil.replaceFirst(strIn, "x", "_"),
                       "_");

                //-- Replace with same pattern but longer.
                strIn = "axb";
                doTest(strIn,
                       StrUtil.replaceFirst(strIn, "x", "xx"),
                       "axxb");

                //-- Replace with shorter pattern.
                strIn = "axxb";
                doTest(strIn,
                       StrUtil.replaceFirst(strIn, "xx", "_"),
                       "a_b");

                //-- Replace with same pattern but shorter.
                strIn = "axxb";
                doTest(strIn,
                       StrUtil.replaceFirst(strIn, "xx", "x"),
                       "axb");

                //-- Replace with same pattern but even shorter.
                strIn = "axxxb";
                doTest(strIn,
                       StrUtil.replaceFirst(strIn, "xxx", "x"),
                       "axb");

                //-- Replace apostrophes with double apostrophes.
                strIn = "a'b";
                doTest(strIn,
                       StrUtil.replaceFirst(strIn, "'", "''"),
                       "a''b");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- quoteDelimitedSubstrings");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Basic test.
                strIn = "abc,def,ghij,k";
                doTest(strIn,
                       StrUtil.quoteDelimitedSubstrings(strIn, ',', "'"),
                       "'abc','def','ghij','k'");

                //-- Only one substring.
                strIn = "abc";
                doTest(strIn,
                       StrUtil.quoteDelimitedSubstrings(strIn, ',', "'"),
                       "'abc'");

                //-- Empty string.
                strIn = "";
                doTest(strIn,
                       StrUtil.quoteDelimitedSubstrings(strIn, ',', "'"),
                       "");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- insertAfterDelimiters");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Basic test.
                strIn = "a,b,c";
                doTest(strIn,
                       StrUtil.insertAfterDelimiters(strIn, ',', " "),
                       "a, b, c");

                //-- Empty string.
                strIn = "";
                doTest(strIn,
                       StrUtil.insertAfterDelimiters(strIn, ',', " "),
                       "");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- lpad");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Basic test.
                strIn = "12345";
                doTest(strIn,
                       StrUtil.lpad(strIn, '0', 10),
                       "0000012345");

                //-- Already correct length.
                strIn = "12345";
                doTest(strIn,
                       StrUtil.lpad(strIn, '0', 5),
                       "12345");

                //-- Already too long.
                strIn = "12345";
                doTest(strIn,
                       StrUtil.lpad(strIn, '0', 4),
                       "12345");

                //-- Pad to zero length.
                strIn = "12345";
                doTest(strIn,
                       StrUtil.lpad(strIn, '0', 0),
                       "12345");

                //-- Empty string.
                strIn = "";
                doTest(strIn,
                       StrUtil.lpad(strIn, '0', 10),
                       "0000000000");

                //-- Pad zero length to zero length.
                strIn = "";
                doTest(strIn,
                       StrUtil.lpad(strIn, '0', 0),
                       "");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- rpad");
                System.out.println ("--");
                //-------------------------------------------------------------
 
                //-- Basic test.
                strIn = "12345";
                doTest(strIn,
                       StrUtil.rpad(strIn, '0', 10),
                       "1234500000");

                //-- Already correct length.
                strIn = "12345";
                doTest(strIn,
                       StrUtil.rpad(strIn, '0', 5),
                       "12345");

                //-- Already too long.
                strIn = "12345";
                doTest(strIn,
                       StrUtil.rpad(strIn, '0', 4),
                       "12345");

                //-- Pad to zero length.
                strIn = "12345";
                doTest(strIn,
                       StrUtil.rpad(strIn, '0', 0),
                       "12345");

                //-- Empty string.
                strIn = "";
                doTest(strIn,
                       StrUtil.rpad(strIn, '0', 10),
                       "0000000000");

                //-- Pad zero length to zero length.
                strIn = "";
                doTest(strIn,
                       StrUtil.rpad(strIn, '0', 0),
                       "");

                //-- Basic test with letters.
                strIn = "aaa";
                doTest(strIn,
                       StrUtil.rpad(strIn, '*', 5),
                       "aaa**");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- ltrim");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Empty string, empty match
                str1 = "";
                str2 = "";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "");

                //-- Empty string, non-empty match
                str1 = "";
                str2 = "x";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "");

                //-- Non-empty string, empty match
                str1 = "x";
                str2 = "";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "x");

                //-- Match too long
                str1 = "x";
                str2 = "abc";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "x");

                //-- Single char, no match.
                str1 = "12345";
                str2 = "6";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "12345");

                //-- Multiple char, no match.
                str1 = "12345";
                str2 = "65";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "12345");

                //-- Single char, single match.
                str1 = "12345";
                str2 = "1";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "2345");

                //-- Multiple char, single match.
                str1 = "12345";
                str2 = "12";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "345");

                //-- Single char, multiple match.
                str1 = "666612345";
                str2 = "6";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "12345");

                //-- Multiple char, multiple match.
                str1 = "65651234";
                str2 = "65";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "1234");

                //-- Single char, all match.
                str1 = "5555";
                str2 = "5";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "");

                //-- Multiple char, all one match.
                str1 = "12345";
                str2 = "12345";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "");

                //-- Multiple char, all multiple match.
                str1 = "12345123451234512345";
                str2 = "12345";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "");

                //-- Match longer than remainder.
                str1 = "12345abc";
                str2 = "12345";
                doTest(str1,
                       str2,
                       StrUtil.ltrim(str1, str2),
                       "abc");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- rtrim");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Empty string, empty match
                str1 = "";
                str2 = "";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "");

                //-- Empty string, non-empty match
                str1 = "";
                str2 = "x";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "");

                //-- Non-empty string, empty match
                str1 = "x";
                str2 = "";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "x");

                //-- Match too long
                str1 = "x";
                str2 = "abc";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "x");

                //-- Single char, no match.
                str1 = "12345";
                str2 = "6";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "12345");

                //-- Multiple char, no match.
                str1 = "12345";
                str2 = "65";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "12345");

                //-- Single char, single match.
                str1 = "12345";
                str2 = "5";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "1234");

                //-- Multiple char, single match.
                str1 = "12345";
                str2 = "45";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "123");

                //-- Single char, multiple match.
                str1 = "123456666";
                str2 = "6";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "12345");

                //-- Multiple char, multiple match.
                str1 = "12346565";
                str2 = "65";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "1234");

                //-- Single char, all match.
                str1 = "5555";
                str2 = "5";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "");

                //-- Multiple char, all one match.
                str1 = "12345";
                str2 = "12345";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "");

                //-- Multiple char, all multiple match.
                str1 = "12345123451234512345";
                str2 = "12345";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "");

                //-- Match longer than remainder.
                str1 = "abc12345";
                str2 = "12345";
                doTest(str1,
                       str2,
                       StrUtil.rtrim(str1, str2),
                       "abc");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- left");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Basic test.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.left(strIn, 7),
                       "1234567");

                //-- Request too many chars.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.left(strIn, 12),
                       "1234567890");

                //-- Request one too many chars.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.left(strIn, 11),
                       "1234567890");

                //-- Request all chars.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.left(strIn, 10),
                       "1234567890");

                //-- Request one less than all chars.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.left(strIn, 9),
                       "123456789");

                //-- Request zero chars.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.left(strIn, 0),
                       "");

                //-- Request negative number of chars.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.left(strIn, -5),
                       "");

                //-- Request from zero-length string.
                strIn = "";
                doTest(strIn,
                       StrUtil.left(strIn, 9),
                       "");

                //-- Request zero chars from zero-length string.
                strIn = "";
                doTest(strIn,
                       StrUtil.left(strIn, 0),
                       "");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- right");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Basic test.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.right(strIn, 7),
                       "4567890");

                //-- Request too many chars.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.right(strIn, 12),
                       "1234567890");

                //-- Request one too many chars.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.right(strIn, 11),
                       "1234567890");

                //-- Request all chars.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.right(strIn, 10),
                       "1234567890");

                //-- Request one less than all chars.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.right(strIn, 9),
                       "234567890");

                //-- Request zero chars.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.right(strIn, 0),
                       "");

                //-- Request negative number of chars.
                strIn = "1234567890";
                doTest(strIn,
                       StrUtil.right(strIn, -5),
                       "");

                //-- Request from zero-length string.
                strIn = "";
                doTest(strIn,
                       StrUtil.right(strIn, 9),
                       "");

                //-- Request zero chars from zero-length string.
                strIn = "";
                doTest(strIn,
                       StrUtil.right(strIn, 0),
                       "");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- insertCommas");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Basic test.
                strIn = "1234567890";
                doTest(strIn, StrUtil.insertCommas(strIn), "1,234,567,890");

                //-- 4 digits.
                strIn = "1234";
                doTest(strIn, StrUtil.insertCommas(strIn), "1,234");

                //-- Only 3 digits.
                strIn = "123";
                doTest(strIn, StrUtil.insertCommas(strIn), "123");

                //-- Only 2 digits.
                strIn = "12";
                doTest(strIn, StrUtil.insertCommas(strIn), "12");

                //-- Only 1 digit.
                strIn = "1";
                doTest(strIn, StrUtil.insertCommas(strIn), "1");

                //-- Empty string.
                strIn = "";
                doTest(strIn, StrUtil.insertCommas(strIn), "");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- containsAnyChar");
                System.out.println ("--");
                //-------------------------------------------------------------

                String strString;
                String strChars;
                
                //-- First null, second null
                strString = null;
                strChars = null;
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyChar(strString, strChars), 
                       false);

                //-- First null, second empty
                strString = null;
                strChars = new String("");
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyChar(strString, strChars), 
                       false);

                //-- First null, second non-empty
                strString = null;
                strChars = new String("abc");
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyChar(strString, strChars), 
                       false);

                //-- First empty, second null
                strString = new String("");
                strChars = null; 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyChar(strString, strChars), 
                       false);

                //-- First empty, second empty
                strString = new String("");
                strChars = new String("");
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyChar(strString, strChars), 
                       false);

                //-- First empty, second non-empty
                strString = new String("");
                strChars = new String("abc");
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyChar(strString, strChars), 
                       false);

                //-- First non-empty, second null
                strString = new String("abc");
                strChars = null; 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyChar(strString, strChars), 
                       false);

                //-- First non-empty, second empty
                strString = new String("abc");
                strChars = new String(""); 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyChar(strString, strChars), 
                       false);

                //-- Same object
                strString = new String("abc");
                strChars = strString; 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyChar(strString, strChars), 
                       true);

                //-- Different object, but same value
                strString = new String("abc");
                strChars = new String("abc"); 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyChar(strString, strChars), 
                       true);

                //-- Values differ only in case
                strString = new String("abc");
                strChars = new String("ABC"); 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyChar(strString, strChars), 
                       false);

                //-- Different values
                strString = new String("abc");
                strChars = new String("def"); 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyChar(strString, strChars), 
                       false);

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- containsAnyOtherChar");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- First null, second null
                strString = null;
                strChars = null;
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyOtherChar(strString, strChars), 
                       false);

                //-- First null, second empty
                strString = null;
                strChars = new String("");
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyOtherChar(strString, strChars), 
                       false);

                //-- First null, second non-empty
                strString = null;
                strChars = new String("abc");
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyOtherChar(strString, strChars), 
                       false);

                //-- First empty, second null
                strString = new String("");
                strChars = null; 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyOtherChar(strString, strChars), 
                       false);

                //-- First empty, second empty
                strString = new String("");
                strChars = new String("");
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyOtherChar(strString, strChars), 
                       false);

                //-- First empty, second non-empty
                strString = new String("");
                strChars = new String("abc");
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyOtherChar(strString, strChars), 
                       false);

                //-- First non-empty, second null
                strString = new String("abc");
                strChars = null; 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyOtherChar(strString, strChars), 
                       false);

                //-- First non-empty, second empty
                strString = new String("abc");
                strChars = new String(""); 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyOtherChar(strString, strChars), 
                       false);

                //-- Same object
                strString = new String("abc");
                strChars = strString; 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyOtherChar(strString, strChars), 
                       true);

                //-- Different object, but same value
                strString = new String("abc");
                strChars = new String("abc"); 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyOtherChar(strString, strChars), 
                       false);

                //-- Values differ only in case
                strString = new String("abc");
                strChars = new String("ABC"); 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyOtherChar(strString, strChars), 
                       true);

                //-- Different values
                strString = new String("abc");
                strChars = new String("def"); 
                doTest(strString, 
                       strChars, 
                       StrUtil.containsAnyOtherChar(strString, strChars), 
                       true);

                System.out.println ("...End tests.");
            }
            catch (Throwable e)
            {
                System.out.println("Error in main(): ");
                e.printStackTrace();
            }
        }
    }
}
