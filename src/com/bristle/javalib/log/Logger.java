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

import com.bristle.javalib.util.ExcUtil;
import com.bristle.javalib.util.StrUtil;
import com.bristle.javalib.io.FileUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.PrintWriter;     //-- For Tester only.
import java.io.Writer;          //-- For WriterLoggerTarget only
import java.io.IOException;     //-- For WriterLoggerTarget and
                                //--     FilenameLoggerTarget only

// Logger
/******************************************************************************
* This class handles logging of messages to one or more Logger.LoggerTargets
* (XML DOM, file, Writer, ServletContext, etc.)
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*
*     - To log to an XML DOM:
*           Logger logger = new Logger();
*           logger.addTarget(new XMLDOMLoggerTarget(xmlDOM));
*           logger.setLogLevel(2);
*           logger.log(1, "Line of text to write to the log.");
*
*     - To log to a file:
*           Logger logger = new Logger();
*           logger.addTarget(new Logger.FilenameLoggerTarget("/my/log/file"));
*           logger.setLogLevel(2);
*           logger.log(1, "Line of text to write to the log.");
*
*     - To log to a Writer:
*           Logger logger = new Logger();
*           logger.addTarget(new Logger.WriterLoggerTarget(writer));
*           logger.setLogLevel(2);
*           logger.log(1, "Line of text to write to the log.");
*
*     - To log to a ServletContext:
*           Logger logger = new Logger();
*           logger.addTarget(new ServletContextLoggerTarget(objServletContext));
*           logger.setLogLevel(2);
*           logger.log(1, "Line of text to write to the log.");
*
*     - To log to multiple targets:
*           Logger logger = new Logger();
*           logger.addTarget(new XMLDOMLoggerTarget(xmlDOM));
*           logger.addTarget(new XMLDOMLoggerTarget(xmlDOM2));
*           ...
*           logger.addTarget(new Logger.FilenameLoggerTarget("/my/log/file"));
*           logger.addTarget(new Logger.FilenameLoggerTarget("/my/log/file2"));
*           ...
*           logger.addTarget(new Logger.WriterLoggerTarget(writer));
*           logger.addTarget(new Logger.WriterLoggerTarget(writer2));
*           ...
*           logger.addTarget(new ServletContextLoggerTarget(objServletContext));
*           logger.addTarget(new ServletContextLoggerTarget(objServletContext2));
*           ...
*           logger.setLogLevel(2);
*           logger.log(1, "Line of text to write to the logs.");
*
*     - To log to System.out:
*           Logger logger = new Logger();
*           logger.addTarget(new Logger.WriterLoggerTarget
*                                       (new PrintWriter(System.out)));
*           logger.setLogLevel(2);
*           logger.log(1, "Line of text to write to the log.");
*
*   - There is also support for logging in cases where no error can be 
*     tolerated.  For example:
*
*     - To log a message without risk of causing an error, even if the 
*       local variable "logger" is null:
*           Logger.logSafely(logger, 1, "Line of text to write to the log.");
*
*     - To log an error message without risk of causing another error, even
*       if the local variable "logger" is null:
*           Logger.logErrorSafely(logger, 1, "An error occurred:", exception);
*
*   - There is also support for using the Logger in cases where the code
*     that sets up the Logger is not able to pass the Logger object to 
*     other code that must use it.  The startup code sets the Logger's
*     internal "singleton", which can be used by other parts of the code
*     via the static methods of the Logger.  For example:
*
*     - To set up the Logger's singleton, after configuring it in any of 
*       the ways shown above:
*           Logger.setSingleton(logger);
*
*     - To use the already configured singleton Logger to log a message: 
*           Logger.logSafely(1, "Line of text to write to the log.");
*
*     - To use the already configured singleton Logger to log an error message: 
*           Logger.logErrorSafely(1, "An error occurred:", exception);
*
*     - To modify the configuration of the singleton Logger:
*           Logger.getSingleton().setLogLevel(9);
*
*   - See the source code of the inner Tester class for more examples.
*  
*<b>Assumptions:</b>
*<b>Effects:</b>
*</pre>
*<xmp>
*     Writes log entries to one or more LoggerTargets, typically in the format
*     (all one one line):
*         2007/02/16 18:14:25.274 Fri MyApp v1.0 fred [Thread-12] 1171667665274 
*         23510888 27770872 1 This is a sample log message.
*     Where the parts are:
*       <DateTime>   is the current date and time on the server in format: 
*                                   yyyy/MM/dd HH:mm:ss.SSS EEE
*                    For example:   2007/12/31 23:59:59.999 Mon
*       <AppName>    is the name of the application that called the logger.
*       <AppVersion> is the version string of the application.
*       <Username>   is the name of the current user.
*       <ThreadName> is the name of the current thread.
*       <Millisecs>  is the current time in milliseconds on the server.
*       <UsedMem>    is the current number of bytes of used memory in
*                    the JVM of the Web Server.
*       <TotalMem>   is the current number of bytes of total memory in 
*                    the JVM of the Web Server.
*       <Level>      indicates the logging level of the message.  Can be
*                    used as an indentation level to format the messages.
*       <Msg>        is the text of the message.
*</xmp>
*<pre>
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
*@see     XMLDOMLoggerTarget
*@see     ServletContextLoggerTarget
******************************************************************************/
public class Logger
{

