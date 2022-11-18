package trying.cosmos.domain.course.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.entity.CoursePlace;
import trying.cosmos.domain.place.dto.response.PlaceFindContent;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL )
public class LogPlaceResponse {

    private PlaceFindContent place;
    private String memo;

    public LogPlaceResponse(CoursePlace coursePlace) {
        this.place = new PlaceFindContent(coursePlace.getPlace());
        this.memo = coursePlace.getMemo();
    }
}
