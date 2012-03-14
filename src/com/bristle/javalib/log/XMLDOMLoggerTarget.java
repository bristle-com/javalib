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

import com.bristle.javalib.xml.XMLUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.transform.TransformerException;


// XMLDOMLoggerTarget
/******************************************************************************
* This class implements the Logger.LoggerTarget interface, writing log entries
* to an XML DOM.
*<pre>
*<b>Usage:</b>
*   - See the {@link Logger} class.
*
*   - See the source code of the inner Tester class for more examples.
*  
*<b>Assumptions:</b>
*<b>Effects:</b>
*</pre>
*<xmp>
*   - Adds nodes to the XML DOM with the following structure:
*       <Log>
*        <Entry>
*         <DateTime>2007/02/16 18:14:25.274 Fri</DateTime>
*         <AppName>MyApp</AppName>
*         <AppVersion>v1.0</AppVersion>
*         <Username>STLUKF00</Username>
*         <ThreadName>[Thread-12]</ThreadName>
*         <Millisecs>1171667665274</Millisecs>
*         <UsedMem>23510888</UsedMem>
*         <TotalMem>27770872</TotalMem>
*         <Level>1</Level>
*         <Msg>This is a sample log message.</Msg>
*        </Entry>
*        ...
*       </Log>
*     Where:
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
******************************************************************************/
public class XMLDOMLoggerTarget implements Logger.LoggerTarget
{

    //--
    //-- Class variables
    //--

    //--
    //-- Instance variables to support public properties
    //--
    private Document m_xmlDOM              = null;
    private String   m_strXMLRootTag       = "Log";
    private String   m_strXMLEntryTag      = "Entry";
    private String   m_strXMLDateTimeTag   = "DateTime";
    private String   m_strXMLAppNameTag    = "AppName";
    private String   m_strXMLAppVersionTag = "AppVersion";
    private String   m_strXMLUsernameTag   = "Username";
    private String   m_strXMLThreadNameTag = "ThreadName";
    private String   m_strXMLMillisecsTag  = "Millisecs";
    private String   m_strXMLUsedMemTag    = "UsedMem";
    private String   m_strXMLTotalMemTag   = "TotalMem";
    private String   m_strXMLLevelTag      = "Level";
    private String   m_strXMLMsgTag        = "Msg";

    //--
    //-- Internal instance variables
    //--

    /**************************************************************************
    * Constructor.
    *@param  xmlDOM     The XML DOM to write log entries to.
    **************************************************************************/
    public XMLDOMLoggerTarget(Document xmlDOM)
    {
        m_xmlDOM = xmlDOM;
    }

    /**************************************************************************
    * Set the XML DOM to write log entries to.  If null, no logging to an 
    * XML DOM is performed.
    *@param  xmlDOM     The new XML DOM.
    **************************************************************************/
    public void setXMLDOM(Document xmlDOM)
    {
        m_xmlDOM = xmlDOM;
    }

    /**************************************************************************
    * Get the XML DOM that messages are currently being logged to.
    *@return            The XML DOM.
    **************************************************************************/
    public Document getXMLDOM()
    {
        return m_xmlDOM;
    }

    /**************************************************************************
    * Set the XML root tag.  All entries logged to the XML DOM will be nested
    * inside this XML tag.  If necessary, this tag will be created as a root
    * node in the XML DOM.
    *@param  strNew     The new XML root tag.
    **************************************************************************/
    public void setXMLRootTag(String strNew)
    {
        m_strXMLRootTag = strNew;
    }

    /**************************************************************************
    * Get the XML root tag.
    *@return            The XML root tag.
    **************************************************************************/
    public String getXMLRootTag()
    {
        return m_strXMLRootTag;
    }

    /**************************************************************************
    * Set the XML tag used for "Entry" nodes.  Each call to log() logs one 
    * entry.
    *@param  strNew     The new XML Entry tag.
    **************************************************************************/
    public void setXMLEntryTag(String strNew)
    {
        m_strXMLEntryTag = strNew;
    }

