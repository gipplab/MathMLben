package org.citeplag.translate.speech;

import org.apache.log4j.Logger;
import org.citeplag.mml.XMLHelper;
import org.citeplag.util.XMLUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * TODO
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
        return XMLUtils.nodeToString(document.getFirstChild());
    }

}
