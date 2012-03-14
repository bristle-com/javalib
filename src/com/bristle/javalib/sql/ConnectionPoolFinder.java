// Copyright (C) 2005-2012 Bristle Software, Inc.
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

package com.bristle.javalib.sql;

import com.bristle.javalib.net.http.HttpUtil;
import com.bristle.javalib.util.ObjUtil;

import javax.servlet.http.HttpServletRequest;

// ConnectionPoolFinder
/******************************************************************************
* This class makes it possible for Java servlets to access an existing 
* ConnectionPool that may be stored in the servlet's Session context, 
* the servlet's ServletContext (application context), or as the ConnectionPool 
* global singleton.
*<pre>
*<b>Usage:</b>
*       - The typical scenario for using this class is:
*         - To find a ConnectionPool:
*             ConnectionPoolFinder finder = new ConnectionPoolFinder(request);
*             ConnectionPool pool = finder.find();
*         - To save a ConnectionPool to be found by future calls to find(): 
*             finder.saveInSession(pool);
*             finder.saveInServletContext(pool)
*             finder.saveAsGlobalSingleton(pool)  
*<b>Assumptions:</b>
*<b>Effects:</b>
*       - Can update the servlet's Session context, ServletContext, and/or
*         the ConnectionPool global singleton.
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class ConnectionPoolFinder
{

    //--
    //-- Class variables
    //--

    //--
    //-- Instance variables to support public properties
    //--

    //--
    //-- Internal instance variables
    //--
    private HttpServletRequest m_request = null;

    //--
    //-- Local constants
    //--
    private final String strCONNECTION_POOL_ATTRIBUTE_NAME 
                        = ObjUtil.getClassName(this) + ".CachedConnectionPool"; 
    
    /**************************************************************************
    * Constant for use as parameter to constructor. 
    **************************************************************************/
    public static final HttpServletRequest poolFIND_SINGLETON_ONLY = null; 

    /**************************************************************************
    * Constructor.
    *@param  request    The HttpServletRequest used to locate the contexts to 
    *                   be searched for the ConnectionPool, or null to cause
    *                   find to skip directly to the singleton when searching. 
    **************************************************************************/
    public ConnectionPoolFinder(HttpServletRequest request)
    {
        m_request = request;
    }

    /**************************************************************************
    * Find an existing ConnectionPool, searching first the Session context 
    * of the previously specified HttpServletRequest (if any), then the 
    * ServletContext (application context) of the servlet associated with that
    * HttpServletRequest (if any), then the ConnectionPool global singleton 
    * (if any), returning null if no ConnectionPool is found in any of those 
    * places.
    *@return               The found ConnectionPool or null.
    **************************************************************************/
    public ConnectionPool find()
    {
        if (m_request != null)
        {
            ConnectionPool pool = null;
            pool = (ConnectionPool)HttpUtil.getSessionAttribute
                                (m_request, strCONNECTION_POOL_ATTRIBUTE_NAME);
            if (pool != null)
            {
                return pool;
            }
            pool = (ConnectionPool)HttpUtil.getServletAttribute
                                (m_request, strCONNECTION_POOL_ATTRIBUTE_NAME);
            if (pool != null)
            {
                return pool;
            }
        }
        return ConnectionPool.getSingleton();
    }

    /**************************************************************************
    * Save the specified ConnectionPool in the Session to be found by future
    * calls to find().
    *@param  pool       The ConnectionPool.
    **************************************************************************/
    public void saveInSession(ConnectionPool pool)
    {
        HttpUtil.setSessionAttribute
                        (m_request, strCONNECTION_POOL_ATTRIBUTE_NAME, pool);
    }

    /**************************************************************************
    * Save the specified ConnectionPool in the ServletContext to be found by 
    * future calls to find().
    *@param  pool       The ConnectionPool.
    **************************************************************************/
    public void saveInServletContext(ConnectionPool pool)
    {
        HttpUtil.setServletAttribute
                        (m_request, strCONNECTION_POOL_ATTRIBUTE_NAME, pool);
    }

    /**************************************************************************
    * Save the specified ConnectionPool as the ConnectionPool global singleton, 
    * to be found by future calls to find().  Note:  Since the ConnectionPool
    * singleton is global, this affects all users of all ConnectionPool 
    * instances, not only users of ConnectionPoolFinder.
    *@param  pool       The ConnectionPool.
    **************************************************************************/
    public void saveAsGlobalSingleton(ConnectionPool pool)
    {
        ConnectionPool.setSingleton(pool);
    }

    /**************************************************************************
    * Each class contains a Tester inner class with a main() for easier
    * unit testing.  To call main from the command line, use:
    * <pre>
    *   java class$Tester
    *</pre>
    * where "class" is the name of the outer class.
    *<pre>
    *<b>Anticipated Changes:</b>
    *      None.
    *</pre>
    **************************************************************************/
    public static class Tester
    {
        /**********************************************************************
        * Main testing method.
        *<pre>
        *<b>Anticipated Changes:</b>
        *      None.
        *</pre>
        *@param  args       Array of command line argument strings
        **********************************************************************/
        public static void main(String[] args)
        {
            System.out.println ("Begin tests...");
            System.out.println ("...End tests.");
        }
    }
}
