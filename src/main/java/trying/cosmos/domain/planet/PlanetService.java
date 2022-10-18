package trying.cosmos.domain.planet;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.Course;
import trying.cosmos.domain.course.CourseRepository;
import trying.cosmos.domain.user.User;
import trying.cosmos.domain.user.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanetService {

    private final UserRepository userRepository;
    private final PlanetRepository planetRepository;
    private final PlanetFollowRepository planetFollowRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Planet create(Long userId, String name, PlanetImageType type) {
        return planetRepository.save(new Planet(userRepository.findById(userId).orElseThrow(), name, type));
    }

    public String getInviteCode(Long userId, Long planetId) {
        Planet planet = planetRepository.findById(planetId).orElseThrow();
        planet.authorize(userId);
        return planet.getInviteCode();
    }

    @Transactional
    public void join(Long userId, String code) {
        Planet planet = planetRepository.findByInviteCode(code).orElseThrow();
        planet.join(userRepository.findById(userId).orElseThrow());
    }

    public Planet find(Long planetId) {
        return planetRepository.findById(planetId).orElseThrow();
    }

    public Planet find(String inviteCode) {
        return planetRepository.findByInviteCode(inviteCode).orElseThrow();
    }

    public Slice<Planet> searchPlanets(String query, Pageable pageable) {
        return planetRepository.findByNameLike("%" + query + "%", pageable);
    }

    public Slice<Course> findPlanetCourses(Long userId, Long planetId, Pageable pageable) {
        Planet planet = planetRepository.findById(planetId).orElseThrow();
        // 익명의 사용자
        if (userId == null) {
            return courseRepository.findPublicByPlanet(planet, pageable);
        }

        User user = userRepository.findById(userId).orElseThrow();
        if (planet.beOwnedBy(user)) {
            // 내 행성
            return courseRepository.findAllByPlanet(planet, pageable);
        } else {
            // 다른 사람 행성
            return courseRepository.findPublicByPlanet(planet, pageable);
        }
    }

    @Transactional
    public void follow(Long userId, Long planetId) {
        User user = userRepository.findById(userId).orElseThrow();
        Planet planet = planetRepository.findById(planetId).orElseThrow();
        if (planet.beOwnedBy(user)) {
            throw new CustomException(ExceptionType.PLANET_FOLLOW_FAILED);
        }
        if (isFollow(userId, planetId)) {
            throw new CustomException(ExceptionType.DUPLICATED);
        }
        planetFollowRepository.save(new PlanetFollow(user, planet));
    }

    @Transactional
    public void unfollow(Long userId, Long planetId) {
        if (!isFollow(userId, planetId)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        planetFollowRepository.delete(planetFollowRepository.findByUserIdAndPlanetId(userId, planetId).orElseThrow());
    }

    private boolean isFollow(Long userId, Long planetId) {
        return planetFollowRepository.existsByUserIdAndPlanetId(userId, planetId);
    }
}
