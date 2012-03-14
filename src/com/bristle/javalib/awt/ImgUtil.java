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

package com.bristle.javalib.awt;

import javax.imageio.ImageIO;

import com.bristle.javalib.io.FileTreeIterator;
import com.bristle.javalib.log.Logger;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;

// ImgUtil
/******************************************************************************
* This class contains utility routines for manipulating images.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*     - To convert an Image to a BufferedImage:
*           BufferedImage bi = ImgUtil.toBufferedImage(image);
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
public class ImgUtil
{
    //--
    //-- Constants for readability
    //--
    public static final boolean blnPRESERVE_ASPECT_RATIO         = true;
    public static final int     intMAX_SCALE_CURRENT_SIZE        = 100;
    public static final int     intMAX_SCALE_DOUBLE_CURRENT_SIZE = 200;
    public static final int     intMAX_SCALE_TRIPLE_CURRENT_SIZE = 300;
    public static final int     intMAX_SCALE_NO_LIMIT            = -1;

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
    * This exception is thrown when the specified data source (InputStream,
    * file, etc) contains no image.
    **************************************************************************/
    public static class NoImageFoundException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public NoImageFoundException(String msg) { super(msg); }
    }

    /**************************************************************************
    * Set AWT to run in headless mode, meaning there is no local graphical 
    * display on the server.  This may be required to prevent some of the 
    * methods in this class from failing.  For example, unless this method is
    * called first, the method getScaledImage() fails with Java 1.4.2, on a 
    * Tomcat 4 Web server if there is no X11 server running on the Web server.
    * To avoid the problem on such servers, do one of the following:
    *   1.  Call this method after the Web server is started and before any
    *       servlet attempts to call getScaledImage() or any other AWT method
    *       that has the same problem.  Calling it from the init() method of
    *       each such servlet might be a good idea.  Calling it after the error
    *       occurs, in an exception handler, for example, is too late and has 
    *       no effect.  Once the error has occurred, Tomcat must be restarted
    *       and this method must be called before the first attempt to call
    *       getScaledImage().
    *   2.  Set the environment variable:
    *           setenv CATALINA_OPTS -Djava.awt.headless=true
    *       before starting the Tomcat server, or
    *   3.  In some other way, define java.awt.headless=true in the Java
    *       environment of the Tomcat Web server.    
    * When the error occurs, it reports either:
    *       java.lang.InternalError: Can't connect to X11 window server using 
    *       ':0.0' as the value of the DISPLAY variable.
    * or:
    *       java.lang.NoClassDefFoundError  
    * Don't call this method unnecessarily, because it does the following 
    * internally:
    *       System.setProperty("java.awt.headless", "true");
    * which has a global effect on the entire JVM, including other Web apps 
    * running in the same Tomcat server.  It is because of this global effect
    * that this method is not called automatically by getScaledImage().
    **************************************************************************/
    public static void setAwtHeadless()
    {
        System.setProperty("java.awt.headless", "true");
    }

    /**************************************************************************
    * Return true if AWT is set to run in headless mode.
    *@return True if headless; false otherwise.
    *@see #setAwtHeadless()
    **************************************************************************/
    public static boolean getAwtHeadless()
    {
        return "true".equalsIgnoreCase(System.getProperty("java.awt.headless"));
    }

    /**************************************************************************
    * Returns a BufferedImage from the specified Image.
    *@param  img    The input Image
    *@return        The corresponding BufferedImage
    **************************************************************************/
    public static BufferedImage toBufferedImage(Image img)
    {
        // Note:  Using null as a parameter to getWidth(), getHeight(), and
        //        drawImage() seems to work fine.  However the docs say that
        //        the image may not be fully loaded, in which case an 
        //        ImageObserver should be used to detect when it is loaded.
        //        If we ever start having problems with partially loaded 
        //        images, we may have to do so.
        BufferedImage bi = new BufferedImage
                                    (img.getWidth(null), 
                                     img.getHeight(null), 
                                     BufferedImage.TYPE_INT_RGB);
        bi.createGraphics().drawImage(img, 0, 0, null);
        return bi;
    }

    /**************************************************************************
    * This class represents an enumerated type that specifies whether scaling
    * of Images should be done with an emphasis on speed or image smoothness.  
    * The constants defined in this class are typically used as arguments to 
    * the other methods in the enclosing class.  
    **************************************************************************/
    public static class ScaleAlgorithm
    {
        public final static ScaleAlgorithm FAST  = 
                                    new ScaleAlgorithm("ScaleAlgorithm.FAST");
        public final static ScaleAlgorithm SMOOTH = 
                                    new ScaleAlgorithm("ScaleAlgorithm.SMOOTH");
        private final String m_strName;  //-- "blank final" assigned in the ctor.
        //-- Note:  No public ctor so new values cannot be created.  Those
        //--        listed above are the only ones.
        private ScaleAlgorithm(String strName) { m_strName = strName; }
        public String toString() { return m_strName; }
        public static class UnexpectedEnumValueException extends Exception
        {
            private static final long serialVersionUID = 1L;
            public UnexpectedEnumValueException(String msg) { super(msg); }
        }
    }

    /**************************************************************************
    * Returns a scaled copy of the specified Image.
    *@param  img        The input Image
    *@param  intWidth   The desired Image width, or negative number to 
    *                   maintain the original aspect ratio.
    *@param  intHeight  The desired Image height, or negative number to 
    *                   maintain the original aspect ratio.
    *@param  algorithm  Algorithm to use for scaling. 
    *@return            The scaled Image
    **************************************************************************/
    public static Image getScaledImage
            (Image img, int intWidth, int intHeight, ScaleAlgorithm algorithm)
    {
        int intHints = (algorithm == ScaleAlgorithm.FAST)
                        ? Image.SCALE_FAST
                        : Image.SCALE_SMOOTH; 
        img = img.getScaledInstance(intWidth, intHeight, intHints);
        System.gc();  //?? To prevent a memory leak when multiple images are 
                      //?? loaded concurrently.  Why necessary?  Does it help?
        System.gc();  //?? Call garbage collector a bunch of times.  Once doesn't
        System.gc();  //?? seem to be enough.  Why? 
        System.gc(); 
        System.gc(); 
        return img;
    }

    /**************************************************************************
    * Read an Image from the specified InputStream. 
    *@param  in          The InputStream to read the Image from.
    *@return             The Image, or null if the InputStream was empty.
    *@throws IOException When an error occurs reading the Image.
    *@throws NoImageFoundException 
    *                    When the InputStream contains no Image.
    **************************************************************************/
    public static Image readImageFromInputStream(InputStream in)
                        throws IOException,
                               NoImageFoundException
    {
        Image img = ImageIO.read(in);
        if (img == null)
        {
            throw new NoImageFoundException
                            ("The input stream contains no Image."); 
        }
        return img;
    }

    /**************************************************************************
    * Read an Image from the specified filename. 
    *@param  strFilename    The name of the file to read the Image from.
    *@return                The Image, or null if the file was empty.
    *@throws IOException    When an error occurs reading the Image.
    *@throws NoImageFoundException   
    *                       When the file contains no Image.
    **************************************************************************/
    public static Image readImageFromFile(String strFilename)
                        throws IOException,
                               NoImageFoundException
    {
        Image img = null;
        try
        {
            img = ImageIO.read(new File(strFilename));
        }
        catch (IOException e)
        {
            throw new IOException("Problem with file " + strFilename + ": " 
                                   + e.getMessage()); 
        }
        if (img == null)
        {
            throw new NoImageFoundException
                ("The file " + strFilename + " contains no Image."); 
        }
        return img;
    }

    /**************************************************************************
    * Write the specified Image to the specified OutputStream, converting it 
    * to a JPEG.
    *@param  img         The input Image
    *@param  out         The OutputStream
    *@throws IOException When an error occurs writing the Image.
    **************************************************************************/
    public static void writeImageToOutputStreamAsJPEG
                            (Image        img,
                             OutputStream out)
                        throws IOException
    {
        ImageIO.write(ImgUtil.toBufferedImage(img), "jpeg", out);
        out.flush();
    }

    /**************************************************************************
    * Write the specified Image to the specified OutputStream, converting it 
    * to a JPEG, and scaling it to the specified size.
    *@param  img         The input Image
    *@param  out         The OutputStream
    *@param  intWidth    The desired Image width, or negative number to 
    *                    maintain the original aspect ratio.
    *@param  intHeight   The desired Image height, or negative number to 
    *                    maintain the original aspect ratio.
    *@param  blnPreserveAspectRatio
    *                    If true, preserve the aspect ratio of the Image 
    *                    even if neither intWidth nor intHeight is negative.
    *                    Scale to the largest size that will fit within both
    *                    specified dimensions.    
    *@param  intMaxScalePercent
    *                    Max percent to scale the image, or negative number 
    *                    for no limit.  This is useful to avoid scaling images
    *                    too much and having them become grainy when scaling
    *                    to a fixed size.  It may be better to be less than the 
    *                    intended size than to be too grainy.  Common values
    *                    are:
    *                       intMAX_SCALE_CURRENT_SIZE (100%)
    *                       intMAX_SCALE_DOUBLE_CURRENT_SIZE (200%)
    *                       intMAX_SCALE_TRIPLE_CURRENT_SIZE (300%)
    *                       intMAX_SCALE_NO_LIMIT (-1)
    *@param  algorithm   Algorithm to use for scaling. 
    *@throws IOException When an error occurs reading or writing the Image.
    **************************************************************************/
    public static void scaleImageToOutputStreamAsJPEG
                            (Image          img,
                             OutputStream   out,
                             int            intWidth,
                             int            intHeight,
                             boolean        blnPreserveAspectRatio,
                             int            intMaxScalePercent,
                             ScaleAlgorithm algorithm)
                        throws IOException
    {
        if (blnPreserveAspectRatio && intWidth > 0 && intHeight > 0)
        {
            // Keep only the smaller of the 2 effective scale factors, 
            // discarding the scale factor for the other dimension by setting
            // that dimension to -1, which causes it to be ignored by
            // getScaledImage().   
            // For example, if it would have grown to be 5 times as tall and 
            // 3 times as wide, discard the height, and plan to scale the width 
            // (and height) by 3.  If it would have shrunk to be half as tall
            // but grown to be 3 times as wide, discard the width, and plan to
            // shrink the height (and width) by half.  The result will fit 
            // inside the specified width and height, but will preserve the
            // aspect ratio and perhaps not fill the entire space.
            float fltCurrentWidth  = new Float(img.getWidth(null)).floatValue();
            float fltCurrentHeight = new Float(img.getHeight(null)).floatValue();
            if (intWidth/fltCurrentWidth > intHeight/fltCurrentHeight)
            {
                // Ignore width.  Scale height to fit.
                intWidth = -1;
            }
            else
            {
                // Ignore height.  Scale width to fit.
                intHeight = -1;
            }
        }

        // If intMaxScalePercent is negative, there is no limit on the scale
        // factor or factors remaining.
        if (intMaxScalePercent >= 0)
        {
            // Regardless of whether we discarded the scale factor for a 
            // dimension above, limit the scale factor for each dimension 
            // to the specified max percent.
            // If both dimensions are still positive, we are scaling the two 
            // dimensions independently, and this limits them independently,
            // which is the correct thing to do.
            // If a dimension is already negative (because it came in that way, 
            // or because it was set to -1 above), the result will be negative 
            // because any negative number is smaller than the current positive 
            // value for that dimension, and that negative value is ignored by
            // getScaledImage(), so the other dimension governs the scaling,
            // which is also the correct thing to do.
            // It is valid to apply this part of the algorithm after the 
            // previous part because the first part discards the larger 
            // effective scale factor, and this second part reduces both 
            // effective scale factors by the same ratio, so the larger would
            // still have been larger.
            intWidth = Math.min(intWidth
                               ,img.getWidth(null) * intMaxScalePercent / 100
                               );
            intHeight = Math.min(intHeight
                                ,img.getHeight(null) * intMaxScalePercent / 100
                                );
        }
        img = ImgUtil.getScaledImage(img, intWidth, intHeight, algorithm); 
        writeImageToOutputStreamAsJPEG(img, out);
    }

    /**************************************************************************
    * Copy an image from the specified InputStream to the specified OutputStream,
    * converting it to a JPEG, and scaling it to the specified size.
    *@param  in          The InputStream
    *@param  out         The OutputStream
    *@param  intWidth    The desired Image width, or negative number to 
    *                    maintain the original aspect ratio.
    *@param  intHeight   The desired Image height, or negative number to 
    *                    maintain the original aspect ratio.
    *@param  blnPreserveAspectRatio
    *                    If true, preserve the aspect ratio of the Image 
    *                    even if neither intWidth nor intHeight is negative.
    *                    Scale to the largest size that will fit within both
    *                    specified dimensions.    
    *@param  intMaxScalePercent
    *                    Max percent to scale the image, or negative number 
    *                    for no limit.  
    *                    See {@link #scaleImageToOutputStreamAsJPEG(Image,
    *                    OutputStream, int, int, boolean, int, ScaleAlgorithm)}
    *@param  algorithm   Algorithm to use for scaling. 
    *@throws IOException When an error occurs reading or writing the image.
    *@throws NoImageFoundException 
    *                    When the InputStream contains no Image.
    **************************************************************************/
    public static void scaleImageToOutputStreamAsJPEG
                            (InputStream    in,
                             OutputStream   out,
                             int            intWidth,
                             int            intHeight,
                             boolean        blnPreserveAspectRatio,
                             int            intMaxScalePercent,
                             ScaleAlgorithm algorithm)
                        throws IOException,
                               NoImageFoundException
    {
        Image img = readImageFromInputStream(in);
        scaleImageToOutputStreamAsJPEG
            (img, out, intWidth, intHeight, blnPreserveAspectRatio, 
             intMaxScalePercent, algorithm);
    }

    /**************************************************************************
    * Copy an image from the specified filename to the specified OutputStream,
    * converting it to a JPEG, and scaling it to the specified size.
    *@param  strFilename The input filename
    *@param  out         The OutputStream
    *@param  intWidth    The desired Image width, or negative number to 
    *                    maintain the original aspect ratio.
    *@param  intHeight   The desired Image height, or negative number to 
    *                    maintain the original aspect ratio.
    *@param  blnPreserveAspectRatio
    *                    If true, preserve the aspect ratio of the Image 
    *                    even if neither intWidth nor intHeight is negative.
    *                    Scale to the largest size that will fit within both
    *                    specified dimensions.    
    *@param  intMaxScalePercent
    *                    Max percent to scale the image, or negative number 
    *                    for no limit.  
    *                    See {@link #scaleImageToOutputStreamAsJPEG(Image,
    *                    OutputStream, int, int, boolean, int, ScaleAlgorithm)}
    *@param  algorithm   Algorithm to use for scaling. 
    *@throws IOException When an error occurs reading or writing the image.
    *@throws NoImageFoundException 
    *                    When the file contains no Image.
    **************************************************************************/
    public static void scaleImageToOutputStreamAsJPEG
                            (String         strFilename,
                             OutputStream   out,
                             int            intWidth,
                             int            intHeight,
                             boolean        blnPreserveAspectRatio,
                             int            intMaxScalePercent,
                             ScaleAlgorithm algorithm)
                        throws IOException,
                               NoImageFoundException
    {
        Image img = readImageFromFile(strFilename);
        scaleImageToOutputStreamAsJPEG
            (img, out, intWidth, intHeight, blnPreserveAspectRatio, 
             intMaxScalePercent, algorithm);
        img = null;
    }

