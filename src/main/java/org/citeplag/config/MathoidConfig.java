package org.citeplag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Replica of the MathoidConfig for auto-configuration via Spring Boot
 *
 * @author Vincent Stange
 */
@Component
@ConfigurationProperties(prefix = "mathoid")
public class MathoidConfig extends com.formulasearchengine.mathmlconverters.mathoid.MathoidConfig {
}