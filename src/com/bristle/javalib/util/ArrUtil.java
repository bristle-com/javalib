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

// ArrUtil
/******************************************************************************
* This class contains utility routines for manipulating Java arrays.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*       String str1 = ArrUtil.arrayToString(arr1);
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
public class ArrUtil
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
    * Return a string version of the array. 
    *@param  arr        Array to convert to a String.
    *@return            String version of the array, or null if array was null.
    **************************************************************************/
    public static String arrayToString(Object arr[])
    {
        return ObjUtil.castToString(ListUtil.toList(arr));
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
        private static void doTest(String strOut, String strValid)
        {
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

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- arrayToString");
                System.out.println ("--");
                //-------------------------------------------------------------

                String arrIn[];
              
                //-- Null array
                arrIn = null;
                doTest(ArrUtil.arrayToString(arrIn),
                       null);

                //-- Empty array
                arrIn = new String[0];
                doTest(ArrUtil.arrayToString(arrIn),
                       "[]");

                //-- Array containing one null element.
                arrIn = new String[1];
                arrIn[0] = null;
                doTest(ArrUtil.arrayToString(arrIn),
                       "[null]");

                //-- Array containing two null elements.
                arrIn = new String[2];
                arrIn[0] = null;
                arrIn[1] = null;
                doTest(ArrUtil.arrayToString(arrIn),
                       "[null, null]");

                //-- Array with one String element.
                arrIn = new String[1];
                arrIn[0] = "abc";
                doTest(ArrUtil.arrayToString(arrIn),
                       "[abc]");

                //-- Array with some String elements.
                arrIn = new String[2];
                arrIn[0] = "abc";
                arrIn[1] = "def";
                doTest(ArrUtil.arrayToString(arrIn),
                       "[abc, def]");

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
