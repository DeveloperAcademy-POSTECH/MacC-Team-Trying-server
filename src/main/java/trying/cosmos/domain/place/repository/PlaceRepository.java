package trying.cosmos.domain.place.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.domain.place.dto.response.PlaceDistanceProjection;
import trying.cosmos.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query(value = "select place_id as placeId, name, code, address, road_address as roadAddress, latitude, longitude, ST_Distance_Sphere(POINT(:longitude, :latitude), POINT(longitude, latitude)) as distance " +
            "from Place " +
            "where name like :name " +
            "order by distance", nativeQuery = true)
    Slice<PlaceDistanceProjection> findByNameLike(String name, Double latitude, Double longitude, Pageable pageable);

    @Query(value = "select place_id as placeId, name, code, address, road_address as roadAddress, latitude, longitude, ST_Distance_Sphere(POINT(:longitude, :latitude), POINT(longitude, latitude)) as distance " +
            "from Place " +
            "where ST_Distance_Sphere(POINT(:longitude, :latitude), POINT(longitude, latitude)) <= :distance " +
            "order by distance", nativeQuery = true)
    Slice<PlaceDistanceProjection> findByPosition(Double latitude, Double longitude, Double distance, Pageable pageable);
}
