package pl.poznachowski.springboot;

import com.neoteric.starter.mongo.test.EmbeddedMongoTest;
import com.neoteric.starter.test.restassured.ContainerIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.poznachowski.springboot.mongo.Person;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static com.jayway.restassured.RestAssured.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(value = {SpringbootTestApplication.class, SampleEndpoint2Test.Config.class})
@ContainerIntegrationTest
@EmbeddedMongoTest(dropCollections = "Person")
@ActiveProfiles("dev")
public class SampleEndpoint2Test {

    @Configuration
    static class Config {

        // this bean will be injected into the OrderServiceTest class
        @Bean
        @Profile("dev")
        @Primary
        public TextReturner orderService() {
            TextReturner orderService = new TextReturner() {
                @Override
                public String returnString() {
                    return "XYZ";
                }
            };
            return orderService;
        }
    }

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