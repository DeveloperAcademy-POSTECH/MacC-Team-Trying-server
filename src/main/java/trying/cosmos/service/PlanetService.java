package trying.cosmos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.entity.Planet;
import trying.cosmos.entity.User;
import trying.cosmos.entity.component.PlanetType;
import trying.cosmos.repository.PlanetRepository;
import trying.cosmos.repository.UserRepository;

import java.awt.print.Pageable;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanetService {

    private final UserRepository userRepository;
    private final PlanetRepository planetRepository;

    @Transactional
    public Planet create(Long userId, String name, PlanetType type) {
        return planetRepository.save(new Planet(userRepository.findById(userId).orElseThrow(), name, type));
    }

    @Transactional
    public void invite(Long userId, String mateName) {
        Planet planet = planetRepository.findByHostId(userId).orElseThrow();
        planet.invite(userRepository.findByName(mateName).orElseThrow());
    }

    public Planet find(Long id) {
        return planetRepository.findById(id).orElseThrow();
    }

    public List<Planet> findPlanets(String query, Pageable pageable) {
        return planetRepository.findByNameLike("%" + query + "%", pageable);
    }
}
