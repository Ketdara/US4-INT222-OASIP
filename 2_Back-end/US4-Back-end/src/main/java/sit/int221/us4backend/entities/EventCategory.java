package sit.int221.us4backend.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "eventCategory")
public class EventCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventCategory_id", nullable = false)
    private Integer id;

    @Column(name = "eventCategoryName", nullable = false, length = 500)
    private String eventCategoryName;

    @Column(name = "eventCategoryDescription", length = 2500)
    private String eventCategoryDescription;

    @Column(name = "eventDuration", nullable = false)
    private Integer eventDuration;
}
