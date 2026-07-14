


package ai.chat2db.community.domain.core.impl.ncx;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class XMLUtils {

    public static Document parseDocument(String fileName) {
        return parseDocument(new java.io.File(fileName));
    }

    public static Document parseDocument(java.io.File file) {
        try (InputStream is = new FileInputStream(file)) {
            return parseDocument(new InputSource(is));
        } catch (IOException e) {
            throw new RuntimeException("Error opening file '" + file + "'", e);
        }
    }

    public static Document parseDocument(InputStream is) {
        return parseDocument(new InputSource(is));
    }

    public static Document parseDocument(java.io.Reader is) {
        return parseDocument(new InputSource(is));
    }

    public static Document parseDocument(InputSource source) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            DocumentBuilder xmlBuilder = dbf.newDocumentBuilder();
            return xmlBuilder.parse(source);
        } catch (Exception er) {
            throw new RuntimeException("Error parsing XML document", er);
        }
    }

    public static Document createDocument() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder xmlBuilder = dbf.newDocumentBuilder();
            return xmlBuilder.newDocument();
        } catch (Exception er) {
            throw new RuntimeException("Error creating XML document", er);
        }
    }

    public static Element getChildElement(Element element,  String childName) {
        if (element == null) {
            return null;
        }
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == Node.ELEMENT_NODE &&
                ((Element) node).getTagName().equals(childName)) {
                return (Element) node;
            }
        }
        return null;
    }

    public static String getChildElementBody(Element element,  String childName) {
        if (element == null) {
            return null;
        }
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == Node.ELEMENT_NODE &&
                ((Element) node).getTagName().equals(childName)) {
                return getElementBody((Element) node);
            }
        }
        return null;
    }

    public static String getElementBody( Element element) {
        return element.getTextContent();
    }

    public static List<Element> getChildElementList(
        Element parent,
        String nodeName) {
        List<Element> list = new ArrayList<>();
        if (parent != null) {
            for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeType() == Node.ELEMENT_NODE &&
                    nodeName.equals(node.getNodeName())) {
                    list.add((Element) node);
                }
            }
        }
        return list;
    }

    public static Collection<Element> getChildElementListNS(
        Element parent,
        String nsURI) {
        List<Element> list = new ArrayList<>();
        if (parent != null) {
            for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeType() == Node.ELEMENT_NODE &&
                    node.getNamespaceURI().equals(nsURI)) {
                    list.add((Element) node);
                }
            }
        }
        return list;
    }
    public static Collection<Element> getChildElementListNS(
        Element parent,
        String nodeName,
        String nsURI) {
        List<Element> list = new ArrayList<>();
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == Node.ELEMENT_NODE &&
                node.getLocalName().equals(nodeName) &&
                node.getNamespaceURI().equals(nsURI)) {
                list.add((Element) node);
            }
        }
        return list;
    }

    public static Collection<Element> getChildElementList(
        Element parent,
        String[] nodeNameList) {
        List<Element> list = new ArrayList<>();
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < nodeNameList.length; i++) {
                    if (node.getNodeName().equals(nodeNameList[i])) {
                        list.add((Element) node);
                    }
                }
            }
        }
        return list;
    }
    public static Element findChildElement(
        Element parent) {
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) node;
            }
        }
        return null;
    }

    public static Object escapeXml(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof CharSequence) {
            return escapeXml((CharSequence) obj);
        } else {
            return obj;
        }
    }

    public static String escapeXml(CharSequence str) {
        if (str == null) {
            return null;
        }
        StringBuilder res = null;
        int strLength = str.length();
        for (int i = 0; i < strLength; i++) {
            char c = str.charAt(i);
            String repl = encodeXMLChar(c);
            if (repl == null) {
                if (res != null) {
                    res.append(c);
                }
            } else {
                if (res == null) {
                    res = new StringBuilder(str.length() + 5);
                    for (int k = 0; k < i; k++) {
                        res.append(str.charAt(k));
                    }
                }
                res.append(repl);
            }
        }
        return res == null ? str.toString() : res.toString();
    }

    public static boolean isValidXMLChar(char c) {
        return (c >= 32 || c == '\n' || c == '\r' || c == '\t');
    }


    public static String encodeXMLChar(char ch) {
        switch (ch) {
            case '&':
                return "&amp;";
            case '\"':
                return "&quot;";
            case '\'':
                return "&#39;";
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            default:
                return null;
        }
    }

    public static RuntimeException adaptSAXException(Exception toCatch) {
        if (toCatch instanceof RuntimeException) {
            return (RuntimeException) toCatch;
        } else if (toCatch instanceof org.xml.sax.SAXException) {
            String message = toCatch.getMessage();
            Exception embedded = ((org.xml.sax.SAXException) toCatch).getException();
            if (embedded != null && embedded.getMessage() != null && embedded.getMessage().equals(message)) {
                return adaptSAXException(embedded);
            } else {
                return new RuntimeException(
                    message,
                    embedded != null ? adaptSAXException(embedded) : null);
            }
        } else {
            return new RuntimeException(toCatch.getMessage(), toCatch);
        }
    }

    public static Collection<Element> getChildElementList(Element element) {
        List<Element> children = new ArrayList<>();
        if (element != null) {
            for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    children.add((Element) node);
                }
            }
        }
        return children;
    }
}
