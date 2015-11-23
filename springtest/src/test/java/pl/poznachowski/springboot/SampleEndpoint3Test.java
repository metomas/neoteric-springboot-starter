package pl.poznachowski.springboot;

import com.neoteric.starter.mongo.test.EmbeddedMongoTest;
import com.neoteric.starter.test.restassured.ContainerIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;
import pl.poznachowski.springboot.mongo.Person;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static com.jayway.restassured.RestAssured.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(SpringbootTestApplication.class)
@ContainerIntegrationTest
@EmbeddedMongoTest
public class SampleEndpoint3Test {

    @Autowired
    EmbeddedWebApplicationContext server;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void testName() throws Exception {

        mongoTemplate.insert(new Person("DSK", 50, LocalDateTime.now(), ZonedDateTime.now()));

        when()
//                .get("/autoconfig")
                .get("api/v1/test")
                .then()
                .log().all()
                .assertThat()
                .statusCode(Response.Status.OK.getStatusCode());
    }
}
