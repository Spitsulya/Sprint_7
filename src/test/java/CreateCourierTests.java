import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class CreateCourierTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Successful creation of a courier")
    @Description("Positive test for POST request to /api/v1/courier endpoint by filling in all required fields")
    public void CreateCourierSucessfullyTest() {

    String json = "{\"login\": \"timonin\", \"password\": \"1234\", \"firstName\": \"elina\"}";

    Response response = createCourierPostRequest(json);
    checkStatusCode201(response);
    checkResponseBody201(response);
    printResponseBodyToConsole(response);
    }

    @Test
    @DisplayName("Unsuccessful creation of two identical couriers")
    @Description("Negative test for POST request to /api/v1/courier endpoint by using the same courier's data")
    public void CreateTwoIdenticalCouriersImpossibleTest() {
        String json = "{\"login\": \"timonin\", \"password\": \"1234\", \"firstName\": \"elina\"}";

        Response response = createCourierPostRequest(json);
        checkStatusCode201(response);

        Response response1 = createCourierPostRequest(json);
        checkStatusCode409(response1);
        checkResponseBody409(response1);
        printResponseBodyToConsole(response1);
    }

    @Test
    @DisplayName("Unsuccessful creation without couriers's login")
    @Description("Negative test for POST request to /api/v1/courier endpoint by not using all required fields")
    public void CreateCourierWithoutLoginImpossibleTest() {
        String json = "{\"password\": \"1234\", \"firstName\": \"elina\"}";

        Response response = createCourierPostRequest(json);
        checkStatusCode400(response);
        checkResponseBody400(response);
        printResponseBodyToConsole(response);
    }

    @Test
    @DisplayName("Unsuccessful creation without couriers's password")
    @Description("Negative test for POST request to /api/v1/courier endpoint by not using all required fields")
    public void CreateCourierWithoutPasswordImpossibleTest() {
        String json = "{\"login\": \"timonin\", \"firstName\": \"elina\"}";

        Response response = createCourierPostRequest(json);
        checkStatusCode400(response);
        checkResponseBody400(response);
        printResponseBodyToConsole(response);
    }


    @After
    public void tearDown() {
        // Шаг 1: Авторизация курьера и получение его ID
        Integer courierId = authorizeAndGetCourierId("timonin", "1234");

        // Шаг 2: Удаление курьера, если ID получен
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }



    @Step ("Courier creating with data {json}")
    public Response createCourierPostRequest(String json) {

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(json)
                        .when()
                        .post("/api/v1/courier");
        return response;
    }

    @Step ("Check positive response code (201 Created)")
    public void checkStatusCode201(Response response) {
        response.then().assertThat()
                .statusCode(201);
    }

    @Step ("Check positive response message {ok: true}")
    public void checkResponseBody201(Response response) {
        response.then().assertThat()
                .body("ok", equalTo(true));
    }

    @Step("Print actual response body")
    public void printResponseBodyToConsole(Response response){
        String responseBody = response.getBody().asString();
        System.out.println(response.body().asString());
    }

    @Step ("Check negative response code (409 Сonflict)")
    public void checkStatusCode409(Response response) {
        response.then().assertThat()
                .statusCode(409);
    }

    @Step ("Check negative response message {\"message\": \"Этот логин уже используется\"}")
    public void checkResponseBody409(Response response) {
        response.then().assertThat()
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Step ("Check negative response code (400 Bad Request)")
    public void checkStatusCode400(Response response) {
        response.then().assertThat()
                .statusCode(400);
    }

    @Step ("Check negative response message {\"message\": \"Недостаточно данных для создания учетной записи\"}")
    public void checkResponseBody400(Response response) {
        response.then().assertThat()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Courier authorization to receive ID, POST /api/v1/courier/login")
    public Integer authorizeAndGetCourierId(String login, String password) {
        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"%s\"}", login, password))
                .when()
                .post("/api/v1/courier/login");

        if (loginResponse.statusCode() != 200) {
            return null;
        }

        return loginResponse.then()
                .extract()
                .path("id");
    }

    @Step("Courier removing with ID and completing the test, DELETE /api/v1/courier/:id")
    public void deleteCourier(Integer courierId) {
        given()
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
    }
}
