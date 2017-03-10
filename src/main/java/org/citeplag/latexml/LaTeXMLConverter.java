package org.citeplag.latexml;

import org.citeplag.util.CommandExecutor;
import org.springframework.web.client.RestTemplate;

/**
 * Main class for conversion from a latex formula to
 * a MathML representation.
 *
 * @author Vincent Stange
 */
public class LaTeXMLConverter {

    /**
     * This methods needs a LaTeXML installation. It converts a latex formula
     * string into mathml and includes pmml, cmml and tex semantics.
     * Conversion is executed by "latexmlc".
     *
     * @param latex Latex Formula
     * @return MathML representation as String
     * @throws Exception Execution of latexmlc failed.
     */
    public String runLatexmlc(String latex) throws Exception {
        CommandExecutor latexmlmath = new CommandExecutor("latexmlc",
                "--includestyles",
                "--format=xhtml",
                "--whatsin=math",
                "--whatsout=math",
                "--pmml",
                "--cmml",
                "--nodefaultresources",
                "--linelength=90",
                "--quiet",
                "--preload", "LaTeX.pool",
                "--preload", "article.cls",
                "--preload", "amsmath.sty",
                "--preload", "amsthm.sty",
                "--preload", "amstext.sty",
                "--preload", "amssymb.sty",
                "--preload", "eucal.sty",
                "--preload", "[dvipsnames]xcolor.sty",
                "--preload", "url.sty",
                "--preload", "hyperref.sty",
                "--preload", "[ids]latexml.sty",
                "--preload", "texvc",
                "literal:" + latex);
        return latexmlmath.exec(2000L);
    }

    public String convertLatexmlService(String latex) {
        String payload = "format=xhtml" +
                "&whatsin=math" +
                "&whatsout=math" +
                "&pmml" +
                "&cmml" +
                "&nodefaultresources" +
                "&preload=LaTeX.pool" +
                "&preload=article.cls" +
                "&preload=amsmath.sty" +
                "&preload=amsthm.sty" +
                "&preload=amstext.sty" +
                "&preload=amssymb.sty" +
                "&preload=eucal.sty" +
                "&preload=%5Bdvipsnames%5Dxcolor.sty" +
                "&preload=url.sty" +
                "&preload=hyperref.sty" +
                "&preload=%5Bids%5Dlatexml.sty" +
                "&preload=texvc" +
                "&tex=literal:"
                + latex;

        RestTemplate restTemplate = new RestTemplate();
        ServiceResponse serviceResponse = restTemplate.postForObject("http://gw125.iu.xsede.org:8888", payload, ServiceResponse.class);
        return serviceResponse.getResult();
    }

    /**
     * This methods needs a LaTeXML installation. It converts a latex formula
     * string into mathml and only returns the pmml.
     * Conversion is executed by "latexmlmath".
     *
     * @param latex Latex Formula
     * @return MathML representation as String
     * @throws Exception Execution of latexmlmath failed.
     */
    String runLatexmlmath(String latex) throws Exception {
        CommandExecutor latexmlmath = new CommandExecutor("latexmlmath",
                "--includestyles",
                "--preload", "LaTeX.pool",
                "--preload", "article.cls",
                "--preload", "amsmath.sty",
                "--preload", "amsthm.sty",
                "--preload", "amstext.sty",
                "--preload", "amssymb.sty",
                "--preload", "eucal.sty",
                "--preload", "[dvipsnames]xcolor.sty",
                "--preload", "url.sty",
                "--preload", "hyperref.sty",
                "--preload", "[ids]latexml.sty",
                "--preload", "texvc",
                "--pmml=-",
                latex);
        return latexmlmath.exec(2000L);
    }

}
