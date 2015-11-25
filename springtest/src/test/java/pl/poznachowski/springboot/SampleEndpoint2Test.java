package pl.poznachowski.springboot;

import com.neoteric.starter.mongo.test.EmbeddedMongoTest;
import com.neoteric.starter.test.ReinjectableSpringBootTest;
import com.neoteric.starter.test.reinject.ReinjectableSpringApplicationContextLoader;
import com.neoteric.starter.test.restassured.ContainerIntegrationTest;
import com.neoteric.starter.test.wiremock.WireMockTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.poznachowski.springboot.mongo.Person;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static com.jayway.restassured.RestAssured.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ReinjectableSpringBootTest(classes = {SpringbootTestApplication.class}, initializers = ReinjectableSpringApplicationContextLoader.ReinjectInitializer.class)
@ContainerIntegrationTest
@EmbeddedMongoTest(dropCollections = "Person")
@WireMockTest("testService")
public class SampleEndpoint2Test {

    private static final Logger LOG = LoggerFactory.getLogger(SampleEndpoint2Test.class);


    @Autowired
    MongoTemplate mongoTemplate;

//    @Test
//    public void testWiremock() throws Exception {
//
//        givenThat(get(urlPathEqualTo("/api/v1/test"))
//                .willReturn(aResponse()
//                        .withHeader("Content-Type", "text/plain")
//                        .withBody("Hello world!")));
//
//        when()
//                .get("api/v1/endpoint")
//                .then()
//                .log().all()
//                .assertThat()
//                .statusCode(Response.Status.OK.getStatusCode());
//    }

    @Test
    public void testName() throws Exception {

        LOG.error("TEST2_SAMPLE1");
        mongoTemplate.insert(new Person("DSK", 50, LocalDateTime.now(), ZonedDateTime.now()));

        when()
                .get("api/v1/test")
                .then()
                .log().all()
                .assertThat()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void testName2() throws Exception {

        LOG.error("TEST2_SAMPLE2");
        mongoTemplate.insert(new Person("DSK", 50, LocalDateTime.now(), ZonedDateTime.now()));

        when()
                .get("api/v1/test")
                .then()
                .log().all()
                .assertThat()
                .statusCode(Response.Status.OK.getStatusCode());
    }
}