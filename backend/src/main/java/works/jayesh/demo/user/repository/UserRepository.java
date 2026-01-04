package works.jayesh.demo.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import works.jayesh.demo.user.model.entity.User;
import works.jayesh.demo.user.model.entity.UserStatus;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Page<User> findByStatus(UserStatus status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.email LIKE %:keyword% OR " +
            "u.firstName LIKE %:keyword% OR u.lastName LIKE %:keyword%")
    Page<User> searchUsers(String keyword, Pageable pageable);

    long countByStatus(UserStatus status);
}
