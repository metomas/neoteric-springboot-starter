package pl.poznachowski.springboot;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/v1/endpoint")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(tags = "/v1/endpoint")
public class SampleEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(SampleEndpoint.class);

    @Autowired
    ExternalServiceAPI externalServiceAPI;

    @GET
    public Response get() {
        LOG.error("ERROR");
        TestJSON json = callExternalApi();
        LOG.info("RETURNED FROM OTHER SERVICE: {}", json);
        return Response.ok(json).build();
    }

    @HystrixCommand(fallbackMethod = "defaultGet")
    private TestJSON callExternalApi() {
        return externalServiceAPI.get();
    }

    private TestJSON defaultGet() {
        return new TestJSON("fallback", "fallback");
    }
}