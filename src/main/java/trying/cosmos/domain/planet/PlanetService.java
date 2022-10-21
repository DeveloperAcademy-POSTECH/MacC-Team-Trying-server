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
        Planet planet = planetRepository.searchById(planetId).orElseThrow();
        return planet.getInviteCode(userId);
    }

    @Transactional
    public void join(Long userId, String code) {
        Planet planet = planetRepository.searchByInviteCode(code).orElseThrow();
        planet.join(userRepository.findById(userId).orElseThrow());
    }

    public Planet find(Long planetId) {
        return planetRepository.searchById(planetId).orElseThrow();
    }

    public Planet find(String inviteCode) {
        Planet planet = planetRepository.searchByInviteCode(inviteCode).orElseThrow();
        if (planet.isFull()) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        return planet;
    }

    public Slice<Planet> findList(String query, Pageable pageable) {
        return planetRepository.searchByNameLike("%" + query + "%", pageable);
    }

    public Slice<Course> findPlanetCourse(Long userId, Long planetId, Pageable pageable) {
        Planet planet = planetRepository.searchById(planetId).orElseThrow();
        // 익명의 사용자
        if (userId == null) {
            return courseRepository.searchPublicByPlanet(planet, pageable);
        }

        User user = userRepository.findById(userId).orElseThrow();
        if (planet.isOwnedBy(user)) {
            // 내 행성
            return courseRepository.searchAllByPlanet(planet, pageable);
        } else {
            // 다른 사람 행성
            return courseRepository.searchPublicByPlanet(planet, pageable);
        }
    }

    @Transactional
    public void follow(Long userId, Long planetId) {
        User user = userRepository.findById(userId).orElseThrow();
        Planet planet = planetRepository.searchById(planetId).orElseThrow();
        if (planet.isOwnedBy(user) || isFollow(userId, planetId)) {
            throw new CustomException(ExceptionType.PLANET_FOLLOW_FAILED);
        }
        planetFollowRepository.save(new PlanetFollow(user, planet));
    }

    @Transactional
    public void unfollow(Long userId, Long planetId) {
        if (!isFollow(userId, planetId)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        planetFollowRepository.delete(planetFollowRepository.searchByUserAndPlanet(userId, planetId).orElseThrow());
    }

    private boolean isFollow(Long userId, Long planetId) {
        return planetFollowRepository.existsByUserIdAndPlanetId(userId, planetId);
    }

    @Transactional
    public void update(Long userId, Long planetId, String name, int dday) {
        Planet planet = planetRepository.searchById(planetId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!planet.isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        planet.update(name, dday);
    }

    @Transactional
    public void delete(Long userId, Long planetId) {
        Planet planet = planetRepository.searchById(planetId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!planet.isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        planet.delete();
    }
}
