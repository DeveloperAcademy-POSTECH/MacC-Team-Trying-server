package trying.cosmos.domain.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trying.cosmos.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
