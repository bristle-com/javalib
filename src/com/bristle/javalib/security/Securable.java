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

package com.bristle.javalib.security;

import com.bristle.javalib.sql.bristle.AbstractStandardTableBackedObject;

// Securable
/******************************************************************************
* This interface acts as a flag that the implementing class is securable via 
* the Bristle Software access control scheme, and requires the implementing 
* class to provide methods in support of that.
*<pre>
*<b>Usage:</b>
*   - The typical usage is to declare a class that implements this interface,
*     and pass it to the Bristle Software access control classes.
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
public interface Securable
{
    /**************************************************************************
    * Get the data source, typically a DB table name, of the object, for use 
    * in determining whether the current user has access to it.
    *@return The data source
    **************************************************************************/
    public String getDataSource();

    /**************************************************************************
    * Get the unique id
    *@return The unique id
    **************************************************************************/
    public int getId();

    /**************************************************************************
    * Get the value that indicates a null id
    *@return The null id
    **************************************************************************/
    public int getNullId();

    /**************************************************************************
    * Get the unique id of the class of objects
    *@return The unique class id
    **************************************************************************/
    public int getClassId();

    /**************************************************************************
    * Get the value that indicates a null class id
    *@return The null class id
    **************************************************************************/
    public int getNullClassId();

    /**************************************************************************
    * @see AbstractStandardTableBackedObject#getCLASS_OF_OBJECTS()
    **************************************************************************/
    public Securable getSecurableCLASS_OF_OBJECTS();

}
