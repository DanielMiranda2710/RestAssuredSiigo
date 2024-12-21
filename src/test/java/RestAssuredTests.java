import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import static io.restassured.RestAssured.given;

public class RestAssuredTests {

    @BeforeEach
    public void setUp(){
            RestAssured.baseURI = "https://reqres.in/";
            RestAssured.basePath = "/api";
            RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
            RestAssured.requestSpecification = new RequestSpecBuilder()
                    .setContentType(ContentType.JSON)
                    .build();
    }

    @Test
    public void GetSingleUserTest(){
        given()
            .get("users/2")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("data.id", equalTo(2));
    }

    @Test
    public void PostLoginSuccessfullyTest(){
        given()
            .body("{\n" +
                    "    \"email\": \"eve.holt@reqres.in\",\n" +
                    "    \"password\": \"cityslicka\"\n" +
                    "}")
            .post("login")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("token", notNullValue());
    }

    @Test
    public void PostLoginUnsuccessfulTest(){
        String jobUpdated= given()
                .when()
                .body("{\n" +
                        "    \"email\": \"peter@klaven\"\n" +
                        "}")
                .post("login")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST).extract().jsonPath().getString("error");
        assertThat(jobUpdated, equalTo("Missing password"));
    }

    @Test
    public void PostCreateUserTest(){
        String nameValidation= given()
                .when()
                .body("{\n" +
                        "    \"name\": \"DanielShelby\",\n" +
                        "    \"job\": \"QA Lead\"\n" +
                        "}")
                .post("users")
                .then()
                .statusCode(HttpStatus.SC_CREATED).extract().jsonPath().getString("name");
        assertThat(nameValidation, equalTo("DanielShelby"));
    }

    @Test
    public void DeleteUserTest(){
        given()
                .delete("users/2")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void PutUpdateTest(){
         String nameUpdated= given()
            .body("{\n" +
                    "    \"name\": \"Mr Shelby\",\n" +
                    "    \"job\": \"BusinessMan\"\n" +
                    "}")
            .put("users/2")
            .then()
            .statusCode(HttpStatus.SC_OK).extract().jsonPath().getString("name");
         assertThat(nameUpdated, equalTo("Mr Shelby"));
    }

    @Test
    public void PatchUpdateTest(){
        String jobUpdated= given()
                .when()
                .body("{\n" +
                        "    \"name\": \"Mr Shelby\",\n" +
                        "    \"job\": \"Senior SDET\"\n" +
                        "}")
                .patch("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK).extract().jsonPath().getString("job");
        assertThat(jobUpdated, equalTo("Senior SDET"));
    }


}
