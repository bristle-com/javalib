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

package com.bristle.javalib.util;

import java.text.DecimalFormat;
import java.util.Random;

// MathUtil
/******************************************************************************
* This class contains utility math routines.
*<pre>
*<b>Usage:</b>
*   - Typical scenarios for using this class are:
*     - Get a random int between 100 and 200 (inclusive).
*       int intRandom = MathUtil.getRandomInt(100, 200);
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
public class MathUtil
{
    //--
    //-- Class variables
    //--
    private static Random random = new Random(); 
    
    //--
    //-- Instance variables to support public properties
    //--

    //--
    //-- Internal instance variables
    //--
    
    /**************************************************************************
    * Return a random int between the specified min and max (inclusive). 
    *@param  intMin     Min value.
    *@param  intMax     Max value.
    *@return            Random int.
    **************************************************************************/
    public static int getRandomInt(int intMin, int intMax)
    {
        // Get a random number between 0 (inclusive) and max-min+1 (exclusive),
        // which is the same as between 0 (inclusive) and max-min (inclusive),
        // and add min to get a number between min and max (both inclusive).
        return random.nextInt(intMax - intMin + 1) + intMin;
    }

    /**************************************************************************
    * Round the specified number to the specified number of decimal digits 
    * (digits following the decimal point) and then trim any trailing zeros, 
    * returning a string containing the specified value with perhaps less 
    * precision.
    *
    *@param dblVal                  Number to trim
    *@param intMaxDecimalDigits     Max number of non-zero decimal digits to 
    *                               preserve.
    ***************************************************************************/
    public static String trimFloat(double dblVal, int intMaxDecimalDigits)
    {
        // Format as string, rounded to max number of decimal digits, and 
        // with no commas at thousands.
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(intMaxDecimalDigits);
        format.setGroupingUsed(false);
        String strVal = format.format(dblVal);
    
        // Trim trailing zeros if any as decimal digits, then trailing decimal 
        // point if any.
        if (strVal.indexOf(".") >= 0)
        {
            strVal = StrUtil.rtrim(strVal, "0");
            strVal = StrUtil.rtrim(strVal, ".");
        }
        
        // If all digits trimmed, keep at least one zero.
        if (strVal.equals("") || strVal.equals("-"))
        {
            strVal = "0";
        }
        
        return strVal;
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

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- getRandomInt(100,999)");
                System.out.println ("--");
                //-------------------------------------------------------------
                System.out.println (getRandomInt(100, 999)); 
                System.out.println (getRandomInt(100, 999)); 
                System.out.println (getRandomInt(100, 999)); 
                System.out.println (getRandomInt(100, 999)); 
                System.out.println (getRandomInt(100, 999)); 
                System.out.println (getRandomInt(100, 999)); 

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- getRandomInt(1000,9999)");
                System.out.println ("--");
                //-------------------------------------------------------------
                System.out.println (getRandomInt(1000, 9999)); 
                System.out.println (getRandomInt(1000, 9999)); 
                System.out.println (getRandomInt(1000, 9999)); 
                System.out.println (getRandomInt(1000, 9999)); 

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- getRandomInt(100,999) again with");
                System.out.println ("-- different numbers, unaffected by the");
                System.out.println ("-- larger range");
                System.out.println ("--");
                //-------------------------------------------------------------
                System.out.println (getRandomInt(100, 999)); 
                System.out.println (getRandomInt(100, 999)); 

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- trimFloat()");
                System.out.println ("--");
                //-------------------------------------------------------------
                System.out.println (trimFloat(0,        -1)); 
                System.out.println (trimFloat(1,        -1)); 
                System.out.println (trimFloat(123,      -1));
                System.out.println (trimFloat(123.987,  -1));
                System.out.println (trimFloat(1000,     -1));
                System.out.println (trimFloat(-0,       -1)); 
                System.out.println (trimFloat(-1,       -1)); 
                System.out.println (trimFloat(-123,     -1));
                System.out.println (trimFloat(-123.987, -1));
                System.out.println (trimFloat(-1000,    -1));
                
                System.out.println (trimFloat(0,        0)); 
                System.out.println (trimFloat(1,        0)); 
                System.out.println (trimFloat(123,      0));
                System.out.println (trimFloat(123.987,  0));
                System.out.println (trimFloat(1000,     0));
                System.out.println (trimFloat(-0,       0)); 
                System.out.println (trimFloat(-1,       0)); 
                System.out.println (trimFloat(-123,     0));
                System.out.println (trimFloat(-123.987, 0));
                System.out.println (trimFloat(-1000,    0));
                
                System.out.println (trimFloat(0.123,    0)); 
                System.out.println (trimFloat(1.123,    0)); 
                System.out.println (trimFloat(123.123,  0)); 
                System.out.println (trimFloat(123.987,  0));
                System.out.println (trimFloat(1000,     0));
                System.out.println (trimFloat(-0.123,   0)); 
                System.out.println (trimFloat(-1.123,   0)); 
                System.out.println (trimFloat(-123.123, 0));
                System.out.println (trimFloat(-123.987, 0));
                System.out.println (trimFloat(-1000,    0));
                
                System.out.println (trimFloat(0.123,    1)); 
                System.out.println (trimFloat(1.123,    1)); 
                System.out.println (trimFloat(123.123,  1)); 
                System.out.println (trimFloat(123.987,  1));
                System.out.println (trimFloat(1000,     1));
                System.out.println (trimFloat(-0.123,   1)); 
                System.out.println (trimFloat(-1.123,   1)); 
                System.out.println (trimFloat(-123.123, 1));
                System.out.println (trimFloat(-123.987, 1));
                System.out.println (trimFloat(-1000,    1));

                System.out.println (trimFloat(0.123,    2)); 
                System.out.println (trimFloat(1.123,    2)); 
                System.out.println (trimFloat(123.123,  2)); 
                System.out.println (trimFloat(123.987,  2));
                System.out.println (trimFloat(1000,     2));
                System.out.println (trimFloat(-0.123,   2)); 
                System.out.println (trimFloat(-1.123,   2)); 
                System.out.println (trimFloat(-123.123, 2));
                System.out.println (trimFloat(-123.987, 2));
                System.out.println (trimFloat(-1000,    2));

                System.out.println (trimFloat(0.123,    3)); 
                System.out.println (trimFloat(1.123,    3)); 
                System.out.println (trimFloat(123.123,  3)); 
                System.out.println (trimFloat(123.987,  3));
                System.out.println (trimFloat(1000,     3));
                System.out.println (trimFloat(-0.123,   3)); 
                System.out.println (trimFloat(-1.123,   3)); 
                System.out.println (trimFloat(-123.123, 3));
                System.out.println (trimFloat(-123.987, 3));
                System.out.println (trimFloat(-1000,    3));

                System.out.println (trimFloat(0.123,    4)); 
                System.out.println (trimFloat(1.123,    4)); 
                System.out.println (trimFloat(123.123,  4)); 
                System.out.println (trimFloat(123.987,  4));
                System.out.println (trimFloat(1000,     4));
                System.out.println (trimFloat(-0.123,   4)); 
                System.out.println (trimFloat(-1.123,   4)); 
                System.out.println (trimFloat(-123.123, 4));
                System.out.println (trimFloat(-123.987, 4));
                System.out.println (trimFloat(-1000,    4));

                System.out.println (trimFloat(0.123,    5)); 
                System.out.println (trimFloat(1.123,    5)); 
                System.out.println (trimFloat(123.123,  5)); 
                System.out.println (trimFloat(123.987,  5));
                System.out.println (trimFloat(1000,     5));
                System.out.println (trimFloat(-0.123,   5)); 
                System.out.println (trimFloat(-1.123,   5)); 
                System.out.println (trimFloat(-123.123, 5));
                System.out.println (trimFloat(-123.987, 5));
                System.out.println (trimFloat(-1000,    5));

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
