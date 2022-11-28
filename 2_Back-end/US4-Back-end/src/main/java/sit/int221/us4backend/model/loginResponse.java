package sit.int221.us4backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class loginResponse {
    private Integer id;
    private String name;
    private String email;
    private ArrayList role;
    private String jwtToken;
    private String refreshToken;
}
