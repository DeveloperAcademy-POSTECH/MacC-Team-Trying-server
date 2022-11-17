package trying.cosmos.domain.place.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.place.entity.Place;
import trying.cosmos.domain.place.repository.PlaceRepository;
import trying.cosmos.global.aop.LogSpace;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Transactional
    public Place create(Long identifier, String name, String category, Double longitude, Double latitude) {
        Optional<Place> placeOptional = placeRepository.findByIdentifier(identifier);
        if (placeOptional.isEmpty()) {
            log.info("{}Create new place identifier={}", LogSpace.getSpace(), identifier);
            return placeRepository.save(new Place(identifier, name, category, longitude, latitude));
        } else {
            Place place = placeOptional.get();
            if (!place.isSame(name, category, longitude, latitude)) {
                log.info("{}Create new place identifier={}", LogSpace.getSpace(), identifier);
                placeRepository.save(new Place(identifier, name, category, longitude, latitude));
            }
            return place;
        }
    }

    public Place find(Long placeId) {
        return placeRepository.findById(placeId).orElseThrow();
    }
}
