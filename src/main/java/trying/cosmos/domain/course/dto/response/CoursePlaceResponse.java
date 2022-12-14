package trying.cosmos.domain.course.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.course.entity.CoursePlace;
import trying.cosmos.domain.place.dto.response.PlaceFindResponse;
import trying.cosmos.global.utils.DistanceUtils;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL )
public class CoursePlaceResponse {

    private PlaceFindResponse place;
    private String memo;
    private Double distanceFromNext;

    public CoursePlaceResponse(CoursePlace coursePlace, CoursePlace nextPlace) {
        this.place = new PlaceFindResponse(coursePlace.getPlace());
        this.memo = coursePlace.getMemo();
        this.distanceFromNext = nextPlace == null ? null : DistanceUtils.getDistance(coursePlace.getCoordinate(), nextPlace.getCoordinate());
    }
}
