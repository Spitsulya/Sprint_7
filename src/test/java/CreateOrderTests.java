import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTests {

    private int orderTrack;
    private final List<String> colors;

    public CreateOrderTests(List<String> colors) {
        this.colors = colors;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Parameterized.Parameters(name = "Test with colors: {0}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList("BLACK")},
                {Arrays.asList("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {Arrays.asList()}
        });
    }

    @Test
    @DisplayName("Successful order creation")
    @Description("Positive tests for POST request to /api/v1/orders endpoint by using parameterization of optional color field and receiving track in response")
    public void testCreateOrder() {

        OrderRequest orderRequest = createOrderRequest(colors);
        Response response = sendCreateOrderRequest(orderRequest);
        checkStatusCode201(response);
        orderTrack = getOrderTrack(response);

    }

    @After
    public void tearDown() {

        if (orderTrack != 0) {
            cancelOrder(orderTrack);
        }
    }

    @Step("Creating order request with colors: {colors}")
    private OrderRequest createOrderRequest(List<String> colors) {
        return new OrderRequest(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                4,
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                colors
        );
    }

    @Step("POST request to \"/api/v1/orders\" to create an order")
    private Response sendCreateOrderRequest(OrderRequest orderRequest) {
        Response response =
                given()
                    .header("Content-type", "application/json")
                    .body(orderRequest)
                    .when()
                    .post("/api/v1/orders");
        return response;
    }

    @Step("Check positive order creation response code (201 Created)")
    private void checkStatusCode201(Response response) {
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Step("Receive track")
    public int getOrderTrack(Response response) {

        if (response.statusCode() != 201) {
            return 0;
        }
        int orderTrack = response.then()
                .extract()
                .path("track");

        return orderTrack;
    }

    @Step("Order canceling and completing the test, PUT /api/v1/orders/cancel/ with params track")
    public void cancelOrder(int orderTrack) {
        given()
                .header("Content-type", "application/json")
                .when()
                .queryParam("track", orderTrack)
                .put("/api/v1/orders/cancel")
                .then()
                .statusCode(200);
    }
}
