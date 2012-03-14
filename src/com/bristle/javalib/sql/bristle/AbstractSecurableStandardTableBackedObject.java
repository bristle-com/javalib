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

package com.bristle.javalib.sql.bristle;

import com.bristle.javalib.security.Securable;

// AbstractSecurableStandardTableBackedObject
/******************************************************************************
* This class is a simple object that implements the Securable interface for
* an AbstractStandardTableBackedObject.
*<pre>
*<b>Usage:</b>
*
*   - The typical scenarios for using this class are:
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
public abstract class AbstractSecurableStandardTableBackedObject 
                                extends AbstractStandardTableBackedObject
                                implements Securable
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
    * @see Securable#getDataSource()
    **************************************************************************/
    public String getDataSource()
    {
        return getTableName();
    }

    /**************************************************************************
    * @see AbstractStandardTableBackedObject#getCLASS_OF_OBJECTS()
    * Calls {@link getSecurableStandardTableBackedCLASS_OF_OBJECTS()}
    **************************************************************************/
    public AbstractStandardTableBackedObject getCLASS_OF_OBJECTS()
    {
        return getSecurableStandardTableBackedCLASS_OF_OBJECTS();
    }

    /**************************************************************************
    * @see Securable#getSecurableCLASS_OF_OBJECTS()
    * Calls {@link getSecurableStandardTableBackedCLASS_OF_OBJECTS()}
    **************************************************************************/
    public Securable getSecurableCLASS_OF_OBJECTS()
    {
        return getSecurableStandardTableBackedCLASS_OF_OBJECTS();
    }

    /**************************************************************************
    * @see #getCLASS_OF_OBJECTS()
    * @see #getSecurableCLASS_OF_OBJECTS()
    **************************************************************************/
    public abstract AbstractSecurableStandardTableBackedObject 
                            getSecurableStandardTableBackedCLASS_OF_OBJECTS();

    /**************************************************************************
    * @see AbstractStandardTableBackedObject#getClassId()
    *<pre>
    * Implementation Note:
    *   Abstract to force each subclass to explicitly specify its class id.
    *   If a subclass doesn't care about class id, it can call getNullClassId() 
    *   from its override.
    *</pre>
    **************************************************************************/
    public abstract int getClassId();

    /**************************************************************************
    * @see AbstractStandardTableBackedObject#getTableName()
    *<pre>
    * Implementation Note:
    *   Abstract to force each subclass to explicitly specify its table name.
    *</pre>
    **************************************************************************/
    public abstract String getTableName();

}
