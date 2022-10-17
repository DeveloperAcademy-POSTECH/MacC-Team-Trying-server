package trying.cosmos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.entity.Place;
import trying.cosmos.repository.PlaceRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Transactional
    public Place create(Long id, String name, double latitude, double longitude) {
        return placeRepository.findById(id).orElseGet(() -> placeRepository.save(new Place(id, name, latitude, longitude)));
    }

    public Place findById(Long id) {
        return placeRepository.findById(id).orElseThrow();
    }
}
