package sit.int221.us4backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.us4backend.constraints.EnumRole;
import sit.int221.us4backend.enums.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWithValidateDTO {
    private Integer id;

    @NotBlank(message = "User Name cannot be null; ")
    @Size(max = 100, message = "User Name cannot exceed 100 characters; ")
    private String name;

    @NotBlank(message = "User Email cannot be null; ")
    @Email(message = "User Email is not valid; ")
    @Size(max = 100, message = "User Email cannot exceed 100 characters; ")
    private String email;

    @NotBlank(message = "User Role cannot be null; ")
    @EnumRole(enumClass = Role.class)
    private String role;
}
