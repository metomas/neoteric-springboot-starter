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
public class JerseyUserEndpointControllerTest {

    private static final String GET_USER_USING_QUERY_VARIABLE_FORMAT = "api/jsuser?id=%s";
    private static final String GET_USER_USING_PATH_VARIABLE_FORMAT = "api/jsuser/%s";

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldGetUserUsingQueryVariable() throws Exception {
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get(String.format(GET_USER_USING_QUERY_VARIABLE_FORMAT, "123"))
                .then()
                .contentType(MediaType.APPLICATION_JSON)
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
                .contentType(MediaType.APPLICATION_JSON)
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
                .hasSize(7)
                .contains(
                        entry("status", "400"),
                        entry("error", "Bad Request"),
                        entry("message", "Bad Request"),
                        entry("path", "/api/jsuser"),
                        entry("contentType", "application/json; charset=UTF-8")
                )
                .containsKeys("timestamp", "status", "error", "message", "path", "contentType", "requestId");
        assertThat(responseContentMap.get("timestamp")).isNotNull();
        assertThat(responseContentMap.get("requestId")).isNotNull();
    }

    @Test
    public void shouldFailGetUserUsingPathVariableOnInvalidEmptyId() throws Exception {
        String responseContent = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get(String.format(GET_USER_USING_PATH_VARIABLE_FORMAT, " "))
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .extract()
                .asString();

        Map<String, String> responseContentMap = objectMapper.readValue(responseContent, new TypeReference<HashMap<String, String>>() {
        });

        assertThat(responseContentMap)
                .hasSize(7)
                .contains(
                        entry("status", "500"),
                        entry("error", "Internal Server Error"),
                        entry("message", "Request failed."),
                        entry("path", "/api/jsuser/"),
                        entry("contentType", "application/json; charset=UTF-8")
                )
                .containsKeys("timestamp", "status", "error", "message", "path", "contentType", "requestId");
        assertThat(responseContentMap.get("timestamp")).isNotNull();
        assertThat(responseContentMap.get("requestId")).isNotNull();
    }

    @Test
    public void shouldFailGetUserUsingQueryVariableOnInvalidNonIntegerId() throws Exception {
        String responseContent = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get(String.format(GET_USER_USING_QUERY_VARIABLE_FORMAT, "abc"))
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .extract()
                .asString();

        Map<String, String> responseContentMap = objectMapper.readValue(responseContent, new TypeReference<HashMap<String, String>>() {
        });

        assertThat(responseContentMap)
                .hasSize(7)
                .contains(
                        entry("status", "500"),
                        entry("error", "Internal Server Error"),
                        entry("message", "Request failed."),
                        entry("path", "/api/jsuser"),
                        entry("contentType", "application/json; charset=UTF-8")
                )
                .containsKeys("timestamp", "status", "error", "message", "path", "contentType", "requestId");
        assertThat(responseContentMap.get("timestamp")).isNotNull();
        assertThat(responseContentMap.get("requestId")).isNotNull();
    }

    @Test
    public void shouldFailGetUserUsingQueryVariableOnInvalidNegativeId() throws Exception {
        String responseContent = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get(String.format(GET_USER_USING_QUERY_VARIABLE_FORMAT, "-1"))
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .extract()
                .asString();

        Map<String, String> responseContentMap = objectMapper.readValue(responseContent, new TypeReference<HashMap<String, String>>() {
        });

        assertThat(responseContentMap)
                .hasSize(7)
                .contains(
                        entry("status", "500"),
                        entry("error", "Internal Server Error"),
                        entry("message", "Request failed."),
                        entry("path", "/api/jsuser"),
                        entry("contentType", "application/json; charset=UTF-8")
                )
                .containsKeys("timestamp", "status", "error", "message", "path", "contentType", "requestId");
        assertThat(responseContentMap.get("timestamp")).isNotNull();
        assertThat(responseContentMap.get("requestId")).isNotNull();
    }
}
