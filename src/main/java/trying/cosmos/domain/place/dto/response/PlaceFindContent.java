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
public class PlaceFindContent {

    private Long placeId;
    private String name;
    private Coordinate coordinate;

    public PlaceFindContent(Place place) {
        this.placeId = place.getId();
        this.name = place.getName();
        this.coordinate = new Coordinate(place.getLatitude(), place.getLongitude());
    }
}
