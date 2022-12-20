package sit.int221.us4backend.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Integer id;

    @Column(name = "bookingName", nullable = false, length = 500)
    private String bookingName;

    @Column(name = "bookingEmail", nullable = false, length = 500)
    private String bookingEmail;

    @ManyToOne(optional = false)
    @JoinColumn(name = "eventCategory_id", nullable = false)
    private EventCategory eventCategory;

    @Column(name = "eventStartTime", nullable = false)
    private String eventStartTime;

    @Column(name = "eventDuration", nullable = false)
    private Integer eventDuration;

    @Column(name = "eventNotes", length = 2500)
    private String eventNotes;

    @Column(name = "fileName", length = 500)
    private String fileName;
}
