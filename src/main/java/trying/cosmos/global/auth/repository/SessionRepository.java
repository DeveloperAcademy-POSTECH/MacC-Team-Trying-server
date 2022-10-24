package trying.cosmos.global.auth.repository;

import org.springframework.data.repository.CrudRepository;
import trying.cosmos.global.auth.entity.Session;

import java.util.Optional;

public interface SessionRepository extends CrudRepository<Session, String> {
    Optional<Session> findByUserId(Long userId);
}
