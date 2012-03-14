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

import java.util.HashMap;

// InvokableMethodViaMap
/******************************************************************************
* This abstract class supports the ability for an arbitrary one-parameter Java 
* method to be invoked via a JSP EL expression, by presenting the method as a 
* map.  Subclasses must override the get() method with an implementation that
* invokes the desired Java method.
*<pre>
*<b>Usage:</b>
*
*   - The typical scenario for using this class is:
*     1. Subclass it with a class that overrides get() to call the Java method.
*     2. Invoke the subclass from a JSP page, as:
*           <xmp>
*           <jsp:useBean id='myBean' 
*                        class='mySubclass' 
*           />
*           </xmp>
*           ${myBean.myParameterValue}
*           ${myBean[myELVariableHoldingTheParameterValue]}
*
*<b>Assumptions:</b>
*<b>Effects:</b>
*       - None.
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*   - Extends HashMap, not extends AbstractMap or implements Map, so that 
*     subclasses are required to implement only the get() method, not all 
*     methods.
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public abstract class InvokableMethodViaMap extends HashMap
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
    * Overridden methods to fake a Map with all possible keys valid.
    **************************************************************************/
    public abstract Object get(Object arg0);
}
