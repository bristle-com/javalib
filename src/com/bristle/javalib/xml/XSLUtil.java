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

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import java.io.IOException;

import com.bristle.javalib.util.ObjUtil;

// XSLUtil
/******************************************************************************
* This class contains utility routines for working with XSL.
*<pre>
*<b>Usage:</b>
*   - Some typical scenarios for using this class are:
*
*     - To apply an XSL transformation to an XML tree:
*           xmlXML2 = XSLUtil.transform(xmlXML1, xmlXSL);
*
*     - To copy an XML tree:   
*           xml2 = XSLUtil.copy(xml1);
*
*     - To strip the comments from an XML tree:   
*           xml2 = XSLUtil.stripComments(xml1);
*
*     - To copy an XML tree into a subtree of another XML tree:   
*           XSLUtil.copy(xml1, xmlChildOfXML2);
*
*     - To sort the child Elements (in this case, the B Elements) within an 
*       XML Element (the A Element), ordering them by the value of a specified 
*       XML Element (the C Element) nested within each child Element.
*<xmp>
*           Document dom = XMLUtil.loadDocumentFromString
*                   ("<A>" +
*                      "<B>" +
*                        "<C>3</C>" +
*                      "</B>" +
*                      "<B>" +
*                        "<C>2</C>" +
*                      "</B>" +
*                      "<B>" +
*                        "<C>10</C>" +
*                      "</B>" +
*                    "</A>");
*           XSLUtil.sortElements(dom, "A", "C",
*                         XSLUtil.SortDataType.TEXT,
*                         XSLUtil.SortOrder.ASCENDING,
*                         XSLUtil.SortCaseOrder.UPPER_CASE_FIRST);
*
*       Resulting DOM: 
*                     <A>
*                       <B>
*                         <C>10</C>
*                       </B>
*                       <B>
*                         <C>2</C>
*                       </B>
*                       <B>   
*                         <C>3</C>
*                       </B>
*                     </A>
*</xmp>
*
*     - You can also sort numerically, instead of alphabetically.
*       To sort the child Elements (in this case, the B Elements) within an 
*       XML Element (the A Element), ordering them by the numeric value of a 
*       specified XML Element (the C Element) nested within each child Element.
*<xmp>
*           Document dom = XMLUtil.loadDocumentFromString
*                   ("<A>" +
*                      "<B>" +
*                        "<C>3</C>" +
*                      "</B>" +
*                      "<B>" +
*                        "<C>2</C>" +
*                      "</B>" +
*                      "<B>" +
*                        "<C>10</C>" +
*                      "</B>" +
*                    "</A>");
*           XSLUtil.sortElements(dom, "A", "C",
*                         XSLUtil.SortDataType.NUMBER,
*                         XSLUtil.SortOrder.ASCENDING,
*                         XSLUtil.SortCaseOrder.UPPER_CASE_FIRST);
*
*       Resulting DOM: 
*                     <A>
*                       <B>
*                         <C>2</C>
*                       </B>
*                       <B>   
*                         <C>3</C>
*                       </B>
*                       <B>
*                         <C>10</C>
*                       </B>
*                     </A>
*</xmp>
* 
*     - You can sort by attribute value, instead of by nested Element value.
*       To sort the child Elements (in this case, the B Elements) within an 
*       XML Element (the A Element), ordering them by the value of a 
*       specified XML Attribute (the attr1 Attribute) of each child Element.
*<xmp>
*           Document dom = XMLUtil.loadDocumentFromString
*                   ("<A>" +
*                      "<B attr1='3'>" +
*                        "<C>some text</C>" +
*                      "</B>" +
*                      "<B attr1='2'>" +
*                        "<C>some more text</C>" +
*                      "</B>" +
*                      "<B attr1='10'>" +
*                        "<C>yet more text</C>" +
*                      "</B>" +
*                    "</A>");
*           XSLUtil.sortElements(dom, "A", "@attr1",
*                         XSLUtil.SortDataType.TEXT,
*                         XSLUtil.SortOrder.ASCENDING,
*                         XSLUtil.SortCaseOrder.UPPER_CASE_FIRST);
*
*       Resulting DOM: 
*                     <A>
*                       <B attr1='10'>
*                         <C>yet more text</C>
*                       </B>
*                       <B attr1='2'>   
*                         <C>some more text</C>
*                       </B>
*                       <B attr1='3'>
*                         <C>some text</C>
*                       </B>
*                     </A>
*</xmp>
*
*     - The sorted child Elements need not all be the same type of Element.  
*       To sort the child Elements (in this case, the B and Z Elements) 
*       within an XML Element (the A Element), ordering them by the value of 
*       a specified XML Element (the C Element) nested within each child 
*       Element.
*<xmp>
*           Document dom = XMLUtil.loadDocumentFromString
*                   ("<A>" +
*                      "<B>" +
*                        "<C>3</C>" +
*                      "</B>" +
*                      "<Z>" +
*                        "<C>2</C>" +
*                      "</Z>" +
*                      "<B>" +
*                        "<C>10</C>" +
*                      "</B>" +
*                    "</A>");
*           XSLUtil.sortElements(dom, "A", "C",
*                         XSLUtil.SortDataType.TEXT,
*                         XSLUtil.SortOrder.ASCENDING,
*                         XSLUtil.SortCaseOrder.UPPER_CASE_FIRST);
*
*       Resulting DOM: 
*                     <A>
*                       <B>
*                         <C>10</C>
*                       </B>
*                       <Z>
*                         <C>2</C>
*                       </Z>
*                       <B>   
*                         <C>3</C>
*                       </B>
*                     </A>
*</xmp>
*
*     - You can also sort the children of multiple parents at the same time.
*       In the above examples, if there had been multiple A Elements in the 
*       XML tree (even at different nesting levels), each would have had its
*       child Elements sorted within it.  In such a case, you can specify a 
*       more specific parent XPath, like "A[2]" to limit the sort to a 
*       single parent.
*
*     - You can use any valid XPath to specify the parents, not just the 
*       simplistic "A" and "A[2]" examples above.    
*
*     - You can use any valid XPath to specify the sort key, not just the 
*       simplistic "C" and "@attr1" examples above.    
*
*   - See the source code of the inner Tester class for more examples.
*  
*<b>Assumptions:</b>
*<b>Effects:</b>
*       - None.
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*       - Pay particular attention to the distinction between the different
*         XML DOM node types.  The names and comments in this class use these
*         different terms very precisely: 
*           Document    An entire XML DOM document
*           DocumentFragment    
*                       A fragment of an XML document.
*           Element     An XML element or tag (an item in angle brackets)
*           Text        A sequence of text stored in an Element
*           Node        Could be any of the above, or an XML attribute, CDATA,
*                       comment, document type, entity, etc.
*         See the org.w3c.dom.Node documentation for details.            
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class XSLUtil
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

    /**************************************************************************
    * Returns an XSL stylesheet as a String, wrapping the syntax for the 
    * required xsl:stylesheet node around the specified XML string which may 
    * be one or more xsl:templates, or any other valid XSL syntax. 
    *@return The XSL stylesheet string
    **************************************************************************/
    public static String wrapInXSLStylesheet(String strXSL)
    {
        return 
            "<?xml version='1.0' ?>\n"
          + "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'\n"
          + "                version='1.0'>\n"
          + strXSL
          + "</xsl:stylesheet>\n"
          ;
    }

    /**************************************************************************
    * Returns the identity transformation template as a string of XSL.  Unlike
    * getIdentityTransformationStylesheetString(), this is not a complete XSL 
    * stylesheet.  It is only an XSL template subtree.  That is, its root 
    * node is xsl:template, not xsl:stylesheet.  Therefore, it cannot be used 
    * as a standalone XSL stylesheet.  However, it can be combined with
    * other strings of XSL to produce a standalone XSL stylesheet.  When that
    * stylesheet is applied to an XML tree, this template will cause XML nodes
    * that are not matched by more specific templates of the stylesheet to be
    * copied unchanged. 
    *@return The XSL transformation template string
    *@see #wrapInXSLStylesheet
    **************************************************************************/
    public static String getIdentityTransformationTemplateString()
    {
        return 
            "  <!-- Identity transformation - Copy all XML constructs\n"
          + "       not mentioned more explicitly later in the same\n"
          + "       transformation.  -->\n"
          + "  <xsl:template match='@*|*|text()|processing-instruction()|comment()'>\n"
          + "   <xsl:copy>\n"
          + "    <xsl:apply-templates select='@*|*|text()|processing-instruction()|comment()'/>\n"
          + "   </xsl:copy>\n"
          + "  </xsl:template>\n"
          ;
    }

    /**************************************************************************
    * Returns the identity transformation stylesheet as a string of XSL.
    * This stylesheet can be applied to an XML tree, causing all XML nodes
    * to be copied unchanged. 
    *@return The XSL transformation stylesheet string
    **************************************************************************/
    public static String getIdentityTransformationStylesheetString()
    {
        return wrapInXSLStylesheet(getIdentityTransformationTemplateString());
    }

    /**************************************************************************
    * Returns the identity transformation stylesheet as an XML Document.
    * This stylesheet can be applied to an XML tree, causing all XML nodes
    * to be copied unchanged. 
    *@return The XSL transformation stylesheet Document.
    *@throws SAXException   Should never happen, unless there is an error in
    *                       this class.  
    *@throws IOException    Should never happen, unless there is an error in
    *                       this class.    
    **************************************************************************/
    public static Document getIdentityTransformationStylesheetDocument()
                            throws SAXException,
                                   IOException
    {
        return XMLUtil.loadDocumentFromString
                                (getIdentityTransformationStylesheetString()); 
    }

    /**************************************************************************
    * Returns a "strip comments" transformation template as a string of XSL.
    * Unlike getStripCommentsTransformationStylesheetString(), this is not a 
    * complete XSL stylesheet.  It is only an XSL template subtree.  That is, 
    * its root node is xsl:template, not xsl:stylesheet.  Therefore, it cannot 
    * be used as a standalone XSL stylesheet.  However, it can be combined with
    * other strings of XSL to produce a standalone XSL stylesheet.  When that
    * stylesheet is applied to an XML tree, this template will cause all XML 
    * non-comment nodes that are not matched by more specific templates of the 
    * stylesheet to be copied unchanged, but comments will be omitted. 
    *@return The XSL transformation template string
    *@see #wrapInXSLStylesheet
    **************************************************************************/
    public static String getStripCommentsTransformationTemplateString()
    {
        return 
            "  <!-- StripComments transformation - Copy all XML constructs\n"
          + "       not mentioned more explicitly later in the same\n"
          + "       transformation, except for comments, which are omitted.  -->\n"
          + "  <xsl:template match='@*|*|text()|processing-instruction()'>\n"
          + "   <xsl:copy>\n"
          + "    <xsl:apply-templates select='@*|*|text()|processing-instruction()|comment()'/>\n"
          + "   </xsl:copy>\n"
          + "  </xsl:template>\n"
          ;
    }

    /**************************************************************************
    * Returns a "strip comments" transformation stylesheet as a string of XSL.
    * This stylesheet can be applied to an XML tree, causing all non-comment 
    * XML nodes to be copied unchanged, but omitting comments. 
    *@return The XSL transformation stylesheet string
    **************************************************************************/
    public static String getStripCommentsTransformationStylesheetString()
    {
        return wrapInXSLStylesheet(getStripCommentsTransformationTemplateString());
    }

    /**************************************************************************
    * Returns a "strip comments" transformation stylesheet as an XML Document.
    * This stylesheet can be applied to an XML tree, causing all non-comment 
    * XML nodes to be copied unchanged, but omitting comments. 
    *@return The XSL transformation stylesheet Document.
    *@throws SAXException   Should never happen, unless there is an error in
    *                       this class.  
    *@throws IOException    Should never happen, unless there is an error in
    *                       this class.    
    **************************************************************************/
    public static Document getStripCommentsTransformationStylesheetDocument()
                            throws SAXException,
                                   IOException
    {
        return XMLUtil.loadDocumentFromString
                            (getStripCommentsTransformationStylesheetString()); 
    }

    /**************************************************************************
    * This class represents an enumerated type that identifies data types to
    * sort by.  
    * A number sort would produce:  2, 3, 10.  
    * A text sort would produce:  10, 2, 3.
    *<pre>
    *<b>Usage:</b>
    *   - The following are typical scenarios for using this class:
    *       XSLUtil.SortDataType dataType = XSLUtil.SortDataType.NUMBER;
    *       if (dataType == XSLUtil.SortDataType.TEXT) ...
    *       aCallToSomeMethod(..., XSLUtil.SortDataType.NUMBER, ...);
    *</pre>
    **************************************************************************/
    public static class SortDataType
    {
        public final static SortDataType TEXT   = new SortDataType("TEXT");
        public final static SortDataType NUMBER = new SortDataType("NUMBER");
        private final String m_strName;  //-- "blank final" assigned in the ctor.
        //-- Note:  No public ctor so new values cannot be created.  Those
        //--        listed above are the only ones.
        private SortDataType(String strName) { m_strName = strName; }
        public String toString() { return m_strName; }
        public static class UnexpectedEnumValueException extends Exception
        {
            private static final long serialVersionUID = 1L;
            public UnexpectedEnumValueException(String msg) { super(msg); }
        }
    }

    /**************************************************************************
    * This class represents an enumerated type that identifies sort orders
    * (ascending and descending).
    *<pre>
    *<b>Usage:</b>
    *   - The following are typical scenarios for using this class:
    *       XSLUtil.SortOrder order = XSLUtil.SortOrder.DESCENDING;
    *       if (order == XSLUtil.SortOrder.ASCENDING) ...
    *       aCallToSomeMethod(..., XSLUtil.SortOrder.DESCENDING, ...);
    *</pre>
    **************************************************************************/
    public static class SortOrder
    {
        public final static SortOrder ASCENDING  = new SortOrder("ASCENDING");
        public final static SortOrder DESCENDING = new SortOrder("DESCENDING");
        private final String m_strName;  //-- "blank final" assigned in the ctor.
        //-- Note:  No public ctor so new values cannot be created.  Those
        //--        listed above are the only ones.
        private SortOrder(String strName) { m_strName = strName; }
        public String toString() { return m_strName; }
        public static class UnexpectedEnumValueException extends Exception
        {
            private static final long serialVersionUID = 1L;
            public UnexpectedEnumValueException(String msg) { super(msg); }
        }
    }

    /**************************************************************************
    * This class represents an enumerated type that identifies sort case orders
    * (upper case first, lower case first, case insensitive).
    *<pre>
    *<b>Usage:</b>
    *   - The following are typical scenarios for using this class:
    *       XSLUtil.SortCaseOrder order = XSLUtil.SortCaseOrder.LOWER_CASE_FIRST;
    *       if (order == XSLUtil.SortCaseOrder.UPPER_CASE_FIRST) ...
    *       aCallToSomeMethod(..., XSLUtil.SortCaseOrder.CASE_INSENSITIVE, ...);
    *</pre>
    **************************************************************************/
    public static class SortCaseOrder
    {
        public final static SortCaseOrder UPPER_CASE_FIRST = 
                                        new SortCaseOrder("UPPER_CASE_FIRST");
        public final static SortCaseOrder LOWER_CASE_FIRST = 
                                        new SortCaseOrder("LOWER_CASE_FIRST");
        public final static SortCaseOrder CASE_INSENSITIVE = 
                                        new SortCaseOrder("CASE_INSENSITIVE");
        private final String m_strName;  //-- "blank final" assigned in the ctor.
        //-- Note:  No public ctor so new values cannot be created.  Those
        //--        listed above are the only ones.
        private SortCaseOrder(String strName) { m_strName = strName; }
        public String toString() { return m_strName; }
        public static class UnexpectedEnumValueException extends Exception
        {
            private static final long serialVersionUID = 1L;
            public UnexpectedEnumValueException(String msg) { super(msg); }
        }
    }

    /**************************************************************************
    * Returns a sortElements transformation template as a string of XSL.
    * Unlike getSortElementsTransformationStylesheetString(), this is not a 
    * complete XSL stylesheet.  It is only an XSL template subtree.  That is, 
    * its root node is xsl:template, not xsl:stylesheet.  Therefore, it cannot 
    * be used as a standalone XSL stylesheet.  However, it can be combined with
    * other strings of XSL to produce a standalone XSL stylesheet.  When that
    * stylesheet is applied to an XML tree, this template will sort the child
    * XML Elements within each specified parent XML Element. 
    *@param strParentElementXPath
    *                           XPath of parent Elements whose child Elements 
    *                           are to be sorted
    *@param strSortKeyXPath     XPath of XML Node to be used as the sort key 
    *                           within each child Element to be sorted.
    *@param dataType            Data type of the sort key
    *@param order               Order of the sort
    *@param caseOrder           Effect of case on the sort order
    *@return The XSL transformation template string
    *@see #wrapInXSLStylesheet
    **************************************************************************/
    public static String getSortElementsTransformationTemplateString
                            (String        strParentElementXPath,
                             String        strSortKeyXPath,
                             SortDataType  dataType,
                             SortOrder     order,
                             SortCaseOrder caseOrder)
    {
        String strDataType = "text";
        if (dataType == SortDataType.NUMBER)
        {
            strDataType = "number";
        }
        strDataType = " data-type='" + strDataType + "'";

        String strOrder = "ascending";
        if (order == SortOrder.DESCENDING)
        {
            strOrder = "descending";
        }
        strOrder = " order='" + strOrder + "'";

        String strCaseOrder = " select='" + strSortKeyXPath + "'" +
                              " case-order='upper-first'";
        if (caseOrder == SortCaseOrder.LOWER_CASE_FIRST)
        {
            strCaseOrder = " select='" + strSortKeyXPath + "'" +
                           " case-order='lower-first'";
        }
        else if (caseOrder == SortCaseOrder.CASE_INSENSITIVE)
        {
            strCaseOrder = " select='translate(\"" + strSortKeyXPath + "\"," +
                                               "\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\"," +
                                               "\"abcdefghijklmnopqrstuvwxyz\")'";
        }

        return 
            // Return the identity template followed by the sort template,
            // so that things irrelevant to the sort are copied unchanged.
            getIdentityTransformationTemplateString()
          + "  <!-- Sorting transformation -->\n"
          + "  <xsl:template match='" + strParentElementXPath + "'>\n"
          + "   <xsl:copy>\n"
          + "    <!-- Copy all child Nodes except Elements unchanged. -->\n"
          + "    <xsl:apply-templates "
          +         "select='@*|text()|processing-instruction()|comment()'/>\n"
          + "    <!-- Copy Elements in sorted order. -->\n"
          + "    <xsl:apply-templates select='*'>"
          + "     <xsl:sort " + strCaseOrder + strDataType + strOrder + " />\n"
          + "    </xsl:apply-templates>\n"
          + "   </xsl:copy>\n"
          + " </xsl:template>\n"
          ;
    }

    /**************************************************************************
    * Returns a sort transformation stylesheet as a string of XSL.
    * This stylesheet can be applied to an XML tree, sorting the child XML
    * Elements within each specified parent XML Element.
    *@param strParentElementXPath
    *                           XPath of parent Elements whose child Elements 
    *                           are to be sorted
    *@param strSortKeyXPath     XPath of XML Node to be used as the sort key 
    *                           within each Element to be sorted.
    *@param dataType            Data type of the sort key
    *@param order               Order of the sort
    *@param caseOrder           Effect of case on the sort order
    *@return The XSL transformation stylesheet string
    **************************************************************************/
    public static String getSortElementsTransformationStylesheetString
                            (String        strParentElementXPath,
                             String        strSortKeyXPath,
                             SortDataType  dataType,
                             SortOrder     order,
                             SortCaseOrder caseOrder)
    {
        return wrapInXSLStylesheet
                (getSortElementsTransformationTemplateString
                            (strParentElementXPath, 
                             strSortKeyXPath, 
                             dataType, 
                             order, 
                             caseOrder));
    }

    /**************************************************************************
    * Returns a sort transformation stylesheet as an XML Document.
    * This stylesheet can be applied to an XML tree, sorting the child XML
    * Elements within each specified parent XML Element.
    *@param strParentElementXPath
    *                           XPath of parent Elements whose child Elements 
    *                           are to be sorted
    *@param strSortKeyXPath     XPath of XML Node to be used as the sort key 
    *                           within each Element to be sorted.
    *@param dataType            Data type of the sort key
    *@param order               Order of the sort
    *@param caseOrder           Effect of case on the sort order
    *@return The XSL transformation stylesheet Document.
    *@throws SAXException   Should never happen, unless there is an error in
    *                       this class.  
    *@throws IOException    Should never happen, unless there is an error in
    *                       this class.    
    **************************************************************************/
    public static Document getSortElementsTransformationStylesheetDocument
                            (String        strParentElementXPath,
                             String        strSortKeyXPath,
                             SortDataType  dataType,
                             SortOrder     order,
                             SortCaseOrder caseOrder)
                            throws SAXException,
                                   IOException
    {
        return XMLUtil.loadDocumentFromString
                (getSortElementsTransformationStylesheetString
                            (strParentElementXPath, 
                             strSortKeyXPath, 
                             dataType, 
                             order, 
                             caseOrder));
    }

    /**************************************************************************
    * Applies the specified XSL transformation to the specified XML Document,
    * DocumentFragment or Element, leaving the original XML untouched, storing 
    * the generated XML in the specified DOMResult (which should refer to an 
    * XML Document, DocumentFragment or Element), and returning the generated
    * XML as a Node.  Can safely be called to update a Document in place as:
    *          xmlXML1 = transform(xmlXML1, xmlXSL, new DOMResult());
    * Can also safely be called to insert new nodes into the source XML by  
    * passing a child of the source XML as the target.  For example:
    *          transform(xmlXML1, xmlXSL, new DOMResult(xmlChildNodeOfXML1));
    *@param  xmlXML     XML to be transformed
    *@param  xmlXSL     XSL transformation
    *@param  xmlResult  The XML Document, DocumentFragment, or Element in
    *                   which to store the generated XML.
    *@throws TransformerConfigurationException
    *                   When the Transformer cannot be constructed.
    *@throws TransformerException               
    *                   When the transformation fails.
    *@return            The transformed XML
    **************************************************************************/
    public static Node transform(Node xmlXML, Node xmlXSL, DOMResult xmlResult)
                        throws TransformerConfigurationException,
                               TransformerException                               
    
    {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new DOMSource(xmlXSL));
        //?? In Java 5.0, using Apache Xalan for transformations, when the 
        //?? following are in the CLASSPATH:
        //??    C:\Apps\Apache\xalan-j_2_7_1\xercesImpl.jar
        //??    C:\Apps\Apache\xalan-j_2_7_1\xml-apis.jar        
        //?? this call to newTransformer() writes warnings and errors to 
        //?? stdout, and doesn't throw an exception on warnings.  
        //??
        //?? For errors, the stdout text looks like:
        //??    ERROR:  'Unsupported XSL element 'xxxsort'.'
        //??    FATAL ERROR:  'Could not compile stylesheet'
        //?? and the value of TransformerConfigurationException.getMessage() 
        //?? is simply:
        //??    Could not compile stylesheet
        //?? instead of:
        //??    javax.xml.transform.TransformerConfigurationException: 
        //??    javax.xml.transform.TransformerException: 
        //??    javax.xml.transform.TransformerException: 
        //??    xsl:xxxsort is not allowed in this position in the stylesheet!
        //??
        //?? For warnings, the stdout text looks like:
        //??    Compiler warnings:
        //??      Illegal attribute 'cxxxase-order'.
        //?? and no exception is thrown instead of it throwing 
        //??    TransformerConfigurationException:
        //?? with the value of TransformerConfigurationException.getMessage() 
        //?? being: 
        //??    javax.xml.transform.TransformerConfigurationException: 
        //??    javax.xml.transform.TransformerException: 
        //??    javax.xml.transform.TransformerException: 
        //??    "cxxxase-order" attribute is not allowed on the xsl:sort element!
        //?? 
        //?? When xalan.jar precedes them in the CLASSPATH:
        //??    C:\Apps\Apache\xalan-j_2_7_1\xalan.jar
        //??    C:\Apps\Apache\xalan-j_2_7_1\xercesImpl.jar
        //??    C:\Apps\Apache\xalan-j_2_7_1\xml-apis.jar        
        //?? there is no exception on errors either, and the stdout text looks
        //?? like:
        //??    D:\Fred\bristle\javaapps\xmltrans\Testing\Actual\dummy.xsl; 
        //??    Line #0; Column #0; xsl:xxxsort is not allowed in this position 
        //??    in the stylesheet!
        //?? or:
        //??    D:\Fred\bristle\javaapps\xmltrans\Testing\Actual\dummy.xsl; 
        //??    Line #0; Column #0; "cxxxase-order" attribute is not allowed 
        //??    on the xsl:sort element!
        //??
        //?? Should find a better parser perhaps.
        //??
        transformer.transform(new DOMSource(xmlXML), xmlResult);
        return xmlResult.getNode();
    }

    /**************************************************************************
    * Applies the specified XSL transformation to the specified XML Document,
    * DocumentFragment or Element, leaving the original XML untouched, storing 
    * the generated XML in the specified result Node (which should refer to an 
    * XML Document, DocumentFragment or Element), and returning the generated
    * XML as a Node.  Can safely be called to update a Document in place as:
    *          xmlXML1 = transform(xmlXML1, xmlXSL, null);
    * Can also safely be called to insert new nodes into the source XML by  
    * passing a child of the source XML as the target.  For example:
    *          transform(xmlXML1, xmlXSL, xmlChildNodeOfXML1);
    *@param  xmlXML     XML to be transformed
    *@param  xmlXSL     XSL transformation
    *@param  xmlResult  The XML Document, DocumentFragment, or Element in
    *                   which to store the generated XML.
    *@throws TransformerConfigurationException
    *                   When the Transformer cannot be constructed.
    *@throws TransformerException               
    *                   When the transformation fails.
    *@return            The transformed XML
    **************************************************************************/
    public static Node transform(Node xmlXML, Node xmlXSL, Node xmlResult)
                        throws TransformerConfigurationException,
                               TransformerException                               
    
    {
        DOMResult domResult = xmlResult == null 
                              ? new DOMResult()
                              : new DOMResult(xmlResult);
        return transform(xmlXML, xmlXSL, domResult); 
    }

    /**************************************************************************
    * Applies the specified XSL transformation to the specified XML Document,
    * DocumentFragment or Element, leaving the original XML untouched, and 
    * returning a new Document.  Can also safely be called to update a 
    * Document in place as:
    *          xmlXML = transform(xmlXML, xmlXSL);
    *@param  xmlXML     XML to be transformed
    *@param  xmlXSL     XSL transformation
    *@throws TransformerConfigurationException
    *                   When the Transformer cannot be constructed.
    *@throws TransformerException               
    *                   When the transformation fails.
    *@return            The transformed XML
    **************************************************************************/
    public static Document transform(Node xmlXML, Node xmlXSL)
                        throws TransformerConfigurationException,
                               TransformerException                               
    
    {
        return XMLUtil.getOwnerDocument(transform(xmlXML, xmlXSL, (Node)null)); 
    }

    /**************************************************************************
    * Uses an XSL transformation to copy the specified XML Document,
    * DocumentFragment or Element to the specified DOMResult (which should 
    * refer to an XML Document, DocumentFragment or Element), and returning 
    * the generated XML as a Node.  Can safely be called to copy a Document 
    * onto itself as:
    *          xmlXML1 = copy(xmlXML1, new DOMResult());
    * Can also safely be called to insert a copy of the XML tree into a branch 
    * of the XML tree by passing a child of the source XML as the target.  
    * For example:
    *          copy(xmlXML1, new DOMResult(xmlChildNodeOfXML1));
    *@param  xmlXML     XML to be transformed
    *@param  xmlResult  The XML Document, DocumentFragment, or Element in
    *                   which to store the generated XML.
    *@return            A copy of the XML.
    *@throws SAXException   Should never happen, unless there is an error in
    *                       this class.  
    *@throws IOException    Should never happen, unless there is an error in
    *                       this class.    
    *@throws TransformerConfigurationException
    *                       When the Transformer cannot be constructed.
    *@throws TransformerException               
    *                       When the transformation fails.
    **************************************************************************/
    public static Node copy(Node xmlXML, DOMResult xmlResult)
                            throws SAXException,
                                   IOException,
                                   TransformerConfigurationException,
                                   TransformerException               
    {
        return transform
                    (xmlXML, 
                     getIdentityTransformationStylesheetDocument(),
                     xmlResult);
    }

    /**************************************************************************
    * Uses an XSL transformation to copy the specified XML Document,
    * DocumentFragment or Element to the specified Node (which should be an  
    * XML Document, DocumentFragment or Element), and returning the generated 
    * XML as a Node.  Can safely be called to copy a Document onto itself as:
    *          xmlXML1 = copy(xmlXML1, null);
    * Can also safely be called to insert a copy of the XML tree into a branch 
    * of the XML tree by passing a child of the source XML as the target.  
    * For example:
    *          copy(xmlXML1, xmlChildNodeOfXML1);
    *@param  xmlXML     XML to be transformed
    *@param  xmlResult  The XML Document, DocumentFragment, or Element in
    *                   which to store the generated XML.
    *@return            A copy of the XML.
    *@throws SAXException   Should never happen, unless there is an error in
    *                       this class.  
    *@throws IOException    Should never happen, unless there is an error in
    *                       this class.    
    *@throws TransformerConfigurationException
    *                       When the Transformer cannot be constructed.
    *@throws TransformerException               
    *                       When the transformation fails.
    **************************************************************************/
    public static Node copy(Node xmlXML, Node xmlResult)
                            throws SAXException,
                                   IOException,
                                   TransformerConfigurationException,
                                   TransformerException               
    {
        DOMResult domResult = xmlResult == null 
                              ? new DOMResult()
                              : new DOMResult(xmlResult);
        return copy(xmlXML, domResult); 
    }

    /**************************************************************************
    * Uses an XSL transformation to copy the specified XML Document,
    * DocumentFragment or Element to a new Document.  Can also safely be 
    * called to update a Document in place as:
    *          xmlXML = copy(xmlXML);
    *@param  xml            XML to be transformed
    *@return                A copy of the XML.
    *@throws SAXException   Should never happen, unless there is an error in
    *                       this class.  
    *@throws IOException    Should never happen, unless there is an error in
    *                       this class.    
    *@throws TransformerConfigurationException
    *                       When the Transformer cannot be constructed.
    *@throws TransformerException               
    *                       When the transformation fails.
    **************************************************************************/
    public static Document copy(Node xml)
                            throws SAXException,
                                   IOException,
                                   TransformerConfigurationException,
                                   TransformerException               
    {
        return XMLUtil.getOwnerDocument(copy(xml, (Node)null));
    }

    /**************************************************************************
    * Uses an XSL transformation to copy the specified XML Document,
    * DocumentFragment or Element to the specified DOMResult (which should 
    * refer to an XML Document, DocumentFragment or Element), but stripping 
    * the comments out of the generated copy, and returning the generated XML 
    * as a Node.  Can safely be called to copy a Document onto itself as:
    *          xmlXML1 = stripComments(xmlXML1, new DOMResult());
    * Can also safely be called to insert a comment-stripped copy of the XML 
    * tree into a branch of the XML tree by passing a child of the source XML 
    * as the target.  For example:
    *          stripComments(xmlXML1, new DOMResult(xmlChildNodeOfXML1));
    *@param  xmlXML     XML to be transformed
    *@param  xmlResult  The XML Document, DocumentFragment, or Element in
    *                   which to store the generated XML.
    *@return            A copy of the XML, with comments stripped.
    *@throws SAXException   Should never happen, unless there is an error in
    *                       this class.  
    *@throws IOException    Should never happen, unless there is an error in
    *                       this class.    
    *@throws TransformerConfigurationException
    *                       When the Transformer cannot be constructed.
    *@throws TransformerException               
    *                       When the transformation fails.
    **************************************************************************/
    public static Node stripComments(Node xmlXML, DOMResult xmlResult)
                            throws SAXException,
                                   IOException,
                                   TransformerConfigurationException,
                                   TransformerException               
    {
        return transform
                    (xmlXML, 
                     getStripCommentsTransformationStylesheetDocument(),
                     xmlResult);
    }

    /**************************************************************************
    * Uses an XSL transformation to copy the specified XML Document,
    * DocumentFragment or Element to the specified Node (which should be an  
    * XML Document, DocumentFragment or Element), but stripping the comments 
    * out of the generated copy, and returning the generated XML as a Node.  
    * Can safely be called to copy a Document onto itself as:
    *          xmlXML1 = stripComments(xmlXML1, null);
    * Can also safely be called to insert a comment-stripped copy of the XML 
    * tree into a branch of the XML tree by passing a child of the source XML 
    * as the target.  For example:
    *          stripComments(xmlXML1, xmlChildNodeOfXML1);
    *@param  xmlXML     XML to be transformed
    *@param  xmlResult  The XML Document, DocumentFragment, or Element in
    *                   which to store the generated XML.
    *@return            A copy of the XML, with comments stripped.
    *@throws SAXException   Should never happen, unless there is an error in
    *                       this class.  
    *@throws IOException    Should never happen, unless there is an error in
    *                       this class.    
    *@throws TransformerConfigurationException
    *                       When the Transformer cannot be constructed.
    *@throws TransformerException               
    *                       When the transformation fails.
    **************************************************************************/
    public static Node stripComments(Node xmlXML, Node xmlResult)
                            throws SAXException,
                                   IOException,
                                   TransformerConfigurationException,
                                   TransformerException               
    {
        DOMResult domResult = xmlResult == null 
                              ? new DOMResult()
                              : new DOMResult(xmlResult);
        return stripComments(xmlXML, domResult); 
    }

    /**************************************************************************
    * Uses an XSL transformation to copy the specified XML Document,
    * DocumentFragment or Element to a new Document, but stripping 
    * the comments out of the generated copy.  Can also safely be 
    * called to update a Document in place as:
    *          xmlXML = stripComments(xmlXML);
    *@param  xml            XML to be transformed
    *@return                A copy of the XML, with comments stripped.
    *@throws SAXException   Should never happen, unless there is an error in
    *                       this class.  
    *@throws IOException    Should never happen, unless there is an error in
    *                       this class.    
    *@throws TransformerConfigurationException
    *                       When the Transformer cannot be constructed.
    *@throws TransformerException               
    *                       When the transformation fails.
    **************************************************************************/
    public static Document stripComments(Node xml)
                            throws SAXException,
                                   IOException,
                                   TransformerConfigurationException,
                                   TransformerException               
    {
        return XMLUtil.getOwnerDocument(stripComments(xml, (Node)null));
    }

    /**************************************************************************
    * Sorts the child Elements of the specified Elements of the specified XML 
    * Document, DocumentFragment or Element, ordering them by the value of the 
    * specified XML Element nested within each child Element, leaving the 
    * original XML unmodified, copying the sorted XML to the specified 
    * DOMResult (which should refer to an XML Document, DocumentFragment or 
    * Element), and returning the sorted XML as a Node.  Can safely be called 
    * to sort a Document onto itself as:  
    *          xmlXML1 = sortElements
    *                       (xmlXML1, 
    *                        strElementName, 
    *                        strKey,
    *                        XSLUtil.SortDataType.TEXT,
    *                        XSLUtil.SortOrder.ASCENDING,
    *                        XSLUtil.SortCaseOrder.UPPER_CASE_FIRST,
    *                        new DOMResult());
    * Can also safely be called to insert a sorted copy of the XML tree into 
    * a branch of the XML tree by passing a child of the source XML as the 
    * target.  For example:
    *                   sortElements
    *                       (xmlXML1, 
    *                        strElementName, 
    *                        strKey, 
    *                        XSLUtil.SortDataType.TEXT,
    *                        XSLUtil.SortOrder.ASCENDING,
    *                        XSLUtil.SortCaseOrder.UPPER_CASE_FIRST,
    *                        new DOMResult(xmlChildNodeOfXML1));
    *@param  xmlXML     XML to be sorted
    *@param strParentElementXPath    
    *                   XPath of parent Elements whose child Elements 
    *                   are to be sorted
    *@param  strSortKeyXPath
    *                   XPath of XML Node to be used as the sort key within 
    *                   each child Element to be sorted.
    *@param  dataType   Data type of the sort key
    *@param  order      Order of the sort
    *@param  caseOrder  Effect of case on the sort order
    *@param  xmlResult  The XML Document, DocumentFragment, or Element in
    *                   which to store the generated XML.
    *@return            A copy of the sorted XML.
    *@throws SAXException   Should never happen, unless there is an error in
    *                       this class.  
    *@throws IOException    Should never happen, unless there is an error in
    *                       this class.    
    *@throws TransformerConfigurationException
    *                       When the Transformer cannot be constructed.
    *@throws TransformerException               
    *                       When the transformation fails.
    **************************************************************************/
    public static Node sortElements
                        (Node           xmlXML, 
                         String         strParentElementXPath,
                         String         strSortKeyXPath,
                         SortDataType   dataType,
                         SortOrder      order,
                         SortCaseOrder  caseOrder,
                         DOMResult      xmlResult)
                            throws SAXException,
                                   IOException,
                                   TransformerConfigurationException,
                                   TransformerException
                                   
    {
        Document xmlXSL = getSortElementsTransformationStylesheetDocument
           (strParentElementXPath, strSortKeyXPath, dataType, order, caseOrder);
        return transform(xmlXML, xmlXSL, xmlResult);
    }

    /**************************************************************************
    * Sorts the child Elements of the specified Elements of the specified XML 
    * Document, DocumentFragment or Element, ordering them by the value of the 
    * specified XML Element nested within each child Element, leaving the 
    * original XML unmodified, copying the sorted XML to the specified 
    * Node (which should be an XML Document, DocumentFragment or Element), 
    * and returning the sorted XML as a Node.  Can safely be called to sort 
    * a Document onto itself as:  
    *          xmlXML1 = sortElements
    *                       (xmlXML1, 
    *                        strElementName, 
    *                        strKey, 
    *                        XSLUtil.SortDataType.TEXT,
    *                        XSLUtil.SortOrder.ASCENDING,
    *                        XSLUtil.SortCaseOrder.UPPER_CASE_FIRST,
    *                        null);
    * Can also safely be called to insert a sorted copy of the XML tree into 
    * a branch of the XML tree by passing a child of the source XML as the 
    * target.  For example:
    *                   sortElements
    *                       (xmlXML1, 
    *                        strElementName, 
    *                        strKey, 
    *                        XSLUtil.SortDataType.TEXT,
    *                        XSLUtil.SortOrder.ASCENDING,
    *                        XSLUtil.SortCaseOrder.UPPER_CASE_FIRST,
    *                        xmlChildNodeOfXML1);
    *@param  xmlXML     XML to be sorted
    *@param strParentElementXPath    
    *                   XPath of parent Elements whose child Elements 
    *                   are to be sorted
    *@param  strSortKeyXPath
    *                   XPath of XML Node to be used as the sort key within 
    *                   each child Element to be sorted.
    *@param  dataType   Data type of the sort key
    *@param  order      Order of the sort
    *@param  caseOrder  Effect of case on the sort order
    *@param  xmlResult  The XML Document, DocumentFragment, or Element in
    *                   which to store the generated XML.
    *@return            A copy of the sorted XML.
    *@throws SAXException   Should never happen, unless there is an error in
    *                       this class.  
    *@throws IOException    Should never happen, unless there is an error in
    *                       this class.    
    *@throws TransformerConfigurationException
    *                       When the Transformer cannot be constructed.
    *@throws TransformerException               
    *                       When the transformation fails.
    **************************************************************************/
    public static Node sortElements
                        (Node           xmlXML, 
                         String         strParentElementXPath,
                         String         strSortKeyXPath,
                         SortDataType   dataType,
                         SortOrder      order,
                         SortCaseOrder  caseOrder,
                         Node           xmlResult)
                            throws SAXException,
                                   IOException,
                                   TransformerConfigurationException,
                                   TransformerException
                                   
    {
        DOMResult domResult = xmlResult == null 
                              ? new DOMResult()
                              : new DOMResult(xmlResult);
        return sortElements
                    (xmlXML, 
                     strParentElementXPath, 
                     strSortKeyXPath, 
                     dataType,
                     order,
                     caseOrder,
                     domResult); 
    }

    /**************************************************************************
    * Sorts the child Elements of the specified Elements of the specified XML 
    * Document, DocumentFragment or Element, ordering them by the value of the 
    * specified XML Element nested within each child Element, leaving the 
    * original XML unmodified, copying the sorted XML to a new Document, 
    * and returning the sorted XML as a Node.  Can safely be called to sort 
    * a Document onto itself as:  
    *          xmlXML1 = sortElements
    *                       (xmlXML1, 
    *                        strElementName, 
    *                        strKey,
    *                        XSLUtil.SortDataType.TEXT,
    *                        XSLUtil.SortOrder.ASCENDING,
    *                        XSLUtil.SortCaseOrder.UPPER_CASE_FIRST);
    *@param  xml        XML to be sorted
    *@param strParentElementXPath    
    *                   XPath of parent Elements whose child Elements 
    *                   are to be sorted
    *@param  strSortKeyXPath
    *                   XPath of XML Node to be used as the sort key within 
    *                   each child Element to be sorted.
    *@param  dataType   Data type of the sort key
    *@param  order      Order of the sort
    *@param  caseOrder  Effect of case on the sort order
    *@return            A copy of the sorted XML.
    *@throws SAXException   Should never happen, unless there is an error in
    *                       this class.  
    *@throws IOException    Should never happen, unless there is an error in
    *                       this class.    
    *@throws TransformerConfigurationException
    *                       When the Transformer cannot be constructed.
    *@throws TransformerException               
    *                       When the transformation fails.
    **************************************************************************/
    public static Node sortElements
                        (Node           xml, 
                         String         strParentElementXPath,
                         String         strSortKeyXPath,
                         SortDataType   dataType,
                         SortOrder      order,
                         SortCaseOrder  caseOrder)
                            throws SAXException,
                                   IOException,
                                   TransformerConfigurationException,
                                   TransformerException
                                   
    {
        return XMLUtil.getOwnerDocument(sortElements
                                            (xml, 
                                             strParentElementXPath, 
                                             strSortKeyXPath, 
                                             dataType,
                                             order,
                                             caseOrder,
                                             (Node)null));
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
        private static Node xmlA;
        private static Node xmlA1;
        private static Node xmlA4;
        private static Document initDocument()
        {
            Document dom = XMLUtil.createEmptyDocument();
            xmlA = XMLUtil.appendElement(dom, "A");
            xmlA1 = XMLUtil.appendElement(xmlA, "A1");
            XMLUtil.appendElementContainingText(xmlA1, "A1a", "A1a value");
            XMLUtil.appendElementContainingText(xmlA1, "A1b", "A1b value");
            XMLUtil.appendElement(xmlA, "A2");
            xmlA4 = XMLUtil.appendElement(xmlA, "A4");
            XMLUtil.appendElementContainingText(xmlA4, "A4a", "A4a value");
            XMLUtil.appendElementContainingText(xmlA4, "A4b", "A4b value");
            XMLUtil.appendElementContainingText(xmlA4, "A4c", "A4c value");
            return dom;
        }

        private static void testTransform
                                (Node xmlXML, 
                                 Node xmlAfter, 
                                 Node xmlValid)
                                    throws IOException 
        {
            System.out.println("--Before transformation:");
            XMLUtil.serialize(XMLUtil.getOwnerDocument(xmlXML), System.out);
            System.out.println("--After transformation:");
            XMLUtil.serialize(XMLUtil.getOwnerDocument(xmlAfter), System.out);
            if (xmlValid == null)
            {
                System.out.println("--Check the above results manually.");
            }
            else 
            {
                System.out.println("--Expected result:");
                XMLUtil.serialize(XMLUtil.getOwnerDocument(xmlValid), System.out);
                if (ObjUtil.equalsOrBothNull
                        (XMLUtil.serialize(XMLUtil.getOwnerDocument(xmlValid)),
                         XMLUtil.serialize(XMLUtil.getOwnerDocument(xmlAfter))))
                {
                    System.out.println("Success!");
                }
                else
                {
                    System.out.println("Failure!");
                }
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

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getIdentityTransformationTemplateString()");
                System.out.println("--");
                //-------------------------------------------------------------
                System.out.println(getIdentityTransformationTemplateString());
                
                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getIdentityTransformationStylesheetString()");
                System.out.println("--");
                //-------------------------------------------------------------
                System.out.println(getIdentityTransformationStylesheetString());
                
                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getIdentityTransformationStylesheetDocument()");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getIdentityTransformationStylesheetDocument(), System.out);
                
                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getStripCommentsTransformationTemplateString()");
                System.out.println("--");
                //-------------------------------------------------------------
                System.out.println
                            (getStripCommentsTransformationTemplateString());
                
                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getStripCommentsTransformationStylesheetString()");
                System.out.println("--");
                //-------------------------------------------------------------
                System.out.println
                            (getStripCommentsTransformationStylesheetString());
                
                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getStripCommentsTransformationStylesheetDocument()");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                            (getStripCommentsTransformationStylesheetDocument(), 
                             System.out);
                
                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- transform(XML, identity)");
                System.out.println("--");
                //-------------------------------------------------------------
                Node xmlBefore = initDocument(); 
                testTransform
                    (xmlBefore, 
                     transform
                            (xmlBefore, 
                             getIdentityTransformationStylesheetDocument()), 
                     xmlBefore);
                
                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- transform(identity, identity) to see" +
                                   " comments copied");
                System.out.println("--");
                //-------------------------------------------------------------
                xmlBefore = getIdentityTransformationStylesheetDocument();  
                testTransform
                    (xmlBefore, 
                     transform
                            (xmlBefore, 
                             getIdentityTransformationStylesheetDocument()), 
                     xmlBefore);
                
                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- copy()");
                System.out.println("--");
                //-------------------------------------------------------------
                xmlBefore = initDocument(); 
                testTransform
                    (xmlBefore, 
                     copy(xmlBefore), 
                     xmlBefore);
                
                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- stripComments()");
                System.out.println("--");
                //-------------------------------------------------------------
                xmlBefore = getIdentityTransformationStylesheetDocument(); 
                                //-- This XML actually has some comments.
                testTransform
                    (xmlBefore, 
                     stripComments(xmlBefore), 
                     null);
                
                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- copy(XML, XML) to see" +
                                   " insertion into existing XML");
                System.out.println("--");
                //-------------------------------------------------------------
                xmlBefore = initDocument();  
                Document xmlToInsert = XMLUtil.loadDocumentFromString
                                ("<A3>" +
                                   "<A3a>A3a value</A3a>" +
                                   "<A3b>" +
                                     "<A3b1>A3b1 value</A3b1>" +
                                     "<A3b2>A3b2 value</A3b2>" +
                                   "</A3b>" +
                                   "<A3c>A3c value</A3c>" +
                                   "<A3d>A3d value</A3d>" +
                                 "</A3>");
                initDocument();     // To set xmlA1 to point into a new DOM.  
                testTransform
                    (xmlBefore, 
                     copy(xmlToInsert, xmlA1), 
                     null);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- copy(XML, XML Child) to" +
                                   " see insertion into child of self XML");
                System.out.println("--");
                //-------------------------------------------------------------
                xmlBefore = initDocument();  
                Document xmlContainingA1 = initDocument();  
                testTransform
                    (xmlBefore, 
                     copy(xmlContainingA1, xmlA1), 
                     null);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getSortElementsTransformationStylesheetDocument" +
                         "(TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getSortElementsTransformationStylesheetDocument
                                    ("A",
                                     "C1",
                                     SortDataType.TEXT,
                                     SortOrder.ASCENDING,
                                     SortCaseOrder.UPPER_CASE_FIRST),
                     System.out);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- sortElements" +
                                   "(TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                Document dom3 = XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>3</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='z'>10</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='y'>2</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>");
                testTransform
                    (dom3, 
                     sortElements
                        (dom3, 
                         "A", 
                         "C1",
                         SortDataType.TEXT,
                         SortOrder.ASCENDING,
                         SortCaseOrder.UPPER_CASE_FIRST),
                     XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "\nText node 3 inside A\n" +
                                       "\nText node 2 inside A\n" +
                                       "\nText node 4 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='z'>10</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='y'>2</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='x'>3</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                     "</A>"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getSortElementsTransformationStylesheetDocument" +
                         "(NUMBER, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getSortElementsTransformationStylesheetDocument
                                    ("A",
                                     "C1",
                                     SortDataType.NUMBER,
                                     SortOrder.ASCENDING,
                                     SortCaseOrder.UPPER_CASE_FIRST),
                     System.out);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- sortElements" +
                                   "(NUMBER, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                dom3 = XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>3</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='z'>10</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='y'>2</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>");
                testTransform
                    (dom3, 
                     sortElements
                        (dom3, 
                         "A", 
                         "C1",
                         SortDataType.NUMBER,
                         SortOrder.ASCENDING,
                         SortCaseOrder.UPPER_CASE_FIRST),
                     XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "\nText node 3 inside A\n" +
                                       "\nText node 2 inside A\n" +
                                       "\nText node 4 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='y'>2</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='x'>3</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='z'>10</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                     "</A>"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getSortElementsTransformationStylesheetDocument" +
                         "(TEXT, DESCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getSortElementsTransformationStylesheetDocument
                                    ("A",
                                     "C1",
                                     SortDataType.TEXT,
                                     SortOrder.DESCENDING,
                                     SortCaseOrder.UPPER_CASE_FIRST),
                     System.out);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- sortElements" +
                                   "(TEXT, DESCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                dom3 = XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>3</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='z'>10</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='y'>2</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>");
                testTransform
                    (dom3, 
                     sortElements
                        (dom3, 
                         "A", 
                         "C1",
                         SortDataType.TEXT,
                         SortOrder.DESCENDING,
                         SortCaseOrder.UPPER_CASE_FIRST),
                     XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "\nText node 3 inside A\n" +
                                       "\nText node 2 inside A\n" +
                                       "\nText node 4 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>3</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='y'>2</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='z'>10</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                     "</A>"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getSortElementsTransformationStylesheetDocument" +
                         "(NUMBER, DESCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getSortElementsTransformationStylesheetDocument
                                    ("A",
                                     "C1",
                                     SortDataType.NUMBER,
                                     SortOrder.DESCENDING,
                                     SortCaseOrder.UPPER_CASE_FIRST),
                     System.out);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- sortElements" +
                                   "(NUMBER, DESCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                dom3 = XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>3</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='z'>10</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='y'>2</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>");
                testTransform
                    (dom3, 
                     sortElements
                        (dom3, 
                         "A", 
                         "C1",
                         SortDataType.NUMBER,
                         SortOrder.DESCENDING,
                         SortCaseOrder.UPPER_CASE_FIRST),
                     XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "\nText node 3 inside A\n" +
                                       "\nText node 2 inside A\n" +
                                       "\nText node 4 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='z'>10</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='x'>3</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='y'>2</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                     "</A>"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getSortElementsTransformationStylesheetDocument" +
                         "(TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getSortElementsTransformationStylesheetDocument
                                    ("A",
                                     "C1",
                                     SortDataType.TEXT,
                                     SortOrder.ASCENDING,
                                     SortCaseOrder.UPPER_CASE_FIRST),
                     System.out);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- sortElements" +
                                   "(TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                dom3 = XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>Abc</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='z'>abc</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='y'>aBc</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>");
                testTransform
                    (dom3, 
                     sortElements
                        (dom3, 
                         "A", 
                         "C1",
                         SortDataType.TEXT,
                         SortOrder.ASCENDING,
                         SortCaseOrder.UPPER_CASE_FIRST),
                     XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "\nText node 3 inside A\n" +
                                       "\nText node 2 inside A\n" +
                                       "\nText node 4 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>Abc</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='y'>aBc</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='z'>abc</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                     "</A>"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getSortElementsTransformationStylesheetDocument" +
                         "(TEXT, ASCENDING, LOWER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getSortElementsTransformationStylesheetDocument
                                    ("A",
                                     "C1",
                                     SortDataType.TEXT,
                                     SortOrder.ASCENDING,
                                     SortCaseOrder.LOWER_CASE_FIRST),
                     System.out);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- sortElements" +
                                   "(TEXT, ASCENDING, LOWER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                dom3 = XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>Abc</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='z'>abc</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='y'>aBc</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>");
                testTransform
                    (dom3, 
                     sortElements
                        (dom3, 
                         "A", 
                         "C1",
                         SortDataType.TEXT,
                         SortOrder.ASCENDING,
                         SortCaseOrder.LOWER_CASE_FIRST),
                     XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "\nText node 3 inside A\n" +
                                       "\nText node 2 inside A\n" +
                                       "\nText node 4 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='z'>abc</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='y'>aBc</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='x'>Abc</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                     "</A>"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getSortElementsTransformationStylesheetDocument" +
                         "(TEXT, ASCENDING, CASE_INSENSITIVE)");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getSortElementsTransformationStylesheetDocument
                                    ("A",
                                     "C1",
                                     SortDataType.TEXT,
                                     SortOrder.ASCENDING,
                                     SortCaseOrder.CASE_INSENSITIVE),
                     System.out);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- sortElements" +
                                   "(TEXT, ASCENDING, CASE_INSENSITIVE)");
                System.out.println("--");
                //-------------------------------------------------------------
                dom3 = XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>Abc</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='z'>abc</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='y'>aBc</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>");
                testTransform
                    (dom3, 
                     sortElements
                        (dom3, 
                         "A", 
                         "C1",
                         SortDataType.TEXT,
                         SortOrder.ASCENDING,
                         SortCaseOrder.CASE_INSENSITIVE),
                     XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "\nText node 3 inside A\n" +
                                       "\nText node 2 inside A\n" +
                                       "\nText node 4 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>Abc</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='z'>abc</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                       "<B>" +
                                         "<C1 attr1='y'>aBc</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                     "</A>"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getSortElementsTransformationStylesheetDocument" +
                         "(Attribute, TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getSortElementsTransformationStylesheetDocument
                                    ("A",
                                     "@attr1",
                                     SortDataType.TEXT,
                                     SortOrder.ASCENDING,
                                     SortCaseOrder.UPPER_CASE_FIRST),
                     System.out);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- sortElements" +
                                   "(Attribute, TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                dom3 = XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B attr2='1' attr1='2'>" +
                                         "<C1 attr1='x'>3</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B attr2='2' attr1='10'>" +
                                         "<C1 attr1='z'>10</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B attr2='3' attr1='3'>" +
                                         "<C1 attr1='y'>2</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>");
                testTransform
                    (dom3, 
                     sortElements
                        (dom3, 
                         "A", 
                         "@attr1",
                         SortDataType.TEXT,
                         SortOrder.ASCENDING,
                         SortCaseOrder.UPPER_CASE_FIRST),
                     XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "\nText node 3 inside A\n" +
                                       "\nText node 2 inside A\n" +
                                       "\nText node 4 inside A\n" +
                                       "<B  attr2='2' attr1='10'>" +
                                         "<C1 attr1='z'>10</C1>" +
                                         "<C2 attr2='b'>C2 value 3</C2>" +
                                       "</B>" +
                                       "<B  attr2='1' attr1='2'>" +
                                         "<C1 attr1='x'>3</C1>" +
                                         "<C2 attr2='a'>C2 value 1</C2>" +
                                       "</B>" +
                                       "<B  attr2='3' attr1='3'>" +
                                         "<C1 attr1='y'>2</C1>" +
                                         "<C2 attr2='c'>C2 value 2</C2>" +
                                       "</B>" +
                                     "</A>"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getSortElementsTransformationStylesheetDocument" +
                         "(Multiple parents, TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getSortElementsTransformationStylesheetDocument
                                    ("B",
                                     "D1",
                                     SortDataType.TEXT,
                                     SortOrder.ASCENDING,
                                     SortCaseOrder.UPPER_CASE_FIRST),
                     System.out);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- sortElements" +
                       "(Multiple parents, TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                dom3 = XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>" +
                                           "<D1>3</D1>" +
                                         "</C1>" +
                                         "<C2 attr2='a'>" +
                                           "<D1>2</D1>" +
                                         "</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "\nText node 1 inside B\n" +
                                         "<C1 attr1='z'>" +
                                           "<D1>4</D1>" +
                                         "</C1>" +
                                         "\nText node 3 inside B\n" +
                                         "<C2 attr2='b'>" +
                                           "<D1>5</D1>" +
                                         "</C2>" +
                                         "\nText node 2 inside B\n" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "\nText node 4 inside B\n" +
                                         "<C1 attr1='y'>" +
                                           "<D1>6</D1>" +
                                         "</C1>" +
                                         "\nText node 6 inside B\n" +
                                         "<C2 attr2='c'>" +
                                           "<D1>1</D1>" +
                                         "</C2>" +
                                         "\nText node 5 inside B\n" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>");
                testTransform
                    (dom3, 
                     sortElements
                        (dom3, 
                         "B", 
                         "D1",
                         SortDataType.TEXT,
                         SortOrder.ASCENDING,
                         SortCaseOrder.UPPER_CASE_FIRST),
                     XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C2 attr2='a'>" +
                                           "<D1>2</D1>" +
                                         "</C2>" +
                                         "<C1 attr1='x'>" +
                                           "<D1>3</D1>" +
                                         "</C1>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "\nText node 1 inside B\n" +
                                         "\nText node 3 inside B\n" +
                                         "\nText node 2 inside B\n" +
                                         "<C1 attr1='z'>" +
                                           "<D1>4</D1>" +
                                         "</C1>" +
                                         "<C2 attr2='b'>" +
                                           "<D1>5</D1>" +
                                         "</C2>" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "\nText node 4 inside B\n" +
                                         "\nText node 6 inside B\n" +
                                         "\nText node 5 inside B\n" +
                                         "<C2 attr2='c'>" +
                                           "<D1>1</D1>" +
                                         "</C2>" +
                                         "<C1 attr1='y'>" +
                                           "<D1>6</D1>" +
                                         "</C1>" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getSortElementsTransformationStylesheetDocument" +
                         "(Absolute XPath, TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getSortElementsTransformationStylesheetDocument
                                    ("/A/B",
                                     "D1",
                                     SortDataType.TEXT,
                                     SortOrder.ASCENDING,
                                     SortCaseOrder.UPPER_CASE_FIRST),
                     System.out);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- sortElements" +
                       "(Absolute XPath, TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                dom3 = XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>" +
                                           "<D1>3</D1>" +
                                         "</C1>" +
                                         "<C2 attr2='a'>" +
                                           "<D1>2</D1>" +
                                         "</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "\nText node 1 inside B\n" +
                                         "<C1 attr1='z'>" +
                                           "<D1>4</D1>" +
                                         "</C1>" +
                                         "\nText node 3 inside B\n" +
                                         "<C2 attr2='b'>" +
                                           "<D1>5</D1>" +
                                         "</C2>" +
                                         "\nText node 2 inside B\n" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "\nText node 4 inside B\n" +
                                         "<C1 attr1='y'>" +
                                           "<D1>6</D1>" +
                                         "</C1>" +
                                         "\nText node 6 inside B\n" +
                                         "<C2 attr2='c'>" +
                                           "<D1>1</D1>" +
                                         "</C2>" +
                                         "\nText node 5 inside B\n" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>");
                testTransform
                    (dom3, 
                     sortElements
                        (dom3, 
                         "/A/B", 
                         "D1",
                         SortDataType.TEXT,
                         SortOrder.ASCENDING,
                         SortCaseOrder.UPPER_CASE_FIRST),
                     XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C2 attr2='a'>" +
                                           "<D1>2</D1>" +
                                         "</C2>" +
                                         "<C1 attr1='x'>" +
                                           "<D1>3</D1>" +
                                         "</C1>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "\nText node 1 inside B\n" +
                                         "\nText node 3 inside B\n" +
                                         "\nText node 2 inside B\n" +
                                         "<C1 attr1='z'>" +
                                           "<D1>4</D1>" +
                                         "</C1>" +
                                         "<C2 attr2='b'>" +
                                           "<D1>5</D1>" +
                                         "</C2>" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "\nText node 4 inside B\n" +
                                         "\nText node 6 inside B\n" +
                                         "\nText node 5 inside B\n" +
                                         "<C2 attr2='c'>" +
                                           "<D1>1</D1>" +
                                         "</C2>" +
                                         "<C1 attr1='y'>" +
                                           "<D1>6</D1>" +
                                         "</C1>" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getSortElementsTransformationStylesheetDocument" +
                         "(Ordinal XPath, TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getSortElementsTransformationStylesheetDocument
                                    ("/A/B[2]",
                                     "D1",
                                     SortDataType.TEXT,
                                     SortOrder.ASCENDING,
                                     SortCaseOrder.UPPER_CASE_FIRST),
                     System.out);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- sortElements" +
                       "(Ordinal XPath, TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                dom3 = XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>" +
                                           "<D1>3</D1>" +
                                         "</C1>" +
                                         "<C2 attr2='a'>" +
                                           "<D1>2</D1>" +
                                         "</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "\nText node 1 inside B\n" +
                                         "<C1 attr1='z'>" +
                                           "<D1>4</D1>" +
                                         "</C1>" +
                                         "\nText node 3 inside B\n" +
                                         "<C2 attr2='b'>" +
                                           "<D1>5</D1>" +
                                         "</C2>" +
                                         "\nText node 2 inside B\n" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "\nText node 4 inside B\n" +
                                         "<C1 attr1='y'>" +
                                           "<D1>6</D1>" +
                                         "</C1>" +
                                         "\nText node 6 inside B\n" +
                                         "<C2 attr2='c'>" +
                                           "<D1>1</D1>" +
                                         "</C2>" +
                                         "\nText node 5 inside B\n" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>");
                testTransform
                    (dom3, 
                     sortElements
                        (dom3, 
                         "/A/B[2]", 
                         "D1",
                         SortDataType.TEXT,
                         SortOrder.ASCENDING,
                         SortCaseOrder.UPPER_CASE_FIRST),
                     XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>" +
                                           "<D1>3</D1>" +
                                         "</C1>" +
                                         "<C2 attr2='a'>" +
                                           "<D1>2</D1>" +
                                         "</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "\nText node 1 inside B\n" +
                                         "\nText node 3 inside B\n" +
                                         "\nText node 2 inside B\n" +
                                         "<C1 attr1='z'>" +
                                           "<D1>4</D1>" +
                                         "</C1>" +
                                         "<C2 attr2='b'>" +
                                           "<D1>5</D1>" +
                                         "</C2>" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "\nText node 4 inside B\n" +
                                         "<C1 attr1='y'>" +
                                           "<D1>6</D1>" +
                                         "</C1>" +
                                         "\nText node 6 inside B\n" +
                                         "<C2 attr2='c'>" +
                                           "<D1>1</D1>" +
                                         "</C2>" +
                                         "\nText node 5 inside B\n" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getSortElementsTransformationStylesheetDocument" +
                         "(Relative Ordinal XPath, TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getSortElementsTransformationStylesheetDocument
                                    ("B[2]",
                                     "D1",
                                     SortDataType.TEXT,
                                     SortOrder.ASCENDING,
                                     SortCaseOrder.UPPER_CASE_FIRST),
                     System.out);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- sortElements" +
                       "(Relative Ordinal XPath, TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                dom3 = XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>" +
                                           "<D1>3</D1>" +
                                         "</C1>" +
                                         "<C2 attr2='a'>" +
                                           "<D1>2</D1>" +
                                         "</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "\nText node 1 inside B\n" +
                                         "<C1 attr1='z'>" +
                                           "<D1>4</D1>" +
                                         "</C1>" +
                                         "\nText node 3 inside B\n" +
                                         "<C2 attr2='b'>" +
                                           "<D1>5</D1>" +
                                         "</C2>" +
                                         "\nText node 2 inside B\n" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "\nText node 4 inside B\n" +
                                         "<C1 attr1='y'>" +
                                           "<D1>6</D1>" +
                                         "</C1>" +
                                         "\nText node 6 inside B\n" +
                                         "<C2 attr2='c'>" +
                                           "<D1>1</D1>" +
                                         "</C2>" +
                                         "\nText node 5 inside B\n" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>");
                testTransform
                    (dom3, 
                     sortElements
                        (dom3, 
                         "B[2]", 
                         "D1",
                         SortDataType.TEXT,
                         SortOrder.ASCENDING,
                         SortCaseOrder.UPPER_CASE_FIRST),
                     XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>" +
                                           "<D1>3</D1>" +
                                         "</C1>" +
                                         "<C2 attr2='a'>" +
                                           "<D1>2</D1>" +
                                         "</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "\nText node 1 inside B\n" +
                                         "\nText node 3 inside B\n" +
                                         "\nText node 2 inside B\n" +
                                         "<C1 attr1='z'>" +
                                           "<D1>4</D1>" +
                                         "</C1>" +
                                         "<C2 attr2='b'>" +
                                           "<D1>5</D1>" +
                                         "</C2>" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "\nText node 4 inside B\n" +
                                         "<C1 attr1='y'>" +
                                           "<D1>6</D1>" +
                                         "</C1>" +
                                         "\nText node 6 inside B\n" +
                                         "<C2 attr2='c'>" +
                                           "<D1>1</D1>" +
                                         "</C2>" +
                                         "\nText node 5 inside B\n" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>"));

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println
                        ("-- getSortElementsTransformationStylesheetDocument" +
                         "(Wrong XPath, TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                XMLUtil.serialize
                    (getSortElementsTransformationStylesheetDocument
                                    ("/B",
                                     "D1",
                                     SortDataType.TEXT,
                                     SortOrder.ASCENDING,
                                     SortCaseOrder.UPPER_CASE_FIRST),
                     System.out);

                //-------------------------------------------------------------
                System.out.println("--");
                System.out.println("-- sortElements" +
                       "(Wrong XPath, TEXT, ASCENDING, UPPER_CASE_FIRST)");
                System.out.println("--");
                //-------------------------------------------------------------
                dom3 = XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>" +
                                           "<D1>3</D1>" +
                                         "</C1>" +
                                         "<C2 attr2='a'>" +
                                           "<D1>2</D1>" +
                                         "</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "\nText node 1 inside B\n" +
                                         "<C1 attr1='z'>" +
                                           "<D1>4</D1>" +
                                         "</C1>" +
                                         "\nText node 3 inside B\n" +
                                         "<C2 attr2='b'>" +
                                           "<D1>5</D1>" +
                                         "</C2>" +
                                         "\nText node 2 inside B\n" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "\nText node 4 inside B\n" +
                                         "<C1 attr1='y'>" +
                                           "<D1>6</D1>" +
                                         "</C1>" +
                                         "\nText node 6 inside B\n" +
                                         "<C2 attr2='c'>" +
                                           "<D1>1</D1>" +
                                         "</C2>" +
                                         "\nText node 5 inside B\n" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>");
                testTransform
                    (dom3, 
                     sortElements
                        (dom3, 
                         "/B", 
                         "D1",
                         SortDataType.TEXT,
                         SortOrder.ASCENDING,
                         SortCaseOrder.UPPER_CASE_FIRST),
                     XMLUtil.loadDocumentFromString
                                    ("<A attr='4'>" +
                                       "\nText node 1 inside A\n" +
                                       "<B>" +
                                         "<C1 attr1='x'>" +
                                           "<D1>3</D1>" +
                                         "</C1>" +
                                         "<C2 attr2='a'>" +
                                           "<D1>2</D1>" +
                                         "</C2>" +
                                       "</B>" +
                                       "\nText node 3 inside A\n" +
                                       "<B>" +
                                         "\nText node 1 inside B\n" +
                                         "<C1 attr1='z'>" +
                                           "<D1>4</D1>" +
                                         "</C1>" +
                                         "\nText node 3 inside B\n" +
                                         "<C2 attr2='b'>" +
                                           "<D1>5</D1>" +
                                         "</C2>" +
                                         "\nText node 2 inside B\n" +
                                       "</B>" +
                                       "\nText node 2 inside A\n" +
                                       "<B>" +
                                         "\nText node 4 inside B\n" +
                                         "<C1 attr1='y'>" +
                                           "<D1>6</D1>" +
                                         "</C1>" +
                                         "\nText node 6 inside B\n" +
                                         "<C2 attr2='c'>" +
                                           "<D1>1</D1>" +
                                         "</C2>" +
                                         "\nText node 5 inside B\n" +
                                       "</B>" +
                                       "\nText node 4 inside A\n" +
                                     "</A>"));

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
