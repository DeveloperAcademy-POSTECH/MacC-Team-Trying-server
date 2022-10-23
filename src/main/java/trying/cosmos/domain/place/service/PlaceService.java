package trying.cosmos.domain.place.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.place.entity.Place;
import trying.cosmos.domain.place.repository.PlaceRepository;
import trying.cosmos.domain.place.dto.request.PlaceCreateRequest;

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
