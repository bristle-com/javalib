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

// DisplayDataInterface
/******************************************************************************
* This interface requires implementing classes to provide methods to get the
* display data about the properties of a bean.
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
public interface DisplayDataInterface
{
    /**************************************************************************
    * Get the display data.
    * <pre>
    * Implementation Note:
    *   Can't make this method static for 2 reasons:
    *   1. Static methods can't be called for beans via JSP/JSTL EL 
    *      expressions, and the whole purpose of this method is to be 
    *      called from there.
    *   2. Java doesn't support having static methods in an interface
    *      or an abstract class.
    * </pre>
    *@return The display data.
    **************************************************************************/
    public ObjectDisplayDataMap getDisplayData();
}
