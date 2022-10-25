package trying.cosmos.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.global.auth.entity.Session;
import trying.cosmos.global.auth.repository.SessionRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public Session create(User user) {
        sessionRepository.findByUserId(user.getId()).ifPresent(sessionRepository::delete);
        return sessionRepository.save(new Session(user));
    }

    public Optional<Session> findById(String id) {
        return sessionRepository.findById(id);
    }

    public Optional<Session> findByUserId(Long id) {
        return sessionRepository.findByUserId(id);
    }

    public void delete(Long userId) {
        sessionRepository.findByUserId(userId).ifPresent(sessionRepository::delete);
    }

    public void clear() {
        sessionRepository.deleteAll();
    }
}
