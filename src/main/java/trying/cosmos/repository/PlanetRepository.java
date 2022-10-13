package trying.cosmos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trying.cosmos.entity.Planet;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface PlanetRepository extends JpaRepository<Planet, Long> {

    Optional<Planet> findByHostId(Long userId);

    List<Planet> findByNameLike(String query, Pageable pageable);
}
