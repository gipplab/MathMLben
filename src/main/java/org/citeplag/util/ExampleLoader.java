package org.citeplag.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * Loads an example from the package resources. <br/>
 * An example need to be in the following format:
 *
 * <code>
 *     name
 *     @@@
 *     latexml 1
 *     @@@
 *     latexml 2
 *     @@@
 *     mathml of latexml 1
 *     @@@
 *     mathml of latexml 2
 *     @@@
 *     similarity json
 * </code>
 *
 * @author Vincent Stange
 */
public class ExampleLoader {

    /**
     * Read a static resource file as an example for the demo.
     *
     * @param exampleName name of the static resource file in the path /static/example
     * @return {@see Example}
     * @throws IOException usually file not found
     */
    public Example load(String exampleName) throws IOException {
        String exampleText = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("static/example/" + exampleName), "UTF-8");
        String[] split = exampleText.split("@@@");
        Example example = new Example();
        example.setName(split[0].trim());
        example.setLatex1(split[1].trim());
        example.setLatex2(split[2].trim());
        example.setMathml1(split[3].trim());
        example.setMathml2(split[4].trim());
        example.setSimilarity(split[5].trim());
        return example;
    }

}
