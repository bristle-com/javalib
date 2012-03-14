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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import com.bristle.javalib.log.Logger;
import com.bristle.javalib.log.LoggerUtil;
import com.bristle.javalib.util.ExcUtil;

// ConnectionPoolUtil
/******************************************************************************
* This class contains utility routines for use with JDBC databases.  Some of
* the methods optionally use the ConnectionPool.
* <pre>
* <b>Usage:</b>
*
*   - Typical scenarios for using this class include:
*
*     // ----------------------------------------------------------------------
*     // To initialize for non-Oracle databases:
*     // ----------------------------------------------------------------------
*     ConnectionPoolUtil.setConnectionTestString
*             ("some SQL string that executes quickly without throwing an" +
*              " exception if the database connection is valid");
*
*     // ----------------------------------------------------------------------
*     // To use with a single database Connection, default result set type, 
*     // default result set concurrency, and optionally a single Logger, 
*     // specifying them each once before doing any database operations:
*     // ----------------------------------------------------------------------
*     ConnectionPoolUtil util = new ConnectionPoolUtil();
*     util.setDefaultConnection(conn);
*     util.setDefaultLogger(logger);  // optional
*     util.executeSQL("delete from mytable");
*     DBContext dbContext;
*     try
*     {
*         dbContext = util.getResultSet("select * from mytable");
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
*     ConnectionPoolUtil util = new ConnectionPoolUtil();
*     util.setDefaultConnectionPool(pool);
*     util.setDefaultDBConfig(config);
*     util.setDefaultLogger(logger);  // optional
*     util.executeSQL("delete from mytable");
*     DBContext dbContext;
*     try
*     {
*         dbContext = util.getResultSet("select * from mytable");
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
*     ConnectionPoolUtil.executeSQL
*             (conn, 
*              "delete from mytable", 
*              logger, // or ConnectionPoolUtil.loggerNO_LOGGING
*              null, 
*              null);
*     DBContext dbContext;
*     try
*     {
*         dbContext = ConnectionPoolUtil.getResultSet
*             (conn,
*              "select * from mytable",
*              ResultSet.TYPE_FORWARD_ONLY,
*              ResultSet.CONCUR_READ_ONLY,
*              logger, // or ConnectionPoolUtil.loggerNO_LOGGING
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
*         ConnectionPoolUtil.cleanupDBContext
*             (dbContext,
*              logger, // or ConnectionPoolUtil.loggerNO_LOGGING
*              null);
*     }
*              
*     // ----------------------------------------------------------------------
*     // To specify a ConnectionPool, database, set of credentials, and 
*     // optionally a Logger, on each operation:
*     // ----------------------------------------------------------------------
*     ConnectionPoolUtil.executeSQL
*             (ConnectionPoolUtil.connALLOCATE_CONNECTION_FROM_POOL, 
*              "delete from mytable", 
*              logger, // or ConnectionPoolUtil.loggerNO_LOGGING
*              pool, 
*              config);
*     DBContext dbContext;
*     try
*     {
*         dbContext = ConnectionPoolUtil.getResultSet
*             (ConnectionPoolUtil.connALLOCATE_CONNECTION_FROM_POOL,
*              "select * from mytable",
*              ResultSet.TYPE_FORWARD_ONLY,
*              ResultSet.CONCUR_READ_ONLY,
*              logger, // or ConnectionPoolUtil.loggerNO_LOGGING
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
*         ConnectionPoolUtil.cleanupDBContext
*             (dbContext,
*              logger, // or ConnectionPoolUtil.loggerNO_LOGGING
*              pool);
*     }
*              
*     // ----------------------------------------------------------------------
*     // To test the validity of a database connection:
*     // ----------------------------------------------------------------------
*     ConnectionPoolUtil.databaseConnectionIsValid(conn);
*              
*     // ----------------------------------------------------------------------
*     // To close a Connection and drop it from the ConnectionPool, perhaps 
*     // because the Connection has gone bad in some way:
*     // ----------------------------------------------------------------------
*     util.cleanupDBContext
*         (dbContext,
*          ConnectionPoolUtil.blnCLOSE_CONNECTION,
*          ConnectionPoolUtil.blnDONE_WITH_CONNECTION);
*     // or
*     ConnectionPoolUtil.cleanupDBContext
*         (dbContext,
*          ConnectionPoolUtil.blnCLOSE_CONNECTION,
*          ConnectionPoolUtil.blnDONE_WITH_CONNECTION,
*          logger, // or ConnectionPoolUtil.loggerNO_LOGGING
*          pool);
*
*     // ----------------------------------------------------------------------
*     // To hold on to a pooled Connection, preventing it from being returned 
*     // to the ConnectionPool, but still releasing the other database objects
*     // (Statement and ResultSet): 
*     // ----------------------------------------------------------------------
*     util.cleanupDBContext
*         (dbContext,
*          !ConnectionPoolUtil.blnCLOSE_CONNECTION,
*          !ConnectionPoolUtil.blnDONE_WITH_CONNECTION);
*     // or
*     ConnectionPoolUtil.cleanupDBContext
*         (dbContext,
*          !ConnectionPoolUtil.blnCLOSE_CONNECTION,
*          !ConnectionPoolUtil.blnDONE_WITH_CONNECTION,
*          logger, // or ConnectionPoolUtil.loggerNO_LOGGING
*          pool);
*     // or:
*     ConnectionPoolUtil.cleanupDBContext
*         (dbContext,
*          logger, // or ConnectionPoolUtil.loggerNO_LOGGING
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
public class ConnectionPoolUtil
{
    //--
    //-- Class variables
    //--
    private static String st_strCONNECTION_TEST_STRING = "select sysdate from dual";

    //--
    //-- Instance variables to support public properties
    //--
    private Connection      m_conn    = connALLOCATE_CONNECTION_FROM_POOL;
    private int             m_intType = ResultSet.TYPE_FORWARD_ONLY; 
    private int             m_intConcurrency = ResultSet.CONCUR_READ_ONLY; 
    private Logger          m_logger  = loggerNO_LOGGING;
    private int             m_intLogLevelOfOperation = 10;
    private ConnectionPool  m_pool    = null;
    private ConnectionPool.DBConfig
                            m_config  = null;

    //--
    //-- Internal instance variables
    //--

    //-- Constants for use as parameters to the methods of this class.
    public static final Connection connALLOCATE_CONNECTION_FROM_POOL = null;
    public static final Logger     loggerNO_LOGGING = null;
    public static final boolean    blnCLOSE_CONNECTION     = true;
    public static final boolean    blnDONE_WITH_CONNECTION = true;

    /**************************************************************************
    * Convenience class used to pass multiple related database objects around.
    **************************************************************************/
    public static class DBContext
    {
        public Connection conn = null;
        public Statement  st   = null;
        public ResultSet  rs   = null;
    }

    /**************************************************************************
    * Set the connection test string.  This string of SQL is executed as 
    * necessary to test the validity of connections in the connection pool.
    * If an exception is thrown, the connection is invalid.  The default value 
    * is Oracle-specific:
    *       select sysdate from dual
    * Override it with any other appropriate quickly-executing SQL string.
    *@param  strNew     The new value.
    **************************************************************************/
    public static void setConnectionTestString(String strNew)
    {
        st_strCONNECTION_TEST_STRING = strNew;
    }

    /**************************************************************************
    * Get the connection test string.
    *@return            The connection test string.
    **************************************************************************/
    public static String getConnectionTestString()
    {
        return st_strCONNECTION_TEST_STRING;
    }

    /**************************************************************************
    * Set the default Connection.
    *@param  connNew     The new value.
    **************************************************************************/
    public void setDefaultConnection(Connection connNew)
    {
        m_conn = connNew;
    }

    /**************************************************************************
    * Get the default Connection.
    *@return            The default Connection.
    **************************************************************************/
    public Connection getDefaultConnection()
    {
        return m_conn;
    }

    /**************************************************************************
    * Set the default result set type to one of:
    *   <code>ResultSet.TYPE_FORWARD_ONLY</code>,
    *   <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>,
    *   or <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
    *@param  intNew     The new value.
    **************************************************************************/
    public void setDefaultResultSetType(int intNew)
    {
        m_intType = intNew;
    }

    /**************************************************************************
    * Get the default result set type.
    *@return            The default result set type.
    **************************************************************************/
    public int getDefaultResultSetType()
    {
        return m_intType;
    }

    /**************************************************************************
    * Set the default result set concurrency to one of:
    *   <code>ResultSet.CONCUR_READONLY</code>,
    *   or <code>ResultSet.CONCUR_UPDATABLE</code>,
    *@param  intNew     The new value.
    **************************************************************************/
    public void setDefaultResultSetConcurrency(int intNew)
    {
        m_intConcurrency = intNew;
    }

    /**************************************************************************
    * Get the default result set concurrency.
    *@return            The default result set concurrency.
    **************************************************************************/
    public int getDefaultResultSetConcurrency()
    {
        return m_intConcurrency;
    }

    /**************************************************************************
    * Set the default Logger.
    *@param  loggerNew     The new value.
    **************************************************************************/
    public void setDefaultLogger(Logger loggerNew)
    {
        m_logger = loggerNew;
    }

    /**************************************************************************
    * Get the default Logger.
    *@return            The default Logger.
    **************************************************************************/
    public Logger getDefaultLogger()
    {
        return m_logger;
    }

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
    * Set the default ConnectionPool.
    *@param  poolNew     The new value.
    **************************************************************************/
    public void setDefaultConnectionPool(ConnectionPool poolNew)
    {
        m_pool = poolNew;
    }

    /**************************************************************************
    * Get the default ConnectionPool.
    *@return            The default ConnectionPool.
    **************************************************************************/
    public ConnectionPool getDefaultConnectionPool()
    {
        return m_pool;
    }

    /**************************************************************************
    * Set the default DBConfig.
    *@param  configNew     The new value.
    **************************************************************************/
    public void setDefaultDBConfig(ConnectionPool.DBConfig configNew)
    {
        m_config = configNew;
    }

    /**************************************************************************
    * Get the default DBConfig.
    *@return            The default DBConfig.
    **************************************************************************/
    public ConnectionPool.DBConfig getDefaultDBConfig()
    {
        return m_config;
    }

    /**************************************************************************
    * Connect to the database and get the data as a ResultSet with the 
    * specified type and concurrency.
    *@param  conn       Connection to use in database query.
    *                   Optional.  If null, a connection from the pool is used.
    *@param  strSQL     String of SQL to use in database query.
    *@param  intType    Type of result set.  Must be one of:
    *                           ResultSet.TYPE_FORWARD_ONLY
    *                           ResultSet.TYPE_SCROLL_INSENSITIVE
    *                           ResultSet.TYPE_SCROLL_SENSITIVE
    *@param  intConcurrency
    *                   Concurrency of result set.  Must be one of:
    *                           ResultSet.CONCUR_READONLY
    *                           ResultSet.CONCUR_UPDATABLE
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
    public static DBContext getResultSet
                        (Connection                 conn, 
                         String                     strSQL, 
                         int                        intType,
                         int                        intConcurrency,
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

        //-- Allocate a DBContext object to hold the references to the
        //-- multiple objects to be created and returned.
        DBContext dbContext = new DBContext();

        //-- Connect to the database and get the data.
        //-- Use the specified connection, if any.  Otherwise get one from 
        //-- the connection pool.
        dbContext.conn = (conn == null)
                          ? pool.getConnection(dbconfig)
                          : conn;

        //-- Try block to ensure we call logEnd(), but don't catch errors.
        //-- Allow them to propagate to the caller who should log them and 
        //-- report them to the user.
        boolean blnDBCleanupRequired = true;
        LoggerUtil logutil = new LoggerUtil(logger);
        try
        {
            logutil.setPreserveLoggerUsername(true);
            logutil.setLogLevelOfOperation(intLogLevelOfOperation);
            logutil.logBegin("ConnectionPoolUtil.getResultSet()");
            logutil.logNested(strSQL);
            
            //-- Get the resultset.
            dbContext.st = dbContext.conn.createStatement(intType, intConcurrency);
            dbContext.rs = dbContext.st.executeQuery(strSQL);
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
                logutil.setAborted(true);
            }
            logutil.logEnd();
        }
    }

    /**************************************************************************
    * Connect to the database and get the data using the default values for 
    * Connection, result set type, result set concurrency, Logger, 
    * ConnectionPool, and DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.    
    *@param  strSQL     String of SQL to use in database query.
    *@return            DBContext object containing returned database objects.
    *@throws SQLException
    **************************************************************************/
    public DBContext getResultSet(String strSQL)
                        throws SQLException
    {
        return getResultSet
                        (getDefaultConnection(),
                         strSQL, 
                         getDefaultResultSetType(),
                         getDefaultResultSetConcurrency(),
                         getDefaultLogger(),
                         getLogLevelOfOperation(),
                         getDefaultConnectionPool(),
                         getDefaultDBConfig());
    }

    /**************************************************************************
    * Connect to the database, execute a string of SQL, commit, and disconnect.
    *@param  conn       Connection to use in executing SQL string.
    *                   Optional.  If null, a connection from the pool is used.
    *@param  strSQL     String of SQL to use in database query.
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
    *@return            Number of rows affected in the database.
    *@throws SQLException
    **************************************************************************/
    public static int executeSQL
                        (Connection                 conn, 
                         String                     strSQL, 
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

        //-- Allocate a DBContext object to pass to cleanupDBContext.
        DBContext dbContext = new DBContext();

        //-- Use the specified connection, if any.  Otherwise get one from 
        //-- the connection pool.
        dbContext.conn = (conn == null) 
                          ? pool.getConnection(dbconfig)
                          : conn;

        //-- Try block to ensure we call logEnd(), but don't catch errors.
        //-- Allow them to propagate to the caller who should log them and 
        //-- report them to the user.
        boolean blnDBCheckRequired = true;
        LoggerUtil logutil = new LoggerUtil(logger);
        try
        {
            logutil.setPreserveLoggerUsername(true);
            logutil.setLogLevelOfOperation(intLogLevelOfOperation);
            logutil.logBegin("ConnectionPoolUtil.executeSQL()");
            logutil.logNested(strSQL);
            
            //-- Execute the SQL.
            dbContext.st = dbContext.conn.createStatement();
            int intRowCount = dbContext.st.executeUpdate(strSQL);
            dbContext.conn.commit();
            blnDBCheckRequired = false;
            return intRowCount;
        }
        finally
        {
            //-- If an error occurred, test the database connection and 
            //-- decide whether it needs to be closed.
            boolean blnCloseBadConnection = 
                        (blnDBCheckRequired
                         ? !databaseConnectionIsValid(dbContext.conn)
                         : false
                        );

            //-- Release the database resources.
            //-- Note:  If the caller specified a connection, do not 
            //--        assume we are done with it.  The caller may 
            //--        still be using it.
            boolean blnDoneWithConnection = (conn == null);
            cleanupDBContext
                        (dbContext, 
                         blnCloseBadConnection,
                         blnDoneWithConnection,
                         logger,
                         intLogLevelOfOperation,
                         pool);

            logutil.setAborted(blnDBCheckRequired);
            logutil.logEnd();
        }
    }

    /**************************************************************************
    * Connect to the database, execute a string of SQL, and disconnect, using 
    * the default values for Connection, Logger, ConnectionPool, and DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strSQL     String of SQL to use in database query.
    *@throws SQLException
    **************************************************************************/
    public void executeSQL(String strSQL)
                        throws SQLException
    {
        executeSQL
                (getDefaultConnection(),
                 strSQL, 
                 getDefaultLogger(),
                 getLogLevelOfOperation(),
                 getDefaultConnectionPool(),
                 getDefaultDBConfig());
    }
    
    /**************************************************************************
    * Thrown when no data is found in a situation that requires data.
    **************************************************************************/
    public static class NoDataFoundException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public NoDataFoundException(String msg) { super(msg); }
    }

    /**************************************************************************
    * Connect to the database, get a single int value from the resultset 
    * returned by the specified SQL statement, and disconnect, returning the 
    * value.
    *@param  conn       Connection to use in executing the SQL statement.
    *                   Optional.  If null, a connection from the pool is used.
    *@param  strSQL     String of SQL to use in the database query.
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
    *@return            Count of rows returned.
    *@throws NoDataFoundException when the query returns no data.
    *@throws SQLException         When a SQL error occurs, including when 
    *                             strSQL returns a non-Integer value.
    **************************************************************************/
    public static int getIntValueFromDB
                        (Connection                 conn, 
                         String                     strSQL, 
                         Logger                     logger,
                         int                        intLogLevelOfOperation,
                         ConnectionPool             pool,
                         ConnectionPool.DBConfig    dbconfig)
                        throws NoDataFoundException
                              ,SQLException
    {
        if (conn == null && (pool == null || dbconfig == null))
        {
            throw new NullPointerException("If conn is null, pool and" +
                                           " dbconfig must both be non-null.");
        }

        //-- Use the specified connection, if any.  Otherwise get one from 
        //-- the connection pool.
        conn = (conn == null) 
                ? pool.getConnection(dbconfig)
                : conn;

        //-- Try block to ensure we call logEnd(), but don't catch errors.
        //-- Allow them to propagate to the caller who should log them and 
        //-- report them to the user.
        LoggerUtil logutil = new LoggerUtil(logger);
        DBContext dbContext = null;
        try
        {
            logutil.setPreserveLoggerUsername(true);
            logutil.setLogLevelOfOperation(intLogLevelOfOperation);
            logutil.logBegin("Getting int value from the database.");

            dbContext = getResultSet
                            (conn,
                             strSQL, 
                             ResultSet.TYPE_FORWARD_ONLY,
                             ResultSet.CONCUR_READ_ONLY,
                             logger,
                             intLogLevelOfOperation + 1,
                             pool,
                             dbconfig);
            boolean blnExists = dbContext.rs.next();
            if (blnExists)
            {
                int intRowCount = dbContext.rs.getInt(1);
                logutil.logNested("int value = " + intRowCount);
                return intRowCount;
            }
            else
            {
                logutil.logNested("int value = (not found)");
                throw new NoDataFoundException
                                ("Zero rows found for SQL: " + strSQL);
            }
        }
        catch(SQLException exception)
        {
            logutil.setAborted(true);
            throw exception;
        }
        finally
        {
            cleanupDBContext
                        (dbContext, 
                         logger,
                         intLogLevelOfOperation,
                         pool);
            logutil.logEnd();
        }
    }

    /**************************************************************************
    * Connect to the database, get a single int value from the resultset 
    * returned by the specified SQL statement, and disconnect, returning the 
    * value, using the default values for Connection, Logger, ConnectionPool, 
    * and DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strSQL     String of SQL to use in the database query.
    *@throws NoDataFoundException when the query returns no data.
    *@throws SQLException         When a SQL error occurs, including when 
    *                             strSQL returns a non-Integer value.
    **************************************************************************/
    public int getIntValueFromDB(String strSQL)
                        throws NoDataFoundException
                              ,SQLException
    {
        return getIntValueFromDB
                        (getDefaultConnection(),
                         strSQL, 
                         getDefaultLogger(),
                         getLogLevelOfOperation(),
                         getDefaultConnectionPool(),
                         getDefaultDBConfig());
    }
    
    /**************************************************************************
    * Connect to the database, execute a one string of SQL or another based 
    * on the value of boolean flag, then disconnect, using the default values 
    * for Connection, Logger, ConnectionPool, and DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  blnIf      The boolean to control which SQL statement to execute.
    *@param  strSQLThen String of SQL to execute if blnIf is true, or null or 
    *                   an empty string.
    *@param  strSQLElse String of SQL to execute if blnIf is false, or null or
    *                   an empty string.
    *@throws SQLException
    **************************************************************************/
    public void executeSQLIfThenElse
                        (boolean blnIf
                        ,String  strSQLThen
                        ,String  strSQLElse)
                        throws SQLException
    {
        String strSQL = (blnIf ? strSQLThen : strSQLElse);
        if (strSQL != null && !strSQL.equals(""))
        {
            executeSQL
                (getDefaultConnection(),
                 strSQL, 
                 getDefaultLogger(),
                 getLogLevelOfOperation(),
                 getDefaultConnectionPool(),
                 getDefaultDBConfig());
        }
    }

    /**************************************************************************
    * Connect to the database, execute a SELECT statement to get an Integer
    * value, and based on its existence, execute a 2nd or 3rd string of SQL, 
    * then disconnect, returning the Integer value if the row exists, null
    * otherwise, using the default values for Connection, Logger, 
    * ConnectionPool, and DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strSelect  String of SQL to get the Integer value.
    *@param  strSQLThen String of SQL to execute if the Integer exists, 
    *                   or null or an empty string.
    *@param  strSQLElse String of SQL to execute if the Integer doesn't exist, 
    *                   or null or an empty string.
    *@return The Integer value if it exists; null otherwise
    *@throws SQLException
    **************************************************************************/
    public Integer executeSQLIfIntegerExistsThenElse
                        (String strSelect
                        ,String strSQLThen
                        ,String strSQLElse)
                        throws SQLException
    {
        Integer intValue = null;
        try
        {
            intValue = new Integer(getIntValueFromDB(strSelect));
        }
        catch (NoDataFoundException e)
        {
            intValue = null;
        }
        boolean blnExists = (intValue != null);
        executeSQLIfThenElse(blnExists, strSQLThen, strSQLElse);
        return intValue;
    }

    /**************************************************************************
    * Connect to the database, execute a SELECT statement to get an Integer 
    * value, and based on whether it is greater than 0, execute a 2nd or 
    * 3rd string of SQL, then disconnect, returning true if the Integer is 
    * greater than 0, using the default values for Connection, Logger, 
    * ConnectionPool, and DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strSelect  String of SQL to get the Integer value
    *@param  strSQLThen String of SQL to execute if the Integer is greater than 
    *                   0, or null or an empty string.
    *@param  strSQLElse String of SQL to execute if the Integer is greater less
    *                   than or equal to 0, or null or an empty string.
    *@return True if the Integer is greater than 0; false otherwise
    *@throws NoDataFoundException When strSelect returns no rows.
    *@throws SQLException         When a SQL error occurs, including when 
    *                             strSelect returns a non-Integer value.
    **************************************************************************/
    public boolean executeSQLIfIntegerGreaterThanZeroThenElse
                        (String strSelect
                        ,String strSQLThen
                        ,String strSQLElse)
                        throws NoDataFoundException
                              ,SQLException
    {
        boolean blnGreater = (getIntValueFromDB(strSelect) > 0);
        executeSQLIfThenElse(blnGreater, strSQLThen, strSQLElse);
        return blnGreater;
    }

    /**************************************************************************
    * Connect to the database, execute a SELECT statement to get an Integer 
    * value, and if it is greater than 0, execute a 2nd string of SQL, then 
    * disconnect, returning true if the Integer is greater than 0, using the 
    * default values for Connection, Logger, ConnectionPool, and DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strSelect  String of SQL to get the Integer value
    *@param  strSQLThen String of SQL to execute if the Integer is greater than 
    *                   0, or null or an empty string.
    *@return True if the Integer is greater than 0; false otherwise
    *@throws NoDataFoundException When strSelect returns no rows.
    *@throws SQLException         When a SQL error occurs, including when 
    *                             strSelect returns a non-Integer value.
    **************************************************************************/
    public boolean executeSQLIfIntegerGreaterThanZero
                        (String strSelect
                        ,String strSQLThen)
                        throws NoDataFoundException
                              ,SQLException

    {
        return executeSQLIfIntegerGreaterThanZeroThenElse
                                        (strSelect, strSQLThen, null);
    }

    /**************************************************************************
    * Connect to the database, execute a SELECT statement to get an Integer 
    * value, and if it is not greater than 0, execute a 2nd string of SQL, then 
    * disconnect, returning true if the Integer is not greater than 0, using the 
    * default values for Connection, Logger, ConnectionPool, and DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strSelect  String of SQL to get the Integer value
    *@param  strSQLThen String of SQL to execute if the Integer is not greater 
    *                   than 0, or null or an empty string.
    *@return True if the Integer is not greater than 0; false otherwise
    *@throws NoDataFoundException When strSelect returns no rows.
    *@throws SQLException         When a SQL error occurs, including when 
    *                             strSelect returns a non-Integer value.
    **************************************************************************/
    public boolean executeSQLIfIntegerNotGreaterThanZero
                        (String strSelect
                        ,String strSQLThen)
                        throws NoDataFoundException
                              ,SQLException

    {
        return executeSQLIfIntegerGreaterThanZeroThenElse
                                        (strSelect, null, strSQLThen);
    }

    /**************************************************************************
    * Return a SELECT statement composed from the specified table name, 
    * columns and WHERE clause.
    *@param  strTableName   Name of the database table
    *@param  strWhere       SQL WHERE clause to use in the SELECT statement 
    *                       to get the row count, without the keyword WHERE.
    *                       Used internally as:
    *                           SELECT COUNT(*) 
    *                           FROM strTableName
    *                           WHERE strWhere
    *@param  strCols        String of comma-separated column names to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@return The SELECT statement
    **************************************************************************/
    public String buildSelectString
                        (String strTableName
                        ,String strWhere
                        ,String strCols)
    {
        return   " select " + strCols
             + "\n from " + strTableName
             + "\n where " + strWhere
             ;
    }

    /**************************************************************************
    * Return a SELECT COUNT(*) FROM statement composed from the specified 
    * table name and WHERE clause.
    *@param  strTableName   Name of the database table.
    *@param  strWhere       SQL WHERE clause to use in the SELECT statement 
    *                       to get the row count, without the keyword WHERE.
    *                       Used internally as:
    *                           SELECT COUNT(*) 
    *                           FROM strTableName
    *                           WHERE strWhere
    *@return The SELECT statement
    **************************************************************************/
    public String buildSelectCountStarString
                        (String strTableName
                        ,String strWhere)
    {
        return buildSelectString(strTableName, strWhere, "count(*)"); 
    }

    /**************************************************************************
    * Return an INSERT statement composed from the specified table name, 
    * columns and values.
    *@param  strTableName   Name of the database table
    *@param  strCols        String of comma-separated column names to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@param  strVals        String of comma-separated values to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@return The INSERT statement
    **************************************************************************/
    public String buildInsertString
                        (String strTableName
                        ,String strCols
                        ,String strVals)
    {
        return   " insert into " + strTableName
             + "\n (" + strCols + ")"
             + "\n values"
             + "\n (" + strVals + ")"
             ;
    }

    /**************************************************************************
    * Connect to the database, execute a SELECT COUNT(*) FROM statement to 
    * decide if a row exists and if not, execute an INSERT statement possibly
    * into a different table, then disconnect, returning true if the row 
    * existed, using the default values for Connection, Logger, ConnectionPool, 
    * and DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strSelectCount SQL SELECT COUNT(*) FROM statement
    *@param  strTableName   Name of the database table
    *@param  strCols        String of comma-separated column names to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@param  strVals        String of comma-separated values to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@return True if the test row existed; false otherwise
    *@throws NoDataFoundException When strSelectCount returns no rows, instead 
    *                             of returning a row containing the count as
    *                             a SELECT COUNT(*) statement does.
    *@throws SQLException         When a SQL error occurs, including when 
    *                             strSelectCount returns a non-Integer value.
    **************************************************************************/
    public boolean insertIfNotExistsMultiTable
                        (String strSelectCount
                        ,String strTableName
                        ,String strCols
                        ,String strVals)
                        throws NoDataFoundException
                              ,SQLException
    {
        String strInsert = buildInsertString(strTableName, strCols, strVals);
        return executeSQLIfIntegerNotGreaterThanZero(strSelectCount, strInsert);
    }

    /**************************************************************************
    * Connect to the database, execute a SELECT COUNT(*) FROM statement to 
    * decide if a row exists and if not, execute an INSERT statement, then 
    * disconnect, returning true if the row existed, using the default values 
    * for Connection, Logger, ConnectionPool, and DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strTableName   Name of the database table.
    *@param  strWhere       SQL WHERE clause to use in the SELECT statement 
    *                       to get the row count, without the keyword WHERE.
    *                       Used internally as:
    *                           SELECT COUNT(*) 
    *                           FROM strTableName
    *                           WHERE strWhere
    *@param  strCols        String of comma-separated column names to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@param  strVals        String of comma-separated values to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@return True if the row existed; false otherwise
    *@throws SQLException
    **************************************************************************/
    public boolean insertIfNotExists
                        (String strTableName
                        ,String strWhere
                        ,String strCols
                        ,String strVals)
                        throws SQLException
    {
        String strSelect = buildSelectCountStarString(strTableName, strWhere);
        try
        {
            return insertIfNotExistsMultiTable
                        (strSelect, strTableName, strCols, strVals);
        }
        catch(NoDataFoundException exception)
        {
            // Can't happen.  SELECT COUNT(*) always returns a value, even if
            // the value is zero.  Any other problem with the SELECT statement
            // will cause a SQLException.
            return false;
        }
    }

    /**************************************************************************
    * Connect to the database, execute an INSERT statement, then disconnect,
    * using the default values for Connection, Logger, ConnectionPool, and 
    * DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strTableName   Name of the database table.
    *@param  strCols        String of comma-separated column names to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@param  strVals        String of comma-separated values to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@throws SQLException
    **************************************************************************/
    public void insert
                        (String strTableName
                        ,String strCols
                        ,String strVals)
                        throws SQLException
    {
        String strInsert = buildInsertString(strTableName, strCols, strVals);
        executeSQL(strInsert);
    }

    /**************************************************************************
    * Return an UPDATE statement composed from the specified table name, 
    * WHERE clause, columns and values.
    *@param  strTableName   Name of the database table
    *@param  strWhere       SQL WHERE clause to use in the UPDATE statement,
    *                       without the keyword WHERE.
    *                       Used internally as:
    *                           UPDATE strTableName 
    *                           SET strSetColsVals
    *                           WHERE strWhere
    *@param  strSetColsVals String of comma-separated column=value pairs to 
    *                       be used on the UPDATE as:
    *                           UPDATE strTableName 
    *                           SET strSetColsVals
    *                           WHERE strWhere
    *@return The UPDATE statement
    **************************************************************************/
    public String buildUpdateString
                        (String strTableName
                        ,String strWhere
                        ,String strSetColsVals)
    {
        return   " update " + strTableName
             + "\n set " + strSetColsVals
             + "\n where " + strWhere
             ;
    }

    /**************************************************************************
    * Connect to the database, execute an UPDATE statement, then disconnect,
    * using the default values for Connection, Logger, ConnectionPool, and 
    * DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strTableName   Name of the database table.
    *@param  strWhere       SQL WHERE clause to use in the UPDATE statement,
    *                       without the keyword WHERE.
    *                       Used internally as:
    *                           UPDATE strTableName 
    *                           SET strSetColsVals
    *                           WHERE strWhere
    *@param  strSetColsVals String of comma-separated column=value pairs to 
    *                       be used on the UPDATE as:
    *                           UPDATE strTableName 
    *                           SET strSetColsVals
    *                           WHERE strWhere
    *@throws SQLException
    **************************************************************************/
    public void update
                        (String strTableName
                        ,String strWhere
                        ,String strSetColsVals)
                        throws SQLException
    {
        String strUpdate = buildUpdateString
                                    (strTableName, strWhere, strSetColsVals);
        executeSQL(strUpdate);
    }

    /**************************************************************************
    * Connect to the database, execute a SELECT COUNT(*) FROM statement to 
    * decide if a row exists and based on its existence, execute an INSERT
    * or UPDATE statement, then disconnect, returning true if the row existed,
    * using the default values for Connection, Logger, ConnectionPool, and 
    * DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strTableName   Name of the database table.
    *@param  strWhere       SQL WHERE clause to use in the SELECT statement 
    *                       to get the row count, and in the UPDATE statement,
    *                       without the keyword WHERE.
    *                       Used internally as:
    *                           SELECT COUNT(*) 
    *                           FROM strTableName
    *                           WHERE strWhere
    *                       and:
    *                           UPDATE strTableName 
    *                           SET strSetColsVals
    *                           WHERE strWhere
    *@param  strCols        String of comma-separated column names to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@param  strVals        String of comma-separated values to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@param  strSetColsVals String of comma-separated column=value pairs to 
    *                       be used on the UPDATE as:
    *                           UPDATE strTableName 
    *                           SET strSetColsVals
    *                           WHERE strWhere
    *@return True if the row existed; false otherwise
    *@throws SQLException
    **************************************************************************/
    public boolean insertOrUpdate
                        (String strTableName
                        ,String strWhere
                        ,String strCols
                        ,String strVals
                        ,String strSetColsVals)
                        throws SQLException
    {
        String strSelect = buildSelectCountStarString(strTableName, strWhere);
        String strInsert = buildInsertString(strTableName, strCols, strVals);
        String strUpdate 
                    = buildUpdateString(strTableName, strWhere, strSetColsVals);
        try
        {
            return executeSQLIfIntegerGreaterThanZeroThenElse
                                (strSelect, strUpdate, strInsert);
        }
        catch(NoDataFoundException exception)
        {
            // Can't happen.  SELECT COUNT(*) always returns a value, even if
            // the value is zero.  Any other problem with the SELECT statement
            // will cause a SQLException.
            return false;
        }
    }

    /**************************************************************************
    * This class is used as the return value of insertOrUpdateReturnPK()
    * so that it can return a composite value.
    **************************************************************************/
    public static class InsertOrUpdateResult
    {
        public boolean blnExisted = false;
        public int     intPK      = 0;
    }
    
    /**************************************************************************
    * Connect to the database, execute a SELECT statement to get the primary
    * key of an existing row, and if it doesn't exist, execute an INSERT 
    * statement possibly into a different table and execute a different SELECT 
    * statement to get a primary key, then disconnect, returning an 
    * InsertOrUpdateResult object, using the default values for Connection, 
    * Logger, ConnectionPool, and DBConfig.
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strSelectPKExisting
    *                       SQL SELECT statement to get the primary key of the 
    *                       existing row.
    *@param  strSelectPKInserted
    *                       SQL SELECT statement to get the primary key of the 
    *                       inserted row.
    *@param  strTableName   Name of the database table
    *@param  strCols        String of comma-separated column names to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@param  strVals        String of comma-separated values to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@return InsertOrUpdateResult object containing the primary key returned 
    *        by one of the SELECT statements and a boolean flag that is true 
    *        if the test row existed; false otherwise.
    *@throws NoDataFoundException When, after successfully executing the 
    *                             INSERT, strSelectPKInserted returns no rows.
    *@throws SQLException
    **************************************************************************/
    public InsertOrUpdateResult insertIfNotExistsMultiTableReturnPK
                                                (String strSelectPKExisting
                                                ,String strSelectPKInserted
                                                ,String strTableName
                                                ,String strCols
                                                ,String strVals)
                                                throws NoDataFoundException
                                                      ,SQLException
    {
        InsertOrUpdateResult result = new InsertOrUpdateResult();
        try
        {
            result.intPK = getIntValueFromDB(strSelectPKExisting);
            result.blnExisted = true;
        }
        catch(NoDataFoundException exception)
        {
            insert(strTableName, strCols, strVals);
            result.intPK = getIntValueFromDB(strSelectPKInserted);
            result.blnExisted = false;
        }
        return result;
    }

    /**************************************************************************
    * Connect to the database, execute a SELECT statement to get the primary
    * key of an existing row, and if it doesn't exist, execute an INSERT 
    * statement into the table and re-execute the SELECT statement to get 
    * the primary key, then disconnect, returning an InsertOrUpdateResult 
    * object, using the default values for Connection, Logger, ConnectionPool, 
    * and DBConfig.
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strTableName   Name of the database table
    *@param  strPKName      Name of the primary key of the table.
    *@param  strWhere       SQL WHERE clause to use in the SELECT statement 
    *                       to get the primary key after doing or not doing 
    *                       the INSERT, without the keyword WHERE.
    *                       Used internally as:
    *                           SELECT strPKName
    *                           FROM strTableName
    *                           WHERE strWhere
    *@param  strCols        String of comma-separated column names to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@param  strVals        String of comma-separated values to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@return InsertOrUpdateResult object containing the primary key returned 
    *        from the table by strWhere, regardless of whether the INSERT 
    *        was necessary, and a boolean flag that is true if the row existed; 
    *        false otherwise.
    *@throws NoDataFoundException When, after successfully executing the 
    *                             INSERT, strWhere still returns no rows.
    *@throws SQLException
    **************************************************************************/
    public InsertOrUpdateResult insertIfNotExistsReturnPK
                                                (String strTableName
                                                ,String strPKName
                                                ,String strWhere
                                                ,String strCols
                                                ,String strVals)
                                                throws NoDataFoundException
                                                      ,SQLException
    {
        String strGetPK = buildSelectString(strTableName, strWhere, strPKName);
        return insertIfNotExistsMultiTableReturnPK
                                                (strGetPK
                                                ,strGetPK
                                                ,strTableName
                                                ,strCols
                                                ,strVals
                                                );
    }

    /**************************************************************************
    * Connect to the database, execute an UPDATE, then disconnect, returning 
    * an InsertOrUpdateResult object, using the default values for Connection, 
    * Logger, ConnectionPool, and DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strTableName   Name of the database table.
    *@param  strPKName      Name of the primary key of the table.
    *@param  strWhere       SQL WHERE clause to use in the UPDATE statement,
    *                       without the keyword WHERE.
    *@param  strSetColsVals String of comma-separated column=value pairs to 
    *                       be used on the UPDATE as:
    *                           UPDATE strTableName 
    *                           SET strSetColsVals
    *                           WHERE strWhere
    *@return InsertOrUpdateResult object containing the primary key of the 
    *        first updated row and a boolean flag that is true if such a row 
    *        existed; false otherwise.
    *@throws SQLException
    **************************************************************************/
    public InsertOrUpdateResult updateReturnPK
                                                (String strTableName
                                                ,String strPKName
                                                ,String strWhere
                                                ,String strSetColsVals)
                                                throws SQLException
    {
        InsertOrUpdateResult result = new InsertOrUpdateResult();
        String strGetPK = buildSelectString(strTableName, strWhere, strPKName);
        try
        {
            result.intPK = getIntValueFromDB(strGetPK);
            result.blnExisted = true;
        }
        catch(NoDataFoundException exception)
        {
            result.intPK = 0;
            result.blnExisted = false;
        }
        if (result.blnExisted)
        {
            update(strTableName, strWhere, strSetColsVals);
        }
        return result;
    }

    /**************************************************************************
    * Connect to the database, execute a SELECT statement to decide if a row 
    * exists and based on its existence, execute an INSERT or UPDATE statement, 
    * then disconnect, returning an InsertOrUpdateResult object, using the 
    * default values for Connection, Logger, ConnectionPool, and DBConfig.  
    * This method is useful when it is more convenient to set the defaults once 
    * than to specify them on each call.
    *@param  strTableName   Name of the database table.
    *@param  strPKName      Name of the primary key of the table.
    *@param  strWhere       SQL WHERE clause to use in the SELECT statement 
    *                       to get the primary key before doing the UPDATE
    *                       or after doing the INSERT, and to use in the 
    *                       UPDATE statement, without the keyword WHERE.
    *                       Used internally as:
    *                           SELECT strPKName
    *                           FROM strTableName
    *                           WHERE strWhere
    *                       and:
    *                           UPDATE strTableName 
    *                           SET strSetColsVals
    *                           WHERE strWhere
    *@param  strCols        String of comma-separated column names to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@param  strVals        String of comma-separated values to be used
    *                       on the INSERT as:
    *                           INSERT INTO strTableName 
    *                           ( strCols ) 
    *                           VALUES 
    *                           ( strVals )
    *@param  strSetColsVals String of comma-separated column=value pairs to 
    *                       be used on the UPDATE as:
    *                           UPDATE strTableName 
    *                           SET strSetColsVals
    *                           WHERE strWhere
    *@return InsertOrUpdateResult object containing the primary key of the 
    *        inserted or updated row and a boolean flag that is true if the 
    *        row existed; false otherwise.
    *@throws NoDataFoundException When strWhere specifies no rows in 
    *                             strTableName after the INSERT or UPDATE 
    *                             is done.
    *@throws SQLException
    **************************************************************************/
    public InsertOrUpdateResult insertOrUpdateReturnPK
                                                (String strTableName
                                                ,String strPKName
                                                ,String strWhere
                                                ,String strCols
                                                ,String strVals
                                                ,String strSetColsVals)
                                                throws NoDataFoundException
                                                      ,SQLException
    {
        InsertOrUpdateResult result = 
            updateReturnPK(strTableName, strPKName, strWhere, strSetColsVals);
        if (!result.blnExisted)
        {
            insert(strTableName, strCols, strVals);
            String strGetPK = buildSelectString
                                    (strTableName, strWhere, strPKName);
            result.intPK = getIntValueFromDB(strGetPK);
        }
        return result;
    }

    /**********************************************************************
    * Test the specified database connection.
    *@param  conn       Database connection
    *@return            True if valid; False if invalid.
    **********************************************************************/
    public static boolean databaseConnectionIsValid(Connection conn)
    {
        //-- Try a simple query to make sure the connection is valid.
        Statement st = null;
        ResultSet rs = null;
        try
        {
            st = conn.createStatement();
            rs = st.executeQuery(getConnectionTestString());  
            return true;
        }
        catch (Throwable e1)
        {
            //-- Nothing to do.  
            //-- Must suppress errors from this simple query.
            //-- No need to log the error.  The caller can do so.
            return false;
        }
        finally
        {
            //-- Clean up resources, suppressing any errors, regardless of 
            //-- whether the simple query succeeded.
            try
            {
                if (rs != null) { rs.close(); }
            }
            catch (Throwable e2)
            {
                //-- Nothing to do.  Suppress the error and don't log it.
            }
            try
            {
                if (st != null) { st.close(); }
            }
            catch (Throwable e3)
            {
                //-- Nothing to do.  Suppress the error and don't log it.
            }
        }
    }

    /**************************************************************************
    * Cleanup the database objects, optionally closing the database connection.
    *@param  dbContext  DBContext object containing the database objects.
    *@param  blnClose   Boolean flag indicating whether to close the connection.
    *                   Ignored if blnDoneWithConnection is false.
    *@param  blnDoneWithConnection
    *                   Boolean flag indicating whether the caller is done 
    *                   with the connection.  If so, it is released to the 
    *                   connection pool, unless blnClose indicates that it 
    *                   should be closed.
    *@param  logger     Logger to log operations to.
    *                   Optional.  If null, no logging is done.
    *@param  intLogLevelOfOperation
    *                   Level at which to log operations.
    *@param  pool       ConnectionPool to return the pooled connection to, or 
    *                   null if the connection is not to be returned.
    **************************************************************************/
    public static void cleanupDBContext
                                (DBContext      dbContext, 
                                 boolean        blnClose,
                                 boolean        blnDoneWithConnection,
                                 Logger         logger,
                                 int            intLogLevelOfOperation,
                                 ConnectionPool pool)
    {
        //-- Nothing to do if there are no database objects to clean up.
        if (dbContext == null)
        {
            return;
        }

        //-- Clean up resources, suppressing any errors.
        //-- Note:  It is important to suppress all errors here.  This is
        //--        typically called from the finally clause of the caller
        //--        which may already be in the process of throwing an error
        //--        to its caller.  Don't want another error to mask that one.
        try
        {
            if (dbContext.rs != null)
            {
                dbContext.rs.close();
                dbContext.rs = null;
            }
        }
        catch (Throwable e)
        {
            Logger.logSafely
                    (logger, 1, 
                     "ERROR cleanupDBContext (while closing ResultSet):\n" 
                     + ExcUtil.getStackTrace(e));  
        }
        try
        {
            if (dbContext.st != null)
            {
                dbContext.st.close();
                dbContext.st = null;
            }
        }
        catch (Throwable e)
        {
            Logger.logSafely
                    (logger, 1, 
                     "ERROR cleanupDBContext (while closing Statement):\n" 
                     + ExcUtil.getStackTrace(e));  
        }
        try
        {
            if (dbContext.conn != null)
            {
                if (blnDoneWithConnection)
                {
                    if (blnClose)
                    {
                        Logger.logSafely
                            (logger, 1, 
                             "BADCONN cleanupDBContext() is closing a bad"
                             + " database connection.");
                    }
                    if (pool != null)
                    {
                        pool.returnConnection(dbContext.conn, blnClose);
                        dbContext.conn = null;
                    }
                }
                else
                {
                    Logger.logSafely
                            (logger, 1, 
                             "KEEPCONN cleanupDBContext() is keeping a"
                             + " database connection reserved.");
                }
            }
        }
        catch (Throwable e)
        {
            Logger.logSafely
                    (logger, 1, 
                     "ERROR cleanupDBContext (while returning Connection" 
                      + " to pool):\n" 
                      + ExcUtil.getStackTrace(e));  
        }
        if (pool != null)
        {
            Logger.logSafely
                    (logger, intLogLevelOfOperation, 
                     "DB Connections : " 
                     + pool.getAvailableConnectionCount()
                     + "/"
                     + pool.getConnectionCount());
        }
    }

    /**************************************************************************
    * Cleanup the database objects, leaving the database connection open,
    * and returning it to the connection pool, if any.  This is a convenience
    * method to handle a common use case. 
    *@param  dbContext  DBContext object containing the database objects.
    *@param  logger     Logger to log operations to.
    *                   Optional.  If null, no logging is done.
    *@param  intLogLevelOfOperation
    *                   Level at which to log operations.
    *@param  pool       ConnectionPool to return the pooled connection to, or 
    *                   null if the connection is not to be returned.
    **************************************************************************/
    public static void cleanupDBContext
                                (DBContext      dbContext, 
                                 Logger         logger,
                                 int            intLogLevelOfOperation,
                                 ConnectionPool pool)
    {
        cleanupDBContext
                        (dbContext, 
                         !blnCLOSE_CONNECTION,
                         blnDONE_WITH_CONNECTION,
                         logger,
                         intLogLevelOfOperation,                         
                         pool);
    }

    /**************************************************************************
    * Cleanup the database objects, optionally closing the database connection,
    * using the default values for Logger and ConnectionPool.  This method is 
    * useful for the when it is more convenient to set the defaults once than 
    * to specify them on each call. 
    *@param  dbContext  DBContext object containing the database objects.
    *@param  blnClose   Boolean flag indicating whether to close the connection.
    *                   Ignored if blnDoneWithConnection is false.
    *@param  blnDoneWithConnection
    *                   Boolean flag indicating whether the caller is done 
    *                   with the connection.  If so, it is released to the 
    *                   connection pool, unless blnClose indicates that it 
    *                   should be closed.
    **************************************************************************/
    public void cleanupDBContext
                                (DBContext dbContext, 
                                 boolean blnClose,
                                 boolean blnDoneWithConnection)
    {
        cleanupDBContext
                (dbContext,
                 blnClose,
                 blnDoneWithConnection,
                 getDefaultLogger(),
                 getLogLevelOfOperation(),
                 getDefaultConnectionPool());
    }

    /**************************************************************************
    * Cleanup the database objects, leaving the database connection open,
    * and returning it to the connection pool, if any, using the default values 
    * for Logger and ConnectionPool.  This method is useful when it is more 
    * convenient to set the defaults once than to specify them on each call.
    *@param  dbContext  DBContext object containing the database objects.
    **************************************************************************/
    public void cleanupDBContext(DBContext dbContext)
    {
        cleanupDBContext
                (dbContext,
                 getDefaultLogger(),
                 getLogLevelOfOperation(),                 
                 getDefaultConnectionPool());
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
