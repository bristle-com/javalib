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

// SecurableObject
/******************************************************************************
* This class is a simple object that implements the Securable interface.
*<pre>
*<b>Usage:</b>
*
*   - The typical scenarios for using this class are:
*     - To create a Securable from a set of properties that describe it:
*           Securable securable = new SecurableObject
*                                   (strDataSource, intClassId, intId);
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
public class SecurableObject implements Securable
{

    //
    // Class variables
    //

    // Static instance used to indicate the class of objects instead of
    // any one particular instance.
    private static final SecurableObject st_CLASS_OF_OBJECTS 
                                                    = new SecurableObject();

    private static final int st_intDefaultNullClassId = 0;
    private static final int st_intDefaultNullId      = 0;
    
    //
    // Instance variables to support public properties
    //
    private String m_strDataSource  = "";
    private int    m_intNullClassId = st_intDefaultNullClassId;
    private int    m_intClassId     = m_intNullClassId;
    private int    m_intNullId      = st_intDefaultNullId;
    private int    m_intId          = m_intNullId;

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
    * Get the special instance that indicates the class of objects instead of
    * any one particular instance, without requiring an instance to be created.
    * Note:  This method exists as a convenience for code that doesn't want
    *        to create an instance before calling.  Can't simply make 
    *        getSecurableCLASS_OF_OBJECTS() static because Securable can't 
    *        enforce the existence of a static method. 
    *@return The special instance.
    **************************************************************************/
    public static SecurableObject getStaticCLASS_OF_OBJECTS()
    {
        return st_CLASS_OF_OBJECTS;
    }

    /**************************************************************************
    * @see Securable#getSecurableCLASS_OF_OBJECTS()
    **************************************************************************/
    public Securable getSecurableCLASS_OF_OBJECTS()
    {
        return getStaticCLASS_OF_OBJECTS();
    }

    /**************************************************************************
    * Constructor.
    **************************************************************************/
    public SecurableObject()
    {
        // Nothing to do here.  Must declare this default constructor since 
        // there is another constructor defined also, which means this default
        // constructor does not automatically get generated.
    }

    /**************************************************************************
    * Constructor.
    *
    *@param strDataSource   The data source
    *@param intClassId      The class id
    *@param intId           The object id
    **************************************************************************/
    public SecurableObject
                        (String strDataSource
                        ,int    intClassId
                        ,int    intId)
    {
        m_strDataSource = strDataSource;
        m_intClassId    = intClassId;
        m_intId         = intId;
    }

    /**************************************************************************
    * @see Securable#getDataSource()
    **************************************************************************/
    public String getDataSource()
    {
        return m_strDataSource;
    }

    /**************************************************************************
    * Set the data source, typically a DB table name, of the object.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setDataSource(String strVal)
    {
        m_strDataSource = strVal;
    }

    /**************************************************************************
    * @see Securable#getClassId()
    **************************************************************************/
    public int getClassId()
    {
        return m_intClassId;
    }

    /**************************************************************************
    * Set the id of the class of objects.
    *@param  intVal     The value to set.
    **************************************************************************/
    public void setClassId(int intVal)
    {
        m_intClassId = intVal;
    }

    /**************************************************************************
    * @see Securable#getNullClassId()
    **************************************************************************/
    public int getNullClassId()
    {
        return m_intNullClassId;
    }

    /**************************************************************************
    * Set the value that indicates a null id of the class of objects.
    *@param  intVal     The value to set.
    **************************************************************************/
    public void setNullClassId(int intVal)
    {
        m_intNullClassId = intVal;
    }

    /**************************************************************************
    * Get the default value for NullClassId.
    *@return The default value.
    **************************************************************************/
    public static int getDefaultNullClassId()
    {
        return st_intDefaultNullClassId;
    }

    /**************************************************************************
    * @see Securable#getId()
    **************************************************************************/
    public int getId()
    {
        return m_intId;
    }

    /**************************************************************************
    * Set the id.
    *@param  intVal     The value to set.
    **************************************************************************/
    public void setId(int intVal)
    {
        m_intId = intVal;
    }

    /**************************************************************************
    * @see Securable#getNullId()
    **************************************************************************/
    public int getNullId()
    {
        return m_intNullId;
    }

    /**************************************************************************
    * Set the value that indicates a null id.
    *@param  intVal     The value to set.
    **************************************************************************/
    public void setNullId(int intVal)
    {
        m_intNullId = intVal;
    }

    /**************************************************************************
    * Get the default value for NullId.
    *@return The default value.
    **************************************************************************/
    public static int getDefaultNullId()
    {
        return st_intDefaultNullId;
    }
}
