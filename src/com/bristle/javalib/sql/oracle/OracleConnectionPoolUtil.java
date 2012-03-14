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

package com.bristle.javalib.sql.oracle;

import com.bristle.javalib.log.Logger;
import com.bristle.javalib.sql.ConnectionPoolUtil;
import com.bristle.javalib.sql.ConnectionPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.SQLException;

import oracle.jdbc.driver.OracleTypes;

// OracleConnectionPoolUtil
/******************************************************************************
* This class contains utility routines for use with an Oracle database.  
* Some of the methods optionally use the ConnectionPool.
* <pre>
* <b>Usage:</b>
*
*   - Typical scenarios for using this class include:
*
*     // ----------------------------------------------------------------------
*     // To use with a single database Connection, and optionally a single 
*     // Logger, specifying them each once before doing any database operations:
*     // ----------------------------------------------------------------------
*     OracleConnectionPoolUtil util = new OracleConnectionPoolUtil();
*     util.setDefaultConnection(conn);
*     util.setDefaultLogger(logger);  // optional
*     util.executeSQL("begin dbuser1.package1.proc1(123,'abc',null,true); end;");
*     DBContext dbContext;
*     try
*     {
*         dbContext = util.getResultSetFromOracleFunction
*                       ("dbuser1.package1.function1(123,'abc',null,true)");
*         while (dbContext.rs.next())
*         {
*             String strCol1 = dbContext.rs.getString("col1");
*             String strCol2 = dbContext.rs.getString("col2");
*             ...
*         }
*     }
*     finally
*     {
*         util.cleanupDBContext(dbContext);
*     }
*              
*     // ----------------------------------------------------------------------
*     // To use with a single ConnectionPool, single database, single set of 
*     // credentials, default result set type, default result set concurrency,
*     // and optionally a single Logger, specifying them each once before 
*     // doing any database operations:
*     // ----------------------------------------------------------------------
*     OracleConnectionPoolUtil util = new OracleConnectionPoolUtil();
*     util.setDefaultConnectionPool(pool);
*     util.setDefaultDBConfig(config);
*     util.setDefaultLogger(logger);  // optional
*     util.executeSQL("begin dbuser1.package1.proc1(123,'abc',null,true); end;");
*     DBContext dbContext;
*     try
*     {
*         dbContext = util.getResultSetFromOracleFunction
*                       ("dbuser1.package1.function1(123,'abc',null,true)");
*         while (dbContext.rs.next())
*         {
*             String strCol1 = dbContext.rs.getString("col1");
*             String strCol2 = dbContext.rs.getString("col2");
*             ...
*         }
*     }
*     finally
*     {
*         util.cleanupDBContext(dbContext);
*     }
*              
*     // ----------------------------------------------------------------------
*     // To specify a database Connection, and optionally a Logger, on each 
*     // operation:
*     // ----------------------------------------------------------------------
*     OracleConnectionPoolUtil.executeSQL
*             (conn, 
*              "begin dbuser1.package1.proc1(123,'abc',null,true); end;", 
*              logger, // or OracleConnectionPoolUtil.loggerNO_LOGGING
*              null, 
*              null);
*     DBContext dbContext;
*     try
*     {
*         dbContext = OracleConnectionPoolUtil.getResultSetFromOracleFunction
*             (conn,
*              "dbuser1.package1.function1(123,'abc',null,true)",
*              ResultSet.TYPE_FORWARD_ONLY,
*              ResultSet.CONCUR_READ_ONLY,
*              logger, // or OracleConnectionPoolUtil.loggerNO_LOGGING
*              null, 
*              null);
*         while (dbContext.rs.next())
*         {
*             String strCol1 = dbContext.rs.getString("col1");
*             String strCol2 = dbContext.rs.getString("col2");
*             ...
*         }
*     }
*     finally
*     {
*         OracleConnectionPoolUtil.cleanupDBContext
*             (dbContext,
*              logger, // or OracleConnectionPoolUtil.loggerNO_LOGGING
*              null);
*     }
*              
*     // ----------------------------------------------------------------------
*     // To specify a ConnectionPool, database, set of credentials, and 
*     // optionally a Logger, on each operation:
*     // ----------------------------------------------------------------------
*     OracleConnectionPoolUtil.executeSQL
*             (OracleConnectionPoolUtil.connALLOCATE_CONNECTION_FROM_POOL, 
*              "begin dbuser1.package1.proc1(123,'abc',null,true); end;", 
*              logger, // or OracleConnectionPoolUtil.loggerNO_LOGGING
*              pool, 
*              config);
*     DBContext dbContext;
*     try
*     {
*         dbContext = OracleConnectionPoolUtil.getResultSetFromOracleFunction
*             (OracleConnectionPoolUtil.connALLOCATE_CONNECTION_FROM_POOL,
*              "dbuser1.package1.function1(123,'abc',null,true)",
*              ResultSet.TYPE_FORWARD_ONLY,
*              ResultSet.CONCUR_READ_ONLY,
*              logger, // or OracleConnectionPoolUtil.loggerNO_LOGGING
*              pool, 
*              config);
*         while (dbContext.rs.next())
*         {
*             String strCol1 = dbContext.rs.getString("col1");
*             String strCol2 = dbContext.rs.getString("col2");
*             ...
*         }
*     }
*     finally
*     {
*         OracleConnectionPoolUtil.cleanupDBContext
*             (dbContext,
*              logger, // or OracleConnectionPoolUtil.loggerNO_LOGGING
*              pool);
*     }
*              
*     // ----------------------------------------------------------------------
*     // To test the validity of a database connection:
*     // ----------------------------------------------------------------------
*     OracleConnectionPoolUtil.databaseConnectionIsValid(conn);
*              
*     // ----------------------------------------------------------------------
*     // To close a Connection and drop it from the ConnectionPool, perhaps 
*     // because the Connection has gone bad in some way:
*     // ----------------------------------------------------------------------
*     util.cleanupDBContext
*         (dbContext,
*          OracleConnectionPoolUtil.blnCLOSE_CONNECTION,
*          OracleConnectionPoolUtil.blnDONE_WITH_CONNECTION);
*     // or
*     OracleConnectionPoolUtil.cleanupDBContext
*         (dbContext,
*          OracleConnectionPoolUtil.blnCLOSE_CONNECTION,
*          OracleConnectionPoolUtil.blnDONE_WITH_CONNECTION,
*          logger, // or OracleConnectionPoolUtil.loggerNO_LOGGING
*          pool);
*
*     // ----------------------------------------------------------------------
*     // To hold on to a pooled Connection, preventing it from being returned 
*     // to the ConnectionPool, but still releasing the other database objects
*     // (Statement and ResultSet): 
*     // ----------------------------------------------------------------------
*     util.cleanupDBContext
*         (dbContext,
*          !OracleConnectionPoolUtil.blnCLOSE_CONNECTION,
*          !OracleConnectionPoolUtil.blnDONE_WITH_CONNECTION);
*     // or
*     OracleConnectionPoolUtil.cleanupDBContext
*         (dbContext,
*          !OracleConnectionPoolUtil.blnCLOSE_CONNECTION,
*          !OracleConnectionPoolUtil.blnDONE_WITH_CONNECTION,
*          logger, // or OracleConnectionPoolUtil.loggerNO_LOGGING
*          pool);
*     // or:
*     OracleConnectionPoolUtil.cleanupDBContext
*         (dbContext,
*          logger, // or OracleConnectionPoolUtil.loggerNO_LOGGING
*          null);
*
* <b>Assumptions:</b>
* <b>Effects:</b>
*       - None.
* <b>Anticipated Changes:</b>
* <b>Notes:</b>
* <b>Implementation Notes:</b>
* <b>Portability Issues:</b>
* <b>Revision History:</b>
*   $Log$
* </pre>
******************************************************************************/

