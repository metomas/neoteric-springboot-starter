package pl.poznachowski.springboot.exception.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.poznachowski.springboot.exception.InvalidIdentifierException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class InvalidIdentifierExceptionMapper implements ExceptionMapper<InvalidIdentifierException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidIdentifierExceptionMapper.class);

    @Override
    public Response toResponse(InvalidIdentifierException exception) {
        LOGGER.warn("Exception mapped to response", exception);

        return Response
                .status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}