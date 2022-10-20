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
    public Place create(PlaceCreateRequest request) {
        return placeRepository.findById(request.getPlaceId())
                .orElseGet(() -> placeRepository.save(
                        new Place(
                                request.getPlaceId(),
                                request.getName(),
                                request.getLatitude(),
                                request.getLongitude()
                        )
                ));
    }

    public Place findById(Long placeId) {
        return placeRepository.findById(placeId).orElseThrow();
    }
}
