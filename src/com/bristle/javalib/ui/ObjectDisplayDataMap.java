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

package com.bristle.javalib.ui;

import java.util.HashMap;
import java.util.Map;

// ObjectDisplayDataMap
/******************************************************************************
* This bean class carries a map of display data about all properties of a 
* displayable object, with the property names as map keys.
*<pre>
*<b>Usage:</b>
*
*   - The typical scenario for using this class is from a JSP page, as:
*           ${userBean.displayData.username.minDisplayLength}
*           ${userBean.displayData.password.maxLength}
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
public class ObjectDisplayDataMap extends HashMap
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

    //?? Nope.  Can't do this in Java. like you can in C++.  Instead have to 
    //?? encapsulate and delegate, not inherit from, HashMap.  See Bristle
    //?? tips page for details.  For now, leave these public and throw 
    //?? exceptions.
    //?? Hmmm... Still have to beware things like caller calling entrySet() or 
    //?? even get() and modifying what it finds there.  How to produce a truly
    //?? readonly map?
    //?? Perhaps use java.util.Collections.unmodifiableMap()
    
    /**************************************************************************
    * Overridden clear method to prevent the map from being modified.
    **************************************************************************/
    public void clear()
    {
        throw new RuntimeException("Cannot modify the display data.");
    }

    /**************************************************************************
    * Overridden put method to prevent the map from being modified.
    * ?? Oops!  Can't prevent this.  Otherwise, the map can't be initialized.
    **************************************************************************/
    public Object put(Object arg0, Object arg1)
    {
        return super.put(arg0, arg1);
        //?? throw new RuntimeException("Cannot modify the display data.");
    }

    /**************************************************************************
    * Overridden putAll method to prevent the map from being modified.
    **************************************************************************/
    public void putAll(Map arg0)
    {
        throw new RuntimeException("Cannot modify the display data.");
    }

    /**************************************************************************
    * Overridden put method to prevent the map from being modified.
    **************************************************************************/
    public Object remove(Object arg0)
    {
        throw new RuntimeException("Cannot modify the display data.");
    }
}
