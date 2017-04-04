package org.citeplag.translate.speech;

import org.apache.log4j.Logger;
import org.citeplag.mml.CMMLInfo;
import org.citeplag.mml.XMLHelper;
import org.citeplag.util.XMLUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.IOException;

/**
 * Transformer from Enriched Math to CMML.
 *
 * @author Vincent Stange
 */
public class EnrichedMathMLTransformer {

    private static Logger logger = Logger.getLogger(EnrichedMathMLTransformer.class);

    public static final String XSL = "org/citeplag/translate/speech/EnrichedMathML2Cmml.xsl";

    private final Document readDocument;

    public EnrichedMathMLTransformer(String readXml) throws IOException, ParserConfigurationException {
        readDocument = XMLHelper.String2Doc(readXml, true);
    }

    public String transform() throws TransformerException, ParserConfigurationException {
        Document document = XMLHelper.XslTransform(readDocument, XSL);
        logger.debug(document.getFirstChild());
        return XMLUtils.nodeToString(document.getFirstChild());
    }

    public String getFullMathML() throws Exception {
        XPath xpath = XMLHelper.namespaceAwareXpath("m", CMMLInfo.NS_MATHML);
        Element semanticRoot = (Element) xpath.compile("*//m:semantics").evaluate(readDocument, XPathConstants.NODE);
        // secure the id field
        convertIdField(semanticRoot);
        // get the first element
        Node firstElement = semanticRoot.getFirstChild();

        try {
            // create the cmml apply node
            Document newDocument = XMLHelper.XslTransform(firstElement, XSL);

            // adopt the new cmml structure into the original enriched mathml
            Node copy = readDocument.adoptNode(newDocument.getFirstChild().cloneNode(true));

            // create empty start element for our new cmml annotation
            Element cmmlRoot = readDocument.createElement("annotation-xml");
            cmmlRoot.setAttribute("encoding", "MathML-Content");
            cmmlRoot.appendChild(copy);

            // add to old document
            semanticRoot.appendChild(cmmlRoot);

            // currently fix the whole ns to MathML standard, may be changed later on
            XMLHelper.useFixNamespace(readDocument, CMMLInfo.NS_MATHML);
        } catch (Exception e) {
            String oldEnrichedXml = XMLUtils.nodeToString(readDocument.getFirstChild(), true);
            return oldEnrichedXml + "\n" + e.getMessage();
        }

        return XMLUtils.nodeToString(readDocument.getFirstChild(), true);
    }

    /**
     * Copy the "data-semantic-id" attribute to "id", if it does not exist
     *
     * @param readNode element to change
     */
    private void convertIdField(Element readNode) {
        String newId = readNode.getAttribute("data-semantic-id");
        if (!StringUtils.isEmpty(newId)) {
            readNode.setAttribute("id", "p" + newId);
        }
        for (Element child : XMLUtils.getChildElements(readNode)) {
            convertIdField(child);
        }
    }

}
