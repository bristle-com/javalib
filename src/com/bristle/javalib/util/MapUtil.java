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

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Enumeration;

// MapUtil
/******************************************************************************
* This class contains utility routines for creating and manipulating Java Maps.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*       Map map = MapUtil.createMap(arr1, arr2);
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
public class MapUtil
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
    * Return a string version of the Map. 
    *@param  map        Map to convert to a String.
    *@return            String version of the Map, or null if map was null.
    **************************************************************************/
    public static String mapToString(Map map)
    {
        return ObjUtil.castToString(map);
    }

    /**************************************************************************
    * Create a Map from the specified Iterator, using Integer values 
    * 0, 1, 2, and so on as the key values. 
    * Note:  This leaves the Iterator with hasNext() == false.
    *@param  iter       Iterator of Objects to put in the Map.
    *@return            The new Map.
    **************************************************************************/
    public static Map createMap(Iterator iter)
    {
        if (iter == null)
        {
            return null;
        }
        Map map = new HashMap();
        int i = 0;
        while (iter.hasNext())
        {
            map.put(new Integer(i++), iter.next());
        }
        return map;
    }

    /**************************************************************************
    * Create a Map from the specified Enumeration, using Integer values 
    * 0, 1, 2, and so on as the key values. 
    * Note:  This leaves the Enumeration with hasMoreElements() == false.
    *@param  enumeration Enumeration of Objects to put in the Map.
    *@return             The new Map.
    **************************************************************************/
    public static Map createMap(Enumeration enumeration)
    {
        return createMap(IterUtil.toIterator(enumeration));
    }

    /**************************************************************************
    * Create a Map from the specified List, using Integer values 0, 1, 2,
    * and so on as the key values. 
    *@param  list       List of Objects to put in the Map.
    *@return            The new Map.
    **************************************************************************/
    public static Map createMap(List list)
    {
        return createMap((list == null) ? null : list.iterator());
    }

    /**************************************************************************
    * Create a Map from the specified array, using Integer values 0, 1, 2,
    * and so on as the key values. 
    *@param  arr        Array of Objects to put in the Map.
    *@return            The new Map.
    **************************************************************************/
    public static Map createMap(Object arr[])
    {
        return createMap(ListUtil.toList(arr));
    }

    /**************************************************************************
    * This exception is thrown when the number of specified keys is not the 
    * same as the number of specified values. 
    **************************************************************************/
    public static class DifferentNumberOfKeysAndValues extends Exception
    {
        private static final long serialVersionUID = 1L;
        public DifferentNumberOfKeysAndValues(String msg) { super(msg); }
    }

    /**************************************************************************
    * Create a Map from the specified Iterators of keys and values.  
    * Returns null if the Iterator of keys is null and the Iterator of values 
    * is null or empty.
    * Returns an empty Map if the Iterator of keys is empty and the 
    * Iterator of values is null or empty. 
    * Note:  If the same key occurs more than once, the later value overwrites
    *        the earlier value in the Map.   
    * Note:  This leaves the Iterators with hasNext() == false.
    *@param  iterKeys   Iterator of Objects to use as keys in the Map.
    *@param  iterVals   Iterator of Objects to use as values in the Map.
    *@return            The new Map.
    *@throws DifferentNumberOfKeysAndValues
    *                   When the Iterators do not contain the same number of
    *                   elements.
    **************************************************************************/
    public static Map createMap(Iterator iterKeys, Iterator iterVals)
                                throws DifferentNumberOfKeysAndValues
    {

        if (iterKeys == null && 
            (iterVals == null || !iterVals.hasNext()))
        {
            // Keys is null and Vals is null or empty.
            return null;
        }

        if ((iterKeys == null || !iterKeys.hasNext()) && 
            (iterVals != null && iterVals.hasNext()))  
        {
            // Keys is null and Vals is non-empty, or
            // Keys is empty and Vals is non-empty.
            throw new DifferentNumberOfKeysAndValues
                        ("Zero keys, but more than zero values"); 
        }

        // Keys is not null.
        // If Keys is empty, Vals may be null or empty.
        // If Keys is not empty, Vals may be null, empty, or not empty.
        // Create map, which may be returned empty.
        Map map = new HashMap();

        if (iterKeys.hasNext() && iterVals == null)  
        {
            // Keys is non-empty and Vals is null.
            throw new DifferentNumberOfKeysAndValues
                        ("More than zero keys, but zero values"); 
        }

        // Keys is not null.
        // If Keys is empty, Vals may be null or empty.
        // If Keys is not empty, Vals may empty, or non-empty.
        if (iterVals != null)
        {

            // Keys is not null.
            // Vals is not null.
            // If Keys is empty, Vals is empty.
            // If Keys is not empty, Vals may empty, or non-empty.
            // Start filling the map and counting.
            int intKeysSize = 0;
            int intValsSize = 0;
            while (iterKeys.hasNext())
            {
                // Keys is not null or empty..
                // Vals is not null, but may be empty, or not empty.
                try
                {
                    intKeysSize++;
                    map.put(iterKeys.next(), iterVals.next());
                    intValsSize++;
                }
                catch (NoSuchElementException e)
                {
                    throw new DifferentNumberOfKeysAndValues
                                ("Number of keys (" + intKeysSize + " or more)"+ 
                                 " doesn't match number of values" +
                                 " (" + intValsSize + ")"); 
                }
            }
            if (iterVals.hasNext())
            {
                intValsSize++;
                throw new DifferentNumberOfKeysAndValues
                            ("Number of keys (" + intKeysSize + ")"+ 
                             " doesn't match number of values" +
                             " (" + intValsSize + " or more)"); 
            }
        }
        return map;
    }

    /**************************************************************************
    * Create a Map from the specified Enumerations of keys and values.  
    * Returns null if the Enumeration of keys is null and the Enumeration of 
    * values is null or empty.
    * Returns an empty Map if the Enumeration of keys is empty and the 
    * Enumeration of values is null or empty. 
    * Note:  If the same key occurs more than once, the later value overwrites
    *        the earlier value in the Map.   
    * Note:  This leaves the Enumerations with hasMoreElements() == false.
    *@param  enumKeys   Enumeration of Objects to use as keys in the Map.
    *@param  enumVals   Enumeration of Objects to use as values in the Map.
    *@return            The new Map.
    *@throws DifferentNumberOfKeysAndValues
    *                   When the Enumerations are not the same size.
    **************************************************************************/
    public static Map createMap(Enumeration enumKeys, Enumeration enumVals)
                                throws DifferentNumberOfKeysAndValues
    {
        return createMap
                    ((enumKeys == null) ? null : IterUtil.toIterator(enumKeys),
                     (enumVals == null) ? null : IterUtil.toIterator(enumVals));                 
    }

    /**************************************************************************
    * Create a Map from the specified Lists of keys and values.  
    * Returns null if the List of keys is null and the List of values is 
    * null or empty.
    * Returns an empty Map if the List of keys is empty and the List of 
    * values is null or empty. 
    * Note:  If the same key occurs more than once, the later value overwrites
    *        the earlier value in the Map.   
    *@param  listKeys   List of Objects to use as keys in the Map.
    *@param  listVals   List of Objects to use as values in the Map.
    *@return            The new Map.
    *@throws DifferentNumberOfKeysAndValues
    *                   When the Lists are not the same size.
    **************************************************************************/
    public static Map createMap(List listKeys, List listVals)
                                throws DifferentNumberOfKeysAndValues
    {
        return createMap
                    ((listKeys == null) ? null : listKeys.iterator(),
                     (listVals == null) ? null : listVals.iterator());                 
    }

    /**************************************************************************
    * Create a Map from the specified arrays of keys and values.  
    * Returns null if the array of keys is null and the array of values is 
    * null or empty.
    * Returns an empty Map if the array of keys is empty and the array of 
    * values is null or empty.
    * Note:  If the same key occurs more than once, the later value overwrites
    *        the earlier value in the Map.   
    *@param  arrKeys    Array of Objects to use as keys in the Map.
    *@param  arrVals    Array of Objects to use as values in the Map.
    *@return            The new Map.
    *@throws DifferentNumberOfKeysAndValues
    *                   When the arrays are not the same size.
    **************************************************************************/
    public static Map createMap(Object arrKeys[], Object arrVals[])
                                throws DifferentNumberOfKeysAndValues
 
    {
        return createMap (ListUtil.toList(arrKeys), ListUtil.toList(arrVals));                 
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
                (Object[] arr, Map map, String strValid)
        {
            System.out.println("Input is: " + ArrUtil.arrayToString(arr));
            System.out.println("Output should be: " + strValid);
            System.out.println("Output is:        " + mapToString(map));
            System.out.println(ObjUtil.equalsOrBothNull(mapToString(map), strValid) 
                               ? "Success!" 
                               : "Failure!");
        }

        private static void doTest
                (Object[] arrKeys, Object[] arrVals, Map map, String strValid)
        {
            System.out.println("Input keys is: " + ArrUtil.arrayToString(arrKeys));
            System.out.println("Input vals is: " + ArrUtil.arrayToString(arrVals));
            System.out.println("Output should be: " + strValid);
            System.out.println("Output is:        " + mapToString(map));
            System.out.println(ObjUtil.equalsOrBothNull(mapToString(map), strValid) 
                               ? "Success!" 
                               : "Failure!");
        }

        private static void doTestException
                                        (Object[] arrKeys, 
                                         Object[] arrVals, 
                                         Exception eValid)
        {
            System.out.println("Input keys is: " + ArrUtil.arrayToString(arrKeys));
            System.out.println("Input vals is: " + ArrUtil.arrayToString(arrVals));
            System.out.println("Output should be: " + eValid);
            try
            {
                MapUtil.createMap(arrKeys, arrVals);                    
                System.out.println("Failure!  Expected error not detected.");
            }
            catch (Exception eOut)
            {
                System.out.println("Output is:        " + eOut);
                System.out.println((eOut.getClass() == eValid.getClass()) 
                                   ? "Success!" 
                                   : "Failure!");
            }
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
                System.out.println("-- mapToString");
                System.out.println("--");
                //-------------------------------------------------------------

                Map mapIn = null;
              
                //-- Null map
                mapIn = null;
                doTest(MapUtil.mapToString(mapIn),
                       null);

                //-- Empty map
                mapIn = new HashMap();
                doTest(MapUtil.mapToString(mapIn),
                       "{}");

                //-- Map containing one null element.
                mapIn = new HashMap();
                mapIn.put(new Integer(0), null);
                doTest(MapUtil.mapToString(mapIn),
                       "{0=null}");

                //-- Map containing two null elements.
                mapIn = new HashMap();
                mapIn.put(new Integer(0), null);
                mapIn.put(new Integer(1), null);
                doTest(MapUtil.mapToString(mapIn),
                       "{1=null, 0=null}");

                //-- Map containing one empty string.
                mapIn = new HashMap();
                mapIn.put(new Integer(0), "");
                doTest(MapUtil.mapToString(mapIn),
                       "{0=}");

                //-- Map containing two empty strings.
                mapIn = new HashMap();
                mapIn.put(new Integer(0), "");
                mapIn.put(new Integer(1), "");
                doTest(MapUtil.mapToString(mapIn),
                       "{1=, 0=}");

                //-- Map with one String element.
                mapIn = new HashMap();
                mapIn.put(new Integer(0), "abc");
                doTest(MapUtil.mapToString(mapIn),
                       "{0=abc}");

                //-- Map with some String elements.
                mapIn = new HashMap();
                mapIn.put(new Integer(0), "abc");
                mapIn.put(new Integer(1), "def");
                doTest(MapUtil.mapToString(mapIn),
                       "{1=def, 0=abc}");

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- createMap(arr)");
                System.out.println("--");
                //-------------------------------------------------------------

                String arrIn[];
              
                //-- Null array
                arrIn = null;
                doTest(arrIn,
                       MapUtil.createMap(arrIn),
                       null);
                            
                //-- Empty array
                arrIn = new String[0];
                doTest(arrIn,
                       MapUtil.createMap(arrIn),
                       "{}");

                //-- Array containing one null element.
                arrIn = new String[1];
                arrIn[0] = null;
                doTest(arrIn,
                       MapUtil.createMap(arrIn),
                       "{0=null}");

                //-- Array containing two null elements.
                arrIn = new String[2];
                arrIn[0] = null;
                arrIn[1] = null;
                doTest(arrIn,
                       MapUtil.createMap(arrIn),
                       "{1=null, 0=null}");

                //-- Array containing one empty string.
                arrIn = new String[1];
                arrIn[0] = "";
                doTest(arrIn,
                       MapUtil.createMap(arrIn),
                       "{0=}");

                //-- Array containing two empty strings.
                arrIn = new String[2];
                arrIn[0] = "";
                arrIn[1] = "";
                doTest(arrIn,
                       MapUtil.createMap(arrIn),
                       "{1=, 0=}");

                //-- Array with one String element.
                arrIn = new String[1];
                arrIn[0] = "abc";
                doTest(arrIn,
                       MapUtil.createMap(arrIn),
                       "{0=abc}");

                //-- Array with some String elements.
                arrIn = new String[2];
                arrIn[0] = "abc";
                arrIn[1] = "def";
                doTest(arrIn,
                       MapUtil.createMap(arrIn),
                       "{1=def, 0=abc}");

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- createMap(arr,null)");
                System.out.println("--");
                //-------------------------------------------------------------

                String arrKeys[];
                String arrVals[];
              
                //-- Keys null, Values null
                arrKeys = null;
                arrVals = null;
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       null);

                //-- Keys empty, Values null
                arrKeys = new String[0];
                arrVals = null;
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       "{}");

                //-- Keys containing one null, Values null
                arrKeys = new String[1];
                arrKeys[0] = null;
                arrVals = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));
                    
                //-- Keys containing two nulls, Values null
                arrKeys = new String[2];
                arrKeys[0] = null;
                arrKeys[1] = null;
                arrVals = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys containing one empty string, Values null
                arrKeys = new String[1];
                arrKeys[0] = "";
                arrVals = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys containing two empty strings, Values null
                arrKeys = new String[2];
                arrKeys[0] = "";
                arrKeys[1] = "";
                arrVals = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys with one String, Values null
                arrKeys = new String[1];
                arrKeys[0] = "abc";
                arrVals = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys with some Strings, Values null
                arrKeys = new String[2];
                arrKeys[0] = "abc";
                arrKeys[1] = "def";
                arrVals = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- createMap(arr,empty)");
                System.out.println("--");
                //-------------------------------------------------------------

                //-- Keys null, Values empty
                arrKeys = null;
                arrVals = new String[0];
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       null);

                //-- Keys empty, Values empty
                arrKeys = new String[0];
                arrVals = new String[0];
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       "{}");

                //-- Keys containing one null, Values empty
                arrKeys = new String[1];
                arrKeys[0] = null;
                arrVals = new String[0];
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));
                    
                //-- Keys containing two nulls, Values empty
                arrKeys = new String[2];
                arrKeys[0] = null;
                arrKeys[1] = null;
                arrVals = new String[0];
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys containing one empty string, Values empty
                arrKeys = new String[1];
                arrKeys[0] = "";
                arrVals = new String[0];
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys containing two empty strings, Values empty
                arrKeys = new String[2];
                arrKeys[0] = "";
                arrKeys[1] = "";
                arrVals = new String[0];
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys with one String, Values empty
                arrKeys = new String[1];
                arrKeys[0] = "abc";
                arrVals = new String[0];
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys with some Strings, Values empty
                arrKeys = new String[2];
                arrKeys[0] = "abc";
                arrKeys[1] = "def";
                arrVals = new String[0];
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- createMap(arr,[null])");
                System.out.println("--");
                //-------------------------------------------------------------

                //-- Keys null, Values containing one null
                arrKeys = null;
                arrVals = new String[1];
                arrVals[0] = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys empty, Values containing one null
                arrKeys = new String[0];
                arrVals = new String[1];
                arrVals[0] = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys containing one null, Values containing one null
                arrKeys = new String[1];
                arrKeys[0] = null;
                arrVals = new String[1];
                arrVals[0] = null;
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       "{null=null}");
                    
                //-- Keys containing two nulls, Values containing one null
                arrKeys = new String[2];
                arrKeys[0] = null;
                arrKeys[1] = null;
                arrVals = new String[1];
                arrVals[0] = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys containing one empty string, Values containing one null
                arrKeys = new String[1];
                arrKeys[0] = "";
                arrVals = new String[1];
                arrVals[0] = null;
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       "{=null}");

                //-- Keys containing two empty strings, Values containing one null
                arrKeys = new String[2];
                arrKeys[0] = "";
                arrKeys[1] = "";
                arrVals = new String[1];
                arrVals[0] = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys with one String, Values containing one null
                arrKeys = new String[1];
                arrKeys[0] = "abc";
                arrVals = new String[1];
                arrVals[0] = null;
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       "{abc=null}");

                //-- Keys with some Strings, Values containing one null
                arrKeys = new String[2];
                arrKeys[0] = "abc";
                arrKeys[1] = "def";
                arrVals = new String[1];
                arrVals[0] = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- createMap(arr,[null,null])");
                System.out.println("--");
                //-------------------------------------------------------------

                //-- Keys null, Values containing two nulls
                arrKeys = null;
                arrVals = new String[2];
                arrVals[0] = null;
                arrVals[1] = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys empty, Values containing two nulls
                arrKeys = new String[0];
                arrVals = new String[2];
                arrVals[0] = null;
                arrVals[1] = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys containing one null, Values containing two nulls
                arrKeys = new String[1];
                arrKeys[0] = null;
                arrVals = new String[2];
                arrVals[0] = null;
                arrVals[1] = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));
                    
                //-- Keys containing two nulls, Values containing two nulls
                //-- Map contains only one null=null entry.
                arrKeys = new String[2];
                arrKeys[0] = null;
                arrKeys[1] = null;
                arrVals = new String[2];
                arrVals[0] = null;
                arrVals[1] = null;
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       "{null=null}");

                //-- Keys containing one empty string, Values containing two nulls
                arrKeys = new String[1];
                arrKeys[0] = "";
                arrVals = new String[2];
                arrVals[0] = null;
                arrVals[1] = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys containing two empty strings, Values containing two nulls
                //-- Map contains only one =null entry.
                arrKeys = new String[2];
                arrKeys[0] = "";
                arrKeys[1] = "";
                arrVals = new String[2];
                arrVals[0] = null;
                arrVals[1] = null;
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       "{=null}");

                //-- Keys with one String, Values containing two nulls
                arrKeys = new String[1];
                arrKeys[0] = "abc";
                arrVals = new String[2];
                arrVals[0] = null;
                arrVals[1] = null;
                doTestException
                      (arrKeys,
                       arrVals,
                       new DifferentNumberOfKeysAndValues("error message"));

                //-- Keys with some Strings, Values containing two nulls
                arrKeys = new String[2];
                arrKeys[0] = "abc";
                arrKeys[1] = "def";
                arrVals = new String[2];
                arrVals[0] = null;
                arrVals[1] = null;
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       "{def=null, abc=null}");

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- createMap(arr,arr)");
                System.out.println("--");
                //-------------------------------------------------------------

                //-- Normal case of two arrays.
                arrKeys = new String[2];
                arrKeys[0] = "key1";
                arrKeys[1] = "key2";
                arrVals = new String[2];
                arrVals[0] = "val1";
                arrVals[1] = "val2";
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       "{key1=val1, key2=val2}");

                //-- Same key String object.
                //-- Second overwrites first in map.
                arrKeys = new String[2];
                arrKeys[0] = "key1";
                arrKeys[1] = arrKeys[0];
                arrVals = new String[2];
                arrVals[0] = "val1";
                arrVals[1] = "val2";
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       "{key1=val2}");

                //-- Same key String object (Java shares literal pool constants) 
                //-- Second overwrites first in map.
                arrKeys = new String[2];
                arrKeys[0] = "key1";
                arrKeys[1] = "key1";
                arrVals = new String[2];
                arrVals[0] = "val1";
                arrVals[1] = "val2";
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       "{key1=val2}");

                //-- Same key value, but different String objects. 
                //-- Second overwrites first in map because String.equals()
                //-- says they are the same key.
                arrKeys = new String[2];
                arrKeys[0] = new String("key1");
                arrKeys[1] = new String("key1");
                arrVals = new String[2];
                arrVals[0] = "val1";
                arrVals[1] = "val2";
                doTest(arrKeys,
                       arrVals,
                       MapUtil.createMap(arrKeys, arrVals),
                       "{key1=val2}");

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
