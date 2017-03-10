package org.citeplag;

import io.swagger.annotations.ApiOperation;
import org.citeplag.latexml.LaTeXMLConverter;
import org.citeplag.match.Similarity;
import org.citeplag.search.BruteSearch;
import org.citeplag.search.Generator;
import org.citeplag.search.MathNode;
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

    @PostMapping()
    @ApiOperation(value = "Converts a String from LaTeXMLConverter to MathML with pmml, cmml and tex semantics.")
    public String convertLatexmlInstallation(
            @RequestParam(required = false, defaultValue = "false") Boolean service,
            @RequestBody String latex) throws Exception {
        LaTeXMLConverter laTeXMLConverter = new LaTeXMLConverter();
        return service ? laTeXMLConverter.convertLatexmlService(latex) : laTeXMLConverter.runLatexmlc(latex);
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
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
