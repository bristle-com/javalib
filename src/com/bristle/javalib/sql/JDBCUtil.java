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

import java.sql.SQLException;
import java.sql.ResultSet;

// JDBCUtil
/******************************************************************************
* This class contains utility routines for use with JDBC.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*       String s = JDBCUtil.getColumnOrEmptyString(rs, strColumnName);
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
public class JDBCUtil
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
    * Returns the specified column of the specified resultset, mapping
    * null values to the empty string.
    *@param  rs            Resultset
    *@param  strColumnName Column name
    *@return               Column value (empty string if null).
    *@throws SQLException  When an error occurs getting the column.
    **************************************************************************/
    public static String getColumnOrEmptyString(
                        ResultSet rs,
                        String    strColumnName)
                throws SQLException
    {
        String strRC;
        strRC = rs.getString(strColumnName);
        if (rs.wasNull())
        {
            strRC = "";
        }
        return strRC;
    }

    /**************************************************************************
    * Returns the number of rows in the specified resultset.
    *<pre>
    *<b>Notes:</b>
    *   - This method requires a resultset that can be rewound.  Therefore,
    *     you can't use a TYPE_FORWARD_ONLY resultset.  However, if you use
    *     a TYPE_SCROLL_SENSITIVE or TYPE_SCROLL_INSENSITIVE resultset,
    *     calling this method has the side effect of loading all rows of 
    *     the resultset into memory.  (This happens automatically at the 
    *     call to last()).  Therefore, do not use this method if you can't
    *     afford the memory or the time to load all rows.
    *</pre>
    *@param  rs            Resultset
    *@return               Row count.
    *@throws SQLException
    *                      If the resultset is of type TYPE_FORWARD_ONLY.
    **************************************************************************/
    public static int getRowCount(ResultSet rs)
                throws SQLException
    {
        //-- Record the current row so we can return to it later.
        int     intOriginalRow = rs.getRow();
        boolean blnBeforeFirst = rs.isBeforeFirst();
        boolean blnAfterLast   = rs.isAfterLast();

        //-- Move to the last row and see what row it is, defaulting to zero
        //-- if the resultset has no rows.
        int intRowCount   = 0;
        if (rs.last()) 
        {
            intRowCount = rs.getRow();
        }

        //-- Return to the original row.
        if (intRowCount == 0)
        {
            //-- No original row to return to.  There are no rows.
        }
        else if (blnBeforeFirst)
        {
            rs.beforeFirst();
        }
        else if (blnAfterLast)
        {
            rs.afterLast();
        }
        else
        {
            rs.absolute(intOriginalRow);
        }

        return intRowCount;
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
