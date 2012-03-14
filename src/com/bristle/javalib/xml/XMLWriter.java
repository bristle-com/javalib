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

package com.bristle.javalib.xml;

import com.bristle.javalib.log.Logger;

import java.io.Writer;
import java.io.PrintWriter;

import org.apache.xml.serialize.LineSeparator;

// XMLWriter
/******************************************************************************
* This class writes formatted XML strings to a Writer.
* It is a class, rather than just a collection of routines, to make the 
* calling code simpler.  As a class, it can use instance variables to store 
* things like the Writer, which would otherwise have to be passed on each 
* method call. 
* <pre>
* <b>Usage:</b>
*   - The following is a typical scenario for using this class:
*       XMLWriter writer = new XMLWriter(writerOut);
*       writer.writeStartTag("abcd");
*       writer.write("some text in the abcd tag");
*       writer.writeEndTag("abcd");
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
public class XMLWriter
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
    private Writer m_writer     = null;
    private String m_strNewLine = LineSeparator.Windows;

    //--
    //-- Other constants.
    //--
    public static final boolean m_blnFORCE_LOG  = true;
    public static final boolean blnWITH_NEWLINE = true;

    /**************************************************************************
    * Constructor.
    *@param writer The Writer to write the XML to.
    **************************************************************************/
    public XMLWriter(Writer writer)
    {
        m_writer = writer;
    }

    /**************************************************************************
    * Set the newline String.  Recommended values are:
    *   org.apache.xml.serialize.LineSeparator.Macintosh;
    *   org.apache.xml.serialize.LineSeparator.Unix;
    *   org.apache.xml.serialize.LineSeparator.Web;
    *   org.apache.xml.serialize.LineSeparator.Windows;
    *@param  strNew     The new value.
    **************************************************************************/
    public void setNewLineString(String strNew)
    {
        m_strNewLine = strNew;
    }

    /**************************************************************************
    * Get the newline String.
    *@return            The newline String.
    **************************************************************************/
    public String getNewLineString()
    {
        return m_strNewLine;
    }

    /**************************************************************************
    * Write the line of text to the Writer.
    *@param  strText    String of text.
    *@throws java.io.IOException
    *                   When an error occurs writing to the Writer.
    **************************************************************************/
    public void write(String strText)
                 throws java.io.IOException
    {
        m_writer.write(strText);
        //?? Delete the flush() call?  Is it needed for anything?
        //?? Doing a flush() for every write() is slow.  A simple test
        //?? case with a tight loop writing "x" then calling flush() 
        //?? runs 100 times faster without the flush() calls, when 
        //?? called by a Web server to write to a Web browser.
        m_writer.flush();
    }

    /**************************************************************************
    * Write a newline to the Writer.
    *@throws java.io.IOException
    *                   When an error occurs writing to the Writer.
    **************************************************************************/
    public void writeln()
                 throws java.io.IOException
    {
        write(m_strNewLine);
    }

    /**************************************************************************
    * Write the line of text to the Writer, followed by a newline.
    *@param  strText    String of text.
    *@throws java.io.IOException
    *                   When an error occurs writing to the Writer.
    **************************************************************************/
    public void writeln(String strText)
                 throws java.io.IOException
    {
        write(strText);
        writeln();
    }

    /**************************************************************************
    * Write the start version of the specified XML tag to the Writer, 
    * optionally followed by a newline.
    *@param  strTag     XML tag (without angle brackets).
    *@param  blnNewLine Boolean flag indicating whether to write a newline
    *                   after the tag.
    *@throws java.io.IOException
    *                   When an error occurs writing to the Writer.
    **************************************************************************/
    public void writeStartTag(String strTag, boolean blnNewLine)
                 throws java.io.IOException
    {
        XMLUtil.writeStartTag(m_writer, strTag);
        if (blnNewLine)
        {
            writeln();
        }
    }

    /**************************************************************************
    * Write the start version of the specified XML tag to the Writer, 
    * followed by a newline.
    *@param  strTag     XML tag (without angle brackets).
    *@throws java.io.IOException
    *                   When an error occurs writing to the Writer.
    **************************************************************************/
    public void writeStartTag(String strTag)
                 throws java.io.IOException
    {
        writeStartTag(strTag, blnWITH_NEWLINE);
    }

    /**************************************************************************
    * Write the start version of the specified XML tag to the Writer, 
    * including a string containing one or more attributes,
    * optionally followed by a newline.
    *@param  strTag        XML tag (without angle brackets).
    *@param  strAttributes Attributes to be included in the start tag
    *@param  blnNewLine    Boolean flag indicating whether to write a
    *                      newline after the tag.
    *@throws java.io.IOException
    *                      When an error occurs writing to the Writer.
    **************************************************************************/
    public void writeStartTagAndAttributes(String strTag,
                                           String strAttributes,
                                           boolean blnNewLine)
                 throws java.io.IOException
    {
        XMLUtil.writeStartTagAndAttributes(m_writer, strTag, strAttributes);
        if (blnNewLine)
        {
            writeln();
        }
    }

    /**************************************************************************
    * Write the start version of the specified XML tag to the Writer,
    * including a string containing one or more attributes, 
    * followed by a newline.
    *@param  strTag        XML tag (without angle brackets).
    *@param  strAttributes Attributes to be included in the start tag
    *@throws java.io.IOException
    *                      When an error occurs writing to the Writer.
    **************************************************************************/
    public void writeStartTagAndAttributes(String strTag,
                                           String strAttributes)
                 throws java.io.IOException
    {
        writeStartTagAndAttributes(strTag, strAttributes, blnWITH_NEWLINE);
    }

    /**************************************************************************
    * Write the end version of the specified XML tag to the Writer, 
    * optionally followed by a newline.
    *@param  strTag     XML tag (without angle brackets or slash).
    *@param  blnNewLine Boolean flag indicating whether to write a newline
    *                   after the tag.
    *@throws java.io.IOException
    *                   When an error occurs writing to the Writer.
    **************************************************************************/
    public void writeEndTag(String strTag, boolean blnNewLine)
                 throws java.io.IOException
    {
        XMLUtil.writeEndTag(m_writer, strTag);
        if (blnNewLine)
        {
            writeln();
        }
    }

    /**************************************************************************
    * Write the end version of the specified XML tag to the Writer, 
    * followed by a newline.
    *@param  strTag     XML tag (without angle brackets).
    *@throws java.io.IOException
    *                   When an error occurs writing to the Writer.
    **************************************************************************/
    public void writeEndTag(String strTag)
                 throws java.io.IOException
    {
        writeEndTag(strTag, blnWITH_NEWLINE);
    }

    /**************************************************************************
    * Write the specified value, enclosed in the start and end versions of 
    * the specified XML tag, to the Writer, optionally followed by a 
    * newline.
    *@param  strTag     XML tag (without angle brackets).
    *@param  strValue   String value to enclose in XML tags.
    *@param  blnNewLine Boolean flag indicating whether to write a newline
    *                   after the formatted XML.
    *@throws java.io.IOException
    *                   When an error occurs writing to the Writer.
    **************************************************************************/
    public void writeTagAndValue
                        (String   strTag,
                         String   strValue,
                         boolean  blnNewLine)
                 throws java.io.IOException
    {
        writeStartTag(strTag, !blnWITH_NEWLINE);
        write(strValue);
        writeEndTag(strTag, blnNewLine);
    }

    /**************************************************************************
    * Write the specified value, enclosed in the start and end versions of 
    * the specified XML tag, to the Writer, followed by a newline.
    *@param  strTag     XML tag (without angle brackets).
    *@param  strValue   String value to enclose in XML tags.
    *@throws java.io.IOException
    *                   When an error occurs writing to the Writer.
    **************************************************************************/
    public void writeTagAndValue(String strTag, String strValue)
                 throws java.io.IOException
    {
        writeTagAndValue(strTag, strValue, blnWITH_NEWLINE);
    }

    /**************************************************************************
    * Check for errors in the nested Writer, if it's a PrintWriter.
    *@return            True if errors have occurred;
    *                   False if no errors, or the nested Writer is not 
    *                   a PrintWriter.
    **************************************************************************/
    public boolean checkError()
    {
        try
        {
            return ((PrintWriter)m_writer).checkError();
        }
        catch (ClassCastException e)
        {
            //-- Nothing to do here.  If the Writer is not a PrintWriter,
            //-- it won't have a checkError() method, but it will raise
            //-- an exception when any error occurs, so the caller will 
            //-- be aware of errors without calling this checkError()
            //-- method.
            return false;
        }
    }

    /**************************************************************************
    * Log the progress of the calling routine in generating its data stream,
    * and check for errors that have occurred when the calling routine wrote
    * to its XMLWriter.  Typically called every row of data, but only
    * bothers to do its various tasks (log, check for error, garbage collect)
    * periodically at different periods.
    * Return True if any errors have occurred; False otherwise.
    *@param  logger         Logger to log progress messages.
    *@param  intCount       Count of times called (used internally to decide 
    *                       whether to bother with periodic tasks).
    *@param  blnForceLog    Flag to force logging at this call, instead of just 
    *                       periodically.  Useful to log the final data row.
    *@return                True if errors have occurred; False otherwise.
    **************************************************************************/
    public boolean logProgressAndCheckError
                        (Logger    logger,
                         int       intCount,
                         boolean   blnForceLog)
    {
        //-- Periodically, call the Java garbage collector since it seems 
        //-- to fall behind if you don't sometimes call it explicitly.
        final int intGC_FREQUENCY = 100;
        if (intCount % intGC_FREQUENCY == 0)
        {
            System.gc();
        }

        //-- Periodically, log progress.
        final int intLOG_FREQUENCY = 100;
        if (blnForceLog || (intCount % intLOG_FREQUENCY == 0))
        {
            Logger.logSafely(logger, 4, "Row: " + intCount);
        }

        //-- Periodically, check for errors.
        //-- Note:  This is critical.  It is supposed to be inefficient, but 
        //--        harmless, to allow a servlet to continue writing to its 
        //--        response writer after the Web browser cancels the request.
        //--        However, with iPlanet 4.1 and iPlanet 6.0 SP2, when this 
        //--        class is used by a servlet to generate a long stream of 
        //--        output, but the browser cancels the request, the Web 
        //--        server goes crazy, rapidly consuming memory for as long 
        //--        as the writing continues.  The Web server can grow from
        //--        80MB to 800MB in a couple minutes, eventually crashing 
        //--        when the system runs out of swap space.  This check for 
        //--        errors allows us to detect that the browser has cancelled,
        //--        so that the caller of this routine aborts its output loop.
        //--        The Browser cancels when:
        //--        - The user clicks a 2nd time on a Download button before
        //--          the browser's download dialog box appears, which cancels 
        //--          the first download request.
        //--        - The user clicks the Web browser Stop button or hits Esc.
        //--        - The user moves to a different Web page.
        //--        - The user closes the browser.
        //--        - The browser crashes.
        final int intCHECK_ERROR_FREQUENCY = 100;
        if (intCount % intCHECK_ERROR_FREQUENCY == 0)
        if (intCount % 100 == 0)
        {
            if (checkError())
            {
                return true;
            }
        }
        return false;
    }

    /**************************************************************************
    * Log the progress of the calling routine in generating its data stream,
    * and check for errors that have occurred when the calling routine wrote
    * to this XMLWriter.  Typically called every row of data, but only
    * bothers to do its various tasks (log, check for error, garbage collect)
    * periodically at different periods.
    * Return True if any errors have occurred; False otherwise.
    *@param  logger         Logger to log progress messages.
    *@param  intCount       Count of times called (used internally to decide 
    *                       whether to bother with periodic tasks).
    *@return                True if errors have occurred; False otherwise.
    **************************************************************************/
    public boolean logProgressAndCheckError
                        (Logger    logger,
                         int       intCount)
    {
        return logProgressAndCheckError(logger, intCount, !m_blnFORCE_LOG);
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
