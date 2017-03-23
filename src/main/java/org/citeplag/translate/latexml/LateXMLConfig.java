package org.citeplag.translate.latexml;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuration for the LaTeXML service.
 *
 * @author Vincent Stange
 */
@Component
@ConfigurationProperties(prefix = "latexml")
public class LateXMLConfig {

    private String url = "";

    private Map<String, String> params = new LinkedHashMap<>();

    public LateXMLConfig() {
        // empty constructor
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
