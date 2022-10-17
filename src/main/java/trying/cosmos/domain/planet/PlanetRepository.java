package trying.cosmos.domain.planet;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanetRepository extends JpaRepository<Planet, Long> {

    Slice<Planet> findByNameLike(String query, Pageable pageable);

    Optional<Planet> findByInviteCode(String code);
}
