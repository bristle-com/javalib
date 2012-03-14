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

package com.bristle.javalib.net.http;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// ServletDebugger
/******************************************************************************
* This class contains utility routines for interacting with the HTTP protocol.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*
*     - To enable the ServletDebugger for an HttpServlet, add the following 
*       line of code to the top of the servlet's service() method:
*           if (ServletDebugger.intercept(this, request, response)) return;
*       This causes the ServletDebugger to examine the HTTP request, and 
*       decide whether to take action.  If so, it generates an HTTP response
*       (setting the content-type, writing to the response Writer, etc.) and
*       returns true.  Returning true causes the calling servlet to return 
*       to its caller without doing anything. 
*
*     - To activate an enabled ServletDebugger for an HTTP request, specify
*       the following HTTP parameter (typically in the URL string)  
*           bristleDebug
*
*     - To force activation of the ServletDebugger, even if the HTTP parameters
*       didn't request it:
*           ServletDebugger.invoke(this, request, response);
*
*?? Add other params that can cause other actions.
*<b>Assumptions:</b>
*<b>Effects:</b>
*       - Intercepts requests to a servlet, sending its own response to the 
*         HTTP client while running in the original servlet's Session.  Can
*         modify values of attributes in the Session, the ServletContext, 
*         etc.  ??Not yet??
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class ServletDebugger
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

    /**************************************************************************
    * Name of the HTTP parameter used to active the ServletDebugger.
    **************************************************************************/
    public static final String strACTIVATION_PARAM_NAME = "bristleDebug";


    /**************************************************************************
    * Examine the specified HTTP request, and decide whether to invoke the
    * "servlet debugger", returning true if invoked; false otherwise.  
    * See invoke() for more details.  
    *@param  servlet        The HttpServlet object defining the servlet.
    *@param  request        The HttpServletRequest object of the servlet.
    *@param  response       The HttpServletResponse object of the servlet.
    *@return                true if invoked; false otherwise.
    *@throws IOException    When an I/O error occurs during interaction 
    *                       with the servlet, request, or response.
    **************************************************************************/
    public static boolean intercept
                        (HttpServlet         servlet,
                         HttpServletRequest  request,
                         HttpServletResponse response)
                throws IOException
    {
        if (HttpUtil.getParamPresent(request, strACTIVATION_PARAM_NAME))
        {
            invoke(servlet, request, response);
            return true;
        }
        return false;
    }

    /**************************************************************************
    * Invoke the "servlet debugger".  For now, this simply means to write as an 
    * XML stream to the HTTP client all information available to the servlet.  
    * Format of XML is as shown in HttpUtil.writeInfoAvailableToServlet. 
    *@param  servlet        The HttpServlet object defining the servlet.
    *@param  request        The HttpServletRequest object of the servlet.
    *@param  response       The HttpServletResponse object of the servlet.
    *@throws IOException    When an I/O error occurs during interaction 
    *                       with the servlet, request, or response.
    **************************************************************************/
    public static void invoke
                        (HttpServlet         servlet,
                         HttpServletRequest  request,
                         HttpServletResponse response)
                throws IOException
    {
        HttpUtil.writeInfoAvailableToServlet(servlet, request, response);
    }

    /**************************************************************************
    * Each class contains a Tester inner class with a main() for easier
    * unit testing.  To call main from the command line, use:
    * <pre>
    *   java class$Tester
    *</pre>
    * where "class" is the name of the outer class.
    **************************************************************************/
    public static class Tester
    {
        /**********************************************************************
        * Main testing method.
        *@param  args       Array of command line argument strings
        **********************************************************************/
        public static void main(String[] args)
        {
            try
            {
                System.out.println("Begin tests...");
                System.out.println("...End tests.");
            }
            catch (Throwable e)
            {
                System.out.println("Error in main(): ");
                e.printStackTrace();
            }
        }
    }
}
