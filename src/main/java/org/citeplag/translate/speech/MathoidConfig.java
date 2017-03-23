package org.citeplag.translate.speech;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuration for the Mathoid service.
 *
 * @author Vincent Stange
 */
@Component
@ConfigurationProperties(prefix = "mathoid")
public class MathoidConfig {

    private String url = "";

    public MathoidConfig() {
        // empty constructor
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