    /**************************************************************************
    * Get the XML tag used for "Entry" nodes.
    *@return            The XML Entry tag.
    **************************************************************************/
    public String getXMLEntryTag()
    {
        return m_strXMLEntryTag;
    }

    /**************************************************************************
    * Set the XML tag used for "DateTime" nodes.
    *@param  strNew     The new XML DateTime tag.
    **************************************************************************/
    public void setXMLDateTimeTag(String strNew)
    {
        m_strXMLDateTimeTag = strNew;
    }

    /**************************************************************************
    * Get the XML tag used for "DateTime" nodes.
    *@return            The XML DateTime tag.
    **************************************************************************/
    public String getXMLDateTimeTag()
    {
        return m_strXMLDateTimeTag;
    }

    /**************************************************************************
    * Set the XML tag used for "AppName" nodes.
    *@param  strNew     The new XML AppName tag.
    **************************************************************************/
    public void setXMLAppNameTag(String strNew)
    {
        m_strXMLAppNameTag = strNew;
    }

    /**************************************************************************
    * Get the XML tag used for "AppName" nodes.
    *@return            The XML AppName tag.
    **************************************************************************/
    public String getXMLAppNameTag()
    {
        return m_strXMLAppNameTag;
    }

    /**************************************************************************
    * Set the XML tag used for "AppVersion" nodes.
    *@param  strNew     The new XML AppVersion tag.
    **************************************************************************/
    public void setXMLAppVersionTag(String strNew)
    {
        m_strXMLAppVersionTag = strNew;
    }

    /**************************************************************************
    * Get the XML tag used for "AppVersion" nodes.
    *@return            The XML AppVersion tag.
    **************************************************************************/
    public String getXMLAppVersionTag()
    {
        return m_strXMLAppVersionTag;
    }

    /**************************************************************************
    * Set the XML tag used for "Username" nodes.
    *@param  strNew     The new XML Username tag.
    **************************************************************************/
    public void setXMLUsernameTag(String strNew)
    {
        m_strXMLUsernameTag = strNew;
    }

    /**************************************************************************
    * Get the XML tag used for "Username" nodes.
    *@return            The XML Username tag.
    **************************************************************************/
    public String getXMLUsernameTag()
    {
        return m_strXMLUsernameTag;
    }

    /**************************************************************************
    * Set the XML tag used for "ThreadName" nodes.
    *@param  strNew     The new XML ThreadName tag.
    **************************************************************************/
    public void setXMLThreadNameTag(String strNew)
    {
        m_strXMLThreadNameTag = strNew;
    }

    /**************************************************************************
    * Get the XML tag used for "ThreadName" nodes.
    *@return            The XML ThreadName tag.
    **************************************************************************/
    public String getXMLThreadNameTag()
    {
        return m_strXMLThreadNameTag;
    }

    /**************************************************************************
    * Set the XML tag used for "Millisecs" nodes.
    *@param  strNew     The new XML Millisecs tag.
    **************************************************************************/
    public void setXMLMillisecsTag(String strNew)
    {
        m_strXMLMillisecsTag = strNew;
    }

    /**************************************************************************
    * Get the XML tag used for "Millisecs" nodes.
    *@return            The XML Millisecs tag.
    **************************************************************************/
    public String getXMLMillisecsTag()
    {
        return m_strXMLMillisecsTag;
    }

    /**************************************************************************
    * Set the XML tag used for "UsedMem" nodes.
    *@param  strNew     The new XML UsedMem tag.
    **************************************************************************/
    public void setXMLUsedMemTag(String strNew)
    {
        m_strXMLUsedMemTag = strNew;
    }

