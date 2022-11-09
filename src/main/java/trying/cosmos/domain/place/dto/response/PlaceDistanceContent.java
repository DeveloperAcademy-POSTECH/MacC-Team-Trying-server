package trying.cosmos.domain.place.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.place.entity.Place;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceDistanceContent {

    private PlaceFindContent place;
    private Double distance;

    public PlaceDistanceContent(Long placeId,
                                String name,
                                String code,
                                String address,
                                String roadAddress,
                                Double latitude,
                                Double longitude,
                                Double distance) {
        this.place = new PlaceFindContent(new Place(
                placeId,
                name,
                code,
                address,
                roadAddress,
                latitude,
                longitude)
        );
        this.distance = distance;
    }
}
