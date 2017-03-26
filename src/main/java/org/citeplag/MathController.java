package org.citeplag;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.citeplag.match.Similarity;
import org.citeplag.node.MathNode;
import org.citeplag.node.MathNodeGenerator;
import org.citeplag.search.BruteTreeSearch;
import org.citeplag.translate.latexml.LaTeXMLConverter;
import org.citeplag.translate.latexml.LateXMLConfig;
import org.citeplag.translate.speech.MathoidConfig;
import org.citeplag.translate.speech.MathoidConverter;
import org.citeplag.translate.speech.OwnTransformator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
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

    @Autowired
    MathoidConfig mathoidConfig;

    @PostMapping()
    @ApiOperation(value = "Converts a Latex String via LaTeXML to MathML semantics.")
    public String convertLatexml(
            @RequestParam(required = false) String config,
            @RequestParam() String latex,
            HttpServletRequest request) throws Exception {

        // if request configuration is given, use it.
        LateXMLConfig usedConfig = (config != null ? new ObjectMapper().readValue(config, LateXMLConfig.class) : lateXMLConfig);

        LaTeXMLConverter laTeXMLConverter = new LaTeXMLConverter(usedConfig);
        // no url = local installation / url = online service
        if (StringUtils.isEmpty(usedConfig.getUrl())) {
            logger.info("local latex conversion from: " + request.getRemoteAddr());
            return laTeXMLConverter.runLatexmlc(latex);
        } else {
            logger.info("service latex conversion from: " + request.getRemoteAddr());
            return laTeXMLConverter.convertLatexmlService(latex);
        }
    }

    @PostMapping("/mathoid")
    @ApiOperation(value = "Converts a Latex String via Mathoid to MathML semantics.")
    public String convertMathoid(
            @RequestParam(required = false) String mathoidUrl,
            @RequestParam() String latex,
            HttpServletRequest request) throws Exception {

        // If local configuration is given, use it.
        mathoidUrl = (mathoidUrl != null ? mathoidUrl : mathoidConfig.getUrl());

        MathoidConverter converter = new MathoidConverter(mathoidUrl);
        try {
            String enrichedMathml = converter.convertLatex(latex);

            OwnTransformator transformator = new OwnTransformator(enrichedMathml);
            logger.info("latex conversion via mathoid from: " + request.getRemoteAddr());
            return transformator.getFullMathML();
        } catch (ResourceAccessException e) {
            return "mathoid not available under: " + mathoidUrl;
        } catch (Exception e) {
            logger.error("mathoid service error", e);
            return e.getMessage();
        }
    }

    @PostMapping(path = "similarity")
    @ApiOperation(value = "Get a list of similarities between two MathML semantics.")
    public List<Similarity> getSimilarities(
            @RequestParam(value = "mathml1") String mathmlA,
            @RequestParam(value = "mathml2") String mathmlB,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "onlyOperations", defaultValue = "false", required = false) Boolean onlyOperations,
            HttpServletRequest request) {

        MathNodeGenerator generator = new MathNodeGenerator();
        try {
            Node cmmlA, cmmlB;
            if (type.equals("similar")) {
                logger.info("similarity comparison from: " + request.getRemoteAddr());
                cmmlA = generator.generateAbstractCDNode(mathmlA);
                cmmlB = generator.generateAbstractCDNode(mathmlB);
                onlyOperations = true;
            } else {
                logger.info("identical comparison from: " + request.getRemoteAddr());
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
