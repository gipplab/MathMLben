package org.citeplag;

import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.citeplag.latexml.LateXMLConfig;
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
@RequestMapping("/math/config")
public class ConfigController {

    private static Logger logger = Logger.getLogger(ConfigController.class);

    @Autowired
    LateXMLConfig lateXMLConfig;

    @GetMapping()
    @ApiOperation(value = "Show the current default LaTeXML configuration")
    public LateXMLConfig getConfig(
            HttpServletRequest request) throws Exception {
        return lateXMLConfig;
    }


}
