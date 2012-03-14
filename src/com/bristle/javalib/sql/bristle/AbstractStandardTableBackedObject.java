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

import com.bristle.javalib.sql.dictionary.AbstractDictionaryEntry;

// AbstractStandardTableBackedObject
/******************************************************************************
* This abstract class carries data for a single object that is backed by a 
* table in a database, including all of the fields that are required to 
* exist in all database tables according to the Bristle Software database 
* standards.
*
**<pre>
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
public abstract class AbstractStandardTableBackedObject
{
    //
    // Class variables
    //
    private static final int st_intDefaultNullClassId = 0;
    private static final int st_intDefaultNullId      = 0;

    //
    // Instance variables to support public properties that correspond to 
    // standard table columns.
    //
    private int                     m_intId           = getNullId();
    private String                  m_strCreateUser   = "";
    private String                  m_strCreateDT     = "";
    private String                  m_strUpdateUser   = "";
    private String                  m_strUpdateDT     = "";
    private AbstractDictionaryEntry m_status          = null;

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
    * Set all properties.  Use this whenever you want to be sure that 
    * properties added in the future are also set.  Once this method is
    * updated to include a new property, all calls will fail to compile
    * unless they are also updated. 
    *@param  intId           The property value.
    *@param  strCreateUser   The property value.
    *@param  strCreateDT     The property value.
    *@param  strUpdateUser   The property value.
    *@param  strUpdateDT     The property value.
    *@param  status          The property value.
    **************************************************************************/
    protected void setAll
                    (int                    intId
                    ,String                 strCreateUser
                    ,String                 strCreateDT
                    ,String                 strUpdateUser
                    ,String                 strUpdateDT
                    ,AbstractDictionaryEntry status
                    )
    {
        setId           (intId);
        setCreateUser   (strCreateUser);
        setCreateDT     (strCreateDT);
        setUpdateUser   (strUpdateUser);
        setUpdateDT     (strUpdateDT);
        setStatus       (status);
    }
    
    /**************************************************************************
    * Do a shallow copy of all properties from the specified object. 
    *@param  objFrom    The object to copy from.
    **************************************************************************/
    protected void shallowCopyFrom(AbstractStandardTableBackedObject objFrom)
    {
        setAll(objFrom.getId()
              ,objFrom.getCreateUser()
              ,objFrom.getCreateDT()
              ,objFrom.getUpdateUser()
              ,objFrom.getUpdateDT()
              ,objFrom.getStatus()
              );
    }
    
    /**************************************************************************
    * Get the id.
    *@return The id.
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
    * Get the username of the user who created it.
    *@return The username.
    **************************************************************************/
    public String getCreateUser()
    {
        return m_strCreateUser;
    }

    /**************************************************************************
    * Set the username of the user who created it.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setCreateUser(String strVal)
    {
        m_strCreateUser = strVal;
    }

    /**************************************************************************
    * Get the date and time at which it was created.
    *@return The date/time.
    **************************************************************************/
    public String getCreateDT()
    {
        return m_strCreateDT;
    }

    /**************************************************************************
    * Set the date and time at which it was created.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setCreateDT(String strVal)
    {
        m_strCreateDT = strVal;
    }

    /**************************************************************************
    * Get the username of the user who last updated it.
    *@return The username.
    **************************************************************************/
    public String getUpdateUser()
    {
        return m_strUpdateUser;
    }

    /**************************************************************************
    * Set the username of the user who last updated it.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setUpdateUser(String strVal)
    {
        m_strUpdateUser = strVal;
    }

    /**************************************************************************
    * Get the date and time at which it was last updated.
    *@return The date/time.
    **************************************************************************/
    public String getUpdateDT()
    {
        return m_strUpdateDT;
    }

    /**************************************************************************
    * Set the date and time at which it was last updated.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setUpdateDT(String strVal)
    {
        m_strUpdateDT = strVal;
    }

    /**************************************************************************
    * Get the status.
    *@return The status.
    **************************************************************************/
    public AbstractDictionaryEntry getStatus()
    {
        return m_status;
    }

    /**************************************************************************
    * Set the status.
    *@param  objVal     The value to set.
    **************************************************************************/
    public void setStatus(AbstractDictionaryEntry objVal)
    {
        m_status = objVal;
    }

    /**************************************************************************
    * Get the value that indicates a null id.
    *<pre>
    * Implementation Note:
    *   Not abstract, so subclasses can implicitly use the default value for 
    *   null id.
    *</pre>
    *@return The null id.
    **************************************************************************/
    public int getNullId()
    {
        return getDefaultNullId();
    }

    /**************************************************************************
    * Get the default for the value that indicates a null id.
    *<pre>
    * Implementation Note:
    *   Not abstract, so subclasses can explicitly choose if and when to use 
    *   the default value for null id.
    *</pre>
    *@return The default value.
    **************************************************************************/
    public static int getDefaultNullId()
    {
        return st_intDefaultNullId;
    }

    /**************************************************************************
    * Get the id of the class of objects.
    *<pre>
    * Implementation Note:
    *   Abstract to force each subclass to explicitly specify its class id.
    *   If a subclass doesn't care about class id, it can call getNullClassId() 
    *   from its override.
    *</pre>
    *@return The class id.
    **************************************************************************/
    public abstract int getClassId();

    /**************************************************************************
    * Get the value that indicates a null id of the class of objects.
    *<pre>
    * Implementation Note:
    *   Not abstract, so subclasses can implicitly use the default value for 
    *   null class id.
    *</pre>
    *@return The null class id.
    **************************************************************************/
    public int getNullClassId()
    {
        return getDefaultNullClassId();
    }

    /**************************************************************************
    * Get the default the value that indicates a null id of the class of 
    * objects.
    *<pre>
    * Implementation Note:
    *   Not abstract, so subclasses can explicitly choose if and when to use 
    *   the default value for null class id.
    *</pre>
    *@return The default value.
    **************************************************************************/
    public static int getDefaultNullClassId()
    {
        return st_intDefaultNullClassId;
    }

    /**************************************************************************
    * Get the name of the database table that holds the class of objects.
    *<pre>
    * Implementation Note:
    *   Abstract to force each subclass to explicitly specify its table name.
    *</pre>
    *@return The table name.
    **************************************************************************/
    public abstract String getTableName();

    /**************************************************************************
    * Get the special instance that indicates the class of objects instead of
    * any one particular instance.
    *<pre>
    * Implementation Note:
    *   Abstract to force each subclass to explicitly specify an instance of 
    *   itself to represent its entire class.
    *</pre>
    *@return The special instance.
    **************************************************************************/
    public abstract AbstractStandardTableBackedObject getCLASS_OF_OBJECTS();
}
