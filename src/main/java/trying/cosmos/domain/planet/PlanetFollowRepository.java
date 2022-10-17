package trying.cosmos.domain.planet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanetFollowRepository extends JpaRepository<PlanetFollow, Long> {

    boolean existsByUserIdAndPlanetId(Long userId, Long planetId);

    Optional<PlanetFollow> findByUserIdAndPlanetId(Long userId, Long planetId);
}
