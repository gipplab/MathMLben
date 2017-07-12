package org.citeplag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Simple container for the MAST renderer.
 * Currently only the URL is stored.
 *
 * @author Vincent Stange
 */
@Component
@ConfigurationProperties(prefix = "mast")
public class MathASTConfig {

    private String url = "";

    public MathASTConfig() {
        // empty constructor
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}