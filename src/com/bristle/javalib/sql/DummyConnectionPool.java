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
import java.sql.Connection;

// DummyConnectionPool
/******************************************************************************
* This implements dummy ConnectionPool for testing purposes.
*<pre>
*<b>Usage:</b>
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
public class DummyConnectionPool extends ConnectionPool
{
    protected void init(String strDBDriverClassName) throws ClassNotFoundException {}
    public DummyConnectionPool(String strDBDriverClassName) 
            throws ClassNotFoundException,
                   InstantiationException,
                   IllegalAccessException 
    { super(strDBDriverClassName);}
    public DummyConnectionPool(String strDBDriverClassName, int intMaxTimesToUse)
            throws ClassNotFoundException,
                   InstantiationException,
                   IllegalAccessException
    { super(strDBDriverClassName,intMaxTimesToUse); }
    protected Connection makeNewConnection(DBConfig objConfig)
           throws SQLException
    {
        return new DummyConnection();
    }
}
