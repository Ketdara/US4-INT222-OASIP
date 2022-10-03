package sit.int221.us4backend.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Table(name = "user", indexes = {
        @Index(name = "email_UNIQUE", columnList = "email", unique = true),
        @Index(name = "name_UNIQUE", columnList = "name", unique = true)
})
@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Lob
    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "createdOn")
    private Instant createdOn;

    @Column(name = "updatedOn")
    private Instant updatedOn;
}