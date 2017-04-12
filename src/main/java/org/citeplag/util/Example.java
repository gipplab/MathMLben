package org.citeplag.util;

/**
 * Just a POJO for an example for the demo.
 *
 * @author Vincent Stange
 */
public class Example {

    private String name = "";

    private String latex1 = "";

    private String latex2 = "";

    private String mathml1 = "";

    private String mathml2 = "";

    private String similarity = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatex1() {
        return latex1;
    }

    public void setLatex1(String latex1) {
        this.latex1 = latex1;
    }

    public String getLatex2() {
        return latex2;
    }

    public void setLatex2(String latex2) {
        this.latex2 = latex2;
    }

    public String getMathml1() {
        return mathml1;
    }

    public void setMathml1(String mathml1) {
        this.mathml1 = mathml1;
    }

    public String getMathml2() {
        return mathml2;
    }

    public void setMathml2(String mathml2) {
        this.mathml2 = mathml2;
    }

    public String getSimilarity() {
        return similarity;
    }

    public void setSimilarity(String similarity) {
        this.similarity = similarity;
    }
}
