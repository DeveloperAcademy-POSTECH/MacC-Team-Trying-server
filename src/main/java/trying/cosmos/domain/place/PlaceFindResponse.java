package trying.cosmos.domain.place;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceFindResponse {

    private Long placeNumber;
    private String name;
    private Coordinate coordinate;

    public PlaceFindResponse(Place place) {
        this.placeNumber = place.getPlaceId();
        this.name = place.getName();
        this.coordinate = place.getCoordinate();
    }
}
