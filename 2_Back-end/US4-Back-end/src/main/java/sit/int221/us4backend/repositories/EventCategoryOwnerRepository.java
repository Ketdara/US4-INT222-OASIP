package sit.int221.us4backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.us4backend.entities.EventCategoryOwner;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface EventCategoryOwnerRepository extends JpaRepository<EventCategoryOwner, Integer> {
    List<EventCategoryOwner> findAllById_UserId(Integer userId);
    List<EventCategoryOwner> findAllById_EventCategoryId(Integer categoryId);
    void deleteAllById_UserId(Integer userId);
}
