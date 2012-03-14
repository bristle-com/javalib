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

import java.util.Iterator;
import java.util.List;

import com.bristle.javalib.bean.MapBean;

// SecurableObjectMapBean
/******************************************************************************
* This bean class carries a MapBean which is a map of Securable ids to 
* Securable objects.
*<pre>
*<b>Usage:</b>
*
*   - The typical scenarios for using this class are:
*     - To initialize it from a List of Securables:
*           MapBean mapBean;
*           mapBean = new SecurableObjectMapBean(securableListBean)
*     - To access it, see scenarios in {@link MapBean}.
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
public class SecurableObjectMapBean extends MapBean
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
    * Constructor.
    *
    *@param list The List to copy from.  Does a shallow copy, so the items 
    *            that are in the List when this constructor is called are 
    *            shared between the List and the SecurableObjectMapBean.
    *            Future changes to any of those items will be reflected in 
    *            both.  However, adding or deleting items from one will not
    *            affect the other.
    **************************************************************************/
    public SecurableObjectMapBean(List list)
    {
        for (Iterator iter = list.iterator(); iter.hasNext();)
        {
            Securable securable = (Securable)iter.next();
            put(new Integer(securable.getId()), securable);
        }
    }
}
