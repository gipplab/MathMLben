package org.citeplag.util;

import com.formulasearchengine.mathmlsim.similarity.result.Match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for the result of the similarity comparison.
 *
 * @author Vincent Stange
 */
public class SimilarityResult {

    private String statusCode = "";

    private String log = "";

    private List<Match> result = new ArrayList<>();

    private Map<String, Object> original = new HashMap<>();

    public SimilarityResult(String statusCode, String log, List<Match> result, Map<String, Object> original) {
        this.statusCode = statusCode;
        this.result = result;
        this.log = log;
        this.original = original;
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

    public List<Match> getResult() {
        return result;
    }

    public void setResult(List<Match> result) {
        this.result = result;
    }

    public Map<String, Object> getOriginal() {
        return original;
    }

    public void setOriginal(Map<String, Object> original) {
        this.original = original;
    }
}
