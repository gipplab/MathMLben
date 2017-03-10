package org.citeplag.search;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * @author Vincent Stange
 */
public class Generator {

    private DocumentBuilderFactory domFactory;

    public Generator() {
        // Initialize these objects only once per process instance
        this.domFactory = DocumentBuilderFactory.newInstance();
        this.domFactory.setNamespaceAware(false); // ignore all namespaces
    }

    public MathNode generateMathNode(String mathml) throws Exception {
        // use only the Content MathML semantics
        Document mathmlDocument = parseDocument(mathml);
        XPath xPath = XPathFactory.newInstance().newXPath();
        Node cmmlRoot = (Node) xPath.evaluate("//annotation-xml[@encoding='MathML-Content']/apply", mathmlDocument, XPathConstants.NODE);

        //printNodeList(rootNode.getChildNodes());
        return createMathNode(cmmlRoot);
    }

    /**
     * recursive method
     *
     * @param node
     * @return
     * @throws TransformerException
     */
    MathNode createMathNode(Node node) throws TransformerException {
        MathNode mathNode = new MathNode();
        ArrayList<Node> childNodes = getChildElements(node);

        // TODO the current renderer always refers to the first child as the current display node
        // if it's an "apply" node - look at the first child
        int cIndex = 0;
        if ("apply".equals(node.getNodeName())) {
            if (childNodes.size() > 0) {
                mathNode.operator = createMathNode(childNodes.get(cIndex++));
                childNodes.remove(0);
            }
        }

        mathNode.setName(node.getNodeName());
        mathNode.setAttributes(node.getAttributes());
        mathNode.value = node.getFirstChild() != null ? node.getFirstChild().getTextContent().trim() : node.getTextContent().trim();

        if (childNodes.size() > 0) {
            for (Node childNode : childNodes) {
                mathNode.children.add(createMathNode(childNode));
            }
        }
        return mathNode;
    }

    /**
     * Only return child nodes that are elements.
     *
     * @param node
     * @return
     */
    ArrayList<Node> getChildElements(Node node) {
        ArrayList<Node> childElements = new ArrayList<>();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element)
                childElements.add(childNodes.item(i));
        }
        return childElements;
    }

    /**
     * Prints out a XML node as a String
     *
     * @param node   node to be translated
     * @param indent pretty print on?
     * @return String representation
     * @throws TransformerException mostly not xml conform
     */
    String nodeToString(Node node, boolean indent) throws TransformerException {
        StringWriter sw = new StringWriter();
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
        t.transform(new DOMSource(node), new StreamResult(sw));
        return sw.toString();
    }

    Document parseDocument(String documentContent) throws IOException, ParserConfigurationException, SAXException {
        try {
            InputSource inputStream = new InputSource(new StringReader(documentContent));
            return domFactory.newDocumentBuilder().parse(inputStream);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw e;
        }
    }

}
