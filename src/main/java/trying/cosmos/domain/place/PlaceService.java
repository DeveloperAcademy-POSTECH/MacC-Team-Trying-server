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
    public Place create(Long id, String name, double latitude, double longitude) {
        return placeRepository.findById(id).orElseGet(() -> placeRepository.save(new Place(id, name, latitude, longitude)));
    }

    public Place findById(Long id) {
        return placeRepository.findById(id).orElseThrow();
    }
}
