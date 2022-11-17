package trying.cosmos.domain.course.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.place.dto.request.PlaceCreateRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CoursePlaceRequest {

    private PlaceCreateRequest place;

    private String memo;
}
