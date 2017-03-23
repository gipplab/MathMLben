package org.citeplag.translate.speech;

import org.apache.log4j.Logger;
import org.citeplag.mml.CMMLInfo;
import org.citeplag.mml.XMLHelper;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Enriched MathML > Full MathML
 *
 * @author Vincent Stange
 */
public class OwnTransformator {

    private static Logger logger = Logger.getLogger(OwnTransformator.class);

    private static HashMap<String, String> symbolMap = new HashMap<>();

    static {
        // basics
        symbolMap.put("addition", "plus");
        symbolMap.put("subtraction", "minus");
        symbolMap.put("multiplication", "times");
        symbolMap.put("division", "divide");

        // advanced
        symbolMap.put("sqrt", "root");

        // identifiers
        symbolMap.put("identifier", "ci");
        symbolMap.put("number", "cn");
    }

    private final Document readDocument;

    private final Document writeDocument;

    private int uniqueIdCounter;

    public OwnTransformator(String readXml) throws IOException, ParserConfigurationException {
        readDocument = XMLHelper.String2Doc(readXml, true);
        writeDocument = XMLHelper.getNewDocument(true);
    }

    public String getFullMathML() throws Exception {
        XPath xpath = XMLHelper.namespaceAwareXpath("m", CMMLInfo.NS_MATHML);
        Element semanticRoot = (Element) xpath.compile("*//m:semantics").evaluate(readDocument, XPathConstants.NODE);

        // System.out.println(nodeToString(readDocument.getFirstChild(), true));

        // secure the id field
        convertIdField(semanticRoot);

        Element firstRow = (Element) xpath.compile("*//m:mrow").evaluate(readDocument, XPathConstants.NODE);
        // create new annotation
        Element cmmlRoot = writeDocument.createElement("annotation-xml");
        cmmlRoot.setAttribute("encoding", "MathML-Content");

        // create the cmml root-apply node
        createCmml(firstRow, cmmlRoot);

        // adopt the new cmml structure into the original enriched mathml
        Node copy = readDocument.adoptNode(cmmlRoot.cloneNode(true));
        semanticRoot.appendChild(copy);

        // currently fix the whole ns to MathML standard, may be changed later on
        XMLHelper.useFixNamespace(readDocument, CMMLInfo.NS_MATHML);

        return nodeToString(readDocument.getFirstChild(), true);
    }

    /**
     * Copy the "data-semantic-id" attribute to "id", if it does not exist
     *
     * @param readNode element to change
     */
    private void convertIdField(Element readNode) {
        String newId = readNode.getAttribute("data-semantic-id");
        if (!StringUtils.isEmpty(newId)) {
            readNode.setAttribute("id", newId);
        }
        for (Element child : getChildElements(readNode)) {
            convertIdField(child);
        }
    }

