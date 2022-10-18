package trying.cosmos.domain.place;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Transactional
    public Place create(Long placeId, String name, double latitude, double longitude) {
        return placeRepository.findById(placeId).orElseGet(() -> placeRepository.save(new Place(placeId, name, latitude, longitude)));
    }

    public Place findById(Long placeId) {
        return placeRepository.findById(placeId).orElseThrow();
    }
}
