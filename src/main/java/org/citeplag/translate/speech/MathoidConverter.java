package org.citeplag.translate.speech;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Alternative approach for conversion from a latex formula to
 * a MathML representation via Mathoid.
 *
 * @author Vincent Stange
 */
public class MathoidConverter {

    private static Logger logger = Logger.getLogger(MathoidConverter.class);

    private final String mathoidUrl;

    public MathoidConverter(String mathoidUrl) {
        this.mathoidUrl = mathoidUrl;
    }

    /**
     * Request against mathoid to receive an enriched MathML.
     *
     * @param latex LaTeX formula to be converted
     * @return Enrichted MathML String from mathoid
     */
    public String convertLatex(String latex) {
        // set necessary header: request per form
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // pack the latex string as the parameter q (q for query ;) )
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("q", latex);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        try {
            String rep = new RestTemplate().postForObject(mathoidUrl, request, String.class);
            logger.info(rep);
            return rep;
        } catch (HttpClientErrorException e) {
            logger.error(e.getResponseBodyAsString());
            throw e;
        }
    }
}