/*??  Commented out because it's not really needed.  Put back temporarily
      when I resume memory leak testing.
    /**************************************************************************
    * Copy an image from the specified filename to the specified OutputStream,
    * converting it to a JPEG, and scaling it to the specified size.
    *@param  strFilename The input filename
    *@param  out         The OutputStream
    *@param  intWidth    The desired Image width, or negative number to 
    *                    maintain the original aspect ratio.
    *@param  intHeight   The desired Image height, or negative number to 
    *                    maintain the original aspect ratio.
    *@param  blnPreserveAspectRatio
    *                    If true, preserve the aspect ratio of the Image 
    *                    even if neither intWidth nor intHeight is negative.
    *                    Scale to the largest size that will fit within both
    *                    specified dimensions.    
    *@param  intMaxScalePercent
    *                    Max percent to scale the image, or negative number 
    *                    for no limit.  
    *                    See {@link #scaleImageToOutputStreamAsJPEG(Image,
    *                    OutputStream, int, int, boolean, int, ScaleAlgorithm)}
    *@param  algorithm   Algorithm to use for scaling. 
    *@throws IOException When an error occurs reading or writing the image.
    *@throws NoImageFoundException 
    *                    When the file contains no Image.
    **************************************************************************/
/*??  Commented out because it's not really needed.  Put back temporarily
      when I resume memory leak testing.
    public static void scaleImageToOutputStreamAsJPEG
                            (Logger         logger,  //?? Temporary
                             String         strFilename,
                             OutputStream   out,
                             int            intWidth,
                             int            intHeight,
                             boolean        blnPreserveAspectRatio,
                             int            intMaxScalePercent,
                             ScaleAlgorithm algorithm)
                        throws IOException,
                               NoImageFoundException
    {
System.gc();
logger.log(4, "Reading image from file: " + strFilename);
        Image img = readImageFromFile(strFilename);
System.gc();
logger.log(4, "Done reading image: " + strFilename);
logger.log(4, "Scaling image from file: " + strFilename);
        scaleImageToOutputStreamAsJPEG
            (img, out, intWidth, intHeight, blnPreserveAspectRatio, 
             intMaxScalePercent, algorithm);
System.gc();
logger.log(4, "Done scaling image from file: " + strFilename);
        img = null;
    }
??*/

    /**************************************************************************
    * Write the specified Image to the specified OutputStream, converting it 
    * to a JPEG, and scaling it by the specified percent.
    *@param  img         The input Image
    *@param  out         The OutputStream
    *@param  intScalePercent    
    *                    The desired scale percent
    *@param  algorithm   Algorithm to use for scaling. 
    *@throws IOException When an error occurs reading or writing the Image.
    **************************************************************************/
    public static void scaleImageToOutputStreamAsJPEG
                            (Image          img,
                             OutputStream   out,
                             int            intScalePercent,
                             ScaleAlgorithm algorithm)
                        throws IOException
    {
        int intWidth = img.getWidth(null) * intScalePercent / 100; 
        int intHeight = img.getHeight(null) * intScalePercent / 100; 
        img = ImgUtil.getScaledImage(img, intWidth, intHeight, algorithm); 
        writeImageToOutputStreamAsJPEG(img, out);
    }

    /**************************************************************************
    * Copy an image from the specified InputStream to the specified OutputStream,
    * converting it to a JPEG, and scaling it by the specified percent.
    *@param  in          The InputStream
    *@param  out         The OutputStream
    *@param  intScalePercent    
    *                    The desired scale percent
    *@param  algorithm   Algorithm to use for scaling. 
    *@throws IOException When an error occurs reading or writing the Image.
    *@throws NoImageFoundException 
    *                    When the InputStream contains no Image.
    **************************************************************************/
    public static void scaleImageToOutputStreamAsJPEG
                            (InputStream    in,
                             OutputStream   out,
                             int            intScalePercent,
                             ScaleAlgorithm algorithm)
                        throws IOException,
                               NoImageFoundException
    {
        Image img = readImageFromInputStream(in);
        scaleImageToOutputStreamAsJPEG(img, out, intScalePercent, algorithm);
    }

    /**************************************************************************
    * Copy an image from the specified filename to the specified OutputStream,
    * converting it to a JPEG, and scaling it by the specified percent.
    *@param  strFilename The input filename
    *@param  out         The OutputStream
    *@param  intScalePercent    
    *                    The desired scale percent
    *@param  algorithm   Algorithm to use for scaling. 
    *@throws IOException When an error occurs reading or writing the Image.
    *@throws NoImageFoundException 
    *                    When the file contains no Image.
    **************************************************************************/
    public static void scaleImageToOutputStreamAsJPEG
                            (String         strFilename,
                             OutputStream   out,
                             int            intScalePercent,
                             ScaleAlgorithm algorithm)
                        throws IOException,
                               NoImageFoundException
    {
        Image img = readImageFromFile(strFilename);
        scaleImageToOutputStreamAsJPEG(img, out, intScalePercent, algorithm);
    }

    /**************************************************************************
    * Write the specified Image to the file with the specified name, converting 
    * it to a JPEG.
    *@param  img         The input Image
    *@param  strFilename The name of the file to write to.
    *@throws IOException When an error occurs writing the Image.
    **************************************************************************/
    public static void writeImageToFileAsJPEG
                            (Image  img,
                             String strFilename)
                        throws IOException
    {
        FileOutputStream out = new FileOutputStream(strFilename);
        writeImageToOutputStreamAsJPEG(img, out);
        out.close();
    }

    /**************************************************************************
    * Copy an image from the specified filename to the specified filename,
    * converting it to a JPEG, and scaling it to the specified size.
    *@param  strFilenameIn  The input filename
    *@param  strFilenameOut The name of the file to write to.
    *@param  intWidth       The desired Image width, or negative number to 
    *                       maintain the original aspect ratio.
    *@param  intHeight      The desired Image height, or negative number to 
    *                       maintain the original aspect ratio.
    *@param  blnPreserveAspectRatio
    *                    If true, preserve the aspect ratio of the Image 
    *                    even if neither intWidth nor intHeight is negative.
    *                    Scale to the largest size that will fit within both
    *                    specified dimensions.    
    *@param  intMaxScalePercent
    *                    Max percent to scale the image, or negative number 
    *                    for no limit.  
    *                    See {@link #scaleImageToOutputStreamAsJPEG(Image,
    *                    OutputStream, int, int, boolean, int, ScaleAlgorithm)}
    *@param  algorithm   Algorithm to use for scaling. 
    *@throws IOException    When an error occurs reading or writing the image.
    *@throws NoImageFoundException 
    *                    When the file contains no Image.
    **************************************************************************/
    public static void scaleImageToFileAsJPEG
                            (String         strFilenameIn,
                             String         strFilenameOut,
                             int            intWidth,
                             int            intHeight,
                             boolean        blnPreserveAspectRatio,
                             int            intMaxScalePercent,
                             ScaleAlgorithm algorithm)
                        throws IOException,
                               NoImageFoundException
    {
        Image img = readImageFromFile(strFilenameIn);
        FileOutputStream out = new FileOutputStream(strFilenameOut);
        scaleImageToOutputStreamAsJPEG
            (img, out, intWidth, intHeight, blnPreserveAspectRatio, 
             intMaxScalePercent, algorithm);
        out.close();
    }

    /**************************************************************************
    * Copy an image from the specified filename to the specified filename,
    * converting it to a JPEG, and scaling it by the specified percent.
    *@param  strFilenameIn  The input filename
    *@param  strFilenameOut The name of the file to write to.
    *@param  intScalePercent       
    *                       The desired scale percent
    *@param  algorithm      Algorithm to use for scaling. 
    *@throws IOException    When an error occurs reading or writing the image.
    *@throws NoImageFoundException 
    *                       When the file contains no Image.
    **************************************************************************/
    public static void scaleImageToFileAsJPEG
                            (String         strFilenameIn,
                             String         strFilenameOut,
                             int            intScalePercent,
                             ScaleAlgorithm algorithm)
                        throws IOException,
                               NoImageFoundException
    {
        Image img = readImageFromFile(strFilenameIn);
        FileOutputStream out = new FileOutputStream(strFilenameOut);
        scaleImageToOutputStreamAsJPEG(img, out, intScalePercent, algorithm);
        out.close();
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
        /**************************************************************************
        * Print the usage string.
        **************************************************************************/
        private static void printUsage()
        {
            System.err.println
                ("Usage: java -classpath bristle.jar" +
                 " com.bristle.javalib.awt.ImgUtil$Tester" +
                 " dirname [count]");
            System.err.println
                ("Where:");
            System.err.println
                ("   dirname = Name of directory of images to scale");
            System.err.println
                ("   count   = Number of times to scale each image");
        }

        private static void printMemoryUsage()
        {
            long   lngTotalMem = Runtime.getRuntime().totalMemory();
            String strUsedMem  = Long.toString
                                        (lngTotalMem
                                         - Runtime.getRuntime().freeMemory()
                                        );
            String strTotalMem = Long.toString(lngTotalMem);
            System.out.println("Memory used: " + strUsedMem 
                               + " total: " + strTotalMem);
        }

        /**********************************************************************
        * Main testing method.
        *@param  args       Array of command line argument strings
        **********************************************************************/
        public static void main(String[] args)
        {
            try
            {
                // Get command line params
                String strPath  = null;
                if (args.length > 0)
                {
                    strPath  = args[0];
                }
                else
                {
                    System.err.println("Dirname must be specified");
                    printUsage();
                    return;
                }
                    
                int    intCount = 1;
                if (args.length > 1)
                {
                    try
                    {
                        intCount = Integer.parseInt(args[1]);
                    }
                    catch(NumberFormatException exception)
                    {
                        System.err.println("Count must be an integer");
                        printUsage();
                    }
                }

                System.out.println("Begin tests...");
                System.out.println("Note: These are really just memory leak"
                		        + " and CPU time tests.");
                System.out.println("Other tests are done by the ScaleJPG app.");

                long lngStartTime = System.currentTimeMillis();
                printMemoryUsage();
                for (int i = 1; i <= intCount; i++)
                {
                    FileTreeIterator iter = new FileTreeIterator(strPath);
                    while (iter.hasNext())
                    {
                        File file = (File)iter.next();
                        String strIn = file.getName();
                        String strOut = strIn + ".out.jpeg";
                        if (   strIn.endsWith(".jpg")
                            || strIn.endsWith(".JPG")
                            || strIn.endsWith(".gif")
                            || strIn.endsWith(".GIF")
                           )
                        {
                            System.out.println("Scaling: " + strIn 
                                               + " --> " + strOut);
                            long lngImageStartTime = System.currentTimeMillis();
                            ImgUtil.scaleImageToFileAsJPEG
                                    (strPath + File.separator + strIn, 
                                     strOut, 
                                     600,
                                     450,
                                     true,
                                     ImgUtil.intMAX_SCALE_CURRENT_SIZE,
                                     ScaleAlgorithm.SMOOTH);
                            System.out.print
                                    (((System.currentTimeMillis() 
                                      - lngImageStartTime) 
                                      / 1000)
                                      + " secs  ");
                            printMemoryUsage();
                        }
                    }
                }
                System.out.print  ("Garbage collection...");
                System.gc();
                System.out.println("Done.");
                printMemoryUsage();
                System.out.println
                        ("Elapsed time: " 
                         + ((System.currentTimeMillis() - lngStartTime) / 1000)
                         + " secs");

                System.out.println ("");
                System.out.println ("...End tests.");
            }
            catch (Throwable e)
            {
                System.err.println("Error in main(): ");
                e.printStackTrace();
            }
        }
    }
}
