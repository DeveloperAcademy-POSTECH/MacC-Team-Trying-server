package trying.cosmos.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trying.cosmos.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByName(String name);
}
