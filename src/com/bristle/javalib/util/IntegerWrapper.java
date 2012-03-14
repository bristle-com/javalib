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

// IntegerWrapper
/******************************************************************************
* This class wraps an Integer.  It is useful to subclass when you want an 
* object that is essentially an Integer, but can be distinguished from other
* Integer types.  For example, when you write a method with multiple 
* parameters that are all Integers, but serve different purposes, and you 
* want to be sure they were passed in the correct order on the method call,
* you can define each parameter to be a different subclass of this class.   
* Note:  This class would not be necessary if java.lang.Integer was not 
*        declared as "final".  
 *<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*     - Declare subclasses of this class:
*        public static class MinInteger extends IntegerWrapper
*        { public MinInteger(int intValue) { super(intValue); } }
*        public static class MaxInteger extends IntegerWrapper
*        { public MaxInteger(int intValue) { super(intValue); } }
*     - Declare a method that takes the subclasses as parameters:
*        public Integer getValue(MinInteger intMin, MaxInteger intMax);
*     - Call the method, with order of arguments enforced:
*        intValue = getValue(new MinInteger(1), new MaxInteger(100));
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
public class IntegerWrapper
{
    //--
    //-- Class variables
    //--

    //--
    //-- Instance variables to support public properties
    //--
    private Integer m_int = null;
    
    //--
    //-- Internal instance variables
    //--

    /**************************************************************************
    * Constructor.
    *@param intValue    Value of the Integer
    **************************************************************************/
    public IntegerWrapper(int intValue)
    {
        m_int = new Integer(intValue);
    }

    /**************************************************************************
    * Constructor.
    *@param intValue    Value of the Integer
    **************************************************************************/
    public IntegerWrapper(Integer intValue)
    {
        m_int = intValue;
    }

    /**************************************************************************
    * Set the wrapped Integer.
    *@param  intNew     The new Integer.
    **************************************************************************/
    public void setInteger(Integer intNew)
    {
        m_int = intNew;
    }

    /**************************************************************************
    * Get the wrapped Integer.
    *@return            The log level.
    **************************************************************************/
    public Integer getInteger()
    {
        return m_int;
    }

    /**************************************************************************
    * Set the wrapped Integer.
    *@param  intNew     The new Integer.
    **************************************************************************/
    public void setInt(int intNew)
    {
        m_int = new Integer(intNew);
    }

    /**************************************************************************
    * Get the wrapped Integer.
    *@return            The log level.
    **************************************************************************/
    public int getInt()
    {
        return m_int.intValue();
    }

    /**************************************************************************
    * Return the IntegerWrapper cast to a String, or null if the specified 
    * IntegerWrapper is null.
    *@param wrapper     IntegerWrapper to cast to String.
    *@return            String version of the object.
    **************************************************************************/
    public static String castToString(IntegerWrapper wrapper)
    {
        return (wrapper == null)
               ? null
               : ObjUtil.castToString(wrapper.getInteger());
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
