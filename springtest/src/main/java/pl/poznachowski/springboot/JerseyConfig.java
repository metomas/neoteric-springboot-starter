package pl.poznachowski.springboot;

import com.neoteric.starter.jersey.AbstractJerseyConfig;
import org.springframework.stereotype.Component;
import pl.poznachowski.springboot.controller.CustomErrorController;
import pl.poznachowski.springboot.exception.CustomErrorAttributes;
import pl.poznachowski.springboot.exception.mapper.InvalidIdentifierExceptionMapper;
import pl.poznachowski.springboot.rabbit.SendRabbitEndpoint;

@Component
public class JerseyConfig extends AbstractJerseyConfig {

    @Override
    protected void configure() {
        register(SampleEndpoint.class);
        register(SampleEndpoint2.class);
        register(SendRabbitEndpoint.class);
        register(JerseyUserEndpointController.class);

        register(InvalidIdentifierExceptionMapper.class);
        register(CustomErrorAttributes.class);
        register(CustomErrorController.class);
    }
}
