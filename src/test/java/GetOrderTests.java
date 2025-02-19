import client.ScooterServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import model.OrderData;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.apache.http.HttpStatus.SC_OK;


public class GetOrderTests {

    private ScooterServiceClient client;

    @Before
    public void setUp() {
        client = new ScooterServiceClient();
    }

    @Test
    @DisplayName("Successful receipt of order list")
    @Description("Positive tests for GET request to /api/v1/orders endpoint")
    public void getOrderSuccseccfully () {

        ValidatableResponse response = client.getOrderList();
        checkStatusCode200(response);

        List<OrderData> orders = extractOrderList(response);
        verifyOrderListIsNotEmpty(orders);
    }


    @Step("Check positive order getting response code (200 OK)")
    private void checkStatusCode200(ValidatableResponse response) {
        response.statusCode(SC_OK);
    }

    @Step("Extract the list of orders from the response")
    private List<OrderData> extractOrderList(ValidatableResponse response) {
        return response.extract().jsonPath().getList("orders", OrderData.class);
    }

    @Step("Verify that the order list is not empty")
    private void verifyOrderListIsNotEmpty(List<OrderData> orders) {
        assertFalse("The order list should not be empty", orders.isEmpty());
    }
}


