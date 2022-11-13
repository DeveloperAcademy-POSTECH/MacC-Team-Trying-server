package trying.cosmos.domain.place.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.place.entity.Place;
import trying.cosmos.global.utils.DistanceUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceDistance {

    private Place place;
    private Double distance;

    public PlaceDistance(Place place, Double distance) {
        this.place = place;
        this.distance = DistanceUtils.format(distance);
    }
}
