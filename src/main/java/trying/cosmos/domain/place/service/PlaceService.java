package trying.cosmos.domain.place.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.place.entity.Place;
import trying.cosmos.domain.place.repository.PlaceRepository;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Transactional
    public Place create(Long identifier, String name, String category, String address, Double longitude, Double latitude) {
        Optional<Place> place = placeRepository.find(identifier, name, category, address, longitude, latitude);
        if (place.isEmpty()) {
            return placeRepository.save(new Place(identifier, name, category, address, longitude, latitude));
        }
        return place.get();
    }

    public Place find(Long placeId) {
        return placeRepository.findById(placeId).orElseThrow();
    }
}
