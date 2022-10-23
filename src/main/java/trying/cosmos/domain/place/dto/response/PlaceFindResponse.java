package trying.cosmos.domain.place.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.place.entity.Coordinate;
import trying.cosmos.domain.place.entity.Place;

@ToString
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
