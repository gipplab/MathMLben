package org.citeplag.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * TODO
 *
 * @author Vincent Stange
 */
public class XMLUtils {

    /**
     * Only return child nodes that are elements, ignore text passages.
     *
     * @param node We will take the children from this node.
     * @return New ordered list of child elements.
     */
    public static ArrayList<Element> getChildElements(Node node) {
        ArrayList<Element> childElements = new ArrayList<>();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element)
                childElements.add((Element) childNodes.item(i));
        }
        return childElements;
    }

    /**
     * TODO
     *
     * @param node
     * @return
     * @throws TransformerException
     */
    public static String nodeToString(Node node) throws TransformerException {
        return nodeToString(node, true);
    }

    /**
     * Prints out a XML node as a String
     *
     * @param node   node to be translated
     * @param indent pretty print on?
     * @return String representation
     * @throws TransformerException mostly not xml conform
     */
    public static String nodeToString(Node node, boolean indent) throws TransformerException {
        StringWriter sw = new StringWriter();
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
        t.transform(new DOMSource(node), new StreamResult(sw));
        return sw.toString();
    }
}