    //--
    //-- Class variables
    //--
    private static Logger st_loggerSingleton = null;

    //--
    //-- Instance variables to support public properties
    //--
    private int       m_intLogLevel   = 1;
    private String    m_strAppName    = "";
    private String    m_strAppVersion = "";
    private String    m_strUsername   = System.getProperty("user.name");
    private ArrayList m_alTargets     = new ArrayList();

    //--
    //-- Internal instance variables
    //--

    /**************************************************************************
    * This class represents a log entry that can be sent by a Logger to a
    * LoggerTarget.
    **************************************************************************/
    public static class Entry
    {
        private String m_strDateTime   = null;
        private String m_strAppName    = null;
        private String m_strAppVersion = null;
        private String m_strUsername   = null;
        private String m_strThreadName = null;
        private String m_strMillisecs  = null;
        private String m_strUsedMem    = null;
        private String m_strTotalMem   = null;
        private String m_strLevel      = null;
        private String m_strMsg        = null;
        //-- Note:  Offer a constructor that requires all values, and no "set"
        //--        methods, rather than a constructor with no parameters and
        //--        a bunch of set methods.  This makes it possible for the
        //--        compiler to detect when the caller accidentally forgets to
        //--        provide one of the values.
        public Entry(
                        String strDateTime,
                        String strAppName,
                        String strAppVersion,
                        String strUsername,
                        String strThreadName,
                        String strMillisecs,
                        String strUsedMem,
                        String strTotalMem,
                        String strLevel,
                        String strMsg)
        {
            m_strDateTime   = strDateTime;
            m_strAppName    = strAppName;
            m_strAppVersion = strAppVersion;
            m_strUsername   = strUsername;
            m_strThreadName = strThreadName;
            m_strMillisecs  = strMillisecs;
            m_strUsedMem    = strUsedMem;
            m_strTotalMem   = strTotalMem;
            m_strLevel      = strLevel;
            m_strMsg        = strMsg;
        }
        public String getDateTime()   { return m_strDateTime;   }
        public String getAppName()    { return m_strAppName;    }
        public String getAppVersion() { return m_strAppVersion; }
        public String getUsername()   { return m_strUsername;   }
        public String getThreadName() { return m_strThreadName; }
        public String getMillisecs()  { return m_strMillisecs;  }
        public String getUsedMem()    { return m_strUsedMem;    }
        public String getTotalMem()   { return m_strTotalMem;   }
        public String getLevel()      { return m_strLevel;      }
        public String getMsg()        { return m_strMsg;        }
        public String getFormattedLogLine(boolean blnDateTime
                                         ,boolean blnAppName
                                         ,boolean blnAppVersion
                                         ,boolean blnUsername
                                         ,boolean blnThreadName
                                         ,boolean blnMillisecs
                                         ,boolean blnUsedMem
                                         ,boolean blnTotalMem
                                         ,boolean blnLevel
                                         ,boolean blnDots
                                         ,boolean blnMsg
                                         )
        {
            // Build an indentation string with a single space plus ". " for 
            // each level beyond 1.  This will be used to indent lines as:
            //          1 BEGIN operation1
            //          2 . BEGIN operation1a
            //          3 . . BEGIN operation1a1
            //          3 . . END operation1a1   
            //          2 . BEGIN operation1a
            //          1 BEGIN operation1
            StringBuffer sbIndent = new StringBuffer(" ");
            final int intLevel = Integer.parseInt(m_strLevel);
            if (blnDots)
            {
                for (int i = intLevel; i > 1; i--)
                {
                    sbIndent.append(". ");
                }
            }

            // Prefix each line of the message with a standard set of fields.
            final String strONE_OR_TWO_BLANKS
                         =  (intLevel < 10 ? "  " : " ");
            final String strPrefix 
                      = (!blnDateTime   ? "" : m_strDateTime)
                      + (!blnAppName    ? "" : " "  + m_strAppName)
                      + (!blnAppVersion ? "" : " "  + m_strAppVersion)
                      + (!blnUsername   ? "" : " "  + m_strUsername)
                      + (!blnThreadName ? "" : " [" + m_strThreadName + "]")
                      + (!blnMillisecs  ? "" : " "  + m_strMillisecs)
                      + (!blnUsedMem    ? "" : " "  + m_strUsedMem)
                      + (!blnTotalMem   ? "" : " "  + m_strTotalMem)
                      + (!blnLevel      ? "" : strONE_OR_TWO_BLANKS + m_strLevel)
                      + (!blnDots       ? "" : sbIndent.toString())
                      ;
            String strAllLinesExceptFirstPrefixed = "";
            if (blnMsg)
            {
                strAllLinesExceptFirstPrefixed
                   = StrUtil.replaceAll(m_strMsg, "\n", "\n" + strPrefix);
            }
            return strPrefix + strAllLinesExceptFirstPrefixed;
        }
        public String getFormattedLogLine()
        {
            return getFormattedLogLine
                    (true,true,true,true,true,true,true,true,true,true,true);
        }
    }

