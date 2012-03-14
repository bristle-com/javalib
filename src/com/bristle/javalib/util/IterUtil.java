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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

// IterUtil
/******************************************************************************
* This class contains utility routines for creating and manipulating Java 
* Iterators.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*       Map map = IterUtil.toIterator(Enumeration enumeration);
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
public class IterUtil
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
    * Return a String version of the Iterator. 
    * Note:  This leaves the Iterator with hasNext() == false.
    *@param  iter       Iterator to convert to a String.
    *@return            String version of the Iterator.
    **************************************************************************/
    public static String iteratorToString(Iterator iter)
    {
        if (iter == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        while (iter.hasNext()) {
            sb.append(iter.next() + ", "); 
        }
        if (sb.length() > 2)
        {
            sb.setLength(sb.length() - 2);      // Trim trailing comma and space
        }
        sb.append("]");
        return sb.toString();
    }

    /**************************************************************************
    * Return a String version of the Enumeration. 
    * Note:  This leaves the Enumeration with hasMoreElements() == false.
    *@param  enumeration Enumeration to convert to a String.
    *@return             String version of the Enumeration.
    **************************************************************************/
    public static String enumerationToString(Enumeration enumeration)
    {
        return iteratorToString(toIterator(enumeration));
    }

    /**************************************************************************
    * Create an Iterator from the specified Enumeration. 
    * Note:  This leaves the Enumeration with hasMoreElements() == false.
    *@param  enumeration Enumeration of Objects to put in the Iterator.
    *@return             The new Iterator.
    **************************************************************************/
    public static Iterator toIterator(Enumeration enumeration)
    {
        return (enumeration == null) 
                ? null 
                : ListUtil.toList(enumeration).iterator();
    }

    /**************************************************************************
    * Create an Enumeration from the specified Iterator, or null if 
    * the  Iterator is null.  
    * Note:  This leaves the Iterator with hasNext() == false.
    *@param  iter       Iterator to copy to a new List.
    *@return            The new Enumeration
    **************************************************************************/
    public static Enumeration toEnumeration(Iterator iter)
    {
        if (iter == null)
        {
            return null;
        }
        Vector vec = new Vector();
        while (iter.hasNext())
        {
            vec.add(iter.next());
        }
        return vec.elements();
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
            System.out.println("Output should be: " + strValid);
            System.out.println("Output is:        " + strOut);
            System.out.println(ObjUtil.equalsOrBothNull(strOut, strValid) 
                                ? "Success!" 
                                : "Failure!");
        }

        private static void doTest
                        (List listIn, List listOut, List listValid)
        {
            System.out.println("Input: " + ListUtil.listToString(listIn));
            System.out.println("Output should be: " + ListUtil.listToString(listValid));
            System.out.println("Output is:        " + ListUtil.listToString(listOut));
            System.out.println(ListUtil.haveIdenticalContents(listOut, listValid) 
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

                ArrayList listIn = null;
                Vector vecIn = null;
              
                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- iteratorToString");
                System.out.println("--");
                //-------------------------------------------------------------

                //-- Null list
                listIn = null;
                doTest(IterUtil.iteratorToString(null),
                       null);

                //-- Empty list
                listIn = new ArrayList();
                doTest(IterUtil.iteratorToString(listIn.iterator()),
                       "[]");

                //-- List containing one null element.
                listIn = new ArrayList();
                listIn.add(null);
                doTest(IterUtil.iteratorToString(listIn.iterator()),
                       "[null]");

                //-- List containing two null elements.
                listIn = new ArrayList();
                listIn.add(null);
                listIn.add(null);
                doTest(IterUtil.iteratorToString(listIn.iterator()),
                       "[null, null]");

                //-- List containing one empty string.
                listIn = new ArrayList();
                listIn.add("");
                doTest(IterUtil.iteratorToString(listIn.iterator()),
                       "[]");

                //-- List containing two empty strings.
                listIn = new ArrayList();
                listIn.add("");
                listIn.add("");
                doTest(IterUtil.iteratorToString(listIn.iterator()),
                       "[, ]");

                //-- List with one String element.
                listIn = new ArrayList();
                listIn.add("abc");
                doTest(IterUtil.iteratorToString(listIn.iterator()),
                       "[abc]");

                //-- List with some String elements.
                listIn = new ArrayList();
                listIn.add("abc");
                listIn.add("def");
                doTest(IterUtil.iteratorToString(listIn.iterator()),
                       "[abc, def]");

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- enumerationToString");
                System.out.println("--");
                //-------------------------------------------------------------

                //-- Null Enumeration
                vecIn = null;
                doTest(IterUtil.enumerationToString(null),
                       null);

                //-- Empty Enumeration
                vecIn = new Vector();
                doTest(IterUtil.enumerationToString(vecIn.elements()),
                       "[]");

                //-- Enumeration containing one null element.
                vecIn = new Vector();
                vecIn.add(null);
                doTest(IterUtil.enumerationToString(vecIn.elements()),
                       "[null]");

                //-- Enumeration containing two null elements.
                vecIn = new Vector();
                vecIn.add(null);
                vecIn.add(null);
                doTest(IterUtil.enumerationToString(vecIn.elements()),
                       "[null, null]");

                //-- Enumeration containing one empty string.
                vecIn = new Vector();
                vecIn.add("");
                doTest(IterUtil.enumerationToString(vecIn.elements()),
                       "[]");

                //-- Enumeration containing two empty strings.
                vecIn = new Vector();
                vecIn.add("");
                vecIn.add("");
                doTest(IterUtil.enumerationToString(vecIn.elements()),
                       "[, ]");

                //-- Enumeration with one String element.
                vecIn = new Vector();
                vecIn.add("abc");
                doTest(IterUtil.enumerationToString(vecIn.elements()),
                       "[abc]");

                //-- Enumeration with some String elements.
                vecIn = new Vector();
                vecIn.add("abc");
                vecIn.add("def");
                doTest(IterUtil.enumerationToString(vecIn.elements()),
                       "[abc, def]");

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- toIterator(Enumeration)");
                System.out.println("--");
                //-------------------------------------------------------------

                //-- Null Enumeration
                vecIn = null;
                doTest(vecIn, 
                       ListUtil.toList(toIterator((Enumeration)null)), 
                       vecIn);

                //-- Empty Enumeration
                vecIn = new Vector();
                doTest(vecIn, 
                       ListUtil.toList(toIterator(vecIn.elements())), 
                       vecIn);

                //-- Enumeration containing one null element.
                vecIn = new Vector();
                vecIn.add(null);
                doTest(vecIn, 
                       ListUtil.toList(toIterator(vecIn.elements())), 
                       vecIn);

                //-- Enumeration containing two null elements.
                vecIn = new Vector();
                vecIn.add(null);
                vecIn.add(null);
                doTest(vecIn, 
                       ListUtil.toList(toIterator(vecIn.elements())), 
                       vecIn);

                //-- Enumeration containing one String element.
                vecIn = new Vector();
                vecIn.add("abc");
                doTest(vecIn, 
                       ListUtil.toList(toIterator(vecIn.elements())), 
                       vecIn);

                //-- Enumeration with some String elements.
                vecIn = new Vector();
                vecIn.add("abc");
                vecIn.add("def");
                doTest(vecIn, 
                       ListUtil.toList(toIterator(vecIn.elements())), 
                       vecIn);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- toEnumeration(Iterator)");
                System.out.println("--");
                //-------------------------------------------------------------

                //-- Null Iterator
                vecIn = null;
                doTest(vecIn, 
                       ListUtil.toList(toEnumeration((Iterator)null)), 
                       vecIn);

                //-- Empty Iterator
                vecIn = new Vector();
                doTest(vecIn, 
                       ListUtil.toList(toEnumeration(vecIn.iterator())), 
                       vecIn);

                //-- Iterator containing one null element.
                vecIn = new Vector();
                vecIn.add(null);
                doTest(vecIn, 
                       ListUtil.toList(toEnumeration(vecIn.iterator())), 
                       vecIn);

                //-- Iterator containing two null elements.
                vecIn = new Vector();
                vecIn.add(null);
                vecIn.add(null);
                doTest(vecIn, 
                       ListUtil.toList(toEnumeration(vecIn.iterator())), 
                       vecIn);

                //-- Iterator containing one String element.
                vecIn = new Vector();
                vecIn.add("abc");
                doTest(vecIn, 
                       ListUtil.toList(toEnumeration(vecIn.iterator())), 
                       vecIn);

                //-- Iterator with some String elements.
                vecIn = new Vector();
                vecIn.add("abc");
                vecIn.add("def");
                doTest(vecIn, 
                       ListUtil.toList(toEnumeration(vecIn.iterator())), 
                       vecIn);

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
