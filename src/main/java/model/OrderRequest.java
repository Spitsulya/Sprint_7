package model;
import lombok.*;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private String firstName;
    private String lastName;
    private String address;
    private int metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private String comment;
    private List<String> color;
}