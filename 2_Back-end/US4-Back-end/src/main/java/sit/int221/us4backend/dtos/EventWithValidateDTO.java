package sit.int221.us4backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.us4backend.constraints.PastString;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventWithValidateDTO {
    private Integer id;

    @NotBlank(message = "Booking Name cannot be null; ")
    @Size(max = 100, message = "Booking Name cannot exceed 100 characters; ")
    private String bookingName;

    @NotBlank(message = "Booking Email cannot be null; ")
    @Email(message = "Booking Email is not valid; ")
    @Size(max = 100, message = "Booking Email cannot exceed 100 characters; ")
    private String bookingEmail;

    private EventCategoryDTO eventCategory;

    @NotBlank(message = "Event Start Time cannot be null; ")
    @PastString(message = "Cannot book event in the past; ")
    private String eventStartTime;

    private Integer eventDuration;

    @Size(max = 500, message = "Event Notes cannot exceed 500 characters; ")
    private String eventNotes;

    @Size(max = 500, message = "File Name cannot exceed 500 characters; ")
    private String fileName;

    private byte[] file;
}
