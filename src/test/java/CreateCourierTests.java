import client.ScooterServiceClient;
import io.restassured.response.ValidatableResponse;
import model.CourierData;
import model.Credentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import static org.hamcrest.Matchers.equalTo;
import static org.apache.http.HttpStatus.*;


public class CreateCourierTests {

    private CourierData courierData;
    private ScooterServiceClient client;

    @Before
    public void setUp() {
        client = new ScooterServiceClient();
    }

    @Test
    @DisplayName("Successful creation of a courier")
    @Description("Positive test for POST request to /api/v1/courier endpoint by filling in all required fields")
    public void CreateCourierSucessfullyTest() {

        courierData = new CourierData("courierelina", "1234", "elina");
        ValidatableResponse response = client.createCourierPostRequest(courierData);
        checkStatusCode201(response);
        checkResponseBody201(response);
    }

    @Test
    @DisplayName("Unsuccessful creation of two identical couriers")
    @Description("Negative test for POST request to /api/v1/courier endpoint by using the same courier's data")
    public void CreateTwoIdenticalCouriersImpossibleTest() {

        courierData = new CourierData("spitsynacourier", "1234", "elina");

        ValidatableResponse response = client.createCourierPostRequest(courierData);;
        checkStatusCode201(response);

        ValidatableResponse response1 = client.createCourierPostRequest(courierData);;
        checkStatusCode409(response1);
        checkResponseBody409(response1);
    }

    @Test
    @DisplayName("Unsuccessful creation without couriers's login")
    @Description("Negative test for POST request to /api/v1/courier endpoint by not using all required fields")
    public void CreateCourierWithoutLoginImpossibleTest() {

        courierData = new CourierData("", "1234", "elina");
        ValidatableResponse response = client.createCourierPostRequest(courierData);;
        checkStatusCode400(response);
        checkResponseBody400(response);
    }

    @Test
    @DisplayName("Unsuccessful creation without couriers's password")
    @Description("Negative test for POST request to /api/v1/courier endpoint by not using all required fields")
    public void CreateCourierWithoutPasswordImpossibleTest() {

        courierData = new CourierData("elinaelina", "", "elina");

        ValidatableResponse response = client.createCourierPostRequest(courierData);;
        checkStatusCode400(response);
        checkResponseBody400(response);
    }


    @After
    public void tearDown() {
        int courierId = 0;

        try {
            courierId = client.authorizeCourier(Credentials.fromCourierData(courierData))
                    .extract().path("id"); // Получаем ID курьера, если запрос успешен
        } catch (Exception e) {
            System.out.println("Authorization failed or courier does not exist. Skipping deletion.");
        }

        // Удаляем курьера, если ID получен
        if (courierId != 0) {
            client.deleteCourier(courierId);
        }
    }


    @Step ("Check positive response code (201 Created)")
    public void checkStatusCode201(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_CREATED);
    }

    @Step ("Check positive response message {ok: true}")
    public void checkResponseBody201(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("ok", equalTo(true));
    }

    @Step ("Check negative response code (409 Сonflict)")
    public void checkStatusCode409(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_CONFLICT);
    }

    @Step ("Check negative response message {\"message\": \"Этот логин уже используется\"}")
    public void checkResponseBody409(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Step ("Check negative response code (400 Bad Request)")
    public void checkStatusCode400(ValidatableResponse response) {
        response.log().all().assertThat()
                .statusCode(SC_BAD_REQUEST);
    }

    @Step ("Check negative response message {\"message\": \"Недостаточно данных для создания учетной записи\"}")
    public void checkResponseBody400(ValidatableResponse response) {
        response.log().all().assertThat()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}
