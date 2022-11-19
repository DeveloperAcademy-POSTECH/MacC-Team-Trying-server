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

    private Long placeId;
    private Long identifier;
    private String name;
    private String category;
    private String address;
    private Coordinate coordinate;

    public PlaceFindResponse(Place place) {
        this.placeId = place.getId();
        this.identifier = place.getIdentifier();
        this.name = place.getName();
        this.category = place.getCategory();
        this.address = place.getAddress();
        this.coordinate = new Coordinate(place.getLongitude(), place.getLatitude());
    }
}
