package sit.int221.us4backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventPartialDTO {
    private Integer id;
    private String bookingName;
    private EventCategoryDTO eventCategory;
    private String eventStartTime;
    private Integer eventDuration;
}
