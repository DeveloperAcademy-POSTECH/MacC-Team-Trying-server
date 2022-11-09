package trying.cosmos.domain.planet.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.planet.dto.response.PlanetFindResponse;
import trying.cosmos.domain.planet.dto.response.PlanetListFindContent;
import trying.cosmos.domain.planet.dto.response.PlanetListFindResponse;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanetService {

    private final UserRepository userRepository;
    private final PlanetRepository planetRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Planet create(Long userId, String name, String type) {
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

    public PlanetFindResponse find(Long userId, Long planetId) {
        if (userId == null) {
            return new PlanetFindResponse(planetRepository.searchById(planetId).orElseThrow());
        } else {
            User user = userRepository.findById(userId).orElseThrow();
            Planet planet = planetRepository.searchById(planetId).orElseThrow();
            return new PlanetFindResponse(planetRepository.searchById(planetId).orElseThrow());
        }
    }

    public Planet find(String inviteCode) {
        Planet planet = planetRepository.searchByInviteCode(inviteCode).orElseThrow();
        if (planet.isFull()) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        return planet;
    }

    public PlanetListFindResponse findList(Long userId, String query, Pageable pageable) {
        Slice<Planet> planetSlice = planetRepository.searchByName("%" + query + "%", pageable);
        return getPlanetList(userId, planetSlice);
    }

    private PlanetListFindResponse getPlanetList(Long userId, Slice<Planet> planetSlice) {
        if (userId == null) {
            List<PlanetListFindContent> contents = planetSlice.getContent().stream()
                    .map(PlanetListFindContent::new)
                    .collect(Collectors.toList());
            return new PlanetListFindResponse(new SliceImpl<>(contents, planetSlice.getPageable(), planetSlice.hasNext()));
        } else {
            User user = userRepository.findById(userId).orElseThrow();
            List<PlanetListFindContent> contents = planetSlice.getContent().stream()
                    .map(PlanetListFindContent::new)
                    .collect(Collectors.toList());
            return new PlanetListFindResponse(new SliceImpl<>(contents, planetSlice.getPageable(), planetSlice.hasNext()));
        }
    }

    public Slice<Course> findPlanetCourse(Long userId, Long planetId, Pageable pageable) {
        Planet planet = planetRepository.searchById(planetId).orElseThrow();
        Planet myPlanet = userId == null ? null : userRepository.findById(userId).orElseThrow().getPlanet();
        return courseRepository.searchByPlanet(myPlanet, planet, pageable);
    }

    @Transactional
    public Planet update(Long userId, Long planetId, String name, LocalDate date, String image) {
        Planet planet = planetRepository.searchById(planetId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!planet.isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }
        planet.update(name, date, image);
        return planet;
    }

    @Transactional
    public void leave(Long userId, Long planetId) {
        Planet planet = planetRepository.searchById(planetId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        planet.leave(user);
    }
}
