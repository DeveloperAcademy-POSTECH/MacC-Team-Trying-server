package trying.cosmos.domain.course.dto.request;

import lombok.*;
import trying.cosmos.domain.place.dto.request.PlaceCreateRequest;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CoursePlaceRequest {

    private PlaceCreateRequest place;

    private String memo;
}
