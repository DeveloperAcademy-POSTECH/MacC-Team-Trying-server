package trying.cosmos.domain.course.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.entity.CoursePlace;
import trying.cosmos.domain.place.dto.response.PlaceFindContent;
import trying.cosmos.global.utils.DistanceUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL )
public class CoursePlaceResponse {

    private PlaceFindContent place;
    private String memo;
    private Double distanceFromNext;

    public CoursePlaceResponse(CoursePlace coursePlace, CoursePlace nextPlace) {
        this.place = new PlaceFindContent(coursePlace.getPlace());
        this.memo = coursePlace.getMemo();
        this.distanceFromNext = nextPlace == null ? null : DistanceUtils.getDistance(coursePlace.getCoordinate(), nextPlace.getCoordinate());
    }
}