    /**************************************************************************
    * Get the XML tag used for "UsedMem" nodes.
    *@return            The XML UsedMem tag.
    **************************************************************************/
    public String getXMLUsedMemTag()
    {
        return m_strXMLUsedMemTag;
    }

    /**************************************************************************
    * Set the XML tag used for "TotalMem" nodes.
    *@param  strNew     The new XML TotalMem tag.
    **************************************************************************/
    public void setXMLTotalMemTag(String strNew)
    {
        m_strXMLTotalMemTag = strNew;
    }

    /**************************************************************************
    * Get the XML tag used for "TotalMem" nodes.
    *@return            The XML TotalMem tag.
    **************************************************************************/
    public String getXMLTotalMemTag()
    {
        return m_strXMLTotalMemTag;
    }

    /**************************************************************************
    * Set the XML tag used for "Level" nodes.
    *@param  strNew     The new XML Level tag.
    **************************************************************************/
    public void setXMLLevelTag(String strNew)
    {
        m_strXMLLevelTag = strNew;
    }

    /**************************************************************************
    * Get the XML tag used for "Level" nodes.
    *@return            The XML Level tag.
    **************************************************************************/
    public String getXMLLevelTag()
    {
        return m_strXMLLevelTag;
    }

    /**************************************************************************
    * Set the XML tag used for "Msg" nodes.
    *@param  strNew     The new XML Msg tag.
    **************************************************************************/
    public void setXMLMsgTag(String strNew)
    {
        m_strXMLMsgTag = strNew;
    }

    /**************************************************************************
    * Get the XML tag used for "Msg" nodes.
    *@return            The XML Msg tag.
    **************************************************************************/
    public String getXMLMsgTag()
    {
        return m_strXMLMsgTag;
    }

    /**************************************************************************
    * Log the log entry to the XML DOM.
    *@param  entry          The log entry to write to the log.
    *@throws TransformerException When an error occurs adding to the XML.
    **************************************************************************/
    public void log(Logger.Entry entry) 
                       throws TransformerException
    {
        if (m_xmlDOM != null)
        {
            //-- Get the existing Debug node, if any, in the DOM.  
            //-- Otherwise, create one.  Then log to it.
            Node xmlDebug  = XMLUtil.getOrAppendElement
                        (m_xmlDOM,  m_strXMLRootTag);
            Node xmlEntry  = XMLUtil.appendElement
                        (xmlDebug,  m_strXMLEntryTag);
            XMLUtil.appendElementContainingText
                        (xmlEntry,  m_strXMLDateTimeTag,   entry.getDateTime());
            XMLUtil.appendElementContainingText
                        (xmlEntry,  m_strXMLAppNameTag,    entry.getAppName());
            XMLUtil.appendElementContainingText
                        (xmlEntry,  m_strXMLAppVersionTag, entry.getAppVersion());
            XMLUtil.appendElementContainingText
                        (xmlEntry,  m_strXMLUsernameTag,   entry.getUsername());
            XMLUtil.appendElementContainingText
                        (xmlEntry,  m_strXMLThreadNameTag, entry.getThreadName());
            XMLUtil.appendElementContainingText
                        (xmlEntry,  m_strXMLMillisecsTag,  entry.getMillisecs());
            XMLUtil.appendElementContainingText
                        (xmlEntry,  m_strXMLUsedMemTag,    entry.getUsedMem());
            XMLUtil.appendElementContainingText
                        (xmlEntry,  m_strXMLTotalMemTag,   entry.getTotalMem());
            XMLUtil.appendElementContainingText
                        (xmlEntry,  m_strXMLLevelTag,      entry.getLevel());
            XMLUtil.appendElementContainingText
                        (xmlEntry,  m_strXMLMsgTag,        entry.getMsg());
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
                Document dom = XMLUtil.createEmptyDocument();
                logger.addTarget(new XMLDOMLoggerTarget(dom));

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

                System.out.println ("   Log contains:");
                System.out.println (XMLUtil.serialize(dom));

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
