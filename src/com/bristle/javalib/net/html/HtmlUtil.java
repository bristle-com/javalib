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

package com.bristle.javalib.net.html;

import com.bristle.javalib.util.ObjUtil;

// HtmlUtil
/******************************************************************************
* This class contains utility routines for interacting with the HTML documents.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*     - To get various HTML keywords and such:
*               strTarget = HtmlUtil.strTARGET_NEW_WINDOW;
*               strTarget = HtmlUtil.strANCHOR_TARGET_SELF;
*               strTarget = HtmlUtil.strANCHOR_TARGET_PARENT;
*               strTarget = HtmlUtil.strANCHOR_TARGET_TOP;
*               strTarget = HtmlUtil.strANCHOR_TARGET_SEARCH;
*               strTarget = HtmlUtil.strANCHOR_TARGET_MEDIA;
*<b>Assumptions:</b>
*<b>Effects:</b>
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class HtmlUtil
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
    * Anchor target:  New browser window.
    **************************************************************************/
    public static final String strANCHOR_TARGET_NEW_WINDOW = "_blank";

    /**************************************************************************
    * Anchor target:  Current browser window or frame.
    **************************************************************************/
    public static final String strANCHOR_TARGET_SELF = "_self";

    /**************************************************************************
    * Anchor target:  Parent window or frame of current frame, or self if no
    * parent. 
    **************************************************************************/
    public static final String strANCHOR_TARGET_PARENT = "_parent";

    /**************************************************************************
    * Anchor target:  Top-level window of browser.
    **************************************************************************/
    public static final String strANCHOR_TARGET_TOP = "_top";

    /**************************************************************************
    * Anchor target:  Browser's search pane (IE 5.0 and later).
    **************************************************************************/
    public static final String strANCHOR_TARGET_SEARCH = "_search";

    /**************************************************************************
    * Anchor target:  Browser's media bar (IE 6.0 and later).
    **************************************************************************/
    public static final String strANCHOR_TARGET_MEDIA = "_media";

    /**************************************************************************
    * Get a friendly error message, formatted as HTML.
    *@param  strMessage A String to put in front of the error message.
    *@param  throwable  The Throwable containing the error message.
    **************************************************************************/
    public static String getFriendlyErrorMessage
                        (String              strMessage,
                         Throwable           throwable)
    {
        return "\n <b>"
             + "\n   " + strMessage
             + "\n   <br />"
             + "\n   <br />"
             + "\n   Error Details:"
             + "\n   <br />"
             + "\n   &nbsp;&nbsp;Name: " + ObjUtil.getShortClassName(throwable)
             + "\n   <br />"
             + "\n   &nbsp;&nbsp;Reason: " + throwable.getMessage()
             + "\n   <br />"
             + "\n   <br />"
             + "\n   See additional details in the server log file."
             + "\n </b>"
             ;
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