    /**************************************************************************
    * Add to the list of LoggerTargets to which logging is done.
    *@param  target     An additional LoggerTarget to log to.
    **************************************************************************/
    public void addTarget(LoggerTarget target)
    {
        m_alTargets.add(target);
    }

    /**************************************************************************
    * Set the log level.  Each call to log() with a logLevel greater than this
    * value will be ignored.  Only calls with logLevel less than or equal to
    * this value are logged.  Default = 1.
    *@param  intNew     The new log level.
    **************************************************************************/
    public void setLogLevel(int intNew)
    {
        m_intLogLevel = intNew;
    }

    /**************************************************************************
    * Get the log level.
    *@return            The log level.
    **************************************************************************/
    public int getLogLevel()
    {
        return m_intLogLevel;
    }

    /**************************************************************************
    * Set the application name to be recorded in log entries.
    * Default = "".
    *@param  strNew     The new application name.
    **************************************************************************/
    public void setAppName(String strNew)
    {
        m_strAppName = strNew;
    }

    /**************************************************************************
    * Get the application name.
    *@return            The application name.
    **************************************************************************/
    public String getAppName()
    {
        return m_strAppName;
    }

    /**************************************************************************
    * Set the application version to be recorded in log entries.
    * Default = "".
    *@param  strNew     The new application version.
    **************************************************************************/
    public void setAppVersion(String strNew)
    {
        m_strAppVersion = strNew;
    }

    /**************************************************************************
    * Get the application version.
    *@return            The application version.
    **************************************************************************/
    public String getAppVersion()
    {
        return m_strAppVersion;
    }

    /**************************************************************************
    * Set the username to be recorded in log entries.
    * Default = current logged in username.
    *@param  strNew     The new username.
    **************************************************************************/
    public void setUsername(String strNew)
    {
        m_strUsername = strNew;
    }

    /**************************************************************************
    * Get the username.
    *@return            The username.
    **************************************************************************/
    public String getUsername()
    {
        return m_strUsername;
    }

