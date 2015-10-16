package com.neoteric.starter.jersey;

import com.neoteric.starter.Constants;
import com.neoteric.starter.swagger.SwaggerProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class AbstractJerseyConfig extends ResourceConfig {

    @Autowired
    protected SwaggerProperties swaggerProperties;

    /**
     * Register application specific Jersey resources.
     */
    protected abstract void configure();

    @PostConstruct // In constructor we can't inject properties
    public void register() {
        register(ObjectMapperProvider.class);
        if (swaggerProperties.isEnabled()) {
            this.packages(Constants.SWAGGER_PACKAGE);
        }
        configure();
    }
}