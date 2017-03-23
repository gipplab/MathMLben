package org.citeplag.translate.speech;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;

/**
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
     * @param latex LaTeX formula
     * @return Enrichted MathML String from mathoid
     */
    public String convertLatex(String latex) {
        // set necessary header / request per form
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("q", latex);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            String rep = restTemplate.postForObject(mathoidUrl, request, String.class);
            logger.info(rep);
            return rep;
        } catch (HttpClientErrorException e) {
            logger.error(e.getResponseBodyAsString());
            throw e;
        }
    }
}
