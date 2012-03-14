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

import java.util.Stack;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.File;

// FileTreeIterator
/******************************************************************************
* This class implements an iterator for a directory tree of files and 
* subdirectories.  Within each subdirectory, it returns the immediate
* children (files and subdirectories) in case-insensitive alphabetic order.  
* However, after returning each subdirectory, it returns the contents of 
* the subdirectory, recursively, before continuing with the remaining siblings 
* of that subdirectory.  That is, it performs a "depth-first pre-order 
* left-to-right" traversal.  It does not return the top-level directory that 
* was originally specified for traversal -- only its descendants. 
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*       for (Iterator i = new FileTreeIterator("/dir"); i.hasNext(); )
*       {
*           File file = (File)i.next();
*           doSomething(file);
*       }
*
*   - See the source code of the inner Tester class for more examples.
*  
*<b>Assumptions:</b>
*<b>Effects:</b>
*       - None.  Iterates over the directory tree, but does not change it.
*<b>Anticipated Changes:</b>
*       - Currently iterates forward only.  May add hasPrev() and prev() to 
*         support reverse iteration and reversing during an iteration. 
*       - May add support for resetting the iteration to a specified index 
*         within the current subdirectory (the 3rd file, 5th file, etc.)
*       - May add support for querying the index within the current 
*         subdirectory.
*       - Currently caches the entire set of filenames in a single directory,
*         internally as an array of Strings.  May add an option to cache less
*         and retrieve more directly from the filesystem on each iteration.
*       - May add new constructor with a FilenameFilter parameter, so the 
*         iterator returns a set of Files filtered by name.
*       - May add new constructor with a FileFilter parameter, so the 
*         iterator returns a set of Files filtered by other File properties.
*         Note:  Supporting FileFilter will be easiest to do by switching 
*                internally from File.list() to File.listFiles(), but that
*                will make it harder to sort by filename.
*       - May add additional traversal orders:
*         - breadth-first, instead of depth-first
*         - case-sensitive, instead of case-insensitive
*         - post-order, instead of pre-order
*         - right-to-left, instead of left-to-right     
*       - May add a limit on the depth to traverse, so caller can limit a
*         traversal to children only, children and grandchildren only, etc. 
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class FileTreeIterator implements Iterator
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

    // The most nested current directory.
    // For example:
    //          /home/fred/Pictures/House/20040920
    String m_strDirectory = null;

    // The stack of indexes (zero-based indexes into arrays of filenames) 
    // into each of the nested current directories.  There should be one item 
    // in this stack for each nested directory in the value of m_strDirectory 
    // starting at the root value where the iteration started.  In each case,
    // the index refers to the file (which may be a subdirectory) in that 
    // nested subdirectory that will be the next to be processed.  For example, 
    // if the starting point was:
    //          /home/fred/Pictures
    // and the current value of strDirectory is:
    //          /home/fred/Pictures/House/20040920
    // the stack should contain 3 values, like:
    //          7       Currently processing the 8th file in Pictures,
    //                  the subdirectory House.  Have already returned 
    //                  House, but not yet all of its descendants.
    //          9       Currently processing the 10th file in House,
    //                  the subdirectory 20040920.  Have already returned 
    //                  20040920, but not yet all of its descendants.
    //          2       About to process the 3rd file in 20040920.
    Stack m_stackIndexes = new Stack();

    // Array of filenames in the directory named by m_strDirectory.
    // This array is indexed by the value at the top of the stack 
    // m_stackIndexes.
    String[] m_arrNames = null;

    /**************************************************************************
    * Constructor.
    *@param  strDirectoryName   String name of the directory tree to iterate.
    *@throws NullPointerException If strDirectoryName is null.
    **************************************************************************/
    public FileTreeIterator(String strDirectoryName)
    {
        // Initialize all the internal data to point to the next file in the 
        // iteration, if any.  Each operation on the iterator leaves it 
        // pointing to the next file, the first one that has not yet been 
        // returned.
        if (strDirectoryName == null)
        {
            throw new NullPointerException ("strDirectoryName is null");
        }
        m_strDirectory = strDirectoryName;
        m_arrNames = FileUtil.getSortedNames(new File(m_strDirectory));
        if (m_arrNames != null && m_arrNames.length > 0)
        {
            m_stackIndexes.push(new Integer(0));
        }
    }

    /**************************************************************************
    * Returns true if there are more Files to be iterated; false otherwise.
    *@return true if there are more Files to be iterated; false otherwise
    **************************************************************************/
    public boolean hasNext()
    {
        return !m_stackIndexes.empty();
    }

    /**************************************************************************
    * Returns the next File in the iteration.
    *@return the next File in the iteration
    *@throws NoSuchElementException when no more Files in the iteration
    **************************************************************************/
    public Object next() throws NoSuchElementException
    {
        if (!hasNext())
        {
            throw new NoSuchElementException("There are no more files in the" +
                    " iteration of " + m_strDirectory);
        }        
        File file = new File (m_strDirectory + File.separator 
                              + m_arrNames[getIndex()]);
        findNext();
        return file;
    }

    /**************************************************************************
    * Not implemented.  This is an optional method of the Iterator interface.
    *@throws UnsupportedOperationException
    **************************************************************************/
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    /**************************************************************************
    * Get the value on the top of the stack of indexes.
    *@return Value from the top of the stack.
    **************************************************************************/
    private int getIndex()
    {
        return ((Integer)m_stackIndexes.peek()).intValue();
    }

    /**************************************************************************
    * Increment the value on the top of the stack of indexes.
    **************************************************************************/
    private void incrementIndex()
    {
        int intIndex = ((Integer)m_stackIndexes.pop()).intValue();
        m_stackIndexes.push(new Integer(intIndex + 1));
    }

    /**************************************************************************
    * Advance to the next file (which may be a subdirectory) in the iteration, 
    * if any.
    **************************************************************************/
    private void findNext()
    {

        // Flag used to distinguish between loop iterations in 2 different
        // situations:
        // true  = We're at a file that has already been returned by the 
        //         iterator, and should skip it to find the next one. 
        // false = We're at a file that has not yet been returned by the 
        //         iterator, and should be considered as a possible next
        //         one.
        // The reason we need this flag is because some loop iterations 
        // occur when we've already descended into a subdirectory and not
        // yet discovered that it is empty, or we've already popped back
        // to the parent and not yet discovered that there are no more 
        // siblings, etc.  So we keep looping, but don't want to skip 
        // files that haven't yet been returned.
        // Initially, set the flag true because this method is called to
        // advance to the next file after the current one has been returned.
        boolean blnReturned = true;

        // Loop through the directory tree, finding the next file to be 
        // returned by the iterator.
        while(true)
        {
            // If the stack of indexes is empty, there is no next file to 
            // process.  The iteration is done.  Simply return.
            if (m_stackIndexes.empty())
            {
                return;
            }        
    
            // If the directory is empty or fully processed, resume searching 
            // in the parent directory, if any.
            if (   m_arrNames == null 
                || m_arrNames.length == 0
                || getIndex() >= m_arrNames.length)
            {
                if (!m_stackIndexes.empty())
                {
                    m_stackIndexes.pop();
                }

                if (m_stackIndexes.empty())
                {
                    // There is no parent directory in which to resume, 
                    // which means the iteration has finished the entire 
                    // directory tree.
                    return;
                }

                // The parent directory exists.  Resume searching there.
                m_strDirectory = new File(m_strDirectory).getParent();
                m_arrNames = FileUtil.getSortedNames(new File(m_strDirectory));
                incrementIndex();       // Skip to next sibling of parent to 
                                        // avoid dropping into this subdirectory
                                        // again.
                blnReturned = false;    // Flag next sibling as not yet 
                                        // returned by the iterator to avoid 
                                        // skipping it in this loop.
                                        // Note:  We can't simply return here 
                                        //        because the next sibling may 
                                        //        not exist.  The next loop 
                                        //        iteration will check this
                                        //        and continue searching if 
                                        //        necessary.
            }
            else 
            {
                // The directory is not empty or fully processed.  If the 
                // current sibling has not already been returned by the 
                // iterator, it is the next file to return.
                if (!blnReturned)
                {
                    return;
                }

                // The current sibling has already been returned.  If it is a 
                // subdirectory, descend into it.
                String strFilename 
                    = m_strDirectory  + File.separator + m_arrNames[getIndex()];
                File file = new File(strFilename);
                if (file.isDirectory())
                {
                    m_stackIndexes.push(new Integer(0));
                    m_strDirectory = strFilename;
                    m_arrNames = FileUtil.getSortedNames
                                        (new File(m_strDirectory));
                    blnReturned = false;    // Flag first child as not yet 
                                            // returned by the iterator to avoid 
                                            // skipping it in this loop.
                                            // Note:  We can't simply return
                                            //        here because the first 
                                            //        child may not exist.  The
                                            //        subdirectory may be empty.
                                            //        The next loop iteration
                                            //        will check this and 
                                            //        continue searching if 
                                            //        necessary.
                }
                else                
                {
                    // The current sibling is not a directory.  Move to the 
                    // next sibling.
                    incrementIndex();
                    blnReturned = false;    // Flag next sibling as not yet 
                                            // returned by the iterator to avoid 
                                            // skipping it in this loop.
                                            // Note:  We can't simply return  
                                            //        here because the next  
                                            //        sibling may not exist.   
                                            //        The next loop iteration 
                                            //        will check this and 
                                            //        continue searching if 
                                            //        necessary.
                    
                }
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
            File file = null; 

            try
            {
                System.out.println ("Begin tests...");
                System.out.println ("");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- iterate a non-existent directory");
                System.out.println ("--");
                //-------------------------------------------------------------
                printTree(new FileTreeIterator("no_such_directory"));
                
                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- iterate a non-directory file");
                System.out.println ("--");
                //-------------------------------------------------------------
                file = new File("not_a_directory");
                file.createNewFile();
                printTree(new FileTreeIterator("not_a_directory"));
                file.delete();
                
                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- iterate an empty tree");
                System.out.println ("--");
                //-------------------------------------------------------------
                file = new File("empty");
                file.mkdir();
                printTree(new FileTreeIterator("empty"));
                file.delete();
                
                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- iterate a one-level tree");
                System.out.println ("--");
                //-------------------------------------------------------------
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
                    FileUtil.deleteDirectoryTree("a");
                }
                    
                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- iterate a multi-level tree");
                System.out.println ("--");
                //-------------------------------------------------------------
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
                    FileUtil.deleteDirectoryTree("a");
                }

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- iterate a multi-level tree w/empty branches");
                System.out.println ("--");
                //-------------------------------------------------------------
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
                    FileUtil.deleteDirectoryTree("a");
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

