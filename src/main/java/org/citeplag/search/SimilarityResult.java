package org.citeplag.search;

import org.citeplag.match.Similarity;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for the result of the similarity comparison.
 *
 * @author Vincent Stange
 */
public class SimilarityResult {

    private String statusCode = "";

    private String log = "";

    private List<Similarity> result = new ArrayList<>();

    public SimilarityResult() {
        // empty constructor
    }

    public SimilarityResult(String statusCode, String log, List<Similarity> result) {
        this.statusCode = statusCode;
        this.result = result;
        this.log = log;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public List<Similarity> getResult() {
        return result;
    }

    public void setResult(List<Similarity> result) {
        this.result = result;
    }
}
