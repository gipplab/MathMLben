package org.citeplag.translate.speech;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * @author Vincent Stange
 */
public class EnrichedMathMLTransformerTest {

    @Test
    public void test1() throws IOException, ParserConfigurationException, TransformerException {
        String xml = "<mrow data-semantic-children=\"0,2\"\n" +
                "            data-semantic-content=\"1\"\n" +
                "            data-semantic-id=\"3\"\n" +
                "            data-semantic-role=\"addition\"\n" +
                "            data-semantic-type=\"infixop\"\n" +
                "            id=\"p3\">\n" +
                "         <mi data-semantic-font=\"italic\"\n" +
                "             data-semantic-id=\"0\"\n" +
                "             data-semantic-parent=\"3\"\n" +
                "             data-semantic-role=\"latinletter\"\n" +
                "             data-semantic-type=\"identifier\"\n" +
                "             id=\"p0\">a</mi>\n" +
                "         <mo data-semantic-id=\"1\"\n" +
                "             data-semantic-operator=\"infixop,+\"\n" +
                "             data-semantic-parent=\"3\"\n" +
                "             data-semantic-role=\"addition\"\n" +
                "             data-semantic-type=\"operator\"\n" +
                "             id=\"p1\">+</mo>\n" +
                "         <mi data-semantic-font=\"italic\"\n" +
                "             data-semantic-id=\"2\"\n" +
                "             data-semantic-parent=\"3\"\n" +
                "             data-semantic-role=\"latinletter\"\n" +
                "             data-semantic-type=\"identifier\"\n" +
                "             id=\"p2\">b</mi>\n" +
                "      </mrow>";

        EnrichedMathMLTransformer transformer = new EnrichedMathMLTransformer(xml);
        System.out.println(transformer.transform());
    }

    @Test
    public void testSimpleEnrichedMath1() throws Exception {
        // a*b
        test("xsl_test_simple_1");
    }

    private void test(String basicFilename) throws Exception {
        // prepare test strings
        String testString = IOUtils.toString(this.getClass().getResourceAsStream( basicFilename + "_test.txt"),"UTF-8");;
        String expected = IOUtils.toString(this.getClass().getResourceAsStream(basicFilename + "_expected.txt"),"UTF-8");

        // test it
        EnrichedMathMLTransformer transformator = new EnrichedMathMLTransformer(testString);
        String output = transformator.transform();

        System.out.println(output);
        Assert.assertThat(output, CoreMatchers.equalTo(expected));
    }

}