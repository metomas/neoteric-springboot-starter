package pl.poznachowski.springboot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoteric.starter.test.restassured.ContainerIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(SpringbootTestApplication.class)
@ContainerIntegrationTest
public class SpringMVCUserEndpointControllerTest {
    private static final String GET_USER_USING_QUERY_VARIABLE_FORMAT = "user?id=%s";
    private static final String GET_USER_USING_PATH_VARIABLE_FORMAT = "user/%s";

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldGetUserUsingQueryVariable() throws Exception {
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get(String.format(GET_USER_USING_QUERY_VARIABLE_FORMAT, "123"))
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .content(Matchers.equalTo("Query variable:123"));
    }

    @Test
    public void shouldGetUserUsingPathVariable() throws Exception {
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get(String.format(GET_USER_USING_PATH_VARIABLE_FORMAT, "123"))
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .content(Matchers.equalTo("Path variable:123"));
    }

    @Test
    public void shouldFailGetUserUsingQueryVariableIfInvalidEmptyId() throws Exception {
        String responseContent = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get(String.format(GET_USER_USING_QUERY_VARIABLE_FORMAT, " "))
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract()
                .asString();

        Map<String, String> responseContentMap = objectMapper.readValue(responseContent, new TypeReference<HashMap<String, String>>() {
        });

        assertThat(responseContentMap)
                .hasSize(8)
                .contains(
                        entry("status", "400"),
                        entry("error", "Bad Request"),
                        entry("exception", "pl.poznachowski.springboot.exception.InvalidIdentifierException"),
                        entry("message", "Identifier cannot be null"),
                        entry("path", "/user"),
                        entry("contentType", "application/json; charset=UTF-8")
                )
                .containsKeys("timestamp", "status", "error", "exception", "message", "path", "contentType", "requestId");
        assertThat(responseContentMap.get("timestamp")).isNotNull();
        assertThat(responseContentMap.get("requestId")).isNull();
    }

    @Test
    public void shouldFailGetUserUsingPathVariableOnInvalidEmptyId() throws Exception {
        String responseContent = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get(String.format(GET_USER_USING_PATH_VARIABLE_FORMAT, " "))
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract()
                .asString();

        Map<String, String> responseContentMap = objectMapper.readValue(responseContent, new TypeReference<HashMap<String, String>>() {
        });

        assertThat(responseContentMap)
                .hasSize(8)
                .contains(
                        entry("status", "400"),
                        entry("error", "Bad Request"),
                        entry("exception", "org.springframework.web.bind.MissingServletRequestParameterException"),
                        entry("message", "Required String parameter 'id' is not present"),
                        entry("path", "/user/"),
                        entry("contentType", "application/json; charset=UTF-8")
                )
                .containsKeys("timestamp", "status", "error", "exception", "message", "path", "contentType", "requestId");
        assertThat(responseContentMap.get("timestamp")).isNotNull();
        assertThat(responseContentMap.get("requestId")).isNull();
    }

    @Test
    public void shouldFailGetUserUsingQueryVariableOnInvalidNonIntegerId() throws Exception {
        String responseContent = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get(String.format(GET_USER_USING_QUERY_VARIABLE_FORMAT, "abc"))
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract()
                .asString();

        Map<String, String> responseContentMap = objectMapper.readValue(responseContent, new TypeReference<HashMap<String, String>>() {
        });

        assertThat(responseContentMap)
                .hasSize(8)
                .contains(
                        entry("status", "400"),
                        entry("error", "Bad Request"),
                        entry("exception", "java.lang.NumberFormatException"),
                        entry("message", "Unable to convert identifier to number"),
                        entry("path", "/user"),
                        entry("contentType", "application/json; charset=UTF-8")
                )
                .containsKeys("timestamp", "status", "error", "exception", "message", "path", "contentType", "requestId");
        assertThat(responseContentMap.get("timestamp")).isNotNull();
        assertThat(responseContentMap.get("requestId")).isNull();
    }

    @Test
    public void shouldFailGetUserUsingQueryVariableOnInvalidNegativeId() throws Exception {
        String responseContent = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get(String.format(GET_USER_USING_QUERY_VARIABLE_FORMAT, "-1"))
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract()
                .asString();

        Map<String, String> responseContentMap = objectMapper.readValue(responseContent, new TypeReference<HashMap<String, String>>() {
        });

        assertThat(responseContentMap)
                .hasSize(8)
                .contains(
                        entry("status", "400"),
                        entry("error", "Bad Request"),
                        entry("exception", "java.lang.IllegalArgumentException"),
                        entry("message", "Unhandled exception"),
                        entry("path", "/user"),
                        entry("contentType", "application/json; charset=UTF-8")
                )
                .containsKeys("timestamp", "status", "error", "exception", "message", "path", "contentType", "requestId");
        assertThat(responseContentMap.get("timestamp")).isNotNull();
        assertThat(responseContentMap.get("requestId")).isNull();
    }
}