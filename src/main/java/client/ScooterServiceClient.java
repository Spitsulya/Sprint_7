package client;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.ValidatableResponse;
import model.CourierData;
import io.qameta.allure.Step;
import model.Credentials;
import static io.restassured.RestAssured.given;
import model.OrderRequest;
import model.constants.*;


public class ScooterServiceClient {

    private String baseURI;

    public ScooterServiceClient() {
        this.baseURI = Url.BASE_URI;
    }

    @Step("Courier creating with data")
    public ValidatableResponse createCourierPostRequest(CourierData courierData) {

        return given()
                        .filter(new AllureRestAssured())
                        .log()
                        .all()
                        .baseUri(baseURI)
                        .header("Content-type", "application/json")
                        .body(courierData)
                        .when()
                        .post(Endpoints.CREATE_COURIER)
                        .then()
                        .log()
                        .all();
    }

    @Step("Courier authorization with existing data")
    public ValidatableResponse authorizeCourier(Credentials credentials) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .body(credentials)
                .post(Endpoints.LOGIN_COURIER)
                .then()
                .log()
                .all();
    }

    @Step("Courier removing with ID and completing the test, DELETE /api/v1/courier/:id")
    public ValidatableResponse deleteCourier(int courierId) {

        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .when()
                .delete(Endpoints.DELETE_COURIER + courierId)
                .then()
                .log()
                .all()
                .statusCode(200);
    }

    @Step("POST request to \"/api/v1/orders\" to create an order")
    public ValidatableResponse sendCreateOrderRequest(OrderRequest orderRequest) {

        return given()
                        .filter(new AllureRestAssured())
                        .log()
                        .all()
                        .baseUri(baseURI)
                        .header("Content-type", "application/json")
                        .body(orderRequest)
                        .post(Endpoints.CREATE_ORDER)
                        .then()
                        .log()
                        .all();
    }

    @Step("Extract track number from response")
    public int extractTrackNumber(ValidatableResponse response) {
        return response.extract().path("track");
    }

    @Step("Order canceling and completing the test, PUT /api/v1/orders/cancel/ with params track")
    public ValidatableResponse cancelOrder(int orderTrack) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .when()
                .queryParam("track", orderTrack)
                .put(Endpoints.CANCEL_ORDER)
                .then()
                .log()
                .all()
                .statusCode(200);
    }

    @Step("Send GET request to retrieve the order list")
    public ValidatableResponse getOrderList() {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-type", "application/json")
                .get(Endpoints.GET_ORDER_LIST)
                .then()
                .log()
                .all();
    }
}