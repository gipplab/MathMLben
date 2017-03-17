package org.citeplag;

import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.citeplag.latexml.LaTeXMLConverter;
import org.citeplag.latexml.LateXMLConfig;
import org.citeplag.match.Similarity;
import org.citeplag.search.BruteTreeSearch;
import org.citeplag.search.MathNode;
import org.citeplag.search.MathNodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
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
            @RequestParam(required = false, defaultValue = "true") Boolean service,
            @RequestBody String latex,
            HttpServletRequest request) throws Exception {
        LaTeXMLConverter laTeXMLConverter = new LaTeXMLConverter(lateXMLConfig);
        try {
            if (service && !StringUtils.isEmpty(lateXMLConfig.getUrl())) {
                logger.info("service latex conversion from: " + request.getRemoteAddr());
                return laTeXMLConverter.convertLatexmlService(latex);
            }
        } catch (Exception e) {
            logger.error("latexml online service error", e);
            // fallback, try to use the local installation
        }
        logger.info("local latex conversion from: " + request.getRemoteAddr());
        return laTeXMLConverter.runLatexmlc(latex);
    }

    @PostMapping(path = "similarity")
    @ApiOperation(value = "Get a list of similarities between two MathML semantics.")
    public List<Similarity> getSimilarities(
            @RequestParam(value = "mathml1") String mathmlA,
            @RequestParam(value = "mathml2") String mathmlB,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "onlyOperations", defaultValue = "false", required = false) Boolean onlyOperations,
            HttpServletRequest request) {

        logger.info("similarity comparison from: " + request.getRemoteAddr());
        MathNodeGenerator generator = new MathNodeGenerator();
        try {
            Node cmmlA, cmmlB;
            if (type.equals("similar")) {
                cmmlA = generator.generateAbstractCDNode(mathmlA);
                cmmlB = generator.generateAbstractCDNode(mathmlB);
                onlyOperations = true;
            } else {
                cmmlA = generator.getCmmlRoot(mathmlA);
                cmmlB = generator.getCmmlRoot(mathmlB);
            }

            // Convert the MathML into our own internal representation of a Math Tree
            MathNode mathNodeA = generator.generateMathNode(cmmlA);
            MathNode mathNodeB = generator.generateMathNode(cmmlB);

            BruteTreeSearch bruteSearch = new BruteTreeSearch(type);
            return bruteSearch.getSimilarities(mathNodeA, mathNodeB, onlyOperations);
        } catch (Exception e) {
            logger.error("similarity error", e);
            return Collections.emptyList();
        }
    }

}
