package sit.int221.us4backend.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Table(name = "eventCategoryOwner", indexes = {
        @Index(name = "fk_eventCategory_has_user_eventCategory1_idx", columnList = "eventCategory_id"),
        @Index(name = "fk_eventCategory_has_user_user1_idx", columnList = "user_id")
})
@Getter
@Setter
@Entity
public class EventCategoryOwner {
    @EmbeddedId
    private EventCategoryOwnerId id;
}