    /**********************************************************************
    * Log a message to the various LoggerTargets.  Compares the specified
    * log level with the current log level.  Logs the message if the
    * specified level is less than or equal to the current level.
    *@param  intLogLevel Level at which to log the message.
    *@param  strMsg      String to write to the log entry.
    **********************************************************************/
    public void log(int intLogLevel, String strMsg)
    {
        if (m_intLogLevel < intLogLevel)
        {
            return;
        }

        try
        {
            Date date           = new Date();
            String strDateTime   
                    = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS EEE")
                          .format(date);
            String strThreadName = Thread.currentThread().getName();
            String strMillisecs  = Long.toString(date.getTime());
//??System.gc();
            long   lngTotalMem   = Runtime.getRuntime().totalMemory();
            String strUsedMem    = Long.toString
                                        (lngTotalMem
                                         - Runtime.getRuntime().freeMemory()
                                        );
            String strTotalMem   = Long.toString(lngTotalMem);
            String strLevel      = Integer.toString(intLogLevel);
            Entry  entry = new Entry
                                (strDateTime,
                                 m_strAppName,
                                 m_strAppVersion,
                                 m_strUsername,
                                 strThreadName,
                                 strMillisecs,
                                 strUsedMem,
                                 strTotalMem,
                                 strLevel,
                                 strMsg);

            for (Iterator i = m_alTargets.iterator(); i.hasNext(); )
            {
                LoggerTarget target = (LoggerTarget)i.next();
                try
                {
                    target.log(entry);
                }
                catch (Throwable e)
                {
                    //-- Ignore errors that occur while writing to any one
                    //-- LoggerTarget.
                }
            }

        }
        catch (Throwable e)
        {
            //-- Ignore logging errors.  No place to report them to.
        }
    }

    /**************************************************************************
    * Log a message, suppressing all possible errors, even the error of
    * passing a null value for logger.
    * It is safe to call this method from an exception handler or finally
    * clause without fear of throwing another exception.
    *@param  logger      The Logger to pass the message to.
    *@param  intLogLevel Level at which to log the message.
    *@param  strMsg      String to write to the log entry.
    **************************************************************************/
    public static void logSafely
                        (Logger logger,
                         int    intLogLevel,
                         String strMsg)
    {
        try
        {
            if (logger != null)
            {
                logger.log(intLogLevel, strMsg);
            }
        }
        catch(Throwable eSuppressed)
        {
            //-- Suppress all errors.
        }
    }

    /**************************************************************************
    * Log an error message, suppressing all possible errors, even the error of
    * passing a null value for logger.
    * It is safe to call this method from an exception handler or finally
    * clause without fear of throwing another exception.
    *@param  logger      The Logger to pass the message to.
    *@param  intLogLevel Level at which to log the message.
    *@param  strMsg      String to write to the log entry.
    *@param  e           Throwable to include in the message text, or null.
    **************************************************************************/
    public static void logErrorSafely
                        (Logger    logger,
                         int       intLogLevel,
                         String    strMsg,
                         Throwable e)
    {
        try
        {
            logSafely
                (logger,
                 intLogLevel,
                 strMsg 
                 + "\n" 
                 + ((e == null) 
                    ? "(No stack trace available to logger.)" 
                    : ExcUtil.getStackTrace(e)));
        }
        catch(Throwable eSuppressed)
        {
            //-- Suppress all errors.
        }
    }

    /**************************************************************************
    * Set the singleton Logger.
    *@param  logger     The Logger instance to be stored as the singleton.
    **************************************************************************/
    public static void setSingleton(Logger logger)
    {
        st_loggerSingleton = logger;
    }

    /**************************************************************************
    * Get the singleton Logger, if any.
    *@return            The singleton Logger, or null.
    **************************************************************************/
    public static Logger getSingleton()
    {
        return st_loggerSingleton;
    }

    /**************************************************************************
    * Log a message via the singleton Logger, suppressing all possible errors, 
    * even the error of having a null value for the singleton Logger.
    * It is safe to call this method from an exception handler or finally
    * clause without fear of throwing another exception.
    *@param  intLogLevel Level at which to log the message.
    *@param  strMsg      String to write to the log entry.
    **************************************************************************/
    public static void logSafely
                        (int    intLogLevel,
                         String strMsg)
    {
        Logger.logSafely(st_loggerSingleton, intLogLevel, strMsg);
    }

    /**************************************************************************
    * Log an error message via the singleton Logger, suppressing all possible 
    * errors, even the error of having a null value for the singleton Logger.
    * It is safe to call this method from an exception handler or finally
    * clause without fear of throwing another exception.
    *@param  intLogLevel Level at which to log the message.
    *@param  strMsg      String to write to the log entry.
    *@param  e           Throwable to include in the message text, or null.
    **************************************************************************/
    public static void logErrorSafely
                        (int       intLogLevel,
                         String    strMsg,
                         Throwable e)
    {
        Logger.logErrorSafely(st_loggerSingleton, intLogLevel, strMsg, e);
    }

