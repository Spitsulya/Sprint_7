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

    // передаем неверный логин
    public static Credentials invalidLogin(CourierData courierData) {
        return new Credentials("invalidLogin", courierData.getPassword());
    }

    // передаем неверный пароль
    public static Credentials invalidPassword(CourierData courierData) {
        return new Credentials(courierData.getLogin(), "invalidPassword");
    }
}
