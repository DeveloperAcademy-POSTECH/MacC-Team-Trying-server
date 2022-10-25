package trying.cosmos.domain.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.domain.place.entity.Place;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("select p from Place p " +
            "where p.name = :name " +
            "and p.coordinate.latitude = :latitude " +
            "and p.coordinate.longitude = :longitude")
    Optional<Place> find(String name, double latitude, double longitude);
}
