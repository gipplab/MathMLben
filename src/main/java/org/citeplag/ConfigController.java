package org.citeplag;

import io.swagger.annotations.ApiOperation;
import org.citeplag.config.LateXMLConfig;
import org.citeplag.config.MathASTConfig;
import org.citeplag.config.MathoidConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * REST Controller to load the current configuration.s
 *
 * @author Vincent Stange
 */
@RestController
@RequestMapping("config")
public class ConfigController {

    @Autowired
    private MathASTConfig mathASTConfig;

    @Autowired
    private LateXMLConfig lateXMLConfig;

    @Autowired
    private MathoidConfig mathoidConfig;

    @GetMapping("mathoid")
    @ApiOperation(value = "Show the current default LaTeXML configuration")
    public MathoidConfig getMathoidConfig(HttpServletRequest request) throws Exception {
        return mathoidConfig;
    }

    @GetMapping("latexml")
    @ApiOperation(value = "Show the current default LaTeXML configuration")
    public LateXMLConfig getLaTeXMLConfig(HttpServletRequest request) throws Exception {
        return lateXMLConfig;
    }

    @GetMapping("mast")
    @ApiOperation(value = "Get the Math AST ")
    public String getMathUrl() throws Exception {
        return mathASTConfig.getUrl();
    }
}