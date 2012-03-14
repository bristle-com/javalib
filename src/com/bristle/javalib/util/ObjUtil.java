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

import java.text.SimpleDateFormat;
import java.util.Date;

// ObjUtil
/******************************************************************************
* This class contains utility routines for manipulating Java Objects.
*<pre>
*<b>Usage:</b>
*   - Typical scenarios for using this class are:
*       String str1 = ObjUtil.castToString(obj);
*       String str1 = ObjUtil.getClassName(obj);
*       String str1 = ObjUtil.getShortClassName(obj);
*       String str1 = ObjUtil.getPackageName(obj);
*       String str1 = ObjUtil.generateUniqueId(obj);
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
public class ObjUtil
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
    * Return true if the specified objects are equal or both null; false 
    * otherwise. 
    *@param  obj1       Object to compare.
    *@param  obj2       Object to compare.
    *@return            true if equal or both null; false otherwise.
    **************************************************************************/
    public static boolean equalsOrBothNull(Object obj1, Object obj2)
    {
        if ((obj1 == null) != (obj2 == null))
        {
            // One is null and the other is not.
            return false;
        }
        // Both are null, or both are non-null.
        return (obj1 == null || obj1.equals(obj2));
    }

    /**************************************************************************
    * Return the specified object cast to a String, or null if no object was 
    * specified.
    * <pre>
    * This method avoids the following problems that occur at runtime:
    * 1. Can't cast Object to String, if it is something like an Integer:
    *          Object obj1 = new Integer(1);
    *          String dummy1 = (String)obj1;
    * 2. Can't call its toString() method if it is null:
    *          Object obj2 = null;
    *          String dummy2 = obj2.toString()
    * Instead, for any Object, do:
    *          String dummy3 = ObjUtils.castToString(obj1);
    *          String dummy4 = ObjUtils.castToString(obj2);
    * </pre>
    *@param  obj        Object to cast to String.
    *@return            String version of the object.
    **************************************************************************/
    public static String castToString(Object obj)
    {
        return (obj == null)
               ? null
               : obj.toString();
    }

    /**************************************************************************
    * Return a string that is the class name of the specified object, or null 
    * if no object was specified.
    *@param  obj        Object to get the class name of.
    *@return            Class name or null.
    **************************************************************************/
    public static String getClassName(Object obj)
    {
        return (obj == null)
               ? null
               : obj.getClass().getName(); 
    }

    /**************************************************************************
    * Return a string that is the short class name of the specified object
    * (the name without the package prefixes), or null if no object was
    * specified.
    *@param  obj        Object to get the short class name of
    *@return            Short class name or null.
    **************************************************************************/
    public static String getShortClassName(Object obj)
    {
        String strFullClassName = getClassName(obj);
        if (strFullClassName == null) {
            return null;
        }
        int intStartPos = strFullClassName.lastIndexOf('.') + 1;
        return strFullClassName.substring(intStartPos);
    }

    /**************************************************************************
    * Return a string that is the package name of the specified object, with
    * a trailing dot ("."), or the empty string if there is no package, or 
    * null if the specified object is null. 
    *@param  obj        Object to get the package name of.
    *@return            Package name with trailing dot, or "".
    **************************************************************************/
    public static String getPackageName(Object obj)
    {
        if (obj == null)
        {
            return null;
        }
        try
        {
            return obj.getClass().getPackage().getName() + ".";
        }
        catch (NullPointerException e)
        {
            return "";
        }
    }

    /**************************************************************************
    * Generate a unique id based on an Object and the current date and time.
    * The generated format includes a unique number, and the current date and 
    * time as:
    *           unique_DD_MON_YYYY_HH_MM_SS
    *@param  obj    Object used to generate a unique number (its hashcode).
    *@return        Unique id.
    **************************************************************************/
    public static String generateUniqueId(Object obj)
    {
        //-- Note:  The hashCode() value is sufficient to be unique.
        //--        It is the address of the object in memory.  The only 
        //--        exception is when uniqueness is required across allocations
        //--        and deallocations of objects, in which case different
        //--        objects could have the same hashCode() at different times.
        //--        Adding a date/timestamp solves this, and is also useful
        //--        for debugging, reviewing log files, etc.
        return obj.hashCode()
             + "_"
             + (new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss")).format(new Date());
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
                (Object obj1, Object obj2, boolean blnOut, boolean blnValid)
        {
            System.out.println ("Input 1: " + obj1);
            System.out.println ("Input 2: " + obj2);
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
            System.out.println (equalsOrBothNull(strOut, strValid)
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

                Object obj1 = null;
                Object obj2 = null;
                String strIn = "";

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- equalsOrBothNull");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Both null
                obj1 = null;
                obj2 = null;
                doTest(obj1, obj2, ObjUtil.equalsOrBothNull(obj1, obj2), true);

                //-- First null
                obj1 = null;
                obj2 = new String("abc");
                doTest(obj1, obj2, ObjUtil.equalsOrBothNull(obj1, obj2), false);

                //-- Second null
                obj1 = new String("abc");
                obj2 = null; 
                doTest(obj1, obj2, ObjUtil.equalsOrBothNull(obj1, obj2), false);

                //-- Same obect
                obj1 = new String("abc");
                obj2 = obj1; 
                doTest(obj1, obj2, ObjUtil.equalsOrBothNull(obj1, obj2), true);

                //-- Different object, but same value
                obj1 = new String("abc");
                obj2 = new String("abc"); 
                doTest(obj1, obj2, ObjUtil.equalsOrBothNull(obj1, obj2), true);

                //-- Different values
                obj1 = new String("abc");
                obj2 = new String("def"); 
                doTest(obj1, obj2, ObjUtil.equalsOrBothNull(obj1, obj2), false);

                //-- Different Object types
                obj1 = new String("abc");
                obj2 = new Object(); 
                doTest(obj1, obj2, ObjUtil.equalsOrBothNull(obj1, obj2), false);

                //-- Different Object types, order reversed
                obj1 = new Object();
                obj2 = new String("abc");  
                doTest(obj1, obj2, ObjUtil.equalsOrBothNull(obj1, obj2), false);

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- castToString");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Cast a String
                strIn = "abc";
                doTest(strIn,
                       ObjUtil.castToString(strIn),
                       strIn);

                //-- Cast an Integer
                strIn = "123";
                doTest(strIn,
                       ObjUtil.castToString(new Integer(123)),
                       strIn);

                //-- Cast a null
                strIn = null;
                Object obj = null;
                doTest(strIn,
                       ObjUtil.castToString(obj),
                       null);

                //-- Can't cast to String the standard way if it's an Integer.
                System.out.println();
                System.out.println("Casting (String)new Integer(1)...");
                try
                {
                    obj1 = new Integer(1);
                    String dummy1 = (String)obj1;
                    System.out.println("Failure!  Expected error not detected.");
                    //-- Suppress compiler warnings by using the variable.
                    if (dummy1 != null) { dummy1 = null; } 
                }
                catch (ClassCastException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                //-- Can't call toString() if it's null.
                System.out.println();
                System.out.println("Calling toString(null)...");
                try
                {
                    obj2 = null;
                    String dummy2 = obj2.toString();
                    System.out.println("Failure!  Expected error not detected.");
                    //-- Suppress compiler warnings by using the variable.
                    if (dummy2 != null) { dummy2 = null; } 
                }
                catch (NullPointerException e)
                {
                    System.out.println("Caught expected error:");
                    System.out.println("   " + e.getClass().getName());
                    System.out.println("   " + e.getMessage());
                    System.out.println("Success!");
                }

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- getClassName, getShortClassName and " +
                                    "getPackageName");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Get name of String
                String str1 = "";
                doTest(str1,
                       ObjUtil.getClassName(str1),
                       "java.lang.String");
                doTest(str1,
                       ObjUtil.getShortClassName(str1),
                       "String");
                doTest(str1,
                       ObjUtil.getPackageName(str1),
                       "java.lang.");

                //-- Get name of Integer
                Integer int1 = new Integer(123);
                doTest(int1.toString(),
                       ObjUtil.getClassName(int1),
                       "java.lang.Integer");
                doTest(int1.toString(),
                       ObjUtil.getShortClassName(int1),
                       "Integer");
                doTest(int1.toString(),
                       ObjUtil.getPackageName(int1),
                       "java.lang.");

                //-- Get name of null keyword
                doTest("null",
                       ObjUtil.getClassName(null),
                       null);
                doTest("null",
                       ObjUtil.getShortClassName(null),
                       null);
                doTest("null",
                       ObjUtil.getPackageName(null),
                       null);

                //-- Get name of null Object
                Object objNull = null;
                doTest("null Object",
                       ObjUtil.getClassName(objNull),
                       null);
                doTest("null Object",
                       ObjUtil.getShortClassName(objNull),
                       null);
                doTest("null Object",
                       ObjUtil.getPackageName(objNull),
                       null);

                //-- Get name of null String
                String strNull = null;
                doTest("null String",
                       ObjUtil.getClassName(strNull),
                       null);
                doTest("null String",
                       ObjUtil.getShortClassName(strNull),
                       null);
                doTest("null String",
                       ObjUtil.getPackageName(strNull),
                       null);

                //-- Get name of null Integer
                Integer intNull = null;
                doTest("null Integer",
                       ObjUtil.getClassName(intNull),
                       null);
                doTest("null Integer",
                       ObjUtil.getShortClassName(intNull),
                       null);
                doTest("null Integer",
                       ObjUtil.getPackageName(intNull),
                       null);

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
