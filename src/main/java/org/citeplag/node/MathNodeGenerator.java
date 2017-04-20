package org.citeplag.node;

import org.citeplag.util.XMLUtils;
import org.w3c.dom.Node;

/**
 * This class converts MathML (Content MathML) into our own  math expression tree representation
 * of a mathematical formula. (CMML document > MathNode)
 *
 * @author Vincent Stange
 */
public class MathNodeGenerator {

    /**
     * Create a math expression tree (MET) starting from the root element of a
     * Content MathML document.
     *
     * @param cmmlRoot root element of a CMML document.
     * @return first MathNode representing the root of the MET
     */
    public MathNode generateMathNode(Node cmmlRoot) {
        return createMathNode(cmmlRoot);
    }

    /**
     * Recursive method to create a math expression tree (MET). Every child
     * and all attributes are considered in the conversion.
     *
     * @param node current xml node in cmml, typically the root element
     * @return converted MathNode we use in this application
     */
    MathNode createMathNode(Node node) {
        MathNode mathNode = new MathNode();
        mathNode.setName(node.getNodeName());
        mathNode.setAttributes(node.getAttributes());
        mathNode.setValue(node.getFirstChild() != null ? node.getFirstChild().getTextContent().trim() : node.getTextContent().trim());
        // iterate over all child elements
        XMLUtils.getChildElements(node).forEach(c -> mathNode.addChild(createMathNode(c)));
        return mathNode;
    }

}
