import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CourierData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class CreateCourierTests {

    private CourierData courierData;
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    @DisplayName("Successful creation of a courier")
    @Description("Positive test for POST request to /api/v1/courier endpoint by filling in all required fields")
    public void CreateCourierSucessfullyTest() {

    courierData = new CourierData("leylaakнkal", "1234", "elina");

    Response response = createCourierPostRequest(courierData);
    checkStatusCode201(response);
    checkResponseBody201(response);
    printResponseBodyToConsole(response);
    }

    @Test
    @DisplayName("Unsuccessful creation of two identical couriers")
    @Description("Negative test for POST request to /api/v1/courier endpoint by using the same courier's data")
    public void CreateTwoIdenticalCouriersImpossibleTest() {

        courierData = new CourierData("leylaakнkal", "1234", "elina");

        Response response = createCourierPostRequest(courierData);
        checkStatusCode201(response);

        Response response1 = createCourierPostRequest(courierData);
        checkStatusCode409(response1);
        checkResponseBody409(response1);
        printResponseBodyToConsole(response1);
    }

    @Test
    @DisplayName("Unsuccessful creation without couriers's login")
    @Description("Negative test for POST request to /api/v1/courier endpoint by not using all required fields")
    public void CreateCourierWithoutLoginImpossibleTest() {

        courierData = new CourierData("", "1234", "elina");
        Response response = createCourierPostRequest(courierData);
        checkStatusCode400(response);
        checkResponseBody400(response);
        printResponseBodyToConsole(response);
    }

    @Test
    @DisplayName("Unsuccessful creation without couriers's password")
    @Description("Negative test for POST request to /api/v1/courier endpoint by not using all required fields")
    public void CreateCourierWithoutPasswordImpossibleTest() {

        courierData = new CourierData("apapapa", "", "elina");

        Response response = createCourierPostRequest(courierData);
        checkStatusCode400(response);
        checkResponseBody400(response);
        printResponseBodyToConsole(response);
    }


    @After
    public void tearDown() {

        int courierId = authorizeAndGetCourierId(courierData);

        if (courierId != 0) {
            deleteCourier(courierId);
        }
    }


    @Step ("Courier creating with data {json}")
    public Response createCourierPostRequest(CourierData courierData) {

        Response response =
                given()
                        .log()
                        .all()
                        .header("Content-type", "application/json")
                        .body(courierData)
                        .when()
                        .post("/api/v1/courier");
        return response;
    }

    @Step ("Check positive response code (201 Created)")
    public void checkStatusCode201(Response response) {
        response.then().log().all().assertThat()
                .statusCode(201);
    }

    @Step ("Check positive response message {ok: true}")
    public void checkResponseBody201(Response response) {
        response.then().log().all().assertThat()
                .body("ok", equalTo(true));
    }

    @Step("Print actual response body")
    public void printResponseBodyToConsole(Response response){
        String responseBody = response.getBody().asString();
        System.out.println(response.body().asString());
    }

    @Step ("Check negative response code (409 Сonflict)")
    public void checkStatusCode409(Response response) {
        response.then().log().all().assertThat()
                .statusCode(409);
    }

    @Step ("Check negative response message {\"message\": \"Этот логин уже используется\"}")
    public void checkResponseBody409(Response response) {
        response.then().log().all().assertThat()
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Step ("Check negative response code (400 Bad Request)")
    public void checkStatusCode400(Response response) {
        response.then().log().all().assertThat()
                .statusCode(400);
    }

    @Step ("Check negative response message {\"message\": \"Недостаточно данных для создания учетной записи\"}")
    public void checkResponseBody400(Response response) {
        response.then().log().all().assertThat()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Courier authorization to receive ID, POST /api/v1/courier/login")
    public int authorizeAndGetCourierId(CourierData courierData) {
        Response loginResponse = given()
                .log()
                .all()
                .header("Content-type", "application/json")
                .body(courierData)
                .when()
                .post("/api/v1/courier/login");

        if (loginResponse.getStatusCode() != 200) {
            return 0;
        }

        int courierId = loginResponse.then()
                .extract()
                .path("id");
        return courierId;
    }

    @Step("Courier removing with ID and completing the test, DELETE /api/v1/courier/:id")
    public void deleteCourier(int courierId) {
        given()
                .log()
                .all()
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .log()
                .all()
                .statusCode(200);
    }
}
