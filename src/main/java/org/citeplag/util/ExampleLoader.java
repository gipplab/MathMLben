package org.citeplag.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * Loads example from the prepackages resources.
 *
 * @author Vincent Stange
 */
public class ExampleLoader {

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
