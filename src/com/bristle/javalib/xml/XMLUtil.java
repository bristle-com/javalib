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

import com.bristle.javalib.util.ExcUtil;
import com.bristle.javalib.util.ObjUtil;

import java.io.StringReader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Enumeration;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.DocumentFragment;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.LineSeparator;
import org.apache.xpath.XPathAPI;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;

// XMLUtil
/******************************************************************************
* This class contains utility routines for manipulating XML.
*<pre>
*<b>Usage:</b>
*   - Some typical scenarios for using this class are:
*
*     - To create an XML Document from scratch and convert it to a String:
*           Document dom = XMLUtil.createEmptyDocument();
*           Node xmlNode = XMLUtil.appendElement(dom, "Person");
*           XMLUtil.appendElementContainingText(xmlNode, "Name", "John Smith");
*           XMLUtil.appendElementContainingText(xmlNode, "Phone", "800-555-1212");
*           String strXML = XMLUtil.serialize(dom);
*
*     - To load and query an existing XML Document:
*           Document dom = XMLUtil.loadDocumentFromURL
*                                       ("http://www.xmlsource.com");
*           String strName = XMLUtil.getTextNodeValueViaXPath
*                                       (xmlNode, ".//Name");
*           String strPhone = XMLUtil.getTextNodeValueViaXPath
*                                       (xmlNode, "Phone");
*
*     - To load and modify an existing XML Document:
*           Document dom = XMLUtil.loadDocumentFromURL
*                                       ("http://www.xmlsource.com");
*           String strName = XMLUtil.setTextNodeValueViaXPath
*                                       (xmlNode, ".//Name", "John Smith");
*           String strPhone = XMLUtil.setTextNodeValueViaXPath
*                                       (xmlNode, "Phone", "800-555-1212");
*
*   - See the source code of the inner Tester class for more examples.
*  
*<b>Assumptions:</b>
*<b>Effects:</b>
*       - None.
*<b>Anticipated Changes:</b>
*<b>Notes:</b>
*       - Pay particular attection to the distinction between the different
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
*       - The term "append" when used in method names in this class typically
*         means "append a newly created child node to the specified node". 
*<b>Implementation Notes:</b>
*<b>Portability Issues:</b>
*<b>Revision History:</b>
*   $Log$
*</pre>
******************************************************************************/
public class XMLUtil
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
    * This exception is thrown when the specified XML node doesn't exist.
    **************************************************************************/
    public static class NoSuchXMLNodeException extends Exception
    {
        private static final long serialVersionUID = 1L;
        public NoSuchXMLNodeException(String msg) { super(msg); }
    }

    /**************************************************************************
    * Creates an empty XML Document.
    *@return            Empty XML Document.
    **************************************************************************/
    public static Document createEmptyDocument()
    {
        return new DocumentImpl();
    }

    /**************************************************************************
    * Load the specified URL into an XML Document.
    *<pre>
    *<b>Anticipated Changes:</b>
    *      None.
    *</pre>
    *@param  strURL         URL containing XML to load.
    *@return                XML Document.
    *@throws SAXException   When URL contains invalid XML.
    *@throws IOException    When an error occurs reading from the URL.
    **************************************************************************/
    public static Document loadDocumentFromURL (String strURL)
                 throws SAXException
                       ,IOException
    {
        DOMParser parser = new DOMParser();
        parser.parse(strURL);
        return parser.getDocument();
    }

    /**************************************************************************
    * Load the specified URL into an XML Document.
    *<pre>
    *<b>Anticipated Changes:</b>
    *      None.
    *</pre>
    *@param  inputSource    InputSource containing XML to load.
    *@return                XML Document.
    *@throws SAXException   When InputSource contains invalid XML.
    *@throws IOException    When an error occurs reading from the InputSource.
    **************************************************************************/
    public static Document loadDocumentFromInputSource (InputSource inputSource)
                 throws SAXException
                       ,IOException
    {
        DOMParser parser = new DOMParser();
        parser.parse(inputSource);
        return parser.getDocument();
    }

    /**************************************************************************
    * Load the specified XML string into an XML Document.
    *<pre>
    *<b>Anticipated Changes:</b>
    *      None.
    *</pre>
    *@param  strXML         String of XML to load.
    *@return                XML Document.
    *@throws SAXException   When string contains invalid XML.
    *@throws IOException    When an error occurs reading from the string.
    **************************************************************************/
    public static Document loadDocumentFromString (String strXML)
                 throws SAXException
                       ,IOException
    {
        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(strXML)));
        return parser.getDocument();
    }

    /**************************************************************************
    * Get the owner document of the specified node.  If the node is itself a 
    * document, return it.
    *@param  xmlNode    Node to get the owner document of.
    *@return            Document node.
    **************************************************************************/
    public static Document getOwnerDocument(Node xmlNode)
    {
        return (xmlNode.getNodeType() == Node.DOCUMENT_NODE)
               ? (Document) xmlNode
               : xmlNode.getOwnerDocument();
    }

    /**************************************************************************
    * Append a child Text node to the specified Node.
    *@param  xmlNode    Node to append to.
    *@param  strText    String to put in text node.
    *@return            New text node.
    **************************************************************************/
    public static Text appendTextNode
                        (Node     xmlNode,
                         String   strText)
    {
        Text xmlTextNode = 
                XMLUtil.getOwnerDocument(xmlNode).createTextNode(strText);
        xmlNode.appendChild(xmlTextNode);
        return xmlTextNode;
    }

    /**************************************************************************
    * Append a child Element to the specified Node.
    *@param  xmlNode    Node to append to.
    *@param  strName    String to use as name of new Node.
    *@return            New Element.
    **************************************************************************/
    public static Element appendElement(Node xmlNode, String strName)
    {
        return (Element)xmlNode.appendChild
                (XMLUtil.getOwnerDocument(xmlNode).createElement(strName));
    }

    /**************************************************************************
    * Append a child Element containing a Text node to the specified Node.
    *@param  xmlNode    Node to append to.
    *@param  strName    String to use as name of new Element.
    *@param  strText    String to put in Text node.
    *@return            New Element.
    **************************************************************************/
    public static Element appendElementContainingText
                        (Node     xmlNode,
                         String   strName,
                         String   strText)
    {
        Element xmlNewNode = appendElement(xmlNode, strName);
        appendTextNode(xmlNewNode, strText);
        return xmlNewNode;
    }

    /**************************************************************************
    * Wraps the info from a Java Throwable in an XML Document.
    *@param  e          Throwable to wrap as XML.
    *@param  objSource  Object throwing the Throwable.
    *@return            Document containing XML.
    **************************************************************************/
    public static Document wrapThrowableInXML(Throwable e, Object objSource)
    {
        Document dom = createEmptyDocument();
        Node xmlError = appendElement(dom, "Error");
        appendElementContainingText
                (xmlError, "Name", e.getClass().getName());
        appendElementContainingText
                (xmlError, "Source", objSource.getClass().getName());
        appendElementContainingText
                (xmlError, "Message", e.getMessage());
        appendElementContainingText
                (xmlError, "StackTrace", ExcUtil.getStackTrace(e));
        return dom;
    }

    /**************************************************************************
    * Return a new OutputFormat initialized with our standard settings.
    *@return            The new OutputFormat.
    **************************************************************************/
    private static OutputFormat initOutputFormat()
    {
        //-- Note:  Return an OutputFormat that does little formatting
        //--        of the data.  Otherwise, by default, each Element gets 
        //--        indented by 4 spaces per nesting level.  There are two 
        //--        problems with that:
        //--        1)  It makes the serialized string much larger, which 
        //--            limits the amount of XML we can serialize into a 
        //--            StringWriter without running out of memory.  
        //--        2)  Long lines may be wrapped, which can cause line breaks
        //--            newlines to be embedded in the whitespace of the data 
        //--            values.
        //-- Note:  We purposely allow each Element to be put on its own 
        //--        line because this makes it easier to debug, and easier
        //--        to compare old and new outputs during regression testing.
        //-- Note:  Despite our wish to keep the serialized XML strings as 
        //--        short as possible, we had to set a non-zero indent value.
        //--        Otherwise, the line breaks don't get inserted.  Why??
        //--        On the other hand, the indentation is useful for debugging
        //--        purposes and we've restructured the calling code to not 
        //--        try to hold so much output in memory at once anyhow, so 
        //--        perhaps the indentation is not a problem.
        final boolean blnPRETTY_PRINT = true;
        final String  strDEFAULT_ENCODING = null;
        OutputFormat format = new OutputFormat
                                        (Method.XML, 
                                         strDEFAULT_ENCODING, 
                                         blnPRETTY_PRINT);
        format.setLineSeparator(LineSeparator.Windows);
        format.setIndent(1);
        format.setLineWidth(0);
        format.setOmitXMLDeclaration(true);
        return format;
    }

    /**************************************************************************
    * Serialize the XML Document into the specified Writer, silently 
    * tolerating a null Document.
    *@param  xml            XML Document
    *@param  writer         The Writer to serialize to.
    *@return                The specified Writer.
    *@throws IOException    When the Document can't be serialized.
    **************************************************************************/
    public static Writer serialize(Document xml, Writer writer)
                throws IOException
    {
        if (xml == null)
        {
            return writer;
        }
        XMLSerializer objXMLSerializer = new XMLSerializer
                                                (writer, 
                                                 initOutputFormat());
        objXMLSerializer.serialize(xml);
        return writer;
    }

    /**************************************************************************
    * Serialize the XML Document into the specified OutputStream, silently 
    * tolerating a null Document.
    *@param  xml            XML Document
    *@param  out            The OutputStream to serialize to.
    *@return                The specified OutputStream.
    *@throws IOException    When the Document can't be serialized.
    **************************************************************************/
    public static OutputStream serialize(Document xml, OutputStream out)
                throws IOException
    {
        serialize(xml, new OutputStreamWriter(out));
        return out;
    }

    /**************************************************************************
    * Serialize the XML Document into a string of XML.
    *@param  xml            XML Document
    *@return                String of XML.
    *@throws IOException    When the Document can't be serialized.
    **************************************************************************/
    public static String serialize(Document xml)
                throws IOException
    {
        return serialize(xml, new StringWriter()).toString();
    }

    /**************************************************************************
    * Serialize the XML DocumentFragment into the specified Writer, silently 
    * tolerating a null DocumentFragment.
    *@param  xml            XML DocumentFragment
    *@param  writer         The Writer to serialize to.
    *@return                The specified Writer.
    *@throws IOException    When the DocumentFragment can't be serialized.
    **************************************************************************/
    public static Writer serialize(DocumentFragment xml, Writer writer)
                throws IOException
    {
        if (xml == null)
        {
            return writer;
        }
        XMLSerializer objXMLSerializer = new XMLSerializer
                                                (writer, 
                                                 initOutputFormat());
        objXMLSerializer.serialize(xml);
        return writer;
    }

    /**************************************************************************
    * Serialize the XML DocumentFragment into the specified OutputStream, silently 
    * tolerating a null DocumentFragment.
    *@param  xml            XML DocumentFragment
    *@param  out            The OutputStream to serialize to.
    *@return                The specified OutputStream.
    *@throws IOException    When the DocumentFragment can't be serialized.
    **************************************************************************/
    public static OutputStream serialize(DocumentFragment xml, OutputStream out)
                throws IOException
    {
        serialize(xml, new OutputStreamWriter(out));
        return out;
    }

    /**************************************************************************
    * Serialize the XML DocumentFragment into a string of XML.
    *@param  xml            XML DocumentFragment
    *@return                String of XML.
    *@throws IOException    When the DocumentFragment can't be serialized.
    **************************************************************************/
    public static String serialize(DocumentFragment xml)
                throws IOException
    {
        return serialize(xml, new StringWriter()).toString();
    }

    /**************************************************************************
    * Serialize the XML Element into the specified Writer, silently 
    * tolerating a null Element.
    *@param  xml            XML Element
    *@param  writer         The Writer to serialize to.
    *@return                The specified Writer.
    *@throws IOException    When the Element can't be serialized.
    **************************************************************************/
    public static Writer serialize(Element xml, Writer writer)
                throws IOException
    {
        if (xml == null)
        {
            return writer;
        }
        XMLSerializer objXMLSerializer = new XMLSerializer
                                                (writer, 
                                                 initOutputFormat());
        objXMLSerializer.serialize(xml);
        return writer;
    }

    /**************************************************************************
    * Serialize the XML Element into the specified OutputStream, silently 
    * tolerating a null Element.
    *@param  xml            XML Element
    *@param  out            The OutputStream to serialize to.
    *@return                The specified OutputStream.
    *@throws IOException    When the Element can't be serialized.
    **************************************************************************/
    public static OutputStream serialize(Element xml, OutputStream out)
                throws IOException
    {
        serialize(xml, new OutputStreamWriter(out));
        return out;
    }

    /**************************************************************************
    * Serialize the XML Element into a string of XML.
    *@param  xml            XML Element
    *@return                String of XML.
    *@throws IOException    When the Element can't be serialized.
    **************************************************************************/
    public static String serialize(Element xml)
                throws IOException
    {
        return serialize(xml, new StringWriter()).toString();
    }

    /**************************************************************************
    * Serialize the child Elements of the XML Node into the specified Writer, 
    * silently tolerating a null Node.
    *@param  xml            XML Node
    *@param  writer         The Writer to serialize to.
    *@return                The specified Writer.
    *@throws IOException    When the child Elements of the Node can't be 
    *                       serialized.
    **************************************************************************/
    public static Writer serializeChildElements(Node xml, Writer writer)
                throws IOException
    {
        if (xml == null)
        {
            return writer;
        }
        XMLSerializer objXMLSerializer = new XMLSerializer
                                                (writer, 
                                                 initOutputFormat());
        for (Node xmlChild = xml.getFirstChild();
             xmlChild != null;
             xmlChild = xmlChild.getNextSibling())
        {
            if (xmlChild.getNodeType() == Node.ELEMENT_NODE)
            {
                objXMLSerializer.serialize((Element)xmlChild);
            }
        }
        return writer;
    }

    /**************************************************************************
    * Serialize the child Elements of the XML Node into the specified 
    * OutputStream, silently tolerating a null Node.
    *@param  xml            XML Node
    *@param  out            The OutputStream to serialize to.
    *@return                The specified OutputStream.
    *@throws IOException    When the child Elements of the Node can't be 
    *                       serialized.
    **************************************************************************/
    public static OutputStream serializeChildElements(Node xml, OutputStream out)
                throws IOException
    {
        serializeChildElements(xml, new OutputStreamWriter(out));
        return out;
    }

    /**************************************************************************
    * Serialize the child Elements of the XML Node into a string of XML
    *@param  xml            XML Node
    *@return                The string of XML containing the child Elements.
    *@throws IOException    When the child Elements of the Node can't be 
    *                       serialized.
    **************************************************************************/
    public static String serializeChildElements(Node xml)
                throws IOException
    {
        return serializeChildElements(xml, new StringWriter()).toString();
    }

    /**************************************************************************
    * Wraps the info from a Java Throwable in an XML String.
    *@param  e              Throwable to wrap as XML.
    *@param  objSource      Object throwing the Throwable.
    *@return                String containing XML.
    *@throws IOException    When the XML can't be serialized.
    **************************************************************************/
    public static String wrapThrowableInXMLString(Throwable e, Object objSource)
                throws IOException
    {
        return serialize(wrapThrowableInXML(e, objSource));
    }

    /**************************************************************************
    * Wraps the info from a Java Throwable in an XML String, without risk of
    * throwing another Throwable.  If unable to wrap the XML, it generates an
    * XML string saying so.
    *@param  e          Throwable to wrap as XML.
    *@param  objSource  Object throwing the Throwable.
    *@return            String containing XML.
    **************************************************************************/
    public static String wrapThrowableSafelyInXMLString
                        (Throwable e,
                         Object    objSource)
    {
        try
        {
            return wrapThrowableInXMLString(e, objSource);
        }
        //-- Note:  Must use Throwable here, not Exception, to be sure to
        //--        catch all possible errors.
        catch (Throwable ignoredThrowable)
        {
            //-- Create an XML stream to report the error without using the
            //-- Xerces classes, since they may be the source of the error.
            return "<Error>\n"
                 + "  <Name>" + e.getClass().getName() + "</Name>\n"
                 + "  <Source>" + objSource.getClass().getName() + "</Source>\n"
                 + "  <Message>" + e.getMessage() + "</Message>\n"
                 + "  <Note>An additional error occurred and was suppressed "
                 +    "while formatting this error message as XML.</Note>\n"
                 + "  <StackTrace>" + ExcUtil.getStackTrace(e) + "</StackTrace>\n"
                 + "</Error>\n"
                 ;
        }
    }

    /**************************************************************************
    * Get the value of the text node that is embedded in the node that is
    * located at the specified XPath from the specified node.
    *@param  xmlNode    Node to search from.
    *@param  strXPath   XPath to get from xmlNode to the desired node.
    *@return            String value of the text node.
    *@throws TransformerException
    *                   When the specified XPath is invalid.
    *@throws NoSuchXMLNodeException
    *                   When the node specified by the XPath doesn't exist.
    **************************************************************************/
    public static String getTextNodeValueViaXPath
                        (Node   xmlNode,
                         String strXPath)
                         throws TransformerException
                               ,NoSuchXMLNodeException
    {
        //-- Note:  XPath indexes are one-based, not zero-based.
        Node xmlTargetNode = 
                    XPathAPI.selectSingleNode(xmlNode, strXPath + "/text()[1]");
        if (xmlTargetNode == null)
        {
            throw new NoSuchXMLNodeException(strXPath);
        }
        return xmlTargetNode.getNodeValue();
    }

    /**************************************************************************
    * Get the value of the text node that is embedded in the node that is
    * located at the specified XPath from the specified node.  If not found,
    * return the empty string instead.
    *@param  xmlNode    Node to search from.
    *@param  strXPath   XPath to get from xmlNode to the desired node.
    *@return            String value of the text node, or empty string.
    *@throws TransformerException
    *                   When the specified XPath is invalid.
    **************************************************************************/
    public static String getTextNodeValueOrEmptyStringViaXPath
                        (Node   xmlNode,
                         String strXPath)
                         throws TransformerException
    {
        try
        {
            return getTextNodeValueViaXPath (xmlNode, strXPath);
        }
        catch (NoSuchXMLNodeException e)
        {
            return "";
        }
    }

    /**************************************************************************
    * Get the int value of the text node that is embedded in the node that is
    * located at the specified XPath from the specified node, defaulting to 0
    * if the text node has an empty value.
    *@param  xmlNode    Node to search from.
    *@param  strXPath   XPath to get from xmlNode to the desired node.
    *@return            int value of the text node.
    *@throws TransformerException
    *                   When the specified XPath is invalid.
    *@throws NoSuchXMLNodeException
    *                   When the node specified by the XPath doesn't exist.
    *@throws NumberFormatException
    *                   When the value of the text node is not "" or an integer.
    **************************************************************************/
    public static int getIntTextNodeValueViaXPath
                        (Node   xmlNode,
                         String strXPath)
                         throws TransformerException
                               ,NoSuchXMLNodeException
                               ,NumberFormatException
    {
        String strRC = getTextNodeValueViaXPath(xmlNode, strXPath);
        return ObjUtil.equalsOrBothNull(strRC, "")
               ? 0
               : Integer.parseInt(strRC);
    }

    /**************************************************************************
    * Set the value of the text node that is embedded in the node that is
    * located at the specified XPath from the specified node.  If the node
    * specified by the XPath exists, but the text node doesn't, create the
    * text node.
    *@param  xmlNode    Node to search from.
    *@param  strXPath   XPath to get from xmlNode to the desired node.
    *@param  strText    String to put in text node.
    *@throws TransformerException
    *                   When the specified XPath is invalid.
    *@throws NoSuchXMLNodeException
    *                   When the node specified by the XPath doesn't exist.
    **************************************************************************/
    public static void setTextNodeValueViaXPath
                        (Node   xmlNode,
                         String strXPath,
                         String strText)
                       throws TransformerException
                             ,NoSuchXMLNodeException
    {
        //-- Get the node at the specified XPath.
        Node xmlTargetNode = XPathAPI.selectSingleNode(xmlNode, strXPath);
        if (xmlTargetNode == null)
        {
            throw new NoSuchXMLNodeException(strXPath);
        }

        //-- Delete all text nodes of the specified node, and add a new text
        //-- node.
        //-- Note:  This makes sense because XML doesn't provide a way to 
        //--        easily distinguish between multiple text nodes of a node,
        //--        so having multiple text nodes is not generally useful.
        Node xmlChildNode = xmlTargetNode.getFirstChild();
        while (xmlChildNode != null)
        {
            Node xmlTemp = xmlChildNode.getNextSibling();
            if (xmlChildNode.getNodeType() == Node.TEXT_NODE)
            {
                xmlTargetNode.removeChild(xmlChildNode);
            }
            xmlChildNode = xmlTemp;
        }
        appendTextNode(xmlTargetNode, strText);
    }

    /**************************************************************************
    * Set the value of the specified XML Node.
    *@param  xmlNode    Node to search from.
    *@param  strXPath   XPath to get from xmlNode to the desired Node.
    *@param  strText    New value of the node.
    *@throws TransformerException
    *                   When the specified XPath is invalid.
    *@throws NoSuchXMLNodeException
    *                   When the node specified by the XPath doesn't exist.
    **************************************************************************/
    public static void setNodeValueViaXPath
                        (Node   xmlNode,
                         String strXPath,
                         String strText)
                       throws TransformerException
                             ,NoSuchXMLNodeException
    {
        //-- Get the node at the specified XPath.
        Node xmlTargetNode = XPathAPI.selectSingleNode(xmlNode, strXPath);
        if (xmlTargetNode == null)
        {
            throw new NoSuchXMLNodeException(strXPath);
        }
        xmlTargetNode.setNodeValue(strText);
    }

    /**************************************************************************
    * Get the child Element with the specified name.  If not found, append a 
    * new child Element to the specified Node.
    *@param  xmlNode    Node to append child to.
    *@param  strName    Name of the Element to get or create.
    *@return            Element that was found or created.
    *@throws TransformerException
    *                   When an error occurs searching the XML via XPath,
    *                   probably because of an invalid value for strName. 
    **************************************************************************/
    public static Element getOrAppendElement
                        (Node     xmlNode,
                         String   strName)
                       throws TransformerException
    {
        Element xmlRC = (Element)XPathAPI.selectSingleNode(xmlNode, strName);
        if (xmlRC == null)
        {
            xmlRC = appendElement(xmlNode, strName);
        }
        return xmlRC;
    }

    /**************************************************************************
    * Get the child Element with the specified name.  If not found, append a new
    * child Element to the specified Node.  Then set the value of the Text node 
    * inside the child Element.
    *@param  xmlNode    Node to append child to.
    *@param  strName    Name of the Element to get or create.
    *@param  strValue   String to put in text node.
    *@throws TransformerException
    *                   When an error occurs searching the XML via XPath,
    *                   probably because of an invalid value for strName. 
    **************************************************************************/
    public static void getOrAppendElementAndSetTextNodeValue
                        (Node     xmlNode,
                         String   strName,
                         String   strValue)
                       throws TransformerException
    {
        //?? Good idea to keep this in XMLUtil?  It is kind of specialized.
        //?? It uses strName as an XPath (not just a simple name) when looking
        //?? for an existing node, but uses it as a simple node name when 
        //?? creating a node.  Might be better to move it back to Config.java
        //?? which is the only place to use it so far.
        try
        {
            setTextNodeValueViaXPath(xmlNode, strName, strValue);
        }
        catch (NoSuchXMLNodeException e)
        {
            //-- Element not found.  Create it and try again.
            appendElement(xmlNode, strName);
            try
            {
                setTextNodeValueViaXPath(xmlNode, strName, strValue);
            }
            catch (NoSuchXMLNodeException e2)
            {
                //-- Not possible.  We just added it.
            }
        }
    }

    /**************************************************************************
    * Remove all child nodes from the specified Node.
    *@param  xmlNode    Node to remove from.
    **************************************************************************/
    public static void removeAllChildren(Node xmlNode)
    {
        Node xmlChildNode = xmlNode.getFirstChild();
        while (xmlChildNode != null)
        {
            Node xmlTemp = xmlChildNode.getNextSibling();
            xmlNode.removeChild(xmlChildNode);
            xmlChildNode = xmlTemp;
        }
    }

    /**************************************************************************
    * Returns the first child Element of a Node, or null if no child Element 
    * found. 
    *@param  xml    Node to find first child Element of
    *@return        The first child Element or null.
    **************************************************************************/
    public static Element getFirstChildElement(Node xml)
    {
        xml = xml.getFirstChild();
        while (xml != null && xml.getNodeType() != Node.ELEMENT_NODE)
        {
            xml = xml.getNextSibling();
        }
        return (Element)xml;
    }

    /**************************************************************************
    * Returns the root Element of a Document, or null if no root found. 
    *@param  dom    Document to find root Element of
    *@return        The root Element or null.
    **************************************************************************/
    public static Element getRootElement(Document dom)
    {
        return getFirstChildElement(dom);
    }

    /**************************************************************************
    * Constant for use as parameter to insertBefore. 
    **************************************************************************/
    public static final Node nodeINSERT_AFTER_LAST = null;

    /**************************************************************************
    * Inserts xmlNewChild as a child of xmlParent before the existing child 
    * xmlRefChild, or as the last child if xmlRefChild is null, copying the 
    * new Node and its entire subtree from a different Document if necessary.
    *<pre>
    * Note:  This is identical to org.w3c.dom.Node.insertBefore() except that 
    *        1.  It calls org.w3c.dom.Document.importNode() to copy the Node 
    *            and its subtree from another Document, if necessary to avoid 
    *            throwing an exception.
    *        2.  If xmlNewChild is an entire Document, it inserts the single 
    *            Element child of the Document (the root Element) instead of 
    *            the Document itself, to avoid throwing an exception.
    *</pre>
    *@param  xmlParent   Node to insert the child into
    *@param  xmlNewChild Node to insert as child
    *@param  xmlRefChild Existing child Node to insert before, or null.
    *@return             The inserted Node.
    **************************************************************************/
    public static Node insertBefore
                        (Node xmlParent,
                         Node xmlNewChild,
                         Node xmlRefChild)
    {
        if (xmlNewChild.getNodeType() == Node.DOCUMENT_NODE)
        {
            xmlNewChild = getRootElement((Document)xmlNewChild);
        }
        Document domParent = getOwnerDocument(xmlParent); 
        if (getOwnerDocument(xmlNewChild) != domParent)
        {
            final boolean blnImportEntireSubtree = true;
            xmlNewChild = domParent.importNode
                                    (xmlNewChild, blnImportEntireSubtree);
        }
        xmlParent.insertBefore(xmlNewChild, xmlRefChild);
        return xmlNewChild;
    }

    /**************************************************************************
    * Format the start version of the specified XML tag.
    *@param  strTag     XML tag (without angle brackets).
    *@return            The formatted XML string.
    **************************************************************************/
    public static String formatStartTag(String strTag)
    {
        return "<" + strTag + ">";
    }

    /**************************************************************************
    * Format the start version of the specified XML tag,
    * and include a set of attributes.
    *@param  strTag         XML tag (without angle brackets).
    *@param  strAttributes  Attributes to be included in the start tag.
    *@return                The formatted XML string.
    **************************************************************************/
    public static String formatStartTagAndAttributes(String strTag,
                                                     String strAttributes)
    {
        return "<" + strTag + " " + strAttributes + ">";
    }

    /**************************************************************************
    * Format the end version of the specified XML tag.
    *@param  strTag     XML tag (without angle brackets or slash).
    *@return            The formatted XML string.
    **************************************************************************/
    public static String formatEndTag(String strTag)
    {
        return "</" + strTag + ">";
    }

    /**************************************************************************
    * Write the start version of the specified XML tag to the specified 
    * Writer.
    *@param  writer         Writer to write start tag to.
    *@param  strTag         XML tag (without angle brackets).
    *@return                The specified Writer.
    *@throws IOException    When an error occurs writing to the Writer.
    **************************************************************************/
    public static Writer writeStartTag
                        (Writer   writer, 
                         String   strTag)
                 throws IOException
    {
        writer.write(formatStartTag(strTag));
        return writer;
    }

    /**************************************************************************
    * Write the start version of the specified XML tag to the specified 
    * Writer, including an attribute string.
    *@param  writer         Writer to write start tag to.
    *@param  strTag         XML tag (without angle brackets).
    *@param  strAttributes  Attributes to be included in the start tag.
    *@return                The specified Writer.
    *@throws IOException    When an error occurs writing to the Writer.
    **************************************************************************/
    public static Writer writeStartTagAndAttributes
                        (Writer   writer, 
                         String   strTag,
                         String   strAttributes)
                 throws IOException
    {
        writer.write(formatStartTagAndAttributes(strTag, strAttributes));
        return writer;
    }

    /**************************************************************************
    * Write the end version of the specified XML tag to the specified 
    * Writer.
    *@param  writer         Writer to write end tag to.
    *@param  strTag         XML tag (without angle brackets or slash).
    *@return                The specified Writer.
    *@throws IOException    When an error occurs writing to the Writer.
    **************************************************************************/
    public static Writer writeEndTag
                        (Writer   writer, 
                         String   strTag)
                 throws IOException
    {
        writer.write(formatEndTag(strTag));
        return writer;
    }

    /**************************************************************************
    * Write the specified value, enclosed in the start and end versions 
    * of the specified XML tag, to the specified Writer.
    *@param  writer         Writer to write to.
    *@param  strTag         XML tag (without angle brackets).
    *@param  strValue       String value to enclose in XML tags.
    *@return                The specified Writer.
    *@throws IOException    When an error occurs writing to the Writer.
    **************************************************************************/
    public static Writer writeTagAndValue
                        (Writer       writer, 
                         String       strTag,
                         String       strValue)
                 throws IOException
    {
        writeStartTag(writer, strTag);
        writer.write(strValue);
        writeEndTag(writer, strTag);
        return writer;
    }

    /**************************************************************************
    * Get an XML Document containing all Java system properties.
    *<xmp>
    * Format of XML is:
    *   <SystemProps>
    *    <SystemProp>
    *     <PropName>xxx</PropName>
    *     <PropValue>xxx</PropValue>
    *    </SystemProp>
    *     ...
    *   </SystemProps>
    *</xmp>
    *@return                The XML Document
    **************************************************************************/
    public static Document getSystemProperties()
    {
        Document dom = XMLUtil.createEmptyDocument();
        Node xmlSystemProps = XMLUtil.appendElement (dom, "SystemProps");
        for (Enumeration enumPropNames = System.getProperties().propertyNames();
             enumPropNames.hasMoreElements();
            ) 
        {
            String strName = (String) enumPropNames.nextElement();
            Node xmlSystemProp = XMLUtil.appendElement
                    (xmlSystemProps, "SystemProp");
            XMLUtil.appendElementContainingText
                    (xmlSystemProp, "PropName", strName);
            XMLUtil.appendElementContainingText
                    (xmlSystemProp, "PropValue", System.getProperty(strName));
        }
        return dom;
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
            Document dom = createEmptyDocument();
            xmlA = appendElement(dom, "A");
            xmlA1 = appendElement(xmlA, "A1");
            appendElementContainingText(xmlA1, "A1a", "A1a value");
            appendElementContainingText(xmlA1, "A1b", "A1b value");
            appendElement(xmlA, "A2");
            xmlA4 = appendElement(xmlA, "A4");
            appendElementContainingText(xmlA4, "A4a", "A4a value");
            appendElementContainingText(xmlA4, "A4b", "A4b value");
            appendElementContainingText(xmlA4, "A4c", "A4c value");
            return dom;
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
                System.out.println ("--");
                System.out.println ("-- Create XML tree and print children.");
                System.out.println ("--");
                //-------------------------------------------------------------
                Document dom = initDocument();
                serializeChildElements(xmlA, new OutputStreamWriter(System.out));

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- Print entire tree.");
                System.out.println ("--");
                //-------------------------------------------------------------
                serialize(dom, System.out);

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- Load a 2nd XML stream from a string.");
                System.out.println ("--");
                //-------------------------------------------------------------
                Document dom2 = loadDocumentFromString
                                    ("<A3>" +
                                       "<A3a>A3a value</A3a>" +
                                       "<A3b>" +
                                         "<A3b1>A3b1 value</A3b1>" +
                                         "<A3b2>A3b2 value</A3b2>" +
                                       "</A3b>" +
                                       "<A3c>A3c value</A3c>" +
                                       "<A3d>A3d value</A3d>" +
                                     "</A3>");
                serialize(dom2, System.out);

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- Insert one XML Document into another.");
                System.out.println ("--");
                //-------------------------------------------------------------
                insertBefore(xmlA, dom2, xmlA4);
                serialize(dom, System.out);

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- Insert one XML subtree into another.");
                System.out.println ("--");
                //-------------------------------------------------------------
                dom = initDocument();
                Node xmlA3b = XPathAPI.selectSingleNode(dom2, "//A3b");
                insertBefore(xmlA, xmlA3b, xmlA4);
                serialize(dom, System.out);

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- getSystemProperties()");
                System.out.println ("--");
                //-------------------------------------------------------------
                serialize(getSystemProperties(), System.out);

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- Modify a text value in an XML tree.");
                System.out.println ("--");
                //-------------------------------------------------------------
                dom = initDocument();
                setTextNodeValueViaXPath(dom, "//A1b", "A1b new value");
                serialize(dom, System.out);

                System.out.println ("...End tests.");

                //-------------------------------------------------------------
                System.out.println ("--");
                System.out.println ("-- Modify an attribute value in an XML tree.");
                System.out.println ("--");
                //-------------------------------------------------------------
                Document dom3 = loadDocumentFromString
                                    ("<A>" +
                                       "<B>" +
                                         "<B1 attr1='old'>B1 value</B1>" +
                                         "<B2 attr1='old'>B2 value</B2>" +
                                       "</B>" +
                                     "</A>");
                setNodeValueViaXPath(dom3, "//B2/@attr1", "new");
                serialize(dom3, System.out);

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
