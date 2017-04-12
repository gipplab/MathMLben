package org.citeplag.mml;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Taken from mathosphere
 */
public class CMMLInfo implements Document {

    //For XML math processing
    public static final String NS_MATHML = "http://www.w3.org/1998/Math/MathML";
    protected static final Log LOG = LogFactory.getLog(CMMLInfo.class);
    private static final String FN_PATH_FROM_ROOT = "declare namespace functx = \"http://www.functx.com\";\n" +
            "declare function functx:path-to-node\n" +
            "  ( $nodes as node()* )  as xs:string* {\n" +
            "\n" +
            "$nodes/string-join(ancestor-or-self::*/name(.), '/')\n" +
            " } ;";
    private static final String XQUERY_HEADER = "declare default element namespace \"http://www.w3.org/1998/Math/MathML\";\n" +
            FN_PATH_FROM_ROOT +
            "<result>{";
    public static final String ROBERT_MINER_XSL = "org/citeplag/node/RobertMinerC2s.xsl";
    final String XQUERY_FOOTER = "<element><x>{$x}</x><p>{data(functx:path-to-node($x))}</p></element>}\n" +
            "</result>";
    private final static String FN_PATH_FROM_ROOT2 = "declare function path-from-root($x as node()) {\n" +
            " if ($x/parent::*) then\n" +
            " concat( path-from-root($x/parent::*), \"/\", node-name($x) )\n" +
            " else\n" +
            " concat( \"/\", node-name($x) )\n" +
            " };\n";
    private static final String MATH_HEADER = "<?xml version=\"1.0\" ?>\n" +
            "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n" +
            "<semantics>\n";
    private static final String MATH_FOOTER = "</semantics>\n" +
            "</math>";
    private static final List formulaIndicators = Arrays.asList(
            "eq",
            "neq",
            "le",
            "ge",
            "leq",
            "geq",
            "equivalent"
    );
    private Document cmmlDoc;

    public CMMLInfo(Document cmml) {
        constructor(cmml, true, false);
    }

    public CMMLInfo(String s) throws IOException, ParserConfigurationException {
        Document cmml = XMLHelper.String2Doc(s, true);
        constructor(cmml, true, false);
    }

    public CMMLInfo(CMMLInfo other) {
        cmmlDoc = (Document) other.cmmlDoc.cloneNode(true);
    }

    public static CMMLInfo newFromSnippet(String snippet) throws IOException, ParserConfigurationException {
        return new CMMLInfo(MATH_HEADER + snippet + MATH_FOOTER);
    }

    public final Document getDoc() {
        return cmmlDoc;
    }

    private void fixNamespaces() {
        Node math = new NonWhitespaceNodeList(cmmlDoc.getElementsByTagNameNS("*", "math")).getFirstElement();
        if (math == null) {
            try {
                LOG.error("No mathml element found in:\n" + XMLHelper.printDocument(cmmlDoc));
            } catch (TransformerException e) {
                LOG.error("No mathml element found in unpritnabel input.");
            }
            return;
        }
        try {
            math.getAttributes().removeNamedItem("xmlns");
        } catch (final DOMException e) {
            //Remove if it exists, ignore any errors thrown if it does not exist
        }
        new XmlNamespaceTranslator()
                .setDefaultNamespace(NS_MATHML)
                .addTranslation("m", NS_MATHML)
                .addTranslation("mws", "http://search.mathweb.org/ns")
                .addUnwantedAttribute("xml:id")
                .translateNamespaces(cmmlDoc);
        try {
            math.getAttributes().removeNamedItem("xmlns:m");
        } catch (final DOMException e) {
            //Ignore any errors thrown if element does not exist
        }
    }

    private void removeElementsByName(String name) {
        final NonWhitespaceNodeList nodes = new NonWhitespaceNodeList(cmmlDoc.getElementsByTagNameNS("*", name));
        for (final Node node : nodes) {
            // be sure not to remove content MathML
            if (!node.getAttributes().getNamedItem("encoding").getTextContent().equals("MathML-Content")) {
                node.getParentNode().removeChild(node);
            }
        }
    }

    private void removeAnnotations() {
        removeElementsByName("annotation");
        removeElementsByName("annotation-xml");
    }