public class OracleConnectionPoolUtil extends ConnectionPoolUtil 
{
    //--
    //-- Class variables
    //--

    //--
    //-- Instance variables to support public properties
    //--
    private int m_intLogLevelOfOperation 
                            = new ConnectionPoolUtil().getLogLevelOfOperation();

    //--
    //-- Internal instance variables
    //--

    /**************************************************************************
    * Set the level of logging operations done by this class.
    *@param  intNew     The new value.
    **************************************************************************/
    public void setLogLevelOfOperation(int intNew)
    {
        m_intLogLevelOfOperation = intNew;
    }

    /**************************************************************************
    * Get the level of logging operations done by this class.
    *@return            The log level.
    **************************************************************************/
    public int getLogLevelOfOperation()
    {
        return m_intLogLevelOfOperation;
    }

    /**************************************************************************
    * Connect to the database and get the data as a readonly forward-only 
    * ResultSet by calling an Oracle stored function that returns a ResultSet.
    *@param  conn       Connection to use in database query.
    *                   Optional.  If null, a connection from the pool is used.
    *@param  strCall    String of SQL to call the Oracle stored function.
    *                   Example:  "Function1('abc', 1, null, 'xyz', true)"
    *@param  logger     Logger to log operations to.
    *                   Optional.  If null, no logging is done.
    *@param  intLogLevelOfOperation
    *                   Level at which to log operations.
    *@param  pool       ConnectionPool to use to obtain a connection if conn 
    *                   is null.
    *                   Optional and ignored if conn is not null.
    *@param  dbconfig   Info used to choose a pooled connection when the pool
    *                   is used. 
    *                   Optional and ignored if the pool is not used.
    *@return            DBContext object containing returned database objects.
    *@throws SQLException
    **************************************************************************/
    public static DBContext getResultSetFromOracleFunction
                        (Connection                 conn, 
                         String                     strCall, 
                         Logger                     logger,
                         int                        intLogLevelOfOperation,
                         ConnectionPool             pool,
                         ConnectionPool.DBConfig    dbconfig)
                        throws SQLException
    {
        if (conn == null && (pool == null || dbconfig == null))
        {
            throw new NullPointerException("If conn is null, pool and" +
                                           " dbconfig must both be non-null.");
        }

        long lngStartTime = System.currentTimeMillis();
        Logger.logSafely(logger, 
                         intLogLevelOfOperation, 
                         "BEGIN getResultSetFromOracleFunction()");

        //-- Allocate a DBContext object to hold the references to the
        //-- multiple objects to be created and returned.
        DBContext dbContext = new DBContext();

        //-- Connect to the database and call the stored function, getting a
        //-- ResultSet.
        //-- Use the specified connection, if any.  Otherwise get one from 
        //-- the connection pool.
        dbContext.conn = (conn == null)
                        ? pool.getConnection(dbconfig)
                        : conn;
        boolean blnDBCleanupRequired = true;
        try
        {
            CallableStatement st = dbContext.conn.prepareCall
                                        ("{ call ? := " + strCall + " }");
            dbContext.st = st;     //-- So the caller can release it later.
            st.registerOutParameter(1, OracleTypes.CURSOR);
            //-- Note:  Could bind parameters of different types, allowing some of
            //--        them to be output parameters via code like the following.
            //--        For now, it is sufficient to set all input params as 
            //--        literals, and just return a ResultSet (cursor).
            //--            st.setString           (2, strCDPName);
            //--            st.registerOutParameter(3, OracleTypes.XXX);
            //--            st.setFloat            (4, intContainerNumberMax);
            st.execute();
            dbContext.rs = (ResultSet)st.getObject(1);
            Logger.logSafely(logger, 
                             intLogLevelOfOperation, 
                             "END   getResultSetFromOracleFunction()" 
                             + " : " 
                             + (System.currentTimeMillis() - lngStartTime));
            blnDBCleanupRequired = false;
            return dbContext;
        }
        finally
        {
            //-- Release the database resources if an error occurred before
            //-- we were able to get the resultset.  The caller is unable to
            //-- do so because we don't return dbContext to the caller.
            //-- Also, test the database connection and decide whether it
            //-- needs to be closed.
            if (blnDBCleanupRequired)
            {
                //-- Note:  If the caller specified a connection, do not 
                //--        assume we are done with it.  The caller may 
                //--        still be using it.
                boolean blnDoneWithConnection = (conn == null);
                boolean blnCloseBadConnection = 
                        !databaseConnectionIsValid(dbContext.conn);
                cleanupDBContext
                                (dbContext, 
                                 blnCloseBadConnection,
                                 blnDoneWithConnection,
                                 logger,
                                 intLogLevelOfOperation,
                                 pool);
            }
        }
    }

    /**************************************************************************
    * Connect to the database and get the data as a readonly forward-only 
    * ResultSet by calling an Oracle stored function that returns a ResultSet,
    * using the default values for Connection, Logger, ConnectionPool, and 
    * DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.    
    *@param  strCall    String of SQL to call the Oracle stored function.
    *                   Example:  "Function1('abc', 1, null, 'xyz', true)"
    *@return            Context object containing returned database objects.
    *@throws SQLException
    **************************************************************************/
    public DBContext getResultSetFromOracleFunction(String strCall)
                        throws SQLException
    {
        return getResultSetFromOracleFunction
                        (getDefaultConnection(),
                         strCall, 
                         getDefaultLogger(),
                         getLogLevelOfOperation(),
                         getDefaultConnectionPool(),
                         getDefaultDBConfig());
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
