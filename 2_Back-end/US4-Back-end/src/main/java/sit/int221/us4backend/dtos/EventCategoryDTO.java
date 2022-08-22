package sit.int221.us4backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.CrossOrigin;
import sit.int221.us4backend.constraints.DurationNotNull;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCategoryDTO {
    private Integer id;

    @NotBlank(message = "Category name cannot be null; ")
    @Size(max = 100, message = "Category name cannot exceed 100 characters; ")
    private String eventCategoryName;

    @Size(max = 500, message = "Category description cannot exceed 500 characters; ")
    private String eventCategoryDescription;

    @DurationNotNull(message = "Duration cannot be null; ")
    @Min(value = 1, message = "Duration cannot be less than 1 minute; ")
    @Max(value = 480, message = "Duration cannot be more than 480 minutes; ")
    private Integer eventDuration;
}
