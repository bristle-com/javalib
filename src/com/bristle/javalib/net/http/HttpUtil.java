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

package com.bristle.javalib.net.http;

import com.bristle.javalib.net.html.HtmlUtil;
import com.bristle.javalib.util.Base64;
import com.bristle.javalib.util.StrUtil;
import com.bristle.javalib.util.ObjUtil;
import com.bristle.javalib.util.GetOpt;
import com.bristle.javalib.xml.XMLUtil;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import javax.servlet.GenericServlet;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

// HttpUtil
/******************************************************************************
* This class contains utility routines for interacting with the HTTP protocol.
*<pre>
*<b>Usage:</b>
*   - The typical scenario for using this class is:
*     - To get the user's credentials, prompting if necessary:
*               HttpUtil.Credentials credentials = HttpUtil.getCredentials
*                               (request, 
*                                response, 
*                                "",
*                                !HttpUtil.blnREJECT_CURRENT_CREDENTIALS,
*                                HttpUtil.blnASK_CLIENT_IF_NECESSARY);
*     - To reject any credentials the user may previously have specified,
*       forcing a new prompt:
*               HttpUtil.Credentials credentials = HttpUtil.getCredentials
*                               (request, 
*                                response, 
*                                "", 
*                                HttpUtil.blnREJECT_CURRENT_CREDENTIALS,
*                                HttpUtil.blnASK_CLIENT_IF_NECESSARY);
*     - To get any credentials the user may previously have specified,
*       without permitting a new prompt:
*               HttpUtil.Credentials credentials = HttpUtil.getCredentials
*                               (request, 
*                                null,
*                                null,
*                                !HttpUtil.blnREJECT_CURRENT_CREDENTIALS,
*                                !HttpUtil.blnASK_CLIENT_IF_NECESSARY);
*     - To get only the username, without permitting a new prompt:
*               String strUsername = HttpUtil.getUsername(request);
*<b>Assumptions:</b>
*<b>Effects:</b>
*       - Interacts with the HTTP client via the HTTP protocol.  If the HTTP
*         client is a Web Browser, getCredentials may cause it to prompt its 
*         interactive user for credentials.
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class HttpUtil
{
    //--
    //-- Class variables
    //--
    private static Map st_mapAbbrevs = null; 

    //--
    //-- Instance variables to support public properties
    //--

    //--
    //-- Internal instance variables
    //--
    
    /**************************************************************************
    * HTML content-type.
    **************************************************************************/
    public static final String strCONTENT_TYPE_HTML = "text/html";

    /**************************************************************************
    * XML content-type.
    **************************************************************************/
    public static final String strCONTENT_TYPE_XML  = "text/xml";

    /**************************************************************************
    * Constant included in the value returned by getHttpUserAgentAbbrev() when 
    * the specified user agent string and/or OS are unknown.
    **************************************************************************/
    public static final String strUSER_AGENT_UNKNOWN = "UNK";

    /**************************************************************************
    * Nested class used as argument of getCredentials, so that it can allocate 
    * and return multiple Strings.
    *<pre>
    *<b>Notes:</b>
    *   - This class must be static so that it can be instantiated by static
    *     methods of its enclosing class.
    *</pre>
    **************************************************************************/
    public static class Credentials
    {
        public String strUsername = "";
        public String strPassword = "";

        /**************************************************************************
        * Constructor.
        **************************************************************************/
        public Credentials()
        {
        }
        /**************************************************************************
        * Constructor.
        *@param  strUsername    Username to store in Credentials.
        *@param  strPassword    Password to store in Credentials.
        **************************************************************************/
        public Credentials(String strUsername, String strPassword)
        {
            this.strUsername = strUsername;
            this.strPassword = strPassword;
        }
    }

    /**************************************************************************
    * Constant for use as parameter to getCredentials(). 
    **************************************************************************/
    public static final boolean blnREJECT_CURRENT_CREDENTIALS = true;

    /**************************************************************************
    * Constant for use as parameter to getCredentials(). 
    **************************************************************************/
    public static final boolean blnASK_CLIENT_IF_NECESSARY = true;

    /**************************************************************************
    * Get the credentials of the current user from the HTTP client via the
    * HTTP protocol.  If the HTTP client did not already send them as an HTTP
    * header or if blnRejectCurrentCredentials is true, return null to the 
    * calling routine.  Before returning null, if blnAskClientIfNecessary is 
    * true, send a response to the HTTP client asking it to re-issue the HTTP 
    * request with the credentials.  If the HTTP client is a Web Browser, this 
    * may cause it to prompt its user for credentials before re-issuing the 
    * request.
    *@param  request        The HttpServletRequest object used to get the 
    *                       HTTP headers from the HTTP client.
    *@param  response       The HttpServletResponse object used to tell the
    *                       HTTP client to re-issue the HTTP request.
    *                       Can be null if blnAskClientIfNecessary is false.
    *@param  strRealm       String to pass to the HTTP client as the "realm".
    *                       If the client is a Web Browser, it may show this 
    *                       string to its user when prompting for credentials.
    *                       Can be null if blnAskClientIfNecessary is false.
    *@param  blnRejectCurrentCredentials      
    *                       Boolean specifying whether to reject any  
    *                       credentials already in the HTTP headers.
    *@param  blnAskClientIfNecessary   
    *                       Boolean specifying whether to allow any 
    *                       interaction with the HTTP client that may cause
    *                       it to prompt its user for new credentials.
    *@return                Credentials object containing the credentials,
    *                       or null if no credentials are available yet.
    *@throws IOException    When an I/O error occurs trying to tell the
    *                       HTTP client to re-issue the HTTP request with 
    *                       credentials, or when an error occurs trying to
    *                       decode the credentials sent by the HTTP client.
    **************************************************************************/
    public static Credentials getCredentials
                        (HttpServletRequest  request,
                         HttpServletResponse response,
                         String              strRealm,
                         boolean             blnRejectCurrentCredentials,
                         boolean             blnAskClientIfNecessary)
                throws IOException
    {
        //-- Get the current credentials, if any, from the HTTP client.
        String strAuthorization = request.getHeader("Authorization");

        //-- If directed by the caller to reject the current credentials, or 
        //-- if no credentials were passed by the HTTP client, return null.
        if (blnRejectCurrentCredentials || (strAuthorization == null))
        {

            //-- If directed by the caller, ask the HTTP client to re-issue 
            //-- the HTTP request with new credentials (prompting its user 
            //-- if necessary).
            //-- Note:  Nothing else to do here.  When we return null, the 
            //--        caller should do nothing.  The HTTP client should 
            //--        re-issue the same request with new credentials,
            //--        and we'll try again then.
            //-- Note:  Use setStatus(), not sendError() to set the 
            //--        SC_UNAUTHORIZED status code.  For the Tomcat Web 
            //--        Server 3.1 and for iPlanet Web Server 4.1, it doesn't 
            //--        matter, but for iPlanet 6.0, sendError() discards the 
            //--        "WWW-Authenticate" header set by setHeader(), and the 
            //--        page contents sent to the PrintWriter.  Since the 
            //--        header is not sent to the Web Browser, IE 5.0 displays 
            //--        a default "Unauthorized" page without prompting the 
            //--        user.  Netscape 4.73 still prompts the user, even 
            //--        without the header, but for IE users, we must call
            //--        setStatus() instead of sendError().
            //--        Note:  This was officially acknowledged by iPlanet 
            //--               Tech Support as a known bug (id#4657029) in 
            //--               iPlanet 6.0.  Recommended workaround is to use
            //--               setStatus() as we now do.  To be fixed in the 
            //--               next release of iPlanet.
            //-- Note:  According to some notes I found on the Web, setStatus()
            //--        should be called before anything is written to the 
            //--        PrintWriter.  Doesn't seem to matter in my testing.
            //--        Both cases seem to work, but it may be more correct 
            //--        to call setStatus() first.
            if (blnAskClientIfNecessary)
            {
                response.setContentType("text/html");
                response.setHeader
                        ("WWW-Authenticate", 
                         "BASIC realm=\"" + strRealm + "\"");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter pw = response.getWriter();
                pw.println("<html>");
                pw.println(" <body>");
                pw.println("  <h1>");
                pw.println("   Unauthorized");
                pw.println("  </h1>");
                pw.println(" </body>");
                pw.println("</html>");
            }

            return null;
        }

        //-- Decode the credentials.
        String strEncoded = strAuthorization.substring("Basic ".length());
        String strDecoded;
        try
        {
            strDecoded = Base64.decode(strEncoded);
        }
        catch (Exception e)
        {
            //-- Can't decode the credentials.  Return null.
            return null;
        }

        //-- Parse the decoded credentials as "username:password".
        StringTokenizer st = new StringTokenizer(strDecoded, ":");
        Credentials credentials = new Credentials();
        try
        {
            credentials.strUsername = st.nextToken();
            credentials.strPassword = st.nextToken();
        }
        catch (NoSuchElementException e)
        {
            //-- Can't parse the decoded credentials string.  Perhaps no
            //-- password was specified.  No problem.  Perhaps the user 
            //-- has no password.  Return the credentials object which 
            //-- was initialized to empty strings and has perhaps had the
            //-- username filled in by now.  The caller is responsible 
            //-- for validating the credentials.
        }
        return credentials;
    }

    /**************************************************************************
    * Get the uppercased username of the current user from the HTTP headers 
    * of the specified HTTP request.  If the username is not already contained 
    * in the HTTP headers, return "".  Do not interact further with the HTTP 
    * client, attempting to determine the username.
    *@param  request        The HttpServletRequest object used to get the 
    *                       HTTP headers from the HTTP client.
    *@return                String containing the username, or "".
    *@see    HttpUtil#getCredentials
    **************************************************************************/
    public static String getUsername(HttpServletRequest request)
    {

        HttpUtil.Credentials credentials = null;
        try
        {
            credentials = HttpUtil.getCredentials
                                (request, 
                                null,
                                null,
                                !HttpUtil.blnREJECT_CURRENT_CREDENTIALS,
                                !HttpUtil.blnASK_CLIENT_IF_NECESSARY);
        }
        catch (IOException e)
        {
            // Do nothing.  Shouldn't be possible since we told getCredentials()
            // to not interact with the client.
        }
        return (credentials == null)
               ? ""
               : credentials.strUsername.toUpperCase();
    }

    /**************************************************************************
    * Returns a GetOpt object that can be used to get the values of the 
    * HTTP parameters of the specified HttpServletRequest object. 
    *@param  request    The HttpServletRequest to get the parameters from.
    *@return            The GetOpt object.
    **************************************************************************/
    public static GetOpt getParams(HttpServletRequest request)
    {
        return new GetOpt(request.getParameterMap());
    }

    /**************************************************************************
    * Returns a GetOpt object that can be used to get the values of the 
    * HTTP headers of the specified HttpServletRequest object. 
    *@param  request    The HttpServletRequest to get the headers from.
    *@return            The GetOpt object.
    **************************************************************************/
    public static GetOpt getHeaders(HttpServletRequest request)
    {
        Map map = new HashMap();
        for (Enumeration enumHeaderNames = request.getHeaderNames(); 
             enumHeaderNames.hasMoreElements();
            ) 
        {
            String strKey = (String)enumHeaderNames.nextElement();
            map.put(strKey, request.getHeader(strKey));
        }
        return new GetOpt(map);
    }

    /**************************************************************************
    * Returns a GetOpt object that can be used to get the values of the 
    * HTTP cookies of the specified HttpServletRequest object. 
    *@param  request    The HttpServletRequest to get the cookies from.
    *@return            The GetOpt object.
    **************************************************************************/
    public static GetOpt getCookies(HttpServletRequest request)
    {
        Cookie[] cookies = request.getCookies();
        Map map = new HashMap();
        for (int i = 0; i < cookies.length; i++)
        {
            map.put(cookies[i].getName(), cookies[i].getValue());
        }
        return new GetOpt(map);
    }

    /**************************************************************************
    * Returns true if the parameter with the specified name is present in the 
    * HttpServletRequest object; false otherwise. 
    *@param  request    The HttpServletRequest object to get the value from.
    *@param  strName    Name of the parameter.
    *@return            true if present; false otherwise.
    **************************************************************************/
    public static boolean getParamPresent
                        (HttpServletRequest request, String strName)
    {
        return (null != request.getParameter(strName));
    }

    /**************************************************************************
    * Returns the value of the parameter with the specified name, getting
    * it from the specified HttpServletRequest object, and mapping empty 
    * values to null.
    *@param  request    The HttpServletRequest object to get the value from.
    *@param  strName    Name of the parameter.
    *@return            Null or the parameter value.
    **************************************************************************/
    public static String getParam(HttpServletRequest request, String strName)
    {
        return StrUtil.mapEmptyToNull(request.getParameter(strName));
    }

    /**************************************************************************
    * Returns the value of the parameter with the specified name, getting 
    * it from the specified HttpServletRequest object, and mapping empty 
    * values ("") and missing parameters (null) to the specified default.
    *@param  request    The HttpServletRequest object to get the value from.
    *@param  strName    Name of the parameter.
    *@param  strDefault Default value to use if no parameter value exists.
    *@return            Parameter value.
    **************************************************************************/
    public static String getParamString
                            (HttpServletRequest request, 
                             String             strName,
                             String             strDefault)
    {
        String strRC = getParam(request, strName);
        if (strRC == null)
        {
            strRC = strDefault;
        }
        return strRC;
    }

    /**************************************************************************
    * Returns the Integer value of the parameter with the specified name, 
    * getting it from the specified HttpServletRequest object, and mapping 
    * non-integer values to null.
    *@param  request    The HttpServletRequest object to get the value from.
    *@param  strName    Name of the parameter.
    *@return            Null or the parameter value.
    **************************************************************************/
    public static Integer getParamInteger
                        (HttpServletRequest request, String strName)
    {
        String strValue = getParam(request, strName);
        try
        {
            return new Integer(strValue);
        }
        catch (Throwable t)
        {
            return null;
        }
    }

    /**************************************************************************
    * Returns the int value of the parameter with the specified name, getting 
    * it from the specified HttpServletRequest object.  If the value cannot
    * be interpreted as an int, an exception is thrown.
    *@param  request    The HttpServletRequest object to get the value from.
    *@param  strName    Name of the parameter.
    *@return            Parameter value.
    *@throws NullPointerException
    *                   When the specified value cannot be interpreted as an 
    *                   int.
    **************************************************************************/
    public static int getParam_int(HttpServletRequest request, String strName)
    {
        return getParamInteger(request, strName).intValue();
    }

    /**************************************************************************
    * Returns the int value of the parameter with the specified name, getting 
    * it from the specified HttpServletRequest object.  If the value cannot
    * be interpreted as an int, the specified default value is returned.
    *@param  request    The HttpServletRequest object to get the value from.
    *@param  strName    Name of the parameter.
    *@param  intDefault Default value to use if no valid parameter value exists.
    *@return            Parameter value.
    **************************************************************************/
    public static int getParam_int
                            (HttpServletRequest request, 
                             String             strName,
                             int                intDefault)
    {
        try
        {
            return getParamInteger(request, strName).intValue();
        }
        catch (Throwable t)
        {
            return intDefault;
        }
    }

    /**************************************************************************
    * Returns the Float value of the parameter with the specified name, 
    * getting it from the specified HttpServletRequest object, and mapping 
    * non-float values to null.
    *@param  request    The HttpServletRequest object to get the value from.
    *@param  strName    Name of the parameter.
    *@return            Null or the parameter value.
    **************************************************************************/
    public static Float getParamFloat
                        (HttpServletRequest request, String strName)
    {
        String strValue = getParam(request, strName);
        try
        {
            return new Float(strValue);
        }
        catch (Throwable t)
        {
            return null;
        }
    }

    /**************************************************************************
    * Returns the float value of the parameter with the specified name, getting 
    * it from the specified HttpServletRequest object.  If the value cannot
    * be interpreted as a float, an exception is thrown.
    *@param  request    The HttpServletRequest object to get the value from.
    *@param  strName    Name of the parameter.
    *@return            Parameter value.
    *@throws NullPointerException
    *                   When the specified value cannot be interpreted as a 
    *                   float.
    **************************************************************************/
    public static float getParam_float(HttpServletRequest request, String strName)
    {
        return getParamFloat(request, strName).floatValue();
    }

    /**************************************************************************
    * Returns the float value of the parameter with the specified name, getting 
    * it from the specified HttpServletRequest object.  If the value cannot
    * be interpreted as a float, the specified default value is returned.
    *@param  request      The HttpServletRequest object to get the value from.
    *@param  strName      Name of the parameter.
    *@param  floatDefault Default value to use if no valid parameter value exists.
    *@return              Parameter value.
    **************************************************************************/
    public static float getParam_float
                            (HttpServletRequest request, 
                             String             strName,
                             float              floatDefault)
    {
        try
        {
            return getParamFloat(request, strName).floatValue();
        }
        catch (Throwable t)
        {
            return floatDefault;
        }
    }

    /**************************************************************************
    * Open a Reader to the specified URL.
    *@param  strURL         URL to connect to.
    *@return                Reader connected to the URL.
    *@throws IOException    When an I/O error occurs reading from the URL.
    **************************************************************************/
    public static Reader openURLReader(String strURL)
                throws IOException
    {
        return new InputStreamReader(new URL(strURL).openStream());
    }

    /**************************************************************************
    * Open a Reader to the specified URL, using the specified HTTP credentials.
    *@param  strURL         URL to connect to.
    *@param  credentials    Credentials to use in connecting.
    *@return                Reader connected to the URL.
    *@throws IOException    When an I/O error occurs reading from the URL.
    **************************************************************************/
    public static Reader openHttpReader
                        (String         strURL,
                         Credentials    credentials)
                throws IOException
    {
        URLConnection conn = new URL(strURL).openConnection();
        conn.setRequestProperty
                ("Authorization",
                 "Basic " + Base64.encode
                    (credentials.strUsername + ":" + credentials.strPassword)
                );
        conn.connect();            
        return new InputStreamReader(conn.getInputStream());
    }

    /**************************************************************************
    * Return the full URL string, including URL parameters.  This is like 
    * HttpServletRequest.getRequestURL(), but also includes the URL parameters.
    * For example:
    *   http://bristle.com:1234/servletname?p1=abc&p2=def 
    *@param  request        The HttpServletRequest object of the servlet.
    **************************************************************************/
    public static String getFullRequestURL (HttpServletRequest request)
    {
        StringBuffer sbRC = request.getRequestURL();
        String strQueryString = request.getQueryString();
        if (strQueryString != null)
        {
            sbRC.append("?");
            sbRC.append(strQueryString);
        }
        return sbRC.toString();
    }

    /**************************************************************************
    * Return the full URI string, including URI parameters.  This is like 
    * HttpServletRequest.getRequestURI(), but also includes the URI parameters.
    * For example:
    *   /servletname?p1=abc&p2=def 
    *@param  request        The HttpServletRequest object of the servlet.
    **************************************************************************/
    public static String getFullRequestURI (HttpServletRequest request)
    {
        String strRC = request.getRequestURI();
        String strQueryString = request.getQueryString();
        if (strQueryString != null)
        {
            strRC += "?" + strQueryString;
        }
        return strRC;
    }

    /**************************************************************************
    * Get the ServletContext of the specified request.
    *@param  request    The HttpServletRequest object.
    *@return            The ServletContext object.
    **************************************************************************/
    public static ServletContext getServletContext(HttpServletRequest request)
    {
        return request.getSession().getServletContext();
    }

    /**************************************************************************
    * Get the full path of the directory associated with the specified 
    * ServletContext.
    *@param  context    The ServletContext
    *@return            Full path of the directory, ending in a path separator
    *                   (typically "/") for the native file system, as returned
    *                   by java.io.File.separator.
    **************************************************************************/
    public static String getServletDirectory(ServletContext context)
    {
        String strRC = context.getRealPath("");
        String strSeparator = File.separator;
        if (!strRC.endsWith(strSeparator))
        {
            strRC += strSeparator;
        }
        return strRC;
    }

    /**************************************************************************
    * Get the full path of the directory where the specified servlet resides.
    *@param  servlet    The HttpServlet object.
    *@return            Full path of the directory, ending in a path separator
    *                   (typically "/") for the native file system, as returned
    *                   by java.io.File.separator.
    **************************************************************************/
    public static String getServletDirectory(GenericServlet servlet)
    {
        return getServletDirectory(servlet.getServletContext());
    }

    /**************************************************************************
    * Get the full path of the directory where the servlet associated with the
    * specified request resides.
    *@param  request    The HttpServletRequest object.
    *@return            Full path of the directory, ending in a path separator
    *                   (typically "/") for the native file system, as returned
    *                   by java.io.File.separator.
    **************************************************************************/
    public static String getServletDirectory(HttpServletRequest request)
    {
        return getServletDirectory(getServletContext(request));
    }

    /**************************************************************************
    * Get the value of the specified attribute from the Session associated 
    * with the specified HttpServletRequest.  
    *@param  request    The HttpServletRequest object.
    *@param  strName    The attribute name.
    *@return            The requested value.
    **************************************************************************/
    public static Object getSessionAttribute
                                    (HttpServletRequest request,
                                     String             strName)
    {
        final boolean blnCreateSessionIfNoneExists = true;
        HttpSession session = request.getSession(!blnCreateSessionIfNoneExists); 
        if (session == null)
        {
            return null;
        }
        return session.getAttribute(strName);
    }

    /**************************************************************************
    * Get the int value of the specified attribute from the Session 
    * associated with the specified HttpServletRequest.  
    * If any error occurs (no Session, no such attribute, attribute value not 
    * an int, etc.), return the specified default value.
    *@param  request    The HttpServletRequest object.
    *@param  strName    The attribute name.
    *@param  intDefault Default value to use if an error occurs.
    *@return            The requested value.
    **************************************************************************/
    public static int getSessionAttribute_int
                            (HttpServletRequest request, 
                             String             strName,
                             int                intDefault)
    {
        try
        {
            return Integer.parseInt
                (ObjUtil.castToString(getSessionAttribute(request, strName)));
        }
        catch (Throwable t)
        {
            return intDefault;
        }
    }

    /**************************************************************************
    * Set the value of the specified attribute in the Session associated with 
    * the specified HttpServletRequest.  
    *@param  request    The HttpServletRequest object.
    *@param  strName    The attribute name.
    **************************************************************************/
    public static void setSessionAttribute
                                    (HttpServletRequest request,
                                     String             strName,
                                     Object             obj)
    {
        request.getSession().setAttribute(strName, obj); 
    }

    /**************************************************************************
    * Get the value of the specified attribute from the ServletContext 
    * (application context) of the servlet associated with the specified 
    * HttpServletRequest.  
    *@param  request    The HttpServletRequest object.
    *@param  strName    The attribute name.
    *@return            The requested value.
    **************************************************************************/
    public static Object getServletAttribute
                                    (HttpServletRequest request,
                                     String             strName)
    {
        return getServletContext(request).getAttribute(strName);
    }

    /**************************************************************************
    * Set the value of the specified attribute in the ServletContext 
    * (application context) of the servlet associated with the specified 
    * HttpServletRequest.  
    *@param  request    The HttpServletRequest object.
    *@param  strName    The attribute name.
    **************************************************************************/
    public static void setServletAttribute
                                    (HttpServletRequest request,
                                     String             strName,
                                     Object             obj)
    {
        getServletContext(request).setAttribute(strName, obj); 
    }

    /**************************************************************************
    * Get an XML DOM document containing all information available to a 
    * servlet.
    *<xmp>
    * Format of XML is:
    *   <InfoAvailableToServlet>
//?? Update to match code -- new additions.
    *    <RemoteUser>xxx</RemoteUser>
    *    <RemoteHost>xxx</RemoteHost>
    *    <RemoteAddr>xxx</RemoteAddr>
    *    <RemotePort>xxx</RemotePort>
    *    <AuthType>xxx</AuthType>
    *    <HttpUserAgent>xxx</HttpUserAgent>
    *    <HttpReferer>xxx</HttpReferer>
    *
    *    <Method>xxx</Method>
    *    <FullRequestURL>xxx</FullRequestURL>
    *    <RequestURL>xxx</RequestURL>
    *    <Protocol>xxx</Protocol>
    *    <isSecure>xxx</isSecure>
    *    <Scheme>xxx</Scheme>
    *    <ServerName>xxx</ServerName>
    *    <ServerPort>xxx</ServerPort>
    *    <FullRequestURI>xxx</FullRequestURI>
    *    <RequestURI>xxx</RequestURI>
    *    <ContextPath>xxx</ContextPath>
    *    <ServletPath>xxx</ServletPath>
    *    <PathInfo>xxx</PathInfo>
    *    <PathTranslated>xxx</PathTranslated>
    *    <QueryString>xxx</QueryString>
    *    <CharacterEncoding>xxx</CharacterEncoding>
    *    <ContentType>xxx</ContentType>
    *    <ContentLength>xxx</ContentLength>
    *    <LocalAddr>xxx</LocalAddr>
    *    <LocalName>xxx</LocalName>
    *    <LocalPort>xxx</LocalPort>
    *    <HttpAccept>xxx</HttpAccept>
    *    <RequestHeaders>
    *     <RequestHeader>
    *      <HeaderName>xxx</HeaderName>
    *      <HeaderValue>xxx</HeaderValue>
    *     </RequestHeader>
    *     ...
    *    </RequestHeaders>
    *    <RequestParams>
    *     <RequestParam>
    *      <ParamName>xxx</ParamName>
    *      <ParamValue>xxx</ParamValue>
    *     </RequestParam>
    *     ...
    *    </RequestParams>
    *    <RequestAttributes>
    *     <RequestAttribute>
    *      <AttributeName>xxx</AttributeName>
    *      <AttributeValue>xxx</AttributeValue>
    *     </RequestAttribute>
    *     ...
    *    </RequestAttributes>
    *    <RequestCookies>
    *     <RequestCookie>
    *      <CookieName>xxx</CookieName>
    *      <CookieValue>xxx</CookieValue>
    *      <CookieComment>xxx</CookieComment>
    *      <CookieDomain>xxx</CookieDomain>
    *      <CookieMaxAge>xxx</CookieMaxAge>
    *      <CookiePath>xxx</CookiePath>
    *      <CookieSecure>xxx</CookieSecure>
    *      <CookieVersion>xxx</CookieVersion>
    *     </RequestCookie>
    *     ...
    *    </RequestCookies>
    *
    *    <SessionId>xxx</SessionId>
    *    <isNew>xxx</isNew>
    *    <SessionCreationTime>xxx</SessionCreationTime>
    *    <SessionLastAccessedTime>xxx</SessionLastAccessedTime>
    *    <SessionMaxInactiveInterval>xxx</SessionMaxInactiveInterval>
    *    <SessionAttributes>
    *     <SessionAttribute>
    *      <AttributeName>xxx</AttributeName>
    *      <AttributeValue>xxx</AttributeValue>
    *     </SessionAttribute>
    *     ...
    *    </SessionAttributes>
    *
    *    <ServerInfo>xxx</ServerInfo>
    *    <DocumentRoot>xxx</DocumentRoot>
    *    <MajorVersion>xxx</MajorVersion>
    *    <MinorVersion>xxx</MinorVersion>
    *    <ServerAttributes>
    *     <ServerAttribute>
    *      <AttributeName>xxx</AttributeName>
    *      <AttributeValue>xxx</AttributeValue>
    *     </ServerAttribute>
    *     ...
    *    </ServerAttributes>
    *    <SystemProps>
    *     <SystemProp>
    *      <PropName>xxx</PropName>
    *      <PropValue>xxx</PropValue>
    *     </SystemProp>
    *     ...
    *    </SystemProps>
    *
    *    <ServletName>xxx</ServletName>
    *    <ClassName>xxx</ClassName>
    *    <PackageName>xxx</PackageName>
    *    <InitParams>
    *     <InitParam>
    *      <ParamName>xxx</ParamName>
    *      <ParamValue>xxx</ParamValue>
    *     </InitParam>
    *     ...
    *    </InitParams>
    *    <ConfigURL>xxx</ConfigURL>
    *   </InfoAvailableToServlet>
    *</xmp>
    *@param  servlet        The HttpServlet object defining the servlet.
    *@param  request        The HttpServletRequest object of the servlet.
    *@return                The XML DOM Document
    **************************************************************************/
    public static Document getInfoAvailableToServlet
                                (HttpServlet         servlet,
                                 HttpServletRequest  request)
    {
        Document dom = XMLUtil.createEmptyDocument();
        Node xmlServlet =
                XMLUtil.appendElement(dom, "InfoAvailableToServlet");

        //--
        //-- Info about the client
        //--
        XMLUtil.appendElementContainingText
                    (xmlServlet, "RemoteUser", 
                     request.getRemoteUser());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "RemoteHost", 
                     request.getRemoteHost());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "RemoteAddr", 
                     request.getRemoteAddr());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "RemotePort", 
                     Integer.toString(request.getRemotePort()));
        XMLUtil.appendElementContainingText
                    (xmlServlet, "AuthType", 
                     request.getAuthType());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "HttpUserAgent", 
                     request.getHeader("User-Agent"));
        XMLUtil.appendElementContainingText
                    (xmlServlet, "HttpReferer", 
                     request.getHeader("Referer"));

        //--
        //-- Info about the request
        //--
        XMLUtil.appendElementContainingText
                    (xmlServlet, "Method", 
                     request.getMethod());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "FullRequestURL", 
                     getFullRequestURL(request));
        XMLUtil.appendElementContainingText
                    (xmlServlet, "RequestURL", 
                     request.getRequestURL().toString());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "Protocol", 
                     request.getProtocol());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "isSecure", 
                     new Boolean(request.isSecure()).toString());
                  //-- Note:  Don't use Boolean.toString() because it doesn't
                  //--        exist in Java 1.3.1.
        XMLUtil.appendElementContainingText
                    (xmlServlet, "Scheme", 
                     request.getScheme());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ServerName", 
                     request.getServerName());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ServerPort", 
                     Integer.toString(request.getServerPort()));
        XMLUtil.appendElementContainingText
                    (xmlServlet, "FullRequestURI", 
                     getFullRequestURI(request));
        XMLUtil.appendElementContainingText
                    (xmlServlet, "RequestURI", 
                     request.getRequestURI());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ContextPath", 
                     request.getContextPath());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ServletPath", 
                     request.getServletPath());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "PathInfo", 
                     request.getPathInfo());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "PathTranslated", 
                     request.getPathTranslated());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "QueryString", 
                     request.getQueryString());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "CharacterEncoding", 
                     request.getCharacterEncoding());
