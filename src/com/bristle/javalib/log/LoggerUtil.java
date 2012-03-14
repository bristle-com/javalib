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

// LoggerUtil
/******************************************************************************
* This class supports a useful convention for logging via the Logger class, 
* and makes the logging calls simpler, shorter, and less obtrusive.
*<pre>
*<b>Usage:</b>
*   - See the {@link Logger} documentation for the typical appearance of 
*     a log file line without this class.  This class enhances that format.
*   - When used as described below, this class generates log file lines  
*     that:
*         1)  Have a <UserInfo> field that contains a user name, optional 
*             location and optional user agent, formatted as: 
*                   username
*                   username@location
*                   anonymous
*                   anonymous@location
*                   username/useragent
*                   username@location/useragent
*                   anonymous/useragent
*                   anonymous@location/useragent
*             The caller can specify:
*             - The username, which defaults to "anonymous".
*             - The location, which has no default but is typically specified 
*               as an IP address, host name, domain name, fully qualified 
*               hostname, or any other string.
*             - The user agent, which has no default but is typically specified
*               as a short identifier of a Web browser, operating system, or 
*               other user agent, or any other string.
*             Examples:   
*                   fred
*                   fred@127.0.0.1
*                   fred@neptune
*                   fred@bristle.com
*                   fred@neptune.bristle.com
*                   fred@"The Internet Cafe"
*                   fred@The Internet Cafe  (A way to create the effect of new 
*                                            space-separated fields in the 
*                                            logger following the username and
*                                            location.)  
*                   fred/FF2
*                   fred@127.0.0.1/WinXP
*                   fred@neptune/IE6WinXP
*                   fred@bristle.com/FF2Linux
*                   fred@neptune.bristle.com/Moz1
*                   fred@"The Internet Cafe"/[[Mozilla/5.0 (Windows; U; 
*                     Windows NT 5.0; en-US; rv:1.8.0.12) Gecko/20070508 
*                     Firefox/1.5.0.12]]
*                                           (Makes for very long log lines.
*                                            It is probably better to log all 
*                                            this detail once per login session
*                                            or something, not once per line.
*                                            However, you can use as long a 
*                                            string, with as many of your own 
*                                            delimiters, as you like.) 
*                   fred/RedHat More words
*                                           (A way to create the effect of new 
*                                            space-separated fields in the 
*                                            logger following the username
*                                            and user agent.)  
*                   fred@neptune/FF2 More words
*                                           (A way to create the effect of new 
*                                            space-separated fields in the 
*                                            logger following the username,
*                                            location, and user agent.)  
*                   anonymous
*                   anonymous@127.0.0.1
*                   anonymous@neptune
*                   etc...
*             Note: See the HttpUtil.getHttpUserAgentAbbrev() method, as a way 
*                   to produce short unique strings from the HTTP user-agent 
*                   header of common Web browsers.      
*         2)  Have a structured <Msg> field on each line to produce a nested 
*             effect like: 
*                   BEGIN ContextBO.login()
*                   . BEGIN ContextBO.hasRight(LOGIN)
*                   . . BEGIN getResultSet()
*                   . . END   getResultSet() : 5
*                   . END   ContextBO.hasRight(LOGIN) : 5
*                   END   ContextBO.login() : 6
*                   BEGIN ProductListBO.load()
*                   . BEGIN ContextBO.hasRight(VIEW_PRODUCT)
*                   . . BEGIN getResultSet()
*                   . . END   getResultSet() : 24
*                   . END   ContextBO.hasRight(VIEW_PRODUCT) : 25
*                   . BEGIN Getting product data from the database.
*                   . . BEGIN getResultSet()
*                   . . END   getResultSet() : 12
*                   . END   Getting product data from the database. : 13
*                   END   ProductListBO.load() : 40
*             where:
*             - The numbers following the colons (5, 6, 24, etc.) on lines
*               starting with "END" are the number of milliseconds since the
*               corresponding "BEGIN".
*
*   - The typical scenario for using this class is:
*       LoggerUtil loggerUtil = new LoggerUtil
*           (safelyWithoutThrowingAnyExceptionGetOrCreateLoggerSomehow());
*       try
*       {
*           // Initialize the LoggerUtil, and log the beginning of an operation
*           // in either one step:
*           loggerUtil.logBegin
*               (intLogLevel
*               ,ObjUtil.getShortClassName(this) + ".methodName(params...)"
*               ,getUsernameOrNullFromSomewhere()
*               ,getClientIPAddressOrNullFromSomewhere()
*               ,getUserAgentOrNullFromSomewhere()
*               );
*           // or two steps:
*           loggerUtil.init
*               (intLogLevel
*               ,getUsernameOrNullFromSomewhere()
*               ,getClientIPAddressOrNullFromSomewhere()
*               ,getUserAgentOrNullFromSomewhere()
*               );
*           loggerUtil.logBegin
*               (ObjUtil.getShortClassName(this) + ".methodName(params...)");
*
*           // Log additional messages for the operation.
*           if (userAccessIsDeniedForSomeReason())
*           {
*               loggerUtil.logDenied("dummy reason");
*               return; 
*           } 
*           doSomething();
*           loggerUtil.log(intLogLevel + 1, "Log this more detailed message.");
*           doSomethingElse();
*           loggerUtil.log(intLogLevel + 2, "Log this even more detailed message.");
*           if (somethingFailedAndWillBeRetried())
*           {
*               loggerUtil.logRetry("This operation failed and will be retried...");
*           } 
*       }
*       catch (Throwable exception)
*       {
*           // Call one of the following to log an error:
*           loggerUtil.logError(strMessage, exception);
*           loggerUtil.logError(strMessage);
*           loggerUtil.logError(exception);
*           
*           // Or set the Aborted flag to tell logEnd() to log an abnormal
*           // end, and re-throw the exception to be handled by the caller.
*           loggerUtil.setAborted(true);
*           throw exception;
*       }
*       finally
*       {
*           // Log the end of the operation.
*           loggerUtil.logEnd();
*       }
*
*   - You can also initialize one LoggerUtil from another LoggerUtil, in which
*     case it logs at a level one greater than the specified LoggerUtil.  
*     This is useful for logging at nested levels of detail without having to 
*     keep track of the levels.  For example, your caller can pass you its
*     LoggerUtil, and you can log to the same Logger at one nesting level 
*     deeper:
*       LoggerUtil loggerUtil = new LoggerUtil(callersLoggerUtil); 
*       try
*       {
*           loggerUtil.logBegin
*               (ObjUtil.getShortClassName(this) + ".methodName(params...)");
*       ... etc.  (Same as previous example)
*     Note: Be sure to never use the same LoggerUtil instance for multiple 
*           concurrent or nested operations (including subroutine calls, 
*           <jsp:forward>, and concurrent multi-threaded operations.  Calling 
*           logBegin() again before calling logEnd() overwrites the current 
*           operation name and start time, causing the 2nd operation to be 
*           logged instead of the 1st operation.  Use a separate LoggerUtil
*           instance for each concurrent or nested operation.            
*
*   - You can also use a LoggerUtil simply to access an existing Logger to get
*     the BEGIN END functionality, without disturbing the username of the 
*     existing Logger.  This is especially useful when the Logger's username 
*     is already initialized with info not available to you.  Initialize it 
*     from the Logger, and set the PreserveLoggerUsername flag before calling
*     any methods that specify a username, user location, or user agent.   
*     
*       LoggerUtil loggerUtil = new LoggerUtil
*           (safelyWithoutThrowingAnyExceptionGetOrCreateLoggerSomehow());
*       try
*       {
*           loggerUtil.setPreserveLoggerUsername(true);
*           loggerUtil.setLogLevelOfOperation(intLogLevel);
*           loggerUtil.logBegin
*               (ObjUtil.getShortClassName(this) + ".methodName(params...)");
*       ... etc.  (Same as previous example)
*
*
*<b>Assumptions:</b>
*<b>Effects:</b>
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
*@see Logger
******************************************************************************/
public class LoggerUtil
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
    private Logger  m_logger                    = null;
    private int     m_intLogLevelOfOperation    = 0;
    private String  m_strOperationName          = "";
    private long    m_lngStartTime              = 0;
    private String  m_strUsername               = "";
    private String  m_strUserLocation           = "";
    private String  m_strUserAgent              = "";
    private boolean m_blnPreserveLoggerUsername = false;
    private boolean m_blnShowElapsedTime        = true;
    private boolean m_blnAborted                = false;

    /**************************************************************************
     * This number identifies the version of the class definition, used for 
     * serialized instances.  Be sure to increment it when adding/modifying
     * instance variable definitions or making any other change to the class
     * definition.  Omitting this declaration causes a compiler warning for 
     * any class that implements java.io.Serializable.
     *************************************************************************/
    private static final long serialVersionUID = 1L;

    /**************************************************************************
    * Constructor.
    *@param  logger     The Logger to be used for logging. 
    **************************************************************************/
    public LoggerUtil(Logger logger)
    {
        m_logger = logger;
    }

    /**************************************************************************
    * Constructor.
    *@param  loggerUtil     A LoggerUtil to copy from, but using a log level 
    *                       that is greater by 1.
    *                       Note: The information about the current operation
    *                             (name, start time, aborted) is not copied.
    **************************************************************************/
    public LoggerUtil(LoggerUtil loggerUtil)
    {
        this(loggerUtil.getLogger());
        init
            (loggerUtil.m_intLogLevelOfOperation + 1
            ,loggerUtil.m_strUsername
            ,loggerUtil.m_strUserLocation
            ,loggerUtil.m_strUserAgent
            );
        m_blnPreserveLoggerUsername = loggerUtil.m_blnPreserveLoggerUsername;
        m_blnShowElapsedTime        = loggerUtil.m_blnShowElapsedTime;
    }

    /**************************************************************************
    * Initialize the LoggerUtil with info to be used in future calls. 
    *@param intLogLevelOfOperation  
    *                           Log level for future operations.  It is compared 
    *                           with the current log level of the logger to  
    *                           decide whether to actually log those operations. 
    *@param strUsername         Name of user, or null to use "anonymous".
    *@param strUserLocation     Location (typically IP Address or hostname),
    *                           or null to display no location and no "@" sign. 
    *@param strUserAgent        User agent (typically a value like "FF2", "IE6",
    *                           etc, often generated by 
    *                           HttpUtil.getHttpUserAgentAbbrev()),
    *                           or null to display no user agent and no "/". 
    **************************************************************************/
    public void init
                    (int    intLogLevelOfOperation
                    ,String strUsername
                    ,String strUserLocation
                    ,String strUserAgent)
    {
        m_intLogLevelOfOperation    = intLogLevelOfOperation;
        m_strUsername               = strUsername;
        m_strUserLocation           = strUserLocation;
        m_strUserAgent              = strUserAgent;
    }

    /**************************************************************************
    * Set the Logger to impersonate the previously specified user, location,
    * and user agent, rather than logging all entries as the current username
    * logged into the computer, which may be a Web Server.  Log the user as 
    * one of:
    *            username@location/useragent
    *            anonymous@location/useragent
    *            username/useragent
    *            anonymous/useragent
    *            username@location
    *            anonymous@location
    *            username
    *            anonymous
    * depending on whether the specified username, location and/or user agent
    * are null.
    * See the "Usage" section of this class for examples.
    **************************************************************************/
    public void setLoggersUserInfoString()
    {
        if (m_blnPreserveLoggerUsername)
        {
            return;
        }
        if (m_logger != null)
        {
            m_logger.setUsername
                    (((m_strUsername == null || m_strUsername.equals(""))
                      ? "anonymous"
                      : m_strUsername
                     )
                     +
                     ((m_strUserLocation == null || m_strUserLocation.equals(""))
                      ? ""
                      : "@" + m_strUserLocation
                     )
                     +
                     ((m_strUserAgent == null || m_strUserAgent.equals(""))
                      ? ""
                      : "/" + m_strUserAgent
                     )
                    );
        }
    }

    /**************************************************************************
    * Log the beginning of an operation, using the info previously specified,
    * and remembering the current operation name to use on subsequent calls 
    * to logEnd(), LogDenied(), etc. 
    * 
    *@param strOperationName    Name of operation being logged
    **************************************************************************/
    public void logBegin(String strOperationName)
    {
        m_strOperationName = strOperationName;
        m_lngStartTime     = System.currentTimeMillis();
        
        log("BEGIN " + m_strOperationName);
    }

    /**************************************************************************
    * Log the beginning of an operation.
    * 
    * Note:  This is a convenience method that is identical to calling init() 
    *        followed by logBegin(String).
    * 
    *@param intLogLevelOfOperation  
    *                           Log level for this operation.  It is compared 
    *                           with the current log level of the logger to  
    *                           decide whether to actually log this operation. 
    *@param strOperationName    Name of operation being logged
    *@param strUsername         Name of user, or null to use "anonymous".
    *@param strUserLocation     Location (typically IP Address or hostname),
    *                           or null to display no location and no "@" sign. 
    *@param strUserAgent        User agent (typically a value like "FF2", "IE6",
    *                           etc, often generated by 
    *                           HttpUtil.getHttpUserAgentAbbrev()),
    *                           or null to display no user agent and no "/". 
    **************************************************************************/
    public void logBegin
                    (int    intLogLevelOfOperation
                    ,String strOperationName
                    ,String strUsername
                    ,String strUserLocation
                    ,String strUserAgent)
    {
        init(intLogLevelOfOperation, strUsername, strUserLocation, strUserAgent);
        logBegin(strOperationName);
    }

    /**************************************************************************
    * Log the beginning of an operation, using the same user info as the 
    * specified parentLoggerUtil, and a log level one greater than it.
    * The idea is that this LoggerUtil would log lines that are nested inside
    * the BEGIN END pair of those logged by parentLoggerUtil.
    *@param parentLoggerUtil    The parent LoggerUtil 
    *@param strOperationName    Name of operation being logged
    **************************************************************************/
    public void logBegin
                    (LoggerUtil parentLoggerUtil
                    ,String     strOperationName)
    {
        setLogger(parentLoggerUtil.getLogger());
        logBegin
            (parentLoggerUtil.m_intLogLevelOfOperation + 1
            ,strOperationName
            ,parentLoggerUtil.m_strUsername
            ,parentLoggerUtil.m_strUserLocation
            ,parentLoggerUtil.m_strUserAgent
            );
    }

    /**************************************************************************
    * Log the fact that a request was denied, and optionally the reason.
    *@param strReason   String specifying reason for denial, or null.
    **************************************************************************/
    public void logDenied(String strReason)
    {
        log(1,
            "DENIED " 
            + m_strOperationName 
            + " denied access to user " 
            + "\"" + m_strUsername + "\""
            + ((strReason == null)
               ? ""
               : " for reason: " + strReason
              )
           );
    }

    /**************************************************************************
    * Log the fact that something failed and will be retried.
    *@param strMsg      Message to log 
    **************************************************************************/
    public void logRetry(String strMsg)
    {
        log("RETRY " + strMsg); 
    }

    /**************************************************************************
    * Log a message.
    *@param intLogLevelOfMessage
    *                   Log level for this message.  It is compared 
    *                   with the current log level of the logger to  
    *                   decide whether to actually log this message. 
    *@param strMsg      Message to log 
    **************************************************************************/
    public void log(int intLogLevelOfMessage, String strMsg)
    {
        // Impersonate the specified user.
        // Note:  Explicitly set the user info for each call to the Logger, 
        //        in case the Logger object is being shared by multiple users.
        //        ?? Should change this to pass them as params on each call to 
        //        ?? the logger, instead of storing them in the Logger object,
        //        ?? and then racing to make the call before another user of 
        //        ?? the same Logger object changes them.
        setLoggersUserInfoString();
        Logger.logSafely(m_logger, intLogLevelOfMessage, strMsg);
    }

    /**************************************************************************
    * Log a message at the log level specified in the call to logBegin().
    *@param strMsg      Message to log 
    **************************************************************************/
    public void log(String strMsg)
    {
        log(m_intLogLevelOfOperation, strMsg);
    }

    /**************************************************************************
    * Log a message at one more than the log level of the current operation.
    *@param strMsg      Message to log 
    **************************************************************************/
    public void logNested(String strMsg)
    {
        log(m_intLogLevelOfOperation + 1, strMsg);
    }

    /**************************************************************************
    * Log an error.
    *@param strMsg      Message to log, or null. 
    *@param exception   Throwable to include in the message text, or null.
    **************************************************************************/
    public void logError(String strMsg, Throwable exception)
    {
        // Impersonate the specified user.
        // Note:  Explicitly set the user info for each call to the Logger, 
        //        in case the Logger object is being shared by multiple users.
        //        ?? Should change this to pass them as params on each call to 
        //        ?? the logger, instead of storing them in the Logger object,
        //        ?? and then racing to make the call before another user of 
        //        ?? the same Logger object changes them.
        setLoggersUserInfoString();
        Logger.logErrorSafely
                        (m_logger, 
                         1, 
                         "ERROR " + strMsg, 
                         exception
                        );
    }
    
    /**************************************************************************
    * Log an error.
    *@param strMsg      Message to log 
    **************************************************************************/
    public void logError(String strMsg)
    {
        logError(strMsg, null);
    }
    
    /**************************************************************************
    * Log an error.
    *@param exception   Throwable to include in the message text, or null.
    **************************************************************************/
    public void logError(Throwable exception)
    {
        logError(null, exception);
    }
    
    /**************************************************************************
    * Log the end of an operation.
    **************************************************************************/
    public void logEnd()
    {
        log("END   " 
            + m_strOperationName
            + (m_blnAborted ? " (ABORTED)" : "")
            + (m_blnShowElapsedTime 
               ? " : " + (System.currentTimeMillis() - m_lngStartTime)
               : ""
              )
           );
    }

    /**************************************************************************
    * Get the Logger.
    *@return The Logger.
    **************************************************************************/
    public Logger getLogger()
    {
        return m_logger;
    }

    /**************************************************************************
    * Set the Logger.
    *@param logger     The value to set.
    **************************************************************************/
    public void setLogger(Logger logger)
    {
        m_logger = logger;
    }

    /**************************************************************************
    * Get the log level of the current operation. 
    *@return The log level.
    **************************************************************************/
    public int getLogLevelOfOperation()
    {
        return m_intLogLevelOfOperation;
    }

    /**************************************************************************
    * Set the log level of the current operation. 
    *@param intLogLevelOfOperation     The value to set.
    **************************************************************************/
    public void setLogLevelOfOperation(int intLogLevelOfOperation)
    {
        m_intLogLevelOfOperation = intLogLevelOfOperation;
    }

    /**************************************************************************
    * Get the name of the current operation. 
    *@return The name.
    **************************************************************************/
    public String getOperationName()
    {
        return m_strOperationName;
    }

    /**************************************************************************
    * Set the name of the current operation. 
    *@param strOperationName    The value to set.
    **************************************************************************/
    public void setOperationName(String strOperationName)
    {
        m_strOperationName = strOperationName;
    }

    /**************************************************************************
    * Get the username. 
    *@return The username.
    **************************************************************************/
    public String getUsername()
    {
        return m_strUsername;
    }

    /**************************************************************************
    * Set the username. 
    *@param strUsername     The value to set, or null to use "anonymous".
    **************************************************************************/
    public void setUsername(String strUsername)
    {
        m_strUsername = strUsername;
        setLoggersUserInfoString();
    }

    /**************************************************************************
    * Get the user location. 
    *@return The user location.
    **************************************************************************/
    public String getUserLocation()
    {
        return m_strUserLocation;
    }

    /**************************************************************************
    * Set the user location. 
    *@param strUserLocation     The value to set, (typically an IP Address or 
    *                           hostname), or null to display no location and 
    *                           no "@" sign. 
    **************************************************************************/
    public void setUserLocation(String strUserLocation)
    {
        m_strUserLocation = strUserLocation;
        setLoggersUserInfoString();
    }

    /**************************************************************************
    * Get the user agent. 
    *@return The user agent.
    **************************************************************************/
    public String getUserAgent()
    {
        return m_strUserAgent;
    }

    /**************************************************************************
    * Set the user agent. 
    *@param strUserAgent     The value to set (typically a value like "FF2", 
    *                        "IE6", etc, often generated by 
    *                        HttpUtil.getHttpUserAgentAbbrev()),
    *                        or null to display no user agent and no "/". 
    **************************************************************************/
    public void setUserAgent(String strUserAgent)
    {
        m_strUserAgent = strUserAgent;
        setLoggersUserInfoString();
    }

    /**************************************************************************
    * Get the aborted flag. 
    *@return The flag.
    **************************************************************************/
    public boolean getAborted()
    {
        return m_blnAborted;
    }

    /**************************************************************************
    * Set the aborted flag.  This causes logEnd() to add " (ABORTED)" to the 
    * END line before the colon and duration.  For example:
    * <pre>
    *   . END   Getting product data from the database. (ABORTED) : 13
    * </pre>  
    *@param blnAborted  The value to set. 
    **************************************************************************/
    public void setAborted(boolean blnAborted)
    {
        m_blnAborted = blnAborted;
    }

    /**************************************************************************
    * Get the PreserveLoggerUsername flag. 
    *@return The flag.
    **************************************************************************/
    public boolean getPreserveLoggerUsername()
    {
        return m_blnPreserveLoggerUsername;
    }

    /**************************************************************************
    * Set the PreserveLoggerUsername flag.  This causes the LoggerUtil to not
    * set the Logger's username to the structured "username@location/agent" 
    * value.  This is useful when the Logger's username is already initialized 
    * with info not available to you.   
    *@param blnPreserveLoggerUsername  The value to set. 
    **************************************************************************/
    public void setPreserveLoggerUsername(boolean blnPreserveLoggerUsername)
    {
        m_blnPreserveLoggerUsername = blnPreserveLoggerUsername;
    }

    /**************************************************************************
    * Set the flag about whether to show elapsed time on logEnd() calls.
    * It is useful to turn these fields off if you are planning to compare 
    * the results with a previously captured set of results, and don't really 
    * care about the elapsed time.
    *@param  blnVal The new value.
    **************************************************************************/
    public void setShowElapsedTime(boolean blnVal)
    {
        m_blnShowElapsedTime = blnVal;
    }

    /**************************************************************************
    * Get the flag about whether to show elapsed time on logEnd() calls.
    *@return The value of the flag.
    **************************************************************************/
    public boolean getShowElapsedTime()
    {
        return m_blnShowElapsedTime;
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
