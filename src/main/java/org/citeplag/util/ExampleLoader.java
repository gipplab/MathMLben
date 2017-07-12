package org.citeplag.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * Loads an example from the package resources. <br/>
 *
 * @author Vincent Stange
 */
public class ExampleLoader {

    /**
     * Read a static resource file as an example for the demo.
     *
     * @param exampleName name of the static resource file in the path /static/example
     * @return {@see Example}
     * @throws IOException usually file not found, resource should be checked
     */
    public Example load(String exampleName) throws IOException {
        Example example = new Example();
        example.setName(exampleName);
        example.setLatex1(getResourceFileAsString(exampleName, "latex_1.txt"));
        example.setLatex2(getResourceFileAsString(exampleName, "latex_2.txt"));
        example.setMathml1(getResourceFileAsString(exampleName, "mathml_1.xml"));
        example.setMathml2(getResourceFileAsString(exampleName, "mathml_2.xml"));
        example.setSimilarity(getResourceFileAsString(exampleName, "similarity.json"));
        return example;
    }

    private String getResourceFileAsString(String exampleName, String filename) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream("static/example/" + exampleName + "/" + filename), "UTF-8");
    }
}