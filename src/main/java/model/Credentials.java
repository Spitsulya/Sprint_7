package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Credentials {
    private String login;
    private String password;

    // корректные данные для авторизации
    public static Credentials fromCourierData(CourierData courierData) {
        return new Credentials(courierData.getLogin(), courierData.getPassword());
    }

    // передан только логин
    public static Credentials fromCourierDataLoginOnly(CourierData courierData) {
        return new Credentials(courierData.getLogin(), null);
    }

    // передан только пароль
    public static Credentials fromCourierDataPasswordOnly(CourierData courierData) {
        return new Credentials(null, courierData.getPassword());
    }

    // передаем несуществующие данные курьера
    public static Credentials invalidData(CourierData courierData) {
        return new Credentials("invalidLogin", "invalidPassword");
    }
}
