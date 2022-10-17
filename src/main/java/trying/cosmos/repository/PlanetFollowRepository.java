package trying.cosmos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trying.cosmos.entity.component.PlanetFollow;

import java.util.Optional;

public interface PlanetFollowRepository extends JpaRepository<PlanetFollow, Long> {

    boolean existsByUserIdAndPlanetId(Long userId, Long planetId);

    Optional<PlanetFollow> findByUserIdAndPlanetId(Long userId, Long planetId);
}
