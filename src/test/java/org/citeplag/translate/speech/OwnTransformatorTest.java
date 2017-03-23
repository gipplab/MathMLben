package org.citeplag.translate.speech;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vincent Stange
 */
public class OwnTransformatorTest {

    @Test
    public void testSimpleEnrichedMath2() throws Exception {
        // a*bc
        test("simpleMath2");
    }

    @Test
    public void testSimpleEnrichedMath1() throws Exception {
        // a*b
        test("simpleMath1");
    }

    @Test
    public void testComplexEnrichedMath1() throws Exception {
        // \sqrt{3}+\frac{a+1}{b-2}
        test("complexMath1");
    }

    private void test(String basicFilename) throws Exception {
        // prepare test strings
        String testString = IOUtils.toString(this.getClass().getResourceAsStream( basicFilename + "_test.txt"),"UTF-8");;
        String expected = IOUtils.toString(this.getClass().getResourceAsStream(basicFilename + "_expected.txt"),"UTF-8");

        // test it
        OwnTransformator transformator = new OwnTransformator(testString);
        String output = transformator.getFullMathML();

        //System.out.println(output);
        Assert.assertThat(output, CoreMatchers.equalTo(expected));
    }

}