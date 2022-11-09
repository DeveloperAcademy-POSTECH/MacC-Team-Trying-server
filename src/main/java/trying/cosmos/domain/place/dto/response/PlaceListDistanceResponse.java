package trying.cosmos.domain.place.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceListDistanceResponse {

    private List<PlaceDistanceContent> contents;
    private int size;
    private boolean hasNext;

    public PlaceListDistanceResponse(Slice<PlaceDistanceProjection> placeSlice) {
        this.contents = placeSlice.getContent().stream().map(c ->
                new PlaceDistanceContent(
                        c.getPlaceId(),
                        c.getName(),
                        c.getCode(),
                        c.getAddress(),
                        c.getRoadAddress(),
                        c.getLatitude(),
                        c.getLongitude(),
                        c.getDistance()
                )
        ).collect(Collectors.toList());
        this.size = placeSlice.getNumberOfElements();
        this.hasNext = placeSlice.hasNext();
    }
}
