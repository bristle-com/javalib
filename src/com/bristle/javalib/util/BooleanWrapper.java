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

// BooleanWrapper
/******************************************************************************
* This class wraps a Boolean.  It is useful to subclass when you want an 
* object that is essentially a Boolean, but can be distinguished from other
* Boolean types.  For example, when you write a method with multiple 
* parameters that are all Booleans, but serve different purposes, and you 
* want to be sure they were passed in the correct order on the method call,
* you can define each parameter to be a different subclass of this class.   
* Note:  This class would not be necessary if java.lang.Boolean was not 
*        declared as "final".  
 *<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*     - Declare subclasses of this class:
*        public static class IsFlat extends BooleanWrapper
*        { public IsFlat(boolean blnValue) { super(blnValue); } }
*        public static class IsHard extends BooleanWrapper
*        { public IsHard(boolean blnValue) { super(blnValue); } }
*     - Declare a method that takes the subclasses as parameters:
*        public void validate(Boolean blnValue, IsFlat blnFlat, IsHard blnHard);
*     - Call the method, with order of arguments enforced:
*        validate(blnValue, new IsFlat(true), new IsHard(false));
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
public class BooleanWrapper
{
    //--
    //-- Class variables
    //--

    //--
    //-- Instance variables to support public properties
    //--
    private Boolean m_bln = null;
    
    //--
    //-- Internal instance variables
    //--

    /**************************************************************************
    * Constructor.
    *@param blnValue    Value of the Boolean
    **************************************************************************/
    public BooleanWrapper(boolean blnValue)
    {
        m_bln = new Boolean(blnValue);
    }

    /**************************************************************************
    * Constructor.
    *@param blnValue    Value of the Boolean
    **************************************************************************/
    public BooleanWrapper(Boolean blnValue)
    {
        m_bln = blnValue;
    }

    /**************************************************************************
    * Set the wrapped Boolean.
    *@param  blnNew     The new Boolean.
    **************************************************************************/
    public void setBoolean(Boolean blnNew)
    {
        m_bln = blnNew;
    }

    /**************************************************************************
    * Get the wrapped Boolean.
    *@return            The log level.
    **************************************************************************/
    public Boolean getBoolean()
    {
        return m_bln;
    }

    /**************************************************************************
    * Set the wrapped Boolean.
    *@param  blnNew     The new Boolean.
    **************************************************************************/
    public void setboolean(boolean blnNew)
    {
        m_bln = new Boolean(blnNew);
    }

    /**************************************************************************
    * Get the wrapped Boolean.
    *@return            The log level.
    **************************************************************************/
    public boolean getboolean()
    {
        return m_bln.booleanValue();
    }

    /**************************************************************************
    * Return the BooleanWrapper cast to a String, or null if the specified 
    * BooleanWrapper is null.
    *@param wrapper     BooleanWrapper to cast to String.
    *@return            String version of the object.
    **************************************************************************/
    public static String castToString(BooleanWrapper wrapper)
    {
        return (wrapper == null)
               ? null
               : ObjUtil.castToString(wrapper.getBoolean());
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
