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

package com.bristle.javalib.bean;

import com.bristle.javalib.util.MathUtil;

// MathBean
/******************************************************************************
* This class encapsulates mathematical functions in a Java Bean so they can 
* be accessed as beans from JSP/JSTL EL expressions.
*
*<b>Usage:</b>
*
*   - The typical scenario for using this class is:
*           <xmp>
*           <jsp:useBean id='myMathBean' 
*                        class='com.bristle.javalib.bean.MathBean' 
*           />
            <img src='getImage?name='image1.jpg&forceReload=${myMathBean.randomInt}'>
*           </xmp>
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
public class MathBean
{

    //
    // Class variables
    //

    //
    // Instance variables to support public properties
    //
    private int m_intMinRandomInt = 1;
    private int m_intMaxRandomInt = Integer.MAX_VALUE;
    
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
    * Get a random int.
    *
    *@return The random int
    **************************************************************************/
    public int getRandomInt()
    {
        return MathUtil.getRandomInt(m_intMinRandomInt, m_intMaxRandomInt);
    }
    
    /**************************************************************************
    * Get the min random int.
    **************************************************************************/
    public int getMinRandomInt()
    {
        return m_intMinRandomInt;
    }

    /**************************************************************************
    * Set the min random int.
    **************************************************************************/
    public void setMinRandomInt(int intVal)
    {
        m_intMinRandomInt = intVal;
    }

    /**************************************************************************
    * Get the max random int.
    **************************************************************************/
    public int getMaxRandomInt()
    {
        return m_intMaxRandomInt;
    }

    /**************************************************************************
    * Set the max random int.
    **************************************************************************/
    public void setMaxRandomInt(int intVal)
    {
        m_intMaxRandomInt = intVal;
    }
}
