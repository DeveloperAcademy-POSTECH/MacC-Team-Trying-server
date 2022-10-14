package trying.cosmos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.entity.Planet;
import trying.cosmos.entity.component.PlanetImageType;
import trying.cosmos.repository.PlanetRepository;
import trying.cosmos.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanetService {

    private final UserRepository userRepository;
    private final PlanetRepository planetRepository;

    @Transactional
    public Planet create(Long userId, String name, PlanetImageType type) {
        return planetRepository.save(new Planet(userRepository.findById(userId).orElseThrow(), name, type));
    }

    public String getInviteCode(Long userId, Long planetId) {
        Planet planet = planetRepository.findById(planetId).orElseThrow();
        return planet.getInviteCode(userRepository.findById(userId).orElseThrow());
    }

    @Transactional
    public void joinPlanet(Long userId, String code) {
        Planet planet = planetRepository.findByInviteCode(code).orElseThrow();
        planet.join(userRepository.findById(userId).orElseThrow());
    }

    public Planet find(Long id) {
        return planetRepository.findById(id).orElseThrow();
    }

    public Slice<Planet> findPlanets(String query, Pageable pageable) {
        return planetRepository.findByNameLike("%" + query + "%", pageable);
    }
}
