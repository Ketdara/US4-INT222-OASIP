package sit.int221.us4backend.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class EventCategoryOwnerId implements Serializable {
    private static final long serialVersionUID = 1089817981330516632L;
    @Column(name = "eventCategory_id", nullable = false)
    private Integer eventCategoryId;
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Override
    public int hashCode() {
        return Objects.hash(eventCategoryId, userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EventCategoryOwnerId entity = (EventCategoryOwnerId) o;
        return Objects.equals(this.eventCategoryId, entity.eventCategoryId) &&
                Objects.equals(this.userId, entity.userId);
    }
}