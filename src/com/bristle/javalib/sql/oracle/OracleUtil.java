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

package com.bristle.javalib.sql.oracle;

import com.bristle.javalib.util.StrUtil;
import com.bristle.javalib.util.ObjUtil;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

// OracleUtil
/******************************************************************************
* This class contains utility routines for use with an Oracle database.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*       String s = OracleUtil.oracleBoolean(bln);
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
public class OracleUtil
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
    * Returns a string containing the Oracle keyword NULL if the specified 
    * string is null.  Otherwise, returns the specified string with Oracle 
    * quotes (') around it
    *@param  strIn      The input string.
    *@return            The string "NULL" or the value of strIn enclosed in
    *                   quotes as an Oracle string literal.
    **************************************************************************/
    public static String oracleStringOrNull(String strIn)
    {
        if (strIn == null)
        {
            return "NULL";
        }
        final String strORACLE_QUOTE         = "'";
        final String strORACLE_QUOTE_ESCAPED = "''";
        return strORACLE_QUOTE
             + StrUtil.replaceAll
                                (strIn, 
                                 strORACLE_QUOTE, 
                                 strORACLE_QUOTE_ESCAPED)
             + strORACLE_QUOTE;
    }

    /**************************************************************************
    * Returns a string containing the Oracle keyword NULL if the specified 
    * Integer is null.  Otherwise, returns the specified Integer as a string
    * (to be used as an Oracle numeric literal value).
    *@param  intIn      The input Integer.
    *@return            The string "NULL" or the string value of intIn.
    **************************************************************************/
    public static String oracleIntegerOrNull(Integer intIn)
    {
        if (intIn == null)
        {
            return "NULL";
        }
        return intIn.toString();
    }

    /**************************************************************************
    * Returns a string containing the Oracle keyword TRUE if the specified 
    * boolean is null.  Otherwise, returns the Oracle keyword FALSE.
    *@param  blnIn      The input boolean.
    *@return            The stringized value "TRUE" or"FALSE".
    **************************************************************************/
    public static String oracleBoolean(boolean blnIn)
    {
        return (blnIn ? "TRUE" : "FALSE");
    }

    /**********************************************************************
    * Test the specified Oracle database connection.
    *@param  conn       Oracle database connection
    *@return            True if valid; False if invalid.
    **********************************************************************/
    public static boolean databaseConnectionIsValid(Connection conn)
    {
        //-- Try a simple query to make sure the connection is valid.
        Statement st = null;
        ResultSet rs = null;
        try
        {
            st = conn.createStatement();
            rs = st.executeQuery("select sysdate from dual");
            return true;
        }
        catch (Throwable e1)
        {
            //-- Nothing to do.  Suppress all errors, and just return false.  
            return false;
        }
        finally
        {
            //-- Clean up resources, suppressing any errors, regardless of 
            //-- whether the simple query succeeded.
            try
            {
                if (rs != null) { rs.close(); }
            }
            catch (Throwable e2)
            {
                //-- Nothing to do.  Suppress the error.
            }
            try
            {
                if (st != null) { st.close(); }
            }
            catch (Throwable e3)
            {
                //-- Nothing to do.  Suppress the error.
            }
        }
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
                                (Integer intIn, String strOut, String strValid)
        {
            doTest(ObjUtil.castToString(intIn), strOut, strValid);
        }

        private static void doTest
                                (boolean blnIn, String strOut, String strValid)
        {
            doTest(new Boolean(blnIn).toString(), strOut, strValid);
                  //-- Note:  Don't use Boolean.toString() because it doesn't
                  //--        exist in Java 1.3.1.
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

                String strIn = null;
                Integer intIn = null;
                boolean blnIn = false;

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- oracleStringOrNull");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Basic test.
                strIn = "abc";
                doTest(strIn,
                       oracleStringOrNull(strIn),
                       "'abc'");

                //-- Null string
                strIn = null;
                doTest(strIn,
                       oracleStringOrNull(strIn),
                       "NULL");

                //-- Empty string.
                strIn = "";
                doTest(strIn,
                       oracleStringOrNull(strIn),
                       "''");

                //-- Spaces only.
                strIn = "  ";
                doTest(strIn,
                       oracleStringOrNull(strIn),
                       "'  '");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- oracleIntegerOrNull");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Basic test.
                intIn = new Integer(123);
                doTest(intIn,
                       oracleIntegerOrNull(intIn),
                       "123");

                //-- Null Integer
                intIn = null;
                doTest(intIn,
                       oracleIntegerOrNull(intIn),
                       "NULL");

                //-- Zero
                intIn = new Integer(0);
                doTest(intIn,
                       oracleIntegerOrNull(intIn),
                       "0");

                //-- Negative
                intIn = new Integer(-123);
                doTest(intIn,
                       oracleIntegerOrNull(intIn),
                       "-123");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- oracleBoolean");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- True
                blnIn = true;
                doTest(blnIn,
                       oracleBoolean(blnIn),
                       "TRUE");

                //-- False
                blnIn = false;
                doTest(blnIn,
                       oracleBoolean(blnIn),
                       "FALSE");

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
