package sit.int221.us4backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFullDTO {
    private Integer id;
    private String name;
    private String email;
    private String role;
    private Instant createdOn;
    private Instant updatedOn;
}
