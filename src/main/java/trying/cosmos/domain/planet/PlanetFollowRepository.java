package trying.cosmos.domain.planet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlanetFollowRepository extends JpaRepository<PlanetFollow, Long> {

    boolean existsByUserIdAndPlanetId(Long userId, Long planetId);

    @Query("select pf from PlanetFollow pf where pf.user.id = :userId and pf.planet.id = :planetId and pf.planet.isDeleted = false")
    Optional<PlanetFollow> searchByUserAndPlanet(Long userId, Long planetId);
}
