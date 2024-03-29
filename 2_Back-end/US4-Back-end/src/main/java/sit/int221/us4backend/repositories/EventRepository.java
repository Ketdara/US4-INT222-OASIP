package sit.int221.us4backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.int221.us4backend.entities.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    //for default
    List<Event> findAllByEventStartTimeBetweenAndEventCategory_Id(String minDate, String maxDate, Integer categoryId);

    Page<Event> findAllByEventCategory_Id(Integer categoryId, Pageable pageable);

    @Query(value = "select e from Event e where FUNCTION('ADDTIME', e.eventStartTime, FUNCTION('SEC_TO_TIME', e.eventDuration * 60)) > ?1")
    Page<Event> findEventUpcomingAll(String minTime, Pageable pageable);

    @Query(value = "select e from Event e where FUNCTION('ADDTIME', e.eventStartTime, FUNCTION('SEC_TO_TIME', e.eventDuration * 60)) <= ?1")
    Page<Event> findEventPastAll(String now, Pageable pageable);

    Page<Event> findAllByEventStartTimeBetween(String minDate, String maxDate, Pageable pageable);

    //for student
    Page<Event> findAllByBookingEmail(String email, Pageable pageable);

    Page<Event> findAllByEventCategory_IdAndBookingEmail(Integer categoryId, String email, Pageable pageable);

    @Query(value = "select e from Event e where FUNCTION('ADDTIME', e.eventStartTime, FUNCTION('SEC_TO_TIME', e.eventDuration * 60)) > ?1 and e.bookingEmail = ?2")
    Page<Event> findEventUpcomingAllAndEmail(String minTime, String email, Pageable pageable);

    @Query(value = "select e from Event e where FUNCTION('ADDTIME', e.eventStartTime, FUNCTION('SEC_TO_TIME', e.eventDuration * 60)) <= ?1 and e.bookingEmail = ?2")
    Page<Event> findEventPastAllAndEmail(String now, String email, Pageable pageable);

    Page<Event> findAllByEventStartTimeBetweenAndBookingEmail(String minDate, String maxDate, String email, Pageable pageable);

    //for lecturer
    @Query(value = "select e from Event e where e.eventCategory.id in (select o.id.eventCategoryId from EventCategoryOwner o where o.id.userId = (select u.id from User u where u.email = ?1)) order by e.eventStartTime desc")
    Page<Event> findAllByLecturerEmail(String email, Pageable pageable);

    @Query(value = "select e from Event e where e.eventCategory.id in (select o.id.eventCategoryId from EventCategoryOwner o where o.id.userId = (select u.id from User u where u.email = ?1)) and e.eventCategory.id = ?2 order by e.eventStartTime desc")
    Page<Event> findAllByEventCategory_IdAndLecturerEmail(String email, Integer categoryId, Pageable pageable);

    @Query(value = "select e from Event e where e.eventCategory.id in (select o.id.eventCategoryId from EventCategoryOwner o where o.id.userId = (select u.id from User u where u.email = ?2)) and FUNCTION('ADDTIME', e.eventStartTime, FUNCTION('SEC_TO_TIME', e.eventDuration * 60)) > ?1 order by e.eventStartTime desc")
    Page<Event> findEventUpcomingAllAndLecturerEmail(String minTime, String email, Pageable pageable);

    @Query(value = "select e from Event e where e.eventCategory.id in (select o.id.eventCategoryId from EventCategoryOwner o where o.id.userId = (select u.id from User u where u.email = ?2)) and FUNCTION('ADDTIME', e.eventStartTime, FUNCTION('SEC_TO_TIME', e.eventDuration * 60)) <= ?1 order by e.eventStartTime desc")
    Page<Event> findEventPastAllAndLecturerEmail(String now, String email, Pageable pageable);

    @Query(value = "select e from Event e where e.eventCategory.id in (select o.id.eventCategoryId from EventCategoryOwner o where o.id.userId = (select u.id from User u where u.email = ?3)) and e.eventStartTime between ?1 and ?2 order by e.eventStartTime desc")
    Page<Event> findAllByEventStartTimeBetweenAndLecturerEmail(String minDate, String maxDate, String email, Pageable pageable);
}
