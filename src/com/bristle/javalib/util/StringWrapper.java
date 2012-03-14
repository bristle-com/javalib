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

// StringWrapper
/******************************************************************************
* This class wraps a String.  It is useful to subclass when you want an 
* object that is essentially a String, but can be distinguished from other
* String types.  For example, when you write a method with multiple 
* parameters that are all Strings, but serve different purposes, and you 
* want to be sure they were passed in the correct order on the method call,
* you can define each parameter to be a different subclass of this class.   
* Note:  This class would not be necessary if java.lang.String was not 
*        declared as "final".  
 *<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*     - Declare subclasses of this class:
*        public static class PromptString extends StringWrapper
*        { public PromptString(String strValue) { super(strValue); } }
*        public static class ErrorString extends StringWrapper
*        { public ErrorString(String strValue) { super(strValue); } }
*     - Declare a method that takes the subclasses as parameters:
*        public String getValue(PromptString strPrompt, ErrorString strError);
*     - Call the method, with order of arguments enforced:
*        strValue = getValue
*                      (new PromptString("prompt: "), 
*                       new ErrorString("Error occurred"));
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
public class StringWrapper
{
    //--
    //-- Class variables
    //--

    //--
    //-- Instance variables to support public properties
    //--
    private String m_str = null;
    
    //--
    //-- Internal instance variables
    //--

    /**************************************************************************
    * Constructor.
    *@param strValue    Value of the String
    **************************************************************************/
    public StringWrapper(String strValue)
    {
        m_str = strValue;
    }

    /**************************************************************************
    * Set the wrapped String.
    *@param  strNew     The new String.
    **************************************************************************/
    public void setString(String strNew)
    {
        m_str = strNew;
    }

    /**************************************************************************
    * Get the wrapped String.
    *@return            The log level.
    **************************************************************************/
    public String getString()
    {
        return m_str;
    }

    /**************************************************************************
    * Return the StringWrapper cast to a String, or null if the specified 
    * StringWrapper is null.
    *@param wrapper     StringWrapper to cast to String.
    *@return            String version of the object.
    **************************************************************************/
    public static String castToString(StringWrapper wrapper)
    {
        return (wrapper == null)
               ? null
               : wrapper.getString();
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
