package org.citeplag;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
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
