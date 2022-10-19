package trying.cosmos.domain.place;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlaceCreateRequest {

    private Long placeNumber;
    private String name;
    private double latitude;
    private double longitude;
}
