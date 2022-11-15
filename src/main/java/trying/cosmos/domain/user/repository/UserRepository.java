package trying.cosmos.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByName(String name);

    Optional<User> findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);

    @Query("select u from User u where u.status = trying.cosmos.domain.user.entity.UserStatus.LOGIN")
    List<User> findLoginUsers();
}
