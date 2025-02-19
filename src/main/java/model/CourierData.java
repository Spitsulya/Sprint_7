package model;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourierData {
    private String login;
    private String password;
    private String firstName;
}
