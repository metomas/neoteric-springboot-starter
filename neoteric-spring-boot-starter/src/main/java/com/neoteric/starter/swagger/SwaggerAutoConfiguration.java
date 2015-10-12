package com.neoteric.starter.swagger;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Swagger.class)
@ConditionalOnProperty(value = "swagger.enabled", matchIfMissing = true)
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(SwaggerAutoConfiguration.class);

    @Autowired
    SwaggerProperties swaggerProperties;

    @Bean
    BeanConfig beanConfig() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setPrettyPrint(swaggerProperties.isPrettyPrint());
        beanConfig.setVersion(swaggerProperties.getVersion());
        beanConfig.setContact(swaggerProperties.getContact());
        beanConfig.setTitle(swaggerProperties.getTitle());
        beanConfig.setDescription(swaggerProperties.getDescription());
        beanConfig.setSchemes(swaggerProperties.getSchemes());
        beanConfig.setLicense(swaggerProperties.getLicense());
        beanConfig.setLicenseUrl(swaggerProperties.getLicenseUrl());
        beanConfig.setResourcePackage(swaggerProperties.getResourcePackage());
        beanConfig.setSchemes(swaggerProperties.getSchemes());
        beanConfig.setBasePath("/api");
        beanConfig.setScan(true);
        LOG.debug("Swagger enabled on {}", swaggerProperties.getResourcePackage());
        return beanConfig;
    }
}
