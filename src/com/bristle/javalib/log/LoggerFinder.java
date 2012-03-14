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

package com.bristle.javalib.log;

import com.bristle.javalib.net.http.HttpUtil;
import com.bristle.javalib.util.ObjUtil;

import javax.servlet.http.HttpServletRequest;

// LoggerFinder
/******************************************************************************
* This class makes it possible for Java servlets to access an existing 
* Logger that may be stored in the servlet's Session context, 
* the servlet's ServletContext (application context), or as the Logger 
* global singleton.
*<pre>
*<b>Usage:</b>
*       - The typical scenario for using this class is:
*         - To find a Logger:
*             LoggerFinder finder = new LoggerFinder(request);
*             Logger logger = finder.find();
*         - To save a Logger to be found by future calls to find(): 
*             finder.saveInSession(logger);
*             finder.saveInServletContext(logger)
*             finder.saveAsGlobalSingleton(logger)  
*<b>Assumptions:</b>
*<b>Effects:</b>
*       - Can update the servlet's Session context, ServletContext, and/or
*         the Logger global singleton.
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class LoggerFinder
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
    private final String strLOGGER_ATTRIBUTE_NAME 
                        = ObjUtil.getClassName(this) + ".cachedLogger"; 
    
    /**************************************************************************
    * Constructor.
    *@param  request    The HttpServletRequest used to locate the contexts to 
    *                   be searched for the Logger.
    **************************************************************************/
    public LoggerFinder(HttpServletRequest request)
    {
        m_request = request;
    }

    /**************************************************************************
    * Find an existing Logger, searching first the Session context 
    * of the previously specified HttpServletRequest (if any), then the 
    * ServletContext (application context) of the servlet associated with that
    * HttpServletRequest (if any), then the Logger global singleton 
    * (if any), returning null if no Logger is found in any of those 
    * places.
    *@return               The found Logger or null.
    **************************************************************************/
    public Logger find()
    {
        if (m_request == null)
        {
            return null;
        }
        Logger logger = (Logger)HttpUtil.getSessionAttribute
                                (m_request, strLOGGER_ATTRIBUTE_NAME);
        if (logger != null)
        {
            return logger;
        }
        logger = (Logger)HttpUtil.getServletAttribute
                                (m_request, strLOGGER_ATTRIBUTE_NAME);
        if (logger != null)
        {
            return logger;
        }
        logger = Logger.getSingleton();
        if (logger != null)
        {
            return logger;
        }
        return null; 
    }

    /**************************************************************************
    * Find an existing Logger, suppressing all possible errors.  It is safe to 
    * call this method from an exception handler or finally clause without fear 
    * of throwing another exception.
    * Find an existing Logger, searching first the Session context 
    * of the previously specified HttpServletRequest (if any), then the 
    * ServletContext (application context) of the servlet associated with that
    * HttpServletRequest (if any), then the Logger global singleton 
    * (if any), returning null if no Logger is found in any of those 
    * places, and returning null if any error occurs.
    *@return               The found Logger or null.
    **************************************************************************/
    public Logger findSafely()
    {
        try
        {
            return find();
        }
        catch (Throwable eSuppressed)
        {
            return null;
        }
    }

    /**************************************************************************
    * Save the specified Logger in the Session to be found by future
    * calls to find().
    *@param  logger       The Logger.
    **************************************************************************/
    public void saveInSession(Logger logger)
    {
        HttpUtil.setSessionAttribute
                        (m_request, strLOGGER_ATTRIBUTE_NAME, logger);
    }

    /**************************************************************************
    * Save the specified Logger in the ServletContext to be found by 
    * future calls to find().
    *@param  logger       The Logger.
    **************************************************************************/
    public void saveInServletContext(Logger logger)
    {
        HttpUtil.setServletAttribute
                        (m_request, strLOGGER_ATTRIBUTE_NAME, logger);
    }

    /**************************************************************************
    * Save the specified Logger as the Logger global singleton, 
    * to be found by future calls to find().  Note:  Since the Logger
    * singleton is global, this affects all users of all Logger 
    * instances, not only users of LoggerFinder.
    *@param  logger       The Logger.
    **************************************************************************/
    public void saveAsGlobalSingleton(Logger logger)
    {
        Logger.setSingleton(logger);
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
