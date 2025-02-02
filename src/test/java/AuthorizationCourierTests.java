import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;


public class AuthorizationCourierTests {

    private int courierId;
    private String jsonSuccess = "{\"login\": \"timonin\", \"password\": \"1234\", \"firstName\": \"elina\"}";
    private String jsonPassword = "{\"password\": \"1234\", \"firstName\": \"elina\"}";
    private String jsonLogin = "{\"login\": \"timonin\", \"firstName\": \"elina\"}";
    private String jsonWrong = "{\"login\": \"wronglogin\", \"password\": \"1111\", \"firstName\": \"elina\"}";

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        createCourierPostRequest();
    }

    @Test
    @DisplayName("Successful authorization of a courier/get ID")
    @Description("Positive test for POST request to /api/v1/courier/login endpoint by filling in existing data and getting ID")
        public void AuthorizationCourierSucessfullyTest() {

        Response response = authorizeCourier(jsonSuccess);
        checkStatusCode200(response);
        checkForIdAvailability(response);
        courierId = getCourierId(response);
    }

    @Test
    @DisplayName("Unsuccessful authorization without couriers's login")
    @Description("Negative test for POST request to /api/v1/courier/login endpoint by not using all required fields")
        public void AuthorizeCourierWithoutLoginImpossibleTest() {


        Response response = authorizeCourier(jsonPassword);
        checkStatusCode400(response);
        checkResponseBody400(response);
        courierId = getCourierId(response);
    }

    @Test
    @DisplayName("Unsuccessful authorization without couriers's password")
    @Description("Negative test for POST request to /api/v1/courier/login endpoint by not using all required fields")
        public void AuthorizeCourierWithoutPasswordImpossibleTest() {

        Response response = authorizeCourier(jsonLogin);
        checkStatusCode400(response);
        checkResponseBody400(response);
        courierId = getCourierId(response);
    }

    @Test
    @DisplayName("Unsuccessful authorization with non-existent courier's data")
    @Description("Negative test for POST request to /api/v1/courier/login endpoint by using non-existent courier's data")
    public void AuthorizeCourierWithNonexistentDataTest() {

        Response response = authorizeCourier(jsonWrong);
        checkStatusCode404(response);
        checkResponseBody404(response);
        courierId = getCourierId(response);
    }

    @After
    public void tearDown() {

        if (courierId != 0) {
           deleteCourier(courierId);
        }
    }


    @Step ("Successful courier creating")
    public Response createCourierPostRequest() {

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(jsonSuccess)
                        .when()
                        .post("/api/v1/courier");
        return response;
    }

    @Step("Courier authorization with existing data")
    public Response authorizeCourier(String json) {

        Response response =
                given()
                         .header("Content-type", "application/json")
                         .body(json)
                         .when()
                         .post("/api/v1/courier/login");
            return response;
    }

    @Step ("Check positive authorization response code (200)")
    public void checkStatusCode200(Response response) {
        response.then().assertThat()
                .statusCode(200);
    }

    @Step("Receive ID")
    public int getCourierId(Response response) {

        if (response.statusCode() != 200) {
            return 0;
        }
        int courierId = response.then()
                .extract()
                .path("id");

        return courierId;
    }

    @Step("Courier removing with ID and completing the test, DELETE /api/v1/courier/:id")
    public void deleteCourier(int courierId) {
        given()
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
    }

    @Step ("Check for courier ID in response")
    public void checkForIdAvailability(Response response) {
        response.then().assertThat()
                .body("id", notNullValue());
    }

    @Step ("Check negative authorization response code (400 Bad Request)")
    public void checkStatusCode400(Response response) {
        response.then().assertThat()
                .statusCode(400);
    }

    @Step ("Check negative authorization response message {\"message\": \"Недостаточно данных для входа\"}")
    public void checkResponseBody400(Response response) {
        response.then().assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Step ("Check negative authorization response code (404 Bad Request)")
    public void checkStatusCode404(Response response) {
        response.then().assertThat()
                .statusCode(404);
    }

    @Step ("Check negative authorization response message {\"message\": \"Учетная запись не найдена\"}")
    public void checkResponseBody404(Response response) {
        response.then().assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
