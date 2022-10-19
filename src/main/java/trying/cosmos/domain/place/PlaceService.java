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
        Place place = placeRepository.findByPlaceNumber(request.getPlaceNumber())
                .orElseGet(() -> placeRepository.save(
                        new Place(
                                request.getPlaceNumber(),
                                request.getName(),
                                request.getLatitude(),
                                request.getLongitude()
                        )
                ));
        if (!place.isSame(request.getName(), request.getLatitude(), request.getLongitude())) {
            return placeRepository.save(new Place(request.getPlaceNumber(), request.getName(), request.getLatitude(), request.getLongitude()));
        }
        return place;
    }

    public Place findById(Long placeId) {
        return placeRepository.findById(placeId).orElseThrow();
    }
}
