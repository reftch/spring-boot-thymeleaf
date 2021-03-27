package com.chornyi.spring.boot.thymeleaf.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Autowired
    private ApplicationProperties properties;

    @Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(properties.getSwagger().getBasePackage()))
                .paths(PathSelectors.any()).build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(properties.getSwagger().getTitle())
                .description(properties.getSwagger().getDescription())
                .contact(new Contact(
                        properties.getSwagger().getContactName(),
                        properties.getSwagger().getContactUrl(),
                        properties.getSwagger().getContactEmail()))
                .version(properties.getSwagger().getVersion())
                .termsOfServiceUrl(properties.getSwagger().getTermsOfServiceUrl())
                .license(properties.getSwagger().getLicense())
                .licenseUrl(properties.getSwagger().getLicenseUrl())
                .build();
    }

}
