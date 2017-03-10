package org.citeplag;

import io.swagger.annotations.ApiOperation;
import org.citeplag.match.Similarity;
import org.citeplag.search.BruteSearch;
import org.citeplag.search.Generator;
import org.citeplag.search.MathNode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
    public String convertLatexml(@RequestBody String latex) throws Exception {
        return new LaTeXMLConverter().runLatexmlc(latex);
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

    //    @GetMapping()
    public String test2() {
        // http://gw125.iu.xsede.org:8888

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("http://gturnquist-quoters.cfapps.io/api/random", String.class);

        //return "<html><body><h1>Hello World</h1></body></html>";
    }

}
