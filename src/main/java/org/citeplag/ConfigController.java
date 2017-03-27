package org.citeplag;

import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.citeplag.translate.latexml.LateXMLConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * REST Controller for our little MathML Pipeline.
 *
 * @author Vincent Stange
 */
@RestController
@RequestMapping("config")
public class ConfigController {

    private static Logger logger = Logger.getLogger(ConfigController.class);

    @Autowired
    MathASTConfig mathASTConfig;

    @Autowired
    LateXMLConfig lateXMLConfig;

    @GetMapping("latexml")
    @ApiOperation(value = "Show the current default LaTeXML configuration")
    public LateXMLConfig getLatexConfig(
            HttpServletRequest request) throws Exception {
        return lateXMLConfig;
    }

    @GetMapping("mast")
    @ApiOperation(value = "Get the Math AST ")
    public String getMathUrl() throws Exception {
        return mathASTConfig.getUrl();
    }

}
