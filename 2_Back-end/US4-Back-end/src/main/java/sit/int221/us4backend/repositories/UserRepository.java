package sit.int221.us4backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.int221.us4backend.entities.User;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
//    @Query(value = "select e from Event e where FUNCTION('ADDTIME', e.eventStartTime, FUNCTION('SEC_TO_TIME', e.eventDuration * 60)) > ?1")

    @Modifying
    @Query(value = "insert into user (name, email, role, password) values (:name, :email, :role, :password)", nativeQuery = true)
    void createUser(@Param("name") String name,@Param("email") String email,@Param("role") String role,@Param("password") String password);

    @Modifying
    @Query(value = "update User u set u.name = ?1, u.email = ?2, u.role = ?3 where u.id = ?4")
    void updateUser(String name, String email, String role, Integer id);

    User findByEmail(String email);
}
