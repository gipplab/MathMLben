package org.citeplag.node;

import org.citeplag.mml.CMMLInfo;
import org.citeplag.mml.XMLHelper;
import org.springframework.util.StringUtils;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
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
        // Convert to normal document
        Document mathmlDocument = XMLHelper.String2Doc(mathml, false);

        // get the apply node of the ContentMathML root
        XPath xpath = XMLHelper.namespaceAwareXpath("m", CMMLInfo.NS_MATHML);
        return (Node) xpath.compile("*//m:annotation-xml[@encoding='MathML-Content']/m:apply").evaluate(mathmlDocument, XPathConstants.NODE);
    }

    public String generateAbstractCD(String mathml) throws Exception {
        return nodeToString(generateAbstractCDNode(mathml), true);
    }

    public Node generateAbstractCDNode(String mathml) throws Exception {
        // convert to mathosphere cmml document
        CMMLInfo cmmlInfo = new CMMLInfo(mathml);

        // transform ContentMathML to Strict ContentMathML
        cmmlInfo = cmmlInfo.toStrictCmml();

        // get the apply node of the ContentMathML root
        XPath xpath = XMLHelper.namespaceAwareXpath("m", CMMLInfo.NS_MATHML);
        Node strictCmmlRoot = (Node) xpath.compile("*//m:annotation-xml[@encoding='MathML-Content']/m:apply").evaluate(cmmlInfo, XPathConstants.NODE);

        // our own abstract method for the strict CMML root
        abstractNodeCD(cmmlInfo, strictCmmlRoot);
        return strictCmmlRoot;
    }

    /**
     * Main recursive method to create our math tree
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

    String getNodeValue(Node node) {
        return node.getFirstChild() != null ? node.getFirstChild().getTextContent().trim() : node.getTextContent().trim();
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

}
