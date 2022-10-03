package sit.int221.us4backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.us4backend.entities.EventCategoryOwner;

import java.util.List;

public interface EventCategoryOwnerRepository extends JpaRepository<EventCategoryOwner, Integer> {
    List<EventCategoryOwner> findAllById_UserId(Integer userId);
}
