package trying.cosmos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trying.cosmos.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
