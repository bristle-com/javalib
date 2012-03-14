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

import java.io.StringWriter;
import java.io.PrintWriter;

// ExcUtil
/******************************************************************************
* This class contains utility routines for manipulating Java Exceptions,
* Errors and Throwables.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*       String str = ExcUtil.getStackTrace(throwable);
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
public class ExcUtil
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
    * Return the stack trace of the specified Throwable as a String.
    *@param  e          Throwable to get the stack trace from.
    *@return            Stack trace as a string.
    **************************************************************************/
    public static String getStackTrace(Throwable e)
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
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
                System.out.println ("Begin tests...");
                try
                {
                    long divisor = 0;
                    long lng = 1/divisor;  //-- Raises divide by zero error.
                    System.out.println("No problem found with value:" + lng);
                }
                catch (Throwable e)
                {
                    System.out.println ("A stack trace for an intentionally"
                                      + " generated 'divide by zero' error");
                    System.out.println ("should appear below:");
                    System.out.println (ExcUtil.getStackTrace(e));
                }
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
