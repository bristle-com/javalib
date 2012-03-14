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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

// DisplayDataTypeEnum
/******************************************************************************
* This class encapsulates the concept of an data type for displayable
* data, offering an enumerated type, comparison operators, toString(), the 
* ability to retrieve a Collection of all values, and various other utility 
* functions.
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
public class DisplayDataTypeEnum implements Serializable, Comparable {
    
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
    **************************************************************************/
    private static final long serialVersionUID = 1L;

    // Note:  These are all "blank finals" which requires them to be assigned 
    //        in the constructor.
    private final String m_strValue; 
    private final String m_strDescrip;   
   
    /**************************************************************************
    * Constructor (private, so no more values can be created)
    *@param strValue     The coded string value of the DisplayDataTypeEnum
    *@param strDescrip   A unique human-readable description of the 
    *                    DisplayDataTypeEnum.
    **************************************************************************/
    private DisplayDataTypeEnum(String strValue, String strDescrip)
    {
        m_strValue   = strValue;
        m_strDescrip = strDescrip;
    }
    
    // Enumeration values
    public static final DisplayDataTypeEnum STRING 
            = new DisplayDataTypeEnum("STRING", "String data type");
    public static final DisplayDataTypeEnum PASSWORD_STRING 
            = new DisplayDataTypeEnum
                            ("PASSWORD_STRING", "Password string data type");
    public static final DisplayDataTypeEnum DICT_STRING 
            = new DisplayDataTypeEnum
                            ("DICT_STRING", "Dictionary string data type");
    public static final DisplayDataTypeEnum INTEGER 
            = new DisplayDataTypeEnum("INTEGER", "Integer data type");
    public static final DisplayDataTypeEnum FLOAT 
            = new DisplayDataTypeEnum("FLOAT", "Floating point data type");
    public static final DisplayDataTypeEnum DATE_TIME 
            = new DisplayDataTypeEnum("DATE_TIME", "Date/time data type");
    
    // Map used internally for conversions and to support 
    // getAllDisplayDataTypeEnums() and getDisplayDataTypeEnum(String).
    // Note:  Use TreeMap, not just HashMap, so getAllDisplayDataTypeEnums()
    //        returns an ordered list of values.
    private static final Map st_mapALL_DISPLAY_DATA_TYPES = new TreeMap();
    static {
        st_mapALL_DISPLAY_DATA_TYPES.put
                            (STRING.getValue(),
                             STRING);
        st_mapALL_DISPLAY_DATA_TYPES.put
                            (PASSWORD_STRING.getValue(),
                             PASSWORD_STRING);
        st_mapALL_DISPLAY_DATA_TYPES.put
                            (DICT_STRING.getValue(),
                             DICT_STRING);
        st_mapALL_DISPLAY_DATA_TYPES.put
                            (INTEGER.getValue(),
                             INTEGER);
        st_mapALL_DISPLAY_DATA_TYPES.put
                            (FLOAT.getValue(),
                             FLOAT);
        st_mapALL_DISPLAY_DATA_TYPES.put
                            (DATE_TIME.getValue(),
                             DATE_TIME);
    }

    /**************************************************************************
    * Thrown when a specified value does not match any DisplayDataTypeEnum
    **************************************************************************/
    public static class NoSuchDisplayDataTypeEnumException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public NoSuchDisplayDataTypeEnumException(String msg) { super(msg); }
    }

    /**************************************************************************
    * The coded string value of the DisplayDataTypeEnum.
    **************************************************************************/
    public String getValue() 
    {
        return m_strValue;
    }

    /**************************************************************************
    * A unique human-readable description of the DisplayDataTypeEnum.
    **************************************************************************/
    public String getDescrip() 
    {
        return m_strDescrip;
    }

    /**************************************************************************
    * Get a DisplayDataTypeEnum based on its coded string value.
    *@param  strValue      The coded string value.
    *@throws NoSuchDisplayDataTypeEnumException 
    *                      when coded string value is invalid
    **************************************************************************/
    public static DisplayDataTypeEnum getDisplayDataTypeEnum(String strValue) 
                            throws NoSuchDisplayDataTypeEnumException
    {
        DisplayDataTypeEnum rc = (DisplayDataTypeEnum) 
                            st_mapALL_DISPLAY_DATA_TYPES.get(strValue);
        if (rc == null)
        {
            throw new NoSuchDisplayDataTypeEnumException
                  ("There is no DisplayDataTypeEnum with value " + strValue);
        }
        return rc;
    }

    /**************************************************************************
    * Returns a string representation of the DisplayDataTypeEnum.  
    * Useful for debugging.
    **************************************************************************/
    public String toString()
    {
        return m_strValue + " " + m_strDescrip;
    }
    
    /**************************************************************************
    * Returns a collection of all DisplayDataTypeEnums, sorted by coded string 
    * getValue() values.
    **************************************************************************/
    public static Collection getAllDisplayDataTypeEnums() 
    {
        return st_mapALL_DISPLAY_DATA_TYPES.values();
    }
    
    /**************************************************************************
    * Compares other objects to this DisplayDataTypeEnum
    *@param  arg0      The object to compare with.
    *@return 1 if equal, 0 otherwise.
    **************************************************************************/
    public int compareTo(Object arg0) 
    {
        return equals(arg0) ? 1 : 0;
    }
}