    /**************************************************************************
    * This interface must be implemented by any class that expects to be
    * called by the Logger class to log entries to a target (XML DOM, file,
    * Writer, ServletContext, etc.)
    **************************************************************************/
    public interface LoggerTarget
    {
        /**********************************************************************
        * Log the log entry.
        *@param  entry      The log entry to record somewhere.
        *@throws Exception  The LoggerTarget.log method is permitted to throw
        *                   any exception, and it will be caught.
        **********************************************************************/
        public void log(Logger.Entry entry)
                        throws Exception;
    }

    /**************************************************************************
    * This class implements the LoggerTarget interface, writing log entries
    * to a named text file.  It is a simple example of a LoggerTarget that
    * is used by the Tester class below, and can also be used by clients
    * outside of the Logger class.
    **************************************************************************/
    public static class FilenameLoggerTarget implements LoggerTarget
    {

        //--
        //-- Class variables
        //--

        //--
        //-- Instance variables to support public properties
        //--
        private String m_strFilename = null;

        //--
        //-- Internal instance variables
        //--

        /**********************************************************************
        * Constructor.
        *@param  strFilename    The filename to write log entries to.
        **********************************************************************/
        public FilenameLoggerTarget(String strFilename)
        {
            m_strFilename = strFilename;
        }

        /**********************************************************************
        * Set the filename to write log entries to.  If null, no logging to a
        * file is performed.
        *@param  strFilename    The new filename.
        **********************************************************************/
        public void setFilename(String strFilename)
        {
            m_strFilename = strFilename;
        }

        /**********************************************************************
        * Get the filename that messages are currently being logged to.
        *@return            The filename.
        **********************************************************************/
        public String getFilename()
        {
            return m_strFilename;
        }

        /**********************************************************************
        * Log the log entry to the file.
        *@param  entry          The log entry to write to the log.
        *@throws IOException    When an error occurs writing the log file.
        **********************************************************************/
        public void log(Logger.Entry entry)
                            throws IOException
        {
            if (m_strFilename != null)
            {
                FileUtil.appendToFile
                                (m_strFilename,
                                 entry.getFormattedLogLine() + "\n");
            }
        }
    }

    /**************************************************************************
    * This class implements the LoggerTarget interface, writing log entries
    * to a Writer.  It is a simple example of a LoggerTarget that is used by
    * the Tester class below, and can also be used by clients outside of the
    * Logger class.
    **************************************************************************/
    public static class WriterLoggerTarget implements LoggerTarget
    {

        //--
        //-- Class variables
        //--

        //--
        //-- Instance variables to support public properties
        //--
        private Writer m_writer = null;
        private boolean m_blnShowDynamicFields = true;
        
        //--
        //-- Internal instance variables
        //--

        /**********************************************************************
        * Constructor.
        *@param  writer     The Writer to write log entries to.
        **********************************************************************/
        public WriterLoggerTarget(Writer writer)
        {
            m_writer = writer;
        }

        /**********************************************************************
        * Set the Writer to write log entries to.  If null, no logging to a
        * Writer is performed.
        *@param  writer         The new Writer.
        **********************************************************************/
        public void setWriter(Writer writer)
        {
            m_writer = writer;
        }

        /**********************************************************************
        * Get the Writer that messages are currently being logged to.
        *@return            The Writer.
        **********************************************************************/
        public Writer getWriter()
        {
            return m_writer;
        }

        /**********************************************************************
        * Set the flag about whether to show dynamic fields, like memory sizes
        * and time stamps.  It is useful to turn these fields off if you are
        * planning to compare the results with a previously captured set of 
        * results, and don't really care about the values of the dynamic fields.
        *@param  blnVal The new value.
        **********************************************************************/
        public void setShowDynamicFields(boolean blnVal)
        {
            m_blnShowDynamicFields = blnVal;
        }

        /**********************************************************************
        * Get the flag about whether to show dynamic fields, like memory sizes
        * and time stamps.
        *@return The value of the flag.
        **********************************************************************/
        public boolean getShowDynamicFields()
        {
            return m_blnShowDynamicFields;
        }

