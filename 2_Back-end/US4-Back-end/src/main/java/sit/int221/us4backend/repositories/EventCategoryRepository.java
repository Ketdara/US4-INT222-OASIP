package sit.int221.us4backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.us4backend.entities.EventCategory;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Integer> {
}
