package sit.int221.us4backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CredentialsDTO {
    @NotBlank(message = "Email is blank; ")
    @Email(message = "Email is not valid; ")
    @Size(max = 50, message = "Email exceeded 50 characters; ")
    private String email;

    @NotBlank(message = "Password is blank; ")
    private String password;
}