    /**
     * This is main recursive method to build a Content MathML.
     * It will usually start with the first mrow-tag of the PMML.
     *
     * @param readNode  current node to be read
     * @param writeNode current to write on
     * @return the current node created,
     * if this is the first call it will typically return the root-apply node.
     * @throws Exception mostly NPE because of errors :)
     */
    Node createCmml(Element readNode, Element writeNode) throws Exception {
        if ("mi".equals(readNode.getNodeName()) ||
                "mo".equals(readNode.getNodeName()) ||
                "mn".equals(readNode.getNodeName())) {
            // normal node
            Element newCmmlNode = createSingleCmmlNode(readNode, true);
            writeNode.appendChild(newCmmlNode);
            return newCmmlNode;
        } else if ("mrow".equals(readNode.getNodeName())) {
            // 1. Build apply node
            Element apply = writeDocument.createElement("apply");
            String pmmlId = readNode.getAttribute("data-semantic-id");
            if (Objects.equals(pmmlId, "")) {
                // go one element deeper, since this is an encapsulate
                return createCmml(getChildElements(readNode).get(0), writeNode);
            }

            // 1a. further build a normal apply node
            apply.setAttribute("id", "c" + pmmlId);
            apply.setAttribute("xref", pmmlId);
            writeNode.appendChild(apply);

            // 2. Take content identifiers
            String rawContentIds = readNode.getAttribute("data-semantic-content");
            String[] contentIds = rawContentIds.split(",");
            if (!"".equals(contentIds[0])) {
                // take first operator
                Element contentChild = getChildById(getChildElements(readNode), contentIds[0]);
                Element operator = createSingleCmmlNode(contentChild, true);
                apply.appendChild(operator);
            }

            // 3a. filter children
            String rawChildrenIds = readNode.getAttribute("data-semantic-children");
            // 3. Take children
            String[] childrenIds = rawChildrenIds.split(",");
            for (String childId : childrenIds) {
                Element readChild = getChildById(getChildElements(readNode), childId);
                createCmml(readChild, apply);
            }

            return apply;
        } else {
            // direct operator

            // 1. Build apply node
            // take the Ids from the current node for a new apply node
            Element apply = writeDocument.createElement("apply");
            String tmpPmmlId = readNode.getAttribute("data-semantic-id");
            apply.setAttribute("id", "c" + tmpPmmlId);
            apply.setAttribute("xref", tmpPmmlId);
            writeNode.appendChild(apply);

            // 2. take operator
            Element operator = createSingleCmmlNode(readNode, false);
            apply.appendChild(operator);

            // 3a. filter children
            String rawChildrenIds = readNode.getAttribute("data-semantic-children");
            // 3. Take children
            String[] childrenIds = rawChildrenIds.split(",");
            for (String childId : childrenIds) {
                Element readChild = getChildById(getChildElements(readNode), childId);
                createCmml(readChild, apply);
            }

            return apply;
        }
    }

    /**
     * TODO
     *
     * @param readNode
     * @param useId
     * @return
     * @throws TransformerException
     */
    private Element createSingleCmmlNode(Element readNode, boolean useId) throws TransformerException {
        // set symbol by role and type
        String role = readNode.getAttribute("data-semantic-role");
        String type = readNode.getAttribute("data-semantic-type");
        String symbol = getSymbol(role, type);
        Element e = writeDocument.createElement(symbol);
        // set cd
        e.setAttribute("cd", type);
        // set ids
        String pmmlId = readNode.getAttribute("data-semantic-id");
        if (useId) {
            e.setAttribute("id", "c" + pmmlId);
        } else {
            e.setAttribute("id", "u" + String.valueOf(uniqueIdCounter++));
        }
        e.setAttribute("xref", pmmlId);
        // set content
        e.setTextContent(getNodeValue(readNode));
        return e;
    }

    /**
     * Conversion of the symbol / tag name for an element.
     *
     * @param roleKey the role will be seached for as first
     * @param typeKey the type will be searched for as second
     * @return either a mapped value for the role or type, or if none is found will return typekey.
     */
    private String getSymbol(String roleKey, String typeKey) {
        String s = symbolMap.get(roleKey);
        if (s == null) {
            return symbolMap.getOrDefault(typeKey, typeKey);
        }
        return s;
    }

    /**
     * TODO
     *
     * @param childElements
     * @param searchId
     * @return
     */
    private Element getChildById(ArrayList<Element> childElements, String searchId) {
        for (Element ele : childElements) {
            if (ele.getAttribute("data-semantic-id").equals(searchId)) {
                return ele; // found
            }
            // now look for children
            ArrayList<Element> moreChildren = getChildElements(ele);
            if (moreChildren.size() > 0) {
                Element childById = getChildById(moreChildren, searchId);
                if (childById != null)
                    return childById; // just pass through
            }
        }
        return null;
    }

    /**
     * Only return child nodes that are elements, ignore text passages.
     *
     * @param node We will take the children from this node.
     * @return New ordered list of child elements.
     */
    ArrayList<Element> getChildElements(Node node) {
        ArrayList<Element> childElements = new ArrayList<>();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element)
                childElements.add((Element) childNodes.item(i));
        }
        return childElements;
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
