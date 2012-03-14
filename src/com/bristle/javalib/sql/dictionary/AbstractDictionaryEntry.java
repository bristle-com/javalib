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

package com.bristle.javalib.sql.dictionary;

import com.bristle.javalib.sql.bristle.AbstractStandardTableBackedObject;

// AbstractDictionaryEntry
/******************************************************************************
* This class carries data for a single dictionary entry.  It is useful for any
* object that requires no more properties than those defined here (numeric id, 
* term, definition, notes, status, etc.) and is commonly mapped to a dictionary 
* or lookup table in a database. 
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
public abstract class AbstractDictionaryEntry 
                                extends AbstractStandardTableBackedObject
{

    //
    // Class variables
    //

    //
    // Instance variables to support public properties
    //
    private AbstractDictionaryEntry m_internalCategory = null;
    private String                  m_strCategory      = "";
    private String                  m_strTerm          = "";
    private String                  m_strDefinition    = "";
    private String                  m_strNotes         = "";

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
    * Default constructor.  Must be explicitly declared since there are other
    * declared constructors.
    **************************************************************************/
    public AbstractDictionaryEntry()
    {
        // Nothing to do.
    }

    /**************************************************************************
    * Constructor
    *  
    *@param  intId            The property value.
    **************************************************************************/
    public AbstractDictionaryEntry(int intId)
    {
        setId(intId);
    }

    /**************************************************************************
    * Constructor
    *  
    *@param  intId            The property value.
    *@param  strTerm          The property value.
    *@param  strDefinition    The property value.
    **************************************************************************/
    public AbstractDictionaryEntry
                    (int             intId
                    ,String          strTerm
                    ,String          strDefinition
                    )
    {
        setMinimalFields(intId, strTerm, strDefinition);
    }

    /**************************************************************************
    * Constructor
    *  
    *@param  intId            The property value.
    *@param  internalCategory The property value.
    *@param  strCategory      The property value.
    *@param  strTerm          The property value.
    *@param  strDefinition    The property value.
    *@param  strNotes         The property value.
    *@param  strCreateUser    The property value.
    *@param  strCreateDT      The property value.
    *@param  strUpdateUser    The property value.
    *@param  strUpdateDT      The property value.
    *@param  status           The property value.
    **************************************************************************/
    public AbstractDictionaryEntry
                    (int                     intId
                    ,AbstractDictionaryEntry internalCategory
                    ,String                  strCategory
                    ,String                  strTerm
                    ,String                  strDefinition
                    ,String                  strNotes
                    ,String                  strCreateUser
                    ,String                  strCreateDT
                    ,String                  strUpdateUser
                    ,String                  strUpdateDT
                    ,AbstractDictionaryEntry status
                    )
    {
        setAll(intId
              ,internalCategory
              ,strCategory
              ,strTerm
              ,strDefinition
              ,strNotes
              ,strCreateUser
              ,strCreateDT
              ,strUpdateUser
              ,strUpdateDT
              ,status
              );
    }

    /**************************************************************************
    * Set all properties.  Use this whenever you want to be sure that 
    * properties added in the future are also set.  Once this method is
    * updated to include a new property, all calls will fail to compile
    * unless they are also updated. 
    *@param  intId            The property value.
    *@param  internalCategory The property value.
    *@param  strCategory      The property value.
    *@param  strTerm          The property value.
    *@param  strDefinition    The property value.
    *@param  strNotes         The property value.
    *@param  strCreateUser    The property value.
    *@param  strCreateDT      The property value.
    *@param  strUpdateUser    The property value.
    *@param  strUpdateDT      The property value.
    *@param  status           The property value.
    **************************************************************************/
    public void setAll
                    (int                     intId
                    ,AbstractDictionaryEntry internalCategory
                    ,String                  strCategory
                    ,String                  strTerm
                    ,String                  strDefinition
                    ,String                  strNotes
                    ,String                  strCreateUser
                    ,String                  strCreateDT
                    ,String                  strUpdateUser
                    ,String                  strUpdateDT
                    ,AbstractDictionaryEntry status
                    )
    {
        setAll              (intId
                             ,strCreateUser
                             ,strCreateDT
                             ,strUpdateUser
                             ,strUpdateDT
                             ,status
                             );
        setInternalCategory (internalCategory);
        setCategory         (strCategory);
        setTerm             (strTerm);
        setDefinition       (strDefinition);
        setNotes            (strNotes);
    }
    
    /**************************************************************************
    * Do a shallow copy of all properties from the specified object. 
    *@param  objFrom    The object to copy from.
    **************************************************************************/
    public void shallowCopyFrom(AbstractDictionaryEntry objFrom)
    {
        setAll(objFrom.getId()
              ,objFrom.getInternalCategory()
              ,objFrom.getCategory()
              ,objFrom.getTerm()
              ,objFrom.getDefinition()
              ,objFrom.getNotes()
              ,objFrom.getCreateUser()
              ,objFrom.getCreateDT()
              ,objFrom.getUpdateUser()
              ,objFrom.getUpdateDT()
              ,objFrom.getStatus()
              );
    }
    
    /**************************************************************************
    * Set only the properties required to use the dictionary entry, without 
    * concern for editing it, maintaining it, etc. 
    *@param  intId            The property value.
    *@param  strTerm          The property value.
    *@param  strDefinition    The property value.
    **************************************************************************/
    public void setMinimalFields
                    (int             intId
                    ,String          strTerm
                    ,String          strDefinition
                    )
    {
        setId               (intId);
        setTerm             (strTerm);
        setDefinition       (strDefinition);
    }
    
    /**************************************************************************
    *@see AbstractStandardTableBackedObject#getClassId()
    *<pre>
    * Implementation Note:
    *   Abstract to force each subclass to explicitly specify its class id.
    *   If a subclass doesn't care about class id, it can call getNullClassId() 
    *   from its override.
    *</pre>
    **************************************************************************/
    public abstract int getClassId();

    /**************************************************************************
    *@see AbstractStandardTableBackedObject#getCLASS_OF_OBJECTS()
    *<pre>
    * Implementation Note:
    *   Abstract to force each subclass to explicitly specify an instance of 
    *   itself to represent its entire class.
    *</pre>
    **************************************************************************/
    public abstract AbstractStandardTableBackedObject getCLASS_OF_OBJECTS();

    /**************************************************************************
    *@see AbstractStandardTableBackedObject#getTableName()
    *<pre>
    * Implementation Note:
    *   Abstract to force each subclass to explicitly specify its table name.
    *</pre>
    **************************************************************************/
    public abstract String getTableName();

    /**************************************************************************
    * Get the internal category.
    *@return The internal category.
    **************************************************************************/
    public AbstractDictionaryEntry getInternalCategory()
    {
        return m_internalCategory;
    }

    /**************************************************************************
    * Set the internal category.  This value is typically hidden from the user
    * and used internally to partition the entries in the dictionary into 
    * categories like those that are used internally by the system and those 
    * that are visible to the users.  The categories themselves are additional
    * dictionary entries.  Multiple user visible categories typically have the
    * same value for internal category, but different values for category.
    *@param  objVal     The value to set.
    **************************************************************************/
    public void setInternalCategory(AbstractDictionaryEntry objVal)
    {
        m_internalCategory = objVal;
    }

    /**************************************************************************
    * Get the category.
    *@return The category.
    **************************************************************************/
    public String getCategory()
    {
        return m_strCategory;
    }

    /**************************************************************************
    * Set the category.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setCategory(String strVal)
    {
        m_strCategory = strVal;
    }

    /**************************************************************************
    * Get the term.
    *@return The term.
    **************************************************************************/
    public String getTerm()
    {
        return m_strTerm;
    }

    /**************************************************************************
    * Set the term.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setTerm(String strVal)
    {
        m_strTerm = strVal;
    }

    /**************************************************************************
    * Get the definition.
    *@return The definition.
    **************************************************************************/
    public String getDefinition()
    {
        return m_strDefinition;
    }

    /**************************************************************************
    * Set the definition.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setDefinition(String strVal)
    {
        m_strDefinition = strVal;
    }

    /**************************************************************************
    * Get the notes.
    *@return The notes.
    **************************************************************************/
    public String getNotes()
    {
        return m_strNotes;
    }

    /**************************************************************************
    * Set the notes.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setNotes(String strVal)
    {
        m_strNotes = strVal;
    }
}
