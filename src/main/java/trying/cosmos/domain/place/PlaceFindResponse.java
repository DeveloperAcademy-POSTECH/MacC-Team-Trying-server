package trying.cosmos.domain.place;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceFindResponse {

    private Long placeNumber;
    private String name;
    private double latitude;
    private double longitude;

    public PlaceFindResponse(Place place) {
        this.placeNumber = place.getPlaceId();
        this.name = place.getName();
        this.latitude = place.getLatitude();
        this.longitude = place.getLongitude();
    }
}