//?? Add getLocale and getLocales.
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ContentType", 
                     request.getContentType());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ContentLength", 
                     Integer.toString(request.getContentLength()));
        XMLUtil.appendElementContainingText
                    (xmlServlet, "LocalAddr", 
                     request.getLocalAddr());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "LocalName", 
                     request.getLocalName());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "LocalPort", 
                     Integer.toString(request.getLocalPort()));
        XMLUtil.appendElementContainingText
                    (xmlServlet, "HttpAccept", 
                     request.getHeader("Accept"));
        XMLUtil.appendElementContainingText
                    (xmlServlet, "RequestedSessionId", 
                     request.getRequestedSessionId());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "isRequestedSessionIdFromCookie", 
                     new Boolean(request.isRequestedSessionIdFromCookie()).toString());
                  //-- Note:  Don't use Boolean.toString() because it doesn't
                  //--        exist in Java 1.3.1.
        XMLUtil.appendElementContainingText
                    (xmlServlet, "isRequestedSessionIdFromURL", 
                     new Boolean(request.isRequestedSessionIdFromURL()).toString());
                  //-- Note:  Don't use Boolean.toString() because it doesn't
                  //--        exist in Java 1.3.1.
        XMLUtil.appendElementContainingText
                    (xmlServlet, "isRequestedSessionIdValid", 
                     new Boolean(request.isRequestedSessionIdValid()).toString());
                  //-- Note:  Don't use Boolean.toString() because it doesn't
                  //--        exist in Java 1.3.1.
        Node xmlRequestParams =
                XMLUtil.appendElement
                    (xmlServlet, "RequestParams");
        for (Enumeration enumParamNames = request.getParameterNames();
             enumParamNames.hasMoreElements();
            ) 
        {
            String strName = (String) enumParamNames.nextElement();
            Node xmlRequestParam = XMLUtil.appendElement
                    (xmlRequestParams, "RequestParam");
            XMLUtil.appendElementContainingText
                    (xmlRequestParam, "ParamName", strName);
            XMLUtil.appendElementContainingText
                    (xmlRequestParam, "ParamValue", 
                     request.getParameter(strName));
        }
        Node xmlRequestHeaders = XMLUtil.appendElement
                    (xmlServlet, "RequestHeaders");
        for (Enumeration enumHeaderNames = request.getHeaderNames();
             enumHeaderNames.hasMoreElements();
            ) 
        {
            String strName = (String) enumHeaderNames.nextElement();
            Node xmlRequestHeader = XMLUtil.appendElement
                    (xmlRequestHeaders, "RequestHeader");
            XMLUtil.appendElementContainingText
                    (xmlRequestHeader, "HeaderName", strName);
            XMLUtil.appendElementContainingText
                    (xmlRequestHeader, "HeaderValue", 
                     request.getHeader(strName));
        }
        Node xmlRequestAttributes = XMLUtil.appendElement
                    (xmlServlet, "RequestAttributes");
        for (Enumeration enumAttributeNames = request.getAttributeNames();
             enumAttributeNames.hasMoreElements();
            ) 
        {
            String strName = (String) enumAttributeNames.nextElement();
            Node xmlRequestAttribute = XMLUtil.appendElement
                    (xmlRequestAttributes, "RequestAttribute");
            XMLUtil.appendElementContainingText
                    (xmlRequestAttribute, "AttributeName", strName);
            XMLUtil.appendElementContainingText
                    (xmlRequestAttribute, "AttributeValue", 
                     request.getAttribute(strName).toString());
        }
        Node xmlRequestCookies = XMLUtil.appendElement
                    (xmlServlet, "RequestCookies");
        Cookie[] arrCookies = request.getCookies();
        for (int i = 0; 
             arrCookies != null && i < arrCookies.length; 
             i++
            ) 
        {
            Node xmlRequestCookie = XMLUtil.appendElement
                    (xmlRequestCookies, "RequestCookie");
            XMLUtil.appendElementContainingText
                    (xmlRequestCookie, "CookieName", 
                     arrCookies[i].getName());
            XMLUtil.appendElementContainingText
                    (xmlRequestCookie, "CookieValue", 
                     arrCookies[i].getValue());
            XMLUtil.appendElementContainingText
                    (xmlRequestCookie, "CookieComment", 
                     arrCookies[i].getComment());
            XMLUtil.appendElementContainingText
                    (xmlRequestCookie, "CookieDomain", 
                     arrCookies[i].getDomain());
            XMLUtil.appendElementContainingText
                    (xmlRequestCookie, "CookieMaxAge", 
                     Integer.toString(arrCookies[i].getMaxAge()));
            XMLUtil.appendElementContainingText
                    (xmlRequestCookie, "CookiePath", 
                     arrCookies[i].getPath());
            XMLUtil.appendElementContainingText
                    (xmlRequestCookie, "CookieSecure", 
                     new Boolean(arrCookies[i].getSecure()).toString());
                  //-- Note:  Don't use Boolean.toString() because it doesn't
                  //--        exist in Java 1.3.1.
            XMLUtil.appendElementContainingText
                    (xmlRequestCookie, "CookieVersion", 
                     Integer.toString(arrCookies[i].getVersion()));
        }
