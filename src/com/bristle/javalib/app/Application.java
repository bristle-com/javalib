// Copyright (C) 2007-2012 Bristle Software, Inc.
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

package com.bristle.javalib.app;

// Application
/******************************************************************************
* This class encapsulates data about an application (name, version, etc.)
*<pre>
*<b>Usage:</b>
*   - Typical scenarios for using this class are...
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
public class Application
{

    //
    // Class variables
    //
    
    //
    // Instance variables to support public properties
    //
    private String m_strFullName            = "";
    private String m_strShortName           = "";
    private String m_strAcronym             = "";
    private String m_strInternalName        = "";
    private String m_strVersionString       = "";
    private int    m_intMajorVersionNumber  = 0;
    private int    m_intMinorVersionNumber  = 0;
    private int    m_intRevisionNumber      = 0; 
    private String m_strCompanyName         = "";
    private String m_strCompanyAddress      = "";
    private String m_strCompanyPhoneNumber  = "";
    private String m_strCompanyEMailAddress = "";
    private String m_strCompanyWebSiteURL   = "";
    private String m_strShortCopyright      = "";
    private String m_strLongCopyright       = "";
    private String m_strSmallImageFileName  = "";
    private String m_strLargeImageFileName  = "";
    private String m_strDescrip             = "";
    private String m_strNotes               = "";

    //
    // Internal instance variables
    //

    /**************************************************************************
     * This number identifies the version of the class definition, used for 
     * serialized instances.  Be sure to increment it when adding/modifying
     * instance variable definitions or making any other change to the class
     * definition.  Omitting this declaration causes a compiler warning for 
     * any class that implements java.io.Serializable.
     *************************************************************************/
    private static final long serialVersionUID = 1L;

    /**************************************************************************
    * Set all properties.  Use this whenever you want to be sure that 
    * properties added in the future are also set.  Once this method is
    * updated to include a new property, all calls will fail to compile
    * unless they are also updated. 
    *@param  strFullName            The property value.
    *@param  strShortName           The property value.
    *@param  strAcronym             The property value.
    *@param  strInternalName        The property value.
    *@param  strVersionString       The property value.
    *@param  intMajorVersionNumber  The property value.
    *@param  intMinorVersionNumber  The property value.
    *@param  intRevisionNumber      The property value.
    *@param  strCompanyName         The property value.
    *@param  strCompanyAddress      The property value.
    *@param  strCompanyPhoneNumber  The property value.
    *@param  strCompanyEMailAddress The property value.
    *@param  strCompanyWebSiteURL   The property value.
    *@param  strShortCopyright      The property value.
    *@param  strLongCopyright       The property value.
    *@param  strSmallImageFileName  The property value.
    *@param  strLargeImageFileName  The property value.
    *@param  strDescrip             The property value.
    *@param  strNotes               The property value.
    **************************************************************************/
    public void setAll
                    (String strFullName
                    ,String strShortName
                    ,String strAcronym
                    ,String strInternalName
                    ,String strVersionString
                    ,int    intMajorVersionNumber
                    ,int    intMinorVersionNumber
                    ,int    intRevisionNumber
                    ,String strCompanyName
                    ,String strCompanyAddress
                    ,String strCompanyPhoneNumber
                    ,String strCompanyEMailAddress
                    ,String strCompanyWebSiteURL
                    ,String strShortCopyright
                    ,String strLongCopyright
                    ,String strSmallImageFileName
                    ,String strLargeImageFileName
                    ,String strDescrip
                    ,String strNotes)
    {
        setFullName             (strFullName);
        setShortName            (strShortName);
        setAcronym              (strAcronym);
        setInternalName         (strInternalName);
        setVersionString        (strVersionString);
        setMajorVersionNumber   (intMajorVersionNumber);
        setMinorVersionNumber   (intMinorVersionNumber);
        setRevisionNumber       (intRevisionNumber);
        setCompanyName          (strCompanyName);
        setCompanyAddress       (strCompanyAddress);
        setCompanyPhoneNumber   (strCompanyPhoneNumber);
        setCompanyEMailAddress  (strCompanyEMailAddress);
        setCompanyWebSiteURL    (strCompanyWebSiteURL);
        setShortCopyright       (strShortCopyright);
        setLongCopyright        (strLongCopyright);
        setSmallImageFileName   (strSmallImageFileName);
        setLargeImageFileName   (strLargeImageFileName);
        setDescrip              (strDescrip);
        setNotes                (strNotes);
    }
    
    /**************************************************************************
    * Do a shallow copy of all properties from the specified object. 
    *@param  objFrom    The object to copy from.
    **************************************************************************/
    public void shallowCopyFrom(Application objFrom)
    {
        setAll(objFrom.getFullName()
              ,objFrom.getShortName()
              ,objFrom.getAcronym()
              ,objFrom.getInternalName()
              ,objFrom.getVersionString()
              ,objFrom.getMajorVersionNumber()
              ,objFrom.getMinorVersionNumber()
              ,objFrom.getRevisionNumber()
              ,objFrom.getCompanyName()
              ,objFrom.getCompanyAddress()
              ,objFrom.getCompanyPhoneNumber()
              ,objFrom.getCompanyEMailAddress()
              ,objFrom.getCompanyWebSiteURL()
              ,objFrom.getShortCopyright()
              ,objFrom.getLongCopyright()
              ,objFrom.getSmallImageFileName()
              ,objFrom.getLargeImageFileName()
              ,objFrom.getDescrip()
              ,objFrom.getNotes()
              );
    }
    
    /**************************************************************************
    * Get the full user-visible name of the application.
    *@return The name.
    **************************************************************************/
    public String getFullName()
    {
        return m_strFullName;
    }

    /**************************************************************************
    * Set the full user-visible name of the application.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setFullName(String strVal)
    {
        m_strFullName = strVal;
    }

    /**************************************************************************
    * Get the short user-visible name of the application.
    *@return The name.
    **************************************************************************/
    public String getShortName()
    {
        return m_strShortName;
    }

    /**************************************************************************
    * Set the short user-visible name of the application.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setShortName(String strVal)
    {
        m_strShortName = strVal;
    }

    /**************************************************************************
    * Get the user-visible acronym of the application.
    *@return The acronym.
    **************************************************************************/
    public String getAcronym()
    {
        return m_strAcronym;
    }

    /**************************************************************************
    * Set the user-visible acronym of the application.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setAcronym(String strVal)
    {
        m_strAcronym = strVal;
    }

    /**************************************************************************
    * Get the internal name of the application, typically used in properties 
    * files and such, but not typically shown to the user.  
    *@return The name.
    **************************************************************************/
    public String getInternalName()
    {
        return m_strInternalName;
    }

    /**************************************************************************
    * Set the internal name of the application, typically used in property
    * files and such, but not typically shown to the user.  
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setInternalName(String strVal)
    {
        m_strInternalName = strVal;
    }

    /**************************************************************************
    * Get the user-visible version string of the application.
    *@return The version string.
    **************************************************************************/
    public String getVersionString()
    {
        return m_strVersionString;
    }

    /**************************************************************************
    * Set the user-visible version string of the application.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setVersionString(String strVal)
    {
        m_strVersionString = strVal;
    }

    /**************************************************************************
    * Get the major version number of the application.
    *@return The version number.
    **************************************************************************/
    public int getMajorVersionNumber()
    {
        return m_intMajorVersionNumber;
    }

    /**************************************************************************
    * Set the major version number of the application.
    *@param  intVal     The value to set.
    **************************************************************************/
    public void setMajorVersionNumber(int intVal)
    {
        m_intMajorVersionNumber = intVal;
    }

    /**************************************************************************
    * Get the minor version number of the application.
    *@return The version number.
    **************************************************************************/
    public int getMinorVersionNumber()
    {
        return m_intMinorVersionNumber;
    }

    /**************************************************************************
    * Set the minor version number of the application.
    *@param  intVal     The value to set.
    **************************************************************************/
    public void setMinorVersionNumber(int intVal)
    {
        m_intMinorVersionNumber = intVal;
    }

    /**************************************************************************
    * Get the revision number of the application.
    *@return The revision number.
    **************************************************************************/
    public int getRevisionNumber()
    {
        return m_intRevisionNumber;
    }

    /**************************************************************************
    * Set the revision number of the application.
    *@param  intVal     The value to set.
    **************************************************************************/
    public void setRevisionNumber(int intVal)
    {
        m_intRevisionNumber = intVal;
    }

    /**************************************************************************
    * Get the company name.
    *@return The company name.
    **************************************************************************/
    public String getCompanyName()
    {
        return m_strCompanyName;
    }

    /**************************************************************************
    * Set the company name.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setCompanyName(String strVal)
    {
        m_strCompanyName = strVal;
    }

    /**************************************************************************
    * Get the company address.
    *@return The company address.
    **************************************************************************/
    public String getCompanyAddress()
    {
        return m_strCompanyAddress;
    }

    /**************************************************************************
    * Set the company address.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setCompanyAddress(String strVal)
    {
        m_strCompanyAddress = strVal;
    }

    /**************************************************************************
    * Get the company phone number.
    *@return The company phone number.
    **************************************************************************/
    public String getCompanyPhoneNumber()
    {
        return m_strCompanyPhoneNumber;
    }

    /**************************************************************************
    * Set the company phone number.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setCompanyPhoneNumber(String strVal)
    {
        m_strCompanyPhoneNumber = strVal;
    }

    /**************************************************************************
    * Get the company e-mail address.
    *@return The company e-mail address.
    **************************************************************************/
    public String getCompanyEMailAddress()
    {
        return m_strCompanyEMailAddress;
    }

    /**************************************************************************
    * Set the company e-mail address.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setCompanyEMailAddress(String strVal)
    {
        m_strCompanyEMailAddress = strVal;
    }

    /**************************************************************************
    * Get the company web site URL.
    *@return The company web site URL.
    **************************************************************************/
    public String getCompanyWebSiteURL()
    {
        return m_strCompanyWebSiteURL;
    }

    /**************************************************************************
    * Set the company web site URL.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setCompanyWebSiteURL(String strVal)
    {
        m_strCompanyWebSiteURL = strVal;
    }

    /**************************************************************************
    * Get the short copyright of the application.
    *@return The copyright.
    **************************************************************************/
    public String getShortCopyright()
    {
        return m_strShortCopyright;
    }

    /**************************************************************************
    * Set the short copyright of the application.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setShortCopyright(String strVal)
    {
        m_strShortCopyright = strVal;
    }

    /**************************************************************************
    * Get the long copyright of the application.
    *@return The copyright.
    **************************************************************************/
    public String getLongCopyright()
    {
        return m_strLongCopyright;
    }

    /**************************************************************************
    * Set the long copyright of the application.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setLongCopyright(String strVal)
    {
        m_strLongCopyright = strVal;
    }

    /**************************************************************************
    * Get the filename of the small image (GIF, JPG, etc.) for the application.
    *@return The filename.
    **************************************************************************/
    public String getSmallImageFileName()
    {
        return m_strSmallImageFileName;
    }

    /**************************************************************************
    * Get the filename of the small image (GIF, JPG, etc.) for the application.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setSmallImageFileName(String strVal)
    {
        m_strSmallImageFileName = strVal;
    }

    /**************************************************************************
    * Get the filename of the large image (GIF, JPG, etc.) for the application.
    *@return The filename.
    **************************************************************************/
    public String getLargeImageFileName()
    {
        return m_strLargeImageFileName;
    }

    /**************************************************************************
    * Get the filename of the large image (GIF, JPG, etc.) for the application.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setLargeImageFileName(String strVal)
    {
        m_strLargeImageFileName = strVal;
    }

    /**************************************************************************
    * Get the description of the application, potentially multiple sentences.
    *@return The description.
    **************************************************************************/
    public String getDescrip()
    {
        return m_strDescrip;
    }

    /**************************************************************************
    * Set the description of the application, potentially multiple sentences.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setDescrip(String strVal)
    {
        m_strDescrip = strVal;
    }

    /**************************************************************************
    * Get the notes about the application.
    *@return The notes.
    **************************************************************************/
    public String getNotes()
    {
        return m_strNotes;
    }

    /**************************************************************************
    * Set the notes about the application.
    *@param  strVal     The value to set.
    **************************************************************************/
    public void setNotes(String strVal)
    {
        m_strNotes = strVal;
    }

    /**************************************************************************
    * Load all properties from a properties file, using the specified internal
    * name as the identifier in the properties file.  
    *@param  strInternalName  The name to use when looking in properties files.
    **************************************************************************/
    public void loadProperties(String strInternalName)
    {
        //?? Not yet supported.
    }
}
