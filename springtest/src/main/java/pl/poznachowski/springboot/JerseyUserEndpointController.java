package pl.poznachowski.springboot;

import com.google.common.base.Strings;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.poznachowski.springboot.exception.InvalidIdentifierException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path("/jsuser")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(tags = "/jsuser")
public class JerseyUserEndpointController {
    private static final Logger LOG = LoggerFactory.getLogger(JerseyUserEndpointController.class);

    @GET
    public String getUserUsingQueryVariable(@QueryParam("id") String id) {
        final String userId = id.trim();
        validateId(userId);
        return "Query variable:" + id;
    }

    @GET
    @Path("/{id}")
    public String getUserUsingPathVariable(@PathParam("id") String id) {
        final String userId = id.trim();
        validateId(userId);
        return "Path variable:" + id;
    }

    private void validateId(String id) {
        if (Strings.isNullOrEmpty(id))
            throw new InvalidIdentifierException();

        int parsedId = Integer.parseInt(id);
        if (parsedId < 0)
            throw new IllegalArgumentException();
    }
}