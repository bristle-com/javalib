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

import javax.servlet.ServletContext;

// ServletContextLoggerTarget
/******************************************************************************
* This class implements the Logger.LoggerTarget interface, writing log entries
* to a ServletContext (Web server log file).
*<pre>
*<b>Usage:</b>
*   - See the Logger class.
*<b>Assumptions:</b>
*<b>Effects:</b>
*   - Writes log entries to the specified ServletContext.
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class ServletContextLoggerTarget implements Logger.LoggerTarget
{

    //--
    //-- Class variables
    //--

    //--
    //-- Instance variables to support public properties
    //--
    private ServletContext m_objServletContext = null;

    //--
    //-- Internal instance variables
    //--

    /**************************************************************************
    * Constructor.
    *@param  context            The ServletContext to write log entries to.
    **************************************************************************/
    public ServletContextLoggerTarget(ServletContext context)
    {
        m_objServletContext = context;
    }

    /**************************************************************************
    * Set the ServletContext to write log entries to.  If null, no logging to 
    * a ServletContext is performed.
    *@param  context        The new ServletContext.
    **************************************************************************/
    public void setServletContext(ServletContext context)
    {
        m_objServletContext = context;
    }

    /**************************************************************************
    * Get the ServletContext that messages are currently being logged to.
    *@return            The ServletContext.
    **************************************************************************/
    public ServletContext getServletContext()
    {
        return m_objServletContext;
    }

    /**************************************************************************
    * Log the log entry to the ServletContext.
    *@param  entry          The log entry to write to the log.
    **************************************************************************/
    public void log(Logger.Entry entry) 
    {
        if (m_objServletContext != null)
        {
            // Log each line separately because the ServletContext may 
            // decorate each line with a prefix or something.
            String strMultiLineEntry = entry.getFormattedLogLine();
            String[] arrEntries = strMultiLineEntry.split("\n");
            for (int i=0; i < arrEntries.length; i++)
            {
                // Strip out passwords simplistically, by truncating any 
                // line at the first occurrence of the word "password".
                // Not 100% reliable, but tends to get all echoed SQL 
                // queries using fields named password in their WHERE
                // clauses, which is the biggest problem (logins).  
                // Also, tends to get SQL UPDATE statements with fields 
                // named password (password changes).  Notably, does not
                // get INSERTS, unless the SQL is all on one line.
                // Good enough for now.
                String strLine = arrEntries[i];
                int intPasswordStartPos = strLine.indexOf("password");
                if (intPasswordStartPos >= 0)
                {
                    strLine = strLine.substring(0,intPasswordStartPos)
                            + "[password lines not logged]";
                }
                m_objServletContext.log(strLine);
            }
        }
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
                System.out.println ("Begin tests...");
                System.out.println ("...End tests.");
            }
            catch (Throwable e)
            {
                System.out.println("Error in main(): ");
                e.printStackTrace();
            }
        }
    }
}
