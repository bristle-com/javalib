// Copyright (C) 2007-2012 Bristle Software, Inc.
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

package com.bristle.javalib.jsp;

// Thrower
/******************************************************************************
* This class exists as a way to throw a Throwable from a JSP page, without 
* getting the JSP compile-time error:  
*<pre> 
*   org.apache.jasper.JasperException: Unable to compile class for JSP
*   ...
*   Generated servlet error:
*   ...  unreachable statement
*</pre>
*<pre>
*<b>Usage:</b>
*
*   - The typical scenario for using this class from a JSP page is:
*       <% Thrower.throwit(new MyException("text")); %>
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
public class Thrower
{
    //
    // Class variables
    //

    //
    // Instance variables to support public properties
    //

    //
    // Internal instance variables
    //

    /**************************************************************************
     * This number identifies the version of the class definition, used for 
     * serialized instances.  Be sure to increment it when adding/modifying
     * instance variable definitions or making any other change to the class
     * definition.  Omitting this declaration causes a compiler warning for 
     * any class that implements java.io.Serializable.
     *************************************************************************/
    private static final long serialVersionUID = 1L;

    /**************************************************************************
    * Throw the specified Throwable, without the compiler knowing in advance 
    * that it will certainly be thrown.
    *@param throwable Throwable to be thrown
    *@throws Throwable
    **************************************************************************/
    public static void throwit(Throwable throwable) throws Throwable
    {
        throw throwable;
    }
}
