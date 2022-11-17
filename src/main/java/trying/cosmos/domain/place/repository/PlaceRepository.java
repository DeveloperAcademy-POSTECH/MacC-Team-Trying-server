package trying.cosmos.domain.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trying.cosmos.domain.place.entity.Place;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByIdentifier(Long identifier);
}