//?? request.getUserPrincipal()

        //--
        //-- Info about the session
        //--
        XMLUtil.appendElementContainingText
                    (xmlServlet, "SessionId", 
                     request.getSession().getId());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "SessionIsNew", 
                     new Boolean(request.getSession().isNew()).toString());
                  //-- Note:  Don't use Boolean.toString() because it doesn't
                  //--        exist in Java 1.3.1.
        XMLUtil.appendElementContainingText
                    (xmlServlet, "SessionCreationTime", 
                     Long.toString(request.getSession().getCreationTime()));
        XMLUtil.appendElementContainingText
                    (xmlServlet, "SessionLastAccessedTime", 
                     Long.toString(request.getSession().getLastAccessedTime()));
        XMLUtil.appendElementContainingText
                    (xmlServlet, "SessionMaxInactiveInterval", 
                     Integer.toString
                                (request.getSession().getMaxInactiveInterval()));
        Node xmlSessionAttributes = XMLUtil.appendElement
                    (xmlServlet, "SessionAttributes");
        for (Enumeration enumAttributeNames = 
                                    request.getSession().getAttributeNames();
             enumAttributeNames.hasMoreElements();
            ) 
        {
            String strName = (String) enumAttributeNames.nextElement();
            Node xmlSessionAttribute = XMLUtil.appendElement
                    (xmlSessionAttributes, "SessionAttribute");
            XMLUtil.appendElementContainingText
                    (xmlSessionAttribute, "AttributeName", strName);
            XMLUtil.appendElementContainingText
                    (xmlSessionAttribute, "AttributeValue", 
                     request.getSession().getAttribute(strName).toString());
        }

        //--
        //-- Info about the web application (ServletContext)
        //--
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ContextName", 
                     servlet.getServletContext().getServletContextName());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ContextDocumentRoot", 
                     servlet.getServletContext().getRealPath("/"));
        Node xmlContextAttributes = XMLUtil.appendElement
                    (xmlServlet, "ContextAttributes");
        for (Enumeration enumAttributeNames = 
                            servlet.getServletContext().getAttributeNames();
             enumAttributeNames.hasMoreElements();
            ) 
        {
            String strName = (String) enumAttributeNames.nextElement();
            Node xmlContextAttribute = XMLUtil.appendElement
                    (xmlContextAttributes, "ContextAttribute");
            XMLUtil.appendElementContainingText
                    (xmlContextAttribute, "AttributeName", strName);
            XMLUtil.appendElementContainingText
                    (xmlContextAttribute, "AttributeValue", 
                     servlet.getServletContext().getAttribute(strName).toString());
        }
        Node xmlContextInitParams = XMLUtil.appendElement
                    (xmlServlet, "ContextInitParams");
        for (Enumeration enumInitParamNames = 
                        servlet.getServletContext().getInitParameterNames();
             enumInitParamNames.hasMoreElements();
            ) 
        {
            String strName = (String) enumInitParamNames.nextElement();
            Node xmlContextInitParam = XMLUtil.appendElement
                    (xmlContextInitParams, "ContextInitParam");
            XMLUtil.appendElementContainingText
                    (xmlContextInitParam, "InitParamName", strName);
            XMLUtil.appendElementContainingText
                    (xmlContextInitParam, "InitParamValue", 
                     servlet.getServletContext().getInitParameter(strName).toString());
        }

        //--
        //-- Info about the servlet
        //--
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ServletName", 
                     servlet.getServletName());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ServletClassName", 
                     servlet.getClass().getName());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ServletPackageName", 
                     (servlet.getClass().getPackage() == null)
                     ? ""
                     : servlet.getClass().getPackage().getName()
                    );
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ServletInfo", 
                     servlet.getServletInfo());
        Node xmlInitParams = XMLUtil.appendElement
                    (xmlServlet, "ServletInitParams");
        for (Enumeration enumParamNames = servlet.getInitParameterNames();
             enumParamNames.hasMoreElements();
            ) 
        {
            String strName = (String) enumParamNames.nextElement();
            Node xmlInitParam = XMLUtil.appendElement
                    (xmlInitParams, "ServletInitParam");
            XMLUtil.appendElementContainingText
                    (xmlInitParam, "ParamName", strName);
            XMLUtil.appendElementContainingText
                    (xmlInitParam, "ParamValue", 
                     servlet.getInitParameter(strName));
        }

        //--
        //-- Info about the Web server
        //--
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ServerInfo", 
                     servlet.getServletContext().getServerInfo());
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ServletAPIMajorVersion", 
                     Integer.toString(servlet.getServletContext().getMajorVersion()));
        XMLUtil.appendElementContainingText
                    (xmlServlet, "ServletAPIMinorVersion", 
                     Integer.toString(servlet.getServletContext().getMinorVersion()));
        try
        {
            XMLUtil.appendElementContainingText
                    (xmlServlet, "LocalHostIPAddress", 
                     InetAddress.getLocalHost().getHostAddress());
        }
        catch (UnknownHostException exception)
        {
            XMLUtil.appendElementContainingText
                    (xmlServlet, "LocalHostIPAddress", 
                     "Exception: " + exception.getClass().getName());
        }
        try
        {
            XMLUtil.appendElementContainingText
                    (xmlServlet, "LocalHostName", 
                     InetAddress.getLocalHost().getHostName());
        }
        catch (UnknownHostException exception)
        {
            XMLUtil.appendElementContainingText
                    (xmlServlet, "LocalHostName", 
                     "Exception: " + exception.getClass().getName());
        }
        try
        {
            XMLUtil.appendElementContainingText
                    (xmlServlet, "LocalHostCanonicalName", 
                     InetAddress.getLocalHost().getCanonicalHostName());
        }
        catch (UnknownHostException exception)
        {
            XMLUtil.appendElementContainingText
                    (xmlServlet, "LocalHostCanonicalName", 
                     "Exception: " + exception.getClass().getName());
        }

        //--
        //-- Info about the operating system
        //--
        XMLUtil.insertBefore
                    (xmlServlet, 
                     XMLUtil.getSystemProperties(), 
                     XMLUtil.nodeINSERT_AFTER_LAST);
        
        return dom;
    }

    /**************************************************************************
    * Write as an XML stream to the HTTP client all information available to 
    * a servlet.  Format of XML is as shown in getInfoAvailableToServlet. 
    *@param  servlet        The HttpServlet object defining the servlet.
    *@param  request        The HttpServletRequest object of the servlet.
    *@param  response       The HttpServletResponse object of the servlet.
    *@throws IOException    When an I/O error occurs during interaction 
    *                       with the servlet, request, or response.
    **************************************************************************/
    public static void writeInfoAvailableToServlet
                        (HttpServlet         servlet,
                         HttpServletRequest  request,
                         HttpServletResponse response)
                throws IOException
    {
        Document dom = getInfoAvailableToServlet(servlet, request);
        PrintWriter pw = response.getWriter();
        response.setContentType("text/xml");
        XMLUtil.serialize(dom, pw).flush();
    }

    /**************************************************************************
    * Write a friendly error message to the HTTP client. 
    *@param  response   The HttpServletResponse object of the servlet.
    *@param  strMessage A String to put in front of the error message.
    *@param  throwable  The Throwable containing the error message.
    **************************************************************************/
    public static void writeFriendlyErrorMessage
                        (HttpServletResponse response,
                         String              strMessage,
                         Throwable           throwable)
    {
        //?? Incorporate an OK button and such, as in 
        //?? ErrorBO.writeErrorMessageToHttpClient()?
        try
        {
            PrintWriter pw = response.getWriter();
            response.setContentType("text/html");
            pw.println(HtmlUtil.getFriendlyErrorMessage(strMessage, throwable));
        }
        catch(Throwable t)
        {
            // Nothing to do.  Can't report the error, and it should 
            // already have been logged.
        }
    }

    /**************************************************************************
    * Set the response header to prevent caching of the page by the browser. 
    *@param  response   The HttpServletResponse object.
    **************************************************************************/
    public static void setResponseNoCache(HttpServletResponse response)
    {
        //-- Note:  Do NOT set either of the following.  They cause the 
        //--        IE5 browser to refuse to download a file (either to disk, 
        //--        or opening it into an application).  
        //--        Why?  Perhaps it views downloading the file to a 
        //--        persistent non-cache location as a form of caching, 
        //--        assuming that the no-cache or no-store directive was 
        //--        for security reasons.
        //--            response.setHeader("Cache-Control", "no-cache");
        //--            response.setHeader("Cache-Control", "no-store");
        //-- Note:  The following HTTP/1.0 approach seems to also work in
        //--        the IE5 browser.  Could use it if there is a problem
        //--        someday with setting "Cache-Control" to "max-age=0":
        //--            response.setHeader("Expires", "0");
        //--        However, it is less desirable because HTTP/1.1 agents
        //--        are supposed to treat Expires=0 the same as "no-cache",
        //--        according to RFC 2616.
        //-- Note:  I never tried the following:
        //--            response.setHeader("Cache-Control", "must-revalidate");
        //--        It may also be an acceptable alternative solution.
        response.setHeader("Cache-Control", "max-age=0");
    }

    /**************************************************************************
    * Set the response header to indentify the response content as a file 
    * download, specifying a default name for the HTTP client to use when 
    * saving the file.  When the HTTP client is a browser, it typically prompts
    * the user with a "File Download" dialog, asking whether to open the file 
    * or save it to disk.  If the user chooses to save it to disk, the browser 
    * typically pops up a "Save As" dialog, allowing the user to choose a 
    * location.  The filename specified here is the default filename used 
    * within the user specified directory.  If the user chooses to open the 
    * file, the browser typically invokes the application associated with
    * the filename extension.  For example, a ".csv" extension might cause the 
    * browser to invoke Microsoft Excel.
    *@param  response    The HttpServletResponse object.
    *@param  strFilename The name of the file.
    **************************************************************************/
    public static void setResponseDownloadFilename
                                (HttpServletResponse response,
                                 String              strFilename)
    {
        response.setHeader
                    ("Content-Disposition",
                     "attachment; filename=\"" + strFilename + "\"");
    }

    /**************************************************************************
    * Return a short human-readable abbreviation for the Web browser that is 
    * identified by the specified user agent string, which should be the full
    * string value of the HTTP "user-agent" header from a common Web browser.
    * This is useful for logging, summarizing, etc, but does not carry as 
    * much info as the full header string.  Header strings that differ in terms
    * of exact version number, OS, modes, etc., may generate the same 
    * abbreviation.    
    *@param  strHttpUserAgent   The full string value of the "user-agent" 
    *                           header from a common Web browser.
    *@return Abbreviation for the user agent string.  If the browser and/or
    *        OS is unknown, the value of strUSER_AGENT_UNKNOWN may occur as 
    *        portions of the abbreviation.    
    *        Examples:  FF2.x-Lin, SFx-Mac, UNK-Win, UNK-UNK, etc. 
    **************************************************************************/
    public static String getHttpUserAgentAbbrev(String strHttpUserAgent)
    {
        if (strHttpUserAgent == null) {
            return strHttpUserAgent;
        }
    
        // Create the lookup map if not already created.
        if (st_mapAbbrevs == null)
        {
            // Create and load the map.
            //?? Get these values from a data file somewhere?  Where?
            //??st_mapAbbrevs.put
            //??    ("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.4) " +
            //??     "Gecko/2006 1201 Firefox/2.0.0.4 (Ubuntu-feisty)"
            //??     ,"FF2-Lin");
            st_mapAbbrevs = new HashMap();
        }

        // Return the abbreviation from the map, if found.
        String strAbbrev = (String)st_mapAbbrevs.get(strHttpUserAgent);
        if (strAbbrev != null)
        {
            return strAbbrev;
        }

        // Sample values returned by request.getHeader("user-agent") for 
        // various browsers:
        // - FF1.0: Mozilla/5.0 (Windows; U;           Windows NT 5.0; en-US; rv:1.7.8)       Gecko/20050511 Firefox/1.0.4
        // - FF1.5: Mozilla/5.0 (Windows; U;           Windows NT 5.0; en-US; rv:1.8.0.12)    Gecko/20070508 Firefox/1.5.0.12
        // - FF2.0: Mozilla/5.0 (Windows; U;           Windows NT 5.0; en-US; rv:1.8.1.13)    Gecko/20080311 Firefox/2.0.0.13
        // - NS6:   Mozilla/5.0 (Windows; U;           Windows NT 5.0; en-US; rv:0.9.4.1)     Gecko/20020508 Netscape6/6.2.3
        // - NS7:   Mozilla/5.0 (Windows; U;           Windows NT 5.0; en-US; rv:1.4)         Gecko/20030624 Netscape/7.1 (ax)
        // - NS8:   Mozilla/5.0 (Windows; U;           Windows NT 5.0; en-US; rv:1.7.5)       Gecko/20051012 Netscape/8.0.4
        // - NS9:   Mozilla/5.0 (Windows; U;           Windows NT 5.0; en-US; rv:1.8.1.10pre) Gecko/20071127 Firefox/2.0.0.10 Navigator/9.0.0.4
        // - IE6.0: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322)
        
        // Search for clues, and return a best guess, if possible.
        // Conventions:
        // - "x" (not ".x") means we checked for some specific major versions 
        //   and didn't find a recognized one.  Need to add more tests.  
        // - ".x" means we checked for some specific minor versions and didn't
        //   find a recognized one.  May want to add more tests.  
        // - Missing ".x" means we didn't bother to check for minor versions.
 
        //?? Enhance this to do a better job of parsing the header string.
        //?? Something like:
        //??    if (contains "Firefox/")
        //??    {
        //??        Grab following digits as version, up to 1st or 2nd period
        //??        and or first space.
        //??    }
        //??    else if (contains "MSIE ")
        //??    {
        //??        Grab following digits as version, up to 1st or 2nd period
        //??        and or first semicolon.
        //??    }
        //??    etc...
        
        //?? Also enhance to deal with old versions of Safari that report 
        //?? Safari/312.6, but no Version/ string at all.
        
        String strAgent = strHttpUserAgent;  // To shorten the following lines. 
        String str1 = "";
        if (false);
        // Note: Check for Netscape first, since version 9.0 also reports 
        //       itself as Firefox 2.0.0.4, and we want to distinguish from
        //       native Firefox.
        else if (strAgent.indexOf("Navigator/") >= 0)
        {
            if (false);
            else if (strAgent.indexOf("Navigator/12.0")  >= 0) str1 = "NS12.0";
            else if (strAgent.indexOf("Navigator/12")    >= 0) str1 = "NS12.x";
            else if (strAgent.indexOf("Navigator/11.0")  >= 0) str1 = "NS11.0";
            else if (strAgent.indexOf("Navigator/11")    >= 0) str1 = "NS11.x";
            else if (strAgent.indexOf("Navigator/10.0")  >= 0) str1 = "NS10.0";
            else if (strAgent.indexOf("Navigator/10")    >= 0) str1 = "NS10.x";
            else if (strAgent.indexOf("Navigator/9.0")   >= 0) str1 = "NS9.0";
            else if (strAgent.indexOf("Navigator/9")     >= 0) str1 = "NS9.x";
            else                                               str1 = "NSx";
        }
        else if (strAgent.indexOf("Netscape/") >= 0)
        {
            if (false);
            else if (strAgent.indexOf("Netscape/8.0")    >= 0) str1 = "NS8.0";
            else if (strAgent.indexOf("Netscape/8")      >= 0) str1 = "NS8.x";
            else if (strAgent.indexOf("Netscape/7.2")    >= 0) str1 = "NS7.2";
            else if (strAgent.indexOf("Netscape/7.1")    >= 0) str1 = "NS7.1";
            else if (strAgent.indexOf("Netscape/7.0")    >= 0) str1 = "NS7.0";
            else if (strAgent.indexOf("Netscape/7")      >= 0) str1 = "NS7.x";
            else if (strAgent.indexOf("Netscape6/6")     >= 0) str1 = "NS6";
            else if (strAgent.indexOf("Netscape/6")      >= 0) str1 = "NS6";
            else if (strAgent.indexOf("Netscape/4")      >= 0) str1 = "NS4";
            else if (strAgent.indexOf("Netscape/3")      >= 0) str1 = "NS3";
            else                                               str1 = "NSx";
        }

        else if (strAgent.indexOf("Firefox/") >= 0)
        {
            if (false);
            else if (strAgent.indexOf("Firefox/9.0")     >= 0) str1 = "FF9.0"; 
            else if (strAgent.indexOf("Firefox/9")       >= 0) str1 = "FF9.x"; 
            else if (strAgent.indexOf("Firefox/8.0")     >= 0) str1 = "FF8.0"; 
            else if (strAgent.indexOf("Firefox/8")       >= 0) str1 = "FF8.x"; 
            else if (strAgent.indexOf("Firefox/7.0")     >= 0) str1 = "FF7.0"; 
            else if (strAgent.indexOf("Firefox/7")       >= 0) str1 = "FF7.x"; 
            else if (strAgent.indexOf("Firefox/6.0")     >= 0) str1 = "FF6.0"; 
            else if (strAgent.indexOf("Firefox/6")       >= 0) str1 = "FF6.x"; 
            else if (strAgent.indexOf("Firefox/5.0")     >= 0) str1 = "FF5.0"; 
            else if (strAgent.indexOf("Firefox/5")       >= 0) str1 = "FF5.x"; 
            else if (strAgent.indexOf("Firefox/4.0")     >= 0) str1 = "FF4.0"; 
            else if (strAgent.indexOf("Firefox/4")       >= 0) str1 = "FF4.x"; 
            else if (strAgent.indexOf("Firefox/3.6")     >= 0) str1 = "FF3.6"; 
            else if (strAgent.indexOf("Firefox/3.5")     >= 0) str1 = "FF3.5"; 
            else if (strAgent.indexOf("Firefox/3.0")     >= 0) str1 = "FF3.0"; 
            else if (strAgent.indexOf("Firefox/3")       >= 0) str1 = "FF3.x"; 
            else if (strAgent.indexOf("Firefox/2.0")     >= 0) str1 = "FF2.0"; 
            else if (strAgent.indexOf("Firefox/2")       >= 0) str1 = "FF2.x"; 
            else if (strAgent.indexOf("Firefox/1.5")     >= 0) str1 = "FF1.5";
            else if (strAgent.indexOf("Firefox/1.4")     >= 0) str1 = "FF1.4";
            else if (strAgent.indexOf("Firefox/1.0")     >= 0) str1 = "FF1.0";
            else if (strAgent.indexOf("Firefox/1")       >= 0) str1 = "FF1.x";
            else if (strAgent.indexOf("Firefox/0")       >= 0) str1 = "FF0";
            else                                               str1 = "FFx"; 
        }

        // Note: Check for Chrome before Safari, since it also reports itself
        //       as Safari.
        else if (strAgent.indexOf("Chrome/") >= 0)
        {
            if (false);
            else if (strAgent.indexOf("Chrome/20.0")       >= 0) str1 = "CH20.0";
            else if (strAgent.indexOf("Chrome/20")         >= 0) str1 = "CH20.x";
            else if (strAgent.indexOf("Chrome/19.0")       >= 0) str1 = "CH19.0";
            else if (strAgent.indexOf("Chrome/19")         >= 0) str1 = "CH19.x";
            else if (strAgent.indexOf("Chrome/18.0")       >= 0) str1 = "CH18.0";
            else if (strAgent.indexOf("Chrome/18")         >= 0) str1 = "CH18.x";
            else if (strAgent.indexOf("Chrome/17.0")       >= 0) str1 = "CH17.0";
            else if (strAgent.indexOf("Chrome/17")         >= 0) str1 = "CH17.x";
            else if (strAgent.indexOf("Chrome/16.0")       >= 0) str1 = "CH16.0";
            else if (strAgent.indexOf("Chrome/16")         >= 0) str1 = "CH16.x";
            else if (strAgent.indexOf("Chrome/15.0")       >= 0) str1 = "CH15.0";
            else if (strAgent.indexOf("Chrome/15")         >= 0) str1 = "CH15.x";
            else if (strAgent.indexOf("Chrome/14.0")       >= 0) str1 = "CH14.0";
            else if (strAgent.indexOf("Chrome/14")         >= 0) str1 = "CH14.x";
            else if (strAgent.indexOf("Chrome/13.0")       >= 0) str1 = "CH13.0";
            else if (strAgent.indexOf("Chrome/13")         >= 0) str1 = "CH13.x";
            else if (strAgent.indexOf("Chrome/12.0")       >= 0) str1 = "CH12.0";
            else if (strAgent.indexOf("Chrome/12")         >= 0) str1 = "CH12.x";
            else if (strAgent.indexOf("Chrome/11.0")       >= 0) str1 = "CH11.0";
            else if (strAgent.indexOf("Chrome/11")         >= 0) str1 = "CH11.x";
            else if (strAgent.indexOf("Chrome/10.0")       >= 0) str1 = "CH10.0";
            else if (strAgent.indexOf("Chrome/10")         >= 0) str1 = "CH10.x";
            else if (strAgent.indexOf("Chrome/9.0")        >= 0) str1 = "CH9.0";
            else if (strAgent.indexOf("Chrome/9")          >= 0) str1 = "CH9.x";
            else if (strAgent.indexOf("Chrome/8.0")        >= 0) str1 = "CH8.0";
            else if (strAgent.indexOf("Chrome/8")          >= 0) str1 = "CH8.x";
            else if (strAgent.indexOf("Chrome/7.0")        >= 0) str1 = "CH7.0";
            else if (strAgent.indexOf("Chrome/7")          >= 0) str1 = "CH7.x";
            else if (strAgent.indexOf("Chrome/6.0")        >= 0) str1 = "CH6.0";
            else if (strAgent.indexOf("Chrome/6")          >= 0) str1 = "CH6.x";
            else if (strAgent.indexOf("Chrome/5.0")        >= 0) str1 = "CH5.0";
            else if (strAgent.indexOf("Chrome/5")          >= 0) str1 = "CH5.x";
            else if (strAgent.indexOf("Chrome/4.0")        >= 0) str1 = "CH4.0";
            else if (strAgent.indexOf("Chrome/4")          >= 0) str1 = "CH4.x";
            else if (strAgent.indexOf("Chrome/3.0")        >= 0) str1 = "CH3.0";
            else if (strAgent.indexOf("Chrome/3")          >= 0) str1 = "CH3.x";
            else if (strAgent.indexOf("Chrome/2.0")        >= 0) str1 = "CH2.0";
            else if (strAgent.indexOf("Chrome/2")          >= 0) str1 = "CH2.x";
            else if (strAgent.indexOf("Chrome/1.0")        >= 0) str1 = "CH1.0";
            else if (strAgent.indexOf("Chrome/1")          >= 0) str1 = "CH1.x";
            else                                                 str1 = "CHx";
        }
        else if (strAgent.indexOf("Safari/") >= 0)
        {
            if (false);
            else if (strAgent.indexOf("Version/9.0") >= 0) str1 = "SF9.0";
            else if (strAgent.indexOf("Version/9")   >= 0) str1 = "SF9.x";
            else if (strAgent.indexOf("Version/8.0") >= 0) str1 = "SF8.0";
            else if (strAgent.indexOf("Version/8")   >= 0) str1 = "SF8.x";
            else if (strAgent.indexOf("Version/7.0") >= 0) str1 = "SF7.0";
            else if (strAgent.indexOf("Version/7")   >= 0) str1 = "SF7.x";
            else if (strAgent.indexOf("Version/6.0") >= 0) str1 = "SF6.0";
            else if (strAgent.indexOf("Version/6")   >= 0) str1 = "SF6.x";
            else if (strAgent.indexOf("Version/5.0") >= 0) str1 = "SF5.0";
            else if (strAgent.indexOf("Version/5")   >= 0) str1 = "SF5.x";
            else if (strAgent.indexOf("Version/4.0") >= 0) str1 = "SF4.0";
            else if (strAgent.indexOf("Version/4")   >= 0) str1 = "SF4.x";
            else if (strAgent.indexOf("Version/3.0") >= 0) str1 = "SF3.0";
            else if (strAgent.indexOf("Version/3")   >= 0) str1 = "SF3.x";
            else if (strAgent.indexOf("Version/2.0") >= 0) str1 = "SF2.0";
            else if (strAgent.indexOf("Version/2")   >= 0) str1 = "SF2.x";
            else if (strAgent.indexOf("Version/1.0") >= 0) str1 = "SF1.0";
            else if (strAgent.indexOf("Version/1")   >= 0) str1 = "SF1.x";
            else                                           str1 = "SFx";
        }

        else if (strAgent.indexOf("Opera/") >= 0)
        {
            if (false);
            else if (strAgent.indexOf("Opera/9")         >= 0) str1 = "OP9";
            else if (strAgent.indexOf("Opera/8")         >= 0) str1 = "OP8";
            else if (strAgent.indexOf("Opera/7")         >= 0) str1 = "OP7";
            else if (strAgent.indexOf("Opera/6")         >= 0) str1 = "OP6";
            else if (strAgent.indexOf("Opera/5")         >= 0) str1 = "OP5";
            else if (strAgent.indexOf("Opera/4")         >= 0) str1 = "OP4";
            else if (strAgent.indexOf("Opera/3")         >= 0) str1 = "OP3";
            else if (strAgent.indexOf("Opera/2")         >= 0) str1 = "OP2";
            else if (strAgent.indexOf("Opera/1")         >= 0) str1 = "OP1";
            else                                               str1 = "OPx";
        }
           
        else if (strAgent.indexOf("Konqueror")       >= 0) str1 = "KO";

        // Note: Check for AOL first, since it also reports itself as IE.
        //       but behaves differently, and we want to distinguish.
        else if (strAgent.indexOf("America Online Browser 1.1; rev1.2")  
                                                     >= 0) str1 = "AOL1.2";
        else if (strAgent.indexOf("America Online")  >= 0) str1 = "AOLx";

        else if (strAgent.indexOf("MSIE 9.0")        >= 0) str1 = "IE9.0";
        else if (strAgent.indexOf("MSIE 9")          >= 0) str1 = "IE9.x";
        else if (strAgent.indexOf("MSIE 8.0")        >= 0) str1 = "IE8.0";
        else if (strAgent.indexOf("MSIE 8")          >= 0) str1 = "IE8.x";
        else if (strAgent.indexOf("MSIE 7.0")        >= 0) str1 = "IE7.0";
        else if (strAgent.indexOf("MSIE 7")          >= 0) str1 = "IE7.x";
        else if (strAgent.indexOf("MSIE 6.0")        >= 0) str1 = "IE6.0";
        else if (strAgent.indexOf("MSIE 6")          >= 0) str1 = "IE6.x";
        else if (strAgent.indexOf("MSIE 5.5")        >= 0) str1 = "IE5.5";
        else if (strAgent.indexOf("MSIE 5.0")        >= 0) str1 = "IE5.0";
        else if (strAgent.indexOf("MSIE 5")          >= 0) str1 = "IE5.x";
        else if (strAgent.indexOf("MSIE 4")          >= 0) str1 = "IE4";
        else if (strAgent.indexOf("MSIE 3")          >= 0) str1 = "IE3";
        else if (strAgent.indexOf("MSIE")            >= 0) str1 = "IEx";

        // Note: Check for Gecko last, since lots of browsers mention Gecko,
        //       and we want to be as specific as possible.
        else if (strAgent.indexOf("Gecko")           >= 0) str1 = "GK";
        else                               str1 = strUSER_AGENT_UNKNOWN;

        String str2 = "";
        if (false);
        else if (strAgent.indexOf("Linux")           >= 0) str2 = "Lin"; 
        else if (strAgent.indexOf("Macintosh")       >= 0) str2 = "Mac"; 
        else if (strAgent.indexOf("Windows")         >= 0) str2 = "Win"; 
        else                               str2 = strUSER_AGENT_UNKNOWN;

        strAbbrev = str1 + "-" + str2;
        st_mapAbbrevs.put(strAgent, strAbbrev);
        return strAbbrev;

    }
    
    /**************************************************************************
    * Return a formatted multi-line string containing all parameter names
    * and values of the specified HttpServletRequest.    
    *@param  request    The HttpServletRequest object.
    *@return String of parameter names and values.    
    *        Example:  
    *         action:
    *          login
    *         selectedOptions:
    *          1
    *          2
    *          7
    *         username:
    *          fred 
    **************************************************************************/
    public static String getFormattedStringOfParameters
                                            (HttpServletRequest request)
    {
        StringBuffer sb = new StringBuffer();
        for (Enumeration enumNames = request.getParameterNames();
             enumNames.hasMoreElements();
            )
        {
            String strName = (String) enumNames.nextElement();
            sb.append(" " + strName + ":\n");
            String[] values = request.getParameterValues(strName);
            for (int i=0; i < values.length; i++)
            {
                sb.append("  " + values[i] + "\n");
            }
        }
        return sb.toString();
    }

    /**************************************************************************
    * Constant for use as parameter to getConfigFileName(). 
    **************************************************************************/
    public static final boolean blnUSE_PATH_TO_SERVLET_FOLDER = true;

    /**************************************************************************
    * Constant for use as parameter to getConfigFileName(). 
    **************************************************************************/
    public static final boolean blnUSE_SERVER_NAME = true;
    
    /**************************************************************************
    * Get the fully qualified filename of a config file.  The filename is 
    * built as:
    *       path_to_servlet_folder/prefixservernamesuffix
    * from 4 parts, all of which are optional:
    * <ul>
    * <li>path_to_servlet_folder/
    *     <ul>
    *     <li>Is the path to the folder in which the servlet associated with 
    *         the specified request is installed.  It always ends with a 
    *         path separator (typically "/") for the native file system, as 
    *         returned by java.io.File.separator.
    *     </li>
    *     <li>This allows different web apps, or even different versions of
    *         the same web app to be installed on the same server, each using
    *         its own config file.
    *     </li>
    *     <li>Since the config file resides in the servlet's folder, this also
    *         allows the config file to be deployed as part of a WAR file.
    *     </li>
    *     </ul>
    * </li>
    * <li>prefix
    *     <ul>
    *     <li>Is the value specified by the strPrefix param.  
    *     </li>
    *     <li>Can be any string, including one that contains path separators,
    *         (typically "/"), which allows for config files in deeply nested 
    *         subfolders of the servlet folder.
    *     </li>
    *     <li>Can even use the parent folder notation (typically "../") for 
    *         config files the reside outside the servlet folder, but can be 
    *         found relative to it.
    *     </li>
    *     </ul>
    * </li>
    * <li>servername
    *     <ul>
    *     <li>Is the name of the server, as returned by request.getServerName().
    *     </li>
    *     <li>This allows multiple config files for multiple servers (perhaps
    *          the development, test, and production servers) to be deployed 
    *          in a single web app, with each config file being found correctly.
    *     </li>
    *     </ul>
    * </li>
    * <li>suffix
    *     <ul>
    *     <li>Is the value specified by the strSuffix param.  
    *     </li>
    *     <li>Like prefix, it can be any string.
    *     </li>
    *     </ul>
    * </li>
    * </ul>
    * Examples:
    * <ul>
    * <li>getConfigFilename(request, "config" + java.io.File.separator, ".xml")
    *     <ul>
    *     <li>/usr/local/tomcat/webapps/mywebapp/config/myhostname.xml
    *     </li>
    *     </ul>
    * </li>
    * <li>getConfigFilename(request, "config.", ".xml")
    *     <ul>
    *     <li>/usr/local/tomcat/webapps/mywebapp/config.myhostname.xml
    *     </li>
    *     </ul>
    * </li>
    * <li>getConfigFilename(request, null, null)
    *     <ul>
    *     <li>/usr/local/tomcat/webapps/mywebapp/myhostname
    *     </li>
    *     </ul>
    * </li>
    * <li>getConfigFilename(request, "1/2/3/4", "567")
    *     <ul>
    *     <li>/usr/local/tomcat/webapps/mywebapp/1/2/3/4myhostname567
    *     </li>
    *     </ul>
    * </li>
    * <li>getConfigFilename(request, "/home/fred/configfile", null, 
    *                       !HttpUtil.blnUSE_PATH_TO_SERVLET_FOLDER, 
    *                       !HttpUtil.blnUSE_SERVER_NAME)
    *     <ul>
    *     <li>/home/fred/configfile
    *     </li>
    *     </ul>
    * </li>
    * </ul>
    *@param request         HttpServletRequest object.
    *@param strPrefix       The prefix to use, or null or empty string.
    *@param strSuffix       The suffix to use, or null or empty string.
    *@param blnUsePathToServletFolder
    *                       If false, the path to the servlet folder and its 
    *                       trailing path separator are omitted.
    *@param blnUseServerName
    *                       If false, servername is omitted, so the prefix
    *                       is immediately followed by the suffix.
    *@return                Fully qualified config file name.
    **************************************************************************/
    public static String getConfigFileName
                            (HttpServletRequest request
                            ,String             strPrefix
                            ,String             strSuffix
                            ,boolean            blnUsePathToServletFolder
                            ,boolean            blnUseServerName
                            )
    {
        String strFileName = "";
        if (blnUsePathToServletFolder)
        {
            strFileName += getServletDirectory(request);
        }
        if (strPrefix != null)
        {
            strFileName += strPrefix;
        }
        if (blnUseServerName)
        {
            try
            {
                strFileName += InetAddress.getLocalHost().getHostName();
            }
            catch (UnknownHostException exception)
            {
                // Note: This is really unlikely, since the exception is thrown
                //       only when this computer which presumably is currently 
                //       handling an HTTP request, has no IP address.  Still,
                //       seems useful as a way to deal with the exception on 
                //       the off chance, and a way to document the alternative.
                // Note: Try InetAddress.getLocalHost().getHostName() first, 
                //       rather than always using request.getServerName().
                //       Otherwise, we get the name of the server as passed
                //       in the URL from the HTTP client, not the name known 
                //       locally to the server.  For example, we may get 
                //       localhost, or an IP address, or perhaps even an alias 
                //       for the server defined locally to the client.
                strFileName += request.getServerName();
            }
        }
        if (strSuffix != null)
        {
            strFileName += strSuffix;
        }
        return strFileName;
    }

    /**************************************************************************
    * Get the fully qualified filename of a config file.  The filename is 
    * built as:
    *       path_to_servlet_folder/prefixservernamesuffix
    * Calls {@link #getConfigFileName(HttpServletRequest, String, String, 
    *                                boolean, boolean)} 
    * specifying true for the 2 boolean parameters.
    *
    *@param request         HttpServletRequest object.
    *@param strPrefix       The prefix to use, or null or empty string.
    *@param strSuffix       The suffix to use, or null or empty string.
    *@return                Fully qualified config file name.
    **************************************************************************/
    public static String getConfigFileName
                            (HttpServletRequest request
                            ,String             strPrefix
                            ,String             strSuffix
                            )
    {
        return getConfigFileName
                        (request
                        ,strPrefix
                        ,strSuffix
                        ,blnUSE_PATH_TO_SERVLET_FOLDER
                        ,blnUSE_SERVER_NAME
                        );
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
                System.out.println("Begin tests...");
                System.out.println("...End tests.");
            }
            catch (Throwable e)
            {
                System.out.println("Error in main(): ");
                e.printStackTrace();
            }
        }
    }
}
