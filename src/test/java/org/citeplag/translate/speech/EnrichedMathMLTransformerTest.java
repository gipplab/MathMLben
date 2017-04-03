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
    public void manualTest() throws IOException, ParserConfigurationException, TransformerException {
        String xml = "<mrow xmlns=\"http://www.w3.org/1998/Math/MathML\" class=\"MJX-TeXAtom-ORD\">\n" +
                "         <mroot data-semantic-children=\"0,1\"\n" +
                "                data-semantic-id=\"2\"\n" +
                "                data-semantic-role=\"unknown\"\n" +
                "                data-semantic-type=\"root\"\n" +
                "                id=\"p2\">\n" +
                "            <mn data-semantic-font=\"normal\"\n" +
                "                data-semantic-id=\"1\"\n" +
                "                data-semantic-parent=\"2\"\n" +
                "                data-semantic-role=\"integer\"\n" +
                "                data-semantic-type=\"number\"\n" +
                "                id=\"p1\">8</mn>\n" +
                "            <mrow class=\"MJX-TeXAtom-ORD\">\n" +
                "               <mn data-semantic-font=\"normal\"\n" +
                "                   data-semantic-id=\"0\"\n" +
                "                   data-semantic-parent=\"2\"\n" +
                "                   data-semantic-role=\"integer\"\n" +
                "                   data-semantic-type=\"number\"\n" +
                "                   id=\"p0\">3</mn>\n" +
                "            </mrow>\n" +
                "         </mroot>\n" +
                "      </mrow>";

        EnrichedMathMLTransformer transformer = new EnrichedMathMLTransformer(xml);
        System.out.println(transformer.transform());
    }

    @Test
    public void testSimpleEnrichedMath1() throws Exception {
        // (a+b)+c=d
        test("xsl_test_simple_1");
    }

    @Test
    public void testComplexEnrichedMath1() throws Exception {
        // \sqrt{3}+\frac{a+1}{b-2}
        test("xsl_test_complex_1");
    }

    @Test
    public void testComplexEnrichedMath1_Full() throws Exception {
        // \sqrt{3}+\frac{a+1}{b-2}
        // prepare test strings
        String testString = IOUtils.toString(this.getClass().getResourceAsStream( "complexMath1" + "_test.txt"),"UTF-8");;
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("complexMath1" + "_expected.txt"),"UTF-8");

        // test it
        EnrichedMathMLTransformer transformator = new EnrichedMathMLTransformer(testString);
        String output = transformator.getFullMathML();

        System.out.println(output);
        Assert.assertThat(output, CoreMatchers.equalTo(expected));
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