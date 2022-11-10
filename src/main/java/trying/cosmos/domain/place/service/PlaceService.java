package trying.cosmos.domain.place.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.place.dto.response.PlaceDistanceProjection;
import trying.cosmos.domain.place.entity.Place;
import trying.cosmos.domain.place.repository.PlaceRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public Slice<PlaceDistanceProjection> findByName(String name, Double latitude, Double longitude, Pageable pageable) {
        return placeRepository.findByNameLike("%" + name + "%", latitude, longitude, pageable);
    }

    public Slice<PlaceDistanceProjection> findByPosition(Double latitude, Double longitude, Double distance, Pageable pageable) {
        return placeRepository.findByPosition(latitude, longitude, distance, pageable);
    }

    public Place find(Long placeId) {
        return placeRepository.findById(placeId).orElseThrow();
    }
}
