package pl.poznachowski.springboot;

import org.springframework.cloud.netflix.feign.FeignClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@FeignClient(serviceId = "springtest2-service")
public interface ExternalServiceAPI {

    @GET
    @Path("/api/v1/test")
    TestJSON get();
}
