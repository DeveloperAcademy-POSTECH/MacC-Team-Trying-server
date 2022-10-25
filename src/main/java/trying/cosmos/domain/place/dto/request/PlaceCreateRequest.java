package trying.cosmos.domain.place.dto.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlaceCreateRequest {

    private String name;
    private double latitude;
    private double longitude;
}
