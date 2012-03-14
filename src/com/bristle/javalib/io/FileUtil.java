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

package com.bristle.javalib.io;

import com.bristle.javalib.util.ArrUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;

// FileUtil
/******************************************************************************
* This class contains utility routines for manipulating files.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*       String s = FileUtil.getTextFileContents("/dir/filename.txt");
*
*   - See the source code of the inner Tester class for more examples.
*  
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
public class FileUtil
{
    //--
    //-- Constants for readability
    //--
    public static final long lngNO_MAX_BYTES = -1;

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
    * Returns the contents of the requested text file as a string.
    *@param  reader        Reader of the file to get.
    *@return               Contents of the file as a string.
    *@throws IOException   When an error occurs reading the file.
    **************************************************************************/
    public static String getTextFileContents(Reader reader)
                                throws IOException
    {
        BufferedReader buffReader = new BufferedReader(reader);
        StringBuffer   sbIn   = new StringBuffer();
        String         strIn  = null;
        while (null != (strIn = buffReader.readLine()))
        {
            sbIn.append(strIn);
            sbIn.append("\n");
        }
        return sbIn.toString();
    }

    /**************************************************************************
    * Returns the contents of the requested text file as a string, or null 
    * if no such file exists or any other error occurs. 
    *@param  reader        Reader of the file to get.
    *@return               Contents of the file as a string.
    **************************************************************************/
    public static String getTextFileContentsOrNull(Reader reader)
    {
        try
        {
            return getTextFileContents(reader);
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    /**************************************************************************
    * Returns the contents of the requested text file as a string, or the  
    * empty String ("") if no such file exists or any other error occurs. 
    *@param  reader        Reader of the file to get.
    *@return               Contents of the file as a string.
    **************************************************************************/
    public static String getTextFileContentsOrEmpty(Reader reader)
    {
        try
        {
            return getTextFileContents(reader);
        }
        catch (Throwable e)
        {
            return "";
        }
    }

    /**************************************************************************
    * Returns the contents of the requested text file as a string.
    *@param  istream       InputStream of the file to get.
    *@return               Contents of the file as a string.
    *@throws IOException   When an error occurs reading the file.
    **************************************************************************/
    public static String getTextFileContents(InputStream istream)
                                throws IOException
    {
        return getTextFileContents(new InputStreamReader(istream));
    }

    /**************************************************************************
    * Returns the contents of the requested text file as a string, or null 
    * if no such file exists or any other error occurs. 
    *@param  istream       InputStream of the file to get.
    *@return               Contents of the file as a string.
    **************************************************************************/
    public static String getTextFileContentsOrNull(InputStream istream)
    {
        return getTextFileContentsOrNull(new InputStreamReader(istream));
    }

    /**************************************************************************
    * Returns the contents of the requested text file as a string, or the  
    * empty String ("") if no such file exists or any other error occurs. 
    *@param  istream       InputStream of the file to get.
    *@return               Contents of the file as a string.
    **************************************************************************/
    public static String getTextFileContentsOrEmpty(InputStream istream)
    {
        return getTextFileContentsOrEmpty(new InputStreamReader(istream));
    }

    /**************************************************************************
    * Returns the contents of the requested text file as a string.
    *@param  strFilename   String name of the file to get.
    *@return               Contents of the file as a string.
    *@throws FileNotFoundException
    *                      When the specified file is not found.
    *@throws IOException   When an error occurs reading the file.
    **************************************************************************/
    public static String getTextFileContents(String strFilename)
                                throws FileNotFoundException,
                                       IOException
    {
        return getTextFileContents(new FileReader(strFilename));
    }

    /**************************************************************************
    * Returns the contents of the requested text file as a string, or null 
    * if no such file exists or any other error occurs. 
    *@param  strFilename   String name of the file to get.
    *@return               Contents of the file as a string.
    **************************************************************************/
    public static String getTextFileContentsOrNull(String strFilename)
    {
        try
        {
            return getTextFileContents(new FileReader(strFilename));
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    /**************************************************************************
    * Returns the contents of the requested text file as a string, or the  
    * empty String ("") if no such file exists or any other error occurs. 
    *@param  strFilename   String name of the file to get.
    *@return               Contents of the file as a string.
    **************************************************************************/
    public static String getTextFileContentsOrEmpty(String strFilename)
    {
        try
        {
            return getTextFileContents(new FileReader(strFilename));
        }
        catch (Throwable e)
        {
            return "";
        }
    }

    /**************************************************************************
    * Copy the contents of the specified binary file to the specified Writer.
    *@param  strFilename       Name of the file to copy.
    *@param  writer            Writer to write to.
    *@throws FileNotFoundException
    *                           When binary file not found.
    *@throws IOException        When an I/O error occurs reading the file or 
    *                           writing it to the Writer.
    **************************************************************************/
    public static void copyBinaryFileToWriter
                (String strFilename,
                 Writer writer)
                throws FileNotFoundException
                      ,IOException
    {
        RandomAccessFile file = new RandomAccessFile(strFilename, "r");
        try 
        {
            int i;
            while (-1 != (i = file.read()))
            {
                writer.write((char)i);
            }
            writer.flush();
        }
        finally
        {
            file.close();
        }
    }

    /**************************************************************************
    * Thrown when an attempt is made to operate on too many bytes of data.
    **************************************************************************/
    public static class TooManyBytesException extends Exception
    {
        private static final long serialVersionUID = 1L;
        private final long m_lngBytesWritten;
        public TooManyBytesException(String msg, long lngBytesWritten)
        { 
            super(msg);
            m_lngBytesWritten = lngBytesWritten;
        }
        public long getBytesWritten()
        {
            return m_lngBytesWritten;
        }
    }

    /**************************************************************************
    * Copy the binary contents of the specified InputStream to the specified 
    * OutputStream, up to the specified number of bytes, returning the count
    * of bytes written.
    *@param  streamIn       InputStream to read from
    *@param  streamOut      OutputStream to write to
    *@param  lngMaxBytes    Max number of bytes to copy, or lngNO_MAX_BYTES 
    *                       for unlimited
    *@return                Count of bytes written
    *@throws TooManyBytesException
    *                       When streamIn contains more than intMaxBytes,
    *                       resulting in a partially copied binary stream.
    *@throws IOException    When an I/O error occurs reading or writing a
    *                       stream.
    **************************************************************************/
    public static long copyBinaryStreamToStream
                (InputStream  streamIn
                ,OutputStream streamOut
                ,long         lngMaxBytes
                )
                throws TooManyBytesException
                      ,IOException
    {
        // Buffer the input and output for efficiency, to avoid lots of 
        // small I/O operations.
        // Note: Don't call the read() and write() methods that could do it 
        //       all in a single byte-array because we don't want to consume 
        //       that caller-specified amount of memory.  Better to do it
        //       one byte at a time, but with buffering for efficiency.
        BufferedInputStream  buffIn  = new BufferedInputStream (streamIn);
        BufferedOutputStream buffOut = new BufferedOutputStream(streamOut);

        long lngBytesWritten = 0;
        for (int intByte = buffIn.read();
             intByte > -1;
             intByte = buffIn.read())
        {
            if (   lngMaxBytes != lngNO_MAX_BYTES 
                && lngBytesWritten >= lngMaxBytes)
            {
                buffOut.flush();
                throw new TooManyBytesException
                    ("The input stream contains more than " 
                     + lngMaxBytes + " bytes.  Only "
                     + lngBytesWritten + " bytes were written to the "
                     + "output stream"
                    ,lngBytesWritten
                    );
            }
            buffOut.write(intByte);
            lngBytesWritten++;
        }
        buffOut.flush();
        return lngBytesWritten;
    }

    /**************************************************************************
    * Copy the binary contents of the specified InputStream to the specified
    * Writer, up to the specified number of bytes, returning the count of 
    * bytes written.
    *@param  streamIn       InputStream to read from
    *@param  writerOut      Writer to write to
    *@param  lngMaxBytes    Max number of bytes to copy, or lngNO_MAX_BYTES
    *                       for unlimited
    *@return                Count of bytes written
    *@throws TooManyBytesException
    *                       When streamIn contains more than intMaxBytes,
    *                       resulting in a partially copied binary stream.
    *@throws IOException    When an I/O error occurs reading or writing a
    *                       stream or Writer.
    **************************************************************************/
    public static long copyBinaryStreamToWriter
                (InputStream  streamIn
                ,Writer       writerOut
                ,long         lngMaxBytes
                )
                throws TooManyBytesException
                      ,IOException
    {
        // Note: It seems as though this method should be able to call 
        //       copyBinaryStreamToStream() instead of being a nearly 
        //       identical copy of the code, but how to convert a Writer 
        //       to an OutputStream to pass to copyBinaryStreamToStream()?
        
        // Buffer the input and output for efficiency, to avoid lots of 
        // small I/O operations.
        // Note: Don't call the read() and write() methods that could do it 
        //       all in a single byte-array because we don't want to consume 
        //       that caller-specified amount of memory.  Better to do it
        //       one byte at a time, but with buffering for efficiency.
        BufferedInputStream  buffIn  = new BufferedInputStream (streamIn);
        BufferedWriter       buffOut = new BufferedWriter      (writerOut);

        long lngBytesWritten = 0;
        for (int intByte = buffIn.read();
             intByte > -1;
             intByte = buffIn.read())
        {
            if (   lngMaxBytes != lngNO_MAX_BYTES 
                && lngBytesWritten >= lngMaxBytes)
            {
                buffOut.flush();
                throw new TooManyBytesException
                    ("The input stream contains more than " 
                     + lngMaxBytes + " bytes.  Only "
                     + lngBytesWritten + " bytes were written to the "
                     + "writer"
                    ,lngBytesWritten
                    );
            }
            buffOut.write(intByte);
            lngBytesWritten++;
        }
        buffOut.flush();
        return lngBytesWritten;
    }
    
    /**************************************************************************
    * Copy the binary contents of the specified InputStream to the file with 
    * the specified name, up to the specified number of bytes, returning the 
    * count of bytes written.
    *@param  streamIn       InputStream to read from
    *@param  strFilenameOut String name of the file to write to
    *@param  lngMaxBytes    Max number of bytes to copy, or lngNO_MAX_BYTES
    *                       for unlimited
    *@return                Count of bytes written
    *@throws TooManyBytesException
    *                       When streamIn contains more than intMaxBytes,
    *                       resulting in a partially copied binary stream.
    *                       The file is still closed properly.
    *@throws FileNotFoundException 
    *                       When the output file cannot be created or written.
    *@throws IOException    When an I/O error occurs reading or writing a
    *                       stream or file.
    **************************************************************************/
    public static long copyBinaryStreamToFile
                (InputStream  streamIn
                ,String       strFilenameOut
                ,long         lngMaxBytes
                )
                throws TooManyBytesException
                      ,FileNotFoundException 
                      ,IOException
    {
        FileOutputStream streamOut = new FileOutputStream(strFilenameOut);
        try
        {
            return copyBinaryStreamToStream(streamIn, streamOut, lngMaxBytes);
        }
        finally
        {
            streamOut.close();
        }
    }
    
    /**************************************************************************
    * Copy the binary contents of the file with the specified name to the 
    * specified OutputStream, up to the specified number of bytes, returning 
    * the count of bytes written.
    *@param  strFilenameIn  String name of the file to read from
    *@param  streamOut      OutputStream to write to
    *@param  lngMaxBytes    Max number of bytes to copy, or lngNO_MAX_BYTES 
    *                       for unlimited
    *@return                Count of bytes written
    *@throws TooManyBytesException
    *                       When the input file contains more than intMaxBytes,
    *                       resulting in a partially copied binary stream.
    *                       The input file is still closed properly, and the
    *                       output stream is flushed.
    *@throws FileNotFoundException 
    *                       When the input file cannot be found or read.
    *@throws IOException    When an I/O error occurs reading or writing a
    *                       stream or file.
    **************************************************************************/
    public static long copyBinaryFileToStream
                (String       strFilenameIn
                ,OutputStream streamOut
                ,long         lngMaxBytes
                )
                throws TooManyBytesException
                      ,FileNotFoundException 
                      ,IOException
    {
        FileInputStream streamIn = new FileInputStream(strFilenameIn);
        try
        {
            return copyBinaryStreamToStream(streamIn, streamOut, lngMaxBytes);
        }
        finally
        {
            streamIn.close();
        }
    }
    
    /**************************************************************************
    * Copy the binary contents of the file with the specified name to the 
    * specified Writer, up to the specified number of bytes, returning 
    * the count of bytes written.
    *@param  strFilenameIn  String name of the file to read from
    *@param  writerOut      Writer to write to
    *@param  lngMaxBytes    Max number of bytes to copy, or lngNO_MAX_BYTES 
    *                       for unlimited
    *@return                Count of bytes written
    *@throws TooManyBytesException
    *                       When the input file contains more than intMaxBytes,
    *                       resulting in a partially copied binary stream.
    *                       The input file is still closed properly, and the
    *                       Writer is flushed.
    *@throws FileNotFoundException 
    *                       When the input file cannot be found or read.
    *@throws IOException    When an I/O error occurs reading or writing a
    *                       Writer or file.
    **************************************************************************/
    public static long copyBinaryFileToWriter
                (String       strFilenameIn
                ,Writer       writerOut
                ,long         lngMaxBytes
                )
                throws TooManyBytesException
                      ,FileNotFoundException 
                      ,IOException
    {
        FileInputStream streamIn = new FileInputStream(strFilenameIn);
        try
        {
            return copyBinaryStreamToWriter(streamIn, writerOut, lngMaxBytes);
        }
        finally
        {
            streamIn.close();
        }
    }
    
    /**************************************************************************
    * Skip past all bytes in the specified InputStream, returning the count
    * of bytes found.
    *@param  streamIn       InputStream to read from
    *@return                Count of bytes found in the stream.
    *@throws IOException    When an I/O error occurs reading or writing a
    *                       stream.
    **************************************************************************/
    public static long skipEntireStreamReturningCount
                (InputStream  streamIn)
                throws IOException
    {
        // Buffer the input and output for efficiency, to avoid lots of 
        // small I/O operations.
        // Note: Don't call the read() and write() methods that could do it 
        //       all in a single byte-array because we don't want to consume 
        //       that caller-specified amount of memory.  Better to do it
        //       one byte at a time, but with buffering for efficiency.
        BufferedInputStream  buffIn  = new BufferedInputStream (streamIn);

        long lngCount = 0;
        for (int intByte = buffIn.read();
             intByte > -1;
             intByte = buffIn.read())
        {
            lngCount++;
        }
        return lngCount;
    }

    /**************************************************************************
    * Append the specified string to the specified file, opening and closing
    * the file to do so.
    *@param  strFilename   String name of the file to get.
    *@param  strString     String to append to the file.
    *@throws IOException   When an error occurs writing the file.
    **************************************************************************/
    public static void appendToFile
                                (String strFilename,
                                 String strString)
                                throws IOException
    {
        final boolean blnAPPEND = true;
        BufferedWriter fileOut = new BufferedWriter
                                     (new FileWriter(strFilename, blnAPPEND));
        fileOut.write(strString);
        fileOut.close();
    }

    /**************************************************************************
    * This class implements FilenameFilter, filtering based on one or
    * more filename extensions.
    **************************************************************************/
    public static class ExtensionFilenameFilter implements FilenameFilter
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
        private ArrayList m_alExtensions = new ArrayList();

        /**********************************************************************
        * Constructor.
        *@param  strExtensions  Comma-separated list of filename extensions,
        *                       with or without the leading dots.  Dots are
        *                       automatically prepended to extensions where 
        *                       not already present.
        **********************************************************************/
        public ExtensionFilenameFilter(String strExtensions)
        {
            StringTokenizer st = new StringTokenizer(strExtensions, ",");
            while (st.hasMoreTokens())
            {
                String strExtension = st.nextToken();
                if (!strExtension.startsWith("."))
                {
                     strExtension = "." + strExtension;
                }
                m_alExtensions.add(strExtension);
            }
        }

        /**********************************************************************
        * Return true or false based on whether strFileName matches any of the 
        * extensions stored internally.
        *@param  fileDirectory  The directory in which the file was found.
        *@param  strFilename    The name of the file.
        **********************************************************************/
        public boolean accept(File fileDirectory, String strFilename) 
        {
            for (Iterator i = m_alExtensions.iterator(); i.hasNext(); )
            {
                String strExtension = (String)i.next();
                if (strFilename.endsWith(strExtension))
                {
                    return true;
                }
            }
            return false;
        }
    }

    /**************************************************************************
    * Returns a sorted array of Strings naming the files and directories in
    * the specified directory.
    *@param  directory  The directory to search
    *@return            Sorted array of file and directories in the directory.
    **************************************************************************/
    public static String[] getSortedNames(File directory)
    {
        String[] arrNames = directory.list();  
        if (arrNames != null)
        {
            Arrays.sort(arrNames, String.CASE_INSENSITIVE_ORDER);
        }
        return arrNames; 
    }

    /**************************************************************************
    * Thrown when an attempt to rename a file fails.
    **************************************************************************/
    public static class FileRenameException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public FileRenameException(String msg) { super(msg); }
    }

    /**************************************************************************
    * Rename the file with the specified old name to the specified new name.
    *@param  strOldFileName         Old name of the file.
    *@param  strNewFileName         New name of the file.
    *@throws FileRenameException    When unable to rename the file.
    **************************************************************************/
    public static void rename(String strOldFileName, String strNewFileName) 
                              throws FileRenameException
    {
        File fileOld = new File(strOldFileName); 
        File fileNew = new File(strNewFileName); 
        if (!fileOld.renameTo(fileNew))
        {
            throw new FileRenameException
                    ("Unable to rename file " + strOldFileName
                     + " to " + strNewFileName);
        }
    }

    /**************************************************************************
    * Thrown when an attempt to delete a file fails.
    **************************************************************************/
    public static class FileDeleteException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public FileDeleteException(String msg) { super(msg); }
    }

    /**************************************************************************
    * Delete the file with the specified name, if it exists, returning true 
    * if it existed and was successfully deleted.
    *@param  strFileName            Name of the file to delete.
    **************************************************************************/
    public static boolean deleteIfExists(String strFileName)
    {
        File file = new File(strFileName); 
        return file.delete();
    }

    /**************************************************************************
    * Delete the file with the specified name.
    *@param  strFileName            Name of the file to delete.
    *@throws FileDeleteException    When unable to delete the file.
    **************************************************************************/
    public static void delete(String strFileName) throws FileDeleteException
    {
        File file = new File(strFileName); 
        if (!file.delete())
        {
            throw new FileDeleteException
                    ("Unable to delete file " + strFileName);
        }
    }

    /**************************************************************************
    * Delete the directory tree with the specified name.
    * Note:  This uses a brute force approach, failing on all non-empty
    *       directories potentially multiple times before finally 
    *       emptying them all and succeeding.
    *       It would be better to use a post-order iterator, one that
    *       returns the contents of a subdirectory before returning 
    *       the subdirectory itself.
    *       It also deletes files as they are being iterated, which
    *       may confuse the iteration, generating errors before finally
    *       succeeding.
    *       It would be better to use an iterator that implements 
    *       delete(), or to collect the names first and delete afterwards.
    *       For now, this works, and is useful for cleaning after the 
    *       test cases of FileTreeIterator. 
    *@param  strDirectoryName  String name of the directory tree to delete.
    *@return                   True if successful; false otherwise.  Can fail
    *                          if a file is locked or protected, for example. 
    **************************************************************************/
    public static boolean deleteDirectoryTree
                                (String strDirectoryName)
    {
        // Keep re-doing the iteration until it succeeds, but quit after 100
        // tries in case some locked file is preventing it from succeeding.
        int count = 0;
        File fileDirectory = new File(strDirectoryName); 
        while (fileDirectory.exists())
        {
            for (Iterator i = new FileTreeIterator(strDirectoryName); 
                 i.hasNext();
                 )
            {
                ((File)i.next()).delete();
            }
            fileDirectory.delete();
            if (count++ > 100)
            {
                return false;
            }
        }
        return true;
    }

    /**************************************************************************
    * Return the simple name, including extension, but not path, of the 
    * specified filename.
    * Note: This only works for filenames that use the syntax of the native
    *       OS.  To parse Windows filenames on a Unix system, or vice versa,
    *       for example, when uploading files from a Web browser to a Web 
    *       server and parsing the filenames sent by the browser, use
    *       org.apache.commons.io.FilenameUtils.getName() instead.
    *@param  strFileName    File name
    *@return                Simple file name
    **************************************************************************/
    public static String getFileNameWithoutPath(String strFileName)
    {
        File file = new File(strFileName);
        return file.getName();
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
        private static void printTree(FileTreeIterator iter)
        {
            while (iter.hasNext())
            {
                System.out.println (((File)iter.next()).getAbsoluteFile());
            }
        }

        /**********************************************************************
        * Main testing method.
        *@param  args       Array of command line argument strings
        **********************************************************************/
        public static void main(String[] args)
        {
            try
            {
                System.out.println ("Begin tests...");
                System.out.println ("");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- getTextFileContents");
                System.out.println ("--");
                //-------------------------------------------------------------

                System.out.println ("Contents of FileUtil.txt are:");
                System.out.println (getTextFileContents("FileUtil.txt"));

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- getSortedNames");
                System.out.println ("--");
                //-------------------------------------------------------------

                System.out.println ("Unsorted contents of valid directory are:");
                File dir = new File("../valid");
                System.out.println (ArrUtil.arrayToString(dir.list()));
                System.out.println ("Sorted contents of valid directory are:");
                System.out.println (ArrUtil.arrayToString(getSortedNames(dir)));

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- deleteDirectoryTree");
                System.out.println ("--");
                //-------------------------------------------------------------

                System.out.println ("-- delete an empty tree");
                try
                {
                    new File("empty").mkdir();
                    printTree(new FileTreeIterator("empty"));
                }
                finally
                {
                    deleteDirectoryTree("empty");
                }
                
                System.out.println ("-- delete a one-level tree");
                try
                {
                    new File("a").mkdir();
                    new File("a/a1").createNewFile();
                    new File("a/a3").createNewFile();
                    new File("a/a2").createNewFile();
                    printTree(new FileTreeIterator("a"));
                }
                finally
                {
                    deleteDirectoryTree("a");
                }
                    
                System.out.println ("-- delete a multi-level tree");
                try
                {
                    new File("a").mkdir();
                    new File("a/a1").mkdir();
                    new File("a/a1/a11").createNewFile();
                    new File("a/a2").mkdir();
                    new File("a/a2/a21").createNewFile();
                    new File("a/a2/a22").createNewFile();
                    new File("a/a3").mkdir();
                    new File("a/a3/a31").createNewFile();
                    new File("a/a3/a32").createNewFile();
                    new File("a/a3/a33").createNewFile();
                    printTree(new FileTreeIterator("a"));
                }
                finally
                {
                    deleteDirectoryTree("a");
                }

                System.out.println ("-- delete a multi-level tree w/empty branches");
                try
                {
                    new File("a").mkdir();
                    new File("a/a1").mkdir();
                    new File("a/a1/a11").createNewFile();
                    new File("a/a2").mkdir();
                    new File("a/a3").mkdir();
                    printTree(new FileTreeIterator("a"));
                }
                finally
                {
                    deleteDirectoryTree("a");
                }


                System.out.println ("");
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

