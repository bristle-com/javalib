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

import java.util.Collection;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

// MapBean
/******************************************************************************
* This class encapsulates a Map in a Java Bean so that it can accessed 
* as a bean from JSP/JSTL EL expressions, and in a Collection so that it 
* can be iterated by the JSTL <c:forEach> tag.  All this requires is some
* additional Bean-style method names that invoke existing non-Bean-style 
* methods.  The Collection methods operate on the collection of entries in
* the map.
* 
*<pre>
* Note:  Can't get it to work.  For some reason, all the lines I could think
*        to try like:
*           <xmp>
*           <jsp:useBean id="userIdAccessAttrMapBean" 
*                        class="com.infratrac.itf.bean.UserIdAccessAttrMapBean" 
*                        scope='page'
*           <c:forEach items="${userIdAccessAttrMapBean.values}" var="val">
*           <c:forEach items="${userIdAccessAttrMapBean.iterator}" var="entry">
*           <c:forEach items="${userIdAccessAttrMapBean.entrySet.iterator}" var="entry">
*           </xmp>
*        iterate zero times.  What am I doing wrong?
*
* Note:  May not be necessary anyhow, since forEach is supposed to iterate
*        the entries of Maps even without this class, according to:
*           http://www.theserverside.com/discussions/thread.tss?thread_id=35477
*        and:
*           http://www.ibm.com/developerworks/java/library/j-jstl0318/
*
*        So, why is it not working for me?  When I iterate over the same 
*        object, in the same page with the following Java code instead, it
*        works fine:
*           <xmp>
*           <%
*           for (Iterator i = userIdAccessAttrMapBean.getIterator(); i.hasNext();)
*           {
*               Map.Entry      entry = (Map.Entry) i.next();
*               String         key   = (String)entry.getKey();
*               AccessAttrBean attr  = (AccessAttrBean)entry.getValue();
*               String         role  = attr.getName();
*               logutil.log("JSP Username = " + key);
*               logutil.log("JSP Role     = " + role);
*           }
*           %>
*           </xmp>
*
*<b>Usage:</b>
*
*   - The typical scenario for using this class is:
*           <xmp>
*           <jsp:useBean id='myMapBean' 
*                        class='com.bristle.javalib.bean.MapBean' 
*           />
*           <% myMapBean.setMap(myMap); // No way to do this line in JSP? %>
*           <c:forEach items="${myMapBean.entrySet}" var="entry">
*             <tr>
*               <td>${entry.key.name}</td>
*               <td>${entry.value.name}</td>
*             </tr>
*           </c:forEach>
*           <c:forEach items="${myMapBean.iterator}" var="entry">
*             <tr>
*               <td>${entry.key.name}</td>
*               <td>${entry.value.name}</td>
*             </tr>
*           </c:forEach>
*           <c:forEach items="${myMapBean.keySet}" var="key">
*             <tr><td>${key.name}</td></tr>
*           </c:forEach>
*           <c:forEach items="${myMapBean.values}" var="value">
*             <tr><td>${value.name}</td></tr>
*           </c:forEach>
*           ${myMapBean.containsKey}
*           ${myMapBean.containsValue}
*           ${myMapBean.hashCode}
*           ${myMapBean.size}
*           ${myMapBean.toString}
*           ${myMapBean.empty}
*           ${myMapBean.toArray}
*           ${myMapBean.map}
*           </xmp>
*
*<b>Assumptions:</b>
*<b>Effects:</b>
*       - None.
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*       - Cannot implement both interfaces Map and Collection because they 
*         have incompatible versions of remove(Object).  See the comments
*         at remove(Object) below for details.
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class MapBean implements Map // Not also Collection or Set
{

    //
    // Class variables
    //

    //
    // Instance variables to support public properties
    //
    private Map m_map = new TreeMap();
    
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
    * Default Constructor.  
    * Note: This constructor with no params is required for the class to be a
    *       Java Bean, so that it can be accessed via <xmp><jsp:useBean></xmp>.
    *       With no params, this constructor cannot initialize the wrapped Map.
    *       Therefore, m_map can't be a "blank final", so we initialize it to 
    *       a Map to support all the methods that use it,
    *       Also, we have a setter for it, so the caller can pass in an entire 
    *       map at once, like they can do with the non-default constructor.
    *       We have a getter also for completeness, even though it is really 
    *       not necessary since this class itself is a Map that could always 
    *       be used by any caller in place of the encapsulated Map.
    **************************************************************************/
    public MapBean()
    {
    }
    
    /**************************************************************************
    * Constructor.
    *
    *@param map The Map to encapsulate.
    **************************************************************************/
    public MapBean(Map map)
    {
        m_map = map;
    }
    
    /**************************************************************************
    * Return the encapsulated Map.
    *
    *@return The encapsulated Map.
    **************************************************************************/
    public Map getMap()
    {
        return m_map;
    }
    
    /**************************************************************************
    * Set the encapsulated Map.
    *
    *@param map The Map to encapsulate.
    **************************************************************************/
    public void setMap(Map map)
    {
        m_map = map;
    }
    

    ///////////////////////////////////////////////////////////////////////////
    // Methods of encapsulated Map.
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
    * Calls {@link Map#equals(Object)}
    **************************************************************************/
    public boolean equals(Object obj)
    {
        return m_map.equals(obj);
    }

    /**************************************************************************
    * Calls {@link Map#hashCode()}
    **************************************************************************/
    public int hashCode()
    {
        return m_map.hashCode();
    }

    /**************************************************************************
    * Calls {@link TreeMap#toString()}
    **************************************************************************/
    public String toString()
    {
        return m_map.toString();
    }

    /**************************************************************************
    * Calls {@link Map#clear()}
    **************************************************************************/
    public void clear()
    {
        m_map.clear();        
    }

    /**************************************************************************
    * Calls {@link Map#isEmpty()}
    **************************************************************************/
    public boolean isEmpty()
    {
        return m_map.isEmpty();
    }

    /**************************************************************************
    * Calls {@link Map#size()}
    **************************************************************************/
    public int size()
    {
        return m_map.size();
    }

    /**************************************************************************
    * Calls {@link Map#containsKey(Object)}
    **************************************************************************/
    public boolean containsKey(Object arg0)
    {
        return m_map.containsKey(arg0);
    }

    /**************************************************************************
    * Calls {@link Map#containsValue(Object)}
    **************************************************************************/
    public boolean containsValue(Object arg0)
    {
        return m_map.containsValue(arg0);
    }

    /**************************************************************************
    * Calls {@link Map#entrySet()}
    **************************************************************************/
    public Set entrySet()
    {
        return m_map.entrySet();
    }

    /**************************************************************************
    * Calls {@link Map#get(Object)}
    **************************************************************************/
    public Object get(Object key)
    {
        return m_map.get(key);
    }

    /**************************************************************************
    * Calls {@link Map#keySet()}
    **************************************************************************/
    public Set keySet()
    {
        return m_map.keySet();
    }

    /**************************************************************************
    * Calls {@link Map#put(Object, Object)}
    **************************************************************************/
    public Object put(Object arg0, Object arg1)
    {
        return m_map.put(arg0, arg1);
    }

    /**************************************************************************
    * Calls {@link Map#putAll(Map)}
    **************************************************************************/
    public void putAll(Map arg0)
    {
        m_map.putAll(arg0);
    }

    /**************************************************************************
    * Calls {@link Map#values()}
    **************************************************************************/
    public Collection values()
    {
        return m_map.values();
    }


    ///////////////////////////////////////////////////////////////////////////
    // Methods of entrySet Collection of encapsulated Map.
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
    * Calls {@link Set#add(Object)} on the Set returned by 
    * {@link Map#entrySet()}
    **************************************************************************/
    public boolean add(Object arg0)
    {
        return m_map.entrySet().add(arg0);
    }

    /**************************************************************************
    * Calls {@link Set#addAll(Collection)} on the Set returned by 
    * {@link Map#entrySet()}
    **************************************************************************/
    public boolean addAll(Collection arg0)
    {
        return m_map.entrySet().addAll(arg0);
    }

    /**************************************************************************
    * Calls {@link Set#contains(Object)} on the Set returned by 
    * {@link Map#entrySet()}
    **************************************************************************/
    public boolean contains(Object arg0)
    {
        return m_map.entrySet().contains(arg0);
    }

    /**************************************************************************
    * Calls {@link Set#containsAll(Collection)} on the Set returned by 
    * {@link Map#entrySet()}
    **************************************************************************/
    public boolean containsAll(Collection arg0)
    {
        return m_map.entrySet().containsAll(arg0);
    }

    /**************************************************************************
    * Calls {@link Set#iterator()} on the Set returned by 
    * {@link Map#entrySet()}
    **************************************************************************/
    public Iterator iterator()
    {
        return m_map.entrySet().iterator();
    }

    /**************************************************************************
    * Calls {@link Set#remove(Object)} on the Set returned by 
    * {@link Map#entrySet()}
    **************************************************************************/
    public Object remove(Object arg0)
    {
        return m_map.remove(arg0);
    }


// Note:  Cannot also implement this method because it differs from the 
//        map version of remove(Object) only in its return type, and Java
//        does not allow overloaded methods to differ only in return type.
//        Therefore, this class cannot have both clauses "implements Map" 
//        and "implements Collection".
//    /**************************************************************************
//    * Calls {@link Set#remove(Object)} on the Set returned by 
//    * {@link Map#entrySet()}
//    **************************************************************************/
//    public boolean remove(Object arg0)
//    {
//        return m_map.entrySet().remove(arg0);
//    }

    /**************************************************************************
    * Calls {@link Set#removeAll(Collection)} on the Set returned by 
    * {@link Map#entrySet()}
    **************************************************************************/
    public boolean removeAll(Collection arg0)
    {
        return m_map.entrySet().removeAll(arg0);
    }

    /**************************************************************************
    * Calls {@link Set#retainAll(Collection)} on the Set returned by 
    * {@link Map#entrySet()}
    **************************************************************************/
    public boolean retainAll(Collection arg0)
    {
        return m_map.entrySet().retainAll(arg0);
    }

    /**************************************************************************
    * Calls {@link Set#toArray()} on the Set returned by 
    * {@link Map#entrySet()}
    **************************************************************************/
    public Object[] toArray()
    {
        return m_map.entrySet().toArray();
    }

    /**************************************************************************
    * Calls {@link Set#toArray(Object[])} on the Set returned by 
    * {@link Map#entrySet()}
    **************************************************************************/
    public Object[] toArray(Object[] arg0)
    {
        return m_map.entrySet().toArray(arg0);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Renamed Bean-style methods.
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
    * Calls {@link #hashCode()}
    **************************************************************************/
    public int getHashCode()
    {
        return hashCode();
    }

    /**************************************************************************
    * Calls {@link #toString()}
    **************************************************************************/
    public String getToString()
    {
        return toString();
    }

    /**************************************************************************
    * Calls {@link #size()}
    **************************************************************************/
    public int getSize()
    {
        return size();
    }

    /**************************************************************************
    * Calls {@link #containsKey(Object)}
    **************************************************************************/
    public boolean getContainsKey(Object arg0)
    {
        return containsKey(arg0);
    }

    /**************************************************************************
    * Calls {@link #containsValue(Object)}
    **************************************************************************/
    public boolean getContainsValue(Object arg0)
    {
        return containsValue(arg0);
    }

    /**************************************************************************
    * Calls {@link #entrySet()}
    **************************************************************************/
    public Set getEntrySet()
    {
        return entrySet();
    }

    /**************************************************************************
    * Calls {@link #keySet()}
    **************************************************************************/
    public Set getKeySet()
    {
        return keySet();
    }

    /**************************************************************************
    * Calls {@link #values()}
    **************************************************************************/
    public Collection getValues()
    {
        return values();
    }

    /**************************************************************************
    * Calls {@link #iterator()}
    **************************************************************************/
    public Iterator getIterator()
    {
        return iterator();
    }

    /**************************************************************************
    * Calls {@link #toArray()}
    **************************************************************************/
    public Object[] getToArray()
    {
        return toArray();
    }

}
