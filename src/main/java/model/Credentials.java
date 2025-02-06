package model;

public class Credentials {
    private String login;
    private String password;

    public Credentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

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


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
