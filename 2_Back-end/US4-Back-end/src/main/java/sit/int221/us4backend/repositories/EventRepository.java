package sit.int221.us4backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.int221.us4backend.entities.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllByEventStartTimeBetweenAndEventCategory_Id(String minDate, String maxDate, Integer categoryId);

    Page<Event> findAllByEventCategory_Id(Integer categoryId, Pageable pageable);

    @Query(value = "select e from Event e where FUNCTION('ADDTIME', e.eventStartTime, FUNCTION('SEC_TO_TIME', e.eventDuration * 60)) > ?1")
    Page<Event> findEventUpcomingAll(String minTime, Pageable pageable);

    @Query(value = "select e from Event e where FUNCTION('ADDTIME', e.eventStartTime, FUNCTION('SEC_TO_TIME', e.eventDuration * 60)) <= ?1")
    Page<Event> findEventPastAll(String now, Pageable pageable);

    Page<Event> findAllByEventStartTimeBetween(String minDate, String maxDate, Pageable pageable);
}
