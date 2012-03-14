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

// PropertyDisplayData
/******************************************************************************
* This class carries the display data about one property of a displayable
* object.
*<pre>
*<b>Usage:</b>
*   - See ObjectDisplayDataMap for typical usage.
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
public class PropertyDisplayData
{

    // Constants for readability.
    public static final boolean blnREQUIRED = true;
    public static final boolean blnREADONLY = true;
    
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

    // Note:  These are all "blank finals" which requires them to be assigned 
    //        in the constructor.
    private final String                  m_strName;
    private final DisplayDataTypeEnum     m_dataType;
    private final String                  m_strDataTypeMessage;
    private final boolean                 m_blnRequired;
    private final String                  m_strRequiredMessage;
    private final boolean                 m_blnReadOnly;
    private final String                  m_strReadOnlyMessage;
    private final String                  m_strPrompt;
    private final int                     m_intMinDisplayLength;
    private final int                     m_intMaxLength;
    private final String                  m_strMaxLengthMessage;
    private final String                  m_strDisallowedChars;
    private final String                  m_strDisallowedCharsMessage;
    private final float                   m_fltMinValue;
    private final String                  m_strMinValueMessage;
    private final float                   m_fltMaxValue;
    private final String                  m_strMaxValueMessage;

    /**************************************************************************
    * Constructor (the only way to set the values).
    *@param strName         The name of the property.
    *@param dataType        The data type of the property. 
    *@param strDataTypeMessage 
    *                       The message to show a user if the value of the 
    *                       property has the wrong data type.
    *@param blnRequired     Is the property required to have a value? 
    *@param strRequiredMessage
    *                       The message to show a user if the value of the 
    *                       property is empty.
    *@param blnReadOnly     Is the property readonly? 
    *@param strReadOnlyMessage
    *                       The message to show a user if the readonly value of 
    *                       the property is changed.
    *@param strPrompt       The prompt to show the user for this property. 
    *@param intMinDisplayLength 
    *                       The min length to show the user in a table or
    *                       any place where space is at a premium. 
    *@param intMaxLength    The max char length to let the user enter. 
    *@param strMaxLengthMessage
    *                       The message to show a user if the value of the 
    *                       property is too long.
    *@param strDisallowedChars 
    *                       Chars to prevent the user from entering as part of
    *                       the value.
    *@param strDisallowedCharsMessage
    *                       The message to show a user if the value of the 
    *                       property contains disallowed chars. 
    *@param fltMinValue     The min allowed value. 
    *@param strMinValueMessage
    *                       The message to show a user if the value of the 
    *                       property is less than the min.
    *@param fltMaxValue     The max allowed value. 
    *@param strMaxValueMessage
    *                       The message to show a user if the value of the 
    *                       property is more than the max.
    **************************************************************************/
    public PropertyDisplayData
                (String              strName
                ,DisplayDataTypeEnum dataType
                ,String              strDataTypeMessage 
                ,boolean             blnRequired
                ,String              strRequiredMessage
                ,boolean             blnReadOnly
                ,String              strReadOnlyMessage
                ,String              strPrompt
                ,int                 intMinDisplayLength
                ,int                 intMaxLength
                ,String              strMaxLengthMessage
                ,String              strDisallowedChars
                ,String              strDisallowedCharsMessage
                ,float               fltMinValue
                ,String              strMinValueMessage
                ,float               fltMaxValue
                ,String              strMaxValueMessage
                )
    {
        m_strName                       = strName;
        m_dataType                      = dataType;
        m_strDataTypeMessage            = strDataTypeMessage;
        m_blnRequired                   = blnRequired;
        m_strRequiredMessage            = strRequiredMessage;
        m_blnReadOnly                   = blnReadOnly;
        m_strReadOnlyMessage            = strReadOnlyMessage;
        m_strPrompt                     = strPrompt;
        m_intMinDisplayLength           = intMinDisplayLength;
        m_intMaxLength                  = intMaxLength;
        m_strMaxLengthMessage           = strMaxLengthMessage;
        m_strDisallowedChars            = strDisallowedChars;
        m_strDisallowedCharsMessage     = strDisallowedCharsMessage;
        m_fltMinValue                   = fltMinValue;
        m_strMinValueMessage            = strMinValueMessage;
        m_fltMaxValue                   = fltMaxValue;
        m_strMaxValueMessage            = strMaxValueMessage;
    }
    
    /**************************************************************************
    * Get the name.
    *@return The name.
    **************************************************************************/
    public String getName()
    {
        return m_strName;
    }

    /**************************************************************************
    * Get the data type.
    *@return The data type.
    **************************************************************************/
    public DisplayDataTypeEnum getDataType()
    {
        return m_dataType;
    }

    /**************************************************************************
    * Get the message to show a user if the value of the property has the 
    * wrong data type.
    *@return The message.
    **************************************************************************/
    public String getDataTypeMessage()
    {
        return m_strDataTypeMessage;
    }

    /**************************************************************************
    * Get the required flag.
    *@return The required flag.
    **************************************************************************/
    public boolean isRequired()
    {
        return m_blnRequired;
    }

    /**************************************************************************
    * Get the message to show a user if the value of the property is empty.
    *@return The message.
    **************************************************************************/
    public String getRequiredMessage()
    {
        return m_strRequiredMessage;
    }

    /**************************************************************************
    * Get the readonly flag.
    *@return The readonly flag.
    **************************************************************************/
    public boolean isReadOnly()
    {
        return m_blnReadOnly;
    }

    /**************************************************************************
    * Get the message to show a user if the readonly value of the property is 
    * changed.
    **************************************************************************/
    public String getReadOnlyMessage()
    {
        return m_strReadOnlyMessage;
    }

    /**************************************************************************
    * Get the prompt.
    *@return The prompt.
    **************************************************************************/
    public String getPrompt()
    {
        return m_strPrompt;
    }

    /**************************************************************************
    * Get the min display length.
    *@return The min display length.
    **************************************************************************/
    public int getMinDisplayLength()
    {
        return m_intMinDisplayLength;
    }

    /**************************************************************************
    * Get the max length.
    *@return The max length.
    **************************************************************************/
    public int getMaxLength()
    {
        return m_intMaxLength;
    }

    /**************************************************************************
    * Get the message to show a user if the value of the property is too long.
    *@return The message.
    **************************************************************************/
    public String getMaxLengthMessage()
    {
        return m_strMaxLengthMessage;
    }

    /**************************************************************************
    * Get the disallowed chars.
    *@return The disallowed chars.
    **************************************************************************/
    public String getDisallowedChars()
    {
        return m_strDisallowedChars;
    }

    /**************************************************************************
    * Get the message to show a user if the value of the property contains 
    * disallowed chars. 
    *@return The message.
    **************************************************************************/
    public String getDisallowedCharsMessage()
    {
        return m_strDisallowedCharsMessage;
    }

    /**************************************************************************
    * Get the min value.
    *@return The min value.
    **************************************************************************/
    public float getMinValue()
    {
        return m_fltMinValue;
    }

    /**************************************************************************
    * Get the message to show a user if the value of the property is less than
    * the min.
    *@return The message.
    **************************************************************************/
    public String getMinValueMessage()
    {
        return m_strMinValueMessage;
    }

    /**************************************************************************
    * Get the max value.
    *@return The max value.
    **************************************************************************/
    public float getMaxValue()
    {
        return m_fltMaxValue;
    }

    /**************************************************************************
    * Get the message to show a user if the value of the property is more than
    * the max.
    *@return The message.
    **************************************************************************/
    public String getMaxValueMessage()
    {
        return m_strMaxValueMessage;
    }

}
