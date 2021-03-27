package com.chornyi.spring.boot.thymeleaf.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
@Data
public class ApplicationProperties {

    private String vigilanteApi;

    private Swagger swagger = new Swagger();

    private Ldap ldap = new Ldap();
    
    @Data
    public class Swagger {
        private String basePackage;
        private String title;
        private String description;
        private String version;
        private String termsOfServiceUrl;
        private String contactName;
        private String contactUrl;
        private String contactEmail;
        private String license;
        private String licenseUrl;
    }

    @Data
    public class Ldap {
        private boolean enable;
        private String url;
        private String baseDn;
        private String username;
        private String password;
        private String userDnPattern;
    }

}
