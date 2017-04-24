package org.citeplag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formulasearchengine.mathmlsim.similarity.MathPlag;
import com.formulasearchengine.mathmlsim.similarity.SubTreeComparison;
import com.formulasearchengine.mathmlsim.similarity.node.MathNode;
import com.formulasearchengine.mathmlsim.similarity.node.MathNodeGenerator;
import com.formulasearchengine.mathmlsim.similarity.result.Match;
import com.formulasearchengine.mathmlsim.similarity.util.CMMLHelper;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.citeplag.search.SimilarityResult;
import org.citeplag.translate.latexml.LaTeXMLConverter;
import org.citeplag.translate.latexml.LateXMLConfig;
import org.citeplag.translate.latexml.ServiceResponse;
import org.citeplag.translate.speech.EnrichedMathMLTransformer;
import org.citeplag.translate.speech.MathoidConfig;
import org.citeplag.translate.speech.MathoidConverter;
import org.citeplag.util.Example;
import org.citeplag.util.ExampleLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for our little MathML Pipeline.
 * Here we have in total:
 * <p>
 * 1. two POST methods for our latex to mathml conversion via latexml and mathoid
 * 2. one POST method for the similarity comparison
 * 3. one GET method to load a predefined example
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
    public ServiceResponse convertLatexml(
            @RequestParam(required = false) String config,
            @RequestParam() String latex,
            HttpServletRequest request) throws Exception {

        // if request configuration is given, use it.
        LateXMLConfig usedConfig = (config != null ? new ObjectMapper().readValue(config, LateXMLConfig.class) : lateXMLConfig);

        LaTeXMLConverter laTeXMLConverter = new LaTeXMLConverter(usedConfig);

        // no url = use the local installation of latexml, otherwise use: url = online service
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

            EnrichedMathMLTransformer transformer = new EnrichedMathMLTransformer(enrichedMathml);
            logger.info("latex conversion via mathoid from: " + request.getRemoteAddr());
            return transformer.getFullMathML();
        } catch (ResourceAccessException e) {
            return "mathoid not available under: " + mathoidUrl;
        } catch (Exception e) {
            logger.error("mathoid service error", e);
            return e.getMessage();
        }
    }

    @PostMapping(path = "similarity")
    @ApiOperation(value = "Get a list of similarities between two MathML semantics.")
    public SimilarityResult getSimilarities(
            @RequestParam(value = "mathml1") String mathmlA,
            @RequestParam(value = "mathml2") String mathmlB,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "onlyOperations", defaultValue = "true", required = false) Boolean onlyOperations,
            HttpServletRequest request) {

        try {
            Node cmmlA, cmmlB;
            if (type.equals("similar")) {
                logger.info("similarity comparison from: " + request.getRemoteAddr());
                // for similarity comparison we want the Content Dictionary or also called: strict CMML
                cmmlA = CMMLHelper.getStrictCmml(mathmlA);
                cmmlB = CMMLHelper.getStrictCmml(mathmlB);
            } else {
                logger.info("identical comparison from: " + request.getRemoteAddr());
                cmmlA = CMMLHelper.getCmml(mathmlA);
                cmmlB = CMMLHelper.getCmml(mathmlB);
            }

            // Convert the MathML into our own internal representation of a Math Tree
            MathNode mathNodeA = MathNodeGenerator.generateMathNode(cmmlA);
            MathNode mathNodeB = MathNodeGenerator.generateMathNode(cmmlB);
            if (type.equals("similar")) {
                // strict cmml should also be converted to the abstract variant
                mathNodeA = MathNodeGenerator.toAbstract(mathNodeA);
                mathNodeB = MathNodeGenerator.toAbstract(mathNodeB);
            }

            // start the similarity comparison
            List<Match> similarities = new SubTreeComparison(type).getSimilarities(mathNodeA, mathNodeB, onlyOperations);

            // get the old / original result and add it as an extra map
            Map<String, Object> originals = MathPlag.compareOriginalFactors(mathmlA, mathmlB);

            return new SimilarityResult("Okay", "", similarities, originals);
        } catch (Exception e) {
            logger.error("similarity error", e);
            return new SimilarityResult("Error", e.getMessage(), Collections.emptyList(), Maps.newTreeMap());
        }
    }

    @GetMapping(path = "example")
    @ApiOperation(value = "Get a full example for the demo.")
    public Example getExample() throws IOException {
        // this could easily be extended for more examples
        return new ExampleLoader().load("example_1.txt");
    }

}
