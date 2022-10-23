package trying.cosmos.domain.place.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlaceCreateRequest {

    private Long placeId;
    private String name;
    private double latitude;
    private double longitude;
}
