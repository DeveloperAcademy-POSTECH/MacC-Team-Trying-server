package trying.cosmos.domain.planet.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.entity.PlanetFollow;
import trying.cosmos.domain.planet.entity.PlanetImageType;
import trying.cosmos.domain.planet.repository.PlanetFollowRepository;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
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
        User user = userRepository.findById(userId).orElseThrow();
        if (user.getPlanet() != null) {
            throw new CustomException(ExceptionType.PLANET_CREATE_FAILED);
        }
        return planetRepository.save(new Planet(userRepository.findById(userId).orElseThrow(), name, type, generateCode()));
    }

    private String generateCode() {
        String code = RandomStringUtils.random(6, true, true);
        while (planetRepository.existsByInviteCode(code)) {
            code = RandomStringUtils.random(6, true, true);
        }
        return code;
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
        return planetRepository.searchByName("%" + query + "%", pageable);
    }

    public Slice<Planet> findFollowPlanets(Long userId, Pageable pageable) {
        return planetRepository.getFollowPlanets(userId, pageable);
    }

    public Slice<Course> findPlanetCourse(Long userId, Long planetId, Pageable pageable) {
        Planet planet = planetRepository.searchById(planetId).orElseThrow();
        Planet myPlanet = userId == null ? null : userRepository.findById(userId).orElseThrow().getPlanet();
        return courseRepository.searchByPlanet(myPlanet, planet, pageable);
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
    public void leave(Long userId, Long planetId) {
        Planet planet = planetRepository.searchById(planetId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        planet.leave(user);
    }
}