    private void constructor(Document cmml, Boolean fixNamespace, Boolean preserveAnnotations) {
        cmmlDoc = cmml;
        if (fixNamespace) {
            fixNamespaces();
        }
        if (!preserveAnnotations) {
            removeAnnotations();
        }
        removeElementsByName("id");
    }

    @Override
    public final CMMLInfo clone() {
        return new CMMLInfo(this);
    }

    public final CMMLInfo toStrictCmml() throws TransformerException, ParserConfigurationException {
        cmmlDoc = XMLHelper.XslTransform(cmmlDoc, ROBERT_MINER_XSL);
        return this;
    }

    @Override
    public final String toString() {
        try {
            return XMLHelper.printDocument(cmmlDoc);
        } catch (final TransformerException e) {
            return "cmml not printable";
        }
    }

    @Override
    public final DocumentType getDoctype() {
        return cmmlDoc.getDoctype();
    }

    @Override
    public final EntityReference createEntityReference(String s) throws DOMException {
        return cmmlDoc.createEntityReference(s);
    }

    @Override
    public final void normalizeDocument() {
        cmmlDoc.normalizeDocument();
    }

    @Override
    public final Object getUserData(String s) {
        return cmmlDoc.getUserData(s);
    }

    @Override
    public final Node getNextSibling() {
        return cmmlDoc.getNextSibling();
    }

    @Override
    public final CDATASection createCDATASection(String s) throws DOMException {
        return cmmlDoc.createCDATASection(s);
    }

    @Override
    public final Node getPreviousSibling() {
        return cmmlDoc.getPreviousSibling();
    }

    @Override
    public final boolean isSameNode(Node node) {
        return cmmlDoc.isSameNode(node);
    }

    @Override
    public final Attr createAttributeNS(String s, String s1) throws DOMException {
        return cmmlDoc.createAttributeNS(s, s1);
    }

    @Override
    public final NodeList getChildNodes() {
        return cmmlDoc.getChildNodes();
    }

    @Override
    public final Node getFirstChild() {
        return cmmlDoc.getFirstChild();
    }

    @Override
    public final Object setUserData(String s, Object o, UserDataHandler userDataHandler) {
        return cmmlDoc.setUserData(s, o, userDataHandler);
    }

    @Override
    public final String getNamespaceURI() {
        return cmmlDoc.getNamespaceURI();
    }

    @Override
    public final Node renameNode(Node node, String s, String s1) throws DOMException {
        return cmmlDoc.renameNode(node, s, s1);
    }

    @Override
    public final Node insertBefore(Node node, Node node1) throws DOMException {
        return cmmlDoc.insertBefore(node, node1);
    }

    @Override
    public final String getXmlVersion() {
        return cmmlDoc.getXmlVersion();
    }

    @Override
    public final void setXmlVersion(String s) throws DOMException {
        cmmlDoc.setXmlVersion(s);
    }

    @Override
    public final String getDocumentURI() {
        return cmmlDoc.getDocumentURI();
    }

    @Override
    public final void setDocumentURI(String s) {
        cmmlDoc.setDocumentURI(s);
    }

    @Override
    public final String getInputEncoding() {
        return cmmlDoc.getInputEncoding();
    }

    @Override
    public final NodeList getElementsByTagNameNS(String s, String s1) {
        return cmmlDoc.getElementsByTagNameNS(s, s1);
    }

    @Override
    public final DocumentFragment createDocumentFragment() {
        return cmmlDoc.createDocumentFragment();
    }

    @Override
    public final String getPrefix() {
        return cmmlDoc.getPrefix();
    }

    @Override
    public final void setPrefix(String s) throws DOMException {
        cmmlDoc.setPrefix(s);
    }

    @Override
    public final String getTextContent() throws DOMException {
        return cmmlDoc.getTextContent();
    }

    @Override
    public final void setTextContent(String s) throws DOMException {
        cmmlDoc.setTextContent(s);
    }

    @Override
    public final void normalize() {
        cmmlDoc.normalize();
    }

    @Override
    public final Node removeChild(Node node) throws DOMException {
        return cmmlDoc.removeChild(node);
    }

    @Override
    public final boolean isSupported(String s, String s1) {
        return cmmlDoc.isSupported(s, s1);
    }

    @Override
    public final ProcessingInstruction createProcessingInstruction(String s, String s1) throws DOMException {
        return cmmlDoc.createProcessingInstruction(s, s1);
    }

    @Override
    public final short getNodeType() {
        return cmmlDoc.getNodeType();
    }

