package org.citeplag;

import org.citeplag.util.CommandExecutor;

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
    String runLatexmlc(String latex) throws Exception {
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
