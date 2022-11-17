package trying.cosmos.domain.place.dto.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlaceCreateRequest {

    private Long identifier;
    private String name;
    private String category;
    private double latitude;
    private double longitude;
}
