import client.ScooterServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.OrderRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import static org.hamcrest.Matchers.notNullValue;


@RunWith(Parameterized.class)
public class CreateOrderTests {

    private int orderTrack;
    private final List<String> colors;
    private ScooterServiceClient client;

    public CreateOrderTests(List<String> colors) {
        this.colors = colors;
    }

    @Before
    public void setUp() {
        client = new ScooterServiceClient();
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
        ValidatableResponse response = client.sendCreateOrderRequest(orderRequest);
        checkStatusCode201(response);
        orderTrack = client.extractTrackNumber(response);
    }

    @After
    public void tearDown() {

        if (orderTrack != 0) {
            client.cancelOrder(orderTrack);
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

    @Step("Check positive order creation response code (201 Created)")
    private void checkStatusCode201(ValidatableResponse response) {
        response.log()
                .all()
                .statusCode(201)
                .body("track", notNullValue());
    }
}
