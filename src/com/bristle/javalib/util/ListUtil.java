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

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Arrays;
import java.util.Iterator;

// ListUtil
/******************************************************************************
* This class contains utility routines for manipulating Java Lists.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*       String str1 = ListUtil.listToString(list1);
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
public class ListUtil
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
    * Return a String version of the List. 
    *@param  list       List to convert to a String.
    *@return            String version of the List.
    **************************************************************************/
    public static String listToString(List list)
    {
        return (list == null) ? null : list.toString();
    }

    /**************************************************************************
    * Create a List from the specified array.
    * Note:  Same as Arrays.asList, except it tolerates null, returning null.
    *@param  arr        Array of Objects to put in the List.
    *@return            The new List, or null.
    **************************************************************************/
    public static List toList(Object arr[])
    {
        return (arr == null) ? null : Arrays.asList(arr);
    }

    /**************************************************************************
    * Return a new List copied from the specified Iterator, or null if 
    * the  Iterator is null.  
    * Note:  This leaves the Iterator with hasNext() == false.
    *@param  iter       Iterator to copy to a new List.
    *@return            The new List.
    **************************************************************************/
    public static List toList(Iterator iter)
    {
        if (iter == null)
        {
            return null;
        }
        List list = new ArrayList();
        while (iter.hasNext())
        {
            list.add(iter.next());
        }
        return list;
    }

    /**************************************************************************
    * Return a new List copied from the specified Enumeration, or null if 
    * the  Enumeration is null.  
    * Note:  This leaves the Enumeration with hasMoreElements() == false.
    *@param  enumeration Enumeration to copy to a new List.
    *@return             The new List.
    **************************************************************************/
    public static List toList(Enumeration enumeration)
    {
        if (enumeration == null)
        {
            return null;
        }
        List list = new ArrayList();
        while (enumeration.hasMoreElements())
        {
            list.add(enumeration.nextElement());
        }
        return list;
    }

    /**************************************************************************
    * Return true if the two Lists contains the same elements or are both null
    * or both empty; false otherwise.
    * Note:  Elements must be exactly the same.  "==", not just equals().   
    *@param  list1      List to compare.
    *@param  list2      List to compare.
    *@return            true if the same, false otherwise.
    **************************************************************************/
    public static boolean haveIdenticalContents(List list1, List list2)
    {
        if ((list1 == null) != (list2 == null))
        {
            // One is null and other is not.
            return false;
        }
        if (list1 != null)
        {
            // Both are not null.
            if (list1.size() != list2.size())
            {
                // Different sizes.
                return false;
            }
            // Both not null and same size.
            for (int i = 0; i < list1.size(); i++)
            {
                if (list1.get(i) != list2.get(i))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**************************************************************************
    * Return true if the two Lists contains equal elements or are both null
    * or both empty; false otherwise.
    * Note:  Elements may not be identical.  May be just equals(), not "==".   
    *@param  list1      List to compare.
    *@param  list2      List to compare.
    *@return            true if equal, false otherwise.
    **************************************************************************/
    public static boolean haveEqualContents(List list1, List list2)
    {
        if ((list1 == null) != (list2 == null))
        {
            // One is null and other is not.
            return false;
        }
        if (list1 != null)
        {
            // Both are not null.
            if (list1.size() != list2.size())
            {
                // Different sizes.
                return false;
            }
            // Both not null and same size.
            for (int i = 0; i < list1.size(); i++)
            {
                if (!ObjUtil.equalsOrBothNull(list1.get(i), list2.get(i)))
                {
                    return false;
                }
            }
        }
        return true;
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
        private static void doTest(List listIn, String strOut, String strValid)
        {
            System.out.println("Input: " + listIn);
            System.out.println("Output should be: " + strValid);
            System.out.println("Output is:        " + strOut);
            System.out.println(ObjUtil.equalsOrBothNull(strOut, strValid)
                               ? "Success!" 
                               : "Failure!");
        }

        private static void doTest
                        (Object[] arrIn, List listOut, List listValid)
        {
            System.out.println("Input: " + ArrUtil.arrayToString(arrIn));
            System.out.println("Output should be: " + listToString(listValid));
            System.out.println("Output is:        " + listToString(listOut));
            System.out.println(haveIdenticalContents(listOut, listValid) 
                               ? "Success!" 
                               : "Failure!");
        }

        private static void doTest
                        (List listIn, List listOut, List listValid)
        {
            System.out.println("Input: " + listToString(listIn));
            System.out.println("Output should be: " + listToString(listValid));
            System.out.println("Output is:        " + listToString(listOut));
            System.out.println(haveIdenticalContents(listOut, listValid) 
                               ? "Success!" 
                               : "Failure!");
        }

        private static void doTest
                        (List list1, List list2, boolean blnOut, boolean blnValid)
        {
            System.out.println("Input 1: " + listToString(list1));
            System.out.println("Input 2: " + listToString(list2));
            System.out.println("Output should be: " + blnValid);
            System.out.println("Output is:        " + blnOut);
            System.out.println(blnOut == blnValid 
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
                System.out.println("Begin tests...");

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- listToString(ArrayList)");
                System.out.println("--");
                //-------------------------------------------------------------

                ArrayList alIn;
              
                //-- Null ArrayList
                alIn = null;
                doTest(alIn,
                       ListUtil.listToString(alIn),
                       null);

                //-- Empty ArrayList
                alIn = new ArrayList();
                doTest(alIn,
                       ListUtil.listToString(alIn),
                       "[]");

                //-- ArrayList containing one null element.
                alIn = new ArrayList();
                alIn.add(null);
                doTest(alIn,
                       ListUtil.listToString(alIn),
                       "[null]");

                //-- ArrayList containing two null elements.
                alIn = new ArrayList();
                alIn.add(null);
                alIn.add(null);
                doTest(alIn,
                       ListUtil.listToString(alIn),
                       "[null, null]");

                //-- ArrayList with one String element.
                alIn = new ArrayList();
                alIn.add("abc");
                doTest(alIn,
                       ListUtil.listToString(alIn),
                       "[abc]");

                //-- ArrayList with some String elements.
                alIn = new ArrayList();
                alIn.add("abc");
                alIn.add("def");
                doTest(alIn,
                       ListUtil.listToString(alIn),
                       "[abc, def]");

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- listToString(Vector)");
                System.out.println("--");
                //-------------------------------------------------------------

                Vector vectorIn;
              
                //-- Null Vector
                vectorIn = null;
                doTest(vectorIn,
                       ListUtil.listToString(vectorIn),
                       null);

                //-- Empty Vector
                vectorIn = new Vector();
                doTest(vectorIn,
                       ListUtil.listToString(vectorIn),
                       "[]");

                //-- Vector containing one null element.
                vectorIn = new Vector();
                vectorIn.add(null);
                doTest(vectorIn,
                       ListUtil.listToString(vectorIn),
                       "[null]");

                //-- Vector containing two null elements.
                vectorIn = new Vector();
                vectorIn.add(null);
                vectorIn.add(null);
                doTest(vectorIn,
                       ListUtil.listToString(vectorIn),
                       "[null, null]");

                //-- Vector with one String element.
                vectorIn = new Vector();
                vectorIn.add("abc");
                doTest(vectorIn,
                       ListUtil.listToString(vectorIn),
                       "[abc]");

                //-- Vector with some String elements.
                vectorIn = new Vector();
                vectorIn.add("abc");
                vectorIn.add("def");
                doTest(vectorIn,
                       ListUtil.listToString(vectorIn),
                       "[abc, def]");

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- toList(array)");
                System.out.println("--");
                //-------------------------------------------------------------

                ArrayList alValid;
                Object[] arrIn = null;
                
                //-- Null array
                arrIn = null;
                alValid = null;
                doTest(arrIn, toList(arrIn), alValid);

                //-- Empty array
                arrIn = new Object[0];
                alValid = new ArrayList();
                doTest(arrIn, toList(arrIn), alValid);

                //-- Array containing one null element.
                arrIn = new Object[] {null};
                alValid = new ArrayList();
                alValid.add(null);
                doTest(arrIn, toList(arrIn), alValid);

                //-- Array containing two null elements.
                arrIn = new Object[] {null, null};
                alValid = new ArrayList();
                alValid.add(null);
                alValid.add(null);
                doTest(arrIn, toList(arrIn), alValid);

                //-- Array containing one String element.
                arrIn = new String[] {"abc"};
                alValid = new ArrayList();
                alValid.add(arrIn[0]);
                doTest(arrIn, toList(arrIn), alValid);

                //-- Array with some String elements.
                arrIn = new String[] {"abc", "def"};
                alValid = new ArrayList();
                alValid.add(arrIn[0]);
                alValid.add(arrIn[1]);
                doTest(arrIn, toList(arrIn), alValid);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- toList(Iterator)");
                System.out.println("--");
                //-------------------------------------------------------------

                Vector vecIn = null;
                
                //-- Null Iterator
                vecIn = null;
                doTest(vecIn, toList((Iterator)null), vecIn);

                //-- Empty Iterator
                vecIn = new Vector();
                doTest(vecIn, toList(vecIn.iterator()), vecIn);

                //-- Iterator containing one null element.
                vecIn = new Vector();
                vecIn.add(null);
                doTest(vecIn, toList(vecIn.iterator()), vecIn);

                //-- Iterator containing two null elements.
                vecIn = new Vector();
                vecIn.add(null);
                vecIn.add(null);
                doTest(vecIn, toList(vecIn.iterator()), vecIn);

                //-- Iterator containing one String element.
                vecIn = new Vector();
                vecIn.add("abc");
                doTest(vecIn, toList(vecIn.iterator()), vecIn);

                //-- Iterator with some String elements.
                vecIn = new Vector();
                vecIn.add("abc");
                vecIn.add("def");
                doTest(vecIn, toList(vecIn.iterator()), vecIn);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- toList(Enumeration)");
                System.out.println("--");
                //-------------------------------------------------------------

                //-- Null Enumeration
                vecIn = null;
                doTest(vecIn, toList((Enumeration)null), vecIn);

                //-- Empty Enumeration
                vecIn = new Vector();
                doTest(vecIn, toList(vecIn.elements()), vecIn);

                //-- Enumeration containing one null element.
                vecIn = new Vector();
                vecIn.add(null);
                doTest(vecIn, toList(vecIn.elements()), vecIn);

                //-- Enumeration containing two null elements.
                vecIn = new Vector();
                vecIn.add(null);
                vecIn.add(null);
                doTest(vecIn, toList(vecIn.elements()), vecIn);

                //-- Enumeration containing one String element.
                vecIn = new Vector();
                vecIn.add("abc");
                doTest(vecIn, toList(vecIn.elements()), vecIn);

                //-- Enumeration with some String elements.
                vecIn = new Vector();
                vecIn.add("abc");
                vecIn.add("def");
                doTest(vecIn, toList(vecIn.elements()), vecIn);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- haveIdenticalContents and " +
                                   "haveEqualContents");
                System.out.println("--");
                //-------------------------------------------------------------

                ArrayList al1;
                ArrayList al2;
              
                //-- First null, second null
                al1 = null;
                al2 = null;
                doTest(al1, al2, haveIdenticalContents(al1, al2), true);
                doTest(al1, al2, haveEqualContents    (al1, al2), true);

                //-- First null, second empty
                al1 = null;
                al2 = new ArrayList();
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- First null, second contains a null element
                al1 = null;
                al2 = new ArrayList();
                al2.add(null);
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- First null, second not empty
                al1 = null;
                al2 = new ArrayList();
                al2.add(new String("abc"));
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- First empty, second null
                al1 = new ArrayList();
                al2 = null;
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- First empty, second empty
                al1 = new ArrayList();
                al2 = new ArrayList();
                doTest(al1, al2, haveIdenticalContents(al1, al2), true);
                doTest(al1, al2, haveEqualContents    (al1, al2), true);

                //-- First empty, second contains a null element
                al1 = new ArrayList();
                al2 = new ArrayList();
                al2.add(null);
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- First empty, second not empty
                al1 = new ArrayList();
                al2 = new ArrayList();
                al2.add(new String("abc"));
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- First contains null element, second null
                al1 = new ArrayList();
                al1.add(null);
                al2 = null;
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- First contains null element, second empty
                al1 = new ArrayList();
                al1.add(null);
                al2 = new ArrayList();
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- First contains null element, second contains a null element
                al1 = new ArrayList();
                al1.add(null);
                al2 = new ArrayList();
                al2.add(null);
                doTest(al1, al2, haveIdenticalContents(al1, al2), true);
                doTest(al1, al2, haveEqualContents    (al1, al2), true);

                //-- First contains null element, second not empty
                al1 = new ArrayList();
                al1.add(null);
                al2 = new ArrayList();
                al2.add(new String("abc"));
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- First not empty, second null
                al1 = new ArrayList();
                al1.add(new String("abc"));
                al2 = null;
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- First not empty, second empty
                al1 = new ArrayList();
                al1.add(new String("abc"));
                al2 = new ArrayList();
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- First not empty, second contains a null element
                al1 = new ArrayList();
                al1.add(new String("abc"));
                al2 = new ArrayList();
                al2.add(null);
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- Both not empty, identical
                String str1 = new String("abc");
                al1 = new ArrayList();
                al1.add(str1);
                al2 = new ArrayList();
                al2.add(str1);
                doTest(al1, al2, haveIdenticalContents(al1, al2), true);
                doTest(al1, al2, haveEqualContents    (al1, al2), true);

                //-- Both not empty, equal, but not identical
                al1 = new ArrayList();
                al1.add(new String("abc"));
                al2 = new ArrayList();
                al2.add(new String("abc"));
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), true);

                //-- Multiple values, identical
                str1 = new String("abc");
                String str2 = new String("def");
                al1 = new ArrayList();
                al1.add(str1);
                al1.add(str2);
                al2 = new ArrayList();
                al2.add(str1);
                al2.add(str2);
                doTest(al1, al2, haveIdenticalContents(al1, al2), true);
                doTest(al1, al2, haveEqualContents    (al1, al2), true);

                //-- Identical values in different orders.
                str1 = new String("abc");
                str2 = new String("def");
                al1 = new ArrayList();
                al1.add(str1);
                al1.add(str2);
                al2 = new ArrayList();
                al2.add(str2);
                al2.add(str1);
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- Multiple values, equal but not identical
                al1 = new ArrayList();
                al1.add(new String("abc"));
                al1.add(new String("def"));
                al2 = new ArrayList();
                al2.add(new String("abc"));
                al2.add(new String("def"));
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), true);

                //-- Multiple values, some identical, all equal
                al1 = new ArrayList();
                al1.add(str1);
                al1.add(new String("abc"));
                al2 = new ArrayList();
                al2.add(str1);
                al2.add(new String("abc"));
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), true);

                //-- Multiple values, some identical, all equal
                al1 = new ArrayList();
                al1.add(new String("abc"));
                al1.add(str1);
                al2 = new ArrayList();
                al2.add(new String("abc"));
                al2.add(str1);
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), true);

                //-- Multiple values, some identical, not all equal
                al1 = new ArrayList();
                al1.add(str1);
                al1.add(new String("abc"));
                al2 = new ArrayList();
                al2.add(str1);
                al2.add(new String("def"));
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- Multiple values, some identical, not all equal
                al1 = new ArrayList();
                al1.add(new String("abc"));
                al1.add(str1);
                al2 = new ArrayList();
                al2.add(new String("def"));
                al2.add(str1);
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), false);

                //-- Multiple values, some null, some identical, all equal
                al1 = new ArrayList();
                al1.add(null);
                al1.add(str1);
                al1.add(new String("abc"));
                al2 = new ArrayList();
                al2.add(null);
                al2.add(str1);
                al2.add(new String("abc"));
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), true);

                //-- Multiple values, some null, some identical, all equal
                al1 = new ArrayList();
                al1.add(null);
                al1.add(new String("abc"));
                al1.add(str1);
                al2 = new ArrayList();
                al2.add(null);
                al2.add(new String("abc"));
                al2.add(str1);
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), true);

                //-- Multiple values, some null, some identical, all equal
                al1 = new ArrayList();
                al1.add(new String("abc"));
                al1.add(null);
                al1.add(str1);
                al2 = new ArrayList();
                al2.add(new String("abc"));
                al2.add(null);
                al2.add(str1);
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), true);

                //-- Multiple values, some null, some identical, all equal
                al1 = new ArrayList();
                al1.add(new String("abc"));
                al1.add(str1);
                al1.add(null);
                al2 = new ArrayList();
                al2.add(new String("abc"));
                al2.add(str1);
                al2.add(null);
                doTest(al1, al2, haveIdenticalContents(al1, al2), false);
                doTest(al1, al2, haveEqualContents    (al1, al2), true);

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
