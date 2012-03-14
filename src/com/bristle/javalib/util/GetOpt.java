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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// GetOpt
/******************************************************************************
* This class contains utility routines for extracting positional arguments and
* named options from any "option source" (the command line or any specified 
* array, List, Iterator, Enumeration, Map, etc.).  It handles required values,
* default values, conversion of values to various data types (String, Boolean, 
* Integer, Float, etc.), checking values against constraints (min, max, etc.), 
* and other argument and option processing features.   
* 
*<pre>
*<b>Usage:</b>
*   - A typical scenario for extracting positional arguments and named options 
*     from the command line is:
*       public static void main(String[] args)
*       { 
*           GetOpt getopt = new GetOpt(args);
*           Integer intWidth  = GetOpt.getOptionInteger("width");
*           Integer intHeight = GetOpt.getOptionInteger("height");
*           String strFilename = GetOpt.getNextArgString();
*       } 
*
*   - To extract positional arguments and named options from a List:
*       GetOpt getopt = new GetOpt(list);
*       Integer intWidth  = GetOpt.getOptionInteger("width");
*       Integer intHeight = GetOpt.getOptionInteger("height");
*       String strFilename = GetOpt.getNextArgString();
*
*   - To extract values from a Map (for example, the parameter Map of a 
*     ServletRequest) via key value:
*       GetOpt getopt = new GetOpt(request.getParameterMap());
*       Integer intWidth  = GetOpt.getOptionInteger("width");
*       Integer intHeight = GetOpt.getOptionInteger("height");
*     Note:  The getNextArg...() methods don't apply when the option source 
*            is a Map, rather than an ordered array or List.
*
*   - To operate on an Iterator:
*       GetOpt getopt = new GetOpt(ListUtil.toList(iterator));
*
*   - To operate on an Enumeration:
*       GetOpt getopt = new GetOpt(ListUtil.toList(enum));
*
*   - To operate on parallel arrays of option names and values, or parallel
*     Lists, parallel Iterators, or parallel Enumerations, use one of:
*       GetOpt getopt = new GetOpt(MapUtil.createMap(arr1, arr2));
*       GetOpt getopt = new GetOpt(MapUtil.createMap(list1, list2));
*       GetOpt getopt = new GetOpt(MapUtil.createMap(iter1, iter2));
*       GetOpt getopt = new GetOpt(MapUtil.createMap(enum1, enum2));
*
*   - See the source code of the inner Tester class for more examples.
*  
*<b>Assumptions:</b>
*<b>Effects:</b>
*       - The various get...() methods return the requested value if present
*         in the option source, and delete it from the internally stored copy 
*         of the option source, similar to way the "shift" operation in Unix 
*         shell scripts, perl scripts, and Windows batch files delete values
*         from the parameter list specified on the command line.    
*         Therefore, you should make all calls to getOption...() first, to 
*         extract the options and then make all calls to getNextArg...() in 
*         the expected order of the remaining positional arguments.
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class GetOpt
{
    //--
    //-- Class variables
    //--

    //--
    //-- Instance variables to support public properties
    //--

    /**************************************************************************
    * The remaining portions of the option source when it was specified as 
    * an array or List.  
    * Note: Only one of m_list or m_map is valued.  
    **************************************************************************/
    private List m_list = null;

    /**************************************************************************
    * The remaining portions of the option source when it was specified as 
    * a Map.  
    * Note: Only one of m_list or m_map is valued.  
    **************************************************************************/
    private Map m_map = null;

    //--
    //-- Internal instance variables
    //--

    /**************************************************************************
    * Constructor.
    *@param args        Option source.
    **************************************************************************/
    public GetOpt(String[] args)
    {
        // Note: Need an ArrayList, not just a List so that calls to remove() 
        //       don't throw java.lang.UnsupportedOperationException. 
        m_list = new ArrayList(ListUtil.toList(args));
    }

    /**************************************************************************
    * Constructor.
    *@param list        Option source.
    **************************************************************************/
    public GetOpt(List list)
    {
        // Note: Need an ArrayList, not just a List so that calls to remove() 
        //       don't throw java.lang.UnsupportedOperationException. 
        m_list = new ArrayList(list);
    }

    /**************************************************************************
    * Constructor.
    *@param map         Option source.
    **************************************************************************/
    public GetOpt(Map map)
    {
        m_map = map;
    }

    /**************************************************************************
    * This class represents a value that specifies whether arguments and 
    * options are required.  See the BooleanWrapper documentation for why 
    * this class is used instead of simply boolean or Boolean.    
    **************************************************************************/
    public static class Required extends BooleanWrapper
    {
        public Required(boolean blnValue) { super(blnValue); }
        public Required(Boolean blnValue) { super(blnValue); }
    }

    /**************************************************************************
    * This class represents a value that specifies whether options are 
    * required to have values.  See the BooleanWrapper documentation for why 
    * this class is used instead of simply boolean or Boolean.  
    **************************************************************************/
    public static class ValueRequired extends BooleanWrapper
    {
        public ValueRequired(boolean blnValue) { super(blnValue); }
        public ValueRequired(Boolean blnValue) { super(blnValue); }
    }

    /**************************************************************************
    * This class represents a default value for a String.
    * It is typically used as an argument to the other methods in the 
    * enclosing class.  
    **************************************************************************/
    public static class DefaultString extends StringWrapper
    {
        public DefaultString(String strValue) { super(strValue); }
        public final static DefaultString NONE = null;
    }

    /**************************************************************************
    * This class represents a default value for an Boolean.
    * It is typically used as an argument to the other methods in the 
    * enclosing class.  
    **************************************************************************/
    public static class DefaultBoolean extends BooleanWrapper
    {
        public DefaultBoolean(boolean blnValue) { super(blnValue); }
        public DefaultBoolean(Boolean blnValue) { super(blnValue); }
        public final static DefaultBoolean NONE = null;
    }

    /**************************************************************************
    * This class represents a default value for an Integer.
    * It is typically used as an argument to the other methods in the 
    * enclosing class.  
    **************************************************************************/
    public static class DefaultInteger extends IntegerWrapper
    {
        public DefaultInteger(int intValue) { super(intValue); }
        public DefaultInteger(Integer intValue) { super(intValue); }
        public final static DefaultInteger NONE = null;
    }

    /**************************************************************************
    * This class represents a minimum allowable value for an Integer.
    * It is typically used as an argument to the other methods in the 
    * enclosing class.  
    **************************************************************************/
    public static class MinInteger extends IntegerWrapper
    {
        public MinInteger(int intValue) { super(intValue); }
        public MinInteger(Integer intValue) { super(intValue); }
        public final static MinInteger NONE = null;
    }

    /**************************************************************************
    * This class represents a maximum allowable value for an Integer.
    * It is typically used as an argument to the other methods in the 
    * enclosing class.  
    **************************************************************************/
    public static class MaxInteger extends IntegerWrapper
    {
        public MaxInteger(int intValue) { super(intValue); }
        public MaxInteger(Integer intValue) { super(intValue); }
        public final static MaxInteger NONE = null;
    }

    /**************************************************************************
    * This exception is thrown when a required argument is missing or null.
    * Note:  When the option source is a Map, which is an unordered set of 
    *        named values, all such name/value pairs are treated as options,
    *        and the concept of positional arguments does not apply, so this
    *        exception is thrown on any call to any of the getNextArg...()
    *        methods.
    **************************************************************************/
    public static class MissingArgException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public MissingArgException(String msg) { super(msg); }
    }

    /**************************************************************************
    * This exception is thrown when a required option is missing. 
    **************************************************************************/
    public static class MissingOptionException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public MissingOptionException(String msg) { super(msg); }
    }

    /**************************************************************************
    * This exception is thrown when an option with a required value has a 
    * missing or null value. 
    **************************************************************************/
    public static class MissingValueException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public MissingValueException(String msg) { super(msg); }
    }

    /**************************************************************************
    * This exception is thrown when a value required to be an integer was not. 
    **************************************************************************/
    public static class NotAnIntegerException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public NotAnIntegerException(String msg) { super(msg); }
    }

    /**************************************************************************
    * This exception is thrown when a numeric value is less than the 
    * specified minimum. 
    **************************************************************************/
    public static class LessThanMinException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public LessThanMinException(String msg) { super(msg); }
    }

    /**************************************************************************
    * This exception is thrown when a numeric value is more than the 
    * specified maximum. 
    **************************************************************************/
    public static class MoreThanMaxException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public MoreThanMaxException(String msg) { super(msg); }
    }
    
    /**************************************************************************
    * Return the next argument from the previously specified option source, 
    * defaulting to the specified value. 
    * Note:  This removes the argument from the internally stored option 
    *        source, so the next call returns a different argument. 
    *@param  required       Flag indicating whether the argument is required to 
    *                       be present and not null in the option source. 
    *@param  strDefault     Default value, if argument has missing or null 
    *                       value in the option source. 
    *@return                Argument value.
    *@throws MissingArgException
    *                       When argument is required but missing or null.
    **************************************************************************/
    public String getNextArgString
                            (Required      required, 
                             DefaultString strDefault)
                throws MissingArgException
    {
        if (m_map != null)
        {
            throw new MissingArgException
                    ("Positional arguments are not supported when the option" +
                     " source is a Map");
        }
        String strValue = null;
        if (m_list != null && m_list.size() > 0)
        {
            strValue = (String)m_list.get(0);
            m_list.remove(0);
        }
        if (strValue == null)
        {
            if (required != null && 
                required.getBoolean() != null &&
                required.getBoolean().equals(Boolean.TRUE))
            {
                throw new MissingArgException
                        ("Argument is required");
            }
            else
            {
                return DefaultString.castToString(strDefault);
            }
        }
        else
        {
            return strValue;
        }
    }
        
    /**************************************************************************
    * Return the next argument from the previously specified option source, 
    * defaulting to null. 
    * Note:  This removes the argument from the internally stored option 
    *        source, so the next call returns a different argument. 
    *@param  required       Flag indicating whether the argument is required to 
    *                       be present and not null in the option source. 
    *@return                Argument value.
    *@throws MissingArgException
    *                       When argument is required but missing or null.
    **************************************************************************/
    public String getNextArgString(Required required) 
               throws MissingArgException
    {
        return getNextArgString(required, DefaultString.NONE);
    }
        
    /**************************************************************************
    * Return the next argument from the previously specified option source, 
    * defaulting to null. 
    * Note:  This removes the argument from the internally stored option 
    *        source, so the next call returns a different argument. 
    *@return                Argument value.
    **************************************************************************/
    public String getNextArgString() 
    {
        try
        {
            return getNextArgString(new Required(false));
        }
        catch (MissingArgException e)
        {
            // Can't happen because we didn't require the argument.
            return null;
        }
    }

    /**************************************************************************
    * Return the next argument from the previously specified option source, 
    * defaulting to the specified value. 
    * Note:  This removes the argument from the internally stored option 
    *        source, so the next call returns a different argument. 
    *@param  required       Flag indicating whether the argument is required to 
    *                       be present and not null in the option source. 
    *@param  intDefault     Default value, if argument has missing or null 
    *                       value in the option source. 
    *@param  intMin         Minimum acceptable value, or null for unrestricted.
    *                       Note:  Only applies when the argument value 
    *                              (whether from the option source or from the 
    *                              default) is not null.  
    *@param  intMax         Maximum acceptable value, or null for unrestricted. 
    *                       Note:  Only applies when the argument value 
    *                              (whether from the option source or from the 
    *                              default) is not null.  
    *@return                Argument value.
    *@throws MissingArgException
    *                       When argument is required but missing or null.
    *@throws NotAnIntegerException  
    *                       When argument is not an integer.
    *@throws LessThanMinException  
    *                       When argument is less than the specified min.
    *@throws MoreThanMaxException  
    *                       When argument is more than the specified max.
    **************************************************************************/
    public Integer getNextArgInteger
                            (Required       required, 
                             DefaultInteger intDefault,
                             MinInteger     intMin,
                             MaxInteger     intMax)
                throws MissingArgException,
                       NotAnIntegerException,
                       LessThanMinException,  
                       MoreThanMaxException  
    {
        String strValue = getNextArgString
                                (required, 
                                 new DefaultString
                                        (DefaultInteger.castToString
                                            (intDefault)));
        Integer intValue = null; 
        try
        {
            intValue = (strValue == null) ? null : new Integer(strValue); 
        }
        catch (NumberFormatException e)
        {
            throw new NotAnIntegerException
                        ("Argument '" + strValue + "' must be an integer value");
        }
        if (intMin != null && 
            intValue != null && 
            intValue.intValue() < intMin.getInt())
        {
            throw new LessThanMinException
                        ("Argument '" + strValue + "' must not be less than " 
                         + intMin.getInt());
        }
        if (intMax != null && 
            intValue != null &&
            intValue.intValue() > intMax.getInt())
        {
            throw new MoreThanMaxException
                        ("Argument '" + strValue + "' must not be more than " 
                         + intMax.getInt());
        }
        return intValue; 
    }
        
    /**************************************************************************
    * Return the next argument from the previously specified option source, 
    * defaulting to the specified value. 
    * Note:  This removes the argument from the internally stored option 
    *        source, so the next call returns a different argument. 
    *@param  required       Flag indicating whether the argument is required to 
    *                       be present and not null in the option source. 
    *@param  intDefault     Default value, if argument has missing or null 
    *                       value in the option source. 
    *@return                Argument value.
    *@throws MissingArgException
    *                       When argument is required but missing or null.
    *@throws NotAnIntegerException  
    *                       When argument is not an integer.
    **************************************************************************/
    public Integer getNextArgInteger
                            (Required       required, 
                             DefaultInteger intDefault)
                throws MissingArgException,
                       NotAnIntegerException
    {
        try
        {
            return getNextArgInteger
                                (required, 
                                 intDefault,
                                 MinInteger.NONE,
                                 MaxInteger.NONE);
        }
        catch (LessThanMinException e)
        {
            // Can't happen because we passed no min.
            return null;
        }
        catch (MoreThanMaxException e)
        {
            // Can't happen because we passed no max.
            return null;
        }
    }
        
    /**************************************************************************
    * Return the next argument from the previously specified option source, 
    * defaulting to null. 
    * Note:  This removes the argument from the internally stored option 
    *        source, so the next call returns a different argument. 
    *@param  required       Flag indicating whether the argument is required to 
    *                       be present and not null in the option source. 
    *@return                Argument value.
    *@throws MissingArgException
    *                       When argument is required but missing or null.
    *@throws NotAnIntegerException  
    *                       When argument is not an integer.
    **************************************************************************/
    public Integer getNextArgInteger(Required required) 
                throws MissingArgException,
                       NotAnIntegerException
    {
        return getNextArgInteger(required, DefaultInteger.NONE);
    }
        
    /**************************************************************************
    * Return the next argument from the previously specified option source, 
    * defaulting to null. 
    * Note:  This removes the argument from the internally stored option 
    *        source, so the next call returns a different argument. 
    *@return                Argument value.
    *@throws NotAnIntegerException  
    *                       When argument is not an integer.
    **************************************************************************/
    public Integer getNextArgInteger() 
                throws NotAnIntegerException
    {
        try
        {
            return getNextArgInteger(new Required(false));
        }
        catch (MissingArgException e)
        {
            // Can't happen because we didn't require the argument.
            return null;
        }
    }

    /**************************************************************************
    * Get the zero-based index of the specified option in the previously 
    * specified option source, or -1 if not found.
    *@param  strName        Name of option (e.g., "width" for -width option).
    *@return                Option index
    **************************************************************************/
    public int findOptionInList(String strName) 
    {
        if (m_list != null)
        {
            for (int i = 0; i < m_list.size(); i++)
            {
                if (m_list.get(i).equals("-" + strName))
                {
                    return i;
                }
            }
        }
        return -1;
    }
        
    /**************************************************************************
    * Return Boolean.TRUE if the specified option is present on the 
    * previously specified option source, blnDefault otherwise.
    * Note:  When the option source is an Array or List, this removes the 
    *        option from the internally stored option source, so subsequent
    *        calls would not find it present again.  
    *@param  strName        Name of option (e.g., "width" for -width option).
    *@param  optionRequired Flag indicating whether the option is required to 
    *                       be present in the option source. 
    *@param  blnDefault     Default value, if option not found in the option 
    *                       source. 
    *@return                Boolean indicating presence of option.
    *@throws MissingOptionException
    *                       When option is required but missing.
    **************************************************************************/
    public Boolean getOptionPresent
                            (String         strName, 
                             Required       optionRequired, 
                             DefaultBoolean blnDefault)
                throws MissingOptionException
    {
        int i = findOptionInList(strName);
        if (i == -1 && 
            (m_map == null || !m_map.containsKey(strName)))
        {
            // Option is not present in m_list or m_map.
            if (optionRequired != null && 
                optionRequired.getBoolean() != null &&
                optionRequired.getBoolean().equals(Boolean.TRUE))
            {
                throw new MissingOptionException
                        ("'" + strName + "' option is required");
            }
            else
            {
                return (blnDefault == null) ? null : blnDefault.getBoolean();
            }
        }

        // Option is present in m_list or m_map.
        if (i != -1)
        {
            m_list.remove(i);
        }
        return Boolean.TRUE;
    }
        
    /**************************************************************************
    * Return Boolean.TRUE if the specified option is present on the 
    * previously specified option source, null otherwise.
    * Note:  When the option source is an Array or List, this removes the 
    *        option from the internally stored option source, so subsequent
    *        calls would not find it present again.  
    *@param  strName        Name of option (e.g., "width" for -width option).
    *@param  optionRequired Flag indicating whether the option is required to 
    *                       be present in the option source. 
    *@return                Boolean indicating presence of option.
    *@throws MissingOptionException
    *                       When option is required but missing.
    **************************************************************************/
    public Boolean getOptionPresent
                            (String   strName, 
                             Required optionRequired)
                throws MissingOptionException
    {
        return getOptionPresent(strName, optionRequired, DefaultBoolean.NONE);
    }
        
    /**************************************************************************
    * Return Boolean.TRUE if the specified option is present on the 
    * previously specified option source, null otherwise.
    * Note:  When the option source is an Array or List, this removes the 
    *        option from the internally stored option source, so subsequent
    *        calls would not find it present again.  
    *@param  strName        Name of option (e.g., "width" for -width option).
    *@return                Boolean indicating presence of option.
    **************************************************************************/
    public Boolean getOptionPresent(String strName) 
    {
        try
        {
            return getOptionPresent(strName, new Required(false));
        }
        catch (MissingOptionException e)
        {
            // Can't happen because we didn't require the option.
            return null;
        }
    }
        
    /**************************************************************************
    * Return a String option from the previously specified option source.
    * Note:  When the option source is an Array or List, this removes the 
    *        option from the internally stored option source, so subsequent
    *        calls would not find it present again.  
    *@param  strName        Name of option (e.g., "width" for -width option).
    *@param  optionRequired Flag indicating whether the option is required to 
    *                       be present in the option source. 
    *@param  valueRequired  Flag indicating whether the option is required to
    *                       have a non-null value when it occurs in the option 
    *                       source.
    *@param  strMissingOptionDefault
    *                       Default value, if option not found in the option 
    *                       source. 
    *@param  strMissingValueDefault
    *                       Default value, if option found in the option source 
    *                       with missing or null value. 
    *@return                Option value
    *@throws MissingOptionException 
    *                       When option is missing from option source.
    *@throws MissingValueException  
    *                       When option has missing or null value.
    **************************************************************************/
    public String getOptionString
                            (String        strName, 
                             Required      optionRequired, 
                             ValueRequired valueRequired, 
                             DefaultString strMissingOptionDefault,
                             DefaultString strMissingValueDefault)
                throws MissingOptionException,
                       MissingValueException
    {
        // Find the option in the option source.
        int i = findOptionInList(strName);
        if (i == -1 && 
            (m_map == null || !m_map.containsKey(strName)))
        {
            // Option is not present in m_list or m_map.
            if (optionRequired != null && 
                optionRequired.getBoolean() != null &&
                optionRequired.getBoolean().equals(Boolean.TRUE))
            {
                throw new MissingOptionException
                        ("'" + strName + "' option is required");
            }
            else
            {
                return DefaultString.castToString(strMissingOptionDefault);
            }
        }

        // Option is present in m_list or m_map.
        String strValue = null;
        if (i != -1)
        {
            // Found the option in m_list.  Remove it and its value.
            m_list.remove(i);
            if (m_list.size() > i)
            {
                strValue = (String)m_list.get(i);
                m_list.remove(i);
            }
        }
        else
        {
            // Found the option in m_map.
            strValue = (String)m_map.get(strName);
        }
        if (strValue == null)
        {
            if (valueRequired != null && 
                valueRequired.getBoolean() != null &&
                valueRequired.getBoolean().equals(Boolean.TRUE))
            {
                throw new MissingValueException
                        ("'" + strName + "' option requires a value");
            }
            else
            {
                return DefaultString.castToString(strMissingValueDefault);
            }
        }
        return strValue;
    }
        
    /**************************************************************************
    * Return a String option from the previously specified option source.
    * Note:  When the option source is an Array or List, this removes the 
    *        option from the internally stored option source, so subsequent
    *        calls would not find it present again.  
    *@param  strName        Name of option (e.g., "width" for -width option).
    *@param  optionRequired Flag indicating whether the option is required to 
    *                       be present in the option source. 
    *@param  valueRequired  Flag indicating whether the option is required to
    *                       have a non-null value when it occurs in the option 
    *                       source.
    *@param  strDefault     Default value, if option not found in the option 
    *                       source or option found in the option source with 
    *                       missing or null value. 
    *@return                Option value
    *@throws MissingOptionException 
    *                       When option is missing from option source.
    *@throws MissingValueException  
    *                       When option has missing or null value.
    **************************************************************************/
    public String getOptionString
                            (String         strName, 
                             Required       optionRequired, 
                             ValueRequired  valueRequired, 
                             DefaultString  strDefault)
                throws MissingOptionException,
                       MissingValueException
    {
        return getOptionString
                        (strName, 
                         optionRequired,
                         valueRequired,
                         strDefault,
                         strDefault);
    }

    /**************************************************************************
    * Return a String option from the previously specified option source, 
    * defaulting to null. 
    * Note:  When the option source is an Array or List, this removes the 
    *        option from the internally stored option source, so subsequent
    *        calls would not find it present again.  
    *@param  strName        Name of option (e.g., "width" for -width option).
    *@param  optionRequired Flag indicating whether the option is required to 
    *                       be present in the option source. 
    *@param  valueRequired  Flag indicating whether the option is required to
    *                       have a non-null value when it occurs in the option 
    *                       source.
    *@return                Option value
    *@throws MissingOptionException 
    *                       When option is missing from option source.
    *@throws MissingValueException  
    *                       When option has missing or null value.
    **************************************************************************/
    public String getOptionString
                            (String         strName, 
                             Required       optionRequired, 
                             ValueRequired  valueRequired)
                throws MissingOptionException,
                       MissingValueException
    {
        return getOptionString
                        (strName, 
                         optionRequired,
                         valueRequired,
                         DefaultString.NONE);
    }

    /**************************************************************************
    * Return a String option from the previously specified option source, 
    * defaulting to null. 
    * Note:  When the option source is an Array or List, this removes the 
    *        option from the internally stored option source, so subsequent
    *        calls would not find it present again.  
    *@param  strName        Name of option (e.g., "width" for -width option).
    *@return                Option value
    **************************************************************************/
    public String getOptionString(String strName) 
    {
        try
        {
            return getOptionString
                            (strName, 
                             new Required(false),
                             new ValueRequired(false));
        }
        catch (MissingOptionException e)
        {
            // Can't happen because we didn't require the option.
            return null;
        }
        catch (MissingValueException e)
        {
            // Can't happen because we didn't require the option value. 
            return null;
        }
    }

    /**************************************************************************
    * Return an Integer option from the previously specified option source.
    * Note:  When the option source is an Array or List, this removes the 
    *        option from the internally stored option source, so subsequent
    *        calls would not find it present again.  
    *@param  strName        Name of option (e.g., "width" for -width option).
    *@param  optionRequired Flag indicating whether the option is required to 
    *                       be present in the option source. 
    *@param  valueRequired  Flag indicating whether the option is required to
    *                       have a non-null value when it occurs in the option 
    *                       source.
    *@param  intMissingOptionDefault
    *                       Default value, if option not found in the option 
    *                       source.
    *@param  intMissingValueDefault
    *                       Default value, if option found in the option source 
    *                       with missing or null value. 
    *@param  intMin         Minimum acceptable value, or null for unrestricted.
    *                       Note:  Only applies when the option value 
    *                              (whether from the option source or from the 
    *                              default) is not null.  
    *@param  intMax         Maximum acceptable value, or null for unrestricted. 
    *                       Note:  Only applies when the option value 
    *                              (whether from the option source or from the 
    *                              default) is not null.  
    *@return                Option value
    *@throws MissingOptionException 
    *                       When option is missing from option source.
    *@throws MissingValueException  
    *                       When option has missing or null value.
    *@throws NotAnIntegerException  
    *                       When option value is not an integer.
    *@throws LessThanMinException  
    *                       When option value is less than the specified min.
    *@throws MoreThanMaxException  
    *                       When option value is more than the specified max.
    **************************************************************************/
    public Integer getOptionInteger
                            (String         strName, 
                             Required       optionRequired, 
                             ValueRequired  valueRequired, 
                             DefaultInteger intMissingOptionDefault,
                             DefaultInteger intMissingValueDefault,
                             MinInteger     intMin,
                             MaxInteger     intMax)
                throws MissingOptionException,
                       MissingValueException,
                       NotAnIntegerException,
                       LessThanMinException,  
                       MoreThanMaxException
    {
        String strValue = getOptionString
                                (strName, 
                                 optionRequired,
                                 valueRequired,
                                 new DefaultString
                                        (DefaultInteger.castToString
                                            (intMissingOptionDefault)),
                                 new DefaultString
                                        (DefaultInteger.castToString
                                            (intMissingValueDefault)));
        Integer intValue = null; 
        try
        {
            intValue = (strValue == null) ? null : new Integer(strValue); 
        }
        catch (NumberFormatException e)
        {
            throw new NotAnIntegerException
                        ("'" + strName + "' option value '" + strValue 
                         + "' must be an integer");
        }
        if (intMin != null && 
            intValue != null &&
            intValue.intValue() < intMin.getInt())
        {
            throw new LessThanMinException
                        ("'" + strName + "' option value '" + strValue 
                         + "' must not be less than " + intMin.getInt());
        }
        if (intMax != null && 
            intValue != null && 
            intValue.intValue() > intMax.getInt())
        {
            throw new MoreThanMaxException
                        ("'" + strName + "' option value '" + strValue 
                         + "' must not be more than " + intMax.getInt());
        }
        return intValue; 
    }
        
    /**************************************************************************
    * Return an Integer option from the previously specified option source.
    * Note:  When the option source is an Array or List, this removes the 
    *        option from the internally stored option source, so subsequent
    *        calls would not find it present again.  
    *@param  strName        Name of option (e.g., "width" for -width option).
    *@param  optionRequired Flag indicating whether the option is required to 
    *                       be present in the option source. 
    *@param  valueRequired  Flag indicating whether the option is required to
    *                       have a non-null value when it occurs in the option 
    *                       source.
    *@param  intMissingOptionDefault
    *                       Default value, if option not found in the option 
    *                       source.
    *@param  intMissingValueDefault
    *                       Default value, if option found in the option source 
    *                       with missing or null value. 
    *@return                Option value
    *@throws MissingOptionException 
    *                       When option is missing from option source.
    *@throws MissingValueException  
    *                       When option has missing or null value.
    *@throws NotAnIntegerException  
    *                       When option value is not an integer.
    **************************************************************************/
    public Integer getOptionInteger
                            (String         strName, 
                             Required       optionRequired, 
                             ValueRequired  valueRequired, 
                             DefaultInteger intMissingOptionDefault,
                             DefaultInteger intMissingValueDefault)
                throws MissingOptionException,
                       MissingValueException,
                       NotAnIntegerException
    {
        try
        {
            return getOptionInteger
                                (strName,
                                 optionRequired, 
                                 valueRequired, 
                                 intMissingOptionDefault,
                                 intMissingValueDefault,
                                 MinInteger.NONE, 
                                 MaxInteger.NONE);
        }
        catch (LessThanMinException e)
        {
            // Can't happen because we passed no min.
            return null;
        }
        catch (MoreThanMaxException e)
        {
            // Can't happen because we passed no max.
            return null;
        }
    }
        
    /**************************************************************************
    * Return an Integer option from the previously specified option source.
    * Note:  When the option source is an Array or List, this removes the 
    *        option from the internally stored option source, so subsequent
    *        calls would not find it present again.  
    *@param  strName        Name of option (e.g., "width" for -width option).
    *@param  optionRequired Flag indicating whether the option is required to 
    *                       be present in the option source. 
    *@param  valueRequired  Flag indicating whether the option is required to
    *                       have a non-null value when it occurs in the option 
    *                       source.
    *@param  intDefault     Default value, if option not found in the option 
    *                       source or option found in the option source with  
    *                       missing or null value. 
    *@return                Option value
    *@throws MissingOptionException 
    *                       When option is missing from option source.
    *@throws MissingValueException  
    *                       When option has missing or null value.
    *@throws NotAnIntegerException  
    *                       When option value is not an integer.
    **************************************************************************/
    public Integer getOptionInteger
                            (String         strName, 
                             Required       optionRequired, 
                             ValueRequired  valueRequired, 
                             DefaultInteger intDefault)
                throws MissingOptionException,
                       MissingValueException,
                       NotAnIntegerException
    {
        return getOptionInteger
                        (strName, 
                         optionRequired,
                         valueRequired,
                         intDefault,
                         intDefault);
    }
        
    /**************************************************************************
    * Return an Integer option from the previously specified option source,
    * defaulting to null. 
    * Note:  When the option source is an Array or List, this removes the 
    *        option from the internally stored option source, so subsequent
    *        calls would not find it present again.  
    *@param  strName        Name of option (e.g., "width" for -width option).
    *@param  optionRequired Flag indicating whether the option is required to 
    *                       be present in the option source. 
    *@param  valueRequired  Flag indicating whether the option is required to
    *                       have a non-null value when it occurs in the option 
    *                       source.
    *@return                Option value
    *@throws MissingOptionException 
    *                       When option is missing from option source.
    *@throws MissingValueException  
    *                       When option has missing or null value.
    *@throws NotAnIntegerException  
    *                       When option value is not an integer.
    **************************************************************************/
    public Integer getOptionInteger
                            (String         strName, 
                             Required       optionRequired, 
                             ValueRequired  valueRequired)
                throws MissingOptionException,
                       MissingValueException,
                       NotAnIntegerException
    {
        return getOptionInteger
                        (strName, 
                         optionRequired,
                         valueRequired,
                         DefaultInteger.NONE);
    }
        
    /**************************************************************************
    * Return an Integer option from the previously specified option source,
    * defaulting to null.
    * Note:  When the option source is an Array or List, this removes the 
    *        option from the internally stored option source, so subsequent
    *        calls would not find it present again.  
    *@param  strName        Name of option (e.g., "width" for -width option).
    *@return                Option value
    *@throws NotAnIntegerException  
    *                       When option value is not an integer.
    **************************************************************************/
    public Integer getOptionInteger(String strName)
                throws NotAnIntegerException
    {
        try
        {
            return getOptionInteger
                            (strName, 
                             new Required(false),
                             new ValueRequired(false));
        }
        catch (MissingOptionException e)
        {
            // Can't happen because we didn't require the option.
            return null;
        }
        catch (MissingValueException e)
        {
            // Can't happen because we didn't require the option value. 
            return null;
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

        private static void doTest(String strOut, String strValid)
        {
            System.out.println ("Output should be: " + strValid);
            System.out.println ("Output is:        " + strOut);
            System.out.println (ObjUtil.equalsOrBothNull(strOut, strValid) 
                                ? "Success!"   
                                : "Failure!");
        }

        private static void doTest(Integer intOut, Integer intValid)
        {
            doTest
                (ObjUtil.castToString(intOut), ObjUtil.castToString(intValid));
        }

        private static void reportCaught(Exception e)
        {
            System.out.println("Caught expected error:");
            System.out.println("   " + e.getClass().getName());
            System.out.println("   " + e.getMessage());
            System.out.println("Success!");
        }

        private static void reportNotCaught()
        {
            System.out.println("Failure!  Expected error not detected.");
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

                GetOpt getopt = null;

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- getNextArgString");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Basic test.
                getopt = new GetOpt(new String[]{"a", "b", "c"}); 
                doTest
                        (getopt.getNextArgString(),
                         "a");

                //-- Missing
                getopt = new GetOpt(new String[]{}); 
                doTest
                        (getopt.getNextArgString(),
                         null);

                //-- Null
                getopt = new GetOpt(new String[]{null}); 
                doTest
                        (getopt.getNextArgString(),
                         null);

                //-- Empty string
                getopt = new GetOpt(new String[]{""}); 
                doTest
                        (getopt.getNextArgString(),
                         "");

                //-- Missing, not required
                getopt = new GetOpt(new String[]{}); 
                doTest
                        (getopt.getNextArgString
                                (new Required(false)), 
                         null);

                //-- Null, not required
                getopt = new GetOpt(new String[]{null}); 
                doTest
                        (getopt.getNextArgString
                                (new Required(false)), 
                         null);

                //-- Empty string, not required
                getopt = new GetOpt(new String[]{""}); 
                doTest
                        (getopt.getNextArgString
                                (new Required(false)), 
                         "");

                //-- Missing and required
                getopt = new GetOpt(new String[]{}); 
                try
                {
                    getopt.getNextArgString
                                (new Required(true)); 
                    reportNotCaught();
                }
                catch (MissingArgException e)
                {
                    reportCaught(e);                    
                }

                //-- Null and required
                getopt = new GetOpt(new String[]{null}); 
                try
                {
                    getopt.getNextArgString
                                (new Required(true)); 
                    reportNotCaught();
                }
                catch (MissingArgException e)
                {
                    reportCaught(e);                    
                }

                //-- Empty string and required
                getopt = new GetOpt(new String[]{""}); 
                doTest
                        (getopt.getNextArgString
                                (new Required(true)), 
                         "");

                //-- Missing w/default
                getopt = new GetOpt(new String[]{}); 
                doTest
                        (getopt.getNextArgString
                                (new Required(false), 
                                 new DefaultString("default")),
                         "default");

                //-- Null w/default
                getopt = new GetOpt(new String[]{null}); 
                doTest
                        (getopt.getNextArgString
                                (new Required(false), 
                                 new DefaultString("default")),
                         "default");

                //-- Empty string w/default (default ignored)
                getopt = new GetOpt(new String[]{""}); 
                doTest
                        (getopt.getNextArgString
                                (new Required(false), 
                                 new DefaultString("default")),
                         "");

                //-- Missing and required w/ignored default
                getopt = new GetOpt(new String[]{}); 
                try
                {
                    getopt.getNextArgString
                                (new Required(true),
                                 new DefaultString("default"));
                    reportNotCaught();
                }
                catch (MissingArgException e)
                {
                    reportCaught(e);                    
                }

                //-- Null and required w/ignored default
                getopt = new GetOpt(new String[]{null}); 
                try
                {
                    getopt.getNextArgString
                                (new Required(true),
                                 new DefaultString("default"));
                    reportNotCaught();
                }
                catch (MissingArgException e)
                {
                    reportCaught(e);                    
                }

                //-- Empty string and required w/ignored default
                getopt = new GetOpt(new String[]{""}); 
                doTest
                        (getopt.getNextArgString
                                (new Required(true), 
                                 new DefaultString("default")),
                         "");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- getNextArgInteger");
                System.out.println ("--");
                //-------------------------------------------------------------

                //-- Basic test.
                getopt = new GetOpt(new String[]{"1", "2", "3"}); 
                doTest
                        (getopt.getNextArgInteger(),
                         new Integer(1));

                //-- Missing
                getopt = new GetOpt(new String[]{}); 
                doTest
                        (getopt.getNextArgInteger(),
                         null);

                //-- Null
                getopt = new GetOpt(new String[]{null}); 
                doTest
                        (getopt.getNextArgInteger(),
                         null);

                //-- Empty string
                getopt = new GetOpt(new String[]{""}); 
                try
                {
                    getopt.getNextArgInteger();
                    reportNotCaught();
                }
                catch (NotAnIntegerException e)
                {
                    reportCaught(e);                    
                }

                //-- Non-numeric
                getopt = new GetOpt(new String[]{"xx"}); 
                try
                {
                    getopt.getNextArgInteger();
                    reportNotCaught();
                }
                catch (NotAnIntegerException e)
                {
                    reportCaught(e);                    
                }

                //-- Missing, not required
                getopt = new GetOpt(new String[]{}); 
                doTest
                        (getopt.getNextArgInteger
                                (new Required(false)), 
                         null);

                //-- Null, not required
                getopt = new GetOpt(new String[]{null}); 
                doTest
                        (getopt.getNextArgInteger
                                (new Required(false)), 
                         null);

                //-- Empty string, not required
                getopt = new GetOpt(new String[]{""}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(false));
                    reportNotCaught();
                }
                catch (NotAnIntegerException e)
                {
                    reportCaught(e);                    
                }

                //-- Missing and required
                getopt = new GetOpt(new String[]{}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(true)); 
                    reportNotCaught();
                }
                catch (MissingArgException e)
                {
                    reportCaught(e);                    
                }

                //-- Null and required
                getopt = new GetOpt(new String[]{null}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(true)); 
                    reportNotCaught();
                }
                catch (MissingArgException e)
                {
                    reportCaught(e);                    
                }

                //-- Empty string and required
                getopt = new GetOpt(new String[]{""}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(true));
                    reportNotCaught();
                }
                catch (NotAnIntegerException e)
                {
                    reportCaught(e);                    
                }

                //-- Missing w/default
                getopt = new GetOpt(new String[]{}); 
                doTest
                        (getopt.getNextArgInteger
                                (new Required(false), 
                                 new DefaultInteger(123)),
                         new Integer(123));

                //-- Null w/default
                getopt = new GetOpt(new String[]{null}); 
                doTest
                        (getopt.getNextArgInteger
                                (new Required(false), 
                                 new DefaultInteger(123)),
                         new Integer(123));

                //-- Empty string w/default (default ignored)
                getopt = new GetOpt(new String[]{""}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(false),
                                 new DefaultInteger(123));
                    reportNotCaught();
                }
                catch (NotAnIntegerException e)
                {
                    reportCaught(e);                    
                }

                //-- Missing and required w/ignored default
                getopt = new GetOpt(new String[]{}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(true),
                                 new DefaultInteger(123));
                    reportNotCaught();
                }
                catch (MissingArgException e)
                {
                    reportCaught(e);                    
                }

                //-- Null and required w/ignored default
                getopt = new GetOpt(new String[]{null}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(true),
                                 new DefaultInteger(123));
                    reportNotCaught();
                }
                catch (MissingArgException e)
                {
                    reportCaught(e);                    
                }

                //-- Empty string and required w/ignored default
                getopt = new GetOpt(new String[]{""}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(true),
                                 new DefaultInteger(123));
                    reportNotCaught();
                }
                catch (NotAnIntegerException e)
                {
                    reportCaught(e);                    
                }

                //-- Within min-max range
                getopt = new GetOpt(new String[]{"2"}); 
                doTest
                        (getopt.getNextArgInteger
                                (new Required(true), 
                                 DefaultInteger.NONE,
                                 new MinInteger(1),
                                 new MaxInteger(3)),
                         new Integer(2));

                //-- Within min-max range of size 1
                getopt = new GetOpt(new String[]{"2"}); 
                doTest
                        (getopt.getNextArgInteger
                                (new Required(true), 
                                 DefaultInteger.NONE,
                                 new MinInteger(2),
                                 new MaxInteger(2)),
                         new Integer(2));

                //-- Less than min
                getopt = new GetOpt(new String[]{"0"}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(true),
                                 DefaultInteger.NONE,
                                 new MinInteger(1),
                                 MaxInteger.NONE);
                    reportNotCaught();
                }
                catch (LessThanMinException e)
                {
                    reportCaught(e);                    
                }

                //-- More than max
                getopt = new GetOpt(new String[]{"3"}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(true),
                                 DefaultInteger.NONE,
                                 MinInteger.NONE, 
                                 new MaxInteger(2));
                    reportNotCaught();
                }
                catch (MoreThanMaxException e)
                {
                    reportCaught(e);                    
                }

                //-- Null w/min
                getopt = new GetOpt(new String[]{null}); 
                doTest
                        (getopt.getNextArgInteger
                                (new Required(false),
                                 DefaultInteger.NONE,
                                 new MinInteger(1),
                                 MaxInteger.NONE),
                         null);

                //-- Null w/max
                getopt = new GetOpt(new String[]{null}); 
                doTest
                        (getopt.getNextArgInteger
                                (new Required(false),
                                 DefaultInteger.NONE,
                                 MinInteger.NONE, 
                                 new MaxInteger(2)),
                         null);

                //-- Empty string w/min
                getopt = new GetOpt(new String[]{""}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(false),
                                 DefaultInteger.NONE,
                                 new MinInteger(1),
                                 MaxInteger.NONE);
                    reportNotCaught();
                }
                catch (NotAnIntegerException e)
                {
                    reportCaught(e);                    
                }

                //-- Empty string w/max
                getopt = new GetOpt(new String[]{""}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(false),
                                 DefaultInteger.NONE,
                                 MinInteger.NONE, 
                                 new MaxInteger(2));
                    reportNotCaught();
                }
                catch (NotAnIntegerException e)
                {
                    reportCaught(e);                    
                }

                //-- Default within min-max range
                getopt = new GetOpt(new String[]{}); 
                doTest
                        (getopt.getNextArgInteger
                                (new Required(false), 
                                 new DefaultInteger(2),
                                 new MinInteger(1),
                                 new MaxInteger(3)),
                         new Integer(2));

                //-- Default within min-max range of size 1
                getopt = new GetOpt(new String[]{}); 
                doTest
                        (getopt.getNextArgInteger
                                (new Required(false), 
                                 new DefaultInteger(2),
                                 new MinInteger(2),
                                 new MaxInteger(2)),
                         new Integer(2));

                //-- Default less than min
                getopt = new GetOpt(new String[]{}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(false),
                                 new DefaultInteger(0),
                                 new MinInteger(1),
                                 MaxInteger.NONE);
                    reportNotCaught();
                }
                catch (LessThanMinException e)
                {
                    reportCaught(e);                    
                }

                //-- Default more than max
                getopt = new GetOpt(new String[]{}); 
                try
                {
                    getopt.getNextArgInteger
                                (new Required(false),
                                 new DefaultInteger(3),
                                 MinInteger.NONE, 
                                 new MaxInteger(2));
                    reportNotCaught();
                }
                catch (MoreThanMaxException e)
                {
                    reportCaught(e);                    
                }

                //-- Null default w/min
                getopt = new GetOpt(new String[]{}); 
                doTest
                        (getopt.getNextArgInteger
                                (new Required(false),
                                 DefaultInteger.NONE,
                                 new MinInteger(1),
                                 MaxInteger.NONE),
                         null);

                //-- Null default w/max
                getopt = new GetOpt(new String[]{}); 
                doTest
                        (getopt.getNextArgInteger
                                (new Required(false),
                                 DefaultInteger.NONE ,
                                 MinInteger.NONE, 
                                 new MaxInteger(2)),
                         null);

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
