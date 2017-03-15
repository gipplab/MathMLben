package org.citeplag;

import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.citeplag.latexml.LaTeXMLConverter;
import org.citeplag.latexml.LateXMLConfig;
import org.citeplag.match.Similarity;
import org.citeplag.search.BruteSearch;
import org.citeplag.search.Generator;
import org.citeplag.search.MathNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * REST Controller for our little MathML Pipeline.
 *
 * @author Vincent Stange
 */
@RestController
@RequestMapping("/math")
public class MathController {

    private static Logger logger = Logger.getLogger(MathController.class);

    @Autowired
    LateXMLConfig lateXMLConfig;

    @PostMapping()
    @ApiOperation(value = "Converts a String from LaTeXMLConverter to MathML semantics.")
    public String convertLatexmlInstallation(
            @RequestParam(required = false, defaultValue = "false") Boolean service,
            @RequestBody String latex) throws Exception {
        LaTeXMLConverter laTeXMLConverter = new LaTeXMLConverter(lateXMLConfig);
        try {
            if (service && !StringUtils.isEmpty(lateXMLConfig.getUrl()))
                return laTeXMLConverter.convertLatexmlService(latex);
        } catch (Exception e) {
            logger.error("latexml online service error", e);
            // fallback, try to use the local installation
        }
        return laTeXMLConverter.runLatexmlc(latex);
    }

    @PostMapping(path = "similarity")
    @ApiOperation(value = "Get a list of similarities between two MathML semantics.")
    public List<Similarity> getSimilarities(
            @RequestParam(value = "mathml1") String mathmlA,
            @RequestParam(value = "mathml2") String mathmlB) {

        Generator generator = new Generator();
        try {
            // Convert the MathML into our own internal representation of a Math Tree
            MathNode mathNodeA = generator.generateMathNode(mathmlA);
            MathNode mathNodeB = generator.generateMathNode(mathmlB);

            BruteSearch bruteSearch = new BruteSearch();
            return bruteSearch.getSimilarities(mathNodeA, mathNodeB);
        } catch (Exception e) {
            logger.error("similarity error", e);
            return Collections.emptyList();
        }
    }

}
