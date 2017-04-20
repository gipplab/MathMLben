package org.citeplag.mml;

import org.citeplag.util.XMLUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.util.ArrayList;

/**
 * @author Vincent Stange
 */
public class CMMLHelper {

    public Node getCmml(String mathml) throws Exception {
        // Convert to normal document
        Document mathmlDocument = XMLHelper.String2Doc(mathml, false);

        // get the apply node of the ContentMathML root
        XPath xpath = XMLHelper.namespaceAwareXpath("m", CMMLInfo.NS_MATHML);
        return (Node) xpath.compile("*//m:annotation-xml[@encoding='MathML-Content']/m:apply").evaluate(mathmlDocument, XPathConstants.NODE);
    }

    public Node getStrictCmml(String mathml) throws Exception {
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
     * Converts a CMML node to strict CMML via its
     * 'cd' attribute field.
     *
     * @param cmmlDoc CMML document
     * @param node    current node to be converted
     */
    void abstractNodeCD(Document cmmlDoc, Node node) {
        ArrayList<Element> childElements = XMLUtils.getChildElements(node);
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
}
