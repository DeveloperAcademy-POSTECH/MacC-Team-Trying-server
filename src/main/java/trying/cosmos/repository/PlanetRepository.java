package trying.cosmos.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import trying.cosmos.entity.Planet;

import java.util.Optional;

public interface PlanetRepository extends JpaRepository<Planet, Long> {

    Slice<Planet> findByNameLike(String query, Pageable pageable);

    Optional<Planet> findByInviteCode(String code);
}
