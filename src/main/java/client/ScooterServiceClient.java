package client;
import io.restassured.response.ValidatableResponse;
import model.CourierData;
import io.qameta.allure.Step;
import model.Credentials;
import static io.restassured.RestAssured.given;


public class ScooterServiceClient {

    private String baseURI;

    public ScooterServiceClient(String baseURI) {
        this.baseURI = baseURI;
    }

    @Step("Courier creating with data")
    public ValidatableResponse createCourierPostRequest(CourierData courierData) {

        return given()
                        .log()
                        .all()
                        .baseUri(baseURI)
                        .header("Content-type", "application/json")
                        .body(courierData)
                        .when()
                        .post("/api/v1/courier")
                        .then()
                        .log()
                        .all();
    }

    @Step("Courier authorization with existing data")
    public ValidatableResponse authorizeCourier(Credentials credentials) {
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .body(credentials)
                .post("/api/v1/courier/login")
                .then()
                .log()
                .all();
    }

    @Step("Courier removing with ID and completing the test, DELETE /api/v1/courier/:id")
    public ValidatableResponse deleteCourier(int courierId) {

        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .log()
                .all()
                .statusCode(200);
    }
}