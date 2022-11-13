package trying.cosmos.domain.place.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.domain.place.dto.response.PlaceDistanceProjection;
import trying.cosmos.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query(value = "select place_id as placeId, name, code, address, road_address as roadAddress, latitude, longitude, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) " +
            "* cos(radians(longitude) - radians(:longitude)) " +
            "+ sin(radians(:latitude)) * sin(radians(latitude)))) as distance " +
            "from Place " +
            "where name like :name " +
            "order by distance", nativeQuery = true)
    Slice<PlaceDistanceProjection> findByNameLike(String name, Double latitude, Double longitude, Pageable pageable);

    // alias를 이용하기 위해서 having에서 데이터 정제
    // sql 표준이 아니기 때문에 DB를 바꿀 때 주의가 필요
    // FixMe: H2 database(test용)에서는 having을 통한 데이터 정제가 불가능하다.
    // FixMe: having절 대신 주석 처리된 구문을 이용하면 테스트 가능하다.
    @Query(value = "select place_id as placeId, name, code, address, road_address as roadAddress, latitude, longitude, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) " +
            "* cos(radians(longitude) - radians(:longitude)) " +
            "+ sin(radians(:latitude)) * sin(radians(latitude)))) as distance " +
            "from Place " +
            "where (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) " +
            "* cos(radians(longitude) - radians(:longitude)) " +
            "+ sin(radians(:latitude)) * sin(radians(latitude)))) < :distance " +
//            "having distance <= :distance " +
            "order by distance", nativeQuery = true)
    Slice<PlaceDistanceProjection> findByPosition(Double latitude, Double longitude, Double distance, Pageable pageable);
}
