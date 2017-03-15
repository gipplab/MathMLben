package org.citeplag.latexml;

import org.citeplag.util.CommandExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Main class for conversion from a latex formula to
 * a MathML representation.
 *
 * @author Vincent Stange
 */
public class LaTeXMLConverter {

    private final LateXMLConfig lateXMLConfig;

    public LaTeXMLConverter(LateXMLConfig lateXMLConfig) {
        this.lateXMLConfig = lateXMLConfig;
    }

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
                configToUrlString(lateXMLConfig.getParams()) +
                "&tex=literal:"
                + latex;

        RestTemplate restTemplate = new RestTemplate();
        ServiceResponse serviceResponse = restTemplate.postForObject(lateXMLConfig.getUrl(), payload, ServiceResponse.class);
        return serviceResponse.getResult();
    }

    private String configToUrlString(Map<String, String> values) {
        StringBuilder sb = new StringBuilder();
        values.forEach((k, v) -> {
            // value splitting or create a array with a single or null string
            String[] list = v.contains(",") ? v.split(",") : new String[]{v};
            for (String value : list) {
                sb.append("&").append(k);
                if (!"".equals(value)) {
                    sb.append("=").append(v);
                }
            }
        });
        return sb.toString();
    }

}