    @Override
    public final Document getOwnerDocument() {
        return cmmlDoc.getOwnerDocument();
    }

    @Override
    public final Comment createComment(String s) {
        return cmmlDoc.createComment(s);
    }

    @Override
    public final Attr createAttribute(String s) throws DOMException {
        return cmmlDoc.createAttribute(s);
    }

    @Override
    public final boolean getStrictErrorChecking() {
        return cmmlDoc.getStrictErrorChecking();
    }

    @Override
    public final void setStrictErrorChecking(boolean b) {
        cmmlDoc.setStrictErrorChecking(b);
    }

    @Override
    public final NamedNodeMap getAttributes() {
        return cmmlDoc.getAttributes();
    }

    @Override
    public final String getBaseURI() {
        return cmmlDoc.getBaseURI();
    }

    @Override
    public final Element getDocumentElement() {
        return cmmlDoc.getDocumentElement();
    }

    @Override
    public final DOMConfiguration getDomConfig() {
        return cmmlDoc.getDomConfig();
    }

    @Override
    public final DOMImplementation getImplementation() {
        return cmmlDoc.getImplementation();
    }

    @Override
    public final String getNodeValue() throws DOMException {
        return cmmlDoc.getNodeValue();
    }

    @Override
    public final void setNodeValue(String s) throws DOMException {
        cmmlDoc.setNodeValue(s);
    }

    @Override
    public final boolean hasAttributes() {
        return cmmlDoc.hasAttributes();
    }

    @Override
    public final Element createElementNS(String s, String s1) throws DOMException {
        return cmmlDoc.createElementNS(s, s1);
    }

    @Override
    public final Element createElement(String s) throws DOMException {
        return cmmlDoc.createElement(s);
    }

    @Override
    public final Node importNode(Node node, boolean b) throws DOMException {
        return cmmlDoc.importNode(node, b);
    }

    @Override
    public final Text createTextNode(String s) {
        return cmmlDoc.createTextNode(s);
    }

    @Override
    public final String lookupPrefix(String s) {
        return cmmlDoc.lookupPrefix(s);
    }

    @Override
    public final boolean isEqualNode(Node node) {
        return cmmlDoc.isEqualNode(node);
    }

    @Override
    public final NodeList getElementsByTagName(String s) {
        return cmmlDoc.getElementsByTagName(s);
    }

    @Override
    public final Node getLastChild() {
        return cmmlDoc.getLastChild();
    }

    @Override
    public final Node appendChild(Node node) throws DOMException {
        return cmmlDoc.appendChild(node);
    }

    @Override
    public final short compareDocumentPosition(Node node) throws DOMException {
        return cmmlDoc.compareDocumentPosition(node);
    }

    @Override
    public final Object getFeature(String s, String s1) {
        return cmmlDoc.getFeature(s, s1);
    }

    @Override
    public final Element getElementById(String s) {
        return cmmlDoc.getElementById(s);
    }

    @Override
    public final boolean isDefaultNamespace(String s) {
        return cmmlDoc.isDefaultNamespace(s);
    }

    @Override
    public final String lookupNamespaceURI(String s) {
        return cmmlDoc.lookupNamespaceURI(s);
    }

    @Override
    public final String getLocalName() {
        return cmmlDoc.getLocalName();
    }

    @Override
    public final String getXmlEncoding() {
        return cmmlDoc.getXmlEncoding();
    }

    @Override
    public final String getNodeName() {
        return cmmlDoc.getNodeName();
    }

    @Override
    public final Node getParentNode() {
        return cmmlDoc.getParentNode();
    }

    @Override
    public final Node cloneNode(boolean b) {
        return cmmlDoc.cloneNode(b);
    }

    @Override
    public final boolean getXmlStandalone() {
        return cmmlDoc.getXmlStandalone();
    }

    @Override
    public final void setXmlStandalone(boolean b) throws DOMException {
        cmmlDoc.setXmlStandalone(b);
    }

    @Override
    public final Node replaceChild(Node node, Node node1) throws DOMException {
        return cmmlDoc.replaceChild(node, node1);
    }

    @Override
    public final boolean hasChildNodes() {
        return cmmlDoc.hasChildNodes();
    }

    @Override
    public final Node adoptNode(Node node) throws DOMException {
        return cmmlDoc.adoptNode(node);
    }


}
