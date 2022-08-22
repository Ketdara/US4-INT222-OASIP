package sit.int221.us4backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventTimeframeDTO {
    private Integer id;
    private String eventStartTime;
    private String eventEndTime;
    private Integer eventDuration;
}
