package trying.cosmos.domain.course.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.entity.CoursePlace;
import trying.cosmos.domain.place.dto.response.PlaceFindResponse;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL )
public class LogPlaceResponse {

    private PlaceFindResponse place;
    private String memo;

    public LogPlaceResponse(CoursePlace coursePlace) {
        this.place = new PlaceFindResponse(coursePlace.getPlace());
        this.memo = coursePlace.getMemo();
    }
}
