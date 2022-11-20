package trying.cosmos.domain.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.domain.place.entity.Place;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("select p from Place p " +
            "where p.identifier = :identifier " +
            "and p.name = :name " +
            "and p.category = :category " +
            "and p.address = :address " +
            "and p.longitude = :longitude " +
            "and p.latitude = :latitude")
    Optional<Place> find(Long identifier, String name, String category, String address, Double longitude, Double latitude);
}
