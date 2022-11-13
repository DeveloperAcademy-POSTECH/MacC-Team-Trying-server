package trying.cosmos.domain.planet.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanetService {

    private final UserRepository userRepository;
    private final PlanetRepository planetRepository;

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

    @Transactional
    public void join(Long userId, String code) {
        Planet planet = planetRepository.searchByInviteCode(code).orElseThrow();
        planet.join(userRepository.findById(userId).orElseThrow());
    }

    public Planet find(String inviteCode) {
        Planet planet = planetRepository.searchByInviteCode(inviteCode).orElseThrow();
        if (planet.isFull()) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        return planet;
    }

    @Transactional
    public Planet update(Long userId, String name, LocalDate date, String image) {
        User user = userRepository.findById(userId).orElseThrow();
        Planet planet = user.getPlanet();
        if (planet == null) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        planet.update(name, date, image);
        return planet;
    }

    @Transactional
    public void leave(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Planet planet = user.getPlanet();
        if (planet == null) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        planet.leave(user);
    }
}
