package org.citeplag.search;

import org.springframework.util.StringUtils;
import org.w3c.dom.*;
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
 * This class converts MathML into our own tree representation of a
 * mathematical formula.
 *
 * @author Vincent Stange
 */
public class MathNodeGenerator {

    private DocumentBuilderFactory domFactory;

    public MathNodeGenerator() {
        // Initialize these objects only once per process instance
        this.domFactory = DocumentBuilderFactory.newInstance();
        this.domFactory.setNamespaceAware(false); // ignore all namespaces
    }

    public MathNode generateMathNode(Node cmmlRoot) throws Exception {
        return createMathNode(cmmlRoot);
    }

    public Node getCmmlRoot(String mathml) throws Exception {
        // use only the Content MathML semantics
        Document mathmlDocument = parseDocument(mathml);
        XPath xPath = XPathFactory.newInstance().newXPath();
        Node cmmlRoot = (Node) xPath.evaluate("//annotation-xml[@encoding='MathML-Content']/apply", mathmlDocument, XPathConstants.NODE);
        return cmmlRoot;
    }

    public String generateAbstractCD(String mathml) throws Exception {
        return nodeToString(generateAbstractCDNode(mathml), true);
    }

    public Node generateAbstractCDNode(String mathml) throws Exception {
        // use only the Content MathML semantics
        Document mathmlDocument = parseDocument(mathml);
        XPath xPath = XPathFactory.newInstance().newXPath();
        Node cmmlRoot = (Node) xPath.evaluate("//annotation-xml[@encoding='MathML-Content']/apply", mathmlDocument, XPathConstants.NODE);

        //System.out.println(nodeToString(cmmlRoot, true));
        abstractNodeCD(mathmlDocument, cmmlRoot);
        return cmmlRoot;
    }

    /**
     * recursive method
     *
     * @param node current xml node in cmml
     * @return converted MathNode we use in this application
     * @throws TransformerException xml reading error
     */
    MathNode createMathNode(Node node) throws TransformerException {
        MathNode mathNode = new MathNode();
        ArrayList<Node> childNodes = getChildElements(node);

        /* Since the current renderer always refers to the first child
         * as the current display node, I mash the apply and operation node together. */
        if ("apply".equals(node.getNodeName())) {
            if (childNodes.size() > 0) {
                mathNode.setOperator(createMathNode(childNodes.get(0)));
                childNodes.remove(0);
            }
        }

        mathNode.setName(node.getNodeName());
        mathNode.setAttributes(node.getAttributes());
        mathNode.setValue(node.getFirstChild() != null ? node.getFirstChild().getTextContent().trim() : node.getTextContent().trim());

        if (childNodes.size() > 0) {
            for (Node childNode : childNodes) {
                mathNode.addChild(createMathNode(childNode));
            }
        }
        return mathNode;
    }

    /**
     * Only return child nodes that are elements, ignore text passages.
     *
     * @param node We will take the children from this node.
     * @return New ordered list of child elements.
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
     * @param cmmlDoc
     * @param node
     */
    void abstractNodeCD(Document cmmlDoc, Node node) {
        ArrayList<Node> childElements = getChildElements(node);
        if (childElements.size() > 0) {
            for (Node childElement : childElements) {
                abstractNodeCD(cmmlDoc, childElement);
            }
        } else {
            node.setTextContent("");
        }

        String cd;
        try {
            cd = node.getAttributes().getNamedItem("cd").getNodeValue();
        } catch (Exception e) {
            cd = "";
        }
        if (!StringUtils.isEmpty(cd)) {
            try {
                cmmlDoc.renameNode(node, "http://formulasearchengine.com/ns/pseudo/gen/cd", cd);
            } catch (final DOMException e) {
                e.printStackTrace();
            }
        }

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

    private Document parseDocument(String documentContent) throws IOException, ParserConfigurationException, SAXException {
        try {
            InputSource inputStream = new InputSource(new StringReader(documentContent));
            return domFactory.newDocumentBuilder().parse(inputStream);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw e;
        }
    }

}
