import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import client.ScooterServiceClient;
import model.CourierData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;


public class AuthorizationCourierTests {

    private CourierData courierData;
    private ScooterServiceClient client;


    @Before
    public void setUp() {
        client = new ScooterServiceClient();
        courierData = new CourierData("elinacourier", "1234", "elina");
        client.createCourierPostRequest(courierData);
    }

    @Test
    @DisplayName("Successful authorization of a courier/get ID")
    @Description("Positive test for POST request to /api/v1/courier/login endpoint by filling in existing data and getting ID")
    public void AuthorizationCourierSucessfullyTest() {

        Credentials credentials = Credentials.fromCourierData(courierData);
        ValidatableResponse response = client.authorizeCourier(credentials);

        checkStatusCode200(response);
        checkForIdAvailability(response);
    }

    @Test
    @DisplayName("Unsuccessful authorization without couriers's login")
    @Description("Negative test for POST request to /api/v1/courier/login endpoint by not using all required fields")
        public void AuthorizeCourierWithoutLoginImpossibleTest() {

        Credentials credentials = Credentials.fromCourierDataPasswordOnly(courierData);
        ValidatableResponse response = client.authorizeCourier(credentials);

        checkStatusCode400(response);
        checkResponseBody400(response);
    }

    @Test
    @DisplayName("Unsuccessful authorization without couriers's password")
    @Description("Negative test for POST request to /api/v1/courier/login endpoint by not using all required fields")
        public void AuthorizeCourierWithoutPasswordImpossibleTest() {

        Credentials credentials = Credentials.fromCourierDataLoginOnly(courierData);
        ValidatableResponse response = client.authorizeCourier(credentials);

        checkStatusCode400(response);
        checkResponseBody400(response);
    }

    @Test
    @DisplayName("Unsuccessful authorization with non-existent courier's data")
    @Description("Negative test for POST request to /api/v1/courier/login endpoint by using invalid courier's login")
    public void AuthorizeCourierWithInvalidLoginTest() {

            Credentials credentials = Credentials.invalidLogin(courierData);
            ValidatableResponse response = client.authorizeCourier(credentials);

            checkStatusCode404(response);
            checkResponseBody404(response);
    }

    @Test
    @DisplayName("Unsuccessful authorization with non-existent courier's data")
    @Description("Negative test for POST request to /api/v1/courier/login endpoint by using invalid courier's password")
    public void AuthorizeCourierWithInvalidPasswordTest() {

        Credentials credentials = Credentials.invalidPassword(courierData);
        ValidatableResponse response = client.authorizeCourier(credentials);

        checkStatusCode404(response);
        checkResponseBody404(response);
    }


    @After
    public void tearDown() {
        int courierId = client.authorizeCourier(Credentials.fromCourierData(courierData))
                .extract().path("id"); // получаем ID курьера

        if (courierId != 0) {
            client.deleteCourier(courierId); // удаляем курьера
        }
    }



    @Step("Check positive authorization response code (200)")
    public void checkStatusCode200(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(200);
    }

    @Step ("Check for courier ID in response")
    public void checkForIdAvailability(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("id", notNullValue());
    }
//
    @Step ("Check negative authorization response code (400 Bad Request)")
    public void checkStatusCode400(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(400);
    }

    @Step ("Check negative authorization response message {\"message\": \"Недостаточно данных для входа\"}")
    public void checkResponseBody400(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Step ("Check negative authorization response code (404 Bad Request)")
    public void checkStatusCode404(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(404);
    }

    @Step ("Check negative authorization response message {\"message\": \"Учетная запись не найдена\"}")
    public void checkResponseBody404(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
    }

}
