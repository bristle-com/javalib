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

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

// ConnectionPool
/******************************************************************************
* This class encapsulates pools of database connections.
*<pre>
*<b>Usage:</b>
*       - The typical scenario for using this class is:
*         - Once, at startup:
*             ConnectionPool pool = new ConnectionPool
*                                         ("oracle.jdbc.driver.OracleDriver");
*         - At each point where a connection is needed.
*             Connection conn = pool.getConnection(objConfig);
*             ... Use the connection ...
*             pool.returnConnection(conn);
*         - If an error occurs while using the connection, you can advise 
*           the connection pool to close the connection and stop pooling it.
*             Connection conn = pool.getConnection(objConfig);
*             ... Try to use the connection, but decide that there is 
*                 something wrong with the connection, so you need a
*                 different one ...
*             pool.returnConnection(conn, true);
*             Connection conn = pool.getConnection(objConfig);
*             ... Use the connection ...
*             pool.returnConnection(conn);
*
*   - See the source code of the inner Tester class for more examples.
*  
*<b>Assumptions:</b>
*<b>Effects:</b>
*       - Creates and manages database connections.
*<b>Anticipated Changes:</b>
*       - Add code to close and remove connections that have been held for 
*         too long.  Client must have forgotten to return them to the pool.
*         Should close them to conserve resources.  Don't just return them 
*         to the pool.  May not be a good idea to let other clients use 
*         them since we're not positive the current client is done.
*       - Add code to limit the number of concurrent connections.
*       - Could be rewritten to require the caller to create a separate 
*         connection pool for each set of credentials, by moving the 
*         credentials from getConnection() to the constructor.  Advantages?
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class ConnectionPool
{

    //--
    //-- Class variables
    //--
    private static ConnectionPool st_poolSingleton = null;

    //--
    //-- Instance variables to support public properties
    //--

    //-- Limit on the number of times a database connection will be reused
    //-- before it is closed and a new one opened.
    //-- Note:  No need to make thread safe.  Doesn't matter if a race occurs
    //--        testing and setting it.  The set will happen eventually, and 
    //--        subsequent tests will use the new value.  No harm done if the 
    //--        old value is used by another thread a little longer.
    private int m_intMaxTimesToUse = 100;

    //-- Limit on the number of milliseconds a database connection will sit
    //-- idle in the pool before being closed.  The clock is reset for a 
    //-- connection by returnConnection() and checked occasionally for each 
    //-- connection by TimeoutIdleConnectionsThread, which is started by 
    //-- returnConnection() and runs until there are no available connections 
    //-- to monitor.
    //-- Note:  No need to make thread safe.  Doesn't matter if a race occurs
    //--        testing and setting it.  The set will happen eventually, and 
    //--        subsequent tests will use the new value.  No harm done if the 
    //--        old value is used by another thread a little longer.
    private static final long lngONE_HOUR = 1000 * 60 * 60;
    private long m_lngMaxIdleMillisecs = lngONE_HOUR;

    //--
    //-- Internal instance variables
    //--

    //-- Note:  Concurrent multi-threaded access to these instance variables
    //--        is prevented by the synchronized keyword in all places that 
    //--        access them.
    //--        Can't simply do as suggested in the javadocs for HashMap:
    //--            Map m = Collections.synchronizedMap(new HashMap(...));
    //--        for the following reasons:
    //--        1.  Need to maintain consistency between the multiple instance 
    //--            variables, not only protect the map variable.
    //--        2.  Need to protect sequences of operations on the map, not 
    //--            only individual operations.
    //--        3.  Need to protect indirect access to the map contents, like:
    //--                if (ci.blnAvailable) ci.blnAvailable = false;

    //-- Map with Connection as key and ConnectionInfo as value, and count of
    //-- available connections in the map.
    //-- Note: Flag m_intAvailable as volatile to ensure that 
    //--       TimeoutIdleConnectionsThread notices quickly when it hits 
    //--       zero.  For details, see:
    //--            http://java.sun.com/j2se/1.3/docs/guide/misc/threadPrimitiveDeprecation.html
    private          Map m_mapPool      = new HashMap();
    private volatile int m_intAvailable = 0;

    //-- Thread to timeout connections that have exceeded m_lngMaxIdleMillisecs.
    //-- Note: No need to create it until there are connections to monitor.
    private TimeoutIdleConnectionsThread m_timeoutIdleConnectionsThread = null;
    
    /**************************************************************************
    * This interface must be implemented by any class that expects to serve
    * as a DBConfig object for use with ConnectionPool.
    **************************************************************************/
    public interface DBConfig
    {
        /**********************************************************************
        * Get the DB URL.
        *@return            The DB URL.
        **********************************************************************/
        public String getDBURL();

        /**********************************************************************
        * Get the DB Username.
        *@return            The DB Username.
        **********************************************************************/
        public String getDBUsername();

        /**********************************************************************
        * Get the DB Password.
        *@return            The DB Password.
        **********************************************************************/
        public String getDBPassword();
    }

    /**************************************************************************
    * Sample class implementing the DBConfig interface.
    **************************************************************************/
    public static class SimpleDBConfig implements DBConfig
    {
        private String  m_strDBURL      = null;
        private String  m_strDBUsername = null;
        private String  m_strDBPassword = null;

        /**********************************************************************
        * Constructor.
        *@param  strDBURL       DB URL used for connection
        *@param  strDBUsername  DB username used for connection
        *@param  strDBPassword  DB password used for connection
        **********************************************************************/
        public SimpleDBConfig(String strDBURL,
                              String strDBUsername,
                              String strDBPassword)
        {
            m_strDBURL      = strDBURL;
            m_strDBUsername = strDBUsername;
            m_strDBPassword = strDBPassword;
        }

        /**********************************************************************
        * Get the DB URL.
        *@return            The DB URL.
        **********************************************************************/
        public String getDBURL()
        {
            return m_strDBURL;
        }

        /**********************************************************************
        * Get the DB Username.
        *@return            The DB Username.
        **********************************************************************/
        public String getDBUsername()
        {
            return m_strDBUsername;
        }

        /**********************************************************************
        * Get the DB Password.
        *@return            The DB Password.
        **********************************************************************/
        public String getDBPassword()
        {
            return m_strDBPassword;
        }
    }

    /**************************************************************************
    * Internal class to store the info about each connection in the pool.  
    * It is used as the value stored in m_mapPool.
    **************************************************************************/
    private class ConnectionInfo
    {
        //-- Each instance is created just in time to record that a new 
        //-- connection has been created and is about to be used.  Therefore, 
        //-- record it initially as unavailable and having been used once.
        public  boolean blnAvailable            = false;
        public  int     intHowOftenUsed         = 1;
        public  long    lngLastUsedMilleseconds = System.currentTimeMillis();

        //-- Config info used to open the connection.
        private DBConfig m_objConfig = null;

        /**********************************************************************
        * Constructor.
        *@param  objConfig    Info used to open the connection.
        **********************************************************************/
        public ConnectionInfo(DBConfig objConfig)
        {
            //-- Note: Keep a private readonly copy of the config info used 
            //--       to create this connection.  If we instead kept a 
            //--       pointer to the DBConfig object passed by the caller, 
            //--       the caller could change the values on the fly, which 
            //--       would not make sense while we are holding the open 
            //--       connection in the pool.
            m_objConfig = 
                    new SimpleDBConfig(objConfig.getDBURL(), 
                                       objConfig.getDBUsername(),
                                       objConfig.getDBPassword());
        }

        /**********************************************************************
        * Get the DB URL.
        *@return            The DB URL.
        **********************************************************************/
        public String getDBURL()
        {
            return m_objConfig.getDBURL();
        }

        /**********************************************************************
        * Get the DB Username.
        *@return            The DB Username.
        **********************************************************************/
        public String getDBUsername()
        {
            return m_objConfig.getDBUsername();
        }

        /**********************************************************************
        * Get the DB Password.
        *@return            The DB Password.
        **********************************************************************/
        public String getDBPassword()
        {
            return m_objConfig.getDBPassword();
        }
    }

    /**************************************************************************
    * Internal class used as a separate thread to automatically close 
    * connections that have been idle in the connection pool for too long.
    **************************************************************************/
    private class TimeoutIdleConnectionsThread extends Thread
    {
        /**********************************************************************
        * Run the thread. 
        **********************************************************************/
        public void run()
        {
            while (m_intAvailable > 0)
            {
                clearIdle();
                // Sleep until it is time to check again.
                // Note: No need to check more often than 10 times each timeout 
                //       period.  Would just add overhead to check more often.
                // Note: Don't check more often than once per second.  Wasteful.
                //       Also, this prevents checking constantly if
                //       m_lngMaxIdleMillisecs / 10 evaluates to 0.        
                try
                {
                    sleep(Math.max(1000, m_lngMaxIdleMillisecs / 10));
                }
                catch (InterruptedException e)
                {
                    //-- Nothing to do.  If interrupted, we just loop again 
                    //-- sooner than usual.  Also, this explicitly provides a 
                    //-- way for calls to interrupt() to cause this thread to
                    //-- notice changes to the value of m_lngMaxIdleMillisecs
                    //-- more quickly. 
                }
            }

            // Release the thread.  There are no connections to monitor.   
            m_timeoutIdleConnectionsThread = null;
        }
    }
    
    /**************************************************************************
    * Common method to be called from all constructors.
    *@param  strDBDriverClassName    Name of the database JDBC driver class.
    *                                Example:  oracle.jdbc.driver.OracleDriver   
    *@throws ClassNotFoundException
    *                   When driver class not installed.
    *@throws InstantiationException
    *                   When driver class can't be instantiated.
    *@throws IllegalAccessException
    *                   When driver class can't be accessed.
    **************************************************************************/
    protected void init(String strDBDriverClassName)
           throws ClassNotFoundException,
                  InstantiationException,
                  IllegalAccessException
    {
        //-- Load the JDBC driver.
        //-- Note:  The newInstance() call is a work around that according
        //--        to the MySQL Connector/J docs is necessary for "some
        //--        broken Java implementations".
        Class.forName(strDBDriverClassName).newInstance();
    }

    /**************************************************************************
    * Constructor.
    *@param  strDBDriverClassName    Name of the database JDBC driver class.
    *                                Example:  "oracle.jdbc.driver.OracleDriver"   
    *@throws ClassNotFoundException
    *                   When driver class not installed.
    *@throws InstantiationException
    *                   When driver class can't be instantiated.
    *@throws IllegalAccessException
    *                   When driver class can't be accessed.
    **************************************************************************/
    public ConnectionPool(String strDBDriverClassName)
           throws ClassNotFoundException,
                  InstantiationException,
                  IllegalAccessException
    {
        init(strDBDriverClassName);
    }

    /**************************************************************************
    * Constructor.
    *@param  strDBDriverClassName    Name of the database JDBC driver class.
    *                                Example:  "oracle.jdbc.driver.OracleDriver"   
    *@param  intMaxTimesToUse   Max times to reuse connection before closing
    *                           and opening a new one.
    *@throws ClassNotFoundException
    *                           When driver class not installed.
    *@throws InstantiationException
    *                           When driver class can't be instantiated.
    *@throws IllegalAccessException
    *                           When driver class can't be accessed.
    **************************************************************************/
    public ConnectionPool(String strDBDriverClassName, int intMaxTimesToUse)
           throws ClassNotFoundException,
                  InstantiationException,
                  IllegalAccessException
    {
        m_intMaxTimesToUse = intMaxTimesToUse;
        init(strDBDriverClassName);
    }

    /**************************************************************************
    * Set the max number of times to use the connection before closing it and
    * opening a new one.  Actually, the number of times to give it to an 
    * application via getConnection() for potentially multiple uses and accept 
    * it back via returnConnection() before closing it and opening a new one.
    * This is useful for keeping connections from getting too old in case they
    * leak resources of some sort by being used, or somehow become stale.  It
    * also offers the application a way to force a peak number of connections 
    * to be released in a controlled way.  Setting it low will force each 
    * connection to release itself after the next use.  Then set it high again.
    *@param  intVal     The new value.
    **************************************************************************/
    public void setMaxTimesToUse(int intVal)
    {
        m_intMaxTimesToUse = intVal;
    }

    /**************************************************************************
    * Get the max number of times to use the connection before closing it and
    * opening a new one.  Actually, the number of times to give it to an 
    * application via getConnection() for potentially multiple uses and accept 
    * it back via returnConnection() before closing it and opening a new one.
    *@return            The max number.
    **************************************************************************/
    public int getMaxTimesToUse()
    {
        return m_intMaxTimesToUse;
    }

    /**************************************************************************
    * Set the max number of milliseconds a database connection will sit idle 
    * in the pool before being closed.  This is especially useful with 
    * databases like MySQL that timeout idle connections after 8 hours, 
    * causing further attempts at using them to throw exceptions. 
    * Connections exceeding this idle time are closed and removed from 
    * the pool by a monitor thread.  Connections dispensed to the application
    * by getConnection() are not closed; only those returned to the pool by 
    * returnConnection().  The idle time for a connection is reset to zero 
    * milliseconds by returnConnection().  
    * Note:  The monitor thread runs no more than 10 times per max number of 
    *        milliseconds, and never more than once per second, so connections 
    *        may sit idle up to 1 second longer or even up to 10% longer than 
    *        specified. 
    *@param  lngVal     The new value
    **************************************************************************/
    public void setMaxIdleMillisecs(long lngVal)
    {
        m_lngMaxIdleMillisecs = lngVal;

        // Interrupt the thread if it is running, so it can notice the new 
        // value and adjust its sleep interval without waiting for the old 
        // interval to expire.
        synchronized (this)
        {
            if (m_timeoutIdleConnectionsThread != null)
            {
                m_timeoutIdleConnectionsThread.interrupt();
            }
        }
    }

    /**************************************************************************
    * Get the max number of milliseconds a database connection will sit idle 
    * in the pool before being closed.
    *@return            The max number.
    **************************************************************************/
    public long getMaxIdleMillisecs()
    {
        return m_lngMaxIdleMillisecs;
    }

    /**************************************************************************
    * Get an existing connection from the pool, if any are available.  Do not 
    * create a new connection.
    *@return               Database connection, or null.
    **************************************************************************/
    private synchronized Connection getAvailableConnection
                                            (DBConfig objConfig)
    {
        if (m_intAvailable == 0)
        {
            return null;
        }
        Iterator i = m_mapPool.entrySet().iterator();
        while (i.hasNext())
        {
            Map.Entry entry = (Map.Entry)i.next();
            ConnectionInfo ci = (ConnectionInfo)entry.getValue();
            if (   ci.blnAvailable
                && ci.getDBURL()     .equals(objConfig.getDBURL()     )
                && ci.getDBUsername().equals(objConfig.getDBUsername())
                && ci.getDBPassword().equals(objConfig.getDBPassword())
               )
            {
                ci.blnAvailable = false;
                ci.intHowOftenUsed++;
                m_intAvailable--;
                return (Connection)entry.getKey();
            }
        }
        return null;
    }

    /**************************************************************************
    * Get a connection from the pool.
    *@param  objConfig     Configuration data needed to connect to the database.
    *@return               Database connection.
    *@throws SQLException  When unable to connect to the database.
    **************************************************************************/
    public Connection getConnection(DBConfig objConfig)
           throws SQLException
    {
        return getConnectionWithRetry(objConfig, true);
    }

    /**************************************************************************
    * Create and start a new TimeoutIdleConnectionsThread, if necessary.
    **************************************************************************/
    private synchronized void startTimeoutIdleConnectionsThreadIfNecessary()
    {
        // Note: Mark the thread as a daemon so it doesn't keep the JVM from 
        //       exiting.
        // Note: Create it here on the fly, rather than just once statically,
        //       to reduce overhead when there are no connections in the pool.
        if (m_timeoutIdleConnectionsThread == null)
        {
            m_timeoutIdleConnectionsThread = new TimeoutIdleConnectionsThread();
            m_timeoutIdleConnectionsThread.setDaemon(true);
            m_timeoutIdleConnectionsThread.start();
        }
    }

    /**************************************************************************
    * Make a new connection.
    *@param  objConfig     Configuration data needed to connect to the database.
    *@return               Database connection.
    *@throws SQLException  When unable to connect to the database.
    **************************************************************************/
    protected Connection makeNewConnection(DBConfig objConfig)
           throws SQLException
    {
        return DriverManager.getConnection(objConfig.getDBURL(),
                                               objConfig.getDBUsername(),
                                               objConfig.getDBPassword());
    }

    /**************************************************************************
    * Get a connection from the pool, retrying if an error occurs.
    *@param  objConfig     Configuration data needed to connect to the database.
    *@param  blnRetry      Boolean flag indicating whether to retry.
    *@return               Database connection.
    *@throws SQLException  When unable to connect to the database.
    **************************************************************************/
    private Connection getConnectionWithRetry
                        (DBConfig objConfig, boolean blnRetry)
           throws SQLException
    {
        //-- Get a connection from the pool.
        Connection conn = getAvailableConnection(objConfig);

        //-- Create a new connection, if necessary, and add it to the pool.
        if (conn == null)
        {
            conn = makeNewConnection(objConfig);
            synchronized(this)
            {
                m_mapPool.put(conn, new ConnectionInfo(objConfig));
                //-- Note:  No need to increment m_intAvailable.  The newly 
                //--        added connection is already consumed, not 
                //--        available.
            }
        }

        //-- Set all connections, newly created or recycled, to not 
        //-- autocommit.  Defaulting to false is safer than true.  No 
        //-- chance of accidentally committing on behalf of a caller.
        //-- Let the caller decide whether to commit, to rollback, or 
        //-- to set the connection to autocommit.  
        conn.setAutoCommit(false);
        
//?? The following code is commented out for speed.  No need to test the 
//?? database connection before we pass it to the caller.  Too slow.
//??        //-- Try a simple query to make sure the connection is still valid.
//??        Statement st = null;
//??        ResultSet rs = null;
//??        try
//??        {
//??            st = conn.createStatement();
//??            rs = st.executeQuery("select sysdate from dual");
//??        }
//??        catch (SQLException e)
//??        {
//??            if (blnRetry)
//??            {
//??                //-- Recur once, forcing a new connection, to see if the error
//??                //-- can be fixed.  Perhaps the pooled connection just timed out.
//??                try
//??                {
//??                    conn.close();
//??                }
//??                catch (Throwable excIgnored)
//??                {
//??                    //-- Nothing to do.
//??                }
//??                synchronized(this)
//??                {
//??                    m_mapPool.remove(conn);
//??                    //-- Note:  No need to update m_intAvailable.  It was
//??                    //--        already updated to indicate that this 
//??                    //--        connection in unavailable.  Removing the
//??                    //--        connection from the pool leaves it still
//??                    //--        unavailable.
//??                }
//??                return getConnectionWithRetry(objConfig, false);
//??            }
//??            else
//??            {
//??                //-- Throw the exception to the caller, with all original
//??                //-- exception details.  The caller will have to deal with it.
//??                throw e;
//??            }
//??        }
//??        finally
//??        {
//??            //-- Cleanup all database resources, explicitly suppressing any
//??            //-- more errors.  Don't know how much of this cleanup is
//??            //-- necessary, and don't want any further errors to mask an
//??            //-- error that has already occurred and is about to be thrown to
//??            //-- the caller.
//??            try
//??            {
//??                rs.close();
//??                st.close();
//??            }
//??            catch (Throwable e)
//??            {
//??                //-- Nothing to do.
//??            }
//??        }

        return conn;
    }

    /**************************************************************************
    * Return a connection to the pool, optionally closing it so it won't be 
    * used anymore.
    *@param  conn          Connection to return.
    *@param  blnClose      Boolean flag indicating whether to close the 
    *                      connection.
    *@throws SQLException  When unable to rollback the returned connection.
    **************************************************************************/
    public synchronized void returnConnection(Connection conn, boolean blnClose)
           throws SQLException
    {
        ConnectionInfo ci = (ConnectionInfo)m_mapPool.get(conn);
        
        //-- Close old tired connections instead of putting them back
        //-- in the pool.  Also close connections at caller's request.
        //-- Note:  This is strictly a preventative measure.  Just in case a 
        //--        database connection leaks memory or in any other way gets 
        //--        old and tired.  Probably not a good idea to reuse the 
        //--        same connection over and over for months.
        if (blnClose || (ci.intHowOftenUsed >= m_intMaxTimesToUse))
        {
            //-- Old tired connection, or caller requested it to be closed.
            //-- Close it, suppressing errors.
            m_mapPool.remove(conn);
            //-- Note:  No need to update m_intAvailable.  The connection 
            //--        being returned is not going back into the pool.
            try
            {
                conn.close();
            }
            catch (Throwable e)
            {
                //-- Nothing to do.
            }
        }
        else
        {
            //-- Young connection.  Put it back in the pool.
            //-- Note:  Do a rollback in case the previous client never did a 
            //--        commit or a rollback.  Don't want the next client of
            //--        the same connection to accidentally commit or rollback
            //--        operations done by the previous client.
            //-- Note:  Set autocommit false to prevent errors thrown by 
            //--        rollback().
            conn.setAutoCommit(false);
            conn.rollback();
            ci.lngLastUsedMilleseconds = System.currentTimeMillis();
            ci.blnAvailable = true;
            m_intAvailable++;

            //-- Note: Start checking for idle connections now that there is
            //--       at least one available that may be idle. 
            startTimeoutIdleConnectionsThreadIfNecessary();
        }
    }

    /**************************************************************************
    * Return a connection to the pool for reuse by others.
    *@param  conn          Connection to return.
    *@throws SQLException  When unable to rollback the returned connection.
    **************************************************************************/
    public void returnConnection(Connection conn)
           throws SQLException
    {
        returnConnection(conn, false);
    }

    /**************************************************************************
    * Clear all available connections from the pool, closing them.
    **************************************************************************/
    public synchronized void clearAvailable()
    {
        if (m_intAvailable == 0)
        {
            return;
        }
        Iterator i = m_mapPool.entrySet().iterator();
        while (i.hasNext())
        {
            Map.Entry entry = (Map.Entry)i.next();
            ConnectionInfo ci = (ConnectionInfo)entry.getValue();
            if (ci.blnAvailable)
            {
                m_intAvailable--;
                Connection conn = (Connection)entry.getKey();
                i.remove();
                try
                {
                    conn.close();
                }
                catch (Throwable e)
                {
                    //-- Nothing to do.
                }
            }
        }
    }

    /**************************************************************************
    * Clear all available connections that have been idle too long from the 
    * pool, closing them.
    **************************************************************************/
    public synchronized void clearIdle()
    {
        if (m_intAvailable == 0)
        {
            return;
        }

        long lngNow = System.currentTimeMillis(); 
        Iterator i = m_mapPool.entrySet().iterator();
        while (i.hasNext())
        {
            Map.Entry entry = (Map.Entry)i.next();
            ConnectionInfo ci = (ConnectionInfo)entry.getValue();
            if (ci.blnAvailable) 
            {
                long lngIdleMillisecs = 
                        lngNow - ci.lngLastUsedMilleseconds;
                boolean blnIdle = lngIdleMillisecs > m_lngMaxIdleMillisecs;
                if (blnIdle) 
                {
                    m_intAvailable--;
                    Connection conn = (Connection)entry.getKey();
                    i.remove();
                    try
                    {
                        conn.close();
                    }
                    catch (Throwable e)
                    {
                        //-- Nothing to do.
                    }
                }
            }
        }
    }

    /**************************************************************************
    * Clear all connections from the pool, closing them.
    *<pre>
    *<b>Note:</b>
    *      Dangerous.  Closes all database connections in the entire pool
    *      regardless of whether they are currently allocated by a client.
    *      Use clearAvailable() instead when appropriate.
    *</pre>
    **************************************************************************/
    public synchronized void clear()
    {
        Iterator i = m_mapPool.keySet().iterator();
        while (i.hasNext())
        {
            Connection conn = (Connection)i.next();
            i.remove();
            try
            {
                conn.close();
            }
            catch (Throwable e)
            {
                //-- Nothing to do.
            }
        }
        m_intAvailable = 0;
    }

    /**************************************************************************
    * Get the count of connections in the pool.
    *@return               Count of connections.
    **************************************************************************/
    public synchronized int getConnectionCount()
    {
        return m_mapPool.size();
    }

    /**************************************************************************
    * Get the count of available connections in the pool.
    *@return               Count of available connections.
    **************************************************************************/
    public int getAvailableConnectionCount()
    {
        return m_intAvailable;
    }

    /**************************************************************************
    * Set the singleton ConnectionPool.
    *@param  pool     The ConnectionPool instance to be stored as the singleton.
    **************************************************************************/
    public static void setSingleton(ConnectionPool pool)
    {
        st_poolSingleton = pool;
    }

    /**************************************************************************
    * Get the singleton ConnectionPool, if any.
    *@return            The singleton ConnectionPool, or null.
    **************************************************************************/
    public static ConnectionPool getSingleton()
    {
        return st_poolSingleton;
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
        private static void showStats(ConnectionPool pool)
        {
            System.out.println (  pool.getAvailableConnectionCount()
                                + "/" 
                                + pool.getConnectionCount());
        }

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
            try
            {
                String strDBURL = "jdbc:oracle:thin:@bristle.com:1525:db_SID";
                String strDBUsername = "fred";
                String strDBPassword = "bogus_password";
                DBConfig objConfig = 
                    new SimpleDBConfig(strDBURL, strDBUsername, strDBPassword);
//                ConnectionPool pool = new ConnectionPool
//                                        ("oracle.jdbc.driver.OracleDriver", 10);
                ConnectionPool pool = new DummyConnectionPool("", 10);

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- Test 1:");
                System.out.println ("-- Get and return a bunch of connections.");
                System.out.println ("-- Pool never grows above size 1.");
                System.out.println ("-- After 10 uses, connection is closed.");
                System.out.println ("-- On the 11th use, a new one is opened.");
                System.out.println ("--");
                //-------------------------------------------------------------
                {
                    Connection conn = null;
                    showStats(pool);

                    System.out.println ("Getting connection from the pool (new)");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool (and closing it)");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool (new)");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);

                    System.out.println ("Getting connection from the pool");
                    conn = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn);
                    showStats(pool);
                }

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- Test 2:");
                System.out.println ("-- Get multiple concurrent connections.");
                System.out.println ("-- Each one is separately created.");
                System.out.println ("--");
                //-------------------------------------------------------------
                {
                    showStats(pool);
                    pool.clear();
                    showStats(pool);

                    System.out.println ("Getting connection from the pool (new)");
                    Connection conn1  = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Getting connection from the pool (new)");
                    Connection conn2  = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Getting connection from the pool (new)");
                    Connection conn3  = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Getting connection from the pool (new)");
                    Connection conn4  = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Getting connection from the pool (new)");
                    Connection conn5  = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Getting connection from the pool (new)");
                    Connection conn6  = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Getting connection from the pool (new)");
                    Connection conn7  = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Getting connection from the pool (new)");
                    Connection conn8  = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Getting connection from the pool (new)");
                    Connection conn9  = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Getting connection from the pool (new)");
                    Connection conn10 = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Getting connection from the pool (new)");
                    Connection conn11 = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Getting connection from the pool (new)");
                    Connection conn12 = pool.getConnection(objConfig);
                    showStats(pool);

                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn1);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn2);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn3);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn4);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn5);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn6);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn7);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn8);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn9);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn10);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn11);
                    showStats(pool);
                    System.out.println ("Returning connection to the pool");
                    pool.returnConnection(conn12);
                    showStats(pool);
                }

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- Test 3:");
                System.out.println ("-- Get and reuse multiple connections.");
                System.out.println ("--");
                //-------------------------------------------------------------
                {
                    showStats(pool);
                    pool.clear();
                    showStats(pool);

                    System.out.println ("Get 5 connections:");
                    Connection conn1  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn2  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn3  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn4  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn5  = pool.getConnection(objConfig);
                    showStats(pool);

                    System.out.println ("Return and re-get the same 5:");
                    pool.returnConnection(conn1);
                    showStats(pool);
                    pool.returnConnection(conn2);
                    showStats(pool);
                    pool.returnConnection(conn3);
                    showStats(pool);
                    pool.returnConnection(conn4);
                    showStats(pool);
                    pool.returnConnection(conn5);
                    showStats(pool);
                    Connection conn6  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn7  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn8  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn9  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn10 = pool.getConnection(objConfig);
                    showStats(pool);

                    System.out.println ("Return and re-get the first one:");
                    pool.returnConnection(conn6);
                    showStats(pool);
                    Connection conn11 = pool.getConnection(objConfig);
                    showStats(pool);

                    System.out.println ("Get 1 more:");
                    Connection conn12 = pool.getConnection(objConfig);
                    showStats(pool);

                    System.out.println ("Return and re-get the last one:");
                    pool.returnConnection(conn12);
                    showStats(pool);
                    Connection conn13 = pool.getConnection(objConfig);
                    showStats(pool);

                    System.out.println ("Return and re-get a middle one:");
                    pool.returnConnection(conn9);
                    showStats(pool);
                    Connection conn14 = pool.getConnection(objConfig);
                    showStats(pool);
                
                    System.out.println ("Clear the entire pool:");
                    pool.clear();
                    showStats(pool);
                    // Use the local variables to prevent compiler warnings.
                    if (conn7 != null) { conn7 = null; }
                    if (conn8 != null) { conn8 = null; }
                    if (conn10 != null) { conn10 = null; }
                    if (conn11 != null) { conn11 = null; }
                    if (conn13 != null) { conn13 = null; }
                    if (conn14 != null) { conn14 = null; }

                    System.out.println ("Get 1 more:");
                    Connection conn15 = pool.getConnection(objConfig);
                    showStats(pool);
                    pool.returnConnection(conn15);
                    showStats(pool);
                }

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- Test 4:");
                System.out.println ("-- Advise the pool to close a connection.");
                System.out.println ("--");
                //-------------------------------------------------------------
                {
                    showStats(pool);
                    pool.clear();
                    showStats(pool);

                    System.out.println ("Get a connection:");
                    Connection conn1  = pool.getConnection(objConfig);
                    showStats(pool);
                    System.out.println ("Return and close it:");
                    pool.returnConnection(conn1, true);
                    showStats(pool);
                }

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- Test 5:");
                System.out.println ("-- Get a bunch of connections, return");
                System.out.println ("-- half, then close the returned");
                System.out.println ("-- (available) ones.");
                System.out.println ("--");
                //-------------------------------------------------------------
                {
                    showStats(pool);
                    pool.clear();
                    showStats(pool);

                    System.out.println ("Get 10 connections:");
                    Connection conn1  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn2  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn3  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn4  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn5  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn6  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn7  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn8  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn9  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn10 = pool.getConnection(objConfig);
                    showStats(pool);

                    System.out.println ("Return 5:");
                    pool.returnConnection(conn1);
                    showStats(pool);
                    pool.returnConnection(conn2);
                    showStats(pool);
                    pool.returnConnection(conn3);
                    showStats(pool);
                    pool.returnConnection(conn4);
                    showStats(pool);
                    pool.returnConnection(conn5);
                    showStats(pool);

                    System.out.println ("Close the 5 returned connections:");
                    pool.clearAvailable();
                    showStats(pool);

                    System.out.println ("Clear the entire pool:");
                    pool.clear();
                    showStats(pool);
                    // Use the local variables to prevent compiler warnings.
                    if (conn6 != null) { conn6 = null; }
                    if (conn7 != null) { conn7 = null; }
                    if (conn8 != null) { conn8 = null; }
                    if (conn9 != null) { conn9 = null; }
                    if (conn10 != null) { conn10 = null; }
                }

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- Test 6:");
                System.out.println ("-- Get and return some connections, then");
                System.out.println ("-- watch them timeout.");
                System.out.println ("--");
                //-------------------------------------------------------------
                {
                    showStats(pool);
                    pool.clear();
                    showStats(pool);

                    System.out.println ("Get 2 connections:");
                    Connection conn1  = pool.getConnection(objConfig);
                    showStats(pool);
                    Connection conn2  = pool.getConnection(objConfig);
                    showStats(pool);

                    System.out.println ("Set the timeout to be very short:");
                    long lngMaxIdleMillisecs = pool.getMaxIdleMillisecs();
                    pool.setMaxIdleMillisecs(1000);
                    
                    System.out.println ("Return the connections and wait for ");
                    System.out.println ("them to timeout:");
                    pool.returnConnection(conn1);
                    showStats(pool);
                    pool.returnConnection(conn2);
                    showStats(pool);
                    Thread.sleep(5000);
                    showStats(pool);

                    System.out.println ("Restore timeout value, clear the pool:");
                    pool.setMaxIdleMillisecs(lngMaxIdleMillisecs);
                    pool.clear();
                    showStats(pool);
                }
            }
            catch (Throwable e)
            {
                System.out.println("Error in main(): ");
                e.printStackTrace();
            }
            System.out.println ("...End tests.");
        }
    }
}
