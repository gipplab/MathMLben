package org.citeplag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration for the LaTeXML service.
 *
 * @author Vincent Stange
 */
@Component
@ConfigurationProperties(prefix = "latexml")
public class LateXMLConfig extends com.formulasearchengine.mathmlconverters.latexml.LateXMLConfig {
}
