package sit.int221.us4backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class loginResponse {
    private String name;
    private String email;
    private String role;
    private String jwtToken;
    private String refreshToken;
}