        /**********************************************************************
        * Log the log entry to the Writer.
        *@param  entry          The log entry to write to the log.
        *@throws IOException    When an error occurs writing to the Writer.
        **********************************************************************/
        public void log(Logger.Entry entry)
                            throws IOException
        {
            if (m_writer != null)
            {
                m_writer.write(
                           entry.getFormattedLogLine(m_blnShowDynamicFields
                                                     ,true
                                                     ,true
                                                     ,true
                                                     ,m_blnShowDynamicFields
                                                     ,m_blnShowDynamicFields
                                                     ,m_blnShowDynamicFields
                                                     ,m_blnShowDynamicFields
                                                     ,true
                                                     ,true
                                                     ,true)
                           + "\n");
                m_writer.flush();
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
                Logger logger = new Logger();
                logger.addTarget
                        (new FilenameLoggerTarget("110_junk.log"));
                logger.addTarget
                        (new WriterLoggerTarget(new PrintWriter(System.out)));
                
                System.out.println ("   Setting log level to 2...");
                logger.setLogLevel(2);
                int intLogLevel = logger.getLogLevel();
                System.out.println ("   Log level is " + intLogLevel);

                System.out.println ("   Logging at level 1...");
                logger.log(1, "Line of text logged at level 1.");

                System.out.println ("   Logging at level 2...");
                logger.log(2, "Line of text logged at level 2.");

                System.out.println ("   Logging at level 3...");
                logger.log(3, "Line of text logged at level 3.");

                System.out.println ("   Logging multi-line log entry...");
                logger.log(1, "Multi-line log entry\nLine 1\nLine 2\nLine 3");

                System.out.println ("   Setting app name and version...");
                logger.setAppName("MyApp");
                logger.setAppVersion("v1.0");
                System.out.println ("   Logging at level 1...");
                logger.log(1, "Line of text logged at level 1.");

                System.out.println ("   Logging at level 2...");
                logger.log(2, "Line of text logged at level 2.");

                System.out.println ("   Logging at level 3...");
                logger.log(3, "Line of text logged at level 3.");
                
                System.out.println ("   Logging safely...");
                Logger.logSafely(logger, 1, "Line of text.");

                System.out.println ("   Logging safely with a null logger");
                System.out.println ("   (fails silently)...");
                Logger.logSafely(null, 1, "Line of text.");

                System.out.println ("   Logging an error safely...");
                Logger.logErrorSafely
                                (logger, 1, "Exception 1.",
                                 new Exception("Exception 1"));

                System.out.println ("   Logging an error safely with a null exception...");
                Logger.logErrorSafely
                                (logger, 1, "Error with no exception.", null);

                System.out.println ("   Logging an error safely with a null logger");
                System.out.println ("   (fails silently)...");
                Logger.logErrorSafely
                                (null, 1, "Exception 2.",
                                 new Exception("Exception 2"));

                System.out.println ("   Logging an error safely with a null logger");
                System.out.println ("   and a null exception (fails silently)...");
                Logger.logErrorSafely
                                (null, 1, "No logger and no exception", null);

                System.out.println ("   Setting singleton...");
                Logger.setSingleton(logger);

                int intLogLevel2 = Logger.getSingleton().getLogLevel();
                System.out.println ("   Singleton log level is " + intLogLevel2);

                System.out.println ("   Logging at level 1...");
                Logger.logSafely(1, "Line of text logged at level 1.");

                System.out.println ("   Logging at level 2...");
                Logger.logSafely(2, "Line of text logged at level 2.");

                System.out.println ("   Logging at level 3...");
                Logger.logSafely(3, "Line of text logged at level 3.");

                System.out.println ("   Logging an error safely...");
                Logger.logErrorSafely
                                (1, "Exception 3.",
                                 new Exception("Exception 3"));

                System.out.println ("   Logging an error safely with a null exception...");
                Logger.logErrorSafely
                                (1, "Error with no exception.", null);

                System.out.println ("   Setting singleton to null...");
                Logger.setSingleton(null);

                System.out.println ("   Logging safely with a null logger");
                System.out.println ("   (fails silently)...");
                Logger.logSafely(1, "Line of text.");

                System.out.println ("   Logging an error safely with a null logger");
                System.out.println ("   (fails silently)...");
                Logger.logErrorSafely
                                (1, "Exception 4.",
                                 new Exception("Exception 4"));

                System.out.println ("   Logging an error safely with a null logger");
                System.out.println ("   and a null exception (fails silently)...");
                Logger.logErrorSafely
                                (1, "No logger and no exception", null);

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
