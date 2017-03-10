package org.citeplag;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * @author Vincent Stange
 */
@ComponentScan
@Configuration
@EnableSwagger2
@EnableAutoConfiguration
public class ApplicationStart {

    public static void main(String[] args) throws Exception {
        // start the full spring environment
        SpringApplication.run(ApplicationStart.class, args);
    }

    @Autowired
    private TypeResolver typeResolver;

    /**
     * Pretty print for every json output
     * @return override the jackson builder
     */
    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(true);
        return builder;
    }

    /**
     * Springfox /Swagger configuration
     *
     * @return Docket Object from Springfox /Swagger
     */
    @Bean
    public Docket petApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                // general informations
                .apiInfo(getApiInfo())
                .pathMapping("/")
                // exposed endpoints
                .select()
                .paths(getDocumentedApiPaths())
                .build()
                // Convenience rule builder that substitutes a generic type with one type parameter
                // with the type parameter. In this case ResponseEntity<T>
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(
                        newRule(typeResolver.resolve(DeferredResult.class,
                                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                                typeResolver.resolve(WildcardType.class)))
                // default response code should not be used
                .useDefaultResponseMessages(false)
                ;
    }

    /**
     * Every REST Service we want to document with Swagger
     *
     * @return Predicate conditions
     */
    private Predicate<String> getDocumentedApiPaths() {
        return or(
                regex("/math.*")
        );
    }

    /**
     * General information about our project's API.
     * (Informations for the Swagger UI)
     *
     * @return see ApiInfo
     */
    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("MathML Pipeline")
                .description("SciPlore Project")
                .termsOfServiceUrl("http://springfox.io")
                .contact(new Contact("Vincent Stange", null, null))
                .license("Apache License Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .version("1.0")
                .build();
    }

}